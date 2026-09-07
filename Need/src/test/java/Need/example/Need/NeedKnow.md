Absolutely. The main confusion here is **what `a` and `b` actually represent** inside `Arrays.sort()`.

Let's build a clear cheat sheet from the discussion.

## 1. First understand the basic rule

The type of `arr` determines what `a` and `b` are.

```java
Arrays.sort(arr, (a, b) -> ...);
```

Think:

> **What is one element of `arr`?**
> That is what `a` and `b` will be.

---

# 2. `int[]` — array of primitive integers

```java
int[] arr = {5, 2, 8, 1, 3};
```

Each element is an `int`.

So conceptually:

```text
a = 5
b = 2
```

### Ascending

```java
Arrays.sort(arr);
```

Output:

```text
[1, 2, 3, 5, 8]
```

### ❌ Don't do this

```java
Arrays.sort(arr, (a, b) -> a - b);
```

Why?

`Arrays.sort()` for primitive `int[]` doesn't accept a `Comparator`.

---

# 3. `Integer[]` — array of Integer objects

```java
Integer[] arr = {5, 2, 8, 1, 3};
```

Now each element is an `Integer`.

So:

```text
a = Integer
b = Integer
```

You can use a comparator.

### Ascending

```java
Arrays.sort(arr, (a, b) -> a - b);
```

or better:

```java
Arrays.sort(arr, Integer::compare);
```

### Descending

```java
Arrays.sort(arr, (a, b) -> b - a);
```

Better:

```java
Arrays.sort(arr, (a, b) -> Integer.compare(b, a));
```

Result:

```text
[8, 5, 3, 2, 1]
```

---

# 4. `String[]`

```java
String[] arr = {"cat", "apple", "dog", "banana"};
```

Here:

```text
a = String
b = String
```

### Alphabetical order

```java
Arrays.sort(arr);
```

Output:

```text
[apple, banana, cat, dog]
```

### Using comparator

```java
Arrays.sort(arr, (a, b) -> a.compareTo(b));
```

Same result.

### Reverse alphabetical

```java
Arrays.sort(arr, (a, b) -> b.compareTo(a));
```

Output:

```text
[dog, cat, banana, apple]
```

Or:

```java
Arrays.sort(arr, Comparator.reverseOrder());
```

---

# 5. `int[][]` — 2D integer array

This is where your original error happened.

```java
int[][] arr = {
    {6, 2},
    {6, 5},
    {1, 2},
    {7, 3}
};
```

Here one element of `arr` is:

```java
int[]
```

Therefore:

```text
a = int[]
b = int[]
```

NOT:

```text
a = int
b = int
```

That's why this fails:

```java
Arrays.sort(arr, (a, b) -> a - b);  // ❌
```

You cannot subtract two arrays.

---

# 6. Sort 2D array using first column

If you want:

```text
{6,2}
{6,5}
{1,2}
{7,3}
```

sorted by the first value:

```java
Arrays.sort(arr, (a, b) -> a[0] - b[0]);
```

Result:

```text
{1,2}
{6,2}
{6,5}
{7,3}
```

Better:

```java
Arrays.sort(arr, (a, b) -> Integer.compare(a[0], b[0]));
```

---

# 7. Sort 2D array using second column

```java
Arrays.sort(arr, (a, b) -> Integer.compare(a[1], b[1]));
```

For:

```text
{6,2}
{6,5}
{1,2}
{7,3}
```

Result:

```text
{6,2}
{1,2}
{7,3}
{6,5}
```

---

# 8. Sort 2D array by first column, then second column

This is **very important for LeetCode/interviews**.

Given:

```java
int[][] arr = {
    {6, 5},
    {1, 2},
    {6, 2},
    {7, 3}
};
```

You want:

```text
{1,2}
{6,2}
{6,5}
{7,3}
```

Use:

```java
Arrays.sort(arr, (a, b) -> {
    if (a[0] != b[0]) {
        return Integer.compare(a[0], b[0]);
    }
    return Integer.compare(a[1], b[1]);
});
```

Or:

```java
Arrays.sort(arr,
    Comparator.comparingInt((int[] a) -> a[0])
              .thenComparingInt(a -> a[1])
);
```

---

# 9. Sort 2D array by first column descending

```java
Arrays.sort(arr, (a, b) -> Integer.compare(b[0], a[0]));
```

Example:

```text
{6,2}
{1,5}
{7,3}
```

becomes:

```text
{7,3}
{6,2}
{1,5}
```

---

# 10. Sort objects by a property

This is another **very important real-world use case**.

Suppose:

```java
class Employee {
    int id;
    String name;
    int salary;
}
```

And:

```java
Employee[] employees = ...;
```

### Sort by salary

```java
Arrays.sort(employees,
    Comparator.comparingInt(e -> e.salary)
);
```

### Sort by salary descending

```java
Arrays.sort(employees,
    Comparator.comparingInt((Employee e) -> e.salary).reversed()
);
```

