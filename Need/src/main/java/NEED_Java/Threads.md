### **Q1. What is a Thread and why do we need Multithreading?**

**A Thread is the smallest unit of execution within a process. A process can have multiple threads that share the same memory space but execute independently.Why we need multithreading:**

**Without Multithreading**

**With Multithreading**

**One task at a time (sequential)**

**Multiple tasks simultaneously**

**UI freezes during long operations**

**UI stays responsive while background tasks run**

**Cannot utilize multi-core CPUs**

**Utilizes all CPU cores**

**Slow I/O blocks everything**

**One thread waits for I/O, others continue**

**Poor user experience**

**Responsive, fast applications**

**Real-world examples:**

**Web Server (Tomcat):     Each HTTP request → separate thread**

**Video Player:            Thread 1: decode video, Thread 2: play audio, Thread 3: render UI**

**Banking App:             Thread 1: process transaction, Thread 2: send notification**

**IDE (IntelliJ):          Thread 1: code editing, Thread 2: compilation, Thread 3: indexing**

### **Q2. What is the difference between Process and Thread?**

**Feature**

**Process**

**Thread**

**Definition**

**Independent program in execution**

**Lightweight sub-task within a process**

**Memory**

**Separate memory space**

**Shared memory space (heap)**

**Communication**

**IPC (Inter-Process Communication) — slow**

**Direct shared memory — fast**

**Creation cost**

**Heavy (new memory space)**

