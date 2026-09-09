Q1. What is a Thread and why do we need Multithreading?
A Thread is the smallest unit of execution within a process. A process can have multiple threads that share the same memory space but execute independently.Why we need multithreading:
Without Multithreading
With Multithreading
One task at a time (sequential)
Multiple tasks simultaneously
UI freezes during long operations
UI stays responsive while background tasks run
Cannot utilize multi-core CPUs
Utilizes all CPU cores
Slow I/O blocks everything
One thread waits for I/O, others continue
Poor user experience
Responsive, fast applications


Real-world examples:
Web Server (Tomcat):     Each HTTP request → separate thread
Video Player:            Thread 1: decode video, Thread 2: play audio, Thread 3: render UI
Banking App:             Thread 1: process transaction, Thread 2: send notification
IDE (IntelliJ):          Thread 1: code editing, Thread 2: compilation, Thread 3: indexing


Q2. What is the difference between Process and Thread?
Feature
Process
Thread
Definition
Independent program in execution
Lightweight sub-task within a process
Memory
Separate memory space
Shared memory space (heap)
Communication
IPC (Inter-Process Communication) — slow
Direct shared memory — fast
Creation cost
Heavy (new memory space)
Light (shares parent's memory)
Crash impact
One process crash doesn't affect others
One thread crash can kill the entire process
Context switch
Expensive (save/restore entire memory map)
Cheaper (save/restore only registers, stack)


Process A                       Process B
┌─────────────────────┐        ┌─────────────────────┐
│  Heap Memory         │        │  Heap Memory         │
│  ┌──────┐ ┌──────┐  │        │  ┌──────┐ ┌──────┐  │
│  │Thread│ │Thread│  │        │  │Thread│ │Thread│  │
│  │  1   │ │  2   │  │        │  │  1   │ │  2   │  │
│  │Stack │ │Stack │  │        │  │Stack │ │Stack │  │
│  └──────┘ └──────┘  │        │  └──────┘ └──────┘  │
│  Shared: heap, static│        │  Shared: heap, static│
│  Private: stack each  │        │  Private: stack each  │
└─────────────────────┘        └─────────────────────┘
↑ Separate memory ↑            ↑ Separate memory ↑


Q3. What are the two ways to create a Thread in Java?
Way 1: Extend Thread class
class MyThread extends Thread {
@Override
public void run() {
System.out.println("Running in: " + Thread.currentThread().getName());
}
}
MyThread t = new MyThread();
t.start(); // creates new thread and calls run()
Way 2: Implement Runnable interface
class MyTask implements Runnable {
@Override
public void run() {
System.out.println("Running in: " + Thread.currentThread().getName());
}
}
Thread t = new Thread(new MyTask());
t.start();
// Or with lambda (Java 8+):
Thread t2 = new Thread(() -> System.out.println("Lambda thread"));
t2.start();
Which is better and why?
Feature
Extends Thread
Implements Runnable
Inheritance
Cannot extend another class
Can extend another class
Separation of concerns
Task + thread mechanics mixed
Task logic separated from threading
Reusability
Task tied to Thread class
The same Runnable can be used with Thread, ExecutorService, etc.
Recommendation
Avoid
Preferred


Way 3: Implement Callable<V> (returns a result)
Callable<Integer> task = () -> {
Thread.sleep(1000);
return 42;
};
ExecutorService executor = Executors.newSingleThreadExecutor();
Future<Integer> future = executor.submit(task);
int result = future.get(); // blocks until result is available → 42
executor.shutdown();
Feature
Runnable
Callable
Method
run() → void
call() → V
Return value
No
Yes
Checked exceptions
Cannot throw
Can throw
Used with
Thread, Executor
Executor only (returns Future)




Q4. What is the difference between start() and run()?
Thread t = new Thread(() -> {
System.out.println("Thread: " + Thread.currentThread().getName());
});
t.run();   // Output: Thread: main         ← runs in CALLER's thread (no new thread!)
t.start(); // Output: Thread: Thread-0     ← creates a NEW thread, then calls run()
Internally:
t.start():
1. JVM allocates a new native OS thread
2. Thread transitions from NEW → RUNNABLE
3. OS scheduler picks it up
4. JVM calls run() on the NEW thread
   t.run():
1. Just a normal method call on the current thread
2. No new thread is created
3. Runs synchronously in the calling thread
   Can you call start() twice?
   Thread t = new Thread(() -> System.out.println("Hello"));
   t.start(); // OK
   t.start(); // IllegalThreadStateException! A thread cannot be restarted.


Q5. Explain the Thread Lifecycle (Thread States).
┌──────────┐
│   NEW     │  Thread object created, not yet started
└────┬─────┘
│ start()
▼
┌──────────┐       ┌─────────────┐
│ RUNNABLE  │◄─────│   BLOCKED    │  Waiting to acquire a lock
│(Ready +   │      └─────────────┘
│ Running)  │      ┌─────────────┐
│           │◄─────│  WAITING     │  wait(), join(), park()
│           │      └─────────────┘
│           │      ┌─────────────┐
│           │◄─────│TIMED_WAITING │  sleep(ms), wait(ms), join(ms)
└────┬─────┘      └─────────────┘
│ run() completes or exception
▼
┌──────────┐
│TERMINATED │  Thread is done
└──────────┘
All 6 states (from Thread.State enum):
State
When
How to enter
NEW
Thread object created
new Thread()
RUNNABLE
Running or ready to run
start(), lock acquired, notify, sleep done
BLOCKED
Waiting for a monitor lock
Trying to enter synchronized block held by another thread
WAITING
Waiting indefinitely
wait(), join(), LockSupport.park()
TIMED_WAITING
Waiting with a timeout
sleep(ms), wait(ms), join(ms)
TERMINATED
Execution completed
run() finishes or uncaught exception


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


PART 2: Synchronization & Thread Safety


Q6. What is a Race Condition? Explain with an example.
A race condition occurs when two or more threads access shared data simultaneously and at least one modifies it, leading to unpredictable results.
class BankAccount {
private int balance = 1000;
public void withdraw(int amount) {
if (balance >= amount) {          // Thread A checks: 1000 >= 800 ✓
// Thread A pauses here...
// Thread B checks: 1000 >= 500 ✓ (balance hasn't changed yet!)
balance -= amount;            // Thread A: balance = 1000 - 800 = 200
// Thread B: balance = 200 - 500 = -300 ← OVERDRAFT! Bug!
}
}
}
// Two threads withdrawing simultaneously
BankAccount account = new BankAccount();
Thread t1 = new Thread(() -> account.withdraw(800));
Thread t2 = new Thread(() -> account.withdraw(500));
t1.start(); t2.start();
// Expected: one succeeds, one fails. Actual: both may succeed → negative balance!
The problem: The check (balance >= amount) and the update (balance -= amount) are not atomic — another thread can sneak in between.
Q7. What is synchronized and how does it work internally?
synchronized ensures that only ONE thread can execute a block of code at a time by acquiring a monitor lock (also called intrinsic lock or mutex).Two forms:
// Form 1: Synchronized method — locks on 'this' object
class BankAccount {
private int balance = 1000;
public synchronized void withdraw(int amount) {
if (balance >= amount) {
balance -= amount;
}
}
public synchronized int getBalance() {
return balance;
}
}
// Form 2: Synchronized block — locks on a specific object
class BankAccount {
private int balance = 1000;
private final Object lock = new Object();
public void withdraw(int amount) {
synchronized (lock) {  // only lock the critical section
if (balance >= amount) {
balance -= amount;
}
}
// code outside synchronized runs concurrently
}
}
// Form 3: Static synchronized — locks on the Class object
class Counter {
private static int count = 0;
public static synchronized void increment() {
count++; // lock on Counter.class
}
}
Internal working — Monitor (Object Header):Every Java object has an object header containing a mark word that stores lock information.
Object Header (64-bit JVM):
┌────────────────────────────────────────────────────┐
│ Mark Word (64 bits)                                │
│  ┌─────────────────────────────────────────────┐   │
│  │ Biased Lock / Thin Lock / Fat Lock info     │   │
│  │ + HashCode + GC age                         │   │
│  └─────────────────────────────────────────────┘   │
│ Class Pointer (32/64 bits)                         │
└────────────────────────────────────────────────────┘
Lock escalation (how JVM optimizes):
1. No contention       → Biased Locking (almost zero cost — just stores thread ID)
2. Light contention    → Thin Lock / Lightweight Lock (CAS spin)
3. Heavy contention    → Fat Lock / Heavyweight Lock (OS mutex, thread blocking)
   Biased Lock → Thin Lock → Fat Lock
   (fast)        (medium)    (slow, involves OS context switch)
   Thread A enters synchronized:
1. Check mark word: no lock → set biased to Thread A (almost free)
2. Next time Thread A enters: same bias → no operation needed
3. Thread B tries to enter: revoke bias → upgrade to thin lock (CAS spin)
4. If CAS fails repeatedly: upgrade to fat lock (OS mutex)
   Thread B enters synchronized while A holds the lock:
1. Thread B → BLOCKED state
2. Context switch to OS → thread parked
3. When A exits: notify OS → wake up B → B acquires lock


Q8. What is the difference between synchronized method and synchronized block?
// Synchronized method — locks ENTIRE method on 'this'
public synchronized void transfer(Account to, int amount) {
this.balance -= amount;  // ← locked
to.balance += amount;    // ← still locked (unnecessary if 'to' has its own sync)
logTransaction();        // ← still locked (unnecessary!)
}
// Synchronized block — lock ONLY what needs locking
public void transfer(Account to, int amount) {
synchronized (this) {
this.balance -= amount;  // ← locked (only the critical section)
}
synchronized (to) {
to.balance += amount;    // ← locked on different object
}
logTransaction();            // ← NOT locked (runs freely)
}
Feature
Synchronized Method
Synchronized Block
Lock object
this (instance) or Class (static)
Any object you specify
Granularity
Entire method
Only critical section
Flexibility
Low
High (different locks for different data)
Performance
May lock too much
Better — fine-grained locking


Best practice: Prefer synchronized blocks with fine-grained locking. Lock only what you must, for as little time as possible.
Q9. What is a Deadlock? How to detect and prevent it?
Deadlock = Two or more threads are waiting for each other to release locks, and none can proceed.
// DEADLOCK SCENARIO
Object lockA = new Object();
Object lockB = new Object();
// Thread 1: locks A, then tries to lock B
Thread t1 = new Thread(() -> {
synchronized (lockA) {
System.out.println("T1: holding lockA, waiting for lockB...");
try { Thread.sleep(100); } catch (InterruptedException e) {}
synchronized (lockB) { // BLOCKED! T2 holds lockB
System.out.println("T1: holding both locks");
}
}
});
// Thread 2: locks B, then tries to lock A
Thread t2 = new Thread(() -> {
synchronized (lockB) {
System.out.println("T2: holding lockB, waiting for lockA...");
try { Thread.sleep(100); } catch (InterruptedException e) {}
synchronized (lockA) { // BLOCKED! T1 holds lockA
System.out.println("T2: holding both locks");
}
}
});
t1.start(); t2.start();
// DEADLOCK! Both threads block forever.
Four conditions for deadlock (ALL must be true):
Mutual Exclusion — Resources are non-shareable
Hold and Wait — Thread holds one lock and waits for another
No Preemption — Locks can't be forcibly taken
Circular Wait — T1→T2→T1 (circular dependency)
Prevention strategies:
// Strategy 1: Lock ordering — always acquire locks in the same order
// INSTEAD of Thread1(A→B) and Thread2(B→A), ALWAYS acquire A before B
synchronized (lockA) {
synchronized (lockB) {
// both threads use the same order → no circular wait
}
}
// Strategy 2: tryLock with timeout (using ReentrantLock)
ReentrantLock lock1 = new ReentrantLock();
ReentrantLock lock2 = new ReentrantLock();
boolean acquired = false;
try {
acquired = lock1.tryLock(1, TimeUnit.SECONDS)
&& lock2.tryLock(1, TimeUnit.SECONDS);
if (acquired) {
// do work
} else {
// back off, retry later
}
} finally {
if (lock2.isHeldByCurrentThread()) lock2.unlock();
if (lock1.isHeldByCurrentThread()) lock1.unlock();
}
// Strategy 3: Use a single lock for related resources
private final Object transferLock = new Object();
synchronized (transferLock) {
accountA.debit(amount);
accountB.credit(amount);
}
Detection with jstack or programmatically:
ThreadMXBean bean = ManagementFactory.getThreadMXBean();
long[] deadlockedThreads = bean.findDeadlockedThreads();
if (deadlockedThreads != null) {
ThreadInfo[] infos = bean.getThreadInfo(deadlockedThreads, true, true);
for (ThreadInfo info : infos) {
System.out.println("Deadlocked thread: " + info.getThreadName());
System.out.println("Waiting for lock: " + info.getLockInfo());
System.out.println("Held locks: " + Arrays.toString(info.getLockedSynchronizers()));
}
}


Q10. What is a Livelock and Starvation?
Livelock — Threads keep responding to each other without making progress (like two people in a hallway stepping aside in the same direction).
// Livelock example — both threads keep yielding
class Worker {
private boolean active = true;
public void work(Worker other) {
while (active) {
if (other.active) {
System.out.println(Thread.currentThread().getName() + ": I'll wait, you go first");
Thread.yield(); // politely yields — but the other does the same!
continue;
}
// do work...
active = false;
}
}
}
// Both threads keep saying "you go first" → no progress!
Starvation — A thread never gets CPU time because higher-priority threads keep running.
// Starvation: low priority thread never runs
Thread highPriority = new Thread(task);
highPriority.setPriority(Thread.MAX_PRIORITY); // 10
Thread lowPriority = new Thread(task);
lowPriority.setPriority(Thread.MIN_PRIORITY);  // 1
// lowPriority may NEVER run if highPriority keeps the CPU busy
// Another cause: unfair lock
// synchronized uses an UNFAIR lock — the thread that just released
// can immediately re-acquire, starving waiting threads
// Solution: Use fair lock
ReentrantLock fairLock = new ReentrantLock(true); // fair = true
// Threads are served in FIFO order (but ~10% slower)


Q11. What is the volatile keyword and how does it work internally?
volatile guarantees visibility and ordering but NOT atomicity.Problem without volatile:
class StopFlag {
boolean running = true; // not volatile
void stop() { running = false; }
void run() {
while (running) {
// Thread may NEVER see running = false!
// JVM/CPU can cache 'running' in a register and never re-read from main memory
}
}
}
With volatile:
class StopFlag {
volatile boolean running = true;
void stop() { running = false; }  // write goes to MAIN MEMORY
void run() {
while (running) {             // read comes from MAIN MEMORY
// guaranteed to see the updated value
}
}
}
How volatile works internally (Java Memory Model):
Without volatile:
Thread 1 CPU Cache: running = true (cached, never refreshed)
Thread 2 CPU Cache: running = false (wrote here)
Main Memory: running = true (Thread 2's write may be buffered)
With volatile:
WRITE: Thread 2 writes → flushes to main memory immediately
READ:  Thread 1 reads → always reads from main memory (bypasses cache)
Also: establishes a happens-before relationship
What volatile does NOT do — NOT atomic:
volatile int counter = 0;
// THIS IS STILL NOT THREAD-SAFE!
counter++; // this is actually: read → increment → write (3 operations, not atomic)
// Thread A: reads 5
// Thread B: reads 5
// Thread A: writes 6
// Thread B: writes 6 (lost update!)
// For atomic increment, use AtomicInteger or synchronized
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet(); // truly atomic
When to use volatile:
Use volatile when
Don't use volatile when
Simple flag (stop signal)
Counter (increment/decrement)
One thread writes, others only read
Multiple threads write
Published immutable reference
Compound check-then-act
Double-checked locking singleton
Any read-modify-write operation




PART 3: Thread Communication


Q12. Explain wait(), notify(), and notifyAll() with their internal working.
These methods enable inter-thread communication — one thread waits for a condition, another signals when the condition is met.They must be called from within a synchronized block (on the same object used as the lock).
class MessageQueue {
private final Queue<String> queue = new LinkedList<>();
private final int capacity;
public MessageQueue(int capacity) {
this.capacity = capacity;
}
public synchronized void put(String message) throws InterruptedException {
while (queue.size() == capacity) {
wait(); // release lock, go to WAITING state, wait for notification
}
queue.add(message);
System.out.println("Produced: " + message);
notifyAll(); // wake up all waiting threads (consumers)
}
public synchronized String take() throws InterruptedException {
while (queue.isEmpty()) {
wait(); // release lock, go to WAITING state, wait for notification
}
String message = queue.poll();
System.out.println("Consumed: " + message);
notifyAll(); // wake up all waiting threads (producers)
return message;
}
}
Internal working:
Each object has:
1. Monitor Lock (mutex) — only one thread can hold it
2. Wait Set — set of threads that called wait() on this object
   Thread A calls wait():
1. Releases the monitor lock
2. A is added to the object's wait set
3. A goes to WAITING state (parked by OS)
   Thread B calls notify():
1. ONE random thread is removed from the wait set
2. That thread moves to BLOCKED state (needs to re-acquire the lock)
3. When B exits synchronized, the notified thread can acquire the lock
   Thread B calls notifyAll():
1. ALL threads are removed from the wait set
2. All move to BLOCKED state, competing for the lock
3. One wins, others stay BLOCKED
   Why use while and not if with wait()?
   // BAD — spurious wakeups can cause bugs
   if (queue.isEmpty()) {
   wait(); // wakes up, but queue might still be empty! (spurious wakeup)
   }
   String msg = queue.poll(); // NullPointerException!
   // GOOD — always re-check the condition
   while (queue.isEmpty()) {
   wait(); // even if spuriously woken, the while loop re-checks
   }
   String msg = queue.poll(); // safe
   Why must wait/notify be in synchronized?
   // Without synchronized, you get IllegalMonitorStateException
   obj.wait();   // throws IllegalMonitorStateException!
   obj.notify(); // throws IllegalMonitorStateException!
   // The thread MUST own the monitor (lock) to call wait/notify
   synchronized (obj) {
   obj.wait();   // OK — releases the lock and waits
   obj.notify(); // OK — signals a waiting thread
   }


Q13. What is the difference between notify() and notifyAll()?
Feature
notify()
notifyAll()
Wakes up
One random waiting thread
All waiting threads
Risk
If wrong thread is woken → others wait forever
No missed signals
Performance
Slightly faster (less context switching)
Slower but safer
Use when
Only one type of waiter (homogeneous)
Multiple types of waiters (producers AND consumers)


// PROBLEM with notify() in producer-consumer:
// Producers and consumers both wait on the same object
// notify() might wake up another PRODUCER instead of a CONSUMER
// → The consumer message is never delivered!
// SOLUTION: Use notifyAll() to wake ALL threads
// Each thread re-checks its condition in the while loop
// Only the appropriate thread proceeds
Best practice: Almost always use notifyAll(). Use notify() only when all waiting threads are equivalent and interchangeable.
Q14. What are join() and sleep()?
join() — Wait for another thread to finish.
Thread t1 = new Thread(() -> {
System.out.println("T1: downloading file...");
try { Thread.sleep(3000); } catch (InterruptedException e) {}
System.out.println("T1: download complete");
});
t1.start();
t1.join(); // main thread BLOCKS until t1 finishes
System.out.println("Main: processing downloaded file"); // runs AFTER t1 is done
// With timeout:
t1.join(5000); // wait at most 5 seconds
if (t1.isAlive()) {
System.out.println("T1 still running after 5 seconds!");
}
sleep() — Pause the current thread for a specified duration.
Thread.sleep(1000); // pause for 1 second
// Thread goes to TIMED_WAITING state
// Does NOT release any locks!
Feature
sleep(ms)
wait(ms)
Class
Thread.sleep() (static)
Object.wait() (instance)
Lock
Does NOT release
Releases the lock
Purpose
Pause execution
Wait for a condition/signal
Wake-up
After timeout
notify()/notifyAll() or timeout
Requires sync
No
Yes (must be in synchronized)




Q15. What is ThreadLocal and when would you use it?
ThreadLocal provides per-thread variable storage — each thread gets its own independent copy of the variable.
// Each thread gets its own SimpleDateFormat (which is NOT thread-safe)
private static final ThreadLocal<SimpleDateFormat> dateFormat =
ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
public String formatDate(Date date) {
return dateFormat.get().format(date); // each thread uses its own instance
}
Internal working:
Thread object:
┌─────────────────────────────┐
│ Thread                       │
│   threadLocalMap:            │
│     ThreadLocal_1 → "value1" │
│     ThreadLocal_2 → 42       │
│     ThreadLocal_3 → [user]   │
└─────────────────────────────┘
Each Thread has a ThreadLocalMap (a hash map):
Key = ThreadLocal object (weak reference)
Value = the thread-specific value
Real-world scenarios:
// 1. User context in web applications (per-request data)
public class UserContext {
private static final ThreadLocal<User> currentUser = new ThreadLocal<>();
public static void set(User user) { currentUser.set(user); }
public static User get() { return currentUser.get(); }
public static void clear() { currentUser.remove(); } // IMPORTANT!
}
// In a servlet filter:
public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
try {
User user = authenticateUser(req);
UserContext.set(user);
chain.doFilter(req, res);
} finally {
UserContext.clear(); // prevent memory leaks in thread pools!
}
}
// Anywhere in the request processing:
User user = UserContext.get(); // no need to pass User through every method
// 2. Transaction management
public class TransactionContext {
private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
public static Connection getConnection() {
Connection conn = connectionHolder.get();
if (conn == null) {
conn = dataSource.getConnection();
connectionHolder.set(conn);
}
return conn;
}
}
Memory leak warning with thread pools:
// In a thread pool, threads are REUSED
// If you set a ThreadLocal but never remove it:
// 1. The thread lives on (it's pooled)
// 2. The ThreadLocal value is never garbage collected
// 3. MEMORY LEAK!
// ALWAYS clean up in a finally block:
try {
threadLocal.set(value);
// do work
} finally {
threadLocal.remove(); // CRITICAL for thread pools
}


PART 4: Thread Pools & ExecutorService


Q16. What is a Thread Pool and why should you use it?
Without a pool — creating threads manually:
// BAD: Creating a new thread for every request
for (int i = 0; i < 10_000; i++) {
new Thread(() -> handleRequest()).start(); // 10,000 threads!
// OS thread creation: ~1MB stack per thread = ~10GB memory
// Thread creation overhead: ~1ms each
// Context switching: catastrophic with 10,000 threads
}
With a pool — reusing threads:
// GOOD: Fixed pool of reusable threads
ExecutorService pool = Executors.newFixedThreadPool(100);
for (int i = 0; i < 10_000; i++) {
pool.submit(() -> handleRequest()); // 10,000 tasks, but only 100 threads
// Tasks queue up and are processed by available threads
}
pool.shutdown();
Benefits:
Reuse — No thread creation/destruction overhead per task
Bounded — Limit resource consumption (prevent OOM)
Managed — Queue tasks, handle rejection, monitor pool health
Tunable — Adjust pool size based on workload


Q17. Explain all the types of ExecutorService and their internal working.
1. FixedThreadPool — Fixed number of threads
   ExecutorService pool = Executors.newFixedThreadPool(10);
   // Internally: new ThreadPoolExecutor(10, 10, 0L, MILLISECONDS, new LinkedBlockingQueue<>())
   // Core threads: 10, Max threads: 10, Queue: unbounded
   // If all 10 threads busy → tasks wait in queue (queue can grow infinitely → OOM risk!)
2. CachedThreadPool — Elastic pool (creates threads as needed)
   ExecutorService pool = Executors.newCachedThreadPool();
   // Internally: new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, SECONDS, new SynchronousQueue<>())
   // Core threads: 0, Max threads: unlimited, Idle timeout: 60 seconds
   // No queue! Each task needs a thread immediately
   // Great for short-lived tasks, DANGEROUS for long-running tasks (unbounded threads → OOM!)
3. SingleThreadExecutor — Exactly one thread
   ExecutorService pool = Executors.newSingleThreadExecutor();
   // Internally: new ThreadPoolExecutor(1, 1, 0L, MILLISECONDS, new LinkedBlockingQueue<>())
   // Guarantees tasks are executed sequentially in submission order
   // Use case: sequential processing, file writing, audit logging
4. ScheduledThreadPool — For delayed/periodic tasks
   ScheduledExecutorService pool = Executors.newScheduledThreadPool(5);
   // Run after 3 seconds delay
   pool.schedule(() -> System.out.println("Delayed"), 3, TimeUnit.SECONDS);
   // Run every 5 seconds (fixed rate)
   pool.scheduleAtFixedRate(() -> System.out.println("Periodic"), 0, 5, TimeUnit.SECONDS);
   // Run 5 seconds after PREVIOUS task finishes (fixed delay)
   pool.scheduleWithFixedDelay(() -> System.out.println("Delayed repeat"), 0, 5, TimeUnit.SECONDS);
5. WorkStealingPool (Java 8) — Fork/Join based
   ExecutorService pool = Executors.newWorkStealingPool();
   // Uses ForkJoinPool internally
   // Threads: number of available CPU cores
   // Each thread has its own deque; idle threads STEAL work from busy threads
   // Best for CPU-intensive, recursive tasks


Q18. How does ThreadPoolExecutor work internally?
// Full constructor — you should use this instead of Executors factory methods
ThreadPoolExecutor executor = new ThreadPoolExecutor(
5,                      // corePoolSize: always keep 5 threads alive
20,                     // maximumPoolSize: can grow up to 20
60L,                    // keepAliveTime: idle non-core threads die after 60 sec
TimeUnit.SECONDS,
new ArrayBlockingQueue<>(100),  // workQueue: holds 100 waiting tasks
new ThreadPoolExecutor.CallerRunsPolicy()  // rejectionHandler
);
Task submission flow:
submit(task)
│
▼
Active threads < corePoolSize?  ──YES──→  Create new core thread, run task
│
NO
▼
Queue has space?  ──YES──→  Add task to queue
│
NO (queue full)
▼
Active threads < maximumPoolSize?  ──YES──→  Create new non-core thread, run task
│
NO (pool is full AND queue is full)
▼
Execute RejectionPolicy
Visual:
┌─────────────────────────┐
submit(task) ──→  │    Core Threads (5)      │ ← always alive
│  T1  T2  T3  T4  T5     │
└─────────┬───────────────┘
│ if all busy
┌─────────▼───────────────┐
│     Work Queue (100)     │ ← tasks wait here
│  [task6, task7, ...]     │
└─────────┬───────────────┘
│ if queue full
┌─────────▼───────────────┐
│   Non-Core Threads (15)  │ ← created on demand, die when idle
│  T6  T7 ... T20          │
└─────────┬───────────────┘
│ if max reached + queue full
┌─────────▼───────────────┐
│    Rejection Policy      │
└─────────────────────────┘
Rejection policies (when pool and queue are both full):
// 1. AbortPolicy (default) — throws RejectedExecutionException
new ThreadPoolExecutor.AbortPolicy()
// 2. CallerRunsPolicy — the submitting thread runs the task itself
new ThreadPoolExecutor.CallerRunsPolicy()
// Slows down the producer, providing natural back-pressure
// 3. DiscardPolicy — silently drops the task
new ThreadPoolExecutor.DiscardPolicy()
// 4. DiscardOldestPolicy — drops the oldest queued task, retries
new ThreadPoolExecutor.DiscardOldestPolicy()


Q19. How to properly shut down an ExecutorService?
ExecutorService executor = Executors.newFixedThreadPool(10);
// Submit tasks...
executor.submit(task1);
executor.submit(task2);
// Graceful shutdown
executor.shutdown(); // stops accepting new tasks, finishes existing ones
try {
if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
// Tasks didn't finish in 30 seconds
executor.shutdownNow(); // interrupts all running tasks
if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
System.err.println("Pool did not terminate");
}
}
} catch (InterruptedException e) {
executor.shutdownNow();
Thread.currentThread().interrupt(); // preserve interrupt status
}
Method
Behavior
shutdown()
No new tasks accepted; in-progress and queued tasks finish
shutdownNow()
Interrupts running tasks, removes queued tasks, returns unexecuted list
awaitTermination(timeout)
Blocks until all tasks complete or timeout
isShutdown()
Returns true after shutdown() called
isTerminated()
Returns true after all tasks completed post-shutdown




