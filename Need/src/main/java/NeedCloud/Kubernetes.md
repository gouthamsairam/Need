# Part C: Kubernetes (K8s)

## Section 5: Kubernetes Fundamentals

### Q17: What is Kubernetes, and why do we need it when we already have Docker?

#### In simple terms

Docker starts and runs containers, usually on one machine. Kubernetes decides which machines should run which containers, keeps the number you asked for running, replaces containers that fail, and sends traffic only to containers that are ready. You describe the desired state, such as "three copies of order-service at version 1.4," and Kubernetes keeps working to make reality match that description. The notes' fleet analogy fits well. Docker is the truck, and Kubernetes is the dispatcher who assigns loads, replaces broken trucks, and adds more during rush hour.

#### Background: how it was before

Google ran an internal cluster manager called Borg for more than a decade. The Borg design introduced ideas such as declarative job specifications, scheduling across machines, and continuous reconciliation, which Kubernetes carried forward. Google open-sourced Kubernetes in 2014, released version 1.0 in 2015, and donated it to the Cloud Native Computing Foundation in 2016. Before Kubernetes, teams used shell scripts that logged into servers and restarted processes, configuration management tools such as Ansible and Chef to keep machines consistent, or platforms such as Cloud Foundry and Mesos with Marathon. Docker Swarm added clustering to Docker itself. Each approach solved part of the problem, and Kubernetes gained wide adoption because its API was open, consistent, and supported by many vendors.

A correction to the notes: Docker Swarm can run containers across several machines, so "Docker alone can't do it" is too strong. The more accurate statement is that Docker does not provide a broad, standard set of answers for scheduling, self-healing, and service discovery across a fleet. The notes also attribute CPU-based scaling to Kubernetes in general, when it is specifically the Horizontal Pod Autoscaler. Zero-downtime rolling updates depend on readiness probes, because Kubernetes cannot know a pod is ready unless you tell it.

#### How it works internally

Every operation passes through the API server, which authenticates the request, checks authorization with role-based access control, runs admission controllers that can validate or modify objects, and stores the result in etcd, a consistent key-value store. Controllers run reconciliation loops. A controller watches the objects it owns, compares desired state with actual state, and acts on the difference. A Deployment controller creates ReplicaSets, and ReplicaSets create pods. The scheduler assigns unplaced pods to nodes by filtering out nodes that cannot run them and scoring the rest. On each node, the kubelet asks the container runtime, usually containerd, to start containers and reports their health. A network plugin, called a CNI plugin, gives each pod an address, and kube-proxy or an eBPF-based replacement routes traffic to Service addresses. Because all of this is continuous, a failed node causes its pods to be recreated elsewhere without anyone intervening.

#### Advantages and disadvantages

Kubernetes provides self-healing, autoscaling, rolling updates, rollbacks, and a common interface across clouds and on-premises data centers. Its ecosystem is enormous, covering Helm, Argo CD, Prometheus, Istio, and many operators. The disadvantages are equally real. The learning curve is steep, and operating a cluster means handling upgrades, certificates, networking, node security patches, and storage. For a system with three services and modest traffic, the operational cost can exceed the benefit. Managed services reduce that burden without removing it, and YAML files multiply quickly as systems grow.

#### Real-world examples

A retailer runs about sixty services on a cluster of twelve nodes. Before a sale, the Horizontal Pod Autoscaler adds pods, and the node autoscaler adds machines when the pods no longer fit. At two in the morning, a node fails, and its pods are rescheduled without paging anyone. A two-service startup chooses Amazon ECS on Fargate instead, because it wants fewer components to operate. A bank runs the same manifests on premises and in two clouds, so its release process does not change when workloads move.

#### Alternatives and industry practice

Amazon ECS, HashiCorp Nomad, Cloud Run, Azure Container Apps, and AWS App Runner each cover part of the same job with less operational work. Red Hat OpenShift adds opinionated tooling on top of Kubernetes. Current practice is to use a managed control plane, such as EKS, AKS, or GKE, and to have a platform team provide templates, security baselines, and deployment pipelines, so product teams focus on their services. Kubernetes is worth its complexity once the number of services and teams makes manual operations expensive.

### Q18: Explain the key Kubernetes components and objects.

#### In simple terms

A Kubernetes cluster has two parts. The control plane makes decisions and stores the cluster's state, and the worker nodes run the actual workloads. The main objects you create are the building blocks of your application. A Pod is one or more containers that share a network address and storage. A Deployment manages a set of identical Pods, handling replicas and updates. A Service gives a stable name and address to a changing set of Pods. Ingress routes outside HTTP traffic to Services. ConfigMaps and Secrets hold configuration, and a Namespace separates resources within one cluster. The notes' diagram captures the layout well.

#### Background: how it was before

Early software ran as processes on fixed servers, found each other through configuration files with hardcoded addresses, and was restarted by administrators. Load balancers pointed at IP addresses that changed when servers were replaced. Kubernetes introduced a model in which objects describe what should exist, and controllers make it so. This removed much of the manual wiring. The model also made it possible for one object, the Service, to represent a moving group of Pods behind one stable name.

#### How it works internally

The control plane consists of the API server, etcd, the scheduler, and the controller manager. The notes describe these correctly. On each worker, the kubelet manages Pods, and kube-proxy programs network rules so that traffic to a Service address reaches one of its Pods. The notes' description of kube-proxy as handling "load balancing" is too broad. It implements the Service abstraction by writing rules, and some clusters replace it with eBPF-based networking.