### Sort by name

```java
Arrays.sort(employees,
    Comparator.comparing(e -> e.name)
);
```

---

# 11. Sort objects by multiple fields

Suppose employees have:

```text
name
salary
age
```

Sort by salary first, then age:

```java
Arrays.sort(employees,
    Comparator.comparingInt((Employee e) -> e.salary)
              .thenComparingInt(e -> e.age)
);
```

This is similar to:

```java
ORDER BY salary, age
```

in SQL.

---

# 12. `List<Integer>` — use `Collections.sort()`

If you have:

```java
List<Integer> list = Arrays.asList(5, 2, 8, 1);
```

### Ascending

```java
Collections.sort(list);
```

or:

```java
list.sort(Integer::compare);
```

### Descending

```java
list.sort((a, b) -> Integer.compare(b, a));
```

or:

```java
list.sort(Comparator.reverseOrder());
```

---

# 13. `List<String>`

```java
List<String> list =
    Arrays.asList("cat", "apple", "dog");
```

### Ascending

```java
list.sort(String::compareTo);
```

or simply:

```java
Collections.sort(list);
```

### Descending

```java
list.sort(Comparator.reverseOrder());
```

---

# 14. `List<Employee>`

For custom objects:

```java
List<Employee> employees = ...;
```

### Salary ascending

```java
employees.sort(
    Comparator.comparingInt(e -> e.salary)
);
```

### Salary descending

```java
employees.sort(
    Comparator.comparingInt((Employee e) -> e.salary)
              .reversed()
);
```

---

# 15. `Arrays.sort()` vs `Collections.sort()` vs `List.sort()`

This is a good thing to remember:

| Data             | Use                                  |
| ---------------- | ------------------------------------ |
| `int[]`          | `Arrays.sort(arr)`                   |
| `double[]`       | `Arrays.sort(arr)`                   |
| `char[]`         | `Arrays.sort(arr)`                   |
| `Integer[]`      | `Arrays.sort(arr, comparator)`       |
| `String[]`       | `Arrays.sort(arr, comparator)`       |
| `int[][]`        | `Arrays.sort(arr, comparator)`       |
| `Employee[]`     | `Arrays.sort(arr, comparator)`       |
| `List<Integer>`  | `list.sort()` / `Collections.sort()` |
| `List<String>`   | `list.sort()` / `Collections.sort()` |
| `List<Employee>` | `list.sort(comparator)`              |

---

# 16. The most important concept: what are `a` and `b`?

Memorize this table:

| Array            | `a`        | `b`        |
| ---------------- | ---------- | ---------- |
| `int[]`          | `int`      | `int`      |
| `Integer[]`      | `Integer`  | `Integer`  |
| `String[]`       | `String`   | `String`   |
| `int[][]`        | `int[]`    | `int[]`    |
| `Employee[]`     | `Employee` | `Employee` |
| `List<Integer>`  | `Integer`  | `Integer`  |
| `List<String>`   | `String`   | `String`   |
| `List<Employee>` | `Employee` | `Employee` |

So when you see:

```java
(a, b) -> ...
```

**first ask:**

> "What is the type of ONE element?"

That immediately tells you what you can do with `a` and `b`.

---

# 17. Your specific examples

### Case 1

```java
int[] arr = {5, 2, 8};
```

Use:

```java
Arrays.sort(arr);
```

---

### Case 2

```java
Integer[] arr = {5, 2, 8};
```

Use:

```java
Arrays.sort(arr, (a, b) -> Integer.compare(a, b));
```

---

### Case 3

```java
int[][] arr = {
    {5, 2},
    {8, 1},
    {3, 4}
};
```

Sort by first value:

```java
Arrays.sort(arr, (a, b) -> Integer.compare(a[0], b[0]));
```

Notice:

```text
a       → {5,2}
a[0]    → 5

b       → {8,1}
b[0]    → 8
```

---

### Case 4

```java
String[] arr = {"dog", "cat", "apple"};
```

Use:

```java
Arrays.sort(arr);
```

---

### Case 5

```java
Employee[] arr = ...;
```

Sort by salary:

```java
Arrays.sort(arr,
    Comparator.comparingInt(e -> e.salary)
);
```

---

## ⭐ Interview shortcut

Whenever you see:

```java
Arrays.sort(arr, (a, b) -> ???);
```

follow these **3 steps**:

```text
        What is arr?
           ↓
   What is ONE element?
           ↓
     What are a and b?
           ↓
   Compare the required field
```

For example:

```java
int[][] arr
```

↓

```text
One element = int[]
```

↓

```text
a = int[]
b = int[]
```

↓

Want first element?

```java
a[0] vs b[0]
```

↓

```java
Arrays.sort(arr,
    (a, b) -> Integer.compare(a[0], b[0])
);
```

**This "what are `a` and `b`?" approach will remove most of the confusion around `Comparator`, `Arrays.sort()`, `List.sort()`, and Java sorting questions.**
`.boxed()` is used in Java Streams to **convert primitive values into their corresponding wrapper objects**.

