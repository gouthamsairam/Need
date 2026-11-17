PART C: KUBERNETES (K8s)

Section 5: Kubernetes Fundamentals
Q17. What is Kubernetes and why do we need it when we already have Docker?
Expected Answer:Docker runs containers on a single machine. Kubernetes orchestrates containers across multiple machines at scale.What Docker alone can't do well:
Run 50 containers across 10 servers and keep them healthy.
Automatically restart crashed containers.
Scale from 3 to 20 instances based on CPU load.
Route traffic only to healthy instances.
Perform zero-downtime rolling updates.
Manage secrets, config, and storage across a cluster.
Kubernetes provides:
Feature
What It Does
Self-healing
Restarts failed containers, replaces unresponsive nodes
Auto-scaling
Scales pods up/down based on CPU, memory, or custom metrics
Service discovery
Services find each other by name (DNS)
Load balancing
Distributes traffic across pod replicas
Rolling updates
Zero-downtime deployments
Rollback
Instantly revert to a previous version
Config & secrets
Inject configuration without rebuilding images
Storage orchestration
Attach persistent volumes to stateful workloads

Analogy: Docker is like having individual trucks (containers). Kubernetes is the fleet management system — routing trucks, replacing broken ones, adding more trucks during peak hours.
Q18. Explain the key Kubernetes components/objects.
Expected Answer:
┌────────────────────── Kubernetes Cluster ──────────────────────┐
│                                                                │
│  Control Plane (Master)                                        │
│  ├── API Server      ← All communication goes through here    │
│  ├── etcd            ← Key-value store (cluster state)         │
│  ├── Scheduler       ← Decides which node runs a pod           │
│  └── Controller Manager ← Ensures desired state = actual state│
│                                                                │
│  Worker Nodes                                                  │
│  ├── Node 1                                                    │
│  │   ├── kubelet          ← Agent that manages pods on node    │
│  │   ├── kube-proxy       ← Handles networking/load balancing  │
│  │   ├── Pod (order-svc)  ← Smallest deployable unit           │
│  │   └── Pod (payment-svc)                                     │
│  ├── Node 2                                                    │
│  │   ├── Pod (order-svc)                                       │
│  │   └── Pod (order-svc)                                       │
│  └── Node 3                                                    │
│      └── Pod (payment-svc)                                     │
└────────────────────────────────────────────────────────────────┘
Object
What It Is
Pod
Smallest unit — one or more containers sharing network/storage
Deployment
Manages pods — handles replicas, rolling updates, rollback
Service
Stable network endpoint for a set of pods (DNS name + load balancing)
Ingress
HTTP routing from external traffic to internal services
ConfigMap
Non-sensitive configuration (key-value pairs)
Secret
Sensitive data (passwords, tokens) — base64 encoded
Namespace
Logical isolation within a cluster (dev, staging, prod)
HPA
Horizontal Pod Autoscaler — scales pods based on metrics
PersistentVolume
Storage that outlives pods


Section 6: Deploying Spring Boot to Kubernetes
Q19. Write the Kubernetes manifests to deploy a Spring Boot application.
Expected Answer:1. Deployment (manages pods):
apiVersion: apps/v1
kind: Deployment
metadata:
name: order-service
namespace: production
labels:
app: order-service
spec:
replicas: 3
selector:
matchLabels:
app: order-service
strategy:
type: RollingUpdate
rollingUpdate:
maxSurge: 1
maxUnavailable: 0
template:
metadata:
labels:
app: order-service
spec:
containers:
- name: order-service
image: 123456789.dkr.ecr.us-east-1.amazonaws.com/order-service:1.0.0
ports:
- containerPort: 8080
env:
- name: SPRING_PROFILES_ACTIVE
value: "prod"
- name: DB_PASSWORD
valueFrom:
secretKeyRef:
name: order-db-secret
key: password
envFrom:
- configMapRef:
name: order-config
resources:
requests:
cpu: "250m"
memory: "512Mi"
limits:
cpu: "500m"
memory: "1Gi"
readinessProbe:
httpGet:
path: /actuator/health/readiness
port: 8080
initialDelaySeconds: 20
periodSeconds: 10
livenessProbe:
httpGet:
path: /actuator/health/liveness
port: 8080
initialDelaySeconds: 30
periodSeconds: 15
startupProbe:
httpGet:
path: /actuator/health
port: 8080
failureThreshold: 30
periodSeconds: 5
2. Service (stable network endpoint):
   apiVersion: v1
   kind: Service
   metadata:
   name: order-service
   namespace: production
   spec:
   selector:
   app: order-service
   ports:
    - port: 80
      targetPort: 8080
      type: ClusterIP
