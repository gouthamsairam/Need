## SQL Sequence

A **Sequence** is a database object used to generate **unique numeric values**, commonly for primary keys.

Think of it as an automatic number generator:

```text
1 → 2 → 3 → 4 → 5 → 6 → ...
```

It is especially common in **Oracle and PostgreSQL**.

### 1. Create a sequence

```sql
CREATE SEQUENCE customer_seq
START WITH 1
INCREMENT BY 1;
```

Now the sequence can generate:

```sql
SELECT customer_seq.NEXTVAL FROM dual;
```

Oracle result:

```text
1
```

Next call:

```sql
SELECT customer_seq.NEXTVAL FROM dual;
```

Result:

```text
2
```

Next:

```text
3
```

---

## 2. Use it while inserting

For Oracle:

```sql
INSERT INTO customer
(
    customer_id,
    customer_name
)
VALUES
(
    customer_seq.NEXTVAL,
    'John'
);
```

Another insert:

```sql
INSERT INTO customer
(
    customer_id,
    customer_name
)
VALUES
(
    customer_seq.NEXTVAL,
    'David'
);
```

Result:

```text
customer_id | customer_name
------------|--------------
1            | John
2            | David
```

---

## 3. `NEXTVAL` vs `CURRVAL`

### `NEXTVAL`

Gets the **next value** from the sequence.

```sql
SELECT customer_seq.NEXTVAL FROM dual;
```

### `CURRVAL`

Gets the **current value** that was already generated in your session.

```sql
SELECT customer_seq.CURRVAL FROM dual;
```

You generally need to call `NEXTVAL` first in the session before using `CURRVAL`.

---

## 4. Sequence with different configuration

You can configure:

```sql
CREATE SEQUENCE order_seq
START WITH 1000
INCREMENT BY 1
MINVALUE 1000
MAXVALUE 999999
CACHE 50;
```

Important options:

| Option         | Meaning                        |
| -------------- | ------------------------------ |
| `START WITH`   | Initial value                  |
| `INCREMENT BY` | Increment amount               |
| `MINVALUE`     | Minimum value                  |
| `MAXVALUE`     | Maximum value                  |
| `CACHE`        | Preallocate sequence values    |
| `CYCLE`        | Start again after reaching max |
| `NOCYCLE`      | Don't restart                  |

---

## 5. Very important: Sequence does NOT guarantee gap-free IDs

Suppose:

```text
1
2
3
4
```

Application gets:

```text
5
```

but transaction fails.

The next value may be:

```text
6
```

So you can have:

```text
1
2
3
4
6
```

**5 is missing.**

This is normal.

A sequence is designed primarily for **unique number generation**, not for guaranteeing continuous numbering.

---

## 6. Sequence vs `MAX(id) + 1`

❌ Don't do this in concurrent applications:

```sql
SELECT MAX(customer_id) + 1
FROM customer;
```

Imagine two requests arrive simultaneously:

```text
Request A → MAX = 100 → generates 101
Request B → MAX = 100 → generates 101
```

Now both can try to insert:

```text
101
```

You can get a duplicate key problem.

A sequence handles concurrent requests much more safely.

---

## 7. Sequence in Spring Boot / JPA

Since you're working with Spring Boot, this is particularly important.

For Oracle/PostgreSQL, you can use:

```java
@Entity
public class Customer {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "customer_seq"
    )
    @SequenceGenerator(
        name = "customer_seq",
        sequenceName = "customer_seq",
        allocationSize = 1
    )
    private Long id;

    private String name;
}
```

Database:

```sql
CREATE SEQUENCE customer_seq
START WITH 1
INCREMENT BY 1;
```

Then when you do:

```java
Customer customer = new Customer();
customer.setName("John");

customerRepository.save(customer);
```

Hibernate/JPA obtains the ID using the sequence.

---

## 8. Sequence vs Identity

This is a common interview question.

### Sequence

```text
Application
     ↓
Hibernate
     ↓
Database Sequence
     ↓
ID
```

### Identity / Auto Increment

```text
Application
     ↓
INSERT
     ↓
Database generates ID
```

For example, MySQL commonly uses:

```sql
id BIGINT AUTO_INCREMENT
```

while Oracle commonly uses:

```sql
id NUMBER
```

with:

```sql
customer_seq.NEXTVAL
```

---

### ⭐ Interview answer

> **"A sequence is a database object that generates numeric values, typically used for primary keys. It can generate unique values concurrently without relying on MAX(id)+1. A sequence can have properties such as start value, increment, cache, minimum and maximum values. One important point is that sequences generally don't guarantee gap-free numbers because values can be consumed by rolled-back transactions or cached values."**

If you're preparing for **Java/Spring Boot interviews**, the next important topic after sequences is **Sequence vs Identity vs UUID**, especially how Hibernate handles each one.
