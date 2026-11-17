# Section 1: Docker Fundamentals

## Q1: What is Docker and why do we need it for Spring Boot applications?

### In simple terms

Docker packages an application together with everything it needs to run, such as the runtime, libraries, and configuration, into a single unit called a container. That container behaves the same way on a developer's laptop, a test server, and production. For a Spring Boot application, this means the Java version, the Maven build output, and the application's settings travel together, so the question "why does it work here but not there?" becomes much less common. The notes' example is accurate in spirit. Two things need a correction, though. The JDK is provided by the base image that the Dockerfile starts from, not by the application itself, and a container usually does not include the database. PostgreSQL normally runs in its own container, and the application connects to it over a network.

### Background: how it was before

The "it works on my machine" problem is as old as shared software. Teams tried to solve it with long setup documents, shared virtual machines, and configuration management tools such as Chef and Puppet, which described how to install software on a server. Virtual machines helped because a whole machine image could be copied, but VMs are large and slow to start. Linux had been developing the underlying features for years. Control groups, which limit and measure resource use, were merged into the Linux kernel in 2007, and namespaces, which isolate what a process can see, appeared earlier. LXC in 2008 combined these into usable containers. Docker, released in 2013, made the experience simple with a build file, a standard image format, and a public registry. Spring Boot's executable JAR, which appeared in 2014, made Java applications self-contained, and containers extended that idea to the whole environment.

### How it works internally

Docker runs a daemon on the host, and that daemon uses Linux namespaces to give each container its own view of processes, network interfaces, mounted files, and hostnames. Control groups limit how much CPU and memory a container can use. A container is therefore a regular Linux process with boundaries around it, not a separate computer. The image provides the files the process sees, and the container runs on the host's kernel. This is why containers start in a fraction of a second.

### Advantages and disadvantages

The advantages are consistency across environments, isolation between applications on the same host, fast startup, easy scaling, and a natural fit with continuous integration. A build produces one image, and that same image can be tested and promoted. The disadvantages are real. Containers share the host kernel, so they are not as strongly isolated as virtual machines. Docker introduces its own learning curve, including networking, volumes, and image hygiene. Docker Desktop on macOS and Windows runs a Linux virtual machine underneath, which can affect performance. Licensing for Docker Desktop changed in 2021 for larger organizations, so enterprises should check the terms that apply to them.

### Real-world examples

A team builds an order service as a Spring Boot application, and the QA server still runs Java 11. Before containers, the team spent days discovering this difference. With Docker, the image pins Java 17, so QA runs the same bits as production. Another example is a bug report from a customer whose setup differs slightly. A developer runs the exact image the customer used, reproducing the issue in minutes. A third example is scaling for a flash sale, when ten copies of the same container start within seconds on a set of machines.

### Alternatives and current industry practice

Virtual machines remain useful when stronger isolation is needed, and many containers still run on virtual machines in cloud environments. Podman offers a daemonless, rootless alternative with a compatible command line. Buildpacks and Jib can produce container images for Java applications without a Dockerfile. Industry practice treats containers as the standard unit of deployment, with images built once in continuous integration, scanned for vulnerabilities, stored in a registry, and promoted through environments.

## Q2: What is the difference between a container and a virtual machine?

### In simple terms

A virtual machine pretends to be a complete computer. It has its own operating system kernel, its own virtual hardware, and its own boot process. A container shares the host's kernel and only isolates the application's processes and files. The notes' analogy is apt. A virtual machine is like renting an entire apartment with its own kitchen and bathroom. A container is like renting a room in a shared house, where the kitchen and bathroom are shared but your room is private. The room is much cheaper and faster to set up, but the walls are thinner, so the people living in the house need to trust each other more.

### Background: how it was before

Virtualization is older than most people assume. IBM's CP-40 system in the 1960s let several users run separate operating system instances on one mainframe. Modern x86 virtualization became practical with VMware in 1999, and hardware support from Intel and AMD in the mid-2000s made it efficient. Xen appeared in 2003 and KVM was merged into Linux in 2007. Operating-system-level isolation existed in parallel, with FreeBSD jails in 2000 and Solaris zones in 2004. Linux containers combined these ideas later, which is why containers became popular in the early 2010s, once Docker made them easy to use.

