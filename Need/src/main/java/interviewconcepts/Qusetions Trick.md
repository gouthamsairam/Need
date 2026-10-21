Absolutely. This is a **very good interview-preparation pattern**, especially for 5+ years Java/Spring Boot interviews.

Below is a structured **"Can We...?" Java Interview Question Bank**. I have deliberately kept the questions short and screening-oriented. These are the kinds of questions an interviewer can ask rapidly to test your fundamentals before moving to scenario-based questions.

# 🚀 Java "Can We...?" Interview Question Bank

**Total: 275 questions**

---
Yes. These are excellent **Java exception-handling interview questions**. I'll answer each one with **Yes/No + reason + example**, and point out the tricky interview cases.

---

# 1. Can we execute a `try` block without a `catch` block?

### ✅ Yes

A `try` block does not necessarily need a `catch` if it has a `finally`.

```java
try {
    System.out.println("Hello");
} finally {
    System.out.println("Finally");
}
```

Output:

```text
Hello
Finally
```

So:

```text
try + catch       ✅
try + finally     ✅
try + catch + finally ✅
```

But:

```text
try alone         ❌
```

---

# 2. Can we execute a `catch` block without a `try` block?

### ❌ No

`catch` must always be associated with a `try`.

```java
catch (Exception e) {
    // ❌ Compilation error
}
```

Correct:

```java
try {
    // code
} catch (Exception e) {
    // handling
}
```

### Interview point

`catch` cannot exist independently.

---

# 3. Can we execute a `finally` block without a `try` block?

### ❌ No

`finally` must always be associated with a `try`.

```java
finally {
    System.out.println("Hello");
}
```

❌ Compilation error.

Correct:

```java
try {
    // code
} finally {
    // cleanup
}
```

---

# 4. Can we have `try` without both `catch` and `finally`?

### ❌ No

This is invalid:

```java
try {
    System.out.println("Hello");
}
```

Compilation error.

A `try` must be followed by at least one of:

```text
catch
finally
```

Therefore:

```java
try + catch       ✅
try + finally     ✅
try + catch + finally ✅
try alone         ❌
```

---

# 5. Can we have multiple `catch` blocks?

### ✅ Yes

We can have multiple `catch` blocks for one `try`.

```java
try {
    int a = 10 / 0;
}
catch (ArithmeticException e) {
    System.out.println("Arithmetic exception");
}
catch (Exception e) {
    System.out.println("General exception");
}
```

But **order matters**.

### ❌ Wrong

```java
try {
    
}
catch (Exception e) {
    
}
catch (ArithmeticException e) {
    
}
```

Why?

Because:

```text
ArithmeticException
       ↓
     Exception
```

`ArithmeticException` is already caught by `Exception`.

So the second `catch` becomes unreachable.

### Correct order:

```java
catch (ArithmeticException e) {
    
}
catch (Exception e) {
    
}
```

**Specific → General**

---

# 6. Can we have multiple `finally` blocks?

### ❌ No

You cannot do:

```java
try {
    
}
finally {
    
}
finally {
    
}
```

Compilation error.

There can be only **one `finally` block associated with a try statement**.

---

# 7. Can we have `catch` before `try`?

### ❌ No

This is invalid:

```java
catch (Exception e) {
    
}

try {
    
}
```

`catch` must immediately follow its corresponding `try` block.

Correct:

```java
try {
    
}
catch (Exception e) {
    
}
```

---

# 8. Can we have `finally` before `catch`?

### ❌ No

The order must be:

```text
try
 ↓
catch
 ↓
finally
```

Correct:

```java
try {
    
}
catch (Exception e) {
    
}
finally {
    
}
```

Incorrect:

```java
try {
    
}
finally {
    
}
catch (Exception e) {
    
}
```

Once `finally` appears, the `try-catch-finally` statement is complete.

---

# 9. Can we throw an exception from a `finally` block?

### ✅ Yes

For example:

```java
try {
    System.out.println("Try");
}
finally {
    throw new RuntimeException("Exception from finally");
}
```

This is legal.

The exception thrown from `finally` can even **replace/suppress the effect of an exception or return from the `try`/`catch` flow**.

