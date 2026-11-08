# Java Collections — Senior Java Developer Interview Master Guide (2026)

For a **Senior Java Developer**, Collections interviews go far beyond `ArrayList vs LinkedList`. Interviewers test **internals, performance, concurrency, design decisions, real-world scenarios and follow-up questions**.

## How to study each question

Every question in this guide follows the same pattern:

1.**Definition** — what the collection or concept is.
2.**Simple explanation** — an everyday analogy.
3.**How it works** — internals, diagrams and complexity.
4.**Example** — compilable Java code.
5.**Real-time example** — how it appears in production systems.
6.**Interview summary** — a short answer to say aloud.

## Section map

| Part | Topic | Questions |
| --- | --- | --- |
| Part 1 | Fundamentals and hierarchy | Q1-Q3 |
| Part 2 | List: ArrayList, LinkedList, Vector, CopyOnWriteArrayList | Q4-Q7 |
| Part 3 | Set: HashSet, LinkedHashSet, TreeSet | Q8-Q10 |
| Part 4 | Map: HashMap, LinkedHashMap, TreeMap, ConcurrentHashMap | Q11-Q17 |
| Part 5 | Queue, Deque, PriorityQueue, BlockingQueue | Q18-Q20B |
| Part 6 | Iterators, fail-fast and fail-safe | Q21 |
| Part 7 | Comparable vs Comparator | Q22 |
| Part 8 | Java 8+ collection features and streams | Q23-Q24 |
| Part 9 | Real-world scenario questions | Q25-Q30 |
| Part 10 | Tricky interview questions | Q31-Q35 |
| Part 11 | Additional senior-level questions | Q36-Q60 |
| Part 12 | Rapid scenario round | S1-S54 |
| Appendix | Production and current-industry guidance | A1-A43 |

## Big-O cheat sheet (memorise this table)

| Collection | get / lookup | add | remove | contains | Ordered? | Nulls | Thread-safe? |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `ArrayList` | O(1) by index | O(1) amortized (end) | O(n) | O(n) | Insertion | Yes | No |
| `LinkedList` | O(n) by index | O(1) at ends | O(1) at ends | O(n) | Insertion | Yes | No |
| `ArrayDeque` | O(1) at ends | O(1) amortized | O(1) at ends | O(n) | Insertion | No | No |
| `HashSet` | — | O(1) avg | O(1) avg | O(1) avg | None | One null | No |
| `LinkedHashSet` | — | O(1) avg | O(1) avg | O(1) avg | Insertion | One null | No |
| `TreeSet` | — | O(log n) | O(log n) | O(log n) | Sorted | No | No |
| `HashMap` | O(1) avg | O(1) avg | O(1) avg | O(1) avg | None | One null key | No |
| `LinkedHashMap` | O(1) avg | O(1) avg | O(1) avg | O(1) avg | Insertion/access | One null key | No |
| `TreeMap` | O(log n) | O(log n) | O(log n) | O(log n) | Sorted by key | No null key | No |
| `PriorityQueue` | O(1) peek | O(log n) | O(log n) poll | O(n) | Heap (head only) | No | No |
| `ConcurrentHashMap` | O(1) avg | O(1) avg | O(1) avg | O(1) avg | None | No | Yes |
| `CopyOnWriteArrayList` | O(1) | O(n) copy | O(n) copy | O(n) | Insertion | Yes | Yes |

---

# Part 1: Fundamentals — Why, What, Where

### Q1. What is the Java Collections Framework and why do we need it?

**Definition:** The Java Collections Framework (JCF) is a unified architecture in `java.util` — **interfaces** (`List`, `Set`, `Queue`, `Map`), **implementations** (`ArrayList`, `HashMap`, ...) and **algorithms** (`sort`, `binarySearch`, ...) — for storing and manipulating groups of objects.

**Simple explanation:** Arrays are like a fixed row of lockers — you must know the size in advance and write your own logic to search or sort. Collections are like a smart warehouse that grows automatically and already knows how to search, sort and remove duplicates.

| Without Collections (arrays) | With Collections |
| --- | --- |
| Fixed size: `int[] arr = new int[10]` | Dynamic size: `ArrayList` grows automatically |
| Write your own search, sort, insert logic | Built-in `sort()`, `binarySearch()`, `removeIf()` |
| No standard way to pass groups of objects | Standard interfaces `List`, `Set`, `Map` as parameters |
| Type-unsafe `Object[]` before generics | Type-safe with generics: `List<String>` |
| No thread-safe options | `ConcurrentHashMap`, `CopyOnWriteArrayList`, `BlockingQueue` |

**Before Collections (Java 1.0):**

```java
Vector v = new Vector(); // synchronized on every call
v.add("hello");
String s = (String) v.get(0); // manual cast, ClassCastException risk

Hashtable ht = new Hashtable();
ht.put("key", "value");
String val = (String) ht.get("key"); // manual cast again
```

**With Collections and generics (Java 5+):**

```java
List<String> list = new ArrayList<>();
list.add("hello");
String s = list.get(0); // no cast, checked at compile time

Map<String, String> map = new HashMap<>();
map.put("key", "value");
String val = map.get("key");
```

**Real-time example:** A notification batch loads 50,000 pending records. With an array you would need to know the count up front and resize manually. With `List<Notification>` the list grows as records arrive, `Map<String, List<Notification>>` groups them by customer, and `Set<String>` removes duplicate email addresses — all in a few lines.

**Interview summary:**

> The Collections Framework gives standard interfaces, ready implementations and algorithms for groups of objects. It removes boilerplate, provides type safety through generics, lets APIs accept `List` or `Map` instead of concrete classes, and includes concurrent collections for multi-threaded code.

### Q2. Draw and explain the complete Collections hierarchy.

```text
Iterable
 └── Collection
 ├── List
 │ ├── ArrayList
 │ ├── LinkedList (also implements Deque)
 │ ├── Vector ── Stack (legacy)
 │ └── CopyOnWriteArrayList (concurrent)
 ├── Set
 │ ├── HashSet ── LinkedHashSet
 │ ├── SortedSet ── NavigableSet ── TreeSet
 │ ├── EnumSet
 │ └── ConcurrentSkipListSet (concurrent)
 └── Queue
 ├── PriorityQueue
 ├── BlockingQueue (ArrayBlockingQueue, LinkedBlockingQueue, DelayQueue ...)
 └── Deque ── ArrayDeque, LinkedList

Map (separate hierarchy — NOT a Collection)
 ├── HashMap ── LinkedHashMap
 ├── Hashtable (legacy)
 ├── SortedMap ── NavigableMap ── TreeMap
 ├── ConcurrentMap ── ConcurrentHashMap, ConcurrentSkipListMap
 └── EnumMap, WeakHashMap, IdentityHashMap
```

**Why is `Map` not a `Collection`?** A `Collection` holds single elements (`add(E)`), while a `Map` holds key-value pairs (`put(K, V)`). The operations do not fit one interface, so `Map` has its own hierarchy but exposes collection *views*: `keySet()`, `values()` and `entrySet()`.

| Interface | Purpose | Ordering | Duplicates | Null |
| --- | --- | --- | --- | --- |
| `List` | Ordered sequence, index access | Insertion order | Yes | Yes |
| `Set` | Unique elements | Depends on implementation | No | Depends |
| `Queue` | Hold elements before processing | FIFO or priority | Yes | Depends |
| `Deque` | Double-ended queue (queue + stack) | Both ends | Yes | No (`ArrayDeque`) |
| `Map` | Key-value pairs | Depends on implementation | Keys no, values yes | Depends |

**Real-time mapping:**

```text
List -> order lines on an invoice (order matters, duplicates allowed)
Set -> unique roles of a user (ADMIN, READER)
Queue -> jobs waiting for a worker thread
Deque -> undo/redo history in an editor
Map -> customerId -> Customer cache
```

### Q3. What is the difference between Collection and Collections?

| `Collection` | `Collections` |
| --- | --- |
| Interface: `java.util.Collection` | Utility class: `java.util.Collections` |
| Root interface for `List`, `Set`, `Queue` | Only static helper methods |
| Defines `add()`, `remove()`, `size()`, `iterator()` | Provides `sort()`, `unmodifiableList()`, `synchronizedList()`, `emptyList()`, `frequency()` |

**Memory trick:** `Collection` is the *thing*; `Collections` (with an **s**) is the *toolbox*, like `Array` vs `Arrays`.

```java
// Collection — the interface
Collection<String> items = new ArrayList<>();
items.add("Apple");

// Collections — the utility class
List<String> list = new ArrayList<>(List.of("Banana", "Apple", "Cherry"));
Collections.sort(list); // [Apple, Banana, Cherry]
List<String> readOnly = Collections.unmodifiableList(list); // read-only view
List<String> synced = Collections.synchronizedList(list); // synchronized wrapper
int count = Collections.frequency(list, "Apple"); // 1
```

**Real-time example:** A REST method must never return `null` for "no results":

```java
public List<Order> findOrders(String customerId) {
List<Order> orders = repository.findByCustomerId(customerId);
 return orders == null ? Collections.emptyList() : orders;
}
```

Callers can loop over the result without a null check, avoiding `NullPointerException` in every consumer.

# Part 2: List Interface — ArrayList, LinkedList, Vector, CopyOnWriteArrayList

### Q4. How does ArrayList work internally?

**Definition:** `ArrayList` is a resizable array. Internally it keeps an `Object[] elementData` and an `int size`.

**Simple explanation:** Think of a train. Seats are numbered, so jumping to seat 500 is instant. When the train is full, the railway adds a **new, longer train** and moves every passenger across — that move is the expensive resize.

```text
new ArrayList<>() -> elementData = {} (empty), capacity allocated lazily
first add("A") -> capacity becomes 10, elementData[0] = "A", size = 1
...
11th add -> size == capacity? YES
 -> newCapacity = old + (old >> 1) = 10 + 5 = 15
 -> Arrays.copyOf(elementData, 15) <- O(n) copy
 -> elementData[10] = element, size = 11

Capacity growth: 10 -> 15 -> 22 -> 33 -> 49 -> 73 ... (about +50% each time)
```

| Operation | Complexity | Why |
| --- | --- | --- |
| `get(index)` | O(1) | Direct array index |
| `add(element)` at end | Amortized O(1) | Usually O(1); occasional O(n) resize |
| `add(index, element)` | O(n) | Shifts all later elements right |
| `remove(index)` | O(n) | Shifts all later elements left |
| `contains(element)` | O(n) | Linear scan using `equals()` |
| Iteration | O(n), very fast | Contiguous memory is CPU-cache friendly |

**Real-time example 1: pre-size when you know the count**

```java
List<NotificationDto> dtos = new ArrayList<>(entities.size()); // no resize copies
for (NotificationEntity e : entities) {
 dtos.add(NotificationDto.from(e));
}
```

**Real-time example 2: the hidden O(n²) bug**

```java
// BAD: removing from the front of a 200,000-element ArrayList
while (!pending.isEmpty()) {
 process(pending.remove(0)); // shifts 199,999 elements every time -> O(n²)
}

// GOOD: use a queue
Deque<Task> pendingQueue = new ArrayDeque<>(pending);
while (!pendingQueue.isEmpty()) {
 process(pendingQueue.pollFirst()); // O(1)
}
```

A batch job that took 40 minutes finished in seconds after this one-line change.

**Interview summary:**

> `ArrayList` is a dynamic array: O(1) index access and amortized O(1) append, O(n) insert or remove in the middle. It grows by about 50% by copying into a bigger array, so I pre-size it when the size is known. It is my default `List` because it is compact and cache-friendly.

### Q5. How does LinkedList work internally? How is it different from ArrayList?

**Definition:** `LinkedList` is a **doubly-linked list**. Each element is wrapped in a `Node` that points to the previous and next nodes. It implements both `List` and `Deque`.

```java
// Simplified from JDK source
private static class Node<E> {
E item;
Node<E> next;
Node<E> prev;
}
```

**Simple explanation:** A treasure hunt — each clue tells you where the next one is. Adding a clue in the middle is easy once you are standing there, but finding clue 500 means following 499 clues first.

```text
ArrayList : [A][B][C][D][E] <- one contiguous block
LinkedList: [prev|A|next] <-> [prev|B|next] <-> ... <- nodes scattered on the heap
```

| Operation | ArrayList | LinkedList |
| --- | --- | --- |
| `get(index)` | O(1) | O(n) — walks nodes from the nearest end |
| `add(element)` at end | O(1) amortized | O(1) |
| `add(0, element)` | O(n) — shifts all | O(1) — relink head |
| `remove(0)` | O(n) — shifts all | O(1) |
| Insert in middle | O(n) shift | O(n) to find + O(1) to link |
| Memory per element | ~4-8 bytes (reference) | ~24-40 bytes (node object + 2 pointers) |
| CPU cache | Excellent | Poor (pointer chasing) |

**When to use LinkedList:** rarely. Only when you insert/remove through a `ListIterator` at positions you are already at, or you need a `List` that is also a `Deque`.

**When to use ArrayList:** almost always — index access, iteration, appends.

**Real-time example:** benchmark results on 1 million `Integer` elements typically show `ArrayList` iteration several times faster than `LinkedList`, because the CPU pre-fetches contiguous memory. For queue behaviour, `ArrayDeque` is faster than both.

**Interview summary:**

> `LinkedList` gives O(1) insert/remove at the ends or at a known node, but O(n) access by index, high memory overhead and poor cache locality. In practice I use `ArrayList` for lists and `ArrayDeque` for queues and stacks.

### Q6. What is the difference between ArrayList and Vector?

| Feature | ArrayList | Vector |
| --- | --- | --- |
| Synchronization | Not synchronized | Every method `synchronized` |
| Performance | Fast | Slower (lock on every call, even single-threaded) |
| Growth | +50% | +100% (doubles) |
| Iterator | Fail-fast | Fail-fast + legacy `Enumeration` |
| Introduced | Java 1.2 | Java 1.0 (legacy) |

**Why `Vector` is still not thread-safe for real workflows:**

```java
Vector<String> v = new Vector<>();
if (!v.contains("A")) { // thread 1 checks
 v.add("A"); // thread 2 may add "A" in between -> duplicate
}
```

Each method is synchronized, but the **check-then-act sequence** is not atomic. Method-level locking gives cost without true safety.

**Modern alternatives:**

```java
List<String> local = new ArrayList<>(); // single thread
List<String> simple = Collections.synchronizedList(new ArrayList<>()); // simple locking
List<String> readHeavy = new CopyOnWriteArrayList<>(); // many readers, rare writers
```

### Q7. What is CopyOnWriteArrayList and when should you use it?

**Definition:** `CopyOnWriteArrayList` is a thread-safe list where every write (`add`, `set`, `remove`) creates a **new copy** of the underlying array; reads use the current array without locking.

**Simple explanation:** A restaurant menu board. Customers read it all day. When the chef changes one dish, a new board is printed and swapped in. Customers already reading the old board are never disturbed.

```text
Reader threads -> read current array reference (no lock)
Writer thread -> lock -> copy array -> modify copy -> publish new reference (volatile)
Iterators -> work on the snapshot taken at creation -> never ConcurrentModificationException
```

```java
private final List<EventListener> listeners = new CopyOnWriteArrayList<>();

public void register(EventListener l) { listeners.add(l); } // rare, expensive
public void publish(Event e) { listeners.forEach(l -> l.onEvent(e)); } // frequent, lock-free
```

| Use it when | Avoid it when |
| --- | --- |
| Reads vastly outnumber writes | Frequent writes (each write is O(n) copy) |
| Safe iteration while others modify | Very large lists (copy cost + memory spikes) |
| Listener registries, feature-flag lists, route tables | Work queues, counters, logs |

