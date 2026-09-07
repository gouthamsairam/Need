**Spring Boot Security - Comprehensive Interview Questions & Answers**
======================================================================

**Section 1: Fundamentals -- Why, How, and Where**
--------------------------------------------------

### **Q1: Why do we need Spring Security in a Spring Boot application?**

**Answer:Spring Security addresses three fundamental concerns in any application:**

1.  **Authentication -- Verifying the identity of the user ("Who are you?")**

2.  **Authorization -- Determining what an authenticated user is allowed to do ("What can you do?")**

3.  **Protection against common attacks -- CSRF, session fixation, clickjacking, XSS header protection**


**Without Spring Security, you would have to manually write servlet filters, manage session tokens, hash passwords, validate credentials, and protect against exploits -- all of which are error-prone.Real-World Scenario:Imagine you are building an e-commerce application with these endpoints:**

**Endpoint**

**Who should access it**

**GET /products**

**Everyone (public)**

**POST /orders**

**Logged-in customers**

**GET /admin/reports**

**Only admin users**

**DELETE /admin/users/{id}**

**Only super-admin**

**Without Spring Security, you would need to write if-else checks in every controller method to verify the caller. Spring Security centralizes all of this into a configuration layer, keeping your business logic clean.**

### **Q2: How does Spring Security integrate with Spring Boot?**

**Answer:Spring Boot auto-configures Spring Security when the spring-boot-starter-security dependency is on the classpath. The auto-configuration:**

1.  **Creates a default SecurityFilterChain that secures all endpoints.**

2.  **Generates a random password printed in the console logs.**

3.  **Sets the default username to user.**

4.  **Enables form-based login at /login.**

5.  **Enables HTTP Basic authentication.**

6.  **Enables CSRF protection for non-GET requests.**


**Example -- Adding the dependency:**

**org.springframework.boot**

**spring-boot-starter-security**

**After adding this, even a simple GET /hello endpoint returns 401 Unauthorized unless credentials are provided.Customization is done by defining a SecurityFilterChain bean:**

**@Configuration**

**@EnableWebSecurity**

**public class SecurityConfig {**

**@Bean**

**public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {**

**http**

**.authorizeHttpRequests(auth -> auth**

**.requestMatchers("/public/\*\*").permitAll()**

**.requestMatchers("/admin/\*\*").hasRole("ADMIN")**

**.anyRequest().authenticated()**

**)**

**.httpBasic(Customizer.withDefaults());**

**return http.build();**

**}**

**}**

### **Q3: Where is Spring Security typically used in enterprise applications?**

**Answer:Spring Security is used across multiple layers and application types:**

**Use Case**

**Example**

**REST API Security**

**Securing microservice endpoints with JWT tokens**

**Web Application Security**

**Form-based login for customer portals**

**OAuth 2.0 Resource Server**

**Validating access tokens issued by an authorization server like Okta or Keycloak**

**Method-Level Security**

**Restricting service-layer methods based on user roles**

**Inter-Service Communication**

**Propagating security context between microservices**

**Real-World Scenario:In a banking application:**

*   **The web portal uses form-based login with session management.**

*   **The mobile app communicates with REST APIs secured by JWT.**

*   **The admin dashboard uses OAuth 2.0 with role-based access.**

*   **Internal microservices pass tokens between themselves for service-to-service authentication.**


**All of these can be handled by Spring Security with different configurations.**

**Section 2: Authentication vs Authorization**
----------------------------------------------

### **Q4: What is the difference between Authentication and Authorization?**

**Answer:**

**Aspect**

**Authentication**

**Authorization**

**Question**

**"Who are you?"**

**"What are you allowed to do?"**

**When it happens**

**First -- before any access decision**

**Second -- after identity is confirmed**

**Failure response**

**401 Unauthorized**

**403 Forbidden**

**Example**

**Logging in with username/password**

**Checking if the logged-in user has "ADMIN" role**

**Spring Security class**

**AuthenticationManager**

**AccessDecisionManager**

**Real-World Scenario:Think of an office building:**

*   **Authentication = Showing your employee ID badge at the front desk. The guard verifies you work here.**

*   **Authorization = Your badge grants access to floors 1-3, but not the server room on floor 5. You are authenticated (known employee) but not authorized for that restricted area.**


**Example in code:**

**@RestController**

**public class OrderController {**

_**// Authentication required, but any authenticated user can access**_

**@GetMapping("/orders")**

**public List getOrders() {**

**return orderService.findAll();**

**}**

_**// Authentication required AND must have ADMIN role (authorization)**_

**@DeleteMapping("/orders/{id}")**

**@PreAuthorize("hasRole('ADMIN')")**

**public void deleteOrder(@PathVariable Long id) {**

**orderService.delete(id);**

**}**

**}**

**If user "john" (role: USER) calls DELETE /orders/5, he gets 403 Forbidden -- he is authenticated but not authorized.**

### **Q5: Can authorization happen without authentication?**

**Answer:No. Authorization always depends on authentication. You cannot determine what a user is allowed to do if you do not know who the user is. Spring Security enforces this by running the authentication filter before any authorization check.However, you can have authentication without authorization. For example, an endpoint that requires login but does not check roles:**

**.requestMatchers("/profile").authenticated()**  _**// Any logged-in user, no role check**_

**Section 3: Form-Based Authentication**
----------------------------------------

### **Q6: What is form-based authentication and how does Spring Security implement it?**

**Answer:Form-based authentication presents an HTML login page where the user enters a username and password. The form submits a POST request to a login processing URL. Spring Security handles the entire flow.Default behavior:**

*   **Login page: /login (auto-generated by Spring Security)**

*   **Login processing URL: POST /login**

*   **On success: redirects to the originally requested URL**

*   **On failure: redirects back to /login?error**


**Custom configuration:**

**@Bean**

**public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {**

**http**

**.authorizeHttpRequests(auth -> auth**

**.requestMatchers("/login", "/register", "/css/\*\*").permitAll()**

**.anyRequest().authenticated()**

**)**

**.formLogin(form -> form**

**.loginPage("/custom-login")**              _**// Custom login page URL**_

**.loginProcessingUrl("/perform-login")**    _**// Form action URL**_

**.defaultSuccessUrl("/dashboard", true)**   _**// Redirect after successful login**_

**.failureUrl("/custom-login?error=true")**  _**// Redirect on failure**_

**.usernameParameter("email")**              _**// Custom form field name**_

**.passwordParameter("pass")**               _**// Custom form field name**_

**)**

**.logout(logout -> logout**

**.logoutUrl("/perform-logout")**

**.logoutSuccessUrl("/custom-login?logout=true")**

**.deleteCookies("JSESSIONID")**

**);**

**return http.build();**

**}**

**Real-World Scenario:An HR portal where employees log in through a branded login page. After authentication, managers see a dashboard with team reports, while regular employees see only their own profile. The login page is custom-designed to match company branding, not the default Spring Security page.**

### **Q7: What is the difference between form-based login and HTTP Basic authentication?**

**Answer:**

**Aspect**

**Form-Based Login**

**HTTP Basic Authentication**

**How credentials are sent**

**HTML form POST body (username=x&password=y)**

**Authorization header: Basic base64(user:pass)**

**Login UI**

**HTML page (customizable)**

**Browser popup dialog (not customizable)**

**Session**

**Creates a session; subsequent requests use session cookie**

**Typically stateless; credentials sent with every request**

**Logout**

**Supported (invalidate session)**

**Not natively supported (browser caches credentials)**

**Best for**

**Web applications with browser UI**

**REST APIs, service-to-service calls**

**Security**

**Credentials in POST body (not in URL)**

**Credentials in every request header (base64 encoded, not encrypted)**

**Section 4: HTTP Basic Authentication**
----------------------------------------

### **Q8: How does HTTP Basic Authentication work in Spring Security?**

**Answer:HTTP Basic Authentication encodes the username and password in Base64 and sends them in the Authorization header with every request.Flow:**

**1\. Client sends:     GET /api/data  (no credentials)**

**2\. Server responds:  401 Unauthorized**

**WWW-Authenticate: Basic realm="MyApp"**

**3\. Client resends:   GET /api/data**

**Authorization: Basic amF2YTp0ZWNoaWU=    (base64 of "java:techie")**

**4\. Server responds:  200 OK  (if credentials are valid)**

**Configuration:**

**@Bean**

**public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {**

**http**

**.csrf(csrf -> csrf.disable())**

**.authorizeHttpRequests(auth -> auth**

**.anyRequest().authenticated()**

**)**

**.httpBasic(Customizer.withDefaults());**

**return http.build();**

**}**

**Testing with curl:**

**curl -u admin:password123 http://localhost:8080/api/data**

**Important: Base64 is encoding, not encryption. The credentials can be trivially decoded. HTTP Basic should only be used over HTTPS in production.**

### **Q9: In a real-world scenario, when would you choose HTTP Basic over other mechanisms?**

**Answer:HTTP Basic is appropriate for:**

1.  **Internal microservice-to-service communication within a private network where simplicity is valued and TLS is enforced.**

2.  **CLI tools or scripts that call APIs programmatically (e.g., CI/CD pipelines calling a deployment API).**

3.  **Quick prototyping and testing where implementing JWT or OAuth would be overkill.**


**It is not appropriate for:**

*   **Public-facing web applications (no logout, no session management).**

*   **Mobile apps (credentials stored on device for every request).**

*   **APIs exposed to third parties (no token expiry, no scopes).**


**Section 5: JWT (JSON Web Tokens)**
------------------------------------

### **Q10: What is JWT and why is it preferred for securing REST APIs?**

**Answer:JWT (JSON Web Token) is a compact, self-contained token that carries user identity and claims. It consists of three Base64-encoded parts separated by dots:**

**header.payload.signature**

**Structure:**

**eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIiwicm9sZXMiOlsiQURNSU4iXSwiZXhwIjoxNzA3MjQ1NjAwfQ.abc123signature**

**Part**

**Content**

**Example**

**Header**

**Algorithm and token type**

**{"alg": "HS256", "typ": "JWT"}**

**Payload**

**Claims (user data)**

**{"sub": "john", "roles": \["ADMIN"\], "exp": 1707245600}**

**Signature**

**Verification hash**

**HMACSHA256(base64(header) + "." + base64(payload), secret)**

**Why JWT is preferred for REST APIs:**

1.  **Stateless -- The server does not store session data. The token itself contains all the information needed.**

2.  **Scalable -- Any server instance can validate the token without hitting a session store.**

3.  **Cross-domain -- Works across different domains and services (ideal for microservices).**

4.  **Expiry built-in -- The exp claim automatically expires the token.**


### **Q11: How do you implement JWT authentication in Spring Boot?**

**Answer:The typical flow involves:**

**1\. Client sends:   POST /auth/login   { "username": "john", "password": "secret" }**

**2\. Server:         Validates credentials, generates JWT, returns it**

**3\. Client sends:   GET /api/data**

**Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...**

**4\. Server:         Validates JWT signature and expiry, extracts user, processes request**

**Key components:a) JWT utility class:**

**@Component**