PART 5: Locks & Concurrency Utilities


Q20. What is the difference between synchronized and ReentrantLock?
Feature
synchronized
ReentrantLock
Lock acquisition
Automatic (enter block)
Manual (lock())
Lock release
Automatic (exit block)
Manual (unlock() in finally!)
Try without blocking
No
tryLock()
Try with timeout
No
tryLock(timeout)
Interruptible waiting
No
lockInterruptibly()
Fairness
Unfair only
Configurable (new ReentrantLock(true))
Condition variables
One per object (wait/notify)
Multiple (newCondition())
Read-write split
No
Use ReentrantReadWriteLock


ReentrantLock lock = new ReentrantLock();
public void transfer(Account from, Account to, int amount) {
lock.lock();
try {
from.debit(amount);
to.credit(amount);
} finally {
lock.unlock(); // ALWAYS unlock in finally!
}
}
// tryLock — non-blocking attempt
public boolean tryTransfer(Account from, Account to, int amount) {
if (lock.tryLock()) {
try {
from.debit(amount);
to.credit(amount);
return true;
} finally {
lock.unlock();
}
}
return false; // couldn't acquire lock — try later
}
// tryLock with timeout — avoids deadlock
public boolean transferWithTimeout(Account from, Account to, int amount)
throws InterruptedException {
if (lock.tryLock(2, TimeUnit.SECONDS)) {
try {
from.debit(amount);
to.credit(amount);
return true;
} finally {
lock.unlock();
}
}
throw new TimeoutException("Could not acquire lock in 2 seconds");
}


