# LRU Cache from Scratch — Java Interview Guide

An LRU Cache (Least Recently Used Cache) is a cache that removes the item that has not been used for the longest time when the cache reaches its capacity.

This is a very common Java, Data Structures, and System Design interview question, especially for mid-level and senior backend developer roles.

We'll build it from scratch without using Java's built-in `LinkedHashMap`.

## 1. Understand the problem

Suppose the cache capacity is `3`.

We perform these operations:

Cache state after each operation — most recently used item on the left.

Operation 1: `put(1, 100)`

## 1 : 100

MRU

Empty

Empty

Operation 2: `put(2, 200)`

## 2 : 200

MRU

## 1 : 100

Empty

Operation 3: `put(3, 300)`

## 3 : 300

MRU

## 2 : 200

## 1 : 100

LRU

Operation 4: `get(1)`

Key 1 is accessed, so it becomes the most recently used.

## 1 : 100

MRU

## 3 : 300

## 2 : 200

LRU

Operation 5: `put(4, 400)`

The cache is full. Key 2 is the least recently used, so it is evicted.

## 4 : 400

MRU

## 1 : 100

## 3 : 300

LRU

Two rules are important:

- Every successful `get()` makes that key the most recently used.
- Every `put()` makes the inserted or updated key the most recently used.

## 2. Why can't we use only a HashMap?

A `HashMap` can find a key in average \\(O(1)\\) time, but it doesn't maintain the order in which keys are accessed.

A normal doubly linked list maintains the order, but finding an arbitrary key in it takes \\(O(n)\\) time.

We need both.

## 3. Data structures required

We combine a `HashMap` and a Doubly Linked List.

1\. HashMap

Stores `key → Node` mappings, allowing us to find a cache entry in average \\(O(1)\\) time.

Example: `1 → Node(1, 100)`

2\. Doubly Linked List

Maintains usage order. Each node has a `prev` pointer and a `next` pointer, so we can remove or move any known node in \\(O(1)\\) time.

MRU ←→ Recently Used ←→ LRU

We'll use two dummy nodes, `head` and `tail`, to simplify insertion and removal.

- `head.next` points to the most recently used node.
- `tail.prev` points to the least recently used node.
- The dummy nodes are not actual cache entries.

## 4. Implement LRU Cache from scratch in Java

This implementation uses no built-in LRU cache or `LinkedHashMap`.

```

import java.util.HashMap;
import java.util.Map;

public class LRUCache {

    // Represents one entry in the cache
    private static class Node {
        int key;
        int value;
        Node prev;
        Node next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> cache;

    private final Node head;
    private final Node tail;

    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                "Capacity must be positive"
            );
        }

        this.capacity = capacity;
        this.cache = new HashMap<>();

        // Dummy nodes
        head = new Node(-1, -1);
        tail = new Node(-1, -1);

        head.next = tail;
        tail.prev = head;
    }

    // Remove a node from its current position
    private void removeNode(Node node) {
        Node previous = node.prev;
        Node next = node.next;

        previous.next = next;
        next.prev = previous;
    }

    // Insert a node immediately after head (MRU position)
    private void addToFront(Node node) {
        Node first = head.next;

        node.prev = head;
        node.next = first;

        head.next = node;
        first.prev = node;
    }

    // Move an existing node to the MRU position
    private void moveToFront(Node node) {
        removeNode(node);
        addToFront(node);
    }

    // Remove and return the LRU node
    private Node removeLRU() {
        Node lru = tail.prev;
        removeNode(lru);
        return lru;
    }

    // Retrieve a value
    public int get(int key) {
        Node node = cache.get(key);

        if (node == null) {
            return -1;
        }

        moveToFront(node);
        return node.value;
    }

    // Insert or update a key-value pair
    public void put(int key, int value) {
        Node existing = cache.get(key);

        // Update an existing key
        if (existing != null) {
            existing.value = value;
            moveToFront(existing);
            return;
        }

        // Evict the LRU entry if the cache is full
        if (cache.size() == capacity) {
            Node lru = removeLRU();
            cache.remove(lru.key);
        }

        // Add the new entry as MRU
        Node newNode = new Node(key, value);
        addToFront(newNode);
        cache.put(key, newNode);
    }

    // Display cache from MRU to LRU
    public void display() {
        Node current = head.next;

        while (current != tail) {
            System.out.print(
                current.key + "=" + current.value + " "
            );
            current = current.next;
        }

        System.out.println();
    }

    public static void main(String[] args) {
        LRUCache lru = new LRUCache(3);

        lru.put(1, 100);
        lru.put(2, 200);
        lru.put(3, 300);

        lru.display();
        // 3=300 2=200 1=100

        System.out.println(lru.get(1));
        // 100

        lru.display();
        // 1=100 3=300 2=200

        lru.put(4, 400);
        lru.display();
        // 4=400 1=100 3=300

        System.out.println(lru.get(2));
        // -1 (key 2 was evicted)
    }
}

```

