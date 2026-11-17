Section 1: Docker Fundamentals — Why Do We Need It?
Q1. What is Docker and why do we need it for Spring Boot applications?
Expected Answer:Docker is a containerization platform that packages your application along with all its dependencies (JDK, libraries, configs) into a lightweight, portable unit called a container.The classic problem — "It works on my machine":
Developer's machine:  Java 17, Maven 3.9, PostgreSQL 15   → App works ✓
QA server:            Java 11, Maven 3.6, PostgreSQL 13   → App fails ✗
Production server:    Java 17, Maven 3.9, MySQL 8          → Different DB ✗
Docker solves this:
Docker Container = Your App + Java 17 + All Dependencies
→ Runs identically on developer's laptop, QA, staging, and production
Why Docker for Spring Boot:
Consistency — Same container runs everywhere.
Isolation — Each microservice runs in its own container, no conflicts.
Fast startup — Containers start in seconds (vs. minutes for VMs).
Scalability — Spin up 10 copies of the same container instantly.
CI/CD friendly — Build once, deploy anywhere.

Q2. What is the difference between a Container and a Virtual Machine?
Expected Answer:
Virtual Machine:                    Container:
┌─────────────────────┐            ┌─────────────────────┐
│  App A   │  App B   │            │  App A   │  App B   │
│  Libs    │  Libs    │            │  Libs    │  Libs    │
│  Guest OS│  Guest OS│            │  ────────┴────────  │
│  ────────┴────────  │            │  Docker Engine      │
│  Hypervisor         │            │  Host OS            │
│  Host OS            │            │  Hardware           │
│  Hardware           │            └─────────────────────┘
└─────────────────────┘
Aspect
Virtual Machine
Container
Size
GBs (includes full OS)
MBs (shares host OS kernel)
Startup
Minutes
Seconds
Isolation
Full (separate OS)
Process-level (shared kernel)
Performance
Overhead from hypervisor
Near-native performance
Portability
Heavy to move
Lightweight, easily portable
Resource usage
Heavy
Lightweight

Analogy: A VM is like renting an entire apartment (with its own kitchen, bathroom). A container is like renting a room in a shared house (you share the kitchen/bathroom = shared OS kernel).
Q3. Explain these Docker terms: Image, Container, Dockerfile, Registry.
Expected Answer:
Term
What It Is
Analogy
Dockerfile
A recipe/script with instructions to build an image
A cooking recipe
Image
A read-only template built from a Dockerfile
A frozen meal prepared from the recipe
Container
A running instance of an image
The meal being served and eaten
Registry
A storage for images (Docker Hub, ECR, GCR)
A grocery store that stocks frozen meals

Dockerfile  →  docker build  →  Image  →  docker run  →  Container
↕
Docker Registry
(push / pull)
You can run multiple containers from the same image, just like cooking multiple meals from the same recipe.
Section 2: Dockerizing a Spring Boot Application
Q4. How do you write a Dockerfile for a Spring Boot application?
Expected Answer:Basic Dockerfile:
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/order-service-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
Optimized Multi-Stage Dockerfile (build + run):
# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn clean package -DskipTests
# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
Why multi-stage:
Build stage has JDK + Maven (~400MB). Run stage only has JRE (~100MB).
Final image is much smaller — no compiler, no source code, no build tools.
Security — Less surface area for vulnerabilities.

Q5. What are Docker layers? How do they affect image size and build speed?
Expected Answer:Each instruction in a Dockerfile (FROM, COPY, RUN) creates a layer. Layers are cached — if a layer hasn't changed, Docker reuses it from cache.
FROM eclipse-temurin:17-jre-alpine     # Layer 1 (cached if base image unchanged)
WORKDIR /app                            # Layer 2 (cached)
COPY target/app.jar app.jar             # Layer 3 (rebuilt only when JAR changes)
ENTRYPOINT ["java", "-jar", "app.jar"]  # Layer 4 (cached)
Best practices for smaller, faster builds:
Put rarely changing instructions first — base image, dependencies.
Put frequently changing instructions last — application code.
Use .dockerignore — Exclude unnecessary files (.git, target/, node_modules/).
Layered JAR optimization (Spring Boot 2.3+):
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/extracted/dependencies/ ./
COPY --from=builder /app/target/extracted/spring-boot-loader/ ./
COPY --from=builder /app/target/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/target/extracted/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
This splits the JAR into 4 layers. Dependencies (which rarely change) are cached separately from your application code (which changes frequently). Result: faster rebuilds.
Q6. What is the difference between CMD and ENTRYPOINT in a Dockerfile?
Expected Answer:


ENTRYPOINT
CMD
Purpose
Defines the main executable
Provides default arguments
Override
Not easily overridden (use --entrypoint)
Easily overridden by appending args to docker run
Best for
The command that should always run
Default args that users might want to change

Typical Spring Boot pattern:
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--spring.profiles.active=prod"]
docker run myapp → runs java -jar app.jar --spring.profiles.active=prod
docker run myapp --spring.profiles.active=dev → runs java -jar app.jar --spring.profiles.active=dev (CMD is replaced)

Q7. How do you pass environment-specific configuration to a Spring Boot Docker container?
Expected Answer:Method 1: Environment variables
docker run -e SPRING_PROFILES_ACTIVE=prod \
-e DB_HOST=prod-db.example.com \
-e DB_PASSWORD=secret \
myapp:1.0
In application.yml:
spring:
datasource:
url: jdbc:postgresql://${DB_HOST}:5432/orders
password: ${DB_PASSWORD}
Method 2: Mount external config file
docker run -v /etc/myapp/application-prod.yml:/app/config/application-prod.yml \
-e SPRING_PROFILES_ACTIVE=prod \
myapp:1.0
Method 3: Docker Compose environment
services:
order-service:
image: myapp:1.0
environment:
- SPRING_PROFILES_ACTIVE=prod
- DB_HOST=postgres
env_file:
- .env.prod
Best practice: Never bake secrets into images. Use environment variables, Docker secrets, or Vault.
Q8. What is Docker Compose and when do you use it?
Expected Answer:Docker Compose lets you define and run multi-container applications with a single YAML file. Instead of running multiple docker run commands, you define everything in docker-compose.yml.
version: '3.8'
services:
order-service:
build: ./order-service
ports:
- "8081:8080"
environment:
- SPRING_PROFILES_ACTIVE=local
- DB_HOST=postgres
- KAFKA_BOOTSTRAP_SERVERS=kafka:9092
depends_on:
postgres:
condition: service_healthy
kafka:
condition: service_started
postgres:
image: postgres:16-alpine
environment:
POSTGRES_DB: orders
POSTGRES_USER: admin
POSTGRES_PASSWORD: secret
ports:
- "5432:5432"
volumes:
- pgdata:/var/lib/postgresql/data
healthcheck:
test: ["CMD-SHELL", "pg_isready -U admin"]
interval: 5s
timeout: 3s
retries: 5
kafka:
image: confluentinc/cp-kafka:7.5.0
ports:
- "9092:9092"
environment:
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
volumes:
pgdata:
docker-compose up -d     # Start all services
docker-compose down      # Stop all services
docker-compose logs -f   # Follow logs
When to use: Local development, integration testing, spinning up the full stack on one machine. NOT for production — use Kubernetes for that.
Section 3: Docker — Internal Working & Advanced
Q9. How does Docker networking work? How do containers communicate?
Expected Answer:Docker provides several network types:
Network Type
Description
Use Case
bridge (default)
Containers get private IPs on an isolated network
Single-host, containers talk via container name
host
Container shares the host's network directly
Performance-critical, no port mapping needed
none
No networking
Completely isolated containers
overlay
Spans multiple Docker hosts
Docker Swarm, multi-host communication

In Docker Compose, service names act as DNS hostnames:
services:
order-service:
environment:
- DB_HOST=postgres         # "postgres" resolves to the postgres container
- KAFKA=kafka:9092         # "kafka" resolves to the kafka container
postgres:
image: postgres:16
order-service can reach postgres by hostname postgres — Docker's embedded DNS handles the resolution.
Q10. What are common Docker best practices for Spring Boot production images?
Expected Answer:
Use a minimal base image — eclipse-temurin:17-jre-alpine (~100MB) instead of openjdk:17 (~400MB).
Multi-stage builds — Build with JDK, run with JRE.
Don't run as root — Create a non-root user:
RUN addgroup -S app && adduser -S app -G app
USER app
Use .dockerignore — Exclude .git, target/, .idea/, *.md.
Pin versions — FROM eclipse-temurin:17.0.9_9-jre-alpine not FROM eclipse-temurin:latest.
Set JVM memory limits — Container memory ≠ JVM memory:
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
Health check in Dockerfile:
HEALTHCHECK --interval=30s --timeout=3s \
CMD wget -q --spider http://localhost:8080/actuator/health || exit 1
Scan for vulnerabilities:
docker scout cve myapp:1.0