### Simple example

Suppose you have:

```java
int[] arr = {10, 20, 30};
```

When you do:

```java
Arrays.stream(arr)
```

you get an **`IntStream`**, which contains primitive `int` values.

If you use:

```java
Arrays.stream(arr).boxed()
```

the `int` values are converted to `Integer` objects:

```text
int → Integer
10  → Integer(10)
20  → Integer(20)
30  → Integer(30)
```

### Why do we need it?

Because many Java APIs work with **objects**, not primitives.

For example, suppose you want to convert `int[]` to `Integer[]`:

```java
int[] arr = {5, 2, 8, 1};

Integer[] result = Arrays.stream(arr)
                         .boxed()
                         .toArray(Integer[]::new);
```

Now `result` is:

```java
Integer[] 
```

So you can use a `Comparator`:

```java
Arrays.sort(result, (a, b) -> Integer.compare(b, a));
```

This is related directly to your previous question:

```text
int[] 
  ↓
Arrays.stream()
  ↓
IntStream
  ↓
.boxed()
  ↓
Stream<Integer>
  ↓
Integer[]
```

### `.boxed()` vs `mapToObj()`

For an `IntStream`, `.boxed()` is the convenient way to get `Stream<Integer>`:

```java
Stream<Integer> stream = Arrays.stream(arr).boxed();
```

You could technically do:

```java
Stream<Integer> stream =
    Arrays.stream(arr).mapToObj(Integer::valueOf);
```

but `.boxed()` is simpler and specifically designed for this conversion.

### ⭐ Remember

```text
.boxed() = primitive stream → object stream
```

Common conversions:

```text
IntStream     → Stream<Integer>
LongStream    → Stream<Long>
DoubleStream  → Stream<Double>
```

So whenever you see:

```java
Arrays.stream(intArray).boxed()
```

think:

> **"Convert my primitive `int` values into `Integer` objects."**
There are **two mistakes** in this line:

```java
Collections.sort((a, b) -> Integer.compare(a, b));
```

### Mistake 1: Missing the list

`Collections.sort()` needs the **collection/list that you want to sort** as the first argument.

Its basic syntax is:

```java
Collections.sort(list);
```

or with a comparator:

```java
Collections.sort(list, comparator);
```

So you need something like:

```java
List<Integer> list = Arrays.asList(5, 2, 8, 1);

Collections.sort(list, (a, b) -> Integer.compare(a, b));
```

Output:

```text
[1, 2, 5, 8]
```

---

### Mistake 2: Understanding `(a, b)`

In:

```java
(a, b) -> Integer.compare(a, b)
```

`a` and `b` are **elements from the list**.

For:

```java
List<Integer> list = Arrays.asList(5, 2, 8, 1);
```

Java essentially compares:

```text
5 vs 2
8 vs 1
...
```

So:

```java
Integer.compare(a, b)
```

is a valid comparator.

---

## Correct ways

### 1. Default ascending

You don't even need a comparator:

```java
Collections.sort(list);
```

### 2. Ascending with comparator

```java
Collections.sort(list, (a, b) -> Integer.compare(a, b));
```

### 3. Descending

```java
Collections.sort(list, (a, b) -> Integer.compare(b, a));
```

### 4. Modern preferred approach

Instead of:

```java
Collections.sort(list, (a, b) -> Integer.compare(a, b));
```

you can simply do:

```java
list.sort(Integer::compare);
```

And descending:

```java
list.sort((a, b) -> Integer.compare(b, a));
```

### Remember the syntax

```text
Collections.sort(
    LIST,          ← what should be sorted
    COMPARATOR     ← how should it be sorted
);
```

Therefore:

```java
Collections.sort(list, (a, b) -> Integer.compare(a, b));
```

is correct.
In Java, **`Comparable` and `Comparator` are both used for sorting objects**, but the main difference is **where you define the sorting logic**.

A simple way to remember:

> **Comparable = "I know how to compare myself."**
> **Comparator = "I know how to compare two objects."**

---

## 1. Comparable

`Comparable` is an interface in:

```java
java.lang.Comparable
```

It has one important method:

```java
int compareTo(T o)
```

You implement it **inside the class whose objects you want to sort**.

### Example

Suppose we have students:

```java
class Student implements Comparable<Student> {

    int age;
    String name;

    Student(int age, String name) {
        this.age = age;
        this.name = name;
    }

    @Override
    public int compareTo(Student s) {
        return Integer.compare(this.age, s.age);
    }
}
```

Now:

```java
List<Student> students = new ArrayList<>();

students.add(new Student(25, "Gowtham"));
students.add(new Student(20, "Rahul"));
students.add(new Student(30, "Suresh"));

Collections.sort(students);
```

The result is sorted by **age**:

```text
20 Rahul
25 Gowtham
30 Suresh
```

Why?

Because `Student` implements:

```java
Comparable<Student>
```

and Java calls:

