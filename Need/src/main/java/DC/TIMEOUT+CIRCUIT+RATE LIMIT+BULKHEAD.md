Absolutely. These are **core resilience patterns** that are very important for a Senior/Lead Java + Spring Boot Microservices interview.

I’ll explain them using one realistic architecture throughout:

> **E-commerce application**
>
> `Order Service → Payment Service → Inventory Service → Notification Service`

Suppose the customer places an order. The Order Service calls Payment and Inventory. If one downstream service becomes slow or unavailable, we don't want the entire application to collapse.

The commonly used library in Spring Boot applications for these patterns is **Resilience4j**. It supports Circuit Breaker, Retry, Rate Limiter, Bulkhead and Time Limiter, and integrates with Spring Boot through annotations/configuration. ([resilience4j][1])

---

# 1. Why do we need resilience in Microservices?

Imagine this architecture:

```text
                  ┌─────────────────┐
                  │     Client      │
                  └────────┬────────┘
                           │
                           ▼
                  ┌─────────────────┐
                  │  Order Service  │
                  └───────┬─────────┘
                          │
              ┌───────────┴───────────┐
              │                       │
              ▼                       ▼
      ┌───────────────┐       ┌────────────────┐
      │Payment Service│       │Inventory Service│
      └───────────────┘       └────────────────┘
```

Now imagine Payment Service has a database problem.

Payment requests start taking:

```text
Normal:       100 ms
Problem:      10 seconds
```

Order Service keeps calling Payment.

Soon:

```text
100 Order requests
        ↓
100 Payment calls
        ↓
Payment is slow
        ↓
Order threads waiting
        ↓
Order thread pool exhausted
        ↓
Order Service becomes slow
        ↓
Other APIs affected
        ↓
Entire application degraded
```

This is called **cascading failure**.

Resilience patterns are designed to prevent this.

The major ones are:

```text
Retry
  ↓
Circuit Breaker
  ↓
Fallback
  ↓
Bulkhead
  ↓
Rate Limiter
  ↓
Timeout
```

They solve **different problems**.

---

# 2. The difference between all of them

This is one of the most important interview concepts.

| Pattern             | Main purpose                                        |
| ------------------- | --------------------------------------------------- |
| **Retry**           | Temporary failure → try again                       |
| **Circuit Breaker** | Repeated failure → stop calling service             |
| **Fallback**        | Failure → return alternative response               |
| **Bulkhead**        | Prevent one dependency from consuming all resources |
| **Rate Limiter**    | Limit number of requests                            |
| **Timeout**         | Don't wait forever                                  |

Think of a real ship:

```text
             SHIP
      ┌─────────────────┐
      │      A          │
      │                 │
      ├────────┬────────┤
      │   B    │   C    │
      │        │        │
      └────────┴────────┘
```

Bulkheads divide the ship into isolated compartments.

If compartment B gets flooded:

```text
B → flooded
```

C can continue working.

That's exactly what **Bulkhead** does in microservices.

---

# 3. Retry Mechanism

## What is Retry?

Retry means:

> "The downstream service failed temporarily. Let's try again."

Example:

```text
Order Service
     |
     | Request
     ▼
Payment Service
     |
     X Connection timeout
     |
     ▼
Retry
     |
     ▼
Payment Service
     |
     ✓ Success
```

### Real-world use case

Suppose Payment Service temporarily returns:

```text
HTTP 503 Service Unavailable
```

It may recover after 200 ms.

Instead of immediately failing:

```text
Request
   ↓
503
   ↓
FAIL
```

we can do:

```text
Request
   ↓
503
   ↓
wait 200ms
   ↓
Retry
   ↓
Success
```

---

# 4. Retry configuration

For a Spring Boot application using Resilience4j:

```yaml
resilience4j:
  retry:
    instances:

      paymentService:
        maxAttempts: 3
        waitDuration: 500ms
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2
```

This means:

```text
Attempt 1 → failure

wait 500 ms

Attempt 2 → failure

wait 1000 ms

Attempt 3 → failure

FAIL
```

Resilience4j supports `maxAttempts`, `waitDuration`, exponential backoff and exception-based retry configuration. ([resilience4j][1])

---

# 5. Retry in Java

