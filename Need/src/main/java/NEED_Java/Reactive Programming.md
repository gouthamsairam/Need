PART 1: Fundamentals — Why, What, Where

Q1. What is Reactive Programming and why do we need it?

Reactive Programming is a declarative programming paradigm concerned with asynchronous data streams and the propagation of change. Instead of pulling data, you react to data as it arrives.The problem it solves:

TRADITIONAL (Blocking/Synchronous):

Thread-1 → call DB → \[WAITING 50ms...\] → got result → call API → \[WAITING 200ms...\] → got result → respond

Thread-2 → call DB → \[WAITING 50ms...\] → got result

Thread-3 → call API → \[WAITING 200ms...\] → got result

...

Thread-200 → call DB → \[WAITING...\] → No more threads available!

Thread-201 → REJECTED! Server overwhelmed.

Each thread: ~1MB stack × 200 threads = 200MB just for WAITING

Most of that time threads are IDLE, just waiting for I/O

REACTIVE (Non-Blocking/Asynchronous):

Thread-1 → call DB (non-blocking) → immediately freed → handles another request

↓ (when DB responds, a callback fires on any available thread)

→ call API (non-blocking) → immediately freed → handles another request

↓ (when API responds)

→ respond to client

4 threads handle THOUSANDS of concurrent requests!

Key comparison:

Aspect

Blocking (Servlet/Spring MVC)

Reactive (WebFlux/Reactor)

Thread model

1 thread per request

Few threads, many requests

Threads needed for 10K concurrent users

~10,000

~4-8 (CPU cores)

Memory

~10GB (10K × 1MB stack)

~50MB

I/O waiting

Thread blocked, wasted

Thread freed, reused

Throughput under load

Degrades (thread exhaustion)

Stays stable

CPU utilization

Low (threads mostly idle)

High (threads always busy)

Best for

CPU-intensive work

I/O-intensive (APIs, DB, messaging)

Q2. What is the Reactive Streams specification?

Reactive Streams is a standard (specification) for asynchronous stream processing with non-blocking backpressure. It was developed by engineers from Netflix, Lightbend, Pivotal, and others.The 4 interfaces (in org.reactivestreams / java.util.concurrent.Flow since Java 9):

public interface Publisher {

void subscribe(Subscriber subscriber);

}

public interface Subscriber {

void onSubscribe(Subscription subscription); // called first

void onNext(T item); // for each element

void onError(Throwable throwable); // on failure (terminal)

void onComplete(); // when done (terminal)

}

public interface Subscription {

void request(long n); // subscriber requests N items (BACKPRESSURE!)

void cancel(); // subscriber cancels the subscription

}

public interface Processor extends Subscriber, Publisher {

// both subscriber and publisher (transforms data)

}

The flow:

Publisher Subscriber

│ │

│◄──── subscribe(subscriber) ──────│

│ │

│──── onSubscribe(subscription) ──►│

│ │

│◄──── request(3) ────────────────│ ← subscriber asks for 3 items

│ │

│──── onNext("item1") ───────────►│

│──── onNext("item2") ───────────►│

│──── onNext("item3") ───────────►│ ← sent exactly 3

│ │

│◄──── request(2) ────────────────│ ← subscriber asks for 2 more

│ │

│──── onNext("item4") ───────────►│

│──── onNext("item5") ───────────►│

│──── onComplete() ──────────────►│ ← no more items

Key rule: The Publisher MUST NOT send more items than the Subscriber has requested. This is backpressure — the consumer controls the rate.Implementations of Reactive Streams:

Library

Publisher types

Used by

Project Reactor

Mono, Flux

Spring WebFlux

RxJava

Single, Observable, Flowable

Android, Netflix

JDK 9 Flow

Flow.Publisher

Standard library

Mutiny

Uni, Multi

Quarkus

Q3. What is Project Reactor? What are Mono and Flux?

Project Reactor is the reactive library used by Spring WebFlux. It implements the Reactive Streams specification and provides two core types:Mono — Emits 0 or 1 element, then completes (or errors).

Mono: ────\[element\]────|

or ──────────────| (empty)

or ──────────────✗ (error)

Flux — Emits 0 to N elements, then completes (or errors).

Flux: ────\[1\]────\[2\]────\[3\]────\[4\]────|

or ────\[1\]────✗ (error after first element)

or ──────────────| (empty)

// Mono — single value (like Optional, but async)

Mono user = userRepository.findById(42); // 0 or 1 user

Mono name = Mono.just("Alice"); // exactly 1 value

Mono done = emailService.sendEmail(email); // no value, just completion signal

// Flux — multiple values (like Stream, but async + push-based)

Flux users = userRepository.findAll(); // 0 to N users

Flux numbers = Flux.just(1, 2, 3, 4, 5); // exactly 5 values

Flux lines = fileService.readLines("data.csv"); // stream of lines

Flux prices = stockService.streamPrices(); // infinite real-time stream

When to use which:

Scenario

Use

Why

Find user by ID

Mono

0 or 1 result

Save an entity

Mono

Returns the saved entity

Delete operation

Mono

No return value

Find all users

Flux

Multiple results

Real-time events

Flux

Continuous stream

API call returning a list

Mono\> or Flux

Mono if you need the whole list; Flux if you want streaming

Q4. What is the difference between Reactive (Mono/Flux) and CompletableFuture?

// CompletableFuture — async, but EAGER, single value

CompletableFuture future = CompletableFuture.supplyAsync(() -> fetchUser(id));

// ↑ Execution starts IMMEDIATELY when this line runs

// Mono — async, LAZY, single value

Mono mono = Mono.fromCallable(() -> fetchUser(id));

// ↑ NOTHING happens yet! Execution starts only when someone subscribes

mono.subscribe(user -> System.out.println(user)); // NOW it executes

Feature

CompletableFuture

Mono/Flux

Evaluation

Eager (runs immediately)

Lazy (runs on subscribe)

Values

Single value only

Mono: 0-1, Flux: 0-N

Backpressure

No

Yes (Reactive Streams)

Operators

Limited (thenApply, thenCompose)

Rich (100+ operators)

Cancellation

Difficult

Built-in (Disposable.dispose())

Error handling

exceptionally, handle

onErrorReturn, onErrorResume, retry

Thread model

ForkJoinPool by default

Schedulers (elastic, parallel, etc.)

Streaming

No (single future)

Yes (Flux streams elements)

// CompletableFuture: fetch user then fetch orders — composition

CompletableFuture dashboard = fetchUser(id)

.thenCompose(user -> fetchOrders(user.getId())

.thenApply(orders -> new Dashboard(user, orders)));

// Mono: same thing, but with richer operators

Mono dashboard = fetchUser(id)

.flatMap(user -> fetchOrders(user.getId())

.collectList()

.map(orders -> new Dashboard(user, orders)))

.timeout(Duration.ofSeconds(5))

.retry(3)

