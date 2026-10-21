These are **very strong production-level interview questions**. For a Senior Java/Spring Boot interview, don't answer only with definitions—explain **how you would diagnose the problem in production and what you would change**.

# 🔍 Production-Level Spring/JPA & Performance — Questions 26–30

---

# 26. How do you detect and resolve N+1 queries in a production application?

## What is N+1?

N+1 happens when Hibernate/JPA executes:

```text
1 query → fetch parent records
N queries → fetch related records one-by-one
```

Example:

```java
List<Order> orders = orderRepository.findAll();

for (Order order : orders) {
    System.out.println(order.getCustomer().getName());
}
```

Suppose there are 100 orders.

You may get:

```sql
SELECT * FROM orders;
```

followed by:

```sql
SELECT * FROM customer WHERE id = 1;
SELECT * FROM customer WHERE id = 2;
SELECT * FROM customer WHERE id = 3;
...
SELECT * FROM customer WHERE id = 100;
```

Total:

```text
1 + 100 = 101 queries
```

That's N+1.

---

## How do you detect it?

### 1. Check application/database metrics

Look at:

```text
Database query count
DB latency
Connection pool usage
DB CPU
Slow query logs
```

A request that normally executes 5 SQL queries suddenly executing 500 is a strong signal.

---

### 2. Enable Hibernate SQL logging in non-production

For example:

```properties
spring.jpa.show-sql=true
```

Or better, use proper Hibernate logging/configuration and SQL observability rather than leaving verbose SQL logging permanently enabled in production.

You can also use tools such as:

```text
Hibernate Statistics
p6spy
Datasource Proxy
APM tools
```

---

### 3. Check distributed traces

With tools such as:

```text
OpenTelemetry
Jaeger
Datadog
New Relic
Dynatrace
```

you may see:

```text
HTTP request
   |
   ├── SELECT orders
   ├── SELECT customer
   ├── SELECT customer
   ├── SELECT customer
   ├── SELECT customer
   └── ...
```

That's a very strong indication of N+1.

---

# How do you fix it?

There isn't one universal fix. Choose based on the use case.

## Solution 1 — `JOIN FETCH`

```java
@Query("""
    SELECT o
    FROM Order o
    JOIN FETCH o.customer
""")
List<Order> findOrdersWithCustomer();
```

Instead of:

```text
1 + N queries
```

you can get:

```text
1 query
```

---

## Solution 2 — Entity Graph

```java
@EntityGraph(attributePaths = {"customer"})
List<Order> findAll();
```

This is often cleaner than putting `JOIN FETCH` everywhere.

---

## Solution 3 — DTO projection

For APIs, this is often one of the best approaches.

Instead of loading the entire entity graph:

```java
@Query("""
    SELECT new com.example.OrderSummary(
        o.id,
        c.name,
        o.total
    )
    FROM Order o
    JOIN o.customer c
""")
List<OrderSummary> findOrderSummaries();
```

You fetch only the data required by the API.

---

## Important production warning

Don't blindly solve N+1 by using:

```java
JOIN FETCH
```

for every relationship.

You can create:

```text
Huge joins
Duplicate rows
Large result sets
High memory consumption
Slow DB queries
Pagination problems
```

### Interview answer

> I first detect N+1 using SQL query counts, Hibernate statistics, database monitoring and distributed traces. Then I inspect the entity relationships and access patterns. Depending on the use case, I use `JOIN FETCH`, `@EntityGraph`, or DTO projections. For production APIs, I prefer fetching exactly the data required rather than eagerly loading the entire entity graph.

---

# 27. Optimistic vs pessimistic locking: when would you use each?

Both solve the problem of **concurrent updates**.

Suppose two users edit the same order.

```text
User A → Order 100
User B → Order 100
```

Both read:

```text
status = PENDING
```

Then both attempt to update it.

Without concurrency control:

```text
A updates
B updates
```

One update may overwrite the other.

---

# Optimistic Locking

Optimistic locking assumes:

> Conflicts are relatively rare.

JPA provides this using `@Version`.

```java
@Entity
public class Order {

    @Id
    private Long id;

    @Version
    private Long version;

    private String status;
}
```

Suppose:

```text
Database

id = 100
version = 5
```

