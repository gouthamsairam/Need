Absolutely. If your **Spring Boot application is running inside AWS EKS**, and you want it to communicate with an AWS **messaging service**, the most common choices are **Amazon SQS** and **Amazon SNS**.

The key point is: **EKS does not directly "talk to a message broker" in some special way. Your Spring Boot application acts as an AWS client and communicates with the messaging service using AWS APIs/SDKs over HTTPS.**

## 1. Typical architecture

For example, suppose you have an `Order Service` running in EKS and you want to send an order event:

```text
 AWS Cloud
┌──────────────────────────────────────────────────────────────┐
│ │
│ EKS Cluster │
│ ┌──────────────────────────────────────────────┐ │
│ │ │ │
│ │ Spring Boot Order Service │ │
│ │ │ │ │
│ │ │ AWS SDK / Spring Cloud AWS │ │
│ │ │ HTTPS │ │
│ │ ▼ │ │
│ └──────────┼───────────────────────────────────┘ │
│ │ │
│ ▼ │
│ Amazon SQS Queue │
│ │ │
│ │ Message │
│ ▼ │
│ ┌───────────────┐ │
│ │ Consumer │ │
│ │ Spring Boot │ │
│ │ Service │ │
│ └───────────────┘ │
│ │
└──────────────────────────────────────────────────────────────┘
```

For example:

```text
Order Service
 |
 | "Order Created"
 ↓
 SQS
 |
 ↓
Notification Service
```

The two Spring Boot services **don't need to directly call each other**.

---

# 2. How does communication actually happen?

Let's say your Spring Boot application has:

```text
POST /orders
```

A customer creates an order.

The flow is:

```text
Client
 |
 | HTTP
 ↓
ALB
 |
 ↓
EKS
 |
 ↓
Spring Boot Order Service
 |
 | 1. Save order
 |
 | 2. Publish message
 ↓
Amazon SQS
 |
 | 3. Message waits in queue
 ↓
Notification Service
 |
 | 4. Poll SQS
 ↓
Process message
```

Suppose the order is:

```json
{
"orderId": 1001,
"customerId": 5001,
"amount": 2500
}
```

The Order Service sends a message to SQS:

```json
{
"eventType": "ORDER_CREATED",
"orderId": 1001,
"customerId": 5001,
"amount": 2500
}
```

---

# 3. What happens inside AWS?

This is important for interviews.

Your Spring Boot application is running inside an EKS Pod:

```text
EKS
 |
 └── Pod
 |
 └── Spring Boot
```

Spring Boot uses an AWS SDK:

```text
Spring Boot
 |
 ↓
AWS SDK
 |
 ↓
AWS API
 |
 ↓
SQS
```

The communication generally happens through **HTTPS**.

So conceptually:

```text
Spring Boot Pod
 |
 | HTTPS
 ↓
AWS SQS API
 |
 ↓
SQS Queue
```

---

# 4. How does Spring Boot get permission to access SQS?

This is one of the **most important EKS interview questions**.

You should **not** put AWS access keys directly inside your application:

```properties
AWS_ACCESS_KEY=xxxx
AWS_SECRET_KEY=xxxx
```

❌ Don't do this in production.

Instead, use **IAM**.

For EKS, a common approach is **EKS Pod Identity** or **IAM Roles for Service Accounts (IRSA)**, depending on your cluster setup.

Conceptually:

```text
Spring Boot Pod
 |
 ↓
Kubernetes Service Account
 |
 ↓
IAM Role
 |
 ↓
IAM Policy
 |
 ↓
SQS
```

The IAM policy might allow:

```text
sqs:SendMessage
sqs:ReceiveMessage
sqs:DeleteMessage
```

on a particular queue.

Therefore:

```text
Spring Boot
 |
 | "I want to send message"
 ↓
AWS SDK
 |
 | IAM authentication
 ↓
SQS
```

AWS checks:

> Does this workload have permission to send a message to this queue?

If yes → message is accepted.

---

# 5. How does the consumer receive the message?