**Light (shares parent's memory)**

**Crash impact**

**One process crash doesn't affect others**

**One thread crash can kill the entire process**

**Context switch**

**Expensive (save/restore entire memory map)**

**Cheaper (save/restore only registers, stack)**

**Process A                       Process B**

**┌─────────────────────┐        ┌─────────────────────┐**

**│  Heap Memory         │        │  Heap Memory         │**

**│  ┌──────┐ ┌──────┐  │        │  ┌──────┐ ┌──────┐  │**

**│  │Thread│ │Thread│  │        │  │Thread│ │Thread│  │**

**│  │  1   │ │  2   │  │        │  │  1   │ │  2   │  │**

**│  │Stack │ │Stack │  │        │  │Stack │ │Stack │  │**

**│  └──────┘ └──────┘  │        │  └──────┘ └──────┘  │**

**│  Shared: heap, static│        │  Shared: heap, static│**

**│  Private: stack each  │        │  Private: stack each  │**

**└─────────────────────┘        └─────────────────────┘**

**↑ Separate memory ↑            ↑ Separate memory ↑**

### **Q3. What are the two ways to create a Thread in Java?**

**Way 1: Extend Thread class**

**class MyThread extends Thread {**

**@Override**

**public void run() {**

**System.out.println("Running in: " + Thread.currentThread().getName());**

**}**

**}**

**MyThread t = new MyThread();**

**t.start();** _**// creates new thread and calls run()**_

**Way 2: Implement Runnable interface**

**class MyTask implements Runnable {**

**@Override**

**public void run() {**

**System.out.println("Running in: " + Thread.currentThread().getName());**

**}**

**}**

**Thread t = new Thread(new MyTask());**

**t.start();**

_**// Or with lambda (Java 8+):**_

**Thread t2 = new Thread(() -> System.out.println("Lambda thread"));**

**t2.start();**

**Which is better and why?**

**Feature**

**Extends Thread**

**Implements Runnable**

**Inheritance**

**Cannot extend another class**

**Can extend another class**

**Separation of concerns**

**Task + thread mechanics mixed**

**Task logic separated from threading**

**Reusability**

**Task tied to Thread class**

**The same Runnable can be used with Thread, ExecutorService, etc.**

**Recommendation**

**Avoid**

**Preferred**

**Way 3: Implement Callable (returns a result)**

**Callable task = () -> {**

**Thread.sleep(1000);**

**return 42;**

**};**

**ExecutorService executor = Executors.newSingleThreadExecutor();**

**Future future = executor.submit(task);**

**int result = future.get();** _**// blocks until result is available → 42**_

**executor.shutdown();**

**Feature**

**Runnable**

**Callable**

**Method**

**run() → void**

**call() → V**

**Return value**

**No**

**Yes**

**Checked exceptions**

**Cannot throw**

**Can throw**

**Used with**

**Thread, Executor**

**Executor only (returns Future)**

### **Q4. What is the difference between start() and run()?**

**Thread t = new Thread(() -> {**

**System.out.println("Thread: " + Thread.currentThread().getName());**

**});**

**t.run();**   _**// Output: Thread: main         ← runs in CALLER's thread (no new thread!)**_

**t.start();** _**// Output: Thread: Thread-0     ← creates a NEW thread, then calls run()**_

**Internally:**

**t.start():**

**1. JVM allocates a new native OS thread**

**2. Thread transitions from NEW → RUNNABLE**

**3. OS scheduler picks it up**

**4. JVM calls run() on the NEW thread**

**t.run():**

**1. Just a normal method call on the current thread**

**2. No new thread is created**

**3. Runs synchronously in the calling thread**

**Can you call start() twice?**

**Thread t = new Thread(() -> System.out.println("Hello"));**

**t.start();** _**// OK**_

**t.start();** _**// IllegalThreadStateException! A thread cannot be restarted.**_

### **Q5. Explain the Thread Lifecycle (Thread States).**

**┌──────────┐**

**│   NEW     │  Thread object created, not yet started**

**└────┬─────┘**

**│ start()**

**▼**

**┌──────────┐       ┌─────────────┐**

**│ RUNNABLE  │◄─────│   BLOCKED    │  Waiting to acquire a lock**

**│(Ready +   │      └─────────────┘**

**│ Running)  │      ┌─────────────┐**

**│           │◄─────│  WAITING     │  wait(), join(), park()**

**│           │      └─────────────┘**

**│           │      ┌─────────────┐**

**│           │◄─────│TIMED\_WAITING │  sleep(ms), wait(ms), join(ms)**

**└────┬─────┘      └─────────────┘**

**│ run() completes or exception**

**▼**

**┌──────────┐**

**│TERMINATED │  Thread is done**

**└──────────┘**

**All 6 states (from Thread.State enum):**

**State**

**When**

**How to enter**

**NEW**

**Thread object created**

**new Thread()**

**RUNNABLE**

**Running or ready to run**

**start(), lock acquired, notify, sleep done**

**BLOCKED**

**Waiting for a monitor lock**

**Trying to enter synchronized block held by another thread**

**WAITING**

**Waiting indefinitely**

**wait(), join(), LockSupport.park()**

**TIMED\_WAITING**

**Waiting with a timeout**

**sleep(ms), wait(ms), join(ms)**

**TERMINATED**

**Execution completed**

**run() finishes or uncaught exception**

**Thread t = new Thread(() -> {**

**try { Thread.sleep(5000); } catch (InterruptedException e) {}**

**});**

**System.out.println(t.getState());** _**// NEW**_

**t.start();**

**System.out.println(t.getState());** _**// RUNNABLE**_

**Thread.sleep(100);**

**System.out.println(t.getState());** _**// TIMED\_WAITING (sleeping)**_

**t.join();**

**System.out.println(t.getState());** _**// TERMINATED**_

**PART 2: Synchronization & Thread Safety**
-------------------------------------------

### **Q6. What is a Race Condition? Explain with an example.**

**A race condition occurs when two or more threads access shared data simultaneously and at least one modifies it, leading to unpredictable results.**

**class BankAccount {**

**private int balance = 1000;**

**public void withdraw(int amount) {**

**if (balance >= amount) {**          _**// Thread A checks: 1000 >= 800 ✓**_

_**// Thread A pauses here...**_

_**// Thread B checks: 1000 >= 500 ✓ (balance hasn't changed yet!)**_

**balance -= amount;**            _**// Thread A: balance = 1000 - 800 = 200**_

_**// Thread B: balance = 200 - 500 = -300 ← OVERDRAFT! Bug!**_

**}**

**}**

**}**

_**// Two threads withdrawing simultaneously**_

**BankAccount account = new BankAccount();**

**Thread t1 = new Thread(() -> account.withdraw(800));**

**Thread t2 = new Thread(() -> account.withdraw(500));**

**t1.start(); t2.start();**

_**// Expected: one succeeds, one fails. Actual: both may succeed → negative balance!**_

**The problem: The check (balance >= amount) and the update (balance -= amount) are not atomic — another thread can sneak in between.**

### **Q7. What is synchronized and how does it work internally?**

**synchronized ensures that only ONE thread can execute a block of code at a time by acquiring a monitor lock (also called intrinsic lock or mutex).Two forms:**

_**// Form 1: Synchronized method — locks on 'this' object**_

**class BankAccount {**

**private int balance = 1000;**

**public synchronized void withdraw(int amount) {**

**if (balance >= amount) {**

**balance -= amount;**

**}**

**}**

**public synchronized int getBalance() {**

**return balance;**

**}**

**}**

_**// Form 2: Synchronized block — locks on a specific object**_

**class BankAccount {**

**private int balance = 1000;**

**private final Object lock = new Object();**

**public void withdraw(int amount) {**

**synchronized (lock) {**  _**// only lock the critical section**_

**if (balance >= amount) {**

**balance -= amount;**

**}**

**}**

_**// code outside synchronized runs concurrently**_

**}**

**}**

_**// Form 3: Static synchronized — locks on the Class object**_

**class Counter {**

**private static int count = 0;**

**public static synchronized void increment() {**

**count++;** _**// lock on Counter.class**_

**}**

**}**

**Internal working — Monitor (Object Header):Every Java object has an object header containing a mark word that stores lock information.**

**Object Header (64-bit JVM):**

**┌────────────────────────────────────────────────────┐**

**│ Mark Word (64 bits)                                │**

**│  ┌─────────────────────────────────────────────┐   │**

**│  │ Biased Lock / Thin Lock / Fat Lock info     │   │**

**│  │ + HashCode + GC age                         │   │**

**│  └─────────────────────────────────────────────┘   │**

**│ Class Pointer (32/64 bits)                         │**

**└────────────────────────────────────────────────────┘**

**Lock escalation (how JVM optimizes):**

**1\. No contention       → Biased Locking (almost zero cost — just stores thread ID)**

**2\. Light contention    → Thin Lock / Lightweight Lock (CAS spin)**

**3\. Heavy contention    → Fat Lock / Heavyweight Lock (OS mutex, thread blocking)**

**Biased Lock → Thin Lock → Fat Lock**

**(fast)        (medium)    (slow, involves OS context switch)**

**Thread A enters synchronized:**

**1. Check mark word: no lock → set biased to Thread A (almost free)**

**2. Next time Thread A enters: same bias → no operation needed**

**3. Thread B tries to enter: revoke bias → upgrade to thin lock (CAS spin)**

**4. If CAS fails repeatedly: upgrade to fat lock (OS mutex)**

**Thread B enters synchronized while A holds the lock:**

**1. Thread B → BLOCKED state**

**2. Context switch to OS → thread parked**

**3. When A exits: notify OS → wake up B → B acquires lock**

### **Q8. What is the difference between synchronized method and synchronized block?**

_**// Synchronized method — locks ENTIRE method on 'this'**_

**public synchronized void transfer(Account to, int amount) {**

**this.balance -= amount;**  _**// ← locked**_

**to.balance += amount;**    _**// ← still locked (unnecessary if 'to' has its own sync)**_

**logTransaction();**        _**// ← still locked (unnecessary!)**_

**}**

_**// Synchronized block — lock ONLY what needs locking**_

**public void transfer(Account to, int amount) {**

**synchronized (this) {**

**this.balance -= amount;**  _**// ← locked (only the critical section)**_

**}**

**synchronized (to) {**

**to.balance += amount;**    _**// ← locked on different object**_

**}**

**logTransaction();**            _**// ← NOT locked (runs freely)**_

**}**

**Feature**

**Synchronized Method**

**Synchronized Block**

**Lock object**

**this (instance) or Class (static)**

**Any object you specify**

**Granularity**

**Entire method**

**Only critical section**

**Flexibility**

**Low**

**High (different locks for different data)**

**Performance**

**May lock too much**

**Better — fine-grained locking**

**Best practice: Prefer synchronized blocks with fine-grained locking. Lock only what you must, for as little time as possible.**

### **Q9. What is a Deadlock? How to detect and prevent it?**

**Deadlock = Two or more threads are waiting for each other to release locks, and none can proceed.**

_**// DEADLOCK SCENARIO**_

**Object lockA = new Object();**

**Object lockB = new Object();**

_**// Thread 1: locks A, then tries to lock B**_

**Thread t1 = new Thread(() -> {**

**synchronized (lockA) {**

**System.out.println("T1: holding lockA, waiting for lockB...");**

**try { Thread.sleep(100); } catch (InterruptedException e) {}**

**synchronized (lockB) {** _**// BLOCKED! T2 holds lockB**_

**System.out.println("T1: holding both locks");**

**}**

**}**

**});**

_**// Thread 2: locks B, then tries to lock A**_

**Thread t2 = new Thread(() -> {**

**synchronized (lockB) {**

**System.out.println("T2: holding lockB, waiting for lockA...");**

**try { Thread.sleep(100); } catch (InterruptedException e) {}**

**synchronized (lockA) {** _**// BLOCKED! T1 holds lockA**_

**System.out.println("T2: holding both locks");**

**}**

**}**

**});**

**t1.start(); t2.start();**

_**// DEADLOCK! Both threads block forever.**_

**Four conditions for deadlock (ALL must be true):**

1.  **Mutual Exclusion — Resources are non-shareable**

2.  **Hold and Wait — Thread holds one lock and waits for another**

3.  **No Preemption — Locks can't be forcibly taken**

4.  **Circular Wait — T1→T2→T1 (circular dependency)**


**Prevention strategies:**

_**// Strategy 1: Lock ordering — always acquire locks in the same order**_

_**// INSTEAD of Thread1(A→B) and Thread2(B→A), ALWAYS acquire A before B**_

**synchronized (lockA) {**

**synchronized (lockB) {**

_**// both threads use the same order → no circular wait**_

**}**

**}**

_**// Strategy 2: tryLock with timeout (using ReentrantLock)**_

**ReentrantLock lock1 = new ReentrantLock();**

**ReentrantLock lock2 = new ReentrantLock();**

**boolean acquired = false;**

**try {**

**acquired = lock1.tryLock(1, TimeUnit.SECONDS)**

**&& lock2.tryLock(1, TimeUnit.SECONDS);**

**if (acquired) {**

_**// do work**_

**} else {**

_**// back off, retry later**_

**}**

**} finally {**

**if (lock2.isHeldByCurrentThread()) lock2.unlock();**

**if (lock1.isHeldByCurrentThread()) lock1.unlock();**

**}**

_**// Strategy 3: Use a single lock for related resources**_

**private final Object transferLock = new Object();**

**synchronized (transferLock) {**

**accountA.debit(amount);**

**accountB.credit(amount);**

**}**

**Detection with jstack or programmatically:**

**ThreadMXBean bean = ManagementFactory.getThreadMXBean();**

**long\[\] deadlockedThreads = bean.findDeadlockedThreads();**

**if (deadlockedThreads != null) {**

**ThreadInfo\[\] infos = bean.getThreadInfo(deadlockedThreads, true, true);**

**for (ThreadInfo info : infos) {**

**System.out.println("Deadlocked thread: " + info.getThreadName());**

**System.out.println("Waiting for lock: " + info.getLockInfo());**

**System.out.println("Held locks: " + Arrays.toString(info.getLockedSynchronizers()));**

**}**

**}**

### **Q10. What is a Livelock and Starvation?**

**Livelock — Threads keep responding to each other without making progress (like two people in a hallway stepping aside in the same direction).**

_**// Livelock example — both threads keep yielding**_

**class Worker {**

**private boolean active = true;**

**public void work(Worker other) {**

**while (active) {**

**if (other.active) {**

**System.out.println(Thread.currentThread().getName() + ": I'll wait, you go first");**

**Thread.yield();** _**// politely yields — but the other does the same!**_

**continue;**

**}**

_**// do work...**_

**active = false;**

**}**

**}**

**}**

_**// Both threads keep saying "you go first" → no progress!**_

**Starvation — A thread never gets CPU time because higher-priority threads keep running.**

_**// Starvation: low priority thread never runs**_

**Thread highPriority = new Thread(task);**

**highPriority.setPriority(Thread.MAX\_PRIORITY);** _**// 10**_

**Thread lowPriority = new Thread(task);**

**lowPriority.setPriority(Thread.MIN\_PRIORITY);**  _**// 1**_

_**// lowPriority may NEVER run if highPriority keeps the CPU busy**_

_**// Another cause: unfair lock**_

_**// synchronized uses an UNFAIR lock — the thread that just released**_

_**// can immediately re-acquire, starving waiting threads**_

_**// Solution: Use fair lock**_

**ReentrantLock fairLock = new ReentrantLock(true);** _**// fair = true**_

_**// Threads are served in FIFO order (but ~10% slower)**_

### **Q11. What is the volatile keyword and how does it work internally?**

**volatile guarantees visibility and ordering but NOT atomicity.Problem without volatile:**

**class StopFlag {**

**boolean running = true;** _**// not volatile**_

**void stop() { running = false; }**

**void run() {**

**while (running) {**

_**// Thread may NEVER see running = false!**_

_**// JVM/CPU can cache 'running' in a register and never re-read from main memory**_

**}**

**}**

**}**

**With volatile:**

**class StopFlag {**

**volatile boolean running = true;**

**void stop() { running = false; }**  _**// write goes to MAIN MEMORY**_

**void run() {**

**while (running) {**             _**// read comes from MAIN MEMORY**_

_**// guaranteed to see the updated value**_

**}**

**}**

**}**

**How volatile works internally (Java Memory Model):**

**Without volatile:**

**Thread 1 CPU Cache: running = true (cached, never refreshed)**

**Thread 2 CPU Cache: running = false (wrote here)**

**Main Memory: running = true (Thread 2's write may be buffered)**

**With volatile:**

**WRITE: Thread 2 writes → flushes to main memory immediately**

**READ:  Thread 1 reads → always reads from main memory (bypasses cache)**

**Also: establishes a happens-before relationship**

**What volatile does NOT do — NOT atomic:**

**volatile int counter = 0;**

_**// THIS IS STILL NOT THREAD-SAFE!**_

**counter++;** _**// this is actually: read → increment → write (3 operations, not atomic)**_

_**// Thread A: reads 5**_

_**// Thread B: reads 5**_

_**// Thread A: writes 6**_

_**// Thread B: writes 6 (lost update!)**_

_**// For atomic increment, use AtomicInteger or synchronized**_

**AtomicInteger counter = new AtomicInteger(0);**

**counter.incrementAndGet();** _**// truly atomic**_

**When to use volatile:**

**Use volatile when**

**Don't use volatile when**

**Simple flag (stop signal)**

**Counter (increment/decrement)**

**One thread writes, others only read**

**Multiple threads write**

**Published immutable reference**

**Compound check-then-act**

**Double-checked locking singleton**

**Any read-modify-write operation**

**PART 3: Thread Communication**
--------------------------------

### **Q12. Explain wait(), notify(), and notifyAll() with their internal working.**

**These methods enable inter-thread communication — one thread waits for a condition, another signals when the condition is met.They must be called from within a synchronized block (on the same object used as the lock).**

**class MessageQueue {**

**private final Queue queue = new LinkedList<>();**

**private final int capacity;**

**public MessageQueue(int capacity) {**

**this.capacity = capacity;**

**}**

**public synchronized void put(String message) throws InterruptedException {**

**while (queue.size() == capacity) {**

**wait();** _**// release lock, go to WAITING state, wait for notification**_

**}**

**queue.add(message);**

**System.out.println("Produced: " + message);**

**notifyAll();** _**// wake up all waiting threads (consumers)**_

**}**

**public synchronized String take() throws InterruptedException {**

**while (queue.isEmpty()) {**

**wait();** _**// release lock, go to WAITING state, wait for notification**_

**}**

**String message = queue.poll();**

**System.out.println("Consumed: " + message);**

**notifyAll();** _**// wake up all waiting threads (producers)**_

**return message;**

**}**

**}**

**Internal working:**

**Each object has:**

**1. Monitor Lock (mutex) — only one thread can hold it**

**2. Wait Set — set of threads that called wait() on this object**

**Thread A calls wait():**

**1. Releases the monitor lock**

**2. A is added to the object's wait set**

**3. A goes to WAITING state (parked by OS)**

**Thread B calls notify():**

**1. ONE random thread is removed from the wait set**

**2. That thread moves to BLOCKED state (needs to re-acquire the lock)**

**3. When B exits synchronized, the notified thread can acquire the lock**

**Thread B calls notifyAll():**

**1. ALL threads are removed from the wait set**

**2. All move to BLOCKED state, competing for the lock**

**3. One wins, others stay BLOCKED**

**Why use while and not if with wait()?**

_**// BAD — spurious wakeups can cause bugs**_

**if (queue.isEmpty()) {**

**wait();** _**// wakes up, but queue might still be empty! (spurious wakeup)**_

**}**

**String msg = queue.poll();** _**// NullPointerException!**_

_**// GOOD — always re-check the condition**_

**while (queue.isEmpty()) {**

**wait();** _**// even if spuriously woken, the while loop re-checks**_

**}**

**String msg = queue.poll();** _**// safe**_

**Why must wait/notify be in synchronized?**

_**// Without synchronized, you get IllegalMonitorStateException**_

**obj.wait();**   _**// throws IllegalMonitorStateException!**_

**obj.notify();** _**// throws IllegalMonitorStateException!**_

_**// The thread MUST own the monitor (lock) to call wait/notify**_

**synchronized (obj) {**

**obj.wait();**   _**// OK — releases the lock and waits**_

**obj.notify();** _**// OK — signals a waiting thread**_

**}**

### **Q13. What is the difference between notify() and notifyAll()?**

**Feature**

**notify()**

**notifyAll()**

**Wakes up**

**One random waiting thread**

**All waiting threads**

**Risk**

**If wrong thread is woken → others wait forever**

**No missed signals**

**Performance**

**Slightly faster (less context switching)**

**Slower but safer**

**Use when**

**Only one type of waiter (homogeneous)**

**Multiple types of waiters (producers AND consumers)**

_**// PROBLEM with notify() in producer-consumer:**_

_**// Producers and consumers both wait on the same object**_

_**// notify() might wake up another PRODUCER instead of a CONSUMER**_

_**// → The consumer message is never delivered!**_

_**// SOLUTION: Use notifyAll() to wake ALL threads**_

_**// Each thread re-checks its condition in the while loop**_

_**// Only the appropriate thread proceeds**_

**Best practice: Almost always use notifyAll(). Use notify() only when all waiting threads are equivalent and interchangeable.**

### **Q14. What are join() and sleep()?**

**join() — Wait for another thread to finish.**

**Thread t1 = new Thread(() -> {**

**System.out.println("T1: downloading file...");**

**try { Thread.sleep(3000); } catch (InterruptedException e) {}**

**System.out.println("T1: download complete");**

**});**

**t1.start();**

**t1.join();** _**// main thread BLOCKS until t1 finishes**_

**System.out.println("Main: processing downloaded file");** _**// runs AFTER t1 is done**_

_**// With timeout:**_

**t1.join(5000);** _**// wait at most 5 seconds**_

**if (t1.isAlive()) {**

**System.out.println("T1 still running after 5 seconds!");**

**}**

**sleep() — Pause the current thread for a specified duration.**

**Thread.sleep(1000);** _**// pause for 1 second**_

_**// Thread goes to TIMED\_WAITING state**_

_**// Does NOT release any locks!**_

**Feature**

**sleep(ms)**

**wait(ms)**

**Class**

**Thread.sleep() (static)**

**Object.wait() (instance)**

**Lock**

**Does NOT release**

**Releases the lock**

**Purpose**

**Pause execution**

**Wait for a condition/signal**

**Wake-up**

**After timeout**

**notify()/notifyAll() or timeout**

**Requires sync**

**No**

**Yes (must be in synchronized)**

### **Q15. What is ThreadLocal and when would you use it?**

**ThreadLocal provides per-thread variable storage — each thread gets its own independent copy of the variable.**

_**// Each thread gets its own SimpleDateFormat (which is NOT thread-safe)**_

**private static final ThreadLocal dateFormat =**

**ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));**

