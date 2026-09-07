Absolutely. For a **Senior Java Developer interview**, Streams questions are usually not just “write a filter.” Interviewers test whether you understand:

* `map()` vs `flatMap()`
* `filter()`
* `sorted()`
* `distinct()`
* `groupingBy()`
* `partitioningBy()`
* `toMap()`
* `reduce()`
* `collect()`
* `Comparator`
* `Comparable`
* Collections + Streams
* nested collections
* duplicate handling
* top-N problems
* frequency counting
* real-world object transformations
* performance and parallel streams

Below is a strong **interview-oriented practice set with solutions**.

---

# 1. Basic Interview Setup

Assume we have:

```java
List<Integer> numbers =
        Arrays.asList(10, 20, 30, 40, 50, 20, 30, 10);

List<String> names =
        Arrays.asList("John", "Alice", "Bob", "David", "Alice");

List<Employee> employees = List.of(
    new Employee(1, "John", "IT", 90000),
    new Employee(2, "Alice", "HR", 70000),
    new Employee(3, "Bob", "IT", 85000),
    new Employee(4, "David", "Finance", 80000),
    new Employee(5, "Mary", "IT", 95000)
);
```

Employee:

```java
class Employee {

    private int id;
    private String name;
    private String department;
    private double salary;

    // constructor, getters
}
```

---

# 2. Find Even Numbers

### Question

Find all even numbers from a list.

```java
List<Integer> result = numbers.stream()
        .filter(n -> n % 2 == 0)
        .toList();
```

Output:

```text
[10, 20, 30, 40, 50, 20, 30, 10]
```

### Interview follow-up

**What is the difference between `filter()` and `map()`?**

`filter()` removes/selects elements.

```java
stream.filter(x -> condition)
```

`map()` transforms elements.

```java
stream.map(x -> transformation)
```

Example:

```java
numbers.stream()
       .filter(n -> n > 20)
       .map(n -> n * 2)
       .toList();
```

---

# 3. Find Unique Elements

```java
List<Integer> unique = numbers.stream()
        .distinct()
        .toList();
```

Output:

```text
[10, 20, 30, 40, 50]
```

### Senior-level follow-up

How does `distinct()` identify duplicates?

It internally relies on equality semantics, primarily `equals()` and `hashCode()`.

For custom objects, you must correctly implement:

```java
equals()
hashCode()
```

---

# 4. Find Duplicate Elements

This is a **very common interview question**.

```java
Set<Integer> seen = new HashSet<>();

Set<Integer> duplicates = numbers.stream()
        .filter(n -> !seen.add(n))
        .collect(Collectors.toSet());
```

Output:

```text
[10, 20, 30]
```

Why?

`HashSet.add()` returns:

```text
true  -> element was not present
false -> element already exists
```

Therefore:

```java
!seen.add(n)
```

means duplicate.

---

# 5. Count Frequency of Each Element

Very common senior interview question.

```java
Map<Integer, Long> frequency =
        numbers.stream()
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));
```

Output:

```text
10 -> 2
20 -> 2
30 -> 2
40 -> 1
50 -> 1
```

Equivalent:

```java
Collectors.groupingBy(
    x -> x,
    Collectors.counting()
)
```

---

# 6. Find the Most Frequent Element

```java
Integer result = numbers.stream()
        .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
        ))
        .entrySet()
        .stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse(null);
```

Output:

```text
10
```

There is a tie here, so the exact result can depend on map/order characteristics.

---

# 7. Sort Numbers

Ascending:

```java
List<Integer> result = numbers.stream()
        .sorted()
        .toList();
```

Descending:

```java
List<Integer> result = numbers.stream()
        .sorted(Comparator.reverseOrder())
        .toList();
```

---

# 8. Find Maximum and Minimum

Maximum:

```java
Integer max = numbers.stream()
        .max(Integer::compareTo)
        .orElseThrow();
```

Minimum:

```java
Integer min = numbers.stream()
        .min(Integer::compareTo)
        .orElseThrow();
```

Or:

```java
int max = numbers.stream()
        .mapToInt(Integer::intValue)
        .max()
        .orElseThrow();
```

The second approach avoids boxing during the numeric aggregation.

---

# 9. Second Highest Number

Extremely common.

```java
Integer secondHighest = numbers.stream()
        .distinct()
        .sorted(Comparator.reverseOrder())
        .skip(1)
        .findFirst()
        .orElse(null);
```

For:

```text
[10, 20, 30, 40, 50]
```

Result:

```text
40
```

### Why `distinct()`?

Without it:

```text
[50, 50, 40, ...]
```

could incorrectly produce `50` as the second element.

---

# 10. Third Highest Number

```java
Integer thirdHighest = numbers.stream()
        .distinct()
        .sorted(Comparator.reverseOrder())
        .skip(2)
        .findFirst()
        .orElse(null);
```

---

# 11. Top 3 Highest Numbers

```java
List<Integer> top3 = numbers.stream()
        .distinct()
        .sorted(Comparator.reverseOrder())
        .limit(3)
        .toList();
```

---

# 12. Sum of Numbers

```java
int sum = numbers.stream()
        .mapToInt(Integer::intValue)
        .sum();
```

Output:

```text
210
```

---

# 13. Average

```java
double average = numbers.stream()
        .mapToInt(Integer::intValue)
        .average()
        .orElse(0);
```

---

# 14. Convert List to Set

```java
Set<Integer> set = numbers.stream()
        .collect(Collectors.toSet());
```

Or:

```java
Set<Integer> set = new HashSet<>(numbers);
```

### Interview question

**When would you prefer Streams?**

Streams are useful when you're performing a pipeline of transformations/filtering/aggregation.

For a simple:

```java
new HashSet<>(list)
```

using Streams doesn't necessarily make the code better.

---

# 15. Convert List of Strings to Uppercase

```java
List<String> result = names.stream()
        .map(String::toUpperCase)
        .toList();
```

---

# 16. Find Names Starting With "A"

```java
List<String> result = names.stream()
        .filter(name -> name.startsWith("A"))
        .toList();
```

---

# 17. Find String With Maximum Length

```java
String result = names.stream()
        .max(Comparator.comparingInt(String::length))
        .orElse(null);
```

---

# 18. Sort Strings by Length

```java
List<String> result = names.stream()
        .sorted(Comparator.comparingInt(String::length))
        .toList();
```

Descending:

```java
List<String> result = names.stream()
        .sorted(Comparator.comparingInt(String::length).reversed())
        .toList();
```

---

# 19. Join Strings

```java
String result = names.stream()
        .distinct()
        .collect(Collectors.joining(", "));
```

Output:

```text
John, Alice, Bob, David
```

---

# 20. Find Employees From IT Department

```java
List<Employee> result = employees.stream()
        .filter(e -> e.getDepartment().equals("IT"))
        .toList();
```

---

# 21. Get Employee Names

```java
List<String> employeeNames = employees.stream()
        .map(Employee::getName)
        .toList();
```

This is one of the most basic examples of:

```text
Employee -> String
```

using `map()`.

---

# 22. Find Highest Paid Employee

Very common.

```java
Employee highestPaid = employees.stream()
        .max(Comparator.comparingDouble(Employee::getSalary))
        .orElse(null);
```

---

# 23. Find Second Highest Paid Employee

```java
Employee secondHighest = employees.stream()
        .sorted(
            Comparator.comparingDouble(Employee::getSalary)
                      .reversed()
        )
        .skip(1)
        .findFirst()
        .orElse(null);
```

### Better approach for large data

If you only need the second highest salary, sorting the entire collection is not necessarily optimal.

You can maintain top values or use a reduction strategy.

This is a good senior-level discussion point:

> `sorted()` is convenient but requires ordering the whole stream. For very large datasets, a full sort may be more expensive than maintaining only the top N elements.

---

# 24. Group Employees by Department

One of the **most important Stream interview questions**.

```java
Map<String, List<Employee>> result =
        employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment
                ));
```

Result conceptually:

