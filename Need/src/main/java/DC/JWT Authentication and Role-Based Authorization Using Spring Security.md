# JWT Authentication and Role-Based Authorization Using Spring Security

## 1. Overview

Spring Security is commonly used in Spring Boot applications to implement **authentication** and **authorization**.

In a typical REST API application, Spring Security helps us answer two important questions:

1. **Who is the user?** → Authentication
2. **What is the user allowed to do?** → Authorization

When using JWT, the application can implement authentication in a **stateless** way, which is particularly suitable for REST APIs and microservices.

---

# 2. What Problem Does Spring Security Solve?

Spring Security provides three major capabilities:

### 1. Authentication

Authentication verifies the identity of a user.

In simple terms:

> "Who are you?"

For example:

```text
Username: goutham
Password: ********
```

Spring Security verifies whether these credentials are valid.

---

### 2. Authorization

Authorization determines what an authenticated user is allowed to access.

In simple terms:

> "What are you allowed to do?"

For example:

```text
ADMIN → Can delete users
USER  → Cannot delete users
```

---

### 3. Protection Against Common Attacks

Spring Security also provides protection against common security-related attacks, including:

* CSRF
* Session fixation
* Other common web security concerns

---

# 3. Authentication vs Authorization

These two concepts are fundamental to understanding Spring Security.

| Concept            | Meaning                                           | Example                       |
| ------------------ | ------------------------------------------------- | ----------------------------- |
| **Authentication** | Verifies the user's identity                      | Username/password login       |
| **Authorization**  | Determines what the authenticated user can access | Only `ADMIN` can delete users |

### Easy way to remember

```text
Authentication
      ↓
"Who are you?"

Authorization
      ↓
"What are you allowed to access?"
```

Authentication happens before authorization.

---

# 4. Why JWT?

Traditional web applications commonly use **server-side sessions**.

The server maintains information about the authenticated user in a session.

However, modern REST APIs are usually designed to be **stateless**.

For stateless authentication, applications commonly use **JSON Web Tokens (JWT)**.

---

## Benefits of JWT

JWT provides several benefits:

* **Stateless authentication**
* Easily scalable in microservices
* Suitable for mobile applications
* Suitable for Single Page Applications (SPA)
* Each request carries its own authentication token

The important concept is:

```text
Client
  |
  | Request + JWT
  ↓
Server
```

The server can use the JWT to determine the authenticated user's identity.

---

# 5. JWT Structure

A JWT has three main parts:

```text
Header.Payload.Signature
```

These three parts are separated using `.`.

Conceptually:

```text
Header
   .
Payload
   .
Signature
```

---

## Example JWT Payload

```json
{
  "sub": "goutham",
  "roles": ["ROLE_ADMIN", "ROLE_USER"],
  "exp": 1719999999
}
```

The payload contains information about the token.

In this example:

| Claim   | Meaning                        |
| ------- | ------------------------------ |
| `sub`   | Subject / username             |
| `roles` | Roles associated with the user |
| `exp`   | Token expiration time          |

---

# 6. Complete JWT Login and Request Flow

The overall authentication flow can be understood as follows:

```text
Client
   |
   | POST /login
   | username + password
   ↓
AuthenticationManager
   |
   ↓
UserDetailsService
   |
   ↓
PasswordEncoder
   |
   ↓
Credentials verified
   |
   ↓
JWT generated
   |
   ↓
Client stores JWT
   |
   ↓
Client sends:
Authorization: Bearer <token>
   |
   ↓
JwtAuthenticationFilter
   |
   ↓
JWT validated
   |
   ↓
SecurityContextHolder
   |
   ↓
Authentication stored
   |
   ↓
Spring Security Authorization
   |
   ↓
Request allowed / denied
```

This is the most important flow to understand.

---

# 7. Step-by-Step Explanation of the Flow

## Step 1 — Client Sends Login Request

The client sends the user's credentials to the login endpoint.

```text
POST /login
```

With:

```text
username
password
```

For example:

```text
Client
   ↓
POST /login
   ↓
username + password
```

---

## Step 2 — AuthenticationManager Authenticates the User

The `AuthenticationManager` is responsible for performing authentication.

It receives the user's credentials and attempts to authenticate them.

```text
AuthenticationManager
        ↓
Authenticate credentials
```

---

## Step 3 — UserDetailsService Loads the User

