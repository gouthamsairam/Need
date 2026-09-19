Absolutely. For a **Senior Java Developer**, Collections interviews are usually not limited to definitions such as `ArrayList vs LinkedList`. Interviewers focus heavily on **real-world scenarios, performance, concurrency, Java internals, design decisions, and follow-up questions**.

Below is a senior-level scenario-based interview set.

# Java Collections — Senior Developer Scenario-Based Interview Questions & Answers
**PART 1: Fundamentals — Why, What, Where**
-------------------------------------------

### **Q1. What is the Java Collections Framework and why do we need it?**

**The Java Collections Framework (JCF) is a unified architecture (interfaces + classes + algorithms) in java.util for representing and manipulating groups of objects.Why we need it:**

**Without Collections (old way)**

**With Collections**

**Fixed-size arrays (int\[\] arr = new int\[10\])**

**Dynamic-size (ArrayList grows automatically)**

**Write your own search, sort, insert logic**

**Built-in sort(), binarySearch(), Collections.unmodifiableList()**

**No standard way to pass groups of objects**

**Standard interfaces (List, Set, Map) as method parameters**

**Type-unsafe (Object\[\] everywhere)**

**Type-safe with generics (List)**

**No thread-safe options out of the box**

**ConcurrentHashMap, CopyOnWriteArrayList, etc.**

**Before collections (Java 1.0):**

_**// Old painful way**_

**Vector v = new Vector();**_**// synchronized, slow**_

**v.add("hello");**

**String s = (String) v.get(0);**_**// manual casting, ClassCastException risk**_

**Hashtable ht = new Hashtable();**

**ht.put("key", "value");**

**String val = (String) ht.get("key");**_**// manual casting again**_

**With collections (Java 1.2+):**

**List list = new ArrayList<>();**

**list.add("hello");**

**String s = list.get(0);**_**// no casting needed, compile-time type safety**_

**Map map = new HashMap<>();**

**map.put("key", "value");**

**String val = map.get("key");**_**// type-safe**_

### **Q2. Draw and explain the complete Collections hierarchy.**

**Iterable**

**│**

**Collection**

**/ | \\**

**/ | \\**

**List Set Queue**

**/ | \\ | \\ | \\**

**/ | \\ | \\ | \\**

**ArrayList | Vector | TreeSet | ArrayDeque**

**LinkedList HashSet LinkedHashSet**

**PriorityQueue**

**Map (separate hierarchy, NOT Collection)**

**/ | \\**

**/ | \\**

**HashMap TreeMap LinkedHashMap**

**|**

**ConcurrentHashMap**

**|**

**Hashtable (legacy)**

**Key interfaces:**

**Interface**

**Purpose**

**Ordering**

**Duplicates**

**Null**

**List**

**Ordered sequence**

**Insertion order**

**Yes**

**Yes**

**Set**

**Unique elements**

**Depends on impl**

**No**

**Depends**

**Queue**

**FIFO processing**

**FIFO / priority**

**Yes**

**Depends**

**Deque**

**Double-ended queue**

**Both ends**

**Yes**

**No (ArrayDeque)**

**Map**

**Key-value pairs**

**Depends on impl**

**Keys: No, Values: Yes**

**Depends**

### **Q3. What is the difference between Collection and Collections?**

**Collection (interface)**

**Collections (utility class)**

**java.util.Collection**

**java.util.Collections**

**Root interface for List, Set, Queue**

**Utility class with static helper methods**

**Defines add(), remove(), size(), etc.**

**Provides sort(), unmodifiableList(), synchronizedList(), etc.**

_**// Collection — the interface**_

**Collection items = new ArrayList<>();**

**items.add("Apple");**

_**// Collections — the utility class**_

**List list = new ArrayList<>(List.of("Banana", "Apple", "Cherry"));**

**Collections.sort(list);**_**// \[Apple, Banana, Cherry\]**_

**List safe = Collections.unmodifiableList(list);**_**// immutable view**_

**List synced = Collections.synchronizedList(list);**_**// thread-safe wrapper**_

**PART 2: List Interface — ArrayList, LinkedList, Vector**
----------------------------------------------------------

### **Q4. How does ArrayList work internally?**

**ArrayList is backed by a dynamic array (Object\[\] elementData).Internal working:**

**Initial state: elementData = new Object\[10\] (default capacity)**

**size = 0**

**add("A"): elementData\[0\] = "A", size = 1**

**add("B"): elementData\[1\] = "B", size = 2**

**...**

**add 11th element:**

**1. Check: size == elementData.length? YES**

**2. Grow: newCapacity = oldCapacity + (oldCapacity >> 1) → 10 + 5 = 15**

**3. Arrays.copyOf(elementData, 15) ← O(n) copy!**

**4. elementData\[10\] = newElement, size = 11**

**Key operations and time complexity:**

**Operation**

**Time Complexity**

**Why**

**get(index)**

**O(1)**

**Direct array index access**

**add(element) (end)**

**Amortized O(1)**

**Usually O(1), occasionally O(n) when resizing**

**add(index, element)**

**O(n)**

**Must shift all elements after index**

**remove(index)**

**O(n)**

**Must shift all elements after index**

**contains(element)**

**O(n)**

**Linear scan**

**Resizing visualized:**

**Capacity: 10 → 15 → 22 → 33 → 49 → ... (grows by 50% each time)**

**Formula: newCapacity = oldCapacity + (oldCapacity >> 1)**

**Real-world scenario: Use ArrayList when you need fast random access by index and mostly add to the end.**

_**// E-commerce: displaying a paginated product list**_

**List products = productRepository.findAll();**_**// ArrayList internally**_

**Product thirdProduct = products.get(2);**_**// O(1) — fast!**_

### **Q5. How does LinkedList work internally? How is it different from ArrayList?**

**LinkedList is a doubly-linked list. Each element is a Node object:**

_**// Internal Node structure (simplified from JDK source)**_

**private static class Node {**

**E item;**

**Node next;**

**Node prev;**

**}**

**Memory layout:**

**ArrayList: \[A\]\[B\]\[C\]\[D\]\[E\] ← contiguous memory block**

