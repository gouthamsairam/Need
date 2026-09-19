Absolutely. Below is a *Senior Software Architect + Technical Trainer + Interviewer style guide* for *Amazon DynamoDB, Amazon SQS, and Amazon CloudWatch*.

I’ll explain each service in this order:

1. What it is
2. Why it exists / problem it solves
3. Real-world analogy
4. Core concepts
5. Internal working
6. Architecture
7. Production example
8. Spring Boot/Java integration
9. Performance & scalability
10. Best practices
11. Common mistakes
12. Comparisons
13. System-design perspective
14. Interview questions
15. Scenario-based questions
16. Quick revision cheat sheet

---

# 1. Big Picture First

Before learning the individual services, understand *why these three services are often used together*.

Imagine an e-commerce application:

text
Customer
|
v
React / Mobile
|
v
API Gateway / ALB
|
v
Spring Boot Service
|
+----------+----------+
| |
v v
DynamoDB SQS
| |
| v
| Background Worker
| |
| v
+-------------> DynamoDB

+
|
v
CloudWatch
/ | \
/ | \
Logs Metrics Alarms


Think of them like this:

| AWS Service | Simple meaning | Main responsibility |
| -------------- | -------------------- | ------------------------------------------------- |
| *DynamoDB* | Fast digital storage | Store application data |
| *SQS* | Digital waiting line | Decouple services and process work asynchronously |
| *CloudWatch* | Control room | Monitor applications and AWS resources |

### One-line memory trick

>*DynamoDB stores, SQS waits, CloudWatch watches.*

---

# PART 1 — AMAZON DYNAMODB

# 2. What is DynamoDB?

*Amazon DynamoDB* is a fully managed, serverless *NoSQL database* provided by AWS.

It is designed for:

* Very high traffic
* Very low latency
* Massive scalability
* Flexible schema
* Distributed applications

Unlike traditional relational databases such as MySQL or PostgreSQL, DynamoDB does not primarily model data using:

text
Database
↓
Tables
↓
Rows
↓
Columns


Instead, it uses:

text
Table
↓
Items
↓
Attributes


For example:

text
Customer Table

customerId name email
-------------------------------------
C101 John john@gmail.com
C102 David david@gmail.com


An item could also contain different attributes:

text
{
"customerId": "C101",
"name": "John",
"email": "john@gmail.com",
"phone": "9999999999",
"address": {
"city": "Hyderabad"
}
}


Another item might have:

text
{
"customerId": "C102",
"name": "David",
"email": "david@gmail.com"
}


The schema is much more flexible than a traditional relational database.

---

# 3. Why was DynamoDB created?

Traditional databases can become difficult to scale horizontally when traffic becomes extremely large.

Suppose your application initially has:

text
1,000 requests/sec


Then suddenly:

text
10,000 requests/sec


Then:

text
100,000 requests/sec


A traditional architecture may require:

text
Bigger server
↓
Read replicas
↓
Sharding
↓
Partitioning
↓
Connection management


DynamoDB is designed around distributed horizontal scaling.

Conceptually:

text
DynamoDB
|
+----------+----------+
| | |
Partition Partition Partition
| | |
Data Data Data


AWS manages much of the infrastructure for you.

---

# 4. Real-world analogy

Imagine a huge library.

A traditional approach could be:

text
One huge room
|
Millions of books


Finding and managing everything becomes difficult.

DynamoDB is more like:

text
Library
|
+--- Section A
|
+--- Section B
|
+--- Section C
|
+--- Section D


Books are distributed across sections.

When more books arrive, more sections can be added.

That is similar to *horizontal scaling and partitioning*.

---

# 5. Core DynamoDB Concepts

You must understand these for interviews.

### Table

Container for items.

text
Customer
Order
Product
Payment


### Item

Equivalent roughly to a row.

json
{
"customerId": "C101",
"name": "John"
}


### Attribute

Equivalent roughly to a column.

text
customerId
name
email
age


### Primary Key

Used to uniquely identify an item.

There are two major types.

### Simple Primary Key

text
Partition Key


Example:

text
customerId


### Composite Primary Key

text
Partition Key + Sort Key


Example:

text
PK = customerId
SK = orderId


Example:

text
customerId orderId amount
-----------------------------------
C101 O1001 500
C101 O1002 700
C102 O1003 900


---

# 6. Partition Key

This is one of the *most important DynamoDB interview topics*.

Suppose:

text
customerId


is your partition key.

DynamoDB uses a hashing mechanism to determine where the item should be stored.

Conceptually:

text
customerId
|
v
Hash Function
|
v
Partition


For example:

text
C101 → Hash → Partition A

C102 → Hash → Partition B

C103 → Hash → Partition C


This allows DynamoDB to distribute traffic.

---

# 7. What is a Hot Partition?

Suppose you design:

text
partitionKey = country


And 80% of your traffic is:

text
country = INDIA


Then:

text
INDIA
|
v
Same partition
|
v
Huge traffic
|
v
HOT PARTITION


This is bad.

A good partition key should generally provide *high cardinality and good distribution of traffic*.

Examples:

text
customerId
orderId
transactionId
deviceId


are usually better candidates than:

text
country
status
gender


depending on access patterns.

---

# 8. Sort Key

Sort key allows multiple related items to be grouped under the same partition key.

Example:

text
PK = CUSTOMER#101
SK = ORDER#1001

PK = CUSTOMER#101
SK = ORDER#1002

PK = CUSTOMER#101
SK = ORDER#1003


Now you can efficiently query:

text
All orders for customer 101


because they share the same partition key.

---

# 9. Query vs Scan

Very important interview question.

### Query

Uses the key.

text
Partition Key = C101


DynamoDB can directly locate the relevant partition.

Conceptually:

text
Query
↓
Partition Key
↓
Relevant partition
↓
Items


Fast and efficient.

### Scan

Reads items across the table.

text
Scan
↓
Partition 1
↓
Partition 2
↓
Partition 3
↓
Partition 4


Potentially expensive.

### Interview answer

> Prefer Query over Scan whenever possible. Design the table around access patterns so that required data can be retrieved using keys and indexes.

---

# 10. DynamoDB Indexes

Two major types:

### Local Secondary Index — LSI

Uses:

text
Same partition key
Different sort key


### Global Secondary Index — GSI

Can use a different partition key and sort key.

Example:

Main table:

text
PK = customerId
SK = orderId


GSI:

text
PK = orderStatus
SK = createdAt


Now you can query:

text
PENDING orders


efficiently.

---

# 11. DynamoDB Consistency

DynamoDB supports:

### Eventually Consistent Reads

You may temporarily receive slightly older data.

text
Write
↓
Replica A
↓
Replica B
↓
Replica C

Replication happens


### Strongly Consistent Reads

Request the latest committed value from the supported read path.

Trade-off:

text
Strong consistency
↓
Higher consistency guarantee
↓
Potentially higher cost/latency considerations