3. ConfigMap:
   apiVersion: v1
   kind: ConfigMap
   metadata:
   name: order-config
   namespace: production
   data:
   DB_HOST: "prod-db.cluster-xyz.us-east-1.rds.amazonaws.com"
   DB_PORT: "5432"
   KAFKA_BOOTSTRAP_SERVERS: "kafka-broker-1:9092,kafka-broker-2:9092"
4. Secret:
   apiVersion: v1
   kind: Secret
   metadata:
   name: order-db-secret
   namespace: production
   type: Opaque
   data:
   password: c3VwZXItc2VjcmV0  # base64 encoded
5. Ingress (external access):
   apiVersion: networking.k8s.io/v1
   kind: Ingress
   metadata:
   name: api-ingress
   namespace: production
   annotations:
   nginx.ingress.kubernetes.io/rewrite-target: /
   spec:
   ingressClassName: nginx
   rules:
    - host: api.mycompany.com
      http:
      paths:
      - path: /orders
      pathType: Prefix
      backend:
      service:
      name: order-service
      port:
      number: 80
      - path: /payments
      pathType: Prefix
      backend:
      service:
      name: payment-service
      port:
      number: 80
      Deploy:
      kubectl apply -f k8s/

Q20. Explain the three types of Kubernetes probes and why they matter for Spring Boot.
Expected Answer:
Probe
Question It Answers
Action on Failure
Startup Probe
"Has the app finished starting?"
Keep waiting (don't kill it yet)
Liveness Probe
"Is the app alive and not stuck?"
Kill and restart the pod
Readiness Probe
"Is the app ready to accept traffic?"
Remove from load balancer (don't send traffic)

Why all three matter for Spring Boot:Spring Boot apps can take 30-60 seconds to start (loading context, connecting to DB, warming caches). Without a startup probe, the liveness probe might kill the pod before it finishes starting.
Timeline:
0s ────── 30s ──────── 45s ──────── 60s ─────►
starting...   context loaded   ready!
Startup probe:  ✗  ✗  ✗  ✗  ✓  (stops checking)
Liveness probe:                 ✓  ✓  ✓  ✓  (takes over)
Readiness probe:                   ✓  ✓  ✓  (traffic flows)
Spring Boot Actuator health groups:
management:
endpoint:
health:
probes:
enabled: true
health:
livenessstate:
enabled: true
readinessstate:
enabled: true
/actuator/health/liveness — Application is alive
/actuator/health/readiness — Application is ready (DB connected, caches warm)

Q21. How does Kubernetes auto-scaling work for Spring Boot?
Expected Answer:Horizontal Pod Autoscaler (HPA):
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
name: order-service-hpa
spec:
scaleTargetRef:
apiVersion: apps/v1
kind: Deployment
name: order-service
minReplicas: 2
maxReplicas: 20
metrics:
- type: Resource
resource:
name: cpu
target:
type: Utilization
averageUtilization: 70
- type: Resource
resource:
name: memory
target:
type: Utilization
averageUtilization: 80
How it works:
1. Metrics Server collects CPU/memory from all pods every 15s
2. HPA checks: average CPU across all order-service pods
3. If avg CPU > 70% → scale up (add pods)
4. If avg CPU < 70% for 5 minutes → scale down (remove pods)
5. Respects minReplicas (2) and maxReplicas (20)
   Scaling formula:
   desiredReplicas = ceil(currentReplicas × (currentMetric / targetMetric))
   Example: 3 pods at 90% CPU, target 70%
   desiredReplicas = ceil(3 × (90/70)) = ceil(3.86) = 4 pods
   Custom metrics scaling (e.g., Kafka consumer lag):
   metrics:
- type: Pods
  pods:
  metric:
  name: kafka_consumer_lag
  target:
  type: AverageValue
  averageValue: 1000
  Scale based on how many unprocessed Kafka messages are waiting — very useful for event-driven Spring Boot services.
  Section 7: Real-World Scenario Questions
  Q22. Scenario: Your Spring Boot app runs fine locally but crashes in Kubernetes with OOMKilled. What's happening?
  Expected Answer:OOMKilled means the container exceeded its memory limit and Kubernetes killed it.Root cause: The JVM doesn't automatically respect container memory limits (in older JDKs). Even in modern JDKs, the default heap can exceed the container limit.Fix:
  resources:
  limits:
  memory: "1Gi"
  ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
  Rule of thumb:
  Container limit: 1Gi
  JVM heap (MaxRAMPercentage=75%): ~768MB
  Remaining 25%: Metaspace, thread stacks, native memory, OS overhead
  Debugging:
  kubectl describe pod order-service-xyz    # Shows OOMKilled reason
  kubectl top pod order-service-xyz         # Shows current CPU/memory usage
  kubectl logs order-service-xyz --previous # Logs from the crashed pod

Q23. Scenario: You need zero-downtime deployment for your Spring Boot microservice. How do you achieve it?
Expected Answer:1. Rolling Update Strategy:
strategy:
type: RollingUpdate
rollingUpdate:
maxSurge: 1        # Add 1 new pod at a time
maxUnavailable: 0  # Never remove a pod until new one is ready
2. Readiness probe: Kubernetes won't send traffic to a pod until it's ready.3. Graceful shutdown in Spring Boot:
   server:
   shutdown: graceful
   spring:
   lifecycle:
   timeout-per-shutdown-phase: 30s
   This ensures in-flight requests complete before the old pod is killed.4. Pre-stop hook (gives time for load balancer to drain):
   lifecycle:
   preStop:
   exec:
   command: ["sh", "-c", "sleep 10"]
   Full flow during a rolling update:
1. New pod (v2) starts
2. Startup probe passes
3. Readiness probe passes → traffic starts going to v2
4. Old pod (v1) receives SIGTERM
5. preStop hook: sleep 10s (LB stops sending new requests)
6. Graceful shutdown: finish in-flight requests (up to 30s)
7. Old pod terminates
8. Repeat for next pod

Q24. Scenario: You have 10 Spring Boot microservices. How do you structure the Kubernetes deployment for production?
Expected Answer:
Namespace: production
├── order-service     (Deployment + Service + HPA)
├── payment-service   (Deployment + Service + HPA)
├── user-service      (Deployment + Service + HPA)
├── notification-svc  (Deployment + Service + HPA)
├── api-gateway       (Deployment + Service + Ingress)
├── config-server     (Deployment + Service)
├── kafka             (StatefulSet + Service)
├── redis             (StatefulSet + Service)
├── postgres          (managed via RDS, not in K8s)
└── monitoring        (Prometheus + Grafana)
Namespace: staging
└── (mirrors production with fewer replicas)
Namespace: monitoring
├── Prometheus
├── Grafana
├── Loki
└── Tempo (tracing)
Key decisions:
Stateless services → Deployment (easy to scale, replace)
Stateful services (Kafka, Redis) → StatefulSet (stable identity, persistent storage) or managed services (Amazon MSK, ElastiCache)
Databases → Use managed services (RDS, Aurora). Don't run production databases in K8s unless you have strong K8s expertise.
One namespace per environment — Isolates resources, RBAC, quotas.

Q25. Scenario: How do you debug a Spring Boot pod that keeps restarting in Kubernetes?
Expected Answer:Step 1: Check pod status and events
kubectl get pods -n production
kubectl describe pod order-service-abc123 -n production
Look for:
OOMKilled → Memory issue (increase limits, tune JVM)
CrashLoopBackOff → App keeps crashing on startup
Liveness probe failed → App started but became unresponsive
Step 2: Check logs
kubectl logs order-service-abc123 -n production           # Current logs
kubectl logs order-service-abc123 -n production --previous # Logs from crashed instance
Look for: OutOfMemoryError, Connection refused (DB not reachable), missing config properties.Step 3: Check resource usage
kubectl top pod order-service-abc123
Step 4: Exec into the pod (if it's running)
kubectl exec -it order-service-abc123 -- /bin/sh
curl localhost:8080/actuator/health
Step 5: Common fixes
Symptom
Cause
Fix
OOMKilled
JVM exceeds container memory
Set -XX:MaxRAMPercentage=75.0
CrashLoopBackOff
App fails on startup
Check logs — missing DB, bad config
Liveness probe fail
App hangs (deadlock, GC pause)
Increase probe timeout, tune JVM GC
Readiness probe fail
DB or dependency not ready
Use depends_on or init containers


Q26. How do you handle database migrations in a Kubernetes/Docker deployment?
Expected Answer:Use Flyway or Liquibase — they run migrations automatically on startup.Flyway with Spring Boot:
<dependency>
<groupId>org.flywaydb</groupId>
<artifactId>flyway-core</artifactId>
</dependency>
spring:
flyway:
enabled: true
locations: classpath:db/migration
Migration files: src/main/resources/db/migration/V1__create_orders_table.sqlProblem with multiple pods:If 3 pods start simultaneously, all 3 try to run migrations. Flyway handles this with a lock table (flyway_schema_history) — only one instance runs migrations, others wait.Alternative — Init Container:Run migrations as an init container that completes before the app starts:
initContainers:
- name: db-migration
  image: flyway/flyway:10
  args: ["migrate"]
  env:
    - name: FLYWAY_URL
      value: "jdbc:postgresql://db:5432/orders"
    - name: FLYWAY_USER
      valueFrom:
      secretKeyRef:
      name: db-secret
      key: username
      This guarantees migrations complete before any app pod starts.
      Section 8: Full Architecture — Putting It All Together
      Q27. Draw and explain a complete production architecture for Spring Boot microservices on AWS with Kubernetes.
      Expected Answer:
      Internet
      │
      ▼
      [Route 53 (DNS)]
      │
      ▼
      [AWS WAF + CloudFront (CDN)]
      │
      ▼
      [Application Load Balancer]
      │
      ▼
      [EKS Cluster]
      ├── Ingress Controller (NGINX)
      │      │
      │      ├── /api/orders  → order-service (3 pods + HPA)
      │      ├── /api/users   → user-service (2 pods + HPA)
      │      └── /api/payments→ payment-service (3 pods + HPA)
      │
      ├── Internal Services
      │      ├── config-server (2 pods)
      │      └── service-mesh (Istio sidecars)
      │
      └── Monitoring (separate namespace)
      ├── Prometheus + Grafana
      ├── Loki (logs)
      └── Tempo (tracing)
      External Managed Services:
      ├── RDS Aurora (PostgreSQL) — multi-AZ
      ├── ElastiCache (Redis) — caching + sessions
      ├── Amazon MSK (Kafka) — event streaming
      ├── S3 — file storage
      ├── Secrets Manager — credentials
      ├── ECR — Docker images
      └── CloudWatch — alarms + log backup
      Key design decisions:
      EKS for container orchestration (managed Kubernetes).
      Managed databases (RDS, ElastiCache, MSK) — don't run stateful services in K8s.
      Multi-AZ for high availability.
      HPA for auto-scaling each service independently.
      Istio service mesh for mTLS, observability, traffic management.
      GitOps (ArgoCD) for declarative deployments — every change goes through Git.

Q28. What is GitOps and how does it apply to Spring Boot Kubernetes deployments?
Expected Answer:GitOps treats Git as the single source of truth for infrastructure and application deployments. Any change to the cluster goes through a Git commit.
Developer pushes code
↓
CI Pipeline (GitHub Actions)
├── Build JAR
├── Build Docker image
├── Push to ECR
└── Update image tag in deployment YAML → Git commit
ArgoCD (running in K8s)
├── Watches the Git repo
├── Detects YAML changed
└── Automatically applies to K8s cluster
Benefits:
Audit trail — Every deployment is a Git commit.
Rollback — Revert a Git commit to roll back a deployment.
Consistency — Cluster state always matches what's in Git.
No kubectl apply from laptops — All changes go through Git PR review.

Q29. What is Helm and why use it for Spring Boot Kubernetes deployments?
Expected Answer:Helm is a package manager for Kubernetes — like Maven for Java. It templates and bundles Kubernetes manifests into reusable charts.Without Helm: Each microservice needs ~5 YAML files × 15 services = 75 files, mostly repetitive.With Helm: One chart template + per-service values.yaml:
charts/spring-boot-service/
├── Chart.yaml
├── templates/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── hpa.yaml
│   ├── ingress.yaml
│   └── configmap.yaml
└── values.yaml
templates/deployment.yaml (template):
apiVersion: apps/v1
kind: Deployment
metadata:
name: {{ .Values.name }}
spec:
replicas: {{ .Values.replicas }}
template:
spec:
containers:
- name: {{ .Values.name }}
image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
resources:
limits:
memory: {{ .Values.resources.memory }}
values-order-service.yaml:
name: order-service
replicas: 3
image:
repository: 123456.dkr.ecr.us-east-1.amazonaws.com/order-service
tag: "1.0.0"
resources:
memory: "1Gi"
Deploy:
helm install order-service ./charts/spring-boot-service -f values-order-service.yaml
helm upgrade order-service ./charts/spring-boot-service -f values-order-service.yaml
helm rollback order-service 1  # rollback to revision 1

Q30. Compare: When do you use ECS Fargate vs EKS for Spring Boot microservices?
Expected Answer:
Aspect
ECS Fargate
EKS
Complexity
Simpler — AWS-native, fewer concepts
Complex — full Kubernetes learning curve
Vendor lock-in
AWS-specific
Portable — runs on any cloud or on-prem
Team size
Small teams (< 10 devs)
Larger teams with K8s expertise
Ecosystem
Limited to AWS tools
Huge K8s ecosystem (Helm, Istio, ArgoCD, etc.)
Cost
Pay per vCPU/memory used
EKS control plane cost + node costs
Service mesh
AWS App Mesh (limited)
Istio, Linkerd (rich features)
Auto-scaling
Simple — set target tracking
HPA, VPA, Cluster Autoscaler, KEDA
Best for
5-15 services, AWS-only shop
15+ services, multi-cloud, or K8s-native team

Rule of thumb:
Start with ECS Fargate — simpler, faster to get running.
Move to EKS when you need portability, service mesh, advanced scaling, or have K8s expertise.