Q21. What is ReadWriteLock and when should you use it?
Problem: synchronized and ReentrantLock block ALL access — even reads block other reads. But if data is mostly read and rarely written, allowing concurrent reads is safe and much faster.
// With synchronized — reads block each other (unnecessary!)
// 100 threads reading simultaneously → only 1 can read at a time → bottleneck!
// With ReadWriteLock — multiple readers can read simultaneously
ReadWriteLock rwLock = new ReentrantReadWriteLock();
Lock readLock = rwLock.readLock();
Lock writeLock = rwLock.writeLock();
Map<String, String> cache = new HashMap<>();
// Multiple threads can read simultaneously (no blocking between readers)
public String read(String key) {
readLock.lock();
try {
return cache.get(key);
} finally {
readLock.unlock();
}
}
// Only ONE thread can write (blocks all readers and other writers)
public void write(String key, String value) {
writeLock.lock();
try {
cache.put(key, value);
} finally {
writeLock.unlock();
}
}
Concurrency rules:
Read Lock:   Multiple readers at the same time ✓
Reader while writer holds lock ✗ (blocks)
Write Lock:  Only one writer at a time ✓
Writer while readers hold lock ✗ (blocks)
Scenario
synchronized
ReadWriteLock
100 concurrent reads
One at a time
All 100 simultaneously
Read during write
Blocked
Blocked
Write during reads
Blocked
Blocked
Read:Write = 95:5
Terrible
Excellent


