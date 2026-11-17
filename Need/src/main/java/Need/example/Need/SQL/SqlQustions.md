# SQL and NoSQL Interview Preparation Guide

This guide contains **72 linked questions**. Repeated interview angles are grouped under one question, with their additional explanations retained as related details. The sequence moves from foundations to implementation and operations.

## Learning Path

| Stage | Topics | Questions |
|---|---|---:|
| 1 | Relational foundations, keys, sequences, normalization, and OLTP/OLAP | 1–5 |
| 2 | NoSQL models, database selection, and scaling | 6–8 |
| 3 | SQL joins, subqueries, windows, and database objects | 9–14 |
| 4 | Transactions, isolation, concurrency, and idempotency | 15–21 |
| 5 | SQL indexes, execution plans, and optimization | 22–28 |
| 6 | NoSQL consistency, replication, partitioning, and modeling | 29–38 |
| 7 | Distributed architecture, messaging, migrations, caching, and recovery | 39–49 |
| 8 | Cloud deployment, security, and operations | 50–56 |
| 9 | High-Level and Low-Level Design scenarios | 57–72 |

## Easy: Relational Foundations and Data Modeling

### 1. What is the relational model?
**Answer:** It represents data as relations (tables) of rows and columns. A schema defines attributes, types, and constraints; SQL expresses queries and updates.

**Reasoning/trade-off:** Relations and constraints make data integrity explicit, and joins support flexible queries. Schema changes require coordination.

**Mistakes:** Treating SQL tables as unordered; confusing a table’s logical model with its physical storage.

**Follow-ups:** What is a candidate key? How does a database enforce a constraint?

**Example:** Banking systems commonly use relational schemas for accounts and transactions because their relationships and integrity rules are central.

### 2. What are primary, foreign, and unique keys, and how do constraints protect data?
**Answer:** A primary key uniquely identifies each row and cannot be null. A foreign key references a key in another table. A unique constraint prevents duplicate values in a column or column set.

**Reasoning/trade-off:** Constraints keep invalid data out, including when writes come from multiple services. Foreign-key enforcement can complicate bulk loads or cross-service ownership.

**Mistakes:** Using a mutable business value, such as email, as the primary key; assuming an application check replaces a database constraint.

**Follow-ups:** When would you use a composite primary key? Should a foreign key cross service boundaries?

**Example:** An order’s `customer_id` can reference a customer row, while an order ID remains stable if the customer changes their email.

#### Related interview angle: What is the difference between primary, unique, and foreign keys?

**Answer:** A primary key identifies a row and cannot be null. A unique constraint prevents duplicate key values. A foreign key enforces a reference to a valid row in another table.

**Reasoning and trade-offs:** Database constraints protect integrity even when multiple application instances write concurrently. Cross-service foreign keys are usually inappropriate when services own independent databases.

**Common mistake:** Replacing a unique constraint with an application-level “check then insert.”

**Follow-up:** What happens if two requests concurrently insert the same email?

**Example:** A unique index on an idempotency key can prevent duplicate payment commands.

### 3. What is a SQL sequence? How is it different from an identity column?
**Answer:** A sequence is a database object that generates numeric values, often used for surrogate keys. An identity column is a column configured to generate values automatically; the database may implement it using a sequence or an equivalent mechanism.

```sql
CREATE SEQUENCE order_id_seq START WITH 1 INCREMENT BY 1;

SELECT nextval('order_id_seq'); -- PostgreSQL example
```

**Benefits and trade-offs:** Sequences support concurrent ID generation without requiring a table-wide counter lock. Values may have gaps after rollbacks, caching, or failures; they are not a reliable count of committed rows.

**Common mistake:** Assuming IDs are gapless or that an ID’s order exactly represents commit order.

**Follow-up:** When would you use UUIDs or Snowflake-style IDs instead?

**Example:** A relational order table may use a generated numeric primary key internally and a separate public identifier in APIs.

### 4. What is normalization, and when should a design be denormalized?
**Answer:** Normalization structures data to reduce duplication and update anomalies. 1NF uses atomic values and rows; 2NF removes dependencies on part of a composite key; 3NF removes transitive dependencies on non-key attributes; BCNF requires every determinant to be a candidate key.

**Reasoning/trade-off:** Normalization suits OLTP writes and integrity. It may require joins that increase query cost.

**Mistakes:** Assuming normalization means “one fact per table” without analyzing dependencies; applying BCNF mechanically when it harms a real workload.

**Follow-ups:** What update anomaly does 3NF prevent? When would denormalization be reasonable?

**Example:** A product’s canonical description usually belongs in a product table, not copied into every order line.

#### Related interview angle: What is normalization, and when should data be denormalized?

**Answer:** Normalization reduces duplication and update anomalies; 3NF is a common OLTP target. Denormalization duplicates or precomputes data to fit measured read patterns.

**Reasoning and trade-offs:** Normalized data supports consistency and flexible joins. Denormalized data can speed reads but requires a clear update, repair, and rebuild strategy.

**Common mistake:** Treating 3NF or denormalization as a universal requirement rather than a workload choice.

**Follow-up:** Which copy is authoritative, and how will derived data be refreshed?

**Example:** An order line often stores a price/name snapshot even though the canonical product details remain in a product table.

#### Related interview angle: What is denormalization, and when is it appropriate?

**Answer:** Denormalization duplicates or precomputes data to reduce joins or read latency. Use it when measured read patterns justify the write, storage, and consistency costs.

**Reasoning/trade-off:** It can improve read-heavy paths, but every duplicate needs an update strategy: synchronous, asynchronous, or rebuildable.

**Mistakes:** Denormalizing before measuring; failing to define which copy is authoritative.

**Follow-ups:** How do you handle stale copies? How would you rebuild a materialized view?

**Example:** A news-feed entry may store a post summary so feed reads do not need to fetch every post separately.

### 5. How do you choose OLTP versus OLAP modeling?
**Answer:** OLTP typically uses normalized schemas, often targeting 3NF, to preserve transactional integrity. OLAP commonly uses dimensional models such as star schemas: fact tables for measurements and dimensions for descriptive context.

**Reasoning/trade-off:** The two optimize different access patterns. Analytical queries on production OLTP can contend with user-facing transactions, so use replicas, warehouses, or lakehouse systems.

**Mistakes:** Running large scans on the primary; modeling a fact table without a clear grain.

**Follow-ups:** What is the grain of a sales fact? When should dimensions be denormalized?

**Example:** A sales warehouse can represent each order line as a fact with product, customer, date, and channel dimensions.

## Easy: Choosing and Scaling Database Models

### 6. What are the NoSQL data models, and which applications fit each type?
**Answer:** Document stores group flexible records; key-value stores retrieve values by key; wide-column stores organize sparse, partitioned data by row and column; graph stores model vertices and edges.

| Model | Good fit | Common trade-off |
|---|---|---|
| Document | Aggregate-oriented records and varied attributes | Cross-document joins and constraints may be limited |
| Key-value | Fast lookup by known key | Limited query flexibility |
| Wide-column | High-volume, partition-keyed access | Data model follows query patterns; scans can be costly |
| Graph | Relationship traversal | Distributed traversal can be expensive |

**Mistakes:** Choosing by product popularity rather than access pattern.

**Follow-ups:** How would you model a many-to-many relationship in each?

**Example:** Amazon’s Dynamo paper popularized a distributed key-value design for highly available services.

#### Related interview angle: What kinds of NoSQL databases are there, and what are they suited for?

**Answer:**

| Type | Data model | Suitable applications | Main trade-off |
|---|---|---|---|
| Key-value | Key maps to a value | Sessions, cache, feature flags, lookups | Queries usually depend on known keys |
| Document | JSON-like documents | Profiles, catalogs, flexible records | Cross-document joins and constraints may be limited |
| Wide-column | Partitioned rows with ordered columns | Telemetry, event history, messaging | Queries must align with partition and sort keys |
| Graph | Nodes and relationships | Fraud analysis, recommendations, relationship traversal | Distributed graph traversal can be costly |

**Common mistake:** Picking a database by its label without listing query patterns.

**Follow-up:** How would you model the same entity in a relational database and a document database?

**Example:** A telemetry service may store recent readings in a time-oriented wide-column store while keeping device ownership in a relational database.

### 7. How do you choose SQL, NoSQL, or both for an application?
**Answer:** Choose from invariants, query patterns, transaction boundaries, scale profile, operational skills, and recovery needs—not data shape alone. SQL is a strong default for relational OLTP and ad hoc joins; NoSQL is useful when its partitioned access model, flexibility, or scale characteristics fit the workload.

**Reasoning/trade-off:** SQL often gives richer constraints and queries; NoSQL can offer predictable key access and horizontal distribution but may shift joins and consistency logic into the application.

**Mistakes:** “NoSQL is faster”; choosing a database before defining access patterns.

**Follow-ups:** What workload would change your choice? How would you benchmark?

**Example:** A service may use SQL for orders and a key-value store for a high-volume session lookup.

#### Related interview angle: When should an application use SQL, NoSQL, or both?

**Answer:** Prefer SQL when the application needs relational integrity, multi-row transactions, joins, or flexible ad hoc querying. Prefer a NoSQL model when its specific access pattern—such as key lookup, document retrieval, high-volume partitioned writes, or graph traversal—fits the workload better. Use both when the benefits justify operating and synchronizing multiple systems.