```java
student1.compareTo(student2)
```

---

# 2. Comparator

`Comparator` is an interface in:

```java
java.util.Comparator
```

It has:

```java
int compare(T o1, T o2)
```

The important difference is that the comparison logic is **outside the class**.

For example, our `Student` class doesn't need to implement `Comparable`.

```java
class Student {

    int age;
    String name;

    Student(int age, String name) {
        this.age = age;
        this.name = name;
    }
}
```

Now suppose we want to sort by age:

```java
Comparator<Student> byAge =
        (s1, s2) -> Integer.compare(s1.age, s2.age);

Collections.sort(students, byAge);
```

Or using Java's modern syntax:

```java
students.sort(Comparator.comparingInt(s -> s.age));
```

---

# 3. The biggest difference

Consider this Student class:

```java
class Student {

    int age;
    String name;
    double salary;
}
```

Suppose you need:

```text
Sort by age
Sort by name
Sort by salary
Sort by age descending
Sort by salary descending
```

With `Comparable`, you normally define **one natural ordering**:

```java
@Override
public int compareTo(Student s) {
    return Integer.compare(this.age, s.age);
}
```

Now `Collections.sort(students)` always uses age.

But with `Comparator`, you can create multiple sorting strategies:

```java
Comparator<Student> byAge =
        (s1, s2) -> Integer.compare(s1.age, s2.age);

Comparator<Student> byName =
        (s1, s2) -> s1.name.compareTo(s2.name);

Comparator<Student> bySalary =
        (s1, s2) -> Double.compare(s1.salary, s2.salary);
```

Then:

```java
students.sort(byAge);
```

or:

```java
students.sort(byName);
```

or:

```java
students.sort(bySalary);
```

That's why **Comparator is more flexible**.

---

# 4. `compareTo()` vs `compare()`

This is very important for interviews.

### Comparable

```java
s1.compareTo(s2)
```

There are **two objects involved**, but the method belongs to `s1`.

```java
s1.compareTo(s2)
│       │
│       └── object being compared
└── current object
```

### Comparator

```java
comparator.compare(s1, s2)
```

Both objects are passed as parameters:

```java
compare(s1, s2)
       │   │
       │   └── second object
       └────── first object
```

---

# 5. Return values

Both `compareTo()` and `compare()` return an `int`.

### Negative

```text
object1 < object2
```

### Zero

```text
object1 == object2
```

### Positive

```text
object1 > object2
```

For example:

```java
Integer.compare(10, 20)
```

returns a negative number.

```java
Integer.compare(20, 20)
```

returns `0`.

```java
Integer.compare(30, 20)
```

returns a positive number.

You generally **shouldn't depend on the exact negative/positive value**, only its sign.

---

# 6. Why `Integer.compare()` is preferred

You may have seen:

```java
return this.age - s.age;
```

This works for many normal values, but it can cause **integer overflow**.

For example:

```java
Integer.MAX_VALUE - (-1)
```

can overflow.

Instead use:

```java
return Integer.compare(this.age, s.age);
```

This is safer and is the recommended approach.

---

# 7. Comparator with Lambda

This is very common in modern Java.

```java
students.sort((s1, s2) ->
        Integer.compare(s1.age, s2.age));
```

You can also write:

```java
students.sort(Comparator.comparingInt(s -> s.age));
```

For strings:

```java
students.sort((s1, s2) ->
        s1.name.compareTo(s2.name));
```

or:

```java
students.sort(Comparator.comparing(s -> s.name));
```

---

# 8. Descending order

### Comparable

If your natural ordering is ascending:

```java
@Override
public int compareTo(Student s) {
    return Integer.compare(this.age, s.age);
}
```

For descending order, you would need to change the implementation or otherwise reverse the ordering.

### Comparator

Very easy:

```java
students.sort(
    Comparator.comparingInt((Student s) -> s.age).reversed()
);
```

Now:

```text
30 Suresh
25 Gowtham
20 Rahul
```

---

# 9. Multiple conditions

This is where `Comparator` becomes extremely powerful.

Suppose:

> Sort students by age, and if age is the same, sort by name.

You can write:

```java
students.sort(
    Comparator.comparingInt((Student s) -> s.age)
              .thenComparing(s -> s.name)
);
```

Example:

```text
20 Amit
20 Rahul
25 Gowtham
25 Suresh
30 Kiran
```

First it compares:

```text
age
```

If age is equal, it compares:

```text
name
```

---

# 10. Quick comparison

| Feature                      | Comparable                                   | Comparator                 |
| ---------------------------- | -------------------------------------------- | -------------------------- |
| Package                      | `java.lang`                                  | `java.util`                |
| Method                       | `compareTo()`                                | `compare()`                |
| Logic location               | Inside class                                 | Outside class              |
| Number of sorting strategies | Usually one natural order                    | Multiple                   |
| Modifies original class?     | Yes                                          | No                         |
| Lambda                       | ❌ Not normally used for direct sorting logic | ✅ Very common              |
| Multiple sorting fields      | Less flexible                                | Very flexible              |
| Best for                     | Natural/default ordering                     | Custom/different orderings |