### How it works internally

A hypervisor emulates hardware for each virtual machine, and each one boots its own kernel. System calls from applications go to that guest kernel, which talks to virtual devices, which the hypervisor maps onto real hardware. A container's system calls go straight to the host kernel, so there is no hardware emulation layer. Its isolation depends on namespaces and control groups. This is why containers are lighter, and it is also why a kernel vulnerability can affect every container on the host, while a flaw inside one guest kernel is usually contained within that virtual machine.

The notes' table is largely right. One correction is needed: images are often tens to hundreds of megabytes rather than "MBs" in the abstract, because a Spring Boot image includes a Java runtime. The table should also mention security isolation explicitly, because it is the main reason some workloads still run in virtual machines.

### Advantages and disadvantages

Containers start in seconds, use fewer resources, and pack more applications onto the same hardware. Virtual machines provide stronger isolation, support different operating systems on the same hypervisor, and have mature management tools. Containers depend on the host kernel, so a Linux container cannot run natively on a Windows kernel. Multi-tenant platforms running untrusted code often add a stronger boundary by running containers inside lightweight virtual machines.

### Real-world examples

A large company runs its Spring Boot services in containers on a set of cloud virtual machines, combining the density of containers with the isolation of virtual machines. A platform that runs customer-supplied code uses microVMs for each workload, so one customer cannot reach another's processes even if the container layer has a flaw. AWS Lambda uses Firecracker microVMs for this reason.

### Alternatives and current industry practice

Firecracker and Kata Containers provide virtual machine isolation with container-like startup. gVisor intercepts system calls in user space to reduce kernel exposure. WebAssembly runtimes are emerging for lightweight, portable workloads. The most common production pattern today is containers running inside virtual machines, which gives both density and an extra isolation layer.

## Q3: Explain Docker terms: Image, Container, Dockerfile, and Registry.

### In simple terms

A Dockerfile is a text file with instructions for building an image. An image is the packaged result of those instructions, a read-only template with the files and settings an application needs. A container is a running instance of an image, which adds a thin writable layer on top. A registry stores images so that other machines can download them. The notes' cooking analogy works well: the Dockerfile is the recipe, the image is the frozen meal prepared from it, the container is the meal being served, and the registry is the grocery store. One small correction to the notes is that an image is read-only only in the sense that its layers do not change. Each container gets its own writable layer, and changes made there disappear when the container is removed unless they are stored in a volume.

### Background: how it was before

Before Docker, applications were distributed as archives, installation scripts, or machine images. Each approach was hard to version, and it was difficult to see what was inside. Docker's key idea was to combine a build recipe, a layered image format, and a registry into one workflow, which made images easy to share and inspect. The Docker Hub public registry launched in 2013 along with the project. The Open Container Initiative, founded in 2015, standardized the image format and the runtime specification, so images built with one tool can run on another.

### How it works internally

When you run docker build, the client sends a build context to the daemon, which executes each instruction in order. Each instruction that changes the filesystem creates a layer identified by a hash of its content. The image is described by a manifest that lists its layers and configuration, and each layer is stored by its content hash, which is why identical layers are shared between images. When you run docker run, the daemon creates a container from the image, adds a writable layer using copy-on-write storage, and starts the process defined by the image. Pushing and pulling uses the registry API, which transfers only the layers the other side does not have.

Tags and digests are an important distinction. A tag such as order-service:1.0 can be moved to a different image later, while a digest such as sha256 followed by a long hash cannot. Production systems should deploy by digest or by an immutable tag, not by latest.

### Advantages and disadvantages

The Dockerfile makes builds reproducible when pinned versions are used. Layered images make storage and transfer efficient. Registries provide a central place to scan, sign, and control images. The drawbacks include image sprawl, where old images accumulate and cost storage. Tags can be confusing, especially latest. Registries need access control, because an image can contain secrets or vulnerable libraries. Users often forget that a container's writable layer is temporary.

### Real-world examples

A continuous integration pipeline builds the order service, tags the image with the Git commit hash, and pushes it to Amazon ECR. The staging environment pulls that exact digest, and production later pulls the same digest after testing, which means the code that was tested is the code that runs. A developer runs a container from the same image to reproduce a production issue. When a vulnerability is found in a base image, the team rebuilds and redeploys each service that uses it.