`UserDetailsService` is responsible for loading the user's security information.

Typically, it retrieves the user from the database.

```text
UserDetailsService
        ↓
User Database
        ↓
User information
```

---

## Step 4 — PasswordEncoder Verifies the Password

The `PasswordEncoder` is responsible for password hashing and verification.

The application should not store passwords in plain text.

During registration:

```text
Raw Password
     ↓
PasswordEncoder
     ↓
Encoded Password
     ↓
Database
```

During login, Spring Security uses the encoded password to verify the supplied password.

---

## Step 5 — JWT Is Generated

After successful authentication, the application generates a JWT.

```text
Authentication Successful
        ↓
Generate JWT
        ↓
Return JWT to Client
```

The client can then use this JWT for subsequent requests.

---

## Step 6 — Client Sends JWT with Every Request

For subsequent API requests, the client sends the JWT using the `Authorization` header.

```text
Authorization: Bearer <token>
```

Example:

```text
GET /user/profile

Authorization: Bearer eyJhbGciOi...
```

---

## Step 7 — JwtAuthenticationFilter Validates the Token

The `JwtAuthenticationFilter` runs for incoming requests.

It:

1. Reads the `Authorization` header.
2. Checks whether it starts with `Bearer `.
3. Extracts the JWT.
4. Extracts the username.
5. Loads the user.
6. Validates the JWT.
7. Creates an authentication object.
8. Stores the authentication in `SecurityContextHolder`.

---

## Step 8 — SecurityContextHolder Stores Authentication

Once the JWT has been successfully validated, Spring Security stores the authenticated user's information in:

```java
SecurityContextHolder
```

Conceptually:

```text
JWT
 ↓
JwtAuthenticationFilter
 ↓
Authentication
 ↓
SecurityContextHolder
```

After this, Spring Security considers the request authenticated.

---

## Step 9 — Spring Security Performs Authorization

Once the user is authenticated, Spring Security checks whether the user has the required role or permission.

For example:

```text
/admin/**
      ↓
Requires ADMIN

/user/**
      ↓
Requires USER or ADMIN
```

If the user has the required role, access is granted.

Otherwise, access is denied.

---

# 8. Core Spring Security Components

The main components involved are:

| Component               | Responsibility                       |
| ----------------------- | ------------------------------------ |
| `UserDetailsService`    | Loads the user from the database     |
| `UserDetails`           | Security representation of the user  |
| `PasswordEncoder`       | Hashes and verifies passwords        |
| `AuthenticationManager` | Performs authentication              |
| JWT Utility             | Generates and validates JWTs         |
| `OncePerRequestFilter`  | Parses the JWT on every request      |
| `SecurityFilterChain`   | Configures Spring Security           |
| `SecurityContextHolder` | Holds the current authenticated user |

---

# 9. Database Design

A simple database design contains:

1. User table
2. Role table
3. User-role join table

---

## User Table

```text
users
-----
id
username
password
enabled
```

The `users` table stores the user's basic information.

---

## Role Table

```text
roles
-----
id
name
```

Examples of roles:

```text
ROLE_ADMIN
ROLE_USER
```

---

## User-Role Join Table

```text
user_roles
----------
user_id
role_id
```

This table represents the relationship between users and roles.

Conceptually:

```text
User
 |
 | Many-to-Many
 |
Role
```

A user can have multiple roles.

For example:

```text
goutham
   |
   ├── ROLE_USER
   |
   └── ROLE_ADMIN
```

---

# 10. User Entity

The User entity represents the application user.

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}
```

The important fields are:

```text
id
username
password
roles
```

The `roles` field represents the user's roles.

The relationship is configured using:

```java
@ManyToMany(fetch = FetchType.EAGER)
```

and:

```java
@JoinTable(...)
```

The join table is:

```text
user_roles
```

---

# 11. Role Entity

The Role entity represents a user's role.

```java
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;   // ROLE_ADMIN, ROLE_USER

    // Constructors
    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

The role name can contain values such as:

```text
ROLE_ADMIN
ROLE_USER
```

---

# 12. Repository Layer

The repository layer is responsible for interacting with the database.

---

## UserRepository

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
```

The important method is:

```java
findByUsername(String username)
```

It retrieves a user using the username.

---

## RoleRepository

```java
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
```

The important method is:

```java
findByName(String name)
```

It retrieves a role using its name.

---

# 13. Custom UserDetailsService

Spring Security needs a mechanism to load user information.

This is provided by a custom implementation of:

```java
UserDetailsService
```

Example:

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + username));

        List<GrantedAuthority> authorities =
                user.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .toList();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
```