This is slightly different from normal REST communication.

With REST:

```text
Order Service
 |
 | HTTP request
 ↓
Notification Service
```

The Order Service needs to know where Notification Service is.

With SQS:

```text
Order Service
 |
 ↓
 SQS
 ↑
 |
Notification Service
```

The services are **decoupled**.

The Notification Service continuously polls the queue.

Conceptually:

```text
Notification Service
 |
 | ReceiveMessage()
 ↓
 SQS
 |
 ↓
 Message available?
 |
 YES
 |
 ↓
Return message
```

Then:

```text
Notification Service
 |
 ↓
Process message
 |
 ↓
Send notification
 |
 ↓
Delete message from SQS
```

---

# 6. Why use messaging instead of REST?

Imagine:

```text
Order Service → Payment Service → Notification Service → Email Service
```

If everything is synchronous:

```text
Order
 |
 ↓
Payment
 |
 ↓
Notification
 |
 ↓
Email
```

If Notification Service is down, your Order Service may also be affected.

With messaging:

```text
 ┌───────────────┐
 │ Order Service │
 └───────┬───────┘
 |
 ↓
 SQS
 |
 ┌──────────┴──────────┐
 ↓ ↓
 Payment Consumer Notification Consumer
```

Order Service can put the message into SQS and continue.

The consumer can process it later.

This gives you:

***Loose coupling**
***Asynchronous communication**
***Retry capability**
***Fault tolerance**
***Buffering**
***Independent scaling**

---

# 7. SQS vs SNS

This is another very important distinction.

### SQS

SQS is primarily a **queue**.

```text
Producer
 |
 ↓
 SQS
 |
 ↓
Consumer
```

Example:

```text
Order Service
 ↓
Order Queue
 ↓
Payment Service
```

Generally, messages are consumed from the queue by consumers.

---

### SNS

SNS is primarily a **pub/sub notification service**.

```text
 SNS Topic
 / | \
 / | \
 ↓ ↓ ↓
 SQS Lambda HTTP
```

Suppose Order Service publishes:

```text
ORDER_CREATED
```

SNS can fan that event out to multiple subscribers:

```text
 SNS
 |
 +------------+------------+
 | | |
 ↓ ↓ ↓
 SQS SQS Lambda
 Payment Email Analytics
```

This is called **fan-out**.

---

# 8. SQS + SNS together

In real production architectures, you will often see:

```text
 Spring Boot
 Order Service
 |
 |
 ↓
 SNS Topic
 ORDER_EVENTS
 / \
 / \
 ↓ ↓
 Payment Queue Notification Queue
 | |
 ↓ ↓
 Payment Service Notification Service
```

This is a powerful architecture.

The Order Service doesn't need to know:

```text
Who consumes my event?
Where is Payment Service?
Where is Notification Service?
```

It simply publishes:

```text
ORDER_CREATED
```

SNS handles distribution.

---

# 9. What about Kafka?

There is another important messaging technology you should know:

**Amazon MSK (Managed Streaming for Apache Kafka).**

Then the architecture becomes:

```text
Spring Boot
 |
 ↓
 Kafka Producer
 |
 ↓
Amazon MSK
 |
 ├───────────────┐
 ↓ ↓
Consumer 1 Consumer 2
```

Kafka is different from SQS.

A simplified comparison:

| | SQS | SNS | Kafka/MSK |
| ------------------------------- | ----------------------- | --------------------- | ------------------ |
| Type | Queue | Pub/Sub | Event streaming |
| Producer | Yes | Yes | Yes |
| Consumer | Yes | Subscribers | Consumers |
| Replay messages | Limited/different model | Not its primary model | Yes |
| Ordering | FIFO queues available | FIFO topics available | Partition ordering |
| Fan-out | Usually via SNS → SQS | Excellent | Excellent |
| High-throughput event streaming | Not primary use | Not primary use | Excellent |
| Operational complexity | Low | Low | Higher |

---

# 10. EKS + Spring Boot + SQS example

