Idempotency in Spring Boot — Complete Senior Developer Guide

1. What is Idempotency?

Idempotency means that sending the same logical request multiple times should have the same intended effect on the system as sending it once.

In simple words, if a client retries the same operation because of a timeout, network failure, or another transient problem, the retry should not create an unintended duplicate side effect.

Examples:

Sending the same payment request multiple times should result in one payment attempt/effect.

Clicking Place Order multiple times should not create duplicate orders.

Retrying an API call after a response timeout should not create duplicate business data.

Important: Idempotency is about the effect of an operation, not necessarily that every retry returns byte-for-byte identical HTTP metadata. For example, a repeated DELETE may return a different status depending on whether the resource still exists, while the resource-removal effect remains idempotent.

2. Why is Idempotency Important?

Duplicate requests occur frequently in distributed systems because of:

Network failures

Client retries

Server timeouts

User double-clicks

Browser/mobile-app retries

Reverse proxies/load balancers retrying requests

Message redelivery

Microservice communication failures

Connection failures after the server has already committed a transaction

Without idempotency, a retry can cause:

Multiple payments

Duplicate orders

Duplicate invoices

Duplicate shipment requests

Incorrect inventory deductions

Duplicate Kafka/message processing

Inconsistent downstream state

The most dangerous case

The server successfully performs the operation, but the client never receives the response:

Client
  |
  | POST /payments
  v
Server
  |
  |-- Payment committed
  |
  X Response lost / timeout
  |
Client thinks request failed
  |
  | Retry
  v
Server
  |
  |-- Payment committed again

The server cannot safely assume that "no response received" means "operation did not happen."

That is one of the primary reasons idempotency is required in payment, order, booking, and other business-critical APIs.

3. HTTP Methods and Idempotency

According to HTTP semantics, the important distinction is between methods whose intended effect is idempotent and methods that commonly create new resources.

HTTP Method

Idempotent by HTTP semantics?

Typical use

GET

Yes

Read data

HEAD

Yes

Read headers

OPTIONS

Yes

Discover supported operations

PUT

Yes

Replace/create a resource at a known URI

DELETE

Yes

Delete a resource

POST

No, by default

Create/process an operation

PATCH

Depends on implementation

Partial modification

GET

GET /users/1

Repeated GET requests should not change the resource.

PUT

PUT /users/1
Content-Type: application/json

{
  "name": "John"
}

Sending the same representation repeatedly results in the same intended resource state:

name = John

DELETE

DELETE /users/1

The first request removes the resource. Repeating the operation does not cause another deletion side effect.

POST

POST /orders

Repeated POST requests may create multiple orders because POST is not inherently idempotent.

However, POST can be made application-level idempotent by introducing an idempotency key.

4. Idempotency vs Safety

These concepts are often confused.

Safe

A safe method is intended not to modify server state.

Examples:

GET
HEAD
OPTIONS

Idempotent

An idempotent operation can be repeated without changing the intended final state after the first successful execution.

Examples:

GET
PUT
DELETE

A method can be idempotent without being safe.

For example:

DELETE /users/1

changes server state, so it is not safe, but it is idempotent in terms of the deletion effect.

5. Real-World Payment Example

Suppose a customer makes a payment:

POST /payments
Content-Type: application/json

{
  "amount": 1000,
  "userId": 123
}

The server processes the payment:

Payment = ₹1000

But the response is lost because of a timeout.

The client retries:

POST /payments
Content-Type: application/json

{
  "amount": 1000,
  "userId": 123
}

Without idempotency:

Payment 1 = ₹1000
Payment 2 = ₹1000

Total = ₹2000

This is an unintended duplicate.

6. Solution: Idempotency Key

The client generates a unique key for one logical operation:

POST /payments
Idempotency-Key: abc123xyz
Content-Type: application/json

{
  "amount": 1000,
  "userId": 123
}

The key represents the logical operation, not each network attempt.

Therefore:

Initial request → abc123xyz
Retry           → abc123xyz
Retry again     → abc123xyz

A completely new payment should receive a different key.

7. Basic Idempotency Flow

Client
   |
   | POST /payments
   | Idempotency-Key: abc123
   v
API
   |
   | Check idempotency store
   v
Does key exist?
   |
   +---- YES ----> Return previously stored result
   |
   +---- NO -----> Process business operation
                         |
                         v
                    Store result
                         |
                         v
                    Return result

