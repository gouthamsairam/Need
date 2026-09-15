# HashMap Internal Working — Expanded Guide (Senior Java Developer)

`HashMap` internal working is one of the most important Java Collections questions for a Senior Java Developer. This guide walks through `put()` → hash calculation → bucket selection → collision handling → `get()` → resizing → Java 8+ treeification, with extra examples and deeper explanations added throughout.

---
Great follow-up — this is one of the most asked Java interview questions, and it connects directly to the HashMap internals we just covered. Let me break it down from first principles.

## The short answer

You override `equals()` and `hashCode()` together whenever you want objects of your class to be compared **by their content/value**, rather than by their **memory identity** — and this becomes *mandatory* the moment you use that class as a key in a `HashMap`, `HashSet`, or similar hash-based collection.

## Why `equals()` alone isn't enough

By default, every Java object inherits `equals()` and `hashCode()` from the `Object` class, and both are based on **memory address (identity)**:

```java
class Employee {
    int id;
    String name;
    Employee(int id, String name) { this.id = id; this.name = name; }
}

Employee e1 = new Employee(101, "John");
Employee e2 = new Employee(101, "John");

System.out.println(e1.equals(e2)); // false! Different objects in memory
System.out.println(e1 == e2);      // false
```

Even though `e1` and `e2` represent the *same employee data*, Java sees them as two completely different objects — because default `equals()` just checks `this == obj`.

If you only override `equals()` (to compare `id`) but forget `hashCode()`, you create a broken class. Here's why that matters so much.

## The contract: why they must be overridden *together*

Java's rule is:

> **If two objects are `equals()`, they MUST have the same `hashCode()`.**

The reverse isn't required — two unequal objects *can* share a hash code (that's just a collision, which is fine). But breaking the forward rule silently corrupts hash-based collections.

### Example of the bug

```java
class Employee {
    int id;
    Employee(int id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Employee)) return false;
        return this.id == ((Employee) o).id;
    }
    // hashCode() NOT overridden!
}
```

```java
Map<Employee, String> map = new HashMap<>();
map.put(new Employee(101), "John");

System.out.println(map.get(new Employee(101)));
// Prints: null  ← even though .equals() would return true!
```

**Why this happens:** `HashMap.get()` doesn't scan every entry comparing with `equals()`. It first computes `hashCode()` to jump straight to a bucket, *then* uses `equals()` only within that bucket. Since `hashCode()` wasn't overridden, Java falls back to the default identity-based hash — so the two `Employee(101)` objects (different instances) get **different hash codes**, land in **different buckets**, and `get()` never even attempts the `equals()` comparison. It just returns `null` and moves on.

## The correct implementation

```java
class Employee {
    private final int id;
    private final String name;

    Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                    // same reference → trivially equal
        if (!(o instanceof Employee)) return false;     // different type → not equal
        Employee other = (Employee) o;
        return id == other.id && name.equals(other.name); // compare actual content
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);  // must use the SAME fields as equals()
    }
}
```

```java
Map<Employee, String> map = new HashMap<>();
map.put(new Employee(101, "John"), "Engineering");

System.out.println(map.get(new Employee(101, "John")));
// Prints: "Engineering" ✔ works correctly now
```

**Key rule to remember:** whatever fields you use in `equals()`, use the *exact same fields* in `hashCode()`. That's what keeps the contract intact.

## A simple analogy

Think of `hashCode()` as a **street name** and `equals()` as the **exact house number**.

- HashMap uses `hashCode()` to find the right **street** (bucket) quickly.
- Then it uses `equals()` to find the right **house** on that street.

If two houses are actually the same house (`equals() == true`) but you gave them different street names (`hashCode()` differs), the mailman (HashMap) will search the wrong street entirely and report "no such house" — even though the house genuinely exists, just on a different street than where he's looking.

## Where this matters beyond HashMap