**public String formatDate(Date date) {**

**return dateFormat.get().format(date);** _**// each thread uses its own instance**_

**}**

**Internal working:**

**Thread object:**

**┌─────────────────────────────┐**

**│ Thread                       │**

**│   threadLocalMap:            │**

**│     ThreadLocal\_1 → "value1" │**

**│     ThreadLocal\_2 → 42       │**

**│     ThreadLocal\_3 → \[user\]   │**

**└─────────────────────────────┘**

**Each Thread has a ThreadLocalMap (a hash map):**

**Key = ThreadLocal object (weak reference)**

**Value = the thread-specific value**

**Real-world scenarios:**

_**// 1. User context in web applications (per-request data)**_

**public class UserContext {**

**private static final ThreadLocal currentUser = new ThreadLocal<>();**

**public static void set(User user) { currentUser.set(user); }**

**public static User get() { return currentUser.get(); }**

**public static void clear() { currentUser.remove(); }** _**// IMPORTANT!**_

**}**

_**// In a servlet filter:**_

**public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {**

**try {**

**User user = authenticateUser(req);**

**UserContext.set(user);**

**chain.doFilter(req, res);**

**} finally {**

**UserContext.clear();** _**// prevent memory leaks in thread pools!**_

**}**

**}**

_**// Anywhere in the request processing:**_

**User user = UserContext.get();** _**// no need to pass User through every method**_

_**// 2. Transaction management**_

**public class TransactionContext {**

**private static final ThreadLocal connectionHolder = new ThreadLocal<>();**

**public static Connection getConnection() {**

**Connection conn = connectionHolder.get();**

**if (conn == null) {**

**conn = dataSource.getConnection();**

**connectionHolder.set(conn);**

**}**

**return conn;**

**}**

**}**

**Memory leak warning with thread pools:**

_**// In a thread pool, threads are REUSED**_

_**// If you set a ThreadLocal but never remove it:**_

_**// 1. The thread lives on (it's pooled)**_

_**// 2. The ThreadLocal value is never garbage collected**_

_**// 3. MEMORY LEAK!**_

_**// ALWAYS clean up in a finally block:**_

**try {**

**threadLocal.set(value);**

_**// do work**_

**} finally {**

**threadLocal.remove();** _**// CRITICAL for thread pools**_

**}**

**PART 4: Thread Pools & ExecutorService**
------------------------------------------

### **Q16. What is a Thread Pool and why should you use it?**

**Without a pool — creating threads manually:**

_**// BAD: Creating a new thread for every request**_

**for (int i = 0; i < 10\_000; i++) {**

**new Thread(() -> handleRequest()).start();** _**// 10,000 threads!**_

_**// OS thread creation: ~1MB stack per thread = ~10GB memory**_

_**// Thread creation overhead: ~1ms each**_

_**// Context switching: catastrophic with 10,000 threads**_

**}**

**With a pool — reusing threads:**

_**// GOOD: Fixed pool of reusable threads**_

**ExecutorService pool = Executors.newFixedThreadPool(100);**

**for (int i = 0; i < 10\_000; i++) {**

**pool.submit(() -> handleRequest());** _**// 10,000 tasks, but only 100 threads**_

_**// Tasks queue up and are processed by available threads**_

**}**

**pool.shutdown();**

**Benefits:**

*   **Reuse — No thread creation/destruction overhead per task**

*   **Bounded — Limit resource consumption (prevent OOM)**

*   **Managed — Queue tasks, handle rejection, monitor pool health**

*   **Tunable — Adjust pool size based on workload**


### **Q17. Explain all the types of ExecutorService and their internal working.**

**1\. FixedThreadPool — Fixed number of threads**

**ExecutorService pool = Executors.newFixedThreadPool(10);**

_**// Internally: new ThreadPoolExecutor(10, 10, 0L, MILLISECONDS, new LinkedBlockingQueue<>())**_

_**// Core threads: 10, Max threads: 10, Queue: unbounded**_

_**// If all 10 threads busy → tasks wait in queue (queue can grow infinitely → OOM risk!)**_

**2\. CachedThreadPool — Elastic pool (creates threads as needed)**

**ExecutorService pool = Executors.newCachedThreadPool();**

_**// Internally: new ThreadPoolExecutor(0, Integer.MAX\_VALUE, 60L, SECONDS, new SynchronousQueue<>())**_

_**// Core threads: 0, Max threads: unlimited, Idle timeout: 60 seconds**_

_**// No queue! Each task needs a thread immediately**_

_**// Great for short-lived tasks, DANGEROUS for long-running tasks (unbounded threads → OOM!)**_

**3\. SingleThreadExecutor — Exactly one thread**

**ExecutorService pool = Executors.newSingleThreadExecutor();**

_**// Internally: new ThreadPoolExecutor(1, 1, 0L, MILLISECONDS, new LinkedBlockingQueue<>())**_

_**// Guarantees tasks are executed sequentially in submission order**_

_**// Use case: sequential processing, file writing, audit logging**_

**4\. ScheduledThreadPool — For delayed/periodic tasks**

**ScheduledExecutorService pool = Executors.newScheduledThreadPool(5);**

_**// Run after 3 seconds delay**_

**pool.schedule(() -> System.out.println("Delayed"), 3, TimeUnit.SECONDS);**

_**// Run every 5 seconds (fixed rate)**_

**pool.scheduleAtFixedRate(() -> System.out.println("Periodic"), 0, 5, TimeUnit.SECONDS);**

_**// Run 5 seconds after PREVIOUS task finishes (fixed delay)**_

**pool.scheduleWithFixedDelay(() -> System.out.println("Delayed repeat"), 0, 5, TimeUnit.SECONDS);**

**5\. WorkStealingPool (Java 8) — Fork/Join based**

