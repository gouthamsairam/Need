Absolutely. These are **very important Spring transaction interview questions**. I’ll explain each from **simple concept → internal implementation → real-world example → interview answer**.

# 🔄 Spring `@Transactional` — Questions 11–25

---

## 11. How does Spring implement `@Transactional` internally?

Spring generally implements `@Transactional` using **AOP proxies**.

Suppose you have:

```java
@Service
public class OrderService {

    @Transactional
    public void createOrder() {
        orderRepository.save(order);
        paymentRepository.save(payment);
    }
}
```

Spring doesn't simply modify the method itself.

Instead, Spring creates a proxy around `OrderService`.

Conceptually:

```text
Client
  |
  v
Spring Proxy
  |
  | begin transaction
  |
  v
OrderService.createOrder()
  |
  | DB operations
  |
  v
  return
  |
  | commit
  v
Proxy
```

### Internally

The important components are:

```text
@Transactional
      ↓
Spring AOP
      ↓
Proxy
      ↓
TransactionInterceptor
      ↓
PlatformTransactionManager
      ↓
Database Transaction
```

The `TransactionInterceptor` roughly does:

```java
beginTransaction();

try {
    Object result = targetMethod();

    commitTransaction();

    return result;

} catch (Exception e) {

    rollbackTransaction();

    throw e;
}
```

The actual implementation is more sophisticated, but this is the correct mental model.

### Interview answer

> Spring implements `@Transactional` primarily through AOP proxies. When a transactional method is invoked through the Spring proxy, `TransactionInterceptor` starts or joins a transaction using the configured transaction manager. After successful execution it commits, and if a rollback-triggering exception occurs, it rolls back.

---

# 12. Why can `@Transactional` appear to have no effect?

This is one of the **most common Spring interview questions**.

There are several reasons.

### 1. Self-invocation

```java
@Service
public class OrderService {

    public void process() {
        saveOrder();   // direct method call
    }

    @Transactional
    public void saveOrder() {
        // DB operation
    }
}
```

This doesn't go through the Spring proxy.

```text
process()
   |
   └── saveOrder()
          ↑
       direct call
```

Therefore the transaction interceptor isn't invoked.

---

### 2. Method is `private`

```java
@Transactional
private void saveOrder() {
}
```

Spring proxy-based transaction management doesn't intercept private methods.

---

### 3. Object created using `new`

```java
OrderService service = new OrderService();
service.createOrder();
```

Spring isn't managing this object.

Therefore no Spring proxy exists.

---

### 4. Transaction manager isn't configured correctly

For example:

```text
@Transactional
     ↓
Which transaction manager?
     ↓
Incorrect/missing configuration
```

---

### 5. Wrong import

You should generally use:

```java
import org.springframework.transaction.annotation.Transactional;
```

---

### 6. Exception doesn't trigger rollback

For example:

```java
@Transactional
public void process() {

    try {
        // DB operation
    } catch (Exception e) {
        // exception swallowed
    }
}
```

The transaction may commit because the exception never reaches the transaction interceptor.

---

# 13. What happens during self-invocation inside a transactional bean?

Consider:

```java
@Service
public class PaymentService {

    public void processPayment() {
        savePayment();
    }

    @Transactional
    public void savePayment() {
        // database operation
    }
}
```

The call is:

```text
Client
  ↓
Proxy
  ↓
processPayment()
  ↓
savePayment()
```

The important point is:

```text
processPayment()
      |
      | this.savePayment()
      ↓
Actual object
```

The second call doesn't go through the proxy.

Therefore:

```java
@Transactional
```

on `savePayment()` isn't applied.

### Better approach

Move the transactional method into another Spring bean:

```java
@Service
public class PaymentService {

    private final PaymentTransactionService transactionService;

    public void processPayment() {
        transactionService.savePayment();
    }
}
```

```java
@Service
public class PaymentTransactionService {

    @Transactional
    public void savePayment() {
        // DB operation
    }
}
```

Now:

```text
PaymentService
      ↓
Spring Proxy
      ↓
PaymentTransactionService
      ↓
@Transactional
```

---

# 14. Why is `@Transactional` ineffective on private methods?

Spring's normal proxy-based AOP works by intercepting method calls made **through the proxy**.