---

# 12. DynamoDB Internal Working

Let's say:

text
PUT customer C101


### Step 1

Application sends:

text
PutItem


### Step 2

DynamoDB examines:

text
Partition Key = C101


### Step 3

DynamoDB hashes the partition key.

text
C101
↓
Hash
↓
Partition location


### Step 4

The item is stored in the appropriate distributed storage infrastructure.

### Step 5

DynamoDB handles replication and availability internally.

### Step 6

Application receives the result.

Conceptually:

text
Spring Boot
|
| PutItem
v
DynamoDB
|
+----> Partition A
|
Distributed
replication


You don't manage database servers directly.

---

# 13. DynamoDB Capacity Modes

Two important modes:

### Provisioned

You specify expected capacity.

text
Read Capacity
Write Capacity


Useful when traffic is predictable.

### On-Demand

DynamoDB automatically handles changing traffic without you specifying capacity in advance.

Useful for:

* Unpredictable workloads
* Variable traffic
* New applications

---

# 14. DynamoDB Production Example

Suppose you're building an e-commerce application.

You need to store shopping carts.

text
Cart Table

PK = CUSTOMER#101
SK = PRODUCT#P1001


Example:

json
{
"PK": "CUSTOMER#101",
"SK": "PRODUCT#P1001",
"productName": "Laptop",
"quantity": 2,
"price": 70000
}


Retrieve customer's cart:

text
Query
PK = CUSTOMER#101


This is much better than scanning the entire table.

---

# 15. Java/Spring Boot Example

Using AWS SDK for Java:

java
DynamoDbClient dynamoDbClient =
DynamoDbClient.builder()
.region(Region.AP_SOUTH_1)
.build();


Create an item:

java
Map<String, AttributeValue> item = new HashMap<>();

item.put("customerId",
AttributeValue.builder().s("C101").build());

item.put("name",
AttributeValue.builder().s("John").build());

item.put("email",
AttributeValue.builder().s("john@gmail.com").build());

PutItemRequest request = PutItemRequest.builder()
.tableName("Customer")
.item(item)
.build();

dynamoDbClient.putItem(request);


### What happens?

text
Java Application
|
v
AWS SDK
|
v
PutItem API
|
v
DynamoDB


---

# 16. DynamoDB Advantages

* Fully managed
* Serverless
* High scalability
* Low latency
* Automatic infrastructure management
* Highly available
* Flexible schema
* Good for massive workloads

---

# 17. DynamoDB Disadvantages

This is important.

DynamoDB is *not a replacement for every database*.

It is not ideal when you need:

* Complex joins
* Ad-hoc SQL queries
* Highly relational data
* Complex transactions across many entities
* Flexible reporting queries

For example:

sql
SELECT *
FROM orders o
JOIN customers c
ON o.customer_id = c.id
JOIN products p
ON o.product_id = p.id;


This relational workload may be better suited to:

text
Aurora / RDS


---

# PART 2 — AMAZON SQS

# 18. What is SQS?

*Amazon Simple Queue Service (SQS)* is a managed message queue.

Its main purpose is:

>*Decouple producers and consumers.*

Instead of:

text
Order Service
|
v
Payment Service


we can have:

text
Order Service
|
v
SQS
|
v
Payment Service


---

# 19. Real-world analogy

Imagine a restaurant.

Customer:

text
Customer
|
v
Counter


The counter gives the order a token:

text
Token #101


Kitchen processes orders one by one.

The customer doesn't stand inside the kitchen waiting.

That's similar to SQS.

text
Producer
|
v
Queue
|
v
Consumer


---

# 20. What problem does SQS solve?

Without SQS:

text
Order Service
|
v
Payment Service


If Payment Service is down:

text
Order Service
|
X
Payment DOWN


The request may fail.

With SQS:

text
Order Service
|
v
SQS
|
X
Payment DOWN


The message stays in the queue.

When Payment Service recovers:

text
SQS
|
v
Payment Service


This provides *decoupling and resilience*.

---

# 21. SQS Architecture

text
Producer
|
v
+---------------+
| SQS |
| Queue |
+---------------+
|
+-------+-------+
| |
v v
Consumer 1 Consumer 2


Multiple consumers can process messages.

---

# 22. SQS Standard vs FIFO

Very important.

## Standard Queue

Designed for:

* Very high throughput
* At-least-once delivery
* Best-effort ordering

Example:

text
Message A
Message B
Message C


Consumer could potentially receive:

text
B
A
C


For workloads where strict ordering isn't required, Standard is often appropriate.

---

# 23. FIFO Queue

FIFO means:

> First In, First Out.

Useful when ordering matters.

Example:

text
Payment Initiated
↓
Payment Authorized
↓
Payment Completed


You don't want:

text
Payment Completed
↓
Payment Initiated


FIFO provides stronger ordering guarantees and deduplication capabilities, subject to its queue semantics and configuration.

---

# 24. SQS Message Lifecycle

This is an important interview topic.

Suppose producer sends:

text
Order #1001


### Step 1

Producer sends message:

text
Order Service
|
v
SQS


### Step 2

Message waits in queue.

text
SQS
|
+--- Message 1001
+--- Message 1002
+--- Message 1003


### Step 3

Consumer calls:

text
ReceiveMessage


### Step 4

SQS makes the message temporarily invisible.

This is called:

>*Visibility Timeout*

### Step 5

Consumer processes it.

### Step 6

Consumer successfully calls:

text
DeleteMessage


### Step 7

Message disappears from the queue.

---

# 25. Visibility Timeout

One of the most frequently asked SQS questions.

Suppose:

text
Visibility Timeout = 30 seconds


Consumer receives:

text
Order #1001


For the next 30 seconds, other consumers normally won't receive that message.

text
SQS
|
|-- Order 1001
↓
Consumer
|
30 seconds
|
v
Delete message


If the consumer crashes:

text
Consumer
|
X
Crash


and the message isn't deleted, it can become visible again after the visibility timeout.

This is why consumers should be *idempotent*.

---

# 26. Dead Letter Queue — DLQ

Suppose a message repeatedly fails.

text
SQS
|
v
Consumer
|
X
Failure
|
v
Retry
|
X
Failure
|
v
Retry
|
X
Failure
|
v
DLQ


A *Dead Letter Queue* stores messages that couldn't be processed successfully after the configured retry policy.

This helps developers investigate problematic messages without blocking normal processing.

---

# 27. SQS Java Example

Using AWS SDK:

java
SqsClient sqsClient =
SqsClient.builder()
.region(Region.AP_SOUTH_1)
.build();


Send message:

java
SendMessageRequest request =
SendMessageRequest.builder()
.queueUrl(queueUrl)
.messageBody("ORDER_CREATED:1001")
.build();

sqsClient.sendMessage(request);


Consumer:

java
ReceiveMessageRequest request =
ReceiveMessageRequest.builder()
.queueUrl(queueUrl)
.maxNumberOfMessages(10)
.build();