User A reads:

```text
version = 5
```

User B also reads:

```text
version = 5
```

A updates:

```sql
UPDATE orders
SET status = 'APPROVED',
    version = 6
WHERE id = 100
AND version = 5;
```

Success.

Now B attempts:

```sql
UPDATE orders
SET status = 'CANCELLED',
    version = 6
WHERE id = 100
AND version = 5;
```

No row matches because version is already 6.

Hibernate throws an optimistic locking exception such as:

```text
OptimisticLockException
```

---

## When should you use optimistic locking?

Good for:

```text
High-read systems
Low/moderate write contention
Web applications
Order/customer/profile updates
Long-running user interactions
```

Advantages:

```text
No database lock held while user is thinking
High concurrency
Good scalability
```

---

# Pessimistic Locking

Pessimistic locking assumes:

> Conflicts are likely, so lock the database row.

Example:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT o FROM Order o WHERE o.id = :id")
Order findForUpdate(Long id);
```

Conceptually:

```sql
SELECT *
FROM orders
WHERE id = ?
FOR UPDATE;
```

The database locks the row.

Another transaction trying to acquire the same lock may have to wait.

---

## When would you use pessimistic locking?

Useful when:

```text
High write contention
Critical resource allocation
Inventory decrement
Financial/account operations
Seat/resource allocation
```

Example:

```text
Inventory = 1

Request A → lock row → decrement
Request B → waits
```

This can prevent two concurrent requests from allocating the same resource.

---

# Optimistic vs Pessimistic

|               | Optimistic              | Pessimistic                   |
| ------------- | ----------------------- | ----------------------------- |
| Assumption    | Conflicts are rare      | Conflicts are likely          |
| Mechanism     | Version/check           | DB lock                       |
| Blocking      | Usually no              | Yes                           |
| Concurrency   | Higher                  | Lower                         |
| Deadlock risk | Lower                   | Higher                        |
| Performance   | Good with low conflicts | Can degrade under contention  |
| Typical use   | Web CRUD                | Inventory/resource allocation |

### Interview answer

> I prefer optimistic locking when contention is low and scalability is important. I use pessimistic locking when the same resource is highly contended and preventing concurrent modification is more important than maximum concurrency.

---

# 28. `LazyInitializationException`: what causes it, and what are 3 production-safe solutions?

This is another very common Hibernate question.

Suppose:

```java
@Entity
public class Order {

    @OneToMany(fetch = FetchType.LAZY)
    private List<OrderItem> items;
}
```

Service:

```java
@Transactional
public Order getOrder(Long id) {

    return repository.findById(id).orElseThrow();
}
```

The transaction ends.

Later:

```java
order.getItems();
```

Hibernate tries to load:

```text
Order → items
```

but the Hibernate session/entity manager is already closed.

Result:

```text
LazyInitializationException
```

---

## Why does this happen?

The relationship is:

```text
LAZY
 ↓
Load only when accessed
```

But when accessed:

```text
Persistence Context → CLOSED
```

Hibernate cannot execute the SQL needed to initialize the collection.

---

# Solution 1 — Fetch what you need inside the transaction

Use:

```java
@Transactional
public OrderResponse getOrder(Long id) {

    Order order = repository.findById(id)
            .orElseThrow();

    order.getItems().size();

    return mapToResponse(order);
}
```

But **don't recommend this as your primary design** just to force lazy loading.

A better approach is explicitly fetching the required data.

---

# Solution 2 — `JOIN FETCH` / `EntityGraph`

Example:

```java
@Query("""
    SELECT o
    FROM Order o
    JOIN FETCH o.items
    WHERE o.id = :id
""")
Optional<Order> findOrderWithItems(Long id);
```

Now the required relationship is loaded as part of the repository query.

This makes the data-access requirement explicit.

---

# Solution 3 — DTO projection

For REST APIs, this is often the cleanest approach.

```java
@Query("""
    SELECT new com.example.OrderResponse(
        o.id,
        o.status,
        i.productName,
        i.quantity
    )
    FROM Order o
    JOIN o.items i
    WHERE o.id = :id
""")
List<OrderResponse> findOrderResponse(Long id);
```

You fetch exactly what the API needs.

---

## What about `Open Session in View`?

You may hear:

```text
spring.jpa.open-in-view=true
```

This can keep the persistence context available through the web request and can mask lazy-loading problems.

However, I wouldn't present enabling OSIV as the preferred fix for a production architecture.

Why?

Because it can lead to:

```text
Controller
   ↓