Example:

```java
public static int test() {

    try {
        return 10;
    }
    finally {
        throw new RuntimeException("Error");
    }
}
```

The method does **not return 10**.

The `RuntimeException` is thrown.

### Interview warning ⚠️

Although legal, throwing exceptions from `finally` is generally **bad practice**, because it can hide the original exception.

---

# 10. Can `finally` block execute if `return` is present in `try`?

### ✅ Yes

This is a very common interview question.

```java
public static int test() {

    try {
        return 10;
    }
    finally {
        System.out.println("Finally");
    }
}
```

Output:

```text
Finally
```

And the returned value is:

```text
10
```

The flow is:

```text
try
 ↓
return 10
 ↓
finally executes
 ↓
method returns 10
```

### Important exception

`finally` generally does **not** execute if the JVM is terminated abruptly, for example:

```java
System.exit(0);
```

Example:

```java
try {
    System.out.println("Try");
    System.exit(0);
}
finally {
    System.out.println("Finally");
}
```

`Finally` will not execute because the JVM terminates.

---

# 11. Can `finally` block be skipped?

### ✅ Yes

Normally, `finally` executes.

But there are situations where it can be skipped.

For example:

```java
System.exit(0);
```

Example:

```java
try {
    System.out.println("Try");
    System.exit(0);
}
finally {
    System.out.println("Finally");
}
```

Output:

```text
Try
```

`finally` doesn't execute.

Other abnormal situations such as JVM/process termination can also prevent `finally` from running.

### Interview answer

> `finally` is designed to execute almost always, but it is not guaranteed if the JVM terminates abruptly.

---

# 12. Can we catch `Throwable`?

### ✅ Yes

`Throwable` is the parent of both:

```text
Throwable
   |
   +-- Exception
   |
   +-- Error
```

So this is valid:

```java
try {
    
}
catch (Throwable t) {
    System.out.println("Caught");
}
```

However, catching `Throwable` is generally **not recommended**.

Why?

Because it catches both:

```text
Exception
+
Error
```

including serious JVM problems.

---

# 13. Can we catch `Error`?

### ✅ Yes

`Error` is also a subclass of `Throwable`.

So this is legal:

```java
try {
    
}
catch (Error e) {
    System.out.println("Error caught");
}
```

For example:

```java
try {
    throw new StackOverflowError();
}
catch (Error e) {
    System.out.println("Caught Error");
}
```

But generally, applications should **not try to recover from most `Error`s**.

Examples include:

```text
OutOfMemoryError
StackOverflowError
NoClassDefFoundError
```

These usually indicate serious JVM/application problems.

---

# 14. Can we catch a checked exception without declaring it using `throws`?

### ⚠️ Depends on how the exception is caught.

Consider:

```java
try {
    
}
catch (IOException e) {
    
}
```

This can result in a compilation error if the `try` block **cannot throw `IOException`**.

For example:

```java
try {
    System.out.println("Hello");
}
catch (IOException e) {
    
}
```

❌ Compilation error because `IOException` is a checked exception and there is no possible statement in the `try` that throws it.

But:

```java
try {
    FileInputStream file = new FileInputStream("test.txt");
}
catch (IOException e) {
    
}
```

✅ Valid.

Why?

Because `new FileInputStream()` can throw `FileNotFoundException`, which is an `IOException`.

### Important concept

You don't need to write:

```java
throws IOException
```

on the surrounding method merely because you're **catching** the exception.

`throws` is required when a checked exception is **propagated to the caller**, not when it is handled locally.

---

# 15. Can we throw a checked exception without using `throws`?

### ❌ Normally, no

Suppose:

```java
public void test() {

    throw new IOException();
}
```

❌ Compilation error.

Because `IOException` is checked.

You need:

```java
public void test() throws IOException {

    throw new IOException();
}
```

Or catch it:

```java
public void test() {

    try {
        throw new IOException();
    }
    catch (IOException e) {
        System.out.println("Handled");
    }
}
```

### Exception: "sneaky throw"

