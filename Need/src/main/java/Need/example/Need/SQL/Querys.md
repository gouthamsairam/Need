# SQL and NoSQL Interview Master Guide for Senior Java / Spring Boot Developers (2026)

For a **Senior Java/Spring Boot Developer**, database interviews go far beyond basic `SELECT`, `JOIN` and `GROUP BY`. Expect **query writing, joins, subqueries, CTEs, window functions, transactions, indexing, execution plans, concurrency, optimization, partitioning, database design** and, increasingly, **NoSQL data modelling** (MongoDB, Redis, Cassandra, DynamoDB, Azure Cosmos DB).

## How this guide is organised

| Part | Content | Use it for |
| --- | --- | --- |
| Part A | SQL levels 1-60, Basic -> Intermediate -> Advanced -> Extreme | Learning and practising SQL |
| Part A solutions | Worked answers for the advanced/extreme coding list | Coding rounds |
| Production appendix | SQL in real Spring Boot systems | Senior design discussions |
| Part B | NoSQL interview questions and answers | Polyglot persistence and NoSQL rounds |

## How to answer any database question in an interview

```text
1. Restate the requirement -> "one row per customer, latest order only"
2. Write the simplest correct SQL
3. Walk through edge cases -> NULLs, ties, duplicates, empty tables
4. Talk about performance -> index, plan, row counts
5. Talk about production -> concurrency, transactions, monitoring
```

Interviewers score the reasoning in steps 3-5 as highly as the query itself.

---

# PART A: SQL

# SQL Interview Master Guide — Basic to Extreme

## 0. Sample Database Used Throughout

We'll use these tables in examples:

```sql
employees
---------
employee_id
employee_name
department_id
manager_id
salary
hire_date
city

departments
-----------
department_id
department_name

projects
--------
project_id
project_name
department_id

employee_projects
-----------------
employee_id
project_id

customers
---------
customer_id
customer_name
city

orders
------
order_id
customer_id
order_date
amount
status

products
--------
product_id
product_name
category_id
price

order_items
-----------
order_id
product_id
quantity
price
```

---

# LEVEL 1 — SQL Fundamentals

## 1. What is SQL?

SQL = Structured Query Language.

Used for:

* Reading data
* Inserting data
* Updating data
* Deleting data
* Creating database objects
* Controlling transactions
* Managing permissions

---

# 2. SELECT

### Q1. Retrieve all employees

```sql
SELECT *
FROM employees;
```

### Q2. Retrieve specific columns

```sql
SELECT employee_id, employee_name, salary
FROM employees;
```

### Q3. Give an alias

```sql
SELECT employee_name AS name,
 salary AS employee_salary
FROM employees;
```

---

# 3. DISTINCT

### Q4. Find unique cities

```sql
SELECT DISTINCT city
FROM employees;
```

### Q5. Find unique department/city combinations

```sql
SELECT DISTINCT department_id, city
FROM employees;
```

---

# 4. WHERE

### Q6. Employees earning more than 100,000

```sql
SELECT *
FROM employees
WHERE salary > 100000;
```

### Q7. Multiple conditions

```sql
SELECT *
FROM employees
WHERE salary > 100000
AND city = 'Hyderabad';
```

---

# 5. Comparison Operators

Know:

```text
=
<>
!=
>
<
>=
<=
```

Example:

```sql
SELECT *
FROM employees
WHERE salary >= 100000;
```

---

# 6. BETWEEN

### Q8. Salary between 50K and 100K

```sql
SELECT *
FROM employees
WHERE salary BETWEEN 50000 AND 100000;
```

---

# 7. IN

```sql
SELECT *
FROM employees
WHERE city IN ('Hyderabad', 'Bangalore', 'Pune');
```

---

# 8. NOT IN

```sql
SELECT *
FROM employees
WHERE city NOT IN ('Hyderabad', 'Pune');
```

### Senior-level consideration

Understand how `NULL` affects `NOT IN`.

For example:

```sql
WHERE department_id NOT IN (10, 20, NULL)
```

can produce unexpected results because comparisons with `NULL` are `UNKNOWN`.

---

# 9. LIKE

### Q9. Names starting with A

```sql
SELECT *
FROM employees
WHERE employee_name LIKE 'A%';
```

### Ending with A

```sql
WHERE employee_name LIKE '%A';
```

### Contains A

```sql
WHERE employee_name LIKE '%A%';
```

### Exactly 5 characters

```sql
WHERE employee_name LIKE '_____';
```

---

# 10. NULL

### Q10. Find employees without manager

```sql
SELECT *
FROM employees
WHERE manager_id IS NULL;
```

Never do:

```sql
WHERE manager_id = NULL;
```

Use:

```sql
IS NULL
```

or:

```sql
IS NOT NULL
```

---

# 11. ORDER BY

```sql
SELECT *
FROM employees
ORDER BY salary DESC;
```

Multiple columns:

```sql
SELECT *
FROM employees
ORDER BY department_id ASC,
 salary DESC;
```

---

# 12. LIMIT / FETCH / TOP

Depending on database.

### PostgreSQL/MySQL

```sql
SELECT *
FROM employees
ORDER BY salary DESC
LIMIT 10;
```

### SQL Server

```sql
SELECT TOP 10 *
FROM employees
ORDER BY salary DESC;
```

### Oracle / ANSI style

```sql
SELECT *
FROM employees
ORDER BY salary DESC
FETCH FIRST 10 ROWS ONLY;
```

---

# LEVEL 2 — DML

# 13. INSERT

```sql
INSERT INTO employees
(employee_id, employee_name, department_id, salary)
VALUES
(101, 'John', 10, 100000);
```

---

# 14. INSERT Multiple Rows

```sql
INSERT INTO employees
(employee_id, employee_name, department_id, salary)
VALUES
(101, 'John', 10, 100000),
(102, 'David', 20, 120000),
(103, 'Alex', 10, 90000);
```

---

# 15. UPDATE

```sql
UPDATE employees
SET salary = 120000
WHERE employee_id = 101;
```

### Q: Increase salary by 10%

```sql
UPDATE employees
SET salary = salary * 1.10;
```

---

# 16. DELETE

```sql
DELETE FROM employees
WHERE employee_id = 101;
```

---

# 17. DELETE vs TRUNCATE vs DROP

You should know this extremely well.

| Operation | Data | Structure | WHERE |
| --------- | ---------------- | --------- | ----- |
| DELETE | Removes rows | Remains | Yes |
| TRUNCATE | Removes all rows | Remains | No |
| DROP | Removes table | Removed | No |

---

# LEVEL 3 — Aggregate Functions

Know:

```text
COUNT()
SUM()
AVG()
MIN()
MAX()
```

---

# 18. COUNT

```sql
SELECT COUNT(*)
FROM employees;
```

### Important interview question

Difference:

```sql
COUNT(*)
```

vs

```sql
COUNT(column)
```

`COUNT(column)` does not count `NULL` values.

---

# 19. Average Salary

```sql
SELECT AVG(salary)
FROM employees;
```

---

# 20. Maximum Salary

```sql
SELECT MAX(salary)
FROM employees;
```

---

# 21. Minimum Salary

```sql
SELECT MIN(salary)
FROM employees;
```

---

# 22. Total Salary

```sql
SELECT SUM(salary)
FROM employees;
```

---

# LEVEL 4 — GROUP BY

## 23. Employees per department

```sql
SELECT department_id,
 COUNT(*) AS employee_count
FROM employees
GROUP BY department_id;
```

---

# 24. Average salary per department

```sql
SELECT department_id,
 AVG(salary) AS avg_salary
FROM employees
GROUP BY department_id;
```

---

# 25. Highest salary per department

```sql
SELECT department_id,
 MAX(salary)
FROM employees
GROUP BY department_id;
```

---

# LEVEL 5 — HAVING

## 26. Departments having more than 5 employees

```sql
SELECT department_id,
 COUNT(*) AS employee_count
FROM employees
GROUP BY department_id
HAVING COUNT(*) > 5;
```

### Important

```text
WHERE → filters rows BEFORE grouping

HAVING → filters groups AFTER grouping
```

---

# 27. WHERE + GROUP BY + HAVING

```sql
SELECT department_id,
 AVG(salary) AS avg_salary
FROM employees
WHERE salary > 50000
GROUP BY department_id
HAVING AVG(salary) > 100000;
```

---

# LEVEL 6 — JOINS

This is one of the **most important interview areas**.

### Understand joins with two tiny tables

```text
employees departments
id | name | dept_id dept_id | dept_name
1 | Asha | 10 10 | IT
2 | Ravi | 20 20 | HR
3 | Meera | NULL 30 | Finance
```

| Join | Rows returned | Result in this example |
| --- | --- | --- |
| `INNER JOIN` | Only matching pairs | Asha-IT, Ravi-HR |
| `LEFT JOIN` | All left rows + matches (NULL if none) | Asha-IT, Ravi-HR, Meera-NULL |
| `RIGHT JOIN` | All right rows + matches | Asha-IT, Ravi-HR, NULL-Finance |
| `FULL OUTER JOIN` | Everything from both sides | Asha-IT, Ravi-HR, Meera-NULL, NULL-Finance |
| `CROSS JOIN` | Every combination | 3 x 3 = 9 rows |

**Memory trick:** the table written first is "left". `LEFT JOIN` means "keep everything from the left, borrow from the right where possible".

### Real-time example: a customer-support dashboard

Support wants every policy holder with their *latest* open ticket, including customers who have **no** tickets:

```sql
SELECT c.customer_id,
 c.customer_name,
 t.ticket_id,
 t.opened_at
FROM customers c
LEFT JOIN support_tickets t
 ON t.customer_id = c.customer_id
 AND t.status = 'OPEN' -- filter inside ON keeps customers without tickets
ORDER BY c.customer_id;
```

If `t.status = 'OPEN'` were placed in `WHERE`, customers without tickets would disappear — a classic bug covered in Level 54.

### Join row-multiplication trap

```text
1 customer x 3 orders x 4 payments = 12 rows
SUM(order.amount) over those rows -> 4x the real total
```

Aggregate each child table separately (CTE or subquery) before joining, or use `EXISTS` when you only need existence.

---

# 28. INNER JOIN

```sql
SELECT e.employee_name,
 d.department_name
FROM employees e
INNER JOIN departments d
 ON e.department_id = d.department_id;
```

Returns matching records.

---

# 29. LEFT JOIN

```sql
SELECT e.employee_name,
 d.department_name
FROM employees e
LEFT JOIN departments d
 ON e.department_id = d.department_id;
```

Returns:

```text
All employees
+
Matching departments
```

---

# 30. Find employees without departments

Very common interview question.

```sql
SELECT e.*
FROM employees e
LEFT JOIN departments d
 ON e.department_id = d.department_id
WHERE d.department_id IS NULL;
```

---

# 31. RIGHT JOIN

```sql
SELECT e.employee_name,
 d.department_name
FROM employees e
RIGHT JOIN departments d
 ON e.department_id = d.department_id;
```

---

# 32. FULL OUTER JOIN

```sql
SELECT e.employee_name,
 d.department_name
FROM employees e
FULL OUTER JOIN departments d
 ON e.department_id = d.department_id;
```

Returns:

```text
Matching rows
+
Employees without departments
+
Departments without employees
```

---

# 33. CROSS JOIN

```sql
SELECT e.employee_name,
 d.department_name
FROM employees e
CROSS JOIN departments d;
```

If:

```text
100 employees
10 departments
```

Result can contain:

```text
100 × 10 = 1000 rows
```

---

# 34. SELF JOIN

Extremely common.

Find employee and manager:

```sql
SELECT e.employee_name AS employee,
 m.employee_name AS manager
FROM employees e
LEFT JOIN employees m
 ON e.manager_id = m.employee_id;
```

---

# LEVEL 7 — JOIN Interview Problems

## 35. Employees earning more than their manager

```sql
SELECT e.employee_name,
 e.salary,
 m.employee_name AS manager,
 m.salary AS manager_salary
FROM employees e
JOIN employees m
 ON e.manager_id = m.employee_id
WHERE e.salary > m.salary;
```

---

# 36. Employees and project names

```sql
SELECT e.employee_name,
 p.project_name
FROM employees e
JOIN employee_projects ep
 ON e.employee_id = ep.employee_id
JOIN projects p
 ON ep.project_id = p.project_id;
```

---

# 37. Employees working on multiple projects

```sql
SELECT employee_id,
 COUNT(project_id) AS project_count
FROM employee_projects
GROUP BY employee_id
HAVING COUNT(project_id) > 1;
```

---

# 38. Customers who never placed an order

```sql
SELECT c.*
FROM customers c
LEFT JOIN orders o
 ON c.customer_id = o.customer_id
WHERE o.customer_id IS NULL;
```

---

# LEVEL 8 — SUBQUERIES

# 39. Employees earning above average salary

```sql
SELECT *
FROM employees
WHERE salary > (
 SELECT AVG(salary)
 FROM employees
);
```

---

# 40. Employee with maximum salary

```sql
SELECT *
FROM employees
WHERE salary = (
 SELECT MAX(salary)
 FROM employees
);
```

---

# 41. Second highest salary

Classic interview question.

```sql
SELECT MAX(salary)
FROM employees
WHERE salary < (
 SELECT MAX(salary)
 FROM employees
);
```

---

# 42. Third highest salary

```sql
SELECT MAX(salary)
FROM employees
WHERE salary < (
 SELECT MAX(salary)
 FROM employees
 WHERE salary < (
 SELECT MAX(salary)
 FROM employees
 )
);
```

But for a Senior Developer, prefer a window function:
A **window function in SQL** is a function that performs a calculation across a set of related rows **without collapsing those rows into a single row**.

This is the key difference from `GROUP BY`.

### 1. Simple example

Suppose we have an `employees` table:

| id | name | department | salary |
| -: | ----- | ---------- | -----: |
| 1 | John | IT | 80000 |
| 2 | David | IT | 90000 |
| 3 | Mike | IT | 70000 |
| 4 | Sarah | HR | 60000 |
| 5 | Jane | HR | 70000 |

If you want to calculate the **average salary of each department**, you could use:

```sql
SELECT department, AVG(salary)
FROM employees
GROUP BY department;
```

Result:

| department | avg_salary |
| ---------- | ---------: |
| IT | 80000 |
| HR | 65000 |

Notice that the individual employees disappeared.

With a **window function**:

```sql
SELECT
 name,
 department,
 salary,
 AVG(salary) OVER (PARTITION BY department) AS dept_avg_salary
FROM employees;
```

Result:

| name | department | salary | dept_avg_salary |
| ----- | ---------- | -----: | --------------: |
| John | IT | 80000 | 80000 |
| David | IT | 90000 | 80000 |
| Mike | IT | 70000 | 80000 |
| Sarah | HR | 60000 | 65000 |
| Jane | HR | 70000 | 65000 |

**The original rows remain.**

That's the main idea of a window function.

---

# 2. Window function syntax

The general syntax is:

```sql
function_name(...)
OVER (
 PARTITION BY ...
 ORDER BY ...
)
```

For example:

```sql
AVG(salary) OVER (
 PARTITION BY department
)
```

Think of it as:

```text
AVG(salary)
 ↓
Calculate something

OVER
 ↓
Define which rows participate
```

---

# 3. `PARTITION BY`

`PARTITION BY` divides the result into logical groups.

Example:

```sql
AVG(salary) OVER (
 PARTITION BY department
)
```

means:

```text
IT
 ├── John 80000
 ├── David 90000
 └── Mike 70000

HR
 ├── Sarah 60000
 └── Jane 70000
```

The average is calculated separately for each partition.

### Without `PARTITION BY`

```sql
AVG(salary) OVER ()
```

calculates the average across **all employees**.

```text
John 80000 ─┐
David 90000 │
Mike 70000 ├── Overall average
Sarah 60000 │
Jane 70000 ─┘
```

---

# 4. `ORDER BY` inside a window

`ORDER BY` is extremely important for functions such as `ROW_NUMBER`, `RANK`, `LAG`, `LEAD`, and running totals.

For example:

```sql
SELECT
 name,
 salary,
 ROW_NUMBER() OVER (
 ORDER BY salary DESC
 ) AS row_num
FROM employees;
```

Result:

| name | salary | row_num |
| ----- | -----: | ------: |
| David | 90000 | 1 |
| John | 80000 | 2 |
| Mike | 70000 | 3 |
| Jane | 70000 | 4 |
| Sarah | 60000 | 5 |
We're saying:

> Order employees by salary descending and assign a number to each row.

---

# 5. Most important window functions

For interviews, focus on these:

### Ranking

```sql
ROW_NUMBER()
RANK()
DENSE_RANK()
```

### Aggregate window functions

```sql
SUM()
AVG()
MIN()
MAX()
COUNT()
```

### Navigation

```sql
LAG()
LEAD()
FIRST_VALUE()
LAST_VALUE()
```

---

# 6. `ROW_NUMBER()`

Assigns a unique sequential number.

```sql
SELECT
 name,
 department,
 salary,
 ROW_NUMBER() OVER (
 PARTITION BY department
 ORDER BY salary DESC
 ) AS rn
FROM employees;
```

Result:

| name | department | salary | rn |
| ----- | ---------- | -----: | -: |
| David | IT | 90000 | 1 |
| John | IT | 80000 | 2 |
| Mike | IT | 70000 | 3 |
| Jane | HR | 70000 | 1 |
| Sarah | HR | 60000 | 2 |

This is extremely useful when you need:

>**Top N records per group**

---

# 7. `RANK()` vs `DENSE_RANK()`

Suppose salaries are:

```text
90000
80000
80000
70000
```

### `RANK()`

```sql
RANK() OVER (ORDER BY salary DESC)
```

Result:

```text
90000 → 1
80000 → 2
80000 → 2
70000 → 4
```

There is a gap after the tie.

### `DENSE_RANK()`

```sql
DENSE_RANK() OVER (ORDER BY salary DESC)
```

Result:

```text
90000 → 1
80000 → 2
80000 → 2
70000 → 3
```

No gap.

### Easy interview rule

```text
ROW_NUMBER → always unique
RANK → ties + gaps
DENSE_RANK → ties + no gaps
```

---

# 8. Running total

Window functions are excellent for calculating running totals.

Suppose:

| date | amount |
| ----- | -----: |
| Jan 1 | 100 |
| Jan 2 | 200 |
| Jan 3 | 150 |
| Jan 4 | 300 |

Query:

```sql
SELECT
 date,
 amount,
 SUM(amount) OVER (
 ORDER BY date
 ) AS running_total
FROM transactions;
```

Result:

| date | amount | running_total |
| ----- | -----: | ------------: |
| Jan 1 | 100 | 100 |
| Jan 2 | 200 | 300 |
| Jan 3 | 150 | 450 |
| Jan 4 | 300 | 750 |

Conceptually:

```text
100
100 + 200
100 + 200 + 150
100 + 200 + 150 + 300
```

---

# 9. `LAG()` — previous row

`LAG()` allows you to access the previous row.

Example:

```sql
SELECT
 date,
 amount,
 LAG(amount) OVER (
 ORDER BY date
 ) AS previous_amount
FROM transactions;
```

Result:

| date | amount | previous_amount |
| ----- | -----: | --------------: |
| Jan 1 | 100 | NULL |
| Jan 2 | 200 | 100 |
| Jan 3 | 150 | 200 |
| Jan 4 | 300 | 150 |

This is very useful for calculating differences.

```sql
SELECT
 date,
 amount,
 amount - LAG(amount) OVER (
 ORDER BY date
 ) AS difference
FROM transactions;
```

Result:

| date | amount | difference |
| ----- | -----: | ---------: |
| Jan 1 | 100 | NULL |
| Jan 2 | 200 | 100 |
| Jan 3 | 150 | -50 |
| Jan 4 | 300 | 150 |

---

# 10. `LEAD()` — next row

`LEAD()` is the opposite of `LAG()`.

```sql
SELECT
 date,
 amount,
 LEAD(amount) OVER (
 ORDER BY date
 ) AS next_amount
FROM transactions;
```

Result:

| date | amount | next_amount |
| ----- | -----: | ----------: |
| Jan 1 | 100 | 200 |
| Jan 2 | 200 | 150 |
| Jan 3 | 150 | 300 |
| Jan 4 | 300 | NULL |

Think:

```text
LAG() → look backward
LEAD() → look forward
```

---

# 11. Very important interview problem: second-highest salary

Suppose you need the **second-highest salary in each department**.

Use:

```sql
SELECT *
FROM (
 SELECT
 e.*,
 DENSE_RANK() OVER (
 PARTITION BY department
 ORDER BY salary DESC
 ) AS rnk
 FROM employees e
) x
WHERE rnk = 2;
```

This is a very common real-world and interview use case.

---

# 12. Window Function vs GROUP BY

This is one of the most important concepts.

### `GROUP BY`

```sql
SELECT
 department,
 AVG(salary)
FROM employees
GROUP BY department;
```

Output:

```text
IT → 80000
HR → 65000
```

Rows are **collapsed**.

### Window function

```sql
SELECT
 name,
 department,
 salary,
 AVG(salary) OVER (
 PARTITION BY department
 ) AS avg_salary
FROM employees;
```

Output:

```text
John IT 80000 80000
David IT 90000 80000
Mike IT 70000 80000
```

Rows are **not collapsed**.

### Remember this

```text
GROUP BY
 ↓
Multiple rows → ONE row per group

WINDOW FUNCTION
 ↓
Multiple rows → Multiple rows
 +
 calculated value
```

---

# 13. A real production example

Imagine an e-commerce application with:

```text
orders
--------------------------------
order_id
customer_id
order_date
amount
```

You want to find each customer's previous order amount.

```sql
SELECT
 customer_id,
 order_id,
 order_date,
 amount,
 LAG(amount) OVER (
 PARTITION BY customer_id
 ORDER BY order_date
 ) AS previous_order_amount
FROM orders;
```

Now you can answer questions like:

> Did the customer's latest order increase compared with their previous order?

This type of analysis is very common in production reporting and analytics.

---

# 14. One important rule

A window function generally **doesn't reduce the number of rows**.

For example:

```sql
SELECT
 employee_id,
 salary,
 AVG(salary) OVER () AS avg_salary
FROM employees;
```

If there are **1,000 employees**, you'll still get **1,000 rows**.

Each row simply gets an additional calculated value.

---

# 15. Easy way to remember

Think of a window as:

```text
 WINDOW
 ┌───────────────────┐
 │ John 80K │
 │ David 90K │
 │ Mike 70K │
 └───────────────────┘
 ↓
 Calculate
 ↓
 Average = 80K
```

The database looks at a **window of rows**, performs a calculation, and attaches the result to each applicable row.

---

## Interview definition

If an interviewer asks:

>**What is a window function?**

A strong answer is:

>**A window function performs a calculation across a set of related rows while preserving the individual rows in the result set. We define the window using `OVER()`, optionally using `PARTITION BY` to divide rows into groups and `ORDER BY` to define their order. Common window functions include `ROW_NUMBER`, `RANK`, `DENSE_RANK`, `SUM`, `AVG`, `LAG`, and `LEAD`.**

For your **Java/Spring Boot interviews**, the most important window-function problems to practice are **Top N per group, second-highest salary, duplicate detection, running totals, previous/next record comparison, latest record per customer, and pagination**.

```sql
SELECT salary
FROM (
 SELECT salary,
 DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
 FROM employees
) x
WHERE rnk = 3;
```

---

# LEVEL 9 — EXISTS

## 43. Customers who placed at least one order

```sql
SELECT *
FROM customers c
WHERE EXISTS (
 SELECT 1
 FROM orders o
 WHERE o.customer_id = c.customer_id
);
```

---

# 44. Customers who never placed orders

```sql
SELECT *
FROM customers c
WHERE NOT EXISTS (
 SELECT 1
 FROM orders o
 WHERE o.customer_id = c.customer_id
);
```

---

# 45. EXISTS vs IN

Interview discussion:

```text
IN
EXISTS
JOIN
```

You should understand:

* Semantics
* `NULL` behavior
* Query optimizer behavior
* Correlated subqueries
* Index usage

Don't claim that `EXISTS` is always faster than `IN`; modern optimizers can transform them into similar execution strategies.

---

# LEVEL 10 — CASE

# 46. Categorize employees by salary

```sql
SELECT employee_name,
 salary,
 CASE
 WHEN salary >= 150000 THEN 'HIGH'
 WHEN salary >= 100000 THEN 'MEDIUM'
 ELSE 'LOW'
 END AS salary_category
FROM employees;
```

---

# 47. Conditional aggregation

Very important.

```sql
SELECT
 COUNT(*) AS total,
 SUM(CASE WHEN salary > 100000 THEN 1 ELSE 0 END) AS high_salary
FROM employees;
```

---

# LEVEL 11 — COALESCE / NULL Handling

## 48. Replace NULL

```sql
SELECT employee_name,
 COALESCE(salary, 0)
FROM employees;
```

---

# 49. Manager name or "No Manager"

`COALESCE` returns the first non-NULL argument. To show the manager's **name**, self-join and default the missing value:

```sql
SELECT e.employee_name,
 COALESCE(m.employee_name, 'No Manager') AS manager_name
FROM employees e
LEFT JOIN employees m
 ON m.employee_id = e.manager_id;
```

| Function | Meaning | Portable? |
| --- | --- | --- |
| `COALESCE(a, b, c)` | First non-NULL value | Yes (ANSI) |
| `NULLIF(a, b)` | NULL if `a = b`, else `a` | Yes |
| `IFNULL` / `ISNULL` / `NVL` | Two-argument NULL replacement | MySQL / SQL Server / Oracle only |

**Real-time use of `NULLIF`:** avoid divide-by-zero in a conversion-rate report.

```sql
SELECT campaign_id,
 orders_count * 100.0 / NULLIF(visits_count, 0) AS conversion_pct
FROM campaign_stats;
```

When `visits_count = 0`, the result is `NULL` instead of an error that fails the whole report.

---

# LEVEL 12 — String Functions

Know:

```text
UPPER
LOWER
LENGTH
SUBSTRING
TRIM
REPLACE
CONCAT
LEFT
RIGHT
```

Example:

```sql
SELECT UPPER(employee_name)
FROM employees;
```

---

# 50. Find names containing "John"

```sql
SELECT *
FROM employees
WHERE LOWER(employee_name) LIKE '%john%';
```

---

# LEVEL 13 — Date Functions

You should know:

```text
CURRENT_DATE
CURRENT_TIMESTAMP
DATE_TRUNC
EXTRACT
DATEDIFF
DATE_ADD
INTERVAL
```

Exact functions vary by database.

---

# 51. Employees hired in 2026

Portable-ish approach:

```sql
SELECT *
FROM employees
WHERE hire_date >= DATE '2026-01-01'
AND hire_date < DATE '2027-01-01';
```

This is generally preferable to applying a function to the indexed column.

---

# 52. Employees hired in last 30 days

Database-specific syntax varies.

For PostgreSQL:

```sql
SELECT *
FROM employees
WHERE hire_date >= CURRENT_DATE - INTERVAL '30 days';
```

---

# LEVEL 14 — Set Operations

Know:

```text
UNION
UNION ALL
INTERSECT
EXCEPT
```

---

# 53. UNION

```sql
SELECT city FROM employees
UNION
SELECT city FROM customers;
```

Removes duplicates.

---

# 54. UNION ALL

```sql
SELECT city FROM employees
UNION ALL
SELECT city FROM customers;
```

Keeps duplicates.

### Important

`UNION ALL` is generally faster because it doesn't need duplicate elimination.

---

# LEVEL 15 — CTE

## 55. Basic CTE

```sql
WITH high_salary AS (
 SELECT *
 FROM employees
 WHERE salary > 100000
)
SELECT *
FROM high_salary;
```

---

# 56. Multiple CTEs

```sql
WITH employee_count AS (
 SELECT department_id,
 COUNT(*) AS total
 FROM employees
 GROUP BY department_id
),
department_info AS (
 SELECT *
 FROM departments
)
SELECT d.department_name,
 e.total
FROM department_info d
JOIN employee_count e
 ON d.department_id = e.department_id;
```

---

# LEVEL 16 — Recursive CTE

Senior-level topic.

Useful for organizational hierarchies.

```sql
WITH RECURSIVE employee_tree AS (

 SELECT employee_id,
 employee_name,
 manager_id,
1 AS level
 FROM employees
 WHERE manager_id IS NULL

 UNION ALL

 SELECT e.employee_id,
 e.employee_name,
 e.manager_id,
 et.level + 1
 FROM employees e
 JOIN employee_tree et
 ON e.manager_id = et.employee_id
)
SELECT *
FROM employee_tree;
```

---

# LEVEL 17 — WINDOW FUNCTIONS

This is **extremely important for Senior interviews**.

Know:

```text
ROW_NUMBER()
RANK()
DENSE_RANK()
NTILE()
LAG()
LEAD()
FIRST_VALUE()
LAST_VALUE()
SUM() OVER()
AVG() OVER()
COUNT() OVER()
```

---

# 57. ROW_NUMBER

```sql
SELECT employee_name,
 salary,
 ROW_NUMBER() OVER (
 ORDER BY salary DESC
 ) AS row_num
FROM employees;
```

---

# 58. RANK

```sql
SELECT employee_name,
 salary,
 RANK() OVER (
 ORDER BY salary DESC
 ) AS rank
FROM employees;
```

---

# 59. DENSE_RANK

```sql
SELECT employee_name,
 salary,
 DENSE_RANK() OVER (
 ORDER BY salary DESC
 ) AS rank
FROM employees;
```

### Important difference

Suppose salaries:

```text
100
100
90
80
```

`RANK()`:

```text
1
1
3
4
```

`DENSE_RANK()`:

```text
1
1
2
3
```

---

# 60. Top 3 salaries per department

Extremely common.

```sql
SELECT *
FROM (
 SELECT e.*,
 DENSE_RANK() OVER (
 PARTITION BY department_id
 ORDER BY salary DESC
 ) AS rnk
 FROM employees e
) x
WHERE rnk <= 3;
```

---

# 61. Highest-paid employee in each department

```sql
SELECT *
FROM (
 SELECT e.*,
 ROW_NUMBER() OVER (
 PARTITION BY department_id
 ORDER BY salary DESC
 ) AS rn
 FROM employees e
) x
WHERE rn = 1;
```

---

# 62. Running total

```sql
SELECT order_date,
 amount,
 SUM(amount) OVER (
 ORDER BY order_date
 ) AS running_total
FROM orders;
```

---

# 63. Running total per customer

```sql
SELECT customer_id,
 order_date,
 amount,
 SUM(amount) OVER (
 PARTITION BY customer_id
 ORDER BY order_date
 ) AS running_total
FROM orders;
```

---

# 64. Previous order amount

```sql
SELECT customer_id,
 order_date,
 amount,
 LAG(amount) OVER (
 PARTITION BY customer_id
 ORDER BY order_date
 ) AS previous_amount
FROM orders;
```

---

# 65. Next order

```sql
SELECT customer_id,
 order_date,
 amount,
 LEAD(amount) OVER (
 PARTITION BY customer_id
 ORDER BY order_date
 ) AS next_amount
FROM orders;
```

---

# 66. Difference from previous order

```sql
SELECT customer_id,
 order_date,
 amount,
 amount -
 LAG(amount) OVER (
 PARTITION BY customer_id
 ORDER BY order_date
 ) AS difference
FROM orders;
```

---

# LEVEL 18 — Advanced SQL Interview Problems

## 67. Find duplicate employees

```sql
SELECT employee_name,
 COUNT(*)
FROM employees
GROUP BY employee_name
HAVING COUNT(*) > 1;
```

---

# 68. Find duplicate records

```sql
SELECT employee_name,
 department_id,
 salary,
 COUNT(*)
FROM employees
GROUP BY employee_name,
 department_id,
 salary
HAVING COUNT(*) > 1;
```

---

# 69. Delete duplicates but keep one

Common senior interview question.

