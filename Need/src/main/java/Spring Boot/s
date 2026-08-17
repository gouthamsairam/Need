Absolutely. For a **Senior Java Developer / Senior Spring Boot Developer**, interviewers usually expect much more than knowing `@RestController` and `application.yml`. They test **Spring Boot internals, auto-configuration, dependency management, configuration, profiles, Actuator, production readiness, testing, database integration, transactions, observability, performance, deployment, Docker/Kubernetes, and troubleshooting**.

One current-version note: the Spring Boot documentation currently lists **Spring Boot 4.1.0 as stable**, alongside maintained 4.0.x and 3.x lines; Boot 4.1 requires Java 17+. ([Home][1]) The questions below are intentionally written to remain useful for both **Spring Boot 3.x and modern Boot 4.x**, while calling out version-sensitive areas where relevant.

# Spring Boot — 60 Senior Developer Interview Questions & Answers

---

# Section 1 — Spring Boot Fundamentals

## 1. What is Spring Boot and why was it introduced?

**Answer:**

Spring Boot is built on top of the Spring Framework and simplifies building production-ready Spring applications.

Traditional Spring applications required significant configuration for:

* Dependency configuration
* Web server configuration
* Database configuration
* MVC setup
* JSON serialization
* Application deployment

Spring Boot provides:

* Auto-configuration
* Starter dependencies
* Embedded servers
* Externalized configuration
* Production-ready Actuator
* Opinionated defaults
* Easy testing
* Executable JARs

For example:

```text
Traditional Spring
       |
       +--> XML/configuration
       +--> manually configure server
       +--> manually configure dependencies
       +--> manually configure infrastructure

Spring Boot
       |
       +--> Starters
       +--> Auto Configuration
       +--> Embedded Server
       +--> Actuator
       +--> External Configuration
```

The key philosophy is:

> Convention over configuration.

---

# Section 2 — Spring Boot vs Spring Framework

## 2. What is the difference between Spring Framework and Spring Boot?

**Answer:**

| Spring Framework                  | Spring Boot                           |
| --------------------------------- | ------------------------------------- |
| Core framework                    | Built on Spring                       |
| Requires more configuration       | Convention over configuration         |
| No embedded server by itself      | Embedded server support               |
| Dependency management is manual   | Starters simplify dependencies        |
| Production features require setup | Actuator provides production features |
| More flexible                     | More opinionated                      |

Spring Boot does **not replace Spring Framework**.

Instead:

```text
Spring Boot
     |
     +---- Spring Core
     +---- Spring MVC
     +---- Spring Data
     +---- Spring Security
     +---- Spring Integration
     +---- etc.
```

---

## 3. What does `@SpringBootApplication` actually do?

**Answer:**

This is one of the most frequently asked senior-level questions.

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

`@SpringBootApplication` is effectively a combination of:

```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
```

So:

```text
@SpringBootApplication
        |
        +--> @SpringBootConfiguration
        |
        +--> @EnableAutoConfiguration
        |
        +--> @ComponentScan
```

### `@SpringBootConfiguration`

Identifies the main Boot configuration class.

### `@EnableAutoConfiguration`

Enables Boot's auto-configuration mechanism.

### `@ComponentScan`

Scans for Spring components.

---

# Section 3 — Auto Configuration

## 4. What is Spring Boot Auto-Configuration?

**Answer:**

Auto-configuration attempts to configure your application automatically based on:

1. Classpath dependencies
2. Existing beans
3. Application properties
4. Conditional configuration

For example, if you add:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

Boot can automatically configure infrastructure such as:

* DataSource
* EntityManagerFactory
* Transaction infrastructure

provided the necessary conditions are satisfied.

Spring Boot's auto-configuration is non-invasive: if you define your own bean, Boot can back away from its default configuration. ([Home][2])

---

## 5. How does Spring Boot Auto-Configuration work internally?

**Answer:**

This is a very important senior interview question.

The simplified flow is:

```text
@SpringBootApplication
       |
       v
@EnableAutoConfiguration
       |
       v
Auto-Configuration Import
       |
       v
Auto-Configuration Classes
       |
       v
@Conditional checks
       |
       v
Beans created
```

Modern Boot auto-configurations are registered through:

```text
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

Auto-configuration classes commonly use conditions such as:

```java
@ConditionalOnClass
@ConditionalOnMissingBean
@ConditionalOnProperty
```

Spring Boot's current documentation describes this mechanism and the `AutoConfiguration.imports` file. ([Home][3])

---

## 6. What is `@ConditionalOnClass`?

**Answer:**

It activates configuration when a particular class exists on the classpath.

Example:

```java
@ConditionalOnClass(DataSource.class)
```

Meaning:

> Configure this feature only when `DataSource` is available.

This allows Boot to avoid configuring functionality that isn't being used.

---

## 7. What is `@ConditionalOnMissingBean`?

**Answer:**

It allows Boot to provide a default bean only when the application hasn't already provided one.

Example:

```java
@Bean
@ConditionalOnMissingBean
public PaymentService paymentService() {
    return new DefaultPaymentService();
}
```

If the application defines:

```java
@Bean
PaymentService paymentService() {
    return new CustomPaymentService();
}
```

Boot can back off.

This is one of the core mechanisms that makes Boot's auto-configuration customizable.

---

## 8. How do you find out why a Spring Boot auto-configuration was applied?

**Answer:**

Use:

```bash
java -jar application.jar --debug
```

or:

```bash
-Ddebug
```

Boot generates a **condition evaluation report** showing which auto-configurations matched or didn't match.

This is extremely useful when debugging:

> "Why did Spring Boot create this bean?"

or:

> "Why didn't Spring Boot create this bean?"

The official documentation specifically recommends the debug condition report for understanding applied auto-configuration. ([Home][2])

---

# Section 4 — Starters and Dependencies

## 9. What is a Spring Boot Starter?

**Answer:**

A starter is a convenient dependency descriptor that brings together dependencies commonly required for a feature.

Example:

```xml
<artifactId>
    spring-boot-starter-web
</artifactId>
```

Instead of manually adding:

```text
Spring MVC
Jackson
Embedded server
Validation dependencies
Logging
...
```

the starter manages the typical dependency set.

Examples:

```text
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-security
spring-boot-starter-test
spring-boot-starter-actuator
```

---

## 10. What is the difference between a Starter and Auto-Configuration?

**Answer:**

They solve different problems.

### Starter

Provides dependencies.

```text
Starter
   |
   +--> Required libraries
```

### Auto-configuration

Configures beans automatically.

```text
Dependencies
     |
     v
Auto Configuration
     |
     v
Spring Beans
```

So:

```text
Starter = What libraries do I need?

Auto Configuration = How should those libraries be configured?
```

---

## 11. How does Spring Boot manage dependency versions?

**Answer:**

Spring Boot provides dependency management through its BOM/dependency management mechanism.

Instead of specifying versions for every Spring-related dependency, Boot provides compatible versions.

For example, you generally don't want:

```xml
spring-core 6.x
spring-context 6.y
spring-web 6.z
```

with incompatible combinations.

Boot manages a tested set of dependency versions.

For enterprise projects, this helps prevent dependency conflicts.

---

# Section 5 — Application Startup

## 12. Explain the Spring Boot application startup lifecycle.

**Answer:**

Simplified:

```text
main()
  |
  v
SpringApplication.run()
  |
  v
Create Environment
  |
  v
Load Configuration
  |
  v