Your application might have:

```text
EKS Cluster
│
├── Order Pod
│ └── Spring Boot
│
├── Payment Pod
│ └── Spring Boot
│
└── Notification Pod
 └── Spring Boot

AWS Messaging
│
└── SQS
 │
 ├── payment-queue
 │
 └── notification-queue
```

The communication:

```text
Order Service
 |
 | SendMessage
 ↓
payment-queue
 |
 ↓
Payment Service
```

and:

```text
Order Service
 |
 | SendMessage
 ↓
notification-queue
 |
 ↓
Notification Service
```

---

# 11. Spring Boot code concept

With AWS SDK/Spring integration, your producer essentially does:

```java
@Service
public class OrderService {

 private final SqsTemplate sqsTemplate;

 public OrderService(SqsTemplate sqsTemplate) {
 this.sqsTemplate = sqsTemplate;
 }

 public void createOrder(Order order) {

 // Save order in DB

 sqsTemplate.send("order-queue", order);
 }
}
```

The important thing to understand is:

```text
sqsTemplate.send()
 ↓
AWS SDK
 ↓
HTTPS request
 ↓
SQS API
 ↓
SQS Queue
```

The consumer can receive/process messages through Spring's SQS listener support.

Conceptually:

```java
@SqsListener("order-queue")
public void processOrder(Order order) {

 // Process order

}
```

So your application code doesn't have to manually implement all the low-level AWS HTTP communication.

---

# 12. Very important: Where is the message actually stored?

Suppose:

```text
Order Service
 |
 ↓
 SQS
```

Once SQS accepts the message, the message is stored by **AWS SQS**, not inside your EKS Pod.

So if your Pod crashes:

```text
Order Pod
 X
 DOWN

 ↓

 SQS
 |
 | Message still available
 ↓
New Order Pod
```

The new Pod can consume the message.

This is one of the major benefits of messaging.

---

# 13. What if the consumer crashes?

Suppose:

```text
SQS
 |
 ↓
Payment Service
 |
 X
CRASH
```

The message isn't simply lost immediately.

SQS has concepts such as **visibility timeout**.

Simplified:

```text
Message
 ↓
Consumer receives
 ↓
Message temporarily hidden
 ↓
Consumer processes
 ↓
DeleteMessage()
```

If the consumer fails before successfully deleting the message:

```text
Consumer crashes
 ↓
Message becomes visible again
 ↓
Another consumer can process it
```

For messages that repeatedly fail, you can use a **Dead-Letter Queue (DLQ)**:

```text
 SQS
 |
 ↓
 Payment Service
 |
 Processing fails
 |
 ↓
 DLQ
```

---

# 14. The complete production picture

A realistic architecture could be:

```text
 INTERNET
 |
 ↓
 Application Load
 Balancer
 |
 ↓
 AWS EKS Cluster
 |
 +-----------------+-----------------+
 | | |
 ↓ ↓ ↓
 Order Service Payment Service Notification
 Spring Boot Spring Boot Spring Boot
 |
 | Publish Event
 ↓
 SNS Topic
 ORDER_EVENTS
 |
 +----+----+
 | |
 ↓ ↓
 SQS SQS
 Payment Notification
 Queue Queue
 | |
 ↓ ↓
 Payment Notification
 Service Service

 AWS
 |
 +--------------+-------------+
 | | |
 ↓ ↓ ↓
 RDS DynamoDB CloudWatch
```

### The communication chain to remember

```text
Spring Boot
 ↓
AWS SDK / Spring Cloud AWS
 ↓
HTTPS
 ↓
AWS API
 ↓
SNS / SQS / MSK
 ↓
Message
 ↓
Consumer Spring Boot application
 ↓
Business logic
```

### Interview answer

If an interviewer asks:

>**"How does your Spring Boot application running on EKS communicate with AWS messaging services?"**

A strong answer is:

> "Our Spring Boot services run as pods inside an EKS cluster. The application uses the AWS SDK or Spring Cloud AWS to interact with services such as SQS or SNS. The pod gets AWS permissions through an IAM role associated with its Kubernetes identity, rather than hard-coded credentials. The SDK communicates with the AWS messaging API over HTTPS. For asynchronous communication, the producer publishes a message to SQS or SNS. A consumer service running in another EKS pod consumes the message and processes it. We can use visibility timeouts, retries and Dead-Letter Queues to handle failures. SNS can be used when we need fan-out to multiple consumers, while SQS is commonly used for reliable queue-based asynchronous processing."

**One thing to clarify for interviews:** AWS **SQS/SNS are managed messaging services**, while **Amazon MSK is managed Kafka**. Calling SQS a "broker" in the same sense as Kafka/RabbitMQ is usually not the best terminology; SQS is a managed queue service.

If you're learning this for **AWS + Spring Boot microservices**, the next concept to master is the complete **`EKS → IAM → SQS → Producer → Consumer → Visibility Timeout → Retry → DLQ` flow**, because interviewers commonly ask you to explain this architecture end-to-end.

---

# Additional Production-Standard Guidance

The complete pasted source above is preserved. The following sections add the operational details that are normally required before an EKS messaging design is production-ready.

## 15. Choose the messaging service by delivery requirement

Start with the business behaviour, not the product name.

| Requirement | Suitable AWS pattern |
| --- | --- |
| One work item should be processed by one competing consumer | SQS queue |
| One event should reach several independent consumers | SNS topic with one SQS queue per consumer |
| Long-lived ordered event stream and replay | MSK/Kafka |
| Simple fan-out to AWS services | SNS subscriptions |
| Strict order and duplicate suppression for a queue workload | SQS FIFO, within its documented limits |
| Delayed or scheduled work | SQS delay, EventBridge Scheduler or a workflow service |
| Long-running orchestration | Step Functions or a dedicated workflow design |

Do not choose Kafka only because it is popular. Kafka introduces partitions, consumer groups, broker capacity, retention, rebalancing and operational ownership. Do not choose SQS when consumers need arbitrary historical replay or stream-processing semantics.

## 16. Delivery semantics: assume at-least-once

Standard SQS and most SNS-to-SQS designs provide **at-least-once delivery**. A message can be delivered more than once. A successful `DeleteMessage` does not mean your business operation can never be repeated because a consumer can fail after committing business work but before deleting the message.

Therefore, consumers should be idempotent:

```text
Receive message
 |
 v
Check business idempotency key
 |
 +-- Already processed -> acknowledge/delete safely
 |
 `-- New message -> perform work and record completion
```

Use a stable event ID or business operation ID:

```json
{
"eventId": "01JORDER1001-ORDER_CREATED",
"eventType": "ORDER_CREATED",
"eventVersion": 1,
"occurredAt": "2026-09-19T10:15:00Z",
"producer": "order-service",
"correlationId": "request-abc-123",
"orderId": "1001",
"payload": {
"customerId": "5001",
"amount": 2500
 }
}
```

A database-backed idempotency record can enforce uniqueness:

```sql
create table processed_message (
 consumer_name varchar(100) not null,
 event_id varchar(200) not null,
 processed_at timestamp not null,
 primary key (consumer_name, event_id)
);
```

The exact transaction design depends on the business operation. The key point is that “exactly once” is rarely an end-to-end property of a distributed system. Design for at-least-once delivery and idempotent effects.

## 17. The dual-write problem and the transactional outbox

This code has a failure window:

```java
orderRepository.save(order);
sqsTemplate.send("order-queue", event);
```

If the database commit succeeds and the process crashes before the SQS call, the order exists but the event is missing. If SQS accepts the message and the database transaction later rolls back, consumers may observe an event for an order that does not exist.

For important business events, use a transactional outbox:

```text
HTTP request
 |
 v
One database transaction
 +-- save order
 `-- save outbox event as NEW
 |
 v
 Outbox publisher
 |
 v
 SQS/SNS
 |
 v
 mark outbox event SENT
```

Example table:

```sql
create table outbox_event (
 id varchar(200) primary key,
 aggregate_type varchar(100) not null,
 aggregate_id varchar(200) not null,
 event_type varchar(100) not null,
 payload json not null,
 status varchar(20) not null,
 attempt_count integer not null default 0,
 created_at timestamp not null,
 published_at timestamp null
);
```

The publisher must still be idempotent because it can publish the same outbox row more than once. Consumers must remain idempotent too. Alternatives include change data capture and managed event integration, but the reliability goal is the same: make the database change and the intent to publish durable together.

## 18. Event contract design

Do not publish an arbitrary Java entity as a long-lived integration contract. Define a versioned event DTO:

```java
public record OrderCreatedEvent(
 String eventId,
 String eventType,
 int eventVersion,
 Instant occurredAt,
 String correlationId,
 String orderId,
 String customerId,
 BigDecimal amount) {
}
```

Good event contracts are:

- Explicitly versioned.
- Backward compatible where possible.
- Independent of database column names.
- Free from passwords, access tokens and unnecessary personal data.
- Stable enough for consumers to evolve independently.
- Documented with examples and ownership.

Prefer additive changes. If a field must be removed or its meaning changed, publish a new event version and define the migration period.

## 19. Spring Boot producer example

With Spring Cloud AWS 3.x-style APIs, a producer can use `SqsTemplate`:

```java
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventPublisher {

 private final SqsTemplate sqsTemplate;

 public OrderEventPublisher(SqsTemplate sqsTemplate) {
 this.sqsTemplate = sqsTemplate;
 }

 public void publish(OrderCreatedEvent event) {
 sqsTemplate.send(to -> to
 .queue("order-events")
 .payload(event)
 .header("eventType", event.eventType())
 .header("eventId", event.eventId())
 .header("correlationId", event.correlationId()));
 }
}
```

The exact builder methods depend on the Spring Cloud AWS version. Pin compatible dependency versions and verify the API against the version in `pom.xml`; do not copy a code sample from a different major release without checking it.

## 20. Spring Boot consumer example

```java
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventConsumer {

 private final NotificationService notificationService;

 public NotificationEventConsumer(NotificationService notificationService) {
 this.notificationService = notificationService;
 }

 @SqsListener("notification-queue")
 public void receive(OrderCreatedEvent event) {
 notificationService.sendOrderConfirmation(event);
 }
}
```

The listener container receives messages and manages acknowledgement according to its configuration. The handler should throw or otherwise signal failure when processing was not completed, so the message can become visible again or move to the DLQ according to the configured policy. Do not catch every exception and return normally; that can acknowledge a message whose business operation failed.

## 21. Visibility timeout and acknowledgement timing

Visibility timeout is the period during which a received SQS message is hidden from other consumers. It is not a permanent lock.

Choose a timeout longer than the normal processing duration, with room for transient latency:

```text
Receive message
 |
 | visibility timeout starts
 v
Process business operation
 |
 +-- success -> delete/acknowledge
 |
 `-- failure or timeout -> message becomes visible again
```

If processing can exceed the initial timeout, extend visibility while work is active or redesign the job. Avoid setting an extremely large timeout as a substitute for failure handling because it delays retries and DLQ movement.

Track:

- Approximate age of the oldest message.
- Receive count and DLQ count.
- Processing duration percentiles.
- Visibility timeout expirations.
- Successful and failed acknowledgements.

## 22. Retry policy and DLQ design

Retries should distinguish transient failures from permanent failures:

```text
Temporary network failure -> retry with backoff
Rate limit -> retry with backoff and jitter
Invalid message contract -> route to DLQ
Missing required entity -> business decision, often DLQ
Permission failure -> alert and stop retry storm
```

Configure a redrive policy with a maximum receive count appropriate to the operation. A DLQ is not a trash can. It needs:

```text
Alert
 -> inspect message and error metadata
 -> classify root cause
 -> repair data/code/configuration
 -> replay safely or archive
 -> record the incident and outcome