.onErrorReturn(Dashboard.empty());

PART 2: Creating Mono and Flux

Q5. What are all the ways to create a Mono?

// 1. From a value

Mono mono = Mono.just("hello");

// 2. Empty Mono (completes immediately with no value)

Mono empty = Mono.empty();

// 3. Error Mono

Mono error = Mono.error(new RuntimeException("Something went wrong"));

// 4. From a Callable/Supplier (lazy)

Mono lazy = Mono.fromCallable(() -> expensiveComputation());

Mono fromSupplier = Mono.fromSupplier(() -> generateValue());

// 5. From a CompletableFuture

Mono fromFuture = Mono.fromFuture(() -> httpClient.sendAsync(request));

// 6. From a Runnable (Mono)

Mono action = Mono.fromRunnable(() -> cleanupResources());

// 7. Deferred (creates a new Mono for each subscriber)

Mono deferred = Mono.defer(() -> Mono.just(System.currentTimeMillis()));

// Each subscriber gets a DIFFERENT timestamp

// 8. Delay

Mono delayed = Mono.delay(Duration.ofSeconds(3)); // emits 0 after 3 seconds

// 9. Never (never emits, never completes)

Mono never = Mono.never();

// 10. From Optional

Mono fromOpt = Mono.justOrEmpty(Optional.of("hello"));

Mono fromNull = Mono.justOrEmpty(null); // becomes Mono.empty()

// 11. Using create (programmatic, for bridging callback APIs)

Mono created = Mono.create(sink -> {

asyncCallbackApi(result -> {

if (result.isSuccess()) {

sink.success(result.getValue());

} else {

sink.error(result.getError());

}

});

});

Q6. What are all the ways to create a Flux?

// 1. From values

Flux flux = Flux.just("A", "B", "C");

// 2. From an Iterable/Collection

Flux fromList = Flux.fromIterable(List.of("A", "B", "C"));

// 3. From an array

Flux fromArray = Flux.fromArray(new String\[\]{"A", "B", "C"});

// 4. From a Stream (use defer to avoid reuse issues)

Flux fromStream = Flux.defer(() -> Flux.fromStream(Files.lines(path)));

// 5. Range

Flux range = Flux.range(1, 10); // 1, 2, 3, ..., 10

// 6. Interval (emits 0, 1, 2, ... at fixed intervals)

Flux ticker = Flux.interval(Duration.ofSeconds(1)); // every second

// 7. Empty / Error / Never

Flux empty = Flux.empty();

Flux error = Flux.error(new RuntimeException("fail"));

Flux never = Flux.never();

// 8. Generate (synchronous, stateful, one-at-a-time)

Flux fibonacci = Flux.generate(

() -> new long\[\]{0, 1}, // initial state

(state, sink) -> {

sink.next((int) state\[0\]);

long next = state\[0\] + state\[1\];

state\[0\] = state\[1\];

state\[1\] = next;

if (state\[0\] > 1000) sink.complete(); // stop condition

return state;

}

);

// 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987

// 9. Create (asynchronous, multi-threaded, bridging APIs)

Flux events = Flux.create(sink -> {

eventSource.onEvent(event -> sink.next(event)); // push events

eventSource.onComplete(() -> sink.complete());

eventSource.onError(err -> sink.error(err));

sink.onDispose(() -> eventSource.close()); // cleanup on cancel

});

// 10. Push (single-threaded variant of create)

Flux pushed = Flux.push(sink -> {

singleThreadedSource.register(sink::next);

});

// 11. Concat / Merge / Zip multiple sources

Flux concat = Flux.concat(flux1, flux2); // sequential: all of flux1, then flux2

Flux merge = Flux.merge(flux1, flux2); // interleaved: whichever emits first

Flux\> zipped = Flux.zip(flux1, flux2); // pairs elements

// 12. Using / Resource management

Flux lines = Flux.using(

() -> new BufferedReader(new FileReader("data.txt")), // resource supplier

reader -> Flux.fromStream(reader.lines()), // flux factory

reader -> { try { reader.close(); } catch (IOException e) {} } // cleanup

);

Q7. What is the difference between Mono.just() and Mono.defer()?

This is a critical interview question — it tests understanding of laziness.

// Mono.just() — value is computed IMMEDIATELY (eager)

Mono eager = Mono.just(System.currentTimeMillis());

// currentTimeMillis() runs RIGHT NOW, result is cached

eager.subscribe(t -> System.out.println(t)); // prints: 1709985600000

Thread.sleep(1000);

eager.subscribe(t -> System.out.println(t)); // prints: 1709985600000 ← SAME value!

// Mono.defer() — value is computed for EACH subscriber (lazy)

Mono lazy = Mono.defer(() -> Mono.just(System.currentTimeMillis()));

// nothing runs yet

lazy.subscribe(t -> System.out.println(t)); // prints: 1709985600000

Thread.sleep(1000);

lazy.subscribe(t -> System.out.println(t)); // prints: 1709985601000 ← DIFFERENT value!

When to use defer():

When the Mono creation involves side effects (time, random, state)

When wrapping blocking code that should be re-evaluated per subscription

When conditionally creating different Monos

// Real-world: conditional Mono

Mono findUser(String id) {

return Mono.defer(() -> {

if (cache.contains(id)) {

return Mono.just(cache.get(id));

} else {

return userRepository.findById(id);

}

});

}

PART 3: Core Operators — Transforming, Filtering, Combining

Q8. Explain map(), flatMap(), and flatMapMany() in Mono/Flux.

map() — Synchronous 1:1 transformation.

Mono name = Mono.just("alice");

Mono upper = name.map(String::toUpperCase); // "ALICE"

Flux numbers = Flux.just(1, 2, 3);

Flux doubled = numbers.map(n -> n \* 2); // 2, 4, 6

flatMap() — Asynchronous transformation that returns a reactive type.

// map() with an async operation — WRONG: gives Mono\>

Mono\> wrong = userId.map(id -> orderService.findLatestOrder(id));

// flatMap() — CORRECT: unwraps inner Mono → Mono

Mono correct = userId.flatMap(id -> orderService.findLatestOrder(id));

flatMap() on Flux — concurrent, unordered processing:

Flux users = Flux.just(1L, 2L, 3L, 4L, 5L)

.flatMap(id -> userService.findById(id)); // all 5 calls run CONCURRENTLY!

// Results arrive in whatever order they complete — NOT necessarily 1,2,3,4,5

// Control concurrency:

Flux users = Flux.range(1, 100)

.flatMap(id -> userService.findById(id), 10); // max 10 concurrent calls

flatMapSequential() — Concurrent execution but ordered results.

Flux ordered = Flux.just(1L, 2L, 3L)

.flatMapSequential(id -> userService.findById(id));

// Calls run concurrently, but results are emitted in original order (1, 2, 3)

concatMap() — Sequential execution, one at a time.

Flux sequential = Flux.just(1L, 2L, 3L)