**Benefits and trade-offs:** SQL commonly offers rich constraints and query flexibility. NoSQL databases can offer useful partitioned access patterns and horizontal scaling, but may require denormalized models and more application-level consistency handling. Neither category is universally faster or more reliable.

**Common mistake:** Choosing NoSQL just because a system is expected to “scale.”

**Follow-up:** Which system is the source of truth? How will derived data be updated?

**Example:** A commerce service might keep orders in SQL, product-search documents in a search engine, and temporary sessions in a key-value store.

### 8. How do vertical and horizontal scaling work, and what approaches are available?
**Answer:** Vertical scaling (scale up) adds resources to one server, such as CPU, memory, or faster storage. Horizontal scaling (scale out) adds servers and distributes work or data across them.

**Benefits and trade-offs:** Vertical scaling is simpler and often preserves familiar SQL transactions, but has hardware limits and can create a larger single failure domain. Horizontal scaling provides more aggregate capacity and can improve availability, but introduces routing, replication, partitioning, and coordination complexity.

**Common mistake:** Assuming horizontal scaling is automatically cheaper or easier. Distributed systems add operational and application complexity.

**Follow-up:** What would you try before sharding a SQL database?

**Example:** A team might first scale a relational primary vertically, add read replicas, optimize queries, and partition large tables. Sharding may follow only when those measures no longer meet requirements.

#### Related interview angle: What are the main SQL and NoSQL scaling approaches?

**Answer:** Common approaches include vertical scaling, read replicas, caching, partitioning, sharding, and workload separation. Select based on the bottleneck and access pattern.

**Benefits and trade-offs:** Read replicas can scale reads but may be stale. Partitioning can help maintenance and data pruning. Sharding distributes capacity but complicates cross-shard queries and transactions. Caching reduces repeated reads but introduces invalidation and staleness concerns.

**Common mistake:** Treating sharding as the first optimization.

**Follow-up:** How would you determine whether the bottleneck is CPU, I/O, locks, network, or application connection pooling?

**Example:** A reporting workload can be moved to a warehouse or read model instead of competing with transactional requests on the primary database.

## Easy: SQL Querying and Database Objects

### 9. What do INNER, LEFT, RIGHT, and FULL OUTER JOINs return?
**Answer:** `INNER` returns matching rows. `LEFT` preserves every left row and fills unmatched right columns with nulls; `RIGHT` does the reverse; `FULL` preserves unmatched rows from both sides.

**Reasoning/trade-off:** Join type encodes whether missing related data should exclude or preserve a row.

**Mistakes:** Putting a right-table filter in `WHERE` after a left join when unmatched rows must remain; this can turn it into an effective inner join.

**Follow-ups:** How do nulls behave in join predicates? What is a self join?

**Example:** A report listing all customers, including those with no orders, uses a left join from customers to orders.

### 10. What are subqueries and CTEs?
**Answer:** A subquery is a query nested inside another query. A common table expression (CTE), declared with `WITH`, names a query expression for reuse or readability; recursive CTEs can traverse hierarchies.

**Reasoning/trade-off:** CTEs can clarify complex logic, but a CTE is not necessarily a materialized temporary table. Optimizer behavior varies by database.

**Mistakes:** Assuming a CTE always improves performance or executes exactly once.

**Follow-ups:** When would you use a recursive CTE? How can you inspect its plan?

**Example:** A recursive CTE can walk a category tree or organizational hierarchy.

### 11. What is a window function, and how does it differ from GROUP BY?
**Answer:** A window function calculates over a related set of rows without collapsing those rows. `PARTITION BY` defines groups; `ORDER BY` defines order within each group.

```sql
SELECT customer_id,
 order_id,
 order_date,
 ROW_NUMBER() OVER (
 PARTITION BY customer_id
 ORDER BY order_date DESC, order_id DESC
 ) AS order_rank
FROM orders;
```

**Reasoning and trade-offs:** Useful for top-N-per-group, ranking, running totals, and previous/next comparisons. Large partitions or sorts can be expensive.

**Common mistake:** Forgetting a unique tie-breaker, which can make row ordering nondeterministic.

**Follow-up:** How does `ROW_NUMBER()` differ from `RANK()` and `DENSE_RANK()`?

**Example:** To retrieve each customer’s latest order, rank by date and keep rank 1.

#### Related interview angle: What are window functions, and how do they differ from `GROUP BY`?

**Answer:** Window functions calculate across related rows while retaining each row. `GROUP BY` collapses rows into groups. For example, `ROW_NUMBER() OVER (PARTITION BY customer_id ORDER BY created_at DESC)` ranks each customer’s orders.

**Reasoning/trade-off:** Windows avoid self-joins for ranking and running totals, but large partitions or sorts can be expensive.

**Mistakes:** Omitting deterministic tie-break ordering; using `GROUP BY` when individual rows are needed.

**Follow-ups:** Difference between `RANK` and `DENSE_RANK`? How do frames affect running totals?

**Example:** A finance report can use windows to calculate a running account balance.

### 12. What is a window frame, and why does it matter?
**Answer:** A frame defines which rows in a window contribute to a calculation. For example, a running total commonly uses all preceding rows through the current row. A `ROWS` frame counts rows; a `RANGE` frame groups peers with the same ordering value.

```sql
SUM(amount) OVER (
 PARTITION BY account_id
 ORDER BY transaction_time, transaction_id
 ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
)
```

**Reasoning and trade-offs:** Explicit frames avoid surprises, especially when timestamps tie. The correct frame depends on whether the requirement is row-based or value/date-range-based.

**Common mistake:** Assuming `ROWS 6 PRECEDING` always means seven calendar days.

**Follow-up:** How would you calculate a true seven-day moving average when some days have no rows?

**Example:** A transaction ledger can calculate a deterministic running balance using timestamp plus transaction ID.

### 13. What are views, stored procedures, and triggers?
**Answer:** A view is a named query. A stored procedure runs database-side logic. A trigger executes in response to a database event.

**Reasoning/trade-off:** They can centralize logic and reduce round trips, but hide control flow and couple application behavior to the database.

**Mistakes:** Putting business workflows in triggers without observability or tests.

**Follow-ups:** When is a materialized view useful? How are procedures versioned?

**Example:** A reporting view can give analysts a stable interface over normalized tables.

### 14. When are views, materialized views, procedures, or triggers useful?
**Answer:** Use a normal view for a reusable logical query, a materialized view for a refreshable read model, procedures for database-local operations, and triggers for narrow database-enforced side effects or auditing.

**Reasoning/trade-off:** They centralize work near data but may obscure dependencies and complicate migrations, testing, and portability.

**Mistakes:** Using triggers as an invisible event bus; forgetting materialized-view refresh and staleness behavior.

**Follow-ups:** How do you deploy procedure changes safely? What owns the refresh schedule?

**Example:** An analytics dashboard may use a periodically refreshed aggregate rather than recomputing from raw events on every request.

## Medium: Transactions and Concurrency

### 15. What is ACID?
**Answer:** Atomicity: all-or-nothing transaction. Consistency: constraints and invariants hold. Isolation: concurrent transactions behave according to an isolation level. Durability: committed changes survive the promised failure model.

**Reasoning/trade-off:** ACID supports reliable state transitions, but stronger guarantees can cost throughput or latency, especially across distributed nodes.

**Mistakes:** Assuming ACID alone guarantees correct business logic or zero data loss under every disaster.

**Follow-ups:** How does a database provide durability? Does every NoSQL database lack ACID?

**Example:** A money transfer needs a transaction or equivalent invariant-preserving protocol so debits and credits cannot silently diverge.

### 16. Explain dirty, non-repeatable, and phantom reads.
**Answer:** A dirty read sees uncommitted data. A non-repeatable read sees a changed value when the same row is read again. A phantom read sees a changed set of rows matching a predicate.

**Reasoning/trade-off:** Isolation levels vary in which anomalies they prevent; stronger isolation may increase blocking or retries.

**Mistakes:** Assuming `REPEATABLE READ` means the same behavior in every database; confusing a phantom with a changed value in an existing row.

**Follow-ups:** Which level does your database use by default? How does serializable isolation work?

**Example:** A reservation workflow must prevent concurrent transactions from both claiming the final available unit.

### 17. How do locking and MVCC work?
**Answer:** Locks coordinate conflicting operations, often blocking readers or writers depending on engine and isolation. MVCC (multi-version concurrency control) keeps versions so readers can see a consistent snapshot while writes proceed.

**Reasoning/trade-off:** MVCC improves many read/write workloads but creates version cleanup and long-transaction concerns; locking can cause waits and deadlocks.

**Mistakes:** Assuming MVCC eliminates all locks; leaving long-running transactions open.

**Follow-ups:** What is a deadlock? What does vacuum or version cleanup do?

**Example:** A web request that holds a transaction open while calling an external service can retain old row versions and increase contention.

### 18. How do transactions and isolation levels prevent concurrency bugs?
**Answer:** Transactions group operations. Isolation levels determine which concurrent changes are visible. Common anomalies include dirty reads, non-repeatable reads, phantoms, and lost updates; exact behavior depends on the database.

**Reasoning and trade-offs:** Stronger isolation can prevent more anomalies but may increase contention, blocking, or retries. Use constraints and atomic updates to enforce key invariants.