```

Never replay a DLQ blindly if the consumer is not idempotent. A replay can create duplicate emails, charges or downstream updates.

## 23. Standard versus FIFO SQS queues

### Standard queue

Use when high throughput and at-least-once delivery are more important than strict ordering. Consumers must tolerate duplicates and possible reordering.

### FIFO queue

Use when the workflow requires ordering within a message group and deduplication features fit the use case. FIFO design still requires careful message-group selection because one busy group can serialize work and reduce throughput.

```text
Order 1001 -> MessageGroup order-1001
Order 1002 -> MessageGroup order-1002
```

Ordering is not global unless the system is deliberately serialized. Document whether the requirement is ordering per order, customer, account or partition.

## 24. SNS to SQS fan-out details

For reliable consumers, subscribe one SQS queue per independent consumer to the SNS topic:

```text
 SNS topic
 ORDER_EVENTS
 / \
 v v
 payment-queue notification-queue
 | |
 payment service notification service
```

Each queue gets its own retry, visibility timeout, DLQ and scaling policy. If the notification service is down, its queue grows without blocking payment processing.

Use SNS message filtering when consumers need different event types, but keep filtering rules documented and tested. Decide whether consumers should receive the SNS envelope or use raw message delivery. The choice affects deserialization and observability.

## 25. IAM least privilege for EKS workloads

Create separate Kubernetes service accounts and IAM permissions per workload where practical:

```text
order-service service account
 -> publish to order-events topic only

payment-service service account
 -> receive/delete payment-queue only

notification-service service account
 -> receive/delete notification-queue only
```

Do not give every pod administrator access or `sqs:*` on every queue. Restrict both actions and resources. Typical producer permissions might include `sqs:SendMessage`; a consumer may need `sqs:ReceiveMessage`, `sqs:DeleteMessage` and `sqs:ChangeMessageVisibility` if it extends processing time.

Use short-lived workload credentials through EKS Pod Identity or IRSA as appropriate for the cluster. Keep the AWS SDK default credential provider chain enabled rather than embedding access keys in properties, ConfigMaps, images or source code.

## 26. EKS Pod Identity and IRSA distinction

Both approaches associate AWS permissions with a Kubernetes workload, but their setup differs:

```text
EKS Pod Identity
 -> EKS-native association between a pod identity and IAM role

IRSA
 -> OIDC-backed web identity token and service-account annotation
```

The correct option depends on cluster version, platform standards and account setup. The application should generally use the AWS SDK credential provider chain in both cases. The application should not need to know whether the role came from Pod Identity or IRSA.

## 27. EKS networking path

The application needs network access to the AWS service endpoint:

```text
Pod
 -> node/pod network
 -> VPC routing
 -> NAT gateway or VPC endpoint
 -> regional AWS API endpoint
 -> SQS/SNS
```

For private EKS clusters, evaluate VPC endpoints such as the appropriate AWS service endpoint instead of assuming a public internet path. Verify:

- Route tables.
- Security groups and network policies.
- DNS resolution.
- NAT capacity and cost.
- VPC endpoint policy.
- AWS region configuration.
- Proxy behaviour, if present.

IAM permission and network reachability are separate checks. A pod can have the right role and still fail because it cannot resolve or reach the endpoint. Conversely, it can reach the endpoint and still receive `AccessDenied`.

## 28. Configuration example

Keep environment-specific names and regions outside application code:

```yaml
spring:
 cloud:
 aws:
 region:
 static: ${AWS_REGION:us-east-1}
 credentials:
 use-default-aws-credentials-chain: true

app:
 messaging:
 order-events-queue: ${ORDER_EVENTS_QUEUE}
 notification-queue: ${NOTIFICATION_QUEUE}