```text
IT       -> [John, Bob, Mary]
HR       -> [Alice]
Finance  -> [David]
```

---

# 25. Count Employees by Department

```java
Map<String, Long> result =
        employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.counting()
                ));
```

Output:

```text
IT       -> 3
HR       -> 1
Finance  -> 1
```

---

# 26. Average Salary by Department

Very important.

```java
Map<String, Double> result =
        employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.averagingDouble(
                                Employee::getSalary
                        )
                ));
```

---

# 27. Total Salary by Department

```java
Map<String, Double> result =
        employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.summingDouble(
                                Employee::getSalary
                        )
                ));
```

---

# 28. Highest Paid Employee in Each Department

This is a **very good senior-level interview problem**.

```java
Map<String, Optional<Employee>> result =
        employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.maxBy(
                                Comparator.comparingDouble(
                                        Employee::getSalary
                                )
                        )
                ));
```

You can unwrap it:

```java
Map<String, Employee> result =
        employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(
                                        Comparator.comparingDouble(
                                                Employee::getSalary
                                        )
                                ),
                                Optional::get
                        )
                ));
```

---

# 29. Partition Employees Based on Salary

Suppose:

```text
salary >= 80000
```

```java
Map<Boolean, List<Employee>> result =
        employees.stream()
                .collect(Collectors.partitioningBy(
                        e -> e.getSalary() >= 80000
                ));
```

Result:

```text
true  -> employees >= 80000
false -> employees < 80000
```

### Important interview question

**`groupingBy()` vs `partitioningBy()`?**

`groupingBy()`:

```java
Map<K, List<T>>
```

Used when there can be multiple groups.

Example:

```text
IT
HR
Finance
```

`partitioningBy()`:

```java
Map<Boolean, List<T>>
```

Used for exactly two logical groups:

```text
true
false
```

---

# 30. Convert Employees to Map

```java
Map<Integer, Employee> employeeMap =
        employees.stream()
                .collect(Collectors.toMap(
                        Employee::getId,
                        Function.identity()
                ));
```

Result:

```text
1 -> John
2 -> Alice
3 -> Bob
...
```

---

# 31. Important `toMap()` Duplicate Question

Suppose employee IDs can duplicate.

This will fail:

```java
Collectors.toMap(
    Employee::getId,
    Function.identity()
)
```

with:

```text
IllegalStateException:
Duplicate key
```

You need a merge function:

```java
Map<Integer, Employee> result =
        employees.stream()
                .collect(Collectors.toMap(
                        Employee::getId,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
```

Or keep the latest:

```java
(existing, replacement) -> replacement
```

This is **frequently asked in senior interviews**.

---

# 32. `map()` vs `flatMap()`

Suppose:

```java
List<List<Integer>> numbers = List.of(
        List.of(1, 2, 3),
        List.of(4, 5),
        List.of(6, 7)
);
```

Using `map()`:

```java
numbers.stream()
       .map(List::stream)
```

produces:

```text
Stream<Stream<Integer>>
```

Using `flatMap()`:

```java
List<Integer> result =
        numbers.stream()
                .flatMap(List::stream)
                .toList();
```

produces:

```text
[1, 2, 3, 4, 5, 6, 7]
```

### Easy interview definition

> `map()` performs one-to-one transformation, while `flatMap()` transforms and flattens nested structures.

---

# 33. Find Common Elements Between Two Lists

```java
List<Integer> common =
        list1.stream()
             .filter(new HashSet<>(list2)::contains)
             .distinct()
             .toList();
```

Using a `HashSet` for membership checking is preferable to repeatedly calling `List.contains()` when the second collection is large.

---

# 34. Find Elements Present in List 1 but Not List 2

```java
Set<Integer> secondSet = new HashSet<>(list2);

List<Integer> result =
        list1.stream()
             .filter(n -> !secondSet.contains(n))
             .toList();
```

---

# 35. Find Intersection of Two Lists

```java
Set<Integer> set2 = new HashSet<>(list2);

Set<Integer> intersection =
        list1.stream()
             .filter(set2::contains)
             .collect(Collectors.toSet());
```

