## Q1: What is AWS and why do we need it?

### In simple terms

AWS is a large collection of computing services that Amazon rents out over the internet. You can rent servers, storage, databases, and networking, use them as long as you need them, and pay only for what you consumed. Instead of owning the hardware, you borrow it. Think of electricity: a factory used to build its own power plant, but most factories now plug into the grid, use what they need, and pay the bill. AWS is the grid for computing.

### Background: how it was before

Before cloud computing, a company that wanted a new system had to predict its demand years in advance, buy servers, storage, and networking equipment, install them in a server room or a rented rack in a data center, and hire people to maintain them. Procurement could take weeks or months. If the forecast was too low, the site crashed on launch day. If it was too high, expensive hardware sat idle and lost value. Disaster recovery meant buying a second set of hardware in a second building, which many companies could not afford.

AWS grew out of Amazon's own problem. Amazon had built large internal infrastructure to run its retail business and had learned to automate it. In 2006 it began selling storage (S3) and virtual servers (EC2) to outside developers. The key shift was treating infrastructure as software: resources could be requested through an API instead of a purchase order.

### How it works internally

AWS runs many physical servers inside data centers, grouped into regions and availability zones (covered in Q3). Each physical server runs a hypervisor, software that divides its CPU, memory, and network among several virtual machines. Modern EC2 hosts use the AWS Nitro system, which moves networking and storage work onto dedicated hardware so most of the host's CPU is available to customers. Every action, such as launching a server or creating a bucket, is an API call. That is why you can use the console, the command-line tool, or infrastructure-as-code tools like Terraform and AWS CloudFormation to create and destroy whole environments repeatedly. Usage is metered in the background, and that metering produces the monthly bill.

### Advantages

Large upfront spending becomes a variable operating cost. A team can launch a production environment in minutes, test an idea for a few dollars, and shut it down the same day. Capacity can rise for a product launch and fall afterward. AWS also provides global reach, physical facilities and security investments that a single company could not match, and built-in redundancy that would be expensive to build alone. Small companies get the same infrastructure tooling that large enterprises use.

### Disadvantages and trade-offs

Cloud costs can grow unexpectedly, especially when resources are left running, data transfer charges accumulate, or nobody reviews the bill. Heavy use of proprietary services creates vendor lock-in, which makes a later move expensive. The shared responsibility model means AWS secures the underlying infrastructure, but you still own your configuration, identities, data access, and application security. Many breaches come from misconfiguration rather than from failures on AWS's side. For steady, predictable workloads, owned hardware can be cheaper. Dropbox moved most of its storage off AWS to its own data centers beginning in 2015, after concluding that its scale made owning infrastructure cheaper. Cloud skills are also scarce and costly to hire for.

### Real-world examples

Netflix runs much of its streaming platform on AWS, scaling up for evening peaks and back down overnight. A startup can launch a food-delivery app in a few weeks without buying a single server, then scale it as orders grow. A hospital research team analyzing anonymized imaging data for a six-month study can rent large compute capacity for that period and avoid buying hardware that would sit idle afterward.

### Alternatives and related concepts

The main alternatives are Microsoft Azure and Google Cloud. Private clouds built on OpenStack or VMware give some cloud-like behavior inside your own data center, and colocation rents space and power while you keep owning the hardware. Hybrid options such as AWS Outposts and Azure Arc extend cloud management to on-premises systems. The related service models are IaaS, where you manage the operating system (EC2), PaaS, where you manage only the application (Elastic Beanstalk), and SaaS, where you use a finished product (email, for example).

### Current industry practice

Most large companies use more than one cloud, or a hybrid setup, and they manage infrastructure as code so environments can be reproduced. Cloud cost management, often called FinOps, is a standard discipline, with tagging policies, budgets, and regular reviews. The Well-Architected Framework in Q2 is the checklist most teams use to design cloud systems well.

## Q2: What are the core pillars of AWS Cloud (Well-Architected Framework)?

### In simple terms

The Well-Architected Framework is a set of questions and best practices for judging whether a cloud system is well designed. It has six pillars, and each one is a lens: how easy the system is to run, how safe it is, how well it survives failures, how fast it is, how cheap it is, and how little environmental harm it causes. A good design balances all six. It works like the inspection an architect performs before signing off a building, where plumbing, electrical wiring, fire safety, load capacity, and energy use all get checked.

### Background: how it was before

Early cloud adopters designed systems based on personal experience, so the quality of architecture varied widely between teams. Many learned best practices only after an outage. AWS published the framework in 2015 to capture lessons from its own operations and from thousands of customers. The original set had five pillars, and Sustainability was added as the sixth in 2021, reflecting growing concern about the energy use of computing.

### How the pillars work in practice

Operational Excellence is about running systems safely every day. It favors infrastructure as code so that changes are reviewed and repeatable, small deployments that are easy to roll back, runbooks for known failures, and monitoring through Amazon CloudWatch and AWS CloudTrail. Blameless post-incident reviews feed improvements back into the process.

Security covers identity, detection, infrastructure protection, data protection, and incident response. In practice that means IAM roles with least privilege instead of long-lived access keys, GuardDuty and Security Hub for detection, encryption with AWS KMS, and a response plan agreed on before an incident happens.

Reliability asks how the system behaves when parts fail. It favors spreading workloads across Availability Zones, scaling horizontally, automatic recovery, and tested backups. It also recommends regularly testing recovery, because an untested backup is only a hope.

Performance Efficiency is about choosing resources that match the shape of the workload and checking that they still do. It favors caching with Amazon ElastiCache and Amazon CloudFront, managed or serverless services where they fit, and experiments with instance generations, since the price-performance difference can be large.

Cost Optimization aims to remove waste rather than minimize spending at any cost. It relies on tagging for cost allocation, budgets and anomaly alerts, deleting idle resources such as unattached volumes and forgotten test environments, choosing purchase options (Q5), and right-sizing instances based on measured usage.

Sustainability reduces the environmental impact of workloads. Right-sizing and turning off unused resources cut energy use directly, and managed services often share infrastructure more efficiently than dedicated servers. Region choice can also matter, because the carbon intensity of local grids differs.

### How it works internally

The framework is not software running inside AWS. It is a documented body of guidance, and AWS provides the Well-Architected Tool in the console, which walks a team through the questions for each pillar. A review records which practices are in place, flags high-risk issues, and produces an improvement plan. The pillars also conflict. Running in multiple regions improves reliability but raises cost and data transfer charges, and stricter security controls can add latency. Good architects make those trade-offs explicitly and record them.

### Advantages

The framework gives teams a shared vocabulary and a repeatable review process. It surfaces blind spots, especially the ones that only appear under load or during a failure. It also helps leaders see why investments such as backups and monitoring matter before a crisis.

### Disadvantages and trade-offs

A review can become a box-ticking exercise if it is done only to satisfy a process. Small teams may find full reviews heavy for a simple application. Because the pillars conflict, a review does not produce one correct answer. It produces informed choices, and those still require judgment. Teams also sometimes over-engineer for reliability or performance the business does not need. For example, turning on Multi-AZ for a managed database roughly doubles its instance cost, which is a reliability decision that cost optimization has to accept.

### Real-world examples

Before a retail company launches a new checkout service, its architects run a review. The review finds that backups exist but have never been restored (a Reliability gap), that some engineers hold administrator rights they do not need (a Security gap), and that a nonproduction environment runs at full size all month (a Cost gap). The team fixes each before launch. In another case, a media company adds automated deployments and dashboards after a review shows that most of its incidents came from manual changes, which is an Operational Excellence finding.

### Alternatives and related concepts

Microsoft Azure and Google Cloud publish their own architecture frameworks with similar pillars. Security is often governed by standards such as the CIS Benchmarks, the NIST Cybersecurity Framework, or ISO/IEC 27001. Within AWS, related controls include AWS Config, which checks resource configuration; AWS Control Tower, which sets up multi-account governance; and Service Control Policies, which act as guardrails across accounts.

### Current industry practice

Most organizations run formal architecture reviews before major launches and periodically afterward. Many have moved from manual review to policy-as-code, where rules such as "no public storage buckets" are enforced automatically in deployment pipelines. Sustainability is now reported alongside cost, and right-sizing serves both goals.

## Q3: Explain Regions, Availability Zones, and Edge Locations

### In simple terms

A region is a geographic area, such as Mumbai or Northern Virginia, where AWS runs its infrastructure. An availability zone (AZ) is one or more data centers inside a region with independent power, cooling, and networking. An edge location is a small site close to end users, used mainly by Amazon CloudFront to cache content. The notes' analogy holds up: a region is the state where your data lives, AZs are city blocks that protect it from local disasters, and edge locations are vending machines that put content close to the people who want it.

### Background: how it was before

Early hosting companies ran from single data centers. A power failure, a fire, or a network cut could take a whole service offline, and recovery depended on restoring backups somewhere else. A well-known example is the April 2011 outage in AWS's US East region, where a storage failure took down many popular sites, including Reddit and Foursquare, for hours to days. That event pushed AWS and its customers toward designing across multiple AZs. Content delivery networks existed before AWS, but CloudFront brought edge caching onto the same platform as compute and storage.

### How it works internally

Each AZ has its own power and cooling and is connected to the others in its region by high-bandwidth, low-latency fiber. Services replicate data between AZs in different ways. Amazon RDS Multi-AZ replicates synchronously to a standby, and S3 Standard stores each object across several AZs automatically. Replication between regions is asynchronous, so a regional failover can lose the most recent writes. Edge locations are points of presence where CloudFront caches responses. On a cache miss, the request goes to a regional edge cache and then to your origin, which might be an S3 bucket or a load balancer in a particular region. Your data stays in the region you chose unless you configure replication or a transfer.

### Advantages

A multi-AZ design lets a system survive the loss of a whole data center without manual intervention. Regions let you keep data in a specific country for legal reasons and place compute near users to reduce latency. Edge caching reduces load on origin servers and speeds up page loads worldwide.

### Disadvantages and trade-offs

Traffic between AZs and between regions is billed, so chatty architectures become expensive. Not every service exists in every region, and new services often launch first in a few large regions. Multi-region systems are harder to build: data must be replicated, conflicts resolved, and failover tested, and a regional failover usually takes minutes rather than seconds. Latency between regions is set by physics, which limits designs that need synchronous writes across distant locations.

### Real-world examples

The notes' scenario works well: a user base in India and the US, served from Mumbai and Virginia, with CloudFront caching static assets. Data residency is another example. India's RBI payment data localization directive, issued in 2018, requires payment system data to be stored in India, so a payments company keeps its primary data in an Indian region. A third example is a bank that runs active-passive across two AZs in one region for everyday resilience and uses a second region for disaster recovery.

### Alternatives and related concepts

Azure and Google Cloud use the same region-and-zone model under different names. AWS Local Zones place compute near large cities for latency-sensitive workloads, and AWS Wavelength places compute inside telecom 5G networks. Route 53 health checks and latency-based routing send users to the healthiest or closest endpoint. AWS Global Accelerator provides static IP addresses and routes traffic across the AWS network.

### Current industry practice

Most production workloads run across at least two AZs as a baseline. Critical systems often run in two regions, either active-passive or active-active, with infrastructure defined as code so the second region can be built quickly. Teams track cross-AZ and cross-region transfer costs closely, because those charges are easy to overlook. A correction to the notes: the number of AZs varies by region, edge locations now number several hundred, and AZs are separated by a meaningful distance rather than a fixed number of miles.

## Q4: What is Amazon EC2? Explain instance types and when to use each.

### In simple terms

EC2 (Elastic Compute Cloud) is a virtual computer you rent. You choose its operating system, CPU, memory, storage, and network, and AWS runs it in one of its data centers. An instance type is a menu option that bundles CPU and memory in a particular ratio. General-purpose types balance the two. Compute types have more CPU per gigabyte of memory. Memory types have a lot of RAM. Storage types have fast local disks, and accelerated types add GPUs. It is like choosing a vehicle: a sedan, a delivery van, a truck, or a race car.

### Background: how it was before

Before EC2, a team that needed a server had to buy one, which meant weeks of procurement, a fixed configuration, and guesses about future needs. EC2 launched in 2006 with a few fixed sizes. Over time AWS added families tuned for specific workloads and new processor generations, which is why the names encode several facts. The notes' example, m6i.xlarge, breaks down as follows: m is the general-purpose family, 6 is the generation, i means Intel, and xlarge is the size. For that particular size in the m family, the instance has 4 vCPUs and 16 GiB of memory. Graviton processors use a g suffix, as in m7g.

### How it works internally

Modern instances run on the Nitro system, which handles networking, storage, and security on dedicated hardware. The main processor therefore serves your workload more fully, and the virtualization overhead is small. A vCPU is a logical processor, usually one hyperthread on Intel and AMD chips. On Graviton, each vCPU is a full core. The T family is burstable. An instance earns CPU credits while it is idle and spends them during spikes. When the credits run out, it drops to a baseline percentage of CPU, which is a common and surprising cause of slowdowns in production. Storage-optimized instances provide local NVMe disks that are much faster than network-attached EBS volumes, but the data is lost if the instance stops.

### Advantages

Matching the instance type to the workload usually gives the best price-performance. Graviton-based instances often cost less for the same work, and newer generations usually deliver more performance per dollar. A wide range of families lets one platform run everything from a small web server to a GPU training cluster.

### Disadvantages and trade-offs

A wrong choice wastes money or creates a bottleneck. Burstable instances can throttle unexpectedly under sustained load. Some software needs recompiling for ARM, and some vendor licenses are tied to specific hardware. Old generations are eventually retired, which forces migrations. Because EC2 is infrastructure, you still patch the operating system, manage security groups, and handle scaling yourself, which is more work than with managed services.

### Real-world examples

A video transcoding service runs on the C family because encoding is CPU-bound. A product-catalogue cache runs on the R family so the working set fits in memory. A Cassandra cluster uses I-family instances with local NVMe disks for fast reads and writes. A machine learning team trains models on P-family instances with GPUs, and uses G-family instances for graphics or inference workloads.

### Alternatives and related concepts

Containers on ECS or EKS let you pack several applications onto fewer instances. Lambda and Fargate remove instance management entirely. Other clouds offer comparable options under different names, such as Azure Virtual Machines and Google Compute Engine machine types. Bare-metal instances, such as the .metal sizes, give you a whole physical server for licensing or performance reasons.

### Current industry practice

Teams measure before they choose. They run load tests, watch CloudWatch metrics, and use AWS Compute Optimizer to suggest sizes based on observed usage. Graviton adoption is now common for stateless services because of its price-performance. Many teams run several instance families in one fleet and use Auto Scaling (Q6) to adjust the count, rather than picking one large server.

## Q5: What are EC2 purchasing options?

### In simple terms

The same EC2 instance can cost very different amounts depending on how you pay. On-Demand has no commitment and the highest hourly price. Reserved Instances and Savings Plans trade a one- or three-year commitment for discounts of up to roughly 72 percent. Spot Instances use spare AWS capacity for up to roughly 90 percent off, but AWS can reclaim them with a two-minute warning. Dedicated Hosts give you a physical server, which matters for some software licenses. The notes' hotel, lease, and standby-ticket analogies capture the trade-off well.

### Background: how it was before

Early EC2 had only On-Demand pricing, which was simple but expensive for steady workloads. Reserved Instances arrived in 2009 to reward committed usage, and Spot Instances launched later that year to sell spare capacity. Savings Plans followed in 2019 as a more flexible commitment that applies across instance families. Before cloud pricing, companies paid for hardware whether or not they used it. These options let cost follow demand more closely.

### How it works internally

Discounts are applied at billing time, not at launch. When a Savings Plan or Reserved Instance matches a running instance, AWS applies the discounted rate automatically. A Compute Savings Plan applies to EC2, Fargate, and Lambda usage across families and regions. An EC2 Instance Savings Plan is tied to an instance family in one region but flexible in size. Standard Reserved Instances lock to a family, size, and region, while convertible ones allow changes. Spot capacity is priced per capacity pool and moves with supply and demand. When AWS needs the capacity back, it sends an interruption notice about two minutes before stopping the instance, and the workload can read that notice from the instance metadata service. Dedicated Hosts are physical servers, and they support bring-your-own-license arrangements that count sockets or cores.

### Advantages

On-Demand offers maximum flexibility with no commitment. Savings Plans and Reserved Instances can substantially cut the cost of a steady baseline. Spot is extremely cheap for fault-tolerant work such as batch processing, rendering, data pipelines, and CI runners. Dedicated Hosts help meet licensing and compliance requirements.

### Disadvantages and trade-offs

A commitment is a bet. If usage drops, you still pay for what you reserved. Reserved Instances are less flexible than Savings Plans, and both need ongoing analysis. Spot interruptions are unpredictable, so the workload must checkpoint its progress, retry, or fall back to On-Demand. Dedicated Hosts are expensive and reduce the elasticity that justifies using the cloud. The "up to" discounts depend on term, payment option, and workload, so the average saving is usually lower than the headline figure.

### Real-world examples

The notes' scenario is a good model. A production baseline runs on Savings Plans or Reserved Instances, weekday development runs On-Demand, and a nightly batch job runs on Spot. A rendering studio runs frames on Spot instances and saves progress to Amazon S3 after each frame, so an interruption loses only the current frame. A company that must pay for Windows Server and SQL Server licenses by physical core runs those workloads on Dedicated Hosts.

### Alternatives and related concepts

Azure offers Reserved VM Instances and Spot VMs, and Google Cloud offers committed use discounts and Spot VMs. Third-party tools automate Spot management across providers. Within AWS, Capacity Reservations guarantee capacity in a specific AZ without a long-term commitment. They are useful for launches and disaster recovery, but they do not discount the price on their own.

### Current industry practice

Mature teams layer these options. They cover the predictable baseline with Savings Plans, handle variable load with On-Demand, and send fault-tolerant work to Spot. They review coverage and utilization monthly as part of FinOps practice. Container platforms such as EKS also use Spot nodes with careful interruption handling, since Kubernetes can reschedule pods when a node is reclaimed.

## Q6: What is EC2 Auto Scaling? How does it work internally?

### In simple terms

Auto Scaling changes the number of servers automatically based on demand. You set a minimum, a maximum, and a target, and the system adds instances when load is high and removes them when load falls. The notes' grocery-store analogy is accurate: during a rush you open more checkout lanes, and when the line disappears you close them so you stop paying the extra staff.

### Background: how it was before

Before automatic scaling, operators had two choices. They could buy enough capacity for the peak and waste it the rest of the time, or accept outages during spikes. Some teams wrote scripts that added servers by hand in response to alerts, which was slow and error-prone. Amazon's own retail business needed elastic capacity for seasonal peaks, and AWS launched Auto Scaling as a service in 2009 to make that pattern available to everyone.

### How it works internally

An Auto Scaling group keeps the number of instances between a minimum and a maximum, aiming for a desired count. It launches instances from a launch template, which defines the AMI, instance type, security groups, key pair, and a user-data startup script. Health checks, from EC2 status checks or load balancer health checks, replace instances that fail. Scaling policies drive the count. Target tracking keeps a metric near a value, such as average CPU at 60 percent, and creates the CloudWatch alarms for you. Step scaling adds or removes capacity in increments based on how far a metric is past a threshold. Scheduled actions change capacity at fixed times, and predictive scaling forecasts load from history. New instances warm up before they receive full traffic, and the load balancer sends requests only to instances that pass health checks. When the group scales in, a termination policy chooses which instance to remove, based on factors such as which Availability Zone has the most instances and which instance is oldest. AWS recommends launch templates over the older launch configurations.

### Advantages

Auto Scaling reduces cost by matching capacity to demand, and it improves reliability by replacing failed instances. It handles unplanned spikes and planned events such as sales, and it spreads capacity across multiple AZs.

### Disadvantages and trade-offs

New instances take time to boot, install software, and warm their caches, so a sudden spike can overwhelm the fleet before capacity arrives. Policies that react to the wrong metric cause flapping, where instances are added and removed repeatedly. Applications that store sessions on local disks or assume a fixed number of servers need redesign. Scale-in can interrupt long-running jobs unless lifecycle hooks drain them first. Account quotas can block scale-out at the worst possible moment.

### Real-world examples

The notes' flash-sale scenario works well: the group scales out during the event and back down afterward. A news site scales on requests per target, because breaking-news traffic arrives in bursts. A game studio uses a scheduled action to pre-scale before a known launch, so the fleet is ready before players arrive. A batch-processing fleet scales on queue depth, adding workers as messages accumulate.

### Alternatives and related concepts

Kubernetes has its own scaling tools. The Horizontal Pod Autoscaler adds pods, Karpenter or the Cluster Autoscaler adds nodes, and KEDA scales workloads based on event sources such as queue length. DynamoDB and Aurora offer their own capacity scaling. Lambda and Fargate scale without you managing instances at all.

