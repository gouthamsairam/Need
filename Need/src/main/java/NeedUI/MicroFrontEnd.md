# Micro Frontends — Complete Beginner-to-Advanced Interview Guide

Think of **Micro Frontends (MFE)** as applying the **Microservices idea to the frontend**.

Instead of building one huge frontend application owned by one team, we split it into **smaller independently developed and deployed frontend applications**.

---

# 1. What is a Micro Frontend?

### Simple definition

> **Micro Frontend is an architectural approach where a large frontend application is divided into smaller, independently developed, tested, and deployed frontend applications.**

For example, imagine an e-commerce application:

```text
                    E-Commerce Application
                           |
          +----------------+----------------+
          |                |                |
       Product          Cart             Payment
       Frontend         Frontend          Frontend
          |                |                |
       Team A            Team B           Team C
          |                |                |
      Deployment        Deployment       Deployment
```

Each team can own its part of the UI.

---

# 2. Why did Micro Frontends come into existence?

Imagine a company has a huge application:

```text
                  ONE HUGE FRONTEND
                         |
       +-----------------+----------------+
       |                 |                |
     Login             Orders          Payments
       |                 |                |
       +-----------------+----------------+
                         |
                    500K+ LOC
```

Over time, problems appear:

* Huge codebase
* Long build times
* Difficult deployments
* Teams stepping on each other's code
* One team's bug can affect the entire application
* Difficult ownership
* Difficult scaling
* Difficult technology upgrades

This is sometimes called a **Frontend Monolith**.

Micro Frontends attempt to solve these organizational and technical problems.

---

# 3. Micro Frontend vs Microservices

This is a very common interview question.

| Microservices                         | Micro Frontends                             |
| ------------------------------------- | ------------------------------------------- |
| Backend architecture                  | Frontend architecture                       |
| Services split by business capability | UI split by business capability             |
| REST/gRPC/events                      | Components/modules/routes/events            |
| Independently deployed                | Independently deployed                      |
| Database may be independent           | UI/application ownership can be independent |
| Backend teams                         | Frontend teams                              |

Think:

```text
MICROSERVICES

                API Gateway
                     |
       +-------------+-------------+
       |             |             |
    Order Service  Payment       User
                   Service       Service


MICRO FRONTENDS

                Shell / Container
                     |
       +-------------+-------------+
       |             |             |
    Order MFE     Payment MFE    User MFE
```

Together:

```text
                    Browser
                       |
                Application Shell
                       |
       +---------------+---------------+
       |               |               |
    Order MFE       Payment MFE     Profile MFE
       |               |               |
       v               v               v
 Order APIs       Payment APIs     User APIs
```

---

# 4. Real-world analogy

Imagine a large shopping mall.

Instead of one company building and managing the entire mall:

```text
                 SHOPPING MALL
                      |
       +--------------+--------------+
       |              |              |
     Nike           Apple         Starbucks
       |              |              |
     Team A          Team B         Team C
```

Each store:

* has its own team
* manages its own products
* can renovate independently
* can operate independently

But customers see:

> **One shopping mall**

That's the Micro Frontend concept.

---

# 5. Main goal

The primary goal isn't simply "split JavaScript."

The real goal is:

> **Independent ownership and delivery of business capabilities while providing one consistent user experience.**

This distinction is important in interviews.

---

# 6. Core concepts

The major concepts are:

### 1. Host / Shell

The main application.

```text
                 Shell
                   |
       +-----------+-----------+
       |           |           |
     Home       Orders       Cart
```

The Shell usually handles:

* routing
* authentication
* layout
* navigation
* loading MFEs
* global configuration

---

### 2. Remote / Micro Frontend

An independently developed frontend.

Example:

```text
Orders MFE
Payment MFE
Profile MFE
```

---

### 3. Composition

Combining multiple MFEs into one application.

```text
                 Browser
                    |
                  Shell
                    |
       +------------+------------+
       |            |            |
    Header       Orders        Cart
```

---

### 4. Independent deployment

This is one of the most important characteristics.

Suppose:

```text
Order Team → v5.2
Payment Team → v3.7
Cart Team → v8.1
```

You can deploy:

```text
Order MFE → production
```

without deploying the entire frontend.

---

### 5. Business-domain ownership

A good MFE boundary usually follows business domains.

For example:

```text
E-commerce
   |
   +── Product
   +── Order
   +── Payment
   +── Customer
```

rather than:

```text
Frontend
   |
   +── Button MFE
   +── Table MFE
   +── Header MFE
```

The second approach often creates unnecessary complexity.

---

# 7. Typical Architecture

A common architecture looks like this:

```text
                        USER
                         |
                         v
                  +-------------+
                  |   Browser   |
                  +-------------+
                         |
                         v
                +------------------+
                | Application Shell|
                +------------------+
                    /    |     \
                   /     |      \
                  v      v       v
             Product    Cart   Payment
               MFE       MFE      MFE
                 |        |        |
                 v        v        v
             Product     Cart    Payment
               API        API      API
```

The Shell provides the common application experience.

---

# 8. How does it work internally?

Let's use **Webpack Module Federation**, one of the popular implementation mechanisms.

Suppose we have:

```text
HOST
  |
  +-- Product MFE
  +-- Cart MFE
  +-- Payment MFE
```

### Step 1 — Product team builds its application

Product team generates something like:

```text
remoteEntry.js
```

This exposes modules/components.

---

### Step 2 — Deploy the Product MFE

For example:

```text
CDN
 |
 +-- product/
      |
      +-- remoteEntry.js
      +-- chunks/
```

---

### Step 3 — Host knows where the remote lives

Conceptually:

```javascript
remotes: {
    product: "product@https://example.com/product/remoteEntry.js"
}
```

---

### Step 4 — User opens application

```text
Browser
   |
   v
Host
   |
   v
Load Product remote
   |
   v
remoteEntry.js
   |
   v
Product component
```

---

### Step 5 — Host renders it

The user sees:

```text
+-----------------------------------+
| Header                            |
+-----------------------------------+
|                                   |
|       Product Micro Frontend      |
|                                   |
+-----------------------------------+
```

The user doesn't necessarily know that different applications are being combined.

---

# 9. Module Federation Architecture

A simplified view:

```text
              HOST APPLICATION
                    |
          Module Federation
                    |
        +-----------+-----------+
        |           |           |
        v           v           v
    Product      Cart        Payment
     Remote      Remote       Remote
        |           |           |
        v           v           v
 remoteEntry    remoteEntry  remoteEntry
```

Webpack Module Federation allows applications to:

* expose modules
* consume remote modules
* share dependencies
* load modules dynamically

---

# 10. Example — React Micro Frontend

Suppose Product MFE exposes:

```javascript
// ProductApp.jsx

function ProductApp() {
    return (
        <div>
            <h2>Products</h2>
            <p>Product list goes here</p>
        </div>
    );
}

export default ProductApp;
```

Webpack configuration:

```javascript
const ModuleFederationPlugin =
    require("webpack").container.ModuleFederationPlugin;

module.exports = {
    plugins: [
        new ModuleFederationPlugin({
            name: "product",
            filename: "remoteEntry.js",

            exposes: {
                "./ProductApp": "./src/ProductApp"
            }
        })
    ]
};
```

The Product MFE is now exposing:

```text
product/ProductApp
```

---

# 11. Host application

Host configuration:

```javascript
new ModuleFederationPlugin({
    name: "host",

    remotes: {
        product:
          "product@https://cdn.example.com/product/remoteEntry.js"
    }
});
```

Then:

```javascript
import ProductApp from "product/ProductApp";

function App() {

    return (
        <div>
            <h1>My Application</h1>

            <ProductApp />
        </div>
    );
}
```

Conceptually:

```text
Host
 |
 +---- ProductApp
          |
          +---- Loaded from remote
```

---

# 12. Dynamic loading

Instead of statically loading everything:

```javascript
import ProductApp from "product/ProductApp";
```

you can lazy-load it:

```javascript
const ProductApp = React.lazy(
    () => import("product/ProductApp")
);
```

Then:

```javascript
<Suspense fallback={<div>Loading...</div>}>
    <ProductApp />
</Suspense>
```

This can improve initial loading.

---

# 13. Routing

Routing is extremely important in Micro Frontends.

For example:

```text
/orders
/products
/payments
/profile
```

The Shell can own top-level routing:

```text
                  Shell
                    |
        +-----------+-----------+
        |           |           |
     /products    /orders     /payment
        |           |           |
      MFE         MFE          MFE
```

Example:

```javascript
<Route
    path="/orders/*"
    element={<OrdersApp />}
/>

<Route
    path="/payment/*"
    element={<PaymentApp />}
/>
```

The Orders MFE can have internal routes:

```text
/orders
    |
    +-- /list
    +-- /details
    +-- /history
```

---

# 14. Communication between Micro Frontends

This is one of the most important architectural decisions.

Suppose:

```text
Product MFE
      |
      | Add product
      v
    Cart MFE
```

How do they communicate?

There are several approaches.

---

## Approach 1 — Custom Events

```javascript
window.dispatchEvent(
    new CustomEvent("productAdded", {
        detail: {
            productId: 101
        }
    })
);
```

Cart:

```javascript
window.addEventListener(
    "productAdded",
    event => {
        console.log(event.detail.productId);
    }
);
```

Architecture:

```text
Product MFE
     |
     | Custom Event
     v
  Browser
     |
     v
 Cart MFE
```

This reduces direct coupling.

---

# 15. Shared state

You can use:

* Redux
* Zustand
* Context
* browser storage
* event-based communication

But be careful.

Bad architecture:

```text
             GLOBAL REDUX
                  |
       +----------+----------+
       |          |          |
     MFE A      MFE B      MFE C
```

If everything depends on one global store, you can accidentally create:

> **a distributed monolith**

A better approach is:

```text
MFE A → local state
MFE B → local state
MFE C → local state

Global state → only truly global concerns
```

Examples of global state:

* authentication
* user identity
* locale
* theme
* feature flags

---

# 16. Communication using events

A useful architecture:

```text
              Event Bus
                  |
       +----------+----------+
       |          |          |
     MFE A      MFE B      MFE C
```

Example:

```javascript
window.dispatchEvent(
    new CustomEvent("orderCreated", {
        detail: {
            orderId: "ORD-1001"
        }
    })
);
```

Another MFE:

```javascript
window.addEventListener(
    "orderCreated",
    ({ detail }) => {
        console.log(detail.orderId);
    }
);
```

---

# 17. Sharing dependencies

Suppose every MFE downloads:

```text
React
React DOM
React Router
```

You may end up with:

```text
MFE A
 + React 18
 + React Router

MFE B
 + React 18
 + React Router

MFE C
 + React 18
 + React Router
```

This increases bundle size.

Module Federation can share dependencies.

Conceptually:

```javascript
shared: {
    react: {
        singleton: true
    },
    "react-dom": {
        singleton: true
    }
}
```

This tells the federation system that React should be shared.

---

# 18. What does singleton mean?

This is an interview favorite.

If multiple MFEs load React independently:

```text
MFE A → React instance A
MFE B → React instance B
```

Potential problems can occur.

With singleton:

```text
             React
               |
       +-------+-------+
       |       |       |
     MFE A   MFE B   MFE C
```

All use the same shared instance when compatible.

---

# 19. Deployment Architecture

A typical enterprise setup:

```text
                    Internet
                       |
                       v
                 CDN / CloudFront
                       |
                 Application Shell
                       |
       +---------------+---------------+
       |               |               |
       v               v               v
 Product CDN       Order CDN       Payment CDN
       |               |               |
 Product MFE       Order MFE       Payment MFE
```

Each team has its own CI/CD pipeline.

```text
Product Team
     |
     v
Git
     |
     v
Build
     |
     v
Test
     |
     v
Deploy
     |
     v
Product CDN
```

The same happens independently for Orders and Payments.

---

# 20. CI/CD

One of the biggest advantages.

Traditional:

```text
Developer A
Developer B
Developer C
      |
      v
 ONE BUILD
      |
      v
 ONE DEPLOYMENT
```

Micro Frontend:

```text
Team A → Build → Test → Deploy → MFE A

Team B → Build → Test → Deploy → MFE B

Team C → Build → Test → Deploy → MFE C
```

This allows teams to release independently.

---

# 21. Lifecycle of a Micro Frontend

A useful interview answer:

```text
Development
    ↓
Build
    ↓
Unit Tests
    ↓
Integration Tests
    ↓
Artifact Creation
    ↓
Deployment
    ↓
Registration / Discovery
    ↓
Host Loads Remote
    ↓
Remote Initialization
    ↓
Rendering
    ↓
User Interaction
    ↓
Unmount
```

---

# 22. Runtime lifecycle

When a user opens:

```text
https://example.com/orders
```

Conceptually:

```text
1. Browser requests Shell
          ↓
2. Shell loads
          ↓
3. Router determines /orders
          ↓
4. Orders MFE is identified
          ↓
5. remoteEntry.js loaded
          ↓
6. Required chunks loaded
          ↓
7. Shared dependencies resolved
          ↓
8. Orders MFE initialized
          ↓
9. Orders UI rendered
```

---

# 23. Micro Frontend integration approaches

There isn't only one way.

## 1. Build-time integration

```text
MFE
 ↓
Package
 ↓
Host installs package
 ↓
Build Host
```

Problem:

> Host must rebuild when MFE changes.

Therefore deployment independence is weaker.

---

## 2. Runtime integration

```text
Host
 |
 +---- Load MFE at runtime
```

Module Federation is a popular approach here.

---

## 3. Web Components

A MFE can expose:

```html
<product-app></product-app>
```

Host:

```html
<body>

    <header>Application</header>

    <product-app></product-app>

</body>
```

Web Components provide a framework-independent interface.

---

## 4. iframe

Very strong isolation:

```text
Host
 |
 +---- iframe
        |
        +---- MFE
```

But communication and UX can be harder.

---

# 24. iframe vs Module Federation

| Feature             | iframe            | Module Federation                  |
| ------------------- | ----------------- | ---------------------------------- |
| Isolation           | Very high         | Lower                              |
| Integration         | Easier isolation  | Better integration                 |
| Shared DOM          | No                | Yes                                |
| Shared dependencies | Difficult         | Supported                          |
| Communication       | postMessage       | Events/functions/shared mechanisms |
| UX integration      | Harder            | Easier                             |
| Performance         | Can have overhead | Usually better                     |
| Security boundary   | Stronger          | Weaker                             |

---

# 25. Micro Frontend vs Monolith

| Area                 | Monolith          | Micro Frontend    |
| -------------------- | ----------------- | ----------------- |
| Codebase             | One               | Multiple          |
| Deployment           | Usually together  | Independent       |
| Ownership            | Shared            | Team/domain based |
| Scaling              | Application level | Team/domain level |
| Complexity           | Lower initially   | Higher            |
| Governance           | Easier            | Harder            |
| Technology diversity | Lower             | Possible          |
| CI/CD                | Centralized       | Distributed       |

---

# 26. Micro Frontend vs SPA

This is another common interview question.

**SPA** describes how the application behaves from the user's perspective.

**Micro Frontend** describes how the frontend is architected and organized.

You can have:

```text
SPA + Micro Frontends
```

For example:

```text
One browser application
       |
       +-- Multiple MFEs
```

