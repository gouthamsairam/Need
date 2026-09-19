Absolutely. The easiest way to understand this is to look at **Spring Boot deployment on AWS as one application, deployed on four different compute models**:

```text
 Spring Boot Application
 |
 +--------------------+--------------------+
 | | |
 v v v
 EKS ECS + Fargate Lambda
 | |
 | |
 v v
 Kubernetes ECS Service
 |
 v
 EC2 Worker Nodes
```

And there is a fourth model:

```text
ECS + EC2
 |
 v
Your own EC2 instances run ECS tasks
```

The important interview concept is:

>**EKS = Kubernetes orchestration**
>**ECS + Fargate = AWS-managed container orchestration without managing EC2 servers**
>**ECS + EC2 = AWS container orchestration where you manage the underlying EC2 capacity**
>**Lambda = serverless function execution, best suited to event-driven/short-lived workloads rather than a traditional always-running Spring Boot service**

---

# 1. First understand the common architecture

Regardless of whether you use EKS, ECS or Lambda, a production Spring Boot application usually looks something like this:

```text
 Internet
 |
 v
 Route 53 / DNS
 |
 v
 CloudFront / WAF
 |
 v
 Load Balancer
 ALB / API Gateway
 |
 +-------------+-------------+
 | | |
 v v v
 Spring Boot Spring Boot Spring Boot
 Instance Instance Instance
 | | |
 +-------------+-------------+
 |
 +-----------+-----------+
 | |
 v v
 Database Other Services
 RDS/Aurora Internal APIs
 | |
 v v
 ElastiCache SQS/Kafka
```

The compute service changes, but the **application communication model remains largely the same**.

---

# 2. The first decision: which AWS service?

This is the first thing I'd discuss as a Senior Architect.

| Requirement | Recommended |
| ----------------------------------------- | ---------------------------------------------------- |
| Need Kubernetes | **EKS** |
| Already have Kubernetes expertise | **EKS** |
| Want containers without managing servers | **ECS + Fargate** |
| Simple Spring Boot REST API | **ECS + Fargate** is often a strong choice |
| Need maximum control over EC2 | **ECS + EC2** |
| Existing EC2 infrastructure | **ECS + EC2** or plain EC2 depending on requirements |
| Event-driven short-running function | **Lambda** |
| S3/SQS/EventBridge-triggered processing | **Lambda** |
| Very large enterprise Kubernetes platform | **EKS** |
| Long-running container | **ECS/EKS** |
| Need Kubernetes ecosystem/operators | **EKS** |

### My default choice for a new Spring Boot API

If there is no special requirement:

>**ECS + Fargate is often the simplest production container approach.**

You don't need to manage EC2 machines, but you still get a normal Dockerized Spring Boot application.

---

# 3. Before deployment — Dockerize Spring Boot

For EKS, ECS Fargate and ECS EC2, I would normally package Spring Boot as a Docker image.

Architecture:

```text
Spring Boot
 |
 v
Maven/Gradle
 |
 v
JAR
 |
 v
Docker Image
 |
 v
Amazon ECR
 |
 +---------> EKS
 |
 +---------> ECS Fargate
 |
 +---------> ECS EC2
```

---

# 4. Spring Boot project

Example:

```text
order-service/
│
├── src/
│ ├── main/
│ │ ├── java/
│ │ └── resources/
│ │ └── application.yml
│ │
│ └── test/
│
├── pom.xml
├── Dockerfile
└── .dockerignore
```

---

# 5. Spring Boot configuration

For example:

```yaml
server:
 port: 8080

spring:
 application:
 name: order-service

 datasource:
 url: ${DB_URL}
 username: ${DB_USERNAME}
 password: ${DB_PASSWORD}

 jpa:
 hibernate:
 ddl-auto: validate
```

Notice something important:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

We don't hardcode production database credentials.

---

# 6. Where should configuration come from?

Production architecture:

```text
AWS Secrets Manager
 |
 v
Environment / Secret injection
 |
 v
Spring Boot
```

or:

```text
AWS Systems Manager Parameter Store
 |
 v
Application configuration
```

For sensitive values, I generally prefer:

>**AWS Secrets Manager**

For ordinary configuration:

>**Parameter Store / environment-specific configuration**

---

# 7. Dockerfile

A simple production-oriented Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/order-service.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build:

```bash
mvn cleanpackage
```

Then:

```bash
docker build-torder-service:1.0.
```

Run locally:

```bash
docker run-p8080:8080order-service:1.0
```

---

# 8. Push Docker image to ECR

Architecture:

```text
Developer
 |
 v
Docker Build
 |
 v
Docker Image
 |
 v
Amazon ECR
```

Example:

```bash
aws ecrcreate-repository \
 --repository-name order-service
```

Authenticate:

```bash
aws ecrget-login-password \
 --region ap-south-1 |
docker login \
 --username AWS \
--password-stdin <account>.dkr.ecr.ap-south-1.amazonaws.com
```

Tag:

```bash
docker tagorder-service:1.0 \
<account>.dkr.ecr.ap-south-1.amazonaws.com/order-service:1.0
```

Push:

```bash
docker push \
<account>.dkr.ecr.ap-south-1.amazonaws.com/order-service:1.0
```

Now:

```text
ECR
 |
 +-- order-service:1.0
```

---

# 9. Now let's understand EKS

## What is EKS?

**Amazon Elastic Kubernetes Service** is AWS's managed Kubernetes service.

AWS manages the Kubernetes control plane for you, while your workloads run on Kubernetes worker capacity such as managed EC2 nodes or Fargate.

Conceptually:

```text
 AWS
 |
 EKS Control Plane
 |
 +-------------+-------------+
 | |
 v v
 Worker Nodes Fargate
 EC2 (optional)
 |
 v
 Kubernetes Pods
 |
 v
 Spring Boot
```

---

# 10. EKS deployment architecture

A typical production architecture:

```text
 Internet
 |
 v
 Route 53
 |
 v
 WAF
 |
 v
 ALB
 |
 v
 Kubernetes Service
 |
 +--------------+--------------+
 | | |
 v v v
 Pod Pod Pod
 Spring Boot Spring Boot Spring Boot
 | | |
 +--------------+--------------+
 |
 v
 RDS
```

---

# 11. EKS important components

You should know these for interviews:

```text
EKS
 |
 +-- Cluster
 |
 +-- Control Plane
 |
 +-- Node Group
 | |
 | +-- EC2
 |
 +-- Pod
 |
 +-- Deployment
 |
 +-- Service
 |
 +-- Ingress
 |
 +-- ConfigMap
 |
 +-- Secret
 |
 +-- HPA
```

---

# 12. What is a Pod?

A Pod is the basic deployable unit in Kubernetes.

Your Spring Boot application normally runs inside a container inside a Pod.

```text
Pod
 |
 +-- Spring Boot Container
```

You may have:

```text
Pod 1 → order-service
Pod 2 → order-service
Pod 3 → order-service
```

---

# 13. Deployment YAML

Example:

```yaml
apiVersion: apps/v1
kind: Deployment

metadata:
 name: order-service

spec:
 replicas: 3

 selector:
 matchLabels:
 app: order-service

 template:
 metadata:
 labels:
 app: order-service

 spec:
 containers:
 - name: order-service

 image: <account>.dkr.ecr.ap-south-1.amazonaws.com/order-service:1.0

 ports:
 - containerPort: 8080

 env:
 - name: DB_URL
 valueFrom:
 secretKeyRef:
 name: order-db-secret
 key: url
```

This tells Kubernetes:

