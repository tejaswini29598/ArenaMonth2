# E-Commerce Backend API

Complete Spring Boot e-commerce backend system with REST APIs, Spring Data JPA, Security, and comprehensive testing.

## 📋 Project Overview

This is a full-featured e-commerce backend application built with Spring Boot 2.7.14 and Java 17. It provides a complete REST API for managing products, categories, users, orders, and shopping carts.

### Key Features
-  Product catalog management with categories
-  User authentication and authorization
-  Shopping cart functionality
-  Order processing system
-  Advanced search and filtering
-  JWT-based security
-  Comprehensive error handling
-  API documentation with Swagger
-  Docker support
-  Unit and integration tests

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.8.1+
- PostgreSQL 14+ (or use H2 for development)
- Docker and Docker Compose (optional)

### Setup Instructions

#### 1. Clone the Repository
```bash
git clone <repository-url>
cd ecommerce-backend
```

#### 2. Configure Database

**For Development (Using H2):**
```properties
# Already configured in application-dev.properties
spring.profiles.active=dev
```

**For Production (Using PostgreSQL):**
```bash
# Update application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

#### 3. Build the Project
```bash
mvn clean install
```

#### 4. Run the Application

**Using Maven:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

**Using Docker Compose:**
```bash
docker-compose up -d
```

## 📚 API Documentation

### Access Swagger UI
```
http://localhost:8080/api/swagger-ui.html
```

### Base URL
```
http://localhost:8080/api
```

## 🔌 API Endpoints

### Products
- `GET /products` - Get all products (paginated)
- `GET /products/{id}` - Get product by ID
- `POST /products` - Create new product (Admin only)
- `PUT /products/{id}` - Update product (Admin only)
- `DELETE /products/{id}` - Delete product (Admin only)
- `GET /products/search?keyword=` - Search products
- `GET /products/filter/price?minPrice=&maxPrice=` - Filter by price
- `GET /products/category/{categoryId}` - Get products by category

### Categories
- `GET /categories` - Get all categories
- `GET /categories/{id}` - Get category by ID
- `POST /categories` - Create category (Admin only)
- `PUT /categories/{id}` - Update category (Admin only)
- `DELETE /categories/{id}` - Delete category (Admin only)

### Authentication
- `POST /auth/register` - User registration
- `POST /auth/login` - User login
- `POST /auth/logout` - User logout

### Orders
- `GET /orders` - Get user's orders
- `GET /orders/{id}` - Get order details
- `POST /orders` - Create new order
- `PUT /orders/{id}/status` - Update order status (Admin only)

### Shopping Cart
- `GET /cart` - Get user's cart items
- `POST /cart/add` - Add item to cart
- `PUT /cart/{itemId}` - Update cart item quantity
- `DELETE /cart/{itemId}` - Remove item from cart
- `DELETE /cart/clear` - Clear entire cart

## 🔐 Security

### Authentication
- JWT token-based authentication
- Token expiration: 24 hours
- Refresh token mechanism

### Authorization
- Role-based access control (USER, ADMIN, VENDOR)
- Protected endpoints require authentication
- Admin-only operations for sensitive data

### Password Security
- BCrypt hashing
- Minimum 8 characters required
- CSRF protection enabled

## 🏗️ Architecture

### Project Structure
```
ecommerce-backend/
├── src/main/java/com/ecommerce/
│   ├── config/               # Spring configuration classes
│   ├── controller/           # REST API controllers
│   ├── service/              # Business logic services
│   ├── repository/           # Spring Data JPA repositories
│   ├── model/                # JPA entity classes
│   ├── dto/                  # Data Transfer Objects
│   ├── security/             # Security configuration
│   ├── exception/            # Custom exceptions and handlers
│   └── ECommerceApplication.java  # Main application class
├── src/main/resources/       # Configuration files
├── src/test/                 # Test classes
├── docker/                   # Docker configuration
├── scripts/                  # Database initialization scripts
├── docs/                     # API documentation
├── pom.xml                   # Maven dependencies
└── docker-compose.yml        # Docker Compose configuration
```

### Technology Stack
- **Framework:** Spring Boot 2.7.14
- **Language:** Java 17
- **Database:** PostgreSQL 14
- **ORM:** Hibernate (Spring Data JPA)
- **Security:** Spring Security + JWT
- **API Documentation:** Springdoc OpenAPI (Swagger)
- **Mapping:** ModelMapper
- **Logging:** SLF4J + Logback
- **Testing:** JUnit 5, Mockito, TestContainers
- **Build:** Maven 3.8.1
- **Containerization:** Docker

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Test Coverage
```bash
mvn jacoco:report
```

### Test Types
- **Unit Tests:** Service and repository tests
- **Integration Tests:** Controller and database tests
- **API Tests:** REST endpoint tests

## 📦 Dependencies

### Core Dependencies
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-validation

### Additional Libraries
- JWT (io.jsonwebtoken)
- ModelMapper
- Lombok
- Springdoc OpenAPI (Swagger)

### Testing Dependencies
- spring-boot-starter-test
- spring-security-test
- TestContainers
- H2 Database

## 🚢 Deployment

### Docker Deployment
```bash
# Build image
docker build -f docker/Dockerfile -t ecommerce-backend:latest .

# Run container
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod ecommerce-backend:latest
```

### Using Docker Compose
```bash
docker-compose up -d
```

### Environment Variables
```
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5432/ecommerce_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your-password
APP_JWT_SECRET=your-secret-key-min-32-chars
```

## 📊 Database Schema

### Tables
- **categories** - Product categories
- **products** - Product catalog
- **users** - User accounts
- **orders** - Customer orders
- **order_items** - Items in orders
- **cart_items** - Shopping cart items

### Relationships
- One Category → Many Products
- One User → Many Orders
- One Order → Many OrderItems
- One User → Many CartItems

## 🔍 Monitoring

### Health Check
```
GET http://localhost:8080/api/actuator/health
```

### Metrics
```
GET http://localhost:8080/api/actuator/metrics
```

### Application Info
```
GET http://localhost:8080/api/actuator/info
```

## 📝 Logging

Logs are stored in:
```
./logs/ecommerce-backend.log
```

Configure logging in `application.properties`:
```properties
logging.level.root=INFO
logging.level.com.ecommerce=DEBUG
logging.file.name=logs/ecommerce-backend.log
```

##  Contributing

1. Create a feature branch: `git checkout -b feature/feature-name`
2. Commit changes: `git commit -am 'Add feature'`
3. Push to branch: `git push origin feature/feature-name`
4. Create Pull Request

##  License

MIT License - see LICENSE file for details

## Troubleshooting

### Port Already in Use
```bash
# Kill process on port 8080
lsof -ti:8080 | xargs kill -9
```

### Database Connection Error
- Verify PostgreSQL is running
- Check connection credentials
- Ensure database exists

### JWT Token Error
- Verify token expiration
- Check SECRET_KEY configuration
- Ensure Authorization header format: `Bearer <token>`



---

**Version:** 1.0.0  
**Last Updated:** January 2026  
**Java Version:** 17  
**Spring Boot Version:** 2.7.14
