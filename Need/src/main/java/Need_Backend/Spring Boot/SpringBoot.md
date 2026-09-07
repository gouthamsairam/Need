**Section 1: Why Spring Boot?Q1. What is Inversion of Control (IoC) and why do we need it?**
--------------------------------------------------------------------------------------------

**Expected Answer:In traditional programming, your code creates and manages its own dependencies:**
---------------------------------------------------------------------------------------------------

_**// WITHOUT IoC — tight coupling**_
-------------------------------------

**public class OrderService {**
-------------------------------

    **private final OrderRepository orderRepository;**
------------------------------------------------------

    **private final PaymentService paymentService;**
----------------------------------------------------

    **private final EmailService emailService;**
------------------------------------------------

    **public OrderService() {**
-------------------------------

        **this.orderRepository = new JdbcOrderRepository();**      _**// hardcoded**_
-------------------------------------------------------------------------------------

        **this.paymentService = new StripePaymentService();**       _**// hardcoded**_
--------------------------------------------------------------------------------------

        **this.emailService = new SmtpEmailService();**             _**// hardcoded**_
--------------------------------------------------------------------------------------

    **}**
---------

**}**
-----

**Problems:**
-------------

*   **OrderService is tightly coupled to specific implementations.**

*   **Can't swap JdbcOrderRepository for MongoOrderRepository without modifying OrderService.**

*   **Can't test with mocks — constructor always creates real objects.**

*   **If SmtpEmailService needs its own dependencies, OrderService must know about those too (transitive coupling).**


**With IoC — the container controls object creation and injection:**
--------------------------------------------------------------------

_**// WITH IoC — loose coupling**_
----------------------------------

**@Service**
------------

**public class OrderService {**
-------------------------------

    **private final OrderRepository orderRepository;**
------------------------------------------------------

    **private final PaymentService paymentService;**
----------------------------------------------------

    **private final EmailService emailService;**
------------------------------------------------

    _**// Dependencies are INJECTED by the container — OrderService doesn't create anything**_
----------------------------------------------------------------------------------------------

    **public OrderService(OrderRepository orderRepository,**
------------------------------------------------------------

                        **PaymentService paymentService,**
----------------------------------------------------------

                        **EmailService emailService) {**
--------------------------------------------------------

        **this.orderRepository = orderRepository;**
---------------------------------------------------

        **this.paymentService = paymentService;**
-------------------------------------------------

        **this.emailService = emailService;**
---------------------------------------------

    **}**
---------

**}**
-----

**IoC means: The control of creating and wiring dependencies is inverted — it moves from the class itself to an external container (the Spring IoC container).Benefits:**
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

*   **Loose coupling — Classes depend on interfaces, not implementations.**

*   **Testability — Inject mocks easily.**

*   **Configurability — Swap implementations via configuration without code changes.**

*   **Lifecycle management — The container manages creation, initialization, and destruction.**


### **Q2. What is Dependency Injection (DI)? How does it relate to IoC?**

**Expected Answer:IoC is the principle — "Don't create your dependencies, let someone else provide them."DI is the mechanism — How those dependencies are actually provided.IoC is the "what," DI is the "how."Three types of DI:**
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

#### **1\. Constructor Injection (Recommended)**

**@Service**
------------

**public class OrderService {**
-------------------------------

    **private final OrderRepository repository;**
-------------------------------------------------

    **public OrderService(OrderRepository repository) {**  _**// injected via constructor**_
--------------------------------------------------------------------------------------------

        **this.repository = repository;**
-----------------------------------------

    **}**
---------

**}**
-----

#### **2\. Setter Injection**

**@Service**
------------

**public class OrderService {**
-------------------------------

    **private OrderRepository repository;**
-------------------------------------------

    **@Autowired**
------------------

    **public void setRepository(OrderRepository repository) {**  _**// injected via setter**_
---------------------------------------------------------------------------------------------

        **this.repository = repository;**
-----------------------------------------

    **}**
---------

**}**
-----

#### **3\. Field Injection (Discouraged)**

**@Service**
------------

**public class OrderService {**
-------------------------------

    **@Autowired**
------------------

    **private OrderRepository repository;**  _**// injected directly into field via reflection**_
-------------------------------------------------------------------------------------------------

**}**
-----

**Comparison:**
---------------

**Criteria**
------------

**Constructor**
---------------

**Setter**
----------

**Field**
---------

**Immutability (final)**
------------------------

**Yes**
-------

**No**
------

**No**
------

**Required dependencies**
-------------------------

**Enforced (won't compile without)**
------------------------------------

**Optional (can be null)**
--------------------------

**Optional**
------------

**Testability**
---------------

**Easy (pass mocks in constructor)**
------------------------------------

**Possible**
------------

**Hard (requires reflection or Spring context)**
------------------------------------------------

**Circular dependency detection**
---------------------------------

**Fails fast at startup**
-------------------------

**Allowed (risky)**
-------------------

**Allowed (risky)**
-------------------

**Readability**
---------------

**Clear — all dependencies visible in constructor**
---------------------------------------------------

**Scattered**
-------------

**Hidden**
----------

### **Q3. What is the IoC Container in Spring? What are its types?**

**Expected Answer:The Spring IoC Container is the core of the framework. It is responsible for:**
-------------------------------------------------------------------------------------------------

1.  **Instantiating beans.**

2.  **Configuring them (injecting dependencies, setting properties).**

3.  **Managing their lifecycle (initialization, destruction).**


**Two types of IoC containers:**
--------------------------------

                  **IoC Container**
-----------------------------------

                       **│**
----------------------------

          **┌────────────┴────────────┐**
-----------------------------------------

          **│                         │**
-----------------------------------------

    **BeanFactory              ApplicationContext**
---------------------------------------------------

    **(basic)                  (feature-rich, extends BeanFactory)**
--------------------------------------------------------------------

**Aspect**
----------

**BeanFactory**
---------------

**ApplicationContext**
----------------------

**Bean loading**
----------------

**Lazy — creates beans only when requested**
--------------------------------------------

**Eager — creates all singleton beans at startup**
--------------------------------------------------

**Event publishing**
--------------------

**No**
------

**Yes (ApplicationEventPublisher)**
-----------------------------------

**Internationalization (i18n)**
-------------------------------

**No**
------

**Yes (MessageSource)**
-----------------------

**AOP support**
---------------

**Manual**
----------

**Built-in**
------------

**Resource loading**
--------------------

**Basic**
---------

**Rich (ResourceLoader)**
-------------------------

**Environment/profiles**
------------------------

**No**
------

**Yes (Environment abstraction)**
---------------------------------

**Annotation processing**
-------------------------

**Minimal**
-----------

**Full (@Autowired, @PostConstruct, etc.)**
-------------------------------------------

**Use case**
------------

**Lightweight/embedded/memory-constrained**
-------------------------------------------

**All real-world applications**
-------------------------------

**In practice: You almost always use ApplicationContext. BeanFactory is the low-level interface that ApplicationContext extends.Key behavioral difference:**
------------------------------------------------------------------------------------------------------------------------------------------------------------

_**// BeanFactory — lazy loading**_
-----------------------------------

**BeanFactory factory = new XmlBeanFactory(new ClassPathResource("beans.xml"));**
---------------------------------------------------------------------------------

_**// No beans created yet!**_
------------------------------

**OrderService service = factory.getBean(OrderService.class);**
---------------------------------------------------------------

_**// Bean created NOW, when first requested**_
-----------------------------------------------

_**// ApplicationContext — eager loading**_
-------------------------------------------

**ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");**
---------------------------------------------------------------------------------

_**// ALL singleton beans are created immediately during context initialization**_
----------------------------------------------------------------------------------

_**// Errors surface at startup, not at runtime (fail fast)**_
--------------------------------------------------------------

**Why eager loading matters: If you have a misconfigured bean (wrong dependency, missing property), ApplicationContext fails at startup — you discover the problem immediately. BeanFactory would fail only when that specific bean is first requested at runtime, potentially in production. What are the different bean scopes? When would you use each?**
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

**Expected Answer:**
--------------------

**Scope**
---------

**Instances**
-------------

**Lifecycle**
-------------

**Use Case**
------------

**singleton (default)**
-----------------------

**One per ApplicationContext**
------------------------------

**From context start to shutdown**
----------------------------------

**Stateless services, repositories, controllers**
-------------------------------------------------

**prototype**
-------------

**New instance every time requested**
-------------------------------------

**Created on demand, not destroyed by Spring**
----------------------------------------------

**Stateful objects, builders, request-specific logic**
------------------------------------------------------

**request**
-----------

**One per HTTP request**
------------------------

**Created at request start, destroyed at request end**
------------------------------------------------------

**Request-scoped data (audit info, request context)**
-----------------------------------------------------

**session**
-----------

**One per HTTP session**
------------------------

**Created at session start, destroyed at session end**
------------------------------------------------------

**Shopping cart, user preferences**
-----------------------------------

**application**
---------------

**One per ServletContext**
--------------------------

**Application lifetime**
------------------------

**Shared counters, global config**
----------------------------------

**websocket**
-------------

**One per WebSocket session**
-----------------------------

**WebSocket session lifetime**
------------------------------

**WebSocket-specific state**
----------------------------

**@Component**
--------------

**@Scope("prototype")**
-----------------------

**public class ReportGenerator {**
----------------------------------

    **private final List data = new ArrayList<>();**
----------------------------------------------------

    **public void addData(String row) {**
-----------------------------------------

        **data.add(row);** _**// stateful — each request needs its own instance**_
----------------------------------------------------------------------------------

    **}**
---------

    **public byte\[\] generate() {**
------------------------------------

        **return buildReport(data);**
-------------------------------------

    **}**
---------

**}**
-----

**Prototype scope pitfall with singleton injection:**
-----------------------------------------------------

**@Service** _**// singleton**_
-------------------------------

**public class OrderService {**
-------------------------------

    **@Autowired**
------------------

    **private ReportGenerator reportGenerator;** _**// prototype — BUT this is injected ONCE!**_
------------------------------------------------------------------------------------------------

    _**// Every call uses the SAME instance — prototype scope is defeated!**_
-----------------------------------------------------------------------------

**}**
-----

**Fix — Use ObjectFactory or Provider:**
----------------------------------------

**@Service**
------------

**public class OrderService {**
-------------------------------

    **@Autowired**
------------------

    **private ObjectFactory reportGeneratorFactory;**
-----------------------------------------------------

    **public byte\[\] generateReport() {**
------------------------------------------

        **ReportGenerator generator = reportGeneratorFactory.getObject();** _**// new instance each time**_
-----------------------------------------------------------------------------------------------------------

        **generator.addData("...");**
-------------------------------------

        **return generator.generate();**
----------------------------------------

    **}**
---------

**}**
-----

### **Q10. What is a FactoryBean? How is it different from BeanFactory?**

**Expected Answer:These are completely different despite the similar names:**
-----------------------------------------------------------------------------

**BeanFactory**
---------------

**FactoryBean**
---------------

**What**
--------

**The IoC container itself**
----------------------------

**A special bean that produces other beans**
--------------------------------------------

**Role**
--------

**Manages all beans**
---------------------

**A factory for creating complex beans**
----------------------------------------

**FactoryBean is used when creating a bean requires complex logic that can't be expressed with simple constructor/setter injection:**
-------------------------------------------------------------------------------------------------------------------------------------

**@Component**
--------------

**public class HttpClientFactoryBean implements FactoryBean {**
---------------------------------------------------------------

    **@Value("${http.connection.timeout:5000}")**
-------------------------------------------------

    **private int connectionTimeout;**
--------------------------------------

    **@Value("${http.max.connections:100}")**
---------------------------------------------

    **private int maxConnections;**
-----------------------------------

    **@Override**
-----------------

    **public CloseableHttpClient getObject() {**
------------------------------------------------

        **return HttpClients.custom()**
---------------------------------------

                **.setMaxConnTotal(maxConnections)**
----------------------------------------------------

                **.setDefaultRequestConfig(RequestConfig.custom()**
-------------------------------------------------------------------

                    **.setConnectTimeout(Timeout.ofMilliseconds(connectionTimeout))**
-------------------------------------------------------------------------------------

                    **.build())**
---------------------------------

                **.setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()**
--------------------------------------------------------------------------------------------

                    **.setMaxConnTotal(maxConnections)**
--------------------------------------------------------

                    **.build())**
---------------------------------

                **.build();**
-----------------------------

    **}**
---------

    **@Override**
-----------------

    **public Class getObjectType() {**
--------------------------------------

        **return CloseableHttpClient.class;**
---------------------------------------------

    **}**
---------

    **@Override**
-----------------

    **public boolean isSingleton() {**
--------------------------------------

        **return true;**
------------------------

    **}**
---------

**}**
-----