The logical algorithm is:

Receive the request.

Validate the idempotency key.

Check whether the key has already been processed.

If a completed result exists, return the stored result.

If the key is new, atomically reserve/register it.

Execute the business operation.

Store the final result/status.

Return the result.

Senior-level point: The check and the reservation must be concurrency-safe. A simple find() followed by insert() is not sufficient when two identical requests arrive at exactly the same time.

8. Idempotency Database Table

A simple design is:

CREATE TABLE idempotency_keys (
    id BIGINT PRIMARY KEY,
    idempotency_key VARCHAR(255) NOT NULL UNIQUE,
    request_hash VARCHAR(128),
    status VARCHAR(30) NOT NULL,
    response_status INT,
    response_body TEXT,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP
);

Example:

id

idempotency_key

status

response_status

response_body

1

abc123

COMPLETED

200

Payment successful

Why store request_hash?

A client should not normally reuse the same idempotency key for a different request.

For example:

Request 1:
key = abc123
amount = 1000

Request 2:
key = abc123
amount = 5000

The server should detect that the same key is being reused with a different payload and reject it, commonly with a 409 Conflict or another application-defined response.

9. Recommended State Model

For production systems, storing only a response is often too simple.

A more useful model is:

IN_PROGRESS
COMPLETED
FAILED
EXPIRED

Example:

abc123 → IN_PROGRESS
abc123 → COMPLETED

This helps the system handle concurrent requests and failures more explicitly.

10. Spring Boot Entity

A production-oriented entity could look like:

@Entity
@Table(
    name = "idempotency_keys",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_idempotency_key",
            columnNames = "idempotency_key"
        )
    }
)
public class IdempotencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idempotency_key", nullable = false, length = 255)
    private String idempotencyKey;

    @Column(name = "request_hash", length = 128)
    private String requestHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IdempotencyStatus status;

    private Integer responseStatus;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    private Instant createdAt;

    private Instant expiresAt;

    // getters and setters
}

Example enum:

public enum IdempotencyStatus {
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    EXPIRED
}

11. Repository

@Repository
public interface IdempotencyRepository
        extends JpaRepository<IdempotencyEntity, Long> {

    Optional<IdempotencyEntity>
        findByIdempotencyKey(String idempotencyKey);
}

The database's unique constraint is still essential. Application-level checking alone cannot reliably prevent duplicates under concurrency.

12. Basic Spring Boot Service

A simplified implementation:

@Service
public class PaymentService {

    private final IdempotencyRepository repository;

    public PaymentService(IdempotencyRepository repository) {
        this.repository = repository;
    }

    public String makePayment(
            String key,
            PaymentRequest request) {

        Optional<IdempotencyEntity> existing =
                repository.findByIdempotencyKey(key);

        if (existing.isPresent()) {
            return existing.get().getResponseBody();
        }

        String response = processPayment(request);

        IdempotencyEntity entity = new IdempotencyEntity();
        entity.setIdempotencyKey(key);
        entity.setStatus(IdempotencyStatus.COMPLETED);
        entity.setResponseBody(response);

        repository.save(entity);

        return response;
    }

    private String processPayment(PaymentRequest request) {
        return "Payment successful";
    }
}

This is useful for learning, but do not consider this version fully concurrency-safe. Two simultaneous requests can both perform the find() before either one inserts the key.

13. Controller

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<String> makePayment(
            @RequestHeader("Idempotency-Key") String key,
            @RequestBody PaymentRequest request) {

        String response =
                paymentService.makePayment(key, request);

        return ResponseEntity.ok(response);
    }
}

Example request:

POST /payments
Idempotency-Key: 123abc
Content-Type: application/json

{
    "amount": 500
}

14. First Request

POST /payments
Idempotency-Key: 123abc

Processing:

Key does not exist
       ↓
Create payment
       ↓
Store result
       ↓
Return response

Database:

key

status

response

123abc

COMPLETED

Payment successful

15. Second Request with Same Key

POST /payments
Idempotency-Key: 123abc

Processing:

Key exists
    ↓
Do NOT execute payment again
    ↓
Return stored result

No duplicate payment is created.

16. Redis-Based Idempotency

Redis is useful when the idempotency result is short-lived and very high request volume makes a database lookup undesirable.

Basic operations:

redisTemplate.opsForValue()
        .set(key, response);

Get:

String response =
        redisTemplate.opsForValue().get(key);

But this is not sufficient by itself:

if (!redisTemplate.hasKey(key)) {
    processPayment();
}

Two instances can both observe that the key is absent.

Prefer an atomic operation such as SET NX / setIfAbsent, with an appropriate expiration.

Conceptually:

Boolean acquired =
    redisTemplate.opsForValue()
        .setIfAbsent(key, "IN_PROGRESS", ttl);

Only the request that successfully acquires the reservation should proceed.

17. Database vs Redis

Feature

Database

Redis

Durable

Usually yes

Depends on configuration

Fast

Good

Very fast

TTL support

Usually application-managed

Native TTL

Strong source of business truth

Yes

Usually not

High-volume lookup

More expensive

Excellent

Transaction integration

Strong

Different consistency model

Best use

Durable business/idempotency record

Fast temporary/idempotency cache

A common architecture can use both:

Client
  ↓
API
  ↓
Redis / distributed reservation
  ↓
Database transaction
  ↓
Business operation
  ↓
Persist final result

The exact design depends on whether the idempotency record must survive cache loss.

18. AOP-Based Idempotency

For multiple endpoints, a custom annotation can reduce repeated controller/service code.

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
}

Then:

@Aspect
@Component
public class IdempotencyAspect {

    @Around("@annotation(Idempotent)")
    public Object check(ProceedingJoinPoint joinPoint)
            throws Throwable {

        // Read Idempotency-Key
        // Validate key
        // Check/reserve key
        // Return stored response if already completed
        // Otherwise execute method

        return joinPoint.proceed();
    }
}

However, AOP solves the cross-cutting implementation problem; it does not automatically solve distributed concurrency, persistence, TTL, or transaction consistency.

19. Distributed Systems Challenge

Consider three instances:

             Load Balancer
                  |
       +----------+----------+
       |          |          |
   Instance 1  Instance 2  Instance 3

Two identical requests arrive simultaneously:

Request A → Instance 1
Request B → Instance 2

If both perform:

check key → key absent → process payment

both may process the payment.

Therefore, this is unsafe:

SELECT/check
     ↓
if not found
     ↓
process
     ↓
INSERT

unless the design has proper concurrency protection.

20. Database Unique Constraint

A unique constraint provides an important final line of defense:

ALTER TABLE payments
ADD CONSTRAINT uk_payment_idempotency_key
UNIQUE (idempotency_key);

If two instances try to create the same idempotency record, only one can successfully insert it.

But the application must correctly handle the unique-constraint conflict.

Important: A unique constraint prevents duplicate keys. It does not by itself guarantee that the entire payment workflow is idempotent. The business transaction and idempotency record must be designed together.

21. Idempotency + Transaction

A strong design tries to make the idempotency state and the business operation consistent.

Conceptually:

BEGIN TRANSACTION

1. Reserve/check idempotency key
2. Create/update business data
3. Store final idempotency result

COMMIT

If the transaction fails:

ROLLBACK

The exact implementation depends on whether the business operation and idempotency store are in the same database.

If they are in different systems, a normal local database transaction cannot atomically cover both.

22. Idempotency and External Payment Providers

This is a critical real-world scenario.

Suppose your service calls an external payment provider:

Your Service
     |
     | payment request
     v
Payment Provider

You cannot assume your local database transaction will roll back an already-successful external payment.

Therefore, you should consider:

Provider-supported idempotency keys

Stable payment/order identifiers

Timeouts and retries

Reconciliation

Payment status lookup

Webhooks

Idempotent webhook processing

Compensation/recovery logic

23. Kafka and Idempotency

Kafka's idempotent producer feature:

enable.idempotence=true

helps prevent duplicate records caused by producer retries.

However:

Kafka idempotent producer ≠ entire business operation is automatically idempotent.

For example:

Kafka message
     ↓
Consumer
     ↓
Database update

The consumer can still process the same logical event more than once unless the consumer-side business operation is also designed to be idempotent.

For Kafka exactly-once processing, Kafka transactions and the surrounding processing design matter. Exactly-once semantics are not a blanket guarantee across arbitrary external databases or external services.

24. Idempotent Consumer Pattern

Suppose Kafka sends:

OrderCreated
eventId = EVT-1001