```java
@Service
public class PaymentService {

    @Retry(name = "paymentService")
    public PaymentResponse makePayment(PaymentRequest request) {

        return paymentClient.processPayment(request);
    }
}
```

If the call fails with a configured retryable exception, Resilience4j retries it.

---

# 6. Which errors should you Retry?

This is extremely important.

### Good candidates

```text
Connection timeout
HTTP 503
Temporary network failure
Temporary database connectivity failure
```

### Usually don't retry

```text
HTTP 400
HTTP 401
HTTP 403
Invalid credit card
Insufficient balance
Invalid request
Business validation failure
```

For example:

```text
Payment failed because balance = ₹0
```

Retrying 3 times doesn't magically create money. 😄

---

# 7. Exponential Backoff

Never blindly retry immediately.

Bad:

```text
Failure
Retry immediately
Failure
Retry immediately
Failure
Retry immediately
```

This can overload the already struggling service.

Instead:

```text
Failure
 ↓
500 ms
 ↓
Retry
 ↓
1 sec
 ↓
Retry
 ↓
2 sec
 ↓
Retry
```

This is **exponential backoff**.

You can also add **jitter** so thousands of clients don't retry at exactly the same moment.

---

# 8. Circuit Breaker

Now let's move to the most important pattern.

## Problem with Retry

Suppose Payment Service is completely down.

You have:

```text
1000 requests/sec
```

Each request retries 3 times.

Now you may create:

```text
1000 × 3 = 3000 requests/sec
```

toward an already broken service.

That's dangerous.

This is where **Circuit Breaker** comes in.

---

# 9. Circuit Breaker concept

Circuit breaker works like an electrical circuit breaker.

Normal:

```text
Order
  |
  ▼
Payment
```

If Payment repeatedly fails:

```text
Order
  |
  X
Payment
```

The circuit opens.

Future calls don't even go to Payment.

```text
Order
  |
  X
Circuit Breaker
```

Instead, we immediately execute fallback.

---

# 10. Circuit Breaker states

There are **three primary states**.

```text
              failures
 CLOSED ─────────────────► OPEN
   ▲                         |
   |                         |
   |                         | wait
   |                         ▼
   └────────────── HALF OPEN
          success
```

## CLOSED

Everything is normal.

```text
Request
   ↓
Circuit Breaker
   ↓
Payment
   ↓
Success
```

Failures are monitored.

---

# 11. OPEN

Suppose:

```text
failureRateThreshold = 50%
```

and enough calls have been recorded.

If failure rate crosses 50%:

```text
CLOSED
   ↓
OPEN
```

Now:

```text
Request
   ↓
Circuit Breaker
   ↓
REJECT
   ↓
Fallback
```

No call reaches Payment.

---

# 12. HALF_OPEN

After a configured waiting period:

```text
OPEN
 ↓
wait 10 seconds
 ↓
HALF_OPEN
```

Circuit Breaker allows a small number of test requests.

For example:

```text
permittedNumberOfCallsInHalfOpenState = 3
```

Then:

```text
Request 1 → Payment → success
Request 2 → Payment → success
Request 3 → Payment → success
```

Circuit becomes:

```text
HALF_OPEN → CLOSED
```

If they fail:

```text
HALF_OPEN → OPEN
```

---

# 13. Circuit Breaker configuration

```yaml
resilience4j:
  circuitbreaker:
    instances:

      paymentService:

        registerHealthIndicator: true

        slidingWindowType: COUNT_BASED

        slidingWindowSize: 10

        minimumNumberOfCalls: 5

        failureRateThreshold: 50

        waitDurationInOpenState: 10s

        permittedNumberOfCallsInHalfOpenState: 3
```

Let's understand each.

### `slidingWindowSize`

```yaml
slidingWindowSize: 10
```

Circuit Breaker looks at the latest 10 calls.

Example:

```text
✓ ✓ X X X X X ✓ ✓ X
```

Failures:

```text
6 / 10 = 60%
```

If:

```text
failureRateThreshold = 50
```

Circuit opens.

---

# 14. `minimumNumberOfCalls`

```yaml
minimumNumberOfCalls: 5
```

This prevents the circuit from opening based on only one or two calls.

Example:

```text
First call → failure
```

You don't want:

```text
1 failure / 1 request = 100%
```

to immediately open the circuit.

---

# 15. Circuit Breaker Java code