A private method cannot be overridden by a subclass proxy and isn't exposed as an interceptable proxy method.

Example:

```java
@Transactional
private void updateDatabase() {
}
```

Calling:

```java
updateDatabase();
```

doesn't provide the Spring proxy an interception point.

Therefore the transaction isn't started because of that annotation.

### Recommended

Use:

```java
@Transactional
public void updateDatabase() {
}
```

or package/protected visibility where appropriate, but the most important point is that the call must be made through the Spring-managed proxy.

---

# 15. Which exceptions trigger rollback by default in Spring?

By default:

### Rollback

```text
RuntimeException
Error
```

Example:

```java
@Transactional
public void process() {

    saveOrder();

    throw new RuntimeException("Payment failed");
}
```

Spring rolls back.

---

### Checked exceptions

By default, checked exceptions **do not trigger rollback**.

```java
@Transactional
public void process() throws IOException {

    saveOrder();

    throw new IOException();
}
```

By default, Spring may commit.

### Force rollback for checked exception

```java
@Transactional(rollbackFor = IOException.class)
public void process() throws IOException {

    saveOrder();

    throw new IOException();
}
```

You can also specify:

```java
@Transactional(
    rollbackFor = Exception.class
)
```

### Important interview statement

> By default, Spring rolls back transactions for unchecked `RuntimeException` and `Error`, but not checked exceptions.

---

# 16. `REQUIRED` vs `REQUIRES_NEW`: how is the existing transaction handled?

This is extremely important.

## `REQUIRED`

```java
@Transactional(propagation = Propagation.REQUIRED)
```

If transaction exists:

```text
Existing Transaction
       ↓
Join it
```

If no transaction exists:

```text
No Transaction
       ↓
Create new transaction
```

Example:

```java
@Transactional
public void order() {

    payment();
}
```

```java
@Transactional(propagation = REQUIRED)
public void payment() {
}
```

Both use the **same transaction**.

---

## `REQUIRES_NEW`

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
```

If a transaction already exists:

```text
Transaction A
     ↓
SUSPEND A
     ↓
Create Transaction B
     ↓
Execute method
     ↓
Commit/Rollback B
     ↓
RESUME A
```

Example:

```java
@Transactional
public void createOrder() {

    saveOrder();

    auditService.saveAudit();
}
```

```java
@Transactional(propagation = REQUIRES_NEW)
public void saveAudit() {
}
```

Conceptually:

```text
Transaction A
    |
    | saveOrder
    |
    | suspend A
    ↓
Transaction B
    |
    | saveAudit
    |
    | commit B
    ↓
resume A
    |
    | continue
    ↓
commit A
```

This is useful for things like independent audit records.

---

# 17. Explain the 7 Spring transaction propagation behaviors

Spring provides **7 propagation modes**.

| Propagation     | Behavior                                                |
| --------------- | ------------------------------------------------------- |
| `REQUIRED`      | Join existing or create new                             |
| `REQUIRES_NEW`  | Suspend existing and create new                         |
| `SUPPORTS`      | Join if transaction exists; otherwise non-transactional |
| `NOT_SUPPORTED` | Suspend existing transaction                            |
| `MANDATORY`     | Must have existing transaction                          |
| `NEVER`         | Must not have transaction                               |
| `NESTED`        | Execute using nested transaction/savepoint              |

### 1. REQUIRED

```text
Existing → Join
None → Create
```

Most commonly used.

---

### 2. REQUIRES_NEW

```text
Existing → Suspend
New transaction → Create
```

---

### 3. SUPPORTS

```text
Existing → Join
None → Execute without transaction
```

---

### 4. NOT_SUPPORTED

```text
Existing → Suspend
Execute without transaction
```

---

### 5. MANDATORY

```text
Existing → Join
None → Exception
```

---

### 6. NEVER

```text
Existing → Exception
None → Execute normally
```

---

### 7. NESTED

```text
Existing transaction
       ↓
Create savepoint
       ↓