```sql
UPDATE inventory
SET available_qty = available_qty - :quantity
WHERE sku = :sku
 AND available_qty >= :quantity;
```

**Common mistake:** Doing a read-then-write check in application code and assuming it is safe under concurrency.

**Follow-up:** How would you handle a serialization failure or deadlock?

**Example:** An inventory decrement can be conditional and atomic, with success determined by the affected-row count.

### 19. How do you enforce “never oversell” inventory?
**Answer:** Make the inventory decrement conditional and atomic, for example `UPDATE inventory SET available = available - :qty WHERE sku = :sku AND available >= :qty`, and verify affected rows. For multi-item orders, use a transaction where feasible or a reservation/saga workflow with expiry and compensation.

**Reasoning/trade-off:** A single atomic write is simple; distributed inventory across warehouses needs allocation and reconciliation rules.

**Mistakes:** Read-then-write without concurrency control; treating a cache as authoritative stock.

**Follow-ups:** How do you handle payment failure after reservation? How do you prevent duplicate order submission?

**Example:** E-commerce systems commonly distinguish available, reserved, and fulfilled quantities to represent the lifecycle explicitly.

### 20. How do you design a banking ledger?
**Answer:** Store immutable, balanced journal entries; represent amounts in integer minor units or exact decimal types; make posting idempotent; and update account projections transactionally or rebuildably. Enforce that debits and credits balance per transaction.

**Reasoning/trade-off:** Append-only history supports audit and recovery, while balance projections accelerate reads but must reconcile to the ledger.

**Mistakes:** Using floating point; editing historical entries; relying on eventual consistency for authoritative balances.

**Follow-ups:** How do you handle reversals? What consistency does a balance read require?

**Example:** Double-entry accounting is the standard model for preserving financial invariants.

### 21. How do you make a payment or order API idempotent?
**Answer:** Accept an idempotency key scoped to the caller, persist it with the request/result under a unique constraint, and return the prior result on retries. Define key retention and conflict behavior.

**Reasoning/trade-off:** This prevents duplicate effects from network retries, but requires durable key storage and careful handling of in-progress requests.

**Mistakes:** Generating a new key on every retry; assuming a client timeout means the server did not commit.

**Follow-ups:** What happens if the same key arrives with a different payload? When can the key expire?

**Example:** Payment APIs use idempotency keys so clients can safely retry after an uncertain network response.

## Medium: SQL Indexing and Query Performance

### 22. What does an index do?
**Answer:** An index is an auxiliary structure that helps locate rows or satisfy ordering without scanning the entire table. Common relational indexes use B-trees.

**Reasoning/trade-off:** Indexes speed selected reads but consume storage and add write, vacuum, and maintenance cost.

**Mistakes:** Indexing every column; assuming an index guarantees the optimizer will use it.

**Follow-ups:** What is index selectivity? When might a sequential scan be cheaper?

**Example:** An index on `orders(customer_id, created_at)` can accelerate a customer’s recent-order query.

### 23. What is the difference between clustered and non-clustered indexes?
**Answer:** A clustered organization determines or strongly influences the table’s row order, depending on the database. A non-clustered index is a separate lookup structure that points to rows or their identifiers. Some databases allow only one clustered organization; others use different terminology.

**Reasoning/trade-off:** Physical ordering can help range scans, while secondary indexes add write and storage costs.

**Mistakes:** Treating “clustered” as portable SQL behavior; assuming all databases store rows the same way.

**Follow-ups:** How does a secondary index find the row? What is a heap table?

**Example:** Time-ordered data may benefit from physical or partition-local ordering by timestamp, subject to engine-specific behavior.

### 24. What is a covering index?
**Answer:** An index is covering for a query when it contains all columns needed for filtering, joining, and returning results, avoiding a separate table lookup where the engine supports that optimization.

**Reasoning/trade-off:** It can reduce I/O, but wider indexes cost more storage and write work.

**Mistakes:** Making every index wide “just in case”; ignoring included-column features and engine-specific behavior.

**Follow-ups:** How do included columns differ from key columns? How can you verify index-only access?

**Example:** A listing query may be served by an index containing `(tenant_id, status, created_at)` plus the small set of displayed fields.

### 25. How do you design a composite index and choose its column order?
**Answer:** Start with the actual predicates and ordering. Commonly place equality filters before range or sort columns, then test with representative data and plans. Column order determines which prefixes are useful.

**Reasoning/trade-off:** One index rarely serves every query; optimizing a read can slow writes and increase storage.

**Mistakes:** Applying a universal “most selective first” rule; ignoring `ORDER BY`, range predicates, and workload frequency.

**Follow-ups:** Can the index support a query on only the second column? How do nulls affect it?

**Example:** `(tenant_id, status, created_at DESC)` can support a tenant-scoped status listing sorted by recency.

#### Related interview angle: What is a composite index, and how do you choose its column order?

**Answer:** A composite index covers multiple columns. Its leading columns matter: an index on `(tenant_id, status, created_at)` is generally useful for queries filtering by `tenant_id` and optionally `status`, but may not help a query filtering only on `created_at`.

**Reasoning and trade-offs:** Match the index to frequent filters and ordering, considering equality predicates, ranges, selectivity, and write volume. Validate with the target database’s execution plan.

**Common mistake:** Applying a universal “most selective column first” rule without considering query shapes.

**Follow-up:** What is a covering index, and what does it cost?

**Example:** An order-history query may use `(customer_id, order_date DESC)` to filter and return recent orders efficiently.

### 26. How do you read and interpret an execution plan?
**Answer:** Inspect scan type, join order and algorithm, estimated versus actual rows, sorts, spills, and repeated operations. Use the database’s `EXPLAIN` or `EXPLAIN ANALYZE` carefully; the latter may execute the query.

**Reasoning/trade-off:** Plans expose optimizer assumptions and expensive operations. Production-sized data and statistics matter.

**Mistakes:** Fixating on cost units as elapsed time; running an analyzing plan on a costly mutation without safeguards.

**Follow-ups:** What causes a bad cardinality estimate? How would you address a disk spill?

**Example:** A missing index on a selective filter may turn an expected index lookup into a large table scan.

#### Related interview angle: What is an execution plan?

**Answer:** It shows the database’s chosen physical operations, such as scans, joins, sorts, and aggregations. `EXPLAIN ANALYZE`-style tools can show actual runtime and row counts, but may execute the statement.

**Reasoning and trade-offs:** The plan helps identify where work occurs; plan estimates depend on statistics and data distribution.

**Common mistake:** Treating optimizer cost as elapsed time or running a mutating analysis command unsafely in production.

**Follow-up:** How do actual and estimated row counts help diagnose a bad plan?

**Example:** A nested-loop join estimated to run for a few rows may become slow when actual input cardinality is millions.

### 27. How do you optimize a slow SQL query?
**Answer:** Confirm the expected result first, capture the exact query and parameter types, inspect the execution plan, compare estimated and actual rows, and investigate scans, joins, sorts, spills, locks, and result size. Then test one change with representative data.

**Common remedies:** Add or adjust an index, rewrite a non-sargable predicate, reduce selected columns, paginate, update statistics, shorten transactions, or move analytical work off the OLTP database.

**Common mistake:** Adding indexes without measuring write cost or confirming the plan uses them.

**Follow-up:** What would you check if the plan shows an index exists but a table scan is chosen?

**Example:** Replace `WHERE YEAR(created_at) = 2026` with a timestamp range so a normal index can be considered.

### 28. What database optimization techniques apply to SQL and NoSQL?
**Answer:** Measure first, then target the actual bottleneck.

| SQL | NoSQL |
|---|---|
| Inspect execution plans and actual row counts | Inspect query metrics and partition fan-out |
| Add/select indexes based on workload | Model around partition and sort keys |
| Avoid `SELECT *`, unbounded results, and non-sargable filters | Avoid hot partitions, unbounded documents, and large items |
| Use keyset pagination for deep pages | Use continuation tokens or key-based pagination |
| Reduce lock duration and tune transaction boundaries | Tune consistency, retries, and throughput for the access pattern |
| Separate OLTP from analytics | Use projections or analytical storage for broad scans |

**Reasoning and trade-offs:** Every optimization has costs: indexes add write work, caches add staleness risk, replicas can return lagging data, and denormalization adds repair complexity.

**Common mistake:** Making changes based on generic rules without testing representative data and traffic.

**Follow-up:** Which metrics would prove the optimization worked?

**Example:** A Cosmos DB query that scans every partition may be redesigned to include the partition key; a SQL query scanning a large table may need a better access path or a narrower report model.

## Medium: NoSQL Consistency, Replication, and Data Modeling

### 29. What do CAP and PACELC explain about distributed systems?
**Answer:** CAP describes a distributed system during a network partition: it cannot guarantee both linearizable consistency and availability for every request. It must either reject or delay some requests to preserve consistency, or continue serving requests that may return stale or conflicting data.

**Benefits and trade-offs:** It provides a way to reason about failure behavior. It does not mean a product is simply “CP” or “AP” in all cases; guarantees vary by operation and configuration.

**Common mistake:** Saying CAP means every system must choose two of three all the time. The trade-off is specifically about behavior during a partition.

**Follow-up:** What does the system do when there is no partition?