**Now you can inject CloseableHttpClient directly — Spring calls the FactoryBean to create it:**
------------------------------------------------------------------------------------------------

**@Autowired**
--------------

**private CloseableHttpClient httpClient;** _**// produced by HttpClientFactoryBean**_
--------------------------------------------------------------------------------------

 **Real Spring examples: SqlSessionFactoryBean (MyBatis), LocalContainerEntityManagerFactoryBean (JPA), ProxyFactoryBean (AOP).**
---------------------------------------------------------------------------------------------------------------------------------

### **Q1. What is Spring Boot and why do we need it when we already have the Spring Framework?**

**Expected Answer:Spring Framework is powerful but requires heavy configuration — XML files, bean definitions, dependency wiring, servlet configuration, and boilerplate setup. Spring Boot is an opinionated layer on top of Spring that eliminates this ceremony.**

**Problem with Raw Spring**

**How Spring Boot Solves It**

**Extensive XML/Java configuration**

**Auto-configuration — sensible defaults applied automatically**

**Manually managing dependency versions**

**Starter POMs — curated, compatible dependency sets**

**External server setup (Tomcat, Jetty)**

**Embedded server — runs as a standalone JAR**

**Complex project setup**

**Spring Initializr — generate a project in seconds**

**Manual health/metrics endpoints**

**Actuator — production-ready monitoring out of the box**

**In short: Spring Boot lets you focus on business logic instead of infrastructure plumbing.**

### **Q2. What are the key features of Spring Boot?**

**Expected Answer:**

1.  **Auto-Configuration — Automatically configures beans based on classpath, properties, and existing beans.**

2.  **Starter Dependencies — Pre-packaged dependency descriptors (spring-boot-starter-web, spring-boot-starter-data-jpa, etc.).**

3.  **Embedded Server — Tomcat, Jetty, or Undertow embedded in the JAR — no WAR deployment needed.**

4.  **Spring Boot Actuator — Health checks, metrics, environment info, HTTP trace.**

5.  **Spring Boot DevTools — Auto-restart, live reload during development.**

6.  **Externalized Configuration — application.properties/application.yml, profiles, environment variables.**

7.  **Production-Ready — Logging, security defaults, graceful shutdown.**

8.  **No Code Generation, No XML — Pure Java configuration.**


### **Q3. What is the difference between Spring, Spring Boot, and Spring MVC?**

**Expected Answer:**

**Aspect**

**Spring (Framework)**

**Spring MVC**

**Spring Boot**

**What**

**Core IoC/DI container + ecosystem**

**Web MVC framework (part of Spring)**

**Opinionated auto-config layer on top of Spring**

**Purpose**

**Dependency injection, AOP, data access, etc.**

**Build web apps with Model-View-Controller**

**Rapid application development with minimal config**

**Configuration**

**Manual (XML or Java)**

**Manual DispatcherServlet setup, view resolvers**

**Auto-configured**

**Server**

**External (deploy WAR to Tomcat)**

**External**

**Embedded**

**Standalone**

**No**

**No**

**Yes — runs as java -jar app.jar**

**Spring Boot uses Spring Framework and can include Spring MVC. They're not alternatives — they're layers.**

**Section 2: How Spring Boot Works Internally**
-----------------------------------------------

### **Q4. What happens when you run a Spring Boot application? Explain the startup process.**

**Expected Answer:When you call SpringApplication.run(MyApp.class, args):**

**1\. SpringApplication instance created**

**2\. Determine application type (SERVLET, REACTIVE, NONE)**

**3\. Load SpringApplicationRunListeners (publish events)**

**4\. Prepare Environment (read application.properties, env vars, CLI args)**

**5\. Create ApplicationContext (AnnotationConfigServletWebServerApplicationContext)**

**6\. Load bean definitions (component scanning, @Configuration classes)**

**7\. Run Auto-Configuration (@EnableAutoConfiguration)**

**8\. Refresh context (instantiate all singleton beans, dependency injection)**

**9\. Start embedded web server (Tomcat/Jetty)**

**10\. Call ApplicationRunner / CommandLineRunner beans**

**11\. Application is ready — publish ApplicationReadyEvent**

**Key lifecycle events:**

*   **ApplicationStartingEvent — Before anything happens.**

*   **ApplicationEnvironmentPreparedEvent — Environment ready, context not created yet.**

*   **ApplicationContextInitializedEvent — Context created, beans not loaded.**

*   **ApplicationPreparedEvent — Beans loaded, not refreshed.**

*   **ApplicationStartedEvent — Context refreshed, runners not called.**

*   **ApplicationReadyEvent — Everything is ready.**

*   **ApplicationFailedEvent — Startup failed.**


### **Q5. What is Auto-Configuration? How does it work internally?**

**Expected Answer:Auto-configuration is Spring Boot's mechanism to automatically configure beans based on what's on the classpath, what beans already exist, and what properties are set.How it works:**

1.  **@SpringBootApplication includes @EnableAutoConfiguration.**

2.  **@EnableAutoConfiguration imports AutoConfigurationImportSelector.**

3.  **The selector reads META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports (Spring Boot 3.x) or META-INF/spring.factories (older versions).**

4.  **This file lists hundreds of auto-configuration classes.**

5.  **Each class has conditions — it only activates if conditions are met.**


**Example — DataSourceAutoConfiguration:**

**@AutoConfiguration**

**@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })**