Java has techniques/libraries that can bypass checked-exception checking, commonly called **sneaky throws**. But for normal Java code and interviews, the expected answer is:

> A checked exception must either be caught or declared with `throws`.

---

# 16. Can we use `throw` without `throws`?

### ✅ Yes

`throw` and `throws` are different concepts.

You can throw an **unchecked exception** without declaring it.

```java
public void test() {

    throw new RuntimeException("Something went wrong");
}
```

No `throws` required.

Because:

```text
RuntimeException
      ↓
Unchecked exception
```

You can also have:

```java
public void test() throws RuntimeException {

    throw new RuntimeException();
}
```

But `throws RuntimeException` is unnecessary.

### Remember

```text
throw  → actually throws an exception object
throws → declares possible exceptions in method signature
```

---

# 17. Can we use `throws` without actually throwing an exception?

### ✅ Yes

This is completely legal:

```java
public void test() throws IOException {

    System.out.println("Hello");
}
```

The method doesn't actually throw anything.

Why is this allowed?

Because `throws` is a **declaration**, not a requirement that the method must throw the exception.

For example, a method may declare:

```java
throws IOException
```

because its implementation might throw it in the future or because the API contract wants callers to account for it.

---

# 18. Can an overridden method throw a broader checked exception?

### ❌ No

This is an important OOP + exception-handling rule.

Parent:

```java
class Parent {

    void test() throws IOException {
    }
}
```

Child:

```java
class Child extends Parent {

    @Override
    void test() throws Exception {
    }
}
```

❌ Compilation error.

Why?

Because:

```text
Exception
   ↑
IOException
```

`Exception` is broader than `IOException`.

The child cannot introduce a broader checked exception than the parent method declares.

### Why?

Imagine:

```java
Parent p = new Child();
p.test();
```

The caller only knows that `Parent.test()` throws `IOException`.

If the child suddenly throws `Exception`, it breaks the contract.

---

# 19. Can an overridden method throw a narrower checked exception?

### ✅ Yes

Parent:

```java
class Parent {

    void test() throws Exception {
    }
}
```

Child:

```java
class Child extends Parent {

    @Override
    void test() throws IOException {
    }
}
```

✅ Valid.

Because:

```text
Exception
   |
   +-- IOException
```

`IOException` is narrower.

You can also remove the exception completely:

```java
class Child extends Parent {

    @Override
    void test() {
    }
}
```

✅ Valid.

### Rule

For checked exceptions, an overriding method can throw:

```text
Same checked exception       ✅
Narrower checked exception   ✅
No checked exception         ✅
Broader checked exception    ❌
```

---

# 20. Can we catch multiple exceptions in a single `catch` block?

### ✅ Yes

This feature was introduced in **Java 7**.

You can use multi-catch:

```java
try {

}
catch (IOException | SQLException e) {

    System.out.println("Exception occurred");
}
```

This avoids:

```java
try {

}
catch (IOException e) {

}
catch (SQLException e) {

}
```

when both exceptions need exactly the same handling.

### Important restriction

You cannot use parent-child exceptions together:

```java
catch (Exception | IOException e) {
}
```

❌ Compilation error.

Because:

```text
Exception
   ↑
IOException
```

`IOException` is already covered by `Exception`.

---

# ⭐ Quick Interview Revision

| #  | Question                                      | Answer                       |
| -- | --------------------------------------------- | ---------------------------- |
| 1  | `try` without `catch`?                        | ✅ Yes, with `finally`        |
| 2  | `catch` without `try`?                        | ❌ No                         |
| 3  | `finally` without `try`?                      | ❌ No                         |
| 4  | `try` without `catch` and `finally`?          | ❌ No                         |
| 5  | Multiple `catch`?                             | ✅ Yes                        |
| 6  | Multiple `finally`?                           | ❌ No                         |
| 7  | `catch` before `try`?                         | ❌ No                         |
| 8  | `finally` before `catch`?                     | ❌ No                         |
| 9  | Throw exception from `finally`?               | ✅ Yes                        |
| 10 | `finally` with `return` in `try`?             | ✅ Yes                        |
| 11 | Can `finally` be skipped?                     | ✅ Yes, e.g. JVM termination  |
| 12 | Catch `Throwable`?                            | ✅ Yes                        |
| 13 | Catch `Error`?                                | ✅ Yes                        |
| 14 | Catch checked exception without `throws`?     | ✅ Yes, if actually catchable |
| 15 | Throw checked exception without `throws`?     | ❌ Normally no                |
| 16 | `throw` without `throws`?                     | ✅ Yes                        |
| 17 | `throws` without actually throwing?           | ✅ Yes                        |
| 18 | Overridden method broader checked exception?  | ❌ No                         |
| 19 | Overridden method narrower checked exception? | ✅ Yes                        |
| 20 | Multiple exceptions in one `catch`?           | ✅ Yes                        |