**public class JwtUtil {**

**private final String SECRET\_KEY = "my-secret-key-which-should-be-at-least-256-bits";**

**public String generateToken(String username, List roles) {**

**return Jwts.builder()**

**.setSubject(username)**

**.claim("roles", roles)**

**.setIssuedAt(new Date())**

**.setExpiration(new Date(System.currentTimeMillis() + 1000 \* 60 \* 60))**  _**// 1 hour**_

**.signWith(Keys.hmacShaKeyFor(SECRET\_KEY.getBytes()), SignatureAlgorithm.HS256)**

**.compact();**

**}**

**public String extractUsername(String token) {**

**return extractClaims(token).getSubject();**

**}**

**public boolean isTokenValid(String token) {**

**try {**

**extractClaims(token);**

**return true;**

**} catch (JwtException e) {**

**return false;**

**}**

**}**

**private Claims extractClaims(String token) {**

**return Jwts.parserBuilder()**

**.setSigningKey(Keys.hmacShaKeyFor(SECRET\_KEY.getBytes()))**

**.build()**

**.parseClaimsJws(token)**

**.getBody();**

**}**

**}**

**b) JWT filter:**

**@Component**

**public class JwtAuthenticationFilter extends OncePerRequestFilter {**

**@Autowired**

**private JwtUtil jwtUtil;**

**@Autowired**

**private UserDetailsService userDetailsService;**

**@Override**

**protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,**

**FilterChain filterChain) throws ServletException, IOException {**

**String authHeader = request.getHeader("Authorization");**

**if (authHeader != null && authHeader.startsWith("Bearer ")) {**

**String token = authHeader.substring(7);**

**String username = jwtUtil.extractUsername(token);**

**if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {**

**UserDetails userDetails = userDetailsService.loadUserByUsername(username);**

**if (jwtUtil.isTokenValid(token)) {**

**UsernamePasswordAuthenticationToken authToken =**

**new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());**

**SecurityContextHolder.getContext().setAuthentication(authToken);**

**}**

**}**

**}**

**filterChain.doFilter(request, response);**

**}**

**}**

**c) Security configuration:**

**@Bean**

**public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {**

**http**

**.csrf(csrf -> csrf.disable())**

**.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))**

**.authorizeHttpRequests(auth -> auth**

**.requestMatchers("/auth/\*\*").permitAll()**

**.anyRequest().authenticated()**

**)**

**.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);**

**return http.build();**

**}**

### **Q12: What happens if a JWT token is stolen? How do you handle token revocation?**

**Answer:Since JWTs are stateless and self-contained, the server cannot "revoke" a token once issued -- it remains valid until it expires. This is a known trade-off.Mitigation strategies:**

1.  **Short expiry times -- Set tokens to expire in 15-30 minutes.**

2.  **Refresh tokens -- Issue a short-lived access token and a longer-lived refresh token. The refresh token is stored securely and used to obtain new access tokens.**

3.  **Token blacklist -- Maintain a Redis/database store of revoked token IDs (the jti claim). Check this store in the JWT filter.**

4.  **Rotate signing keys -- Changing the secret key invalidates all existing tokens.**


**Real-World Scenario:A banking app issues a 15-minute access token. When the user logs out, the token's jti is added to a Redis blacklist with a TTL matching the token's remaining lifetime. The JWT filter checks Redis before granting access.**

**Section 6: OAuth 2.0 & OpenID Connect**
-----------------------------------------

### **Q13: What is OAuth 2.0 and how does it differ from simple authentication?**

**Answer:OAuth 2.0 is an authorization framework that allows a third-party application to access a user's resources without exposing the user's credentials. It is not an authentication protocol by itself.Key difference:**

**Aspect**

**Simple Authentication**

**OAuth 2.0**

**Purpose**

**Verify user identity**

**Grant limited access to resources**

**Credentials**

**User gives password directly to the app**

**User authenticates with the authorization server; app receives a token**

**Example**

**Logging into your email**

**Allowing a photo printing app to access your Google Photos without giving it your Google password**

**OAuth 2.0 Roles:**

**Role**

**Description**

**Example**

**Resource Owner**

**The user who owns the data**

**You (your Google account)**

**Client**

**The app requesting access**

**A photo printing website**

**Authorization Server**

**Issues tokens after user consents**

**Google's OAuth server**

**Resource Server**

**Hosts the protected data**

**Google Photos API**

**Authorization Code Flow (most common for web apps):**

**1\. User clicks "Login with Google" on your app**

**2\. Browser redirects to Google's authorization endpoint**

**3\. User logs in to Google and grants consent**

**4\. Google redirects back to your app with an authorization code**

**5\. Your app exchanges the code for an access token (server-to-server)**

**6\. Your app uses the access token to call Google APIs on behalf of the user**

### **Q14: What is OpenID Connect and how does it relate to OAuth 2.0?**

**Answer:OpenID Connect (OIDC) is a thin identity layer built on top of OAuth 2.0. While OAuth 2.0 only handles authorization (access tokens), OIDC adds authentication by introducing an ID Token.**

**Feature**

**OAuth 2.0**

**OpenID Connect**

**Purpose**

**Authorization (access to resources)**

**Authentication (user identity)**

**Token**

**Access Token**

**Access Token + ID Token (JWT)**

**User info**

**Not standardized**

**Standardized claims (sub, email, name)**

**Use case**

**"Allow app X to read my photos"**

**"Log me in using my Google account"**

**Spring Boot configuration for OIDC login with Google:**

**spring:**

**security:**

**oauth2:**

**client:**

**registration:**

**google:**

**client-id: your-client-id**

**client-secret: your-client-secret**

**scope: openid, profile, email**

**@Bean**

**public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {**

**http**

**.authorizeHttpRequests(auth -> auth**

**.requestMatchers("/", "/login").permitAll()**

**.anyRequest().authenticated()**

**)**

**.oauth2Login(Customizer.withDefaults());**

**return http.build();**

**}**

**With just this configuration, Spring Boot handles the entire OIDC flow: redirect to Google, handle the callback, exchange the code for tokens, and create a security context.**

### **Q15: What is the difference between an Access Token and a Refresh Token in OAuth 2.0?**

**Answer:**

**Aspect**

**Access Token**

**Refresh Token**

**Purpose**

**Grants access to protected resources**

**Obtains a new access token without re-authentication**

**Lifetime**

**Short (15 minutes to 1 hour)**

**Long (days to months)**

**Sent to**

**Resource server (API)**

**Authorization server only**

**Storage**

**In-memory or short-lived cookie**

**Secure HTTP-only cookie or server-side storage**

**If stolen**

**Limited damage (expires quickly)**

**Serious risk (can generate new access tokens)**

**Flow:**

**1\. User logs in         --> Gets Access Token (15 min) + Refresh Token (7 days)**

**2\. User calls API       --> Sends Access Token in header**

**3\. Access Token expires  --> Client sends Refresh Token to auth server**

**4\. Auth server validates --> Issues new Access Token (15 min)**

**5\. Refresh Token expires --> User must log in again**

**Section 7: Role-Based Access Control (RBAC)**
-----------------------------------------------

### **Q16: How do you implement Role-Based Access Control in Spring Security?**

**Answer:RBAC restricts access based on roles assigned to users. In Spring Security, roles are represented as GrantedAuthority objects, typically prefixed with ROLE\_.Step 1 -- Define users with roles:**

**@Bean**

**public UserDetailsService userDetailsService(PasswordEncoder encoder) {**

**UserDetails admin = User.builder()**

**.username("admin")**

**.password(encoder.encode("admin123"))**

**.roles("ADMIN")**                           _**// Internally stored as ROLE\_ADMIN**_

**.build();**

**UserDetails manager = User.builder()**

**.username("manager")**

**.password(encoder.encode("mgr123"))**

**.roles("MANAGER")**

**.build();**

**UserDetails user = User.builder()**

**.username("john")**

**.password(encoder.encode("john123"))**

**.roles("USER")**

**.build();**

**return new InMemoryUserDetailsManager(admin, manager, user);**

**}**

**Step 2 -- Apply role-based rules:**

**@Bean**

**public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {**

**http**

**.authorizeHttpRequests(auth -> auth**

**.requestMatchers("/admin/\*\*").hasRole("ADMIN")**

**.requestMatchers("/reports/\*\*").hasAnyRole("ADMIN", "MANAGER")**

**.requestMatchers("/profile/\*\*").hasRole("USER")**

**.requestMatchers("/public/\*\*").permitAll()**

**.anyRequest().authenticated()**

**)**

**.httpBasic(Customizer.withDefaults());**

**return http.build();**

**}**

**Result:**

**User**

**Role**

**/admin/users**

**/reports/sales**

**/profile/me**

**/public/home**

**admin**

**ADMIN**

**200**

**200**

**403**

**200**

**manager**

**MANAGER**

**403**

**200**

**403**

**200**

**john**

**USER**

**403**

**403**

**200**

**200**

**(anonymous)**

**\--**

**401**

**401**

**401**

**200**

**Real-World Scenario:A hospital management system:**

*   **ADMIN -- Can manage users, view all records, configure system settings.**

*   **DOCTOR -- Can view and update patient records assigned to them.**

*   **NURSE -- Can view patient records but cannot update diagnoses.**

*   **RECEPTIONIST -- Can only register new patients and schedule appointments.**


### **Q17: What is the difference between hasRole() and hasAuthority() in Spring Security?**

**Answer:**

**Method**

**Prefix**

**Example**

**hasRole("ADMIN")**

**Automatically prepends ROLE\_**

**Checks for ROLE\_ADMIN**

**hasAuthority("ROLE\_ADMIN")**

**No prefix added**

**Checks for exactly ROLE\_ADMIN**

**hasAuthority("DELETE\_PRIVILEGE")**

**No prefix added**

**Checks for exactly DELETE\_PRIVILEGE**

**hasRole() is a convenience method that adds the ROLE\_ prefix. hasAuthority() is more flexible and can be used for fine-grained permissions beyond roles.Example -- Fine-grained permissions:**

_**// A user can have both a role and specific permissions**_

**UserDetails admin = User.builder()**

**.username("admin")**

**.password(encoder.encode("admin123"))**

**.authorities("ROLE\_ADMIN", "READ\_PRIVILEGE", "WRITE\_PRIVILEGE", "DELETE\_PRIVILEGE")**

**.build();**

**UserDetails editor = User.builder()**

**.username("editor")**

**.password(encoder.encode("editor123"))**

**.authorities("ROLE\_EDITOR", "READ\_PRIVILEGE", "WRITE\_PRIVILEGE")**

**.build();**

**.requestMatchers(HttpMethod.DELETE, "/api/\*\*").hasAuthority("DELETE\_PRIVILEGE")**

**.requestMatchers(HttpMethod.PUT, "/api/\*\*").hasAuthority("WRITE\_PRIVILEGE")**

**.requestMatchers(HttpMethod.GET, "/api/\*\*").hasAuthority("READ\_PRIVILEGE")**

**Section 8: Method Security**
------------------------------

### **Q18: What is method-level security and how do @PreAuthorize, @PostAuthorize, and @Secured differ?**

**Answer:Method-level security applies access control directly on service or controller methods, rather than at the URL level.Enable it first:**

**@Configuration**

**@EnableMethodSecurity**

**public class MethodSecurityConfig {**

**}**

**Comparison:**

**Annotation**

**When it runs**

**SpEL support**

**Example**

**@PreAuthorize**

**Before method execution**

**Yes**