**ExecutorService pool = Executors.newWorkStealingPool();**

_**// Uses ForkJoinPool internally**_

_**// Threads: number of available CPU cores**_

_**// Each thread has its own deque; idle threads STEAL work from busy threads**_

_**// Best for CPU-intensive, recursive tasks**_

### **Q18. How does ThreadPoolExecutor work internally?**

_**// Full constructor — you should use this instead of Executors factory methods**_

**ThreadPoolExecutor executor = new ThreadPoolExecutor(**

**5,**                      _**// corePoolSize: always keep 5 threads alive**_

**20,**                     _**// maximumPoolSize: can grow up to 20**_

**60L,**                    _**// keepAliveTime: idle non-core threads die after 60 sec**_

**TimeUnit.SECONDS,**

**new ArrayBlockingQueue<>(100),**  _**// workQueue: holds 100 waiting tasks**_

**new ThreadPoolExecutor.CallerRunsPolicy()**  _**// rejectionHandler**_

**);**

**Task submission flow:**

**submit(task)**

**│**

**▼**

**Active threads < corePoolSize?  ──YES──→  Create new core thread, run task**

**│**

**NO**

**▼**

**Queue has space?  ──YES──→  Add task to queue**

**│**

**NO (queue full)**

**▼**

**Active threads < maximumPoolSize?  ──YES──→  Create new non-core thread, run task**

**│**

**NO (pool is full AND queue is full)**

**▼**

**Execute RejectionPolicy**

**Visual:**

**┌─────────────────────────┐**

**submit(task) ──→  │    Core Threads (5)      │ ← always alive**

**│  T1  T2  T3  T4  T5     │**

**└─────────┬───────────────┘**

**│ if all busy**

**┌─────────▼───────────────┐**

**│     Work Queue (100)     │ ← tasks wait here**

**│  \[task6, task7, ...\]     │**

**└─────────┬───────────────┘**

**│ if queue full**

**┌─────────▼───────────────┐**

**│   Non-Core Threads (15)  │ ← created on demand, die when idle**

**│  T6  T7 ... T20          │**

**└─────────┬───────────────┘**

**│ if max reached + queue full**

**┌─────────▼───────────────┐**

**│    Rejection Policy      │**

**└─────────────────────────┘**

**Rejection policies (when pool and queue are both full):**

_**// 1. AbortPolicy (default) — throws RejectedExecutionException**_

**new ThreadPoolExecutor.AbortPolicy()**

_**// 2. CallerRunsPolicy — the submitting thread runs the task itself**_

**new ThreadPoolExecutor.CallerRunsPolicy()**

_**// Slows down the producer, providing natural back-pressure**_

_**// 3. DiscardPolicy — silently drops the task**_

**new ThreadPoolExecutor.DiscardPolicy()**

_**// 4. DiscardOldestPolicy — drops the oldest queued task, retries**_

**new ThreadPoolExecutor.DiscardOldestPolicy()**

### **Q19. How to properly shut down an ExecutorService?**

**ExecutorService executor = Executors.newFixedThreadPool(10);**

_**// Submit tasks...**_

**executor.submit(task1);**

**executor.submit(task2);**

_**// Graceful shutdown**_

**executor.shutdown();** _**// stops accepting new tasks, finishes existing ones**_

**try {**

**if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {**

_**// Tasks didn't finish in 30 seconds**_

**executor.shutdownNow();** _**// interrupts all running tasks**_

**if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {**

**System.err.println("Pool did not terminate");**

**}**

**}**

**} catch (InterruptedException e) {**

**executor.shutdownNow();**

**Thread.currentThread().interrupt();** _**// preserve interrupt status**_

**}**

**Method**

**Behavior**

**shutdown()**

**No new tasks accepted; in-progress and queued tasks finish**

**shutdownNow()**

**Interrupts running tasks, removes queued tasks, returns unexecuted list**

**awaitTermination(timeout)**

**Blocks until all tasks complete or timeout**

**isShutdown()**

**Returns true after shutdown() called**

**isTerminated()**

**Returns true after all tasks completed post-shutdown**

**PART 5: Locks & Concurrency Utilities**
-----------------------------------------

### **Q20. What is the difference between synchronized and ReentrantLock?**

**Feature**

**synchronized**

**ReentrantLock**

**Lock acquisition**

**Automatic (enter block)**

**Manual (lock())**

**Lock release**

**Automatic (exit block)**

**Manual (unlock() in finally!)**

**Try without blocking**

**No**

**tryLock()**

**Try with timeout**

**No**

**tryLock(timeout)**

**Interruptible waiting**

**No**

**lockInterruptibly()**

**Fairness**

**Unfair only**

**Configurable (new ReentrantLock(true))**

**Condition variables**

**One per object (wait/notify)**

**Multiple (newCondition())**

**Read-write split**

**No**

**Use ReentrantReadWriteLock**

**ReentrantLock lock = new ReentrantLock();**

**public void transfer(Account from, Account to, int amount) {**

**lock.lock();**

**try {**

**from.debit(amount);**

**to.credit(amount);**

**} finally {**

**lock.unlock();** _**// ALWAYS unlock in finally!**_

**}**

**}**

_**// tryLock — non-blocking attempt**_

**public boolean tryTransfer(Account from, Account to, int amount) {**

**if (lock.tryLock()) {**

**try {**

**from.debit(amount);**

**to.credit(amount);**

**return true;**

**} finally {**

**lock.unlock();**

**}**

**}**

**return false;** _**// couldn't acquire lock — try later**_

**}**

_**// tryLock with timeout — avoids deadlock**_

**public boolean transferWithTimeout(Account from, Account to, int amount)** 

        **throws InterruptedException {**

**if (lock.tryLock(2, TimeUnit.SECONDS)) {**

**try {**

**from.debit(amount);**

**to.credit(amount);**

**return true;**

**} finally {**

**lock.unlock();**

**}**

**}**

**throw new TimeoutException("Could not acquire lock in 2 seconds");**

**}**

### **Q21. What is ReadWriteLock and when should you use it?**

**Problem: synchronized and ReentrantLock block ALL access — even reads block other reads. But if data is mostly read and rarely written, allowing concurrent reads is safe and much faster.**

_**// With synchronized — reads block each other (unnecessary!)**_

_**// 100 threads reading simultaneously → only 1 can read at a time → bottleneck!**_

_**// With ReadWriteLock — multiple readers can read simultaneously**_

**ReadWriteLock rwLock = new ReentrantReadWriteLock();**

**Lock readLock = rwLock.readLock();**

**Lock writeLock = rwLock.writeLock();**

**Map cache = new HashMap<>();**

_**// Multiple threads can read simultaneously (no blocking between readers)**_

**public String read(String key) {**

**readLock.lock();**

**try {**

**return cache.get(key);**

**} finally {**

**readLock.unlock();**

**}**

**}**

_**// Only ONE thread can write (blocks all readers and other writers)**_

**public void write(String key, String value) {**

**writeLock.lock();**

**try {**

**cache.put(key, value);**

**} finally {**

**writeLock.unlock();**

**}**

**}**

**Concurrency rules:**

**Read Lock:   Multiple readers at the same time ✓**

**Reader while writer holds lock ✗ (blocks)**

**Write Lock:  Only one writer at a time ✓**

**Writer while readers hold lock ✗ (blocks)**

**Scenario**

**synchronized**

**ReadWriteLock**

**100 concurrent reads**

**One at a time**

**All 100 simultaneously**

**Read during write**

**Blocked**

**Blocked**

**Write during reads**

**Blocked**

**Blocked**

**Read:Write = 95:5**

**Terrible**

**Excellent**

**StampedLock (Java 8) — even faster for read-heavy workloads:**

**StampedLock lock = new StampedLock();**

_**// Optimistic read — doesn't acquire any lock (ultra-fast)**_

**public double readCoordinates() {**

**long stamp = lock.tryOptimisticRead();** _**// no blocking, no locking!**_

**double x = this.x;**

**double y = this.y;**

**if (!lock.validate(stamp)) {**

_**// a write happened while we were reading — fall back to read lock**_

**stamp = lock.readLock();**

**try {**

**x = this.x;**

**y = this.y;**

**} finally {**

**lock.unlockRead(stamp);**

**}**

**}**

**return Math.sqrt(x \* x + y \* y);**

**}**

### **Q22. What are CountDownLatch, CyclicBarrier, and Semaphore?**

**CountDownLatch — Wait for N events to complete before proceeding. One-time use.**

_**// Real-world: application startup — wait for all services to initialize**_

**CountDownLatch latch = new CountDownLatch(3);** _**// count = 3**_

_**// Each service counts down when ready**_

**executor.submit(() -> { initDatabase();    latch.countDown(); });** _**// 3→2**_

**executor.submit(() -> { initCache();       latch.countDown(); });** _**// 2→1**_

**executor.submit(() -> { initMessageQueue(); latch.countDown(); });** _**// 1→0**_

**latch.await();** _**// main thread blocks until count reaches 0**_

**System.out.println("All services initialized. Starting application...");**

_**// With timeout:**_

**if (!latch.await(30, TimeUnit.SECONDS)) {**

**throw new StartupException("Services failed to initialize in 30 seconds");**

**}**

**CyclicBarrier — N threads wait for each other at a barrier point. Reusable.**

_**// Real-world: parallel computation in phases**_

_**// 4 threads process a matrix, then synchronize after each phase**_

**CyclicBarrier barrier = new CyclicBarrier(4, () -> {**

**System.out.println("All threads reached the barrier — merging results");**

**});**

**for (int i = 0; i < 4; i++) {**

**final int threadId = i;**

**executor.submit(() -> {**

**for (int phase = 0; phase < 3; phase++) {**

**processPhase(threadId, phase);**

**barrier.await();** _**// wait for all 4 threads to finish this phase**_

_**// barrier resets automatically for the next phase!**_

**}**

**});**

**}**

**Feature**

**CountDownLatch**

**CyclicBarrier**

**Reusable**

**No (one-time)**

**Yes (resets after each use)**

**Who waits**

**One thread waits for N events**

**N threads wait for each other**