Several corrections are needed for the objects table. A Secret's values are base64 encoded, which is an encoding and not encryption. By default, Secrets are stored unencrypted in etcd unless encryption at rest is configured, and anyone who can read the manifest can decode them. Ingress is also being superseded by the Gateway API, which reached general availability in 2023 and provides a more expressive and role-oriented model. The community ingress-nginx project has announced its retirement, so new designs should evaluate Gateway API or their cloud provider's controller. The notes also omit several objects that appear in almost every real system. A ReplicaSet maintains a fixed number of identical Pods and is usually created by a Deployment. A StatefulSet gives Pods stable names and storage, for databases and brokers. A DaemonSet runs one Pod on each node, which suits log collectors. A Job runs a task to completion, and a CronJob runs Jobs on a schedule. Service types include ClusterIP, which is internal only, NodePort, and LoadBalancer, which provisions a cloud load balancer.

#### Advantages and disadvantages

The object model is consistent, so the same concepts apply to a small application and a large platform. Declarative objects can be stored in Git and reviewed. The drawbacks are the number of objects needed for even a simple application and the subtle interactions between them. A Deployment can look healthy while a Service sends traffic to no pods because the labels do not match, and beginners often spend hours on such mismatches.

#### Real-world examples

A team deploys an order service with a Deployment, a ClusterIP Service for internal calls, an Ingress for external access, a ConfigMap for settings, and a Secret for credentials. A logging agent runs as a DaemonSet on every node. A nightly reconciliation job runs as a CronJob that creates a Job each night. A database cluster runs as a StatefulSet with a PersistentVolume claim for each replica, so each replica keeps its identity and data across restarts.

#### Alternatives and industry practice

Helm and Kustomize package and parameterize these objects, and operators extend Kubernetes with custom resources for databases and message brokers. Current practice is to keep the objects in version control, use namespaces for environments and team boundaries, and encrypt Secrets at rest or move secret values into a managed vault and sync them into the cluster.

## Section 6: Deploying Spring Boot to Kubernetes

### Q19: Write the Kubernetes manifests to deploy a Spring Boot application.

#### In simple terms

Deploying a Spring Boot application to Kubernetes means writing several small YAML files that describe what should run and how it should be reached. The Deployment describes the pods: the image, replicas, resource needs, environment variables, and health probes. The Service gives those pods a stable internal name. The ConfigMap holds ordinary settings, the Secret holds sensitive ones, and the Ingress exposes selected paths to the outside world. Applying these files with kubectl apply sends the desired state to the cluster, and the controllers do the rest.

#### Background: how it was before

Early Kubernetes users often created resources with imperative commands such as kubectl run, which were quick but not reproducible. Teams that wanted repeatable deployments had to store command sequences in scripts, which broke whenever the scripts drifted from the cluster. The declarative model, where files describe the desired state, became the standard. Server-side apply, generally available since version 1.22, moved field-ownership tracking to the API server, which reduced conflicts between tools that edit the same objects.

#### How it works internally

When you apply the Deployment, the API server stores it, and the Deployment controller creates a ReplicaSet with the desired pod template. The ReplicaSet creates the pods, the scheduler places them, and the kubelet starts the containers. The readiness probe determines when each pod is added to the Service's endpoints, so a new pod receives traffic only after it reports ready. The Service is backed by an EndpointSlice that lists the ready pod addresses. Ingress is processed by an ingress controller, which configures a proxy with the routing rules.

The notes contain several issues to correct. The Ingress rewrite annotation is set to "/", which rewrites every incoming path to the root. A request for /orders/123 would therefore reach the order service as "/", which is wrong. A corrected rule uses a regular expression and a capture group:

```yaml
metadata:
 annotations:
 nginx.ingress.kubernetes.io/use-regex: "true"
 nginx.ingress.kubernetes.io/rewrite-target: /$2
spec:
 rules:
 - host: api.mycompany.com
 http:
 paths:
 - path: /orders(/|$)(.*)
 pathType: ImplementationSpecific
```

The ConfigMap sets KAFKA_BOOTSTRAP_SERVERS, but Spring reads the property spring.kafka.bootstrap-servers from the environment variable SPRING_KAFKA_BOOTSTRAP_SERVERS. The notes' variable is only used if application.yml references it explicitly. The CPU limit of 500m causes throttling when the JVM's garbage collector or startup code needs more CPU, so many teams set a CPU request and omit the CPU limit. The Secret's base64 value is not protection, so the manifest should never contain plaintext credentials in Git, and the value should come from a vault through a synchronization tool. Finally, image tags such as 1.0.0 are better than latest, but an immutable digest is safer still.

#### Advantages and disadvantages

Declarative manifests are reviewable, repeatable, and portable across clusters. Separating configuration from the image lets one image run in several environments. The disadvantages are verbosity, duplication across services, and the risk of subtle mistakes such as a wrong selector or an incorrect port. The notes' manifests will work with corrections, but repeating them for ten services invites drift, which is why Helm and Kustomize exist.

#### Real-world examples

A team applies the manifests to a staging namespace, watches the rollout with kubectl rollout status, and checks the Service endpoints to confirm that pods are receiving traffic. When an engineer changes the image tag in Git, the change is reviewed, and the pipeline applies it. A platform team discovers that a service's ingress path returns 404 for nested URLs, and traces the problem to the rewrite annotation.

#### Alternatives and industry practice

Kustomize overlays let one base manifest serve several environments. Helm charts template the same resources, as covered in Q29. Current practice keeps manifests in Git, uses server-side apply or GitOps tools for deployment, and sources secrets from managed stores.

### Q20: Explain the three types of Kubernetes probes and why they matter for Spring Boot.

#### In simple terms

A probe is a periodic check that the kubelet runs against a container. A startup probe answers whether the application has finished starting. A liveness probe answers whether the application is still alive or stuck, and a failure causes a restart. A readiness probe answers whether the application can accept traffic, and a failure removes the pod from the Service without restarting it. The distinction matters because the right response differs. A slow start should not trigger a restart, a stuck process should be restarted, and a temporarily overloaded process should simply stop receiving new requests.

#### Background: how it was before

Before Kubernetes, load balancers checked whether a TCP port accepted connections, or requested a page at fixed intervals. Those checks could not tell a process that was alive from one that was ready to serve. Spring Boot Actuator, first released in 2014, exposed a health endpoint that reported the status of the application and its dependencies. Kubernetes added probes early in its history, and Spring Boot 2.3 in 2020 added separate liveness and readiness states, which made the mapping to Kubernetes cleaner.