**Real-time example:** An API gateway keeps a list of 200 allowed partner IP ranges. 5,000 requests per second read it; security updates it twice a day. `CopyOnWriteArrayList` gives lock-free reads and consistent snapshots. Using it for a list that receives 1,000 writes per second would copy the array 1,000 times per second and flood the garbage collector.

# Part 3: Set Interface — HashSet, LinkedHashSet, TreeSet

### Q8. How does HashSet work internally?

**Definition:** `HashSet` stores unique elements. Internally it is **backed by a `HashMap`**: every element becomes a key, and the value is a shared dummy object.

```java
// Simplified from JDK source
public class HashSet<E> {
 private transient HashMap<E, Object> map;
 private static final Object PRESENT = new Object();

 public boolean add(E e) { return map.put(e, PRESENT) == null; }
 public boolean contains(Object o) { return map.containsKey(o); }
 public boolean remove(Object o) { return map.remove(o) == PRESENT; }
}
```

**Simple explanation:** A guest list at a party. The doorman checks the name (hash -> which page of the list, then `equals()` -> exact name match). If the name is already there, the guest is not added again.

```text
add("Apple") -> hash -> bucket 5 -> empty -> store -> returns true
add("Apple") -> hash -> bucket 5 -> equals() match found -> returns false (duplicate)
```

**Custom objects must override both `equals()` and `hashCode()`:**

```java
public class Employee {
 private final int id;
 private final String name;

 @Override
 public boolean equals(Object o) {
 if (this == o) return true;
 if (!(o instanceof Employee e)) return false;
 return id == e.id;
 }

 @Override
 public int hashCode() {
 return Integer.hashCode(id);
 }
}

Set<Employee> employees = new HashSet<>();
employees.add(new Employee(1, "Alice"));
employees.add(new Employee(1, "Alice")); // duplicate, not added
System.out.println(employees.size()); // 1
```

**What if you override `equals()` but not `hashCode()`?**

```text
e1.equals(e2) -> true
e1.hashCode() -> 735 (identity-based default)
e2.hashCode() -> 982 (different)

set.add(e1) -> bucket 735 & 15 = 15
set.add(e2) -> bucket 982 & 15 = 6 <- different bucket, equals() is never even called
set.size() -> 2 <- WRONG: same employee stored twice
```

**Contract:** if `a.equals(b)` is `true`, then `a.hashCode() == b.hashCode()` **must** be true. Equal hash codes do not imply equal objects.

**Modern shortcut:** a Java `record` generates correct `equals()` and `hashCode()` automatically:

```java
record EmployeeId(int id) {}
Set<EmployeeId> ids = new HashSet<>();
```

**Real-time example:** A Kafka consumer receives the same `PolicyIssued` event twice after a rebalance. Keeping processed event IDs in a `HashSet<String>` for the current batch skips duplicates in O(1). (For duplicates across restarts or pods, the check must be in durable storage — see Appendix A36.)

### Q9. What is the difference between HashSet, LinkedHashSet, and TreeSet?

| Feature | HashSet | LinkedHashSet | TreeSet |
| --- | --- | --- | --- |
| Internal structure | `HashMap` | `LinkedHashMap` (hash table + doubly-linked list) | Red-black tree (`TreeMap`) |
| Ordering | None guaranteed | Insertion order | Sorted (natural or `Comparator`) |
| Null elements | One allowed | One allowed | Not allowed (natural ordering) |
| `add` / `remove` / `contains` | O(1) average | O(1) average | O(log n) |
| Memory | Low | Slightly higher (links) | Higher (tree nodes) |
| Uniqueness decided by | `hashCode()` + `equals()` | `hashCode()` + `equals()` | `compareTo()` / `compare()` |
| Best for | Fast membership check | Unique + keep arrival order | Unique + sorted + range queries |

```java
List<String> input = List.of("Banana", "Apple", "Cherry");

Set<String> hash = new HashSet<>(input); // e.g. [Apple, Cherry, Banana] — unpredictable
Set<String> linked = new LinkedHashSet<>(input); // [Banana, Apple, Cherry] — insertion order
Set<String> tree = new TreeSet<>(input); // [Apple, Banana, Cherry] — sorted
```

**Real-time example 1 — remove duplicate orders but keep arrival order:**

```java
List<Order> incoming = pollFromKafka(); // may contain redeliveries
Set<Order> unique = new LinkedHashSet<>(incoming); // Order must implement equals/hashCode
unique.forEach(orderProcessor::process); // processed in arrival order
```

**Real-time example 2 — leaderboard with a tie-breaker:**

```java
Comparator<Player> byScoreDesc = Comparator.comparingInt(Player::score).reversed()
 .thenComparing(Player::name); // without this, equal scores are dropped

TreeSet<Player> leaderboard = new TreeSet<>(byScoreDesc);
leaderboard.add(new Player("Alice", 950));
leaderboard.add(new Player("Bob", 1200));
leaderboard.add(new Player("Charlie", 950));
// Bob(1200) -> Alice(950) -> Charlie(950)
```

Without `thenComparing`, Charlie would be silently rejected because the comparator says he "equals" Alice (same score).

### Q10. How does TreeSet maintain sorted order internally?

**Definition:** `TreeSet` is backed by a `TreeMap`, which is a **red-black tree** — a self-balancing binary search tree.

**Simple explanation:** A dictionary: every word has a fixed alphabetical place. The tree keeps itself balanced so no path is much longer than another, therefore lookup is always about `log2(n)` steps (about 20 steps for 1 million elements).

Red-black tree rules:

1. Every node is RED or BLACK.
2. The root is BLACK.
3. A RED node cannot have a RED child.
4. Every root-to-leaf path has the same number of BLACK nodes.
5. Result: height ≤ 2 × log2(n + 1), so operations are O(log n).

```text
 [50 B]
 / \
 [30 R] [70 R]
 / \ / \
 [20 B] [40 B] [60 B] [80 B]
```

**`NavigableSet` methods — the real reason to choose `TreeSet`:**

```java
TreeSet<Integer> set = new TreeSet<>(List.of(20, 30, 40, 50, 60, 70));

set.first(); // 20 smallest
set.last(); // 70 largest
set.headSet(50); // [20, 30, 40] elements < 50
set.tailSet(50); // [50, 60, 70] elements >= 50
set.subSet(25, 65); // [30, 40, 50, 60] 25 <= x < 65
set.floor(45); // 40 greatest <= 45
set.ceiling(45); // 50 smallest >= 45
set.higher(50); // 60 strictly greater
set.pollFirst(); // removes and returns 20
set.descendingSet(); // [70, 60, 50, 40, 30]
```

**Real-time example — find the next available appointment slot:**

```java
TreeSet<LocalTime> freeSlots = new TreeSet<>(List.of(
 LocalTime.of(9, 0), LocalTime.of(10, 30), LocalTime.of(14, 0), LocalTime.of(16, 0)));

LocalTime requested = LocalTime.of(11, 0);
LocalTime next = freeSlots.ceiling(requested); // 14:00 — first free slot at or after 11:00

# Part 4: Map Interface — HashMap, LinkedHashMap, TreeMap, ConcurrentHashMap

### Q11. How does HashMap work internally? (Most asked question)

**Definition:** `HashMap` stores key-value pairs in an **array of buckets**. The key's hash code decides the bucket; `equals()` finds the exact key inside the bucket.

**Simple explanation:** A post office with 16 numbered pigeonholes. The postcode (hash) tells the clerk which pigeonhole; inside it, the clerk reads each name (`equals()`) until the right letter is found. When pigeonholes get too full, the office doubles the number of pigeonholes and re-sorts all letters (resize).

```java
// Simplified from JDK source
public class HashMap<K, V> {
Node<K, V>[] table; // buckets, default capacity 16 (created lazily on first put)
int size; // number of entries
int threshold; // capacity * loadFactor -> 16 * 0.75 = 12
 final float loadFactor; // default 0.75

 static class Node<K, V> {
 final int hash;
 final K key;
V value;
Node<K, V> next; // chain for collisions
 }
}
```

**Step-by-step: `map.put("name", "Alice")`**

```text
Step 1 h = "name".hashCode()
 hash = h ^ (h >>> 16) // mixes high bits into low bits -> fewer collisions

Step 2 index = hash & (capacity - 1) // same as hash % 16 but faster; needs power-of-2 capacity
 e.g. index = 12

Step 3 bucket 12:
 A. empty -> place new Node
 B. has nodes -> walk the chain:
 same hash AND key.equals(existing) -> replace value, return old value
 no match -> append new Node at tail
 C. chain length >= 8 AND table length >= 64
 -> convert chain to red-black tree (Java 8+), O(n) -> O(log n)
 (if table < 64, HashMap resizes instead of treeifying)

Step 4 if (++size > threshold) -> resize (double capacity)
```

```text
table[]
[0] -> null
[1] -> null
[2] -> ("age", 25) -> ("score", 99) <- collision chain
...
[12] -> ("name", "Alice")
...
[15] -> null
```

**Resize (rehash):**

```text
capacity 16, threshold 12 -> 13th entry triggers resize
1. new table of 32 buckets
2. each entry either stays at index i or moves to i + 16
 (decided by one extra bit of the hash, so no full re-hash is needed in Java 8+)
3. cost O(n) -> avoid by pre-sizing
```

**Real-time example — lookup table built once per batch:**

```java
// 200,000 policies loaded once, then looked up 2 million times while rendering emails
Map<String, Policy> policyByNumber = HashMap.newHashMap(policies.size()); // Java 19+, sized for N entries
for (Policy p : policies) {
 policyByNumber.put(p.number(), p);
}
Policy p = policyByNumber.get(notification.policyNumber()); // O(1) per lookup
```

Replacing a per-notification database query with this map turned 2 million queries into 1 query.

**Interview summary:**

> `HashMap` hashes the key, spreads the bits, picks a bucket with `hash & (n - 1)`, and uses `equals()` to find the key in that bucket. Collisions are chained and, from Java 8, a long chain becomes a red-black tree. When size exceeds capacity × 0.75 the table doubles. Average operations are O(1); worst case is O(log n) with treeified buckets. It is not thread-safe.

### Q12. What happens when two keys have the same hashCode in HashMap?

**Definition:** This is a **hash collision**. `HashMap` stores both entries in the same bucket (chaining) and tells them apart with `equals()`.

```text
put(key1, "v1") -> bucket 5 -> [key1|v1]
put(key2, "v2") -> bucket 5 -> [key1|v1] -> [key2|v2]

get(key2) -> bucket 5
 -> key1: same hash? yes; equals? no -> next
 -> key2: same hash? yes; equals? yes -> return "v2"
```

**Classic collision example:** `"Aa".hashCode() == "BB".hashCode()` (both 2112), yet they are different keys and both are stored.

```java
Map<String, Integer> map = new HashMap<>();
map.put("Aa", 1);
map.put("BB", 2);
System.out.println(map.size()); // 2
```

**Worst-case key design:**

```java
class BadKey {
 @Override public int hashCode() { return 42; } // every key in one bucket
 // equals() still correct
}
```

```text
Before Java 8: every lookup O(n)
Java 8+: bucket becomes a tree -> O(log n), faster if the key implements Comparable
```

**Real-time example:** Hash-flooding attacks sent thousands of HTTP parameters with colliding hash codes, making servers spend seconds per request in `HashMap` lookups. Java 8 treeification is one of the defences against this denial-of-service pattern.

### Q13. Why is the default capacity 16 and load factor 0.75?

**Capacity is a power of 2** so the bucket can be computed with a bitwise AND instead of the slower modulo:

```text
capacity = 16 -> capacity - 1 = 15 = 0b1111
index = hash & 0b1111 -> keeps the lowest 4 bits -> 0..15
```

If you pass `new HashMap<>(100)`, HashMap rounds the table up to the next power of 2 (128).

**Load factor 0.75** is a balance between memory and speed:

| Load factor | Effect |
| --- | --- |
| 0.5 | Fewer collisions, more empty buckets (memory waste) |
| 0.75 (default) | Good average; chains stay very short |
| 0.9-1.0 | Less memory, more collisions, slower lookups |

**Sizing rule:** to hold N entries without resizing, capacity ≈ N / 0.75.

```java
Map<String, User> a = new HashMap<>(1_000); // resizes when size > 768 (table rounds to 1024)
Map<String, User> b = new HashMap<>((int) (1_000 / 0.75f) + 1); // holds 1,000 without resizing
Map<String, User> c = HashMap.newHashMap(1_000); // Java 19+: does the calculation for you
```

**Real-time example:** A reconciliation job built a map of 5 million transactions with `new HashMap<>()`. It resized 19 times, and each resize allocated a new, larger array while the old one was still live, which caused two long GC pauses. Pre-sizing removed the resizes and reduced job time by ~15%.

### Q14. What is the difference between HashMap, LinkedHashMap, and TreeMap?

| Feature | HashMap | LinkedHashMap | TreeMap |
| --- | --- | --- | --- |
| Ordering | None | Insertion order (or access order) | Sorted by key |
| Internal | Array + chains/trees | HashMap + doubly-linked list | Red-black tree |
| `put` / `get` / `remove` | O(1) average | O(1) average | O(log n) |
| Null keys | One | One | Not allowed (natural ordering) |
| Extra API | — | `removeEldestEntry` (LRU) | `firstKey`, `floorKey`, `subMap`, `headMap` ... |
| Use case | General lookup | Predictable iteration, LRU cache | Sorted data, range queries |

```java
Map<String, Integer> hash = new HashMap<>();
Map<String, Integer> linked = new LinkedHashMap<>();
Map<String, Integer> tree = new TreeMap<>();

for (Map<String, Integer> m : List.of(hash, linked, tree)) {
 m.put("Charlie", 3);
 m.put("Alice", 1);
 m.put("Bob", 2);
}

System.out.println(hash); // e.g. {Bob=2, Alice=1, Charlie=3} no guaranteed order
System.out.println(linked); // {Charlie=3, Alice=1, Bob=2} insertion order
System.out.println(tree); // {Alice=1, Bob=2, Charlie=3} sorted
```

**Real-time example — `TreeMap` for tiered pricing (floor lookup):**

```java
// Discount by order total: 0+ -> 0%, 10,000+ -> 5%, 50,000+ -> 10%, 100,000+ -> 15%
NavigableMap<Integer, Integer> discountTiers = new TreeMap<>(Map.of(
0, 0, 10_000, 5, 50_000, 10, 100_000, 15));

int discount = discountTiers.floorEntry(64_500).getValue(); // 10 (%) — the 50,000 tier
```

The same pattern works for tax brackets, premium age bands, SLA levels and effective-dated configuration (`floorKey(today)`).

**Real-time example — `LinkedHashMap` for a stable API response:**

```java
Map<String, Object> body = new LinkedHashMap<>();
body.put("status", "OK");
body.put("count", results.size());
body.put("items", results);
// JSON field order stays: status, count, items — easier to read and stable for snapshot tests
```

### Q15. How to implement an LRU Cache using LinkedHashMap?

**Definition:** An LRU (Least Recently Used) cache holds a fixed number of entries and, when full, evicts the entry that was used longest ago.

**Simple explanation:** A small desk that fits three files. Every time you use a file you put it on top of the pile. When a fourth file arrives, the one at the bottom (least recently touched) goes back to the cabinet.

```java
public class LRUCache<K, V> extends LinkedHashMap<K, V> {

 private final int maxSize;

 public LRUCache(int maxSize) {
 super(16, 0.75f, true); // true = access order instead of insertion order
 this.maxSize = maxSize;
 }

 @Override
 protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
 return size() > maxSize; // called after every put
 }
}

