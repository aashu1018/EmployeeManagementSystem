# Employee Management System

A comprehensive Spring Boot application for employee management with department organization, leave request handling, and a RabbitMQ-based notification system, all containerized with Docker.

## Architecture Overview

```
┌──────────────────────────────────────────────────────────┐
│                    Client (Postman/Browser)               │
└──────────────────────────┬───────────────────────────────┘
                           │ HTTP (Basic Auth)
┌──────────────────────────▼───────────────────────────────┐
│                 Spring Boot Application                   │
│  ┌─────────────┐  ┌──────────────┐  ┌────────────────┐  │
│  │ Controllers  │  │   Services   │  │   Security     │  │
│  │ (REST API)  │──│ (Business    │  │ (Basic Auth +  │  │
│  │             │  │  Logic)      │  │  Role-Based)   │  │
│  └─────────────┘  └──────┬───────┘  └────────────────┘  │
│                          │                                │
│  ┌───────────────┐  ┌────▼──────────┐                    │
│  │ Repositories  │  │  Messaging    │                    │
│  │ (JPA/MySQL)   │  │ (Publisher/   │                    │
│  │               │  │  Consumer)    │                    │
│  └───────┬───────┘  └──────┬────────┘                    │
└──────────┼─────────────────┼─────────────────────────────┘
           │                 │
    ┌──────▼──────┐   ┌──────▼──────┐
    │   MySQL 8   │   │  RabbitMQ   │
    │  Database   │   │   Broker    │
    └─────────────┘   └─────────────┘
```

## Tech Stack

| Technology      | Version      | Purpose                    |
|----------------|-------------|----------------------------|
| Java           | 11+         | Programming language       |
| Spring Boot    | 2.7.18      | Application framework      |
| Spring Security| 5.x         | Authentication & Authorization |
| Spring AMQP    | 2.x         | RabbitMQ messaging         |
| MySQL          | 8.0         | Relational database        |
| RabbitMQ       | 3.x         | Message broker             |
| Docker         | Latest      | Containerization           |
| Maven          | 3.x         | Build tool                 |
| JaCoCo         | 0.8.8       | Code coverage              |
| Lombok         | Latest      | Boilerplate reduction      |

## Prerequisites

- **Docker** & **Docker Compose** (recommended approach)
- OR **Java 11+**, **Maven 3.x**, **MySQL 8**, **RabbitMQ 3.x** (local setup)

## Quick Start (Docker)

### 1. Clone the repository
```bash
git clone <repository-url>
cd EmployeeManagementSystem
```

### 2. Build and start all services
```bash
docker-compose up --build -d
```

### 3. Verify services are running
```bash
docker-compose ps
```

### 4. Access the application
- **Application API**: http://localhost:8080
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)

### 5. Stop services
```bash
docker-compose down
```

### 6. Stop and remove volumes (clean reset)
```bash
docker-compose down -v
```

## Local Development Setup

### 1. Start MySQL
```bash
# Using Docker for MySQL only
docker run -d --name ems-mysql -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=ems_db \
  mysql:8.0
```

### 2. Start RabbitMQ
```bash
docker run -d --name ems-rabbitmq -p 5672:5672 -p 15672:15672 \
  rabbitmq:3-management
```

### 3. Build and run the application
```bash
mvn clean install
mvn spring-boot:run
```

### 4. Run tests
```bash
mvn test
```

### 5. Generate test coverage report
```bash
mvn test jacoco:report
# Report available at: target/site/jacoco/index.html
```

## API Documentation

### Authentication
All endpoints use **HTTP Basic Authentication**.

| Role  | Username | Password  | Permissions                              |
|-------|----------|-----------|------------------------------------------|
| ADMIN | admin    | admin123  | Full access to all operations             |
| USER  | user     | user123   | View employees, apply/view leave requests |

### Employee Endpoints

| Method | Endpoint              | Role          | Description                    |
|--------|-----------------------|---------------|--------------------------------|
| GET    | /api/employees        | ADMIN, USER   | List employees (paginated)     |
| GET    | /api/employees/{id}   | ADMIN, USER   | Get employee by ID             |
| POST   | /api/employees        | ADMIN         | Create new employee            |
| PUT    | /api/employees/{id}   | ADMIN         | Update employee                |
| DELETE | /api/employees/{id}   | ADMIN         | Delete employee                |

**GET /api/employees Query Parameters:**
| Parameter    | Default | Description                                  |
|-------------|---------|----------------------------------------------|
| page         | 0       | Page number (0-based)                        |
| sortBy       | name    | Sort field: name, department, joiningDate    |
| sortDir      | asc     | Sort direction: asc, desc                    |
| departmentId | null    | Filter by department ID                      |

### Department Endpoints

| Method | Endpoint                        | Role          | Description              |
|--------|---------------------------------|---------------|--------------------------|
| GET    | /api/departments                | ADMIN, USER   | List all departments     |
| POST   | /api/departments                | ADMIN         | Create new department    |
| GET    | /api/departments/{id}/employees | ADMIN, USER   | List department employees|

### Leave Management Endpoints

| Method | Endpoint                      | Role          | Description              |
|--------|-------------------------------|---------------|--------------------------|
| POST   | /api/leaves                   | ADMIN, USER   | Submit leave request     |
| PUT    | /api/leaves/{id}/status       | ADMIN         | Update leave status      |
| GET    | /api/leaves/employee/{empId}  | ADMIN, USER   | Get employee's leaves    |

### Sample API Requests