.concatMap(id -> userService.findById(id));

// Calls run ONE AT A TIME: finish 1 → start 2 → finish 2 → start 3

Operator

Execution

Order

Use case

flatMap()

Concurrent

Unordered

Maximum throughput, order doesn't matter

flatMapSequential()

Concurrent

Ordered

Throughput + order

concatMap()

Sequential

Ordered

Dependencies between calls, rate limiting

flatMapMany() — Convert Mono to Flux.

// Mono\> → Flux

Mono\> ordersMono = orderService.getOrdersForUser(userId);

Flux ordersFlux = ordersMono.flatMapMany(Flux::fromIterable);

Q9. What are the key filtering and conditional operators?

Flux numbers = Flux.range(1, 20);

// filter — keep elements matching condition

Flux evens = numbers.filter(n -> n % 2 == 0); // 2, 4, 6, 8, ...

// filterWhen — async filter

Flux activeUsers = users.filterWhen(user -> isActiveAsync(user)); // returns Mono

// distinct — remove duplicates

Flux unique = Flux.just(1, 2, 2, 3, 3, 3).distinct(); // 1, 2, 3

// distinctUntilChanged — remove CONSECUTIVE duplicates

Flux deduped = Flux.just(1, 1, 2, 2, 1, 1).distinctUntilChanged(); // 1, 2, 1

// take / skip

Flux first5 = numbers.take(5); // 1, 2, 3, 4, 5

Flux skip5 = numbers.skip(5); // 6, 7, 8, ..., 20

Flux takeWhile = numbers.takeWhile(n -> n < 5); // 1, 2, 3, 4

Flux skipWhile = numbers.skipWhile(n -> n < 5); // 5, 6, 7, ..., 20

// takeLast / skipLast

Flux last3 = numbers.takeLast(3); // 18, 19, 20

// next — first element as Mono

Mono first = numbers.next(); // Mono(1)

// elementAt

Mono third = numbers.elementAt(2); // Mono(3) — 0-based index

// defaultIfEmpty / switchIfEmpty

Mono user = findUser(id)

.defaultIfEmpty(User.anonymous()); // provide default value

Mono user2 = findUser(id)

.switchIfEmpty(findInLegacySystem(id)); // fallback to another Mono

// hasElement / hasElements

Mono exists = userRepository.findById(id).hasElement();

Q10. How to combine multiple Mono/Flux sources?

Combining Monos:

Mono userMono = userService.findById(id);

Mono\> ordersMono = orderService.findByUserId(id);

Mono profileMono = profileService.findByUserId(id);

// zip — wait for ALL, combine results (parallel execution)

Mono dashboard = Mono.zip(userMono, ordersMono, profileMono)

.map(tuple -> new Dashboard(tuple.getT1(), tuple.getT2(), tuple.getT3()));

// zipWith — combine two Monos

Mono result = userMono

.zipWith(ordersMono)

.map(tuple -> new UserWithOrders(tuple.getT1(), tuple.getT2()));

// zipWhen — use result of first Mono to trigger second

Mono result2 = userMono

.zipWhen(user -> orderService.findByUserId(user.getId()))

.map(tuple -> new UserWithOrders(tuple.getT1(), tuple.getT2()));

Combining Fluxes:

Flux flux1 = Flux.just("A", "B", "C");

Flux flux2 = Flux.just(1, 2, 3);

// zip — pairs elements positionally

Flux zipped = Flux.zip(flux1, flux2)

.map(tuple -> tuple.getT1() + tuple.getT2()); // "A1", "B2", "C3"

// merge — interleave (concurrent, order depends on timing)

Flux merged = Flux.merge(

fetchFromServiceA(), // starts immediately

fetchFromServiceB() // starts immediately

); // results interleaved in completion order

// concat — sequential (all of first, then all of second)

Flux concatenated = Flux.concat(

fetchFromServiceA(), // all results first

fetchFromServiceB() // then all results

);

// combineLatest — emit when ANY source emits, combine with latest from other

Flux combined = Flux.combineLatest(

temperatureSensor,

humiditySensor,

(temp, humidity) -> "Temp: " + temp + ", Humidity: " + humidity

);

// firstWithSignal — use whichever source emits first (racing)

Flux fastest = Flux.firstWithSignal(

fetchFromCacheFlux(), // if cache has data, it wins

fetchFromDatabaseFlux() // otherwise database result is used

);

PART 4: Error Handling

Q11. How does error handling work in reactive streams?

Fundamental rule: An error is a terminal event — the stream stops. No more onNext() calls after onError().

Normal: ─\[1\]─\[2\]─\[3\]─|

Error: ─\[1\]─\[2\]─✗ (element 3 and beyond are never emitted)

Error handling operators:

Flux risky = Flux.just(1, 2, 0, 4)

.map(n -> 10 / n); // ArithmeticException at n=0

// 1. onErrorReturn — replace error with a static fallback value

Flux safe1 = risky.onErrorReturn(-1);

// emits: 10, 5, -1 (stops after error, emits -1 instead)

// 2. onErrorReturn with predicate

Flux safe2 = risky.onErrorReturn(

e -> e instanceof ArithmeticException, -1);

// 3. onErrorResume — replace error with a fallback Publisher

Flux safe3 = risky.onErrorResume(e -> {

log.warn("Error: " + e.getMessage());

return Flux.just(-1, -2, -3); // switch to fallback flux

});

// 4. onErrorMap — transform error to a different exception

Flux safe4 = risky.onErrorMap(

ArithmeticException.class,

e -> new BusinessException("Division by zero", e)

);

// 5. onErrorContinue — skip the failed element, continue with remaining (USE WITH CAUTION!)

Flux safe5 = Flux.just(1, 2, 0, 4, 5)

.map(n -> 10 / n)

.onErrorContinue((error, element) -> {

log.warn("Skipping element {} due to {}", element, error.getMessage());

});

// emits: 10, 5, 2 (skips 0, continues with 4 and 5... BUT behavior can be surprising!)

// 6. doOnError — side effect on error (logging) without handling it

Flux safe6 = risky

.doOnError(e -> log.error("Stream failed", e))

.onErrorReturn(-1);

// 7. retry — retry the entire sequence on error

Flux retried = riskyApiCall()

.retry(3); // retry up to 3 times

// 8. retryWhen — advanced retry with backoff

Flux resilient = callExternalApi()

.retryWhen(Retry.backoff(3, Duration.ofSeconds(1))

.maxBackoff(Duration.ofSeconds(30))

.filter(e -> e instanceof ServiceUnavailableException)

.onRetryExhaustedThrow((spec, signal) ->

new ServiceDownException("Service unavailable after 3 retries", signal.failure()))

);

Error handling in real-world service:

@Service

