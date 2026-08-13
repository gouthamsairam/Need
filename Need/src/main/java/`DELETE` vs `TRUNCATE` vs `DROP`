The main difference is **what happens to the table itself, its data, and its structure**.

### `DELETE` vs `TRUNCATE` vs `DROP`

| Feature                  | `DELETE`                      | `TRUNCATE`                                     | `DROP`        |
| ------------------------ | ----------------------------- | ---------------------------------------------- | ------------- |
| Removes data             | ✅ Yes                         | ✅ Yes                                          | ✅ Yes         |
| Removes table structure  | ❌ No                          | ❌ No                                           | ✅ Yes         |
| Table remains            | ✅ Yes                         | ✅ Yes                                          | ❌ No          |
| Can use `WHERE`          | ✅ Yes                         | ❌ No                                           | ❌ No          |
| Deletes selected rows    | ✅ Yes                         | ❌ No                                           | ❌ No          |
| Generally faster         | ❌ Slower                      | ✅ Faster                                       | ✅ Very fast   |
| Transaction rollback     | ✅ Usually possible            | DB-dependent                                   | DB-dependent  |
| Resets identity/sequence | Usually ❌                     | Often ✅                                        | Table removed |
| Triggers                 | Usually fires DELETE triggers | Usually doesn't fire row-level DELETE triggers | ❌ No          |
| Indexes remain           | ✅ Yes                         | ✅ Yes                                          | ❌ No          |
| Table constraints remain | ✅ Yes                         | ✅ Yes                                          | ❌ No          |

---

## 1. `DELETE`

`DELETE` removes **rows**, but keeps the table.

```sql
DELETE FROM employees
WHERE department = 'IT';
```

Only IT employees are deleted.

The table still exists:

```text
employees
├── id
├── name
├── salary
└── department
```

You can also delete everything:

```sql
DELETE FROM employees;
```

### Key point

> **DELETE = Remove rows**

Use it when you need **conditional deletion**.

---

## 2. `TRUNCATE`

`TRUNCATE` removes **all rows** from the table but keeps the table structure.

```sql
TRUNCATE TABLE employees;
```

After this:

```text
employees
├── id
├── name
├── salary
└── department
```

The table exists, but it has no rows.

### Why is `TRUNCATE` usually faster?

`DELETE` generally processes rows as delete operations, while `TRUNCATE` is typically a **data-page/deallocation operation**, so it can remove a large amount of data much more efficiently.

You **cannot** normally do:

```sql
TRUNCATE TABLE employees
WHERE department = 'IT'; -- ❌
```

### Key point

> **TRUNCATE = Remove all rows quickly, keep the table**

---

## 3. `DROP`

`DROP` removes the **entire table**.

```sql
DROP TABLE employees;
```

After this:

```text
employees
     ❌
```

The table structure itself is gone.

You cannot do:

```sql
SELECT * FROM employees;
```

because the table no longer exists.

### Key point

> **DROP = Remove the table itself**

---

# Easy way to remember

Think about a house:

```text
DELETE
    ↓
Remove selected furniture
House remains

TRUNCATE
    ↓
Remove ALL furniture
House remains

DROP
    ↓
Destroy the entire house
```

---

# Example

Suppose we have:

```sql
CREATE TABLE employees (
    id INT,
    name VARCHAR(100),
    salary DECIMAL(10,2)
);
```

And 10,000 records.

### If you execute:

```sql
DELETE FROM employees;
```

Result:

```text
Rows:       0
Table:      EXISTS
Columns:    EXISTS
Indexes:    EXISTS
Constraints: EXISTS
```

### If you execute:

```sql
TRUNCATE TABLE employees;
```

Result:

```text
Rows:       0
Table:      EXISTS
Columns:    EXISTS
Indexes:    EXISTS
Constraints: EXISTS
```

### If you execute:

```sql
DROP TABLE employees;
```

Result:

```text
Rows:       ❌
Table:      ❌
Columns:    ❌
Indexes:    ❌
Constraints: ❌
```

---

## ⭐ Interview Question: Which one should you use?

### Delete a few records

```sql
DELETE FROM employees
WHERE id = 100;
```

### Remove all records but keep the table

```sql
TRUNCATE TABLE employees;
```

### Completely remove the table

```sql
DROP TABLE employees;
```

### One-line interview answer

> **DELETE removes selected rows and supports `WHERE`; TRUNCATE removes all rows while keeping the table structure and is generally faster; DROP removes the entire table including its structure.**

**Important:** Transaction and rollback behavior for `TRUNCATE` and `DROP` is **database-specific** (for example, Oracle, PostgreSQL, MySQL, and SQL Server differ), so don't say unconditionally in an interview that "`TRUNCATE` cannot be rolled back."
