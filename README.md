# Bonus Task: Performance Comparison: Binary Search Tree vs. Hash Table

## Overview & Implementation

For this experiment, I implemented an unbalanced Binary Search Tree (BST) and a Hash Table using separate chaining (linked list buckets with an initial capacity of 16 and a 0.75 load factor) in Java. I tested insertion, lookup, and deletion across three dataset sizes: 100, 1,000, and 10,000 random integer keys.

## Theoretical Complexity

Theoretically, the two structures have different asymptotic guarantees:

* **Hash Table:** Provides average-case `O(1)` time for insertion, search, and deletion. In the worst case, where multiple keys hash to the exact same bucket or table resizing stalls execution, operations degrade to `O(n)`.
* **BST:** Provides average-case `O(log n)` time when keys arrive in random order. However, if data is inserted in sorted or reverse-sorted order, the tree degenerates into a linear linked list with worst-case `O(n)` height.

## Experimental Observations

In practice, the Hash Table consistently outperformed the BST across all three operations by a factor of roughly **X** to **Y**.

1. As the input scaled from 1,000 to 10,000 elements, the Hash Table's total execution time grew nearly linearly with `N`, reflecting steady `O(1)` per-operation performance.
2. The BST's runtimes grew more noticeably because each lookup had to traverse an average tree depth of around **A** to **B** levels at `N = 10,000`.
3. Deletion in the BST was consistently slower than search due to the overhead of locating and replacing two-child nodes with their in-order successors.

## Why Performance Differs in Practice

The speed gap comes down to hardware and memory access:

* **Memory & Caching:** The Hash Table jumps directly to an array bucket in contiguous memory. The BST requires chasing reference pointers across the heap, causing frequent CPU cache misses.
* **Constant Factors:** A hash table computes `hashCode()` once and does very few equality checks. The BST evaluates a comparison at every single node depth.

## Conclusion & Trade-offs

While the Hash Table is faster for raw key-value lookups, it destroys key ordering. The BST remains necessary whenever sorted order, range queries, or min/max lookups are required.