public class UserService {

public Mono getUserProfile(String userId) {

return userRepository.findById(userId)

.switchIfEmpty(Mono.error(new UserNotFoundException(userId)))

.flatMap(user -> enrichWithPreferences(user))

.timeout(Duration.ofSeconds(5))

.retryWhen(Retry.backoff(2, Duration.ofMillis(500))

.filter(e -> e instanceof TimeoutException))

.onErrorMap(TimeoutException.class,

e -> new ServiceUnavailableException("User service timed out"))

.doOnError(e -> metrics.incrementErrorCount("getUserProfile"));

}

}

Q12. What is the difference between onErrorResume and onErrorContinue?

// onErrorResume — the ORIGINAL flux terminates, a FALLBACK flux takes over

Flux.just(1, 2, 0, 4, 5)

.map(n -> 10 / n)

.onErrorResume(e -> Flux.just(-1))

.subscribe(System.out::println);

// Output: 10, 5, -1

// Elements 4 and 5 are NEVER processed — the original flux died at 0

// onErrorContinue — the failed element is SKIPPED, remaining elements continue

Flux.just(1, 2, 0, 4, 5)

.map(n -> 10 / n)

.onErrorContinue((e, val) -> System.err.println("Skipped: " + val))

.subscribe(System.out::println);

// Output: 10, 5, 2 (from 10/5=2? No, 10/4=2... wait)

// Actually: 10, 5, 2, 2 — hmm

// onErrorContinue can be SURPRISING — it propagates upstream!

WARNING about onErrorContinue: It doesn't work the way many people expect. It modifies behavior of upstream operators in a way that can be unpredictable. Many Reactor experts recommend avoiding it and using flatMap with individual error handling instead:

// RECOMMENDED pattern — handle errors per element

Flux.just(1, 2, 0, 4, 5)

.flatMap(n -> Mono.fromCallable(() -> 10 / n)

.onErrorResume(e -> {

log.warn("Skipping {} due to {}", n, e.getMessage());

return Mono.empty(); // skip this element

})

)

.subscribe(System.out::println);

// Output: 10, 5, 2, 2 — correct! Each element handled independently

PART 5: Backpressure

Q13. What is Backpressure and why is it important?

Backpressure is a mechanism where the consumer tells the producer how fast to send data, preventing the consumer from being overwhelmed.The problem without backpressure:

Fast Producer (1000 events/sec) → Slow Consumer (100 events/sec)

Without backpressure: buffer grows → OutOfMemoryError!

With backpressure: producer slows down to match consumer's pace

How it works in Reactive Streams:

// The subscriber controls the pace via request(n)

// Publisher MUST NOT send more than requested

// Internal flow:

// Subscriber: "I want 5 items" → request(5)

// Publisher: sends 5 items via onNext()

// Subscriber: processes them... "I want 3 more" → request(3)

// Publisher: sends 3 items

// ...and so on

Backpressure strategies in Reactor:

Flux fastProducer = Flux.range(1, 1\_000\_000);

// 1. buffer — buffer ALL excess elements (can cause OOM)

fastProducer.onBackpressureBuffer()

.subscribe(/\* slow consumer \*/);

// 2. buffer with limit

fastProducer.onBackpressureBuffer(1000) // buffer max 1000, then error

.subscribe(/\* slow consumer \*/);

// 3. buffer with overflow strategy

fastProducer.onBackpressureBuffer(1000,

dropped -> log.warn("Dropped: " + dropped),

BufferOverflowStrategy.DROP\_OLDEST) // or DROP\_LATEST

.subscribe(/\* slow consumer \*/);

// 4. drop — drop elements that can't be consumed

fastProducer.onBackpressureDrop(dropped -> log.warn("Dropped: " + dropped))

.subscribe(/\* slow consumer \*/);

// 5. latest — keep only the LATEST unconsumed element

fastProducer.onBackpressureLatest()

.subscribe(/\* slow consumer \*/);

// 6. error — signal error when consumer can't keep up

fastProducer.onBackpressureError()

.subscribe(/\* slow consumer \*/); // throws OverflowException

Real-world example — sensor data:

// IoT sensor emits 10,000 readings/sec, dashboard can handle 100/sec

Flux readings = sensorSource.getReadings() // 10K/sec

.onBackpressureLatest() // keep only the latest reading if consumer is slow

.sample(Duration.ofMillis(100)) // take 1 reading every 100ms = 10/sec

.subscribe(dashboard::update);

Q14. What is the difference between hot and cold Publishers?

Cold Publisher — Each subscriber gets its own independent stream. Like a video on demand — each viewer watches from the beginning.

Flux cold = Flux.range(1, 5);

cold.subscribe(n -> System.out.println("Subscriber A: " + n));

// A: 1, A: 2, A: 3, A: 4, A: 5

cold.subscribe(n -> System.out.println("Subscriber B: " + n));

// B: 1, B: 2, B: 3, B: 4, B: 5

// Both get ALL elements from the beginning

Hot Publisher — All subscribers share the same stream. Like a live broadcast — you see whatever is happening when you tune in.

// Creating a hot publisher

Sinks.Many sink = Sinks.many().multicast().onBackpressureBuffer();

Flux hot = sink.asFlux();

hot.subscribe(s -> System.out.println("A: " + s));

sink.tryEmitNext("event1"); // A sees this

sink.tryEmitNext("event2"); // A sees this

hot.subscribe(s -> System.out.println("B: " + s)); // B subscribes LATE

sink.tryEmitNext("event3"); // Both A and B see this

// B missed event1 and event2!

// Converting cold to hot:

Flux cold = Flux.interval(Duration.ofSeconds(1));

ConnectableFlux hot2 = cold.publish(); // now it's hot (but not started yet)

hot2.subscribe(n -> System.out.println("A: " + n));

hot2.subscribe(n -> System.out.println("B: " + n));

hot2.connect(); // START emitting — both A and B receive same events

// share() — shorthand for publish().refCount(1)

Flux shared = Flux.interval(Duration.ofSeconds(1)).share();

// auto-connects on first subscriber, auto-disconnects when last unsubscribes

Feature

Cold Publisher

Hot Publisher

Data generated

Per subscriber (independent)

Once (shared among all)

Late subscriber

Gets all data from start

Misses past events

Example

Database query, HTTP request

WebSocket, stock prices, events

Analogy

Video on demand

Live TV broadcast

PART 6: Schedulers & Threading

Q15. How does threading work in Reactor? What are Schedulers?

By default, Reactor runs on the thread of the subscriber (or the thread that produced the signal). You control threading with Schedulers.

// Default — runs on the calling thread

Flux.just(1, 2, 3)

.map(n -> {

System.out.println("Map on: " + Thread.currentThread().getName()); // main

return n \* 2;

})

.subscribe(n -> System.out.println("Subscribe on: " + Thread.currentThread().getName())); // main

Available Schedulers:

Scheduler

Threads

Use case

Schedulers.immediate()

Current thread

Testing, no switching

Schedulers.single()

One dedicated thread

Sequential background work