So they are **not alternatives**.

---

# 27. Micro Frontend vs Monorepo

They are also not opposites.

You can have:

```text
Monorepo
   |
   +-- Shell
   +-- Product MFE
   +-- Order MFE
   +-- Payment MFE
```

or:

```text
Separate repositories

repo-shell
repo-product
repo-order
repo-payment
```

A monorepo is a **source-code management strategy**.

Micro Frontend is an **application architecture**.

---

# 28. When should you use Micro Frontends?

Good candidate:

```text
Large enterprise
       |
       +-- 500+ developers
       |
       +-- Multiple business domains
       |
       +-- Independent release cycles
       |
       +-- Multiple teams
       |
       +-- Long-lived application
```

Example:

```text
Banking
 |
 +-- Accounts
 +-- Cards
 +-- Loans
 +-- Investments
 +-- Payments
```

Different teams can own each domain.

---

# 29. When should you NOT use Micro Frontends?

This is important.

Don't use MFE simply because it's trendy.

For example:

```text
Small application
    |
    +-- 4 developers
    +-- 10 pages
    +-- One release cycle
```

Micro Frontend could create unnecessary complexity.

You now have:

* multiple deployments
* runtime loading
* version compatibility
* distributed debugging
* cross-MFE communication
* infrastructure complexity

The architecture can become worse than a monolith.

---

# 30. Enterprise example

Imagine a banking portal:

```text
                      BANKING PORTAL
                            |
                    Application Shell
                            |
       +--------------------+--------------------+
       |                    |                    |
    Accounts              Cards               Loans
       |                    |                    |
      Team A               Team B              Team C
       |                    |                    |
       v                    v                    v
   Accounts MFE          Cards MFE           Loans MFE
```

The Shell owns:

```text
Authentication
Navigation
Header
Footer
Global error handling
Feature flags
```

Each domain owns:

```text
UI
Domain logic
API integration
Tests
Deployment
```

---

# 31. Domain-driven Micro Frontends

A strong architecture follows:

> **Business boundaries rather than technical boundaries.**

Good:

```text
Customer
Orders
Payments
Claims
Policies
```

Bad:

```text
Buttons
Tables
Forms
Modals
```

The second approach fragments the UI too much.

---

# 32. Backend-for-Frontend (BFF)

Micro Frontends often work well with BFF.

Architecture:

```text
                 Browser
                    |
                  Shell
                    |
       +------------+------------+
       |            |            |
    Orders MFE   Payment MFE  Profile MFE
       |            |            |
      BFF          BFF          BFF
       |            |            |
       v            v            v
   Services      Services      Services
```

A BFF provides APIs optimized for a specific frontend domain.

---

# 33. Security

Important enterprise concerns:

### Authentication

Usually handled centrally.

```text
Identity Provider
       |
       v
   Shell
       |
       +---- MFE A
       +---- MFE B
       +---- MFE C
```

Use:

* OAuth 2.0
* OpenID Connect
* secure token handling
* HTTPS
* CSP
* appropriate CORS configuration

Avoid allowing each MFE to invent its own authentication mechanism.

---

# 34. Token sharing

Avoid casually doing:

```javascript
localStorage.setItem(
    "accessToken",
    token
);
```

and allowing every MFE to manipulate it.

Instead, establish a controlled authentication contract.

For example:

```text
Shell
 |
 +-- Authentication service
 |
 +-- User information
 |
 +-- Authorized API mechanism
```

Security architecture must be decided centrally.

---

# 35. Error handling

Suppose:

```text
Shell
 |
 +-- Product MFE ✓
 +-- Order MFE  ✗
 +-- Payment MFE ✓
```

The entire application shouldn't necessarily crash.

Use boundaries:

```text
Shell
 |
 +-----------------------+
 |       Error Boundary  |
 |                       |
 |      Orders MFE       |
 |                       |
 +-----------------------+
```

Display:

```text
Orders are temporarily unavailable.
Please try again later.
```

while the rest of the application remains usable.

---

# 36. Versioning

Suppose:

```text
Shell expects Product API v2
Product MFE provides v3
```

Potential compatibility issue.

Define contracts:

```text
Shell ↔ MFE Contract
```

Use:

* semantic versioning
* compatibility rules
* contract testing
* backward compatibility
* automated validation

---

# 37. Contract testing

Suppose Shell expects:

```javascript
{
    userId: string,
    name: string
}
```

Product MFE suddenly changes it to:

```javascript
{
    id: number,
    username: string
}
```

Integration breaks.

Contract tests help detect this before production.

---

# 38. Performance challenges

Micro Frontends can actually make performance **worse** if poorly designed.

Example:

```text
MFE A → React 10 MB
MFE B → React 10 MB
MFE C → React 10 MB
```

Potential problems:

* duplicated dependencies
* multiple network requests
* JavaScript execution cost
* larger initial bundle
* runtime loading overhead

---

# 39. Performance optimization

### 1. Lazy loading

Load only when needed.

```text
User opens /orders
        |
        v
Load Orders MFE
```

Don't load everything at startup.

---

### 2. CDN

Deploy static assets to a CDN.