```

Prefer queue URLs or resolved queue names supplied through deployment configuration according to the library's conventions. Validate required configuration at startup, but never log credentials or sensitive message payloads.

## 29. Scaling consumers in EKS

CPU usage alone is often a poor signal for queue consumers. A service can be CPU-idle while the queue backlog grows because it is waiting on a downstream API.

Useful scaling signals include:

```text
Queue depth
Approximate age of oldest message
Processing latency
Consumer concurrency
Downstream rate limits
CPU and memory
```

Kubernetes HPA can scale on resource or external metrics. KEDA can be used where the platform permits event-driven scaling from queue metrics. Set upper bounds so a backlog does not create an uncontrolled downstream request storm.

A simple capacity model is:

```text
required consumers ~= incoming messages per second
 / messages processed per consumer per second
```

Measure this with realistic payloads and downstream latency; do not rely only on local benchmarks.

## 30. Observability and correlation

Every message should carry enough metadata to trace a business operation without exposing unnecessary personal data:

```text
eventId
correlationId
causationId
eventType
eventVersion
producer
occurredAt
trace context where supported
```

Log structured fields rather than entire sensitive payloads:

```text
eventType=ORDER_CREATED
eventId=...
correlationId=...
queue=notification-queue
receiveCount=2
processingMs=184
outcome=SUCCESS
```

Monitor:

- Queue depth and oldest-message age.
- DLQ message count and age.
- Send, receive, delete and failure rates.
- Consumer processing latency.
- Authentication and authorization failures.
- Throttling and downstream dependency errors.
- Pod restarts and readiness failures.
- Trace continuity from HTTP request to consumer.

CloudWatch is useful for AWS metrics and logs; application metrics should expose business outcomes and processing latency through the team's standard observability stack.

## 31. Security and data protection

Use encryption in transit through AWS service endpoints and encryption at rest according to the organization's requirements. If customer or payment-related data is involved:

- Send the minimum necessary payload.
- Prefer identifiers over full personal records.
- Use KMS customer-managed keys where required.
- Restrict key permissions separately from queue permissions.
- Define message retention and deletion requirements.
- Avoid logging message bodies, tokens and payment data.
- Protect DLQs because they often contain failed sensitive payloads.

Message integrity and authorization are separate from transport encryption. A consumer must still validate schema, tenant ownership and allowed event types.

## 32. Testing strategy

Use multiple test layers:

```text
Unit test
 -> event mapping, validation, idempotency decisions

Component test
 -> producer/consumer serialization and listener configuration

Integration test
 -> AWS emulator or isolated AWS resources where appropriate

Contract test
 -> producer and consumer agree on event schema/version

End-to-end test
 -> order created -> event -> notification outcome
```

Test duplicate delivery explicitly:

```text
Send same event twice
 -> business action occurs once
 -> both deliveries are acknowledged safely
```

Also test malformed messages, downstream timeout, permission failure, visibility timeout expiry, DLQ routing, replay and graceful shutdown.

## 33. Graceful shutdown and deployment safety

During an EKS rollout, a consumer should stop accepting new work, finish or safely abandon in-flight messages, and then terminate within the pod grace period. Configure readiness and termination behaviour so traffic and message intake drain before the process exits.

Consider:

```text
SIGTERM
 -> mark pod unready
 -> stop new message polling
 -> finish bounded in-flight work
 -> acknowledge only successful work
 -> exit before termination deadline
```

A message that is not acknowledged can be retried after visibility timeout. That is preferable to acknowledging work that did not complete.

## 34. Common failure diagnosis

### `AccessDenied`

Check the pod identity, assumed role, action, resource ARN, region and queue/topic policy. Confirm that the request is coming from the intended service account.

### Messages remain in the queue

Check listener startup, queue name/URL, region, network path, IAM receive permission and application logs. Check whether messages are invisible because another consumer holds them.

### Messages return repeatedly

Check handler exceptions, visibility timeout, downstream latency, acknowledgement mode and receive count. Inspect the DLQ before increasing retries.

### Duplicate business actions

Assume at-least-once delivery. Add an idempotency key and durable deduplication record; do not rely only on an in-memory set in one pod.

### Backlog grows after scaling

Check downstream capacity, listener concurrency, long polling, connection pools, CPU/memory limits and whether new pods have the correct identity/configuration.

### Events disappear from the expected consumer

For SNS fan-out, verify the subscription, filter policy, confirmation state, queue policy and whether the consumer is reading its own queue rather than a shared queue.

## 35. Long polling, batching and cost

Consumers should generally use long polling rather than repeatedly making empty short polls. Batch receive, processing and deletion can improve throughput, but partial failures must be handled carefully.

For a batch:

```text
Receive messages A, B, C
 -> A succeeds
 -> B fails
 -> C succeeds