### Current industry practice

Most teams scale on application-level signals such as request latency, queue depth, or business metrics rather than CPU alone. Predictive and scheduled scaling handle known patterns. Immutable deployments, where a new launch template version replaces instances rather than patching them in place, fit naturally with Auto Scaling. Stateless design, with sessions kept in a cache or database, is now the default expectation.

## Q7: What is AWS Lambda? How is it different from EC2?

### In simple terms

Lambda lets you upload a function, a piece of code that does one job, and AWS runs it each time an event occurs, such as a file upload, an API request, or a message arriving in a queue. You manage no servers, and you pay only for the number of requests and the time the code runs. The notes' microwave analogy works: EC2 is an oven you keep heating all day, and Lambda is a microwave that runs only when you use it. Serverless means you don't manage servers, not that no servers exist.

### Background: how it was before

Before Lambda, event-driven work ran either on servers that sat idle most of the time or on scheduled jobs that polled for work. Thumbnail generation, webhook processing, and nightly cleanup all needed a machine that was always on. AWS introduced Lambda in 2014, and the serverless model spread quickly, with similar offerings from Google and Microsoft soon after.

### How it works internally

When an event arrives, Lambda finds an execution environment for your function. If an idle one exists, it is reused, which is a warm start. Otherwise Lambda creates one. That involves starting a Firecracker microVM, a lightweight virtual machine that AWS built and open-sourced, downloading your code, starting the language runtime, and running your initialization code. That first setup is the cold start. Each environment handles one request at a time, so concurrency equals the number of environments in use. Lambda scales by creating more environments, subject to account and function concurrency limits. Billing counts the number of requests plus the duration multiplied by the memory allocated, rounded to the millisecond. Event sources work differently. S3 and EventBridge invoke functions asynchronously, while Lambda polls SQS through an event source mapping. Idle environments are eventually recycled, and AWS does not guarantee how long they stay warm.

### Advantages

You pay nothing when nothing runs, and scaling is automatic from zero to large volumes. There are no servers to patch. You can build pipelines by chaining events between services, and small functions are easy to deploy and roll back independently.

### Disadvantages and trade-offs

Each invocation has a 15-minute maximum, so long jobs need a different service. Cold starts add latency to user-facing APIs. Mitigations include Provisioned Concurrency, SnapStart for Java, smaller deployment packages, and ARM processors. Functions are stateless, so state must live in other services. Debugging a chain of functions is harder than debugging one application. At sustained high throughput, Lambda can cost more than a well-utilized server. The default account concurrency limit is 1,000 per region, which you can raise, but bursts can be throttled until then. Heavy use also creates lock-in, because triggers and permissions are AWS-specific.

### Real-world examples

The notes' scenario is a classic. An S3 upload triggers a function that creates thumbnail, medium, and large versions, and you pay only for the uploads. Other common uses include processing orders from a queue, running nightly cleanup jobs, and serving low-traffic APIs through API Gateway. A bank might run a fraud check for each card transaction event, scaling automatically during peak shopping hours.

### Alternatives and related concepts

Google Cloud Functions and Cloud Run, and Azure Functions and Azure Container Apps, offer similar models. For longer or heavier work, containers on Fargate or Cloud Run fit better. AWS Step Functions coordinates multi-step workflows, and EventBridge routes events between services.

### Current industry practice

Lambda is widely used as event glue and for bursty, short tasks. It is used less often as the backend for heavy, steady traffic. Teams use AWS Lambda Powertools for structured logging and tracing, AWS X-Ray for distributed tracing, and infrastructure as code for deployments. Good practice includes keeping functions small, setting timeouts and reserved concurrency, and avoiding long synchronous chains.

## Q8: What is Elastic Beanstalk?

### In simple terms

Elastic Beanstalk is a platform that takes your application package, such as a JAR, WAR, or ZIP file, and builds the infrastructure to run it. That infrastructure includes EC2 instances, a load balancer, auto scaling, and monitoring. You focus on the code and its configuration. The notes' general-contractor analogy fits: you hand over the blueprint, the contractor handles the trades, and you can still inspect or change the plumbing later.

### Background: how it was before

Before services like Beanstalk, a team deploying a web application had to set up servers, configure web servers, write deployment scripts, and build load balancing and monitoring by hand or in long infrastructure templates. Heroku, launched in 2007, popularized the simpler idea of pushing code and having the platform run it. Elastic Beanstalk, launched around 2011, brought that model to AWS while keeping access to the underlying resources.

### How it works internally

Beanstalk creates an environment, which is essentially a CloudFormation stack that AWS manages for you. An environment includes an Auto Scaling group of EC2 instances, an Elastic Load Balancer, security groups, CloudWatch alarms, and an S3 bucket that stores application versions. Platform branches cover languages and runtimes such as Java, Node.js, Python, and Docker. Deployment policies control how a new version rolls out: all at once, rolling, rolling with an additional batch, immutable, or blue/green through an environment swap. You can adjust configuration in the console, through saved configurations, or with .ebextensions files in the project.

Beanstalk can also create an RDS database inside the environment. That database's lifecycle is tied to the environment, so production databases are usually created separately and connected by configuration. This is a correction to the notes, which implied RDS is always provisioned.

### Advantages

Beanstalk gets a standard web application running quickly with sensible defaults, applies managed platform updates, and keeps the underlying resources visible. Small teams can run production-grade architecture without becoming infrastructure specialists. When the defaults are not enough, you can drop down to the EC2 settings.

### Disadvantages and trade-offs

The abstraction can leak, and when something breaks you still need to understand EC2, load balancers, and CloudFormation. Customization beyond the platform's model is limited. Platform upgrades can require careful testing. Beanstalk is a poor fit for complex microservice architectures, where ECS or EKS give more control. Beanstalk still runs on EC2, so you pay for the instances, the load balancer, and the other resources it creates.

### Real-world examples

A university builds a course-registration portal on Beanstalk, deploys new versions through the console, and relies on rolling deployments to avoid downtime during registration week. A startup uses it for an MVP to test an idea before moving to containers. An internal reporting tool runs on Beanstalk because the team has no dedicated infrastructure staff.

### Alternatives and related concepts

Newer options cover similar ground. AWS App Runner runs containerized or source-based web services with very little configuration. AWS Amplify targets frontend and full-stack web applications. Lightsail provides simple virtual servers with predictable pricing. Outside AWS, Heroku, Render, Google App Engine, and Azure App Service offer similar platform experiences.

### Current industry practice

Many teams have moved from Beanstalk toward containers on ECS with Fargate, or toward App Runner, because containers give the same artifact in every environment. Beanstalk remains in use for existing applications and for teams that want a simple, managed, well-understood path. The industry rule of thumb is to choose the least complex platform that meets the requirements.

## Q9: ECS vs EKS vs Fargate: What, When, Why?

### In simple terms

Containers package an application with everything it needs to run, so it behaves the same on a laptop and in production. Something has to decide where containers run, restart them when they fail, and scale them. ECS and EKS do that orchestration. ECS is AWS's own orchestrator, and EKS is managed Kubernetes. Fargate is not a third orchestrator. It is a way to run containers without managing the servers underneath, and it works with both ECS and EKS. The notes' shipping analogy fits: the container is the box, ECS or EKS is the dock manager, and Fargate is like handing the port authority your container without caring which ship or crane moves it.

### Background: how it was before

Before containers, applications ran on virtual machines configured by hand or with tools like Chef and Puppet, and deployments were fragile because each server drifted from the others. Docker, released in 2013, made packaging standard. Kubernetes, which Google open-sourced in 2014 based on its internal Borg system, became the leading orchestrator. Amazon announced ECS in 2014, launched Fargate in 2017, and made EKS generally available in 2018.

### How it works internally

In ECS, you define a task definition that describes the containers, their images, CPU, memory, and environment variables. A service keeps the desired number of tasks running and connects them to a load balancer. The control plane is managed by AWS and has no separate charge. Tasks run either on EC2 instances in your cluster, which you manage, or on Fargate, where each task runs inside its own isolated microVM that AWS manages.

In EKS, the Kubernetes control plane, including the API server and its backing store, runs across several AZs and is managed by AWS for a fixed hourly charge per cluster. You add worker capacity as managed node groups on EC2, with Karpenter for dynamic node provisioning, or as Fargate profiles, where pods run on Fargate. Kubernetes objects such as pods, deployments, and services describe the desired state, and controllers keep reality matching that description.

### Advantages

ECS is simpler, has fewer components to learn, and integrates tightly with IAM, load balancing, and service discovery. EKS offers standard Kubernetes, which is portable across clouds and supported by a large ecosystem of tools. Fargate removes the need to manage, patch, and plan capacity for the underlying machines, and each task gets its own isolation boundary.

### Disadvantages and trade-offs

ECS creates some AWS lock-in, and its ecosystem is smaller than Kubernetes'. EKS has a steep learning curve and requires you to keep the cluster and its add-ons upgraded. Fargate costs more per unit of compute than well-utilized EC2 instances, and it has restrictions, such as limits on privileged containers and certain storage options. Fargate tasks also take longer to start than tasks on a warm EC2 instance. Choosing EKS only for its résumé value often adds complexity without benefit.

### Real-world examples

The notes' scenarios are typical. A company with fifty microservices, a team that knows Kubernetes, and a possible future move to another cloud chooses EKS. A startup with three services chooses ECS on Fargate because it has no one to manage servers. A third pattern is a company with steady, large workloads that runs them on ECS with EC2 capacity and Spot instances, which costs less than Fargate at that scale.

### Alternatives and related concepts

Docker Swarm and HashiCorp Nomad are other orchestrators, with smaller adoption today. Azure Kubernetes Service and Google Kubernetes Engine are the equivalents of EKS in other clouds. Google Cloud Run and Azure Container Apps offer serverless containers similar to Fargate, and AWS App Runner covers simple web services.

### Current industry practice

Kubernetes is the de facto standard for large container platforms, and many organizations use GitOps tools such as Argo CD to deploy declaratively. ECS remains popular in AWS-only environments because it is simpler to operate. Fargate is common for spiky or small workloads, while EC2 capacity with Spot remains a cost-efficient choice for large, steady fleets. A correction to the notes: Fargate is a compute option for ECS and EKS, not a separate orchestrator.

## Q10: Explain S3 storage classes. When do you use each?

### In simple terms

S3 stores files, called objects, in buckets. Storage classes control how each object is stored, and each class trades storage price against access speed, retrieval fees, and minimum storage time. Frequently used data belongs in S3 Standard. Data you touch rarely can move to cheaper classes, and archives you almost never need can go to Glacier classes at a very low monthly price, with a delay and a fee when you retrieve them. The notes' filing-cabinet analogy works well: Standard is the desk, Infrequent Access is the cabinet down the hall, and Deep Archive is the warehouse across the state.

### Background: how it was before

S3 launched in 2006 with a single storage class. Organizations that had to keep years of logs, backups, and records bought tape libraries, maintained them, rotated tapes offsite, and managed slow restores. Cheaper S3 classes were added over time. Standard-Infrequent Access arrived in 2015, One Zone-Infrequent Access in 2018, Intelligent-Tiering in 2018, Glacier Deep Archive in 2019, and Glacier Instant Retrieval in 2021. These dates are approximate.

### How it works internally

Standard, Standard-IA, Intelligent-Tiering, and Glacier Instant Retrieval store each object redundantly across at least three AZs. S3 Standard is designed for eleven nines of durability. One Zone-IA keeps data in a single AZ, so it is cheaper but would lose data if that AZ were destroyed. Glacier Flexible Retrieval and Glacier Deep Archive store data for long periods. Retrieval takes minutes to hours for Flexible and 12 to 48 hours for Deep Archive. A restore request creates a temporary readable copy.

Intelligent-Tiering monitors each object's access pattern and moves it between tiers automatically, for a small per-object monitoring fee. Lifecycle rules move objects between classes or delete them based on age. Each class has a minimum storage duration: 30 days for the IA classes, 90 days for the Glacier classes other than Deep Archive, and 180 days for Deep Archive. Deleting earlier is charged for the remaining time. The IA classes also bill a minimum of 128 KB per object.

Approximate list prices from the notes, for us-east-1, are about $0.023 per GB-month for Standard, $0.0125 for Standard-IA, $0.01 for One Zone-IA, $0.004 for Glacier Instant Retrieval, $0.0036 for Glacier Flexible Retrieval, and $0.001 for Deep Archive. Prices change and vary by region.

### Advantages

Archive storage can cost a small fraction of Standard, and lifecycle rules automate the transitions, so nobody has to remember to move files. The Standard classes offer very high durability, and S3 Object Lock supports write-once, read-many retention for compliance.

### Disadvantages and trade-offs

Retrieval fees and delays surprise teams that archive data they later need often. Early deletion charges penalize objects removed before their minimum duration. Many tiny files are inefficient in the IA and Glacier classes because of minimum sizes and per-object transition and request charges. A poorly designed lifecycle rule can cost more than it saves, for example by moving objects that are accessed every few days into a class with retrieval fees. Costs also become harder to predict when access patterns change.

### Real-world examples

Application logs move from Standard to Standard-IA after a month, to Glacier Flexible Retrieval after three months, and are deleted after two years, which matches the notes' lifecycle example. A hospital stores older medical images in Glacier Instant Retrieval, because they are rarely viewed but must open quickly when a patient returns. A financial firm uses S3 Object Lock in compliance mode to keep trade records unchangeable for seven years, which satisfies regulatory retention rules.

### Alternatives and related concepts

Azure Blob Storage offers Hot, Cool, Cold, and Archive tiers, and Google Cloud Storage offers Standard, Nearline, Coldline, and Archive classes, with similar trade-offs. On-premises options include tape libraries and network-attached storage with tiering. Table formats such as Apache Iceberg and columnar files such as Parquet reduce the number of objects, which lowers request and transition costs.

### Current industry practice

Most organizations apply lifecycle policies to every bucket that holds growing data, and Intelligent-Tiering is a common default when access patterns are unknown. S3 Storage Lens helps find cold data and waste. Compliance-sensitive data uses Object Lock and versioning, and critical backups are copied to another region or account to protect against account-level compromise.
# Section 3 (continued): Storage

## Q11: What is the difference between S3, EBS, and EFS?

### In simple terms

All three store data, but each connects to your servers in a different way. S3 is object storage. You store whole files, called objects, in buckets and reach them over HTTP or an API, and no server mounts them as a drive. EBS is block storage. It behaves like a hard disk attached to one EC2 instance, where the operating system formats it and reads and writes small chunks of it. EFS is file storage. It is a shared network folder that many EC2 instances can mount at the same time. The notes' analogies are right: S3 is like uploading to Dropbox in a browser, EBS is the C: drive inside your laptop, and EFS is a shared folder on the office network.

### Background: how it was before

Before cloud storage, a company's files lived on a disk inside a server or on a network-attached storage (NAS) box in the server room. Sharing meant mounting a network drive over NFS or SMB, and growth meant buying a bigger array. Block storage came from storage area networks (SANs), which carved large disk arrays into logical volumes for databases. Object storage grew out of the need to store huge numbers of files over the web without a folder hierarchy. AWS offered all three models as managed services, which removed the hardware purchasing and most of the administration.

### How it works internally

S3 spreads each object across multiple devices in several AZs and identifies it by a key in a flat namespace. "Folders" are only prefixes in key names. Every operation is an HTTP request, so there is no mount point. An EBS volume is a network-attached block device. Its data is replicated within a single AZ, and the instance reaches it over the Nitro networking fabric, which is why a volume can be used only from its own AZ. EFS is a managed NFS service (NFSv4.1) whose file system spans several AZs, and clients mount it over the network. Its throughput modes (bursting, elastic, and provisioned) control how quickly the file system can read and write.

Two corrections to the notes are needed. EBS maximum volume sizes depend on the volume type: gp3 volumes top out at 16 TiB, and io2 Block Express volumes reach 64 TiB. Durability also depends on type: io2 is rated at 99.999 percent, while gp2 and gp3 are rated around 99.8 to 99.9 percent per year. The notes give one figure for all of EBS. The "one EC2 instance only" rule also has an exception. io1 and io2 volumes support Multi-Attach, which lets up to 16 instances in the same AZ attach one volume, though a file system is usually the better choice for shared access.

### Advantages

S3 offers effectively unlimited capacity, very high durability, low cost per GB, and integration with nearly every AWS service. EBS gives the low-latency, random read and write access that databases and operating systems need, and snapshots provide backups. EFS removes capacity planning for shared data, grows and shrinks automatically, and lets many instances work on the same files at once.

### Disadvantages and trade-offs

S3 is not a file system. You cannot edit a few bytes inside an object, and every request has latency and request cost, so millions of tiny objects can be slow and expensive. EBS is tied to one AZ and, in most cases, one instance. If that instance fails, you restore from a snapshot into the same or another AZ. EBS volumes are provisioned in advance and billed in full whether or not they are used. EFS costs more per GB than EBS, has higher latency for small operations than a local disk, and speaks NFS, so Windows workloads need Amazon FSx for Windows File Server instead.

### Real-world examples

The notes' media company is a good model. Raw video lives in S3 because it is cheap and unlimited. Editing workstations run on EC2 with EBS for the operating system and scratch space. EFS holds shared project files so several editors and render nodes see the same folders. Other examples include a PostgreSQL database on io2 volumes for consistent latency, a content site whose uploads go directly to S3, and a machine-learning training fleet that mounts an EFS dataset on every GPU instance.

### Alternatives and related concepts

Instance store provides temporary local NVMe disks that are very fast but lost when the instance stops, which suits caches and scratch data. Amazon FSx for Lustre is a high-performance parallel file system for HPC and ML workloads that can load data from S3 and write results back. In other clouds, the equivalents are Azure Blob Storage, Managed Disks, and Azure Files, and Google Cloud Storage, Persistent Disk, and Filestore.

### Current industry practice

Durable data goes to S3 with lifecycle rules, as covered in Q10. EBS is treated as replaceable, protected by regular snapshots, and AWS Backup applies backup policies across volumes, file systems, and databases. EFS is used when several compute nodes need the same files. The usual rule is to choose by access pattern: whole objects go to S3, blocks for a single machine go to EBS, and shared files go to EFS.

# Section 4: Networking

## Q12: What is a VPC? Explain with a diagram.

### In simple terms

A VPC (Virtual Private Cloud) is your own private network inside AWS. Your servers, databases, and load balancers sit inside it, and you decide what can reach what. You pick the IP address range, divide it into subnets, control routing, and choose which doors connect to the internet. The notes' castle analogy works well. The walls are the VPC, the drawbridge is the internet gateway, the courtyard is the public subnet where web-facing services stand, and the keep is the private subnet where the application and database sit, reachable only through controlled routes.
### Background: how it was before

Before cloud networking, a company's network was physical. Machines were wired to switches, firewalls were appliances at the internet edge, and a new application might require new cables, a new VLAN, and a change request to the network team. Testing a network design meant building a copy of the network. Early AWS offered EC2-Classic, a flat network where all customers' instances shared one address space and isolation came from security groups alone. VPC, launched in 2009, gave each customer its own logically isolated network, with control over addressing and routing. That model is now the only one.

### How it works internally

A VPC is defined by a CIDR block such as 10.0.0.0/16, which provides 65,536 addresses. Each subnet takes a slice of that range, such as 10.0.1.0/24, and lives in exactly one AZ. AWS reserves five addresses in every subnet. A subnet becomes "public" only because its route table sends 0.0.0.0/0 traffic to an internet gateway. Without that route, the subnet is private, regardless of its name. A NAT gateway must sit in a public subnet, and private subnets send outbound traffic to it. Traffic inside the VPC is routed automatically, and security groups and network ACLs filter it (Q13).

A correction to the notes: the database tier should normally span two AZs. RDS Multi-AZ creates a standby in another AZ, which is not the same thing as a read replica, and the diagram should reflect that.

```
 Internet
 |
 [ Internet Gateway ]
 |
 VPC 10.0.0.0/16 ----------|-----------------------------------
 |
 Public subnet 10.0.1.0/24 | route 0.0.0.0/0 -> Internet Gateway
 [ALB] [NAT Gateway] [Bastion host]
 |
 | outbound only (route 0.0.0.0/0 -> NAT Gateway)
 v
 Private app subnet 10.0.2.0/24
 [EC2 app servers] [ECS containers]
 |
 | port 5432 only (security group rule)
 v
 Private DB subnet 10.0.3.0/24 (no route to the internet)
 [RDS primary] <-- synchronous standby in another AZ -->
```

### Advantages