Lazy loading
   ↓
Unexpected DB queries
```

and potentially database access during response serialization.

### Interview answer

> `LazyInitializationException` occurs when a lazy association is accessed after the persistence context is closed. My preferred solutions are to fetch the required association explicitly using `JOIN FETCH` or `@EntityGraph`, or use DTO projections. I avoid relying on Open Session in View as a general solution because it can hide inefficient database access.

---

# 29. A transaction unexpectedly commits instead of rolling back. What are your first 3 checks?

This is a **production debugging question**.

Suppose:

```java
@Transactional
public void createOrder() {

    orderRepository.save(order);

    throw new RuntimeException("Something failed");
}
```

But the database changes remain committed.

I would check these **three things first**.

---

## Check 1 — Is the exception actually reaching the transaction interceptor?

Look for:

```java
try {
    service.createOrder();
} catch (Exception e) {
    log.error("Failed", e);
}
```

If the exception is swallowed inside the transactional method:

```java
@Transactional
public void createOrder() {

    try {
        saveOrder();
        throw new RuntimeException();
    } catch (Exception e) {
        log.error("Error", e);
    }
}
```

Spring sees:

```text
Method returned normally
```

Therefore:

```text
COMMIT
```

---

## Check 2 — Is the exception a rollback-triggering exception?

By default:

```text
RuntimeException → rollback
Error            → rollback
Checked Exception → normally no rollback
```

If you have:

```java
throw new IOException();
```

use:

```java
@Transactional(
    rollbackFor = IOException.class
)
```

---

## Check 3 — Is the `@Transactional` method actually being intercepted?

Check for:

```text
Self-invocation
Private method
Object created with new
Non-Spring-managed object
Wrong transaction manager
```

For example:

```java
public void process() {
    this.save();
}

@Transactional
public void save() {
}
```

The call bypasses the proxy.

---

## My production debugging sequence

I'd inspect:

```text
1. Exception propagation
       ↓
2. Exception type / rollback rules
       ↓
3. Spring proxy / transaction manager
       ↓
4. Transaction logs
       ↓
5. Database transaction behavior
```

Enable appropriate transaction logging in a controlled environment to confirm whether Spring actually logged:

```text
Creating transaction
Committing transaction
Rolling back transaction
```

### Interview answer

> My first three checks are: whether the exception escapes the transactional method, whether the exception matches Spring's rollback rules, and whether the method is actually being invoked through the Spring transactional proxy. Self-invocation and caught exceptions are especially common causes.

---

# 30. p99 latency suddenly doubles while CPU remains stable. What metrics and dependencies do you investigate first?

This is a **real production incident question**.

Suppose:

```text
Before:
p99 = 200 ms

Now:
p99 = 400 ms

CPU = 40%
```

CPU isn't the bottleneck.

I would immediately investigate **I/O and downstream dependencies**.

---

# Step 1 — Check distributed tracing

Look at a slow request:

```text
API
 |
 ├── DB → 250 ms
 ├── Redis → 10 ms
 ├── Kafka → 5 ms
 └── HTTP Payment → 100 ms
```

Compare it with a normal request:

```text
API
 |
 ├── DB → 50 ms
 ├── Redis → 10 ms
 ├── Kafka → 5 ms
 └── HTTP Payment → 20 ms
```

Immediately the DB or downstream HTTP service becomes suspicious.

---

# Step 2 — Database metrics

Check:

```text
Query latency
Slow queries
Connection pool usage
Active connections
Connection acquisition time
Lock wait time
Deadlocks
Database CPU
Database I/O
Buffer/cache hit ratio
```

A particularly important distinction:

```text
Query execution = 50 ms
Connection acquisition = 300 ms
```

The database query itself isn't necessarily slow.

Your **connection pool may be exhausted**.

---

# Step 3 — External dependencies

Check:

```text
HTTP downstream latency
HTTP timeout rate
5xx rate
Connection pool utilization
DNS latency
TLS connection establishment
Retry count
Circuit breaker state
```

For example:

```text
Service A
   ↓