### Alternatives and current industry practice

Amazon ECR, Azure Container Registry, Google Artifact Registry, Harbor, and GitHub Container Registry all store images using the same standard. Cloud Native Buildpacks produce OCI images without a Dockerfile. Current industry practice is to build once, tag immutably, sign images with tools such as cosign, record a software bill of materials, and promote the same image through every environment.

# Section 2: Dockerizing a Spring Boot Application

## Q4: How do you write a Dockerfile for a Spring Boot application?

### In simple terms

A Dockerfile for a Spring Boot application describes three things. It chooses a base image that already has a Java runtime, it copies the application's JAR file into the image, and it tells Docker how to start that JAR. The simplest version has four lines. A multi-stage version separates the build from the runtime, so the final image contains only what the application needs to run. The notes' multi-stage example demonstrates the idea well, but it has a few issues that matter in practice, which are covered below.

### Background: how it was before

Early containerized builds often used one large image that contained the JDK, Maven, the source code, and the application. That image was heavy, slow to transfer, and full of tools that attackers could use. Some teams built the application on the host and copied the JAR into a simple image, which worked but made builds depend on the developer's machine. Docker introduced multi-stage builds in version 17.05 in 2017, which allowed a single Dockerfile to build in one image and copy only the result into another.

### How it works internally

Each FROM instruction starts a new stage. COPY --from copies files between stages, and only the final stage becomes the image. The ENTRYPOINT in exec form, written as a JSON array, makes the Java process the main process of the container, so it receives signals such as SIGTERM directly. That matters because Spring Boot's graceful shutdown depends on receiving that signal.

The notes contain several corrections. The example installs Maven with apk every time the build runs, which is slow and cannot be cached well. A better approach uses an official Maven image or the Maven wrapper, and it copies pom.xml and runs a dependency download before copying the source code. That way, a change to the source does not force every dependency to download again. The example's COPY --from=builder /app/target/*.jar pattern can match more than one file if the build produces a plain JAR as well, so the file name should be controlled. The version number in the basic example is also hardcoded, which breaks on each release.

A corrected multi-stage Dockerfile looks like this:

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder /app/target/order-service.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

The final image uses the JRE and runs as a non-root user. The artifact name is set explicitly, which avoids surprises.

### Advantages and disadvantages

Multi-stage builds shrink the final image, remove build tools from production, and let the build run the same way on any machine. The disadvantages are that the build takes longer if caching is not set up, Alpine's C library can cause subtle differences with native code and DNS resolution, and debugging a failing build requires understanding both stages. A Dockerfile that is copied from another project often contains stale choices.

### Real-world examples

A team's first image for the order service was 480 MB and took four minutes to build. After moving the Maven build into a separate stage, copying the dependency layer first, and removing build tools from the final image, the image dropped to about 130 MB, and rebuilds for code-only changes finished in under a minute. A security scan also became cleaner because the final image had far fewer packages.

### Alternatives and current industry practice

Jib, from Google, builds optimized images directly from Maven or Gradle without a Dockerfile, and it creates layers for dependencies automatically. Spring Boot's build-image goal uses Cloud Native Buildpacks, which create secure, reproducible images with sensible defaults. Distroless and Chainguard images provide minimal runtimes without shells or package managers. Current practice is to use multi-stage builds with BuildKit caching, non-root users, pinned base images, and a software bill of materials.

## Q5: What are Docker layers, and how do they affect image size and build speed?

### In simple terms

A Docker image is a stack of layers. Each Dockerfile instruction that changes files creates a layer, and layers are reused when their inputs have not changed. Think of it like a stack of transparent sheets. The bottom sheets are the base operating system and Java runtime, and the top sheets are your code. If you change only the top sheet, Docker keeps the sheets below it. The order of instructions decides how often each layer must be rebuilt. Rarely changing instructions belong near the top of the Dockerfile, and frequently changing ones belong near the bottom.

### Background: how it was before

Early Docker versions used union filesystems such as AUFS to combine layers, and later versions moved to OverlayFS, which is now the default on most Linux systems. Spring Boot's executable JAR made build speed worse for a while, because a single 100 MB JAR changed on every build, so the whole layer was copied and pushed again. Teams found that a Spring Boot application with many dependencies rebuilt slowly even for small changes. Spring Boot 2.3 introduced layered JARs in 2020, which split the application into layers that change at different rates.