## 🔥 The 5 rules I'd memorize for interviews

```text
1. try must have catch OR finally.

2. finally normally executes, even when try has return.

3. Checked exception → catch OR throws.

4. Overriding method cannot throw a broader checked exception.

5. throw = actually throw
   throws = declare
```

These five rules will help you answer a large number of Java exception-handling interview questions.

### ⭐ Important follow-ups

* Difference between `throw` and `throws`?
* Why should specific exceptions be caught before generic exceptions?
* What happens if both `try` and `finally` have `return`?
* What happens to an exception thrown from `try` when `finally` throws another exception?
* Checked vs unchecked exception?

---

# 2. OOP — 30 Questions

### Encapsulation

1. Can we achieve encapsulation without using getters and setters?
2. Can a class be completely immutable?
3. Can a private variable be accessed outside the class?
4. Can a private method be overridden?
5. Can a private constructor be used?
6. Can a class have only private constructors?

### Inheritance

7. Can a class extend another class?
8. Can a class extend multiple classes?
9. Can an interface extend multiple interfaces?
10. Can a class implement multiple interfaces?
11. Can an abstract class extend another abstract class?
12. Can an abstract class extend a concrete class?
13. Can a final class be inherited?
14. Can a constructor be inherited?
15. Can a constructor be overridden?
16. Can private members be inherited?
17. Can static members be inherited?

### Polymorphism

18. Can we overload a method by changing only its return type?
19. Can we overload a method by changing parameter names only?
20. Can we overload a static method?
21. Can we override a static method?
22. Can we override a private method?
23. Can we override a final method?
24. Can we reduce visibility while overriding?
25. Can we increase visibility while overriding?
26. Can an overridden method return a subclass type?
27. Can a parent reference hold a child object?
28. Can a child reference hold a parent object directly?

### Abstraction

29. Can an abstract class have a constructor?
30. Can an abstract class contain concrete methods?

### ⭐ Important follow-ups

* Overloading vs overriding?
* Compile-time vs runtime polymorphism?
* Why can't static methods be overridden?
* Why can't private methods be overridden?
* What is covariant return type?
* Why does Java not support multiple class inheritance?

---

# 3. String — 20 Questions

1. Can we create a String without using `new`?
2. Can we create a String using `new`?
3. Can a String be modified after creation?
4. Can String be inherited?
5. Can String be extended?
6. Can we override a String method?
7. Can String be `null`?
8. Can two String variables point to the same object?
9. Can String literals be stored in the String Pool?
10. Can `new String("Java")` create an object outside the String Pool?
11. Can we compare Strings using `==`?
12. Can we compare Strings using `.equals()`?
13. Can String have duplicate values in a `Set`?
14. Can String be used as a HashMap key?
15. Can String be used in a `switch`?
16. Can String be used in a `synchronized` block?
17. Can StringBuilder be used instead of String?
18. Can StringBuffer be used instead of String?
19. Can StringBuilder be shared safely between multiple threads?
20. Can we make a String mutable?

### ⭐ Important follow-ups

* Why is String immutable?
* String Pool internals?
* `String`, `StringBuilder`, `StringBuffer` differences?
* What happens when you concatenate Strings using `+`?
* Why is String a good HashMap key?

---

# 4. Collections — 30 Questions

### List

