## 1. What Is React.js?

**React.js**, commonly called **React**, is an open-source JavaScript library for building user interfaces. It is maintained by Meta and a broad community of contributors. React is especially useful for interfaces that change in response to user actions or incoming data, such as dashboards, online stores, social applications, and single-page applications.

React encourages developers to build a UI from **components**: small, reusable pieces that describe what should appear on screen. A component might represent a navigation bar, a search box, a product card, or an entire page. Components can be composed into larger structures, which makes complex interfaces easier to organize and maintain.

For example, a shopping page could be divided into:

```text
App
├── Header
├── SearchBar
├── ProductList
│ └── ProductCard
└── ShoppingCart
```

Each component can receive data, display it, and respond to user interaction. When that data changes, React updates the interface to reflect the new state.

### A simple example

```jsx
function WelcomeMessage({ name }) {
 return <h1>Welcome, {name}</h1>;
}

function App() {
 return <WelcomeMessagename="Jordan" />;
}
```

`WelcomeMessage` is a component. The `name` value is passed in as a **prop**, and the component returns JSX describing the heading to display.

## 2. Is React a Library or a Framework?

React is primarily a **UI library**, not a complete application framework. Its main responsibility is rendering and updating user interfaces. It does not prescribe a single built-in solution for every concern in an application.

A React application commonly uses additional tools:

| Concern | Common choices |
|---|---|
| UI rendering | React |
| Routing | React Router or a framework’s routing system |
| Server rendering and application structure | Next.js or another React framework |
| Shared client state | React Context, Redux Toolkit, Zustand |
| Server-state and data fetching | TanStack Query, framework loaders, or `fetch` |
| Styling | CSS Modules, plain CSS, or a component styling solution |
| Testing | React Testing Library and a test runner |

A framework may combine React with routing, server rendering, data loading, and build conventions. React itself remains focused on the UI programming model.

**Interview summary:** React is a library for building component-based user interfaces. Applications often combine it with other libraries or a framework to handle routing, data fetching, server rendering, and broader application structure.

## 3. Core React Concepts

### Components

A component is a reusable function or class that describes part of an interface. Modern React code primarily uses function components.

```jsx
function Notification({ message }) {
 return <p className="notification">{message}</p>;
}
```

Components help divide a large interface into units with clear responsibilities. A notification panel, for example, might contain separate components for the panel, each notification, and the empty state.

### JSX

**JSX** is a syntax extension that lets developers write markup-like expressions in JavaScript. It is not HTML embedded directly in JavaScript; build tools transform it into JavaScript calls that create React elements.

```jsx
const heading = <h1>Account overview</h1>;
```

JavaScript expressions can be included inside braces:

```jsx
function Greeting({ name }) {
 return <h1>Hello, {name}</h1>;
}
```

JSX makes component output easier to read, while still allowing ordinary JavaScript expressions and logic around it.

### Props and state

**Props** are inputs passed from a parent component to a child. A component should treat its props as read-only.

**State** is data managed by a component that can change over time. When state changes, React schedules a re-render so the component can describe the updated UI.

```jsx
import { useState } from "react";

function Counter() {
 const [count, setCount] = useState(0);

 return (
 <button onClick={() => setCount(current => current + 1)}>
 Clicked {count} times
 </button>
 );
}
```

Here, `count` is state. Clicking the button requests a state update, and React renders the button with the new value.

### Hooks

Hooks let function components use React features such as state, context, and effects. Common hooks include:

- `useState` for component state.
- `useEffect` for synchronizing with external systems, such as browser APIs or subscriptions.
- `useContext` for reading a value provided by a context.
- `useReducer` for state transitions that benefit from a reducer function.

Hooks must be called at the top level of a component or custom hook, not conditionally inside loops or branches. React relies on a consistent order of hook calls to associate each hook with its stored data.

## 4. How React Works Internally

A useful high-level model is: **describe the UI, calculate what should change, then apply the changes**.

1.**The application creates a component tree.** React components return React elements, which are descriptions of the desired UI. JSX is compiled into calls that create these element descriptions.
2.**An event or update occurs.** Examples include a user typing, a button click, a network response, or a parent passing new props. A state setter such as `setCount` schedules an update; it does not directly modify the DOM.
3.**React renders the affected components.** React calls the relevant component functions to calculate the next descriptions of the UI. This is the render phase. Rendering should be free of side effects because React may perform render work more than once.
4.**React reconciles the new output with the previous output.** Reconciliation is the process of comparing the new React element tree with the previous one and determining what needs to change. React uses element types, positions, and keys to reason about component identity. Stable keys are important when rendering lists because they help React match items correctly as items are added, removed, or reordered.
5.**React commits the necessary changes.** In the commit phase, React applies the required updates to the host environment, usually the browser DOM. It may update text, attributes, event handlers, or insert and remove elements. It does not normally replace the entire page for every state update.
6.**The browser paints the result.** After DOM updates, the browser performs its rendering work and displays the updated pixels.

React’s internal **Fiber** architecture represents rendering work as units that can be scheduled and prioritized. This supports React’s ability to manage rendering work efficiently. In concurrent rendering scenarios, React may pause or abandon render work before it commits it. The commit phase applies the selected result; users should not rely on a render attempt itself having completed or having run exactly once.

The phrase **Virtual DOM** is often used as a simple way to describe React’s in-memory UI descriptions and comparison process. It is a useful introduction, but it is not a guarantee that React always performs a particular “diff algorithm” or that every update results in the smallest possible number of DOM operations. The important idea is that components describe the desired UI and React manages applying updates.

## 5. Real-World Examples

### Search results

A search component stores the user’s query in state. When the query changes, React renders the updated input and result state. The application may also request matching results from a server, then render loading, success, or error UI based on the response.

```text
User types "claim"
→ query state updates
→ React renders the new input value
→ application requests matching claims
→ results state updates
→ React renders the result list
```

### Shopping cart

The cart can display a count in the header and a list of selected products. When a user adds an item, cart state changes. Components that depend on that state can render the new count and totals.

### Operations dashboard

A dashboard might show notification counts, batch status, and recent failures. When fresh data arrives, React can update the relevant parts of the interface without requiring a full-page reload. The application still needs to decide how often to fetch data and how to handle stale or failed requests.

### Form validation

A registration form can hold field values and validation messages in state. As the user changes a field or submits the form, React renders the corresponding feedback. Validation rules and server-side validation remain application responsibilities; React provides the UI mechanism.

## 6. Advantages of React

-**Reusable components:** Shared UI patterns can be implemented once and reused across screens.
-**Declarative rendering:** Developers describe what the UI should look like for the current data, rather than manually issuing DOM updates for every change.
-**Local reasoning:** Component boundaries can make large interfaces easier to understand, test, and maintain.
-**Ecosystem:** React has extensive community resources, libraries, and tooling.
-**Flexible integration:** React can be used for a full application, embedded in part of an existing page, or paired with a framework.
-**Efficient UI updates:** React manages updates to the rendered interface when props or state change, helping avoid repetitive manual DOM manipulation.
-**Testability:** Components can be tested by rendering them and checking visible behavior and interactions.

## 7. Disadvantages and Trade-Offs

-**More decisions for the application team:** React alone does not define routing, server-state management, or a complete project structure. Teams must select tools and conventions.
-**Additional learning concepts:** JSX, hooks, rendering behavior, effects, and state ownership can take time to learn well.
-**State can become difficult to manage:** Poorly chosen state ownership or excessive shared state can make updates and data flow hard to follow.
-**Effects are easy to misuse:** Treating effects as a general place for application logic can cause duplicate requests, stale values, or complicated synchronization.
-**Performance is not automatic:** Unnecessary state updates, expensive rendering, or poorly structured lists can still make an application slow. Optimization should be based on measurement.
-**Server rendering adds complexity:** Frameworks can improve initial rendering and search-engine visibility, but introduce server/client boundaries, hydration concerns, and additional deployment considerations.
-**Fast-moving ecosystem:** Libraries and recommended practices change, so teams must choose dependencies carefully and maintain them.

## 8. When Is React a Good Choice?

React is a strong choice when an application has a dynamic interface, reusable UI patterns, interactive forms, frequently changing data, or a team that benefits from a component-based architecture. A static page with little interaction may not need React at all. The choice should depend on the product’s requirements, team experience, deployment model, and expected maintenance needs.

**Final interview answer:** React is a JavaScript UI library based on reusable components. Components receive props and manage state; when inputs or state change, React renders a new UI description, reconciles it with the previous one, and commits the necessary updates to the browser DOM. React’s strengths include reusable components, declarative updates, and a large ecosystem. Its trade-offs include choosing additional application tools, learning its rendering and state model, and managing complexity as the application grows.

Updated todo list

## 2. Why Do We Use React?

React is used to build interactive user interfaces whose content changes as users interact with them or as application data changes. It gives developers a component-based, declarative way to describe the interface and manages updating the browser DOM when the underlying data changes.

A traditional page may require manually finding DOM elements and updating them whenever something changes. For example, adding a product to a shopping cart might mean updating the cart count, subtotal, and cart panel separately. With React, the application updates its state to represent the new cart, and React renders the affected UI from that state.

```text
User action
 ↓
Application state changes
 ↓
React renders the updated UI description
 ↓
React applies the necessary DOM updates
```

React is useful when an interface has reusable patterns, interactive workflows, or data that changes frequently. It is not automatically the right choice for every website: a simple static page may not need a JavaScript UI library.

### 2.1 Main Reasons to Use React

#### 1. Reusable Components

React lets developers package a visual element and its behavior into a component. The same component can be used in multiple places while receiving different data through props.

For example, a notification application may have the same alert style in an inbox, a dashboard, and a confirmation dialog:

```jsx
function Alert({ type, message }) {
 return (
 <div className={`alert alert-${type}`} role="status">
 {message}
 </div>
 );
}

function Dashboard() {
 return <Alerttype="warning"message="Three deliveries need attention." />;
}
```

Reusable components reduce duplicated markup and make visual changes easier to apply consistently. If the application changes its alert styling, the shared component can be updated instead of editing separate copies on several screens.

Reusability is a design benefit, not an automatic result of using React. Components still need useful boundaries: a component that tries to handle unrelated screens and business rules may become harder to reuse than duplicated, focused components.

#### 2. Declarative UI

React uses a declarative model: developers describe what the UI should look like for the current data, rather than manually issuing DOM operations for each change.

```jsx
function CartCount({ items }) {
 return <span>Items in cart: {items.length}</span>;
}
```

If the `items` prop changes, React renders the count from the new value. The component does not need to manually find a `<span>` and replace its text.

This is helpful in complex interfaces because the UI can be understood as a function of the current props and state:

```text
UI = render(current props, current state)
```

It also encourages a single source of truth. For example, if a cart’s item list is the authoritative state, the item count and subtotal can be derived from that list rather than maintained as separate values that may drift out of sync.

#### 3. Component-Based Structure

A large application can be split into components with clear responsibilities. A claims dashboard, for example, might contain:

```text
ClaimsPage
├── ClaimsSummary
├── ClaimsFilter
├── ClaimsTable
│ └── ClaimRow
└── PaginationControls
```

This makes it easier to reason about each part, test it, and have different developers work on separate areas. Components can also be composed: smaller components are combined to create larger sections and pages.

#### 4. Efficient, Managed UI Updates

React manages updates when props or state change. Instead of requiring developers to manually update every affected DOM element, React compares the new UI description with the previous one and commits the changes needed for the browser interface.

This is useful in applications such as live dashboards, where data arrives periodically and only some displayed values may change. It does not mean every React application is automatically fast. Expensive rendering, excessive state updates, and large lists can still cause performance problems. Developers should measure actual performance and optimize where the evidence points.

#### 5. Hooks for State and External Synchronization

Hooks let function components use state and other React features without writing class components. For example, `useState` manages local UI state:

```jsx
import { useState } from "react";

function FilterPanel() {
 const [isOpen, setIsOpen] = useState(false);

 return (
 <section>
 <button onClick={() => setIsOpen(open => !open)}>
 Filters
 </button>
 {isOpen && <div>Filter options appear here.</div>}
 </section>
 );
}
```

Hooks help encapsulate reusable behavior in custom hooks, such as sharing logic for a form or browser subscription. Effects such as `useEffect` are intended to synchronize with external systems, such as setting up a subscription or interacting with a browser API; they should not be used as a general-purpose place for every calculation or state update.

#### 6. Ecosystem and Developer Tooling

React has a large ecosystem, including tools for routing, forms, testing, data fetching, and server rendering. Browser developer tools can help inspect component trees and investigate rendering behavior. This can shorten development and debugging time, particularly in applications that have many interactive screens.

The ecosystem is also a trade-off: teams must choose libraries and keep them maintained. React alone does not dictate one solution for routing, global state, or data fetching.

#### 7. Support for Different Application Sizes

React can be introduced into part of an existing page or used as the UI foundation for a larger application. A team can start with a single interactive widget and expand as requirements grow. Frameworks built around React can add conventions and features for routing, server rendering, or data loading when an application needs them.

### 2.2 How React Updates the UI Internally

Understanding React’s update process explains why its programming model is useful:

1.**An update is triggered.** A user clicks a button, types into an input, or new data arrives. A state setter such as `setIsOpen(true)` schedules an update; it does not directly change the DOM.
2.**React renders components.** React calls the relevant component functions to calculate what the UI should look like with the new props and state. This calculation is the render phase. Render logic should be free of side effects because React may repeat or interrupt render work.
3.**React reconciles the result.** React compares the new element descriptions with the previous ones. Component type, position in the tree, and keys help React determine which elements represent the same items. In a list, stable keys help preserve the correct identity when items are inserted, removed, or reordered.
4.**React commits changes.** In the commit phase, React applies changes to the browser DOM, such as changing text, updating attributes, or adding and removing elements. The browser then paints the updated result.

React’s Fiber architecture helps represent and schedule rendering work. In concurrent rendering scenarios, React can pause or discard render work before committing it. The key practical distinction is that calculating a possible UI is separate from applying that UI to the DOM.

### 2.3 Real-World Examples

**E-commerce product catalog:** A product card can be reused for each item, receiving the product name, price, image, and availability as props. Filters and sorting update state, and React renders the resulting product list. The shopping cart can display an updated count and total from cart data.

**Notification or operations dashboard:** A shared alert component can display success, warning, and error messages. When a batch status changes from “Processing” to “Complete,” the application updates its data and renders the new status without manually replacing page elements.

**Multi-step application form:** Each step can be represented by a component. State holds the user’s answers, while validation feedback is rendered next to the relevant fields. Components can be reused for common inputs and error messages. React does not itself define the validation rules or guarantee that data is valid on the server; those responsibilities belong to the application and backend.

**Admin search screen:** A search box, status filter, results table, and pagination controls can each be separate components. Their state and data determine what the user sees. This is easier to extend than manually coordinating many DOM changes as each filter or page selection changes.

### 2.4 Advantages of React

-**Less duplicated UI code:** Shared components support consistent design and behavior.
-**Clearer update model:** UI output follows props and state, rather than a collection of scattered manual DOM operations.
-**Composition:** Small components can be combined into larger features and pages.
-**Reusable behavior:** Hooks can package stateful behavior for use in multiple components.
-**Large ecosystem:** Teams can choose additional tools to fit their application needs.
-**Incremental adoption:** React can be used for a single page area or a broader application.
-**Testable UI units:** Components can be tested for visible output and user interactions.

### 2.5 Disadvantages and Trade-Offs

-**React is not a complete framework:** Teams may need to choose and integrate routing, data-fetching, form, and state-management tools.
-**There is a learning curve:** JSX, hooks, state ownership, rendering, and effects introduce concepts that developers must understand.
-**State can become difficult to manage:** Poorly planned data ownership can lead to duplicated state, unnecessary updates, or difficult component relationships.
-**Performance requires attention:** React manages UI updates, but it cannot prevent inefficient application logic or expensive rendering by itself.
-**Server rendering can add complexity:** Frameworks can improve initial rendering and search visibility, but bring server/client boundaries and hydration considerations.
-**The ecosystem changes:** Dependencies need to be selected and maintained thoughtfully.
-**Accessibility is not automatic:** Components still need appropriate semantics, keyboard support, focus management, and accessible labels.

### 2.6 Follow-Up: What Is React’s Biggest Advantage?

A strong answer is **the component-based declarative model**. Reusability is a major benefit, but the deeper advantage is that components let a team express an interface in manageable pieces and have React update those pieces as data changes.

For example, a shared `Button` component can give an application a consistent style and interaction pattern. But if every screen duplicates the same button markup, React alone has not delivered that advantage. The team must design components around real shared responsibilities.

**Interview summary:**

> We use React to build interactive interfaces from reusable components. Its declarative model lets us describe the UI from current props and state, while React reconciles changes and commits the necessary DOM updates. This supports maintainable, data-driven applications, but React does not guarantee good performance or provide every application feature out of the box. Teams still need to make sound decisions about state, accessibility, architecture, and supporting libraries.
Updated todo list

## 2. JSX

### 3. What Is JSX?

**JSX** stands for **JavaScript XML**. It is a syntax extension commonly used with React that lets developers write markup-like descriptions of user interfaces inside JavaScript files.

```jsx
const element = <h1>Hello, world!</h1>;
```

This looks similar to HTML, but JSX is not HTML and browsers do not understand it directly. A build tool such as Babel, TypeScript, or a framework’s compiler transforms JSX into regular JavaScript before the application runs.

JSX is optional in React, but it is widely used because it keeps the description of a component’s UI close to the component’s logic. Without JSX, developers can create React elements by calling JavaScript functions directly. That approach is valid, but can become difficult to read when the UI contains nested elements and dynamic content.

#### JSX and JavaScript

Curly braces let you insert a JavaScript **expression** into JSX:

```jsx
function Greeting({ name }) {
 const message = `Hello, ${name}`;

 return <h1>{message}</h1>;
}
```

The expression inside `{}` is evaluated by JavaScript. Its result becomes part of the rendered output. JSX expressions can include variables, function calls, arithmetic, and conditional expressions, but not statements such as `if` or `for` directly.

For example, this works:

```jsx
<p>Total: {price * quantity}</p>
```

But an `if` statement cannot be placed directly inside JSX braces. Use a conditional expression or calculate the value before returning JSX:

```jsx
function StatusMessage({ isComplete }) {
 return (
 <p>
 {isComplete ? "Completed" : "In progress"}
 </p>
 );
}
```

### How JSX Is Transformed

Conceptually, older JSX transforms converted this:

```jsx
const element = <h1 className="title">Hello</h1>;
```

into something like:

```js
const element = React.createElement(
"h1",
 { className: "title" },
"Hello"
);
```

Modern projects commonly use the **automatic JSX runtime**. Build tooling may generate calls to functions such as `jsx` or `jsxs` from `react/jsx-runtime`, so developers usually do not need to write `React.createElement` or import React solely to use JSX.

The exact generated code depends on the compiler and configuration. The important idea is that JSX becomes JavaScript that creates **React element descriptions**. Those descriptions are not themselves DOM nodes. React uses them as input when rendering and reconciling the interface, then commits the needed changes to the browser DOM.

```text
JSX source
 ↓
Babel / TypeScript / framework compiler
 ↓
JavaScript that creates React elements
 ↓
React render and reconciliation
 ↓
DOM updates in the browser
```

### Follow-Up: Can a Browser Directly Understand JSX?

**No.** JSX must be transformed into JavaScript by a compiler or build tool before the browser can execute it. Some development environments perform this transformation automatically, which can make it seem as though the browser accepts JSX directly.

The browser ultimately runs the generated JavaScript, not the original JSX syntax.

## 4. Rules and Common Patterns in JSX

### 1. Return One Root Element

A component’s returned JSX must have one root element. Multiple sibling elements can be grouped in a wrapper or a **Fragment**.

```jsx
function UserDetails() {
 return (
 <>
 <h2>Jordan Lee</h2>
 <p>Claims administrator</p>
 </>
 );
}
```

A Fragment groups elements without adding an extra DOM element. This is useful when an extra `<div>` would interfere with layout or semantic markup.

### 2. Close Every Tag

JSX requires elements to be properly closed. This includes elements that are usually written without a closing tag in HTML.

```jsx
<img src="/profile.png"alt="Profile" />
<input type="text" />
```

### 3. Use `className` for CSS Classes

In JSX, use `className` rather than `class`:

```jsx
<div className="user-card">Jordan</div>
```

React’s DOM API uses the property name `className` to represent the HTML `class` attribute. The compiler and React translate it appropriately when rendering. In modern JavaScript, `class` is also a language keyword used to declare classes, so JSX uses the distinct property name `className`.

### 4. Use Curly Braces for JavaScript Expressions

Use `{}` when inserting a JavaScript expression into JSX:

```jsx
function NotificationCount({ count }) {
 return <span>You have {count} new notifications</span>;
}
```

A string can be written directly as JSX text, while dynamic values and calculations go inside braces.

### 5. Use JavaScript Names for DOM Properties

Many JSX DOM properties use camelCase:

```jsx
<button onClick={handleClick} tabIndex={0}>
 Open details
</button>
```

Common examples include `onClick`, `onChange`, and `tabIndex`. A few attributes use names that differ from their HTML spelling, including `className` and `htmlFor`.

```jsx
<label htmlFor="email">Email address</label>
<input id="email"type="email" />
```

### 6. Render Lists with Stable Keys

When rendering a collection of elements, provide a stable `key` so React can match items between renders:

```jsx
function ClaimList({ claims }) {
 return (
 <ul>
 {claims.map(claim => (
 <li key={claim.id}>
 {claim.referenceNumber}: {claim.status}
 </li>
 ))}
 </ul>
 );
}
```

Keys are used by React during reconciliation; they are not displayed in the UI and are not passed through as ordinary props. A stable database ID is generally better than an array index, especially if items can be inserted, removed, or reordered. Incorrect keys can make React associate an item’s previous state or DOM with the wrong list item.

### 7. Pass Styles as JavaScript Objects

The `style` prop accepts an object whose property names are typically camelCase:

```jsx
<div style={{ backgroundColor: "white", padding: "12px" }}>
 Account summary
</div>
```

The outer braces indicate a JavaScript expression; the inner braces create the object. For larger or reusable styling, a CSS class is often clearer.

### 8. Use Components as Components

In JSX, lowercase names represent built-in HTML elements, while capitalized names refer to JavaScript components:

```jsx
function ProfileCard() {
 return <section>Profile information</section>;
}

function Page() {
 return <ProfileCard />;
}
```

Writing `<profileCard />` would be interpreted as a lowercase tag, not as the `ProfileCard` component.

### 9. Use Conditional Rendering Intentionally

Conditional expressions can determine which elements appear:

```jsx
function DeliveryStatus({ failed, message }) {
 return (
 <div>
 {failed
 ? <p role="alert">Delivery failed: {message}</p>
 : <p>Delivery complete</p>}
 </div>
 );
}
```

Logical `&&` is also common when rendering something only when a condition is true:

```jsx
{hasErrors && <p role="alert">Correct the highlighted fields.</p>}
```

Be aware that JavaScript’s `&&` expression returns the left-hand value when it is falsy. For numeric values such as `0`, `{count && <span>...</span>}` can render the `0`. A comparison such as `{count > 0 && <span>...</span>}` avoids that surprise.

### 10. Treat HTML Injection Carefully

React escapes text values rendered in JSX by default, which helps prevent untrusted text from being interpreted as HTML. The special `dangerouslySetInnerHTML` prop bypasses that protection and should only be used with trusted or properly sanitized HTML.

```jsx
<div dangerouslySetInnerHTML={{ __html: sanitizedHtml }} />
```

Never insert untrusted user-provided HTML this way without an appropriate sanitization strategy.

## 5. Real-World JSX Examples

### Search Results

A search page can render different UI based on its data and loading state:

```jsx
function SearchResults({ isLoading, results }) {
 if (isLoading) {
 return <p>Searching...</p>;
 }

 if (results.length === 0) {
 return <p>No matching claims were found.</p>;
 }

 return (
 <ul>
 {results.map(result => (
 <li key={result.id}>
 {result.claimNumber} - {result.status}
 </li>
 ))}
 </ul>
 );
}
The JSX expresses the loading, empty, and populated states. React renders the appropriate output when the component receives updated props.

### Notification Panel

A reusable row component can present each message:

```jsx
function NotificationRow({ notification }) {
 return (
 <li className={notification.read ? "read" : "unread"}>
 <strong>{notification.title}</strong>
 <p>{notification.message}</p>
 <time dateTime={notification.createdAt}>
 {notification.displayTime}
 </time>
 </li>
 );
}
```

The component uses dynamic text and a conditional CSS class. A parent can map over notification data and provide a stable key for each row.

### Form Field and Validation

JSX can keep labels, inputs, and validation messages together:

```jsx
function EmailField({ value, error, onChange }) {
 return (
 <div>
 <label htmlFor="email">Email address</label>
 <input
id="email"
type="email"
value={value}
onChange={onChange}
aria-invalid={Boolean(error)}
aria-describedby={error ? "email-error" : undefined}
 />
 {error && (
 <p id="email-error"role="alert">
 {error}
 </p>
 )}
 </div>
 );
}
```

This example shows how JSX can express accessible relationships as well as visual structure. JSX itself does not guarantee accessibility; developers must choose appropriate HTML elements and attributes.

## 6. Advantages of JSX

-**Readable component markup:** Nested UI is often easier to scan than deeply nested function calls.
-**JavaScript integration:** Data, props, and expressions can be used directly in the UI description.
-**Compile-time feedback:** Tooling can detect many syntax mistakes and provide editor support.
-**Component composition:** A component can be included naturally inside another component’s JSX.
-**Consistent rendering model:** JSX describes React elements; React handles reconciling and committing UI updates.
-**Supports dynamic interfaces:** Lists, conditional content, and changing values can be expressed alongside markup.

## 7. Disadvantages and Trade-Offs

-**Requires a build step:** Browsers do not execute JSX directly, so projects need appropriate compilation tooling.
-**It is not HTML:** Developers must learn differences such as `className`, camelCase event names, and expression braces.
-**Large JSX expressions can become hard to read:** Complex business logic inside markup may be difficult to test and maintain. Moving logic into named variables or smaller components often helps.
-**It can blur markup and logic:** Keeping UI and JavaScript together is convenient, but teams still need boundaries and consistent conventions.
-**The output can hide implementation details:** Developers troubleshooting a build may need to understand how the JSX transform works.
-**JSX does not ensure good UI quality:** It does not automatically provide accessibility, security for manually injected HTML, responsive styling, or efficient application architecture.

### Interview Summary

> JSX is a JavaScript syntax extension that lets us describe React UI with markup-like syntax. A compiler transforms it into JavaScript that creates React element descriptions; React then uses those descriptions during rendering and reconciliation. JSX is not understood directly by browsers. It improves readability and component composition, but requires build tooling and has rules that differ from HTML, such as `className`, camelCase DOM properties, expression braces, and stable keys for lists.
Updated todo list

## 3. Virtual DOM

### 5. What Is the Virtual DOM?

The **Virtual DOM** is a common name for React’s in-memory description of the UI. React components return **React elements** describing what should appear: which elements or components to render, their properties, and their children. React uses these descriptions, along with its internal data structures, to decide how to update the actual browser DOM.

For example:

```jsx
function Greeting({ name }) {
 return <h1 className="greeting">Hello, {name}</h1>;
}
```

This JSX does not immediately create an `<h1>` DOM node. It is transformed into JavaScript that creates a lightweight React element description. React processes that description as part of rendering.

A simplified update flow is:

```text
Props or state change
 ↓
React renders component output
 ↓
React reconciles the new output with its previous work
 ↓
React commits necessary changes to the host environment
 ↓
Browser updates and paints the DOM
```

For a web application, the host environment is usually the browser DOM. React Native uses a different host environment while using React’s component and rendering model.

### What Happens Internally?

The term “Virtual DOM” is useful as an introduction, but it can make React’s internals sound simpler than they are. A React element is a description of UI, not a browser node. React’s **Fiber architecture** stores and schedules work associated with components and their rendered output.

When an update occurs, React broadly performs these steps:

1.**An update is scheduled.** A state setter, new props, or another update causes React to schedule work. React may group multiple updates together.
2.**React renders the affected component tree.** React calls component functions to calculate the next element descriptions. This is the **render phase**. Rendering should not cause side effects because React may repeat, pause, or discard render work.
3.**React reconciles the new output.** React compares the new output with its previous internal representation. It uses heuristics based on element type, tree position, and keys to decide which component instances or elements can be reused and which need to be created, updated, or removed.
4.**React commits the result.** In the **commit phase**, React applies the selected changes to the browser DOM. It also coordinates relevant lifecycle behavior and effects according to their timing.
5.**The browser paints.** The browser processes the DOM and displays the visual result.

React does not generally perform an exhaustive comparison of every possible tree against every other tree. It uses rules that make reconciliation practical. For example, when an element has a different type from the element previously at that position, React will generally replace that part of the tree. When elements of the same type remain in the same positions, React can often reuse them and update changed properties.

### Why Keys Matter

Keys help React identify items in a list across renders. Consider a list of notifications:

```jsx
function NotificationList({ notifications }) {
 return (
 <ul>
 {notifications.map(notification => (
 <li key={notification.id}>
 {notification.message}
 </li>
 ))}
 </ul>
 );
}
```

If a notification is inserted at the beginning, stable keys allow React to match each item with the corresponding previous item. This helps it update the right elements and preserve the intended component state.

Using an array index as a key can cause mismatches when list items are inserted, removed, or reordered. React may associate previous UI or local component state with the wrong item. Keys should be stable and unique among siblings; a database ID is often suitable.

### Example: Updating a Cart

Suppose a user adds an item to a shopping cart. The application updates cart state. A component that displays the count can render the new value:

```jsx
function CartCount({ items }) {
 return <span>Cart: {items.length} items</span>;
}
```

React calculates the updated component output and commits the required DOM change. The developer does not need to find the old text node manually and replace it. Other parts of the page that do not need to change can remain untouched.

### Example: Live Operations Dashboard

A dashboard may display batch status, notification totals, and recent failures. When new data arrives, React renders the current status from the updated data. Reconciliation helps determine which parts of the rendered interface changed, such as a status label or a row in a table.

The Virtual DOM does not fetch the new data or decide how often to refresh it. Those are application concerns. React handles rendering the interface based on the data it receives.

### Follow-Up: Is the Virtual DOM Always Faster Than the DOM?

**No.** The Virtual DOM is not inherently faster than direct DOM manipulation. React adds work: it must render components, create or process element descriptions, reconcile them, and then commit updates. For a simple page with one small update, carefully written direct DOM code may involve less overhead.

The benefit is that React provides a consistent programming model for managing UI changes. In a complex application, manually keeping many DOM elements synchronized with application data can become error-prone and expensive to maintain. React manages that relationship and applies updates based on the new UI description.

Actual performance depends on factors such as:

- How many components render after an update.
- How expensive those components are to render.
- How large or frequently changing the DOM is.
- Whether list keys and state ownership are appropriate.
- Whether the application creates unnecessary updates.
- Browser layout, painting, and other work outside React.

React can also avoid some unnecessary work through component structure and mechanisms such as `memo`, but these should be used based on profiling and actual needs. A Virtual DOM does not guarantee that an application is fast.

## Advantages

-**Declarative updates:** Developers describe the desired UI for current props and state; React manages applying changes.
-**Fewer manual DOM operations:** Application code does not need to coordinate every text, attribute, and element update itself.
-**Consistent rendering model:** The same component can render different output as its inputs change.
-**Component reuse:** Shared components can be updated and reused across screens.
-**Reconciliation heuristics:** React can reuse compatible component and DOM structures instead of requiring developers to rebuild the entire page.
-**Support for scheduling:** Fiber allows React to organize rendering work and, in supported scenarios, pause or reprioritize render work before it commits.

## Disadvantages and Limitations

-**It has overhead:** React’s rendering and reconciliation work takes time and memory. For very small interfaces, direct DOM code can be simpler and lighter.
-**It does not guarantee better performance:** Poor state design or frequent expensive renders can make an application slow.
-**Keys and identity matter:** Unstable keys can cause incorrect state association in lists or unnecessary element replacement.
-**The terminology can mislead:** The Virtual DOM is not a second browser DOM, nor is it necessarily a complete copy of the DOM. React elements and Fiber structures have different roles.
-**Debugging can require understanding React’s lifecycle:** Render work may be repeated or discarded, so side effects placed in render logic can cause bugs.
-**DOM work is not the only cost:** Network requests, JavaScript computation, CSS layout, image loading, and browser painting can dominate performance regardless of reconciliation.

### Interview Summary

> The Virtual DOM is a convenient term for React’s in-memory descriptions of the UI. When props or state change, React renders new descriptions, reconciles them with its prior internal work using element types and keys, and commits the necessary updates to the actual DOM. This lets developers describe UI changes declaratively instead of manually synchronizing DOM nodes. It is not always faster than direct DOM manipulation; its main value is providing a predictable and maintainable way to manage complex interfaces, while performance still depends on the application’s rendering patterns and browser work.

Updated todo list

## 4. Reconciliation

### 6. What Is Reconciliation?

**Reconciliation** is the process React uses to figure out how the rendered UI should change when a component’s props or state change. React compares the new element descriptions produced by rendering with its previous internal representation, then determines which parts can be reused and which need to be created, updated, or removed.

For example, when a user marks one task complete, the application may render the task list again with a changed status. React reconciles the new output with the previous output and commits the necessary DOM updates. The developer describes the desired UI; React manages how the existing UI is brought into line with that description.

```text
Props or state change
 ↓
Components render new element descriptions
 ↓
React reconciles new output with previous work
 ↓
React commits required updates
 ↓
Browser displays the result
```

Reconciliation is related to the idea of comparing a “Virtual DOM,” but it is more precise to say that React reconciles **React elements and its internal Fiber representation**. React elements are descriptions of the UI; Fibers hold information React uses to track component work and schedule it.

### How Reconciliation Works

At a high level, React follows these steps:

1.**An update is scheduled.** A state update, new props, or another event tells React that some part of the interface may need to change.
2.**React renders the component output.** React calls components to calculate the next element descriptions. This is the render phase. Rendering calculates a possible result; it does not directly update the browser DOM.
3.**React matches old and new elements.** React uses heuristics to determine which elements and components correspond between the previous and next output. Element type, tree position, and keys are important signals.
4.**React prepares changes.** Based on those matches, React determines which DOM nodes or component instances can be reused, which properties need updating, and which elements should be inserted or removed.
5.**React commits the result.** During the commit phase, React applies the selected changes to the host environment, usually the browser DOM.

React’s reconciliation is designed to be practical rather than to exhaustively test every possible way two arbitrary trees might be equivalent. In general, compatible elements at corresponding positions can be reused. Different element types usually cause React to replace that part of the tree and recreate its descendants.

React’s Fiber architecture lets it represent and schedule rendering work. In concurrent rendering scenarios, React may pause or discard render work before committing it. That is why render logic should be pure: it must not directly perform side effects that would be incorrect if React renders more than once or abandons a render attempt.

### Element Type and Position Affect Identity

Consider two renders that both produce a `<p>` element in the same position:

```jsx
function Message({ text }) {
 return <p>{text}</p>;
}
```

If only `text` changes, React can generally reuse the paragraph element and update its text content.

But if the element type changes:

```jsx
function Message({ showWarning, text }) {
 return showWarning
 ? <p className="warning">{text}</p>
 : <div>{text}</div>;
}
```

React sees a different type (`p` versus `div`) at that position. It will generally replace that element and recreate the relevant subtree. If the replaced subtree contains stateful components, their state may be discarded because they are no longer the same component instances.

The same component type in the same position will often retain its component identity and state across updates. A key can alter that identity intentionally, as described below.

## 7. What Role Do Keys Play in Reconciliation?

A **key** is a stable identifier React uses to match items among siblings in a list across renders. It helps React distinguish items even when their positions change.

```jsx
function UserList({ users }) {
 return (
 <ul>
 {users.map(user => (
 <Userkey={user.id} user={user} />
 ))}
 </ul>
 );
}
```

When the list changes, the keys help React identify which item was added, removed, retained, or reordered. Keys should be unique among the siblings in that list and stable between renders.

Keys are not passed to the component as ordinary props. If the component needs the ID, pass it separately:

```jsx
<Userkey={user.id} id={user.id} user={user} />
```

### What Happens Without Stable Keys?

Suppose the current list is:

```text
Position 0: Alice
Position 1: Ben
Position 2: Casey
```

Then a new user is inserted at the beginning:

```text
Position 0: Dana
Position 1: Alice
Position 2: Ben
Position 3: Casey
```

If the array index is used as the key, React may match the old item at position 0 with the new item at position 0, even though they represent different users. React then has to update the element at each position, and state belonging to a row component can be associated with the wrong user.

A stable user ID avoids this mismatch. React can recognize that Dana is new, while Alice, Ben, and Casey are existing items that moved.

### Why Index Keys Can Be Risky

This is often acceptable when the list is static and never reordered or edited:

```jsx
items.map((item, index) => (
 <li key={index}>{item.label}</li>
))
```

It can cause problems when items are inserted, deleted, filtered, or reordered. If each row contains local state, an input value, or focus state, React might preserve that state at a position rather than with the logical item.

Prefer a stable identifier from the data:

```jsx
items.map(item => (
 <li key={item.id}>{item.label}</li>
))
```

Avoid generating a new key every render:

```jsx
items.map(item => (
 <li key={Math.random()}>{item.label}</li>
))
```

A changing key tells React that each item is new on every render. React may remove and recreate the elements repeatedly, losing local state and doing unnecessary work.

### Real-World Examples

**Editable task list:** Each task row has an input field and a completion checkbox. If a new task is inserted at the top, stable task IDs ensure each input and checkbox remains associated with the correct task.

**Search results with sorting:** A user sorts claims by date or status. Stable claim IDs let React track a claim even though its position in the rendered list changes. Using the index would identify the claim by its current position, not its identity.

**Paginated tables:** When moving between pages, keys help React identify rows. A unique record ID is generally appropriate. If records are intentionally different between pages, changing the component’s key can also be used to reset local state, but this should be an intentional choice.

**Tabs and forms:** Suppose a form component is rendered for one selected account, then the selected account changes. If the form should start fresh, the parent can give it a key based on the account ID:

```jsx
<AccountFormkey={account.id} account={account} />
```

When the key changes, React treats it as a different component identity and recreates it, resetting its local state. If the form should preserve state while switching accounts, changing the key would be the wrong choice.

**Dashboard cards:** A dashboard may render cards from configurable data. Stable card identifiers help React preserve card component state when cards are reordered or a card is added or removed.

### Advantages of Reconciliation

-**Declarative updates:** Developers describe the new UI rather than manually applying every DOM change.
-**Component reuse:** React can preserve compatible component instances and their state across updates.
-**List identity:** Stable keys let React match list items across insertions, deletions, and reordering.
-**Managed DOM updates:** React applies the changes needed to bring the rendered UI in line with the latest component output.
-**Scheduling support:** Fiber gives React a structure for organizing rendering work and, in supported scenarios, interrupting or reprioritizing render work before commit.

### Limitations and Common Mistakes

-**It is not a guarantee of maximum speed:** Rendering and reconciliation have their own costs, and application code can still cause unnecessary work.
-**Incorrect keys can cause bugs:** Index keys in changing lists may associate state with the wrong item.
-**Unstable keys cause remounts:** Random or frequently changing keys can make React discard and recreate components.
-**Changing types may reset state:** Replacing one component type with another at the same position generally creates a new component identity.
-**Render logic must remain pure:** React may repeat or discard render work; side effects in render functions can therefore produce incorrect behavior.
-**Keys only identify siblings:** A key needs to be unique within its sibling list, not globally across the entire application.

### Interview Summary

> Reconciliation is how React determines what to update when component output changes. React renders new element descriptions, matches them with its previous internal representation using factors such as type, position, and keys, and then commits the necessary DOM changes. Keys are stable identifiers for sibling elements, especially list items. They let React track items through insertion, deletion, and reordering, and help preserve the correct component state. Index or unstable keys can cause state mismatches, unnecessary remounts, and rendering bugs.
Updated todo list

Read-only Ask mode prevents me from inserting this into the file. The expanded, ready-to-add section below is written for `react.md`.

## 5. Components

### 7. What Is a React Component?

A **React component** is a reusable unit that describes part of a user interface and, when needed, the behavior associated with it. Components can receive data, respond to events, manage state, and compose other components. A component may represent a small element, such as a button, or a larger part of an application, such as a dashboard page.

```jsx
function Welcome() {
 return <h1>Welcome</h1>;
}
```

A component is used in JSX like an element:

```jsx
function App() {
 return (
 <main>
 <Welcome />
 </main>
 );
}
```

`<Welcome />` is not an HTML tag. It tells React to render the `Welcome` component. React calls the component as part of rendering and uses its returned React elements to determine what should appear.

A component does not have to return exactly one DOM element. It can return a Fragment containing several elements, or `null` when it should render nothing.

```jsx
function AccountNotice({ hasAlerts }) {
 if (!hasAlerts) {
 return null;
 }

 return (
 <>
 <h2>Account alerts</h2>
 <p>Please review the items that need attention.</p>
 </>
 );
}
```

A Fragment groups elements without adding an extra DOM wrapper.

### Function Components and Class Components

Historically, React had two common component styles:

-**Function components:** JavaScript functions that return React elements.
-**Class components:** JavaScript classes that extend `React.Component` and define a `render()` method.

Modern React primarily uses **function components with Hooks**. Class components are still supported, and developers may encounter them in existing applications, but new components are usually written as functions.

```jsx
function Greeting({ name }) {
 return <h1>Hello, {name}</h1>;
}
```

A class component expresses similar UI differently:

```jsx
class Greeting extends React.Component {
 render() {
 return <h1>Hello, {this.props.name}</h1>;
 }
}
```

Function components can use Hooks such as `useState` and `useEffect` to manage state and synchronize with external systems. Hooks must be called at the top level of a function component or custom Hook, not inside conditions or loops. React uses the stable order of Hook calls to associate stored Hook data with the correct component.

### Props, State, and Composition

**Props** are inputs passed from a parent component. A child component should treat props as read-only: it should not modify the values it receives.

```jsx
function NotificationRow({ notification }) {
 return (
 <article>
 <h3>{notification.title}</h3>
 <p>{notification.message}</p>
 </article>
 );
}

function NotificationList({ notifications }) {
 return (
 <section>
 {notifications.map(notification => (
 <NotificationRow
key={notification.id}
notification={notification}
 />
 ))}
 </section>
 );
}
```

Here, `NotificationList` composes several `NotificationRow` components. Each row receives data through props. The `key` helps React identify each sibling row during reconciliation; it is not automatically passed as a normal prop.

**State** is data a component owns that can change over time. A state update schedules React to render the component again with the new state.

```jsx
import { useState } from "react";

function ExpandablePanel({ title, children }) {
 const [isOpen, setIsOpen] = useState(false);

 return (
 <section>
 <button
type="button"
aria-expanded={isOpen}
onClick={() => setIsOpen(open => !open)}
 >
 {title}
 </button>
 {isOpen && <div>{children}</div>}
 </section>
 );
}
```

This component receives its title and content through props, while it owns the open/closed state. The `children` prop represents content nested between the component’s opening and closing tags.

### How Components Work Internally

When React renders an application, it works through a **component tree**. Components return React element descriptions, which React processes to determine the next UI. A component function is not itself a DOM node; it may return one element, many elements through a Fragment, another component, or `null`.

A simplified sequence is:

1.**React encounters a component in the tree.** For example, it encounters `<NotificationRow notification={item} />`.
2.**React calls the component during the render phase.** It provides the current props and makes the component’s Hook state available.
3.**The component returns React elements.** These describe the desired output, rather than directly changing the browser DOM.
4.**React reconciles the output.** It compares the new output with the previous one, using factors such as component type, tree position, and keys to determine what can be reused.
5.**React commits required changes.** React applies selected changes to the host environment, usually the browser DOM. The browser then paints the updated UI.

A component can render again because its state changed, its parent rendered with new props, or context it consumes changed. A render does not necessarily mean React replaces all DOM nodes. React may reuse compatible elements and update only what needs to change.

The **render phase should be pure**: given the same props and state, a component should produce the same output and should not modify external systems. React may render components more than once, or discard render work before committing it. Network requests, subscriptions, and other external side effects should not be started during rendering. Hooks such as `useEffect` are used for appropriate synchronization with external systems.

Component identity matters too. If React sees a different component type at the same position, it generally treats it as a different component and resets the prior component’s local state. A changed `key` can also intentionally create a new component identity. For example, changing a form’s key when switching accounts can reset the form, but doing so accidentally can discard user input.

### Real-World Examples

**Notification center:** A page can be split into a notification panel, filter controls, notification rows, and an empty state. A row component handles the display of one message, while the parent handles the collection and passes each row its data. This keeps the row’s presentation separate from list-level responsibilities.

**Application form:** A form can use reusable input components that receive a label, value, error message, and change handler. The parent may own the form data and validation status, while each field focuses on presenting one control accessibly. This makes consistent validation feedback easier across several forms.

**Operations dashboard:** Separate components can display batch status, delivery totals, and recent failures. Each can receive data from a parent or shared data layer. When a status changes, React renders the relevant component output from the new data.

**Product catalog:** A product card can be reused across search results, recommendations, and category pages. It receives product details through props, while actions such as adding an item to the cart can be provided through event-handler props.

These examples illustrate component reuse, but reuse should not be forced. A component is most useful when it represents a clear responsibility or a UI pattern that benefits from being shared.

### Advantages of Components

-**Reuse and consistency:** Shared UI and behavior can be implemented once and used in multiple places.
-**Separation of responsibilities:** A large interface can be divided into smaller units with clearer purposes.
-**Composability:** Small components can be combined to build pages and larger features.
-**Testability:** Components can be tested through their rendered output and user interactions.
-**Maintainability:** A focused component is generally easier to change than a large block of unrelated UI logic.
-**State boundaries:** Components make it possible to keep local state close to the part of the interface that uses it.

### Disadvantages and Trade-Offs

-**Poor boundaries create complexity:** Components with too many responsibilities can become difficult to understand and reuse.
-**Over-fragmentation can obscure the UI:** Splitting every minor markup detail into a component can make it harder to follow the overall page.
-**Data can be passed through too many layers:** Deep component trees may lead to “prop drilling,” where intermediate components pass props they do not use. Context or another state-management approach may help when appropriate.
-**State ownership takes thought:** State kept in the wrong component can cause unnecessary updates or awkward coordination.
-**Components do not guarantee performance:** A component may render often or perform expensive work. Optimize based on measured problems rather than assuming componentization automatically makes an application fast.
-**Hooks have rules:** Incorrect Hook usage or effects with unclear responsibilities can introduce subtle bugs.

### Interview Summary

> A React component is a reusable unit that describes part of the UI and may also manage behavior or local state. Modern React mainly uses function components with Hooks; class components remain supported and appear in legacy code. Components receive read-only inputs through props, can own changing state, and can be composed to build larger interfaces. Internally, React calls components during rendering, reconciles their returned element descriptions, and commits necessary changes to the host environment. Good component boundaries improve reuse and maintainability, but components still need clear responsibilities and deliberate state ownership.
Updated todo list

Read-only Ask mode prevents me from editing files. The following ready-to-add section is intended for `react.md`.

## Functional Components vs Class Components

React components can be written as **function components** or **class components**. Both can describe UI, receive props, and participate in React’s rendering process. Modern React primarily uses function components with Hooks. Class components remain supported, and understanding them is useful when maintaining older applications or discussing React lifecycle behavior.

### Function Component Example

```jsx
import { useState } from "react";

function Counter() {
 const [count, setCount] = useState(0);

 return (
 <button onClick={() => setCount(current => current + 1)}>
 Clicked {count} times
 </button>
 );
}
```

A function component is a JavaScript function that receives props and returns React elements. Hooks such as `useState` let it use React features such as state and effects.

### Class Component Example

```jsx
import { Component } from "react";

class Counter extends Component {
 state = { count: 0 };

 render() {
 return (
 <button
onClick={() =>
 this.setState(current => ({ count: current.count + 1 }))
 }
 >
 Clicked {this.state.count} times
 </button>
 );
 }
}
```

A class component extends `Component`, stores local state on `this.state`, and describes its UI in a `render()` method. State is updated with `this.setState()`.

### Comparison

| Topic | Function component | Class component |
|---|---|---|
| Declaration | JavaScript function | ES6 class extending `Component` |
| UI output | Function return value | `render()` return value |
| State | Hooks such as `useState` or `useReducer` | `this.state` and `this.setState()` |
| Side effects | Hooks such as `useEffect` | Lifecycle methods |
| `this` binding | Not needed for component state | Often needed for methods and state |
| Reusable logic | Custom Hooks | Higher-order components, render props, other patterns |
| Common use today | Default for new React code | Common in legacy code; still supported |
| Error boundaries | Not implemented directly with function components alone | Supported through class lifecycle methods |

Neither style is automatically faster. Performance depends on the component’s work, how often it renders, and the surrounding application design. Choose function components for most new work because Hooks are the current, widely used React model. Use class components when working in an existing codebase or when a requirement calls for a class-based error boundary.

## Class Component Lifecycle

A class component’s lifecycle is commonly explained in three phases: **mounting**, **updating**, and **unmounting**. Error handling is often described as a fourth category.

### 1. Mounting

Mounting is when React creates a component and inserts its output into the UI.

Common methods:

- `constructor(props)`: Initializes state or binds methods when necessary. Call `super(props)` before using `this`. Many class fields can now be initialized without writing a constructor.
- `static getDerivedStateFromProps(props, state)`: Updates state in response to props. This is rarely needed and can make state flow harder to reason about.
- `render()`: Describes the UI. It must be pure and should not perform side effects.
- `componentDidMount()`: Runs after the component has been committed to the DOM. It is a common place to start a subscription, connect to an external system, or perform an initial request.
### 2. Updating

An update can happen when the component receives new props, its state changes, or consumed context changes.

Common methods:

- `static getDerivedStateFromProps(props, state)`: Runs during mounting and updating when present.
- `shouldComponentUpdate(nextProps, nextState)`: Can prevent an update for optimization. Use it carefully; returning `false` incorrectly can prevent required UI changes.
- `render()`: Produces the next UI description.
- `getSnapshotBeforeUpdate(prevProps, prevState)`: Runs before React applies the DOM changes. It can capture information such as a scroll position.
- `componentDidUpdate(prevProps, prevState, snapshot)`: Runs after the update is committed. It is commonly used to synchronize with an external system when relevant props or state changed.

`componentDidUpdate` must be guarded when it triggers another state update. Without a condition, setting state there can cause an endless update loop.

### 3. Unmounting

Unmounting is when React removes a component from the UI.

- `componentWillUnmount()`: Clean up subscriptions, timers, event listeners, or other resources created by the component.

Cleanup matters because a subscription or timer that continues after the component is removed may keep using resources or cause unwanted updates.

### 4. Error Handling

Class components can act as **error boundaries** using:

- `static getDerivedStateFromError(error)` to update state so fallback UI can be shown.
- `componentDidCatch(error, info)` to report or log the error.

A normal function component cannot implement an error boundary solely by adding a Hook. Applications commonly use a class error boundary or a library/framework abstraction.

Older lifecycle methods such as `componentWillMount`, `componentWillReceiveProps`, and `componentWillUpdate` are legacy APIs and should generally be avoided in new code.

## Lifecycle Thinking with Function Components

Function components do not have class lifecycle methods. Instead, Hooks express state and synchronization needs. For example, an effect can set up and clean up a subscription:

```jsx
import { useEffect } from "react";

function BatchStatus({ batchId }) {
 useEffect(() => {
 const connection = subscribeToBatch(batchId, status => {
 console.log("New batch status:", status);
 });

 return () => {
 connection.unsubscribe();
 };
 }, [batchId]);

 return <p>Monitoring batch {batchId}</p>;
}
```

When `batchId` changes, React runs the cleanup for the previous subscription, then runs the effect for the new ID. When the component is removed, React runs the cleanup.

A useful conceptual comparison is:

| Class lifecycle need | Common function-component approach |
|---|---|
| Initialize state | `useState` or `useReducer` |
| Set up synchronization after commit | `useEffect` |
| Clean up a subscription or timer | Return a cleanup function from `useEffect` |
| Capture a value immediately before a DOM update | Sometimes `getSnapshotBeforeUpdate`; function components may use refs and an appropriate layout effect, depending on the requirement |
| Error boundary | Use a class error boundary or a framework/library abstraction |

`useEffect` is **not** a one-to-one replacement for each lifecycle method. It is primarily for synchronizing with external systems after React commits an update. Effects run based on their dependencies, and their cleanup runs before the effect is set up again and when the component is removed. For work that must happen before the browser paints, `useLayoutEffect` may be appropriate, but it can block painting and should be used deliberately.

In development, React Strict Mode may run effect setup and cleanup an extra time to help reveal missing cleanup and unsafe assumptions. That behavior is for development checks; effect logic should still be written so setup and cleanup are safe.

## Real-World Example: Subscribe to a Batch Status

A class component might subscribe after mounting, resubscribe if the batch ID changes, and unsubscribe when removed:

```jsx
class BatchStatus extends Component {
 componentDidMount() {
 this.subscribe(this.props.batchId);
 }

 componentDidUpdate(previousProps) {
 if (previousProps.batchId !== this.props.batchId) {
 this.unsubscribe();
 this.subscribe(this.props.batchId);
 }
 }

 componentWillUnmount() {
 this.unsubscribe();
 }

 subscribe(batchId) {
 this.connection = subscribeToBatch(batchId, status => {
 this.setState({ status });
 });
 }

 unsubscribe() {
 this.connection?.unsubscribe();
 this.connection = null;
 }

 state = { status: "Loading" };

 render() {
 return <p>Status: {this.state.status}</p>;
 }
}
```

A function component expresses the same synchronization around the `batchId` dependency:

```jsx
function BatchStatus({ batchId }) {
 const [status, setStatus] = useState("Loading");

 useEffect(() => {
 const connection = subscribeToBatch(batchId, setStatus);
 return () => connection.unsubscribe();
 }, [batchId]);

 return <p>Status: {status}</p>;
}
```

The effect keeps the subscription aligned with the current `batchId`. This is a better mental model than trying to mechanically translate `componentDidMount` into an empty dependency array and `componentDidUpdate` into another effect.

## Advantages and Disadvantages

**Function components:**

-**Advantages:** Less class-specific syntax, no `this` binding, Hooks support reusable stateful logic, and the style aligns with current React development.
-**Disadvantages:** Hook rules and dependency arrays require care. Closures can capture outdated values if dependencies or logic are wrong. Effects are sometimes misused to calculate values that could be derived during rendering.

**Class components:**

-**Advantages:** Lifecycle methods make certain mount/update/unmount transitions explicit, and classes remain useful for reading legacy code and implementing error boundaries.
-**Disadvantages:** More boilerplate, `this` binding and lifecycle coordination add complexity, and related logic can be split across several lifecycle methods. Reusing stateful logic often requires patterns such as higher-order components or render props.

### Interview Summary

> Function components are JavaScript functions that return UI and use Hooks for state and synchronization. Class components extend `React.Component`, return UI from `render()`, manage state with `this.setState()`, and use lifecycle methods such as `componentDidMount`, `componentDidUpdate`, and `componentWillUnmount`. Modern React generally favors function components with Hooks. Lifecycle methods describe class component phases, while effects describe synchronization with external systems; `useEffect` is not a direct one-to-one replacement for every lifecycle method.

Updated todo list

Read-only Ask mode prevents me from editing `react.md`. Below is a revised, expanded section based on the pasted text, with lifecycle details, internal behavior, trade-offs, and matching real-world examples.

## Functional Components vs Class Components

React supports two component styles: **function components** and **class components**. Both can receive props, describe UI, and participate in React’s rendering and reconciliation. The main difference is how they express state, lifecycle-related behavior, and reusable logic.

Modern React generally favors **function components with Hooks**. Class components remain supported and are common in older codebases. Understanding both is useful when maintaining existing applications or migrating features.

### Function Component

A function component is a JavaScript function that returns React elements. Hooks give it access to React state and other capabilities.

```jsx
import { useState } from "react";

function Counter() {
 const [count, setCount] = useState(0);

 return (
 <button onClick={() => setCount(current => current + 1)}>
 Clicked {count} times
 </button>
 );
}
```

The function itself is not a persistent object. React calls it during rendering with the current props and makes its Hook state available. Calling `setCount` schedules an update; it does not directly change the DOM.

### Class Component

A class component extends `React.Component`, stores state on the component instance, and describes the UI in `render()`.

```jsx
import { Component } from "react";

class Counter extends Component {
 state = { count: 0 };

 render() {
 return (
 <button
onClick={() =>
 this.setState(current => ({ count: current.count + 1 }))
 }
 >
 Clicked {this.state.count} times
 </button>
 );
 }
}
```

Class components have a persistent instance managed by React. State updates are made using `this.setState()`. When an object is passed to `setState`, React shallow-merges it into the existing state object; with `useState`, setting an object replaces the previous state value, so the component must explicitly preserve fields it still needs.

### Comparison

| Topic | Function component | Class component |
|---|---|---|
| Declaration | JavaScript function | Class extending `React.Component` |
| UI description | Function return value | `render()` return value |
| State | `useState`, `useReducer` | `this.state`, `this.setState()` |
| Lifecycle-related behavior | Hooks such as `useEffect` | Lifecycle methods |
| `this` | Not needed for state or props | Used for props, state, and instance methods |
| Reusable stateful logic | Custom Hooks | Often higher-order components or render props |
| Typical use today | Preferred for new React code | Supported; often found in existing applications |
| Error boundaries | Not implemented by a function component alone | Supported with class lifecycle methods |

Neither style is automatically faster. Performance depends on the work performed, the component tree, and the application’s rendering patterns. Function components are the usual choice for new code because Hooks provide a direct way to reuse stateful logic. Class components are still appropriate when maintaining a class-based codebase or implementing an error boundary.

## How React Handles Components Internally

React does not treat a component as a command to immediately update the DOM. A component describes what the UI should be for its current props and state.

A simplified update proceeds as follows:

1.**React receives an update.** A parent passes new props, a state setter is called, context changes, or another update is scheduled.
2.**React renders.** React calls relevant function components or class `render()` methods to calculate new React elements. This is the render phase.
3.**React reconciles.** It compares the new descriptions with the previous internal representation, using factors such as component type, tree position, and keys to determine what can be reused.
4.**React commits.** React applies the required changes to the host environment, usually the browser DOM, and runs the relevant commit-phase behavior.
5.**The browser paints.** The browser displays the result.

For function components, React associates Hook state with the component’s internal Fiber and the order of Hook calls. This is why Hooks must be called in the same order on every render: conditional Hook calls could make React associate state with the wrong Hook. A state setter queues an update and schedules React to render again.

For class components, React manages a class instance and its state. `setState` queues an update and schedules rendering; it should not be treated as a synchronous DOM update. Class state updates that depend on previous state should use the functional form, as in `this.setState(previous => ...)`, to avoid relying on a potentially stale value.

Rendering should be **pure** in both styles. Do not start requests, subscriptions, or timers directly inside a function component’s body or a class component’s `render()` method. React may repeat render work or discard it before commit. Put external synchronization in an effect or an appropriate lifecycle method instead.

## Class Component Lifecycle

Class lifecycles are usually described as mounting, updating, and unmounting.

### Mounting

Mounting is when a component is added to the rendered tree. The common sequence is:

```text
constructor
→ static getDerivedStateFromProps, if defined
→ render
→ DOM commit
→ componentDidMount
```

-**`constructor(props)`** initializes state or binds methods when needed. Call `super(props)` before using `this`. Class fields often remove the need for a constructor.
-**`static getDerivedStateFromProps(props, state)`** can derive state from props, but it is rarely needed and can make state harder to reason about.
-**`render()`** returns the UI description. It must not perform side effects.
-**`componentDidMount()`** runs after the component has been committed to the DOM. It is a suitable place to subscribe to an external source, start a timer, or perform an initial request.

### Updating

An update can result from new props, a state change, or a context change. A simplified sequence is:

```text
static getDerivedStateFromProps, if defined
→ shouldComponentUpdate, if defined
→ render
→ getSnapshotBeforeUpdate, if defined
→ DOM commit
→ componentDidUpdate
```

-**`shouldComponentUpdate(nextProps, nextState)`** can skip rendering as an optimization. An incorrect `false` can prevent the UI from reflecting new data, so it should be used carefully.
-**`getSnapshotBeforeUpdate(prevProps, prevState)`** runs before React applies DOM changes. It can capture information such as a scroll position.
-**`componentDidUpdate(prevProps, prevState, snapshot)`** runs after the update is committed. It is useful for synchronizing with an external system when relevant inputs change.

If `componentDidUpdate` calls `setState`, it must check whether an update is necessary; otherwise, it can create an update loop.

### Unmounting and Error Handling

-**`componentWillUnmount()`** runs before React removes the component. Clean up subscriptions, timers, and event listeners established by the component.
-**`static getDerivedStateFromError(error)`** lets an error boundary render fallback UI after a descendant throws during rendering.
-**`componentDidCatch(error, info)`** is used to report or log errors caught by the boundary.

Some older lifecycle methods, including `componentWillMount`, `componentWillReceiveProps`, and `componentWillUpdate`, are legacy APIs and should generally not be used in new code.

## Function Components and Effect Cleanup

Function components do not have class lifecycle methods. Instead, Hooks express state and synchronization needs. `useEffect` runs after React has committed the UI and is used to synchronize with external systems. Its cleanup runs before the effect is set up again for changed dependencies and when the component is removed.

```jsx
import { useEffect, useState } from "react";

function BatchStatus({ batchId }) {
 const [status, setStatus] = useState("Loading");

 useEffect(() => {
 const connection = subscribeToBatch(batchId, setStatus);

 return () => {
 connection.unsubscribe();
 };
 }, [batchId]);

 return <p>Status: {status}</p>;
}
```

When `batchId` changes, React cleans up the old subscription and establishes the new one. When the component unmounts, it unsubscribes. This models synchronization around the current `batchId`; it is more useful than thinking of `useEffect` as merely “the function-component version of `componentDidMount`.”

`useEffect` is **not a one-to-one replacement** for every lifecycle method. For work that must run after DOM mutations but before the browser paints, `useLayoutEffect` may be appropriate, though it can block painting. In development, Strict Mode may run effect setup and cleanup an extra time to expose missing cleanup. Effect logic should therefore tolerate setup-cleanup-setup behavior.

## Real-World Example: Batch Status Subscription

A class component might establish a subscription when mounted, replace it when the batch ID changes, and remove it when unmounted:

```jsx
class BatchStatus extends Component {
 state = { status: "Loading" };

 componentDidMount() {
 this.subscribe(this.props.batchId);
 }

 componentDidUpdate(previousProps) {
 if (previousProps.batchId !== this.props.batchId) {
 this.unsubscribe();
 this.subscribe(this.props.batchId);
 }
 }

 componentWillUnmount() {
 this.unsubscribe();
 }

 subscribe(batchId) {
 this.connection = subscribeToBatch(batchId, status => {
 this.setState({ status });
 });
 }

 unsubscribe() {
 this.connection?.unsubscribe();
 this.connection = null;
 }

 render() {
 return <p>Status: {this.state.status}</p>;
 }
}
```

The function-component version keeps the setup and cleanup together in one effect, keyed to the `batchId`. That co-location often makes it easier to verify that every subscription is cleaned up correctly.

Other common examples include:

-**Search screen:** Local state stores the query and loading status; an effect or data-fetching library handles synchronization with the server.
-**Editable table:** Each row receives its record through props, while the parent owns sorting and filtering state.
-**Multi-step form:** A reducer can manage related fields and validation transitions; components present individual steps and inputs.
-**Dashboard:** Separate components display batch status, totals, and failures, allowing each section to receive the data it needs.

## Advantages and Disadvantages

**Function components**

-**Advantages:** Less class-specific syntax; no `this` binding; Hooks support reusable stateful logic; related setup and cleanup can live together.
-**Disadvantages:** Hook call order must remain stable; effect dependencies require care; closures can capture outdated values if dependencies or logic are wrong; effects can be misused for values that should simply be derived during rendering.

**Class components**

-**Advantages:** Lifecycle methods provide explicit points for mounting, updating, and cleanup; useful when reading legacy code; class lifecycle methods support error boundaries.
-**Disadvantages:** More boilerplate and possible `this` binding issues; related behavior can become split across lifecycle methods; reusable stateful logic often requires more involved patterns.

### Interview Summary

> Function components are JavaScript functions that return UI and use Hooks for state and synchronization. Class components extend `React.Component`, return UI from `render()`, manage state with `this.setState()`, and use lifecycle methods such as `componentDidMount`, `componentDidUpdate`, and `componentWillUnmount`. Modern React generally favors function components with Hooks. React schedules updates, renders component output, reconciles it, and commits changes; effects and lifecycle methods are for synchronizing with external systems, not for directly controlling every render.

Updated todo list

Read-only Ask mode prevents me from editing files. The following expanded, ready-to-add section is intended for `react.md`.

## 7. State

### 9. What Is State?

**State** is data that React remembers for a component between renders. It represents information that can change over time and affect what the component displays or how it behaves.

Examples include:

- Whether a menu is open.
- The text entered in a search field.
- Items in a shopping cart.
- Whether a request is loading or has failed.
- The selected tab or current page of a table.

A function component commonly declares local state with the `useState` Hook:

```jsx
import { useState } from "react";

function Counter() {
 const [count, setCount] = useState(0);

 return (
 <button onClick={() => setCount(1)}>
 Count: {count}
 </button>
 );
}
```

`useState(0)` gives the component an initial state value of `0`. It returns a pair:

- `count`: the state value for the current render.
- `setCount`: a function used to request a state update.

When `setCount(1)` is called, React schedules an update. It does **not** immediately change the `count` variable in the currently executing function or directly modify the DOM. React renders the component again, this time providing the updated state value. It then reconciles the new output with the previous output and commits the necessary UI changes.

### State Is a Snapshot for Each Render

Each render of a function component uses a snapshot of its props and state. Calling a state setter does not change values already captured in that render’s variables or event handler.

```jsx
function Counter() {
 const [count, setCount] = useState(0);

 function handleClick() {
 setCount(count + 1);
 console.log(count); // The value from this render
 }

 return <button onClick={handleClick}>Count: {count}</button>;
}
```

The log may show the old value because the current handler closes over the state snapshot from the render that created it. React schedules a later render with the new value.

When the next state depends on the previous state, use the **functional updater** form:

```jsx
setCount(currentCount => currentCount + 1);
```

This is especially important when multiple updates may be queued before React renders again:

```jsx
setCount(count => count + 1);
setCount(count => count + 1);
setCount(count => count + 1);
```

Each updater receives the result of the previous updater, so the count increases by three. Writing `setCount(count + 1)` three times uses the same render snapshot and may only increase it by one.

### State Updates Should Be Immutable

React compares state values to determine whether an update should be processed. Do not mutate an object or array already held in state and then pass the same reference back. Instead, create a new object or array describing the updated data.

Incorrect:

```jsx
items.push(newItem);
setItems(items);
```

Preferred:

```jsx
setItems(currentItems => [...currentItems, newItem]);
```

For an object:

```jsx
setUser(currentUser => ({
 ...currentUser,
 displayName: "Jordan"
}));
```

With `useState`, the new value replaces the old state value. It does not automatically merge object fields. The spread syntax preserves the fields that should remain.

## Props and State: What’s the Difference?

**Props** are inputs passed into a component, usually by its parent. **State** is data the component, or another designated owner, manages over time.

| Topic | Props | State |
|---|---|---|
| Where it comes from | Passed in by a parent | Managed by a component or state owner |
| How the component treats it | Read-only input | Updated by a setter or `dispatch` |
| Typical purpose | Configure a child or pass data downward | Remember changing UI or application data |
| What can cause a render | A parent provides new props | The state owner schedules an update |
| Example | `user`, `title`, `items` | `isOpen`, `query`, `selectedTab` |

A child should not modify its props. If it needs to request a change, the parent can pass a callback:

```jsx
function SearchBox({ query, onQueryChange }) {
 return (
 <input
value={query}
onChange={event => onQueryChange(event.target.value)}
aria-label="Search claims"
 />
 );
}

function ClaimsPage() {
 const [query, setQuery] = useState("");

 return <SearchBoxquery={query} onQueryChange={setQuery} />;
}
```

Here, `query` is state owned by `ClaimsPage`. `SearchBox` receives it as a prop and asks the parent to update it through `onQueryChange`. This keeps the state owner in control.

## How State Works Internally

A simplified state update proceeds as follows:

1.**A component calls a setter or dispatch function.** For example, a click handler calls `setIsOpen(true)`.
2.**React queues the update.** React associates it with the component’s internal state and schedules rendering. Updates can be batched so React can process several related changes together.
3.**React renders the component again.** The component function runs with the new state snapshot. Rendering should remain pure; it should calculate UI, not perform side effects.
4.**React reconciles the output.** React compares the new element descriptions with its previous work to determine what can be reused and what needs updating.
5.**React commits changes.** React applies the required DOM updates, after which the browser paints the result.

Internally, React stores Hook information with its Fiber representation of a component. Hook state is associated with the order in which Hooks are called. That is why Hooks such as `useState` must be called at the top level of a component or custom Hook, not conditionally inside a branch or loop.

State setters schedule work rather than forcing an immediate DOM update. React may batch updates, so code should not depend on the DOM having changed immediately after a setter is called. If code must respond to a change, it should generally use the newly rendered state or synchronize with an external system using an appropriate effect.

### Avoid Redundant State

Do not store a value in state if it can be calculated from props or other state during rendering. Keeping duplicate values can make them inconsistent.

For example, if a cart already stores its items, derive the item count:

```jsx
const itemCount = items.reduce(
 (total, item) => total + item.quantity,
0
);
```

Avoid separately storing both `items` and `itemCount` unless there is a specific reason. Otherwise, every update must keep both values synchronized.

## Real-World Examples

### Search and Filtering

The search text can be state because it changes as the user types. The visible results can often be derived from the search text and the full list:

```jsx
function ClaimSearch({ claims }) {
 const [query, setQuery] = useState("");

 const filteredClaims = claims.filter(claim =>
 claim.reference.toLowerCase().includes(query.toLowerCase())
 );

 return (
 <>
 <label>
 Search claims
 <input
value={query}
onChange={event => setQuery(event.target.value)}
 />
 </label>

 <ul>
 {filteredClaims.map(claim => (
 <li key={claim.id}>{claim.reference}</li>
 ))}
 </ul>
 </>
 );
}
```

The query is state. The filtered list is derived data. This avoids maintaining two separate sources of truth.

### API Loading, Success, and Error States

A dashboard might track whether a request is loading, whether it succeeded, and whether it failed. These values determine which UI to render: a progress message, results, or an error. For complex related state transitions, `useReducer` can make changes explicit, such as `FETCH_STARTED`, `FETCH_SUCCEEDED`, and `FETCH_FAILED`.

State is not the same thing as the server or database. It is the UI’s in-memory representation of data. Applications still need to handle request failures, stale responses, cancellation, and persistence appropriately.

### Shopping Cart

The cart’s item list might be owned by a page or shared state provider. Components receive item data and callbacks through props. The total can be derived from item prices and quantities rather than stored independently, reducing the chance that the displayed total becomes inconsistent with the cart contents.

## Advantages and Disadvantages of State

**Advantages:**

-**Interactive UI:** State lets components respond to clicks, typing, incoming data, and other events.
-**Predictable rendering:** The UI can be calculated from the current props and state.
-**Encapsulation:** Local state can live near the component that uses it.
-**Explicit updates:** Setters and reducer actions make state changes visible in the code.
-**Composable ownership:** State can be lifted to a parent or shared through context when multiple components need it.

**Disadvantages and risks:**

-**Unnecessary state creates complexity:** Duplicating values that could be derived can lead to bugs and synchronization work.
-**State ownership can be unclear:** Keeping state too low makes sharing difficult; keeping it too high can cause broader updates than necessary.
-**Mutations break expectations:** Modifying state objects or arrays in place can prevent React from detecting the intended change.
-**Updates are scheduled, not immediate:** Code that assumes a setter immediately changes variables or the DOM can behave incorrectly.
-**Stale closures can surprise developers:** Event handlers and effects capture values from the render that created them.
-**Too much shared state increases coupling:** Global state should be reserved for data that genuinely needs to be shared.
### Interview Summary

> State is data React preserves for a component between renders and that can change over time. A state setter or reducer dispatch schedules an update; React then renders with a new state snapshot, reconciles the output, and commits the required UI changes. Props are read-only inputs from a parent, while state is managed by its owner. I keep state minimal, avoid mutating it, derive values when possible, and use functional updates when the next value depends on the previous one.
Updated todo list

Read-only Ask mode prevents me from editing `react.md`. Below is an expanded, ready-to-add version.

## 8. `useState`

### 10. What Is `useState()`?

`useState` is a React **Hook** that lets a function component keep track of a value between renders. State is useful for information that can change and affect the UI, such as a counter, selected tab, search query, open menu, or list of cart items.

```jsx
import { useState } from "react";

function Counter() {
 const [count, setCount] = useState(0);

 return (
 <button onClick={() => setCount(count + 1)}>
 Count: {count}
 </button>
 );
}
```

In this example:

- `useState(0)` declares a state variable with an initial value of `0`.
- `count` is the state value for the current render.
- `setCount` is the function used to request a state update.

State is not just an ordinary variable inside the component. If you wrote `let count = 0` in the component body, that variable would be recreated each time React called the component. React state is preserved between renders and provided to the component again when it renders.

### What Happens When the Setter Is Called?

Calling `setCount(1)` does not immediately change `count` in the currently executing function, and it does not directly modify the browser DOM. It tells React that the component has an update to process.

```text
setCount(newValue)
 ↓
React queues/schedules an update
 ↓
React renders the component with the next state
 ↓
React reconciles the new UI output
 ↓
React commits required DOM changes
```

React may batch multiple updates together before rendering. This avoids unnecessary work and means code should not expect the UI or local variables to change immediately after calling a setter.

Each render sees a **snapshot** of its props and state. An event handler created during that render refers to that snapshot. This explains why the following log usually prints the old value:

```jsx
function Counter() {
 const [count, setCount] = useState(0);

 function handleClick() {
 setCount(count + 1);
 console.log(count); // Value from the current render
 }

 return <button onClick={handleClick}>Count: {count}</button>;
}
```

React schedules a new render. The handler’s `count` variable does not change while that handler is running.

### Initial State and Lazy Initialization

The value passed to `useState` is used to initialize the state when that component instance is first rendered. Later renders use the existing state, not the new initial argument.

```jsx
const [page, setPage] = useState(1);
```

If calculating the initial value is expensive, pass an initializer function:

```jsx
const [preferences, setPreferences] = useState(() => {
 return loadPreferencesFromStorage();
});
```

React calls the initializer to get the initial value rather than requiring the calculation to run as an argument on every render. In development Strict Mode, React may call initializer functions more than once to help detect impure code, so the initializer should not cause side effects.

If a component receives a new prop and should reset its state in response, changing the `useState` initial argument alone will not reset it. The initial value is not reapplied on every render. Decide explicitly whether to update the state, derive the value from props, or give the component a new `key` so React treats it as a new component instance.

## Why Use the Functional Form of State Update?

A state setter can receive either a **next value** or an **updater function**.

```jsx
setCount(10); // Set the next value to 10
setCount(previous => previous + 1); // Calculate it from pending state
```

Use the functional form when the next value depends on the previous value. React applies updater functions to the pending state in sequence.

```jsx
function Counter() {
 const [count, setCount] = useState(0);

 function addThree() {
 setCount(previous => previous + 1);
 setCount(previous => previous + 1);
 setCount(previous => previous + 1);
 }

 return <button onClick={addThree}>Count: {count}</button>;
}
```

After the click, the count increases by three. By contrast:

```jsx
function addThree() {
 setCount(count + 1);
 setCount(count + 1);
 setCount(count + 1);
}
```

Each expression uses the same `count` snapshot from the current render. Those calls all request the same next value, rather than three sequential increments. The functional form avoids depending on a potentially stale snapshot.

Updater functions should be **pure**: calculate and return the next state without changing external data or causing side effects. In development Strict Mode, React may call updater functions more than once to help detect impurities. Do not send requests, mutate objects, or write to external systems inside an updater.

## Updating Objects and Arrays

With `useState`, a new value **replaces** the previous state value. React does not automatically merge object properties as class-component `this.setState()` does.

```jsx
const [user, setUser] = useState({
 name: "Jordan",
 role: "Reviewer"
});

setUser(previous => ({
 ...previous,
 role: "Administrator"
}));
```

The spread operator preserves `name` while setting a new `role`. Without it, `setUser({ role: "Administrator" })` would replace the entire object, and `name` would be missing.

Treat state objects and arrays as immutable. Create a new value rather than changing the existing one in place:

```jsx
setItems(previousItems => [...previousItems, newItem]);

setUser(previousUser => ({
 ...previousUser,
 active: true
}));
```

Mutating the old value and passing the same object or array back can make it harder for React to detect that the state changed. It can also cause bugs because previous renders may still refer to that same object.

When the next value is identical to the current value according to `Object.is`, React may skip rendering work for that update. This is another reason to make meaningful changes by returning a new object or array.

## Internal Working of `useState`

At a high level, React associates Hook state with a component’s internal Fiber representation. During rendering, React processes Hooks in order, retrieves the stored value for each Hook, and returns that value with its setter.

When a setter is called:

1. React records the requested value or updater function as an update.
2. React schedules the component to render again.
3. React processes queued updates and calculates the next state.
4. React calls the component again with the state snapshot for that render.
5. React reconciles the returned elements and commits any necessary UI changes.

This internal association is why Hooks must be called in the same order on every render. Do not put `useState` inside a condition, loop, or nested function:

```jsx
// Incorrect: Hook order depends on the condition
if (isLoggedIn) {
 const [tab, setTab] = useState("overview");
}
```

Instead, call Hooks at the top level of the component and use conditions in the returned UI.

The setter function has a stable identity, so it usually does not need to be recreated or included as a changing dependency merely because the component rendered again.

## Real-World Examples

### Search Input

The query is state because it changes as the user types and determines the visible UI.

```jsx
function ClaimSearch({ claims }) {
 const [query, setQuery] = useState("");

 const visibleClaims = claims.filter(claim =>
 claim.reference.toLowerCase().includes(query.toLowerCase())
 );

 return (
 <>
 <label>
 Search claims
 <input
value={query}
onChange={event => setQuery(event.target.value)}
 />
 </label>

 <ul>
 {visibleClaims.map(claim => (
 <li key={claim.id}>{claim.reference}</li>
 ))}
 </ul>
 </>
 );
}
```

Here, the query is state. `visibleClaims` is derived from the current query and claim list, so it usually does not need its own state variable.

### Expandable Panel

A boolean state value can control whether details are visible:

```jsx
function BatchDetails({ batch }) {
 const [expanded, setExpanded] = useState(false);

 return (
 <section>
 <button
type="button"
aria-expanded={expanded}
onClick={() => setExpanded(open => !open)}
 >
 {expanded ? "Hide details" : "Show details"}
 </button>

 {expanded && <p>{batch.summary}</p>}
 </section>
 );
}
```

### Loading and Error Feedback

A component may use state to represent request status and render different UI for loading, success, or failure. When these values are closely related and many actions affect them, `useReducer` may be clearer than several independent `useState` calls.

State is the component’s current in-memory UI data; it is not automatically persisted to the server or browser storage. Applications must explicitly handle persistence, request failures, and stale data.

## Advantages and Disadvantages

**Advantages:**

- State lets the UI respond to user input and changing data.
- State updates use React’s rendering model instead of manual DOM updates.
- Local state keeps component-specific data near the UI that uses it.
- Functional updater forms make updates based on previous state reliable.
- State can be lifted to a parent or shared through context when multiple components need the same value.

**Disadvantages and common risks:**

- Too much state makes components harder to reason about.
- Duplicating derived values can create inconsistent data.
- Mutating objects or arrays can prevent updates from behaving as intended.
- Updates are scheduled, so treating setters as immediate assignments causes bugs.
- Event handlers can capture older render snapshots; functional updates help when computing from prior state.
- State stored at the wrong level can make components overly coupled or cause broader updates than necessary.
Updated todo list

Read-only Ask mode prevents me from editing `react.md`. Below is an expanded, ready-to-add section.

## 9. State Immutability

### 11. Why Should We Not Directly Modify State?

In React, **immutability** means treating state values as if they cannot be changed after they are created. When state needs to change, create and pass a new object or array that represents the updated value instead of editing the existing one.

Incorrect:

```jsx
user.name = "John";
setUser(user);
```

Preferred:

```jsx
setUser(previousUser => ({
 ...previousUser,
 name: "John"
}));
```

The first example changes the existing object and then passes the same object reference to React. React may use `Object.is` to determine that the new state is the same value as the current state and skip rendering work. Even when a render happens for another reason, other code may still hold a reference to that object and observe its changed contents unexpectedly.

The second example creates a new object. It retains the unchanged fields and gives React a new reference to the updated state. This makes the transition easier to reason about: the previous state remains a stable snapshot, and the new state describes the next version.

Immutability is a coding discipline; React does **not** automatically freeze every object in state or prevent mutations. The developer is responsible for not mutating state values.

### What React Is Doing Internally

When a state setter is called, React queues or schedules the requested update. During rendering, React processes the update and provides the component with the next state value.

If the new value is identical to the current value according to `Object.is`, React may skip rendering work for that update. For objects and arrays, reference identity is significant: modifying an object in place keeps the same reference, while creating a replacement produces a new reference.

Immutability also fits React’s render model. A render uses a snapshot of props and state. If an object from an earlier render is mutated later, that old snapshot is no longer reliable: earlier code may now observe values that did not exist when that render occurred. Keeping previous state unchanged makes rendering, debugging, and concurrent work more predictable.

React does not deeply compare every field in every state object to detect mutations. Nor does immutability mean that React performs no work when a new object is created. A new state reference schedules an update, after which React renders and reconciles the component output as needed.

### Shallow Copying and Nested State

The object spread operator creates a **shallow copy**. It copies the top-level properties, but nested objects remain shared references. Therefore, updating nested state requires copying each object along the path to the changed value.

Suppose state contains a user and an address:

```jsx
const [user, setUser] = useState({
 name: "Jordan",
 address: {
 city: "Boston",
 country: "US"
 }
});
```

This is incorrect because it mutates the existing nested object:

```jsx
user.address.city = "Chicago";
setUser({ ...user });
```

The outer object is new, but `user.address` was changed in place. Instead, copy both the outer object and the nested object:

```jsx
setUser(previousUser => ({
 ...previousUser,
 address: {
 ...previousUser.address,
 city: "Chicago"
 }
}));
```

Only the changed path needs to be copied. There is usually no need to deep-clone the entire state tree. Unchanged branches can be shared safely as long as they are not mutated.

## Updating Arrays in State

Array methods such as `push`, `pop`, `splice`, `sort`, and `reverse` mutate the array on which they are called. For state updates, prefer methods and patterns that return a new array.

### Add an Item

Incorrect:

```jsx
users.push(newUser);
setUsers(users);
```

Preferred:

```jsx
setUsers(previousUsers => [...previousUsers, newUser]);
```

### Remove an Item

Use `filter` to create a new array containing the items that should remain:

```jsx
setUsers(previousUsers =>
 previousUsers.filter(user => user.id !== id)
);
```

### Update an Item

Use `map` to return a new array, replacing only the matching item:

```jsx
setUsers(previousUsers =>
 previousUsers.map(user =>
 user.id === id
 ? { ...user, name: "John" }
 : user
 )
);
```

The matching user is copied with a new name. Other user objects can be reused because they are unchanged.

### Sort an Array

`sort()` mutates its array, so copy first:

```jsx
setUsers(previousUsers =>
 [...previousUsers].sort((first, second) =>
 first.name.localeCompare(second.name)
 )
);
```

This preserves the existing state array while producing a sorted replacement.

## Real-World Examples

### Editable Task List

A task list may allow users to complete, rename, or remove tasks. Each action should create a new array and, when updating a task, a new task object.

```jsx
function renameTask(taskId, name) {
 setTasks(previousTasks =>
 previousTasks.map(task =>
 task.id === taskId
 ? { ...task, name }
 : task
 )
 );
}

function removeTask(taskId) {
 setTasks(previousTasks =>
 previousTasks.filter(task => task.id !== taskId)
 );
}
```

This is predictable even when updates are queued, because each functional updater receives the latest pending state.

### Shopping Cart

When changing a product quantity, calculate a new cart array and replace the matching item:

```jsx
function updateQuantity(productId, quantity) {
 setItems(previousItems =>
 previousItems.map(item =>
 item.productId === productId
 ? { ...item, quantity }
 : item
 )
 );
}
```

The cart total can then be derived from the current items. This avoids storing a separate total that could fall out of sync if one update changes the items but forgets to update the total.

### Optimistic UI Update

When a user marks a notification as read, the UI can update immediately by creating a new array:

```jsx
setNotifications(previousNotifications =>
 previousNotifications.map(notification =>
 notification.id === notificationId
 ? { ...notification, read: true }
 : notification
 )
);
```

The application can then send the change to the server. If the request fails, it can restore a saved previous snapshot or display an error. Because the earlier array and objects were not mutated, keeping or restoring that snapshot is straightforward.

## Advantages of Immutable State Updates

-**Predictable snapshots:** Previous renders and saved values are not silently changed.
-**Reliable change detection:** New references make state transitions visible to React and to tools that use shallow comparisons.
-**Safer rendering:** Immutable values fit React’s model of calculating UI from a particular state snapshot.
-**Easier debugging:** Developers can inspect previous and next values without the previous value having changed underneath them.
-**Simpler undo and rollback:** Keeping prior state values makes history and optimistic-update rollback easier.
-**Works well with memoization:** Components and selectors can compare references to avoid some unnecessary work, when correctly designed.

## Trade-Offs and Common Mistakes

-**Copying has a cost:** Creating new arrays or objects allocates memory. For typical UI state this is usually a reasonable trade-off, but large, frequently updated structures may need profiling and a more suitable data model.
-**Shallow copies are not deep copies:** Spreading an outer object does not copy its nested objects. Copy the full path being changed.
-**Unnecessary deep cloning is wasteful:** Deep-copying the whole state for a small change can be expensive and discard useful reference sharing.
-**Mutation bugs can be subtle:** Methods such as `sort()` and `splice()` mutate arrays even when the code appears to be preparing a display result.
-**`const` does not make an object immutable:** It prevents reassignment of the variable, not changes to the object it references.
-**State is not automatically frozen:** Development tools or libraries may detect mutations, but React itself does not guarantee that state objects are frozen.

For deeply nested state, an established helper such as **Immer** can make immutable updates easier to write. It provides a draft-like API and produces immutable results, but the underlying state still needs to be treated as immutable. Use such a helper when it simplifies real complexity, not just to avoid learning object and array updates.

### Interview Summary

> I avoid mutating React state because React and surrounding code rely on stable snapshots and reference identity. Mutating an object in place can preserve its reference, make updates difficult to detect, and change data still referenced by an earlier render. Instead, I create a new object or array, copying each nested level along the path I change. I use functional updater forms when an update depends on previous state, and derive values rather than storing duplicate state whenever possible.
### Interview Summary

> `useState` is a Hook that lets a function component preserve a value between renders. Its setter schedules an update; React processes that update, renders with a new state snapshot, reconciles the output, and commits the required UI changes. Use a functional updater when the next value depends on the previous one, and replace objects or arrays immutably. Keep state minimal, derive values where possible, and do not expect a setter to change the current render’s variables immediately.

Updated todo list

Read-only Ask mode prevents me from editing `react.md`. Below is an expanded, ready-to-add section.

## 9. State Immutability

### 11. Why Should We Not Directly Modify State?

In React, **immutability** means treating state values as if they cannot be changed after they are created. When state needs to change, create and pass a new object or array that represents the updated value instead of editing the existing one.

Incorrect:

```jsx
user.name = "John";
setUser(user);
```

Preferred:

```jsx
setUser(previousUser => ({
 ...previousUser,
 name: "John"
}));
```

The first example changes the existing object and then passes the same object reference to React. React may use `Object.is` to determine that the new state is the same value as the current state and skip rendering work. Even when a render happens for another reason, other code may still hold a reference to that object and observe its changed contents unexpectedly.

The second example creates a new object. It retains the unchanged fields and gives React a new reference to the updated state. This makes the transition easier to reason about: the previous state remains a stable snapshot, and the new state describes the next version.

Immutability is a coding discipline; React does **not** automatically freeze every object in state or prevent mutations. The developer is responsible for not mutating state values.

### What React Is Doing Internally

When a state setter is called, React queues or schedules the requested update. During rendering, React processes the update and provides the component with the next state value.

If the new value is identical to the current value according to `Object.is`, React may skip rendering work for that update. For objects and arrays, reference identity is significant: modifying an object in place keeps the same reference, while creating a replacement produces a new reference.

Immutability also fits React’s render model. A render uses a snapshot of props and state. If an object from an earlier render is mutated later, that old snapshot is no longer reliable: earlier code may now observe values that did not exist when that render occurred. Keeping previous state unchanged makes rendering, debugging, and concurrent work more predictable.

React does not deeply compare every field in every state object to detect mutations. Nor does immutability mean that React performs no work when a new object is created. A new state reference schedules an update, after which React renders and reconciles the component output as needed.

### Shallow Copying and Nested State

The object spread operator creates a **shallow copy**. It copies the top-level properties, but nested objects remain shared references. Therefore, updating nested state requires copying each object along the path to the changed value.

Suppose state contains a user and an address:

```jsx
const [user, setUser] = useState({
 name: "Jordan",
 address: {
 city: "Boston",
 country: "US"
 }
});
```

This is incorrect because it mutates the existing nested object:

```jsx
user.address.city = "Chicago";
setUser({ ...user });
```

The outer object is new, but `user.address` was changed in place. Instead, copy both the outer object and the nested object:

```jsx
setUser(previousUser => ({
 ...previousUser,
 address: {
 ...previousUser.address,
 city: "Chicago"
 }
}));
```

Only the changed path needs to be copied. There is usually no need to deep-clone the entire state tree. Unchanged branches can be shared safely as long as they are not mutated.
## Updating Arrays in State

Array methods such as `push`, `pop`, `splice`, `sort`, and `reverse` mutate the array on which they are called. For state updates, prefer methods and patterns that return a new array.

### Add an Item

Incorrect:

```jsx
users.push(newUser);
setUsers(users);
```

Preferred:

```jsx
setUsers(previousUsers => [...previousUsers, newUser]);
```

### Remove an Item

Use `filter` to create a new array containing the items that should remain:

```jsx
setUsers(previousUsers =>
 previousUsers.filter(user => user.id !== id)
);
```

### Update an Item

Use `map` to return a new array, replacing only the matching item:

```jsx
setUsers(previousUsers =>
 previousUsers.map(user =>
 user.id === id
 ? { ...user, name: "John" }
 : user
 )
);
```

The matching user is copied with a new name. Other user objects can be reused because they are unchanged.

### Sort an Array

`sort()` mutates its array, so copy first:

```jsx
setUsers(previousUsers =>
 [...previousUsers].sort((first, second) =>
 first.name.localeCompare(second.name)
 )
);
```

This preserves the existing state array while producing a sorted replacement.

## Real-World Examples

### Editable Task List

A task list may allow users to complete, rename, or remove tasks. Each action should create a new array and, when updating a task, a new task object.

```jsx
function renameTask(taskId, name) {
 setTasks(previousTasks =>
 previousTasks.map(task =>
 task.id === taskId
 ? { ...task, name }
 : task
 )
 );
}

function removeTask(taskId) {
 setTasks(previousTasks =>
 previousTasks.filter(task => task.id !== taskId)
 );
}
```

This is predictable even when updates are queued, because each functional updater receives the latest pending state.

### Shopping Cart

When changing a product quantity, calculate a new cart array and replace the matching item:

```jsx
function updateQuantity(productId, quantity) {
 setItems(previousItems =>
 previousItems.map(item =>
 item.productId === productId
 ? { ...item, quantity }
 : item
 )
 );
}
```

The cart total can then be derived from the current items. This avoids storing a separate total that could fall out of sync if one update changes the items but forgets to update the total.

### Optimistic UI Update

When a user marks a notification as read, the UI can update immediately by creating a new array:

```jsx
setNotifications(previousNotifications =>
 previousNotifications.map(notification =>
 notification.id === notificationId
 ? { ...notification, read: true }
 : notification
 )
);
```

The application can then send the change to the server. If the request fails, it can restore a saved previous snapshot or display an error. Because the earlier array and objects were not mutated, keeping or restoring that snapshot is straightforward.

## Advantages of Immutable State Updates

-**Predictable snapshots:** Previous renders and saved values are not silently changed.
-**Reliable change detection:** New references make state transitions visible to React and to tools that use shallow comparisons.
-**Safer rendering:** Immutable values fit React’s model of calculating UI from a particular state snapshot.
-**Easier debugging:** Developers can inspect previous and next values without the previous value having changed underneath them.
-**Simpler undo and rollback:** Keeping prior state values makes history and optimistic-update rollback easier.
-**Works well with memoization:** Components and selectors can compare references to avoid some unnecessary work, when correctly designed.

## Trade-Offs and Common Mistakes

-**Copying has a cost:** Creating new arrays or objects allocates memory. For typical UI state this is usually a reasonable trade-off, but large, frequently updated structures may need profiling and a more suitable data model.
-**Shallow copies are not deep copies:** Spreading an outer object does not copy its nested objects. Copy the full path being changed.
-**Unnecessary deep cloning is wasteful:** Deep-copying the whole state for a small change can be expensive and discard useful reference sharing.
-**Mutation bugs can be subtle:** Methods such as `sort()` and `splice()` mutate arrays even when the code appears to be preparing a display result.
-**`const` does not make an object immutable:** It prevents reassignment of the variable, not changes to the object it references.
-**State is not automatically frozen:** Development tools or libraries may detect mutations, but React itself does not guarantee that state objects are frozen.

For deeply nested state, an established helper such as **Immer** can make immutable updates easier to write. It provides a draft-like API and produces immutable results, but the underlying state still needs to be treated as immutable. Use such a helper when it simplifies real complexity, not just to avoid learning object and array updates.

### Interview Summary

> I avoid mutating React state because React and surrounding code rely on stable snapshots and reference identity. Mutating an object in place can preserve its reference, make updates difficult to detect, and change data still referenced by an earlier render. Instead, I create a new object or array, copying each nested level along the path I change. I use functional updater forms when an update depends on previous state, and derive values rather than storing duplicate state whenever possible.

Updated todo list

Read-only Ask mode prevents me from editing `react.md`. The expanded, ready-to-add section below covers React event handling, its internals, historical context, alternatives, and current practices.

## 10. Event Handling

### 12. How Does Event Handling Work in React?

React event handling lets a component respond to user interactions such as clicks, typing, form submissions, focus changes, and keyboard input. Event handlers are supplied as JSX props, usually with camelCase names such as `onClick` and `onChange`.

```jsx
function App() {
 function handleClick() {
 console.log("Clicked");
 }

 return (
 <button type="button"onClick={handleClick}>
 Click
 </button>
 );
}
```

The important detail is that `handleClick` is **passed as a function reference**. React calls it when the button is clicked.

#### Pass a Function; Do Not Call It During Rendering

Correct:

```jsx
<button onClick={handleClick}>Click</button>
```

Incorrect for a click handler:

```jsx
<button onClick={handleClick()}>Click</button>
```

`handleClick()` calls the function immediately while React is rendering the component. Its return value, rather than the function, is then assigned to `onClick`. If the function updates state, it can cause repeated rendering or an infinite render loop.

A function may be called directly during render when the goal is to calculate a value, but an event handler prop needs a function that React can call later.

### Passing Parameters

To pass a parameter, create a function that React can call when the event happens:

```jsx
function UserRow({ user, onDelete }) {
 return (
 <button
type="button"
onClick={() => onDelete(user.id)}
 >
 Delete {user.name}
 </button>
 );
}
```

The arrow function is created during rendering but calls `onDelete` only after the click. It closes over the current `user.id`.

React also passes an event object to the handler. If both an application value and the event are needed, accept the event in the wrapper and pass both explicitly:

```jsx
<button
onClick={event => handleDelete(user.id, event)}
>
 Delete
</button>
```

### What Is the Event Object?

A React event handler receives an event object that provides information about the interaction. Commonly used properties and methods include:

- `event.target`: The element where the event originated.
- `event.currentTarget`: The element whose handler is currently running.
- `event.preventDefault()`: Prevents the browser’s default action when appropriate.
- `event.stopPropagation()`: Stops the event from continuing through the propagation path.

For example, submitting a form normally causes the browser to navigate or reload the page. A client-side form can prevent that default behavior:

```jsx
function SearchForm() {
 function handleSubmit(event) {
 event.preventDefault();
 // Read the form values or start the search.
 }

 return (
 <form onSubmit={handleSubmit}>
 <input name="query" />
 <button type="submit">Search</button>
 </form>
 );
}
```

Use the form’s `onSubmit` handler rather than handling only a button click. That also supports submitting with the keyboard.

## How React Processes Events Internally

In ordinary browser code, a developer might register a listener directly on an element with `addEventListener`. React provides a declarative alternative: the JSX describes which function should respond to an event on a rendered element.

A simplified event path is:

```text
Browser event occurs
 ↓
React's event system receives it
 ↓
React determines the relevant handler and propagation path
 ↓
The handler runs with an event object
 ↓
State updates are scheduled and may be batched
 ↓
React renders and commits resulting UI changes
```

Modern React generally uses **event delegation** for many events: React listens at the root container and uses the event’s path through the DOM to find the applicable React handlers. This avoids needing an independent native listener for every JSX handler. Not every event is handled identically internally, so delegation is a useful high-level model rather than a guarantee about every event type.

React also exposes a **SyntheticEvent** interface that presents a consistent event API across supported environments. It wraps or relates to the underlying browser event, so properties such as `event.nativeEvent` can provide access to that native event when needed. Most application code should use the standard React event properties instead.

Older React versions pooled event objects, which meant an event’s properties could be cleared after the handler returned. Event pooling was removed in React 17, so current React code does not need to call `event.persist()` for that reason. Async code should still capture the specific values it needs; that makes the logic clearer and avoids depending on a changing DOM target.

### Event Propagation

Browser events often **bubble** from the target element up through its ancestors. React handlers generally participate in bubbling in a similar way.

```jsx
<div onClick={() => console.log("Container clicked")}>
 <button onClick={() => console.log("Button clicked")}>
 Select
 </button>
</div>
```

Clicking the button normally invokes the button handler and then the container handler. If the button interaction should not trigger the parent behavior, the child can stop propagation:

```jsx
<button
onClick={event => {
 event.stopPropagation();
 selectItem();
 }}
>
 Select
</button>
```

Use this carefully. Stopping propagation can make interactions harder to understand if a page relies on parent handlers. React also supports capture-phase event props, such as `onClickCapture`, for cases where an ancestor needs to observe an event before it reaches the target’s bubbling handlers.

### Controlled Inputs

React event handling is often combined with **controlled inputs**, where the displayed input value comes from React state and an event handler updates that state.

```jsx
function SearchBox() {
 const [query, setQuery] = useState("");

 return (
 <input
value={query}
onChange={event => setQuery(event.target.value)}
aria-label="Search"
 />
 );
}
```

This creates a clear data flow:

```text
User types
→ onChange runs
→ state update is scheduled
→ component renders with the new query
→ input displays the updated value
```

An input with a `value` prop should have an appropriate change handler; otherwise, it may be read-only. For large forms, teams may choose a form library or use uncontrolled inputs with refs, depending on the requirements.

## Before React and Alternative Approaches

Before declarative UI libraries were common, developers often used inline HTML handlers or manually attached DOM listeners:

```html
<button onclick="deleteUser(42)">Delete</button>
```

or:

```js
button.addEventListener("click", handleClick);
```

With manual DOM handling, code had to find the right elements, attach listeners, and update the DOM when data changed. In a large interactive interface, keeping those updates consistent could be difficult. React’s event props keep the handler alongside the component that describes the relevant UI, while React manages its relationship to the rendered tree.

Direct `addEventListener` remains appropriate for some browser APIs and events outside React’s rendered tree. When using it in a component, install and remove the listener in an effect so the listener does not outlive the component or become duplicated:

```jsx
useEffect(() => {
 function handleKeyDown(event) {
 if (event.key === "Escape") {
 closeDialog();
 }
 }

 window.addEventListener("keydown", handleKeyDown);
 return () => window.removeEventListener("keydown", handleKeyDown);
}, [closeDialog]);
```

For normal interactions on elements rendered by React, prefer JSX event props. Use native listeners when you genuinely need an event source React does not manage directly, such as a window-level browser event or a third-party DOM integration.

## Real-World Examples

**Delete action in a table:** Pass a stable record ID to a handler. The handler can request deletion, while the parent updates the records state after success or applies an optimistic update with rollback behavior.

**Modal dialog:** A button opens the dialog by updating state. The dialog can handle Escape through a keyboard event and stop clicks inside the dialog from triggering a backdrop’s click-to-close behavior. It should also consider focus management and accessible dialog semantics.

**Form submission:** Use `onSubmit`, call `preventDefault()` when handling the submission in the client, validate the data, display errors, and report loading or success state. The server must still validate the submitted data.

**Expandable table row:** A click can toggle row details through state. For keyboard accessibility, use a real button rather than making a plain `<div>` act like a button.

## Advantages and Trade-Offs

**Advantages:**

- Event behavior is declared near the UI it affects.
- React’s event system provides a consistent API and integrates updates with rendering.
- Event handlers can receive current props and state from the render that created them.
- State updates from handlers can be batched, reducing unnecessary renders.
- Components can receive callbacks as props, making responsibilities and interactions explicit.

**Disadvantages and common pitfalls:**

- Calling a handler during render instead of passing it can trigger unwanted work or repeated updates.
- Closures capture values from a render; an async callback may need careful handling if it relies on changing values.
- Inline wrappers create functions during rendering, though this alone is usually not a problem and should not automatically prompt memoization.
- Stopping propagation can hide event flow and make parent-child interactions harder to debug.
- Browser defaults can be useful; calling `preventDefault()` unnecessarily can harm expected behavior or accessibility.
- React event handling does not replace keyboard support, semantic HTML, input validation, or server-side security checks.

### Interview Summary

> In React, event handlers are passed as function references through JSX props such as `onClick` and `onChange`. React’s event system receives the browser event, invokes the appropriate handler, and integrates resulting state updates with React’s render and commit process. I pass callbacks rather than calling them during render, use wrapper functions when passing parameters, understand event propagation, and use `preventDefault()` only when replacing a browser default action. For normal UI interactions I use JSX handlers; direct DOM listeners are reserved for external browser events or integrations.

Updated todo list

Read-only Ask mode prevents me from editing `react.md`. Below is an expanded, ready-to-add section.

## 11. Conditional Rendering

### 13. How Do You Conditionally Render Components?

**Conditional rendering** means showing different UI depending on the current props, state, or other data. React uses ordinary JavaScript expressions to decide what a component returns. For example, an application might display a sign-in form for a logged-out visitor and a dashboard after sign-in.

```jsx
function App({ isLoggedIn }) {
 return isLoggedIn ? <Dashboard /> : <Login />;
}
```

This is a declarative approach: describe the UI that corresponds to the current condition and let React update the rendered result when that condition changes.

Before libraries like React became common, developers often wrote imperative code to find DOM elements, hide or remove one, and insert another. That approach can become difficult to maintain when many parts of a page depend on the same changing data. In React, the condition is part of the component’s render logic, and React manages reconciling the old and new output.

### Common Approaches

#### 1. `if` Statements and Early Returns

Use an `if` statement when the component has distinct states or the logic is clearer outside JSX:

```jsx
function ClaimResults({ isLoading, error, claims }) {
 if (isLoading) {
 return <p>Loading claims...</p>;
 }

 if (error) {
 return <p role="alert">Could not load claims: {error}</p>;
 }

 if (claims.length === 0) {
 return <p>No claims found.</p>;
 }

 return <ClaimTableclaims={claims} />;
}
```

This pattern is easy to scan when loading, error, empty, and success states need different layouts. Each early return gives one branch a clear purpose.

#### 2. Ternary Operator

A ternary is useful when choosing between two compact alternatives:

```jsx
function AccountPage({ isLoggedIn }) {
 return isLoggedIn
 ? <Dashboard />
 : <Login />;
}
```

For more complex branches, nested ternaries can become hard to read. Prefer early returns or extract a helper component rather than building a long chain of nested expressions.

#### 3. Logical `&&`

Use `&&` when an element should appear only if a condition is true:

```jsx
function AdminTools({ isAdmin }) {
 return (
 <section>
 <h1>Account</h1>
 {isAdmin && <AdminPanel />}
 </section>
 );
}
```

JavaScript’s `&&` returns an operand, not strictly a Boolean. When the left-hand side is truthy, it returns the right-hand side; when it is falsy, it returns the left-hand side. React ignores `false`, `null`, and `undefined` as children, but it can render a number or string.

That is why this can show an unexpected `0`:

```jsx
{count && <NotificationList />}
```

If `count` is `0`, the expression evaluates to `0`, and React renders that number. Use a Boolean comparison instead:

```jsx
{count > 0 && <NotificationList />}
```

Or explicitly convert the condition to a Boolean:

```jsx
{Boolean(count) && <NotificationList />}
```

#### 4. Render Nothing with `null`

A component can return `null` when there is no UI to display:

```jsx
function Warning({ visible, message }) {
 if (!visible) {
 return null;
 }

 return <p role="alert">{message}</p>;
}
```

Returning `null` is different from rendering an empty `<div>`: it produces no corresponding DOM element.

#### 5. Store the Result in a Variable

For a larger condition, calculate the element before returning the main JSX:

```jsx
function DeliveryStatus({ status }) {
 let statusView;

 switch (status) {
 case "loading":
 statusView = <p>Checking delivery...</p>;
 break;
 case "failed":
 statusView = <p role="alert">Delivery failed.</p>;
 break;
 case "sent":
 statusView = <p>Delivery sent.</p>;
 break;
 default:
 statusView = <p>Status unavailable.</p>;
 }

 return <section>{statusView}</section>;
}
```

This can be clearer than placing a complex `switch` or multiple nested conditions inside JSX.

### How Conditional Rendering Works Internally

Conditional rendering is ordinary JavaScript evaluation during React’s render phase:

1. React calls a component with its current props and state.
2. JavaScript evaluates conditions, such as `isLoggedIn ? ... : ...`.
3. The component returns React elements, `null`, or another renderable value.
4. React reconciles that result against the previous output.
5. React commits any needed DOM insertions, removals, or updates.

Suppose the previous render returned `<Login />` and the next returns `<Dashboard />`. React sees different component types and generally removes the old component subtree and mounts the new one. Local state in the removed subtree is discarded, and its effect cleanup runs. The newly mounted component starts with its own initial state.

This is important when switching between components. If both branches render the same component type in the same position, React may preserve its state. Changing a component’s `key` intentionally tells React to treat it as a different instance and reset its state.

Hooks must still be called at the top level of a function component. Do not call Hooks conditionally:

```jsx
// Incorrect: Hook order changes depending on the condition
if (isLoggedIn) {
 const [tab, setTab] = useState("overview");
}
```

It is fine to conditionally render a component that uses Hooks:

```jsx
{isLoggedIn && <Dashboard />}
```

React controls when that component is rendered; the Hook is still called at the top level of `Dashboard`.

### Conditional Rendering vs Hiding with CSS

These approaches are not equivalent:

```jsx
{isOpen && <DetailsPanel />}
```

```jsx
<div style={{ display: isOpen ? "block" : "none" }}>
 <DetailsPanel />
</div>
```

In the first example, when `isOpen` is false, `DetailsPanel` is removed from the rendered tree. Its local state is discarded, and effect cleanup runs.

In the second example, the component remains mounted but is visually hidden. Its state is preserved, and its effects may continue running. This can be useful when preserving a partially completed form, but wasteful or incorrect if a hidden component continues polling or listening for events. Choose based on whether the component should remain active while hidden.

### Real-World Examples

**Authentication UI:** Render a sign-in view or an account view based on authentication status. Hiding an admin button is not security: authorization must also be enforced by the server and protected APIs.

**Search results:** Render a loading indicator while a request is pending, an error message after failure, an empty state when no results match, and a result list when data is available. These states should be designed intentionally rather than leaving the page blank.

**Notification count:** Display a badge only when there are unread notifications. Use `{unreadCount > 0 && <Badge count={unreadCount} />}` so a zero count does not appear as stray text.

**Feature availability:** A feature flag can determine whether a section appears. If the feature is unavailable, consider whether omitting it is sufficient or whether the user needs an explanatory message.

**Form workflow:** A multi-step form can render one step at a time. Decide whether switching steps should preserve each step’s values. State owned by a persistent parent can survive conditional removal; state owned only inside an unmounted step will be reset.

### Advantages and Trade-Offs

**Advantages:**

- The UI directly reflects the current state and permissions.
- Early returns make loading, error, empty, and success cases explicit.
- React manages adding and removing elements during reconciliation.
- Conditional component boundaries can keep individual UI states focused.

**Trade-offs and common mistakes:**

- Deeply nested ternaries and conditions are hard to read and test.
- Using `count && ...` can render `0`; use a Boolean comparison.
- Removing a component resets its local state and runs effect cleanup.
- Hiding with CSS preserves component state and effects, which may be undesirable.
- Hiding restricted UI does not replace server-side authorization.
- Conditional branches that switch component types can cause remounts and discard local state.

React’s `Suspense` is a related feature for displaying fallback UI while supported code or data is loading. It is not a general replacement for ordinary conditions; use explicit state-based rendering when the application owns the loading and error state.

### Interview Summary

> Conditional rendering uses JavaScript conditions to return different React elements based on props or state. I use early returns for distinct loading, error, and empty states; ternaries for a choice between two views; and `&&` for optional content, checking numeric conditions explicitly to avoid rendering `0`. React evaluates the branch during rendering, reconciles it with the previous output, and commits DOM changes. I also consider whether a branch should unmount and reset state or remain mounted but hidden, and never treat hiding UI as authorization.

Updated todo list

Read-only Ask mode prevents me from editing `react.md`. Here is an expanded, ready-to-add section.

## 12. Lists and Keys

### 14. Why Are Keys Required?

React uses **keys** to identify elements among siblings when rendering a list. A key tells React which rendered item corresponds to which data item across renders. With that identity, React can determine whether an item is new, removed, retained, or moved.

```jsx
function UserList({ users }) {
 return (
 <ul>
 {users.map(user => (
 <Userkey={user.id} user={user} />
 ))}
 </ul>
 );
}
```

Here, `user.id` is a stable identifier for each user. If the list changes, React can match each `<User>` element to the same user in the previous render, even if that user has moved to another position.

Keys should be **unique among siblings** and **stable across renders**. They do not need to be globally unique across the application; the same key can be used in separate, unrelated lists.
### Background: Why Identity Matters

Before declarative UI libraries were widely used, developers often changed lists by manually adding, deleting, or moving DOM elements. The application code had to keep those DOM nodes synchronized with its data.

React lets a component describe the list it wants to render. When the data changes, React reconciles the new descriptions with the previous ones. Keys provide the identity information React needs to match list items rather than relying only on their positions.

For example, consider a list of editable user rows. Each row has an input with local state. If a user is inserted at the beginning and React identifies rows only by their positions, state from one row may appear associated with a different user. Stable keys let React associate the row component and its state with the correct user.

### What Happens Internally?

At a high level, React compares the new list of elements with the previous list:

1. React receives new props or state and renders the list again.
2. It examines sibling elements, including their types and keys.
3. It matches elements whose identity is compatible and marks others as insertions, removals, or moves.
4. It commits the required changes to the UI.

Keys are one of React’s signals for identity; they do not direct React to perform a particular DOM operation. Exact reconciliation details are implementation behavior, not an application-level contract.

For example, if the previous list is:

```text
A (key="a"), B (key="b"), C (key="c")
```

and the next list is:

```text
C (key="c"), A (key="a"), B (key="b")
```

React can recognize that the same three logical items remain, although their order changed. The key gives it a reliable way to match them.

A key is a **special React prop**. React consumes it during reconciliation; the child component does not receive it as `props.key`. Pass the ID separately when the component needs it:

```jsx
<Userkey={user.id} userId={user.id} user={user} />
```

### Why Can Array Indexes Be Problematic?

An array index describes an item’s current position, not its identity. Consider a list keyed by index:

```jsx
users.map((user, index) => (
 <Userkey={index} user={user} />
))
```

If a new user is inserted at the start, the items’ indexes all change. React may associate an existing component instance with a different user. This can produce confusing results when rows contain local state, input values, focus, or animations.

Use a stable ID when the list can be:

- Reordered or sorted.
- Filtered.
- Inserted into or deleted from.
- Updated while items have local component state.

An index key can be acceptable when the list is static, never reordered or edited, and its items do not have state that needs to follow a logical item. A stable identifier is still the clearer default when one is available.

### Choosing a Good Key

**Use a stable ID from the data** when possible:

```jsx
users.map(user => (
 <Userkey={user.id} user={user} />
))
```

If a record has no single unique field, a composite key can work when its parts are stable and together uniquely identify the item:

```jsx
rows.map(row => (
 <Rowkey={`${row.accountId}:${row.period}`} row={row} />
))
```

If the application must generate an ID, generate it when the data item is created and keep it with that item. Do not generate it during rendering.

Avoid keys that change on every render:

```jsx
users.map(user => (
 <Userkey={Math.random()} user={user} />
))
```

A different key tells React that the old component is no longer the same instance. React will generally unmount it and mount a new one. This can discard local state, run effect cleanup and setup, reset input fields, and cause unnecessary work.

### Lists with Fragments

Sometimes each data item produces several sibling elements. Use an explicit Fragment with a key:

```jsx
import { Fragment } from "react";

function ContactList({ contacts }) {
 return contacts.map(contact => (
 <Fragmentkey={contact.id}>
 <h2>{contact.name}</h2>
 <p>{contact.email}</p>
 </Fragment>
 ));
}
```

The short Fragment syntax `<>...</>` cannot take a key. Use `<Fragment key={...}>` for keyed list items that need a Fragment.

### Real-World Examples

**Editable admin table:** Use a stable record ID as the row key. If the table sorts by status or date, each row remains associated with its record and local input state.

**Search results:** Use a result ID rather than the result’s position. Filtering changes which items are visible and where they appear; IDs preserve the identity of the remaining results.

**Notification feed:** Use the notification ID when new messages arrive at the top. This helps React distinguish a new notification from older notifications that shift down.

**Multi-step form:** A key can intentionally reset a form when the selected account changes:

```jsx
<AccountFormkey={account.id} account={account} />
```

Changing the key tells React to treat the form as a new component instance, resetting its local state. Use this deliberately: if the user should retain unsaved edits while switching views, resetting may be harmful.

### Advantages and Trade-Offs

**Advantages:**

- Keys give React a stable way to match list items across renders.
- They help preserve local component state with the correct data item during reordering.
- They make insertions and removals easier for React to reconcile accurately.
- Stable identity supports predictable behavior in editable lists, tables, and animated sequences.

**Common mistakes and trade-offs:**

- Index keys can associate component state with the wrong item when a list changes.
- Random or changing keys force remounts and can discard local state.
- Duplicate keys among siblings make identity ambiguous and can lead to incorrect behavior.
- Keys do not automatically make rendering faster or eliminate the need to measure performance.
- Keys are not available as regular props inside the child component.

### Interview Summary

> Keys give React stable identity for elements within a sibling list. During reconciliation, React uses them to match items across renders, so inserted, removed, or reordered data does not accidentally inherit another item’s component state. I prefer stable IDs from the data, avoid index keys for mutable lists, and never generate keys during rendering. Keys are consumed by React and are not passed to the component as ordinary props.

Updated todo list

Read-only Ask mode prevents me from editing `react.md`. Here is an expanded, ready-to-add section with a more precise explanation of effects and their trade-offs.

## 13. `useEffect`

### 15. What Is `useEffect()`?

`useEffect` is a React Hook for **synchronizing a component with an external system**. An external system is something React does not manage as part of rendering, such as a network request, browser API, timer, event listener, or subscription.

```jsx
import { useEffect } from "react";

function PageTitle({ title }) {
 useEffect(() => {
 document.title = title;
 }, [title]);

 return <h1>{title}</h1>;
}
```

Here, the component renders the heading, while the effect synchronizes the browser tab title with the `title` prop.

Effects are **not** a general-purpose place for all code that should run after rendering. User-triggered actions, such as submitting a form in response to a click, usually belong in an event handler. Values that can be calculated from props or state should usually be calculated during rendering rather than copied into state through an effect.

### Basic Syntax

```jsx
useEffect(() => {
 // Set up synchronization with an external system.

 return () => {
 // Clean up that synchronization, if needed.
 };
}, [dependencies]);
```

The effect has three pieces:

-**Setup function:** Runs after React commits the rendered UI. It performs the synchronization.
-**Cleanup function:** Optional. It reverses or ends the synchronization started by setup.
-**Dependency array:** Tells React which reactive values the effect reads, so React knows when to synchronize again.

The setup function may return a cleanup function. It should **not** be declared `async`, because an async function returns a Promise, but React expects either a cleanup function or no return value. For asynchronous work, define an async function inside the effect and call it.

## When Does an Effect Run?

React renders the component, commits the resulting changes, and then runs its effects. The precise timing relative to browser painting can vary; passive effects commonly run after paint, but code should not depend on that timing for visual layout work.

On a later render, React compares each dependency with its previous value using `Object.is`. If a dependency changed, React first runs the previous cleanup, then runs the new setup. When the component is removed, React runs the final cleanup.

```text
Render
 ↓
Commit UI changes
 ↓
Run effect setup
 ↓
Dependency changes?
 ├── No: keep the current synchronization
 └── Yes: run old cleanup, then new setup
 ↓
Component removed: run final cleanup
```

An effect should describe one synchronization process. For example, an effect that subscribes to a selected account’s updates should clean up the old account’s subscription and establish one for the new account.

### The Dependency Array

#### No Dependency Array

```jsx
useEffect(() => {
 console.log("Effect ran after a commit");
});
```

With no dependency array, React runs the effect after every commit of this component. If the effect updates state on every run, it can cause a render loop.

#### Empty Dependency Array

```jsx
useEffect(() => {
 console.log("Effect ran after the initial client mount");
}, []);
```

An empty array means the effect has no reactive dependencies to watch. In normal client-side use, the setup runs after the initial mount, and cleanup runs when the component unmounts.

It is more precise to understand `[]` as “this effect reads no reactive values that should cause it to resynchronize,” rather than as a universal “run exactly once” guarantee. A component can unmount and mount again, and React Strict Mode in development may perform an extra setup-cleanup cycle to expose missing cleanup. Effects also do not run during server rendering.

#### Dependencies Listed

```jsx
useEffect(() => {
 const connection = subscribeToAccount(accountId);

 return () => connection.unsubscribe();
}, [accountId]);
```

When `accountId` changes, React cleans up the subscription for the previous ID and establishes the new one. Include reactive values used by the effect, such as props, state, and functions or variables declared in the component. The React Hooks lint rule helps identify missing dependencies; suppressing it without understanding the cause can create stale-value bugs.

Avoid creating a new object or function during every render and then using it as a dependency unless the effect genuinely needs that identity. Since a new object is a different reference, it can cause the effect to rerun each render. Prefer moving the object creation inside the effect or using stable inputs.

## Real-World Examples

### Subscription with Cleanup

```jsx
import { useEffect, useState } from "react";

function BatchStatus({ batchId }) {
 const [status, setStatus] = useState("Loading");

 useEffect(() => {
 const subscription = subscribeToBatch(batchId, setStatus);

 return () => {
 subscription.unsubscribe();
 };
 }, [batchId]);

 return <p>Batch status: {status}</p>;
}
```

When `batchId` changes, React removes the old subscription before setting up the new one. When the component unmounts, it unsubscribes. Cleanup prevents the old subscription from continuing to send updates.

### Fetching Data

For a simple client-side request, use cleanup to prevent an outdated request from updating the component after its input changes or it unmounts:

```jsx
function ClaimDetails({ claimId }) {
 const [claim, setClaim] = useState(null);
 const [error, setError] = useState(null);

 useEffect(() => {
 const controller = new AbortController();

 async function loadClaim() {
 try {
 const response = await fetch(`/api/claims/${claimId}`, {
 signal: controller.signal
 });

 if (!response.ok) {
 throw new Error(`Request failed: ${response.status}`);
 }

 const result = await response.json();
 setClaim(result);
 } catch (requestError) {
 if (requestError.name !== "AbortError") {
 setError(requestError.message);
 }
 }
 }

 loadClaim();

 return () => controller.abort();
 }, [claimId]);

 if (error) return <p role="alert">{error}</p>;
 if (!claim) return <p>Loading claim...</p>;

 return <h2>{claim.reference}</h2>;
}
```

For larger applications, data fetching is often better handled by framework data-loading features or a server-state library. Those tools can address caching, request deduplication, retries, and stale data more comprehensively than hand-written effects.

### Timer or Browser Event

Effects can also manage browser resources:

```jsx
useEffect(() => {
 const timerId = window.setInterval(() => {
 refreshStatus();
 }, 30_000);

 return () => window.clearInterval(timerId);
}, [refreshStatus]);
```

If `refreshStatus` changes identity on every render, this effect may repeatedly clear and recreate the timer. Ensure the dependency represents a stable function or structure the synchronization so the timer behaves as intended.

## Historical Context and Related Concepts

Class components used lifecycle methods such as `componentDidMount`, `componentDidUpdate`, and `componentWillUnmount` to start, update, and clean up synchronization. Function components do not have those methods. `useEffect` lets function components express setup and cleanup together, tied to the values that determine the synchronization.

This is not a direct one-to-one mapping. An effect is best understood as “keep this external system synchronized with these React values.” For example, a subscription effect depends on the current account ID. This model is often clearer than thinking “run code after mount” or “run code after every update.”

For a visual change that must happen after DOM mutations but before the browser paints, `useLayoutEffect` may be appropriate. It can block painting, so use it only when visual measurement or adjustment requires that timing. For ordinary subscriptions, requests, and browser synchronization, prefer `useEffect`.

In development Strict Mode, React may run an effect’s setup, cleanup, and setup again to reveal missing cleanup or assumptions that setup happens exactly once. A robust effect should be safe to start and stop in this way.

## Advantages and Trade-Offs

**Advantages:**

- Keeps external synchronization tied to the component inputs that control it.
- Provides a clear place to set up and clean up subscriptions, listeners, and timers.
- Re-runs synchronization when relevant dependencies change.
- Supports function components without class lifecycle methods.

**Common risks:**

- Missing dependencies can leave an effect using stale values.
- Unnecessary dependencies can cause repeated setup and cleanup.
- Missing cleanup can leak listeners, subscriptions, or timers.
- Using effects to derive state can create extra renders and synchronization bugs.
- Hand-written fetching can omit caching, cancellation, deduplication, or race handling.
- Effects run only on the client, so they cannot provide data during server rendering.

### Interview Summary

> `useEffect` synchronizes a component with external systems after React commits its UI. Its dependency array determines when React must resynchronize; when dependencies change, React runs the previous cleanup before the next setup, and it runs cleanup on unmount. I use effects for subscriptions, browser APIs, and similar synchronization—not for calculations that can happen during render or work that belongs directly in an event handler. I include the reactive dependencies and clean up resources to prevent stale behavior and leaks.

Updated todo list

Read-only Ask mode prevents me from editing `react.md`. Here is an expanded, ready-to-add section.

## 14. `useEffect` Cleanup

### 16. Why Do We Need Cleanup?

An effect can start work or establish a connection outside React, such as registering an event listener, starting a timer, or subscribing to a data source. The **cleanup function** reverses that setup when it is no longer needed.

Without cleanup, an old listener or subscription can remain active after the component is removed or its inputs change. That can lead to duplicate callbacks, unnecessary work, stale data updates, or resources staying reachable longer than intended.

```jsx
import { useEffect } from "react";

function ScrollPositionLogger() {
 useEffect(() => {
 function handleScroll() {
 console.log(window.scrollY);
 }

 window.addEventListener("scroll", handleScroll);

 return () => {
 window.removeEventListener("scroll", handleScroll);
 };
 }, []);

 return <p>Scroll the page and check the console.</p>;
}
```

The effect adds the listener. The returned function removes that same listener. The handler is declared inside the effect so setup and cleanup use the same function reference.

### When Does Cleanup Run?

React runs an effect’s cleanup:

1.**Before rerunning the effect because a dependency changed.** React cleans up the previous synchronization, then runs the new setup.
2.**When the component is removed.** React cleans up resources that should not remain active after unmounting.
3.**In development Strict Mode, during an additional setup-and-cleanup check.** This helps expose effects that do not clean up correctly.

For example, when `roomId` changes, a chat component should disconnect from the old room before connecting to the new one:

```jsx
useEffect(() => {
 const connection = connectToRoom(roomId);
 connection.connect();

 return () => {
 connection.disconnect();
 };
}, [roomId]);
```

When `roomId` changes, React runs `disconnect()` for the previous effect instance, then establishes the connection for the new room. If the component unmounts, React disconnects the active room.

Cleanup does **not** run before every render. If the dependencies have not changed, React normally leaves the current effect setup in place. With no dependency array, React reruns the effect after every commit, cleaning up the previous setup first. With an empty array, the effect has no reactive dependencies and usually runs after the initial client-side mount; cleanup runs when that component instance is removed. Development Strict Mode adds an extra check, so `[]` should not be interpreted as an unconditional “exactly once” promise.

### How Cleanup Works Internally

`useEffect` runs after React commits the rendered UI. React stores information about the effect and its dependencies as part of the component’s Hook data. On a later commit, React compares the current dependencies with the previous ones using `Object.is`.

If a dependency changed, React runs the previous cleanup before running the new setup. When the component unmounts, it runs the final cleanup. This gives an effect a lifecycle tied to its synchronization inputs rather than just to the component as a whole.

A useful model is:

```text
Effect setup establishes synchronization
 ↓
Dependencies stay the same: keep it active
 ↓
Dependency changes: clean up old setup, establish new one
 ↓
Component unmounts: clean up active setup
```

Cleanup should reverse or release what the corresponding setup created. It should generally not be used to set state simply because an effect is ending; state updates during cleanup often indicate that the state or synchronization logic should be redesigned.

## Common Cleanup Examples

### Timers

Timers continue running until they are cleared:

```jsx
useEffect(() => {
 const timerId = window.setInterval(() => {
 refreshBatchStatus();
 }, 30_000);

 return () => {
 window.clearInterval(timerId);
 };
}, [refreshBatchStatus]);
```

If `refreshBatchStatus` changes identity on every render, the effect may repeatedly clear and restart the timer. Ensure that effect dependencies reflect actual synchronization inputs, and avoid suppressing dependency warnings without understanding the resulting behavior.

### Fetch Requests

A request may finish after its component has unmounted or after the user has switched to a different record. An `AbortController` can cancel the previous request during cleanup:

```jsx
useEffect(() => {
 const controller = new AbortController();

 async function loadClaim() {
 try {
 const response = await fetch(`/api/claims/${claimId}`, {
 signal: controller.signal
 });

 if (!response.ok) {
 throw new Error(`Request failed: ${response.status}`);
 }

 const claim = await response.json();
 setClaim(claim);
 } catch (error) {
 if (error.name !== "AbortError") {
 setError(error.message);
 }
 }
 }

 loadClaim();

 return () => controller.abort();
}, [claimId]);
```

This cleanup aborts the request associated with the previous `claimId`. The effect itself is not declared `async`, because an effect must return a cleanup function or nothing, not a Promise.

For more involved data fetching, a framework’s data-loading APIs or a server-state library can provide caching, request deduplication, retries, and stale-result handling. Effects are useful for client-side synchronization, but hand-written fetching can omit important production behavior.

### WebSockets and Subscriptions

Long-lived connections need explicit teardown:

```jsx
useEffect(() => {
 const socket = new WebSocket(`/events?batchId=${batchId}`);

 socket.addEventListener("message", handleMessage);

 return () => {
 socket.removeEventListener("message", handleMessage);
 socket.close();
 };
}, [batchId]);
```

Without cleanup, changing `batchId` could leave several sockets active, each delivering events to the page.

When removing a DOM event listener, use the same event type and handler reference used to register it. If listener options such as `capture` are involved, keep them consistent as well.

## Historical Context

Class components commonly handled setup and teardown with lifecycle methods: subscribe in `componentDidMount`, respond to changed props in `componentDidUpdate`, and unsubscribe in `componentWillUnmount`.

Function components do not have those lifecycle methods. `useEffect` combines setup and cleanup in one place and ties them to the values that determine the synchronization. This helps keep related behavior together. It is not a direct one-to-one replacement for each class lifecycle method; the stronger model is “synchronize this external system with these current inputs.”

React 18 removed the old warning about setting state after a component unmounted, because that warning often flagged harmless cases and did not reliably identify memory leaks. That change did **not** make cleanup optional. Listeners, timers, subscriptions, and connections can still do unnecessary work or cause real resource and correctness problems if left active.

## Advantages and Trade-Offs

**Advantages:**

- Releases resources when a component is removed or its dependencies change.
- Prevents old subscriptions or listeners from continuing alongside new ones.
- Keeps setup and teardown for an external system together.
- Helps effects behave correctly through remounts and Strict Mode checks.

**Common risks and disadvantages:**

- Forgetting cleanup can leave duplicate listeners, timers, or connections active.
- Incorrect dependencies can leave an effect synchronized to stale values or trigger repeated setup and teardown.
- Cleanup logic adds code that must match the setup correctly.
- Aborting a request does not replace server-side handling; the server may already have received or processed it.
- Strict Mode’s development setup-cleanup-setup sequence can reveal fragile effects, but should not be worked around by disabling cleanup.

### Interview Summary

> A `useEffect` cleanup function reverses the setup performed by the effect. React runs it before rerunning the effect when dependencies change and when the component unmounts; development Strict Mode may also run an extra setup-cleanup cycle to test resilience. I use cleanup to remove listeners, clear timers, unsubscribe, close connections, or cancel obsolete requests. The setup and cleanup should describe one synchronization process, with dependencies that accurately reflect the values it uses.
Updated todo list

Read-only Ask mode prevents me from editing `react.md`. Here is an expanded, ready-to-add section.

## 15. `useEffect` vs `useLayoutEffect`

Both `useEffect` and `useLayoutEffect` let a function component synchronize React with something outside its render calculation. Their main difference is **when their setup and cleanup run relative to DOM updates and browser painting**.

A simplified browser update looks like this:

```text
React renders components
 ↓
React commits DOM changes
 ↓
Layout effects run
 ↓
Browser paints
 ↓
Passive effects usually run
```

`useLayoutEffect` runs synchronously after React has changed the DOM but **before the browser paints**. `useEffect` runs after React commits; in many cases it runs after the browser has had an opportunity to paint. This distinction matters when an effect must measure or adjust layout before the user sees the screen.

### `useEffect`: The Default

Use `useEffect` for synchronization that does not need to block the browser’s next paint. Examples include setting up subscriptions, timers, and many non-visual integrations.

```jsx
import { useEffect } from "react";

function BatchStatus({ batchId }) {
 useEffect(() => {
 const subscription = subscribeToBatch(batchId);

 return () => subscription.unsubscribe();
 }, [batchId]);

 return <p>Monitoring batch {batchId}</p>;
}
```

When `batchId` changes, React cleans up the previous subscription and sets up a new one. When the component unmounts, React runs the final cleanup.

Although `useEffect` is commonly described as running “after paint,” that is not an absolute timing guarantee in every situation. React may run an effect before paint when an update was caused by an interaction. Use it for ordinary external synchronization, not when correctness depends on measuring layout before display.

### `useLayoutEffect`: Layout-Sensitive Work

Use `useLayoutEffect` when you must read or adjust the DOM after React updates it but before the browser paints. A common case is measuring an element to position a tooltip.

```jsx
import { useLayoutEffect, useRef, useState } from "react";

function Tooltip({ targetId, children }) {
 const tooltipRef = useRef(null);
 const [top, setTop] = useState(0);

 useLayoutEffect(() => {
 const target = document.getElementById(targetId);
 const tooltip = tooltipRef.current;

 if (!target || !tooltip) return;

 const targetRect = target.getBoundingClientRect();
 const tooltipRect = tooltip.getBoundingClientRect();

 setTop(targetRect.top - tooltipRect.height);
 }, [targetId, children]);

 return (
 <div ref={tooltipRef} style={{ position: "fixed", top }}>
 {children}
 </div>
 );
}
```

React first commits the tooltip to the DOM. The layout effect reads its dimensions and updates the position before paint. React can then commit the corrected position before the user sees it, avoiding a visible jump.

In a real tooltip or popover, positioning may also need to account for scrolling, viewport boundaries, resizing, and placement preferences. CSS positioning or a well-tested positioning library may be preferable to implementing all of those rules manually.
### Comparison

| Topic | `useEffect` | `useLayoutEffect` |
|---|---|---|
| When setup runs | After commit; usually after paint when timing allows | After DOM mutations and before paint |
| Typical purpose | Subscriptions, timers, non-visual synchronization | Measure or adjust layout before display |
| Blocks browser paint | No, in the usual case | Yes, while it runs |
| Performance risk | Usually low for visual responsiveness | Can delay paint and cause jank if slow |
| Server rendering | Does not run on the server | Does not run on the server; may need special handling for SSR |

Both Hooks accept a setup function and an optional dependency array. If a dependency changes, React runs the previous cleanup before the next setup. An empty dependency array means the effect has no reactive dependencies; it does not guarantee one execution across all mounts or development checks.

## What Happens Internally?

For both Hooks, React evaluates the component during the **render phase**. Effects do not run during that calculation. After React commits the resulting DOM changes, it processes layout effects and passive effects at different points:

1.**Render:** React calculates the next UI. Render logic should remain pure.
2.**Commit DOM mutations:** React applies changes to the DOM and updates relevant refs.
3.**Layout effects:** React runs `useLayoutEffect` setup and any required cleanup before paint. A state update here can cause another render and commit before the browser displays the frame.
4.**Paint:** The browser displays the committed layout.
5.**Passive effects:** React generally runs `useEffect` setup after paint when possible.

The `useLayoutEffect` callback is synchronous from the user-interface perspective: while it runs, the browser cannot paint the new frame. Reading layout measurements, such as `getBoundingClientRect()`, may also cause the browser to calculate layout synchronously. That is useful when necessary, but doing it frequently or performing expensive work can delay the frame.

React Strict Mode may run effect setup and cleanup an extra time in development. This helps expose missing cleanup and assumptions that setup runs exactly once. Both effect types should be written so their setup and cleanup are safe to repeat.

## Real-World Guidance

**Use `useEffect` for subscriptions and timers.** These usually need to stay synchronized with props or state, but do not need to block the first paint.

**Use `useLayoutEffect` for visible layout correction.** Examples include measuring a tooltip, restoring a scroll position before display, or synchronously adjusting focus when a UI requirement demands it.

**Prefer CSS when CSS can solve the problem.** Responsive layout, spacing, and many positioning rules should not require reading the DOM from JavaScript. CSS avoids adding synchronization work to React’s commit process.

**Use `ResizeObserver` when responding to ongoing size changes.** An initial layout measurement may use `useLayoutEffect`; detecting later element-size changes is often better handled by a `ResizeObserver` whose observer is installed and disconnected with an effect.

**Do not use either effect for values that can be derived during render.** For example, filtering a list from its current data and query does not need an effect that copies filtered results into another state variable. Deriving it directly avoids an extra update cycle and duplicated state.

**For data fetching, consider the application’s data layer.** A simple client-only request can use `useEffect`, but production applications often benefit from framework loaders or a server-state library that handles caching, retries, cancellation, and request deduplication.

## Advantages and Disadvantages

**`useEffect` advantages:**

- Does not synchronously block paint in the usual case.
- Suits subscriptions, timers, and external synchronization.
- Cleanup can keep resources aligned with changing dependencies.

**`useEffect` trade-offs:**

- Visual updates may briefly show before an effect corrects them.
- Incorrect dependencies can create stale values or repeated setup.
- Hand-written effects can grow complicated, particularly for data fetching.

**`useLayoutEffect` advantages:**

- Can measure the committed DOM before the browser paints.
- Can prevent a visible intermediate layout when synchronous correction is necessary.

**`useLayoutEffect` trade-offs:**

- Blocks paint while it and any synchronous updates complete.
- Layout reads can force expensive browser layout calculations.
- It does not run during server rendering, so server-rendered components that depend on it need a deliberate strategy.

### Interview Summary

> I use `useEffect` by default for synchronizing with external systems, such as subscriptions and timers. I use `useLayoutEffect` only when I need to read or adjust the committed DOM before the browser paints, such as measuring a tooltip to prevent flicker. Layout effects can block paint, so I first consider whether CSS or another browser API can solve the problem. Neither Hook runs during server rendering, and both need correct dependencies and cleanup.

Updated todo list

Read-only Ask mode prevents me from editing `react.md`. Here is an expanded, ready-to-add section.

## 16. `useRef`

### 18. What Is `useRef()`?

`useRef` is a React Hook that gives a component a **stable, mutable object** whose `.current` property can hold a value between renders.

```jsx
import { useRef } from "react";

function SearchBox() {
 const inputRef = useRef(null);

 return (
 <>
 <input ref={inputRef} aria-label="Search claims" />
 <button onClick={() => inputRef.current?.focus()}>
 Focus search
 </button>
 </>
 );
}
```

When React commits the input to the DOM, it assigns the DOM node to `inputRef.current`. The click handler can then call the browser’s `focus()` method on that node.

A ref can also hold a non-DOM value:

```jsx
const requestCountRef = useRef(0);

requestCountRef.current += 1;
```

Changing `.current` does **not** by itself schedule a render. This makes a ref useful for information that must persist but does not need to appear in the rendered UI.

### `useRef` vs `useState`

Use state when a value affects what React should display. Use a ref when a value must persist between renders but changing it should not itself update the UI.

| Requirement | Usually use |
|---|---|
| Show the current count in the UI | `useState` |
| Keep a DOM node to focus it | `useRef` |
| Store a timer ID for later cleanup | `useRef` |
| Track a value that should trigger a render when it changes | `useState` |
| Hold an external object, such as a player or connection instance | `useRef` |

For example, this ref update will not refresh the displayed number:

```jsx
function ClickCounter() {
 const clicksRef = useRef(0);

 function handleClick() {
 clicksRef.current += 1;
 }

 return (
 <button onClick={handleClick}>
 Clicks: {clicksRef.current}
 </button>
 );
}
```

The value displayed in the button comes from the last render. Clicking changes `.current`, but does not cause another render. If the count must update on screen, use state:

```jsx
const [clicks, setClicks] = useState(0);
```

A ref is not a substitute for state when the value affects visible output.

### How `useRef` Works Internally

A typical call looks like this:

```jsx
const valueRef = useRef(initialValue);
```

React stores the ref object with the component’s Hook data. On subsequent renders of the same component instance, React returns the **same ref object**, so `.current` retains the value assigned to it.

A ref update is an ordinary JavaScript mutation:

```jsx
valueRef.current = nextValue;
```

React does not subscribe to that property or compare it to decide whether the component should render. There is no automatic re-render when `.current` changes.

For a DOM ref, React assigns the node during the commit process after rendering. When that node is removed, React clears the ref. Therefore, a DOM ref can be `null` before the element is mounted or after it is removed; use optional chaining or an explicit null check before accessing it.

Do not use ref mutations as a way to communicate render-affecting data. React can render from state and props; changing a ref alone does not tell React to refresh the output. Also avoid changing or depending on mutable refs during rendering. Render work may be repeated or abandoned, so render should remain pure. The common exception is predictable one-time initialization of a ref when necessary.

### Real-World Examples

#### Focus an Input

Focusing an input is an imperative browser operation. A ref provides access to the actual element:

```jsx
function ClaimSearch() {
 const searchRef = useRef(null);

 function focusSearch() {
 searchRef.current?.focus();
 }

 return (
 <>
 <input ref={searchRef} aria-label="Search claims" />
 <button type="button"onClick={focusSearch}>
 Focus search
 </button>
 </>
 );
}
```

This is a reasonable use of a ref because focus is managed by the browser and is not directly represented by React state.

#### Store a Timer ID

A timer ID is needed later to cancel the timer, but it normally does not need to be rendered:

```jsx
function PollingControl() {
 const timerRef = useRef(null);

 function startPolling() {
 if (timerRef.current !== null) return;

 timerRef.current = window.setInterval(() => {
 refreshStatus();
 }, 30_000);
 }

 function stopPolling() {
 if (timerRef.current === null) return;

 window.clearInterval(timerRef.current);
 timerRef.current = null;
 }

 useEffect(() => {
 return () => {
 if (timerRef.current !== null) {
 window.clearInterval(timerRef.current);
 }
 };
 }, []);

 return (
 <>
 <button type="button"onClick={startPolling}>Start</button>
 <button type="button"onClick={stopPolling}>Stop</button>
 </>
 );
}
```

If the UI must show whether polling is active, store that visible status in state. The timer ID can remain in a ref.

#### Work with an Uncontrolled Input

An uncontrolled input keeps its current value in the DOM rather than mirroring every keystroke into React state:

```jsx
function QuickFilter() {
 const inputRef = useRef(null);

 function applyFilter() {
 const query = inputRef.current?.value ?? "";
 runSearch(query);
 }

 return (
 <>
 <input ref={inputRef} defaultValue=""aria-label="Filter" />
 <button type="button"onClick={applyFilter}>
 Apply
 </button>
 </>
 );
}
```

This can be appropriate for simple forms or integrations where React does not need to respond to each keystroke. Use controlled inputs when the UI must react immediately to the current field value, validate as the user types, or coordinate the value with other components.

### Background and Other Approaches

Before Hooks, class components commonly stored persistent values on the class instance and used `createRef()` for DOM access:

```jsx
class SearchBox extends React.Component {
 inputRef = React.createRef();

 focusInput = () => {
 this.inputRef.current?.focus();
 };

 render() {
 return <input ref={this.inputRef} />;
 }
}
```

In a function component, calling `createRef()` during every render would create a new ref object each time. `useRef()` exists to give function components a ref object that persists across renders.

For some use cases, a ref is not the best tool:

- Use **state** if changing a value should update rendered UI.
- Use a **callback ref** when you need to react to a DOM node being attached or detached.
- Use **effects** to set up and clean up subscriptions or browser listeners.
- Prefer **CSS** for layout and visual state when it can solve the problem without DOM measurements.

For custom components, ref passing depends on the React version and component API. React 19 function components can accept `ref` as a prop; older React versions commonly use `forwardRef`. Follow the version and conventions used by the application.

### Advantages and Trade-Offs

**Advantages:**

- Persists data across renders without causing a re-render when changed.
- Provides direct access to DOM nodes for imperative operations such as focus or measurement.
- Can store timer IDs, external instances, and other values that do not affect the UI.
- Avoids creating a new ref object on every render.

**Disadvantages and common risks:**

- Updating `.current` does not refresh the UI, which can produce stale displayed values if a ref is used in place of state.
- Mutable refs are not tracked by React, so they can make data flow harder to inspect.
- Direct DOM manipulation can conflict with React if used to change content React also owns.
- A DOM ref can be `null` before mount and after removal.
- Ref changes during rendering can be unsafe if React repeats or abandons that render.

### Interview Summary

> `useRef` returns a stable object with a mutable `.current` property. React preserves that object between renders, but changing `.current` does not cause a render. I use refs for imperative DOM access, such as focusing an input, and for persistent values like timer IDs that do not affect the UI. If a value should change what the component displays, I use state instead.

Updated todo list

Read-only Ask mode prevents me from editing `react.md`. Here is an expanded, ready-to-add section.

## 17. `useMemo`

### 19. What Is `useMemo()`?

`useMemo` is a React Hook that caches the **result of a calculation** between renders. React recalculates the value when one of the listed dependencies changes; otherwise, it can reuse the previous result.

```jsx
import { useMemo } from "react";

function ClaimList({ claims, query }) {
 const filteredClaims = useMemo(() => {
 return claims.filter(claim =>
 claim.reference.toLowerCase().includes(query.toLowerCase())
 );
 }, [claims, query]);

 return <ClaimTableclaims={filteredClaims} />;
}
```

The first argument is a function that calculates and returns the value. The second argument is a dependency array containing the reactive values used by that calculation.

Without memoization, React would run `filter` each time `ClaimList` renders. With `useMemo`, React can reuse the previous filtered list when both `claims` and `query` are unchanged.

### What Does `useMemo` Actually Optimize?

`useMemo` can avoid repeating an **expensive calculation**. It can also preserve an object or array reference when that stable identity helps another optimization, such as a memoized child component.

It does **not** prevent the component containing `useMemo` from rendering. It only lets React reuse a calculation result when the dependencies have not changed.

```jsx
const visibleRows = useMemo(
 () => filterAndSortRows(rows, filter, sortOrder),
 [rows, filter, sortOrder]
);
```

When `rows`, `filter`, or `sortOrder` changes, the calculation runs again. When the component renders for another reason but those dependencies remain the same, React may reuse the cached value.

### How Dependencies Work

React compares each dependency with its previous value using `Object.is`. If any dependency is different, React recalculates the value.

Include every reactive value used by the calculation. Omitting one can make the memoized result stale:

```jsx
const greeting = useMemo(() => {
 return `Hello, ${name}`;
}, [name]);
```

Avoid creating a new object outside the memo and then using it as a dependency:

```jsx
function ProductList({ products, category }) {
 const options = { category };

 const visibleProducts = useMemo(
 () => filterProducts(products, options),
 [products, options]
 );

 // ...
}
```

`options` is a new object on every render, so its reference changes and the calculation runs every time. If the object is only needed for the calculation, create it inside the `useMemo` callback and depend on the primitive value:

```jsx
const visibleProducts = useMemo(() => {
 const options = { category };
 return filterProducts(products, options);
}, [products, category]);
```

Dependencies are compared by identity, not by deeply inspecting objects. Mutating `products` in place while keeping the same array reference may prevent React from recognizing that the data changed. Use immutable data updates and stable dependency values.

### Real-World Example: Expensive Table Filtering

A large table may need filtering and sorting. If profiling shows that the calculation is expensive and unrelated state changes frequently render the component, memoization may help:

```jsx
function ClaimsTable({ claims, query, sortKey }) {
 const visibleClaims = useMemo(() => {
 const matching = claims.filter(claim =>
 claim.reference.toLowerCase().includes(query.toLowerCase())
 );

 return matching.toSorted((first, second) =>
 first[sortKey].localeCompare(second[sortKey])
 );
 }, [claims, query, sortKey]);

 return <ClaimRowsclaims={visibleClaims} />;
}
```

`toSorted()` returns a sorted copy instead of mutating the original array. If the project’s JavaScript target does not support it, copy before calling `sort()`:

```jsx
return [...matching].sort(compareClaims);
```

For a small list or a cheap calculation, the plain version may be clearer and fast enough. Measure before adding memoization.

### Real-World Example: Stable Props for a Memoized Child

A memoized child can skip rendering when its props are unchanged. Creating an object inline gives it a new identity on every parent render, even if its contents are equivalent:

```jsx
<Chartoptions={{ showLegend, color }} />
```

If `Chart` is wrapped in `memo`, the new `options` object can make its props appear changed. `useMemo` can preserve the object identity until its actual inputs change:

```jsx
const chartOptions = useMemo(
 () => ({ showLegend, color }),
 [showLegend, color]
);

return <Chartoptions={chartOptions} />;
```

This only helps if the child’s memoization and the surrounding workload make the stable reference useful. `useMemo` alone does not prevent `Chart` from rendering if another prop changes or if the child is not memoized.

## Internal Working and Background

Hooks, including `useMemo`, were introduced to let function components use stateful React features without class components. Before Hooks, teams commonly used class lifecycle methods and tools such as `PureComponent` or `shouldComponentUpdate` to avoid some unnecessary work. `useMemo` offers function components a way to reuse a calculated value or stabilize its reference.

At a high level, React stores the memoized value and its dependencies with the component’s Hook data. During a later render, it compares the new dependencies with the previous ones. If they match, React can return the stored value; if they differ, it runs the calculation and stores the new result.

The calculation must be **pure**. Do not use `useMemo` to perform side effects such as network requests, subscriptions, or DOM changes. Those belong in the appropriate event handler, effect, or data-loading mechanism.

React treats `useMemo` as a **performance optimization, not a correctness guarantee**. React may discard a memoized value in certain situations, so the application must still work if the calculation runs again. Do not rely on it to preserve data, maintain application state, or ensure an operation happens exactly once. In development Strict Mode, React may run the calculation more than once to help detect impurities.

## Should We Use `useMemo` Everywhere?

**No.** Memoization has costs: React must store the cached value, compare dependencies, and maintain extra code that developers must understand. For a cheap calculation, those costs and added complexity may outweigh the benefit.

Use `useMemo` when:

- A calculation is measurably expensive and runs repeatedly with unchanged inputs.
- A stable object or array reference is needed by a memoized child or another dependency-sensitive Hook.
- Profiling shows repeated calculation or rendering is a meaningful problem.

Avoid using it automatically for every expression, or to make code “more optimized” without evidence. Also avoid using it to hide a missing dependency or to keep mutable state. If a value can be calculated simply during render, calculate it directly.

Current React tooling may change the need for manual memoization. If a project uses **React Compiler** and has it configured, the compiler can automatically optimize many values and functions. Follow the project’s compiler setup and team guidance; retain manual memoization where it provides a measured benefit or is required by an API boundary.

### Related Hooks

-**`useMemo`** caches a calculated value.
-**`useCallback`** caches a function reference. Conceptually, it is similar to `useMemo(() => function, dependencies)`.
-**`memo`** can let a component skip rendering when its props are unchanged, subject to its comparison behavior.

These tools address different parts of the rendering process. None should be added by default without understanding what work they avoid.

### Advantages and Trade-Offs

**Advantages:**

- Avoids repeating expensive calculations when dependencies are unchanged.
- Can preserve object or array identity for memoized children and dependency lists.
- Keeps expensive derived data tied to explicit inputs.

**Disadvantages:**

- Adds dependency and cache-management complexity.
- Provides little value for inexpensive calculations.
- Does not prevent the component itself from rendering.
- Is not a persistent cache or a correctness mechanism.
- Can hide stale dependency bugs when dependencies are incomplete.

### Interview Summary

> `useMemo` caches the result of a pure calculation between renders while its dependencies remain unchanged. React compares dependencies with `Object.is` and recalculates when one changes. I use it when profiling shows an expensive calculation or when stable identity benefits a memoized child. I do not use it everywhere: it adds complexity, does not prevent the component from rendering, and must never be required for correctness.

Updated todo list

Read-only Ask mode prevents me from editing `react.md`. Here is an expanded, ready-to-add section.

## 18. `useCallback`

### 20. What Is `useCallback()`?

`useCallback` is a React Hook that caches a **function reference** between renders, as long as its dependencies remain unchanged.

```jsx
import { useCallback, useState } from "react";

function SearchPanel() {
 const [query, setQuery] = useState("");

 const handleClear = useCallback(() => {
 setQuery("");
 }, []);

 return (
 <>
 <input
value={query}
onChange={event => setQuery(event.target.value)}
 />
 <button onClick={handleClear}>Clear</button>
 </>
 );
}
```

A JavaScript function is an object, so defining the same-looking function again creates a new function reference:

```jsx
function SearchPanel() {
 function handleClear() {
 setQuery("");
 }

 // A new handleClear function is created on each render.
}
```

Often, that is perfectly fine. `useCallback` becomes useful when **the function’s identity matters**, such as when passing a callback to a memoized child or using it as a dependency of another Hook.

`useCallback` does not cache the result of calling the function. It caches the function itself.

## How Does `useCallback` Work?

```jsx
const handleClick = useCallback(() => {
 console.log("Clicked");
}, []);
```

React stores the function and its dependency values with the component’s Hook data. On a later render, it compares the new dependencies with the previous ones using `Object.is`:

- If all dependencies are unchanged, React returns the previously cached function reference.
- If any dependency changed, React stores and returns the new function.

Like other Hooks, `useCallback` must be called at the top level of a function component or custom Hook, not conditionally inside branches or loops.

A callback captures values from the render in which it was created. Include every reactive value used inside the callback in its dependencies, or it may continue using stale values.

```jsx
const handleSelect = useCallback(() => {
 setSelectedId(selectedId);
}, []); // Bug: selectedId is captured from an earlier render
```

A correct version includes the dependency:

```jsx
const handleSelect = useCallback(() => {
 setSelectedId(selectedId);
}, [selectedId]);
```

If the update depends on the previous state, a functional state update can avoid needing that state value as a dependency:

```jsx
const handleToggle = useCallback(id => {
 setSelectedId(previousId => previousId === id ? null : id);
}, []);
```

This does not mean dependencies should be omitted casually. Include the reactive values the function actually reads. The Hooks lint rule can help identify missing dependencies.

### Real-World Example: Memoized List Rows

Suppose a list contains many rows, and each row is wrapped in `memo`. React can skip rendering a row when its props have not changed. However, if the parent creates a new callback on every render, that function prop has a new identity and can make the row appear to have changed.

```jsx
import { memo, useCallback, useState } from "react";

const UserRow = memo(function UserRow({ user, onSelect }) {
 return (
 <button onClick={() => onSelect(user.id)}>
 {user.name}
 </button>
 );
});

function UserList({ users }) {
 const [selectedId, setSelectedId] = useState(null);
 const [theme, setTheme] = useState("light");

 const handleSelect = useCallback(id => {
 setSelectedId(previousId => previousId === id ? null : id);
 }, []);

 return (
 <section className={theme}>
 <button onClick={() => setTheme(current =>
 current === "light" ? "dark" : "light"
 )}>
 Toggle theme
 </button>

 {users.map(user => (
 <UserRow
key={user.id}
user={user}
onSelect={handleSelect}
 />
 ))}
 <p>Selected: {selectedId ?? "None"}</p>
 </section>
 );
}
```

When the theme changes, `UserList` renders again. Because `handleSelect` keeps the same reference and the user objects are unchanged, `memo` may allow the rows to skip rendering.

This is not guaranteed: a row still renders if another prop changes, if its own state changes, or if consumed context changes. `useCallback` also does not make the callback’s work faster; it only stabilizes its identity.

## `useMemo` vs `useCallback`

Both Hooks use dependency arrays, but they cache different things.

```jsx
const filteredUsers = useMemo(
 () => filterUsers(users, query),
 [users, query]
);

const handleSelect = useCallback(
 id => setSelectedId(id),
 []
);
```

| Hook | What it caches | Typical use |
|---|---|---|
| `useMemo` | A calculated value | Avoid repeating an expensive calculation or preserve an object/array reference |
| `useCallback` | A function reference | Preserve a callback identity when that identity matters |

Conceptually:

```jsx
useCallback(fn, dependencies)
```

is similar to:

```jsx
useMemo(() => fn, dependencies)
```

`useMemo` calls its calculation function to get a value. `useCallback` returns the callback function itself without calling it.

## When Should You Use `useCallback`?

Consider it when profiling or component behavior shows that a new function reference is causing meaningful unnecessary work, particularly when:

- Passing a callback to a child wrapped in `memo`.
- Using a callback as a dependency of another Hook.
- Providing a stable function identity required by an external integration.

Often the simplest alternative is to define a function inside the effect that needs it. That can avoid adding `useCallback` just to stabilize an effect dependency. Likewise, ordinary event handlers usually do not need memoization unless a measured performance issue or an API contract makes their identity important.
## Background and Current Practice

Before Hooks were introduced, class components commonly used instance methods for callbacks and bound them in a constructor or with class fields. `PureComponent` and `shouldComponentUpdate` were used to avoid some unnecessary rendering. Function components often used inline callbacks, and `useCallback` provided a way to preserve callback identity across renders.

Today, start with straightforward code and measure before adding manual memoization. `useCallback` adds dependency-management complexity and some bookkeeping; for a cheap callback, it may provide no meaningful benefit. If the project uses **React Compiler**, it may automatically memoize many values and functions. Follow the project’s compiler configuration and conventions rather than adding manual memoization everywhere.

React treats `useCallback` as a **performance optimization, not a correctness guarantee**. React may discard a cached value in certain situations, so the application must remain correct if the function is recreated. Do not use it to store durable state or to control whether an operation happens exactly once.

### Advantages and Trade-Offs

**Advantages:**

- Can preserve a callback reference while dependencies remain the same.
- Can help memoized children avoid rendering solely because a callback prop changed identity.
- Can keep a Hook dependency stable when that stability is useful.

**Disadvantages and common mistakes:**

- Adds code and dependency management.
- Missing dependencies can create stale closures and incorrect behavior.
- Does not prevent the parent component from rendering.
- Does not guarantee that child components will not render.
- Adds little value when the callback is cheap and its identity is irrelevant.

### Interview Summary

> `useCallback` caches a function reference until one of its dependencies changes; it does not cache the function’s result. I use it when stable identity provides a measured benefit, such as passing a callback to a memoized child. I include all reactive dependencies and avoid applying it everywhere. `useMemo` caches a calculated value, while `useCallback` caches a function. Both are performance optimizations, not correctness mechanisms.
Updated todo list

Read-only Ask mode prevents me from editing `react.md`. Here is an expanded, ready-to-add section.

## 19. `React.memo`

### 21. What Is `React.memo()`?

`React.memo` is a higher-order component that can help React **skip rendering a function component when its props have not changed**.

```jsx
import { memo } from "react";

const User = memo(function User({ name }) {
 return <h1>{name}</h1>;
});
```

When a parent renders, React normally renders its child components as well. Wrapping `User` in `memo` lets React compare the new props with the previous props. If they are considered equal, React can reuse the previous rendered result instead of calling `User` again.

This is a **performance optimization**, not a change to the component’s meaning or a guarantee that it will never render again. The initial render still happens, and React may render a memoized component for other reasons.

### How Does `React.memo` Compare Props?

By default, React compares each prop from the previous render with the corresponding prop in the next render using `Object.is`.

```jsx
<Username="Jordan" />
```

If `name` remains the same string, the props compare equal.

Objects, arrays, and functions are compared by **reference identity**, not by their contents. Creating a new object inline gives it a different reference even if its fields are identical:

```jsx
<Chartoptions={{ color: "blue" }} />
```

On each parent render, this creates a new `options` object. A memoized `Chart` sees a changed prop and may render again.

If profiling shows that this is a problem, one option is to memoize the object:

```jsx
const chartOptions = useMemo(() => ({ color: "blue" }), []);

return <Chartoptions={chartOptions} />;
```

The same issue can occur with function props. A function defined in the parent is usually recreated on each render. `useCallback` can preserve its reference when the relevant dependencies are unchanged, but it should be added only when stable identity provides a measured benefit.

### Real-World Example: Large Table

Imagine an admin table with many rows. The parent tracks whether a filter panel is open. Toggling the panel re-renders the parent, even though the claim data has not changed.

```jsx
import { memo, useCallback, useState } from "react";

const ClaimRow = memo(function ClaimRow({ claim, onSelect }) {
 return (
 <tr>
 <td>{claim.reference}</td>
 <td>{claim.status}</td>
 <td>
 <button onClick={() => onSelect(claim.id)}>
 View
 </button>
 </td>
 </tr>
 );
});

function ClaimTable({ claims, onSelectClaim }) {
 const [filtersOpen, setFiltersOpen] = useState(false);

 const handleSelect = useCallback(
 claimId => onSelectClaim(claimId),
 [onSelectClaim]
 );

 return (
 <>
 <button onClick={() => setFiltersOpen(open => !open)}>
 {filtersOpen ? "Hide filters" : "Show filters"}
 </button>

 {filtersOpen && <FilterPanel />}

 <table>
 <tbody>
 {claims.map(claim => (
 <ClaimRow
key={claim.id}
claim={claim}
onSelect={handleSelect}
 />
 ))}
 </tbody>
 </table>
 </>
 );
}
```

If each `claim` object and the `handleSelect` callback keep the same references, `memo` may let individual rows skip rendering when only `filtersOpen` changes. This can help when rows are numerous or rendering each row is expensive.

It may not help if the parent recreates every `claim` object on each render, if another prop changes, or if the row consumes context that changed. Measure with React DevTools Profiler before applying memoization broadly.

## Does `React.memo` Prevent All Re-Renders?

**No.** A memoized component may still render when:

- Its own state changes.
- A context it consumes changes.
- It receives a prop that React considers changed.
- React chooses to render it for another reason.

It also does not preserve a component after it is removed from the tree. If a conditional branch removes `<User />` and later mounts it again, its local state is reset. Changing its `key` tells React that it is a different component identity, which can also cause a remount.

`memo` is not a global cache of rendered output. It is a hint that React can skip rendering work when the component’s inputs have not changed.

## Custom Prop Comparison

`memo` accepts an optional second argument, a custom comparison function:

```jsx
const Chart = memo(ChartView, areChartPropsEqual);
```

The comparison function returns `true` when the previous and next props can be treated as equivalent, and `false` when the component should render.

Custom comparators are easy to get wrong. They must account for **every prop that can affect rendered output or behavior**, including callback props. If a comparator ignores a changed callback, the component may keep an older function closure and behave incorrectly. Deep comparisons can also cost more than simply rendering the component.

Use a custom comparison only when profiling identifies a real issue and the comparator can be kept correct and cheaper than the work it avoids.

## Internal Working

React records the memoized component in its internal Fiber tree. When the parent renders, React checks the memoized component’s new props against its previous props. If they compare equal and there is no relevant update scheduled for the component itself or its consumed context, React can bail out of rendering that component and reuse its previous result.

If a prop changes, or the component has its own state or context update, React renders it as needed. This comparison-and-bailout process is why `memo` can reduce repeated work, but it cannot guarantee that a render is skipped in every situation.

### Background and Related Approaches

Class components historically used `PureComponent` or `shouldComponentUpdate` to avoid some unnecessary renders. `React.memo` offers a similar optimization for function components. Hook-based tools complement it:

- `memo` can skip rendering a component when its props are unchanged.
- `useMemo` can preserve a calculated value, such as an object passed as a prop.
- `useCallback` can preserve a function reference passed as a prop.

These optimizations address different sources of work and are not automatically needed together. Prefer simpler code unless profiling shows repeated work is a problem.

In current React projects, **React Compiler**, when installed and enabled, can automatically memoize many components, values, and functions. Follow the project’s compiler configuration and team guidance; avoid manual memoization by habit, but use it when required by a measured performance issue or an API boundary.

### Advantages and Trade-Offs

**Advantages:**

- Can skip expensive child renders when props are unchanged.
- Can improve performance in large lists or complex visual components.
- Encourages clear component inputs when used with stable props.

**Disadvantages and common mistakes:**

- Adds comparison work and code complexity.
- Often provides little benefit for inexpensive components.
- New object, array, or function props can defeat the default comparison.
- Does not prevent renders caused by the component’s own state or context.
- A faulty custom comparator can preserve stale behavior.
- It should not be treated as a correctness mechanism.

### Interview Summary

> `React.memo` can let React skip rendering a function component when its props are unchanged according to a shallow comparison using `Object.is`. It does not prevent renders caused by the component’s own state or context, and new object or function references can make props appear changed. I use it when profiling shows a meaningful rendering cost, and I pair it with stable props only when that improves the measured result. It is an optimization, not a guarantee.
Compacted conversation

Updated todo list

Read-only Ask mode prevents me from editing `react.md`. The following expanded section is ready to add.

## 20. Context API

### 22. What Is the Context API?

The **Context API** is a built-in React mechanism for making a value available to components lower in a component tree without explicitly passing it through every intermediate component as a prop. This avoids a pattern called **prop drilling**: a value is passed through components that do not use it only so that a deeply nested component can receive it.

For example, an application may have a page layout with many layers between the root and a profile menu:

```text
App
└── Dashboard
 └── Header
 └── AccountMenu
 └── UserName
```

Without Context, `user` might need to be passed as a prop through `Dashboard`, `Header`, and `AccountMenu`, even if those components do not need it themselves. Context lets `UserName` read the value directly from a provider higher in the tree.

Context is useful for values that many components need within a particular part of the application, such as:

- Theme or visual preferences.
- Locale or translation settings.
- The current authenticated user or session-related UI information.
- Feature flags or application configuration.
- A shared service or dependency, such as a client instance.

Context provides **distribution**, not a complete state-management solution by itself. The value still needs an owner, a way to change it, and rules for handling updates.

### Creating, Providing, and Reading Context

Create a context with `createContext`. Its argument is the **default value**:

```jsx
import { createContext, useContext } from "react";

const UserContext = createContext(null);
```

A provider supplies the value to components beneath it:

```jsx
function App({ user }) {
 return (
 <UserContext.Providervalue={user}>
 <Dashboard />
 </UserContext.Provider>
 );
}
```

A descendant reads the nearest matching provider’s value with `useContext`:

```jsx
function UserName() {
 const user = useContext(UserContext);

 if (user === null) {
 return <span>Guest</span>;
 }

 return <span>{user.displayName}</span>;
}
```

In React 19, a context object can itself be rendered as a provider:

```jsx
<UserContextvalue={user}>
 <Dashboard />
</UserContext>
```

`<UserContext.Provider>` remains valid and is common in existing codebases. Follow the React version and conventions used by the application.

### Provider Scope and Default Values

A provider makes its value available to its **descendants**. It does not affect components rendered outside that provider. If providers for the same context are nested, a consumer reads the value from the **closest provider above it** in the tree.

The value passed to `createContext(defaultValue)` is used only when a consumer has no matching provider above it. It is not automatically merged with a provider’s value, and it is not a fallback for a provider whose `value` is `undefined`.

This distinction matters when choosing a default. A value such as `null` can make a missing provider visible during development:

```jsx
const UserContext = createContext(null);

function useUser() {
 const user = useContext(UserContext);

 if (user === null) {
 throw new Error("useUser must be used within UserProvider");
 }

 return user;
}
```

A custom Hook like `useUser` gives consumers a consistent API and produces a clearer error if the provider was accidentally omitted. Alternatively, an application can choose a meaningful default, such as a default theme, when operating without a provider is valid.

### How Context Works Internally

At a high level, React associates context values with providers in the rendered tree. When a component calls `useContext(SomeContext)`, React reads the value from the nearest matching provider during rendering.

When a provider receives a different value, React can schedule updates for components that consume that context. React compares the previous and next provider values using `Object.is`. Therefore, object identity matters:

```jsx
<UserContext.Providervalue={{ user, role }}>
 <Dashboard />
</UserContext.Provider>
```

This creates a new object on every render. Even when `user` and `role` are unchanged, the provider value has a new reference, so consumers can be updated. If that becomes a measured performance issue, stabilize the value:

```jsx
const userContextValue = useMemo(
 () => ({ user, role }),
 [user, role]
);

return (
 <UserContext.Providervalue={userContextValue}>
 <Dashboard />
 </UserContext.Provider>
);
```

Do not add memoization automatically. First consider whether the provider should be split into smaller contexts, whether the value can be structured more simply, and whether profiling shows a real cost.

A context update is not the same as every component in the application rendering. Components that consume the changed context are the important subscribers. However, when a consumer renders, its descendants may also render as normal unless another optimization applies. Wrapping a consumer in `memo` does not prevent it from receiving an update when the context it reads changes.

### Example: Theme Context

A theme is a common Context use case because many components may need to select colors or styles:

```jsx
import { createContext, useContext, useState } from "react";

const ThemeContext = createContext(null);

function ThemeProvider({ children }) {
 const [theme, setTheme] = useState("light");

 const value = {
 theme,
 toggleTheme() {
 setTheme(current => current === "light" ? "dark" : "light");
 }
 };

 return (
 <ThemeContext.Providervalue={value}>
 {children}
 </ThemeContext.Provider>
 );
}

function ThemeButton() {
 const context = useContext(ThemeContext);

 if (context === null) {
 throw new Error("ThemeButton must be inside ThemeProvider");
 }

 return (
 <button type="button"onClick={context.toggleTheme}>
 Current theme: {context.theme}
 </button>
 );
}
```

In production code, the provider value can be memoized if its changing identity causes unnecessary consumer updates. Another option is to put changing state and stable actions in separate contexts when consumers need only one of them. That separation should solve a real update or ownership problem, not be added just for ceremony.

### Example: Authentication-Related UI

A provider can make session information available to navigation, account menus, and page components:

```jsx
const AuthContext = createContext(null);

function AccountMenu() {
 const auth = useContext(AuthContext);

 if (auth === null || !auth.user) {
 return <a href="/login">Sign in</a>;
 }

 return <span>Signed in as {auth.user.displayName}</span>;
}
```

Context can help decide what to display, but it is **not a security boundary**. Hiding an admin button or checking a role in a React component does not protect sensitive data or operations. The server must authenticate requests and authorize access independently.

### Context Is Not a Replacement for Redux

Context and Redux address related but different needs:

-**Context** makes a value available through a React tree.
-**Redux** is a state-management library with a store, defined update patterns, and an ecosystem of tools. Redux Toolkit is the recommended way to write Redux applications.

Context can carry state managed with `useState` or `useReducer`, so a small application may not need another global-state library. But Context alone does not provide Redux’s complete set of conventions and tooling, such as middleware, established action and reducer patterns, or Redux DevTools time-travel debugging.

A useful decision is based on the problem:

- Use **local component state** when one component or a small subtree owns the value.
- Use **Context with state or a reducer** when a value must be shared across a subtree and its update model is straightforward.
- Consider **Redux Toolkit** or another state library when state transitions, cross-feature coordination, middleware, debugging, or team conventions justify the additional structure.
- Use a **server-state library** such as TanStack Query, or framework data-loading features, for remote data caching, refetching, and request lifecycle concerns. Context is usually not a substitute for those capabilities.

“Frequently changing state” does not automatically mean Redux is required. The relevant questions include how many components consume the value, how updates are organized, how much rendering they trigger, and which debugging or middleware capabilities the team needs. Other libraries, such as Zustand, provide different trade-offs and may be a good fit depending on the application.

### Historical Background and Alternatives

Before Context became a standard built-in API, applications commonly passed props through intermediate components or used third-party state libraries to share data. React also had an older **legacy context** mechanism, but it was less reliable and less suitable as a public application API. The modern Context API replaced that legacy approach with explicit context objects and providers.

Context is still not always the right answer to prop drilling. If only one or two layers are forwarding a value, passing props can make dependencies easier to see. Component composition can also help: a parent can pass a prepared child element instead of making every layer know about the same data. Use Context when broad access within a meaningful subtree makes the component interfaces simpler.

### Advantages and Disadvantages

**Advantages:**

- Avoids passing the same prop through components that do not use it.
- Uses a built-in React API without requiring a separate state library.
- Works well for values shared by many components in a specific subtree.
- Supports custom provider and Hook patterns that make application dependencies explicit.
- Can be combined with `useState` or `useReducer` for a small shared state model.

**Disadvantages and risks:**

- It can hide dependencies: a component using Context may appear independent from its provider when reading it in isolation.
- Broad or frequently updated values can cause many consumers to render.
- A newly created provider object can trigger updates even when its contents are equivalent.
- Context does not provide persistence, server synchronization, caching, middleware, or advanced debugging by itself.
- Too many contexts or oversized providers can make ownership and update paths difficult to follow.
- Context-based authentication checks do not replace server-side authorization.

### Interview Summary

> Context lets a component read a value from the nearest matching provider above it, avoiding the need to pass that value through every intermediate component. It is useful for shared subtree values such as theme, locale, configuration, or session-related UI data. React updates consumers when the provider value changes, so value identity and provider scope matter. Context can carry state managed with Hooks, but it is not automatically a replacement for Redux or a server-state library; the choice depends on update complexity, performance needs, debugging requirements, and application architecture.

Updated todo list

Read-only Ask mode prevents me from editing `react.md`. The following expanded section is ready to add.

## 21. Prop Drilling

### 23. What Is Prop Drilling?

**Prop drilling** is the practice of passing data through several components as props so that a deeply nested component can use it, even though the intermediate components do not need the data themselves.

For example, suppose `UserProfile` needs the current user, but several layout components sit between the application root and the profile:

```text
App
└── Page
 └── MainLayout
 └── Sidebar
 └── UserProfile
```

Without another way to share the value, each intermediate component must receive and forward it:

```jsx
function App() {
 const user = { id: "u-42", displayName: "Jordan Lee" };

 return <Pageuser={user} />;
}

function Page({ user }) {
 return <MainLayoutuser={user} />;
}

function MainLayout({ user }) {
 return <Sidebaruser={user} />;
}

function Sidebar({ user }) {
 return <UserProfileuser={user} />;
}

function UserProfile({ user }) {
 return <p>Signed in as {user.displayName}</p>;
}
```

Only `UserProfile` uses `user`, but `Page`, `MainLayout`, and `Sidebar` must still know about it so they can pass it onward. As the application grows, this can make component interfaces noisy and couple layout components to data they do not own.

Prop drilling is **not automatically a React bug or an architectural failure**. Passing data through a few components can make dependencies explicit and easy to trace. It becomes a problem when many unrelated components must accept and forward the same values, especially when the data changes frequently or the tree structure makes the flow difficult to maintain.

### How Props Flow Through React

React’s ordinary data flow is top-down:

1. A parent owns or receives a value.
2. The parent passes the value to a child as a prop.
3. The child can display the value or pass it to another child.
4. If the value changes, the owner updates it and React renders the affected component tree as needed.

Props are read-only inputs. An intermediate component that forwards a prop is not changing the data; it is acting as a conduit.

A parent rendering again does not necessarily mean every descendant’s component function must always run. React may reuse work or skip some rendering under appropriate conditions. However, prop forwarding still creates maintenance costs: intermediate components depend on the value’s existence and must be updated if its name, shape, or ownership changes.

### Why Prop Drilling Can Become a Problem

Consider an operations dashboard where the current user’s permissions are needed by the navigation, a page toolbar, and several deeply nested actions. If each layout layer must accept `permissions` only to forward them, those layout components become aware of account authorization details they do not actually use.

Common symptoms include:

- Components with long prop lists that mostly forward values.
- A change to one piece of data requiring edits across many files.
- Layout or presentation components becoming coupled to application-specific data.
- New developers finding it difficult to locate where a value originates or which component owns it.
- Several copies of the same value being introduced because passing the original one is cumbersome.

The goal is not to eliminate every prop. It is to keep data ownership and component responsibilities clear.

## Ways to Address Prop Drilling

### 1. Keep Props When the Flow Is Short and Clear

For a small tree, ordinary props are often the simplest choice:

```jsx
function AccountPage({ user }) {
 return <UserProfileuser={user} />;
}
```

This makes the dependency visible in the component signature and keeps the data flow easy to follow. Do not introduce Context or a state library solely to avoid passing one prop through one or two layers.

### 2. Use Component Composition

Sometimes an intermediate component does not need to know the data at all. Instead of asking it to receive and forward a value, pass it a component or element that is already configured by the owner.

```jsx
function App() {
 const user = { displayName: "Jordan Lee" };

 return (
 <Page
sidebar={<UserProfileuser={user} />}
 />
 );
}

function Page({ sidebar }) {
 return (
 <main>
 <h1>Account overview</h1>
 {sidebar}
 </main>
 );
}
```

`Page` controls where the sidebar content appears, but does not need to know about the user. This approach is useful when a parent controls layout while another component controls the content.

Composition can also use a render prop or a function prop when the receiving component needs to control how or when content is created. These patterns can be useful, but they should remain straightforward; elaborate composition APIs can be harder to understand than a few ordinary props.

### 3. Restructure Component Boundaries

If the component that owns the data is far away from the components that use it, the tree or state ownership may be poorly matched to the feature. **Lifting state up** places state in the closest common ancestor that needs to coordinate it. Conversely, moving state down can reduce unnecessary coupling when only a small subtree uses it.

For example, if only a search panel and its results need the search query, the query may belong in their shared parent rather than in a top-level `App` component. Choosing a sensible owner can reduce the number of components that need the value.

This is an architectural choice, not a trick for avoiding props. The owner should be high enough to coordinate the consumers, but not so high that unrelated parts of the application become dependent on it.

### 4. Use Context for Shared Subtree Values

Context lets descendants read a value from the nearest matching provider without intermediate components forwarding it:

```jsx
const UserContext = createContext(null);

function App() {
 const user = { displayName: "Jordan Lee" };

 return (
 <UserContext.Providervalue={user}>
 <Page />
 </UserContext.Provider>
 );
}

function UserProfile() {
 const user = useContext(UserContext);

 if (user === null) {
 return <p>Guest</p>;
 }

 return <p>Signed in as {user.displayName}</p>;
}
```

This is useful when many components in a meaningful subtree need the same value, such as a theme, locale, or session-related UI information. It removes the need for intermediate components to forward that value.

Context has trade-offs. It can make a component’s dependencies less obvious because its props do not show that it relies on a provider. Consumers need the provider in their rendered tree, unless the context has a usable default. When a provider value changes, components that read that context may update. For frequently changing or broad state, consider the provider’s scope and value shape, and measure before adding optimizations.

Context provides access to a value; it does not automatically provide persistence, server caching, middleware, or advanced debugging. For those capabilities, another state or data-fetching tool may be more appropriate.

### 5. Use a State-Management Library When the Problem Justifies It

A library such as **Redux Toolkit**, **Zustand**, or another store can help when state is shared broadly and has complex transitions, multiple feature owners, or debugging and middleware requirements. A library is not automatically a better solution for every drilled prop. It adds concepts and dependencies, so adopt it to address concrete needs.

Also distinguish **client state** from **server state**. A selected tab or open panel is usually client UI state. Data fetched from an API has concerns such as caching, refetching, stale responses, and retries; a framework’s data APIs or a server-state library such as TanStack Query may be a better fit than putting all remote data in Context or Redux.

Custom Hooks can organize reusable logic, but a custom Hook alone does not make state globally shared. If each component calls a custom Hook that creates its own `useState`, each call has independent state. To share a single value, the Hook must read from a shared owner, Context, or an external store.

## Background: Before Context and Modern State Tools

In traditional imperative JavaScript, developers often found DOM elements, attached event listeners, and manually updated the elements when data changed. This could make it difficult to keep several parts of a page synchronized.

React’s component model introduced a more structured approach: parents pass data down through props, and components describe UI from their current inputs. This makes dependencies explicit, but deeply nested trees can require many forwarding components. Composition, Context, and external state libraries are different ways to manage that tension. Context became a supported built-in option for sharing values across a React subtree, while libraries provide additional store behavior and tooling.
### Real-World Examples

**Nested navigation:** A profile menu needs the current user’s display name, but page and layout components do not. Passing a prop through a few layers may be perfectly readable. If many unrelated navigation descendants need session information, an authentication context or a shared session store may fit better.

**Theme settings:** Many buttons, menus, and panels need the current theme. A theme provider is a natural way to make the setting available throughout a subtree without threading a `theme` prop through every layout component.

**Admin permissions:** A toolbar needs to know whether to display an action for the current user. Context may distribute the UI permission data to relevant components, but the backend must still authorize the action. Client-side UI checks do not secure APIs.

**Search page:** If only a filter panel and result table need a query, keep the query in their closest shared parent and pass it as props. A global store would likely add needless indirection.

**Complex workflow:** A multi-step claims workflow may have state transitions, asynchronous actions, and several distant consumers. A reducer or a dedicated state library can make transitions more explicit. If the data comes from a server, use a suitable data-fetching or caching layer rather than treating Context as a request cache.

### Advantages and Disadvantages

**Advantages of addressing prop drilling thoughtfully:**

- Keeps intermediate components focused on their actual responsibilities.
- Reduces repetitive forwarding when many descendants need shared data.
- Makes state ownership easier to align with the feature.
- Composition can separate layout decisions from data-specific content.
- Context or a store can provide shared access where the component tree warrants it.

**Trade-offs and risks:**

- Props are explicit and easy to trace; replacing them unnecessarily can hide dependencies.
- Composition can become difficult to follow if its API is overly flexible.
- Context can broaden updates or obscure which provider a component requires.
- External state libraries add dependencies, conventions, and learning overhead.
- Restructuring state ownership may require changes across feature boundaries.
- A custom Hook does not share state merely because several components call it.

### Interview Summary

> Prop drilling is passing a value through intermediate components that do not use it so a deeper descendant can receive it. It is acceptable for short, clear data flows, but becomes cumbersome when many layers forward unrelated data. I first check whether ordinary props, component composition, or better state ownership solves the problem. I use Context for values shared by many descendants in a subtree, and a state-management library when the application needs broader coordination or tooling. I choose based on ownership, update patterns, and maintainability rather than trying to eliminate every prop.
Updated todo list

Read-only Ask mode prevents me from editing `react.md`. The following expanded section is ready to add.

## 22. Controlled Components

### 24. What Is a Controlled Component?

A **controlled input** is a form element whose current value is supplied by React, usually from component state. The input displays that value, and an event handler updates the state when the user interacts with it. React is therefore the source of truth for the value shown in the input.

```jsx
import { useState } from "react";

function NameField() {
 const [name, setName] = useState("");

 return (
 <label>
 Name
 <input
value={name}
onChange={event => setName(event.target.value)}
 />
 </label>
 );
}
```

The data flow is:

```text
User types
 ↓
The browser raises an input event
 ↓
onChange reads the new value
 ↓
React state is updated
 ↓
React renders the input with the new value
```

The `value` prop controls what the input displays. The `onChange` handler reports edits back to React. If an input receives `value` but has no handler that updates it, React will keep rendering the supplied value, so the field will generally behave as read-only. For intentionally read-only fields, use `readOnly` to make that behavior explicit.

A controlled input is not necessarily “controlled by the server.” React state is the source of truth for the client-side field while the component is active. The application still decides when to validate, save, or submit that value to a server.

### Other Controlled Form Elements

The same pattern applies to checkboxes, select menus, and text areas, although the controlled prop differs:

```jsx
const [accepted, setAccepted] = useState(false);

<input
type="checkbox"
checked={accepted}
onChange={event => setAccepted(event.target.checked)}
/>
```

For a checkbox, use `checked`, not `value`, to control whether it is selected. For a select menu, control the selected option with `value`. For a text area, React uses `value` rather than relying on HTML text between the opening and closing tags.

```jsx
const [status, setStatus] = useState("pending");

<select
value={status}
onChange={event => setStatus(event.target.value)}
>
 <option value="pending">Pending</option>
 <option value="complete">Complete</option>
</select>
```

Input values from browser events are commonly strings, including values from `type="number"`. Convert them deliberately when the application needs a number, and account for empty input rather than assuming every value parses successfully.

### How Controlled Inputs Work Internally

The browser owns the actual DOM input element, but React renders its properties from the current props and state. When the user types, the browser changes the DOM value and dispatches an input event. React’s event system invokes the `onChange` handler, which schedules a state update.

React then renders with the updated state. During commit, React keeps the DOM input aligned with the value or checked state specified by the component. If the state is not updated in response to the edit, React can restore the previous controlled value.

This is why controlled input handlers should normally update the state from the event promptly:

```jsx
function EmailField() {
 const [email, setEmail] = useState("");

 return (
 <input
type="email"
value={email}
onChange={event => setEmail(event.target.value)}
 />
 );
}
```

If the handler transforms the text, such as trimming or changing letter case on every keystroke, the cursor can jump or typing can feel surprising. Many applications preserve the exact typed value while editing, then normalize or validate it on blur or submission.

React expects an input to remain consistently controlled or uncontrolled during its lifetime. Avoid starting with `value={undefined}` and later switching to a string. Use a stable initial value such as `""` for text fields and `false` for checkboxes. React warns about switching modes because the ownership of the input value becomes ambiguous.

### Real-World Example: Registration Form

Controlled inputs are useful when the UI needs to react immediately to field changes, such as showing validation feedback or enabling a submit button:

```jsx
function RegistrationForm() {
 const [email, setEmail] = useState("");
 const [password, setPassword] = useState("");

 const emailIsValid = email.includes("@");
 const canSubmit = emailIsValid && password.length >= 8;

 function handleSubmit(event) {
 event.preventDefault();
 if (!canSubmit) return;

 // Submit the current values to the application or server.
 }

 return (
 <form onSubmit={handleSubmit}>
 <label>
 Email
 <input
type="email"
value={email}
onChange={event => setEmail(event.target.value)}
aria-invalid={email !== "" && !emailIsValid}
 />
 </label>

 <label>
 Password
 <input
type="password"
value={password}
onChange={event => setPassword(event.target.value)}
 />
 </label>

 <button type="submit"disabled={!canSubmit}>
 Create account
 </button>
 </form>
 );
}
```

This makes validation and submission state available to the component as the user types. Client-side checks improve the experience, but the server must validate submitted data independently.

### Advantages of Controlled Components

-**Immediate access to values:** The component can validate, format, or react to a field change.
-**Conditional UI is straightforward:** State can determine whether to show errors, enable a button, or display the next step.
-**A clear source of truth:** The rendered field value comes from React state, which helps coordinate related UI.
-**Easy to reset or populate:** Updating state can clear a form or load existing record values.
-**Predictable testing:** Tests can interact with fields and assert the state-driven visible result.

### Disadvantages and Trade-Offs

-**More code:** Each controlled field typically needs state and an update handler.
-**Frequent updates:** Typing schedules React updates. This is normally fine, but very large forms or expensive parent renders may need careful state placement or profiling.
-**State ownership can get unwieldy:** Storing every field high in the tree can cause unnecessary coupling. Keep form state near the form unless other components genuinely need it.
-**Formatting can affect typing:** Transforming values on every change can disrupt cursor position or make input difficult.
-**Values require deliberate types:** Browser input values are often strings, so numeric or date values need appropriate parsing and validation.

## What Is an Uncontrolled Component?

An **uncontrolled input** keeps its current value in the DOM instead of mirroring each edit into React state. React can provide an initial value with `defaultValue` or `defaultChecked`, and a ref can be used to read the current value when needed.

```jsx
import { useRef } from "react";

function QuickSearch() {
 const inputRef = useRef(null);

 function handleSubmit(event) {
 event.preventDefault();
 const query = inputRef.current?.value ?? "";
 runSearch(query);
 }

 return (
 <form onSubmit={handleSubmit}>
 <label>
 Search
 <input ref={inputRef} defaultValue="" />
 </label>
 <button type="submit">Search</button>
 </form>
 );
}
```

`defaultValue` sets the initial value; it does not control later edits. After the user types, the DOM maintains the current value. Similarly, use `defaultChecked` for an initially selected checkbox.

For simple forms, the browser’s `FormData` API can read uncontrolled values at submission time without creating a ref for every field:

```jsx
function ContactForm() {
 function handleSubmit(event) {
 event.preventDefault();

 const formData = new FormData(event.currentTarget);
 const email = formData.get("email");

 submitContactRequest({ email });
 }

 return (
 <form onSubmit={handleSubmit}>
 <label>
 Email
 <input name="email"type="email"defaultValue="" />
 </label>
 <button type="submit">Send</button>
 </form>
 );
}
```

A field generally needs a `name` to be included meaningfully in submitted form data.

### Advantages and Disadvantages of Uncontrolled Inputs

**Advantages:**

- Less state and event-handler code for simple forms.
- The browser handles ordinary input editing without a React state update for every keystroke.
- `FormData` can conveniently collect values at submission.
- Can be useful when integrating a third-party widget that manages its own DOM.

**Disadvantages:**

- React does not automatically know the current value as the user types.
- Immediate validation and state-driven UI require additional event handling or reading from the DOM.
- Resetting or programmatically coordinating values can be less direct.
- Reading values depends on refs or form submission APIs, rather than ordinary component state.

### Special Case: File Inputs

File inputs are generally treated as uncontrolled. Browsers restrict scripts from setting a file picker’s selected file path, so an application cannot control it like a normal text input. Read selected files from the input’s `files` property, usually through a change event or ref.

```jsx
function UploadField() {
 function handleChange(event) {
 const files = event.target.files;
 if (files?.length) {
 uploadFile(files[0]);
 }
 }

 return <input type="file"onChange={handleChange} />;
}
```

### Background and Current Practice

Before React, form fields were typically DOM-owned: developers read values from elements at submission time, or used browser form submission directly. React’s controlled pattern makes the UI value part of its state-driven rendering model, which is especially useful for interactive validation and workflows. The uncontrolled pattern remains useful when the browser can manage the field naturally and the application only needs its value at a particular moment.

Current React applications commonly use both approaches. A simple search form or file picker may be uncontrolled; a multi-step form with live validation is often controlled. Form libraries may support either approach, sometimes using uncontrolled inputs internally for performance while exposing a convenient validation API. The choice is about how much the UI needs to respond to field values, not which approach is universally superior.

### Interview Summary

> A controlled input gets its current `value` or `checked` state from React, and an event handler updates React state when the user edits it. This makes validation and state-driven UI straightforward, at the cost of more state and handlers. An uncontrolled input keeps its current value in the DOM; React can set its initial value with `defaultValue`, and the application reads it later through a ref or `FormData`. I choose based on whether the UI needs to respond to each edit, and I keep an input consistently controlled or uncontrolled.

Updated todo list

Read-only Ask mode prevents me from editing `react.md`. The following expanded section is ready to add.

## 23. Hooks

### 25. What Are Hooks?

**Hooks** are React functions that let function components use React capabilities such as state, context, refs, effects, and update scheduling. They also let developers package reusable stateful behavior into **custom Hooks**.

Before Hooks were introduced in React 16.8, state and lifecycle behavior were primarily associated with class components. Function components were often limited to describing UI from props. Hooks made it possible to use state and reusable behavior in function components without converting them into classes.

Hooks are called from a function component or another custom Hook. They are not ordinary utility functions: React must be rendering a component when most Hooks are called so it can associate their behavior with that component.

```jsx
import { useState } from "react";

function NotificationToggle() {
 const [enabled, setEnabled] = useState(false);

 return (
 <button
type="button"
aria-pressed={enabled}
onClick={() => setEnabled(current => !current)}
 >
 Notifications: {enabled ? "On" : "Off"}
 </button>
 );
}
```

`useState` gives the component a value that persists between renders. Calling its setter schedules an update; React renders the component again with a new state snapshot.

### Common Hooks and What They Do

| Hook | Main purpose |
|---|---|
| `useState` | Holds local state and schedules updates through a setter. |
| `useReducer` | Manages state through a reducer and dispatched actions; useful when transitions are related or numerous. |
| `useContext` | Reads a value from the nearest matching Context provider. |
| `useRef` | Holds a stable mutable value or a reference to a DOM node without causing a render when `.current` changes. |
| `useEffect` | Synchronizes a component with an external system after React commits, such as a subscription or browser API. |
| `useLayoutEffect` | Runs layout-sensitive synchronization after DOM changes but before the browser paints; it can block painting. |
| `useMemo` | Caches a calculation result as a performance optimization while dependencies remain unchanged. |
| `useCallback` | Caches a function reference as a performance optimization while dependencies remain unchanged. |

These Hooks solve different problems. For example, `useRef` does not replace state when a value should update the screen, and `useMemo` does not make a component avoid rendering. Use memoization when it has a measurable benefit, not as a default decoration.

### Other React APIs That Are Hooks

The supplied outline also names several Hooks used for more specialized situations:

-**`useTransition`** marks some state updates as non-urgent so urgent interactions, such as typing, can remain responsive while React works on a larger UI update.
-**`useDeferredValue`** lets part of the UI use a deferred version of a changing value. For example, an input can update immediately while a costly result list catches up.
-**`useId`** generates IDs suitable for connecting related accessibility attributes, such as a label and its input. It is not intended to generate list keys.
-**`useSyncExternalStore`** lets a component subscribe to a state source outside React, such as a third-party store, while supporting React’s rendering model.
-**`useImperativeHandle`** customizes what a parent can access through a ref. It is useful for limited imperative operations, but ordinary props and declarative APIs are preferable when they fit.

React 19 also includes Hooks such as **`useActionState`** for state associated with actions and **`useOptimistic`** for displaying optimistic UI while an operation is pending. Availability and recommended patterns depend on the React version and framework in use, so check the project’s installed version and documentation.

## The Rules of Hooks

React relies on a predictable relationship between Hook calls and component state. Follow these rules for Hooks such as `useState`, `useEffect`, `useContext`, and custom Hooks:

1.**Call Hooks at the top level** of a function component or custom Hook.
2.**Do not call them inside conditions or loops.**
3.**Do not call them inside nested functions**, event handlers, or callbacks passed to other APIs.
4.**Call them only from React function components or custom Hooks**, not ordinary JavaScript functions or class components.

Incorrect:

```jsx
function AccountPanel({ isLoggedIn }) {
 if (isLoggedIn) {
 useEffect(() => {
 loadAccount();
 }, []);
 }

 return <div>Account</div>;
}
```

Correct:

```jsx
function AccountPanel({ isLoggedIn }) {
 useEffect(() => {
 if (!isLoggedIn) return;

 loadAccount();
 }, [isLoggedIn]);

 return <div>Account</div>;
}
```

The Hook is always called in the same place. The condition is inside the effect, where it controls the effect’s behavior rather than whether the Hook itself is called.

Conditional rendering of a child component is also valid:

```jsx
function App({ isLoggedIn }) {
 return isLoggedIn ? <AccountPanel /> : <SignInPanel />;
}
```

Each component calls its own Hooks at its top level. React decides which component to render.

### Why Must Hooks Be Called in the Same Order?

React associates Hook data with a component’s internal representation, commonly described through its **Fiber** data. During rendering, React processes Hooks in the order they are called. On the next render, it expects the same sequence so each Hook continues to correspond to the same state, effect, or memoized value.

If a Hook were called only when a condition was true, the order could shift:

```jsx
function Example({ showExtra }) {
 const [name, setName] = useState("");

 if (showExtra) {
 useEffect(() => {
 // This Hook would exist only on some renders.
 }, []);
 }

 const [count, setCount] = useState(0);
 // ...
}
```

If `showExtra` changes, React could no longer reliably associate each position in the Hook sequence with the intended Hook. Calling Hooks unconditionally preserves that ordering.

A simplified update proceeds like this:

1. React begins rendering a component.
2. Each Hook call reads or updates its associated Hook data in call order.
3. The component returns its next UI description.
4. React reconciles that output with the previous result.
5. React commits required changes. Effects are processed according to their timing after or during the commit process.

Render work can be repeated or abandoned before commit, so component rendering and Hook calculations should be pure. Effects are for synchronizing with external systems, not for work that must happen exactly once during rendering.

### Custom Hooks

A **custom Hook** is a JavaScript function whose name starts with `use` and that calls other Hooks. It lets components reuse behavior without sharing or hiding a component’s own state instance.

```jsx
import { useEffect, useState } from "react";

function useOnlineStatus() {
 const [isOnline, setIsOnline] = useState(navigator.onLine);

 useEffect(() => {
 function handleOnline() {
 setIsOnline(true);
 }

 function handleOffline() {
 setIsOnline(false);
 }

 window.addEventListener("online", handleOnline);
 window.addEventListener("offline", handleOffline);

 return () => {
 window.removeEventListener("online", handleOnline);
 window.removeEventListener("offline", handleOffline);
 };
 }, []);

 return isOnline;
}

function StatusMessage() {
 const isOnline = useOnlineStatus();

 return <p>{isOnline ? "Connected" : "Offline"}</p>;
}
```

The custom Hook reuses the subscription logic. If two components call `useOnlineStatus`, each call has its own `useState` instance and effect subscription; the custom Hook does **not** automatically create one shared global state. To share one state source, use Context, a shared owner, or an external store as appropriate.

## Real-World Guidance

**Form state:** Use `useState` for a few independent values and `useReducer` when related fields change through several coordinated actions. A form library may be appropriate for complex validation, nested fields, and submission workflows.

**Remote data:** An effect can synchronize with an external subscription or support a simple client-side request. For production data loading, framework data APIs or a server-state library can provide caching, request deduplication, retries, and stale-data handling.

**Large search results:** Keep the input update urgent. If rendering filtered results is expensive, `useDeferredValue` or `useTransition` may help keep typing responsive. Profile the actual bottleneck before introducing concurrent features.

**DOM integration:** Use `useRef` to focus an input or access an element when necessary. Prefer semantic HTML and declarative props over directly mutating DOM that React owns.

**External stores:** If a non-React store must drive React UI, `useSyncExternalStore` provides a supported subscription interface. Avoid writing a custom subscription Hook without considering concurrent rendering and consistent snapshots.

## Advantages and Trade-Offs

**Advantages:**

- Function components can use state, effects, context, and other React features without class syntax.
- Custom Hooks can extract and reuse stateful behavior.
- Related setup and cleanup can live together in an effect.
- The Hook APIs provide specific tools for different needs, from local state to external-store subscriptions.
- Hooks fit the current function-component approach used by most new React code.

**Disadvantages and risks:**

- Hook ordering rules are essential and can be confusing at first.
- Effects and dependency arrays can produce stale values or unnecessary re-synchronization if used incorrectly.
- Too many Hooks or poorly designed custom Hooks can obscure a component’s behavior.
- Memoization Hooks add dependency and maintenance complexity, and should not be required for correctness.
- Hooks do not automatically solve state ownership, data fetching, form architecture, or performance problems.
- Class components remain in existing applications, so developers may still need to understand lifecycle methods when maintaining them.

The `eslint-plugin-react-hooks` rules can detect many invalid Hook calls and missing effect dependencies. Current React tooling may also include React Compiler, which can automate some memoization when enabled. Neither tooling changes the fundamental Rules of Hooks; follow the version and configuration used by the project.

### Interview Summary

> Hooks let function components use React features such as state, context, refs, effects, and update scheduling. React associates Hook data with a component based on the order of Hook calls, which is why Hooks must be called unconditionally at the top level of function components or custom Hooks. I use each Hook for its intended purpose, keep rendering pure, and use effects for external synchronization. Custom Hooks reuse logic but do not automatically share state between components.
## **Understanding `useReducer()` in React**

`useReducer` is a React Hook for managing complex state logic with a predictable pattern. It's particularly useful when you have state that involves multiple sub-values or when the next state depends on the previous one. `useReducer` follows the Redux pattern—using a reducer function and actions—to make state transitions explicit and easier to debug.

### **What is `useReducer()`?**

`useReducer` allows you to manage state using a reducer function that processes actions. A reducer is a pure function that takes the current state and an action, then returns the new state based on the action type.

**Basic Syntax:**
```javascript
const [state, dispatch] = useReducer(reducer, initialState);
```

-**`reducer`**: A function that determines how state should change based on actions
-**`initialState`**: The starting state value
-**`state`**: The current state object
-**`dispatch`**: A function that sends actions to the reducer

### **Reducer Function Structure**

```javascript
function reducer(state, action) {
 switch (action.type) {
 case "INCREMENT":
 return {
 ...state,
 count: state.count + 1
 };
 case "DECREMENT":
 return {
 ...state,
 count: state.count - 1
 };
 case "RESET":
 return {
 count: 0,
 step: 1
 };
 default:
 return state;
 }
}
```

### **Dispatching Actions**

Actions are objects with a `type` property and optional `payload`:

```javascript
// Simple action
dispatch({ type: "INCREMENT" });

// Action with payload
dispatch({ type: "ADD_TODO", payload: { id: 1, text: "Buy milk" } });

// Complex action
dispatch({
 type: "UPDATE_USER",
 payload: { name: "John", age: 30, email: "john@example.com" }
});
```

---

## **Real-Time Examples with Detailed Scenarios**

### **Example 1: Shopping Cart Management**

```javascript
const initialCartState = {
 items: [],
 totalPrice: 0,
 itemCount: 0,
 loading: false,
 error: null
};

function cartReducer(state, action) {
 switch (action.type) {
 case "ADD_ITEM":
 const newItems = [...state.items, action.payload];
 return {
 ...state,
 items: newItems,
 totalPrice: state.totalPrice + action.payload.price,
 itemCount: state.itemCount + 1
 };

 case "REMOVE_ITEM":
 const filteredItems = state.items.filter(item => item.id !== action.payload);
 const removedItem = state.items.find(item => item.id === action.payload);
 return {
 ...state,
 items: filteredItems,
 totalPrice: state.totalPrice - (removedItem?.price || 0),
 itemCount: state.itemCount - 1
 };

 case "UPDATE_QUANTITY":
 return {
 ...state,
 items: state.items.map(item =>
 item.id === action.payload.id
 ? { ...item, quantity: action.payload.quantity }
 : item
 ),
 totalPrice: state.items.reduce((total, item) => {
 if (item.id === action.payload.id) {
 return total - (item.price * item.quantity) + (item.price * action.payload.quantity);
 }
 return total;
 }, state.totalPrice)
 };

 case "CLEAR_CART":
 return initialCartState;

 case "SET_LOADING":
 return { ...state, loading: action.payload };

 case "SET_ERROR":
 return { ...state, error: action.payload };

 default:
 return state;
 }
}
function ShoppingCart() {
 const [cart, dispatch] = useReducer(cartReducer, initialCartState);

 const addItem = (item) => {
 dispatch({ type: "ADD_ITEM", payload: item });
 };

 const removeItem = (itemId) => {
 dispatch({ type: "REMOVE_ITEM", payload: itemId });
 };

 return (
 <div>
 <h2>Cart Items: {cart.itemCount}</h2>
 <p>Total: ${cart.totalPrice.toFixed(2)}</p>
 <button onClick={() => addItem({ id: 1, name: "Laptop", price: 999 })}>
 Add Laptop
 </button>
 <button onClick={() => removeItem(1)}>Remove Item</button>
 </div>
 );
}
```

### **Example 2: Form with Multiple Fields and Validation**

```javascript
const initialFormState = {
 email: "",
 password: "",
 confirmPassword: "",
 errors: {},
 isSubmitting: false,
 isValid: false
};

function formReducer(state, action) {
 switch (action.type) {
 case "UPDATE_FIELD":
 return {
 ...state,
 [action.payload.field]: action.payload.value
 };

 case "SET_ERROR":
 return {
 ...state,
 errors: {
 ...state.errors,
 [action.payload.field]: action.payload.error
 }
 };

 case "CLEAR_ERRORS":
 return {
 ...state,
 errors: {}
 };

 case "SET_SUBMITTING":
 return {
 ...state,
 isSubmitting: action.payload
 };

 case "RESET_FORM":
 return initialFormState;

 default:
 return state;
 }
}

function RegistrationForm() {
 const [form, dispatch] = useReducer(formReducer, initialFormState);

 const handleChange = (e) => {
 const { name, value } = e.target;
 dispatch({ type: "UPDATE_FIELD", payload: { field: name, value } });
 };

 const validateForm = () => {
 dispatch({ type: "CLEAR_ERRORS" });
 const newErrors = {};

 if (!form.email.includes("@")) newErrors.email = "Invalid email";
 if (form.password.length < 8) newErrors.password = "Min 8 characters";
 if (form.password !== form.confirmPassword) {
 newErrors.confirmPassword = "Passwords don't match";
 }

 Object.entries(newErrors).forEach(([field, error]) => {
 dispatch({ type: "SET_ERROR", payload: { field, error } });
 });

 return Object.keys(newErrors).length === 0;
 };

 const handleSubmit = async (e) => {
 e.preventDefault();
 if (!validateForm()) return;

 dispatch({ type: "SET_SUBMITTING", payload: true });
 // API call here
 dispatch({ type: "SET_SUBMITTING", payload: false });
 };

 return (
 <form onSubmit={handleSubmit}>
 <input
name="email"
value={form.email}
onChange={handleChange}
placeholder="Email"
 />
 {form.errors.email && <span>{form.errors.email}</span>}
 {/* Similar fields for password */}
 <button type="submit"disabled={form.isSubmitting}>
 {form.isSubmitting ? "Submitting..." : "Register"}
 </button>
 </form>
 );
}
```

### **Example 3: Async Operations with Loading States**

```javascript
const initialState = {
 data: null,
 loading: false,
 error: null
};

function dataReducer(state, action) {
 switch (action.type) {
 case "FETCH_START":
 return { ...state, loading: true, error: null };
 case "FETCH_SUCCESS":
 return { data: action.payload, loading: false, error: null };
 case "FETCH_ERROR":
 return { ...state, loading: false, error: action.payload };
 default:
 return state;
 }
}

function DataFetcher() {
 const [state, dispatch] = useReducer(dataReducer, initialState);

 useEffect(() => {
 dispatch({ type: "FETCH_START" });
 fetch("/api/data")
 .then(res => res.json())
 .then(data => dispatch({ type: "FETCH_SUCCESS", payload: data }))
 .catch(err => dispatch({ type: "FETCH_ERROR", payload: err.message }));
 }, []);

 return (
 <>
 {state.loading && <p>Loading...</p>}
 {state.error && <p>Error: {state.error}</p>}
 {state.data && <p>Data: {state.data}</p>}
 </>
 );
}
```

---

## **Advantages of `useReducer`**

1.**Predictable State Management**: Centralized logic makes state changes explicit and trackable
2.**Better Scalability**: Handles complex state with multiple related fields efficiently
3.**Easier Debugging**: You can log every action to see exactly what changed and why
4.**Testability**: Pure reducer functions are easy to unit test in isolation
5.**Performance Optimization**: Can optimize by passing dispatch to child components instead of multiple callbacks
6.**Complex State Transitions**: Manages interdependent state updates elegantly
7.**Undo/Redo Capability**: Easier to implement with action history

## **Disadvantages of `useReducer`**

1.**Boilerplate Code**: More setup required compared to `useState`
2.**Learning Curve**: Requires understanding of reducer pattern
3.**Overkill for Simple State**: Unnecessary complexity for single-value state
4.**Action Objects Everywhere**: Can lead to verbose action dispatching
5.**No Built-in Middleware**: Unlike Redux, no middleware support out-of-the-box
6.**Debugging Tools**: Limited compared to Redux DevTools

---

## **useState vs useReducer**

| Feature | useState | useReducer |
|---------|----------|-----------|
| **Complexity** | Simple, single value | Complex, multiple values |
| **Code** | Less boilerplate | More boilerplate |
| **Learning** | Easier | Steeper curve |
| **Use Case** | Toggle, simple forms | Shopping carts, multi-step forms |
| **Performance** | Good for simple | Better for complex |
| **Debugging** | Basic logging | Action-based logging |

**Use `useState`** when managing a single piece of state or simple boolean flags.

**Use `useReducer`** when state has multiple related fields, many actions can change it, or transitions are complex and interdependent.

---

You can copy this expanded version into your `collection.md` file to replace the existing section!
Updated todo list

Read-only Ask mode prevents me from editing `react.md`. The following expanded section is ready to add.

## 26. Redux

### 28. What Is Redux?

**Redux** is a predictable state-management library for storing and updating shared application state. It is not limited to React, although **React Redux** provides the standard bindings for using a Redux store from React components.

Redux is useful when multiple parts of an application need coordinated access to the same state, or when state transitions benefit from explicit rules, centralized debugging, and middleware. It is not automatically necessary for every application. Local component state, Context, or another store may be simpler when the shared-state requirements are small.

The core Redux concepts are:

| Concept | Purpose |
|---|---|
| **Store** | Holds the current application state and provides ways to dispatch actions and subscribe to changes. |
| **Action** | Describes an event or requested change, commonly as an object with a `type` and optional payload. |
| **Reducer** | A pure function that calculates the next state from the current state and an action. |
| **Dispatch** | Sends an action to the store so it can be processed. |
| **Selector** | Reads or derives a value from the store state for a component or other consumer. |
| **Middleware** | Intercepts or observes dispatched actions, commonly to support asynchronous work, logging, or other cross-cutting behavior. |

Redux is often introduced through the idea of a **single source of truth**: shared application state is kept in a store, and changes follow a defined path. This can make state updates easier to trace than having unrelated components modify shared data in different ways.

### Redux Data Flow

The simplified flow is:

```text
User interaction
 ↓
Component dispatches an action
 ↓
Middleware may inspect or handle the action
 ↓
Reducer calculates the next state
 ↓
Store saves the next state and notifies subscribers
 ↓
Selectors read updated data
 ↓
React components update when their selected values change
```

For example, when a user adds a product to a cart, the component dispatches an `addItem` action. A reducer calculates the updated cart state. Components displaying the cart count and total then receive the selected values and update their UI.

Redux reducers must be **pure functions**: they should return the next state based on their inputs without directly changing the store, making network requests, or performing other side effects. This predictability is what makes it possible to inspect actions and state transitions reliably.

### Example with Redux Toolkit

For modern Redux applications, the recommended approach is **Redux Toolkit (RTK)**. It reduces boilerplate and provides the standard tools for store configuration, reducer creation, and common async patterns.

A slice groups related state, reducer logic, and action creators:

```jsx
import { createSlice, configureStore } from "@reduxjs/toolkit";

const cartSlice = createSlice({
 name: "cart",
 initialState: {
 items: []
 },
 reducers: {
 addItem(state, action) {
 state.items.push(action.payload);
 },
 removeItem(state, action) {
 state.items = state.items.filter(
 item => item.id !== action.payload
 );
 }
 }
});

export const { addItem, removeItem } = cartSlice.actions;

const store = configureStore({
 reducer: {
 cart: cartSlice.reducer
 }
});
```

The reducer appears to mutate `state`, but Redux Toolkit uses **Immer** internally to turn these draft-style changes into immutable state updates. The actual stored state is not supposed to be mutated directly.

A React application typically makes the store available through React Redux’s `Provider`, then uses `useSelector` to read selected data and `useDispatch` to send actions:

```jsx
import { Provider, useDispatch, useSelector } from "react-redux";

function CartCount() {
 const itemCount = useSelector(state => state.cart.items.length);
 const dispatch = useDispatch();

 return (
 <button onClick={() => dispatch(addItem({ id: "p-7", name: "Notebook" }))}>
 Add item ({itemCount})
 </button>
 );
}

function App() {
 return (
 <Providerstore={store}>
 <CartCount />
 </Provider>
 );
}
```

In a real application, define the store and slices in suitable modules and include the actual application entry point. The example focuses on the main concepts: `Provider` connects React to the store, `useSelector` reads state, and dispatching an action requests a state transition.

## How Redux Works Internally

At a high level, a Redux store keeps its current state and a reducer function. When application code dispatches an action:

1.**The action enters the dispatch pipeline.** Middleware, if configured, can inspect it, log it, delay it, or perform asynchronous work before forwarding an action.
2.**The reducer runs.** The reducer receives the current state and action, then calculates the next state. Redux Toolkit’s `createSlice` generates action creators and action types for the slice.
3.**The store adopts the next state.** Redux makes the resulting state available as the current store state.
4.**Subscribers are notified.** React Redux checks the values selected by subscribed components.
5.**Relevant components render again.** A component using `useSelector` is updated when its selected result changes according to the selector’s comparison behavior. It is not necessary for every component to render after every action.

Selectors are important because components should generally depend on the smallest useful part of state, not the entire store. A selector can also derive a value such as a filtered list or cart total. Memoized selectors are useful when a derivation is expensive or when stable result identity matters; simple calculations do not need memoization by default.

Middleware is a pipeline around dispatch. For example, middleware can handle an asynchronous action, perform a request, and dispatch a success or failure action when the request completes. Redux Toolkit includes **thunk middleware** by default. For many server-data needs, **RTK Query** offers a purpose-built API for fetching, caching, and managing request status; remote data does not always need to be manually copied into ordinary Redux slices.

## Real-World Examples

**Shopping cart:** A cart slice can respond to actions such as adding an item, removing it, or changing its quantity. The header selects the item count, while the cart page selects the items and totals. Components can update independently based on the values they use.

**Operations dashboard:** Several screens may need the same selected batch, filter settings, or user interface preferences. A shared store can provide a consistent state source, and actions such as `batchSelected` or `filterChanged` make transitions observable.

**Multi-step workflow:** A workflow might have related transitions such as starting a review, recording a decision, and submitting the result. Reducers can encode valid state transitions, while middleware or async thunks handle network calls and dispatch completion or failure events.

**Remote search results:** Redux can be part of the solution, but caching and request lifecycle are server-state concerns. RTK Query or another data-fetching layer may be more suitable than building request caching, invalidation, retries, and loading flags by hand.

## Background and Related Approaches

Redux was introduced as an approach to predictable, one-way state updates, influenced by Flux’s unidirectional data flow and functional reducer patterns. Before a shared store, applications often relied on component-local state, passing props through the tree, or custom event systems. Those approaches can work well, but updates become harder to trace when many distant components coordinate the same data.

Redux’s explicit actions and reducers make state transitions easier to inspect and test. Early Redux usage often involved substantial boilerplate: manually defining action types, action creators, reducers, and store setup. **Redux Toolkit is now the recommended way to write Redux**, addressing much of that repetitive setup while preserving the Redux model.

Redux is one option among several. Use component state for local UI concerns, Context for suitable values shared through a subtree, or a store such as Redux Toolkit or Zustand when broader coordination and tooling justify it. For API data, consider framework data APIs or a server-state library such as TanStack Query or RTK Query. These choices can coexist; there is no requirement that one tool manage every kind of state.

## Advantages and Disadvantages

**Advantages:**

-**Predictable updates:** Reducers make state transitions explicit and testable.
-**Centralized shared state:** Distant features can use a consistent source for application-wide data.
-**Traceability:** Actions provide a record of what events led to a state change.
-**Developer tooling:** Redux DevTools can inspect actions and state history, supporting debugging and time-travel workflows.
-**Middleware support:** Cross-cutting behavior such as logging and asynchronous orchestration can be added to the dispatch pipeline.
-**Redux Toolkit reduces boilerplate:** Slices, `configureStore`, and Immer make common Redux patterns more concise.

**Disadvantages and trade-offs:**

-**More architecture to learn:** Teams must understand actions, reducers, selectors, dispatch, store setup, and middleware.
-**May be unnecessary:** A small application or local UI state may be simpler with `useState` or Context.
-**State design still matters:** Poorly structured state or overly broad selectors can create coupling and unnecessary updates.
-**Async behavior needs a strategy:** Requests require error handling, loading states, cancellation or stale-result handling, and caching as appropriate.
-**Not a security boundary:** Client-side Redux state is controlled by the user’s environment. Authorization and data protection must be enforced by the server.
-**Server state and client state differ:** Treating all fetched data as manually managed global state can duplicate capabilities already provided by a data-fetching library.

### Interview Summary

> Redux is a predictable state-management library that centralizes shared state and updates it through dispatched actions and pure reducers. Middleware can extend the dispatch pipeline, and selectors let components read the state they need. In React, React Redux provides `Provider`, `useSelector`, and `useDispatch`. For modern applications, Redux Toolkit is the recommended Redux approach. I choose Redux when shared state complexity, traceability, or tooling justify it; I use local state, Context, or a server-state tool when those better fit the problem.

Updated todo list

Read-only Ask mode prevents me from editing `react.md`. The following expanded section is ready to add.

## 27. Redux Toolkit

### 29. Why Is Redux Toolkit Preferred?

**Redux Toolkit (RTK)** is the officially recommended way to write Redux logic. It keeps Redux’s predictable state flow while reducing the repetitive setup that older Redux code often required.

RTK provides tools for:

-**Store configuration** with `configureStore`.
-**Slices** that group related state, reducers, and generated action creators.
-**Immutable updates** through Immer.
-**Asynchronous work** through tools such as `createAsyncThunk`.
-**Server data management** through RTK Query.
-**Boilerplate reduction** while retaining Redux’s action-and-reducer model.

Redux Toolkit is not a separate state-management concept from Redux. It is the modern, opinionated toolset for writing Redux applications. The usual React integration is still provided by **React Redux**, which exposes the store to components and offers Hooks such as `useSelector` and `useDispatch`.

### Creating a Slice

A **slice** is a feature-oriented part of the Redux state, together with the reducers that update it. `createSlice` generates action creators and action type strings from its reducer definitions.

```jsx
import { createSlice } from "@reduxjs/toolkit";

const counterSlice = createSlice({
 name: "counter",
 initialState: {
 count: 0
 },
 reducers: {
 increment(state) {
 state.count += 1;
 },
 decrement(state) {
 state.count -= 1;
 },
 incrementByAmount(state, action) {
 state.count += action.payload;
 }
 }
});

export const {
increment,
decrement,
incrementByAmount
} = counterSlice.actions;

export default counterSlice.reducer;
```

The slice name helps form action types such as `"counter/increment"`. The generated `increment()` function creates an action object with that type. A component can dispatch the action without manually writing the action type string:

```jsx
dispatch(incrementByAmount(5));
```

This reduces spelling errors and keeps action creation consistent with the slice’s reducer definitions.

### Configuring the Store

Use `configureStore` to create the Redux store and combine feature reducers:

```jsx
import { configureStore } from "@reduxjs/toolkit";
import counterReducer from "./counterSlice";

export const store = configureStore({
 reducer: {
 counter: counterReducer
 }
});
```

The application connects the store to React with React Redux’s `Provider`. Components can then select values and dispatch actions:

```jsx
import { Provider, useDispatch, useSelector } from "react-redux";
import { increment } from "./counterSlice";
import { store } from "./store";

function Counter() {
 const count = useSelector(state => state.counter.count);
 const dispatch = useDispatch();

 return (
 <button onClick={() => dispatch(increment())}>
 Count: {count}
 </button>
 );
}

function App() {
 return (
 <Providerstore={store}>
 <Counter />
 </Provider>
 );
}
```

`useSelector` reads the part of the store that the component needs. `useDispatch` provides the store’s dispatch function. In a larger TypeScript application, teams commonly create typed versions of these Hooks so components receive the correct state and dispatch types.

## Isn’t `state.count++` Mutation?

In an RTK case reducer, code like this is valid:

```jsx
increment(state) {
 state.count++;
}
```

It looks like direct mutation, but `state` is an **Immer draft**, not the ordinary current Redux state object. Immer tracks the draft changes. When the reducer finishes, it produces a new immutable state value, preserving unchanged references where possible.

The Redux state remains immutable from the application’s perspective. The draft syntax is a more convenient way to describe changes, particularly when updating nested objects or arrays.

The safety applies **inside RTK reducers**. Do not use this syntax to mutate Redux state in a component, selector, or ordinary function:

```jsx
// Do not mutate a value returned from useSelector.
const counter = useSelector(state => state.counter);
counter.count++;
```

Also, a case reducer should either modify the Immer draft **or** return a replacement state value. Avoid doing both in the same reducer, because the intended result would be ambiguous.

For example, returning a new object is also valid:

```jsx
reset() {
 return { count: 0 };
}
```

## How Redux Toolkit Works Internally

When a component dispatches an action, the Redux store processes it through the configured middleware and reducer pipeline:

1.**An event occurs.** A button click or another application event decides that a state transition is needed.
2.**The component dispatches an action.** For example, `dispatch(increment())` sends an action describing the event.
3.**Middleware runs.** Middleware can inspect the action, perform logging, handle asynchronous work, or pass the action onward. Ordinary slice reducers are synchronous; async work is handled by middleware or a data-fetching tool.
4.**The reducer calculates next state.** Redux Toolkit calls the matching reducer. Immer tracks draft changes and creates the immutable result.
5.**The store publishes the update.** The new state becomes current, and subscribers are notified.
6.**React Redux checks selected values.** Components using `useSelector` update when their selected results change according to the Hook’s comparison behavior.

`configureStore` supplies useful defaults, including thunk middleware and development checks for common Redux mistakes. It also integrates with Redux DevTools when available. This provides a solid baseline without requiring teams to assemble the common middleware and development configuration manually.

### Handling Asynchronous Logic

A slice reducer should remain synchronous and pure. For an operation such as loading claim details, RTK provides `createAsyncThunk` to represent pending, fulfilled, and rejected request states.

```jsx
import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";

export const fetchClaim = createAsyncThunk(
"claims/fetchClaim",
 async claimId => {
 const response = await fetch(`/api/claims/${claimId}`);

 if (!response.ok) {
 throw new Error("Could not load claim");
 }

 return response.json();
 }
);

const claimsSlice = createSlice({
 name: "claims",
 initialState: {
 claim: null,
 status: "idle",
 error: null
 },
 reducers: {},
 extraReducers: builder => {
 builder
 .addCase(fetchClaim.pending, state => {
 state.status = "loading";
 state.error = null;
 })
 .addCase(fetchClaim.fulfilled, (state, action) => {
 state.status = "succeeded";
 state.claim = action.payload;
 })
 .addCase(fetchClaim.rejected, (state, action) => {
 state.status = "failed";
 state.error = action.error.message;
 });
 }
});
```

The thunk performs the request outside the reducer and dispatches lifecycle actions as the operation proceeds. The reducers then make loading, success, and error states explicit.

For applications that need API caching, request deduplication, cache invalidation, and request lifecycle management, **RTK Query** may be a better fit than manually writing a thunk and slice for every endpoint. It is part of Redux Toolkit and is designed specifically for server data. Not every API response needs to be copied into hand-managed Redux state.

## Real-World Examples

**Cart feature:** A cart slice can handle adding products, changing quantities, removing items, and clearing the cart. The header can select the item count, while the checkout screen selects the item details. Calculated totals can be derived rather than stored redundantly.

**Claims dashboard:** A slice can hold client-side filters or the currently selected claim when multiple features need that information. RTK Query can load and cache claim records from the server, while components select the records and render the relevant states.

**Multi-step workflow:** Actions such as `reviewStarted`, `decisionRecorded`, and `reviewSubmitted` make workflow transitions explicit. Reducers can keep related client-side status consistent, while asynchronous operations are handled by thunks or API tooling.

**Feature preferences:** Shared UI preferences, such as selected dashboard layout or active filters, may belong in Redux if multiple distant components need coordinated access. A local panel’s open/closed state usually belongs in local component state instead.

## Background and Current Industry Practice

Early Redux applications often used separate files and hand-written code for action type constants, action creators, reducers, and store setup. This made the data flow explicit but could produce substantial boilerplate. Redux Toolkit was created to make the recommended Redux patterns easier to use and harder to misconfigure.

Current Redux guidance is to use **Redux Toolkit for Redux logic**, **React Redux Hooks for React integration**, and **RTK Query when its server-data features fit the application**. Older patterns such as manually defining every action type or using legacy `createStore` setups may appear in existing projects, but they are generally not the starting point for new Redux code.

RTK does not mean every piece of application state belongs in Redux. Use component state for local interaction details, Context for appropriate subtree-wide values, and a server-state solution for API caching and synchronization. Redux Toolkit is most valuable when its explicit update flow, shared state, middleware, or debugging tools solve a real application need.

### Advantages and Disadvantages

**Advantages:**

-**Less boilerplate:** `createSlice` generates action creators and action types from reducer definitions.
-**Readable immutable updates:** Immer allows concise draft-style updates while preserving immutable state.
-**Good defaults:** `configureStore` sets up standard middleware and development checks.
-**Explicit transitions:** Reducers make state changes centralized, testable, and easier to review.
-**Strong tooling:** Redux DevTools can help trace actions and inspect state history.
-**Async and API options:** Thunks and RTK Query cover common asynchronous and server-data use cases.

**Disadvantages and trade-offs:**

-**Still has concepts to learn:** Store, slices, reducers, actions, selectors, middleware, and async behavior require familiarity.
-**Can be overkill:** Local state or Context may be clearer for a small, isolated feature.
-**Requires thoughtful state boundaries:** Putting everything in one global store can increase coupling.
-**Async code needs deliberate handling:** Errors, stale requests, loading states, and caching still need appropriate design.
-**Client state is not authoritative security data:** Server authorization must be enforced independently.
-**RTK is an abstraction, not magic:** Developers still need to understand Redux flow and avoid mutating state outside Immer-managed reducers.

### Interview Summary

> Redux Toolkit is the recommended modern way to write Redux logic. `createSlice` groups state and reducers while generating action creators, `configureStore` provides standard setup, and Immer lets reducers use draft-style updates that produce immutable state. React Redux connects the store to components through `Provider`, `useSelector`, and `useDispatch`. For asynchronous work, use tools such as `createAsyncThunk`; for API caching and request management, consider RTK Query. I choose RTK when shared state and its debugging or update conventions justify the added structure, rather than putting all state into Redux by default.

Updated todo list

Read-only Ask mode prevents me from editing `react.md`. The following expanded section is ready to add.

## 28. React Router

### 30. What Is React Router?

**React Router** is a routing library for React applications. It connects a URL, such as `/users/42`, to the UI that should appear for that location. It can also provide navigation links, route parameters, nested layouts, query-string handling, and, in its data-oriented modes, route-based data loading and form actions.

A basic declarative setup might look like this:

```jsx
import {
 BrowserRouter,
 Link,
 Route,
 Routes
} from "react-router-dom";

function App() {
 return (
 <BrowserRouter>
 <nav>
 <Linkto="/users">Users</Link>
 <Linkto="/products">Products</Link>
 </nav>

 <Routes>
 <Routepath="/users"element={<Users />} />
 <Routepath="/products"element={<Products />} />
 <Routepath="*"element={<NotFound />} />
 </Routes>
 </BrowserRouter>
 );
}
```

`BrowserRouter` connects routing to the browser’s URL and history. `Routes` selects a matching route, and each `Route` associates a path with an element to render. `Link` provides client-side navigation while retaining normal link semantics, including keyboard support and opening links in new tabs.

Imports depend on the React Router version and mode. React Router v6 examples commonly import these APIs from `react-router-dom`; current v7 projects may use the `react-router` package or its framework conventions. Follow the installed version and project setup.

### Dynamic Routes and URL Data

Routes can contain parameters for identifying a particular resource:

```jsx
<Routepath="/users/:userId"element={<UserDetails />} />
```

A component can read that parameter with `useParams`:

```jsx
import { useParams } from "react-router-dom";

function UserDetails() {
 const { userId } = useParams();

 return <h1>User {userId}</h1>;
}
```

For `/users/42`, `userId` is `"42"`. Route parameters are strings, so convert and validate them if the application needs another type.

Query-string values are separate from route parameters. A URL like `/users?status=active&page=2` can encode filters or pagination state. React Router’s `useSearchParams` can read and update those values. Putting shareable view state in the URL lets users bookmark a search, reload it, or send it to someone else.

### Nested Routes and Layouts

A dashboard often has a shared layout with several child screens. Nested routes let the layout remain in place while the child route changes:

```jsx
import { Outlet, Route, Routes } from "react-router-dom";

function DashboardLayout() {
 return (
 <div>
 <DashboardNavigation />
 <main>
 <Outlet />
 </main>
 </div>
 );
}

function AppRoutes() {
 return (
 <Routes>
 <Routepath="/dashboard"element={<DashboardLayout />}>
 <Routeindexelement={<DashboardHome />} />
 <Routepath="users"element={<Users />} />
 <Routepath="settings"element={<Settings />} />
 </Route>
 </Routes>
 );
}
```

`Outlet` marks where the matching child route renders. This avoids repeating the same navigation and layout across every dashboard screen.

## How Client-Side Routing Works

With ordinary full-page navigation, clicking a link asks the browser to request a new document from the server. The server responds with a page, and the browser loads it.

In a client-routed application, navigation usually follows a different path:

1. The user activates a router link.
2. The router recognizes the navigation and updates the browser’s history and URL.
3. React Router matches the new location against the route definitions.
4. The matching route elements render within the existing application.
5. The browser updates the displayed UI without loading a new document.

For `BrowserRouter`, the browser History API is central to this behavior. The router can update the URL with `pushState` and respond to back/forward navigation through browser history events such as `popstate`. A client-side route change usually avoids a full document reload, but it **does not mean the application makes no network requests**. A route may load data, request code-split bundles, or call an API.

For example, moving from `users` to `/users/42` may preserve the shared application shell while loading the details for user 42. Route-based data APIs can coordinate loading and error handling with navigation, depending on the router mode and version.

## Client-Side Routing vs Server-Side Routing

**Client-side routing** changes the UI in the browser without requesting a new HTML document for each navigation. It can make transitions feel quick and preserve application state, but the browser still needs the application assets and may request route data.

**Server-side routing** generally means the browser requests a document for a URL and the server decides what response to return. The server might return a rendered HTML page, redirect, or error response. This is a natural fit for traditional multi-page applications.

These approaches are not mutually exclusive. Modern React frameworks can use server rendering for the initial document and client-side navigation afterward. Some routes or requests may be handled on the server while interactive transitions happen in the client.

A critical deployment detail is **direct URL access and refresh**. If a user opens `/users/42` directly, the browser requests that path from the server before React Router runs. A single-page application server usually needs a fallback rule that serves the application’s entry document for client routes. Without that setup, navigation may work after the app loads but refreshing a nested route can return a server 404.

### Real-World Examples

**User directory:** `users` shows the list, `/users/42` shows one user, and `?page=2` records the current page. Route parameters identify the selected user; query parameters can represent shareable filters or pagination.

**Admin dashboard:** `/dashboard/reports` and `/dashboard/settings` can share navigation and a header through a parent layout with an `Outlet`.

**Protected pages:** A route can render different UI depending on whether a user appears authenticated, or redirect them to a sign-in page. This is a user-experience feature, not a security control. The server must still authenticate requests and authorize access to protected data.

**Not-found page:** A catch-all route such as `path="*"` can show a useful not-found screen when no route matches. The server should also return appropriate HTTP status codes when serving routes directly, especially for server-rendered applications.

## Advantages and Disadvantages

**Advantages:**

-**Navigation without full document reloads:** Client-side transitions can preserve shared application UI and state.
-**URLs represent application locations:** Users can bookmark, share, refresh, and navigate with browser back/forward controls.
-**Nested layouts:** Shared page structure can be defined once and reused across child routes.
-**Route parameters and query strings:** Resource IDs and shareable view state can be represented in the URL.
-**Data-routing features:** Depending on the chosen mode, routes can coordinate loading, actions, pending UI, and error handling.
-**Flexible rendering strategies:** React Router can be used in client-rendered apps and within broader server-rendering or framework architectures.

**Disadvantages and trade-offs:**

-**Additional concepts:** Route matching, nested routes, loaders, navigation state, redirects, and error boundaries take time to learn.
-**Deployment configuration matters:** Client-side routes need suitable server fallback behavior for direct navigation and refresh.
-**Client routing does not eliminate network latency:** Route data and code may still need to load.
-**Route-level security is not authorization:** Hiding a page in the UI does not protect its API or data.
-**The router must fit the project:** React Router, framework-provided routers, and other routing libraries have different conventions and capabilities.
-**Routing can become tangled:** Poorly planned route structure, duplicated layouts, or state stored in the wrong place can make navigation harder to maintain.

### Other Approaches and Current Practice

A small React widget may not need a router. A traditional multi-page application can rely on server routes and ordinary links. Larger React applications commonly use a router or a framework that provides routing as part of its application model. Alternatives include framework-specific routing systems and libraries such as TanStack Router; the right choice depends on the project’s data-loading model, server-rendering needs, type-safety requirements, and team conventions.

Current React Router projects may use declarative routing or a data/framework-oriented mode. Declarative routing associates paths with components. Data-oriented routing can associate routes with loaders, actions, pending states, and error boundaries. Prefer the approach already adopted by the project rather than mixing patterns without a reason.

### Interview Summary

> React Router maps URLs to React UI and provides navigation, route parameters, nested layouts, and other routing features. In client-side routing, the router updates browser history, matches the new URL, and renders the relevant route without loading a new document. It may still fetch route data. Server-side routing typically handles a document request on the server, and modern frameworks often combine server rendering with client navigation. For a client-rendered app, the server must be configured to serve the app entry point for direct route requests, and real authorization must remain on the server.
Updated todo list

Read-only Ask mode prevents me from editing `react.md`. The following expanded sections are ready to add.

## 29. Lazy Loading

### 31. What Is Lazy Loading in React?

**Lazy loading** means deferring the loading of some code or content until it is needed. In a React application, it commonly refers to **code splitting**: instead of including every page or feature in the initial JavaScript bundle, the build system creates separate bundles that can be requested when a feature is about to render.

React’s `lazy` function can load a component through a dynamic `import()`:

```jsx
import { lazy, Suspense } from "react";

const Admin = lazy(() => import("./Admin"));
```

The imported module must provide the component as its **default export**:

```jsx
// Admin.jsx
export default function Admin() {
 return <h1>Administration</h1>;
}
```

The lazy component can then be rendered inside a `Suspense` boundary:

```jsx
<Suspensefallback={<p>Loading administration...</p>}>
 <Admin />
</Suspense>
```

The dynamic import returns a Promise. Bundlers and frameworks commonly use it as a signal to create a separate JavaScript chunk. When React first renders `Admin`, it waits for the module to load and shows the nearest Suspense fallback in the meantime. Once the module is available, React renders the component.

`lazy` should generally be declared **outside** the component that renders it:

```jsx
const Admin = lazy(() => import("./Admin"));

function App() {
 return (
 <Suspensefallback={<p>Loading...</p>}>
 <Admin />
 </Suspense>
 );
}
```

Declaring a new lazy component inside `App` on each render can create a new component type repeatedly, which may cause remounting and state resets.

### What Happens Internally?

A simplified sequence is:

1. The browser loads the application’s initial JavaScript.
2. React renders the route or component tree.
3. React encounters the lazy component and begins its dynamic import if it has not already loaded.
4. While the import is pending, React shows the fallback from the nearest Suspense boundary.
5. Once the module resolves, React retries the suspended rendering work and displays the loaded component.

The bundler determines how the import becomes files and when the browser requests them. React coordinates rendering and the fallback; it does not itself decide how many chunks the bundler creates or guarantee that the import will be faster.

If the import fails, `Suspense` does not act as an error message for that failure. Handle the error with an **Error Boundary** or the framework’s route error handling, and provide an intentional retry or recovery experience where appropriate.

### Why Use Lazy Loading?

The main motivation is to reduce the amount of JavaScript a user must download and process before the initial screen becomes interactive. This can be useful for features that are large, infrequently visited, or not needed for the first view.

Common candidates include:

- Admin or reporting sections used by only some users.
- Large charts, editors, maps, or visualization libraries.
- Secondary routes in a multi-page application.
- Dialogs or tools that are opened only after a user action.

For example, a user visiting a dashboard overview may not need to download a complex report editor immediately. Loading it when they navigate to the reporting area can keep the initial route’s JavaScript smaller.

### Real-World Example: Lazy Route Content

```jsx
import { lazy, Suspense } from "react";
import { Route, Routes } from "react-router-dom";

const Reports = lazy(() => import("./Reports"));
const Settings = lazy(() => import("./Settings"));

function AppRoutes() {
 return (
 <Suspensefallback={<p role="status">Loading page...</p>}>
 <Routes>
 <Routepath="/reports"element={<Reports />} />
 <Routepath="/settings"element={<Settings />} />
 </Routes>
 </Suspense>
 );
}
```

The route components can be loaded only when the router renders them. In larger apps, framework or router conventions may provide route-level code splitting without writing `lazy` calls by hand. Follow the application’s existing routing and build conventions.

### Advantages and Trade-Offs

**Advantages:**

- Can reduce the initial JavaScript payload.
- Can reduce parse and execution work for code that is not needed on the first screen.
- Lets the application load large or infrequently used features on demand.
- Works with Suspense boundaries to provide a loading state while code is pending.

**Disadvantages and risks:**

- A feature may feel slower the first time it is opened because the browser must fetch its chunk.
- Too many small chunks can add network overhead and make loading behavior harder to manage.
- Poorly placed boundaries can replace a large section of the page with a fallback.
- Failed chunk requests need error handling; Suspense alone does not provide it.
- Lazy loading does not guarantee better performance. Measure initial bundle size, route usage, and loading behavior.

Lazy loading is different from **lazy-loading images**. For images, HTML’s `loading="lazy"` or an image component’s equivalent defers media loading. `React.lazy` is for loading React component code.

## 30. Suspense

### 32. What Is Suspense?

`Suspense` is a React component that defines a **loading boundary**. If a descendant suspends while rendering, React can show the boundary’s `fallback` until that descendant is ready.

```jsx
import { Suspense, lazy } from "react";

const Admin = lazy(() => import("./Admin"));

function AdminPage() {
 return (
 <Suspensefallback={<p role="status">Loading administration...</p>}>
 <Admin />
 </Suspense>
 );
}
```

In this example, the fallback appears while React waits for the lazy-loaded module. Once `Admin` is ready, React replaces the fallback with the page content.

Suspense does not mean “show this fallback whenever some Promise exists in the component.” React supports Suspense for specific integrations, including `lazy`, and for data-loading patterns provided by compatible frameworks and libraries. A normal `fetch()` inside `useEffect` does not automatically activate Suspense.

### How Suspense Boundaries Work

A Suspense boundary applies to the components rendered beneath it. If one of those descendants suspends, React can display that boundary’s fallback while the suspended content is unavailable. The nearest applicable boundary controls the fallback, so nested boundaries can provide more targeted loading states.

```jsx
<Suspensefallback={<PageLoading />}>
 <Dashboard>
 <Suspensefallback={<ChartLoading />}>
 <RevenueChart />
 </Suspense>
 </Dashboard>
</Suspense>
```

If `RevenueChart` suspends, the inner boundary can show `ChartLoading` while the rest of the dashboard remains visible. If a larger part of the tree suspends without a closer boundary, the outer fallback may appear.

Boundary placement affects the user experience. A single boundary around the entire application is easy to add, but may replace too much UI with one generic loader. Smaller boundaries can preserve already available content and show loading feedback closer to the part that is waiting.

For transitions between already visible content, React’s transition APIs can affect whether existing UI remains on screen while new content is prepared. The exact behavior depends on the update, boundary placement, and React integration; test the real navigation and loading flow rather than assuming every suspended update behaves identically.

### Suspense, Errors, and Server Rendering

Suspense handles a **pending** state, not a failed one. If a lazy import or supported data operation fails, an Error Boundary or framework-level error boundary should render an error state.

Suspense is also used in modern server-rendering architectures. Frameworks can stream server-rendered HTML in segments and coordinate hydration so parts of a page can become interactive progressively. The framework manages much of this integration; applications should use the framework’s documented conventions rather than building a custom Suspense data-fetching mechanism.

### Real-World Examples

**Reports page:** The application can load a report-building feature only when the user visits `/reports`. A route-level Suspense boundary displays a page skeleton while the code chunk loads.

**Dashboard chart:** A chart library may be large. The surrounding dashboard can render first while a nested boundary shows a chart-specific placeholder.

**Product details:** A supported data-loading integration can suspend the details panel while its data is prepared, leaving the product navigation visible. The user gets feedback localized to the part that is waiting.

**Modal editor:** If an editor is a large, optional feature, load it when the user opens the editor. A Suspense fallback can appear inside the modal rather than blanking the entire application.

Loading feedback should be designed accessibly and match the region being loaded. For example, use `role="status"` for a concise status message and consider `aria-busy` on a region while its contents are updating.

### Advantages and Disadvantages

**Advantages:**

- Centralizes fallback UI at a meaningful component boundary.
- Works naturally with lazy-loaded components.
- Supports nested loading states so unaffected parts of a page can remain visible.
- Integrates with supported data and server-rendering architectures.

**Disadvantages and risks:**

- A broad boundary can replace too much of the screen with a fallback.
- Suspense does not automatically handle arbitrary asynchronous code or `useEffect` requests.
- Loading boundaries, error boundaries, and transitions solve related but different problems.
- A fallback can flicker if a boundary is placed poorly or content repeatedly suspends.
- Data Suspense behavior depends on the framework or library and its caching and error conventions.

### Current Practice and Alternatives

For code splitting, use route-level or feature-level lazy loading where it meaningfully reduces the initial bundle. Frameworks may already split route code automatically. For data loading, prefer the framework’s route loaders or a Suspense-compatible data library when the application uses that model. A conventional `loading` state in component state is still appropriate for requests managed directly by the component.

### Interview Summary

> Lazy loading defers loading a component’s code until it is needed, commonly using `React.lazy` with a dynamic import. Suspense defines the boundary and fallback React can show while supported content, such as a lazy component, is pending. They are related but distinct: lazy loading creates the deferred import, while Suspense controls the loading UI. Suspense does not automatically handle arbitrary fetch calls or failures; use the appropriate data integration and Error Boundary. I place boundaries around meaningful regions and measure whether code splitting improves the actual user experience.

Updated todo list

Read-only Ask mode prevents me from editing `react.md`. The following expanded section is ready to add.

## 31. Error Boundaries

### 33. What Is an Error Boundary?

An **Error Boundary** is a React component that catches certain errors thrown while React is rendering its descendant tree. It can replace the failed part of the UI with fallback content instead of allowing the error to leave that subtree unusable.

For example, a dashboard may contain a chart, a notification panel, and a navigation bar. If the chart encounters a rendering error, a boundary around the chart can show “Chart unavailable” while leaving the rest of the dashboard visible.

Error Boundaries are traditionally implemented with a **class component** using one or both of these lifecycle methods:

-**`static getDerivedStateFromError(error)`** updates state so the boundary can render fallback UI.
-**`componentDidCatch(error, info)`** receives the error and component stack information, commonly for logging or reporting.

```jsx
import { Component } from "react";

class ErrorBoundary extends Component {
 state = { hasError: false };

 static getDerivedStateFromError(error) {
 return { hasError: true };
 }

 componentDidCatch(error, info) {
 reportError(error, info.componentStack);
 }

 render() {
 if (this.state.hasError) {
 return <p role="alert">This section is temporarily unavailable.</p>;
 }

 return this.props.children;
 }
}
```

Wrap the part of the tree that should have its own recovery UI:

```jsx
function Dashboard() {
 return (
 <main>
 <h1>Operations dashboard</h1>

 <ErrorBoundary>
 <BatchChart />
 </ErrorBoundary>

 <RecentNotifications />
 </main>
 );
}
```

If `BatchChart` or one of its descendants throws during rendering, React can use the boundary’s fallback for that subtree. `RecentNotifications` is outside that boundary and can remain available.

### What Errors Do Error Boundaries Catch?

Error Boundaries catch errors associated with rendering and React’s processing of a descendant tree, including errors thrown while rendering descendants and in relevant lifecycle processing. They are intended to contain failures that would otherwise break the affected portion of the React UI.

There are important exceptions. Error Boundaries do **not** catch errors from:

- Event handlers, such as a button’s `onClick`.
- Asynchronous callbacks, such as a `setTimeout` callback or a rejected Promise handled later.
- Server-side rendering.
- The Error Boundary itself. An error in a boundary must be handled by an ancestor boundary, if one exists.

A request failure or event-handler failure should be handled where that operation is performed. For example, a form submission should use `try-catch` around its asynchronous request and update loading or error state. An Error Boundary is not a replacement for ordinary application-level error handling.

### Why a `try-catch` Cannot Replace an Error Boundary

This does not catch an error thrown later while React renders `Child`:

```jsx
function Parent() {
 try {
 return <Child />;
 } catch (error) {
 return <p>Something went wrong.</p>;
 }
}
```

`<Child />` creates a React element description; it does not directly call `Child`. React renders the child as part of its own rendering process, outside the JavaScript `try` block in `Parent`.

Use `try-catch` for code executed directly within that block, such as parsing data or making a request:

```jsx
async function handleSubmit() {
 try {
 await saveForm();
 } catch (error) {
 setErrorMessage("The form could not be saved.");
 }
}
```

Use an Error Boundary to contain rendering failures in a descendant subtree. These approaches solve different problems and often belong in the same application.

### How Error Boundaries Work Internally

When React encounters a rendering error in a descendant, it searches upward through the component tree for the nearest Error Boundary. If it finds one, React captures the error for that boundary and renders it again in its error state. `getDerivedStateFromError` supplies the state update that selects the fallback UI. React then commits the fallback in place of the failed subtree.

`componentDidCatch` runs after the error has been caught and is commonly used to report diagnostic details. Avoid putting logging side effects in `getDerivedStateFromError`; that method is intended to calculate the fallback state.

If no boundary catches the error, the error can propagate upward and React may unmount the affected root rather than leave the application in a potentially corrupted UI state. Boundaries should therefore be placed at useful recovery points, not assumed to be supplied automatically by every React application.

### Fallback UI, Logging, and Recovery

A fallback should tell the user what happened and offer a useful next step where possible. Logging should capture enough context to diagnose the problem, while avoiding sensitive user data. A production system may report the error to an observability service, but the fallback should not expose stack traces or internal details.

A boundary can also reset its error state and try rendering its children again. The reset should be connected to a meaningful user action, such as retrying a failed feature or navigating to another record. Simply resetting immediately without changing the condition that caused the error may cause the same failure again.

```jsx
if (this.state.hasError) {
 return (
 <section role="alert">
 <p>Reports could not be displayed.</p>
 <button onClick={() => this.setState({ hasError: false })}>
 Try again
 </button>
 </section>
 );
}
```

In real applications, retries may need to reset related data, refresh a route, or use a library that provides boundary reset behavior. A reset button by itself cannot repair a persistent code defect.

## Real-World Examples

**Dashboard widget:** Put an Error Boundary around an optional chart or third-party visualization. If it fails, the rest of the dashboard remains usable.

**Route-level error UI:** A router or framework may provide route error boundaries. These can show a route-specific error page when rendering or route data handling fails, while preserving the surrounding application shell.

**Rich text editor:** An editor may be complex and supplied by an external package. Containing its rendering errors prevents a defect in that area from blanking an entire workflow.

**Whole application fallback:** A top-level boundary can provide a last-resort message if a more localized boundary does not catch an error. Keep this fallback simple and ensure reporting still works if the failing feature is large.

**API request failure:** Use request-specific error state, a rejected-action state, or a data-fetching library’s error UI. A network rejection occurring in an asynchronous callback is not caught by an Error Boundary automatically.

**Lazy-loaded component failure:** `Suspense` handles a component that is still loading; it is not the error fallback for a failed import. If a lazy import rejects, an Error Boundary or framework error handler is needed to display recovery UI.

### Background and Current Practice

Before Error Boundaries were introduced, a rendering error could leave a React application in an inconsistent state or cause a much larger portion of the UI to disappear. Error Boundaries provided a structured way to isolate rendering failures and define fallback behavior.

In current React applications, use boundaries around meaningful areas: often at a route, page, or independently useful widget. A single boundary around the entire app may be a useful final safeguard, but more localized boundaries can preserve unaffected features. Frameworks and libraries may provide boundary abstractions, but the underlying distinction remains: rendering errors need boundary-based recovery, while event, request, and validation errors need handling in their own flows.

### Advantages and Disadvantages

**Advantages:**

- Contains rendering failures to a chosen portion of the component tree.
- Allows unaffected parts of the application to remain available.
- Provides a place for consistent fallback UI and error reporting.
- Helps prevent users from being left with an empty or broken screen.
- Supports clearer recovery boundaries for routes and independent features.

**Disadvantages and risks:**

- Boundaries do not catch every kind of error; event handlers and asynchronous code need their own error handling.
- A boundary can hide a defect if it only displays a generic fallback and does not report useful diagnostics.
- Poorly placed boundaries can replace too much UI or create inconsistent recovery behavior.
- A boundary cannot catch errors thrown by itself; a parent boundary is needed for that case.
- Resetting a boundary does not guarantee recovery if the underlying error condition remains.

### Interview Summary

> An Error Boundary catches rendering-related errors in its descendant tree and renders fallback UI, usually through the class lifecycle methods `getDerivedStateFromError` and `componentDidCatch`. It is different from `try-catch`: React renders descendants outside the parent’s ordinary JavaScript call stack, while event handlers and asynchronous operations need local error handling. I place boundaries around useful recovery areas, report errors for diagnosis, and keep server authorization and request-error handling separate.

Updated todo list

Read-only Ask mode prevents me from editing `react.md`. The following expanded section is ready to add.

## 32. Component Lifecycle

### 34. Explain the React Component Lifecycle

A component’s **lifecycle** describes how it enters the rendered tree, responds to updates, and leaves the tree. The classic explanation applies to **class components** and groups their behavior into three phases:

```text
Mounting → Updating → Unmounting
```

Modern React code generally uses function components and Hooks. They do not have class lifecycle methods; instead, Hooks such as `useEffect` express synchronization with external systems and cleanup. It is usually more useful to think about *what needs to stay synchronized* than to translate each old lifecycle method one-for-one.

### Class Component Lifecycle

#### Mounting

**Mounting** occurs when React adds a component to the rendered tree for the first time. A simplified sequence is:

```text
constructor
→ render
→ React commits DOM changes
→ componentDidMount
```

`render()` describes the UI and should remain pure. `componentDidMount()` runs after React has committed the component to the DOM. Historically, it has been used to set up subscriptions, timers, or integrations with browser APIs.

```jsx
class BatchStatus extends React.Component {
 componentDidMount() {
 this.connection = subscribeToBatch(this.props.batchId);
 }

 render() {
 return <p>Monitoring batch {this.props.batchId}</p>;
 }
}
```

If a constructor or `static getDerivedStateFromProps` is used, those participate before rendering, but derived state from props is uncommon in well-structured modern code and can make state ownership harder to understand.

#### Updating

An **update** may happen when a component receives new props, its state changes, or its consumed context changes. A simplified class-component sequence is:

```text
getDerivedStateFromProps, if defined
→ shouldComponentUpdate, if defined
→ render
→ getSnapshotBeforeUpdate, if defined
→ React commits DOM changes
→ componentDidUpdate
`shouldComponentUpdate` can skip rendering as an optimization, but an incorrect answer can prevent the UI from reflecting new data. `getSnapshotBeforeUpdate` can capture information from the DOM immediately before React applies changes, such as a scroll position. `componentDidUpdate` runs after the update is committed and can synchronize with an external system. If it calls `setState`, it needs a condition to avoid an update loop.

#### Unmounting

**Unmounting** occurs when React removes a component from the rendered tree. `componentWillUnmount()` is where a class component cleans up resources it created, such as timers, subscriptions, or event listeners.

```jsx
componentWillUnmount() {
 this.connection?.unsubscribe();
}
```

Cleanup matters because a resource that continues after the component is removed can cause unnecessary work or keep objects alive longer than intended.

Older lifecycle methods such as `componentWillMount`, `componentWillReceiveProps`, and `componentWillUpdate` are legacy APIs and should generally not be used in new code.

### Lifecycle Thinking with Function Components

Function components do not have `componentDidMount`, `componentDidUpdate`, or `componentWillUnmount`. An effect instead describes synchronization with an external system, based on the reactive values it uses:

```jsx
import { useEffect } from "react";

function BatchStatus({ batchId }) {
 useEffect(() => {
 const connection = subscribeToBatch(batchId);

 return () => {
 connection.unsubscribe();
 };
 }, [batchId]);

 return <p>Monitoring batch {batchId}</p>;
}
```

When `batchId` changes, React cleans up the previous subscription and creates one for the new ID. When the component is removed, React runs the cleanup.

`useEffect` is **not a direct replacement for every lifecycle method**. It is for synchronizing with external systems, not a general “after render” callback. Values that can be calculated from current props or state should usually be derived during rendering. User-triggered work, such as submitting a form after a click, generally belongs in the event handler. Use `useLayoutEffect` only when DOM measurement or adjustment must happen before the browser paints; it can block painting.

In development, Strict Mode may run effect setup and cleanup an extra time to reveal missing cleanup or fragile assumptions. Write effects so their setup and cleanup can safely repeat.

## 33. Rendering

### 35. What Causes a React Component to Render?

**Rendering** is React calling a component to calculate the next description of its UI. A render is not the same as changing the DOM. After rendering, React reconciles the new output with previous work and commits DOM changes only when needed.

Common reasons React schedules rendering work include:

-**Initial render:** The component is first added to the tree.
-**State update:** A state setter or reducer dispatch requests an update.
-**Parent render:** A parent renders again, and React may render its children as part of reconciling the tree.
-**Context update:** A component consuming a context may render when its provider value changes.
-**External store update:** A subscription through an API such as `useSyncExternalStore`, or through a library integration, can schedule an update when the store changes.

A parent rendering does not guarantee that every child’s function must run in every situation. React can skip some work through mechanisms such as `memo`, but memoization is an optimization, not a correctness guarantee. A memoized component still needs to render when its own state or consumed context changes.

### Render, Reconciliation, and Commit

A simplified update looks like this:

```text
An update is scheduled
 ↓
React renders components to calculate new UI descriptions
 ↓
React reconciles new output with previous work
 ↓
React commits required changes to the host environment
 ↓
The browser paints
```

During the **render phase**, React calls components to determine what the UI should look like. This work should be pure: do not modify external systems or directly change the DOM in a component body. React may render more than once, pause work, or abandon an uncommitted render.

During **reconciliation**, React uses element type, tree position, and keys to determine which parts of the previous UI can be reused. During the **commit phase**, React applies the selected changes to the DOM. A component can render without causing any DOM mutation if its output is equivalent to what is already displayed.

This distinction is important for debugging. A log in a component body indicates that React rendered that component; it does not prove that React replaced its DOM nodes or that the browser painted a visibly different screen.

### Does a State Update Immediately Update the DOM?

**No.** Calling a state setter schedules an update. It does not synchronously replace the state variable inside the currently running function or immediately update the DOM. Each function-component render sees a snapshot of its props and state.

React can **batch** multiple updates and process them together, reducing unnecessary rendering work. In modern React, automatic batching applies broadly when using the modern root API. When the next state depends on the previous state, use an updater function:

```jsx
setCount(current => current + 1);
```

Do not depend on reading the updated state immediately after calling its setter:

```jsx
setCount(count + 1);
console.log(count); // Value from the current render
```

In the uncommon case where an integration must observe a DOM update synchronously, React provides `flushSync`. It can hurt performance and should be reserved for cases where the normal scheduled update is insufficient.

### Real-World Examples

**Search screen:** Typing updates the query state, so React renders the input and results UI. A request may then update loading or result state. Rendering the new loading indicator does not itself mean the server request succeeded.

**Dashboard with independent widgets:** Each widget can own its own loading, error, and display state. A failure in one area need not force the others to change. Keep state close to the UI that uses it unless it genuinely needs shared ownership.

**Long list:** A state change in a parent can cause list rows to be considered during reconciliation. Stable keys preserve item identity; profiling can show whether memoization or list virtualization is warranted.

### Advantages and Trade-Offs

**Advantages:**

- The lifecycle model helps identify when to initialize, synchronize, and clean up external resources.
- Separating render from commit keeps component logic declarative and supports React’s scheduling.
- Batching can combine related updates and reduce unnecessary work.
- Local state and component boundaries can limit how broadly updates affect the UI.

**Trade-offs and common mistakes:**

- Assuming every render changes the DOM can lead to misleading debugging conclusions.
- Side effects during render can run more than once or occur for work React later abandons.
- Effects with incorrect dependencies can use stale values or repeatedly tear down and recreate resources.
- Parent-owned state can cause broad rendering work; moving state closer to its consumers can help, while memoization should be guided by measurement.
- Treating state setters as synchronous assignments can cause stale-value bugs.

### Interview Summary

> Class components are traditionally described through mounting, updating, and unmounting, with lifecycle methods such as `componentDidMount`, `componentDidUpdate`, and `componentWillUnmount`. Function components use Hooks, especially effects, to synchronize with external systems and clean them up. React can render because of initial mounting, state or prop changes, context updates, or external-store updates. Rendering calculates UI; reconciliation determines what can be reused; commit applies necessary host changes. State setters schedule work that React may batch, so a render does not necessarily mean the DOM changed immediately.
Updated todo list

Read-only Ask mode prevents me from editing `react.md`. The following expanded section is ready to add.

## 35. Strict Mode

### 37. What Is React Strict Mode?

**Strict Mode** is a development-only React tool that helps reveal bugs and deprecated patterns. It does not add visible UI or change the application’s production behavior. You enable it by wrapping all or part of the component tree:

```jsx
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";

createRoot(document.getElementById("root")).render(
 <StrictMode>
 <App />
 </StrictMode>
);
```

Strict Mode deliberately performs extra development checks. Some component logic may run more than once, and React may perform an additional setup-and-cleanup cycle for effects and callback refs. The goal is to expose code that accidentally depends on “this runs exactly once” or that fails to clean up correctly.

These checks do **not** mean every function in the application is run twice. For example, React does not intentionally call a user’s click handler twice simply because Strict Mode is enabled.

### What Does Strict Mode Check?

Depending on the React version and where Strict Mode is placed, its development checks include:

-**Repeated rendering of components:** Helps find render logic that mutates data or causes side effects. Components should be pure: rendering should calculate UI, not send requests or modify external state.
-**Extra effect setup and cleanup:** Helps reveal missing cleanup for subscriptions, event listeners, timers, and similar resources.
-**Extra callback-ref setup and cleanup:** Helps find ref callbacks that attach resources but do not release them.
-**Warnings for deprecated APIs:** Helps identify older APIs that may cause problems or should be replaced.

For example, this component has an effect that subscribes but never unsubscribes:

```jsx
function BatchStatus({ batchId }) {
 useEffect(() => {
 subscribeToBatch(batchId);
 }, [batchId]);

 return <p>Monitoring batch {batchId}</p>;
}
```

With Strict Mode’s development checks, setup may run again without the prior subscription being cleaned up. The fix is to return cleanup:

```jsx
function BatchStatus({ batchId }) {
 useEffect(() => {
 const subscription = subscribeToBatch(batchId);

 return () => {
 subscription.unsubscribe();
 };
 }, [batchId]);

 return <p>Monitoring batch {batchId}</p>;
}
```

Now the effect describes a complete synchronization cycle: connect to the current batch, disconnect when that setup is no longer active, and safely connect again if needed.

### Why Might an Effect Run More Than Once?

In development, Strict Mode tests whether an effect can be stopped and restarted safely. Conceptually, React may perform:

```text
setup
→ cleanup
→ setup again
```

This simulates remount-like behavior and exposes effects that assume setup happens once. A subscription, for instance, should not leave two active listeners after this sequence.

This can surprise developers when an effect performs a request:

```jsx
useEffect(() => {
 fetch("/api/claims").then(/* ... */);
}, []);
```

Seeing multiple development requests does not mean Strict Mode is broken. It may reveal that the effect has no cancellation, deduplication, or other handling for repeated setup. For requests, use an appropriate cleanup strategy, such as `AbortController`, or prefer framework data-loading features or a data-fetching library that manages caching and request lifecycles. Aborting a request can stop the client from waiting for it, but does not guarantee the server never received or processed it.

A useful design question is whether the work belongs in an effect at all. Work caused by a specific user action, such as submitting a payment, usually belongs in that event handler. Effects are for keeping an external system synchronized with the component’s current inputs, not for encoding “run this user action once.”

### Repeated Rendering and Pure Components

React may call a component function more than once while checking or preparing UI. This is safe when the function is pure:

```jsx
function Greeting({ name }) {
 return <h1>Hello, {name}</h1>;
}
```

It is unsafe to mutate shared data during rendering:

```jsx
function UserList({ users }) {
 users.sort((a, b) => a.name.localeCompare(b.name)); // Mutates the prop
 return users.map(user => <p key={user.id}>{user.name}</p>);
}
```

The mutation can affect other code and make the output depend on how often React renders. Instead, create a new array:

```jsx
function UserList({ users }) {
 const sortedUsers = [...users].sort((a, b) =>
 a.name.localeCompare(b.name)
 );

 return sortedUsers.map(user => (
 <p key={user.id}>{user.name}</p>
 ));
}
```

Strict Mode also helps expose impure state initializers, updater functions, and memo calculations. Those functions should calculate and return a result without changing external data.

### Does Strict Mode Double-Render in Production?

**No.** Strict Mode’s extra development checks do not run in production builds. They are intended to make problems easier to find during development, not to add production rendering work.

Strict Mode can be applied to the whole app or a subtree:

```jsx
<StrictMode>
 <App />
</StrictMode>
```

A detail for advanced use: when Strict Mode is applied only below the root rather than at the root, React avoids certain initial extra effect checks that could produce a development-only sequence impossible in the corresponding production tree. Follow the project’s root setup and React version when interpreting the exact development behavior.

### Background and Current Practice

Strict Mode was introduced to help teams find unsafe lifecycle methods and other behaviors that become problematic as React evolves. Its checks have expanded over time to cover effects, refs, and rendering assumptions.

In current React projects, it is common to enable Strict Mode near the application root during development. The best response to a Strict Mode warning or repeated setup is to investigate whether the component is pure and its resources are correctly cleaned up. Disabling Strict Mode can hide the symptom without fixing the underlying defect.

### Real-World Examples

**WebSocket subscription:** If an effect connects twice but closes only once, development checks reveal a leaked connection. Return cleanup that removes listeners and closes or disconnects the resource.

**Event listeners:** If a component adds a window listener on every setup but never removes it, one key press can trigger duplicate handlers. Pair each registration with `removeEventListener` using the same function reference.

**Ref callbacks:** A callback ref that initializes a third-party widget should also dispose of it when the node is detached. Strict Mode’s extra callback-ref checks can expose missing teardown.

**Data fetching:** Repeated setup may expose duplicate or stale requests. Add cancellation or use a data layer with caching and request coordination; do not assume an empty dependency array is a universal exactly-once guarantee.

### Advantages and Trade-Offs

**Advantages:**

- Finds render-time mutations and other impure logic during development.
- Exposes effects and ref callbacks that do not clean up correctly.
- Surfaces deprecated API usage earlier.
- Encourages components and effects that tolerate setup, cleanup, and retry behavior.

**Trade-offs and common misunderstandings:**

- Development logs or requests may appear more than once, which can be confusing.
- Extra checks can add development work, especially in large trees.
- It can reveal existing defects that take time to fix.
- It does not catch every bug, replace tests, or validate production performance.
- Removing Strict Mode may silence a symptom while leaving unsafe code in place.

### Interview Summary

> Strict Mode is a development-only tool that helps find impure rendering, missing effect or ref cleanup, and deprecated API usage. It may deliberately repeat certain rendering and setup logic in development, but it does not double-render in production or intentionally invoke event handlers twice. I treat repeated setup as a signal to make rendering pure and effects safe to clean up and restart, rather than relying on them running exactly once.

Compacted conversation

Updated todo list

## 36. React 18 / Concurrent Rendering Concepts

### 38. What Is Concurrent Rendering?

**Concurrent rendering** is React’s ability to prepare a UI update in interruptible, schedulable pieces. When React is rendering a large update, it can pause that rendering work, handle a more urgent update, and later continue, restart, or discard the unfinished work.

The practical goal is to keep important interactions responsive while React works on less urgent updates. For example, when a user types into a search field, the text they typed should appear immediately. Updating a large table of matching results may take longer and can be treated as lower priority.

```text
Urgent update: show the character the user just typed
 ↓
React handles it promptly

Non-urgent update: render a large result list
 ↓
React may pause, resume, or replace this work
```

Concurrent rendering does **not** mean React renders on multiple JavaScript threads or changes the DOM in parallel. JavaScript normally runs on the browser’s main thread. React can yield control between pieces of its own render work so the browser can process other events, such as typing, scrolling, or painting a frame.

There is an important boundary: React cannot interrupt an arbitrary long-running JavaScript function in the middle of its execution. If a component synchronously performs a very expensive calculation, that calculation can still block the main thread. Concurrent scheduling helps React organize its rendering work; it does not make CPU-heavy code automatically non-blocking.

### Render Work vs. Committing the UI

To understand concurrency, distinguish React’s **render phase** from its **commit phase**:

1.**Render phase:** React calls components and calculates a possible next UI. This work can be paused, repeated, or abandoned before it becomes visible.
2.**Commit phase:** React applies the chosen result to the host environment, usually the browser DOM. The commit is not an interruptible sequence of partial DOM updates; React commits a consistent result.
3.**Browser paint:** The browser displays the committed result.

This separation is why rendering should be pure. A render attempt may not become the screen the user sees. Do not send requests, mutate shared data, or perform other side effects directly in a component body. Use event handlers or the appropriate effect or data-loading mechanism for that work.

### Historical Background: What Changed in React 18?

Before React’s concurrent capabilities, React generally processed rendering work synchronously once it began. A sufficiently large render could occupy the main thread long enough to make an interaction feel delayed. Developers often tried to address this by reducing unnecessary renders, splitting code, debouncing inputs, or virtualizing long lists.

React’s **Fiber architecture**, introduced in React 16, provided the internal foundation for organizing rendering as units of work. React 18 made concurrent rendering capabilities available through its modern root API and features such as transitions. The architecture gives React more control over when render work is performed, but it does not mean every update is automatically interruptible or lower priority.

For a React 18 application to use the modern root, the entry point commonly uses `createRoot`:

```jsx
import { createRoot } from "react-dom/client";

createRoot(document.getElementById("root")).render(<App />);
```

Applications using the older `ReactDOM.render` API retain legacy behavior and do not get the same React 18 root behavior. Adopting `createRoot` does not automatically make every update a transition. Developers identify updates that may be deferred using the relevant React APIs.

### Urgent and Non-Urgent Updates

“Urgent” and “non-urgent” describe the **user experience the application wants**, not a strict business-logic classification.

**Urgent updates** are usually needed to acknowledge a direct interaction:

- Showing the latest characters in a controlled input.
- Moving focus or responding to a button press.
- Updating a checkbox as soon as it is selected.
- Showing immediate feedback that an action was activated.

**Non-urgent updates** can often wait briefly while React keeps the interface responsive:

- Rendering a large filtered result list.
- Switching to a complex view or route.
- Updating a chart based on a new selection.
- Rendering a large preview as the user changes configuration.

React does not guess which application updates are safe to delay. The developer marks suitable updates as transitions or supplies a deferred value. A transition should not be used for updates that must directly control text inputs or provide immediate feedback.

### `useTransition` and `startTransition`

`useTransition` lets a component mark some state updates as **transitions**, which React can treat as non-urgent. It returns a pending indicator and a function for marking updates:

```jsx
import { useState, useTransition } from "react";

function ClaimSearch({ claims }) {
 const [inputValue, setInputValue] = useState("");
 const [query, setQuery] = useState("");
 const [isPending, startTransition] = useTransition();

 function handleChange(event) {
 const nextValue = event.target.value;

 // Keep typing responsive.
 setInputValue(nextValue);

 // Let the results update be interruptible.
 startTransition(() => {
 setQuery(nextValue);
 });
 }

 const results = claims.filter(claim =>
 claim.reference.toLowerCase().includes(query.toLowerCase())
 );

 return (
 <>
 <input
value={inputValue}
onChange={handleChange}
aria-label="Search claims"
 />
 {isPending && <p role="status">Updating results...</p>}
 <ClaimResultsclaims={results} />
 </>
 );
}
```

The input has its own urgent state, so it reflects each keystroke immediately. The result-driving `query` update is marked as a transition. If the user types again while React is preparing results for an earlier query, React can prioritize the latest input and avoid committing obsolete intermediate UI.

`startTransition` is also available as a standalone API when a component does not need the pending flag. A transition changes the priority of React state updates; it does **not** move calculations to another thread, debounce the input, or cancel network requests automatically.

### `useDeferredValue`

`useDeferredValue` lets a component continue rendering with an older value while React prepares UI for a newer one. This can help when the input is urgent but a downstream component is expensive.

```jsx
import { useDeferredValue, useState } from "react";

function ClaimSearch({ claims }) {
 const [query, setQuery] = useState("");
 const deferredQuery = useDeferredValue(query);

 const results = claims.filter(claim =>
 claim.reference.toLowerCase().includes(deferredQuery.toLowerCase())
 );

 const resultsAreStale = query !== deferredQuery;

 return (
 <>
 <input
value={query}
onChange={event => setQuery(event.target.value)}
aria-label="Search claims"
 />
 <section aria-busy={resultsAreStale}>
 <ClaimResultsclaims={results} />
 </section>
 </>
 );
}
```

The input updates from `query`, while the result list can temporarily use `deferredQuery`. `useDeferredValue` does not specify a fixed delay and is not a replacement for debouncing. It lets React decide when to render the lower-priority value according to its scheduling needs.

Choose between the APIs based on what you have:

- Use **`useTransition`** when you control the state update and want to mark it as non-urgent.
- Use **`useDeferredValue`** when you receive a value and want a part of the UI to lag behind it.
- Use **debouncing** when the requirement is specifically to wait for a period of inactivity, often before sending a request.

### Simplified Internal Working

React’s internal scheduler tracks pending work and its relative priority. Internally, React uses mechanisms commonly discussed as **lanes** to represent categories of updates. The precise scheduling details are implementation internals and can change; application code should rely on the public APIs rather than assuming a particular priority number or timing.

At a high level:

1. A state setter schedules an update.
2. React determines whether the update is urgent or marked as a transition.
3. React begins rendering the relevant component tree.
4. If higher-priority work arrives, React may yield between units of render work.
5. React can continue the earlier work, restart it with newer inputs, or discard it if it is no longer useful.
6. React commits a consistent result when it is ready.

This is cooperative scheduling: React can yield while it controls the work, but it cannot preempt arbitrary synchronous application code. A long loop or expensive calculation inside a component can still block the browser. For expensive computation, consider optimizing the algorithm, using a Web Worker, or reducing how much UI must render.

### How Concurrent Rendering Relates to Suspense

Suspense defines a boundary that can display fallback UI while supported content is pending, such as a lazy-loaded component or data managed by a Suspense-compatible framework or library. Transitions can help React prepare new content without immediately replacing already visible content with a fallback in some supported scenarios.

These features have distinct jobs:

-**Concurrent rendering** gives React scheduling flexibility.
-**Transitions** mark certain updates as non-urgent.
-**Suspense** defines fallback UI for supported pending content.
-**Error Boundaries** handle certain rendering errors; a Suspense fallback is not an error fallback.

A regular request started inside `useEffect` does not automatically suspend. Follow the data-loading approach supported by the application’s framework or library.

### Real-World Examples

**Large search results:** Keep the text field responsive while React updates a costly list. If the list itself is the bottleneck, virtualization can reduce the number of mounted rows; a transition alone does not make thousands of DOM nodes cheap.

**Dashboard filters:** A selected filter can update immediately while React prepares a complex chart and table. Show a pending or stale indicator if the old results remain visible briefly, so users can tell the view is updating.

**Route changes:** A transition can mark a navigation-related state update as non-urgent, helping React prepare the next screen. Route data loading, code splitting, and error handling still need the router or framework’s appropriate mechanisms.

**Live monitoring dashboard:** Incoming data may update summaries and charts. React can schedule rendering work, but the application still needs to control update frequency, avoid excessive computation, and decide how stale data should be presented.

### Advantages and Disadvantages

**Advantages:**

-**More responsive interactions:** Urgent updates can be prioritized over large rendering updates.
-**Interruptible rendering:** React can avoid spending time finishing obsolete render work when newer input arrives.
-**Better pending experiences:** Transitions expose an `isPending` signal for showing progress without blocking the immediate interaction.
-**Flexible loading UI:** Combined with suitable Suspense boundaries, React can prepare content while preserving a useful view in supported cases.
-**Fits declarative React code:** Developers express update intent through APIs instead of manually coordinating every render step.

**Disadvantages and limitations:**

-**Not multithreading:** Rendering still uses the main JavaScript thread; concurrency does not make CPU-heavy code run in parallel.
-**Not every update is non-urgent:** Marking the wrong update can make essential feedback feel delayed.
-**More states to design:** The UI may need to account for pending, stale, loaded, and error states.
-**Does not replace optimization:** Large DOM trees, inefficient calculations, and expensive component rendering may still need profiling, virtualization, or algorithmic improvements.
-**Does not debounce or cancel requests:** Network timing and request cancellation require separate handling.
-**Can reveal impure render logic:** Because React may repeat or abandon render work, side effects during rendering can create bugs.

### Current Industry Practice

Use transitions and deferred values when profiling or user experience indicates that a non-urgent render is interfering with an interaction. Keep direct feedback, especially controlled input state, urgent. Use React DevTools Profiler and browser performance tools to identify whether the bottleneck is React rendering, JavaScript computation, network latency, or browser layout and paint.

For large lists, virtualization may be more effective than scheduling alone. For expensive CPU work, optimize the calculation or consider a Web Worker. For network requests, use the application’s data-fetching or framework conventions for caching, cancellation, and stale-result handling. Concurrent APIs complement these approaches; they do not replace them.

### Interview Summary

> Concurrent rendering lets React schedule render work so it can pause, restart, or abandon unfinished work before committing a consistent UI. It is not parallel JavaScript or partial DOM updates. I keep interaction-critical updates urgent and mark suitable expensive UI updates as transitions with `useTransition`, or defer a value with `useDeferredValue`. I use these APIs to improve responsiveness, while remembering that they do not debounce requests, move CPU work off the main thread, or replace profiling and techniques such as virtualization.
Updated todo list

## 37. `useTransition`

### 39. What Is `useTransition()`?

`useTransition` is a React Hook that lets a component mark certain state updates as **non-urgent**. React can then prioritize more immediate interactions, such as typing or clicking, while it prepares the UI for those lower-priority updates.

```jsx
const [isPending, startTransition] = useTransition();
```

It returns two values:

-**`isPending`**: `true` while React has transition work pending for this Hook. Use it to show subtle progress feedback.
-**`startTransition`**: a function that marks state updates made inside its callback as transitions.

```jsx
startTransition(() => {
 setSelectedView("reports");
});
```

The callback is called immediately. `startTransition` does **not** delay or move the callback to another thread; it tells React how to prioritize the state update. React may interrupt, restart, or delay rendering the resulting UI so it can respond to more urgent work.

A transition is about **render priority**, not business importance. The update may be important to the user, but still be safe to prepare after immediate input feedback.

### Real-World Example: Search and Filtering

A search field should display each keystroke promptly. Rendering a large set of matching results may be less urgent. Keep the input value separate from the value that drives the results:

```jsx
import { memo, useState, useTransition } from "react";

const ClaimResults = memo(function ClaimResults({ claims, query }) {
 const normalizedQuery = query.toLowerCase();

 const results = claims.filter(claim =>
 claim.reference.toLowerCase().includes(normalizedQuery)
 );

 return (
 <ul>
 {results.map(claim => (
 <li key={claim.id}>
 {claim.reference}: {claim.status}
 </li>
 ))}
 </ul>
 );
});

function ClaimSearch({ claims }) {
 const [inputValue, setInputValue] = useState("");
 const [query, setQuery] = useState("");
 const [isPending, startTransition] = useTransition();

 function handleChange(event) {
 const nextValue = event.target.value;

 setInputValue(nextValue);

 startTransition(() => {
 setQuery(nextValue);
 });
 }

 return (
 <section>
 <label>
 Search claims
 <input
value={inputValue}
onChange={handleChange}
 />
 </label>

 {isPending && <p role="status">Updating results...</p>}

 <div aria-busy={isPending}>
 <ClaimResultsclaims={claims} query={query} />
 </div>
 </section>
 );
}
```

The controlled input uses `inputValue`, which updates urgently. The results use `query`, which updates as transition work. While React prepares results for an earlier query, the user can keep typing; React can prioritize the new input and avoid committing outdated intermediate results.

`memo` can help prevent `ClaimResults` from rendering during the urgent input update when its `claims` and `query` props have not changed. It is an optimization, not a requirement. If `claims` is recreated on every parent render, its new reference may prevent that optimization from helping.

### Important Limitation: It Does Not Make Calculations Faster

`useTransition` can change the priority of React’s rendering work, but it does not make JavaScript computations run in parallel. For example, a large synchronous `claims.filter(...)` still runs as one JavaScript operation once React reaches it. React cannot pause halfway through an arbitrary `filter` callback to process another keystroke.

If profiling shows that the filtering computation itself blocks the main thread, consider improving the algorithm, reducing the data set, virtualizing the rendered list, or moving genuinely CPU-heavy work to a Web Worker. Transitions are useful when React’s rendering work is causing poor responsiveness; they are not a substitute for addressing every kind of performance bottleneck.

### What Should and Shouldn’t Be a Transition?

Good candidates include:

- Updating a large filtered or sorted table after the user changes a control.
- Switching to a complex view that takes time to prepare.
- Updating a chart or preview in response to a selection.
- Changing route-related UI when the immediate interaction should remain responsive.

Keep immediate feedback urgent, especially the state controlling a text input. React does not support using a transition update to control text inputs; the input’s own value should update synchronously from the interaction.

A useful rule is: **the interaction’s direct response is urgent; expensive UI that follows can often be a transition.**

### What Does `isPending` Mean?

`isPending` indicates that React is still processing transition work associated with the Hook. It is useful for communicating that results are being updated:

```jsx
{isPending && <p role="status">Updating results...</p>}
```

It does not mean that a server request is pending, that the operation failed, or that a particular amount of time has passed. It can be brief, and the application should not treat it as a network-loading flag. Track request status with the relevant data-fetching or application state mechanism.

### How It Works Internally

At a high level, React processes the update like this:

1. The event handler updates urgent state, such as the input value.
2. `startTransition` marks the state update inside its callback as transition work.
3. React schedules the resulting render at a lower priority than urgent interaction updates.
4. If more urgent work arrives, React can pause or abandon unfinished render work and handle the new update.
5. React commits a consistent result when it is ready.

React’s Fiber architecture allows it to organize rendering into units of work. This is not multithreading: JavaScript still runs on the main thread, and React cannot preempt a long synchronous calculation in the middle. The public contract is that transitions are non-urgent; internal scheduling details and exact timing are not guarantees application code should depend on.

### Background: Before Transitions

Before React 18’s concurrent rendering capabilities, a large synchronous render could keep the main thread busy and make typing or other interactions feel delayed. Developers addressed this with techniques such as debouncing, reducing unnecessary renders, splitting bundles, and virtualizing long lists.
React 18 introduced transition APIs alongside the modern root API, typically enabled by changing an application entry point to `createRoot`:

```jsx
import { createRoot } from "react-dom/client";

createRoot(document.getElementById("root")).render(<App />);
```

Using `createRoot` makes the modern React root available; it does not automatically classify every update as a transition. The application identifies suitable updates with `useTransition` or the standalone `startTransition` API.

### Related Approaches

-**`useDeferredValue`** is useful when a component receives a value and wants part of its UI to lag behind it. `useTransition` is useful when the component controls the state update and wants to mark it as non-urgent.
-**Debouncing** waits for a specified period of inactivity, often to limit API calls. A transition does not impose a fixed delay and does not reduce the number of network requests by itself.
-**Virtualization** renders only the visible portion of a large list. It reduces DOM and rendering work; a transition alone does not make a huge list inexpensive.
-**Web Workers** can move suitable CPU-heavy calculations off the main thread. Transitions do not do this.
-**`useDeferredValue` and transitions with Suspense** can help React prepare supported content while retaining existing UI in some cases. Suspense handles pending content for supported integrations; it is not a general network-request or error-handling mechanism.

### Advantages

-**Keeps direct interactions responsive:** Urgent updates can be handled ahead of less urgent rendering.
-**Allows React to abandon obsolete work:** If newer input arrives, React need not finish rendering results for an outdated value.
-**Provides pending feedback:** `isPending` can communicate that a view is being prepared.
-**Fits React’s rendering model:** The application expresses update priority declaratively, while React decides when to perform the work.
-**Can improve perceived performance:** Users can continue interacting while React prepares a more expensive view.

### Disadvantages and Trade-Offs

-**Does not parallelize JavaScript:** CPU-heavy synchronous work can still block the main thread.
-**Requires separating state appropriately:** The urgent input value and the transition-driven result value may need separate state.
-**Adds UI states to consider:** The interface may briefly show old results while new results are pending.
-**Does not debounce or cancel requests:** Network request frequency and cancellation need separate handling.
-**Can be misapplied:** Marking immediate feedback or controlled input state as non-urgent can make the interface feel sluggish.
-**Does not replace profiling:** If the bottleneck is expensive computation, a huge DOM, network latency, or browser layout, the appropriate solution may be elsewhere.

### Current Practice

Use `useTransition` when a specific user interaction triggers rendering work that should not block immediate feedback. Profile with React DevTools and browser performance tools to identify whether the problem is React rendering, computation, network activity, or DOM size. Pair transitions with techniques such as virtualization or a Web Worker when those address the measured bottleneck.

For asynchronous work, do not assume `isPending` automatically tracks every request. Handle network loading, errors, cancellation, and stale responses through the application’s data-fetching approach. In current React versions, transitions can also participate in supported action workflows, but the exact async patterns depend on the React version and framework; follow the project’s conventions.

### Interview Summary

> `useTransition` returns `isPending` and `startTransition`. I use `startTransition` to mark UI state updates as non-urgent, allowing React to prioritize direct interactions such as typing while it prepares a costly view. I keep controlled input state urgent and use `isPending` for transition feedback. A transition changes React’s scheduling priority; it does not debounce requests, run JavaScript on another thread, or make expensive calculations intrinsically faster.
Updated todo list

## 38. `useDeferredValue`

### 40. What Is `useDeferredValue()`?

`useDeferredValue` is a React Hook that lets part of the UI use a **deferred version** of a changing value. That version can temporarily lag behind the current value while React prepares a less urgent render.

```jsx
const deferredQuery = useDeferredValue(query);
```

This is useful when an interaction should update immediately, but rendering everything that depends on the new value is expensive. For example, a search input should show each keystroke promptly, while a large result list can catch up afterward.

The Hook does not delay the value for a fixed period. It gives React the option to render with the previous value first and prepare the newer value as background work. React may interrupt that background render if more urgent work arrives.

### Real-World Example: Search Results

Keep the input responsive, and pass the deferred query to a separate expensive component. Wrapping that component in `memo` lets it skip the urgent render when its props have not changed.

```jsx
import { memo, useDeferredValue, useState } from "react";

const ClaimResults = memo(function ClaimResults({ claims, query }) {
 const normalizedQuery = query.toLowerCase();

 const results = claims.filter(claim =>
 claim.reference.toLowerCase().includes(normalizedQuery)
 );

 return (
 <ul>
 {results.map(claim => (
 <li key={claim.id}>
 {claim.reference}: {claim.status}
 </li>
 ))}
 </ul>
 );
});

function ClaimSearch({ claims }) {
 const [query, setQuery] = useState("");
 const deferredQuery = useDeferredValue(query);
 const resultsAreStale = query !== deferredQuery;

 return (
 <section>
 <label>
 Search claims
 <input
value={query}
onChange={event => setQuery(event.target.value)}
 />
 </label>

 <div aria-busy={resultsAreStale}>
 {resultsAreStale && <p role="status">Updating results...</p>}
 <ClaimResultsclaims={claims} query={deferredQuery} />
 </div>
 </section>
 );
}
```

When the user types, `query` updates immediately, so the input displays the latest text. The result component can continue showing results for `deferredQuery` while React prepares the results for the newer query. The `resultsAreStale` comparison lets the UI communicate that the displayed results do not yet match the input.

`memo` matters in this example: without it, the parent’s urgent render would normally call `ClaimResults` again with the same deferred query, and the expensive filtering could run immediately anyway. The optimization also depends on `claims` keeping the same reference. If the parent recreates the array on every render, `memo` sees a changed prop and may not skip the work.

### How It Works Internally

A simplified sequence is:

1. The user types, and the component updates `query`.
2. React renders the input using the latest `query`.
3. `useDeferredValue` can return the previous deferred value during that urgent render.
4. React schedules background rendering with the newer deferred value.
5. If another urgent update arrives, React can interrupt or restart that background render.
6. When the background render completes, React commits a consistent UI using the newer value.

React compares the current value with its previous value using `Object.is`. For a primitive such as a string query, this is straightforward. For objects and arrays, identity matters. Passing a newly created object on every render can continually make the value look changed and schedule unnecessary background work. Prefer a stable value from state or props; if an object is genuinely needed, make its identity stable where appropriate.

Deferred rendering does not make the underlying calculation faster. React can yield between units of its own rendering work, but it cannot pause an arbitrary synchronous JavaScript operation halfway through. A single expensive loop inside a component can still block the main thread. If profiling identifies CPU-heavy work, optimize the calculation, reduce the data, virtualize the list, or consider a Web Worker.

### What `useDeferredValue` Does Not Do

- It does **not** impose a fixed delay. React decides when to perform the background render.
- It does **not** debounce input or reduce API requests.
- It does **not** cancel obsolete network requests.
- It does **not** automatically stop a component from rendering. Component boundaries and, where useful, `memo` determine whether expensive child rendering can be skipped during the urgent update.
- It does **not** run JavaScript on another thread.

Use request-specific state and a data-fetching strategy for loading, error, cancellation, caching, and stale responses. If the goal is to wait until typing has stopped for a set amount of time before calling an API, debounce the request separately. The input can remain immediate while the request is debounced.

### `useDeferredValue` vs. `useTransition`

Both APIs help React prioritize updates, but they fit different situations:

| API | Use it when |
|---|---|
| `useDeferredValue(value)` | You receive a value and want an expensive part of the UI to use a version that may lag behind it. |
| `useTransition()` | You control the state update and want to mark that update as non-urgent. |

For example, a search input can update its own state immediately and pass that value through `useDeferredValue`. A filter button that updates several pieces of view state may instead wrap those setters in `startTransition`.

`useDeferredValue` does not provide an `isPending` flag. A common way to indicate stale content is to compare the current value with the deferred value. `useTransition` does provide `isPending` for transition work associated with that Hook.

### Related Performance Approaches

**`useMemo`:** Caches a calculation result when dependencies remain unchanged. It does not change the priority of rendering. `useDeferredValue` lets React defer work that depends on the value; `useMemo` avoids repeating a calculation when its inputs have not changed.

**Debouncing:** Waits for a configured interval of inactivity. This is useful for limiting requests, but adds a deliberate delay. It is not the same as React deciding when to render deferred UI.

**Virtualization:** Renders only the visible rows of a large list. This can reduce DOM and rendering work more directly than deferring a render.

**Web Workers:** Move suitable CPU-intensive calculations off the main thread. `useDeferredValue` schedules React work but does not move computation off-thread.

These techniques can be combined when they solve different measured problems.

### Background: Before React 18

Before concurrent rendering features, a large synchronous render could occupy the browser’s main thread and make typing or other interactions feel delayed. Developers commonly reduced unnecessary rendering, used debouncing, divided code into chunks, or virtualized long lists.

React’s Fiber architecture, introduced earlier, gave React an internal way to organize work into units that can be scheduled. React 18 exposed concurrent rendering capabilities through APIs such as `useDeferredValue` and `useTransition`. Applications typically use `createRoot` to adopt the modern React root. Merely switching to `createRoot` does not defer every update; code uses the Hooks to express which UI work may be lower priority.

### More Real-World Examples

**Live preview editor:** A markdown editor can update its text field immediately while the preview uses a deferred version of the text. If rendering the preview is expensive, it may briefly display the previous version while the user continues typing.

**Dashboard filters:** A selected filter value can update immediately, while a costly chart or table receives a deferred filter. Display a stale or busy indicator so users understand that the visible results are catching up.

**Autocomplete:** Keep the typed query immediate and defer rendering local suggestions. If suggestions come from a server, separately manage request timing and cancellation; deferring the value alone does not manage network behavior.

**Configuration tool:** A user can change several controls while a large visual preview updates at a lower priority. This keeps the controls responsive, though a CPU-heavy preview calculation may still need optimization.

### Advantages

-**Keeps immediate input responsive:** The current value can update while dependent UI catches up.
-**Avoids some obsolete intermediate renders:** React can abandon background work when newer values arrive.
-**Does not require owning the update:** It can be applied where a value is consumed, even if another component owns the state.
-**Works with React’s scheduler:** React can balance background UI work with more urgent interactions.
-**Can communicate stale content:** Comparing current and deferred values enables clear pending feedback.

### Disadvantages and Trade-Offs

-**The UI can temporarily show stale results:** This is intentional, but should be communicated when stale content could confuse users.
-**It may not help without a useful component boundary:** Expensive work in the component that must render urgently can still run on every input update.
-**It adds coordination concerns:** The interface must distinguish the current value from the value used by deferred content.
-**It does not solve CPU bottlenecks by itself:** Long synchronous calculations and very large DOM trees still need appropriate optimization.
-**It does not manage network requests:** Debouncing, cancellation, caching, and error handling remain separate concerns.
-**Fresh object identities can defeat the intent:** Passing a new object every render can continually schedule deferred work.

### Current Practice

Use `useDeferredValue` when a value changes in response to an urgent interaction and a dependent part of the UI is measurably expensive. Keep the input’s displayed value immediate, isolate expensive work in a suitable child component, and profile the result with React DevTools and browser performance tools. If the bottleneck is a large list, virtualization may be the better fix; if it is CPU-heavy computation, optimize it or move suitable work to a Web Worker.

### Interview Summary

> `useDeferredValue` gives a component a version of a changing value that React may update at lower priority. I use it when input or other urgent UI should respond immediately while an expensive dependent view catches up. It does not provide a fixed delay, debounce or cancel requests, or move computation to another thread. I keep the expensive view behind an appropriate component boundary, communicate stale results when needed, and measure whether deferring improves the actual interaction.

Updated todo list

## 40. Large Lists

### 42. How Do You Optimize Rendering 10,000 Records?

The first question is whether the screen needs to show all 10,000 records at once. If a user can only see a few dozen rows in the viewport, creating thousands of DOM elements is usually unnecessary work.

A common solution is **virtualization**, also called **windowing**. The application keeps the full data set available, but renders only the rows currently visible, plus a small buffer before and after them.

```text
10,000 records in application data
 ↓
Determine which records are near the viewport
 ↓
Render perhaps 20–50 rows, plus a small overscan buffer
 ↓
Reuse or replace rows as the user scrolls
```

The exact number of rendered rows depends on the viewport, row height, and overscan configuration. Virtualization does not discard records; it limits how many corresponding UI elements exist in the DOM at one time.

### Why Rendering 10,000 DOM Rows Can Be Expensive

Each row may contain text, buttons, links, checkboxes, icons, and event handlers. Rendering all rows can increase:

-**React work:** React must create and reconcile a large element tree.
-**DOM size:** The browser must manage many elements and their attributes.
-**Layout and paint work:** Scrolling or changing content may make the browser calculate and draw more than the visible region.
-**Memory use:** DOM nodes and associated application data consume memory.
-**Interaction cost:** Focus movement, selection, and updates can become more cumbersome in a very large table.

These costs depend on the content and devices involved. Ten thousand simple rows may behave differently from ten thousand rows containing complex controls. Measure the actual screen before deciding that virtualization is necessary.

### How Virtualization Works Internally

For a list with a fixed row height, a virtualizer can estimate the first visible row from the scroll position. Conceptually:

```text
start index ≈ scroll offset / row height
```

If the scroll position is 2,000 pixels and each row is 40 pixels high, the first visible row is around index 50. The virtualizer then renders the visible range and any configured overscan.

To make a short set of rendered rows behave like a long list, a virtualizer typically:

1. Tracks the scroll container’s position and dimensions.
2. Calculates which item indexes intersect the viewport.
3. Renders those items and an optional buffer of nearby items.
4. Positions the rendered rows appropriately, or uses spacer elements to preserve the overall scroll range.
5. Updates the rendered range as scrolling continues.

Some implementations reuse row components or DOM nodes as items move through the visible range. Others mount and unmount items as needed. The implementation varies by library, but the goal is the same: keep the rendered DOM proportional to what the user can see rather than to the total number of records.

### Fixed-Height and Variable-Height Rows

**Fixed-height rows** are simpler to virtualize because the virtualizer can calculate item positions from a known height. If row heights vary, the virtualizer must measure them or estimate their size and correct positions as measurements become available.

Variable-height lists require extra care. Expanding a row, loading an image, or showing validation errors can change its height and shift content. Choose a virtualizer that supports dynamic measurement when the design requires variable-height rows, and test scrolling after content changes.

### Real-World Example: Operations Table

Imagine an operations dashboard containing 10,000 batch records. Each row displays a batch ID, status, start time, and an action button. Rendering every row at once can make initial display and scrolling sluggish.

A virtualized table can keep all records in application data but render only the rows around the current scroll position. When a user scrolls, the visible range changes. Stable record IDs should be used as keys so React can associate row state with the correct record as the rendered window moves.

If users need to find a specific batch, combine virtualization with search, filtering, sorting, and perhaps server-side pagination. Virtualization makes the visible list cheaper to render; it does not automatically make searching or sorting 10,000 records inexpensive.

### Libraries and Current Practice

Virtualization libraries provide the mechanics for calculating and rendering the visible range. Common choices include **TanStack Virtual**, **react-window**, and **React Virtuoso**. Their APIs, feature sets, and version-specific usage differ, so select one based on the project’s React version, list or table requirements, dynamic row sizing, accessibility needs, and maintenance constraints.

Use a library that fits the interface rather than writing a custom virtualizer by default. A custom implementation must correctly handle measurements, resizing, scroll position, overscan, focus, and edge cases. That work is justified only when the requirements or constraints make existing libraries unsuitable.

### Real-World Approaches Beyond Virtualization

Virtualization is powerful, but it is not the only way to keep large data screens responsive:

-**Pagination:** Show a bounded page of records at a time. This is often appropriate for administrative tables where users need clear page boundaries and direct navigation.
-**Infinite loading:** Fetch or append more records as the user approaches the end of a list. This limits how much data is loaded initially, but if every fetched item remains mounted, the DOM can still grow without bound. Infinite loading and virtualization can be combined.
-**Server-side filtering and sorting:** Send search and sort criteria to the server when the data set is large or the client should not download every record. Handle request latency, cancellation, and stale responses separately.
-**Search or filters:** Let the user narrow the result set before display. Filtering reduces rendered items only if the UI actually renders fewer rows.
-**CSS `content-visibility`:** For some content-heavy documents, this browser feature can skip some offscreen rendering work. It does not remove DOM nodes and is not a full replacement for virtualization in an interactive table.

A good design may use several approaches: server-side search to reduce the result set, pagination for navigation, and virtualization within a large page.

### What Virtualization Does Not Solve

Virtualization reduces mounted UI elements, but it does not automatically make every part of the workflow fast:

- Filtering 10,000 records on every keystroke may still be expensive.
- Sorting the entire collection can still take noticeable CPU time.
- Large network responses still take time to download and parse.
- Complex row components may still be expensive to render.
- Poorly designed row state can appear to move between items if identity is handled incorrectly.

Profile the full interaction. For expensive client-side filtering, consider improving the algorithm, debouncing server requests, using `useDeferredValue` for non-urgent UI updates, or moving suitable computation to a Web Worker. Those techniques address different problems; none replaces virtualization when the issue is a very large rendered DOM.

### Accessibility and Interaction Trade-Offs

Virtualized lists need deliberate accessibility testing. Since offscreen rows are not mounted, they may not be available to assistive technology or browser find-in-page in the same way as a fully rendered list. Keyboard navigation must move focus predictably, including when the next item is not currently mounted. Use semantic table or list markup where appropriate, and ensure row controls remain reachable and announce meaningful labels.

Also consider whether preserving row-local state is important. Virtualizers may unmount rows as they leave the visible range. If a row contains unsaved edits, focus-sensitive controls, or expanded details, store important state outside the row or configure the library to preserve it. Stable keys help React preserve identity among rendered siblings, but they do not keep an unmounted component’s local state alive.

### Advantages

-**Limits DOM size:** Only visible items and a small buffer need to be mounted.
-**Improves scrolling in suitable cases:** The browser has fewer elements to lay out and paint.
-**Supports large data sets:** Users can scroll through many records without rendering all row components at once.
-**Can reduce memory and initial work:** Especially when rows contain complex markup or controls.
-**Combines with data strategies:** It can be used alongside pagination, infinite loading, or server-side filtering.

### Disadvantages and Trade-Offs

-**More implementation complexity:** Scroll behavior, sizing, and item measurement need configuration.
-**Variable row heights are harder:** Dynamic content requires measurement and position correction.
-**Accessibility needs attention:** Unmounted rows can affect screen-reader navigation and browser find-in-page.
-**Row state may be lost:** Virtualized rows can unmount as they leave the viewport.
-**Does not optimize data processing:** Filtering, sorting, network transfer, and parsing may remain bottlenecks.
-**May be unnecessary for small lists:** For a few hundred simple items, virtualization can add complexity without a meaningful user benefit.

### Interview Summary

> For 10,000 records, I first determine whether the user needs every row mounted at once. If not, I profile the screen and consider virtualization, which renders the visible range plus a small overscan buffer while keeping the full data set available. I choose a maintained library that supports the row sizing and accessibility requirements. I also check search, sorting, network loading, stable keys, and row-state behavior, because virtualization reduces DOM work but does not solve those other costs by itself.
Updated todo list

## 42. Throttling

### 44. What Is Throttling?

**Throttling** limits how frequently a function can execute while events continue to occur. Instead of running a handler for every event, it allows execution at a controlled rate, such as at most once every 100 milliseconds.

```text
Scroll event fires repeatedly
 ↓
Throttle limits handler execution
 ↓
Handler runs at a controlled interval
```

This is useful for events that can fire many times during a continuous action: scrolling, pointer movement, window resizing, or dragging a control. A throttled function does not necessarily wait until activity stops. It can run during the activity at regular intervals, which is the key difference from debouncing.

### Why Throttling Can Help

Browsers may emit scroll or pointer events much more frequently than an application needs to update its UI. If every event starts expensive work, the page can consume CPU and feel less responsive.

For example, a scroll handler might update a progress indicator or a “back to top” button. Updating that UI on every scroll event may be unnecessary; updating it a few times per second, or once per animation frame, can be sufficient.

Throttling does not make the work itself faster. It limits how often the work is started. If a single handler is expensive enough to block the main thread, that handler still needs optimization.

### Time-Based Example with React

A utility such as Lodash `throttle` can rate-limit an event handler. In this example, the scroll position state updates at most once per 100 ms while scrolling continues:

```jsx
import { useEffect, useState } from "react";
import throttle from "lodash/throttle";

function ScrollPosition() {
 const [scrollY, setScrollY] = useState(0);

 useEffect(() => {
 const handleScroll = throttle(() => {
 setScrollY(window.scrollY);
 }, 100);

 window.addEventListener("scroll", handleScroll, { passive: true });

 return () => {
 window.removeEventListener("scroll", handleScroll);
 handleScroll.cancel();
 };
 }, []);

 return <output>Scroll position: {scrollY}px</output>;
}
```

The `passive` listener option tells the browser that this listener will not call `preventDefault()`, which can help the browser handle scrolling smoothly. It is appropriate for this example because the handler only reads `scrollY`.

The listener is registered in an effect because it interacts with a browser API outside React’s render calculation. The cleanup removes the listener and cancels a trailing throttled call so it cannot run after the component is gone. Lodash’s throttle supports leading and trailing execution by default; check the chosen library’s documentation if the application requires different behavior.

### Leading and Trailing Execution

Throttle utilities often provide options that control when execution happens:

-**Leading execution:** Run at the beginning of an event burst. This gives immediate feedback.
-**Trailing execution:** Run again after the interval with the latest arguments. This helps ensure the final value is eventually processed.
-**Leading and trailing:** Run immediately and, if more events arrive during the interval, run again when appropriate.

The exact behavior depends on the implementation and its options. Decide based on the interaction. For example, a scroll indicator may benefit from an immediate update followed by periodic updates. A drag operation may also need a final trailing update so the displayed position matches where the user released the pointer.

Timer intervals are not exact deadlines. The callback may run later if the browser’s main thread is busy or timers are throttled in a background tab.

### Throttling vs. Debouncing

| Technique | Behavior during repeated events | Typical use |
|---|---|---|
| **Debouncing** | Waits until events pause, then runs | Search requests, autosave after typing stops |
| **Throttling** | Runs at a controlled rate while events continue | Scroll, resize, pointer movement |

For a search input, the user usually cares about results after pausing, so debounce is often suitable. For a scroll-linked indicator, the user may expect updates during scrolling, so throttle or animation-frame scheduling is usually a better fit.

### `requestAnimationFrame` for Visual Updates

For work that should stay synchronized with painting, `requestAnimationFrame` can coalesce many events into at most one update per animation frame. This is often a better fit for visual effects such as tracking an element while scrolling.

It is not the same as a fixed time-based throttle. The browser controls frame timing, which varies by display refresh rate and whether the page is in the background. Use a time-based throttle when the requirement is “no more often than this interval.” Use `requestAnimationFrame` when the work should align with visible frames.

For visibility checks, an **`IntersectionObserver`** may be preferable to manually processing every scroll event. For element-size changes, **`ResizeObserver`** can be preferable to polling or repeatedly handling window resize events. Browser APIs can eliminate work rather than merely limit its frequency.

### Real-World Examples

**Scroll tracking:** Update a reading-progress indicator or sticky-header state at a controlled rate. For simple visibility detection, consider `IntersectionObserver`.

**Window resizing:** Recalculate a chart layout no more than a few times per second while a user resizes the window. If layout updates should track animation frames, consider `requestAnimationFrame`.

**Pointer movement or dragging:** Process position updates at a controlled rate when each update is relatively expensive. Ensure the final pointer position is applied when the drag ends.

**Live dashboard:** Limit how frequently incoming events trigger a costly chart redraw. However, do not throttle the underlying data ingestion if doing so would lose events; batch or aggregate data when the domain requires preserving it.

**API rate limits:** Client-side throttling can reduce unnecessary requests from one browser session, but it is not a security or reliability boundary. The server must enforce its own rate limits because clients can bypass browser-side controls.

### Internal Behavior and Common Pitfalls in React

A throttle function keeps timing state, typically the time of its last invocation and, optionally, a pending timer for trailing execution. When an event arrives, it checks whether enough time has elapsed. If so, it runs the handler; otherwise, it skips or schedules work according to its configuration.

In React, avoid creating a fresh throttled function on every render. Each instance has its own timer state, so frequent recreation can defeat the rate limit or leave pending timers behind. Create it in an effect or keep its identity stable, and clean it up when no longer needed. Also be careful with closures: a throttled callback can capture values from the render in which it was created. Read current values deliberately, or use a maintained latest-value pattern when needed.

Do not use throttling to update a controlled text input. The displayed input value should respond immediately to typing; throttle downstream work instead.

### Advantages and Disadvantages

**Advantages:**

- Reduces repeated work during high-frequency events.
- Can keep scroll, resize, and pointer interactions more responsive.
- Can limit client-side request frequency when paired with appropriate server behavior.
- Can provide periodic updates while an interaction is still in progress.

**Disadvantages and trade-offs:**

- Updates may be delayed or skipped between executions.
- Leading/trailing behavior must match the interaction or the final value may not be applied when expected.
- Timer management and cleanup add implementation details.
- Throttling does not speed up one expensive execution or reduce DOM size.
- It can hide symptoms rather than fix inefficient work.
- Client-side throttling cannot enforce server-side rate limits.

### Current Practice

Measure the event frequency and the cost of the handler before adding throttling. For visual updates, compare a time-based throttle with `requestAnimationFrame`; for visibility and size changes, consider `IntersectionObserver` and `ResizeObserver`. Use a tested utility when its leading, trailing, cancellation, and timing options are useful. Clean up listeners and pending work, and verify that the final interaction state is reflected in the UI.

### Interview Summary

> Throttling limits how often a function executes during a continuous stream of events. I use it for high-frequency events such as scrolling or resizing when periodic updates are useful. Debouncing waits for activity to stop, while throttling continues at a controlled rate. In React, I keep the throttled handler stable, remove the listener and cancel pending work during cleanup, and choose browser alternatives such as `requestAnimationFrame` or `IntersectionObserver` when they better match the task. Throttling reduces frequency; it does not make expensive work faster or replace server-side rate limiting.

Updated todo list

## 43. API Calls

### 45. How Do You Call an API from React?

A React application calls an API using browser networking tools such as `fetch`, or through a data-fetching library or framework. The response is then represented in application state and rendered as loading, success, empty, or error UI.

The outline’s example shows the basic idea:

```jsx
useEffect(() => {
 async function loadUsers() {
 const response = await fetch("/api/users");
 const data = await response.json();

 setUsers(data);
 }

 loadUsers();
}, []);
```

It starts a request after the component mounts and stores the returned data. But production code also needs to consider failed HTTP responses, JSON parsing errors, unmounting, changing inputs, duplicate or stale requests, and user-visible status.

### When Should the Request Start?

Use an **effect** when the request synchronizes the component with its current inputs, such as loading details for the current `userId` or loading initial data for a route.

Use an **event handler** when the request is caused by a specific user action, such as submitting a form or clicking “Send.” That keeps the operation connected to the action that triggered it. Starting such an operation in an effect can accidentally repeat it when dependencies change or when React performs development checks.

For larger applications, route loaders or data-fetching libraries are often preferable to manually implementing request lifecycles in effects.

### A More Complete Example

This example loads users when `url` changes, cancels the client-side request when the component is removed or the URL changes, and renders loading, error, empty, and success states.

```jsx
import { useEffect, useState } from "react";

function Users({ url = "/api/users" }) {
 const [users, setUsers] = useState([]);
 const [status, setStatus] = useState("loading");
 const [error, setError] = useState(null);

 useEffect(() => {
 const controller = new AbortController();
 let ignoreResult = false;

 async function loadUsers() {
 setStatus("loading");
 setError(null);

 try {
 const response = await fetch(url, {
 signal: controller.signal
 });

 if (!response.ok) {
 throw new Error(`Request failed: ${response.status}`);
 }

 const data = await response.json();

 if (!ignoreResult) {
 setUsers(data);
 setStatus("success");
 }
 } catch (requestError) {
 if (requestError.name === "AbortError" || ignoreResult) {
 return;
 }

 setError(requestError.message);
 setStatus("error");
 }
 }

 loadUsers();

 return () => {
 ignoreResult = true;
 controller.abort();
 };
 }, [url]);

 if (status === "loading") {
 return <p role="status">Loading users...</p>;
 }

 if (status === "error") {
 return <p role="alert">Could not load users: {error}</p>;
 }

 if (users.length === 0) {
 return <p>No users were found.</p>;
 }

 return (
 <ul>
 {users.map(user => (
 <li key={user.id}>{user.name}</li>
 ))}
 </ul>
 );
}
```

The `ignoreResult` guard is useful even when using `AbortController`: it prevents an obsolete result from updating state if a response resolves around the same time cleanup runs. For APIs or operations that cannot be cancelled, a request identifier or equivalent stale-result guard can serve the same purpose.

### What Happens Internally?

An effect does not run as part of calculating the component’s JSX. React first renders and commits the UI; then it runs the effect setup. When `url` changes, React runs cleanup for the previous effect before setting up the new one. On unmount, it runs cleanup for the active effect.

A typical request flow is:

```text
React renders current UI
 ↓
React commits the UI
 ↓
Effect starts request
 ↓
Response arrives or request fails
 ↓
State update is scheduled
 ↓
React renders and commits the resulting UI
```

The network request itself is not managed by React. `fetch` handles the browser request and returns a Promise; application code decides how to interpret the response and represent its status. Calling a state setter does not synchronously replace a variable in the current render. It schedules React to render again with the new state.

The effect callback should not itself be `async`, because an effect may return a cleanup function, not a Promise. Define an async function inside the effect and call it, as in the example.

### Production Concerns

**Loading:** Show useful feedback while the request is pending. For route-level loading, a framework or router may provide a more integrated loading mechanism.

**HTTP errors:** `fetch` generally rejects for network-level failures, but an HTTP status such as `404` or `500` still resolves to a `Response`. Check `response.ok` before treating the response as successful.

**Parsing and data shape:** `response.json()` can reject if the response is not valid JSON. Even valid JSON may not have the structure the UI expects. Validate or normalize external data at an appropriate boundary.

**Empty results:** A successful response with no records is not necessarily an error. Render an intentional empty state rather than leaving the user with a blank screen.

**Cancellation and races:** If a request depends on a changing input, such as a selected user, an older request may finish after a newer one. Cancel obsolete requests where possible and guard against stale results. Aborting a browser request does not guarantee that the server never received or processed it.

**Retries:** Retry transient failures selectively, with a limit and often a delay. Retrying every failure indefinitely can increase server load and make recovery harder. A data-fetching library may provide configurable retry behavior.

**Caching and duplicate requests:** Re-fetching on every mount can waste bandwidth and cause flicker. Caching, request deduplication, invalidation, and refetch policies are common reasons teams use a dedicated server-state library.

**Security:** Do not put secret API keys or privileged credentials in browser code. The browser is under the user’s control. The server must enforce authentication, authorization, validation, and rate limits; hiding data in a React component is not security.

### Alternatives and Current Industry Practice

A small, isolated client-only request can be handled with `fetch` in an effect. As data requirements grow, applications commonly use one of these approaches:

-**Framework or router loaders:** Load route data as part of navigation, with integrated pending and error handling.
-**TanStack Query, SWR, or RTK Query:** Manage server-state concerns such as caching, request deduplication, refetching, retries, and invalidation.
-**Server Components or framework data APIs:** In frameworks that support them, fetch data on the server when that fits the rendering and deployment model.
-**Event-handler requests:** Submit or mutate data directly in response to a user action. Update UI state based on the outcome, often with a form/action abstraction or a mutation API.

These options can coexist. Local UI state such as an open panel is different from remote server state such as a list of users. Pick the data owner and tool that fit the request lifecycle rather than placing every API response in global state.

Historically, class components often started requests in `componentDidMount` and managed changes or cleanup across lifecycle methods. Hooks made it possible to express setup and cleanup in one effect, but they did not remove the need to handle races, caching, errors, and loading UI. In development, Strict Mode may run an extra effect setup-and-cleanup cycle to expose fragile effects. Effects should therefore be safe to start, clean up, and start again.

### Advantages and Disadvantages

**Advantages of effects for API calls:**

- A request can stay synchronized with a component input, such as a record ID.
- Effect cleanup provides a place to cancel or ignore obsolete work.
- React state can drive explicit loading, success, empty, and error UI.

**Disadvantages and risks:**

- Hand-written effects require careful dependency, cleanup, and race handling.
- Effects run on the client, so they do not provide data during server rendering.
- Caching, deduplication, retries, and invalidation can become complex to implement manually.
- Strict Mode development checks can reveal duplicate-request assumptions.
- Effects are the wrong place for operations that should happen only because a user explicitly performed an action.

### Interview Summary

> I call an API from React using `fetch` or a data-fetching layer suited to the application. I use an effect when data should stay synchronized with current inputs, and an event handler for user-triggered operations. I check `response.ok`, represent loading, error, empty, and success states, and handle cancellation or stale responses when inputs change. For production data needs, I generally prefer framework loaders or a server-state library when caching, retries, or request deduplication would otherwise become custom work.
Compacted conversation

Updated todo list

## 44. Race Conditions

### Q46. What is an API race condition in a React application, and how do you prevent stale responses from updating the UI?
An **API race condition** occurs when multiple asynchronous operations are started close together, but their results finish in a different order than they were started. If the application applies every result without checking whether it is still relevant, an older response can overwrite newer state.

Consider a search box. A user types `React`, then quickly changes it to `ReactJS`. The browser starts request A for `React` and request B for `ReactJS`. Request B may finish first and display the correct `ReactJS` results. If request A finishes afterward and also updates the UI, the screen now shows results for `React` even though the input says `ReactJS`.

```text
User input: React -------- ReactJS
Request started: A: React B: ReactJS
Response order: B: ReactJS -------- A: React
UI without guard: ReactJS results React results (stale)
```

The key issue is not that JavaScript executed two lines at exactly the same time. In the browser, JavaScript commonly runs application callbacks on an event loop, but asynchronous work such as network requests can be in flight concurrently. Their completion order is not guaranteed. The race is between **which result becomes available** and **which result is allowed to update the current UI**.

#### Why it happens

React state updates are applied when code calls a state setter, but React does not automatically know whether an API response is still relevant to the user’s latest intent. Each request’s callback has access to the values captured when that request began. When it completes, it can still call `setState` unless the application explicitly prevents it.

This pattern is vulnerable:

```jsx
useEffect(() => {
 fetch(`/api/search?q=${encodeURIComponent(query)}`)
 .then(response => response.json())
 .then(data => setResults(data));
}, [query]);
```

When `query` changes, the effect runs again, but the old request continues. Both callbacks can eventually call `setResults`. The later state update is not necessarily from the latest request; it is simply from whichever callback ran last.

This issue appears in more than search:

- A user switches between customer profiles while each profile’s details load.
- A dashboard filter changes while an earlier report request is still pending.
- A user navigates from one product page to another before the first product request completes.
- An address validation request returns after the user has already changed the address.
- A form submits twice, and the older response incorrectly changes the status of the newer submission.

#### Approach 1: Cancel obsolete requests and guard state updates

For `fetch`, `AbortController` can request cancellation. An effect cleanup runs before React re-runs the effect for changed dependencies and when the component unmounts. This makes it a natural place to cancel work associated with the previous query.

```jsx
import { useEffect, useState } from "react";

function SearchResults({ query }) {
 const [results, setResults] = useState([]);
 const [error, setError] = useState(null);
 const [loading, setLoading] = useState(false);

 useEffect(() => {
 const normalizedQuery = query.trim();

 if (!normalizedQuery) {
 setResults([]);
 setError(null);
 setLoading(false);
 return;
 }

 const controller = new AbortController();
 let isCurrent = true;

 async function loadResults() {
 setLoading(true);
 setError(null);

 try {
 const response = await fetch(
`/api/search?q=${encodeURIComponent(normalizedQuery)}`,
 { signal: controller.signal }
 );

 if (!response.ok) {
 throw new Error(`Search failed: ${response.status}`);
 }

 const data = await response.json();

 if (isCurrent) {
 setResults(data);
 }
 } catch (err) {
 if (isCurrent && !controller.signal.aborted) {
 setError(err instanceof Error ? err.message : "Search failed");
 }
 } finally {
 if (isCurrent) {
 setLoading(false);
 }
 }
 }

 loadResults();

 return () => {
 isCurrent = false;
 controller.abort();
 };
 }, [query]);

 // Render loading, error, and results states here.
}
```

There are two related protections here:

- `controller.abort()` asks the browser to stop the fetch and associated response processing where possible. This can reduce unnecessary client-side work and network usage.
- `isCurrent` prevents an obsolete effect from updating React state, even if the request completes or some asynchronous work cannot be cancelled.

The state guard matters because cancellation is not a guarantee that the server never received or processed the request. The request might already have reached the server, or a non-`fetch` operation may not support cancellation. The UI still needs a correctness rule: only the currently relevant operation may update it.

In TypeScript, the caught value is `unknown` by default in strict configurations, so the `instanceof Error` check provides a safe way to produce an error message.

#### Approach 2: Use a request sequence ID

Another “latest request wins” strategy assigns each request an increasing identifier. A response updates state only if its identifier still matches the latest one.

```jsx
import { useRef, useState } from "react";

function SearchBox() {
 const latestRequestId = useRef(0);
 const [results, setResults] = useState([]);

 async function search(query) {
 const requestId = ++latestRequestId.current;

 const response = await fetch(
`/api/search?q=${encodeURIComponent(query)}`
 );
 const data = await response.json();

 if (requestId === latestRequestId.current) {
 setResults(data);
 }
 }

 // Call search(query) when the user submits or changes the query.
}
```

This approach explicitly expresses the policy: **ignore every result except the result from the newest request**. It can be useful when requests are started from event handlers or when the asynchronous logic does not fit neatly into one effect. It does not cancel the older network request, however. If cancellation is supported, combine a sequence check with cancellation when reducing wasted work is important.

Use a monotonically increasing request ID for ordering; do not compare request completion times or assume that the last-started request will finish last.

#### Approach 3: Use a data-fetching library

In production React applications, teams often use a query library such as TanStack Query or a router’s data-loading APIs instead of building request lifecycle management separately for every component. A query cache can associate results with a **query key**, track loading and error states, deduplicate equivalent requests, and manage stale data and refetching.

Conceptually, a search query should be identified by its input:

```text
["search", "React"] -> result for React
["search", "ReactJS"] -> result for ReactJS
```

The key distinction is that data for `React` is not treated as data for `ReactJS`. A library can also support cancellation if the query function passes along and honors the provided abort signal. Exact APIs vary by library version, so follow that library’s current documentation and application conventions.

Benefits include less hand-written lifecycle code, cache reuse, consistent loading and error behavior, and features such as retries or background refresh. Trade-offs include an additional dependency, library-specific concepts, and the need to configure cache freshness and invalidation correctly. A library does not automatically solve every server-side ordering problem, especially for mutations.

#### Debouncing is related, but it does not solve the race by itself

Debouncing waits for a quiet period before starting a request. For example, it can avoid sending a search request for every keystroke. This reduces request volume, but it does **not** guarantee response order: two debounced requests can still overlap if the user changes the input after the first request starts.

A robust search workflow may combine debouncing to reduce traffic with cancellation or a latest-request guard to prevent stale results. These solve different problems:

```text
Debouncing -> fewer requests are started
Cancellation -> obsolete work is asked to stop
Request identity guard -> obsolete results cannot update the UI
```

#### Important distinction: read races versus write races

For a search or profile lookup, ignoring a stale response usually protects the displayed UI. For a mutation, such as “save profile” or “submit payment,” ignoring a response is not enough to undo a server-side effect. The server may have processed both requests.

For writes, the solution may require server-side rules, such as:

-**Idempotency keys** so retrying one logical operation does not duplicate it.
-**Version numbers or optimistic concurrency checks** so an update based on an old record cannot silently overwrite a newer one.
-**Serialized or ordered processing** when operations must occur in sequence.
-**Domain-specific conflict handling** when two users edit the same entity.

For example, if two profile updates are sent rapidly, the UI can prevent an older response from replacing newer displayed data, but only the backend can reliably enforce which update is accepted as the final record.

#### Common mistakes and edge cases

-**Assuming abort means the server did no work.** Aborting the browser’s fetch does not roll back server processing.
-**Guarding only the success path.** An obsolete request can also overwrite current error or loading state. Apply the “still current” check to all state updates.
-**Updating state after unmount.** Effect cleanup should invalidate or cancel work so a departed screen cannot apply obsolete results.
-**Treating every failure as an error to display.** An intentional abort during cleanup is usually not a user-facing failure.
-**Confusing rendering priority with request ordering.** React features such as transitions can affect how rendering work is prioritized; they do not inherently cancel network requests or decide which response is valid.
-**Assuming a request library makes mutations safe.** Client-side caching and cancellation do not replace backend idempotency or concurrency controls.

In development, React Strict Mode may run effect setup and cleanup more than once to help reveal effects that are not resilient to cleanup. Effects should therefore tolerate cancellation and repeated setup rather than relying on “this runs exactly once” assumptions.

#### Testing the race

A useful test deliberately controls response order:

1. Start a request for `React`.
2. Start a request for `ReactJS`.
3. Resolve the `ReactJS` request first and verify that its results appear.
4. Resolve the `React` request afterward and verify that the displayed results remain those for `ReactJS`.

Also test unmounting, request failure, and cancellation. This proves the behavior under the problematic ordering instead of relying on a test server that happens to respond in the expected order.

**Interview summary:**

> An API race condition happens when overlapping requests complete out of order and an obsolete response updates current UI state. I make the latest-intent policy explicit: cancel obsolete reads with `AbortController` where supported, guard state updates with an effect cleanup flag or request ID, or use a query library with appropriate keys and cancellation. Debouncing reduces request volume but does not guarantee response ordering. For mutations, client-side guards are not enough; the backend may need idempotency or optimistic concurrency controls. I test by resolving requests in reverse order

Updated todo list

I’ll inspect the surrounding section in `collection.md` so the expanded material preserves its numbering and style, then provide the complete 800+ word insert here. I’m in read-only mode, so I can’t edit the file directly.

No matches found. Your search pattern might be excluded completely by either the search.exclude settings or .*ignore files.
If you believe that it should have results, you can check into the .*ignore files and the exclude setting (here are some excluded patterns for reference:[**/node_modules,**/bower_components,**/*.code-search]).
Then if you want to include those files you can call the tool again by setting "includeIgnoredFiles" to true.

That exact wording isn’t present in the current collection file, so I’m checking whether it uses different headings or whether this is intended as new material.

Searched codebase for "Find the section in documentation/collection.md about React interview questions, React Query, authentication, or web security. Return its heading and surrounding content.", 1 result

Read , lines 1 to 180

## 45. React Query / TanStack Query

### Q47. Why use a server-state library?

**Server state** is data owned by a backend and temporarily displayed or manipulated in the browser: notification records, customer profiles, policy details, search results, or payment history. **Client state** belongs to the current interface: whether a dialog is open, which tab is selected, or what text is currently in an unsaved form.

These kinds of state have different problems. A dialog’s open/closed value usually needs no network coordination. A customer record can become outdated, be requested by multiple components, fail to load, or need refreshing after a change. TanStack Query (formerly React Query) provides a lifecycle and cache for this remote data so application code does not have to rebuild those behaviors around every `fetch` call.

#### What it provides

-**Fetching:** Runs a query function and exposes its result to React components.
-**Caching and sharing:** Components using the same query key can observe the same cached result instead of independently managing duplicate copies.
-**Loading and error state:** Components can distinguish initial loading, background fetching, success, and failure.
-**Freshness and refetching:** The application can decide how long data is considered fresh and when stale data should be revalidated.
-**Invalidation:** After changing server data, the app can mark related cached data stale so it will be refreshed.
-**Pagination and infinite loading:** It supports page-oriented queries while keeping prior results available when appropriate.
-**Mutations:** It manages operations that create, update, or delete server data, including pending and error states.

#### How it works internally

A query is identified by a **query key**, usually an array such as `['notifications', userId, filters]`. The key is not just a label: it defines which request result belongs in which cache entry. If a filter changes, the key should change too, or the UI could accidentally display results for the previous filter.

A query function performs the actual request and returns a promise. TanStack Query stores the result in its query cache, tracks its status, and notifies components that are observing that key when relevant state changes. Multiple components can observe the same query. They do not need to pass the fetched result through every intermediate component.

A cached result is not necessarily fresh forever. In TanStack Query v5, `staleTime` controls how long successful data is considered fresh; `gcTime` controls how long an inactive cache entry is retained before garbage collection. These settings solve different problems. A query can be stale but still available on screen while a refetch happens in the background. This supports a responsive experience without treating cached data as permanently authoritative.

Invalidation is a request to refresh the cache’s understanding, not a database transaction. For example, after marking a notification as read, a mutation can invalidate the notification-list query so the list is re-fetched or otherwise updated. For low-latency interfaces, the cache can also be updated optimistically, but the app needs a rollback or reconciliation strategy if the server rejects the change.

```tsx
const notificationsQuery = useQuery({
 queryKey: ['notifications', userId, status],
 queryFn: () => fetchNotifications({ userId, status }),
 enabled: Boolean(userId),
});
```

The query key includes all values that change the result. The `enabled` condition prevents the request from running before `userId` exists.

#### Before server-state libraries

Before libraries such as TanStack Query, teams commonly used `useEffect`, local component state, and `fetch` directly. Each feature then needed to handle request initiation, loading flags, errors, cancellation, duplicate requests, stale results, and cleanup. This can work for a small, isolated request, but repeated implementations tend to drift. For example, one page might refetch after an update while another keeps showing old data.

A shared data-fetching layer or Redux middleware was another approach. Those approaches can still be appropriate, but they often require more application-specific code to model caching, freshness, and invalidation. TanStack Query packages those server-state concerns into a reusable library.

#### Advantages and disadvantages

**Advantages:** Less repeated request-lifecycle code; shared cache; consistent loading and error handling; background refresh; easier invalidation; and useful support for pagination, mutations, and optimistic updates. It can make a multi-screen application easier to reason about because the server remains the source of truth while the client coordinates its local representation.

**Disadvantages:** It introduces library concepts and configuration that a small application may not need. Incorrect query keys can serve incorrect or stale data. Cache invalidation can be subtle when one mutation affects several views. Optimistic updates add rollback complexity. It also does not replace API design, authorization, validation, or backend consistency guarantees.

#### Redux vs React Query

The shorthand distinction is useful but incomplete:

```text
Redux or component state
 -> Often client/application state

TanStack Query
 -> Server-state caching and synchronization
```

Redux is a predictable state container and can hold client state, while TanStack Query specializes in managing asynchronous server state. They can coexist: Redux might hold a workflow draft or UI preferences while TanStack Query loads notification data.

However, **Redux Toolkit Query (RTK Query)** is also designed to manage server data and provides caching, request status, and invalidation. Therefore, the practical decision is often between TanStack Query and RTK Query, not “Redux can never manage server state.” Teams already standardized on Redux may prefer RTK Query to avoid maintaining overlapping tools. Teams without that constraint may prefer TanStack Query’s focused API. Avoid keeping competing authoritative copies of the same server data in Redux and a query cache unless there is a specific synchronization design.

**Production example:** An operations portal displays paginated notification batches. The query key includes the operator, page cursor, and filters. A “retry batch” mutation calls the backend and invalidates the batch details and list queries. The UI shows the retry as pending, then refreshes server-owned status instead of assuming the retry succeeded.

---

## 46. Authentication

### Q48. How would you implement authentication in React?

Authentication establishes **who the user is**. Authorization determines **what that user is allowed to do**. A React application can display a sign-in screen and hide protected routes, but the backend must enforce authentication and authorization on every protected operation. A hidden button or guarded route is not a security boundary.

A common flow is:

```text
User signs in
 -> Identity provider or backend verifies credentials
 -> Server establishes a session or issues tokens
 -> Browser makes authenticated API requests
 -> Server verifies identity and permissions
 -> UI renders the permitted experience
```

In an enterprise application, the identity provider may use OpenID Connect (OIDC) on top of OAuth 2.0. OAuth is primarily an authorization framework; OIDC adds an identity layer. For browser-based public clients, modern guidance generally favors Authorization Code flow with PKCE rather than older implicit-flow patterns. A **Backend-for-Frontend (BFF)** is another common architecture: the browser talks to an application backend, which manages tokens and calls downstream services.

#### Session, cookies, and tokens

A **server-side session** usually stores session state on the server and sends the browser a session identifier. A **JWT** is a signed token containing claims that a recipient can validate. A JWT is generally encoded, not encrypted; its payload should not be treated as secret. Neither JWT nor cookies automatically make a system secure. The choice depends on deployment architecture, cross-site behavior, threat model, and operational needs.

For a browser-based application, an `HttpOnly`, `Secure` cookie with an appropriate `SameSite` policy can reduce the chance that JavaScript steals the session credential through an XSS vulnerability. `HttpOnly` prevents direct access through JavaScript; `Secure` restricts transmission to HTTPS. Cookie-based credentials are sent automatically in applicable requests, which means CSRF must be considered. `SameSite` helps mitigate some cross-site request scenarios but is not a universal substitute for CSRF protections. Depending on the application, teams may also use anti-CSRF tokens, origin checks, or custom request headers.

A bearer token stored in `localStorage` is accessible to JavaScript running on the origin. An XSS vulnerability can therefore expose it. Storage in memory reduces persistence but does not protect against malicious script running in the page. There is no universal “JWT must always go here” answer; select a complete session design, including expiration, renewal, revocation, and XSS/CSRF defenses.

#### Practical concerns

-**Expiration and refresh:** Short-lived credentials limit exposure, but users need a secure renewal flow. Refresh-token rotation and reuse detection can reduce the impact of a stolen refresh token.
-**Logout:** Clear browser-held session material and invalidate the server session or refresh credential where supported. Merely navigating to a login page does not necessarily end the session.
-**Protected routes:** Route guards improve user experience by redirecting unauthenticated users. They do not replace backend checks.
-**Authorization:** The backend should verify permissions for each API operation and resource. A user changing a URL or request body must not gain access to another user’s records.
-**Sensitive data:** Do not put secrets or credentials in frontend environment variables that are bundled into public JavaScript. Treat browser-visible configuration as public.

**Production example:** A notification portal authenticates staff through the organization’s identity provider. The application UI may hide the “retry batch” action from users without permission, but the API independently checks the user’s role and access to that particular batch before accepting the retry.

---

## 47. Security

### Q49. What security issues should React developers know?

React helps escape text rendered as normal JSX, which reduces many common injection risks, but it does not make an application automatically secure. Security depends on browser behavior, dependencies, API design, deployment configuration, and server-side enforcement. Useful reference points include OWASP’s guidance for cross-site scripting, CSRF, authentication, and secure headers.

-**Cross-site scripting (XSS):** Malicious script executes in the application’s origin, potentially reading page data or acting as the user. Avoid injecting untrusted HTML, validate and contextually encode data, sanitize rich text with a maintained sanitizer, and consider a Content Security Policy (CSP) as defense in depth.
-**Cross-site request forgery (CSRF):** A browser may attach cookies to a forged request. This is especially relevant to cookie-based sessions. Use an appropriate combination of `SameSite`, CSRF tokens, origin validation, and request design.
-**Authentication and authorization:** The server must verify credentials and permissions. Client-side checks are for presentation, not access control.
-**Dependency vulnerabilities:** Review direct and transitive dependencies, update deliberately, and use lockfiles and automated vulnerability monitoring. An audit alert needs triage; blindly upgrading can introduce breaking changes.
-**Sensitive data exposure:** Do not log credentials, tokens, personal data, or sensitive API responses unnecessarily. HTTPS protects data in transit but does not prevent exposure through browser storage, logs, analytics, or overly broad API responses.
-**Insecure API handling:** Validate inputs on the server, handle errors without leaking internal details, and ensure CORS is configured narrowly. CORS controls which browser origins may read responses; it is not authentication or authorization.
-**Security headers and deployment:** HTTPS, CSP, clickjacking protections, and carefully configured cookies reduce risk. Their exact settings should match the application’s architecture.

### Follow-up: What is `dangerouslySetInnerHTML`?

`dangerouslySetInnerHTML` asks React to insert an HTML string into an element rather than render the value as ordinary text:

```jsx
<div dangerouslySetInnerHTML={{ __html: html }} />
```

The name is a deliberate warning. If `html` contains attacker-controlled markup or script-capable content, inserting it without proper sanitization can create XSS. React’s normal text rendering escapes HTML-like characters; this API bypasses that protection because the caller is explicitly providing HTML.

Use it only when HTML rendering is required, such as displaying formatted rich text from a trusted content workflow. “It came from our database” does not automatically make it safe: stored content may have originated from users or external systems. Sanitize untrusted HTML with a well-maintained HTML sanitizer configured for the allowed tags and attributes, and sanitize at the appropriate trust boundary. Do not attempt to secure HTML with ad hoc regular expressions. Where rich HTML is unnecessary, render plain text or structured data through ordinary JSX instead.

**Advantages:** It can render existing HTML content and rich-text output without manually rebuilding every element.

**Disadvantages:** It bypasses React’s normal escaping behavior, expands the XSS attack surface, and requires a reliable sanitization policy that remains correct as content and allowed markup evolve.

**Interview summary:** React’s defaults help with text rendering, but security is an end-to-end responsibility. Protect credentials and sessions according to the threat model, enforce authorization on the server, mitigate XSS and CSRF, keep dependencies maintained, and treat raw HTML insertion as a high-risk boundary..

Updated todo list

# 48. Testing React Applications

### Q50. How do you test React applications?

React testing checks that the application behaves correctly at several levels. The levels are often called **unit**, **integration**, and **end-to-end (E2E)** testing. They are not competing choices: a maintainable application usually uses a mix, with most tests running quickly and a smaller number verifying complete user journeys.

A useful principle is to test observable behavior: given a user action and some data, does the application show the right result, communicate errors clearly, and prevent invalid actions? This gives confidence without tying every test to the component’s internal implementation.

#### Unit tests

A unit test checks a small piece of logic in isolation. It might test a date formatter, validation function, reducer, permission rule, or sorting function without rendering a React component.

```ts
expect(formatBatchStatus('FAILED')).toBe('Needs attention');
```

**Advantages:** Unit tests are usually fast, focused, and helpful when diagnosing a failure. They are especially valuable for business rules with many edge cases.

**Disadvantages:** Passing unit tests do not prove that components are connected correctly, that a user can complete a flow, or that the application communicates successfully with its APIs. A formatter can work perfectly while the page never displays its result.

**Real-time example:** A notification portal computes whether a batch can be retried. A unit test can cover completed, failed, already-running, and unauthorized cases without starting a browser.

#### Component and integration tests

A component test renders React into a DOM-like environment and checks what a user can see and do. An integration test typically exercises several connected pieces, such as a form, validation, a mutation, and the resulting confirmation. In practice, component and integration tests can overlap; the important question is how much of the real feature is included.

React Testing Library (RTL) is popular because its APIs encourage querying elements the way a user or assistive technology would, rather than relying on internal component state or private implementation details. Prefer queries such as `getByRole('button', { name: 'Retry batch' })` over selectors tied to a component’s internal structure.

```tsx
test('shows a success message after retrying a failed batch', async () => {
 const user = userEvent.setup();

 render(<BatchActionsbatchId="batch-42"status="FAILED" />);

 await user.click(
 screen.getByRole('button', { name: 'Retry batch' })
 );

 expect(
 await screen.findByRole('status')
 ).toHaveTextContent('Retry requested');
});
```

Here, `getByRole` checks the user-facing button name, `userEvent` simulates an interaction, and `findByRole` waits for asynchronous UI to appear. Use `getBy...` for something expected immediately, `queryBy...` when checking that something is absent, and `findBy...` for content that appears after asynchronous work.

Under the hood, RTL mounts the component using React DOM into a test DOM, often provided by **jsdom**. The test runner executes assertions and reports failures. React’s `act` mechanism ensures relevant updates are processed before assertions; RTL wraps common operations to handle this in normal usage. jsdom is not a full browser, however. It cannot accurately reproduce every browser API, layout calculation, animation, or rendering behavior.

**Advantages:** These tests cover real component behavior and connected UI logic while usually running faster than browser-driven tests. Accessible queries also help reveal missing labels, roles, or names.

**Disadvantages:** DOM-based tests still run in a simulated environment. Over-mocking can make a test pass even when production integration is broken; testing every tiny component in isolation can also create a large, brittle suite.

For network behavior, **Mock Service Worker (MSW)** is a common approach: it intercepts requests at the network boundary and returns controlled responses. This lets the component use its usual API client while a test exercises loading, success, empty, and error states. Mock the external boundary, not every internal function, unless there is a clear reason to isolate that function.

#### End-to-end tests

An E2E test runs the application in a real browser and exercises a complete flow, often against a test backend or controlled test services. Playwright and Cypress are widely used options.

```ts
test('an operator can retry a failed batch', async ({ page }) => {
 await page.goto('/batches/batch-42');

 await page.getByRole('button', { name: 'Retry batch' }).click();

 await expect(page.getByRole('status'))
 .toContainText('Retry requested');
});
```

**Advantages:** E2E tests can verify routing, browser behavior, API integration, and whether multiple application layers work together. They are particularly useful for critical user journeys such as sign-in, submitting a payment, or retrying a batch.

**Disadvantages:** They are slower and can be more expensive to maintain. Tests that depend on shared, changing data or unpredictable networks may be flaky. Keep them focused on high-value flows and make their test data repeatable.

#### How the tools fit together

-**Vitest or Jest** run tests, provide assertions and mocks, and can run component tests in a DOM environment. Vitest is often chosen with Vite projects; Jest remains common in many established codebases.
-**React Testing Library** provides React-focused rendering and DOM queries that favor user-observable behavior.
-**Playwright or Cypress** automate a real browser for E2E and browser-level testing.
-**MSW** can provide realistic, controlled API responses for component and integration tests.

The exact tool choice should fit the project’s build system and existing ecosystem. Avoid maintaining overlapping test frameworks without a reason.

#### What was done before, and what changed?

Earlier React testing approaches often used shallow rendering or tests that inspected component instances, state, and lifecycle methods. Those techniques could be useful for narrow implementation checks, but tests often broke after a harmless refactor because they depended on *how* a component worked rather than *what* the user experienced.

RTL popularized the principle that tests should resemble actual use. This does not mean every test must use a browser or that unit tests are obsolete. It means choosing the boundary that gives useful confidence with reasonable cost. A practical strategy uses focused unit tests for complex business rules, DOM-based integration tests for feature behavior, and a smaller E2E suite for critical workflows.

#### What should be tested?

For a data-driven page, cover meaningful states: loading, successful results, no results, API failure, and permission restrictions. Also test important keyboard interactions and accessible names. Avoid asserting incidental details such as a particular CSS class or the number of internal hook calls unless that detail is itself part of the contract.

Prefer a few representative assertions over snapshots of large component trees. Snapshots can detect changes, but they do not explain whether a change is correct; large snapshots also tend to be updated mechanically without careful review.

**Interview summary:** I use unit tests for isolated business rules, RTL integration tests for user-visible component behavior, and Playwright or Cypress for a few critical browser workflows. I choose boundaries that provide confidence without binding tests to implementation details, and I test important success, loading, empty, error, and authorization states.

---

# 50. State Architecture — Senior Level

### Q52. How would you decide where state should live?

State should live at the **narrowest scope that can reliably serve every part of the application that needs it**. Start by identifying what the state represents, who needs it, how long it should exist, and who owns its authoritative value.

A useful decision guide is:

```text
Only one component needs it?
 -> Keep it local.

Several nearby components need it?
 -> Lift it to their nearest common parent.

Many distant components need shared client state?
 -> Consider Context or a state-management library.

It is owned by a server?
 -> Use a server-state solution such as TanStack Query or RTK Query.

It should be shareable in a URL or survive refresh/navigation?
 -> Consider URL or route state.

It belongs to a form before submission?
 -> Usually keep it in the form, with a form library if complexity warrants one.
```

#### Local state and lifting state up

For a dropdown’s open/closed state, local component state is usually the clearest option:

```tsx
const [isOpen, setIsOpen] = useState(false);
```

Making this global would add coordination and indirection without creating value. If two sibling components must agree on a selected notification, move that selection to their nearest common parent and pass the value and update function down. This is called **lifting state up**.

**Advantages:** Local and lifted state make ownership visible and keep unrelated parts of the app independent.

**Disadvantages:** As a shared value passes through many intermediate components, **prop drilling** can make the component tree harder to maintain. That may indicate a better shared boundary, but prop drilling alone is not automatically a reason to add a global store.

#### Context and shared client state

React Context lets a component provide a value to descendants without explicitly passing it through every intermediate component. Common examples include locale, theme, or a stable authenticated-user summary.

Context is a **value distribution mechanism**, not a complete state-management solution. A provider still needs to decide how updates happen, and consumers still need clear ownership rules. When a context value changes, components that consume that value may re-render. Splitting unrelated values into separate contexts, keeping provider values appropriately stable, or using a store with selectors can help when updates are frequent or the provider serves a large subtree.

**Advantages:** Context uses built-in React APIs and works well for broadly shared values that change infrequently.

**Disadvantages:** A large context containing unrelated, frequently changing values can cause unnecessary renders and obscure which component owns an update. Context also does not automatically provide persistence, time-travel debugging, server-data caching, or a good model for complex workflows.

#### State-management libraries

For complex shared **client/application state**, a library may provide structured update patterns, selectors, middleware, debugging tools, or support for complex transitions. Redux Toolkit is the modern Redux approach for teams that need Redux’s centralized model; Zustand and Jotai are other options with different APIs and trade-offs. A workflow with explicit states and transitions may benefit from a reducer or state-machine approach.

A local reducer is often enough when a component has several related transitions:

```tsx
type State = {
 status: 'idle' | 'saving' | 'saved' | 'error';
};

type Action =
 | { type: 'save' }
 | { type: 'success' }
 | { type: 'failure' };

function reducer(state: State, action: Action): State {
 switch (action.type) {
 case 'save':
 return { status: 'saving' };
 case 'success':
 return { status: 'saved' };
 case 'failure':
 return { status: 'error' };
 }
}
```

This makes allowed transitions explicit and can prevent contradictory flags such as `isSaving === true` and `hasSaved === true` at the same time.

#### Server state is different

A user profile fetched from an API remains owned by the server. Copying it into a general-purpose global client store can require application code to manage caching, loading, failures, refresh, and invalidation. A server-state library such as TanStack Query or RTK Query is designed for that lifecycle. Avoid maintaining separate, conflicting “authoritative” copies of the same server data in a query cache and a Redux slice without a deliberate synchronization strategy.

#### URL and form state

State that should survive refresh, be shareable, or participate in browser navigation may belong in the URL. Search terms, filters, sorting, and pagination are common examples. A user can bookmark a filtered notification list or share it with a teammate.

Unsubmitted form input usually belongs in the form rather than in global state. For complex forms, libraries such as React Hook Form can help with validation, touched/dirty tracking, and performance. After a successful save, the server response becomes server state; the draft before submission is client state.

#### How state architecture evolved

Early React applications commonly used component state and passed values through props. As applications grew, **Flux** introduced one-way data flow, and Redux popularized a centralized store with explicit actions and reducers. Hooks later made local state and reusable stateful logic easier; Context made shared values accessible without prop drilling. Meanwhile, server-state libraries emerged to handle the distinct problems of asynchronous remote data.

These approaches are not a linear replacement ladder. Each addresses different needs. Modern architecture often combines local React state, Context for selected shared values, a focused client-state store where needed, URL state for navigation, and a query library for server data.

#### Should everything go into Redux?

No. Globalizing every value increases coupling and can make simple behavior harder to follow. A modal’s open flag, temporary hover state, or one form’s draft usually belongs locally. Redux can be appropriate for shared client workflows, cross-feature state, or applications that benefit from its conventions and tooling, but it should solve a real coordination problem.

**Real-time example:** In a notification operations portal, the selected filter and page cursor can live in the URL; whether a confirmation dialog is open can stay in the dialog component; the current list of batches can live in TanStack Query; and a multi-step retry workflow may use a reducer or shared client store if several screens need to coordinate it.

**Interview summary:** I classify state by ownership and lifetime. I keep transient UI state local, lift shared state to the nearest common owner, use Context or a client store when cross-tree sharing justifies it, use the URL for navigable state, and use a server-state library for remote data. I avoid duplicating derived or server-owned state unless there is a clear consistency strategy.

**Files changed:** None. In this session I’m restricted to read-only assistance; this is ready-to-insert material.
Updated todo list

## 52. Custom Hook vs Component

### Q54. When should you create a custom Hook versus a component?

Use a **component** to reuse rendered UI. Use a **custom Hook** to reuse React behavior or logic. They solve related but different problems, and a feature may use both.

A component returns UI:

```jsx
function UserCard({ user }) {
 return (
 <article>
 <h2>{user.name}</h2>
 <p>{user.role}</p>
 </article>
 );
}
```

A custom Hook shares behavior between components:

```jsx
function useOnlineStatus() {
 const [isOnline, setIsOnline] = useState(navigator.onLine);

 useEffect(() => {
 const handleOnline = () => setIsOnline(true);
 const handleOffline = () => setIsOnline(false);

 window.addEventListener("online", handleOnline);
 window.addEventListener("offline", handleOffline);

 return () => {
 window.removeEventListener("online", handleOnline);
 window.removeEventListener("offline", handleOffline);
 };
 }, []);

 return isOnline;
}
```

A screen can use the Hook and decide how to display its result:

```jsx
function ConnectionNotice() {
 const isOnline = useOnlineStatus();

 if (isOnline) return null;

 return <p role="status">You are offline.</p>;
}
```

A useful test is: **Am I trying to reuse what the user sees, or how the feature behaves?** Reuse presentation with a component. Reuse stateful behavior with a Hook.

#### What a custom Hook does, and does not do

A custom Hook is an ordinary JavaScript function whose name starts with `use` and which can call other Hooks. It can encapsulate details such as subscribing to browser events, tracking a form value, or coordinating a piece of UI behavior.

Calling a custom Hook does **not** automatically share one state instance between all its callers. If two components call `useOnlineStatus()`, each call has its own Hook state and effect. In this example, each caller independently subscribes to browser events, though the browser event itself is shared. If multiple components need one shared state source, use a shared owner, Context, or an external store as appropriate.

Hooks must follow the Rules of Hooks: call them at the top level of a function component or another custom Hook, not conditionally or inside loops. A custom Hook should usually return data and actions, not JSX. If the main purpose is to render a reusable visual unit, make it a component.

#### How the distinction developed

Before Hooks were introduced in React 16.8, developers often reused component behavior through higher-order components, render props, or class inheritance. Those patterns remain relevant in existing code, but can add wrapper layers or split related logic across components. Hooks made it possible to extract reusable stateful logic directly into functions while leaving each component in charge of its presentation.

This does not mean every repeated line needs a custom Hook. A small custom Hook with one caller can still be useful if it gives a complex behavior a clear name; a Hook created only to wrap a trivial expression can obscure rather than clarify the code.

#### Advantages and trade-offs

**Components:**
- Provide a clear, reusable UI boundary and can be tested through what they render and how users interact with them.
- Let teams centralize visual and accessibility behavior.
- Can become overly generic or accumulate unrelated responsibilities if reused without a clear design.

**Custom Hooks:**
- Reuse behavior without adding wrapper components to the rendered tree.
- Keep synchronization setup and cleanup together, and can make a component easier to read.
- Do not share state automatically, and can hide dependencies if their API is unclear.
- Can become difficult to understand when they combine unrelated effects, state, and network behavior.

**Real-time example:** A notification row’s layout belongs in a component such as `<NotificationRow />`. A Hook such as `useBatchSubscription(batchId)` could encapsulate subscribing to batch updates and cleaning up when the ID changes. A query library may be a better choice than a hand-written `useFetch` Hook for remote data, because it can provide caching, deduplication, retries, and invalidation.

**Interview summary:** I create components for reusable UI and custom Hooks for reusable React behavior. A Hook shares logic, not one global state instance. I choose an abstraction that makes ownership and behavior clearer, and avoid extracting code just because it appears twice.

---

## 53. Context Performance

### Q55. What is a common performance problem with Context?

When a Context provider’s `value` changes, React updates components that consume that Context. A common cause of unnecessary updates is creating a new object or function in the provider on every render:

```jsx
<AuthContext.Providervalue={{ user, login }}>
 {children}
</AuthContext.Provider>
```

Even if `user` and `login` have not meaningfully changed, `{ user, login }` creates a new object reference each time the provider renders. React compares the previous and next Context values with `Object.is`; those two object references differ, so consumers may update.

If profiling shows this is causing wasted work, stabilize the value:

```jsx
const value = useMemo(
 () => ({ user, login }),
 [user, login]
);

return (
 <AuthContext.Providervalue={value}>
 {children}
 </AuthContext.Provider>
);
```

This only helps if the dependencies are stable when their underlying values have not changed. If `login` is recreated every render, the memoized object still changes. Stabilize a function with `useCallback` only if its identity matters and doing so provides a real benefit.

#### What happens internally?

A component that calls `useContext(AuthContext)` subscribes to that Context value as part of rendering. When the provider’s value changes, React schedules updates for consumers of that Context. Context is not a field-level subscription system: if a provider supplies one large object and any part of it changes, consumers of that Context may be updated even if they only use one field.

This is separate from ordinary parent-child rendering. A consumer can receive a Context update even when wrapped with `memo`; memoization does not block updates to Context the component reads.

#### Ways to address unnecessary updates

-**Keep the provider value small.** Avoid combining unrelated, frequently changing values into one Context.
-**Split Contexts by responsibility.** For example, separate a theme from frequently changing batch data if their consumers differ.
-**Memoize provider values when useful.** Do this when a new object identity is causing measured unnecessary updates.
-**Move state closer to its consumers.** A global provider for a value used by one panel may broaden dependencies unnecessarily.
-**Use a store with selectors for high-frequency shared state.** This can let components subscribe to smaller slices of state.
-**Profile before optimizing.** Context updates are not automatically a performance problem; a small subtree may update cheaply.

**Real-time example:** An operations portal provides `{ user, permissions, selectedBatch, liveCounts }` through one Context. A change in `liveCounts` could update every component consuming that Context, including components that only need `user`. Splitting stable identity/permission data from rapidly changing live counts, or using a store with selectors for the latter, may reduce unrelated work.

#### Advantages and disadvantages

**Advantages of Context:** Built into React, convenient for values shared across a subtree, and avoids forwarding props through components that do not need them.

**Disadvantages:** It can hide dependencies, broad values can trigger updates across many consumers, and it does not provide caching, selectors, or sophisticated state tooling by itself. Memoization can reduce identity churn but introduces dependency complexity and is not a replacement for good ownership boundaries.

**Interview summary:** Context consumers can update when their provider’s value changes. Newly created objects and functions can change identity even when their contents look the same. I first check provider scope and value design, then use splitting or memoization if profiling shows unnecessary work. I do not assume Context is slow merely because it updates.

---

## 54. `useMemo` Misuse

### Q56. Why can excessive useMemo be bad?

`useMemo` caches the result of a calculation between renders while its dependencies remain unchanged. It can avoid repeating an expensive calculation, or preserve an object’s identity when that identity matters to a memoized child. It is not needed for every value.

This is usually unnecessary:

```jsx
const name = useMemo(() => user.name, [user.name]);
```

Reading a property is cheap. The memo adds a Hook, a dependency to maintain, and work for React to compare dependencies, without a meaningful performance improvement.

Prefer:

```jsx
const name = user.name;
```

Use `useMemo` when there is a specific reason, such as a calculation that profiling shows is expensive:

```jsx
const visibleBatches = useMemo(
 () => filterAndSortBatches(batches, filter, sortOrder),
 [batches, filter, sortOrder]
);
```

It can also preserve reference identity for a memoized child:

```jsx
const chartOptions = useMemo(
 () => ({ showLegend, color }),
 [showLegend, color]
);

return <Chartoptions={chartOptions} />;
```

#### How it works, and its limits

React stores the calculated value and its dependencies with the component’s Hook data. On a later render, it compares dependencies using `Object.is`. If they are unchanged, React may reuse the previous value; if any differ, React recalculates it.

`useMemo` does **not** prevent the component containing it from rendering, and it is not a persistent application cache. React treats memoization as an optimization, not a correctness guarantee. The application must still work if React recalculates the value. The calculation should be pure and must not perform side effects.

Dependencies must include all reactive values used by the calculation. An omitted dependency can make the cached result stale. Conversely, using a newly created object as a dependency can make the calculation run every render because the object has a new identity each time.

#### Advantages and disadvantages

**Advantages:** Can skip a genuinely expensive repeated calculation and can preserve object or array identity where that enables another measured optimization.

**Disadvantages:** Adds dependency and maintenance complexity, provides little benefit for cheap calculations, and can hide stale-dependency bugs. It does not make a component render less often by itself.

**Real-time example:** Filtering a handful of notification rows is probably cheap enough to calculate directly. Filtering and sorting tens of thousands of records on every unrelated render may warrant measurement and memoization; a virtualized list or server-side filtering might address the actual bottleneck better.

React Compiler, when configured in a project, can automate some memoization. Follow the project’s version and conventions; do not add manual memoization by habit.

**Interview summary:** I use `useMemo` for expensive calculations or stable identity when it solves a measured rendering problem. I do not memoize trivial expressions. It is a performance optimization, not state, a correctness mechanism, or a substitute for profiling.

---

## 55. Referential Equality

### Q57. What is referential equality and why is it important in React?

For objects and functions, equality often means checking whether two variables refer to the **same object in memory**, not whether their contents are equivalent.

```js
const first = { name: "John" };
const second = { name: "John" };

console.log(first === second); // false
```

The objects have the same contents, but they were created separately, so they have different identities. By contrast:

```js
const first = { name: "John" };
const second = first;

console.log(first === second); // true
```

Both variables refer to the same object.

React uses identity comparisons in several places. For example, `useMemo` and `useCallback` compare dependencies with `Object.is`; `React.memo` compares props by default using `Object.is` per prop; and Context checks whether the provider value changed. A freshly created object or function can therefore look changed even when its contents or behavior are equivalent.

```jsx
<Profilesettings={{ compact: true }} />
```

The `settings` object is new on each parent render. If `Profile` is memoized, that prop can still make it render again. But new function identities are often harmless; do not add `useMemo` or `useCallback` unless stable identity matters or profiling shows a problem.

Referential equality also explains why immutable updates matter. Creating a new object for changed state gives React and other tools a changed reference to observe. Mutating an existing object keeps the same reference and can make changes harder to detect.

**Real-time example:** A dashboard passes a new `{ from, to }` date-range object into several chart components on each render. If charts are expensive and memoized, stabilizing the range object may help. If the charts are cheap or their own inputs change anyway, memoization may add complexity without benefit.

---

## 56. Stale Closures

### Q58. What is a stale closure?

A **closure** is a function together with the variables from the scope where that function was created. In a function component, each render creates a new set of local variables and functions. An asynchronous callback created during one render may continue to hold values from that render even after the component has rendered again.

Consider this effect:

```jsx
useEffect(() => {
 const timer = setInterval(() => {
 console.log(count);
 }, 1000);

 return () => clearInterval(timer);
}, []);
```

Because the effect runs with an empty dependency list, the interval callback captures the `count` value from the render that created it. Later renders may have a new count, but the callback can continue logging the old value.

The issue is not that React failed to update `count`; the callback still refers to its original render’s snapshot.

#### Choose the fix that matches the requirement

**1. Use a functional updater when calculating new state from previous state.**

```jsx
useEffect(() => {
 const timer = setInterval(() => {
 setCount(previous => previous + 1);
 }, 1000);

 return () => clearInterval(timer);
}, []);
```

The callback does not need to read the captured `count`. React supplies the latest pending state to the updater. This is the simplest solution when the callback’s job is to update state based on its previous value.

**2. Include a reactive value as a dependency when the effect should resynchronize.**

```jsx
useEffect(() => {
 const timer = setInterval(() => {
 console.log(count);
 }, 1000);

 return () => clearInterval(timer);
}, [count]);
```

Now the interval is cleaned up and recreated whenever `count` changes. This is correct if changing `count` should restart the synchronization, but restarting a timer on every tick may not be desirable.

**3. In React versions that provide `useEffectEvent`, use it for non-reactive logic that needs the latest values.**

```jsx
const onTick = useEffectEvent(() => {
 console.log(count);
});

useEffect(() => {
 const timer = setInterval(() => onTick(), 1000);
 return () => clearInterval(timer);
}, []);
```

This is appropriate when the timer should remain established while its callback reads the latest `count`. `useEffectEvent` is version-dependent and intended for logic called from Effects or Effect-managed callbacks. It is not a general way to hide dependencies that should actually cause resynchronization.

**4. A ref can hold a latest value when that fits the design.**

Refs are mutable and do not trigger renders. They can be useful when an external callback needs the latest value without rebuilding a subscription, but keeping state and refs synchronized adds complexity. Prefer the updater, dependencies, or `useEffectEvent` when one of those directly matches the requirement.

#### Background and production relevance

Class components typically accessed current values through the component instance, while function components create closures for each render. Closures are normal JavaScript behavior and are often useful; they become surprising when a long-lived timer, subscription, or async callback outlives the render that created it.

Stale closures commonly appear in intervals, event listeners, subscriptions, debounced callbacks, and requests that complete after inputs change. Use the Hooks lint rules to catch missing dependencies, but do not silence a warning mechanically. Ask whether the effect should restart when the value changes, whether the callback only needs the previous state to calculate the next one, or whether it needs to read the latest value without restarting.

**Real-time example:** A batch-status subscription is established for `batchId`. If the batch ID changes, the subscription should usually be recreated, so `batchId` belongs in the dependencies. A periodic analytics callback may need the latest filter selection but should not restart its timer; an Effect Event or carefully managed ref may fit better.

#### Advantages and disadvantages of the approaches

-**Functional updater:** Concise and robust for next-state calculations; only applies when the update can be expressed from previous state.
-**Effect dependencies:** Makes synchronization inputs explicit; may recreate subscriptions or timers when a dependency changes.
-**Effect Event:** Separates latest-value event logic from synchronization setup in supported React versions; must only be used for its intended Effect-related purpose.
-**Ref:** Provides a stable mutable container; React does not track changes, so it can make data flow less obvious and requires careful synchronization.

**Interview summary:** A stale closure is an asynchronous or long-lived callback using values captured from an older render. I fix it based on intent: use a functional state updater when deriving from prior state, add dependencies when the external synchronization must change, or use a supported latest-value pattern when the synchronization should remain stable. I test timers and requests across changing props and state, and do not suppress dependency warnings without understanding the behavior.