Service B
   ↓
Service C
```

If C suddenly becomes slow:

```text
C latency ↑
     ↓
B latency ↑
     ↓
A p99 latency ↑
```

Your CPU can remain perfectly normal.

---

# Step 4 — Thread pools

Check:

```text
Tomcat/Netty threads
ExecutorService
ForkJoinPool
Kafka consumer threads
Task queue depth
Rejected tasks
Active threads
```

Example:

```text
Thread pool:
max = 200
active = 200
queue = 5000
```

Your CPU might be only 30%, but requests are waiting in queues.

---

# Step 5 — Connection pools

For Spring Boot applications using HikariCP:

```text
Active connections
Idle connections
Pending threads
Connection acquisition time
Max pool size
```

Example:

```text
MaximumPoolSize = 50

Active = 50
Idle = 0
Pending = 100
```

This can produce huge latency even with low application CPU.

---

# Step 6 — Retries

This is often overlooked.

Suppose:

```text
Normal:

Request
 ↓
Service B
```

But now Service B is failing:

```text
Request
 ↓
B → timeout
 ↓
retry
 ↓
B → timeout
 ↓
retry
 ↓
B → success
```

One logical request may now generate:

```text
3 downstream calls
```

That can dramatically increase p99 latency.

Check:

```text
Retry count
Timeout count
Circuit breaker metrics
5xx
```

---

# Step 7 — Kubernetes / AWS layer

Since enterprise Spring Boot microservices commonly run on Kubernetes/EKS, I'd also check:

```text
Pod restarts
Pod readiness
HPA activity
Node CPU/memory
Network latency
Network errors
Load balancer latency
Ingress/API Gateway latency
```

Also check whether traffic suddenly shifted to a new pod/node/AZ.

---

# Production debugging flow

My mental model is:

```text
             p99 ↑
              |
      ┌───────┴────────┐
      ↓                ↓
 Application         Dependencies
      |                |
      ↓                ↓
Thread pools       Database
Connection pools   Redis
GC pauses          Kafka
Queue depth        HTTP services
      |                |
      └───────┬────────┘
              ↓
        Distributed Trace
              ↓
       Identify slow span
```

### The first metrics I'd check

| Area           | Metrics                              |
| -------------- | ------------------------------------ |
| Application    | p50/p95/p99, throughput, errors      |
| JVM            | GC pause, heap, threads              |
| Thread pools   | Active, queue depth, rejected        |
| DB             | Query latency, locks, pool wait      |
| HikariCP       | Active, idle, pending                |
| HTTP           | Downstream latency, timeout, retries |
| Redis          | Latency, hit ratio                   |
| Kafka          | Producer latency, consumer lag       |
| Kubernetes     | Pod restarts, throttling, network    |
| Infrastructure | Network/LB latency                   |

### Interview answer

> If p99 doubles while CPU remains stable, I would first look for queueing or I/O latency rather than CPU saturation. I would use distributed tracing to identify which span became slow, then investigate database query and connection-pool latency, downstream HTTP services, Redis/Kafka, thread-pool queues, retries/timeouts, and Kubernetes/networking metrics. A key distinction is whether time is being spent executing work or simply waiting for a resource such as a DB connection or downstream service.

---

# ⭐ Senior-Level Takeaway

For these five questions, remember this production troubleshooting mindset:

```text
N+1
 ↓
Too many DB calls
 ↓
Measure query count + trace
 ↓
JOIN FETCH / EntityGraph / DTO


Optimistic Lock
 ↓
Conflict detected at update
 ↓
@Version


Pessimistic Lock
 ↓
Prevent concurrent modification
 ↓
DB row lock


LazyInitializationException
 ↓
Persistence context closed
 ↓
Explicitly fetch required data


Unexpected COMMIT
 ↓
Exception swallowed?
Rollback rule?
Proxy bypassed?


p99 ↑ + CPU stable
 ↓
Don't immediately blame CPU
 ↓
Trace + DB + pools + downstreams
```

These are the kinds of answers that demonstrate **production debugging experience**, rather than just knowing Spring annotations.
