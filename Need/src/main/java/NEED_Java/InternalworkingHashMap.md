Sure. **`HashMap` internal working is one of the most important Java Collections questions for a Senior Java Developer.** I'll explain it from `put()` → hash calculation → bucket selection → collision → `get()` → resizing → Java 8+ treeification.

## 1. What is HashMap internally?

Conceptually, a `HashMap` stores data in an array called a **bucket table**.

```text
HashMap
   |
   v
+-----+-----+-----+-----+-----+-----+
|  0  |  1  |  2  |  3  |  4  | ... |
+-----+-----+-----+-----+-----+-----+
        |
        v
     Node / Entry
```

In modern Java, the table contains `Node<K,V>` objects.

A node conceptually contains:

```java
static class Node<K,V> {
    int hash;
    K key;
    V value;
    Node<K,V> next;
}
```

So each entry has:

```text
hash
key
value
next
```

The `next` reference is used when multiple entries land in the same bucket.

---

# 2. What happens when we call `put()`?

Suppose:

```java
Map<Integer, String> map = new HashMap<>();

map.put(101, "John");
```

Internally, the process is approximately:

```text
put(101, "John")
       |
       v
Calculate hash
       |
       v
Calculate bucket index
       |
       v
Check bucket
       |
       +---- Empty ---> Insert
       |
       +---- Not empty
                    |
                    v
               Compare keys
                    |
             +------+------+
             |             |
          Equal         Not Equal
             |             |
          Replace       Collision
                         |
                         v
                   Add new node
```

Let's break this down.

---

# 3. Step 1 — Calculate hash

When you do:

```java
map.put(101, "John");
```

HashMap gets the key's hash code:

```java
key.hashCode()
```

For an `Integer`:

```java
Integer key = 101;

key.hashCode()
```

returns:

```text
101
```

But HashMap doesn't simply use this value directly.

Java's implementation performs additional hash spreading.

Conceptually, modern JDK implementations use something similar to:

```java
int h = key.hashCode();

int hash = h ^ (h >>> 16);
```

The purpose is to spread bits from the hash code so that bucket distribution is better.

---

# 4. Step 2 — Calculate bucket index

Now HashMap needs to determine **which bucket** should contain the entry.

Conceptually:

```java
index = (n - 1) & hash;
```

where:

```text
n = table length
```

For example, suppose:

```text
table length = 16
hash = 101
```

Then:

```text
index = (16 - 1) & 101
      = 15 & 101
      = 5
```

So:

```text
101 → bucket 5
```

---

# 5. Why does HashMap use `&` instead of `%`?

This is a very common interview question.

Instead of:

```java
hash % 16
```

HashMap can use:

```java
hash & (16 - 1)
```

because HashMap's capacity is maintained as a **power of two**.

For example:

```text
16
32
64
128
256
...
```

This makes bucket-index calculation efficient.

### Interview answer

> HashMap uses power-of-two table sizes so that bucket indexing can efficiently use bitwise AND instead of modulo, while also making the lower bits of the hash useful for distribution.

---

# 6. What if the bucket is empty?

Suppose:

```text
101 → bucket 5
```

and bucket 5 is empty:

```text
Bucket 5

null
```

HashMap creates a node:

```text
+-------+
| 101   |
| John  |
+-------+
```

and stores it in bucket 5.

---

# 7. What happens if another key goes to the same bucket?

This is called a **collision**.

Suppose:

```text
Key A → bucket 5
Key B → bucket 5
```

Even though:

```java
keyA.hashCode() != keyB.hashCode()
```

they can still map to the same bucket.

Conceptually:

```text
Bucket 5
   |
   v
+---------+      +---------+
| Key A   | ---> | Key B   |
| Value A |      | Value B |
+---------+      +---------+
```

Historically this was primarily a linked list.

In modern Java, heavily-collided buckets can be converted into a **red-black tree**.

---

# 8. Very important: Same hash does NOT mean same key

Suppose:

```java
key1.hashCode() == key2.hashCode()
```

This does **not** mean:

```java
key1.equals(key2)
```

is true.

Hash collisions are completely valid.

HashMap ultimately uses `equals()` to determine whether two keys are logically the same.

---

# 9. How does HashMap handle collision?

Suppose:

```java
map.put(key1, "A");
map.put(key2, "B");
```

Both go to bucket 5.

HashMap checks the existing node.

Conceptually:

```java
if (existingHash == newHash
        && existingKey.equals(newKey)) {

    // Replace value
}
```

If the keys are equal:

```text
same key → replace value
```

If they're different:

```text
different key → collision → add another node
```

---

# 10. Example

Consider:

```java
Map<Integer, String> map = new HashMap<>();

map.put(1, "A");
map.put(17, "B");
```