The same contract applies to:
- **`HashSet`** — internally just a `HashMap` under the hood; duplicates are detected using `equals()` + `hashCode()` together.
- **`HashMap`/`Hashtable`/`ConcurrentHashMap`** — any hash-bucket-based collection.
- Anywhere you do `list.contains(obj)`, `list.indexOf(obj)`, etc. on custom objects — these use `equals()` (though not `hashCode()`, since `ArrayList` isn't hash-based).

## A second example — using it in a `HashSet` for deduplication

```java
class Point {
    int x, y;
    Point(int x, int y) { this.x = x; this.y = y; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) return false;
        Point p = (Point) o;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

Set<Point> points = new HashSet<>();
points.add(new Point(1, 2));
points.add(new Point(1, 2));  // "duplicate" by value

System.out.println(points.size()); // 1 — correctly deduplicated
```

Without the overrides, `points.size()` would print `2`, because `HashSet` would treat them as two distinct objects (different identities), even though logically they represent the same point.

## Quick summary

| Without overriding | With correct overriding |
|---|---|
| Objects compared by memory address | Objects compared by actual field values |
| Same-content objects treated as different keys | Same-content objects treated as the same key |
| `HashMap.get()` can silently return `null` for a key that "should" match | `HashMap.get()` works correctly |
| `HashSet` allows logical duplicates | `HashSet` properly deduplicates |

**One more best practice:** prefer using `Objects.equals()` and `Objects.hash()` (from `java.util.Objects`) when writing these methods — they handle `null` fields safely and reduce boilerplate, which is exactly what most modern Java codebases (and IDE-generated code) use.
## 1. What is HashMap internally?

Conceptually, a `HashMap` stores data in an array called a **bucket table** (also called the "table" internally).

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

> **Extra explanation:** Think of the bucket table as a row of numbered mailboxes (0, 1, 2, 3...). Every key-value pair you `put()` gets assigned to exactly one mailbox based on a calculation done on the key. The `next` field exists because sometimes *two different keys* get assigned to the *same mailbox* — in that case, they're chained together like letters stacked inside the same mailbox, one pointing to the next.

### Extra Example — visualizing an empty HashMap
```java
Map<String, Integer> ages = new HashMap<>();
```
At this point, internally it looks like:
```text
Bucket table (capacity 16, all empty):
[0][1][2][3][4][5][6][7][8][9][10][11][12][13][14][15]
 -  -  -  -  -  -  -  -  -  -  -   -   -   -   -   -
```
No table array is actually allocated yet in many implementations until the first `put()` call — this is called **lazy initialization**. The table is only built when needed, saving memory for HashMaps that are created but never used.

---

## 2. What happens when we call `put()`?

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

> **Extra explanation:** `put()` is really answering two questions in sequence: **"Which mailbox does this key belong to?"** and then, once we're at that mailbox, **"Is this key already there, or is it a brand-new key?"** The first question is answered by hashing; the second is answered by comparing keys with `equals()`.

---

## 3. Step 1 — Calculate hash

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

> **Extra explanation — why "spreading" matters:** Imagine a hash code like `0x0000ABCD` — most of its "interesting" bits are in the lower 16 bits, and the upper 16 bits are all zero. If HashMap only looked at a *few* low-order bits to pick a bucket (which it does, as we'll see in Step 2), then any hash codes that only differ in the *upper* bits would all pile into the same buckets, causing needless collisions. By XOR-ing the upper 16 bits into the lower 16 bits (`h ^ (h >>> 16)`), any "uniqueness" hiding in the upper bits gets folded down and mixed into the bits that actually matter for bucket selection.

### Extra Example — Hash spreading effect
Suppose two keys have hash codes that look identical in the lower bits but differ in the upper bits:
```java
int h1 = 0x0001_0005; // hashCode = 65541
int h2 = 0x0002_0005; // hashCode = 131077
```
Without spreading, if HashMap only used the lowest 4 bits (`& 15`) directly on the raw hash code:
```text
h1 & 15 = 0x5 = 5
h2 & 15 = 0x5 = 5   → same bucket! (bad distribution)
```
With spreading (`h ^ (h >>> 16)`):
```text
h1 spread = 0x0001_0005 ^ 0x0000_0001 = 0x0001_0004
h2 spread = 0x0002_0005 ^ 0x0000_0002 = 0x0002_0007

h1 spread & 15 = 0x4 = 4
h2 spread & 15 = 0x7 = 7   → different buckets! (better distribution)
```
This is a simplified illustration, but it demonstrates *why* the spreading step exists — it reduces accidental collisions caused by patterns in hash codes.

---

## 4. Step 2 — Calculate bucket index

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

> **Extra explanation:** `(n - 1)` when `n` is a power of two produces a binary number that's all 1s in the lower bits — e.g., if `n = 16` (binary `10000`), then `n - 1 = 15` (binary `01111`). ANDing any number with `01111` simply **keeps only its lowest 4 bits** and zeroes out everything above. That's mathematically equivalent to `hash % 16`, but implemented as a single, extremely fast bitwise operation instead of a division.

### Extra Example — bucket index for multiple keys
```text
table length = 16  (n - 1 = 15, binary 1111)

Key: 20   → 20 & 15  = 4
Key: 36   → 36 & 15  = 4   (collision with 20!)
Key: 7    → 7  & 15  = 7
Key: 101  → 101 & 15 = 5
Key: 5    → 5  & 15  = 5   (collision with 101!)
```
Notice that `20` and `36` differ by exactly `16` (the table size) — this is a classic pattern that causes collisions, because adding/subtracting a multiple of the table size doesn't change the lower bits used for indexing.

---

## 5. Why does HashMap use `&` instead of `%`?

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

> **Extra explanation:** At the CPU level, a bitwise AND is a single-cycle operation, while integer modulo/division typically takes several CPU cycles (division is one of the more expensive basic arithmetic operations). Since `put()` and `get()` are meant to be O(1) and are called extremely often, even this small per-call saving adds up significantly across millions of operations — this is a deliberate low-level performance optimization baked into the data structure's design.

### Extra Example — why `%` would still "work" but isn't used
```text
101 % 16 = 5      (same result as 101 & 15 = 5)
```
Mathematically identical for power-of-two divisors — but `&` is implemented directly by the CPU's bitwise logic unit, while `%` typically routes through the (slower) integer division circuitry. This is purely a performance choice, not a correctness one.

---

## 6. What if the bucket is empty?

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

> **Extra explanation:** This is the simplest and fastest case — no comparisons needed, no traversal, just a direct array write at index 5. This is what gives HashMap its average O(1) `put()` performance when collisions are rare.

---

## 7. What happens if another key goes to the same bucket?

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

> **Extra explanation:** This is a critical point that trips people up: collisions happen *by bucket index*, not by hash code equality. Two completely different hash codes can still land in the same bucket, because the bucket index is only derived from a *portion* of the hash (via the `& (n-1)` operation). This is why the number of possible bucket indices (equal to table capacity) is much smaller than the number of possible hash codes (2^32), and some overlap is mathematically inevitable — this is called the **pigeonhole principle**.

### Extra Example — Real collision with different hash codes
```java
"Aa".hashCode()  // = 2112
"BB".hashCode()  // = 2112 (famous Java example — different strings, same hashCode!)
```
Both `"Aa"` and `"BB"` produce the exact same `hashCode()` in Java due to how `String.hashCode()` is computed — yet they are clearly different keys (`"Aa".equals("BB")` is `false`). If both are used as keys in the same HashMap, they will land in the *same bucket* and be chained together, and `equals()` will be used to tell them apart during lookups.

---

## 8. Very important: Same hash does NOT mean same key

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

> **Extra explanation:** This is exactly the `"Aa"` / `"BB"` situation shown above. A hash code is meant to be a *fast, approximate fingerprint* of an object — it's entirely acceptable, and expected, for different objects to occasionally share a fingerprint. `equals()` is the *authoritative, exact* check. Hashing narrows down "which bucket to search," and `equals()` answers "is this actually the same key."

---

## 9. How does HashMap handle collision?

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

> **Extra explanation:** Notice the order of the check: HashMap compares `hash` **first** (a cheap integer comparison) before calling `equals()` (which can be an expensive, potentially recursive comparison for complex objects). This is a performance optimization — if the hashes don't even match, there's no point calling the more expensive `equals()` method at all, since `equals() == true` always implies `hashCode()` equality (assuming a correctly implemented class).

### Extra Example — Replace vs. Collision in code
```java
Map<Integer, String> map = new HashMap<>();

map.put(1, "Apple");
map.put(1, "Banana");   // SAME key → replaces value

System.out.println(map.get(1)); // "Banana"
System.out.println(map.size()); // 1  (not 2 — it was a replace, not an addition)
```
```java
map.put(17, "Cherry");   // DIFFERENT key that happens to land in the same bucket as key 1
System.out.println(map.size()); // 2  (a genuine new entry was added)
```

---

## 10. Example

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

### Extra Example — extending this with a third colliding key
```java
map.put(33, "C");   // 33 & 15 = 1  → also lands in bucket 1!
```
Now bucket 1 holds a chain of three nodes:
```text
Bucket 1
   |
   v
+------+      +------+      +------+
|  1   | ---> | 17   | ---> | 33   |
| "A"  |      | "B"  |      | "C"  |
+------+      +------+      +------+
```
Notice the pattern: `1`, `17`, `33` are all `16` apart. Any integer keys that are exact multiples of the table capacity apart from each other will always collide into the same bucket — a good practical example of *why* good hash distribution matters, and why table resizing (doubling capacity) helps reduce this over time.

---

## 11. What happens during `get()`?

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

> **Extra explanation:** The key insight for interviews is that `get()` **recomputes the hash of the key you're searching for**, using the exact same hashing + spreading + indexing steps used in `put()`. This guarantees that a key inserted at bucket 5 will always be looked up at bucket 5 too — the math is deterministic and repeatable as long as the key's `hashCode()` doesn't change between insertion and lookup (a good reason keys should be immutable — see Section 22).

### Extra Example — get() walking a collision chain
```java
map.get(33); // From our 3-node chain example above
```
Internally:
```text
1. hash(33) → bucket 1
2. Go to bucket 1, start at the head of the chain
3. Compare: node "1" → hash/key mismatch, move to next
4. Compare: node "17" → hash/key mismatch, move to next
5. Compare: node "33" → MATCH → return "C"
```
This shows why long collision chains slow down `get()` — in the worst case, you might have to walk the *entire* chain before finding (or failing to find) your key.

---

## 12. Why do we say average O(1), not guaranteed O(1)?

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

> **Extra explanation:** There's actually a worst-case scenario even worse than a plain linked list: if **every single key** in the HashMap happens to hash to the *same* bucket (e.g., due to a poorly written or malicious `hashCode()` implementation that always returns a constant), then *before* Java 8's treeification, the entire HashMap would degrade to **O(n)** lookup — essentially becoming a slow linked list wearing a HashMap's clothing. Treeification specifically exists to put a ceiling on this worst case, capping degraded performance at O(log n) instead of O(n). This was actually a known **denial-of-service vulnerability** in older web frameworks — attackers could craft form field names or JSON keys with deliberately colliding hash codes to degrade server performance.

---

## 13. Java 8+ Treeification

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

> **Extra explanation:** Red-black trees are **self-balancing binary search trees** — meaning no matter how items are inserted, the tree keeps itself roughly balanced so no single path from root to leaf gets too long. This balance is exactly what guarantees O(log n) lookup, insert, and delete within the treeified bucket. Note the important nuance mentioned above: if the *overall table* is still small, HashMap prefers to **resize/grow the table first** (spreading entries into more buckets) rather than treeify, because growing the table might naturally fix the collision problem without needing the added complexity of a tree structure at all. Treeification is really a "last resort" safeguard for genuinely bad hash distribution, not a first response to normal collisions.

### Extra Example — why treeification needs `Comparable` (or falls back)
Red-black trees need a way to *order* their nodes (left = smaller, right = larger), which normal linked-list buckets don't need. If your key class implements `Comparable`, that ordering is used directly:
```java
class EmployeeId implements Comparable<EmployeeId> {
    int id;
    public int compareTo(EmployeeId other) { return Integer.compare(id, other.id); }
}
```
If keys do **not** implement `Comparable`, the JDK falls back to comparing based on class name and identity hash code as a tie-breaker — it's less meaningful ordering, but it still lets the tree structure function correctly for storage/retrieval purposes.

---

## 14. What is the default initial capacity?

For a standard `HashMap` constructor, the implementation uses a default capacity of:

```text
16
```

when the table is first allocated, subject to implementation details such as lazy initialization.

### Extra Example — setting a custom initial capacity to avoid early resizing
```java
// If you KNOW you're going to store ~1000 items,
// setting a higher initial capacity avoids multiple resize operations:
Map<String, String> bigMap = new HashMap<>(1500);  
```
Sizing the map up front avoids the repeated doubling-and-rehashing cost that would otherwise happen as the map grows from 16 → 32 → 64 → 128 → ... → 1024 → 2048, saving real CPU time in performance-sensitive code that loads large datasets.

---

## 15. What is the default load factor?

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

### Extra Example — tracking the threshold across growth
```text
Capacity 16  → threshold = 12   (resize triggers on the 13th entry)
Capacity 32  → threshold = 24
Capacity 64  → threshold = 48
Capacity 128 → threshold = 96
```
So if you insert exactly 13 entries into a fresh default HashMap, a resize from 16 → 32 buckets is triggered automatically as soon as the 13th entry is added — even though the map still has plenty of "theoretical" room left.

---

## 16. What happens during resizing?

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

> **Extra explanation:** "Redistribution" (often called **rehashing**) means every single existing entry's bucket index must be recalculated against the *new* capacity, because `index = (n-1) & hash` depends on `n`. An entry that lived happily in bucket 5 when `n = 16` might now belong in bucket 5 *or* bucket 21 when `n = 32` — it has to be checked and potentially moved. For a HashMap with millions of entries, a resize event can cause a noticeable, brief performance pause, which is why pre-sizing (Section 14's example) matters for performance-critical, large-scale code.

### Extra Example — the "same bucket or +oldCapacity" rule
When resizing from 16 → 32, Java's implementation uses a clever trick: for each existing node, it only needs to check **one extra bit** of the hash to decide whether the entry stays in its current bucket index or moves to `currentIndex + oldCapacity`:
```text
Key 5  (bucket 5 at capacity 16) → stays at bucket 5 OR moves to bucket 21 (5 + 16)
Key 21 (bucket 5 at capacity 16) → stays at bucket 5 OR moves to bucket 21 (5 + 16)
```
This means a full "recompute everything from scratch" isn't needed — just one extra bit check per node — making resize operations more efficient than they might initially sound.

---

## 17. Why does HashMap resize by doubling?

Because maintaining power-of-two capacities makes the bucket calculation efficient:

```java
index = (n - 1) & hash;
```

Doubling also allows the implementation to efficiently determine whether an existing entry stays in its old position or moves by the old capacity during resizing.

> **Extra explanation:** This ties directly back to Section 16's "extra bit" trick — that optimization is *only* possible because the capacity always doubles (a power-of-two multiplied by 2 is still a power-of-two). If HashMap grew capacity by some arbitrary amount (like +10 each time), this elegant bit trick wouldn't work, and every single entry would need a full hash-and-modulo recalculation from scratch during every resize — a strictly more expensive operation.

---

## 18. What are `initialCapacity` and `loadFactor`?

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

### Extra Example — a memory-conscious low-load-factor map
```java
// Prioritize speed over memory: resize earlier, keep buckets sparser
Map<String, String> fastLookupMap = new HashMap<>(16, 0.5f);
// Threshold = 16 * 0.5 = 8 → resizes sooner, keeping average chain length shorter
```
```java
// Prioritize memory over speed: allow more entries per bucket before resizing
Map<String, String> memoryEfficientMap = new HashMap<>(16, 0.9f);
// Threshold = 16 * 0.9 = 14.4 ≈ 14 → resizes later, uses memory more fully
```

---

## 19. Why not use a load factor of 1.0?

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

> **Extra explanation:** A load factor of exactly `1.0` would mean HashMap only resizes once the table is *completely* full (every bucket has at least one entry, statistically speaking) — but due to how hashing distributes entries somewhat randomly across buckets (not perfectly evenly), by the time a table is "full" under a 1.0 load factor, many buckets already have multiple chained entries while others remain empty. `0.75` was chosen by the JDK's designers as an empirically good balance based on statistical analysis of typical hash distributions — it resizes early enough to keep average chain lengths short, without wasting so much memory that it resizes needlessly often.

---

## 20. The most important concept: `equals()` + `hashCode()`

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

> **Extra explanation:** This is called the **`equals`/`hashCode` contract**, and it's a one-directional rule: equal objects MUST have equal hash codes, but equal hash codes do NOT need to imply equal objects (that's the whole point of allowing collisions, as shown in Section 7/8 with `"Aa"` and `"BB"`). Breaking this contract is one of the most common — and most subtle — bugs in Java applications involving custom key classes.