---

## Interview answer

If the interviewer asks:

**"What is the difference between Comparable and Comparator?"**

You can answer:

> **Comparable is used to define the natural ordering of a class by implementing `compareTo()` inside the class itself. Comparator is used to define custom or multiple sorting strategies externally using the `compare()` method. Comparable is suitable when an object has one natural order, while Comparator is more flexible when we need different ways of sorting the same objects.**

### One-line memory trick

```text
Comparable → compareTo() → inside the class → natural ordering

Comparator → compare()   → outside the class → custom ordering
```

If you're preparing for Java interviews, the next important topic after this is **how `Comparable`, `Comparator`, `Collections.sort()`, `List.sort()`, `TreeSet`, and `TreeMap` interact with each other**.
Yes. This is a **very important Java interview topic**, especially when working with `HashMap`, `HashSet`, collections, and debugging.

The key idea is:

> `equals()` → tells Java whether two objects are logically equal.
> `hashCode()` → helps Java find objects efficiently in hash-based collections.
> `toString()` → gives a readable representation of an object.

Let's understand why we override them.

---

# 1. What happens if we don't override `equals()`?

Consider:

```java
class Employee {
    int id;
    String name;

    Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
```

Now:

```java
Employee e1 = new Employee(101, "Gowtham");
Employee e2 = new Employee(101, "Gowtham");

System.out.println(e1.equals(e2));
```

You might expect:

```text
true
```

because both have:

```text
id   = 101
name = Gowtham
```

But the result is:

```text
false
```

Why?

Because `Employee` inherits `equals()` from `Object`.

The default implementation essentially checks whether they are the **same object/reference**.

```text
e1 ───────> Employee object #1
                 id=101
                 name=Gowtham

e2 ───────> Employee object #2
                 id=101
                 name=Gowtham
```

They contain the same data, but they are different objects.

So:

```java
e1.equals(e2)
```

is `false`.

---

# 2. Why override `equals()`?

If your business definition says:

> Two employees are equal when their IDs are equal.

Then you should override `equals()`.

```java
@Override
public boolean equals(Object obj) {

    if (this == obj)
        return true;

    if (obj == null || getClass() != obj.getClass())
        return false;

    Employee employee = (Employee) obj;

    return id == employee.id;
}
```

Now:

```java
Employee e1 = new Employee(101, "Gowtham");
Employee e2 = new Employee(101, "Gowtham");

System.out.println(e1.equals(e2));
```

Output:

```text
true
```

Because now you're saying:

```text
Employee equality = same id
```

---

# 3. Then why do we need `hashCode()`?

This is where things become very important.

Suppose:

```java
Set<Employee> employees = new HashSet<>();

Employee e1 = new Employee(101, "Gowtham");
Employee e2 = new Employee(101, "Gowtham");

employees.add(e1);
employees.add(e2);

System.out.println(employees.size());
```

You might expect:

```text
1
```

because `e1.equals(e2)` is `true`.

But if you override only `equals()` and **not `hashCode()`**, you can get:

```text
2
```

Why?

Because `HashSet` uses **both `hashCode()` and `equals()`**.

---

# 4. How HashSet works

When you do:

```java
employees.add(e1);
```

Java roughly does:

```text
             e1
              ↓
         hashCode()
              ↓
          bucket #5
              ↓
          store object
```

When you add `e2`:

```text
             e2
              ↓
         hashCode()
              ↓
          bucket #?
```

If `e1` and `e2` have different hash codes, Java may put them into different buckets.

It doesn't necessarily call `equals()` between them.

So even if:

```java
e1.equals(e2) == true
```

different hash codes can cause incorrect behavior in hash-based collections.

---

# 5. The important contract

Java has a very important rule:

> **If two objects are equal according to `equals()`, they MUST have the same `hashCode()`.**

So:

```java
e1.equals(e2) == true
```

must imply:

```java
e1.hashCode() == e2.hashCode()
```

But the reverse is NOT required.

Two objects can have the same hash code but not be equal.

```text
Same equals
    ↓
MUST have
    ↓
Same hashCode
```

But:

```text
Same hashCode
    ↓
Does NOT mean
    ↓
equals() == true
```

This is called a **hash collision**.

---

# 6. Correct implementation

So if we define equality based on `id`, both methods should use `id`.

```java
class Employee {

    int id;
    String name;

    Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        Employee employee = (Employee) obj;

        return id == employee.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
```

Now:

```java
Set<Employee> employees = new HashSet<>();

employees.add(new Employee(101, "Gowtham"));
employees.add(new Employee(101, "Gowtham"));

System.out.println(employees.size());
```

Output:

```text
1
```

---

# 7. Why `HashMap` also needs them

Consider:

```java
Map<Employee, String> map = new HashMap<>();

Employee e1 = new Employee(101, "Gowtham");

map.put(e1, "Java Developer");
```

