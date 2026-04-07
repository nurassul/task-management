# Task Management System

Microservice-based project for task management with authentication, admin functions, statistics, notifications, and a simple React frontend for API testing.

## What is inside

- `api-gateway` - single entry point for client requests.
- `user-service` - registration, auth (JWT), user profile, admin user operations.
- `task-service` - CRUD and lifecycle for tasks.
- `statistics-service` - aggregated task/user statistics from Kafka events.
- `notification-service` - notification processing from Kafka.
- `simplified-front` - simple test frontend for quick API verification (login, registration, dashboard, profile, create task, admin panel).
- `common` - shared models/events between services.

## Tech stack

- Java 21, Spring Boot 3, Spring Cloud Gateway
- PostgreSQL, MongoDB
- Kafka
- Liquibase
- React
- Docker Compose
- Observability: Prometheus, Grafana, Zipkin, Loki

## Quick start

### 1. Prepare `.env`

Root `.env` should contain:

```env
DB_USERNAME=postgres
DB_PASSWORD=postgres
DB_uNAME=user-db
DB_tNAME=task-db
GMAIL_USER=your_email@gmail.com
GMAIL_PASSWORD=your_app_password
```

### 2. Start all services

```bash
docker compose up -d --build
```

### 3. Open frontend

```bash
cd simplified-front
npm install
npm start
```

Frontend is configured to run on `http://localhost:3001` and use gateway `http://localhost:8080`.

## Main URLs

- API Gateway: `http://localhost:8080`
- Frontend: `http://localhost:3001`
- Grafana: `http://localhost:3000`
- Prometheus: `http://localhost:9090`
- Zipkin: `http://localhost:9411`
- Mongo Express: `http://localhost:8085`

## API routes (via Gateway)

Base URL: `http://localhost:8080`

### Auth/User

- `POST /auth/sign-in`
- `POST /auth/refresh`
- `POST /users/registration`
- `GET /users/email/{email}` (auth required)
- `PUT /users/{id}` (auth required)

### Admin-only (ROLE_ADMIN)

- `GET /users`
- `POST /users/{id}/banUser`
- `DELETE /users/{id}/delete`

### Tasks

- `GET /tasks`
- `GET /tasks/{id}`
- `POST /tasks`
- `PUT /tasks/{id}`
- `DELETE /tasks/{id}`
- `POST /tasks/{id}/start`
- `POST /tasks/{id}/complete`

### Statistics

- `GET /stats/task`
- `GET /stats/user/{userId}`

## Frontend pages

- `/login`
- `/register`
- `/dashboard`
- `/tasks/new`
- `/statistics`
- `/profile`
- `/admin/users` (ROLE_ADMIN only)

## How to create an admin user

Registration creates users with `ROLE_USER` by default.

1. Register normally through `/users/registration`.
2. Promote role in DB:

```bash
docker exec -it postgresdb psql -U postgres -d "user-db" -c "UPDATE users SET role='ROLE_ADMIN' WHERE email='admin@example.com';"
```

3. Re-login to receive JWT with `ROLE_ADMIN`.

## Useful commands

### Rebuild one service

```bash
docker compose up -d --build statistics-service
```

### Show container status

```bash
docker compose ps
```

### Stop project

```bash
docker compose down
```

## Notes

- Statistics are event-driven (Kafka), so small delay after task updates is expected.
- If user stats are empty for a new user, service initializes zero stats automatically.
