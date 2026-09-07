Absolutely. Let’s understand **Redux Toolkit (RTK) in React.js** from the basics to a real-world e-commerce application.

> First, one important clarification: **Redux Toolkit** is the modern, recommended way to write Redux logic. You may hear people say “Redux” and “Redux Toolkit” interchangeably, but Redux Toolkit is the actual toolset you would normally use today.

# 1. What is Redux?

![Image](https://images.openai.com/static-rsc-4/Ktf_ZttrhE0jZq0auRP62xckrAMZIjRNEdj2f4yU3OknsajBkAAA2eG1Z9eSKAGZ0cPfiVQf11c3w3fsvW9AfURsUNAiQV8j3OHc4DHMBIgPXu75t833GQkf2DhEw4JWNzQmAcq_BnDXnbfONHztY0lPiD4RPJvON793JLtOZ18NC8XZjPHG_Mlkg3h_e9di?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/RIOkKz8R7vidgOJxJWMxj_Mno0n50FOuG6wo1H2u5HVtmzjzMfjxWwMR7yqnzedd5pYYWAASHq305KU3b3pKJ5xFjr5tGp979F4m16cqMb-Hz1dRY7nTdBdTbYhnx9kaZ2rEToBfyetaekjYxx8O941B30DQJ-28Mt97rOm07hV1XYAhnAYiebszJMM6KtUR?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/sceGBYaID6APpM8umOM2H3DifAuDZAF_utYyb7AlQlG935OZLSHxkpgNwDCN-_Gf6SBPg6K9ctR8_f52Yh-4hTkY9aCWoDgbjEuS9Ndy2Or1uiBaUvDhr3F2MrZKJ8fN0pi77p_-5A6P2I0iSyvbLB3umKgdm_k53MLPMDdgow3oGLyQSSFbfVvO4NTlmZ1M?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/RHpNkDMJh16P7jHGaEu7_ugHnQAs83TjbWrDXq2k4dwI-hQRuo2TkdfneyWugOMrJp5LbWyX38vPkF0hgLxgYawDJ_ga-w8sNPyHqsR6_UG6VOGhqEvgoqy63GScdhfRw0gjdK78nsS5RgEqIpmyGdDEogzntpZM0Bex9d1ccfCGEREH--Hu-tjIxlh06O_5?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/-30MysOfNDFSeHYReUIJOOaCsi5nMycP195ANnCw9CSAi4W-6XWJ-Vxw337-Dse2zjmoet7kIaNeaipkh2dXaceSwV8VoPdDfAEFAVx3_GT8NjrZOkT6dktOZxj7y_B37OncTb1K2S3V2DVaI9qq8XnESR7hfzGLk4nLc1XE17kL20Y_JpkPsWzqLakpj3Jq?purpose=fullsize)

**Redux is a state management library.**

In a React application, components normally manage their own state:

```jsx
const [count, setCount] = useState(0);
```

But imagine you have an e-commerce application with:

* Login user
* Shopping cart
* Products
* Wishlist
* Orders
* Notifications
* User preferences

Many different components may need access to the same data.

For example:

```text
Navbar
   ↓
Cart count

Product Page
   ↓
Add product to cart

Cart Page
   ↓
Display cart products

Checkout Page
   ↓
Read cart products

Payment Page
   ↓
Calculate total
```

Passing this information through many components can become difficult.

Redux provides a **centralized store** where shared application state can be maintained.

```text
                    Redux Store
                        |
        +---------------+---------------+
        |               |               |
      User            Cart           Products
        |               |               |
     Navbar        Cart Page       Product Page
```

---

# 2. Why do we need Redux?

Consider this component structure:

```text
App
 |
 +-- Navbar
 |
 +-- Home
 |    |
 |    +-- ProductList
 |          |
 |          +-- Product
 |
 +-- Cart
 |
 +-- Checkout
```

Suppose `Product` needs to add something to the cart.

Without Redux, you might pass functions and state through multiple components:

```text
App
 ↓
Home
 ↓
ProductList
 ↓
Product
```

This is called **prop drilling**.

For example:

```jsx
<App>
  <Home addToCart={addToCart}>
    <ProductList addToCart={addToCart}>
      <Product addToCart={addToCart} />
    </ProductList>
  </Home>
</App>
```

As the application grows, this becomes difficult to maintain.

Redux solves this by providing a centralized state:

```text
                 Redux Store
                     |
          +----------+----------+
          |                     |
       Product                Cart
          |                     |
          ↓                     ↓
     Product Page          Cart Page
```

---

# 3. What is Redux Toolkit?

Redux Toolkit provides utilities that make Redux easier to write.

The main things you will use are:

```text
configureStore()
createSlice()
createAsyncThunk()
createSelector()
```

And in React:

```text
Provider
useSelector()
useDispatch()
```

The important ones to learn first are:

```text
configureStore
createSlice
useSelector
useDispatch
```

---

# 4. Redux Architecture

The basic Redux architecture looks like this:

```text
             React Component
                   |
                   | dispatch()
                   ↓
                Action
                   |
                   ↓
                Reducer
                   |
                   ↓
              Redux Store
                   |
                   | state changes
                   ↓
             React Component
```

Let's understand each part.

---

# 5. Store

The **store** contains your application's global state.

Example:

```javascript
{
    user: {
        name: "Gowtham",
        loggedIn: true
    },

    cart: {
        items: [],
        total: 0
    },

    products: []
}
```

You normally configure the store using:

```javascript
configureStore()
```

Example:

```javascript
import { configureStore } from "@reduxjs/toolkit";

const store = configureStore({
    reducer: {
        // reducers go here
    }
});

export default store;
```

---

# 6. Slice

A **slice** represents one logical part of your Redux state.

For example:

```text
userSlice
cartSlice
productSlice
orderSlice
```

An e-commerce application might have:

```text
Redux Store
│
├── user
├── cart
├── products
├── orders
└── wishlist
```

Each is normally implemented as a slice.

---

# 7. createSlice()

Let's create a simple cart slice.

```javascript
import { createSlice } from "@reduxjs/toolkit";

const cartSlice = createSlice({
    name: "cart",

    initialState: {
        items: [],
        total: 0
    },

    reducers: {

        addToCart: (state, action) => {
            state.items.push(action.payload);
        },

        removeFromCart: (state, action) => {
            state.items = state.items.filter(
                item => item.id !== action.payload
            );
        }
    }
});

export const {
    addToCart,
    removeFromCart
} = cartSlice.actions;

export default cartSlice.reducer;
```

There are several important things here.

---

# 8. name

```javascript
name: "cart"
```

This identifies the slice.

---

# 9. initialState

```javascript
initialState: {
    items: [],
    total: 0
}
```

This is the initial state of the cart.

Initially:

```text
items = []
total = 0
```

After adding products:

```text
items = [
   { id: 1, name: "iPhone" },
   { id: 2, name: "Laptop" }
]
```

---

# 10. Reducers

Reducers describe **how the state can change**.

Example:

```javascript
reducers: {

    addToCart: (state, action) => {
        state.items.push(action.payload);
    }

}
```

Here:

```text
addToCart
```

is the reducer/action name.

---

# 11. What is action?

When you execute:

```javascript
dispatch(addToCart(product));
```

Redux creates an action approximately like:

```javascript
{
    type: "cart/addToCart",
    payload: product
}
```

So:

```text
dispatch()
   ↓
Action
   ↓
Reducer
   ↓
State update
```

---

# 12. What is payload?

The data you send with an action is called the **payload**.

Example:

```javascript
dispatch(addToCart({
    id: 101,
    name: "iPhone",
    price: 80000
}));
```

The payload is:

```javascript
{
    id: 101,
    name: "iPhone",
    price: 80000
}
```

Inside reducer:

```javascript
addToCart: (state, action) => {

    console.log(action.payload);

}
```

You get the product using:

```javascript
action.payload
```

---

# 13. configureStore()

Now connect our cart reducer to the Redux store.

```javascript
import { configureStore } from "@reduxjs/toolkit";
import cartReducer from "./cartSlice";

const store = configureStore({

    reducer: {
        cart: cartReducer
    }

});

export default store;
```

Now the Redux state looks like:

```javascript
{
    cart: {
        items: [],
        total: 0
    }
}
```

Notice:

```javascript
cart
```

comes from:

```javascript
reducer: {
    cart: cartReducer
}
```

---

# 14. Provider

React needs to know where the Redux store is available.

That's why we use:

```jsx
<Provider store={store}>
    <App />
</Provider>
```

Usually in:

```text
main.jsx
```

or:

```text
index.jsx
```

Example:

```jsx
import React from "react";
import ReactDOM from "react-dom/client";
import { Provider } from "react-redux";

import App from "./App";
import store from "./store";

ReactDOM.createRoot(
    document.getElementById("root")
).render(
    <Provider store={store}>
        <App />
    </Provider>
);
```

Now every component inside `Provider` can access Redux.

---

# 15. useDispatch()

`useDispatch()` is used when you want to **change Redux state**.

Example:

```jsx
import { useDispatch } from "react-redux";
import { addToCart } from "./cartSlice";

function Product() {

    const dispatch = useDispatch();

    const product = {
        id: 1,
        name: "iPhone",
        price: 80000
    };

    const handleAddToCart = () => {

        dispatch(addToCart(product));

    };

    return (
        <button onClick={handleAddToCart}>
            Add to Cart
        </button>
    );
}
```

The flow is:

```text
User clicks button
       ↓
handleAddToCart()
       ↓
dispatch(addToCart(product))
       ↓
Redux action
       ↓
cart reducer
       ↓
Redux state changes
```

---

# 16. useSelector()

`useSelector()` is used to **read data from Redux**.

Example:

```jsx
import { useSelector } from "react-redux";

function Cart() {

    const items = useSelector(
        state => state.cart.items
    );

    return (
        <div>
            {items.map(item => (
                <p key={item.id}>
                    {item.name}
                </p>
            ))}
        </div>
    );
}
```

Here:

```javascript
state.cart.items
```

means:

```text
Redux Store
    ↓
cart
    ↓
items
```

---

# 17. Complete flow

This is probably the **most important concept for interviews**.

Suppose the user clicks:

```text
Add to Cart
```

The flow is:

```text
             React Component
                    |
                    |
              dispatch()
                    |
                    ↓
                  Action
                    |
                    ↓
                Reducer
                    |
                    ↓
              Redux Store
                    |
                    ↓
              State Updated
                    |
                    ↓
             React Re-renders
```

Example:

```javascript
dispatch(addToCart(product));
```

↓

```text
cart/addToCart
```

↓

Reducer:

```javascript
addToCart: (state, action) => {
    state.items.push(action.payload);
}
```

↓

Store:

```javascript
cart.items
```

changes.

↓

Components using:

```javascript
useSelector()
```

receive the updated state.

---

# 18. Why can we directly modify state?

This is one of the most common Redux interview questions.

You might see:

```javascript
state.items.push(action.payload);
```

Normally in React, we shouldn't mutate state directly:

```javascript
state.items.push(product);
```

Redux Toolkit allows this syntax because it internally uses **Immer**.

You write:

```javascript
state.items.push(product);
```

but Immer produces an immutable state update behind the scenes.

So you get the convenience of mutation-like syntax while Redux maintains immutable state semantics.

---

# 19. Multiple slices

Real applications usually have multiple slices.

For example:

```text
src
│
├── store
│    └── store.js
│
├── features
│    │
│    ├── auth
│    │    └── authSlice.js
│    │
│    ├── cart
│    │    └── cartSlice.js
│    │
│    ├── products
│    │    └── productSlice.js
│    │
│    └── orders
│         └── orderSlice.js
```

Store:

```javascript
const store = configureStore({

    reducer: {

        auth: authReducer,

        cart: cartReducer,

        products: productReducer,

        orders: orderReducer

    }

});
```

The state becomes:

```javascript
{
    auth: {},
    cart: {},
    products: {},
    orders: {}
}
```

---

# 20. Real-world E-commerce example

Let's design a simple e-commerce application.

```text
                 E-Commerce App
                       |
              Redux Global Store
                       |
       +---------------+----------------+
       |               |                |
      Auth            Cart           Products
       |               |                |
       ↓               ↓                ↓
     User            Items           Product List
```

### Auth Slice

```javascript
const initialState = {
    user: null,
    token: null,
    isAuthenticated: false
};
```

### Cart Slice

```javascript
const initialState = {
    items: [],
    totalQuantity: 0,
    totalAmount: 0
};
```

### Product Slice

```javascript
const initialState = {
    products: [],
    loading: false,
    error: null
};
```

### Order Slice

```javascript
const initialState = {
    orders: [],
    loading: false
};
```

---

# 21. Redux and API calls

This is where Redux becomes particularly useful.

Suppose you need to get products from backend:

```text
React
   ↓
GET /api/products
   ↓
Spring Boot
   ↓
Database
```

You can manage:

```text
loading
success
error
data
```

with Redux.

For asynchronous operations, Redux Toolkit provides:

```javascript
createAsyncThunk()
```

---

# 22. createAsyncThunk()

Example:

```javascript
import {
    createAsyncThunk,
    createSlice
} from "@reduxjs/toolkit";
```

Create API operation:

```javascript
export const fetchProducts = createAsyncThunk(
    "products/fetchProducts",

    async () => {

        const response =
            await fetch("/api/products");

        return response.json();
    }
);
```

Now create the slice:

```javascript
const productSlice = createSlice({

    name: "products",

    initialState: {
        products: [],
        loading: false,
        error: null
    },

    reducers: {},

    extraReducers: (builder) => {

        builder

            .addCase(
                fetchProducts.pending,
                (state) => {
                    state.loading = true;
                }
            )

            .addCase(
                fetchProducts.fulfilled,
                (state, action) => {

                    state.loading = false;

                    state.products =
                        action.payload;
                }
            )

            .addCase(
                fetchProducts.rejected,
                (state, action) => {

                    state.loading = false;

                    state.error =
                        action.error.message;
                }
            );
    }
});
```

---

# 23. Why pending / fulfilled / rejected?

An API call can have three states:

```text
             API Request
                  |
       +----------+----------+
       |          |          |
     pending   fulfilled   rejected
       |          |          |
    Loading     Success      Error
```

For example:

```javascript
fetchProducts.pending
```

means:

```text
Request started
```

```javascript
fetchProducts.fulfilled
```

means:

```text
Request succeeded
```

```javascript
fetchProducts.rejected
```

means:

```text
Request failed
```

---

# 24. Calling the API from React

```jsx
import { useDispatch, useSelector }
    from "react-redux";

import { fetchProducts }
    from "./productSlice";

function ProductList() {

    const dispatch = useDispatch();

    const {
        products,
        loading,
        error
    } = useSelector(
        state => state.products
    );

    useEffect(() => {

        dispatch(fetchProducts());

    }, [dispatch]);

    if (loading) {
        return <p>Loading...</p>;
    }

    if (error) {
        return <p>{error}</p>;
    }

    return (
        <div>
            {products.map(product => (
                <p key={product.id}>
                    {product.name}
                </p>
            ))}
        </div>
    );
}
```

---

# 25. Redux with your Spring Boot backend

Since you were asking earlier about **React + Spring Boot + JWT**, a common architecture would look like this:

```text
                    React
                      |
              Redux Toolkit
                      |
          +-----------+-----------+
          |                       |
       Auth Slice             Product Slice
          |                       |
          ↓                       ↓
      JWT Token              Product Data
          |                       |
          +-----------+-----------+
                      |
                   Axios
                      |
                      ↓
                Spring Boot
                      |
               Spring Security
                      |
                      ↓
                  Database
```

For example, login:

```text
React Login
    ↓
dispatch(loginUser(credentials))
    ↓
createAsyncThunk
    ↓
POST /api/login
    ↓
Spring Boot
    ↓
Validate username/password
    ↓
Generate JWT
    ↓
Return JWT
    ↓
Redux auth state
```

The important distinction is:

**Redux does not create the JWT.**

Your backend creates the JWT.

Redux can store the authentication state/token on the frontend side and make it available to the application.

---

# 26. Redux vs useState

This is another common interview question.

### useState

Use it for **local component state**.

Example:

```javascript
const [isOpen, setIsOpen] = useState(false);
```

Maybe only one component cares about it.

```text
ProductFilter
     |
   isOpen
```

### Redux

Use it for **shared application state**.

For example:

```text
                  Cart
                   |
       +-----------+-----------+
       |           |           |
    Navbar      CartPage    Checkout
```

All three may need cart information.

---

# 27. Redux vs Context API

They are not exactly the same.

### Context

Good for relatively simple global values:

```text
Theme
Language
Current user
```

### Redux Toolkit

Better suited to complex application state:

```text
Products
Cart
Orders
Authentication
Wishlist
Notifications
Complex async operations
```

For example:

```text
Context
   ↓
Simple global data

Redux
   ↓
Complex shared application state
```

---

# 28. What happens when Redux state changes?

Suppose:

```javascript
const cartItems =
    useSelector(state => state.cart.items);
```

Initially:

```javascript
[]
```

User clicks:

```text
Add to Cart
```

Then:

```javascript
dispatch(addToCart(product));
```

Reducer updates the state.

Redux notifies subscribers.

The component using that selected state is rendered with the new value.

```text
Old State
items = []

       ↓

dispatch(addToCart(product))

       ↓

Reducer

       ↓

New State
items = [product]

       ↓

React updates UI
```

---

# 29. Selectors

Instead of writing complex state access everywhere:

```javascript
state.cart.items
```

you can create selectors.

Example:

```javascript
const selectCartItems =
    state => state.cart.items;
```

Then:

```javascript
const items =
    useSelector(selectCartItems);
```

You can also create derived selectors.

For example:

```javascript
const selectCartTotal = state =>
    state.cart.items.reduce(
        (total, item) =>
            total + item.price * item.quantity,
        0
    );
```

Then:

```javascript
const total =
    useSelector(selectCartTotal);
```

For more advanced memoized selectors, Redux Toolkit works with `createSelector`.

---

# 30. Redux DevTools

One major advantage of Redux is **Redux DevTools**.

You can inspect:

```text
Actions
State
Previous State
Next State
Payload
```

For example:

```text
Action:
cart/addToCart

Payload:
{
    id: 101,
    name: "iPhone"
}
```

You can see how the state changed.

This is extremely useful for debugging large applications.

---

# 31. Redux folder structure

A good modern structure could look like:

```text
src/
│
├── app/
│   └── store.js
│
├── features/
│   │
│   ├── auth/
│   │   ├── authSlice.js
│   │   └── authApi.js
│   │
│   ├── products/
│   │   ├── productSlice.js
│   │   └── productApi.js
│   │
│   ├── cart/
│   │   └── cartSlice.js
│   │
│   └── orders/
│       └── orderSlice.js
│
├── components/
│
├── pages/
│
├── services/
│
└── App.jsx
```

This is generally easier to maintain than putting all Redux code into one huge file.

---

# 32. Complete mini example

Let's connect everything.

### cartSlice.js

```javascript
import { createSlice } from "@reduxjs/toolkit";

const cartSlice = createSlice({

    name: "cart",

    initialState: {
        items: []
    },

    reducers: {

        addToCart: (state, action) => {

            state.items.push(action.payload);

        },

        removeFromCart: (state, action) => {

            state.items =
                state.items.filter(
                    item =>
                        item.id !== action.payload
                );

        }

    }

});

export const {
    addToCart,
    removeFromCart
} = cartSlice.actions;

export default cartSlice.reducer;
```

### store.js

```javascript
import { configureStore }
    from "@reduxjs/toolkit";

import cartReducer
    from "../features/cart/cartSlice";

export const store = configureStore({

    reducer: {

        cart: cartReducer

    }

});
```

### main.jsx

```jsx
import { Provider }
    from "react-redux";

import { store }
    from "./app/store";

ReactDOM.createRoot(
    document.getElementById("root")
).render(

    <Provider store={store}>

        <App />

    </Provider>

);
```

### Product.jsx

```jsx
import { useDispatch }
    from "react-redux";

import { addToCart }
    from "../features/cart/cartSlice";

function Product() {

    const dispatch = useDispatch();

    const product = {
        id: 1,
        name: "Laptop",
        price: 70000
    };

    return (

        <button
            onClick={() =>
                dispatch(addToCart(product))
            }
        >
            Add to Cart

        </button>

    );
}

export default Product;
```

### Cart.jsx

```jsx
import { useSelector }
    from "react-redux";

function Cart() {

    const items =
        useSelector(
            state => state.cart.items
        );

    return (

        <div>

            <h2>Cart</h2>

            {items.map(item => (

                <div key={item.id}>

                    {item.name}
                    -
                    ₹{item.price}

                </div>

            ))}

        </div>

    );
}

export default Cart;
```

Now the complete flow is:

```text
Product.jsx
     |
     | dispatch(addToCart(product))
     ↓
cartSlice
     |
     | reducer
     ↓
Redux Store
     |
     | state.cart.items updated
     ↓
Cart.jsx
     |
     | useSelector()
     ↓
Updated UI
```

---

# 33. The most important Redux Toolkit concepts

For interviews and real projects, learn these in this order:

```text
1. Redux Store
       ↓
2. createSlice
       ↓
3. Initial State
       ↓
4. Reducer
       ↓
5. Action
       ↓
6. Payload
       ↓
7. dispatch()
       ↓
8. useSelector()
       ↓
9. Provider
       ↓
10. createAsyncThunk
       ↓
11. extraReducers
       ↓
12. Selectors
       ↓
13. Redux DevTools
       ↓
14. Middleware
       ↓
15. RTK Query
```

---

# 34. Redux Toolkit vs old Redux

You may see older Redux code like:

```javascript
const ADD_TO_CART = "ADD_TO_CART";

function addToCart(product) {
    return {
        type: ADD_TO_CART,
        payload: product
    };
}

function cartReducer(state, action) {

    switch(action.type) {

        case ADD_TO_CART:
            return {
                ...state,
                items: [
                    ...state.items,
                    action.payload
                ]
            };

        default:
            return state;
    }
}
```

This requires a lot of boilerplate.

Redux Toolkit simplifies it to:

```javascript
const cartSlice = createSlice({

    name: "cart",

    initialState: {
        items: []
    },

    reducers: {

        addToCart: (state, action) => {
            state.items.push(action.payload);
        }

    }

});
```

That's why **Redux Toolkit is preferred for modern Redux applications**.

---

# 35. One very important concept: Redux does NOT mean every state goes into Redux

This is a common beginner mistake.

Don't put everything into Redux.

For example:

```javascript
const [isModalOpen, setIsModalOpen] =
    useState(false);
```

If only one component needs it, keep it local.

But:

```text
Logged-in user
Cart
Products
Orders
Wishlist
```

may be good candidates for shared/global state.

Think:

```text
Does multiple unrelated components need this data?
                |
              YES
                ↓
          Consider Redux
                |
               NO
                ↓
          useState/local state
```

---

# 36. Redux in an e-commerce application

A realistic architecture could look like:

```text
                         React Application
                                |
                         Redux Toolkit
                                |
      +------------+------------+-------------+-------------+
      |            |            |             |             |
     Auth        Products      Cart         Orders       Wishlist
      |            |            |             |             |
      +------------+------------+-------------+-------------+
                                |
                             API Layer
                                |
                              Axios
                                |
                                ↓
                         Spring Boot APIs
                                |
                         Spring Security
                                |
                              JWT
                                |
                                ↓
                            Database
```

For example, when the user logs in:

```text
React Login Form
       ↓
dispatch(loginUser())
       ↓
createAsyncThunk
       ↓
POST /api/login
       ↓
Spring Boot
       ↓
Authentication
       ↓
JWT generated
       ↓
JWT returned
       ↓
authSlice
       ↓
Redux state
```

When the user adds a product:

```text
Product Page
      ↓
dispatch(addToCart(product))
      ↓
cartSlice
      ↓
Redux Store
      ↓
Navbar
      ↓
Cart count = 1
```

When the user checks out:

```text
Checkout
    ↓
useSelector()
    ↓
Cart data
    ↓
POST /api/orders
    ↓
Spring Boot
    ↓
Database
```

---

## The simplest way to remember Redux

Think of Redux as a **central warehouse for application state**:

```text
                 ┌─────────────────┐
                 │   Redux Store   │
                 │                 │
                 │ User            │
                 │ Products        │
                 │ Cart            │
                 │ Orders          │
                 │ Wishlist        │
                 └────────┬────────┘
                          │
             ┌────────────┼────────────┐
             ↓            ↓            ↓
          Navbar       Products       Cart
```

**To read data:**

```javascript
useSelector()
```

**To change data:**

```javascript
useDispatch()
```

**To define how data changes:**

```javascript
createSlice()
```

**To create the store:**

```javascript
configureStore()
```

**To handle API calls:**

```javascript
createAsyncThunk()
```

And for modern applications, also learn **RTK Query**, which can significantly simplify API fetching, caching, loading/error states, and server-state management.

If you're preparing for a **React + Spring Boot interview**, the next useful step is to understand **Redux Toolkit + JWT authentication + Axios interceptors together**, because that shows how Redux is actually used in a production e-commerce application.