Later:

```java
Employee e2 = new Employee(101, "Gowtham");

System.out.println(map.get(e2));
```

You want:

```text
Java Developer
```

For that to work correctly, Java needs to recognize:

```java
e1.equals(e2)
```

and:

```java
e1.hashCode() == e2.hashCode()
```

That's why `equals()` and `hashCode()` are particularly important when objects are used as:

* `HashMap` keys
* `HashSet` elements
* `Hashtable` keys
* other hash-based collections

---

# 8. Now `toString()`

`toString()` has a completely different purpose.

Suppose:

```java
Employee e1 = new Employee(101, "Gowtham");

System.out.println(e1);
```

Without overriding `toString()`, you might see something like:

```text
Employee@5e2de80c
```

This comes from `Object.toString()`.

It is not very useful to humans.

---

# 9. Override `toString()`

You can write:

```java
@Override
public String toString() {
    return "Employee{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
}
```

Now:

```java
System.out.println(e1);
```

gives:

```text
Employee{id=101, name='Gowtham'}
```

Much easier to understand.

---

# 10. Why is `toString()` useful?

It's mainly for **debugging, logging, and displaying object information**.

For example:

```java
List<Employee> employees = new ArrayList<>();

employees.add(new Employee(101, "Gowtham"));
employees.add(new Employee(102, "Rahul"));

System.out.println(employees);
```

If `toString()` is overridden:

```text
[Employee{id=101, name='Gowtham'}, Employee{id=102, name='Rahul'}]
```

Without it:

```text
[Employee@5e2de80c, Employee@1d44bcfa]
```

So `toString()` makes debugging much easier.

---

# 11. All three together

Suppose we have:

```java
class Employee {

    int id;
    String name;

    Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        Employee employee = (Employee) obj;

        return id == employee.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "Employee{id=" + id +
               ", name='" + name + "'}";
    }
}
```

Now each method has a different responsibility:

```text
┌─────────────┬─────────────────────────────────────┐
│ Method      │ Purpose                             │
├─────────────┼─────────────────────────────────────┤
│ equals()    │ Logical equality                    │
│ hashCode()  │ Hash-based collection lookup        │
│ toString()  │ Human-readable object representation│
└─────────────┴─────────────────────────────────────┘
```

---

# 12. Very important interview scenario

An interviewer may ask:

**"Why should we override hashCode when we override equals?"**

Good answer:

> When two objects are logically equal according to `equals()`, they must return the same hash code. Hash-based collections such as `HashMap` and `HashSet` first use `hashCode()` to identify the bucket and then use `equals()` to determine equality. If we override `equals()` without consistently overriding `hashCode()`, equal objects can be placed in different buckets and hash-based collections may behave incorrectly.

---

# 13. One more important point: mutable fields

Be careful when using mutable fields in `equals()` and `hashCode()`.

For example:

```java
Employee e = new Employee(101, "Gowtham");

Set<Employee> set = new HashSet<>();

set.add(e);
```

If your `hashCode()` is based on `id`, and then you change `id`:

```java
e.id = 200;
```

the object's hash code changes.

The `HashSet` may have stored the object in the bucket corresponding to:

```text
id = 101
```

but now you're trying to find it using:

```text
id = 200
```

This can lead to surprising behavior.

That's why fields used in `equals()`/`hashCode()` are ideally **immutable** while the object is being used as a hash key/set element.

---

## The easiest way to remember

```text
equals()
   ↓
"Are these two objects logically the same?"

hashCode()
   ↓
"Which bucket should this object go to?"

toString()
   ↓
"How should I display this object to a human?"
```

And the most important rule:

```text
If A.equals(B) == true
        ↓
A.hashCode() MUST == B.hashCode()
```

**`equals()` + `hashCode()` → collections correctness**

**`toString()` → readability/debugging**

This is one of the core Java concepts you should know before going deeper into `HashMap`, `HashSet`, `ConcurrentHashMap`, and Java collections.
Absolutely. In Java, there are several ways to **sort a `List`**, and understanding them is important for interviews.

## 1. Sort a List of Strings

```java
import java.util.*;

public class Main {
    public static void main(String[] args) {

        List<String> names = Arrays.asList(
            "Ravi", "Gowtham", "Anil", "Kiran"
        );

        Collections.sort(names);

        System.out.println(names);
    }
}
```

### Output

```text
[Anil, Gowtham, Kiran, Ravi]
```

`Collections.sort()` sorts Strings in **natural ascending order**.

---

## 2. Using `list.sort()`

Modern Java provides a simpler way:

```java
List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 3);

numbers.sort(null);

System.out.println(numbers);
```

Output:

```text
[1, 2, 3, 5, 8]
```

Here:

```java
numbers.sort(null);
```

means use the element's **natural ordering**.

You can also write:

```java
numbers.sort(Comparator.naturalOrder());
```

---

## 3. Sort in Descending Order

