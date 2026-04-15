# FoodRush - Food Ordering System

A comprehensive food ordering system built with Spring Boot 3.2.0 that allows customers to browse restaurants, place orders, and track deliveries while providing restaurant owners with tools to manage their menus and orders.

## Features

### Customer Features
- **User Registration & Authentication** - Secure JWT-based authentication system
- **Restaurant Browsing** - Browse restaurants by cuisine type and location
- **Menu Management** - View restaurant menus and item details
- **Order Placement** - Place orders with multiple items and customizations
- **Order Tracking** - Real-time order status tracking
- **Payment Processing** - Multiple payment methods support
- **Order History** - View past orders and reorder favorites

### Restaurant Owner Features
- **Restaurant Management** - Add/edit restaurant information
- **Menu Management** - Add/edit menu items, prices, and descriptions
- **Order Management** - View and manage incoming orders
- **Order Status Updates** - Update order status (Preparing, Ready, Delivered)
- **Analytics Dashboard** - Track sales and popular items

### Admin Features
- **User Management** - Manage all system users
- **Restaurant Approval** - Approve/reject restaurant registrations
- **System Analytics** - View system-wide statistics
- **Order Monitoring** - Monitor all orders in the system

## Technology Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database access layer
- **MySQL** - Primary database
- **JWT (JSON Web Tokens)** - Token-based authentication
- **Lombok** - Boilerplate code reduction
- **Maven** - Build and dependency management

### Testing
- **JUnit 5** - Unit testing framework
- **Spring Boot Test** - Spring Boot testing utilities
- **TestContainers** - Integration testing with Docker containers
- **Mockito** - Mocking framework for unit tests
- **JaCoCo** - Code coverage reporting

## Project Structure

```
src/
|-- main/
|   |-- java/com/foodrush/food_ordering_system/
|   |   |-- config/                 # Security and JWT configuration
|   |   |-- controller/             # REST API controllers
|   |   |   |-- AdminController.java
|   |   |   |-- AuthController.java
|   |   |   |-- OrderController.java
|   |   |   |-- RestaurantController.java
|   |   |-- dto/                    # Data Transfer Objects
|   |   |   |-- request/           # Request DTOs
|   |   |   |-- response/          # Response DTOs
|   |   |-- entity/                # JPA entities
|   |   |   |-- User.java
|   |   |   |-- Restaurant.java
|   |   |   |-- Order.java
|   |   |-- enums/                 # Enumerations
|   |   |   |-- Role.java
|   |   |   |-- OrderStatus.java
|   |   |   |-- PaymentMethod.java
|   |   |   |-- CuisineType.java
|   |   |-- exception/              # Custom exceptions
|   |   |-- repository/             # JPA repositories
|   |   |-- security/               # Security components
|   |   |   |-- JwtAuthenticationFilter.java
|   |   |   |-- JwtTokenProvider.java
|   |   |-- service/               # Business logic
|   |   |   |-- UserService.java
|   |   |   |-- RestaurantService.java
|   |   |   |-- OrderService.java
|   |   |-- FoodOrderingSystemApplication.java
|   |-- resources/
|       |-- application.yml         # Main configuration
|       |-- application-dev.yml     # Development profile
|       |-- application-prod.yml    # Production profile
|-- test/                          # Test classes
```

## Database Schema

### Users Table
- `id` - Primary key
- `email` - Unique email address
- `password` - Encrypted password
- `firstName` - User's first name
- `lastName` - User's last name
- `phoneNumber` - Contact number
- `role` - User role (CUSTOMER, RESTAURANT_OWNER, ADMIN)
- `enabled` - Account status
- `createdAt` - Account creation timestamp
- `updatedAt` - Last update timestamp

### Restaurants Table
- `id` - Primary key
- `name` - Restaurant name
- `description` - Restaurant description
- `address` - Physical address
- `phoneNumber` - Contact number
- `email` - Contact email
- `cuisineType` - Type of cuisine
- `status` - Restaurant status (ACTIVE, INACTIVE, PENDING)
- `ownerId` - Foreign key to users table
- `createdAt` - Creation timestamp
- `updatedAt` - Last update timestamp

### Orders Table
- `id` - Primary key
- `customerId` - Foreign key to users table
- `restaurantId` - Foreign key to restaurants table
- `orderStatus` - Current order status
- `paymentMethod` - Payment method used
- `paymentStatus` - Payment status
- `totalAmount` - Order total
- `deliveryAddress` - Delivery location
- `orderNotes` - Special instructions
- `createdAt` - Order placement timestamp
- `updatedAt` - Last update timestamp

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token