#### How it works internally

The kubelet runs probes on each container. Probes can be HTTP GET, TCP socket, command execution, or gRPC. Each probe has an initial delay, a period, a timeout, and a failure threshold. The default timeout is one second, which is short for a JVM that may pause for garbage collection, so the timeout should be set explicitly. When the startup probe is configured, liveness and readiness checks are held back until it succeeds. Spring Boot's probe groups are configured with the notes' keys and expose /actuator/health/liveness and /actuator/health/readiness.

A critical point the notes do not make is that liveness should not depend on external systems. Spring Boot's liveness state reflects only the application's own state by default, while readiness includes dependencies such as the database. If the liveness check included the database, a database outage would make every pod restart at once, which would make the outage worse. The notes' configuration is correct in keeping these states separate, and the principle should be applied to any custom health indicators.

The notes' timeline diagram is a helpful picture. During the first thirty seconds, the startup probe fails repeatedly without restarting the pod. Once it succeeds, liveness takes over, and readiness controls traffic.

#### Advantages and disadvantages

Probes allow Kubernetes to distinguish between starting, broken, and busy states. Using separate endpoints keeps decisions precise. The disadvantages are that poorly chosen thresholds can cause restart loops, that probes add load on the application, and that a readiness check that is too strict can remove all pods from service during a minor dependency blip. Testing probe behavior under load is important.

#### Real-world examples

A service loads a large cache at startup, which takes ninety seconds. Without a startup probe, the liveness probe would kill it repeatedly. With a startup probe allowing five minutes, the cache finishes loading and the pod joins the service. A payment service loses its connection to a database for thirty seconds. Its readiness probe fails, so traffic stops reaching it, but it does not restart, and traffic resumes when the connection returns. A deadlocked worker fails its liveness probe, and Kubernetes restarts it within a minute.

#### Alternatives and industry practice

Some teams use a TCP probe or a lightweight custom endpoint for liveness, which avoids dependency checks entirely. Service meshes and cloud load balancers can use their own health checks, and the AWS Load Balancer Controller supports pod readiness gates that tie target registration to pod readiness. Current practice is to give liveness minimal scope, make readiness reflect real ability to serve, set explicit timeouts, and test each probe under realistic startup and failure conditions.

### Q21: How does Kubernetes auto-scaling work for Spring Boot?

#### In simple terms

The Horizontal Pod Autoscaler watches a metric, usually CPU, and changes the number of replicas in a Deployment to keep that metric near a target. If CPU is high across the pods, it adds pods. If CPU stays low for a while, it removes pods, within the minimum and maximum you set. The formula is simple. The desired replica count is the current count multiplied by the ratio of the current metric to the target, rounded up. The notes' example is correct: three pods at ninety percent against a seventy percent target give ceil(3 × 90/70), which is four pods.

#### Background: how it was before

Before Kubernetes, scaling was either manual or handled by cloud provider groups, as described in the EC2 Auto Scaling section. Kubernetes introduced the HPA in version 1.1 in 2015 with CPU only. Later versions added memory, custom metrics, and external metrics, and the autoscaling/v2 API became generally available in version 1.23 in 2021. Vertical scaling, which changes resource requests rather than replica counts, is handled by a separate Vertical Pod Autoscaler.

#### How it works internally

The HPA controller runs about every fifteen seconds. It reads metrics from the metrics APIs. For CPU and memory, the data comes from the Metrics Server, which samples kubelet statistics. The controller calculates utilization as a percentage of the pods' CPU requests, which is why requests must be set correctly. If the ratio of current to target is within a tolerance of ten percent, the controller takes no action, which prevents jitter. Scale-up responds quickly, while scale-down waits for a stabilization window, which defaults to five minutes, so brief dips in traffic do not remove capacity that will soon be needed.

The notes contain two points to correct. Memory is a poor scaling signal for a JVM, because the heap grows to whatever the collector allows and rarely shrinks, so memory utilization can stay high even when the service is idle. Requests per second or latency usually make better signals. The custom metric example uses kafka_consumer_lag, which is not a built-in metric. It requires a metrics adapter such as the Prometheus Adapter or KEDA to expose it to the HPA. Also, scaling consumers beyond the number of partitions in a Kafka topic does not increase parallelism, because each partition is consumed by at most one consumer in a group. That limit is essential when designing event-driven scaling.

The HPA also cannot scale to zero by default, which is one reason KEDA is popular for queue-driven workers.

#### Advantages and disadvantages

The HPA adapts to load without human involvement and works with any metric the cluster can expose. The disadvantages include scaling lag, because new pods must be scheduled, started, and warmed up, and the JVM may take many seconds to reach full speed. Scaling out can overload a shared database if every new pod opens a connection pool. HPA interacts with node autoscaling, so pods may wait for new nodes, which adds minutes.

#### Real-world examples

An order API scales on requests per second per pod exposed through a custom metrics adapter, and the team sets minimum replicas to cover the normal baseline. A consumer for a payment topic scales on consumer lag, up to the number of partitions. A marketing team schedules a campaign, and the team raises the minimum replicas in advance, because waiting for the HPA to react would cause slow responses in the first minutes of the campaign.

#### Alternatives and industry practice

KEDA scales on event sources such as queues, topics, and cron schedules, and can scale to zero. Karpenter and the Cluster Autoscaler add nodes when pods cannot be scheduled. Predictive scaling and scheduled scaling are used where traffic follows known patterns. Current practice scales on business-relevant signals, sets requests carefully, and combines HPA with node autoscaling and pre-scaling for known events.

## Section 7: Real-World Scenario Questions

### Q22: Your Spring Boot app runs fine locally but crashes in Kubernetes with OOMKilled. What is happening?

#### In simple terms