### Extra Example — a correctly implemented Employee key class
```java
class Employee {
    private final int id;   // final = immutable, safe as a key

    public Employee(int id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee other = (Employee) o;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);   // consistent with equals()
    }
}
```
```java
Map<Employee, String> map = new HashMap<>();
map.put(new Employee(101), "John");

System.out.println(map.get(new Employee(101))); // "John" — works correctly!
```
This works because `e1.equals(e2) == true` guarantees `e1.hashCode() == e2.hashCode() == true` — so the lookup finds the correct bucket, and `equals()` confirms the match within that bucket.

---

## 21. What happens if you override `equals()` but not `hashCode()`?

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

> **Extra explanation:** Without an overridden `hashCode()`, Java falls back to `Object.hashCode()`, which is typically derived from the object's **memory address / identity** — meaning two *different* `Employee` object instances will almost always get *different* default hash codes, even if their `id` fields are identical. This means `e1` and `e2` (with the same `id`) would likely be routed to *completely different buckets* — so `map.get(e2)` would go looking in the wrong bucket entirely and never even attempt the `equals()` comparison, silently returning `null`.

### Extra Example — demonstrating the bug in code
```java
class BrokenEmployee {
    private int id;
    public BrokenEmployee(int id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BrokenEmployee)) return false;
        return this.id == ((BrokenEmployee) o).id;
    }
    // hashCode() NOT overridden — uses Object's default identity-based hash!
}

Map<BrokenEmployee, String> map = new HashMap<>();
map.put(new BrokenEmployee(101), "John");

System.out.println(map.get(new BrokenEmployee(101))); 
// Prints: null  <-- BUG! Even though the two objects are .equals()
```
This is a genuinely common real-world bug — often caught only in production when a lookup mysteriously "loses" data that was clearly inserted moments earlier.