> Run 3 instances of my Spring Boot application.

---

# 14. Kubernetes Service

Pods are ephemeral.

Their IP addresses can change.

Therefore we use a Service.

```yaml
apiVersion: v1
kind: Service

metadata:
 name: order-service

spec:
 selector:
 app: order-service

 ports:
 - port: 80
 targetPort: 8080

 type: ClusterIP
```

Architecture:

```text
 Service
 order-service
 |
 +------------+------------+
 | | |
 v v v
 Pod 1 Pod 2 Pod 3
 :8080 :8080 :8080
```

---

# 15. Internal service communication in EKS

Suppose:

```text
Order Service
 |
 v
Payment Service
```

Payment Service:

```text
payment-service
```

Order service can call:

```text
http://payment-service:8080/payments
```

Because Kubernetes DNS resolves the Service.

Architecture:

```text
Order Pod
 |
 | HTTP
 v
payment-service
 |
 v
Payment Pods
```

This is **internal service-to-service communication**.

---

# 16. External database communication

Suppose database is:

```text
Amazon RDS PostgreSQL
```

Architecture:

```text
Spring Boot Pod
 |
 | JDBC
 v
RDS PostgreSQL
```

Configuration:

```yaml
spring:
 datasource:
 url: jdbc:postgresql://${DB_HOST}:5432/orders
```

AWS networking:

```text
EKS Pod
 |
 v
VPC
 |
 v
Security Group / Network Policy
 |
 v
RDS
```

You should normally keep RDS private rather than exposing the database directly to the internet.

---

# 17. Security groups

Example:

```text
EKS nodes/pods
 |
 | TCP 5432
 v
RDS
```

RDS security group:

```text
Inbound:
TCP 5432
Source = application security group
```

Not:

```text
0.0.0.0/0
```

for a production database.

---

# 18. EKS configuration files

Common Kubernetes files:

```text
deployment.yaml
service.yaml
ingress.yaml
configmap.yaml
secret.yaml
hpa.yaml
namespace.yaml
```

For enterprise applications, you might use:

```text
Helm
Kustomize
Argo CD
Flux
```

rather than maintaining dozens of raw YAML files manually.

---

# 19. EKS deployment commands

Example:

```bash
aws eksupdate-kubeconfig \
 --region ap-south-1 \
--nameproduction-cluster
```

Then:

```bash
kubectl apply-fdeployment.yaml
```

```bash
kubectl apply-fservice.yaml
```

Check:

```bash
kubectl getpods
```

```bash
kubectl getservices
```

Logs:

```bash
kubectl logs <pod-name>
```

---

# 20. EKS scaling

Suppose:

```text
Normal traffic
3 Pods
```

High traffic:

```text
3 Pods
 ↓
10 Pods
```

Use:

```text
Horizontal Pod Autoscaler
```

Conceptually:

```text
 HPA
 |
 CPU / Memory / metrics
 |
 v
 +----------+----------+
 | | |
 Pod 1 Pod 2 Pod 3
 |
 High traffic
 |
 v
 More Pods
```

You can also use cluster/node autoscaling mechanisms to add worker capacity.

---

# 21. Now ECS

ECS means:

>**Amazon Elastic Container Service**

It is AWS's container orchestration service.

You don't need Kubernetes.

Architecture:

```text
ECS Cluster
 |
 +-- Service
 |
 +-- Task
 |
 +-- Container
 |
 +-- Spring Boot
```

---

# 22. ECS has two major compute options

This is extremely important:

```text
 ECS
 |
 +-------+-------+
 | |
 Fargate EC2
 | |
 AWS manages You manage
 infrastructure EC2 capacity
```

---

# 23. ECS + Fargate

Fargate is serverless compute for containers.

You provide:

```text
Docker image
CPU
Memory
Networking
IAM
Environment
```

AWS handles the underlying servers.

Architecture:

```text
 ECS
 |
 Service
 |
 +------------+------------+
 | | |
 Task Task Task
 | | |
 Container Container Container
 | | |
 Spring Boot Spring Boot Spring Boot
```

No EC2 instances for you to manage.

---

# 24. ECS + Fargate task definition

A simplified task definition:

```json
{
"family": "order-service",

"cpu": "512",
"memory": "1024",

"containerDefinitions": [
 {
"name": "order-service",

"image": "<account>.dkr.ecr.ap-south-1.amazonaws.com/order-service:1.0",

"portMappings": [
 {
"containerPort": 8080
 }
 ],

"environment": [
 {
"name": "SPRING_PROFILES_ACTIVE",
"value": "prod"
 }
 ]
 }
 ]
}
```

The task definition describes how the container should run.

---

# 25. ECS Service

The ECS Service maintains the desired number of tasks.

For example:

```text
Desired count = 3
```

ECS ensures approximately:

```text
Task 1 ✓
Task 2 ✓
Task 3 ✓
```

If Task 2 crashes:

```text
Task 2 ✗
```

ECS can replace it:

```text
Task 4 ✓
```

---

# 26. ECS Fargate architecture with ALB

Production architecture:

```text
 Internet
 |
 v
 Route 53
 |
 v
 WAF
 |
 v
 ALB
 |
 Target Group
 |
 +-------------+-------------+
 | | |
 v v v
 Fargate Fargate Fargate
 Task Task Task
 | | |
 +-------------+-------------+
 |
 v
 RDS
```

---

# 27. ECS + EC2

Now the difference.

Here AWS ECS manages containers, but **you provide EC2 instances as the compute capacity**.

Architecture:

```text
 ECS Cluster
 |
 +-------------+-------------+
 | | |
 EC2-1 EC2-2 EC2-3
 | | |
 Task Task Task
 | | |
 Spring Boot Spring Boot Spring Boot
```

You are responsible for more infrastructure.

---

# 28. ECS + EC2 vs Fargate

| | ECS Fargate | ECS EC2 |
| ----------------------- | ----------------------- | ---------------------------------- |
| EC2 management | AWS | You |
| OS patching | AWS | You |
| Capacity management | AWS | You |
| Container orchestration | ECS | ECS |
| Cost model | Per task resources | EC2 capacity |
| Control | Less | More |
| Operational effort | Lower | Higher |
| Good for | Most standard workloads | Specialized/high-control workloads |

---

# 29. When ECS EC2 makes sense

Consider it when:

* you need special EC2 instance types
* workloads need more control over the host
* you have existing EC2 capacity
* you have high and predictable utilization
* you need host-level customization
* you need specialized hardware/capabilities

Otherwise:

>**Fargate is usually simpler.**

---

# 30. ECS internal communication

Suppose:

```text
Order Service
 |
 v
Payment Service
```

You can use ECS Service Connect, Cloud Map/service discovery, or an internal load balancer depending on architecture.

Conceptually:

```text
Order Task
 |
 | HTTP
 v
Payment Service
 |
 +-- Payment Task 1
 +-- Payment Task 2
```

The important interview answer is:

>**ECS provides mechanisms such as service discovery/Service Connect and load balancing for service-to-service communication.**

---

# 31. ECS → RDS

Same basic model:

```text
Spring Boot Task
 |
 | JDBC
 v
 RDS
```

Security:

```text
ECS Security Group
 |
 | TCP 5432
 v
RDS Security Group
```

Configuration:

```yaml
spring:
 datasource:
 url: jdbc:postgresql://${DB_HOST}:5432/orders
 username: ${DB_USERNAME}
 password: ${DB_PASSWORD}
```

Secrets should come from AWS Secrets Manager or an equivalent secure configuration mechanism, not source code.

