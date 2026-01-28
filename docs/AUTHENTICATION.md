# Authentication & Authorization Guide

## Overview

This e-commerce backend uses **HTTP Basic Authentication** for development and testing with predefined user credentials. The authentication system is implemented using Spring Security 6.3.4 with role-based access control (RBAC).

---

## 🔐 Test Credentials (Development)

Three predefined users are available for immediate testing without registration:

| Username | Password | Roles | Description |
|----------|----------|-------|-------------|
| `admin` | `admin123` | ADMIN, USER | Full system access, can manage all resources |
| `user` | `user123` | USER | Regular user, can view products, create orders |
| `vendor` | `vendor123` | VENDOR, USER | Can manage own products and orders |

### Access Test Credentials
```bash
GET /api/v1/credentials
```

Response:
```json
{
  "message": "Test credentials for development use only",
  "users": [
    {
      "username": "admin",
      "password": "admin123",
      "role": "ADMIN, USER",
      "description": "Administrator account with full access"
    },
    {
      "username": "user",
      "password": "user123",
      "role": "USER",
      "description": "Regular user account"
    },
    {
      "username": "vendor",
      "password": "vendor123",
      "role": "VENDOR, USER",
      "description": "Vendor account"
    }
  ]
}
```

---

## 🔑 Authentication Methods

### 1. HTTP Basic Authentication (Current Implementation)

Used for login and accessing protected endpoints.

#### Login Endpoint
```
POST /api/v1/auth/login
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "dev-token-1706162217000",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "username": "admin",
    "role": "ADMIN, USER"
  }
}
```

**Response (401 Unauthorized):**
```json
{
  "status": 401,
  "message": "Invalid credentials",
  "timestamp": "2026-01-25T13:30:00.000+05:30",
  "path": "/api/v1/auth/login"
}
```

#### Using Login Token with API Calls

After logging in, use the `accessToken` in subsequent requests:

```bash
curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer dev-token-1706162217000"
```

---

## 🛡️ Authorization & Role-Based Access Control (RBAC)

### Role Definitions

| Role | Permissions |
|------|------------|
| **ADMIN** | Full system access, manage all resources (products, categories, users) |
| **USER** | View products, create orders, manage own cart |
| **VENDOR** | Manage own products, view own orders |

### Protected Endpoints

#### Public Endpoints (No Authentication Required)
- `GET /api/v1/credentials` - Get test credentials
- `GET /api/swagger-ui.html` - Swagger UI documentation
- `GET /api/v3/api-docs/**` - OpenAPI documentation
- `GET /api/h2-console/**` - H2 database console

#### Authentication Required (Any User)
- `GET /api/v1/products` - View products
- `GET /api/v1/products/{id}` - View product details
- `GET /api/v1/categories` - View categories
- `GET /api/v1/categories/{id}` - View category details

#### Admin Only
- `POST /api/v1/products` - Create product
- `PUT /api/v1/products/{id}` - Update product
- `DELETE /api/v1/products/{id}` - Delete product
- `POST /api/v1/categories` - Create category
- `PUT /api/v1/categories/{id}` - Update category
- `DELETE /api/v1/categories/{id}` - Delete category

---

## 📝 Implementation Details

### Security Configuration

The application uses `SecurityConfig` (Spring Security 6.3.4) with the following configuration:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    // Three predefined in-memory users with BCrypt password encoding
    @Bean
    public UserDetailsService userDetailsService() { ... }
    
    // HTTP Basic Authentication enabled
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ...) {
        http.httpBasic(basic -> {});
        // Public endpoints permitted
        // Other endpoints require authentication
    }
}
```

### Password Encoding

All passwords are encoded using **BCrypt** with configurable strength:
- Algorithm: BCrypt
- Strength: 10
- Development Credentials: Pre-encoded during initialization

### User Storage

Currently using **In-Memory User Details Manager** for development/testing:
- Users stored in application memory
- Lost on application restart
- Suitable for development and testing only

---

## 🧪 Testing Authentication

### Test with cURL

```bash
# Get credentials
curl http://localhost:8080/api/v1/credentials

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Access protected endpoint
curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer <accessToken>"
```

### Test with Postman

1. **Get Credentials**
   - Method: GET
   - URL: `http://localhost:8080/api/v1/credentials`
   - Click "Send"