StampedLock (Java 8) — even faster for read-heavy workloads:
StampedLock lock = new StampedLock();
// Optimistic read — doesn't acquire any lock (ultra-fast)
public double readCoordinates() {
long stamp = lock.tryOptimisticRead(); // no blocking, no locking!
double x = this.x;
double y = this.y;
if (!lock.validate(stamp)) {
// a write happened while we were reading — fall back to read lock
stamp = lock.readLock();
try {
x = this.x;
y = this.y;
} finally {
lock.unlockRead(stamp);
}
}
return Math.sqrt(x * x + y * y);
}


Q22. What are CountDownLatch, CyclicBarrier, and Semaphore?
CountDownLatch — Wait for N events to complete before proceeding. One-time use.
// Real-world: application startup — wait for all services to initialize
CountDownLatch latch = new CountDownLatch(3); // count = 3
// Each service counts down when ready
executor.submit(() -> { initDatabase();    latch.countDown(); }); // 3→2
executor.submit(() -> { initCache();       latch.countDown(); }); // 2→1
executor.submit(() -> { initMessageQueue(); latch.countDown(); }); // 1→0
latch.await(); // main thread blocks until count reaches 0
System.out.println("All services initialized. Starting application...");
// With timeout:
if (!latch.await(30, TimeUnit.SECONDS)) {
throw new StartupException("Services failed to initialize in 30 seconds");
}
CyclicBarrier — N threads wait for each other at a barrier point. Reusable.
// Real-world: parallel computation in phases
// 4 threads process a matrix, then synchronize after each phase
CyclicBarrier barrier = new CyclicBarrier(4, () -> {
System.out.println("All threads reached the barrier — merging results");
});
for (int i = 0; i < 4; i++) {
final int threadId = i;
executor.submit(() -> {
for (int phase = 0; phase < 3; phase++) {
processPhase(threadId, phase);
barrier.await(); // wait for all 4 threads to finish this phase
// barrier resets automatically for the next phase!
}
});
}
Feature
CountDownLatch
CyclicBarrier
Reusable
No (one-time)
Yes (resets after each use)
Who waits
One thread waits for N events
N threads wait for each other
Action on complete
None
Optional Runnable
Decrement
Any thread can countDown()
Only participating threads await()