Schedulers.parallel()

N threads (N = CPU cores)

CPU-intensive (computation)

Schedulers.boundedElastic()

Up to 10×cores, 100K queued tasks

Blocking I/O (DB, HTTP, file)

Schedulers.fromExecutor(exec)

Your custom ExecutorService

Custom pool

subscribeOn() vs publishOn():

// subscribeOn — affects the ENTIRE chain (where subscription starts)

Flux.range(1, 5)

.map(n -> {

System.out.println("map1: " + Thread.currentThread().getName()); // boundedElastic

return n;

})

.subscribeOn(Schedulers.boundedElastic()) // everything runs on boundedElastic

.map(n -> {

System.out.println("map2: " + Thread.currentThread().getName()); // boundedElastic

return n;

})

.subscribe();

// publishOn — affects DOWNSTREAM operators (switches thread from this point)

Flux.range(1, 5)

.map(n -> {

System.out.println("map1: " + Thread.currentThread().getName()); // main

return n;

})

.publishOn(Schedulers.parallel()) // switch thread HERE

.map(n -> {

System.out.println("map2: " + Thread.currentThread().getName()); // parallel-1

return n;

})

.subscribe();

subscribeOn(elastic): elastic ──→ elastic ──→ elastic ──→ elastic (affects whole chain)

publishOn(parallel): main ──→ main ──→ \[SWITCH\] ──→ parallel ──→ parallel (affects downstream)

Wrapping blocking code:

// DANGEROUS — blocking call on a Reactor thread

Mono bad = Mono.just(userId)

.map(id -> jdbcTemplate.queryForObject("SELECT ...", User.class)); // BLOCKS the event loop!

// SAFE — offload blocking code to boundedElastic

Mono good = Mono.fromCallable(() ->

jdbcTemplate.queryForObject("SELECT ...", User.class))

.subscribeOn(Schedulers.boundedElastic()); // runs on a dedicated blocking thread

PART 7: Spring WebFlux

Q16. What is Spring WebFlux and how is it different from Spring MVC?

Spring MVC (Servlet stack):

Tomcat → 200 threads → 1 thread per request → blocks on I/O → limited to ~200 concurrent requests

Spring WebFlux (Reactive stack):

Netty → 4 event loop threads → non-blocking I/O → handles 10,000+ concurrent requests

Feature

Spring MVC

Spring WebFlux

Server

Tomcat, Jetty (Servlet-based)

Netty (non-blocking), Tomcat, Undertow

Threading

1 thread per request

Event loop (few threads, many requests)

I/O model

Blocking

Non-blocking

Return types

User, List, ResponseEntity

Mono, Flux

Database

JDBC (blocking)

R2DBC (reactive), reactive MongoDB

HTTP client

RestTemplate (blocking)

WebClient (non-blocking)

Best for

CPU-intensive, CRUD with JDBC

I/O-intensive, streaming, high concurrency

WebFlux Controller example:

@RestController

@RequestMapping("/api/users")

public class UserController {

private final UserService userService;

// GET single user

@GetMapping("/{id}")

public Mono\> getUser(@PathVariable String id) {

return userService.findById(id)

.map(ResponseEntity::ok)

.defaultIfEmpty(ResponseEntity.notFound().build());

}

// GET all users (streaming!)

@GetMapping(produces = MediaType.TEXT\_EVENT\_STREAM\_VALUE)

public Flux getAllUsers() {

return userService.findAll(); // streams users one by one via SSE

}

// POST create user

@PostMapping

@ResponseStatus(HttpStatus.CREATED)

public Mono createUser(@RequestBody Mono userMono) {

return userMono.flatMap(userService::save);

}

// DELETE user

@DeleteMapping("/{id}")

public Mono deleteUser(@PathVariable String id) {

return userService.deleteById(id);

}

}

Q17. What is WebClient and how does it replace RestTemplate?

RestTemplate is blocking — the thread waits for the HTTP response.WebClient is non-blocking — the thread is freed while waiting.

// RestTemplate — BLOCKING (deprecated in Spring 6)

User user = restTemplate.getForObject("/api/users/1", User.class); // thread waits here

// WebClient — NON-BLOCKING

@Service

public class UserClient {

private final WebClient webClient;

public UserClient(WebClient.Builder builder) {

this.webClient = builder

.baseUrl("http://user-service")

.defaultHeader(HttpHeaders.CONTENT\_TYPE, MediaType.APPLICATION\_JSON\_VALUE)

.build();

}

// GET single resource

public Mono getUser(String id) {

return webClient.get()

.uri("/api/users/{id}", id)

.retrieve()

.bodyToMono(User.class)

.timeout(Duration.ofSeconds(5))

.retryWhen(Retry.backoff(3, Duration.ofMillis(500)));

}

// GET multiple resources (streaming)

public Flux getAllUsers() {

return webClient.get()

.uri("/api/users")

.retrieve()

.bodyToFlux(User.class);

}

// POST

public Mono createUser(User user) {

return webClient.post()

.uri("/api/users")

.bodyValue(user)

.retrieve()

.onStatus(HttpStatusCode::is4xxClientError,

response -> response.bodyToMono(String.class)

.flatMap(body -> Mono.error(new ClientException(body))))

.bodyToMono(User.class);

}

// Parallel calls to multiple services

public Mono getDashboard(String userId) {

Mono userMono = getUser(userId);

Mono\> ordersMono = getOrders(userId).collectList();

Mono\> notifMono = getNotifications(userId).collectList();

return Mono.zip(userMono, ordersMono, notifMono)

.map(tuple -> new Dashboard(tuple.getT1(), tuple.getT2(), tuple.getT3()));

// All three calls execute IN PARALLEL, total time = max(user, orders, notif)

}

}

Q18. What is R2DBC and how does reactive database access work?

R2DBC (Reactive Relational Database Connectivity) is the reactive equivalent of JDBC.

// JDBC (blocking):

User user = jdbcTemplate.queryForObject("SELECT \* FROM users WHERE id = ?", User.class, id);

// Thread BLOCKS until DB responds

// R2DBC (non-blocking):

Mono user = r2dbcTemplate.selectOne(

query(where("id").is(id)), User.class);

// Thread is FREED immediately, callback fires when DB responds

Spring Data R2DBC repository:

public interface UserRepository extends ReactiveCrudRepository {

Flux findByLastName(String lastName);

@Query("SELECT \* FROM users WHERE email = :email")

Mono findByEmail(String email);

@Query("SELECT \* FROM users WHERE age > :age ORDER BY age")

Flux findUsersOlderThan(int age);

}

@Service

public class UserService {

private final UserRepository repository;

public Mono createUser(User user) {

return repository.save(user)

.doOnSuccess(saved -> log.info("Created user: {}", saved.getId()));

}

public Flux findAllActive() {

return repository.findAll()

.filter(User::isActive)

.sort(Comparator.comparing(User::getLastName));

}

@Transactional

public Mono updateEmail(Long userId, String newEmail) {

return repository.findById(userId)

.switchIfEmpty(Mono.error(new UserNotFoundException(userId)))

.flatMap(user -> {

user.setEmail(newEmail);

return repository.save(user);

});

}

}