OOMKilled means the container used more memory than its limit, so the Linux kernel's out-of-memory mechanism terminated it. Kubernetes reports this as a termination reason, and the exit code is 137, which corresponds to a SIGKILL. Locally, the application might run comfortably because your laptop has many gigabytes of memory and the JVM sizes its heap from that total. In Kubernetes, the same JVM sees only the container's limit, but it can still use more memory than that limit in total, because heap is only part of the process's memory.

#### Background: how it was before

Before containers, Java applications ran on virtual machines or physical servers with fixed memory, and administrators set the heap size by hand. Many teams set a large fixed heap and hoped for the best. When containers arrived, the JVM read the host's memory rather than the container's limit, which caused containers to be killed. Java 8 update 191 and Java 10 added container awareness, reading cgroup limits. Modern JDKs such as Java 17 and 21 respect those limits by default, so the old root cause is rarer, though it still appears on older runtimes.

#### How it works internally

The JVM's default maximum heap is twenty-five percent of the available memory. In a container with a one-gigabyte limit, that default is about 256 megabytes, which is safe. The notes' claim that the default heap can exceed the container limit is therefore incorrect for modern JVMs. The problems usually come from elsewhere. Teams set -Xmx explicitly to a value equal to the limit, which leaves no room for other memory. Non-heap memory includes metaspace, which can grow with class loading, thread stacks of roughly one megabyte per thread on Linux, direct buffers used by Netty, Kafka clients, and many libraries, and the code cache for JIT-compiled methods. A service with two hundred threads can reserve about two hundred megabytes for stacks alone.

The notes' suggested fix is correct in principle. Setting MaxRAMPercentage to seventy-five percent leaves about a quarter of the container limit for non-heap memory and the operating system. The fix works best when combined with an explicit understanding of the non-heap components. Native Memory Tracking can show where the memory goes, using the flag -XX:NativeMemoryTracking=summary together with jcmd.

The diagnostic commands in the notes are useful. kubectl describe pod shows the last state and reason, kubectl top pod shows current usage if the Metrics Server is installed, and kubectl logs --previous shows output from the container that was killed.

#### Advantages and disadvantages

Setting explicit memory limits protects other workloads on the node, and percentage-based heap sizing adapts to different container sizes. The disadvantages are that setting the limit too low causes repeated kills, setting it too high wastes money, and percentage-based sizing can hide a memory leak until a larger instance is involved. Memory limits and the JVM's behavior must be tuned together, and load testing is the only reliable way to validate the settings.

#### Real-world examples

A team sets a one-gigabyte limit and an explicit heap of one gigabyte. Under load, the process grows past the limit, and the kernel kills it. Switching to MaxRAMPercentage leaves room, and the restarts stop. Another team sees OOMKills only after a feature adds a thread pool of four hundred threads. The stacks alone take 400 megabytes, and the fix is either a smaller pool or a higher limit. A third team finds that a Kafka client's direct buffer usage grows with message size, and they tune batch sizes.

#### Alternatives and industry practice

Some teams run the JVM with a fixed heap sized from a benchmark and a generous limit, which is predictable but wasteful. Others use Java runtimes built for low memory, such as those with the Shenandoah or ZGC collectors, which trade CPU for lower pause times. Current practice sets requests and limits from load tests, uses MaxRAMPercentage, monitors non-heap metrics with Micrometer, and alerts on restarts with reason OOMKilled.

### Q23: You need zero-downtime deployment for your Spring Boot microservice. How do you achieve it?

#### In simple terms

Zero downtime means users never see an outage while a new version is rolled out. Kubernetes does most of the work with a rolling update. It starts a new pod, waits until that pod is ready, and only then removes an old pod. The application must also stop cleanly. When a pod is told to terminate, it should finish the requests it has already accepted, and it should stop receiving new ones before it exits. The notes' strategy settings are correct. Their shutdown sequence has an order error and a dependency on a shell that some images do not have, and both need correction.

#### Background: how it was before

Before rolling updates, teams used blue/green deployments with two complete fleets, or they accepted a maintenance window. Some used load balancers to remove servers from a pool before updating them one at a time, which worked but required manual coordination. Kubernetes added rolling updates to Deployments early, and the pattern became the default. Spring Boot's graceful shutdown, which lets the web server finish in-flight requests, arrived in version 2.3 in 2020, and before then teams wrote their own drain logic.

#### How it works internally

The Deployment controller uses the maxSurge and maxUnavailable settings. With maxSurge 1 and maxUnavailable 0, it creates one new pod, waits for readiness, and then removes one old pod. Traffic is only sent to pods that have passed readiness, through the Service's endpoints.

The termination sequence is more subtle than the notes suggest. When a pod is deleted, Kubernetes marks it as terminating and removes it from the endpoints. Kube-proxy and load balancers update asynchronously, so some requests may still arrive for a short time. The preStop hook runs before SIGTERM is sent to the container, not after it. The grace period, which defaults to thirty seconds, includes the time spent in the preStop hook. If the preStop sleep is ten seconds and Spring Boot's graceful shutdown allows thirty seconds, the total of forty seconds exceeds the default grace period, and the pod will be killed with SIGKILL before the drain finishes. The fix is to set terminationGracePeriodSeconds to a value larger than the sum.

The preStop hook as written uses sh -c, which requires a shell in the image. A distroless image has no shell, so the hook fails. In that case, the hook can use an exec command that exists in the image, or the team can rely on the readiness mechanism and a small preStop binary.

Graceful shutdown also has limits. It applies to the HTTP server. Kafka consumers must stop polling, finish processing the current batch, and commit their offsets. The Spring Boot shutdown phase must be configured for those components too. The SIGTERM signal must reach the Java process, which requires the exec form of ENTRYPOINT discussed in the Docker section.

#### Advantages and disadvantages