---

# 32. ECS → External APIs

Suppose Spring Boot needs:

```text
Payment Gateway
```

Architecture:

```text
ECS Task
 |
 v
Private Subnet
 |
 v
NAT Gateway
 |
 v
Internet
 |
 v
External Payment API
```

Why NAT?

If your ECS tasks are in private subnets, they can use a NAT Gateway for outbound internet access without being directly reachable from the internet.

---

# 33. ECS → AWS services

For example:

```text
Spring Boot
 |
 +---- S3
 |
 +---- SQS
 |
 +---- DynamoDB
 |
 +---- Secrets Manager
 |
 +---- CloudWatch
```

Don't put AWS access keys inside:

```text
application.yml
```

Instead use:

>**IAM roles for tasks / task roles**

Conceptually:

```text
ECS Task
 |
 v
IAM Task Role
 |
 +---- S3 permissions
 +---- SQS permissions
 +---- Secrets Manager permissions
```

---

# 34. Now Lambda

Lambda is different.

You shouldn't think of Lambda as:

> "A small EC2 server."

Instead:

>**Lambda runs functions in response to events/requests without you managing servers.**

Architecture:

```text
Event
 |
 v
Lambda
 |
 v
Spring Boot functionality
```

---

# 35. Can Spring Boot run on Lambda?

**Yes.**

There are several approaches.

The common modern approach is:

```text
Spring Boot
 |
 v
AWS Lambda
```

using Spring Cloud Function / AWS Lambda integration, or AWS's Java Lambda runtime with appropriate adapters.

But there's an important architectural consideration:

> Don't blindly take a large traditional always-running Spring Boot application and put the entire thing into Lambda.

Lambda works best when the workload fits the function execution model.

---

# 36. Lambda architecture

For an HTTP API:

```text
 Internet
 |
 v
 API Gateway
 |
 v
 Lambda
 |
 +-------+-------+
 | |
 v v
 RDS SQS
```

For event-driven processing:

```text
S3
 |
 | Object uploaded
 v
Lambda
 |
 v
Spring logic
```

Or:

```text
SQS
 |
 v
Lambda
 |
 v
Spring business logic
```

---

# 37. Lambda + Spring Boot

Conceptually:

```text
API Gateway
 |
 v
AWS Lambda
 |
 v
Spring Context
 |
 v
Controller / Function
 |
 v
Service
 |
 v
Repository
 |
 v
RDS
```

However, Spring application startup can increase initialization time and memory usage.

This is one reason Lambda isn't automatically the best deployment model for every Spring Boot application.

---

# 38. Lambda cold start

Important interview topic.

Suppose Lambda hasn't been invoked recently.

```text
Request
 |
 v
Create execution environment
 |
 v
Initialize runtime
 |
 v
Initialize Spring
 |
 v
Execute function
```

That initial request may take longer.

Later:

```text
Request
 |
 v
Existing warm environment
 |
 v
Execute
```

This is the **cold-start/warm-start** distinction.

---

# 39. Lambda and database connections

This is an important production concern.

If Lambda scales:

```text
1 Lambda
 ↓
100 concurrent Lambda executions
```

you might suddenly have many database connections.

```text
Lambda × 100
 |
 +------> RDS
 |
 +------> RDS
 |
 +------> RDS
```

This can overwhelm the DB connection limit.

For Lambda + relational databases, consider:

>**Amazon RDS Proxy**

Architecture:

```text
Lambda
 | | | | |
 v v v v v
RDS Proxy
 |
 v
 RDS
```

The proxy helps manage and reuse database connections.

---

# 40. Lambda networking

If Lambda needs access to private RDS:

```text
Lambda
 |
 v
VPC
 |
 v
Private Subnet
 |
 v
RDS
```

But putting Lambda into a VPC has networking implications, especially for outbound internet access.

For internet access from private subnets:

```text
Lambda
 ↓
Private Subnet
 ↓
NAT Gateway
 ↓
Internet
```

---

# 41. Lambda configuration

You may have:

```text
template.yaml
serverless.yml
AWS SAM
CDK
Terraform
CloudFormation
```

depending on the infrastructure-as-code approach.

For enterprise AWS, common choices include:

```text
Terraform
CDK
CloudFormation/SAM
```

---

# 42. Spring Boot configuration across all services

A useful pattern:

```text
application.yml
application-dev.yml
application-qa.yml
application-prod.yml
```

Example:

```yaml
spring:
 application:
 name: order-service

server:
 port: 8080
```

Production:

```yaml
spring:
 datasource:
 url: ${DB_URL}
```

Then AWS injects:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

---

# 43. Don't store environment-specific configuration in the image

Bad:

```dockerfile
ENV DB_PASSWORD=MyPassword
```

Don't do that.

Better:

```text
Docker Image
 |
 | same image
 |
 +---+---------+---------+
 | | |
 DEV QA PROD
 | | |
 config config secrets
```

This is called:

>**Build once, deploy many.**

---

# 44. Database communication — complete flow

Let's say we use ECS Fargate.

```text
User
 |
 v
ALB
 |
 v
ECS Fargate
 |
 v
Spring Boot
 |
 | JDBC
 v
RDS PostgreSQL
```

Detailed:

```text
Spring Boot
 |
 | HikariCP
 v
Connection Pool
 |
 | TCP 5432
 v
RDS
 |
 v
PostgreSQL
```

Spring Boot commonly uses HikariCP as the connection pool.

---

# 45. Internal microservice communication

Suppose:

```text
Order Service
Payment Service
Inventory Service
Notification Service
```

Architecture:

```text
 Order Service
 / | \
 / | \
 v v v
 Payment Inventory Notification
 Service Service Service
```

Communication can be:

### Synchronous

```text
REST
HTTP
gRPC
```

### Asynchronous

```text
SQS
SNS
EventBridge
Kafka / Amazon MSK
```

---

# 46. When should you use synchronous communication?

For immediate response:

```text
Order
 |
 | "Is payment valid?"
 v
Payment
 |
 v
Response
```

Use:

```text
REST
gRPC
```

when the caller needs an immediate answer.

---

# 47. When asynchronous?

Suppose order is created:

```text
Order Service
 |
 v
OrderCreated Event
 |
 +----> Notification
 |
 +----> Analytics
 |
 +----> Inventory
```

You can use:

```text
SQS
SNS
EventBridge
Kafka
```

Benefits:

* decoupling
* resilience
* independent scaling
* buffering

---

# 48. SQS example

```text
Order Service
 |
 | Send message
 v
 SQS
 |
 v
Notification Consumer
 |
 v
Spring Boot
```

If Notification service is down:

```text
SQS
 |
 +-- message remains
```

and the consumer can process it later, subject to visibility timeout and retention configuration.

---

# 49. REST internal communication

For ECS:

```text
Order ECS Service
 |
 | HTTP
 v
Payment ECS Service
```

For EKS:

```text
Order Pod
 |
 | HTTP
 v
payment-service
```

For Lambda:

```text
Order Lambda
 |
 | HTTP
 v
Payment Service
```

The communication pattern isn't fundamentally changed by the compute service.

---

# 50. AWS networking architecture

This is critical for interviews.

A typical production VPC:

```text
 VPC
 |
 +---------------+---------------+
 | |
 Public Subnets Private Subnets
 | |
 +----+----+ +-------+-------+
 | | | |
 ALB NAT GW ECS/EKS RDS
 Spring Boot
```

More specifically:

```text
Internet
 |
Internet Gateway
 |
Public Subnet
 |
ALB
 |
Private Subnet
 |
Spring Boot
 |
Private DB Subnet
 |
RDS
```

