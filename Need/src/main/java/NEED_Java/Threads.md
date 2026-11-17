# Threads and Multithreading: A Senior Developer's Deep Dive

Your passage gets the core idea right. Below I expand each concept with internals, history, trade-offs, and current practice. I also correct one example: the Tomcat line describes an older model, not how Tomcat works today.

## 1. Background: Where Threads Came From

Early computers ran one program at a time. Batch systems in the 1950s and 1960s loaded a job, ran it to completion, and only then loaded the next. The CPU sat idle whenever a program waited for a tape drive or a punch card reader, which was expensive when machines cost millions of dollars.

Multiprogramming and time-sharing (1960s) introduced the idea of keeping several programs in memory and switching between them. The unit of isolation was the **process**, which had its own address space. Processes are safe but heavy. Creating one with `fork()` on UNIX copies or marks page tables, and switching between processes flushes more CPU state.

Developers wanted several flows of control inside one program that could share data cheaply. Operating systems responded with lightweight processes and then threads. Mach and Solaris LWPs were early examples, and POSIX standardized threads as `pthreads` in IEEE 1003.1c (1995). Windows NT had native threads from its 1993 release. The Linux NPTL implementation (around 2003–2005) made thread creation cheap and fast.

Languages followed. Java, released in 1995, put threads in the language itself, with `Thread` and `synchronized` from JDK 1.0. Early JVMs used "green threads" that the JVM scheduled itself. Native OS threads replaced them. Java 5 (2004) added `java.util.concurrent`, which Doug Lea designed, and that library is still the foundation of most Java concurrency code.

## 2. Process versus Thread

A process is a container for resources. It owns a virtual address space, file descriptors, sockets, a PID, security credentials, and at least one thread. A thread is a unit of scheduling. It owns its own program counter, register set, and stack, plus a thread ID and a scheduling priority. Threads in the same process share the code segment, the heap, global variables, and open file handles.

| Aspect | Process | Thread |
|---|---|---|
| Memory | Isolated address space | Shares the process heap and globals |
| Creation cost | Higher (new address space, page tables) | Lower (new stack and registers only) |
| Context switch | Higher (may flush TLB, swap page tables) | Lower (same address space) |
| Communication | IPC: pipes, sockets, shared memory, message queues | Direct reads and writes to shared memory |
| Fault isolation | A crash is usually contained | One thread's bad pointer can corrupt the whole process |
| Synchronization needs | Mostly handled by the OS | Your code must coordinate access to shared data |

The trade-off is clear. Threads are cheaper and communicate faster, but they are less isolated. Chrome, for example, runs each site in a separate process for security and stability, and uses threads inside each process for work like compositing and networking.

## 3. Why We Need Multithreading

Your table lists four reasons. Each is worth looking at more closely.

**Responsiveness.** A GUI has one job that must never be delayed: reacting to input and repainting. If a file download or a database query runs on that same thread, the window freezes. Moving the work to a background thread keeps the event loop running. Android enforces this rule with `NetworkOnMainThreadException`, and browsers use separate threads for JavaScript, layout, and networking.

**Throughput and multicore hardware.** Since the mid-2000s, clock speeds have stalled and growth has come from more cores. A single-threaded program uses one core and leaves the others idle. Amdahl's law describes the ceiling: if a fraction P of a program can run in parallel on N cores, the speedup is 1 / ((1 − P) + P/N). With P = 0.9 and N = 8, the speedup is about 4.7×, not 8×. The serial 10% dominates. This is why concurrency design focuses on shrinking the serial part, such as lock hold times and shared bottlenecks.

**I/O latency hiding.** Disk, network, and database calls take milliseconds, while a CPU instruction takes fractions of a nanosecond. A thread blocked on a socket read consumes no CPU but holds its place in the program. With enough threads, the CPU stays busy while others wait. This is the most common reason servers use threads.

**Structure and modularity.** Some programs are naturally concurrent. A game has input, physics, AI, rendering, and audio, each with its own timing. Writing these as separate threads, or as cooperating tasks, often matches the problem better than one large loop.

There is also a distinction worth keeping in mind. **Concurrency** is about structuring a program to handle many things at once, even on one core. **Parallelism** is about actually running things at the same instant on multiple cores. Multithreading gives you concurrency by design and parallelism when hardware allows it.

## 4. Internal Working

### Creating a thread

When you call `pthread_create` in C or `Thread.start()` in Java, the program asks the kernel to create a new schedulable entity. On Linux that is the `clone()` system call with flags that share the memory space, file table, and signal handlers. The kernel allocates a kernel stack and a task structure, and the user-space stack is reserved, typically around 1 MB by default on 64-bit systems. Creating a platform thread usually takes tens of microseconds, which is why programs reuse threads through pools rather than creating one per task.

### Scheduling and context switches

Each core runs one thread at a time. The OS scheduler decides which runnable thread gets the core next. Modern schedulers are preemptive: a timer interrupt forces a switch when a thread's time slice expires. Linux uses the Completely Fair Scheduler, which tracks each thread's "virtual runtime" and picks the one that has received the least CPU time. Windows uses a priority-based preemptive scheduler with 32 priority levels and quanta.

A context switch saves the current thread's registers and program counter, loads the next thread's state, and switches stacks. The direct cost is usually in the low microseconds. The indirect cost is often larger: the new thread may find its data evicted from the CPU cache, and the TLB may need reloading. This is why adding threads beyond the number of cores can slow a CPU-bound program down.

### Thread states

At the OS level, a thread is New, Ready (runnable, waiting for a core), Running, Waiting (blocked on I/O, a lock, or a timer), or Terminated. Java's `Thread.State` exposes a similar model: NEW, RUNNABLE, BLOCKED (waiting to enter a `synchronized` block), WAITING, TIMED_WAITING, and TERMINATED. Reading these states in a thread dump is one of the most useful diagnostic skills for production debugging.

### Threading models

