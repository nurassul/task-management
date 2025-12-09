# Task Management System

A robust microservices-based system for managing tasks and users, built with **Spring Boot 3**. This project demonstrates a scalable architecture using modern practices like **RestClient** for inter-service communication, **Docker Compose** for orchestration, and **PostgreSQL** as the persistent data store.

## 📋 Project Overview

The Task Management System allows you to manage users and tasks efficiently. It is designed as a distributed system where responsibilities are decoupled into separate microservices:

- **Task Service**: Manages task lifecycles (create, read, update, delete, start, complete) with business logic.
- **User Service**: Handles user registration, authentication (JWT), profile management, and user banning.
- **Communication**: Services talk to each other using **Spring's RestClient** for synchronous REST calls.
- **Database**: A shared **PostgreSQL** instance stores all data.
- **Infrastructure**: Fully containerized with **Docker** for easy deployment.

## 🛠️ Tech Stack

| Component | Technology | Description |
|-----------|------------|-------------|
| **Core Framework** | Spring Boot 3.x | Application framework |
| **Language** | Java 17 | Core programming language |
| **Database** | PostgreSQL 15 | Relational database |
| **ORM** | Spring Data JPA | Data persistence and Hibernate |
| **HTTP Client** | Spring RestClient | HTTP client for microservice communication |
| **Authentication** | JWT (JSON Web Tokens) | Stateless authentication |
| **Containerization** | Docker & Docker Compose | Container orchestration |
| **Build Tool** | Maven | Dependency management |

## 📁 Project Structure

```
task-management/
├── task-service/               # Task management microservice
│   ├── src/main/java/com/project/taskservice/
│   │   ├── config/
│   │   │   └── AppConfig.java              # RestClient bean configuration
│   │   ├── controllers/
│   │   │   └── TaskController.java         # Task REST endpoints
│   │   ├── service/
│   │   │   └── TaskService.java            # Business logic
│   │   ├── model/
│   │   │   └── Task.java                   # Task entity
│   │   └── ...
│   ├── Dockerfile              # Docker build instruction
│   └── pom.xml                 # Maven dependencies
│
├── user-service/               # User management microservice
│   ├── src/main/java/com/project/userservice/
│   │   ├── controller/
│   │   │   ├── AuthController.java         # Authentication endpoints
│   │   │   └── UserController.java         # User management endpoints
│   │   ├── service/
│   │   │   └── UserService.java            # Business logic
│   │   ├── dto/
│   │   │   ├── User.java
│   │   │   ├── UserCredentialsDto.java
│   │   │   └── JwtAuthenticationDto.java
│   │   └── ...
│   ├── Dockerfile              # Docker build instruction
│   └── pom.xml                 # Maven dependencies
│
├── docker-compose.yml          # Container orchestration config
└── README.md                   # Project documentation
```

## 🚀 Getting Started

### Prerequisites

Ensure you have the following installed:
- **Docker** & **Docker Compose**
- **Java 17** (optional, for local development)
- **Maven** (optional, for local builds)

### 📦 Installation & Run via Docker (Recommended)

1. **Clone the repository:**
   ```bash
   git clone https://github.com/nurassul/task-management.git
   cd task-management
   ```

2. **Build and start services:**
   ```bash
   docker-compose up --build
   ```
   This will:
   - Build Docker images for both services
   - Start PostgreSQL database
   - Start Task Service on port 8082
   - Start User Service on port 8081

3. **Verify running containers:**
   ```bash
   docker-compose ps
   ```
   You should see `task-service`, `user-service`, and `postgres` running.

## 🌐 API Endpoints

Once running, the services are accessible at:

### Task Service (`:8082`)

Base URL: `http://localhost:8082/tasks`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/tasks` | Get all tasks |
| `GET` | `/tasks/{id}` | Get task by ID |
| `POST` | `/tasks` | Create new task |
| `PUT` | `/tasks/{id}` | Update task |
| `DELETE` | `/tasks/{id}` | Delete task |
| `POST` | `/tasks/{id}/start` | Mark task as IN_PROGRESS |
| `POST` | `/tasks/{id}/complete` | Mark task as DONE |

### User Service (`:8081`)

#### Authentication Endpoints

Base URL: `http://localhost:8081/auth`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/auth/sign-in` | Login and get JWT token |
| `POST` | `/auth/refresh` | Refresh expired JWT token |

#### User Management Endpoints

Base URL: `http://localhost:8081/users`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/users` | Get all users |
| `GET` | `/users/{id}` | Get user by ID (public) |
| `GET` | `/users/private/{id}` | Get user by ID (private for task-service) |
| `GET` | `/users/email/{email}` | Get user by email |
| `POST` | `/users/registration` | Register new user |
| `PUT` | `/users/{id}` | Update user |
| `DELETE` | `/users/{id}/delete` | Delete user |
| `POST` | `/users/{id}/banUser` | Ban user |

## 📡 Inter-Service Communication

### RestClient Configuration

The `AppConfig` class in `task-service` defines a `RestClient` bean:

```java
@Configuration
public class AppConfig {
    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }
}
```

The **Task Service** uses `RestClient` to communicate with **User Service** for:
- Validating user existence
- Fetching user information
- Assigning tasks to users

Example usage pattern:
```java
restClient.get()
    .uri("http://user-service:8081/users/{id}")
    .retrieve()
    .body(User.class);
```

## 🔐 Authentication & Security

### JWT Implementation

The User Service uses **JWT** for authentication:

1. **Sign In** → POST `/auth/sign-in` with credentials
2. **Receive Token** → Returns `JwtAuthenticationDto` with access and refresh tokens
3. **Use Token** → Include in `Authorization: Bearer <token>` header for protected endpoints
4. **Refresh Token** → POST `/auth/refresh` when token expires


## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring RestClient Guide](https://spring.io/blog/2023/07/13/new-in-spring-6-1-restclient)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

---

**Author**: [Nurassul](https://github.com/nurassul)

**Happy Coding!**