Semaphore — Limits concurrent access to a resource. Like a parking lot with N spots.
// Real-world: database connection pool with max 10 connections
Semaphore semaphore = new Semaphore(10); // 10 permits
public Connection getConnection() throws InterruptedException {
semaphore.acquire(); // blocks if 10 connections already in use
try {
return pool.borrowConnection();
} catch (Exception e) {
semaphore.release(); // release permit if borrowing fails
throw e;
}
}
public void releaseConnection(Connection conn) {
pool.returnConnection(conn);
semaphore.release(); // return permit
}
// tryAcquire — non-blocking
if (semaphore.tryAcquire(5, TimeUnit.SECONDS)) {
try {
// use resource
} finally {
semaphore.release();
}
} else {
throw new ResourceUnavailableException("No connection available");
}


Q23. What is Phaser? How is it different from CyclicBarrier?
Phaser (Java 7) is a more flexible version of CyclicBarrier that supports:
Dynamic party registration/deregistration
Multiple phases
Phase-specific actions
Phaser phaser = new Phaser(1); // register self (the main thread)
for (int i = 0; i < 3; i++) {
phaser.register(); // dynamically add participants
executor.submit(() -> {
// Phase 0: download data
downloadData();
phaser.arriveAndAwaitAdvance(); // sync at end of phase 0
// Phase 1: process data
processData();
phaser.arriveAndAwaitAdvance(); // sync at end of phase 1
// Phase 2: upload results
uploadResults();
phaser.arriveAndDeregister(); // done — remove self from phaser
});
}
// Main thread waits for phase 0 to complete, then deregisters
phaser.arriveAndAwaitAdvance(); // wait for phase 0
phaser.arriveAndDeregister();   // main thread exits


PART 6: Atomic Classes & CAS


Q24. What are Atomic classes and how does CAS (Compare-And-Swap) work internally?
Atomic classes provide lock-free, thread-safe operations on single variables using CPU-level atomic instructions.
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();   // atomic: read + increment + write
counter.decrementAndGet();
counter.addAndGet(5);
counter.compareAndSet(5, 10); // set to 10 only if current value is 5
counter.getAndUpdate(v -> v * 2); // atomic read-modify-write with lambda
counter.updateAndGet(v -> v * 2);
counter.accumulateAndGet(3, Integer::sum);
CAS (Compare-And-Swap) — the internal mechanism:
CAS(memory_location, expected_value, new_value):
ATOMICALLY (at CPU hardware level):
if (memory[location] == expected_value) {
memory[location] = new_value;
return true;  // success
} else {
return false; // someone else changed it — retry
}
How incrementAndGet() works internally:
// Simplified internal implementation
public int incrementAndGet() {
while (true) {
int current = get();           // read current value
int next = current + 1;        // compute new value
if (compareAndSet(current, next)) { // CAS: set if unchanged
return next;               // success!
}
// CAS failed — another thread changed it. Retry the loop (spin).
}
}
CAS vs Locking:
Feature
CAS (Atomic)
Lock (synchronized)
Blocking
Non-blocking (spin)
Blocking (thread sleeps)
Overhead
Low (no context switch)
High (OS mutex, context switch)
Contention
Good for low-medium
Good for high contention
Starvation
Possible (spin loop)
Unlikely with fair lock
Complexity
Simple single-variable ops
Complex multi-variable ops


ABA problem:
Thread 1: reads value A
Thread 2: changes A → B → A
Thread 1: CAS succeeds (value is still A) — but it CHANGED in between!
Solution: AtomicStampedReference — includes a version stamp
AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 0);
int[] stampHolder = new int[1];
String current = ref.get(stampHolder); // value="A", stamp=0
ref.compareAndSet("A", "B", 0, 1);    // check both value AND stamp
All atomic classes:
AtomicInteger, AtomicLong, AtomicBoolean    // primitives
AtomicReference<V>                           // object reference
AtomicIntegerArray, AtomicLongArray          // arrays
AtomicStampedReference<V>                    // solves ABA problem
AtomicMarkableReference<V>                   // reference + boolean mark
LongAdder, DoubleAdder                       // high-throughput counters (Java 8)
LongAccumulator, DoubleAccumulator           // generalized accumulation (Java 8)


Q25. What is LongAdder and why is it faster than AtomicLong?
Problem with AtomicLong: Under high contention, many threads spin on the same memory location, causing cache-line contention.LongAdder solution: Distributes updates across multiple cells, then sums them when the value is read.
AtomicLong (single counter):
Thread1 → CAS(counter) → retry → retry → success
Thread2 → CAS(counter) → retry → retry → retry → success
Thread3 → CAS(counter) → retry → success
(all 3 fight over the same memory location)
LongAdder (striped cells):
Thread1 → CAS(cell[0]) → success          (no contention!)
Thread2 → CAS(cell[1]) → success          (different cell!)
Thread3 → CAS(cell[2]) → success          (different cell!)
sum() = cell[0] + cell[1] + cell[2]       (only computed on read)
LongAdder counter = new LongAdder();
// From multiple threads (fast — no contention)
counter.increment();
counter.add(5);
// Read the total (slightly slower — must sum all cells)
long total = counter.sum();
Feature
AtomicLong
LongAdder
Write performance (high contention)
Moderate (CAS spin)
Much faster
Read performance
Fast (get())
Slightly slower (sum())
Memory
Less
More (multiple cells)
Use case
Low contention, exact reads
High contention, rare reads
Examples
Sequence generators
Request counters, metrics




PART 7: Concurrent Collections


Q26. What are the key concurrent collections and when to use each?
// 1. ConcurrentHashMap — thread-safe map with fine-grained locking
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.put("key", 1);
map.computeIfAbsent("key", k -> expensiveCompute()); // atomic
map.merge("key", 1, Integer::sum);                     // atomic
// 2. CopyOnWriteArrayList — snapshot-based list (read-heavy, write-rare)
CopyOnWriteArrayList<String> listeners = new CopyOnWriteArrayList<>();
listeners.add("listener1");       // copies entire array!
listeners.forEach(l -> notify(l)); // safe iteration, no lock
// 3. CopyOnWriteArraySet — same idea for Set
CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<>();
// 4. ConcurrentLinkedQueue — lock-free FIFO queue (CAS-based)
ConcurrentLinkedQueue<Task> queue = new ConcurrentLinkedQueue<>();
queue.offer(task);
Task next = queue.poll();
// 5. ConcurrentLinkedDeque — lock-free double-ended queue
ConcurrentLinkedDeque<Task> deque = new ConcurrentLinkedDeque<>();
// 6. ConcurrentSkipListMap — sorted concurrent map (like ConcurrentTreeMap)
ConcurrentSkipListMap<String, Integer> sortedMap = new ConcurrentSkipListMap<>();
// 7. ConcurrentSkipListSet — sorted concurrent set
ConcurrentSkipListSet<String> sortedSet = new ConcurrentSkipListSet<>();