1. Can a List contain duplicate values?
2. Can a List contain null values?
3. Can ArrayList contain null?
4. Can LinkedList contain null?
5. Can ArrayList be synchronized?
6. Can ArrayList be accessed concurrently?
7. Can we modify ArrayList while iterating?
8. Can ArrayList grow dynamically?
9. Can LinkedList be used as a Stack?
10. Can LinkedList be used as a Queue?

### Set

11. Can a Set contain duplicate values?
12. Can HashSet contain null?
13. Can TreeSet contain null?
14. Can LinkedHashSet maintain insertion order?
15. Can TreeSet maintain sorted order?
16. Can Set contain mutable objects?

### Map

17. Can HashMap contain a null key?
18. Can HashMap contain multiple null values?
19. Can HashMap contain duplicate keys?
20. Can HashMap contain duplicate values?
21. Can ConcurrentHashMap contain null keys?
22. Can ConcurrentHashMap contain null values?
23. Can TreeMap contain null keys?
24. Can LinkedHashMap maintain insertion order?
25. Can LinkedHashMap maintain access order?
26. Can a Map be converted into a List?
27. Can we modify a Map while iterating over it?
28. Can HashMap be accessed by multiple threads safely?
29. Can Collections.synchronizedMap make HashMap thread-safe?
30. Can immutable collections contain mutable objects?

### ⭐ Important follow-ups

* HashMap internal working?
* HashMap collision handling?
* HashMap vs ConcurrentHashMap?
* ArrayList vs LinkedList?
* HashSet vs TreeSet vs LinkedHashSet?
* HashMap vs LinkedHashMap vs TreeMap?
* Fail-fast vs fail-safe iterator?

---

# 5. Java 8 / Streams — 30 Questions

1. Can an interface have a default method?
2. Can an interface have a static method?
3. Can an interface have private methods?
4. Can a functional interface have multiple abstract methods?
5. Can a functional interface have default methods?
6. Can we create a lambda without a functional interface?
7. Can lambda expressions access local variables?
8. Can lambda modify a local variable?
9. Can lambda access instance variables?
10. Can lambda access static variables?
11. Can we use a method reference instead of a lambda?
12. Can we create a Stream from a List?
13. Can we reuse a Stream after a terminal operation?
14. Can a Stream have multiple terminal operations?
15. Can a Stream have multiple intermediate operations?
16. Can Stream modify the original collection?
17. Can Stream contain null values?
18. Can Stream process elements in parallel?
19. Can parallelStream() improve performance always?
20. Can we convert a Stream back to a List?
21. Can `map()` change the type of elements?
22. Can `filter()` change the type of elements?
23. Can `map()` return null?
24. Can `filter()` return null?
25. Can `map()` be used without `filter()`?
26. Can `filter()` be used without `map()`?
27. Can `reduce()` return an Optional?
28. Can `findFirst()` return null?
29. Can `findAny()` return different results in parallel execution?
30. Can `Collectors.groupingBy()` return a Map?

### ⭐ Important follow-ups

* `map()` vs `flatMap()`?
* `map()` vs `filter()`?
* `reduce()` vs `collect()`?
* `findFirst()` vs `findAny()`?
* `stream()` vs `parallelStream()`?
* Intermediate vs terminal operations?
* Lazy evaluation in Streams?
* How does `groupingBy()` work internally?

---

# 6. Multithreading — 30 Questions

1. Can we create a thread without extending `Thread`?
2. Can we create a thread using `Runnable`?
3. Can we create a thread using `Callable`?
4. Can a thread be started twice?
5. Can we call `run()` directly?
6. Can we call `start()` multiple times?
7. Can a thread return a value?
8. Can a thread throw a checked exception?
9. Can we stop a thread forcefully?
10. Can we interrupt a thread?
11. Can a thread ignore interruption?
12. Can multiple threads execute the same method?
13. Can multiple threads access the same object?
14. Can two threads execute synchronized methods simultaneously?
15. Can two threads execute different synchronized methods simultaneously?
16. Can a static method be synchronized?
17. Can a constructor be synchronized?
18. Can an abstract method be synchronized?
19. Can a synchronized block use any object as a lock?
20. Can we synchronize on `this`?
21. Can we synchronize on a Class object?
22. Can a thread acquire the same lock twice?
23. Can a thread deadlock itself?
24. Can multiple threads have the same priority?
25. Can we create a daemon thread?
26. Can a daemon thread prevent JVM shutdown?
27. Can ExecutorService create threads?
28. Can ExecutorService reuse threads?
29. Can Future return a result from another thread?
30. Can CompletableFuture execute tasks asynchronously?