**@PreAuthorize("hasRole('ADMIN') and #id > 0")**

**@PostAuthorize**

**After method execution**

**Yes**

**@PostAuthorize("returnObject.owner == authentication.name")**

**@Secured**

**Before method execution**

**No**

**@Secured({"ROLE\_ADMIN", "ROLE\_MANAGER"})**

**Example -- @PreAuthorize:**

**@Service**

**public class OrderService {**

**@PreAuthorize("hasRole('ADMIN')")**

**public void deleteOrder(Long orderId) {**

**orderRepository.deleteById(orderId);**

**}**

**@PreAuthorize("hasRole('ADMIN') or #username == authentication.name")**

**public List getOrdersByUser(String username) {**

**return orderRepository.findByUsername(username);**

**}**

**}**

**In the second method, #username refers to the method parameter. A user can view their own orders, but only an admin can view anyone's orders.Example -- @PostAuthorize:**

**@PostAuthorize("returnObject.createdBy == authentication.name or hasRole('ADMIN')")**

**public Document getDocument(Long documentId) {**

**return documentRepository.findById(documentId).orElseThrow();**

**}**

**The method executes first. After execution, Spring checks if the returned document was created by the current user. If not (and the user is not an admin), a 403 Forbidden is thrown.Real-World Scenario:In a multi-tenant SaaS application, a user should only be able to access data belonging to their own tenant. @PreAuthorize can check #tenantId == authentication.principal.tenantId before the query even runs.**

### **Q19: When would you use method-level security over URL-based security?**

**Answer:**

**Use URL-based security when...**

**Use method-level security when...**

**Security rules are based on URL patterns**

**Security rules depend on method parameters or return values**

**Rules are broad (whole path segments)**

**Rules are granular (specific business logic)**

**You want all security rules in one place**

**Security logic is tied to service-layer behavior**

**Example: /admin/\*\* requires ADMIN role**

**Example: A user can only edit their own profile**

**Best practice: Use both together. URL-based security as the first line of defense, method-level security for fine-grained business rules.**

**Section 9: CSRF Protection**
------------------------------

### **Q20: What is CSRF and how does Spring Security protect against it?**

**Answer:CSRF (Cross-Site Request Forgery) is an attack where a malicious website tricks a user's browser into making an unwanted request to a site where the user is already authenticated.Attack scenario:**

**1\. User logs into their banking app (bank.com). Browser stores session cookie.**

**2\. User visits a malicious site (evil.com) in another tab.**

**3\. evil.com contains: ![](https://bank.com/transfer?to=attacker&amount=10000)**

**4\. Browser automatically attaches the bank.com session cookie to this request.**

**5\. The bank processes the transfer because it sees a valid session.**

**Spring Security's defense -- Synchronizer Token Pattern:Spring Security generates a unique, unpredictable CSRF token per session. This token must be included in every state-changing request (POST, PUT, DELETE). Since the malicious site cannot read this token, it cannot forge a valid request.How it works in a form:**

    

    

    

**Transfer**

**Spring Security automatically injects the \_csrf hidden field when using Thymeleaf or JSP.**

### **Q21: Why do we typically disable CSRF for REST APIs?**

**Answer:CSRF attacks exploit browser-based session cookies. REST APIs that use stateless authentication (JWT in Authorization header) are not vulnerable to CSRF because:**

1.  **The browser does not automatically attach the Authorization header to cross-origin requests (unlike cookies).**

2.  **The attacker's malicious site has no way to read or set the Authorization header.**


**http.csrf(csrf -> csrf.disable())**  _**// Safe for stateless JWT-based APIs**_

**However, if your REST API uses cookies for authentication (e.g., session cookies), CSRF protection should remain enabled.Rule of thumb:**

**Authentication method**

**CSRF needed?**

**Session cookies**

**Yes**

**JWT in Authorization header**

**No**

**HTTP Basic (browser popup)**

**Depends on context**

**OAuth 2.0 bearer tokens**

**No**

**Section 10: Password Encoding (BCrypt)**
------------------------------------------

### **Q22: Why must passwords be encoded, and why is BCrypt the recommended encoder?**

**Answer:Storing passwords in plain text is the most dangerous security vulnerability. If the database is breached, every user's password is exposed.Types of password handling:**

**Approach**

**Storage Example**

**Security Level**

**Plain text**

**password123**

**None -- attacker sees the password directly**

**MD5 hash**

**482c811da5d5b4bc6d497ffa98491e38**

**Weak -- can be reversed with rainbow tables**

**SHA-256 hash**

**ef92b778bafe...**

**Better, but still vulnerable to rainbow tables**

**BCrypt hash**

**$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy**

**Strong -- includes salt and is computationally expensive**

**Why BCrypt is preferred:**

1.  **Built-in salt -- Each hash includes a random salt, so identical passwords produce different hashes.**

2.  **Adaptive cost factor -- The $2a$10$ means 2^10 (1024) rounds of hashing. You can increase the cost factor as hardware gets faster.**

3.  **Designed to be slow -- Intentionally takes ~100ms per hash, making brute-force attacks impractical (billions of years to crack).**


**Spring Security configuration:**

**@Bean**

**public PasswordEncoder passwordEncoder() {**

**return new BCryptPasswordEncoder(12);**  _**// Cost factor of 12 (2^12 = 4096 rounds)**_

**}**

**Usage when registering a user:**

**@PostMapping("/register")**

**public String register(@RequestBody UserDto dto) {**

**User user = new User();**

**user.setUsername(dto.getUsername());**

**user.setPassword(passwordEncoder.encode(dto.getPassword()));**  _**// Hash before saving**_

**userRepository.save(user);**

**return "User registered successfully";**

**}**

**BCrypt hash anatomy:**

**$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy**

**|  |  |                     |**

**|  |  +-- 22-char salt      +-- 31-char hash**

**|  +-- cost factor (10)**

**+-- algorithm version (2a)**

### **Q23: What happens if you use NoOpPasswordEncoder in production?**

**Answer:NoOpPasswordEncoder performs zero encoding -- it stores and compares passwords as plain text. It is marked as @Deprecated in Spring Security.Risks:**

1.  **Passwords stored in the database are fully readable.**

2.  **A database breach exposes every user's actual password.**

3.  **Users who reuse passwords across sites are compromised everywhere.**

4.  **Fails security audits and compliance requirements (PCI-DSS, HIPAA, SOC 2).**


**It should only be used in unit tests or learning exercises, never in production.**

**Section 11: Security Filters**
--------------------------------

### **Q24: How does the Spring Security filter chain work?**

**Answer:Spring Security operates as a chain of servlet filters that process every HTTP request before it reaches your controllers. Each filter handles a specific security concern.Request flow:**

**HTTP Request**

**|**

**v**

**\[DelegatingFilterProxy\]           -- Entry point from Servlet container**

**|**

**v**

**\[FilterChainProxy\]                -- Spring Security's main filter**

**|**

**v**

**\[SecurityContextPersistenceFilter\]  -- Loads/saves SecurityContext from session**

**|**

**v**

**\[CsrfFilter\]                       -- Validates CSRF token**

**|**

**v**

**\[UsernamePasswordAuthenticationFilter\]  -- Processes form login**

**|**

**v**

**\[BasicAuthenticationFilter\]         -- Processes HTTP Basic auth**

**|**

**v**

**\[JwtAuthenticationFilter\]           -- Your custom JWT filter (if added)**

**|**

**v**

**\[ExceptionTranslationFilter\]       -- Converts security exceptions to HTTP responses**

**|**

**v**

**\[FilterSecurityInterceptor\]        -- Final authorization check**

**|**

**v**

**Your Controller**

**Adding a custom filter:**

**http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);**

**This inserts your JWT filter before the username/password filter in the chain.**

### **Q25: How would you create a custom security filter?**

**Answer:Extend OncePerRequestFilter to ensure the filter runs exactly once per request:**

**@Component**

**public class ApiKeyAuthFilter extends OncePerRequestFilter {**

**private static final String API\_KEY\_HEADER = "X-API-KEY";**

**private static final String VALID\_API\_KEY = "my-secret-api-key-12345";**

**@Override**

**protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,**

**FilterChain filterChain) throws ServletException, IOException {**

**String apiKey = request.getHeader(API\_KEY\_HEADER);**

**if (VALID\_API\_KEY.equals(apiKey)) {**

_**// Create an authentication token and set it in the context**_

**UsernamePasswordAuthenticationToken auth =**

**new UsernamePasswordAuthenticationToken("api-client", null, List.of(new SimpleGrantedAuthority("ROLE\_API")));**

**SecurityContextHolder.getContext().setAuthentication(auth);**

**filterChain.doFilter(request, response);**

**} else {**

**response.setStatus(HttpServletResponse.SC\_UNAUTHORIZED);**

**response.getWriter().write("Invalid API Key");**

**}**

**}**

**@Override**

**protected boolean shouldNotFilter(HttpServletRequest request) {**

_**// Skip this filter for public endpoints**_

**return request.getRequestURI().startsWith("/public/");**

**}**

**}**

**Real-World Scenario:A SaaS platform exposes APIs to external partners. Each partner receives a unique API key. The custom filter validates the key, identifies the partner, and attaches the appropriate roles to the security context.**

**Section 12: Custom Authentication Providers**
-----------------------------------------------

### **Q26: What is a custom AuthenticationProvider and when would you use one?**

**Answer:An AuthenticationProvider is the component that performs the actual authentication logic. Spring Security calls it with an Authentication object and expects a fully authenticated Authentication object in return (or an exception if authentication fails).When to use it:**

*   **Authenticating against an external system (LDAP, Active Directory, third-party API).**

*   **Multi-factor authentication.**

*   **Custom credential validation logic (e.g., OTP, biometric).**


**Example -- Authenticating against an external REST API:**

**@Component**

**public class ExternalApiAuthenticationProvider implements AuthenticationProvider {**

**@Autowired**

**private RestTemplate restTemplate;**

**@Override**

**public Authentication authenticate(Authentication authentication) throws AuthenticationException {**

**String username = authentication.getName();**

**String password = authentication.getCredentials().toString();**

_**// Call external authentication service**_

**ExternalAuthRequest request = new ExternalAuthRequest(username, password);**

**try {**

**ExternalAuthResponse response = restTemplate.postForObject(**

**"https://auth-service.internal/validate", request, ExternalAuthResponse.class);**

**if (response != null && response.isValid()) {**

**List authorities = response.getRoles().stream()**

**.map(role -> new SimpleGrantedAuthority("ROLE\_" + role))**

**.collect(Collectors.toList());**

**return new UsernamePasswordAuthenticationToken(username, password, authorities);**

**}**

**} catch (Exception e) {**

**throw new BadCredentialsException("External authentication service unavailable");**

**}**

**throw new BadCredentialsException("Invalid username or password");**

**}**

**@Override**

**public boolean supports(Class authentication) {**

**return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);**

**}**

**}**

**Register it:**

**@Bean**

**public AuthenticationManager authenticationManager(ExternalApiAuthenticationProvider provider) {**

**return new ProviderManager(provider);**

**}**

**Real-World Scenario:A company migrating from a legacy system still authenticates users against an old LDAP directory. The custom AuthenticationProvider connects to LDAP, validates credentials, and maps LDAP groups to Spring Security roles.**

