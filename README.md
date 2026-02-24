# ✅ QuickTask API

A RESTful API built with **Spring Boot 4** for managing tasks and task lists, backed by a **MySQL** database. QuickTask provides a clean and organized way to create, read, update, and delete tasks, as well as group them into customizable task lists.

---

## 📋 Table of Contents

- [Description](#description)
- [Features](#features)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
  - [Tasks](#tasks)
  - [Task Lists](#task-lists)
- [Database Configuration](#database-configuration)
- [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [Author](#author)

---

## 📖 Description

QuickTask API is a backend application designed to help users manage their pending tasks efficiently. It allows users to:

- Create, view, update, and delete individual **tasks** with titles, descriptions, and completion status.
- Organize tasks into **task lists** for better categorization and management.
- Paginate and sort results for optimized data retrieval.

The project follows a **layered architecture** (Domain, Application, Infrastructure) inspired by clean architecture principles, ensuring a clear separation of concerns and maintainability.

---

## ✨ Features

- Full **CRUD operations** for Tasks and Task Lists
- **Pagination and sorting** support on all list endpoints
- **Input validation** using Jakarta Bean Validation
- **Custom exception handling** with meaningful error responses
- **Duplicate detection** for task titles and task list names
- **One-to-many relationship** between Task Lists and Tasks
- **UUID-based identifiers** for all entities
- **Automatic timestamps** for creation and update tracking
- **Transactional operations** with read-only optimization for queries
- **MapStruct** for type-safe object mapping between layers
- **Lombok** for reducing boilerplate code
- **Unit and integration tests** for service, repository, controller, and entity layers

---

## 🏗️ Architecture

The project is organized following a **layered/hexagonal architecture** pattern:

```
┌─────────────────────────────────────────────┐
│              Infrastructure Layer           │
│  ┌─────────────────┐  ┌──────────────────┐  │
│  │ REST Controllers │  │ JPA Repositories │  │
│  │  (Presentation)  │  │  (Persistence)   │  │
│  └────────┬────────┘  └────────┬─────────┘  │
├───────────┼────────────────────┼────────────┤
│           │  Application Layer │            │
│  ┌────────▼────────────────────▼─────────┐  │
│  │     Services / DTOs / Mappers         │  │
│  └────────────────┬──────────────────────┘  │
├───────────────────┼─────────────────────────┤
│                   │    Domain Layer          │
│  ┌────────────────▼──────────────────────┐  │
│  │   Domain Models / Repository Ports    │  │
│  └───────────────────────────────────────┘  │
└─────────────────────────────────────────────┘
```

Each bounded context (`task`, `tasklist`) has its own set of layers, promoting modularity and independent evolution.

---

## 🛠️ Technologies

| Technology         | Version | Purpose                                      |
|--------------------|---------|----------------------------------------------|
| Java               | 17      | Programming language                         |
| Spring Boot        | 4.0.2   | Application framework                        |
| Spring Data JPA    | —       | Database access and ORM                      |
| Spring Web MVC     | —       | REST API framework                           |
| Spring Validation  | —       | Request validation (Jakarta Bean Validation) |
| MySQL              | —       | Relational database                          |
| Hibernate          | —       | ORM / JPA implementation                     |
| MapStruct          | 1.6.2   | Object mapping between layers                |
| Lombok             | —       | Boilerplate code reduction                   |
| Maven              | —       | Build and dependency management              |
| JUnit 5            | —       | Unit and integration testing                 |

---

## 📌 Prerequisites

Before running the application, ensure you have the following installed:

- **Java 17** or higher — [Download](https://adoptium.net/)
- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi) (or use the included Maven Wrapper `mvnw`)
- **MySQL 8.0+** — [Download](https://dev.mysql.com/downloads/)

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-username/Spring_Boot_QuickTask_API.git
cd Spring_Boot_QuickTask_API/quicktask/quicktask
```

### 2. Configure the database

Make sure MySQL is running and accessible. The application will automatically create the database `tasks_db` if it does not exist.

Default database configuration (in `src/main/resources/application.properties`):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tasks_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
```

> ⚠️ Update the `username` and `password` values if your MySQL credentials differ.

### 3. Build the project

```bash
# Using Maven Wrapper (recommended)
./mvnw clean install

# Or using system Maven
mvn clean install
```

### 4. Run the application

```bash
# Using Maven Wrapper
./mvnw spring-boot:run

# Or using the compiled JAR
java -jar target/quicktask-0.0.1-SNAPSHOT.jar
```

The API will start on **http://localhost:8080**.

---

## 📡 API Endpoints

### Tasks

Base URL: `/api/v1/tasks`

| Method   | Endpoint               | Description                      | Request Body             | Response                |
|----------|------------------------|----------------------------------|--------------------------|-------------------------|
| `GET`    | `/api/v1/tasks`        | Get all tasks (paginated)        | —                        | `Page<TaskDTOResponse>` |
| `GET`    | `/api/v1/tasks/{id}`   | Get a task by ID                 | —                        | `TaskDetailDTOResponse` |
| `POST`   | `/api/v1/tasks`        | Create a new task                | `TaskDTOCreateRequest`   | `TaskDetailDTOResponse` |
| `PUT`    | `/api/v1/tasks/{id}`   | Update an existing task          | `TaskDTOUpdateRequest`   | `TaskDetailDTOResponse` |
| `DELETE` | `/api/v1/tasks/{id}`   | Delete a task                    | —                        | `204 No Content`        |

**Pagination Parameters** (for `GET` list endpoints):

| Parameter | Default | Description                                  |
|-----------|---------|----------------------------------------------|
| `page`    | `0`     | Zero-indexed page number                     |
| `size`    | `20`    | Number of items per page                     |
| `sort`    | `id,asc`| Sort criteria (`property,asc\|desc`)         |

**Example:**
```
GET /api/v1/tasks?page=0&size=10&sort=createdAt,desc
```

### Task Lists

Base URL: `/api/v1/task-lists`

| Method   | Endpoint                    | Description                          | Request Body                 | Response                    |
|----------|-----------------------------|--------------------------------------|------------------------------|-----------------------------|
| `GET`    | `/api/v1/task-lists`        | Get all task lists (paginated)       | —                            | `Page<TaskListDTOResponse>` |
| `GET`    | `/api/v1/task-lists/{id}`   | Get a task list by ID (with tasks)   | —                            | `TaskListDetailDTOResponse` |
| `POST`   | `/api/v1/task-lists`        | Create a new task list               | `TaskListDTOCreateRequest`   | `TaskListDetailDTOResponse` |
| `PUT`    | `/api/v1/task-lists/{id}`   | Update an existing task list         | `TaskListDTOUpdateRequest`   | `TaskListDetailDTOResponse` |
| `DELETE` | `/api/v1/task-lists/{id}`   | Delete a task list                   | —                            | `204 No Content`            |

---

## 🗄️ Database Configuration

The application uses **MySQL** as its primary database. Hibernate is configured with `ddl-auto=update`, meaning it will automatically create or update the database schema based on the JPA entity definitions.

| Property                              | Value                                                         |
|---------------------------------------|---------------------------------------------------------------|
| Database URL                          | `jdbc:mysql://localhost:3306/tasks_db?createDatabaseIfNotExist=true` |
| Driver                                | `com.mysql.cj.jdbc.Driver`                                    |
| Hibernate DDL Auto                    | `update`                                                      |
| Dialect                               | `org.hibernate.dialect.MySQLDialect`                          |
| Show SQL                              | `true`                                                        |

---

## 🧪 Running Tests

The project includes unit and integration tests covering:

- **Service layer** (`TaskServiceTest`)
- **Repository layer** (`TaskRepositoryTest`)
- **Controller layer** (`TaskControllerTest`)
- **Entity mapping** (`TaskEntityTest`)

To run all tests:

```bash
# Using Maven Wrapper
./mvnw test

# Or using system Maven
mvn test
```

---

## 📂 Project Structure

```
quicktask/
├── src/
│   ├── main/
│   │   ├── java/com/nsalazar/quicktask/
│   │   │   ├── QuicktaskApplication.java          # Application entry point
│   │   │   ├── shared/
│   │   │   │   └── exception/                     # Global exception handling
│   │   │   │       ├── ResourceNotFoundException.java
│   │   │   │       └── infrastructure/controller/
│   │   │   │           ├── ExceptionController.java
│   │   │   │           └── ErrorDTOResponse.java
│   │   │   ├── task/
│   │   │   │   ├── domain/                        # Task domain model & repository port
│   │   │   │   ├── application/                   # Task service, DTOs & mappers
│   │   │   │   └── infrastructure/                # Task REST controller & JPA repository
│   │   │   └── tasklist/
│   │   │       ├── domain/                        # TaskList domain model & repository port
│   │   │       ├── application/                   # TaskList service, DTOs & mappers
│   │   │       └── infrastructure/                # TaskList REST controller & JPA repository
│   │   └── resources/
│   │       └── application.properties             # Application configuration
│   └── test/
│       └── java/com/nsalazar/quicktask/           # Test classes
├── pom.xml                                        # Maven project configuration
├── mvnw / mvnw.cmd                                # Maven Wrapper scripts
└── HELP.md                                        # Spring Boot help references
```

---

## 👤 Author

**nsalazar**

---

## 📄 License

This project is for educational and personal use.