```text
Browser
   |
   v
CDN
   |
   +-- Shell
   +-- Orders
   +-- Product
```

---

### 3. Cache aggressively

Use hashed assets:

```text
main.a8f3c.js
```

Then configure appropriate caching.

---

### 4. Share common dependencies

For example:

```text
React
ReactDOM
```

---

### 5. Keep MFEs reasonably sized

Avoid creating 50 tiny MFEs.

---

# 40. Too many Micro Frontends

Bad:

```text
App
 |
 +-- Header MFE
 +-- Button MFE
 +-- Table MFE
 +-- Search MFE
 +-- Modal MFE
 +-- Form MFE
 +-- Product MFE
 +-- Cart MFE
```

This becomes difficult to manage.

Better:

```text
App
 |
 +-- Product MFE
 +-- Cart MFE
 +-- Checkout MFE
```

Reusable UI components should usually be shared libraries/design systems rather than MFEs.

---

# 41. Design System

Enterprise MFE architecture often has:

```text
                 Design System
                      |
       +--------------+--------------+
       |              |              |
    Product         Order          Payment
       MFE            MFE            MFE
```

The design system provides:

* Button
* Input
* Modal
* Table
* Typography
* Colors
* Accessibility patterns

This keeps UX consistent.

---

# 42. Shared component library vs Micro Frontend

Very common interview question.

### Component library

```text
<Button />
<Table />
<Modal />
```

Used at development/build time.

### Micro Frontend

```text
<ProductApplication />
<OrderApplication />
```

Usually represents a larger business capability and can be independently deployed.

---

# 43. Enterprise repository strategy

### Option 1 — Monorepo

```text
frontend/
 |
 +-- shell/
 +-- product/
 +-- orders/
 +-- payments/
 +-- shared-ui/
```

Tools can include:

* Nx
* Turborepo
* pnpm workspaces

Benefits:

* easier shared code
* unified tooling
* easier refactoring

---

### Option 2 — Polyrepo

```text
shell-repo
product-repo
orders-repo
payment-repo
```

Benefits:

* stronger team independence
* independent pipelines
* independent ownership

Cost:

* more infrastructure
* more governance

---

# 44. Testing strategy

You need multiple levels.

```text
                  Testing
                     |
       +-------------+-------------+
       |             |             |
     Unit         Integration     E2E
       |             |             |
   Components      MFE contract   User flow
```

Examples:

### Unit

Test:

```text
Product component
```

### Integration

Test:

```text
Shell + Product MFE
```

### E2E

Test:

```text
Login
 ↓
Product
 ↓
Cart
 ↓
Checkout
```

---

# 45. Observability

Distributed frontend applications need good observability.

Track:

* JavaScript errors
* MFE load failures
* network failures
* performance
* route changes
* user journeys

Conceptually:

```text
Browser
   |
   +-- Shell logs
   +-- Product logs
   +-- Order logs
   +-- Payment logs
          |
          v
   Observability Platform
```

Use correlation/request IDs where applicable so frontend activity can be connected to backend traces.

---

# 46. Deployment failure scenario

Suppose:

```text
Shell v10
Product MFE v5
Orders MFE v7
```

Orders v7 has a production bug.

Because the MFEs are independently deployed:

```text
Rollback Orders MFE
        ↓
Orders v6
```

without necessarily rolling back:

```text
Shell
Product
Payment
```

This is a major operational advantage.

---

# 47. Canary deployment

You can deploy:

```text
Orders MFE v8
```

to only:

```text
5% users
```

Then:

```text
5%
 ↓
20%
 ↓
50%
 ↓
100%
```

If errors increase:

```text
Rollback
```

This works well with CDN/version routing and enterprise deployment infrastructure.

---

# 48. Blue-Green deployment

```text
                 CDN
                  |
          +-------+-------+
          |               |
       Version A        Version B
       Orders v7        Orders v8
          |
       Current
```

Switch traffic when the new version is validated.

---

# 49. Feature flags

Feature flags are very useful.

```text
if (featureFlags.newCheckout) {
    load CheckoutV2;
} else {
    load CheckoutV1;
}
```

This allows controlled rollout.

---

# 50. Runtime configuration

Avoid hardcoding:

```javascript
https://prod.example.com/payment
```

Instead:

```javascript
const config = {
    paymentMfe:
        "https://cdn.example.com/payment/remoteEntry.js"
};
```

This can be environment-specific:

```text
DEV
QA
UAT
PROD
```

---

# 51. Dynamic remote discovery

A more advanced architecture:

```text
                Shell
                  |
                  v
            MFE Registry
                  |
       +----------+----------+
       |          |          |
    Product     Order     Payment
      URL         URL        URL
       |          |          |
       v          v          v
     CDN        CDN        CDN
```

The Shell can determine which MFE version to load.

This provides greater deployment flexibility, but adds operational complexity.

---

# 52. What if a Micro Frontend is down?

Very important scenario question.

Suppose:

```text
Product ✓
Orders ✗
Payment ✓
```

The Shell should:

1. detect loading failure
2. log the error
3. show fallback UI
4. allow retry
5. keep other MFEs functional

Example:

```text
+--------------------------------+
| Header                         |
+--------------------------------+
|                                |
| Orders unavailable             |
| [Retry]                        |
|                                |
+--------------------------------+
```