```java
@Service
public class OrderService {

    @CircuitBreaker(
        name = "paymentService",
        fallbackMethod = "paymentFallback"
    )
    public PaymentResponse processPayment(
            PaymentRequest request) {

        return paymentClient.processPayment(request);
    }

    private PaymentResponse paymentFallback(
            PaymentRequest request,
            Exception ex) {

        return PaymentResponse.pending(
                "Payment service temporarily unavailable"
        );
    }
}
```

So:

```text
processPayment()
       |
       ▼
CircuitBreaker
       |
       ▼
Payment Service
       |
    failure
       |
       ▼
paymentFallback()
```

---

# 16. Fallback Mechanism

Fallback means:

> "If the primary operation fails, execute an alternative operation."

Example:

```text
Get Product Details
       |
       ▼
Product Service
       |
       X
       |
       ▼
Fallback
       |
       ▼
Redis Cache
```

If Product Service is unavailable:

```text
Product Service → unavailable
```

we can return cached product data.

---

# 17. Real-world fallback examples

### Product Service

Primary:

```text
Product DB
```

Fallback:

```text
Redis cache
```

---

### Recommendation Service

Primary:

```text
ML Recommendation Service
```

Fallback:

```text
Popular products
```

---

### Pricing Service

Primary:

```text
Pricing Service
```

Fallback:

```text
Last known price
```

---

### Notification Service

Primary:

```text
Email Service
```

Fallback:

```text
Kafka
```

Instead of:

```text
API → Email Service → failure → API fails
```

we can do:

```text
API
 ↓
Kafka
 ↓
Return success
```

Notification can be processed asynchronously later.

---

# 18. Important distinction

Fallback is **not** the same as Circuit Breaker.

Circuit Breaker says:

> "Don't call the failing service."

Fallback says:

> "What should I return/do when the operation fails?"

They are often used together.

```text
Circuit Breaker
       |
       X
       |
    Fallback
```

---

# 19. Bulkhead

Now let's understand one of the most important production patterns.

Imagine Order Service has:

```text
100 threads
```

And it calls:

```text
Payment
Inventory
Recommendation
Notification
```

Suppose Recommendation Service becomes extremely slow.

```text
Recommendation
     ↓
10 sec response
```

100 threads might become:

```text
Thread 1 → Recommendation
Thread 2 → Recommendation
Thread 3 → Recommendation
...
Thread 100 → Recommendation
```

Now:

```text
Order Service
Thread Pool
     ↓
100 threads occupied
     ↓
NO THREADS AVAILABLE
```

Even though Payment and Inventory are healthy.

This is a **resource exhaustion / cascading failure** problem.

---

# 20. Bulkhead solves this

We isolate resources.

```text
Order Service

Payment       → 20 concurrent calls
Inventory     → 20 concurrent calls
Recommendation → 10 concurrent calls
Notification  → 10 concurrent calls
```

Now if Recommendation fails:

```text
Recommendation → 10 calls blocked
```

Payment can still use:

```text
20 threads
```

This is the Bulkhead pattern.

---

# 21. Semaphore Bulkhead

The simplest form:

```yaml
resilience4j:
  bulkhead:
    instances:

      recommendationService:
        maxConcurrentCalls: 10
        maxWaitDuration: 100ms
```

Meaning:

```text
Maximum concurrent requests = 10
```

Request 11 may be rejected if capacity isn't available within the configured wait duration.

Resilience4j supports both semaphore bulkheads and thread-pool bulkheads. ([resilience4j][1])

---

# 22. Java Bulkhead

```java
@Bulkhead(
    name = "recommendationService",
    type = Bulkhead.Type.SEMAPHORE
)
public RecommendationResponse getRecommendations(
        String customerId) {

    return recommendationClient
            .getRecommendations(customerId);
}
```

Imagine:

```text
Requests = 100

Bulkhead capacity = 10
```

Then only 10 concurrent requests enter.

The others are rejected/wait according to configuration.

---

# 23. Thread Pool Bulkhead

Another approach is:

```text
Main application threads
          |
          ▼
   Thread Pool Bulkhead
          |
     ┌────┴─────┐
     │          │
 Thread 1    Thread 2
     │          │
     └────┬─────┘
          ▼
 Recommendation
```

Configuration can define:

```text
coreThreadPoolSize
maxThreadPoolSize
queueCapacity
```

Spring Cloud's Resilience4j integration supports semaphore and fixed-thread-pool bulkheads. ([Home][2])

---

# 24. Rate Limiter

Rate Limiter answers:

> "How many requests are allowed during a given period?"

Example:

```text
100 requests / second
```

If 101st request arrives:

```text
REJECT
```

This is useful for:

* Public APIs
* Login APIs
* Payment APIs
* Expensive APIs
* Third-party APIs
* Preventing abuse

---

# 25. Real-world example

Suppose you call an external payment provider.

Their contract says:

```text
Maximum 100 requests/sec
```

Your application receives:

```text
500 requests/sec
```

Without Rate Limiter:

```text
Your Service
    |
    ├── 500 requests
    ↓
Payment Provider
    |
    X
Rate limit exceeded
```

With Rate Limiter:

```text
Your Service
    |
    ▼
Rate Limiter
    |
    ├── 100 → Payment Provider
    |
    └── 400 → rejected/throttled
```

---

# 26. Rate Limiter configuration

```yaml
resilience4j:
  ratelimiter:
    instances:

      paymentProvider:
        limitForPeriod: 100
        limitRefreshPeriod: 1s
        timeoutDuration: 0
```

Meaning:

```text
100 requests
per
1 second
```

Resilience4j's rate limiter configuration includes `limitForPeriod`, `limitRefreshPeriod` and `timeoutDuration`. ([resilience4j][1])

---

# 27. Java Rate Limiter

```java
@RateLimiter(name = "paymentProvider")
public PaymentResponse makePayment(
        PaymentRequest request) {

    return paymentClient.process(request);
}
```

---

# 28. Rate Limiter vs Bulkhead

This is a common interview question.

### Rate Limiter

Controls:

> **How many requests can enter over time?**

Example:

```text
100 requests / second
```

### Bulkhead

Controls:

> **How many operations can execute concurrently?**

Example:

```text
Maximum 10 concurrent calls
```

Think:

```text
Rate Limiter
     ↓
100 requests/sec

Bulkhead
     ↓
10 concurrent executions
```

You can use both.

---

# 29. Retry vs Circuit Breaker

Another common interview question.

### Retry

Used for:

```text
Temporary failures
```

Example:

```text
Service temporarily unavailable
```

### Circuit Breaker

Used for:

```text
Persistent failures
```

Example:

```text
Payment service is down for 5 minutes
```

Together:

```text
Request
   ↓
Retry
   ↓
Failure
   ↓
Circuit Breaker detects repeated failures
   ↓
OPEN
   ↓
Fallback
```

But be careful with ordering and retry amplification. Resilience4j's Spring AOP documentation defines an aspect ordering and allows the order to be customized; the documented default nesting is `Retry(CircuitBreaker(RateLimiter(TimeLimiter(Bulkhead(Function)))))`. ([resilience4j][1])

---

# 30. Timeout

You didn't explicitly mention Timeout, but **you should understand it along with these patterns**.

Suppose:

```text
Order → Payment
```

Payment doesn't respond.

Without timeout:

```text
Order Thread
    |
    | waiting...
    |
    | waiting...
    |
    | waiting...
    |
    | waiting...
```

Eventually resources are exhausted.

With timeout:

```text
Order
  |
  | request
  ▼
Payment
  |
  | 2 seconds
  |
  X TIMEOUT
  |
  ▼
Fallback
```

Example:

```yaml
resilience4j:
  timelimiter:
    instances:
      paymentService:
        timeoutDuration: 2s
        cancelRunningFuture: true
```

Resilience4j supports TimeLimiter configuration alongside Circuit Breaker and the other resilience mechanisms. ([resilience4j][1])

---

# 31. Putting everything together

This is the architecture I would recommend understanding for a Senior/Lead interview:

```text
                       Client
                         |
                         ▼
                   API Gateway
                         |
                         ▼
                  ┌─────────────┐
                  │Order Service│
                  └──────┬──────┘
                         |
                 ┌───────┴────────┐
                 │                │
                 ▼                ▼
             Payment          Inventory
              Service           Service
                 |
                 ▼
          External Payment
             Provider
```

Now protect the Payment call:

```text
                         Order Service
                              |
                              ▼
                       Rate Limiter
                              |
                              ▼
                          Bulkhead
                              |
                              ▼
                         TimeLimiter
                              |
                              ▼
                       Circuit Breaker
                              |
                              ▼
                            Retry
                              |
                              ▼
                    Payment Service
                              |
                     ┌────────┴────────┐
                     │                 │
                   SUCCESS           FAILURE
                                       |
                                       ▼
                                   Fallback
```

The exact nesting/order should be chosen deliberately rather than blindly stacking annotations; Resilience4j exposes configuration for aspect order. ([resilience4j][1])

---

# 32. A realistic Spring Boot configuration

Here's a useful interview-level `application.yml`:

```yaml
resilience4j:

  circuitbreaker:

    instances:

      paymentService:

        registerHealthIndicator: true

        slidingWindowType: COUNT_BASED

        slidingWindowSize: 10

        minimumNumberOfCalls: 5

        failureRateThreshold: 50

        waitDurationInOpenState: 10s

        permittedNumberOfCallsInHalfOpenState: 3


  retry:

    instances:

      paymentService:

        maxAttempts: 3

        waitDuration: 500ms

        enableExponentialBackoff: true

        exponentialBackoffMultiplier: 2


  bulkhead:

    instances:

      paymentService:

        maxConcurrentCalls: 20

        maxWaitDuration: 100ms


  ratelimiter:

    instances:

      paymentService:

        limitForPeriod: 100

        limitRefreshPeriod: 1s

        timeoutDuration: 0


  timelimiter:

    instances:

      paymentService:

        timeoutDuration: 2s

        cancelRunningFuture: true
```

These property names and instance-based configuration are supported by the Resilience4j Spring Boot integration. ([resilience4j][1])

---

# 33. Dependencies

For Spring Boot 3, Resilience4j provides a dedicated Spring Boot 3 starter. The starter expects Actuator and AOP; reactive applications may additionally need the Reactor module. ([resilience4j][1])

For example:

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

For WebFlux:

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-reactor</artifactId>
</dependency>
```

---

# 34. Complete Java example

```java
@Service
public class PaymentService {

    @RateLimiter(name = "paymentService")
    @Bulkhead(
        name = "paymentService",
        type = Bulkhead.Type.SEMAPHORE
    )
    @CircuitBreaker(
        name = "paymentService",
        fallbackMethod = "paymentFallback"
    )
    @Retry(name = "paymentService")
    public PaymentResponse processPayment(
            PaymentRequest request) {

        return paymentClient.process(request);
    }


    private PaymentResponse paymentFallback(
            PaymentRequest request,
            Exception ex) {

        return PaymentResponse.pending(
            "Payment is temporarily unavailable"
        );
    }
}
```

Conceptually:

```text
Client
  |
  ▼
Rate Limiter
  |
  ▼
Bulkhead
  |
  ▼
Circuit Breaker
  |
  ▼
Retry
  |
  ▼
Payment Service
  |
  ├──────────────► SUCCESS
  |
  └──────────────► FAILURE
                       |
                       ▼
                    Fallback
```

---

# 35. Real production scenario

Let's take a real e-commerce example.

Customer clicks:

**"Place Order"**

The flow is:

```text
POST /orders
       |
       ▼
Order Service
       |
       ├──────────────► Inventory Service
       |
       └──────────────► Payment Service
                              |
                              ▼
                       Payment Provider
```

Now Payment Provider starts having issues.

### First request

```text
Payment → 503
```

Retry:

```text
Retry #1 → 503
Retry #2 → 503
```

Circuit Breaker records failures.

---

### More requests

```text
Request 2 → failure
Request 3 → failure
Request 4 → failure
Request 5 → failure
```

Failure percentage crosses threshold.

Circuit:

```text
CLOSED
   ↓
OPEN
```

Now:

```text
Request 6
   |
   ▼
Circuit Breaker
   |
   X
Payment Provider
```

No external call happens.

Fallback:

```text
Payment status = PENDING
```

Order can potentially be stored as:

```text
ORDER_CREATED
PAYMENT_PENDING
```

and an event can be published:

```text
OrderCreated
      |
      ▼
    Kafka
      |
      ▼
