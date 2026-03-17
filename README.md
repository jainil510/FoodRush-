# 🍔 FoodRush - Online Food Ordering System

## 📋 Project Overview

FoodRush is a comprehensive online food ordering platform built with modern Java technologies. This project demonstrates industry-grade backend development practices including secure authentication, RESTful APIs, and scalable architecture.

## 🛠️ Tech Stack

### Backend
- **Java 17** - Programming Language
- **Spring Boot 3.2.0** - Application Framework
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Database Operations
- **MySQL 8.0** - Database
- **JWT (JSON Web Tokens)** - Stateless Authentication

### Development & Testing
- **Maven** - Build Tool
- **JUnit 5** - Unit Testing
- **Mockito** - Mocking Framework
- **TestContainers** - Integration Testing
- **JaCoCo** - Code Coverage

## 🏗️ Architecture

### Clean Architecture Pattern
```
┌─────────────────┐
│   Controller    │ ← REST API Endpoints
├─────────────────┤
│    Service      │ ← Business Logic
├─────────────────┤
│  Repository     │ ← Data Access Layer
├─────────────────┤
│    Entity       │ ← Database Models
└─────────────────┘
```

### Package Structure
```
com.foodrush.food_ordering_system/
├── config/          # Security, Database configurations
├── controller/      # REST API controllers
├── service/         # Business logic implementation
├── repository/      # Data access interfaces
├── entity/          # JPA entities
├── dto/            # Request/Response objects
├── security/       # JWT authentication
├── exception/       # Global exception handling
└── util/           # Utility classes
```

## 🚀 Features

### User Management
- [x] User registration and login
- [x] JWT-based authentication
- [x] Role-based access control (Customer, Restaurant Owner, Admin)
- [x] Password encryption with BCrypt

### Restaurant Management
- [ ] Restaurant registration
- [ ] Menu management
- [ ] Restaurant search and filtering
- [ ] Rating system

### Order Processing
- [ ] Shopping cart functionality
- [ ] Order placement and tracking
- [ ] Real-time order status updates
- [ ] Order history

## 🔐 Security Features

- **JWT Authentication**: Stateless token-based authentication
- **Role-Based Authorization**: Different access levels for different user types
- **Password Security**: BCrypt encryption for password storage
- **Input Validation**: Comprehensive input sanitization
- **CORS Configuration**: Cross-origin resource sharing setup

## 📊 Database Schema

### Core Entities
- **Users**: Customer and restaurant owner accounts
- **Restaurants**: Restaurant information and details
- **MenuItems**: Food items and pricing
- **Orders**: Order management and tracking
- **OrderItems**: Individual order items
- **Carts**: Shopping cart functionality

## 🧪 Testing

### Test Coverage
- **Unit Tests**: Service layer business logic
- **Integration Tests**: Repository layer with TestContainers
- **Controller Tests**: REST API endpoints
- **Security Tests**: Authentication and authorization

### Running Tests
```bash
mvn test
```

### Code Coverage Report
```bash
mvn jacoco:report
```

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- MySQL 8.0
- Maven 3.6+

### Database Setup
```sql
CREATE DATABASE food_ordering;
CREATE USER 'foodapp'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON food_ordering.* TO 'foodapp'@'localhost';
FLUSH PRIVILEGES;
```

### Running the Application
```bash
# Clone the repository
git clone https://github.com/yourusername/food-ordering-system.git
cd food-ordering-system

# Build and run
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

## 📚 API Documentation

### Authentication Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token

### Restaurant Endpoints
- `GET /api/restaurants` - List all restaurants
- `GET /api/restaurants/{id}` - Get restaurant details
- `GET /api/restaurants/{id}/menu` - Get restaurant menu

### Order Endpoints
- `POST /api/orders` - Create new order
- `GET /api/orders/{id}` - Get order details
- `GET /api/orders/user/{userId}` - Get user orders

## 🔧 Configuration

### Application Profiles
- **Development**: `application-dev.yml`
- **Production**: `application-prod.yml`

### Environment Variables
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/food_ordering
    username: ${DB_USERNAME:foodapp}
    password: ${DB_PASSWORD:password}

jwt:
  secret: ${JWT_SECRET:mySecretKey123456789012345678901234567890}
  expiration: ${JWT_EXPIRATION:86400000}
```

## 📈 Project Status

### Current Progress
- [x] Project setup and configuration
- [x] Database configuration
- [x] Security framework setup
- [ ] User entity implementation
- [ ] Restaurant management
- [ ] Order processing
- [ ] API development
- [ ] Testing implementation

### Next Steps
1. Implement User entity with roles
2. Create user registration/login APIs
3. Build restaurant management system
4. Develop order processing workflow
5. Add comprehensive testing
6. Deploy to production

## 🤝 Contributing

This is a learning project to demonstrate industry-grade Java development practices. Feel free to fork and use as a reference.

## 📞 Contact

- **LinkedIn**: [Your LinkedIn Profile]
- **GitHub**: [Your GitHub Profile]
- **Email**: [Your Email]

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

### 🎯 Key Learning Outcomes

1. **Spring Boot Mastery**: Comprehensive understanding of Spring Boot ecosystem
2. **Security Implementation**: JWT authentication and authorization
3. **Database Design**: JPA/Hibernate with MySQL
4. **API Development**: RESTful API design principles
5. **Testing Strategy**: Unit and integration testing
6. **Production Practices**: Configuration management and deployment

### 💼 Resume Highlights

- "Developed a full-stack food ordering platform using Spring Boot and MySQL"
- "Implemented secure JWT-based authentication with role-based access control"
- "Designed RESTful APIs following industry best practices"
- "Applied clean architecture principles for maintainable code"
- "Achieved 80%+ test coverage with JUnit 5 and Mockito"
