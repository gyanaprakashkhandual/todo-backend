# 🚀 Todo App — Spring Boot + PostgreSQL Backend Setup

---

## 📁 Project Structure

```
todo-backend/
├── src/
│   ├── main/
│   │   ├── java/com/todo/app/
│   │   │   ├── TodoApplication.java
│   │   │   ├── controller/
│   │   │   │   └── TodoController.java
│   │   │   ├── model/
│   │   │   │   └── Todo.java
│   │   │   ├── repository/
│   │   │   │   └── TodoRepository.java
│   │   │   └── service/
│   │   │       └── TodoService.java
│   │   └── resources/
│   │       └── application.properties
├── pom.xml
└── .env (optional, for local dev)
```

---

## 🛠️ Step 1 — Create the Spring Boot Project

### Option A: Using Spring Initializr (Recommended)

1. Go to [https://start.spring.io](https://start.spring.io)
2. Fill in the following:

| Field       | Value                 |
| ----------- | --------------------- |
| Project     | Maven                 |
| Language    | Java                  |
| Spring Boot | 3.2.x (latest stable) |
| Group       | `com.todo`            |
| Artifact    | `app`                 |
| Packaging   | Jar                   |
| Java        | 17 or 21              |

3. Add these **Dependencies**:
   - `Spring Web`
   - `Spring Data JPA`
   - `PostgreSQL Driver`
   - `Spring Boot DevTools` _(optional, for hot reload)_
   - `Lombok` _(optional, reduces boilerplate)_
   - `Validation`

4. Click **Generate** → Download & extract the ZIP.

### Option B: Using Spring Boot CLI

```bash
spring init \
  --dependencies=web,data-jpa,postgresql,devtools,lombok,validation \
  --group-id=com.todo \
  --artifact-id=app \
  --java-version=17 \
  todo-backend
```

---

## 📦 Step 2 — pom.xml Dependencies

```xml
<dependencies>

    <!-- Spring Web (REST APIs) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Data JPA (ORM) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Lombok (reduces boilerplate) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- DevTools (hot reload) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>

    <!-- Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

</dependencies>
```

---

## 🗄️ Step 3 — Get Your PostgreSQL Database URL

You have two options: **Local** or **Cloud (Free Tier)**.

---

### 🖥️ Option A: Local PostgreSQL

#### Install PostgreSQL

**Windows:**

- Download from [https://www.postgresql.org/download/windows/](https://www.postgresql.org/download/windows/)
- Run the installer, set a password for the `postgres` user (remember this!), and use default port `5432`.

**macOS:**

```bash
brew install postgresql@16
brew services start postgresql@16
```

**Linux (Ubuntu/Debian):**

```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### Create Database & User

```bash
# Login to PostgreSQL
sudo -u postgres psql        # Linux
psql -U postgres             # Windows / macOS

# Inside psql shell:
CREATE DATABASE tododb;
CREATE USER todouser WITH PASSWORD 'yourpassword';
GRANT ALL PRIVILEGES ON DATABASE tododb TO todouser;
\q
```

#### Your Local Database URL

```
jdbc:postgresql://localhost:5432/tododb
```

---

### ☁️ Option B: Free Cloud PostgreSQL (Neon — Recommended)

**Neon** offers a free PostgreSQL database with no credit card required.

1. Go to [https://neon.tech](https://neon.tech) → Sign up (free)
2. Click **"New Project"** → Give it a name (e.g., `todo-app`)
3. Choose a region closest to you
4. Once created, go to **Dashboard → Connection Details**
5. Select **"Connection string"** → Copy it

It will look like:

```
postgresql://todouser:yourpassword@ep-xyz-123456.us-east-2.aws.neon.tech/tododb?sslmode=require
```

**Convert it to JDBC format for Spring Boot:**

```
jdbc:postgresql://ep-xyz-123456.us-east-2.aws.neon.tech/tododb
```

> ⚠️ Keep the username, password, and host from the original connection string.

---

### ☁️ Option C: Other Free Cloud Options

| Service         | Free Tier          | Link                                           |
| --------------- | ------------------ | ---------------------------------------------- |
| **Neon**        | 500 MB, 1 project  | [neon.tech](https://neon.tech)                 |
| **Supabase**    | 500 MB, 2 projects | [supabase.com](https://supabase.com)           |
| **Railway**     | $5 credit/month    | [railway.app](https://railway.app)             |
| **ElephantSQL** | 20 MB free         | [elephantsql.com](https://www.elephantsql.com) |

All of them give you a **connection string** in the format:

```
postgresql://username:password@host:port/database
```

---

## ⚙️ Step 4 — application.properties Configuration

`src/main/resources/application.properties`

```properties
# ─────────────────────────────────────────
# Server
# ─────────────────────────────────────────
server.port=8080

# ─────────────────────────────────────────
# PostgreSQL Database
# ─────────────────────────────────────────
spring.datasource.url=jdbc:postgresql://localhost:5432/tododb
spring.datasource.username=todouser
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=org.postgresql.Driver

# ─────────────────────────────────────────
# JPA / Hibernate
# ─────────────────────────────────────────
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# ─────────────────────────────────────────
# App Name
# ─────────────────────────────────────────
spring.application.name=todo-backend
```

> 💡 `ddl-auto=update` auto-creates/updates tables based on your entities. Use `validate` in production.

---

### 🔐 For Cloud DB (Neon/Supabase — SSL Required)

```properties
spring.datasource.url=jdbc:postgresql://your-host/tododb?sslmode=require
spring.datasource.username=your_username
spring.datasource.password=your_password
```

---

## 🧱 Step 5 — Core Code Files

### `TodoApplication.java`

```java
package com.todo.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TodoApplication {
    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }
}
```

---

### `model/Todo.java`

```java
package com.todo.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "todos")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