Rolling updates require no extra infrastructure and keep capacity during the rollout. The disadvantage is that both versions run at the same time for a while, so APIs and database schemas must be compatible across versions. This usually means using an expand-and-contract approach for schema changes. A rollout is also slower than a blue/green switch, and an incompatible version can serve traffic before anyone notices. Canary releases and automated analysis reduce that risk, at the cost of more tooling.

#### Real-world examples

An order service handles a thousand requests per second. During a rollout, each old pod receives its termination signal, the preStop sleep allows the endpoint change to propagate, and the server finishes the requests in flight. Users see no errors. A consumer pod that does not stop polling cleanly processes a message twice after the rollout, and the team adds idempotency checks. A team uses an Argo Rollouts canary to send five percent of traffic to the new version and analyzes error rates before continuing.

#### Alternatives and industry practice

Blue/green deployments using a Service selector switch or a progressive delivery controller give fast rollback, and canary releases give controlled exposure. Current practice combines rolling updates for routine changes with readiness probes, graceful shutdown, a correctly sized termination grace period, backward-compatible APIs, and a documented rollback procedure.

### Q24: You have 10 Spring Boot microservices. How do you structure the Kubernetes deployment for production?

#### In simple terms

Ten services can live in one cluster, but they need an organized layout. Each stateless service gets a Deployment, a Service, a HorizontalPodAutoscaler, and usually a PodDisruptionBudget. Namespaces group related resources and provide boundaries for access, quotas, and policies. The notes' layout is a reasonable starting point. Its main weaknesses are that it mixes self-managed and managed data services, treats namespaces as complete security boundaries, and does not mention the policies that make a multi-service cluster safe to operate.

#### Background: how it was before

Early in Kubernetes adoption, many organizations put everything in one flat namespace, which made access control and resource limits hard to manage. Teams often ran one cluster per application, which wasted capacity. Platform teams later moved toward shared clusters with namespaces per team and environment, combined with policy controls. Some organizations went the other way and created separate clusters for production and non-production to reduce the blast radius of mistakes and the risk of cross-environment access.

#### How it works internally

Objects in a namespace are addressed by DNS names that include the namespace, so the order service is reachable from the payment service as payment-service.production.svc.cluster.local, or simply by name within the same namespace. ResourceQuotas cap the total CPU and memory a namespace can request, and LimitRanges set defaults for containers that do not declare resources. RBAC rules control who can change objects in each namespace. NetworkPolicies control which pods can talk to which, and they must be enforced by the network plugin. Without NetworkPolicies, every pod can reach every other pod, which is the default in Kubernetes.

The notes should correct several points. A namespace is not a strong security boundary by itself, because pods in different namespaces share nodes and the same kernel, and a misconfigured role can cross namespace lines. Many organizations therefore use separate clusters for production and non-production. The notes also mix managed and self-run services. Kafka appears as a StatefulSet and as a managed service in other sections, so the team should choose one. Running Kafka on Kubernetes requires an operator such as Strimzi and serious expertise, and Amazon MSK or Confluent Cloud removes that burden. Redis has the same tradeoff with ElastiCache. The config-server is optional. Kubernetes ConfigMaps and Secrets may be enough, and Spring Cloud Config adds another service to run and secure.

A production-ready layout also includes a PodDisruptionBudget for each service so that node maintenance cannot remove too many replicas at once, a ServiceAccount per service for least-privilege AWS access, resource requests and limits, and probes.

#### Advantages and disadvantages

A shared cluster reduces overhead and makes common tooling easier to standardize. Namespaces and quotas make team boundaries visible. Separate clusters improve isolation and make upgrades less risky, but they multiply operational work. A single cluster also means a control-plane problem or a bad upgrade affects every service at once.

#### Real-world examples

An e-commerce company runs production and staging in separate clusters, each with namespaces for services and for monitoring. Each service has its own Helm release, and a platform team enforces NetworkPolicies so that only the API gateway can reach the user service from outside. The database and message broker are managed services reached through private endpoints. A startup with ten services runs one cluster and uses namespaces for environments, which is acceptable at that scale if policies are enforced.

#### Alternatives and industry practice

Multi-cluster management tools and GitOps controllers can manage many clusters from one repository. Some organizations put each team in its own cluster. Current practice uses managed control planes, separates production clusters, applies ResourceQuota, LimitRange, RBAC, NetworkPolicy, and Pod Security admission, and runs stateful systems either as managed services or with mature operators.

### Q25: How do you debug a Spring Boot pod that keeps restarting in Kubernetes?

#### In simple terms

A pod that keeps restarting is usually in CrashLoopBackOff. Kubernetes starts the container, it exits or fails a probe, and Kubernetes waits before restarting it, with the wait increasing each time up to a maximum of five minutes. The debugging approach is to read the evidence Kubernetes keeps: the pod's status and events, the logs from the last crashed container, and the resource usage. Most problems show up in one of those three places. The notes describe this workflow correctly, and a few details need correction.

#### Background: how it was before

Before containers, a failing service was debugged by logging into the machine, reading log files, and checking processes. Restarts were handled by process supervisors such as systemd or supervisord, and their history was often lost. Kubernetes records restart counts, last termination states, and events, and it keeps logs for the previous container instance. The backoff mechanism prevents a broken application from consuming resources by restarting constantly.

#### How it works internally

The kubelet tracks each container's exit code and reason. An exit code of 137 indicates a SIGKILL, often from OOMKilled. An exit code of 143 indicates SIGTERM, which is normal when a pod is deleted. An exit code of 1 usually means the application threw an unhandled exception. The restart policy for Deployments is Always, so the kubelet restarts the container with exponential backoff. Events describe other failures, such as a missing ConfigMap, which prevents the container from starting with a CreateContainerConfigError, or an image that cannot be pulled, which shows as ImagePullBackOff.

The notes contain a few errors. The table suggests using depends_on or init containers to handle an unready database. depends_on is a Docker Compose feature and has no effect in Kubernetes. Init containers can wait for a dependency before the main container starts, but a more robust approach is for the application to retry its connection and let readiness reflect the dependency state. The notes also suggest kubectl exec into a pod that is in CrashLoopBackOff, which fails because the container is not running. The alternative is kubectl debug with an ephemeral container, which attaches a debugging image to the pod. A curl command inside the application container also assumes that curl exists, which is not true of distroless images.