---

## 22. What happens if a key is mutable?

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

> **Extra explanation:** The entry itself is **not moved** when you mutate the key after insertion — the `Node` object physically remains sitting in the *old* bucket (bucket X), because HashMap has no way of knowing you changed something that affects its hash code; it doesn't "watch" your objects for changes. So now there's a mismatch: the entry physically lives in bucket X, but any `get()` call using the *current* state of that key will compute hash Y and search bucket Y instead — where nothing is found. The entry isn't deleted; it's just **permanently lost/unreachable** through normal lookups (though it would still show up if you iterated over the whole map, and it still consumes memory).

### Extra Example — the "orphaned entry" caused by a mutable key
```java
class MutablePoint {
    int x, y;
    MutablePoint(int x, int y) { this.x = x; this.y = y; }

    @Override public int hashCode() { return Objects.hash(x, y); }
    @Override public boolean equals(Object o) {
        if (!(o instanceof MutablePoint)) return false;
        MutablePoint p = (MutablePoint) o;
        return x == p.x && y == p.y;
    }
}

Map<MutablePoint, String> map = new HashMap<>();
MutablePoint p = new MutablePoint(1, 1);

map.put(p, "origin-area");
System.out.println(map.get(p));       // "origin-area" ✔ works fine here

p.x = 99;   // mutate the key AFTER insertion — danger!

System.out.println(map.get(p));       // null ✘ — entry is now "lost"
System.out.println(map.size());       // 1    — but it's still technically in the map!
System.out.println(map.containsKey(p)); // false — same lookup problem

// Proof the entry still physically exists:
for (Map.Entry<MutablePoint, String> e : map.entrySet()) {
    System.out.println(e.getValue()); // prints "origin-area" — found via iteration, not get()!
}
```
This example demonstrates the "orphaned entry" problem concretely — the value is still in the map (iteration finds it), but it's permanently unreachable via `get()`/`containsKey()` because those methods rely on the (now-wrong) recalculated hash.