```sql
WITH duplicates AS (
 SELECT employee_id,
 ROW_NUMBER() OVER (
 PARTITION BY employee_name,
 department_id,
 salary
 ORDER BY employee_id
 ) AS rn
 FROM employees
)
DELETE FROM employees
WHERE employee_id IN (
 SELECT employee_id
 FROM duplicates
 WHERE rn > 1
);
```

Exact syntax can differ by database.

---

# 70. Find employees with same salary

```sql
SELECT *
FROM employees
WHERE salary IN (
 SELECT salary
 FROM employees
 GROUP BY salary
 HAVING COUNT(*) > 1
);
```

---

# 71. Find employees whose salary is higher than department average

```sql
SELECT *
FROM employees e
WHERE salary > (
 SELECT AVG(e2.salary)
 FROM employees e2
 WHERE e2.department_id = e.department_id
);
```

---
# 72. Find departments with highest average salary

```sql
SELECT department_id,
 AVG(salary) AS avg_salary
FROM employees
GROUP BY department_id
ORDER BY avg_salary DESC
FETCH FIRST 1 ROW ONLY;
```

---

# 73. Find second highest salary in each department

```sql
SELECT *
FROM (
 SELECT e.*,
 DENSE_RANK() OVER (
 PARTITION BY department_id
 ORDER BY salary DESC
 ) AS rnk
 FROM employees e
) x
WHERE rnk = 2;
```

---

# 74. Find employees who joined before their manager

```sql
SELECT e.employee_name,
 e.hire_date,
 m.employee_name AS manager,
 m.hire_date AS manager_hire_date
FROM employees e
JOIN employees m
 ON e.manager_id = m.employee_id
WHERE e.hire_date < m.hire_date;
```

---

# LEVEL 19 — Pagination

## 75. OFFSET pagination

```sql
SELECT *
FROM employees
ORDER BY employee_id
LIMIT 20 OFFSET 40;
```

Means:

```text
Skip 40
Return next 20
```

### Senior question

What happens with very large OFFSET?

```text
OFFSET 1000000
```

The database may need to process/skip many rows.

---

# 76. Keyset / Cursor Pagination

Better for large datasets.

```sql
SELECT *
FROM employees
WHERE employee_id > 100000
ORDER BY employee_id
LIMIT 20;
```

This is often much more scalable for APIs.

---

# LEVEL 20 — INDEXES

This is critical for Senior Developers.

---

# 77. What is an index?

An index is a data structure that helps the database locate rows more efficiently.

```sql
CREATE INDEX idx_employee_department
ON employees(department_id);
```

---

# 78. Composite index

```sql
CREATE INDEX idx_employee_dept_salary
ON employees(department_id, salary);
```

---

# 79. Composite index column order

Suppose:

```sql
CREATE INDEX idx_emp
ON employees(department_id, salary);
```

Understand the **leftmost-prefix principle**.

This can efficiently support predicates involving:

```text
department_id
department_id + salary
```

But a query only on:

```text
salary
```

may not be able to use that index efficiently, depending on the database and optimizer.

---

# 80. Covering index

An index containing all columns needed by a query can sometimes allow the database to answer the query directly from the index.

Example:

```sql
CREATE INDEX idx_emp_cover
ON employees(department_id, salary, employee_name);
```

---

# 81. Unique index

```sql
CREATE UNIQUE INDEX idx_employee_email
ON employees(email);
```

Prevents duplicate values.

---

# 82. When should you NOT create indexes?

Important senior question.

Indexes have costs:

```text
More disk space
+
Slower INSERT
+
Slower UPDATE
+
Slower DELETE
+
Maintenance overhead
```

Avoid blindly indexing every column.

### Easy explanation: the book index analogy

```text
Without an index: read every page of a 1,000-page book to find "HikariCP" -> full table scan
With an index: open the index at the back, jump to pages 212 and 540 -> index seek
```

Most relational indexes are **B+ trees**: sorted, balanced trees where finding one key in 100 million rows needs only about 3-4 page reads.

```text
 [ 500 | 1000 ]
 / | \
 [100|300] [700|900] [1200|1500]
 ... ... ...
 leaf pages: sorted keys -> row locations (linked for range scans)
```

Because leaves are sorted and linked, B+ trees are good at `=`, `<`, `>`, `BETWEEN`, `ORDER BY` and prefix `LIKE 'abc%'`, but not at `LIKE '%abc'`.

### Index types quick reference

| Type | Good for | Example |
| --- | --- | --- |
| B-tree / B+ tree | Equality, ranges, sorting | `customer_id`, `order_date` |
| Hash | Equality only | Session token lookup (engine-specific) |
| Clustered | Table rows physically ordered by key | SQL Server / MySQL InnoDB primary key |
| Composite | Multi-column filters | `(customer_id, status, order_date)` |
| Covering / INCLUDE | Query answered from index alone | `INCLUDE (amount)` |
| Partial / filtered | Subset of rows | `WHERE status = 'PENDING'` |
| Expression / function | Computed values | `LOWER(email)` |
| Bitmap | Low-cardinality analytics | Data warehouse flags (Oracle) |
| GIN / full-text | JSON, arrays, text search | PostgreSQL `jsonb` |

### Real-time example: a partial index for a job queue

A notification table has 50 million rows, but the batch only ever reads the ~2,000 rows still `PENDING`:

```sql
-- PostgreSQL syntax; SQL Server uses a filtered index with the same idea
CREATE INDEX idx_notification_pending
 ON notifications (scheduled_at)
 WHERE status = 'PENDING';
```

```text
Full index on status: 50M entries, updated on every status change
Partial index: ~2,000 entries, tiny, stays in memory
Batch query: WHERE status = 'PENDING' AND scheduled_at <= now() -> milliseconds
```

### Real-time example: composite index order matters

```sql
-- Query used by the "My Orders" page
SELECT order_id, order_date, amount
FROM orders
WHERE customer_id = ?
ORDER BY order_date DESC
FETCH FIRST 20 ROWS ONLY;
```

| Index | Result |
| --- | --- |
| `(order_date, customer_id)` | Scans many dates, filters customers -> slow |
| `(customer_id, order_date DESC)` | Jumps to one customer, rows already sorted, stops after 20 -> fast |

Rule of thumb: **equality columns first, then range/sort columns.**

---

# LEVEL 21 — Query Optimization

## 83. EXPLAIN

```sql
EXPLAIN
SELECT *
FROM employees
WHERE department_id = 10;
```

---

# 84. EXPLAIN ANALYZE

Database-specific.

Example PostgreSQL:

```sql
EXPLAIN ANALYZE
SELECT *
FROM employees
WHERE department_id = 10;
```

You should understand:

```text
Sequential Scan
Index Scan
Index Only Scan
Nested Loop
Hash Join
Merge Join
Sort
Aggregate
Estimated Rows
Actual Rows
Cost
Execution Time
```

---

# 85. Why is my query slow?

Interviewers may expect you to investigate:

```text
Missing index
Wrong index
Poor join condition
Large table scan
Bad cardinality estimates
Functions on indexed columns
Implicit conversions
Large result set
Sorting
Grouping
Poor pagination
Lock contention
Network transfer
Outdated statistics
```

---

# 86. Function on indexed column

Potentially problematic:

```sql
WHERE LOWER(employee_name) = 'john';
```

Depending on DB/index design, this can prevent use of a normal index on `employee_name`.

Possible solution:

* Functional/expression index
* Normalized search column
* Appropriate database-specific indexing

---

# 87. Avoid SELECT *

Instead of:

```sql
SELECT *
FROM employees;
```

prefer:

```sql
SELECT employee_id,
 employee_name,
 salary
FROM employees;
```

Why?

```text
Less data
Less network traffic
Potentially better covering-index opportunities
Clearer contract
```

---

# LEVEL 22 — Transactions

Senior developers absolutely need this.

---

# 88. What is a transaction?

A transaction is a logical unit of work.

Example:

```text
Transfer ₹10,000

Account A
 ↓
Debit ₹10,000

Account B
 ↓
Credit ₹10,000
```

Both should succeed or fail together.

---

# 89. ACID

Know this perfectly.

```text
A → Atomicity
C → Consistency
I → Isolation
D → Durability
```

| Property | Simple meaning | Real-time example (money transfer) | What provides it |
| --- | --- | --- | --- |
| Atomicity | All or nothing | Debit and credit both happen, or neither | Undo/redo log, rollback |
| Consistency | Rules always hold | Balance never negative; foreign keys valid | Constraints, triggers, application rules |
| Isolation | Concurrent transactions do not corrupt each other | Two transfers from the same account do not overspend | Locks, MVCC, isolation levels |
| Durability | Committed data survives crashes | Power cut after COMMIT; money is still moved | Write-ahead log flushed to disk, replicas |

**Interview tip:** "C" in ACID is *application-level correctness enforced by the database*, which is different from the "C" (linearizable reads) in the CAP theorem discussed in Part B.

---

# 90. COMMIT

```sql
BEGIN;

UPDATE accounts
SET balance = balance - 10000
WHERE account_id = 1;

COMMIT;
```

---

# 91. ROLLBACK

```sql
BEGIN;

UPDATE accounts
SET balance = balance - 10000
WHERE account_id = 1;

ROLLBACK;
```

---

# LEVEL 23 — Isolation Levels

Know:

```text
READ UNCOMMITTED
READ COMMITTED
REPEATABLE READ
SERIALIZABLE
```

And database-specific behavior such as:

```text
MVCC
Snapshot isolation
```

### Which anomalies each level prevents (ANSI definition)

| Isolation level | Dirty read | Non-repeatable read | Phantom read | Typical cost |
| --- | --- | --- | --- | --- |
| READ UNCOMMITTED | Possible | Possible | Possible | Lowest |
| READ COMMITTED | Prevented | Possible | Possible | Low (common default) |
| REPEATABLE READ | Prevented | Prevented | Possible (ANSI) | Medium |
| SERIALIZABLE | Prevented | Prevented | Prevented | Highest; more blocking or retries |

### Defaults you should know

| Database | Default | Note |
| --- | --- | --- |
| PostgreSQL | READ COMMITTED | MVCC; REPEATABLE READ is snapshot isolation and also prevents phantoms |
| Oracle | READ COMMITTED | MVCC; readers never block writers |
| SQL Server | READ COMMITTED | Lock-based unless `READ_COMMITTED_SNAPSHOT` is on (Azure SQL enables it) |
| MySQL InnoDB | REPEATABLE READ | Gap/next-key locks reduce phantoms |

### Real-time example: the lost-update anomaly (not fixed by READ COMMITTED)

```text
10:00:00 T1 reads stock = 1
10:00:00 T2 reads stock = 1
10:00:01 T1 sets stock = 0, sells item, COMMIT
10:00:01 T2 sets stock = 0, sells item, COMMIT -> one item sold twice
```

Fixes, from simplest to strongest:

```sql
-- 1. Atomic conditional update (best for counters)
UPDATE inventory SET stock = stock - 1
WHERE product_id = ? AND stock >= 1; -- check affected rows = 1

-- 2. Pessimistic lock
SELECT stock FROM inventory WHERE product_id = ? FOR UPDATE;

-- 3. Optimistic lock with a version column (JPA @Version)
UPDATE inventory SET stock = ?, version = version + 1
WHERE product_id = ? AND version = ?;
```

---

# 92. Dirty Read

Transaction A:

```text
UPDATE salary = 200000
```

Transaction B reads:

```text
200000
```

Transaction A rolls back.

Transaction B read data that was never committed.

That's a:

> Dirty Read

---

# 93. Non-repeatable Read

Transaction A:

```text
SELECT salary → 100000
```

Transaction B:

```text
UPDATE salary → 120000
COMMIT
```

Transaction A reads again:

```text
120000
```

Same query, different committed value.

---

# 94. Phantom Read

Transaction A:

```sql
SELECT *
FROM employees
WHERE salary > 100000;
```

Transaction B inserts another employee satisfying the condition.

Transaction A executes the query again and sees an additional row.

That's a:

> Phantom Read

---

# LEVEL 24 — Locks

Know:

```text
Shared Lock
Exclusive Lock
Row Lock
Table Lock
Intent Lock
```

Database implementations differ.

---

# 95. SELECT FOR UPDATE

Common example:

```sql
SELECT *
FROM accounts
WHERE account_id = 100
FOR UPDATE;
```

Typically locks the selected row(s) so another transaction cannot modify them in conflicting ways until the transaction completes.

---

# 96. Deadlock

Example:

```text
Transaction A locks Row 1
Transaction B locks Row 2

A waits for Row 2
B waits for Row 1

 ↓

 DEADLOCK
```

Senior interview:

> How do you prevent deadlocks?

Possible approaches:

```text
Consistent lock ordering
Short transactions
Appropriate indexes
Avoid unnecessary locks
Retry deadlock victims
Reduce transaction scope
```

### Real-time example: two transfers in opposite directions

```text
T1: transfer A -> B locks A, then waits for B
T2: transfer B -> A locks B, then waits for A -> deadlock, DB kills one transaction
```

**Fix 1 — consistent lock ordering:** always lock the lower account ID first.

```java
@Transactional
public void transfer(long from, long to, BigDecimal amount) {
long first = Math.min(from, to);
long second = Math.max(from, to);
Account a = accounts.findByIdForUpdate(first);
Account b = accounts.findByIdForUpdate(second);
 // debit/credit using a and b ...
}
```

**Fix 2 — bounded retry of the deadlock victim** (Spring translates the vendor error to `DeadlockLoserDataAccessException` / `CannotAcquireLockException`):

```java
@Retryable(retryFor = {DeadlockLoserDataAccessException.class, CannotAcquireLockException.class},
 maxAttempts = 3, backoff = @Backoff(delay = 50, multiplier = 2, random = true))
public void transferWithRetry(long from, long to, BigDecimal amount) {
 transferService.transfer(from, to, amount); // separate bean so @Transactional starts a new transaction per attempt
}
```

**Missing index cause:** an `UPDATE ... WHERE customer_email = ?` without an index can lock many rows while scanning, which greatly increases deadlock probability. Indexing the predicate shrinks the lock footprint.

---

# LEVEL 25 — Constraints

Know:

```text
PRIMARY KEY
FOREIGN KEY
UNIQUE
NOT NULL
CHECK
DEFAULT
```

---

# 97. Primary Key

```sql
CREATE TABLE employees (
 employee_id BIGINT PRIMARY KEY,
 employee_name VARCHAR(100)
);
```

---

# 98. Foreign Key

```sql
CREATE TABLE employees (
 employee_id BIGINT PRIMARY KEY,
 department_id BIGINT,
 FOREIGN KEY (department_id)
 REFERENCES departments(department_id)
);
```

---

# 99. UNIQUE

```sql
email VARCHAR(255) UNIQUE
```

---

# 100. CHECK

```sql
salary DECIMAL(12,2)
CHECK (salary >= 0)
```

---

# LEVEL 26 — Normalization

Know:

```text
1NF
2NF
3NF
BCNF
4NF
5NF
```

Interviewers commonly ask:

> Why normalize?

Benefits:

```text
Reduce redundancy
Prevent update anomalies
Improve data integrity
```

But also understand:

> When would you denormalize?

For:

```text
Read-heavy systems
Reporting
Analytics
Performance
Reducing expensive joins
```

---

# LEVEL 27 — Views

## 101. Create View

```sql
CREATE VIEW employee_details AS
SELECT e.employee_id,
 e.employee_name,
 d.department_name
FROM employees e
JOIN departments d
ON e.department_id = d.department_id;
```

Then:

```sql
SELECT *
FROM employee_details;
```

---

# 102. Materialized View

A materialized view stores the query result physically.

Useful for expensive read/reporting queries.

But you need to understand:

```text
Refresh
Staleness
Storage
Maintenance
```

Support and refresh behavior vary by database.

---

# LEVEL 28 — Stored Procedures

Know:

```text
Stored Procedure
Function
Trigger
Cursor
```

Example concept:

```sql
CREATE PROCEDURE ...
```

Exact syntax differs significantly across Oracle, PostgreSQL, SQL Server, and MySQL.