LRUCache<String, String> cache = new LRUCache<>(3);
cache.put("A", "1");
cache.put("B", "2");
cache.put("C", "3");
cache.get("A"); // A becomes most recent
cache.put("D", "4"); // full -> evicts B
System.out.println(cache); // {C=3, A=1, D=4}
```

```text
put A, B, C HEAD(oldest) -> A <-> B <-> C <- TAIL(newest)
get A HEAD -> B <-> C <-> A <- TAIL
put D HEAD -> C <-> A <-> D <- TAIL (B evicted from head)
```

**Senior follow-ups:**

| Question | Answer |
| --- | --- |
| Is it thread-safe? | No. `get()` also mutates the order, so even concurrent reads need a lock. Wrap with `Collections.synchronizedMap` or use Caffeine. |
| Does it support expiry? | No TTL. Use Caffeine (`expireAfterWrite`) or Redis for time-based expiry. |
| Is it shared across pods? | No; each JVM has its own copy. Use Redis for a shared cache. |

**Real-time example:** A batch renders 300,000 emails using ~40 distinct templates stored in Blob Storage. An `LRUCache<String, Template>` of size 50 turns 300,000 downloads into ~40, while bounding memory if template IDs grow unexpectedly.

### Q16. What is ConcurrentHashMap and how does it work internally?

**The problem:**

| Option | Problem |
| --- | --- |
| `HashMap` | Not thread-safe: lost updates, corrupted structure (Java 7 resize could even create an infinite loop) |
| `Hashtable` / `Collections.synchronizedMap` | One lock for the whole map: only one thread at a time, reads block too |
| `ConcurrentHashMap` | Fine-grained locking + lock-free reads: many threads read and write simultaneously |

**Simple explanation:** A supermarket with one checkout (synchronizedMap) versus one checkout per aisle (ConcurrentHashMap). Customers in different aisles never wait for each other.

**Java 8+ internal design:**

```text
Node<K,V>[] table (like HashMap)
 - put into an EMPTY bucket -> CAS (compare-and-swap), no lock
 - put into a NON-EMPTY bucket -> synchronized on that bucket's first node only
 - get() -> no lock; volatile reads see the latest published value
 - resize -> multiple threads help transfer buckets in parallel
 - size() -> counter cells (like LongAdder), approximate under contention

(Java 7 used 16 "segments", each a separate lock; Java 8 replaced this with per-bucket locking.)
```

**Atomic operations — the real reason to use it:**

```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

map.putIfAbsent("counter", 0);
map.compute("counter", (k, v) -> v == null ? 1 : v + 1); // atomic read-modify-write
map.merge("counter", 1, Integer::sum); // atomic increment
map.computeIfAbsent("users", k -> new CopyOnWriteArrayList<>()); // atomic lazy init
```

| Feature | synchronizedMap | Hashtable | ConcurrentHashMap |
| --- | --- | --- | --- |
| Lock scope | Entire map | Entire map | Per bucket |
| Reads | Blocked by writes | Blocked by writes | Lock-free |
| Null keys/values | Depends on backing map | Not allowed | Not allowed |
| Compound atomic ops | No | No | Yes (`compute`, `merge`, ...) |
| Iterator | Fail-fast (needs external lock) | Fail-fast | Weakly consistent (no CME) |
| Performance under contention | Poor | Poor | Excellent |

**Real-time example — thread-safe word counter:**

```java
ConcurrentHashMap<String, LongAdder> wordCount = new ConcurrentHashMap<>();

void processWord(String word) {
 wordCount.computeIfAbsent(word, k -> new LongAdder()).increment();
}

// after all worker threads finish
wordCount.forEach((word, count) -> System.out.println(word + ": " + count.sum()));
```

**Real-time example — per-tenant rate-limiter registry:**

```java
private final ConcurrentHashMap<String, RateLimiter> limiters = new ConcurrentHashMap<>();

RateLimiter limiterFor(String tenantId) {
 return limiters.computeIfAbsent(tenantId, id -> RateLimiter.create(100)); // created exactly once per tenant
}
```

**Caution:** keep the function passed to `computeIfAbsent` short. It runs while the bucket is locked; a slow database call inside it blocks other threads hashing to the same bucket, and calling back into the same map from the function can deadlock or throw `IllegalStateException`.

### Q17. What is the difference between HashMap and Hashtable?

| Feature | HashMap | Hashtable |
| --- | --- | --- |
| Synchronized | No | Yes (every method) |
| Null keys / values | One null key, many null values | Neither allowed |
| Performance | Fast | Slower (lock on every call) |
| Iterator | Fail-fast | Fail-fast + legacy `Enumeration` |
| Introduced | Java 1.2 | Java 1.0 (legacy) |
| Superclass | `AbstractMap` | `Dictionary` (obsolete) |

**Bottom line:** do not use `Hashtable` in new code. Use `HashMap` for single-threaded or confined access and `ConcurrentHashMap` for shared concurrent access.

**Interview tip:** if asked *"Why does ConcurrentHashMap reject nulls when HashMap allows them?"* — in a concurrent map, `get(key) == null` would be ambiguous ("no mapping" or "mapped to null?"), and you cannot safely call `containsKey` afterwards because another thread may have changed the map in between.

# Part 5: Queue and Deque

### Q18. Explain the Queue and Deque interfaces with their implementations.

**Definition:** A `Queue` holds elements before processing, usually **FIFO** (first in, first out). A `Deque` (double-ended queue, pronounced "deck") allows insert and remove at **both ends**, so it can act as a queue or a stack.

**Simple explanation:** A queue is a ticket counter line — the first person in is served first. A deque is a line where people may join or leave at either end; used from one end only, it becomes a stack of plates (LIFO).

**Two method families — know this table:**

| Operation | Throws exception | Returns special value | Blocking (`BlockingQueue` only) |
| --- | --- | --- | --- |
| Insert | `add(e)` — `IllegalStateException` if full | `offer(e)` — `false` if full | `put(e)` waits for space |
| Remove head | `remove()` — `NoSuchElementException` if empty | `poll()` — `null` if empty | `take()` waits for an element |
| Examine head | `element()` — exception if empty | `peek()` — `null` if empty | — |

**Deque stack and queue methods:**

| Use as | Add | Remove | Peek |
| --- | --- | --- | --- |
| Queue (FIFO) | `offerLast` / `offer` | `pollFirst` / `poll` | `peekFirst` |
| Stack (LIFO) | `push` (= `addFirst`) | `pop` (= `removeFirst`) | `peek` |

```java
// FIFO queue
Queue<String> queue = new ArrayDeque<>();
queue.offer("Task 1");
queue.offer("Task 2");
queue.poll(); // "Task 1"

// Stack (use instead of legacy java.util.Stack)
Deque<String> stack = new ArrayDeque<>();
stack.push("A");
stack.push("B");
stack.pop(); // "B"

// Priority queue: smallest first
PriorityQueue<Integer> minHeap = new PriorityQueue<>(List.of(30, 10, 20));
minHeap.poll(); // 10

// Priority queue: largest first
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
```

**Implementations at a glance:**

| Implementation | Ordering | Nulls | Thread-safe | Bounded | Typical use |
| --- | --- | --- | --- | --- | --- |
| `ArrayDeque` | FIFO / LIFO | No | No | No (grows) | Default local queue or stack, BFS |
| `LinkedList` | FIFO / LIFO | Yes | No | No | When you also need `List` operations |
| `PriorityQueue` | By priority (heap) | No | No | No | Scheduling, top-K, Dijkstra |
| `ArrayBlockingQueue` | FIFO | No | Yes | Yes (fixed) | Producer-consumer with backpressure |
| `LinkedBlockingQueue` | FIFO | No | Yes | Optional | Executor work queues |
| `PriorityBlockingQueue` | By priority | No | Yes | No | Concurrent priority jobs |
| `DelayQueue` | By delay expiry | No | Yes | No | Retry-after, scheduled timeouts |
| `SynchronousQueue` | Direct hand-off, no storage | No | Yes | Capacity 0 | `Executors.newCachedThreadPool()` |
| `ConcurrentLinkedQueue` | FIFO, non-blocking | No | Yes (CAS) | No | High-throughput lock-free hand-off |

**Real-time use cases:**

```text
ArrayDeque -> BFS over an org chart, undo/redo stack, sliding-window algorithms
PriorityQueue -> send CRITICAL alerts before INFO emails; top 10 highest claims
ArrayBlockingQueue -> reader thread feeds 4 email-sender threads, max 1,000 items in memory
DelayQueue -> retry a failed email after 30s, 2 min, 10 min
LinkedBlockingQueue -> the work queue inside a ThreadPoolExecutor
```

**Interview tip:** prefer `offer`/`poll`/`peek` in production code when "full" or "empty" is a normal situation, and use the exception-throwing versions when it indicates a bug.

### Q19. How does PriorityQueue work internally?

**Definition:** `PriorityQueue` is an unbounded queue backed by a **binary min-heap stored in an array**. The head is always the smallest element according to natural order or the supplied `Comparator`.

**Simple explanation:** A hospital emergency room. Patients are not treated in arrival order; the most critical patient is always seen next. Nobody sorts the whole waiting room — the triage nurse only makes sure the most critical patient is at the front.

**Heap property:** every parent ≤ its children (min-heap). The array is **not** fully sorted.

```text
Array: [10, 20, 30, 25, 35]

 10 parent(i) = (i - 1) / 2
 / \ leftChild(i) = 2i + 1
 20 30 rightChild(i) = 2i + 2
 / \
 25 35
```

| Operation | Complexity | How |
| --- | --- | --- |
| `offer(e)` | O(log n) | Add at end, "sift up" while smaller than parent |
| `poll()` | O(log n) | Remove root, move last element to root, "sift down" |
| `peek()` | O(1) | Return index 0 |
| `remove(Object)` / `contains` | O(n) | Linear search |
| Iteration / `toString()` | O(n) | Heap order, **not** sorted order |

```java
PriorityQueue<Integer> pq = new PriorityQueue<>(List.of(30, 10, 20));
System.out.println(pq); // [10, 30, 20] <- heap layout, not sorted!
while (!pq.isEmpty()) {
 System.out.print(pq.poll() + " "); // 10 20 30 <- sorted only when polling
}
```

**Real-time example — alert dispatcher with priorities and FIFO tie-breaking:**

```java
record Alert(String message, int severity, long sequence) {} // lower severity number = more urgent

AtomicLong seq = new AtomicLong();
PriorityQueue<Alert> dispatcher = new PriorityQueue<>(
 Comparator.comparingInt(Alert::severity)
 .thenComparingLong(Alert::sequence)); // same severity -> first come, first served

dispatcher.offer(new Alert("Nightly backup finished", 5, seq.incrementAndGet()));
dispatcher.offer(new Alert("CRITICAL: payment service down", 1, seq.incrementAndGet()));
dispatcher.offer(new Alert("Deploy completed", 3, seq.incrementAndGet()));

while (!dispatcher.isEmpty()) {
 System.out.println(dispatcher.poll().message());
}
// CRITICAL: payment service down
// Deploy completed
// Nightly backup finished
```

**Comparator trap:** `(a, b) -> b - a` can overflow for large or negative values and give a wrong order. Use `Comparator.reverseOrder()` or `Integer.compare(b, a)`.

### Q20. What is the difference between ArrayDeque and LinkedList as a Queue/Stack?

| Feature | ArrayDeque | LinkedList |
| --- | --- | --- |
| Internal | Resizable circular array | Doubly-linked nodes |
| Memory | Low (no node objects) | High (one node object per element) |
| CPU cache | Friendly (contiguous) | Unfriendly (scattered) |
| Null elements | Not allowed | Allowed |
| Speed as queue/stack | Faster | Slower (allocation per add) |
| Also a `List`? | No | Yes |

The JDK Javadoc for `ArrayDeque` states: *"This class is likely to be faster than Stack when used as a stack, and faster than LinkedList when used as a queue."*

```java
Deque<String> stack = new ArrayDeque<>(); // instead of Stack<String>
Queue<String> queue = new ArrayDeque<>(); // instead of LinkedList<String>
```

**Why not `java.util.Stack`?** It extends `Vector` (synchronized on every call) and exposes `get(index)`, which breaks the stack abstraction.

**Real-time example — BFS to find everyone within 2 levels of a manager:**

```java
Set<String> withinTwoLevels(String managerId, Map<String, List<String>> reportsOf) {
Set<String> visited = new HashSet<>();
Deque<Map.Entry<String, Integer>> queue = new ArrayDeque<>();
 queue.offer(Map.entry(managerId, 0));

 while (!queue.isEmpty()) {
 var current = queue.poll();
 if (current.getValue() == 2) continue;
 for (String report : reportsOf.getOrDefault(current.getKey(), List.of())) {
 if (visited.add(report)) { // add() returns false if already seen
 queue.offer(Map.entry(report, current.getValue() + 1));
 }
 }
 }
 return visited;
}

 ### Q20A. What is a BlockingQueue? Implement producer-consumer.

**Definition:** A `BlockingQueue` is a thread-safe queue whose `put()` waits when the queue is full and whose `take()` waits when it is empty. It is the standard Java building block for producer-consumer designs.

**Simple explanation:** A conveyor belt between a baker and a packer. If the belt is full, the baker waits; if it is empty, the packer waits. Nobody needs to write `wait()`/`notify()` code.

```java
BlockingQueue<Notification> queue = new ArrayBlockingQueue<>(1_000); // bounded = backpressure
Notification POISON = new Notification("__STOP__");

// Producer: reads pending notifications from the database page by page
Runnable producer = () -> {
 try {
 for (Notification n : repository.streamPending()) {
 queue.put(n); // waits if 1,000 items are already queued
 }
 for (int i = 0; i < 4; i++) queue.put(POISON); // one stop signal per consumer
 } catch (InterruptedException e) {
 Thread.currentThread().interrupt();
 }
};

// Consumers: 4 threads sending emails
Runnable consumer = () -> {
 try {
 while (true) {
Notification n = queue.take(); // waits if empty
 if (n == POISON) break;
 emailSender.send(n);
 }
 } catch (InterruptedException e) {
 Thread.currentThread().interrupt();
 }
};
```

```text
Why bounded?
 Unbounded queue + slow mail provider -> producer keeps adding -> millions of objects in heap -> OutOfMemoryError
 Bounded queue -> producer slows to the consumers' speed -> memory stays flat
```

**Interview points:**

- Always restore the interrupt flag with `Thread.currentThread().interrupt()` when catching `InterruptedException`.
- Use a **poison pill** or an `ExecutorService` shutdown to stop consumers cleanly.
- Use `offer(e, timeout, unit)` when the producer must not block forever.

### Q20B. Which queue does a ThreadPoolExecutor use, and why does it matter?

| Factory method | Work queue | Risk |
| --- | --- | --- |
| `Executors.newFixedThreadPool(n)` | Unbounded `LinkedBlockingQueue` | Tasks pile up without limit -> memory exhaustion |
| `Executors.newCachedThreadPool()` | `SynchronousQueue` | Creates a new thread for every burst -> thousands of threads |
| `Executors.newSingleThreadExecutor()` | Unbounded `LinkedBlockingQueue` | Same as fixed pool |

**Production-safe configuration:**

```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
8, 16, // core and max threads
60, TimeUnit.SECONDS,
 new ArrayBlockingQueue<>(500), // bounded queue
 new ThreadPoolExecutor.CallerRunsPolicy()); // when full, the caller runs the task -> natural backpressure
```

**Real-time example:** An upload API used `newFixedThreadPool(10)`. When the virus-scan service slowed down, 400,000 tasks queued in memory and the pod was OOM-killed. A bounded queue with `CallerRunsPolicy` made request threads slow down instead, and the pod stayed healthy.

