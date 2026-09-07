Absolutely. Let’s understand `@RestControllerAdvice` in **Spring Boot** from the basics, with a practical example.

## 1. What is `@RestControllerAdvice`?

`@RestControllerAdvice` is used for **global exception handling** in a Spring Boot REST API.

Instead of writing exception-handling code in every controller, you can create **one central class** that handles exceptions for all controllers.

Think of it like:

```text
Controller 1 ──┐
Controller 2 ──┤
Controller 3 ──┼──> @RestControllerAdvice
Controller 4 ──┘          ↓
                     Handle Exceptions
```

---

# 2. Why do we need it?

Suppose you have this controller:

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {

        if (id <= 0) {
            throw new RuntimeException("Invalid user ID");
        }

        return new User(id, "Gowtham");
    }
}
```

If an exception occurs, Spring will return a default error response.

But usually, in a real application, we want our own response:

```json
{
    "status": 400,
    "message": "Invalid user ID",
    "timestamp": "2026-09-03T08:00:00"
}
```

We can achieve this using `@RestControllerAdvice`.

---

# 3. Basic Example

Create a separate class:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
```

Now if **any controller** throws `RuntimeException`, this method can handle it.

For example:

```java
@GetMapping("/{id}")
public User getUser(@PathVariable int id) {

    if (id <= 0) {
        throw new RuntimeException("Invalid user ID");
    }

    return new User(id, "Gowtham");
}
```

The flow becomes:

```text
Client
   |
   | GET /users/-1
   ↓
UserController
   |
   | throws RuntimeException
   ↓
@RestControllerAdvice
   |
   | @ExceptionHandler(RuntimeException.class)
   ↓
ResponseEntity
   |
   ↓
Client
```

Response:

```text
HTTP 400 Bad Request

Invalid user ID
```

---

# 4. `@ControllerAdvice` vs `@RestControllerAdvice`

This is an important interview question.

### `@ControllerAdvice`

Used for global exception handling, but generally works with MVC controllers and can return views.

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
}
```

### `@RestControllerAdvice`

It is essentially:

```java
@ControllerAdvice
@ResponseBody
```

So:

```java
@RestControllerAdvice
```

is equivalent to:

```java
@ControllerAdvice
@ResponseBody
```

The major difference is that `@RestControllerAdvice` is designed specifically for **REST APIs**, where we normally return JSON/XML responses rather than HTML views.

---

# 5. What is `@ExceptionHandler`?

`@RestControllerAdvice` itself doesn't tell Spring **which exception to handle**.

That's what `@ExceptionHandler` does.

Example:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
```

Here:

```java
@ExceptionHandler(RuntimeException.class)
```

means:

> "Whenever a `RuntimeException` occurs in a controller, call this method."

---

# 6. Handling different exceptions