Create ApplicationContext
  |
  v
Prepare Context
  |
  v
Load Bean Definitions
  |
  v
Refresh Context
  |
  v
Create/Initialize Beans
  |
  v
Embedded Server Starts
  |
  v
ApplicationReadyEvent
```

Important events include:

```text
ApplicationStartingEvent
ApplicationEnvironmentPreparedEvent
ApplicationContextInitializedEvent
ApplicationPreparedEvent
ContextRefreshedEvent
ApplicationStartedEvent
ApplicationReadyEvent
ApplicationFailedEvent
```

---

## 13. What is `SpringApplication`?

**Answer:**

`SpringApplication` is responsible for bootstrapping a Spring Boot application.

Example:

```java
SpringApplication.run(
    Application.class,
    args
);
```

It:

* Creates the application context
* Loads environment/configuration
* Applies Boot initialization
* Starts the embedded web server when appropriate
* Publishes lifecycle events

---

## 14. What is `CommandLineRunner`?

**Answer:**

It executes logic after the application context has been initialized.

Example:

```java
@Component
public class DataLoader
        implements CommandLineRunner {

    @Override
    public void run(String... args) {
        System.out.println("Application started");
    }
}
```

Useful for:

* Initial data loading
* Startup validation
* One-time initialization

But avoid using it for long-running blocking operations because that can delay application readiness.

---

## 15. What is `ApplicationRunner` and how is it different from `CommandLineRunner`?

**Answer:**

Both execute after application startup.

### `CommandLineRunner`

Receives:

```java
String... args
```

### `ApplicationRunner`

Receives:

```java
ApplicationArguments
```

`ApplicationRunner` is useful when you want structured command-line argument access.

---

# Section 6 — Configuration

## 16. How does Spring Boot externalize configuration?

**Answer:**

Spring Boot supports:

```text
application.properties
application.yml
Environment variables
System properties
Command-line arguments
External config files
SPRING_APPLICATION_JSON
@ConfigurationProperties
```

Example:

```yaml
server:
  port: 8080

app:
  payment:
    timeout: 5s
```

Boot has an ordered property-source mechanism, allowing higher-precedence configuration such as environment variables and command-line arguments to override lower-precedence values. ([Home][4])

---

## 17. `@Value` vs `@ConfigurationProperties` — which should you use?

**Answer:**

### `@Value`

Good for a small number of individual properties.

```java
@Value("${app.name}")
private String appName;
```

### `@ConfigurationProperties`

Better for structured configuration.

```java
@ConfigurationProperties(prefix = "app.payment")
public class PaymentProperties {

    private Duration timeout;
    private int retryCount;
}
```

Configuration:

```yaml
app:
  payment:
    timeout: 5s
    retry-count: 3
```

For enterprise applications, I prefer `@ConfigurationProperties` for grouped configuration because it provides type-safe binding and validation.

Spring Boot explicitly recommends `@ConfigurationProperties` for structured, type-safe configuration. ([Home][4])

---
Absolutely. These are **core Spring/Spring Boot concepts**, and for a Senior Developer interview you should be able to explain **IoC Container → ApplicationContext → Beans → Dependency Injection → Spring Boot annotations** as one connected story.

# 1. What is IoC?

**IoC = Inversion of Control.**

Normally, in plain Java, **you create and manage objects yourself**.

```java
public class OrderService {

    private PaymentService paymentService;

    public OrderService() {
        this.paymentService = new PaymentService();
    }
}
```

Here, `OrderService` controls the creation of `PaymentService`.

```text
OrderService
     |
     | creates
     v
PaymentService
```

This creates **tight coupling**.

With Spring:

```java
@Service
public class OrderService {

    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

You don't write:

```java
new PaymentService();
```

Spring creates the object and supplies it.

```text
                Spring
                  |
             creates beans
                  |
        ┌─────────┴─────────┐
        ↓                   ↓
OrderService         PaymentService
        ↑
        |
   dependency injected
```

That's **Inversion of Control**.

### Interview definition

> **IoC means that the responsibility for creating, configuring, managing, and wiring application objects is transferred from the application code to the Spring container.**

---

# 2. What is an IoC Container?

The **IoC Container** is the component in Spring that manages your application's objects, which Spring calls **beans**.

For example:

```java
@Service
public class OrderService {
}
```

Spring sees this class and creates an object:

```text
OrderService object
```

and manages it inside the container.

The container is responsible for things like:

* Creating objects
* Injecting dependencies
* Managing bean lifecycle
* Applying configuration
* Managing scopes
* Applying proxies
* Handling certain cross-cutting concerns

Conceptually:

```text
                IoC Container
                     |
       ┌─────────────┼─────────────┐
       ↓             ↓             ↓
 OrderService   PaymentService  UserRepository
       |             |             |
       └─────────────┼─────────────┘
                     |
                Dependency
                 Injection
```

---

# 3. What is a Spring Bean?

A **Bean** is simply an object that is created and managed by the Spring IoC container.

For example:

```java
@Service
public class OrderService {
}
```

Spring creates something conceptually like:

```java
OrderService orderService = new OrderService();
```

but **Spring controls the lifecycle and management** of that object.

You can also explicitly create a bean:

```java
@Configuration
public class AppConfig {

    @Bean
    public PaymentService paymentService() {
        return new PaymentService();
    }
}
```

Now:

```text
PaymentService
      |
      v
Spring Container
      |
      v
Managed Bean
```

---

# 4. What is ApplicationContext?

This is where many interview candidates get confused.

`ApplicationContext` is a **Spring IoC container implementation/interface that provides the container functionality plus many additional enterprise features**.

In simple terms:

> **ApplicationContext is the central Spring container that creates, configures, stores, and manages beans and also provides additional application-level features.**

For example:

```java
ApplicationContext context =
        SpringApplication.run(Application.class, args);
```

You can retrieve a bean:

```java
OrderService service =
        context.getBean(OrderService.class);
```

---

# 5. IoC Container vs ApplicationContext

A very important interview distinction:

```text
IoC Container
     |
     +--> Concept / responsibility
     |
     +--> Spring manages beans

ApplicationContext
     |
     +--> Spring's powerful container abstraction
     |
     +--> Bean management
     +--> Events
     +--> Resources
     +--> Environment
     +--> Internationalization
     +--> Application infrastructure
```

Spring has historically had two major container interfaces:

```text
BeanFactory
ApplicationContext
```

`ApplicationContext` extends the capabilities of `BeanFactory`.

For most Spring Boot applications, you interact with an `ApplicationContext`.

---

# 6. What happens when Spring Boot starts?

Suppose you have:

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(
            Application.class,
            args
        );
    }
}
```

The simplified startup flow is:

```text
main()
  |
  v
SpringApplication.run()
  |
  v
Create ApplicationContext
  |
  v
Scan components
  |
  v
Find @Component / @Service / @Repository etc.
  |
  v
Create Bean Definitions
  |
  v
Create Beans
  |
  v
Resolve Dependencies
  |
  v
Dependency Injection
  |
  v
Initialize Beans
  |
  v
Start Application
```

For a web application, the embedded web server is also started as part of the Boot application startup process.

---

# 7. What is Dependency Injection?

Dependency Injection is one of the main ways Spring implements IoC.

Suppose:

```java
@Service
public class OrderService {

    private final PaymentService paymentService;