# Part 6: Iterators — Fail-Fast vs Fail-Safe

### Q21. What is fail-fast and fail-safe iteration?

**Definition:**

-**Fail-fast** iterators throw `ConcurrentModificationException` (CME) as soon as they detect that the collection was structurally modified outside the iterator.
-**Fail-safe** (more precisely *weakly consistent* or *snapshot*) iterators never throw CME; they iterate over a snapshot or tolerate concurrent changes.

**Simple explanation:** Fail-fast is a teacher counting students who shouts "stop!" if anyone leaves the room mid-count. Fail-safe is a teacher counting from a photo taken earlier — the count never breaks, but it may be slightly out of date.

**The classic bug:**

```java
List<String> list = new ArrayList<>(List.of("A", "B", "C"));
for (String s : list) {
 if (s.equals("B")) {
 list.remove(s); // ConcurrentModificationException (even in ONE thread)
 }
}
```

**How fail-fast works:**

```text
ArrayList keeps modCount (incremented on every structural change)
Iterator stores expectedModCount (copied when the iterator is created)
next() checks modCount == expectedModCount ? continue : throw CME
```

**Safe ways to remove while iterating:**

```java
// 1. Iterator.remove() keeps expectedModCount in sync
Iterator<String> it = list.iterator();
while (it.hasNext()) {
 if (it.next().equals("B")) it.remove();
}

// 2. Java 8+ (clearest)
list.removeIf(s -> s.equals("B"));

// 3. Build a new list
List<String> kept = list.stream().filter(s -> !s.equals("B")).toList();
```

**Fail-safe examples:**

```java
// ConcurrentHashMap — weakly consistent: no CME, may or may not see new entries
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>(Map.of("A", 1, "B", 2));
for (String key : map.keySet()) {
 map.put("C", 3); // allowed
}

// CopyOnWriteArrayList — snapshot: iterator never sees "D"
CopyOnWriteArrayList<String> cow = new CopyOnWriteArrayList<>(List.of("A", "B", "C"));
for (String s : cow) {
 cow.add("D"); // allowed; loop still runs 3 times
}
```

| Collection | Iterator type | Throws CME? | Sees concurrent changes? |
| --- | --- | --- | --- |
| `ArrayList`, `HashMap`, `HashSet`, `TreeMap` | Fail-fast | Yes (best effort) | — |
| `ConcurrentHashMap`, `ConcurrentLinkedQueue` | Weakly consistent | No | Maybe |
| `CopyOnWriteArrayList`, `CopyOnWriteArraySet` | Snapshot | No | No |

**Important:** fail-fast is *best-effort* detection, not a thread-safety mechanism. Never rely on CME to detect races in multi-threaded code.

**Real-time example:** A scheduled job iterated an in-memory `HashMap` of active sessions while HTTP threads added new sessions. It failed randomly with CME in production but never in tests (single-threaded). Switching to `ConcurrentHashMap` and using `entrySet().removeIf(e -> e.getValue().isExpired())` fixed it.

# Part 7: Comparable vs Comparator

### Q22. What is the difference between Comparable and Comparator?

**Definition:**

- `Comparable<T>` defines the **natural order** inside the class itself (`compareTo`).
- `Comparator<T>` defines an **external, pluggable order** (`compare`), so a class can be sorted in many ways.

**Simple explanation:** `Comparable` is the default order printed on the box ("sorted by employee ID"). `Comparator` is a different sorting instruction you hand over when needed ("sort by salary this time").

| Feature | Comparable | Comparator |
| --- | --- | --- |
| Package | `java.lang` | `java.util` |
| Method | `compareTo(T other)` | `compare(T a, T b)` |
| Where defined | Inside the class | Outside (lambda, method reference, class) |
| Number of orders | One natural order | Unlimited |
| Needs source change? | Yes | No — works with third-party classes |
| Used by | `Collections.sort(list)`, `TreeSet`, `TreeMap` default | `list.sort(cmp)`, `new TreeSet<>(cmp)` |

```java
// Comparable — natural order by ID
public class Employee implements Comparable<Employee> {
 private final int id;
 private final String name;
 private final double salary;

 @Override
 public int compareTo(Employee other) {
 return Integer.compare(this.id, other.id); // never use this.id - other.id (overflow)
 }
}

Collections.sort(employees); // by ID

// Comparator — any number of external orders
Comparator<Employee> byName = Comparator.comparing(Employee::getName);
Comparator<Employee> bySalaryDesc = Comparator.comparingDouble(Employee::getSalary).reversed();
Comparator<Employee> byDeptThenSalaryDesc = Comparator
 .comparing(Employee::getDepartment)
 .thenComparing(Employee::getSalary, Comparator.reverseOrder());

employees.sort(byDeptThenSalaryDesc);

// Null-safe: employees without a name go last
Comparator<Employee> nullSafeByName =
 Comparator.comparing(Employee::getName, Comparator.nullsLast(Comparator.naturalOrder()));
```

**Consistency with equals:** for `TreeSet`/`TreeMap`, if `compare(a, b) == 0` the collection treats `a` and `b` as the **same element**, even when `equals()` says otherwise. `BigDecimal` is a famous example: `new BigDecimal("1.0").equals(new BigDecimal("1.00"))` is `false`, but `compareTo` returns `0`, so a `HashSet` keeps both while a `TreeSet` keeps one.

**Real-time example — user-selected sort on an API:**

```java
private static final Map<String, Comparator<ClaimDto>> SORTS = Map.of(
"amount", Comparator.comparing(ClaimDto::amount),
"date", Comparator.comparing(ClaimDto::submittedAt),
"status", Comparator.comparing(ClaimDto::status));

public List<ClaimDto> sorted(List<ClaimDto> claims, String sortBy, boolean desc) {
Comparator<ClaimDto> cmp = SORTS.get(sortBy);
 if (cmp == null) throw new IllegalArgumentException("Unsupported sort: " + sortBy);
 cmp = cmp.thenComparing(ClaimDto::id); // deterministic tie-breaker
 return claims.stream().sorted(desc ? cmp.reversed() : cmp).toList();
}
```

The allowlist map prevents arbitrary input from choosing behaviour, and the ID tie-breaker keeps page order stable.

# Part 8: Java 8+ Collection Features

### Q23. What are the important Java 8+ enhancements to Collections?

| Java version | Feature | Example |
| --- | --- | --- |
| 8 | `forEach`, `removeIf`, `replaceAll` | `list.removeIf(Order::isCancelled)` |
| 8 | Stream API | `orders.stream().filter(...).map(...).toList()` |
| 8 | Map defaults: `getOrDefault`, `putIfAbsent`, `computeIfAbsent`, `merge`, `compute` | `counts.merge(word, 1, Integer::sum)` |
| 8 | `HashMap` treeification | Long collision chains become trees |
| 9 | Immutable factories `List.of`, `Set.of`, `Map.of`, `Map.ofEntries` | `Set.of("ADMIN", "USER")` |
| 10 | `List.copyOf`, `Collectors.toUnmodifiableList` | `List.copyOf(mutable)` |
| 16 | `Stream.toList()` | `stream.toList()` (unmodifiable) |
| 19 | `HashMap.newHashMap(n)` | Pre-sized for n entries |
| 21 | Sequenced collections: `getFirst`, `getLast`, `reversed` | `list.getLast()` |

**1. `forEach`, `removeIf`, `replaceAll`:**

```java
List<String> names = new ArrayList<>(List.of("alice", "bob", "charlie"));
names.forEach(System.out::println);
names.removeIf(n -> n.startsWith("b"));
names.replaceAll(String::toUpperCase); // [ALICE, CHARLIE]
```

**2. Streams — filter, transform, aggregate:**

```java
// Total revenue of completed orders (use BigDecimal for money)
BigDecimal revenue = orders.stream()
 .filter(o -> o.getStatus() == Status.COMPLETED)
 .map(Order::getAmount)
 .reduce(BigDecimal.ZERO, BigDecimal::add);

// Group by status
Map<Status, List<Order>> byStatus = orders.stream()
 .collect(Collectors.groupingBy(Order::getStatus));

// Count per status
Map<Status, Long> countByStatus = orders.stream()
 .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));

// Top 5 highest-value orders
List<Order> top5 = orders.stream()
 .sorted(Comparator.comparing(Order::getAmount).reversed())
 .limit(5)
 .toList();
```

**3. Map enhancements:**

```java
Map<String, List<String>> userRoles = new HashMap<>();
userRoles.computeIfAbsent("alice", k -> new ArrayList<>()).add("ADMIN"); // lazy init

Map<String, Integer> wordCount = new HashMap<>();
for (String word : words) {
 wordCount.merge(word, 1, Integer::sum); // increment or start at 1
}

int missing = wordCount.getOrDefault("missing", 0);
wordCount.replaceAll((word, count) -> count * 2);
```

**4. Immutable collections (Java 9+):**

```java
List<String> list = List.of("A", "B", "C");
Set<String> set = Set.of("A", "B", "C");
Map<String, Integer> map = Map.of("A", 1, "B", 2);

list.add("D"); // UnsupportedOperationException
List.of("A", null); // NullPointerException — nulls not allowed
Set.of("A", "A"); // IllegalArgumentException — duplicate element
```

**5. `toList()` (Java 16+):**

```java
List<String> a = stream.collect(Collectors.toList()); // mutable in practice, but no guarantee
List<String> b = stream.toList(); // guaranteed unmodifiable, allows nulls
List<String> c = stream.collect(Collectors.toUnmodifiableList()); // unmodifiable, rejects nulls
```

**Real-time example — building a daily batch summary:**

```java
Map<String, Map<DeliveryStatus, Long>> summary = auditRows.stream()
 .collect(Collectors.groupingBy(
 AuditRow::channel, // EMAIL, SMS
 TreeMap::new, // channels sorted in the report
 Collectors.groupingBy(AuditRow::status, () -> new EnumMap<>(DeliveryStatus.class),
 Collectors.counting())));
// {EMAIL={SENT=3101, FAILED=12}, SMS={SENT=880, FAILED=3}}
```

### Q24. What are the differences between stream() and parallelStream()?

**Definition:** `stream()` processes elements on the calling thread. `parallelStream()` splits the data and processes chunks on the shared **ForkJoinPool.commonPool()**, then combines results.

**Simple explanation:** One cashier versus several cashiers splitting a huge pile of receipts. Many cashiers only help if the pile is large and each receipt takes real effort; for five receipts, organising the team costs more than just doing the work.

```java
List<Integer> numbers = IntStream.rangeClosed(1, 1_000_000).boxed().toList();

long sequential = numbers.stream()
 .filter(n -> n % 2 == 0)
 .mapToLong(Integer::longValue)
 .sum();

long parallel = numbers.parallelStream()
 .filter(n -> n % 2 == 0)
 .mapToLong(Integer::longValue)
 .sum();
```

| Use `parallelStream()` when | Avoid it when |
| --- | --- |
| Large data (roughly 100K+ elements) | Small collections (overhead > gain) |
| CPU-heavy, independent work per element | Blocking I/O: DB, HTTP, files (starves the common pool) |
| Source splits well (`ArrayList`, arrays, `IntStream.range`) | Source splits badly (`LinkedList`, `Stream.iterate`) |
| No shared mutable state | Order-dependent logic, `forEach` with side effects |
| You measured a gain with a benchmark | Inside a web request handling thousands of concurrent users |

**Race condition trap:**

```java
// UNSAFE: ArrayList is not thread-safe -> lost elements or ArrayIndexOutOfBoundsException
List<Integer> results = new ArrayList<>();
numbers.parallelStream().filter(n -> n % 2 == 0).forEach(results::add);

// SAFE: let the collector combine per-thread results
List<Integer> safe = numbers.parallelStream().filter(n -> n % 2 == 0).toList();
```

**Real-time example:** An API endpoint used `parallelStream()` to call a pricing service for each item in a cart. Under load, all common-pool threads were blocked on HTTP calls, and unrelated `CompletableFuture` tasks in the same JVM stalled. The fix was a dedicated bounded executor with `CompletableFuture.supplyAsync(..., pricingExecutor)` and a timeout.

# Part 9: Real-World Scenario-Based Questions

### Q25. You need to store 10 million user records for fast lookup by user ID. Which collection and why?

**Answer:** `HashMap` for single-threaded or read-only use after loading; `ConcurrentHashMap` if many threads read and write.

**Why:**

- `get(userId)` is O(1) on average.
- To hold 10 million entries without resizing, capacity ≈ 10,000,000 / 0.75 ≈ 13.4 million (the table rounds up to 16,777,216 buckets).
- Pre-size to avoid ~20 resize copies while loading.

```java
Map<Long, User> users = HashMap.newHashMap(10_000_000); // Java 19+
Map<Long, User> usersJava8 = new HashMap<>(14_000_000); // older Java
Map<Long, User> concurrent = new ConcurrentHashMap<>(14_000_000); // multi-threaded
```

**Memory estimate (the part seniors are expected to raise):**

```text
Per entry (64-bit JVM, compressed oops, approximate):
 HashMap.Node ~32 bytes
 Long key object ~16 bytes
 bucket reference ~4-8 bytes
 User object depends on fields, say ~100 bytes
----------------------------------------------
~150 bytes x 10M = ~1.5 GB of heap
```

**Senior follow-ups:**

| Concern | Better option |
| --- | --- |
| Heap too large / long GC pauses | Keep only needed fields in a compact DTO; primitive-key maps (`Long2ObjectOpenHashMap` from fastutil) avoid `Long` boxing |
| Data shared by several pods | Redis or a database with an index — an in-memory map exists per JVM |
| Data changes frequently | A cache with TTL (Caffeine) instead of a static map |
| Survives restart | Not with an in-memory map; reload or use a persistent store |

### Q26. How would you find the first non-repeated character in a String using Collections?

```java
public char firstNonRepeated(String str) {
Map<Character, Integer> counts = new LinkedHashMap<>(); // keeps first-seen order
 for (char c : str.toCharArray()) {
 counts.merge(c, 1, Integer::sum);
 }
 return counts.entrySet().stream()
 .filter(e -> e.getValue() == 1)
 .map(Map.Entry::getKey)
 .findFirst()
 .orElseThrow(() -> new IllegalArgumentException("No non-repeated character"));
}

// "swiss" -> 'w'
```

**Why `LinkedHashMap`?** We need **frequency + first-seen order**. `HashMap` would count correctly but iterate in hash order, so "first" would be random.

**Complexity:** O(n) time, O(k) space (k = distinct characters).

**Follow-up — first repeated character:**

```java
Set<Character> seen = new HashSet<>();
for (char c : "programming".toCharArray()) {
 if (!seen.add(c)) return c; // add() returns false when already present -> 'r'
}
```

**Real-time variant:** "Find the first customer ID in today's log that appears exactly once" — the same `LinkedHashMap` counting pattern, applied to log lines.

### Q27. How do you make a collection read-only / immutable?

**Three levels — interviewers love the difference:**

| Level | API | Can the data still change? |
| --- | --- | --- |
| Unmodifiable **view** | `Collections.unmodifiableList(original)` | Yes — changes to `original` show through |
| Immutable **copy** | `List.copyOf(original)` (Java 10+) | No — independent snapshot |
| Immutable **from creation** | `List.of(...)`, `Set.of(...)`, `Map.of(...)` (Java 9+) | No |

```java
List<String> original = new ArrayList<>(List.of("A", "B"));

List<String> view = Collections.unmodifiableList(original);
view.add("C"); // UnsupportedOperationException
original.add("C"); // works — and view now shows [A, B, C]

List<String> copy = List.copyOf(original);
original.add("D"); // copy is still [A, B, C]
```