Interview questions:

* Procedure vs function?
* When should business logic be in DB?
* Advantages?
* Disadvantages?
* Version control?
* Testing?
* Deployment?

---

# LEVEL 29 — Triggers

Example concept:

```sql
CREATE TRIGGER ...
```

Common uses:

```text
Audit
History
Validation
Automatic metadata
```

But excessive triggers can make systems difficult to understand and debug.

---

# LEVEL 30 — Sequences / Identity

## 103. Sequence

Common in Oracle/PostgreSQL:

```sql
CREATE SEQUENCE employee_seq
START WITH 1
INCREMENT BY 1;
```

Then database-specific usage might be:

```sql
SELECT nextval('employee_seq');
```

---

# LEVEL 31 — Advanced SQL Problems

These are very common for Senior candidates.

---

## 104. Find nth highest salary

```sql
SELECT salary
FROM (
 SELECT salary,
 DENSE_RANK() OVER (
 ORDER BY salary DESC
 ) AS rnk
 FROM employees
) x
WHERE rnk = 5;
```

---

## 105. Top 3 employees per department

```sql
SELECT *
FROM (
 SELECT e.*,
 ROW_NUMBER() OVER (
 PARTITION BY department_id
 ORDER BY salary DESC
 ) rn
 FROM employees e
) x
WHERE rn <= 3;
```

---

## 106. Employees with no projects

```sql
SELECT e.*
FROM employees e
LEFT JOIN employee_projects ep
ON e.employee_id = ep.employee_id
WHERE ep.employee_id IS NULL;
```

---

## 107. Departments with no employees

```sql
SELECT d.*
FROM departments d
LEFT JOIN employees e
ON d.department_id = e.department_id
WHERE e.employee_id IS NULL;
```

---

## 108. Customers with more than 5 orders

```sql
SELECT customer_id,
 COUNT(*) AS order_count
FROM orders
GROUP BY customer_id
HAVING COUNT(*) > 5;
```

---

## 109. Customer with highest total order value

```sql
SELECT customer_id,
 SUM(amount) AS total_amount
FROM orders
GROUP BY customer_id
ORDER BY total_amount DESC
FETCH FIRST 1 ROW ONLY;
```

---

## 110. Second-largest order

```sql
SELECT *
FROM (
 SELECT o.*,
 DENSE_RANK() OVER (
 ORDER BY amount DESC
 ) rnk
 FROM orders o
) x
WHERE rnk = 2;
```

---

# LEVEL 32 — Gaps and Islands

Very important advanced SQL concept.

Suppose employee attendance:

```text
Jan 1
Jan 2
Jan 3

Jan 7
Jan 8

Jan 15
```

You may be asked:

> Find consecutive date ranges.

This requires techniques involving:

```text
ROW_NUMBER()
LAG()
DATE arithmetic
```

### The trick explained simply

For consecutive dates, `date - row_number` stays constant inside each streak. That constant becomes the group key.

```text
login_date | rn | login_date - rn days (group key)
Jan 1 | 1 | Dec 31
Jan 2 | 2 | Dec 31 <- same group
Jan 3 | 3 | Dec 31
Jan 7 | 4 | Jan 3 <- new group
Jan 8 | 5 | Jan 3
Jan 15 | 6 | Jan 9 <- new group
```

### Solution (PostgreSQL syntax)

```sql
WITH numbered AS (
 SELECT employee_id,
 attendance_date,
 attendance_date
 - (ROW_NUMBER() OVER (PARTITION BY employee_id ORDER BY attendance_date))::int
 AS grp
 FROM (SELECT DISTINCT employee_id, attendance_date FROM attendance) d
)
SELECT employee_id,
 MIN(attendance_date) AS streak_start,
 MAX(attendance_date) AS streak_end,
 COUNT(*) AS days
FROM numbered
GROUP BY employee_id, grp
ORDER BY employee_id, streak_start;
Result: `Jan 1-Jan 3 (3 days)`, `Jan 7-Jan 8 (2 days)`, `Jan 15 (1 day)`.

`DISTINCT` matters: two check-ins on the same day would otherwise break the arithmetic.

**Real-time uses:** login streaks for gamification, consecutive missed premium payments (policy lapse rules), server downtime windows from heartbeat rows, and finding gaps in invoice numbers for auditors.

---

# LEVEL 33 — Consecutive Records

Example:

> Find customers who placed orders on 3 consecutive days.

Typical approach:

```sql
LAG()
LEAD()
```

or gaps-and-islands techniques.

This is a very common advanced SQL interview category.

### Solution with `LAG`: customers who ordered on 3 consecutive days

```sql
WITH daily AS (
 SELECT DISTINCT customer_id, CAST(order_date AS DATE) AS d
 FROM orders
),
with_prev AS (
 SELECT customer_id,
 d,
 LAG(d, 1) OVER (PARTITION BY customer_id ORDER BY d) AS prev1,
 LAG(d, 2) OVER (PARTITION BY customer_id ORDER BY d) AS prev2
 FROM daily
)
SELECT DISTINCT customer_id
FROM with_prev
WHERE prev1 = d - INTERVAL '1 day'
 AND prev2 = d - INTERVAL '2 day';
```

```text
Customer 7: Mar 3, Mar 4, Mar 5 -> on Mar 5, prev1 = Mar 4 and prev2 = Mar 3 -> match
Customer 9: Mar 3, Mar 5, Mar 6 -> never three in a row -> no match
```

For "N consecutive days" with large N, use the gaps-and-islands pattern above and filter `HAVING COUNT(*) >= N`.

---

# LEVEL 34 — Running / Moving Aggregates

### 111. Running average

```sql
SELECT order_date,
 amount,
 AVG(amount) OVER (
 ORDER BY order_date
 ROWS BETWEEN UNBOUNDED PRECEDING
 AND CURRENT ROW
 ) AS running_average
FROM orders;
```

---

# 112. Moving 7-day average

Conceptually:

```sql
AVG(amount) OVER (
 ORDER BY order_date
 ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
)
```

Be careful: `ROWS` counts rows, not necessarily calendar days. For true 7-calendar-day windows, the exact syntax depends on the database and data model.

---

# LEVEL 35 — Pivoting

Interviewers may ask:

> Convert rows into columns.

Some databases provide:

```text
PIVOT
```

Alternative:

```sql
SELECT
 SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) AS success,
 SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) AS failed
FROM orders;
```

### Real-time example: daily email-batch report, one row per day

```sql
SELECT CAST(sent_at AS DATE) AS batch_day,
 COUNT(*) AS total,
 SUM(CASE WHEN status = 'SENT' THEN 1 ELSE 0 END) AS sent,
 SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) AS failed,
 SUM(CASE WHEN status = 'SKIPPED' THEN 1 ELSE 0 END) AS skipped,
 ROUND(100.0 * SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END)
 / NULLIF(COUNT(*), 0), 2) AS failure_pct
FROM notification_audit
WHERE sent_at >= CURRENT_DATE - INTERVAL '7 days'
GROUP BY CAST(sent_at AS DATE)
ORDER BY batch_day;
```

```text
batch_day | total | sent | failed | skipped | failure_pct
2026-09-20 | 3120 | 3101 | 12 | 7 | 0.38
2026-09-21 | 2980 | 2700 | 270 | 10 | 9.06 <- operations investigates this day
```

This "conditional aggregation" pattern works on every database, unlike vendor-specific `PIVOT`. PostgreSQL also supports the shorter `COUNT(*) FILTER (WHERE status = 'FAILED')`.

---

# LEVEL 36 — JSON SQL

Modern applications increasingly store/process JSON.

Depending on database:

```text
PostgreSQL JSON / JSONB
MySQL JSON
Oracle JSON
SQL Server JSON
```

Interview questions:

* How do you query JSON?
* JSON vs normalized relational columns?
* JSON indexing?
* When would you use JSON?

### Real-time example: flexible product attributes (PostgreSQL `jsonb`)

A laptop has `ram` and `cpu`; a shirt has `size` and `colour`. Creating a column for every attribute is impractical, so store them in `jsonb`:

```sql
CREATE TABLE products (
 product_id BIGINT PRIMARY KEY,
 name TEXT NOT NULL,
 price NUMERIC(12,2) NOT NULL, -- queried/validated fields stay relational
 attributes JSONB NOT NULL DEFAULT '{}'
);

INSERT INTO products VALUES
 (1, 'Laptop X', 999.00, '{"ram": "16GB", "cpu": "i7", "tags": ["work", "travel"]}'),
 (2, 'T-Shirt', 19.99, '{"size": "M", "colour": "blue"}');

-- Read a field
SELECT name, attributes ->> 'ram' AS ram FROM products WHERE attributes ? 'ram';

-- Containment search, accelerated by a GIN index
CREATE INDEX idx_products_attr ON products USING GIN (attributes);
SELECT name FROM products WHERE attributes @> '{"colour": "blue"}';
```

| Store as a column | Store in JSON |
| --- | --- |
| Used in joins, filters, constraints, reporting | Rarely filtered, varies by type, schema evolves often |
| Needs `NOT NULL`, `CHECK`, foreign keys | Opaque payloads (partner API response, audit snapshot) |

Senior answer: *"I keep core, frequently queried fields relational and put genuinely variable attributes in JSON with a targeted index. If most of the data is JSON and access is by key, that is a signal to evaluate a document database — see Part B."*

---

# LEVEL 37 — Database Partitioning

Senior/architect-level topic.

Know:

```text
Range Partitioning
List Partitioning
Hash Partitioning
Composite Partitioning
```

Example concept:

```text
orders_2024
orders_2025
orders_2026
```

Benefits:

```text
Partition pruning
Manageability
Large-table maintenance
Archival
Potentially better query performance
```

---

# LEVEL 38 — Sharding

Very important for distributed systems interviews.

Difference:

```text
Partitioning
vs
Sharding
```

### Partitioning

Usually within one database system.

### Sharding

Data distributed across multiple database instances/nodes.

Example:

```text
Customer ID 1-1M
 ↓
Shard 1

Customer ID 1M-2M
 ↓
Shard 2
```

Senior questions:

* How do you choose shard key?
* What is a hot shard?
* How do you handle cross-shard queries?
* Cross-shard transactions?
* Rebalancing?
* Consistent hashing?
* Global indexes?

---

# LEVEL 39 — Query Execution Order

One of the most important SQL concepts.

For a typical `SELECT`:

```text
FROM
JOIN
WHERE
GROUP BY
HAVING
SELECT
DISTINCT
ORDER BY
LIMIT / FETCH
```

Remember:

```sql
SELECT department_id,
 COUNT(*)
FROM employees
WHERE salary > 50000
GROUP BY department_id
HAVING COUNT(*) > 5
ORDER BY department_id;
```

Logical processing is approximately:

```text
FROM
 ↓
JOIN
 ↓
WHERE
 ↓
GROUP BY
 ↓
HAVING
 ↓
SELECT
 ↓
DISTINCT
 ↓
ORDER BY
 ↓
LIMIT
```

This explains questions like:

> Why can't I use a SELECT alias in WHERE?

Because logically `WHERE` is evaluated before `SELECT`.

---

# LEVEL 40 — NULL Concepts

Senior interviews often test this.

Know:

```text
NULL ≠ 0
NULL ≠ ''
NULL ≠ FALSE
```

SQL uses three-valued logic:

```text
TRUE
FALSE
UNKNOWN
```

Understand:

```sql
NULL = NULL
```

does not evaluate to `TRUE`.

Use:

```sql
IS NULL
```

---

# LEVEL 41 — SQL Injection

Extremely important for Java developers.

Bad:

```java
String sql =
"SELECT * FROM users WHERE username = '" + username + "'";
```

Potential SQL injection.

Use parameterized queries:

```java
PreparedStatement ps =
 connection.prepareStatement(
"SELECT * FROM users WHERE username = ?"
 );

ps.setString(1, username);
```

With Spring Data/JPA, use parameter binding rather than string concatenation.

---

# LEVEL 42 — JDBC / Spring Boot SQL Questions

Since you're a Senior Java Developer, expect these too.

### Questions

* JDBC vs JPA?
* Hibernate vs JPA?
* `JdbcTemplate`?
* Named parameters?
* PreparedStatement?
* Connection pooling?
* HikariCP?
* Transaction management?
* `@Transactional`?
* Propagation?
* Isolation?
* Lazy vs eager loading?
* N+1 query?
* Entity relationships?
* Optimistic locking?
* Pessimistic locking?

---

# LEVEL 43 — N+1 Query Problem

Suppose:

```java
List<Customer> customers = customerRepository.findAll();
```

Then accessing:

```java
customer.getOrders()
```

may generate:

```text
1 query → customers

N queries → orders for each customer
```

Total:

```text
1 + N queries
```

Solutions may include:

```text
JOIN FETCH
EntityGraph
Batch fetching
DTO projections
Explicit optimized queries
```

---

# LEVEL 44 — Optimistic Locking

Common JPA approach:

```java
@Version
private Long version;
```

Database row:

```text
id | salary | version
1 | 100000 | 5
```

Two transactions read version 5.

One updates:

```text
version = 6
```

The other tries to update using version 5 and fails because the version no longer matches.

Useful when conflicts are relatively uncommon.

---

# LEVEL 45 — Pessimistic Locking

Conceptually:

```sql
SELECT *
FROM accounts
WHERE account_id = 100
FOR UPDATE;
```

Use when you need database-level locking during a transaction and understand the concurrency consequences.

---

# LEVEL 46 — Transactions in Spring

Typical:

```java
@Transactional
public void transferMoney(...) {
 debit();
 credit();
}
```

Interview questions:

* What happens when exception occurs?
* Checked vs unchecked exceptions?
* Propagation?
* Isolation?
* Rollback?
* Nested transactions?
* Self-invocation?
* Multiple data sources?
* Distributed transactions?

---

# LEVEL 47 — Distributed Transactions

For microservices:

```text
Service A
 ↓
Database A

Service B
 ↓
Database B
```

A normal database transaction cannot automatically span independent databases/services.

Know:

```text
2PC
Saga
Choreography
Orchestration
Outbox Pattern
CDC
Idempotency
Compensation
```

---

# LEVEL 48 — OUTBOX Pattern

Very important for modern microservices.

Instead of:

```text
DB transaction
 +
Kafka publish
```

which can fail halfway:

```text
DB SUCCESS
Kafka FAILURE
```

Use:

```text
Application
 ↓
DB Transaction
 ┌───────────────┐
 │ Business Data │
 │ Outbox Event │
 └───────────────┘
 ↓
 CDC / Publisher
 ↓
 Kafka