---

# 36. Find Union of Two Lists

```java
Set<Integer> union =
        Stream.concat(list1.stream(), list2.stream())
              .collect(Collectors.toSet());
```

---

# 37. Find First Non-Repeated Character

Very common senior coding question.

For:

```text
"swiss"
```

Expected:

```text
w
```

Solution:

```java
Character result =
        input.chars()
             .mapToObj(c -> (char) c)
             .collect(Collectors.groupingBy(
                     Function.identity(),
                     LinkedHashMap::new,
                     Collectors.counting()
             ))
             .entrySet()
             .stream()
             .filter(e -> e.getValue() == 1)
             .map(Map.Entry::getKey)
             .findFirst()
             .orElse(null);
```

### Why `LinkedHashMap`?

Because we need to preserve insertion order.

This is a great interview discussion:

```text
HashMap       -> no ordering guarantee
LinkedHashMap -> insertion order
TreeMap       -> sorted order
```

---

# 38. Find Duplicate Characters

```java
Set<Character> duplicates =
        input.chars()
             .mapToObj(c -> (char) c)
             .collect(Collectors.groupingBy(
                     Function.identity(),
                     Collectors.counting()
             ))
             .entrySet()
             .stream()
             .filter(e -> e.getValue() > 1)
             .map(Map.Entry::getKey)
             .collect(Collectors.toSet());
```

---

# 39. First Repeated Character

```java
Character result =
        input.chars()
             .mapToObj(c -> (char) c)
             .collect(Collectors.groupingBy(
                     Function.identity(),
                     LinkedHashMap::new,
                     Collectors.counting()
             ))
             .entrySet()
             .stream()
             .filter(e -> e.getValue() > 1)
             .map(Map.Entry::getKey)
             .findFirst()
             .orElse(null);
```

---

# 40. Sort Employees by Salary

```java
List<Employee> result =
        employees.stream()
                .sorted(
                    Comparator.comparingDouble(
                        Employee::getSalary
                    )
                )
                .toList();
```

Descending:

```java
.sorted(
    Comparator.comparingDouble(Employee::getSalary)
              .reversed()
)
```

---

# 41. Sort by Multiple Fields

This is a **very common senior interview question**.

Sort by:

1. Department
2. Salary descending
3. Name ascending

```java
List<Employee> result =
        employees.stream()
                .sorted(
                    Comparator.comparing(Employee::getDepartment)
                              .thenComparing(
                                  Comparator.comparingDouble(
                                      Employee::getSalary
                                  ).reversed()
                              )
                              .thenComparing(Employee::getName)
                )
                .toList();
```

This demonstrates:

```java
comparing()
thenComparing()
reversed()
```

---

# 42. `Comparable` vs `Comparator`

This is extremely important.

## Comparable

`Comparable` defines the **natural ordering** of an object.

```java
class Employee implements Comparable<Employee> {

    private double salary;

    @Override
    public int compareTo(Employee other) {
        return Double.compare(this.salary, other.salary);
    }
}
```

Then:

```java
employees.stream()
        .sorted()
        .toList();
```

works because Employee implements `Comparable`.

---

# 43. Comparator

`Comparator` defines an **external/custom ordering**.

```java
Comparator<Employee> salaryComparator =
        Comparator.comparingDouble(Employee::getSalary);
```

Then:

```java
employees.stream()
        .sorted(salaryComparator)
        .toList();
```

---

# 44. Comparable vs Comparator — Interview Answer

| Comparable                          | Comparator                     |
| ----------------------------------- | ------------------------------ |
| `java.lang`                         | `java.util`                    |
| `compareTo()`                       | `compare()`                    |
| Defines natural ordering            | Defines custom ordering        |
| Usually implemented by class        | Usually separate object/lambda |
| One primary ordering                | Multiple possible orderings    |
| `Collections.sort(list)` can use it | Pass comparator explicitly     |
| `stream.sorted()` can use it        | `stream.sorted(comparator)`    |

### Simple example

Comparable:

```java
class Employee implements Comparable<Employee> {

    @Override
    public int compareTo(Employee e) {
        return this.name.compareTo(e.name);
    }
}
```

Comparator:

```java
Comparator<Employee> bySalary =
        Comparator.comparingDouble(Employee::getSalary);
```

---

# 45. `compareTo()` vs `compare()`

### Comparable

```java
employee1.compareTo(employee2);
```

Conceptually:

```text
negative -> employee1 < employee2
zero     -> employee1 == employee2 in ordering
positive -> employee1 > employee2
```

### Comparator

```java
comparator.compare(employee1, employee2);
```

Same contract:

```text
negative
zero
positive
```

---

# 46. Very Important Interview Trap

Consider:

```java
return this.salary > other.salary ? 1 : -1;
```

What's wrong?

If salaries are equal, it returns:

```text
-1
```

instead of:

```text
0
```

Better:

```java
return Double.compare(this.salary, other.salary);
```

For integers:

```java
return Integer.compare(this.id, other.id);
```

### Avoid this:

```java
return this.salary - other.salary;
```

because numeric subtraction can introduce precision/overflow problems depending on the types.

---

# 47. Comparable and TreeSet

Interviewers often ask:

> What happens if Employee implements Comparable and you put Employees into TreeSet?

Example:

```java
Set<Employee> employees = new TreeSet<>();
```

TreeSet uses the ordering defined by:

```java
compareTo()
```

Therefore, if:

```java
compareTo() == 0
```

TreeSet considers the objects equivalent for its ordering purposes and may not retain both.

This can surprise developers if `compareTo()` is inconsistent with `equals()`.

---

# 48. TreeSet with Comparator

```java
Set<Employee> employees =
        new TreeSet<>(
            Comparator.comparing(Employee::getName)
        );
```

Now the TreeSet uses name ordering rather than Employee's natural ordering.

---

# 49. Stream + Comparable

If Employee implements:

```java
Comparable<Employee>
```

then:

```java
employees.stream()
        .sorted()
        .forEach(System.out::println);
```

uses:

```java
compareTo()
```

But:

```java
employees.stream()
        .sorted(
            Comparator.comparingDouble(
                Employee::getSalary
            )
        );
```

uses the supplied `Comparator`.

---

# 50. Find Highest Salary Per Department Without Sorting

This is a stronger senior-level problem.

```java
Map<String, Employee> result =
        employees.stream()
                .collect(Collectors.toMap(
                    Employee::getDepartment,
                    Function.identity(),
                    BinaryOperator.maxBy(
                        Comparator.comparingDouble(
                            Employee::getSalary
                        )
                    )
                ));
```

This is an excellent alternative to:

```java
groupingBy()
    + maxBy()
```

because you're directly merging employees by department.

---

# 51. Group Employees by Salary Range

For example:

```text
< 50000       -> LOW
50000-80000   -> MEDIUM
> 80000       -> HIGH
```

```java
Map<String, List<Employee>> result =
        employees.stream()
                .collect(Collectors.groupingBy(e -> {

                    if (e.getSalary() < 50000)
                        return "LOW";

                    if (e.getSalary() <= 80000)
                        return "MEDIUM";

                    return "HIGH";
                }));
```

---

# 52. Find Employees Whose Salary Is Above Average

Very good interview question.

```java
double average =
        employees.stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0);

List<Employee> result =
        employees.stream()
                .filter(e -> e.getSalary() > average)
                .toList();
```

---

# 53. Find Department With Highest Average Salary

```java
String department =
        employees.stream()
                .collect(Collectors.groupingBy(
                    Employee::getDepartment,
                    Collectors.averagingDouble(
                        Employee::getSalary
                    )
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
```

---

# 54. Get Top 2 Employees From Each Department

This is a **strong senior interview problem**.

```java
Map<String, List<Employee>> result =
        employees.stream()
                .collect(Collectors.groupingBy(
                    Employee::getDepartment,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> list.stream()
                            .sorted(
                                Comparator.comparingDouble(
                                    Employee::getSalary
                                ).reversed()
                            )
                            .limit(2)
                            .toList()
                    )
                ));
```