**Section 13: Remember Me**
---------------------------

### **Q27: How does the "Remember Me" feature work in Spring Security?**

**Answer:"Remember Me" allows users to stay authenticated across browser sessions without re-entering credentials. When enabled, Spring Security sets a persistent cookie that survives browser restarts.Two implementations:**

**Type**

**Storage**

**Security**

**Simple hash-based**

**Cookie contains base64(username:expiryTime:md5Hex(username:expiryTime:password:key))**

**Less secure -- if password changes, token is invalidated**

**Persistent token**

**Cookie contains a series identifier + random token; mappings stored in the database**

**More secure -- supports token rotation and theft detection**

**Simple configuration:**

**@Bean**

**public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {**

**http**

**.formLogin(Customizer.withDefaults())**

**.rememberMe(remember -> remember**

**.key("uniqueAndSecretKey")**            _**// Key for hashing**_

**.tokenValiditySeconds(7 \* 24 \* 3600)**  _**// 7 days**_

**.rememberMeParameter("remember-me")**   _**// HTML checkbox name**_

**);**

**return http.build();**

**}**

**Persistent token configuration (production-grade):**

**@Bean**

**public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {**

**http**

**.formLogin(Customizer.withDefaults())**

**.rememberMe(remember -> remember**

**.tokenRepository(persistentTokenRepository())**

**.tokenValiditySeconds(7 \* 24 \* 3600)**

**);**

**return http.build();**

**}**

**@Bean**

**public PersistentTokenRepository persistentTokenRepository() {**

**JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();**

**tokenRepository.setDataSource(dataSource);**

**return tokenRepository;**

**}**

**This requires a database table:**

**CREATE TABLE persistent\_logins (**

**username  VARCHAR(64) NOT NULL,**

**series    VARCHAR(64) PRIMARY KEY,**

**token     VARCHAR(64) NOT NULL,**

**last\_used TIMESTAMP   NOT NULL**

**);**

**Real-World Scenario:An email client like a webmail application. Users check "Remember me" so they don't have to log in every time they open the browser. If a token theft is detected (the series matches but the token doesn't), all remember-me tokens for that user are invalidated.**

**Section 14: Session Management**
----------------------------------

### **Q28: How does Spring Security manage HTTP sessions?**

**Answer:Spring Security uses the HTTP session to store the SecurityContext (which contains the authenticated user's details). Session management controls how sessions are created, tracked, and limited.Session creation policies:**

**http.sessionManagement(session -> session**

**.sessionCreationPolicy(SessionCreationPolicy.IF\_REQUIRED)**  _**// default**_

**);**

**Policy**

**Behavior**

**Use Case**

**ALWAYS**

**Always creates a session**

**Traditional web apps**

**IF\_REQUIRED**

**Creates a session only when needed (default)**

**Most web apps**

**NEVER**

**Never creates a session, but uses one if it already exists**

**Hybrid apps**

**STATELESS**

**Never creates or uses a session**

**REST APIs with JWT**

### **Q29: How do you prevent concurrent sessions (a user logging in from multiple devices)?**

**Answer:**

**http.sessionManagement(session -> session**

**.maximumSessions(1)**                          _**// Only one session allowed per user**_

**.maxSessionsPreventsLogin(false)**             _**// New login expires old session**_

_**// .maxSessionsPreventsLogin(true)           // New login is rejected**_

**.expiredUrl("/login?expired=true")**           _**// Redirect when session is expired**_

**);**

**You also need to register a listener:**

**@Bean**

**public HttpSessionEventPublisher httpSessionEventPublisher() {**

**return new HttpSessionEventPublisher();**

**}**

**Behavior with maxSessionsPreventsLogin(false) (default):**

**1\. User logs in from Laptop     --> Session A created**

**2\. User logs in from Phone      --> Session B created, Session A is expired**

**3\. User tries to use Laptop     --> Redirected to /login?expired=true**

**Behavior with maxSessionsPreventsLogin(true):**

**1\. User logs in from Laptop     --> Session A created**

**2\. User tries to log in from Phone --> Login rejected (error: "Maximum sessions reached")**

**Real-World Scenario:A video streaming service (like Netflix) limits concurrent sessions based on the subscription plan:**

*   **Basic plan: maximumSessions(1) -- only one device at a time.**

*   **Standard plan: maximumSessions(3).**

*   **Premium plan: maximumSessions(5).**


### **Q30: What is session fixation and how does Spring Security prevent it?**

**Answer:Session fixation is an attack where the attacker sets a known session ID for the victim. When the victim logs in, the session ID doesn't change, and the attacker can use the pre-set session ID to hijack the authenticated session.Attack flow:**

**1\. Attacker visits the app, gets session ID: ABC123**

**2\. Attacker sends victim a link: https://app.com/login?JSESSIONID=ABC123**

**3\. Victim clicks the link and logs in**

**4\. The session ID remains ABC123**

**5\. Attacker uses session ID ABC123 to access victim's authenticated session**

**Spring Security's protection:**

**http.sessionManagement(session -> session**

**.sessionFixation().migrateSession()**   _**// Default -- creates new session, copies attributes**_

_**// .sessionFixation().newSession()    // Creates new session, does NOT copy attributes**_

_**// .sessionFixation().changeSessionId()  // Changes session ID (Servlet 3.1+)**_

_**// .sessionFixation().none()          // DANGEROUS -- no protection**_

**);**

**With migrateSession() (default), after the user logs in, Spring Security creates a completely new session with a new ID and copies all attributes from the old session. The attacker's pre-set session ID becomes useless.**

**Section 15: Scenario-Based Questions**
----------------------------------------

### **Q31: You are building a microservices architecture with 5 services. How would you implement security?**

**Answer:Recommended approach -- API Gateway + JWT + OAuth 2.0:**

**\[Authorization Server\]**

**(Keycloak / Okta)**

**|**

**Issues JWT**

**|**

**\[Client\] --> \[API Gateway\] --> \[Service A\] --> \[Service B\]**

**|                     |**

**|-- Validates JWT     |-- Passes JWT in header**

**|-- Rate limiting     |-- Validates JWT locally**

**|-- CORS handling**

1.  **Authorization Server (Keycloak/Okta) -- Handles user login, issues JWT tokens.**

2.  **API Gateway -- Single entry point. Validates JWT, routes requests, handles CORS.**

3.  **Each microservice -- Configured as an OAuth 2.0 Resource Server. Validates JWT signature locally (no call to auth server needed).**

4.  **Service-to-service calls -- Pass the JWT token in the Authorization header.**

5.  **Scopes and roles -- Encoded in the JWT claims; each service checks relevant roles.**


### **Q32: A penetration tester found that your API is vulnerable to brute-force login attacks. How would you fix this?**

**Answer:**

1.  **Rate limiting -- Limit login attempts per IP/username (e.g., 5 attempts per minute).**

2.  **Account lockout -- Lock the account after N failed attempts.**


**@Component**

**public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {**

**@Autowired**

**private LoginAttemptService loginAttemptService;**

**@Override**

**public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,**

**AuthenticationException exception) throws IOException {**

**String username = request.getParameter("username");**

**loginAttemptService.recordFailedAttempt(username);**

**if (loginAttemptService.isBlocked(username)) {**

**response.sendError(HttpServletResponse.SC\_FORBIDDEN, "Account locked for 15 minutes");**

**} else {**

**response.sendError(HttpServletResponse.SC\_UNAUTHORIZED, "Invalid credentials");**

**}**

**}**

**}**

1.  **CAPTCHA -- Add a CAPTCHA after 3 failed attempts.**

2.  **Exponential backoff -- Increase delay between allowed attempts (1s, 2s, 4s, 8s...).**

3.  **Multi-factor authentication -- Even if the password is guessed, a second factor is required.**


### **Q33: Your application needs to support both internal employees (LDAP) and external customers (database). How would you configure authentication?**

**Answer:Use multiple AuthenticationProvider instances. Spring Security tries each provider in order:**

**@Configuration**

**@EnableWebSecurity**

**public class SecurityConfig {**

**@Bean**

**public AuthenticationManager authenticationManager(**

**LdapAuthenticationProvider ldapProvider,**

**DaoAuthenticationProvider dbProvider) {**

**return new ProviderManager(List.of(ldapProvider, dbProvider));**

**}**

**@Bean**

**public LdapAuthenticationProvider ldapProvider() {**

_**// Configure LDAP authentication for employees**_

**LdapAuthenticator authenticator = new BindAuthenticator(contextSource());**

**authenticator.setUserDnPatterns(new String\[\]{"uid={0},ou=employees"});**

**return new LdapAuthenticationProvider(authenticator);**

**}**

**@Bean**

**public DaoAuthenticationProvider dbProvider(UserDetailsService userDetailsService,**

**PasswordEncoder encoder) {**

_**// Configure database authentication for customers**_

**DaoAuthenticationProvider provider = new DaoAuthenticationProvider();**

**provider.setUserDetailsService(userDetailsService);**

**provider.setPasswordEncoder(encoder);**

**return provider;**

**}**

**}**

**Flow:**

**1\. User submits credentials**

**2\. Spring tries LdapAuthenticationProvider**

**- If LDAP validates --> authenticated as employee**

**- If LDAP rejects  --> move to next provider**

**3\. Spring tries DaoAuthenticationProvider**

**- If DB validates  --> authenticated as customer**

**- If DB rejects    --> authentication fails (401)**

### **Q34: How would you secure sensitive data in application.properties such as database passwords and JWT secret keys?**

**Answer:**

1.  **Environment variables:**


**spring.datasource.password=${DB\_PASSWORD}**

**jwt.secret=${JWT\_SECRET\_KEY}**

1.  **Spring Cloud Vault -- Fetch secrets from HashiCorp Vault at startup.**

2.  **Jasypt encryption (as used in your project):**


**database.pass\_word=ENC(bKQGso95v8HxHL7uW1q8F7SbWTgLEs7o...)**

1.  **AWS Secrets Manager / Azure Key Vault -- Cloud-native secret storage.**

2.  **Never commit secrets to version control. Use .gitignore for local .env files.**


**This covers all the core topics of Spring Boot Security comprehensively. Each section builds on the previous one, progressing from basic concepts to production-grade implementations with real-world scenarios and code examples.**

**Encoding vs Encryption — Clear Differences**
==============================================

**One-Line Answer**
-------------------

**\> Encoding transforms data into a different format for compatibility (no secret).> Encryption transforms data into an unreadable form for security (requires a secret key).**

**Side-by-Side Comparison**
---------------------------

**Feature**

**Encoding**

**Encryption**

**Purpose**

**Data format conversion (compatibility)**

**Data protection (confidentiality)**

**Security**

**NOT secure — anyone can decode**

**Secure — only key holders can decrypt**

**Key required?**

**No key needed**

**Yes, requires a secret key**

**Reversible?**

**Yes, by anyone (public algorithm)**

**Yes, but only with the correct key**

**Goal**

**"Make data usable in a different system"**

**"Make data unreadable to unauthorized people"**

**Example**

**Base64, URL encoding, ASCII, Unicode, UTF-8**

**AES, RSA, DES, SHA (hashing is one-way)**

**How Each Works**
------------------

### **Encoding (No Secret)**

**Original:  "Hello World"**