---

# 51. Why put Spring Boot in private subnet?

Because users don't need direct access to the application instances.

Instead:

```text
Internet
 |
 v
ALB
 |
 v
Private Spring Boot
```

This reduces the attack surface.

---

# 52. Typical production VPC

```text
 VPC
 |
 +---------------+---------------+
 | |
 Public Subnets Private Subnets
 | |
 | +-------+-------+
 | | |
 ALB Application DB
 | | |
 | EKS/ECS RDS
 |
 NAT Gateway
 |
 v
 Internet
```

---

# 53. Load balancing

For Spring Boot REST APIs, commonly:

>**Application Load Balancer (ALB)**

Architecture:

```text
 ALB
 |
 +----------+----------+
 | | |
 v v v
 App 1 App 2 App 3
```

The ALB performs health checks.

Example:

```text
GET /actuator/health
```

If one instance is unhealthy:

```text
App 2 ✗
```

ALB stops routing traffic to it.

---

# 54. Health checks

Spring Boot Actuator is very useful.

Dependency:

```xml
<dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Configuration:

```yaml
management:
 endpoints:
 web:
 exposure:
 include: health,info
```

Endpoint:

```text
/actuator/health
```

You can use this for container/load-balancer health checks.

---

# 55. EKS health probes

Kubernetes uses:

```text
livenessProbe
readinessProbe
startupProbe
```

Example:

```yaml
livenessProbe:
 httpGet:
 path: /actuator/health
 port: 8080

readinessProbe:
 httpGet:
 path: /actuator/health
 port: 8080
```

### Liveness

> Is the application alive?

### Readiness

> Can this application receive traffic?

### Startup

> Has the application finished starting?

This distinction is extremely important.

---

# 56. ECS health check

ECS can use container health checks and ALB target health checks.

Conceptually:

```text
ALB
 |
 | Health check
 v
Spring Boot
 |
 +-- Healthy → receive traffic
 |
 +-- Unhealthy → stop routing
```

---

# 57. Logging

For containers, don't primarily write logs into local files.

Prefer:

```text
Spring Boot
 |
 v
stdout/stderr
 |
 v
CloudWatch Logs
```

For EKS, you can also use centralized logging stacks/platforms.

---

# 58. Monitoring

Typical AWS architecture:

```text
Spring Boot
 |
 +-- Logs → CloudWatch
 |
 +-- Metrics → CloudWatch/Prometheus
 |
 +-- Traces → tracing platform
```

Monitor:

* CPU
* memory
* latency
* error rate
* request count
* DB connections
* JVM metrics
* GC
* container restarts

---

# 59. CI/CD architecture

A strong enterprise pipeline:

```text
Developer
 |
 v
Git
 |
 v
Jenkins / GitHub Actions / GitLab / AWS tooling
 |
 +---- Unit Test
 |
 +---- SonarQube
 |
 +---- Security Scan
 |
 +---- Maven Build
 |
 +---- Docker Build
 |
 +---- Push ECR
 |
 +---- Deploy
 |
 +-------- EKS
 |
 +-------- ECS
 |
 +-------- Lambda
```

---

# 60. ECS deployment pipeline

```text
Git
 |
 v
Maven
 |
 v
JAR
 |
 v
Docker
 |
 v
ECR
 |
 v
ECS Task Definition
 |
 v
ECS Service
 |
 v
ALB
 |
 v
Users
```

---
# 61. EKS deployment pipeline

```text
Git
 |
 v
Maven
 |
 v
Docker
 |
 v
ECR
 |
 v
Kubernetes Deployment
 |
 v
EKS
 |
 v
Service / Ingress / ALB
 |
 v
Users
```

---

# 62. Lambda deployment pipeline

```text
Git
 |
 v
Maven
 |
 v
Package
 |
 v
Lambda Artifact / Container Image
 |
 v
Lambda
 |
 v
API Gateway / SQS / EventBridge / S3
```

---

# 63. What configuration files do we use?

This is a very good interview question.

## Spring Boot

```text
pom.xml
application.yml
application-{profile}.yml
Dockerfile
```

---

## EKS

```text
deployment.yaml
service.yaml
ingress.yaml
configmap.yaml
secret.yaml
hpa.yaml
namespace.yaml
```

Usually managed through:

```text
Helm
Kustomize
```

for larger systems.

---

## ECS

Usually:

```text
task-definition.json
service configuration
ALB/target group configuration
IAM roles
CloudFormation/CDK/Terraform
```

---

## Lambda

Depending on deployment approach:

```text
template.yaml
serverless.yml
CDK
Terraform
CloudFormation
```

---

# 64. Terraform enterprise approach

Instead of manually creating everything:

```text
Terraform
 |
 +-- VPC
 +-- Subnets
 +-- Security Groups
 +-- IAM
 +-- ECR
 +-- ECS
 +-- EKS
 +-- ALB
 +-- RDS
 +-- CloudWatch
```

Infrastructure becomes code.

---

# 65. How I would organize an enterprise repository

For EKS:

```text
project/
│
├── application/
│ ├── src/
│ ├── pom.xml
│ └── Dockerfile
│
├── k8s/
│ ├── deployment.yaml
│ ├── service.yaml
│ ├── ingress.yaml
│ ├── hpa.yaml
│ └── configmap.yaml
│
└── infrastructure/
 └── terraform/
```

For ECS:

```text
project/
│
├── application/
│ ├── src/
│ ├── pom.xml
│ └── Dockerfile
│
├── ecs/
│ └── task-definition.json
│
└── terraform/
```

---

# 66. Deployment strategy

Don't directly do:

```text
Developer → Production
```

Use:

```text
DEV
 ↓
QA
 ↓
UAT
 ↓
PROD
```

And production:

```text
New Version
 |
 v
Canary
 |
 v
Monitor
 |
 v
100%
```

or:

```text
Blue
Green
```

---

# 67. EKS rolling deployment

Suppose:

```text
v1:
Pod 1
Pod 2
Pod 3
```

Deploy v2:

```text
v1 → v2
Pod 1 v1
Pod 2 v1
Pod 3 v1
 ↓
Pod 1 v2
Pod 2 v1
Pod 3 v1
 ↓
Pod 1 v2
Pod 2 v2
Pod 3 v1
 ↓
Pod 1 v2
Pod 2 v2
Pod 3 v2
```

Kubernetes performs a rolling update according to the Deployment strategy.

---

# 68. ECS rolling deployment

Similar idea:

```text
Old Tasks
 ↓
Start New Tasks
 ↓
Health check
 ↓
Stop Old Tasks
```

ECS supports deployment strategies including rolling deployments, and AWS deployment tooling can also support blue/green patterns.

---

# 69. Lambda deployment

Lambda versions can be used:

```text
Lambda
 |
 +-- Version 1
 |
 +-- Version 2
```

Then aliases:

```text
PROD
 |
 +-- 90% → v1
 +-- 10% → v2