List<Message> messages =
sqsClient.receiveMessage(request).messages();


After successful processing:

java
DeleteMessageRequest deleteRequest =
DeleteMessageRequest.builder()
.queueUrl(queueUrl)
.receiptHandle(message.receiptHandle())
.build();

sqsClient.deleteMessage(deleteRequest);


---

# 28. Production SQS Example

Imagine an e-commerce order.

text
Order Service
|
| Order Created
v
SQS
|
+------------------+
| |
v v
Inventory Service Notification Service
| |
v v
DynamoDB SES/SMS


Now Order Service doesn't have to wait for every downstream operation.

This is:

>*Asynchronous processing*

---

# 29. SQS Advantages

* Highly scalable
* Fully managed
* Decouples services
* Handles traffic spikes
* Improves resilience
* Supports retries
* Supports DLQ
* Supports Standard and FIFO queues

---

# 30. SQS Disadvantages

* Asynchronous processing adds complexity
* Duplicate messages can occur with at-least-once delivery
* Debugging distributed workflows can be harder
* Ordering isn't guaranteed in Standard queues
* Consumers must be designed carefully

---

# 31. Idempotency

Extremely important for production systems.

Suppose:

text
Payment = ₹10,000


Message is processed.

But consumer crashes *after performing the payment and before deleting the SQS message*.

SQS may deliver it again.

Without idempotency:

text
₹10,000
+
₹10,000
=
₹20,000


Very bad.

With idempotency:

text
Transaction ID = TX1001

First request:
TX1001 → processed

Second request:
TX1001 → already processed


So the consumer doesn't execute the business operation twice.

For FinTech, *idempotency is critical*.

---

# PART 3 — AMAZON CLOUDWATCH

# 32. What is CloudWatch?

*Amazon CloudWatch* is AWS's monitoring and observability platform.

Think:

>*CloudWatch = Eyes and ears of your AWS application.*

It collects and helps visualize:

* Metrics
* Logs
* Events
* Alarms
* Application health information

---

# 33. Real-world analogy

Imagine a hospital.

text
Patient
|
+-- Heart rate
+-- Blood pressure
+-- Temperature
+-- Oxygen


Doctors monitor those values.

Similarly:

text
Application
|
+-- CPU
+-- Memory
+-- Request count
+-- Error count
+-- Latency
+-- Queue depth


CloudWatch monitors these signals.

---

# 34. CloudWatch Core Components

## Metrics

Numerical measurements.

Examples:

text
CPUUtilization = 75%
RequestCount = 10,000
ErrorCount = 500
QueueDepth = 10,000
Latency = 300 ms


---

## Logs

Application/system logs.

Spring Boot might produce:

text
2026-09-09 20:30:12 INFO Order created: 1001

2026-09-09 20:30:14 ERROR Payment failed


These logs can be sent to CloudWatch Logs.

---

## Alarms

CloudWatch can monitor a metric and trigger an action.

Example:

text
CPU > 80%
|
v
CloudWatch Alarm
|
v
Notification / automated action


---

# 35. CloudWatch Internal Flow

text
Application
|
v
Metrics / Logs
|
v
CloudWatch
|
+---+---+
| |
v v
Alarm Dashboard
|
v
SNS / Automation


---

# 36. CloudWatch + SQS

This combination is very important.

Suppose SQS contains:

text
100 messages


Suddenly:

text
10,000 messages


CloudWatch can monitor:

text
ApproximateNumberOfMessagesVisible


Conceptually:

text
SQS
|
| Queue depth
v
CloudWatch
|
| Threshold exceeded
v
Alarm
|
v
Operations team


You can then scale consumers or investigate the bottleneck.

---

# 37. CloudWatch + Spring Boot

Your Spring Boot application can generate logs:

java
logger.info("Order created: {}", orderId);

logger.error("Payment processing failed", exception);


These logs can be shipped into CloudWatch Logs depending on your deployment architecture.

Example:

text
Spring Boot
|
v
Container / EC2
|
v
CloudWatch Logs


Then you can search:

text
ERROR
Payment
Order ID
Exception


---

# 38. CloudWatch Metrics for Production

For a Java microservice, monitor:

### Application

text
Request Count
Error Rate
Latency
Throughput


### JVM

text
Heap Usage
GC activity
Thread count


### Infrastructure

text
CPU
Memory
Disk
Network


### Database

text
DynamoDB throttling
Read/write activity
Latency


### Queue

text
Messages available
Messages in flight
Oldest message age


---

# 39. What is an Alarm?

Suppose:

text
Error rate > 5%


CloudWatch:

text
Metric
|
v
Threshold
|
v
Alarm
|
v
Notification


The notification could involve SNS or another supported integration/action.

---

# 40. Production Architecture — All Three Together

Now let's combine everything.

## E-commerce example

text
CUSTOMER
|
v
React / Mobile
|
v
API Gateway / ALB
|
v
Spring Boot API
|
+----------+----------+
| |
v v
DynamoDB SQS
| |
| +------+------+
| | |
| v v
| Payment Worker Notification
| | |
| v v
| DynamoDB Email
|
|
+----------------------+
|
v
CloudWatch
/ | \
/ | \
Metrics Logs Alarms


---

# 41. Complete Request Flow

Customer:

text
Place Order


### Step 1

Request reaches Spring Boot.

text
POST /orders


### Step 2

Spring Boot validates the request.

### Step 3

Order is stored:

text
Spring Boot
|
v
DynamoDB


### Step 4

Application sends asynchronous event:

text
Order Created
|
v
SQS


### Step 5

Payment worker consumes:

text
SQS
|
v
Payment Service


### Step 6

Payment result is persisted.

### Step 7

Notification service consumes another event/message.

### Step 8

CloudWatch monitors everything.

text
Application
Database
Queue
Infrastructure


---

# 42. Why Not Direct Communication?

Without SQS:

text
Order
|
v
Payment
|
v
Inventory
|
v
Notification


The request can become tightly coupled.

If Notification Service is slow:

text
Order
|
v
Payment
|
v
Inventory
|
X
Notification slow


The overall workflow can suffer.

With SQS:

text
Order
|
v
SQS
|
+----> Payment
|
+----> Inventory
|
+----> Notification


Each consumer can process independently.

---

# 43. DynamoDB vs RDS

| Feature | DynamoDB | RDS |
| ----------------- | --------------------------------------- | ------------------------------ |
| Type | NoSQL | Relational |
| Schema | Flexible | Structured |
| SQL | No traditional SQL model | Yes |
| Joins | No traditional joins | Yes |
| Scaling | Horizontal/serverless-oriented | Vertical + read replicas/etc. |
| Transactions | Supported, but designed differently | Strong relational transactions |
| Best for | High-scale key-value/document workloads | Relational workloads |
| Query pattern | Known access patterns | Flexible queries |
| Server management | AWS managed | AWS managed |