PART 8: Internal Working of Reactor

Q19. How does the Reactor pipeline execute internally?

Nothing happens until you subscribe! This is the most important concept.

// This builds a RECIPE (assembly time) — no execution yet

Flux pipeline = Flux.just("A", "B", "C") // Source

.filter(s -> !s.equals("B")) // FilterFlux wraps Source

.map(String::toLowerCase); // MapFlux wraps FilterFlux

// Assembled pipeline (linked list of operators):

// MapFlux → FilterFlux → FluxArray("A","B","C")

// THIS triggers execution (subscription time):

pipeline.subscribe(System.out::println);

// What happens internally:

// 1. subscribe() propagates UPSTREAM through the chain:

// MapFlux.subscribe() → FilterFlux.subscribe() → FluxArray.subscribe()

//

// 2. FluxArray calls subscriber.onSubscribe(subscription)

// Propagates DOWNSTREAM:

// FluxArray.onSubscribe → FilterFlux.onSubscribe → MapFlux.onSubscribe → your subscriber

//

// 3. Your subscriber calls request(Long.MAX\_VALUE)

// Propagates UPSTREAM:

// your subscriber.request → MapFlux.request → FilterFlux.request → FluxArray.request

//

// 4. Elements flow DOWNSTREAM:

// FluxArray emits "A" → FilterFlux passes "A" → MapFlux transforms to "a" → println("a")

// FluxArray emits "B" → FilterFlux BLOCKS "B" (filtered out)

// FluxArray emits "C" → FilterFlux passes "C" → MapFlux transforms to "c" → println("c")

// FluxArray emits onComplete → propagates downstream → subscription ends

Visual:

ASSEMBLY (building the pipeline — nothing executes):

\[subscribe\] ← \[MapFlux\] ← \[FilterFlux\] ← \[FluxArray("A","B","C")\]

SUBSCRIPTION (triggered by subscribe()):

subscribe() travels UPSTREAM: → → → → → → → → → →

↓

onSubscribe() travels DOWNSTREAM: ← ← ← ← ← ← ←

request(n) travels UPSTREAM: → → → → → → → →

onNext(elem) travels DOWNSTREAM: ← ← ← ← ← ← ←

onComplete() travels DOWNSTREAM: ← ← ← ← ← ← ←

Q20. What is the difference between Assembly time, Subscription time, and Runtime?

Phase

When

What happens

Assembly time

When you chain operators

Builds operator pipeline (linked list). No data flows.

Subscription time

When subscribe() is called

subscribe() propagates upstream, onSubscribe() propagates downstream

Runtime

After subscription

Data flows via onNext(), onError(), onComplete()

Common mistake — operator at assembly time instead of runtime:

// BUG: request() runs at ASSEMBLY time — same request for all subscribers!

ServerHttpRequest request = getRequest(); // runs ONCE at assembly

Mono user = Mono.just(request)

.map(req -> extractUser(req));

// FIX: defer() ensures code runs at SUBSCRIPTION time — per subscriber

Mono user = Mono.defer(() -> {

ServerHttpRequest req = getRequest(); // runs per subscriber

return Mono.just(extractUser(req));

});

PART 9: Real-World Scenario-Based Questions

Q21. Design a reactive microservice that aggregates data from multiple services.

@Service

public class DashboardService {

private final WebClient userClient;

private final WebClient orderClient;

private final WebClient recommendationClient;

private final ReactiveRedisTemplate cache;

public Mono getDashboard(String userId) {

return getCachedDashboard(userId)

.switchIfEmpty(buildDashboard(userId)

.flatMap(dashboard -> cacheDashboard(userId, dashboard)));

}

private Mono buildDashboard(String userId) {

Mono profileMono = userClient.get()

.uri("/users/{id}", userId)

.retrieve()

.bodyToMono(UserProfile.class)

.timeout(Duration.ofSeconds(3))

.onErrorReturn(UserProfile.anonymous());

Mono\> ordersMono = orderClient.get()

.uri("/orders?userId={id}", userId)

.retrieve()

.bodyToFlux(Order.class)

.collectList()

.timeout(Duration.ofSeconds(3))

.onErrorReturn(Collections.emptyList());

Mono\> recommendationsMono = recommendationClient.get()

.uri("/recommendations/{id}", userId)

.retrieve()

.bodyToFlux(Product.class)

.take(10)

.collectList()

.timeout(Duration.ofSeconds(2))

.onErrorReturn(Collections.emptyList());

return Mono.zip(profileMono, ordersMono, recommendationsMono)

.map(tuple -> new Dashboard(tuple.getT1(), tuple.getT2(), tuple.getT3()))

.doOnSuccess(d -> log.info("Dashboard built for user: {}", userId))

.doOnError(e -> log.error("Dashboard build failed for user: {}", userId, e));

}

private Mono getCachedDashboard(String userId) {

return cache.opsForValue().get("dashboard:" + userId);

}

private Mono cacheDashboard(String userId, Dashboard dashboard) {

return cache.opsForValue()

.set("dashboard:" + userId, dashboard, Duration.ofMinutes(5))

.thenReturn(dashboard);

}

}

Q22. Implement a real-time Server-Sent Events (SSE) stream.

@RestController

public class NotificationController {

private final Sinks.Many sink = Sinks.many().multicast().onBackpressureBuffer();

// SSE endpoint — client receives events in real-time

@GetMapping(value = "/notifications/stream", produces = MediaType.TEXT\_EVENT\_STREAM\_VALUE)

public Flux\> streamNotifications(

@RequestParam String userId) {

return sink.asFlux()

.filter(n -> n.getUserId().equals(userId))

.map(n -> ServerSentEvent.builder()

.id(n.getId())

.event(n.getType())

.data(n)

.build())

.doOnCancel(() -> log.info("Client disconnected: {}", userId));

}

// Publish notification (called from other services)

@PostMapping("/notifications")

public Mono publishNotification(@RequestBody Notification notification) {

sink.tryEmitNext(notification);

return Mono.empty();

}

// Heartbeat to keep connection alive

@GetMapping(value = "/notifications/heartbeat", produces = MediaType.TEXT\_EVENT\_STREAM\_VALUE)

public Flux\> heartbeat() {

return Flux.interval(Duration.ofSeconds(30))

.map(seq -> ServerSentEvent.builder()

.event("heartbeat")

.data("ping")

.build());

}

}

Q23. How to handle reactive transactions?

@Service