Assume capacity is 16.

Both may map to the same bucket:

```text
1  → bucket 1
17 → bucket 1
```

because:

```text
(16 - 1) & 1  = 1

(16 - 1) & 17 = 1
```

So internally:

```text
Bucket 1
   |
   v
+------+      +------+
|  1   | ---> | 17   |
| "A"  |      | "B"  |
+------+      +------+
```

---

# 11. What happens during `get()`?

Now:

```java
map.get(17);
```

HashMap essentially does:

```text
get(17)
   |
   v
Calculate hash
   |
   v
Calculate bucket
   |
   v
Go directly to bucket
   |
   v
Compare hash/key
   |
   v
Find 17
   |
   v
Return "B"
```

So it doesn't search the entire Map.

It jumps directly to the appropriate bucket.

That's why HashMap lookup is **O(1) average case**.

---

# 12. Why do we say average O(1), not guaranteed O(1)?

Because collisions can occur.

Ideal:

```text
Bucket
  |
  v
One node
```

Lookup:

```text
O(1)
```

But with many collisions:

```text
Bucket
  |
  v
Node → Node → Node → Node
```

you may have to inspect multiple entries.

Modern Java can convert heavily-collided buckets into a red-black tree, making lookup approximately:

```text
O(log n)
```

within that tree.

So a useful interview summary is:

```text
Average HashMap get/put → O(1)
Heavy collision tree → O(log n)
```

---

# 13. Java 8+ Treeification

This is a **very important senior-level question**.

In Java 8+, if a bucket becomes heavily populated, HashMap can transform its linked-list structure into a **red-black tree**.

Conceptually:

### Before

```text
Bucket
  |
  v
A → B → C → D → E → F → G → H
```

### After treeification

```text
          D
        /   \
       B     F
      / \   / \
     A   C E   G
              \
               H
```

This improves lookup from approximately:

```text
O(n)
```

to:

```text
O(log n)
```

under heavy collisions.

There are implementation thresholds governing when treeification occurs, and treeification also depends on the table having reached a sufficient capacity; otherwise HashMap may resize instead.

---

# 14. What is the default initial capacity?

For a standard `HashMap` constructor, the implementation uses a default capacity of:

```text
16
```

when the table is first allocated, subject to implementation details such as lazy initialization.

---

# 15. What is the default load factor?

Default load factor:

```text
0.75
```

This is very important.

If:

```text
capacity = 16
```

then the threshold is approximately:

```text
16 × 0.75 = 12
```

When the number of entries crosses the resize threshold, HashMap expands.

---

# 16. What happens during resizing?

Suppose:

```text
capacity = 16
```

and it needs to grow.

It generally doubles:

```text
16 → 32
```

Then entries need to be redistributed according to the new table size.

Conceptually:

```text
Before:

16 buckets
   ↓
[0][1][2][3]...[15]

After:

32 buckets
   ↓
[0][1][2][3]...[31]
```

This redistribution is one reason excessive resizing can be expensive.

---

# 17. Why does HashMap resize by doubling?

Because maintaining power-of-two capacities makes the bucket calculation efficient:

```java
index = (n - 1) & hash;
```

Doubling also allows the implementation to efficiently determine whether an existing entry stays in its old position or moves by the old capacity during resizing.

---

# 18. What are `initialCapacity` and `loadFactor`?

You can configure them:

```java
Map<Integer, String> map =
    new HashMap<>(100, 0.75f);
```

### Initial capacity

Controls the initial table sizing behavior.

### Load factor

Controls how full the table is allowed to become before resizing.

Default:

```text
capacity = 16
loadFactor = 0.75
```

---

# 19. Why not use a load factor of 1.0?

You could configure it, but it creates a trade-off.

Higher load factor:

```text
less memory
more collisions
```

Lower load factor:

```text
more memory
fewer collisions
```

So `0.75` is a practical compromise between memory usage and lookup performance.

---

# 20. The most important concept: `equals()` + `hashCode()`

Suppose:

```java
class Employee {

    private int id;

    // equals()
    // hashCode()
}
```

and:

```java
Map<Employee, String> map = new HashMap<>();

Employee e1 = new Employee(101);
Employee e2 = new Employee(101);

map.put(e1, "John");
```

If:

```java
e1.equals(e2)
```

is `true`, then:

```java
e1.hashCode() == e2.hashCode()
```

**must also be true.**

Otherwise HashMap may put logically equal keys into different buckets.

---

# 21. What happens if you override `equals()` but not `hashCode()`?

Bad implementation:

```java
class Employee {

    private int id;

    @Override
    public boolean equals(Object obj) {
        // compare id
    }

    // hashCode() missing
}
```

Then two objects can be logically equal but have different hash codes.

This can cause:

```java
map.get(e2)
```

to fail even though:

```java
e1.equals(e2)
```

is true.

### Senior interview answer

> If a class overrides `equals()`, it must override `hashCode()` consistently because hash-based collections use the hash code to locate the bucket and `equals()` to identify the matching key.

---

# 22. What happens if a key is mutable?

This is one of the **best HashMap interview traps**.

```java
Employee employee = new Employee(101);

map.put(employee, "John");

employee.setId(200);
```

Suppose `id` is used in:

```java
hashCode()
```

Originally:

```text
Employee 101
     ↓
hash = X
     ↓
bucket X
```

After changing ID:

```text
Employee 200
     ↓
hash = Y
     ↓
bucket Y
```

Now:

```java
map.get(employee);
```

may return:

```text
null
```

because HashMap looks in the bucket corresponding to the **new hash**, while the entry was inserted using the **old hash**.

### Best practice

Use immutable objects as HashMap keys.

---

# 23. Does HashMap allow null?

Yes.

```java
Map<String, String> map = new HashMap<>();

map.put(null, "ABC");
map.put("A", null);
```

Both are allowed.

But:

```java
ConcurrentHashMap
```

does **not** permit null keys or null values.

This is an excellent comparison question.

---

# 24. HashMap vs ConcurrentHashMap

| Feature           | HashMap                          | ConcurrentHashMap       |
| ----------------- | -------------------------------- | ----------------------- |
| Thread-safe       | No                               | Yes                     |
| Null key          | Yes                              | No                      |
| Null value        | Yes                              | No                      |
| Concurrent access | Unsafe                           | Designed for it         |
| Typical use       | Single-threaded/non-shared state | Shared concurrent state |

---

# 25. Complete HashMap flow

This is the flow I recommend remembering for interviews:

```text
                 map.put(key, value)
                         |
                         v
                  key.hashCode()
                         |
                         v
                  Hash spreading
                         |
                         v
              Calculate bucket index
                         |
                         v
                Is bucket empty?
                  /             \
                YES              NO
                 |                |
                 v                v
              Insert       Compare existing node
                                  |
                           hash + equals()
                              /       \
                           Same       Different
                            |             |
                            v             v
                      Replace value    Collision
                                           |
                                           v
                                  Linked structure /
                                  Red-black tree
```

And for retrieval:

```text
                 map.get(key)
                       |
                       v
                hashCode()
                       |
                       v
                 hash spreading
                       |
                       v
                bucket index
                       |
                       v
                Search bucket
                       |
                       v
                hash + equals()
                       |
                       v
                    value
```

---

# ⭐ Senior Interview: Explain HashMap in 60 Seconds

If the interviewer says:

> **"Explain the internal working of HashMap."**

You can give this answer:

HashMap stores key-value pairs using an internal bucket array. When we call `put(key, value)`, HashMap first obtains the key's `hashCode()` and performs hash spreading. It then calculates a bucket index using the hash and the table capacity.

If the bucket is empty, the new entry is inserted. If another entry already exists in that bucket, HashMap handles the collision by comparing the hash and then using `equals()` to determine whether the key already exists. If the key is the same, its value is replaced; otherwise, another node is added to the bucket.

In modern Java, if a bucket becomes heavily populated and the table is sufficiently large, the bucket can be converted from a linked structure into a red-black tree, improving lookup behavior under heavy collisions.

HashMap maintains a threshold based on capacity and load factor. With the default load factor of 0.75, when the map exceeds its threshold, the table is resized, generally doubling its capacity, and entries are redistributed.

For `get(key)`, HashMap calculates the hash and bucket index and then searches only the relevant bucket using hash comparison and `equals()`.

Therefore, `get()` and `put()` are O(1) on average, while heavy collisions can result in O(log n) lookup when the bucket is treeified. Correct implementation of `equals()` and `hashCode()` is essential, and mutable objects should generally not be used as keys.

---

## 🔥 10 Follow-up Questions You Should Prepare Next

For a **Senior Java Developer**, after you answer HashMap internals, an interviewer will very commonly ask:

1. **Why does HashMap use power-of-two capacity?**
2. **Why is the default load factor 0.75?**
3. **What exactly happens during HashMap resizing?**
4. **How does Java 8 HashMap handle collisions?**
5. **When does a bucket become a red-black tree?**
6. **What is the difference between HashMap and ConcurrentHashMap internally?**
7. **Why can't ConcurrentHashMap accept null keys/values?**
8. **What happens if `hashCode()` is the same for every object?**
9. **What happens if `equals()` returns true but `hashCode()` is different?**
10. **What happens if you modify a HashMap key after inserting it?**

These **10 follow-ups are more likely to distinguish a senior candidate** than simply knowing the definition of HashMap.