```

This can support gradual rollout patterns.

---

# 70. Database migration

This is often overlooked.

Suppose Spring Boot v2 requires:

```text
New column
```

Don't simply deploy application first and break old instances.

Use:

```text
Flyway
```

or:

```text
Liquibase
```

Migration strategy:

```text
1. Add backward-compatible DB change
2. Deploy application
3. Start using new field
4. Remove old field later
```

This is called an **expand-and-contract** approach.

---

# 71. ECS/EKS/Lambda — database connectivity comparison

| | EKS | ECS Fargate | ECS EC2 | Lambda |
| ---------------------------- | --------- | ----------- | --------- | ---------------------------------------- |
| RDS connection | JDBC | JDBC | JDBC | JDBC possible |
| Private VPC | Yes | Yes | Yes | Yes |
| Connection pooling | HikariCP | HikariCP | HikariCP | Requires special consideration |
| RDS Proxy useful | Sometimes | Sometimes | Sometimes | **Especially useful** |
| Long-running connection pool | Yes | Yes | Yes | Not reliable as traditional server model |

Lambda's execution environments may be reused, but you should **not assume** traditional server-style persistent connection behavior under arbitrary concurrency.

---

# 72. Choosing the service — practical scenarios

## Scenario A

> "We have a simple Spring Boot REST API with 10 endpoints."

I'd choose:

```text
ECS Fargate
```

unless there is another requirement.

Why?

```text
Docker
+
Simple deployment
+
No EC2 management
+
Auto scaling
+
ALB
```

---

# 73. Scenario B

> "We have 50 microservices and the organization already has a Kubernetes platform."

Choose:

```text
EKS
```

because:

```text
Existing Kubernetes skills
+
Common platform
+
Helm
+
Service mesh
+
Kubernetes ecosystem
```

can justify it.

---

# 74. Scenario C

> "We need to process S3 files whenever they are uploaded."

Choose:

```text
S3
 |
 v
Lambda
 |
 v
Process file
```

No need for an always-running Spring Boot container for a small event-driven processor.

---

# 75. Scenario D

> "We have an application that must continuously run and process requests."

Choose:

```text
ECS Fargate
```

or:

```text
EKS
```

rather than Lambda if the workload doesn't naturally fit Lambda's execution model.

---

# 76. Scenario E

> "We need very specific EC2 instance types and host-level control."

Consider:

```text
ECS + EC2
```

---

# 77. Scenario F

> "Our organization needs Kubernetes CRDs, operators, service mesh and Kubernetes-native tooling."

Choose:

```text
EKS
```

---

# 78. The most important comparison

```text
 COMPUTE
 |
 +--------------+--------------+
 | | |
 EKS ECS Lambda
 | |
 Kubernetes |
 | +-----+-----+
 | | |
 EC2 Fargate EC2
```

### EKS

> "I want Kubernetes."

### ECS Fargate

> "I want containers but don't want to manage servers."

### ECS EC2

> "I want ECS but I want control over the underlying EC2 capacity."

### Lambda

> "I want event/function-based serverless execution."

---

# 79. Complete production architecture — ECS Fargate

If I were designing a normal Spring Boot microservices application today:

```text
 Internet
 |
 v
 Route 53 / DNS
 |
 v
 WAF
 |
 v
 ALB
 |
 +---------------+---------------+
 | | |
 v v v
 ECS Service ECS Service ECS Service
 Order Payment User
 | | |
 Fargate Tasks Fargate Tasks Fargate Tasks
 | | |
 +---------------+---------------+
 |
 +---------------+---------------+
 | |
 v v
 RDS SQS/EventBridge
 | |
 v v
 Database Consumers
```

Cross-cutting:

```text
IAM
Secrets Manager
CloudWatch
ECR
VPC
Security Groups
NAT Gateway
```

---

# 80. Complete production architecture — EKS

```text
 Internet
 |
 WAF
 |
 ALB
 |
 EKS Ingress
 |
 +-------------+-------------+
 | | |
 v v v
 Order Pod Payment Pod User Pod
 | | |
 +-------------+-------------+
 |
 RDS
```

Infrastructure:

```text
EKS
 |
 +-- Control Plane
 |
 +-- Managed Node Group
 |
 +-- EC2
 | |
 | +-- Pods
 |
 +-- Autoscaling
```

---

# 81. Complete production architecture — Lambda

```text
 Internet
 |
 v
 API Gateway
 |
 v
 Lambda
 |
 +-------------+-------------+
 | |
 v v
 RDS SQS
 | |
 v v
 Database Lambda
```

Event-driven:

```text
S3
 |
 v
Lambda
 |
 v
Process
```

---

# 82. How services communicate with each other

You can remember this table:

| Requirement | Technology |
| ----------------- | -------------------------------------------------------------------------- |
| External HTTP API | ALB / API Gateway |
| Internal HTTP | ECS Service Connect / service discovery / internal LB / Kubernetes Service |
| Database | JDBC |
| Async messaging | SQS |
| Pub/sub | SNS |
| Event routing | EventBridge |
| Streaming | Kafka / Amazon MSK |
| Cache | ElastiCache |
| Files | S3 |
| Secrets | Secrets Manager |
| Configuration | Parameter Store |
| Logs | CloudWatch |
| Metrics | CloudWatch / Prometheus ecosystem |

---

# 83. A very important interview distinction

An interviewer might ask:

>**"Does EKS communicate with RDS differently from ECS?"**

The answer is:

>**The application-level communication is essentially the same.**

Spring Boot still uses:

```text
JDBC
 ↓
Connection pool
 ↓
Database endpoint
```

What changes is the infrastructure/networking layer:

```text
EKS Pod
 \
 → VPC → RDS

ECS Task
 \
 → VPC → RDS

Lambda
 \
 → VPC → RDS
```

---

# 84. Another important distinction

### Application configuration

```text
application.yml
```

### Container configuration

```text
Dockerfile
```

### Orchestration configuration

```text
EKS → Kubernetes YAML
ECS → Task Definition
Lambda → SAM/CDK/Terraform/etc.
```

### Infrastructure configuration

```text
Terraform
CloudFormation
CDK
```

Think:

```text
 Infrastructure
 |
 Terraform
 |
 +-----------+-----------+
 | | |
 EKS ECS Lambda
 | | |
 K8s Tasks Function
 | | |
 +-----------+-----------+
 |
 Spring Boot
```

---

# 85. Interview question: "How would you deploy Spring Boot to EKS?"

A strong answer:

> "First I package the Spring Boot application as a JAR and create a Docker image. I push that image to Amazon ECR. Then I create or use an EKS cluster with appropriate worker capacity. I define a Kubernetes Deployment with the ECR image and desired replicas, a Service for internal discovery, and an Ingress or AWS Load Balancer Controller configuration for external traffic. Configuration and secrets are injected through appropriate mechanisms such as ConfigMaps and Secrets, integrated with AWS secret-management solutions where required. I configure readiness, liveness and startup probes using Spring Boot Actuator endpoints. For production I add HPA, centralized logging, monitoring, IAM, network policies/security controls, rolling deployments and CI/CD."

---

# 86. Interview question: "How would you deploy Spring Boot to ECS Fargate?"

Answer:

> "I would Dockerize the Spring Boot application and push the image to ECR. Then I create an ECS cluster, define a Fargate task definition specifying the container image, CPU, memory, port and IAM roles, and create an ECS service with the desired task count. I would place the tasks in private subnets and put an Application Load Balancer in front of them. The ALB performs health checks against the application. Database connectivity would use RDS through private networking and security groups. Secrets would come from Secrets Manager or Parameter Store, and logs would be sent to CloudWatch. Finally, I'd configure autoscaling and a CI/CD pipeline."

---

# 87. Interview question: "Why Fargate instead of ECS EC2?"

Answer:

> "Fargate removes the need to provision, patch and manage EC2 worker instances. I specify CPU and memory for the task and AWS manages the underlying compute. This reduces operational overhead. ECS with EC2 can still be preferable when I need host-level control, specialized instance types, existing EC2 capacity, or specific cost/performance characteristics."

---

# 88. Interview question: "Why EKS instead of ECS?"

Answer:

> "I would choose EKS when Kubernetes itself provides meaningful value—for example, the organization already has strong Kubernetes expertise and platform standards, or we need Kubernetes-native tooling, operators, CRDs, portability or a mature Kubernetes ecosystem. If those requirements don't exist, ECS—especially Fargate—can be operationally simpler."

---

# 89. Interview question: "Why Lambda?"

Answer:

> "Lambda is appropriate when the workload naturally fits event-driven or request-triggered function execution, such as S3 processing, SQS consumers, EventBridge events or lightweight APIs. It eliminates server management and scales automatically, but I would consider execution limits, startup behavior, concurrency, database connection management and the suitability of Spring's startup characteristics before choosing it for a traditional Spring Boot application."

---

# 90. My architecture decision tree

Use this in interviews:

```text
 Spring Boot Application
 |
 v
 Is it event/function based?
 / \
 YES NO
 | |
 v v
 Lambda Need Kubernetes?
 / \
 YES NO
 | |
 v v
 EKS Need host-level
 EC2 control?
 / \
 YES NO
 | |
 v v
 ECS + EC2 ECS Fargate