---

# 53. What if remoteEntry.js fails?

Potential reasons:

* CDN outage
* network problem
* bad deployment
* DNS problem
* incorrect URL

Handle it with:

```javascript
try {
    // Load remote
} catch (error) {
    // fallback
}
```

Production systems should also have:

* monitoring
* rollback
* fallback version
* health checks

---

# 54. Architecture decision: who owns what?

A strong enterprise model:

| Responsibility    |   Shell |                 MFE |
| ----------------- | ------: | ------------------: |
| Global navigation |       ✅ |                     |
| Authentication    |       ✅ |                     |
| Global layout     |       ✅ |                     |
| Business UI       |         |                   ✅ |
| Domain API calls  |         |                   ✅ |
| Domain state      |         |                   ✅ |
| Domain tests      |         |                   ✅ |
| Deployment        |         |                   ✅ |
| Global theme      |       ✅ |                     |
| Feature flags     | Usually | Domain-specific use |

The exact division varies by organization.

---

# 55. Important principle: Don't share everything

A common mistake is:

```text
Shell
 ↓
Everything shared globally
 ↓
Every MFE depends on Shell
```

This creates tight coupling.

Prefer:

```text
Shared:
- Authentication contract
- Design system
- Logging contract
- Common utilities

Local:
- Domain state
- Domain business logic
- Domain API logic
```

---

# 56. Micro Frontend maturity model

You can think of enterprise adoption in stages.

### Level 1

```text
Monolith
```

### Level 2

```text
Monolith + shared libraries
```

### Level 3

```text
Domain-based MFEs
```

### Level 4

```text
Independent deployments
```

### Level 5

```text
Runtime composition
+ observability
+ governance
+ automated contracts
+ progressive delivery
```

---

# 57. System design example

Suppose interviewer asks:

> Design an Amazon-like frontend using Micro Frontends.

You could propose:

```text
                         CDN
                          |
                          v
                   Application Shell
                          |
          +---------------+---------------+
          |               |               |
          v               v               v
      Product MFE     Cart MFE       Checkout MFE
          |               |               |
          v               v               v
      Product BFF      Cart BFF      Checkout BFF
          |               |               |
          v               v               v
      Microservices / APIs
```

Cross-cutting services:

```text
              +-------------------+
              | Authentication    |
              +-------------------+
              | Feature Flags     |
              +-------------------+
              | Observability     |
              +-------------------+
              | Design System     |
              +-------------------+
```

---

# 58. Interview answer: "How would you design MFE?"

A strong answer:

> "I would first identify business domains and assign ownership to teams. I would create an application shell responsible for global navigation, authentication, layout and routing. Each business domain would be an independently deployable MFE. For runtime composition, I could use Module Federation or another suitable composition mechanism. I would keep domain state inside each MFE and minimize cross-MFE communication. Shared dependencies such as React would be carefully managed, and a centralized design system would provide UI consistency. For production, I would use CDN-based deployment, CI/CD, observability, feature flags, contract testing, versioning and rollback mechanisms."

That's a good senior-level answer.

---

# 59. Most important interview question

### Q: What problem does Micro Frontend solve?

**Answer:**

> Micro Frontends primarily solve organizational and delivery problems in large frontend applications. They allow different teams to independently own, develop, test, and deploy business capabilities while presenting them as a unified application.

Don't answer:

> "It makes frontend smaller."

That's incomplete.

---

# 60. Beginner Interview Questions

## Q1. What is Micro Frontend?

**Answer:**

An architectural pattern that divides a large frontend application into smaller independently developed and deployed frontend applications.

---

## Q2. Why do we need Micro Frontends?

**Answer:**

To improve:

* team autonomy
* independent deployments
* scalability of development
* ownership
* release velocity

especially in large enterprise applications.

---

## Q3. Is Micro Frontend a framework?

**Answer:**

No.

It's an **architectural approach/pattern**.

Technologies such as Module Federation, Web Components, iframes, and various composition approaches can be used to implement it.

---

## Q4. What is a Shell?

**Answer:**

The host/container application that provides the common application experience and composes the Micro Frontends.

---

## Q5. What is a Remote?

**Answer:**

An independently developed frontend application that exposes functionality consumed by the host.

---

## Q6. What is Module Federation?

**Answer:**

A mechanism that allows separately built applications to expose and consume modules at runtime.

---

## Q7. Can different teams use different frameworks?

Technically, yes.

For example:

```text
Shell → React
Product → React
Legacy Orders → Angular
Reporting → Vue
```

But don't choose different frameworks just because you can.

It increases:

* bundle size
* operational complexity
* developer learning cost
* debugging complexity

---

# 61. Intermediate Interview Questions

## Q8. How do Micro Frontends communicate?

Possible mechanisms:

```text
Custom Events
Shared APIs
URL / Routing
Shared State
Event Bus
postMessage
```

Prefer loosely coupled communication.

---

## Q9. Should we use one global Redux store?

Usually, no.

Use global state only for truly global concerns.

Keep business/domain state local whenever possible.

---

## Q10. How do you share React?

With a suitable module sharing mechanism, for example:

```javascript
shared: {
    react: {
        singleton: true
    },
    "react-dom": {
        singleton: true
    }
}
```

Also ensure compatible versions.

---

## Q11. How do you handle routing?

Usually:

```text
Shell
 ↓
Top-level routes

MFE
 ↓
Domain-specific routes
```

For example:

```text
/orders
   |
   +-- /list
   +-- /details
```

---

## Q12. How do you handle authentication?

Centralize authentication responsibilities in the Shell/platform layer and expose a controlled contract to MFEs.

Avoid implementing independent authentication logic in every MFE.

---

## Q13. What happens if one MFE fails?

Use:

* error boundaries
* retry
* fallback UI
* monitoring
* rollback

and keep unaffected MFEs operational.

---

# 62. Advanced Interview Questions

## Q14. What is the biggest disadvantage of Micro Frontends?

There isn't one universal answer, but a major concern is:

> **Distributed frontend complexity.**

You trade monolith complexity for:

```text
Runtime composition
+
Version management
+
Cross-team contracts
+
Distributed debugging
+
Deployment coordination
+
Performance challenges
```

---

## Q15. How do you prevent distributed monoliths?

Keep boundaries strong.

Avoid:

```text
MFE A
 ↓
MFE B
 ↓
MFE C
 ↓
Shell
```

where everything depends on everything else.

Prefer:

```text
        Shell
       / |  \
      /  |   \
    MFE MFE  MFE

Minimal communication
```

---

# 63. Scenario-Based Interview Questions

## Scenario 1

> Product team deploys a new version and suddenly the Shell breaks. What could be wrong?

Possible reasons:

* incompatible contract
* incompatible shared dependency
* remote configuration problem
* breaking exposed module API
* incorrect deployment artifact
* version mismatch

Solutions:

```text
Contract testing
Semantic versioning
Backward compatibility
Automated integration testing
Canary deployment
Rollback
```

---

# 64. Scenario 2

> Three MFEs all load React separately and page performance is poor. What would you do?

Answer:

1. Analyze bundle sizes.
2. Identify duplicated dependencies.
3. Share compatible common dependencies.
4. Lazy-load MFEs.
5. Use CDN caching.
6. Preload only critical resources.
7. Measure Web Vitals.
8. Avoid loading unnecessary MFEs during startup.

---

# 65. Scenario 3

> Payment MFE is unavailable but the rest of the application works. What should happen?

Answer:

```text
Shell
 |
 +-- Product ✓
 +-- Cart ✓
 +-- Payment ✗
             |
             v
       Error Boundary
             |
             v
     Payment unavailable
          [Retry]
```

Don't allow the entire application to crash.

---

# 66. Scenario 4

> Two MFEs need to know when an order is created. How would you implement it?

Prefer an event contract:

```javascript
window.dispatchEvent(
    new CustomEvent("orderCreated", {
        detail: {
            orderId: "123"
        }
    })
);
```

Consumers subscribe to:

```text
orderCreated
```

rather than directly calling another MFE.

---

# 67. Scenario 5

> Your organization has only 8 frontend developers. Would you recommend Micro Frontends?

Likely **no**, unless there is a strong business/organizational reason.

I'd first consider:

```text
Modular Monolith
+
Good component architecture
+
Clear domain boundaries
+
CI/CD
```

MFE becomes more attractive as independent team ownership and release requirements grow.

---

# 68. Scenario 6

> Can one Micro Frontend be deployed without deploying the Shell?

**Yes**, if the architecture supports independent runtime deployment.

For example:

```text
Shell v10
Product v5
Orders v7
```

Deploy:

```text
Orders v8
```

without rebuilding Shell, assuming compatibility contracts are maintained.

---

# 69. Scenario 7

> Shell and MFE use different React versions. Is that possible?

Potentially yes, depending on the integration strategy.

But if you're sharing React as a singleton, incompatible versions can cause problems.

In general:

> Standardize versions where practical.

---

# 70. Scenario 8

> How do you decide Micro Frontend boundaries?

Use:

```text
Business capability
+
Team ownership
+
Independent deployment requirement
+
Domain boundaries
```

Not:

```text
Number of components
```

---

# 71. Common mistakes

### Mistake 1

Making every component a Micro Frontend.

❌

### Mistake 2

Sharing one giant global state.

❌

### Mistake 3

Allowing MFEs to directly depend on one another.

❌

### Mistake 4

Ignoring performance.

❌

### Mistake 5

Using different frameworks without justification.

❌

### Mistake 6

No versioning strategy.

❌

### Mistake 7

No fallback if a remote fails.

❌

### Mistake 8

No observability.

❌

### Mistake 9

No ownership model.

❌

### Mistake 10

Using MFE for a small application.

❌

---

# 72. Best Practices

Remember these **10 rules**:

```text
1. Split by business domain
2. Give clear ownership
3. Keep MFEs independently deployable
4. Minimize cross-MFE communication
5. Keep domain state local
6. Share only what is necessary
7. Standardize common dependencies
8. Use a shared design system
9. Add strong observability
10. Design for failure
```

---

# 73. Architecture I would recommend for an enterprise