### Interview answer

> Choose DynamoDB when the application has predictable access patterns and requires massive scalability and low latency. Choose RDS/Aurora when relationships, joins, SQL flexibility, and relational transactions are central to the workload.

---

# 44. SQS vs Kafka

This is a very common senior interview question.

| Feature | SQS | Kafka |
| -------------- | ----------------------------------------------- | ------------------------------------ |
| Type | Managed queue | Distributed event streaming platform |
| AWS management | Fully managed | Amazon MSK or self-managed options |
| Ordering | FIFO available | Partition ordering |
| Replay | Not designed primarily as a long-term event log | Strong event replay model |
| Consumer model | Queue-based | Consumer groups |
| Retention | Queue retention | Configurable log retention |
| Best for | Async jobs/decoupling | Event streaming |
| Complexity | Lower | Higher |

### Simple rule

text
Need background processing?
↓
SQS


text
Need event streaming/replay/analytics?
↓
Kafka


---

# 45. SQS vs SNS

Another frequent interview question.

### SQS

One queue receives messages for consumers.

text
Producer
|
v
SQS
|
v
Consumer


### SNS

Publish/subscribe.

text
SNS
/ | \
/ | \
v v v
SQS Email Lambda


### Easy memory trick

>*SQS = queue*

>*SNS = notification/pub-sub*

They can also be combined:

text
Producer
|
v
SNS
|
+---+---+
| |
v v
SQS SQS


---

# 46. CloudWatch vs CloudTrail

Frequently asked.

| CloudWatch | CloudTrail |
| ----------- | --------------------------------- |
| Monitoring | Auditing |
| Metrics | API activity |
| Logs | Who did what |
| Alarms | Governance/security investigation |
| Performance | Account activity |

### Memory trick

>*CloudWatch watches performance.*

>*CloudTrail tracks AWS activity.*

---

# 47. Production Best Practices

## DynamoDB

### 1. Design around access patterns

Don't start with:

text
What tables should I create?


Start with:

text
What queries does my application need?


For example:

text
Get customer
Get customer orders
Get order
Get pending orders


Then design keys/indexes accordingly.

### 2. Avoid Scan

Prefer:

text
Query


over:

text
Scan


when possible.

### 3. Choose partition keys carefully

Avoid hot partitions.

### 4. Use TTL where appropriate

Useful for temporary data:

text
Sessions
Tokens
Temporary records


### 5. Use conditional writes

Useful for concurrency and correctness.

Example:

text
Update inventory
ONLY IF quantity >= requestedQuantity


---

# 48. SQS Best Practices

### 1. Make consumers idempotent

Critical.

### 2. Configure visibility timeout correctly

It should accommodate normal processing time.

### 3. Use DLQ

Don't retry a permanently bad message forever.

### 4. Monitor queue depth

Use CloudWatch.

### 5. Batch messages where appropriate

Can improve throughput and efficiency.

### 6. Use FIFO only when required

Don't automatically choose FIFO when ordering isn't necessary.

---

# 49. CloudWatch Best Practices

Monitor:

text
Latency
Error rate
Throughput
CPU
Memory
Queue depth
Database throttling


Create meaningful alarms.

For example:

text
5xx > 5%
Queue depth > 10,000
DynamoDB throttling > threshold
Latency > 1 second


Don't create hundreds of meaningless alarms.

---
# 50. Senior-Level System Design Scenario

### Question

> Design an order-processing system for an e-commerce company that receives 100,000 orders per minute.

One possible architecture:

```text
 Users
 |
 v
 CloudFront
 |
 v
 WAF
 |
 v
 API Gateway
 |
 v
 Spring Boot APIs
 |
 +---------+---------+
 | |
 v v
 DynamoDB SQS
 | |
 | +------+------+
 | | |
 v v v
 Orders Payment Inventory
 | |
 +------+------+
 |
 v
 Notification
 |
 v
 CloudWatch
```

### Why?

**DynamoDB**

For:

```text
Orders
Customers
Cart
High-volume key-value access
```

**SQS**

For:

```text
Async processing
Traffic buffering
Decoupling
Retry
```

**CloudWatch**

For:

```text
Monitoring
Logs
Metrics
Alarms
```

---

# 51. What Happens During Traffic Spike?

Normal:

```text
1,000 orders/min
```

Suddenly:

```text
100,000 orders/min
```

Synchronous architecture:

```text
API
 |
 +--> Payment
 +--> Inventory
 +--> Notification

Everything overloaded
```

SQS architecture:

```text
API
 |
 v
SQS
 |
 +---- Message
 +---- Message
 +---- Message
 +---- Message
 +---- Message
```

The queue acts as a **buffer**.

Consumers can process messages at a controlled rate.

Then:

```text
CloudWatch
 |
 v
Queue depth increasing
 |
 v
Scale consumers
```

This is a key system-design concept:

>**Buffer + asynchronous processing + horizontal scaling**

---

# 52. FinTech Scenario

Suppose you're building a payment system.

```text
Customer
 |
 v
Payment API
 |
 v
Spring Boot
 |
 +----> DynamoDB
 |
 +----> SQS
 |
 v
 Payment Worker
 |
 +----+----+
 | |
 v v
 Payment Fraud
 Gateway Service
 |
 v
 Transaction
 |
 v
 DynamoDB
```

Critical requirements:

### Idempotency

```text
transactionId = TX10001
```

Prevent duplicate payment.

### Encryption

Use appropriate encryption mechanisms such as:

```text
KMS
```

### Secrets

Don't put:

```text
DB_PASSWORD
API_KEY
```

inside source code.

Use:

```text
Secrets Manager
```

### Monitoring

Monitor:

```text
Payment failures
Latency
Queue depth
Transaction errors
```

---

# 53. Advanced Interview Questions

## Q1. Why would you choose DynamoDB instead of MySQL?

**Answer:**

For workloads requiring very high scale, low predictable latency, and key-value/document access patterns. DynamoDB is especially useful when access patterns are known in advance and horizontal scaling is important.

---

## Q2. What causes a DynamoDB hot partition?

**Answer:**

When a disproportionate amount of traffic or data is directed to a particular partition key, causing uneven workload distribution.

---

## Q3. Query vs Scan?

**Answer:**

Query retrieves items based on a key condition and is generally efficient. Scan examines the table and is much more expensive for large datasets.

---

## Q4. What is a GSI?

**Answer:**

A Global Secondary Index provides an alternative access pattern using a different partition key and optionally sort key.

---

## Q5. Why use SQS?

**Answer:**

To decouple services, buffer traffic, support asynchronous processing, improve resilience, and allow producers and consumers to scale independently.

---

## Q6. What happens if an SQS consumer crashes?

**Answer:**

If it hasn't deleted the message, the message can become visible again after the visibility timeout, allowing another consumer to retry it.

---

## Q7. Why is idempotency important with SQS?

**Answer:**