### How it works internally

Each layer is stored as a set of files that were added, changed, or deleted. Docker decides whether a cached layer can be reused by comparing the instruction and the contents of any files it copies. A change to one copied file invalidates the cache for that instruction and every instruction after it. This is why the order of instructions matters so much.

Layers also have a subtle trap. If a file is created in one RUN instruction and deleted in a later one, the file still exists in the earlier layer, and the image remains large. Secrets are even more dangerous. A password written in one layer and removed in the next can still be recovered from the image history. Combining steps into one RUN instruction, or using BuildKit's secret mounts, avoids these problems.

The notes' layered JAR example is useful, but two details need correcting. Extracting the layers requires running the JAR with the layertools mode. The launcher class name depends on the Spring Boot version. Starting with Spring Boot 3.2, the launcher is org.springframework.boot.loader.launch.JarLauncher, and earlier versions use org.springframework.boot.loader.JarLauncher. A corrected Dockerfile for the extracted layers would look like this:

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/extracted/dependencies/ ./
COPY --from=builder /app/target/extracted/spring-boot-loader/ ./
COPY --from=builder /app/target/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/target/extracted/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

### Advantages and disadvantages

Layer caching makes rebuilds fast, and shared base layers save storage and pull time across many images. The drawbacks are that many layers can slow pulls, deleted files still occupy space in earlier layers, and secrets written to a layer are hard to remove. Cache invalidation can also surprise people, especially when a COPY instruction includes more files than intended.

### Real-world examples

A team's build used to re-copy the full application JAR on every commit, taking three minutes to push the image to a registry. After switching to layered JARs, only the application layer changed for most commits, and the push finished in about ten seconds. In another case, a developer committed an API key in a RUN instruction and then deleted it in the next line. The key remained in the image history, and the team had to rotate it and rebuild the image with BuildKit secret mounts.

### Alternatives and current industry practice

Spring Boot's build-image handles layering automatically with buildpacks. Jib organizes layers by dependency change frequency. BuildKit cache mounts keep Maven's local repository between builds without adding it to the image, and remote cache backends share cache across CI runners. Teams use tools such as dive to inspect layers and find wasted space, and they avoid secrets in build arguments and layers.

## Q6: What is the difference between CMD and ENTRYPOINT in a Dockerfile?

### In simple terms

ENTRYPOINT names the program that a container runs. CMD provides default arguments for that program, and those arguments can be replaced when the container starts. For a Spring Boot image, the JVM command belongs in ENTRYPOINT, because the image is always meant to run that application. Default options such as a profile belong in CMD, so someone can override them without rebuilding the image. The notes' example is correct, with one important addition. Passing a profile on the command line works, but environment variables are usually a better choice for Spring Boot configuration, as discussed in Q7.

### Background: how it was before

Early container images often used a single startup command, and users who wanted to run something else had to override the whole command. Docker later separated the entrypoint from the default arguments, which made images behave more like command-line tools. Kubernetes adopted similar terminology. Its command field maps to ENTRYPOINT, and its args field maps to CMD, which sometimes confuses people who move between the two systems.

### How it works internally

Docker computes the final command as ENTRYPOINT followed by CMD, unless you override them. If the user runs docker run with arguments, those arguments replace CMD but leave ENTRYPOINT in place. If the user passes --entrypoint, the entrypoint is replaced entirely. There are two forms. The exec form, written as a JSON array, starts the program directly. The shell form, written as plain text, wraps the command in /bin/sh -c. With the shell form, the shell becomes process ID 1, and it does not forward signals to Java, so Spring Boot's graceful shutdown never starts. Using the exec form avoids that problem.

### Advantages and disadvantages

The separation gives users a clean way to change behavior without changing the image. It also makes images work like command-line tools, where one image can serve several roles. The drawbacks are confusion when people append arguments and accidentally replace CMD, and surprises when someone runs a debugging shell command that the entrypoint tries to interpret as a Java argument.

### Real-world examples