**@ConditionalOnMissingBean(type = "io.r2dbc.spi.ConnectionFactory")**

**@EnableConfigurationProperties(DataSourceProperties.class)**

**public class DataSourceAutoConfiguration {**

**@Bean**

**@ConditionalOnMissingBean**

**public DataSource dataSource(DataSourceProperties properties) {**

**return properties.initializeDataSourceBuilder().build();**

**}**

**}**

**This means:**

*   **Only activates if DataSource class is on the classpath (i.e., you added a JDBC dependency).**

*   **Only creates a DataSource bean if you haven't defined one yourself (@ConditionalOnMissingBean).**


**To see what was auto-configured: Run with --debug or check /actuator/conditions.**

### **Q6. What are the key @Conditional annotations used in auto-configuration?**

**Expected Answer:**

**Annotation**

**Condition**

**@ConditionalOnClass**

**Class is on the classpath**

**@ConditionalOnMissingClass**

**Class is NOT on the classpath**

**@ConditionalOnBean**

**A specific bean exists in the context**

**@ConditionalOnMissingBean**

**A specific bean does NOT exist**

**@ConditionalOnProperty**

**A property has a specific value**

**@ConditionalOnResource**

**A resource (file) exists on the classpath**

**@ConditionalOnWebApplication**

**App is a web application**

**@ConditionalOnExpression**

**A SpEL expression evaluates to true**

**Example:**

**@Bean**

**@ConditionalOnProperty(name = "feature.notifications.enabled", havingValue = "true")**

**public NotificationService notificationService() {**

**return new EmailNotificationService();**

**}**

**This bean is only created if feature.notifications.enabled=true is in your properties.**

### **Q7. What is the role of @SpringBootApplication? What does it combine?**

**Expected Answer:@SpringBootApplication is a meta-annotation that combines three annotations:**

**@Target(ElementType.TYPE)**

**@Retention(RetentionPolicy.RUNTIME)**

**@SpringBootConfiguration**   _**// equivalent to @Configuration**_

**@EnableAutoConfiguration**   _**// triggers auto-configuration**_

**@ComponentScan**             _**// scans current package and sub-packages**_

**public @interface SpringBootApplication { }**

**Annotation**

**Purpose**

**@SpringBootConfiguration**

**Marks this as a configuration class (extends @Configuration)**

**@EnableAutoConfiguration**

**Activates auto-configuration based on classpath**

**@ComponentScan**

**Scans for @Component, @Service, @Repository, @Controller in the same package and below**

**Important: The main class should be in the root package so that @ComponentScan finds all sub-package components.**

**com.example.myapp**

**├── MyApplication.java        ← @SpringBootApplication here**

**├── controller/**

**│   └── OrderController.java  ← found by @ComponentScan**

**├── service/**

**│   └── OrderService.java     ← found by @ComponentScan**

**└── repository/**

**└── OrderRepository.java  ← found by @ComponentScan**

### **Q8. What is the difference between application.properties and application.yml? What is the property loading order?**

**Expected Answer:Both serve the same purpose — externalized configuration. The difference is format:**

**\# application.properties**

**server.port=8080**

**spring.datasource.url=jdbc:mysql://localhost:3306/mydb**

**spring.datasource.username=root**

_**\# application.yml**_

**server:**

**port: 8080**

**spring:**

**datasource:**

**url: jdbc:mysql://localhost:3306/mydb**

**username: root**

**YAML advantages: Hierarchical structure, less repetition, supports lists naturally.Properties advantages: Simpler, no indentation sensitivity.Property source priority (highest to lowest):**

1.  **Command-line arguments (--server.port=9090)**

2.  **SPRING\_APPLICATION\_JSON (inline JSON env var)**

3.  **Servlet init parameters**

4.  **JNDI attributes**

5.  **Java system properties (-Dserver.port=9090)**

6.  **OS environment variables (SERVER\_PORT=9090)**

7.  **Profile-specific properties (application-dev.properties)**

8.  **Application properties (application.properties / application.yml)**

9.  **@PropertySource on @Configuration classes**

10.  **Default properties (SpringApplication.setDefaultProperties(...))**


### **Q9. What are Spring Boot Profiles and how do you use them?**

**Expected Answer:Profiles allow you to define environment-specific configurations — different database URLs, logging levels, feature flags for dev/staging/prod.Define profile-specific files:**

**application.properties        ← common config**

**application-dev.properties     ← dev overrides**

**application-staging.properties ← staging overrides**

**application-prod.properties    ← prod overrides**

**Activate a profile:**

**\# In application.properties**

**spring.profiles.active=dev**

_**\# Command line**_

**java -jar app.jar --spring.profiles.active=prod**

_**\# Environment variable**_

**SPRING\_PROFILES\_ACTIVE=prod**

**Conditional beans by profile:**

**@Configuration**

**@Profile("dev")**

**public class DevConfig {**

**@Bean**

**public DataSource dataSource() {**

**return new H2DataSource();** _**// in-memory DB for dev**_

**}**

**}**

**@Configuration**

**@Profile("prod")**

**public class ProdConfig {**

**@Bean**

**public DataSource dataSource() {**

**return new HikariDataSource();** _**// production connection pool**_

**}**

**}**

**Multiple profiles: spring.profiles.active=prod,metrics — both activate.**

**PART B: SPRING BOOT ANNOTATIONS — COMPREHENSIVE GUIDE**
---------------------------------------------------------

**Section 3: Core / Stereotype Annotations**
--------------------------------------------

### **Q10. Explain all the stereotype annotations and their differences.**

**Expected Answer:**

**Annotation**

**Purpose**

**Layer**

**@Component**

**Generic Spring-managed bean**

**Any**

**@Service**

**Business logic bean**

**Service layer**

**@Repository**

**Data access bean**

**Persistence layer**

**@Controller**

**Web controller (returns views)**

**Web layer**

**@RestController**

**REST controller (returns JSON/XML)**

**Web layer**

**@Configuration**

**Class that defines @Bean methods**

**Configuration**

**Key point: @Service, @Repository, @Controller are all specializations of @Component. Functionally, Spring treats them the same for component scanning. The differences are:**

*   **Semantic clarity — Makes code intent obvious.**

*   **@Repository additionally enables automatic exception translation — SQL exceptions are converted to Spring's DataAccessException hierarchy.**

*   **@Controller enables Spring MVC handler mapping.**

*   **@RestController = @Controller + @ResponseBody.**


**@Target(ElementType.TYPE)**

**@Retention(RetentionPolicy.RUNTIME)**

**@Component**  _**// ← it IS a component**_

**public @interface Service { }**

### **Q11. What is the difference between @Component and @Bean?**

**Expected Answer:**

**Aspect**

**@Component**

**@Bean**

**Target**

**Class-level**

**Method-level (inside @Configuration)**

**Detection**

**Auto-detected via @ComponentScan**

**Explicitly declared**

**Control**

**You must own the class**

**Can create beans from third-party classes you don't own**

**Customization**

**Limited — constructor/setter injection**

**Full control — call any constructor, set any property**

**When to use @Bean:When you need to create a bean from a class you can't annotate (third-party library):**

**@Configuration**

**public class AppConfig {**

**@Bean**

**public RestTemplate restTemplate() {**

**return new RestTemplateBuilder()**

**.setConnectTimeout(Duration.ofSeconds(5))**

**.setReadTimeout(Duration.ofSeconds(10))**

**.build();**

**}**

**@Bean**

**public ObjectMapper objectMapper() {**

**ObjectMapper mapper = new ObjectMapper();**

**mapper.registerModule(new JavaTimeModule());**

**mapper.disable(SerializationFeature.WRITE\_DATES\_AS\_TIMESTAMPS);**

**return mapper;**

**}**

**}**

**You can't put @Component on RestTemplate or ObjectMapper — they're third-party classes.**

### **Q12. What is @Configuration and how does it differ from @Component internally?**

**Expected Answer:@Configuration is a special @Component that uses CGLIB proxying to ensure @Bean methods return singleton instances.**

**@Configuration**

**public class AppConfig {**

**@Bean**

**public ServiceA serviceA() {**

**return new ServiceA(commonDependency());** _**// calls commonDependency()**_

**}**

**@Bean**

**public ServiceB serviceB() {**

**return new ServiceB(commonDependency());** _**// calls commonDependency() again**_

**}**

**@Bean**

**public CommonDependency commonDependency() {**

**return new CommonDependency();**

**}**

**}**

**With @Configuration: commonDependency() is called once — both ServiceA and ServiceB get the same instance (CGLIB intercepts the second call and returns the cached bean).With @Component: commonDependency() would be called twice, creating two separate instances — breaking the singleton contract.This is called full mode (@Configuration) vs. lite mode (@Component).**

**Section 4: Dependency Injection Annotations**
-----------------------------------------------

### **Q13. Compare @Autowired, @Inject, @Resource, and constructor injection. Which is preferred?**

**Expected Answer:**

**Annotation**

**Source**

**Lookup Strategy**

**@Autowired**

**Spring**

**By type first, then by name if ambiguous**

**@Inject**

**JSR-330 (Java standard)**