**Shallow, not deep:** an immutable list of mutable `Employee` objects still allows `list.get(0).setSalary(0)`. For true immutability, the elements must be immutable too (records with final fields).

**Real-time example — protecting internal state:**

```java
public class Order {
 private final List<OrderLine> lines = new ArrayList<>();

 public List<OrderLine> getLines() {
 return Collections.unmodifiableList(lines); // callers cannot add lines and bypass addLine() validation
 }

 public void addLine(OrderLine line) {
 if (lines.size() >= 100) throw new IllegalStateException("Max 100 lines");
 lines.add(line);
 }
}
```

### Q28. What is the difference between Iterator and ListIterator?

| Feature | Iterator | ListIterator |
| --- | --- | --- |
| Direction | Forward only | Forward and backward |
| Works on | Any `Collection` | Only `List` |
| Remove | `remove()` | `remove()` |
| Add | No | `add(e)` |
| Replace | No | `set(e)` |
| Index | No | `nextIndex()`, `previousIndex()` |

```java
List<String> list = new ArrayList<>(List.of("A", "B", "C", "D"));

ListIterator<String> it = list.listIterator(list.size()); // start at the end
while (it.hasPrevious()) {
 System.out.print(it.previous() + " "); // D C B A
}
```

**Real-time example — mask values in place:**

```java
ListIterator<String> it = cardNumbers.listIterator();
while (it.hasNext()) {
String card = it.next();
 it.set("**** **** **** " + card.substring(card.length() - 4)); // replace without index arithmetic
}
```

### Q29. What is an IdentityHashMap and when would you use it?

**Definition:** `IdentityHashMap` compares keys with **reference equality (`==`)** and `System.identityHashCode()`, instead of `equals()` and `hashCode()`.

```java
String s1 = new String("hello");
String s2 = new String("hello");

Map<String, Integer> hashMap = new HashMap<>();
hashMap.put(s1, 1);
hashMap.put(s2, 2);
System.out.println(hashMap.size()); // 1 — s1.equals(s2)

Map<String, Integer> identityMap = new IdentityHashMap<>();
identityMap.put(s1, 1);
identityMap.put(s2, 2);
System.out.println(identityMap.size()); // 2 — s1 != s2
```

**Use cases:** serialization and deep-copy frameworks (track objects already visited so cycles do not loop forever), object-graph traversal, proxies. It deliberately violates the general `Map` contract, so do not use it for business keys.

**Real-time example — deep copy with cycle detection:**

```java
Map<Object, Object> alreadyCopied = new IdentityHashMap<>();
// Two different Address objects with identical fields must stay two copies,
// and the same Address referenced twice must be copied once -> identity, not equals()

 ### Q30. What are EnumSet and EnumMap? Why are they special?

**Definition:**

- `EnumSet` is a `Set` of enum constants stored as a **bit vector** (a single `long` for up to 64 constants).
- `EnumMap` is a `Map` with enum keys stored in an **array indexed by `ordinal()`**.

**Simple explanation:** An `EnumSet` is a row of light switches — one bit per permission, on or off. Checking a permission is one bitwise operation.

```java
public enum Permission { READ, WRITE, EXECUTE, DELETE, ADMIN }

Set<Permission> userPerms = EnumSet.of(Permission.READ, Permission.WRITE);
Set<Permission> all = EnumSet.allOf(Permission.class);
Set<Permission> none = EnumSet.noneOf(Permission.class);
Set<Permission> dangerous = EnumSet.range(Permission.DELETE, Permission.ADMIN);

boolean canWrite = userPerms.contains(Permission.WRITE); // single bit check

Map<Permission, String> descriptions = new EnumMap<>(Permission.class);
descriptions.put(Permission.READ, "Can read resources");
descriptions.put(Permission.ADMIN, "Full access");
```

| Benefit | Why |
| --- | --- |
| Very fast | Bit operations / array index instead of hashing |
| Compact | No entry objects, no hash table |
| Ordered | Iterates in enum declaration order |
| Type-safe | Cannot mix constants of different enums |

**Real-time example — state machine transitions:**

```java
enum ClaimStatus { SUBMITTED, IN_REVIEW, APPROVED, REJECTED, PAID }

private static final Map<ClaimStatus, Set<ClaimStatus>> ALLOWED = new EnumMap<>(Map.of(
 ClaimStatus.SUBMITTED, EnumSet.of(ClaimStatus.IN_REVIEW),
 ClaimStatus.IN_REVIEW, EnumSet.of(ClaimStatus.APPROVED, ClaimStatus.REJECTED),
 ClaimStatus.APPROVED, EnumSet.of(ClaimStatus.PAID),
 ClaimStatus.REJECTED, EnumSet.noneOf(ClaimStatus.class),
 ClaimStatus.PAID, EnumSet.noneOf(ClaimStatus.class)));

void transition(Claim claim, ClaimStatus target) {
 if (!ALLOWED.get(claim.status()).contains(target)) {
 throw new IllegalStateException(claim.status() + " -> " + target + " not allowed");
 }
 claim.setStatus(target);
}
```

**PART 10: Tricky Interview Questions**
---------------------------------------

### **Q31. What happens if you use a mutable object as a HashMap key and then modify it?**

**List key = new ArrayList<>();**

**key.add("hello");**

**Map, String> map = new HashMap<>();**

**map.put(key, "value");**

**System.out.println(map.get(key));**_**// "value" — works fine**_

**key.add("world");**_**// MUTATE THE KEY!**_

**System.out.println(map.get(key));**_**// null! — KEY IS LOST FOREVER**_

**System.out.println(map.size());**_**// 1 — entry exists but unreachable**_

**Why? The hashCode changed after insertion. The entry sits in the old bucket, but the new hashCode points to a different bucket.Rule: HashMap keys should be immutable (String, Integer, records, etc.) or at minimum, hashCode() and equals() should depend only on immutable fields.**

### **Q32. What is the difference between remove() of Collection, Iterator, and Map?**

_**// Collection.remove(Object) — removes by value, uses equals()**_

**List list = new ArrayList<>(List.of(1, 2, 3));**

**list.remove(Integer.valueOf(2));**_**// removes element 2 (by value)**_

_**// GOTCHA with autoboxing!**_

**list.remove(1);**_**// removes element at INDEX 1, not value 1!**_

**list.remove(Integer.valueOf(1));**_**// removes value 1**_

_**// Iterator.remove() — removes current element during iteration (safe)**_

**Iterator it = list.iterator();**

**while (it.hasNext()) {**

**if (it.next() == 3) it.remove();**

**}**

_**// Map.remove(key) — removes entry by key**_

**Map map = new HashMap<>();**

**map.remove("key");**

_**// Map.remove(key, value) — conditional remove (Java 8+)**_

**map.remove("key", 42);**_**// only removes if key maps to 42**_

### **Q33. How does Collections.sort() work internally?**

**Java uses TimSort (hybrid of merge sort + insertion sort), invented by Tim Peters.How TimSort works:**

**1\. Divide data into "runs" (naturally sorted subsequences)**

**2\. If a run is too short (< minRun), extend it using INSERTION SORT**

**3\. Merge runs using MERGE SORT with galloping optimization**

**Example: \[3, 1, 2, 5, 8, 4, 6, 7\]**

**→ Detect runs: \[1, 2, 3, 5, 8\] and \[4, 6, 7\]**

**→ Merge: \[1, 2, 3, 4, 5, 6, 7, 8\]**

**Property**

**Value**

**Best case**

**O(n) — already sorted**

**Average**

**O(n log n)**

**Worst case**

**O(n log n)**

**Space**

**O(n)**

**Stable**

**Yes (equal elements maintain relative order)**

_**// For primitives, Java uses Dual-Pivot Quicksort (faster, not stable)**_

**int\[\] arr = {3, 1, 4, 1, 5};**

**Arrays.sort(arr);**_**// Dual-Pivot Quicksort**_

_**// For objects, Java uses TimSort (stable)**_

**String\[\] arr = {"Banana", "Apple", "Cherry"};**

**Arrays.sort(arr);**_**// TimSort**_

### **Q34. What is the difference between Collections.synchronizedMap() and ConcurrentHashMap?**

_**// synchronizedMap — wraps every method in synchronized(mutex)**_

**Map syncMap = Collections.synchronizedMap(new HashMap<>());**

_**// Problem: compound operations are NOT atomic**_

**if (!syncMap.containsKey("key")) {**_**// another thread can sneak in here!**_

**syncMap.put("key", "value");**_**// race condition!**_

**}**

_**// You must manually synchronize compound operations:**_

**synchronized (syncMap) {**

**if (!syncMap.containsKey("key")) {**

**syncMap.put("key", "value");**

**}**

**}**

_**// ConcurrentHashMap — built-in atomic compound operations**_

**ConcurrentHashMap concMap = new ConcurrentHashMap<>();**

**concMap.putIfAbsent("key", "value");**_**// atomic!**_

**concMap.computeIfAbsent("key", k -> expensiveComputation());**_**// atomic!**_

**Feature**

**synchronizedMap**

**ConcurrentHashMap**

**Lock granularity**

**Entire map**

**Per-bucket**

**Concurrent reads**

**Blocked**

**Lock-free**

**Atomic compound ops**

**No (manual sync needed)**

**Yes (putIfAbsent, compute, etc.)**

**Iterator**

**Fail-fast**

**Weakly consistent**

**Null keys/values**

**Depends on wrapped map**

**Not allowed**

### **Q35. How would you convert between different collection types?**

_**// List ↔ Set (remove duplicates)**_

**List list = List.of("A", "B", "A", "C", "B");**

**Set set = new LinkedHashSet<>(list);**_**// \[A, B, C\] — order preserved, deduped**_

**List deduped = new ArrayList<>(set);**_**// back to list**_

_**// List ↔ Array**_

**String\[\] array = list.toArray(new String\[0\]);**

**List fromArray = List.of(array);**_**// immutable**_

**List fromArray2 = new ArrayList<>(Arrays.asList(array));**_**// mutable**_

_**// Map → Set of entries, keys, values**_

**Map map = Map.of("A", 1, "B", 2, "C", 3);**

**Set keys = map.keySet();**

**Collection values = map.values();**

**Set\> entries = map.entrySet();**

_**// Two Lists → Map (Java 8+)**_

**List keys2 = List.of("name", "age", "city");**

**List values2 = List.of("Alice", "30", "NYC");**

**Map zipped = IntStream.range(0, keys2.size())**

**.boxed()**

**.collect(Collectors.toMap(keys2::get, values2::get));**

_**// {name=Alice, age=30, city=NYC}**_

_**// Stream to Map**_

**Map userMap = users.stream()**
## 1. You have 10 million records. You need fast random access by index. Which collection would you choose?

**Answer:** `ArrayList`.

```java
List<Employee> employees = new ArrayList<>();
Employee e = employees.get(5000000);
```

`ArrayList` internally uses a dynamically growing array.

Access by index:

```text
get(index) → O(1)
```

Adding at the end is generally:

```text
O(1) amortized
```

But inserting/removing in the middle is:

```text
O(n)
```

because elements may need to be shifted.

### Follow-up

**Interviewer:** Why not `LinkedList`?

**Answer:** `LinkedList` has O(1) insertion/removal when you already have the node/iterator, but finding the position is O(n). Also, its node-based structure has more memory overhead and poorer cache locality.

So for most application code, `ArrayList` is preferable.

---

# 2. Scenario: You frequently add and remove elements from both ends. Which collection?

**Answer:** `ArrayDeque`.

```java
Deque<String> queue = new ArrayDeque<>();

queue.addFirst("A");
queue.addLast("B");

queue.removeFirst();
queue.removeLast();
```

It is generally a better choice than `LinkedList` for deque/stack/queue operations.

### Follow-up

**Interviewer:** Can `ArrayDeque` contain `null`?

**Answer:** No.

```java
ArrayDeque<String> deque = new ArrayDeque<>();
deque.add(null); // NullPointerException
```

---

# 3. Scenario: You need to maintain unique employee IDs. Which collection?

**Answer:** `HashSet`.

```java
Set<Integer> employeeIds = new HashSet<>();

employeeIds.add(101);
employeeIds.add(102);
employeeIds.add(101);
```

Result:

```text
101
102
```

Duplicates are not allowed.

Average complexity:

```text
add() → O(1)
remove() → O(1)
contains() → O(1)
```

assuming a good hash distribution.

---

# 4. Scenario: You need employee IDs sorted automatically.

Use `TreeSet`.

```java
Set<Integer> ids = new TreeSet<>();

ids.add(105);
ids.add(101);
ids.add(103);

System.out.println(ids);
```

Output:

```text
[101, 103, 105]
```

`TreeSet` maintains sorted order.

Typical complexity:

```text
add() → O(log n)
remove() → O(log n)
contains() → O(log n)
```

---

# 5. Scenario: You need insertion order + uniqueness.

Use `LinkedHashSet`.

```java
Set<String> names = new LinkedHashSet<>();

names.add("John");
names.add("David");
names.add("John");
names.add("Alex");

System.out.println(names);
```

Output:

```text
[John, David, Alex]
```

Difference:

| Collection | Unique | Ordering |
| ------------- | ------ | ----------------------------- |
| HashSet | Yes | No guaranteed iteration order |
| LinkedHashSet | Yes | Insertion order |
| TreeSet | Yes | Sorted order |

---

# 6. Scenario: You need to store employee ID → Employee object.

Use `HashMap`.

```java
Map<Integer, Employee> employees = new HashMap<>();

employees.put(101, employee1);
employees.put(102, employee2);

Employee employee = employees.get(101);
```

Average:

```text
put() → O(1)
get() → O(1)
```

---

# 7. Scenario: You need sorted keys.

Use `TreeMap`.

```java
Map<Integer, Employee> employees = new TreeMap<>();

employees.put(105, e1);
employees.put(101, e2);
employees.put(103, e3);
```

Keys are maintained in sorted order:

```text
101
103
105
```

Typical operations:

```text
put() → O(log n)
get() → O(log n)
```

---
# 8. Scenario: You want insertion order while using a Map.

Use `LinkedHashMap`.

```java
Map<Integer, String> employees = new LinkedHashMap<>();

employees.put(101, "John");
employees.put(102, "David");
employees.put(103, "Alex");
```

Iteration follows insertion order.

### Senior-level follow-up

**Interviewer:** Can `LinkedHashMap` be used to implement an LRU cache?

**Answer:** Yes.

```java
LinkedHashMap<Integer, String> cache =
 new LinkedHashMap<>(16, 0.75f, true);
```

The third constructor parameter:

```java
accessOrder = true
```

causes recently accessed entries to move toward the end.

---

# 9. Scenario: Design an LRU Cache using Java Collections.

A common senior interview question.

```java
class LRUCache<K, V> extends LinkedHashMap<K, V> {

 private final int capacity;

 public LRUCache(int capacity) {
 super(16, 0.75f, true);
 this.capacity = capacity;
 }

 @Override
 protected boolean removeEldestEntry(
Map.Entry<K, V> eldest) {

 return size() > capacity;
 }
}
```

Usage:

```java
LRUCache<Integer, String> cache = new LRUCache<>(3);

cache.put(1, "A");
cache.put(2, "B");
cache.put(3, "C");

cache.get(1);