---

# 55. Flatten Nested Employee Data

Suppose:

```java
class Department {
    String name;
    List<Employee> employees;
}
```

Then:

```java
List<Employee> allEmployees =
        departments.stream()
                   .flatMap(d -> d.getEmployees().stream())
                   .toList();
```

This is a real-world use of `flatMap()`.

---

# 56. Find Employees Across Multiple Departments

```java
Set<String> departments =
        Set.of("IT", "Finance");

List<Employee> result =
        employees.stream()
                .filter(e -> departments.contains(
                        e.getDepartment()
                ))
                .toList();
```

---

# 57. Convert List to Map of Name → Salary

```java
Map<String, Double> result =
        employees.stream()
                .collect(Collectors.toMap(
                    Employee::getName,
                    Employee::getSalary
                ));
```

If names can duplicate:

```java
.collect(Collectors.toMap(
    Employee::getName,
    Employee::getSalary,
    Double::max
));
```

---

# 58. Find Employees With Duplicate Names

```java
Set<String> duplicates =
        employees.stream()
                .collect(Collectors.groupingBy(
                    Employee::getName,
                    Collectors.counting()
                ))
                .entrySet()
                .stream()
                .filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
```

---

# 59. `reduce()` Interview Question

Calculate product:

```java
int product =
        numbers.stream()
                .reduce(1, (a, b) -> a * b);
```

Sum:

```java
int sum =
        numbers.stream()
                .reduce(0, Integer::sum);
```

Maximum:

```java
int max =
        numbers.stream()
                .reduce(Integer.MIN_VALUE, Integer::max);
```

### Interview question

Why use:

```java
mapToInt().sum()
```

instead of:

```java
reduce(0, Integer::sum)
```

For primitive numeric aggregations, specialized streams such as `IntStream` can avoid boxing and communicate intent clearly.

---

# 60. `findFirst()` vs `findAny()`

```java
employees.stream()
        .filter(e -> e.getSalary() > 80000)
        .findFirst();
```

`findFirst()` respects encounter order when the stream has one.

```java
employees.parallelStream()
        .filter(e -> e.getSalary() > 80000)
        .findAny();
```

`findAny()` allows an arbitrary matching element and can be advantageous for parallel processing.

---

# 61. `anyMatch()`, `allMatch()`, `noneMatch()`

```java
boolean exists =
        employees.stream()
                .anyMatch(e -> e.getSalary() > 100000);
```

```java
boolean all =
        employees.stream()
                .allMatch(e -> e.getSalary() > 50000);
```

```java
boolean none =
        employees.stream()
                .noneMatch(e -> e.getSalary() < 0);
```

These are short-circuiting terminal operations.

---

# 62. `limit()` and `skip()`

First 5:

```java
numbers.stream()
       .limit(5)
       .toList();
```

Skip first 5:

```java
numbers.stream()
       .skip(5)
       .toList();
```

Pagination-style example:

```java
int page = 2;
int pageSize = 10;

List<Employee> result =
        employees.stream()
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .toList();
```

For large database-backed datasets, however, pagination should normally be pushed down to the database rather than loading everything into memory first.

---

# 63. `Collectors.groupingBy()` Advanced Question

Group by department and collect only employee names:

```java
Map<String, List<String>> result =
        employees.stream()
                .collect(Collectors.groupingBy(
                    Employee::getDepartment,
                    Collectors.mapping(
                        Employee::getName,
                        Collectors.toList()
                    )
                ));
```

Result:

```text
IT       -> [John, Bob, Mary]
HR       -> [Alice]
Finance  -> [David]
```

---

# 64. Group Department → Set of Employee Names

```java
Map<String, Set<String>> result =
        employees.stream()
                .collect(Collectors.groupingBy(
                    Employee::getDepartment,
                    Collectors.mapping(
                        Employee::getName,
                        Collectors.toSet()
                    )
                ));
```

---

# 65. Group Employees and Get Total Salary