**By type (equivalent to @Autowired)**

**@Resource**

**JSR-250 (Java standard)**

**By name first, then by type**

**Constructor injection (preferred):**

**@Service**

**public class OrderService {**

**private final OrderRepository orderRepository;**

**private final PaymentService paymentService;**

_**// @Autowired is optional when there's only one constructor (Spring 4.3+)**_

**public OrderService(OrderRepository orderRepository, PaymentService paymentService) {**

**this.orderRepository = orderRepository;**

**this.paymentService = paymentService;**

**}**

**}**

**Why constructor injection is preferred:**

1.  **Immutability — Fields can be final.**

2.  **Required dependencies — Cannot create the object with null dependencies.**

3.  **Testability — Easy to pass mocks in unit tests without Spring.**

4.  **No reflection — Unlike field injection, no reflection magic.**

5.  **Fail fast — App won't start if a dependency is missing.**


**Field injection (discouraged):**

**@Service**

**public class OrderService {**

**@Autowired**  _**// discouraged**_

**private OrderRepository orderRepository;**

**}**

**Problems: Can't be final, harder to test, hides dependencies, allows optional nulls.**

### **Q14. How do you resolve ambiguity when multiple beans of the same type exist?**

**Expected Answer:**

**public interface NotificationService { void send(String message); }**

**@Service**

**public class EmailNotificationService implements NotificationService { ... }**

**@Service**

**public class SmsNotificationService implements NotificationService { ... }**

**Now @Autowired NotificationService fails — two candidates.Solution 1: @Primary — Marks the default:**

**@Service**

**@Primary**

**public class EmailNotificationService implements NotificationService { ... }**

**Solution 2: @Qualifier — Specifies which one:**

**@Service**

**public class OrderService {**

**public OrderService(@Qualifier("smsNotificationService") NotificationService service) {**

**this.notificationService = service;**

**}**

**}**

**Solution 3: @Resource(name = "...") — Looks up by bean name:**

**@Resource(name = "smsNotificationService")**

**private NotificationService notificationService;**

**Solution 4: Inject all implementations:**

**public OrderService(List services) {**

_**// receives both EmailNotificationService and SmsNotificationService**_

**}**

### **Q15. What are @Value and @ConfigurationProperties? When do you use each?**

**Expected Answer:@Value — Inject individual properties:**

**@Service**

**public class PaymentService {**

**@Value("${payment.gateway.url}")**

**private String gatewayUrl;**

**@Value("${payment.timeout:5000}")** _**// default value if not set**_

**private int timeout;**

**@Value("${payment.retry.enabled:false}")**

**private boolean retryEnabled;**

**@Value("#{${payment.fee.rates}}")** _**// SpEL for maps**_

**private Map feeRates;**

**}**

**@ConfigurationProperties — Bind a group of related properties to a POJO:**

**@ConfigurationProperties(prefix = "payment.gateway")**

**@Validated**

**public class PaymentGatewayProperties {**

**@NotBlank**

**private String url;**

**@Min(1000) @Max(30000)**

**private int timeout = 5000;**

**private boolean retryEnabled = false;**

**private Map feeRates = new HashMap<>();**

_**// getters and setters**_

**}**

**payment:**

**gateway:**

**url: https://api.payment.com**

**timeout: 10000**

**retry-enabled: true**

**fee-rates:**

**credit: 2.5**

**debit: 1.0**

**When to use which:**

**Scenario**

**Use**

**One or two simple values**

**@Value**

**Group of related properties**

**@ConfigurationProperties**

**Need validation (@Validated)**

**@ConfigurationProperties**

**Type-safe, refactorable config**

**@ConfigurationProperties**

**SpEL expressions needed**

**@Value**

**Section 5: Web / REST Annotations**
-------------------------------------

### **Q16. Explain all the Spring MVC/REST annotations with examples.**

**Expected Answer:**

#### **Request Mapping Annotations**

**@RestController**

**@RequestMapping("/api/v1/orders")**

**public class OrderController {**

**@GetMapping**                          _**// GET /api/v1/orders**_

**public List getAllOrders() { ... }**

**@GetMapping("/{id}")**                 _**// GET /api/v1/orders/123**_

**public OrderDTO getOrder(@PathVariable Long id) { ... }**

**@GetMapping("/search")**               _**// GET /api/v1/orders/search?status=PENDING&page=0**_

**public Page search(**

**@RequestParam OrderStatus status,**

**@RequestParam(defaultValue = "0") int page) { ... }**

**@PostMapping**                         _**// POST /api/v1/orders**_

**@ResponseStatus(HttpStatus.CREATED)**

**public OrderDTO createOrder(@Valid @RequestBody CreateOrderRequest request) { ... }**

**@PutMapping("/{id}")**                 _**// PUT /api/v1/orders/123**_

**public OrderDTO updateOrder(**

**@PathVariable Long id,**

**@Valid @RequestBody UpdateOrderRequest request) { ... }**

**@PatchMapping("/{id}/status")**        _**// PATCH /api/v1/orders/123/status**_

**public OrderDTO updateStatus(**

**@PathVariable Long id,**

**@RequestBody StatusUpdateRequest request) { ... }**

**@DeleteMapping("/{id}")**              _**// DELETE /api/v1/orders/123**_

**@ResponseStatus(HttpStatus.NO\_CONTENT)**

**public void deleteOrder(@PathVariable Long id) { ... }**

**}**

#### **Parameter Annotations**

**Annotation**

**Source**

**Example**

**@PathVariable**

**URL path segment**

**/orders/{id} → @PathVariable Long id**

**@RequestParam**

**Query string**

**/orders?status=ACTIVE → @RequestParam String status**

**@RequestBody**

**HTTP body (JSON)**

**POST body → @RequestBody OrderRequest request**

**@RequestHeader**

**HTTP header**

**@RequestHeader("Authorization") String token**

**@CookieValue**

**HTTP cookie**

**@CookieValue("sessionId") String sessionId**

**@ModelAttribute**

**Form data / model**

**@ModelAttribute UserForm form**

**@MatrixVariable**

**Matrix params**

**/orders;status=ACTIVE → @MatrixVariable String status**

### **Q17. What is the difference between @Controller and @RestController?**

**Expected Answer:**

_**// @Controller — returns VIEW names (resolved by ViewResolver)**_

**@Controller**

**public class WebController {**

**@GetMapping("/home")**

**public String home(Model model) {**

**model.addAttribute("message", "Hello");**

**return "home";**  _**// resolves to home.html (Thymeleaf)**_

**}**

_**// Need @ResponseBody to return data directly**_

**@GetMapping("/api/data")**

**@ResponseBody**

**public DataDTO getData() {**

**return new DataDTO("value");**

**}**

**}**

_**// @RestController — EVERY method returns data (JSON/XML), never a view**_

**@RestController**  _**// = @Controller + @ResponseBody on every method**_

**public class ApiController {**

**@GetMapping("/api/data")**

**public DataDTO getData() {**

**return new DataDTO("value");** _**// automatically serialized to JSON**_

**}**

**}**

### **Q18. What is @Valid vs @Validated? How does validation work?**

**Expected Answer:**

**Aspect**

**@Valid**

**@Validated**

**Source**

**JSR-303 (Jakarta Bean Validation)**

**Spring-specific**

**Supports groups**

**No**

**Yes**

**Cascading**

**Yes (validates nested objects)**

**No**

**Where**

**Method parameters, fields**

**Class-level, method parameters**

**public class CreateOrderRequest {**

**@NotBlank(message = "Customer name is required")**

**private String customerName;**

**@NotNull**

**@Email**

**private String email;**

**@NotEmpty**

**@Size(min = 1, max = 100)**

**private List<@Valid OrderItemRequest> items;** _**// @Valid cascades into each item**_

**@Positive**

**private BigDecimal totalAmount;**

**@Future**

**private LocalDate deliveryDate;**

**}**

**public class OrderItemRequest {**

**@NotBlank**

**private String productId;**

**@Min(1) @Max(10000)**

**private int quantity;**

**}**

**@PostMapping**

**public OrderDTO createOrder(@Valid @RequestBody CreateOrderRequest request) {**

_**// If validation fails, MethodArgumentNotValidException is thrown**_

_**// Spring returns 400 Bad Request automatically**_

**}**

**Validation groups with @Validated:**

**public interface OnCreate {}**

**public interface OnUpdate {}**

**public class UserRequest {**

**@Null(groups = OnCreate.class)**         _**// ID must be null on create**_

**@NotNull(groups = OnUpdate.class)**      _**// ID must exist on update**_

**private Long id;**

**@NotBlank(groups = {OnCreate.class, OnUpdate.class})**

**private String name;**

**}**

**@PostMapping**

**public UserDTO create(@Validated(OnCreate.class) @RequestBody UserRequest request) { ... }**

**@PutMapping("/{id}")**

**public UserDTO update(@Validated(OnUpdate.class) @RequestBody UserRequest request) { ... }**

**Global Exception Handling in Spring Boot — Super Easy Explanation**
=====================================================================

**First, Understand the Problem**
---------------------------------

**Without Global Handler:**