Because messages can be delivered more than once under at-least-once delivery semantics. Consumers must safely handle duplicate messages.

---

## Q8. What is a DLQ?

**Answer:**

A Dead Letter Queue stores messages that repeatedly fail processing after the configured maximum receive/retry threshold.

---

## Q9. Standard SQS vs FIFO?

**Answer:**

Standard is designed for high throughput and at-least-once delivery with best-effort ordering. FIFO is used when ordering and deduplication requirements are important.

---

## Q10. What is CloudWatch?

**Answer:**

AWS's monitoring and observability service used for metrics, logs, alarms, dashboards, and operational visibility.

---

# 54. Advanced Scenario-Based Interview Questions

### Scenario 1

> Your DynamoDB application suddenly becomes slow. What do you investigate?

Answer:

```text
1. Check CloudWatch metrics
2. Check throttling
3. Check partition-key distribution
4. Check hot partitions
5. Check read/write capacity
6. Check query patterns
7. Check large scans
8. Check indexes
9. Check application latency
```

---

### Scenario 2

> Your SQS queue suddenly has 1 million messages. What do you do?

Answer:

```text
Check:
 ↓
CloudWatch
 ↓
Queue depth
 ↓
Consumer health
 ↓
Consumer processing latency
 ↓
Error rate
 ↓
DLQ
```

Then:

```text
Increase consumer capacity
 +
Fix bottleneck
 +
Check downstream dependencies
```

Don't simply keep adding consumers if the bottleneck is:

```text
Database
```

or:

```text
External payment gateway
```

---

### Scenario 3

> Payment was processed twice. What could be the reason?

Possible reason:

```text
SQS message
 |
 v
Payment processed
 |
 X
Consumer crashes
 |
 v
Message becomes visible
 |
 v
Message processed again
```

Solution:

```text
Idempotency Key
 +
Transaction State
 +
Conditional write
```

---

### Scenario 4

> Your DynamoDB table contains 500 million records and the application needs to find one customer's orders.

Bad:

```text
Scan entire table
```

Better:

```text
PK = CUSTOMER#101
SK = ORDER#...
```

Then:

```text
Query
```

---

### Scenario 5

> The application is returning HTTP 200, but customers aren't receiving emails.

Don't make email sending synchronous.

Use:

```text
Order Service
 |
 v
 SQS
 |
 v
Notification Service
 |
 v
 SES
```

Monitor:

```text
SQS queue
Consumer errors
Email service errors
CloudWatch logs
```

---

# 55. Most Important Senior-Level Concepts

If you are interviewing for **Senior Java Developer / Technical Lead**, focus especially on:

### DynamoDB

```text
Partition Key
Sort Key
GSI
LSI
Query vs Scan
Hot Partition
Capacity
Consistency
Conditional Writes
TTL
Transactions
Data Modeling
Access Patterns
```

### SQS

```text
Producer
Consumer
Visibility Timeout
At-least-once delivery
FIFO
Standard
DLQ
Retry
Idempotency
Long Polling
Batch Processing
Message retention
```

### CloudWatch

```text
Metrics
Logs
Alarms
Dashboards
Log Insights
Monitoring
Alerting
Application observability
```

---

# 56. Quick Revision Cheat Sheet

```text
================================================
 AWS QUICK REVISION
================================================

DYNAMODB
------------------------------------------------
Type → NoSQL
Purpose → Fast scalable data storage
Model → Key-Value / Document

Primary Key
 |
 +-- Partition Key
 |
 +-- Partition Key + Sort Key

Query → Efficient key-based retrieval
Scan → Reads across table, avoid when possible

GSI → Different access pattern
LSI → Same partition key, different sort key

Hot Part. → Uneven traffic distribution

Capacity
 |
 +-- Provisioned
 +-- On-Demand

Consistency
 |
 +-- Eventually Consistent
 +-- Strongly Consistent

================================================

SQS
------------------------------------------------
Purpose → Message queue

Producer
 |
 v
 SQS Queue
 |
 v
Consumer

Key concepts:
 |
 +-- Visibility Timeout
 +-- Retry
 +-- DLQ
 +-- Idempotency
 +-- Long Polling
 +-- FIFO
 +-- Standard

Main benefit:
 → Decoupling
 → Buffering
 → Async processing
 → Resilience

================================================

CLOUDWATCH
------------------------------------------------
Purpose → Monitoring / Observability

Monitors:
 |
 +-- Metrics
 +-- Logs
 +-- Alarms
 +-- Dashboards

Example:

SQS Queue Depth
 |
 v
 CloudWatch
 |
 v
 Alarm
 |
 v
 Notification / Action

================================================
```

# 57. The 3 Services in One Sentence

For interviews, remember this:

>**DynamoDB is the scalable data store, SQS is the asynchronous communication/buffering mechanism, and CloudWatch provides monitoring and operational visibility.**

And the architectural relationship is:

```text
 Spring Boot
 |
 +---------+---------+
 | |
 v v
 DynamoDB SQS
 | |
 Persistent Async work
 Data |
 v
 Consumers
 |
 v
 DynamoDB

 Everything is monitored by

 CloudWatch
 / | \
 Logs Metrics Alarms
```

### The most important interview mindset

Don't answer:

> "We use DynamoDB because it is AWS NoSQL."

Instead answer:

> "We selected DynamoDB because our access patterns are primarily key-based, we require low-latency reads/writes and horizontal scalability, and we don't require relational joins."

Don't answer:

> "We use SQS for communication."

Instead answer:

> "We use SQS to decouple services, absorb traffic spikes, process work asynchronously, and provide retry/DLQ capabilities. Because delivery can be at least once, our consumers are idempotent."

Don't answer:

> "We use CloudWatch for monitoring."

Instead answer:

> "We use CloudWatch to monitor application and infrastructure metrics, centralize logs, create operational dashboards, and trigger alarms for conditions such as elevated error rates, latency, DynamoDB throttling, or growing SQS backlog."

That is the difference between a **basic AWS answer** and a **Senior Developer/Architect-level answer**.

---

# Additional Production-Standard Guidance

The complete supplied guide above is preserved. The sections below extend it with implementation and operational details that are commonly expected in production design reviews and senior interviews.

# 58. DynamoDB Data Modeling Starts with Access Patterns

DynamoDB design is query-first rather than entity-first. Write down the operations the application must support before choosing keys:

```text
Get order by orderId
List orders for customer sorted by creation time
Find pending orders for a tenant
Read the current cart
Record an idempotency result
```

Then map each operation to a `GetItem`, `Query`, or an index-backed query. If an important operation requires scanning millions of items, the table design is usually incomplete.

A practical design worksheet is:

| Access pattern | Operation | Partition key | Sort key | Index | Expected volume |
| --- | --- | --- | --- | --- | --- |
| Get order | `GetItem` | `ORDER#<id>` | none | base table | high |
| Customer orders | `Query` | `CUSTOMER#<id>` | `ORDER#<timestamp>#<id>` | base table or GSI | high |
| Pending orders | `Query` | `STATUS#PENDING` | `createdAt` | GSI | moderate |