Execute nested operation
```

If nested operation fails, rollback can occur to the savepoint while the outer transaction can potentially continue.

---

# 18. How does `NESTED` propagation work internally?

`NESTED` is different from `REQUIRES_NEW`.

Suppose:

```java
@Transactional
public void order() {

    saveOrder();

    inventory();
}
```

```java
@Transactional(propagation = Propagation.NESTED)
public void inventory() {
}
```

Conceptually:

```text
Transaction A
     |
     | saveOrder
     |
     | CREATE SAVEPOINT
     ↓
 inventory()
     |
     | failure
     ↓
ROLLBACK TO SAVEPOINT
     |
     ↓
Transaction A continues
```

The key is:

> `NESTED` generally uses a **savepoint inside the existing transaction**, whereas `REQUIRES_NEW` creates a completely separate transaction.

### Difference

```text
REQUIRES_NEW

Transaction A
    ↓
 suspend
    ↓
Transaction B
    ↓
 commit/rollback B
    ↓
resume A
```

versus:

```text
NESTED

Transaction A
    ↓
 savepoint
    ↓
nested operation
    ↓
rollback to savepoint
    ↓
continue Transaction A
```

### Important

`NESTED` support depends on the transaction manager and underlying database capabilities. It is commonly associated with JDBC savepoints.

---

# 19. Explain `READ_UNCOMMITTED`, `READ_COMMITTED`, `REPEATABLE_READ`, and `SERIALIZABLE`

These are database **transaction isolation levels**.

They control how much one transaction can see from another concurrent transaction.

| Isolation        | Dirty Read | Non-repeatable Read | Phantom Read |
| ---------------- | ---------: | ------------------: | -----------: |
| READ_UNCOMMITTED |          ✅ |                   ✅ |            ✅ |
| READ_COMMITTED   |          ❌ |                   ✅ |            ✅ |
| REPEATABLE_READ  |          ❌ |                   ❌ | DB-dependent |
| SERIALIZABLE     |          ❌ |                   ❌ |            ❌ |

---

## READ_UNCOMMITTED

Transaction can read uncommitted changes.

```text
Transaction A
UPDATE balance = 500
(not committed)

Transaction B
SELECT balance
→ 500
```

If A rolls back:

```text
Actual balance → previous value
```

B read invalid/uncommitted data.

---

## READ_COMMITTED

Only committed data can be read.

```text
A updates
   ↓
B cannot see update yet
   ↓
A commits
   ↓
B can see update
```

This is a common default in databases such as PostgreSQL and Oracle.

---

## REPEATABLE_READ

If you read a row once:

```text
SELECT balance
```

you generally get a consistent value for subsequent reads in the same transaction.

It prevents **non-repeatable reads**.

---

## SERIALIZABLE

Highest isolation.

Transactions behave approximately as though they execute one after another.

```text
Transaction A
      ↓
Transaction B waits/conflicts
```

It provides the strongest isolation but generally reduces concurrency and can increase locking/conflict overhead.

---

# 20. How do dirty reads occur in a concurrent transaction?

Example:

### Transaction A

```sql
BEGIN;

UPDATE account
SET balance = 500
WHERE id = 1;

-- NOT COMMITTED
```

### Transaction B

```sql
SELECT balance
FROM account
WHERE id = 1;
```

Under `READ_UNCOMMITTED`:

```text
B sees → 500
```

But then:

```text
Transaction A → ROLLBACK
```

Actual balance becomes:

```text
1000
```

Transaction B previously saw:

```text
500
```

That is a **dirty read**.

### Prevent it

Use:

```text
READ_COMMITTED
```

or stronger isolation.

---

# 21. What causes phantom reads, and how can they be prevented?

A phantom read occurs when the **same query returns a different set of rows** because another transaction inserted/deleted matching rows.

Example:

### Transaction A

```sql
SELECT *
FROM employees
WHERE salary > 100000;
```

Result:

```text
10 rows
```

### Transaction B

```sql
INSERT INTO employees(...)
VALUES (..., 150000);
COMMIT;
```

### Transaction A executes same query again

```sql
SELECT *
FROM employees
WHERE salary > 100000;
```

Now:

```text
11 rows
```

The new row is the **phantom**.

### Prevention

Use stronger isolation such as:

```text
SERIALIZABLE
```

Database-specific locking/MVCC mechanisms can also prevent or control phantoms.

---

# 22. Why is calling an external HTTP service inside a DB transaction risky?

Consider:

```java
@Transactional
public void processOrder() {

    saveOrder();

    paymentClient.callPaymentService();

    updateOrder();
}
```

The DB transaction remains open while waiting for the HTTP request.

```text
BEGIN TRANSACTION
       ↓