A reliable sequence is to run kubectl get pods to see the restart count and status, kubectl describe pod to see the last state, reason, exit code, and recent events, kubectl logs with --previous to read the crash output, and kubectl top to check resource pressure. Events can be viewed across the namespace with kubectl get events sorted by timestamp.

#### Advantages and disadvantages

The built-in evidence is excellent once you know where to look. The disadvantages are that logs are lost when a pod is deleted unless they are shipped elsewhere, that the previous container's logs only cover the last crash, and that exec and debug access may be restricted in production for security reasons. Good observability and centralized logs reduce the dependence on these tools.

#### Real-world examples

A pod restarts every few minutes. Its last state shows exit code 1, and the previous logs show a missing property for the database URL, because a ConfigMap key was renamed. A second pod shows OOMKilled after load testing. A third pod is killed by its liveness probe because the health endpoint calls a slow external service, and the fix is to move that check to readiness. A fourth pod stays in ImagePullBackOff because the registry credentials expired.

#### Alternatives and industry practice

Centralized logging through Loki, CloudWatch, or Elastic keeps logs after pods are gone. Metrics for restart counts and alerting for CrashLoopBackOff shorten detection time. Current practice includes readiness and liveness design, structured logs with the failing configuration key, and runbooks for common causes.

### Q26: How do you handle database migrations in a Kubernetes/Docker deployment?

#### In simple terms

A database migration changes the database schema, such as adding a table or a column. In a deployment with several replicas, migrations must run exactly once and must finish before the new code depends on them. Flyway and Liquibase are the usual tools. They record which migrations have run, apply the new ones in order, and refuse to run if a previously applied file has changed. The main design question is where to run them. The notes suggest two places, the application's startup and an init container, and the second suggestion has a serious flaw.

#### Background: how it was before

Before automated migration tools, database administrators applied SQL scripts by hand, often at night, and environments drifted apart. Liquibase appeared in 2006 and Flyway in 2010, both bringing version control to schema changes. Early Spring Boot applications often ran migrations at startup, which was simple for one instance and became a problem as instances multiplied. Container platforms made the issue more visible because replicas start in parallel.

#### How it works internally

Flyway reads migration files such as V1__create_orders_table.sql, computes checksums, and stores its history in a table called flyway_schema_history. Before applying changes, it acquires a lock, which on PostgreSQL is a database-level advisory lock. The notes describe the lock as a lock table, but the history table records migrations and the locking is done by the database. Because of this lock, if three pods start together, one runs the migrations and the others wait, and then they all see the schema is current.

The init container example in the notes is not safe as a general approach. An init container runs in every pod, so three replicas run it three times. Flyway's lock prevents conflicting changes, but the sequence still gives no guarantee about old pods running during a rolling update. An old pod using the previous code may run against a schema that has already changed. The notes claim the init container guarantees migrations finish before any app pod starts, which is not true across a rollout because old pods continue running. Also, the example omits the password for Flyway.

A safer design runs migrations as a Kubernetes Job before the Deployment is updated. Helm supports pre-upgrade hooks, and Argo CD supports PreSync hooks, which both run the Job at the right point in the deployment. Schema changes should also be backward compatible, using the expand-and-contract pattern. First, add nullable columns or new tables. Next, deploy code that writes to both the old and new shapes. Then backfill data. Finally, switch reads and remove the old structure in a later release.

#### Advantages and disadvantages

Running migrations at startup is simple and works for small systems. A separate Job decouples migration from application instances and gives clearer failure reporting, but it adds an orchestration step. Expand-and-contract avoids downtime but requires more releases. Flyway does not roll back automatically, so the usual approach is a forward fix. Some changes, such as adding an index on a large table, can lock writes for long periods, and they need specialized handling.

#### Real-world examples

A team adds a column to a table with millions of rows. The first release adds the nullable column, the second writes to it, the third backfills in batches, and the fourth makes it required. A migration that rebuilds a large index runs during a low-traffic window with a monitored lock timeout. A team that ran migrations in every pod discovers that two pods applied conflicting changes after a failed deployment, and it moves migrations into a Job.

#### Alternatives and industry practice

Liquibase offers change sets and rollback scripts, and some teams use an online schema change tool such as gh-ost or pt-online-schema-change for MySQL. Current practice runs migrations as a separate, idempotent step in the pipeline, keeps each migration small and reversible in principle, and uses backward-compatible changes so old and new code can run together.

## Section 8: Full Architecture: Putting It All Together

### Q27: Draw and explain a complete production architecture for Spring Boot microservices on AWS with Kubernetes.

#### In simple terms

A production architecture connects the internet, the edge, the cluster, and the managed services that hold data. A request starts at DNS, passes through a content delivery network and a web application firewall, reaches a load balancer, enters the cluster through an ingress layer, and is routed to the right service. Services talk to managed databases, caches, and message brokers. Monitoring, logging, and tracing run alongside. The notes' diagram is a reasonable overview, and it needs several corrections to be coherent.

#### Background: how it was before

Early web architectures were three tiers: a web server, an application server, and a database, usually on dedicated machines. Scaling meant buying bigger machines or adding copies behind a hardware load balancer. Service-oriented systems added enterprise service buses. The current architecture pattern grew from cloud platforms, in which load balancers, databases, queues, and secrets became managed services, and Kubernetes became the common runtime for the application tier.

#### How it works internally

A request reaches Route 53, which resolves the domain to CloudFront. AWS WAF is associated with the CloudFront distribution, so malicious requests are blocked at the edge. CloudFront forwards dynamic requests to an origin, which is typically an Application Load Balancer. The AWS Load Balancer Controller can create that ALB from Kubernetes Ingress resources and register pod IPs directly as targets.