**Action on complete**

**None**

**Optional Runnable**

**Decrement**

**Any thread can countDown()**

**Only participating threads await()**

**Semaphore — Limits concurrent access to a resource. Like a parking lot with N spots.**

_**// Real-world: database connection pool with max 10 connections**_

**Semaphore semaphore = new Semaphore(10);** _**// 10 permits**_

**public Connection getConnection() throws InterruptedException {**

**semaphore.acquire();** _**// blocks if 10 connections already in use**_

**try {**

**return pool.borrowConnection();**

**} catch (Exception e) {**

**semaphore.release();** _**// release permit if borrowing fails**_

**throw e;**

**}**

**}**

**public void releaseConnection(Connection conn) {**

**pool.returnConnection(conn);**

**semaphore.release();** _**// return permit**_

**}**

_**// tryAcquire — non-blocking**_

**if (semaphore.tryAcquire(5, TimeUnit.SECONDS)) {**

**try {**

_**// use resource**_

**} finally {**

**semaphore.release();**

**}**

**} else {**

**throw new ResourceUnavailableException("No connection available");**

**}**

### **Q23. What is Phaser? How is it different from CyclicBarrier?**

**Phaser (Java 7) is a more flexible version of CyclicBarrier that supports:**

*   **Dynamic party registration/deregistration**

*   **Multiple phases**

*   **Phase-specific actions**


**Phaser phaser = new Phaser(1);** _**// register self (the main thread)**_

**for (int i = 0; i < 3; i++) {**

**phaser.register();** _**// dynamically add participants**_

**executor.submit(() -> {**

_**// Phase 0: download data**_

**downloadData();**

**phaser.arriveAndAwaitAdvance();** _**// sync at end of phase 0**_

_**// Phase 1: process data**_

**processData();**

**phaser.arriveAndAwaitAdvance();** _**// sync at end of phase 1**_

_**// Phase 2: upload results**_

**uploadResults();**

**phaser.arriveAndDeregister();** _**// done — remove self from phaser**_

**});**

**}**

_**// Main thread waits for phase 0 to complete, then deregisters**_

**phaser.arriveAndAwaitAdvance();** _**// wait for phase 0**_

**phaser.arriveAndDeregister();**   _**// main thread exits**_

**PART 6: Atomic Classes & CAS**
--------------------------------

### **Q24. What are Atomic classes and how does CAS (Compare-And-Swap) work internally?**

**Atomic classes provide lock-free, thread-safe operations on single variables using CPU-level atomic instructions.**

**AtomicInteger counter = new AtomicInteger(0);**

**counter.incrementAndGet();**   _**// atomic: read + increment + write**_

**counter.decrementAndGet();**

**counter.addAndGet(5);**

**counter.compareAndSet(5, 10);** _**// set to 10 only if current value is 5**_

**counter.getAndUpdate(v -> v \* 2);** _**// atomic read-modify-write with lambda**_

**counter.updateAndGet(v -> v \* 2);**

**counter.accumulateAndGet(3, Integer::sum);**

**CAS (Compare-And-Swap) — the internal mechanism:**

**CAS(memory\_location, expected\_value, new\_value):**

**ATOMICALLY (at CPU hardware level):**

**if (memory\[location\] == expected\_value) {**

**memory\[location\] = new\_value;**

**return true;  // success**

**} else {**

**return false; // someone else changed it — retry**

**}**

**How incrementAndGet() works internally:**

_**// Simplified internal implementation**_

**public int incrementAndGet() {**

**while (true) {**

**int current = get();**           _**// read current value**_

**int next = current + 1;**        _**// compute new value**_

**if (compareAndSet(current, next)) {** _**// CAS: set if unchanged**_

**return next;**               _**// success!**_

**}**

_**// CAS failed — another thread changed it. Retry the loop (spin).**_

**}**

**}**

**CAS vs Locking:**

**Feature**

**CAS (Atomic)**

**Lock (synchronized)**

**Blocking**

**Non-blocking (spin)**

**Blocking (thread sleeps)**

**Overhead**

**Low (no context switch)**

**High (OS mutex, context switch)**

**Contention**

**Good for low-medium**

**Good for high contention**

**Starvation**

**Possible (spin loop)**

**Unlikely with fair lock**

**Complexity**

**Simple single-variable ops**

**Complex multi-variable ops**

**ABA problem:**

**Thread 1: reads value A**

**Thread 2: changes A → B → A**

**Thread 1: CAS succeeds (value is still A) — but it CHANGED in between!**

**Solution: AtomicStampedReference — includes a version stamp**

**AtomicStampedReference ref = new AtomicStampedReference<>("A", 0);**

**int\[\] stampHolder = new int\[1\];**

**String current = ref.get(stampHolder); // value="A", stamp=0**

**ref.compareAndSet("A", "B", 0, 1);    // check both value AND stamp**

**All atomic classes:**

**AtomicInteger, AtomicLong, AtomicBoolean**    _**// primitives**_

**AtomicReference**                           _**// object reference**_

**AtomicIntegerArray, AtomicLongArray**          _**// arrays**_

**AtomicStampedReference**                    _**// solves ABA problem**_

**AtomicMarkableReference**                   _**// reference + boolean mark**_

**LongAdder, DoubleAdder**                       _**// high-throughput counters (Java 8)**_

**LongAccumulator, DoubleAccumulator**           _**// generalized accumulation (Java 8)**_

### **Q25. What is LongAdder and why is it faster than AtomicLong?**

**Problem with AtomicLong: Under high contention, many threads spin on the same memory location, causing cache-line contention.LongAdder solution: Distributes updates across multiple cells, then sums them when the value is read.**

**AtomicLong (single counter):**

**Thread1 → CAS(counter) → retry → retry → success**

**Thread2 → CAS(counter) → retry → retry → retry → success**

**Thread3 → CAS(counter) → retry → success**

**(all 3 fight over the same memory location)**

**LongAdder (striped cells):**

**Thread1 → CAS(cell\[0\]) → success          (no contention!)**

**Thread2 → CAS(cell\[1\]) → success          (different cell!)**

**Thread3 → CAS(cell\[2\]) → success          (different cell!)**

**sum() = cell\[0\] + cell\[1\] + cell\[2\]       (only computed on read)**

**LongAdder counter = new LongAdder();**

_**// From multiple threads (fast — no contention)**_

**counter.increment();**

**counter.add(5);**

_**// Read the total (slightly slower — must sum all cells)**_

**long total = counter.sum();**

**Feature**

**AtomicLong**

**LongAdder**

**Write performance (high contention)**

**Moderate (CAS spin)**

**Much faster**

**Read performance**

**Fast (get())**

**Slightly slower (sum())**

**Memory**

**Less**

**More (multiple cells)**

**Use case**

**Low contention, exact reads**

**High contention, rare reads**

**Examples**

**Sequence generators**

**Request counters, metrics**

**PART 7: Concurrent Collections**
----------------------------------

### **Q26. What are the key concurrent collections and when to use each?**

_**// 1. ConcurrentHashMap — thread-safe map with fine-grained locking**_

**ConcurrentHashMap map = new ConcurrentHashMap<>();**

**map.put("key", 1);**

**map.computeIfAbsent("key", k -> expensiveCompute());** _**// atomic**_

**map.merge("key", 1, Integer::sum);**                     _**// atomic**_

_**// 2. CopyOnWriteArrayList — snapshot-based list (read-heavy, write-rare)**_

**CopyOnWriteArrayList listeners = new CopyOnWriteArrayList<>();**

**listeners.add("listener1");**       _**// copies entire array!**_

**listeners.forEach(l -> notify(l));** _**// safe iteration, no lock**_

_**// 3. CopyOnWriteArraySet — same idea for Set**_

**CopyOnWriteArraySet set = new CopyOnWriteArraySet<>();**

_**// 4. ConcurrentLinkedQueue — lock-free FIFO queue (CAS-based)**_

**ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue<>();**

**queue.offer(task);**

**Task next = queue.poll();**

_**// 5. ConcurrentLinkedDeque — lock-free double-ended queue**_

**ConcurrentLinkedDeque deque = new ConcurrentLinkedDeque<>();**

_**// 6. ConcurrentSkipListMap — sorted concurrent map (like ConcurrentTreeMap)**_

**ConcurrentSkipListMap sortedMap = new ConcurrentSkipListMap<>();**

_**// 7. ConcurrentSkipListSet — sorted concurrent set**_

**ConcurrentSkipListSet sortedSet = new ConcurrentSkipListSet<>();**

### **Q27. What are BlockingQueue implementations and how do they work?**

**BlockingQueue — A queue that blocks when you try to take() from an empty queue or put() on a full queue. It's the foundation of the Producer-Consumer pattern.**

_**// Key methods:**_

_**// put(e)  — blocks if queue is full (waits for space)**_

_**// take()  — blocks if queue is empty (waits for element)**_

_**// offer(e, timeout) — blocks with timeout**_

_**// poll(timeout)     — blocks with timeout**_

**Implementations:**

_**// 1. ArrayBlockingQueue — bounded, array-backed, fair option**_

**BlockingQueue queue = new ArrayBlockingQueue<>(100);**

_**// Fixed capacity 100. Blocks producer when full.**_

_**// 2. LinkedBlockingQueue — optionally bounded, linked-node based**_

**BlockingQueue queue = new LinkedBlockingQueue<>(1000);**  _**// bounded**_

**BlockingQueue queue = new LinkedBlockingQueue<>();**       _**// unbounded (Integer.MAX\_VALUE)**_

_**// 3. PriorityBlockingQueue — unbounded, priority-ordered**_

**BlockingQueue queue = new PriorityBlockingQueue<>();**

_**// Elements must be Comparable or provide Comparator**_

_**// 4. SynchronousQueue — zero capacity! Handoff mechanism**_

**BlockingQueue queue = new SynchronousQueue<>();**

_**// put() blocks until another thread calls take()**_

_**// Used by CachedThreadPool internally**_

_**// 5. DelayQueue — elements available only after a delay**_

**BlockingQueue queue = new DelayQueue<>();**