    public OrderService(
            PaymentService paymentService) {

        this.paymentService = paymentService;
    }
}
```

`OrderService` depends on `PaymentService`.

Instead of:

```java
this.paymentService = new PaymentService();
```

Spring provides it.

```text
Spring Container
      |
      +---- creates PaymentService
      |
      +---- creates OrderService
                    |
                    +---- injects PaymentService
```

This is **Dependency Injection**.

---

# 8. Why is Constructor Injection preferred?

Consider:

```java
@Service
public class OrderService {

    private final PaymentService paymentService;

    public OrderService(
            PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

Advantages:

* Dependencies are explicit
* `final` fields can be used
* Easier unit testing
* Object cannot normally exist without required dependencies
* Better immutability
* Avoids hidden dependencies

Testing becomes easy:

```java
PaymentService paymentService =
        mock(PaymentService.class);

OrderService service =
        new OrderService(paymentService);
```

For modern Spring applications, **constructor injection is generally the preferred approach**.

---

# 9. Now let's understand Spring Boot annotations

This is extremely important for interviews.

Don't memorize annotations individually.

Group them by purpose.

```text
Spring Boot / Spring Annotations
          |
 ┌────────┼───────────┬────────────┐
 ↓        ↓           ↓            ↓
Startup  Beans     Configuration  Web
 ↓        ↓           ↓            ↓
@Spring  @Service   @Configuration @RestController
Boot     @Repository @Bean          @GetMapping
         @Component  @Value         @PostMapping
```

Let's go through the important ones.

---

# 10. `@SpringBootApplication`

This is the most important Spring Boot annotation.

```java
@SpringBootApplication
public class Application {
}
```

It combines:

```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
```

Conceptually:

```text
@SpringBootApplication
        |
        +---- @SpringBootConfiguration
        |
        +---- @EnableAutoConfiguration
        |
        +---- @ComponentScan
```

---

# 11. `@ComponentScan`

It tells Spring where to look for Spring components.

For example:

```java
@ComponentScan("com.example")
```

Spring scans the package and subpackages for:

```text
@Component
@Service
@Repository
@Controller
@RestController
```

Example:

```text
com.example
   |
   +-- Application
   |
   +-- controller
   |      └── UserController
   |
   +-- service
   |      └── UserService
   |
   +-- repository
          └── UserRepository
```

If `Application` is at:

```text
com.example.Application
```

Spring Boot's component scanning convention normally discovers components underneath `com.example`.

---

# 12. `@Component`

`@Component` tells Spring:

> Create and manage an object of this class as a Spring bean.

Example:

```java
@Component
public class EmailValidator {
}
```

Spring creates:

```text
EmailValidator Bean
```

---

# 13. `@Service`

`@Service` is a specialization of `@Component`.

```java
@Service
public class OrderService {
}
```

It indicates:

> This class contains service/business logic.

Technically, Spring treats it as a component stereotype.

Conceptually:

```text
@Component
   |
   +---- @Service
   +---- @Repository
   +---- @Controller
```

---

# 14. `@Repository`

Used primarily for the persistence/data-access layer.

```java
@Repository
public class UserRepository {
}
```

It also participates in Spring's exception translation mechanism for supported persistence technologies.

With Spring Data:

```java
public interface UserRepository
        extends JpaRepository<User, Long> {
}
```

you often don't explicitly add `@Repository`; Spring Data creates the repository implementation/proxy.

---

# 15. `@Controller`

Used for Spring MVC controllers, especially when returning views.

```java
@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
```

Here `"home"` can represent a view name.

---

# 16. `@RestController`

Used for REST APIs.

```java
@RestController
public class UserController {

    @GetMapping("/users")
    public User getUser() {
        return user;
    }
}
```

Conceptually:

```java
@Controller
@ResponseBody
```

So instead of returning a view, the returned object is normally serialized into the HTTP response body.

---

# 17. `@Configuration`

Used to define configuration classes.

```java
@Configuration
public class ApplicationConfig {
}
```

For example:

```java
@Configuration
public class ApplicationConfig {

    @Bean
    public PaymentClient paymentClient() {
        return new PaymentClient();
    }
}
```

---

# 18. `@Bean`

This is extremely important.

`@Bean` tells Spring:

> Register the object returned by this method as a Spring bean.

Example:

```java
@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
```

Now Spring manages that `ObjectMapper`.

---

# 19. `@Component` vs `@Bean`

This is a very common interview question.

### `@Component`

Used directly on the class:

```java
@Component
public class EmailService {
}
```

Spring discovers it through component scanning.

### `@Bean`

Used on a method:

```java
@Bean
public EmailClient emailClient() {
    return new EmailClient();
}
```

Useful when:

* You don't own the class
* You need custom construction
* Third-party library object
* Complex initialization

Example:

```java
@Bean
public RestClient restClient() {
    return RestClient.builder().build();
}
```

### Easy interview answer:

> `@Component` is class-level component scanning, whereas `@Bean` explicitly registers an object returned by a configuration method.

---

# 20. `@Autowired`

`@Autowired` tells Spring to inject a dependency.

Example:

```java
@Service
public class OrderService {

    private final PaymentService paymentService;

    @Autowired
    public OrderService(
            PaymentService paymentService) {

        this.paymentService = paymentService;
    }
}
```

With a single constructor, modern Spring can infer constructor injection without explicitly writing `@Autowired`.

So this is enough:

```java
@Service
public class OrderService {

    private final PaymentService paymentService;

    public OrderService(
            PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

---

# 21. What happens if multiple beans have the same type?

Suppose:

```java
@Component
class PaypalPaymentService
        implements PaymentService {
}
```

and:

```java
@Component
class StripePaymentService
        implements PaymentService {
}
```

Then:

```java
public OrderService(
        PaymentService paymentService) {
}
```

is ambiguous.

Spring doesn't know which one to inject.

Use:

```java
@Primary
```

or:

```java
@Qualifier
```

---

# 22. `@Primary`

Example:

```java
@Component
@Primary
public class StripePaymentService
        implements PaymentService {
}
```

Now when multiple `PaymentService` beans exist, Stripe becomes the default candidate.

---

# 23. `@Qualifier`

Use:

```java
@Component("stripe")
public class StripePaymentService
        implements PaymentService {
}
```

Then:

```java
public OrderService(
    @Qualifier("stripe")
    PaymentService paymentService) {
}
```

This explicitly selects the desired bean.

---

# 24. `@Value`

Used to inject configuration values.

```yaml
app:
  name: OrderService
```

Then:

```java
@Value("${app.name}")
private String appName;
```

For a few simple properties this is convenient.

For large structured configuration, prefer:

```java
@ConfigurationProperties
```

---

# 25. `@ConfigurationProperties`

For enterprise applications, this is very useful.

```yaml
app:
  payment:
    timeout: 5s
    retry-count: 3
    enabled: true
```

Java:

```java
@ConfigurationProperties(prefix = "app.payment")
public class PaymentProperties {

    private Duration timeout;
    private int retryCount;
    private boolean enabled;
}
```

This gives you structured, type-safe configuration.

---

# 26. `@Profile`

Used to activate beans only for specific environments.

```java
@Profile("dev")
@Bean
public PaymentClient mockPaymentClient() {
    return new MockPaymentClient();
}
```

Production:

```java
@Profile("prod")
@Bean
public PaymentClient realPaymentClient() {
    return new RealPaymentClient();
}
```

---

# 27. `@Lazy`

Normally Spring creates singleton beans eagerly during application context initialization.

With:

```java
@Lazy
@Component
public class HeavyService {
}
```

Spring delays creation until the bean is actually needed.

```text
Normal:

Application startup
      |
      v
Create Bean

@Lazy:

Application startup
      |
      v
Don't create

First usage
      |
      v
Create Bean
```

Use it carefully; it can hide startup-time failures.

---

# 28. `@Scope`

Controls bean scope.

Common scopes include:

```text
singleton
prototype
request
session
application
websocket
```

Most Spring beans are singleton-scoped by default.

```java
@Scope("prototype")
@Component
public class ReportGenerator {
}
```

---

# 29. `@PostConstruct`

Used to run initialization logic after dependency injection.

```java
@PostConstruct
public void init() {
    System.out.println("Initialization");
}
```

Lifecycle:

```text
Create Bean
    |
    v
Inject Dependencies
    |
    v
@PostConstruct
    |
    v
Bean Ready
```

---

# 30. `@PreDestroy`

Runs during bean destruction for supported lifecycle-managed beans.

```java
@PreDestroy
public void cleanup() {
}
```

Useful for cleanup operations.

---

# 31. `@Transactional`

Defines transaction boundaries.

```java
@Transactional
public void transferMoney() {
    ...
}
```

Spring typically implements declarative transactions through proxy/interceptor infrastructure.

Important senior interview point:

```text
@Transactional
      |
      v
Spring Proxy
      |
      v
Transaction begin
      |
      v
Method execution
      |
      v
Commit / Rollback
```

Self-invocation is a common reason transaction behavior doesn't apply as expected.

---

# 32. `@Async`

Used for asynchronous method execution.

```java
@EnableAsync
```

and:

```java
@Async
public void sendEmail() {
}
```

The method can execute using a Spring-managed task executor.

For production, configure the executor rather than relying on an inappropriate default.

---

# 33. `@Scheduled`

Used for scheduled execution.

```java
@EnableScheduling
```

Then:

```java
@Scheduled(cron = "0 0 * * * *")
public void processOrders() {
}
```

Important microservices interview question:

> If you have 10 Kubernetes pods, what happens?

Potentially **all 10 instances execute the scheduled method**.

For distributed scheduling, consider mechanisms such as:

* Kubernetes CronJob
* Quartz
* ShedLock
* Cloud scheduler/EventBridge

depending on requirements.

---

# 34. `@Cacheable`

Used for caching.

```java
@Cacheable("users")
public User findUser(Long id) {
    return repository.findById(id)
        .orElseThrow();
}
```

Flow:

```text
Request
   |
   v
Cache?
 /   \
Yes   No
 |     |
Return  DB
        |
        v
      Cache
```

---

# 35. `@CacheEvict`

Removes data from cache.

```java
@CacheEvict(
    value = "users",
    key = "#id"
)
public void deleteUser(Long id) {
    repository.deleteById(id);
}
```

Useful when database state changes.

---

# 36. `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`

These map HTTP requests to controller methods.

```java
@GetMapping("/users")
```

```java
@PostMapping("/users")
```

```java
@PutMapping("/users/{id}")
```

```java
@DeleteMapping("/users/{id}")
```

They are specialized forms of:

```java
@RequestMapping
```

---

# 37. `@RequestBody`

Converts request JSON into a Java object.

```java
@PostMapping("/users")
public User create(
        @RequestBody UserRequest request) {
}
```

Flow:

```text
JSON
 |
 v
Jackson
 |
 v
UserRequest
```

---

# 38. `@PathVariable`

Gets a value from the URL.

```java
@GetMapping("/users/{id}")
public User getUser(
        @PathVariable Long id) {
}
```

Request:

```text
GET /users/100
```

Then:

```text
id = 100
```

---

# 39. `@RequestParam`

Gets a query parameter.

```java
@GetMapping("/users")
public List<User> getUsers(
        @RequestParam String status) {
}
```

Request:

```text
GET /users?status=ACTIVE
```

Then:

```text
status = ACTIVE
```

---

# 40. `@Valid`

Triggers Bean Validation.

```java
@PostMapping("/users")
public User create(
        @Valid
        @RequestBody
        CreateUserRequest request) {
}
```

Example:

```java
public class CreateUserRequest {

    @NotBlank
    private String name;

    @Email
    private String email;
}
```

---

# 41. `@ControllerAdvice`

Used for centralized controller-related exception handling.

Example:

```java
@ControllerAdvice
public class GlobalExceptionHandler {
}
```

For REST APIs, commonly use:

```java
@RestControllerAdvice
```

---

# 42. `@RestControllerAdvice`

This is particularly useful for REST APIs.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handle(
            UserNotFoundException ex) {

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ex.getMessage());
    }
}
```

This avoids duplicating:

```java
try {
   ...
} catch (...) {
}
```

inside every controller.

---

# 43. `@ExceptionHandler`

Defines a method to handle a particular exception.

```java
@ExceptionHandler(
    UserNotFoundException.class
)
public ResponseEntity<?> handleUserNotFound(
        UserNotFoundException ex) {
}
```

---

# 44. `@EnableAutoConfiguration`

Enables Spring Boot's auto-configuration mechanism.

It's already included by:

```java
@SpringBootApplication
```

So normally you don't need to explicitly add it.

---

# 45. `@EnableWebSecurity`

Enables/configures Spring Security's web security infrastructure.

Modern Spring Security applications generally combine it with one or more:

```java
SecurityFilterChain
```

beans.

---

# 46. `@EnableMethodSecurity`

Enables method-level security.

Example:

```java
@EnableMethodSecurity
```

Then:

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) {
}
```

---

# 47. `@Entity`

This isn't a Spring Boot annotation specifically; it is a Jakarta Persistence annotation.

It tells JPA that a class represents a persistent entity.

```java
@Entity
public class User {

