# 📋 Task Management System

A robust, observability-ready microservices system for managing tasks and users. Built with **Spring Boot 3**, **Spring Cloud Gateway**, and a full monitoring stack.

## 🚀 Project Overview

The Task Management System is designed as a distributed architecture where responsibilities are decoupled into separate microservices. It features centralized routing, asynchronous communication, and comprehensive monitoring.

### Architecture

* **API Gateway**: The single entry point for all client requests, handling routing and cross-cutting concerns.
* **Task Service**: Manages task lifecycles (CRUD, status changes) with business logic.
* **User Service**: Handles registration, JWT authentication, and profile management.
* **Notification Service**: Listens to Kafka topics to send notifications (simulated/email) and logs history to MongoDB.

### Key Features

* **Communication**: Synchronous via **Spring Cloud OpenFeign**; Asynchronous via **Kafka**.
* **Database**: **PostgreSQL** for relational data (Users, Tasks); **MongoDB** for notification logs.
* **Schema Migration**: **Liquibase** for version-controlled database changes.
* **Observability**: Full tracing and logging with **Zipkin**, **Prometheus**, **Grafana**, and **Loki**.
* **Infrastructure**: Fully containerized with Docker & Docker Compose.

---

## 🛠️ Tech Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Core Framework** | Spring Boot 3.x | Microservices backbone |
| **Gateway** | Spring Cloud Gateway | Centralized API routing & load balancing |
| **Language** | Java 21 | Core programming language |
| **Database** | PostgreSQL & MongoDB | Relational & Document storage |
| **Migration** | Liquibase | Database schema version control |
| **Communication** | OpenFeign & Kafka | Sync HTTP & Async Event-driven messaging |
| **Security** | JWT (JSON Web Tokens) | Stateless authentication |
| **Tracing** | Zipkin | Distributed request tracing |
| **Metrics** | Prometheus | Time-series metrics collection |
| **Logs** | Grafana Loki | Log aggregation system |
| **Visualization** | Grafana | Dashboards for logs and metrics |
| **Containerization** | Docker | Container orchestration |

---

## 🚀 Getting Started

### 📦 Installation & Run via Docker (Recommended)

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/nurassul/task-management.git](https://github.com/nurassul/task-management.git)
    cd task-management
    ```

2.  **Build and start the ecosystem:**
    ```bash
    docker-compose up -d --build
    ```

    **This will start:**
    * PostgreSQL & MongoDB
    * Kafka & Zookeeper
    * Microservices (User, Task, Notification)
    * API Gateway (Port 8080)
    * Observability Stack (Grafana, Loki, Zipkin, Prometheus)

3.  **Verify running containers:**
    ```bash
    docker-compose ps
    ```

---

## 🌐 API Endpoints (via API Gateway)

All requests should be routed through the **API Gateway** on port **8080**.

**Base URL:** `http://localhost:8080`

### 👤 User Service Routes (`/users/**`, `/auth/**`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **POST** | `/auth/sign-in` | Login and receive JWT |
| **POST** | `/auth/refresh` | Refresh expired access token |
| **POST** | `/users/registration` | Register a new user |
| **GET** | `/users` | Get all users |
| **GET** | `/users/{id}` | Get user details |
| **POST** | `/users/{id}/ban` | Ban a specific user |

### 📝 Task Service Routes (`/tasks/**`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/tasks` | Get all tasks |
| **POST** | `/tasks` | Create a new task |
| **PUT** | `/tasks/{id}` | Update a task |
| **POST** | `/tasks/{id}/start` | Mark task as IN_PROGRESS |
| **POST** | `/tasks/{id}/complete` | Mark task as DONE |

---

## 📊 Observability & Monitoring

The system includes a pre-configured observability stack to monitor health, logs, and request flows.

| Tool | URL | Credentials (if any) | Description |
| :--- | :--- | :--- | :--- |
| **Grafana** | [http://localhost:3000](http://localhost:3000) | `admin` / `admin` | Visual dashboards for Metrics & Logs (Loki) |
| **Zipkin** | [http://localhost:9411](http://localhost:9411) | None | Distributed Tracing (Waterfall view of requests) |
| **Prometheus**| [http://localhost:9090](http://localhost:9090) | None | Raw metrics data |
| **Mongo Express**| [http://localhost:8085](http://localhost:8085)| `admin` / `admin` | GUI for MongoDB (Notification logs) |

### How to use Observability:
1.  **Tracing**: Go to **Zipkin**, click "Run Query". You will see the flow of requests (Gateway -> Task -> Kafka -> Notification).
2.  **Logs**: Go to **Grafana** -> Explore -> Select **Loki**. Filter by `{app="task-service"}` to see centralized logs.
3.  **Metrics**: Go to **Grafana** -> Dashboards to view JVM usage, request rates, and latency.

---

## 🗄️ Database Management (Liquibase)

Database schema changes are managed automatically using **Liquibase**.
* **Changelogs**: Located in `src/main/resources/db/changelog`.
* **Startup**: Liquibase automatically runs on container startup to apply any new SQL changes (tables, columns, constraints) to PostgreSQL.
* **Consistency**: Ensures all environments (Dev, Test, Prod) have the exact same database schema.

---

## 🔐 Security

* **Gateway Security**: The API Gateway acts as the first line of defense.
* **JWT**: The User Service issues tokens. Protected endpoints require the `Authorization: Bearer <token>` header.
* **CORS**: Configured globally in the Gateway to allow frontend communication.

---

## 📚 Resources

* [Spring Cloud Gateway Docs](https://spring.io/projects/spring-cloud-gateway)
* [Micrometer Tracing Docs](https://micrometer.io/docs/tracing)
* [Liquibase Documentation](https://docs.liquibase.com/)

**Author:** Nurassul
Happy Coding! 🚀