Q27. What are BlockingQueue implementations and how do they work?
BlockingQueue — A queue that blocks when you try to take() from an empty queue or put() on a full queue. It's the foundation of the Producer-Consumer pattern.
// Key methods:
// put(e)  — blocks if queue is full (waits for space)
// take()  — blocks if queue is empty (waits for element)
// offer(e, timeout) — blocks with timeout
// poll(timeout)     — blocks with timeout
Implementations:
// 1. ArrayBlockingQueue — bounded, array-backed, fair option
BlockingQueue<Task> queue = new ArrayBlockingQueue<>(100);
// Fixed capacity 100. Blocks producer when full.
// 2. LinkedBlockingQueue — optionally bounded, linked-node based
BlockingQueue<Task> queue = new LinkedBlockingQueue<>(1000);  // bounded
BlockingQueue<Task> queue = new LinkedBlockingQueue<>();       // unbounded (Integer.MAX_VALUE)
// 3. PriorityBlockingQueue — unbounded, priority-ordered
BlockingQueue<Task> queue = new PriorityBlockingQueue<>();
// Elements must be Comparable or provide Comparator
// 4. SynchronousQueue — zero capacity! Handoff mechanism
BlockingQueue<Task> queue = new SynchronousQueue<>();
// put() blocks until another thread calls take()
// Used by CachedThreadPool internally
// 5. DelayQueue — elements available only after a delay
BlockingQueue<DelayedTask> queue = new DelayQueue<>();
class DelayedTask implements Delayed {
private final long executeAt;
public long getDelay(TimeUnit unit) {
return unit.convert(executeAt - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
}
public int compareTo(Delayed o) {
return Long.compare(this.getDelay(MILLISECONDS), o.getDelay(MILLISECONDS));
}
}
Producer-Consumer pattern:
BlockingQueue<Order> orderQueue = new ArrayBlockingQueue<>(100);
// Producer thread
class OrderProducer implements Runnable {
public void run() {
while (true) {
Order order = receiveOrderFromKafka();
orderQueue.put(order); // blocks if queue full — natural back-pressure!
}
}
}
// Consumer thread
class OrderConsumer implements Runnable {
public void run() {
while (true) {
Order order = orderQueue.take(); // blocks if queue empty — waits for work
processOrder(order);
}
}
}
// Start 1 producer, 5 consumers
executor.submit(new OrderProducer());
for (int i = 0; i < 5; i++) {
executor.submit(new OrderConsumer());
}


PART 8: Fork/Join Framework


Q28. What is the Fork/Join Framework and how does it work internally?
Fork/Join is designed for divide-and-conquer parallelism — split a big task into smaller sub-tasks, process in parallel, then combine results.
[Sort 1M elements]
/        \
[Sort 500K]          [Sort 500K]       ← FORK (split)
/     \               /     \
[Sort 250K] [Sort 250K] [Sort 250K] [Sort 250K]
\     /               \     /
[Merge 500K]           [Merge 500K]    ← JOIN (combine)
\        /
[Merge 1M elements]
Internal mechanism — Work Stealing:
Thread 1 Deque: [Task A, Task B, Task C]   ← thread works from the TOP (LIFO)
Thread 2 Deque: [Task D]
Thread 3 Deque: []                          ← IDLE! Steals from BOTTOM of Thread 1's deque
Thread 4 Deque: [Task E, Task F]
Thread 3 STEALS Task C from Thread 1's deque (from the bottom = FIFO)
This minimizes contention — owner works from top, thief steals from bottom
Java implementation:
public class ParallelMergeSort extends RecursiveAction {
private final int[] array;
private final int start, end;
private static final int THRESHOLD = 1000;
public ParallelMergeSort(int[] array, int start, int end) {
this.array = array;
this.start = start;
this.end = end;
}
@Override
protected void compute() {
if (end - start <= THRESHOLD) {
Arrays.sort(array, start, end); // small enough — sort directly
return;
}
int mid = (start + end) / 2;
ParallelMergeSort left = new ParallelMergeSort(array, start, mid);
ParallelMergeSort right = new ParallelMergeSort(array, mid, end);
invokeAll(left, right); // FORK both, wait for both to JOIN
merge(array, start, mid, end);
}
}
// Usage
ForkJoinPool pool = new ForkJoinPool(); // default: number of CPU cores
int[] data = generateRandomArray(1_000_000);
pool.invoke(new ParallelMergeSort(data, 0, data.length));
RecursiveTask (returns a value):
public class ParallelSum extends RecursiveTask<Long> {
private final long[] array;
private final int start, end;
private static final int THRESHOLD = 10_000;
@Override
protected Long compute() {
if (end - start <= THRESHOLD) {
long sum = 0;
for (int i = start; i < end; i++) sum += array[i];
return sum;
}
int mid = (start + end) / 2;
ParallelSum left = new ParallelSum(array, start, mid);
ParallelSum right = new ParallelSum(array, mid, end);
left.fork();                  // submit left to pool
long rightResult = right.compute(); // compute right in current thread
long leftResult = left.join();      // wait for left's result
return leftResult + rightResult;
}
}
ForkJoinPool pool = new ForkJoinPool();
long total = pool.invoke(new ParallelSum(array, 0, array.length));


PART 9: CompletableFuture & Async Patterns


Q29. How does CompletableFuture relate to Threads?
// CompletableFuture uses threads under the hood
// Default: ForkJoinPool.commonPool()
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
System.out.println("Running on: " + Thread.currentThread().getName());
// Output: Running on: ForkJoinPool.commonPool-worker-1
return fetchData();
});
// Custom executor (recommended for I/O operations):
ExecutorService ioPool = Executors.newFixedThreadPool(20);
CompletableFuture<String> future2 = CompletableFuture
.supplyAsync(() -> callExternalAPI(), ioPool)      // I/O thread
.thenApplyAsync(data -> transform(data), ioPool)   // another I/O thread
.thenApply(result -> format(result));               // may run on calling thread
// Why custom pool for I/O?
// ForkJoinPool has only N-1 threads (N = CPU cores)
// I/O operations BLOCK threads (waiting for response)
// → Common pool gets exhausted → affects ALL parallelStream() and CompletableFuture in the JVM!


Q30. What is the Producer-Consumer pattern? Implement it with modern Java.
// Implementation 1: Using BlockingQueue (classic)
public class OrderProcessingSystem {
private final BlockingQueue<Order> queue = new ArrayBlockingQueue<>(1000);
private final ExecutorService producers = Executors.newFixedThreadPool(2);
private final ExecutorService consumers = Executors.newFixedThreadPool(10);
private volatile boolean running = true;
public void start() {
// Producers
for (int i = 0; i < 2; i++) {
producers.submit(() -> {
while (running) {
Order order = receiveFromKafka();
queue.put(order); // blocks if queue full
}
});
}
// Consumers
for (int i = 0; i < 10; i++) {
consumers.submit(() -> {
while (running) {
Order order = queue.poll(1, TimeUnit.SECONDS); // timeout avoids hanging on shutdown
if (order != null) {
processOrder(order);
}
}
});
}
}
public void shutdown() {
running = false;
producers.shutdown();
consumers.shutdown();
}
}
// Implementation 2: Using Exchanger (direct thread-to-thread handoff)
Exchanger<List<Order>> exchanger = new Exchanger<>();
// Producer fills a batch, then exchanges with consumer
Thread producer = new Thread(() -> {
List<Order> batch = new ArrayList<>();
while (true) {
batch.add(receiveOrder());
if (batch.size() >= 100) {
batch = exchanger.exchange(batch); // swap with consumer's empty list
batch.clear();
}
}
});
// Consumer processes the batch, gives empty list back
Thread consumer = new Thread(() -> {
List<Order> batch = new ArrayList<>();
while (true) {
batch = exchanger.exchange(batch); // get producer's full list
batch.forEach(this::processOrder);
batch.clear();
}
});


PART 10: Real-World Scenario-Based Questions


Q31. Design a thread-safe Singleton.
// Method 1: Double-Checked Locking (most common in interviews)
public class Singleton {
private static volatile Singleton instance; // volatile prevents instruction reordering
private Singleton() {}
public static Singleton getInstance() {
if (instance == null) {                    // first check (no lock)
synchronized (Singleton.class) {
if (instance == null) {            // second check (with lock)
instance = new Singleton();
}
}
}
return instance;
}
}
// Why volatile? Without it, instruction reordering might let another thread
// see a partially constructed object!
// new Singleton() is actually:
//   1. Allocate memory
//   2. Initialize object
//   3. Assign reference to 'instance'
// Without volatile, JVM might reorder to 1→3→2
// Another thread sees non-null instance but uninitialized object!
// Method 2: Bill Pugh (holder pattern — simplest, laziest)
public class Singleton {
private Singleton() {}
private static class Holder {
private static final Singleton INSTANCE = new Singleton();
}
public static Singleton getInstance() {
return Holder.INSTANCE; // class loaded only when first accessed → lazy + thread-safe
}
}
// Method 3: Enum (effective Java — serialization-safe, reflection-safe)
public enum Singleton {
INSTANCE;
public void doSomething() { /* ... */ }
}