---

## 23. Does HashMap allow null?

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

> **Extra explanation:** HashMap handles the `null` key as a **special case** — it's always placed in **bucket 0** (since there's no `hashCode()` to call on a `null` reference, the implementation just hard-codes its hash as `0`). There can only ever be **one** `null` key in a HashMap (calling `put(null, ...)` again just replaces the previous null-keyed value, exactly like any other key). `ConcurrentHashMap` disallows nulls specifically because, in a concurrent context, a `null` return from `get(key)` would be ambiguous — you couldn't tell whether it meant "the key isn't in the map" or "the key IS in the map, but another thread is concurrently in the middle of inserting a null value for it."

### Extra Example
```java
Map<String, String> map = new HashMap<>();
map.put(null, "first");
map.put(null, "second");   // replaces, doesn't add a duplicate

System.out.println(map.get(null)); // "second"
System.out.println(map.size());    // 1

// vs. ConcurrentHashMap:
Map<String, String> concurrentMap = new java.util.concurrent.ConcurrentHashMap<>();
try {
    concurrentMap.put(null, "value"); // throws NullPointerException immediately
} catch (NullPointerException e) {
    System.out.println("ConcurrentHashMap rejected the null key!");
}
```

---

## 24. HashMap vs ConcurrentHashMap

| Feature           | HashMap                          | ConcurrentHashMap        |
| ----------------- | --------------------------------- | ------------------------- |
| Thread-safe       | No                                 | Yes                        |
| Null key          | Yes                                | No                         |
| Null value        | Yes                                | No                         |
| Concurrent access | Unsafe                             | Designed for it            |
| Typical use       | Single-threaded/non-shared state  | Shared concurrent state    |