---

### `repository/TodoRepository.java`

```java
package com.todo.app.repository;

import com.todo.app.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByCompleted(boolean completed);
}
```

---

### `service/TodoService.java`

```java
package com.todo.app.service;

import com.todo.app.model.Todo;
import com.todo.app.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public Optional<Todo> getTodoById(Long id) {
        return todoRepository.findById(id);
    }

    public Todo createTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    public Optional<Todo> updateTodo(Long id, Todo updatedTodo) {
        return todoRepository.findById(id).map(todo -> {
            todo.setTitle(updatedTodo.getTitle());
            todo.setDescription(updatedTodo.getDescription());
            todo.setCompleted(updatedTodo.isCompleted());
            return todoRepository.save(todo);
        });
    }

    public boolean deleteTodo(Long id) {
        if (todoRepository.existsById(id)) {
            todoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Todo> getTodosByStatus(boolean completed) {
        return todoRepository.findByCompleted(completed);
    }
}
```

---

### `controller/TodoController.java`

```java
package com.todo.app.controller;

import com.todo.app.model.Todo;
import com.todo.app.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*") // Allow mobile app calls
public class TodoController {

    @Autowired
    private TodoService todoService;

    // GET all todos
    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        return ResponseEntity.ok(todoService.getAllTodos());
    }

    // GET todo by ID
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        return todoService.getTodoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST create todo
    @PostMapping
    public ResponseEntity<Todo> createTodo(@Valid @RequestBody Todo todo) {
        Todo created = todoService.createTodo(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT update todo
    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @Valid @RequestBody Todo todo) {
        return todoService.updateTodo(id, todo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE todo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        if (todoService.deleteTodo(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // GET by status
    @GetMapping("/status")
    public ResponseEntity<List<Todo>> getTodosByStatus(@RequestParam boolean completed) {
        return ResponseEntity.ok(todoService.getTodosByStatus(completed));
    }
}
```

---

## 🌐 Step 6 — API Endpoints Reference

| Method   | Endpoint                            | Description       |
| -------- | ----------------------------------- | ----------------- |
| `GET`    | `/api/todos`                        | Get all todos     |
| `GET`    | `/api/todos/{id}`                   | Get a single todo |
| `POST`   | `/api/todos`                        | Create a new todo |
| `PUT`    | `/api/todos/{id}`                   | Update a todo     |
| `DELETE` | `/api/todos/{id}`                   | Delete a todo     |
| `GET`    | `/api/todos/status?completed=false` | Filter by status  |

### Sample Request Body (POST/PUT)

```json
{
  "title": "Buy groceries",
  "description": "Milk, eggs, bread",
  "completed": false
}
```

---

## ▶️ Step 7 — Run the Application

```bash
# Navigate to project directory
cd todo-backend

# Run with Maven
./mvnw spring-boot:run

# Or build JAR and run
./mvnw clean package
java -jar target/app-0.0.1-SNAPSHOT.jar
```

Server starts at: `http://localhost:8080`

---

## 🧪 Step 8 — Test with cURL

```bash
# Get all todos
curl http://localhost:8080/api/todos

# Create a todo
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"Buy milk","description":"From the store","completed":false}'

# Update a todo
curl -X PUT http://localhost:8080/api/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Buy milk","description":"Updated","completed":true}'

# Delete a todo
curl -X DELETE http://localhost:8080/api/todos/1
```

---

## ✅ Summary Checklist

- [ ] Created Spring Boot project via Spring Initializr
- [ ] Added PostgreSQL, JPA, Web, Lombok dependencies in `pom.xml`
- [ ] Set up PostgreSQL database (local or cloud)
- [ ] Configured `application.properties` with DB URL, username, password
- [ ] Created `Todo` entity, `TodoRepository`, `TodoService`, `TodoController`
- [ ] Ran the app with `./mvnw spring-boot:run`
- [ ] Tested APIs with cURL or Postman

---

> 💡 **Tip:** Use [Postman](https://www.postman.com/) or [Insomnia](https://insomnia.rest/) for a visual API testing experience instead of cURL.