**User calls API → Something goes wrong → Spring shows UGLY error**

**{**

**"timestamp": "2026-04-22T10:00:00",**

**"status": 500,**

**"error": "Internal Server Error",**

**"trace": "java.lang.NullPointerException at line 45..."  ❌ Ugly!**

**}**

**With Global Handler:**

**User calls API → Something goes wrong → YOU control the error**

**{**

**"code": "NOT\_FOUND",**

**"message": "Employee not found with id: 5"   ✅ Clean!**

**}**

**Think of it Like a Security Guard 🔒**
----------------------------------------

**Every request enters your building (app)**

**↓**

**Something goes wrong inside**

**↓**

**Instead of the building catching fire publicly...**

**↓**

**Security Guard (GlobalExceptionHandler) catches it**

**↓**

**Security Guard sends a calm, clean message to the visitor**

**Step 1 — Create Your Custom Exception**
-----------------------------------------

**This is just a custom error class you throw when something isn't found.**

**// This is just a custom error — nothing fancy**

**public class ResourceNotFoundException extends RuntimeException {**

**public ResourceNotFoundException(String resource, Long id) {**

**// Calls RuntimeException with your message**

**super(resource + " not found with id: " + id);**

**}**

**}**

### **How you USE it in your service:**

**@Service**

**public class EmployeeService {**

**public Employee getEmployee(Long id) {**

**return employeeRepository.findById(id)**

**.orElseThrow(() ->**

**// You THROW this when employee doesn't exist**

**new ResourceNotFoundException("Employee", id)**

**// Message becomes → "Employee not found with id: 5"**

**);**

**}**

**}**

**Step 2 — Create the Error Response Class**
--------------------------------------------

**This is the clean JSON you want to send back to the user.**

**// This is just a simple class to hold your error details**

**public record ErrorResponse(**

**String code,      // e.g. "NOT\_FOUND"**

**String message,   // e.g. "Employee not found with id: 5"**

**Map details  // extra info (optional)**

**) {**

**// Constructor without details (for simple errors)**

**public ErrorResponse(String code, String message) {**

**this(code, message, null);**

**}**

**}**

### **What this looks like as JSON:**

**{**

**"code": "NOT\_FOUND",**

**"message": "Employee not found with id: 5",**

**"details": null**

**}**

**Step 3 — Create the Global Exception Handler**
------------------------------------------------

**This is your Security Guard class — one place that catches ALL errors.**

**@RestControllerAdvice  // ← This makes it the GLOBAL handler**

**public class GlobalExceptionHandler {**

**// GUARD 1 — Catches ResourceNotFoundException specifically**

**@ExceptionHandler(ResourceNotFoundException.class)**

**@ResponseStatus(HttpStatus.NOT\_FOUND)  // sends 404**

**public ErrorResponse handleNotFound(ResourceNotFoundException ex) {**

**return new ErrorResponse("NOT\_FOUND", ex.getMessage());**

**// Returns → { "code": "NOT\_FOUND", "message": "Employee not found with id: 5" }**

**}**

**// GUARD 2 — Catches validation errors (@Valid failures)**

**@ExceptionHandler(MethodArgumentNotValidException.class)**

**@ResponseStatus(HttpStatus.BAD\_REQUEST)  // sends 400**

**public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {**

**Map errors = new HashMap<>();**

**// Loop through all field errors and collect them**

**ex.getBindingResult().getFieldErrors().forEach(error ->**

**errors.put(error.getField(), error.getDefaultMessage())**

**);**

**return new ErrorResponse("VALIDATION\_FAILED", "Invalid request", errors);**

**// Returns →**

**// {**

**//   "code": "VALIDATION\_FAILED",**

**//   "message": "Invalid request",**

**//   "details": {**

**//     "email": "must be a valid email",**

**//     "name": "must not be blank"**

**//   }**

**// }**

**}**

**// GUARD 3 — Catches database constraint violations**

**@ExceptionHandler(DataIntegrityViolationException.class)**

**@ResponseStatus(HttpStatus.CONFLICT)  // sends 409**

**public ErrorResponse handleConflict(DataIntegrityViolationException ex) {**

**return new ErrorResponse("CONFLICT", "Data integrity violation");**

**// Example: trying to insert duplicate email**

**}**

**// GUARD 4 — Catches EVERYTHING else (safety net)**

**@ExceptionHandler(Exception.class)**

**@ResponseStatus(HttpStatus.INTERNAL\_SERVER\_ERROR)  // sends 500**

**public ErrorResponse handleGeneral(Exception ex) {**

**log.error("Unhandled exception", ex);  // log it for debugging**

**return new ErrorResponse("INTERNAL\_ERROR", "An unexpected error occurred");**

**}**

**}**

**Step 4 — See the Full Flow**
------------------------------

**User Request: GET /api/employees/999**

**↓**

**EmployeeService runs**

**↓**

**Employee 999 doesn't exist**

**↓**

**throw new ResourceNotFoundException("Employee", 999)**

**↓**

**Exception travels up the call stack**

**↓**

**@RestControllerAdvice intercepts it  ← Security Guard catches it**

**↓**

**Finds matching @ExceptionHandler(ResourceNotFoundException.class)**

**↓**

**Returns clean JSON with 404 status**

**Response to User:**

**HTTP 404**

**{**

**"code": "NOT\_FOUND",**

**"message": "Employee not found with id: 999"**

**}**

**Step 5 — Validation Error Flow**
----------------------------------

**// Your request class with validation rules**

**public class EmployeeRequest {**

**@NotBlank(message = "Name must not be blank")**

**private String name;**

**@Email(message = "Must be a valid email")**

**private String email;**

**@Min(value = 18, message = "Age must be at least 18")**

**private int age;**

**}**

**// Your controller uses @Valid**

**@PostMapping("/employees")**

**public Employee createEmployee(@Valid @RequestBody EmployeeRequest request) {**

**return employeeService.save(request);**

**// If validation fails → MethodArgumentNotValidException is thrown**

**// GlobalExceptionHandler catches it automatically ✅**

**}**

**User sends: POST /api/employees**

**Body: { "name": "", "email": "notanemail", "age": 15 }**

**GlobalExceptionHandler catches MethodArgumentNotValidException**

**Response:**

**HTTP 400**

**{**

**"code": "VALIDATION\_FAILED",**

**"message": "Invalid request",**

**"details": {**

**"name":  "Name must not be blank",**

**"email": "Must be a valid email",**

**"age":   "Age must be at least 18"**

**}**

**}**

**The Priority Order of Guards**
--------------------------------

**Spring checks handlers top to bottom, most specific first:**

**Exception thrown**

**↓**

**Is it ResourceNotFoundException?     → Guard 1 handles it ✅**

**↓ No**

**Is it MethodArgumentNotValidException? → Guard 2 handles it ✅**

**↓ No**

**Is it DataIntegrityViolationException? → Guard 3 handles it ✅**

**↓ No**

**Is it any other Exception?            → Guard 4 handles it ✅**

**Always put Exception.class handler LAST — it is the catch-all safety net.**

**Key Annotations — Simple Meaning**
------------------------------------

**Annotation**

**What it does**

**@RestControllerAdvice**

**Makes this class the global error handler for all controllers**

**@ExceptionHandler(X.class)**

**This method handles exceptions of type X**

**@ResponseStatus(HttpStatus.NOT\_FOUND)**

**Sets HTTP status code in the response**

**Without vs With — Side by Side**
----------------------------------

**WITHOUT GlobalExceptionHandler       WITH GlobalExceptionHandler**

**─────────────────────────────        ────────────────────────────**

**HTTP 500                             HTTP 404**

**{                                    {**

**"timestamp": "...",                  "code": "NOT\_FOUND",**

**"status": 500,                       "message": "Employee not**

**"error": "Internal...",                        found with id: 5"**

**"trace": "java.lang..."  ❌        }  ✅**

**}**

**Simple One-Line Summary**
---------------------------

**@RestControllerAdvice = One class that catches all exceptions from all controllers and returns clean, consistent error responses instead of ugly stack traces.**

**Would you like me to show how to add custom exceptions for your Workforce Management project specifically — like EmployeeNotFoundException, DepartmentNotFoundException etc.?**

**Section 6: Data Access Annotations**
--------------------------------------

### **Q20. Explain JPA/Spring Data annotations with examples.**

**Expected Answer:**

#### **Entity Annotations**

**@Entity**

**@Table(name = "orders", indexes = {**

**@Index(name = "idx\_customer\_id", columnList = "customer\_id"),**

**@Index(name = "idx\_status", columnList = "status")**

**})**

**public class Order {**

**@Id**

**@GeneratedValue(strategy = GenerationType.IDENTITY)**

**private Long id;**

**@Column(name = "order\_number", unique = true, nullable = false, length = 20)**

**private String orderNumber;**

**@Enumerated(EnumType.STRING)**

**@Column(nullable = false)**

**private OrderStatus status;**

**@ManyToOne(fetch = FetchType.LAZY)**

**@JoinColumn(name = "customer\_id", nullable = false)**

**private Customer customer;**

**@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)**