### ⭐ Important follow-ups

* Thread vs Runnable vs Callable?
* `start()` vs `run()`?
* `sleep()` vs `wait()`?
* `notify()` vs `notifyAll()`?
* synchronized vs Lock?
* ReentrantLock?
* Deadlock?
* Race condition?
* AtomicInteger?
* volatile?
* ExecutorService?
* CompletableFuture?

---

# 7. JVM — 20 Questions

1. Can JVM run without JDK?
2. Can JVM run without JRE?
3. Can Java bytecode run on different operating systems?
4. Can JVM execute bytecode directly?
5. Can JVM run multiple Java applications simultaneously?
6. Can multiple JVMs run on the same machine?
7. Can one JVM have multiple threads?
8. Can threads share heap memory?
9. Can each thread have its own stack?
10. Can objects be created on the stack?
11. Can objects be created on the heap?
12. Can JVM automatically release unused objects?
13. Can we force Garbage Collection?
14. Can `System.gc()` guarantee Garbage Collection?
15. Can an object become eligible for GC while the application is running?
16. Can JVM run out of heap memory?
17. Can JVM run out of stack memory?
18. Can JVM run out of Metaspace?
19. Can class loading happen multiple times?
20. Can JVM unload classes?

### ⭐ Important follow-ups

* Heap vs Stack?
* Metaspace?
* ClassLoader?
* Garbage Collection?
* G1 vs ZGC?
* Young Generation vs Old Generation?
* Minor GC vs Major/Full GC?
* `OutOfMemoryError` vs `StackOverflowError`?

---

# 8. Generics — 20 Questions

1. Can a class be generic?
2. Can an interface be generic?
3. Can a method be generic?
4. Can a constructor be generic?
5. Can an interface have multiple generic parameters?
6. Can we use primitive types with Generics?
7. Can `List<int>` compile?
8. Can `List<Integer>` contain primitive `int` values?
9. Can generic types have upper bounds?
10. Can generic types have lower bounds?
11. Can a generic type extend another generic type?
12. Can a generic method have multiple type parameters?
13. Can we create an array of generic types?
14. Can we create `new T()`?
15. Can we use `instanceof` with generic types?
16. Can static variables use class-level generic type `T`?
17. Can a generic class have static methods?
18. Can wildcard `?` be used with Generics?
19. Can we use `? extends` for writing data?
20. Can we use `? super` for reading data?

### ⭐ Important follow-ups

* `T` vs `?`?
* `? extends` vs `? super`?
* PECS principle?
* Type erasure?
* Why can't we create `new T()`?
* Why can't we create generic arrays?

---

# 9. Java 17 / Java 21 — 20 Questions

1. Can we use records instead of traditional POJOs?
2. Can a record extend another class?
3. Can a record implement an interface?
4. Can a record have methods?
5. Can a record have a constructor?
6. Can a record have static fields?
7. Can a record be mutable?
8. Can we use sealed classes?
9. Can a sealed class have multiple permitted subclasses?
10. Can a sealed interface be implemented by multiple classes?
11. Can a final class extend a sealed class?
12. Can pattern matching simplify `instanceof`?
13. Can pattern matching be used with switch?
14. Can switch return a value?
15. Can switch use expressions?
16. Can text blocks be used instead of traditional Strings?
17. Can virtual threads be created using Java 21?
18. Can virtual threads be used with blocking I/O?
19. Can millions of virtual threads be created?
20. Can virtual threads replace every use case for platform threads?

### ⭐ Important follow-ups

* Virtual threads vs platform threads?
* Virtual threads vs CompletableFuture?
* Records vs Lombok?
* Sealed classes?
* Pattern matching?
* Java 17 vs Java 21?
* When should virtual threads NOT be used?