**LinkedList: \[prev|A|next\] ↔ \[prev|B|next\] ↔ \[prev|C|next\] ← scattered in heap**

**Comparison:**

**Operation**

**ArrayList**

**LinkedList**

**get(index)**

**O(1) — array access**

**O(n) — must traverse nodes**

**add(end)**

**O(1) amortized**

**O(1) — update last pointer**

**add(0, elem)**

**O(n) — shift all**

**O(1) — update head pointer**

**remove(0)**

**O(n) — shift all**

**O(1) — update head pointer**

**Memory**

**Less (just array + elements)**

**More (each node = element + 2 pointers = ~3x)**

**Cache performance**

**Excellent (contiguous)**

**Poor (scattered in heap)**

**Iterator.remove()**

**O(n)**

**O(1)**

**When to use LinkedList:**

***Frequent insertions/removals at the beginning or middle (using iterator)**

***Implementing a Queue or Deque**

**When to use ArrayList (almost always):**

***Random access by index**

***Iterating through elements (cache-friendly)**

***Most general-purpose use cases**

_**// LinkedList as a Queue (real-world: task processing)**_

**Queue taskQueue = new LinkedList<>();**

**taskQueue.offer(new Task("Send email"));**

**taskQueue.offer(new Task("Generate report"));**

**Task next = taskQueue.poll();**_**// removes and returns head — O(1)**_

### **Q6. What is the difference between ArrayList and Vector?**

**Feature**

**ArrayList**

**Vector**

**Synchronization**

**Not synchronized**

**Synchronized (every method)**

**Performance**

**Fast (no lock overhead)**

**Slow (acquires lock on every operation)**

**Growth**

**Grows by 50% (old + old >> 1)**

**Grows by 100% (doubles)**

**Iterator**

**fail-fast**

**fail-fast (also has legacy Enumeration)**

**Introduced**

**Java 1.2**

**Java 1.0 (legacy)**

**Why you should NEVER use Vector:**

_**// Vector synchronizes EVERY method — even if you're single-threaded!**_

**Vector v = new Vector<>();**_**// DON'T DO THIS**_

_**// Instead, if you need thread safety:**_

**List safe = Collections.synchronizedList(new ArrayList<>());**

_**// or better:**_

**List concurrent = new CopyOnWriteArrayList<>();**

### **Q7. What is CopyOnWriteArrayList and when should you use it?**

**CopyOnWriteArrayList creates a new copy of the entire array on every write (add/set/remove), but reads happen on the original array without any locking.Internal working:**

**Thread 1 reads: → reads from array reference (no lock, fast)**

**Thread 2 writes: → copies entire array → modifies copy → swaps reference (volatile)**

**Thread 1 reads: → still reads old array (snapshot) — no ConcurrentModificationException**

_**// Perfect for: configurations, listener lists, caches that rarely change**_

**CopyOnWriteArrayList listeners = new CopyOnWriteArrayList<>();**

_**// Multiple threads can iterate safely**_

**listeners.forEach(listener -> listener.onEvent(event));**_**// no lock needed**_

_**// Occasional adds (expensive but rare)**_

**listeners.add(new MyListener());**

**When to use:**

***Read-heavy, write-rare scenarios (e.g., list of active listeners, feature flags)**

***When you need safe iteration without ConcurrentModificationException**

**When NOT to use:**

***Frequent writes (each write copies the entire array = O(n))**

***Large lists (copy cost is proportional to size)**

**PART 3: Set Interface — HashSet, LinkedHashSet, TreeSet**
-----------------------------------------------------------

### **Q8. How does HashSet work internally?**

**Secret: HashSet is backed by a HashMap internally!**

_**// Actual JDK source (simplified)**_

**public class HashSet {**

**private transient HashMap map;**

**private static final Object PRESENT = new Object();**_**// dummy value**_

**public boolean add(E e) {**

**return map.put(e, PRESENT) == null;**

**}**

**public boolean contains(Object o) {**

**return map.containsKey(o);**

**}**

**public boolean remove(Object o) {**

**return map.remove(o) == PRESENT;**

**}**

**}**

**Every element you add to a HashSet becomes a KEY in the internal HashMap, and the VALUE is a shared dummy object called PRESENT.How uniqueness works:**

**add("Apple"):**

**1. hash("Apple") → bucket index (say 5)**

**2. Check bucket 5: empty → store**

**3. Returns true (new element)**

**add("Apple"):**

**1. hash("Apple") → bucket index 5**

**2. Check bucket 5: found "Apple" via equals()**

**3. Key already exists → overwrite value (same PRESENT)**

**4. Returns false (duplicate)**

**This is why you MUST override both hashCode() and equals() for custom objects in a HashSet:**

**public class Employee {**

**private int id;**

**private String name;**

**@Override**

**public boolean equals(Object o) {**

**if (this == o) return true;**

**if (!(o instanceof Employee e)) return false;**

**return id == e.id;**

**}**

**@Override**

**public int hashCode() {**

**return Integer.hashCode(id);**

**}**

**}**

**Set employees = new HashSet<>();**

**employees.add(new Employee(1, "Alice"));**

**employees.add(new Employee(1, "Alice"));**_**// duplicate — not added**_

**System.out.println(employees.size());**_**// 1**_

**What happens if you override equals() but NOT hashCode()?**

_**// BAD — only equals() overridden**_

**Employee e1 = new Employee(1, "Alice");**

**Employee e2 = new Employee(1, "Alice");**

**e1.equals(e2);**_**// true**_

**e1.hashCode();**_**// 735 (default from Object — memory address based)**_

**e2.hashCode();**_**// 982 (different!)**_

**Set set = new HashSet<>();**

**set.add(e1);**_**// goes to bucket 735 % 16 = bucket 3**_

**set.add(e2);**_**// goes to bucket 982 % 16 = bucket 6 — DIFFERENT BUCKET!**_

**set.size();**_**// 2! — WRONG! Both represent the same employee**_

**Contract: If a.equals(b) is true, then a.hashCode() MUST equal b.hashCode().**