```

This is highly relevant to Senior Spring Boot + Kafka interviews.

---

# LEVEL 49 — Advanced Performance Questions

You should be prepared for:

### Q: Why is an index not being used?

Possible reasons:

```text
Low selectivity
Wrong column order
Function on column
Implicit type conversion
Stale statistics
Leading wildcard
Optimizer decision
Very small table
Expression mismatch
```

---

### Q: What is cardinality?

Number of distinct values / distribution characteristics of a column.

Example:

```text
gender:
M
F
```

Low cardinality.

```text
employee_id:
1
2
3
...
10 million
```

High cardinality.

---

# LEVEL 50 — SARGability

A very important senior concept.

Bad:

```sql
WHERE YEAR(hire_date) = 2026
```

Potentially prevents normal index range usage.

Better:

```sql
WHERE hire_date >= DATE '2026-01-01'
AND hire_date < DATE '2027-01-01';
```

The second form is generally more index-friendly.

---

# LEVEL 51 — JOIN Algorithms

Understand:

```text
Nested Loop Join
Hash Join
Merge Join
```

Interview question:

> Which join algorithm is best?

There is no universal answer.

It depends on:

```text
Data size
Indexes
Cardinality
Join condition
Memory
Sort order
Optimizer estimates
```

---

# LEVEL 52 — Database Statistics

Know:

```text
Statistics
Histograms
Cardinality estimates
Selectivity
Cost-based optimizer
```

The optimizer uses statistics to estimate the cost of possible execution plans.

---

# LEVEL 53 — Isolation vs Locking vs MVCC

Senior interviewers may ask:

> Does READ COMMITTED mean there are no locks?

No.

Locking behavior depends on the DB engine and operation.

Also understand:

```text
MVCC
Row versions
Snapshots
Readers vs writers
```

This is database-specific and should be answered for the particular database being discussed.

---

# LEVEL 54 — Common SQL Traps

### Trap 1

```sql
COUNT(*)
```

vs

```sql
COUNT(column)
```

`COUNT(column)` ignores `NULL`.

---

### Trap 2

```sql
WHERE column = NULL
```

Wrong.

Use:

```sql
WHERE column IS NULL
```

---

### Trap 3

```sql
NOT IN
```

with `NULL` can produce unexpected results.

Consider:

```sql
NOT EXISTS
```

when appropriate.

---

### Trap 4

LEFT JOIN accidentally converted to INNER JOIN

This:

```sql
SELECT *
FROM employees e
LEFT JOIN departments d
ON e.department_id = d.department_id
WHERE d.department_name = 'IT';
```

filters out NULL department rows.

If you intend to preserve unmatched employees, the condition may belong in the `ON` clause:

```sql
SELECT *
FROM employees e
LEFT JOIN departments d
ON e.department_id = d.department_id
AND d.department_name = 'IT';
```

---

# LEVEL 55 — SQL Design Questions

Senior interviews also ask conceptual questions.

You should be able to answer:

### Database Design

1. Primary key vs unique key?
2. Natural key vs surrogate key?
3. Composite primary key?
4. Foreign key?
5. Referential integrity?
6. Normalization?
7. Denormalization?
8. 1NF?
9. 2NF?
10. 3NF?
11. BCNF?
12. OLTP vs OLAP?
13. Star schema?
14. Snowflake schema?
15. Fact table?
16. Dimension table?

---

# LEVEL 56 — Database Architecture

Senior/Architect level:

```text
Read Replica
Primary Database
Replication
Failover
High Availability
Connection Pooling
Partitioning
Sharding
Caching
CDC
Backup
Recovery
Point-in-Time Recovery
```

Questions:

* How would you scale a database?
* How do you handle 10 million requests?
* Read-heavy system?
* Write-heavy system?
* Reporting queries affecting OLTP?
* Database failover?
* Replication lag?
* Read-after-write consistency?

---

# LEVEL 57 — OLTP vs OLAP

### OLTP

```text
Orders
Payments
Customers
Transactions
```

Characteristics:

```text
Many small transactions
Low latency
Highly concurrent
Normalized schema
```

### OLAP

```text
Analytics
Reports
Business intelligence
```

Characteristics:

```text
Large scans
Aggregations
Historical data
Analytical queries
```

---

# LEVEL 58 — Real Senior-Level SQL Scenarios

These are excellent interview questions.

### Scenario 1

> Your API takes 8 seconds because of a SQL query. How will you debug it?

Expected thought process:

```text
1. Capture exact SQL
2. Check execution plan
3. Check actual execution time
4. Check indexes
5. Check cardinality
6. Check joins
7. Check scans
8. Check sorting/grouping
9. Check locks
10. Check DB CPU/IO
11. Check returned rows
12. Optimize
13. Re-test with realistic data
```

---

### Scenario 2

> Query works quickly with 1,000 rows but takes 30 seconds with 50 million rows.

Think about:

```text
Indexes
Query plan
Cardinality
Partitioning
Pagination
Data distribution
Statistics
Join strategy
```

---

### Scenario 3

> Two users update the same account simultaneously.

Discuss:

```text
Lost update
Optimistic locking
Pessimistic locking
Isolation
Atomic UPDATE
Transactions
```

---

### Scenario 4

> Payment succeeded but order status wasn't updated.

Discuss:

```text
Transaction boundary
Distributed transaction
Saga
Outbox
Idempotency
Retry
Compensation
```

---

### Scenario 5

> Your database has 500 million orders.

Discuss:

```text
Partitioning
Indexes
Archival
Pagination
Read replicas
Query optimization
Data retention
Sharding if necessary
```

---

# LEVEL 59 — Extremely Advanced SQL Questions

For Senior/Lead/Architect interviews, prepare these:

### Query Processing

* How does SQL execute internally?
* Logical vs physical execution?
* Cost-based optimizer?
* Statistics?
* Cardinality estimation?
* Predicate pushdown?
* Projection pushdown?
* Join reordering?
* Query transformation?

### Indexing

* B-tree?
* B+ tree?
* Hash indexes?
* Bitmap indexes?
* Clustered index?
* Non-clustered index?
* Covering index?
* Composite index?
* Partial/filtered index?
* Function-based index?
* Index selectivity?
### Concurrency

* Dirty reads?
* Lost updates?
* Non-repeatable reads?
* Phantom reads?
* Deadlocks?
* MVCC?
* Snapshot isolation?
* Lock escalation?

### Distributed Database

* Replication?
* Leader/follower?
* Read replicas?
* Replication lag?
* Sharding?
* Consistent hashing?
* Distributed transactions?
* 2PC?
* Saga?
* CAP theorem?
* Eventual consistency?

---

# LEVEL 60 — SQL Coding Round Questions

You should practice these without looking at solutions.

## Beginner

1. Find all employees.
2. Find employees earning > 100K.
3. Find unique departments.
4. Find employees from Hyderabad.
5. Find employees whose names start with A.
6. Find employees with NULL manager.
7. Sort employees by salary.
8. Find maximum salary.
9. Find minimum salary.
10. Find average salary.
11. Count employees.
12. Calculate total salary.

---

## Intermediate

13. Find employees per department.
14. Find average salary per department.
15. Find departments having > 5 employees.
16. Find second highest salary.
17. Find third highest salary.
18. Find duplicate employees.
19. Find employees without departments.
20. Find departments without employees.
21. Find customers without orders.
22. Find employees earning above company average.
23. Find employees earning above department average.
24. Find employees earning more than their manager.
25. Find employees who joined before their manager.
26. Find customers with more than 5 orders.
27. Find highest order per customer.
28. Find latest order per customer.
29. Find employees working on multiple projects.
30. Find departments with highest average salary.

---

# Advanced Coding Questions

31. Top 3 salaries per department.

32. Second highest salary per department.

33. Remove duplicate records.

34. Find consecutive login dates.

35. Find customers with 3 consecutive orders.

36. Find gaps in order dates.

37. Find running total.

38. Find moving average.

39. Find previous transaction.

40. Find next transaction.

41. Calculate month-over-month growth.

42. Calculate year-over-year growth.

43. Find first order for every customer.

44. Find last order for every customer.

45. Find customers whose latest order is greater than previous order.

46. Find employees whose salary increased.

47. Find departments where every employee earns > 50K.

48. Find departments where at least one employee earns > 200K.

49. Find products never ordered.

50. Find products ordered by every customer.

---

# Extreme SQL Coding Questions

51. Gaps and islands.

52. Sessionization.

53. Recursive employee hierarchy.

54. Organizational tree.

55. Find hierarchy depth.

56. Find all subordinates of a manager.

57. Find all managers of an employee.

58. Running balance.

59. Detect overlapping date ranges.

60. Merge overlapping intervals.

61. Find missing IDs.

62. Find duplicate events.

63. Find latest record per group.

64. Find first and last event per group.

65. Find top N per group.

66. Median salary.

67. Percentiles.

68. Ranking with ties.

69. Moving window calculations.

70. Conditional aggregation.

71. Pivot rows to columns.

72. Unpivot columns to rows.

73. Recursive CTE.

74. Hierarchical aggregation.

75. Complex multi-level aggregation.

---

# Worked Solutions for Advanced and Extreme Coding Questions

The queries use ANSI SQL where possible; PostgreSQL syntax is used for date arithmetic. Practise writing each one before reading the answer.

## S1. Month-over-month revenue growth (Q41)

```sql
WITH monthly AS (
 SELECT DATE_TRUNC('month', order_date) AS month,
 SUM(amount) AS revenue
 FROM orders
 GROUP BY DATE_TRUNC('month', order_date)
)
SELECT month,
 revenue,
 LAG(revenue) OVER (ORDER BY month) AS prev_revenue,
 ROUND(100.0 * (revenue - LAG(revenue) OVER (ORDER BY month))
 / NULLIF(LAG(revenue) OVER (ORDER BY month), 0), 2) AS growth_pct
FROM monthly
ORDER BY month;
```

Year-over-year (Q42) is the same query with `LAG(revenue, 12)`.

## S2. First and latest order per customer (Q43, Q44, Q63)

```sql
SELECT *
FROM (
 SELECT o.*,
 ROW_NUMBER() OVER (PARTITION BY customer_id
 ORDER BY order_date DESC, order_id DESC) AS rn
 FROM orders o
) x
WHERE rn = 1; -- use ASC ordering for the first order
```

The `order_id` tie-breaker makes the result deterministic when two orders share a timestamp.

## S3. Customers whose latest order is bigger than the previous one (Q45)

```sql
WITH ranked AS (
 SELECT customer_id, amount,
 ROW_NUMBER() OVER (PARTITION BY customer_id ORDER BY order_date DESC, order_id DESC) AS rn,
 LEAD(amount) OVER (PARTITION BY customer_id ORDER BY order_date DESC, order_id DESC) AS prev_amount
 FROM orders
)
SELECT customer_id, amount AS latest, prev_amount
FROM ranked
WHERE rn = 1 AND amount > prev_amount;
```

## S4. Departments where **every** employee earns > 50K (Q47) vs **at least one** earns > 200K (Q48)

```sql
-- Every: the minimum passes the test
SELECT department_id FROM employees GROUP BY department_id HAVING MIN(salary) > 50000;

-- At least one: the maximum passes the test
SELECT department_id FROM employees GROUP BY department_id HAVING MAX(salary) > 200000;
```

## S5. Products ordered by every customer (Q50) — relational division

```sql
SELECT oi.product_id
FROM order_items oi
JOIN orders o ON o.order_id = oi.order_id
GROUP BY oi.product_id
HAVING COUNT(DISTINCT o.customer_id) = (SELECT COUNT(*) FROM customers);
```

## S6. Products never ordered (Q49)

```sql
SELECT p.*
FROM products p
WHERE NOT EXISTS (SELECT 1 FROM order_items oi WHERE oi.product_id = p.product_id);
```

## S7. Find missing IDs (Q61)

```sql
SELECT prev_id + 1 AS gap_start, order_id - 1 AS gap_end
FROM (
 SELECT order_id, LAG(order_id) OVER (ORDER BY order_id) AS prev_id
 FROM orders
) x
WHERE order_id - prev_id > 1;
```

Real-time use: auditors ask why invoice numbers 10045-10047 are missing.

## S8. Detect overlapping date ranges (Q59)

Two ranges overlap when **each starts before the other ends**:

```sql
SELECT a.booking_id, b.booking_id AS overlaps_with
FROM bookings a
JOIN bookings b
 ON a.room_id = b.room_id
 AND a.booking_id < b.booking_id
 AND a.start_date < b.end_date
 AND b.start_date < a.end_date;
```

Real-time use: stop a meeting room, hotel room or insurance coverage period from being double-booked.

## S9. Merge overlapping intervals (Q60)

```sql
WITH ordered AS (
 SELECT room_id, start_date, end_date,
 MAX(end_date) OVER (PARTITION BY room_id ORDER BY start_date
 ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING) AS max_prev_end
 FROM bookings
),
flagged AS (
 SELECT *,
 CASE WHEN max_prev_end IS NULL OR start_date > max_prev_end THEN 1 ELSE 0 END AS new_group
 FROM ordered
),
grouped AS (
 SELECT *, SUM(new_group) OVER (PARTITION BY room_id ORDER BY start_date) AS grp
 FROM flagged
)
SELECT room_id, MIN(start_date) AS merged_start, MAX(end_date) AS merged_end
FROM grouped
GROUP BY room_id, grp
ORDER BY room_id, merged_start;
```

## S10. Sessionization — 30 minutes of inactivity starts a new session (Q52)

```sql
WITH events AS (
 SELECT user_id, event_time,
 CASE WHEN event_time - LAG(event_time) OVER (PARTITION BY user_id ORDER BY event_time)
 > INTERVAL '30 minutes'
 OR LAG(event_time) OVER (PARTITION BY user_id ORDER BY event_time) IS NULL
 THEN 1 ELSE 0 END AS is_new_session
 FROM click_events
)
SELECT user_id,
 SUM(is_new_session) OVER (PARTITION BY user_id ORDER BY event_time) AS session_no,
 event_time
FROM events;
```

## S11. All subordinates of a manager and hierarchy depth (Q54-Q56)

```sql
WITH RECURSIVE reports AS (
 SELECT employee_id, employee_name, manager_id, 1 AS depth
 FROM employees
 WHERE manager_id = :managerId
 UNION ALL
 SELECT e.employee_id, e.employee_name, e.manager_id, r.depth + 1
 FROM employees e
 JOIN reports r ON e.manager_id = r.employee_id
 WHERE r.depth < 20 -- guard against cycles caused by bad data
)
SELECT * FROM reports ORDER BY depth, employee_name;
```

For "all managers of an employee" (Q57), start from the employee and join `e.employee_id = r.manager_id` upward.

## S12. Running balance (Q58)

```sql
SELECT account_id, txn_time, amount,
 SUM(CASE WHEN txn_type = 'CREDIT' THEN amount ELSE -amount END)
 OVER (PARTITION BY account_id ORDER BY txn_time, txn_id
 ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS balance
FROM account_transactions;
```

`ROWS` (not the default `RANGE`) prevents two transactions with the same timestamp from getting the same running balance.

## S13. Median and percentiles (Q66, Q67)

```sql
SELECT department_id,
 PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY salary) AS median_salary,
 PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY salary) AS p95_salary