**Example:** A service may reject a balance update if it cannot reach the authoritative replica, while a “like” counter may accept updates and reconcile later.

#### Related interview angle: What do CAP and PACELC say?

**Answer:** CAP describes the trade-off during a network partition: a distributed system cannot guarantee both linearizable consistency and availability for every request. PACELC adds that otherwise, systems may trade latency against consistency.

**Reasoning/trade-off:** These are design lenses, not product labels. Real systems offer different guarantees by operation, configuration, and failure state.

**Mistakes:** Calling a database simply “CP” or “AP” without specifying operation semantics and failure assumptions.

**Follow-ups:** What does “available” mean in the theorem? How does a system behave when there is no partition?

**Example:** A geographically distributed service may accept a local update quickly, then reconcile replicas, trading immediate consistency for latency.

### 30. How do ACID and BASE describe different consistency and transaction trade-offs?
**Answer:** BASE stands for Basically Available, Soft state, Eventually consistent. It describes a family of distributed-system trade-offs, not a formal guarantee that data loss or inconsistency is acceptable.

**Reasoning/trade-off:** It can suit workflows where temporary divergence is tolerable and convergence is explicit.

**Mistakes:** Treating BASE as the opposite of all transactions or as permission to ignore invariants.

**Follow-ups:** How will the application expose stale state? What is the reconciliation strategy?

**Example:** Search indexing can lag behind a committed transaction if the source database remains authoritative and indexing is retryable.

#### Related interview angle: What are ACID and BASE?

**Answer:** ACID describes transactional properties: atomicity, consistency, isolation, and durability. BASE—Basically Available, Soft state, Eventually consistent—is a broad description of systems that prioritize availability and convergence over immediate consistency.

**Benefits and trade-offs:** ACID semantics help preserve invariants in transactions. Eventual consistency can support responsive distributed writes, but requires the application to tolerate stale reads and define conflict resolution.

**Common mistake:** Assuming all NoSQL databases lack transactions, or that BASE means data loss is acceptable.

**Follow-up:** Could one application use ACID for its ledger and eventual consistency for its search index?

**Example:** The payment record can commit transactionally while a search or analytics projection updates asynchronously.

### 31. What is eventual consistency, and what does it mean for applications?
**Answer:** Replicas may temporarily return different values, but converge if updates stop and replication succeeds.

**Reasoning/trade-off:** It can improve availability and geographic latency, but applications must tolerate stale reads, retries, and conflict resolution.

**Mistakes:** Treating eventual consistency as “inconsistent forever”; using stale replica data for a decision that requires current state.

**Follow-ups:** What is read-your-writes consistency? How do you resolve concurrent updates?

**Example:** A social profile’s follower count can tolerate brief replica lag better than a financial balance.

#### Related interview angle: What are BASE and eventual consistency?

**Answer:** Eventual consistency means replicas may temporarily disagree but converge if updates stop and replication succeeds. BASE is a broad description of systems prioritizing availability and convergence; it is not a specific consistency contract.

**Reasoning and trade-offs:** Eventual consistency can reduce latency and improve availability, but callers must tolerate stale reads and define conflict handling.

**Common mistake:** Using a potentially stale replica for a business decision that must reflect the latest committed update.

**Follow-up:** What is read-your-writes consistency, and when is it needed?

**Example:** A profile update may be eventually visible in recommendations, while the immediate profile page should show the user’s own write.

### 32. How do you choose a consistency level?
**Answer:** Start from business invariants and user-visible staleness tolerance. Use strong or serializable semantics for critical coordination; use eventual or bounded-staleness reads for derived, non-critical views.

**Reasoning/trade-off:** Stronger consistency can add latency, coordination, or retry costs; weaker consistency requires conflict handling and clear UX.

**Mistakes:** Choosing a consistency level globally rather than per operation; confusing durability with read freshness.

**Follow-ups:** What is the invariant? How does the system behave during a region outage?

**Example:** A bank balance and a social “like” count have different consistency needs.

### 33. How do replication and quorum reads and writes work?
**Answer:** Replication copies data to multiple nodes. In a quorum system with replication factor $N$, a write quorum $W$ and read quorum $R$ may overlap when $R + W > N$, helping reads observe recent writes, subject to system semantics and failures.

**Reasoning/trade-off:** Higher quorums can improve consistency but increase latency and reduce availability during failures.

**Mistakes:** Treating quorum overlap as a universal linearizability guarantee; ignoring concurrent writes, hinted handoff, or replica repair.

**Follow-ups:** What if replicas are unavailable? How does read repair work?

**Example:** Dynamo-style systems expose tunable consistency choices so a service can trade latency/availability against freshness.

#### Related interview angle: What are replication and quorum reads/writes?

**Answer:** Replication keeps copies of data on multiple nodes. Quorum settings determine how many replicas must acknowledge a read or write. In some quorum systems, `R + W > N` gives overlap between a read set and a write set, but does not universally guarantee linearizability.

**Reasoning and trade-offs:** Larger quorums may improve freshness but increase latency and reduce availability during failures.

**Common mistake:** Treating quorum overlap as a universal consistency proof without accounting for the database’s conflict, repair, and failure semantics.

**Follow-up:** What is read repair? What happens when a replica is unavailable?

**Example:** A high-value update may use a stronger write/read setting than a non-critical view counter.

### 34. What is partitioning, and how is it different from sharding?
**Answer:** Partitioning splits a table or dataset into manageable subsets, often within one database cluster. Sharding distributes ownership across independent database nodes. Implementations and terms vary.

**Reasoning/trade-off:** Partitioning can improve pruning and maintenance; sharding adds horizontal capacity but complicates routing, joins, and transactions.

**Mistakes:** Treating partitioning as automatic speedup; sharding before optimizing schema, indexes, or workload.

**Follow-ups:** Range, list, or hash partitioning? How do you repartition without downtime?

**Example:** A large event table partitioned by month can make retention deletion and time-range scans cheaper.

### 35. How do you select a NoSQL partition key and avoid hot partitions?
**Answer:** A partition key maps records to storage partitions. A hot partition receives disproportionate traffic or data, limiting throughput even if the cluster has spare capacity.

**Reasoning/trade-off:** A high-cardinality, well-distributed key helps balance load; query patterns may push toward a less evenly distributed key.

**Mistakes:** Choosing a timestamp-only key when all current writes land on one partition; using random keys that make required range queries costly.

**Follow-ups:** How do you detect skew? Can you salt a key and still query efficiently?

**Example:** A popular live event can create a hot key if all engagement counters are stored under one event ID.

#### Related interview angle: How do you choose a NoSQL partition key?

**Answer:** Start with the dominant reads and writes. The key should distribute traffic and storage while allowing common requests to target a small number of partitions.

**Reasoning and trade-offs:** A tenant or customer key can provide locality, but a very large or active tenant may become hot. Randomized keys distribute writes but can make range queries expensive.

**Common mistake:** Choosing a timestamp-only key for a high-write stream, concentrating writes on the newest partition.

**Follow-up:** How would you detect and mitigate a hot partition?

**Example:** Telemetry may partition by device and time bucket rather than date alone.

### 36. What are NoSQL secondary indexes, and what are their trade-offs?
**Answer:** It provides lookup by an attribute other than the primary partition/key access path. It may be local to a partition or global across partitions.

**Reasoning/trade-off:** Secondary indexes improve query flexibility but can require extra storage, write amplification, distributed coordination, or eventual maintenance.

**Mistakes:** Assuming a secondary-index query has the same latency or consistency as a primary-key read.

**Follow-ups:** Is the index global or local? How stale can it be?

**Example:** A document store may index `email` for lookup while documents remain keyed by account ID.

#### Related interview angle: What are TTL and secondary indexes in NoSQL systems?

**Answer:** TTL (time to live) expires items after a configured duration. A secondary index supports access by attributes other than the primary key.

**Reasoning and trade-offs:** TTL simplifies cleanup, but expiration may be asynchronous. Secondary indexes add storage and write cost and may have consistency or partition-scope limits.

**Common mistake:** Treating TTL as an exact-time deletion guarantee or as the only required personal-data deletion mechanism.

**Follow-up:** How do backups, caches, and derived data handle deletion?

**Example:** Session tokens can use TTL; a customer’s persistent account record needs a separate retention policy.

### 37. When should related data be embedded or referenced in a document store?
**Answer:** Embed related data when it is read and updated as one bounded aggregate. Reference it when it is shared, independently updated, or unbounded.

**Reasoning/trade-off:** Embedding reduces read round trips but duplicates data and can hit document-size or contention limits. References reduce duplication but require additional reads.

**Mistakes:** Embedding unbounded arrays; referencing every small child and recreating relational joins manually.

**Follow-ups:** How do you update embedded data atomically? What is the maximum aggregate size?

**Example:** An order can embed a snapshot of purchased item names and prices while referencing the canonical product catalog.

#### Related interview angle: What is embedding versus referencing in a document database?

**Answer:** Embed related data when it is bounded and commonly read or updated with its parent. Reference it when it is shared, independently updated, or unbounded.

**Reasoning and trade-offs:** Embedding can reduce round trips and allow atomic document updates; referencing limits duplication and growth but can require more reads.

**Common mistake:** Embedding an unbounded activity or message array inside a single customer document.

**Follow-up:** Which fields should be copied as an immutable snapshot?