### **Q9. What is the difference between HashSet, LinkedHashSet, and TreeSet?**

**Feature**

**HashSet**

**LinkedHashSet**

**TreeSet**

**Internal structure**

**HashMap**

**LinkedHashMap**

**Red-Black Tree (TreeMap)**

**Ordering**

**No order guaranteed**

**Insertion order**

**Sorted order (natural or Comparator)**

**Null elements**

**1 null allowed**

**1 null allowed**

**No null (throws NPE)**

**add/remove/contains**

**O(1) average**

**O(1) average**

**O(log n)**

**Use case**

**Fast uniqueness check**

**Unique + preserve insertion order**

**Sorted unique elements**

_**// HashSet — no order**_

**Set hash = new HashSet<>();**

**hash.addAll(List.of("Banana", "Apple", "Cherry"));**

**System.out.println(hash);**_**// \[Apple, Cherry, Banana\] — unpredictable order**_

_**// LinkedHashSet — insertion order**_

**Set linked = new LinkedHashSet<>();**

**linked.addAll(List.of("Banana", "Apple", "Cherry"));**

**System.out.println(linked);**_**// \[Banana, Apple, Cherry\] — insertion order!**_

_**// TreeSet — sorted order**_

**Set tree = new TreeSet<>();**

**tree.addAll(List.of("Banana", "Apple", "Cherry"));**

**System.out.println(tree);**_**// \[Apple, Banana, Cherry\] — alphabetically sorted!**_

**Real-world scenario — Removing duplicate orders while keeping arrival order:**

**List incomingOrders = getOrdersFromKafka();**_**// may contain duplicates**_

**Set uniqueOrders = new LinkedHashSet<>(incomingOrders);**

_**// Duplicates removed, processing order preserved**_

**Real-world scenario — Leaderboard (sorted scores):**

**TreeSet leaderboard = new TreeSet<>(Comparator.comparingInt(Player::getScore).reversed());**

**leaderboard.add(new Player("Alice", 950));**

**leaderboard.add(new Player("Bob", 1200));**

**leaderboard.add(new Player("Charlie", 800));**

_**// Iteration: Bob(1200) → Alice(950) → Charlie(800)**_

### **Q10. How does TreeSet maintain sorted order internally?**

**TreeSet is backed by a Red-Black Tree (a self-balancing binary search tree).Red-Black Tree properties:**

1.**Every node is either RED or BLACK**

2.**Root is always BLACK**

3.**No two consecutive RED nodes (parent-child)**

4.**Every path from root to leaf has the same number of BLACK nodes**

5.**Guarantees tree height ≤ 2 \* log(n) → O(log n) operations**

**\[50 BLACK\]**

**/ \\**

**\[30 RED\] \[70 RED\]**

**/ \\ / \\**

**\[20 BLK\] \[40 BLK\] \[60 BLK\] \[80 BLK\]**

**Operations:**

**TreeSet set = new TreeSet<>();**

**set.add(50);**_**// root**_

**set.add(30);**_**// left of 50**_

**set.add(70);**_**// right of 50**_

**set.add(20);**_**// left of 30 → may trigger rebalancing**_

_**// Powerful NavigableSet methods:**_

**set.first();**_**// 20 (smallest)**_

**set.last();**_**// 70 (largest)**_

**set.headSet(50);**_**// \[20, 30\] (elements < 50)**_

**set.tailSet(50);**_**// \[50, 70\] (elements >= 50)**_

**set.subSet(25, 65);**_**// \[30, 50\] (elements in range)**_

**set.floor(45);**_**// 40 (greatest element <= 45)**_

**set.ceiling(45);**_**// 50 (smallest element >= 45)**_

**PART 4: Map Interface — HashMap, TreeMap, LinkedHashMap**
-----------------------------------------------------------

### **Q11. How does HashMap work internally? (Most asked question!)**

**This is the #1 most asked Java interview question. Let's go deep.Internal structure:**

_**// Simplified JDK source**_

**public class HashMap {**

**Node\[\] table;**_**// array of buckets (default 16)**_

**int size;**_**// number of key-value pairs**_

**int threshold;**_**// capacity \* loadFactor**_

**float loadFactor;**_**// default 0.75**_

**static class Node {**

**final int hash;**

**final K key;**

**V value;**

**Node next;**_**// linked list for collisions**_

**}**

**}**

**Step-by-step: map.put("name", "Alice")**

**Step 1: Calculate hash**

**hash = hash("name")**

**// JDK spreads the hash: h ^ (h >>> 16) — reduces collisions**

**// Let's say hash = 3292**

**Step 2: Find bucket index**

**index = hash & (capacity - 1) // equivalent to hash % capacity, but faster**

**index = 3292 & 15 = 12 // assuming capacity = 16**

**Step 3: Check bucket 12**

**Case A: Bucket is empty → create new Node, place it there**

**Case B: Bucket has nodes → traverse linked list:**

**- If key.equals(existingKey) → update value (overwrite)**

**- If no match → append new Node at end of list**

**Case C: If linked list length >= 8 AND table size >= 64**

**→ convert linked list to RED-BLACK TREE (Java 8+)**

**(search goes from O(n) to O(log n))**

**Step 4: Check if resize needed**

**if (++size > threshold) → resize to double capacity**

**Visual representation:**

**Buckets (table\[\]):**

**\[0\] → null**

**\[1\] → null**

**\[2\] → \[hash|"age"|25|next\] → \[hash|"score"|99|null\] ← collision chain**

**\[3\] → null**

**...**

**\[12\] → \[hash|"name"|"Alice"|null\]**

**...**

**\[15\] → null**

**Treeification (Java 8+):**

**When bucket has ≥ 8 nodes AND table size ≥ 64:**

**Linked List → Red-Black Tree (O(n) → O(log n) lookup)**

**When bucket shrinks to ≤ 6 nodes:**

**Red-Black Tree → Linked List (untreeify)**

**Resizing (rehashing):**

**Old table: capacity 16, threshold 12 (16 \* 0.75)**

**When 13th entry added:**

**1. Create new table of capacity 32**