A VPC lets you design network boundaries the way you would in a physical data center, with tiers, filters, and controlled entry points. Isolation by default protects databases from direct internet exposure. You can connect a VPC to your corporate network through VPN or Direct Connect, and you can build the whole layout as code, so it is reviewed and repeatable.

### Disadvantages and trade-offs

Network design is easy to get wrong. CIDR ranges that overlap with corporate or other VPC networks block peering and connectivity later, and changing them is painful. NAT gateways are billed per hour and per gigabyte processed, which surprises teams that route large volumes of traffic through them. Bastion hosts are another weak point, because they need management and open SSH ports. Each additional layer makes troubleshooting harder.

### Real-world examples

A healthcare application places its web tier in public subnets behind a load balancer, its application servers in private subnets, and its patient database in an isolated subnet with no internet route at all. A company connects its VPC to its data center through AWS Site-to-Site VPN, so internal services can talk to cloud services without traffic crossing the public internet. A development team gives each environment its own VPC, which keeps test traffic away from production.

### Alternatives and related concepts

Azure uses virtual networks (VNets) and Google Cloud uses VPC networks, both with similar subnet and routing concepts. Systems Manager Session Manager can replace bastion hosts for shell access, with no open SSH port. Transit Gateway connects many VPCs and on-premises networks through one hub. VPC endpoints and PrivateLink let services reach AWS APIs or each other without traffic leaving the AWS network.

### Current industry practice

Most organizations use a hub-and-spoke or multi-account network design, with shared networking handled centrally and application VPCs connected through Transit Gateway. Zero-trust approaches reduce reliance on network position, so each service also authenticates its callers. Infrastructure as code defines VPCs, route tables, and endpoints, and tools like AWS Config or policy-as-code check for accidental public exposure.

## Q13: Security Group vs NACL: What's the difference?

### In simple terms

Security groups and network ACLs (NACLs) are both firewalls, but they sit at different levels. A security group is attached to an instance, or more precisely to its network interface, and controls what traffic reaches it. A NACL is attached to a subnet and controls traffic crossing the subnet boundary. The main practical difference is that security groups remember connections and NACLs do not. The notes' bouncer and turnstile analogies capture this exactly: the bouncer recognizes you on the way out, while the turnstile needs a separate swipe to exit.

### Background: how it was before

Physical networks used firewall appliances and access control lists on routers. Those were central devices that required specialist configuration, and rules were ordered and matched top to bottom. Cloud providers initially had only a basic model. AWS introduced security groups with EC2 and later added VPC network ACLs for subnet-level control. The two layers reflect a common security principle: defense in depth, where multiple controls each cover for the others.

### How it works internally

A security group holds only allow rules. Every rule is evaluated together, and if any rule allows the traffic, it passes. Because security groups are stateful, return traffic for an allowed connection is permitted automatically. Security groups can reference other security groups as sources, which makes rules like "the app tier may talk to the database tier" stable even when IP addresses change.

A NACL holds numbered allow and deny rules. Rules are evaluated from the lowest number upward, the first match wins, and an implicit deny at the end blocks anything unmatched. NACLs are stateless, so return traffic must be allowed explicitly on the ephemeral port range, commonly 1024 to 65535.

Two corrections to the notes. The default security group denies inbound traffic from outside, but it allows inbound traffic from other members of the same group, so instances in the default group can talk to each other. Also, the default NACL allows everything in both directions, while a custom NACL denies everything until you add rules, which is why new subnets with custom NACLs often appear to be broken.

### Advantages

Security groups are simple to reason about, scale well, and follow the resource, so they still apply when instances move between subnets. NACLs can block specific IP ranges for the entire subnet, which is useful for responding to an attack quickly. Using both layers means one misconfiguration does not expose a resource completely.

### Disadvantages and trade-offs

Security groups cannot express explicit deny, so blocking a single malicious address requires a NACL or a WAF. NACLs are error-prone because a missing ephemeral-port rule silently breaks return traffic. Both have quotas: a NACL direction has a default limit of 20 rules, and security groups limit the number of rules and groups per interface. Troubleshooting requires checking both layers, which is why VPC Flow Logs and Reachability Analyzer are valuable.

### Real-world examples

A web application's load balancer security group allows port 443 from anywhere. The application's security group allows port 8080 only from the load balancer's security group. A database security group allows port 5432 only from the application's security group. A NACL on the database subnet denies all traffic from a range that a threat-intelligence feed flagged, as an extra layer. During an incident, a security team adds an explicit deny to a NACL for a single attacking IP, because security groups cannot block one address.

### Alternatives and related concepts

AWS Network Firewall provides stateful inspection with domain and intrusion-prevention rules at the VPC level. AWS WAF filters web requests at the load balancer or CloudFront by content, such as SQL injection patterns. In other clouds, Azure uses network security groups (which act at both subnet and NIC levels) and Google Cloud uses firewall rules applied by network tags or service accounts.

### Current industry practice

Teams use security groups as the main control and rely on references between groups instead of hard-coded IP addresses. They use NACLs sparingly, mainly for broad denies. Infrastructure as code defines rules, and policy checks flag security groups open to 0.0.0.0/0 on administrative ports such as SSH or RDP. Many organizations are moving toward identity-aware access, so network rules are supplemented by service-level authentication.

## Q14: What is the difference between ALB, NLB, and CLB?

### In simple terms

A load balancer spreads incoming traffic across several servers so no single server is overloaded and failures are hidden from users. ALB (Application Load Balancer) reads HTTP requests and routes them by path, host name, or header, like an intelligent receptionist. NLB (Network Load Balancer) works at the TCP and UDP level, forwarding connections quickly without reading their contents, like a fast sorting machine. CLB (Classic Load Balancer) is the older generation that does both jobs in a more limited way. The notes' table omits CLB, so this section covers all three.

### Background: how it was before

Early web applications used a hardware load balancer, an appliance from vendors such as F5 or Citrix, which required purchase, rack space, and configuration. AWS launched the Classic Load Balancer in 2009 as a managed alternative. It handled basic traffic well but could not route by URL path or host name, which modern microservices need. AWS added the Application Load Balancer in 2016 for Layer 7 routing and the Network Load Balancer in 2017 for extreme performance and static IP addresses. AWS now recommends ALB or NLB for new work and suggests migrating from CLB.

### How it works internally

Each load balancer has a node in every AZ you enable, and its DNS name resolves to those nodes. ALB terminates the client's TLS connection, reads the request, chooses a target from a target group based on rules, and opens a separate connection to that target. Because it understands HTTP, it can also handle WebSockets, gRPC, and authentication through Amazon Cognito or OIDC providers, and it integrates with AWS WAF. NLB forwards TCP or UDP connections using a flow hash of the source and destination addresses and ports, so each connection stays on one target. It preserves the client's source IP address and can offer a static IP address per AZ, which matters for allowlists. CLB works at both layers but cannot route by content, has fewer features, and has no support for many modern targets.

Health checks run continuously against each target. A target that fails is removed from rotation, and connection draining lets in-flight requests finish before a target is deregistered.

### Advantages

ALB makes it possible to run many microservices behind one domain name, with clean routing rules and built-in security integration. NLB handles very high connection rates with very low latency and suits protocols that are not HTTP. Both are fully managed, scale automatically, and span AZs for resilience.

### Disadvantages and trade-offs

ALB adds some latency compared with NLB because it parses every request, and its pricing includes a per-request component that adds up at scale. NLB cannot inspect content, so routing decisions must happen elsewhere, and it offers fewer built-in security features. Both charge for capacity units. CLB is cheaper in some cases, but it lacks features and is being phased out, so keeping it means technical debt.

### Real-world examples

The notes' ALB example is a common pattern. A single domain such as api.myapp.com routes /users to a user service target group, /orders to an order service target group, and /payment to a payment service target group. A multiplayer game uses NLB for UDP traffic because each millisecond of latency is noticeable to players. A trading platform places NLB in front of a fleet that needs static IP addresses for allowlisting by banking partners. A legacy monolith that was launched on CLB is migrated to ALB when the team adds path-based routing.

### Alternatives and related concepts

Gateway Load Balancer operates at Layer 3 and inserts third-party security appliances, such as firewalls, into the traffic path. Outside AWS, Azure offers Application Gateway, Load Balancer, and Front Door, and Google Cloud offers its Application and Network Load Balancers. NGINX, HAProxy, and Envoy are commonly deployed as software load balancers or service-mesh proxies, especially in Kubernetes.

### Current industry practice

Most web applications use ALB, often with ingress controllers in Kubernetes that create ALBs automatically. NLB is chosen for latency-sensitive, non-HTTP, or static-IP needs. Teams increasingly place a CDN such as CloudFront in front of ALB to cache content and absorb attacks, and they use WAF rules to block malicious requests before they reach the application.

## Q15: What is Route 53? What routing policies does it support?

### In simple terms

Route 53 is AWS's DNS service. DNS is the phonebook of the internet: it turns a name such as www.example.com into an IP address. The notes' phonebook analogy is accurate. Route 53 also decides which address to return, which is where routing policies come in. It can return one address, split traffic by weight, send each user to the nearest region, switch to a standby when the primary fails, and more.

### Background: how it was before

Early DNS was managed in plain text files on a few servers, and changes were slow and centralized. Companies ran their own DNS servers, which required careful configuration and constant monitoring. Traffic management across data centers usually relied on expensive global load-balancing appliances. Route 53, launched in 2010, offered a managed, globally distributed DNS service with an API. Its name refers to TCP/UDP port 53, the standard DNS port.

### How it works internally

Route 53 stores records in hosted zones, which can be public (for internet use) or private (for names that resolve only inside a VPC). Records include A and AAAA for IP addresses, CNAME for aliases, and the AWS-specific alias record, which can point at a load balancer or CloudFront distribution at the zone apex, where a standard CNAME is not allowed. Each record has a TTL, the number of seconds resolvers may cache the answer. Health checks run from multiple checker locations around the world, at intervals of 10 or 30 seconds, and mark an endpoint unhealthy after a set number of failures. Routing decisions then use the health results.

A correction to the notes: failover is not instant. The delay is the detection time plus the TTL, because resolvers and clients may keep using the old IP address until their cached answer expires. Route 53 does not "erase" the IP from clients' memory. Lower TTLs reduce the delay but increase query volume.

The routing policies are as follows. Simple returns one value. Weighted splits traffic by weight, which suits canary releases. Latency-based chooses the region with the lowest measured latency to the user's resolver. Failover returns the secondary record when the primary is unhealthy. Geolocation returns answers based on the user's country or continent, which helps with residency rules. Geoproximity shifts traffic toward or away from a region using a bias value, and it requires Traffic Flow. Multivalue answer returns up to eight healthy records. IP-based routing returns answers based on the client's network CIDR block.

### Advantages

Route 53 is highly available, and AWS publishes a 100 percent availability SLA for it. It integrates directly with AWS resources through alias records, and its policies support blue-green deployments, disaster recovery, and regional compliance without extra infrastructure.

### Disadvantages and trade-offs

DNS-based routing is coarse. It cannot see the request itself, so it cannot route by path or user. Caching makes changes slow to take effect, and some clients ignore TTLs. Latency-based routing estimates the user's location from the resolver, which is not always accurate. Health checks cost money per endpoint, and misconfigured checks can cause unnecessary failovers.

### Real-world examples

A global application uses latency-based routing so users in India reach the Mumbai region and users in the United States reach Virginia. A payments company uses weighted records to send 5 percent of traffic to a new version during a migration, then increases the weight gradually. A regulated service uses geolocation routing so European users always reach servers in an EU region. A company with a primary data center and a disaster-recovery region uses failover routing with health checks on the primary.

### Alternatives and related concepts

Cloudflare, Google Cloud DNS, and Azure DNS provide similar managed DNS, and Azure Traffic Manager and Google Cloud Load Balancing offer global traffic management. Anycast networks announce the same IP address from many places so the nearest location answers. For traffic that needs HTTP-level decisions, a global load balancer or CDN is more suitable than DNS alone.

### Current industry practice

Teams use DNS routing for coarse decisions such as regional failover and migrations, and combine it with application-level routing and health checks. Infrastructure as code manages records, and automated tests confirm failover works, because an untested failover policy often fails when it matters. Many teams also use low TTLs before planned cutovers and raise them afterward to reduce query costs.

# Section 5: Database Services

## Q16: When do you choose RDS vs DynamoDB vs Aurora vs Redshift?

### In simple terms

Each database is built for a different kind of work. RDS and Aurora are relational databases that store data in tables with fixed columns and support joins and transactions, which is ideal for orders, accounts, and inventory. DynamoDB is a key-value and document database that handles huge volumes with very fast lookups by key, which suits shopping carts and session data. Redshift is a data warehouse built to scan and summarize enormous amounts of data for analysis, so it suits reports and dashboards rather than individual transactions. The notes' analogies work well: RDS is a spreadsheet, DynamoDB is a box of sticky notes, and Redshift is a library archive.

ElastiCache, DocumentDB, and Neptune also appear in the notes. ElastiCache is an in-memory cache for fast reads and temporary data, DocumentDB is a MongoDB-compatible document database, and Neptune is a graph database for connected data.

### Background: how it was before

Before managed services, companies ran databases on their own servers. Oracle and SQL Server licenses were expensive, and a relational database was typically the only database. Teams scaled up by buying bigger machines and scaled out only with complex replication and sharding. NoSQL systems such as Amazon's Dynamo paper in 2007 inspired distributed key-value stores. Redshift launched in 2013 to make warehouse-scale analytics affordable, and RDS launched in 2009 as a managed relational service. Each new service targeted a specific pain point.

### How it works internally

RDS runs a standard engine (MySQL, PostgreSQL, MariaDB, Oracle, or SQL Server) on EC2-class instances, with storage on EBS-type volumes that AWS manages. Multi-AZ keeps a synchronous standby in another AZ and fails over automatically. DynamoDB distributes items across storage partitions based on a hash of the partition key, as explained in Q18. Redshift is a massively parallel, columnar system: a leader node plans queries, and compute nodes scan their slices of columnar data in parallel, which is why it is fast for aggregates over a few columns. ElastiCache keeps data in memory on Redis or Valkey (a Redis-compatible open-source fork) or Memcached nodes, and data loss on node failure is acceptable for a cache. Neptune stores graph relationships and queries them with Gremlin, openCypher, or SPARQL.

### Advantages

Matching the database to the workload gives you the right trade-off: relational consistency, massive key-based throughput, cheap large-scale analytics, or fast cache access. Managed services handle patching, backups, and replication, so teams focus on the data model.

### Disadvantages and trade-offs

Using several databases adds operational complexity, data synchronization work, and more skills to maintain. RDS does not scale writes horizontally in the way DynamoDB does, so very large write loads push teams toward sharding or a different service. DynamoDB makes ad-hoc queries difficult, because access must be planned in advance. Redshift has high startup cost for small datasets and is poorly suited to frequent single-row updates. ElastiCache data can disappear, so it cannot be the only copy of anything important.

### Real-world examples

An online store keeps orders and payments in Aurora for transactional consistency, stores shopping carts in DynamoDB for fast reads at peak traffic, caches product pages in ElastiCache, and copies order data nightly into Redshift so analysts can build sales reports. A social network uses Neptune to compute friend-of-friend recommendations. A small business runs its accounting application on RDS PostgreSQL because the workload is modest and the team knows SQL well.

### Alternatives and related concepts

Other vendors offer similar categories. Azure SQL, Cosmos DB, and Azure Synapse cover the relational, NoSQL, and analytics roles, and Google Cloud offers Cloud SQL, Spanner, Bigtable, and BigQuery. Open-source options include MongoDB, Cassandra, and ClickHouse. The general concept is polyglot persistence, where each service uses the store that best matches its access pattern.

### Current industry practice

Most architectures use more than one database. Operational data lives in a relational or key-value store, and change data capture tools such as AWS DMS stream changes into S3 data lakes or Redshift for analytics. Data-modeling decisions start from access patterns rather than from the database brand. Teams document which service owns which data and avoid sharing one database across unrelated services, to keep changes independent.

## Q17: What is Amazon Aurora? How is it different from RDS MySQL?

### In simple terms

Aurora is AWS's own relational database engine, compatible with MySQL and PostgreSQL. You still write the same SQL and use the same drivers. The difference is underneath: Aurora separates the compute that processes queries from the storage that holds the data, and that storage is replicated six ways across three AZs. In the notes' words, Amazon took MySQL apart and rebuilt it for the cloud. Standard RDS MySQL is closer to installing MySQL on one machine with its own disk.

### Background: how it was before

Running a replicated MySQL setup used to mean choosing a primary and one or more replicas, configuring replication by hand, and handling failover with scripts or tools such as MHA or Orchestrator. Replicas lagged behind the primary, and failovers took minutes and sometimes lost data. Amazon announced Aurora in 2014 as a MySQL-compatible engine built on a distributed storage layer. The idea was to write only the redo log to storage and let the storage nodes rebuild pages, which reduces the work the database does for every write.

### How it works internally

In Aurora, the database instance sends redo log records to a shared storage volume rather than writing full data pages. The storage volume is split into segments, each with six copies spread across three AZs. A write is acknowledged when four of the six copies confirm it, and a read needs three copies, which keeps the system working if an entire AZ is lost. Storage nodes repair corruption and apply logs in the background. Replicas read from the same storage volume, so replication lag is typically in the tens of milliseconds rather than seconds. Failover promotes a replica, which is fast because it does not have to replay logs from a separate disk. Storage grows in 10 GB increments up to 128 TiB without manual resizing.

Corrections to the notes: AWS describes Aurora as up to five times faster than MySQL and up to three times faster than PostgreSQL, so "5x" is an upper bound for specific workloads. Failover is typically under 30 seconds but depends on the workload. Standard RDS also supports point-in-time recovery, because it combines automated snapshots with transaction logs, so Aurora's advantage in backups is continuous backup with faster restores rather than the presence of PITR. Aurora's cost is also more nuanced than "20 percent more." Instance prices are typically higher, and standard Aurora bills I/O separately, while Aurora I/O-Optimized removes I/O charges for a higher instance price.

### Advantages

Aurora offers higher throughput, quicker failover, and very low replica lag. Storage scales without downtime, and up to 15 read replicas can share the same data. Aurora Global Database replicates data across regions with typically under a second of lag, which supports regional disaster recovery. Aurora Serverless v2 scales capacity up and down in small increments, which suits variable workloads.

### Disadvantages and trade-offs

Aurora costs more than standard RDS, and I/O charges can surprise teams with write-heavy workloads unless they choose the I/O-Optimized configuration. It is not 100 percent identical to MySQL or PostgreSQL, and features sometimes arrive in Aurora after the community release. Using Aurora-specific features increases lock-in. For small applications, Aurora is overkill, and a simpler RDS instance may be enough.

### Real-world examples

A ticketing platform uses Aurora PostgreSQL with read replicas to handle a spike of read traffic during a concert sale, while writes go to the primary. A global SaaS product uses Aurora Global Database so a secondary region can take over during an outage. A small internal tool runs on a single RDS MySQL instance, because the team does not need the performance or failover speed that Aurora offers.

### Alternatives and related concepts

Google Cloud AlloyDB and Azure HorizonDB offer similar cloud-native, separated-storage databases. Vitess, CockroachDB, and YugabyteDB provide horizontally scalable SQL, which is a different approach. Managed MySQL and PostgreSQL on other clouds, and self-managed clusters with tools such as Patroni, are the traditional alternatives.

### Current industry practice

Aurora is commonly the default choice for new relational workloads on AWS that need high availability or significant read scale, while RDS remains common for simpler applications and cost-sensitive systems. Teams use Aurora read replicas to offload reporting, Global Database for disaster recovery, and Serverless v2 for development environments and spiky workloads. Performance Insights and query tuning are still necessary, because a better storage layer does not fix an inefficient query.

## Q18: Explain DynamoDB internal working: partitions, keys, and indexes.

### In simple terms

DynamoDB is a fully managed NoSQL database. Every item is identified by a primary key, which can be a partition key alone or a partition key plus a sort key. The partition key decides which storage partition holds the item. The sort key orders items that share the same partition key. Indexes let you query by other attributes. The notes' library analogy is useful, with one important caution: if the genre is the partition key, a very popular genre can overload one room, which is the "hot partition" problem that good key design avoids.

### Background: how it was before

Before DynamoDB, large web companies struggled with relational databases that could not scale writes past one machine. Engineers sharded MySQL by hand, writing application code to decide which shard held each user, and resharding was painful. In 2007, Amazon published the Dynamo paper, describing a key-value store built on consistent hashing and replication. DynamoDB, launched in 2012, turned those ideas into a managed service with an API and predictable performance. Teams gained scale and lost the ability to run ad-hoc queries.

### How it works internally

