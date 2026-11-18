<div align="center">

```
╔══════════════════════════════════════════════════════════════════╗
║                                                                  ║
║        💸  EXPENSE TRACKER API  💸                               ║
║                                                                  ║
║     Track Income & Expenses · REST API · MySQL · Spring Boot     ║
║                                                                  ║
╚══════════════════════════════════════════════════════════════════╝
```

[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red?style=for-the-badge&logo=apachemaven)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)

**A production-ready REST API for managing personal income & expense transactions,  
built with Java 17, Spring Boot 3, JDBC, and MySQL.**

</div>

---

## 📌 Table of Contents

- [About the Project](#-about-the-project)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [API Endpoints](#-api-endpoints)
- [API Outputs](#-api-outputs)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [Design Decisions](#-design-decisions)

---

## 🧾 About the Project

The **Expense Tracker API** is a backend REST API that lets users:

- ✅ **Add** income and expense transactions
- 📋 **View** all transactions with optional filters (by category or type)
- 🔍 **Search** transactions by keyword
- 📅 **Filter** by date range
- ✏️ **Update** existing transactions
- 🗑️ **Delete** transactions
- 💰 **Get total** amount (overall or per category)
- 📊 **Get summary** — total income, total expense, and net balance

> All data is **persistently stored in a MySQL database** using Spring JDBC (`JdbcTemplate`).  
> Transactions survive server restarts.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Database | MySQL 8.0 |
| Data Access | Spring JDBC (`JdbcTemplate`) |
| Validation | Spring Validation (`jakarta.validation`) |
| Build Tool | Maven |
| API Testing | Postman |

---

## 📁 Project Structure

```
Expense-Tracker-API/
├── pom.xml
├── README.md
├── PROJECT_EXPLANATION.txt          ← (local only, not pushed)
└── src/
    └── main/
        ├── java/com/expensetracker/
        │   ├── ExpenseTrackerApplication.java   ← Entry point
        │   ├── controller/
        │   │   └── ExpenseController.java       ← REST endpoints
        │   ├── service/
        │   │   └── ExpenseService.java          ← Business logic
        │   ├── repository/
        │   │   └── ExpenseRepository.java       ← SQL queries (JDBC)
        │   ├── model/
        │   │   └── Expense.java                 ← Data model
        │   ├── dto/
        │   │   ├── ExpenseRequest.java          ← Input DTO (validated)
        │   │   ├── TotalResponse.java           ← Output DTO
        │   │   ├── SummaryResponse.java         ← Dashboard DTO
        │   │   └── ApiError.java                ← Error response DTO
        │   └── exception/
        │       ├── ExpenseNotFoundException.java
        │       └── GlobalExceptionHandler.java  ← @RestControllerAdvice
        └── resources/
            ├── application.yml                  ← Config (DB, port)
            ├── schema.sql                       ← Auto-runs on startup
            └── static/
                └── index.html                   ← Simple UI
```

---

## 🗄️ Database Schema

```sql
CREATE TABLE IF NOT EXISTS transactions (
    id          VARCHAR(36)    NOT NULL,   -- UUID primary key
    title       VARCHAR(255)   NOT NULL,   -- e.g. "Lunch at Cafe"
    amount      DECIMAL(15, 2) NOT NULL,   -- e.g. 250.00
    type        VARCHAR(10)    NOT NULL,   -- "INCOME" or "EXPENSE"
    category    VARCHAR(100)   NOT NULL,   -- e.g. "Food", "Salary"
    txn_date    DATE           NOT NULL,   -- yyyy-MM-dd
    notes       VARCHAR(500),              -- Optional details
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
```

> Spring Boot runs `schema.sql` automatically on startup (`sql.init.mode: always`), so the table is created if it doesn't exist — **no manual setup needed**.

---

## 🔗 API Endpoints

**Base URL:** `http://localhost:8080/api/expenses`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/expenses` | Add a new transaction |
| `GET` | `/api/expenses` | Get all transactions (optional `?category=` & `?type=`) |
| `GET` | `/api/expenses/{id}` | Get a single transaction by ID |
| `PUT` | `/api/expenses/{id}` | Update a transaction |
| `DELETE` | `/api/expenses/{id}` | Delete a transaction |
| `GET` | `/api/expenses/search?keyword=` | Search by title keyword |
| `GET` | `/api/expenses/range?startDate=&endDate=` | Filter by date range |
| `GET` | `/api/expenses/total` | Get total amount (all or `?category=`) |
| `GET` | `/api/expenses/summary` | Get income / expense / net balance summary |

---

### 📨 Request & Response Examples

<details>
<summary><strong>POST /api/expenses — Add a transaction</strong></summary>

**Request Body:**
```json
{
  "title": "Grocery Shopping",
  "amount": 850.00,
  "type": "EXPENSE",
  "category": "Food",
  "date": "2026-11-18",
  "notes": "Weekly groceries from supermarket"
}
```

**Response `201 Created`:**
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "title": "Grocery Shopping",
  "amount": 850.00,
  "type": "EXPENSE",
  "category": "Food",
  "date": "2026-11-18",
  "notes": "Weekly groceries from supermarket",
  "createdAt": "2026-11-18T20:38:00"
}
```
</details>

<details>
<summary><strong>GET /api/expenses/summary — Financial dashboard</strong></summary>

**Response `200 OK`:**
```json
{
  "totalIncome": 50000.00,
  "totalExpense": 12350.00,
  "netBalance": 37650.00
}
```
</details>

<details>
<summary><strong>GET /api/expenses/total?category=Food — Category total</strong></summary>

**Response `200 OK`:**
```json
{
  "category": "Food",
  "total": 3200.00
}
```
</details>

<details>
<summary><strong>404 Error Response</strong></summary>

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Expense not found with id: abc-123",
  "timestamp": "2026-11-18T20:45:00"
}
```
</details>

---

## 📸 API Outputs

> Screenshots taken from Postman showing live API responses.

### ➕ POST — Add Transaction
![Add Transaction](src/main/asserts/Screenshot%202026-11-18%20203818.png)

### 📋 GET — All Transactions
![Get All](src/main/asserts/Screenshot%202026-11-18%20204035.png)

### 🔍 GET — Single Transaction by ID
![Get By ID](src/main/asserts/Screenshot%202026-11-18%20204118.png)

### ✏️ PUT — Update Transaction
![Update](src/main/asserts/Screenshot%202026-11-18%20204158.png)

### 📊 GET — Summary (Income / Expense / Balance)
![Summary](src/main/asserts/Screenshot%202026-11-18%20204340.png)

### 💰 GET — Total Amount
![Total](src/main/asserts/Screenshot%202026-11-18%20204530.png)

---

## 🚀 Getting Started

### Prerequisites

- **JDK 17+** — [Download](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi)
- **MySQL 8.0+** — [Download](https://dev.mysql.com/downloads/)

### Setup Steps

**1. Clone the repository**
```bash
git clone https://github.com/Vishva1605/Expense-Tracker-API.git
cd Expense-Tracker-API
```

**2. Create the MySQL database**
```sql
CREATE DATABASE expense_tracker_db;
```

**3. Configure your database credentials**

Edit `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/expense_tracker_db?useSSL=false&serverTimezone=UTC
    username: root
    password: your_password_here   # ← change this
```

**4. Build the project**
```bash
mvn clean install
```

**5. Run the server**
```bash
mvn spring-boot:run
```

The API is now live at → **http://localhost:8080**

> The `transactions` table is created automatically on first run via `schema.sql`. No manual SQL setup needed beyond creating the database.

---

## ⚙️ Configuration

| Property | Default | Description |
|---|---|---|
| `server.port` | `8080` | HTTP port the server listens on |
| `spring.datasource.url` | `localhost:3306/expense_tracker_db` | MySQL connection URL |
| `spring.datasource.username` | `root` | MySQL username |
| `spring.datasource.password` | _(set yours)_ | MySQL password |
| `spring.sql.init.mode` | `always` | Runs `schema.sql` on every startup |

---

## 💡 Design Decisions

| Decision | Reason |
|---|---|
| **UUID as primary key** | Server-generated UUIDs prevent ID collisions between clients |
| **JdbcTemplate over JPA** | Direct SQL control for learning JDBC fundamentals |
| **`schema.sql` with `CREATE TABLE IF NOT EXISTS`** | Auto-creates table on startup without overwriting existing data |
| **Global Exception Handler** | `@RestControllerAdvice` returns structured JSON errors instead of stack traces |
| **DTO pattern (ExpenseRequest)** | Separates API input from the internal model; enables independent validation |
| **`type` field (INCOME / EXPENSE)** | Enables both income and expense tracking for a complete financial picture |
| **Optional filter params** | `?category=` and `?type=` are optional so one endpoint serves multiple use cases |

---

<div align="center">

**Made with ☕ Java & 💚 Spring Boot**

[⭐ Star this repo](https://github.com/Vishva1605/Expense-Tracker-API) · [🐛 Report Bug](https://github.com/Vishva1605/Expense-Tracker-API/issues)

</div>
