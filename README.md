# Task Management System

A robust microservices-based system for managing tasks and users with notification-service, built with **Spring Boot 3**.

## 📋 Project Overview

The Task Management System allows you to manage users and tasks efficiently. It is designed as a distributed system where responsibilities are decoupled into separate microservices:

- **Task Service**: Manages taskDto lifecycles (create, read, update, delete, start, complete) with business logic.
- **User Service**: Handles user registration, authentication (JWT), profile management, and user banning.
- **Communication**: Services talk to each other using Spring Cloud OpenFeign. Also for asynchronous communication integrated Kafka for notification-service
- **Database**: A shared **PostgreSQL** instance stores all data. MongoDB for saving notification history.
- **Infrastructure**: Fully containerized with **Docker** for easy deployment.

## 🛠️ Tech Stack

| Component | Technology | Description |
|-----------|------------|-------------|
| **Core Framework** | Spring Boot 3.5.7 |
| **Language** | Java 17 |
| **Database** | PostgreSQL & MongoDB |
| **ORM** | Spring Data JPA | Data persistence and Hibernate |
| **Migrations** | Liquibase | Database schema version control
| **HTTP Client** | Spring Cloud OpenFeign |
| **Asynchronous Communication** | Kafka |
| **Authentication** | JWT (JSON Web Tokens) | Stateless authentication |
| **Containerization** | Docker & Docker Compose | Container orchestration |
| **Build Tool** | Maven | Dependency management |


## 🚀 Getting Started

### 📦 Installation & Run via Docker (Recommended)

1. **Clone the repository:**
   ```bash
   git clone https://github.com/nurassul/taskDto-management.git
   cd taskDto-management
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

## 🌐 API Endpoints

Once running, the services are accessible at:

### Task Service (`:8082`)

Base URL: `http://localhost:8082/tasks`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/tasks` | Get all tasks |
| `GET` | `/tasks/{id}` | Get taskDto by ID |
| `POST` | `/tasks` | Create new taskDto |
| `PUT` | `/tasks/{id}` | Update taskDto |
| `DELETE` | `/tasks/{id}` | Delete taskDto |
| `POST` | `/tasks/{id}/start` | Mark taskDto as IN_PROGRESS |
| `POST` | `/tasks/{id}/complete` | Mark taskDto as DONE |

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
| `GET` | `/users/private/{id}` | Get user by ID (private for taskDto-service) |
| `GET` | `/users/email/{email}` | Get user by email |
| `POST` | `/users/registration` | Register new user |
| `PUT` | `/users/{id}` | Update user |
| `DELETE` | `/users/{id}/delete` | Delete user |
| `POST` | `/users/{id}/banUser` | Ban user |

### MongoDB Express

To monitor Mongo database: `http://localhost:8085`


## 🔐 Authentication & Security

### JWT Implementation

The User Service uses **JWT** for authentication:

1. **Sign In** → POST `/auth/sign-in` with credentials
2. **Receive Token** → Returns `JwtAuthenticationDto` with access and refresh tokens
3. **Use Token** → Include in `Authorization: Bearer <token>` header for protected endpoints
4. **Refresh Token** → POST `/auth/refresh` when token expires


## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

---

**Author**: [Nurassul](https://github.com/nurassul)

**Happy Coding!**