**Example:** An order can embed purchased item names and prices while referencing the canonical product record.

### 38. What are TTL and retention policies?
**Answer:** TTL (time to live) automatically expires data after a configured duration. Retention policies define how long data is kept, archived, or deleted.

**Reasoning/trade-off:** TTL simplifies ephemeral data cleanup but deletion may be asynchronous and unsuitable as an exact-time guarantee.

**Mistakes:** Using TTL as the only GDPR deletion mechanism without checking replicas, backups, logs, and derived data.

**Follow-ups:** How do you prove deletion? What happens to expired data in backups?

**Example:** Short-lived session tokens can expire automatically while audit records follow a separate retention schedule.

## Hard: Distributed Architecture and Data Operations

### 39. How do you design a sharding strategy?
**Answer:** Select a shard key that distributes load and aligns with dominant access patterns; keep routing deterministic; plan for resharding, cross-shard queries, and tenant skew. Monitor per-shard capacity and rebalance before saturation.

**Reasoning/trade-off:** Good locality reduces scatter-gather requests. A key that is too tenant-centric can overload a large tenant; one that is too random can make common reads expensive.

**Mistakes:** Choosing a shard key solely for uniqueness; assuming shards will remain evenly loaded.

**Follow-ups:** How do you move a shard online? How do you handle a tenant larger than one shard?

**Example:** SaaS systems often shard by tenant for isolation, with explicit strategies for exceptionally large tenants.

### 40. How do you model a social feed at scale?
**Answer:** Use a hybrid fan-out model: push posts to feeds for ordinary users, but pull or merge on read for high-follower accounts. Keep posts canonical and feed entries as rebuildable references or snapshots.

**Reasoning/trade-off:** Fan-out-on-write speeds reads but creates write amplification; fan-out-on-read avoids huge writes but increases read work.

**Mistakes:** One strategy for all accounts; treating the feed cache as the source of truth.

**Follow-ups:** How do you rank posts? How do you delete or edit a post already fanned out?

**Example:** Instagram engineering has publicly discussed ranking and feed systems; hybrid fan-out is a common design response to skewed follower counts.

### 41. How should services handle schema evolution?
**Answer:** Use versioned migrations and an expand-migrate-contract approach: add backward-compatible schema, deploy compatible readers/writers, backfill, switch usage, then remove old fields in a later release.

**Reasoning/trade-off:** Staged changes support rolling deployments but temporarily increase schema complexity.

**Mistakes:** Renaming/dropping a live column in one deployment; running an unbounded blocking migration on a hot table.

**Follow-ups:** How do you backfill safely? How do you roll back after writes use the new field?

**Example:** Flyway or Liquibase can track ordered migrations, while application releases remain compatible across deployment stages.

### 42. How do you handle schema and data migrations safely?
**Answer:** Use versioned migrations and an expand-migrate-contract approach: add compatible schema, deploy readers/writers that support both versions, backfill in batches, switch usage, then remove old schema in a later release.

**Reasoning and trade-offs:** This supports rolling deploys but temporarily requires compatibility across application versions. Large backfills need throttling, checkpoints, and monitoring.

**Common mistake:** Renaming or dropping a live column in the same deployment that first changes application code.

**Follow-up:** How do you roll back after new code writes data the old version cannot read?

**Example:** Flyway or Liquibase can track relational migrations; document stores still need versioned document shapes and readers that tolerate old records.

### 43. How do you keep SQL and NoSQL read models synchronized?
**Answer:** Avoid relying on two independent writes in the request path. Use a transactional outbox, CDC (change data capture), or the database’s change feed, then make consumers idempotent and retryable.

**Reasoning and trade-offs:** Derived stores become eventually consistent, but events can be replayed and the read model can be rebuilt from the source of truth.

**Common mistake:** Assuming a database write and a message publish either both succeed or both fail without a distributed transaction.

**Follow-up:** How do you handle duplicate event delivery and schema changes?

**Example:** An order transaction can write both the order and an outbox event; a consumer updates search or analytics storage.

### 44. What is a message queue’s role in database-backed systems?
**Answer:** A queue decouples producers and consumers, buffers bursts, and enables asynchronous work. A transactional outbox records an event in the same database transaction as the business change, then publishes it reliably.

**Reasoning/trade-off:** Queues improve resilience but introduce duplicate delivery, lag, ordering, and poison-message handling.

**Mistakes:** Assuming “exactly once” end-to-end; publishing before the database transaction commits.

**Follow-ups:** How do consumers become idempotent? How do you detect and replay a dead-letter message?

**Example:** An order service can commit an order and outbox event together, then asynchronously notify fulfillment.

### 45. What is the outbox pattern, and why is it used?
**Answer:** The service writes its business change and an event row in one database transaction. A separate publisher delivers the event to a queue or stream. Consumers must handle retries and duplicates idempotently.

**Reasoning and trade-offs:** It prevents the common failure where the database commits but event publication fails, or vice versa. It adds publisher, retry, and outbox-cleanup operations.

**Common mistake:** Assuming the outbox gives exactly-once delivery.

**Follow-up:** How will you detect a stuck outbox, and how can consumers deduplicate events?

**Example:** An order service commits an order and an `OrderCreated` outbox row together; a worker publishes it for fulfillment.

### 46. What is polyglot persistence?
**Answer:** Using different storage systems for different bounded workloads, with a clearly named source of truth and explicit synchronization.

**Reasoning/trade-off:** It can fit each workload well, but increases operational, security, and consistency complexity.

**Mistakes:** Adding a second database without owning replication, backup, access control, and failure behavior.

**Follow-ups:** How do you propagate changes? How do you rebuild a derived store?

**Example:** A product service may keep canonical catalog data in SQL and publish a search projection to Elasticsearch-like search infrastructure.
47. How would you migrate from SQL to NoSQL?
    **Answer:** Define the target access patterns and invariants first. Build the new model, backfill from a consistent snapshot, capture ongoing changes (CDC or outbox), dual-read or shadow-compare, cut over gradually, and retain a rollback path until validated.

**Reasoning/trade-off:** Online migration limits downtime but temporarily increases cost and complexity. Dual writes require ordering, retries, and reconciliation.

**Mistakes:** Big-bang migration without reconciliation; assuming schema conversion preserves semantics.

**Follow-ups:** How do you handle deletes during backfill? What metrics decide cutover?

**Example:** Search and analytics projections are often rebuilt from a transactional source using CDC rather than treated as independent authorities.

### 48. What is caching, and what problems does it introduce?
**Answer:** Caching stores frequently accessed data closer to the application to reduce latency and database load. Common approaches include cache-aside, read-through, and write-through.

**Reasoning and trade-offs:** Caches can improve performance but create stale data, cache stampedes, invalidation complexity, and extra operational cost.

**Common mistake:** Treating a cache as the authoritative source for critical balances or inventory.

**Follow-up:** How would you prevent a thundering herd when a hot key expires?

**Example:** A product page may use cache-aside with jittered TTLs, while checkout validates stock in the authoritative database.

### 49. How should backups, disaster recovery, and observability be designed?
**Answer:** Define RPO (maximum acceptable data loss) and RTO (maximum recovery time) per service. Use encrypted, isolated backups, point-in-time recovery where needed, replica failover, and regular restore/failover tests. Monitor latency, errors, saturation, replication lag, queue lag, backup age, and business invariants.

**Reasoning/trade-off:** Replication helps availability but can copy corruption or deletion; backups provide a separate recovery path.

**Mistakes:** Treating a replica as a backup; measuring backup success without testing restore.

**Follow-ups:** What are the RPO/RTO targets? How do you test regional recovery?

**Example:** A payment ledger may require a tighter RPO and more rigorous restore tests than a rebuildable search index.

## Hard: Cloud Operations, Security, and Governance

### 50. How would you deploy SQL and NoSQL databases to AWS or Azure?
**Answer:** First choose a managed or self-managed deployment based on operational capability, required engine features, compliance, scale, and cost. Managed services reduce infrastructure maintenance, but teams still own schema design, access control, monitoring, recovery, and application behavior.

| Workload | AWS examples | Azure examples |
|---|---|---|
| Relational SQL | Amazon RDS or Aurora | Azure SQL Database, Azure SQL Managed Instance, Azure Database for PostgreSQL |
| Key-value / document | DynamoDB, DocumentDB | Azure Cosmos DB |
| Cache / in-memory | ElastiCache | Azure Managed Redis |
| Graph | Neptune | Cosmos DB graph APIs |

**Deployment outline:**
1. Define region, availability, capacity, data residency, RPO, and RTO.
2. Select the database engine/API and managed service.
3. Deploy network boundaries, private access, and security rules using IaC such as Terraform, CloudFormation, or Bicep.
4. Configure encryption, identities, least-privilege roles, secrets, backups, and monitoring.
5. Apply versioned schema migrations through a controlled release pipeline.
6. Test failover, restore, scaling, and application reconnection before production.
7. Monitor cost and workload signals, including SQL latency/locks or NoSQL partition utilization/throttling.

**Reasoning and trade-offs:** Managed services simplify patching and routine operations but do not remove the need for capacity planning, restore testing, and database-specific design. Confirm current service availability, feature support, and pricing in the target region.

**Common mistake:** Treating deployment as “create a database and point the application at it,” without private networking, credentials, backups, migrations, or recovery tests.