    @Id
    private Long id;
}
```

---

# 48. `@Repository`, `@Service`, `@Controller` — are they really different?

This is a common senior interview trick.

All are component stereotypes, but they communicate different responsibilities.

```text
@Component
    |
    +---- @Service
    |
    +---- @Repository
    |
    +---- @Controller
```

### `@Service`

Business logic.

### `@Repository`

Persistence/data-access layer and exception translation semantics.

### `@Controller`

MVC presentation layer.

The distinction is therefore both **semantic and, in some cases, behaviorally meaningful**.

---

# 49. Complete Spring Boot application example

Let's connect everything.

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(
            Application.class,
            args
        );
    }
}
```

Controller:

```java
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService) {

        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUser(
            @PathVariable Long id) {

        return userService.getUser(id);
    }
}
```

Service:

```java
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(
            UserRepository repository) {

        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public User getUser(Long id) {

        return repository.findById(id)
                .orElseThrow(
                    () -> new RuntimeException(
                        "User not found"
                    )
                );
    }
}
```

Repository:

```java
@Repository
public interface UserRepository
        extends JpaRepository<User, Long> {
}
```

The dependency flow is:

```text
              Spring ApplicationContext
                       |
                       |
          ┌────────────┼────────────┐
          ↓            ↓            ↓
      Controller     Service     Repository
          |            |            |
          └────────────┼────────────┘
                       |
                Dependency Injection
```

---

# 50. The Most Important Concept to Remember

If an interviewer asks:

> **"Explain ApplicationContext, IoC, DI and Spring Boot annotations."**

Don't explain them as four unrelated topics.

Explain the relationship:

```text
                    Spring Boot
                         |
                         v
              Application Startup
                         |
                         v
                 ApplicationContext
                         |
                         v
                  IoC Container
                         |
              ┌──────────┴──────────┐
              |                     |
        Creates Beans          Manages Beans
              |                     |
              └──────────┬──────────┘
                         |
                         v
                Dependency Injection
                         |
          ┌──────────────┼──────────────┐
          ↓              ↓              ↓
     Controller       Service       Repository
```

And the annotations tell Spring **what those classes/objects mean and how they should be configured**.

For example:

```text
@SpringBootApplication
        ↓
Starts/configures Boot application

@Component / @Service / @Repository / @Controller
        ↓
Identify Spring-managed components

@Bean
        ↓
Explicitly register an object

@Autowired / constructor
        ↓
Dependency Injection

@Configuration
        ↓
Application configuration

@ConfigurationProperties
        ↓
External configuration binding

@Transactional
        ↓
Transaction boundary

@Cacheable
        ↓
Caching

@Async
        ↓
Asynchronous execution

@Scheduled
        ↓
Scheduled execution

@RestController
        ↓
REST API endpoint
```

### ⭐ Senior interview one-liner

> **"Spring Boot starts the application and creates an ApplicationContext. The ApplicationContext acts as the IoC container, managing Spring Beans and their lifecycle. IoC means Spring takes responsibility for object creation and wiring, while Dependency Injection is the mechanism through which those dependencies are supplied to objects. Spring Boot annotations such as `@SpringBootApplication`, `@Service`, `@Repository`, `@RestController`, `@Configuration`, `@Bean`, and `@ConfigurationProperties` provide metadata that tells Spring how to discover, create, configure, and manage those components."**

That is the **core mental model** you should carry into almost every Spring Boot interview.

## 18. What are Spring Profiles?

**Answer:**

Profiles allow environment-specific configuration.

Example:

```text
application.yml
application-dev.yml
application-qa.yml
application-prod.yml
```

Activate:

```yaml
spring:
  profiles:
    active: prod
```

Or:

```bash
SPRING_PROFILES_ACTIVE=prod
```

Example:

```java
@Profile("prod")
@Bean
PaymentClient productionPaymentClient() {
    return new ProductionPaymentClient();
}
```

---

## 19. How would you manage configuration across DEV, QA and PROD?

**Answer:**

I would avoid building separate application binaries for each environment.

Instead:

```text
Same Docker Image
        |
        +--> DEV configuration
        |
        +--> QA configuration
        |
        +--> PROD configuration
```

Use:

```text
Environment variables
ConfigMaps
Secrets
External configuration
Secret managers
Spring Cloud Config where appropriate
```

For Kubernetes:

```text
ConfigMap -> non-sensitive configuration
Secret    -> credentials/secrets
```

The application artifact remains the same.

---

## 20. Where should passwords and secrets be stored?

**Answer:**

Never hard-code them:

```java
String password = "password123";
```

or commit:

```yaml
database:
  password: password123
```

Use:

```text
AWS Secrets Manager
AWS Systems Manager Parameter Store
HashiCorp Vault
Kubernetes Secrets
Cloud provider secret management
```

For your EKS-style deployment architecture:

```text
AWS Secrets Manager
       |
       v
Kubernetes / External Secrets
       |
       v
Spring Boot
```

The principle is:

> Configuration can be externalized; secrets should be managed by a dedicated secret-management mechanism.

---

# Section 7 — REST APIs

## 21. What is the difference between `@Controller` and `@RestController`?

**Answer:**

`@RestController` is effectively:

```java
@Controller
@ResponseBody
```

`@Controller` is typically used for MVC views.

```java
@Controller
public class HomeController {
}
```

`@RestController` is designed for REST APIs:

```java
@RestController
public class UserController {

    @GetMapping("/users")
    public List<User> users() {
        return service.findAll();
    }
}
```

---

## 22. How does Spring Boot convert Java objects into JSON?

**Answer:**

Spring Boot commonly uses **Jackson** for JSON serialization/deserialization in servlet-based REST applications.

Flow:

```text
Java Object
    |
    v
Jackson
    |
    v
JSON
```

For a request:

```text
JSON
 |
 v
Jackson
 |
 v
Java Object
```

Example:

```json
{
  "name": "Goutham",
  "age": 28
}
```

becomes:

```java
User user;
```

---

## 23. How do you handle global exceptions in Spring Boot?

**Answer:**

Use:

```java
@RestControllerAdvice
```

Example:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(
            UserNotFoundException ex) {

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(
                "USER_NOT_FOUND",
                ex.getMessage()
            ));
    }
}
```

Benefits:

* Centralized error handling
* Consistent API responses
* Cleaner controllers
* Easier monitoring

---

## 24. How do you design a production-grade REST API in Spring Boot?

**Answer:**

I would consider:

```text
Controller
   |
   v