The correct schema is the one that makes required reads predictable, bounded and affordable.

# 59. Single-Table Design and Composite Keys

Single-table design stores multiple entity types in one table when they share access patterns. Prefixes make item types and relationships explicit:

```text
PK = CUSTOMER#C101
SK = PROFILE

PK = CUSTOMER#C101
SK = ORDER#2026-09-19T10:15:00Z#O1001

PK = ORDER#O1001
SK = PAYMENT
```

This can retrieve a customer and related records with one query, but it increases modeling complexity. It is not a rule that every DynamoDB application must use one table. Multiple tables can be clearer when ownership, lifecycle, security or operational boundaries differ.

A good senior-level explanation is:

> Single-table design is an optimization for known access patterns and fewer round trips. It is valuable when related entities are read together, but it should not be forced where separate lifecycle or ownership boundaries make multiple tables clearer.

# 60. Capacity Units and Item Size

Capacity is affected by item size, not only request count. As a simplified reference:

```text
1 WCU -> one write per second for an item up to 1 KB
1 RCU -> one strongly consistent read per second for an item up to 4 KB
 or two eventually consistent reads per second for an item up to 4 KB
```

Larger items consume proportionally more capacity. A query also consumes capacity based on the data read, even when a filter expression removes items from the returned result. A filter is not a substitute for a selective key condition.

Review:

- Item size and collection growth.
- Read consistency choice.
- Query page size and pagination.
- GSI write amplification.
- Hot keys and traffic distribution.
- On-demand versus provisioned capacity.

# 61. GSI and LSI Design Trade-offs

An index is another storage and access path, not a free query feature.

```text
Base table write
 |
 +--> base item
 +--> GSI item when indexed attributes exist
 `--> another index item if configured
```

Consequences include additional write capacity, storage, backfill work and operational cost. Project only the attributes each index needs where possible.

Important distinctions:

| Concern | LSI | GSI |
| --- | --- | --- |
| Partition key | Same as base table | Can be different |
| Sort key | Different | Optional/different |
| Created | With the table | Can be added later |
| Strongly consistent read | Supported | Not supported |
| Capacity | Shares table capacity | Has its own capacity model |

Choose an index because it serves a real access pattern. Do not create indexes simply because a field exists.

# 62. Conditional Writes and Optimistic Concurrency

Use conditional expressions to protect correctness under concurrent requests.

```java
UpdateItemRequest request = UpdateItemRequest.builder()
 .tableName("Inventory")
 .key(key)
 .updateExpression("SET quantity = quantity - :requested, version = version + :one")
 .conditionExpression("quantity >= :requested AND version = :expectedVersion")
 .expressionAttributeValues(values)
 .build();

dynamoDbClient.updateItem(request);
```

If the condition fails, treat it as a business conflict such as “insufficient inventory” or “stale version,” not automatically as a transient infrastructure error.

Common uses:

- Prevent duplicate idempotency claims.
- Reserve inventory only when sufficient quantity exists.
- Enforce a state transition such as `PENDING -> COMPLETED`.
- Prevent an older update from overwriting a newer version.
- Implement a lightweight distributed lock with an expiry and careful recovery logic.

# 63. DynamoDB Transactions, Streams and TTL

### Transactions

DynamoDB transactions provide atomicity across supported item operations, but they are not a general replacement for a relational transaction. They have request-size and operation limits and can cost more than individual writes. Use them for a bounded, well-defined consistency boundary.

### Streams

DynamoDB Streams capture item-level changes and can trigger Lambda. They are useful for projections, notifications, audit processing and search indexing.

```text
DynamoDB write
 |
 v
DynamoDB Stream
 |
 v
Lambda consumer
 |
 +--> projection
 +--> event
 `--> audit record
```

Stream consumers must handle duplicates, ordering scope, retries and poison records. A stream event is not a substitute for a durable business event contract without additional design.

### TTL

TTL is useful for sessions, temporary tokens and retention-oriented data. Expiration is best effort rather than a precise deletion deadline. Do not use TTL when a business workflow requires deletion at an exact second.

# 64. DynamoDB Availability, Recovery and Security

Production tables should be reviewed for:

- Point-in-time recovery.
- On-demand backups and restore testing.
- Cross-region strategy and global table requirements.
- Encryption at rest and customer-managed KMS keys where required.
- IAM actions limited to required tables and indexes.
- VPC endpoint or private access requirements.
- CloudTrail data-event logging where audit needs justify the volume and cost.
- Retention and deletion requirements for personal data.

Backups are not a recovery strategy until restore time, data loss objectives and validation procedures have been tested.

# 65. SQS Delivery Semantics: Exactly-Once Business Processing is Your Responsibility

Standard SQS provides at-least-once delivery. A message may be delivered more than once, and messages can arrive out of order. FIFO provides ordering within a message group and deduplication features, but the business operation still needs idempotency.

```text
Message delivered
 |
 v
Business side effect succeeds
 |
 X
Consumer crashes before delete
 |
 v
Message delivered again
```

The safe design is not “SQS gives exactly once.” It is:

```text
At-least-once delivery
 +
Durable idempotency
 +
Conditional state transition
 =
Safe repeated delivery
```

# 66. SQS Visibility Timeout and Lease Renewal

Visibility timeout behaves like a processing lease. Set it longer than normal processing time, but do not use an extremely large value that delays recovery after a crashed consumer.

```text
Receive message
 |
 v
Visibility lease starts
 |
 +--> process successfully -> delete
 |
 `--> processing takes longer -> extend visibility
```

Long-running consumers can extend visibility while work is active. A timeout does not cancel the handler; it only allows another receive after the message becomes visible. The consumer must therefore avoid overlapping side effects or protect them with idempotency.

# 67. Long Polling, Batching and Queue Throughput

Long polling reduces empty receives and unnecessary API calls. A consumer should wait for messages rather than continuously making short empty polls.

Batching can improve throughput and cost, but it changes failure handling:

```text
Batch: M1, M2, M3, M4
```

The consumer must decide whether to retry the complete batch or acknowledge successful messages individually. With Lambda event source mappings, configure partial batch response when independent messages can be completed separately.

Tune together:

- Batch size.
- Maximum batching window.
- Visibility timeout.
- Consumer concurrency.
- Downstream rate limits.
- Message processing time.

Throughput is not improved if a larger batch causes timeouts, lock contention or excessive retry work.

# 68. FIFO Message Groups and Deduplication

FIFO ordering is scoped by `MessageGroupId`. Messages in different groups can be processed concurrently, while messages in the same group preserve order according to the queue semantics.

```text
Group A: A1 -> A2 -> A3
Group B: B1 -> B2 -> B3

