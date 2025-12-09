[README_task.md](https://github.com/user-attachments/files/24054074/README_task.md)
# Task Management System

Микросервисная система управления задачами на основе **Spring Boot 3** с двумя независимыми API сервисами: Task Service и User Service. Система полностью контейнеризирована с использованием Docker и Docker Compose.

## 📋 Описание Проекта

Task Management System — это масштабируемая архитектура для управления задачами с разделением ответственности между сервисами:

- **Task Service** — управление задачами, их статусами и приоритетами
- **User Service** — управление пользователями и аутентификацией
- **PostgreSQL** — единая база данных для обоих сервисов
- **Docker Compose** — оркестрация контейнеров и сетевое взаимодействие

## 🛠️ Технологический Стек

| Компонент | Версия | Описание |
|-----------|--------|---------|
| Java | 17 | JDK для компиляции и выполнения |
| Spring Boot | 3.x | Framework для микросервисов |
| Spring Data JPA | 3.x | ORM для работы с БД |
| PostgreSQL | 13+ | Реляционная база данных |
| Maven | 3.8+ | Управление зависимостями |
| Docker | Latest | Контейнеризация приложения |
| Docker Compose | Latest | Оркестрация многоконтейнерных приложений |

## 📁 Структура Проекта

```
task-management/
├── task-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/taskmanagement/task/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── entity/
│   │   │   │   └── dto/
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── user-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/taskmanagement/user/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── entity/
│   │   │   │   └── dto/
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── docker-compose.yml
├── README.md
└── .gitignore
```

## 🚀 Установка и Запуск

### Требования

Перед началом убедитесь, что установлены:

- **Docker** (версия 20.10+)
- **Docker Compose** (версия 1.29+)
- **Git**

Проверить установку:
```bash
docker --version
docker-compose --version
```

### Шаги Установки

#### 1. Клонирование репозитория

```bash
git clone https://github.com/nurassul/task-management.git
cd task-management
```

#### 2. Настройка переменных окружения

Отредактируйте файл `docker-compose.yml` и убедитесь, что переменные окружения установлены правильно:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/taskdb
  SPRING_DATASOURCE_USERNAME: taskuser
  SPRING_DATASOURCE_PASSWORD: taskpass123
  SPRING_JPA_HIBERNATE_DDL_AUTO: update
```

#### 3. Запуск системы

В корневой папке проекта выполните:

```bash
docker-compose up --build
```

**Флаг `--build`** пересобирает образы перед запуском. При первом запуске это займёт несколько минут.

Для запуска в фоновом режиме:
```bash
docker-compose up -d --build
```

#### 4. Проверка статуса контейнеров

```bash
docker-compose ps
```

Вы должны увидеть три запущенных контейнера:
- `postgres` — база данных
- `task-service` — сервис задач
- `user-service` — сервис пользователей

## 🌐 Доступ к Сервисам

После успешного запуска система доступна по следующим адресам:

| Сервис | URL | Описание |
|--------|-----|---------|
| **Task Service** | `http://localhost:8080` | REST API для управления задачами |
| **User Service** | `http://localhost:8081` | REST API для управления пользователями |
| **PostgreSQL** | `localhost:5432` | База данных (внутри контейнера) |

### Пример запроса к Task Service

```bash
curl -X GET http://localhost:8080/api/tasks
```

### Пример запроса к User Service

```bash
curl -X GET http://localhost:8081/api/users
```

## 🗄️ База Данных

### Подключение к PostgreSQL

**Внутри контейнера** (автоматически при запуске):
- **URL:** `jdbc:postgresql://postgres:5432/taskdb`
- **Пользователь:** `taskuser`
- **Пароль:** `taskpass123`

**Снаружи контейнера** (например, из DBeaver):
- **Host:** `localhost`
- **Port:** `5432`
- **Database:** `taskdb`
- **User:** `taskuser`
- **Password:** `taskpass123`

### Создание таблиц

Таблицы создаются автоматически благодаря настройке:
```yaml
spring.jpa.hibernate.ddl-auto: update
```

JPA/Hibernate автоматически создаёт необходимые таблицы на основе Entity классов.

## 📊 Архитектура

### Диаграмма взаимодействия

```
┌─────────────────────────────────────────────────┐
│              Client / Frontend                   │
└────────────┬──────────────────┬─────────────────┘
             │                  │
      ┌──────▼──────┐    ┌──────▼──────┐
      │ Task Service│    │ User Service │
      │ :8080       │    │ :8081       │
      └──────┬──────┘    └──────┬──────┘
             │                  │
             └──────────┬───────┘
                        │
                ┌───────▼────────┐
                │  PostgreSQL    │
                │  Database      │
                └────────────────┘
```

### Микросервисная архитектура

Каждый сервис:
- 🔄 Независим и развивается отдельно
- 🔗 Общей базой данных (можно разделить при масштабировании)
- 🚀 Контейнеризирован в Docker
- 📡 Доступен через собственный порт

## 🛑 Остановка и Удаление

### Остановить контейнеры

```bash
docker-compose stop
```

### Удалить контейнеры

```bash
docker-compose down
```

### Полная очистка (включая данные БД)

⚠️ **Внимание:** Это удалит все данные в базе!

```bash
docker-compose down -v
```

## 🔧 Полезные Команды

### Просмотр логов

Все сервисы:
```bash
docker-compose logs -f
```

Только Task Service:
```bash
docker-compose logs -f task-service
```

Только User Service:
```bash
docker-compose logs -f user-service
```

### Перестройка образов

```bash
docker-compose build --no-cache
```

### Перезагрузка контейнеров

```bash
docker-compose restart
```

### Выполнить команду в контейнере

```bash
docker-compose exec task-service bash
docker-compose exec user-service bash
docker-compose exec postgres psql -U taskuser -d taskdb
```

## 🐛 Решение Проблем

### Проблема: Порты уже занимаются

**Ошибка:** `Error response from daemon: Ports are not available`

**Решение:** Измените порты в `docker-compose.yml`:
```yaml
services:
  task-service:
    ports:
      - "8080:8080"  # Измените первый номер на свободный
  
  user-service:
    ports:
      - "8081:8081"  # Измените первый номер на свободный
```

### Проблема: Контейнер постоянно перезагружается

**Решение:** Проверьте логи:
```bash
docker-compose logs task-service
docker-compose logs user-service
```

Обычно это означает, что приложение не может подключиться к БД. Убедитесь, что:
1. PostgreSQL контейнер запущен
2. Переменные окружения правильно установлены
3. Подождите несколько секунд — БД может инициализироваться

### Проблема: `Connection refused` при подключении к БД

**Решение:** Контейнеры могут запуститься неодновременно. Добавьте задержку в `docker-compose.yml`:

```yaml
services:
  task-service:
    depends_on:
      postgres:
        condition: service_healthy
```

## 📝 Конфигурация

### application.yml для Task Service

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/taskdb
    username: taskuser
    password: taskpass123
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  application:
    name: task-service

server:
  port: 8080
```

### application.yml для User Service

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/taskdb
    username: taskuser
    password: taskpass123
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  application:
    name: user-service

server:
  port: 8081
```

## 🔐 Безопасность

### Переменные окружения

Не коммитьте пароли в git! Создайте файл `.env`:

```bash
POSTGRES_USER=taskuser
POSTGRES_PASSWORD=taskpass123
POSTGRES_DB=taskdb
SPRING_DATASOURCE_USERNAME=taskuser
SPRING_DATASOURCE_PASSWORD=taskpass123
```

Затем используйте в `docker-compose.yml`:
```yaml
environment:
  - SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
  - SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
```


## 🤝 Разработка

### Локальное развитие без Docker

Если вы хотите разрабатывать локально:

1. Установите PostgreSQL локально
2. Обновите `application.yml` с локальными параметрами подключения
3. Запустите сервисы через IDE или Maven:

```bash
cd task-service
mvn spring-boot:run

# В другом терминале
cd user-service
mvn spring-boot:run
```

### Добавление новых зависимостей

1. Отредактируйте `pom.xml` в нужном сервисе
2. Пересоберите образ:
```bash
docker-compose build --no-cache task-service
```
3. Перезагрузите контейнер:
```bash
docker-compose up
```

## 📊 Мониторинг

### Проверка здоровья сервисов

```bash
curl -X GET http://localhost:8080/actuator/health
curl -X GET http://localhost:8081/actuator/health
```

(Требует наличия Spring Boot Actuator в зависимостях)


### Собрать JAR файлы

```bash
# Task Service
cd task-service
mvn clean package

# User Service
cd user-service
mvn clean package
```

### Загрузить на регистр (Registry)

```bash
docker login
docker tag task-service:latest yourusername/task-service:latest
docker push yourusername/task-service:latest

docker tag user-service:latest yourusername/user-service:latest
docker push yourusername/user-service:latest
```

## 🤖 CI/CD

Для интеграции с GitHub Actions создайте `.github/workflows/build.yml`:

```yaml
name: Build and Test

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:13
        env:
          POSTGRES_PASSWORD: postgres
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - uses: actions/checkout@v2
      
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      
      - name: Build Task Service
        run: cd task-service && mvn clean package
      
      - name: Build User Service
        run: cd user-service && mvn clean package
```


## 👤 Автор

**nurassul** — GitHub: [@nurassul](https://github.com/nurassul)

## 📞 Поддержка

Если у вас возникли проблемы:

1. Проверьте [документацию Docker Compose](https://docs.docker.com/compose/)
2. Просмотрите логи контейнеров: `docker-compose logs`
3. Откройте Issue на [GitHub Issues](https://github.com/nurassul/task-management/issues)

---

**Happy Coding! 🎉**