A batch-processing image uses ENTRYPOINT with the Java command, and CMD names the job to run. A scheduler runs docker run image --job=cleanup nightly, and another team runs --job=report on demand. A support engineer inspects a misbehaving container with docker run -it --entrypoint sh image, which opens a shell instead of starting the application. Kubernetes manifests use the same override idea when a team needs to run a one-time migration from the same image.

### Alternatives and current industry practice

Kubernetes command and args override the image's ENTRYPOINT and CMD for each pod. Some teams use a small init process such as tini or Docker's --init option, which reaps zombie processes and forwards signals when the application does not handle them properly. Current practice is to use the exec form, keep ENTRYPOINT stable, set defaults with environment variables where possible, and document overrides for operators.

## Q7: How do you pass environment-specific configuration to a Spring Boot Docker container?

### In simple terms

The same image should run in development, staging, and production, and only its configuration should change. Spring Boot reads configuration from several places, and Docker can supply values as environment variables, as files mounted into the container, or as values from an orchestrator. The notes give three valid methods. The important rule is that secrets should not be written into the image or typed into a command line where they become visible to others.

### Background: how it was before

Traditional Java deployments packaged configuration inside the WAR file, so each environment needed its own build. Some teams used properties files on the server, which drifted over time. The twelve-factor app methodology, published around 2011, recommended storing configuration in environment variables. Spring Boot supports that model through relaxed binding, where an environment variable such as SPRING_DATASOURCE_URL maps to the property spring.datasource.url. Kubernetes introduced ConfigMaps and Secrets, and HashiCorp Vault brought centralized secret management to many organizations.

### How it works internally

Spring Boot combines property sources in a fixed order. Command-line arguments are the highest priority, followed by environment variables, then profile-specific files such as application-prod.yml, and finally the default application.yml. The notes' placeholder syntax, such as ${DB_HOST}, reads an environment variable and inserts it into the configuration. Docker stores environment variables in the container configuration, so anyone who can inspect the container can read them with docker inspect. That makes environment variables suitable for ordinary settings but risky for secrets unless the host is tightly controlled.

The notes also contain a security issue. The example passes DB_PASSWORD=secret directly on the command line. That value can appear in shell history, in process listings, and in container metadata. A safer approach reads the secret from a file mounted into the container. Spring Boot 2.4 and later supports configuration trees, which read each file in a directory as a property, so a Docker or Kubernetes secret mounted at /run/secrets can be consumed directly. A related trap is ARG in a Dockerfile. Build arguments are stored in the image history, so they should never carry passwords.

### Advantages and disadvantages

Environment variables are simple, portable, and supported by every platform. Mounted files keep secrets out of metadata and command lines, and they can be rotated without rebuilding the image. The disadvantages include environment variables being visible to anyone with access to the container runtime, and mounted files needing careful permissions. Multiple configuration sources can make it hard to know which value won, so teams should document their precedence.

### Real-world examples

A team runs the order service with the same image in three environments, passing only the profile, database host, and feature flags as environment variables. The database password comes from AWS Secrets Manager through the task definition in ECS or through the External Secrets Operator in Kubernetes. A developer who needs local settings mounts a file that is excluded from version control. An audit finds that a CI job had passed a token as a build argument, and the team removes it, rotates the token, and rebuilds the images.

### Alternatives and current industry practice

Spring Cloud Config, HashiCorp Vault agents, cloud secret managers, Kubernetes Secrets with encryption at rest, Docker Swarm secrets, and SOPS for encrypted files in Git all play roles in production. Current practice keeps non-secret configuration in environment variables or config maps, keeps secrets in managed stores with rotation, injects them at runtime, and never bakes them into images or logs.

## Q8: What is Docker Compose, and when do you use it?

### In simple terms

Docker Compose describes a group of containers in one YAML file and starts them together. Your order service, PostgreSQL, and Kafka can be defined once, connected on a shared network, and started with a single command. Compose is for local development, automated testing, and small environments where one host is enough. It is not an orchestrator for production clusters. The notes' example shows the idea well, but it needs several corrections before it will run as written.

### Background: how it was before

Before Compose, developers started each container with a long docker run command, often copied from a document. Starting a full environment meant opening several terminals and remembering the order and the network names. Some teams used Vagrant to create virtual machines for development, which was heavier. Fig, created in 2013 and acquired by Docker in 2015, introduced the YAML approach. Docker later renamed it Compose and rewrote it in Go, shipping it as a plugin, so the modern command is docker compose rather than the older docker-compose.