The notes place an NGINX ingress controller behind the ALB, which creates two layers of load balancing and routing. Either the ALB handles routing through the AWS controller, or an NLB forwards to an NGINX ingress, but both are unnecessary together. The community ingress-nginx project has announced its retirement, so the Gateway API with a supported controller is a better long-term choice. The notes also list Kafka as a StatefulSet in one place and Amazon MSK in another, and Redis as a StatefulSet and ElastiCache. The architecture should use managed services consistently.

Routing rules send /orders to the order service, /users to the user service, and /payments to the payment service. Payment processing usually has stricter security requirements, so it benefits from its own namespace or cluster, strict NetworkPolicies, and mutual TLS. Istio sidecars provide mutual TLS and traffic telemetry, but they add memory and CPU to every pod, which must be counted in capacity planning. Services get AWS permissions through EKS Pod Identity or IAM Roles for Service Accounts, and secrets come from Secrets Manager through the External Secrets Operator or the Secrets Store CSI driver. Logs flow through Fluent Bit to a central store, and metrics are scraped by Prometheus.

The notes also list CloudWatch as "alarms plus log backup," which is vague. A clearer design uses CloudWatch for AWS-native alarms and selected logs, Prometheus and Grafana for application metrics, and a single trace backend fed by OpenTelemetry.

#### Advantages and disadvantages

Managed data services shift patching, backups, and failover to AWS, which lets the team focus on the application tier. Running the application tier in Kubernetes provides portability and a standard operating model. The architecture is expensive, because it combines several load balancers, service mesh overhead, multi-AZ databases, and monitoring stacks. Its complexity is significant, and each component needs an owner.

#### Real-world examples

A retail company routes its web and mobile traffic through CloudFront and WAF, uses Aurora with multiple replicas for orders, ElastiCache for session data, and MSK for order events, and runs its services in EKS with HPA. During a sale, the HPA scales the order service while Aurora read replicas absorb the read traffic. A payment failure triggers an alarm that pages the team, while Grafana dashboards show which service and dependency is affected.

#### Alternatives and industry practice

A serverless architecture with API Gateway and Lambda, as discussed in earlier sections, can replace parts of the cluster for event-driven workloads. Some teams use Fargate on EKS to avoid managing nodes. Current practice uses managed control planes and data services, the Gateway API for routing, a service mesh only when mutual TLS and traffic policy are required, GitOps for deployment, and OpenTelemetry for observability.

### Q28: What is GitOps, and how does it apply to Spring Boot Kubernetes deployments?

#### In simple terms

GitOps means Git is the single source of truth for what runs in a cluster. A change to a deployment is a commit, and an agent inside the cluster continuously compares the cluster with the repository and applies differences. Nobody needs to run kubectl apply from a laptop, and every change is reviewed, recorded, and reversible. The notes' flow is correct in outline. The CI pipeline builds and pushes the image, updates the version in a manifest, and the controller applies it. Two details need correction. The automatic sync and self-heal options must be enabled explicitly, and secrets must never be stored in Git in plaintext.

#### Background: how it was before

Many organizations deployed with push-based pipelines. The CI system had cluster credentials and ran kubectl or Helm directly. That design gave the CI system broad access to production, and it made cluster state depend on whatever the pipeline last did. Drift was common, because engineers made emergency changes directly in the cluster. The term GitOps was coined by Weaveworks around 2017. Argo CD and Flux, both open source, became the leading implementations, and the OpenGitOps principles describe the model in four parts: declarative desired state, versioned and immutable storage, automatic pull and application, and continuous reconciliation.

#### How it works internally

In Argo CD, an Application resource points to a repository path and a target cluster and namespace. The repo server renders the manifests, whether they are plain YAML, Kustomize, or Helm. The application controller compares the rendered desired state with the live objects and reports the difference. A sync applies the changes. With automated sync, the controller applies changes without a human click, and with self-heal enabled, it reverts manual edits in the cluster. Sync waves and hooks order the steps, so a database migration Job can run before the Deployment changes.

The notes' pipeline has a design issue. If CI commits to the same repository that contains the application source, the commit can trigger another pipeline run, and the history mixes code and deployment changes. A common pattern uses a separate configuration repository updated by the CI system. Image updates can also be automated by tools such as Flux's image automation or Argo CD Image Updater, which watch the registry and commit new tags.

Secrets require particular care. Base64 values in Git are readable by anyone with repository access. Options include Sealed Secrets, which encrypts values with a cluster key, SOPS, which encrypts files, and the External Secrets Operator, which keeps only references in Git.

#### Advantages and disadvantages

GitOps gives an audit trail, consistent environments, easy rollback by reverting a commit, and reduced need for cluster credentials outside the cluster. The disadvantages include a learning curve, slower feedback than a direct push, complexity in promoting changes across environments, and the fact that rollback restores manifests but not data changes made by migrations. Repositories can also sprawl if every team creates its own structure without standards.

#### Real-world examples

A team keeps a configuration repository with folders for staging and production. Promoting a release means changing the image digest in the production folder through a reviewed pull request. An incident during a release is handled by reverting that commit, and Argo CD restores the previous state within minutes. An engineer who edits a Deployment directly in the cluster sees the change reverted by self-heal, which teaches the team to use the repository.

#### Alternatives and industry practice

Push-based pipelines with scoped service accounts remain common, especially for smaller teams. Argo Rollouts and Flagger add progressive delivery on top of GitOps. Current practice keeps configuration in Git, separates application and configuration repositories where useful, enables automated sync with self-heal in non-production environments, uses encrypted secrets, and requires review for production changes.

### Q29: What is Helm, and why use it for Spring Boot Kubernetes deployments?

#### In simple terms

Helm is a package manager for Kubernetes. A chart is a package containing templates for Kubernetes objects and a values file with default settings. Each service supplies its own values, such as the image, replica count, and memory, and Helm renders the templates into the final manifests. The notes' analogy to Maven is apt. A chart is like a reusable project template, and values are the parameters that differ between projects. The notes' example of 75 nearly identical files is a fair description of the problem Helm addresses.