You can have multiple exception handlers.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(
            UserNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(
            IllegalArgumentException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(
            Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Something went wrong");
    }
}
```

Now:

```text
UserNotFoundException
        ↓
handleUserNotFound()

IllegalArgumentException
        ↓
handleIllegalArgument()

Other Exception
        ↓
handleGenericException()
```

---

# 7. Custom Exception

In real applications, we normally create our own exceptions.

For example:

```java
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
```

Then in the service:

```java
public User getUser(int id) {

    User user = repository.findById(id);

    if (user == null) {
        throw new UserNotFoundException(
            "User not found with ID: " + id
        );
    }

    return user;
}
```

The exception travels up:

```text
Controller
    ↓
Service
    ↓
UserNotFoundException
    ↓
@RestControllerAdvice
    ↓
@ExceptionHandler
    ↓
HTTP Response
```

---

# 8. Better approach: Create an Error Response

Instead of returning just a String:

```java
.body(ex.getMessage());
```

we usually create a proper error object.

```java
public class ErrorResponse {

    private int status;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(int status,
                         String message,
                         LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    // getters and setters
}
```

Then:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex) {

        ErrorResponse error = new ErrorResponse(
                404,
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }
}
```

The API response becomes:

```json
{
    "status": 404,
    "message": "User not found with ID: 10",
    "timestamp": "2026-09-03T08:02:00"
}
```

This is much cleaner for frontend/client applications.

---

# 9. Modern Spring Boot approach

You will also commonly see a Java `record` used for the error response:

```java
public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp
) {
}
```

Then:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex) {

        ErrorResponse response = new ErrorResponse(
                404,
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
}
```

---

# 10. Handling validation errors

This is another very common use of `@RestControllerAdvice`.

Suppose you have:

```java
public class UserRequest {

    @NotBlank
    private String name;

    @Email
    private String email;
}
```

Controller:

```java
@PostMapping("/users")
public User createUser(
        @Valid @RequestBody UserRequest request) {

    return userService.createUser(request);
}
```

If the request is:

```json
{
    "name": "",
    "email": "abc"
}
```

validation fails.

Spring throws a validation-related exception.

You can handle it globally:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        ErrorResponse response = new ErrorResponse(
                400,
                message,
                LocalDateTime.now()
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }
}
```

---

# 11. Important: How Spring finds the handler

Suppose you have:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(...) {
        ...
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(...) {
        ...
    }
}
```

And your application throws:

```java
UserNotFoundException
```

Spring looks for the **most specific matching handler**.

So it chooses:

```text
UserNotFoundException
       ↓
handleUserNotFound()
```

rather than:

```text
Exception
       ↓
handleException()
```

That's why having a generic:

```java
@ExceptionHandler(Exception.class)
```

is useful as a **fallback**, but you should also define specific handlers for expected errors.

---

# 12. `@RestControllerAdvice` vs `try-catch`

Without global handling, you might write:

```java
@GetMapping("/{id}")
public ResponseEntity<?> getUser(@PathVariable int id) {

    try {

        User user = userService.getUser(id);

        return ResponseEntity.ok(user);

    } catch (UserNotFoundException ex) {

        return ResponseEntity
                .status(404)
                .body(ex.getMessage());
    }
}
```

Imagine you have 20 controllers.

You would have lots of:

```java
try {
    ...
} catch (...) {
    ...
}
```

❌ Repetitive
❌ Difficult to maintain
❌ Controller becomes cluttered

With `@RestControllerAdvice`:

```java
@GetMapping("/{id}")
public User getUser(@PathVariable int id) {

    return userService.getUser(id);
}
```

And exception handling is centralized:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handle(UserNotFoundException ex) {
        ...
    }
}
```

Much cleaner.

---

# 13. Complete architecture

A typical Spring Boot application might look like this:

```text
                Client
                  |
                  | HTTP Request
                  ↓
             Controller
                  |
                  ↓
               Service
                  |
                  ↓
             Repository
                  |
                  ↓
               Database


If something goes wrong:

Controller
    ↓
Service
    ↓
Exception thrown
    ↓
@RestControllerAdvice
    ↓
@ExceptionHandler
    ↓
ErrorResponse
    ↓
Client
```

For example:

```text
GET /users/100
       ↓
UserController
       ↓
UserService
       ↓
Repository
       ↓
User doesn't exist
       ↓
throw UserNotFoundException
       ↓
@RestControllerAdvice
       ↓
handleUserNotFound()
       ↓
HTTP 404
       ↓
{
   "status": 404,
   "message": "User not found"
}
```

### The key thing to remember

**`@RestControllerAdvice` = Centralized/global exception handling for REST controllers.**

And:

```text
@RestControllerAdvice
        +
@ExceptionHandler
        ↓
Global Exception Handling
```

This is a very common **Spring Boot interview topic**, especially along with `@ControllerAdvice`, `@ExceptionHandler`, `ResponseEntity`, validation exceptions, and custom exceptions.