Q32. Design a thread-safe Bounded Buffer (Ring Buffer).
public class BoundedBuffer<T> {
private final Object[] buffer;
private int head, tail, count;
private final ReentrantLock lock = new ReentrantLock();
private final Condition notFull = lock.newCondition();
private final Condition notEmpty = lock.newCondition();
public BoundedBuffer(int capacity) {
buffer = new Object[capacity];
}
public void put(T item) throws InterruptedException {
lock.lock();
try {
while (count == buffer.length) {
notFull.await(); // wait until space is available
}
buffer[tail] = item;
tail = (tail + 1) % buffer.length;
count++;
notEmpty.signal(); // wake up a consumer
} finally {
lock.unlock();
}
}
@SuppressWarnings("unchecked")
public T take() throws InterruptedException {
lock.lock();
try {
while (count == 0) {
notEmpty.await(); // wait until data is available
}
T item = (T) buffer[head];
buffer[head] = null;
head = (head + 1) % buffer.length;
count--;
notFull.signal(); // wake up a producer
return item;
} finally {
lock.unlock();
}
}
}
Why two Conditions instead of wait/notifyAll?
With wait()/notifyAll(), waking up a producer might accidentally wake another producer (not a consumer) — wasted context switch.
With separate Condition objects, notFull.signal() wakes only producers, notEmpty.signal() wakes only consumers — precise signaling.


Q33. How would you implement a rate limiter using threads?
public class TokenBucketRateLimiter {
private final int maxTokens;
private final double refillRate; // tokens per second
private double currentTokens;
private long lastRefillTime;
private final ReentrantLock lock = new ReentrantLock();
public TokenBucketRateLimiter(int maxTokens, double refillRate) {
this.maxTokens = maxTokens;
this.refillRate = refillRate;
this.currentTokens = maxTokens;
this.lastRefillTime = System.nanoTime();
}
public boolean tryAcquire() {
lock.lock();
try {
refill();
if (currentTokens >= 1.0) {
currentTokens -= 1.0;
return true;
}
return false;
} finally {
lock.unlock();
}
}
public void acquire() throws InterruptedException {
while (!tryAcquire()) {
Thread.sleep(10); // back off and retry
}
}
private void refill() {
long now = System.nanoTime();
double elapsed = (now - lastRefillTime) / 1_000_000_000.0;
currentTokens = Math.min(maxTokens, currentTokens + elapsed * refillRate);
lastRefillTime = now;
}
}
// Usage: allow 100 requests per second
TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(100, 100);
if (limiter.tryAcquire()) {
processRequest();
} else {
return Response.status(429).entity("Too Many Requests").build();
}


Q34. What is a Thread-Safe Cache with expiration?
public class ExpiringCache<K, V> {
private final ConcurrentHashMap<K, CacheEntry<V>> map = new ConcurrentHashMap<>();
private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();
record CacheEntry<V>(V value, long expiresAt) {
boolean isExpired() {
return System.currentTimeMillis() > expiresAt;
}
}
public ExpiringCache() {
cleaner.scheduleAtFixedRate(this::evictExpired, 1, 1, TimeUnit.MINUTES);
}
public void put(K key, V value, Duration ttl) {
long expiresAt = System.currentTimeMillis() + ttl.toMillis();
map.put(key, new CacheEntry<>(value, expiresAt));
}
public Optional<V> get(K key) {
CacheEntry<V> entry = map.get(key);
if (entry == null || entry.isExpired()) {
map.remove(key);
return Optional.empty();
}
return Optional.of(entry.value());
}
public V getOrCompute(K key, Function<K, V> loader, Duration ttl) {
return get(key).orElseGet(() -> {
V value = loader.apply(key);
put(key, value, ttl);
return value;
});
}
private void evictExpired() {
map.entrySet().removeIf(e -> e.getValue().isExpired());
}
public void shutdown() {
cleaner.shutdown();
}
}
// Usage
ExpiringCache<String, UserProfile> cache = new ExpiringCache<>();
UserProfile profile = cache.getOrCompute(
userId,
id -> userService.fetchProfile(id),
Duration.ofMinutes(10)
);


PART 11: Tricky Interview Questions


Q35. What happens when a thread throws an unhandled exception?
// The thread terminates silently! The exception is NOT propagated to the parent thread.
Thread t = new Thread(() -> {
throw new RuntimeException("Boom!");
});
t.start();
// Main thread has NO idea the exception happened
// Solution 1: UncaughtExceptionHandler
t.setUncaughtExceptionHandler((thread, exception) -> {
System.err.println("Thread " + thread.getName() + " crashed: " + exception.getMessage());
// log it, alert, restart, etc.
});
// Solution 2: Default handler for ALL threads
Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
logger.error("Uncaught exception in thread: " + thread.getName(), exception);
});
// With ExecutorService — exceptions are captured in the Future
Future<?> future = executor.submit(() -> {
throw new RuntimeException("Boom!");
});
try {
future.get(); // exception surfaces HERE
} catch (ExecutionException e) {
System.err.println("Task failed: " + e.getCause().getMessage());
}


Q36. What is Thread Interruption? How does it work?
Interruption is a cooperative mechanism — one thread requests another to stop, but the target thread must check and respond.
Thread worker = new Thread(() -> {
while (!Thread.currentThread().isInterrupted()) {
try {
// do work...
Thread.sleep(1000); // or any blocking call
} catch (InterruptedException e) {
// sleep/wait/join throws InterruptedException when thread is interrupted
System.out.println("Interrupted! Cleaning up...");
Thread.currentThread().interrupt(); // restore interrupt status!
break;
}
}
System.out.println("Thread exiting gracefully");
});
worker.start();
Thread.sleep(5000);
worker.interrupt(); // REQUEST the thread to stop
Why restore interrupt status?
// When catch InterruptedException, the interrupt flag is CLEARED
// If you don't restore it, code higher up the stack won't know
// the thread was interrupted
try {
Thread.sleep(1000);
} catch (InterruptedException e) {
Thread.currentThread().interrupt(); // IMPORTANT: restore the flag
// Now caller can check isInterrupted() and act accordingly
}
Methods that respond to interruption:
Thread.sleep()
Object.wait()
Thread.join()
BlockingQueue.put()/take()
Lock.lockInterruptibly()
Future.get()


Q37. What is the difference between Daemon and Non-Daemon threads?
Thread daemon = new Thread(() -> {
while (true) {
System.out.println("Background task...");
Thread.sleep(1000);
}
});
daemon.setDaemon(true); // must set BEFORE start()
daemon.start();
// When all non-daemon threads finish, JVM exits
// Daemon threads are killed automatically — no graceful shutdown!
Feature
Non-Daemon (User Thread)
Daemon Thread
JVM shutdown
JVM waits for all user threads to finish
JVM kills daemon threads on exit
Default
All threads are non-daemon by default
Must explicitly set setDaemon(true)
Examples
Main thread, your business logic threads
GC thread, JMX threads, timer threads
Cleanup
Runs finally blocks
No guarantee finally blocks run


// Real-world: background cleanup task
Thread cleanupThread = new Thread(() -> {
while (true) {
cleanupTempFiles();
Thread.sleep(60_000);
}
});
cleanupThread.setDaemon(true); // won't prevent JVM from shutting down
cleanupThread.start();


Q38. What is the happens-before relationship in the Java Memory Model?
The Java Memory Model (JMM) defines which memory operations are visible to which threads. The happens-before relationship guarantees ordering and visibility.Key happens-before rules:
1. Program order:     Within a single thread, each action happens-before the next
2. Monitor lock:      unlock() happens-before subsequent lock() on the same monitor
3. volatile:          write to volatile happens-before subsequent read of same volatile
4. Thread start:      t.start() happens-before any action in thread t
5. Thread join:       All actions in thread t happen-before t.join() returns
6. Thread interrupt:  interrupt() happens-before the interrupted thread detects it
7. Transitivity:      If A happens-before B, and B happens-before C, then A happens-before C
   // Example: volatile establishes happens-before
   volatile boolean ready = false;
   int data = 0;
   // Thread A:
   data = 42;          // (1)
   ready = true;       // (2) volatile write — flushes ALL previous writes
   // Thread B:
   if (ready) {        // (3) volatile read — sees ALL writes before the volatile write
   print(data);    // (4) guaranteed to see 42!
   }
   // Because (2) happens-before (3), and (1) happens-before (2),
   // by transitivity (1) happens-before (4) — data=42 is visible