DTO
   |
   v
Validation
   |
   v
Service
   |
   v
Repository
```

Plus:

```text
Authentication
Authorization
Exception handling
Correlation ID
Logging
Metrics
Tracing
Pagination
Versioning
Idempotency
Rate limiting
Input validation
```

A senior developer should think beyond:

```java
@GetMapping
```

and consider the complete API lifecycle.

---

# Section 8 — Validation

## 25. How do you validate request data?

**Answer:**

Use Jakarta Bean Validation.

Example:

```java
public class CreateUserRequest {

    @NotBlank
    private String name;

    @Email
    private String email;

    @Min(18)
    private int age;
}
```

Controller:

```java
@PostMapping("/users")
public User create(
        @Valid @RequestBody CreateUserRequest request) {

    return service.create(request);
}
```

Common annotations:

```text
@NotNull
@NotBlank
@NotEmpty
@Size
@Min
@Max
@Pattern
@Email
@Positive
@Past
@Future
```

---

# Section 9 — Spring Data JPA

## 26. How does Spring Boot configure JPA automatically?

**Answer:**

With the appropriate Spring Data JPA starter and database configuration, Boot can auto-configure:

```text
DataSource
EntityManagerFactory
Transaction infrastructure
Spring Data repositories
```

For example:

```java
public interface UserRepository
        extends JpaRepository<User, Long> {
}
```

You don't need to manually implement common CRUD operations.

---

## 27. What is the difference between `save()` and `saveAndFlush()`?

**Answer:**

`save()` persists/merges the entity within the persistence context, but SQL execution may be deferred until flush/transaction completion.

`saveAndFlush()` explicitly flushes the persistence context immediately.

Conceptually:

```text
save()
 |
 v
Persistence Context
 |
 v
Flush later
 |
 v
Database
```

versus:

```text
saveAndFlush()
 |
 v
Persistence Context
 |
 v
Immediate flush
 |
 v
Database
```

Important:

> Flush does not necessarily mean transaction commit.

---

## 28. What is the N+1 query problem in Spring Boot/JPA?

**Answer:**

Suppose:

```text
1 query -> fetch 100 orders
100 queries -> fetch customer for each order
```

Total:

```text
101 queries
```

That's N+1.

Solutions include:

```text
JOIN FETCH
EntityGraph
DTO projections
Batch fetching
Appropriate query design
```

Example:

```java
@Query("""
    select o
    from Order o
    join fetch o.customer
""")
List<Order> findOrdersWithCustomer();
```

For production systems, use SQL logging/APM/database metrics to detect it rather than guessing.

---

# Section 10 — Transactions

## 29. What does `@Transactional` do in Spring Boot?

**Answer:**

`@Transactional` defines transaction boundaries.

Example:

```java
@Transactional
public void transferMoney(
        Long source,
        Long target,
        BigDecimal amount) {

    debit(source, amount);
    credit(target, amount);
}
```

If an unchecked exception occurs and rollback rules apply:

```text
Debit
  |
  v