#### Create Department
```bash
curl -X POST http://localhost:8080/api/departments \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"departmentName": "Engineering", "location": "Building A"}'
```

#### Create Employee
```bash
curl -X POST http://localhost:8080/api/employees \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Smith",
    "email": "john.smith@company.com",
    "departmentId": 1,
    "salary": 85000,
    "joiningDate": "2023-01-15"
  }'
```

#### Get Employees (Paginated, Sorted)
```bash
curl http://localhost:8080/api/employees?page=0&sortBy=name&sortDir=asc \
  -u user:user123
```

#### Submit Leave Request
```bash
curl -X POST http://localhost:8080/api/leaves \
  -u user:user123 \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1,
    "startDate": "2024-03-15",
    "endDate": "2024-03-20",
    "reason": "Family vacation"
  }'
```

#### Approve Leave Request
```bash
curl -X PUT http://localhost:8080/api/leaves/1/status \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"status": "APPROVED"}'
```

## Database Schema

### Entity Relationship Diagram
```
┌─────────────────┐     ┌──────────────────────┐     ┌────────────────────┐
│   departments   │     │     employees        │     │  leave_requests    │
├─────────────────┤     ├──────────────────────┤     ├────────────────────┤
│ department_id PK│◄────│ department_id FK      │     │ leave_id PK       │
│ department_name │     │ employee_id PK        │◄────│ employee_id FK    │
│ location        │     │ full_name             │     │ start_date        │
│ created_at      │     │ email (unique)        │     │ end_date          │
└─────────────────┘     │ salary                │     │ status (ENUM)     │
                        │ joining_date          │     │ reason            │
                        │ created_at            │     │ created_at        │
                        │ updated_at            │     └────────────────────┘
                        └──────────────────────┘
```

## Message Queue (RabbitMQ)

### Notification Types

1. **New Employee Notification** - Published when a new employee is created
   - Queue: `ems.employee.notification.queue`
   - Contains: employee name, email, ID, department

2. **Leave Status Notification** - Published when a leave status is updated
   - Queue: `ems.leave.notification.queue`
   - Contains: employee name, leave dates, status, request ID

### RabbitMQ Configuration
- Exchange: `ems.notification.exchange` (Topic Exchange)
- Routing Keys: `ems.employee.notification`, `ems.leave.notification`

## Project Structure

```
src/
├── main/
│   ├── java/com/ems/employeemanagementsystem/
│   │   ├── config/          # RabbitMQ & Data loader configs
│   │   ├── controller/      # REST API controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA entities
│   │   │   └── enums/       # Enum types
│   │   ├── exception/       # Custom exceptions & global handler
│   │   ├── messaging/       # RabbitMQ publisher & consumer
│   │   ├── repository/      # Spring Data JPA repositories
│   │   ├── security/        # Spring Security configuration
│   │   └── service/         # Business logic layer
│   └── resources/
│       └── application.yml  # Application configuration
├── test/
│   ├── java/com/ems/employeemanagementsystem/
│   │   ├── controller/      # Controller unit tests
│   │   └── service/         # Service unit tests
│   └── resources/
│       └── application.yml  # Test configuration (H2)
├── scripts/
│   └── init.sql             # Database initialization script
├── Dockerfile               # Multi-stage Docker build
├── docker-compose.yml       # Docker Compose orchestration
├── pom.xml                  # Maven build configuration
└── README.md                # This file
```

## Assumptions

1. **Authentication**: In-memory user store with two predefined users (admin/user) using Basic Auth for simplicity
2. **Email Notifications**: Simulated via console logging (no actual SMTP server)
3. **Database Initialization**: Sample data is loaded on first startup via DataLoader component
4. **Leave Status Flow**: Leave requests can only be approved/rejected when in PENDING status
5. **Pagination**: Fixed page size of 10 records per page
6. **Employee Email**: Must be unique across the entire system
7. **Department Names**: Must be unique across the system
8. **Cascade Operations**: Deleting an employee also deletes their leave requests

## Testing

### Run all tests
```bash
mvn test
```

### Run specific test class
```bash
mvn test -Dtest=EmployeeServiceTest
```

### Generate coverage report
```bash
mvn clean test jacoco:report
```

### Test coverage includes:
- **Service Layer Tests**: DepartmentServiceTest, EmployeeServiceTest, LeaveRequestServiceTest
- **Controller Layer Tests**: DepartmentControllerTest, EmployeeControllerTest, LeaveRequestControllerTest
- **Messaging Tests**: NotificationPublisherTest
- **Security Tests**: Role-based access control validation

## Environment Variables

| Variable          | Default   | Description              |
|-------------------|-----------|--------------------------|
| MYSQL_HOST        | localhost | MySQL hostname           |
| MYSQL_PORT        | 3306      | MySQL port               |
| MYSQL_DB          | ems_db    | Database name            |
| MYSQL_USER        | root      | MySQL username           |
| MYSQL_PASSWORD    | root      | MySQL password           |
| RABBITMQ_HOST     | localhost | RabbitMQ hostname        |
| RABBITMQ_PORT     | 5672      | RabbitMQ port            |
| RABBITMQ_USER     | guest     | RabbitMQ username        |
| RABBITMQ_PASSWORD | guest     | RabbitMQ password        |
| ADMIN_USERNAME    | admin     | Admin login username     |
| ADMIN_PASSWORD    | admin123  | Admin login password     |
| USER_USERNAME     | user      | User login username      |
| USER_PASSWORD     | user123   | User login password      |