**Follow-up:** How would you deploy a schema change without downtime? How would you fail over to another region?

**Example:** A Spring Boot service can use a managed relational database for orders, a managed key-value store for sessions, and IaC plus a migration pipeline to provision and evolve both.

### 51. What are RPO and RTO?
**Answer:** RPO (Recovery Point Objective) is the maximum acceptable data loss measured in time. RTO (Recovery Time Objective) is the maximum acceptable time to restore service after a failure.

**Reasoning and trade-offs:** Tighter targets usually require more investment in replication, backups, automation, and operational testing. Replication improves availability but can also replicate accidental deletion or corruption, so it does not replace backups.

**Common mistake:** Claiming a backup strategy meets an RTO without testing restoration.

**Follow-up:** How do you test recovery from regional failure?

**Example:** A critical ledger may have stricter recovery targets than a rebuildable analytics index.

### 52. How do you secure and operate a cloud database?
**Answer:** Use private networking, TLS, encryption at rest, identity-based access, least-privilege roles, secret rotation, audit logs, and tested backup policies. Monitor user-visible latency as well as database saturation, replication lag, storage growth, throttling, and connection usage.

**Reasoning and trade-offs:** Encryption protects data but does not replace authorization or data minimization. Monitoring should include signals that reveal correctness and recovery risks, not only CPU.

**Common mistake:** Putting credentials in application configuration or assuming the cloud provider handles application-level access controls.

**Follow-up:** How do you rotate a credential without downtime? How do you meet deletion and retention requirements?

**Example:** A tenant-scoped service should authorize tenant access in the application and avoid cross-tenant cache keys, even when the database is encrypted.

### 53. How do you secure database access and prevent SQL injection?
**Answer:** Use parameterized queries, least-privilege service identities, RBAC, secret rotation, TLS, encryption at rest, network restrictions, audited break-glass access, and safe handling of logs. Never concatenate untrusted input into SQL.

**Reasoning/trade-off:** Prepared statements separate code from values; dynamic identifiers require allowlists because they generally cannot be bound as parameters.

**Mistakes:** Escaping strings manually; using a shared administrator account; logging credentials or PII.

**Follow-ups:** How do you rotate credentials without downtime? How do you authorize tenant-scoped data access?

**Example:** A query should bind values, e.g. `SELECT * FROM users WHERE email = ?`, instead of concatenating an email into SQL.

### 54. How do you handle PII and GDPR requirements?
**Answer:** Inventory personal data, minimize collection, define purpose and retention, restrict access, encrypt, audit use, support access/export/deletion workflows, and account for replicas, caches, logs, backups, and derived data.

**Reasoning/trade-off:** Deletion and auditability can conflict; design retention and legal-hold policies explicitly and involve privacy/legal stakeholders.

**Mistakes:** Assuming encryption alone satisfies GDPR; promising immediate deletion from immutable backups without a documented expiry/recovery process.

**Follow-ups:** How do you erase data in event logs? How do you handle pseudonymization and legal holds?

**Example:** A user deletion workflow may erase direct profile data and enqueue removal from search indexes and derived stores while honoring documented backup retention.

### 55. What database SLOs and metrics would you define?
**Answer:** Set service-level objectives for latency and availability at the API boundary, then monitor database saturation and correctness indicators: p50/p95/p99 latency, errors, connections, CPU, I/O, lock waits, cache hit ratio, replication lag, storage growth, queue lag, backup age, and invariant violations.

**Reasoning/trade-off:** Infrastructure metrics explain symptoms; user-facing SLOs show impact. Alerts should be actionable and tied to error budgets.

**Mistakes:** Alerting on every CPU spike; measuring average latency only; omitting business consistency checks.

**Follow-ups:** How do you choose an alert threshold? How do you detect a silent data-loss bug?

**Example:** A service can alert on stale replication only when it exceeds the freshness budget for the affected endpoint.

### 56. What is a production-ready database delivery process?
**Answer:** Manage infrastructure as code; version migrations with Flyway or Liquibase; test upgrades against representative data; deploy backward-compatible changes in stages; use automated backups, restore drills, access reviews, and documented failover runbooks.

**Reasoning/trade-off:** Automation reduces drift and human error, but migrations still need review for locks, data volume, and rollback limitations.

**Mistakes:** Treating rollback as always possible after destructive data changes; testing only on an empty database.

**Follow-ups:** How do you migrate a large table online? How do you prove a restore meets RTO?

**Example:** An expand-migrate-contract release can add a nullable column, backfill in batches, switch reads/writes, and remove the old column in a later release.

## Hard: High-Level Design Scenarios

**Shared platform assumptions for Questions 57–64:** Use TLS in transit, encryption at rest, least-privilege RBAC, secrets management, parameterized SQL, versioned migrations, and infrastructure as code. Put stateless services behind health-checked load balancers; use a CDN for public static/media content where appropriate. Set explicit SLOs and alert on user-visible symptoms as well as database saturation. Estimates below are illustrative assumptions for interview sizing, not vendor benchmarks.

### 57. HLD: Design a URL shortener.
**Question:** Design a globally available short-link service.

**Answer:** Functional: create, resolve, expire, and optionally manage links. NFR: very low redirect latency, high read availability, abuse protection. Assume 100M new links/month and 10B redirects/month: about 40K average redirects/s and perhaps 10x peak; at roughly 100 bytes/link, 10GB/month raw, excluding indexes/replicas. Redirect traffic at 1KB/response is roughly 40MB/s average.

**Reasoning/trade-off:** Use a relational store for ownership, expiry, and administration, or a distributed key-value store for massive key-to-target lookups. A cache absorbs hot links. Generate collision-resistant short codes; uniqueness must be enforced at write.

**Architecture:** `Client -> CDN/edge -> LB -> redirect API -> cache -> key-value/SQL primary`; `create API -> SQL metadata + outbox -> queue -> analytics`. Shard lookup storage by short code; replicate across regions according to consistency needs. CDN cache only where redirect freshness and expiry permit.

**Reliability/operations:** Fail over to another region; backups and restore tests; target e.g. RPO minutes and RTO under an hour based on product needs. Monitor redirect p99, cache hit rate, misses, collision/retry rate, abuse signals, and replication lag.

**Mistakes:** Predictable sequential IDs; allowing cache entries to outlive link expiration; forgetting phishing/abuse controls.

**Follow-ups:** How do you generate codes without coordination? How do you support custom aliases?

**Example:** Public URL shorteners commonly separate the latency-sensitive redirect path from slower click analytics.

### 58. HLD: Design e-commerce orders and inventory.
**Question:** Design checkout that prevents overselling and survives downstream failures.

**Answer:** Functional: cart, price/tax snapshot, order creation, inventory reservation, payment, fulfillment, cancellation/refund. NFR: correct stock and payment state, idempotent retries, auditable transitions. Assume 1,000 order submissions/s peak, 3–5 inventory items/order: several thousand conditional stock writes/s. At 2KB/order and 100M orders/year, raw order data is about 200GB/year, before indexes and replicas.

**Reasoning/trade-off:** SQL is a strong fit for order and inventory invariants; search/catalog reads may use a search index or document store. Use reservation states and a saga for payment/fulfillment across services.

**Architecture:** `Client -> CDN for catalog assets -> LB -> cart/checkout`; `checkout -> SQL order + inventory transaction/reservation -> outbox -> queue -> payment/fulfillment`; caches for catalog, never authoritative stock. Shard by order ID or region/tenant only when scale requires it; inventory SKU/warehouse is the contention boundary.

**Reliability/operations:** Multi-AZ primary with failover; outbox and idempotent consumers; backups/PITR, tested restore, documented RPO/RTO. Monitor checkout success, oversell count, reservation expiry, payment mismatch, queue lag, lock waits, and DB p99.

**Mistakes:** Holding a DB transaction open during payment calls; trusting cached stock; lacking compensation for partial failure.

**Follow-ups:** How do you reserve multiple SKUs atomically? How do you handle flash sales?

**Example:** Large retailers commonly separate catalog/search from transactional checkout and use reservation/queue workflows for fulfillment.

### 59. HLD: Design a chat application.
**Question:** Design one-to-one and group chat with history and online delivery.

**Answer:** Functional: send, receive, order, read receipts, membership, history, attachments. NFR: low message latency, durable accepted messages, privacy. Assume 10M daily users × 20 messages/day = 200M/day, about 2.3K average and 10x peak 23K messages/s. At 1KB/message, raw message content is about 200GB/day; attachments belong in object storage.

**Reasoning/trade-off:** A partitioned wide-column or relational system can store conversation history by `(conversation_id, sequence/time)`. Separate presence and delivery state from durable message history.

**Architecture:** `Client -> edge/LB -> WebSocket gateways -> chat service -> message store`; queue/event stream fans out to recipient gateways and notifications. Partition by conversation ID; replicate across zones. Cache recent messages and presence; CDN/object storage serves encrypted attachments.

**Reliability/operations:** Gateway reconnect and replay from cursor; durable queue and idempotent message IDs. Backups and cross-region recovery with explicit RPO/RTO. Monitor send-to-delivery p99, reconnect rate, fan-out lag, storage growth, and per-conversation hot spots.

**Mistakes:** Assuming WebSocket delivery equals durable storage; unbounded group fan-out in the request path; storing media in the message database.