**↓ Encode (Base64)**

**Encoded:   "SGVsbG8gV29ybGQ="**

**↓ Decode (Base64) — Anyone can do this**

**Decoded:   "Hello World"**

**No key, no secret. The algorithm is public. Anyone who knows it's Base64 can decode it instantly.**

### **Encryption (Requires Secret Key)**

**Original:    "Hello World"**

**↓ Encrypt (AES with key = "MySecretKey123")**

**Encrypted:   "3q2+7w==aB9xZ..."  (unreadable gibberish)**

**↓ Decrypt (AES with key = "MySecretKey123")**

**Decrypted:   "Hello World"**

**↓ Decrypt (AES with WRONG key = "WrongKey")**

**Result:      ERROR or garbage data**

**Without the correct key, the data is useless.**

**Java Code Examples**
----------------------

### **Encoding (Base64)**

**import java.util.Base64;**

**String original = "Hello World";**

_**// Encode**_

**String encoded = Base64.getEncoder().encodeToString(original.getBytes());**

**System.out.println(encoded);**  _**// SGVsbG8gV29ybGQ=**_

_**// Decode — anyone can do this, no key needed**_

**String decoded = new String(Base64.getDecoder().decode(encoded));**

**System.out.println(decoded);**  _**// Hello World**_

### **Encryption (AES)**

**import javax.crypto.Cipher;**

**import javax.crypto.spec.SecretKeySpec;**

**String original = "Hello World";**

**String secretKey = "MySecretKey12345";** _**// 16 bytes for AES-128**_

**SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");**

_**// Encrypt — requires the secret key**_

**Cipher cipher = Cipher.getInstance("AES");**

**cipher.init(Cipher.ENCRYPT\_MODE, keySpec);**

**byte\[\] encrypted = cipher.doFinal(original.getBytes());**

**System.out.println(Base64.getEncoder().encodeToString(encrypted));**  _**// gibberish**_

_**// Decrypt — requires the SAME secret key**_

**cipher.init(Cipher.DECRYPT\_MODE, keySpec);**

**byte\[\] decrypted = cipher.doFinal(encrypted);**

**System.out.println(new String(decrypted));**  _**// Hello World**_

**Real-World Scenarios**
------------------------

### **When to use Encoding**

**Scenario**

**Why Encoding**

**Sending binary data (image) in a JSON API**

**Base64 encodes bytes into text characters**

**Putting special characters in a URL**

**URL encoding: hello world → hello world**

**Storing text in different languages**

**UTF-8 encoding handles all characters**

**Sending email attachments (MIME)**

**Base64 encodes binary files into text for email**

### **When to use Encryption**

**Scenario**

**Why Encryption**

**Storing user passwords in a database**

**So hackers can't read them if DB is breached**

**Sending credit card numbers over the internet**

**HTTPS/TLS encrypts data in transit**

**Storing sensitive files in S3**

**AES-256 encryption at rest**

**Communication between microservices**

**mTLS encrypts the traffic**

**Common Mistake in Interviews**
--------------------------------

**\> "We Base64 encode the password before storing it in the database for security."This is WRONG. Base64 is encoding, not encryption. Anyone can decode it. Passwords should be hashed (bcrypt, argon2) — which is a one-way transformation that cannot be reversed.**

**Encoding:    reversible, no key       → NOT for security**

**Encryption:  reversible, needs key    → For confidentiality**

**Hashing:     NOT reversible, no key   → For integrity/passwords**

**Bonus: Encoding vs Encryption vs Hashing**
--------------------------------------------

**Encoding**

**Encryption**

**Hashing**

**Reversible?**

**Yes (anyone)**

**Yes (with key)**

**No (one-way)**

**Key needed?**

**No**

**Yes**

**No**

**Purpose**

**Format conversion**

**Confidentiality**

**Integrity / verification**

**Example**

**Base64, UTF-8**

**AES, RSA**

**SHA-256, bcrypt**

**Use case**

**Data transfer**

**Protect secrets**

**Password storage, checksums**

**Simple analogy:**

*   **Encoding = Translating English to Spanish (anyone who knows Spanish can understand it)**

*   **Encryption = Writing a letter in a secret code only you and your friend know (need the code book / key)**

*   **Hashing = Turning a document into a fingerprint (you can't recreate the document from the fingerprint, but you can verify it matches)**


**Can you explain what is DelegatingFilterProxy in spring security?**

**DelegatingFilterProxy in Spring Security**
============================================

**One-Line Answer**
-------------------

**\> DelegatingFilterProxy is a bridge between the Servlet container's filter chain and Spring's application context — it delegates the actual security work to a Spring-managed bean called FilterChainProxy.**

**The Problem It Solves**
-------------------------

**Servlet containers (Tomcat, Jetty) manage Servlet Filters. But Servlet filters are created by the container, NOT by Spring. So they don't have access to Spring beans (like your @Service, @Repository, etc.).Problem: Spring Security filters need access to Spring beans (UserDetailsService, AuthenticationManager, etc.), but the Servlet container doesn't know about Spring's ApplicationContext.Solution: DelegatingFilterProxy — a Servlet filter that the container knows about, which internally delegates to a Spring bean that the container doesn't know about.**

**How It Works Internally (Step by Step)**
------------------------------------------

**HTTP Request from Client**

**│**

**▼**

**┌─────────────────────────────────────────────────┐**

**│           SERVLET CONTAINER (Tomcat)             │**

**│                                                  │**

**│  Servlet Filter Chain:                           │**

**│  ┌─────────────────────┐                         │**

**│  │ CorsFilter          │  ← Regular servlet filter│**

**│  └─────────┬───────────┘                         │**

**│            ▼                                     │**

**│  ┌─────────────────────┐                         │**

**│  │ DelegatingFilterProxy│  ← THE BRIDGE          │**

**│  │ (bean = "springSecurityFilterChain")           │**

**│  └─────────┬───────────┘                         │**

**│            │ delegates to                        │**

**│            ▼                                     │**

**│  ┌─────────────────────────────────────────┐     │**

**│  │      SPRING APPLICATION CONTEXT         │     │**

**│  │                                         │     │**

**│  │  FilterChainProxy                       │     │**

**│  │  (bean name: springSecurityFilterChain) │     │**

**│  │       │                                 │     │**

**│  │       ├── SecurityFilterChain 1         │     │**

**│  │       │   ├── SecurityContextFilter     │     │**

**│  │       │   ├── CsrfFilter               │     │**

**│  │       │   ├── UsernamePasswordAuthFilter│     │**

**│  │       │   ├── BearerTokenAuthFilter     │     │**

**│  │       │   ├── AuthorizationFilter       │     │**

**│  │       │   └── ExceptionTranslationFilter│     │**

**│  │       │                                 │     │**

**│  │       └── SecurityFilterChain 2         │     │**

**│  │           └── (different filters for    │     │**

**│  │               different URL patterns)   │     │**

**│  └─────────────────────────────────────────┘     │**

**│            │                                     │**

**│            ▼                                     │**

**│  ┌─────────────────────┐                         │**

**│  │ DispatcherServlet   │  ← Your controllers     │**

**│  └─────────────────────┘                         │**

**└─────────────────────────────────────────────────┘**

**Three Key Components**
------------------------

### **1\. DelegatingFilterProxy (Servlet world)**

*   **Registered with the Servlet container as a standard javax.servlet.Filter**

*   **Does no security work itself**

*   **Looks up a Spring bean by name (default: springSecurityFilterChain)**

*   **Delegates doFilter() to that Spring bean**
* Absolutely. For a **Senior Java/Spring Boot Developer**, Spring Security interviews usually go beyond annotations like `@PreAuthorize`. Interviewers expect you to understand the **security architecture, filter chain, authentication flow, JWT, OAuth2/OIDC, method security, CSRF, CORS, sessions, password security, microservices security, and production troubleshooting**.

The current Spring Security documentation covers servlet/reactive security, request/method authorization, OAuth2 Client/Resource Server/Authorization Server, JWT and opaque tokens, and common web protections. ([Home][1])

Below are **60 Senior-level interview questions with answers**, organized from fundamentals → internals → JWT/OAuth2 → production scenarios.

---

# Spring Security — 60 Senior Developer Interview Questions & Answers

## Section 1 — Spring Security Fundamentals

### 1. What is Spring Security and what problems does it solve?

**Answer:**

Spring Security is a framework for securing Spring applications.

Its major responsibilities are:

1. **Authentication** — Who are you?
2. **Authorization** — What are you allowed to access?
3. **Protection against common attacks**

    * CSRF
    * Session fixation
    * Clickjacking
    * Brute-force-related application concerns
4. Password encoding
5. OAuth2/OIDC
6. JWT and resource-server security
7. Method-level security
8. Security headers
9. Session management

At a high level:

```text
Client
   |
   v
Spring Security
   |
   +--> Authentication
   |
   +--> Authorization
   |
   +--> Security Filters
   |
   v
Controller
```

Spring Security is designed to provide security consistently across both traditional servlet applications and reactive applications. ([Home][1])

---

### 2. What is the difference between Authentication and Authorization?

**Answer:**

**Authentication** answers:

> Who are you?

**Authorization** answers:

> What are you allowed to do?

Example:

```text
User logs in
     |
     v
Authentication
     |
     v
User = Goutham
     |
     v
Authorization
     |
     +---- ADMIN --> DELETE /users
     |
     +---- USER  --> GET /users
```

Example:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
    .anyRequest().authenticated()
);
```

Authentication happens before authorization.

---

### 3. Explain the Spring Security architecture.

**Answer:**

A simplified architecture is:

```text
HTTP Request
     |
//''[pjlknjhfgrsfdxcvchvfmnhvbjbmnbmb mn nbc nb bnbj     v
Servlet Container
     |
     v
DelegatingFilterProxy
     |
     v
FilterChainProxy
     |
     v
SecurityFilterChain
     |
     +--> Authentication Filters
     |
     +--> Authorization Filters
     |
     +--> CSRF
     |
     +--> Exception Handling
     |
     v
Controller
```

The important components are:

* `DelegatingFilterProxy`
* `FilterChainProxy`
* `SecurityFilterChain`
* Authentication mechanisms
* `AuthenticationManager`
* `AuthenticationProvider`
* `SecurityContext`
* Authorization infrastructure

Understanding this architecture is extremely important for senior-level troubleshooting.

---

### 4. What is `SecurityFilterChain`?

**Answer:**

`SecurityFilterChain` defines which Spring Security filters apply to incoming requests and what security rules should be enforced.

Modern Spring Security configuration commonly looks like:

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception {

    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/public/**").permitAll()
            .anyRequest().authenticated()
        );

    return http.build();
}
```

Spring Boot can auto-configure security, but defining your own `SecurityFilterChain` allows you to customize it.

---

### 5. What is `DelegatingFilterProxy`?

**Answer:**

`DelegatingFilterProxy` is the bridge between the servlet container's filter mechanism and Spring-managed beans.

Conceptually:

```text
Servlet Container
       |
       v
DelegatingFilterProxy
       |
       v
Spring Bean
       |
       v
FilterChainProxy
```

It allows Spring Security filters to participate in the servlet filter chain while being managed by Spring.

---

### 6. What is `FilterChainProxy`?

**Answer:**

`FilterChainProxy` is the central Spring Security filter infrastructure.