```java
Map<String, Double> result =
        employees.stream()
                .collect(Collectors.groupingBy(
                    Employee::getDepartment,
                    Collectors.summingDouble(
                        Employee::getSalary
                    )
                ));
```

---

# 66. `partitioningBy()` With Counting

```java
Map<Boolean, Long> result =
        employees.stream()
                .collect(Collectors.partitioningBy(
                    e -> e.getSalary() > 80000,
                    Collectors.counting()
                ));
```

Result:

```text
true  -> 3
false -> 2
```

---

# 67. Find Longest String

```java
String longest =
        names.stream()
             .max(Comparator.comparingInt(String::length))
             .orElse(null);
```

---

# 68. Find Second Longest String

```java
String secondLongest =
        names.stream()
             .distinct()
             .sorted(
                 Comparator.comparingInt(
                     String::length
                 ).reversed()
             )
             .skip(1)
             .findFirst()
             .orElse(null);
```

---

# 69. Count Strings by Length

```java
Map<Integer, Long> result =
        names.stream()
             .collect(Collectors.groupingBy(
                 String::length,
                 Collectors.counting()
             ));
```

For:

```text
John
Alice
Bob
David
```

you get counts grouped by length.

---

# 70. Find Palindromes

```java
List<String> palindromes =
        names.stream()
             .filter(s ->
                 s.equalsIgnoreCase(
                     new StringBuilder(s)
                         .reverse()
                         .toString()
                 )
             )
             .toList();
```

---

# 71. Remove Null Values

```java
List<String> result =
        names.stream()
             .filter(Objects::nonNull)
             .toList();
```

---

# 72. Handle Null Safely

Suppose:

```java
employee.getDepartment()
```

can be null.

Instead of:

```java
e.getDepartment().equals("IT")
```

use:

```java
"IT".equals(e.getDepartment())
```

or:

```java
Objects.equals(e.getDepartment(), "IT")
```

This is a useful production-level consideration.

---

# 73. Stream Pipeline Internal Execution

Suppose:

```java
numbers.stream()
       .filter(n -> n > 10)
       .map(n -> n * 2)
       .filter(n -> n > 50)
       .toList();
```

Streams don't generally process the entire collection one operation at a time.

Conceptually, for each element:

```text
Element
   ↓
filter
   ↓
map
   ↓
filter
   ↓
next element
```

rather than:

```text
filter ALL
   ↓
map ALL
   ↓
filter ALL
```

This is one reason stream pipelines can be efficient.

---

# 74. Lazy Evaluation

This:

```java
numbers.stream()
       .filter(n -> {
           System.out.println(n);
           return n > 20;
       });
```

doesn't execute the filter by itself.

Why?

Because there is no terminal operation.

Add:

```java
.toList();
```

and the pipeline executes.

---

# 75. Short-Circuiting

Example:

```java
numbers.stream()
       .filter(n -> n > 20)
       .findFirst();
```

The stream can stop once the first matching element is found.

Other important short-circuiting operations include:

```java
findFirst()
findAny()
anyMatch()
allMatch()
noneMatch()
limit()
```

---

# 76. Parallel Stream Interview Question

```java
employees.parallelStream()
        .filter(...)
        .map(...)
        .toList();
```

Does `parallelStream()` always improve performance?

**No.**

Parallel streams can introduce overhead from:

* task splitting
* thread coordination
* merging results
* synchronization
* context switching

They are more appropriate when the workload is sufficiently large and CPU-bound and the operations are safe to execute concurrently.

---

# 77. Important Stream Interview Questions

These are questions I'd strongly recommend preparing for a **Senior Java Developer** interview.

### Fundamentals

1. What is a Stream?
2. Stream vs Collection?
3. Intermediate vs terminal operations?
4. What is lazy evaluation?
5. Why are streams single-use?
6. What is short-circuiting?
7. `map()` vs `filter()`?
8. `map()` vs `flatMap()`?
9. `forEach()` vs `forEachOrdered()`?
10. `findFirst()` vs `findAny()`?

### Collectors