```

This is a very easy way to remember it.

---

# 91. The "golden architecture" to remember

For most containerized Spring Boot APIs:

```text
 USER
 |
 v
 Route 53 / DNS
 |
 v
 WAF
 |
 v
 ALB
 |
 +---------+---------+
 | | |
 v v v
 Spring Spring Spring
 Boot Boot Boot
 Task/Pod Task/Pod Task/Pod
 | | |
 +---------+---------+
 |
 +--------+--------+
 | |
 v v
 RDS SQS/EventBridge
 | |
 v v
 Database Async services
```

Compute underneath:

```text
Spring Boot
 |
 +---- EKS
 |
 +---- ECS Fargate
 |
 +---- ECS EC2
 |
 +---- Lambda
```

---

# 92. Final cheat sheet

| Topic | Remember |
| --------------- | -------------------------------------------- |
| ECR | Stores Docker images |
| EKS | Managed Kubernetes |
| ECS | AWS container orchestration |
| Fargate | Serverless container compute |
| ECS EC2 | Containers run on your EC2 capacity |
| Lambda | Serverless function execution |
| ALB | HTTP/HTTPS load balancing |
| API Gateway | API front door, especially useful for Lambda |
| RDS | Relational database |
| S3 | Object storage |
| SQS | Queue |
| SNS | Pub/sub messaging |
| EventBridge | Event bus/routing |
| Secrets Manager | Secrets |
| Parameter Store | Configuration/parameters |
| CloudWatch | Logs/metrics/monitoring |
| IAM | AWS permissions |
| VPC | Network isolation |
| NAT Gateway | Private subnet outbound internet |
| Security Group | Network-level access control |
| Actuator | Spring Boot health/metrics endpoints |
| Terraform | Infrastructure as Code |

---

# 93. What I would say in a real Senior Architect interview

If asked:

>**"How would you deploy a Spring Boot microservices application on AWS?"**

I'd give this answer:

> "First, I would containerize each Spring Boot service and push the images to ECR. For a standard containerized workload without Kubernetes requirements, I would generally start with ECS Fargate because it provides container orchestration without requiring us to manage EC2 infrastructure. I would put the tasks in private subnets behind an Application Load Balancer, use IAM task roles for AWS service access, Secrets Manager for sensitive configuration, RDS for relational persistence, and CloudWatch for logs and metrics.
>
> If the organization already has a Kubernetes platform or requires Kubernetes-native capabilities, I would use EKS. Each service would be deployed as a Kubernetes Deployment, exposed internally through a Service, and externally through an Ingress/load balancer. I'd configure readiness, liveness and startup probes, HPA, rolling deployments and appropriate network/security controls.
>
> ECS with EC2 would be considered when we need more control over the underlying compute or have specific capacity and cost requirements. Lambda would be reserved for workloads that naturally fit an event-driven/function model rather than simply converting every Spring Boot application into Lambda.
>
> For service communication, synchronous calls could use REST or gRPC, while asynchronous workflows would use SQS, SNS, EventBridge or Kafka depending on the requirement. Database access would use JDBC and a connection pool, with private networking and security groups. For Lambda workloads using RDS, I would pay particular attention to connection scaling and consider RDS Proxy.
>
> Finally, I'd automate the infrastructure with Terraform, build and deploy through CI/CD, use immutable versioned artifacts, progressive deployments, monitoring, tracing, security scanning and automated rollback."

That answer demonstrates that you understand **not only how to deploy Spring Boot, but how to choose the right AWS architecture and operate it in production.**

---

# Additional Production-Standard Guidance

The complete supplied guide above is preserved. These sections add current production practices, clearer operating boundaries and senior-level design considerations for deploying Spring Boot across EKS, ECS and Lambda.

# 94. The deployment unit should be immutable

Build one tested artifact and promote that same artifact through environments:

```text
Source
 -> build and test
 -> Docker image
 -> ECR
 -> image digest
 -> dev -> test -> staging -> production
```

Prefer an image digest over a mutable tag at deployment time:

```text
order-service@sha256:<digest>
```

Tags such as `latest` are useful for human navigation but are unsafe as the only production identity because the same tag can point to different bytes later. Record the commit, image digest, build metadata, dependency report and deployment approval with every release.

## Build once, configure at runtime

The image should contain application code and runtime dependencies, not environment-specific credentials:

```text
Same image
 +--> development configuration
 +--> staging configuration
 `--> production configuration
```

This makes rollback meaningful. A rollback should select a previously tested immutable artifact, not rebuild code with today's dependencies.

# 95. Container image hardening

A production Docker image should be:

- Based on a supported minimal runtime image.
- Run as a non-root user where possible.
- Free from compilers, shells and build tools that are not needed at runtime.
- Scanned for operating-system and application vulnerabilities.
- Built with a generated SBOM.
- Pinned or controlled through a base-image update process.
- Signed or attested when the organization's supply-chain policy requires it.
- Sent to ECR with tag immutability and lifecycle rules.

Example:

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

RUN useradd --system --uid 10001 spring

COPY --chown=spring:spring target/order-service.jar app.jar

USER 10001

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
```

The exact base image and Java flags should be tested against the chosen runtime and workload. Do not add flags from a blog post without measuring startup, memory and garbage collection.

# 96. Spring Boot Actuator: health is not the same as readiness

Expose health endpoints carefully. A process can be alive while it is not ready to receive traffic.

```text
Liveness -> Is the process making progress?
Readiness -> Should the load balancer send traffic here?
Startup -> Has initial application startup completed?
```

For Kubernetes or a load balancer:

```text
Startup fails -> allow more startup time
Readiness fails -> remove from traffic, keep process for recovery
Liveness fails -> restart after a genuine stuck condition
```

Do not put every dependency in liveness. If RDS is temporarily unavailable, restarting every pod can create a restart storm. Dependency health is usually more appropriate for readiness or a separate diagnostic endpoint.

Protect actuator endpoints:

- Expose only required endpoints.
- Keep them on an internal management path or port where appropriate.
- Require authentication for detailed information.
- Do not expose environment, heap dump or thread dump endpoints publicly.
- Avoid returning secrets or connection details in health responses.

# 97. ECS task roles versus execution roles

These two IAM roles have different responsibilities:

```text
ECS execution role
 -> pull image from ECR
 -> write logs to CloudWatch
 -> retrieve referenced startup secrets where configured