DynamoDB hashes the partition key and uses the result to choose a storage partition. Items with the same partition key are stored together, and within that group they are sorted by the sort key, which makes range queries efficient. Each partition holds up to 10 GB of data and can serve roughly 3,000 read capacity units and 1,000 write capacity units per second. When data or traffic grows, DynamoDB splits partitions automatically. Adaptive capacity moves unused throughput toward hot partitions, which reduces, but does not remove, the risk of hot keys. Data is replicated across three AZs. A strongly consistent read reads from the leader replica, and an eventually consistent read can be served by any replica, which is cheaper.

Global Secondary Indexes (GSIs) are separate tables maintained by DynamoDB. Each has its own partition key and optional sort key, spans all partitions, can be created at any time, and supports only eventually consistent reads. Local Secondary Indexes (LSIs) share the table's partition key with a different sort key. They must be defined when the table is created, and each partition key value's item collection, including its LSI data, is limited to 10 GB. Capacity modes are provisioned, where you set read and write capacity and can use auto scaling, or on-demand, where you pay per request and capacity adjusts automatically.

The notes' example is correct in principle. Items with customerId "C001" share a partition key and are sorted by orderDate. A correction: the particular partition labels are illustrative, since DynamoDB decides placement internally.

### Advantages

DynamoDB offers single-digit millisecond latency at virtually any scale, no servers to manage, and built-in replication across AZs. Global Tables provide multi-region, multi-active replication. Streams publish item changes for event-driven processing, Point-in-Time Recovery protects data, and TTL deletes expired items automatically.

### Disadvantages and trade-offs

DynamoDB has no joins, and queries are limited to the keys and indexes you designed. Scans read the whole table and are expensive. Each GSI costs storage and write capacity, and eventual consistency can confuse users who expect to see their own update immediately. Items are limited to 400 KB, so large documents need S3 for storage. Poor partition key design causes hot partitions and throttling. Pricing can be hard to predict, especially for provisioned capacity with bursty traffic.

### Real-world examples

A shopping cart service uses customerId as the partition key and productId as the sort key, so reading a cart is a single fast query. A gaming platform stores player state keyed by playerId and uses a GSI on guildId to list guild members. A ride-sharing app keeps driver location updates in DynamoDB with TTL to remove stale records automatically. A bad example is using a boolean status such as "active" as the partition key, which sends almost all writes to one partition, and the fix is to add a high-cardinality component to the key.

### Alternatives and related concepts

Apache Cassandra and Amazon Keyspaces use similar wide-column models, Google Cloud Bigtable and Firestore cover similar workloads, and Azure Cosmos DB offers multiple APIs including key-value access. MongoDB and DocumentDB provide richer query capabilities at the cost of different scaling trade-offs. DynamoDB Accelerator (DAX) adds an in-memory cache in front of DynamoDB for microsecond reads.

### Current industry practice

Good DynamoDB design starts with a list of access patterns, then designs keys and indexes to serve them, often with single-table designs that store several entity types in one table. Teams use high-cardinality partition keys, monitor throttling and hot-key metrics, and use Streams to drive downstream processes. Global Tables support active-active applications across regions. Long-term analytics typically export DynamoDB data to S3 and query it with a lakehouse engine.

# Section 6: Security and Identity

## Q19: What is IAM? Explain users, groups, roles, and policies.

### In simple terms

IAM (Identity and Access Management) controls who can do what in AWS. A user is a person or application with credentials. A group is a collection of users that shares the same permissions, which makes management easier. A role is a set of permissions that a service or person can temporarily assume. A policy is a JSON document that states which actions are allowed or denied on which resources. The notes' passport analogy fits: the policy is the visa, the user is the traveler, and the role is a temporary visitor pass.

### Background: how it was before

Before IAM, AWS accounts were controlled by a single set of root credentials. Many teams shared those credentials, or embedded access keys in application code, configuration files, and even public repositories. Leaked keys caused a steady stream of breaches. AWS introduced IAM in 2011 to give each person and service its own identity with scoped permissions. That was a major shift: access became an explicit, reviewable configuration rather than an accident of who had the password.

### How it works internally

When a request reaches AWS, the service evaluates all applicable policies. An explicit deny in any policy wins immediately. Then Service Control Policies (SCPs) from AWS Organizations set the maximum permissions an account can have. Resource-based policies, such as an S3 bucket policy, can grant access to principals in other accounts. Identity-based policies attached to the user, group, or role provide the core permissions, and permissions boundaries and session policies can further restrict them. If no statement allows the action, access is denied by default. Groups cannot contain other groups, and a group is not an identity, so it cannot be assumed or used to sign in.

A correction to the notes: in the example policy, the resource "arn:aws:s3:::my-bucket/" matches only the bucket "folder" itself, not the objects inside it. Object actions such as GetObject need "arn:aws:s3:::my-bucket/*". The deny statement works as written, because an explicit deny always overrides an allow.

Users can have console passwords and access keys, and MFA should be required for humans. For people in larger organizations, AWS IAM Identity Center is now the recommended approach, because it uses single sign-on from an identity provider and avoids long-lived user credentials.

### Advantages

IAM gives fine-grained control with a clear evaluation model. Groups make permissions manageable across many people. Roles eliminate long-lived credentials for services. Policies are versioned text, which means they can be reviewed in code review, tested with the policy simulator, and analyzed with IAM Access Analyzer.

### Disadvantages and trade-offs

Policies can become complicated and hard to read, and overly broad wildcards are common. Permission sprawl happens when people accumulate access over time. Cross-account setups and SCPs add layers that are difficult to debug. Even with good tooling, "why was access denied" can take a long time to answer.

### Real-world examples

A company creates a Developers group with read-only access to production logs and an Admins group with broader rights, then adds people to the groups instead of editing individual users. A data pipeline role allows reading from one S3 prefix and writing to one DynamoDB table, and nothing else. A security team uses an SCP to block anyone in member accounts from disabling CloudTrail logging.

### Alternatives and related concepts

Azure uses Entra ID with role-based access control (RBAC), and Google Cloud uses IAM with similar concepts. Attribute-based access control (ABAC) uses tags to grant access, which scales well when resources are numerous. Zero-trust network access and identity-aware proxies extend these ideas beyond the cloud console.

### Current industry practice

The main principles are least privilege, short-lived credentials, MFA for humans, and centralized identity through SSO. Teams manage IAM as code, review changes through pull requests, use Access Analyzer to find unused permissions, and generate policies from access activity. Organizations with many accounts use Control Tower and SCPs to enforce baseline guardrails.

## Q20: What is the difference between IAM Role and IAM User?

### In simple terms

An IAM user is a permanent identity with long-term credentials, meant for a specific person or an application that cannot use a role. An IAM role has no password or permanent keys. Instead, something trusted, such as an EC2 instance, a Lambda function, a user in another account, or a federated identity, assumes the role and receives temporary credentials that expire. The notes' employee badge and contractor pass analogies capture it well. A badge that never expires is a risk, while a pass that lapses automatically is much safer.

### Background: how it was before

Early applications on EC2 needed AWS access, so developers copied access keys onto servers, into configuration files, or into machine images. Those keys were rarely rotated, and a single leak could expose an entire account. Once IAM roles for EC2 launched in 2012, the instance could receive temporary credentials automatically. Web-identity and SAML federation later extended roles to external identities, such as GitHub Actions and corporate directories, so long-lived keys became unnecessary in many cases.

### How it works internally

A role has two policies. A trust policy states who may assume the role, such as an EC2 service principal or a specific account. A permissions policy states what the role can do. When a principal assumes the role, AWS Security Token Service (STS) issues an access key, a secret key, and a session token. The default session lasts one hour and can be configured up to 12 hours. For EC2, an instance profile makes the credentials available through the instance metadata service (IMDSv2), and the AWS SDKs refresh them automatically before they expire. For cross-account access, a trust policy names the other account, and an external ID can prevent confused-deputy attacks. Federated roles let a CI/CD system obtain credentials with OpenID Connect, so no secret is stored in the pipeline.

### Advantages

Roles remove stored secrets from code and servers, rotate credentials automatically, and limit the window for misuse. They make auditing easier because CloudTrail records the role session and the source identity. A single role can serve many instances, and changing its policy takes effect everywhere at once.

### Disadvantages and trade-offs

Trust policies are easy to misconfigure and can allow more than intended. Long-running jobs must handle credential expiration correctly, which is usually handled by the SDK but can fail with custom tooling. Humans who use roles need to switch roles in the console or use SSO, which adds friction. Debugging permissions that span a role chain and a resource policy can be time-consuming.

### Real-world examples

The notes' rule is the right default: never place access keys on an EC2 instance. Attach a role instead. A Lambda function receives a role that allows writing to one DynamoDB table. A GitHub Actions workflow assumes a deploy role through OIDC when it runs on the main branch, so no AWS secret is stored in the repository. A company's security account hosts a read-only audit role that other accounts trust.

### Alternatives and related concepts

IAM Roles Anywhere lets on-premises servers use temporary credentials issued from a certificate authority. Instance profiles and workload identity are the same idea in other clouds, such as Azure managed identities and Google Cloud service accounts with workload identity federation. For human access, Identity Center or an external identity provider replaces long-lived IAM users.

### Current industry practice

Most organizations have eliminated long-lived access keys except where legally or technically unavoidable. CI/CD systems use OIDC federation, workloads use roles, and people use SSO. Security teams scan repositories for leaked secrets, alert on access key creation, and review trust policies regularly. The trend is toward workload identity that proves who a service is, rather than storing a secret that proves it.

## Q21: How does encryption work in AWS? (At rest vs in transit)

### In simple terms

Encryption scrambles data so that only someone with the right key can read it. At rest means data stored on disk, in S3, or in a database is encrypted. In transit means data moving across a network is encrypted, usually with TLS, the same protocol behind HTTPS. KMS (Key Management Service) manages the keys. The notes' examples are apt: in transit is an armored truck carrying cash, and at rest is a vault whose contents need a combination even if someone steals the safe.

### Background: how it was before

Early encryption was often an afterthought. Companies encrypted backup tapes sometimes, databases rarely, and internal network traffic almost never. Key management was a manual, error-prone process: keys lived in config files, on floppy disks, or in the heads of administrators. Hardware security modules (HSMs) existed for protecting keys, but they were expensive and hard to integrate. AWS KMS launched in 2014 to make key management a managed service that applications could call through an API.

### How it works internally

Encrypting large amounts of data directly with a KMS key would be slow and expensive, so AWS uses envelope encryption. KMS generates a data key, and the service encrypts the data with that data key using AES-256. Then KMS encrypts the data key with a customer managed key (CMK) and stores the encrypted data key alongside the ciphertext. To read the data, the caller asks KMS to decrypt the data key, which requires permission on the CMK, and then decrypts the data locally. The CMK itself never leaves KMS, and KMS keeps its key material in hardware security modules.

The notes describe KMS encrypting the data directly, which simplifies the mechanism. The correction matters because envelope encryption explains why KMS is fast, why it is called for keys rather than bulk data, and why losing a key is serious. A second correction concerns rotation. AWS-owned keys rotate automatically. Customer managed keys can have automatic rotation enabled, which by default rotates the key material yearly, and rotation is not on unless you enable it. Key deletion has a waiting period of 7 to 30 days, which protects against accidents.

For in-transit encryption, AWS services use TLS. Certificates from AWS Certificate Manager (ACM) secure load balancers and CloudFront. S3 encrypts new objects by default with server-side encryption (SSE-S3), and bucket policies can require HTTPS by denying requests where aws:SecureTransport is false. EBS, RDS, DynamoDB, and other services offer encryption at rest with KMS keys.

### Advantages

Encryption is largely built into AWS services with a few settings, and KMS logs every key use in CloudTrail, which gives an audit trail. Customer managed keys let you control access and disable or delete keys, which supports compliance requirements. Envelope encryption keeps the performance cost small.

### Disadvantages and trade-offs

Key policies and grants add complexity, and a misconfigured key policy can lock out legitimate users or applications. KMS requests have quotas and cost, which matters for high-volume workloads, although S3 Bucket Keys reduce those calls. Deleting or losing a customer managed key makes the data encrypted with it permanently unreadable, which is the intended security property and also a serious operational risk. Encryption does not protect against an attacker who has legitimate permission to decrypt.

### Real-world examples

A bank encrypts customer records in RDS with a customer managed key, restricts key use to the application role, and enables rotation. A healthcare platform stores medical images in S3 with SSE-KMS and enforces HTTPS with a bucket policy. A company stores backups with a separate key in another account, so a compromise of production cannot destroy the backups. A startup relies on the default encryption that AWS applies automatically, which is a reasonable baseline when no regulatory key control is required.

### Alternatives and related concepts

AWS CloudHSM provides dedicated single-tenant HSMs for stricter key control. External key stores let KMS use keys held outside AWS. Client-side encryption encrypts data before it leaves the application, so AWS never sees plaintext. Other clouds offer Azure Key Vault and Google Cloud KMS, and HashiCorp Vault is common in multi-cloud environments.

### Current industry practice

Encryption at rest and in transit is the default expectation. Regulated data uses customer managed keys, separated from the workloads that use them, and key use is monitored. Teams enforce TLS through policies and reject unencrypted endpoints. Key rotation and backup of key policies are part of the operating runbook, and cryptographic inventories help plan future migrations.
# Section 7: Messaging and Integration

## Q22: SQS vs SNS vs EventBridge vs Kinesis: When to use which?

### In simple terms

These four services move messages between parts of a system, but in different patterns. SQS is a queue: a producer puts a message in, and one consumer pulls it out and processes it. SNS is a publish-subscribe topic: a producer publishes once, and every subscriber receives a copy. EventBridge is an event bus with rules: events arrive, rules match their content, and matching events go to targets. Kinesis is a streaming platform: producers write an ordered sequence of records, and many consumers read it at their own pace, and can replay older records. The notes' inbox tray and intercom analogies describe the first two well.

### Background: how it was before

Before managed messaging, teams ran message brokers such as IBM MQ, TIBCO, or RabbitMQ on their own servers. Those brokers required licensing, clustering, and careful capacity planning. Applications often communicated through direct API calls, so one slow service could block the whole chain. AWS introduced SQS in 2006 as one of its first services, SNS in 2010, Kinesis in 2013, and EventBridge in 2019 as an evolution of CloudWatch Events. The shift was toward decoupling, where services publish facts and others react without knowing about each other.

### How it works internally

SQS stores messages redundantly across AZs. Standard queues offer at-least-once delivery and best-effort ordering, so duplicates can occur. FIFO queues offer exactly-once processing within a deduplication window and preserve order within a message group, with lower throughput unless you use batching or high-throughput mode. When a consumer receives a message, it becomes invisible for the visibility timeout, and if the consumer does not delete it in time, the message reappears for another attempt. After repeated failures, a dead-letter queue (DLQ) holds the message for inspection. Messages can be up to 256 KB, and larger payloads can be stored in S3 with the extended client library.

SNS delivers each published message to all subscribers, which can be SQS queues, Lambda functions, HTTP endpoints, email, or mobile push. Subscription filter policies deliver only matching messages to each subscriber. The common fan-out pattern publishes to SNS and subscribes an SQS queue for each consumer, which gives each consumer its own retry and buffering.

EventBridge routes events on an event bus. Rules match events using JSON patterns, and each rule can have multiple targets, up to five. Input transformers reshape events before delivery, schema discovery documents event structures, and archive and replay let you re-run past events. EventBridge also receives events from AWS services such as EC2 state changes and from SaaS partners.

Kinesis Data Streams divides a stream into shards. Each shard accepts up to 1 MB per second or 1,000 records per second for writes, and records with the same partition key land on the same shard in order. Retention is 24 hours by default and can be extended to 365 days, so consumers can replay data. Kinesis Data Firehose is a related delivery service that buffers records and delivers them to S3, Redshift, or OpenSearch without replay.

A correction to the notes: a standard SQS queue can have several consumers polling it, but each message is delivered to only one of them at a time, so the statement "ONE consumer" should read "one consumer per message."

### Advantages

SQS absorbs spikes, protects downstream services, and scales without provisioning. SNS makes fan-out simple and cheap. EventBridge lets teams add new consumers without changing producers and integrates with many AWS and SaaS sources. Kinesis handles high-throughput ordered data and replay, which suits analytics and event sourcing.

### Disadvantages and trade-offs

Standard SQS can deliver duplicates and out-of-order messages, so consumers must be idempotent. FIFO queues trade throughput for ordering. SNS does not store messages for subscribers that are offline unless each subscriber is a durable queue. EventBridge has per-event pricing and throughput quotas that make it less suitable for extremely high-volume streams. Kinesis requires shard planning, and resharding and hot shards add operational work. Choosing the wrong service often shows up as awkward workarounds later.

### Real-world examples

The notes' order-processing example works well. An order goes into an SQS queue, and a worker processes it when capacity is available. A separate order-placed event goes to an SNS topic, which fans out to inventory, billing, and email subscribers. EventBridge reacts to an EC2 instance changing state and sends an alert to a Lambda function that posts to Slack. For the IoT scenario in the notes, a sensor sending one record per second from 100,000 devices produces 100,000 records per second. At 1,000 records per second per shard, that requires at least 100 shards, assuming records are small. Kinesis then feeds a real-time dashboard through a Flink or Lambda consumer.

### Alternatives and related concepts

Apache Kafka, offered as Amazon MSK, is the standard choice for event streaming with large ecosystems and long retention. Amazon MQ runs ActiveMQ and RabbitMQ for teams migrating existing protocols. Google Cloud Pub/Sub and Azure Service Bus and Event Hubs provide similar messaging in other clouds. AWS IoT Core handles device connectivity and can route device messages into these services.

### Current industry practice

Event-driven architecture is common, and teams apply the outbox pattern to publish events reliably alongside database writes. Consumers are built to handle duplicates, and dead-letter queues are monitored. Schema registries keep event formats compatible over time. A typical pattern uses EventBridge or SNS for routing, SQS for buffering each consumer, and Kinesis or Kafka for high-volume streams that need replay.
# Section 7 (continued): Messaging and Integration

## Q23: What is a Dead Letter Queue (DLQ)?

### In simple terms

A dead letter queue is a holding area for messages that a system could not process after several attempts. A main queue sends each message to a consumer. If the consumer keeps failing on the same message, the queue moves that message aside into a second queue, so the rest of the work keeps flowing. The team can then inspect the message, fix the bug, and send the message back. The notes' mail-sorting analogy is accurate: most letters move smoothly, and one unreadable letter goes into a "needs human review" bin instead of jamming the conveyor belt.

### Background: how it was before

Message brokers have used dead-letter handling for a long time. IBM MQ and other enterprise brokers offered dead-letter queues decades before cloud queues existed, because operators already faced the same problem: a malformed message could fail forever. Without a dead-letter mechanism, teams wrote their own retry loops inside the application, logged the failure, and sometimes dropped the message. Many lost orders and missing payments traced back to that code. Amazon SQS added a native redrive policy so the queue itself handles the move.

### How it works internally

In SQS, you attach a redrive policy to the source queue. The policy names the DLQ and a maxReceiveCount value. SQS counts how many times each message has been received without being deleted. When a message exceeds the limit, SQS moves it to the DLQ. A correction to the notes: SQS does not know whether your code "failed". It counts receives. A consumer that crashes, times out, or simply never deletes the message increases the count, so a slow consumer can send healthy messages to the DLQ too. Moved messages keep their original enqueue timestamp, so the DLQ's retention period should be longer than the source queue's. Otherwise messages can expire in the DLQ before anyone looks at them. SQS now also offers a DLQ redrive action that moves messages back to the source queue in bulk, which replaces the manual copy scripts that used to be common.

For FIFO queues, the DLQ must also be FIFO, and a poison message blocks its message group. Other groups keep flowing, but messages in the same group wait behind the stuck one. That is the accurate version of the notes' claim that a poison message blocks the queue. In a standard queue, a failing message does not block others. It cycles until retention expires, and then it is lost unless a DLQ caught it.

The same concept appears across AWS. Lambda asynchronous invocations retry twice by default and then send the event to an on-failure destination, which can be an SQS queue, SNS topic, or EventBridge bus. SNS subscriptions and EventBridge targets also support DLQs.

### Advantages

A DLQ isolates bad messages so healthy work continues. It preserves failed messages for diagnosis instead of losing them. It gives operations teams a measurable signal, because DLQ depth is an easy metric to alarm on. Redrive lets you replay messages after a fix, so the work is not lost.

### Disadvantages and trade-offs

A DLQ can hide problems if nobody watches it. Messages pile up quietly for weeks, and by then the business impact is large. Redriving without a fix simply recycles the failure. Retry settings are a trade-off: too few attempts send transient failures, such as a brief database outage, to the DLQ, and too many attempts waste capacity and delay detection. Messages in the DLQ may also be out of order and may refer to state that has changed since they failed, so replay needs care. For batch processing, one bad record can cause the whole batch to retry, which is why SQS and Lambda support partial batch responses, where the consumer reports only the failed message IDs.