### Restaurants
- `GET /api/restaurants` - Get all restaurants
- `GET /api/restaurants/{id}` - Get restaurant by ID
- `POST /api/restaurants` - Create new restaurant (Restaurant Owner)
- `PUT /api/restaurants/{id}` - Update restaurant (Owner)
- `DELETE /api/restaurants/{id}` - Delete restaurant (Owner/Admin)

### Orders
- `GET /api/orders` - Get user's orders
- `GET /api/orders/{id}` - Get order by ID
- `POST /api/orders` - Place new order
- `PUT /api/orders/{id}/status` - Update order status (Restaurant Owner)
- `GET /api/orders/restaurant/{restaurantId}` - Get restaurant orders (Owner)

### Admin
- `GET /api/admin/users` - Get all users
- `PUT /api/admin/users/{id}/enable` - Enable/disable user
- `GET /api/admin/restaurants/pending` - Get pending restaurant approvals
- `PUT /api/admin/restaurants/{id}/approve` - Approve/reject restaurant

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-jainil510/food-ordering-system.git
   cd food-ordering-system
   ```

2. **Database Setup**
   - Install MySQL 8.0+
   - Create a database named `food_ordering`
   - Update database credentials in `application.yml`:
     ```yaml
     spring:
       datasource:
         url: jdbc:mysql://localhost:3306/food_ordering?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
         username: root
         password: 
     ```

3. **Build the application**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - The application will start on `http://localhost:8080`
   - API endpoints are available at `http://localhost:8080/api`

### Configuration

#### Application Profiles
- **Default**: Uses configuration from `application.yml`
- **Development**: Uses `application-dev.yml` for development environment
- **Production**: Uses `application-prod.yml` for production environment

#### Environment Variables
You can override configuration using environment variables:
- `SPRING_DATASOURCE_URL` - Database URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `JWT_SECRET` - JWT secret key
- `JWT_EXPIRATION` - JWT token expiration time

## Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run tests with coverage
mvn clean test jacoco:report

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run tests for specific package
mvn test -Dtest="com.foodrush.food_ordering_system.service.*"
```

### Test Coverage
- JaCoCo plugin generates coverage reports in `target/site/jacoco/index.html`
- Aim for >80% code coverage
- Test coverage includes unit tests and integration tests

### TestContainers
The project uses TestContainers for integration testing with real MySQL database:
- Tests automatically spin up Docker containers
- No need for manual database setup for testing
- Tests run in isolated environment

## Security

### Authentication
- JWT-based authentication
- Token expiration: 24 hours
- Refresh token: 7 days
- Password encryption using BCrypt

### Authorization
- Role-based access control (RBAC)
- Three roles: CUSTOMER, RESTAURANT_OWNER, ADMIN
- Method-level security using Spring Security annotations

### Security Best Practices
- Input validation using Spring Validation
- SQL injection prevention using JPA/Hibernate
- XSS protection through proper input handling
- CORS configuration for cross-origin requests

## Deployment

### Docker Deployment
```bash
# Build Docker image
docker build -t food-ordering-system .

# Run with Docker Compose
docker-compose up -d
```

### Production Deployment
1. Update `application-prod.yml` with production settings
2. Build the application: `mvn clean package`
3. Deploy the JAR file to production server
4. Run with production profile: `java -jar food-ordering-system.jar --spring.profiles.active=prod`

## Monitoring and Logging

### Health Checks
- Spring Boot Actuator endpoints at `/actuator/health`
- Database connectivity check
- Application metrics at `/actuator/metrics`

### Logging
- Log levels configured in `application.yml`
- Debug logging for development
- Structured logging for production
- Log files stored in `logs/` directory

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style
- Follow Java coding conventions
- Use Lombok annotations to reduce boilerplate
- Write meaningful commit messages
- Add tests for new features
- Maintain test coverage

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions:
- Create an issue in the GitHub repository
- Email: support@foodrush.com
- Documentation: [Project Wiki](https://github.com/jainil510/food-ordering-system/wiki)

## Future Enhancements

- **Mobile Application** - React Native mobile app
- **Real-time Notifications** - WebSocket-based notifications
- **Payment Gateway Integration** - Stripe, PayPal integration
- **Delivery Tracking** - GPS-based delivery tracking
- **Analytics Dashboard** - Advanced analytics and reporting
- **Multi-tenant Support** - Support for multiple restaurants chains
- **API Rate Limiting** - Prevent API abuse
- **Caching Layer** - Redis integration for performance
- **Microservices Architecture** - Split into microservices
- **Cloud Deployment** - AWS/Azure deployment options

## Version History

- **v1.0.0** - Initial release with basic functionality
- **v1.1.0** - Added restaurant management features
- **v1.2.0** - Enhanced order tracking and notifications
- **v2.0.0** - Major refactor with improved architecture

---

**Built with Spring Boot 3.2.0 | Java 17 | MySQL**