```

Do not acknowledge B merely because A and C succeeded. Use the library's partial-batch failure support or explicitly preserve the failed message for retry. Benchmark batch size against memory, latency, visibility timeout and downstream limits.

## 36. Event ordering and business correctness

If `ORDER_CANCELLED` can arrive before `ORDER_CREATED`, do not assume network arrival order equals business order. Options include:

- Use a FIFO/message-group design where appropriate.
- Store event versions and reject or defer out-of-order events.
- Make handlers query authoritative state before applying a transition.
- Model the process as a workflow with explicit state transitions.

Ordering alone does not solve duplicate delivery, retries or cross-service transactions.

## 37. Recommended production architecture

```text
 Internet
 |
 v
 ALB / API Gateway
 |
 v
 EKS pods
 +--------------+--------------+
 | |
 Order API Consumers
 | |
 v v
 Order database SQS queues
 | / \
 v v v
 Transactional outbox Payment Notification
 | service service
 v | |
 SNS ORDER_EVENTS v v
 | downstream email provider
 +--------> payment and notification queues

Identity: EKS Pod Identity or IRSA
Network: private subnets and approved VPC endpoints/NAT
Operations: CloudWatch, tracing, alerts, DLQ runbook
```

The most important boundaries are:

```text
EKS deployment -> workload identity
Application -> AWS SDK client
AWS service -> durable message storage
Consumer -> idempotent business operation
Operations -> metrics, alerts, retry and replay
```

## 38. Improved interview answer

> Our Spring Boot services run as pods in EKS and use the AWS SDK or Spring Cloud AWS. Workload permissions come from EKS Pod Identity or IRSA and are limited to the required queue or topic actions; credentials are never hard-coded. The SDK calls regional SQS or SNS APIs over HTTPS, using private networking or approved VPC endpoints where required. We use SNS with one SQS queue per independent consumer for fan-out, and SQS directly for a work queue. Consumers assume at-least-once delivery, so handlers are idempotent and carry a stable event ID. The database write and publication intent use a transactional outbox for important events. Visibility timeout, bounded retries and a DLQ handle failures. We monitor queue age, backlog, processing latency, receive count and DLQ volume, and scale consumers using queue metrics while respecting downstream limits. For replay, we repair the cause and reprocess safely rather than blindly redriving messages.

## 39. Final checklist

Before presenting this architecture in an interview or production review, verify:

- The service choice matches queue, fan-out or streaming requirements.
- The producer uses workload identity, not static AWS keys.
- IAM permissions are least-privilege and resource-scoped.
- Private-cluster network access to AWS APIs is designed and tested.
- Event contracts have IDs, versions, timestamps and correlation data.
- Important database-plus-message flows address the dual-write problem.
- Consumers are idempotent and tolerate duplicate delivery.
- Visibility timeout exceeds normal bounded processing time.
- Retry and DLQ policies distinguish transient and permanent failures.
- Each SNS consumer has an independent queue when isolation is required.
- Queue backlog and oldest-message age drive operational alerts.
- Scaling is bounded by downstream capacity.
- Sensitive payloads are minimized, encrypted and not logged.
- Graceful shutdown protects in-flight messages during deployments.
- Integration, contract, duplicate, failure and replay tests exist.
- A documented DLQ inspection and replay runbook is available.

The senior-level lesson is simple: sending a message is only the beginning. A production messaging design must explain identity, network access, durability, delivery semantics, idempotency, retries, ordering, scaling, observability, security and recovery.