cache.put(4, "D");
```

Entry `2` becomes the least recently used and can be removed.

### Senior follow-up

**Interviewer:** Is this implementation thread-safe?

**Answer:** No.

If multiple threads access it, synchronization or a suitable concurrent cache implementation is required.

---

# 10. Scenario: Multiple threads access a HashMap. Is HashMap safe?

**Answer:** No.

`HashMap` is not thread-safe.

For concurrent access, depending on the requirement:

```java
ConcurrentHashMap<K,V>
```

is usually preferred.

```java
Map<Integer, String> map = new ConcurrentHashMap<>();

map.put(1, "John");
```

---

# 11. Scenario: Why not simply use `Collections.synchronizedMap()`?

You can:

```java
Map<Integer, String> map =
 Collections.synchronizedMap(new HashMap<>());
```

But the concurrency characteristics differ from `ConcurrentHashMap`.

For example, iteration requires external synchronization:

```java
synchronized (map) {
 for (String value : map.values()) {
 System.out.println(value);
 }
}
```

`ConcurrentHashMap` is designed specifically for concurrent access and generally provides better scalability.

---

# 12. Scenario: You need a Map where multiple threads frequently read and write.

**Answer:** `ConcurrentHashMap`.

```java
ConcurrentHashMap<String, Integer> map =
 new ConcurrentHashMap<>();
```

It supports concurrent operations without locking the entire map for ordinary operations.

### Important

`ConcurrentHashMap` does **not allow null keys or null values**.

```java
map.put(null, 100); // NullPointerException
```

---

# 13. Scenario: You need a thread-safe list.

Possible options include:

```java
CopyOnWriteArrayList
```

or:

```java
Collections.synchronizedList(new ArrayList<>())
```

But the choice depends on workload.

### `CopyOnWriteArrayList`

Best when:

```text
Reads >>> Writes
```

Every modification creates a new underlying array.

Example:

```java
List<String> users =
 new CopyOnWriteArrayList<>();
```

Excellent for relatively stable configuration/listener lists where reads are frequent.

Not suitable for extremely frequent writes.

---

# 14. Scenario: You have 100 readers and only 1 writer. Which List?

A strong answer is:

```java
CopyOnWriteArrayList
```

because it is optimized for read-heavy workloads.

Example:

```java
CopyOnWriteArrayList<String> list =
 new CopyOnWriteArrayList<>();
```

Readers can iterate without locking the collection in the same way a synchronized list requires.

---

# 15. Scenario: You need a blocking queue between producer and consumer threads.

Use:

```java
BlockingQueue
```

For example:

```java
BlockingQueue<String> queue =
 new LinkedBlockingQueue<>();
```

Producer:

```java
queue.put("Order-101");
```

Consumer:

```java
String order = queue.take();
```

This is a classic Producer-Consumer pattern.

---

# 16. Scenario: Queue capacity must be fixed.

Use:

```java
ArrayBlockingQueue
```

```java
BlockingQueue<String> queue =
 new ArrayBlockingQueue<>(100);
```

Now the queue has a fixed capacity.

This is useful for applying **backpressure**.

---

# 17. Scenario: You need priority-based processing.

Use:

```java
PriorityQueue
```

Example:

```java
PriorityQueue<Integer> queue =
 new PriorityQueue<>();

queue.add(50);
queue.add(10);
queue.add(30);

System.out.println(queue.poll());
```

Output:

```text
10
```

The smallest element has highest priority by default.

---

# 18. Scenario: You need to process highest salary first.

```java
PriorityQueue<Employee> queue =
 new PriorityQueue<>(
 Comparator.comparing(Employee::getSalary).reversed()
 );
```

Then:

```java
Employee employee = queue.poll();
```

returns the employee with the highest salary.

### Senior follow-up

**Interviewer:** Is PriorityQueue completely sorted?

**Answer:** No.

Only the head is guaranteed to have the highest/lowest priority.

Iterating over the queue does **not** guarantee sorted order.

---

# 19. Scenario: You need a collection that supports both stack and queue operations.

Use:

```java
Deque
```

Prefer:

```java
ArrayDeque
```

Example:

```java
Deque<Integer> deque = new ArrayDeque<>();

deque.addFirst(10);
deque.addLast(20);

deque.removeFirst();
deque.removeLast();
```

---

# 20. Scenario: Your HashMap is performing badly. What could be the reason?

Several possibilities:

### 1. Poor `hashCode()`

If many keys produce similar hash values, collisions increase.

### 2. Poor `equals()` / `hashCode()` implementation

For custom objects:

```java
class Employee {
int id;
}
```

If used as a HashMap key, implement `equals()` and `hashCode()` consistently.

### Rule

If:

```java
a.equals(b) == true
```

then:

```java
a.hashCode() == b.hashCode()
```

must be true.

The reverse is not required.

---

# 21. Scenario: Employee is used as a HashMap key. What happens if you change its ID after inserting it?

This is a classic senior-level trap.

```java
Map<Employee, String> map = new HashMap<>();

Employee e = new Employee(101);

map.put(e, "John");

e.setId(200);

map.get(e);
```

You may get:

```text
null
```

if `hashCode()` depends on `id`.

Why?

Because the object's hash code changed after insertion.

The map may look in a different bucket.

### Best practice

Use immutable objects as keys.

For example:

```java
record EmployeeKey(int id) {}
```

---

# 22. Scenario: Can a mutable object be a HashMap key?

Technically yes.

But it is dangerous if fields used by `equals()`/`hashCode()` are modified while the object is a key.

Prefer immutable keys.

---

# 23. Scenario: What happens internally when you call `HashMap.put()`?

Conceptually:

```java
map.put(key, value);
```

HashMap:

1. Calculates the key's hash.
2. Spreads/processes the hash.
3. Determines the bucket.
4. Checks existing entries.
5. Uses `equals()` to determine key equality when necessary.
6. Inserts or replaces the value.
7. May resize when the threshold is exceeded.

Modern Java implementations can use a tree structure for heavily-collided buckets, improving worst-case lookup behavior.

---

# 24. Scenario: Two different keys have the same hashCode. Can HashMap store both?

Yes.

Example:

```java
key1.hashCode() == key2.hashCode()
```

does **not** mean:

```java
key1.equals(key2)
```

must be true.

Hash collision is allowed.

HashMap uses `equals()` to distinguish keys within a collision.

---

# 25. Scenario: What happens if `equals()` is overridden but `hashCode()` isn't?

This violates the contract.

Example:

```java
class Employee {

 private int id;

 @Override
 public boolean equals(Object obj) {
 // implementation
 }
}
```

but no `hashCode()`.

Two logically equal objects may have different hash codes.

Then HashMap/HashSet may behave unexpectedly.

### Interview answer

Whenever overriding:

```java
equals()
```

also override:

```java
hashCode()
```

---

# 26. Scenario: You need to remove duplicates from a List.

Simple approach:

```java
List<Integer> list =
 Arrays.asList(10, 20, 10, 30, 20);

List<Integer> result =
 new ArrayList<>(new LinkedHashSet<>(list));
```

Result:

```text
[10, 20, 30]
```

`LinkedHashSet` preserves insertion order.

---
# 27. Scenario: Remove duplicates but order doesn't matter.

```java
List<Integer> result =
 new ArrayList<>(new HashSet<>(list));
```

But don't rely on the iteration order of `HashSet`.

---

# 28. Scenario: You need the first non-repeated character.

You can use `LinkedHashMap`.

```java
String input = "swiss";

Map<Character, Integer> map =
 new LinkedHashMap<>();

for (char c : input.toCharArray()) {
 map.put(c, map.getOrDefault(c, 0) + 1);
}

for (Map.Entry<Character, Integer> entry : map.entrySet()) {
 if (entry.getValue() == 1) {
 System.out.println(entry.getKey());
 break;
 }
}
```

Output:

```text
w
```

Why `LinkedHashMap`?

Because we need:

```text
frequency + insertion order
```

---

# 29. Scenario: Count word frequency.

```java
Map<String, Integer> frequency =
 new HashMap<>();

for (String word : words) {
 frequency.merge(word, 1, Integer::sum);
}
```

Or:

```java
frequency.put(
 word,
 frequency.getOrDefault(word, 0) + 1
);
```

---

# 30. Scenario: Group employees by department.

A senior Java developer should know `computeIfAbsent()`.

```java
Map<String, List<Employee>> employeesByDept =
 new HashMap<>();

for (Employee employee : employees) {

 employeesByDept
 .computeIfAbsent(
 employee.getDepartment(),
 k -> new ArrayList<>()
 )
 .add(employee);
}
```

Result conceptually:

```text
IT → [Employee1, Employee2]
HR → [Employee3]
Finance → [Employee4, Employee5]
```

---

# 31. Scenario: You need Map operations to be atomic.

Consider:

```java
if (!map.containsKey(key)) {
 map.put(key, value);
}
```

With multiple threads, this is not an atomic compound operation.

For `ConcurrentHashMap`, prefer operations such as:

```java
map.putIfAbsent(key, value);
```

or:

```java
map.computeIfAbsent(key, k -> createValue());
```

This is an important concurrency interview topic.

---

# 32. Scenario: You need a Map that automatically removes entries after garbage collection when keys are no longer strongly referenced.

Consider:

```java
WeakHashMap
```

Example:

```java
Map<Object, String> map =
 new WeakHashMap<>();
```

It can be useful for certain cache-like or metadata scenarios where keys should not prevent garbage collection.

### Senior follow-up

**Interviewer:** Is `WeakHashMap` a general-purpose cache?

**Answer:** No. It has specialized semantics based on weak references and garbage collection. For production caching, dedicated cache solutions are often more appropriate.

---

# 33. Scenario: You need sorted objects based on salary.

Use `TreeSet` with a `Comparator`.

```java
Set<Employee> employees =
 new TreeSet<>(
 Comparator.comparing(Employee::getSalary)
 );
```

### Important trap

If the comparator considers two employees equal:

```java
compare(e1, e2) == 0
```

`TreeSet` treats them as duplicates even if:

```java
e1.equals(e2) == false
```

This is a very good senior interview question.

---

# 34. Scenario: `TreeSet` contains an Employee but `contains()` returns false. Why?

Possible reason:

The ordering comparator is inconsistent with the way you're querying the objects.

`TreeSet` uses its ordering/comparator to determine whether an element exists rather than relying purely on `equals()`.

So always make sure the comparator correctly represents the identity/order requirements.

---

# 35. Scenario: You need top 10 highest salaries from 10 million employees.

Don't necessarily sort all 10 million employees.

Use a bounded `PriorityQueue`.

Conceptually:

```java
PriorityQueue<Employee> minHeap =
 new PriorityQueue<>(
 Comparator.comparing(Employee::getSalary)
 );

for (Employee employee : employees) {

 minHeap.offer(employee);

 if (minHeap.size() > 10) {
 minHeap.poll();
 }
}
```

Complexity:

```text
O(n log k)
```

where:

```text
n = number of employees
k = 10
```

instead of:

```text
O(n log n)
```

for sorting everything.

This is a **very strong senior-level question**.

---

# 36. Scenario: You have a List and need to check membership millions of times.

Current:

```java
List<Integer> ids;
```

If you repeatedly do:

```java
ids.contains(id);
```

it is O(n).

Convert to:

```java
Set<Integer> idSet =
 new HashSet<>(ids);
```

Now average membership lookup becomes:

```text
O(1)
```

This is a common real-world performance optimization.

---

# 37. Scenario: You need fast range queries.

For example:

```text
Find all employees with salary between 50,000 and 100,000.
```

A `TreeMap`/`TreeSet` can be useful because they maintain sorted order.

For example:

```java
NavigableMap<Integer, Employee> employees =
 new TreeMap<>();
```

Then APIs such as:

```java
subMap()
headMap()
tailMap()
```

can perform range-oriented operations.

---

# 38. Scenario: HashMap is storing millions of entries and resizing frequently. What can you do?

Estimate the required capacity and initialize the map appropriately.

```java
Map<Integer, Employee> map =
 new HashMap<>(1_000_000);
```

But remember the constructor's capacity interacts with the load factor and resize threshold; blindly choosing a number is not always optimal.

For large datasets, avoiding repeated resizing can improve performance.

---

# 39. Scenario: Why does HashMap resize?

Because the number of entries has crossed its threshold.

Conceptually:

```text
threshold = capacity × loadFactor
```

With the commonly used default load factor:

```text
0.75
```

the map resizes when it becomes sufficiently full.

Resizing involves redistributing entries into a larger table.

---

# 40. Scenario: You want a collection for a fixed number of elements that never changes.

You can use an immutable/unmodifiable collection depending on your requirements.

For example:

```java
List<String> names =
 List.of("John", "David", "Alex");
```

Attempting:

```java
names.add("Peter");
```

throws:

```text
UnsupportedOperationException
```

### Senior distinction

Don't confuse:

```java
Collections.unmodifiableList(list)
```

with:

```java
List.copyOf(list)
```

The former is an unmodifiable **view** over the original list; changes to the underlying list can be reflected through the view.

`List.copyOf()` creates an unmodifiable copy with its own backing state.

---

# 41. Scenario: You need a thread-safe sorted Map.

Use:

```java
ConcurrentSkipListMap
```

```java
ConcurrentSkipListMap<Integer, Employee> map =
 new ConcurrentSkipListMap<>();
```

It provides concurrent sorted-map behavior.

---

# 42. Scenario: You need a thread-safe sorted Set.

Use:

```java
ConcurrentSkipListSet
```

```java
Set<Integer> set =
 new ConcurrentSkipListSet<>();
```

Useful when you need both:

```text
concurrency + sorted ordering
```

---

# 43. Scenario: You need to process millions of elements. Should you always use streams instead of Collections?

No.

Streams and Collections solve different problems.

Collection:

```text
stores data
```

Stream:

```text
processes data
```

For example:

```java
List<Integer> numbers = ...
```

then:

```java
numbers.stream()
 .filter(n -> n > 100)
 .map(n -> n * 2)
 .toList();
```

The choice depends on readability, performance, parallelism, allocation, and workload.

---

# 44. Scenario: Why can `parallelStream()` make an application slower?

Because parallelism has overhead.

For a small collection:

```java
list.parallelStream()
```

may be slower than:

```java
list.stream()
```

because of:

* task splitting
* thread scheduling
* synchronization/coordination
* combining results
* common ForkJoinPool overhead
* memory/cache effects

Parallel streams should be benchmarked against the actual workload.

---

# 45. Scenario: You have a HashSet and want to preserve insertion order.

Change:

```java
HashSet
```

to:

```java
LinkedHashSet
```

Example:

```java
Set<String> set =
 new LinkedHashSet<>();
```

This is one of the most common collection design decisions.

---

# 46. Scenario: You have a HashMap and want insertion order.

Use:

```java
LinkedHashMap
```

```java
Map<Integer, Employee> map =
 new LinkedHashMap<>();
```

---
# 47. Scenario: You want sorted keys but also need efficient first/last/range operations.

Use:

```java
TreeMap
```

because it implements:

```java
NavigableMap
```

You can use:

```java
firstKey()
lastKey()
floorKey()
ceilingKey()
lowerKey()
higherKey()
subMap()
```

These APIs are particularly useful in senior-level system-design coding questions.

---

# 48. Scenario: What collection would you choose for an API response containing unique values while maintaining input order?

**Answer:**

```java
LinkedHashSet
```

Reason:

```text
unique + insertion order
```

---

# 49. Scenario: What collection would you choose for autocomplete suggestions?

Depending on requirements:

```text
TreeSet
```

can provide sorted values.

If prefix/range querying is important, a sorted structure can be useful:

```java
NavigableSet<String> suggestions =
 new TreeSet<>();
```

For very large-scale autocomplete, however, a trie/search index is often more appropriate than a Java collection alone.

---

# 50. Senior Scenario: Your application has this code

```java
Map<String, List<Order>> orders = new HashMap<>();