saveOrder()
       ↓
HTTP call
       ↓
  waiting...
       ↓
HTTP response
       ↓
updateOrder()
       ↓
COMMIT
```

### Problems

#### 1. Long-running transaction

HTTP could take:

```text
5 sec
10 sec
30 sec
```

The database transaction remains open.

---

#### 2. Connection pool exhaustion

Imagine:

```text
HikariCP = 20 connections
```

20 requests enter transactions and wait for HTTP.

```text
20 DB connections
       ↓
waiting for HTTP
       ↓
pool exhausted
```

New requests can't get DB connections.

---

#### 3. Lock contention

Locks may remain held longer.

---

#### 4. Distributed consistency problem

Suppose:

```text
DB transaction succeeds
HTTP payment succeeds
then DB commit fails
```

Now the external system and DB disagree.

A DB transaction cannot automatically roll back an arbitrary HTTP service.

### Better architecture

Use patterns such as:

```text
DB Transaction
      ↓
Outbox Event
      ↓
Kafka
      ↓
Payment Service
```

This is much safer for microservices.

---

# 23. What is `TransactionSynchronizationManager` used for?

`TransactionSynchronizationManager` is a Spring infrastructure component that keeps track of transaction-related resources and synchronization callbacks associated with the **current thread**.

It can manage things such as:

```text
Current transaction state
        ↓
Database resources
        ↓
Transaction synchronizations
        ↓
Transaction-bound resources
```

For example, frameworks can register callbacks:

```java
TransactionSynchronizationManager.registerSynchronization(
    new TransactionSynchronization() {

        @Override
        public void afterCommit() {
            // execute after commit
        }
    }
);
```

You can use it when you need transaction lifecycle hooks such as:

```text
beforeCommit
afterCommit
afterCompletion
```

### Important

You normally don't directly manipulate this class in application code unless you have a specific advanced use case.

Higher-level mechanisms are often preferable, such as:

```java
@TransactionalEventListener
```

---

# 24. How do you ensure a Kafka event is published only after a database commit?

This is a **very important microservices interview question**.

Suppose:

```java
@Transactional
public void createOrder() {

    orderRepository.save(order);

    kafkaTemplate.send("orders", event);
}
```

Potential problem:

```text
DB transaction
    |
    | save order
    |
Kafka publish
    |
DB COMMIT fails
```

Kafka event may already have been published.

Now:

```text
Kafka → OrderCreated
Database → rollback
```

Inconsistent state.

---

## Option 1: `@TransactionalEventListener`

Publish an application event:

```java
@Transactional
public void createOrder() {

    orderRepository.save(order);

    applicationEventPublisher.publishEvent(
        new OrderCreatedEvent(order.getId())
    );
}
```

Then:

```java
@TransactionalEventListener(
    phase = TransactionPhase.AFTER_COMMIT
)
public void handle(OrderCreatedEvent event) {

    kafkaTemplate.send(
        "orders",
        event
    );
}
```

Flow:

```text
DB Transaction
      ↓
save Order
      ↓
publish Spring Event
      ↓
COMMIT
      ↓
AFTER_COMMIT
      ↓
Kafka publish
```

This ensures Kafka publication happens **after successful DB commit**.

### But there is an important limitation

`AFTER_COMMIT` doesn't make DB + Kafka one atomic transaction.

For example:

```text
DB COMMIT ✅
      ↓
Kafka publish ❌
```

You can still lose the event.

---

# Better solution: Transactional Outbox Pattern

For critical events, use:

```text
Application
    |
    | DB Transaction
    ↓
Orders Table
Outbox Table
    |
    | COMMIT
    ↓
Outbox Publisher
    |
    ↓
Kafka
```

For example:

```sql
BEGIN;

INSERT INTO orders (...);

INSERT INTO outbox (
    event_type,
    aggregate_id,
    payload
);

COMMIT;
```

Both records commit together.

Then an outbox publisher reads:

```text
Outbox
   ↓