### Extra Example — the danger of using plain HashMap across threads
```java
Map<Integer, String> unsafeMap = new HashMap<>();

Runnable writer = () -> {
    for (int i = 0; i < 100_000; i++) unsafeMap.put(i, "value" + i);
};

Thread t1 = new Thread(writer);
Thread t2 = new Thread(writer);
t1.start();
t2.start();
// Risk: this can corrupt the internal structure, cause infinite loops during
// resize (a well-known historical HashMap concurrency bug), lose entries,
// or throw ConcurrentModificationException — all without any warning at compile time.
```
```java
// Safer alternative:
Map<Integer, String> safeMap = new java.util.concurrent.ConcurrentHashMap<>();
// Designed from the ground up to allow concurrent reads/writes without external locking
```

---

## 25. Complete HashMap flow

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

## ⭐ Senior Interview: Explain HashMap in 60 Seconds

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

## 🔥 10 Follow-up Questions You Should Prepare Next (with brief answers added)

For a **Senior Java Developer**, after you answer HashMap internals, an interviewer will very commonly ask:

1. **Why does HashMap use power-of-two capacity?**
   → So bucket indexing can use fast bitwise AND (`hash & (n-1)`) instead of slower modulo division, and to make the resize "extra bit" optimization possible (see Section 16).