Credit
  |
  v
Exception
  |
  v
Rollback
```

Spring typically implements this through proxies/interceptors.

---

## 30. Why does `@Transactional` sometimes not work?

**Answer:**

This is a very common senior interview question.

### 1. Self-invocation

```java
this.method();
```

may bypass the Spring proxy.

### 2. Method isn't invoked through the Spring-managed proxy.

### 3. Wrong transaction manager.

### 4. Method visibility/configuration issues.

### 5. Exception handling prevents rollback.

For example:

```java
@Transactional
public void process() {
    try {
        ...
    } catch (Exception e) {
        // swallowed
    }
}
```

The transaction may not roll back as expected.

---

## 31. What is transaction propagation?

**Answer:**

Propagation defines what happens when a transactional method calls another transactional method.

Important types:

```text
REQUIRED
REQUIRES_NEW
SUPPORTS
MANDATORY
NOT_SUPPORTED
NEVER
NESTED
```

### REQUIRED

Uses existing transaction or creates one.

### REQUIRES_NEW

Suspends existing transaction and creates a new one.

Example:

```text
Outer Transaction
       |
       v
REQUIRES_NEW
       |
       v
Independent Transaction
```

This is often useful for audit logging that should commit independently.

---

## 32. What is transaction isolation?

**Answer:**

Isolation controls how concurrent transactions interact.

Common levels:

```text
READ_UNCOMMITTED
READ_COMMITTED
REPEATABLE_READ
SERIALIZABLE
```

Problems include:

```text
Dirty Read
Non-repeatable Read
Phantom Read
```

Higher isolation generally increases consistency but can reduce concurrency.

The correct level depends on the business requirement and database.

---

# Section 11 — Actuator

## 33. What is Spring Boot Actuator?

**Answer:**

Actuator provides production-ready monitoring and management capabilities.

Dependency:

```xml
spring-boot-starter-actuator
```

Common endpoints/features include:

```text
/actuator/health
/actuator/info
/actuator/metrics
/actuator/loggers
/actuator/env
/actuator/configprops
```

Actuator is particularly important in Kubernetes environments.

---

## 34. How would you use Actuator with Kubernetes?

**Answer:**

I would separate:

```text
Liveness
Readiness
Application health
Metrics
```

For example:

```text
Kubernetes
    |
    +--> livenessProbe
    |
    +--> readinessProbe
```

Conceptually:

```text
Liveness
"What is the process health?"

Readiness
"Can this instance receive traffic?"
```

A database outage may make an application temporarily unready without necessarily meaning the JVM/process should be restarted.

---

## 35. What is the difference between liveness and readiness?

**Answer:**

### Liveness

Determines whether the application should be restarted.

### Readiness

Determines whether the application should receive traffic.

Example:

```text
Application running
       |
       +--> Liveness = UP
       |
       +--> Database unavailable
                 |
                 v
          Readiness = DOWN
```

You don't want Kubernetes restarting all application pods just because a downstream dependency is temporarily unavailable.

---

# Section 12 — Observability

## 36. How would you monitor a Spring Boot production application?

**Answer:**

I would monitor:

### JVM

```text
Heap
GC
Threads
CPU
Memory
```

### Application

```text
Request rate
Error rate
Latency
p50
p95
p99
```

### Database

```text
Connection pool
Query latency
Slow queries
Connections
Locks
```

### External dependencies

```text
Kafka
Redis
HTTP services
AWS services
```

### Infrastructure

```text
CPU
Memory
Network
Pod restarts
Container OOM
```

Typical stack:

```text
Spring Boot Actuator
        |
        v
Micrometer
        |
        +--> Prometheus
        |
        +--> Grafana
        |
        +--> Cloud monitoring
```

---

## 37. What is Micrometer?

**Answer:**

Micrometer provides a vendor-neutral metrics abstraction.

Spring Boot integrates with Micrometer.

Conceptually:

```text
Spring Boot
    |
    v
Micrometer
    |
    +--> Prometheus
    +--> Datadog
    +--> New Relic
    +--> CloudWatch
    +--> etc.
```

This avoids coupling application instrumentation directly to one monitoring vendor.

---

# Section 13 — Logging

## 38. How would you implement production logging?

**Answer:**

I prefer structured logging.

Example:

```json
{
  "timestamp": "...",
  "level": "ERROR",
  "service": "order-service",
  "traceId": "...",
  "spanId": "...",
  "orderId": "123",
  "message": "Payment failed"
}
```

Important:

Don't log:

```text
Passwords
JWTs
Access tokens
Client secrets
Sensitive PII
```

Use correlation/trace IDs to follow requests across microservices.

---

## 39. How do you change logging levels without restarting Spring Boot?

**Answer:**

Actuator can expose logging management.

For example:

```text
/actuator/loggers
```

You can inspect or change configured logger levels depending on your security and management configuration.

This is useful for production troubleshooting.

---

# Section 14 — Caching

## 40. How does caching work in Spring Boot?

**Answer:**

Enable caching:

```java
@EnableCaching
```

Then:

```java
@Cacheable("users")
public User findUser(Long id) {
    return repository.findById(id)
        .orElseThrow();
}
```

Flow:

```text
Request
  |
  v
Cache?
 /   \
Yes   No
 |     |
Return  DB
        |
        v
      Cache
```

---

## 41. What is the difference between `@Cacheable`, `@CachePut`, and `@CacheEvict`?

**Answer:**

### `@Cacheable`

Uses cache before executing the method.

```java
@Cacheable("users")
```

### `@CachePut`

Always executes the method and updates cache.

```java
@CachePut("users")
```

### `@CacheEvict`

Removes entries.

```java
@CacheEvict("users")
```

Example:

```text
Create User
    |
    v
DB
    |
    v
Cache

Update User
    |
    v
DB
    |
    v
Update Cache
```

---

# Section 15 — Async and Scheduling

## 42. How do you execute asynchronous tasks in Spring Boot?

**Answer:**

Use:

```java
@EnableAsync
```

and:

```java
@Async
public CompletableFuture<Result> process() {
    ...
}
```

But for production systems, configure a proper executor instead of relying blindly on defaults.

For example:

```text
Core Threads
Maximum Threads
Queue Capacity
Thread Name Prefix
Rejected Execution Policy
```

Always understand the workload before configuring the pool.

---

## 43. How do you schedule jobs in Spring Boot?

**Answer:**

Use:

```java
@EnableScheduling
```

Then:

```java
@Scheduled(cron = "0 0 * * * *")
public void process() {
}
```

Important production concern:

If you have:

```text
10 Kubernetes pods
```

then a scheduled job may execute on all 10 pods.

For distributed scheduled jobs, consider:

```text
ShedLock
Quartz
Kubernetes CronJob
AWS EventBridge
External scheduler
```

depending on the requirement.

---

# Section 16 — Testing

## 44. What is `@SpringBootTest`?

**Answer:**

`@SpringBootTest` loads a Spring Boot application context for integration testing.

Example:

```java
@SpringBootTest
class UserServiceTest {
}
```

It is useful when you want to test multiple layers together.

But it is heavier than unit testing.

---

## 45. What is the difference between unit testing and integration testing?

**Answer:**

### Unit Test

Tests one component in isolation.

```text
Service
  |
  +--> Mock Repository
