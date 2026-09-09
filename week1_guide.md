# Week 1: Setup, Git, Spring Boot, PostgreSQL Basics

## 🎯 Goal
By the end of this week, you should be able to:
- Set up a Spring Boot project from scratch
- Understand the project structure
- Connect to a PostgreSQL database
- Create basic REST APIs
- Use Git properly
- Test APIs with Postman

---

## Step 1: Create the Spring Boot Project

### Concept: What is Spring Initializr?
Spring Initializr (https://start.spring.io) is a web tool that generates a ready-to-use Spring Boot project with your chosen dependencies. Think of it as a project template generator.

### What to Do
1. Go to **https://start.spring.io**
2. Fill in these settings:

| Setting | Value |
|---------|-------|
| Project | Maven |
| Language | Java |
| Spring Boot | 3.x.x (latest stable) |
| Group | `com.banfico` |
| Artifact | `mini-banking` |
| Name | `mini-banking` |
| Package name | `com.banfico.minibanking` |
| Packaging | Jar |
| Java | 17 |

3. Add these **Dependencies** (click "Add Dependencies"):

| Dependency | Why? |
|------------|------|
| **Spring Web** | To build REST APIs (provides `@RestController`, `@GetMapping`, etc.) |
| **Spring Data JPA** | To interact with the database using Hibernate/JPA |
| **PostgreSQL Driver** | JDBC driver to connect Java to PostgreSQL |
| **Validation** | For request validation (`@NotNull`, `@Size`, etc.) |
| **Spring Boot DevTools** | Auto-restart during development |
| **Spring Boot Actuator** | Provides built-in health check endpoints |
| **Lombok** | Reduces boilerplate code (auto-generates getters/setters/constructors) |

4. Click **Generate** → Download the ZIP
5. Extract the ZIP into your `Bank-app` folder
6. Open the project in **IntelliJ IDEA** or **VS Code**

### 🧠 Understand Before Moving On
- Open `pom.xml` and look at all the dependencies. Each `<dependency>` maps to what you selected.
- Open the main class `MiniBankingApplication.java` — notice the `@SpringBootApplication` annotation. This is the entry point.
- Look at the folder structure — understand where `src/main/java` and `src/main/resources` are.

---

## Step 2: Understand the Project Structure

### Concept: Layered Architecture
Spring Boot projects follow a **layered architecture**. You'll create packages to organize your code:

```
src/main/java/com/banfico/minibanking/
├── MiniBankingApplication.java      ← Entry point
├── controller/                       ← REST API endpoints (receives HTTP requests)
├── service/                          ← Business logic
├── repository/                       ← Database access (JPA repositories)
├── entity/                           ← Database table mappings (JPA entities)
├── dto/                              ← Data Transfer Objects (request/response shapes)
├── config/                           ← Configuration classes
└── exception/                        ← Custom exception handling
```

### Why Layers?
| Layer | Responsibility | Analogy |
|-------|---------------|---------|
| **Controller** | Receives HTTP requests, returns responses | Front desk / receptionist |
| **Service** | Business logic, validation, orchestration | Manager who makes decisions |
| **Repository** | Talks to the database | Warehouse clerk who fetches/stores items |
| **Entity** | Represents a database table as a Java class | The blueprint of what's stored |
| **DTO** | Shapes what the API sends/receives (not tied to DB) | The form you fill out at the front desk |

### What to Do
1. Create these empty packages inside `com.banfico.minibanking`:
   - `controller`
   - `service`
   - `repository`
   - `entity`
   - `dto`
   - `config`
   - `exception`

> [!TIP]
> In IntelliJ: Right-click on `com.banfico.minibanking` → New → Package

---

## Step 3: Set Up PostgreSQL

### Concept: Why PostgreSQL?
PostgreSQL is a powerful, open-source relational database. Our Spring Boot app will store all banking data (customers, accounts, transactions) in PostgreSQL tables.

### Option A: Install PostgreSQL Locally
1. Download from https://www.postgresql.org/download/windows/
2. During installation, set a password for the `postgres` user (remember it!)
3. After installation, open **pgAdmin** or **psql** CLI
4. Create a new database:
   ```sql
   CREATE DATABASE mini_banking;
   ```

### Option B: Use Docker (Recommended)
If you have Docker Desktop installed, run this in your terminal:
```bash
docker run --name mini-banking-db -e POSTGRES_DB=mini_banking -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:15
```

### 🧠 Understand What This Docker Command Does
| Flag | Meaning |
|------|---------|
| `--name mini-banking-db` | Names the container |
| `-e POSTGRES_DB=mini_banking` | Creates a database called `mini_banking` |
| `-e POSTGRES_USER=postgres` | Sets the DB username |
| `-e POSTGRES_PASSWORD=postgres` | Sets the DB password |
| `-p 5432:5432` | Maps container port 5432 to your machine's port 5432 |
| `-d` | Runs in background (detached mode) |
| `postgres:15` | Uses PostgreSQL version 15 image |

### Verify It's Running
```bash
docker ps
```
You should see your `mini-banking-db` container listed.

---

## Step 4: Connect Spring Boot to PostgreSQL

### Concept: application.properties
Spring Boot uses `application.properties` (or `application.yml`) to configure the app. This is where you tell Spring Boot *how to connect to the database*.

### What to Do
Open `src/main/resources/application.properties` and add these properties:

```properties
# Application name
spring.application.name=mini-banking

# PostgreSQL connection
spring.datasource.url=jdbc:postgresql://localhost:5432/mini_banking
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Server port
server.port=8081
```

### 🧠 Understand Each Property

| Property | What it Does |
|----------|-------------|
| `spring.datasource.url` | JDBC URL pointing to your PostgreSQL database |
| `spring.datasource.username/password` | DB credentials |
| `spring.jpa.hibernate.ddl-auto=update` | Hibernate will auto-create/update tables based on your Entity classes |
| `spring.jpa.show-sql=true` | Logs the SQL queries Hibernate runs (great for learning!) |
| `server.port=8081` | App runs on port 8081 (8080 is kept free for Nginx later) |

> [!IMPORTANT]
> `ddl-auto=update` is fine for development. In production, you'd use `validate` or `none` and manage schema with migration tools like Flyway.

### Try Running the App
```bash
mvn spring-boot:run
```
Or run the main class from IntelliJ. You should see logs like:
```
Started MiniBankingApplication in X.X seconds
Tomcat started on port 8081
```

If the app starts successfully, your **PostgreSQL connection is working**! 🎉

---

## Step 5: Create Your First REST APIs

### Concept: REST Controller
A **Controller** is a Java class that handles HTTP requests. Spring Boot uses annotations to map URLs to methods.

### Key Annotations to Know

| Annotation | Purpose |
|------------|---------|
| `@RestController` | Marks the class as a REST API controller |
| `@RequestMapping("/api")` | Base URL prefix for all endpoints in this class |
| `@GetMapping("/path")` | Handles GET requests |
| `@PostMapping("/path")` | Handles POST requests |
| `@ResponseEntity` | Wraps your response with HTTP status code |

### What to Build
Create **two APIs** yourself:

#### API 1: Health Check
- **File**: `controller/HealthController.java`
- **Endpoint**: `GET /health`
- **Response**: A JSON object like `{"status": "UP", "service": "mini-banking"}`
- **Hint**: Create a simple Map or a DTO class, return it from a `@GetMapping` method

#### API 2: App Info
- **File**: `controller/InfoController.java` (or same controller)
- **Endpoint**: `GET /api/info`
- **Response**: A JSON object like `{"name": "Mini Banking System", "version": "1.0.0", "description": "Open Banking Consent Management System"}`

### 🧠 Key Concepts to Understand
1. **How does Spring Boot convert a Java object to JSON?**
   - Spring Boot includes Jackson library automatically. When you return a Java object (Map, DTO, etc.) from a `@RestController` method, Jackson serializes it to JSON.

2. **What is `@ResponseEntity`?**
   - It lets you control the HTTP status code. Example: `ResponseEntity.ok(yourObject)` returns status 200 with your object as the body.

3. **What happens when you hit `GET /health` in the browser?**
   - Browser sends an HTTP GET request → Tomcat (embedded server) receives it → Spring routes it to the matching `@GetMapping` method → Method returns a Java object → Jackson converts it to JSON → Response is sent back.

> [!TIP]
> Try implementing these APIs on your own first. If you get stuck, I'm here to help debug!

---

## Step 6: Test with Postman

### Concept: Why Postman?
Browsers can only do GET requests easily. For POST, PUT, DELETE, you need a tool like Postman to craft full HTTP requests with headers and body.

### What to Do
1. Download **Postman** from https://www.postman.com/downloads/
2. Create a new **Collection** called `Mini Banking`
3. Add requests:
   - `GET http://localhost:8081/health`
   - `GET http://localhost:8081/api/info`
4. Send them and verify you get the expected JSON responses
5. Check the **Status Code** (should be `200 OK`)
6. Look at the **Response Headers** — notice `Content-Type: application/json`

### Also Try: Spring Boot Actuator
Since we added the Actuator dependency, Spring Boot provides a built-in health endpoint:
- `GET http://localhost:8081/actuator/health`
- This should return `{"status":"UP"}`

Compare this with your custom `/health` endpoint.

---

## Step 7: Initialize Git

### Concept: Git Workflow
Git tracks changes to your code. For this project, follow this workflow:

```
1. git init                          ← Initialize repo
2. Create .gitignore                 ← Exclude build files, IDE files
3. git add .                         ← Stage all files
4. git commit -m "message"           ← Commit with a meaningful message
5. git remote add origin <url>       ← Link to GitHub/GitLab (optional)
6. git push -u origin main           ← Push to remote
```

### What to Do
1. Open terminal in your project root (`Bank-app/`)
2. Run `git init`
3. Create a `.gitignore` file with these entries:
   ```
   target/
   .idea/
   *.iml
   .vscode/
   *.class
   .DS_Store
   ```
4. Make your first commit:
   ```bash
   git add .
   git commit -m "Week 1: Initial Spring Boot project setup with PostgreSQL connection"
   ```

### Git Commit Best Practices
- Use **present tense**: "Add health endpoint" not "Added health endpoint"
- Be **specific**: "Add GET /health and GET /api/info endpoints" not "Add stuff"
- Commit **often**: Don't wait until everything is done

---

## Step 8: Write a README

### What to Do
Create a `README.md` in your project root with:

1. **Project Name**: Mini Banking / Open Banking Consent Management System
2. **Description**: A brief description of what this project is
3. **Tech Stack**: Java 17, Spring Boot, PostgreSQL, Maven
4. **Prerequisites**: What someone needs to run this (Java 17, PostgreSQL, Maven)
5. **How to Run**:
   - How to set up the database
   - How to run the application
   - What port it runs on
6. **API Endpoints**: List your endpoints with method, URL, and description

---

## ✅ Week 1 Checklist — Self-Evaluation

Before moving to Week 2, make sure you can answer **YES** to all of these:

| # | Question | ✅ |
|---|----------|-----|
| 1 | Can you explain what `@SpringBootApplication` does? | |
| 2 | Can you explain each dependency in `pom.xml` and why it's needed? | |
| 3 | Can you explain what `spring.jpa.hibernate.ddl-auto=update` does? | |
| 4 | Can you explain how a GET request flows from browser → controller → response? | |
| 5 | Is your app running and connecting to PostgreSQL without errors? | |
| 6 | Do your `/health` and `/api/info` endpoints return correct JSON? | |
| 7 | Can you test both endpoints in Postman and see 200 OK? | |
| 8 | Is your code committed to Git with meaningful messages? | |
| 9 | Does your README explain how to set up and run the project? | |
| 10 | Can you start/stop your PostgreSQL Docker container? | |

---

## 🆘 Common Issues & Debugging

| Problem | Likely Cause | Fix |
|---------|-------------|-----|
| App won't start, "Connection refused" | PostgreSQL isn't running | Check `docker ps` or start PostgreSQL |
| App won't start, "password authentication failed" | Wrong credentials in properties | Verify username/password match your DB |
| `404 Not Found` when hitting endpoint | Wrong URL or missing annotation | Check `@RequestMapping` path and method |
| No JSON response, just text | Missing `@RestController` | Make sure class has `@RestController` not `@Controller` |
| Port already in use | Another app using 8081 | Change `server.port` or kill the other process |

---

## 📚 Recommended Self-Learning Resources for Week 1

- [Spring Boot Official Getting Started Guide](https://spring.io/guides/gs/spring-boot/)
- [Spring Initializr](https://start.spring.io)
- [Baeldung - Spring Boot REST API](https://www.baeldung.com/spring-boot-start)
- [Docker - Getting Started](https://docs.docker.com/get-started/)
- [PostgreSQL Tutorial](https://www.postgresqltutorial.com/)

---

> [!NOTE]
> **Next Step**: Once you've completed all the steps above and your app is running with working APIs, let me know! I'll review your progress and then we'll move to **Week 2: Banking CRUD APIs** where we build the Customer, Account, Transaction, and Beneficiary entities.

**Ask me anytime** if you're stuck on any step — I'll help you debug without writing the code for you! 🚀