FROM employees
GROUP BY department_id;
```

Supported in PostgreSQL, Oracle and SQL Server (as a window function). The same idea drives p95/p99 latency reports.

## S14. Unpivot columns into rows (Q72)

```sql
SELECT customer_id, 'Q1' AS quarter, q1_sales AS sales FROM quarterly_sales
UNION ALL
SELECT customer_id, 'Q2', q2_sales FROM quarterly_sales
UNION ALL
SELECT customer_id, 'Q3', q3_sales FROM quarterly_sales
UNION ALL
SELECT customer_id, 'Q4', q4_sales FROM quarterly_sales;
```

## S15. Duplicate events arriving from a retrying producer (Q62)

```sql
SELECT event_id, COUNT(*) AS copies
FROM inbound_events
GROUP BY event_id
HAVING COUNT(*) > 1;
```

Permanent fix: a unique constraint on `event_id` plus an idempotent consumer, so duplicates are rejected at write time instead of cleaned up later.

---

# The Most Important Topics for Your Senior Java Interview

Given your **Java + Spring Boot + Microservices** background, I would prioritize these rather than trying to memorize hundreds of SQL queries equally.

### Tier 1 — Must Know

```text
SELECT
WHERE
GROUP BY
HAVING
ORDER BY
JOINs
Subqueries
EXISTS
CASE
NULL
Aggregate functions
INSERT / UPDATE / DELETE
```

### Tier 2 — Very Important

```text
CTE
Window Functions
ROW_NUMBER
RANK
DENSE_RANK
LAG
LEAD
Top N per group
Duplicate removal
Pagination
Indexes
Composite indexes
EXPLAIN
Query optimization
Transactions
ACID
Isolation levels
Locks
Deadlocks
```

### Tier 3 — Senior/Lead

```text
Execution plans
Cardinality
SARGability
MVCC
Optimistic locking
Pessimistic locking
Partitioning
Replication
Read replicas
Sharding
Normalization
Denormalization
Materialized views
Stored procedures
Triggers
```

### Tier 4 — Architect / Extreme

```text
Distributed transactions
2PC
Saga
Outbox Pattern
CDC
Database scaling
Sharding strategy
CAP
Consistency models
Hot partitions
Query planner internals
High-volume data processing
OLTP vs OLAP
Data warehousing
```

---

# 30 SQL Questions to Prepare for a Senior Interview

If you have limited preparation time, master these:

1.**Second highest salary**
2.**Nth highest salary**
3.**Top 3 salaries per department**
4.**Duplicate records**
5.**Delete duplicate records**
6.**Employees earning more than their manager**
7.**Employees earning above department average**
8.**Customers without orders**
9.**Departments without employees**
10.**Latest record per customer**
11.**First record per customer**
12.**Running total**
13.**Previous/next record using `LAG`/`LEAD`**
14.**`ROW_NUMBER` vs `RANK` vs `DENSE_RANK`**
15.**Find consecutive records**
16.**Gaps and islands**
17.**Recursive CTE**
18.**`JOIN` vs `EXISTS`**
19.**`UNION` vs `UNION ALL`**
20.**`WHERE` vs `HAVING`**
21.**`DELETE` vs `TRUNCATE` vs `DROP`**
22.**Index and composite index**
23.**Why an index isn't being used**
24.**How to read an execution plan**
25.**Transaction and ACID**
26.**Isolation levels**
27.**Deadlocks**
28.**Optimistic vs pessimistic locking**
29.**Pagination: OFFSET vs keyset**
30.**Database optimization for millions/billions of records**

For a **Senior Java Full Stack / Spring Boot interview**, SQL questions often become **scenario-based rather than syntax-based**. The interviewer may give you a slow query, concurrent update problem, millions of records, or a microservices transaction problem and ask you to design the solution.

If you master the **60 topics + 75 coding problems above**, you will have a strong SQL preparation base from **basic SQL through Senior/Lead-level database discussions**.

---

# Production and Architect Appendix

The complete supplied interview sheet above is preserved. The following sections add the production details that connect SQL syntax to real Spring Boot systems: correctness, concurrency, performance, security, migrations, observability and recovery.

# Appendix A1. SQL Syntax is Portable; SQL Behaviour is Not Always Portable

SQL has standards, but important details differ across PostgreSQL, MySQL, Oracle, SQL Server and cloud-managed variants.

Check the target database before using:

```text
LIMIT / TOP / FETCH
DATE and timestamp functions
String concatenation
MERGE and upsert syntax
Generated columns
JSON operators
Partial or filtered indexes
Sequences and identity columns
Lock syntax
Isolation defaults
NULL ordering
Full outer join support
```

A strong interview answer says:

> The logical idea is portable, but I would verify the exact syntax, optimizer behaviour, transaction semantics and index support for the selected database version.

Do not present PostgreSQL syntax as universal SQL. Label examples when they depend on a vendor.

# Appendix A2. Query Correctness Before Query Speed

Before optimizing, prove that the result is correct.

For every query, ask:

```text
What is the intended grain of one result row?
Can a join multiply rows?
What should happen when a relationship is missing?
How are NULL values treated?
Are ties allowed?
Is the result deterministic?
What happens at the time boundary?
```

Example: if one customer has five orders and you join customers to orders, the customer appears five times. If you only need to know whether an order exists, `EXISTS` may express the intent more accurately than a join.

For “latest row per customer,” define a deterministic tie-breaker:

```sql
ROW_NUMBER() OVER (
 PARTITION BY customer_id
 ORDER BY order_date DESC, order_id DESC
)
```

Ordering only by a non-unique timestamp can return different rows between executions.

# Appendix A3. Join Placement Changes LEFT JOIN Meaning

These queries are not equivalent.

### Filter in WHERE

```sql
SELECT e.employee_id,
 d.department_name
FROM employees e
LEFT JOIN departments d
 ON d.department_id = e.department_id
WHERE d.department_name = 'IT';
```

The `WHERE` condition removes rows where the department is `NULL`, so the result behaves like an inner join for that condition.

### Filter in ON

```sql
SELECT e.employee_id,
 d.department_name
FROM employees e
LEFT JOIN departments d
 ON d.department_id = e.department_id
 AND d.department_name = 'IT';
```

This preserves every employee and only matches an IT department. Use the placement that matches the business meaning, then test both matched and unmatched cases.

# Appendix A4. Deterministic Pagination for APIs

Offset pagination is easy but becomes expensive and unstable when rows are inserted or deleted while a client is paging.

```sql
SELECT order_id, order_date, amount
FROM orders
WHERE (order_date, order_id) < (:lastDate, :lastOrderId)
ORDER BY order_date DESC, order_id DESC
FETCH FIRST 50 ROWS ONLY;
```

The exact row-value syntax varies by database. The principle is keyset pagination:

```text
Stable unique ordering
+
last-seen cursor
+
matching composite index
```

For an API, return a cursor rather than exposing an arbitrary internal offset. Sign or protect the cursor if it contains values that clients should not modify.

# Appendix A5. SARGability and Search Design

A predicate is generally more index-friendly when the database can compare the indexed column to a range or value without applying a function to every row.

Less index-friendly:

```sql
WHERE CAST(order_date AS DATE) = DATE '2026-09-19'
```

Often better:

```sql
WHERE order_date >= TIMESTAMP '2026-09-19 00:00:00'
 AND order_date < TIMESTAMP '2026-09-20 00:00:00'
```

Other risks include:

- Leading wildcard searches such as `LIKE '%term'`.
- Implicit conversion between indexed numeric and text values.
- Expressions that do not match an expression index.
- Applying a function to an indexed timestamp or identifier.
- Returning a very large result even when the lookup is indexed.

For search requirements, evaluate full-text search, a normalized search column, an expression index or a search engine instead of forcing one B-tree index to solve every problem.

# Appendix A6. Index Design from a Real Query

Start with a representative query and its selectivity:

```sql
SELECT order_id, order_date, amount
FROM orders
WHERE customer_id = :customerId
 AND status = :status
ORDER BY order_date DESC
FETCH FIRST 50 ROWS ONLY;
```

A candidate composite index may be:

```sql
CREATE INDEX idx_orders_customer_status_date
 ON orders(customer_id, status, order_date DESC);
```

The final choice depends on data distribution, write cost, database engine and other queries. Validate with realistic data and an actual plan.

Remember:

```text
Index benefit = fewer expensive rows/pages examined
Index cost = storage + write maintenance + cache pressure
```

An index that helps a rare report but slows every write may be the wrong production trade-off.

# Appendix A7. Reading an Execution Plan

Use a repeatable process:

1. Capture the exact SQL and bind-value types.
2. Check estimated rows versus actual rows.
3. Find the highest-cost or longest-running operator.
4. Check whether a large table or index scan is intentional.
5. Check join order and join algorithm.
6. Check sorts, hashes, spills and temporary structures.
7. Check loops that execute unexpectedly many times.
8. Check locks, I/O and buffer/cache behaviour.
9. Change one thing.
10. Re-run with realistic data and compare correctness.

Do not optimize from a cost number alone. Cost is an optimizer estimate, not always elapsed time. `EXPLAIN ANALYZE`-style commands can execute the query, so use caution with `UPDATE`, `DELETE` and production data.

# Appendix A8. Query Performance Checklist

```text
Correct result?
Expected row count?
Selective predicates?
Appropriate indexes?
SARGable conditions?
Unnecessary columns?
Unnecessary joins?
Large sort or aggregation?
Bad cardinality estimate?
Outdated statistics?
Lock wait?
Network/result-set size?
Connection-pool wait?
Database CPU or I/O saturation?
```

The slow query may be only part of the latency. A Spring Boot request can spend time waiting for a connection, executing SQL, transferring rows, mapping objects and serializing JSON.

# Appendix A9. Safe Atomic Updates

Do not implement a critical state transition as an unsafe read-then-write:

```text
Read balance
Application subtracts amount
Write balance
```

Two concurrent requests can read the same value and lose an update. Prefer a conditional atomic statement:

```sql
UPDATE accounts
SET balance = balance - :amount,
 version = version + 1
WHERE account_id = :accountId
 AND balance >= :amount
 AND version = :expectedVersion;
```

Then verify the affected-row count:

```text
1 row updated -> success
0 rows updated -> insufficient balance or stale version
```

This turns a concurrency assumption into a database-enforced rule.

# Appendix A10. Upsert and Idempotent Writes

An idempotent operation has the same business result when safely repeated.

Use database-native upsert syntax only after checking the target dialect:

```text
PostgreSQL -> INSERT ... ON CONFLICT
MySQL -> INSERT ... ON DUPLICATE KEY UPDATE
SQL Server -> MERGE or an update/insert pattern with documented locking
Oracle -> MERGE
```

For payment, message and command processing, protect the operation with a unique business key:

```sql
CREATE UNIQUE INDEX uq_payment_idempotency
 ON payments(idempotency_key);
```

The unique constraint is a final correctness boundary. Application-level “check then insert” without an atomic constraint is vulnerable to concurrent duplicates.

# Appendix A11. Spring `@Transactional` Behaviour

In a typical Spring proxy-based transaction model:

- A public method called through the managed proxy can start or join a transaction.
- Self-invocation within the same object can bypass the proxy.
- Runtime exceptions and errors normally trigger rollback by default.
- Checked exceptions do not automatically trigger rollback unless configured.
- `readOnly = true` is a hint and does not universally prevent writes.
- The transaction normally ends when the proxied method returns or throws.

Example:

```java
@Transactional
public void transfer(long sourceId, long targetId, BigDecimal amount) {
 debit(sourceId, amount);
 credit(targetId, amount);
}
```

The annotation alone does not make calls to remote services atomic. It controls the configured resource transaction, commonly one database transaction.

# Appendix A12. Transaction Boundaries and Remote Calls

Avoid holding a database transaction open while waiting on a slow external API:

```text
BEGIN transaction
 update database
 call payment provider for 20 seconds
COMMIT
```

This holds locks and a connection during network delay. Better designs use a state machine or outbox:

```text
Transaction 1: create order = PAYMENT_PENDING
 + outbox event

Worker: call provider with idempotency key

Transaction 2: conditionally set order = PAID or PAYMENT_FAILED
```

This does not remove failure handling; it makes the boundaries explicit and recoverable.

# Appendix A13. JPA/Hibernate and the SQL It Generates

Senior Java developers should inspect generated SQL rather than assuming the entity model is efficient.

Common causes of unexpected SQL:

- Lazy association accessed in a loop.
- Eager association fetching too much data.
- Entity-level query returning full objects when a DTO would be enough.
- Dirty checking updating more columns than expected.
- Automatic flush before a query.
- Pagination combined with collection fetch joins.
- Missing batch size for repeated child loads.

Useful approaches:

```text
DTO projection
JOIN FETCH for a controlled relationship
EntityGraph
Batch fetching
JdbcTemplate for focused SQL
NamedParameterJdbcTemplate
Native query only when justified
```

Measure the SQL count and payload size in an integration test. A repository method that looks like one line of Java can issue hundreds of SQL statements.

# Appendix A14. Preventing the N+1 Query Problem

Bad pattern:

```java
List<Order> orders = orderRepository.findAll();
for (Order order : orders) {
 order.getCustomer().getName();
}
```

This can produce one query for orders plus one query per customer. Options include:

```java
@Query("""
 select o
 from Order o
 join fetch o.customer
 where o.status = :status
 """)
List<Order> findByStatusWithCustomer(OrderStatus status);
```

For large results, prefer a carefully designed DTO projection and pagination. A fetch join is not automatically safe for multiple collections or unbounded result sets.

# Appendix A15. HikariCP and Connection-Pool Sizing

The pool is a concurrency control, not a magic performance multiplier.

```text
Database connection capacity
 - reporting/administrative connections
 - replicas or other services
 = capacity available to this service
```

Configure and monitor:

- Maximum pool size.
- Minimum idle connections where appropriate.
- Connection timeout.
- Idle and maximum lifetime.
- Leak detection during investigation, not as a substitute for fixing leaks.
- Validation and database-side timeouts.
- Pool wait time and active/idle counts.

If every service independently uses a large pool, the combined pool sizes can overwhelm the database. Size pools from measured query concurrency and database capacity.

# Appendix A16. Isolation, Locking and Retry Design

Choose the weakest isolation that satisfies the business invariant, then enforce the invariant explicitly.

```text
Read committed + conditional update
may be sufficient for an inventory decrement

Serializable
may be necessary for a smaller strongly consistent workflow
```

Deadlock handling is usually:

```text
Detect deadlock victim
 -> rollback current transaction
 -> retry a bounded number of times
 -> use jitter
 -> emit a metric and structured log
```

Do not retry every SQL exception. A constraint violation, invalid input or permission failure is not repaired by retrying.

# Appendix A17. Read Replicas and Read-After-Write Consistency

Read replicas can improve read scale, but replication is not always instantaneous:

```text
Primary write succeeds
 |
 v
Replica catches up later
```

If a user creates an order and immediately reads it, route that read to the primary or use a consistency strategy that satisfies the user experience. Do not hide replica lag behind arbitrary sleeps.

Document which reads tolerate stale data and which require read-after-write consistency.

# Appendix A18. Schema Migration Strategy

Use a migration tool such as Flyway or Liquibase and keep migrations versioned with the application.

For a safe column change:

```text
Expand
 -> add nullable column/index/table
 -> deploy code compatible with old and new schema
 -> backfill in controlled batches
 -> switch reads/writes
Contract
 -> remove old column after all consumers are upgraded
```

Large backfills should be resumable, throttled and observable. Avoid a single transaction that locks a huge table during peak traffic. Test rollback and forward-fix procedures because not every DDL operation is safely reversible.

# Appendix A19. Outbox Pattern with Spring Boot

When a database change and event publication must be consistent:

```text
@Transactional
createOrder()
 -> insert order
 -> insert outbox event
 -> commit

Publisher
 -> read NEW outbox rows
 -> publish with event ID
 -> mark SENT
```

The publisher can send an event and crash before marking it `SENT`, so consumers must be idempotent. Use an event ID, status, retry count, next-attempt time and error details. Polling, CDC and a message relay are implementation choices.

# Appendix A20. SQL Injection and Dynamic Queries

Parameterize values:

```java
String sql = "select * from users where username = :username";
```

Do not concatenate user input into SQL. Parameters generally cannot replace SQL identifiers, so dynamic table names, sort columns and directions require an allowlist:

```java
Map<String, String> allowedSorts = Map.of(
"name", "employee_name",
"salary", "salary",
"joined", "hire_date"
);

String column = allowedSorts.get(requestedSort);
if (column == null) {
 throw new IllegalArgumentException("Unsupported sort field");
}
```

The allowlist must map external values to fixed SQL fragments. Never use raw client input as an identifier.

# Appendix A21. Data Security Beyond SQL Injection

Production database security also includes:

- Separate application read/write roles.
- Database credentials from a secret manager.
- TLS and certificate rotation.
- Encryption at rest and backup encryption.
- Row-level or tenant-level authorization where required.
- Masking or tokenization of sensitive values.
- Audit logging for privileged access.
- Restricted database network paths.
- Safe handling of SQL and bind values in logs.

Never log complete payment data, passwords, tokens or unredacted personal information just to debug a query.

# Appendix A22. Observability for SQL in Spring Boot

Track database signals at both application and database layers:

```text
Application
 -> query duration
 -> connection-pool wait
 -> active/idle connections
 -> transaction rollback count
 -> timeout count

Database
 -> CPU and I/O
 -> lock waits
 -> deadlocks
 -> slow queries
 -> cache/buffer hit ratio
 -> replication lag
```

Use parameterized SQL logging or a safe query fingerprint. Do not emit sensitive bind values by default. A query fingerprint groups similar statements without exposing customer data.

# Appendix A23. Testing SQL and Persistence Code

### Unit tests

Test query-building logic, pagination cursor rules, mapping and business classification without needing a database.

### Repository integration tests

Run against the same database engine and a realistic schema when SQL semantics matter. Test:

- NULL behaviour.
- Duplicate and tie cases.
- Missing relationships.
- Time-zone and boundary dates.
- Pagination stability.
- Unique and foreign-key constraints.
- Optimistic-lock conflicts.
- Transaction rollback.
- Deadlock retry classification.

### Migration tests

Apply migrations from an empty database and upgrade from a representative previous version. Validate indexes, constraints, data backfills and rollback/forward-fix procedures.

### Performance tests

Use realistic row counts and distributions. A query that is fast on ten rows proves very little about a table with 500 million rows.

# Appendix A24. Database Reliability and Recovery

For production databases, define:

```text
RPO -> acceptable data loss
RTO -> acceptable recovery time
Backup retention
Point-in-time recovery
Failover process
Restore validation
Read-replica or standby strategy
Application retry and reconnect behaviour
```

A backup that has never been restored is an assumption, not evidence. Schedule restore tests and verify that applications can reconnect, migrations are compatible and secrets/certificates are available after recovery.

# Appendix A25. OLTP Query Versus Reporting Query

Do not let a dashboard scan the transactional database during peak order processing:

```text
OLTP primary
 -> short transactions and current state

