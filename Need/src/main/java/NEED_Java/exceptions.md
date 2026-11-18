# **Java Exception Handling – Interview Questions (Basic to Advanced)**

Here are **50 important Java Exception Handling interview questions**, organized from basic to advanced levels. These are suitable for Java Developer / Senior Java Developer interviews (4–6 years experience).

## **1. Basic Java Exception Questions**

1. What is an exception in Java?
2. What is the difference between an Error and an Exception?
3. What is the difference between Checked and Unchecked Exceptions?
4. What is the difference between `Exception` and `RuntimeException`?
5. What is the Java exception hierarchy?
6. What are the different types of exceptions in Java?
7. What happens when an exception occurs in a Java program?
8. What is the purpose of exception handling?
9. Can we handle multiple exceptions in a single catch block?
10. What is the difference between `throw` and `throws`?

## **2. try, catch, finally Questions**

11. What is the purpose of try, catch, and finally blocks?
12. Can we write a try block without a catch block?
13. Can we write multiple catch blocks for a single try block?
14. What happens if an exception occurs inside a catch block?
15. Is it possible to have multiple finally blocks?
16. Will the finally block always execute?
17. What happens if we write a return statement inside the try block and another return inside finally?
18. Can we throw an exception from a finally block?
19. What happens if both try and finally blocks throw exceptions?
20. What is the difference between `final`, `finally`, and `finalize()`?

## **3. Intermediate-Level Questions**

21. What is a custom exception in Java? How do you create one?
22. How do you create a custom Checked Exception?
23. How do you create a custom Unchecked Exception?
24. Can we throw multiple exceptions from a single method?
25. Can we catch an exception and rethrow it?
26. What is exception propagation?
27. What happens if an exception is not handled?
28. What is the difference between `getMessage()` and `printStackTrace()`?
29. What is a stack trace?
30. What is the difference between `ClassNotFoundException` and `NoClassDefFoundError`?

## **4. Advanced Java Exception Questions**

31. What is try-with-resources in Java?
32. What is the `AutoCloseable` interface?
33. What is the difference between `Closeable` and `AutoCloseable`?
34. What are suppressed exceptions in Java?
35. What is multi-catch in Java 7?
36. Can we catch the parent exception before the child exception?
37. What happens when we catch `Exception` before `IOException`?
38. Can constructors throw exceptions?
39. Can static blocks throw exceptions?
40. What are best practices for exception handling in enterprise applications?

## **5. Scenario-Based Interview Questions**

These are particularly important for experienced Java developers.

41\. What happens when an exception occurs inside a method and that method calls another method?

42\. Suppose a method declares `throws IOException`. Can we call that method without handling the exception?

43\. If a parent class method throws `IOException`, can the overridden child method throw `Exception`?

44\. If a parent class method does not throw any checked exception, can the child class throw `IOException`?

45\. What happens if we throw an exception inside a finally block while another exception is already occurring?

46\. Suppose you are reading a file and an exception occurs. How will you ensure that the file resource is closed?

47\. How would you handle exceptions in a Spring Boot REST API?

48\. What is the purpose of `@ControllerAdvice` and `@ExceptionHandler`?

49\. How do you create a global exception-handling mechanism in Spring Boot?

50\. How would you handle exceptions when calling an external REST API using `RestTemplate` or `WebClient`?

## **6. Important coding questions**

Try implementing these programs without looking at the answers.

| No. | Coding question                                    | Difficulty |
| --- | -------------------------------------------------- | ---------- |
| 1   | Create a custom checked exception                  | Easy       |
| 2   | Create a custom unchecked exception                | Easy       |
| 3   | Demonstrate exception propagation                  | Medium     |
| 4   | Demonstrate multiple catch blocks                  | Easy       |
| 5   | Demonstrate try-with-resources                     | Medium     |
| 6   | Demonstrate exception chaining                     | Medium     |
| 7   | Demonstrate suppressed exceptions                  | Advanced   |
| 8   | Demonstrate return inside try and finally          | Advanced   |
| 9   | Handle multiple exceptions using multi-catch       | Medium     |
| 10  | Implement global exception handling in Spring Boot | Advanced   |

## **7. Let's practice – Interview Mode**

I recommend practicing one question at a time. I'll evaluate your answer like a technical interviewer.

**Java Exception Quiz**

Question 1 of 4

**What is the difference between Checked and Unchecked Exceptions?**

Checked exceptions are checked at compile time; unchecked exceptions are not required to be caught or declared.

Both are checked only at runtime.

Checked exceptions extend Error; unchecked exceptions extend Throwable.

There is no difference.

Previous

Submit

**My recommendation:** First prepare questions 1–30 thoroughly, then focus on scenario-based questions and Spring Boot global exception handling. These are especially useful for experienced Java backend interviews.