---

# 14. Understanding `loadUserByUsername()`

The method:

```java
loadUserByUsername(String username)
```

performs the following steps.

### Step 1 — Find the user

```java
User user = userRepository.findByUsername(username)
```

The application searches the database.

---

### Step 2 — Handle User Not Found

If the user does not exist:

```java
throw new UsernameNotFoundException(
    "User not found: " + username
);
```

---

### Step 3 — Convert Roles into Authorities

The user's roles are converted into Spring Security authorities.

```java
List<GrantedAuthority> authorities =
        user.getRoles()
                .stream()
                .map(role ->
                    new SimpleGrantedAuthority(role.getName()))
                .toList();
```

For example:

```text
Database:

ROLE_ADMIN
ROLE_USER
```

becomes:

```text
GrantedAuthority
    ↓
ROLE_ADMIN
ROLE_USER
```

---

### Step 4 — Return UserDetails

The method returns Spring Security's `UserDetails` representation.

```java
return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getPassword(),
        authorities
);
```

So the overall process is:

```text
Database User
     ↓
User Entity
     ↓
Roles
     ↓
GrantedAuthority
     ↓
UserDetails
     ↓
Spring Security
```

---

# 15. Password Encoding

Passwords should not be stored as plain text.

A `PasswordEncoder` is used to encode and verify passwords.

Example:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

The application uses BCrypt for password encoding.

---

## Registration

During registration:

```text
Raw Password
      ↓
BCryptPasswordEncoder
      ↓
Encoded Password
      ↓
Database
```

The raw password is not stored directly.

---

## Login

During login:

```text
User enters password
        ↓
PasswordEncoder
        ↓
Password verification
        ↓
Authentication result
```

Spring Security automatically uses `matches()` to verify the password.

---

# 16. AuthenticationManager Bean

The `AuthenticationManager` is used to perform authentication.

It can be exposed as a Spring bean:

```java
@Bean
public AuthenticationManager authenticationManager(
        AuthenticationConfiguration configuration) throws Exception {

    return configuration.getAuthenticationManager();
}
```

This bean is then used by the login API to authenticate the user's credentials.

Conceptually:

```text
Login Request
      ↓
AuthenticationManager
      ↓
Authentication
```

---

# 17. JWT Utility

The JWT utility class is responsible for JWT-related operations.

Its responsibilities include:

1. Generate a JWT after successful login.
2. Extract the username from the JWT.
3. Extract roles from the JWT, optionally.
4. Validate the JWT signature and expiration.

Typical methods are:

```java
String generateToken(UserDetails userDetails);

String extractUsername(String token);

boolean isTokenValid(
        String token,
        UserDetails userDetails
);
```

---

# 18. JWT Libraries

Common JWT libraries include:

* `io.jsonwebtoken` — JJWT
* `com.auth0.jwt`

These libraries can be used to generate and validate JWT tokens.

---

# 19. JWT Authentication Filter

A custom JWT authentication filter generally extends:

```java
OncePerRequestFilter
```

This filter runs for requests and checks whether the request contains a JWT.

Its responsibilities are:

1. Read the `Authorization` header.
2. Verify that it starts with `Bearer `.
3. Extract the JWT.
4. Parse the username.
5. Load the user from the database.
6. Validate the token.
7. Create `UsernamePasswordAuthenticationToken`.
8. Store the authentication in `SecurityContextHolder`.

---

# 20. JWT Filter Flow

The complete process can be visualized as:

```text
HTTP Request
     ↓
Authorization Header
     ↓
Bearer <JWT>
     ↓
Extract JWT
     ↓
Extract Username
     ↓
Load User
     ↓
Validate JWT
     ↓
Create Authentication
     ↓
SecurityContextHolder
```

After successful execution:

```text
Spring Security
      ↓
User is authenticated
```

---

# 21. SecurityFilterChain

`SecurityFilterChain` defines how Spring Security protects the application's HTTP endpoints.

Example:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception {

    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session ->
                session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**")
                    .hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
        )
        .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

    return http.build();
}
```

Let's understand each part.

---

# 22. CSRF Configuration

```java
.csrf(csrf -> csrf.disable())
```

This disables CSRF protection in the shown configuration.

The provided configuration is intended for a stateless REST API using JWT authentication.

---

# 23. Stateless Session Management

```java
.sessionManagement(session ->
        session.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS))