The consumer receives it twice.

Store processed event IDs:

CREATE TABLE processed_events (
    event_id VARCHAR(255) PRIMARY KEY,
    processed_at TIMESTAMP NOT NULL
);

Processing:

Receive event
    ↓
Check event ID
    ↓
Already processed?
   /  YES  NO
  |    |
Ignore Process
       |
       Store event ID

The event ID should be protected by a unique constraint.

25. Idempotency vs Caching

These are different concepts.

Caching

Purpose:

Improve performance by reusing previously computed data.

Request
  ↓
Cache
  ↓
Return cached result

Idempotency

Purpose:

Prevent repeated execution from creating unintended duplicate side effects.

Request
  ↓
Idempotency check
  ↓
Prevent duplicate operation

A cache can be part of an idempotency implementation, but caching itself does not guarantee idempotency.

26. Idempotency vs Deduplication

They are related but not identical.

Idempotency

Defines the behavior of repeating the same logical operation.

Deduplication

Detects and removes repeated messages/requests/events.

Example:

Event A
Event A
Event A

Deduplication may identify all three as the same event.

Idempotent processing ensures that processing the same event repeatedly does not create additional side effects.

27. Idempotency Key Design

A good key should be:

Unique for a logical operation

Unpredictable where appropriate

Stable across retries

Scoped to the correct user/account/tenant

Stored with an expiration policy where appropriate

Common choices:

UUID
Client-generated transaction ID
Order ID
Payment attempt ID
Business operation ID

Example:

Idempotency-Key: 550e8400-e29b-41d4-a716-446655440000

Do not generate a new key for every retry.

28. Request Validation

A robust implementation should validate:

Missing key
Invalid key format
Expired key
Same key + different payload
Same key + different authenticated user
Same key + different tenant

For example:

Key = ABC123

First request:
user = 100
amount = 500

Second request:
user = 100
amount = 900

This should normally be rejected rather than silently returning the result of the first operation.

29. Idempotency-Key Scope

In multi-tenant systems, avoid treating a bare key as globally meaningful if the business requirement is tenant-scoped.

A uniqueness model might conceptually be:

(tenant_id, user_id, idempotency_key)

rather than:

idempotency_key

The correct scope depends on the API contract.

30. TTL and Data Retention

Idempotency records do not necessarily need to live forever.

For example:

Created
   ↓
ACTIVE
   ↓
TTL expires
   ↓
Deleted/archived

But the TTL must match the business retry window.

If a key expires too early:

Original request
     ↓
Processed
     ↓
Key expires
     ↓
Very late retry
     ↓
Operation processed again

For financial operations, retention and reconciliation requirements may be much longer than a simple API cache TTL.

31. Idempotency Response Storage

Depending on the API, store enough information to reproduce the result of a retry.

Potential fields:

HTTP status
Response body
Response headers if necessary
Resource ID
Business status
Error information
Created timestamp
Expiration timestamp

For large responses, storing the entire response may not be appropriate. Instead, store a stable business result/resource ID and reconstruct the response.

32. Common Failure Scenarios

Failure 1 — Payment succeeds, response fails

Payment = SUCCESS
HTTP response = LOST

Retry must return the existing result.

Failure 2 — Two requests arrive simultaneously

Request A → Server 1
Request B → Server 2

Need atomic reservation/unique constraint/appropriate locking.

Failure 3 — Server crashes while processing

Key = IN_PROGRESS
Server crashes

The system needs a recovery strategy:

Retry after timeout

Reconciliation

State transition

Provider status check

Failure 4 — Same key with different payload

ABC123 → amount 1000

ABC123 → amount 2000

Reject as an invalid key reuse.

33. Best-Practice Production Architecture

For a high-value operation such as payment:

                    Client
                      |
                      | POST /payments
                      | Idempotency-Key
                      v
                API Gateway / LB
                      |
                      v
               Payment Service
                      |
              +-------+-------+
              |               |
              v               v
        Idempotency       Business DB
          Store               |
              |               |
              +-------+-------+
                      |
                      v
             External Payment
                Provider
                      |
                      v
             Payment Status
                      |
                      v
              Final Response

For asynchronous workflows:

Payment Service
      |
      | DB Transaction
      v
+--------------------+
| Payment             |
| Outbox Event        |
+--------------------+
      |
      v
 CDC / Publisher
      |
      v
    Kafka
      |
      v