Q39. What are Virtual Threads (Java 21)? How do they change threading?
Virtual Threads (Project Loom) are lightweight threads managed by the JVM, not the OS.
Platform Threads (traditional):
1 Java Thread = 1 OS Thread
~1MB stack each
~10K max practical limit
Blocking I/O blocks the OS thread
Virtual Threads (Java 21+):
1 Virtual Thread ≈ few KB of stack
Millions of virtual threads possible
Blocking I/O only blocks the virtual thread, not the carrier (OS) thread
// Creating virtual threads (Java 21+)
Thread vt = Thread.ofVirtual().start(() -> {
System.out.println("Running on virtual thread: " + Thread.currentThread());
});
// With executor — creates a new virtual thread per task
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
for (int i = 0; i < 1_000_000; i++) { // 1 MILLION concurrent tasks!
executor.submit(() -> {
Thread.sleep(Duration.ofSeconds(1)); // blocks virtual thread, not OS thread
return fetchFromDatabase();
});
}
}
// Structured concurrency (Preview, Java 21+)
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
Future<User> userFuture = scope.fork(() -> fetchUser(id));
Future<List<Order>> ordersFuture = scope.fork(() -> fetchOrders(id));
scope.join();            // wait for both
scope.throwIfFailed();   // propagate errors
return new Dashboard(userFuture.resultNow(), ordersFuture.resultNow());
}
Platform Threads vs Virtual Threads:
Feature
Platform Threads
Virtual Threads
Cost
~1MB stack, OS-managed
~few KB, JVM-managed
Max count
~10K practical
Millions
Blocking I/O
Blocks OS thread
Unmounts from OS thread
Scheduling
OS scheduler
JVM scheduler (mounts on carrier threads)
Thread pools
Required (expensive to create)
Not needed (cheap to create)
Best for
CPU-intensive tasks
I/O-intensive tasks




Q40. How to calculate the optimal thread pool size?
For CPU-bound tasks:
Optimal threads = Number of CPU cores
= Runtime.getRuntime().availableProcessors()
int cpuCores = Runtime.getRuntime().availableProcessors(); // e.g., 8
ExecutorService cpuPool = Executors.newFixedThreadPool(cpuCores);
For I/O-bound tasks (waiting for network, disk, DB):
Optimal threads = CPU cores × (1 + Wait time / Compute time)
Example: 8 cores, 200ms wait (API call), 20ms compute
= 8 × (1 + 200/20) = 8 × 11 = 88 threads
Example: 8 cores, 500ms wait (DB query), 5ms compute
= 8 × (1 + 500/5) = 8 × 101 = 808 threads
int cpuCores = Runtime.getRuntime().availableProcessors();
double waitTime = 200; // ms (average I/O wait)
double computeTime = 20; // ms (average processing)
int optimalThreads = (int) (cpuCores * (1 + waitTime / computeTime));
ExecutorService ioPool = new ThreadPoolExecutor(
optimalThreads / 2,    // core
optimalThreads,         // max
60L, TimeUnit.SECONDS,
new LinkedBlockingQueue<>(1000)
);
Real-world configuration (Spring Boot):
# Tomcat thread pool (handles HTTP requests — I/O bound)
server.tomcat.threads.min-spare=20
server.tomcat.threads.max=200
# Async task executor
spring.task.execution.pool.core-size=10
spring.task.execution.pool.max-size=50
spring.task.execution.pool.queue-capacity=500


The Producer–Consumer problem (often called Publisher–Consumer) is a classic concept in concurrent programming where one part of a system produces data and another part consumes it, typically using a shared buffer or queue.


🧠 Core Idea
Producer (Publisher) → Generates data
Consumer → Uses that data
Shared Buffer → A queue that connects them
Think of it like:
A kitchen (producer) prepares food → puts it on a counter (buffer) → waiter (consumer) picks it up.


⚠️ The Problem
When multiple threads are involved:
What if producer adds data when buffer is full? ❌
What if consumer tries to consume when buffer is empty? ❌
What if both access buffer at the same time? ❌
👉 This leads to:
Race conditions
Data inconsistency
Thread conflicts


✅ Solution Concept
We solve it using:
Synchronization (locks / mutex)
Wait & notify mechanism
Bounded buffer (fixed size queue)


🔄 Flow Explanation












Producer checks → if buffer is not full
Adds item → notifies consumer
Consumer checks → if buffer is not empty
Removes item → notifies producer


💻 Java Example (Simple)
import java.util.LinkedList;
import java.util.Queue;




class SharedBuffer {
private Queue<Integer> queue = new LinkedList<>();
private int capacity = 5;




    public synchronized void produce(int value) throws InterruptedException {
        while (queue.size() == capacity) {
            wait(); // wait if buffer is full
        }
        queue.add(value);
        System.out.println("Produced: " + value);
        notify(); // notify consumer
    }




    public synchronized void consume() throws InterruptedException {
        while (queue.isEmpty()) {
            wait(); // wait if buffer is empty
        }
        int value = queue.poll();
        System.out.println("Consumed: " + value);
        notify(); // notify producer
    }
}




public class Main {
public static void main(String[] args) {
SharedBuffer buffer = new SharedBuffer();




        // Producer Thread
        new Thread(() -> {
            int i = 0;
            while (true) {
                try {
                    buffer.produce(i++);
                    Thread.sleep(500);
                } catch (InterruptedException e) {}
            }
        }).start();




        // Consumer Thread
        new Thread(() -> {
            while (true) {
                try {
                    buffer.consume();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
        }).start();
    }
}






🔑 Key Concepts
wait() → releases lock and pauses thread
notify() → wakes up waiting thread
synchronized → ensures only one thread accesses buffer


🚀 Real-World Examples
Messaging systems (Kafka, RabbitMQ)
Video streaming buffers
Print queue systems
Web servers handling requests


🔥 Advanced Version
Instead of manual wait/notify:
Use BlockingQueue (recommended in real projects)
BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);




👉 It automatically handles:
Thread safety
Waiting
Notification


🧩 Summary
Producer generates → Consumer uses
Buffer connects them
Need synchronization to avoid errors
Use wait() / notify() or BlockingQueue


If you want, I can also:
Show multiple producers & consumers
Explain with real system design (Kafka-like)
Or give interview answers + tricky questions
Good question — and here’s the key thing to understand first:
👉 A single thread does NOT “handle more records” during peak time
👉 Instead, the system scales using multiple threads + async processing + queues
Let’s break this down in a real Spring Boot microservices context 👇


🧠 1. What happens during peak load?
Imagine:
10 users → easy
10,000 users → peak traffic
If you use only 1 thread:
❌ Requests will queue up → slow → timeout
So Java handles it using:


⚙️ 2. Thread Pool (Core Concept)
Spring Boot (via Tomcat/Netty) uses a Thread Pool
👉 Instead of creating new threads:
It reuses threads
Handles multiple requests concurrently


🔄 Flow
















Request comes in
Assigned to a thread from pool
Processed
Thread returned to pool


💻 Example Config (Spring Boot)
server.tomcat.threads.max=200
server.tomcat.threads.min-spare=10




👉 At peak → up to 200 concurrent requests handled


🚀 3. Async Processing (Non-blocking)
If your thread waits (DB call, API call):
❌ Thread gets blocked → waste
👉 Solution: Async processing


💻 Example
@Async
public CompletableFuture<String> process() {
// long-running task
return CompletableFuture.completedFuture("done");
}




👉 Thread is freed → handles other requests


📦 4. Queue-Based Handling (VERY IMPORTANT)
For heavy load systems → don’t process everything instantly
👉 Use queue (Kafka / RabbitMQ)


🔄 Flow
Request received
Put into queue
Worker threads consume gradually
👉 This is Producer–Consumer pattern (you asked earlier 👌)


Example tools:
Apache Kafka
RabbitMQ


⚡ 5. Horizontal Scaling (Microservices Power)
If 1 instance can’t handle load:
👉 Run multiple instances
Example:
1 service → 200 threads
5 services → 1000 concurrent requests


Tools:
Docker
Kubernetes


🧩 6. Database Optimization (Hidden Bottleneck)
Even if threads are many:
👉 DB can become slow
Solutions:
Connection pooling (HikariCP)
Read replicas
Caching (Redis)


🔥 7. Backpressure (Advanced but important)
When system is overloaded:
👉 Don’t accept unlimited requests
Instead:
Queue limit
Reject requests gracefully
Slow down producers


🧠 Real Interview Answer (Short Version)
“A single thread doesn’t handle peak load. In Spring Boot microservices, we use thread pools for concurrency, async processing to avoid blocking, message queues like Kafka for load buffering, and horizontal scaling with Kubernetes to handle high traffic efficiently.”


💡 Key Takeaways
❌ Not about “one thread handling more”
✅ About:
Thread pools
Async processing
Queues
Scaling


If you want, I can go deeper into:
👉 How thread pool tuning works (very important for interviews)
👉 Difference between Tomcat vs Netty (Spring MVC vs WebFlux)
👉 Or a real system design example (like handling 1M requests)