ECS task role
 -> permissions used by the running Spring Boot application
 -> SQS, S3, DynamoDB or other AWS API access
```

The application should use the task role through the AWS SDK default credential provider chain. Do not embed access keys in the image or inject long-lived access keys as environment variables.

Example policy intent:

```text
Order task role
 -> sqs:SendMessage on the order-events queue only
 -> dynamodb:PutItem on the orders table only

Worker task role
 -> sqs:ReceiveMessage/DeleteMessage on the worker queue only
 -> dynamodb:UpdateItem on the payment table only
```

Separate roles make accidental cross-service access easier to detect and limit.

# 98. ECS Fargate production design

A robust ECS service normally includes:

```text
ECS cluster
 -> private-subnet service
 -> task definition
 -> container
 -> port 8080
 -> target group
 -> ALB listener and rules
 -> autoscaling policy
 -> task/execution roles
 -> CloudWatch log group
```

Review these settings together:

- CPU and memory reservation.
- Deployment minimum and maximum healthy percentages.
- Health-check grace period.
- Container stop timeout and graceful shutdown.
- Log group and retention.
- Secret references.
- Security groups and subnet placement.
- Desired count and autoscaling cooldowns.
- Deployment circuit breaker and rollback.

Enable a deployment circuit breaker where appropriate so an unhealthy release does not remain active indefinitely. Confirm that the application handles `SIGTERM` and stops accepting new work before the container exits.

## ECS capacity providers

Fargate capacity providers can express a preference between regular Fargate and Fargate Spot. Spot capacity can reduce cost but can be interrupted, so use it only for workloads that tolerate interruption:

```text
On-demand Fargate -> baseline critical API capacity
Fargate Spot -> interruptible workers or background processing
```

Do not place all replicas of a critical synchronous API on interruptible capacity.

# 99. ECS on EC2 production design

ECS EC2 introduces a second scaling problem:

```text
Traffic
 -> ECS service desired tasks
 -> available cluster capacity
 -> EC2 Auto Scaling Group
```

The service can want ten tasks while the cluster has room for only six. Plan:

- Capacity provider strategy.
- EC2 instance type and architecture.
- AMI patching and replacement.
- Container instance draining.
- Bin packing and task placement.
- Spot interruption handling.
- Cluster scale-out time.
- Host monitoring and security.

ECS EC2 is not simply “Fargate with cheaper servers.” It gives control and may improve economics at steady high utilization, but it adds host lifecycle and capacity-management work.

# 100. EKS production baseline

For a production EKS platform, clarify ownership of:

```text
AWS-managed control plane
 |
 +-- managed node groups or Fargate profiles
 +-- AWS Load Balancer Controller
 +-- IAM integration for pods
 +-- cluster autoscaling/Karpenter strategy
 +-- observability agents
 +-- network and policy controls
 +-- upgrade and add-on lifecycle
```

Use namespaces, resource requests and limits, Pod Disruption Budgets, topology spreading and controlled node groups. A Deployment with three replicas is not highly available if all pods land on one failure domain or one node.

## Kubernetes resource requests and limits

Requests influence scheduling; limits cap usage:

```yaml
resources:
 requests:
 cpu: "250m"
 memory: "512Mi"
 limits:
 cpu: "1000m"
 memory: "768Mi"
```

Set them from measurements. A request that is too low causes contention and poor scheduling decisions. A limit that is too low causes throttling or out-of-memory restarts.

## EKS identity for pods

Use an AWS-supported pod identity mechanism such as EKS Pod Identity or IAM roles for service accounts according to the platform standard. Map a Kubernetes service account to a narrowly scoped IAM role:

```text
Spring Boot pod
 -> Kubernetes service account
 -> IAM role
 -> permitted AWS resources
```

Do not give every pod the node instance role. A node role shared by unrelated workloads breaks workload-level least privilege.

# 101. Kubernetes autoscaling is multi-dimensional

HPA scales pods, but pods need node capacity. A complete scaling design may include:

```text
Request rate/CPU/custom metric
 -> HPA -> more pods
 |
 v
 no node capacity?
 |
 v
 node autoscaler
```

Choose metrics based on the workload:

- CPU and memory for compute-bound services.
- Requests per second for HTTP APIs.
- Queue age or queue depth for workers.
- In-flight requests for concurrency-limited services.
- Business throughput for asynchronous processing.

Autoscaling is not a substitute for capacity planning. Check database connections, downstream rate limits, pod startup time and scale-in behaviour.

# 102. Graceful shutdown and connection draining

When a task or pod is terminated:

```text
Stop new traffic
 -> finish or cancel in-flight requests
 -> stop message intake
 -> close clients and pools
 -> exit before termination deadline
```

Spring Boot should be configured and tested for graceful shutdown. The load balancer needs enough deregistration delay for active requests, while the application needs a timeout shorter than the platform termination window.

For message consumers, stop polling first, then finish bounded work. Do not acknowledge a message before the business operation has reached a durable outcome.

# 103. Resilience between Spring Boot services

Use explicit timeouts for every network call:

```text
connect timeout
read/response timeout
total operation deadline
```

Then choose retry behaviour by failure type:

```text
Transient timeout -> bounded retry with jitter
Validation error -> no retry
Authentication failure -> no blind retry
Rate limit -> backoff and respect provider guidance
Unknown failure -> record, classify and fail safely
```

Use circuit breakers, bulkheads and concurrency limits when one dependency can exhaust all application threads.

```text
Order API
 +-- payment bulkhead
 +-- inventory bulkhead
 `-- notification queue
```

Never combine aggressive client retries, load-balancer retries, SDK retries and queue retries without calculating the total amplification.

# 104. Database connection capacity

The database sees connections from all replicas:

```text
EKS: 20 pods x 10 connections = 200
ECS Fargate: 10 tasks x 10 connections = 100
ECS EC2: 10 tasks x 10 connections = 100
Lambda: concurrent environments x connections
```

The pool size is not a target to maximize. It is a capacity allocation that must fit database CPU, query latency and connection limits. Use RDS Proxy for suitable Lambda patterns and consider it for connection-heavy container workloads. Tune pool size, request concurrency and database capacity together.

# 105. Lambda and Spring Boot production choices

For Lambda, distinguish these shapes:

```text
Small Java handler
 -> low initialization and focused responsibility

Spring Cloud Function/adapter
 -> convenient function model, framework startup cost

Full traditional Spring application
 -> often better suited to ECS/EKS when always running
```

For latency-sensitive Java Lambda functions, benchmark:

- Memory size, which also affects CPU allocation.
- Dependency and Spring context startup.
- Provisioned Concurrency.
- SnapStart compatibility and initialization state.
- VPC networking path.
- RDS Proxy or a non-relational persistence option.

Do not choose Lambda because it is fashionable. Choose it when its event model, scaling and operations are a better fit than a continuously running container.

# 106. API and event boundary choices

```text
Public synchronous HTTP
 -> API Gateway or ALB

Private synchronous service call
 -> internal ALB, ECS Service Connect, Cloud Map or Kubernetes Service

Durable asynchronous work
 -> SQS

Fan-out notification
 -> SNS or EventBridge plus targets

Long-running stateful workflow
 -> Step Functions or a workflow platform
```

Avoid creating long synchronous chains:

```text
API -> order -> payment -> inventory -> email
```

One slow dependency then consumes the caller's timeout budget. Persist the accepted state and move independent work to a durable asynchronous boundary.

# 107. Networking decision details

