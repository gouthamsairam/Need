**Spring Boot Actuator: Complete Architecture & Monitoring Guide**
==================================================================

**1\. What Is Spring Boot Actuator?**
-------------------------------------

**Spring Boot Actuator** is a foundational sub-project of the Spring Boot ecosystem that integrates production-ready operational features into an application. It provides developers, site reliability engineers (SREs), and monitoring tools with a secure, diagnostic window into a running application's internal state.

Actuator natively captures critical runtime instrumentation data, answering vital infrastructure questions:

*   Is the application instance up and running?

*   Are the downstream infrastructure dependencies (databases, message brokers, caches) healthy?

*   What are the real-time Java Virtual Machine (JVM) resource consumptions (heap memory, thread states, CPU utilization)?

*   What is the volume, throughput, and HTTP status distribution of incoming traffic?

*   Which beans are instantiated inside the Spring application context, and what are the active request mapping topologies?

*   Why were certain auto-configurations applied (or excluded) during application startup?

*   Can logging thresholds be dynamically altered at runtime without triggering a container restart?


### **Structural Separation of Concerns**

Actuator strictly isolates management and monitoring vectors from functional business APIs by introducing distinct **management endpoints**:

Plaintext

Functional Business Endpoint:  http://localhost:8080/api/orders/101

Operational Actuator Endpoint: http://localhost:8080/actuator/health

**2\. Why Is It Required?**
---------------------------

Without Actuator, engineering teams must manually construct bespoke operational endpoints to expose system status:

Plaintext

/health

/database-status

/memory-usage

/application-info

This manual approach leads to non-standard implementations, custom maintenance overhead, and fragmented monitoring patterns across microservice architectures. Spring Boot Actuator solves this problem by standardizing operational metrics through automated, production-grade configurations.

### **Core Deployment Paradigms**

*   **Production Ecosystems:** Provides clear hooks for external monitoring agents.

*   **Microservices & Distributed Cloud Networks:** Standardizes diagnostic checks across vast, independent service topologies.

*   **Container Orchestration (Docker & Kubernetes):** Natively feeds lifecycle hooks directly into cluster management systems.

*   **Continuous Integration/Continuous Deployment (CI/CD) Gates:** Offers deterministic smoke-test validations during deployment pipelines.

*   **Alerting Infra:** Supplies standardized data formats for visualization engines.


**3\. Adding Actuator to the Project**
--------------------------------------

Integrating Actuator requires adding the appropriate production-ready starter dependency.

### **Maven Configuration**

XML

org.springframework.boot

spring-boot-starter-actuator

### **Gradle Configuration**

Gradle

implementation 'org.springframework.boot:spring-boot-starter-actuator'

Upon successful application startup, the discovery index and base health state can be reached at the following default locations:

Plaintext

http://localhost:8080/actuator

http://localhost:8080/actuator/health

#### **Default Health JSON Response Payload**

JSON

{

"status": "UP"

}

**4\. Primary Actuator Endpoints**
----------------------------------

**Endpoint**

**Protocol Mapping**

**Functional Purpose & Diagnostic Value**

/actuator/health

HTTP / JMX

Evaluates core application health and downstream dependency availability.

/actuator/info

HTTP / JMX

Exposes general application information (e.g., git commit data, build versions).

/actuator/metrics

HTTP / JMX

Lists and describes available application metrics (JVM, CPU, Memory, HTTP traffic).

/actuator/loggers

HTTP / JMX

Queries, inspects, and modifies application log levels dynamically at runtime.

/actuator/env

HTTP / JMX

Exposes Environment properties from Spring's ConfigurableEnvironment abstraction.

/actuator/beans

HTTP / JMX

Returns a complete listing of all initialized Spring beans in the ApplicationContext.

/actuator/mappings

HTTP / JMX

Displays a collated list of all structural HTTP controller request paths.

/actuator/conditions

HTTP / JMX

Evaluates positive and negative auto-configuration evaluation matching logs.

/actuator/caches

HTTP / JMX

Identifies and describes active application cache providers and spaces.

/actuator/threaddump

HTTP / JMX

Generates a standard JVM thread dump for analyzing concurrency deadlocks.

/actuator/heapdump

HTTP / JMX

Triggers and returns a standard hprof binary heap dump file for memory analysis.

/actuator/scheduledtasks

HTTP / JMX

Tracks and lists all declared cron or scheduled tasks within the application.

/actuator/prometheus

HTTP / JMX

Formats and exposes metrics in a plain-text structure parseable by Prometheus scrapers.

/actuator/logfile

HTTP / JMX

streams the underlying application log file contents (if logging.file.name is set).

/actuator/httpexchanges

HTTP / JMX

Exposes structural trace history of recent HTTP request-response data.

/actuator/sbom

HTTP / JMX

Serves available Software Bill of Materials data to track dependency security vulnerability profiles.

\[!NOTE\]

Specific endpoints (such as /actuator/prometheus) are conditional. They initialize only when their underlying libraries or backing bean instances are present in the runtime classpath.

**5\. Controlling Endpoint Exposure**
-------------------------------------

For security reasons, standard Spring Boot configurations enforce restrictive access out of the box: **only the** /health **endpoint is exposed over HTTP**, while all endpoints remain accessible over JMX.

### **Custom Exposure Configurations**

#### **Target Exposure via application.properties**

To expose specific endpoints over the web:

Properties

management.endpoints.web.exposure.include=health,info,metrics,loggers

#### **Global Exposure over Web Interconnects**

Properties

management.endpoints.web.exposure.include=\*

#### **Selective Exposure with Explicit Exclusions**

Properties

management.endpoints.web.exposure.include=\*

management.endpoints.web.exposure.exclude=env,beans,heapdump

#### **Structured Exposure via application.yml**

YAML

management:

endpoints:

web:

exposure:

include: "\*"

exclude: "env,beans,heapdump"

### **The Three Operational Pillars of Access Control**

1.  **Enabled (Availability):** The endpoint bean is successfully constructed and exists inside the Spring application context.

2.  **Exposed (Visibility):** The endpoint is mapped to transport channels (HTTP/JMX) and can be reached over the network.

3.  **Authorized (Security):** The incoming request satisfies the necessary authentication and role-based authorization rules to view the data.


**6\. Architecture of the Health Endpoint**
-------------------------------------------

The /actuator/health endpoint aggregates diagnostic information from various **HealthIndicator** components across the application.

### **Standard Out-of-the-Box Contributors**

*   DiskSpaceHealthIndicator**:** Monitors low disk space allocations.

*   DataSourceHealthIndicator**:** Executes validation queries against active relational database connections.

*   RedisHealthIndicator **/** MongoHealthIndicator**:** Validates connection pools for cache and NoSQL stores.

*   RabbitHealthIndicator **/** KafkaHealthIndicator**:** Verifies broker connection lifelines.


### **Deterministic Health States**

The overall application status is determined by an aggregated ranking order of individual statuses:

Plaintext

UP ──> DOWN ──> OUT\_OF\_SERVICE ──> UNKNOWN

If any contributor reports a status of DOWN, the overall application state drops to DOWN.

### **Fine-Grained Health Details**

To view details for individual components, adjust the visibility settings:

Properties

management.endpoint.health.show-details=always

For production environments, restrict detail access to authenticated, authorized administrators:

Properties

management.endpoint.health.show-details=when-authorized