Payment Worker
```

When Payment Provider recovers, the payment can be processed asynchronously.

This is much better than making every customer wait for a broken dependency.

---

# 36. How these patterns work together

A good mental model is:

```text
                    Incoming Request
                           |
                           ▼
                    ┌─────────────┐
                    │Rate Limiter │
                    └──────┬──────┘
                           |
                           ▼
                    ┌─────────────┐
                    │  Bulkhead   │
                    └──────┬──────┘
                           |
                           ▼
                    ┌─────────────┐
                    │CircuitBreaker│
                    └──────┬──────┘
                           |
                           ▼
                       Retry
                           |
                           ▼
                       Timeout
                           |
                           ▼
                    Downstream API
                           |
                     ┌─────┴─────┐
                     │           │
                   Success      Failure
                     │           │
                     ▼           ▼
                  Response     Fallback
```

---

# 37. When NOT to use each pattern

This is what separates a senior developer from someone who just knows annotations.

### Don't blindly use Retry

Bad:

```text
POST /payment
```

If the operation isn't safely idempotent, retrying can potentially create duplicate effects.

For example:

```text
Payment succeeds
Response is lost
Client retries
Payment happens again
```

Use **idempotency keys** for payment/order-like operations.

---

### Don't blindly use Circuit Breaker

A Circuit Breaker isn't a replacement for fixing a broken service.

It is a **failure-containment mechanism**.

---

### Don't blindly use Fallback

Don't return fake data.

Bad:

```text
Payment failed
↓
Return "Payment successful"
```

Very dangerous.

Better:

```text
Payment unavailable
↓
PAYMENT_PENDING
```

---

### Don't set Bulkhead too low

If:

```text
maxConcurrentCalls = 2
```

but you normally receive:

```text
100 requests/sec
```

you may artificially throttle your own system.

Capacity should be based on load testing and downstream limits.

---

### Don't use Rate Limiter without understanding the requirement

Rate limiting is useful for:

```text
API protection
third-party quotas
abuse prevention
traffic shaping
```

but it isn't a substitute for capacity planning.

---

# 38. Very important interview question

### "What happens if the downstream service is slow?"

Strong answer:

> "I would first establish a timeout so that calls don't wait indefinitely. For transient failures I would use a bounded retry with exponential backoff and jitter. If failures continue, the Circuit Breaker should open and prevent further calls. A fallback can return a degraded but semantically correct response where possible. Bulkhead isolation prevents that dependency from consuming all application resources, while a Rate Limiter controls request volume and protects either our service or an external dependency."

That's a **Lead/Senior-level answer**.

---

# 39. Another interview question

### "Retry vs Circuit Breaker?"

Answer:

> Retry handles **temporary failures** by attempting the operation again. Circuit Breaker handles **persistent failures** by stopping calls to an unhealthy dependency for a period of time. In production, they are often combined, but retries must be bounded because excessive retries can amplify traffic and make an outage worse.

---

# 40. Another important question

### "Bulkhead vs Rate Limiter?"

Answer:

> Bulkhead limits **concurrent resource usage**, while Rate Limiter limits **request frequency**.

Example:

```text
Rate Limiter:
100 requests / second

Bulkhead:
20 concurrent requests
```

Both can be used together.

---

# 41. One-line memory trick

Remember these five words:

```text
RETRY       → Try again
CIRCUIT     → Stop calling
FALLBACK    → Alternative response
BULKHEAD    → Isolate resources
RATE LIMIT  → Control traffic
TIMEOUT     → Don't wait forever
```

And the overall production strategy:

```text
                 RESILIENCE
                     |
       ┌─────────────┼──────────────┐
       │             │              │
    Protect       Recover        Control
       │             │              │
   Bulkhead       Fallback      RateLimiter
   Timeout        Retry
                  CircuitBreaker
```

For your **Evernorth Software Engineering Lead Analyst interview**, I would especially prepare the next level: **how these patterns behave under real production failures, how to choose their configuration values, how they interact with Kafka, Kubernetes/EKS, API Gateway, Redis, WebFlux, and database failures, and how to monitor them with Actuator + Prometheus/Grafana**. That is where interviewers typically move after asking the basic definitions.

[1]: https://resilience4j.readme.io/docs/getting-started-3?utm_source=chatgpt.com "Getting Started"
[2]: https://docs.spring.io/spring-cloud/docs/current/reference/htmlsingle/?utm_source=chatgpt.com "Spring Cloud"