Group A and Group B can progress independently.
```

Choose a group key that preserves the required business order without serializing the whole queue. For example, `accountId` may preserve account-level payment order while allowing different accounts to proceed in parallel.

Deduplication IDs reduce duplicate sends within the supported FIFO deduplication window, but consumers still need durable idempotency for retries, replay and business correctness.

# 69. DLQ and Poison-Message Operations

A DLQ is a diagnostic and recovery boundary, not a place to hide failures. Define:

```text
Maximum receives
DLQ retention
Alert threshold
Ownership
Replay procedure
Permanent-failure classification
```

Separate transient failures from permanent failures:

```text
Invalid schema or missing required field -> quarantine and investigate
Temporary dependency timeout -> retry with backoff
Business conflict -> record outcome and acknowledge safely
```

Replaying a DLQ blindly can recreate an outage. Correct the underlying issue, test a sample, throttle the replay and monitor downstream capacity.

# 70. Lambda and SQS Integration

With Lambda as an SQS consumer, AWS manages polling and invokes the function with batches. The event source mapping can scale the number of concurrent function invocations, so configure it with downstream capacity in mind.

```text
SQS
 -> event source mapping
 -> Lambda batch handler
 -> success: delete successful messages
 -> failure: retry failed messages
 -> repeated failure: DLQ
```

Use partial batch failure for independent messages. Set the queue visibility timeout to accommodate the Lambda timeout and retry behaviour. Track:

- Approximate age of the oldest message.
- Visible and in-flight messages.
- Lambda errors and throttles.
- DLQ message count.
- Downstream latency and throttles.

# 71. CloudWatch Metrics are Signals, Not the Whole Observability Story

Metrics answer “how much” and “how often.” Logs explain an individual failure. Traces show the path across services. A production observability design combines all three:

```text
Metrics -> detect a problem
Logs -> explain a request or message
Traces -> locate cross-service latency
Events -> record operational changes
```

Use stable dimensions such as service, environment, operation and outcome. Avoid high-cardinality dimensions such as an unbounded request ID in every custom metric; put those values in structured logs or traces instead.

# 72. Structured Spring Boot Logging

Prefer structured logs over concatenated text so CloudWatch Logs Insights can query fields consistently.

```json
{
"timestamp": "2026-09-19T10:15:00Z",
"level": "INFO",
"service": "order-api",
"operation": "createOrder",
"orderId": "O1001",
"correlationId": "req-abc-123",
"durationMs": 42,
"outcome": "success"
}
```

Never log access tokens, passwords, full payment data or unrestricted personal data. Redaction must happen before the log event is emitted, not only in a dashboard.

# 73. CloudWatch Logs Insights Example

For a structured log stream, a useful investigation query can be:

```text
fields @timestamp, service, operation, level, durationMs, errorCode, correlationId
| filter level = "ERROR"
| stats count() as failures,
 avg(durationMs) as averageDuration,
 max(durationMs) as maximumDuration
 by operation, errorCode
| sort failures desc
```

Use queries as operational tools and save the important ones with the service runbook. Log retention should reflect incident investigation, privacy and cost requirements.

# 74. Metrics, Alarms and SLOs

Alarm on user and business impact, not only infrastructure symptoms.

```text
Availability SLI: successful requests / total requests
Latency SLI: requests under agreed threshold / total requests
Queue freshness SLI: messages processed within target age
Payment SLI: completed payments / valid payment attempts
```

An alarm should define:

- Metric and dimensions.
- Threshold and evaluation period.
- Missing-data treatment.
- Severity and notification route.
- Runbook link.
- Owner.
- Whether it is a symptom or a cause.

Composite alarms can reduce notification noise by combining related signals. For example, page on high API errors only when the deployment is active or the dependency is healthy, depending on the incident model.

# 75. Custom Metrics and Embedded Metric Format

AWS service metrics are useful, but business metrics often need to be emitted by the application:

```text
OrdersCreated
PaymentsAuthorized
MessagesDeadLettered
InventoryReservationConflicts
```

Embedded Metric Format lets structured log events produce metrics without a separate synchronous metric API call. Use it carefully: define a small, stable set of dimensions and control cardinality.

Do not treat a metric as correct merely because it appears on a dashboard. Validate its unit, aggregation, timestamp, dimensions and behaviour during retries.

# 76. Distributed Tracing and Correlation

Tracing across a synchronous request may look like:

```text
API Gateway
 -> Spring Boot
 -> DynamoDB
 -> SQS send
```

An asynchronous consumer starts a new processing segment:

```text
SQS message
 -> consumer trace/span
 -> DynamoDB
 -> external provider
```

Propagate a correlation ID and, where supported, trace context in message attributes or the event envelope. Do not rely on logs alone to reconstruct a workflow that crosses queues and retries.

# 77. Spring Boot AWS Client Practices

Create AWS SDK clients once per application context and reuse them. Do not construct a new client for every request.

```java
@Configuration
class AwsClientConfiguration {

 @Bean
 DynamoDbClient dynamoDbClient() {
 return DynamoDbClient.builder()
 .region(Region.AP_SOUTH_1)
 .build();
 }

 @Bean
 SqsClient sqsClient() {
 return SqsClient.builder()
 .region(Region.AP_SOUTH_1)
 .build();
 }
}
```

Production configuration should also consider connection pools, HTTP timeouts, retry modes, backoff, endpoint overrides for tests and graceful shutdown. SDK retries do not replace business-level idempotency.

# 78. A Safer SQS Consumer Boundary

A consumer should validate and classify a message before performing side effects:

```text
Receive
 -> parse envelope
 -> validate schema and required fields
 -> check idempotency/state
 -> perform bounded business operation
 -> persist outcome
 -> acknowledge/delete
```

Never acknowledge a message merely because deserialization succeeded. Acknowledgement means the system has decided what to do with the message, including a deliberate quarantine path for permanent invalid data.

# 79. End-to-End Order Flow with Correctness Boundaries

```text
POST /orders
 |
 v
Spring Boot validates request
 |
 v
DynamoDB transaction or conditional write
 | order = ACCEPTED
 | outbox = ORDER_ACCEPTED
 v
Outbox publisher
 |
 v
SQS
 |
 v
Payment consumer
 |
 +--> idempotency claim
 +--> payment provider
 +--> conditional state transition
 `--> acknowledgement
 |
 v
CloudWatch metrics, logs, traces and alarms
```

The key boundaries are explicit:

- DynamoDB protects order state.
- The outbox protects database-to-message publication.
- SQS protects asynchronous delivery and buffering.
- Idempotency protects repeated delivery.
- Conditional writes protect concurrent state changes.
- CloudWatch makes failure visible.

# 80. Security Architecture

Use least privilege at each boundary:

```text
Spring Boot role
 -> only required DynamoDB table/index actions
 -> only required SQS send actions

Worker role
 -> only required SQS receive/delete actions
 -> only required DynamoDB update actions

Operations role
 -> dashboard, logs and alarm access
 -> no application data mutation unless required
```