2. **Why is the default load factor 0.75?**
   → It's an empirically chosen balance between memory usage and collision frequency — high enough to avoid wasting memory, low enough to keep average bucket chain length short.

3. **What exactly happens during HashMap resizing?**
   → Capacity doubles, and every existing entry is rehashed/redistributed into the new, larger table using an efficient "same bucket or +oldCapacity" check.

4. **How does Java 8 HashMap handle collisions?**
   → Colliding entries form a linked chain within a bucket by default; if a bucket gets too large (and the table is big enough), that chain converts into a red-black tree for faster worst-case lookup.

5. **When does a bucket become a red-black tree?**
   → When the number of entries in a single bucket crosses an internal threshold AND the overall table has reached a sufficient size (otherwise HashMap prefers to resize the whole table first).

6. **What is the difference between HashMap and ConcurrentHashMap internally?**
   → HashMap has no internal synchronization at all; ConcurrentHashMap uses fine-grained internal synchronization/CAS techniques so multiple threads can safely read and write concurrently without external locking.

7. **Why can't ConcurrentHashMap accept null keys/values?**
   → Because a `null` return from `get()` would be ambiguous in a concurrent context — you couldn't distinguish "key absent" from "key present but value not yet visible/being written by another thread."

8. **What happens if `hashCode()` is the same for every object?**
   → All entries would land in the same bucket, degrading the HashMap to a single long chain (or a single tree) — O(n) or O(log n) lookup instead of O(1), a worst-case performance scenario.