It determines which `SecurityFilterChain` should handle a request.

For example:

```text
Request
  |
  +-- /api/** ------> API SecurityFilterChain
  |
  +-- /admin/** ----> Admin SecurityFilterChain
  |
  +-- /** ----------> Default SecurityFilterChain
```

This becomes particularly useful when different URL patterns require different authentication mechanisms.

---

### 7. What is `SecurityContext`?

**Answer:**

`SecurityContext` contains the current authenticated user's security information.

It contains an `Authentication` object.

Example:

```java
SecurityContext context =
    SecurityContextHolder.getContext();

Authentication authentication =
    context.getAuthentication();
```

You can retrieve:

```java
authentication.getName();

authentication.getAuthorities();

authentication.getPrincipal();

authentication.isAuthenticated();
```

Conceptually:

```text
SecurityContext
      |
      v
Authentication
      |
      +--> Principal
      +--> Authorities
      +--> Authentication details
```

---

### 8. What is `SecurityContextHolder`?

**Answer:**

`SecurityContextHolder` provides access to the current `SecurityContext`.

For example:

```java
Authentication auth =
    SecurityContextHolder
        .getContext()
        .getAuthentication();
```

In traditional servlet applications, the security context is commonly associated with the executing thread.

A senior developer should also understand that asynchronous execution can change the thread, so blindly assuming the same security context is available in another thread can cause security bugs.

---

## Section 2 — Authentication Architecture

### 9. Explain `AuthenticationManager`, `AuthenticationProvider`, and `UserDetailsService`.

**Answer:**

These components have different responsibilities.

```text
AuthenticationManager
        |
        v
AuthenticationProvider
        |
        +----> UserDetailsService
        |
        +----> PasswordEncoder
```

### `AuthenticationManager`

Coordinates authentication.

### `AuthenticationProvider`

Actually performs a particular authentication mechanism.

Examples:

* username/password
* LDAP
* custom authentication
* JWT-related authentication infrastructure

### `UserDetailsService`

Loads user information.

```java
UserDetails loadUserByUsername(String username);
```

---

### 10. What is `AuthenticationProvider`?

**Answer:**

`AuthenticationProvider` performs authentication for a particular authentication type.

Example:

```java
public interface AuthenticationProvider {

    Authentication authenticate(
        Authentication authentication);

    boolean supports(
        Class<?> authentication);
}
```

A provider might:

1. Receive username/password.
2. Load user.
3. Compare password.
4. Create authenticated `Authentication`.
5. Return it.

Multiple providers can exist:

```text
AuthenticationManager
      |
      +--> Provider 1
      |
      +--> Provider 2
      |
      +--> Provider 3
```

---

### 11. What is `UserDetailsService`?

**Answer:**

`UserDetailsService` loads user-specific data.

Example:

```java
@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(
            String username) {

        // Load user from database

        return User.builder()
            .username(username)
            .password(encodedPassword)
            .roles("USER")
            .build();
    }
}
```

Important:

`UserDetailsService` is **not itself responsible for password verification**.

Password verification is typically performed by an authentication provider using a `PasswordEncoder`.

---

### 12. What is `PasswordEncoder` and why should passwords not be encrypted?

**Answer:**

Passwords should generally be **hashed**, not reversibly encrypted.

Use:

```java
@Bean
PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Or an appropriate adaptive password-hashing strategy.

The process is:

```text
Password
   |
   v
PasswordEncoder
   |
   v
Hash
   |
   v
Database
```

During login:

```text
Raw password
      |
      v
PasswordEncoder.matches()
      |
      v
Stored password hash
```

You should never store:

```text
password = "Welcome123"
```

in the database.

---

### 13. BCrypt vs SCrypt vs Argon2 — what would you choose?

**Answer:**

All are designed for password hashing, but they have different characteristics.

For a modern application, **Argon2** is a strong choice when supported by the application's requirements and operational environment.

BCrypt remains widely used and well understood.

The important principle is:

> Use an adaptive password hashing algorithm designed to resist brute-force attacks rather than plain hashing such as SHA-256.

For an existing enterprise application, don't blindly change the hashing algorithm without considering password migration and compatibility.

---

### 14. What is the difference between `hasRole()` and `hasAuthority()`?

**Answer:**

`hasRole()` typically adds the `ROLE_` prefix.

```java
.hasRole("ADMIN")
```

effectively checks:

```text
ROLE_ADMIN
```

Whereas:

```java
.hasAuthority("ADMIN")
```

checks exactly:

```text
ADMIN
```

Example:

```java
.hasRole("ADMIN")
```

requires:

```text
ROLE_ADMIN
```

while:

```java
.hasAuthority("ADMIN")
```

requires:

```text
ADMIN
```

This is a common source of authorization bugs.

---

## Section 3 — Security Configuration

### 15. How do you configure Spring Security in modern Spring Boot?

**Answer:**

A common modern configuration is:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/public/**",
                    "/swagger-ui/**"
                ).permitAll()
                .requestMatchers("/admin/**")
                .hasRole("ADMIN")
                .anyRequest()
                .authenticated()
            );

        return http.build();
    }
}
```

The important modern concept is defining a `SecurityFilterChain` bean instead of relying on older configuration styles.

---

### 16. What happened to `WebSecurityConfigurerAdapter`?

**Answer:**

Older Spring Security applications commonly used:

```java
extends WebSecurityConfigurerAdapter
```

Modern Spring Security uses component-based configuration with beans such as:

```java
@Bean
SecurityFilterChain securityFilterChain(...)
```

This gives more explicit and composable configuration.

For new applications, you should generally use the modern bean-based approach.

---

### 17. How do you allow certain endpoints without authentication?

**Answer:**