**private List items = new ArrayList<>();**

**@Embedded**

**private Address shippingAddress;**

**@CreatedDate**

**@Column(updatable = false)**

**private LocalDateTime createdAt;**

**@LastModifiedDate**

**private LocalDateTime updatedAt;**

**@Version**  _**// Optimistic locking**_

**private Long version;**

**@Transient**  _**// Not persisted in DB**_

**private BigDecimal calculatedTax;**

**}**

**Annotation**

**Purpose**

**@Entity**

**Marks class as a JPA entity (DB table)**

**@Table**

**Custom table name, indexes, constraints**

**@Id**

**Primary key**

**@GeneratedValue**

**Auto-generation strategy (IDENTITY, SEQUENCE, UUID)**

**@Column**

**Column mapping, constraints**

**@Enumerated**

**Enum persistence (STRING or ORDINAL)**

**@ManyToOne / @OneToMany**

**Relationships**

**@JoinColumn**

**Foreign key column**

**@Embedded / @Embeddable**

**Value objects embedded in the entity**

**@CreatedDate / @LastModifiedDate**

**Auditing timestamps**

**@Version**

**Optimistic locking**

**@Transient**

**Field not persisted to DB**

#### **Repository Annotations**

**@Repository**

**public interface OrderRepository extends JpaRepository {**

_**// Derived query method — Spring Data generates SQL from method name**_

**List findByStatusAndCustomerId(OrderStatus status, Long customerId);**

**Optional findByOrderNumber(String orderNumber);**

**@Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt > :since")**

**List findRecentByStatus(@Param("status") OrderStatus status,**

**@Param("since") LocalDateTime since);**

**@Query(value = "SELECT \* FROM orders WHERE total > :amount", nativeQuery = true)**

**List findExpensiveOrders(@Param("amount") BigDecimal amount);**

**@Modifying**

**@Transactional**

**@Query("UPDATE Order o SET o.status = :status WHERE o.id = :id")**

**int updateStatus(@Param("id") Long id, @Param("status") OrderStatus status);**

**boolean existsByOrderNumber(String orderNumber);**

**long countByStatus(OrderStatus status);**

**}**

### **Q21. What is the difference between FetchType.LAZY and FetchType.EAGER? What is the N+1 problem?**

**Expected Answer:**

**Fetch Type**

**Behavior**

**Default For**

**LAZY**

**Related data loaded only when accessed**

**@OneToMany, @ManyToMany**

**EAGER**

**Related data loaded immediately with the parent**

**@ManyToOne, @OneToOne**

**N+1 Problem:**

**List orders = orderRepository.findAll();** _**// 1 query: SELECT \* FROM orders**_

**for (Order order : orders) {**

**order.getCustomer().getName();** _**// N queries: SELECT \* FROM customers WHERE id = ?**_

**}**

**1 query for orders + N queries for each order's customer = N+1 queries.Solutions:**

_**// 1. JOIN FETCH — single query with join**_

**@Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.status = :status")**

**List findByStatusWithCustomer(OrderStatus status);**

_**// 2. @EntityGraph — declarative fetch plan**_

**@EntityGraph(attributePaths = {"customer", "items"})**

**List findByStatus(OrderStatus status);**

_**// 3. @BatchSize — loads related entities in batches**_

**@BatchSize(size = 25)**

**@OneToMany(mappedBy = "order")**

**private List items;**

**Section 7: Transaction Annotations**
--------------------------------------

### **Q22. How does @Transactional work internally?**

**Expected Answer:@Transactional uses Spring AOP proxies to wrap the method in a transaction:**

**Caller → Proxy (TransactionInterceptor) → Actual Bean Method**

**Flow:**

1.  **Proxy intercepts the call.**

2.  **PlatformTransactionManager.getTransaction() — starts or joins a transaction.**

3.  **Actual method executes.**

4.  **If method returns normally → commit().**

5.  **If method throws a runtime exception → rollback().**

6.  **If method throws a checked exception → commit() (by default! — a common pitfall).**


**Key attributes:**

**@Transactional(**

**propagation = Propagation.REQUIRED,**     _**// default — join existing or create new**_

**isolation = Isolation.READ\_COMMITTED,**   _**// prevents dirty reads**_

**timeout = 30,**                           _**// seconds**_

**readOnly = true,**                        _**// optimization hint for read-only ops**_

**rollbackFor = BusinessException.class,**  _**// rollback on checked exceptions too**_

**noRollbackFor = EmailException.class**    _**// don't rollback for this exception**_

**)**

**public Order processOrder(OrderRequest request) { ... }**

**Propagation types:**

**Type**

**Behavior**

**REQUIRED (default)**

**Join existing txn, or create new**

**REQUIRES\_NEW**

**Always create a new txn, suspend existing**

**MANDATORY**

**Must run within existing txn, throw if none**

**SUPPORTS**

**Run in txn if one exists, otherwise non-transactional**

**NOT\_SUPPORTED**

**Suspend existing txn, run non-transactional**

**NEVER**

**Throw if a txn exists**

**NESTED**

**Execute in a nested (savepoint) txn**

**Common pitfall — self-invocation:**

**@Service**

**public class OrderService {**

**@Transactional**

**public void processOrder() { ... }**

**public void callProcess() {**

**this.processOrder();** _**// BYPASSES the proxy — NO transaction!**_

**}**

**}**

**Section 8: AOP (Aspect-Oriented Programming) Annotations**
------------------------------------------------------------

### **Q23. What is AOP in Spring and what annotations does it use?**

**Expected Answer:AOP separates cross-cutting concerns (logging, security, transactions, metrics) from business logic. Instead of scattering logging code across 100 methods, you define it once in an aspect.Key terms:**

*   **Aspect — The cross-cutting concern (e.g., logging aspect).**

*   **Advice — What to do (the code) and when (before, after, around).**

*   **Pointcut — Where to apply (which methods).**

*   **Join Point — The actual method being intercepted.**


**@Aspect**

**@Component**

**@Slf4j**

**public class LoggingAspect {**

_**// BEFORE — runs before the method**_

**@Before("execution(\* com.example.service.\*.\*(..))")**

**public void logBefore(JoinPoint joinPoint) {**

**log.info("Entering: {}.{}()",**

**joinPoint.getTarget().getClass().getSimpleName(),**

**joinPoint.getSignature().getName());**

**}**

_**// AFTER RETURNING — runs after successful completion**_

**@AfterReturning(pointcut = "execution(\* com.example.service.\*.\*(..))",**

**returning = "result")**

**public void logAfterReturning(JoinPoint joinPoint, Object result) {**

**log.info("Exiting: {}.{}() with result: {}",**

**joinPoint.getTarget().getClass().getSimpleName(),**

**joinPoint.getSignature().getName(), result);**

**}**

_**// AFTER THROWING — runs when method throws exception**_

**@AfterThrowing(pointcut = "execution(\* com.example.service.\*.\*(..))",**

**throwing = "exception")**

**public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {**

**log.error("Exception in {}.{}(): {}",**

**joinPoint.getTarget().getClass().getSimpleName(),**

**joinPoint.getSignature().getName(), exception.getMessage());**

**}**

_**// AROUND — wraps the method completely (most powerful)**_

**@Around("@annotation(com.example.annotation.Timed)")**

**public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {**

**long start = System.currentTimeMillis();**

**try {**

**return joinPoint.proceed();**

**} finally {**

**long duration = System.currentTimeMillis() - start;**

**log.info("{}.{}() took {}ms",**

**joinPoint.getTarget().getClass().getSimpleName(),**

**joinPoint.getSignature().getName(), duration);**

**}**

**}**

**}**

**Custom annotation + AOP — real-world example:**

**@Target(ElementType.METHOD)**

**@Retention(RetentionPolicy.RUNTIME)**

**public @interface RateLimit {**

**int maxRequests() default 100;**

**int windowSeconds() default 60;**

**}**

**@Aspect**

**@Component**

**public class RateLimitAspect {**

**@Around("@annotation(rateLimit)")**

**public Object enforce(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {**

**String key = joinPoint.getSignature().toShortString();**

**if (rateLimiter.isRateLimited(key, rateLimit.maxRequests(), rateLimit.windowSeconds())) {**

**throw new RateLimitExceededException("Too many requests");**

**}**

**return joinPoint.proceed();**

**}**

**}**

_**// Usage**_

**@RateLimit(maxRequests = 50, windowSeconds = 60)**

**@GetMapping("/search")**

**public List search(@RequestParam String query) { ... }**

**Section 9: Scheduling & Async Annotations**
---------------------------------------------

### **Q24. How do @Scheduled and @Async work?**

**Expected Answer:**

#### **@Scheduled — Run tasks on a schedule**

**@Configuration**

**@EnableScheduling**

**public class SchedulingConfig { }**

**@Service**

**@Slf4j**

**public class ReportService {**

**@Scheduled(fixedRate = 60000)**  _**// every 60 seconds**_

**public void generateReport() { ... }**

**@Scheduled(fixedDelay = 30000)**  _**// 30s after previous execution COMPLETES**_

**public void cleanupTempFiles() { ... }**

**@Scheduled(cron = "0 0 2 \* \* \*")**  _**// every day at 2:00 AM**_

**public void nightlyBatch() { ... }**

**@Scheduled(cron = "0 \*/5 \* \* \* \*")**  _**// every 5 minutes**_

**public void healthCheck() { ... }**

**}**

**Attribute**

**Behavior**

**fixedRate**

**Runs every N ms, regardless of when previous run ended**

**fixedDelay**

**Waits N ms after previous execution completes before starting next**

**cron**

**Cron expression for complex schedules**

**initialDelay**

**Wait before first execution**

#### **@Async — Run methods in a separate thread**

**@Configuration**

**@EnableAsync**

**public class AsyncConfig {**

**@Bean("taskExecutor")**

**public Executor taskExecutor() {**

**ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();**

**executor.setCorePoolSize(5);**

**executor.setMaxPoolSize(20);**

**executor.setQueueCapacity(100);**

**executor.setThreadNamePrefix("async-");**

**executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());**

**executor.initialize();**

**return executor;**

**}**

**}**