```text
                           Users
                             |
                             v
                          CDN/WAF
                             |
                             v
                    Application Shell
                             |
       +---------------------+---------------------+
       |                     |                     |
       v                     v                     v
  Product MFE           Order MFE            Payment MFE
       |                     |                     |
       v                     v                     v
   Product BFF           Order BFF            Payment BFF
       |                     |                     |
       +---------------------+---------------------+
                             |
                        Backend APIs
                             |
                     Microservices Layer
```

Cross-cutting:

```text
        +----------------------------+
        | Authentication / Identity  |
        +----------------------------+
        | Feature Flags              |
        +----------------------------+
        | Design System              |
        +----------------------------+
        | Observability              |
        +----------------------------+
        | CI/CD                      |
        +----------------------------+
```

---

# 74. Micro Frontend mental model

Remember this:

```text
MICROSERVICES
     ↓
Split backend by business domain

MICRO FRONTENDS
     ↓
Split frontend by business domain

APPLICATION SHELL
     ↓
Compose everything

DESIGN SYSTEM
     ↓
Keep UI consistent

EVENTS / CONTRACTS
     ↓
Enable communication

CI/CD
     ↓
Independent deployment

CDN
     ↓
Fast delivery

OBSERVABILITY
     ↓
Detect failures
```

---

# 75. Quick comparison

| Technology        | Main purpose                          |
| ----------------- | ------------------------------------- |
| React             | UI library                            |
| Angular           | Frontend framework                    |
| SPA               | Application behavior/navigation model |
| Monorepo          | Repository strategy                   |
| Micro Frontend    | Frontend architecture                 |
| Module Federation | Runtime module composition mechanism  |
| Web Components    | Standard browser component model      |
| iframe            | Strong application isolation          |
| Design System     | Consistent UI                         |
| BFF               | Backend API optimized for frontend    |
| Microservices     | Backend architecture                  |

---

# 76. 30-second interview answer

If the interviewer asks:

> **"Explain Micro Frontend."**

Say:

> "Micro Frontend is an architectural pattern where a large frontend application is divided into smaller business-domain applications that can be independently developed, tested, and deployed by different teams. Typically, we have an application shell responsible for common concerns such as navigation, authentication and layout, while individual MFEs own specific domains such as Orders, Payments or Products. We can compose them at runtime using technologies such as Module Federation. We keep domain state local, minimize cross-MFE communication, share common dependencies carefully, and use CI/CD, CDN, observability, contract testing and rollback mechanisms to make the architecture production-ready."

---

# 77. 2-minute senior-level answer

> "When designing a Micro Frontend architecture, I start with business boundaries and team ownership rather than technical components. For example, in an e-commerce application, Product, Cart, Order and Checkout could be separate MFEs. An application shell handles global navigation, authentication, layout, routing and cross-cutting concerns. Each MFE owns its domain UI, state and API integration and has its own CI/CD pipeline.
>
> For runtime composition, I could use Module Federation, although the choice depends on requirements. I would carefully manage shared dependencies such as React to avoid duplicate downloads and version conflicts. Communication between MFEs should be minimized and preferably use well-defined contracts or events instead of direct dependencies.
>
> From a production perspective, I would use CDN delivery, lazy loading, caching, feature flags, contract testing, error boundaries, centralized observability and progressive deployments. Each MFE should be independently rollbackable. The main trade-off is that Micro Frontends improve team autonomy and independent delivery but introduce distributed-system-like complexity into the frontend, so I would only use them when the organizational and business benefits justify that complexity."

---

# 78. Quick Revision Cheat Sheet

### Definition

**Micro Frontend = Microservices principles applied to frontend architecture.**

### Main purpose

```text
Independent teams
       +
Independent development
       +
Independent deployment
       +
Unified UX
```

### Key components

```text
Shell
Remote/MFE
Router
Composition mechanism
Shared dependencies
Communication contracts
CI/CD
CDN
Observability
```

### Popular implementation mechanisms

```text
Module Federation
Web Components
iframes
Build-time packages
Server-side composition
```

### Communication

```text
Events
APIs
URL
Shared state — carefully
postMessage — iframe scenarios
```

### Best boundary

```text
Business domain
```

### Avoid

```text
Too many MFEs
Huge global state
Direct MFE dependencies
Duplicated libraries
No versioning
No error handling
No observability
```

### Biggest benefit

> **Independent team ownership and deployment.**

### Biggest risk

> **Distributed frontend complexity.**

---

# 79. The most important points to remember for interviews

If you remember only **8 things**, remember these:

```text
                 MICRO FRONTEND
                       |
       +---------------+---------------+
       |               |               |
    Business        Independent      Team
    Boundaries      Deployment       Ownership
       |               |               |
       +---------------+---------------+
                       |
                    Shell
                       |
          +------------+------------+
          |            |            |
        MFE A        MFE B        MFE C
          |            |            |
       Local         Local        Local
       State         State        State
          \            |            /
           \           |           /
             Contracts / Events
                       |
                 Shared Design
                    System
```

**The golden interview statement:**

> **"Micro Frontends are not primarily about splitting a frontend into smaller files. They are about creating independently owned and independently deployable business capabilities while maintaining a unified user experience."**

That is the key idea that separates a **senior-level Micro Frontend answer** from a basic definition.