Kafka
```

This is generally the preferred approach when reliable DB-to-Kafka event delivery matters.

---

# 25. What does `@Transactional(readOnly = true)` actually change?

Example:

```java
@Transactional(readOnly = true)
public List<Order> getOrders() {
    return orderRepository.findAll();
}
```

`readOnly = true` is primarily a **transaction hint**, not a universal "no writes allowed" guarantee.

Depending on the transaction manager, database, and ORM, it can influence:

### 1. Hibernate/JPA behavior

It can allow Hibernate to optimize certain persistence operations and reduce unnecessary dirty checking in appropriate configurations.

### 2. Database behavior

Some databases/drivers can use read-only transaction hints.

### 3. Application intent

It communicates:

> "This transaction is intended only for reading."

### Important misconception

This:

```java
@Transactional(readOnly = true)
```

does **not universally mean**:

```text
INSERT → impossible
UPDATE → impossible
DELETE → impossible
```

The exact behavior depends on your database, driver, ORM, and configuration.

### Example

```java
@Transactional(readOnly = true)
public Order getOrder(Long id) {

    return orderRepository.findById(id)
            .orElseThrow();
}
```

This is appropriate for read-only service operations.

---

# ⭐ Most Important Interview Comparisons

## `REQUIRED` vs `REQUIRES_NEW` vs `NESTED`

```text
REQUIRED
────────────
Existing transaction?
       ↓
      YES
       ↓
   JOIN IT


REQUIRES_NEW
────────────
Existing transaction?
       ↓
      YES
       ↓
  SUSPEND IT
       ↓
CREATE NEW TX


NESTED
────────────
Existing transaction
       ↓
CREATE SAVEPOINT
       ↓
Execute nested work
       ↓
Rollback to savepoint if required
```

---

## Isolation levels

```text
READ_UNCOMMITTED
      ↓
Lowest isolation
      ↓
Dirty reads possible

READ_COMMITTED
      ↓
No dirty reads

REPEATABLE_READ
      ↓
No dirty/non-repeatable reads
      ↓
Phantom behavior depends on DB

SERIALIZABLE
      ↓
Highest isolation
      ↓
Lowest concurrency generally
```

---

## `@Transactional` mental model

For interviews, remember this:

```text
                    @Transactional
                          |
                          ↓
                     Spring AOP
                          |
                          ↓
                       Proxy
                          |
                          ↓
                 TransactionInterceptor
                          |
                          ↓
                TransactionManager
                          |
                          ↓
                 Begin / Join TX
                          |
                          ↓
                    Business Code
                          |
                  ┌───────┴───────┐
                  ↓               ↓
              Success          Exception
                  ↓               ↓
               COMMIT          ROLLBACK
```

### The 5 points interviewers most commonly probe

1. **`@Transactional` works through a Spring proxy.**
2. **Self-invocation bypasses that proxy.**
3. **Default rollback = `RuntimeException` and `Error`.**
4. **`REQUIRES_NEW` suspends the current transaction and starts another.**
5. **DB transaction + Kafka/external API is not automatically one atomic transaction; use patterns such as Outbox for reliable distributed consistency.**
Yes. But there is an important correction to the explanation you quoted:

> **`@Transactional` does not work simply because a method is public.**
> The key issue is that Spring's proxy must be involved in the method invocation.

For a senior Java interview, explain it like this.

## 1. The recommended solution: move the transaction to a public service method

Instead of:

```java
@Service
public class PaymentService {

    @Transactional
    private void processRefund(Long paymentId) {
        paymentRepository.updateStatus(paymentId, "REFUNDED");
        ledgerRepository.createEntry(paymentId, "CREDIT");
    }
}
```

make the transactional operation a public method:

```java
@Service
public class PaymentService {

    @Transactional
    public void processRefund(Long paymentId) {

        paymentRepository.updateStatus(paymentId, "REFUNDED");

        ledgerRepository.createEntry(paymentId, "CREDIT");
    }
}
```

Then call it from another Spring bean:

```java
@Service
public class RefundControllerService {

    private final PaymentService paymentService;

    public RefundControllerService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void refund(Long paymentId) {
        paymentService.processRefund(paymentId);
    }
}
```

The flow is:

```text
Controller
    |
    v
