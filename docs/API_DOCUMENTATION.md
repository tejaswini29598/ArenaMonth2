# API Documentation

## ✅ Test Credentials (Development)

For quick testing, use these predefined users with HTTP Basic Authentication:

| Username | Password | Roles |
|----------|----------|-------|
| `admin` | `admin123` | ADMIN, USER |
| `user` | `user123` | USER |
| `vendor` | `vendor123` | VENDOR, USER |

Access test credentials at: `GET /api/v1/credentials`

---

## Authentication Endpoints

### Get Test Credentials
```
GET /api/v1/credentials
Content-Type: application/json

Response 200 OK:
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

### User Login
```
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

Response 200 OK:
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

**Authentication Method**: HTTP Basic Auth
- Endpoint: `POST /api/v1/auth/login`
- Request Body: `LoginRequest` with username and password
- Response: `LoginResponse` with access token and user details
- Token Valid For: 24 hours (86400000 milliseconds)

### User Registration
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123",
  "fullName": "John Doe",
  "phoneNumber": "+1234567890"
}

Response 201 Created:
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "role": "USER",
  "isActive": true
}
```
*Note: User registration endpoint is ready for implementation*

## Product Endpoints

### Get All Products
```
GET /api/products?page=0&size=10&sortBy=name&direction=ASC

Response 200 OK:
{
  "content": [
    {
      "id": 1,
      "name": "Laptop Pro",
      "description": "High-performance laptop for professionals",
      "price": 999.99,
      "stockQuantity": 50,
      "categoryId": 1,
      "categoryName": "Electronics",
      "isActive": true
    }
  ],
  "pageable": {...},
  "totalElements": 8,
  "totalPages": 1
}
```

### Get Product by ID
```
GET /api/products/1

Response 200 OK:
{
  "id": 1,
  "name": "Laptop Pro",
  "description": "High-performance laptop for professionals",
  "price": 999.99,
  "stockQuantity": 50,
  "categoryId": 1,
  "categoryName": "Electronics",
  "isActive": true
}
```

### Create Product (Admin Only)
```
POST /api/products
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "name": "New Laptop",
  "description": "Brand new laptop with latest specs",
  "price": 1299.99,
  "stockQuantity": 25,
  "categoryId": 1
}

Response 201 Created:
{
  "id": 9,
  "name": "New Laptop",
  "description": "Brand new laptop with latest specs",
  "price": 1299.99,
  "stockQuantity": 25,
  "categoryId": 1,
  "categoryName": "Electronics",
  "isActive": true
}
```

### Update Product (Admin Only)
```
PUT /api/products/1
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "name": "Updated Laptop Pro",
  "description": "Updated high-performance laptop",
  "price": 1099.99,
  "stockQuantity": 60,
  "categoryId": 1
}

Response 200 OK: (Updated product)
```

### Delete Product (Admin Only)
```
DELETE /api/products/1
Authorization: Bearer <JWT_TOKEN>

Response 204 No Content
```

### Search Products
```
GET /api/products/search?keyword=laptop&page=0&size=10

Response 200 OK: (Paginated results)
```

### Filter Products by Price
```
GET /api/products/filter/price?minPrice=100&maxPrice=1000&page=0&size=10

Response 200 OK: (Filtered results)
```

### Get Products by Category
```
GET /api/products/category/1

Response 200 OK:
[
  {
    "id": 1,
    "name": "Laptop Pro",
    ...
  },
  {
    "id": 2,
    "name": "Smartphone X",
    ...
  }
]
```

## Category Endpoints

### Get All Categories
```
GET /api/categories

Response 200 OK:
[
  {
    "id": 1,
    "name": "Electronics",
    "description": "Electronic devices and gadgets"
  },
  {
    "id": 2,
    "name": "Clothing",
    "description": "Apparel and fashion items"
  }
]
```

### Create Category (Admin Only)
```
POST /api/categories
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "name": "Books",
  "description": "Physical and digital books"
}

Response 201 Created
```

### Update Category (Admin Only)
```
PUT /api/categories/1
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "name": "Updated Electronics",
  "description": "All electronic devices"
}

Response 200 OK
```

### Delete Category (Admin Only)
```
DELETE /api/categories/1
Authorization: Bearer <JWT_TOKEN>

Response 204 No Content
```

## Error Responses

### 404 Not Found
```
{
  "status": 404,
  "message": "Product not found with id: 999",
  "error": "Resource Not Found",
  "path": "/api/products/999",
  "timestamp": "2026-01-25T10:30:00"
}
```

### 400 Bad Request
```
{
  "status": 400,
  "message": "Insufficient stock for product: Laptop Pro",
  "error": "Bad Request",
  "path": "/api/cart/add",
  "timestamp": "2026-01-25T10:30:00"
}
```

### 401 Unauthorized
```
{
  "status": 401,
  "message": "Unauthorized",
  "error": "Unauthorized",
  "path": "/api/products",
  "timestamp": "2026-01-25T10:30:00"
}
```

### 403 Forbidden
```
{
  "status": 403,
  "message": "Access Denied",
  "error": "Forbidden",
  "path": "/api/products",
  "timestamp": "2026-01-25T10:30:00"
}
```

### 422 Validation Error
```
{
  "status": 400,
  "message": "Validation failed",
  "path": "/api/products",
  "timestamp": "2026-01-25T10:30:00",
  "errors": {
    "name": "Product name is required",
    "price": "Price must be greater than 0",
    "categoryId": "Category ID is required"
  }
}
```

## Request Headers

### Required Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

### Optional Headers
```
Accept: application/json
Accept-Language: en-US
X-Request-ID: unique-id
```

## Pagination

All list endpoints support pagination:
```
?page=0&size=10&sortBy=name&direction=ASC
```

Parameters:
- `page`: Page number (0-indexed)
- `size`: Items per page (default: 10, max: 100)
- `sortBy`: Field to sort by (default: name)
- `direction`: ASC or DESC (default: ASC)

## Rate Limiting

- Requests per minute: 100 (per IP)
- Burst requests: 20

---

**API Version:** 1.0.0  
**Last Updated:** January 2026