#### **Sample Detailed JSON Payload**

JSON

{

"status": "UP",

"components": {

"db": {

"status": "UP",

"details": {

"database": "Oracle",

"validationQuery": "isValid()"

}

},

"diskSpace": {

"status": "UP",

"details": {

"total": 499963174912,

"free": 321458902016,

"threshold": 10485760

}

}

}

}

**7\. Custom Health Indicator Implementations**
-----------------------------------------------

When an application depends on an external service (like a payment gateway), you can build a custom HealthIndicator to track its availability:

Java

package com.example.orders.health;

import org.springframework.boot.actuate.health.Health;

import org.springframework.boot.actuate.health.HealthIndicator;

import org.springframework.stereotype.Component;

@Component

public class PaymentServiceHealthIndicator implements HealthIndicator {

@Override

public Health health() {

boolean available = checkPaymentServiceConnectivity();

if (available) {

return Health.up()

.withDetail("paymentService", "Available Connection Active")

.withDetail("latencyMs", 14)

.build();

}

return Health.down()

.withDetail("paymentService", "Unavailable Connection Dropped")

.withDetail("errorCode", 503)

.build();

}

private boolean checkPaymentServiceConnectivity() {

// Real-world logic to check external network availability

return true;

}

}

Spring Boot automatically scans and registers this bean into the /actuator/health aggregation chain.

\[!WARNING\]

**Enforce Network Timeouts:** Custom health checks should always apply strict network connect and read timeouts. A slow or blocking health check will cause the entire /actuator/health endpoint to lag, which can lead to false monitoring alerts or improper container restarts.

**8\. Container Orchestration: Liveness and Readiness**
-------------------------------------------------------

In containerized cloud environments like Kubernetes, the health endpoint can separate application state into **Liveness** and **Readiness** probes.

Plaintext

Kubernetes Orchestrator 

  ├── Liveness Probe  ──> /actuator/health/liveness   ──> Is container process corrupted? (Restart required)

└── Readiness Probe ──> /actuator/health/readiness  ──> Is service ready for traffic? (Route/Isolate)

### **Liveness Probe (**/actuator/health/liveness**)**

Evaluates whether the internal state of the application is valid. If this endpoint returns a failure, Kubernetes assumes the container has entered an unrecoverable deadlock or broken state and restarts it.

### **Readiness Probe (**/actuator/health/readiness**)**

Evaluates whether the application is fully initialized and prepared to route traffic. If this endpoint returns a failure, Kubernetes stops routing incoming requests to this container instance but does not restart it.

### **Enabling Probe Support**

To activate explicit probe endpoints:

Properties

management.endpoint.health.probes.enabled=true

\[!IMPORTANT\]

**Isolate Dependency Faults:** Temporary database connections failures should impact **Readiness**, not **Liveness**. If a database failure degrades Liveness, Kubernetes can enter a destructive restart loop, repeatedly restarting an otherwise healthy application process while waiting for the database to recover.

**9\. Metrics & the Micrometer Architecture**
---------------------------------------------

Actuator uses **Micrometer** as its vendor-neutral metrics collection facade. This allows you to gather metrics from your application and export them to external monitoring tools.

### **Core Out-of-the-Box Telemetry Dimensions**

*   jvm.memory.used**:** Real-time heap and non-heap memory consumption.

*   jvm.threads.live**:** Active thread counts inside the process.

