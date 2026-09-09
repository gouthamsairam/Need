# AWS Lambda — Senior Architect + Interview Preparation Guide

![Image](https://images.openai.com/static-rsc-4/2u-ByU6DpB9txLNsj_uJael_GTWueIg_4vKEjBIrTUnawNhlL3aA3b6TDCO_DbHJyqu-7JrBQSdHRn9fd7KoO2EzNCiEs1wTN2cwr6LsYtrTxpJfQZocYcDqDfvRssi6uqYxOkJJAUJ1TxTU75Cjkx33hZ9P8-pUdKViC-W0V9JD1wU1MgsEWZp7S98ujWs3?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/bnymmOg8x1WwCXkx2Ejs_LQ0oZIHmTi4F4UP-ghZsD53vKWH0oQpMjDSgotJU9LRvjWkh8COlUd_Yu7Uep27KFoMVxj7bnIc1RKrJOoz-Q5VfEE_lmBfKE3D9q8fmLDZ-fIY-iXijVADu3eJ7Cm9-WiLpYi-3hDN1ZCvi3Fe7dm42fkZgQ0MxGtTop-cYa5F?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/kMlAkf1TfZxrBx54dkvSbbJ4p3SK8HH00zZmIjJnF1hmal-iFNrRaP1QNvaWDb50s39xkr1k2Jr4G6vZks83xI8QCkjONlEybrR-EC6EyFJRfXr3V9AsLMXceNJfotK1D0kDOmf_MYnrki7WN0PaL0viyroNFcfcPuKzoMwPSIqD_nBefNWB3h9pQybwgnA2?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/fV4KpliGGVUN6ofiS9jISMy0LdoBdozvNUrkthDSur5HXhH11O7JTBTfOfcePvj2AUT0ZU0e7xdzU2NsIbY6vyZB1Bos6I-V11rIB-d4NoD7yyCatMDkf5h1TnVDCm3zafj4JgWcVtvHLahAllmfYNTEKqrIlKOrxpV56fpLh89wNWG4bvppUmGvw0FyjXkN?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/QfFtKyDWxuCtnYki2YWYNLPDJJ0wW7-8faLVezRZaxJW-a5vlJZWsxQjjrO54asTW5jZ_eSwN9Oha7vXwRvCWz_6MymKpbLOt5iz8zp4-Ej1zzypAlwynb0ioFvHa-6ZTFPNW5HyiqWk_L9kcCKMWIt7XLqDfBOrDMvGANKSOSZCowy48dIzqBoioxjrNwUF?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/nc_VDNRXXgGxzUl-HKcgJDzYC9UReV3FBOq4u7y8LxINQT0ixhvdufAfJdGY7CII2oUsIZMAS08D47yHw1NTPP8MvuFmMwHwBVMoaPAUCapNsuYiJQUP8fxw7_h-cniKVNFE3tqH-gfLOjkCB82VWpjhNNBIQBqpPM3W8jUwFz1qXy-bJayCEQaURInWCUr4?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/-5vNDKu3xp4F9VKohv7hLhHonWfyZDiOOtHk5VQK9nQiy3DW3dOWrlImFKfuWfJ1-KZl6T6Jmv6-g35hGFQIW9XTeYaZbZCo6fi_uksazG_vdFZJ42wong0q6DOHKfdsN0jgWpNHGtzeWloNw9G7Fwnh4sLQ6D20YXJkO65Erl26OBe2Kjt63nMF91ZJLeIA?purpose=fullsize)

If you are a **Senior Java/Spring Boot Developer**, AWS Lambda is important because interviewers usually don't ask only *"What is Lambda?"*. They ask:

> **"Why did you use Lambda instead of a Spring Boot service?"**
> **"How does Lambda scale?"**
> **"What happens during a cold start?"**
> **"How would you design a production application using Lambda?"**

Let's build the topic from the basics to senior-level architecture.

---

# 1. What is AWS Lambda?

**AWS Lambda is a serverless compute service from AWS that runs your code without you managing servers.**

You provide:

```text
Code
 +
Configuration
 +
Trigger
```

AWS takes care of:

```text
Servers
Operating system
Infrastructure
Scaling
Availability
```

For example:

```text
Client
   |
   v
API Gateway
   |
   v
AWS Lambda
   |
   v
Database
```

You write the business logic.

AWS manages the infrastructure required to execute it.

---

# 2. Why was Lambda introduced?

Traditionally, if you wanted to run an application, you might deploy it on:

```text
EC2
 |
 +-- Operating System
 +-- Java
 +-- Application Server
 +-- Spring Boot Application
 +-- Monitoring
 +-- Scaling
```

You are responsible for much of the infrastructure.

Suppose your application gets:

```text
9 AM  → 100 requests
12 PM → 10,000 requests
3 AM  → 10 requests
```

With traditional infrastructure, you need to think about capacity planning.

Lambda changes the model:

```text
Request
   |
   v
Lambda
   |
   +---- Execution 1
   +---- Execution 2
   +---- Execution 3
   +---- ...
```

AWS automatically creates execution environments as needed.

---

# 3. Real-world analogy

Imagine a restaurant.

### Traditional server

You permanently employ:

```text
10 chefs
10 waiters
```

Even when there are no customers, you pay for them.

### Lambda

Instead:

> "Whenever a customer arrives, AWS provides the required kitchen capacity."

No customers:

```text
0 active executions
```

Many customers:

```text
Many parallel executions
```

This is the fundamental idea behind serverless computing.

---

# 4. What problem does Lambda solve?

Lambda is particularly useful for:

### 1. Event-driven processing

```text
File uploaded
     ↓
Lambda
     ↓
Process file
```

### 2. APIs

```text
Client
 ↓
API Gateway
 ↓
Lambda
 ↓
Database
```

### 3. Scheduled jobs

```text
EventBridge
     ↓
Lambda
     ↓
Generate report
```

### 4. Asynchronous processing

```text
Application
     ↓
SQS
     ↓
Lambda
     ↓
Process message
```

### 5. Stream processing

```text
Kinesis / DynamoDB Streams
              ↓
           Lambda
              ↓
         Process event
```

---

# 5. Lambda Core Architecture

A typical enterprise architecture might look like:

```text
                         Internet
                            |
                            v
                       API Gateway
                            |
                            v
                    +---------------+
                    |    Lambda     |
                    +---------------+
                      /      |      \
                     /       |       \
                    v        v        v
               DynamoDB     S3       SQS
                             |
                             v
                          Lambda
                             |
                             v
                         Database
```

Additional services commonly used:

```text
CloudWatch
IAM
EventBridge
SNS
SQS
Step Functions
Secrets Manager
API Gateway
VPC
DynamoDB
S3
```

---

# 6. What is a Lambda Function?

A Lambda function is the unit of code that AWS executes.

For example:

```java
public class HelloLambda {

    public String handleRequest(String input) {
        return "Hello " + input;
    }
}
```

The method is invoked when an event arrives.

The exact handler signature depends on your runtime and event model.

---

# 7. Lambda Handler

For Java, a common handler is:

```java
public class OrderHandler
        implements RequestHandler<OrderRequest, OrderResponse> {

    @Override
    public OrderResponse handleRequest(
            OrderRequest request,
            Context context) {

        System.out.println(
            "Processing order: " + request.getOrderId()
        );

        return new OrderResponse(
            request.getOrderId(),
            "SUCCESS"
        );
    }
}
```

Conceptually:

```text
AWS Event
    |
    v
Lambda Runtime
    |
    v
Handler
    |
    v
Business Logic
    |
    v
Response
```

---

# 8. Lambda Lifecycle

This is extremely important for interviews.

A simplified Lambda execution lifecycle is:

```text
             Lambda Invocation
                    |
                    v
          +-------------------+
          | Create Environment|
          +-------------------+
                    |
                    v
              INIT phase
                    |
                    v
          Runtime initialization
                    |
                    v
              Handler
                    |
                    v
             Invocation
                    |
                    v
          Environment retained?
              /           \
            YES            NO
             |              |
             v              v
        Reuse later       Destroy
```

The important concept is:

> Lambda execution environments may be reused, but you must never depend on reuse.

---

# 9. Cold Start

Suppose Lambda has no ready execution environment.

AWS must create one.

That is called a:

**Cold Start**

Conceptually:

```text
Request
  |
  v
No existing environment
  |
  v
Create environment
  |
  v
Start runtime
  |
  v
Load application
  |
  v
Execute handler
```

This adds latency.

---

# 10. Warm Start

If AWS can reuse an existing environment:

```text
Request
   |
   v
Existing environment
   |
   v
Handler
   |
   v
Response
```

This generally avoids the initialization work of a cold start.

---

# 11. Important Senior Interview Question

### Does Lambda always create a new container for every request?

**No.**

AWS can reuse an execution environment for subsequent invocations.

But you should write your application so that it works correctly whether the environment is reused or recreated.

---

# 12. Lambda Execution Environment

Think of it as:

```text
+--------------------------------+
| Lambda Execution Environment   |
|                                |
| Java Runtime                   |
|                                |
| Your Application               |
|                                |
| Handler                        |
|                                |
| /tmp filesystem                |
+--------------------------------+
```

Important:

* `/tmp` can be used for temporary files.
* Data in memory may survive environment reuse.
* Environment reuse is not guaranteed.
* Do not store critical application state only in memory.

---

# 13. Static Variables and Lambda

Suppose:

```java
public class Handler {

    private static int counter = 0;

    public String handleRequest(...) {

        counter++;

        return String.valueOf(counter);
    }
}
```

You might observe:

```text
1
2
3
```

if the same execution environment is reused.

But after a new environment is created:

```text
1
```

again.

Therefore:

> Never use Lambda memory/static variables as a reliable distributed state store.

Use:

```text
DynamoDB
RDS
S3
ElastiCache
```

depending on the requirement.

---

# 14. Lambda Invocation Models

There are three important invocation patterns.

## Synchronous

Caller waits for response.

```text
Client
  |
  v
Lambda
  |
  v
Response
```

Examples:

* API Gateway
* Application Load Balancer
* Direct Lambda invocation

---

# 15. Asynchronous Invocation

Caller doesn't wait for Lambda's final result.

```text
Producer
   |
   v
Lambda
   |
   v
Processing
```

AWS handles asynchronous delivery mechanics and retries.

Typical use cases:

```text
S3 event
SNS
EventBridge
```

---

# 16. Event Source Mapping

This is important because people sometimes confuse it with asynchronous invocation.

For services such as:

```text
SQS
Kinesis
DynamoDB Streams
```

Lambda polls the event source through an **event source mapping** and invokes your function with batches of records.

For example:

```text
SQS
 |
 | messages
 v
Lambda event source mapping
 |
 v
Lambda
 |
 v
Business logic
```

---

# 17. Lambda + API Gateway

A very common architecture:

```text
                    Internet
                       |
                       v
                +-------------+
                | API Gateway |
                +-------------+
                       |
                       v
                +-------------+
                |   Lambda    |
                +-------------+
                       |
              +--------+--------+
              |                 |
              v                 v
          DynamoDB            SQS
```

Example:

```text
POST /orders
```

API Gateway receives:

```json
{
  "customerId": "C100",
  "amount": 5000
}
```

Lambda:

```java
public OrderResponse handleRequest(
        OrderRequest request,
        Context context) {

    // validate

    // business logic

    // persist

    return new OrderResponse("SUCCESS");
}
```

---

# 18. Lambda + SQS

This is extremely common in enterprise systems.

Suppose an order service receives 100,000 orders.

Instead of processing synchronously:

```text
Client
 ↓
Application
 ↓
Heavy processing
```

Use:

```text
Client
 ↓
API
 ↓
SQS
 ↓
Lambda
 ↓
Processing
```

Benefits:

* decoupling
* buffering
* retry
* asynchronous processing
* scaling

---

# 19. Production Example — Order Processing

Imagine an e-commerce application.

```text
                 Customer
                    |
                    v
               API Gateway
                    |
                    v
              Order Lambda
                    |
          +---------+---------+
          |                   |
          v                   v
      DynamoDB              SQS
                              |
                              v
                       Payment Lambda
                              |
                              v
                       Notification SQS
                              |
                              v
                    Notification Lambda
                              |
                              v
                           Email/SMS
```

This is an event-driven architecture.

---

# 20. Why use SQS between Lambdas?

Without SQS:

```text
Lambda A
   |
   v
Lambda B
   |
   v
Lambda C
```

Failure in Lambda B can directly affect the request chain.

With SQS:

```text
Lambda A
   |
   v
   SQS
   |
   v
Lambda B
```

Now the queue provides buffering and decoupling.

---

# 21. Lambda Retry Behavior

Retry behavior depends on the invocation model and event source.

For asynchronous invocation, AWS can retry failed invocations.

For SQS/Kinesis/DynamoDB Streams, the event source semantics are different: the event source retains records/messages according to its own configuration and Lambda retries processing according to that model.

Therefore, don't give a generic answer like:

> "Lambda always retries three times."

That is an incomplete answer.

---

# 22. Dead Letter Queue

For asynchronous processing, you can configure failure handling such as:

```text
Lambda
  |
  | failure
  v
Destination / DLQ
```

For SQS-based architectures, a common pattern is:

```text
Main Queue
    |
    v
Lambda
    |
 failure
    |
    v
Redrive / DLQ
```

This allows failed messages to be investigated or replayed.

---

# 23. Idempotency

This is **one of the most important Lambda production concepts**.

Suppose:

```text
Payment message
     |
     v
Lambda
```

Lambda processes:

```text
Payment ID = P100
```

Then the same event arrives again.

If your code charges the customer again:

```text
₹1000
₹1000
```

you have a serious production problem.

So make processing **idempotent**.

For example:

```text
Payment ID
    |
    v
Check processed payments
    |
    +---- Already processed → Ignore
    |
    +---- New → Process
```

DynamoDB conditional writes are one possible implementation approach.

---

# 24. Lambda Scaling

Lambda automatically scales by creating additional execution environments as concurrency increases.

Conceptually:

```text
10 requests

        Lambda
       / | | \
      /  | |  \
     E1 E2 E3 E4
```

1000 concurrent requests:

```text
E1
E2
E3
...
E1000
```

subject to account, Region, function concurrency, event source, and downstream capacity limits.

---

# 25. Concurrency

Suppose your Lambda receives:

```text
100 simultaneous requests
```

Lambda may execute multiple environments concurrently.

This is different from:

```text
One Lambda instance
handling everything sequentially
```

Think:

```text
Request 1 → Environment 1
Request 2 → Environment 2
Request 3 → Environment 3
Request 4 → Environment 4
...
```

---

# 26. Reserved Concurrency

Suppose you don't want your Lambda to consume unlimited concurrency.

You can configure:

```text
Reserved Concurrency = 50
```

Conceptually:

```text
Requests
   |
   v
Lambda
   |
   +---- maximum concurrency ≈ 50
```

It also reserves that concurrency for the function.

This can protect downstream systems.

---

# 27. Provisioned Concurrency

Cold starts can be a problem for latency-sensitive applications.

Provisioned concurrency keeps a configured number of execution environments initialized and ready.

Conceptually:

```text
Provisioned environments

E1 READY
E2 READY
E3 READY
E4 READY

Request
   |
   v
READY environment
```

This can significantly reduce cold-start latency.

---

# 28. Reserved vs Provisioned Concurrency

| Feature                           | Reserved  | Provisioned                        |
| --------------------------------- | --------- | ---------------------------------- |
| Limits/reserves concurrency       | Yes       | No, that's not its primary purpose |
| Keeps environments initialized    | No        | Yes                                |
| Helps prevent overload            | Yes       | Indirectly                         |
| Helps cold starts                 | No        | Yes                                |
| Useful for latency-sensitive APIs | Sometimes | Yes                                |

---

# 29. Lambda Memory

Lambda's configured memory affects more than just RAM.

Increasing memory also gives the function access to more CPU resources.

For example:

```text
512 MB
1 GB
2 GB
...
```

Higher memory can sometimes reduce execution time enough that the **total cost is similar or even lower**.

Therefore:

> Don't automatically choose the lowest memory setting.

Benchmark your workload.

---

# 30. Lambda Timeout

Every Lambda has a configured maximum execution duration.

You should not design a Lambda function that performs extremely long-running work if the workload can be decomposed.

For long workflows, consider:

```text
Step Functions
SQS
ECS
AWS Batch
```

depending on the workload.

---

# 31. Lambda Layers

A Lambda Layer allows you to package shared dependencies separately.

Example:

```text
                Layer
          +---------------+
          | Common libs   |
          | Utilities     |
          +---------------+
             /       \
            /         \
           v           v
      Lambda A      Lambda B
```

Useful for:

* shared libraries
* common code
* runtime dependencies
* organization-wide utilities

But don't create excessive layers just because they are available.

---

# 32. Environment Variables

You can configure:

```text
DB_HOST
API_URL
ENVIRONMENT
FEATURE_FLAG
```

Example:

```java
String environment =
    System.getenv("ENVIRONMENT");
```

But don't put sensitive credentials directly into environment variables when a better secret-management mechanism is appropriate.

Use:

```text
AWS Secrets Manager
AWS Systems Manager Parameter Store
```

for appropriate secrets/configuration.

---

# 33. IAM Role

Lambda should use an IAM execution role.

For example:

```text
Lambda
  |
  | IAM Role
  |
  +---- DynamoDB access
  +---- S3 access
  +---- SQS access
```

Follow **least privilege**.

Don't give:

```text
AdministratorAccess
```

just to make the Lambda work.

---

# 34. Lambda + S3

Very common use case:

```text
User
 |
 v
S3
 |
 | Object Created
 v
Lambda
 |
 v
Process file
 |
 +---- Resize image
 +---- Extract metadata
 +---- Validate file
 +---- Store result
```

Example:

```text
invoice.pdf
     |
     v
S3
     |
     v
Lambda
     |
     v
Extract invoice data
```

---

# 35. Lambda + DynamoDB

Example:

```text
API Gateway
     |
     v
Lambda
     |
     v
DynamoDB
```

Java code might use the AWS SDK:

```java
DynamoDbClient client =
    DynamoDbClient.create();
```

Then perform operations against DynamoDB.

For production, reuse clients rather than constructing them repeatedly inside every invocation.

---

# 36. Why reuse SDK clients?

Bad:

```java
public Response handleRequest(...) {

    DynamoDbClient client =
        DynamoDbClient.create();

    // operation
}
```

Better:

```java
private static final DynamoDbClient client =
        DynamoDbClient.create();

public Response handleRequest(...) {

    // use client
}
```

If the execution environment is reused, the client can also be reused.

This can reduce unnecessary initialization and connection setup.

However, your design must remain correct when AWS creates a new environment.

---

# 37. Lambda + Java/Spring Boot

As a Java developer, you may ask:

> Can I deploy Spring Boot to Lambda?

Yes.

There are several approaches, including using AWS Lambda Java runtime and frameworks/libraries designed for serverless Java applications.

For example:

```text
Spring Boot application
        |
        v
AWS Lambda adapter/framework
        |
        v
Lambda runtime
```

But don't blindly put a large traditional Spring Boot application into Lambda.

You should consider:

* startup time
* dependency size
* cold starts
* memory
* initialization
* connection management

---

# 38. Lambda vs Spring Boot on EC2/ECS

| Feature                        | Lambda                    | ECS/EC2                            |
| ------------------------------ | ------------------------- | ---------------------------------- |
| Server management              | Minimal                   | More                               |
| Scaling                        | Automatic                 | Configure/manage                   |
| Billing model                  | Invocation/duration based | Running resources                  |
| Long-running process           | Not ideal                 | Good                               |
| Event-driven workloads         | Excellent                 | Good                               |
| Cold starts                    | Possible                  | Usually no Lambda-style cold start |
| Infrastructure control         | Limited                   | Greater                            |
| Stateless workloads            | Excellent                 | Excellent                          |
| Predictable always-on workload | Sometimes less suitable   | Often suitable                     |

---

# 39. Lambda vs ECS

Use Lambda when:

```text
Short-lived
Event-driven
Variable traffic
Stateless
```

Use ECS when:

```text
Long-running
Containerized
Custom runtime requirements
High infrastructure control
Complex processes
```

---

# 40. Lambda vs EC2

EC2 gives you:

```text
OS
Networking
Processes
Runtime
Full server control
```

Lambda gives you:

```text
Function
Runtime
Event
Automatic scaling
Managed infrastructure
```

So Lambda is more abstract.

---

# 41. Lambda vs Kubernetes

Kubernetes is a container orchestration platform.

Lambda is a serverless function execution platform.

Conceptually:

```text
Kubernetes
   |
   +-- Pods
   +-- Services
   +-- Deployments
   +-- Nodes
   +-- Containers
```

Lambda:

```text
Event
  |
  v
Function
  |
  v
Execution
```

They solve different infrastructure problems.

---

# 42. Production Mistake: Database Connection Explosion

This is a **very important architect-level problem**.

Suppose:

```text
Lambda concurrency = 1000
```

and each Lambda creates a database connection.

You might suddenly have:

```text
1000 database connections
```

Your database may fail.

Architecture:

```text
                 Lambda
              / / / / / /
             / / / / / /
            v v v v v v
        Database
```

This is dangerous.

Possible solutions depend on the database:

```text
RDS Proxy
Connection pooling strategy
Concurrency limits
DynamoDB
Async queues
Caching
```

The important architectural principle is:

> Lambda can scale faster than your downstream dependencies.

---

# 43. Lambda and VPC

If Lambda needs to access private resources such as a private database:

```text
Lambda
   |
   v
VPC
   |
   v
Private RDS
```

You configure appropriate VPC networking.

But putting Lambda in a VPC should be done for a real networking requirement, not simply because "production applications should be in a VPC."

Modern AWS networking has improved significantly, but network configuration can still affect startup and connectivity characteristics.

---

# 44. Observability

Production Lambda applications should have:

```text
Logs
Metrics
Tracing
Alerts
```

AWS CloudWatch is central to this.

Typical metrics:

```text
Invocations
Errors
Duration
Throttles
ConcurrentExecutions
```

You should also use structured logging.

Instead of:

```java
System.out.println("Order failed");
```

prefer structured application logging with:

```text
orderId
customerId
requestId
timestamp
errorCode
```

---

# 45. Distributed Tracing

For distributed systems:

```text
API Gateway
    ↓
Lambda A
    ↓
SQS
    ↓
Lambda B
    ↓
DynamoDB
```

Tracing helps answer:

> Where did the request spend time?

AWS X-Ray and other observability tools can be used depending on your organization's setup.

---

# 46. Lambda Security Architecture

A production architecture should consider:

```text
Client
 |
 v
API Gateway
 |
 v
Lambda
 |
 +---- IAM
 |
 +---- Secrets Manager
 |
 +---- KMS
 |
 +---- VPC
 |
 +---- CloudWatch
```

Important practices:

* Least-privilege IAM
* Encrypt sensitive data
* Don't hard-code credentials
* Validate input
* Protect APIs
* Use appropriate authentication/authorization
* Monitor failures
* Keep dependencies patched

---

# 47. Deployment Strategies

Don't always deploy directly to production.

Common strategies include:

### Blue/Green

```text
Version 1 → Production
Version 2 → New version

Switch traffic
```

### Canary

```text
95% → Version 1
5%  → Version 2
```

If Version 2 works:

```text
80% → V2
20% → V1
```

Eventually:

```text
100% → V2
```

Lambda versions and aliases can support controlled deployments.

---

# 48. Lambda Versions

You can publish immutable versions.

Example:

```text
$LATEST
Version 1
Version 2
Version 3
```

An alias can point to a version:

```text
PROD → Version 3
```

Then you can change the alias rather than changing clients.

---

# 49. Lambda Layers vs Lambda Container Images

Lambda supports different packaging approaches, including ZIP-based packages and container images.

Container images are useful when:

* dependencies are large
* you have existing container tooling
* you need a custom packaging workflow

But:

> Using a container image does not turn Lambda into ECS.

It is still Lambda with Lambda's execution model and limits.

---

# 50. When Should You NOT Use Lambda?

Lambda is not automatically the best solution.

Avoid or reconsider it when you need:

### 1. Long-running workloads

```text
Hours-long processing
```

### 2. Constantly running applications

```text
Always-on service
```

### 3. Very high predictable traffic

Sometimes ECS/EC2 can be more economical depending on workload.

### 4. Stateful processes

Lambda is fundamentally designed around ephemeral execution environments.

### 5. Specialized OS-level control

Use EC2/ECS/EKS where appropriate.

---

# 51. Real Enterprise Architecture

Imagine a banking application:

```text
                       Internet
                           |
                           v
                     API Gateway
                           |
                           v
                    Authentication
                           |
                           v
                    Lambda Services
                     /     |      \
                    /      |       \
                   v       v        v
              Customer   Account   Payment
               Lambda     Lambda    Lambda
                  |         |         |
                  v         v         v
              DynamoDB    RDS      SQS
                                      |
                                      v
                               Payment Worker
                                  Lambda
                                      |
                         +------------+------------+
                         |                         |
                         v                         v
                    Notification              Audit Store
```

Cross-cutting services:

```text
IAM
CloudWatch
X-Ray
Secrets Manager
KMS
EventBridge
```

This is much closer to how Lambda is used in enterprise systems than simply deploying one function.

---

# 52. Function Granularity

A common architectural question:

> Should I create one huge Lambda or many small Lambdas?

Avoid:

```text
One giant Lambda

Customer
Payment
Order
Notification
Reporting
...
```

Prefer bounded responsibilities:

```text
Order Lambda
Payment Lambda
Notification Lambda
Customer Lambda
```

But don't blindly create hundreds of tiny functions without considering operational complexity.

A good rule:

> Align functions with business capabilities and independently deployable responsibilities.

---

# 53. Lambda and Microservices

Lambda can be used to implement parts of a serverless microservices architecture.

```text
             API Gateway
                  |
       +----------+----------+
       |          |          |
       v          v          v
    Order       Payment    Customer
    Lambda      Lambda     Lambda
       |           |          |
       v           v          v
     Store       Store      Store
```

But:

> Serverless does not automatically mean microservices.

You still need good domain boundaries.

---

# 54. Event-Driven Architecture

Lambda is particularly powerful with event-driven systems.

Example:

```text
Order Created
      |
      v
EventBridge
      |
      +------------+
      |            |
      v            v
Payment Lambda  Notification Lambda
      |
      v
Inventory Lambda
```

One event can trigger multiple independent consumers.

This reduces tight coupling.

---

# 55. Step Functions

Suppose your workflow is:

```text
Validate Order
      ↓
Reserve Inventory
      ↓
Charge Payment
      ↓
Generate Invoice
      ↓
Send Notification
```

Don't necessarily build:

```text
Lambda A → Lambda B → Lambda C → Lambda D
```

with complicated retry/state logic.

AWS Step Functions can orchestrate workflows:

```text
        Step Functions
              |
      +-------+-------+
      |       |       |
      v       v       v
   Lambda  Lambda  Lambda
```

This is useful for long-running or multi-step workflows.

---

# 56. Lambda Cost Model

Lambda pricing is generally based on factors such as:

```text
Number of requests
+
Execution duration
+
Configured memory/resources
```

Therefore:

```text
Faster execution
```

can reduce cost.

But don't optimize only for milliseconds.

Consider:

```text
Cost
+
Latency
+
Reliability
+
Operational complexity
```

---

# 57. Performance Optimization

For Java Lambda:

### 1. Minimize dependencies

Large dependency trees can increase startup time.

### 2. Initialize reusable clients outside the handler

```java
private static final DynamoDbClient client =
    DynamoDbClient.create();
```

### 3. Keep initialization lightweight

Avoid expensive work that isn't needed for every invocation.

### 4. Tune memory

More memory can mean more CPU.

### 5. Consider Provisioned Concurrency

For strict latency requirements.

### 6. Use appropriate Java runtime/features

Keep runtime and dependencies current.

### 7. Avoid unnecessary network calls

Every network call increases latency.

---

# 58. Common Mistakes

### Mistake 1

Creating expensive clients on every invocation.

### Mistake 2

Using static memory as persistent state.

### Mistake 3

Ignoring duplicate events.

### Mistake 4

Opening unlimited database connections.

### Mistake 5

Giving Lambda AdministratorAccess.

### Mistake 6

Putting secrets directly in source code.

### Mistake 7

Making Lambda functions excessively large.

### Mistake 8

Using synchronous Lambda chains for everything.

### Mistake 9

Ignoring downstream throttling.

### Mistake 10

Using Lambda for workloads better suited to ECS/Batch/Step Functions.

---

# 59. Scenario-Based Interview Questions

## Scenario 1

**Interviewer:**

> Your Lambda suddenly receives 10,000 requests per second. What problems could occur?

### Answer

Lambda can scale horizontally, but the downstream systems may not.

Potential bottlenecks:

```text
Lambda
  ↓
Database ← bottleneck
  ↓
External API ← bottleneck
```

I'd evaluate:

* Lambda concurrency
* reserved concurrency
* downstream database capacity
* connection limits
* API throttling
* queue-based buffering
* retries
* backpressure
* caching
* idempotency

---

# 60. Scenario 2

> Your Lambda API is taking 8 seconds after being idle but 500 ms afterward. Why?

Likely:

```text
Cold Start
```

Investigate:

* Java startup time
* dependency size
* initialization logic
* VPC/networking
* memory configuration

Potential solution:

```text
Provisioned Concurrency
```

and application optimization.

---

# 61. Scenario 3

> Lambda processes payment messages from SQS. Sometimes the same payment is processed twice. How do you solve it?

Answer:

Implement **idempotency**.

For example:

```text
paymentId
   |
   v
Check processed-payment store
   |
   +---- Exists → Don't process again
   |
   +---- Doesn't exist → Process
                         |
                         v
                  Mark processed
```

Also design the persistence/update operations carefully so that the check-and-record behavior is atomic where necessary.

---

# 62. Scenario 4

> Lambda processes SQS messages, but the database is becoming overloaded.

Possible solution:

```text
SQS
 |
 v
Lambda
 |
 +---- Concurrency control
 |
 +---- Batch processing
 |
 v
Database
```

Use appropriate Lambda concurrency controls, tune event source batching, and consider database-side improvements such as RDS Proxy or a different persistence architecture.

---

# 63. Scenario 5

> You need to process a 2-hour video.

Would you use Lambda?

**Probably not as the primary long-running processor.**

Consider:

```text
S3
 ↓
MediaConvert / ECS / Batch
 ↓
Output S3
```

Lambda can orchestrate the workflow:

```text
S3 event
 ↓
Lambda
 ↓
Start processing job
```

---

# 64. Scenario 6

> You need an API that requires 50 ms latency and receives traffic 24×7.

Would you automatically choose Lambda?

**No.**

I'd benchmark Lambda with provisioned concurrency against alternatives such as ECS.

Consider:

```text
Latency
Traffic pattern
Cost
Scaling
Operational complexity
```

Architecture decisions should be workload-driven.

---

# 65. Scenario 7

> One Lambda is responsible for Order, Payment, Customer and Notification. Would you keep it?

I'd investigate whether the responsibilities represent separate bounded contexts.

Potentially:

```text
Order Lambda
Payment Lambda
Customer Lambda
Notification Lambda
```

with event-driven communication.

But I would avoid splitting purely for the sake of creating more functions.

---

# 66. Scenario 8

> Your Lambda has 512 MB memory and takes 5 seconds. Increasing memory to 1 GB reduces execution time to 1.5 seconds. Which is better?

Don't answer based only on memory.

Compare:

```text
Cost per invocation
+
Latency
+
Throughput
+
Concurrency
```

Benchmark both configurations.

Higher memory may actually be cost-effective if the execution becomes substantially faster.

---

# 67. Scenario 9

> A Lambda writes to DynamoDB and then publishes an event. The DynamoDB write succeeds but event publishing fails. What happens?

This is a distributed consistency problem.

You could consider patterns such as:

```text
Transactional Outbox
```

or use services/features that provide appropriate transactional/event integration.

Don't assume:

```text
DB success + Event failure
```

will automatically roll back across independent AWS services.

---

# 68. Scenario 10 — Architect Level

> Design an order-processing system using Lambda.

A strong answer:

```text
                         Client
                            |
                            v
                       API Gateway
                            |
                            v
                       Order Lambda
                            |
                  +---------+---------+
                  |                   |
                  v                   v
              DynamoDB              EventBridge
                                      |
                    +-----------------+----------------+
                    |                 |                |
                    v                 v                v
              Payment Lambda   Inventory Lambda   Notification
                    |                 |                Lambda
                    v                 v
                 Payment           Inventory
                  Store              Store
```

For reliability:

```text
SQS
DLQ
Idempotency
Retries
CloudWatch
IAM
Secrets Manager
```

For workflow orchestration:

```text
Step Functions
```

where appropriate.

---

# 69. Senior Architect's Decision Framework

When someone says:

> "Let's use Lambda."

Don't immediately agree.

Ask:

### Workload

```text
Is it event-driven?
```

### Duration

```text
How long does processing take?
```

### Traffic

```text
Is traffic unpredictable or constant?
```

### Latency

```text
Do we have strict latency requirements?
```

### State

```text
Does the application require persistent state?
```

### Dependencies

```text
Can downstream systems handle Lambda's concurrency?
```

### Cost

```text
Is Lambda economical for this traffic pattern?
```

### Operations

```text
Do we want serverless operational simplicity?
```

That's the **architect mindset**.

---

# 70. Lambda — Beginner to Advanced Interview Questions

### Beginner

**Q1. What is AWS Lambda?**

Serverless compute service that executes code in response to events without requiring you to manage servers.

**Q2. What languages are supported?**

AWS provides managed runtimes for several languages, including Java, Python, JavaScript/Node.js, Go, .NET, and Ruby, along with custom runtime/container-image options.

**Q3. What triggers Lambda?**

Examples:

```text
API Gateway
S3
SQS
SNS
EventBridge
Kinesis
DynamoDB Streams
```

**Q4. Is Lambda serverless?**

Yes, from the customer's infrastructure-management perspective.

**Q5. Is Lambda always running?**

No. AWS creates and manages execution environments as needed.

---

# 71. Intermediate Questions

**Q6. What is a cold start?**

Latency introduced when AWS needs to initialize a new execution environment.

**Q7. What is warm invocation?**

An invocation using an already initialized environment.

**Q8. What is Lambda concurrency?**

The number of Lambda executions happening simultaneously.

**Q9. What is reserved concurrency?**

A function-level concurrency limit/reservation.

**Q10. What is provisioned concurrency?**

Pre-initialized execution environments maintained to reduce cold-start latency.

**Q11. Can Lambda access databases?**

Yes.

**Q12. Can Lambda be inside a VPC?**

Yes.

**Q13. What is a Lambda Layer?**

A mechanism for sharing dependencies/resources among Lambda functions.

---

# 72. Advanced Questions

**Q14. How does Lambda scale?**

By creating additional execution environments to handle concurrent invocations, subject to concurrency and service limits.

**Q15. What happens when Lambda reaches concurrency limits?**

Additional invocations may be throttled, depending on the invocation source and configuration.

**Q16. How do you prevent Lambda from overwhelming RDS?**

Potential approaches:

```text
Reserved concurrency
RDS Proxy
Connection management
SQS buffering
Caching
Database scaling
```

**Q17. How do you handle duplicate events?**

Idempotency.

**Q18. How do you reduce cold starts?**

```text
Reduce initialization
Reduce dependencies
Tune memory
Provisioned concurrency
Use suitable runtime/framework optimizations
```

**Q19. How do you implement retries?**

Depends on invocation model:

```text
Async invocation
SQS
Event source mapping
Step Functions
```

Each has different retry/error semantics.

**Q20. How do you monitor Lambda?**

```text
CloudWatch Logs
CloudWatch Metrics
X-Ray
Alarms
Distributed tracing/observability tooling
```

---

# 73. Very Advanced Interview Question

### "Why can Lambda scale the application but still cause an outage?"

Because scaling the compute layer doesn't mean every dependency scales equally.

For example:

```text
                    1 Lambda
                       |
                       v
                    1000 Lambdas
                       |
                       v
                    RDS
                  / / / / /
                 / / / / /
              CONNECTION LIMIT
                    ↓
                  FAILURE
```

The senior answer is:

> **Serverless removes infrastructure capacity management for the function, but it does not eliminate capacity constraints in downstream systems.**

This is one of the most important concepts to understand.

---

# 74. Lambda Production Checklist

Before deploying:

```text
☑ Correct IAM role
☑ Least privilege
☑ Timeout configured
☑ Memory benchmarked
☑ Concurrency considered
☑ Idempotency implemented
☑ Retry behavior understood
☑ DLQ/destination configured where appropriate
☑ Database connections controlled
☑ Secrets managed securely
☑ CloudWatch monitoring
☑ Alarms
☑ Structured logging
☑ Tracing where needed
☑ Deployment strategy
☑ Rollback strategy
☑ Dependency vulnerabilities checked
☑ Cost monitored
```

---

# 75. Quick Revision Cheat Sheet

```text
AWS LAMBDA
│
├── Serverless compute
│
├── Event-driven
│
├── Execution Environment
│   ├── Init
│   ├── Invoke
│   └── Reuse / Destroy
│
├── Invocation
│   ├── Synchronous
│   ├── Asynchronous
│   └── Event Source Mapping
│
├── Scaling
│   ├── Concurrency
│   ├── Reserved Concurrency
│   └── Provisioned Concurrency
│
├── Performance
│   ├── Cold Start
│   ├── Memory/CPU
│   ├── Initialization
│   └── Dependency size
│
├── Integration
│   ├── API Gateway
│   ├── S3
│   ├── SQS
│   ├── SNS
│   ├── EventBridge
│   ├── DynamoDB
│   └── Kinesis
│
├── Reliability
│   ├── Retry
│   ├── DLQ
│   ├── Idempotency
│   └── Backpressure
│
├── Security
│   ├── IAM
│   ├── KMS
│   ├── Secrets Manager
│   └── VPC
│
└── Observability
    ├── CloudWatch
    ├── Metrics
    ├── Logs
    └── X-Ray
```

## ⭐ The 5 things I'd memorize for a Senior interview

**1. Lambda is not just "code without servers."**

Think:

> **Event + Function + Execution Environment + Automatic Scaling**

**2. Understand cold vs warm execution.**

**3. Understand concurrency and downstream bottlenecks.**

**4. Always mention idempotency for event-driven processing.**

**5. When designing production Lambda systems, think beyond Lambda:**

```text
API Gateway
     ↓
Lambda
     ↓
SQS/EventBridge
     ↓
Lambda
     ↓
DynamoDB/RDS
     ↓
CloudWatch
```

The strongest senior-level answer is not *"Lambda automatically scales."* It is:

> **"Lambda scales execution environments automatically, but I must control concurrency and design for the capacity, connection limits, retries, idempotency, and failure behavior of downstream systems."**

That demonstrates **Senior Developer + Architect-level understanding**.