11. `groupingBy()`?
12. `partitioningBy()`?
13. `toMap()`?
14. How do you handle duplicate keys in `toMap()`?
15. `mapping()`?
16. `collectingAndThen()`?
17. `joining()`?
18. `counting()`?
19. `summarizingInt()`?
20. `averagingDouble()`?

### Collections + Streams

21. Convert List → Set.
22. Convert List → Map.
23. Find duplicate elements.
24. Find unique elements.
25. Find frequency.
26. Find common elements.
27. Find union.
28. Find difference.
29. Sort Map by value.
30. Sort Map by key.

### Employee Problems

31. Highest salary.
32. Second highest salary.
33. Third highest salary.
34. Highest salary per department.
35. Average salary per department.
36. Total salary per department.
37. Employee count per department.
38. Employees above average salary.
39. Top 2 employees per department.
40. Department with highest average salary.

### Comparable / Comparator

41. What is Comparable?
42. What is Comparator?
43. `compareTo()` vs `compare()`?
44. Can we have multiple Comparators?
45. Can Comparable define multiple natural orderings?
46. What happens when `compareTo()` returns 0?
47. Why should `compareTo()` be consistent with `equals()`?
48. How does TreeSet use Comparator?
49. How does TreeMap use Comparator?
50. What happens if Comparator is inconsistent?

---

# 78. 15 Senior-Level Coding Problems to Practice

If you're preparing for interviews, I would specifically practice these without looking at the solution:

### Problem 1

Find the **second highest salary** from employees.

### Problem 2

Find the **highest-paid employee from each department**.

### Problem 3

Find the **second-highest salary from each department**.

### Problem 4

Find the **department with the highest average salary**.

### Problem 5

Find employees whose salary is **greater than the company average**.

### Problem 6

Find duplicate employee names.

### Problem 7

Convert:

```text
List<Employee>
```

into:

```text
Map<Department, List<Employee>>
```

### Problem 8

Convert:

```text
List<Employee>
```

into:

```text
Map<Id, Employee>
```

while handling duplicate IDs.

### Problem 9

Find the **top 3 salaries per department**.

### Problem 10

Find the **first non-repeated character** in a String.

### Problem 11

Find the **first repeated character**.

### Problem 12

Flatten:

```text
List<List<Employee>>
```

into:

```text
List<Employee>
```

### Problem 13

Find common elements between two lists.

### Problem 14

Sort employees by:

```text
department ASC
salary DESC
name ASC
```

### Problem 15

Group employees by department and return:

```text
Department
    ↓
Employee count
    ↓
Average salary
    ↓
Maximum salary
```

---

## The most important patterns to memorize

For senior interviews, don't memorize 100 individual programs. Memorize these **Stream patterns**:

```java
// Filtering
stream.filter(condition)

// Transformation
stream.map(function)

// Flattening
stream.flatMap(function)

// Sorting
stream.sorted(comparator)

// Distinct
stream.distinct()

// First N
stream.limit(n)

// Skip N
stream.skip(n)

// Grouping
stream.collect(groupingBy(...))

// Partition
stream.collect(partitioningBy(...))

// List → Map
stream.collect(toMap(...))

// Aggregation
stream.reduce(...)

// Numeric aggregation
stream.mapToInt(...).sum()

// Max
stream.max(comparator)

// Min
stream.min(comparator)

// Any
stream.anyMatch(...)

// All
stream.allMatch(...)

// None
stream.noneMatch(...)

// First
stream.findFirst()

// Any matching
stream.findAny()
```

And for **Comparator**, be very comfortable with:

```java
Comparator.comparing(...)
Comparator.comparingInt(...)
Comparator.comparingLong(...)
Comparator.comparingDouble(...)
Comparator.reversed(...)
Comparator.thenComparing(...)
Comparator.nullsFirst(...)
Comparator.nullsLast(...)
Comparator.naturalOrder()
Comparator.reverseOrder()
```

For a Senior Java interview, the **highest-value combination** is:

```text
Streams
   +
Collectors
   +
Collections
   +
Comparator
   +
Comparable
   +
Map operations
   +
Nested collections
   +
Real-world Employee problems
```