PaymentService Proxy
    |
    |  @Transactional
    v
Transaction starts
    |
    v
PaymentService.processRefund()
    |
    +---- paymentRepository.updateStatus()
    |
    +---- ledgerRepository.createEntry()
    |
    v
Transaction commits
```

If an exception occurs:

```text
processRefund()
      |
      +--> updateStatus()
      |
      +--> createEntry()
              |
              X Exception
              |
              v
         Transaction Rollback
```

---

# 2. What if I don't want the method to be public?

This is where the design becomes important.

You **shouldn't make a method public just to make `@Transactional` work**.

Instead, put the transaction boundary at the service-level public method.

For example:

```java
@Service
public class PaymentService {

    @Transactional
    public void refund(Long paymentId) {
        validateRefund(paymentId);
        processRefund(paymentId);
    }

    private void validateRefund(Long paymentId) {
        // validation
    }

    private void processRefund(Long paymentId) {

        paymentRepository.updateStatus(paymentId, "REFUNDED");

        ledgerRepository.createEntry(paymentId, "CREDIT");
    }
}
```

Here:

```java
@Transactional
public void refund(...)
```

is the **transaction boundary**.

The private method doesn't need `@Transactional`.

The transaction is already active when:

```java
processRefund()
```

executes.

This is usually the **best solution**.

---

# 3. Very important: private methods CAN execute inside a transaction

This is a common misunderstanding.

Suppose:

```java
@Service
public class PaymentService {

    @Transactional
    public void refund(Long paymentId) {

        processRefund(paymentId);
    }

    private void processRefund(Long paymentId) {

        paymentRepository.updateStatus(paymentId, "REFUNDED");

        ledgerRepository.createEntry(paymentId, "CREDIT");
    }
}
```

`processRefund()` is private.

But it **still executes inside the transaction**.

Why?

Because the transaction was started when the proxy intercepted:

```java
refund()
```

The private method executes as part of that same call.

So:

```text
Proxy
  |
  | @Transactional
  ↓
refund()
  |
  ↓
private processRefund()
  |
  +--> DB operation
  |
  +--> DB operation
```

Everything happens within the same transaction.

---

# 4. The bigger problem: self-invocation

This is actually more important than public/private.

Consider:

```java
@Service
public class PaymentService {

    public void refund(Long paymentId) {
        processRefund(paymentId);
    }

    @Transactional
    public void processRefund(Long paymentId) {

        paymentRepository.updateStatus(paymentId, "REFUNDED");
    }
}
```

You might think:

```java
refund()
   |
   v
@Transactional processRefund()
```

means Spring starts a transaction.

**It doesn't.**

Because this:

```java
processRefund(paymentId);
```

is a direct call on `this`.

Conceptually:

```java
this.processRefund(paymentId);
```

The call doesn't go through the Spring proxy.

Therefore:

```text
Spring Proxy
     |
     X
     |
PaymentService
     |
     +--> refund()
             |
             +--> this.processRefund()
```

The proxy is bypassed.

---

# 5. Solution for self-invocation: move the method to another bean

This is usually the cleanest solution.

### PaymentService

```java
@Service
public class PaymentService {

    private final RefundService refundService;

    public PaymentService(RefundService refundService) {
        this.refundService = refundService;
    }

    public void refund(Long paymentId) {

        refundService.processRefund(paymentId);
    }
}
```

### RefundService

```java
@Service
public class RefundService {

    @Transactional
    public void processRefund(Long paymentId) {

        paymentRepository.updateStatus(paymentId, "REFUNDED");

        ledgerRepository.createEntry(paymentId, "CREDIT");
    }
}
```

Now:

```text
PaymentService
      |
      v
RefundService Proxy
      |
      | @Transactional
      v
processRefund()
      |
      +--> DB
      +--> DB
```

The proxy is involved.

Therefore the transaction works.

---

# 6. Another solution: inject the proxy

You may see this approach:

```java
@Service
public class PaymentService {

    private final PaymentService self;

    public PaymentService(PaymentService self) {
        this.self = self;
    }

    public void refund(Long paymentId) {

        self.processRefund(paymentId);
    }