### Real-world examples

An order-processing queue has maxReceiveCount set to 5. A malformed JSON payload from one partner fails every time, so it moves to the DLQ after five attempts. An alarm on the DLQ depth notifies the on-call engineer, who finds the bad format, patches the parser, and redrives the message. A payments service keeps its DLQ retention at 14 days, the maximum, so a holiday weekend does not cause messages to expire before the team returns. A notification service uses a Lambda on-failure destination so that failed email events are stored for later retry, rather than dropped.

### Alternatives and related concepts

Error handling often combines several techniques. Exponential backoff with jitter spaces retries out so that a recovering dependency is not overwhelmed. Circuit breakers stop calls to a failing dependency for a period. Step Functions can catch errors and route them to a compensation step. Kafka systems use dead-letter topics, Azure Service Bus has a built-in dead-letter sub-queue, and Google Cloud Pub/Sub supports dead-letter topics.

### Current industry practice

Teams alarm on DLQ depth and treat any non-zero value as an incident to review. Consumers are idempotent, so redriving a message that partly succeeded does not duplicate work. Each failed message stores the error reason in attributes or a log entry, which makes triage faster. Engineers classify failures as transient, which can be retried automatically, or permanent, which needs a code or data fix. A runbook describes how to inspect, fix, and redrive, and redrive is done in small batches while watching the metrics.

# Section 8: Monitoring and Logging

## Q24: How does CloudWatch work? What can it monitor?

### In simple terms

CloudWatch is AWS's monitoring service. It collects numbers over time, called metrics, such as CPU use or request count. It stores log lines from your applications, draws dashboards, and runs alarms that take action when a number crosses a threshold. The notes' car analogy fits: metrics are the gauges, alarms are the warning lights, logs are the diagnostic history, and EventBridge reacts when something changes in the AWS environment.

### Background: how it was before

Before cloud monitoring, operators installed tools such as Nagios, Zabbix, Ganglia, or Munin on their own servers. Each tool polled machines, stored its own data, and needed its own alerting setup. Logs lived in files on each server, and someone had to SSH in and grep them. Central logging products such as Splunk made search easier but were costly and needed dedicated infrastructure. CloudWatch launched in 2009 and gave AWS customers metrics and alarms without installing anything, which mattered because EC2 instances came and went.

### How it works internally

Many AWS services publish metrics to CloudWatch automatically. EC2 provides basic metrics every five minutes for free, and detailed monitoring provides one-minute data for an additional fee. A correction to the notes: memory use and disk space are not default EC2 metrics. They require the CloudWatch agent, which runs on the instance and publishes custom metrics. Applications can publish their own custom metrics through the API or the Embedded Metric Format, and high-resolution custom metrics can be stored at one-second granularity.

CloudWatch stores metric data at full resolution for a short time and rolls it up as it ages. Data points at a period shorter than a minute are kept for about three hours, one-minute data for about 15 days, five-minute data for about 63 days, and one-hour data for about 455 days. Statistics such as average, sum, minimum, maximum, and percentiles are computed from the stored points.

An alarm evaluates a metric over a set number of periods. The alarm is in one of three states: OK, ALARM, or INSUFFICIENT_DATA. When it enters ALARM, it can notify an SNS topic or trigger a scaling policy. The notes' example, CPU above 80 percent for three periods, matches this. CloudWatch Logs collects log events into log groups and streams, and retention can be set per group. Logs Insights queries those logs with its own query language. Metric filters turn log patterns into metrics, and subscription filters stream logs to Lambda, Kinesis, or Firehose.

A correction to the notes: CloudWatch Events has been folded into Amazon EventBridge, which is now the event bus behind the console's "Events" section. The capability is the same, but the service is named EventBridge.

### Advantages

CloudWatch is built into AWS, so most services report metrics with no setup. Alarms connect directly to Auto Scaling, SNS, and Systems Manager. Logs, metrics, and alarms share one console, and composite alarms can combine several conditions to reduce noise.

### Disadvantages and trade-offs

Costs can be surprising. Log ingestion and retention are often the largest part of the bill, and high-cardinality custom metrics multiply charges. The query language takes time to learn. Cross-service views are weaker than those in specialized platforms, and high-resolution data expires quickly. Dashboards and alarms can sprawl across accounts and regions, and without naming standards they become hard to maintain.

### Real-world examples

The notes' CPU scenario is a common one. An Auto Scaling group scales on target CPU, and an alarm notifies the team if the group reaches its maximum size. A payments team creates a custom metric for failed transactions and alarms when the rate exceeds 1 percent for five minutes. A security team uses metric filters on CloudTrail logs to detect root account logins and sends an alert. An operations team uses Logs Insights to find the top 20 slowest API endpoints from the previous day.

### Alternatives and related concepts

Prometheus with Grafana is a popular open-source stack, and Amazon Managed Prometheus and Amazon Managed Grafana provide hosted versions. Datadog, New Relic, Dynatrace, and the Elastic Stack offer broader correlation across metrics, logs, and traces. OpenTelemetry defines a vendor-neutral way to produce all three signals.

### Current industry practice

Teams standardize on OpenTelemetry for instrumentation so they are not locked to one backend. They alarm on symptoms that users notice, such as error rate and latency, rather than on every low-level metric, and they use SLO-based alerting to reduce noise. Log retention policies control cost, and tagging or naming conventions keep dashboards manageable. Metric cardinality is reviewed, because a metric labeled with user IDs can generate millions of series.

## Q25: What is AWS X-Ray?

### In simple terms

AWS X-Ray traces a single request as it moves through several services. Each service adds timing information to the same trace, so you can see the full path of one request and where time was spent. The notes' package-tracking analogy is a good way to think about it: X-Ray gives every request a tracking number, and you can see which stop held it up.

### Background: how it was before

Before distributed tracing, performance problems in microservices were investigated by reading logs from many services and matching timestamps by hand. That was slow and unreliable, especially when requests passed through queues. Google's Dapper paper in 2010 described an internal tracing system that propagated a trace ID through every call. Twitter open-sourced Zipkin in 2012, and Uber built Jaeger. AWS launched X-Ray in 2016 to bring the same approach to AWS services and Lambda.

### How it works internally

When a request enters your system, X-Ray assigns it a trace ID. Each service that handles the request records a segment, which includes start time, end time, and metadata. Calls to other services or databases become subsegments. The trace ID travels with the request, usually in the X-Amzn-Trace-Id HTTP header. Between services that use queues, the trace context must be passed explicitly, for example in SQS message attributes, otherwise the trace breaks. The SDK sends segments to the X-Ray daemon or the X-Ray API, and X-Ray assembles them into traces and builds a service map from the relationships. Sampling controls cost: by default, X-Ray records the first request each second and five percent of additional requests. Annotations are indexed and can be used to filter traces, while metadata is stored but not searchable. AWS now recommends OpenTelemetry-based instrumentation for new work.

Two corrections to the notes. First, the durations in a trace do not always add up. Calls made in parallel overlap, so the total can be much smaller than the sum of parts. Second, the example's numbers are inconsistent. If the RDS query of 150 ms happens inside Lambda 2, then Lambda 2 cannot take only 120 ms. A real trace would show that nesting, and the bottleneck would be judged by the time each component spends on its own work, not its total.

### Advantages

X-Ray shows the real request path, including services you may not expect. The service map reveals dependencies and error rates at a glance. Lambda integration requires only enabling active tracing. X-Ray Insights can detect unusual latency or error patterns and point to the affected service.

### Disadvantages and trade-offs

Instrumentation takes effort, and every service in the path must participate for the trace to be complete. Sampling can miss rare errors unless you set rules that always capture errors or slow requests. Costs grow with trace volume. Cross-account tracing needs extra setup, and the X-Ray console is less flexible for deep analysis than dedicated APM products. Queue-based flows and third-party services often lose context.

### Real-world examples

A checkout request crosses API Gateway, Lambda, DynamoDB, and SQS. The trace shows that the payment Lambda is fast, but the database call inside a second Lambda takes most of the time, which points to a missing index. A team creates a sampling rule that records every request returning an error, so rare failures are always captured. An operations team uses the service map to see that a third-party tax API is responsible for most timeouts.

### Alternatives and related concepts

Jaeger and Zipkin are open-source tracing backends. Grafana Tempo stores traces at low cost and integrates with Grafana dashboards. Datadog APM, Dynatrace, and Honeycomb offer commercial tracing with richer analysis. OpenTelemetry provides the instrumentation libraries and collectors that can send data to almost any of these backends.

### Current industry practice

Tracing is one of the three pillars of observability, alongside metrics and logs, and teams correlate them by trace ID. Tail-based sampling keeps slow and failed traces while discarding most normal ones. Trace context is propagated through message queues, not just HTTP calls. Teams review service maps during architecture discussions and use traces to validate SLOs.

# Section 9: CI/CD and DevOps

## Q26: Explain AWS CI/CD pipeline services.

### In simple terms

CI/CD stands for continuous integration and continuous delivery. Developers merge code often, and an automated pipeline builds it, tests it, and deploys it without manual steps. AWS offers four developer tools that cover those stages. CodeCommit stores the source code, CodeBuild compiles and tests it, CodeDeploy puts it on target servers or services, and CodePipeline connects the stages into one workflow. The notes' comparisons are useful: CodeCommit is like a Git host, CodeBuild is like a Jenkins build agent, and CodePipeline is the conveyor belt that moves code through each stage.

### Background: how it was before

Before automated pipelines, a release meant a long manual checklist. Someone copied files to servers over SSH, ran database scripts by hand, and hoped nothing had drifted. Teams released every few months, often at night on weekends, because each release was risky. Jez Humble and David Farley's book on continuous delivery in 2010 made the case for small, frequent, automated releases. Jenkins became the popular open-source automation server, and each team maintained its own build machines, which were often inconsistent and hard to maintain.

### How it works internally

CodePipeline is an orchestrator. A pipeline has stages, and each stage has actions, such as pulling source from a repository, running a CodeBuild project, deploying with CodeDeploy, or waiting for a manual approval. Artifacts pass between stages through an S3 bucket. CodeBuild runs each build in a fresh managed container, reads instructions from a buildspec.yml file, and uploads its output as an artifact. CodeDeploy needs an agent on EC2 or on-premises servers, or it uses native traffic shifting for Lambda and ECS. Its appspec file defines lifecycle hooks such as BeforeInstall and ValidateService, and CloudWatch alarms can trigger an automatic rollback.

A correction to the notes: CodeCommit is no longer offered to new customers, as AWS announced in 2024, so new projects typically connect CodePipeline to GitHub, GitLab, or Bitbucket. The notes' deployment table also needs changes. CodeDeploy's EC2 and on-premises options are AllAtOnce, OneAtATime, HalfAtATime, or a custom batch size, so "rolling 25 percent" is a custom configuration. Canary and linear traffic shifting apply to Lambda and ECS, not to EC2 instances.

### Advantages

The services integrate with IAM, CloudWatch, and the rest of AWS, so access control and monitoring are consistent. CodeBuild needs no build servers to maintain. CodeDeploy's blue/green and traffic-shifting options reduce deployment risk, and rollback is built in.

### Disadvantages and trade-offs

The ecosystem is smaller than GitHub Actions or Jenkins, with fewer ready-made integrations and community examples. CodePipeline definitions can become complex, and debugging a failed stage means reading logs across several services. CodeCommit's status means teams that adopted it early need a migration plan. For teams that already use GitHub or GitLab, the AWS tools add a second place to manage pipelines.

### Real-world examples

A team pushes to a main branch, CodePipeline pulls the change from GitHub, CodeBuild runs unit tests and builds a container image, and CodeDeploy performs a blue/green deployment to an ECS service. A manual approval stage waits for a release manager before production. A Lambda team uses a canary traffic shift, moving 10 percent of traffic to the new version for five minutes, and rolls back automatically if a CloudWatch alarm fires. An on-premises application uses CodeDeploy with an agent to update servers in batches of two.

### Alternatives and related concepts

GitHub Actions, GitLab CI/CD, Jenkins, CircleCI, and Azure DevOps Pipelines are widely used alternatives. Argo CD and Flux implement GitOps for Kubernetes, where the cluster pulls the desired state from Git. Spinnaker offers advanced deployment strategies for multi-cloud environments.

### Current industry practice

Pipelines are defined as code and reviewed like application code. Teams use trunk-based development with short-lived branches and feature flags to separate deployment from release. Progressive delivery, which means canary, blue/green, and automated rollback, is common for customer-facing services. Supply-chain security is now part of the pipeline, with image scanning, signed artifacts, and dependency checks. Teams track DORA metrics, such as deployment frequency, lead time, change failure rate, and time to restore, to measure improvement.

# Section 10: Serverless Architecture

## Q27: Design a fully serverless application on AWS.

### In simple terms

A serverless application runs without you provisioning or patching servers. The browser loads static files from a CDN, calls an API, and that API runs small functions on demand. Data lives in managed databases and storage, and the system scales with traffic and costs little when idle. The notes' architecture is a sound pattern: CloudFront serves the front end, API Gateway receives requests, Lambda runs the business logic, Cognito handles users, and DynamoDB, S3, and messaging services hold the data and move work along. Serverless does not mean there are no servers. It means you do not manage them.

### Background: how it was before

Before serverless, a web application ran on a fleet of application servers that were sized for peak load and ran all the time. Teams patched operating systems, managed web server configuration, and planned capacity months ahead. Backend-as-a-service products such as Parse and Firebase in the early 2010s proved that developers wanted to skip server management. AWS Lambda in 2014 made the pattern general purpose, and the term serverless spread quickly in the following years.

### How it works internally

CloudFront serves the static site from S3, using an origin access control so the bucket stays private. API Gateway receives API calls, checks authorization, applies throttling and usage plans, validates requests, and invokes Lambda. A Cognito user pool issues JSON Web Tokens, and an API Gateway authorizer validates them before the Lambda runs. Each Lambda function handles one operation and reads or writes DynamoDB, S3, or a relational database. Lambda functions that connect to Aurora need Amazon RDS Proxy, which pools connections, because thousands of short-lived functions can exhaust database connections quickly. SQS and SNS decouple slow work such as email, and Step Functions coordinates multi-step workflows with retries and error paths.

Corrections to the notes. Some components do not scale to zero. OpenSearch Serverless has a minimum capacity, and Aurora Serverless v2 can pause at zero capacity but takes time to resume. Aurora Serverless v1 is a legacy version, so new designs should use v2. Amazon Pinpoint has been announced for retirement, so verify its current status before choosing it for SMS or push notifications. The "$0 bill on Tuesday" is also approximate, because Cognito, storage, and logging still charge small amounts. API Gateway has a hard integration timeout of about 30 seconds, and synchronous Lambda payloads are limited to 6 MB, which shape the design.

### Advantages

The architecture scales automatically and costs little when idle. Teams deploy small functions independently, and operations work is limited to configuration and monitoring. Each managed service has its own availability design, so the application inherits multi-AZ resilience without extra effort. Time to market is often short, because there is no infrastructure to build first.

### Disadvantages and trade-offs

Cold starts add latency for infrequent functions. Lambda's 15-minute limit rules out long jobs. Vendor lock-in is significant because event sources, authorizers, and permissions are AWS-specific. Local development and testing are harder, and tracing a request across many functions requires X-Ray or OpenTelemetry. At sustained high throughput, per-request pricing can exceed the cost of a well-used container cluster. Application logic can also spread into many small functions that are hard to understand as a whole.

### Real-world examples

A conference app uses this design. Attendees sign in through Cognito, browse sessions from DynamoDB, and receive reminders through SNS. The bill stays low between events and rises during registration week without anyone changing settings. A ticketing platform places a queue in front of its checkout Lambda, so when a major concert's tickets go on sale, requests wait in SQS rather than overwhelming the payment service. A small startup uses the design for an MVP and moves its heaviest endpoint to containers later, once traffic justifies it.

### Alternatives and related concepts

Containers on Fargate or Cloud Run suit longer-running services and make local development more consistent with production. Firebase, Supabase, and Vercel offer serverless platforms with different trade-offs. Azure Functions with Cosmos DB and Google Cloud Run with Firestore provide similar patterns on other clouds. Frameworks such as AWS SAM, the Serverless Framework, and the CDK package these designs as code.

### Current industry practice

Teams use single-table DynamoDB designs, Lambda Powertools for logging and metrics, arm64 processors for lower cost, and SnapStart or Provisioned Concurrency for latency-sensitive functions. HTTP APIs are generally preferred over REST APIs for cost and speed when their features are sufficient. Functions are kept small, timeouts and reserved concurrency are set deliberately, and event-driven patterns replace long synchronous chains. Many teams use a hybrid model, with serverless for event handling and APIs and containers for steady, long-running services.

# Section 11: Infrastructure as Code

## Q28: What is CloudFormation? How is it different from Terraform?

### In simple terms

Infrastructure as code means describing cloud resources in files instead of clicking through a console. CloudFormation is AWS's native tool. You write a template in YAML or JSON that lists resources, AWS builds them in the right order, and you can update or delete the whole set as one stack. Terraform, from HashiCorp, uses its own language called HCL and works across many clouds and services. Both achieve the same goal: a repeatable, reviewable description of infrastructure. The notes' recipe analogy is apt.

### Background: how it was before

Before infrastructure as code, teams built environments by following documents or clicking in consoles, which is often called ClickOps. Environments drifted apart, and recreating production after a disaster took weeks. Scripts using the AWS CLI helped, but they were fragile because they did not track what already existed. Configuration tools such as Chef and Puppet managed software on servers, but not the servers themselves. CloudFormation launched in 2011 to describe AWS resources declaratively. Terraform followed in 2014 and added a common approach across providers.

### How it works internally

CloudFormation reads a template and builds a dependency graph from references such as Ref and GetAtt. It creates resources in that order and records their state in the stack. If a step fails, it rolls back the changes automatically. Change sets preview the effect of an update before it is applied. Drift detection compares the stack's recorded properties to the live resources. AWS manages the stack state for you, so there is no state file to store or lock.

Terraform reads configuration files and builds a graph of resources and dependencies. Its state file records the real IDs and attributes of each resource. The plan command refreshes the state, compares it to the configuration, and shows the changes. Apply calls each provider's API to create, update, or delete resources. The state must be stored remotely and locked, so two people cannot change the infrastructure at the same time. Terraform's S3 backend historically used a DynamoDB table for locking. Recent versions support native S3 locking, which removes that extra table.

The notes' comparison table needs three corrections. Terraform's state-locking details have changed, as noted above. Terraform's license changed in 2023 to the Business Source License, which led to OpenTofu, an open-source fork under the Linux Foundation. Terraform Cloud, now called HCP Terraform, has free and paid tiers, so "free or paid" is a better description of its cost than a single entry. The notes' YAML example also sets an explicit BucketName. S3 bucket names must be globally unique, so a fixed name often causes conflicts when the same template runs in several accounts or regions. Omitting the name lets CloudFormation generate a unique one.

### Advantages

CloudFormation requires no extra tooling and no state management, and it is integrated with AWS IAM, StackSets for multi-account deployments, and service features that may appear sooner than in third-party providers. Terraform covers many providers with one language, has a large module ecosystem, and has a clear plan step. Both tools support code review, reproducible environments, and automated testing.

### Disadvantages and trade-offs

CloudFormation is AWS-only, and its YAML and JSON templates become verbose for complex systems. Rollback failures can leave stacks in states that need manual cleanup. Template size limits apply, with inline templates capped at about 50 KB and larger ones passed through S3. Terraform requires careful state management, and a corrupted or unlocked state file causes serious problems. Provider upgrades can introduce breaking changes. Mixing both tools on the same resources causes conflicts.

### Real-world examples

A company defines its VPC, databases, and queues in CloudFormation, then deploys the same stacks to development, staging, and production using parameters. A multi-cloud company uses Terraform to manage AWS networking, Azure identity, and Kubernetes clusters from one workflow. A platform team packages standard VPC and logging configurations as Terraform modules, so every product team starts from a secure baseline. When a region is lost, the team runs the same code in a second region to rebuild the environment.

### Alternatives and related concepts

The AWS Cloud Development Kit (CDK) lets you write infrastructure in TypeScript, Python, or other languages, and it generates CloudFormation templates. The AWS Serverless Application Model (SAM) extends CloudFormation for serverless applications. Pulumi uses general-purpose languages with a state backend similar to Terraform's. Azure Bicep and Google Cloud Deployment Manager cover their own clouds.

### Current industry practice