**2. Rehash ALL entries to new bucket positions**

**3. Some entries move, some stay (depends on new hash bit)**

**This is O(n) — expensive!**

### **Q12. What happens when two keys have the same hashCode in HashMap?**

**This is called a hash collision. HashMap handles it through chaining.**

_**// Two different keys with same hashCode (collision)**_

**map.put(key1, "value1");**_**// hash(key1) % 16 = 5 → goes to bucket 5**_

**map.put(key2, "value2");**_**// hash(key2) % 16 = 5 → also bucket 5!**_

_**// Bucket 5 now has a linked list:**_

_**// \[key1|value1\] → \[key2|value2\] → null**_

**How get() works with collisions:**

**map.get(key2):**

**1. hash(key2) % 16 = 5 → go to bucket 5**

**2. Check first node: key1.equals(key2)? NO → go to next**

**3. Check second node: key2.equals(key2)? YES → return "value2"**

**Worst case without treeification: All keys land in same bucket → O(n) for every operation.With treeification (Java 8+): Same bucket but tree structure → O(log n).Interview trick question:**

_**// Can you design a class that causes maximum collisions?**_

**class BadKey {**

**@Override**

**public int hashCode() {**

**return 42;**_**// ALL objects go to same bucket!**_

**}**

**}**

_**// This makes HashMap degrade to O(n) for < 8 entries, O(log n) for >= 8**_

### **Q13. Why is the default capacity 16 and load factor 0.75?**

**Capacity = 16 (must be power of 2):**

***Powers of 2 allow the fast bitwise AND operation: hash & (capacity - 1) instead of expensive modulo hash % capacity**

***15 in binary = 1111 → bitwise AND distributes evenly**

**Load factor = 0.75:**

***A trade-off between space and time**

***At 0.75, on average each bucket has ≤ 1 entry → most operations are O(1)**

***Higher (e.g., 0.9) = more collisions but less memory**

***Lower (e.g., 0.5) = fewer collisions but more memory wasted**

_**// Custom initial capacity and load factor**_

_**// Pre-size if you know the expected number of entries**_

**Map map = new HashMap<>(1000, 0.75f);**

_**// Avoids multiple resizes when adding ~750 entries**_

_**// Better: use the factory method**_

**Map map = HashMap.newHashMap(1000);**

_**// Java 19+ — automatically calculates capacity for 1000 entries**_

**Rule of thumb: If you know you'll store N entries, initialize with capacity = N / 0.75 + 1 to avoid resizing.**

### **Q14. What is the difference between HashMap, LinkedHashMap, and TreeMap?**

**Feature**

**HashMap**

**LinkedHashMap**

**TreeMap**

**Ordering**

**No order**

**Insertion order (or access order)**

**Sorted by key**

**Internal**

**Array + Linked List/Tree**

**HashMap + Doubly-linked list**

**Red-Black Tree**

**put/get/remove**

**O(1) avg**

**O(1) avg**

**O(log n)**

**Null keys**

**1 null key allowed**

**1 null key allowed**

**No null keys**

**Use case**

**General purpose**

**LRU cache, ordered iteration**

**Range queries, sorted data**

**Map hashMap = new HashMap<>();**

**hashMap.put("Charlie", 3);**

**hashMap.put("Alice", 1);**

**hashMap.put("Bob", 2);**

**System.out.println(hashMap);**_**// {Bob=2, Alice=1, Charlie=3} — random order**_

**Map linkedMap = new LinkedHashMap<>();**

**linkedMap.put("Charlie", 3);**

**linkedMap.put("Alice", 1);**

**linkedMap.put("Bob", 2);**

**System.out.println(linkedMap);**_**// {Charlie=3, Alice=1, Bob=2} — insertion order!**_

**Map treeMap = new TreeMap<>();**

**treeMap.put("Charlie", 3);**

**treeMap.put("Alice", 1);**

**treeMap.put("Bob", 2);**

**System.out.println(treeMap);**_**// {Alice=1, Bob=2, Charlie=3} — sorted!**_

### **Q15. How to implement an LRU Cache using LinkedHashMap?**

**LRU (Least Recently Used) Cache evicts the least recently accessed entry when the cache is full.LinkedHashMap supports this natively with access-order mode:**

**public class LRUCache extends LinkedHashMap {**

**private final int maxSize;**

**public LRUCache(int maxSize) {**

**super(maxSize, 0.75f, true);**_**// true = access-order (not insertion-order)**_

**this.maxSize = maxSize;**

**}**

**@Override**

**protected boolean removeEldestEntry(Map.Entry eldest) {**

**return size() > maxSize;**_**// remove oldest when capacity exceeded**_

**}**

**}**

_**// Usage**_

**LRUCache cache = new LRUCache<>(3);**

**cache.put("A", "1");**

**cache.put("B", "2");**

**cache.put("C", "3");**

**cache.get("A");**_**// access A — moves it to end (most recent)**_

**cache.put("D", "4");**_**// cache full → evicts B (least recently used)**_

**System.out.println(cache);**_**// {C=3, A=1, D=4}**_

**How it works internally:**

**Doubly-linked list maintains access order:**

**HEAD (oldest) ← → ← → ← → TAIL (newest)**

**After put(A), put(B), put(C): HEAD → A ↔ B ↔ C ← TAIL**

**After get(A): HEAD → B ↔ C ↔ A ← TAIL (A moved to tail)**

**After put(D): HEAD → C ↔ A ↔ D ← TAIL (B evicted from head)**

**Real-world scenario: Database query cache, session cache, DNS cache.**

### **Q16. What is ConcurrentHashMap and how does it work internally?**

**Problem with HashMap: Not thread-safe. Two threads modifying simultaneously → data corruption, infinite loops (in pre-Java 8).Problem with Collections.synchronizedMap(): Locks the entire map on every operation → poor concurrency.ConcurrentHashMap solution: Fine-grained locking for maximum concurrency.Java 8+ internal design:**

**Array of Nodes (like HashMap), but with:**

**1\. CAS (Compare-And-Swap) for non-contended buckets**

