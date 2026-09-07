Absolutely. For a **Senior Java/Spring Boot Developer**, SQL interviews usually go far beyond basic `SELECT`, `JOIN`, and `GROUP BY`. They can cover **query writing, joins, subqueries, CTEs, window functions, transactions, indexing, execution plans, concurrency, optimization, stored procedures, partitioning, recursive queries, and database design**.

Below is a **Senior Developer SQL Interview Master Sheet**, organized from **Basic → Intermediate → Advanced → Extreme**.

---

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

| Operation | Data             | Structure | WHERE |
| --------- | ---------------- | --------- | ----- |
| DELETE    | Removes rows     | Remains   | Yes   |
| TRUNCATE  | Removes all rows | Remains   | No    |
| DROP      | Removes table    | Removed   | No    |

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
WHERE  → filters rows BEFORE grouping

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

| id | name  | department | salary |
| -: | ----- | ---------- | -----: |
|  1 | John  | IT         |  80000 |
|  2 | David | IT         |  90000 |
|  3 | Mike  | IT         |  70000 |
|  4 | Sarah | HR         |  60000 |
|  5 | Jane  | HR         |  70000 |

If you want to calculate the **average salary of each department**, you could use:

```sql
SELECT department, AVG(salary)
FROM employees
GROUP BY department;
```

Result:

| department | avg_salary |
| ---------- | ---------: |
| IT         |      80000 |
| HR         |      65000 |

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

| name  | department | salary | dept_avg_salary |
| ----- | ---------- | -----: | --------------: |
| John  | IT         |  80000 |           80000 |
| David | IT         |  90000 |           80000 |
| Mike  | IT         |  70000 |           80000 |
| Sarah | HR         |  60000 |           65000 |
| Jane  | HR         |  70000 |           65000 |

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
 ├── John   80000
 ├── David  90000
 └── Mike   70000

HR
 ├── Sarah  60000
 └── Jane   70000
```

The average is calculated separately for each partition.

### Without `PARTITION BY`

```sql
AVG(salary) OVER ()
```

calculates the average across **all employees**.

```text
John   80000 ─┐
David  90000  │
Mike   70000  ├── Overall average
Sarah  60000  │
Jane   70000 ─┘
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

| name  | salary | row_num |
| ----- | -----: | ------: |
| David |  90000 |       1 |
| John  |  80000 |       2 |
| Mike  |  70000 |       3 |
| Jane  |  70000 |       4 |
| Sarah |  60000 |       5 |

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

| name  | department | salary | rn |
| ----- | ---------- | -----: | -: |
| David | IT         |  90000 |  1 |
| John  | IT         |  80000 |  2 |
| Mike  | IT         |  70000 |  3 |
| Jane  | HR         |  70000 |  1 |
| Sarah | HR         |  60000 |  2 |

This is extremely useful when you need:

> **Top N records per group**

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
ROW_NUMBER  → always unique
RANK        → ties + gaps
DENSE_RANK  → ties + no gaps
```

---

# 8. Running total

Window functions are excellent for calculating running totals.

Suppose:

| date  | amount |
| ----- | -----: |
| Jan 1 |    100 |
| Jan 2 |    200 |
| Jan 3 |    150 |
| Jan 4 |    300 |

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

| date  | amount | running_total |
| ----- | -----: | ------------: |
| Jan 1 |    100 |           100 |
| Jan 2 |    200 |           300 |
| Jan 3 |    150 |           450 |
| Jan 4 |    300 |           750 |

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

| date  | amount | previous_amount |
| ----- | -----: | --------------: |
| Jan 1 |    100 |            NULL |
| Jan 2 |    200 |             100 |
| Jan 3 |    150 |             200 |
| Jan 4 |    300 |             150 |

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

| date  | amount | difference |
| ----- | -----: | ---------: |
| Jan 1 |    100 |       NULL |
| Jan 2 |    200 |        100 |
| Jan 3 |    150 |        -50 |
| Jan 4 |    300 |        150 |

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

| date  | amount | next_amount |
| ----- | -----: | ----------: |
| Jan 1 |    100 |         200 |
| Jan 2 |    200 |         150 |
| Jan 3 |    150 |         300 |
| Jan 4 |    300 |        NULL |

Think:

```text
LAG()  → look backward
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
John   IT   80000   80000
David  IT   90000   80000
Mike   IT   70000   80000
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
        │ John    80K       │
        │ David   90K       │
        │ Mike    70K       │
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

> **What is a window function?**

A strong answer is:

> **A window function performs a calculation across a set of related rows while preserving the individual rows in the result set. We define the window using `OVER()`, optionally using `PARTITION BY` to divide rows into groups and `ORDER BY` to define their order. Common window functions include `ROW_NUMBER`, `RANK`, `DENSE_RANK`, `SUM`, `AVG`, `LAG`, and `LEAD`.**

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

```sql
SELECT employee_name,
       COALESCE(manager_id, 0)
FROM employees;
```

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
1  | 100000 | 5
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
 │ Outbox Event  │
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

# 🔥 The Most Important Topics for Your Senior Java Interview

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

# 🎯 30 SQL Questions I Would Definitely Prepare for a Senior Interview

If you have limited preparation time, master these:

1. **Second highest salary**
2. **Nth highest salary**
3. **Top 3 salaries per department**
4. **Duplicate records**
5. **Delete duplicate records**
6. **Employees earning more than their manager**
7. **Employees earning above department average**
8. **Customers without orders**
9. **Departments without employees**
10. **Latest record per customer**
11. **First record per customer**
12. **Running total**
13. **Previous/next record using `LAG`/`LEAD`**
14. **`ROW_NUMBER` vs `RANK` vs `DENSE_RANK`**
15. **Find consecutive records**
16. **Gaps and islands**
17. **Recursive CTE**
18. **`JOIN` vs `EXISTS`**
19. **`UNION` vs `UNION ALL`**
20. **`WHERE` vs `HAVING`**
21. **`DELETE` vs `TRUNCATE` vs `DROP`**
22. **Index and composite index**
23. **Why an index isn't being used**
24. **How to read an execution plan**
25. **Transaction and ACID**
26. **Isolation levels**
27. **Deadlocks**
28. **Optimistic vs pessimistic locking**
29. **Pagination: OFFSET vs keyset**
30. **Database optimization for millions/billions of records**

For a **Senior Java Full Stack / Spring Boot interview**, SQL questions often become **scenario-based rather than syntax-based**. The interviewer may give you a slow query, concurrent update problem, millions of records, or a microservices transaction problem and ask you to design the solution.

If you master the **60 topics + 75 coding problems above**, you will have a strong SQL preparation base from **basic SQL through Senior/Lead-level database discussions**.