Most organizations treat infrastructure code as production software, with pull requests, automated tests, and policy checks. Tools such as cfn-lint, Checkov, and Open Policy Agent scan templates for insecure settings before deployment. Teams use remote state with locking, separate state per environment, and drift detection to catch manual changes. Many now adopt GitOps workflows, where a merged change triggers the deployment, and avoid manual console changes entirely.

# Section 12: Real-World Scenario-Based Questions

## Q29: Design a highly available web application on AWS.

### In simple terms

High availability means the application keeps working when parts of it fail. The approach is to remove single points of failure. Run several copies of the web tier in separate data centers, place a load balancer in front, store data in a database that fails over automatically, and keep sessions in a shared cache so that losing one server does not log users out. The notes' example captures this well. A web server behind a power cord is a single point of failure, and two servers in different AZs behind a load balancer are not.

### Background: how it was before

Before cloud, high availability was expensive. Companies bought duplicate hardware, configured clustering software such as Veritas or Microsoft Cluster Service, and kept spare machines idle. Disaster recovery often meant restoring backups from tape, which took days. Netflix's Chaos Monkey, introduced in 2011, showed that the best way to know a system is resilient is to break parts of it on purpose. Multi-AZ designs became practical for most companies once cloud regions offered several isolated zones.

### How it works internally

A request starts at Route 53, which resolves the domain name. CloudFront serves cached static content from an edge location close to the user. Requests that need the origin reach an Application Load Balancer with nodes in at least two AZs. The load balancer checks the health of each target and sends traffic only to healthy instances in an Auto Scaling group. The instances connect to an Aurora cluster through its cluster endpoint, and the writer endpoint follows whichever instance is primary after a failover. Aurora stores six copies of the data across three AZs. Sessions live in an ElastiCache for Redis replication group, which should itself span AZs with replicas.

The notes contain three corrections. First, Route 53 failover across regions is a disaster-recovery design. It requires a second, complete stack in another region and is not included in a single-region design. Second, Aurora does not use a "standby" in the RDS Multi-AZ sense. Its failover promotes an existing replica, and its storage is already replicated across AZs. Third, the notes say sessions "survive instance termination," which is true only when the Redis cluster itself is replicated and not a single node.

Availability is measured as a percentage. Ninety-nine point nine percent allows about 8.8 hours of downtime per year, and 99.99 percent allows about 53 minutes. Recovery time objective (RTO) is how long a system can be down, and recovery point objective (RPO) is how much data can be lost. Those targets decide how much redundancy is worth paying for.

### Advantages

Spreading the application across AZs survives the loss of a whole data center. Auto Scaling replaces failed instances, the load balancer hides failures from users, and managed databases fail over without manual steps. The design is reasonably simple to explain and operate.

### Disadvantages and trade-offs

Multi-AZ designs cost more. Running two or more instances, replicas, and cache nodes can roughly double some parts of the bill. Cross-AZ traffic adds data transfer charges. Multi-region designs are much harder, because data must be replicated across distance, which adds lag, and failover logic must avoid split-brain conditions where two regions both accept writes. Untested failover often fails when needed, so it must be exercised regularly.

### Real-world examples

A ticketing site runs across three AZs with Aurora and ElastiCache, and survives an AZ outage during a sale without users noticing. A news site uses CloudFront heavily, so breaking-news traffic is served from edge caches while the origin scales more slowly behind it. A banking application keeps its primary region active and maintains a warm standby in a second region, with an RTO of 15 minutes and an RPO of a few seconds, which it tests every quarter.

### Alternatives and related concepts

Global Accelerator provides static anycast IP addresses that route to the nearest healthy region. DynamoDB Global Tables and Aurora Global Database support multi-region data replication. Active-active designs serve traffic from several regions at once and are the most complex option. Kubernetes multi-cluster setups offer similar resilience on container platforms. AWS Elastic Disaster Recovery replicates servers to a second region for recovery.

### Current industry practice

Multi-AZ is the baseline for production services. Critical systems define RTO and RPO targets, choose between warm standby and active-active based on the cost of downtime, and run game days that simulate failures. Chaos engineering tools such as AWS Fault Injection Service inject controlled failures into test and production environments. Service-level objectives and error budgets guide how much reliability work is needed.

## Q30: Your application is slow. Walk through how you'd diagnose it on AWS.

### In simple terms

Diagnosing slowness means finding where time goes. You start broad and narrow down: first check whether the whole system is overloaded, then check the load balancer for slow responses or errors, then the application, then the database and cache. The notes' funnel method is correct, and it prevents a common mistake, which is scaling web servers when the database is the bottleneck.

### Background: how it was before

Before cloud monitoring and tracing, performance investigations relied on a few tools. Sysadmins ran top, vmstat, and iostat on servers and looked at slow query logs in the database. Developers added timing logs by hand. Brendan Gregg popularized the USE method, which checks utilization, saturation, and errors for each resource. Tom Wilkie later proposed the RED method for services, which checks request rate, errors, and duration. Both gave teams a repeatable checklist instead of guesswork.

### How it works internally

The ALB publishes TargetResponseTime, the time from forwarding a request to the target until the response headers arrive. It does not include the client's network time, so a slow mobile connection will not appear there. ALB publishes separate counts for 5XX errors generated by the load balancer and for errors returned by targets, which tell you whether the application or the infrastructure is failing. RDS Performance Insights samples database activity and shows average active sessions, wait events, and the top SQL statements by load. ElastiCache exposes cache hits, misses, evictions, and memory use. The cache hit ratio is calculated from hits and misses, and low hits or rising evictions mean the cache is too small or keys are wrong.

The notes' steps are sound, but two corrections matter. Increasing memory on an instance can hide a memory leak for a while without fixing it, so the leak should be found and fixed in code. Moving content to CloudFront helps only for cacheable content, and dynamic API responses are not improved by a CDN unless they are carefully cached. The notes also omit several common causes: throttling in DynamoDB, which appears as throttled request metrics, exhausted burst credits on T-family instances or EBS volumes, Lambda concurrency throttles, NAT gateway bandwidth limits, exhausted database connection pools, and VPC flow logs that show dropped connections.

### Advantages

A structured funnel avoids wasted effort. Managed metrics and tracing give consistent data across services. Starting from user-visible symptoms, such as latency and error rate, keeps the investigation focused on what users experience.

### Disadvantages and trade-offs

Observability tools generate large volumes of data, and finding the right signal takes practice. Performance Insights and tracing add some cost. Sampling can make rare slow requests invisible. Investigations can also be misled by averages, so percentiles such as p95 and p99 are more reliable for judging user experience.

### Real-world examples

A checkout API's p99 latency rises from 300 ms to 3 seconds after a release. The ALB shows that target response time rose but target 5XX counts did not. X-Ray traces show that most of the time is spent in one database query. Performance Insights shows that query scanning an entire table, which points to a missing index. Adding the index brings p99 back to 350 ms, and the team adds a regression test for the query plan. In a second case, a Lambda-based API slows during traffic peaks, and CloudWatch shows throttling. The fix is a reserved concurrency increase and a queue to smooth bursts.

### Alternatives and related concepts

Application performance monitoring products such as Datadog APM, Dynatrace, and New Relic combine metrics, traces, and code-level profiling. Database-specific tools such as pganalyze and Percona Monitoring and Management give deeper query analysis. Load testing tools such as k6, Locust, and Gatling reproduce slowdowns before users see them. Profilers such as async-profiler for Java or py-spy for Python show where CPU time goes inside a process.

### Current industry practice

Teams define SLOs for latency and error rate and alert on error budget burn rather than on raw thresholds. Dashboards follow RED and USE conventions. Performance regressions are caught in CI with load tests and query-plan checks. Postmortems record the investigation path, so the next person can move faster. The core principle is to measure before changing anything, because the first guess is often wrong.

## Q31: How would you migrate an on-premises application to AWS?

### In simple terms

Migration means moving an application from your own data center to AWS. There is no single correct method. The 7 Rs are a set of strategies that describe what to do with each application: remove it, leave it where it is, move it unchanged, move it with small improvements, replace it with a SaaS product, or rebuild it for the cloud. The notes' table is an accurate summary, and the best choice depends on the application's value, complexity, and future plans. Most migrations combine several strategies across a portfolio.

### Background: how it was before

Early cloud migrations often followed a single pattern, lift and shift, because it was the fastest way to leave a data center. Many teams discovered that the same servers cost as much in the cloud and that elasticity was unused. Gartner published a version of the migration strategies in the early 2010s, and AWS's migration guidance in 2016 expanded them into six Rs, with Relocate added later for VMware environments. These frameworks helped teams choose deliberately rather than migrate everything the same way.

### How it works internally

A typical migration follows a sequence. Discovery finds applications, dependencies, and utilization. AWS Application Discovery Service can gather data agentlessly or with agents, and migration assessment tools estimate costs. Mobilization sets up a landing zone, which is a secure, multi-account baseline with identity, logging, and networking, often built with AWS Control Tower. Migration then moves each workload in waves, and modernization follows once the application is stable.

AWS Application Migration Service, called MGN, replicates server disks continuously to AWS while the source keeps running. At cutover, you start the replicated servers in EC2 and stop the originals, which keeps downtime short. AWS Database Migration Service performs a full load of the database and then applies ongoing changes, so the target stays in sync until cutover. The Schema Conversion Tool helps convert schemas between engines when the database changes. Large data sets can move with AWS DataSync over the network or with physical devices in the Snow family when bandwidth is too limited.

A correction to the notes: "Replatform" and "Rehost" describe the type of change, but they are not all-or-nothing. A rehost can add a managed database as part of the move, and refactoring is often done in stages. The notes also state that refactoring "heavily drops costs." Refactoring often lowers running costs, but it can add engineering effort and new operational work, so the business case should be measured.

### Advantages

A structured approach lowers risk and makes trade-offs visible. Rehosting is fast and reduces data center cost quickly. Repurchasing to SaaS removes maintenance entirely. Refactoring creates long-term agility and better scaling. Migrating in waves limits the impact of any single failure and lets teams learn as they go.

### Disadvantages and trade-offs

Rehosting carries cloud costs without cloud benefits and can leave a poorly sized application expensive. Replatforming can create partial lock-in. Repurchasing means losing control of features and data, and migrating data between SaaS products is hard. Refactoring takes months and needs skills that may be scarce, and a rewrite can fail in ways the original system did not. Dependencies are often undocumented, and discovering them late can break cutover.

### Real-world examples

A retailer rehosts a legacy inventory application onto EC2 with MGN, cuts over over a weekend, and then right-sizes instances over the following months. An insurance company moves its file-based batch jobs to Amazon S3 and Lambda through a replatform, reducing maintenance without rewriting the logic. A company replaces an on-premises CRM with a SaaS product and migrates contacts with a scripted export. A product team refactors one feature at a time into services behind an API gateway, using the strangler fig pattern, which Martin Fowler described in 2004 and which routes new traffic to new components while the old system shrinks gradually. An unused reporting tool is retired after its owners confirm nobody depends on it.

### Alternatives and related concepts

Some organizations prefer migration factories, which standardize the assessment, migration, and validation steps so that many applications can move on a predictable schedule. Azure Migrate and Google Cloud Migrate offer similar services for their platforms. Hybrid designs keep some workloads on premises for latency, regulation, or contractual reasons, using AWS Outposts or Site-to-Site VPN.

### Current industry practice

Successful migrations start with an inventory and a dependency map, then group applications into waves by risk and business value. Each wave has a rollback plan, a tested cutover runbook, and validation checks for data integrity. Teams set up a landing zone before moving workloads, so security and networking are not improvised. After migration, cost optimization and modernization continue, with rightsizing, reserved capacity, and gradual refactoring of the most valuable applications.
# Q32: How to Implement Distributed Applications

Your text is a design guide for distributed applications on AWS, followed by the start of an AWS-to-Azure migration plan that stops after its introduction. Each concept in the design guide gets its own section. For the migration, I've explained the concepts the introduction names (zero downtime, fault tolerance, and a staged approach) and the standard production approach, and I've marked where I go beyond what your text says. Where the text itself is imprecise, I've noted the correction in the section.

## Concept 1: Break the application into microservices (Step 1)

### In simple terms

A monolith is one application where every feature lives in a single deployable unit. Microservices split that application into small services, each responsible for one business job, such as users, orders, or payments. Each service can be built, tested, and deployed on its own. Think of a restaurant. In a monolith, one cook prepares every dish, so the whole kitchen waits when that cook is busy. In a microservices kitchen, there is a grill station, a dessert station, and a cashier. Each station can be expanded, replaced, or closed without stopping the others. The notes' rule that each service has its own database is what makes that independence real.

### Background: how it was before

Most business applications from the 1990s and 2000s were monoliths, often large enterprise systems deployed once or twice a year. Changing one feature meant testing and releasing everything. The idea that system design mirrors the structure of the organization dates to Melvin Conway's 1967 observation, now called Conway's Law. Service-oriented architecture in the early 2000s attempted to split systems into services, but it often relied on heavy middleware and complex standards. The term microservices took hold around 2012 and 2014. Netflix moved from a monolith to distributed services after a major database corruption outage in 2008, and Amazon's "two-pizza teams" model gave each service a small team that owned it end to end.

### How it works internally

Each microservice has its own code repository, build pipeline, runtime, and data store. Services communicate over the network, so network failures become a normal part of operation rather than an exception. Good boundaries follow business capabilities, which is the central idea of domain-driven design. A service boundary is right when most changes to a business rule stay inside one service. Each service scales according to its own load. For example, the order service may need many more instances during a sale than the user profile service.

### Advantages and disadvantages

Independent deployment lets teams release changes several times a day without coordinating with everyone. Failures can be isolated, so a bug in the recommendation service does not stop checkout. Each team can choose tools that fit its service, and each service can scale separately. The costs are real, though. Every service needs its own pipeline, monitoring, and networking. Calls that were in-process function calls become network calls with latency and failure modes. Data consistency across services becomes a design problem, and debugging requires distributed tracing. Teams that split too early end up with a "distributed monolith," where services are still tightly coupled but now fail over the network.

### Real-world examples

An e-commerce platform splits into user, catalog, cart, order, payment, and shipping services. During a holiday sale, only the cart and order services scale heavily. A bank separates card, loan, and fraud-detection services, which also limits the parts of the system that fall under strict payment-security audits. A ride-hailing app separates matching, pricing, and payments, so the pricing team can release experiments without touching driver matching.

### Alternatives and current industry practice

A modular monolith keeps one deployable application but enforces strict internal module boundaries, which many teams use as a first step. Shopify is well known for running a large modular monolith. The current industry guidance is to start with a modular monolith when the domain is still unclear, and extract services once boundaries are proven by real change patterns and team ownership. Teams often write architecture decision records to explain why each boundary exists.

## Concept 2: Choose the compute strategy: containers or serverless (Step 2)

### In simple terms

A container packages an application with everything it needs to run, so it behaves the same in every environment. A Spring Boot application is usually packaged as a Docker image, and Amazon ECS or Amazon EKS decides where those containers run, restarts them when they fail, and scales them. AWS Lambda is the serverless option. You upload a function, and AWS runs it when an event arrives, such as a message in a queue or an HTTP request. Containers suit long-running services like an order API. Lambda suits short, event-driven tasks like resizing an image after an upload.

### Background: how it was before

Early applications ran directly on physical servers, then on virtual machines managed by VMware and similar platforms. Virtual machines are heavy because each one includes a full operating system. Docker, released in 2013, made lightweight containers practical and standardized packaging. Kubernetes, open-sourced by Google in 2014, became the leading orchestrator. Amazon ECS launched in 2014 and EKS in 2018, and Lambda arrived in 2014 as the first major function-as-a-service platform on AWS.

### How it works internally

A Dockerfile describes the image in layers, and the image is pushed to a registry such as Amazon ECR. ECS places tasks on EC2 instances or on Fargate, which runs each task in its own managed microVM. EKS runs the Kubernetes control plane, and worker nodes run the kubelet, which starts pods. Deployments and controllers keep the desired number of pods running. For Spring Boot on Lambda, the application runs inside a managed Java runtime. The main concern there is the cold start, the delay when Lambda creates a new environment and starts the JVM. AWS Lambda SnapStart reduces this for Java by restoring a snapshot of an initialized environment. Spring Boot 3.2 and later also support Java virtual threads, which help a containerized service handle many concurrent requests with fewer resources.

### Advantages and disadvantages

Containers are portable, match production closely, and suit long-lived services with persistent connections. Kubernetes has a large ecosystem of tools. Fargate removes the need to manage servers for containers, although it costs more per unit of compute than a well-used EC2 fleet. Lambda has no servers to manage, bills per invocation, and scales from zero. Its drawbacks are cold starts for Java, the 15-minute execution limit, difficulty with long-lived connections such as WebSockets, and a different development model that is harder to test locally. Operating EKS well requires real platform expertise, which small teams may not have.

### Real-world examples

An order API runs as a Spring Boot container on EKS because it handles steady traffic and needs long-lived database connection pools. A thumbnail service runs on Lambda, triggered by S3 uploads. A nightly reconciliation job runs as a Fargate task that starts when a scheduled rule fires and stops when it finishes. Many organizations run both models, choosing by workload.

### Alternatives and current industry practice

Google Cloud Run and Azure Container Apps provide managed containers with simpler operation than Kubernetes, and AWS App Runner covers simple web services. Kubernetes remains the standard for large platforms, and Fargate is common for teams that want containers without cluster operations. Current practice favors containers for core business services and serverless for event handling, and many teams use Graviton instances to reduce cost.

## Concept 3: Communication between services: synchronous APIs and asynchronous messaging (Step 3)

### In simple terms

Services communicate in two main ways. In synchronous communication, one service calls another over HTTP and waits for the answer, which is how a REST API works. In asynchronous communication, one service puts a message on a queue or publishes an event to a topic and continues without waiting. The receiving service processes the message when it is ready. The notes recommend asynchronous communication where possible because it reduces coupling. A useful rule is to use synchronous calls when the caller needs an answer right away, such as showing a product price, and asynchronous messages when work can happen later, such as sending a confirmation email.
### Background: how it was before

Early distributed systems used remote procedure calls, such as CORBA and Java RMI in the 1990s and SOAP web services in the 2000s. Message-oriented middleware such as IBM MQ and the Java Message Service let systems exchange messages reliably, but those products were expensive and complex. REST became the dominant style for web APIs in the 2000s because it used plain HTTP. Amazon SQS launched in 2006, and Apache Kafka, developed at LinkedIn and open-sourced in 2011, popularized event streaming. Spring provides clients for both styles, including WebClient and OpenFeign for HTTP and Spring Cloud AWS for messaging.

### How it works internally

A synchronous request leaves the client, passes through API Gateway, which checks authentication, applies throttling, and routes the request. From there it reaches a load balancer or a service endpoint inside the VPC, then the target service. Each hop adds latency, and each call should have a timeout so that a slow service cannot hold threads forever. Asynchronous messages follow a different path. A producer calls SendMessage, and SQS stores the message redundantly across AZs. A consumer receives the message and it becomes invisible for the visibility timeout. If the consumer deletes the message after finishing, the work is complete. If it does not, the message reappears for another attempt. SNS works differently: it delivers each published message to all subscribers, and a common pattern is to subscribe an SQS queue for each consumer so each one buffers and retries independently.

### Advantages and disadvantages

Synchronous calls are simple to understand, debug, and test, and they give immediate feedback. Asynchronous messaging decouples services in time, absorbs traffic spikes, and provides retries. A slow consumer does not block the producer. The drawbacks are real, though. Synchronous chains create cascading failures, where one slow service causes timeouts across many callers. Asynchronous systems deliver messages at least once in standard queues, so consumers must handle duplicates. Ordering is limited, debugging means following events across queues, and users may see results later, which requires careful product design.

### Real-world examples

When a customer places an order, the order service returns a response immediately after saving the order. It publishes an OrderPlaced event to an SNS topic. The payment worker, inventory worker, and notification worker each receive their own copy through SQS. A product page calls the pricing service synchronously because it needs the price before rendering. A payment provider call is synchronous but has a short timeout and a retry policy, and the failure path is an async retry queue.

### Alternatives and current industry practice

gRPC uses HTTP/2 and binary Protocol Buffers for fast internal calls, and GraphQL lets clients ask for exactly the fields they need. EventBridge handles event routing with rules, and Amazon MSK runs managed Kafka for high-volume streams. Service meshes such as Istio and Linkerd handle retries, timeouts, and mutual TLS outside application code. Current practice is to use asynchronous events for commands and state changes, synchronous calls for queries that need immediate answers, the outbox pattern to publish events reliably, and contract testing with tools such as Pact to keep APIs compatible.

## Concept 4: Data management: database per service (Step 4)

### In simple terms