## 5. Understand each method internally

### A. `addToFront(node)`

When a new key is inserted or an existing key is accessed, it moves to the front.

Before:

HEAD

Node A

Node B

TAIL

Node A is currently the most recently used.

After inserting Node C:

HEAD

Node C

Node A

Node B

TAIL

Node C becomes MRU; Node B remains LRU.

The critical pointer assignments are:

```
node.prev = head;
node.next = first;
head.next = node;
first.prev = node;
```

These connect the new node to both neighbors. All four assignments take constant time.

### B. `removeNode(node)`

To remove a node from anywhere in the list:

```
Node previous = node.prev;
Node next = node.next;

previous.next = next;
next.prev = previous;
```

The previous node skips over the removed node, and the next node points back to the previous node.

Because we already have a reference to the node, we don't need to search the list.

### C. `get(key)`

The flow is:

1. Search the HashMap for the node.
2. If the key doesn't exist, return `-1`.
3. Otherwise, move the node to the front.
4. Return its value.

### D. `put(key, value)`

The flow is:

1. If the key exists, update its value and move it to the front.
2. Otherwise, if the cache is full, remove the last node before `tail` and remove its key from the HashMap.
3. Create a new node and insert it at the front.
4. Add the key-to-node mapping to the HashMap.

Important: Eviction must remove the entry from both data structures. If you remove it only from the list or only from the map, they become inconsistent.

## 6. Interactive example

Try the operations yourself to see how the cache changes.

### LRU simulator

Step 5 / 5

Capacity

−

## 3

\+

MRU (left) → LRU (right)

## 4

400

MRU

## 1

100

## 3

300

LRU

put(4, 400)

Entry inserted or updated.

Evicted key: 2

PreviousNextReset

#### Add an operation

Put

Get

Key

Value

Apply operation

## 7. Time and space complexity

| Operation          | Average complexity |
| ------------------ | ------------------ |
| `get(key)`         | \\(O(1)\\)         |
| `put(key, value)`  | \\(O(1)\\)         |
| Remove LRU entry   | \\(O(1)\\)         |
| Move node to front | \\(O(1)\\)         |
| Total space        | \\(O(C)\\)         |

Here, \\(C\\) is the cache capacity. HashMap operations are average \\(O(1)\\); this is the standard expected-complexity analysis.

## 8. Common interview follow-up questions

Q1. Why use a doubly linked list instead of a singly linked list?

A doubly linked list allows us to remove any known node in \\(O(1)\\) time because it provides both `prev` and `next` references. With a singly linked list, we generally need to find the previous node first.

Q2. Why do we need both a HashMap and a linked list?

The HashMap provides fast lookup, while the linked list maintains recency order and supports fast eviction.

Q3. What happens when we call `put()` for an existing key?

Update the value and move that node to the MRU position. Do not increase the cache size or evict another entry.

Q4. Is this implementation thread-safe?

No. Concurrent calls to `get()` and `put()` can interfere with map and linked-list updates. A production implementation needs synchronization or a carefully designed concurrent strategy.

Q5. Can we implement LRU using `LinkedHashMap`?

Yes. Java's access-ordered `LinkedHashMap` can implement an LRU cache more concisely, but building it with a HashMap and doubly linked list demonstrates the underlying data structures.

Q6. What if the cache capacity is zero?

This implementation rejects zero or negative capacity. You could instead define a zero-capacity cache as one that never stores entries, but that behavior must be specified.

### What to remember for interviews

The core design is:

- HashMap: key → node.
- Doubly linked list: MRU at the front, LRU at the back.
- Get: look up, then move to front.
- Put: update or insert at front; evict from the back when full.
- Performance: average \\(O(1)\\) for both `get` and `put`.

Once you can write `removeNode()`, `addToFront()`, `get()`, and `put()` from memory and explain why each pointer changes, you understand the standard LRU Cache problem.