CDC/outbox/replication
 -> analytical store or warehouse

BI/reporting
 -> large scans and aggregations away from OLTP
```

Read replicas can reduce pressure, but they do not automatically solve workload isolation, schema shape, retention or query cost. Choose an analytical model when the reporting workload deserves one.

# Appendix A26. Partitioning, Archival and Retention

For very large time-based tables, partitioning can improve maintenance and pruning:

```text
orders_2026_01
orders_2026_02
orders_2026_03
```

The exact mechanism is database-specific. The application still needs:

- A partition-friendly predicate.
- A retention policy.
- Archival and legal-hold rules.
- A strategy for late-arriving data.
- Indexes appropriate to each partition.
- Monitoring for a hot current partition.

Partitioning is not a replacement for a good access path. A query that does not constrain the partition key may still scan many partitions.

# Appendix A27. Distributed Data and Sharding Decisions

Shard only when a single database cannot meet scale, isolation or availability requirements and simpler options have been exhausted.

Evaluate:

```text
Shard-key cardinality and locality
Hot-key risk
Cross-shard queries
Cross-shard transactions
Rebalancing
Operational tooling
Backups and restore
Global uniqueness
Tenant movement
```

A customer or tenant key can keep related data together, but a large tenant can become a hot shard. Consistent hashing and virtual shards can make rebalancing easier, at the cost of routing complexity.
# Appendix A28. Senior Scenario: A Query Became Slow After Deployment

A disciplined response:

1. Confirm the exact endpoint, query fingerprint and time window.
2. Check whether data volume, bind values or distribution changed.
3. Compare the current and previous execution plans.
4. Check statistics, indexes, locks and database resource saturation.
5. Check application connection-pool wait and result serialization.
6. Roll back or disable the feature if user impact is severe.
7. Apply the smallest safe correction.
8. Re-test with production-like data.
9. Add a regression test or plan-monitoring guard.

Do not immediately add an index without checking write amplification, selectivity and competing indexes.

# Appendix A29. Senior Scenario: Two Payments Were Created

Possible causes:

```text
Client retry after timeout
Message redelivery
Concurrent requests
Application check-then-insert race
Missing unique constraint
Ambiguous external provider outcome
```

A robust design uses:

```text
Client/payment idempotency key
Unique database constraint
Atomic insert or conditional state transition
Provider idempotency key
Durable status and reconciliation
```

The solution is not merely “increase transaction isolation.” Isolation can protect some database races, but the external provider and retry lifecycle still require an idempotency design.

# Appendix A30. Senior Scenario: Database Connection Pool is Exhausted

Investigate:

```text
Pool wait time
Long-running SQL
Unclosed resources
Large transaction scope
External calls inside transactions
Slow database or lock waits
Pool size versus database limit
Connection leaks in error paths
```

Mitigate by fixing the cause, bounding timeouts, reducing transaction scope and right-sizing the pool. Increasing `maximumPoolSize` can make the database failure worse.

# Appendix A31. Interview Answer: How Do You Optimize a Slow SQL Query?

> I first confirm correctness and capture the exact SQL with bind-value types and a representative workload. I inspect the actual execution plan, comparing estimated and actual rows, scans, joins, sorts, spills, loops and wait time. I check indexes and column order, SARGability, statistics, cardinality, locks, result-set size and connection-pool wait. I make one measured change, such as a better access path, query rewrite, projection, pagination or data-model adjustment, then compare plans and latency using realistic data. I also consider write amplification and concurrency, because an index that improves one query can hurt every insert or update. Finally, I add monitoring and a regression test so the improvement remains visible after future data and code changes.

# Appendix A32. Interview Answer: How Do You Handle a Distributed Transaction?

> I first ask whether the operation can be owned by one database transaction. If multiple services or databases are involved, I avoid assuming a local `@Transactional` annotation provides atomicity across them. I model explicit states, use an outbox or CDC to publish durable events, make consumers idempotent, and use a Saga with compensation or orchestration when business steps can fail independently. I reserve two-phase commit for environments where its operational and performance costs are understood and justified. The design must include retries, timeouts, reconciliation and observability.

# Appendix A33. Interview Answer: JPA or JDBC?

> I choose based on access pattern rather than treating one as universally better. JPA is productive for aggregate-oriented CRUD and identity management, but I inspect generated SQL and control fetching to avoid N+1 queries and oversized object graphs. JDBC or `JdbcTemplate` is often clearer for reporting, bulk operations, tuned joins and SQL-specific features. Both require correct transactions, parameterization, timeouts, connection-pool sizing and integration tests against the target database. A mature service may use both behind clear repository boundaries.

# Appendix A34. SQL Review Checklist

### Correctness

- Result-row grain is explicit.
- Joins cannot multiply rows unexpectedly.
- NULL and missing relationships are tested.
- Ties and ordering are deterministic.
- Time zones and inclusive/exclusive boundaries are defined.
- Constraints enforce important invariants.

### Performance

- Query plan is reviewed with realistic data.
- Predicates are selective and index-friendly.
- Composite-index order matches real filters and ordering.
- Result columns and page sizes are bounded.
- Connection-pool and transaction wait are observable.
- Writes and index maintenance cost are considered.

### Transactions and reliability

- Transaction boundaries do not include unnecessary network calls.
- Isolation and locks match the business invariant.
- Deadlocks and transient failures have bounded retry handling.
- Idempotency and unique constraints protect retried commands.
- Migrations are backward compatible and tested.
- Backups and restore procedures are validated.

### Spring Boot and security

- Queries use parameter binding.
- Dynamic identifiers use allowlists.
- JPA generated SQL and N+1 behaviour are tested.
- Secrets and sensitive bind values are not logged.
- Database roles have least privilege.
- SQL metrics and slow-query investigation are available.

# Appendix A35. Final Senior SQL Mindset

```text
Write correct SQL
 -> understand logical processing
 -> model data for access patterns
 -> enforce invariants in the database
 -> inspect the actual execution plan
 -> control transactions and concurrency
 -> protect the database from the application
 -> observe, test and recover in production
```

The strongest senior answer is rarely just a query. It explains:

```text
What result is required?
Why is this query correct?
What happens with NULLs, duplicates and concurrency?
Which index or data model supports it?
How will the plan behave at scale?
What happens when the request is retried?
How does Spring manage the transaction?
How will we monitor, migrate and recover it?
```

---

# PART B: NoSQL Interview Questions for Senior Java Developers

Senior interviews increasingly include a NoSQL round, usually framed as *"Why did you choose this database?"* or *"Model this feature in MongoDB/Cosmos DB/Cassandra/DynamoDB."* The goal is not to memorise every product, but to show that you understand **data models, access patterns, consistency and trade-offs**.

## NoSQL Level 1 — Fundamentals

### N1. What is NoSQL?

**Definition:** NoSQL ("Not only SQL") is a family of databases that store data in models other than relational tables — documents, key-value pairs, wide columns or graphs — usually designed for horizontal scale, flexible schemas and specific access patterns.

**Simple explanation:** A relational database is a well-organised filing cabinet with fixed forms. A NoSQL database is a set of specialised tools: a fast locker (key-value), a folder per customer holding everything about them (document), a huge ledger split across many books (wide-column), or a map of relationships (graph).

### N2. SQL vs NoSQL — the comparison every interviewer asks

| Aspect | Relational (SQL) | NoSQL |
| --- | --- | --- |
| Data model | Tables, rows, columns, foreign keys | Documents, key-value, wide-column, graph |
| Schema | Fixed, enforced by DB (schema-on-write) | Flexible, enforced mostly by the application (schema-on-read) |
| Query language | Standard SQL, ad-hoc joins | Product-specific APIs; joins limited or absent |
| Design approach | Normalise data, then write any query | Start from **access patterns**, then shape the data |
| Scaling | Mostly vertical; replicas; sharding is hard | Horizontal scaling by partition/shard key is built in |
| Transactions | Strong multi-row ACID | Often single-item/partition; multi-document support varies |
| Consistency | Strong by default | Tunable: strong to eventual |
| Best at | Complex queries, integrity, reporting, money | Huge scale, low-latency key lookups, flexible or nested data |

**Senior answer:***"I don't choose SQL or NoSQL by fashion. I start from the access patterns, consistency needs and scale. Financial ledgers and complex reporting stay relational; high-volume, key-based, flexible-schema workloads such as session state, event logs, product catalogues or notification history often fit NoSQL. Many systems use both."*

### N3. The four main NoSQL types

| Type | How data looks | Examples | Real-time use |
| --- | --- | --- | --- |
| Key-value | `key -> value` | Redis, DynamoDB (basic), Memcached | Session store, cache, rate limiter, feature flags |
| Document | JSON/BSON documents with nested fields | MongoDB, Azure Cosmos DB (NoSQL API), Couchbase | Product catalogue, user profile, policy document, notification record |
| Wide-column | Rows grouped by partition key, sorted by clustering key | Apache Cassandra, ScyllaDB, HBase, Bigtable | IoT telemetry, time-series events, messaging history |
| Graph | Nodes and edges | Neo4j, Amazon Neptune, Cosmos DB Gremlin | Fraud rings, recommendations, org charts, network topology |

Also worth knowing: **search engines** (Elasticsearch/OpenSearch) and **time-series databases** (InfluxDB, TimescaleDB) are often grouped with NoSQL.

### N4. When should you NOT use NoSQL?

- Many-to-many relationships queried in unpredictable, ad-hoc ways.
- Strong multi-entity invariants (double-entry accounting, inventory across warehouses).
- Heavy reporting with joins and aggregations over the whole dataset.
- A small dataset where one PostgreSQL instance easily fits the load.
- A team without operational experience of the chosen NoSQL product.

**Real-time example:** a team moved an insurance-claims system to a document DB "for scale" but needed reports joining claims, policies, payments and agents. They ended up exporting everything nightly to a SQL warehouse — added cost with no benefit, because the OLTP volume never needed horizontal scale.

## NoSQL Level 2 — Distributed Systems Theory

### N5. CAP theorem

**Definition:** In a distributed system, when a **network Partition** happens, you must choose between **Consistency** (every read sees the latest write or gets an error) and **Availability** (every request gets a non-error response, possibly stale).

```text
Network is healthy -> you can have both C and A
Network partition (P) -> choose:
 CP: refuse or block some requests to stay consistent
 AP: answer every request, accept temporary divergence
```

| Choice | Example behaviour | Typical fit |
| --- | --- | --- |
| CP | MongoDB primary unreachable -> writes fail until a new primary is elected | Account balances, inventory |
| AP | Cassandra with `ONE` consistency -> any replica answers, may be stale | Likes, view counters, telemetry |

**Common mistake:** saying "MongoDB is CP, Cassandra is AP" as absolute facts. Most modern databases are **tunable** — the behaviour depends on read/write concern or consistency level.

### N6. PACELC — the more complete answer

```text
If Partition -> choose Availability or Consistency
Else (normal) -> choose Latency or Consistency
```

Even without failures, waiting for more replicas to confirm a write costs latency. Mentioning PACELC signals senior-level understanding.

### N7. ACID vs BASE

| ACID | BASE |
| --- | --- |
| Atomic, Consistent, Isolated, Durable | **B**asically **A**vailable, **S**oft state, **E**ventually consistent |
| Correctness first | Availability and scale first |
| Bank transfer | Social-media like counter |

### N8. Consistency models (from strongest to weakest)

| Model | Guarantee | Real-time example |
| --- | --- | --- |
| Strong / linearizable | Every read sees the latest committed write | Balance after a payment |
| Bounded staleness | Reads lag writes by at most K versions or T seconds | Stock prices lagging by 5 s |
| Session | A client always sees its own writes, in order | User updates profile and immediately sees the change |
| Consistent prefix | Reads never see writes out of order | Chat messages never appear before earlier ones |
| Eventual | Replicas converge eventually; no ordering guarantee | View counts, recommendations |

Azure Cosmos DB exposes exactly these five levels, which makes it a common interview reference.

### N9. Replication, quorum and the `R + W > N` rule

```text
N = number of replicas (e.g. 3)
W = replicas that must acknowledge a write
R = replicas that must answer a read

R + W > N -> every read overlaps at least one replica with the latest write (strong reads)

N=3, W=2, R=2 -> 4 > 3 -> strong
N=3, W=1, R=1 -> 2 < 3 -> fast but possibly stale
```

### N10. Partitioning (sharding) and the partition key

**Definition:** Data is split across nodes by a **partition key**. All items with the same key live together.

A good partition key has:

```text
High cardinality -> many distinct values (customerId, deviceId)
Even distribution -> no single value receives most traffic
Matches the main query -> most reads include the key, so they hit ONE partition
```

**Real-time example: the hot partition**

```text
Bad key: country
 "JP" receives 90% of writes -> one node overloaded, others idle -> throttling (HTTP 429)

Better key: customerId
 millions of values, traffic spread evenly

Time-series trap: key = eventDate
 all of today's writes hit one partition
 Fix: composite key deviceId + date bucket (e.g. "dev-42#2026-09-25")
```

### N11. Consistent hashing

Nodes and keys are placed on a hash ring; each key belongs to the next node clockwise. Adding or removing a node moves only the keys of its neighbours, not the whole dataset. **Virtual nodes** spread each physical node across many ring positions to balance load. Used by Cassandra, DynamoDB and many distributed caches.

## NoSQL Level 3 — MongoDB (Document Database)

### N12. What is a document? Embed or reference?

```json
{
"_id": "POL-1001",
"customerId": "C-42",
"status": "ACTIVE",
"premium": { "amount": 12000, "currency": "JPY", "frequency": "MONTHLY" },
"beneficiaries": [
 { "name": "Aiko", "relation": "SPOUSE", "share": 70 },
 { "name": "Ken", "relation": "CHILD", "share": 30 }
 ],
"createdAt": "2026-09-25T02:00:00Z"
}
```

| Embed (nested inside) | Reference (store an ID) |
| --- | --- |
| Data is read together ("policy with its beneficiaries") | Data is shared by many parents (agent, product) |
| Child has no independent life | Child is large or grows without bound |
| One-to-few | One-to-many-thousands or many-to-many |
| Needs atomic update with the parent | Updated independently and frequently |

**Rules to say aloud:**

1.*"Data that is accessed together is stored together."*
2.*"Never embed an unbounded array"* — MongoDB documents are limited to 16 MB, and growing arrays slow every update. Store a customer's 50,000 login events in a separate collection, not inside the customer document.

### N13. MongoDB indexes

```javascript
db.policies.createIndex({ customerId: 1, status: 1, createdAt: -1 }) // compound
db.policies.createIndex({ "beneficiaries.name": 1 }) // multikey (array)
db.sessions.createIndex({ expiresAt: 1 }, { expireAfterSeconds: 0 }) // TTL auto-delete
db.users.createIndex({ email: 1 }, { unique: true }) // uniqueness
```

The **ESR rule** for compound index order: **E**quality fields, then **S**ort fields, then **R**ange fields. Verify with `explain("executionStats")` — look for `IXSCAN` rather than `COLLSCAN`, and compare `totalDocsExamined` with `nReturned`.

### N14. Aggregation pipeline (MongoDB's GROUP BY)

```javascript
// Total premium per status for active customers, largest first
db.policies.aggregate([
 { $match: { createdAt: { $gte: ISODate("2026-01-01") } } }, // like WHERE; put first so an index can be used
 { $group: { _id: "$status", total: { $sum: "$premium.amount" }, count: { $sum: 1 } } },
 { $sort: { total: -1 } }
])
```

| SQL | MongoDB stage |
| --- | --- |
| `WHERE` | `$match` |
| `GROUP BY` + aggregates | `$group` |
| `SELECT col` | `$project` |
| `ORDER BY` | `$sort` |
| `LIMIT` | `$limit` |
| `JOIN` | `$lookup` (use sparingly) |
| Explode array | `$unwind` |

### N15. MongoDB replica sets, read concern and write concern

```text
Replica set: 1 primary (writes) + 2 secondaries (copies)
Primary fails -> election -> a secondary becomes primary within seconds
```

| Setting | Meaning | When to use |
| --- | --- | --- |
| `w: 1` | Primary acknowledged | Low-value logs |
| `w: "majority"` | Majority of nodes acknowledged; survives failover | Orders, payments, anything important |
| `readConcern: "majority"` | Read only data committed on a majority | Avoid reading data that may be rolled back |
| `readPreference: secondary` | Read from replicas | Reports that tolerate staleness |

**Real-time bug:** an application used `w: 1`. The primary acknowledged an order, crashed before replicating, and the newly elected primary never had the order — it was rolled back. Switching to `w: "majority"` fixed it at the cost of a few milliseconds of latency.

### N16. Does MongoDB support transactions?

Yes — single-document operations are always atomic, and multi-document ACID transactions are supported on replica sets and sharded clusters (since 4.0/4.2). But they cost more and have time limits, so the preferred design is to **model data so that one business operation updates one document**.

```java
@Service
@RequiredArgsConstructor
class PolicyTransferService {