Operating systems and runtimes map user threads to kernel threads in three ways. The one-to-one model (used by Linux NPTL, Windows, and HotSpot's platform threads) gives each user thread a kernel thread, which is simple and lets the OS schedule everything. The many-to-one model (classic green threads) keeps all scheduling in user space, which is fast but cannot use multiple cores and blocks the whole process on a single blocking call. The many-to-many model (M:N) maps many user threads onto a smaller pool of kernel threads. Go's runtime uses this model with its G-M-P scheduler, and Java's virtual threads use a version of it.

### Java in particular

HotSpot maps each `java.lang.Thread` to one OS thread. Each has a stack (set by `-Xss`) and participates in the JVM's safepoint mechanism, which garbage collection uses. Blocking I/O in platform threads ties up an OS thread, which is the root reason servers needed large pools. Project Loom, delivered in Java 21 as virtual threads (JEP 444), changes this. A virtual thread is a continuation whose stack lives on the heap. When it blocks on I/O, the JVM unmounts it from its carrier thread, a platform thread in a ForkJoinPool, and that carrier runs other virtual threads. A single JVM can host hundreds of thousands of virtual threads. Blocking inside a `synchronized` block used to pin the carrier thread in Java 21 through 23, and JEP 491 (JDK 24) addressed that.

### Memory visibility

Shared memory brings a subtle hardware issue. Each core has its own cache, and writes may sit in a store buffer or cache line before other cores see them. Compilers and CPUs also reorder instructions. The Java Memory Model defines a happens-before relation: `synchronized`, `volatile` reads and writes, and `java.util.concurrent` classes create ordering guarantees. Without them, one thread can see stale values indefinitely, even though the code looks correct when read line by line.

## 5. Real-World Examples

**Tomcat and web servers.** Your passage says each HTTP request gets a separate thread. That was the classic blocking I/O model, but Tomcat today works differently. Its connector accepts connections on an acceptor thread, hands socket events to poller threads (in the NIO connector), and runs servlet code on threads from a bounded pool. The default `maxThreads` is 200. The thread pool bounds memory use and prevents the server from being overwhelmed. When all threads are busy, requests queue until a thread is free or the queue times out. Spring Boot 3.2 and later can run Tomcat on virtual threads instead, which lets a single server handle many more concurrent blocking requests.

**Video player.** A player typically separates demuxing (reading the file), video decoding, audio decoding, audio output, and UI rendering. Audio has the strictest timing, because a late buffer causes a click or dropout, so it usually has a dedicated high-priority thread. Video decoding can use several threads, one per frame slice, and frames are passed through bounded queues to the renderer.

**Banking transfers.** Transferring money reads one balance, subtracts, reads another, and adds. Two concurrent transfers from the same account can interleave and create money or lose it. Banks solve this with database transactions and row locks, and with idempotency keys so that a retried request does not charge twice. Notifications run on a separate pool, because an email provider's latency should not hold a transfer open.

**IntelliJ.** The IDE keeps the UI thread responsive while background threads index the project, run inspections, compile, and talk to version control. Indexing uses many threads and must be coordinated with the editor, which is why the IDE shows "Indexing" and sometimes limits certain features until it finishes.

**Node.js as a contrast.** Node runs JavaScript on one thread, but its libuv library uses a thread pool (four threads by default, adjustable with `UV_THREADPOOL_SIZE`) for filesystem operations, DNS lookups, and some crypto. Network I/O uses the OS event notification mechanism, epoll on Linux, kqueue on BSD, and IOCP on Windows. This is a deliberate choice: one thread avoids most locking, but any CPU-heavy code blocks everything.

**Game engines.** Unity and Unreal use a main thread for game logic, a render thread that issues GPU commands, and a job system that splits physics and animation work across cores. The jobs are designed to be independent so that they need little locking.

## 6. Advantages and Disadvantages

The advantages are real. Multithreading keeps interactive software responsive, uses multicore hardware, hides I/O latency, and lets a program's structure match its problem. A well-designed pool can serve thousands of users on a few machines.

The disadvantages are equally real, and they are why concurrency is considered one of the hardest parts of software engineering. Race conditions produce wrong results that appear only under load and cannot be reproduced reliably. Deadlocks happen when threads wait on each other in a cycle. The four Coffman conditions for deadlock are mutual exclusion, hold-and-wait, no preemption, and circular wait, and breaking any one prevents it. Livelock happens when threads keep reacting to each other without making progress. Starvation happens when a thread never gets a turn. Priority inversion happens when a low-priority thread holds a lock that a high-priority thread needs, and a medium-priority thread keeps the low-priority one from running. This nearly failed the Mars Pathfinder mission in 1997, and the fix was priority inheritance.

Other costs are less obvious. Each thread reserves stack memory. Context switches consume CPU. Lock contention turns a parallel program back into a serial one. False sharing happens when two threads write to different variables that sit on the same cache line, causing the line to bounce between cores. Debugging is harder because timing changes the behavior, and tests that pass once may fail the next time.

## 7. A Small Example: Race Condition and Fix

```java
class UnsafeCounter {
 private int count = 0;
void increment() { count++; } // read, add, write: three steps, not atomic
}

class SafeCounter {
 private final AtomicInteger count = new AtomicInteger();
void increment() { count.incrementAndGet(); } // single atomic CAS-based operation
}
```

If two threads each call `UnsafeCounter.increment()` a million times, the final value is often below two million, because increments interleave and overwrite each other. `synchronized` and `AtomicInteger` both fix this. The atomic version is usually faster under low contention, and the lock is simpler to extend when several fields must change together.

## 8. Related Concepts

**Thread pools.** Creating threads per task wastes time, so production code uses `ThreadPoolExecutor` with a core size, a maximum size, a queue, and a rejection policy. A common sizing guideline from *Java Concurrency in Practice* is N_threads = N_cpu × U_cpu × (1 + W/C), where U_cpu is the target CPU utilization, W is wait time, and C is compute time. For CPU-bound work, the pool is near the core count. For I/O-bound work, it can be much larger.

**Futures and promises.** A `Future` represents a result that will arrive later. `CompletableFuture` in Java, Promises in JavaScript, and `Task` in C# let you compose asynchronous steps without blocking.

**Thread-local storage.** `ThreadLocal` gives each thread its own copy of a value, such as a database connection or a request context. It is useful but dangerous with pools, because values leak between tasks unless cleared.

**Interrupts and cancellation.** Java uses cooperative cancellation: `Thread.interrupt()` sets a flag that blocking calls respond to. Killing a thread outright is unsafe because it can leave locks held or data half-written.

**Daemon threads.** A daemon thread does not keep the JVM alive. It is suitable for background housekeeping, but it may be stopped abruptly at shutdown.

**Thread safety and immutability.** The simplest way to avoid races is to avoid sharing mutable state. Immutable objects, confinement to one thread, and message passing remove most problems before any lock is needed.

## 9. Other Approaches to Concurrency

**Event loop and async I/O.** Node.js, Nginx, and frameworks like Netty and Vert.x run a small number of threads that react to I/O readiness events. This scales to huge numbers of connections but makes code harder to follow, since control flow is split across callbacks.

**Coroutines and async/await.** Kotlin coroutines, Python `asyncio`, C# `async/await`, and Rust's async model let code look sequential while the runtime suspends and resumes it. They reduce callback complexity while keeping the event-loop efficiency.

**Actor model.** In Erlang, Elixir, and Akka (and its open-source fork Pekko), each actor has private state and processes messages one at a time. Races on state disappear because no two threads touch the same actor. The cost is message passing overhead and a different way of thinking about design.

**CSP and channels.** Go follows Hoare's Communicating Sequential Processes. Goroutines communicate over channels, and the Go mantra is "share memory by communicating." Goroutines start with a stack of a few kilobytes, and the runtime multiplexes them onto OS threads.

**Fork/join and data parallelism.** Java's `ForkJoinPool` uses work-stealing, where idle threads take tasks from busy threads' queues. Parallel streams use it. This suits divide-and-conquer algorithms over large arrays.

**Software transactional memory.** Clojure and some research languages let you group memory operations into transactions that retry on conflict. It is elegant but has not replaced locks in mainstream systems because of performance and I/O concerns.

**Multiprocess models.** PostgreSQL historically used one process per connection, and Nginx uses worker processes. Processes give strong isolation and can be scaled across machines more easily than threads.

**GPU and SIMD.** For numerical work, data parallelism on GPUs (CUDA, Metal, Vulkan compute) and CPU vector instructions (AVX) can outperform threads by orders of magnitude.

## 10. Current Industry Standards

In 2026, a few practices define mainstream concurrency. Java 21 and later LTS releases make virtual threads the default recommendation for blocking I/O servers, and Spring Boot exposes them through configuration. Reactive frameworks like Project Reactor remain relevant for streaming workloads and backpressure. Structured concurrency, where child tasks are bound to the scope that started them so that failures and cancellation propagate cleanly, is becoming the preferred style, with Java's `StructuredTaskScope` still evolving through preview releases.

Go's goroutines and channels, Kotlin coroutines, and Rust's async runtime (Tokio) are widely used for services. Rust is notable because its `Send` and `Sync` traits make many data races compile-time errors, which is a big shift from relying on code review. Python's Global Interpreter Lock is being removed through the free-threaded build (PEP 703), which is moving toward official support, so CPU-bound Python threads can run in parallel. Containers add a twist: runtimes must read cgroup CPU limits, not the host's core count, or their thread pools will be oversized in Kubernetes.

Operationally, teams rely on thread dumps, JFR (Java Flight Recorder), async profilers, and tracing tools such as OpenTelemetry to find lock contention, pool exhaustion, and stuck threads. Load tests with realistic concurrency and chaos testing are standard before release.

## Summary

A thread is a lightweight unit of execution inside a process that shares memory with its siblings. Multithreading lets programs stay responsive, use multiple cores, and overlap waiting with work. Its costs are shared-state bugs, deadlocks, memory visibility problems, and scheduling overhead, so the best modern practice is to minimize shared mutable state, use pools and high-level concurrency utilities, and choose the model that fits the workload: threads or virtual threads for blocking I/O, parallel data structures for CPU work, and actors, channels, or event loops where the problem suits them.
# Process, Thread, Thread Creation, and Thread Lifecycle, Explained Simply

These four questions build on each other. A **process** is a running program. A **thread** is a worker inside that program. Java gives you ways to create workers, a method (`start`) that really starts one, and a lifecycle that every worker goes through. I'll explain each concept in plain language first, then go into the internals, history, trade-offs, and modern practice.

Before the details, three small corrections to the notes:

1. In the process-versus-thread table, "one thread crash can kill the entire process" is true in C and C++, where a segmentation fault takes down everything. In Java, an uncaught exception in one thread only ends that thread. The JVM keeps running unless the crash is severe (an `OutOfMemoryError` the JVM can't recover from, a native crash, or `System.exit`).
2. The `Callable` example needs generic types: `Callable<Integer>` and `Future<Integer>`. Raw types compile with warnings and lose type safety.
3. The `Thread.State` comment for `RUNNABLE` is slightly incomplete. Java reports a thread blocked in a socket read as `RUNNABLE`, because the OS sees it as active. Section 5 covers this.

## Part 1: Process versus Thread (Q2)

### The simple picture

Think of a **process** as a house. The house has walls (memory boundaries), a kitchen (the heap), a garden, a mailbox, and a front door key. Two houses are fully separate. If one catches fire, the other is untouched.

A **thread** is a person living in the house. Every person shares the kitchen, the fridge, and the Wi-Fi password. Each person has their own notebook (their stack), their own to-do list in their head (registers and the program counter), and can work on something different at the same time. People in the same house can talk easily, since they share the kitchen table. But if one person accidentally starts a fire in the kitchen, the whole house is affected.

That analogy explains almost every row of the table: separate houses are safer and slower to communicate between, while people in one house are cheaper to add and faster to coordinate, but they share risk.

### What each one owns

A process owns a virtual address space that holds the program's code, global data, heap, and the stacks for its threads. It also owns open files, sockets, a process ID, user credentials, and signal handlers. The operating system stores this information in a control block, called `task_struct` on Linux, combined with a memory descriptor.

A thread owns only what it needs to run on its own. That is its stack, its register values, its program counter (the address of the next instruction), its thread ID, its priority, and any thread-local storage. Everything else is shared with its siblings.

Linux is a good example of how this works. It doesn't have a separate "thread" object type at the kernel level. Threads are just tasks created with `clone()` flags such as `CLONE_VM` (share memory) and `CLONE_FILES` (share open files). This is why the kernel can treat threads and processes with the same scheduler code.

### The diagram, explained

Your diagram shows two processes, each with its own heap, static data, and two threads with their own stacks. The key idea is that the boxes labeled "Separate memory" are the walls of the house. Nothing inside Process A can read Process B's heap unless the two use an explicit channel like a socket or shared memory. Within one process, both threads can read and write the same heap object, which is the source of both the speed and the danger.

### Communication: IPC versus shared memory

Processes need **Inter-Process Communication (IPC)** to talk. The common mechanisms are pipes (one-way byte streams, such as `ls | grep`), named pipes, UNIX domain sockets, TCP or UDP sockets, message queues, signals, shared memory segments (`mmap`, `shm_open`), and higher-level protocols such as gRPC or HTTP. Each of these involves the kernel copying data or coordinating access, so the notes call IPC "slow." That is usually true compared with a plain memory read, but it's not always the bottleneck. Shared memory IPC can be nearly as fast as threads, since processes can map the same physical memory. The catch is that you then need synchronization, just like with threads, and you give up the crash isolation that made processes attractive.

Threads talk through plain variables and objects. A write in one thread is visible to another thread, though only after proper synchronization, which Section 4 of the earlier answer covered (the Java Memory Model).

### Creation cost and context switching

Creating a process means the kernel must build a new address space, copy or mark page tables, and set up file tables. Modern Linux uses **copy-on-write** for `fork()`. The child shares memory pages with the parent until one of them writes, so creation is cheaper than the old full-copy approach, but it is still heavier than creating a thread.

Switching between processes is expensive for a specific hardware reason. On x86, each process has its own page table, and switching to it changes the page-table base register. This invalidates many entries in the TLB (the cache that translates virtual addresses to physical ones) unless the CPU uses address-space tags like PCID. Switching between threads of the same process keeps the same page tables, so only registers, the stack pointer, and the program counter change. Cache effects still apply, but the TLB penalty mostly disappears.

### Advantages and disadvantages

| | Process model | Thread model |
|---|---|---|
| Safety | Strong isolation, one crash is contained | Shared state, a bug can corrupt everything |
| Speed of communication | IPC has overhead | Direct reads and writes |
| Resource cost | Higher memory and startup cost | Lower cost per unit of work |
| Scaling across machines | Easy, since processes can run anywhere and talk over the network | Harder, since threads live in one address space |
| Complexity | Message passing and serialization | Locks, races, and memory visibility |

Processes are a good fit when you want isolation, like running untrusted plugins, or when you want to scale across machines. Threads are a good fit when many units of work need to share large in-memory data, like a cache or a game world.

### Real-world examples

**Google Chrome** runs each browser tab, or each site, in a separate renderer process. If one site crashes or is compromised, others keep running. This is the isolation advantage at work. Inside each process, several threads handle compositing, networking, and JavaScript tasks.

**Nginx** starts a master process that reads the configuration and manages several worker processes. Each worker handles many connections using an event loop. If a worker dies, the master starts a replacement. Apache's `prefork` MPM is similar in spirit, while its `worker` MPM uses threads inside processes.

**Android** gives each app its own process by default. A new app process is forked from a preloaded "Zygote" process, which speeds up startup because the common libraries are already loaded and shared through copy-on-write.

**PostgreSQL** historically started one process per client connection. This isolates clients well but makes large numbers of connections expensive, which is why connection poolers like PgBouncer are common in production.

**A typical Java web application** runs as one process with many threads. The JVM, the application, and the connection pool all live in one address space. This is efficient, but it means a memory leak or a bad native library can take down every request at once.

### Background

Early operating systems had only processes. In the 1960s and 1970s, the process was the unit of both resource ownership and scheduling. As multicore machines and servers spread, developers wanted to run several flows of control that shared data cheaply. Lightweight processes and then POSIX threads (`pthreads`, standardized in 1995) answered that need. Today, many systems combine both: processes for isolation and threads for parallel work inside each process.

## Part 2: Creating Threads in Java (Q3)

### The simple picture

Here is a useful analogy. A **task** is a job description, such as "fetch the report and save it to disk." A **thread** is a worker who will do the job. In Java, `Runnable` is the job description, `Thread` is the worker, and `Callable` is a job description that returns a result.

The three ways to create a thread are really three ways to write the job description.

### Way 1: Extend `Thread`

```java
class MyThread extends Thread {
 @Override
 public void run() {
 System.out.println("Running in: " + Thread.currentThread().getName());
 }
}

MyThread t = new MyThread();
t.start();
```

This works, but the job description is the worker itself. The class is both the task and the mechanism for running it. Java has only single inheritance, so a class that extends `Thread` cannot extend anything else, such as a base class for your domain logic.

### Way 2: Implement `Runnable`

```java
class MyTask implements Runnable {
 @Override
 public void run() {
 System.out.println("Running in: " + Thread.currentThread().getName());
 }
}

Thread t = new Thread(new MyTask());
t.start();

Thread t2 = new Thread(() -> System.out.println("Lambda thread"));
t2.start();
```

Here the task is separate from the worker. The same `MyTask` object can be given to a plain thread today, to an `ExecutorService` tomorrow, and to a test that runs it synchronously without any threading. The lambda form, available since Java 8, is the normal way to write short tasks.

### Why `Runnable` is better

The main reasons are separation and reuse. Your business logic (what to do) stays apart from your threading logic (how and where to run it). You can change the threading strategy without rewriting the task. Also, since Java has single inheritance, implementing `Runnable` leaves your class free to extend something useful.

Think about a restaurant. If the chef is the same person as the dish, you can't send the dish to a different kitchen. If the dish is a recipe, any kitchen can cook it.

### Way 3: `Callable` for results

```java
Callable<Integer> task = () -> {
 Thread.sleep(1000);
 return 42;
};

ExecutorService executor = Executors.newSingleThreadExecutor();
try {
Future<Integer> future = executor.submit(task);
int result = future.get(); // blocks until the result is ready
 System.out.println(result); // 42
} finally {
 executor.shutdown();
}
```

`Runnable` cannot return a value and cannot throw checked exceptions. `Callable<V>` can do both. `submit` returns a `Future<V>`, a handle to a result that will arrive later.

Two details matter in practice. First, if `call()` throws an exception, `future.get()` wraps it in an `ExecutionException`, and you must unwrap it with `getCause()`. Second, `future.get()` blocks the caller. Use `future.get(timeout, TimeUnit.SECONDS)` to avoid waiting forever. `Callable` cannot be passed to the plain `Thread` constructor. It only works with executors.

### Comparison

| Feature | Extends `Thread` | Implements `Runnable` | `Callable` |
|---|---|---|---|
| Inheritance | Uses up your one superclass slot | Free to extend another class | Free to extend another class |
| Separation of concerns | Task and worker mixed | Task separate from worker | Task separate, with result |
| Reuse | Tied to `Thread` | Works with threads, executors, tests | Works with executors and futures |
| Return value | No | No | Yes |
| Checked exceptions | No | No | Yes |
| Recommendation | Avoid | Preferred for fire-and-forget tasks | Preferred when you need a result |

### Internal working

`Thread` itself implements `Runnable`. When you create `new Thread(target)`, the thread stores your `Runnable`. Its default `run()` method simply calls `target.run()`. When you extend `Thread` and override `run()`, you replace that method directly. This is why both styles work the same way at runtime. Calling `start()` causes the JVM to create an OS thread and invoke `run()` on it.

### Advantages and disadvantages

Extending `Thread` is simple for a one-off demo, but it couples your code to the threading mechanism. Implementing `Runnable` costs one extra object and an extra line, but it gives you flexibility. `Callable` adds a result channel and exception handling, but it adds complexity: you have to hold onto the `Future`, handle `ExecutionException`, and remember to shut down the executor.

A common mistake is creating a new `Thread` for every task. Each platform thread reserves stack memory (around 1 MB by default) and costs time to create. For many short tasks, use an executor instead.

### Real-world examples

A **photo upload service** might submit a `Runnable` per image to generate thumbnails. Fire-and-forget is fine for this, since the user doesn't wait for the result.

A **price comparison service** might ask five suppliers for quotes using `Callable<Quote>` tasks, then collect the futures with timeouts. Any supplier that is too slow gets dropped, and the others still count.

A **report generator** running in a scheduled job might use a `Runnable` that logs failures in its own `catch` block, since no one is waiting for a return value.

### Modern Java

In Java 21 and later, you can create a virtual thread directly with `Thread.ofVirtual().start(runnable)` or `Thread.startVirtualThread(runnable)`. The same `Runnable` interface works. You can also use `Executors.newVirtualThreadPerTaskExecutor()`, which creates a virtual thread per task. This is the current recommended approach for blocking I/O. `CompletableFuture` is the modern alternative to `Future` when you want to compose asynchronous steps without blocking.

### Background

`Runnable` and `Thread` have been in Java since JDK 1.0 in 1996. `Callable`, `Future`, and `ExecutorService` came in Java 5 (2004) with `java.util.concurrent`. Lambdas in Java 8 (2014) made `Runnable` and `Callable` much shorter to write. The history reflects a steady move from "thread as the unit of work" to "task as the unit of work, with threads as an implementation detail."

## Part 3: `start()` versus `run()` (Q4)

### The simple picture

Imagine you hire a chef to cook dinner. Calling `run()` is like walking into your own kitchen and cooking yourself. You do the work on your own time, and nothing else happens until you finish. Calling `start()` is like hiring a second chef and telling them to cook. Now you can set the table while they cook.

Both approaches produce the same dish, but only `start()` gives you parallel work.

### What the code shows

```java
Thread t = new Thread(() -> {
 System.out.println("Thread: " + Thread.currentThread().getName());
});

t.run(); // Thread: main <- runs in the CALLER's thread, no new thread
t.start(); // Thread: Thread-0 <- runs in a NEW thread
```

With `run()`, the lambda executes right away inside the `main` thread, like any other method call. With `start()`, the JVM creates a new thread named `Thread-0`, and the lambda runs there while `main` continues.

### What `start()` does internally

When you call `start()`, the JVM does the following. It checks that the thread hasn't already been started. It asks the operating system to create a new native thread, with its own stack and register state. It moves the thread from `NEW` to `RUNNABLE`. The OS scheduler eventually gives that thread a core, and the JVM then calls `run()` on the new thread. Your `main` thread gets control back immediately, without waiting.

`start()` is a native method. Most of the work happens in C++ code inside the JVM, not in Java code.

### What `run()` does

`run()` is an ordinary method call. It pushes a frame onto the current thread's stack, executes the code, and returns. No new thread is created, no scheduler is involved, and the caller waits until `run()` finishes.

### Can you call `start()` twice?

No. A thread can be started only once.

```java
Thread t = new Thread(() -> System.out.println("Hello"));
t.start(); // OK
t.start(); // IllegalThreadStateException
```

Once a thread reaches `TERMINATED`, it cannot return to `NEW`. If you need to run the task again, create a new `Thread` object, or better, submit the task to an executor. This one-way lifecycle is also why the JVM can clean up threads safely.

### An important difference in exception handling

When `run()` throws an exception, it propagates to the caller, which is often what you want. When a started thread's `run()` throws an uncaught exception, the exception does not reach the code that called `start()`. Instead, it goes to the thread's uncaught exception handler, which by default prints `Exception in thread "Thread-0" ...` to standard error. The other threads and the JVM keep going. This is one reason the "one thread crash" concern from Part 1 is less severe in Java than in C.

### Advantages and disadvantages

`start()` gives you real concurrency, responsive interfaces, and parallel use of multiple cores. It costs an OS thread, some memory, and extra complexity: you must now think about shared data, synchronization, and what happens if the thread is still running when the program ends.

`run()` is simpler and has no overhead, but it executes synchronously and gives you no concurrency. It is useful for testing a `Runnable` in isolation, or when you deliberately want to run something on the current thread.

### Real-world example and a common bug

A desktop app has a "Download" button. The developer writes `downloadThread.run()` instead of `downloadThread.start()`. The button handler runs the download on the UI thread, so the window freezes for the whole download. The fix is a single word, but this bug is common enough that many IDEs and linters warn about calling `run()` on a `Thread`.

### Background

In the earliest Java threading model, `start()` was the only way to create the OS thread, and `run()` was just the method the JVM called. The split reflects a design rule that still applies: `run()` describes what to do, and `start()` decides where and when to do it.

## Part 4: The Thread Lifecycle (Q5)

### The simple picture

A thread's life is like a job in a company. It is first **hired** (NEW), then it **starts work** (RUNNABLE). Sometimes it has to **wait for a shared resource**, like the only photocopier (BLOCKED). Sometimes it **waits for a specific event**, like a manager's approval (WAITING). Sometimes it **waits for a set amount of time**, like a 5-minute coffee break (TIMED_WAITING). At the end, it **finishes and goes home** (TERMINATED).

### The six states

| State | Plain meaning | How a thread gets here |
|---|---|---|
| `NEW` | Created but not started | `new Thread(...)` |
| `RUNNABLE` | Ready to run or currently running | `start()`, or getting back a lock or CPU |
| `BLOCKED` | Waiting to enter a `synchronized` block | Another thread holds that monitor lock |
| `WAITING` | Waiting with no time limit | `wait()`, `join()`, `LockSupport.park()` |
| `TIMED_WAITING` | Waiting with a time limit | `sleep(ms)`, `wait(ms)`, `join(ms)`, `parkNanos` |
| `TERMINATED` | Finished | `run()` returned, or an uncaught exception ended it |

### The diagram, corrected

The diagram in your notes is correct in spirit but misleading in one place. `RUNNABLE` is drawn as a single box that combines "ready" and "running." That matches Java's view: the OS may have the thread on a core, or it may be in the run queue waiting for one, and Java reports both as `RUNNABLE`. The arrows back into `RUNNABLE` from `BLOCKED`, `WAITING`, and `TIMED_WAITING` mean the thread has become eligible to run again, not that it is running at that instant.

### Walking through the example

```java
Thread t = new Thread(() -> {
 try { Thread.sleep(5000); } catch (InterruptedException e) {}
});
System.out.println(t.getState()); // NEW
t.start();
System.out.println(t.getState()); // RUNNABLE
Thread.sleep(100);
System.out.println(t.getState()); // TIMED_WAITING (sleeping)
t.join();
System.out.println(t.getState()); // TERMINATED
```

The first line shows a thread that exists as an object but has no OS thread yet. After `start()`, it is `RUNNABLE`. The `sleep(5000)` inside the task moves it to `TIMED_WAITING`. The main thread's `Thread.sleep(100)` gives it time to get there, so the output is reliable here, though in real code you should never rely on sleeps for ordering. `join()` makes the main thread wait for `t` to finish, and then `t` is `TERMINATED`.

### BLOCKED versus WAITING

These two are often confused, and the difference matters when you read a thread dump.

`BLOCKED` means a thread is trying to enter a `synchronized` block or method and another thread owns that lock. It is trying to acquire a monitor.

`WAITING` means a thread has voluntarily given up the CPU and is waiting for a signal. That includes `wait()`, `join()`, and most `java.util.concurrent` locks. `ReentrantLock.lock()` parks the thread and shows `WAITING`, not `BLOCKED`, which surprises many developers.

### The `RUNNABLE` subtlety

A thread blocked in a socket read, or waiting for a file read, usually shows as `RUNNABLE` in a platform-thread JVM. The OS has the thread inside a system call, and the JVM doesn't know whether it's making progress. This means `RUNNABLE` in a thread dump does not automatically mean the thread is using the CPU. Look at the stack trace to see whether it's sitting in `SocketInputStream.read`.

### Why this matters in production

Thread states are one of the best diagnostic tools you have. Take a thread dump with `jstack`, `jcmd <pid> Thread.print`, or a monitoring tool, and read the states.

If hundreds of threads are `BLOCKED` on the same lock, you have lock contention. A synchronized method is doing too much work, or a shared cache is a bottleneck. If many threads are `WAITING` on a connection pool's `park`, your pool is exhausted, and downstream databases may be slow. If threads are `TIMED_WAITING` on `sleep`, a retry or backoff loop is stuck. If `RUNNABLE` threads are all in the same CPU-heavy method, you have a hot spot.

Real incidents often follow this pattern. A service slows down, the team takes a dump, and sees 150 threads `BLOCKED` on one `synchronized` logger or cache method. The fix might be as small as replacing the lock with a `ConcurrentHashMap`.

### Advantages and disadvantages of the state model

The model is simple, standard across Java versions, and works well with monitoring tools. Its weakness is that it hides details: `RUNNABLE` covers both useful work and blocked I/O, and it does not show which resource a `WAITING` thread is waiting for, unless you read the stack trace or the lock information in the dump.

### Background and current practice

`Thread.State` and `getState()` were added in Java 5 along with the `java.util.concurrent` package. The six states are a simplification of the operating system's model, which typically has "new," "ready," "running," "waiting," and "terminated." Java merges "ready" and "running" into `RUNNABLE` and splits waiting into `BLOCKED` (for monitors) and `WAITING`/`TIMED_WAITING` (for everything else).

Virtual threads, introduced in Java 21, have states that follow the same names, though a virtual thread that is parked on I/O is unmounted from its carrier thread, so it doesn't hold an OS thread while waiting. Newer JDKs can also write thread dumps in structured formats such as JSON, which tools can parse more easily than the classic text format.

## Part 5: Related Concepts and Current Industry Practice

### Where these ideas show up today

Most modern systems use these four ideas together. A **process** is the deployment boundary: a container runs one process (or a few), and Kubernetes scales by adding pods, which are processes in disguise. Within each process, **threads or virtual threads** handle requests, and each **task** is a `Runnable`, `Callable`, or lambda handed to an executor. The **lifecycle** determines whether a service is healthy under load, which is why observability tools show thread-state graphs by default.

### Modern shifts

Java 21 and later use virtual threads for blocking I/O, which makes the "one thread per request" model cheap again. Spring Boot can run Tomcat on virtual threads through configuration. Structured concurrency (`StructuredTaskScope`, still in preview) ties child tasks to a parent scope, so failure and cancellation propagate cleanly. `CompletableFuture` and reactive libraries handle asynchronous composition when non-blocking code is the right choice.

Other languages make the same distinctions. Go's goroutines are closer to virtual threads, and Go's channels are the communication primitive. Rust's `std::thread::spawn` takes a closure, which is like a `Runnable`, and its `JoinHandle` plays the role of a `Future`. The compiler's `Send` and `Sync` traits prevent unsafe sharing at compile time. Python's `threading` module and the `concurrent.futures` executor mirror Java's executors, though the Global Interpreter Lock limits CPU parallelism until the free-threaded build is standard.

### Advice that still holds

Prefer tasks over threads. Use `Runnable` or `Callable` as the unit of work and an executor to run them. Use `start()` to create real concurrency, and never call `run()` expecting a new thread. Remember that threads cannot be restarted. Learn the six states well enough to read a thread dump in five minutes, since that skill solves more production problems than almost any other concurrency knowledge.

## Quick Summary

| Concept | Simple meaning | Key takeaway |
|---|---|---|
| Process vs thread | Separate house vs people in one house | Processes are safer, threads are cheaper and faster to share data |
| Two ways to create threads | Job description, or the worker | Use `Runnable` (or `Callable` for results), not `extends Thread` |
| `start()` vs `run()` | Hire a new chef vs cook yourself | `start()` creates a thread, `run()` is just a method call |
| Lifecycle | A job's journey from hire to finish | Know NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED, and what each means in a thread dump |
# Synchronization and Thread Safety, Explained Simply

Part 1 showed that threads share memory, which is both their power and their danger. This part covers the danger: what goes wrong when threads touch the same data, and how Java's tools (`synchronized` and locks) keep that data safe.

Several examples in your notes would mislead you in production, so here are the corrections first:

1.**Biased locking** appears in the lock escalation diagram. It was deprecated in JDK 15 (JEP 374) and is disabled by default in current releases. The three-level model is an old description. Today, uncontended locks use a fast atomic operation, and contended locks inflate into a full monitor that can park threads.
2.**The method-versus-block transfer** locks only `this`, which does not protect `to.balance`. Each account's data needs that account's lock. The block version also breaks atomicity: money leaves one account before it arrives in the other, so a reader can see a wrong total. A correct transfer needs nested locks taken in a consistent order.
3.**The `tryLock` deadlock example** won't compile as written, because `tryLock(timeout)` throws `InterruptedException`. Section 4 shows a corrected version.
4.**The thread-priority starvation example** depends on Java priorities, which HotSpot on Linux ignores by default. Starvation is real, but it usually comes from unfair locks, overloaded pools, and long-running tasks.
5.**The livelock example** reads a non-volatile flag from another thread. The JIT compiler may hoist that read out of the loop, so the program can hang for a reason unrelated to livelock.

## Part 1: Race Condition (Q6)

### The simple picture

Imagine a shared whiteboard showing a balance of 1000 dollars. Two people want to withdraw. Both look at the board at the same moment, see 1000, and decide there's enough. Both take their money. The board now shows a negative number. Both decisions were based on information that was already out of date.

That is a race condition. The program's correctness depends on which thread happens to win the timing race. The most common shape is **check-then-act**: you check a condition, then act on it, but the condition can change between the check and the action.

### What happens step by step

| Step | Thread A | Thread B | balance |
|---|---|---|---|
| 1 | checks `1000 >= 800`: true | | 1000 |
| 2 | | checks `1000 >= 500`: true | 1000 |
| 3 | `balance -= 800` (now 200) | | 200 |
| 4 | | `balance -= 500` (now -300) | -300 |

Both threads passed the check on the same stale value of 1000. Neither did anything wrong in isolation. The bug lives in the gap between the check and the update.

### Why even the update is risky

Even without the check, `balance -= amount` is three separate operations: read the field, subtract, and write the result back. Two threads can interleave those steps. If both read 1000, then subtract 800 and 500, and write back 200 and 500 in that order, the balance shows 500 even though 1300 was dispensed. This is a **lost update**. The `count++` example from Part 1 has exactly the same problem.

There is also a visibility issue. Without synchronization, one thread may keep seeing an old value that another thread has already changed, because each core has its own cache and the compiler may reorder instructions.

### Data race versus race condition

These two terms are often mixed up. A **data race** is a low-level event: two threads access the same memory without synchronization, and at least one of them writes. A **race condition** is a higher-level logic bug: the program's correctness depends on timing. You can have race conditions with no data races, and the reverse is also possible. For example, a thread-safe list protects each individual call, but "if the list is empty, add an item" written as two separate calls is still a race condition. Each call is safe on its own, and the combination is not.

### Advantages and disadvantages

Race conditions have no advantages. Their real cost is how hard they are to find. They are nondeterministic, so they may pass every test run and fail once a week in production under load. Adding a debugger or a log line changes the timing and can hide the bug. This is why people call them Heisenbugs.

The consequences can be serious. The Therac-25 radiation therapy machine, in the mid-1980s, had a race condition between operator input and the machine's setup task. Several patients received massive overdoses. In 2003, a race condition in an alarm processing system contributed to the Northeast blackout, which left about 50 million people without power in the United States and Canada. More everyday examples include two shoppers buying the last item in stock, two customers booking the same seat, metrics counters that undercount, duplicate IDs generated under load, and file-system bugs where a program checks that a file doesn't exist and then creates it, a pattern attackers exploit as a "time-of-check to time-of-use" vulnerability.

### Background

The term "race" comes from digital circuit design, where an output depends on which of two signals arrives first. Software inherited it. Edsger Dijkstra introduced the mutual exclusion problem in 1965, and most of the tools in this part of the notes descend from that work.

## Part 2: `synchronized` and How It Works Internally (Q7)

### The simple picture

Think of a room with one key. Only the person holding the key can enter. Every Java object has a hidden key called a **monitor** (or intrinsic lock). The `synchronized` keyword means: take the key before entering the protected code, and return it when you leave, even if you leave by an exception. Anyone else who wants the key waits in line.

### The three forms

The synchronized method locks the object itself (`this`). The synchronized block locks any object you name, which lets you choose exactly what to protect. The static synchronized method locks the `Class` object, which is a separate lock from any instance's lock. This means a static synchronized method and an instance synchronized method don't block each other, which surprises many developers.

Two details are often overlooked. First, Java locks are **reentrant**: a thread that already holds a lock can enter another synchronized block on the same lock. Without this, a synchronized method calling another synchronized method on the same object would deadlock itself. Second, `synchronized` provides visibility as well as mutual exclusion. This is why the notes' `getBalance()` is also synchronized. Without it, a reader might see a stale balance even after the writer has released the lock.

### The object header and the monitor

Every Java object has a header. On a 64-bit JVM, the header includes a **mark word**, which stores the identity hash code, GC age bits, and lock state bits. When a lock is acquired, the JVM changes the mark word to record the lock's state.

Current HotSpot works roughly in these stages, though the exact details change between JDK releases, so treat them as a mental model:

When there is no contention, the thread copies the header into a small lock record on its own stack. It then uses a **compare-and-swap (CAS)** instruction to point the object's header at that record. A CAS is one atomic hardware instruction, so the cost is small but not zero.

When another thread tries to take the lock briefly while the first one holds it, the CAS fails. HotSpot spins for a short time, hoping the owner releases soon.

When contention lasts longer, the lock is **inflated** into an `ObjectMonitor`, a native structure that tracks the owner, a recursion count, an entry queue, and a wait set. Waiting threads are parked by the operating system. Parking and waking involve a context switch, which is the expensive part.

Older JDKs had an extra stage called biased locking, which let a single thread re-enter a lock with almost no cost. It was removed from the default configuration because modern hardware and lock implementations made it less useful, and because it complicated the JVM.

### `wait()`, `notify()`, and Java's monitor style

Every object's monitor also has a wait set. `wait()` releases the lock and parks the thread until another thread calls `notify()` or `notifyAll()`. C. A. R. Hoare described monitors in 1974. Java follows the Mesa-style variant described by Lampson and Redell in 1980, where a woken thread must check its condition again, because another thread may have changed it first. That is why the correct pattern is always:

```java
synchronized (lock) {
 while (!conditionIsTrue()) { // "while", not "if"
 lock.wait();
 }
 // act on the condition
}
```

### Advantages and disadvantages

`synchronized` is simple, built into the language, and hard to misuse in the basic sense. It releases the lock automatically on exceptions, which prevents a common class of bugs that manual locks cause. It is reentrant, gives visibility guarantees, and is cheap when uncontended. The JIT compiler can also remove locks on objects that never escape a method, a technique called lock elision.

Its limitations are real. You cannot set a timeout on acquiring it, you cannot use `tryLock`, and a thread waiting to enter a synchronized block cannot be interrupted. Each object has only one wait set, so you cannot separate conditions cleanly. It is not fair: a thread that releases the lock can often grab it again before queued threads run. And because any code holding a reference to your object can synchronize on it, you should lock on a private object.

### Real-world examples and history

Java's original collections showed the cost of synchronizing everything. `Vector` and `Hashtable` lock every method, which is slow for single-threaded code. Modern code uses `ArrayList` with external synchronization when needed, or `ConcurrentHashMap` for shared maps. `StringBuffer` (synchronized) was largely replaced by `StringBuilder` (unsynchronized) for the same reason.

A common real-world pattern is a lazily initialized singleton. Double-checked locking with `synchronized` works only if the field is declared `volatile`, because without it another thread might see a partially constructed object.

There is one current-practice note. On Java 21 through 23, a virtual thread that blocks inside a `synchronized` block stays pinned to its carrier thread, which can exhaust the carrier pool. JEP 491 in JDK 24 addresses this. On older versions where virtual threads are heavily used, `ReentrantLock` is the safer choice.

## Part 3: Synchronized Method versus Synchronized Block (Q8)

### The simple picture

Imagine you need only the fridge in a shared house. A synchronized method is like locking the whole house for the entire time you're in the kitchen. A synchronized block is like locking just the fridge, for just the time you need to take out the milk. The rule is: **lock smaller, lock shorter.**

### What is really protected

The notes' transfer example is instructive, but it hides two problems. A synchronized method on `this` protects only `this.balance`. The line `to.balance += amount` is not protected, because `to` has its own state and needs its own lock. The notes also suggest that the block version is fine, but splitting the transfer into two separate locks means that money briefly disappears from one account before it appears in the other. Any reader who sums balances during that gap sees the wrong total.

A correct transfer must hold both locks at the same time, and must acquire them in a consistent order to avoid deadlock (covered in Part 4):

```java
class Account {
 private final int id; // unique, never changes
 private int balance;
 private final Object lock = new Object(); // private, not visible outside

 Account(int id, int balance) { this.id = id; this.balance = balance; }

 static boolean transfer(Account from, Account to, int amount) {
Account first = from.id < to.id ? from : to; // always lock lower id first
Account second = (first == from) ? to : from;

 synchronized (first.lock) {
 synchronized (second.lock) {
 if (from.balance < amount) return false; // the Q6 check, now safe
 from.balance -= amount;
 to.balance += amount;
 return true;
 }
 }
 }
}
```

The check and the update both happen while both locks are held, so the Q6 race disappears. If `from` and `to` are the same account, the nested lock on the same object is allowed because locks are reentrant.
### Comparison

| Feature | Synchronized method | Synchronized block |
|---|---|---|
| Lock object | `this`, or the `Class` object for static methods | Any object you choose |
| Lock scope | Entire method body | Only the critical section |
| Flexibility | Low: one lock for the whole method | High: different locks for different data |
| Contention risk | Higher, since the lock is held longer | Lower, since the lock is held briefly |
| Code clarity | Very clear | More code, easier to get wrong |
| Typical use | Small, self-contained classes | Larger classes and multi-lock operations |

### Advantages and disadvantages

The synchronized method is hard to get wrong because the entire method is protected, but that protection often covers work that doesn't need it, such as logging, validation, or calling other services. The block version is more efficient and gives control over which data is protected, but it is easier to forget a lock on one path, and it becomes dangerous once multiple locks are involved.

### Rules of thumb that matter in production

Never call slow or unknown code while holding a lock. A checkout method that holds a stock lock while calling a payment gateway that takes two seconds will serialize every checkout behind that call. Joshua Bloch and Brian Goetz both call this out: calls to "alien" methods, meaning code you don't control, should happen outside locked regions.

Protect invariants, not individual fields. If `balance` and `lastUpdated` must change together, one lock must cover both. Use private final lock objects so outside code can't interfere. Finally, prefer existing concurrent classes such as `ConcurrentHashMap`, `AtomicInteger`, and `LongAdder` over hand-written locking when they fit your problem.

## Part 4: Deadlock (Q9)

### The simple picture

Two people each hold one pen and want the other person's paper. Neither will give up what they hold until they get the other item, so neither can finish. Deadlock is the same situation with threads: each thread holds a lock and waits for a lock that another thread holds, and nothing moves.

### Walking through the example

| Step | Thread 1 | Thread 2 |
|---|---|---|
| 1 | acquires `lockA` | acquires `lockB` |
| 2 | sleeps while holding `lockA` | sleeps while holding `lockB` |
| 3 | requests `lockB`: BLOCKED | requests `lockA`: BLOCKED |
| 4 | waits forever | waits forever |

The `sleep(100)` makes the deadlock reliable for demonstration. In real systems, the same interleaving happens less predictably, which is why deadlocks often appear only under load.

### The four Coffman conditions

Deadlock requires all four of these conditions at once, as described by Coffman, Elphick, and Shoshani in 1971. Mutual exclusion means a resource can be held by only one thread. Hold and wait means a thread holds one resource while requesting another. No preemption means a held lock cannot be taken away. Circular wait means a chain of threads each waits for the next, which closes into a loop. Breaking any one condition prevents deadlock. Lock ordering breaks circular wait. Acquiring all locks at once, or releasing everything on failure, breaks hold-and-wait. Timeouts break no-preemption. Mutual exclusion is hard to remove for real shared resources, but lock-free designs reduce how often it matters.

### Prevention in code

Lock ordering is the most common solution. If every thread acquires `lockA` before `lockB`, a cycle cannot form. The notes' version of this is correct.

A timeout-based approach uses `ReentrantLock`, since `synchronized` cannot time out. The notes' version doesn't compile, so here is a corrected form:

```java
static boolean tryWork(ReentrantLock first, ReentrantLock second) throws InterruptedException {
 if (!first.tryLock(1, TimeUnit.SECONDS)) return false;
 try {
 if (!second.tryLock(1, TimeUnit.SECONDS)) return false;
 try {
 // critical work
 return true;
 } finally {
 second.unlock();
 }
 } finally {
 first.unlock();
 }
}
```

If the caller gets `false`, it should wait a random short time before retrying. Without the randomness, two threads that both back off at the same moment can keep colliding, which leads to livelock (Part 5).

The notes' "single lock for related resources" approach is the simplest correct choice when the resources are always updated together. Its cost is reduced concurrency.

### Detection

The notes' `ThreadMXBean.findDeadlockedThreads()` is the programmatic check. `jstack` also reports deadlocks, printing a "Found one Java-level deadlock" section. Monitoring agents typically run this check periodically. Its limitation is that it detects only deadlocks among Java monitors and `java.util.concurrent` locks. It cannot see deadlocks that involve database locks, external services, or native resources, and it cannot see livelock.

Databases handle this differently. PostgreSQL detects deadlocks after `deadlock_timeout` (1 second by default) and aborts one transaction. MySQL InnoDB detects them immediately and rolls back a "victim" transaction. Application code must retry in both cases, so the database deadlock error is an expected event, not an unusual crash.

### Real-world examples

Two concurrent transfers, one from A to B and one from B to A, are the classic case, which is why transfer code always locks in a fixed order. Database deadlocks happen when two transactions update the same rows in different orders. Microservices can also deadlock in a broader sense when two services wait synchronously for each other, which is why many teams use timeouts on every remote call.

### Advantages and disadvantages of prevention

Lock ordering requires discipline across the whole codebase, which is hard once many teams contribute. It must be documented and enforced, often through code review or static analysis. `tryLock` with timeouts adds retry logic and the risk of livelock. Simplifying locking, such as using one lock, reduces deadlock risk but limits parallelism. There is no free solution, only trade-offs.

### Background

Dijkstra's dining philosophers problem, posed in the 1960s, illustrates the circular wait. Five philosophers each need two forks, and if each picks up the left fork at the same time, all wait forever. Operating system textbooks also describe the banker's algorithm, which avoids deadlock by refusing requests that could lead to an unsafe state. Real systems rarely use it, because they can't know the maximum resource needs of every thread in advance.

## Part 5: Livelock and Starvation (Q10)

### Livelock: busy but going nowhere

Imagine two people in a narrow hallway. Each steps aside to let the other pass. They both step to the same side, then both step back, and repeat. Both are active and polite, but neither gets past. That's livelock. Threads are running, CPU usage may be high, and no useful work is being done.

The corrected version of the notes' example makes the flag `volatile`, so each thread sees the other's updates:

```java
class Worker {
 private volatile boolean active = true; // visible to other threads

 public void work(Worker other) {
 while (active) {
 if (other.active) {
 // back off for a random time so we don't mirror each other
 try { Thread.sleep(ThreadLocalRandom.current().nextInt(10, 100)); }
 catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
 continue;
 }
 // do work...
 active = false;
 }
 }
}
```

The random sleep is the key fix. Without randomness, the threads stay synchronized and keep repeating the same pattern. `Thread.yield()` is only a hint to the scheduler, so relying on it for correctness is risky.

Real-world examples of livelock are common. Early Ethernet solved collisions using binary exponential backoff with random delays, which is livelock prevention in practice. Retry storms in microservices are another case: two services retry each other's failing calls at the same moment and keep overloading each other, and the fix is exponential backoff with jitter. Livelock is harder to detect than deadlock, because the CPU looks busy. The useful signal is throughput dropping to zero while CPU usage stays high.

### Starvation: waiting forever while others go first

Starvation happens when a thread never gets the resource it needs, even though the system as a whole keeps working. A simple analogy is a busy counter where new customers keep being served first while one person waits indefinitely.

The notes' priority example is misleading. HotSpot on Linux ignores Java thread priorities by default, so the low-priority thread may run normally. On Windows, priorities do affect scheduling. Starvation doesn't depend on priorities in most real code. The more common causes are the following.

**Unfair locks.** `synchronized` does not queue waiting threads in order. A thread that just released the lock can grab it again before a waiting thread wakes up. This "barging" can starve threads under heavy contention. `new ReentrantLock(true)` makes `lock()` honor first-in, first-out order. However, `tryLock()` still barges even in fair mode, and fair locks are often noticeably slower under contention. The cost varies with the workload, so measure it rather than trusting a fixed percentage.

**Read-write lock writer starvation.** If readers keep arriving, a writer waiting for the lock may never get it. Fair mode or `StampedLock` can help.

**Thread pool starvation deadlock.** This is the most important production case. Suppose a pool has ten threads, and each task submits a subtask to the same pool and then waits for it. All ten threads end up waiting for subtasks that are queued behind them and can never run. The system stops, even though no lock is held in the traditional sense. The fix is to use separate pools for different kinds of work, avoid blocking on tasks from the same pool, or chain asynchronous steps with `CompletableFuture`.

**Priority schedulers.** A task scheduler that always runs the highest-priority task can starve low-priority work indefinitely. The standard fix is priority aging, where a waiting task's priority rises over time.

### Advantages and disadvantages of the fixes

Randomized backoff is simple and effective, but it adds latency and makes behavior less predictable. Fair locks prevent starvation at the cost of throughput. Separating thread pools prevents starvation deadlocks but uses more threads and more memory. Each fix trades one problem for another, which is why measuring under realistic load matters more than picking a rule.

## Part 6: Putting It Together, and Current Industry Practice

The most reliable way to avoid these problems is to share less mutable state. Immutable objects (records in modern Java, `final` fields, `List.copyOf`) need no locks at all. Confining data to a single thread, or passing messages between threads through queues, removes races by design. When sharing is necessary, prefer tested building blocks: `ConcurrentHashMap` for shared maps, `AtomicInteger` and `AtomicReference` for lock-free updates using CAS, `LongAdder` for high-contention counters, `BlockingQueue` for producer-consumer work, `Semaphore` and `CountDownLatch` for coordination, and `ReentrantLock` with timeouts when you need more control than `synchronized` provides.

Tooling has improved a lot. ThreadSanitizer finds data races in C and C++, and Go's race detector does the same for Go programs. Rust's compiler rejects many data races before the program runs, through its `Send` and `Sync` traits. In Java, Error Prone can check `@GuardedBy` annotations, JCStress tests concurrent behavior under stress, and Java Flight Recorder records monitor contention events, which help find hot locks in production.

For thread dumps, look for many threads in `BLOCKED` on one monitor (a hot lock), many in `WAITING` on the same pool (pool exhaustion), and nothing progressing while CPU is high (possible livelock). This connects directly to the lifecycle states from Part 1.

## Quick Summary

| Concept | Simple meaning | Key takeaway |
|---|---|---|
| Race condition | Outcome depends on timing | Check-then-act and read-modify-write are not atomic |
| `synchronized` | One key per object, held while inside | Reentrant, releases on exceptions, provides visibility |
| Method vs block | Lock the whole house vs the fridge | Lock small and short, but protect every piece of shared data |
| Deadlock | Two threads each wait for the other's lock | Break one of the four conditions: usually lock ordering or timeouts |
| Livelock | Both keep politely yielding, no progress | Add random backoff and avoid lockstep retries |
| Starvation | Some thread never gets its turn | Watch for unfair locks, pool starvation, and overloaded schedulers |
# volatile, wait/notify, join/sleep, and ThreadLocal, Explained Simply

These five concepts are the tools threads use to coordinate. `volatile` makes a value visible across threads without locking. `wait`, `notify`, and `notifyAll` let threads sleep until something changes. `join` and `sleep` control timing between threads. `ThreadLocal` gives each thread its own private copy of a value. Each one solves a different problem, and each has a sharp edge that's easy to miss.

Several parts of your notes need correcting before the explanations:

1.**The `volatile` stop-flag problem** is mostly caused by the JIT compiler, not by stale CPU caches. Modern CPU caches are coherent, so the compiler is the real culprit: it reads `running` once, keeps it in a register, and never looks again. The "flushes to main memory" wording is a simplification. The real mechanism is memory barriers plus compiler restrictions.
2.**`wait()` releases only the monitor of the object you call it on.** If the thread also holds other locks through nested `synchronized` blocks, those stay locked while it waits. That's a common deadlock source.
3.**The `notify()` producer-consumer claim is overstated.** In a simple bounded buffer, only one kind of thread waits at a time, so `notify()` is less dangerous there. It becomes unsafe as soon as waiters need different conditions, as Section 3 shows.
4.**The `ThreadLocal` memory description is incomplete.** The weak reference is on the `ThreadLocal` key, not the value. Values are held strongly, so a value can outlive its key until the map cleans it up.
5.**The `TransactionContext` example never calls `remove()`** and never closes the connection, so it leaks in exactly the way the notes warn about.

## Part 1: The `volatile` Keyword (Q11)

### The simple picture

Imagine a shared whiteboard that shows whether a shop is open. Each employee who needs to know copies the sign onto their own notepad at the start of the day and never looks at the whiteboard again. When the owner closes the shop and updates the board, the employees keep working as if it's still open.

That's what happens to the `running` flag. `volatile` tells everyone: "always check the whiteboard, never rely on your notepad."

### Why the loop may never end

Without `volatile`, the JIT compiler sees a loop that reads `running` but never writes it. It reasons that the value cannot change during the loop, so it effectively rewrites the code as:

```java
boolean r = running; // read once
while (r) { } // never re-reads
```

The compiler is allowed to do this under the Java Memory Model, because without synchronization, nothing forces it to re-read the field. The loop runs forever, even though another thread has set `running = false`.

Declaring the field `volatile` forbids this optimization. Every read of a volatile field must observe the most recent write in the program's synchronization order.

### What `volatile` actually guarantees

`volatile` gives three guarantees. The first is **visibility**: each read sees the latest write by any thread. The second is **ordering**: a write to a volatile variable happens-before every later read of that same variable. Everything the writer did before the volatile write is visible to a reader that reads the volatile variable afterward. This is what makes the pattern "prepare the data, then set a volatile ready flag" safe. The third is **atomicity for long and double**. Without `volatile`, the JVM may write a 64-bit `long` as two 32-bit halves, so another thread could see a half-updated value. A `volatile long` is always read and written as a whole.

What `volatile` does not guarantee is atomicity of compound actions. `counter++` is still read, add, and write. Two threads can interleave and lose an update, as shown in Part 2 of the earlier answer.

### Internal working

The Java Memory Model, revised in JSR-133 for Java 5 (2004), defines `volatile` through the happens-before relationship. The hardware mechanism underneath uses memory barriers.

CPU writes do not go straight to shared memory. They first sit in a store buffer, and later reads can overtake them. A volatile write must become visible before subsequent reads, which requires a **StoreLoad barrier**. On x86, HotSpot emits a locked instruction that acts as this barrier, which is why volatile writes cost more than plain writes. On x86, ordinary loads already have acquire semantics, so volatile reads are cheap. On ARM, the JVM uses special load-acquire and store-release instructions.

The compiler is also restricted. It cannot move ordinary reads and writes across a volatile access in ways that break the happens-before rules.

### Advantages and disadvantages

`volatile` is lightweight. It involves no locks, no blocking, and no thread parking. Reads are nearly free on x86, and writes cost one barrier. It is perfect for a single flag or a reference that is swapped as a whole.

It is also easy to misuse. It cannot protect a read-modify-write, cannot protect multiple variables that must change together, and gives no hint that a check-then-act is broken. Code that uses `volatile` where it needs a lock often passes tests and fails rarely in production.

### Real-world examples

The most common use is a shutdown flag for a worker loop, such as `volatile boolean closed` in a connection pool or a log shipper. A better modern approach is `Thread.interrupt()`, which also wakes blocked calls.

Another is safe publication of configuration. A service holds a `volatile Config` reference. On reload, it builds a new immutable `Config` and swaps the reference. Readers never lock and always see a complete object:

```java
class ConfigHolder {
 private volatile Config config; // Config is immutable
Config current() { return config; }
void reload(Config fresh) { config = fresh; }
}
```

The JDK itself uses this pattern. `FutureTask` keeps its state in a volatile field.

The double-checked locking singleton needs `volatile` for the same reason. Without it, another thread can see a non-null reference to an object whose fields are not yet initialized. The holder idiom (a static nested class) is usually simpler and avoids the problem completely.

### Background and a common misconception

C and C++ also have a `volatile` keyword, but it only prevents the compiler from optimizing away reads and writes, mainly for hardware registers and signal handlers. It is not a thread-safety tool. Use `std::atomic` in C++ and `atomic` types in C11 for threads. Java's `volatile` is closer to C++ atomics with sequentially consistent semantics, and that is why it is safe in ways C's `volatile` is not.

Modern alternatives include `AtomicBoolean` for flags, and `VarHandle` (Java 9+), which exposes finer memory-access modes such as acquire and release when you need them.

## Part 2: `wait()`, `notify()`, and `notifyAll()` (Q12)

### The simple picture

Think of a bakery display case that holds three loaves. The baker (producer) adds loaves and must wait when the case is full. The customer (consumer) takes loaves and must wait when the case is empty. Neither can stand at the counter forever, blocking the other, so each one sits down and waits for a bell. When the case changes state, the other side rings the bell.

In Java, the bell is `notify` or `notifyAll`, the seat is the wait set, and the counter is the monitor lock.

### What `wait()` does step by step

When a thread calls `wait()` inside a synchronized block, four things happen. It releases the object's monitor completely, even if it had entered the monitor several times. It adds itself to the object's wait set. Its state changes to `WAITING`, and the OS parks it. When someone calls `notify` or `notifyAll`, the thread is moved out of the wait set into the entry queue, where it becomes `BLOCKED` until it can reacquire the monitor. Only then does it return from `wait()`, with the lock restored and its recursion count restored.

The fact that the thread releases the lock atomically with joining the wait set is what prevents lost wakeups. A thread checks its condition under the lock, and the transition into waiting happens with no gap for a signal to slip through.

### The code, explained

```java
class MessageQueue {
 private final Queue<String> queue = new LinkedList<>();
 private final int capacity;

 public MessageQueue(int capacity) { this.capacity = capacity; }

 public synchronized void put(String message) throws InterruptedException {
 while (queue.size() == capacity) {
 wait(); // release lock, sleep until notified
 }
 queue.add(message);
 notifyAll(); // wake everyone; they recheck their own condition
 }

 public synchronized String take() throws InterruptedException {
 while (queue.isEmpty()) {
 wait();
 }
String message = queue.poll();
 notifyAll();
 return message;
 }
}
```

The `while` loop matters for two reasons. First, the JLS permits **spurious wakeups**, where a thread wakes without any notification. Second, even a real notification is only a hint. By the time the woken thread reacquires the lock, another thread may have taken the item. The loop checks the condition again every time.

### Why `wait()` must be inside `synchronized`

The monitor must be owned to manipulate its wait set. The JVM checks this and throws `IllegalMonitorStateException` otherwise. This rule is what makes the check-then-wait sequence safe.

### Internal model

Each object has one monitor (its lock) and one wait set. Threads trying to enter the monitor queue in the entry set. Threads that called `wait()` sit in the wait set. Notification moves threads from the wait set to the entry set. The notified thread still has to win the lock, and the notifier may keep the lock for a while, so a notification is a request to re-check, not an immediate handoff.

### Advantages and disadvantages

The built-in mechanism is simple, requires no extra classes, and works with `synchronized`. It is also the foundation of most Java concurrency code, including older collections and the `Thread.join` implementation.

Its limits are real. Each object has one wait set, so unrelated conditions share it and must use `notifyAll`. You can't cancel a single wait without interrupting the thread. Any code holding a reference to the object can call `notify` on it, which is why private lock objects are the safer choice. And `wait()` releases only one monitor, so nested locks stay held while the thread sleeps.

### Real-world use and modern practice

Java's `ArrayBlockingQueue` avoids this design. It uses a `ReentrantLock` with two `Condition` objects, `notEmpty` and `notFull`, so producers and consumers wait on separate queues and each signal wakes exactly the right kind of thread. The executor framework's queues work the same way. In modern code, you would normally reach for `BlockingQueue`, not hand-written `wait`/`notify` loops.

### Background

Tony Hoare described monitors in 1974. Butler Lampson and David Redell introduced the Mesa-style variant in 1980, where signaled threads must recheck their conditions. Java's `wait` and `notify` follow the Mesa model, and POSIX condition variables use the same semantics. This explains the `while` loop requirement.

## Part 3: `notify()` versus `notifyAll()` (Q13)

### The simple picture

`notify()` is like ringing one bell for one person in a room of people who are all waiting for different things. `notifyAll()` is like announcing to the whole room: "something changed, everyone check whether it's your turn."

### The danger with `notify()`

`notify()` is safe only when every waiting thread is waiting for the same condition, so any one of them can proceed. The danger appears when waiters need different things. Here is a permit pool:

```java
class PermitPool {
 private int available;
 PermitPool(int initial) { available = initial; }

 public synchronized void acquire(int n) throws InterruptedException {
 while (available < n) wait();
 available -= n;
 }

 public synchronized void release(int n) {
 available += n;
 notify(); // risky: may wake the wrong waiter
 }
}
```

Suppose `available` is 0, thread T1 is waiting for 5 permits, and thread T2 is waiting for 1. Someone releases 2 permits and calls `notify()`. If the JVM happens to wake T1, T1 sees only 2 permits, needs 5, and goes back to sleep. T2 could have proceeded with 1 permit, but it is still asleep and nobody will wake it. The signal was spent on the wrong thread and lost.

With `notifyAll()`, both threads wake. T1 goes back to sleep, and T2 proceeds. The cost is extra wakeups.

As for the notes' producer-consumer, the situation is less dangerous than it looks. In a simple bounded buffer, a thread waits only when the buffer is full (producers) or empty (consumers), so both kinds rarely wait at the same time. That's why the notes' version works. Once you add a second condition, such as "wait until at least N items exist," `notify()` becomes unsafe.

### Comparison

| Feature | `notify()` | `notifyAll()` |
|---|---|---|
| Wakes | One arbitrary waiting thread | Every waiting thread |
| Main risk | Wrong thread woken, signal lost, others wait forever | Thundering herd: many threads wake for nothing |
| Speed | Less context switching | More context switching |
| Use when | All waiters have the same condition | Waiters have different or unknown conditions |

### Advantages and disadvantages

`notify()` is efficient when it's correct, but correctness depends on reasoning about every current and future waiter. `notifyAll()` is the safe default, but when a thousand threads wake and only one can proceed, the rest waste CPU and add lock contention. This is the thundering herd problem.

### Real-world examples

A cache with many readers waiting for one loader thread is a classic case. If the loader calls `notifyAll()` when the value arrives, thousands of threads wake at once, all compete for the lock, and latency spikes. Modern caches avoid the problem by sharing one `Future` per key, so waiters block on the result instead of a monitor.

### Modern practice

The fix that scales is separate conditions. `Condition.signal()` wakes one waiter per condition, so each kind of waiter has its own queue and precise wakeups become safe. Use `signalAll()` on a condition when a whole group should wake. For most application code, use `BlockingQueue` or a high-level synchronizer like `Semaphore`, and avoid writing these loops by hand.

## Part 4: `join()` and `sleep()` (Q14)

### The simple picture

`sleep()` is like taking a nap in your office. You stay in the room and keep your keys, so anyone else who needs the room has to wait for you. `join()` is like waiting in a lobby until a colleague finishes a meeting. You don't do anything else while you wait, but the meeting doesn't need you.

### `join()` in detail

When you call `t.join()`, the calling thread waits until thread `t` terminates. A timeout version, `t.join(5000)`, waits at most five seconds. Platform-thread `join` is implemented with `wait` on the `Thread` object. The JVM calls `notifyAll` on that object when the thread exits. This connects directly to Part 2: `join` is just `wait` and `notifyAll` with a well-known monitor.

`join` also creates a memory-visibility guarantee. The Java Language Specification says that all actions in a thread happen-before another thread's successful return from `join()` on that thread. So after `join`, the main thread sees every write the worker made, without any volatile or lock. This makes `join` a clean way to collect results from workers.

### `sleep()` in detail

`Thread.sleep(ms)` pauses the current thread for at least that long. The thread enters `TIMED_WAITING`. It does not release any locks, so sleeping inside a synchronized block blocks everyone else trying to get that lock. The duration is a minimum, not a guarantee, because the OS scheduler decides when the thread runs again. And `sleep` is static, so `t.sleep(100)` still sleeps the current thread, which surprises many developers.

### Comparison

| Feature | `sleep(ms)` | `wait(ms)` | `join()` |
|---|---|---|---|
| Belongs to | `Thread` (static) | `Object` (instance) | `Thread` (instance) |
| Releases locks | No | Yes, the monitor of that object | Not applicable |
| Wakes when | Timeout passes or interrupted | Notified, timeout, or interrupted | Target thread terminates, timeout, or interrupted |
| Must hold a monitor | No | Yes | No |
| State | `TIMED_WAITING` | `WAITING` or `TIMED_WAITING` | `WAITING` or `TIMED_WAITING` |

### Advantages and disadvantages

`join` is simple and gives a memory-visibility guarantee for free. Its limits are that it waits for one thread at a time, cannot return a result, and is clumsy for many workers. `sleep` is simple and useful for backoff, but it is a poor way to wait for a condition. Polling with sleep wastes time, adds latency, and makes tests flaky.

### Real-world examples

A batch job might start four worker threads, call `join()` on each, and then combine their results. The visibility guarantee means the combined result is correct without extra synchronization.

Retry with exponential backoff is a good use of `sleep`: wait 100 ms, then 200 ms, then 400 ms, with some jitter. Waiting for a database to become ready is a poor use of `sleep`, where a `CountDownLatch`, a timeout on a future, or a proper health check is better.

A common anti-pattern is `Thread.sleep` inside a synchronized method. It holds the lock for the whole sleep, so all other callers stall. Another is using `sleep` to "wait for the other thread to finish" in tests. That passes on a fast laptop and fails in CI.

### Modern practice

`ExecutorService` has been `AutoCloseable` since Java 19, and its `close()` waits for submitted tasks to finish. `CompletableFuture.allOf` and `StructuredTaskScope` replace manual `join` loops. On Java 21 and later, virtual threads make `sleep` nearly free, because a sleeping virtual thread unmounts from its carrier, so per-task sleeping is now a reasonable design.

### Background

`Thread.sleep` and `Thread.join` have been in Java since version 1.0. Early Java had `Thread.stop()`, which was deprecated because it could leave objects half-updated. Interruption replaced it as the cooperative cancellation mechanism, which is why both `sleep` and `join` throw `InterruptedException`.

## Part 5: `ThreadLocal` (Q15)

### The simple picture

Think of a coat check in a busy restaurant. Each guest gets a numbered ticket, and the attendant keeps each person's coat in a locker keyed by that ticket. Guests never see each other's coats, and nobody needs to pass the coat through the kitchen. `ThreadLocal` works the same way: each thread gets its own locker, and the key is the `ThreadLocal` object itself.

### How it works internally

Every `Thread` object has a field holding a `ThreadLocal.ThreadLocalMap`. This is a custom hash table, not `java.util.HashMap`. Its keys are the `ThreadLocal` objects, held through weak references. Its values are held through strong references.

When you call `get()`, the map looks up the current thread's entry for this `ThreadLocal`. If there is none, it creates one using `initialValue()` or the supplier from `withInitial`. Stale entries, where the key has been garbage collected, are removed opportunistically during `get`, `set`, and `remove`.

This design explains the leak. The weak key lets the `ThreadLocal` object be collected, but the value remains strongly referenced in the thread's map until the map cleans that entry. In a thread pool, the thread lives on, so a value can stay in memory for the thread's whole life, along with whatever it references. This is also why a stale value can survive into the next request.

### Real-world uses

`ThreadLocal` shows up in many frameworks. Spring's `SecurityContextHolder` stores the authenticated user per thread by default. SLF4J's MDC, backed by Logback, stores a request ID so that every log line in a request can be correlated. Spring's transaction machinery binds a connection to the current thread. `ThreadLocalRandom` is a JDK example: it gives each thread its own random generator, avoiding the contention a shared `Random` causes on its seed.

Date formatting is the example from the notes. `SimpleDateFormat` is not thread-safe, so a `ThreadLocal` gives each thread its own instance. Today, the better fix is `java.time.format.DateTimeFormatter`, which is immutable and can be shared directly.

The request context example from the notes is the pattern most web apps use:

```java
public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
 try {
 UserContext.set(authenticateUser(req));
 chain.doFilter(req, res);
 } finally {
 UserContext.clear(); // without this, the next request may see this user
 }
}
```

The transaction example in the notes needs a fix. Here is a version that always cleans up and closes its resources:

```java
static void runInTransaction(Runnable work) throws SQLException {
 try (Connection conn = dataSource.getConnection()) {
 HOLDER.set(conn);
 try {
 work.run();
 } finally {
 HOLDER.remove();
 }
 }
}
```

### Advantages and disadvantages

`ThreadLocal` avoids passing parameters through many layers of methods, and it avoids locks, since each thread touches only its own copy. Reads are fast.

It has real drawbacks. It creates hidden dependencies: a method that reads the current user does not declare that it needs one. In pooled threads, forgotten values can leak between requests, which is a security bug, not just a memory issue. A common case is user A's data appearing in user B's request. `ThreadLocal` values also do not follow work to other threads. A `CompletableFuture` or an executor task runs on a different thread, so the context silently disappears. Finally, with millions of virtual threads, a per-thread copy of a large value can use a lot of memory.

### Fixes for these drawbacks

`InheritableThreadLocal` copies values into child threads at creation. It doesn't help with pools, because pooled threads are created once and reused. Context propagation libraries, such as Micrometer's Context Propagation, copy context across `CompletableFuture` and reactive pipelines. Passing an explicit context object is the simplest and most testable option.

### Modern practice

Java 21 introduced `ScopedValue` as a preview API designed for virtual threads. It binds a value to a scope, such as a request, and makes it visible to code run inside that scope and to tasks it starts, then removes it automatically. It has been in preview for several releases, so check whether your JDK has finalized it. For new code on virtual threads, it is worth evaluating before a `ThreadLocal`.

### Background

`ThreadLocal` has been part of Java since version 1.2 in 1998. Its design predates thread pools, reactive programming, and virtual threads, and each of these changes has made its assumptions less safe. The rule of thumb that survives is: if you use `ThreadLocal` in code that runs on pooled threads, always call `remove()` in a `finally` block.

## Putting It Together and Current Industry Practice

Modern Java systems use these tools in a layered way. Shared state is kept small and immutable, so `volatile` is needed mainly for flags and swapped references. Waiting is delegated to `BlockingQueue`, `Condition`, `CountDownLatch`, `Semaphore`, and `CompletableFuture`, rather than hand-written `wait` and `notify` loops. Cancellation uses `Thread.interrupt()` and executor shutdown, not flags. Per-thread context uses `ThreadLocal` only where there's no better option, with `remove()` in every `finally`, and new code on virtual threads is moving toward explicit parameters or scoped values.

Tooling supports these practices. Thread dumps show `WAITING` and `TIMED_WAITING` threads and which monitors they hold. Java Flight Recorder records monitor contention and wait events. Static analysis tools such as Error Prone can check `@GuardedBy` annotations, and concurrency stress tests such as JCStress can reveal visibility bugs that ordinary tests never trigger.

## Quick Summary

| Concept | Simple meaning | Key takeaway |
|---|---|---|
| `volatile` | Always check the shared whiteboard | Gives visibility and ordering, not atomicity; works for flags and swapped references |
| `wait()` | Sit down and wait for a bell | Releases the monitor, must be called in a `while` loop, must hold the lock |
| `notify()` vs `notifyAll()` | One bell vs an announcement to everyone | `notify` is only safe when all waiters have the same condition; prefer `notifyAll` or separate conditions |
| `join()` | Wait in the lobby for a colleague | Waits for a thread to finish, and guarantees its writes are visible afterward |
| `sleep()` | A nap that keeps your keys | Pauses without releasing locks; useful for backoff, not for waiting on conditions |
| `ThreadLocal` | A personal locker per thread | Remove values in `finally`, especially in pools, or data leaks between requests |

# Thread Pools, ExecutorService, and Modern Locking, Explained Simply

Parts 1 through 3 covered threads, synchronization, and communication between threads. Production systems rarely create threads one at a time. They keep a set of worker threads alive and hand them tasks. That machinery is the thread pool, and Java packages it as `ExecutorService`. The last part of this section covers the locks that pools and their tasks depend on.

Several parts of the notes need correcting first:

1.**The cost figures for creating threads are overstated.** A thread's stack is reserved address space, and only the pages it actually uses become physical memory. Creating a thread usually takes tens of microseconds on Linux. The real problems with 10,000 threads are scheduler overhead, cache disruption, and OS limits, which cause the `unable to create native thread` error.
2.**A pool limits threads, not work.** `newFixedThreadPool` uses an unbounded queue, so 10,000 queued tasks sit in memory with no limit. The pool protects your threads, but only a bounded queue protects your memory.
3.**`scheduleAtFixedRate` silently stops if one run throws an exception.** The notes don't mention this, and it is one of the most common scheduling bugs.
4.**A pool that is never shut down keeps the JVM alive**, because pool threads are non-daemon by default. The program can finish its work and still never exit.
5.**The `transferWithTimeout` example does not compile.** `TimeoutException` is a checked exception that the method doesn't declare. Section 5 has a corrected version.

## Part 1: Why Thread Pools Exist (Q16)

### The simple picture

Imagine a call center with 20 agents and 10,000 calls a day. You could hire a new agent for every call and send them home afterward, or you could keep 20 agents and let callers wait in a line. The second design is obviously better. Hiring and dismissing takes time, and the office has no room for 10,000 people at once.

A thread pool is that call center. The agents are threads, the calls are tasks, and the waiting line is the queue.

### The cost of a thread per request

Creating a thread means the JVM allocates a `Thread` object, the OS creates a kernel task, and a stack is reserved. When the work finishes, the thread is torn down. This happens thousands of times per second in a busy server, so the overhead adds up.

Creation time is the smallest problem. Three larger ones matter more. The first is memory. Each Java thread reserves about 1 MB of stack by default, and the thread also has native structures and GC roots. Thousands of threads add up. The second is CPU scheduling. With 10,000 runnable threads on 8 cores, the scheduler spends much of its time switching, and caches keep getting cold, so throughput can fall as the thread count rises. The third is OS limits. Operating systems and containers cap the number of processes and threads, such as `ulimit -u` or a Kubernetes pids limit. When you hit that cap, the JVM throws `java.lang.OutOfMemoryError: unable to create native thread`. That crash appears only under load, which makes it hard to reproduce.

### How a pool solves these problems

A pool reuses threads, so creation cost is paid only occasionally. It bounds concurrency, which puts a ceiling on resource use. It manages the work by owning the queue, deciding what happens when the queue is full, and exposing metrics. It is tunable, so you can match its size to your hardware and workload.

### The honest caveat

The bound applies to threads. The queue in `newFixedThreadPool` is unbounded, so if requests arrive faster than the pool finishes them, the queue grows until the heap runs out. The fix is a bounded queue plus a rejection policy, covered in Section 3.

### Advantages and disadvantages

Pooling lowers overhead, keeps resource use predictable, and gives you one place to handle errors, timeouts, and metrics. Its disadvantages are real. Poor sizing hurts in both directions: too few threads and requests wait, too many and you're back to thread thrashing. Queued work hides latency, since a task may spend a long time waiting before it runs. Thread-local state can leak between tasks, as covered in the earlier answer on `ThreadLocal`. And debugging is harder, because the thread that runs a task is not the thread that submitted it.

### Real-world examples

Tomcat's connector uses a worker pool (200 threads by default) and an accept queue for incoming connections (`acceptCount`, 100 by default). HikariCP, the database connection pool commonly used with Spring Boot, applies the same idea to connections, with a default maximum of 10. `CompletableFuture.supplyAsync` and parallel streams share `ForkJoinPool.commonPool()`, a pool sized roughly to the CPU core count. If you block on I/O inside those tasks, you can starve everything else that uses the pool.

### Background

Pooling is old. Database connections were pooled long before Java, because opening them is expensive. Java 1.0 in 1996 had raw `Thread` and `synchronized`, but no pool framework. Java 5 (2004) added `Executor`, `ExecutorService`, and `ThreadPoolExecutor` through `java.util.concurrent`, designed by Doug Lea.

## Part 2: The Types of ExecutorService (Q17)

### The simple picture

Each factory method is a different staffing policy. One is a fixed team that works through a line. Another hires temporary workers when it's busy and sends them home when it's quiet. Another is a single clerk who handles requests in order. Another runs jobs at scheduled times. Another is a group of workers who take work from each other's piles when they run out. And in modern Java, there's a new worker for every task, because workers are nearly free.

Most of the classic factory methods are just configurations of `ThreadPoolExecutor`. The work-stealing pool is built on `ForkJoinPool` instead. Knowing the underlying settings matters more than memorizing the factory names, because the settings determine how your service fails.

### 1. FixedThreadPool

Threads are created as tasks arrive, up to a fixed count, and then reused. Extra tasks wait in an unbounded `LinkedBlockingQueue`. This is a good default when the workload is steady and you know roughly how much concurrency the downstream system can handle. Its danger is the unbounded queue. A producer that is faster than its consumers fills memory silently. A video transcoding service with eight workers and no queue limit might accept a million uploads and then run out of memory, when it should have told clients to retry.

### 2. CachedThreadPool

This pool has no core threads and no real queue. It uses a `SynchronousQueue`, which is a handoff point with no capacity. A task is passed directly to an idle thread, or a new thread is created. Idle threads exit after 60 seconds. It works well for many short tasks. The danger is that nothing caps the thread count. A burst of 10,000 slow calls creates 10,000 threads, which is the thread-per-request problem the pool was meant to prevent.

### 3. SingleThreadExecutor

This is one thread with an unbounded queue. Tasks run one at a time, in submission order. Use it when ordering matters and tasks must not overlap, such as writing to a single log file or applying events to a state machine. The factory returns a wrapper that can't be cast back to `ThreadPoolExecutor` or reconfigured, which is intentional. If a task submitted with `execute()` throws an uncaught exception, the worker dies and the pool creates a replacement. With `submit()`, the exception is stored in the returned `Future`, and the thread survives.

### 4. ScheduledThreadPool

This pool uses a delay queue ordered by next run time. It offers three operations, and the differences matter. `schedule(task, delay)` runs a task once after a delay. `scheduleAtFixedRate` runs at a fixed period, measured from start to start. If one run is late, later runs catch up but never overlap. `scheduleWithFixedDelay` waits a set time after each run finishes, so the gap between runs stays constant even when runs are slow.

The big gotcha is that if any run of a periodic task throws an exception, all later runs are silently suppressed. Wrap the body of periodic tasks in a try/catch and log the error. For persistent, clustered, or calendar-based schedules, use a dedicated scheduler such as Quartz or a platform scheduler instead of an in-memory pool.

### 5. WorkStealingPool

Created by `Executors.newWorkStealingPool()` (Java 8), this is a `ForkJoinPool` with one worker per available core. Each worker has its own double-ended queue. A worker takes tasks from its own queue, and an idle worker steals from the other end of a busy worker's queue. This keeps all cores busy without a single central queue becoming a bottleneck. It suits CPU-heavy, recursive, divide-and-conquer work. Its weakness is blocking. A worker stuck on I/O can't run other tasks, so throughput drops if the tasks block.

### 6. Virtual thread per task (Java 21)

`Executors.newVirtualThreadPerTaskExecutor()` creates a new virtual thread for each task, with no pooling. Virtual threads are cheap enough that this is fine, and blocking on I/O unmounts them from their carrier threads. The trade-off is that there is no concurrency limit at all. If every task calls the same database, you can overwhelm it. The fix is a `Semaphore` that caps concurrent calls to the downstream system.

### Comparison

| Type | Threads | Queue | Best for | Main danger |
|---|---|---|---|---|
| FixedThreadPool | Fixed (n) | Unbounded | Steady workloads | Queue grows until out of memory |
| CachedThreadPool | 0 to unlimited | None (handoff) | Bursts of short tasks | Unlimited threads |
| SingleThreadExecutor | 1 | Unbounded | Ordered, serial work | One slow task blocks everything |
| ScheduledThreadPool | Fixed (n) | Delay queue | Timed and periodic jobs | Periodic tasks stop silently on exception |
| WorkStealingPool | About one per core | Per-worker deques | CPU-bound recursive work | Blocking tasks idle workers |
| Virtual thread per task | One per task | None | High-concurrency I/O | No built-in limit |

### Advantages and disadvantages

The factory methods are short and readable, which is their main advantage. Their hidden defaults are the disadvantage. The unbounded queues and unlimited threads don't appear at the call site, so production incidents often trace back to them. For serious services, configure `ThreadPoolExecutor` directly and give its threads names.

### Real-world examples

A payment client might use a fixed pool of 20 threads for gateway calls, with a bounded queue. A log shipper uses a single thread so entries stay in order. A nightly report generator uses a scheduled pool. An image service runs CPU-heavy resizing on a work-stealing pool. A service that fans out to many internal APIs uses virtual threads per task, with a semaphore limiting how many calls go to each downstream system.

## Part 3: How ThreadPoolExecutor Works Internally (Q18)

### The simple picture

Go back to the call center. The company keeps five permanent agents. When all five are busy, calls join a waiting line of 100. When the line is full, the company brings in temporary agents, up to 15 more. When the temps are busy and the line is full too, callers hear "all lines are busy, please try again later." Every rule in that design has a reason, and so does every rule in `ThreadPoolExecutor`.

### The four stages

Each submitted task passes through the same decision path:

1. If fewer than `corePoolSize` threads are running, create a new core thread and run the task there, even if other threads are idle.
2. Otherwise, offer the task to the work queue. If the queue accepts it, the task waits there.
3. If the queue is full, create a non-core thread, as long as the total stays below `maximumPoolSize`.
4. If none of those is possible, call the rejection handler.

Using the notes' numbers, with none of the tasks finishing while they are submitted: tasks 1 to 5 each get a core thread, tasks 6 to 105 go into the queue, and task 106 forces a non-core thread. Threads grow until the pool reaches its maximum of 20, at task 120. Task 121 is rejected. The queue fills before extra threads appear, so the queue choice shapes the whole behavior.

### The unbounded queue trap

If you use a `LinkedBlockingQueue` with no capacity, step 3 never happens. The queue always accepts the task, so the pool never grows past its core size, and `maximumPoolSize` has no effect. Use a bounded queue for predictable behavior. This is also why `FixedThreadPool`, where core and maximum sizes are equal, doesn't have this surprise.

### Idle threads and naming

Non-core threads that stay idle longer than `keepAliveTime` exit. Core threads normally stay alive forever, unless you call `allowCoreThreadTimeOut(true)`. A custom `ThreadFactory` lets you name threads, such as `payment-worker-%d`, which makes thread dumps readable. Without one, threads are named `pool-1-thread-1` and similar.

### Rejection policies

When both the pool and the queue are full, the rejection handler decides what happens.

`AbortPolicy`, the default, throws `RejectedExecutionException` to the submitter. Use it when the caller must know, such as a web handler that should return HTTP 503.

`CallerRunsPolicy` makes the submitting thread run the task itself. This slows the producer down, which is natural back-pressure. Be careful if the submitter is a web request thread, because that thread is then busy too.

`DiscardPolicy` silently drops the new task. Use it only when losing tasks is acceptable, and at least count the drops.

`DiscardOldestPolicy` removes the oldest queued task and tries again. It suits "latest value wins" workloads, such as live prices or sensor readings, where a stale item is worth little.

A custom `RejectedExecutionHandler` can write the task to a dead-letter queue, record a metric, or retry later. Many production systems do this.

### Advantages and disadvantages

The design gives fine-grained control. You can tune core size, maximum size, queue type and capacity, timeouts, thread names, and rejection behavior independently. It also exposes monitoring hooks: `getPoolSize()`, `getActiveCount()`, `getQueue().size()`, and `getCompletedTaskCount()`. The cost is complexity. Four settings that interact are easy to misconfigure, and the factory methods hide the choices.

### Real-world examples

Tomcat's connector follows the same pattern: a worker pool, plus an accept queue for connections that arrive when every worker is busy. A payment gateway client might use 50 threads, a queue of 200, and `AbortPolicy`, returning 503 to clients when saturated. A metrics pipeline might use `CallerRunsPolicy`, so that when it falls behind, the producers slow down on their own. A live-video frame processor uses `DiscardOldestPolicy`, because a frame that is two seconds old is worth less than the newest one.

### Sizing the pool

Two rules help. For CPU-bound work, size the pool close to the number of cores. For I/O-bound work, use Little's law: required concurrency equals throughput multiplied by latency. A service handling 500 requests per second, with each call taking 200 ms, needs about 100 concurrent workers. Measure before and after any change, because real latency rarely matches the estimate.
### Background

Doug Lea designed `ThreadPoolExecutor` for Java 5. Its policies and queue options reflect lessons from years of server software. Its design separates three ideas that are often mixed up: how many threads you have (pool size), how much work you can hold while waiting (queue), and what happens when you're full (rejection).

## Part 4: Shutting Down an ExecutorService (Q19)

### The simple picture

Think of closing a restaurant at night. First, stop taking new reservations (`shutdown`). Let current guests finish their meals. Wait until the room is empty, but not forever (`awaitTermination` with a timeout). If guests refuse to leave after the deadline, ask them to go (`shutdownNow`, which interrupts the tasks). If they still won't go, log that the room isn't empty and move on.

### The pattern, explained

The notes' code is the standard idiom from the `ExecutorService` documentation. It has four moves. `shutdown()` stops new submissions. `awaitTermination` waits up to a time limit. `shutdownNow()` interrupts remaining work after the grace period. A second `awaitTermination` gives interrupted tasks a short window to respond. The `InterruptedException` handler restores the thread's interrupt flag, so that calling code can see it. Swallowing an interrupt is a common bug, and this idiom avoids it.

### Corrections and missing pieces

`shutdownNow()` is best effort. It interrupts threads and drains the queue, but a task that ignores interrupts keeps running. For example, a task blocked in a socket read that doesn't respond to interrupts will hold its thread. Termination depends on tasks that cooperate.

Pool threads are non-daemon by default. If you never call `shutdown`, the JVM doesn't exit when `main` finishes, so the program appears to hang. Since Java 19, `ExecutorService` implements `AutoCloseable`, and `close()` calls `shutdown()` and then waits for tasks to finish. Try-with-resources makes this automatic:

```java
try (ExecutorService executor = Executors.newFixedThreadPool(10)) {
 executor.submit(task1);
 executor.submit(task2);
} // close() waits here until both tasks finish
```

The catch is that `close()` waits without a deadline. If a task might hang, use an explicit `shutdown` and `awaitTermination` with a timeout instead.

### Method behavior

| Method | Behavior |
|---|---|
| `shutdown()` | Rejects new tasks; running and queued tasks finish normally |
| `shutdownNow()` | Interrupts running tasks, removes queued tasks, returns the removed tasks as a list |
| `awaitTermination(t, unit)` | Blocks until all tasks finish or the timeout passes |
| `isShutdown()` | True once shutdown has been requested |
| `isTerminated()` | True when all tasks have finished after shutdown |

### How it works internally

`ThreadPoolExecutor` moves through five run states: RUNNING, SHUTDOWN, STOP, TIDYING, and TERMINATED. `shutdown()` moves it to SHUTDOWN, so new submissions are rejected, and workers exit once the queue is empty. Idle workers are interrupted so they wake up and notice. `shutdownNow()` moves it to STOP, drains the queue into the returned list, and interrupts every worker. When the last worker exits, the pool moves through TIDYING to TERMINATED and signals anyone waiting in `awaitTermination`. That signal uses a condition wait, the same mechanism covered in the earlier answer on `wait` and `notify`.

### Advantages and disadvantages

Graceful shutdown finishes in-flight work, so no half-done jobs are lost. Its cost is time: a long task delays shutdown, and a stuck task may never finish. `shutdownNow` is fast but abandons work. The returned list must be saved or re-queued, or that work is lost.

### Real-world examples

Kubernetes sends `SIGTERM` and waits 30 seconds by default (`terminationGracePeriodSeconds`) before sending `SIGKILL`, so a service must finish shutting down within that window. Spring Boot's graceful shutdown (`server.shutdown=graceful`) stops accepting web requests and waits for active ones, with a per-phase timeout that defaults to 30 seconds. A message consumer should stop pulling new messages, finish the ones it holds, and acknowledge them, so the broker redelivers only unfinished work. All of these follow the same pattern: stop intake, wait with a deadline, then force.

### Background

Before Java 19, shutdown was entirely manual, and many programs got it wrong. The `ExecutorService` documentation gave the four-step idiom, and most code copied it. Making `ExecutorService` `AutoCloseable` made the correct behavior the easy one.

## Part 5: `synchronized` versus `ReentrantLock` (Q20)

### The simple picture

`synchronized` is an automatic door. It locks when you walk through and unlocks when you walk out, even if you trip on the way. You can't forget to lock it, but you also can't give up after a set time, and you can't control the order in which people queue.

`ReentrantLock` is a key you carry. You decide when to lock and unlock. You can try the door without waiting, set a time limit, or ask to be interrupted while waiting. The cost is responsibility. If you walk away without unlocking, the door stays locked forever.

### The comparison

The notes' table is accurate. Two points to add: both kinds of lock are reentrant, so the same thread can acquire the lock again without deadlocking itself, and read-write locking comes from a separate class, `ReentrantReadWriteLock`, not from `ReentrantLock` itself.

| Feature | `synchronized` | `ReentrantLock` |
|---|---|---|
| Acquire | Automatic at block entry | `lock()` or `tryLock()` |
| Release | Automatic at block exit | `unlock()` in `finally`, always |
| Non-blocking attempt | No | `tryLock()` |
| Timed attempt | No | `tryLock(timeout, unit)` |
| Interruptible wait | No | `lockInterruptibly()` |
| Fairness | Unfair only | `new ReentrantLock(true)` |
| Conditions | One wait set per object | Any number via `newCondition()` |
| Status queries | None | `isLocked()`, `getQueueLength()`, `isHeldByCurrentThread()` |

### Corrected code

The notes' basic `lock` and `tryLock` patterns are right. The timed version has a compile error, and it throws on timeout where returning `false` gives callers a clearer contract. Here is a corrected version:

```java
public boolean transferWithTimeout(Account from, Account to, int amount)
 throws InterruptedException {
 if (!lock.tryLock(2, TimeUnit.SECONDS)) {
 return false; // caller decides: retry, queue, or report
 }
 try {
 from.debit(amount);
 to.credit(amount);
 return true;
 } finally {
 lock.unlock();
 }
}
```

The notes use one `lock` for every transfer in the system. That is simple, but it serializes all transfers. A scalable design uses per-account locks taken in a consistent order, as covered in the earlier answer on deadlock.

### How ReentrantLock works internally

`ReentrantLock` is built on `AbstractQueuedSynchronizer` (AQS), a framework Doug Lea wrote for Java 5. AQS keeps an integer `state` that is updated with compare-and-swap. A thread that acquires the lock changes `state` from 0 to 1. If the same thread acquires again, it increments `state`, which is how reentrancy works. Threads that can't get the lock join a FIFO queue of nodes and park with `LockSupport.park`. When the owner releases the lock, the next node in the queue is unparked. In fair mode, `tryAcquire` first checks whether anyone is already queued, so newcomers can't jump ahead. Each `Condition` has its own wait queue, which is why one lock can support several conditions.

By contrast, `synchronized` relies on the object header and the JVM's monitor, as described in the earlier answer on synchronization.

### Advantages and disadvantages

`ReentrantLock` offers what `synchronized` cannot: timeouts for deadlock avoidance, interruptible waits for cancellation, fairness against starvation, multiple conditions, and status queries for monitoring. It also doesn't pin virtual threads on Java 21 through 23, as `synchronized` does.

Its disadvantages come from the manual control. Forgetting `unlock()` in a `finally` block leaves the lock held forever, which can freeze the whole system. The code is longer, and each lock allocates more objects than `synchronized`. You also must use `Condition` instead of `wait` and `notify`, which is easy to forget.

Performance is roughly comparable on modern JVMs, and the difference depends on the workload. Choose based on the features you need, and benchmark only when you have evidence it matters.

### Real-world examples

`ArrayBlockingQueue` uses a `ReentrantLock` with two conditions, `notEmpty` and `notFull`. Since Java 8, `ConcurrentHashMap` uses `synchronized` on individual bin heads and CAS for empty bins, which keeps locks small and avoids a lock object per entry. A web service's rate limiter might use `tryLock` to fail fast with HTTP 429 rather than making callers wait. A cache with rare writes and frequent reads might use `ReentrantReadWriteLock`, or `StampedLock` when it can use optimistic reads.

### Background

`synchronized` has been in Java since version 1.0. `ReentrantLock`, AQS, and the rest of the locking utilities came with Java 5 through `java.util.concurrent`. They were added because the built-in lock couldn't express timeouts, interruption, fairness, or multiple conditions, and large systems needed all four.

### Current practice

For short, simple critical sections, `synchronized` is still the best default, because the JVM handles release and it is hard to misuse. On Java 21 through 23, prefer `ReentrantLock` in code where virtual threads might block inside a lock, because `synchronized` pins them to their carrier threads. On JDK 24 and later, that pinning is fixed, so the choice is mostly about features. Whichever you use, pair `lock()` with a `finally` block, and prefer a `Condition` or a higher-level class such as `BlockingQueue` over hand-written waiting.

## Putting It Together

Thread pools are the standard way to run concurrent work in Java. Choose the executor by workload. Use a bounded `ThreadPoolExecutor` for most services, a single thread for ordered work, a scheduled pool for timed jobs, a work-stealing pool for CPU-heavy tasks, and virtual threads with a semaphore for high-concurrency I/O. Always bound the queue, choose a rejection policy deliberately, name the threads, monitor pool size and queue depth, and shut down with a deadline, ideally through try-with-resources. For locking inside tasks, use `synchronized` for simple cases and `ReentrantLock` when you need timeouts, interruption, fairness, or multiple conditions.

## Quick Summary

| Concept | Simple meaning | Key takeaway |
|---|---|---|
| Thread pool | A call center with reusable agents | Reuse threads and bound concurrency |
| ExecutorService types | Different staffing policies | Know each one's hidden queue and thread limits |
| ThreadPoolExecutor | Core agents, overflow agents, a waiting line, then rejection | Use a bounded queue, or the maximum size does nothing |
| Rejection policies | What to do when everything is full | Choose Abort, CallerRuns, Discard, or DiscardOldest on purpose |
| Shutdown | Closing the restaurant with a deadline | `shutdown`, `awaitTermination`, then `shutdownNow`, or use try-with-resources with a timeout |
| `ReentrantLock` vs `synchronized` | Automatic door vs a key you carry | Use `ReentrantLock` for timeouts, interruption, fairness, and conditions, and always unlock in `finally` |
# ReadWriteLock, Synchronizers, Atomic Classes, and LongAdder, Explained Simply

The previous parts covered locks and pools. This part covers tools for specific coordination patterns: data that is read far more often than written, waiting for a group of threads, limiting how many threads can use a resource, phased parallel work, and counters that avoid locks entirely. The last two sections rely on CAS (compare-and-swap), the hardware instruction that makes lock-free code possible.

Some parts of the notes need correcting first:

1.**The "Read:Write = 95:5 → Excellent" claim overstates the benefit.** A read-write lock helps when each read does enough work to outweigh its bookkeeping. Every read-lock acquisition still updates a shared counter, so many cores reading through the same lock fight over one cache line. For very short reads, a plain `ReentrantLock` or a `ConcurrentHashMap` can be faster.
2.**A thread holding a read lock cannot upgrade to the write lock.** The notes don't mention this, and the attempt deadlocks the thread against itself.
3.**The `CountDownLatch` and `Phaser` examples hang forever** if a participant throws an exception before counting down or deregistering. The signal belongs in a `finally` block.
4.**The `CyclicBarrier` lambda does not compile.** `barrier.await()` throws checked exceptions, and a lambda with no return value matches `Runnable`, which cannot throw them.
5.**`incrementAndGet()` is not really a retry loop on x86.** The JIT compiles it to a single locked add instruction. The loop in the notes is a conceptual model of CAS.
6.**`LongAdder.sum()` is not an atomic snapshot.** It adds the base value and all the cells, and if updates happen during the sum, the result is an estimate.
7.**`AtomicStampedReference` compares object references with `==`, not `equals`.** The notes' string example works only because of how string literals are interned.

## Part 1: ReadWriteLock (Q21)

### The simple picture

Think of a library reading room. Any number of people can read the same book at once, because reading doesn't change it. But when a librarian needs to correct a page, everyone must leave the room first, and the librarian works alone. The rule is many readers or one writer, never both.

A `ReadWriteLock` encodes exactly that rule. The read lock is shared, so many threads can hold it together. The write lock is exclusive, so only one thread can hold it, and while it does, no reader can enter.

### Walking through the code

The notes' cache example is correct. The `HashMap` is safe here because writes are exclusive: while a writer holds the write lock, no reader can be inside `get`, and no other writer can run. Each method uses the `lock` / `try` / `finally` / `unlock` pattern from earlier parts, so the lock is released even if the operation throws.

A subtle point: readers can run together only while no writer is active or waiting in a way that blocks them. If a writer is waiting, a fair lock stops new readers from entering ahead of it. A non-fair lock may let readers slip in, which is faster but can delay writers.

### Internal working

`ReentrantReadWriteLock` is built on the same `AbstractQueuedSynchronizer` framework as `ReentrantLock`. The difference is that its single integer `state` is split into two halves of 16 bits. The upper half counts read holds, and the lower half counts the exclusive write hold. A reader adds one to the upper half with a compare-and-swap. A writer succeeds only if the whole state is zero, and then sets the lower half to one.

Each thread's own read holds are tracked in a `ThreadLocal` counter, which allows the same thread to take the read lock more than once.

The lock supports downgrading: a thread holding the write lock can acquire the read lock and then release the write lock, so it keeps a consistent view. It does not support upgrading. This code deadlocks:

```java
readLock.lock();
try {
 if (!cache.containsKey(key)) {
 writeLock.lock(); // DEADLOCK: this thread still holds the read lock
 }
} finally {
 readLock.unlock();
}
```

The write lock waits for all readers to leave, including the thread that is waiting. The correct pattern is to release the read lock, take the write lock, and check again:

```java
readLock.lock();
String value;
try { value = cache.get(key); } finally { readLock.unlock(); }
if (value == null) {
 writeLock.lock();
 try {
 value = cache.computeIfAbsent(key, this::load); // recheck inside the write lock
 } finally {
 writeLock.unlock();
 }
}
```

### StampedLock

`StampedLock`, added in Java 8, goes further. Instead of acquiring a read lock, a reader can take an **optimistic read**: it records a stamp, reads the fields, and then checks whether a write happened in the meantime. If nothing changed, the reader never wrote to shared state and never blocked anyone. If something changed, it falls back to a real read lock.

The notes' version is correct, with one caution: an optimistic read can observe fields in an inconsistent mix while a writer is active. That is safe only because the code validates the stamp and discards those values. Never act on optimistic values before validation.

Its main advantage is that optimistic reads do not write to shared state at all. That is why it beats `ReentrantReadWriteLock` on hot read paths, where the read-lock counter itself becomes the bottleneck. Its limits are that it is not reentrant, and it does not support `Condition`.

### Comparison

| Feature | `synchronized` / `ReentrantLock` | `ReentrantReadWriteLock` | `StampedLock` |
|---|---|---|---|
| Concurrent readers | No | Yes | Yes, and optimistic reads don't write shared state |
| Reentrant | Yes | Yes | No |
| Condition support | Yes | Yes (per lock) | No |
| Read-to-write upgrade | Not applicable | Deadlocks | Must release and retry |
| Overhead on short reads | Low | Higher than a plain lock | Lowest on hot reads |
| Complexity | Low | Moderate | High |

### Advantages and disadvantages

A read-write lock lets reads scale when they're frequent and meaningful. It also expresses intent clearly: a reader of this code knows the method doesn't modify state. The costs are the bookkeeping on every read, the risk of writer starvation under a steady stream of readers, the upgrade deadlock trap, and the extra complexity. For many workloads, a `ConcurrentHashMap` or an immutable snapshot is simpler and faster.

### Real-world examples

A product catalog read thousands of times per second and updated a few times a day is a classic fit. So is a routing table in a network service, where lookups are constant and changes are rare, or a sorted index inside an in-memory search service. In each case, the structure has several fields or a non-trivial shape that a single concurrent map doesn't handle well. For a simple key-value cache, `ConcurrentHashMap` is usually the better choice.

### Background

The problem is classic. Courtois, Heymans, and Parnas formalized the readers-writers problem in 1971. Java 5 brought `ReentrantReadWriteLock` through `java.util.concurrent`, and Java 8 added `StampedLock` for the case where even the read-lock bookkeeping is too expensive.

### Current practice

Start with `ConcurrentHashMap`, `CopyOnWriteArrayList` for read-mostly lists, or an immutable snapshot held in a `volatile` reference, as covered in the earlier discussion of `volatile`. Reach for a read-write lock when several fields must stay consistent together. Use `StampedLock` only on a measured hot path, and always design read-to-write transitions as release-then-acquire.

## Part 2: CountDownLatch, CyclicBarrier, and Semaphore (Q22)

### The simple pictures

A **CountDownLatch** is a rocket launch countdown. The count goes down once, and when it reaches zero, the launch proceeds. It happens only once.

A **CyclicBarrier** is a group of hikers with checkpoints along the trail. Everyone waits at each checkpoint until the whole group arrives, then they all continue. The same checkpoint can be reached again in the next leg, so the barrier resets itself.

A **Semaphore** is a parking lot with ten spaces, or a box of ten restaurant tokens. Anyone who wants to enter takes a token and returns it when they leave. When the box is empty, arrivals wait.

### CountDownLatch

The notes' startup example captures the idea: the main thread waits until three services report ready. Internally, a latch is an AQS shared-mode lock whose state is the count. `countDown()` decrements the count with a CAS, and when the count reaches zero, it wakes every thread waiting in `await()`. The count cannot be reset, which is why the latch is one-time.

The notes' version has a hang risk. If `initCache()` throws before `countDown()`, the main thread waits until its timeout, and the failure is hidden. Count down in a `finally` block, and record the failure separately:

```java
executor.submit(() -> {
 try {
 initCache();
 } catch (RuntimeException e) {
 failed.set(true); // remember the failure
 log.error("cache init failed", e);
 } finally {
 latch.countDown(); // always signal, even on failure
 }
});
// main thread:
if (!latch.await(30, TimeUnit.SECONDS) || failed.get()) {
 throw new StartupException("startup failed");
}
```

Real-world uses include integration tests that wait for containers to start, and load tests where a latch with count one acts as a starting gun, so every virtual user begins at the same instant.

### CyclicBarrier

Internally, a `CyclicBarrier` uses a `ReentrantLock` and a `Condition`. Each cycle is a "generation." When the last thread arrives, the barrier action runs in that thread, and the barrier moves to a new generation so it can be reused. If one participant is interrupted or times out, the barrier is **broken**, and every other waiting thread receives a `BrokenBarrierException`. This is important: a participant that dies without arriving would otherwise leave everyone else waiting forever. Use the timed form of `await` to prevent that.

Here is the notes' example with the compile error fixed and a timeout added:

```java
CyclicBarrier barrier = new CyclicBarrier(4,
 () -> System.out.println("All threads reached the barrier, merging results"));

for (int i = 0; i < 4; i++) {
 final int threadId = i;
 executor.submit(() -> {
 try {
 for (int phase = 0; phase < 3; phase++) {
 processPhase(threadId, phase);
 barrier.await(30, TimeUnit.SECONDS);
 }
 } catch (InterruptedException e) {
 Thread.currentThread().interrupt();
 } catch (BrokenBarrierException | TimeoutException e) {
 log.warn("worker {} stopped: barrier broken", threadId);
 }
 });
}
```

Real-world uses include time-stepped simulations, where every particle must be updated before the next step begins, and parallel matrix algorithms, where each phase needs all partitions done.

### Semaphore

A semaphore is an AQS shared-mode lock whose state is the number of permits. `acquire()` decrements the count if it can, and otherwise waits. `release()` increments it and wakes a waiter. Unlike a lock, a semaphore has no owner. Any thread can release a permit, which is useful for handoffs and dangerous for mistakes: releasing more permits than were acquired is not detected, so the semaphore quietly allows more concurrency than intended.

The notes' connection example has a design weakness. The caller must remember to call `releaseConnection`, and if it forgets, the permit leaks. A safer design hides the release inside a method:

```java
public <T> T withConnection(Function<Connection, T> work) throws InterruptedException {
 semaphore.acquire();
 try {
Connection conn = pool.borrowConnection();
 try {
 return work.apply(conn);
 } finally {
 pool.returnConnection(conn);
 }
 } finally {
 semaphore.release(); // the permit always comes back
 }
}
```

Real-world uses include bulkheads. A service might allow only 20 concurrent calls to a slow partner API. When the partner slows down, the other parts of the service keep working. Resilience4j provides a semaphore-based bulkhead for exactly this. Semaphores are also used to cap parallel file uploads and to limit database connections.

### Comparison

| Feature | CountDownLatch | CyclicBarrier | Semaphore |
|---|---|---|---|
| Reusable | No | Yes | Yes (permits return) |
| Who waits | One or more threads, for N events | N threads, for each other | Threads that need a permit |
| Counted by | `countDown()` from any thread | Arrivals of participating threads | `acquire()` and `release()` |
| Extra action | None | Optional barrier action | None |
| Typical use | Startup, start gun | Phased parallel work | Limiting concurrency |

### Advantages and disadvantages

Each tool expresses one coordination idea clearly. Their failure modes are the main cost. A latch that is never counted down hangs its waiter. A broken barrier takes the whole group down. A semaphore that is over-released quietly grows past its intended limit. Always use timed waits, and always put signals in `finally` blocks.

### Background

Dijkstra introduced semaphores in the 1960s as the basic building block for mutual exclusion and signaling. Barrier synchronization comes from parallel computing, where message-passing libraries have long provided barriers for phased computation. Java 5 brought all three utilities into `java.util.concurrent`, designed by Doug Lea.

### Current practice

For waiting on many asynchronous tasks, `CompletableFuture.allOf` and structured concurrency now replace many latch-based designs. Semaphores remain the standard way to limit concurrency. This matters more with virtual threads, where thread count is no longer the limit, so a semaphore in front of a downstream system is often the only protection against overload.

## Part 3: Phaser (Q23)

### The simple picture

Think of a relay race with checkpoints. Each leg ends at a checkpoint that every current team member must reach before the next leg starts. Runners can join or leave between legs, and the race keeps track of the phase number. That flexibility is what a `Phaser` provides.

### How it differs from CyclicBarrier

The notes' summary is accurate. A `CyclicBarrier` has a fixed party count set at construction. A `Phaser` lets parties register and deregister at any time, tracks phase numbers, and lets you run custom code on each phase transition through `onAdvance`, which can even terminate the phaser.

| Feature | CyclicBarrier | Phaser |
|---|---|---|
| Party count | Fixed at construction | Dynamic (`register`, `arriveAndDeregister`) |
| Phase number | Not exposed | Exposed, with `getPhase()` |
| Phase hook | Barrier action | `onAdvance(phase, parties)`, which can return `true` to terminate |
| Maximum parties | Not a published limit | 65,535 |
| Failure mode | Broken barrier affects all | A participant that never arrives blocks the phase |

### Internal working

A `Phaser` packs its phase number and the counts of registered and not-yet-arrived parties into one `long`, and updates it with compare-and-swap. Each arrival decrements the unarrived count. When that count reaches zero, the phase advances, `onAdvance` runs, the unarrived count resets to the registered count, and all waiters are released. Deregistering reduces both the registered and unarrived counts, so the phase does not wait for a party that has left.

### The code, corrected

The notes' example works in the normal case. It needs a `finally` block so that a failing worker still deregisters, which lets the rest of the group continue instead of waiting forever:

```java
Phaser phaser = new Phaser(1); // the main thread is party #1
for (int i = 0; i < 3; i++) {
 phaser.register();
 executor.submit(() -> {
 try {
 downloadData();
 phaser.arriveAndAwaitAdvance(); // end of phase 0
 processData();
 phaser.arriveAndAwaitAdvance(); // end of phase 1
 uploadResults();
 } finally {
 phaser.arriveAndDeregister(); // always leave, even on failure
 }
 });
}
phaser.arriveAndAwaitAdvance(); // main thread finishes phase 0
phaser.arriveAndDeregister(); // main thread leaves
```

If you want the phaser to stop after a fixed number of phases, override `onAdvance` and return `true` when the work is complete.

### Advantages and disadvantages

The flexibility is the advantage. Iterative algorithms where workers join or leave, or pipelines where stage sizes vary, fit naturally. The cost is complexity. Registration timing matters: a party registered in the middle of a phase is counted as not yet arrived, which can surprise you. A single missed `arrive` call blocks every other participant, which is why the `finally` block matters so much.

### Real-world examples

A parallel data pipeline might change the number of workers between stages, with some workers joining for a stage and leaving afterward. A game server's tick loop can use a phaser so that all active subsystems finish a tick before the next one starts, even as players and systems come and go. Iterative numerical solvers, where each iteration needs every worker's results, are another natural fit.

### Background and current practice

`Phaser` arrived in Java 7 as part of the `java.util.concurrent` work. Its design is similar in spirit to clocks in parallel programming languages such as X10, where tasks can register and synchronize over successive phases. For most applications today, `CompletableFuture` composition or structured concurrency is simpler. Phasers remain the right tool for iterative parallel algorithms where membership changes over time.

## Part 4: Atomic Classes and CAS (Q24)

### The simple picture

Imagine a shop with a price tag that says $5. You want to change it to $10, but only if nobody has changed it since you looked. The rule is: if the tag still says $5, write $10. If it doesn't, someone else changed it while you were looking, so look again and retry.

That is compare-and-swap. The check and the write happen as one indivisible action, so no other thread can slip in between them.

### What CAS is at the hardware level

CAS is a single CPU instruction. On x86, it is `CMPXCHG`. On newer ARM processors, the Large System Extensions provide a CAS instruction, and older ARM designs use load-linked and store-conditional pairs to achieve the same effect. The cache coherence protocol makes sure that the core performing the instruction owns the cache line exclusively during the operation, so no other core can change the value between the comparison and the write.

### The atomic classes

Java's atomic classes are built on this instruction. They fall into groups: primitives (`AtomicInteger`, `AtomicLong`, `AtomicBoolean`), object references (`AtomicReference`), arrays (`AtomicIntegerArray`, `AtomicLongArray`, `AtomicReferenceArray`), references with extra data to prevent ABA (`AtomicStampedReference`, `AtomicMarkableReference`), and high-throughput accumulators (`LongAdder`, `DoubleAdder`, `LongAccumulator`, `DoubleAccumulator`).

The read-modify-write methods in the notes are useful. `getAndUpdate` and `updateAndGet` take a function, and `accumulateAndGet` combines a value with the current one. Under the hood, these run the function inside a CAS retry loop, which means the function may run more than once. It must be fast and free of side effects.

### How incrementAndGet works

The notes' loop is a correct model of what CAS-based increment means: read the current value, compute the next value, attempt the CAS, and retry if another thread changed the value first. The actual Java implementation calls `Unsafe.getAndAddInt`. On x86, the JIT compiler turns that into a single locked add-and-exchange instruction, so the atomic increment completes in one hardware step without a retry loop in the common case. The model still explains the semantics well, and it explains why CAS-based code can spin under contention.

### CAS versus locks

Locks and CAS solve the same problem differently. A lock makes contending threads wait, which costs a context switch but uses no CPU while waiting. CAS never blocks, but a thread whose CAS fails must retry, which costs CPU and bounces the cache line between cores. With light contention, CAS is faster because nothing sleeps. With heavy contention, many threads spin and retry, which wastes CPU, so a lock or a contention-spreading structure such as `LongAdder` can perform better.

CAS-based algorithms are lock-free: some thread always makes progress, even if an individual thread may keep losing. They are not wait-free, which would guarantee every thread finishes in a bounded number of steps. An atomic counter is lock-free, so a thread can, in principle, be starved under extreme contention.

### The ABA problem

ABA is the classic trap. Imagine a lock-free stack whose top is node A, which points to node B:

1. Thread 1 reads the top as A, and prepares to change it to B.
2. Thread 2 pops A, pops B, and then pushes A back onto the stack.
3. Thread 1's CAS checks "is the top still A?" The answer is yes, so the CAS succeeds. But the stack's structure has changed, and B is no longer the next node, which corrupts the stack.

The value looked unchanged, but the history had changed. `AtomicStampedReference` solves this by pairing the reference with a version stamp, so a value that went A, then B, then back to A has a different stamp. The notes' example is correct except for one detail: `AtomicStampedReference` compares references with `==`, so `"A"` matches only because of string interning. For other objects, compare the actual references you intend to match.

### Advantages and disadvantages

CAS is fast when contention is low, avoids blocking, cannot deadlock on a single variable, and is simple for single-value updates. Its weaknesses are wasted work under heavy contention, the ABA problem in pointer-based structures, the limit to single variables (several atomics updated together are not atomic as a group), and the difficulty of writing correct lock-free algorithms beyond simple counters and state machines.

### Real-world examples

A sequence generator for IDs uses `AtomicLong.incrementAndGet()`, since each caller needs a unique returned value. A job scheduler can use `AtomicReference` for a state machine: a worker starts a job only by calling `compareAndSet(CREATED, RUNNING)`, so exactly one worker wins even if many try at once. `ConcurrentHashMap` uses CAS to install new bins into empty table slots. Lock-free queues, such as the one in `ConcurrentLinkedQueue`, use CAS on node links.

### Background

Compare-and-swap appeared in IBM's System/370 instruction set around 1970. In 1991, Maurice Herlihy showed that CAS has unlimited "consensus power," making it a universal building block for wait-free objects. Maged Michael and Michael Scott published a practical lock-free queue in 1996, and many concurrent data structures build on that design. Java exposed CAS through `Unsafe` in its early versions, offered the public atomic classes in Java 5, and added `VarHandle` in Java 9 for more precise memory-access control.

### Current practice

Use `AtomicReference` with `compareAndSet` for state transitions, `AtomicInteger` and `AtomicLong` for single values, and `LongAdder` for hot counters. Use `VarHandle` only when you're building custom concurrent structures. If an algorithm needs several variables to change together, use a lock or an immutable snapshot instead of trying to combine several atomics.

## Part 5: LongAdder (Q25)

### The simple picture

Imagine a classroom where every student must report each time they see a bird. If everyone shouts their count to the teacher, who writes it on one whiteboard, the teacher can't keep up. A better method is for each table group to keep its own tally pad. At the end of the day, someone adds up the pads. Each group writes quickly, and the total is computed only when it's needed.

`LongAdder` works this way. Each thread updates a tally that is mostly its own, and the total is computed when someone asks for it.

### Why AtomicLong slows down

When many threads increment the same `AtomicLong`, they all compete for the same memory location. Each CAS requires its core to own that cache line exclusively. When another core takes ownership, the line moves between cores, and most CAS attempts fail and retry. With 16 threads hammering one counter, the cache line bounces constantly, and throughput can flatten or even drop as threads are added.

### How LongAdder works internally

`LongAdder` extends `Striped64`, a class designed by Doug Lea and Martin Buchholz. It has two parts: a `base` value and an array of `Cell` objects. Under no contention, every update is a CAS on `base`, just like `AtomicLong`. When a CAS on `base` fails, the update moves to a cell instead. The array is created and grown on demand, and each thread uses a hashed probe to pick its cell, so different threads usually touch different cells. Each cell is padded so that it sits on its own cache line, which prevents **false sharing**, where unrelated cells on the same line still interfere with one another.

The notes' diagram is simplified in two ways. `sum()` adds the base value plus every cell, not only the cells. And cells only appear under contention, so an uncontended `LongAdder` costs about the same as an `AtomicLong`.

```java
LongAdder counter = new LongAdder();
counter.increment(); // from any thread
counter.add(5);
long total = counter.sum(); // base + all cells
counter.reset(); // set to zero (not safe during concurrent updates)
long old = counter.sumThenReset(); // read and clear, useful for periodic reporting
```

### Not an atomic snapshot

`sum()` reads each cell in turn. If updates happen while it runs, the total can mix moments in time. For metrics, this is fine, because a count that is off by a few in-flight updates is acceptable. For an exact balance check, it is not. Use a lock or `AtomicLong` when the exact value at a specific instant matters.

### Comparison

| Feature | `AtomicLong` | `LongAdder` |
|---|---|---|
| Write performance, low contention | Very good | Very good |
| Write performance, high contention | Degrades (cache-line bouncing) | Much better (spread across cells) |
| Read performance | Fast, single read | Slower; cost grows with the number of cells |
| Memory | Small | Larger (padded cells under contention) |
| Returns the new value on update | Yes (`incrementAndGet`) | No |
| Supports `compareAndSet` | Yes | No |
| Consistent snapshot read | Yes | No, the sum is an estimate under concurrent updates |
| Best for | IDs, exact values, CAS logic | Hot counters read occasionally |

### Advantages and disadvantages

`LongAdder` scales well when many threads update the same counter, and it reads cheaply when you read rarely. Its limits are that it can't return the updated value, can't perform conditional updates, and gives an estimate during concurrent updates. It also uses more memory, and reads become more expensive as the cell array grows.

### Real-world examples

A busy web server might count total requests across dozens of cores, since every request increments the counter. A reverse proxy might track bytes transferred per second. A cache might count hits and misses on every lookup. These are all write-heavy, read-rarely counters where `LongAdder` shines. A sequence generator, by contrast, needs unique values returned on each call, so it needs `AtomicLong`.

### Background and current practice

`LongAdder` arrived in Java 8, as part of the same work that added `StampedLock`. The idea of spreading a counter across per-CPU slots is older. The Linux kernel has long used per-CPU counters for the same reason. Metrics libraries commonly use this adder-style design on hot paths. Use `LongAdder` for counters that many threads update and few read, and use `AtomicLong` whenever you need a returned value or a compare-and-set.

## Putting It Together

Choose the tool that matches the rule you need to enforce. Use a lock when several fields must change together. Use a read-write lock, or a `ConcurrentHashMap` or immutable snapshot, when reads vastly outnumber writes. Use latches, barriers, and phasers to coordinate groups of threads, and semaphores to limit how many threads can use a resource at once. Use atomics for single-value updates and state transitions, and use `LongAdder` for hot counters. In every case, prefer the highest-level tool that expresses the rule, and always put signals and releases in `finally` blocks.

## Quick Summary

| Concept | Simple meaning | Key takeaway |
|---|---|---|
| ReadWriteLock | Many readers or one writer in the reading room | Helps when reads are frequent and substantial; never upgrade read to write |
| StampedLock | Check the price tag after reading | Optimistic reads avoid shared writes; validate before using values; not reentrant |
| CountDownLatch | Rocket countdown, one time only | Count down in `finally`; always use a timed `await` |
| CyclicBarrier | Hikers meeting at each checkpoint | Reusable; a broken barrier affects everyone; use timed waits |
| Semaphore | Parking lot with N spaces | Limits concurrency; release in `finally`; over-release is not detected |
| Phaser | Relay race where runners join and leave | Dynamic membership and phase hooks; deregister in `finally` |
| CAS | Change the price tag only if it still says $5 | One atomic step; retry on failure; beware ABA |
| Atomic classes | Single values updated without locks | Ideal for IDs, flags, and state transitions |
| LongAdder | Each team keeps its own tally pad | Fast under heavy write contention; the total is an estimate during updates |

# Concurrent Collections, Fork/Join, CompletableFuture, and Producer-Consumer, Explained Simply

The earlier parts covered locks, threads, and atomic variables. This part moves up a level, to the shared data structures themselves: maps, lists, queues, and the frameworks that split work and combine results. The main lesson is to use a concurrent collection built for your access pattern, rather than wrapping an ordinary collection in your own locks.

Some parts of the notes need correcting first:

1.**`computeIfAbsent` holds a lock on one bin while your function runs.** Keep the function short and cheap. Never read or update the same map from inside it, because that can deadlock or throw `IllegalStateException`.
2.**There is no `ConcurrentTreeMap` in the JDK.** `ConcurrentSkipListMap` is the built-in sorted concurrent map.
3.**`DelayedTask.compareTo` compares remaining delays, which change over time.** Heap ordering needs stable values, so compare absolute trigger times.
4.**The producer and consumer `run()` methods don't compile as written.** `put`, `take`, and `poll` throw `InterruptedException`, which a `Runnable` cannot throw. Also, calling `shutdown()` doesn't stop producers blocked in `put()` on a full queue, so the shutdown can hang.
5.**The `Exchanger` example stalls on low traffic.** A partial batch never leaves the producer until 100 orders have arrived, and an `Exchanger` only pairs two threads.

## Part 1: Concurrent Collections (Q26)

### The simple picture

Imagine a shared filing cabinet. An ordinary collection is a cabinet with one lock for the whole thing: only one person can open it at a time, so everyone else waits, even to read a different drawer. Concurrent collections use different designs. Some are cabinets with a separate lock on each drawer. Some give readers a photocopy of the whole file, so readers never wait. Others use lock-free tricks so that people can add and remove papers without a lock at all.

### Why ordinary collections fail under concurrency

Before Java 5, the usual options were `Hashtable` and `Vector`, which lock every method, or `Collections.synchronizedMap` and its relatives, which wrap a collection with one lock. Both serialize all access, so a hundred readers queue behind each other. They also don't protect compound actions. This check-then-act code is a race condition, even though each call is individually synchronized:

```java
if (!map.containsKey(key)) { // another thread can insert here
 map.put(key, computeValue()); // so this overwrites its work
}
```

Concurrent collections fix this by providing atomic compound operations such as `putIfAbsent`, `computeIfAbsent`, and `merge`.

### ConcurrentHashMap

`ConcurrentHashMap` is the most-used concurrent collection. In Java 8, its design changed to use CAS for inserting into empty slots, and `synchronized` on the first node of a slot when the slot is occupied. Reads take no locks at all, because the relevant fields are volatile. Long chains in a slot are converted into balanced trees once they reach eight entries. When the table grows, multiple threads can help move entries, so the resize doesn't stall everything.

The notes' examples are correct:

```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.merge("key", 1, Integer::sum); // atomic counter update
map.computeIfAbsent("key", k -> expensiveCompute()); // atomic, but holds the bin lock
```

For high-traffic per-key counters, `computeIfAbsent(key, k -> new LongAdder()).increment()` avoids contention on a single hot key. Also note that `size()` is an estimate under concurrent updates, so use `mappingCount()` when you want a `long` count, and remember that both are approximate while writes are happening.

Advantages include fast concurrent reads, fine-grained writes, and atomic compound operations. Disadvantages include the fact that iteration is weakly consistent (you see some concurrent changes, not necessarily all), a slow `computeIfAbsent` blocks other keys in the same slot, and you can't guarantee an exact size during writes.

Real-world uses include in-memory caches for lookups, session stores keyed by session ID, request counters per endpoint, and deduplication with `putIfAbsent` when two requests might race to create the same record.

### CopyOnWriteArrayList and CopyOnWriteArraySet

These collections use a different strategy: copy on write. The list holds a volatile reference to an array. Reads simply read that reference and work on the array they got, so they never block. Writes take a lock, copy the entire array, change the copy, and publish it. Iterators walk the snapshot that existed when they were created, so you never see `ConcurrentModificationException`. Iterators can't remove items, because they work on a snapshot.

The notes' `listeners.forEach(l -> notify(l))` is safe because it iterates over a frozen snapshot. `CopyOnWriteArraySet` is a set built on the same mechanism, so it has the same costs.

The trade-off is cost. Every write copies the whole array, so adding to a list of 100,000 items means copying 100,000 references, and lots of writes create lots of garbage. Snapshot reads are also slightly stale, which is fine for listeners and blocklists but wrong for data that must reflect the latest update.

Real-world uses include event listener lists, feature-flag rules that change a few times a day, and blocklists of IP addresses that reload periodically. The idea is the same as copy-on-write memory pages in operating systems and the read-copy-update technique used in the Linux kernel.

### ConcurrentLinkedQueue and ConcurrentLinkedDeque

These are lock-free queues built on CAS. The queue is based on the Michael and Scott algorithm from 1996. Operations never block: `poll` returns `null` when the queue is empty instead of waiting. Both are unbounded. `size()` takes linear time and is not exact under concurrency, so avoid calling it on a hot path.

The deque adds insertion and removal at both ends, which suits work-stealing style designs. Use these when the consumer is happy to check periodically and do other work when the queue is empty. If a consumer should sleep until work arrives, use a `BlockingQueue` (covered in Part 2).

### ConcurrentSkipListMap and ConcurrentSkipListSet

These are sorted collections. A skip list is a layered linked list with random shortcuts, and William Pugh described it in 1990 as an alternative to balanced trees. Lookups, inserts, and removals take logarithmic time on average, and the structure supports concurrent updates with CAS. These collections support range views such as `subMap` and `headMap`.

They suit sorted concurrent data: time-ordered event stores, leaderboards, and price-level books in trading systems. Their costs are higher per operation than a hash map, and they are unnecessary if you don't need sorted order.

### Comparison

| Collection | Strategy | Reads | Writes | Ordered | Best for |
|---|---|---|---|---|---|
| ConcurrentHashMap | Per-slot locks, CAS | Lock-free | Fine-grained | No | General shared maps and caches |
| CopyOnWriteArrayList | Copy on write | Lock-free snapshot | Copies whole array | Insertion order | Rarely changed listener lists |
| CopyOnWriteArraySet | Copy on write | Lock-free snapshot | Copies whole array | Insertion order | Rarely changed unique items |
| ConcurrentLinkedQueue | Lock-free CAS | Non-blocking | Non-blocking | FIFO | Non-blocking work queues |
| ConcurrentLinkedDeque | Lock-free CAS | Non-blocking | Non-blocking | Both ends | Work queues with two ends |
| ConcurrentSkipListMap | Lock-free skip list | Log time | Log time | Sorted | Sorted, range-query data |
| ConcurrentSkipListSet | Lock-free skip list | Log time | Log time | Sorted | Sorted unique items |

### Background

Before Java 5, the standard answer was a synchronized wrapper. Doug Lea's `java.util.concurrent` changed that in 2004, with `ConcurrentHashMap` as the flagship. Java 7 implemented it with lock striping, using 16 segments by default. Java 8 replaced segments with per-slot locking and CAS, which reduced memory and improved scaling.

## Part 2: BlockingQueue (Q27)

### The simple picture

Think of a restaurant's order rail. Waiters clip tickets onto the rail, and cooks take tickets from it. If the rail is full, the waiter has to wait until a cook takes a ticket. If the rail is empty, the cook waits for the next ticket. The rail's limited space is what keeps waiters from piling up more orders than the kitchen can handle. That is **back-pressure**, and it's the core idea of a `BlockingQueue`.

### The four flavors of each operation

A `BlockingQueue` offers each operation in four forms, and choosing the right one matters. `put` and `take` block until they can succeed. `offer` and `poll` return `false` or `null` immediately when they can't succeed. `offer(e, timeout)` and `poll(timeout)` wait up to a limit. `add` and `remove` throw exceptions when they fail. For a producer, `put` gives back-pressure. For a consumer that also has other work, `poll(timeout)` lets it check for shutdown or periodic tasks.

### The implementations

**ArrayBlockingQueue** is a bounded circular array with one lock and two conditions, `notEmpty` and `notFull`. Its capacity is fixed when you create it, so memory use is predictable. The optional fairness flag makes waiting threads proceed in order, at some throughput cost. This is the safest default for bounded producer-consumer work.

**LinkedBlockingQueue** uses linked nodes and two separate locks, one for putting and one for taking, which lets producers and consumers run at the same time. Its default capacity is `Integer.MAX_VALUE`, so `new LinkedBlockingQueue<>()` is effectively unbounded. That is the trap: a fast producer fills memory until the JVM runs out. Always pass a capacity in production.

**PriorityBlockingQueue** is an unbounded heap ordered by priority, so `put` never blocks. Elements must implement `Comparable` or be given a `Comparator`. Items with equal priority have no guaranteed order, and an unbounded heap carries the same memory risk as an unbounded linked queue.

**SynchronousQueue** has no capacity at all. Every `put` waits until another thread calls `take`, which makes it a direct handoff. `offer` returns `false` if no taker is waiting. Since Java 6, it is lock-free and built on a dual stack or queue design. `CachedThreadPool` uses it, which is why cached pools create a new thread whenever no idle thread is waiting for a task.

**DelayQueue** holds elements that become available after a delay. `take` returns only the element whose delay has expired, and it waits until the earliest one is due. Here is a corrected delayed-task class:

```java
class DelayedTask implements Delayed {
 private final long executeAtMillis;

 DelayedTask(long delayMillis) {
 this.executeAtMillis = System.currentTimeMillis() + delayMillis;
 }

 @Override
 public long getDelay(TimeUnit unit) {
long remaining = executeAtMillis - System.currentTimeMillis();
 return unit.convert(remaining, TimeUnit.MILLISECONDS);
 }

 @Override
 public int compareTo(Delayed other) {
 // compare fixed trigger times, not remaining delays (which change as time passes)
 return Long.compare(this.executeAtMillis,
 ((DelayedTask) other).executeAtMillis);
 }
}
```

### The producer-consumer code, corrected

The notes' pattern is right. The `run()` methods need to handle `InterruptedException` and stop cleanly:

```java
BlockingQueue<Order> orderQueue = new ArrayBlockingQueue<>(100);

class OrderProducer implements Runnable {
 public void run() {
 try {
 while (!Thread.currentThread().isInterrupted()) {
 orderQueue.put(receiveOrderFromKafka()); // blocks when full
 }
 } catch (InterruptedException e) {
 Thread.currentThread().interrupt(); // stop, and keep the flag
 }
 }
}

class OrderConsumer implements Runnable {
 public void run() {
 try {
 while (true) {
Order order = orderQueue.take(); // blocks when empty
 processOrder(order);
 }
 } catch (InterruptedException e) {
 Thread.currentThread().interrupt();
 }
 }
}
```

### Advantages and disadvantages

`BlockingQueue` removes the need for hand-written `wait` and `notify` loops, and it provides back-pressure for free when bounded. Its costs are the unbounded-capacity trap, the single-process scope (it doesn't survive a crash or span machines), and the fact that a full queue silently slows producers, which can hide a downstream problem until latency explodes. Queue depth is a leading indicator of trouble, so monitor it.
### Real-world examples

An order-intake service might receive messages from a broker and place them on a bounded queue, so a slow processing step pushes back on intake. A log shipper uses a bounded queue between application threads and a network writer. Image pipelines use queues between download, resize, and upload stages. Email systems use `DelayQueue` to schedule retries with increasing delays.

### Background

The bounded-buffer problem was posed and solved by Edsger Dijkstra in the 1960s, and it is the textbook model for producer-consumer work. Java 5 turned that model into the `BlockingQueue` interface, designed by Doug Lea.

### Modern alternatives

For work that crosses services, use a durable broker such as Kafka, RabbitMQ, or a cloud queue. These survive restarts and let many machines share the work. For very high-throughput in-process messaging, the LMAX Disruptor uses a lock-free ring buffer and avoids allocation, and Log4j2's asynchronous logging is built on it. Reactive Streams, exposed in Java 9 through the `Flow` API, formalize back-pressure between stages. Java 21's virtual threads make a blocking consumer per work item affordable, which simplifies many designs that previously needed a queue.

## Part 3: The Fork/Join Framework (Q28)

### The simple picture

Imagine sorting a huge pile of papers with a team. A manager splits the pile in half, gives one half to a colleague, and keeps the other half. Each person splits again if their pile is still too big, until each pile is small enough for one person to sort quickly. Then the sorted piles are merged back together. The splitting is the **fork**. Waiting for the colleague's result is the **join**.

That's divide-and-conquer, and Fork/Join is Java's framework for it. The diagram in the notes shows the recursion tree. Each level halves the work, and the leaves are sorted directly, then merged back up.

### The threshold

Forking has overhead, so you stop splitting at a threshold. If the threshold is too small, you create huge numbers of tiny tasks, and overhead dominates. If it's too large, you don't create enough parallelism. Typical thresholds are a few thousand elements, but the right value depends on the work, so measure it.

### Internal working: work stealing

Each worker thread in a `ForkJoinPool` has its own double-ended queue of tasks. When a worker forks subtasks, it pushes them onto the top of its own deque, and it pops from the top when it's ready for more work. This last-in, first-out order is cache-friendly, because the most recent subtask is the one whose data is probably still in the cache.

When a worker runs out of tasks, it becomes a **thief**. It steals from the bottom of another worker's deque, which holds the oldest tasks. Those tasks were forked earliest, which in divide-and-conquer means they are usually the largest pieces of work. Stealing big pieces is efficient, because a thief doesn't need to steal often. The owner and the thief work at opposite ends of the deque, so they rarely collide.

The notes' diagram is ambiguous about which task gets stolen. The rule is that the thief takes the oldest task, from the end opposite the owner.

Another important detail is that a worker waiting on `join()` doesn't sleep. It keeps running other tasks from its own deque or steals more, so the core stays busy. The Blumofe and Leiserson analysis of work stealing shows that this approach keeps the expected running time close to the total work divided by the number of workers, plus the critical path length.

Two terms help explain why parallel algorithms scale. The **work** is the total number of operations. The **span** is the longest chain of operations that must run one after another. Parallelism is work divided by span. A divide-and-conquer sort has span proportional to log n merge steps, so it parallelizes well.

### The RecursiveAction example

The notes' merge sort uses `RecursiveAction`, which returns nothing. `invokeAll(left, right)` forks both halves and waits for both. Two details are worth knowing. The `merge` helper needs a temporary buffer to merge in place efficiently, and the base case calls `Arrays.sort`, which for primitive arrays uses a dual-pivot quicksort rather than merge sort, so the class name is slightly misleading. The JDK's own `Arrays.parallelSort` uses the same machinery.

### The RecursiveTask example

The notes' `ParallelSum` shows the standard idiom:

```java
left.fork(); // hand the left half to the pool
long rightResult = right.compute(); // do the right half in this thread
long leftResult = left.join(); // wait for the left half
```

Computing one half directly, instead of forking both, saves a task and keeps the current thread busy. This is a small optimization that matters in deep recursion.

### Advantages and disadvantages

Fork/Join balances load automatically, handles uneven work well through stealing, and fits recursive problems naturally. Its weaknesses are that it is designed for CPU-bound work and blocking inside a task wastes a worker, that tiny tasks add overhead, that deep recursion uses stack space, and that side effects in parallel tasks have no guaranteed order. The shared common pool is also a risk: heavy use of parallel streams by one library can slow another.

### Real-world examples

Parallel streams (`list.parallelStream()`) run on the common Fork/Join pool. `Arrays.parallelSort` uses the same framework. Image processing splits a picture into tiles, transforms each tile in parallel, and combines the results. Matrix multiplication and tree-based computations (for example, summing values in a large in-memory tree) fit well. The virtual-thread scheduler in modern Java is itself a Fork/Join pool, which shows how central the design is.

### Background

Doug Lea described the framework in a 2000 paper, and it shipped in Java 7 in 2011. Its ideas come from Cilk, developed at MIT in the mid-1990s, which popularized work stealing with provable performance bounds. Other systems use the same idea: Intel's Threading Building Blocks, Rust's Rayon for parallel iterators, and Go's runtime scheduler all steal work between workers.

### Current practice

For simple data-parallel operations on large collections, parallel streams are often enough. For control over isolation, create a dedicated `ForkJoinPool` for each workload, so one job can't starve another. Since Java 19, `ForkJoinPool` implements `AutoCloseable`, so use try-with-resources to shut it down. Keep the Fork/Join pool for CPU-bound work, and send blocking I/O to a thread pool or virtual threads.

## Part 4: CompletableFuture and Threads (Q29)

### The simple picture

A plain `Future` is like a receipt from a restaurant. You can wait at the counter until the food comes out, but you can't attach instructions to it. A `CompletableFuture` is a receipt with instructions: "when the soup is ready, bring it to table 4, then, when the main course is ready, combine them and send the bill." You stop waiting at the counter and let the chain of steps run on its own.

### Before CompletableFuture

Java 5's `Future` offered only `get()`, which blocks the caller, and `cancel()`. Composing two futures meant writing extra threads or blocking twice. Guava's `ListenableFuture` added callbacks, and JavaScript promises, C#'s `Task`, and Scala's `Future` offered composition in other ecosystems. Java 8's `CompletableFuture` brought that model into the standard library.

### How it relates to threads

A `CompletableFuture` doesn't have its own thread. The `Async` methods run work on an executor. By default, `supplyAsync` uses `ForkJoinPool.commonPool()`, whose worker threads have names like `ForkJoinPool.commonPool-worker-1`. On machines with two or fewer usable processors, the common pool has too few threads, so the JDK runs each task on a new thread instead.

The version without `Async` runs the next step on whichever thread completed the previous step, or on the calling thread if the result is already available. This detail matters: a non-async `thenApply` on an I/O future may run on an I/O thread, so a heavy callback there blocks I/O.

The notes' custom-executor example is the right pattern for I/O:

```java
ExecutorService ioPool = Executors.newFixedThreadPool(20);

CompletableFuture<String> result = CompletableFuture
 .supplyAsync(() -> callExternalAPI(), ioPool) // I/O on the I/O pool
 .thenApplyAsync(data -> transform(data), ioPool) // continue on the I/O pool
 .thenApply(result -> format(result)); // quick work, runs wherever
```

The common pool has roughly one fewer worker than the number of CPU cores. Each blocking call inside it occupies a worker, and once they're all blocked, every parallel stream and every default `CompletableFuture` in the JVM stalls. Give I/O its own pool. Also shut the pool down when the application stops, because its threads are non-daemon.

### Internal working

A `CompletableFuture` stores its result once, using a CAS on a field. Dependent steps are stored as completion objects in a lock-free stack, the same kind of Treiber stack described in the atomic-classes discussion. When the result arrives, the completing thread pops the dependents and runs them, or submits them to an executor for `Async` steps. If you attach a step to a future that's already complete, the step runs immediately in the calling thread.

Exceptions are stored as the result and wrapped in `CompletionException` as they move through stages. `exceptionally`, `handle`, and `whenComplete` catch them at the points you choose.

### A practical example

A profile page needs a user, their orders, and their recommendations. These calls are independent, so run them in parallel, combine the results, and fall back gracefully if one is slow:

```java
CompletableFuture<User> user = CompletableFuture.supplyAsync(() -> users.get(id), ioPool);
CompletableFuture<List<Order>> orders = CompletableFuture.supplyAsync(() -> orderService.list(id), ioPool);

CompletableFuture<Profile> profile = user
 .thenCombine(orders, Profile::new)
 .orTimeout(2, TimeUnit.SECONDS) // Java 9+
 .exceptionally(ex -> Profile.fallback(id)); // receives CompletionException
```

### Advantages and disadvantages

The model composes asynchronous steps without blocking a thread for each one, and error handling becomes explicit. The disadvantages are real. An exception with no handler is silently lost in a fire-and-forget chain. Stack traces point to executor internals and are harder to read. Thread names can be confusing. `cancel()` marks the future as cancelled but doesn't stop the underlying work unless you design for it. And long chains of callbacks are hard to read and debug.

### Real-world examples

Aggregator services that call several downstream systems in parallel use this pattern heavily. Checkout flows compose payment, inventory, and fraud checks. Dashboards gather metrics from several sources and render whatever arrives within the deadline.

### Background and current practice

Reactive libraries such as Project Reactor and RxJava provide richer composition, with built-in back-pressure. With virtual threads in Java 21 and later, many teams replace deep callback chains with ordinary blocking code running on a virtual-thread executor, which is easier to read and debug. `CompletableFuture` remains the right tool for composing a handful of independent asynchronous calls, and structured concurrency (`StructuredTaskScope`, still in preview) is another option for grouping related tasks so that failure and cancellation propagate cleanly.

## Part 5: The Producer-Consumer Pattern (Q30)

### The simple picture

Think of a bakery. Bakers produce loaves and place them on a shelf. Sellers take loaves from the shelf and sell them. The shelf has limited space, so bakers wait when it's full, which prevents overproduction. Sellers wait when it's empty. When the shop closes, the bakers stop first, and the sellers sell whatever is left on the shelf before going home. The shelf is the queue, and the rules for closing the shop are the shutdown design.

Most of the design is in the shutdown. Starting threads is easy. Stopping them without losing work or hanging is where production systems get it wrong.

### Implementation 1: BlockingQueue, with a graceful shutdown

The notes' design uses `ArrayBlockingQueue` with two executors. Its main flaw is the shutdown. Setting `running = false` and calling `shutdown()` doesn't interrupt producers blocked in `put()`, and if consumers have stopped, a full queue will block the producers forever. The fix combines interrupts with a poison pill: a sentinel object that tells each consumer to stop after everything ahead of it has been processed.

```java
public class OrderProcessingSystem {
 private static final Order POISON = new Order(-1); // sentinel: no more work
 private final BlockingQueue<Order> queue = new ArrayBlockingQueue<>(1000);
 private final ExecutorService producers = Executors.newFixedThreadPool(2);
 private final ExecutorService consumers = Executors.newFixedThreadPool(10);

 public void start() {
 for (int i = 0; i < 2; i++) {
 producers.submit(() -> {
 try {
 while (!Thread.currentThread().isInterrupted()) {
 queue.put(receiveFromKafka()); // back-pressure when full
 }
 } catch (InterruptedException e) {
 Thread.currentThread().interrupt(); // stop on shutdown
 }
 });
 }
 for (int i = 0; i < 10; i++) {
 consumers.submit(() -> {
 try {
 while (true) {
Order order = queue.take();
 if (order == POISON) break; // stop after the sentinel
 processOrder(order);
 }
 } catch (InterruptedException e) {
 Thread.currentThread().interrupt();
 }
 });
 }
 }

 public void shutdown() throws InterruptedException {
 producers.shutdownNow(); // interrupt blocked producers
 if (!producers.awaitTermination(10, TimeUnit.SECONDS)) {
 throw new IllegalStateException("producers still running; not sending stop signals");
 }
 for (int i = 0; i < 10; i++) queue.put(POISON); // one sentinel per consumer
 consumers.shutdown();
 consumers.awaitTermination(30, TimeUnit.SECONDS);
 }
}
```

The order matters. Producers stop first, so no real order arrives after a sentinel. The queue is FIFO, so each consumer processes every order placed before its sentinel. The check on producer termination is essential: if a producer is stuck in a blocking Kafka call that ignores interrupts, sending sentinels would let real orders slip in after them and be lost.

One more real-world point: an order received from Kafka but not yet placed on the queue is lost when the producer is interrupted, unless you commit the Kafka offset only after processing. Commit late, and the broker redelivers anything unfinished. Note also that `KafkaConsumer` is not thread-safe, so one thread should poll it and hand records to workers.

### Implementation 2: Exchanger

An `Exchanger` lets exactly two threads meet at a point and swap objects. The producer fills a batch and swaps it for the consumer's empty batch, so each side always works on a list the other side isn't touching. This is double buffering.

The notes' design has two weaknesses. First, the producer exchanges only when it has 100 items, so a slow trickle of orders can sit in the batch for hours. The fix is a timed exchange, `exchanger.exchange(batch, 1, TimeUnit.SECONDS)`, where a `TimeoutException` means "no partner yet, keep collecting." Second, an `Exchanger` pairs exactly two threads. With many producers and consumers, it can't do the job, and a `BlockingQueue` is the right tool. The notes' `Thread` objects are also never started, and the `exchange` calls throw `InterruptedException`, which requires handling inside the thread body.

### Comparison of the two approaches

| Aspect | BlockingQueue | Exchanger |
|---|---|---|
| Number of threads | Any number of producers and consumers | Exactly two at a time |
| Back-pressure | Natural, when bounded | Implicit, since each side waits for the other |
| Batching | Consumers take one item at a time, or drain in batches | Built in, as a whole batch is swapped |
| Low-traffic behavior | Items flow immediately | Needs a timed exchange to flush partial batches |
| Shutdown | Poison pills and interrupts | Harder, since both sides must reach the exchange |
| Best for | Most producer-consumer work | Two-stage pipelines that exchange buffers |

### Advantages and disadvantages

The producer-consumer pattern decouples the rate of arrival from the rate of processing, smooths traffic spikes, and makes stages easy to scale independently. Its disadvantages are the unbounded-queue trap, the difficulty of shutdown and error handling (a crashed consumer can silently lose its item), ordering issues when several consumers run in parallel, and the hidden latency a queue adds.

### Real-world examples

A service receives events from Kafka, places them on a bounded in-process queue, and runs worker threads that process them and commit offsets after processing. A logging system hands log events from application threads to an asynchronous writer. An email service uses a queue to send messages without making users wait, and a `DelayQueue` to schedule retries. Video platforms split transcoding into stages connected by bounded queues, so a backlog in one stage applies back-pressure to the stage before it.

### Background

The bounded-buffer problem that this pattern solves was posed by Dijkstra in the 1960s, and it is the foundation of most message-passing systems. Doug Lea's `Exchanger` arrived in Java 5. The LMAX Disruptor, published in 2011, showed how a lock-free ring buffer can outperform queues for very high-throughput pipelines.

### Current practice

For work that crosses machines, use a durable broker, and design consumers to be idempotent, since redelivery is normal. Inside a process, use bounded `BlockingQueue`s or the Disruptor, combined with virtual threads for blocking consumers. Use reactive streams when you need standard back-pressure across library boundaries. Whichever you pick, bound the queue, define exactly what happens at shutdown, and alert on queue depth.

## Putting It Together

Use a concurrent collection that matches your access pattern: `ConcurrentHashMap` for shared maps, copy-on-write for rarely changed lists, lock-free queues for non-blocking work, skip lists for sorted concurrent data. Use a bounded `BlockingQueue` when producers and consumers run at different speeds. Use Fork/Join for CPU-bound divide-and-conquer work, and parallel streams for simple data-parallel operations on large collections. Use `CompletableFuture` to compose a handful of asynchronous calls, with an explicit executor for I/O. Design the shutdown path as carefully as the happy path, because that is where producer-consumer systems lose data and hang.

## Quick Summary

| Concept | Simple meaning | Key takeaway |
|---|---|---|
| ConcurrentHashMap | A filing cabinet with a lock per drawer | Lock-free reads and atomic compound operations; avoid long work inside `computeIfAbsent` |
| CopyOnWrite collections | Readers get a photocopy of the file | Reads never block, but every write copies the whole collection |
| Lock-free queues | Papers added and removed without a lock | Non-blocking; `poll` returns null when empty |
| Skip-list collections | A sorted, layered shortcut list | Sorted and concurrent, at logarithmic cost |
| BlockingQueue | A restaurant order rail | Bounded queues give back-pressure; always set a capacity |
| Fork/Join | Split a pile, sort the pieces, merge | Work stealing balances load; keep blocking work out of it |
| CompletableFuture | A receipt with instructions attached | Compose async steps; give I/O its own executor; handle exceptions explicitly |
| Producer-consumer | A bakery shelf with a limited size | Stop producers first, then send poison pills, and commit work only after it's processed |

# Real-World Design Problems and Uncaught Exceptions, Explained Simply

This part applies what you've learned to design problems that come up in interviews and production systems: a singleton, a ring buffer, a rate limiter, an expiring cache, and what happens when a thread crashes. Several examples in the notes contain bugs that would not survive code review, so the corrections come first:

1.**The expiring cache's `get` can delete a fresh value.** It reads an expired entry, another thread writes a new value for the same key, and then the first thread calls `map.remove(key)`, which removes the new value too. The fix is `map.remove(key, entry)`, which removes only the exact entry that was seen.
2.**`getOrCompute` is check-then-act.** When many threads miss the cache at once, each one runs the expensive loader. This is a cache stampede, and it's the same kind of race as Part 1's bank account.
3.**The rate limiter's `acquire()` busy-polls every 10 ms.** That wastes CPU, adds latency, and lets newcomers overtake threads that have been waiting longer. It should compute how long to wait.
4.**The uncaught-exception section says the thread "terminates silently."** The default handler prints the stack trace to standard error. The real silent failure is an exception captured inside a `Future` that nobody reads.
5.**Smaller issues in the cache.** The nested `record` needs its own type parameter, because a nested record is implicitly static and can't reuse the outer `V`. The cache uses wall-clock time for expiry, creates a non-daemon thread for every instance, and its cleanup task stops permanently if one run throws.

## Part 1: A Thread-Safe Singleton (Q31)

### The simple picture

A company that has exactly one CEO. Anyone who asks for the CEO gets the same person. The CEO is hired lazily, when first needed. The difficulty is that two departments might ask at the same moment, and both might try to hire one. A thread-safe singleton prevents that double hire.

### The race in the naive version

The obvious code is `if (instance == null) instance = new Singleton();`. Two threads can both see `null` and both create an instance. This is the check-then-act race from the earlier discussion of race conditions, applied to object creation.

### Double-checked locking, step by step

The notes' double-checked version solves this with two checks. The first check runs without a lock, so the common case, where the instance already exists, is fast. The second check runs inside the lock, so only one thread creates the instance.

The `volatile` keyword is essential, and the reason is subtle. Creating an object is really three steps: allocate memory, run the constructor to initialize the fields, and assign the reference to `instance`. Nothing in the language forbids the compiler or CPU from doing the assignment before the constructor finishes. Then another thread can see a non-null `instance` that points to an object whose fields are still zero. The `volatile` write creates a happens-before edge, so any thread that reads the reference also sees a fully initialized object.

Before Java 5, the memory model did not give `volatile` these semantics, which is why double-checked locking was widely considered broken until the JSR-133 memory model revision in 2004. Bill Pugh's critique in the early 2000s made the problem widely known. With Java 5 and later, the `volatile` version is correct.

### The holder pattern

The notes' Bill Pugh version uses a nested class. The JVM loads and initializes `Holder` only when `Holder.INSTANCE` is first accessed, and class initialization is protected by a JVM-level lock. That gives you laziness and thread safety without any explicit synchronization. It is the simplest correct lazy singleton, and it's what most experienced developers reach for. Its limitation is that it can't take constructor arguments that vary at runtime, and it's hard to reset in tests.

### The enum singleton

`enum Singleton { INSTANCE; }` is what Joshua Bloch recommends in *Effective Java*. Enum constants are created once by the JVM, and the language prevents reflection from creating new enum instances. Serialization also preserves the single instance, which the other two forms don't do automatically. The costs are that an enum can't extend a class and it's less natural when the singleton needs to be lazy with parameters.

### Pitfalls that apply to the other forms

A class with a private constructor can be broken with reflection, since `setAccessible(true)` lets code call the constructor. Deserialization creates a new object unless you define `readResolve()` to return the existing instance. Cloning can also create a copy unless you override `clone()` to refuse. These are the traps interviewers often ask about.

### Comparison

| Approach | Lazy | Thread-safe | Serialization safe | Reflection safe | Best for |
|---|---|---|---|---|---|
| Double-checked locking | Yes | Yes, with `volatile` | No, needs `readResolve` | No | Interview answers, lazy singletons with parameters |
| Holder idiom | Yes | Yes, by the JVM | No, needs `readResolve` | No | Most lazy singletons |
| Enum | No, created at class load | Yes | Yes | Yes | Simple, safest default |

### Real-world examples

The JDK's `Runtime.getRuntime()` returns a single instance created by the JVM. Loggers and configuration registries are usually singletons. An important caveat: Spring's `@Scope("singleton")`, which is the default, means one instance per container, not one per JVM. That is a different concept, and it's the reason dependency injection is preferred over global singletons. Singletons are global state, which makes tests harder to isolate.

### Background and current practice

The singleton appears in the *Design Patterns* book by the Gang of Four in 1994. Modern practice favors dependency injection, where a framework creates one instance and passes it to the classes that need it. A hand-written singleton is usually needed only for a truly process-wide resource, and then the enum or holder form is the safe default.

## Part 2: A Thread-Safe Bounded Buffer (Q32)

### The simple picture

Think of a parking garage with a fixed number of spaces. Cars enter at one gate and leave at another. The entrance gate waits when the garage is full. The exit gate waits when it's empty. The garage is a ring: when a space at the end is used, the next car uses the space at the beginning, once it's free.

That is a ring buffer. It's an array with two positions, `head` and `tail`, that wrap around with modulo arithmetic. The `count` field tells you how many items are inside.

### How the code works

Every operation runs under one lock. `put` waits while the buffer is full, stores the item at `tail`, moves `tail` forward, and signals a consumer. `take` waits while the buffer is empty, reads the item at `head`, clears that slot so the garbage collector can reclaim the item, moves `head` forward, and signals a producer.

The `while` loops are essential, for the reason covered in the earlier discussion of `wait` and `notify`: a woken thread must recheck the condition, because another thread may have changed it first. `await()` atomically releases the lock and parks the thread. When it returns, the lock is held again, which is why the `finally` block can safely unlock.

### Why two conditions

Each condition has only one kind of waiter. Producers wait on `notFull`, and consumers wait on `notEmpty`. So `notFull.signal()` wakes exactly one producer, and `notEmpty.signal()` wakes exactly one consumer. With a single condition and `notifyAll()`, every wakeup would hit both kinds of thread, and most would go back to sleep. This is the same reasoning as the earlier discussion of `notify` versus `notifyAll`. Here `signal()` is safe because the waiters on each condition are interchangeable.

### Internal working

Inside `ReentrantLock`, `await()` moves the calling thread into the condition's own wait queue and releases the lock. `signal()` moves the first waiting node from the condition queue to the lock's queue, so it competes for the lock like any other thread. This is the AQS mechanism described in the earlier section on `ReentrantLock`.

### Advantages and disadvantages

The design uses fixed memory and allocates nothing per operation, which keeps garbage collection quiet. It gives back-pressure for free: a full buffer slows producers down without any extra logic. The drawbacks are the single lock, which becomes a bottleneck when many threads compete, and the fixed capacity, which you must choose up front. Writing this yourself is also easy to get subtly wrong, which is why production code uses the JDK's version.

### Real-world examples

The JDK's `ArrayBlockingQueue` uses exactly this design, with one lock and two conditions named `notEmpty` and `notFull`. Audio drivers use ring buffers between the sound card and the application, and the kernel's `io_uring` interface uses shared ring buffers between user space and the kernel. Network stacks use them for packet queues.

### Scaling beyond one lock

When one lock is the bottleneck, there are three options. You can stripe the buffer into several independent rings, each with its own lock. You can use a lock-free single-producer, single-consumer ring, a design Leslie Lamport analyzed in his 1977 work on concurrent reading and writing, where the producer owns the tail index and the consumer owns the head index. Or you can use the LMAX Disruptor, which uses sequence numbers and claim strategies to support many producers without a lock on the hot path.

### Background and current practice

Ring buffers are older than threads. They appear in hardware buffering and operating systems long before Java existed. Today, use `ArrayBlockingQueue` in ordinary code, and reach for a Disruptor-style design only when measurement shows the queue's lock is the bottleneck.

## Part 3: A Rate Limiter (Q33)

### The simple picture

A bucket of tokens. The bucket holds at most a set number of tokens, and it refills at a steady drip. Each request takes one token. If a token is available, the request goes through. If the bucket is empty, the request is rejected with HTTP 429, "Too Many Requests." The bucket size controls how large a burst can be, and the refill rate controls the sustained rate.

With `TokenBucketRateLimiter(100, 100)`, a client can send 100 requests at once, then gets roughly 100 per second after that. The burst capacity and the sustained rate are separate knobs, which is one of the token bucket's main strengths.

### How the code works

The notes' design refills lazily. It doesn't run a background timer. Instead, every call to `tryAcquire` computes how many tokens have dripped in since the last call, using `System.nanoTime()`. This is a good design: no timer threads, no wasted work when nobody is calling, and the arithmetic is exact. `nanoTime` is monotonic, so clock adjustments can't break it, which is why it's better than wall-clock time for measuring elapsed intervals.

`tryAcquire` takes the lock, refills, and takes a token if one is available. The lock makes the refill and the decrement a single atomic step, so two threads can't spend the same token.

### The correction: wait for the right amount of time

The notes' `acquire()` polls every 10 ms. That approach has three problems. It wastes CPU on repeated checks. It adds up to 10 ms of unnecessary latency. And it isn't fair: a thread that just woke from sleep competes with newly arriving threads, and the newcomers may win. The better approach is to compute how long the next token takes to arrive and sleep for exactly that long:

```java
public void acquire() throws InterruptedException {
 while (true) {
long waitNanos;
 lock.lock();
 try {
 refill();
 if (currentTokens >= 1.0) {
 currentTokens -= 1.0;
 return;
 }
double missing = 1.0 - currentTokens;
 waitNanos = (long) (missing / refillRate * 1_000_000_000L);
 } finally {
 lock.unlock();
 }
 TimeUnit.NANOSECONDS.sleep(waitNanos); // sleep exactly as long as needed
 }
}
```

This version still allows a newcomer to take a token that just appeared, so it isn't perfectly fair. For strict first-come-first-served behavior, use a reservation design, where each caller is assigned a future time slot and sleeps until that slot. Guava's `RateLimiter` uses that approach, and its `acquire()` returns how long the caller slept.

### Comparing rate-limiting algorithms

| Algorithm | How it works | Strength | Weakness |
|---|---|---|---|
| Fixed window | Count requests per clock window | Very simple | A burst at the window boundary can reach double the limit |
| Sliding window log | Store each request timestamp | Exact | High memory per client |
| Sliding window counter | Weighted mix of two windows | Cheap and close to exact | Approximate |
| Token bucket | Tokens refill at a steady rate | Allows bursts, smooth average rate | Burst size must be tuned |
| Leaky bucket | Requests drain at a fixed rate | Very smooth output | Adds queueing delay |

The fixed-window boundary problem is a useful example. If the limit is 100 requests per minute and a client sends 100 requests at 12:00:59 and another 100 at 12:01:00, it has made 200 requests in about two seconds, while staying within both windows.

### Advantages and disadvantages

The token bucket is simple, cheap, and handles bursts well. Its limits are that it lives in one process, so a service with ten instances at 100 requests per second each allows 1,000 per second in total. It also uses a single lock, which becomes a bottleneck under heavy load.

### Distributed rate limiting

To enforce a global limit, store the counter in a shared system. Redis is the usual choice, and a Lua script runs the refill-and-take logic atomically on the server. API gateways such as Envoy, Kong, and NGINX provide rate limiting at the edge. The Generic Cell Rate Algorithm, from ATM networking standards, is a clever in-memory design that stores one timestamp per client and updates it with a single compare-and-swap, avoiding locks altogether.

### Real-world examples

Public APIs return 429 responses with a `Retry-After` header when a client exceeds its quota. Login systems throttle password attempts per account. A service that calls a third-party API with a quota uses an outbound limiter so that it never exceeds that quota, which is cheaper than being blocked. Message producers use limiters to protect a downstream database from spikes.

### Background and current practice

Token bucket and leaky bucket algorithms come from traffic shaping in computer networks, where they were used to smooth bursts of data. Libraries such as Resilience4j and Bucket4j provide ready-made rate limiters for Java. Use a library for production code, and use the lock-based design above to understand the mechanism or for a single-instance service.

## Part 4: A Thread-Safe Cache with Expiration (Q34)

### The simple picture

A refrigerator with dated milk cartons. When you open the fridge and reach for a carton, you check its date before drinking it. That is lazy expiration. But cartons you never reach for would sit there forever, so someone also walks through periodically and throws out the expired ones. That is active cleanup. The cache needs both.

### How the design works

The cache stores each value inside a `CacheEntry` that remembers when it expires. `get` checks the expiry date of the entry it finds and treats an expired entry as a miss. A `ScheduledExecutorService` runs `evictExpired` once a minute to remove the expired entries that nobody reads again. The `ConcurrentHashMap` handles concurrent access to the map itself.

### The race in `get`, explained

Here is how the notes' `get` can lose data:

1. Thread A reads the entry for key `k` and sees it is expired.
2. Thread B writes a fresh value for `k`.
3. Thread A calls `map.remove(k)`, which deletes the fresh value that thread B just wrote.
4. The fix is to remove only the exact entry Thread A saw:

```java
public Optional<V> get(K key) {
CacheEntry<V> entry = map.get(key);
 if (entry == null) return Optional.empty();
 if (entry.isExpired()) {
 map.remove(key, entry); // removes only if still the same entry
 return Optional.empty();
 }
 return Optional.ofNullable(entry.value());
}
```

`ConcurrentHashMap.remove(key, value)` checks and removes atomically. Since the fresh entry is a different object, it survives. The `Optional.ofNullable` change also avoids a `NullPointerException` when a loader legitimately returns `null`.

### Use a monotonic clock

The notes measure expiry with `System.currentTimeMillis()`, which is wall-clock time. If the system clock is adjusted by NTP, an entry can expire early or stay alive too long. For durations, use `System.nanoTime()`, which is monotonic. Store the expiry time as a nanosecond deadline, and compare differences rather than absolute values to avoid overflow issues.

### The cache stampede

The notes' `getOrCompute` is check-then-act. Imagine a popular user profile expires, and 500 requests arrive at once. All 500 miss, and all 500 call `userService.fetchProfile`. That is 500 times the load on the backend, when one call would do. This is a cache stampede, or thundering herd.

The fix is to let the first caller start the load and have everyone else wait for that same result. Store an in-flight future in the map, so late arrivals join it:

```java
private final ConcurrentHashMap<K, CompletableFuture<V>> inFlight = new ConcurrentHashMap<>();

public V getOrCompute(K key, Function<K, V> loader, Duration ttl, Executor ioPool) {
Optional<V> cached = get(key);
 if (cached.isPresent()) return cached.get();

CompletableFuture<V> future = inFlight.computeIfAbsent(key,
 k -> CompletableFuture.supplyAsync(() -> loader.apply(k), ioPool));
 try {
V value = future.join();
 put(key, value, ttl);
 return value;
 } finally {
 inFlight.remove(key, future); // a failed load is retried next time
 }
}
```

This is a sketch of the idea. Caffeine and Guava provide the same protection, with more edge cases handled, and they're what you should use in production.

### Other problems in the notes

The cleanup task is scheduled with `scheduleAtFixedRate`. As covered earlier, if `evictExpired` throws even once, later runs are silently cancelled, so wrap the body in a try/catch that logs. Each cache also creates its own non-daemon scheduler thread, so creating many caches leaks threads and keeps the JVM alive. Share one scheduler, or give the thread a daemon factory. Finally, the cache has no maximum size. Expiry bounds how long entries live, but not how many exist, so a burst of distinct keys can still exhaust memory. Real caches add a size bound with an eviction policy such as LRU, and Caffeine uses a policy called W-TinyLFU.

### Advantages and disadvantages

The design is simple and understandable, and it bounds staleness by time. Its disadvantages are the risks above, plus the difficulty of getting the exact semantics right under concurrency. The extra complexity of a production cache is usually not worth reinventing.

### Real-world examples

A user-profile cache with a ten-minute TTL reduces database load on a busy site. An OAuth token cache stores an access token until shortly before it expires, so the service doesn't ask for a new token on every request. A feature-flag cache refreshes every few seconds. Distributed systems use Redis with a TTL on each key, so every application instance shares the same cache.

### Background and current practice

Time-based expiry has been part of web caching for decades, and HTTP itself has `Cache-Control` and `max-age` headers. Google Guava's `CacheBuilder` made in-process caches with `expireAfterWrite` common in the Java world. Caffeine, written by Ben Manes, is now the standard in-process cache for Java, and Spring's caching abstraction can use it directly. Use Caffeine for in-process caching and Redis for shared caching, and reserve hand-written caches for learning.

## Part 5: Uncaught Exceptions in Threads (Q35)

### The simple picture

Think of a cook who collapses at their station during dinner service. The rest of the kitchen keeps cooking, but the collapsed cook's station is empty, and nobody knows unless someone is told. In Java, a thread that throws an exception that nobody catches dies alone. Its parent thread never hears about it, and the rest of the program carries on.

### What really happens

The exception unwinds the thread's `run()` method and reaches the top of the thread. The JVM then calls the thread's uncaught-exception handler. If the thread has its own handler, that runs. Otherwise, the thread's `ThreadGroup` handles it, which passes it to the default handler if one is set, and otherwise prints the stack trace to standard error with a line beginning `Exception in thread "Thread-0"`. Then the thread terminates. Other threads, and the JVM as a whole, keep running as long as non-daemon threads remain.

So the default isn't silent. But it has three real drawbacks. The message goes to standard error, which many production systems don't capture. The thread is gone and nothing restarts it. And the parent code gets no notification.

### Handling the exception

Attach a handler to each thread that matters, or set a default for the whole application:

```java
Thread worker = new Thread(this::consumeMessages, "order-consumer");
worker.setUncaughtExceptionHandler((thread, ex) -> {
 log.error("Thread {} died", thread.getName(), ex);
 alerts.raise("consumer-died"); // alert, and optionally restart
});

Thread.setDefaultUncaughtExceptionHandler((thread, ex) ->
 log.error("Uncaught in {}", thread.getName(), ex));
```

The handler runs on the dying thread, so it must not throw. It also can't restart the thread, because that thread is finished. Restarting requires a supervisor that creates a new thread.

### The executor trap

Thread pools change the picture, and this is where the most common silent failures happen. The two ways of submitting work behave differently:

```java
ExecutorService executor = Executors.newFixedThreadPool(2);

executor.execute(this::riskyWork); // exception reaches the thread's uncaught handler,
 // the worker dies, and the pool replaces it

Future<?> f = executor.submit(this::riskyWork); // exception is stored in the Future;
 // if nobody calls f.get(), it is silent
```

With `submit`, the exception is wrapped in a `Future`. If the code never calls `get()`, the failure disappears. This is the most common reason that background jobs fail without anyone noticing. As a rule, use `execute` for fire-and-forget work, or always check the `Future`.

### Other silent failures

Several other mechanisms can stop work quietly. A periodic task scheduled with `scheduleAtFixedRate` stops running after an exception, as covered earlier. The old `java.util.Timer` class is worse: its single thread dies on an uncaught exception, and every later `schedule` call throws `IllegalStateException`. That is why `ScheduledExecutorService` replaced it. A `CompletableFuture` stores its exception and does nothing else unless you attach `exceptionally`, `handle`, or `whenComplete`.

### Errors, not just exceptions

The same mechanism applies to `Error` subclasses such as `OutOfMemoryError` and `StackOverflowError`. The thread dies, and the handler runs. But the JVM may be in an unhealthy state, so the right response is often to log, alert, and restart the process, not to keep going.

### The main thread

If the main thread throws, the JVM doesn't exit immediately. It waits for the remaining non-daemon threads to finish, and then exits with status 1. A consumer thread that's still running can keep the process alive after `main` has failed.

### Advantages and disadvantages

Handlers centralize logging and give you a hook for alerting, and the default behavior prevents one thread's failure from taking down the whole process. The disadvantages are that the default is easy to miss, handlers can't restart the thread, and the executor's split between `execute` and `submit` makes failures easy to lose.

### Real-world examples

A message consumer thread hits a null pointer on one malformed message, and the thread dies. The application keeps running, health checks pass, and the queue lag grows silently until customers complain. The fix is to catch exceptions per message inside the loop, log them, and move a poison message to a dead-letter queue. Another common case is a background job submitted with `submit` whose failures never appear in any log. The fix is to read the `Future`, or to switch to `execute` with a handler.

### Other approaches to the same problem

Languages handle this differently, which helps clarify the design choice. In Go, an unrecovered panic in any goroutine crashes the entire program unless it is recovered. In Rust, a panic in a spawned thread stays inside that thread, and `JoinHandle::join` returns an error the spawner can inspect. Erlang and Akka use supervisors: a parent watches its children and restarts them according to a policy, which is the "let it crash" philosophy. Structured concurrency in Java, through `StructuredTaskScope`, propagates a subtask's failure to the code that owns the scope, so failures can't disappear.

### Background and current practice

Java 5 introduced the uncaught-exception handler interfaces, and it's been part of the platform ever since. Virtual threads in Java 21 and later use the same handler mechanism. Modern practice is to wrap each worker's loop in a try/catch that logs and continues, to set a default handler at startup, to use `execute` or check every `Future`, to attach `exceptionally` to every `CompletableFuture` that matters, to avoid `Timer`, and to monitor metrics such as active worker counts, so that a dead worker shows up on a dashboard, not in a customer complaint.

## Putting It Together

These five designs share one lesson. The correct version is usually a combination of small, well-understood pieces: a `volatile` or holder idiom for the singleton, `Condition` objects for the ring buffer, `nanoTime` and a computed wait for the rate limiter, `remove(key, value)` and a shared in-flight future for the cache, and explicit handlers for failure. In interviews, explain the race first, then the mechanism that prevents it, and then the trade-off. In production, reach for the library version, such as the JDK's `ArrayBlockingQueue`, Caffeine, Resilience4j, or Redis, and keep the hand-written version for learning and for cases where the library doesn't fit.

## Quick Summary

| Problem | Core risk | Correct approach | Production choice |
|---|---|---|---|
| Singleton | Two threads create two instances | Double-checked locking with `volatile`, holder idiom, or enum | Dependency injection, or an enum for a true singleton |
| Bounded buffer | Lost wakeups, full or empty misuse | One lock, two `Condition` objects, `while` loops | `ArrayBlockingQueue`, or the Disruptor under heavy load |
| Rate limiter | Overspending tokens, busy polling | Lock-protected refill, computed wait time | Resilience4j, Bucket4j, or Redis for distributed limits |
| Expiring cache | Lost fresh values, stampedes, leaked threads | `remove(key, entry)`, in-flight futures, monotonic clock, daemon scheduler | Caffeine, with Redis for shared caching |
| Uncaught exception | Thread dies, exception disappears | Per-thread and default handlers, `execute` over `submit`, check every `Future` | Monitoring, supervisors, and structured concurrency |
# Interruption, Daemon Threads, Happens-Before, Virtual Threads, Pool Sizing, and Scaling Under Peak Load

This part covers how threads are asked to stop, how they stay consistent with each other, the newest kind of thread, how to size pools, and how a whole Spring Boot service absorbs peak traffic. The first four sections are core Java concepts, the fifth is a calculation, and the last two apply everything to a real system.

Several parts of the notes need correcting before the explanations:

1.**The producer-consumer recap uses `notify()` with several producers and consumers waiting on one monitor.** In that exact code, `notify()` is fragile rather than broken, because a bounded buffer rarely has both kinds of thread waiting at once. But the design is hard to reason about, and `notifyAll()` or separate `Condition` objects are the safe choice. Both loops also swallow `InterruptedException` and never stop.
2.**The daemon examples won't compile as written.** A `Runnable` lambda can't throw `InterruptedException` from `Thread.sleep`. The notes also miss that a thread created by a daemon thread is a daemon too.
3.**The `StructuredTaskScope` example uses the wrong types.** In Java 21, `fork` returns a `StructuredTaskScope.Subtask`, which you read with `get()`, not a `Future`. The API is also still a preview feature.
4.**"Blocking I/O only blocks the virtual thread" needs qualifying.** It holds for most socket and lock operations, but blocking file I/O and `synchronized` blocks (on Java 21 through 23) pin the carrier thread. Creating a million concurrent database calls also overloads the database unless you limit concurrency.
5.**The pool-size example produces 808 threads for a database query.** If the database connection pool has 10 connections, almost all of those threads just wait. The real size is capped by the slowest shared resource.
6.**The Spring `@Async` example doesn't free the request thread on its own.** The request thread is released only if the controller returns before the work finishes, and `@Async` only applies when it's called from another bean on a class that has `@EnableAsync`.

## Part 1: Thread Interruption (Q36)

### The simple picture

Imagine a factory worker who is in the middle of a machine operation. A manager can't just grab the worker and drag them out, because that could leave the machine half-finished. Instead, the manager rings a bell or leaves a note: "please wrap up." That is interruption. It's a request, and the worker decides how to stop safely.

Some workers are resting at their desks (blocked in `sleep` or `wait`). The bell wakes them up immediately, and they get an `InterruptedException`. Other workers are busy with a long calculation, and they must check the note themselves.

### What `interrupt()` really does

Every thread has an interrupt status, which is a boolean flag stored in its `Thread` object. Calling `interrupt()` sets the flag. If the target thread is blocked in an interruptible method, the method wakes up and throws `InterruptedException`, and the flag is cleared at that moment. If the thread is running normally, nothing else happens, and the code must poll the flag with `isInterrupted()`.

Two methods are easy to confuse. `Thread.interrupted()` is static, reads the flag, and clears it. `isInterrupted()` reads the flag without clearing it.

Interruption is cooperative because Java has no safe way to kill a thread from outside. `Thread.stop()` existed for a long time, but it could stop a thread at any point, leaving locks held and objects half-updated. It was deprecated, and recent JDK releases throw `UnsupportedOperationException` when it's called.

### Walking through the notes' example

| Time | Worker thread | Main thread |
|---|---|---|
| 0 s | Starts, checks the flag (clear), begins `sleep(1000)` | Calls `start()`, then `sleep(5000)` |
| 1 s, 2 s, 3 s, 4 s | Does work, sleeps again | Still sleeping |
| 5 s | Still sleeping | Calls `worker.interrupt()`, which sets the flag |
| 5 s | Sleep wakes with `InterruptedException`, flag is cleared | |
| 5 s | Prints the message, restores the flag, breaks out of the loop | |
| 5 s | Prints "Thread exiting gracefully" | |

The restore step is what keeps the design honest. The catch block cleared the flag, so `Thread.currentThread().interrupt()` puts it back for any code above that might check it.

### Why restoring the flag matters

Catching `InterruptedException` clears the flag. If you swallow the exception with an empty catch block, nothing above you learns that the thread was asked to stop. An executor may keep waiting for the task, and a loop may keep running forever. There are two correct responses. You can propagate the exception by declaring `throws InterruptedException` and letting the caller decide. Or, if you can't propagate it, you can restore the flag with `Thread.currentThread().interrupt()`. Empty catch blocks around blocking calls are one of the most common concurrency bugs in real code.

### Which operations respond to interruption

Blocking methods that declare `InterruptedException` respond to interruption. These include `Thread.sleep`, `Object.wait`, `Thread.join`, `BlockingQueue.put` and `take`, `Lock.lockInterruptibly`, `Future.get`, `CountDownLatch.await`, and `Semaphore.acquire`.

Some operations don't respond. Trying to enter a `synchronized` block cannot be interrupted, and a thread waiting there stays `BLOCKED` until the lock is free. Ordinary `java.io` stream reads generally can't be interrupted either. NIO channels are different: they close when interrupted and throw `ClosedByInterruptException`.

CPU-bound loops never wake up, so they must check `isInterrupted()` between units of work.

### Internal working

The interrupt flag lives in the `Thread` object. For `sleep`, the JVM wakes the thread through the operating system's parking mechanism. For `wait` and `join`, the waiter is moved out of its wait set and then throws. The Java Memory Model guarantees that a call to `interrupt()` happens-before the interrupted thread detects the interrupt, so the thread sees everything the interrupter wrote beforehand.

Thread pools handle leftover flags carefully. `ThreadPoolExecutor` clears a stray interrupt before running the next task, unless the pool is shutting down, so one task's leftover flag doesn't affect the next task.

### Advantages and disadvantages

Interruption is safe. It lets threads clean up, release resources in `finally` blocks, and stop at well-defined points. It is also the common mechanism across the library: `shutdownNow`, `Future.cancel(true)`, and timeouts all use it. The disadvantage is that it depends entirely on cooperation. A task that swallows interrupts, runs a non-interruptible call, or does a long CPU loop without checking the flag won't stop when asked.

### Real-world examples

`executor.shutdownNow()` interrupts every worker. A request handler that waits on `future.get(2, TimeUnit.SECONDS)` and then calls `future.cancel(true)` on timeout stops the background work instead of leaving it running. A batch import checks `Thread.currentThread().isInterrupted()` between records, so a shutdown leaves the database in a consistent state. A download manager's Cancel button interrupts the download thread, which closes its sockets.

### Background and current practice

Interruption has been part of Java since the early days of the language. It replaced `Thread.stop` and `suspend`/`resume`, which were unsafe. Structured concurrency uses interruption to cancel subtasks when a sibling fails, and virtual threads honor interrupts like platform threads do.

## Part 2: Daemon and Non-Daemon Threads (Q37)

### The simple picture

A shop has workers and a building janitor. The shop stays open until the workers go home, so the building can't close while any worker is still on shift. The janitor's work is helpful but not essential. When the shop closes, the janitor is sent home immediately, even in the middle of sweeping. Non-daemon threads are the workers, and daemon threads are the janitors.

### The rule that governs shutdown

The JVM exits when no non-daemon threads are still running. The main thread is non-daemon, and so is any thread you create unless you mark it otherwise. When the last non-daemon thread finishes, the JVM exits, even if daemon threads are partway through their work. Daemon threads are not waited for, and they are not unwound, so their `finally` blocks and try-with-resources cleanup may never run.

Calling `System.exit` ends the JVM regardless of what any thread is doing.

### Setting the flag

You must call `setDaemon(true)` before `start()`. Calling it afterward throws `IllegalThreadStateException`. A new thread inherits its daemon status from the thread that creates it. A pool created inside a daemon thread therefore creates daemon workers, which is a common surprise. Here is the notes' cleanup thread, corrected so that it compiles:

```java
Thread cleanupThread = new Thread(() -> {
 while (true) {
 try {
 cleanupTempFiles();
 Thread.sleep(60_000);
 } catch (InterruptedException e) {
 Thread.currentThread().interrupt();
 return;
 }
 }
});
cleanupThread.setDaemon(true);
cleanupThread.start();
```

### Comparison

| Feature | Non-daemon | Daemon |
|---|---|---|
| Effect on JVM exit | JVM waits for it to finish | JVM does not wait for it |
| Default | Yes, for most threads | Must be set explicitly |
| Inherited from creator | Yes, from a non-daemon creator | Yes, from a daemon creator |
| Cleanup on exit | Runs `finally` blocks | No guarantee |
| Typical use | Business logic, request handling | Heartbeats, metrics, cache cleanup |
| Virtual threads | Not applicable | Always daemon; cannot be changed |

### Internal working

The JVM tracks the number of live non-daemon threads. When `main` returns, the launcher waits for that count to reach zero before shutting down. Daemon threads are simply abandoned at process exit. Shutdown hooks registered with `Runtime.addShutdownHook` still run, but the JVM doesn't wait for daemon threads to finish during them.

### Advantages and disadvantages

Daemon threads are convenient for background helpers, since they never prevent the application from exiting. The downside is data loss. A daemon that writes to a file can leave a half-written file. A logging buffer that is never flushed loses its last messages. A metrics reporter that is cut off mid-send loses data. Non-daemon threads avoid those losses, but a single stuck non-daemon thread can keep the process alive forever, which is why many services have a shutdown timeout.

The usual pattern is to make work that must complete a non-daemon thread or an executor that is closed on shutdown, and to make only truly disposable work daemon.

### Real-world examples

The `ForkJoinPool.commonPool()` that backs parallel streams and default `CompletableFuture` tasks uses daemon workers, so a program can exit without waiting for them. A metrics reporter that pushes counters every ten seconds is a good daemon candidate, as long as the final batch is flushed in a shutdown hook. A temp-file cleaner running as a daemon can delete a file while the application is still writing it, which is why cleanup should check file age and state, not just run on a timer. An embedded web server like Tomcat keeps its request threads non-daemon, which is why a Spring Boot application stays alive until it's told to stop.

### Background and current practice

The daemon idea comes from Unix daemons, background services that run without a terminal. Java has had the flag since version 1.0. Virtual threads are always daemon threads, so a virtual thread started from `main` will not keep the JVM alive. If `main` must wait for them, use `join`, a latch, or an executor's `close()`.

## Part 3: The Happens-Before Relationship (Q38)

### The simple picture

Think of a sealed letter. The writer finishes all the pages, then seals the envelope with a wax stamp. Anyone who breaks the stamp is guaranteed to read everything written before the seal. Without a seal, the reader might see some pages but not others, or see them in a different order than they were written.

In Java, the wax stamp is a `volatile` write, an unlock, a thread start, or a thread join. Happens-before is the promise about what a reader is guaranteed to see. It's a rule about visibility and ordering, not about wall-clock time. Two actions can be separated by a millisecond on the clock and still have no happens-before relationship at all.

### Why the rules are necessary

Compilers and processors reorder work for speed. A compiler may move a store to a later point in the method. A processor may keep writes in a store buffer, and cache updates may reach other cores at different times. Without rules, a thread could observe another thread's writes in any order, or never see them at all. The Java Memory Model defines the rules that limit which observations are allowed.

### The rules in plain English

| Rule | What it guarantees |
|---|---|
| Program order | Within one thread, each action is seen as happening before the next one |
| Monitor lock | Unlocking a monitor happens-before every later lock of that monitor |
| Volatile | Writing a volatile variable happens-before every later read of it |
| Thread start | Everything before `t.start()` is visible inside thread `t` |
| Thread join | Everything thread `t` did is visible after `t.join()` returns |
| Interrupt | Calling `interrupt()` happens-before the target detects the interrupt |
| Transitivity | If A happens-before B and B happens-before C, then A happens-before C |

### Walking through the volatile example

The notes' example works like this. Thread A writes `data = 42` (step 1), then writes `ready = true` (step 2). Thread B reads `ready` (step 3) and, if it's true, reads `data` (step 4). Step 1 happens-before step 2 by program order. Step 2 happens-before step 3 because `ready` is volatile. Step 3 happens-before step 4 by program order. By transitivity, step 1 happens-before step 4, so Thread B is guaranteed to see `42`.

The notes say the volatile write "flushes all previous writes." That is a simplification. The JVM doesn't flush anything in a literal sense. It guarantees that the ordering constraint holds, and it inserts memory barriers where needed to make that true.

Without `volatile`, Thread B might see `ready == true` and `data == 0`, because the compiler and processor were free to reorder the writes.

### Internal working

The JIT compiler may not move memory operations across a happens-before edge in ways that break the rules. The processor is also constrained. On x86, a volatile write is followed by a store-load fence, which is the expensive barrier, and ordinary reads already have acquire semantics. On ARM, the JVM uses special load-acquire and store-release instructions. This is why code that looks correct on an x86 laptop can break on an ARM server: ARM reorders memory operations more aggressively, and the bug was always there.

Thread start and join work similarly without any explicit variables. Everything set up in `main` before `start()` is visible to the new thread. Everything the worker computes is visible to `main` after `join()`.

### Real-world examples

The `ConcurrentHashMap` documentation promises that actions before an object is placed in the map happen-before actions that access or remove it in another thread. That is why you can publish a fully built object into a concurrent map and trust readers to see it complete. A common production pattern is to build an immutable configuration object in one thread and publish it through a `volatile` field. Another is the `final` field guarantee: once a constructor completes, a correctly constructed object's final fields are visible to other threads without extra synchronization, as long as `this` doesn't escape during construction.

### Advantages and disadvantages

The model gives a precise contract that lets the compiler and processor optimize aggressively while keeping programs correct. Its disadvantage is that it is subtle. A program with a missing happens-before edge can pass every test, run correctly for months on x86, and fail on ARM hardware or under a different JIT compilation. Tests can show that a bug exists, but they can't prove that one doesn't.

### Background and current practice

The original Java memory model, defined in 1996, was found to be broken in important ways, especially for `volatile` and `final`. The revised model, JSR-133, was adopted with Java 5 in 2004, and its authors were Jeremy Manson, William Pugh, and Sarita Adve. C++11 adopted a similar model. In current Java, `VarHandle` (Java 9) exposes finer access modes, such as acquire and release, for code that needs more control than `volatile` offers.

## Part 4: Virtual Threads (Q39)

### The simple picture

Picture a restaurant. In the platform-thread model, each waiter is assigned one table. When that table waits for the kitchen, the waiter stands there doing nothing, and a restaurant can only afford a few hundred waiters.

In the virtual-thread model, each table is tracked as a ticket in memory, and a small team of waiters serves whichever ticket is ready. When a ticket is waiting for the kitchen, the waiter sets it aside and picks up another. The ticket's notes are filed away and retrieved when the kitchen calls back. The restaurant can handle tens of thousands of tables with a few waiters.

### How virtual threads run

A virtual thread is mounted on a carrier thread, which is an ordinary platform thread from a `ForkJoinPool` sized to the number of cores. While the virtual thread runs CPU work, it occupies its carrier. When it calls a blocking operation that the JDK supports, such as a socket read, a `sleep`, or a `java.util.concurrent` lock, the JVM unmounts it. Its stack frames are saved to the heap as a continuation, and the carrier is free to run another virtual thread. When the operation completes, the virtual thread is rescheduled on any available carrier.

Because the stack lives on the heap, a virtual thread starts small and grows as needed. An idle one costs a few kilobytes at most, far less than the roughly 1 MB reserved for a platform thread's stack.

### Pinning

Some operations keep a virtual thread attached to its carrier, which is called pinning. On Java 21 through 23, blocking inside a `synchronized` block pins the carrier. So does a blocking call made through native code. Blocking file I/O is also a problem, because the JDK compensates by temporarily adding carrier threads, up to a default limit of 256. JEP 491 in JDK 24 removes the `synchronized` pinning problem. Until then, use `ReentrantLock` in code that may block while holding a lock.

### Code

Creating a virtual thread is simple, and the executor form is the usual way to run many tasks:

```java
Thread vt = Thread.ofVirtual().start(() ->
 System.out.println("Running on: " + Thread.currentThread()));

try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
 for (int i = 0; i < 100_000; i++) {
 executor.submit(() -> {
 Thread.sleep(Duration.ofSeconds(1));
 return fetchFromDatabase();
 });
 }
}
```

The notes' million-task loop shows how easily the model is misused. Without a limit, a million calls hit the database at once and overwhelm it. The fix is a `Semaphore` that caps concurrent calls to the database, which is the same pattern covered earlier.

Structured concurrency in Java 21 keeps child tasks inside a scope. Here is the corrected version of the notes' example. It's a preview API and needs `--enable-preview` in Java 21:

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
StructuredTaskScope.Subtask<User> user = scope.fork(() -> fetchUser(id));
StructuredTaskScope.Subtask<List<Order>> orders = scope.fork(() -> fetchOrders(id));
 scope.join();
 scope.throwIfFailed(); // throws ExecutionException if either task failed
 return new Dashboard(user.get(), orders.get());
}
```

### Comparison

| Feature | Platform threads | Virtual threads |
|---|---|---|
| Mapping | One Java thread to one OS thread | Many virtual threads share a few carriers |
| Memory per idle thread | Around 1 MB reserved stack | A few kilobytes, growing on heap |
| Practical count | Thousands | Hundreds of thousands or more |
| Blocking I/O | Blocks the OS thread | Unmounts from the carrier |
| Scheduling | OS scheduler | JVM scheduler on a ForkJoinPool |
| Pooling | Needed, because creation is expensive | Unnecessary, because creation is cheap |
| Best for | CPU-heavy work | I/O-heavy work |

### Advantages and disadvantages

Virtual threads let blocking code scale. You write a simple sequential method, and it can handle many concurrent requests without callback chains or reactive frameworks. Creation is cheap, and there is no pool to size.

The drawbacks are real. CPU-bound work gets no speedup, because it's still limited by the number of cores. Pinning can silently cap concurrency. The model provides no built-in limit, so you must add semaphores in front of shared resources. Each virtual thread can have its own `ThreadLocal` copy, which adds up with millions of threads. Many libraries were written around pooling and `synchronized`, so their behavior may change. And debugging changes too: thread dumps include a huge number of virtual threads, so structured JSON thread dumps and good naming matter more.

### Real-world examples

Spring Boot 3.2 and later can run request handling on virtual threads with `spring.threads.virtual.enabled=true`, so each request gets its own cheap thread, and blocking JDBC calls no longer exhaust a small pool. A service that fans out to twenty downstream APIs per request can call them sequentially in code and let virtual threads run them, instead of building a chain of `CompletableFuture` callbacks. A batch job that processes millions of records can start a virtual thread per record, with a semaphore limiting how many talk to the database at once.

### Background and current practice

The Project Loom effort began in 2017, and its virtual threads were finalized in Java 21 through JEP 444 in 2023. The idea has older roots. Java 1.0 shipped with green threads, which the JVM scheduled itself, but they were replaced by OS threads because the green-thread model couldn't use multiple cores well. Erlang processes, Go goroutines, and Kotlin coroutines are all lightweight tasks multiplexed onto a smaller number of OS threads. In current practice, use virtual threads for I/O-bound work, keep platform or ForkJoin threads for CPU-bound work, limit access to shared resources with semaphores, avoid blocking inside `synchronized` on Java 21 through 23, and avoid pooling virtual threads.

## Part 5: Calculating the Optimal Thread Pool Size (Q40)

### The simple picture

Think of a kitchen. The stoves are the CPU cores, and each cook needs a stove while they're actively cooking. If cooks spend most of their time waiting for deliveries, then you can have more cooks than stoves, because a waiting cook doesn't need a stove. But hiring far more cooks than the kitchen needs causes crowding and fights over pans. In software, the extra cooks show up as context switches, cache misses, and memory use.

The right number depends on how much of each task is computation and how much is waiting.

### CPU-bound tasks

If a task spends nearly all its time computing, a thread beyond the core count only adds switching overhead. The starting point is the number of cores:

```java
int cores = Runtime.getRuntime().availableProcessors();
ExecutorService cpuPool = Executors.newFixedThreadPool(cores);
```

Brian Goetz, in *Java Concurrency in Practice*, suggests adding one extra thread for CPU-bound work, to cover the occasional page fault or pause that would otherwise leave a core idle. `availableProcessors()` is container-aware in modern JDK releases, so a container limited to two CPUs reports two, not the host's total.

### I/O-bound tasks

If a task spends most of its time waiting, you need more threads, so that the cores stay busy while others wait. Let C be the compute time per task, and W the wait time. To keep one core fully busy, you need roughly (C + W) / C threads per core. Goetz's formula expresses this with a target utilization U:

N_threads = N_cpu × U_cpu × (1 + W / C)

The notes' examples are correct arithmetic. With 8 cores, a 200 ms API wait, and 20 ms of compute, you get 8 × (1 + 10) = 88 threads. With a 500 ms database wait and 5 ms of compute, you get 8 × (1 + 100) = 808 threads.

### The hidden ceiling

The 808-thread result shows the most common mistake in pool sizing. Those threads all need a database connection, and HikariCP, the connection pool Spring Boot uses by default, has a maximum of 10 connections. Almost 800 threads would then wait for 10 connections. The extra threads don't increase throughput. They add memory use, GC pressure, and queueing latency.

The real limit is the smallest of several ceilings: the thread pool, the database connection pool, downstream rate limits, and the capacity of the downstream service itself. Sizing the thread pool is only useful after you know the downstream limit.

### Measuring instead of guessing

Little's law, from John Little's 1961 proof, gives a useful cross-check: the average number of requests in flight equals the arrival rate multiplied by the average time each request spends in the system. A service handling 500 requests per second with an average latency of 200 ms has about 100 requests in flight, so it needs roughly 100 concurrent workers.

Watch the actual pool metrics: active thread count, queue length, p99 latency, and CPU utilization. Brendan Gregg's USE method (utilization, saturation, errors) gives a systematic way to find the bottleneck. If CPU is low, the pool is too small or something downstream is slow. If CPU is high, the pool is too large or the work is too heavy.

### Configuration in Spring Boot

```properties
# Tomcat: handles HTTP requests (I/O bound)
server.tomcat.threads.min-spare=20
server.tomcat.threads.max=200
server.tomcat.accept-count=100

# Async task executor: background work
spring.task.execution.pool.core-size=10
spring.task.execution.pool.max-size=50
spring.task.execution.pool.queue-capacity=500
```

With these Tomcat settings, up to 200 requests run at once. Up to 100 more wait in the accept queue. Beyond that, new connections are refused or time out. That's the correct behavior, because failing fast under overload is better than queuing indefinitely.

### Advantages and disadvantages of the formula

The formula is a good starting point, and it makes assumptions visible. Its weakness is that W and C vary from request to request, and they change with load. A database that slows under pressure makes W grow, which makes the formula recommend more threads at the exact moment more threads would make things worse. Treat the formula as a hypothesis to test under realistic load.

### Real-world examples

HikariCP's own guidance is that a small connection pool often outperforms a large one, and it suggests starting from roughly twice the number of cores plus the number of disk spindles. An order service with 16 cores might cap its database pool at 40 connections, and then size its request threads to match, rather than letting them grow to hundreds. A batch enrichment job that calls a rate-limited API should size its pool to the rate limit, not to the formula.

### Background and current practice

Little's law dates to 1961, and the thread-pool formula appeared in Goetz's 2006 book. The C10k problem, described in 1999, asked how a single server could handle ten thousand concurrent connections, and it pushed the industry toward event-driven designs. With virtual threads, pool sizing matters less for I/O-bound code, because the pool is no longer the limit. The question becomes how many concurrent calls the downstream systems can handle, and semaphores enforce that limit.

## Part 6: The Producer-Consumer Recap

The earlier producer-consumer section covered the full design, but the recap's code is worth a short correction. Its `SharedBuffer` uses `synchronized`, `wait`, and `notify`. With exactly one producer and one consumer, the logic works. With several of each, `notify()` can wake a thread of the wrong kind, and the reasoning becomes fragile. Use `notifyAll()`, or separate `Condition` objects as shown in the bounded buffer section. The recap's thread loops also swallow `InterruptedException` and never stop, so they should restore the interrupt flag and exit when they're asked to stop.

The simple version of the pattern is still the right mental model. A producer adds work to a buffer when there's room, a consumer takes work when there is some, and the buffer is the handoff point. In production, use `BlockingQueue`, which implements exactly that logic with correct wakeups, interrupt handling, and bounded capacity.

## Part 7: Scaling a Spring Boot Service Under Peak Load

### The simple picture

Think of a restaurant on a busy Friday night. It has several waiters, which is the thread pool. The kitchen cooks some orders in the background while waiters serve other tables, which is async processing. A waiting list at the door holds guests when the restaurant is full, which is a queue. The host limits how many reservations are accepted, which is rate limiting and backpressure. A second branch opens across town, which is horizontal scaling. And the restaurant depends on its suppliers, which is the database. A restaurant that survives the rush is the one that knows which of these is its real bottleneck.

A single thread never handles peak load. A server handles it by running many requests at once, and each layer of the system contributes a different piece.

### Layer 1: the web server's thread pool

A Spring Boot application using the default servlet stack runs on Tomcat. Each request is handled by a thread taken from a pool, and the pool is bounded. With 200 busy threads, further requests wait in the accept queue, and beyond that they are refused. Tuning the pool is only useful alongside measurements, because more threads don't help if the database is the real limit. Virtual threads, enabled with `spring.threads.virtual.enabled=true` in Spring Boot 3.2 and later, change this layer by giving every request its own cheap thread, so blocking code no longer exhausts a small pool.

### Layer 2: async processing, and what it does and doesn't fix

The notes' `@Async` example is a useful one to examine carefully. `@Async` moves a method's work onto another thread, from a separate executor. It helps only when the caller doesn't need to wait for the result, for example when the controller returns `202 Accepted` right away and the work finishes later. It doesn't reduce the total amount of work, and it doesn't free threads on its own. Two rules often trip people up: `@Async` requires `@EnableAsync` on a configuration class, and calling an `@Async` method from within the same class bypasses the proxy, so the method runs synchronously.

Fully non-blocking servers take a different approach. Spring WebFlux runs on Netty, which uses a small number of event-loop threads to handle a very large number of connections. Its benefit only holds if every step is non-blocking. A single blocking JDBC call on an event loop stalls every connection assigned to that loop. Tomcat versus Netty is therefore a choice about the whole stack, not a setting you can flip for one endpoint.

### Layer 3: queues

For work that doesn't need an immediate answer, put it on a queue. The request handler validates the order, writes it to Kafka or RabbitMQ, and returns `202 Accepted`. Consumers process orders at whatever pace the downstream systems allow. The queue absorbs bursts, and the producer is decoupled from the consumers. In Kafka, the number of parallel consumers in a group is capped by the number of partitions, which is a design decision to make early.

### Layer 4: horizontal scaling

Run several instances of the service behind a load balancer. This works only if the services are stateless, meaning that session data, uploaded files, and local caches are kept out of the instance's memory or shared through an external store. Kubernetes can scale the number of pods automatically with a Horizontal Pod Autoscaler, based on CPU or on custom metrics such as queue lag. A caution: every new pod opens its own database connections, so ten pods with twenty connections each means 200 connections on the database.

### Layer 5: the database

The database is usually the real bottleneck. Connection pooling with HikariCP keeps connection counts bounded. Read replicas handle read-heavy traffic, such as product catalog pages. Redis caches hot data with time-to-live values. Good indexes, batching of writes, and avoiding N+1 query patterns reduce the work per request. A circuit breaker stops calls to a struggling dependency, so that failures don't pile up across the system.

### Layer 6: backpressure and load shedding

Overload is survivable only if the system refuses work it can't finish. Bounded queues prevent memory from filling up. Timeouts stop requests from waiting forever. Returning `429 Too Many Requests` with a `Retry-After` header tells clients when to come back. Bulkheads, which are per-dependency semaphores or thread pools, keep one slow dependency from consuming every thread. And load shedding drops low-priority work first, such as recommendation widgets, so the checkout path keeps working.

### Comparison of the approaches

| Approach | What it solves | What it does not solve |
|---|---|---|
| Thread pool | Bounded concurrency for requests | Slow downstream calls still hold threads |
| Virtual threads | Blocking code scales cheaply | Database and downstream limits still apply |
| `@Async` | Decouples background work from the response | Does not reduce total work; needs proper configuration |
| WebFlux and Netty | Many connections on few threads | Fails if any step blocks |
| Message queues | Absorbs bursts, decouples producers and consumers | Adds latency and needs idempotent consumers |
| Horizontal scaling | Adds capacity across instances | Requires stateless design; multiplies connections |
| Caching and replicas | Reduces database load | Adds staleness and invalidation complexity |
| Backpressure | Keeps overload from causing total failure | Rejects some legitimate requests |

### Real-world example: a Black Friday checkout

An e-commerce platform might use several of these together. An API gateway applies per-client rate limits. The checkout service has a bounded thread pool or virtual threads with a semaphore in front of the payment provider, plus timeouts on every outbound call. Accepted orders go to Kafka, and an inventory service's consumers scale with the topic's partitions. Catalog pages are served from Redis and read replicas. The recommendation widget is shed first when latency rises. The result is a system that slows down gracefully instead of failing all at once.

### Background

The C10k problem, published in 1999, asked how a server could handle ten thousand concurrent connections without a thread for each one. Its answer was event-driven I/O, using mechanisms such as `epoll` on Linux, and servers such as Nginx and Netty built on it. Java's Tomcat has long had non-blocking connectors, and Spring WebFlux arrived in 2017 for reactive stacks. Virtual threads now give Java developers a third option: keep the simple blocking programming model and let the runtime handle the scaling.

### A stronger interview answer

Instead of "a single thread doesn't handle peak load," a strong answer walks through the layers. Requests are handled by a bounded pool or virtual threads. Work that doesn't need a synchronous answer goes to a queue and returns `202 Accepted`. The service scales horizontally, but only if it's stateless. The database and downstream dependencies are the real limits, so connection pools, caches, read replicas, circuit breakers, and timeouts protect them. Overload is handled by rejecting or shedding work at the edge, not by queueing forever. Finally, the team measures everything, because the bottleneck moves as the system changes.

## Putting It Together

These topics form one picture. Interruption and daemon status decide how threads stop. Happens-before decides what threads are allowed to see. Virtual threads change how cheaply blocking code can scale, and pool sizing decides how many threads are worth having. A production service combines all of these with queues, backpressure, and horizontal scaling, and the database usually decides how far it can go.

## Quick Summary

| Concept | Simple meaning | Key takeaway |
|---|---|---|
| Interruption | A "please wrap up" note or bell | Cooperative; always handle or restore the interrupt flag |
| Daemon threads | Janitors who leave when the shop closes | JVM doesn't wait for them, so their cleanup may not run |
| Happens-before | A sealed letter that guarantees what you can read | Visibility needs a rule, not just timing; x86 hides bugs that ARM exposes |
| Virtual threads | Tickets served by a few waiters | Great for I/O-bound code; limit shared resources with semaphores |
| Pool sizing | How many cooks a kitchen can use | Start from the formula, then measure; the downstream limit usually decides |
| Scaling under peak load | A busy restaurant with several safeguards | Pools, queues, stateless scaling, caches, and load shedding, with the database as the key limit |