---

# 10. Spring Boot + Spring Security — 55 Questions

## Spring Core

1. Can Spring create objects automatically?
2. Can Spring inject dependencies automatically?
3. Can we create a Spring Bean without `@Component`?
4. Can we create a Bean using `@Bean`?
5. Can a class have multiple constructors?
6. Can Spring inject through constructor?
7. Can Spring inject through setter?
8. Can Spring inject through field?
9. Can we have multiple Beans of the same type?
10. Can `@Primary` resolve multiple Beans?
11. Can `@Qualifier` resolve multiple Beans?
12. Can a Bean be prototype scoped?
13. Can a Bean be request scoped?
14. Can a Bean be singleton scoped?
15. Can a singleton Bean contain a prototype Bean?
16. Can a Spring Bean be immutable?
17. Can Spring create circular dependencies?
18. Can constructor injection handle circular dependency?
19. Can `@Lazy` resolve some circular dependency scenarios?
20. Can Spring create Beans without component scanning?

---

## Spring Boot

21. Can Spring Boot application run without `@SpringBootApplication`?
22. Can we replace `@SpringBootApplication` with individual annotations?
23. Can a Spring Boot application have multiple configuration classes?
24. Can we change the default embedded server?
25. Can Spring Boot run without an embedded server?
26. Can Spring Boot create REST APIs?
27. Can we have multiple `application.properties` files?
28. Can we use `application.yml` instead of properties?
29. Can we define custom properties?
30. Can `@Value` read application properties?
31. Can `@ConfigurationProperties` read application properties?
32. Can profiles be used in Spring Boot?
33. Can multiple profiles be active simultaneously?
34. Can we have different configuration for Dev and Prod?
35. Can Spring Boot expose Actuator endpoints?
36. Can we customize Actuator endpoints?
37. Can Spring Boot application run on a custom port?
38. Can we disable an auto-configuration?
39. Can we create custom auto-configuration?
40. Can Spring Boot application run without a database?

---

## Spring MVC / REST

41. Can `@Controller` return JSON?
42. Can `@RestController` return HTML?
43. Can a controller have multiple request mappings?
44. Can two controllers have the same endpoint?
45. Can GET request have a request body?
46. Can POST request return a response body?
47. Can `ResponseEntity` be used to control HTTP status?
48. Can an exception thrown from Controller be handled globally?
49. Can `@ControllerAdvice` handle exceptions globally?
50. Can we create custom exception handlers using `@ExceptionHandler`?

---

# Spring Security — 15 Questions

51. Can Spring Security authenticate without a database?
52. Can Spring Security use JWT?
53. Can JWT authentication be stateless?
54. Can Spring Security work with OAuth2?
55. Can authentication and authorization happen separately?
56. Can we authenticate using a custom `UserDetailsService`?
57. Can we define custom authentication logic?
58. Can we have multiple `SecurityFilterChain` beans?
59. Can we configure different security rules for different endpoints?
60. Can one endpoint be public while others require authentication?
61. Can Spring Security use roles?
62. Can Spring Security use authorities?
63. Can roles and authorities be used together?
64. Can method-level security be enabled?
65. Can `@PreAuthorize` restrict access to a method?

---

# 🔥 Bonus: 30 Very Tricky "Can We...?" Questions

These are the ones I would **especially prepare for a senior Java interview**.

### Java

1. Can we overload a method using only generic types?
2. Can we override a method with a broader return type?
3. Can we override a method with a narrower return type?
4. Can we catch `Exception` before `RuntimeException`?
5. Can we catch `RuntimeException` before `Exception`?
6. Can `finally` change the return value?
7. Can a `final` reference point to another object?
8. Can a final object be modified?
9. Can a static block access instance variables?
10. Can an instance block access static variables?

### Collections

11. Can a HashMap key be mutable?
12. Can a HashSet contain objects whose hashCode changes?
13. Can TreeSet contain objects that don't implement Comparable?
14. Can HashMap work correctly if `equals()` is overridden but `hashCode()` isn't?
15. Can two different objects have the same hashCode?
16. Can two objects have different hashCodes but be equal?
17. Can ConcurrentHashMap contain null?
18. Can an immutable List contain a mutable object?
19. Can Collections.unmodifiableList() make the underlying List truly immutable?
20. Can List.of() contain null?