#### Background: how it was before

Before Helm, teams copied manifests between services and edited them by hand, which led to inconsistent labels, probes, and resource settings. Some used shell scripts with sed to substitute values. Deis created Helm in 2015, and it joined the Cloud Native Computing Foundation in 2016. Helm 2 relied on a server component called Tiller, which created security concerns because it had broad cluster permissions. Helm 3, released in 2019, removed Tiller and stores release history as Secrets in the target namespace. Kustomize, built into kubectl since version 1.14, offered a template-free alternative based on overlays.

#### How it works internally

Helm renders templates with Go's template language and the Sprig function library. The notes' template is correct in outline, but a complete chart also includes labels, selectors, probes, and service accounts, and the template should use these consistently. Helm tracks each installed release and its revision history. An upgrade computes a three-way merge between the previous manifest, the new rendered manifest, and the live state, which allows Helm to handle changes that someone made manually. A rollback applies a previous revision. Hooks run Jobs at points such as pre-install or pre-upgrade, which is the right way to run database migrations, as discussed in Q26.

Helm does not undo data changes during a rollback, and it does not manage CRD upgrades completely, because CRDs are not always updated as part of a chart. The notes' commands are valid. The helm install, upgrade, and rollback commands are used correctly.

#### Advantages and disadvantages

Helm reduces duplication, makes releases versioned and reversible, and gives the community a large catalog of ready-made charts for databases, monitoring, and ingress controllers. The disadvantages are that Go templates are hard to read when they become complex, template errors can be confusing, and values files can drift from the defaults. Helm charts often hide what is actually deployed, so debugging requires helm template or helm get manifest. A chart that tries to support every option becomes unmaintainable.

#### Real-world examples

A platform team maintains one internal chart for Spring Boot services. Each team supplies a values file with its image, replicas, probes, and environment settings. A new service is onboarded by writing a values file rather than fifteen manifests. The team installs Prometheus, Grafana, and the ingress controller from community charts with pinned versions. A failed upgrade is rolled back with one command, and the team validates the chart in CI with helm lint and helm template.

#### Alternatives and industry practice

Kustomize uses base manifests and overlays, which is simpler for applications that differ only slightly across environments. Jsonnet, cdk8s, KCL, and Carvel ytt use general-purpose languages for configuration. Current practice uses Helm for third-party software and often for internal services, Kustomize for simple overlays, charts validated in CI, pinned chart versions, and GitOps tools that render charts during sync.

### Q30: Compare ECS Fargate and EKS for Spring Boot microservices. When do you use each?

#### In simple terms

Amazon ECS and Amazon EKS both run containers on AWS. ECS is AWS's own orchestrator, with fewer concepts and tight integration with AWS services. EKS is managed Kubernetes, which exposes the full Kubernetes API and ecosystem. Fargate is a compute option that runs tasks or pods without managing servers, and it works with both. The notes' comparison is a useful starting point, but three entries need correction. EKS manifests are portable across clouds, but load balancers, storage, and identity still differ, so portability is partial. AWS App Mesh, listed in the table as limited, has been announced for retirement, so it should not be chosen for new designs. The rule of thumb about starting with ECS is a reasonable heuristic for small teams, not a law.

#### Background: how it was before

ECS launched in 2014 and EKS in 2018, and Fargate arrived in 2017 for ECS. Before these services, teams ran containers on EC2 instances they managed themselves, often with scripts or configuration management tools. Mesos and Marathon were popular for large workloads, and Docker Swarm offered a simpler option. AWS offered ECS as its answer to orchestration within its own platform, and EKS as managed Kubernetes for teams that wanted the open ecosystem.

#### How it works internally

ECS uses task definitions to describe containers and services to keep a desired number of tasks running, connected to load balancers. The control plane is managed by AWS and has no separate charge. Tasks run on EC2 capacity that you manage or on Fargate, where each task runs in its own isolated environment. ECS Service Connect provides service discovery and traffic management within the service.

EKS runs the Kubernetes control plane across multiple AZs, and AWS charges a fixed hourly fee per cluster, about ten cents an hour at the time of writing. You add capacity through managed node groups, Karpenter, or Fargate profiles. Pods receive AWS permissions through EKS Pod Identity or IRSA. Because EKS exposes standard Kubernetes APIs, the same tools that work on other clusters, such as Helm, Argo CD, and Prometheus operators, work here. AWS has also introduced EKS Auto Mode, which manages more of the node and networking configuration, reducing the operational work.

#### Advantages and disadvantages

ECS is simpler to learn and operate, has fewer moving parts, and integrates tightly with IAM, load balancers, and CloudWatch. EKS offers the standard Kubernetes API and ecosystem, which improves portability, hiring, and tooling. The disadvantages of ECS are narrower ecosystem and AWS lock-in. The disadvantages of EKS are a steeper learning curve, more upgrades to manage, and a higher baseline cost for small deployments. Fargate simplifies both platforms but costs more per unit of compute than well-used EC2 capacity.

#### Real-world examples

A startup with six Spring Boot services and a small team chooses ECS on Fargate. Deployments use CodePipeline, and the team spends its time on features rather than cluster upgrades. A fintech company with forty services, a platform team, and a plan to run some workloads in another cloud chooses EKS. It uses Helm, Argo CD, and a service mesh for mutual TLS. A company with an existing Kubernetes team and strict standards on tooling chooses EKS because its engineers already know the platform.

#### Alternatives and industry practice

Google Cloud Run and Azure Container Apps offer serverless containers with simpler operation than Kubernetes. App Runner suits simple web services. Current practice starts with the simplest platform that meets the requirements, uses managed Kubernetes when the ecosystem and portability matter, and plans for migration costs when moving between platforms. The choice is less about raw capability and more about the team's skills and the number of services it must operate.