**@Service**

**public class NotificationService {**

**@Async("taskExecutor")**

**public CompletableFuture sendEmail(String to, String body) {**

_**// runs in a separate thread — caller doesn't wait**_

**emailClient.send(to, body);**

**return CompletableFuture.completedFuture("sent");**

**}**

**@Async**

**public void sendSms(String number, String message) {**

_**// fire and forget — no return value**_

**smsClient.send(number, message);**

**}**

**}**

**Important: Like @Transactional, @Async uses proxies. Self-invocation won't work.**

**Section 10: Spring Boot Actuator**
------------------------------------

### **Q25. What is Spring Boot Actuator and what endpoints does it provide?**

**Expected Answer:Actuator provides production-ready operational endpoints for monitoring, health checks, and metrics.**

**org.springframework.boot**

**spring-boot-starter-actuator**

**Endpoint**

**Purpose**

**/actuator/health**

**Application health status**

**/actuator/info**

**App info (version, description)**

**/actuator/metrics**

**Application metrics (JVM, HTTP, custom)**

**/actuator/env**

**Environment properties**

**/actuator/beans**

**All registered beans**

**/actuator/mappings**

**All @RequestMapping endpoints**

**/actuator/loggers**

**View and change log levels at runtime**

**/actuator/threaddump**

**JVM thread dump**

**/actuator/heapdump**

**JVM heap dump**

**/actuator/conditions**

**Auto-configuration report (what was applied and why)**

**/actuator/configprops**

**All @ConfigurationProperties**

**/actuator/scheduledtasks**

**All scheduled tasks**

**/actuator/caches**

**Cache information**

**Configuration:**

**management:**

**endpoints:**

**web:**

**exposure:**

**include: health, info, metrics, loggers, env**

**endpoint:**

**health:**

**show-details: when\_authorized**

**info:**

**env:**

**enabled: true**

**Custom health indicator:**

**@Component**

**public class PaymentGatewayHealthIndicator implements HealthIndicator {**

**@Override**

**public Health health() {**

**boolean isGatewayUp = paymentGateway.ping();**

**if (isGatewayUp) {**

**return Health.up()**

**.withDetail("provider", "Stripe")**

**.withDetail("latency", "45ms")**

**.build();**

**}**

**return Health.down()**

**.withDetail("error", "Payment gateway unreachable")**

**.build();**

**}**

**}**

**Custom metrics:**

**@Service**

**public class OrderService {**

**private final Counter orderCounter;**

**private final Timer orderTimer;**

**public OrderService(MeterRegistry registry) {**

**this.orderCounter = Counter.builder("orders.created")**

**.tag("type", "online")**

**.description("Number of orders created")**

**.register(registry);**

**this.orderTimer = Timer.builder("orders.processing.time")**

**.description("Time to process an order")**

**.register(registry);**

**}**

**public Order createOrder(OrderRequest request) {**

**return orderTimer.record(() -> {**

**Order order = processOrder(request);**

**orderCounter.increment();**

**return order;**

**});**

**}**

**}**

**Section 11: Security Annotations**
------------------------------------

### **Q26. What are the key Spring Security annotations?**

**Expected Answer:**

**@Configuration**

**@EnableWebSecurity**

**@EnableMethodSecurity**  _**// enables @PreAuthorize, @PostAuthorize, etc.**_

**public class SecurityConfig {**

**@Bean**

**public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {**

**return http**

**.csrf(csrf -> csrf.disable())**

**.authorizeHttpRequests(auth -> auth**

**.requestMatchers("/api/public/\*\*").permitAll()**

**.requestMatchers("/api/admin/\*\*").hasRole("ADMIN")**

**.requestMatchers("/actuator/\*\*").hasRole("OPS")**

**.anyRequest().authenticated()**

**)**

**.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))**

**.build();**

**}**

**}**

**Method-level security:**

**@Service**

**public class OrderService {**

**@PreAuthorize("hasRole('ADMIN')")**

**public void deleteOrder(Long id) { ... }**

**@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")**

**public List getOrdersByUser(Long userId) { ... }**

**@PreAuthorize("@orderSecurity.canAccess(#orderId, authentication)")**

**public Order getOrder(Long orderId) { ... }**

**@PostAuthorize("returnObject.userId == authentication.principal.id")**

**public Order getOrderSecure(Long id) { ... }**

**@PreFilter("filterObject.userId == authentication.principal.id")**

**public void processBatch(List orders) { ... }**

**}**

**Annotation**

**When**

**Purpose**

**@PreAuthorize**

**Before method**

**Check permission before execution**

**@PostAuthorize**

**After method**

**Check permission on the return value**

**@PreFilter**

**Before method**

**Filter input collection**

**@PostFilter**

**After method**

**Filter return collection**

**@Secured**

**Before method**

**Simpler role check (no SpEL)**

**Section 12: Testing Annotations**
-----------------------------------

### **Q27. What testing annotations does Spring Boot provide?**

**Expected Answer:**

**Annotation**

**Purpose**

**Context Loaded**

**@SpringBootTest**

**Full integration test**

**Full application context**

**@WebMvcTest**

**Controller layer only**

**Web layer (no service, no repo)**

**@DataJpaTest**

**Repository layer only**

**JPA + embedded DB**

**@MockBean**

**Replace a bean with a Mockito mock**

**—**

**@SpyBean**

**Wrap a bean with a Mockito spy**

**—**

**@TestConfiguration**

**Test-specific configuration**

**—**

**@ActiveProfiles("test")**

**Activate test profile**

**—**

**Controller test example:**

**@WebMvcTest(OrderController.class)**

**class OrderControllerTest {**

**@Autowired**

**private MockMvc mockMvc;**

**@MockBean**

**private OrderService orderService;**

**@Test**

**void shouldReturnOrder() throws Exception {**

**OrderDTO order = new OrderDTO(1L, "ORD-001", BigDecimal.TEN);**

**when(orderService.getById(1L)).thenReturn(order);**

**mockMvc.perform(get("/api/orders/1"))**

**.andExpect(status().isOk())**

**.andExpect(jsonPath("$.orderNumber").value("ORD-001"))**

**.andExpect(jsonPath("$.total").value(10));**

**}**

**@Test**

**void shouldReturn404WhenOrderNotFound() throws Exception {**

**when(orderService.getById(999L))**

**.thenThrow(new ResourceNotFoundException("Order", 999L));**

**mockMvc.perform(get("/api/orders/999"))**

**.andExpect(status().isNotFound());**

**}**

**@Test**

**void shouldValidateInput() throws Exception {**

**String invalidRequest = """**

**{ "customerName": "", "email": "not-an-email" }**

**""";**

**mockMvc.perform(post("/api/orders")**

**.contentType(MediaType.APPLICATION\_JSON)**

**.content(invalidRequest))**

**.andExpect(status().isBadRequest());**

**}**

**}**

**Repository test example:**

**@DataJpaTest**

**@ActiveProfiles("test")**

**class OrderRepositoryTest {**

**@Autowired**

**private OrderRepository orderRepository;**

**@Autowired**

**private TestEntityManager entityManager;**

**@Test**

**void shouldFindOrdersByStatus() {**

**Order order = new Order("ORD-001", OrderStatus.PENDING);**

**entityManager.persistAndFlush(order);**

**List results = orderRepository.findByStatus(OrderStatus.PENDING);**

**assertThat(results).hasSize(1);**

**assertThat(results.get(0).getOrderNumber()).isEqualTo("ORD-001");**

**}**

**}**

**Section 13: Real-World Scenario Questions**
---------------------------------------------

### **Q28. Scenario: Your Spring Boot application starts up but certain beans are not being created. How do you debug?**

**Expected Answer:**

1.  **Run with --debug or set debug=true in properties — prints the auto-configuration report showing what was applied and what was skipped (and why).**

2.  **Check /actuator/conditions — Shows positive matches (applied) and negative matches (skipped) with reasons.**

3.  **Check /actuator/beans — Lists all registered beans.**

4.  **Common causes:**


*   **Component not in a package scanned by @ComponentScan (main class in wrong package).**