public class OrderService {

private final TransactionalOperator txOperator;

private final OrderRepository orderRepository;

private final InventoryRepository inventoryRepository;

private final PaymentClient paymentClient;

// Method 1: @Transactional annotation

@Transactional

public Mono placeOrder(OrderRequest request) {

return orderRepository.save(new Order(request, OrderStatus.PENDING))

.flatMap(order ->

inventoryRepository.reserveStock(request.getProductId(), request.getQuantity())

.then(paymentClient.charge(request.getUserId(), order.getTotal()))

.flatMap(payment -> {

order.setStatus(OrderStatus.CONFIRMED);

order.setPaymentId(payment.getId());

return orderRepository.save(order);

})

.onErrorResume(e -> {

order.setStatus(OrderStatus.FAILED);

return orderRepository.save(order)

.then(inventoryRepository.releaseStock(

request.getProductId(), request.getQuantity()))

.then(Mono.error(e));

})

);

}

// Method 2: Programmatic transaction with TransactionalOperator

public Mono placeOrderProgrammatic(OrderRequest request) {

return txOperator.transactional(

orderRepository.save(new Order(request, OrderStatus.PENDING))

.flatMap(order -> processOrder(order))

);

}

}

Q24. How to test reactive code?

@ExtendWith(SpringExtension.class)

class UserServiceTest {

@Test

void shouldReturnUserById() {

UserService service = new UserService(mockRepository);

// StepVerifier — the primary testing tool for reactive streams

StepVerifier.create(service.findById("123"))

.expectNextMatches(user -> user.getName().equals("Alice"))

.verifyComplete();

}

@Test

void shouldReturnAllActiveUsers() {

StepVerifier.create(service.findAllActive())

.expectNextCount(3)

.verifyComplete();

}

@Test

void shouldHandleError() {

StepVerifier.create(service.findById("nonexistent"))

.expectError(UserNotFoundException.class)

.verify();

}

@Test

void shouldStreamWithBackpressure() {

StepVerifier.create(service.findAll(), 2) // request only 2

.expectNextCount(2)

.thenRequest(3) // then request 3 more

.expectNextCount(3)

.thenCancel() // cancel the subscription

.verify();

}

@Test

void shouldTimeoutAfter5Seconds() {

StepVerifier.withVirtualTime(() ->

service.findById("slow-query").timeout(Duration.ofSeconds(5)))

.expectSubscription()

.thenAwait(Duration.ofSeconds(5))

.expectError(TimeoutException.class)

.verify();

}

@Test

void shouldRetryOnFailure() {

when(mockRepository.findById("123"))

.thenReturn(Mono.error(new RuntimeException("DB down"))) // 1st call fails

.thenReturn(Mono.error(new RuntimeException("DB down"))) // 2nd call fails

.thenReturn(Mono.just(new User("123", "Alice"))); // 3rd call succeeds

StepVerifier.create(service.findByIdWithRetry("123"))

.expectNextMatches(u -> u.getName().equals("Alice"))

.verifyComplete();

}

}

// WebFlux controller testing

@WebFluxTest(UserController.class)

class UserControllerTest {

@Autowired

private WebTestClient webClient;

@MockBean

private UserService userService;

@Test

void shouldGetUser() {

when(userService.findById("1")).thenReturn(Mono.just(new User("1", "Alice")));

webClient.get().uri("/api/users/1")

.exchange()

.expectStatus().isOk()

.expectBody(User.class)

.value(user -> {

assertThat(user.getName()).isEqualTo("Alice");

});

}

@Test

void shouldStreamUsers() {

when(userService.findAll()).thenReturn(Flux.just(

new User("1", "Alice"), new User("2", "Bob")));

webClient.get().uri("/api/users")

.accept(MediaType.TEXT\_EVENT\_STREAM)

.exchange()

.expectStatus().isOk()

.expectBodyList(User.class)

.hasSize(2);

}

}

PART 10: Advanced Topics & Tricky Questions

Q25. What are Sinks and why are they needed?

Sinks (introduced in Reactor 3.4) are the programmatic way to create reactive signals. They replace the older FluxProcessor and EmitterProcessor.

// Sinks.One — programmatic Mono

Sinks.One monoSink = Sinks.one();

Mono mono = monoSink.asMono();

// Later, from any thread:

monoSink.tryEmitValue("Hello"); // completes the Mono with "Hello"

// or

monoSink.tryEmitError(new RuntimeException("Failed"));

// or

monoSink.tryEmitEmpty(); // completes with no value

// Sinks.Many — programmatic Flux (multiple emission strategies)

// unicast — exactly 1 subscriber

Sinks.Many unicast = Sinks.many().unicast().onBackpressureBuffer();

// multicast — multiple subscribers, each sees only elements emitted AFTER subscribing

Sinks.Many multicast = Sinks.many().multicast().onBackpressureBuffer();

// replay — multiple subscribers, late subscribers get past elements too

Sinks.Many replayAll = Sinks.many().replay().all(); // replay ALL past

Sinks.Many replayN = Sinks.many().replay().limit(100); // replay last 100

Sinks.Many replayDuration = Sinks.many().replay()

.limit(Duration.ofMinutes(5)); // replay last 5 minutes

Real-world: Event bus in reactive application:

@Component

public class ReactiveEventBus {

private final Sinks.Many sink =

Sinks.many().multicast().onBackpressureBuffer();

public void publish(DomainEvent event) {

sink.emitNext(event, Sinks.EmitFailureHandler.FAIL\_FAST);

}

public Flux on(Class eventType) {

return sink.asFlux()

.filter(eventType::isInstance)

.cast(eventType);

}

}

// Listener

eventBus.on(OrderCreatedEvent.class)

.flatMap(event -> notificationService.sendOrderConfirmation(event))

.subscribe();

Q26. What is Context in Reactor and how is it different from ThreadLocal?

Problem: ThreadLocal doesn't work in reactive programming because operations can hop between threads. The data stored in one thread's ThreadLocal is invisible to another thread.Reactor Context is an immutable key-value store that propagates through the reactive pipeline, regardless of thread switches.

// ThreadLocal — BREAKS in reactive (data lost when thread changes)

ThreadLocal userId = new ThreadLocal<>();

userId.set("alice");

Mono.just("data")

.publishOn(Schedulers.parallel()) // switches thread!

.map(data -> {

String user = userId.get(); // NULL! Different thread, different ThreadLocal

return data;

});

// Context — WORKS in reactive (propagates through the chain)

Mono.just("data")

.publishOn(Schedulers.parallel())

.flatMap(data -> Mono.deferContextual(ctx -> {

String user = ctx.get("userId"); // "alice" — available regardless of thread!

return Mono.just(data + " for " + user);

}))

.contextWrite(Context.of("userId", "alice")) // attach context

.subscribe(System.out::println); // "data for alice"

Important: Context propagates from bottom to top (from subscriber toward publisher).

// Real-world: propagate correlation ID through reactive pipeline

@Component