**2\. synchronized on individual bucket head for contended operations**

**3\. No locking for reads (volatile reads)**

_**// Multiple threads can safely read and write**_

**ConcurrentHashMap map = new ConcurrentHashMap<>();**

_**// Atomic operations (crucial for thread safety)**_

**map.putIfAbsent("counter", 0);**

**map.compute("counter", (key, val) -> val + 1);**_**// atomic read-modify-write**_

**map.merge("counter", 1, Integer::sum);**_**// atomic merge**_

**map.computeIfAbsent("users", key -> new ArrayList<>());**_**// lazy init**_

**Comparison of thread-safe map options:**

**Feature**

**synchronizedMap**

**Hashtable**

**ConcurrentHashMap**

**Lock scope**

**Entire map**

**Entire map**

**Per-bucket**

**Read concurrency**

**Blocked by writes**

**Blocked by writes**

**Lock-free reads**

**Null keys/values**

**Depends on backing map**

**No nulls**

**No nulls**

**Performance**

**Poor under contention**

**Poor**

**Excellent**

**Iterator**

**fail-fast**

**fail-fast**

**Weakly consistent (no CME)**

**Real-world scenario — Thread-safe word counter:**

**ConcurrentHashMap wordCount = new ConcurrentHashMap<>();**

_**// Multiple threads processing different files simultaneously**_

**void processWord(String word) {**

**wordCount.computeIfAbsent(word, k -> new LongAdder()).increment();**

**}**

_**// After all threads finish:**_

**wordCount.forEach((word, count) ->**

**System.out.println(word + ": " + count.sum()));**

### **Q17. What is the difference between HashMap and Hashtable?**

**Feature**

**HashMap**

**Hashtable**

**Synchronized**

**No**

**Yes (every method)**

**Null keys**

**1 null key allowed**

**No null keys or values**

**Performance**

**Fast**

**Slow (lock overhead)**

**Iterator**

**fail-fast**

**fail-fast (also has Enumeration)**

**Introduced**

**Java 1.2**

**Java 1.0 (legacy)**

**Superclass**

**AbstractMap**

**Dictionary (legacy)**

**Bottom line: Never use Hashtable. Use HashMap (single-threaded) or ConcurrentHashMap (multi-threaded).**

PART 5: Queue and Deque
-----------------------

### Here’s a clean, well-structured documentation version of your content, formatted for readability and reuse (like notes, wiki, or interview prep):

📘 Java Queue (Collection Framework) – Documentation
====================================================

🔹 1. Overview
--------------

### A Queue in Java is a collection used to store elements before processing, typically following the FIFO (First In, First Out) principle.

### ✔ Key Idea:

* The first element added is the first one removed.

### ✔ Real-life analogy:

* People standing in a queue → first person in line gets served first.

🔹 2. Queue Interface
---------------------

### The Queue interface is part of the Java Collections Framework.

### public interface Queue extends Collection

🔹 3. Core Operations
---------------------

### Each operation has two variants:

* One throws an exception

* One returns a special value

### Operation

### Throws Exception

### Returns Special Value

### Insert

### add()

### offer()

### Remove

### remove()

### poll()

### Examine

### element()

### peek()

🔹 4. Basic Example
-------------------

### Queue queue = new LinkedList<>();

### queue.add(10);

### queue.add(20);

### queue.add(30);

### System.out.println(queue); // \[10, 20, 30\]

### queue.remove(); // removes 10

### System.out.println(queue); // \[20, 30\]

🔹 5. Method Differences
------------------------

### ✔ add() vs offer()

### queue.add(10); // Throws exception if full

### queue.offer(20); // Returns false if full

### 👉 Prefer offer() in production for safer handling.

### ✔ remove() vs poll()

### queue.remove(); // Throws exception if empty

### queue.poll(); // Returns null if empty

### ✔ element() vs peek()

### queue.element(); // Throws exception if empty

### queue.peek(); // Returns null if empty

🔹 6. Queue Implementations
---------------------------

### 🟢 1. LinkedList

### Queue q = new LinkedList<>();

* Maintains FIFO

* Allows null (not recommended)

### 🟢 2. PriorityQueue

### Queue pq = new PriorityQueue<>();

* Orders elements by priority (min-heap by default)

### pq.add(30);

### pq.add(10);

### pq.add(20);

### System.out.println(pq); // \[10, 30, 20\]

### 🟢 3. ArrayDeque

### Queue dq = new ArrayDeque<>();

* Faster than LinkedList

* Does not allow nulls

* Can act as both Queue and Stack

### 🟢 4. BlockingQueue (Multithreading)

### Used in concurrent systems.

### Examples:

* ArrayBlockingQueue

* LinkedBlockingQueue

🔹 7. Types of Queues
---------------------

### 🔸 FIFO Queue

### Queue q = new LinkedList<>();

### 🔸 Priority Queue

### PriorityQueue pq = new PriorityQueue<>();

* Processes smallest (or highest priority) element first

### 🔸 Deque (Double-ended Queue)

### Deque dq = new ArrayDeque<>();

### dq.addFirst(10);

### dq.addLast(20);

🔹 8. Real-World Use Cases
--------------------------

### ✅ 1. Messaging Systems

* Used in systems like Kafka

* Example:

* Producer adds tasks

* Consumer processes asynchronously

### ✅ 2. Thread Pools / Task Scheduling

### BlockingQueue queue = new LinkedBlockingQueue<>();

* Used in ExecutorService

* Handles background tasks

### ✅ 3. BFS Algorithm

### Queue q = new LinkedList<>();

* Used in:

* Graph traversal

* Shortest path algorithms

### ✅ 4. Rate Limiting / Buffering

* Stores API requests/events before processing

### ✅ 5. Job Scheduling

* Printer queues

* OS task scheduling

🔹 9. Producer-Consumer Example
-------------------------------

### Queue queue = new LinkedList<>();

### // Producer

### queue.add("Task1");

### queue.add("Task2");

### // Consumer

### while (!queue.isEmpty()) {

### System.out.println("Processing: " + queue.poll());

### }

🔹 10. PriorityQueue with Custom Comparator
-------------------------------------------

