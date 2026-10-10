import java.util.*;

public class Main {
    public static class BinarySearchTree<K extends Comparable<K>, V> {
        private static class Node<K, V> {
            K key;
            V value;
            Node<K, V> left;
            Node<K, V> right;

            Node(K key, V value) {
                this.key = key;
                this.value = value;
            }
        }

        private Node<K, V> root;
        private int size = 0;

        public int size() {
            return size;
        }

        public void insert(K key, V value) {
            root = insertRec(root, key, value);
        }

        private Node<K, V> insertRec(Node<K, V> current, K key, V value) {
            if (current == null) {
                size++;
                return new Node<>(key, value);
            }
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current.left = insertRec(current.left, key, value);
            } else if (cmp > 0) {
                current.right = insertRec(current.right, key, value);
            } else {
                current.value = value;
            }
            return current;
        }

        public V search(K key) {
            Node<K, V> node = searchRec(root, key);
            return node == null ? null : node.value;
        }

        private Node<K, V> searchRec(Node<K, V> current, K key) {
            if (current == null) return null;
            int cmp = key.compareTo(current.key);
            if (cmp < 0) return searchRec(current.left, key);
            if (cmp > 0) return searchRec(current.right, key);
            return current;
        }

        public boolean delete(K key) {
            int prevSize = size;
            root = deleteRec(root, key);
            return size < prevSize;
        }

        private Node<K, V> deleteRec(Node<K, V> current, K key) {
            if (current == null) return null;

            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current.left = deleteRec(current.left, key);
            } else if (cmp > 0) {
                current.right = deleteRec(current.right, key);
            } else {
                size--;
                if (current.left == null) return current.right;
                if (current.right == null) return current.left;

                Node<K, V> successor = findMin(current.right);
                current.key = successor.key;
                current.value = successor.value;
                size++;
                current.right = deleteRec(current.right, successor.key);
            }
            return current;
        }

        private Node<K, V> findMin(Node<K, V> node) {
            while (node.left != null) node = node.left;
            return node;
        }
    }

    public static class HashTable<K, V> {
        private static class HashNode<K, V> {
            K key;
            V value;
            HashNode<K, V> next;

            HashNode(K key, V value, HashNode<K, V> next) {
                this.key = key;
                this.value = value;
                this.next = next;
            }
        }

        private HashNode<K, V>[] table;
        private int capacity;
        private int size = 0;
        private final double loadFactor;

        @SuppressWarnings("unchecked")
        public HashTable(int initialCapacity, double loadFactor) {
            this.capacity = initialCapacity;
            this.loadFactor = loadFactor;
            this.table = (HashNode<K, V>[]) new HashNode[capacity];
        }

        public HashTable() {
            this(16, 0.75);
        }

        private int hash(K key) {
            return (key.hashCode() & 0x7fffffff) % capacity;
        }

        public int size() {
            return size;
        }

        public void insert(K key, V value) {
            if ((double) (size + 1) / capacity > loadFactor) {
                resize();
            }

            int idx = hash(key);
            HashNode<K, V> current = table[idx];

            while (current != null) {
                if (current.key.equals(key)) {
                    current.value = value;
                    return;
                }
                current = current.next;
            }

            table[idx] = new HashNode<>(key, value, table[idx]);
            size++;
        }

        public V search(K key) {
            int idx = hash(key);
            HashNode<K, V> current = table[idx];
            while (current != null) {
                if (current.key.equals(key)) return current.value;
                current = current.next;
            }
            return null;
        }

        public boolean delete(K key) {
            int idx = hash(key);
            HashNode<K, V> current = table[idx];
            HashNode<K, V> prev = null;

            while (current != null) {
                if (current.key.equals(key)) {
                    if (prev == null) {
                        table[idx] = current.next;
                    } else {
                        prev.next = current.next;
                    }
                    size--;
                    return true;
                }
                prev = current;
                current = current.next;
            }
            return false;
        }

        @SuppressWarnings("unchecked")
        private void resize() {
            int newCapacity = capacity * 2;
            HashNode<K, V>[] newTable = (HashNode<K, V>[]) new HashNode[newCapacity];
            int oldCapacity = capacity;
            capacity = newCapacity;

            for (int i = 0; i < oldCapacity; i++) {
                HashNode<K, V> head = table[i];
                while (head != null) {
                    HashNode<K, V> next = head.next;
                    int newIdx = hash(head.key);
                    head.next = newTable[newIdx];
                    newTable[newIdx] = head;
                    head = next;
                }
            }
            this.table = newTable;
        }
    }

    public static void main(String[] args) {
        int[] sizes = {100, 1000, 10000};
        Random rng = new Random(42);

        System.out.println("Warming up JIT compiler...");
        runBenchmarkSuite(5000, rng, true);

        System.out.println("\n==========================================================================");
        System.out.println(String.format("%-10s | %-12s | %-16s | %-16s | %-16s",
                "Size (N)", "Structure", "Insert (µs)", "Search (µs)", "Delete (µs)"));
        System.out.println("==========================================================================");

        for (int n : sizes) {
            runBenchmarkSuite(n, rng, false);
            System.out.println("--------------------------------------------------------------------------");
        }
    }

    private static void runBenchmarkSuite(int n, Random rng, boolean isWarmup) {
        List<Integer> keys = new ArrayList<>(n);
        for (int i = 0; i < n; i++) keys.add(rng.nextInt());
        Collections.shuffle(keys, rng);

        List<Integer> searchKeys = new ArrayList<>(keys);
        Collections.shuffle(searchKeys, rng);

        BinarySearchTree<Integer, Integer> bst = new BinarySearchTree<>();
        long bstInsStart = System.nanoTime();
        for (int k : keys) bst.insert(k, k);
        long bstInsTime = (System.nanoTime() - bstInsStart) / 1000;

        long bstSearchStart = System.nanoTime();
        for (int k : searchKeys) bst.search(k);
        long bstSearchTime = (System.nanoTime() - bstSearchStart) / 1000;

        long bstDelStart = System.nanoTime();
        for (int k : keys) bst.delete(k);
        long bstDelTime = (System.nanoTime() - bstDelStart) / 1000;

        HashTable<Integer, Integer> ht = new HashTable<>();
        long htInsStart = System.nanoTime();
        for (int k : keys) ht.insert(k, k);
        long htInsTime = (System.nanoTime() - htInsStart) / 1000;

        long htSearchStart = System.nanoTime();
        for (int k : searchKeys) ht.search(k);
        long htSearchTime = (System.nanoTime() - htSearchStart) / 1000;

        long htDelStart = System.nanoTime();
        for (int k : keys) ht.delete(k);
        long htDelTime = (System.nanoTime() - htDelStart) / 1000;

        if (!isWarmup) {
            System.out.println(String.format("%-10d | %-12s | %-16d | %-16d | %-16d",
                    n, "BST", bstInsTime, bstSearchTime, bstDelTime));
            System.out.println(String.format("%-10d | %-12s | %-16d | %-16d | %-16d",
                    n, "Hash Table", htInsTime, htSearchTime, htDelTime));
        }
    }
}