**Follow-ups:** How do you preserve per-conversation ordering? How does end-to-end encryption affect search and moderation?

**Example:** Messaging systems commonly separate persistent conversation history from transient presence and socket connections.

### 60. HLD: Design a social-media news feed.
**Question:** Design a ranked feed for a large social network.

**Answer:** Functional: publish, follow, rank, paginate, hide/delete. NFR: low read latency, eventual freshness acceptable, resilient to celebrity-scale fan-out. Assume 20M daily users × 100 feed reads/day = 2B reads/day, about 23K average and 10x peak 230K reads/s. Posts and feed entries need separate capacity estimates based on write rate and fan-out.

**Reasoning/trade-off:** Use a hybrid push/pull model; canonical posts may live in a relational or document store, with feed entries in a partitioned key-value/wide-column store and ranking features in specialized stores.

**Architecture:** `Client -> CDN for media -> LB -> feed API -> cache -> feed store`; publish path writes post and outbox, then queue-driven fan-out. Shard feed by user ID; pull high-follower accounts at read time. Replicate read models; caches are rebuildable.

**Reliability/operations:** Queue replay, idempotent fan-out, tombstones for deletion, backup canonical posts and follow graph. Monitor feed p99, freshness lag, queue backlog, cache hit rate, and celebrity skew.

**Mistakes:** Fan-out every post to every follower synchronously; treating rankings as permanent data; ignoring deletion propagation.

**Follow-ups:** How do you paginate while new posts arrive? How do you prevent duplicate feed entries?

**Example:** Instagram has publicly described large-scale feed ranking; hybrid fan-out is a common way to manage skew.

### 61. HLD: Design ride-hailing location tracking.
**Question:** Design driver location updates and nearby-driver lookup.

**Answer:** Functional: ingest location, discover nearby available drivers, dispatch, track trips. NFR: fresh geo data, low dispatch latency, graceful handling of stale devices. Assume 1M active drivers update every 5s: 200K updates/s. At 200 bytes/update, payload is roughly 40MB/s before protocol and replication overhead.

**Reasoning/trade-off:** Keep current location in a fast geo-aware in-memory or key-value index, and stream historical telemetry separately. Partition spatially, but split hot urban cells dynamically.

**Architecture:** `Driver app -> edge/LB -> location ingestion -> stream -> geo index + time-series/object store`; dispatch reads nearby cells, then confirms availability. Replicate within region; avoid cross-region coordination for every ping. CDN is not useful for live location, but may serve static maps/assets.

**Reliability/operations:** Stale-location TTL, reconnect buffering, regional failover, tested backups for trip records. Monitor update lag, location age, geo-query p99, dropped updates, cell skew, and dispatch success.

**Mistakes:** Storing every ping in the transactional trip database; assuming GPS is exact; using one global hot partition.

**Follow-ups:** How do you query nearby drivers efficiently? How do you handle a city event causing hotspot skew?

**Example:** Uber has publicly released H3, a hierarchical geospatial indexing system useful for spatial aggregation and partitioning.

### 62. HLD: Design a banking ledger.
**Question:** Design transfers with auditability, correctness, and recovery.

**Answer:** Functional: open accounts, post transfers, show balances/statements, reverse transactions. NFR: strict correctness, durable audit history, controlled access. Assume 5K transfers/s peak; at 1KB per transfer plus two journal legs, roughly 10K ledger rows/s and ~0.9TB/year raw at 1KB/transfer, before indexes and replicas.

**Reasoning/trade-off:** Use a strongly consistent relational database for posting within a bounded ledger domain. Store immutable balanced entries; use transactional balance projections. Avoid caching authoritative balances.

**Architecture:** `Client -> WAF/LB -> banking API -> relational primary/consensus-backed cluster`; outbox -> queue -> statements/notifications/analytics. Shard by account or ledger domain only with an explicit cross-shard transfer protocol. Replicate synchronously within failure domain as required; async DR replication depends on RPO.

**Reliability/operations:** Strict RBAC, dual control for sensitive operations, encryption and audit logging; point-in-time backups and restore drills. Monitor posting latency, rejected invariant checks, reconciliation variance, replication lag, and unusual access.

**Mistakes:** Floating-point currency; mutable ledger rows; cross-region async replication while promising zero data loss.

**Follow-ups:** How do you transfer between shards? How do you reconcile balances?

**Example:** Double-entry accounting is a proven design for balanced, auditable financial records.

### 63. HLD: Design IoT telemetry ingestion.
**Question:** Design ingestion, retention, and querying for device telemetry.

**Answer:** Functional: authenticate devices, ingest metrics, query recent/history, alert on thresholds. NFR: absorb bursts, tolerate delayed/repeated events, control storage cost. Assume 1M devices send once/minute: about 16.7K events/s. At 200 bytes/event, raw data is about 288GB/day before indexes and replication.

**Reasoning/trade-off:** Stream ingestion into partitioned time-series or wide-column storage, with object-store archival and downsampled aggregates. Use device/time or tenant/time access patterns; avoid a single timestamp-only hot partition.

**Architecture:** `Devices -> IoT gateway/LB -> durable queue/stream -> validation/aggregation -> hot time-series store + object storage`; query API uses cache for dashboard aggregates. Partition by device group and time bucket; replicate queue and hot store across zones. CDN serves dashboard static assets, not private telemetry.

**Reliability/operations:** Backpressure, replay, deduplication by event ID, retention/TTL, backup and archive restore tests. Monitor ingest lag, rejected/auth failures, partition skew, query p99, retention cost, and missing-device rates.

**Mistakes:** Writing unbounded raw data to expensive primary storage; assuming device timestamps are trustworthy.

**Follow-ups:** How do you handle late-arriving data? What retention differs for raw versus aggregate data?

**Example:** Industrial telemetry platforms commonly use a hot/cold tier: recent queryable data plus cheaper long-term object storage.

### 64. HLD: Design a multi-tenant SaaS platform.
**Question:** Design data isolation and scaling for a SaaS product with many organizations.

**Answer:** Functional: tenant-scoped users, data, roles, billing, and audit. NFR: strong tenant isolation, predictable noisy-neighbor limits, compliance, and per-tenant recovery where required. Estimate from assumptions, e.g. 10K tenants × 100 active users, then derive peak tenant-scoped requests; never size only from total averages.

**Reasoning/trade-off:** Shared relational tables with `tenant_id` are economical and easy to operate; schema-per-tenant or database-per-tenant improves isolation/customization but increases operational cost. Use tiering for large or regulated tenants.

**Architecture:** `Client -> CDN for public assets -> WAF/LB -> stateless services -> tenant-aware SQL cluster`; queue for asynchronous work; cache keys include tenant ID. Partition/shard by tenant or tenant group, with a plan for oversized tenants. Encrypt data and manage tenant-scoped authorization centrally.

**Reliability/operations:** Automated provisioning via IaC; migration orchestration; per-tenant limits; tested backup/restore and tenant export/delete. Monitor latency/errors by tenant, noisy-neighbor usage, migration status, and access violations.

**Mistakes:** Relying on client-provided tenant IDs without authorization checks; omitting tenant ID from cache keys or unique constraints.

**Follow-ups:** How would you move a large tenant to a dedicated database? How do you support tenant-specific data residency?

**Example:** SaaS providers commonly use a shared-by-default model with dedicated deployments for unusually large or regulated customers.

## Hard: Low-Level Design Scenarios

**Conventions for Questions 65–72:** Use `snake_case` names, UTC timestamps, UUID or Snowflake-style identifiers, explicit constraints, and parameterized queries. DDL is illustrative; adapt types and syntax to the target database. API examples show the shape of the contract, not authentication details.

### 65. LLD: What schema and API would you use for the URL shortener?
**Question:** Define the short-link record, uniqueness, expiry, and create/resolve APIs.

**Answer:**
```sql
CREATE TABLE short_links (
 code VARCHAR(16) PRIMARY KEY,
 owner_id UUID,
 target_url TEXT NOT NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
 expires_at TIMESTAMPTZ,
 CONSTRAINT valid_expiry CHECK (expires_at IS NULL OR expires_at > created_at)
);
CREATE INDEX short_links_owner_created_idx
 ON short_links (owner_id, created_at DESC);
```
`POST /v1/links` accepts `{ "url": "...", "expires_at": "..." }`, returns code and URL. `GET /{code}` returns redirect or not found/gone.

**Reasoning/trade-off:** Code is the lookup key; owner listing uses a secondary index. Store normalized URL policy and abuse status separately if required.

**Mistakes:** No uniqueness/collision retry; unsafe redirect validation; storing secrets in public URLs.

**Follow-ups:** How do you make custom aliases unique? How do you delete a link across caches?

**Example:** Public short-link services commonly optimize the code-to-target lookup separately from owner management.

### 66. LLD: What schema and API would you use for orders and inventory?
**Question:** Model order snapshots, inventory, and idempotent checkout.