```

Fast.

### Integration Test

Tests multiple real components.

```text
Controller
   |
Service
   |
Repository
   |
Database
```

Slower but closer to production behavior.

A good test strategy uses both.

---

## 46. What are Spring Boot test slices?

**Answer:**

Test slices load only the relevant part of the application.

Examples include:

```text
@WebMvcTest
@DataJpaTest
@JsonTest
```

### `@WebMvcTest`

Focuses on MVC/controller layer.

### `@DataJpaTest`

Focuses on JPA/database-related behavior.

This is usually faster than loading the entire application context.

---

# Section 17 — Performance

## 47. A Spring Boot API suddenly has high latency. How would you troubleshoot it?

**Answer:**

I would not immediately increase the thread pool.

I'd investigate:

```text
1. p95/p99 latency
2. CPU
3. Heap
4. GC
5. Thread pools
6. DB latency
7. Connection pool
8. External API latency
9. Kafka/Redis latency
10. Network
11. Recent deployment
12. Error rate
```

Architecture:

```text
Request
 |
 +--> Controller
 |
 +--> Service
 |
 +--> DB
 |
 +--> Redis
 |
 +--> External API
 |
 +--> Kafka
```

Measure each dependency.

For example:

```text
p99 = 2 seconds

DB = 50 ms
Redis = 5 ms
External API = 1.8 sec
```

The bottleneck is obvious.

---

## 48. What is HikariCP and why is it important?

**Answer:**

HikariCP is a JDBC connection pool commonly used by Spring Boot applications.

Instead of opening a database connection for every request:

```text
Request
  |
  v
Create DB connection
  |
  v
Query
  |
  v
Close
```

we use:

```text
Hikari Pool
 |
 +--> Connection
 +--> Connection
 +--> Connection
 +--> Connection
```

Important settings include:

```text
maximumPoolSize
minimumIdle
connectionTimeout
idleTimeout
maxLifetime
```

Increasing pool size blindly can make database performance worse because the database itself has finite concurrency.

---

# Section 18 — Microservices

## 49. How do you implement REST communication between Spring Boot microservices?

**Answer:**

Modern Spring applications can use:

```text
RestClient
WebClient
Feign-style clients via Spring Cloud
```

depending on whether blocking or reactive communication is appropriate.

For a traditional blocking service:

```text
Order Service
     |
     v
Customer Service
```

For reactive applications:

```text
WebFlux
   |
   v
WebClient
```

The important senior-level concern is not merely choosing a client, but handling:

```text
Timeouts
Retries
Circuit breakers
Connection pools
Authentication
Observability
Idempotency
```

---

## 50. Why are timeouts important in microservices?

**Answer:**

Suppose:

```text
Service A
   |
   v
Service B
   |
   v
Service C
   |
   v
Database
```

If Service C hangs, Service B can consume threads waiting for it.

Eventually:

```text
Service A
   |
   +--> threads blocked
   +--> connection pool exhausted
   +--> request queue grows
   +--> latency increases
   +--> service fails
```

This is a cascading failure.

Always configure appropriate:

```text
Connection timeout
Read timeout
Request timeout
Circuit breaker
```

---

# Section 19 — Resilience

## 51. What is a circuit breaker?

**Answer:**

A circuit breaker prevents repeated calls to an unhealthy dependency.

States:

```text
CLOSED
   |
   | failures exceed threshold
   v
OPEN
   |
   | wait
   v
HALF_OPEN
   |
   +--> success -> CLOSED
   |
   +--> failure -> OPEN
```

Example:

```text
Order Service
     |
     v
Payment Service
     |
   FAILING
     |
     v
Circuit Breaker
     |
     v
Fallback / Error
```

Spring applications commonly use resilience libraries such as Resilience4j.

---

## 52. When should you NOT retry an operation?

**Answer:**

Don't blindly retry.

Retries are dangerous for non-idempotent operations.

Example:

```text
POST /payment
```

If payment succeeded but the response was lost, retrying could create a duplicate payment.

Use:

```text
Idempotency keys
```

and carefully classify errors.

Retry transient failures such as:

```text
temporary network failure
temporary timeout
503
```

but not every:

```text
400
401
403
business validation failure
```

---

# Section 20 — Messaging and Kafka

## 53. How would you integrate Kafka with Spring Boot?

**Answer:**

Spring Boot provides Kafka integration through Spring for Apache Kafka.

Architecture:

```text
Producer
   |
   v
Kafka Topic
   |
   v
Consumer
   |
   v
Spring Boot
```

Important production considerations:

```text
Partitions
Consumer groups
Offset management
Retries
Dead-letter topics
Idempotency
Ordering
Schema compatibility
Monitoring
```

---

## 54. What happens if a Kafka consumer crashes after processing but before committing the offset?

**Answer:**

Potentially the message can be processed again.

Example:

```text
Receive message
     |
     v
Process successfully
     |
     X
Crash before offset commit
```

After restart:

```text
Same message
     |
     v
Processed again
```

Therefore consumers should often be designed to be **idempotent**.

For example:

```text
eventId = 12345
```

Store processed event IDs or design operations so duplicate processing doesn't create an incorrect state.

---

# Section 21 — Deployment and Kubernetes

## 55. How do you containerize a Spring Boot application?

**Answer:**

Typical Docker flow:

```text
Source Code
    |
    v
Maven/Gradle Build
    |
    v
Spring Boot JAR
    |
    v
Docker Image
    |
    v
Container Registry
    |
    v
Kubernetes/EKS
```

Example:

```dockerfile
FROM eclipse-temurin:17-jre

COPY target/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
```

For production, use a suitable supported JDK/JRE base image, non-root execution where possible, minimal image size, predictable JVM/container settings, and vulnerability scanning.

---

## 56. How do you configure Spring Boot for Kubernetes?

**Answer:**

A typical architecture:

```text
                    EKS
                     |
        ┌────────────┼────────────┐
        |            |            |
       Pod          Pod          Pod
        |            |            |
 Spring Boot   Spring Boot   Spring Boot
```

Configuration:

```text
ConfigMap
   |
   v
Environment Variables
   |
   v
Spring Boot

Secret
   |
   v
Environment Variables / mounted secret
   |
   v
Spring Boot
```

Use:

```text
Readiness Probe
Liveness Probe
Resource Requests
Resource Limits
HPA
ConfigMaps
Secrets
```

---

# Section 22 — Native Images and Modern Boot

## 57. What is Spring Boot Native Image support?

**Answer:**

Spring Boot can be compiled into a native executable using GraalVM Native Image.

Traditional JVM:

```text
JAR
 |
 v
JVM
 |
 v
Application
```

Native:

```text
Application
 |
 v
Native Executable
```

Benefits can include:

```text
Fast startup
Lower memory footprint
Good fit for serverless/scale-to-zero
```

Trade-offs include:

```text
Build complexity
Native compatibility considerations
Reflection/configuration concerns
Potentially different runtime characteristics
```

Current Spring Boot documentation supports GraalVM Native Image builds; Boot 4.1 requires Java 17+ and documents GraalVM 25 support for native images. ([Home][5])

---

# Section 23 — Advanced Boot Internals

## 58. What is a custom Spring Boot Starter and why would an enterprise company create one?

**Answer:**

Suppose your company has 100 microservices.

Every service needs:

```text
Logging
Correlation ID
Security
Metrics
Kafka
Exception handling
Tracing
Common headers
```

Instead of duplicating configuration everywhere, create an internal starter:

```text
company-observability-spring-boot-starter
```

Then each service adds:

```xml
<dependency>
    <groupId>com.company</groupId>
    <artifactId>
        company-observability-spring-boot-starter
    </artifactId>