### Multithreading

21. Can two threads execute the same synchronized method simultaneously?
22. Can two threads execute two different synchronized methods simultaneously?
23. Can `volatile` make `count++` thread-safe?
24. Can synchronized guarantee visibility?
25. Can AtomicInteger replace every use of synchronized?
26. Can a deadlock occur with only one thread?
27. Can a daemon thread keep the JVM alive?
28. Can `wait()` be called without synchronized?
29. Can `notify()` wake multiple threads?
30. Can `sleep()` release a lock?

---

# 🎯 How I Recommend You Prepare These

Don't just memorize **Yes/No**.

For every question, prepare this **3-step interview format**:

### Example

**Interviewer:**

> Can we execute a `try` block without a `catch` block?

### Your answer

> **Yes.** A `try` block can exist without a `catch` block if it has a `finally` block.

```java
try {
    System.out.println("Processing");
} finally {
    System.out.println("Cleanup");
}
```

### Then explain why

> `finally` is used when cleanup must happen regardless of whether an exception occurs. If an exception isn't handled by a `catch`, it propagates to the caller after the `finally` block executes.

### Then give the senior-level point

> However, `finally` generally executes except in situations such as JVM termination through `System.exit()`, JVM crash, or similar abnormal termination.

That gives you:

**YES/NO → RULE → EXAMPLE → WHY → EDGE CASE**

That's the pattern interviewers like.

---

# ⭐ Most Important 50 to Master First

If you don't have time to study all 275 initially, start with these:

### Java Core

1. Can `try` exist without `catch`?
2. Can `catch` exist without `try`?
3. Can `finally` exist without `try`?
4. Can `throw` exist without `throws`?
5. Can `throws` exist without `throw`?
6. Can static methods be overridden?
7. Can private methods be overridden?
8. Can final methods be overridden?
9. Can constructors be inherited?
10. Can constructors be overridden?

### OOP

11. Can abstract class have a constructor?
12. Can abstract class have concrete methods?
13. Can abstract class be final?
14. Can interface have static methods?
15. Can interface have private methods?
16. Can class implement multiple interfaces?
17. Can class extend multiple classes?
18. Can method be overloaded by return type?
19. Can overridden method reduce visibility?
20. Can overridden method return subclass type?

### Collections

21. Can HashMap contain null key?
22. Can ConcurrentHashMap contain null?
23. Can HashMap have duplicate keys?
24. Can HashMap have duplicate values?
25. Can HashSet contain null?
26. Can TreeSet contain null?
27. Can HashMap work with mutable keys?
28. Can List contain duplicates?
29. Can Set contain duplicates?
30. Can Map contain duplicate keys?

### Streams

31. Can Stream be reused?
32. Can parallelStream always improve performance?
33. Can `map()` change the element type?
34. Can `filter()` change the element type?
35. Can `findFirst()` return null?
36. Can `findAny()` produce different results?
37. Can functional interface have default methods?
38. Can functional interface have multiple abstract methods?
39. Can lambda modify local variables?
40. Can Stream modify the original collection?

### Multithreading

41. Can `run()` be called directly?
42. Can `start()` be called twice?
43. Can synchronized methods execute simultaneously?
44. Can volatile make `count++` thread-safe?
45. Can daemon thread prevent JVM shutdown?

### JVM / Modern Java

46. Can JVM run without JDK?
47. Can `System.gc()` guarantee GC?
48. Can record extend a class?
49. Can record implement an interface?
50. Can virtual threads handle blocking I/O?

---

## 🧠 The key interview trick

Whenever an interviewer asks:

> **"Can we...?"**

Don't immediately answer only **Yes/No**.

Use:

> **"Yes/No. The rule is ____. For example ____. The reason is ____. One important exception/edge case is ____."**

That small structure makes your answer sound much more like a **senior developer** rather than someone who has memorized Java syntax.
