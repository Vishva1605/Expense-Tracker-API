-- ============================================================
-- Personal Expense Tracker - Database Schema
-- ============================================================
-- This file is run automatically by Spring Boot on startup
-- (because sql.init.mode = always in application.yml).
--
-- It creates the 'transactions' table if it does not already
-- exist, so you never lose existing data on a restart.
-- ============================================================

CREATE TABLE IF NOT EXISTS transactions (
    id          VARCHAR(36)    NOT NULL,          -- UUID primary key (e.g. "a1b2-...")
    title       VARCHAR(255)   NOT NULL,          -- Short description (e.g. "Lunch at Cafe")
    amount      DECIMAL(15, 2) NOT NULL,          -- Money amount (e.g. 250.00)
    type        VARCHAR(10)    NOT NULL,          -- "INCOME" or "EXPENSE"
    category    VARCHAR(100)   NOT NULL,          -- e.g. "Food", "Salary", "Travel"
    txn_date    DATE           NOT NULL,          -- Date of transaction (yyyy-MM-dd)
    notes       VARCHAR(500),                     -- Optional extra details
    created_at  TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
);