```

This configures the application to use:

```text
STATELESS
```

This means the application does not rely on a server-side session to maintain authentication state.

Instead, the JWT is sent with each request.

```text
Request 1 → JWT
Request 2 → JWT
Request 3 → JWT
```

---

# 24. Public Authentication Endpoints

```java
.requestMatchers("/auth/**").permitAll()
```

This means endpoints under:

```text
/auth/**
```

are accessible without authentication.

For example:

```text
POST /auth/login
```

can be accessed by an unauthenticated user because the user needs to log in before obtaining a JWT.

---

# 25. ADMIN Authorization

```java
.requestMatchers("/admin/**").hasRole("ADMIN")
```

This means endpoints under:

```text
/admin/**
```

require the `ADMIN` role.

For example:

```text
DELETE /admin/users/10
```

requires:

```text
ROLE_ADMIN
```

---

# 26. USER and ADMIN Authorization

```java
.requestMatchers("/user/**")
    .hasAnyRole("USER", "ADMIN")
```

This means endpoints under:

```text
/user/**
```

can be accessed by users with either:

```text
ROLE_USER
```

or:

```text
ROLE_ADMIN
```

So:

```text
ROLE_USER   → Allowed
ROLE_ADMIN  → Allowed
```

---

# 27. Any Other Request

```java
.anyRequest().authenticated()
```

Any request that does not match the previous rules must be authenticated.

In other words:

```text
/auth/**      → Public
/admin/**     → ADMIN
/user/**      → USER or ADMIN
Everything else
              → Authenticated user required
```

---

# 28. Adding the JWT Filter

The JWT authentication filter is added before:

```java
UsernamePasswordAuthenticationFilter
```

Using:

```java
.addFilterBefore(
        jwtAuthenticationFilter,
        UsernamePasswordAuthenticationFilter.class
)
```

This ensures that the JWT filter participates in the Spring Security filter chain before the specified standard authentication filter.

---

# 29. Complete End-to-End Architecture

The entire implementation can be understood as the following sequence:

```text
                     CLIENT
                       |
                       |
                 POST /auth/login
                       |
                Username + Password
                       |
                       ↓
             AuthenticationManager
                       |
                       ↓
             UserDetailsService
                       |
                       ↓
                  UserRepository
                       |
                       ↓
                    Database
                       |
                       ↓
               User + Roles
                       |
                       ↓
                PasswordEncoder
                       |
                       ↓
              Authentication Success
                       |
                       ↓
                   JWT Utility
                       |
                       ↓
                  Generate JWT
                       |
                       ↓
                    CLIENT
```

The client then uses the token:

```text
CLIENT
   |
   | Authorization: Bearer <JWT>
   ↓
JWT Authentication Filter
   |
   ↓
Extract JWT
   |
   ↓
Validate JWT
   |
   ↓
Load UserDetails
   |
   ↓
Create Authentication
   |
   ↓
SecurityContextHolder
   |
   ↓
Spring Security Authorization
   |
   ├── /admin/** → ADMIN
   |
   ├── /user/**  → USER / ADMIN
   |
   └── Other     → Authenticated
```

---

# 30. Complete Request Example

Suppose a user has:

```text
Username: goutham

Roles:
ROLE_USER
ROLE_ADMIN
```

After successful login, the server generates a JWT.

The client sends:

```http
Authorization: Bearer <JWT>
```

Now suppose the client requests:

```text
GET /user/profile
```

The request goes through:

```text
Request
   ↓
JwtAuthenticationFilter
   ↓
JWT Validation
   ↓
Authentication
   ↓
SecurityContextHolder
   ↓
Authorization
   ↓
hasAnyRole("USER", "ADMIN")
   ↓
Access Granted
```

For:

```text
DELETE /admin/users/10
```

Spring Security checks:

```java
.hasRole("ADMIN")
```

Since the user has:

```text
ROLE_ADMIN
```

the request is allowed.

---

# 31. Overall Component Relationship

The major components work together like this:

```text
                    Spring Security
                          |
          ┌───────────────┼────────────────┐
          ↓               ↓                ↓
 Authentication      Authorization      Security
          |               |              Protection
          ↓               ↓
AuthenticationManager  SecurityFilterChain
          |
          ↓
UserDetailsService
          |
          ↓
UserRepository
          |
          ↓
Database
```

JWT adds the stateless authentication mechanism:

```text
Login
 ↓
AuthenticationManager
 ↓
JWT Generated
 ↓
Client
 ↓
Bearer Token
 ↓
JwtAuthenticationFilter
 ↓
SecurityContextHolder
 ↓
Authorization
```

---

# 32. Important Classes to Remember

For interviews, remember these core classes and their responsibilities:

| Class / Component                     | Purpose                                              |
| ------------------------------------- | ---------------------------------------------------- |
| `UserDetailsService`                  | Loads user information                               |
| `UserDetails`                         | Represents authenticated user's security information |
| `PasswordEncoder`                     | Encodes and verifies passwords                       |
| `AuthenticationManager`               | Performs authentication                              |
| JWT Utility                           | Generates and validates JWT                          |
| `OncePerRequestFilter`                | Processes JWT for requests                           |
| `SecurityFilterChain`                 | Configures security rules                            |
| `SecurityContextHolder`               | Stores current authentication                        |
| `GrantedAuthority`                    | Represents user's authority                          |
| `UsernamePasswordAuthenticationToken` | Represents authentication                            |

---

# 33. Final Mental Model

The easiest way to remember the complete implementation is:

```text
LOGIN
  ↓
AuthenticationManager
  ↓
UserDetailsService
  ↓
Database
  ↓
PasswordEncoder
  ↓
Authentication Success
  ↓
Generate JWT
  ↓
Client
```

For every subsequent request:

```text
REQUEST
  ↓
Authorization: Bearer JWT
  ↓
JwtAuthenticationFilter
  ↓
Extract JWT
  ↓
Extract Username
  ↓
Validate JWT
  ↓
Load User
  ↓
Create Authentication
  ↓
SecurityContextHolder
  ↓
Authorization Rules
  ↓
ALLOW / DENY
```

And the authorization rules are:

```text
/auth/**

    ↓
PUBLIC


/admin/**

    ↓
ROLE_ADMIN


/user/**

    ↓
ROLE_USER OR ROLE_ADMIN


Any other endpoint

    ↓
AUTHENTICATED USER
```

---

# 34. One-Line Summary of Each Concept

**Spring Security**
→ Provides authentication, authorization, and security protections.

**Authentication**
→ Verifies who the user is.

**Authorization**
→ Determines what the authenticated user can access.

**JWT**
→ Provides a stateless authentication token.

**UserDetailsService**
→ Loads the user's security information.

**PasswordEncoder**
→ Encodes and verifies passwords.

**AuthenticationManager**
→ Performs authentication.

**JWT Utility**
→ Generates and validates JWTs.

**JwtAuthenticationFilter**
→ Extracts and validates JWTs from incoming requests.

**SecurityContextHolder**
→ Stores the current authenticated user.

**SecurityFilterChain**
→ Defines authentication and authorization rules.

**Role-Based Authorization**
→ Controls access based on roles such as `ROLE_USER` and `ROLE_ADMIN`.

---

# 35. Complete Flow to Remember for Interviews

```text
                  USER
                   |
                   | Username + Password
                   ↓
             /auth/login
                   |
                   ↓
        AuthenticationManager
                   |
                   ↓
        UserDetailsService
                   |
                   ↓
             UserRepository
                   |
                   ↓
              User Database
                   |
                   ↓
          Password Verification
                   |
                   ↓
          Authentication Success
                   |
                   ↓
              Generate JWT
                   |
                   ↓
                 CLIENT
                   |
                   | Bearer JWT
                   ↓
        JwtAuthenticationFilter
                   |
                   ↓
             Validate JWT
                   |
                   ↓
          Create Authentication
                   |
                   ↓
         SecurityContextHolder
                   |
                   ↓
         SecurityFilterChain
                   |
             Authorization
                   |
        ┌──────────┼───────────┐
        ↓          ↓           ↓
    /auth/**   /admin/**    /user/**
      Public      ADMIN     USER/ADMIN
```

The complete concept can therefore be summarized as:

> **Authenticate the user once using username/password, generate a JWT, send that JWT with subsequent requests, validate the JWT through a security filter, store the authenticated user in the SecurityContext, and then use Spring Security authorization rules to determine whether the user can access the requested resource.**