9. **What happens if `equals()` returns true but `hashCode()` is different?**
   → This violates the `equals`/`hashCode` contract; the two "equal" objects may end up in different buckets, causing `get()`/`containsKey()` to incorrectly report the key as missing.

10. **What happens if you modify a HashMap key after inserting it?**
    → If the modification changes the value(s) used in `hashCode()`, the entry becomes "orphaned" — it stays in its original bucket, but future lookups compute a different (new) bucket and won't find it (see Section 22's detailed example).

These **10 follow-ups are more likely to distinguish a senior candidate** than simply knowing the definition of HashMap.

---

## Extra Section: Quick Reference Cheat Sheet

| Concept | Key Fact |
|---|---|
| Default capacity | 16 |
| Default load factor | 0.75 |
| Resize trigger | `size > capacity × loadFactor` |
| Resize behavior | Doubles capacity, rehashes all entries |
| Bucket index formula | `(capacity - 1) & spreadHash` |
| Hash spreading | `h ^ (h >>> 16)` |
| Collision resolution | Linked list → red-black tree (Java 8+) |
| Treeify trigger | Bucket size threshold reached AND table large enough |
| Average time complexity | O(1) for `get`/`put` |
| Worst-case time complexity | O(log n) with treeification (was O(n) pre-Java 8) |
| Null keys | 1 allowed, stored at bucket 0 |
| Null values | Allowed |
| Thread safety | Not thread-safe (`ConcurrentHashMap` for concurrency) |
| Key requirement | Must correctly implement `equals()` + `hashCode()`, and ideally be immutable |