Spring Boot Senior Java Interview Master Guide 2026

This guide is designed for senior Java and Spring Boot interview preparation and production-oriented revision. It preserves the original interview questions, examples, scenarios and appendix material while adding a consistent explanation model.

## How to study each concept

For every major topic, read in this order:

1.**Definition** - what the term or annotation means.
2.**Concept explanation** - why it exists and how it fits into Spring.
3.**Lifecycle or request flow** - when the framework performs the work.
4.**Example** - how the idea appears in Java, configuration, SQL or HTTP.
5.**Comparison and decision guidance** - when to use it and what to avoid.
6.**Interview summary** - a concise answer to say aloud.

The guide has three complementary parts:

| Part | Purpose |
| --- | --- |
| Expanded interview sections | Canonical explanations with definitions, flows, examples and production pitfalls. |
| 60-question revision section | Preserved compact revision material for fast review after studying the expanded answers. |
| Production and current-industry appendix | Deeper guidance for reliability, security, performance, testing, deployment and senior-level design answers. |

The same concept may appear more than once because the guide serves both learning and rapid revision. The expanded answer should be treated as the primary explanation; the compact answer is a reminder, not a replacement for the detailed discussion.

## Section map

- [Core Spring and IoC concepts](#section-1-why-spring-boot)
- [Spring Boot internals](#section-2-how-spring-boot-works-internally)
- [Core, dependency-injection and web annotations](#part-b-spring-boot-annotations--comprehensive-guide)
- [Data access, transactions, AOP and operations](#section-6-data-access-annotations)
- [Scenario questions and bean lifecycle](#section-13-real-world-scenario-questions)
- [End-to-end real-time case studies](#section-15-end-to-end-real-time-case-studies)
- [Compact 60-question revision track](#spring-boot--60-senior-developer-interview-questions--answers)
- [Production and current-industry appendix](#production-and-current-industry-appendix)
- SQL and NoSQL interview questions: see [SQL_SENIOR_JAVA_SPRING_BOOT_INTERVIEW_MASTER_GUIDE_2026.md](SQL_SENIOR_JAVA_SPRING_BOOT_INTERVIEW_MASTER_GUIDE_2026.md) (Part A: SQL, Part B: NoSQL)

# Section 1: Why Spring Boot?

## Q1. What is Inversion of Control (IoC) and why do we need it?

**Definition:** Inversion of Control (IoC) is a design principle in which an external container, not the class itself, creates objects and connects them to the objects they depend on.

**Simple explanation:** Normally a class says *"I will build everything I need."* With IoC the class says *"Give me what I need; I will only do my job."* The control over object creation is **inverted** — it moves from your class to Spring.

### Real-life analogy

```text
Without IoC: You cook at home.
 You buy vegetables, gas, utensils, cook and clean yourself.

With IoC: You eat at a restaurant.
 You only place an order. The kitchen (Spring) supplies everything.
```

### Without IoC — tight coupling

```java
public class OrderService {

 private final OrderRepository orderRepository;
 private final PaymentService paymentService;
 private final EmailService emailService;

 public OrderService() {
 this.orderRepository = new JdbcOrderRepository(); // hardcoded
 this.paymentService = new StripePaymentService(); // hardcoded
 this.emailService = new SmtpEmailService(); // hardcoded
 }
}
```

Problems:

- `OrderService` is tightly coupled to specific implementations.
- You cannot swap `JdbcOrderRepository` for `MongoOrderRepository` without editing `OrderService`.
- You cannot test with mocks — the constructor always creates real objects that talk to a real database, payment gateway and mail server.
- If `SmtpEmailService` needs its own dependencies (host, port, credentials), `OrderService` must know about those too (transitive coupling).

### With IoC — loose coupling

```java
@Service
public class OrderService {

 private final OrderRepository orderRepository;
 private final PaymentService paymentService;
 private final EmailService emailService;

 // Spring injects the dependencies; OrderService creates nothing
 public OrderService(OrderRepository orderRepository,
PaymentService paymentService,
EmailService emailService) {
 this.orderRepository = orderRepository;
 this.paymentService = paymentService;
 this.emailService = emailService;
 }
}
```

### Real-time example 1: switching a payment provider without touching business code

An e-commerce company uses Stripe in the US. The business launches in India and must use Razorpay there.

```java
public interface PaymentGateway {
PaymentResult charge(BigDecimal amount, String cardToken);
}

@Service
@Profile("us")
class StripePaymentGateway implements PaymentGateway { /* calls Stripe API */ }

@Service
@Profile("india")
class RazorpayPaymentGateway implements PaymentGateway { /* calls Razorpay API */ }

@Service
public class CheckoutService {

 private final PaymentGateway paymentGateway;

 public CheckoutService(PaymentGateway paymentGateway) {
 this.paymentGateway = paymentGateway;
 }

 public OrderConfirmation checkout(Cart cart, String cardToken) {
PaymentResult result = paymentGateway.charge(cart.total(), cardToken);
 return OrderConfirmation.from(cart, result);
 }
}
```

```text
US deployment -> SPRING_PROFILES_ACTIVE=us -> Stripe injected
India deployment -> SPRING_PROFILES_ACTIVE=india -> Razorpay injected
CheckoutService -> unchanged, not even recompiled
```

### Real-time example 2: unit testing without a database or payment gateway

```java
class CheckoutServiceTest {

 @Test
void chargesCartTotal() {
PaymentGateway fakeGateway = mock(PaymentGateway.class);
 when(fakeGateway.charge(any(), any())).thenReturn(PaymentResult.success("TX-1"));

CheckoutService service = new CheckoutService(fakeGateway);
OrderConfirmation confirmation = service.checkout(cartWithTotal("500.00"), "tok_123");

 assertThat(confirmation.transactionId()).isEqualTo("TX-1");
 verify(fakeGateway).charge(new BigDecimal("500.00"), "tok_123");
 }
}
```

The test runs in milliseconds, needs no Spring context and never charges a real card — possible only because the dependency is supplied from outside.

### Benefits

| Benefit | Meaning | Real-time impact |
| --- | --- | --- |
| Loose coupling | Classes depend on interfaces, not implementations | Swap Stripe for Razorpay by configuration |
| Testability | Inject mocks easily | Fast unit tests in CI without external systems |
| Configurability | Choose implementations per environment | Mock email sender in DEV, real SMTP in PROD |
| Lifecycle management | Container creates, initializes and destroys objects | Connection pools closed cleanly on shutdown |

### Easy interview summary

> IoC means the responsibility for creating and wiring objects moves from my classes to the Spring container. My classes declare what they need, usually through a constructor, and Spring supplies it. This gives loose coupling, easy testing and environment-specific wiring — for example, switching payment providers by profile without changing business code.

### Q2. What is Dependency Injection (DI)? How does it relate to IoC?

**Definition:** Dependency Injection (DI) is the technique of supplying an object's dependencies from the outside — through its constructor, a setter or a field — instead of the object creating them itself.

**How it relates to IoC:**

```text
IoC = the principle -> "Don't create your dependencies; let someone else provide them."
DI = the mechanism -> "Here is HOW they are provided: constructor, setter or field."

IoC is the WHAT. DI is the HOW.
```

**Real-life analogy:** A car does not manufacture its own engine. The factory *injects* an engine during assembly. The same car model can receive a petrol, diesel or electric engine without redesigning the car body.

### Three types of DI

#### 1. Constructor injection (recommended)

```java
@Service
public class OrderService {

 private final OrderRepository repository;

 public OrderService(OrderRepository repository) { // injected via constructor
 this.repository = repository;
 }
}
```

#### 2. Setter injection (for optional dependencies)

```java
@Service
public class OrderService {

 private OrderRepository repository;

 @Autowired
 public void setRepository(OrderRepository repository) { // injected via setter
 this.repository = repository;
 }
}
```

#### 3. Field injection (discouraged)

```java
@Service
public class OrderService {

 @Autowired
 private OrderRepository repository; // injected into the field via reflection
}
```

### Comparison

| Criteria | Constructor | Setter | Field |
| --- | --- | --- | --- |
| Immutability (`final`) | Yes | No | No |
| Required dependencies | Enforced — object cannot exist without them | Optional — may stay `null` | Optional — may stay `null` |
| Testability | Easy — pass mocks to the constructor | Possible — call setters | Hard — needs reflection or a Spring context |
| Circular dependency detection | Fails fast at startup | Allowed (risky) | Allowed (risky) |
| Readability | All dependencies visible in one place | Scattered | Hidden |
| Best use | Mandatory collaborators | Optional or reconfigurable collaborators | Legacy code, some test classes |

### Real-time example: constructor for mandatory, `ObjectProvider` for optional

An insurance claim service **must** have a repository and a fraud checker, but an SMS notifier is optional — it exists only in regions where SMS is enabled.

```java
@Service
public class ClaimService {

 private final ClaimRepository claimRepository; // mandatory
 private final FraudCheckClient fraudCheckClient; // mandatory
 private final ObjectProvider<SmsNotifier> smsNotifier; // optional

 public ClaimService(ClaimRepository claimRepository,
FraudCheckClient fraudCheckClient,
ObjectProvider<SmsNotifier> smsNotifier) {
 this.claimRepository = claimRepository;
 this.fraudCheckClient = fraudCheckClient;
 this.smsNotifier = smsNotifier;
 }

 public Claim submit(ClaimRequest request) {
 fraudCheckClient.verify(request);
Claim claim = claimRepository.save(Claim.from(request));
 smsNotifier.ifAvailable(n -> n.send(request.phone(), "Claim " + claim.id() + " received"));
 return claim;
 }
}
```

```text
Japan region -> SmsNotifier bean exists -> SMS sent
Other regions -> no SmsNotifier bean -> step silently skipped, no NullPointerException
```

### Why field injection causes real problems

```java
// Field-injected class used in a plain unit test
OrderService service = new OrderService();
service.placeOrder(request); // NullPointerException: repository was never injected
```

With constructor injection the compiler forces the test to supply the dependency, so this bug cannot happen.

### Easy interview summary

> DI is how Spring implements IoC. I use constructor injection for mandatory dependencies because fields can be `final`, tests are simple and missing beans fail at startup. For optional dependencies I use `ObjectProvider` or a setter. I avoid field injection because it hides dependencies and breaks plain unit tests.

### Q3. What is the IoC Container in Spring? What are its types?

**Expected Answer:** The Spring IoC container is the part of Spring that creates, configures, connects, and manages application objects. Spring calls an object managed by the container a **bean**.

Think of the container as an application manager:

```text
Your classes say what they need
 |
 v
Spring container creates the objects
 |
 v
Spring injects their dependencies
 |
 v
Spring manages their lifecycle
```

The container is responsible for:

1.**Instantiating beans** - creating objects such as services, repositories and controllers.
2.**Configuring beans** - injecting dependencies and setting configuration properties.
3.**Managing the bean lifecycle** - calling initialization and destruction callbacks.
4.**Applying framework features** - creating proxies for transactions, caching, security, async execution and other infrastructure.
5.**Resolving dependencies** - finding the correct implementation when a class asks for an interface.

### A simple real-world analogy

Imagine a restaurant:

```text
Restaurant manager = Spring IoC container
Chef = OrderService bean
Ingredients = OrderRepository, PaymentService, EmailService
Opening procedure = bean initialization
Closing procedure = bean destruction
```

Without a manager, the chef must purchase and prepare every ingredient. With a manager, the chef declares what is needed and the manager supplies it. In the same way, a Spring service declares its dependencies in its constructor instead of creating them with `new`.

## The two important container abstractions

```text
 IoC Container
 |
 +----------------+----------------+
 | |
 BeanFactory ApplicationContext
 low-level API feature-rich container
```

### `BeanFactory`

`BeanFactory` is the basic, low-level IoC container interface. It provides the fundamental ability to create and retrieve beans. It is generally associated with lazy bean creation: a bean may be created when it is first requested.

### `ApplicationContext`

`ApplicationContext` extends `BeanFactory` and adds the features used by normal Spring and Spring Boot applications:

- Application event publishing.
- Internationalization through `MessageSource`.
- Resource loading.
- Environment and profile support.
- Automatic annotation processing such as `@Autowired` and `@PostConstruct`.
- Easier integration with AOP and other Spring infrastructure.
- Automatic detection of configuration classes and components.

In practice, you almost always use `ApplicationContext`. Spring Boot creates and configures it for you when the application starts. You rarely create an `ApplicationContext` manually in application code.

## BeanFactory and ApplicationContext comparison

| Aspect | `BeanFactory` | `ApplicationContext` |
| --- | --- | --- |
| Bean loading | Basic container behaviour; commonly lazy | Full container behaviour; singleton beans are normally created during startup |
| Event publishing | Not provided as a full application feature | Yes, through `ApplicationEventPublisher` |
| Internationalization | Not provided by the basic interface | Yes, through `MessageSource` |
| AOP support | Must be configured manually or separately | Integrated with Spring infrastructure |
| Resource loading | Basic | Rich `ResourceLoader` support |
| Environment/profiles | Basic or unavailable at the low-level API | Yes, through the `Environment` abstraction |
| Annotation processing | Minimal | Full support for annotations such as `@Autowired` and `@PostConstruct` |
| Typical use case | Very lightweight or memory-constrained environments | Normal Spring and Spring Boot applications |

## The key behavioural difference: when are beans created?

### BeanFactory example: lazy retrieval

The following illustrates the original lazy-loading idea. `XmlBeanFactory` is a historical Spring API and should not be used for new Spring Boot applications, but it makes the concept easy to see:

```java
// BeanFactory - the container exists, but a bean may not exist yet.
BeanFactory factory = new XmlBeanFactory(
 new ClassPathResource("beans.xml"));

// The OrderService bean is requested here.
OrderService service = factory.getBean(OrderService.class);

// Creation and dependency resolution happen when the bean is requested.
```

### ApplicationContext example: startup creation

```java
// ApplicationContext - normal Spring applications use this richer container.
ApplicationContext context =
 new ClassPathXmlApplicationContext("beans.xml");

// Singleton beans are normally created while the context is initialized.
OrderService service = context.getBean(OrderService.class);
```

With an `ApplicationContext`, a missing dependency or invalid configuration is normally discovered during startup. This is called **fail fast**: the application refuses to become ready instead of discovering the problem during a later user request.

With lazy creation, the same error may appear only when the specific bean is first requested. Lazy creation can reduce startup work, but it can also move failures from startup into runtime. Use it deliberately and test the first-use path.

## Easy interview summary

> The IoC container is Spring's object manager. It creates beans, injects their dependencies, applies framework infrastructure and manages their lifecycle. `BeanFactory` is the basic container interface, while `ApplicationContext` extends it with events, profiles, resources, internationalization and annotation processing. Spring Boot applications normally use `ApplicationContext` because it provides the features required by production applications.

---

### Q4. What are the different bean scopes? When would you use each?

**Expected Answer:** A bean scope determines **how many instances Spring creates and how long each instance lives**.

The most important question is:

```text
Should all requests share one object,
or should each request receive a different object?
```

For example, a stateless `OrderService` can usually be shared safely. A report builder containing a mutable `List` should usually not be shared between concurrent requests.

## Bean scope comparison

| Scope | Number of instances | Lifecycle | Typical use case |
| --- | --- | --- | --- |
| `singleton` (default) | One per Spring `ApplicationContext` | Usually from context startup until shutdown | Stateless services, repositories and controllers |
| `prototype` | A new instance each time Spring requests the bean | Created on demand; Spring does not manage final destruction | Stateful objects, builders and short-lived processing objects |
| `request` | One per HTTP request | Created when the request begins and ends with the request | Request-scoped data, audit information and request context |
| `session` | One per HTTP session | Created with the session and ends with the session | Shopping cart and user preferences |
| `application` | One per Servlet `ApplicationContext` | Lives for the application context | Shared web-application state and configuration |
| `websocket` | One per WebSocket session | Lives for the WebSocket session | WebSocket-specific state |

## 1. Singleton scope: the default

Singleton means **one instance per Spring `ApplicationContext`**, not one instance for the whole internet and not necessarily one instance across multiple application pods.

```text
ApplicationContext A
 -> one OrderService instance

ApplicationContext B
 -> a different OrderService instance
```

If an application runs in ten Kubernetes pods, each pod normally has its own singleton instance. A singleton is therefore local to one application context; it is not a distributed singleton.

Singleton beans are a good fit for stateless services:

```java
@Service
public class OrderService {

 private final OrderRepository orderRepository;

 public OrderService(OrderRepository orderRepository) {
 this.orderRepository = orderRepository;
 }

 public Order find(long orderId) {
 return orderRepository.findById(orderId)
 .orElseThrow();
 }
}
```

The service can be shared because the request-specific data is stored in method-local variables, not in mutable fields.

### Singleton safety rule

Avoid request-specific mutable fields in singleton beans:

```java
// Dangerous: all concurrent requests share this list.
private final List<String> currentOrderItems = new ArrayList<>();
```

Prefer local variables:

```java
public Order createOrder(CreateOrderRequest request) {
List<String> itemsForThisRequest = new ArrayList<>(request.items());
 return buildOrder(itemsForThisRequest);
}
```

## 2. Prototype scope: a new object when requested

Prototype means Spring creates a new instance each time the bean is requested from the container:

```java
@Component
@Scope("prototype")
public class ReportGenerator {

 private final List<String> data = new ArrayList<>();

 public void addData(String row) {
 data.add(row); // Each generator owns its own state.
 }

 public byte[] generate() {
 return buildReport(data);
 }
}
```

Use prototype scope only when the object genuinely needs independent mutable state. It is not a general performance feature. Creating many objects can increase allocation and garbage-collection pressure.

### Prototype lifecycle warning

Spring creates and injects a prototype bean, but it does not normally call its destruction callbacks when that prototype is discarded. If it owns files, sockets, threads or other resources, define explicit cleanup or use a different resource-management design.

## 3. Web scopes: request, session, application and websocket

Web scopes are available only when the corresponding web context exists:

-**Request scope:** a new object for each HTTP request.
-**Session scope:** one object for each user HTTP session.
-**Application scope:** one object for the web application's `ServletContext`.
-**WebSocket scope:** one object for each WebSocket session.

A request-scoped bean cannot simply be used like an ordinary singleton in every execution context. Spring commonly injects a proxy that finds the correct request object at runtime:

```java
@Component
@RequestScope
public class RequestAuditContext {

 private String correlationId;
}
```

If a request-scoped bean is injected into a singleton, use a scoped proxy or an `ObjectProvider` and ensure the method is called while an HTTP request is active. Calling it from a scheduled job or startup callback may fail because there is no current request.

## Prototype scope pitfall: injecting it into a singleton

This does **not** create a new `ReportGenerator` on every method call:

```java
@Service // singleton by default
public class OrderService {

 @Autowired
 private ReportGenerator reportGenerator;

 // The prototype is normally resolved once when this singleton is created.
 // Every call can therefore reuse the same instance.
}
```

The reason is simple:

```text
Application startup
 -> create singleton OrderService
 -> inject one ReportGenerator

First generateReport() call
 -> use the already injected ReportGenerator

Second generateReport() call
 -> use that same ReportGenerator again
```

The singleton has captured one prototype instance. The prototype scope is therefore defeated for repeated calls.

## Correct solution: request the prototype when needed

Use constructor injection with `ObjectProvider`, which is the modern Spring-friendly approach:

```java
@Service
public class OrderService {

 private final ObjectProvider<ReportGenerator> reportGenerators;

 public OrderService(ObjectProvider<ReportGenerator> reportGenerators) {
 this.reportGenerators = reportGenerators;
 }

 public byte[] generateReport() {
ReportGenerator generator = reportGenerators.getObject();
 generator.addData("...");
 return generator.generate();
 }
}
```

Now the sequence is:

```text
generateReport() call 1 -> ObjectProvider creates generator A
generateReport() call 2 -> ObjectProvider creates generator B
generateReport() call 3 -> ObjectProvider creates generator C
```

`ObjectFactory<ReportGenerator>` and `javax.inject.Provider<ReportGenerator>`/`jakarta.inject.Provider<ReportGenerator>` can express the same lookup-on-demand idea, depending on the Spring and Jakarta versions used by the project. `ObjectProvider` is useful because it is part of Spring and provides additional lookup methods.

## Scope selection guide

```text
Stateless shared service -> singleton
Mutable short-lived object -> prototype or explicit factory
Data only for one HTTP call -> request
Data for one logged-in session -> session
Web application shared state -> application
State for one WebSocket -> websocket
```

Before choosing a non-singleton scope, ask:

1. Who owns the state?
2. When should the state be created?
3. When should it be destroyed?
4. Can concurrent requests see or modify it?
5. Does the code also run outside an HTTP request?
6. Will the design still work when the application has multiple pods?

## Easy interview summary

> Bean scope controls the number and lifetime of bean instances. Singleton is the default and is appropriate for stateless services, but singleton fields must not hold request-specific mutable state. Prototype creates a new instance whenever Spring is asked for one, but injecting it directly into a singleton usually resolves it only once. Use `ObjectProvider`, `ObjectFactory`, a provider or an explicit factory when a singleton needs a fresh prototype for every operation. Request, session, application and WebSocket scopes are tied to web lifecycles and require the correct web context.

### Q10. What is a FactoryBean? How is it different from BeanFactory?

**Definition:**

- `BeanFactory` is the **container** — the Spring interface that creates and manages all beans.
- `FactoryBean<T>` is a **special bean** living inside the container whose job is to **produce another object** of type `T`.

**Simple explanation:** The names look alike but the roles are opposite. `BeanFactory` is the *factory building*; a `FactoryBean` is *one machine inside that building* that makes a complicated product.

| Aspect | `BeanFactory` | `FactoryBean<T>` |
| --- | --- | --- |
| What is it? | The IoC container itself | A bean that creates another bean |
| Role | Manages all beans | Encapsulates complex creation logic for one type |
| Who implements it? | Spring (for example `DefaultListableBeanFactory`) | You or a library |
| What do you inject? | Rarely injected directly | You inject the *product* `T`, not the factory |

### Example: building a pooled HTTP client

```java
@Component
public class HttpClientFactoryBean implements FactoryBean<CloseableHttpClient> {

 @Value("${http.connection.timeout:5000}")
 private int connectionTimeout;

 @Value("${http.max.connections:100}")
 private int maxConnections;

 @Override
 public CloseableHttpClient getObject() {
 return HttpClients.custom()
 .setDefaultRequestConfig(RequestConfig.custom()
 .setConnectTimeout(Timeout.ofMilliseconds(connectionTimeout))
 .build())
 .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
 .setMaxConnTotal(maxConnections)
 .build())
 .build();
 }

 @Override
 public Class<?> getObjectType() {
 return CloseableHttpClient.class;
 }

 @Override
 public boolean isSingleton() {
 return true;
 }
}
```

Now you inject the **product** directly — Spring calls `getObject()` behind the scenes:

```java
@Service
public class PartnerApiClient {

 private final CloseableHttpClient httpClient; // produced by HttpClientFactoryBean

 public PartnerApiClient(CloseableHttpClient httpClient) {
 this.httpClient = httpClient;
 }
}
```

To get the factory itself instead of the product, prefix the bean name with `&`:

```java
FactoryBean<?> factory = (FactoryBean<?>) context.getBean("&httpClientFactoryBean");
```

### Real-time examples you already use

| FactoryBean | Product it creates | Where you meet it |
| --- | --- | --- |
| `LocalContainerEntityManagerFactoryBean` | JPA `EntityManagerFactory` | Every Spring Data JPA application |
| `SqlSessionFactoryBean` | MyBatis `SqlSessionFactory` | MyBatis projects |
| `ProxyFactoryBean` | AOP proxy | Classic Spring AOP |
| Spring Data repository factory | Implementation of `interface OrderRepository extends JpaRepository` | You write only an interface; a factory creates the implementation |

### When to use which today

```text
Simple third-party object with a few settings -> @Bean method (preferred in modern Boot)
Reusable library/framework creating many similar products -> FactoryBean
```

### Easy interview summary

> `BeanFactory` is the Spring container. A `FactoryBean` is a bean inside the container that manufactures another object, used when creation is complex — for example JPA's `LocalContainerEntityManagerFactoryBean` or Spring Data creating repository implementations from interfaces. In application code I normally prefer a `@Bean` method.

### Q1. What is Spring Boot and why do we need it when we already have the Spring Framework?

**Definition:** Spring Boot is an opinionated layer on top of the Spring Framework that auto-configures common infrastructure, manages compatible dependency versions and packages the application as a self-running JAR.

**Simple explanation:** Spring Framework is a powerful box of parts. Spring Boot is the same box *already assembled* with sensible defaults — you only change what is different for your application.

**Real-life analogy:** Spring Framework is buying furniture parts and assembling them yourself. Spring Boot is buying the same furniture pre-assembled, while still being allowed to replace a drawer.

| Problem with plain Spring | How Spring Boot solves it |
| --- | --- |
| Extensive XML/Java configuration | Auto-configuration — sensible defaults applied automatically |
| Manually managing dependency versions | Starters and BOM — curated, compatible dependency sets |
| External server setup (Tomcat, Jetty) | Embedded server — runs as a standalone JAR |
| Complex project setup | Spring Initializr — generate a project in seconds |
| Manual health/metrics endpoints | Actuator — production-ready monitoring out of the box |

### Real-time example: the same REST service, before and after

```text
Plain Spring MVC (traditional) Spring Boot
------------------------------ -----------
1. web.xml / WebApplicationInitializer 1. Add spring-boot-starter-web
2. Register DispatcherServlet 2. Write @SpringBootApplication main class
3. Configure Jackson message converters 3. Write @RestController
4. Configure DataSource + connection pool 4. Put DB URL in application.yml
5. Configure EntityManagerFactory 5. java -jar app.jar
6. Configure TransactionManager
7. Pick compatible versions of ~20 JARs
8. Build WAR, install Tomcat, deploy WAR
```

A team migrating a legacy insurance-quote service to Boot typically deletes hundreds of lines of XML and ships a single Docker image instead of maintaining a separate Tomcat installation per environment.

In short: Spring Boot lets you focus on business logic instead of infrastructure plumbing.

### Q2. What are the key features of Spring Boot?

**Expected Answer:** Each feature removes one category of repetitive setup work.

| # | Feature | What it does | Real-time example |
| --- | --- | --- | --- |
| 1 | Auto-configuration | Creates beans based on classpath, properties and existing beans | Add the PostgreSQL driver and `spring.datasource.url`; Boot creates a HikariCP `DataSource` automatically |
| 2 | Starter dependencies | One dependency brings a tested set of libraries | `spring-boot-starter-web` brings Spring MVC, Jackson, validation support and Tomcat |
| 3 | Embedded server | Tomcat, Jetty or Undertow inside the JAR | The same `app.jar` runs on a laptop, in Docker and in Kubernetes |
| 4 | Actuator | Health, metrics, info, loggers | Kubernetes calls `/actuator/health/readiness` before sending traffic to a new pod |
| 5 | DevTools | Auto-restart and live reload in development | Change a controller, save, and the app restarts in about a second |
| 6 | Externalized configuration | Properties, YAML, profiles, environment variables | Same image uses a test DB in QA and a production DB in PROD through environment variables |
| 7 | Production-ready defaults | Logging, graceful shutdown, security integration | During a rolling deployment, in-flight requests finish before the pod stops |
| 8 | No code generation, no XML | Pure Java and annotations | New developers read Java classes instead of XML files |

### Interview tip

Do not just list features. Pick one and tell a short production story, for example: *"Actuator readiness probes let our Kubernetes rollout wait until the database pool was warm, so users never saw 503 errors during deployment."*

### Q3. What is the difference between Spring, Spring Boot, and Spring MVC?

**Expected Answer:** Spring, Spring MVC and Spring Boot are related, but they are not three competing frameworks. They are different layers with different responsibilities.

The easiest way to remember them is:

```text
Spring Framework = the foundation and toolbox
Spring MVC = the Spring web module for HTTP applications
Spring Boot = the automation and packaging layer built on Spring
```

## A simple building analogy

Imagine building and operating a house:

```text
Spring Framework
 = foundation, wiring, plumbing and general construction tools

Spring MVC
 = the doors, reception desk and rooms used to receive visitors

Spring Boot
 = the construction manager who chooses sensible defaults,
 arranges the parts and gives you a ready-to-run house
```

Spring Boot does not replace Spring Framework. It uses Spring Framework and removes much of the setup work. Spring MVC is one optional part of Spring that Spring Boot can configure automatically when you build a web application.

## 1. What is the Spring Framework?

The **Spring Framework** is the large foundational ecosystem. Its core feature is the IoC container, which creates objects and injects their dependencies. It also provides modules for common application needs:

- Dependency injection through IoC and DI.
- AOP for cross-cutting concerns such as transactions and logging.
- Data access and transaction management.
- Web support, including Spring MVC and WebFlux.
- Security integration through Spring Security.
- Testing support and integration with other Java libraries.

Spring Framework is like a box of powerful tools. You can choose the tools you need, but in traditional Spring applications you often configure those tools yourself using Java configuration, XML or manual application setup.

## 2. What is Spring MVC?

**Spring MVC** is a web framework module inside the Spring Framework. It helps a Java application receive HTTP requests and return HTML pages, JSON or other HTTP responses.

MVC means **Model-View-Controller**:

```text
Browser or API client
 |
 | HTTP request: GET /orders/10
 v
Controller -> receives the request
 |
 v
Service -> executes business logic
 |
 v
Repository -> reads or writes data
 |
 v
Controller -> returns a view or JSON response
```

The main Spring MVC component is the `DispatcherServlet`. It acts as a front controller: requests enter through it, it finds the matching controller method, and it coordinates the response.

For example:

```java
@RestController
@RequestMapping("/orders")
public class OrderController {

 private final OrderService orderService;

 public OrderController(OrderService orderService) {
 this.orderService = orderService;
 }

 @GetMapping("/{id}")
 public OrderResponse getOrder(@PathVariablelong id) {
 return orderService.findOrder(id);
 }
}
```

This controller is a Spring MVC component. It maps `GET /orders/10` to the `getOrder` method and converts the returned object to JSON through Spring's HTTP message converters.

Spring MVC can be used without Spring Boot, but you must configure more of the web infrastructure yourself, such as the `DispatcherServlet`, component scanning, view resolvers, message converters and the external servlet container.

## 3. What is Spring Boot?

**Spring Boot** is an opinionated layer built on top of the Spring Framework. It makes common Spring applications faster to create and easier to run by providing:

- Auto-configuration based on the dependencies on the classpath.
- Starter dependencies with compatible library versions.
- Embedded Tomcat, Jetty or Undertow for web applications.
- Externalized configuration through properties, YAML and environment variables.
- Production features such as Actuator health checks and metrics.
- An executable JAR that can run with `java -jar`.
- Sensible defaults that can be overridden when your application needs different behaviour.

The word **opinionated** means that Spring Boot chooses a common default for you. For example, if the web starter is present, Boot normally configures Spring MVC and an embedded server. You can still customize or replace those defaults.

## How the three fit together

```text
Spring Boot application
 |
 +-- uses Spring Framework
 | |
 | +-- IoC / Dependency Injection
 | +-- AOP and transactions
 | +-- Data access
 | +-- Spring MVC (when web support is included)
 |
 +-- adds auto-configuration, starters and embedded server
```

A typical Spring Boot REST application therefore uses all three ideas:

1.**Spring Framework** creates the beans and injects dependencies.
2.**Spring MVC** maps HTTP requests to controller methods.
3.**Spring Boot** configures the application and starts the server with minimal setup.

## Side-by-side comparison

| Aspect | Spring Framework | Spring MVC | Spring Boot |
| --- | --- | --- | --- |
| What is it? | The core Spring ecosystem and IoC/DI foundation | A web MVC module inside Spring Framework | An opinionated layer that simplifies Spring application setup |
| Main purpose | Dependency injection, AOP, data access, transactions and other application infrastructure | Build HTTP applications using controllers, models, views and REST endpoints | Start production-ready Spring applications quickly with sensible defaults |
| Scope | Broad; includes many modules | Focused on the web layer | Focused on application setup, defaults, packaging and operations |
| Configuration | Traditionally more manual using XML or Java configuration | Traditionally requires web infrastructure such as `DispatcherServlet` and view resolvers | Auto-configures common infrastructure based on the classpath and properties |
| Server | Does not provide an application server by itself | Commonly deployed to an external servlet container when used without Boot | Usually provides an embedded Tomcat, Jetty or Undertow server |
| Standalone execution | Not by itself | Not by itself in a traditional setup | Yes, commonly with `java -jar app.jar` |
| Relationship | Foundation used by the other two | A web module provided by Spring | Uses Spring Framework and can configure Spring MVC |

## Configuration difference: traditional Spring MVC versus Spring Boot

### Traditional Spring MVC

In a traditional application, you may need to configure the servlet and web components yourself:

```text
1. Register DispatcherServlet
2. Configure component scanning
3. Configure view resolvers or JSON converters
4. Configure the external Tomcat deployment
5. Package the application as a WAR file
6. Deploy the WAR to Tomcat
```

### Spring Boot with Spring MVC

With Spring Boot, the same type of application commonly starts with:

```java
@SpringBootApplication
public class OrderApplication {

 public static void main(String[] args) {
 SpringApplication.run(OrderApplication.class, args);
 }
}
```

When the appropriate web starter is on the classpath, Spring Boot can automatically configure Spring MVC, the `DispatcherServlet`, JSON support and an embedded server. You focus mainly on controllers, services and business logic.

## Important clarification: Spring Boot does not always mean Spring MVC

Spring Boot can create different kinds of applications:

- REST APIs and web applications using Spring MVC.
- Reactive applications using Spring WebFlux.
- Batch applications using Spring Batch.
- Scheduled workers.
- Command-line applications with no web server.

Therefore:

```text
Every Spring Boot application uses Spring Framework.
Some Spring Boot applications use Spring MVC.
Not every Spring Boot application is a web application.
```

## One request through a Spring Boot MVC application

For a request such as `GET /orders/10`, the flow is usually:

```text
Client
 -> embedded Tomcat receives the HTTP request
 -> DispatcherServlet from Spring MVC receives it
 -> matching @RestController method is selected
 -> controller calls a service bean
 -> service calls a repository bean
 -> response object is converted to JSON
 -> client receives the HTTP response
```

Spring Boot starts and configures the infrastructure, Spring MVC handles the HTTP request, and the Spring Framework IoC container creates and connects the controller, service and repository beans.

## Easy interview summary

> Spring Framework is the foundation that provides IoC, dependency injection, AOP, transactions and many other modules. Spring MVC is the web module inside Spring Framework that handles HTTP requests using controllers, models, views and REST endpoints. Spring Boot is an opinionated layer on top of Spring that provides auto-configuration, starter dependencies, embedded servers, externalized configuration and production-ready features. They are layers, not alternatives: a Spring Boot web application uses Spring Framework, can use Spring MVC, and uses Boot to reduce configuration and simplify running the application.

# Section 2: How Spring Boot Works Internally

### Q4. What happens when you run a Spring Boot application? Explain the startup process.

**Definition:** Application startup is the process in which Spring Boot creates the application context, loads configuration, discovers and creates beans, starts the embedded server when needed, and signals that the application can accept work.

**Concept explanation:** `SpringApplication.run(MyApp.class, args)` is not just a call that starts Tomcat. It bootstraps the Spring IoC container and coordinates several phases. A failure in an early phase prevents later phases from running.

```text
main()
 -> SpringApplication.run()
 -> Environment and configuration are prepared
 -> ApplicationContext is created
 -> Bean definitions and auto-configuration are loaded
 -> Beans are created and dependencies are injected
 -> Embedded server starts, if this is a web application
 -> runners execute
 -> ApplicationReadyEvent is published
```

When you call `SpringApplication.run(MyApp.class, args)`, the simplified sequence is:

1. Create the `SpringApplication` instance.
2. Determine the application type: `SERVLET`, `REACTIVE` or `NONE`.
3. Load run listeners that publish lifecycle events.
4. Prepare the `Environment` from configuration files, environment variables, system properties and command-line arguments.
5. Create the appropriate `ApplicationContext`, such as `AnnotationConfigServletWebServerApplicationContext`.
6. Load bean definitions from component scanning and `@Configuration` classes.
7. Apply auto-configuration enabled by `@EnableAutoConfiguration`.
8. Refresh the context: create singleton beans, resolve dependencies and run initialization callbacks.
9. Start the embedded web server, such as Tomcat or Jetty, for a web application.
10. Call `ApplicationRunner` and `CommandLineRunner` beans.
11. Publish `ApplicationReadyEvent` to indicate that startup is complete.

### Startup phases and typical failures

| Phase | What happens | Typical failure |
| --- | --- | --- |
| Environment preparation | Properties and profiles are loaded | Invalid YAML, unresolved placeholder or bad configuration value |
| Context creation | The correct Spring context is selected | Missing web or reactive dependency |
| Bean definition loading | Components and configuration classes are discovered | Component is outside the scan package |
| Bean creation | Constructors and factory methods run | `BeanCreationException`, circular dependency or missing bean |
| Context refresh | Post-processors and lifecycle callbacks run | Invalid proxy, database connection or validation failure |
| Server startup | The embedded server binds to a port | Port already in use or invalid server configuration |
| Runner execution | Startup tasks run | A runner throws an exception or blocks indefinitely |

This classification makes startup logs easier to read. For example, an error before `ApplicationContext` refresh usually points to configuration or bean registration. An error about a port usually appears later, while a slow `CommandLineRunner` can delay readiness even though all beans were created successfully.

### Key lifecycle events

| Event | Meaning |
| --- | --- |
| `ApplicationStartingEvent` | Startup has begun; the environment and context are not ready yet. |
| `ApplicationEnvironmentPreparedEvent` | The environment is prepared, but the application context has not been created. |
| `ApplicationContextInitializedEvent` | The context exists, but bean definitions have not been fully loaded. |
| `ApplicationPreparedEvent` | Bean definitions are loaded and the context is prepared for refresh. |
| `ApplicationStartedEvent` | The context is refreshed and the server is started, but runners have not completed. |
| `ApplicationReadyEvent` | Startup work, including runners, has completed and the application is ready. |
| `ApplicationFailedEvent` | Startup failed at some point and the application could not become ready. |

For Kubernetes or another orchestrator, a process being alive is not the same as being ready. Readiness checks should represent whether the application can serve traffic, and startup tasks should not block readiness indefinitely.

### Q5. What is Auto-Configuration? How does it work internally?

**Definition:** Auto-configuration is Spring Boot's mechanism for creating common infrastructure beans automatically when the application has the required dependencies, configuration properties and conditions.

**Concept explanation:** Auto-configuration is conditional, not magical. Boot examines the classpath, the beans already registered and the current environment. It applies a default only when its conditions match. If the application supplies its own bean, Boot commonly backs away so the application can customize the behavior.

The internal flow is:

```text
@SpringBootApplication
 -> @EnableAutoConfiguration
 -> AutoConfigurationImportSelector
 -> candidate auto-configuration classes
 -> @Conditional checks
 -> matching @Bean methods
 -> beans added to the ApplicationContext
```

How it works:

1. `@SpringBootApplication` includes `@EnableAutoConfiguration`.
2. `@EnableAutoConfiguration` imports `AutoConfigurationImportSelector`.
3. The selector reads `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` in Spring Boot 3.x. Older Boot versions used `META-INF/spring.factories`.
4. The imports file lists candidate auto-configuration classes.
5. Each candidate has conditions, so it activates only when the required classes, beans, properties or application type are present.
6. Matching configuration contributes beans to the application context.

Example based on `DataSourceAutoConfiguration`:

```java
@AutoConfiguration
@ConditionalOnClass({DataSource.class, EmbeddedDatabaseType.class})
@ConditionalOnMissingBean(type = "io.r2dbc.spi.ConnectionFactory")
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceAutoConfiguration {

 @Bean
 @ConditionalOnMissingBean
 public DataSource dataSource(DataSourceProperties properties) {
 return properties.initializeDataSourceBuilder().build();
 }
}
```

This means:

- The configuration is relevant when JDBC `DataSource` classes are available on the classpath.
- It does not apply when the application is using the conflicting R2DBC connection setup represented by the condition.
- The default `DataSource` is created only when the application has not already defined one.
- The application can override the default by declaring its own `DataSource` bean or changing the relevant properties.

### How to troubleshoot auto-configuration
Run the application with:

```bash
java -jar application.jar --debug
```

or:

```bash
java -Ddebug -jar application.jar
```

The condition evaluation report helps answer two questions:

```text
Why was this bean created?
 -> a positive condition matched

Why was this bean not created?
 -> a required class, property or bean was missing
```

In a running application, `/actuator/conditions` can provide similar information when that endpoint is enabled and secured appropriately. Do not enable sensitive Actuator endpoints publicly just for debugging.

### Q6. What are the key @Conditional annotations used in auto-configuration?

**Definition:** A conditional annotation tells Spring whether a configuration class or bean should be registered. Conditions allow Boot to provide defaults without forcing every application to use the same infrastructure.

| Annotation | Condition |
| --- | --- |
| `@ConditionalOnClass` | A specified class is available on the classpath. |
| `@ConditionalOnMissingClass` | A specified class is not available on the classpath. |
| `@ConditionalOnBean` | A specified bean already exists in the context. |
| `@ConditionalOnMissingBean` | A specified bean does not already exist in the context. |
| `@ConditionalOnProperty` | A property has the expected value or is present. |
| `@ConditionalOnResource` | A resource exists on the classpath. |
| `@ConditionalOnWebApplication` | The application is a web application. |
| `@ConditionalOnExpression` | A SpEL expression evaluates to `true`; use carefully because it is less type-safe. |

The evaluation model is:

```text
Candidate configuration
 -> evaluate every condition
 -> all required conditions match?
 | yes -> register the configuration or bean
 | no -> skip it and record the reason
```

Example:

```java
@Bean
@ConditionalOnProperty(
 name = "feature.notifications.enabled",
 havingValue = "true")
public NotificationService notificationService() {
 return new EmailNotificationService();
}
```

This bean is created only when `feature.notifications.enabled=true` is available in the environment. If the property is absent or has another value, the method is skipped.

### Q7. What is the role of @SpringBootApplication? What does it combine?

**Definition:** `@SpringBootApplication` is the usual entry-point annotation for a Spring Boot application. It combines the application configuration marker, auto-configuration activation and component scanning.

It is effectively:

```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
public @interface SpringBootApplication {
}
```

| Annotation | Purpose |
| --- | --- |
| `@SpringBootConfiguration` | Identifies the main Boot configuration class; it is a specialized `@Configuration`. |
| `@EnableAutoConfiguration` | Activates conditional Boot configuration based on the classpath and environment. |
| `@ComponentScan` | Finds `@Component`, `@Service`, `@Repository`, `@Controller` and related classes in the scan area. |

The startup class is commonly placed at the root package:

```text
com.example.myapp
|
+-- MyApplication.java <- @SpringBootApplication
+-- controller/
| `-- OrderController.java <- found by component scanning
+-- service/
| `-- OrderService.java <- found by component scanning
`-- repository/
 `-- OrderRepository.java <- found by component scanning
```

If the main class is placed too deeply, sibling packages may fall outside the default scan range. The result is often a missing-bean error even though the class exists in the source tree. Use an explicit `scanBasePackages` only when the package layout requires it; a clean root package is usually easier to maintain.

### Q8. What is the difference between application.properties and application.yml? What is the property loading order?

**Definition:** Externalized configuration means keeping values such as ports, URLs, credentials, feature flags and timeouts outside the Java code so they can change per environment without recompiling the application.

`application.properties` and `application.yml` are two formats for expressing the same configuration model.

```properties
# application.properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
```

```yaml
# application.yml
server:
 port: 8080

spring:
 datasource:
 url: jdbc:mysql://localhost:3306/mydb
 username: root
```

YAML is useful for hierarchical values and lists. Properties are simple and avoid indentation-sensitive syntax. The format does not change the meaning of the property after Spring Boot loads it.

### Property source priority

When the same property exists in several places, the higher-precedence source wins. The relevant order, from highest to lowest, is:

1. Command-line arguments, such as `--server.port=9090`.
2. `SPRING_APPLICATION_JSON`, an inline JSON environment value.
3. Servlet initialization parameters.
4. JNDI attributes.
5. Java system properties, such as `-Dserver.port=9090`.
6. Operating-system environment variables, such as `SERVER_PORT=9090`.
7. Profile-specific configuration, such as `application-dev.properties`.
8. Application configuration, such as `application.properties` or `application.yml`.
9. `@PropertySource` values declared by configuration classes.
10. Default properties configured through `SpringApplication.setDefaultProperties(...)`.

For example, if the application file says `server.port=8080` and the command line says `--server.port=9090`, the command-line value wins.

```text
Need to know the effective value?
 -> search the highest-precedence source first
 -> check active profiles
 -> inspect environment and command-line overrides
 -> use Actuator /env only when it is secured
```

### Q9. What are Spring Boot Profiles and how do you use them?

**Definition:** A Spring profile is a named environment or feature configuration set. Profiles let the same application use different properties and beans for development, testing, staging and production.

The usual model is:

```text
Common configuration
 + profile-specific overrides
 -> effective Environment
 -> active beans and application behavior
```

Define profile-specific files:

```text
application.properties <- common configuration
application-dev.properties <- development overrides
application-staging.properties <- staging overrides
application-prod.properties <- production overrides
```

Activate a profile in several ways:

```properties
# application.properties
spring.profiles.active=dev
```

```bash
java -jar app.jar --spring.profiles.active=prod
```

```text
SPRING_PROFILES_ACTIVE=prod
```

Command-line and environment configuration are generally preferred for deployment because the artifact does not need to change between environments. Avoid storing passwords in profile files; use a secret manager or injected secret configuration.

Profiles can also control which beans exist:

```java
@Configuration
@Profile("dev")
public class DevConfig {

 @Bean
 public DataSource dataSource() {
 return new H2DataSource();
 }
}

@Configuration
@Profile("prod")
public class ProdConfig {

 @Bean
 public DataSource dataSource() {
 return new HikariDataSource();
 }
}
```

When `dev` is active, only the development configuration is eligible. When `prod` is active, the production configuration is eligible. Multiple profiles can be active together, for example `prod,metrics`, but make sure their bean definitions do not conflict.

Multiple profiles: `spring.profiles.active=prod,metrics` activates both.

### Real-time example: one image, four environments

```text
application.yml -> shared: server.port, Jackson settings, actuator exposure
application-local.yml -> H2/Docker DB, mock email sender, DEBUG logging
application-qa.yml -> QA database host, real email sender to a test mailbox
application-prod.yml -> PROD database host, INFO logging, strict timeouts

Secrets (DB password, API keys) -> NOT in any file; injected from Key Vault / Secrets Manager
```

```java
@Configuration
class MailConfig {

 @Bean
 @Profile("local")
EmailSender loggingEmailSender() {
 return msg -> log.info("[FAKE EMAIL] to={} subject={}", msg.to(), msg.subject());
 }

 @Bean
 @Profile("!local")
EmailSender smtpEmailSender(JavaMailSender mailSender) {
 return new SmtpEmailSender(mailSender);
 }
}
```

A developer running locally can never accidentally email real customers, because the real sender bean does not exist in the `local` profile.

# PART B: SPRING BOOT ANNOTATIONS — COMPREHENSIVE GUIDE

# Section 3: Core / Stereotype Annotations

### Q10. Explain all the stereotype annotations and their differences.

**Expected Answer:**

**Definition:** A stereotype annotation identifies the role of a Spring-managed class. It helps component scanning discover the class and communicates whether the class belongs to the general component, service, persistence, web or configuration layer.

**Concept explanation:** These annotations are not independent dependency-injection systems. `@Service`, `@Repository` and `@Controller` are specialized forms of `@Component`, so Spring can discover all of them through component scanning. Their additional value is semantic and, for some annotations, framework behavior such as exception translation or MVC handler registration.

```text
@Component
 +-- @Service -> business operations
 +-- @Repository -> data access and exception translation
 +-- @Controller -> MVC request handling and views
 +-- @RestController -> MVC request handling and response bodies
```

| Annotation | Purpose | Layer | Extra behaviour |
| --- | --- | --- | --- |
| `@Component` | Generic Spring-managed bean | Any | None |
| `@Service` | Business logic bean | Service layer | None (semantic only) |
| `@Repository` | Data access bean | Persistence layer | Translates SQL/JPA exceptions into `DataAccessException` |
| `@Controller` | Web controller returning views | Web layer | Registered as MVC handler |
| `@RestController` | REST controller returning JSON/XML | Web layer | `@Controller` + `@ResponseBody` |
| `@Configuration` | Class that defines `@Bean` methods | Configuration | CGLIB proxy keeps `@Bean` methods singleton |

**Key point:** `@Service`, `@Repository` and `@Controller` are all specializations of `@Component`. The source code proves it:

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component // it IS a component
public @interface Service { }
```

### Real-time example: a claims application laid out by stereotype

```java
@RestController // web layer: receives HTTP, returns JSON
@RequestMapping("/api/claims")
class ClaimController {
 private final ClaimService claimService;
 ClaimController(ClaimService claimService) { this.claimService = claimService; }

 @PostMapping
ClaimResponse submit(@Valid @RequestBodyClaimRequest request) {
 return claimService.submit(request);
 }
}

@Service // business rules live here
class ClaimService {
 private final ClaimRepository claimRepository;
 private final PolicyValidator policyValidator;
 ClaimService(ClaimRepository r, PolicyValidator v) { this.claimRepository = r; this.policyValidator = v; }

 @Transactional
ClaimResponse submit(ClaimRequest request) {
 policyValidator.ensureActive(request.policyNumber());
 return ClaimResponse.from(claimRepository.save(Claim.from(request)));
 }
}

@Repository // data access + exception translation
interface ClaimRepository extends JpaRepository<Claim, Long> { }

@Component // reusable helper that is not "business" or "data"
class PolicyValidator {
void ensureActive(String policyNumber) { /* ... */ }
}
```

### Why `@Repository` exception translation matters

```text
Without translation: service catches org.postgresql.util.PSQLException (vendor-specific)
With @Repository: service catches DataIntegrityViolationException (Spring, vendor-neutral)
```

If the company later moves from PostgreSQL to Oracle or Azure SQL, the service-layer `catch` blocks do not change.

### Easy interview summary

> All stereotypes are `@Component` for scanning purposes. `@Service` documents business logic, `@Repository` adds persistence exception translation, `@Controller` registers MVC handlers and `@RestController` adds `@ResponseBody`. Using the right stereotype makes layering obvious in code reviews and enables targeted AOP pointcuts such as "all `@Service` methods".

### Q11. What is the difference between @Component and @Bean?

**Expected Answer:**

**Definition:** `@Component` marks a class for automatic discovery by component scanning. `@Bean` marks a method whose return value should be registered as a Spring bean.

**Concept explanation:** Use `@Component` when you own the class and its role is naturally expressed by a class-level stereotype. Use `@Bean` when you need explicit construction logic, need to configure a third-party class, or want to select a specific factory method.

```text
Component scanning -> discovers an annotated class -> creates the bean
@Bean method -> configuration method runs -> registers its return value
```

The practical decision is:

| Situation | Preferred choice |
| --- | --- |
| Application service or controller you own | `@Service`, `@Controller` or another stereotype |
| Third-party object you cannot annotate | `@Bean` |
| Several construction steps or custom settings | `@Bean` |
| Simple application component | `@Component` or a more specific stereotype |

| Aspect | `@Component` | `@Bean` |
| --- | --- | --- |
| Target | Class-level | Method-level (inside `@Configuration`) |
| Detection | Auto-detected via `@ComponentScan` | Explicitly declared |
| Ownership | You must own the class source code | Works for third-party classes you cannot edit |
| Customization | Constructor/setter injection only | Full control — call any constructor, builder or setter |
| Multiple instances of same type | Awkward | Easy — write two `@Bean` methods with different names |

**When to use `@Bean`:** when you need a bean from a class you cannot annotate (third-party library):

```java
@Configuration
public class AppConfig {

 @Bean
 public RestTemplate restTemplate(RestTemplateBuilder builder) {
 return builder
 .setConnectTimeout(Duration.ofSeconds(5))
 .setReadTimeout(Duration.ofSeconds(10))
 .build();
 }

 @Bean
 public ObjectMapper objectMapper() {
ObjectMapper mapper = new ObjectMapper();
 mapper.registerModule(new JavaTimeModule());
 mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
 return mapper;
 }
}
```

You cannot put `@Component` on `RestTemplate` or `ObjectMapper` — they are library classes.

### Real-time example: two clients of the same type with different settings

A notification service calls a fast internal template API (2-second timeout) and a slow external mail provider (15-second timeout). Both are `RestClient` objects, so `@Component` cannot express this; `@Bean` can.

```java
@Configuration
public class HttpClientsConfig {

 @Bean
RestClient templateApiClient(@Value("${template.api.url}") String url) {
 return RestClient.builder()
 .baseUrl(url)
 .requestFactory(timeouts(Duration.ofSeconds(2)))
 .build();
 }

 @Bean
RestClient mailProviderClient(@Value("${mail.provider.url}") String url) {
 return RestClient.builder()
 .baseUrl(url)
 .requestFactory(timeouts(Duration.ofSeconds(15)))
 .build();
 }

 private ClientHttpRequestFactory timeouts(Duration read) {
SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
 f.setConnectTimeout(Duration.ofSeconds(2));
 f.setReadTimeout(read);
 return f;
 }
}

@Service
class EmailDispatcher {
 private final RestClient mailProviderClient;

 EmailDispatcher(@Qualifier("mailProviderClient") RestClient mailProviderClient) {
 this.mailProviderClient = mailProviderClient;
 }
}
```

### Easy interview summary

> `@Component` is for my own classes discovered by scanning. `@Bean` is for objects I must construct explicitly — third-party classes, objects needing builders, or several differently configured instances of the same type.

### Q12. What is @Configuration and how does it differ from @Component internally?

**Expected Answer:** @Configuration is a special @Component that uses CGLIB proxying to ensure @Bean methods return singleton instances.

**Definition:** `@Configuration` marks a class as a source of bean definitions. It is a specialized configuration component whose `@Bean` methods participate in Spring's bean lifecycle and dependency management.

**Concept explanation:** In full configuration mode, Spring enhances the configuration class with a proxy. A call from one `@Bean` method to another is intercepted and resolved through the container, so the existing managed bean is returned instead of constructing a second object with `new`.

```text
serviceA() -> commonDependency()
 -> configuration proxy
 -> existing CommonDependency bean
```

This is why direct calls between `@Bean` methods behave differently from ordinary method calls. A component class can also contain `@Bean` methods, but it is treated as lite configuration and direct method calls do not receive the same full configuration enhancement.

```java
@Configuration
public class AppConfig {

 @Bean
 public ServiceA serviceA() {
 return new ServiceA(commonDependency()); // calls commonDependency()
 }

 @Bean
 public ServiceB serviceB() {
 return new ServiceB(commonDependency()); // calls commonDependency() again
 }

 @Bean
 public CommonDependency commonDependency() {
 return new CommonDependency();
 }
}
```

| Mode | Class annotation | Calls to `commonDependency()` | Result |
| --- | --- | --- | --- |
| Full mode | `@Configuration` | Intercepted by CGLIB proxy | `ServiceA` and `ServiceB` share **one** instance |
| Lite mode | `@Component` (or `@Configuration(proxyBeanMethods = false)`) | Plain Java method call | **Two** separate instances — singleton contract broken |

### Real-time example: accidentally creating two connection pools

```java
@Component // WRONG: lite mode
public class DbConfig {

 @Bean
 public DataSource dataSource() {
 return new HikariDataSource(hikariConfig()); // pool of 20 connections
 }

 @Bean
 public JdbcTemplate jdbcTemplate() {
 return new JdbcTemplate(dataSource()); // creates a SECOND pool of 20 connections
 }
}
```

```text
Expected: 20 DB connections per pod
Actual: 40 DB connections per pod x 10 pods = 400 -> database hits max_connections
```

**Fix:** use `@Configuration`, or — even better — receive the dependency as a method parameter, which works correctly in both modes:

```java
@Bean
public JdbcTemplate jdbcTemplate(DataSource dataSource) {
 return new JdbcTemplate(dataSource);
}
```

Spring Boot's own auto-configuration uses `proxyBeanMethods = false` with parameter injection because it starts faster (no CGLIB subclass) and is safe.

# Section 4: Dependency Injection Annotations

### Q13. Compare @Autowired, @Inject, @Resource, and constructor injection. Which is preferred?

**Expected Answer:**

**Definition:** Dependency-injection annotations tell Spring how to provide an object that another object needs. Constructor injection expresses those required dependencies in the class constructor and is generally the preferred style.

**Concept explanation:** Spring first resolves the injection point, then chooses a candidate bean. Type-based annotations usually begin with the required type; name-based resolution can be selected with `@Qualifier` or `@Resource`.

```text
Injection point
 -> determine required type and optional name
 -> find candidate beans
 -> one candidate? inject it
 -> several candidates? use @Primary or @Qualifier
 -> no candidate? fail startup for a required dependency
```

Constructor injection makes the dependency visible, allows `final` fields, supports plain unit tests and fails early when a required bean is absent. Field injection hides the dependency and makes the object harder to construct outside Spring.

| Annotation | Source | Lookup strategy | `required=false` support |
| --- | --- | --- | --- |
| `@Autowired` | Spring | By type first, then by name if ambiguous | Yes |
| `@Inject` | JSR-330 / Jakarta Inject | By type (equivalent to `@Autowired`) | No (use `Optional`/`Provider`) |
| `@Resource` | JSR-250 / Jakarta Annotations | By name first, then by type | No |
| Constructor (no annotation) | Spring 4.3+ | By type, single constructor inferred | Use `Optional` or `ObjectProvider` |

**Constructor injection (preferred):**

```java
@Service
public class OrderService {

 private final OrderRepository orderRepository;
 private final PaymentService paymentService;

 // @Autowired is optional when there is only one constructor
 public OrderService(OrderRepository orderRepository, PaymentService paymentService) {
 this.orderRepository = orderRepository;
 this.paymentService = paymentService;
 }
}
```

With Lombok this becomes even shorter:

```java
@Service
@RequiredArgsConstructor // generates the constructor for final fields
public class OrderService {
 private final OrderRepository orderRepository;
 private final PaymentService paymentService;
}
```

Why constructor injection is preferred:

1.**Immutability** — fields can be `final`.
2.**Required dependencies** — the object cannot be created with `null` collaborators.
3.**Testability** — pass mocks in unit tests without Spring.
4.**No reflection** on private fields.
5.**Fail fast** — the application will not start if a dependency is missing.
6.**Design feedback** — a constructor with 9 parameters is a visible signal that the class does too much.

**Field injection (discouraged):**

```java
@Service
public class OrderService {

 @Autowired // discouraged
 private OrderRepository orderRepository;
}
```

Problems: cannot be `final`, harder to test, hides dependencies, allows silent `null`s.

### Real-time example: a code-review finding

```text
PR review comment:
 "ReportService has 11 @Autowired fields. It builds PDFs, sends emails,
 uploads to Blob Storage and updates audit tables."

Refactor:
 ReportService(ReportDataLoader, PdfRenderer, ReportPublisher) -> 3 constructor args
 ReportPublisher(EmailSender, BlobUploader, AuditRecorder) -> 3 constructor args
```

Switching to constructor injection exposed the problem immediately, which led to smaller, testable classes.

### Q14. How do you resolve ambiguity when multiple beans of the same type exist?

**Expected Answer:**

**Definition:** Bean ambiguity occurs when Spring finds more than one candidate for a required type and cannot determine which implementation should be injected.

**Concept explanation:** Make the selection rule explicit. Use `@Primary` for the normal default, `@Qualifier` for a specific dependency, `@Resource(name = ...)` for name-oriented lookup, or inject a collection when the application intentionally needs every implementation.

```text
NotificationService required
 -> EmailNotificationService
 -> SmsNotificationService
 -> two candidates
 -> @Primary or @Qualifier resolves the choice
```

If a qualifier name is misspelled or does not match a bean name, startup fails with a no-such-bean or no-qualifying-bean error. Treat qualifiers as part of the configuration contract and test the selected implementation.

```java
public interface NotificationService {
void send(String recipient, String message);
}

@Service
public class EmailNotificationService implements NotificationService { /* ... */ }

@Service
public class SmsNotificationService implements NotificationService { /* ... */ }
```

Now injecting `NotificationService` fails at startup:

```text
NoUniqueBeanDefinitionException: expected single matching bean but found 2:
emailNotificationService, smsNotificationService
```

**Solution 1: `@Primary` — mark the default**

```java
@Service
@Primary
public class EmailNotificationService implements NotificationService { /* ... */ }
```

**Solution 2: `@Qualifier` — choose explicitly**

```java
@Service
public class OtpService {

 private final NotificationService notificationService;

 public OtpService(@Qualifier("smsNotificationService") NotificationService notificationService) {
 this.notificationService = notificationService;
 }
}
```

**Solution 3: `@Resource(name = "...")` — look up by bean name**

```java
@Resource(name = "smsNotificationService")
private NotificationService notificationService;
```

**Solution 4: inject all implementations (List or Map)**

```java
public BroadcastService(List<NotificationService> services) {
 // receives both EmailNotificationService and SmsNotificationService
}
```

### Real-time example: strategy pattern with `Map<String, Bean>`

A customer chooses a preferred channel (`EMAIL`, `SMS`, `PUSH`) in their profile. Instead of a growing `if/else`, inject a map keyed by bean name:

```java
@Service("EMAIL") class EmailNotificationService implements NotificationService { /* ... */ }
@Service("SMS") class SmsNotificationService implements NotificationService { /* ... */ }
@Service("PUSH") class PushNotificationService implements NotificationService { /* ... */ }

@Service
public class CustomerNotifier {

 private final Map<String, NotificationService> channels;

 public CustomerNotifier(Map<String, NotificationService> channels) {
 this.channels = channels; // {EMAIL=..., SMS=..., PUSH=...}
 }

 public void notify(Customer customer, String message) {
NotificationService channel = channels.get(customer.preferredChannel());
 if (channel == null) {
 throw new IllegalStateException("Unsupported channel: " + customer.preferredChannel());
 }
 channel.send(customer.contact(), message);
 }
}
```

Adding WhatsApp later means adding one new `@Service("WHATSAPP")` class — `CustomerNotifier` does not change (Open/Closed Principle).

| Situation | Best choice |
| --- | --- |
| One obvious default, occasional override | `@Primary` + `@Qualifier` where needed |
| Each consumer needs a specific implementation | `@Qualifier` |
| Choose implementation at runtime from data | `Map<String, Bean>` strategy |
| Execute every implementation (e.g. validators) | `List<Bean>` with `@Order` |

### Q15. What are @Value and @ConfigurationProperties? When do you use each?

**Definition:** Both annotations bind external configuration to Java code. `@Value` injects one expression or value, while `@ConfigurationProperties` binds a related group of properties to a type-safe object.

**Concept explanation:** Use `@Value` for a small, isolated setting. Use `@ConfigurationProperties` for a configuration namespace with several fields, validation, duration or data-size conversion, and a clear contract that can be tested independently.

```text
payment.gateway.url -> one value -> @Value
payment.gateway.* -> configuration object -> @ConfigurationProperties
```

The grouped approach is usually easier to refactor because property names and Java fields stay together. Avoid scattering many `@Value` fields across services when they represent one coherent configuration object.

**`@Value` — inject individual properties:**

```java
@Service
public class PaymentService {

 @Value("${payment.gateway.url}")
 private String gatewayUrl;

 @Value("${payment.timeout:5000}") // default value if not set
 private int timeout;

 @Value("${payment.retry.enabled:false}")
 private boolean retryEnabled;

 @Value("#{${payment.fee.rates}}") // SpEL for maps
 private Map<String, Double> feeRates;
}
```

**`@ConfigurationProperties` — bind a group of related properties to one object:**

```java
@ConfigurationProperties(prefix = "payment.gateway")
@Validated
public record PaymentGatewayProperties(
 @NotBlankString url,
 @NotNullDuration timeout,
boolean retryEnabled,
Map<String, BigDecimal> feeRates) {
}
```

```yaml
payment:
 gateway:
 url: https://api.payment.com
 timeout: 10s
 retry-enabled: true
 fee-rates:
 credit: 2.5
 debit: 1.0
```

Register it with `@EnableConfigurationProperties(PaymentGatewayProperties.class)` or `@ConfigurationPropertiesScan`.

**When to use which:**

| Scenario | Use |
| --- | --- |
| One or two simple values | `@Value` |
| Group of related properties | `@ConfigurationProperties` |
| Need validation at startup (`@Validated`) | `@ConfigurationProperties` |
| `Duration`/`DataSize` conversion (`10s`, `5MB`) | `@ConfigurationProperties` |
| Type-safe, refactorable, IDE auto-complete | `@ConfigurationProperties` |
| SpEL expressions needed | `@Value` |

### Real-time example: a batch job's settings

A nightly email batch needs a chunk size, retry limit, sender address and a cut-off time. Scattering four `@Value` fields across three classes makes it easy to mistype a key; a typo such as `${batch.chunk-sise}` fails only at runtime.

```java
@ConfigurationProperties(prefix = "notification.batch")
@Validated
public record NotificationBatchProperties(
 @Min(1) @Max(1000) int chunkSize,
 @Min(0) @Max(5) int maxRetries,
 @Email @NotBlankString senderAddress,
 @NotNullLocalTime cutOffTime) {
}
```

```yaml
notification:
 batch:
 chunk-size: 200
 max-retries: 3
 sender-address: no-reply@example.com
 cut-off-time: "05:30"
```

```text
Ops accidentally sets chunk-size: 0 in PROD
 -> @Min(1) fails during startup
 -> pod never becomes ready, old pods keep serving
 -> no half-broken batch run at 2 a.m.
```

That "fail fast at startup" behaviour is the strongest argument for `@ConfigurationProperties` in production systems.

# Section 5: Web / REST Annotations

### Q16. Explain all the Spring MVC/REST annotations with examples.

**Expected Answer:**

**Definition:** Spring MVC annotations map HTTP requests to Java methods and describe how path variables, query parameters, headers, request bodies and return values should be handled.

**Concept explanation:** A request passes through the `DispatcherServlet`, which selects a handler method based on the HTTP method and path. Spring then binds request data to method parameters, validates it when requested, invokes the method and converts the return value into a view or response body.

```text
HTTP request
 -> DispatcherServlet
 -> @RequestMapping / @GetMapping match
 -> bind @PathVariable, @RequestParam and @RequestBody
 -> validate @Valid input
 -> invoke controller method
 -> serialize response or render view
```

The annotations below describe different parts of that flow: mapping annotations choose the handler, parameter annotations bind input, and response annotations control the status or body.

#### Request Mapping Annotations

```java
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

 @GetMapping // GET /api/v1/orders
 public List<OrderDTO> getAllOrders() { ... }

 @GetMapping("/{id}") // GET /api/v1/orders/123
 public OrderDTO getOrder(@PathVariableLong id) { ... }

 @GetMapping("/search") // GET /api/v1/orders/search?status=PENDING&page=0
 public Page<OrderDTO> search(
 @RequestParamOrderStatus status,
 @RequestParam(defaultValue = "0") int page) { ... }

 @PostMapping // POST /api/v1/orders
 @ResponseStatus(HttpStatus.CREATED)
 public OrderDTO createOrder(@Valid @RequestBodyCreateOrderRequest request) { ... }

 @PutMapping("/{id}") // PUT /api/v1/orders/123 (replace whole resource)
 public OrderDTO updateOrder(
 @PathVariableLong id,
 @Valid @RequestBodyUpdateOrderRequest request) { ... }

 @PatchMapping("/{id}/status") // PATCH /api/v1/orders/123/status (partial update)
 public OrderDTO updateStatus(
 @PathVariableLong id,
 @RequestBodyStatusUpdateRequest request) { ... }

 @DeleteMapping("/{id}") // DELETE /api/v1/orders/123
 @ResponseStatus(HttpStatus.NO_CONTENT)
 public void deleteOrder(@PathVariableLong id) { ... }
}
```

| HTTP method | Annotation | Meaning | Idempotent? | Typical success status |
| --- | --- | --- | --- | --- |
| GET | `@GetMapping` | Read | Yes | 200 |
| POST | `@PostMapping` | Create / trigger action | No | 201 or 202 |
| PUT | `@PutMapping` | Replace whole resource | Yes | 200 or 204 |
| PATCH | `@PatchMapping` | Partial update | Not guaranteed | 200 |
| DELETE | `@DeleteMapping` | Remove | Yes | 204 |

#### Parameter Annotations

| Annotation | Source | Example request | Java |
| --- | --- | --- | --- |
| `@PathVariable` | URL path segment | `/orders/42` | `@PathVariable Long id` |
| `@RequestParam` | Query string | `/orders?status=ACTIVE` | `@RequestParam String status` |
| `@RequestBody` | HTTP body (JSON) | POST body | `@RequestBody OrderRequest request` |
| `@RequestHeader` | HTTP header | `X-Correlation-Id: abc` | `@RequestHeader("X-Correlation-Id") String cid` |
| `@CookieValue` | HTTP cookie | `Cookie: sessionId=xyz` | `@CookieValue("sessionId") String sessionId` |
| `@ModelAttribute` | Form data / model | HTML form submit | `@ModelAttribute UserForm form` |
| `@MatrixVariable` | Matrix params | `/orders;status=ACTIVE` | `@MatrixVariable String status` |

**Rule of thumb:** use the path for *identity* (`/customers/42`), query parameters for *filtering, sorting and paging* (`?status=ACTIVE&page=2`), headers for *metadata* (auth, correlation ID, idempotency key) and the body for *data being created or changed*.

#### Real-time example: a policy-document download API

A customer portal lets a signed-in customer list and download their insurance documents.

```java
@RestController
@RequestMapping("/api/v1/customers/{customerId}/documents")
public class DocumentController {

 private final DocumentService documentService;

 public DocumentController(DocumentService documentService) {
 this.documentService = documentService;
 }

 // GET /api/v1/customers/42/documents?type=POLICY&page=0&size=20
 @GetMapping
 public Page<DocumentSummary> list(
 @PathVariablelong customerId,
 @RequestParam(required = false) DocumentType type,
 @PageableDefault(size = 20, sort = "issuedAt", direction = Sort.Direction.DESC) Pageable pageable,
 @RequestHeader("X-Correlation-Id") String correlationId) {
 return documentService.list(customerId, type, pageable);
 }

 // GET /api/v1/customers/42/documents/9001/content
 @GetMapping("/{documentId}/content")
 public ResponseEntity<Resource> download(@PathVariablelong customerId,
 @PathVariablelong documentId) {
StoredDocument doc = documentService.load(customerId, documentId);
 return ResponseEntity.ok()
 .contentType(MediaType.APPLICATION_PDF)
 .header(HttpHeaders.CONTENT_DISPOSITION,
 ContentDisposition.attachment().filename(doc.fileName()).build().toString())
 .body(doc.resource());
 }
}
```

Security note: `DocumentService.load` must verify that the authenticated user actually owns `customerId`. Trusting the path variable alone is an Insecure Direct Object Reference (IDOR) vulnerability — one of the most common real-world API bugs.

### Q17. What is the difference between @Controller and @RestController?

**Expected Answer:** The difference is what Spring does with the value returned by a controller method:

- `@Controller` is normally used for server-rendered web pages. A returned `String` is treated as a **view name**, such as `home`, and a view resolver finds the matching HTML template.
- `@RestController` is normally used for REST APIs. A returned object is treated as **response data** and is converted to JSON or XML for the HTTP response body.

The shortest way to remember it is:

```text
@Controller -> return a page to display
@RestController -> return data for a client or another service
```

## First understand the HTTP response

When a browser or API client sends a request, the controller must decide what to send back:

```text
HTTP request
 |
 v
Controller method
 |
 +-- View name such as "home"
 | -> ViewResolver finds home.html
 | -> server renders HTML
 |
 +-- Java object such as OrderResponse
 -> HttpMessageConverter serializes it
 -> client receives JSON or XML
```

The annotation changes the meaning of the method's return value. The Java method may look similar, but the response processing is different.

## 1. `@Controller`: return a view name

Use `@Controller` when the server is responsible for building an HTML page. This is common with server-side template engines such as Thymeleaf, FreeMarker or JSP.

```java
@Controller
public class WebController {

 @GetMapping("/home")
 public String home(Model model) {
 model.addAttribute("message", "Hello");
 return "home";
 }
}
For a request to `GET /home`, Spring interprets `"home"` as a view name, not as literal response text:

```text
GET /home
 -> WebController.home()
 -> returns "home"
 -> ViewResolver searches for the home template
 -> template receives message = "Hello"
 -> server returns rendered HTML
```

For example, with the usual Thymeleaf configuration, `"home"` may resolve to:

```text
src/main/resources/templates/home.html
```

The `Model` carries data from the controller to the template:

```html
<h1 th:text="${message}">Default message</h1>
```

The browser receives the final HTML page, not the Java `Model` object and not the string `"home"`.

## 2. `@Controller` with `@ResponseBody`: return data

An ordinary `@Controller` can also return JSON or other response data. Add `@ResponseBody` to the method:

```java
@Controller
public class WebController {

 @GetMapping("/api/data")
 @ResponseBody
 public DataDTO getData() {
 return new DataDTO("value");
 }
}
```

Now Spring does not interpret the return value as a view name. It writes the returned object directly into the HTTP response body. Jackson usually converts it to JSON:

```json
{
"value": "value"
}
```

The request flow is:

```text
GET /api/data
 -> WebController.getData()
 -> returns DataDTO
 -> @ResponseBody says: write this as response data
 -> Jackson serializes DataDTO to JSON
 -> client receives JSON
```

Without `@ResponseBody`, Spring may interpret a returned `String` as a view name. This is why adding the annotation matters.

## 3. `@RestController`: response body by default

`@RestController` is a convenience annotation. It is effectively:

```java
@Controller
@ResponseBody
```

Therefore, every handler method in a `@RestController` writes its return value to the HTTP response body by default:

```java
@RestController
public class ApiController {

 @GetMapping("/api/data")
 public DataDTO getData() {
 return new DataDTO("value");
 }
}
```

For `GET /api/data`, Spring serializes the `DataDTO` object and returns JSON. You do not need to repeat `@ResponseBody` on every method.

## The most important String example

The same return type can have different meanings:

```java
@Controller
public class PageController {

 @GetMapping("/home")
 public String home() {
 return "home"; // View name: resolve home.html
 }

 @GetMapping("/message")
 @ResponseBody
 public String message() {
 return "home"; // Response body: client receives the text home
 }
}
```

The difference is not the `String`. The difference is whether `@ResponseBody` is present:

| Method | Meaning of `return "home"` |
| --- | --- |
| `@Controller` without `@ResponseBody` | Find and render the `home` view |
| `@Controller` with `@ResponseBody` | Send the literal text `home` in the response body |
| `@RestController` | Send the literal text `home` in the response body |

## Comparison table

| Aspect | `@Controller` | `@RestController` |
| --- | --- | --- |
| Main purpose | Server-rendered HTML pages | REST APIs and data endpoints |
| Return value | Usually a view name | Usually an object or DTO |
| Default interpretation | View name | Response body data |
| HTML rendering | Yes, through a `ViewResolver` | No, not by default |
| JSON response | Add `@ResponseBody` to the method or class | Automatic for returned objects |
| Equivalent form | `@Controller` | `@Controller` + `@ResponseBody` |
| Common clients | Browser requesting pages | Browser JavaScript, mobile apps and other services |

## When should you use each one?

Use `@Controller` when:

- The server returns HTML pages.
- You use Thymeleaf, FreeMarker, JSP or another server-side template engine.
- The controller prepares a `Model` for a view.
- The browser should receive a rendered page.

Use `@RestController` when:

- The application exposes REST endpoints.
- The response is JSON, XML or another data format.
- A frontend application such as React, Angular or Vue consumes the API.
- A mobile application or another backend service consumes the endpoint.

## Can a `@RestController` return an HTML view?

It can return HTML text, but it does not normally resolve a returned string as a template view. For example:

```java
@RestController
public class ApiController {

 @GetMapping(value = "/html", produces = MediaType.TEXT_HTML_VALUE)
 public String html() {
 return "<h1>Hello</h1>";
 }
}
```

This sends the literal HTML text in the response body. It does not search for `html.html` through a view resolver. For server-side template rendering, use `@Controller`.

## A practical REST example

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

 private final OrderService orderService;

 public OrderController(OrderService orderService) {
 this.orderService = orderService;
 }

 @GetMapping("/{id}")
 public OrderResponse getOrder(@PathVariablelong id) {
 return orderService.findOrder(id);
 }
}
```

For `GET /api/orders/10`, the response might be:

```json
{
"id": 10,
"status": "CONFIRMED"
}
```

The controller does not manually build the JSON string. Spring's HTTP message converters, commonly Jackson, serialize the Java object to JSON.

## Easy interview summary

> `@Controller` is mainly used for server-rendered views. When a method returns `"home"`, Spring treats it as a view name and asks a `ViewResolver` to find and render the `home` template. If you add `@ResponseBody`, Spring writes the method's return value directly into the HTTP response body. `@RestController` combines `@Controller` and `@ResponseBody`, so all its methods return response data such as JSON by default. Use `@Controller` for HTML pages and `@RestController` for REST APIs.

### Q18. What is @Valid vs @Validated? How does validation work?

**Expected Answer:** Validation means checking incoming data against rules before the application uses it.

For example, when a client sends a request to create an order, the application should check that:

- The customer name is not blank.
- The email has a valid format.
- At least one order item was supplied.
- The quantity is within the allowed range.
- The total amount is positive.
- The delivery date is in the future.

Without validation, every service would need to write the same checks manually. With Bean Validation, you declare the rules on the request object and Spring runs them automatically.

## A simple analogy

Think of an API request as a form submitted at a reception desk:

```text
Client submits form
 |
 v
Validation desk checks the rules
 |
 +-- Valid form -> service processes the request
 |
 +-- Invalid form -> request is rejected with useful errors
```

The annotations such as `@NotBlank`, `@Email` and `@Positive` are the rules used by the validation desk.

## What do `@Valid` and `@Validated` mean?

Both annotations tell Spring to validate an object, but they have different purposes:

-**`@Valid`** is the standard Jakarta Bean Validation annotation. It performs normal validation and tells the validator to continue into nested objects.
-**`@Validated`** is a Spring annotation. It supports validation groups, which allow different rules to be active in different situations, such as create and update.

The simplest memory rule is:

```text
@Valid -> validate using the normal/default rules
@Validated -> validate using selected Spring validation groups
```

## Side-by-side comparison

| Aspect | `@Valid` | `@Validated` |
| --- | --- | --- |
| Origin | Jakarta Bean Validation standard; formerly JSR-303 | Spring Framework |
| Main purpose | Run the default validation rules | Run validation rules, optionally for selected groups |
| Validation groups | Does not select a group | Can select groups such as `OnCreate` or `OnUpdate` |
| Nested-object validation | Yes, when placed on the nested object or nested parameter | `@Validated` alone does not replace `@Valid` for nested objects |
| Common location | `@RequestBody` parameters, fields and nested objects | Method parameters or class-level method validation |
| Typical choice | Most ordinary request DTO validation | Create/update rules or method validation groups |

## 1. Define validation rules on a request object

The following class represents the JSON body for creating an order:

```java
public class CreateOrderRequest {

 @NotBlank(message = "Customer name is required")
 private String customerName;

 @NotNull(message = "Email is required")
 @Email(message = "Email must have a valid format")
 private String email;

 @NotEmpty(message = "At least one item is required")
 @Size(min = 1, max = 100, message = "Items must contain between 1 and 100 entries")
 private List<@ValidOrderItemRequest> items;

 @Positive(message = "Total amount must be positive")
 private BigDecimal totalAmount;

 @Future(message = "Delivery date must be in the future")
 private LocalDate deliveryDate;
}
```

The annotations mean:

| Annotation | Meaning |
| --- | --- |
| `@NotBlank` | The string must not be `null`, empty or only whitespace |
| `@NotNull` | A value must be present; an empty string is still different from `null` |
| `@Email` | The string must have a valid email format |
| `@NotEmpty` | A collection, map or string must contain at least one value |
| `@Size(min = 1, max = 100)` | The number of characters or elements must be within the range |
| `@Positive` | A number must be greater than zero |
| `@Future` | A date or time must be later than the current date or time |

### Important difference between `@NotNull`, `@NotEmpty` and `@NotBlank`

```text
@NotNull -> value must exist
@NotEmpty -> value must exist and contain something
@NotBlank -> text must contain non-whitespace characters
```

For example:

```text
null -> fails @NotNull, @NotEmpty and @NotBlank
"" -> passes @NotNull, fails @NotEmpty and @NotBlank
" " -> passes @NotNull, may pass @NotEmpty, fails @NotBlank
"hello" -> passes all three string checks
```

## 2. Validate nested objects with `@Valid`

An order contains a list of order items. Each item has its own validation rules:

```java
public class OrderItemRequest {

 @NotBlank(message = "Product ID is required")
 private String productId;

 @Min(value = 1, message = "Quantity must be at least 1")
 @Max(value = 10000, message = "Quantity must not exceed 10000")
 private int quantity;
}
```

The `@Valid` inside the list is important:

```java
private List<@Valid OrderItemRequest> items;
```

It means:

```text
Validate CreateOrderRequest
 -> validate customerName, email, items, totalAmount and deliveryDate
 -> enter every OrderItemRequest in items
 -> validate productId and quantity for every item
```

Without `@Valid`, Spring may validate the outer order fields but not automatically inspect every field inside each `OrderItemRequest`.

You can also place `@Valid` on a nested field:

```java
@Valid
private AddressRequest shippingAddress;
```

This tells the validator to continue into `AddressRequest` and apply its rules as well.

## 3. Use `@Valid` in a controller

Place `@Valid` next to `@RequestBody` when the incoming JSON should be checked:

```java
@RestController
@RequestMapping("/orders")
public class OrderController {

 @PostMapping
 public OrderDTO createOrder(
 @Valid @RequestBodyCreateOrderRequest request) {
 return orderService.create(request);
 }
}
```

The complete request flow is:

```text
1. Client sends JSON to POST /orders
2. Spring reads the JSON
3. Jackson converts JSON into CreateOrderRequest
4. @Valid activates Bean Validation
5. Every constraint is checked
6. If valid, the controller method runs
7. If invalid, the controller method does not run
8. Spring raises MethodArgumentNotValidException
9. The client normally receives HTTP 400 Bad Request
```

Example invalid request:

```json
{
"customerName": "",
"email": "not-an-email",
"items": [],
"totalAmount": -10
}
```

Possible validation failures:

```text
customerName -> Customer name is required
email -> Email must have a valid format
items -> At least one item is required
totalAmount -> Total amount must be positive
```

The service is not called because invalid data is rejected before business logic starts.

## What happens when validation fails?

For an invalid `@RequestBody`, Spring typically throws `MethodArgumentNotValidException`. Spring Boot can return a default error response, or a `@RestControllerAdvice` can convert the errors into your application's standard error format.

For example, a global handler can collect field errors:

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, String>> handleValidation(
MethodArgumentNotValidException exception) {

Map<String, String> errors = new HashMap<>();
 exception.getBindingResult().getFieldErrors().forEach(error ->
 errors.put(error.getField(), error.getDefaultMessage()));

 return ResponseEntity.badRequest().body(errors);
}
```

This is why the earlier validation example says that a failed `@Valid` request normally becomes HTTP 400.

## 4. Why do we need validation groups?

The same DTO may be used for both creating and updating a user, but the rules can be different:

```text
Create user -> ID must be absent
Update user -> ID must be present
Both -> name is required
```

If you use only ordinary constraints, one rule might incorrectly apply to both operations. Validation groups let you activate the correct rules for the current operation.

## 5. Define create and update groups

Groups are normally empty marker interfaces:

```java
public interface OnCreate {
}

public interface OnUpdate {
}
```

Apply the groups to the fields:

```java
public class UserRequest {

 @Null(
 groups = OnCreate.class,
 message = "ID must be empty when creating a user")
 @NotNull(
 groups = OnUpdate.class,
 message = "ID is required when updating a user")
 private Long id;

 @NotBlank(
 groups = {OnCreate.class, OnUpdate.class},
 message = "Name is required")
 private String name;
}
```

The meaning is:

| Field value | `OnCreate` | `OnUpdate` |
| --- | --- | --- |
| `id` | Must be `null` | Must not be `null` |
| `name` | Must not be blank | Must not be blank |

## 6. Select a group with `@Validated`

Use `@Validated(OnCreate.class)` for the create endpoint and `@Validated(OnUpdate.class)` for the update endpoint:

```java
@RestController
@RequestMapping("/users")
public class UserController {

 @PostMapping
 public UserDTO create(
 @Validated(OnCreate.class)
 @RequestBodyUserRequest request) {
 return userService.create(request);
 }

 @PutMapping("/{id}")
 public UserDTO update(
 @Validated(OnUpdate.class)
 @RequestBodyUserRequest request) {
 return userService.update(request);
 }
}
```

The requests are evaluated differently:

```text
POST /users
 -> use OnCreate rules
 -> id must be null
 -> name must not be blank

PUT /users/10
 -> use OnUpdate rules
 -> id must not be null
 -> name must not be blank
```

## Important group warning

When constraints are assigned explicitly to a group, they are not automatically active for every other group. Designate a default group when a rule should always run:

```java
@NotBlank(groups = {
 Default.class,
 OnCreate.class,
 OnUpdate.class
})
private String name;
```

In many applications, using separate create and update request classes is simpler and safer than putting too many groups into one DTO. Use validation groups when the shared request model is genuinely useful and the rules remain easy to understand.

## `@Validated` for method-level validation

`@Validated` can also be placed on a service class to enable Spring method validation when method-validation support is configured:

```java
@Service
@Validated
public class UserService {

 public User findById(@Positivelong id) {
 return userRepository.findById(id).orElseThrow();
 }
}
```

Here, `@Positive` validates the method argument rather than an HTTP request body. This is a different use of `@Validated`, but the idea is the same: Spring checks declared constraints before allowing invalid input into the method.

## Easy decision guide

```text
Normal request DTO validation -> @Valid
Nested DTO validation -> @Valid on the nested object
Create/update-specific rules -> @Validated(OnCreate.class)
Service method parameter validation -> @Validated on the class
```

## Easy interview summary

> Bean Validation checks incoming data against declared rules such as `@NotBlank`, `@Email`, `@Positive` and `@Future`. Put `@Valid` on an `@RequestBody` to validate the request and use `@Valid` on nested objects so their fields are checked too. `@Validated` is Spring's extension that supports validation groups, so create and update operations can apply different rules. A failed request is rejected before the controller or service processes it and normally produces HTTP 400. Use `@Valid` for ordinary validation and `@Validated` when you need groups or Spring method validation.

### Q19. How do you implement global exception handling in Spring Boot?

**Definition:** Global exception handling is a single, central place — a class annotated with `@RestControllerAdvice` — that converts exceptions thrown anywhere in controllers or services into consistent HTTP status codes and error bodies.

**Simple explanation:** Instead of writing `try/catch` in every controller, you let exceptions travel up and one "error desk" translates them into clean responses.

### First, understand the problem

**Without a global handler** — the client may receive a generic, leaky error:

```json
{
"timestamp": "2026-04-22T10:00:00",
"status": 500,
"error": "Internal Server Error",
"trace": "java.lang.NullPointerException at EmployeeService.java:45 ..."
}
```

Problems: wrong status (500 for a simple "not found"), stack trace leaks internal class names, and every endpoint returns a different shape.

**With a global handler** — you control the response:

```json
{
"code": "NOT_FOUND",
"message": "Employee not found with id: 5"
}
```

### Analogy: the reception desk

```text
Request enters the building (application)
 |
 v
Something goes wrong in some office (service/repository)
 |
 v
The problem is sent to the reception desk (@RestControllerAdvice)
 |
 v
Reception gives the visitor a calm, standard message
and files the detailed incident report internally (logs)
```

### Step 1 — Create a custom exception

```java
public class ResourceNotFoundException extends RuntimeException {

 public ResourceNotFoundException(String resource, Object id) {
 super(resource + " not found with id: " + id);
 }
}
```

Use it in the service:

```java
@Service
public class EmployeeService {

 private final EmployeeRepository employeeRepository;

 public EmployeeService(EmployeeRepository employeeRepository) {
 this.employeeRepository = employeeRepository;
 }

 public Employee getEmployee(Long id) {
 return employeeRepository.findById(id)
 .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
 }
}
```

### Step 2 — Create the error response shape

```java
public record ErrorResponse(
String code, // e.g. "NOT_FOUND"
String message, // e.g. "Employee not found with id: 5"
Map<String, String> details, // optional field errors
String traceId) { // lets support staff find the log entry

 public ErrorResponse(String code, String message, String traceId) {
 this(code, message, null, traceId);
 }
}
```

### Step 3 — Create the global handler

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

 @ExceptionHandler(ResourceNotFoundException.class)
 @ResponseStatus(HttpStatus.NOT_FOUND)
 public ErrorResponse handleNotFound(ResourceNotFoundException ex) {
 return new ErrorResponse("NOT_FOUND", ex.getMessage(), MDC.get("traceId"));
 }

 @ExceptionHandler(MethodArgumentNotValidException.class)
 @ResponseStatus(HttpStatus.BAD_REQUEST)
 public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
Map<String, String> errors = new LinkedHashMap<>();
 ex.getBindingResult().getFieldErrors()
 .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
 return new ErrorResponse("VALIDATION_FAILED", "Invalid request", errors, MDC.get("traceId"));
 }

 @ExceptionHandler(DataIntegrityViolationException.class)
 @ResponseStatus(HttpStatus.CONFLICT)
 public ErrorResponse handleConflict(DataIntegrityViolationException ex) {
 // e.g. duplicate email violates a unique constraint; never return the raw SQL message
 return new ErrorResponse("CONFLICT", "Resource already exists or violates a constraint", MDC.get("traceId"));
 }

 @ExceptionHandler(Exception.class)
 @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
 public ErrorResponse handleUnexpected(Exception ex) {
 log.error("Unhandled exception", ex); // full stack trace stays in the logs
 return new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred", MDC.get("traceId"));
 }
}
```

### Step 4 — The full flow

```text
GET /api/employees/999
 -> EmployeeController.getEmployee(999)
 -> EmployeeService throws ResourceNotFoundException("Employee", 999)
 -> exception travels up the call stack
 -> @RestControllerAdvice finds @ExceptionHandler(ResourceNotFoundException.class)
 -> HTTP 404 {"code":"NOT_FOUND","message":"Employee not found with id: 999","traceId":"7f3a..."}
```

### Step 5 — Validation error flow

```java
public record EmployeeRequest(
 @NotBlank(message = "Name must not be blank") String name,
 @Email(message = "Must be a valid email") String email,
 @Min(value = 18, message = "Age must be at least 18") int age) {
}

@PostMapping("/employees")
public Employee createEmployee(@Valid @RequestBodyEmployeeRequest request) {
 return employeeService.save(request); // not reached when validation fails
}
```

Request body `{ "name": "", "email": "notanemail", "age": 15 }` produces:

```json
HTTP 400
{
"code": "VALIDATION_FAILED",
"message": "Invalid request",
"details": {
"name": "Name must not be blank",
"email": "Must be a valid email",
"age": "Age must be at least 18"
 },
"traceId": "7f3a..."
}
```

### How Spring chooses the handler (common misconception)

Spring does **not** simply read handlers top to bottom. It picks the handler whose declared exception type is the **closest match** in the exception's class hierarchy.

```text
Thrown: ResourceNotFoundException (extends RuntimeException extends Exception)

Candidates:
 @ExceptionHandler(ResourceNotFoundException.class) -> distance 0 <- chosen
 @ExceptionHandler(Exception.class) -> distance 2
```

So the `Exception.class` handler acts as a safety net regardless of where it is written. When you have **several** `@RestControllerAdvice` classes, use `@Order` to decide which advice class is consulted first.

### Key annotations

| Annotation | What it does |
| --- | --- |
| `@RestControllerAdvice` | Global handler for all controllers; return values are written as the response body |
| `@ExceptionHandler(X.class)` | This method handles exceptions of type X (and subclasses) |
| `@ResponseStatus(HttpStatus.NOT_FOUND)` | Sets the HTTP status code |
| `ProblemDetail` (Spring 6+) | Built-in RFC 9457 error body: `type`, `title`, `status`, `detail`, `instance` |

### Modern alternative: `ProblemDetail`

```java
@ExceptionHandler(ResourceNotFoundException.class)
public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
 pd.setTitle("Resource not found");
 pd.setProperty("traceId", MDC.get("traceId"));
 return pd;
}
```

Enable framework-level problem details for built-in errors with `spring.mvc.problemdetails.enabled=true`.

### Real-time example: an insurance claims API error catalogue

| Situation | Exception | Status | Code returned to portal |
| --- | --- | --- | --- |
| Policy number does not exist | `PolicyNotFoundException` | 404 | `POLICY_NOT_FOUND` |
| Claim submitted after policy expired | `PolicyInactiveException` | 422 | `POLICY_INACTIVE` |
| Same claim submitted twice (same idempotency key) | `DuplicateClaimException` | 409 | `DUPLICATE_CLAIM` |
| Customer tries to read another customer's claim | `AccessDeniedException` | 403 | `FORBIDDEN` |
| Fraud-check service timed out | `DownstreamTimeoutException` | 503 | `TEMPORARILY_UNAVAILABLE` |
| Anything unexpected | `Exception` | 500 | `INTERNAL_ERROR` |

The front-end team maps each **code** to a translated message (English/Japanese). Because codes are stable, the back-end can reword `message` without breaking the UI, and support engineers search logs by `traceId` when a customer calls.

### Easy interview summary

> I use one `@RestControllerAdvice` that maps domain exceptions to HTTP statuses and a stable error body — today usually `ProblemDetail` with a trace ID. Specific handlers cover not-found, validation, conflict and downstream failures, and a final `Exception` handler logs the stack trace and returns a generic 500 without leaking internals. Spring picks the closest matching exception type, not the first method in the file.

# Section 6: Data Access Annotations

### Q20. Explain JPA/Spring Data annotations with examples.

**Expected Answer:**

**Definition:** JPA annotations map Java classes and fields to relational tables, columns and relationships. Spring Data repository annotations define how application code reads and writes those entities through generated queries or explicit query methods.

**Concept explanation:** An entity is a managed Java representation of a database row. Hibernate tracks it inside a persistence context. Mapping annotations describe identity, columns, relationships, auditing and concurrency behavior; repository annotations describe the data-access operation.

```text
Repository method
 -> Spring Data derives or executes a query
 -> JPA provider sends SQL
 -> rows become managed entities
 -> transaction tracks changes
 -> flush synchronizes changes with the database
```

### Entity lifecycle and relationship loading

```text
new object
 -> persist / save
 -> managed inside persistence context
 -> dirty checking detects changes
 -> flush sends INSERT or UPDATE
 -> commit completes the transaction
 -> detached after the context closes
```

For a relationship such as `@ManyToOne(fetch = FetchType.LAZY)`, Hibernate may create a proxy while loading the parent. The related row is fetched when the proxy is first used inside an active persistence context. Accessing it after the transaction closes can cause `LazyInitializationException` or trigger unwanted serialization queries.

### Cascade decision guide

| Requirement | Mapping decision |
| --- | --- |
| Child has no independent lifecycle and should be saved with parent | Consider selected cascade operations. |
| Child must be deleted when removed from the parent collection | Consider `orphanRemoval = true` after confirming ownership. |
| Related entity is shared by many parents | Avoid `CascadeType.REMOVE` and usually avoid `CascadeType.ALL`. |
| Association is only a read relationship | Use no cascade unless a write lifecycle is explicitly required. |

`CascadeType.ALL` is not a general convenience setting. It propagates every supported operation, including remove, so it should match the ownership and lifecycle rules of the domain.

#### Entity Annotations

```java
@Entity
@Table(name = "orders", indexes = {
 @Index(name = "idx_customer_id", columnList = "customer_id"),
 @Index(name = "idx_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class Order {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(name = "order_number", unique = true, nullable = false, length = 20)
 private String orderNumber;

 @Enumerated(EnumType.STRING)
 @Column(nullable = false)
 private OrderStatus status;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "customer_id", nullable = false)
 private Customer customer;

 @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
 private List<OrderItem> items = new ArrayList<>();

 @Embedded
 private Address shippingAddress;

 @CreatedDate
 @Column(updatable = false)
 private LocalDateTime createdAt;

 @LastModifiedDate
 private LocalDateTime updatedAt;

 @Version // optimistic locking
 private Long version;

 @Transient // not persisted
 private BigDecimal calculatedTax;
}
```

| Annotation | Purpose | Real-time tip |
| --- | --- | --- |
| `@Entity` | Marks class as a JPA entity (DB table) | Needs a no-arg constructor (can be `protected`) |
| `@Table` | Custom table name, indexes, constraints | Index the columns you filter on most (`customer_id`, `status`) |
| `@Id` | Primary key | Never expose sequential IDs publicly if guessing them is a risk |
| `@GeneratedValue` | Key strategy: `IDENTITY`, `SEQUENCE`, `UUID` | `SEQUENCE` allows JDBC batch inserts; `IDENTITY` disables them in Hibernate |
| `@Column` | Column mapping, constraints | Match `length` to the DB schema to catch bad data early |
| `@Enumerated` | Enum persistence (`STRING` or `ORDINAL`) | Always use `STRING`; `ORDINAL` breaks when someone reorders enum constants |
| `@ManyToOne` / `@OneToMany` | Relationships | Make `@ManyToOne` `LAZY` explicitly |
| `@JoinColumn` | Foreign key column | |
| `@Embedded` / `@Embeddable` | Value objects inside the entity | `Address`, `Money`, `Period` |
| `@CreatedDate` / `@LastModifiedDate` | Auditing timestamps | Requires `@EnableJpaAuditing` |
| `@Version` | Optimistic locking | Prevents lost updates when two users edit the same record |
| `@Transient` | Field not persisted | Calculated values |

**Real-time example — why `EnumType.ORDINAL` is dangerous:**

```text
Version 1: enum OrderStatus { PENDING, SHIPPED, DELIVERED } -> SHIPPED stored as 1
Version 2: enum OrderStatus { PENDING, PAID, SHIPPED, DELIVERED } -> 1 now means PAID
Result: every old SHIPPED order suddenly reads as PAID
```

**Real-time example — `@Version` preventing a lost update:**

```text
10:00:00 Agent A opens claim #77 (version 3) and changes the amount
10:00:05 Agent B opens claim #77 (version 3) and changes the address
10:00:10 Agent A saves -> UPDATE ... WHERE id=77 AND version=3 -> success, version=4
10:00:12 Agent B saves -> UPDATE ... WHERE id=77 AND version=3 -> 0 rows
 -> ObjectOptimisticLockingFailureException -> API returns 409
 -> UI shows "This claim was changed by someone else. Reload?"
```

Without `@Version`, Agent B would silently overwrite Agent A's amount change.

#### Repository Annotations

```java
public interface OrderRepository extends JpaRepository<Order, Long> {

 // Derived query: Spring Data generates SQL from the method name
List<Order> findByStatusAndCustomerId(OrderStatus status, Long customerId);

Optional<Order> findByOrderNumber(String orderNumber);

 @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt > :since")
List<Order> findRecentByStatus(@Param("status") OrderStatus status,
 @Param("since") LocalDateTime since);

 @Query(value = "SELECT * FROM orders WHERE total > :amount", nativeQuery = true)
List<Order> findExpensiveOrders(@Param("amount") BigDecimal amount);

 @Modifying
 @Transactional
 @Query("UPDATE Order o SET o.status = :status WHERE o.id = :id")
int updateStatus(@Param("id") Long id, @Param("status") OrderStatus status);

boolean existsByOrderNumber(String orderNumber);

long countByStatus(OrderStatus status);
}
```

| Method style | Use when | Watch out for |
| --- | --- | --- |
| Derived (`findByStatusAndCustomerId`) | Simple filters | Very long names become unreadable — switch to `@Query` |
| JPQL `@Query` | Joins, fetch joins, projections | Uses entity/field names, not table/column names |
| Native query | DB-specific features, tuned SQL | Not portable; always bind parameters, never concatenate strings (SQL injection) |
| `@Modifying` bulk update | Update many rows at once | Bypasses entity lifecycle, `@Version` and the persistence context; add `clearAutomatically = true` if you read the entities afterwards |

**Real-time example — paging a large table safely:**

```java
Page<Order> findByStatus(OrderStatus status, Pageable pageable);

// Controller: GET /orders?status=PENDING&page=3&size=50&sort=createdAt,desc
```

Returning `List<Order> findAll()` from a 5-million-row table is a common cause of `OutOfMemoryError` in production; paging keeps memory bounded. For batch jobs that iterate everything, prefer keyset pagination (`WHERE id > :lastId ORDER BY id LIMIT 500`) or a `Stream<Order>` inside a read-only transaction.

### Q21. What is the difference between FetchType.LAZY and FetchType.EAGER? What is the N+1 problem?

**Expected Answer:** This question has two parts:

1.**Fetch type** describes when related data is loaded from the database.
2.**N+1** describes an inefficient query pattern in which one query loads the parent records and additional queries load related records one by one.

## First understand the relationship

Suppose an `Order` belongs to one `Customer`:

```java
@Entity
public class Order {

 @ManyToOne(fetch = FetchType.LAZY)
 private Customer customer;
}
```

The `Order` row and the `Customer` row are separate database records. When Hibernate loads an `Order`, it must decide whether to load the related `Customer` immediately or wait until the application asks for it.

```text
orders table customers table
------------- ----------------
id | customer_id id | name
10 | 5 5 | Alice
```

The `customer` field is the relationship between the two tables.

## What does fetching mean?

**Fetching** means loading an entity or its related entities from the database into the persistence context.

```text
Database row -> Hibernate entity object -> Java code uses the object
```

The fetch strategy controls the timing of that loading:

```text
LAZY -> load the relationship only when the code uses it
EAGER -> load the relationship as part of loading the parent entity
```

## 1. `FetchType.LAZY`: load when needed

With lazy loading, Hibernate loads the main entity first and postpones loading the relationship:

```java
@ManyToOne(fetch = FetchType.LAZY)
private Customer customer;
The sequence is usually:

```text
Order order = orderRepository.findById(10).orElseThrow();
 -> SQL loads the order row
 -> customer is not fully loaded yet

order.getCustomer().getName();
 -> Hibernate notices that customer is needed
 -> SQL loads the customer row now
```

Hibernate often represents the not-yet-loaded relationship with a proxy. The proxy looks like the related entity, but it acts as a placeholder until a method or property requires the real data.

### Simple LAZY example

```java
List<Order> orders = orderRepository.findAll();
// Orders are loaded. Customers may still be unloaded.

Order firstOrder = orders.get(0);
String customerName = firstOrder.getCustomer().getName();
// The customer may be loaded here, when the relationship is accessed.
```

### Advantages of LAZY loading

- Avoids loading data that the use case does not need.
- Reduces the initial result size.
- Helps prevent large object graphs from being loaded accidentally.
- Is usually a safer default for collections such as `@OneToMany` and `@ManyToMany`.

### LAZY loading pitfall: session closed

Lazy data must be loaded while the persistence context or Hibernate session is available. If the relationship is accessed after that context is closed, Hibernate cannot fetch the data:

```text
Service transaction ends
 -> entity is returned
 -> controller or serializer accesses a lazy relationship
 -> Hibernate session is closed
 -> LazyInitializationException may occur
```

Do not solve this blindly by making every relationship `EAGER`. Instead, load the exact data required by the use case inside the transaction using a fetch join, entity graph or DTO projection.

## 2. `FetchType.EAGER`: load immediately

With eager loading, Hibernate is instructed to load the relationship when the parent entity is loaded:

```java
@ManyToOne(fetch = FetchType.EAGER)
private Customer customer;
```

Conceptually:

```text
Order order = orderRepository.findById(10).orElseThrow();
 -> load the order
 -> load the customer immediately
```

Eager does not guarantee that Hibernate always uses one SQL `JOIN`. Depending on the query, provider and mapping, Hibernate may use a join or issue an additional select. The important meaning is that the relationship is expected to be available immediately.

### EAGER loading pitfall

EAGER relationships can load data even when a particular use case does not need it. A query for 100 orders may also load 100 customers, customer addresses and other eager relationships. This can cause:

- More database work.
- Larger result sets.
- More memory usage.
- Slower response times.
- Unexpected query chains when entities have several eager relationships.

For this reason, senior-level applications commonly prefer lazy relationships and define the fetch plan per query.

## Default fetch types

JPA defines these defaults:

| Relationship | JPA default |
| --- | --- |
| `@OneToMany` | `LAZY` |
| `@ManyToMany` | `LAZY` |
| `@ManyToOne` | `EAGER` |
| `@OneToOne` | `EAGER` |

The defaults are not always the best choice for every application. Many teams explicitly specify `fetch = FetchType.LAZY`, especially for `@ManyToOne` and `@OneToOne`, and then fetch related data deliberately when a use case requires it.

## LAZY versus EAGER comparison

| Aspect | `LAZY` | `EAGER` |
| --- | --- | --- |
| Loading time | When the relationship is accessed | During or as part of loading the parent |
| Initial query cost | Usually lower | Usually higher |
| Unused relationship data | Not loaded | Loaded even if unused |
| Risk | `LazyInitializationException` outside an active session | Accidental large object graphs and extra queries |
| Best use | Load data deliberately per use case | Small relationships that are always required, used carefully |
| Common recommendation | Preferred for most associations | Avoid as a blanket solution |

## What is the N+1 problem?

The **N+1 problem** happens when the application executes:

```text
1 query to load N parent records
N additional queries to load a related record for each parent
--------------------------------------------------------------
Total = N + 1 queries
```

The problem is not that the result is incorrect. The problem is that the application makes many database round trips when one carefully designed query could retrieve the required data more efficiently.

## Step-by-step N+1 example

Assume the database contains five orders and each order has a customer.

```java
List<Order> orders = orderRepository.findAll();
// Query 1: SELECT * FROM orders

for (Order order : orders) {
 System.out.println(order.getCustomer().getName());
 // Query 2: SELECT * FROM customers WHERE id = 1
 // Query 3: SELECT * FROM customers WHERE id = 2
 // Query 4: SELECT * FROM customers WHERE id = 3
 // Query 5: SELECT * FROM customers WHERE id = 4
 // Query 6: SELECT * FROM customers WHERE id = 5
}
```

There are five parent records, so:

```text
1 query for orders + 5 queries for customers = 6 queries
1 + N = N + 1
```

With 10,000 orders, the same code could issue 10,001 queries. This can make an endpoint slow and can overload the database.

## How to recognize N+1

Enable SQL logging or use an observability tool and look for a pattern like:

```text
SELECT ... FROM orders;
SELECT ... FROM customers WHERE id = ?;
SELECT ... FROM customers WHERE id = ?;
SELECT ... FROM customers WHERE id = ?;
SELECT ... FROM customers WHERE id = ?;
```

The repeated query with different IDs is a strong sign that a relationship is being loaded inside a loop.

## Solution 1: JPQL `JOIN FETCH`

Use a fetch join when a particular query always needs the related customer:

```java
@Query("""
 SELECT o
 FROM Order o
 JOIN FETCH o.customer
 WHERE o.status = :status
 """)
List<Order> findByStatusWithCustomer(OrderStatus status);
```

Conceptually, the database can retrieve the orders and customers together:

```text
One query loads the required order and customer data
 -> no customer query is triggered inside the loop
```

Use `JOIN FETCH` for relationships that must exist. Use `LEFT JOIN FETCH` when the relationship is optional and the parent should still be returned when no related row exists.

## Solution 2: `@EntityGraph`

An entity graph declares the relationships to load without writing a JPQL fetch join:

```java
@EntityGraph(attributePaths = {"customer", "items"})
List<Order> findByStatus(OrderStatus status);
```

This is useful when the repository method name is already clear and you want to define a reusable fetch plan near the method.

## Solution 3: batch fetching

Batch fetching does not necessarily reduce the work to one query, but it groups many individual lookups into fewer queries:

```java
@BatchSize(size = 25)
@OneToMany(mappedBy = "order")
private List<OrderItem> items;
```

Instead of loading one relationship at a time, Hibernate may load related records in batches, for example with an `IN` clause:

```sql
SELECT * FROM order_items WHERE order_id IN (?, ?, ?, ...);
```

This is useful when join fetching is not appropriate, but the exact query count and behaviour should be verified with SQL logs.

## Solution 4: DTO projection

If an endpoint needs only a few fields, select those fields directly into a DTO instead of loading a complete entity graph:

```java
@Query("""
 SELECT new com.example.api.OrderSummary(o.id, c.name, o.status)
 FROM Order o
 JOIN o.customer c
 WHERE o.status = :status
 """)
List<OrderSummary> findSummaries(OrderStatus status);
```

DTO projections are often a good choice for read-only API responses because they fetch exactly the data needed by the client.

## Important production considerations

- Do not change every association to `EAGER` just to hide a `LazyInitializationException`.
- Do not assume `EAGER` always means one join query.
- Use a transaction boundary that covers the data needed by the use case.
- Avoid serializing JPA entities directly when it can expose internal fields or trigger lazy loading during JSON serialization.
- Prefer DTOs for API responses when the response shape is different from the entity model.
- Be careful with collection fetch joins and pagination because joining a parent to many child rows can duplicate parent rows and affect paging.
- Measure with SQL logs, query counters, database metrics and endpoint timings.

## Easy decision guide

```text
Relationship not needed for this use case -> keep it LAZY
Relationship always needed in one query -> JOIN FETCH or @EntityGraph
Many similar relationship lookups -> batch fetching
Only a few response fields are needed -> DTO projection
```

## Easy interview summary

> Fetch type controls when Hibernate loads related entities. `LAZY` waits until the relationship is accessed, while `EAGER` loads it with the parent entity. Lazy loading is usually preferred because it avoids unnecessary data, but the relationship must be accessed inside an active persistence context. The N+1 problem occurs when one query loads N parent rows and then a separate query loads related data for each parent, producing N+1 database queries. Solve it per use case with `JOIN FETCH`, `@EntityGraph`, batch fetching or DTO projections, and verify the result with SQL logs and performance metrics.

# Section 7: Transaction Annotations

### Q22. How does @Transactional work internally?

**Definition:** `@Transactional` declares that a method should execute within a database transaction. A transaction groups related database operations into one unit of work so the application can commit them together or roll them back according to the configured rules.

**Concept explanation:** Spring usually implements `@Transactional` with an AOP proxy. The caller invokes the proxy, not the target method directly. The proxy asks a `PlatformTransactionManager` to start or join a transaction, invokes the target method, and then commits or rolls back based on the result.

```text
Caller
 -> Spring proxy
 -> TransactionInterceptor
 -> start or join transaction
 -> actual service method
 -> commit on success or rollback on configured failure
```

The simplified flow is:

1. The proxy intercepts the external method call.
2. `PlatformTransactionManager.getTransaction()` starts a new transaction or joins an existing one.
3. The actual method executes.
4. A normal return normally leads to `commit()`.
5. An unchecked `RuntimeException` or `Error` normally leads to rollback.
6. A checked exception normally does not cause rollback unless configured with `rollbackFor`.
7. The transaction resources are cleaned up and returned to the pool.

### Example with common attributes

```java
@Transactional(
 propagation = Propagation.REQUIRED,
 isolation = Isolation.READ_COMMITTED,
 timeout = 30,
 readOnly = true,
 rollbackFor = BusinessException.class,
 noRollbackFor = EmailException.class)
public Order processOrder(OrderRequest request) {
 // Database work executes within the transaction.
 return orderRepository.save(buildOrder(request));
}
```

| Attribute | Meaning |
| --- | --- |
| `propagation` | Defines how this method behaves when a transaction already exists. |
| `isolation` | Defines which concurrent changes this transaction can observe. |
| `timeout` | Limits how long the transaction may run. |
| `readOnly` | Communicates that the operation is intended to be read-only; it is an optimization hint, not a security barrier that prevents writes. |
| `rollbackFor` | Adds exception types that should trigger rollback, including checked exceptions. |
| `noRollbackFor` | Excludes selected exception types from the normal rollback rule. |

### Propagation types

| Type | Behavior | Typical use |
| --- | --- | --- |
| `REQUIRED` (default) | Join an existing transaction or create one. | Most service methods. |
| `REQUIRES_NEW` | Suspend the current transaction and always create a new one. | Independent audit or notification record that should commit separately. |
| `MANDATORY` | Require an existing transaction and fail if none exists. | A lower-level operation that must never run outside a business transaction. |
| `SUPPORTS` | Use a transaction if one exists; otherwise run without one. | Read operations that can work in either context. |
| `NOT_SUPPORTED` | Suspend an existing transaction and run without one. | Work that should not hold database locks. |
| `NEVER` | Fail if a transaction already exists. | Operations that must be non-transactional. |
| `NESTED` | Use a nested savepoint when the transaction manager supports it. | Partial rollback within a larger transaction. |

### The self-invocation trap

The proxy can intercept an external call, but a call through `this` stays inside the target object and bypasses the proxy:

```java
@Service
public class OrderService {

 @Transactional
 public void processOrder() {
 // This method is transactional when called through the Spring proxy.
 }

 public void callProcess() {
 this.processOrder();
 // The proxy is bypassed. The transaction advice does not run here.
 }
}
```

Prefer calling the transactional method from another Spring bean, or move the operation into a separate service. Private methods and final methods also cannot be advised reliably by the usual proxy-based model because Spring cannot override or intercept them in the same way.

### Common transaction mistakes

- Expecting rollback for a checked exception without `rollbackFor`.
- Calling a transactional method through `this` and assuming the proxy is involved.
- Returning a lazy entity and accessing it after the transaction has closed.
- Holding a transaction open while calling a slow remote service.
- Assuming `readOnly = true` prevents every write; enforce write rules through service design and database permissions.
- Applying `@Transactional` to a controller instead of defining the business transaction at the service boundary.

### Real-time example 1: bank transfer (why atomicity matters)

```java
@Service
@RequiredArgsConstructor
public class TransferService {

 private final AccountRepository accounts;

 @Transactional
 public void transfer(long fromId, long toId, BigDecimal amount) {
Account from = accounts.findByIdForUpdate(fromId); // SELECT ... FOR UPDATE
Account to = accounts.findByIdForUpdate(toId);

 from.debit(amount); // throws InsufficientFundsException (RuntimeException)
 to.credit(amount);
 // No explicit save: dirty checking flushes both UPDATEs at commit
 }
}
```

```text
Success path: debit A, credit B -> COMMIT -> both balances change
Failure path: debit A, exception at credit B -> ROLLBACK -> neither balance changes
Without @Transactional: A is debited, B is never credited -> money disappears
```

### Real-time example 2: `REQUIRES_NEW` for an audit record that must survive failure

```java
@Service
class AuditService {

 @Transactional(propagation = Propagation.REQUIRES_NEW)
 public void record(String action, String outcome) {
 auditRepository.save(new AuditEntry(action, outcome, Instant.now()));
 }
}

@Service
@RequiredArgsConstructor
class PolicyCancellationService {

 private final AuditService auditService;

 @Transactional
 public void cancel(String policyNo) {
 try {
 doCancel(policyNo); // may throw
 auditService.record("CANCEL " + policyNo, "SUCCESS");
 } catch (RuntimeException ex) {
 auditService.record("CANCEL " + policyNo, "FAILED: " + ex.getMessage());
 throw ex; // outer transaction rolls back
 }
 }
}
```

```text
Outer transaction (cancel) -> ROLLBACK (policy stays active)
Inner transaction (audit) -> COMMITTED independently
Compliance can still see: "CANCEL P-123 FAILED: payment reversal timeout"
```

Caution: each `REQUIRES_NEW` borrows a **second** database connection while the outer one is held. Under heavy load this can exhaust the pool, so keep inner transactions short.

### Real-time example 3: never call a slow remote API inside a transaction

```text
BAD GOOD
@Transactional @Transactional save order (PENDING) + outbox row
 save order commit -> connection released
 call payment API (3 s, sometimes 30 s) publisher reads outbox -> calls payment API
 update order @Transactional update order (PAID)

200 concurrent checkouts x 30 s DB connections held only milliseconds
= pool of 20 exhausted -> whole site hangs
```

# Section 8: AOP (Aspect-Oriented Programming) Annotations

### Q23. What is AOP in Spring and what annotations does it use?

**Definition:** Aspect-oriented programming (AOP) separates cross-cutting concerns such as logging, security, transactions and metrics from the business methods they affect.

**Concept explanation:** Instead of adding the same logging or timing code to many methods, an aspect defines the behavior once and a pointcut selects the methods where it should run. Spring usually applies the aspect through a proxy, so external calls pass through the advice before reaching the target method.

| Term | Meaning |
| --- | --- |
| Aspect | The module containing a cross-cutting concern. |
| Advice | The action to run and its timing. |
| Pointcut | The rule that selects methods or annotations. |
| Join point | An intercepted execution point; in Spring proxy AOP this is commonly a method execution. |
| Proxy | The object that intercepts the call and invokes advice around the target. |

Advice timing:

```text
@Before -> before the target method
@Around -> before and after; may decide whether to proceed
target method
@AfterReturning -> after a successful return
@AfterThrowing -> after an exception
@After -> finally-style cleanup after success or failure
```

Example:

```java
@Aspect
@Component
public class LoggingAspect {

 @Before("execution(* com.example.service.*.*(..))")
 public void logBefore(JoinPoint joinPoint) {
 log.info("Entering {}", joinPoint.getSignature().toShortString());
 }

 @AfterReturning(
 pointcut = "execution(* com.example.service.*.*(..))",
 returning = "result")
 public void logAfterReturning(JoinPoint joinPoint, Object result) {
 log.info("Completed {} with result {}",
 joinPoint.getSignature().toShortString(), result);
 }

 @AfterThrowing(
 pointcut = "execution(* com.example.service.*.*(..))",
 throwing = "exception")
 public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
 log.error("Failed {}", joinPoint.getSignature().toShortString(), exception);
 }

 @Around("@annotation(com.example.annotation.Timed)")
 public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
long start = System.currentTimeMillis();
 try {
 return joinPoint.proceed();
 } finally {
long duration = System.currentTimeMillis() - start;
 log.info("{} took {} ms",
 joinPoint.getSignature().toShortString(), duration);
 }
 }
}
```

An `@Around` advice is the most powerful because it can modify arguments, skip the target method, change the return value or translate exceptions. It must call `joinPoint.proceed()` when the target method should execute.

Custom annotation and AOP example:

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
int maxRequests() default 100;
int windowSeconds() default 60;
}

@Aspect
@Component
public class RateLimitAspect {

 @Around("@annotation(rateLimit)")
 public Object enforce(
ProceedingJoinPoint joinPoint,
RateLimit rateLimit) throws Throwable {

String key = joinPoint.getSignature().toShortString();
 if (rateLimiter.isRateLimited(
 key, rateLimit.maxRequests(), rateLimit.windowSeconds())) {
 throw new RateLimitExceededException("Too many requests");
 }
 return joinPoint.proceed();
 }
}
```

Usage:

```java
@RateLimit(maxRequests = 50, windowSeconds = 60)
@GetMapping("/search")
public List<SearchResult> search(@RequestParamString query) {
 return searchService.search(query);
}
```

Like `@Transactional`, proxy-based AOP generally does not apply to self-invocation or methods that cannot be proxied. Keep advice boundaries public and call them through a Spring-managed bean.

### Real-time example: masking personal data in audit logs

A regulated insurer must log every call to customer-data services for audit, but must never write full phone numbers or policy numbers to the log.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {
String action();
}

@Aspect
@Component
@Slf4j
public class AuditAspect {

 @Around("@annotation(audited)")
 public Object audit(ProceedingJoinPoint pjp, Audited audited) throws Throwable {
String user = SecurityContextHolder.getContext().getAuthentication().getName();
String args = Arrays.stream(pjp.getArgs()).map(AuditAspect::mask).collect(Collectors.joining(","));
long start = System.nanoTime();
 try {
Object result = pjp.proceed();
 log.info("AUDIT action={} user={} args=[{}] outcome=SUCCESS ms={}",
 audited.action(), user, args, (System.nanoTime() - start) / 1_000_000);
 return result;
 } catch (Throwable ex) {
 log.warn("AUDIT action={} user={} args=[{}] outcome=FAILED error={}",
 audited.action(), user, args, ex.getClass().getSimpleName());
 throw ex;
 }
 }

 private static String mask(Object arg) {
String s = String.valueOf(arg);
 return s.length() <= 4 ? "****" : "****" + s.substring(s.length() - 4);
 }
}

@Service
class CustomerProfileService {

 @Audited(action = "VIEW_PROFILE")
 public CustomerProfile view(String policyNumber) { /* ... */ }
}
```

```text
Log line: AUDIT action=VIEW_PROFILE user=agent042 args=[****7781] outcome=SUCCESS ms=38
```

The business method contains **zero** audit code. Adding auditing to 40 more methods means adding one annotation to each.

# Section 9: Scheduling & Async Annotations

### Q24. How do @Scheduled and @Async work?

**Definition:** `@Scheduled` runs a Spring bean method according to a time rule. `@Async` submits a method invocation to an executor so the caller can continue without waiting for that work to finish.

## `@Scheduled`: run work on a schedule

```java
@Configuration
@EnableScheduling
public class SchedulingConfig {
}

@Service
public class ReportService {

 @Scheduled(fixedRate = 60_000)
 public void generateReport() {
 // Runs approximately every 60 seconds.
 }

 @Scheduled(fixedDelay = 30_000)
 public void cleanupTempFiles() {
 // Waits 30 seconds after the previous invocation completes.
 }

 @Scheduled(cron = "0 0 2 * * *")
 public void nightlyBatch() {
 // Runs every day at 02:00 according to the scheduler time zone.
 }
}
```

| Attribute | Behavior |
| --- | --- |
| `fixedRate` | Starts executions at a regular interval measured from the scheduled start time. |
| `fixedDelay` | Waits for the previous execution to finish, then waits the configured delay. |
| `cron` | Uses a cron expression for calendar-based schedules. |
| `initialDelay` | Delays the first execution. |

By default, scheduling must be designed carefully because a long-running task can overlap or delay other work depending on the scheduler configuration. Use an explicit scheduler, distributed lock or job coordination mechanism when multiple application instances must not process the same job simultaneously.

### `fixedRate` vs `fixedDelay` on a timeline

```text
Task takes 40 s; interval = 60 s

fixedRate = 60s: start 00:00, 01:00, 02:00 ... (start-to-start)
fixedDelay = 60s: start 00:00, 01:40, 03:20 ... (end-to-start)

Task takes 90 s with fixedRate = 60s on the default single scheduler thread:
 executions queue up and run back-to-back -> the job never "rests"
```

### Real-time example: nightly email batch on 3 pods with ShedLock

```java
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "30m")
class SchedulerConfig {

 @Bean
LockProvider lockProvider(DataSource dataSource) {
 return new JdbcTemplateLockProvider(JdbcTemplateLockProvider.Configuration.builder()
 .withJdbcTemplate(new JdbcTemplate(dataSource))
 .usingDbTime()
 .build());
 }
}

@Component
@RequiredArgsConstructor
class PendingEmailJob {

 private final EmailBatchService emailBatchService;

 @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Tokyo")
 @SchedulerLock(name = "pendingEmailJob", lockAtLeastFor = "1m", lockAtMostFor = "30m")
 public void run() {
 emailBatchService.sendPending();
 }
}
```

```text
02:00:00 pod-1 acquires lock "pendingEmailJob" -> runs
02:00:00 pod-2 tries lock -> already held -> skips
02:00:00 pod-3 tries lock -> already held -> skips
If pod-1 crashes, lockAtMostFor=30m releases the lock so the next run is not blocked forever
```

Without the lock every customer receives the same email **three times**.

## `@Async`: execute work on another thread

```java
@Configuration
@EnableAsync
public class AsyncConfig {

 @Bean("taskExecutor")
 public Executor taskExecutor() {
ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
 executor.setCorePoolSize(5);
 executor.setMaxPoolSize(20);
 executor.setQueueCapacity(100);
 executor.setThreadNamePrefix("async-");
 executor.setRejectedExecutionHandler(
 new ThreadPoolExecutor.CallerRunsPolicy());
 executor.initialize();
 return executor;
 }
}

@Service
public class NotificationService {

 @Async("taskExecutor")
 public CompletableFuture<String> sendEmail(String to, String body) {
 emailClient.send(to, body);
 return CompletableFuture.completedFuture("sent");
 }

 @Async
 public void sendSms(String number, String message) {
 smsClient.send(number, message);
 }
}
```

Use `CompletableFuture` when the caller needs completion or failure information. A `void` async method has no return value for the caller, so configure an `AsyncUncaughtExceptionHandler` for failures. `@Async` uses a proxy, so self-invocation does not switch threads. Also remember that thread-local request context, security context and transactions do not automatically become a safe substitute for explicit async context propagation.

### Real-time example: parallel calls to build a customer dashboard

The portal home page needs policies (300 ms), claims (400 ms) and payments (250 ms) from three services. Called one after another the page takes ~950 ms; in parallel it takes ~400 ms.

```java
@Service
@RequiredArgsConstructor
class DashboardService {

 private final PolicyClient policyClient; // each method is @Async and returns CompletableFuture
 private final ClaimClient claimClient;
 private final PaymentClient paymentClient;

 public Dashboard load(String customerId) {
CompletableFuture<List<Policy>> policies = policyClient.findByCustomer(customerId);
CompletableFuture<List<Claim>> claims = claimClient.findByCustomer(customerId);
CompletableFuture<List<Payment>> payments = paymentClient.findByCustomer(customerId);

 CompletableFuture.allOf(policies, claims, payments)
 .orTimeout(2, TimeUnit.SECONDS)
 .join();

 return new Dashboard(policies.join(), claims.join(), payments.join());
 }
}
```

```text
Sequential: 300 + 400 + 250 = 950 ms
Parallel: max(300, 400, 250) = ~400 ms
```

Production checklist for this pattern: bounded executor (not unlimited threads), a timeout on the combined future, a fallback when one section fails (show "Payments temporarily unavailable" instead of failing the whole page), and propagation of the trace ID/MDC into async threads with a `TaskDecorator`.

# Section 10: Spring Boot Actuator

### Q25. What is Spring Boot Actuator and what endpoints does it provide?

**Definition:** Spring Boot Actuator exposes operational endpoints and Micrometer integrations for health, metrics, environment diagnostics, mappings, logs and other runtime information.

Add the dependency:

```xml
<dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Common endpoints include:

| Endpoint | Purpose | Exposure caution |
| --- | --- | --- |
| `/actuator/health` | Application and dependency health | Usually exposed to a load balancer or platform probe. |
| `/actuator/info` | Build and application information | Keep details non-sensitive. |
| `/actuator/metrics` | JVM, HTTP and custom metrics | Protect from untrusted users. |
| `/actuator/env` | Environment properties and sources | Highly sensitive; restrict strongly or disable. |
| `/actuator/beans` | Registered beans | Internal diagnostic information. |
| `/actuator/mappings` | Request mappings | Internal application structure. |
| `/actuator/loggers` | View or change log levels | Treat runtime changes as privileged operations. |
| `/actuator/threaddump` | JVM thread dump | Restrict because it can expose runtime details. |
| `/actuator/heapdump` | JVM heap dump | Highly sensitive; usually disable or protect tightly. |
| `/actuator/conditions` | Auto-configuration evaluation | Diagnostic and internal. |
| `/actuator/configprops` | Bound configuration properties | May expose sensitive configuration. |
| `/actuator/scheduledtasks` | Registered scheduled tasks | Internal diagnostic information. |
| `/actuator/caches` | Cache information | Internal operational information. |

Example configuration:

```yaml
management:
 endpoints:
 web:
 exposure:
 include: health, info, metrics
 endpoint:
 health:
 show-details: when-authorized
```

Expose only what the operational platform needs. Put sensitive endpoints on a protected management port or behind Spring Security and never assume that an internal network makes `/env` or `/heapdump` safe.

Custom health indicator:

```java
@Component
public class PaymentGatewayHealthIndicator
 implements HealthIndicator {

 @Override
 public Health health() {
boolean gatewayUp = paymentGateway.ping();
 if (gatewayUp) {
 return Health.up()
 .withDetail("provider", "Stripe")
 .withDetail("latency", "45ms")
 .build();
 }
 return Health.down()
 .withDetail("error", "Payment gateway unreachable")
 .build();
 }
}
```

Custom metrics:

```java
@Service
public class OrderService {

 private final Counter orderCounter;
 private final Timer orderTimer;

 public OrderService(MeterRegistry registry) {
 this.orderCounter = Counter.builder("orders.created")
 .tag("type", "online")
 .description("Number of orders created")
 .register(registry);
 this.orderTimer = Timer.builder("orders.processing.time")
 .description("Time spent processing orders")
 .register(registry);
 }

 public Order createOrder(OrderRequest request) {
 return orderTimer.record(() -> {
Order order = processOrder(request);
 orderCounter.increment();
 return order;
 });
 }
}
```

Good production metrics should have stable, low-cardinality tags. Never use an unbounded user ID, request ID or raw URL as a metric tag because the number of time series can grow without control.

### Real-time example: Kubernetes probes wired to Actuator

```yaml
# application.yml
management:
 endpoint:
 health:
 probes:
 enabled: true
 group:
 readiness:
 include: readinessState, db
 endpoints:
 web:
 exposure:
 include: health, info, prometheus
```

```yaml
# Kubernetes deployment (excerpt)
livenessProbe:
 httpGet: { path: /actuator/health/liveness, port: 8080 }
 periodSeconds: 10
 failureThreshold: 3
readinessProbe:
 httpGet: { path: /actuator/health/readiness, port: 8080 }
 periodSeconds: 5
startupProbe:
 httpGet: { path: /actuator/health/liveness, port: 8080 }
 failureThreshold: 30
 periodSeconds: 5
```

```text
Database briefly unavailable:
 readiness -> DOWN -> pod removed from Service endpoints, no user traffic
 liveness -> UP -> pod is NOT restarted (restarting would not fix the DB)
Database recovers:
 readiness -> UP -> traffic resumes automatically
```

A Grafana alert on `rate(orders_created_total[5m]) == 0` during business hours catches a silent failure that health checks cannot see: the app is "healthy" but no orders are being created.

# Section 11: Security Annotations

### Q26. What are the key Spring Security annotations?

**Definition:** Spring Security annotations declare authentication and authorization rules close to the code they protect. Authentication answers “who is calling?” and authorization answers “is that caller allowed to perform this operation?”

The request flow is commonly:

```text
HTTP request
 -> security filter chain authenticates the caller
 -> URL authorization rules are evaluated
 -> controller calls service
 -> method-security interceptor evaluates method annotations
 -> method executes only when access is allowed
```

Example security configuration:

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

 @Bean
 public SecurityFilterChain filterChain(HttpSecurity http)
 throws Exception {
 return http
 .authorizeHttpRequests(auth -> auth
 .requestMatchers("/api/public/**").permitAll()
 .requestMatchers("/api/admin/**").hasRole("ADMIN")
 .requestMatchers("/actuator/health").permitAll()
 .requestMatchers("/actuator/**").hasRole("OPS")
 .anyRequest().authenticated())
 .oauth2ResourceServer(oauth2 ->
 oauth2.jwt(Customizer.withDefaults()))
 .build();
 }
}
```

Do not disable CSRF automatically. For a stateless bearer-token API, the threat model may justify a different CSRF configuration than for a browser session using cookies. Make that decision explicitly.

Method-level security:

```java
@Service
public class OrderService {

 @PreAuthorize("hasRole('ADMIN')")
 public void deleteOrder(Long id) {
 orderRepository.deleteById(id);
 }

 @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
 public List<Order> getOrdersByUser(Long userId) {
 return orderRepository.findByUserId(userId);
 }

 @PreAuthorize("@orderSecurity.canAccess(#orderId, authentication)")
 public Order getOrder(Long orderId) {
 return orderRepository.findById(orderId).orElseThrow();
 }

 @PostAuthorize("returnObject.userId == authentication.principal.id")
 public Order getOrderSecure(Long id) {
 return orderRepository.findById(id).orElseThrow();
 }

 @PreFilter("filterObject.userId == authentication.principal.id")
 public void processBatch(List<Order> orders) {
 orderProcessor.process(orders);
 }
}
| Annotation | When it runs | Purpose |
| --- | --- | --- |
| `@PreAuthorize` | Before the method | Evaluate a SpEL authorization expression. |
| `@PostAuthorize` | After the method | Check the returned object before it is released. |
| `@PreFilter` | Before the method | Filter an input collection. |
| `@PostFilter` | After the method | Filter a returned collection. |
| `@Secured` | Before the method | Perform a simpler role check without SpEL. |

Keep authorization rules at the service boundary as well as at the URL boundary when a business operation can be called from multiple entry points. Be careful with `@PostAuthorize`: the method executes before the result is rejected, so it is not a replacement for pre-authorizing an expensive or destructive operation.

### Real-time example: stopping one customer from reading another customer's claim

```text
Attack: logged-in customer 42 calls GET /api/claims/9001
 claim 9001 belongs to customer 77
URL rule ".authenticated()" -> passes (the caller IS authenticated)
Result without ownership check -> customer 42 sees customer 77's medical claim (IDOR)
```

```java
@Component("claimSecurity")
@RequiredArgsConstructor
class ClaimSecurity {

 private final ClaimRepository claims;

 public boolean isOwner(long claimId, Authentication auth) {
String customerId = ((Jwt) auth.getPrincipal()).getClaimAsString("customer_id");
 return claims.existsByIdAndCustomerId(claimId, customerId);
 }
}

@Service
class ClaimQueryService {

 @PreAuthorize("hasRole('AGENT') or @claimSecurity.isOwner(#claimId, authentication)")
 public ClaimDetails get(long claimId) { /* ... */ }
}
```

```text
Customer 42 -> claim 9001 -> isOwner=false -> 403 Forbidden
Agent -> claim 9001 -> hasRole('AGENT') -> 200
Customer 77 -> claim 9001 -> isOwner=true -> 200
```

Take the customer ID from the **verified token**, never from a request parameter the client can change.

# Section 12: Testing Annotations

### Q27. What testing annotations does Spring Boot provide?

**Definition:** Spring Boot test annotations choose how much of the application to load. A focused test loads only the layer being tested, while an integration test loads multiple real components and infrastructure.

| Annotation | Scope | Context loaded |
| --- | --- | --- |
| `@SpringBootTest` | Full integration test | The full application context. |
| `@WebMvcTest` | Controller and MVC behavior | Web layer only; mock service dependencies. |
| `@DataJpaTest` | Repository and JPA behavior | JPA components and usually an embedded or test database. |
| `@MockBean` | Replace a context bean | A Mockito mock is registered in the context. |
| `@SpyBean` | Wrap a context bean | A Mockito spy delegates to the real bean unless stubbed. |
| `@TestConfiguration` | Test-only infrastructure | Additional beans for tests. |
| `@ActiveProfiles("test")` | Test configuration | Activates the test profile. |

For newer Spring Boot test stacks, `@MockitoBean` and `@MockitoSpyBean` may be available as the current replacement style for context-level Mockito overrides. Use the annotation supported by the project's Spring Boot version and avoid mixing conventions without a reason.

Choose the smallest slice that can prove the behavior:

```text
Controller mapping, JSON and validation -> @WebMvcTest
Repository query and mapping -> @DataJpaTest
Several real application layers -> @SpringBootTest
External dependency -> mock, container or test double
```

### Controller test example

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {

 @Autowired
 private MockMvc mockMvc;

 @MockBean
 private OrderService orderService;

 @Test
void shouldReturnOrder() throws Exception {
OrderDTO order = new OrderDTO(1L, "ORD-001", BigDecimal.TEN);
 when(orderService.getById(1L)).thenReturn(order);

 mockMvc.perform(get("/api/orders/1"))
 .andExpect(status().isOk())
 .andExpect(jsonPath("$.orderNumber").value("ORD-001"))
 .andExpect(jsonPath("$.total").value(10));
 }

 @Test
void shouldReturn404WhenOrderNotFound() throws Exception {
 when(orderService.getById(999L))
 .thenThrow(new ResourceNotFoundException("Order", 999L));

 mockMvc.perform(get("/api/orders/999"))
 .andExpect(status().isNotFound());
 }

 @Test
void shouldValidateInput() throws Exception {
String invalidRequest = """
 { "customerName": "", "email": "not-an-email" }
 """;

 mockMvc.perform(post("/api/orders")
 .contentType(MediaType.APPLICATION_JSON)
 .content(invalidRequest))
 .andExpect(status().isBadRequest());
 }
}
```

This test proves controller behavior without starting a real server or database. The service is mocked because service behavior belongs in a different test.

### Repository test example

```java
@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

 @Autowired
 private OrderRepository orderRepository;

 @Autowired
 private TestEntityManager entityManager;

 @Test
void shouldFindOrdersByStatus() {
Order order = new Order("ORD-001", OrderStatus.PENDING);
 entityManager.persistAndFlush(order);

List<Order> results = orderRepository.findByStatus(OrderStatus.PENDING);

 assertThat(results).hasSize(1);
 assertThat(results.get(0).getOrderNumber()).isEqualTo("ORD-001");
 }
}
```

A repository slice verifies query behavior, mappings and constraints with less startup cost than a full application test. Use `@SpringBootTest` for wiring and cross-layer behavior, and reserve it for scenarios that genuinely require the full context because it is slower and more coupled to application configuration.

### Real-time example: integration test with a real database (Testcontainers)

H2 accepts SQL that PostgreSQL rejects (and vice versa), so a query can pass locally and fail in production. Testcontainers starts the real engine in Docker for the test run.

```java
@SpringBootTest
@Testcontainers
class ClaimSubmissionIT {

 @Container
 @ServiceConnection // Boot 3.1+: wires spring.datasource.* automatically
 static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

 @AutowiredClaimService claimService;
 @AutowiredClaimRepository claimRepository;

 @Test
void duplicateIdempotencyKeyDoesNotCreateSecondClaim() {
ClaimRequest request = ClaimRequest.sample().withIdempotencyKey("key-123");

 claimService.submit(request);
 claimService.submit(request); // client retried after a network timeout

 assertThat(claimRepository.countByIdempotencyKey("key-123")).isEqualTo(1);
 }
}
```

| Test type | What it proves | Speed | Example |
| --- | --- | --- | --- |
| Unit (plain JUnit + Mockito) | Business rules | ms | Premium calculation for age 65+ |
| `@WebMvcTest` | URL mapping, JSON, validation, status codes | ~1 s | Invalid email returns 400 |
| `@DataJpaTest` + Testcontainers | Queries, constraints, mappings | seconds | Unique constraint on policy number |
| `@SpringBootTest` + Testcontainers | Wiring, transactions, idempotency | seconds | Duplicate submit creates one claim |
| Contract test / WireMock | Behaviour against partner APIs | seconds | Payment gateway 503 triggers retry |

# Section 13: Real-World Scenario Questions

### Q28. Scenario: Your Spring Boot application starts up but certain beans are not being created. How do you debug?

**Expected Answer:** Work from evidence, not guesses.

```text
Bean missing
 -> Is the class inside the scanned package tree?
 -> Does it have a stereotype or @Bean method?
 -> Is a @Profile / @Conditional excluding it?
 -> Is a required library missing from the classpath?
 -> Did bean creation fail and get swallowed by lazy init?
```

1.**Run with `--debug`** (or `debug=true`) — prints the condition evaluation report: what was applied, what was skipped and why.
2.**Check `/actuator/conditions`** — positive and negative matches with reasons (secure this endpoint).
3.**Check `/actuator/beans`** — lists every registered bean and its dependencies.
4.**Common causes:**
- Component outside the package scanned by `@ComponentScan` (main class in the wrong package).
- Missing `@Component` / `@Service` / `@Bean`.
- `@Conditional...` evaluated to `false` (property missing or misspelled).
- Missing dependency on the classpath (auto-configuration skipped).
- Profile mismatch — bean has `@Profile("prod")` but the app runs with `dev`.

### Real-time example

```text
Symptom: NoSuchBeanDefinitionException: No qualifying bean of type 'EmailSender'
Layout: com.company.notification.app.NotificationApplication <- @SpringBootApplication
 com.company.notification.mail.SmtpEmailSender <- @Component
Cause: scanning starts at ...notification.app, so ...notification.mail is never scanned
Fix: move the main class to com.company.notification
 (or use scanBasePackages = "com.company.notification")
```

Second real case: `@ConditionalOnProperty(name = "feature.sms.enabled", havingValue = "true")` — the Kubernetes ConfigMap had `FEATURE_SMS_ENABLE=true` (missing **D**). The `--debug` report showed `did not find property 'feature.sms.enabled'`, which located the typo in minutes.

### Q29. Scenario: Your API returns a 200 with an empty body instead of a 404 when a resource doesn't exist. What's wrong?

**Expected Answer:** The service probably returns `null` when the resource is not found, and the controller returns it as HTTP 200 with an empty body. Clients (and monitoring) then believe the call succeeded.

**Fix 1 — throw a domain exception and map it globally:**

```java
@Service
public class OrderService {

 public OrderDTO getById(Long id) {
 return orderRepository.findById(id)
 .map(this::toDTO)
 .orElseThrow(() -> new ResourceNotFoundException("Order", id));
 }
}

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException { /* ... */ }
```

**Fix 2 — return `Optional` and build the `ResponseEntity` in the controller:**

```java
@GetMapping("/{id}")
public ResponseEntity<OrderDTO> getOrder(@PathVariableLong id) {
 return orderService.findById(id)
 .map(ResponseEntity::ok)
 .orElse(ResponseEntity.notFound().build());
}
```

### Real-time impact

```text
Mobile app calls GET /orders/123 for a deleted order
 -> receives 200 with empty body
 -> JSON parser throws, app shows a blank screen
 -> error dashboards show 0% errors, so nobody is alerted
With 404:
 -> app shows "Order no longer exists"
 -> 4xx rate is visible in metrics
```

### Q30. Scenario: You added @Transactional to your service method but the transaction is not rolling back on exception. What could be wrong?

**Expected Answer:** Check these five causes in order.

| # | Cause | Why it happens | Fix |
| --- | --- | --- | --- |
| 1 | Checked exception | By default only `RuntimeException` and `Error` trigger rollback | `@Transactional(rollbackFor = Exception.class)` or throw an unchecked exception |
| 2 | Self-invocation | `this.method()` bypasses the Spring proxy | Call through another bean |
| 3 | Method not proxyable | `private`/`final` methods cannot be intercepted (Spring 6 CGLIB proxies support `protected`/package-private, JDK proxies only interface methods) | Make it a `public` method on a Spring bean |
| 4 | Exception swallowed | The proxy never sees the exception, so it commits | Rethrow, or call `TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()` |
| 5 | Wrong transaction manager | Multiple data sources; default manager covers a different DB | `@Transactional("secondaryTransactionManager")` |

**Cause 4 in code:**

```java
@Transactional
public void process() {
 try {
 riskyOperation();
 } catch (Exception e) {
 log.error("Failed", e);
 // Transaction COMMITS here because no exception propagated
 }
}
```

### Real-time example: money moved but ledger missing

```java
@Transactional
public void transfer(long from, long to, BigDecimal amount) throws LedgerException {
 accountRepository.debit(from, amount);
 accountRepository.credit(to, amount);
 ledgerClient.record(from, to, amount); // throws checked LedgerException
}
```

```text
LedgerException is checked -> no rollback -> debit and credit COMMIT
Finance reconciliation finds balances that do not match the ledger
Fix: @Transactional(rollbackFor = LedgerException.class)
 and move the remote ledger call to an outbox event after commit
```

### Q31. Scenario: Your Spring Boot app takes 45 seconds to start. How do you optimize startup time?

**Expected Answer:** Measure first, then remove the slowest parts.

1.**Find the slow beans** with the startup endpoint:

 ```java
SpringApplication app = new SpringApplication(MyApp.class);
 app.setApplicationStartup(new BufferingApplicationStartup(2048));
 app.run(args);
 ```

 ```properties
 management.endpoints.web.exposure.include=startup
 ```

 Then read `/actuator/startup` for bean initialization timings.

2.**Exclude unnecessary auto-configurations** (or better, remove unused starters):

 ```java
 @SpringBootApplication(exclude = {
 MongoAutoConfiguration.class,
 SecurityAutoConfiguration.class
 })
 ```

3.**Lazy initialization** — `spring.main.lazy-initialization=true`. Faster startup, but errors move to the first request; use with care in production.
4.**Reduce component-scanning scope** — do not scan huge shared libraries.
5.**Review Hibernate/JPA** — use `ddl-auto=validate` or `none`, not `update`; avoid scanning thousands of entities you do not use.
6.**Move slow startup work off the critical path** — a `CommandLineRunner` that loads 500 MB of reference data blocks readiness.
7.**Use CDS / AOT / native image** — Class Data Sharing and Spring AOT reduce JVM startup; GraalVM native images start in milliseconds.
8.**Profile with JFR** (Java Flight Recorder) when the cause is still unclear.

### Real-time example

```text
/actuator/startup showed:
 cacheWarmer 22.0 s <- loads every product into Redis
 entityManagerFactory 9.5 s <- scanning 1,800 entities from a shared JAR
 keyVaultSecretsPropertySource 6.0 s <- 40 sequential secret lookups

Fixes:
 cacheWarmer -> warm asynchronously after ApplicationReadyEvent
 entities -> @EntityScan restricted to the service's own package
 secrets -> fetch only required secrets, in parallel

Result: 45 s -> 11 s; Kubernetes rolling deploy of 20 pods dropped from 18 min to 5 min
```

### Q32. Scenario: You need to run some initialization code after your Spring Boot application is fully started. How do you do it?

**Expected Answer:** Several options, in order of preference.

**1. `ApplicationRunner` (recommended):**

```java
@Component
public class DataInitializer implements ApplicationRunner {

 @Override
 public void run(ApplicationArguments args) {
 loadReferenceData(); // runs after the context is fully refreshed
 }
}
```

**2. `CommandLineRunner`:**

```java
@Component
public class CacheWarmer implements CommandLineRunner {

 @Override
 public void run(String... args) {
 cacheService.warmUp();
 }
}
```

**3. `@EventListener(ApplicationReadyEvent.class)`:**

```java
@Component
public class StartupListener {

 @EventListener(ApplicationReadyEvent.class)
 public void onReady() {
 log.info("Application is ready");
 }
}
```

**4. `@PostConstruct`** — runs after **this one bean** is initialized, *not* after the whole application is ready:

```java
@Service
public class CacheService {

 @PostConstruct
 public void init() {
 loadCache(); // other beans and proxies may not be ready yet
 }
}
```

**Order:**

```text
@PostConstruct (per bean)
 -> context refreshed, web server started
 -> ApplicationStartedEvent
 -> ApplicationRunner / CommandLineRunner (ordered by @Order)
 -> ApplicationReadyEvent
```

| Need | Use |
| --- | --- |
| Validate this bean's own configuration | `@PostConstruct` |
| One-time startup task that must finish before "ready" | `ApplicationRunner` |
| Task that should run after the app is ready (non-blocking) | `ApplicationReadyEvent` + `@Async` |
| Batch job run by Kubernetes CronJob, then exit | `ApplicationRunner` + `System.exit(SpringApplication.exit(ctx))` |

### Real-time example: a Kubernetes CronJob batch

```java
@Component
@RequiredArgsConstructor
public class NightlyEmailBatchRunner implements ApplicationRunner {

 private final EmailBatchService emailBatchService;

 @Override
 public void run(ApplicationArguments args) {
LocalDate businessDate = args.containsOption("date")
 ? LocalDate.parse(args.getOptionValues("date").get(0))
 : LocalDate.now(ZoneId.of("Asia/Tokyo"));
 emailBatchService.sendPendingNotifications(businessDate);
 }
}
```

```text
CronJob starts pod: java -jar batch.jar --date=2026-09-24
 -> runner processes emails
 -> exception? non-zero exit code -> Kubernetes marks the Job failed -> alert fires
 -> success? exit code 0 -> Job completed
```

The `--date` option lets operations re-run a failed day without code changes.

### Q33. What is the difference between @PostConstruct, InitializingBean, and @Bean(initMethod)?

**Expected Answer:** All three run initialization logic after a bean is created and its dependencies are injected.

| Mechanism | Source | When it runs | Advantage |
| --- | --- | --- | --- |
| `@PostConstruct` | Jakarta Annotations (JSR-250) | After DI, before the bean is published | Standard, most common |
| `InitializingBean.afterPropertiesSet()` | Spring | After DI | Explicit interface, but couples code to Spring |
| `@Bean(initMethod = "init")` | Spring | After DI | No Spring dependency inside the class — ideal for third-party classes |

**Execution order:** `@PostConstruct` -> `InitializingBean` -> `initMethod`

```java
@Component
public class MyService implements InitializingBean {

 @PostConstruct // 1st
 public void postConstruct() { }

 @Override // 2nd
 public void afterPropertiesSet() { }

 public void customInit() { } // 3rd (if configured via @Bean(initMethod))
}
```

**Destruction equivalent:** `@PreDestroy` -> `DisposableBean.destroy()` -> `@Bean(destroyMethod)`

### Real-time example

```java
@Configuration
class SftpConfig {

 // Third-party class: we cannot add @PostConstruct to it
 @Bean(initMethod = "connect", destroyMethod = "disconnect")
SftpClient partnerSftpClient(SftpProperties props) {
 return new SftpClient(props.host(), props.port(), props.username());
 }
}

@Component
class TemplateCache {

 private final Map<String, String> templates = new ConcurrentHashMap<>();

 @PostConstruct
void loadTemplates() {
 // Fail fast: the app should not start without its email templates
 templates.putAll(TemplateLoader.loadFromClasspath("email-templates/"));
 if (templates.isEmpty()) {
 throw new IllegalStateException("No email templates found");
 }
 }
}
```

Keep `@PostConstruct` short and local. Do not call remote services or `@Transactional` methods of the same bean from it — proxies and other beans may not be ready yet.

# Section 14: Miscellaneous Annotations

### Q34. Quick-fire — Explain these annotations in one line each.

| Annotation | Purpose | Real-time use |
| --- | --- | --- |
| `@Lazy` | Bean created on first use, not at startup | Heavy PDF engine used only by a rarely called report endpoint |
| `@Scope("prototype")` | New instance every time the bean is requested | Stateful report builder |
| `@Scope("request")` / `@RequestScope` | One instance per HTTP request | Request audit context |
| `@Scope("session")` / `@SessionScope` | One instance per HTTP session | Shopping cart in a server-rendered app |
| `@DependsOn("beanX")` | Ensures `beanX` initializes first | Schema migration bean before cache warmer |
| `@Order(1)` | Execution order of filters, aspects, runners, list injection | Security filter before logging filter |
| `@Conditional(MyCondition.class)` | Bean created only if a custom condition is true | Enable a bean only on a specific cloud provider |
| `@EventListener` | Listens for application events | Send welcome email on `UserRegisteredEvent` |
| `@TransactionalEventListener` | Listens only after the transaction commits | Publish to Kafka only if the DB insert succeeded |
| `@Async` | Method runs in a separate thread | Fire-and-forget audit logging |
| `@EnableScheduling` | Enables `@Scheduled` methods | Nightly clean-up |
| `@CrossOrigin` | Enables CORS for a controller/method | React app on another domain calling the API |
| `@ResponseStatus(HttpStatus.CREATED)` | Sets the HTTP status code | `POST /orders` returns 201 |
| `@JsonIgnore` | Excludes a field from JSON | Hide `passwordHash` |
| `@JsonProperty("full_name")` | Maps a JSON field name to a Java field | Partner API uses snake_case |
| `@JsonFormat(pattern = "yyyy-MM-dd")` | Custom date format | Policy start date |
| `@Slf4j` (Lombok) | Generates a `log` field | Every service class |
| `@RequiredArgsConstructor` (Lombok) | Constructor for `final` fields | Constructor injection without boilerplate |
| `@Data` (Lombok) | Getters, setters, `equals`, `hashCode`, `toString` | DTOs only — avoid on JPA entities (lazy-loading and `hashCode` issues) |
| `@Builder` (Lombok) | Builder pattern | Test data builders |

### Real-time example: `@EventListener` vs `@TransactionalEventListener`

```java
@Service
@RequiredArgsConstructor
class RegistrationService {
 private final UserRepository users;
 private final ApplicationEventPublisher events;

 @Transactional
 public void register(SignUpRequest req) {
User user = users.save(User.from(req));
 events.publishEvent(new UserRegisteredEvent(user.getId(), user.getEmail()));
 }
}

@Component
class WelcomeEmailListener {

 @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
 public void onRegistered(UserRegisteredEvent event) {
 mailer.sendWelcome(event.email());
 }
}
```

```text
@EventListener: email may be sent, then the transaction rolls back -> user gets
 a welcome email for an account that does not exist
@TransactionalEventListener: email sent only after the user row is committed
```

### Q35. What is the Bean lifecycle in Spring? List all the stages.

**Expected Answer:**

```text
 1. Bean class instantiated (constructor called)
 2. Dependencies injected (constructor / setter / @Autowired)
 3. BeanNameAware.setBeanName()
 4. BeanFactoryAware.setBeanFactory()
 5. ApplicationContextAware.setApplicationContext()
 6. BeanPostProcessor.postProcessBeforeInitialization() <- for ALL beans
 7. @PostConstruct
 8. InitializingBean.afterPropertiesSet()
 9. @Bean(initMethod = "customInit")
10. BeanPostProcessor.postProcessAfterInitialization() <- AOP proxies created here
11. ---- Bean is ready for use ----
12. @PreDestroy (on shutdown)
13. DisposableBean.destroy() (on shutdown)
14. @Bean(destroyMethod = "customDestroy") (on shutdown)
```

**Why step 10 matters:** `BeanPostProcessor` is where Spring wraps beans in proxies for `@Transactional`, `@Cacheable`, `@Async` and `@PreAuthorize`. That is why these annotations work only through the proxy and why self-invocation bypasses them.

**Analogy — hiring an employee:**

```text
Constructor -> person is hired
Dependency injection -> gets laptop, badge, email account
Aware callbacks -> learns their employee ID and which office they are in
@PostConstruct -> completes induction training
PostProcessor (proxy) -> gets a manager who reviews their work (transactions, security)
Ready -> starts real work
@PreDestroy -> hands back laptop and badge on the last day
```

### Real-time example: a custom `BeanPostProcessor`

A platform team wants every `RestClient` bean in every microservice to send a correlation-ID header, without asking each team to change code:

```java
@Component
class CorrelationIdRestClientPostProcessor implements BeanPostProcessor {

 @Override
 public Object postProcessAfterInitialization(Object bean, String beanName) {
 if (bean instanceof RestClient client) {
 return client.mutate()
 .requestInterceptor((req, body, exec) -> {
 req.getHeaders().add("X-Correlation-Id", MDC.get("traceId"));
 return exec.execute(req, body);
 })
 .build();
 }
 return bean;
 }
}
```

Shipped inside an internal starter, this applies to every service automatically — the same mechanism Spring itself uses for proxies.

### Reminder: application startup sequence (from Q4)

```text
 1. SpringApplication instance created
 2. Determine application type (SERVLET, REACTIVE, NONE)
 3. Load SpringApplicationRunListeners (publish events)
 4. Prepare Environment (application.yml, env vars, CLI args)
 5. Create ApplicationContext
 6. Load bean definitions (component scanning, @Configuration classes)
 7. Apply auto-configuration (@EnableAutoConfiguration)
 8. Refresh context (create singleton beans; the bean lifecycle above runs for each)
 9. Start embedded web server (Tomcat/Jetty)
10. Call ApplicationRunner / CommandLineRunner beans
11. Publish ApplicationReadyEvent
```

# Section 15: End-to-End Real-Time Case Studies

These case studies connect many annotations and concepts into one realistic flow. In an interview, telling one of these stories is far stronger than reciting definitions.

## Case Study 1: E-commerce checkout API

**Requirement:** A customer places an order. Stock must be reserved, payment charged once, and a confirmation email sent. The API must survive client retries and a slow payment provider.

```text
POST /api/v1/orders (Idempotency-Key: 8f2c...)
 |
 v
OrderController @RestController, @Valid, @RequestHeader
 |
 v
OrderService @Transactional
 |-- idempotency check (unique key in DB)
 |-- reserve stock (optimistic lock @Version)
 |-- save order (status = PENDING_PAYMENT)
 `-- save outbox row (OrderCreated)
 |
 COMMIT
 |
 v
OutboxPublisher @Scheduled + ShedLock -> Kafka topic "orders"
 |
 v
PaymentConsumer @KafkaListener -> PaymentClient (timeout 3s, retry, circuit breaker)
 |
 v
OrderService.markPaid @Transactional -> publishes OrderPaid
 |
 v
EmailConsumer @KafkaListener -> sends confirmation (idempotent by orderId)
```

```java
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
class OrderController {

 private final OrderService orderService;

 @PostMapping
ResponseEntity<OrderResponse> create(
 @RequestHeader("Idempotency-Key") String idempotencyKey,
 @Valid @RequestBodyCreateOrderRequest request) {
OrderResponse response = orderService.create(idempotencyKey, request);
 return ResponseEntity.status(HttpStatus.CREATED).body(response);
 }
}

@Service
@RequiredArgsConstructor
class OrderService {

 private final OrderRepository orders;
 private final InventoryRepository inventory;
 private final OutboxRepository outbox;

 @Transactional
 public OrderResponse create(String idempotencyKey, CreateOrderRequest request) {
 return orders.findByIdempotencyKey(idempotencyKey)
 .map(OrderResponse::from) // retry: return the original result
 .orElseGet(() -> {
 request.items().forEach(i -> inventory.reserve(i.sku(), i.quantity()));
Order order = orders.save(Order.pending(idempotencyKey, request));
 outbox.save(OutboxEvent.orderCreated(order));
 return OrderResponse.from(order);
 });
 }
}
```

| Concern | Concept used | What goes wrong without it |
| --- | --- | --- |
| Client retries after timeout | Idempotency key + unique constraint | Customer charged twice |
| Two buyers, one item left | `@Version` optimistic locking | Stock goes negative |
| DB save + Kafka publish | Transactional outbox | Order saved but event lost (or event sent for a rolled-back order) |
| Slow payment provider | Timeout + circuit breaker, outside the DB transaction | Connection pool exhausted, whole site hangs |
| Duplicate Kafka delivery | Idempotent consumer | Two confirmation emails |
| Bad input | `@Valid` + `@RestControllerAdvice` | 500 errors and leaked stack traces |

## Case Study 2: Nightly notification email batch (insurance)

**Requirement:** Every night, find applications flagged "Not In Good Order" (NIGO), render a Japanese email from a template, send via the mail provider, and record an audit trail. The job runs as a Kubernetes CronJob; operations must be able to re-run a failed day.

```text
Kubernetes CronJob 02:00 JST
 -> java -jar batch.jar --date=2026-09-24
 -> ApplicationRunner
 -> read pending records in chunks of 200 (keyset pagination)
 -> for each record:
 render template (Thymeleaf / FreeMarker)
 send email (timeout 10s, retry 3x on 5xx/timeout only)
 save audit row (status SENT / FAILED, reason)
 -> summary log + metrics (sent, failed, skipped, duration)
 -> exit code 0 (all ok) or 1 (failures) -> alert
```

```java
@Component
@RequiredArgsConstructor
@Slf4j
class NigoEmailBatchRunner implements ApplicationRunner {

 private final PendingNotificationReader reader;
 private final NotificationSender sender;
 private final BatchAuditRepository audit;
 private final NotificationBatchProperties props;

 @Override
 public void run(ApplicationArguments args) {
LocalDate businessDate = resolveDate(args);
BatchSummary summary = new BatchSummary();
String lastId = "";

List<PendingNotification> chunk;
 while (!(chunk = reader.nextChunk(businessDate, lastId, props.chunkSize())).isEmpty()) {
 for (PendingNotification n : chunk) {
 try {
 sender.send(n); // idempotent: skips if already SENT
 audit.markSent(n.id());
 summary.sent();
 } catch (PermanentMailException ex) { // invalid address: do not retry
 audit.markFailed(n.id(), ex.getMessage());
 summary.failed();
 }
 }
 lastId = chunk.get(chunk.size() - 1).id();
 }
 log.info("NIGO batch date={} {}", businessDate, summary);
 if (summary.failedCount() > 0) {
 throw new BatchCompletedWithFailuresException(summary); // non-zero exit code
 }
 }
}
```

| Concern | Concept used |
| --- | --- |
| Settings validated before any email is sent | `@ConfigurationProperties` + `@Validated` |
| Memory stays flat for 1 million rows | Keyset pagination in chunks, not `findAll()` |
| Re-run a failed day | `ApplicationArguments` `--date` option |
| Re-run does not re-send already sent emails | Idempotent sender checks audit status |
| Retry only what can succeed | Classify permanent vs transient mail errors |
| Only one execution at a time | CronJob `concurrencyPolicy: Forbid` (or ShedLock for `@Scheduled`) |
| Operations visibility | Structured summary log, Micrometer counters, exit code |

## Case Study 3: Production incident — "API suddenly times out at 10 a.m."

```text
Symptom: p99 latency 200 ms -> 30 s, many 500s; CPU only 20%
Metric: hikaricp_connections_pending = 180, active = 20 (max 20)
Thread dump: 200 Tomcat threads WAITING in HikariPool.getConnection()
Trace: each slow request spent 29.8 s inside PaymentClient.call()
Code: @Transactional method calls PaymentClient (no read timeout) while holding a DB connection
Trigger: payment provider degraded at 10:00
```

**Root cause chain:**

```text
Provider slow -> no HTTP timeout -> request waits -> DB connection held by open transaction
-> pool (20) exhausted -> every other endpoint also waits for a connection
-> whole service appears down, even endpoints that never call payment
```

**Fixes (short-term to long-term):**

1. Add connect/read timeouts to the payment client (2 s / 5 s).
2. Move the remote call **outside** the `@Transactional` boundary.
3. Add a Resilience4j circuit breaker and bulkhead around the payment client.
4. Alert on `hikaricp_connections_pending > 0` for more than one minute.
5. Add a regression load test with a slow WireMock payment stub.

**Interview answer shape:***symptom -> evidence (metrics, thread dump, trace) -> root cause -> immediate mitigation -> permanent fix -> prevention.*

## Case Study 4: Kafka consumer processing a message twice

```text
1. Consumer receives PolicyIssued(eventId=E-501)
2. Inserts welcome-pack row and calls print vendor
3. Pod is killed (deployment) before the offset is committed
4. New pod receives E-501 again -> second welcome pack printed and posted
```

**Fix: idempotent consumer with a processed-events table:**

```java
@KafkaListener(topics = "policy-issued", groupId = "welcome-pack")
@Transactional
public void onPolicyIssued(PolicyIssued event) {
 try {
 processedEvents.insert(event.eventId()); // PRIMARY KEY(event_id)
 } catch (DuplicateKeyException alreadyProcessed) {
 log.info("Skipping duplicate event {}", event.eventId());
 return;
 }
 welcomePacks.create(event.policyNumber()); // same DB transaction as the insert
}
```

```text
First delivery: insert E-501 ok -> welcome pack created -> commit
Redelivery: insert E-501 -> duplicate key -> skip -> commit offset
```

The external print call is then triggered from an outbox row, so it too happens exactly once from the business point of view.

## How to use these case studies in an interview

```text
1. Context - "We had a nightly email batch for 300k customers..."
2. Problem - "After scaling to 3 pods, customers received duplicate emails."
3. Analysis - "Logs showed three pods starting the same @Scheduled job at 02:00."
4. Solution - "We added ShedLock with a JDBC lock table and an idempotent sender."
5. Result - "Duplicates dropped to zero; we added a metric and alert."
6. Trade-off - "Lock depends on the DB, so we set lockAtMostFor to survive crashes."
```

# Spring Boot — 60 Senior Developer Interview Questions & Answers

>**Revision track:** This section preserves the compact 60-question interview material. Use it for rapid review after reading the expanded answers above. When a topic needs a definition, lifecycle explanation, example or production caveat, follow the corresponding expanded Q&A or appendix section rather than treating this compact section as the complete explanation.

## How to use this revision track

```text
Learn the definition and concept in the expanded sections
 -> practice the compact interview answer
 -> verify production tradeoffs in the appendix
 -> answer the question aloud with one example
```

---

# Section 1 — Spring Boot Fundamentals

## 1. What is Spring Boot and why was it introduced?

**Definition:** Spring Boot is an opinionated layer on Spring that simplifies application setup, dependency management, embedded-server startup and production operations.

**Concept explanation:** Boot reduces infrastructure ceremony through starters and auto-configuration, while still allowing the application to replace defaults when it needs different behavior.

Answer:

Spring Boot is built on top of the Spring Framework and simplifies building production-ready Spring applications.

Traditional Spring applications required significant configuration for:

- Dependency configuration
- Web server configuration
- Database configuration
- MVC setup
- JSON serialization
- Application deployment

Spring Boot provides:

- Auto-configuration
- Starter dependencies
- Embedded servers
- Externalized configuration
- Production-ready Actuator
- Opinionated defaults
- Easy testing
- Executable JARs

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

**Definition:** Spring Framework provides the foundational IoC, web, data and transaction capabilities; Spring Boot packages common Spring application setup into convention-based defaults.

**Concept explanation:** Boot uses Spring rather than replacing it. The framework supplies the building blocks, while Boot configures and packages a common production-ready application around those blocks.

Answer:

| Spring Framework | Spring Boot |
| --------------------------------- | ------------------------------------- |
| Core framework | Built on Spring |
| Requires more configuration | Convention over configuration |
| No embedded server by itself | Embedded server support |
| Dependency management is manual | Starters simplify dependencies |
| Production features require setup | Actuator provides production features |
| More flexible | More opinionated |

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

**Definition:** `@SpringBootApplication` marks the main Boot configuration class and combines component scanning, auto-configuration and Boot configuration metadata.

**Concept explanation:** One annotation starts three discovery processes: find application components, import conditional infrastructure and identify the class used to bootstrap the application.

Answer:

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

**Definition:** Auto-configuration creates common Spring beans when classpath, bean and property conditions indicate that the application needs them.

**Concept explanation:** Boot supplies defaults conditionally and backs away when the application provides its own configuration, which makes the mechanism convenient without making it irreversible.

Answer:

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

- DataSource
- EntityManagerFactory
- Transaction infrastructure

provided the necessary conditions are satisfied.

Spring Boot's auto-configuration is non-invasive: if you define your own bean, Boot can back away from its default configuration. ([Home][2])

---

## 5. How does Spring Boot Auto-Configuration work internally?

**Definition:** The auto-configuration process imports candidate configuration classes and evaluates their conditions before registering matching beans.

**Concept explanation:** The classpath and environment select candidates; conditional annotations decide whether each candidate is applied, and the condition report explains the decision.

Answer:

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

**Definition:** `@ConditionalOnClass` activates configuration only when a specified class is available on the application classpath.

**Concept explanation:** It lets a starter remain optional: Boot can include a configuration candidate without trying to create beans for a library the application did not include.

Answer:

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

**Definition:** `@ConditionalOnMissingBean` registers a default bean only when the application has not already supplied a matching bean.

**Concept explanation:** This condition is the main customization escape hatch in auto-configuration: Boot provides a sensible default, but an application-defined bean takes precedence.

Answer:

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

**Definition:** The condition evaluation report records which auto-configuration conditions matched and which did not.

**Concept explanation:** Running with `--debug` turns a hidden startup decision into evidence: it shows why a bean was created, skipped or prevented by an existing bean or missing dependency.

Answer:

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

**Definition:** A starter is a curated dependency descriptor that brings together the libraries commonly needed for one application capability.

**Concept explanation:** A starter answers “which libraries do I need?” while auto-configuration answers “how should those libraries be wired into Spring?”

Answer:

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

**Definition:** A starter supplies dependencies; auto-configuration uses those dependencies and application conditions to create Spring beans.

**Concept explanation:** Adding a starter changes the classpath, which may activate matching conditional configuration, but the starter itself does not perform bean creation.

Answer:

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

**Definition:** Spring Boot dependency management provides a tested set of compatible library versions through its BOM and Maven or Gradle dependency-management support.

**Concept explanation:** Applications can omit many individual versions so related Spring and third-party modules are upgraded as a compatible group instead of drifting independently.

Answer:

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

**Definition:** The startup lifecycle is the ordered process of preparing configuration, creating the application context, registering and initializing beans, starting infrastructure and publishing readiness events.

**Concept explanation:** Startup has phases with different failure meanings, so the event and log position help distinguish configuration errors, bean errors, server errors and runner failures.

Answer:

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

**Definition:** `SpringApplication` is Boot's bootstrap coordinator for creating the application context, applying configuration and starting the application.

**Concept explanation:** It detects the application type, prepares the environment, loads sources, refreshes the context, starts the embedded server when appropriate and publishes lifecycle events.

Answer:

`SpringApplication` is responsible for bootstrapping a Spring Boot application.

Example:

```java
SpringApplication.run(
 Application.class,
 args
);
```

It:

- Creates the application context
- Loads environment/configuration
- Applies Boot initialization
- Starts the embedded web server when appropriate
- Publishes lifecycle events

---

## 14. What is `CommandLineRunner`?

**Definition:** `CommandLineRunner` is a callback interface whose `run` method executes after the application context has started and command-line arguments are available as strings.

**Concept explanation:** It is suitable for short startup actions such as validation or seed data, but long or unreliable work can delay readiness and should be moved to a controlled job or lifecycle design.

Answer:

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

- Initial data loading
- Startup validation
- One-time initialization

But avoid using it for long-running blocking operations because that can delay application readiness.

---

## 15. What is `ApplicationRunner` and how is it different from `CommandLineRunner`?

**Definition:** `ApplicationRunner` is a startup callback like `CommandLineRunner`, but it receives parsed `ApplicationArguments` instead of a raw string array.

**Concept explanation:** Choose `ApplicationRunner` when options and non-option arguments need structured access; choose `CommandLineRunner` when raw argument strings are sufficient.

Answer:

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

**Definition:** Externalized configuration keeps environment-dependent values outside compiled Java code and loads them from ordered property sources.

**Concept explanation:** The same artifact can run in development, testing and production because higher-precedence files, environment variables or command-line arguments override lower-precedence defaults.

Answer:

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

**Definition:** `@Value` injects individual property expressions, while `@ConfigurationProperties` binds a related property namespace to a type-safe configuration object.

**Concept explanation:** Small isolated values fit `@Value`; grouped settings with validation, conversion and documentation are easier to maintain with `@ConfigurationProperties`.

Answer:

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

# Core Mental Model: IoC, ApplicationContext, Beans, DI and Annotations as One Story

For a senior interview you should be able to explain **IoC Container -> ApplicationContext -> Beans -> Dependency Injection -> Spring Boot annotations** as one connected story rather than as separate definitions.

# 1. What is IoC?

IoC = Inversion of Control.

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
 ↓ ↓
OrderService PaymentService
 ↑
 |
 dependency injected
```

That's **Inversion of Control**.

### Interview definition

>**IoC means that the responsibility for creating, configuring, managing, and wiring application objects is transferred from the application code to the Spring container.**

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

- Creating objects
- Injecting dependencies
- Managing bean lifecycle
- Applying configuration
- Managing scopes
- Applying proxies
- Handling certain cross-cutting concerns

Conceptually:

```text
 IoC Container
 |
 ┌─────────────┼─────────────┐
 ↓ ↓ ↓
 OrderService PaymentService UserRepository
 | | |
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

>**ApplicationContext is the central Spring container that creates, configures, stores, and manages beans and also provides additional application-level features.**

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

- Dependencies are explicit
- `final` fields can be used
- Easier unit testing
- Object cannot normally exist without required dependencies
- Better immutability
- Avoids hidden dependencies

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
 ↓ ↓ ↓ ↓
Startup Beans Configuration Web
 ↓ ↓ ↓ ↓
@Spring @Service @Configuration @RestController
Boot @Repository @Bean @GetMapping
 @Component @Value @PostMapping
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
 | └── UserController
 |
 +-- service
 | └── UserService
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

- You don't own the class
- You need custom construction
- Third-party library object
- Complex initialization

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

- Kubernetes CronJob
- Quartz
- ShedLock
- Cloud scheduler/EventBridge

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
 / \
Yes No
 | |
Return DB
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
 @RequestBodyUserRequest request) {
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
 @PathVariableLong id) {
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
 @RequestParamString status) {
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
 @PathVariableLong id) {

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
 ↓ ↓ ↓
 Controller Service Repository
 | | |
 └────────────┼────────────┘
 |
 Dependency Injection
```

---

# 50. The Most Important Concept to Remember

If an interviewer asks:

>**"Explain ApplicationContext, IoC, DI and Spring Boot annotations."**

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
 | |
 Creates Beans Manages Beans
 | |
 └──────────┬──────────┘
 |
 v
 Dependency Injection
 |
 ┌──────────────┼──────────────┐
 ↓ ↓ ↓
 Controller Service Repository
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

### Senior interview one-liner

>**"Spring Boot starts the application and creates an ApplicationContext. The ApplicationContext acts as the IoC container, managing Spring Beans and their lifecycle. IoC means Spring takes responsibility for object creation and wiring, while Dependency Injection is the mechanism through which those dependencies are supplied to objects. Spring Boot annotations such as `@SpringBootApplication`, `@Service`, `@Repository`, `@RestController`, `@Configuration`, `@Bean`, and `@ConfigurationProperties` provide metadata that tells Spring how to discover, create, configure, and manage those components."**

That is the **core mental model** you should carry into almost every Spring Boot interview.

## 18. What are Spring Profiles?

**Definition:** A Spring profile is a named set of configuration values and bean definitions for an environment or runtime mode.

**Concept explanation:** Profiles let one application artifact select development, test, staging or production behavior without changing the source code or rebuilding the application.

Answer:

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

**Definition:** Environment configuration management is the practice of separating shared defaults, environment overrides and secrets while keeping deployment behavior repeatable.

**Concept explanation:** Store non-sensitive differences in external configuration, inject secrets through a managed secret system and promote the same immutable artifact through environments.

Answer:

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
Secret -> credentials/secrets
```

The application artifact remains the same.

---

## 20. Where should passwords and secrets be stored?

**Definition:** A secret is sensitive operational data such as a password, token, private key or connection credential that must be protected from source control and ordinary logs.

**Concept explanation:** Applications should retrieve secrets at runtime from a managed secret store or protected platform binding, use least-privilege access and rotate credentials without exposing them in configuration files.

Answer:

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

**Definition:** `@Controller` maps requests to MVC handler methods that commonly return views, while `@RestController` treats method return values as HTTP response bodies by default.

**Concept explanation:** The distinction is the default response interpretation: a controller can resolve a view name, whereas a REST controller delegates object serialization to an HTTP message converter such as Jackson.

Answer:

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

**Definition:** JSON serialization converts a Java object into a JSON representation that can be sent in an HTTP response.

**Concept explanation:** Spring MVC selects an `HttpMessageConverter`, commonly Jackson, based on the return type and requested media type, then serializes accessible properties and applies configured naming, date and inclusion rules.

Answer:

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

**Definition:** Global exception handling maps exceptions raised by controllers or services to consistent HTTP status codes and error response bodies.

**Concept explanation:** `@RestControllerAdvice` centralizes the mapping so individual controllers do not repeat error logic, while specific handlers should run before a final safe catch-all handler.

Answer:

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

- Centralized error handling
- Consistent API responses
- Cleaner controllers
- Easier monitoring

---

## 24. How do you design a production-grade REST API in Spring Boot?

**Definition:** A production-grade REST API has a stable HTTP contract, clear resource semantics, validation, consistent errors, security, observability and operational safeguards.

**Concept explanation:** API design is more than controller mappings: clients depend on status codes, schemas, idempotency, pagination, compatibility and failure behavior over time.

Answer:

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

**Definition:** Request validation checks incoming data against declared constraints before business logic processes it.

**Concept explanation:** Bean Validation keeps structural rules on request models, while the controller activates validation and a global handler converts violations into a stable client-facing error format.

Answer:

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
 @Valid @RequestBodyCreateUserRequest request) {

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

**Definition:** JPA auto-configuration creates persistence infrastructure such as the `DataSource`, `EntityManagerFactory`, transaction manager and repository support when the required dependencies and properties are present.

**Concept explanation:** Boot combines classpath detection, conditional configuration and external properties, then lets explicit application beans override defaults where necessary.

Answer:

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

**Definition:** `save()` persists or merges an entity within the current persistence context, while `saveAndFlush()` additionally requests that pending changes be synchronized with the database immediately.

**Concept explanation:** Flushing is not the same as committing: the transaction may still roll back after a flush, so use early flushing only when subsequent work needs database-visible changes or immediate constraint feedback.

Answer:

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

**Definition:** N+1 is a query pattern with one query for parent rows followed by one additional query for each of N related rows.

**Concept explanation:** The result can be correct but inefficient; choose a fetch plan such as `JOIN FETCH`, `@EntityGraph`, batching or DTO projection based on the use case.

Answer:

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

**Definition:** `@Transactional` defines a transaction boundary around a method so related database operations can commit or roll back as one unit.

**Concept explanation:** Spring normally applies the boundary through a proxy that starts or joins a transaction before the method and commits or rolls it back after the method returns or throws.

Answer:

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

**Definition:** A transactional annotation works only when the invocation reaches the Spring-managed proxy and the transaction manager can create or join a transaction.

**Concept explanation:** Self-invocation, private or final methods, non-managed objects, wrong transaction managers and unexpected exception rules can bypass or change the expected transaction behavior.

Answer:

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

**Definition:** Transaction propagation defines how a method behaves when its caller already has an active transaction.

**Concept explanation:** Propagation chooses whether to join, create, suspend or reject a transaction, which controls atomicity and whether selected work can commit independently.

Answer:

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

**Definition:** Transaction isolation controls which changes made by concurrent transactions are visible to a transaction and which read anomalies are prevented.

**Concept explanation:** Stronger isolation can reduce anomalies but may increase locking, contention or retry pressure, so choose it from the business consistency requirement rather than by default.

Answer:

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

**Definition:** Actuator provides operational endpoints and metrics integration for observing application health, runtime state and performance.

**Concept explanation:** Actuator is an operations interface, not a public business API; expose only required endpoints and protect diagnostic data with authentication and authorization.

Answer:

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

**Definition:** Kubernetes health integration uses application health endpoints to decide whether a container should receive traffic or be restarted.

**Concept explanation:** Readiness controls traffic eligibility, liveness detects an unrecoverable process state and startup probes protect slow-starting applications from premature restarts.

Answer:

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

**Definition:** Liveness asks whether the process should be restarted; readiness asks whether the instance can safely receive traffic.

**Concept explanation:** A dependency outage may make an instance unready without making the process dead, so combining the two checks can cause unnecessary restart loops.

Answer:

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

**Definition:** Production monitoring combines metrics, logs, traces, health signals and alerts to show system behavior and user impact.

**Concept explanation:** Use the signals together: metrics identify a pattern, logs explain an event, traces locate latency across dependencies and health checks show whether traffic can be served.

Answer:

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

**Definition:** Micrometer is an instrumentation facade that records application metrics and exports them to monitoring systems through supported registries.

**Concept explanation:** Code can define counters, timers and gauges without binding business code directly to one monitoring vendor, while tags provide dimensions for controlled analysis.

Answer:

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

**Definition:** Production logging is the structured recording of events needed to diagnose behavior, failures and security-relevant actions.

**Concept explanation:** Logs should be searchable, correlated with a request or trace, appropriately leveled and free of secrets or uncontrolled high-cardinality payloads.

Answer:

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

**Definition:** Runtime log-level management changes the logger configuration of a running application without rebuilding or restarting it.

**Concept explanation:** Actuator can expose controlled logger changes for diagnosis, but the endpoint must be privileged and temporary changes should be recorded and reverted.

Answer:

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

**Definition:** Caching stores a reusable result under a key so repeated requests can avoid repeating an expensive computation or data lookup.

**Concept explanation:** A cache improves latency and dependency load only when its key, expiry, invalidation and consistency rules match the underlying data's behavior.

Answer:

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
 / \
Yes No
 | |
Return DB
 |
 v
 Cache
```

---

## 41. What is the difference between `@Cacheable`, `@CachePut`, and `@CacheEvict`?

**Definition:** `@Cacheable` reads or populates a cache, `@CachePut` always executes the method and updates the cache, and `@CacheEvict` removes cached entries.

**Concept explanation:** These annotations express different cache lifecycle actions, so choosing one depends on whether the method is a read, an authoritative refresh or an invalidation operation.

Answer:

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

**Definition:** Asynchronous execution submits work to a separate executor so the initiating thread does not wait for the task to complete.

**Concept explanation:** A reliable async design specifies executor capacity, queue and rejection behavior, error reporting, context propagation and whether the caller needs a result.

Answer:

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

**Definition:** Scheduling invokes a method according to a fixed rate, fixed delay or calendar-based cron expression.

**Concept explanation:** In a multi-instance deployment, a local scheduler may run the same job on every instance, so use coordination or a distributed lock when the job must execute once globally.

Answer:

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

**Definition:** `@SpringBootTest` loads a broad or full Spring Boot application context for integration testing.

**Concept explanation:** It verifies real wiring across multiple layers, but it is slower and more configuration-sensitive than a focused test slice, so use it for cross-component behavior rather than every unit case.

Answer:

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

**Definition:** A unit test isolates one small unit with controlled collaborators; an integration test verifies that real components or infrastructure work together.

**Concept explanation:** Unit tests provide fast feedback about logic, while integration tests provide confidence about wiring, serialization, persistence, transactions and external boundaries.

Answer:

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

**Definition:** A test slice loads only the Spring components needed for one application layer, such as MVC or JPA.

**Concept explanation:** Slices reduce startup time and isolate failures, but they do not prove that the entire application context can wire successfully.

Answer:

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

**Definition:** Latency troubleshooting identifies where request time is being spent and which change or dependency caused the regression.

**Concept explanation:** Start with user-impact metrics and traces, then separate application CPU, database, network, pool, lock, queue and downstream-service time before changing code.

Answer:

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

**Definition:** HikariCP is a JDBC connection-pool implementation that reuses database connections instead of opening a new connection for every operation.

**Concept explanation:** Pool sizing, timeout settings and database capacity must be designed together; a larger pool does not automatically increase throughput and can overload the database.

Answer:

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

**Definition:** Service-to-service REST communication is an HTTP client interaction with an explicit contract, timeout, authentication, error and retry policy.

**Concept explanation:** A production client must treat the network as unreliable and define bounded timeouts, safe retries, observability, compatibility and failure isolation.

Answer:

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

**Definition:** A timeout limits how long a request waits for a dependency before failing or taking a fallback path.

**Concept explanation:** Without bounded waits, a slow dependency consumes request threads and connection-pool capacity, amplifying a small outage into a wider cascading failure.

Answer:

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

**Definition:** A circuit breaker temporarily stops calls to a failing dependency after a threshold of failures or slow responses is reached.

**Concept explanation:** It prevents repeated work against an unhealthy dependency, provides a fast failure or fallback, and later probes for recovery through a half-open state.

Answer:

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

**Definition:** A retry is a repeated attempt after a transient failure; it is unsafe when repeating the operation cannot improve the outcome or may duplicate a side effect.

**Concept explanation:** Do not blindly retry validation errors, authentication failures, non-idempotent writes or permanent business failures; use idempotency keys and bounded backoff when retrying is justified.

Answer:

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

**Definition:** Kafka integration connects a Spring application to durable event topics through producers, consumers, serialization, offset management and error handling.

**Concept explanation:** Correctness depends on delivery and processing semantics, partition ordering, consumer-group ownership, schema compatibility and what happens when processing fails.

Answer:

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

**Definition:** An uncommitted Kafka offset means the broker may deliver the record again after the consumer restarts or its partition is reassigned.

**Concept explanation:** This creates at-least-once processing, so consumers should be idempotent or use a deduplication strategy when repeated delivery is possible.

Answer:

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

**Definition:** Containerization packages the application and its runtime dependencies into an immutable image with a predictable startup command and resource boundary.

**Concept explanation:** A good image is small, reproducible, non-root where possible, configurable through the environment and compatible with health checks and graceful shutdown.

Answer:

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

**Definition:** Kubernetes configuration makes the application discoverable, configurable, observable and safely replaceable inside a cluster.

**Concept explanation:** The deployment must align resource limits, probes, external configuration, secret injection, shutdown behavior, scaling and service networking with the application's runtime behavior.

Answer:

A typical architecture:

```text
 EKS
 |
 ┌────────────┼────────────┐
 | | |
 Pod Pod Pod
 | | |
 Spring Boot Spring Boot Spring Boot
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

**Definition:** Native image support compiles the application ahead of time into a platform-specific executable with a fast startup and lower runtime memory profile.

**Concept explanation:** AOT compilation changes the runtime model, so reflection, proxies, resources and dynamic class loading may require explicit reachability configuration or framework support.

Answer:

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

**Definition:** A custom starter is a reusable dependency and auto-configuration package that standardizes an enterprise capability across Spring Boot applications.

**Concept explanation:** A starter can provide shared defaults for logging, security, tracing or client conventions while allowing application-specific overrides through properties and conditional beans.

Answer:

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

**Definition:** Startup troubleshooting classifies the failure by phase, identifies the first meaningful cause and verifies the smallest configuration or code change that resolves it.

**Concept explanation:** Read the deepest useful cause rather than the final wrapper exception, then check configuration binding, component scanning, conditional beans, dependencies, ports, database access and startup callbacks.

Answer:

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
kubectl describepod
kubectl getevents
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

**Definition:** p99 latency is the response time below which 99 percent of requests complete; a sudden increase means the slowest one percent is experiencing a regression even when average CPU is normal.

**Concept explanation:** Investigate queueing, database waits, connection pools, locks, downstream latency, garbage collection, thread pools and traffic shape using traces and correlated metrics rather than assuming CPU is the bottleneck.

Answer:

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
 | | |
 DB Redis Kafka
 | | |
 v v v
 Latency Latency Consumer Lag
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
Service A 20ms
 |
 v
Service B 1.8s <-- bottleneck
 |
 v
Database 10ms
```

Then fix the actual bottleneck rather than blindly increasing:

```text
CPU
JVM heap
threads
connection pool
```

---

# The 15 Spring Boot Questions You Must Master

For a **Senior Java Developer interview**, if you have limited time, prioritize these:

| Priority | Topic | Interview Importance |
| -------- | --------------------------------------------------- | -------------------- |
| ⭐⭐⭐⭐⭐ | `@SpringBootApplication` internals | Very High |
| ⭐⭐⭐⭐⭐ | Auto-configuration | Very High |
| ⭐⭐⭐⭐⭐ | `@ConditionalOnClass` / `@ConditionalOnMissingBean` | Very High |
| ⭐⭐⭐⭐⭐ | Spring Boot startup lifecycle | Very High |
| ⭐⭐⭐⭐⭐ | `@ConfigurationProperties` vs `@Value` | Very High |
| ⭐⭐⭐⭐⭐ | Profiles/configuration management | Very High |
| ⭐⭐⭐⭐⭐ | `@Transactional` internals | Very High |
| ⭐⭐⭐⭐⭐ | Actuator | Very High |
| ⭐⭐⭐⭐⭐ | Liveness vs Readiness | Very High |
| ⭐⭐⭐⭐⭐ | HikariCP | Very High |
| ⭐⭐⭐⭐⭐ | N+1 / JPA performance | Very High |
| ⭐⭐⭐⭐⭐ | Microservice timeouts/retries | Very High |
| ⭐⭐⭐⭐⭐ | Spring Boot + Kafka | Very High |
| ⭐⭐⭐⭐⭐ | Kubernetes/EKS deployment | Very High |
| ⭐⭐⭐⭐⭐ | Production troubleshooting | **Critical** |

---

# How to Prepare These for a Senior Interview

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

[1]: https://docs.spring.io/spring-boot/reference/index.html?utm_source=chatgpt.com"Reference :: Spring Boot"
[2]: https://docs.spring.io/spring-boot/reference/using/auto-configuration.html?utm_source=chatgpt.com"Auto-configuration :: Spring Boot"
[3]: https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html?utm_source…"Creating Your Own Auto-configuration :: Spring Boot"
[4]: https://docs.spring.io/spring-boot/reference/features/external-config.html?utm_source=chatgpt.com"Externalized Configuration :: Spring Boot"
[5]: https://docs.spring.io/spring-boot/system-requirements.html?utm_source=chatgpt.com"System Requirements :: Spring Boot"

---

# Production and Current-Industry Appendix

The complete supplied Spring Boot interview material above is preserved. This appendix connects those concepts to production design, failure handling, security, testing, operations and senior-level interview answers.

# Appendix A1. The Senior Spring Boot Mental Model

Think of a Spring Boot application as several cooperating layers:

```text
HTTP/event entry point
 |
 v
Validation and authorization
 |
 v
Application use case
 |
 +--> domain rules
 +--> repository or client
 +--> transaction boundary
 `--> event/outbox/workflow
 |
 v
Response, event or durable state
```

Spring provides infrastructure for these layers, but it does not decide business correctness automatically. A senior developer explains where validation, authorization, transaction ownership, retries and observability belong.

# Appendix A2. Constructor Injection and Bean Design

Prefer required dependencies in constructors:

```java
@Service
public class OrderService {

 private final OrderRepository orderRepository;
 private final PaymentClient paymentClient;

 public OrderService(
OrderRepository orderRepository,
PaymentClient paymentClient) {
 this.orderRepository = orderRepository;
 this.paymentClient = paymentClient;
 }
}
```

Benefits:

- The object cannot be created without required dependencies.
- Fields can remain final.
- Unit tests can construct the class directly.
- Circular dependencies become visible early.
- The class's dependency count is easy to review.

If a service has many dependencies, the problem may be design complexity rather than a need for more injection annotations. Split responsibilities around use cases or ports.

# Appendix A3. Configuration with `@ConfigurationProperties`

Use type-safe configuration for related settings:

```java
@ConfigurationProperties(prefix = "payment")
public record PaymentProperties(
URI baseUrl,
Duration timeout,
int maxAttempts) {
}
```

```yaml
payment:
 base-url: https://payment.internal
 timeout: 2s
 max-attempts: 3
```

This is clearer than scattering string lookups across services. Validate required configuration at startup:

```java
@ConfigurationProperties(prefix = "payment")
@Validated
public record PaymentProperties(
 @NotNullURI baseUrl,
 @NotNullDuration timeout,
 @Min(1) int maxAttempts) {
}
```

Use environment variables, Parameter Store or a secret manager for deployment-specific values. Do not commit passwords, private keys or production tokens into YAML, source code or container images.

# Appendix A4. Configuration Precedence and Safety

When debugging configuration, identify the winning property source:

```text
application.yml
profile-specific YAML
environment variables
command-line arguments
external configuration/imports
secret/configuration service
```

The exact precedence depends on the Spring Boot version and configuration mechanism. Verify it rather than guessing. For every sensitive property, document:

```text
Owner
Source
Rotation process
Allowed environments
Failure behaviour
Audit requirements
```

Fail fast for mandatory configuration. A service that starts with an empty signing key is more dangerous than a service that refuses to start.

# Appendix A5. REST API Boundaries

A production controller should be thin:

```java
@RestController
@RequestMapping("/orders")
public class OrderController {

 private final OrderApplicationService service;

 @PostMapping
ResponseEntity<OrderResponse> create(
 @Valid @RequestBodyCreateOrderRequest request) {
OrderResponse response = service.create(request);
 return ResponseEntity.status(HttpStatus.CREATED).body(response);
 }
}
```

Keep transport DTOs separate from persistence entities and domain objects. This avoids exposing database structure as a public API and lets the API evolve independently.

Define:

- Request and response schemas.
- Status-code behaviour.
- Validation errors.
- Idempotency requirements.
- Authentication and resource authorization.
- Pagination and filtering rules.
- Compatibility and deprecation policy.

# Appendix A6. Validation and Problem Details

Validate at the boundary, then enforce business rules in the application layer. Use a consistent error shape, such as an RFC 9457-style problem detail:

```json
{
"type": "https://api.example.com/problems/validation-error",
"title": "Request validation failed",
"status": 400,
"detail": "One or more fields are invalid",
"instance": "/orders",
"errors": [
 {
"field": "items",
"code": "NotEmpty"
 }
 ],
"traceId": "abc-123"
}
```

Do not expose stack traces, SQL text, internal hostnames or secret values to API clients. Log diagnostic details internally with a correlation ID.

# Appendix A7. Exception Handling Strategy

Use a single, intentional mapping from application failures to HTTP responses:

```text
Validation failure -> 400
Authentication failure -> 401
Authorization failure -> 403
Resource not found -> 404
Business conflict -> 409
Rate limit -> 429
Unexpected failure -> 500
```

The exact status depends on the API contract. Avoid catching `Exception` everywhere and returning success. That hides outages and can cause message consumers to acknowledge failed work.

For asynchronous handlers, classify errors explicitly:

```text
Permanent invalid message -> quarantine/DLQ and acknowledge deliberately
Transient dependency error -> bounded retry
Business conflict -> record outcome according to contract
Unknown failure -> fail visibly and preserve evidence
```

# Appendix A8. Transaction Ownership in Spring

Place `@Transactional` around an application use case that owns one database consistency boundary:

```java
@Service
public class OrderApplicationService {

 @Transactional
 public OrderId create(CreateOrderCommand command) {
Order order = orderFactory.create(command);
 orderRepository.save(order);
 outboxRepository.save(OrderCreated.from(order));
 return order.id();
 }
}
```

The transaction protects the configured database resources. It does not make an HTTP call, Kafka publish or email send atomic with the database. Use an outbox, workflow or explicit compensation for cross-system operations.

# Appendix A9. Spring Transaction Traps

Senior interview points:

- Proxy-based transaction interception can be bypassed by self-invocation.
- Runtime exceptions normally trigger rollback; checked exceptions need explicit policy.
- A transaction on a private method may not be intercepted as expected.
- Long network calls inside transactions hold connections and locks.
- `readOnly` is a performance/provider hint, not a universal write prohibition.
- Transaction propagation changes whether a method joins, suspends or creates a transaction.
- Isolation level does not solve external side effects or duplicate messages.

Always state the database and transaction manager when discussing exact behaviour.

# Appendix A10. JPA Entity and API Separation

Avoid returning entities directly from controllers:

```text
Database entity
 -> persistence mapping and relationships

API DTO
 -> public contract and serialization

Domain model
 -> business invariants and use cases
```

This prevents accidental lazy-loading during serialization, sensitive-field exposure and API contracts coupled to schema migrations.

Use projections for read-heavy endpoints:

```java
public record OrderSummary(
long id,
BigDecimal amount,
String status) {
}
```

Read only what the endpoint needs. A `findAll()` that materializes a large object graph is rarely a production query design.

# Appendix A11. JPA Fetching and N+1 Prevention

Inspect generated SQL for every non-trivial repository method. A method that loads orders and then accesses customer or item relationships can issue:

```text
1 query for orders
N queries for customers/items
```

Use one of these deliberately:

```text
DTO projection
Controlled fetch join
EntityGraph
Batch fetching
Separate bounded queries
JdbcTemplate for tuned SQL
```

Do not solve N+1 by making every relationship eager. That may replace many small queries with a huge join, duplicate rows and memory pressure.

# Appendix A12. Optimistic and Pessimistic Concurrency

Optimistic locking is a good fit when conflicts are uncommon:

```java
@Version
private long version;
```

The update succeeds only if the version still matches. Return a clear conflict response or retry according to business rules.

Pessimistic locking can serialize access:

```sql
SELECT *
FROM inventory
WHERE product_id = :productId
FOR UPDATE;
```

Use it only inside a short, well-understood transaction. It can create lock waits and deadlocks under load.

# Appendix A13. Resilience for Outbound HTTP Calls

Configure each client with:

```text
Connection timeout
Response timeout
Total deadline
Bounded retries
Exponential backoff with jitter
Circuit breaker
Bulkhead/concurrency limit
```

Retry only operations that are safe to repeat or have an idempotency key. A retrying client can amplify an outage:

```text
100 requests
 x 3 client retries
 x 2 gateway retries
 = 600 downstream attempts
```

Use Resilience4j or an established platform pattern where appropriate, and monitor the rejected, timed-out and fallback paths.

# Appendix A14. Messaging and Idempotency

For SQS, Kafka or other at-least-once systems:

```text
Receive event
 -> validate envelope
 -> claim durable idempotency key
 -> perform business operation
 -> record outcome
 -> acknowledge/commit offset
```

Do not rely on a static Java collection for durable deduplication. Use a unique database constraint, DynamoDB conditional write or another durable idempotency store.

If the handler performs a database write and publishes an event, use a transactional outbox or a technology-specific atomic integration pattern.

# Appendix A15. Spring Security Production Model

Separate authentication from authorization:

```text
Authentication -> Who is calling?
Authorization -> What may this identity do to this resource?
```

For a resource server, validate issuer, signature, audience, expiry and required scopes/roles. Then enforce ownership or tenant access in the application:

```java
@PreAuthorize("@orderSecurity.canRead(#orderId, authentication)")
public OrderResponse get(long orderId) {
 return service.get(orderId);
}
```

Do not trust a customer ID supplied only in the request body. Do not expose actuator or management endpoints without an explicit security policy.

# Appendix A16. CSRF, CORS and Browser Boundaries

The correct configuration depends on whether the application is:

```text
Browser session application
 -> cookie authentication and CSRF considerations

Bearer-token API
 -> token validation, CORS and browser storage considerations

Internal service
 -> service identity, mTLS or workload identity as required
```

Do not disable CSRF or allow every CORS origin as a reflex. Document the client trust model and test preflight, credentials and rejected origins.

# Appendix A17. Actuator and Observability

A production baseline should include:

```text
Structured logs
Request/trace correlation
Micrometer metrics
Health and readiness
JVM and pool metrics
Outbound client timing
Database query timing/fingerprints
Distributed traces
Business metrics
```

Expose only the actuator endpoints required by operators. Send logs to a central system and set retention according to operational, privacy and cost requirements.

Use OpenTelemetry or the organization's supported tracing standard consistently across HTTP, messaging and database boundaries. A trace without correlation across asynchronous work is incomplete operational evidence.

# Appendix A18. Graceful Shutdown

When a container or pod is terminated:

```text
Stop receiving new traffic
 -> stop polling new messages
 -> finish bounded in-flight work
 -> close connections and executors
 -> exit before the termination deadline
```

Configure Spring Boot graceful shutdown and align it with:

- Load-balancer deregistration delay.
- Kubernetes termination grace period.
- ECS stop timeout.
- Worker visibility timeout.
- Client request deadlines.

Test shutdown during an active request and active message processing. A clean application exit is part of reliability, not merely housekeeping.

# Appendix A19. Virtual Threads and Concurrency

Virtual threads can improve throughput for blocking I/O workloads when the application and libraries support the model, but they do not create unlimited database capacity.

```text
More concurrent request tasks
 |
 v
Database connection pool remains bounded
 |
 v
Backpressure is still required
```

Measure CPU, memory, connection-pool wait, downstream rate limits and latency. Do not remove concurrency limits simply because thread creation is cheaper.

# Appendix A20. Startup and Runtime Performance

Investigate startup and request latency separately:

```text
Startup
 -> classpath and dependency size
 -> auto-configuration
 -> bean initialization
 -> database/client initialization

Runtime
 -> request mapping
 -> validation
 -> business logic
 -> SQL/HTTP calls
 -> serialization
```

Use startup metrics and profiling to find slow beans. Do not disable auto-configuration blindly; remove unused dependencies and narrow component scanning where justified.

# Appendix A21. Native Images and AOT

Native compilation or AOT processing can reduce startup and memory usage for suitable workloads, but it introduces compatibility work for reflection, proxies, resource loading and dynamic frameworks.

Evaluate:

```text
Startup requirement
Memory footprint
Build complexity
Debugging and profiling
Reflection/runtime hints
Library compatibility
Deployment target
```

Use measured benchmarks rather than assuming native is faster for every endpoint or cheaper for every traffic pattern.

# Appendix A22. Container and Deployment Baseline

For a Spring Boot container:

- Use a supported Java runtime.
- Run as a non-root user where possible.
- Use an immutable image digest.
- Scan dependencies and generate an SBOM.
- Externalize configuration and secrets.
- Emit logs to stdout/stderr.
- Set memory and CPU requests/limits from measurements.
- Configure graceful shutdown.
- Add readiness, liveness and startup checks.
- Use progressive rollout and automated rollback.

The same application image should move through environments. Environment-specific values belong in deployment configuration, not in rebuilt binaries.

# Appendix A23. Database Connection Capacity

Connection pools multiply across replicas:

```text
20 pods x 10 connections = 200 possible connections
```

Include every service, migration job, admin tool, read replica and failover path in the capacity calculation. A large pool does not make a slow database faster; it can make overload worse.

Monitor:

```text
Pool active
Pool idle
Pool pending/wait time
Connection creation failures
Database lock waits
Query duration
```

Keep remote calls outside transactions and bound all query timeouts.

# Appendix A24. API Versioning and Compatibility

Use additive, backward-compatible changes when possible:

```text
Consumer supports old and new fields
 -> deploy consumer
 -> deploy producer
 -> deprecate old field
 -> remove after usage is gone
```

Do not rename a response field or change enum meaning without a compatibility plan. Contract tests and OpenAPI review should be part of CI/CD.

# Appendix A25. Testing Pyramid for Spring Boot

```text
Many fast unit tests
 |
Component tests with Spring slices
 |
Integration tests with real dependencies
 |
Few end-to-end tests
```

Useful tools and patterns include:

- `@WebMvcTest` for MVC boundaries.
- `@DataJpaTest` for repository behaviour.
- `@SpringBootTest` for selected full-context tests.
- Testcontainers for the same database or broker family.
- WireMock or MockWebServer for outbound HTTP contracts.
- Contract tests for service and event schemas.
- Test data builders that make edge cases explicit.

Avoid making every test a full application-context test. Also avoid using only mocks for SQL, serialization, security filters and transaction behaviour.

# Appendix A26. Test Scenarios That Find Production Bugs

Test:

```text
Duplicate request with same idempotency key
Concurrent update of one resource
Database timeout during a transaction
Deadlock victim and bounded retry
Remote service timeout and rate limit
Malformed message and poison message
Read-after-write against a replica
Missing secret or invalid configuration
Expired token and insufficient scope
Graceful shutdown during active work
Rollback during a progressive deployment
```

The purpose is not only to prove the happy path. It is to prove that failure produces a safe, observable and recoverable result.

# Appendix A27. CI/CD Quality Gates

A production pipeline can include:

```text
Compile
 -> unit tests
 -> static analysis
 -> dependency/CVE scan
 -> integration tests
 -> contract tests
 -> container build and scan
 -> SBOM/attestation
 -> deploy to test
 -> smoke test
 -> progressive production rollout
 -> monitor and approve
 Keep migrations, application compatibility and rollback strategy in the same release discussion. A green build does not prove a safe database migration or a correct authorization policy.

# Appendix A28. Common Senior-Level Mistakes

- Treating auto-configuration as magic instead of understanding conditions.
- Using field injection for required dependencies.
- Returning JPA entities directly from APIs.
- Catching all exceptions and returning HTTP 200.
- Retrying non-idempotent operations without a key.
- Calling remote services inside long database transactions.
- Using `ddl-auto=update` as a production migration strategy.
- Exposing all actuator endpoints publicly.
- Logging secrets, tokens or complete request bodies.
- Creating a huge connection pool for every replica.
- Making every relationship eager to hide N+1.
- Assuming `@Transactional` spans remote services.
- Scaling application replicas without checking database and provider capacity.
- Deploying a breaking API or schema change without compatibility.

# Appendix A29. Senior Interview Answer: What Happens at Startup?

> Spring Boot creates a `SpringApplication`, determines the application type, prepares the environment and property sources, creates the application context, registers configuration and auto-configuration candidates, scans and registers beans, applies conditions and post-processors, creates singleton beans with dependency injection, starts the embedded web server, publishes lifecycle events and finally marks the application ready. For troubleshooting I use debug condition reports, startup metrics, bean inspection, configuration review and targeted profiling rather than guessing which annotation failed.

# Appendix A30. Senior Interview Answer: How Do You Design a Production API?

> I keep controllers thin and put use-case orchestration in an application service. Requests are validated at the boundary, authorization is checked against the authenticated identity and resource, and response DTOs are separated from persistence entities. I define timeouts and idempotency for commands, use a transaction only around the local consistency boundary, and use an outbox or workflow for external side effects. The service exposes safe health and readiness signals, structured logs, metrics and traces. Database access is tested with the target engine, query plans are reviewed for important paths, and deployment uses immutable artifacts, compatible migrations, progressive rollout and rollback.