### PriorityQueue pq = new PriorityQueue<>((a, b) -> b - a);

### pq.add(10);

### pq.add(30);

### pq.add(20);

### while (!pq.isEmpty()) {

### System.out.println(pq.poll()); // 30, 20, 10

### }

🔹 11. When to Use What?
------------------------

### Scenario

### Recommended Implementation

### General FIFO

### LinkedList / ArrayDeque

### High performance

### ArrayDeque

### Priority handling

### PriorityQueue

### Multithreading

### BlockingQueue

🔹 12. Summary
--------------

* Queue follows FIFO

* Provides safe vs exception-based methods

* Key implementations:

* LinkedList

* PriorityQueue

* ArrayDeque

* BlockingQueue

* Widely used in:

* Messaging systems

* Multithreading

* Algorithms (BFS)

* System design

### **Q18. Explain Queue and Deque interfaces with their implementations.**

**Queue — FIFO (First-In-First-Out):**

_**// Two sets of methods:**_

_**// Throws exception vs Returns special value**_

**queue.add(e); vs queue.offer(e);**_**// insert**_

**queue.remove(); vs queue.poll();**_**// remove head**_

**queue.element(); vs queue.peek();**_**// examine head**_

**Implementations:**

_**// 1. LinkedList as Queue**_

**Queue queue = new LinkedList<>();**

**queue.offer("Task 1");**

**queue.offer("Task 2");**

**queue.poll();**_**// "Task 1"**_

_**// 2. ArrayDeque (faster, preferred for queues and stacks)**_

**Deque stack = new ArrayDeque<>();**

**stack.push("A");**_**// push onto stack**_

**stack.push("B");**

**stack.pop();**_**// "B" — LIFO**_

**Deque queue2 = new ArrayDeque<>();**

**queue2.offerLast("A");**_**// enqueue**_

**queue2.offerLast("B");**

**queue2.pollFirst();**_**// "A" — FIFO**_

_**// 3. PriorityQueue (elements processed by priority, not insertion order)**_

**PriorityQueue pq = new PriorityQueue<>();**_**// min-heap**_

**pq.offer(30);**

**pq.offer(10);**

**pq.offer(20);**

**pq.poll();**_**// 10 (smallest first!)**_

_**// Max-heap:**_

**PriorityQueue maxPq = new PriorityQueue<>(Comparator.reverseOrder());**

### **Q19. How does PriorityQueue work internally?**

**PriorityQueue is backed by a binary min-heap (array-based).Heap property: Parent node ≤ both children (for min-heap).**

**Array: \[10, 20, 30, 25, 35\]**

**Tree representation:**

**10**

**/ \\**

**20 30**

**/ \\**

**25 35**

**Parent of index i = (i - 1) / 2**

**Left child of i = 2 \* i + 1**

**Right child of i = 2 \* i + 2**

**Operations:**

**Operation**

**Complexity**

**How**

**offer(e)**

**O(log n)**

**Add at end, then "sift up"**

**poll()**

**O(log n)**

**Remove root, move last to root, "sift down"**

**peek()**

**O(1)**

**Return root (index 0)**

**remove(e)**

**O(n)**

**Find element linearly, then sift**

**Real-world scenario — Task scheduler with priorities:**

**record Task(String name, int priority) implements Comparable {**

**public int compareTo(Task other) {**

**return Integer.compare(this.priority, other.priority);**_**// lower number = higher priority**_

**}**

**}**

**PriorityQueue scheduler = new PriorityQueue<>();**

**scheduler.offer(new Task("Low priority backup", 5));**

**scheduler.offer(new Task("CRITICAL: Server down", 1));**

**scheduler.offer(new Task("Medium: Deploy update", 3));**

**while (!scheduler.isEmpty()) {**

**System.out.println(scheduler.poll());**

**}**

_**// Output:**_

_**// Task\[name=CRITICAL: Server down, priority=1\]**_

_**// Task\[name=Medium: Deploy update, priority=3\]**_

_**// Task\[name=Low priority backup, priority=5\]**_

### **Q20. What is the difference between ArrayDeque and LinkedList as a Queue/Stack?**

**Feature**

**ArrayDeque**

**LinkedList**

**Internal**

**Resizable circular array**

**Doubly-linked list**

**Memory**

**Less (no node objects)**

**More (2 pointers per node)**

**Cache**

**Cache-friendly (contiguous)**

**Cache-unfriendly (scattered)**

**Null elements**

**Not allowed**

**Allowed**

**Performance**

**Faster for both queue and stack**

**Slower due to object allocation**

**Use as List**

**Not possible (no random access)**

**Yes (implements List)**

**JDK Javadoc says:**_**"This class is likely to be faster than Stack when used as a stack, and faster than LinkedList when used as a queue."**_

_**// PREFERRED: Use ArrayDeque for stacks and queues**_

**Deque stack = new ArrayDeque<>();**_**// instead of Stack<>**_

**Queue queue = new ArrayDeque<>();**_**// instead of LinkedList<>**_

**PART 6: Iterators, Fail-Fast vs Fail-Safe**
---------------------------------------------

### **Q21. What is fail-fast and fail-safe iteration?**

**Fail-fast: Throws ConcurrentModificationException if the collection is modified during iteration.**

**List list = new ArrayList<>(List.of("A", "B", "C"));**

_**// FAIL-FAST — throws ConcurrentModificationException!**_

**for (String s : list) {**

**if (s.equals("B")) {**

**list.remove(s);**_**// modifying while iterating → BOOM!**_

**}**

**}**

**How fail-fast works internally:**

_**// ArrayList maintains a modCount field**_

_**// Iterator stores expectedModCount at creation time**_

_**// On every next() call, iterator checks: modCount == expectedModCount?**_

_**// If not → throw ConcurrentModificationException**_

_**// Safe way to remove during iteration:**_

**Iterator it = list.iterator();**

**while (it.hasNext()) {**

**if (it.next().equals("B")) {**

**it.remove();**_**// uses iterator's remove — updates expectedModCount**_

**}**

**}**