if (!orders.containsKey(customerId)) {
 orders.put(customerId, new ArrayList<>());
}

orders.get(customerId).add(order);
```

### How would you improve it?

Use:

```java
orders
 .computeIfAbsent(
 customerId,
 k -> new ArrayList<>()
 )
 .add(order);
```

This is cleaner and avoids repeating the lookup logic.

---

# 51. Senior Scenario: What happens here?

```java
Map<String, Integer> map = new HashMap<>();

map.put("A", 10);
map.put("A", 20);
```

Answer:

```text
A → 20
```

The second `put()` replaces the previous value.

But:

```java
map.putIfAbsent("A", 30);
```

doesn't replace `20`.

---
# 52. Scenario: You need to increment a counter in a ConcurrentHashMap.

A naive implementation:

```java
map.put(key, map.getOrDefault(key, 0) + 1);
```

is not an atomic compound operation.

Better:

```java
map.merge(key, 1, Integer::sum);
```

For example:

```java
ConcurrentHashMap<String, Integer> counts =
 new ConcurrentHashMap<>();

counts.merge("JAVA", 1, Integer::sum);
```

This is a very useful real-world concurrency pattern.

---

# 53. Scenario: You need a high-performance concurrent frequency counter.

For very high contention, another approach can be:

```java
ConcurrentHashMap<String, LongAdder>
```

Example:

```java
ConcurrentHashMap<String, LongAdder> counts =
 new ConcurrentHashMap<>();

counts.computeIfAbsent(
"JAVA",
 k -> new LongAdder()
).increment();
```

`LongAdder` can perform well under high contention for counters.

---

# 54. Scenario: How would you choose between these collections?

```text
ArrayList
LinkedList
HashSet
LinkedHashSet
TreeSet
HashMap
LinkedHashMap
TreeMap
ArrayDeque
PriorityQueue
ConcurrentHashMap
CopyOnWriteArrayList
```

A senior developer should think in terms of **requirements**, not memorization.

| Requirement | Choice |
| --------------------------- | ----------------------- |
| Fast indexed access | `ArrayList` |
| Unique elements | `HashSet` |
| Unique + insertion order | `LinkedHashSet` |
| Unique + sorted | `TreeSet` |
| Key-value lookup | `HashMap` |
| Key-value + insertion order | `LinkedHashMap` |
| Key-value + sorted keys | `TreeMap` |
| Stack/deque | `ArrayDeque` |
| Priority processing | `PriorityQueue` |
| Concurrent Map | `ConcurrentHashMap` |
| Read-heavy concurrent List | `CopyOnWriteArrayList` |
| Blocking producer-consumer | `BlockingQueue` |
| Concurrent sorted Map | `ConcurrentSkipListMap` |

---

# ⭐ Most Important Senior-Level Questions

If you're preparing specifically for a **Senior Java Developer interview**, I would prioritize these:

### Collections Internals

1. How does `HashMap` work internally?
2. What happens during `HashMap.put()`?
3. How are hash collisions handled?
4. Why are `equals()` and `hashCode()` important?
5. What happens when a HashMap resizes?
6. Why is HashMap not thread-safe?
7. How does `ConcurrentHashMap` work?
8. Why doesn't `ConcurrentHashMap` allow null?
9. HashMap vs Hashtable vs ConcurrentHashMap.
10. ArrayList vs LinkedList internally.

### Scenario/Performance

11. Find top K employees from millions of employees.
12. Remove duplicates while preserving order.
13. Optimize millions of `List.contains()` calls.
14. Implement an LRU cache.
15. Implement Producer-Consumer using BlockingQueue.
16. Implement a frequency counter.
17. Group objects by a property.
18. Implement a priority-based task processor.
19. Perform range queries.
20. Choose the correct collection for a high-volume application.

### Concurrency

21. `synchronizedList` vs `CopyOnWriteArrayList`.
22. `HashMap` vs `ConcurrentHashMap`.
23. `ConcurrentHashMap.computeIfAbsent()`.
24. Atomic updates with `merge()`.
25. `ConcurrentSkipListMap`.
26. BlockingQueue scenarios.
27. WeakHashMap.
28. Fail-fast vs weakly consistent iterators.
29. Iterator behavior during concurrent modification.
30. Collection immutability vs unmodifiable views.

### Coding Scenarios

31. First non-repeated character.
32. First repeated character.
33. Frequency of words.
34. Group employees by department.
35. Find duplicate objects.
36. Find top 3 salaries.
37. Find top K elements efficiently.
38. Sort Map by value.
39. Convert List → Map safely when duplicate keys exist.
40. Implement LRU cache.

**For a Senior interview, don't just answer "use HashMap." Explain *why*, the expected complexity, memory implications, thread-safety, ordering requirements, and what happens under high load.** That's usually what separates a senior-level answer from a basic Collections answer.

---

# Production and Current-Industry Appendix

The complete supplied Java Collections interview material above is preserved. This appendix adds modern Java guidance and production context so that each collection choice can be explained in terms of correctness, memory, concurrency, observability and operational risk.

# Appendix A1. The Collection Choice Decision Tree

Start with the requirement, not the class name:

```text
What data shape do you need?
 |
 +-- key -> value -------------------- Map
 +-- unique values ------------------- Set
 +-- ordered/repeated values --------- List
 +-- producer/consumer flow ---------- Queue or BlockingQueue
 +-- double-ended flow --------------- Deque
 `-- enum keys/values ---------------- EnumMap or EnumSet

Then ask:
 |
 +-- Must insertion or sorted order be preserved?
 +-- Are reads and writes concurrent?
 +-- Is the data bounded?
 +-- Is random access required?
 +-- Is the collection owned or merely viewed?
 +-- What is the expected size and access pattern?
 `-- What happens when the collection is full or empty?
```

Examples:

```text
Unique feature flags -> EnumSet or Set
User id lookup -> HashMap or ConcurrentHashMap
Stable API response order -> List or LinkedHashMap
Priority work scheduling -> PriorityBlockingQueue
FIFO producer/consumer -> ArrayBlockingQueue or LinkedBlockingQueue
Recent-access cache -> bounded cache with eviction policy
```

The best senior answer states the invariant first: uniqueness, ordering, boundedness, thread safety, or lookup complexity.

# Appendix A2. Modern Java Collection Vocabulary

In current Java versions, distinguish these terms precisely:

```text
Mutable -> the object can be changed
Immutable -> the object itself cannot be changed after construction
Unmodifiable -> this reference cannot mutate; the backing object may change
Snapshot -> an independent copy at a point in time
View -> a live projection backed by another collection
Thread-safe -> concurrent access follows the class's documented guarantees
```

Examples:

```java
List<String> mutable = new ArrayList<>(List.of("A", "B"));
List<String> unmodifiableView = Collections.unmodifiableList(mutable);
List<String> immutableSnapshot = List.copyOf(mutable);

mutable.add("C");
// unmodifiableView now observes C; immutableSnapshot does not.
```

Do not describe `Collections.unmodifiableList` as a deep immutable copy. It is a read-only wrapper around the original list.

# Appendix A3. Choosing `List`, `Set` and `Map` as API Types

Declare the narrowest interface that communicates the contract:

```java
public List<Order> findOrders(CustomerId customerId) {
 return repository.findByCustomerId(customerId);
}
```

The caller needs list semantics, not the implementation detail that the result currently uses `ArrayList`.

Return immutable or unmodifiable results when callers must not alter service-owned state:

```java
public List<String> supportedRegions() {
 return List.of("US", "CA", "GB");
}
```

For a dynamically built result:

```java
return List.copyOf(regions);
```

This prevents callers from accidentally mutating internal state and makes ownership easier to reason about.

# Appendix A4. `ArrayList` Capacity and Memory

`ArrayList` provides fast indexed access because it stores references in a contiguous backing array. Its practical strengths are cache locality and low per-element overhead compared with node-based structures.

```java
List<Order> orders = new ArrayList<>(expectedOrderCount);
```

Pre-sizing is useful when the approximate size is known because it reduces growth copies. It is not a reason to allocate an enormous capacity without evidence. Oversized arrays retain memory and can increase garbage-collection pressure.

Use `ArrayList` when:

- Reads by index are common.
- Iteration is common.
- Appends dominate mutations.
- The collection is local to one thread or externally synchronized.

Use a `Deque` or queue abstraction for queue operations rather than repeatedly inserting at index zero.

# Appendix A5. Why `LinkedList` Is Usually Not the Default

Although linked-list insertion can be constant time after a node is known, an application usually first has to find the position. That traversal is linear. Each node also carries link fields and is separately allocated, which hurts memory locality.

Prefer:

```java
Deque<Task> tasks = new ArrayDeque<>();
```

for an in-memory stack or deque. Prefer `ArrayList` for most general-purpose lists. Choose `LinkedList` only when its node and deque semantics are actually useful and measurements support it.

# Appendix A6. `ArrayDeque` for Local Queues and Stacks

`ArrayDeque` is commonly the best general-purpose non-concurrent deque:

```java
Deque<String> stack = new ArrayDeque<>();
stack.push("first");
String item = stack.pop();

Deque<String> queue = new ArrayDeque<>();
queue.addLast("first");
String next = queue.removeFirst();
```

It does not accept `null`, which removes ambiguity between an empty result and a stored null element. It is not a replacement for a blocking queue when threads need to wait for work.

# Appendix A7. Ordered Collections and API Determinism

Order is part of correctness when consumers compare output, generate reports, paginate results or sign payloads.

```text
HashMap -> no order contract
LinkedHashMap -> predictable insertion/access order
TreeMap -> comparator/key sorted order
HashSet -> no order contract
LinkedHashSet -> insertion order
TreeSet -> sorted order
```

Never rely on the current iteration order of `HashMap` or `HashSet`, even if tests happen to pass repeatedly. If output order matters, encode it in the type or sort explicitly.

# Appendix A8. Equality and Hashing Contract

For a key or set element:

```text
If a.equals(b) is true, a.hashCode() == b.hashCode() must be true.
```

The reverse is not required: different objects may share a hash code. A correct `equals`/`hashCode` implementation must also be:

- Consistent while the relevant state remains unchanged.
- Symmetric.
- Transitive.
- Non-null-safe according to the method contract.

Prefer immutable key state. Java records provide value-based `equals` and `hashCode` for their components, which makes them useful for many value keys:

```java
public record CustomerKey(String tenantId, long customerId) {
}

Map<CustomerKey, Customer> customers = new HashMap<>();
```

Do not use fields that can change while an object is stored as a map key or set element.

# Appendix A9. Mutable Keys: A Production Failure

This code is dangerous:

```java
final class UserKey {
 private String username;

 // equals and hashCode use username
}

Map<UserKey, String> sessions = new HashMap<>();
UserKey key = new UserKey("alice");
sessions.put(key, "session-1");
key.setUsername("bob");
```

After mutation, the map entry may remain in the bucket selected by the old hash code while lookup uses the new hash code. The entry appears to have disappeared. The map was not corrupted by randomness; the key violated the map's contract.

Safer alternatives:

```text
Use String, UUID, Integer or a record key
Copy mutable input into an immutable value
Remove and reinsert after a key change
Keep mutable state out of equality/hash fields
# Appendix A10. `HashMap` Capacity and Load

When sizing a hash map, reason about expected entries, load factor, memory and growth cost:

```java
Map<String, User> users = new HashMap<>(expectedUsers);
```

The constructor's capacity is an implementation input, not a guarantee that exactly that many entries fit without resizing. Account for the load factor and the implementation's table sizing. Modern JDK internals are implementation details; do not build application correctness around bucket counts or treeification thresholds.

For very large maps, measure:

```text
Entry count
Key/value object size
Table and node overhead
Resize pauses/copying
Heap occupancy
GC pause and allocation rate
```

# Appendix A11. `TreeMap` and Comparator Correctness

`TreeMap` uses ordering to locate keys. If a comparator returns zero for two values, the map treats them as the same key even if `equals` says otherwise.

```java
Comparator<Employee> byDepartmentThenId =
 Comparator.comparing(Employee::department)
 .thenComparingLong(Employee::id);
```

A comparator must be transitive and consistent enough for the collection's intended semantics. Never subtract integers to compare because overflow can produce incorrect ordering:

```java
// Avoid: return a.getPriority() - b.getPriority();
Comparator<Task> safe = Comparator.comparingInt(Task::priority);
```

# Appendix A12. `EnumSet` and `EnumMap`

When the domain key is an enum, use the specialized collection where it fits:

```java
enum Permission { READ, WRITE, DELETE }

EnumSet<Permission> permissions = EnumSet.of(Permission.READ, Permission.WRITE);
EnumMap<Permission, String> descriptions = new EnumMap<>(Permission.class);
```

They communicate that keys or values come from a fixed enum domain and can be more compact and efficient than general-purpose hash collections. They do not replace a database or distributed permission store.

# Appendix A13. Immutable Construction Patterns

Use factory methods for small fixed collections:

```java
Set<String> roles = Set.of("READER", "WRITER");
Map<String, Integer> limits = Map.of(
"standard", 100,
"premium", 1000);
```

For a conditional builder, construct a mutable collection locally and publish a snapshot:

```java
List<String> result = new ArrayList<>();
if (includePrimary) {
 result.add("primary");
}
if (includeBackup) {
 result.add("backup");
}
return List.copyOf(result);
```

This creates a clear ownership boundary. Remember that collection immutability is shallow: referenced elements can still be mutable.

# Appendix A14. Null Policy

Nulls make collection APIs harder to reason about:

```text
Does null mean missing?
Does null mean an explicit value?
Can a key be null but not a value?
Will a stream operation throw later?
```

Establish a domain policy:

- Reject null with validation where it is invalid.
- Use `Optional` for a single possibly absent return value, not as every collection element or field by default.
- Use empty collections for “no results” when that is the API contract.
- Document whether a map permits null keys or values.

`ConcurrentHashMap` deliberately does not allow null keys or values because a null result would be ambiguous with the absence of a mapping during concurrent operations.

# Appendix A15. Streams: Readability Before Cleverness

Streams are useful for declarative transformations:

```java
Map<String, BigDecimal> totalsByCustomer = orders.stream()
 .collect(Collectors.groupingBy(
 Order::customerId,
 Collectors.mapping(
 Order::total,
 Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
```

For production code, review:

```text
Does the pipeline express the business rule clearly?
Can it handle nulls and empty input?
Is the collection size bounded?
Does it preserve required ordering?
Is the collector safe for parallel execution?
Would a loop be easier to debug?
```

Do not use streams to hide multiple database calls, network calls or unbounded memory accumulation.

# Appendix A16. `toList()` and Collector Mutability

Be precise about result ownership:

```java
List<String> result = stream.toList();
```

The result is unmodifiable according to the Stream API contract. If a mutable `ArrayList` is required by the surrounding code:

```java
List<String> mutable = stream.collect(Collectors.toCollection(ArrayList::new));
```

For an immutable snapshot of an existing collection:

```java
List<String> snapshot = List.copyOf(existing);
```

Choose intentionally instead of relying on a particular JDK implementation class.

# Appendix A17. Parallel Streams and Shared State

This is unsafe:

```java
List<Result> results = new ArrayList<>();
input.parallelStream().forEach(item -> results.add(process(item)));
```

The `ArrayList` is not safe for concurrent mutation. Prefer a collector designed for the stream or, better, a sequential pipeline if the work is not a measured CPU-bound operation:

```java
List<Result> results = input.parallelStream()
 .map(this::process)
 .toList();
```

Parallel streams use the common fork-join pool by default. Blocking database or HTTP work can starve unrelated tasks. Use explicit executors and bounded concurrency for blocking workloads.