Review:

- IAM policies and resource conditions.
- KMS key policies and encryption context requirements.
- SQS queue policies and confused-deputy protections.
- DynamoDB encryption and backup access.
- TLS and private networking.
- Secrets Manager or Parameter Store usage.
- CloudTrail audit events.
- Data classification and log redaction.

An application should not use broad permissions simply because the AWS SDK call currently works with them.

# 81. Failure-Mode Matrix

| Failure | Expected behaviour | Operator signal | Recovery |
| --- | --- | --- | --- |
| DynamoDB throttling | Backoff or controlled failure | Throttled requests, latency | Fix key distribution/capacity and rate |
| SQS consumer crash | Message becomes visible again | Age/in-flight messages | Retry, then DLQ if persistent |
| Permanent invalid message | Do not retry forever | DLQ count | Correct/quarantine/replay selectively |
| External provider timeout | Bounded retry and state tracking | Error rate/latency | Provider recovery or manual reconciliation |
| CloudWatch alarm failure | Observability gap | Missing alarm signal | Test alarm path and ownership |
| Deployment regression | Traffic rollback | Error/latency/business metrics | Repoint alias or revert deployment |

Every row should have an owner and a tested runbook.

# 82. Testing Strategy

### Unit tests

Test key construction, conditional expressions, message classification, retry classification and idempotency decisions without AWS.

### Integration tests

Use isolated AWS resources or a compatible local test environment for:

- DynamoDB key and index queries.
- Conditional write conflicts.
- SQS send, receive, visibility and delete behaviour.
- DLQ routing.
- IAM denial cases.
- CloudWatch metric and log emission where practical.

### Contract tests

Validate event envelope versions, required fields, compatibility and producer/consumer ownership.

### Resilience tests

Inject:

- DynamoDB throttling.
- Duplicate messages.
- Consumer crash after a side effect.
- Visibility timeout expiry.
- External API timeout.
- DLQ replay.
- CloudWatch alarm notification failure.

The most valuable test for an SQS payment consumer is not only “message returns 200.” It is “the same message delivered twice produces one business effect.”

# 83. Infrastructure as Code and Environment Promotion

Define tables, indexes, queues, DLQs, alarms, IAM policies and dashboards with AWS CDK, SAM, CloudFormation or Terraform. Avoid manually creating production resources that are not represented in source control.

Promotion should separate:

```text
development -> test -> staging -> production
```

Use environment-specific values for names, capacity and alarm routing while keeping the structural definition consistent. Protect stateful resources from accidental replacement and review index or key changes as migrations.

# 84. Operational Runbook for a Growing SQS Backlog

1. Confirm the backlog and oldest-message age in CloudWatch.
2. Check consumer errors, throttles, duration and concurrency.
3. Check whether the DLQ is receiving messages.
4. Identify whether the bottleneck is Lambda, the database or an external provider.
5. Check DynamoDB throttling and hot keys.
6. Check provider rate limits and network errors.
7. Increase consumer capacity only within downstream limits.
8. Pause or reduce intake if the dependency is at risk.
9. Fix the cause and test with a small replay.
10. Replay DLQ or backlog gradually while monitoring business correctness.

The wrong first response is to increase concurrency without checking the dependency that is already failing.

# 85. Operational Runbook for DynamoDB Throttling

```text
CloudWatch alarm
 |
 v
Identify table and operation
 |
 v
Check partition-key distribution and item size
 |
 v
Check capacity mode and recent traffic change
 |
 v
Check indexes and write amplification
 |
 v
Apply capacity/key/query correction
 |
 v
Verify latency, throttling and cost
```

Do not hide throttling by adding retries without a limit. Unbounded retries can increase latency, duplicate work and downstream pressure.

# 86. Current-Service Comparison

| Need | Best starting point | Why |
| --- | --- | --- |
| Key-value/document access at massive scale | DynamoDB | Managed low-latency distributed storage |
| Durable asynchronous work | SQS | Buffering, decoupling and retry boundary |
| Metrics, logs and alarms | CloudWatch | AWS-native operational visibility |
| Long-retention event replay | Kinesis, MSK or an event log | Stream retention and consumer replay model |
| Relational joins and reporting | Aurora/RDS or analytics store | SQL and relational access patterns |
| Complex workflow orchestration | Step Functions | Explicit state, retries and branching |

Services can be combined, but each should have a clear responsibility.

# 87. Senior Interview Answer: Design an Order Platform

> I would begin with access patterns and consistency requirements rather than choosing a database by habit. DynamoDB is a good fit for order state when reads are key-based and the scale and latency requirements justify it; I would use conditional writes or a bounded transaction for state transitions and idempotency. I would publish work through an outbox to SQS so the API is decoupled from payment, inventory and notification processing. Consumers would assume at-least-once delivery, use durable idempotency and acknowledge only after the business outcome is safely recorded. Visibility timeout, batch size, concurrency and DLQ policy would be tuned against downstream capacity. CloudWatch would provide structured logs, queue-age and throttling metrics, business metrics, dashboards and actionable alarms, with traces and correlation IDs across asynchronous boundaries. Backups, IAM, KMS, secrets, replay, deployment rollback and recovery objectives would be part of the design rather than afterthoughts.

# 88. Final Production Checklist

### DynamoDB

- Access patterns are documented before schema and index creation.
- Queries use keys and do not depend on large scans.
- Partition-key cardinality and traffic distribution are understood.
- Item size, capacity units and GSI write amplification are measured.
- Conditional writes protect state transitions and idempotency.
- TTL is treated as best-effort expiry.
- Streams, transactions and global replication have explicit use cases.
- Point-in-time recovery, backup and restore have been tested.
- Encryption, IAM and data retention are documented.

### SQS

- Standard versus FIFO is selected from business requirements.
- Consumers are idempotent and tolerate duplicates.
- Visibility timeout covers normal processing and has a renewal strategy when needed.
- Long polling and batching are tuned with downstream capacity.
- DLQ, retention, alerting and replay ownership are documented.
- Poison messages are separated from transient failures.
- Lambda event source maximum concurrency protects dependencies.
- Queue age is monitored, not only queue depth.

### CloudWatch

- Structured logs contain correlation and business identifiers without secrets.
- Metrics have correct units, dimensions and cardinality.
- Alarms are actionable and connected to runbooks.
- SLO indicators cover availability, latency and queue freshness.
- Dashboards show application, queue, database and dependency health.
- Traces or correlation IDs cross asynchronous boundaries.
- Log retention and access follow privacy and cost requirements.
- Alarm and notification paths are tested.

The architect-level summary is:

> DynamoDB provides scalable state when the data model follows access patterns. SQS provides an at-least-once delivery and buffering boundary, so consumers must be idempotent and downstream-aware. CloudWatch turns the design into an operable system by connecting metrics, structured logs, alarms, traces and runbooks to measurable business outcomes.