</dependency>
```

The starter can provide:

```text
Auto Configuration
Beans
Configuration Properties
Logging
Metrics
Tracing
```

Modern Boot custom auto-configuration uses `@AutoConfiguration` and registers auto-configuration classes through `AutoConfiguration.imports`. ([Home][3])

This is a very strong **Senior/Lead Developer** interview topic.

---

# Section 24 — Production Scenarios

## 59. Your Spring Boot application fails during startup. How do you troubleshoot it?

**Answer:**

I would classify the failure first.

### 1. Configuration

Check:

```text
application.yml
Environment variables
Secrets
Profiles
Config imports
```

### 2. Bean creation

Look for:

```text
BeanCreationException
UnsatisfiedDependencyException
```

### 3. Database

Check:

```text
Connection URL
Credentials
Network
Pool
Database availability
```

### 4. Port

Check:

```text
Port already in use
```

### 5. Dependency conflicts

Look for:

```text
NoSuchMethodError
ClassNotFoundException
NoClassDefFoundError
```

### 6. Auto-configuration

Run:

```bash
--debug
```

and inspect the condition evaluation report.

### 7. Kubernetes

Check:

```bash
kubectl logs
kubectl describe pod
kubectl get events
```

Then determine:

```text
Application problem
        vs
Configuration problem
        vs
Infrastructure problem
        vs
Dependency problem
```

---

## 60. A Spring Boot application is running successfully but p99 latency suddenly doubled. CPU is normal. How would you investigate?

**Answer:**

This is a **very strong Senior/Architect-level scenario**.

I would not immediately change JVM settings.

I'd investigate in this order:

```text
                    API
                     |
                     v
              Spring Boot
                     |
       ┌─────────────┼─────────────┐
       |             |             |
      DB           Redis         Kafka
       |             |             |
       v             v             v
   Latency        Latency       Consumer Lag
```

### Step 1 — Compare p50 vs p99

If:

```text
p50 = 50ms
p99 = 2s
```

the problem may affect only a subset of requests.

### Step 2 — Database

Check:

```text
Slow queries
Connection pool exhaustion
Lock contention
Database CPU
Missing indexes
N+1 queries
```

### Step 3 — External services

Check:

```text
HTTP latency
Timeouts
Retries
Circuit breakers
```

### Step 4 — Thread pools

Check:

```text
Tomcat/Jetty threads
Executor pools
Async tasks
Blocked threads
Queue sizes
```

### Step 5 — Connection pools

Check:

```text
Hikari active
Hikari idle
Hikari pending
Connection acquisition time
```

### Step 6 — JVM

Check:

```text
GC pauses
Heap
Allocation rate
Thread count
```

### Step 7 — Recent changes

Check:

```text
Deployment
Database changes
Configuration
Traffic pattern
Feature flags
Dependency versions
```

### Step 8 — Distributed tracing

Trace a slow request:

```text
API Gateway
     |
     v
Service A       20ms
     |
     v
Service B       1.8s  <-- bottleneck
     |
     v
Database        10ms
```

Then fix the actual bottleneck rather than blindly increasing:

```text
CPU
JVM heap
threads
connection pool
```

---

# 🔥 The 15 Spring Boot Questions You MUST Master

For a **Senior Java Developer interview**, if you have limited time, prioritize these:

| Priority | Topic                                               | Interview Importance |
| -------- | --------------------------------------------------- | -------------------- |
| ⭐⭐⭐⭐⭐    | `@SpringBootApplication` internals                  | Very High            |
| ⭐⭐⭐⭐⭐    | Auto-configuration                                  | Very High            |
| ⭐⭐⭐⭐⭐    | `@ConditionalOnClass` / `@ConditionalOnMissingBean` | Very High            |
| ⭐⭐⭐⭐⭐    | Spring Boot startup lifecycle                       | Very High            |
| ⭐⭐⭐⭐⭐    | `@ConfigurationProperties` vs `@Value`              | Very High            |
| ⭐⭐⭐⭐⭐    | Profiles/configuration management                   | Very High            |
| ⭐⭐⭐⭐⭐    | `@Transactional` internals                          | Very High            |
| ⭐⭐⭐⭐⭐    | Actuator                                            | Very High            |
| ⭐⭐⭐⭐⭐    | Liveness vs Readiness                               | Very High            |
| ⭐⭐⭐⭐⭐    | HikariCP                                            | Very High            |
| ⭐⭐⭐⭐⭐    | N+1 / JPA performance                               | Very High            |
| ⭐⭐⭐⭐⭐    | Microservice timeouts/retries                       | Very High            |
| ⭐⭐⭐⭐⭐    | Spring Boot + Kafka                                 | Very High            |
| ⭐⭐⭐⭐⭐    | Kubernetes/EKS deployment                           | Very High            |
| ⭐⭐⭐⭐⭐    | Production troubleshooting                          | **Critical**         |

---

# 🎯 How I Recommend Preparing These for a Senior Interview

Don't memorize the 60 answers word-for-word.

For each topic, prepare this **5-level explanation**:

```text
1. What is it?
       ↓
2. Why do we use it?
       ↓
3. How does it work internally?
       ↓
4. How have I used it in production?
       ↓
5. What problems/trade-offs did I face?
```

For example, for **Spring Boot Auto-Configuration**:

```text
What?
→ Automatically configures Spring infrastructure.

Why?
→ Reduces boilerplate.

Internally?
→ @EnableAutoConfiguration
→ AutoConfiguration imports
→ @Conditional checks
→ Bean registration.

Production?
→ Used JPA/Kafka/Actuator auto-configuration.

Troubleshooting?
→ --debug
→ Condition Evaluation Report.

Trade-off?
→ Convenience vs understanding hidden configuration.
```

That style of answer is much stronger for a **5+ year Senior Java/Spring Boot interview** than simply saying:

> "Spring Boot provides auto-configuration."

The current Boot documentation also explicitly covers **auto-configuration, externalized configuration, Actuator, testing, containerization, observability, and custom auto-configuration**, so these are the areas I would emphasize most in a modern interview. ([Home][4])

[Spring Boot Official Reference Documentation](https://docs.spring.io/spring-boot/reference/index.html?utm_source=chatgpt.com)

[Spring Boot System Requirements](https://docs.spring.io/spring-boot/system-requirements.html?utm_source=chatgpt.com)

[1]: https://docs.spring.io/spring-boot/reference/index.html?utm_source=chatgpt.com "Reference :: Spring Boot"
[2]: https://docs.spring.io/spring-boot/reference/using/auto-configuration.html?utm_source=chatgpt.com "Auto-configuration :: Spring Boot"
[3]: https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html?utm_source=chatgpt.com "Creating Your Own Auto-configuration :: Spring Boot"
[4]: https://docs.spring.io/spring-boot/reference/features/external-config.html?utm_source=chatgpt.com "Externalized Configuration :: Spring Boot"
[5]: https://docs.spring.io/spring-boot/system-requirements.html?utm_source=chatgpt.com "System Requirements :: Spring Boot"
