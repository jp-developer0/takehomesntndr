# Banking Microservice 🏦

[![Java](https://img.shields.io/badge/Java-8-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![H2 Database](https://img.shields.io/badge/Database-H2-lightblue.svg)](https://www.h2database.com/)
[![OpenAPI](https://img.shields.io/badge/API-OpenAPI%203-green.svg)](https://swagger.io/specification/)

Complete banking microservice built with Spring Boot, featuring full CRUD operations, advanced search capabilities, and comprehensive observability.

## 🚀 Features

### Core Banking Operations
- ✅ **Complete CRUD** for bank accounts
- ✅ **Banking transactions**: Debit, Credit operations
- ✅ **Account management**: Activate/Deactivate accounts
- ✅ **Advanced search**: By holder, account type, balance, status
- ✅ **Statistics & reporting**: System-wide analytics

### Technical Features
- ✅ **Spring Boot 2.7.18** with modern architecture
- ✅ **JPA/Hibernate** with H2 in-memory database
- ✅ **SpringDoc OpenAPI 3** documentation (modernized from SpringFox)
- ✅ **Self-consuming endpoints** (special requirement fulfilled)
- ✅ **Comprehensive validation** with custom business rules
- ✅ **Global exception handling** with structured error responses
- ✅ **Full observability** with Actuator, metrics, and logging
- ✅ **Complete test coverage** (unit + integration tests)

### Architecture Highlights
- 🏗️ **SOLID principles** implementation
- 🎯 **Design patterns**: Builder, Repository, Mapper
- 🔒 **Transaction management** for data consistency
- 📋 **DTO pattern** with validation annotations
- 🔍 **Pagination support** for large datasets
- 📊 **Metrics collection** with Micrometer

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| **Runtime** | Java | 8+ |
| **Framework** | Spring Boot | 2.7.18 |
| **Data Access** | Spring Data JPA | 2.7.18 |
| **Database** | H2 Database | Runtime |
| **Documentation** | SpringDoc OpenAPI 3 | 1.6.15 |
| **Testing** | JUnit 5 + Mockito | Latest |
| **Build Tool** | Maven | 3.6+ |
| **Observability** | Spring Actuator | 2.7.18 |

## 🚦 Quick Start

### Prerequisites
- Java 8 or higher
- Maven 3.6+
- Git

### Clone & Run
```bash
git clone https://github.com/jp-developer0/takehomesntndr.git
cd takehomesntndr
mvn spring-boot:run
```

### Access Points
- **Application**: http://localhost:8080/api/v1
- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui/index.html
- **API Docs**: http://localhost:8080/api/v1/v3/api-docs
- **H2 Console**: http://localhost:8080/api/v1/h2-console
- **Health Check**: http://localhost:8080/api/v1/actuator/health

### Database Configuration
```yaml
URL: jdbc:h2:mem:bankingdb
Username: sa
Password: (empty)
```

## 📋 API Documentation

### Main Endpoints

#### Account Management
```
POST   /api/v1/cuentas                    # Create account
GET    /api/v1/cuentas/{id}               # Get account by ID
GET    /api/v1/cuentas/numero/{number}    # Get account by number
PUT    /api/v1/cuentas/{id}               # Update account
DELETE /api/v1/cuentas/{id}               # Delete account
```

#### Banking Operations
```
POST   /api/v1/cuentas/{id}/debitar       # Debit account
POST   /api/v1/cuentas/{id}/acreditar     # Credit account
PATCH  /api/v1/cuentas/{id}/activar      # Activate account
PATCH  /api/v1/cuentas/{id}/desactivar   # Deactivate account
```

#### Search & Analytics
```
GET    /api/v1/cuentas                    # List all accounts (paginated)
GET    /api/v1/cuentas/buscar/titular     # Search by holder
GET    /api/v1/cuentas/buscar/tipo        # Search by account type
GET    /api/v1/cuentas/activas            # Get active accounts
GET    /api/v1/cuentas/estadisticas       # System statistics
```

#### Self-Consuming Endpoints (Special Requirement)
```
GET    /api/v1/consulta-interna/cuenta/{id}        # Internal account query
GET    /api/v1/consulta-interna/cuentas-activas    # Internal active accounts
GET    /api/v1/consulta-interna/estadisticas       # Internal statistics
GET    /api/v1/consulta-interna/resumen-completo   # Complete system summary
```

## 🏗️ Architecture

### Package Structure
```
com.santander.banking/
├── config/           # Configuration classes
├── controller/       # REST Controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA Entities
├── exception/       # Custom exceptions & handlers
├── repository/      # Data access layer
├── service/         # Business logic layer
└── util/            # Utilities and mappers
```

### Key Design Patterns
- **Repository Pattern**: Data access abstraction
- **Builder Pattern**: Entity construction
- **Mapper Pattern**: DTO-Entity conversion
- **Strategy Pattern**: Account type handling

## 🧪 Testing

### Run Tests
```bash
# Unit tests only
mvn test -Dtest=CuentaBancariaServiceImplTest

# Integration tests only
mvn test -Dtest=CuentaBancariaControllerIntegrationTest

# All tests
mvn test
```

### Test Coverage
- ✅ **Unit Tests**: Service layer business logic
- ✅ **Integration Tests**: Complete HTTP request-response cycle
- ✅ **Validation Tests**: Input validation and error handling
- ✅ **Database Tests**: JPA queries and transactions

## 📊 Monitoring & Observability

### Actuator Endpoints
- `/actuator/health` - Application health status
- `/actuator/metrics` - Application metrics
- `/actuator/info` - Application information
- `/actuator/env` - Environment properties
- `/actuator/prometheus` - Prometheus metrics

### Custom Metrics
- Account creation rate
- Transaction volume
- Error rates by endpoint
- Database connection pool stats

## 🔧 Configuration

### Application Properties
Key configurations in `application.yml`:
- Database settings
- JPA/Hibernate configuration
- Logging levels
- Actuator endpoints
- Custom banking properties

### Profiles
- `default`: Development profile with H2 console enabled
- `test`: Testing profile with isolated database

## 📈 Performance Features

- **Pagination**: Efficient large dataset handling
- **Database Indexing**: Optimized queries on account numbers
- **Connection Pooling**: HikariCP for database connections
- **Lazy Loading**: JPA lazy initialization where appropriate
- **Caching**: Strategic use of Spring Cache annotations

## 🛡️ Validation & Security

### Input Validation
- Account number format validation
- Holder name pattern validation (letters and spaces only)
- Balance validation (non-negative values)
- Currency code validation (ISO 4217)

### Business Rules
- Duplicate account number prevention
- Insufficient funds validation
- Account status validation for operations

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**JP Developer** - [GitHub](https://github.com/jp-developer0)

---

## 📋 Take Home Challenge Requirements ✅

This project fulfills all requirements from the original challenge:

- ✅ **Spring Boot microservice** with complete banking functionality
- ✅ **CRUD operations** for bank accounts
- ✅ **JPA/Hibernate** with H2 database integration
- ✅ **Swagger documentation** (modernized to OpenAPI 3)
- ✅ **Special requirement**: Self-consuming endpoints implemented
- ✅ **Java 8 compatibility** maintained
- ✅ **SOLID principles** applied throughout
- ✅ **Design patterns** implemented professionally
- ✅ **Complete test coverage** with unit and integration tests
- ✅ **Professional documentation** and clean code

Built with ❤️ for Santander Take Home Challenge 