*   **Missing @Component / @Service / @Bean annotation.**

*   **@Conditional annotation evaluated to false.**

*   **Missing dependency on classpath (auto-configuration skipped).**

*   **Profile mismatch — bean has @Profile("prod") but running with dev profile.**


### **Q29. Scenario: Your API returns a 200 with an empty body instead of a 404 when a resource doesn't exist. What's wrong?**

**Expected Answer:The service method probably returns null when the resource is not found, and the controller returns it as 200 with an empty body.Fix — Use exceptions:**

**@Service**

**public class OrderService {**

**public OrderDTO getById(Long id) {**

**return orderRepository.findById(id)**

**.map(this::toDTO)**

**.orElseThrow(() -> new ResourceNotFoundException("Order", id));**

**}**

**}**

**@ResponseStatus(HttpStatus.NOT\_FOUND)**

**public class ResourceNotFoundException extends RuntimeException { ... }**

**Or use ResponseEntity:**

**@GetMapping("/{id}")**

**public ResponseEntity getOrder(@PathVariable Long id) {**

**return orderService.findById(id)**

**.map(ResponseEntity::ok)**

**.orElse(ResponseEntity.notFound().build());**

**}**

### **Q30. Scenario: You added @Transactional to your service method but the transaction is not rolling back on exception. What could be wrong?**

**Expected Answer:**

1.  **Checked exception — By default, @Transactional only rolls back on unchecked (runtime) exceptions. Fix:**

2.     **@Transactional(rollbackFor = Exception.class)**

3.  **Self-invocation — Calling the @Transactional method from within the same class bypasses the proxy.**

4.  **Method is not public — Spring AOP proxies only intercept public methods.**

5.  **Exception caught inside the method — If you catch the exception and don't rethrow, Spring thinks it succeeded:**

6.     **@Transactional**

7.     **public void process() {**

8.         **try {**

9.             **riskyOperation();**

10.         **} catch (Exception e) {**

11.             **log.error("Failed", e);**

12.             _**// Transaction will COMMIT because no exception propagated!**_

13.         **}**

14.     **}**

15.  **Wrong transaction manager — Multiple data sources but not specifying which manager:**

16.     **@Transactional("secondaryTransactionManager")**


### **Q31. Scenario: Your Spring Boot app takes 45 seconds to start. How do you optimize startup time?**

**Expected Answer:**

1.  **Lazy initialization — Don't create beans until they're first needed:**

2.     **spring.main.lazy-initialization=true**

3.  **Exclude unnecessary auto-configurations:**

4.     **@SpringBootApplication(exclude = {**

5.         **DataSourceAutoConfiguration.class,**

6.         **MongoAutoConfiguration.class,**

7.         **SecurityAutoConfiguration.class**

8.     **})**

9.  **Use Spring Boot's startup actuator to find slow beans:**

10.     **management.endpoint.startup.enabled=true**


**Then check /actuator/startup for bean initialization timings.**

1.  **Reduce component scanning scope — Don't scan packages you don't need.**

2.  **Use Spring AOT (Ahead-of-Time compilation) with GraalVM native image for sub-second startup.**

3.  **Review Hibernate/JPA — spring.jpa.defer-datasource-initialization=true, validate vs. update ddl-auto.**

4.  **Profile startup with -Dspring.context.initializer.classes or JFR (Java Flight Recorder).**


### **Q32. Scenario: You need to run some initialization code after your Spring Boot application is fully started. How do you do it?**

**Expected Answer:Several options, in order of preference:1. ApplicationRunner (recommended):**

**@Component**

**public class DataInitializer implements ApplicationRunner {**

**@Override**

**public void run(ApplicationArguments args) {**

_**// Runs after context is fully initialized**_

**loadReferenceData();**

**}**

**}**

**2\. CommandLineRunner:**

**@Component**

**public class CacheWarmer implements CommandLineRunner {**

**@Override**

**public void run(String... args) {**

**cacheService.warmUp();**

**}**

**}**

**3\. @EventListener(ApplicationReadyEvent.class):**

**@Component**

**public class StartupListener {**

**@EventListener(ApplicationReadyEvent.class)**

**public void onReady() {**

**log.info("Application is ready, initializing...");**

**}**

**}**

**4\. @PostConstruct — Runs after the individual bean is initialized (NOT after the full app is ready):**

**@Service**

**public class CacheService {**

**@PostConstruct**

**public void init() {**

_**// Runs when THIS bean is created — other beans may not be ready yet**_

**loadCache();**

**}**

**}**

**Order: @PostConstruct → ApplicationRunner / CommandLineRunner → ApplicationReadyEvent**

### **Q33. What is the difference between @PostConstruct, InitializingBean, and @Bean(initMethod)?**

**Expected Answer:All three run initialization logic after a bean is created and dependencies are injected:**

**Mechanism**

**Source**

**When it Runs**

**Advantage**

**@PostConstruct**

**JSR-250**

**After DI, before bean is put in context**

**Standard Java annotation, most common**

**InitializingBean.afterPropertiesSet()**

**Spring**

**After DI**

**Spring-specific, couples code to Spring**

**@Bean(initMethod = "init")**

**Spring**

**After DI**

**No Spring dependency in the class itself**

**Execution order: @PostConstruct → InitializingBean → initMethod**

**@Component**

**public class MyService implements InitializingBean {**

**@PostConstruct**          _**// runs 1st**_

**public void postConstruct() { }**

**@Override**               _**// runs 2nd**_

**public void afterPropertiesSet() { }**

**public void customInit() { }**  _**// runs 3rd (if configured via @Bean(initMethod))**_

**}**

**Destruction equivalent: @PreDestroy → DisposableBean.destroy() → @Bean(destroyMethod)**

**Section 14: Miscellaneous Annotations**
-----------------------------------------

### **Q34. Quick-fire — Explain these annotations in one line each.**

**Annotation**

**Purpose**

**@Lazy**

**Bean is created only when first requested, not at startup**

**@Scope("prototype")**

**A new instance is created every time the bean is requested**

**@Scope("request")**

**One instance per HTTP request**

**@Scope("session")**

**One instance per HTTP session**

**@DependsOn("beanX")**

**Ensures beanX is initialized before this bean**

**@Order(1)**

**Controls execution order of beans (filters, aspects, runners)**

**@Conditional(MyCondition.class)**

**Bean created only if custom condition is true**

**@EventListener**

**Listens for application events**

**@Async**

**Method runs in a separate thread**

**@EnableScheduling**

**Enables @Scheduled methods**

**@CrossOrigin**

**Enables CORS for a controller/method**

**@ResponseStatus(HttpStatus.CREATED)**

**Sets the HTTP status code for the response**

**@JsonIgnore**

**Excludes a field from JSON serialization (Jackson)**

**@JsonProperty("full\_name")**

**Maps a JSON field to a Java field (Jackson)**

**@JsonFormat(pattern = "yyyy-MM-dd")**

**Custom date format in JSON**

**@Slf4j (Lombok)**

**Generates a log field**

**@RequiredArgsConstructor (Lombok)**

**Generates constructor for final fields**

**@Data (Lombok)**

**Generates getters, setters, equals, hashCode, toString**

**@Builder (Lombok)**

**Generates a builder pattern**

### **Q35. What is the Bean lifecycle in Spring? List all the stages.**

**Expected Answer:**

**1.  Bean class instantiated (constructor called)**

**2.  Dependencies injected (setter/@Autowired)**

**3.  BeanNameAware.setBeanName()**

**4.  BeanFactoryAware.setBeanFactory()**

**5.  ApplicationContextAware.setApplicationContext()**

**6.  BeanPostProcessor.postProcessBeforeInitialization()   ← for ALL beans**

**7.  @PostConstruct**

**8.  InitializingBean.afterPropertiesSet()**

**9.  @Bean(initMethod = "customInit")**

**10\. BeanPostProcessor.postProcessAfterInitialization()    ← proxies created here**

**11\. ──── Bean is ready for use ────**

**12\. @PreDestroy                                           (on shutdown)**

**13\. DisposableBean.destroy()                              (on shutdown)**

**14\. @Bean(destroyMethod = "customDestroy")                (on shutdown)**

**BeanPostProcessor is critical — this is where Spring creates AOP proxies for @Transactional, @Cacheable, @Async, etc. That's why those annotations use proxies and why self-invocation bypasses them.**

**1 SpringApplication instance created**

**2\. Determine application type (SERVLET, REACTIVE, NONE)**

**3\. Load SpringApplicationRunListeners (publish events)**

**4\. Prepare Environment (read application.properties, env vars, CLI args)**

**5\. Create ApplicationContext (AnnotationConfigServletWebServerApplicationContext)**

**6\. Load bean definitions (component scanning, @Configuration classes)**

**7\. Run Auto-Configuration (@EnableAutoConfiguration)**

**8\. Refresh context (instantiate all singleton beans, dependency injection)**

**9\. Start embedded web server (Tomcat/Jetty)**

**10\. Call ApplicationRunner / CommandLineRunner beans**

**11\. Application is ready — publish ApplicationReadyEvent**


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
 