Consumers

Each consumer should also use an idempotent processing strategy.

34. Best Practices Checklist

Use an idempotency key for retryable POST operations with side effects.

Keep the same key across retries of the same logical operation.

Add a database unique constraint where appropriate.

Make the check/reservation atomic.

Consider Redis for high-volume, short-lived idempotency state.

Do not rely on Redis alone for durable financial truth unless the design explicitly supports it.

Validate that a reused key has the same logical request.

Consider tenant/user scope.

Define a TTL/retention policy.

Store the final response or a stable resource/result identifier.

Handle IN_PROGRESS states.

Design for server crashes and timeouts.

Make Kafka consumers idempotent when duplicate delivery is possible.

Use provider-side idempotency when calling external payment providers.

Log idempotency keys safely for troubleshooting; avoid logging sensitive payment data.

Monitor duplicate/retry rates.

Test concurrent identical requests.

Test timeout-after-commit scenarios.

Test service restarts during processing.

Test duplicate messages/events.

35. Common Interview Questions

Basic

What is idempotency?

Why is idempotency important?

Give a real-world example.

Which HTTP methods are idempotent?

Is POST idempotent?

Can POST be made idempotent?

What is an idempotency key?

Should the key change during a retry?

Idempotency vs caching?

Idempotency vs deduplication?

Spring Boot

How would you implement idempotency in Spring Boot?

How would you use Redis?

How would you use a database?

How would you implement it using AOP?

Where should the idempotency check happen?

How would you handle concurrent requests?

How would you handle an IN_PROGRESS request?

How would you handle a server crash?

How would you validate key reuse?

How would you configure expiration?

Database

Why is a unique constraint important?

Is findByKey() followed by save() safe?

What happens if two requests arrive simultaneously?

How do transactions affect idempotency?

Should idempotency data and business data be in the same transaction?

How do you handle duplicate-key exceptions?

How would you clean expired keys?

Microservices

How do you implement idempotency across multiple instances?

How do you make Kafka consumers idempotent?

Does Kafka enable.idempotence=true make your entire application idempotent?

How do you handle duplicate messages?

How does the Outbox Pattern help?

How do you handle external payment-provider retries?

How do you design idempotency across multiple services?

36. Senior Interview Scenario

Interviewer:

A payment API sometimes charges customers twice when there is a timeout. How would you fix it?

Strong Senior-level answer

I would first identify whether the duplicate occurs because the client
retries after the original request has already been committed.

I would introduce an idempotency key representing one logical payment
operation. The client would send the same key for all retries.

On the server, I would maintain an idempotency record with the key,
request fingerprint/hash, processing status and final result.

The reservation of the key must be atomic, and I would enforce a
database unique constraint to protect against concurrent requests
reaching different service instances.

If the key is already COMPLETED, I would return the stored result
without executing the payment again.

If the operation is IN_PROGRESS, I would apply a defined policy such
as waiting, returning a retryable response, or checking the business
operation status.

For an external payment provider, I would also use the provider's
idempotency mechanism where supported, because a local transaction
cannot roll back an already-successful external payment.

For asynchronous processing, I would use an Outbox Pattern and make
Kafka consumers idempotent using a unique event/operation ID.

Finally, I would test concurrent duplicate requests, timeout-after-
commit scenarios, server crashes, retries, and reuse of the same key
with a different payload.

37. Key Takeaways

Idempotency
     ↓
Same logical operation can be retried safely
     ↓
Use an Idempotency-Key
     ↓
Atomically reserve the key
     ↓
Execute business operation once
     ↓
Store final result
     ↓
Return stored result on retry

The most important Senior Developer lesson is:

Idempotency is not simply "check Redis and return if the key exists." It is a distributed-systems design problem involving retries, concurrency, transactions, persistence, external services, failure recovery, and duplicate message handling.

Original Source Material

The original notes cover the core definition, HTTP method behavior, payment example, database implementation, Spring Boot entity/repository/service/controller, Redis, AOP, distributed instances, database uniqueness, Redis locking, Kafka, best practices, interview questions, and an e-commerce flow. The expanded sections above retain those core concepts while adding production-level concerns such as atomic reservation, request fingerprints, processing states, TTL, key scope, transaction boundaries, external payment providers, idempotent consumers, and failure scenarios.