```java
List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 3);

numbers.sort(Comparator.reverseOrder());

System.out.println(numbers);
```

Output:

```text
[8, 5, 3, 2, 1]
```

Another way:

```java
Collections.sort(numbers, Comparator.reverseOrder());
```

---

# 4. Sort List of Custom Objects

This is very important for interviews.

Suppose we have:

```java
class Employee {
    int id;
    String name;
    double salary;

    Employee(int id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    @Override
    public String toString() {
        return id + " " + name + " " + salary;
    }
}
```

Create a list:

```java
List<Employee> employees = new ArrayList<>();

employees.add(new Employee(101, "Ravi", 50000));
employees.add(new Employee(103, "Kiran", 30000));
employees.add(new Employee(102, "Anil", 70000));
```

Now suppose we want to sort by **salary**.

```java
employees.sort((e1, e2) ->
    Double.compare(e1.salary, e2.salary)
);
```

Output:

```text
102 Anil 70000.0
101 Ravi 50000.0
103 Kiran 30000.0
```

Wait—that ordering is incorrect for ascending salary. The correct output is:

```text
103 Kiran 30000.0
101 Ravi 50000.0
102 Anil 70000.0
```

### Better approach

Use `Comparator.comparing()`:

```java
employees.sort(
    Comparator.comparingDouble(e -> e.salary)
);
```

---

# 5. Sort by Name

```java
employees.sort(
    Comparator.comparing(e -> e.name)
);
```

Output:

```text
102 Anil 70000.0
103 Kiran 30000.0
101 Ravi 50000.0
```

---

# 6. Sort by Name Descending

```java
employees.sort(
    Comparator.comparing((Employee e) -> e.name)
              .reversed()
);
```

Output:

```text
101 Ravi 50000.0
103 Kiran 30000.0
102 Anil 70000.0
```

---

# 7. Sort by Multiple Fields

This is a very common real-world requirement.

Suppose:

> First sort employees by salary, and if salary is the same, sort by name.

```java
employees.sort(
    Comparator.comparingDouble((Employee e) -> e.salary)
              .thenComparing(e -> e.name)
);
```

For example:

```text
101 Ravi 50000
104 Amit 50000
102 Anil 70000
103 Kiran 30000
```

After sorting:

```text
103 Kiran 30000
104 Amit 50000
101 Ravi 50000
102 Anil 70000
```

Because:

1. Salary is sorted first.
2. Ravi and Amit have the same salary.
3. Their names are compared.

---

# 8. `Integer.compare()` vs subtraction

You may see this in interviews:

```java
numbers.sort((a, b) -> a - b);
```

It works for many normal values, but **is not recommended** because integer overflow can happen.

Prefer:

```java
numbers.sort((a, b) -> Integer.compare(a, b));
```

For objects:

```java
employees.sort((e1, e2) ->
    Double.compare(e1.salary, e2.salary)
);
```

### Remember

```java
Integer.compare(a, b)
```

means:

> Compare `a` and `b` safely.

---

## 9. Complete Example

```java
import java.util.*;

class Employee {
    int id;
    String name;
    double salary;

    Employee(int id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    @Override
    public String toString() {
        return id + " " + name + " " + salary;
    }
}

public class Main {

    public static void main(String[] args) {

        List<Employee> employees = new ArrayList<>();

        employees.add(new Employee(101, "Ravi", 50000));
        employees.add(new Employee(103, "Kiran", 30000));
        employees.add(new Employee(102, "Anil", 70000));
        employees.add(new Employee(104, "Amit", 50000));

        // Sort by salary
        employees.sort(
            Comparator.comparingDouble((Employee e) -> e.salary)
        );

        System.out.println(employees);

        // Sort by salary, then name
        employees.sort(
            Comparator.comparingDouble((Employee e) -> e.salary)
                      .thenComparing(e -> e.name)
        );

        System.out.println(employees);

        // Sort by salary descending
        employees.sort(
            Comparator.comparingDouble((Employee e) -> e.salary)
                      .reversed()
        );

        System.out.println(employees);
    }
}
```

### The key sorting patterns to remember

| Requirement             | Code                                   |
| ----------------------- | -------------------------------------- |
| Integer ascending       | `list.sort(null)`                      |
| Integer descending      | `list.sort(Comparator.reverseOrder())` |
| Custom object by field  | `Comparator.comparing(...)`            |
| `int` field             | `Comparator.comparingInt(...)`         |
| `double` field          | `Comparator.comparingDouble(...)`      |
| Descending              | `.reversed()`                          |
| Multiple fields         | `.thenComparing(...)`                  |
| Safe integer comparison | `Integer.compare(a, b)`                |
| Safe double comparison  | `Double.compare(a, b)`                 |

The most important distinction to learn next is **`Comparable` vs `Comparator`**, because that explains *where the sorting logic should live* and why we sometimes use `Collections.sort()` vs a custom `Comparator`.
Yes — exactly. 👍 The important point is:

**`Employee` does NOT need to implement `Comparable` when you provide a `Comparator` separately.**

The newer Java style makes this much easier to understand.

### 1. Your `Employee` class

```java
class Employee {
    int id;
    String name;
    double salary;

    Employee(int id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }
}
```

There is **no `Comparable`** here.

But we can still sort:

```java
employees.sort(
    Comparator.comparingDouble(e -> e.salary)
);
```

Why?

Because you are telling Java:

> "Don't ask Employee how it should be sorted. **I am giving you the sorting rule from outside.**"

---

# 2. Two different approaches

There are basically two ways to define sorting.

### Approach 1 — `Comparable`

The object itself defines its **natural/default ordering**.

```java
class Employee implements Comparable<Employee> {

    int id;
    String name;
    double salary;

    @Override
    public int compareTo(Employee other) {
        return Integer.compare(this.id, other.id);
    }
}
```

Now Java knows:

> Employee's default sorting = sort by `id`.

So you can simply do:

```java
Collections.sort(employees);
```

or:

```java
employees.sort(null);
```

---

### Approach 2 — `Comparator`

The sorting rule is provided **outside the Employee class**.

```java
employees.sort(
    Comparator.comparingDouble(e -> e.salary)
);
```

Here:

> Employee doesn't know anything about sorting.

The caller decides:

```text
Sort by salary
Sort by name
Sort by ID
Sort by salary descending
...
```

---

# 3. Why is this "new way"?

It's not that `Comparable` has become obsolete.

Java 8 introduced powerful **Comparator factory methods and lambda expressions**, which made this style much cleaner.

Old style:

```java
Collections.sort(employees, new Comparator<Employee>() {

    @Override
    public int compare(Employee e1, Employee e2) {
        return Double.compare(e1.salary, e2.salary);
    }
});
```

Java 8+:

```java
employees.sort(
    Comparator.comparingDouble(e -> e.salary)
);
```

Same concept, much less code.

---

# 4. The biggest advantage

Imagine you have an Employee list.

Sometimes you want:

```text
Sort by ID
```

Sometimes:

```text
Sort by name
```

Sometimes:

```text
Sort by salary
```

Sometimes:

```text
Sort by salary descending
```

If you put everything inside `Comparable`, you have a problem.

`Comparable` can define only **one natural/default ordering**.

For example:

```java
class Employee implements Comparable<Employee> {

    @Override
    public int compareTo(Employee other) {
        return Integer.compare(this.id, other.id);
    }
}
```

Now the default ordering is ID.

But what if you want salary?

You'd have to create a `Comparator`.

```java
employees.sort(
    Comparator.comparingDouble(e -> e.salary)
);
```

And for name:

```java
employees.sort(
    Comparator.comparing(e -> e.name)
);
```

So **Comparator gives you multiple sorting strategies without changing the Employee class.**

---

# 5. Real-world Spring Boot example

This is where the difference becomes very useful.

Suppose you have:

```java
class Employee {
    private Long id;
    private String name;
    private Double salary;
    private String department;
}
```

Your API might support:

```text
GET /employees?sortBy=name
GET /employees?sortBy=salary
GET /employees?sortBy=id
```

You don't want:

```java
Employee implements Comparable<Employee>
```

and put every possible sorting rule inside `Employee`.

Instead, you can define the sorting externally.

For example:

```java
employees.sort(
    Comparator.comparing(Employee::getName)
);
```

or:

```java
employees.sort(
    Comparator.comparingDouble(Employee::getSalary)
);
```

or:

```java
employees.sort(
    Comparator.comparing(Employee::getDepartment)
                  .thenComparing(Employee::getName)
);
```

This follows a very important OOP principle:

> **Keep the Employee class responsible for Employee data/behavior, and keep different sorting strategies outside it.**

---

# 6. One important correction to remember

It's better to think of it like this:

```text
Comparable
    ↓
"Employee has a natural/default way to compare itself"

Comparator
    ↓
"Someone else provides a way to compare two Employees"
```

### Comparable

```java
class Employee implements Comparable<Employee> {

    @Override
    public int compareTo(Employee e) {
        return Integer.compare(this.id, e.id);
    }
}
```

Usage:

```java
Collections.sort(employees);
```

### Comparator

```java
employees.sort(
    Comparator.comparingDouble(Employee::getSalary)
);
```

Usage:

```java
employees.sort(comparator);
```

---

## ⭐ Interview shortcut

If the interviewer asks:

**"Why don't you implement Comparable in Employee?"**

You can answer:

> "`Comparable` is used when the class has a single natural ordering. `Comparator` is preferred when we need different sorting strategies, such as sorting employees by ID, name, salary, or department, without modifying the Employee class."

And one more important point:

**`Comparator.comparing()` is not a replacement for `Comparator`; it is a convenient Java 8+ way to create a `Comparator`.**

So this:

```java
Comparator.comparing(Employee::getName)
```

**creates a Comparator**.

That distinction is very important for interviews.