### How it works internally

Compose reads the file, creates a project network, creates named volumes, and creates containers for each service. Each service name becomes a DNS name on that network, so order-service can reach postgres by name. The depends_on setting controls startup order, and combined with a healthcheck, it can wait until PostgreSQL accepts connections. Compose does not restart the application if it starts before the database is ready, so applications still need retry logic.

The notes contain several corrections. The top-level version key is obsolete in the Compose specification and is ignored by modern versions. The command docker-compose refers to the older standalone tool, which reached end of life in 2023, so the newer docker compose command is the one to use. The Kafka service as written will not start. The Confluent image needs additional settings, such as a node ID, process roles, a controller quorum, and listener definitions for either KRaft mode or ZooKeeper. The Spring Boot environment variable names also need to follow relaxed binding, so KAFKA_BOOTSTRAP_SERVERS should be SPRING_KAFKA_BOOTSTRAP_SERVERS if it is meant to configure Spring Kafka directly.

### Advantages and disadvantages

Compose makes a complete local environment reproducible and easy to share, and the file itself documents the system's dependencies. It is fast for integration testing in continuous integration. The disadvantages are that it runs on one host, has no self-healing across machines, has no rolling deployments, and can behave differently from Kubernetes. Compose files also grow hard to maintain if they are copied across projects without review.

### Real-world examples

A developer starts the order service, PostgreSQL, and Kafka with one command, runs the test suite against them, and stops everything at the end of the day. A continuous integration job brings up the same stack to run end-to-end tests before merging. A small internal tool runs on a single virtual machine using Compose, with a documented restart procedure, because the traffic is light and a cluster would be excessive.

### Alternatives and current industry practice

Testcontainers is a Java library that starts containers from JUnit tests, which is often better than a shared Compose stack because each test gets an isolated database. Tilt and Skaffold automate inner-loop development against Kubernetes. kind and minikube run local Kubernetes clusters. Dev Containers standardize development environments in editors. Current practice uses Testcontainers for integration tests, Compose for shared local stacks, and Kubernetes for production.

# Section 3: Docker Internal Working and Advanced

## Q9: How does Docker networking work, and how do containers communicate?

### In simple terms

Every container gets a network connection, and Docker decides how that connection behaves. On a user-defined bridge network, containers can find each other by name and talk over private addresses. Host networking lets a container use the host's network directly. Overlay networks connect containers across several machines. The notes correctly list the main network types, but one important correction matters. On the default bridge network, containers cannot resolve each other by name. Name-based discovery requires a user-defined network, which Docker Compose creates automatically for each project. That is why the notes' example works in Compose but would fail on the default network.

### Background: how it was before

Early Docker networking used a single bridge called docker0 and port mapping with iptables. Containers found each other by IP address or by linking containers with the --link option, which was clumsy and has been deprecated. Docker 1.10 in 2016 introduced embedded DNS for user-defined networks, which made service names practical. Before containers, applications on a single server used localhost, and multi-server systems used hardcoded IP addresses or configuration files that operators maintained by hand.

### How it works internally

For a bridge network, Docker creates a Linux bridge on the host and a virtual Ethernet pair for each container, with one end inside the container and the other attached to the bridge. Outbound traffic uses network address translation through iptables. When a port is published, such as 8081 to 8080, Docker adds a rule that rewrites incoming traffic on the host port to the container port. Containers on the same network reach each other on the container port, not the published host port. That detail is easy to miss. The order service should connect to postgres:5432, not to a host-mapped port.

For user-defined networks, Docker runs an embedded DNS server at 127.0.0.11 inside each container. It resolves service and container names to their addresses on that network. The host network skips the namespace separation, so the container uses the host's interfaces directly. This is fast, but it works differently on Docker Desktop, where Linux runs in a virtual machine. Overlay networks in Swarm mode use VXLAN to encapsulate traffic between hosts and a gossip-based control plane to share addresses.

### Advantages and disadvantages

Name-based discovery makes configuration simple and portable. Separate networks can segment applications, so a database is reachable only from the services that need it. Bridge networks work without special hardware. The disadvantages are that the default bridge network lacks name resolution, host networking offers less isolation, and overlay networks add operational complexity. Network address translation adds a small performance cost, and debugging connectivity requires knowing which address and port apply in each situation.