# Appendix A18. `ConcurrentHashMap` Compound Operations

A thread-safe map does not automatically make a multi-step workflow atomic:

```java
if (!map.containsKey(key)) {
 map.put(key, value);
}
```

Two threads can both observe absence. Use an atomic map operation:

```java
map.putIfAbsent(key, value);

map.computeIfAbsent(key, ignored -> new AtomicLong())
 .incrementAndGet();

map.merge(word, 1L, Long::sum);
```

Keep mapping functions short and side-effect aware. A mapping function may execute during a map operation and can create contention or recursion problems if it performs unrelated work.

# Appendix A19. Choosing a Concurrent Collection

```text
ConcurrentHashMap -> concurrent key/value lookup and updates
CopyOnWriteArrayList -> many reads, very few writes, snapshot iteration
BlockingQueue -> producer/consumer handoff with waiting
ConcurrentLinkedQueue -> non-blocking concurrent queue
ConcurrentSkipListMap -> concurrent sorted map requirements
Collections.synchronized* -> simple monitor wrapper for small/simple cases
```

Do not choose a concurrent collection merely because the application uses multiple threads. First identify ownership and the operation that must be atomic. A single-threaded collection behind a message partition or actor may be both faster and easier to reason about.

# Appendix A20. Safe Publication and Collection Ownership

Thread safety includes how a collection becomes visible, not just which class is used.

```text
Construct fully
 -> publish through a safe mechanism
 -> never mutate without the documented synchronization policy
```

Safer patterns include:

- Store immutable snapshots in a `volatile` field.
- Publish through a thread-safe queue or concurrent map.
- Guard both reads and writes with the same lock.
- Use an actor/executor that owns all mutations.

Wrapping an already-mutating `ArrayList` with `Collections.unmodifiableList` protects only the wrapper's mutation methods. It does not make concurrent backing-list mutation safe.

# Appendix A21. `CopyOnWriteArrayList` Trade-Off

`CopyOnWriteArrayList` is suitable when reads vastly outnumber writes and readers can use snapshots:

```java
private final CopyOnWriteArrayList<Listener> listeners =
 new CopyOnWriteArrayList<>();
```

Every structural write copies the backing array. Therefore it is useful for listener registries or configuration snapshots, but dangerous for frequent writes or very large lists. It also does not make the listener objects themselves thread-safe.

# Appendix A22. Bounded Queues and Backpressure

An unbounded queue can turn a downstream outage into an out-of-memory failure. Prefer a bounded queue when work arrival can exceed processing capacity:

```java
BlockingQueue<Task> queue = new ArrayBlockingQueue<>(10_000);
```

When full, define the policy:

```text
Reject immediately
Block briefly
Drop oldest or newest according to business rules
Persist to durable messaging
Apply upstream rate limiting
```

Never silently drop business-critical work. Record queue depth, enqueue latency, rejection count, processing age and oldest item age.

# Appendix A23. LRU Cache: Local Correctness Is Not Distributed Correctness

An access-ordered `LinkedHashMap` can implement a local bounded cache, but production design also needs:

```text
Maximum entries or weight
Expiration policy
Concurrent access policy
Negative-cache policy
Stampede protection
Invalidation strategy
Metrics
```

For multi-instance applications, each process has a different local cache. Use Caffeine for a mature in-process cache when appropriate, and use Redis or another distributed cache only when its consistency, latency and failure model are acceptable.

A cache is an optimization, not the source of truth. Decide what happens when it is empty, stale, unavailable or polluted.

# Appendix A24. Cache Stampede and Single-Flight Loading

If many requests observe the same expired entry, they can all call the database simultaneously:

```text
One popular key expires
 -> 1,000 requests miss
 -> 1,000 database loads
 -> database saturation
```

Mitigations include:

- Jittered expiration.
- Background refresh.
- Request coalescing/single-flight loading.
- Bounded loader concurrency.
- Stale-while-revalidate where acceptable.
- A circuit breaker around the source.

Measure hit rate and load latency; a cache with a high hit rate but stale or incorrect values is not a successful design.

# Appendix A25. Memory and Garbage Collection Review

For a large collection, count more than element values:

```text
Collection object
Backing array/table
Entry or node objects
Key objects
Value objects
Object headers and references
Temporary copies during resize or stream collection
```

Common memory failures include:

- Reading a whole file or result set into a list when streaming is possible.
- Keeping a request-scoped map in a singleton.
- Caching without bounds or expiry.
- Creating multiple copies during mapping and sorting.
- Accumulating failed messages indefinitely.

Use heap dumps, allocation profiling, collection-size metrics and realistic load tests. Do not infer memory behaviour from Big-O notation alone.

# Appendix A26. Collection Size Is a Capacity Contract

For every long-lived collection, answer:

```text
What is the maximum size?
Who removes entries?
What is the expiry policy?
What happens when it is full?
How is size observed?
What happens after restart?
```

An in-memory map without these answers is often an accidental cache, queue or database. Make that choice explicit.

# Appendix A27. Database and ORM Boundaries

Do not automatically convert every database result into a large in-memory collection. For large datasets:

```text
Paginate by a stable key
Stream with a bounded fetch size
Use a projection
Aggregate in the database
Process in bounded batches
```

For pagination, keyset pagination is often more stable than a very large offset:

```sql
SELECT id, created_at, status
FROM orders
WHERE (created_at, id) > (:lastCreatedAt, :lastId)
ORDER BY created_at, id
FETCH FIRST 100 ROWS ONLY;
```

The exact syntax depends on the database. The important design point is to use a deterministic, indexed continuation key.

# Appendix A28. Serialization and Collection Contracts

Collection choice can change serialized API behaviour:

```text
List -> ordered JSON array
Set -> uniqueness intent, but ordering must still be defined if clients care
Map -> JSON object with key restrictions
TreeMap -> deterministic key order if that is part of the contract
```

Do not expose an internal `HashSet` and promise stable response order. Convert to an explicitly ordered DTO when the wire contract requires deterministic output.

Also define how duplicate input values, nulls, missing keys and empty collections are represented. Contract tests should assert the actual JSON shape.

# Appendix A29. Security and Data Exposure

Collections often aggregate sensitive data. Review:

- Whether a map key exposes an account, tenant or internal identifier.
- Whether debug logging prints complete lists or maps.
- Whether an exception includes collection contents.
- Whether cached values cross tenant boundaries.
- Whether a collection can be controlled by untrusted input.
- Whether a user can request an unbounded page size or group cardinality.

Bound input sizes and authorize data before aggregation. Never assume that an in-memory collection is private if it is logged, serialized, cached or exposed through metrics.

# Appendix A30. Observability for Collection-Heavy Code

Useful metrics include:

```text
Collection size distribution
Queue depth and oldest item age
Cache hit/miss/eviction rate
Map load or lookup latency
Batch size and processing time
Rejected work count
Deduplication count
Memory and GC pressure
```

Avoid high-cardinality metric labels such as raw user IDs or arbitrary map keys. Use bounded categories, sampled logs and trace attributes that respect privacy requirements.

# Appendix A31. Testing Collection Behaviour

Test the contract, not only the happy-path implementation:

```text
Empty input
One element
Duplicate values
Duplicate keys
Null policy
Very large input
Required order
Unmodifiable result
Concurrent updates
Mutable-key regression
Queue full and empty behaviour
Cache expiry and eviction
```

Property-based tests are useful for equality and ordering invariants:

```text
Comparator ordering is transitive
Set size equals the number of distinct values
Map lookup returns the value for every inserted immutable key
Encode/decode preserves collection semantics
```

For concurrent structures, use stress tests and race-aware tooling where available. A test that passes once does not prove a concurrent algorithm is safe.

# Appendix A32. Benchmarking Collections Correctly

Use JMH for microbenchmarks rather than timing a loop with `System.nanoTime()` in a normal application. A useful benchmark controls:

```text
JDK version and JVM flags
Warm-up iterations
Measurement iterations
Input size and distribution
Blackholes to prevent dead-code elimination
Allocation and GC measurement
Single-threaded versus concurrent workload
```

Benchmark the operation that matters. `ArrayList` versus `LinkedList` with ten elements says little about a production workload with millions of objects and a memory-constrained heap.

# Appendix A33. Java 21 and Later Collection Features

Modern Java includes APIs that make ordering and immutable construction clearer. In Java 21, `SequencedCollection`, `SequencedSet` and `SequencedMap` express access to encounter order where the implementation supports it:

```java
SequencedSet<String> regions = new LinkedHashSet<>();
regions.add("US");
regions.add("CA");

String first = regions.getFirst();
String last = regions.getLast();
SequencedSet<String> reverse = regions.reversed();
```

Use the APIs supported by the project's target JDK and build tool. Do not introduce a newer API merely because the local IDE supports it; production runtime, compiler release and deployment images must agree.

# Appendix A34. Virtual Threads Do Not Remove Collection Constraints

Virtual threads make it cheaper to have many waiting tasks, but they do not make an unbounded collection safe:

```text
More virtual-thread tasks
 -> more outstanding work
 -> more queued objects and downstream calls
 -> possible memory/database saturation
```

Use bounded queues, semaphores, rate limits and connection pools. Measure the entire pipeline. Thread scalability and data-structure capacity are separate concerns.

# Appendix A35. Senior Scenario: Design a Thread-Safe Frequency Counter

Requirement: many threads count words and reads may happen while counting.

```java
ConcurrentHashMap<String, LongAdder> counts = new ConcurrentHashMap<>();

void record(String word) {
 counts.computeIfAbsent(word, ignored -> new LongAdder())
 .increment();
}

long count(String word) {
LongAdder counter = counts.get(word);
 return counter == null ? 0L : counter.sum();
}
```

Interview explanation:

> I use a concurrent map for key publication and `LongAdder` for high-contention increments. The result is appropriate for a frequently updated approximate snapshot, not for a transactionally exact total at every instant. If exact read-after-write semantics are required, I would define the consistency requirement first and may choose a different design.

Also ask whether the key space is bounded. A frequency map over untrusted input can become a memory leak unless entries expire, are sampled or are aggregated in bounded windows.

# Appendix A36. Senior Scenario: Deduplicate Events Safely

Requirement: event delivery is at least once and the consumer must avoid repeating a business effect.

```text
Receive event
 -> validate event ID and tenant
 -> atomically claim event ID in durable storage
 -> execute idempotent business operation
 -> record completion
 -> acknowledge message
```

An in-memory `HashSet` is insufficient because it disappears on restart and is different in every replica. Use a database unique constraint, durable key-value conditional write or another store with a defined retention period.

The senior answer also covers expiry, replay, partial failure and what happens when the claim succeeds but the process stops before completion.

# Appendix A37. Senior Scenario: Bounded Top-K Results

Requirement: find the largest `k` values without sorting millions of elements.

```java
PriorityQueue<Integer> smallest = new PriorityQueue<>(k);

for (int value : values) {
 smallest.offer(value);
 if (smallest.size() > k) {
 smallest.poll();
 }
}
```

This maintains the smallest of the retained values at the head and uses $O(n \log k)$ time and $O(k)$ additional space for a bounded `k`. Validate `k`, define duplicate behaviour and decide how ties are ordered. For distributed data, compute local top-k values and merge bounded candidates rather than shipping every item to one node.

# Appendix A38. Senior Scenario: Safe List-to-Map Conversion

Never assume keys are unique unless the domain guarantees it:

```java
Map<Long, Employee> byId = employees.stream()
 .collect(Collectors.toMap(
 Employee::id,
 Function.identity(),
 (first, second) -> first,
 LinkedHashMap::new));
```

The merge policy must be a business decision:

```text
Reject duplicates
Keep first
Keep last
Combine values
Group into Map<Key, List<Value>>
```

Using `toMap` without a merge function can fail at runtime when duplicate keys occur. Using “keep last” silently can be just as dangerous if duplicates indicate corrupt input.

# Appendix A39. Senior Scenario: Local LRU Cache Review

```java
final class LruCache<K, V> extends LinkedHashMap<K, V> {
 private final int capacity;

 LruCache(int capacity) {
 super(capacity, 0.75f, true);
 if (capacity < 1) {
 throw new IllegalArgumentException("capacity must be positive");
 }
 this.capacity = capacity;
 }

 @Override
 protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
 return size() > capacity;
 }
}
```

This is a useful interview implementation, but a production review asks more:

- Is it thread-safe?
- Does capacity mean entries or weighted bytes?
- Is TTL required as well as LRU?
- How are stale values invalidated?
- What is the stampede policy?
- What metrics and hit-rate alerts exist?
- Should a proven cache library be used instead?

Correct code in isolation is not automatically a production-ready cache.

# Appendix A40. Common Collection Mistakes in Production

- Using `HashMap` order as an undocumented API contract.
- Calling `containsKey` and `put` separately when an atomic operation is required.
- Using mutable objects as keys.
- Returning a mutable internal list from a service.
- Calling `parallelStream()` for blocking I/O.
- Using an unbounded queue for a durable workload.
- Building a whole dataset in memory to paginate it.
- Creating a cache with no maximum size or expiry.
- Treating `unmodifiableList` as a deep immutable copy.
- Logging complete collections containing personal or secret data.
- Choosing `LinkedList` because insertion is theoretically constant time.
- Sorting a million values when only top-k is required.
- Ignoring duplicate keys during list-to-map conversion.
- Assuming a concurrent collection makes the contained objects thread-safe.
- Comparing microbenchmark results across different JDKs without controlling the environment.

# Appendix A41. A Strong Senior Interview Answer Template

For any collection question, answer in this order:

```text
1. State the requirement and invariant.
2. Choose the interface and implementation.
3. Explain expected time and space complexity.
4. Explain ordering and equality semantics.
5. Explain thread-safety and atomicity.
6. Explain capacity, memory and failure behaviour.
7. Give a small example.
8. Name the production metric or test you would add.
```

Example:

> I would use `LinkedHashMap` for a deterministic insertion-ordered lookup result. If I need access order and a strict entry bound, it can support a local LRU design. It is not thread-safe by itself, and it does not solve distributed cache consistency. I would define duplicate-key behaviour, protect or confine access, add hit/eviction metrics and test eviction, concurrency and restart behaviour.

# Appendix A42. Final Production Checklist

### Correctness

- Collection interfaces describe the intended contract.
- Equality, hash code and comparator rules are valid.
- Duplicate, null, empty and ordering behaviour is explicit.
- Mutable keys are prohibited or safely copied.

### Performance

- Expected size and access pattern drive the implementation.
- Large inputs are bounded, paginated or streamed.
- Queries and transformations avoid accidental copies.
- Benchmark claims use realistic data and a controlled JDK.

### Concurrency

- Ownership and publication are defined.
- Compound operations use atomic APIs or a clear lock.
- Queues have capacity and backpressure.
- Concurrent collections are not mistaken for thread-safe element objects.

### Reliability

- Caches have capacity, expiry, invalidation and stampede policies.
- Event deduplication is durable when business effects require it.
- Restart, replay, failure and overload behaviour are tested.
- Collection size, queue age and cache health are observable.

### Security

- Input cardinality and page size are bounded.
- Sensitive values are not logged or cached across authorization boundaries.
- Tenant and ownership checks happen before aggregation.

# Appendix A43. Final Collections Mindset

```text
Requirement
 -> collection contract
 -> equality/order semantics
 -> capacity and memory model
 -> concurrency and ownership
 -> failure and overload policy
 -> tests and measurements
 -> observable production behaviour
```

The senior-level answer is not “use `HashMap`.” It is: “Here is the invariant, here is why this collection preserves it, here is the complexity and memory cost, here is what happens under concurrency and overload, and here is how I will test and observe it in production.”