*   process.cpu.usage **/** system.cpu.usage**:** Direct CPU utilization metrics.

*   http.server.requests**:** Request rates, latency distributions, and HTTP status counts.

*   hikaricp.connections.active**:** Active pool allocations for database connectivity.


To inspect specific metrics via HTTP:

Plaintext

GET http://localhost:8080/actuator/metrics/jvm.memory.used

GET http://localhost:8080/actuator/metrics/http.server.requests

### **Programmatic Custom Metrics**

You can use Micrometer's MeterRegistry to record custom application metrics, such as tracking order volume:

Java

package com.example.orders.service;

import io.micrometer.core.instrument.Counter;

import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.stereotype.Service;

@Service

public class OrderService {

private final Counter orderCounter;

public OrderService(MeterRegistry registry) {

// Declaring a dimensional counter metric

this.orderCounter = registry.counter("orders.created.total", "region", "US-East");

}

public void createOrder() {

// Business logic to process an order

orderCounter.increment();

}

}

### **Telemetry Best Practices**

*   **Counters:** Use to track monotonically increasing events (e.g., total orders processed).

*   **Timers:** Use to measure short-duration events, latency trends, and execution timings.

*   **Gauges:** Use to capture fluctuating, real-time values (e.g., current active log-in sessions).


\[!WARNING\]

**Avoid High-Cardinality Tags:** Avoid using highly unique values—such as a customerId or orderId—as metric tags. Doing so can generate thousands of unique time-series entries, which can overwhelm memory and degrade performance on your downstream monitoring platforms.

**10\. Prometheus and Grafana Pipelines**
-----------------------------------------

To export metrics into a format that a Prometheus time-series database can scrape, include the specialized Micrometer registry dependency:

XML

io.micrometer

micrometer-registry-prometheus

Next, expose the scraper endpoint:

Properties

management.endpoints.web.exposure.include=health,info,prometheus

Prometheus will then pull application telemetry directly from:

Plaintext

http://localhost:8080/actuator/prometheus

### **Complete Telemetry Architecture Pipeline**

Plaintext

Spring Boot Application ──> Actuator Starter ──> Micrometer Facade ──> /actuator/prometheus ──> Prometheus Engine ──> Grafana Dashboard ──> SRE Alerts

**11\. Changing Logging Levels at Runtime**
-------------------------------------------

You can use the /actuator/loggers endpoint to inspect and change application log levels on the fly. This allows you to troubleshoot issues in production without restarting the application.

### **Step 1: Query Current Logging Status**

Plaintext

GET http://localhost:8080/actuator/loggers/com.example.orders

### **Step 2: Dynamically Alter the Log Level**

To change the logging threshold to DEBUG, send a POST request with the new level in the payload:

HTTP

POST /actuator/loggers/com.example.orders HTTP/1.1

Host: localhost:8080

Content-Type: application/json

{

"configuredLevel": "DEBUG"

}

This adjustment updates the logger immediately in memory. The change is temporary and will revert to the original file configuration if the application restarts.

**12\. Custom Actuator Endpoints**
----------------------------------

For operational tasks that don't fit into standard business APIs, you can build custom Actuator endpoints using the @Endpoint annotation:

Java

package com.example.orders.endpoints;

import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;

import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import org.springframework.stereotype.Component;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

@Component

@Endpoint(id = "orderstats")

public class OrderStatisticsEndpoint {

@ReadOperation

public Map getStatistics() {

return Map.of(

"pendingOrders", 42,

"completedOrders", 1850,

"systemLoadAlert", false

);

}

}

Expose the custom endpoint:

Properties

management.endpoints.web.exposure.include=health,orderstats

You can then read the data at:

Plaintext

GET http://localhost:8080/actuator/orderstats

### **Endpoint Annotation Mapping Specifications**

*   @ReadOperation**:** Maps directly to HTTP GET requests (for retrieving status).

*   @WriteOperation**:** Maps directly to HTTP POST requests (for triggering actions).

*   @DeleteOperation**:** Maps directly to HTTP DELETE requests (for removing state).


\[!IMPORTANT\]

**Isolate Business Logic:** Use @RestController components for core business workflows. Reserve custom Actuator endpoints exclusively for system management, platform configuration, and operational diagnostics.

**13\. How Actuator Works Internally**
--------------------------------------

When a Spring Boot application initializes, the Actuator engine follows a structured lifecycle to set up and expose endpoints:

1.  **Dependency Detection:** The Spring Boot loader detects the spring-boot-starter-actuator dependency on the classpath.

2.  **Auto-Configuration Activation:** ActuatorAutoConfiguration kicks in, initializing base operational components and configuration properties.

3.  **Bean and Contributor Discovery:** The framework instantiates built-in endpoints and registers default HealthIndicator contributors.

4.  **Conditional Evaluation:** Conditional annotations (such as @ConditionalOnBean or @ConditionalOnClass) evaluate the environment, auto-configuring indicators for active dependencies like Redis, relational databases, or Kafka.

5.  **Endpoint Endpoint Indexing:** Web and JMX endpoint discoverers find and register built-in and custom endpoints.

6.  **Exposure Filtering:** The application reads exposure settings (such as management.endpoints.web.exposure.include) to determine which endpoints are accessible over the network.

7.  **Transport Mapping:** Spring MVC or WebFlux maps these endpoints to HTTP routes under the configured base path (default: /actuator).

8.  **Security Interception:** The application's SecurityFilterChain processes incoming requests, validating roles and permissions before allowing access to sensitive endpoints.

9.  **Runtime Data Harvesting:** When an endpoint is called, it queries the Spring context, JVM, Micrometer registry, or system dependencies to gather real-time stats.

10.  **Payload Serialization:** The collected data is marshaled into JSON or format-specific plain text (e.g., Prometheus format) and returned in the HTTP response.


### **Request Flow Pipeline**

Plaintext

HTTP Request ──> SecurityFilterChain ──> Actuator Web Mappings ──> Endpoint Operation Method ──> Metrics/JVM Harvesting ──> Serialized JSON Output

**14\. Securing Actuator Endpoints**
------------------------------------

Sensitive endpoints can expose internal system configuration details, environment variables, or memory profiles. Leaving these endpoints unprotected can create security risks, such as information disclosure or unauthorized control over logging states.

\[!WARNING\]

**Protect Heap Dump Files:** The /actuator/heapdump endpoint generates a complete snapshot of JVM memory. This file can contain sensitive data, including unencrypted passwords, access tokens, and personal user data. Never expose this endpoint publicly without strict authentication.

### **Secure Production Strategy**

1.  Run management traffic on an isolated network port.

2.  Bind Actuator endpoints to a local loopback or private internal interface.

3.  Restrict health details to authorized users.


Properties

\# Run management endpoints on a separate port

management.server.port=9090

\# Bind management traffic to internal localhost only

management.server.address=127.0.0.1

\# Limit detailed health payloads to authenticated sessions

management.endpoint.health.show-details=when-authorized

### **Configuring Role-Based Access with Spring Security**

When you declare a custom SecurityFilterChain, Spring Boot's default security rules back off. You must explicitly configure access controls to ensure your management endpoints remain protected:

Java

package com.example.orders.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.web.SecurityFilterChain;

@Configuration

public class ActuatorSecurityConfig {

@Bean

public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {

http

// Isolate rules to actuator endpoints only

.securityMatcher(EndpointRequest.toAnyEndpoint())

.authorizeHttpRequests(auth -> auth

// Allow public access to the base health endpoint

.requestMatchers(EndpointRequest.to("health")).permitAll()

// Require administrative privileges for all other diagnostics

.anyRequest().hasRole("ACTUATOR\_ADMIN")

)

.httpBasic(Customizer.withDefaults());

return http.build();

}

}

**15\. Operational Use-Case Scenario**
--------------------------------------

Consider an e-commerce platform processing checkout transactions. Actuator provides visibility across different operational tiers to help monitor performance and debug incidents:

Plaintext

E-Commerce Application Operational Metrics Matrix:

├── Infrastructure Validation ──> /actuator/health     ──> Verifies Oracle DB pools, Redis cache, & local storage

├── Performance Auditing      ──> /actuator/metrics    ──> Tracks order processing latencies & HTTP error rates

├── Scraper Integration       ──> /actuator/prometheus ──> Supplies clean time-series data streams to Prometheus

├── Live Diagnostics          ──> /actuator/loggers    ──> Enables DEBUG logging at runtime during checkout failures

└── System Forensics          ──> /actuator/threaddump ──> Inspects thread locks if order execution stalls

### **Production Alerting Rule Example**

Plaintext

IF metric(http\_server\_requests\_seconds\_count{status="500"}) / total\_requests > 0.05 FOR 5m

THEN trigger PagerDuty Critical Alert to Order SRE On-Call Team

**16\. Related Observability Concepts**
---------------------------------------

### **Monitoring**

Tracking known indicators and system metrics against predefined thresholds (e.g., checking if disk usage exceeds 85%).

### **Observability**

The ability to infer the internal state of a system based on its external outputs. Observability relies on three primary pillars:

1.  **Logs:** Structured, timestamped event records that provide context around application behavior.

2.  **Metrics:** Aggregated numerical data used to track performance and analyze trends over time.

3.  **Traces:** End-to-end paths of requests as they travel across distributed service topologies.


### **JMX (Java Management Extensions)**

A standard Java technology used for managing and monitoring applications. While Actuator supports JMX, cloud-native architectures typically prefer HTTP endpoints because they integrate more easily with modern container platforms and monitoring tools.

### **Spring Boot Admin**

A community-driven project that provides a web-based user interface for managing and monitoring Spring Boot applications. It visualizes data collected from Actuator endpoints, making it easier to view metrics, read logs, and interact with applications through a central dashboard.

**17\. Advantages**
-------------------

*   **Out-of-the-Box Functionality:** Provides comprehensive production instrumentation with minimal setup.

*   **Seamless Spring Integration:** Works naturally with core Spring Boot auto-configurations.

*   **Extensible Architecture:** Makes it easy to build custom metrics, health checks, and endpoints tailored to your application.

*   **Cloud-Native Compatibility:** Integrates with standard container orchestration tools, load balancers, and Kubernetes probes.

*   **Dynamic System Controls:** Supports runtime adjustments—like changing log thresholds—without requiring an application restart.

*   **Standardized Operations:** Provides a consistent monitoring interface across all microservices in an organization.


**18\. Disadvantages & System Risks**
-------------------------------------

*   **Information Disclosure Vulnerabilities:** If left unprotected, sensitive endpoints can leak internal network layouts, configuration variables, or database paths.

*   **Performance Impact of Memory Dumps:** Generating a heap dump via /actuator/heapdump freezes the JVM and can temporarily exhaust disk space and memory.

*   **Risks of Latency in Health Checks:** Complex or blocking logic in custom health checks can slow down the health endpoint, potentially triggering false monitoring timeouts.

*   **Metric Cardinally Risks:** Misconfiguring metric tags with highly unique values can overload your downstream monitoring systems.

*   **Operational Dependencies:** Actuator collects and surfaces data, but it still requires external tools like Prometheus or Grafana to store, visualize, and alert on that data.

*   **Container Restart Loops:** A misconfigured liveness probe that checks external dependencies can cause Kubernetes to cycle the container repeatedly during a temporary downstream outage.


**19\. Implementation Matrix**
------------------------------

**Monitoring Approach**

**Target Use-Case Scenario**

**Health Endpoints Only**

Small applications or basic health verification behind network load balancers.

**Selected HTTP Endpoints**

Standard production applications that need to balance observability with security.

**Separate Management Ports**

Highly secure enterprise applications that require network-level isolation for admin traffic.

**JMX Management**

Legacy enterprise environments managed via traditional JVM consoles (e.g., JConsole).

**Prometheus Scrapers**

High-scale microservices using Prometheus and Grafana for metrics and alerting.

**Kubernetes Health Probes**

Cloud-native applications orchestrated inside Kubernetes clusters.

**Spring Boot Admin Panels**

Microservice environments that benefit from a shared, central visual dashboard.

**Custom Operational Endpoints**

Applications that require automated, domain-specific administrative actions at runtime.

**20\. Recommended Production Configuration**
---------------------------------------------

The configuration below demonstrates production best practices: running Actuator on an isolated port, enabling Kubernetes probes, restricting health details, and exposing only the required endpoints.

YAML

management:

# Route management traffic over a dedicated internal network port

server:

port: 9090

address: 127.0.0.1

# Limit exposed endpoints to the minimum required set

endpoints:

web:

exposure:

include: "health,info,prometheus,metrics,loggers"

# Configure health endpoint visibility and liveness/readiness probes

endpoint:

health:

show-details: "when-authorized"

probes:

enabled: true

# Expose system and build details via the info endpoint

info:

env:

enabled: true

git:

mode: "full"

### **Production Checklist**

*   **Restrict Access:** Restrict endpoint visibility to a private network or require role-based authentication via Spring Security.

*   **Isolate Traffic:** Run management endpoints on a separate internal port to keep admin traffic isolated from public API traffic.

*   **Optimize Health Checks:** Keep health indicators fast and lightweight; always enforce timeouts on external network dependencies.

*   **Configure Probes Accurately:** Link your container orchestration probes to /actuator/health/liveness and /actuator/health/readiness appropriately to manage container lifecycles safely.

*   **Monitor Metric Cardinality:** Avoid adding highly unique, variable data as tags to maintain stable memory usage in your telemetry systems.

*   **Disable Unused Features:** Turn off endpoints you don't actively use to minimize your application's operational surface area.


**Architectural Synthesis**
---------------------------

**Spring Boot Actuator** is an operational framework that instruments Spring applications by automatically exposing real-time health, metrics, configurations, and diagnostics through standardized HTTP or JMX endpoints.

### **Official Documentation & References**

*   [Spring Boot Production-Ready Features Documentation Reference](https://docs.spring.io/spring-boot/reference/actuator/index.html)

*   [Spring Boot Actuator Core Endpoints and Security Matrix](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html)

*   [Spring Boot Observability, Micrometer Metrics, and Telemetry Registries](https://docs.spring.io/spring-boot/reference/actuator/metrics.html)


**Spring Boot Actuator Interview Questions & Answers**
======================================================

**Comprehensive Technical Documentation**
-----------------------------------------

**Module 1: Fundamentals**
--------------------------

### **Q1: What is Spring Boot Actuator?**

**Answer:** Spring Boot Actuator is a framework sub-project that integrates production-ready operational monitoring, observability, and management features into a running Spring Boot application.

Instead of requiring developers to build custom APIs for system checks, Actuator automatically exposes critical internal application telemetry via **management endpoints** over HTTP or JMX. It allows operators to inspect:

*   **System Health:** Deep state analysis of downstream dependencies (Databases, Caches, Message Brokers).

*   **Dimensional Metrics:** Real-time JVM memory consumption, CPU utilization, garbage collection pauses, and HTTP traffic statistics.

*   **Application Topology:** Active Spring beans, complete request mapping registries, and auto-configuration match reports.

*   **Operational Forensics:** On-demand generation of standard JVM thread dumps and binary heap dumps.

*   **Dynamic Configuration:** Real-time adjustments to package logging levels without requiring an application restart.


### **Q2: How do you add Actuator?**

**Answer:** To introduce Actuator into a project, you include the appropriate starter dependency inside your build configuration file.

#### **Maven Dependency**

XML

org.springframework.boot

spring-boot-starter-actuator

#### **Gradle Dependency**

Gradle

implementation 'org.springframework.boot:spring-boot-starter-actuator'

Upon starting the application on its default port, the primary discovery index can be reached over HTTP at:

Plaintext

http://localhost:8080/actuator

### **Q3: What is the difference between enabled, exposed, and authorized?**

**Answer:** Actuator enforces a strict three-tier lifecycle access boundary for every management endpoint:

*   **Enabled:** This controls whether an endpoint bean is instantiated and registered within the Spring ApplicationContext. An endpoint must be enabled before it can be made available through any transport channel.

*   **Exposed:** This dictates whether an enabled endpoint is accessible over a specific network protocol wrapper, namely HTTP (Web) or JMX. For example, an endpoint can be actively enabled in context but completely hidden from HTTP requests if it is not exposed.

*   **Authorized:** This layer is governed by the application's security configuration (e.g., Spring Security). It determines whether the current authenticated caller possesses the necessary roles, permissions, or security tokens to execute the operations exposed by that endpoint.


\[!NOTE\]

An endpoint can be **enabled** in the application context and **exposed** to the web port, yet remain completely inaccessible to an anonymous user if it fails the required **authorization** filter rules.

### **Q4: How do you expose Actuator endpoints?**

**Answer:** By default, standard configurations expose only the /actuator/health endpoint over HTTP to minimize information disclosure risks. You can adjust this behavior via your environment's configuration files using inclusions and exclusions.

#### **Targeted HTTP Exposure via application.properties**

Properties

management.endpoints.web.exposure.include=health,info,metrics

#### **Global HTTP Exposure**

Properties

management.endpoints.web.exposure.include=\*

#### **Selective Exposure with Explicit Exclusions**

Properties

management.endpoints.web.exposure.include=\*

management.endpoints.web.exposure.exclude=env,heapdump

\[!WARNING\]

Exposing all endpoints via include=\* without an authentication layer like Spring Security is highly unsafe for production. It can leak sensitive system details or expose internal configurations to attackers.

### **Q5: What are the most important Actuator endpoints?**

**Answer:** The primary built-in Actuator endpoints used for enterprise production operations include:

**Endpoint Path**

**Transport Type**

**Operational Purpose & Diagnostic Value**

/actuator/health

HTTP / JMX

Evaluates core application health and downstream dependency availability status.

/actuator/info

HTTP / JMX

Exposes general application information (e.g., git commit logs, build versions).

/actuator/metrics

HTTP / JMX

Lists and breaks down all dimensional application metrics (JVM, CPU, Memory).

/actuator/prometheus

HTTP / JMX

Serves application metrics formatted as plain text for Prometheus scraping engines.

/actuator/loggers

HTTP / JMX

Inspects, queries, and modifies application log levels dynamically at runtime.

/actuator/mappings

HTTP / JMX

Displays a collated list of all registered HTTP controller request path routes.

/actuator/beans

HTTP / JMX

Returns a complete listing of all initialized Spring beans in the active context.

/actuator/conditions

HTTP / JMX

Evaluates positive and negative auto-configuration matching reports.

/actuator/threaddump

HTTP / JMX

Generates a standard JVM thread dump for analyzing concurrency deadlocks.

/actuator/heapdump

HTTP / JMX

Triggers and downloads a standard binary hprof heap dump file for memory analysis.

**Module 2: Health-Check Scenarios**
------------------------------------

### **Q6: How does /actuator/health work internally?**

**Answer:** When a request hits /actuator/health, the framework queries a central registry to discover all beans that implement the HealthContributor or HealthIndicator interface.

Each indicator runs its specific check against a system resource (such as a database or disk) and returns a status. Actuator then processes these results through a configured StatusAggregator to determine the overall system status.

Plaintext

DatabaseHealthIndicator  ──\[Status: UP\]──┐

RedisHealthIndicator     ──\[Status: UP\]──┼──> StatusAggregator ──> Overall Status: UP

DiskSpaceHealthIndicator ──\[Status: UP\]──┘

The four standard health statuses, ranked by severity, are:

Plaintext

DOWN ──> OUT\_OF\_SERVICE ──> UNKNOWN ──> UP

If even a single non-optional contributor reports a status of DOWN, the overall aggregated application status drops to DOWN.

### **Q7: How do you display detailed health information?**

**Answer:** By default, unauthenticated requests to /actuator/health return a simple, high-level summary ({"status": "UP"}). To expose full diagnostic details for each individual system component, configure the visibility settings:

Properties

management.endpoint.health.show-details=always

#### **Production Hardening Strategy**

Exposing full component metrics publicly can leak internal infrastructure details (such as database types or disk paths). For production deployments, change this setting to restrict detailed access to authorized users or roles:

Properties

management.endpoint.health.show-details=when-authorized

### **Q8: How do you create a custom health check?**

**Answer:** You can create a custom health check by defining a Spring bean that implements the HealthIndicator interface and overriding its health() method.

Java

package com.example.payment.health;

import org.springframework.boot.actuate.health.Health;

import org.springframework.boot.actuate.health.HealthIndicator;

import org.springframework.stereotype.Component;

@Component

public class PaymentHealthIndicator implements HealthIndicator {

@Override

public Health health() {

boolean available = checkPaymentServiceConnectivity();

if (available) {

return Health.up()

.withDetail("paymentService", "Available")

.withDetail("connectionLatencyMs", 12)

.build();

}

return Health.down()

.withDetail("paymentService", "Unavailable")

.withDetail("failureErrorCode", 503)

.build();

}

private boolean checkPaymentServiceConnectivity() {

// Core network probe logic goes here

return true;

}

}

Actuator automatically scans the application context, registers this custom bean, and incorporates its output into the global /actuator/health endpoint payload.

### **Q9: Your external payment service is slow. Should its check be added to health?**

**Answer:** Yes, but it requires careful design to prevent performance bottlenecks. If an external service check is poorly optimized, it can degrade the performance of the entire health endpoint.

Follow these production best practices:

*   **Strict Network Timeouts:** Always configure explicit connection and read timeouts on the client integration layer so the check fails quickly rather than hanging.

*   **Lightweight Probes:** Avoid executing heavy or expensive API calls; instead, use lightweight operations like an options check or a dedicated ping endpoint.

*   **Result Caching:** Cache the health status response for a short duration (e.g., 10-15 seconds) so frequent monitoring requests don't hit the external service repeatedly.

*   **Probe Isolation:** Assign the check to the application's readiness group rather than its liveness group, ensuring that a slow external service won't accidentally trigger an unexpected container restart.


### **Q10: What is the difference between liveness and readiness?**

**Answer:** In containerized cloud environments like Kubernetes, application health is separated into two distinct lifecycles:

*   **Liveness (/actuator/health/liveness):** This probe determines whether the application's internal process is healthy and functioning correctly. If this probe fails, the orchestrator assumes the container has entered an unrecoverable deadlock or broken state and restarts it.

*   **Readiness (/actuator/health/readiness):** This probe determines whether the application is fully initialized and prepared to route live user traffic. If this probe fails, the orchestrator stops routing incoming requests to this instance but does _not_ restart the container.


Plaintext

Orchestrator Probes

├── Liveness Probe  ──> Is the process fundamentally healthy?   ──\[Fail\]──> Restart Container

└── Readiness Probe ──> Is the service ready to handle traffic? ──\[Fail\]──> Isolate / Halt Routing

### **Q11: Why should a database usually not be part of liveness?**

**Answer:** A database is an external structural dependency. If a database experiences a temporary network blip or outage, it degrades the application's ability to process data, but it does not mean the application process itself is broken.

If you include the database check in the liveness probe, a database outage will cause the liveness probe to fail across all your application instances. Kubernetes will respond by continuously restarting every container in the cluster:

Plaintext

Database Outage ──> Liveness Fails ──> Kubernetes Restarts All Containers ──> Startup Fails (No DB) ──> Constant Boot Loop

Since restarting the application does not fix the underlying database issue, this loop adds unnecessary strain to your system. Database health belongs in the **readiness** probe, allowing the application to gracefully stop accepting traffic until the database recovers.

### **Q12: Kubernetes keeps restarting an application during a database outage. What is wrong?**

**Answer:** This behavior usually occurs when an external dependency check, like a database connection validation, has been incorrectly included in the application's liveness probe.

To fix this issue:

1.  **Refactor the Probes:** Isolate your checks so that external network dependencies only impact the readiness group, while the liveness probe focuses purely on core internal state.

2.  **Enable Dedicated Probe Endpoints:** Ensure your configuration explicitly activates Spring Boot's native probe mappings:

3.  Properties


management.endpoint.health.probes.enabled=true

3.  **Adjust Orchestrator Thresholds:** Configure appropriate initial startup delays, execution periods, and failure thresholds on your Kubernetes deployment manifest to give your health checks enough breathing room.


### **Q13: Can health indicators be organized into groups?**

**Answer:** Yes. Actuator allows you to organize individual health indicators into logical groups. This helps you present different health views to different monitoring tools or orchestrators.

Properties

\# Enable explicit health probe groups

management.endpoint.health.probes.enabled=true

\# Custom Readiness Group containing internal readiness states plus external indicators

management.endpoint.health.group.readiness.include=readinessState,db,redis

\# Custom Liveness Group restricted strictly to the internal core process state

management.endpoint.health.group.liveness.include=livenessState

Using these groups ensures that Kubernetes can pull streamlined text responses for lifecycle probes, while your internal SRE tools can pull full diagnostic details from independent paths.

**Module 3: Metrics Scenarios**
-------------------------------

### **Q14: What is Micrometer’s role in Actuator?**

**Answer:** Micrometer acts as a vendor-neutral metrics collection facade for modern JVM applications, functioning much like SLF4J does for logging.

Plaintext

Spring Boot Core Instrumentation ──> Micrometer Facade API ──> MeterRegistry Implementation ──> Target Metrics Platform

Spring Boot Actuator uses Micrometer's core API to record application metrics. A chosen registry implementation then handles formatting and exporting that data into the specific syntax required by your monitoring backend (e.g., Prometheus, Datadog, or New Relic).

### **Q15: What are counters, gauges, and timers?**

**Answer:** Micrometer provides several core meter types to capture different kinds of metric data:

*   **Counter:** A metric that tracks a single, monotonically increasing value. Counters can only go up or reset to zero when the application restarts. Use them to track cumulative events, such as orders.completed or http.requests.total.

*   **Gauge:** A metric that represents a single value that can fluctuate up or down over time. Gauges capture instantaneous snapshots of system state, such as active thread counts, connection pool utilization, or current queue depths.

*   **Timer:** A metric designed to measure both the count and total duration of short-lived events. Timers automatically generate valuable latency distributions, including maximum times, total elapsed time, and mean operation durations.

*   **Distribution Summary:** A metric used to track the distribution of non-time events. It operates similarly to a timer but measures quantities like payload sizes, request byte counts, or batch processing sizes.


### **Q16: How do you create a custom business metric?**

**Answer:** You can build custom business metrics by injecting a MeterRegistry instance into your service bean and registering your chosen meter type using its fluent builder API.

Java

package com.example.payment.service;

import io.micrometer.core.instrument.Counter;

import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.stereotype.Service;

@Service

public class OrderService {

private final Counter ordersCreatedCounter;

public OrderService(MeterRegistry registry) {

// Constructing a dimensional metric counter with fixed metadata labels

this.ordersCreatedCounter = Counter.builder("orders.created")

.description("Tracks the cumulative count of successfully created orders")

.tag("region", "US-East")

.tag("channel", "Mobile-App")

.register(registry);

}

public void processCheckout() {

// Business execution logic goes here

ordersCreatedCounter.increment();

}

}

### **Q17: How do you measure method execution time?**

**Answer:** You can track method execution timings programmatically by wrapping your business logic with a Timer.Sample lifecycle block.

Java

package com.example.payment.service;

import io.micrometer.core.instrument.MeterRegistry;

import io.micrometer.core.instrument.Timer;

import org.springframework.stereotype.Service;

@Service

public class PaymentService {

private final MeterRegistry registry;

public PaymentService(MeterRegistry registry) {

this.registry = registry;

}

public void processPaymentTransaction() {

// Start the timing clock

Timer.Sample sample = Timer.start(registry);

try {

executePaymentGatewayCall();

} finally {

// Stop the clock and record the duration under a specific metric name

sample.stop(registry.timer("payment.processing.duration", "provider", "Stripe"));

}

}

private void executePaymentGatewayCall() {

// Core transaction operations go here

}

}

\[!NOTE\]

You can also use Micrometer's declarative @Timed annotation or Spring Boot's Observation API to automatically instrument execution timings across supported methods.

### **Q18: What is metric tag cardinality?**

**Answer:** Metric tag cardinality refers to the total number of unique combinations of metric names and tag values generated by your application. High cardinality is a common pitfall that can degrade system performance.

#### **The Anti-Pattern: Unbounded High Cardinality**

Java

// Danger: This creates a unique time-series entry for every single customer ID

registry.counter("orders.created", "customerId", customerId);

Using highly unique values—such as customer IDs, request UUIDs, email addresses, or timestamps—as metric tags creates an unbounded number of unique time-series entries. This can quickly consume significant JVM memory and overwhelm your downstream monitoring systems.

#### **The Pattern: Bounded Low Cardinality**

Keep your metric tags bounded by using a limited set of distinct values:

Java

registry.counter("orders.created", "status", "success", "tier", "premium");

### **Q19: CPU usage suddenly increases in production. How would you investigate?**

**Answer:** When troubleshooting a sudden CPU spike, you can use Actuator endpoints to systematically narrow down the cause:

1.  **Verify the Metrics:** Query /actuator/metrics/process.cpu.usage and system.cpu.usage to confirm the spike and track its trajectory.

2.  **Check Traffic Volumes:** Inspect /actuator/metrics/http.server.requests to see if a sudden surge in traffic or a spike in API latency correlates with the CPU increase.

3.  **Analyze Runtime Threads:** Take several thread dumps using /actuator/threaddump a few seconds apart. Look for threads stuck in RUNNABLE states, CPU-heavy looping tasks, or complex serialization operations.

4.  **Monitor Garbage Collection:** Check GC metrics (like jvm.gc.pause) to see if the CPU spike is actually caused by the JVM constantly running stop-the-world garbage collection cycles due to memory pressure.

5.  **Correlate with Recent Activity:** Match the timing of the CPU spike against recent deployment timelines, database connection pool usage, or external system latency trends.


### **Q20: Users report slow APIs, but CPU is normal. What do you inspect?**

**Answer:** Normal CPU usage alongside poor response times usually indicates that your application threads are blocked waiting on external I/O resources rather than being bound by computation limits.

To diagnose this, use Actuator to check the following areas:

*   **HTTP Server Request Durations:** Look at the maximum and average response times across your endpoints to pinpoint exactly which API paths are lagging.

*   **Database Connection Pools:** Check your connection pool utilization metrics. If your active connection count consistently matches your pool's maximum limit, requests are likely stalling while waiting for a database connection.

*   **Thread Starvation:** Review your thread state metrics. Look for high numbers of threads sitting in TIMED\_WAITING or BLOCKED states, which suggests they are waiting on locks or external network responses.

*   **External Service Latency:** Look for custom timers tracking outbound HTTP client dependencies, which can reveal slow downstream APIs or third-party gateways.


### **Q21: All database requests are waiting. Which metrics are useful?**

**Answer:** When diagnosing database connection issues with a connection pool like HikariCP, look for these specific instrumentation metrics:

*   **hikaricp.connections.active:** Tracks the number of connections currently in use by application threads.

*   **hikaricp.connections.idle:** Tracks available, unused connections waiting in the pool.

*   **hikaricp.connections.pending:** Tracks the number of application threads currently blocked and waiting to acquire a connection from the pool.

*   **hikaricp.connections.max:** Indicates the maximum total connection capacity configured for the pool.


#### **Diagnosing the Root Cause**

If your active connections match your maximum limit (active == max) and pending requests are stacking up (pending > 0), your connection pool is exhausted.

This symptom points to a few potential root causes: your application may be experiencing a surge in traffic, queries might be running slowly due to missing database indexes, or your code could be leaking connections by failing to close transactions properly.

**Module 4: Prometheus and Monitoring**
---------------------------------------

### **Q22: How do Prometheus and Grafana work with Actuator?**

**Answer:** Actuator works as part of a standard monitoring stack, with each tool handling a specific part of the observability lifecycle:

Plaintext

Telemetry Architecture Lifecycle

1. Actuator / Micrometer ──> Collects raw metrics in memory and exposes them via HTTP.

2. /actuator/prometheus  ──> Converts and presents those metrics in Prometheus text format.

3. Prometheus Engine     ──> Regularly scrapes the endpoint over the network and stores the data.

4. Grafana Dashboard     ──> Queries Prometheus to visualize performance trends over time.

5. Alertmanager          ──> Evaluates metrics against rules and sends alerts (e.g., via Slack).

\[!IMPORTANT\]

Spring Boot Actuator does _not_ store historical metric data. It maintains a real-time, rolling snapshot of metrics in memory, relying on external systems like Prometheus to handle long-term data storage and trend analysis.

### **Q23: /actuator/prometheus returns 404. What would you check?**

**Answer:** If you encounter a 404 Not Found error when accessing the Prometheus endpoint, verify the following configuration items:

1.  **Verify the Dependency:** Ensure the Prometheus registry module is present on your project's classpath:

2.  XML


io.micrometer

micrometer-registry-prometheus

3.  **Check Exposure Configurations:** Confirm that prometheus is explicitly included in your HTTP web exposure list:

4.  Properties


management.endpoints.web.exposure.include=health,prometheus

3.  **Verify the Port and Context Path:** Ensure you are targeting the correct management port and base path if your configuration separates management endpoints from your standard business APIs.


### **Q24: Prometheus can scrape locally but not from another server. Why?**

**Answer:** If your monitoring endpoint works locally but is unreachable from an external Prometheus server, the issue is typically caused by network or binding restrictions:

*   **Network Interface Binding:** Check your network configuration settings. If management.server.address is set to 127.0.0.1, the management server will only accept local loopback traffic and reject external requests. To allow external network access, change this to 0.0.0.0 or bind it to a specific network interface.

*   **Port Restrictions:** Ensure your network security groups, firewalls, or cloud security rules are configured to permit inbound TCP traffic on your application's management port (e.g., port 9090 or 8080).

*   **Kubernetes Network Policies:** If your application runs in a Kubernetes cluster, make sure your target Service manifest explicitly exposes your management port, and verify that your NetworkPolicies allow traffic from the Prometheus namespace.


**Module 5: Security Scenarios**
--------------------------------

### **Q25: Why is exposing every endpoint dangerous?**

**Answer:** Exposing all Actuator endpoints publicly without an authentication layer creates significant information disclosure risks by making internal system details visible to anyone:

*   **Environment Visibility (/env):** Can expose environment variables, configuration properties, system paths, or unmasked credentials.

*   **Internal Application Blueprint (/beans, /mappings):** Reveals your application's internal structure, class names, package designs, and complete API routing topology.

*   **Runtime Diagnostic Details (/threaddump, /heapdump):** Provides deep insight into runtime thread states. A heap dump is particularly sensitive, as it contains a full snapshot of JVM memory that can include unencrypted passwords, session keys, tokens, or personal user data.

*   **Dynamic Control Planes (/loggers):** Allows unauthorized users to alter your logging configurations, potentially allowing them to turn off logging entirely to hide malicious activity or enable heavy debug logging to exhaust system resources.


### **Q26: How should Actuator be secured in production?**

**Answer:** To secure your management endpoints in a production environment, use a defense-in-depth approach across multiple layers:

*   **Expose Only the Minimum Required:** Only expose the endpoints your monitoring infrastructure actually needs (e.g., management.endpoints.web.exposure.include=health,prometheus).

*   **Enforce Role-Based Access Control:** Use a security framework like Spring Security to require authentication and specific administrative roles (e.g., ROLE\_ACTUATOR\_ADMIN) for all non-public diagnostic endpoints.

*   **Isolate Network Ports:** Move your management endpoints to a dedicated internal port separate from your main business APIs, making it easier to isolate management traffic at the network level.

*   **Apply Network Filters:** Use network policies, firewalls, or ingress routing rules to restrict access to your management endpoints so they can only be reached by trusted internal servers or your monitoring infrastructure.

*   **Hide Detailed Health Statuses:** Restrict full health component details to authenticated sessions using management.endpoint.health.show-details=when-authorized.


### **Q27: How do you place Actuator on a separate port?**

**Answer:** You can isolate your management endpoints by assigning them to a dedicated port using the management.server.port property.

Properties

\# Main business API transport port configuration

server.port=8080

\# Isolated management endpoint transport port configuration

management.server.port=9090

With this configuration, your public business APIs handle user traffic on port 8080, while your internal management utilities run entirely on port 9090. This network separation allows you to block port 9090 at your public gateway while keeping it accessible to your internal monitoring systems.

### **Q28: How do you allow public health access but secure other endpoints?**

**Answer:** You can use Spring Security to create a dedicated configuration rule that allows public access to the /health endpoint while requiring authentication for all other management paths.

Java

package com.example.payment.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.web.SecurityFilterChain;

@Configuration

public class ActuatorSecurityConfig {

@Bean

public SecurityFilterChain securityFilterChainForActuator(HttpSecurity http) throws Exception {

http

// Isolate these security rules strictly to Actuator endpoints

.securityMatcher(EndpointRequest.toAnyEndpoint())

.authorizeHttpRequests(auth -> auth

// Allow anyone to check basic liveness and readiness health states

.requestMatchers(EndpointRequest.to("health")).permitAll()

// Require a secure administrative role for all other diagnostic endpoints

.anyRequest().hasRole("ACTUATOR\_ADMIN")

)

.httpBasic(Customizer.withDefaults());

return http.build();

}

}

### **Q29: Does sanitization make /env completely safe?**

**Answer:** No. Actuator applies sanitization rules to mask known sensitive keys (like properties containing "password", "secret", or "key"), but this is a secondary safety measure rather than a complete security boundary.

Sanitization can miss custom sensitive properties, and the endpoint may still reveal valuable details about your application's setup, environment profiles, and internal architecture. You should always combine sanitization with proper network controls and access restrictions rather than relying on it alone.

**Module 6: Debugging and Operations**
--------------------------------------

### **Q30: How can logging be changed without restarting?**

**Answer:** You can change log levels on the fly by sending a POST request to the /actuator/loggers endpoint, specifying the package path and target logging threshold in a JSON payload.

HTTP

POST /actuator/loggers/com.example.payment HTTP/1.1

Host: localhost:9090

Content-Type: application/json

{

"configuredLevel": "DEBUG"

}

This updates the target package's log level immediately in memory without requiring a restart. This is helpful for troubleshooting active incidents in production, but you should remember to restore the original log levels once you finish debugging to avoid performance degradation or filling up your disk space.

### **Q31: What is the difference between a thread dump and a heap dump?**

**Answer:** Thread dumps and heap dumps are used to analyze different types of application issues:

*   **Thread Dump (/actuator/threaddump):** Captures a snapshot of all active JVM threads, including their current execution states and execution stack traces. It is a lightweight text file used to diagnose concurrency deadlocks, blocked threads, or unexpected CPU spikes.

*   **Heap Dump (/actuator/heapdump):** Generates a full binary snapshot of all objects currently allocated in the JVM's heap memory. This file matches the size of your active memory footprint and is used to analyze memory leaks or track down out-of-memory errors using tools like the Eclipse Memory Analyzer (MAT).


### **Q32: Requests are hanging. How would you use a thread dump?**

**Answer:** If your application APIs are stalling or hanging, follow this process to analyze the behavior using thread dumps:

1.  **Collect Multiple Snapshots:** Generate 3 to 4 sequential thread dumps via /actuator/threaddump, waiting 5 to 10 seconds between each capture. Comparing multiple snapshots helps you see if your threads are making progress or completely stuck.

2.  **Identify Blocked Threads:** Look for large groups of threads sitting in BLOCKED or WAITING states.

3.  **Check Connection and I/O Bottlenecks:** Look at the stack traces to see if your worker threads are stalled waiting on external resources, such as long-running database queries, blocking network requests, or third-party APIs without proper timeouts.

4.  **Scan for Deadlocks:** Check the top of the thread dump file for explicit deadlock notifications, where two or more threads are permanently blocked waiting for each other to release locks.


### **Q33: The application has an OutOfMemoryError. Is /heapdump enough?**

**Answer:** Not always. When an application encounters a severe OutOfMemoryError, the JVM may become unstable or stop responding entirely, making it impossible for the Actuator web endpoint to process your request.

To ensure you can capture diagnostics during a memory failure, always supplement Actuator by configuring automated JVM fallback parameters at startup:

Plaintext

\-XX:+HeapDumpOnOutOfMemoryError

\-XX:HeapDumpPath=/var/log/dumps/jvm\_memory\_fault.hprof

This configuration ensures the JVM automatically writes a memory snapshot directly to storage the moment an out-of-memory error occurs, even if the application's web tier is completely unresponsive.

### **Q34: How can /conditions help debugging?**

**Answer:** The /actuator/conditions endpoint provides an evaluation report of all positive and negative auto-configuration decisions made by Spring Boot during startup.

Use this endpoint to troubleshoot issues such as:

*   Finding out why an expected auto-configured component (like a data source bean or security filter) failed to initialize.

*   Identifying which conditional rule failed (e.g., a missing class on the classpath or an unsatisfied environment property).

*   Confirming if a custom bean you defined caused an auto-configuration rule to gracefully back off.


### **Q35: How can /mappings help?**

**Answer:** The /actuator/mappings endpoint returns an indexed list of all registered HTTP request handling paths across your application controllers.

Use this endpoint to troubleshoot routing issues such as:

*   Diagnosing why a specific API route returns a 404 Not Found error by confirming its exact path and method registration.

*   Identifying conflicting URL patterns between multiple controllers.

*   Discovering unexpected endpoints registered by third-party libraries or internal starters.


**Module 7: Customization**
---------------------------

### **Q36: How do you create a custom Actuator endpoint?**

**Answer:** You can create a custom Actuator endpoint by adding the @Endpoint annotation to a Spring bean, and defining your operations using @ReadOperation, @WriteOperation, or @DeleteOperation.

Java

package com.example.payment.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;

import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component

@Endpoint(id = "orderstats")

public class OrderStatsEndpoint {

@ReadOperation

public Map getCustomSystemStatistics() {

return Map.of(

"activeTransactions", 142,

"dlqMessageCount", 3,

"maintenanceModeActive", false

);

}

}

To expose this custom endpoint, add its ID to your web exposure list:

Properties

management.endpoints.web.exposure.include=health,orderstats

You can then read its output by sending a request to:

Plaintext

GET http://localhost:8080/actuator/orderstats

### **Q37: Custom Actuator endpoint or REST controller?**

**Answer:** Choose between a custom Actuator endpoint and a standard REST controller based on the operational purpose of your logic:

*   **Use a Custom Actuator Endpoint:** For infrastructure management, system diagnostics, operational metrics, or administrative actions (like triggering cache evictions or toggling maintenance modes). This aligns your operations with Actuator's dedicated management port, security controls, and transport layer.

*   **Use a Standard REST Controller:** For core business functionality, user-facing features, or domain-specific APIs (such as handling checkouts, retrieving catalog items, or updating user profiles).


### **Q38: Can an Actuator endpoint modify application state?**

**Answer:** Yes. You can use @WriteOperation (which maps to HTTP POST) or @DeleteOperation (which maps to HTTP DELETE) to modify application state through an Actuator endpoint.

Because these operations alter runtime behavior, protect them using strict security controls:

*   Restrict access to authorized administrative roles.

*   Ensure all mutating operations are tracked in your audit logs.

*   Design operations to be idempotent whenever possible to prevent accidental duplicate execution.

*   Isolate the endpoint on an internal port away from public networks.


**Module 8: Architecture Questions**
------------------------------------

### **Q39: How does Actuator work internally?**

**Answer:** Actuator initializes and manages its lifecycle through a structured sequence during application startup:

1.  **Auto-Configuration Activation:** The presence of the Actuator starter triggers auto-configuration classes, initializing the core management infrastructure.

2.  **Conditional Parsing:** The framework evaluates conditional rules to build relevant indicator beans based on the libraries found on your classpath (e.g., creating a RedisHealthIndicator if a Redis client is active).

3.  **Endpoint Endpoint Discovery:** Component scanners locate all built-in and user-defined endpoints tagged with the @Endpoint annotation hierarchy.

4.  **Filter Rules Evaluation:** The system applies exposure rules to determine which enabled endpoints should be accessible over HTTP or JMX.

5.  **Route Mapping:** Exposed endpoints are mapped to network routes under a single base path using Spring MVC or WebFlux handlers.

6.  **Security Interception:** The application's security filter chain intercepts incoming requests to validate credentials and roles before allowing access to endpoint operations.


### **Q40: What is the performance impact of Actuator?**

**Answer:** The performance impact of Actuator is typically negligible, but poorly optimized configurations or heavy operational tasks can introduce resource overhead:

*   **High-Cardinality Tags:** Tracking unique, high-cardinality values in your custom metrics can consume excessive memory and slow down downstream monitoring systems.

*   **Heavy Custom Health Logic:** Adding slow, blocking, or un-timeouted network calls to your health indicators can make the entire health endpoint sluggish.

*   **Frequent Heap Dumps:** Triggering a heap dump pauses the JVM and can cause your application to temporarily stop responding, while also consuming significant disk space.

*   **Aggressive Metric Scraping:** Scraping large volumes of metrics at very high frequencies can add noticeable CPU overhead to your application.


**Module 9: Rapid-Fire Questions**
----------------------------------

### **Q41: Does Actuator replace application logs?**

**Answer:** No. Logs provide detailed, timestamped records of specific code events, whereas Actuator supplies aggregated metrics, system health states, and live diagnostic controls. They are complementary tools in your observability stack.

### **Q42: Does Actuator store historical metrics?**

**Answer:** No. Actuator tracks metrics as a real-time snapshot in memory. To analyze trends over time, you need to use a time-series database or monitoring platform like Prometheus to scrape and store that data.

### **Q43: Does Actuator provide a dashboard?**

**Answer:** No. Actuator exposes raw data via structured JSON or plain text. To view this information visually, you must integrate it with a dashboard tool like Grafana or Spring Boot Admin.

### **Q44: Can Actuator work without Spring MVC?**

**Answer:** Yes. Actuator works seamlessly with asynchronous, non-blocking environments using Spring WebFlux, and can also expose its endpoints purely over JMX without any web tier active.

### **Q45: Can Actuator monitor business metrics?**

**Answer:** Yes. You can inject Micrometer's MeterRegistry into your business services to track custom domain events like transaction volumes, checkout counts, or processing latencies.

### **Q46: Why might an endpoint not appear?**

**Answer:** An endpoint might be missing because it has been explicitly disabled, excluded from your exposure configuration, blocked by your security filters, or because its conditional dependencies were not found on the classpath.

### **Q47: Should /health always return HTTP 200?**

**Answer:** No. Actuator maps its health statuses directly to HTTP response codes. If your system health drops to a status like DOWN, the endpoint will typically return an HTTP 503 Service Unavailable response.

### **Q48: What is the best production endpoint configuration?**

**Answer:** There is no single universal configuration, but a secure production baseline is to expose only health, info, and prometheus over HTTP, while requiring strict authentication for any other diagnostics.

### **Q49: What is the strongest one-line interview summary for Actuator?**

**Answer:** "Spring Boot Actuator integrates production-ready monitoring, observability, and management features into an application by exposing raw system health, metrics, and diagnostics through secure HTTP or JMX endpoints."

### **Q50: What are the primary production principles for using Actuator?**

**Answer:** "Expose only what is necessary, require strict authentication for sensitive data, keep your health checks fast with proper timeouts, isolate liveness from readiness probes, and avoid high-cardinality tags in your custom metrics