 private final MongoTemplate mongo;

 @Transactional // requires a MongoTransactionManager bean
 public void moveBeneficiaryShare(String policyId, String from, String to, int share) {
Query q = Query.query(Criteria.where("_id").is(policyId)
 .and("beneficiaries").elemMatch(Criteria.where("name").is(from).and("share").gte(share)));
Update u = new Update().inc("beneficiaries.$.share", -share);
 if (mongo.updateFirst(q, u, Policy.class).getModifiedCount() == 0) {
 throw new IllegalStateException("Insufficient share");
 }
 // second update on the same document; embedding kept both changes in one document
 mongo.updateFirst(Query.query(Criteria.where("_id").is(policyId).and("beneficiaries.name").is(to)),
 new Update().inc("beneficiaries.$.share", share), Policy.class);
 }
}
```

### N17. Spring Data MongoDB essentials

```java
@Document("notifications")
@CompoundIndex(name = "customer_status_created", def = "{'customerId': 1, 'status': 1, 'createdAt': -1}")
public class Notification {
 @Id private String id;
 private String customerId;
 private NotificationStatus status;
 private Instant createdAt;
 @Version private Long version; // optimistic locking
}

public interface NotificationRepository extends MongoRepository<Notification, String> {

List<Notification> findTop20ByCustomerIdOrderByCreatedAtDesc(String customerId);

 @Query("{ 'status': ?0, 'createdAt': { $lt: ?1 } }")
Page<Notification> findStale(NotificationStatus status, Instant before, Pageable page);
}
```

Atomic update without read-modify-write:

```java
mongoTemplate.findAndModify(
 Query.query(Criteria.where("_id").is(id).and("status").is("PENDING")),
 new Update().set("status", "PROCESSING").set("lockedAt", Instant.now()),
 FindAndModifyOptions.options().returnNew(true),
 Notification.class); // only one worker can claim the document
```

### N18. MongoDB sharding

```text
Client -> mongos (router) -> shards (each a replica set)
Shard key chosen per collection; ranges/hashes of the key are split into chunks
```

| Shard key | Result |
| --- | --- |
| `{ createdAt: 1 }` (monotonic) | All inserts hit the last chunk -> hot shard |
| `{ customerId: "hashed" }` | Even writes; range queries on customerId scatter |
| `{ tenantId: 1, createdAt: 1 }` | Tenant queries target one shard; watch for very large tenants |

Queries **without** the shard key become *scatter-gather* across all shards — a frequent cause of production slowness.

## NoSQL Level 4 — Redis (Key-Value and Cache)

### N19. What is Redis and why is it fast?

Redis is an in-memory data-structure store. It is fast because data lives in RAM, commands are simple, and the core executes commands on a single thread (no lock contention), with optional persistence (RDB snapshots, AOF log).

| Data type | Command examples | Real-time use |
| --- | --- | --- |
| String | `SET`, `GET`, `INCR`, `SETEX` | Cache entry, counter, OTP code |
| Hash | `HSET`, `HGETALL` | User session fields |
| List | `LPUSH`, `RPOP` | Simple queue, recent activity |
| Set | `SADD`, `SISMEMBER` | Unique visitors, blocked tokens |
| Sorted set | `ZADD`, `ZRANGE` | Leaderboards, rate-limit sliding windows, delayed jobs |
| Stream | `XADD`, `XREADGROUP` | Lightweight event log with consumer groups |

### N20. Caching patterns

| Pattern | Flow | Trade-off |
| --- | --- | --- |
| Cache-aside (lazy loading) | App reads cache -> miss -> reads DB -> writes cache | Most common; first request is slow; possible stale data |
| Read-through | Cache library loads from DB on miss | Simpler app code |
| Write-through | Write to cache and DB together | Cache always fresh; slower writes |
| Write-behind | Write cache now, DB later asynchronously | Fast writes; risk of data loss |
| Refresh-ahead | Refresh popular keys before expiry | Avoids miss spikes; extra load |

Spring Boot cache-aside:

```java
@Service
@RequiredArgsConstructor
class ProductService {

 private final ProductRepository repository;

 @Cacheable(cacheNames = "product", key = "#id", unless = "#result == null")
 public ProductDto get(long id) {
 return repository.findById(id).map(ProductDto::from).orElse(null);
 }

 @CacheEvict(cacheNames = "product", key = "#id")
 @Transactional
 public void updatePrice(long id, BigDecimal price) {
 repository.updatePrice(id, price);
 }
}
```

```yaml
spring:
 cache:
 type: redis
 redis:
 time-to-live: 10m # always set a TTL so stale data eventually expires
```

### N21. Cache problems every senior should name

| Problem | What happens | Fix |
| --- | --- | --- |
| Cache stampede / thundering herd | Popular key expires; 10,000 requests hit the DB at once | Lock/single-flight on miss, jittered TTL, refresh-ahead |
| Cache penetration | Requests for IDs that don't exist always miss and hit the DB | Cache negative results briefly, Bloom filter |
| Cache avalanche | Many keys expire at the same moment | Randomise TTLs, warm up gradually |
| Stale data | DB updated, cache still old | Evict on write, short TTL, CDC-driven invalidation |
| Hot key | One key receives huge traffic | Local in-process cache (Caffeine) in front of Redis, key replication |
| Big key | One 50 MB value blocks the single thread | Split values, compress, keep values small |

**Real-time example:** a flash sale page cached product 777 for 60 s. At expiry, 8,000 concurrent requests missed simultaneously and the database CPU hit 100%. Fix: TTL of `60s + random(0..15s)` and a short Redis lock so only one request rebuilds the entry while others serve the old value.

### N22. Distributed lock with Redis

```text
SET lock:invoice-job <uniqueToken> NX PX 30000
 NX -> only if not already set
 PX -> auto-expire so a crashed holder does not block forever
Release only if the value still equals your token (Lua script), so you never delete someone else's lock
```

Senior caveat: a Redis lock is **best-effort**. A GC pause longer than the TTL lets two holders run at once. For correctness-critical work, also use a **fencing token** or database constraint. Libraries: Redisson, ShedLock (Redis/JDBC provider), Spring Integration `RedisLockRegistry`.

### N23. Rate limiter with Redis (real-time)

```text
Key: rate:{userId}:{currentMinute}
INCR key; EXPIRE key 60 on first increment
If value > 100 -> return HTTP 429
```

This works across all pods because the counter lives in Redis, not in each JVM. A sorted-set sliding window is more accurate at minute boundaries.

### N24. Redis eviction and persistence

| Policy | Evicts |
| --- | --- |
| `noeviction` | Nothing; writes fail when memory is full |
| `allkeys-lru` | Least recently used key (typical for pure caches) |
| `volatile-lru` | LRU among keys with a TTL |
| `allkeys-lfu` | Least frequently used |

Persistence: RDB = periodic snapshot (fast restart, may lose minutes); AOF = append log (`everysec` loses up to ~1 s). Never treat Redis as the only copy of important business data unless it is explicitly configured and operated for durability.

## NoSQL Level 5 — Apache Cassandra (Wide-Column)

### N25. Cassandra data model: partition key + clustering key

```sql
CREATE TABLE telemetry_by_device (
 device_id text,
 day date,
 event_time timestamp,
 temperature double,
 PRIMARY KEY ((device_id, day), event_time)
) WITH CLUSTERING ORDER BY (event_time DESC);
```

```text
(device_id, day) -> partition key: decides WHICH node stores the data
event_time -> clustering key: sort order INSIDE the partition
```

Query that works well (one partition, already sorted):

```sql
SELECT * FROM telemetry_by_device
WHERE device_id = 'dev-42' AND day = '2026-09-25'
LIMIT 100;
```

Query that Cassandra rejects without `ALLOW FILTERING` (full cluster scan):

```sql
SELECT * FROM telemetry_by_device WHERE temperature > 80;
```

### N26. Query-first modelling (denormalisation is normal)

In Cassandra you create **one table per query**. If the app needs "orders by customer" and "orders by status", create two tables and write to both.

| SQL habit | Cassandra approach |
| --- | --- |
| One normalised `orders` table + indexes | `orders_by_customer`, `orders_by_status`, `orders_by_day` |
| Joins at read time | Data duplicated at write time |
| Add an index later | Design partitions around queries up front |

### N27. Tunable consistency

| Level | Replicas that must respond (RF=3) | Use |
| --- | --- | --- |
| `ONE` | 1 | Fast telemetry writes |
| `QUORUM` | 2 | Balanced default for important data |
| `LOCAL_QUORUM` | 2 in the local data centre | Multi-region apps (avoid cross-region latency) |
| `ALL` | 3 | Rare; any node down = failure |

`QUORUM` write + `QUORUM` read gives `2 + 2 > 3` -> strong reads.

### N28. Tombstones — a classic Cassandra production issue

Deletes are writes of a *tombstone* marker. Reading a partition with 100,000 tombstones is very slow, and Cassandra may abort the query. Causes: using Cassandra as a queue, deleting many rows, writing `null` values. Fixes: time-bucketed partitions, TTL on rows, avoid queue patterns, drop whole partitions instead of deleting row by row.

## NoSQL Level 6 — Amazon DynamoDB and Azure Cosmos DB

### N29. DynamoDB keys and indexes

```text
Partition key (PK) -> which partition stores the item
Sort key (SK) -> order within the partition; enables range queries
GSI -> alternate PK/SK, eventually consistent, separate capacity
LSI -> same PK, different SK, defined at table creation
```

**Single-table design example:**

| PK | SK | Data |
| --- | --- | --- |
| `CUSTOMER#42` | `PROFILE` | name, email |
| `CUSTOMER#42` | `ORDER#2026-09-25#1001` | amount, status |
| `CUSTOMER#42` | `ORDER#2026-09-20#0993` | amount, status |

"Get customer 42 and their latest 10 orders" = one `Query` on `PK = CUSTOMER#42`, `SK begins_with ORDER#`, descending, limit 10.

**Conditional writes** (the NoSQL equivalent of optimistic locking and idempotency):

```text
PutItem ... ConditionExpression: attribute_not_exists(PK) -> insert only once (idempotency key)
UpdateItem ... ConditionExpression: version = :expected -> optimistic lock
```

### N30. Azure Cosmos DB essentials (very relevant for Azure-hosted Spring Boot apps)

| Concept | Meaning | Interview point |
| --- | --- | --- |
| Container | Collection of JSON items | Throughput and indexing are configured per container/database |
| Partition key | Path such as `/customerId` | Cannot be changed later without migrating data; choose carefully |
| Logical partition | All items with the same key value | Limited to 20 GB; transactions only within one logical partition |
| Request Units (RU/s) | Normalised cost of every operation | A 1 KB point read = 1 RU; cross-partition queries cost far more |
| HTTP 429 | Throttled because RU/s exceeded | SDK retries; fix hot partitions or increase/autoscale RU |
| Consistency levels | Strong, Bounded staleness, Session (default), Consistent prefix, Eventual | Session gives read-your-own-writes per client |
| Change feed | Ordered stream of inserts/updates per partition | Build projections, CDC, outbox-style event publishing |
| TTL | Automatic deletion per item/container | Retention without delete jobs |
| Indexing policy | Everything indexed by default | Exclude unused paths to reduce write RU |

**Point read vs query — the most important Cosmos cost lesson:**

```java
// Point read: id + partition key -> ~1 RU for 1 KB, single partition
container.readItem(id, new PartitionKey(customerId), NotificationRecord.class);

// Query without partition key -> fan-out to every physical partition, many RUs
container.queryItems("SELECT * FROM c WHERE c.status = 'FAILED'", options, NotificationRecord.class);
```

**Spring Data Azure Cosmos:**

```java
@Container(containerName = "notifications")
public class NotificationRecord {
 @Id private String id;
 @PartitionKey private String customerId;
 private String status;
 private Instant createdAt;
 @Version private String _etag; // optimistic concurrency via ETag
}

public interface NotificationRecordRepository extends CosmosRepository<NotificationRecord, String> {
List<NotificationRecord> findByCustomerIdAndStatus(String customerId, String status); // single-partition query
}
```

**Real-time example: archival batch on Cosmos DB**

```text
Requirement: move notification records older than 13 months to cold storage every night.

Naive: SELECT * FROM c WHERE c.createdAt < @cutoff (cross-partition, returns millions)
 -> RU spikes, 429 throttling for the live portal, JVM OutOfMemoryError

Better: - set container TTL for pure deletion requirements, or
 - iterate per partition key / use the change feed,
 - page with continuation tokens (maxItemCount = 100),
 - stream each page to Blob Storage, then delete,
 - cap throughput with a rate limiter so the batch never exceeds e.g. 30% of RU/s,
 - record progress (last continuation token) so a restart resumes instead of starting over.
```

## NoSQL Level 7 — Graph Databases and Search Engines

### N31. When is a graph database the right answer?

When the question is about **relationships of unknown depth**: "Which claims share a phone number, bank account or address with a known fraudulent claim within 3 hops?" In SQL that is multiple recursive self-joins; in a graph database it is one traversal.

```cypher
MATCH (c:Claim {id: 'CL-9'})-[:USES*1..3]-(other:Claim)
WHERE other.flagged = true
RETURN DISTINCT other.id
```

### N32. Elasticsearch/OpenSearch vs a database

| Need | Use |
| --- | --- |
| Full-text search, fuzzy match, relevance ranking, facets | Elasticsearch/OpenSearch |
| System of record, transactions, constraints | Relational or document database |

Typical pattern: write to the database (source of truth) -> publish change via outbox/CDC -> index into Elasticsearch -> search reads from Elasticsearch, detail page reads from the database. Mention that the index is **eventually consistent** with the database.

## NoSQL Level 8 — Data Modelling Scenarios

### N33. Scenario: design a notification-history store for 50 million customers

```text
Access patterns:
 1. Customer portal: latest 20 notifications for customer X (very frequent)
 2. Support: one notification by id for customer X (frequent)
 3. Batch: notifications with status FAILED from the last 24 hours (nightly)
 4. Retention: delete after 13 months (nightly)

Design (document DB / Cosmos DB):
 Partition key: customerId -> patterns 1 and 2 hit one partition
 Composite index: customerId + createdAt DESC
 Pattern 3: change feed or a small "failed_notifications" container keyed by day bucket
 Pattern 4: TTL = 13 months -> no delete job, no RU spikes
```

Say out loud: *"I listed the access patterns first, then picked the partition key that serves the most frequent query in a single partition, and handled the rare cross-cutting query with a secondary projection."*