Each microservice owns its data. The order service keeps orders in its own database, and the payment service keeps payments in a separate one. Other services never read those tables directly. They ask the owning service through its API or listen for its events. Think of separate shops in a mall, each keeping its own inventory ledger. A shop that wants to know another shop's stock asks it directly, instead of walking into its storeroom. The notes recommend Amazon RDS for structured, relational data and DynamoDB for very high scale, which fits this model because each service can choose the store that suits its own workload.

### Background: how it was before

Monolithic systems usually shared one large database. That made reporting easy, but it created hidden coupling. A schema change for one feature could break several others, and a database administrator often became a bottleneck. In the microservices movement, database-per-service became a central pattern, described by Chris Richardson and many practitioners. Teams that tried to keep a shared database while splitting services usually found that they had built a distributed monolith, with the coupling moved into the schema.

### How it works internally

Because data is no longer in one place, cross-service consistency requires new techniques. Other services often keep local copies of the data they need, updated by events. Queries that combine data from several services use API composition, where a gateway or a backend-for-frontend calls each service and merges the results, or they use read models built from events. Multi-step business operations use the saga pattern. For example, placing an order might reserve inventory, charge payment, and confirm the order. If payment fails, the saga runs a compensating action to release the inventory. Sagas can be orchestrated by a central coordinator or choreographed through events. The outbox pattern solves a common problem. A service writes its business record and an outbox record in the same local database transaction, and a relay publishes the outbox message afterward, so the event is never lost or sent for a rolled-back change. Idempotent consumers ensure that a repeated message does not charge a customer twice.

### Advantages and disadvantages

Each service can change its schema without coordinating with others. Teams choose the best store for their data, and the failure of one database does not necessarily take down the others. Scaling is independent. The costs are significant. Cross-service joins disappear, and data is duplicated across services. Consistency becomes eventual, which requires user experience decisions such as showing "payment pending." Reporting needs a separate pipeline that collects data from every service, often through change data capture into a data lake or Redshift. Operating many databases multiplies backup, monitoring, and cost work. A middle-ground option, separate schemas inside one cluster, reduces cost but keeps some coupling, because one cluster failure affects every service.

### Real-world examples

An online travel company stores bookings in Aurora, customer profiles in DynamoDB, and loyalty points in a separate RDS database. When a booking is made, the booking service publishes an event, and the loyalty service updates points. A finance team reads consolidated data from a reporting database fed by change data capture, rather than querying production services. A booking that fails halfway triggers a saga that cancels the hotel hold and refunds the payment.

### Alternatives and current industry practice

Some teams use one database cluster with a separate schema or database user per service, which is a practical compromise for smaller systems. Event sourcing stores changes as an append-only log and rebuilds state from it, which fits some domains well. The current practice is database per service, change data capture for downstream consumers, saga or outbox patterns for workflows that span services, and avoidance of distributed transactions across services because two-phase commit is slow and fragile at scale.

## Concept 5: Load balancing and scaling (Step 5)

### In simple terms

A load balancer receives incoming requests and spreads them across several copies of a service, so no single copy is overloaded. Scaling changes the number of copies based on demand. In Kubernetes, the Horizontal Pod Autoscaler adds or removes pods based on metrics. Node autoscaling adds the underlying servers when pods cannot fit. On EC2, an Auto Scaling group adds instances. The notes list Elastic Load Balancing, EC2 Auto Scaling, and Kubernetes HPA, which together cover both the traffic-distribution and capacity-adjustment sides of the problem.

### Background: how it was before

Early websites handled load by buying bigger servers, which is called vertical scaling. Some teams used DNS round-robin, where multiple IP addresses rotated for one name, but that did not check whether servers were healthy. Hardware load balancers from vendors such as F5 became common in data centers and were expensive. Software balancers such as HAProxy and NGINX later provided flexible alternatives. AWS introduced Elastic Load Balancing in 2009. Kubernetes added the Horizontal Pod Autoscaler in its early releases, and it became a standard way to scale containerized services.

### How it works internally

An Application Load Balancer checks the health of each target, sends requests only to healthy targets, and supports path-based and host-based routing. For containers, target groups can use IP addresses, so each pod or task is registered directly. The AWS Load Balancer Controller creates load balancers from Kubernetes Ingress resources. The HPA calculates the desired replica count with a formula based on the ratio between the current metric value and the target value. It applies stabilization windows so that short spikes do not cause constant scaling up and down. Node autoscaling tools such as Karpenter watch for pods that cannot be scheduled and launch suitable instances.

A detail that matters for Spring Boot services is that CPU is not always the right signal. A service that mostly waits on database calls may show low CPU while its thread pool is saturated. Scaling on requests per second per pod, or on the depth of a queue for a worker service, usually works better. KEDA extends the HPA to scale on event sources such as SQS.

### Advantages and disadvantages

Load balancing removes single points of failure among instances and hides failures from clients. Autoscaling matches capacity to demand and reduces idle cost. The disadvantages include scaling lag, because new pods must be scheduled, pulled, started, and warmed up before they serve traffic, and the JVM may take many seconds to reach full speed. Scaling out can overload a database if each new pod opens its own connection pool. Badly chosen metrics cause oscillation, and HPA needs resource requests set correctly to calculate CPU percentages.

### Real-world examples

An order API scales on requests per second per pod through a custom metrics adapter. A payment worker scales on SQS queue depth through KEDA, so the number of workers grows as messages accumulate. A checkout service uses a pod disruption budget so that node maintenance never removes too many replicas at once. A legacy monolith on EC2 keeps an Auto Scaling group with scheduled actions before known sales events.

### Alternatives and current industry practice

Client-side load balancing, such as Spring Cloud LoadBalancer, lets calling services choose instances directly, and a service mesh such as Istio applies similar logic in sidecars. Azure Application Gateway and Azure Front Door play equivalent roles on Azure. Current practice is to scale on business and queue metrics, set resource requests and limits carefully, reserve capacity for predictable peaks, and combine autoscaling with graceful shutdown so that scaling in does not drop in-flight requests.

## Concept 6: Monitoring and logging with CloudWatch (Management)

### In simple terms

Monitoring measures how the system behaves, using numbers such as CPU use, memory use, request latency, and error rate. Logging records events as text, such as "order 123 failed validation." In a distributed system, logs and metrics come from many services, so they must be collected in one place. The notes list CloudWatch for metrics and CloudWatch Logs for centralized logs. The goal is to answer two questions quickly: is something wrong, and where is it happening?

### Background: how it was before

Before containers, logs lived on each virtual machine. Engineers SSHed into several servers and grepped files, which worked only for small systems. The ELK stack, made of Elasticsearch, Logstash, and Kibana, became popular in the 2010s as an open-source way to centralize logs. Splunk offered a commercial alternative. Prometheus, which began at SoundCloud around 2012 and joined the Cloud Native Computing Foundation in 2016, became the standard for metrics in Kubernetes environments.

### How it works internally

On EKS, Container Insights collects CPU and memory metrics per pod, node, and namespace, which answers the notes' request for CPU and memory tracking in containers. Container logs written to standard output are captured by the container runtime and read by a log agent such as Fluent Bit, which runs as a DaemonSet on each node and ships logs to CloudWatch Logs. Logs are grouped by log group and stream, and retention can be set per group. Spring Boot can emit structured JSON logs through Logback, which makes logs searchable by fields such as orderId or traceId. Micrometer exports application metrics, such as request timers and error counters, to CloudWatch or to an OpenTelemetry collector. CloudWatch Logs Insights queries logs across services, and metric filters can turn log patterns into alarms.

A correction to the notes: CPU and memory for individual containers are not available as default EC2-level metrics. They come from Container Insights or from the CloudWatch agent. API latency is best measured by the load balancer's target response time or by application histograms, and error rates usually come from counting 5XX responses in those metrics.

### Advantages and disadvantages

CloudWatch requires little setup for AWS services and integrates with alarms, Auto Scaling, and EventBridge. Centralized logs make cross-service investigation practical. The costs can grow quickly, especially for log ingestion, long retention, and high-cardinality custom metrics. A metric labeled with a user ID or order ID creates millions of time series. Structured logging requires discipline across teams. Engineers must learn query languages and dashboard conventions, and moving to another vendor later takes effort.

### Real-world examples

An alarm fires when the 99th percentile latency of the order API exceeds 500 milliseconds for five minutes. A dashboard shows request rate, error rate, and latency for each service, following the RED method. A support engineer searches logs for a customer's order ID across every service in one query. A security team alerts when the number of failed login attempts rises sharply in the authentication service.

### Alternatives and current industry practice

Prometheus with Grafana is common for metrics, and Amazon Managed Prometheus and Amazon Managed Grafana provide hosted versions. Loki stores logs cheaply, and Datadog, Dynatrace, and the Elastic Stack offer broader correlation across metrics, logs, and traces. OpenTelemetry has become the common way to instrument applications, so the backend can change without rewriting code. Teams commonly define service-level objectives, alert on error budget burn, use tiered log retention, and limit metric labels to keep costs under control.

## Concept 7: Distributed tracing with AWS X-Ray (Management)

### In simple terms

A single user request may pass through an API gateway, three services, a queue, and a database. Distributed tracing gives that request one trace ID and records each step as a span with its start time and duration. The result is a timeline of the whole journey, which shows where time was spent and which call failed. Your notes describe X-Ray as the way to follow a request across microservices, and the earlier Q25 section covered the mechanics in detail. Here the focus is on how tracing works across service boundaries.

### Background: how it was before

Without tracing, engineers matched timestamps across several log files by hand. Google's Dapper paper in 2010 described an internal system that propagated trace context through every call and recorded timing for each one. Twitter open-sourced Zipkin in 2012, and Uber's Jaeger followed. The W3C Trace Context standard, which defines a traceparent HTTP header, became a formal recommendation in 2020 so that different tools could share context. X-Ray launched in 2016 for AWS environments.

### How it works internally

When a service receives a request, the instrumentation reads the incoming trace context and creates a child span for its own work. When it calls another service, it adds the trace context to the outgoing request. In Spring Boot 3, Micrometer Tracing provides this propagation, and the OpenTelemetry Java agent can instrument HTTP clients, JDBC calls, Kafka, and AWS SDK calls without code changes. Telemetry is sent to the AWS Distro for OpenTelemetry collector, which forwards it to X-Ray. The X-Ray SDKs are now in maintenance mode, and AWS recommends OpenTelemetry for new work.

Context does not travel automatically through every boundary. For SQS, the trace context must be added as a message attribute so that the consumer can continue the trace. For Kafka, it goes into message headers. Logs should include the trace ID, so a log line can be linked to the trace that produced it. Sampling decides which traces are stored. Head-based sampling decides at the start of a request, which is cheap but may miss rare errors. Tail-based sampling waits until the trace completes and keeps slow or failed traces, which is more useful but needs a collector that buffers data.

### Advantages and disadvantages

Tracing shows the real path of a request, reveals hidden dependencies, and helps identify the slowest call. A service map shows which services depend on which and where errors concentrate. The costs include instrumentation effort, the risk of breaking traces at asynchronous boundaries or third-party calls, storage cost for trace volume, and the need to avoid putting sensitive data into span attributes. Sampling reduces cost but can hide the problem you are looking for.

### Real-world examples

A checkout trace shows that a payment provider call adds two seconds, and the retry policy multiplies that delay under load. A trace through SQS reveals that a worker picks up messages long after they were sent because the queue depth is growing. A trace shows that one service calls an internal lookup endpoint hundreds of times per request, which points to a missing batch call.

### Alternatives and current industry practice

Jaeger and Zipkin are open-source backends, Grafana Tempo stores traces at low cost, and Datadog APM, Dynatrace, and Honeycomb provide commercial analysis. Current practice is to use OpenTelemetry for instrumentation, correlate traces with logs and metrics using trace IDs, use exemplars to link a metric spike to a sample trace, and apply tail-based sampling to keep the traces that matter.

## Concept 8: CI/CD pipelines with CodePipeline and CodeBuild (Management)

### In simple terms

CI/CD is the automated path from a code change to a running service. Continuous integration builds and tests every change as it is merged. Continuous delivery packages the verified change and deploys it through environments with minimal manual effort. The notes' flow, Code → Build → Test → Deploy → Monitor, is the correct sequence, and each stage should either pass and continue or stop and tell the team why. For a Spring Boot service, the build stage compiles with Maven or Gradle, the test stage runs unit and integration tests, the image stage builds and scans a container, and the deploy stage rolls the new image out to the cluster.

### Background: how it was before

Before automated pipelines, a release was a manual event. Developers handed a package to operations, who copied it onto servers by hand and ran database scripts from a document. Teams released infrequently because each release was risky. Kent Beck and the Extreme Programming community promoted frequent integration in the late 1990s, and Jenkins, derived from Hudson, popularized automated builds in the 2000s. Jez Humble and David Farley's 2010 book on continuous delivery made the case for automating the path to production as a whole.

### How it works internally

CodePipeline runs a series of stages, each containing actions. A source action fetches code from a repository connected through a CodeConnections link to GitHub or another provider. A CodeBuild action runs in a fresh managed container and reads its instructions from a buildspec file. Build output and deployment artifacts are stored in S3 between stages. For EKS services, the deploy action might apply Helm charts or manifests, or it might update a GitOps repository that Argo CD reconciles. For ECS services, CodeDeploy can perform a blue/green deployment that shifts traffic between two target groups and rolls back automatically if CloudWatch alarms fire. Manual approval actions can pause the pipeline before production.

A correction to the notes: CodeCommit is no longer available to new customers, so teams starting now typically connect CodePipeline to GitHub, GitLab, or Bitbucket.

### Advantages and disadvantages

Automated pipelines make releases repeatable, shorten feedback loops, and reduce human error. Small, frequent releases are easier to diagnose and roll back. AWS-native tools integrate with IAM and CloudWatch, and CodeBuild requires no build servers to maintain. The drawbacks are that CodePipeline definitions can grow complicated, the ecosystem is smaller than GitHub Actions or Jenkins, and flaky tests slow down every team. A pipeline that deploys automatically is only as safe as its tests and monitoring. For microservices, dozens of pipelines can drift apart unless the organization provides shared templates.

### Real-world examples

A team merges a change to the main branch. The pipeline builds the Spring Boot jar, runs tests with Testcontainers against a real PostgreSQL instance, builds and scans the image, deploys it to staging, runs smoke tests, and then performs a canary release to five percent of production traffic. If the error rate rises above one percent during the canary window, the pipeline rolls back automatically. An organization with forty services uses a shared pipeline template maintained by a platform team, so each service only defines its name and deployment settings.

### Alternatives and current industry practice

GitHub Actions, GitLab CI/CD, Jenkins, and Azure DevOps Pipelines are common alternatives. Argo CD and Flux implement GitOps, where the cluster continuously pulls the desired state from Git. Current practice includes pipelines defined as code, trunk-based development with short-lived branches, feature flags to separate deployment from release, progressive delivery with canary and blue/green strategies, and supply-chain security measures such as image signing, software bills of materials, and vulnerability scanning in the pipeline.

## Concept 9: Security: IAM roles, VPC isolation, and API authentication (Management)

### In simple terms

Security in a distributed application means three things. Each service has its own identity and only the permissions it needs, so that a compromised service cannot reach everything. The network is divided so that only the required traffic can flow between components. Callers must prove who they are, usually with a signed token, before they can use an API. The notes list IAM roles, VPC isolation, and API Gateway authentication with JWT or OAuth. Those are the right three areas. The modern principle behind them is zero trust, which means no service is trusted just because it sits inside the network.

### Background: how it was before

Traditional security used a perimeter model, with a firewall protecting the data center and everything inside considered trusted. That model breaks down when services call each other across many hosts and when applications run in shared cloud environments. Single sign-on standards such as SAML appeared in the mid-2000s. OAuth 2.0 was published as RFC 6749 in 2012, OpenID Connect followed in 2014 to add identity, and JSON Web Tokens were standardized in RFC 7519 in 2015. Cloud IAM systems arrived around 2011, giving every workload an identity that could be granted permissions without embedded passwords.

### How it works internally

On EKS, pods receive AWS permissions through IAM Roles for Service Accounts or the newer EKS Pod Identity, so each service's credentials are short-lived and scoped to that service. ECS tasks use task roles for the same purpose. Services sit in private subnets, and only the load balancer is in a public subnet. Security groups refer to each other, so the order service can reach the payment service on its port, and nothing else can. VPC endpoints keep traffic to S3, DynamoDB, SQS, and Secrets Manager inside the AWS network.

For API authentication, API Gateway can validate JWTs issued by Amazon Cognito or another OpenID Connect provider, checking the signature, issuer, audience, and expiry. A Lambda authorizer can add custom logic. Each Spring Boot service should also validate tokens itself, using Spring Security's resource-server support, because defense in depth means one missed check at the gateway does not expose the service. Service-to-service calls can use OAuth client-credentials tokens or mutual TLS through a service mesh. VPC Lattice offers service-level authentication policies for AWS environments.

A correction to the notes: API Gateway authentication protects the edge but does not protect internal calls, so the internal path needs its own identity checks.

### Advantages and disadvantages

Least privilege limits the damage from any single compromise. Short-lived tokens reduce the value of stolen credentials. Auditing is easier because every call carries an identity. The costs include the complexity of managing roles, policies, and certificates across many services, the effort of handling token expiry and key rotation, and the difficulty of debugging permission errors that show up as generic access-denied responses. Overly broad policies, written to make development easier, often persist into production.

### Real-world examples

The payment service accepts requests only from the order service's identity, so a compromised reporting tool cannot initiate payments. A stolen access token expires after fifteen minutes. The database lives in a private subnet, reachable only from the application's security group. A web application firewall on the load balancer blocks common attacks before they reach any service.

### Alternatives and current industry practice

Identity providers such as Microsoft Entra ID, Okta, and Auth0 manage user identity, and Keycloak provides an open-source option. HashiCorp Vault issues dynamic, short-lived credentials for databases and services. Service meshes provide mutual TLS automatically. Current practice centers on zero trust, short-lived credentials, policy-as-code checks in the pipeline, automated secret rotation, and the OWASP API Security Top 10 as a checklist for API design reviews.

## Concept 10: Configuration management with Systems Manager Parameter Store (Management)

### In simple terms

Configuration is any value that differs between environments or can change without a code change, such as a database URL, a timeout, a feature flag, or a password. The guiding principle is that the same container image should run in development, staging, and production, with configuration supplied from outside the image. Parameter Store is AWS's hierarchical key-value store for this purpose. You might store /order-service/prod/db-url as a plain value and /order-service/prod/db-password as an encrypted SecureString. The Spring Boot application reads these values at startup.

### Background: how it was before

Early applications often packaged configuration files inside the deployable archive, so changing a setting meant rebuilding and redeploying. Teams created separate builds for each environment, which made it impossible to be sure that production ran the same code that had been tested. The twelve-factor app methodology, published around 2011 by engineers at Heroku, recommended storing config in the environment rather than in code. Spring Cloud Config, introduced around 2014, provided a central configuration server for Spring applications, and tools such as ZooKeeper, etcd, and Consul offered distributed key-value stores.

### How it works internally

Parameters are organized in paths, and IAM policies can grant access to a path, so one service cannot read another service's settings. Standard parameters are free up to a size and count limit, and advanced parameters allow larger values and more parameters for a fee. SecureString values are encrypted with a KMS key. Spring Cloud AWS can load parameters as a property source at startup, so application code reads them like ordinary properties. Parameter Store has API throughput limits, so applications should read values at startup or cache them rather than querying on every request.

Parameter Store does not push changes to running applications, so a change either requires a restart or a refresh mechanism. For database credentials that need automatic rotation, AWS Secrets Manager is the better choice, because it integrates with RDS and rotates passwords on a schedule. For feature flags that need gradual rollout and validation, AWS AppConfig is designed for that purpose. The notes lump these together under Parameter Store, which works for basic configuration and encrypted values but is not the best tool for rotation or staged feature releases. On Kubernetes, the External Secrets Operator can sync values from Parameter Store or Secrets Manager into native Kubernetes secrets.

### Advantages and disadvantages

Centralized configuration makes changes auditable and controlled through IAM. Encryption protects sensitive values. One image can serve every environment, which reduces the risk of testing a different build than the one shipped. The drawbacks include a startup dependency, because a service that cannot read its configuration may fail to start during an outage in the configuration store. Hierarchies can sprawl over time. Stale feature flags accumulate in code, and changes that are not reloaded correctly can cause confusing differences between instances.

### Real-world examples

The order service reads its database URL differently in development, staging, and production from paths that share the same structure. A feature flag for a new checkout flow is enabled for ten percent of users through AppConfig, and the team removes the flag after the rollout. A database password is rotated monthly by Secrets Manager, and the service reloads its connection pool when the new secret is available.