**Answer:**
```sql
CREATE TABLE inventory (
 sku UUID NOT NULL,
 warehouse_id UUID NOT NULL,
 available_qty INTEGER NOT NULL CHECK (available_qty >= 0),
 reserved_qty INTEGER NOT NULL CHECK (reserved_qty >= 0),
 PRIMARY KEY (sku, warehouse_id)
);
CREATE TABLE orders (
 order_id UUID PRIMARY KEY,
 tenant_id UUID NOT NULL,
 customer_id UUID NOT NULL,
 status VARCHAR(24) NOT NULL,
 idempotency_key VARCHAR(128) NOT NULL,
 total_minor BIGINT NOT NULL CHECK (total_minor >= 0),
 currency CHAR(3) NOT NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
 UNIQUE (tenant_id, idempotency_key)
);
CREATE TABLE order_items (
 order_id UUID NOT NULL REFERENCES orders(order_id),
 line_no INTEGER NOT NULL,
 sku UUID NOT NULL,
 item_name_snapshot TEXT NOT NULL,
 unit_price_minor BIGINT NOT NULL,
 quantity INTEGER NOT NULL CHECK (quantity > 0),
 PRIMARY KEY (order_id, line_no)
);
```
`POST /v1/orders` takes an idempotency key and item list; returns order state and reservation status.

**Reasoning/trade-off:** Price/name snapshots preserve what was purchased; inventory is keyed by SKU and warehouse. Use a transaction for local reservation and outbox publication.

**Mistakes:** Relying on current catalog price for historical orders; missing tenant-scoped idempotency uniqueness.

**Follow-ups:** How do you reserve several SKUs? How do you model refunds?

**Example:** Order systems commonly preserve immutable line-item snapshots for support and audit.

### 67. LLD: What schema and API would you use for chat?
**Question:** Store ordered messages and support cursor-based history.

**Answer:**
```sql
CREATE TABLE messages (
 conversation_id UUID NOT NULL,
 sequence_no BIGINT NOT NULL,
 message_id UUID NOT NULL UNIQUE,
 sender_id UUID NOT NULL,
 body JSONB NOT NULL,
 created_at TIMESTAMPTZ NOT NULL,
 PRIMARY KEY (conversation_id, sequence_no)
);
CREATE INDEX messages_sender_created_idx
 ON messages (sender_id, created_at DESC);
```
A wide-column implementation can use `(conversation_id, time_bucket)` as partition key and sequence/time as sort key. `POST /v1/conversations/{id}/messages` accepts a client message ID; `GET .../messages?after=cursor` returns a page and next cursor.

**Reasoning/trade-off:** Conversation-scoped ordering and pagination are primary; partitioning by time bucket bounds very large conversations.

**Mistakes:** Offset pagination for growing histories; trusting client timestamps for ordering; no membership authorization.

**Follow-ups:** How do you allocate sequence numbers? How do you store read receipts?

**Example:** Chat stores often partition history by conversation and keep presence in a separate, short-lived system.

### 68. LLD: What schema and API would you use for a social feed?
**Question:** Model canonical posts and per-user feed entries.

**Answer:**
```sql
CREATE TABLE posts (
 post_id UUID PRIMARY KEY,
 author_id UUID NOT NULL,
 body JSONB NOT NULL,
 created_at TIMESTAMPTZ NOT NULL,
 deleted_at TIMESTAMPTZ
);
CREATE TABLE feed_entries (
 user_id UUID NOT NULL,
 rank_time TIMESTAMPTZ NOT NULL,
 post_id UUID NOT NULL,
 source_author_id UUID NOT NULL,
 PRIMARY KEY (user_id, rank_time, post_id)
);
CREATE INDEX posts_author_created_idx
 ON posts (author_id, created_at DESC);
```
`POST /v1/posts` publishes; `GET /v1/feed?cursor=...&limit=...` returns entries plus a stable cursor.

**Reasoning/trade-off:** The post is canonical; feed entries are derived and rebuildable. Cursor pagination avoids offset cost and page drift.

**Mistakes:** Making feed entries the only copy of a post; failing to propagate deletion or privacy changes.

**Follow-ups:** How do you merge pulled celebrity posts? How do you deduplicate hybrid fan-out?

**Example:** Large social systems commonly separate canonical content from personalized feed read models.

### 69. LLD: What schema and API would you use for ride-hailing locations?
**Question:** Store current location and trip history without making the trip database a ping sink.

**Answer:**
```json
{
"driver_id": "uuid",
"cell_id": "spatial-cell-id",
"location": { "lat": 37.77, "lon": -122.42 },
"heading": 95,
"updated_at": "2026-11-08T12:00:00Z",
"available": true,
"trip_id": null
}
```
Use a geo index keyed by spatial cell for current locations; write durable trip events separately. `PUT /v1/drivers/me/location` accepts coordinates, timestamp, and sequence; `GET /v1/drivers/nearby?cell=...` is internal and authorization-scoped.

**Reasoning/trade-off:** Current state is replaceable and TTL-bound; trip records are durable. Validate coordinates and reject stale sequence numbers.

**Mistakes:** Publicly exposing driver coordinates; accepting client timestamps without bounds; mixing current location with financial trip records.

**Follow-ups:** How do you expand neighboring cells? How do you handle GPS jitter?

**Example:** H3-style spatial cells provide hierarchical geo partitioning for nearby searches and aggregation.

### 70. LLD: What schema and API would you use for a banking ledger?
**Question:** Model immutable postings and idempotent transfers.

**Answer:**
```sql
CREATE TABLE transfers (
 transfer_id UUID PRIMARY KEY,
 idempotency_key VARCHAR(128) NOT NULL UNIQUE,
 status VARCHAR(24) NOT NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE ledger_entries (
 entry_id UUID PRIMARY KEY,
 transfer_id UUID NOT NULL REFERENCES transfers(transfer_id),
 account_id UUID NOT NULL,
 currency CHAR(3) NOT NULL,
 amount_minor BIGINT NOT NULL CHECK (amount_minor <> 0),
 created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX ledger_account_time_idx
 ON ledger_entries (account_id, created_at DESC, entry_id);
```
`POST /v1/transfers` accepts source, destination, amount in minor units, currency, and idempotency key. A posting transaction validates funds and writes balanced entries atomically.

**Reasoning/trade-off:** Ledger entries are immutable; account balance may be a transactionally updated projection checked against the journal.

**Mistakes:** Adding a mutable “current balance” without reconciliation; failing to enforce balanced postings; leaking account existence through errors.

**Follow-ups:** How do you reverse a transfer? How do you guarantee one currency per posting?

**Example:** Double-entry ledger schemas represent a correction as a new reversal, not an edit to history.

### 71. LLD: What schema and API would you use for IoT telemetry?
**Question:** Store high-volume readings with device and time-range access.

**Answer:**
```json
{
"tenant_id": "uuid",
"device_id": "uuid",
"event_id": "uuid",
"event_time": "2026-11-08T12:00:00Z",
"metric": "temperature",
"value": 21.4,
"schema_version": 2
}
```
A wide-column key can be `(tenant_id, device_id, day_bucket)` with sort key `(event_time, event_id)`. `POST /v1/telemetry:batch` accepts bounded batches; `GET /v1/devices/{id}/metrics?from=...&to=...` queries a constrained interval.

**Reasoning/trade-off:** Bucketed partitions bound size and support time ranges. Keep schema version and event ID for evolution and deduplication.

**Mistakes:** Unbounded partition per device; trusting arbitrary metric names as indexed fields; accepting oversized batches.

**Follow-ups:** How do you deduplicate retries? How do you downsample and archive?

**Example:** Time-series systems commonly partition by entity and time bucket, then expire or archive raw data by retention policy.

### 72. LLD: What schema and API would you use for multi-tenant SaaS?
**Question:** Model tenant ownership and prevent cross-tenant access.

**Answer:**
```sql
CREATE TABLE tenants (
 tenant_id UUID PRIMARY KEY,
 name TEXT NOT NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE projects (
 tenant_id UUID NOT NULL REFERENCES tenants(tenant_id),
 project_id UUID NOT NULL,
 name TEXT NOT NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
 PRIMARY KEY (tenant_id, project_id),
 UNIQUE (tenant_id, name)
);
CREATE INDEX projects_tenant_created_idx
 ON projects (tenant_id, created_at DESC);
```
Every tenant-owned API derives tenant identity from authenticated claims, verifies authorization server-side, and scopes queries by `tenant_id`.

**Reasoning/trade-off:** Composite keys and uniqueness constraints make tenant scope explicit. Database row-level security can add defense in depth, but must be configured and tested carefully.

**Mistakes:** Trusting a request-body `tenant_id`; omitting tenant ID from cache keys, audit events, or unique constraints.

**Follow-ups:** How would you isolate regulated tenants? How do you export or erase one tenant’s data?

**Example:** Shared-table SaaS designs commonly include tenant ID in indexes and constraints; larger customers may move to dedicated storage.

## Interview Checklist

When answering a database design question, cover these points:

-**Requirements:** access patterns, invariants, latency, throughput, retention, privacy.
-**Data model:** keys, relationships, constraints, partition key, and indexes.
-**Scale:** read/write rates, storage growth, hot-key risk, and peak traffic.
-**Consistency:** what can be stale and what must be current?
-**Failure handling:** retries, idempotency, replication, backup, RPO, and RTO.
-**Security:** private access, encryption, RBAC, parameterized queries, and PII handling.
-**Operations:** migrations, monitoring, restore drills, and infrastructure as code.

A strong senior answer explains not just what database to choose, but **which requirement makes that choice appropriate, what trade-off it introduces, and how the team will verify and operate it**.