2. **Login**
   - Method: POST
   - URL: `http://localhost:8080/api/v1/auth/login`
   - Body (JSON):
     ```json
     {
       "username": "admin",
       "password": "admin123"
     }
     ```
   - Click "Send"

3. **Use Token in Swagger UI**
   - Open `http://localhost:8080/api/swagger-ui.html`
   - Click "Authorize" button
   - Enter login response token
   - Try API endpoints

### Test with Swagger UI

1. Navigate to `http://localhost:8080/api/swagger-ui.html`
2. Click the "Authorize" button (lock icon)
3. Enter credentials:
   - Username: `admin`
   - Password: `admin123`
4. Click "Authorize"
5. Try endpoints from the Swagger interface

---

## 🔄 Authentication Flow

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ 1. POST /api/v1/auth/login
       │    (username, password)
       ▼
┌─────────────────────────────┐
│  AuthController.login()     │
│  - Validate credentials     │
│  - Check roles              │
└──────────┬──────────────────┘
           │ 2. Generate token
           ▼
┌─────────────────────────────┐
│   LoginResponse             │
│  - accessToken              │
│  - tokenType                │
│  - user details             │
│  - expiresIn (24h)          │
└──────────┬──────────────────┘
           │ 3. Return response
           ▼
┌─────────────────────────────┐
│   Client                    │
│  - Store accessToken        │
│  - Use in Authorization     │
│    header for next calls    │
└─────────────────────────────┘
```

---

## 📋 API Request Examples

### Example 1: Admin Login & Create Product

```bash
# 1. Login as admin
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Response:
# {
#   "accessToken": "dev-token-xyz",
#   "tokenType": "Bearer",
#   "expiresIn": 86400000,
#   "user": {"username": "admin", "role": "ADMIN, USER"}
# }

# 2. Create product (using token)
curl -X POST http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer dev-token-xyz" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Product",
    "description": "Product description",
    "price": 99.99,
    "stockQuantity": 10,
    "categoryId": 1
  }'
```

### Example 2: Regular User Access

```bash
# 1. Login as user
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user123"}'

# 2. View products (allowed)
curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer dev-token-user"

# 3. Try to create product (denied - not admin)
# Returns 403 Forbidden
curl -X POST http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer dev-token-user" \
  -H "Content-Type: application/json" \
  -d '{...}'
```

---

## 🚀 Next Steps: Production Authentication

For production deployment, consider implementing:

1. **JWT (JSON Web Tokens)**
   - Stateless authentication
   - Secure token generation and validation
   - Token refresh mechanism

2. **Database User Storage**
   - Replace in-memory users with database persistence
   - User registration endpoint
   - User profile management

3. **OAuth 2.0 / OpenID Connect**
   - Social login (Google, GitHub, etc.)
   - Third-party integrations
   - Mobile app support

4. **Two-Factor Authentication (2FA)**
   - Enhanced security for sensitive operations
   - SMS or authenticator app support

5. **HTTPS/TLS**
   - Encrypt data in transit
   - SSL certificate configuration

---

## ⚠️ Important Notes

- **Development Only**: Current implementation is for development/testing only
- **Passwords in Code**: Development credentials are visible in source code
- **No Persistence**: In-memory users are lost on application restart
- **HTTP Basic Auth**: Not recommended for production without HTTPS
- **Token Expiration**: Development tokens don't actually expire (for testing ease)

---

## 📚 Related Files

- [SecurityConfig.java](../src/main/java/com/ecommerce/config/SecurityConfig.java) - Security configuration
- [AuthController.java](../src/main/java/com/ecommerce/controller/AuthController.java) - Authentication endpoints
- [CredentialsController.java](../src/main/java/com/ecommerce/controller/CredentialsController.java) - Credentials endpoint
- [LoginRequest.java](../src/main/java/com/ecommerce/dto/LoginRequest.java) - Login request DTO
- [LoginResponse.java](../src/main/java/com/ecommerce/dto/LoginResponse.java) - Login response DTO