### Alternatives and current industry practice

Spring Cloud Config Server, HashiCorp Vault, Azure App Configuration with Key Vault, and Kubernetes ConfigMaps and Secrets are common alternatives. LaunchDarkly and similar platforms specialize in feature flags. Current practice follows the twelve-factor principles, keeps secrets in a managed vault with rotation, reviews configuration changes like code changes, and removes obsolete feature flags on a schedule so that the code does not fill with dead branches.

## Concept 11: Fault tolerance: multi-AZ, auto-restart, retries, circuit breakers, and DLQs (Management)

### In simple terms

Fault tolerance means designing the system to keep working, or degrade gracefully, when parts of it fail. Failures in distributed systems are normal. Machines crash, networks drop packets, and downstream services slow down. The notes list several techniques, and each addresses a different failure. Multi-AZ deployment survives the loss of a data center. Auto-restart replaces a crashed container. Retries handle brief glitches. A circuit breaker stops calling a service that is clearly failing. A dead letter queue captures messages that cannot be processed. Together, they keep the system responsive even when a dependency is unhealthy.

### Background: how it was before

Enterprise systems in the 1990s relied on clustering software such as Veritas Cluster Server to fail over to a standby machine, often manually. Retry logic was usually written ad hoc in each application, which led to inconsistent behavior. Michael Nygard's book Release It!, published in 2007, described patterns such as the circuit breaker and bulkhead, and it became a reference for resilient design. Netflix open-sourced Hystrix in 2012, which implemented circuit breakers for Java services, and Chaos Monkey in 2011 showed that randomly terminating instances in production can reveal weaknesses. Hystrix is now in maintenance mode, and Resilience4j is the common replacement for Spring Boot.

### How it works internally

A circuit breaker tracks the failure rate of calls to a dependency. When the failure rate crosses a threshold, the breaker opens and calls fail immediately or fall back to a default response. After a wait period, it enters a half-open state and lets a few calls through to test recovery. If those succeed, it closes again. Retries should use exponential backoff with jitter, so that many clients do not retry at the same moment and overload a recovering service. Retries are safe only when operations are idempotent, which is why payment requests carry idempotency keys. Every outbound call needs a timeout, because a call with no timeout can hold a thread indefinitely. Bulkheads isolate resource pools, so a slow dependency cannot consume every thread in the service.

On Kubernetes, liveness probes restart a container that has stopped responding, and readiness probes stop sending traffic to a pod that is not ready. Spring Boot Actuator provides health endpoints for these probes. Graceful shutdown lets in-flight requests finish when a pod receives a termination signal. Pod disruption budgets limit how many replicas can be taken down during maintenance. For messages, SQS redrive policies move repeatedly failing messages to a DLQ, as covered earlier.

### Advantages and disadvantages

These techniques turn many outages into degraded service rather than complete failure. Clear timeouts and breakers make failures visible and bounded. The disadvantages are real, though. Retries can amplify an outage, creating a retry storm that overwhelms the dependency. Thresholds set too tight cause breakers to open during normal load spikes, and thresholds set too loose protect nothing. Testing failure behavior is hard, and many teams discover their fallbacks do not work only during a real incident.

### Real-world examples

A payment provider starts responding slowly during a regional incident. The circuit breaker opens, and checkout shows "payment pending" while the order is queued for later processing. A deadlocked pod stops responding to its health check, so Kubernetes restarts it within a minute. A request to a downstream inventory service is retried twice with backoff and succeeds on the second attempt. A malformed order event fails five times and moves to the DLQ, where an engineer fixes the schema and redrives it.

### Alternatives and current industry practice

Service meshes such as Istio and Linkerd provide retries, timeouts, and circuit breaking outside application code, which keeps policies consistent across languages. Rate limiting and load shedding protect services during overload. Chaos engineering tools, including AWS Fault Injection Service, test resilience deliberately. Current practice is to set timeouts on every call, make all retried operations idempotent, use backoff with jitter, test fallbacks in game days, and define service-level objectives so that resilience work is tied to measurable goals.

## Concept 12: The example architecture and how a request flows through it

### In simple terms

The notes' real-world architecture shows how the pieces fit together. A user request enters through API Gateway, which handles authentication, throttling, and routing. It then reaches a load balancer that distributes it across containers running on EKS. The order service saves the order and publishes an event to a queue. A worker consumes the event and performs slower work, and the data lives in RDS or DynamoDB. Each hop has a purpose, and each hop can fail. Understanding the path helps you know where to put timeouts, retries, and monitoring.

### Background: how it was before

Older web applications followed a three-tier model, with a web server, an application server, and a database. Requests moved through those tiers in a straight line, and the database was usually the bottleneck and the single point of failure. Service-oriented systems later added message brokers and enterprise service buses, which centralized routing. The architecture in the notes reflects a more recent pattern. Public traffic enters through a managed gateway, internal services communicate through both synchronous and asynchronous paths, and specialized data stores serve different needs.

### How it works internally

Here is the request path in order. The client calls the API over HTTPS. API Gateway checks the token, applies rate limits, and forwards the request to a private load balancer through a VPC link, because the load balancer should not be publicly reachable. The load balancer routes the request to a healthy pod in the order service. The pod validates the request, writes the order to its database, and returns a response quickly. It also writes an outbox record, and a relay publishes an OrderPlaced event to SNS, which fans out to an SQS queue. A worker service receives the message, performs payment or inventory work, and updates its own data. If the worker fails repeatedly, the message moves to a DLQ. A trace ID follows the request from the gateway through the queue and into the worker, so an engineer can see the whole journey.

A correction to the notes: the diagram shows the load balancer after API Gateway without explaining how they connect. For a private load balancer, API Gateway needs a VPC link, and the load balancer should sit in private subnets, so the traffic path is controlled. The notes also imply that every request is synchronous from start to finish, but the design works best when the user receives an acknowledgment and the slow work continues asynchronously.

### Advantages and disadvantages

The design separates the fast user-facing path from slow background work, which keeps response times low. Each component scales independently, and failures in one area do not automatically spread to others. Using a managed gateway removes authentication and throttling code from every service. The costs are more moving parts, more places to monitor, and eventual consistency between the order and the worker. Users may see an order as "processing" for a short time, and the product must handle that state clearly.

### Real-world examples

A food-delivery app places an order synchronously and returns an order ID immediately. A notification worker sends the restaurant an alert, and a dispatch worker finds a driver. A customer refreshes the order screen and sees each status change as the events are processed. A ticketing platform uses the same pattern, with a queue that absorbs the initial rush, so the payment service processes requests at a steady rate.

### Alternatives and current industry practice

Some teams route requests through an ingress controller in Kubernetes rather than a managed gateway, which reduces cost but moves authentication and throttling into the cluster. A backend-for-frontend pattern gives each client type, such as mobile or web, its own API layer that aggregates services. GraphQL gateways can merge several service responses into one request. The current practice is to keep the user-facing path short, push slow work to queues, trace every request, and document each service's timeouts, retries, and failure modes in one place.

## Concept 13: Common mistakes and key principles

### In simple terms

Most distributed application failures come from a small set of mistakes, and most of the principles that prevent them are simple to state. The mistakes include tight coupling between services, a shared database, missing monitoring, ignoring retries and dead letter queues, and running without autoscaling. The principles are loose coupling, independent deployment, observability from the start, and designing for failure. The notes list these clearly, and each one is worth explaining because teams often learn them through painful incidents.

### Background: how it was before

Many of these mistakes started as shortcuts in the monolith era. A shared database was the fastest way to share data. Synchronous calls were the default in most frameworks. Logging and monitoring were added after problems appeared. Industry writing in the late 2010s, including the work of Sam Newman on microservices and Martin Fowler's articles on distributed systems, collected the lessons from organizations that had split their systems too early, too carelessly, or without the operational foundation to support them.

### How it works internally

Tight coupling appears when services depend on each other's internals, such as shared code libraries that must be upgraded together, or synchronous chains where one service cannot respond without three others. A shared database couples schema changes across teams. Missing monitoring means failures are discovered by customers, and the team cannot tell which service caused the problem. Ignoring retries and DLQs means transient errors become permanent losses, and messages disappear without a trace. Missing autoscaling means capacity is either wasted or insufficient. Another common mistake is the nano-service, where services are so small that a single user action requires dozens of network calls, which multiplies latency and failure probability. Teams also underestimate the importance of contract testing, so an API change in one service silently breaks another.

The principles address those mistakes directly. Loose coupling means services communicate through stable contracts and events. Independent deployment means each service can be released without coordinating with others, which requires backward-compatible API changes and database migrations that happen in phases. Observability means logs, metrics, and traces exist before the first production incident, and they carry correlation IDs. Designing for failure means assuming that every dependency will fail, setting timeouts, making operations idempotent, and testing those assumptions regularly.

### Advantages and disadvantages of following these principles

Following the principles makes systems slower to start and more demanding to design. A team must write contracts, invest in observability, and build pipelines before the first feature ships. The return is that incidents are shorter, changes are safer, and teams can work independently. The disadvantage is that principles can be applied too rigidly. A small team with a modest workload may gain more from a modular monolith than from a full microservices platform, and treating every rule as absolute can slow delivery.

### Real-world examples

A team builds a new service that reads directly from another team's database tables. A schema change months later breaks the reporting feature in an unrelated area, and nobody can tell which change caused it until hours have been spent. A different team had no alert on DLQ depth, so a bug in a parser moved two weeks of orders to the DLQ before anyone noticed. A third team set no timeout on an external call, and a provider's slowdown exhausted every thread in its service within minutes.

### Alternatives and current industry practice

Some organizations adopt a platform engineering model, where a central team provides paved roads, meaning templates for services, pipelines, observability, and security, so product teams do not each solve the same problems. Architecture reviews and scorecards check whether new services meet baseline requirements for monitoring, timeouts, and ownership. The current consensus is to adopt the principles early, apply them in proportion to the system's size, and check them continuously through automated tests, dashboards, and game days.

# Migrating a Spring Boot application from AWS to Azure with fault tolerance and zero downtime

## Concept 14: Why lift-and-shift does not guarantee zero downtime

### In simple terms

Zero downtime means users never experience an outage while the application moves. Lift-and-shift means copying the application's servers or images to a new cloud and starting them there. The notes say that a simple lift-and-shift will not guarantee zero downtime, and that is correct. The application tier is usually easy to copy, because it is stateless or nearly so. The difficult parts are the state and the switch. Databases, sessions, queued messages, uploaded files, and DNS records all have to move, and at some moment traffic must change from the old environment to the new one. A copy is not atomic. Data keeps changing while you copy it, so a plan that only copies the data once will lose any writes that happen during the copy.

### Background: how it was before

Traditional migrations were planned around maintenance windows. Teams announced downtime, stopped the application, copied the data, and restarted it in the new location. That approach was acceptable for internal systems used during business hours, but it does not work for customer-facing services that operate around the clock. The movement toward continuous delivery and zero-downtime deployment, described by Jez Humble and David Farley and supported by blue/green deployment practices, made frequent, controlled changes normal. Migrations inherited the same expectation.

### How it works internally

A Spring Boot application typically has several kinds of state. The database holds the business data. Sessions may live in memory or in Redis. Asynchronous work sits in SQS queues. Files live in S3. Configuration lives in Parameter Store or environment variables. Traffic reaches the application through DNS and a load balancer. Each kind of state needs its own strategy. The application containers can be built in Azure and tested without affecting production. The database needs continuous replication until the moment of cutover. Queues must be drained or mirrored. Files must be copied and kept in sync. DNS or the global traffic manager must shift traffic gradually.

It is also useful to separate three goals that are often confused. Zero downtime means users are not disconnected. Zero data loss means every committed write survives the move. Zero risk means nothing can go wrong, which is not achievable. A good plan targets the first two rigorously and keeps the risk small and reversible.

### Advantages and disadvantages

A staged migration with zero downtime protects users from outages and gives the team time to verify each step. Rollback remains possible for longer. The disadvantages are that the migration takes longer, running two environments costs money, data replication adds complexity, and cross-cloud network traffic can add latency and egress charges while both platforms are active. Teams must also keep both environments secure and monitored during the transition, which doubles the operational surface.

### Real-world examples

A payment team migrating its Spring Boot API from AWS to Azure runs both environments for six weeks. The database is replicated continuously to Azure, and the new environment receives a small share of read traffic before any writes move. A retail company moves its catalog service first because it is read-heavy and easy to roll back, and it leaves the order service on AWS until the catalog migration has been stable for a month. A healthcare platform requires an audit trail for every data copy, so its migration includes checksums and reconciliation reports at each stage.

### Alternatives and current industry practice

Some organizations accept a short, announced maintenance window for systems where a few minutes of downtime is tolerable, because it is simpler and cheaper. Others migrate by service, using the strangler fig pattern, in which new traffic goes to the new platform gradually while the old one shrinks. Current practice treats migration as a series of reversible waves, each with its own success criteria, rollback plan, and monitoring, rather than as a single cutover event.

## Concept 15: Zero-downtime migration patterns and fault tolerance across clouds

### In simple terms

The practical patterns for zero-downtime migration are parallel running, data replication, gradual traffic shifting, and a short, controlled write cutover. First, the stateless parts of the Spring Boot application run in Azure alongside the AWS version. Second, the database is replicated continuously from AWS to Azure, so the copies stay close to identical. Third, traffic moves in small steps, such as one percent, then ten, then fifty, while monitoring watches for errors. Finally, the team performs the cutover of writes, which means briefly pausing writes, confirming that replication has caught up, and switching the write endpoint. Rollback is planned from the start, usually by replicating changes back from Azure to AWS for a period after the cutover.

### Background: how it was before

Early data migrations used nightly exports and imports, which meant hours of stale data at the moment of switching. Change data capture, which reads the database's transaction log and streams changes to another system, became mainstream in the 2010s and made near-real-time replication practical. Blue/green deployment, popularized by Jez Humble and David Farley, gave teams a model for switching traffic between two complete environments. Combining those two ideas gives the modern migration pattern.

### How it works internally

Database replication uses the source database's change log. For PostgreSQL, logical replication or a managed migration service streams committed changes to the target. AWS Database Migration Service can perform a full load followed by ongoing change data capture, and Azure Database Migration Service offers similar online migrations to Azure Database for PostgreSQL and MySQL. The team monitors replication lag, which should be a few seconds or less before cutover. At cutover, the application stops accepting writes briefly, the lag drops to zero, and the write endpoint switches. Reads can often move earlier because they tolerate slight staleness.

Traffic shifting happens at the edge. Azure Front Door or Azure Traffic Manager can assign weights to the AWS and Azure origins, and the weights change over time. DNS-based shifting works too, but it depends on low TTL values and caches that do not always respect them. Feature flags can route specific users or tenants to Azure first, which gives finer control than percentages. Queues need careful handling. Before cutover, the team drains the AWS queues or mirrors messages into Azure Service Bus so that no message is lost or processed twice.

Fault tolerance during the migration depends on several factors. Connectivity between the two clouds must be secure and reliable, often through VPN or private links, and latency between them must be measured because a chatty service that moves to Azure while its database stays in AWS will slow down. Consumers must be idempotent, because dual running can deliver the same event to both environments. Observability must span both clouds, which is easiest when both send telemetry through OpenTelemetry to one backend. Egress from AWS is billed, so data movement costs should be estimated before the replication starts, and current pricing should be checked because it changes.

### Advantages and disadvantages

These patterns keep users online, limit the impact of any mistake, and make rollback practical for days or weeks. Measuring each step gives evidence for decisions. The disadvantages are operational complexity, the cost of running two platforms, the need for skilled people in both clouds, and the difficulty of keeping replicated data consistent when writes occur in two places at once. Avoiding true dual writes is usually safer than trying to handle conflicts, so the cutover pause is typically a few seconds rather than literally zero. That pause is close enough to zero for most systems, but the team should say so honestly in its plan.

### Real-world examples

A team migrates its order service with a short cutover at two in the morning, when traffic is lowest. Replication lag drops to under two seconds, writes pause for about five seconds, and the write endpoint switches to Azure. The team keeps replication running in reverse for two weeks. A team moves its reporting workers first, since they can fall behind for an hour without customer impact, and learns how the message mirror behaves before touching the customer-facing path.

### Alternatives and current industry practice

Some teams use a dual-write approach in which the application writes to both databases during a transition. This avoids the replication lag but introduces consistency problems when one write succeeds and the other fails, so it requires reconciliation jobs and careful design. Active-active designs spread users across both clouds permanently, which is the most resilient but also the most complex option. Current practice combines replication, weighted traffic shifting, feature flags, reverse replication for rollback, reconciliation reports, and a written cutover runbook that names who makes each decision.

## Concept 16: Mapping the Spring Boot system from AWS to Azure

### In simple terms

Migrating a Spring Boot application means matching each AWS service to its Azure equivalent. The application code stays largely the same, but the libraries and configuration change. EKS maps to Azure Kubernetes Service (AKS), or to Azure Container Apps for simpler services. API Gateway maps to Azure API Management. RDS maps to Azure Database for PostgreSQL or MySQL. DynamoDB maps to Azure Cosmos DB, but that mapping is not drop-in because the data model and pricing differ. SQS and SNS map to Azure Service Bus and Azure Event Grid, with different delivery semantics. Parameter Store and Secrets Manager map to Azure App Configuration and Azure Key Vault. Cognito maps to Microsoft Entra External ID. CloudWatch and X-Ray map to Azure Monitor and Application Insights. CodePipeline and CodeBuild map to Azure Pipelines or GitHub Actions.

### Background: how it was before

Before cloud portability became a priority, applications were written directly against vendor SDKs, so moving them required rewriting large parts of the code. The rise of Spring as a framework helped because Spring abstracts many integrations behind interfaces. Spring Cloud AWS and Spring Cloud Azure provide starters for each platform's messaging, storage, and configuration services. Kubernetes gave teams a portable runtime for containers, which reduced the platform-specific work at the compute layer. The hardest parts remain managed services without a true equivalent, such as DynamoDB.

### How it works internally

The most practical approach is to isolate platform-specific code behind ports and adapters. For example, an order service might define an interface called EventPublisher. An SQS adapter implements it on AWS, and a Service Bus adapter implements it on Azure. During migration, the application can run with either adapter, selected by configuration. The same approach applies to file storage, where an interface can switch between S3 and Blob Storage, and to configuration, where a property source can switch between Parameter Store and App Configuration. This allows the team to migrate one dependency at a time and test each switch independently.

Some mappings need deeper changes. Azure Service Bus offers queues and topics with sessions, dead-lettering, and duplicate detection, which overlap with SQS and SNS but do not behave identically. For example, message ordering and visibility timeouts have different semantics, so consumers must be checked. DynamoDB to Cosmos DB requires rethinking partition keys and request-unit consumption, because the throughput model differs. Cognito users may need to be migrated, and password hashes may not transfer, so the team might use a staged login approach. Application Insights can receive OpenTelemetry data, which means the instrumentation used on AWS can often be reused with a different exporter.

Authentication requires attention too. Service-to-service calls that used IAM roles on AWS switch to managed identities on Azure. Spring Security's resource-server configuration can usually stay the same if the token issuer and audience change to Entra ID. Each change should be tested with real tokens in staging.

### Advantages and disadvantages

Mapping services carefully lets the team migrate one component at a time and keep the application functional throughout. Azure offers strong integration with Entra ID and Microsoft tooling, which may suit some organizations. The disadvantages are that the mappings are rarely exact, that each managed service has its own limits and pricing model, and that the migration can expose subtle behavior differences only in production. Teams sometimes underestimate the amount of AWS-specific code in utility libraries, retry settings, and deployment scripts.

### Real-world examples

A Spring Boot order service uses an adapter for messaging. During the migration, its outbox relay publishes events to Azure Service Bus while the consumers in AWS still read from SQS through a bridge that copies messages. Once the consumers in Azure are verified, the bridge is removed. A team maps DynamoDB to Cosmos DB for a shopping cart and rewrites the partition key design, because the old key would have created hot partitions under Cosmos DB's throughput model. A security team replaces IAM roles with managed identities and verifies each service's access with automated tests before any traffic moves.

### Alternatives and current industry practice

Multi-cloud platform tools, such as Terraform and Crossplane, can manage resources on both clouds with one workflow, and open-source messaging systems such as Apache Kafka can run on both clouds to avoid changing the messaging layer. Some organizations choose a Kubernetes-centered approach so that the compute layer is nearly identical on both clouds, leaving the managed data and identity services as the main differences. Current practice is to introduce portability abstractions where they reduce real risk, avoid abstracting everything, document every mapping and its known differences, and test each migrated component with production-like load before the next one moves.