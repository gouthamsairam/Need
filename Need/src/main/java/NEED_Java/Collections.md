Absolutely. For a **Senior Java Developer**, Collections interviews are usually not limited to definitions such as `ArrayList vs LinkedList`. Interviewers focus heavily on **real-world scenarios, performance, concurrency, Java internals, design decisions, and follow-up questions**.

Below is a senior-level scenario-based interview set.

# Java Collections — Senior Developer Scenario-Based Interview Questions & Answers

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
add()    → O(1)
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
add()      → O(log n)
remove()   → O(log n)
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

| Collection    | Unique | Ordering                      |
| ------------- | ------ | ----------------------------- |
| HashSet       | Yes    | No guaranteed iteration order |
| LinkedHashSet | Yes    | Insertion order               |
| TreeSet       | Yes    | Sorted order                  |

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
get()  → O(1)
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
get()  → O(log n)
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
IT       → [Employee1, Employee2]
HR       → [Employee3]
Finance  → [Employee4, Employee5]
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

| Requirement                 | Choice                  |
| --------------------------- | ----------------------- |
| Fast indexed access         | `ArrayList`             |
| Unique elements             | `HashSet`               |
| Unique + insertion order    | `LinkedHashSet`         |
| Unique + sorted             | `TreeSet`               |
| Key-value lookup            | `HashMap`               |
| Key-value + insertion order | `LinkedHashMap`         |
| Key-value + sorted keys     | `TreeMap`               |
| Stack/deque                 | `ArrayDeque`            |
| Priority processing         | `PriorityQueue`         |
| Concurrent Map              | `ConcurrentHashMap`     |
| Read-heavy concurrent List  | `CopyOnWriteArrayList`  |
| Blocking producer-consumer  | `BlockingQueue`         |
| Concurrent sorted Map       | `ConcurrentSkipListMap` |

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