public class CorrelationIdFilter implements WebFilter {

@Override

public Mono filter(ServerWebExchange exchange, WebFilterChain chain) {

String correlationId = exchange.getRequest().getHeaders()

.getFirst("X-Correlation-ID");

if (correlationId == null) {

correlationId = UUID.randomUUID().toString();

}

return chain.filter(exchange)

.contextWrite(Context.of("correlationId", correlationId));

}

}

// Anywhere in the reactive chain:

Mono.deferContextual(ctx -> {

String correlationId = ctx.getOrDefault("correlationId", "unknown");

log.info("\[{}\] Processing request", correlationId);

return processRequest();

});

Q27. What are the common pitfalls and anti-patterns in reactive programming?

Pitfall 1: Blocking in a reactive pipeline

// TERRIBLE — blocks the Netty event loop thread

@GetMapping("/user/{id}")

public Mono getUser(@PathVariable String id) {

User user = jdbcTemplate.queryForObject("SELECT ...", User.class, id); // BLOCKS!

return Mono.just(user);

}

// FIX: offload to boundedElastic or use reactive driver

@GetMapping("/user/{id}")

public Mono getUser(@PathVariable String id) {

return Mono.fromCallable(() ->

jdbcTemplate.queryForObject("SELECT ...", User.class, id))

.subscribeOn(Schedulers.boundedElastic());

}

// BEST: use reactive repository (R2DBC)

@GetMapping("/user/{id}")

public Mono getUser(@PathVariable String id) {

return userRepository.findById(id);

}

Pitfall 2: Forgetting to subscribe

// BUG: nothing happens! No one subscribed.

public Mono sendNotification(String userId) {

notificationService.send(userId); // returns Mono — NOT subscribed!

return Mono.empty();

}

// FIX: chain it properly

public Mono sendNotification(String userId) {

return notificationService.send(userId); // returned to the framework, which subscribes

}

Pitfall 3: .block() in a reactive chain

// DEADLOCK RISK: calling block() on a Netty thread

@GetMapping("/data")

public Mono getData() {

String result = webClient.get().uri("/api").retrieve()

.bodyToMono(String.class)

.block(); // BLOCKS THE EVENT LOOP → DEADLOCK!

return Mono.just(result);

}

// block() should ONLY be used outside reactive chains (e.g., in tests, main method)

Pitfall 4: Multiple subscriptions to a hot source

// BUG: calling the API twice!

Mono userMono = webClient.get().uri("/users/1").retrieve().bodyToMono(User.class);

return Mono.zip(

userMono.map(User::getName), // subscription 1 → API call 1

userMono.map(User::getEmail) // subscription 2 → API call 2! (duplicate!)

);

// FIX: cache the Mono

Mono userMono = webClient.get().uri("/users/1").retrieve()

.bodyToMono(User.class)

.cache(); // caches the result — second subscriber gets cached value

return Mono.zip(

userMono.map(User::getName), // subscription 1 → API call

userMono.map(User::getEmail) // subscription 2 → uses cached result

);

Pitfall 5: Ignoring errors

// BUG: error silently swallowed

flux.subscribe(System.out::println); // if error occurs, it goes to Hooks.onErrorDropped

// FIX: always handle errors in subscribe

flux.subscribe(

data -> System.out.println(data),

error -> log.error("Stream failed", error),

() -> log.info("Stream completed")

);

Q28. When should you use reactive (WebFlux) and when should you stick with blocking (Spring MVC)?

Use WebFlux when:

Scenario

Why

Many concurrent I/O calls (API gateway, aggregation)

Threads freed during I/O wait

Real-time streaming (SSE, WebSocket)

Built-in support for Flux streams

High concurrency (10K+ concurrent users)

Few threads handle many requests

Microservice orchestration (call 5 services in parallel)

Mono.zip() is natural

Streaming large data sets (CSV export, log streaming)

Backpressure prevents OOM

Stick with Spring MVC when:

Scenario

Why

JDBC/JPA database (no reactive driver)

Blocking calls negate reactive benefits

CPU-intensive processing

Reactive doesn't help with CPU-bound work

Simple CRUD applications

Added complexity not justified

Team is not familiar with reactive

Steep learning curve, harder debugging

Synchronous 3rd-party libraries

Wrapping everything in subscribeOn(elastic) defeats the purpose

The hybrid approach (most practical):

// Use Spring MVC for most controllers, WebClient for HTTP calls

@RestController

public class OrderController {

private final WebClient userService; // non-blocking HTTP client

@GetMapping("/orders/{id}")

public Order getOrder(@PathVariable String id) {

Order order = orderRepository.findById(id); // blocking JDBC — that's OK in MVC

User user = userService.get()

.uri("/users/{id}", order.getUserId())

.retrieve()

.bodyToMono(User.class)

.block(); // OK to block in Spring MVC thread

order.setUser(user);

return order;

}

}

Q29. Explain generate() vs create() vs push().

Method

Synchronous/Async

Thread-safety

Elements per round

generate()

Synchronous

Single thread

Exactly 1 next() per call

create()

Asynchronous

Multi-thread safe

Multiple next() from any thread

push()

Asynchronous

Single thread

Multiple next() from one thread

// generate — synchronous, one element at a time, with state

Flux generated = Flux.generate(

() -> 0, // initial state

(state, sink) -> {

sink.next("Event-" + state); // EXACTLY ONE next() per call!

// sink.next("another"); // ERROR! Only one next per round

if (state >= 10) sink.complete();

return state + 1; // return new state

}

);

// create — asynchronous, multi-threaded, bridging callback APIs

Flux created = Flux.create(sink -> {

// Can call sink.next() from ANY thread, ANY number of times

messageListener.onMessage(msg -> sink.next(msg)); // thread A

anotherSource.onData(data -> sink.next(data)); // thread B

sink.onDispose(() -> {

messageListener.close();

anotherSource.close();

});

});

// push — single-threaded async, slightly more efficient than create

Flux pushed = Flux.push(sink -> {

singleThreadedApi.register(event -> sink.next(event)); // only ONE thread pushes

sink.onDispose(() -> singleThreadedApi.unregister());

});

Q30. What is Mono.zip() vs Mono.when()?

Mono userMono = fetchUser();

Mono\> ordersMono = fetchOrders();

// zip — waits for ALL, combines results into a Tuple

Mono\>> zipped = Mono.zip(userMono, ordersMono);

// or with combinator:

Mono combined = Mono.zip(userMono, ordersMono)

.map(tuple -> new Dashboard(tuple.getT1(), tuple.getT2()));

// when — waits for ALL, but discards results (Mono)

Mono allDone = Mono.when(

emailService.sendEmail(user),

smsService.sendSms(user),

pushService.sendNotification(user)

);

// You don't care about the individual results, just that all completed

// Difference:

// zip() → gives you the results → Mono\>

// when() → only signals completion → Mono

WebSocket, RSocket, reactive security, Reactor debugging (Hooks.onOperatorDebug), or performance tuning — just let me know.