_**// Or with Java 8+:**_

**list.removeIf(s -> s.equals("B"));**_**// internally uses iterator.remove()**_

**Fail-safe (weakly consistent): Works on a snapshot or copy — never throws CME.**

_**// ConcurrentHashMap — weakly consistent**_

**ConcurrentHashMap map = new ConcurrentHashMap<>();**

**map.put("A", 1);**

**map.put("B", 2);**

**for (Map.Entry entry : map.entrySet()) {**

**map.put("C", 3);**_**// OK! No exception. Iterator may or may not see "C"**_

**}**

_**// CopyOnWriteArrayList — snapshot**_

**CopyOnWriteArrayList cowList = new CopyOnWriteArrayList<>(List.of("A", "B", "C"));**

**for (String s : cowList) {**

**cowList.add("D");**_**// OK! Iterator works on snapshot. Won't see "D"**_

**}**

**Collection**

**Iterator Type**

**Throws CME?**

**ArrayList, HashSet, HashMap**

**Fail-fast**

**Yes**

**ConcurrentHashMap**

**Weakly consistent**

**No**

**CopyOnWriteArrayList**

**Snapshot**

**No**

**PART 7: Comparable vs Comparator**
------------------------------------

### **Q22. What is the difference between Comparable and Comparator?**

**Feature**

**Comparable**

**Comparator**

**Package**

**java.lang**

**java.util**

**Method**

**compareTo(T o)**

**compare(T o1, T o2)**

**Modifies class?**

**Yes (class must implement it)**

**No (external, separate class/lambda)**

**Sorting**

**Natural ordering (single)**

**Custom ordering (multiple)**

**Usage**

**Collections.sort(list)**

**Collections.sort(list, comparator)**

_**// Comparable — natural ordering (built into the class)**_

**public class Employee implements Comparable {**

**private int id;**

**private String name;**

**private double salary;**

**@Override**

**public int compareTo(Employee other) {**

**return Integer.compare(this.id, other.id);**_**// sort by ID naturally**_

**}**

**}**

**List employees = getEmployees();**

**Collections.sort(employees);**_**// uses compareTo — sorts by ID**_

_**// Comparator — external, multiple sort strategies**_

**Comparator byName = Comparator.comparing(Employee::getName);**

**Comparator bySalaryDesc = Comparator.comparingDouble(Employee::getSalary).reversed();**

**Comparator byNameThenSalary = Comparator**

**.comparing(Employee::getName)**

**.thenComparingDouble(Employee::getSalary);**

**employees.sort(byName);**_**// sort by name**_

**employees.sort(bySalaryDesc);**_**// sort by salary descending**_

**employees.sort(byNameThenSalary);**_**// sort by name, break ties by salary**_

_**// Null-safe comparator**_

**Comparator nullSafe = Comparator.nullsLast(**

**Comparator.comparing(Employee::getName)**

**);**

**PART 8: Java 8+ Collections Features**
----------------------------------------

### **Q23. What are the important Java 8+ enhancements to Collections?**

**1\. forEach() method:**

**List names = List.of("Alice", "Bob", "Charlie");**

**names.forEach(name -> System.out.println(name));**

**names.forEach(System.out::println);**_**// method reference**_

**2\. Stream API — transform, filter, aggregate:**

**List orders = getOrders();**

_**// Real-world: total revenue from completed orders**_

**double totalRevenue = orders.stream()**

**.filter(o -> o.getStatus() == Status.COMPLETED)**

**.mapToDouble(Order::getAmount)**

**.sum();**

_**// Group orders by status**_

**Map\> byStatus = orders.stream()**

**.collect(Collectors.groupingBy(Order::getStatus));**

_**// Top 5 highest-value orders**_

**List topOrders = orders.stream()**

**.sorted(Comparator.comparingDouble(Order::getAmount).reversed())**

**.limit(5)**

**.toList();**

**3\. Map enhancements:**

**Map\> userRoles = new HashMap<>();**

_**// computeIfAbsent — lazy initialization**_

**userRoles.computeIfAbsent("alice", k -> new ArrayList<>()).add("ADMIN");**

_**// merge — combine values**_

**Map wordCount = new HashMap<>();**

**for (String word : words) {**

**wordCount.merge(word, 1, Integer::sum);**_**// increment or set to 1**_

**}**

_**// getOrDefault**_

**int count = wordCount.getOrDefault("missing", 0);**

_**// replaceAll**_

**wordCount.replaceAll((key, val) -> val \* 2);**

**4\. Immutable collections (Java 9+):**

**List list = List.of("A", "B", "C");**_**// immutable**_

**Set set = Set.of("A", "B", "C");**_**// immutable**_

**Map map = Map.of("A", 1, "B", 2);**_**// immutable**_

**list.add("D");**_**// throws UnsupportedOperationException!**_

_**// Java 10+**_

**List copy = List.copyOf(mutableList);**_**// immutable copy**_

**5\. toList() (Java 16+):**

_**// Before Java 16**_

**List result = stream.collect(Collectors.toList());**

_**// or**_

**List result = stream.collect(Collectors.toUnmodifiableList());**

_**// Java 16+**_

**List result = stream.toList();**_**// unmodifiable list**_

### **Q24. What are the differences between stream() and parallelStream()?**

**List numbers = IntStream.rangeClosed(1, 1\_000\_000).boxed().toList();**

_**// Sequential stream — single thread**_

**long sum1 = numbers.stream()**

**.filter(n -> n % 2 == 0)**

**.mapToLong(Integer::longValue)**

**.sum();**

_**// Parallel stream — uses ForkJoinPool (multiple threads)**_

**long sum2 = numbers.parallelStream()**

**.filter(n -> n % 2 == 0)**

**.mapToLong(Integer::longValue)**

**.sum();**

**When to use parallelStream():**

***Large datasets (100K+ elements)**

***CPU-intensive operations (not I/O)**

***Operations are stateless and independent**

***No shared mutable state**

**When NOT to use:**

***Small datasets (overhead > benefit)**

***I/O operations (threads block, wasted)**

***Order matters and operations are interdependent**