    @Transactional
    public void processRefund(Long paymentId) {

        paymentRepository.updateStatus(paymentId, "REFUNDED");
    }
}
```

Now:

```java
self.processRefund()
```

can go through the Spring proxy.

But I **wouldn't recommend this as the first choice**.

It makes the class harder to understand and can introduce circular/self-reference concerns depending on configuration.

Prefer splitting responsibilities into another service.

---

# 7. `AopContext.currentProxy()` — possible but not preferred

Another approach is:

```java
public void refund(Long paymentId) {

    PaymentService proxy =
        (PaymentService) AopContext.currentProxy();

    proxy.processRefund(paymentId);
}
```

with:

```java
@Transactional
public void processRefund(Long paymentId) {
    // transaction
}
```

But this requires proxy exposure:

```properties
spring.aop.expose-proxy=true
```

Again, this is generally **not the preferred design**.

---

# 8. What about protected/package-private methods?

This is where your quoted answer is slightly oversimplified.

Don't memorize:

> "Spring transactions only work on public methods."

Instead remember:

> **With Spring's proxy-based transaction management, the transaction interceptor needs the invocation to pass through the Spring proxy. Public methods invoked through the proxy are the normal and recommended transaction boundary.**

For modern Spring/Spring Boot versions, proxy behavior has evolved and some non-public method scenarios can be supported depending on the proxy/transaction configuration. But **self-invocation remains a fundamental proxy-AOP limitation**.

So in an interview, don't overstate the rule as "private/protected/package-private always silently fail."

---

# 9. What about JDK proxy vs CGLIB?

### JDK Dynamic Proxy

Historically:

```text
Interface
    ↑
Proxy
    |
Implementation
```

Only interface methods are exposed through the proxy.

Example:

```java
public interface PaymentService {
    void refund(Long id);
}
```

```java
@Service
public class PaymentServiceImpl
        implements PaymentService {

    @Transactional
    public void refund(Long id) {
    }
}
```

The call:

```java
paymentService.refund(id);
```

can go through the proxy.

---

### CGLIB

CGLIB creates a subclass conceptually:

```text
PaymentServiceProxy extends PaymentService
```

The proxy overrides/intercepts methods.

A private method:

```java
private void processRefund()
```

cannot be overridden.

Therefore it cannot be intercepted in the same way.

Final methods also cannot be overridden, so they present another proxy limitation.

---

# 10. Best production design

For your example, I'd write:

```java
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LedgerRepository ledgerRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            LedgerRepository ledgerRepository) {
        this.paymentRepository = paymentRepository;
        this.ledgerRepository = ledgerRepository;
    }

    @Transactional
    public void processRefund(Long paymentId) {

        validateRefund(paymentId);

        updatePaymentStatus(paymentId);

        createLedgerEntry(paymentId);
    }

    private void validateRefund(Long paymentId) {
        // validation
    }

    private void updatePaymentStatus(Long paymentId) {
        paymentRepository.updateStatus(
                paymentId,
                "REFUNDED"
        );
    }

    private void createLedgerEntry(Long paymentId) {
        ledgerRepository.createEntry(
                paymentId,
                "CREDIT"
        );
    }
}
```

This gives you:

```text
               PaymentService Proxy
                       |
                       | @Transactional
                       ↓
                processRefund()
                       |
          +------------+------------+
          |            |            |
          ↓            ↓            ↓
      validate     update DB    ledger DB
          |            |            |
          +------------+------------+
                       |
                 Commit / Rollback
```

### The key principle

**Put `@Transactional` on the public service-layer method that represents one business transaction.**

Don't put `@Transactional` on every private method.

---

## Senior interview answer

If the interviewer asks:

> **"What is the solution when @Transactional doesn't work on a private method?"**

A strong answer is:

> "`@Transactional` is implemented through Spring AOP proxies. The important requirement is that the method invocation passes through the Spring proxy. I don't make a private method public just to enable transactions. Instead, I put `@Transactional` on a public service-layer method that defines the transaction boundary and let the private methods execute inside that transaction. If the problem is self-invocation, I move the transactional operation to another Spring bean so the call goes through the proxy. I can technically use self-injection or `AopContext.currentProxy()`, but I prefer separating the service because it is cleaner and easier to maintain."

That answer is much stronger than simply saying **"make the method public."**