**class DelayedTask implements Delayed {**

**private final long executeAt;**

**public long getDelay(TimeUnit unit) {**

**return unit.convert(executeAt - System.currentTimeMillis(), TimeUnit.MILLISECONDS);**

**}**

**public int compareTo(Delayed o) {**

**return Long.compare(this.getDelay(MILLISECONDS), o.getDelay(MILLISECONDS));**

**}**

**}**

**Producer-Consumer pattern:**

**BlockingQueue orderQueue = new ArrayBlockingQueue<>(100);**

_**// Producer thread**_

**class OrderProducer implements Runnable {**

**public void run() {**

**while (true) {**

**Order order = receiveOrderFromKafka();**

**orderQueue.put(order);** _**// blocks if queue full — natural back-pressure!**_

**}**

**}**

**}**

_**// Consumer thread**_

**class OrderConsumer implements Runnable {**

**public void run() {**

**while (true) {**

**Order order = orderQueue.take();** _**// blocks if queue empty — waits for work**_

**processOrder(order);**

**}**

**}**

**}**

_**// Start 1 producer, 5 consumers**_

**executor.submit(new OrderProducer());**

**for (int i = 0; i < 5; i++) {**

**executor.submit(new OrderConsumer());**

**}**

**PART 8: Fork/Join Framework**
-------------------------------

### **Q28. What is the Fork/Join Framework and how does it work internally?**

**Fork/Join is designed for divide-and-conquer parallelism — split a big task into smaller sub-tasks, process in parallel, then combine results.**

**\[Sort 1M elements\]**

**/        \\**

**\[Sort 500K\]          \[Sort 500K\]       ← FORK (split)**

**/     \\               /     \\**

**\[Sort 250K\] \[Sort 250K\] \[Sort 250K\] \[Sort 250K\]**

**\\     /               \\     /**

**\[Merge 500K\]           \[Merge 500K\]    ← JOIN (combine)**

**\\        /**

**\[Merge 1M elements\]**

**Internal mechanism — Work Stealing:**

**Thread 1 Deque: \[Task A, Task B, Task C\]   ← thread works from the TOP (LIFO)**

**Thread 2 Deque: \[Task D\]**

**Thread 3 Deque: \[\]                          ← IDLE! Steals from BOTTOM of Thread 1's deque**

**Thread 4 Deque: \[Task E, Task F\]**

**Thread 3 STEALS Task C from Thread 1's deque (from the bottom = FIFO)**

**This minimizes contention — owner works from top, thief steals from bottom**

**Java implementation:**

**public class ParallelMergeSort extends RecursiveAction {**

**private final int\[\] array;**

**private final int start, end;**

**private static final int THRESHOLD = 1000;**

**public ParallelMergeSort(int\[\] array, int start, int end) {**

**this.array = array;**

**this.start = start;**

**this.end = end;**

**}**

**@Override**

**protected void compute() {**

**if (end - start <= THRESHOLD) {**

**Arrays.sort(array, start, end);** _**// small enough — sort directly**_

**return;**

**}**

**int mid = (start + end) / 2;**

**ParallelMergeSort left = new ParallelMergeSort(array, start, mid);**

**ParallelMergeSort right = new ParallelMergeSort(array, mid, end);**

**invokeAll(left, right);** _**// FORK both, wait for both to JOIN**_

**merge(array, start, mid, end);**

**}**

**}**

_**// Usage**_

**ForkJoinPool pool = new ForkJoinPool();** _**// default: number of CPU cores**_

**int\[\] data = generateRandomArray(1\_000\_000);**

**pool.invoke(new ParallelMergeSort(data, 0, data.length));**

**RecursiveTask (returns a value):**

**public class ParallelSum extends RecursiveTask {**

**private final long\[\] array;**

**private final int start, end;**

**private static final int THRESHOLD = 10\_000;**

**@Override**

**protected Long compute() {**

**if (end - start <= THRESHOLD) {**

**long sum = 0;**

**for (int i = start; i < end; i++) sum += array\[i\];**

**return sum;**

**}**

**int mid = (start + end) / 2;**

**ParallelSum left = new ParallelSum(array, start, mid);**

**ParallelSum right = new ParallelSum(array, mid, end);**

**left.fork();**                  _**// submit left to pool**_

**long rightResult = right.compute();** _**// compute right in current thread**_

**long leftResult = left.join();**      _**// wait for left's result**_

**return leftResult + rightResult;**

**}**

**}**

**ForkJoinPool pool = new ForkJoinPool();**

**long total = pool.invoke(new ParallelSum(array, 0, array.length));**

**PART 9: CompletableFuture & Async Patterns**
----------------------------------------------

### **Q29. How does CompletableFuture relate to Threads?**

_**// CompletableFuture uses threads under the hood**_

_**// Default: ForkJoinPool.commonPool()**_

**CompletableFuture future = CompletableFuture.supplyAsync(() -> {**

**System.out.println("Running on: " + Thread.currentThread().getName());**

_**// Output: Running on: ForkJoinPool.commonPool-worker-1**_

**return fetchData();**

**});**

_**// Custom executor (recommended for I/O operations):**_

**ExecutorService ioPool = Executors.newFixedThreadPool(20);**

**CompletableFuture future2 = CompletableFuture**

**.supplyAsync(() -> callExternalAPI(), ioPool)**      _**// I/O thread**_

**.thenApplyAsync(data -> transform(data), ioPool)**   _**// another I/O thread**_

**.thenApply(result -> format(result));**               _**// may run on calling thread**_

_**// Why custom pool for I/O?**_

_**// ForkJoinPool has only N-1 threads (N = CPU cores)**_

_**// I/O operations BLOCK threads (waiting for response)**_

_**// → Common pool gets exhausted → affects ALL parallelStream() and CompletableFuture in the JVM!**_

### **Q30. What is the Producer-Consumer pattern? Implement it with modern Java.**

_**// Implementation 1: Using BlockingQueue (classic)**_

**public class OrderProcessingSystem {**

**private final BlockingQueue queue = new ArrayBlockingQueue<>(1000);**

**private final ExecutorService producers = Executors.newFixedThreadPool(2);**

**private final ExecutorService consumers = Executors.newFixedThreadPool(10);**

**private volatile boolean running = true;**

**public void start() {**

_**// Producers**_

**for (int i = 0; i < 2; i++) {**

**producers.submit(() -> {**

**while (running) {**

**Order order = receiveFromKafka();**

**queue.put(order);** _**// blocks if queue full**_

**}**

**});**

**}**

_**// Consumers**_

**for (int i = 0; i < 10; i++) {**

**consumers.submit(() -> {**

**while (running) {**

**Order order = queue.poll(1, TimeUnit.SECONDS);** _**// timeout avoids hanging on shutdown**_

**if (order != null) {**

**processOrder(order);**

**}**

**}**

**});**

**}**

**}**

**public void shutdown() {**

**running = false;**

**producers.shutdown();**

**consumers.shutdown();**

**}**

**}**

_**// Implementation 2: Using Exchanger (direct thread-to-thread handoff)**_

**Exchanger\> exchanger = new Exchanger<>();**

_**// Producer fills a batch, then exchanges with consumer**_

**Thread producer = new Thread(() -> {**

**List batch = new ArrayList<>();**

**while (true) {**

**batch.add(receiveOrder());**

**if (batch.size() >= 100) {**

**batch = exchanger.exchange(batch);** _**// swap with consumer's empty list**_

**batch.clear();**

**}**

**}**

**});**

_**// Consumer processes the batch, gives empty list back**_

**Thread consumer = new Thread(() -> {**

**List batch = new ArrayList<>();**

**while (true) {**

**batch = exchanger.exchange(batch);** _**// get producer's full list**_

**batch.forEach(this::processOrder);**

**batch.clear();**

**}**

**});**

**PART 10: Real-World Scenario-Based Questions**
------------------------------------------------

### **Q31. Design a thread-safe Singleton.**

_**// Method 1: Double-Checked Locking (most common in interviews)**_

**public class Singleton {**

**private static volatile Singleton instance;** _**// volatile prevents instruction reordering**_

**private Singleton() {}**

**public static Singleton getInstance() {**

**if (instance == null) {**                    _**// first check (no lock)**_

**synchronized (Singleton.class) {**

**if (instance == null) {**            _**// second check (with lock)**_

**instance = new Singleton();**

**}**

**}**

**}**

**return instance;**

**}**

**}**

_**// Why volatile? Without it, instruction reordering might let another thread**_

_**// see a partially constructed object!**_

_**// new Singleton() is actually:**_

_**//   1. Allocate memory**_

_**//   2. Initialize object**_

_**//   3. Assign reference to 'instance'**_

_**// Without volatile, JVM might reorder to 1→3→2**_

_**// Another thread sees non-null instance but uninitialized object!**_

_**// Method 2: Bill Pugh (holder pattern — simplest, laziest)**_

**public class Singleton {**

**private Singleton() {}**

**private static class Holder {**

**private static final Singleton INSTANCE = new Singleton();**

**}**

**public static Singleton getInstance() {**

**return Holder.INSTANCE;** _**// class loaded only when first accessed → lazy + thread-safe**_

**}**

**}**

_**// Method 3: Enum (effective Java — serialization-safe, reflection-safe)**_

**public enum Singleton {**

**INSTANCE;**

**public void doSomething() {** _**/\* ... \*/**_ **}**

**}**

### **Q32. Design a thread-safe Bounded Buffer (Ring Buffer).**

**public class BoundedBuffer {**

**private final Object\[\] buffer;**

**private int head, tail, count;**

**private final ReentrantLock lock = new ReentrantLock();**

**private final Condition notFull = lock.newCondition();**

**private final Condition notEmpty = lock.newCondition();**

**public BoundedBuffer(int capacity) {**

**buffer = new Object\[capacity\];**

**}**

**public void put(T item) throws InterruptedException {**

**lock.lock();**

**try {**

**while (count == buffer.length) {**

**notFull.await();** _**// wait until space is available**_

**}**

**buffer\[tail\] = item;**

**tail = (tail + 1) % buffer.length;**

**count++;**

**notEmpty.signal();** _**// wake up a consumer**_

**} finally {**

**lock.unlock();**

**}**