***Shared mutable state (race conditions)**

_**// DANGEROUS — race condition with parallelStream**_

**List results = new ArrayList<>();**_**// not thread-safe!**_

**numbers.parallelStream()**

**.filter(n -> n % 2 == 0)**

**.forEach(results::add);**_**// UNSAFE — concurrent writes to ArrayList**_

_**// SAFE**_

**List results = numbers.parallelStream()**

**.filter(n -> n % 2 == 0)**

**.toList();**_**// collector handles thread-safety**_

**PART 9: Real-World Scenario-Based Questions**
-----------------------------------------------

### **Q25. You need to store 10 million user records for fast lookup by user ID. Which collection and why?**

**Answer: HashMap (or ConcurrentHashMap if multi-threaded).Why:**

***get(userId) is O(1) average**

***10M entries with load factor 0.75 → allocates ~13.3M buckets**

***Pre-size to avoid resizing: new HashMap<>(14\_000\_000, 0.75f)**

_**// Pre-sized for 10M entries**_

**Map userCache = new HashMap<>(14\_000\_000, 0.75f);**

_**// Or if multi-threaded**_

**Map userCache = new ConcurrentHashMap<>(14\_000\_000, 0.75f, 16);**

_**// ^**_

_**// concurrency level**_

**Memory estimate: ~10M entries × ~80 bytes per entry ≈ 800MB. Consider off-heap solutions (MapDB, Chronicle Map) if memory is a concern.**

### **Q26. How would you find the first non-repeated character in a String using Collections?**

**public char firstNonRepeated(String str) {**

_**// LinkedHashMap maintains insertion order**_

**Map charCount = new LinkedHashMap<>();**

**for (char c : str.toCharArray()) {**

**charCount.merge(c, 1, Integer::sum);**

**}**

**return charCount.entrySet().stream()**

**.filter(e -> e.getValue() == 1)**

**.map(Map.Entry::getKey)**

**.findFirst()**

**.orElseThrow(() -> new IllegalArgumentException("No non-repeated character"));**

**}**

_**// "swiss" → 'w'**_

_**// LinkedHashMap preserves order, so first entry with count 1 is the answer**_

**Why LinkedHashMap? HashMap doesn't preserve order — you'd lose track of which non-repeated character came first.**

### **Q27. How do you make a collection read-only / immutable?**

**Three levels of "unmodifiable":**

_**// Level 1: Unmodifiable VIEW (Java 2+)**_

**List original = new ArrayList<>(List.of("A", "B"));**

**List unmodifiable = Collections.unmodifiableList(original);**

**unmodifiable.add("C");**_**// UnsupportedOperationException!**_

**original.add("C");**_**// WORKS — and unmodifiable now shows \["A", "B", "C"\]!**_

_**// ↑ This is a VIEW, not a copy!**_

_**// Level 2: Truly immutable COPY (Java 10+)**_

**List immutable = List.copyOf(original);**

**original.add("D");**_**// original changes**_

**immutable.size();**_**// still 3 — independent copy**_

_**// Level 3: Immutable from creation (Java 9+)**_

**List list = List.of("A", "B", "C");**_**// immutable**_

**Map map = Map.of("x", 1, "y", 2);**_**// immutable**_

**Set set = Set.of("A", "B");**_**// immutable**_
### **Q28. What is the difference between Iterator and ListIterator?**

**Feature**

**Iterator**

**ListIterator**

**Direction**

**Forward only**

**Bidirectional (forward + backward)**

**Works on**

**All Collection types**

**Only List**

**Add element**

**No**

**add(E e) — yes**

**Set element**

**No**

**set(E e) — replace current**

**Get index**

**No**

**nextIndex(), previousIndex()**

**List list = new ArrayList<>(List.of("A", "B", "C", "D"));**

**ListIterator it = list.listIterator(list.size());**_**// start at end**_

**while (it.hasPrevious()) {**

**String s = it.previous();**

**System.out.print(s + " ");**_**// D C B A — reverse iteration!**_

**}**

### **Q29. What is an IdentityHashMap and when would you use it?**

**Regular HashMap: Uses equals() for key comparison.IdentityHashMap: Uses reference equality (==) instead of equals().**

**String s1 = new String("hello");**

**String s2 = new String("hello");**

_**// Regular HashMap**_

**Map hashMap = new HashMap<>();**

**hashMap.put(s1, 1);**

**hashMap.put(s2, 2);**

**System.out.println(hashMap.size());**_**// 1 — because s1.equals(s2)**_

_**// IdentityHashMap**_

**Map identityMap = new IdentityHashMap<>();**

**identityMap.put(s1, 1);**

**identityMap.put(s2, 2);**

**System.out.println(identityMap.size());**_**// 2 — because s1 != s2 (different objects)**_

**Use case: Serialization frameworks, object graph traversal (track visited objects by reference, not equality).**

### **Q30. What is an EnumSet and EnumMap? Why are they special?**

**EnumSet — A Set implementation optimized for enums, using a bit vector internally.EnumMap — A Map implementation with enum keys, using a simple array internally.**

**public enum Permission { READ, WRITE, EXECUTE, DELETE, ADMIN }**

_**// EnumSet — stored as a single long (bit flags), incredibly fast**_

**Set userPerms = EnumSet.of(Permission.READ, Permission.WRITE);**

**Set allPerms = EnumSet.allOf(Permission.class);**

**Set noPerms = EnumSet.noneOf(Permission.class);**

_**// Check permission — O(1) bitwise operation**_

**if (userPerms.contains(Permission.WRITE)) {**_**/\* ... \*/**_ **}**

_**// EnumMap — array-backed, no hashing needed**_

**Map descriptions = new EnumMap<>(Permission.class);**

**descriptions.put(Permission.READ, "Can read resources");**

**descriptions.put(Permission.ADMIN, "Full access");**

**Why use them?**

***EnumSet is 10-100x faster than HashSet for enums (bit operations vs hashing)**

***EnumMap is faster and more memory-efficient than HashMap for enum keys**

***Both are type-safe and prevent accidental mixing of enum types**

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
```

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