A private subnet is not automatically secure, and a public subnet is not automatically unsafe. The design must include routing, security groups, network ACLs, identity and application authorization.

For private Spring Boot workloads, confirm:

- ALB or API Gateway can reach application targets.
- Application can resolve private DNS names.
- Application can reach RDS or other private services.
- Application can reach AWS APIs through NAT or VPC endpoints.
- NAT gateways have capacity and cost controls.
- Security groups permit only required source/destination ports.
- Egress is controlled where policy requires it.

Use interface or gateway VPC endpoints for supported AWS services when they reduce NAT dependence or satisfy private-connectivity requirements.

# 108. Deployment safety and database migrations

Use progressive delivery:

```text
Build artifact
 -> automated tests
 -> security checks
 -> deploy to staging
 -> smoke and contract tests
 -> small production slice
 -> monitor technical and business metrics
 -> increase traffic or roll back
```

For EKS, this can use rolling, canary or blue/green tooling. For ECS, use rolling or blue/green deployment strategies. For Lambda, use versions and aliases with weighted traffic.

Database changes should be backward compatible:

```text
1. Add nullable/new structure
2. Deploy code that can read old and new forms
3. Backfill or dual-write if needed
4. Switch reads/writes
5. Remove old structure later
```

Never couple an irreversible schema change to a deployment that may need rollback.

# 109. Observability baseline

Every deployment model should expose the same application signals:

```text
Logs
 -> structured JSON to a central destination

Metrics
 -> request count, error rate, latency, saturation

Traces
 -> cross-service and database timing

Business signals
 -> orders, payments, messages, failures
```

Useful dimensions include service, environment, version, operation and outcome. Avoid putting an unbounded customer ID or request ID into metric dimensions; keep those in logs and trace attributes.

For a rollout, compare the new version with the old version using:

- HTTP 4xx/5xx rates.
- p95/p99 latency.
- JVM heap and garbage collection.
- CPU throttling and memory pressure.
- Container restarts or Lambda throttles.
- Database latency and connections.
- Queue age and DLQ count.
- Business success rate.

# 110. Cost comparison should include operations

Compare the complete system, not only compute price:

```text
Compute
+ load balancer/API Gateway
+ NAT gateways and data transfer
+ ECR storage and scanning
+ RDS/cache and connections
+ CloudWatch logs, metrics and traces
+ control-plane/add-on/platform operations
+ engineering and on-call effort
```

Typical tendencies:

| Workload | Likely fit | Main cost/operations question |
| --- | --- | --- |
| Small bursty event handler | Lambda | Duration, concurrency and downstream access |
| Standard always-on API | ECS Fargate | Task count, ALB and steady utilization |
| Predictable high utilization | ECS EC2 | Host management versus capacity savings |
| Kubernetes platform workload | EKS | Platform team and add-on operating cost |

Benchmark with realistic traffic, payloads, startup behaviour and dependency limits before making a cost claim.

# 111. Troubleshooting by deployment model

## EKS pod is not receiving traffic

```text
Check pod phase and events
 -> readiness probe
 -> Service selector/endpoints
 -> Ingress/Load Balancer Controller
 -> target health
 -> security groups and network policy
```

## ECS task repeatedly stops

```text
Check stopped-task reason
 -> image pull and execution role
 -> memory/CPU exit
 -> container health check
 -> secret retrieval
 -> application startup logs
 -> target-group health
```

## Lambda is slow or throttled

```text
Check cold-start and duration metrics
 -> memory/CPU setting
 -> concurrency and reserved limits
 -> downstream/database connections
 -> VPC path and DNS
 -> retries and event-source backlog
```

## All deployment models show elevated latency

Check the shared path first:

```text
RDS/database
External API
DNS/network
Connection pool
Thread pool
Queue backlog
Recent deployment
```

The compute platform is not always the root cause.

# 112. Testing strategy for deployment

### Application tests

- Unit and component tests for business logic.
- Integration tests for databases and AWS APIs.
- Contract tests for service and event interfaces.
- Security tests for authorization and secret handling.

### Container tests

- Start the image and verify the health endpoint.
- Confirm non-root execution.
- Verify graceful shutdown.
- Test configuration injection without production secrets.

### Platform tests

- EKS probes, rolling update and pod disruption behaviour.
- ECS task replacement, ALB health checks and deployment rollback.
- Lambda event retries, duplicate delivery and timeout handling.
- Network access from private subnets.
- IAM allow and deny cases.

### Production-readiness tests

- Load and soak tests.
- Dependency throttling and timeout tests.
- Database connection exhaustion tests.
- Failure of one availability zone or task/node.
- DLQ and replay tests.
- Observability and alarm notification tests.

# 113. Architectural trade-off matrix

| Dimension | EKS | ECS Fargate | ECS EC2 | Lambda |
| --- | --- | --- | --- | --- |
| Abstraction | Kubernetes | AWS container service | AWS container service | Function runtime |
| Host management | Platform/node responsibility | AWS | Customer | AWS |
| Portability | High within Kubernetes | AWS-specific orchestration | AWS-specific orchestration | Function/event model |
| Startup model | Long-running pods | Long-running tasks | Long-running tasks | Invocation environments |
| Scaling unit | Pod and node | Task | Task and EC2 capacity | Concurrent invocation |
| Best fit | Kubernetes platform | Standard containers | Controlled/high-utilization containers | Event-driven short work |
| Main risk | Platform complexity | Cost at sustained scale | Host/capacity operations | Limits, retries and cold starts |

The table is a starting point, not a universal ranking. Existing organizational capability and operational ownership are architecture inputs.

# 114. Final senior architect checklist

### Application

- Container or function boundary matches the workload.
- Configuration is externalized and secrets are managed.
- Timeouts, retries, circuit breakers and graceful shutdown are defined.
- Database migrations are backward compatible.
- Health endpoints distinguish liveness and readiness.
- Logs, metrics, traces and business signals are available.

### EKS

- Pods have requests, limits, probes and disruption policy.
- Identity is assigned per workload, not through a shared node role.
- Service, ingress and target health are tested.
- HPA and node capacity scale together.
- Cluster upgrades and add-ons have owners.

### ECS

- Task role and execution role are separate.
- Fargate or EC2 capacity choice is justified.
- Task definition, deployment circuit breaker and health grace period are correct.
- ALB, security groups, logs and secrets are configured.
- EC2 capacity and host patching are owned when ECS EC2 is used.

### Lambda

- Invocation model, retries and duplicate delivery are understood.
- Cold start and memory/CPU behaviour are measured.
- Concurrency protects downstream systems.
- RDS Proxy or another persistence strategy addresses connection scaling.
- Versions, aliases and rollback are automated.

### Platform and delivery

- Infrastructure is defined as code.
- Images are immutable, scanned and promoted by digest.
- IAM is least privilege.
- Private networking and egress are intentional.
- Progressive delivery and automated rollback are tested.
- Recovery objectives, backups and runbooks are documented.

# 115. Concise interview conclusion

> I would choose the compute model from workload shape, platform capability and operational ownership. ECS Fargate is a strong default for a normal always-on Spring Boot container when Kubernetes is not required. EKS is justified when Kubernetes capabilities, standards or ecosystem value are important. ECS on EC2 is appropriate when host control, specialized capacity or steady utilization outweighs the additional operations. Lambda is best for event-driven or short-lived functions and should not be selected merely because the existing application is written in Spring Boot. In every model I would use immutable artifacts, least-privilege identity, private networking, externalized secrets, correct health and shutdown behaviour, bounded retries, protected database connections, observability, progressive delivery and tested rollback.