**}**

**@SuppressWarnings("unchecked")**

**public T take() throws InterruptedException {**

**lock.lock();**

**try {**

**while (count == 0) {**

**notEmpty.await();** _**// wait until data is available**_

**}**

**T item = (T) buffer\[head\];**

**buffer\[head\] = null;**

**head = (head + 1) % buffer.length;**

**count--;**

**notFull.signal();** _**// wake up a producer**_

**return item;**

**} finally {**

**lock.unlock();**

**}**

**}**

**}**

**Why two Conditions instead of wait/notifyAll?**

*   **With wait()/notifyAll(), waking up a producer might accidentally wake another producer (not a consumer) — wasted context switch.**

*   **With separate Condition objects, notFull.signal() wakes only producers, notEmpty.signal() wakes only consumers — precise signaling.**


### **Q33. How would you implement a rate limiter using threads?**

**public class TokenBucketRateLimiter {**

**private final int maxTokens;**

**private final double refillRate;** _**// tokens per second**_

**private double currentTokens;**

**private long lastRefillTime;**

**private final ReentrantLock lock = new ReentrantLock();**

**public TokenBucketRateLimiter(int maxTokens, double refillRate) {**

**this.maxTokens = maxTokens;**

**this.refillRate = refillRate;**

**this.currentTokens = maxTokens;**

**this.lastRefillTime = System.nanoTime();**

**}**

**public boolean tryAcquire() {**

**lock.lock();**

**try {**

**refill();**

**if (currentTokens >= 1.0) {**

**currentTokens -= 1.0;**

**return true;**

**}**

**return false;**

**} finally {**

**lock.unlock();**

**}**

**}**

**public void acquire() throws InterruptedException {**

**while (!tryAcquire()) {**

**Thread.sleep(10);** _**// back off and retry**_

**}**

**}**

**private void refill() {**

**long now = System.nanoTime();**

**double elapsed = (now - lastRefillTime) / 1\_000\_000\_000.0;**

**currentTokens = Math.min(maxTokens, currentTokens + elapsed \* refillRate);**

**lastRefillTime = now;**

**}**

**}**

_**// Usage: allow 100 requests per second**_

**TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(100, 100);**

**if (limiter.tryAcquire()) {**

**processRequest();**

**} else {**

**return Response.status(429).entity("Too Many Requests").build();**

**}**

### **Q34. What is a Thread-Safe Cache with expiration?**

**public class ExpiringCache {**

**private final ConcurrentHashMap\> map = new ConcurrentHashMap<>();**

**private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();**

**record CacheEntry(V value, long expiresAt) {**

**boolean isExpired() {**

**return System.currentTimeMillis() > expiresAt;**

**}**

**}**

**public ExpiringCache() {**

**cleaner.scheduleAtFixedRate(this::evictExpired, 1, 1, TimeUnit.MINUTES);**

**}**

**public void put(K key, V value, Duration ttl) {**

**long expiresAt = System.currentTimeMillis() + ttl.toMillis();**

**map.put(key, new CacheEntry<>(value, expiresAt));**

**}**

**public Optional get(K key) {**

**CacheEntry entry = map.get(key);**

**if (entry == null || entry.isExpired()) {**

**map.remove(key);**

**return Optional.empty();**

**}**

**return Optional.of(entry.value());**

**}**

**public V getOrCompute(K key, Function loader, Duration ttl) {**

**return get(key).orElseGet(() -> {**

**V value = loader.apply(key);**

**put(key, value, ttl);**

**return value;**

**});**

**}**

**private void evictExpired() {**

**map.entrySet().removeIf(e -> e.getValue().isExpired());**

**}**

**public void shutdown() {**

**cleaner.shutdown();**

**}**

**}**

_**// Usage**_

**ExpiringCache cache = new ExpiringCache<>();**

**UserProfile profile = cache.getOrCompute(**

**userId,**

**id -> userService.fetchProfile(id),**

**Duration.ofMinutes(10)**

**);**

**PART 11: Tricky Interview Questions**
---------------------------------------

### **Q35. What happens when a thread throws an unhandled exception?**

_**// The thread terminates silently! The exception is NOT propagated to the parent thread.**_

**Thread t = new Thread(() -> {**

**throw new RuntimeException("Boom!");**

**});**

**t.start();**

_**// Main thread has NO idea the exception happened**_

_**// Solution 1: UncaughtExceptionHandler**_

**t.setUncaughtExceptionHandler((thread, exception) -> {**

**System.err.println("Thread " + thread.getName() + " crashed: " + exception.getMessage());**

_**// log it, alert, restart, etc.**_

**});**

_**// Solution 2: Default handler for ALL threads**_

**Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {**

**logger.error("Uncaught exception in thread: " + thread.getName(), exception);**

**});**

_**// With ExecutorService — exceptions are captured in the Future**_

**Future future = executor.submit(() -> {**

**throw new RuntimeException("Boom!");**

**});**

**try {**

**future.get();** _**// exception surfaces HERE**_

**} catch (ExecutionException e) {**

**System.err.println("Task failed: " + e.getCause().getMessage());**

**}**

### **Q36. What is Thread Interruption? How does it work?**

**Interruption is a cooperative mechanism — one thread requests another to stop, but the target thread must check and respond.**

**Thread worker = new Thread(() -> {**

**while (!Thread.currentThread().isInterrupted()) {**

**try {**

_**// do work...**_

**Thread.sleep(1000);** _**// or any blocking call**_

**} catch (InterruptedException e) {**

_**// sleep/wait/join throws InterruptedException when thread is interrupted**_

**System.out.println("Interrupted! Cleaning up...");**

**Thread.currentThread().interrupt();** _**// restore interrupt status!**_

**break;**

**}**

**}**

**System.out.println("Thread exiting gracefully");**

**});**

**worker.start();**

**Thread.sleep(5000);**

**worker.interrupt();** _**// REQUEST the thread to stop**_

**Why restore interrupt status?**

_**// When catch InterruptedException, the interrupt flag is CLEARED**_

_**// If you don't restore it, code higher up the stack won't know**_

_**// the thread was interrupted**_

**try {**

**Thread.sleep(1000);**

**} catch (InterruptedException e) {**

**Thread.currentThread().interrupt();** _**// IMPORTANT: restore the flag**_

_**// Now caller can check isInterrupted() and act accordingly**_

**}**

**Methods that respond to interruption:**

*   **Thread.sleep()**

*   **Object.wait()**

*   **Thread.join()**

*   **BlockingQueue.put()/take()**

*   **Lock.lockInterruptibly()**

*   **Future.get()**


### **Q37. What is the difference between Daemon and Non-Daemon threads?**

**Thread daemon = new Thread(() -> {**

**while (true) {**

**System.out.println("Background task...");**

**Thread.sleep(1000);**

**}**

**});**

**daemon.setDaemon(true);** _**// must set BEFORE start()**_

**daemon.start();**

_**// When all non-daemon threads finish, JVM exits**_

_**// Daemon threads are killed automatically — no graceful shutdown!**_

**Feature**

**Non-Daemon (User Thread)**

**Daemon Thread**

**JVM shutdown**

**JVM waits for all user threads to finish**

**JVM kills daemon threads on exit**

**Default**

**All threads are non-daemon by default**

**Must explicitly set setDaemon(true)**

**Examples**

**Main thread, your business logic threads**

**GC thread, JMX threads, timer threads**

**Cleanup**

**Runs finally blocks**

**No guarantee finally blocks run**

_**// Real-world: background cleanup task**_

**Thread cleanupThread = new Thread(() -> {**

**while (true) {**

**cleanupTempFiles();**

**Thread.sleep(60\_000);**

**}**

**});**

**cleanupThread.setDaemon(true);** _**// won't prevent JVM from shutting down**_

**cleanupThread.start();**

### **Q38. What is the happens-before relationship in the Java Memory Model?**

**The Java Memory Model (JMM) defines which memory operations are visible to which threads. The happens-before relationship guarantees ordering and visibility.Key happens-before rules:**

**1\. Program order:     Within a single thread, each action happens-before the next**

**2\. Monitor lock:      unlock() happens-before subsequent lock() on the same monitor**

**3\. volatile:          write to volatile happens-before subsequent read of same volatile**

**4\. Thread start:      t.start() happens-before any action in thread t**

**5\. Thread join:       All actions in thread t happen-before t.join() returns**

**6\. Thread interrupt:  interrupt() happens-before the interrupted thread detects it**

**7\. Transitivity:      If A happens-before B, and B happens-before C, then A happens-before C**

_**// Example: volatile establishes happens-before**_

**volatile boolean ready = false;**

**int data = 0;**

_**// Thread A:**_

**data = 42;**          _**// (1)**_

**ready = true;**       _**// (2) volatile write — flushes ALL previous writes**_

_**// Thread B:**_

**if (ready) {**        _**// (3) volatile read — sees ALL writes before the volatile write**_

**print(data);**    _**// (4) guaranteed to see 42!**_

**}**

_**// Because (2) happens-before (3), and (1) happens-before (2),**_

_**// by transitivity (1) happens-before (4) — data=42 is visible**_

### **Q39. What are Virtual Threads (Java 21)? How do they change threading?**

**Virtual Threads (Project Loom) are lightweight threads managed by the JVM, not the OS.**

**Platform Threads (traditional):**

**1 Java Thread = 1 OS Thread**

**~1MB stack each**

**~10K max practical limit**

**Blocking I/O blocks the OS thread**

**Virtual Threads (Java 21+):**

**1 Virtual Thread ≈ few KB of stack**

**Millions of virtual threads possible**

**Blocking I/O only blocks the virtual thread, not the carrier (OS) thread**

_**// Creating virtual threads (Java 21+)**_

**Thread vt = Thread.ofVirtual().start(() -> {**

**System.out.println("Running on virtual thread: " + Thread.currentThread());**

**});**

_**// With executor — creates a new virtual thread per task**_