### Real-world examples

A team places the frontend proxy and the API on a frontend network, and the API and database on a backend network. The database is unreachable from the frontend even if a container there is compromised. A developer runs a diagnostic container on the same network to test connectivity to postgres by name, which confirms that DNS is working before blaming the application. A performance team uses host networking for a latency-sensitive component on a Linux server, and accepts the reduced isolation.

### Alternatives and current industry practice

Kubernetes uses a different model, where each pod gets its own IP address and CoreDNS resolves service names. Container network interface plugins such as Calico, Cilium, and the AWS VPC CNI implement that model. Service meshes add encryption and identity between services. Podman uses netavark for similar functions without a daemon. Current practice divides networks by trust level, avoids publishing ports unnecessarily, and uses network policies to limit traffic.

## Q10: What are common Docker best practices for Spring Boot production images?

### In simple terms

A production image should be small, predictable, secure, and easy to operate. It should use a known base image, run as a non-root user, handle shutdown signals correctly, and respect the memory limits of its container. It should be scanned for vulnerabilities and should not contain build tools or secrets. The notes list the right practices. Several of them need precise corrections, and each one has trade-offs that teams should understand before applying it.

### Background: how it was before

Early Docker images often ran as root because that was the default, and many examples on the internet used large base images with everything installed. Teams deployed with the latest tag, which meant the same Dockerfile could produce different results on different days. Java applications had a particular problem. Before JDK 8u191 and JDK 10, the JVM did not understand container memory limits and sized its heap based on the host's memory, so containers were killed by the operating system's out-of-memory handler. Those problems shaped many of the practices in use today.

### How it works internally

The notes recommend eclipse-temurin:17-jre-alpine and warn against openjdk:17. The correction is that the official openjdk images were deprecated, so new images should use Eclipse Temurin or another maintained distribution. For memory, the notes' recommendation of MaxRAMPercentage is correct for modern JVMs, which read cgroup limits. The default percentage is 25 percent, so setting 75 percent leaves room for metaspace, thread stacks, and native memory. The heap is only part of a Java process's memory, so a heap that uses the entire container limit will still be killed.

Running as a non-root user limits the damage from a compromised application, and it works with ports above 1024. Docker supports rootless mode for the daemon as well. The notes' HEALTHCHECK instruction works with Docker and Compose, but Kubernetes ignores it and uses its own probes, so Spring Boot's liveness and readiness endpoints should be configured in the deployment manifest for Kubernetes.

For vulnerability scanning, Docker Scout provides a command for listing CVEs in an image. The notes write the subcommand as cve, which is usually written as cves in current documentation. Other scanners such as Trivy and Grype are common in pipelines.

Additional practices complete the picture. Logs should go to standard output, and the application should not write logs inside the container. A read-only root filesystem prevents unexpected writes. Dropping Linux capabilities that the application does not need reduces privilege. Resource requests and limits should be set in the orchestrator.

### Advantages and disadvantages

These practices reduce risk, speed up incident response, and make behavior predictable. The trade-offs are real. Pinned versions require regular updates. A non-root user may fail when the application writes to directories owned by root, which needs careful permission setup. Health checks that are too aggressive can restart a healthy but slow service. Vulnerability scanners report false positives and unfixed issues, which can overwhelm teams without prioritization.

### Real-world examples

A service was repeatedly killed by the operating system with an OOM error because its heap was set to a fixed size equal to the container limit. Switching to a percentage-based setting fixed the problem. A team pinned its base image and discovered later that an unexpected update had changed the default timezone, which caused a reporting bug. Running as a non-root user limited the damage when a dependency vulnerability was exploited, because the attacker could not install tools or modify system files.

### Alternatives and current industry practice

Distroless and Chainguard images provide runtime images without shells or package managers, which reduces attack surface. Rootless Podman, gVisor, seccomp profiles, and Kubernetes Pod Security Standards add further protection. Current practice includes pinning by digest, generating software bills of materials, signing images with cosign, linting Dockerfiles with hadolint, scanning in continuous integration, and setting resource limits and security contexts in the deployment.