Use:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers(
        "/login",
        "/register",
        "/public/**"
    ).permitAll()
    .anyRequest().authenticated()
)
```

Be careful with:

```java
.anyRequest().permitAll()
```

because it effectively disables authorization for those requests.

---

### 18. What happens if authorization rules are defined in the wrong order?

**Answer:**

Ordering matters.

For example:

```java
.requestMatchers("/**").permitAll()
.requestMatchers("/admin/**").hasRole("ADMIN")
```

The broad rule can match `/admin/**` first, defeating the intended restriction.

Prefer:

```java
.requestMatchers("/admin/**").hasRole("ADMIN")
.requestMatchers("/public/**").permitAll()
.anyRequest().authenticated()
```

Think:

```text
Most specific
      ↓
Less specific
      ↓
Default
```

---

### 19. How do you create multiple `SecurityFilterChain`s?

**Answer:**

You can define multiple chains with request matching and ordering.

Conceptually:

```text
/api/**      -> JWT SecurityFilterChain
/admin/**    -> Admin SecurityFilterChain
/**          -> Default chain
```

Example:

```java
@Bean
@Order(1)
SecurityFilterChain apiChain(HttpSecurity http)
        throws Exception {

    http
        .securityMatcher("/api/**")
        .authorizeHttpRequests(auth -> auth
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 ->
            oauth2.jwt());

    return http.build();
}
```

A second chain can handle other application URLs.

This is useful when different areas of an application require different security mechanisms.

---

## Section 4 — JWT

### 20. What is JWT?

**Answer:**

JWT stands for **JSON Web Token**.

A JWT normally contains:

```text
Header.Payload.Signature
```

Example:

```text
xxxxx.yyyyy.zzzzz
```

The payload can contain claims:

```json
{
  "sub": "goutham",
  "iss": "https://identity.example.com",
  "aud": "orders-api",
  "roles": ["USER"],
  "exp": 1780000000
}
```

The signature protects integrity.

Important:

> A signed JWT is not automatically encrypted.

Therefore, don't put sensitive secrets into a normal signed JWT payload.

---

### 21. Explain JWT authentication flow in Spring Security.

**Answer:**

For a resource server:

```text
Client
  |
  | Authorization: Bearer <JWT>
  v
Spring Security
  |
  v
BearerTokenAuthenticationFilter
  |
  v
JwtDecoder
  |
  +--> Signature validation
  +--> exp validation
  +--> nbf validation
  +--> issuer validation
  |
  v
Authentication
  |
  v
Authorization
  |
  v
Controller
```

Spring Security's Resource Server support can validate JWT bearer tokens using a `JwtDecoder`. ([Home][2])

---

### 22. What does `BearerTokenAuthenticationFilter` do?

**Answer:**

It looks for a bearer token in the request, commonly:

```http
Authorization: Bearer eyJ...
```

It then passes the token into the authentication process.

For JWT resource-server configuration, Spring Security registers this authentication mechanism. ([Home][3])

---

### 23. What is `JwtDecoder`?

**Answer:**

`JwtDecoder` decodes and validates JWTs.

It can validate:

* Signature
* `exp`
* `nbf`
* `iss`
* Additional application-specific claims

For example:

```java
@Bean
JwtDecoder jwtDecoder() {
    return JwtDecoders
        .fromIssuerLocation(issuerUri);
}
```

Spring Boot can automatically configure this when the appropriate OAuth2 Resource Server configuration is supplied. ([Home][2])

---

### 24. What is `issuer-uri`?

**Answer:**

Example:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://identity.example.com
```

The issuer identifies the authorization server that issued the token.

Spring Security can use the issuer to discover provider metadata and signing keys and validate the JWT's issuer claim. ([Home][2])

---

### 25. What is a JWK/JWK Set?

**Answer:**

JWK means **JSON Web Key**.

A JWK Set is a collection of public keys exposed by an authorization server.

Example architecture:

```text
Authorization Server
       |
       | JWK Set
       v
Resource Server
       |
       v
JwtDecoder
       |
       v
Verify JWT
```

This allows signing keys to rotate without manually deploying a new public key everywhere.

Spring Security supports retrieving signing keys through a JWK Set URI. ([Home][2])

---

### 26. What happens when JWT signing keys rotate?

**Answer:**

A production authorization server can periodically rotate keys.

The token contains a key identifier such as:

```text
kid
```

The resource server obtains the appropriate public key from the JWK Set.

Spring Security's JWT support can automatically accommodate newly published signing keys. ([Home][2])

---

### 27. What claims should you validate in a JWT?

**Answer:**

At minimum, depending on the security model:

```text
iss  -> issuer
aud  -> audience
exp  -> expiration
nbf  -> not before
signature
```

For example:

```json
{
  "iss": "https://auth.example.com",
  "aud": "orders-api",
  "sub": "user123",
  "exp": 1780000000
}
```

Don't simply verify:

```text
signature == valid
```

and assume the token is trustworthy.

You must ensure it was issued for the expected issuer/audience and is currently valid.

Spring Boot supports configuring expected audiences as well. ([Home][4])

---

### 28. How do you map JWT claims to Spring authorities?

**Answer:**

Suppose JWT contains:

```json
{
  "roles": [
    "ADMIN",
    "USER"
  ]
}
```

You can customize `JwtAuthenticationConverter`.

Conceptually:

```java
JwtAuthenticationConverter converter =
    new JwtAuthenticationConverter();

converter.setJwtGrantedAuthoritiesConverter(
    jwt -> {
        // map roles to GrantedAuthority
    }
);
```

This is especially common when an external identity provider uses claims such as:

```text
roles
groups
authorities
scp
scope
```

---

### 29. What is the difference between JWT and opaque tokens?

**Answer:**

### JWT

The resource server can validate the token locally.

```text
Request
 |
 v
JWT
 |
 v
Local validation
```

Advantages:

* Low latency
* No introspection call per request
* Good for distributed systems

Disadvantages:

* Revocation is harder
* Token may contain stale authorization information

### Opaque token

The resource server asks the authorization server to introspect the token.

```text
Request
 |
 v
Opaque Token
 |
 v
Authorization Server
 |
 v
Introspection
```

Spring Security supports both JWT and opaque bearer tokens. ([Home][5])

---

## Section 5 — OAuth2 and OIDC

### 30. What is OAuth2?

**Answer:**

OAuth2 is primarily an **authorization framework**.

It allows a client to obtain an access token to access protected resources.

Typical architecture:

```text
User
 |
 v
Client Application
 |
 v
Authorization Server
 |
 | Access Token
 v
Resource Server
```

OAuth2 is not itself an authentication protocol.

---

### 31. What is OpenID Connect?

**Answer:**

OIDC builds an authentication layer on top of OAuth2.

OAuth2:

> Allows delegated authorization.

OIDC:

> Provides authentication and identity information.

OIDC commonly introduces an:

```text
ID Token
```

which contains identity-related claims.

---

### 32. Explain Authorization Code Flow.

**Answer:**

Typical flow:

```text
User
 |
 v
Client
 |
 | Authorization Request
 v
Authorization Server
 |
 | Login + Consent
 |
 | Authorization Code
 v
Client
 |
 | Code + client authentication
 v
Authorization Server
 |
 | Access Token
 v
Client
 |
 | Access Token
 v
Resource Server
```

For enterprise applications, Authorization Code Flow with PKCE is commonly preferred for browser/public-client scenarios.

---

### 33. What is PKCE and why is it important?

**Answer:**

PKCE means:

**Proof Key for Code Exchange**

The client generates:

```text
code_verifier
```

Then derives:

```text
code_challenge
```

The authorization request contains the challenge.

The token request contains the verifier.

This prevents an attacker who intercepts the authorization code from easily exchanging it for a token.

---

### 34. Access Token vs ID Token?

**Answer:**

### Access Token

Used to access APIs.

```text
Client -> API
Authorization: Bearer <access-token>
```

### ID Token

Used by the client to understand the authenticated user.

It is primarily an OIDC identity artifact.

A common mistake is sending the ID token to APIs when the API expects an access token.

---

### 35. What is the difference between Resource Server and Authorization Server?

**Answer:**

### Authorization Server

Issues tokens.

Examples:

```text
Keycloak
Okta
Auth0
Microsoft Entra ID
Spring Authorization Server
```

### Resource Server

Protects APIs and validates access tokens.

Example:

```text
Order Service
Payment Service
Customer Service
```

Architecture:

```text
                Authorization Server
                     |
                     | JWT
                     v
Client ----------> API Gateway
                     |
                     v
                Resource Server
```

Spring Security provides support for resource servers and authorization-server functionality. ([Home][6])

---

## Section 6 — Authorization

### 36. What is method-level security?

**Answer:**

Method-level security protects service methods rather than only HTTP endpoints.

Enable it using:

```java
@EnableMethodSecurity
```

Then:

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) {
}
```

Other supported annotations include:

```text
@PreAuthorize
@PostAuthorize
@PreFilter
@PostFilter
```

Method security is not enabled simply by adding the Spring Boot Security starter; it needs explicit activation. ([Home][7])

---

### 37. What is `@PreAuthorize`?

**Answer:**

It evaluates authorization before method execution.

Example:

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteAccount(Long id) {
}
```

You can also use method parameters:

```java
@PreAuthorize("#userId == authentication.principal.id")
public User getUser(Long userId) {
}
```

This provides fine-grained authorization.

---

### 38. What is `@PostAuthorize`?

**Answer:**

`@PostAuthorize` evaluates authorization **after** method execution.

Example:

```java
@PostAuthorize(
    "returnObject.owner == authentication.name"
)
public Document getDocument(Long id) {
}
```

This is useful when authorization depends on the returned object.

However, be careful when the method performs side effects because the method executes before authorization is evaluated.

---

### 39. `@PreAuthorize` vs URL authorization — which should you use?

**Answer:**

Use both at appropriate boundaries.

### URL authorization

Protects HTTP endpoints:

```java
.requestMatchers("/admin/**")
.hasRole("ADMIN")
```

### Method authorization

Protects business operations:

```java
@PreAuthorize("hasRole('ADMIN')")
```

A strong enterprise design is often:

```text
API Gateway / HTTP
       |
       v
URL Authorization
       |
       v
Service Layer
       |
       v
Method Authorization
       |
       v
Database
```

This creates defense in depth.

---

### 40. What is `GrantedAuthority`?

**Answer:**

`GrantedAuthority` represents an authority granted to an authenticated user.

Examples:

```text
ROLE_ADMIN
ROLE_USER
ORDER_READ
ORDER_WRITE
SCOPE_orders.read
```

Example:

```java
authentication.getAuthorities();
```

Authorization rules evaluate these authorities.

---

## Section 7 — CSRF, CORS and Web Security

### 41. What is CSRF?

**Answer:**

CSRF means:

**Cross-Site Request Forgery**

An attacker tricks a user's browser into making an authenticated request to your application.

For example:

```text
User logged into bank.com
        |
        v
Attacker site
        |
        v
POST bank.com/transfer
```

Because browsers automatically send cookies, the server may believe the request came from the legitimate user.

Spring Security protects against CSRF by default for unsafe methods such as POST. ([Home][8])

---

### 42. When can CSRF be disabled for a REST API?

**Answer:**

This requires careful reasoning.

If your API is truly stateless and uses bearer tokens in the `Authorization` header rather than browser cookies, CSRF is generally not the same threat because browsers do not automatically attach arbitrary Authorization headers cross-site.

A common configuration for a stateless bearer-token API is:

```java
http.csrf(csrf -> csrf.disable());
```

But don't blindly disable CSRF just because the application is called "REST."

If authentication relies on cookies, CSRF protection can still be important.

---

### 43. What is CORS?

**Answer:**

CORS means:

**Cross-Origin Resource Sharing**

It controls whether browser JavaScript from one origin can call another origin.

Example:

```text
Frontend
https://shop.example.com

Backend
https://api.example.com
```

These are different origins.

Spring Security can integrate with Spring MVC/WebFlux CORS configuration.

Important distinction:

```text
CORS != CSRF
```

CORS controls browser cross-origin access.

CSRF protects against unwanted authenticated state-changing requests.

---

### 44. What is preflight request?

**Answer:**

A browser can send:

```http
OPTIONS /api/orders
```

before the actual request.

This is called a **CORS preflight request**.

The server must respond with appropriate headers such as:

```http
Access-Control-Allow-Origin
Access-Control-Allow-Methods
Access-Control-Allow-Headers
```

A common production issue is accidentally requiring authentication for OPTIONS requests.

---

### 45. What security headers does Spring Security provide?

**Answer:**

Spring Security can help configure headers such as:

```text
Content-Security-Policy
X-Content-Type-Options
X-Frame-Options
Referrer-Policy
HSTS
```

Headers should be selected based on the application's architecture and browser requirements.

For example:

```text
Strict-Transport-Security
```

helps enforce HTTPS in supporting browsers.

---

## Section 8 — Sessions

### 46. What is session fixation?

**Answer:**

Session fixation occurs when an attacker gets a victim to use a session ID known to the attacker.

After successful authentication, the attacker may attempt to reuse that session.

Spring Security provides session-fixation protection, including changing the session identifier after authentication.

Conceptually:

```text
Before Login
Session ID = ABC

Login

After Login
Session ID = XYZ
```

This prevents the old identifier from being reused.

---

### 47. Stateful vs Stateless authentication?

**Answer:**

### Stateful

Server stores authentication/session state.

```text
Client
 |
 | Session Cookie
 v
Server
 |
 +--> Session Store
```

Example:

```text
JSESSIONID
```

### Stateless

Each request contains authentication information.

```text
Client
 |
 | Bearer JWT
 v
Server
 |
 v
Validate JWT
```

No server-side HTTP session is required for authentication.

For microservice APIs, stateless bearer-token security is common.

---

### 48. How do you configure a stateless Spring Security API?

**Answer:**

Typically:

```java
http
    .sessionManagement(session ->
        session.sessionCreationPolicy(
            SessionCreationPolicy.STATELESS
        )
    );
```

Combined with JWT:

```java
http
    .oauth2ResourceServer(oauth2 ->
        oauth2.jwt());
```

Conceptually:

```text
Request
  |
  v
Bearer JWT
  |
  v
Validate
  |
  v
Authorize
  |
  v
Controller
```

---

### 49. What is `SessionCreationPolicy`?

**Answer:**

It controls how Spring Security handles HTTP sessions.

Common values include:

```text
ALWAYS
IF_REQUIRED
NEVER
STATELESS
```

For JWT-based APIs:

```java
SessionCreationPolicy.STATELESS
```

is commonly appropriate.

---

## Section 9 — Exception Handling

### 50. What is the difference between 401 and 403?

**Answer:**

### 401 Unauthorized

Usually means:

> Authentication is missing or invalid.

Example:

```text
No JWT
Expired JWT
Invalid JWT
```

### 403 Forbidden

Usually means:

> User is authenticated but does not have sufficient permission.

Example:

```text
User = USER
Endpoint requires = ADMIN
```

Flow:

```text
Not authenticated
       |
       v
401

Authenticated
but insufficient authority
       |
       v
403
```

---

### 51. What is `AuthenticationEntryPoint`?

**Answer:**

`AuthenticationEntryPoint` handles authentication failures where the client needs to authenticate.

For REST APIs, you may return:

```http
HTTP/1.1 401 Unauthorized
Content-Type: application/json
```

with:

```json
{
  "error": "Unauthorized"
}
```

This is preferable to returning an HTML login page from an API.

---

### 52. What is `AccessDeniedHandler`?

**Answer:**

`AccessDeniedHandler` handles authorization failures.

Example:

```text
Authenticated user
       |
       v
Requires ADMIN
       |
       v
User only has USER
       |
       v
403
```

You can customize the response:

```java
http.exceptionHandling(exception ->
    exception
        .accessDeniedHandler(customHandler)
);
```

---

## Section 10 — Microservices Security

### 53. How would you secure microservices using Spring Security?

**Answer:**

A common architecture:

```text
                 Identity Provider
                       |
                       | JWT
                       v
Client ---> API Gateway ---> Service A
                |              |
                |              +--> JWT validation
                |
                +-----------> Service B
                               |
                               +--> JWT validation
```

Each service should generally validate the token rather than blindly trusting headers added by another service.

Typical approach:

```text
OAuth2/OIDC
+
JWT
+
Spring Security Resource Server
+
API Gateway
```

---

### 54. Should every microservice validate JWT independently?

**Answer:**

In many architectures, yes.

Each service can act as an OAuth2 Resource Server and validate the access token.

Advantages:

* Defense in depth
* Services don't need to trust gateway-only validation
* Independent deployment
* Reduced central dependency

However, organizations may choose gateway-centric or service-mesh approaches depending on their architecture.

The important point is:

> Don't blindly trust identity headers coming from the network unless the network boundary and trust model explicitly guarantee their integrity.

---

### 55. How do you secure service-to-service communication?

**Answer:**

Options include:

### OAuth2 Client Credentials

```text
Service A
   |
   | client credentials
   v
Authorization Server
   |
   | access token
   v
Service B
```

### mTLS

```text
Service A <==== TLS/mTLS ====> Service B
```

### Service mesh

For example:

```text
Istio
Linkerd
```

A mature architecture can combine:

```text
OAuth2
+
mTLS
+
Network Policies
+
API Gateway
+
Least Privilege
```

---

### 56. What is Client Credentials Flow?

**Answer:**

It is used when one application/service authenticates as itself rather than on behalf of an end user.

Example:

```text
Order Service
     |
     | client_id + client_secret
     v
Authorization Server
     |
     | access_token
     v
Payment Service
```

There is no end-user login involved.

This is commonly used for service-to-service communication.

---

## Section 11 — Advanced Senior-Level Questions

### 57. A JWT is valid, but the user still receives 403. How do you troubleshoot it?

**Answer:**

I would check in this order:

### 1. Is authentication successful?

Check:

```java
SecurityContextHolder
    .getContext()
    .getAuthentication();
```

### 2. What authorities exist?

Check:

```java
authentication.getAuthorities();
```

Maybe token contains:

```text
ADMIN
```

but application expects:

```text
ROLE_ADMIN
```

### 3. Check `hasRole()` vs `hasAuthority()`

```java
hasRole("ADMIN")
```

expects:

```text
ROLE_ADMIN
```

### 4. Check JWT authority converter

Maybe:

```text
roles
```

are not being mapped to `GrantedAuthority`.

### 5. Check URL rule ordering

A broad matcher may be overriding your intended rule.

### 6. Check method security

For example:

```java
@PreAuthorize("hasRole('ADMIN')")
```

may reject the request even though URL authorization passed.

---

### 58. A valid JWT suddenly returns 401 in production. What would you investigate?

**Answer:**

My investigation would include:

```text
1. Token expiration
2. Issuer
3. Audience
4. Signature
5. Signing-key rotation
6. JWK endpoint
7. Clock synchronization
8. Algorithm
9. Authorization header
10. Resource-server configuration
```

For example:

```text
JWT
 |
 +--> exp?
 +--> iss?
 +--> aud?
 +--> kid?
 +--> signature?
 +--> nbf?
```

I would also inspect whether the authorization server rotated keys and whether the resource server can retrieve the new JWK.

Spring Security validates timestamp and issuer claims as part of JWT resource-server validation, with configurable validation behavior. ([Home][2])

---

### 59. How would you design production-grade Spring Security for a React + Spring Boot microservices application?

**Answer:**

I would use an architecture like:

```text
                    ┌─────────────────┐
                    │ Identity        │
                    │ Provider        │
                    └────────┬────────┘
                             |
                          JWT/Token
                             |
                             v
┌──────────┐          ┌──────────────┐
│ React UI │ -------->│ API Gateway  │
└──────────┘          └──────┬───────┘
                             |
             ┌───────────────┼───────────────┐
             |               |               |
             v               v               v
        Order Service   User Service    Payment Service
             |               |               |
             v               v               v
       Resource Server  Resource Server  Resource Server
```

Security principles:

### Authentication

OIDC/OAuth2.

### API security

OAuth2 access tokens.

### Token format

JWT where appropriate.

### Authorization

```text
URL authorization
+
@PreAuthorize
+
business/domain authorization
```

### Secrets

Never put secrets in:

```text
Git
application.yml
JWT
Docker image
```

Use a secret-management solution.

### Infrastructure

```text
HTTPS
mTLS where required
Network policies
API Gateway
Security headers
Rate limiting
Centralized auditing
```

### Observability

Log:

```text
authentication failures
authorization failures
security events
```

But **never log passwords, access tokens, refresh tokens, or sensitive credentials**.

---

### 60. You are reviewing a Spring Security implementation in production. What security issues would you look for?

**Answer:**

This is one of the strongest senior-level interview questions.

I would perform a security review across multiple layers.

### Authentication

Check:

* Strong password hashing
* No plaintext passwords
* MFA where required
* Secure token validation
* Token expiration
* Issuer validation
* Audience validation
* Signing algorithm restrictions
* Key rotation

### Authorization

Check:

```text
Least privilege
RBAC
Fine-grained permissions
Method-level authorization
Object-level authorization
```

Look for:

```java
.anyRequest().permitAll()
```

and overly broad rules.

### JWT

Check:

```text
signature
iss
aud
exp
nbf
kid
algorithm
```

### Sessions

Check:

```text
session fixation
session timeout
secure cookies
HttpOnly
SameSite
```

### CSRF

Determine whether the application uses:

```text
cookie authentication
```

or:

```text
Authorization: Bearer
```

and configure CSRF accordingly.

Spring Security provides CSRF protection by default for unsafe methods in servlet applications. ([Home][8])

### CORS

Check:

```text
Allowed origins
Allowed methods
Allowed headers
Credentials
Preflight
```

Never blindly configure:

```text
Access-Control-Allow-Origin: *
```

together with credentialed browser requests.

### APIs

Check:

```text
401/403 behavior
Rate limiting
Input validation
Security headers
Sensitive error messages
```

### Microservices

Check:

```text
Service-to-service authentication
mTLS
JWT validation
Gateway trust boundaries
Network policies
```

### Secrets

Verify that credentials aren't stored in:

```text
Git
Docker images
application.properties
logs
JWT payloads
```

### Logging

Ensure security logs don't expose:

```text
passwords
JWTs
client secrets
session IDs
PII
```

### Final principle

I would evaluate the system using:

```text
Authentication
       +
Authorization
       +
Token Security
       +
Session Security
       +
Transport Security
       +
Application Security
       +
Infrastructure Security
       +
Monitoring/Auditing
       =
Production Security
```

---

# ⭐ Senior Interview — The 15 Questions You Should Master First

If you have limited preparation time, prioritize these:

| Priority | Question                                            | Why interviewer asks            |
| -------- | --------------------------------------------------- | ------------------------------- |
| ⭐⭐⭐⭐⭐    | Security Filter Chain                               | Tests framework internals       |
| ⭐⭐⭐⭐⭐    | Authentication vs Authorization                     | Fundamental concept             |
| ⭐⭐⭐⭐⭐    | JWT authentication flow                             | Essential for microservices     |
| ⭐⭐⭐⭐⭐    | `AuthenticationManager` vs `AuthenticationProvider` | Architecture                    |
| ⭐⭐⭐⭐⭐    | JWT validation                                      | Production security             |
| ⭐⭐⭐⭐⭐    | JWT vs opaque token                                 | Architecture decision           |
| ⭐⭐⭐⭐⭐    | OAuth2 vs OIDC                                      | Modern authentication           |
| ⭐⭐⭐⭐⭐    | Access Token vs ID Token                            | Common real-world issue         |
| ⭐⭐⭐⭐⭐    | `@PreAuthorize`                                     | Method security                 |
| ⭐⭐⭐⭐⭐    | 401 vs 403                                          | Troubleshooting                 |
| ⭐⭐⭐⭐⭐    | CSRF vs CORS                                        | Common interview trap           |
| ⭐⭐⭐⭐⭐    | Stateless vs Stateful                               | Microservices                   |
| ⭐⭐⭐⭐⭐    | Service-to-service authentication                   | Distributed systems             |
| ⭐⭐⭐⭐⭐    | JWT 403 troubleshooting                             | Senior production scenario      |
| ⭐⭐⭐⭐⭐    | Production security review                          | Principal/Senior-level thinking |

---

# 🔥 One Architecture You Should Be Able to Explain in an Interview

If the interviewer says:

> **"Explain how you implemented Spring Security in your current microservices application."**

A strong answer can follow this flow:

```text
                         ┌──────────────────┐
                         │ Identity Provider│
                         │ OAuth2 / OIDC    │
                         └────────┬─────────┘
                                  |
                                  | Access Token
                                  | JWT
                                  v
┌─────────────┐          ┌──────────────────┐
│ React       │ --------> │ API Gateway      │
│ Application │          │                  │
└─────────────┘          └────────┬─────────┘
                                  |
                    ┌─────────────┼─────────────┐
                    |             |             |
                    v             v             v
              ┌──────────┐ ┌──────────┐ ┌──────────┐
              │ Order    │ │ Customer │ │ Payment  │
              │ Service  │ │ Service  │ │ Service  │
              └────┬─────┘ └────┬─────┘ └────┬─────┘
                   |             |             |
                   v             v             v
             JWT Validation  JWT Validation  JWT Validation
                   |
                   v
             Authorization
                   |
             ┌─────┴─────┐
             |           |
         URL Rules   Method Security
                       |
                  @PreAuthorize
```

Then explain:

> "The identity provider authenticates the user and issues an OAuth2 access token. The React application sends the access token as a bearer token. Each Spring Boot microservice acts as an OAuth2 Resource Server and validates the JWT signature and relevant claims such as issuer, audience and expiration. After authentication, Spring Security creates the Authentication and stores it in the SecurityContext. Authorization is then performed using URL-level rules and method-level security such as `@PreAuthorize`. The services are stateless, so we don't use HTTP sessions for API authentication."

That answer demonstrates **architecture + Spring internals + OAuth2 + JWT + authorization + microservices**, which is exactly the level expected from a senior developer.

For reference, Spring Security's current documentation describes OAuth2 support across **Resource Server, Client, and Authorization Server**, and its current authorization model includes request and method authorization. ([Home][6])

[Spring Security official documentation](https://docs.spring.io/spring-security/reference/?utm_source=chatgpt.com)

[Spring Security OAuth2 Resource Server documentation](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html?utm_source=chatgpt.com)

[Spring Security Method Security documentation](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html?utm_source=chatgpt.com)

[1]: https://docs.spring.io/spring-security/reference/?utm_source=chatgpt.com "Spring Security :: Spring Security"
[2]: https://docs.spring.io/spring-security/reference/reactive/oauth2/resource-server/jwt.html?utm_source=chatgpt.com "OAuth 2.0 Resource Server JWT :: Spring Security"
[3]: https://docs.spring.io/spring-security/reference/api/java/org/springframework/security/config/annotation/web/configurers/oauth2/server/resource/OAuth2ResourceServerConfigurer.html?utm_source=chatgpt.com "OAuth2ResourceServerConfigurer (spring-security-docs 7.1.0 API)"
[4]: https://docs.spring.io/spring-boot/reference/security/oauth2.html?utm_source=chatgpt.com "OAuth2 :: Spring Boot"
[5]: https://docs.spring.io/spring-security/reference/7.0/servlet/oauth2/resource-server/index.html?utm_source=chatgpt.com "OAuth 2.0 Resource Server :: Spring Security"
[6]: https://docs.spring.io/spring-security/reference/7.0/servlet/oauth2/index.html?utm_source=chatgpt.com "OAuth2 :: Spring Security"
[7]: https://docs.spring.io/spring-security/reference/6.5/servlet/authorization/method-security.html?utm_source=chatgpt.com "Method Security :: Spring Security"
[8]: https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html?utm_source=chatgpt.com "Cross Site Request Forgery (CSRF) :: Spring Security"