**try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {**

**for (int i = 0; i < 1\_000\_000; i++) {** _**// 1 MILLION concurrent tasks!**_

**executor.submit(() -> {**

**Thread.sleep(Duration.ofSeconds(1));** _**// blocks virtual thread, not OS thread**_

**return fetchFromDatabase();**

**});**

**}**

**}**

_**// Structured concurrency (Preview, Java 21+)**_

**try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {**

**Future userFuture = scope.fork(() -> fetchUser(id));**

**Future\> ordersFuture = scope.fork(() -> fetchOrders(id));**

**scope.join();**            _**// wait for both**_

**scope.throwIfFailed();**   _**// propagate errors**_

**return new Dashboard(userFuture.resultNow(), ordersFuture.resultNow());**

**}**

**Platform Threads vs Virtual Threads:**

**Feature**

**Platform Threads**

**Virtual Threads**

**Cost**

**~1MB stack, OS-managed**

**~few KB, JVM-managed**

**Max count**

**~10K practical**

**Millions**

**Blocking I/O**

**Blocks OS thread**

**Unmounts from OS thread**

**Scheduling**

**OS scheduler**

**JVM scheduler (mounts on carrier threads)**

**Thread pools**

**Required (expensive to create)**

**Not needed (cheap to create)**

**Best for**

**CPU-intensive tasks**

**I/O-intensive tasks**

### **Q40. How to calculate the optimal thread pool size?**

**For CPU-bound tasks:**

**Optimal threads = Number of CPU cores**

**= Runtime.getRuntime().availableProcessors()**

**int cpuCores = Runtime.getRuntime().availableProcessors();** _**// e.g., 8**_

**ExecutorService cpuPool = Executors.newFixedThreadPool(cpuCores);**

**For I/O-bound tasks (waiting for network, disk, DB):**

**Optimal threads = CPU cores × (1 + Wait time / Compute time)**

**Example: 8 cores, 200ms wait (API call), 20ms compute**

**= 8 × (1 + 200/20) = 8 × 11 = 88 threads**

**Example: 8 cores, 500ms wait (DB query), 5ms compute**

**= 8 × (1 + 500/5) = 8 × 101 = 808 threads**

**int cpuCores = Runtime.getRuntime().availableProcessors();**

**double waitTime = 200;** _**// ms (average I/O wait)**_

**double computeTime = 20;** _**// ms (average processing)**_

**int optimalThreads = (int) (cpuCores \* (1 + waitTime / computeTime));**

**ExecutorService ioPool = new ThreadPoolExecutor(**

**optimalThreads / 2,**    _**// core**_

**optimalThreads,**         _**// max**_

**60L, TimeUnit.SECONDS,**

**new LinkedBlockingQueue<>(1000)**

**);**

**Real-world configuration (Spring Boot):**

**\# Tomcat thread pool (handles HTTP requests — I/O bound)**

**server.tomcat.threads.min-spare=20**

**server.tomcat.threads.max=200**

**\# Async task executor**

**spring.task.execution.pool.core-size=10**

**spring.task.execution.pool.max-size=50**

**spring.task.execution.pool.queue-capacity=500**

**The Producer–Consumer problem (often called** _**Publisher–Consumer**_**) is a classic concept in concurrent programming where one part of a system produces data and another part consumes it, typically using a shared buffer or queue.**

**🧠 Core Idea**
----------------

*   **Producer (Publisher) → Generates data**

*   **Consumer → Uses that data**

*   **Shared Buffer → A queue that connects them**


**Think of it like:**

**A kitchen (producer) prepares food → puts it on a counter (buffer) → waiter (consumer) picks it up.**

**⚠️ The Problem**
------------------

**When multiple threads are involved:**

*   **What if producer adds data when buffer is full? ❌**

*   **What if consumer tries to consume when buffer is empty? ❌**

*   **What if both access buffer at the same time? ❌**


**👉 This leads to:**

*   **Race conditions**

*   **Data inconsistency**

*   **Thread conflicts**


**✅ Solution Concept**
----------------------

**We solve it using:**

*   **Synchronization (locks / mutex)**

*   **Wait & notify mechanism**

*   **Bounded buffer (fixed size queue)**


**🔄 Flow Explanation**
-----------------------

1.  **Producer checks → if buffer is not full**

2.  **Adds item → notifies consumer**

3.  **Consumer checks → if buffer is not empty**

4.  **Removes item → notifies producer**


**💻 Java Example (Simple)**
----------------------------

**import java.util.LinkedList;**

**import java.util.Queue;**

**class SharedBuffer {**

**private Queue queue = new LinkedList<>();**

**private int capacity = 5;**

**public synchronized void produce(int value) throws InterruptedException {**

**while (queue.size() == capacity) {**

**wait(); // wait if buffer is full**

**}**

**queue.add(value);**

**System.out.println("Produced: " + value);**

**notify(); // notify consumer**

**}**

**public synchronized void consume() throws InterruptedException {**

**while (queue.isEmpty()) {**

**wait(); // wait if buffer is empty**

**}**

**int value = queue.poll();**

**System.out.println("Consumed: " + value);**

**notify(); // notify producer**

**}**

**}**

**public class Main {**

**public static void main(String\[\] args) {**

**SharedBuffer buffer = new SharedBuffer();**

**// Producer Thread**

**new Thread(() -> {**

**int i = 0;**

**while (true) {**

**try {**

**buffer.produce(i++);**

**Thread.sleep(500);**

**} catch (InterruptedException e) {}**

**}**

**}).start();**

**// Consumer Thread**

**new Thread(() -> {**

**while (true) {**

**try {**

**buffer.consume();**

**Thread.sleep(1000);**

**} catch (InterruptedException e) {}**

**}**

**}).start();**

**}**

**}**

**🔑 Key Concepts**
-------------------

*   **wait() → releases lock and pauses thread**

*   **notify() → wakes up waiting thread**

*   **synchronized → ensures only one thread accesses buffer**


**🚀 Real-World Examples**
--------------------------

*   **Messaging systems (Kafka, RabbitMQ)**

*   **Video streaming buffers**

*   **Print queue systems**

*   **Web servers handling requests**


**🔥 Advanced Version**
-----------------------

**Instead of manual wait/notify:**

*   **Use BlockingQueue (recommended in real projects)**


**BlockingQueue queue = new ArrayBlockingQueue<>(5);**

**👉 It automatically handles:**

*   **Thread safety**

*   **Waiting**

*   **Notification**


**🧩 Summary**
--------------

*   **Producer generates → Consumer uses**

*   **Buffer connects them**

*   **Need synchronization to avoid errors**

*   **Use wait() / notify() or BlockingQueue**


**If you want, I can also:**

*   **Show multiple producers & consumers**

*   **Explain with real system design (Kafka-like)**

*   **Or give interview answers + tricky questionsGood question — and here’s the key thing to understand first:**


**👉 A single thread does NOT “handle more records” during peak time👉 Instead, the system scales using multiple threads + async processing + queues**

**Let’s break this down in a real Spring Boot microservices context 👇**

**🧠 1. What happens during peak load?**
========================================

**Imagine:**

*   **10 users → easy**

*   **10,000 users → peak traffic**


**If you use only 1 thread:❌ Requests will queue up → slow → timeout**

**So Java handles it using:**

**⚙️ 2. Thread Pool (Core Concept)**
====================================

**Spring Boot (via Tomcat/Netty) uses a Thread Pool**

**👉 Instead of creating new threads:**

*   **It reuses threads**

*   **Handles multiple requests concurrently**


**🔄 Flow**
-----------

1.  **Request comes in**

2.  **Assigned to a thread from pool**

3.  **Processed**

4.  **Thread returned to pool**


**💻 Example Config (Spring Boot)**
-----------------------------------

*   **server.tomcat.threads.max=200**

*   **server.tomcat.threads.min-spare=10**


**👉 At peak → up to 200 concurrent requests handled**

**🚀 3. Async Processing (Non-blocking)**
=========================================

**If your thread waits (DB call, API call):**

**❌ Thread gets blocked → waste**

**👉 Solution: Async processing**

### **💻 Example**

*   **@Async**

*   **public CompletableFuture process() {**

*       **// long-running task**

*       **return CompletableFuture.completedFuture("done");**

*   **}**


**👉 Thread is freed → handles other requests**

**📦 4. Queue-Based Handling (VERY IMPORTANT)**
===============================================

**For heavy load systems → don’t process everything instantly**

**👉 Use queue (Kafka / RabbitMQ)**

**🔄 Flow**
-----------

*   **Request received**

*   **Put into queue**

*   **Worker threads consume gradually**


**👉 This is Producer–Consumer pattern (you asked earlier 👌)**

**Example tools:**
------------------

*   **Apache Kafka**

*   **RabbitMQ**


**⚡ 5. Horizontal Scaling (Microservices Power)**
=================================================

**If 1 instance can’t handle load:**

**👉 Run multiple instances**

**Example:**

*   **1 service → 200 threads**

*   **5 services → 1000 concurrent requests**


**Tools:**
----------

*   **Docker**

*   **Kubernetes**


**🧩 6. Database Optimization (Hidden Bottleneck)**
===================================================

**Even if threads are many:👉 DB can become slow**

**Solutions:**

*   **Connection pooling (HikariCP)**

*   **Read replicas**

*   **Caching (Redis)**


**🔥 7. Backpressure (Advanced but important)**
===============================================

**When system is overloaded:👉 Don’t accept unlimited requests**

**Instead:**

*   **Queue limit**

*   **Reject requests gracefully**

*   **Slow down producers**


**🧠 Real Interview Answer (Short Version)**
============================================

**“A single thread doesn’t handle peak load. In Spring Boot microservices, we use thread pools for concurrency, async processing to avoid blocking, message queues like Kafka for load buffering, and horizontal scaling with Kubernetes to handle high traffic efficiently.”**

**💡 Key Takeaways**
====================

*   **❌ Not about “one thread handling more”**

*   **✅ About:**

    *   **Thread pools**

    *   **Async processing**

    *   **Queues**

    *   **Scaling**


**If you want, I can go deeper into:👉 How thread pool tuning works (very important for interviews)👉 Difference between Tomcat vs Netty (Spring MVC vs WebFlux)👉 Or a real system design example (like handling 1M requests)**