<div align="center">

# 💸 Expense Tracker API

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red?style=flat-square&logo=apachemaven)](https://maven.apache.org/)

**A REST API for tracking personal income & expenses — built with Java 17, Spring Boot 3, JDBC, and MySQL.**

</div>

---

## 📌 Table of Contents
- [About](#-about)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [API Endpoints](#-api-endpoints)
- [API Outputs](#-api-outputs)
- [Getting Started](#-getting-started)
- [Design Decisions](#-design-decisions)

---

## 🧾 About

A backend REST API that lets users:
- ✅ Add income and expense transactions
- 📋 View, filter, search, and update transactions
- 🗑️ Delete transactions
- 💰 Get total amount (overall or per category)
- 📊 Get a financial summary — total income, total expense, net balance

> All data is **persistently stored in MySQL** using Spring JDBC (`JdbcTemplate`).

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

---

## 📁 Project Structure

```
Expense-Tracker-API/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/expensetracker/
    │   ├── ExpenseTrackerApplication.java
    │   ├── controller/ExpenseController.java
    │   ├── service/ExpenseService.java
    │   ├── repository/ExpenseRepository.java
    │   ├── model/Expense.java
    │   ├── dto/  (ExpenseRequest, TotalResponse, SummaryResponse, ApiError)
    │   └── exception/  (ExpenseNotFoundException, GlobalExceptionHandler)
    └── resources/
        ├── application.yml
        ├── schema.sql
        └── static/index.html
```

---

## 🗄️ Database Schema

```sql
CREATE TABLE IF NOT EXISTS transactions (
    id          VARCHAR(36)    NOT NULL,
    title       VARCHAR(255)   NOT NULL,
    amount      DECIMAL(15, 2) NOT NULL,
    type        VARCHAR(10)    NOT NULL,   -- "INCOME" or "EXPENSE"
    category    VARCHAR(100)   NOT NULL,
    txn_date    DATE           NOT NULL,
    notes       VARCHAR(500),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
```

> `schema.sql` runs automatically on startup — no manual table creation needed.

---

## 🔗 API Endpoints

**Base URL:** `http://localhost:8080/api/expenses`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/expenses` | Add a new transaction |
| `GET` | `/api/expenses` | Get all (optional `?category=` & `?type=`) |
| `GET` | `/api/expenses/{id}` | Get single transaction by ID |
| `PUT` | `/api/expenses/{id}` | Update a transaction |
| `DELETE` | `/api/expenses/{id}` | Delete a transaction |
| `GET` | `/api/expenses/search?keyword=` | Search by title keyword |
| `GET` | `/api/expenses/range?startDate=&endDate=` | Filter by date range |
| `GET` | `/api/expenses/total` | Total amount (all or `?category=`) |
| `GET` | `/api/expenses/summary` | Income / Expense / Net Balance |

---

## 📸 API Outputs

<div align="center">
<table>
  <tr>
    <td align="center"><b>➕ POST — Add Transaction</b><br><img src="src/main/asserts/Screenshot 2026-11-18 203818.png" width="380"/></td>
    <td align="center"><b>📋 GET — All Transactions</b><br><img src="src/main/asserts/Screenshot 2026-11-18 204035.png" width="380"/></td>
  </tr>
  <tr>
    <td align="center"><b>🔍 GET — By ID</b><br><img src="src/main/asserts/Screenshot 2026-11-18 204118.png" width="380"/></td>
    <td align="center"><b>✏️ PUT — Update</b><br><img src="src/main/asserts/Screenshot 2026-11-18 204158.png" width="380"/></td>
  </tr>
  <tr>
    <td align="center"><b>📊 GET — Summary</b><br><img src="src/main/asserts/Screenshot 2026-11-18 204340.png" width="380"/></td>
    <td align="center"><b>💰 GET — Total</b><br><img src="src/main/asserts/Screenshot 2026-11-18 204530.png" width="380"/></td>
  </tr>
</table>
</div>

---

## 🚀 Getting Started

### Prerequisites
- JDK 17+ · Maven 3.8+ · MySQL 8.0+

### Setup

```bash
# 1. Clone
git clone https://github.com/Vishva1605/Expense-Tracker-API.git
cd Expense-Tracker-API

# 2. Create the database
# In MySQL: CREATE DATABASE expense_tracker_db;

# 3. Copy the example config and set your credentials
cp src/main/resources/application-example.yml src/main/resources/application.yml
# Then open application.yml and set your MySQL password

# 4. Build & Run
mvn clean install
mvn spring-boot:run
```

API available at → **http://localhost:8080**

---

## 💡 Design Decisions

| Decision | Reason |
|---|---|
| UUID as primary key | Server-generated, collision-free IDs |
| JdbcTemplate over JPA | Direct SQL control; demonstrates core JDBC skills |
| `CREATE TABLE IF NOT EXISTS` in schema.sql | Auto-creates table without wiping existing data |
| `@RestControllerAdvice` global handler | Returns structured JSON errors — no raw stack traces |
| DTO pattern (`ExpenseRequest`) | Decouples API input from internal model; enables validation |
| `type` field (INCOME / EXPENSE) | Enables complete financial tracking, not just expenses |

---

<div align="center">

**Made with ☕ Java & 💚 Spring Boot**  
[⭐ Star this repo](https://github.com/Vishva1605/Expense-Tracker-API) · [🐛 Report Bug](https://github.com/Vishva1605/Expense-Tracker-API/issues)

</div>
