package com.expensetracker.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Core domain object representing a single financial transaction.
 *
 * A transaction can be either INCOME (money coming in, e.g. salary)
 * or EXPENSE (money going out, e.g. buying groceries).
 *
 * This is a plain Java class (POJO) - no framework magic here.
 * It just holds data. Getters and setters let other classes read/write it.
 */
public class Expense {

    private String id;           // Unique identifier (UUID)
    private String title;        // Short name, e.g. "Monthly Salary"
    private double amount;       // Money amount, e.g. 5000.00
    private String type;         // "INCOME" or "EXPENSE"
    private String category;     // e.g. "Food", "Salary", "Travel"
    private LocalDate date;      // When this transaction happened
    private String notes;        // Optional extra detail
    private LocalDateTime createdAt; // When this record was saved to DB

    // ── Constructors ──────────────────────────────────────────────────────────

    /** No-arg constructor needed by Spring / JSON libraries */
    public Expense() {
    }

    /** Full constructor used when reading rows back from the database */
    public Expense(String id, String title, double amount, String type,
                   String category, LocalDate date, String notes, LocalDateTime createdAt) {
        this.id        = id;
        this.title     = title;
        this.amount    = amount;
        this.type      = type;
        this.category  = category;
        this.date      = date;
        this.notes     = notes;
        this.createdAt = createdAt;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // ── equals / hashCode ─────────────────────────────────────────────────────
    // Two expenses are the same if they have the same ID (like a unique ID card)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Expense)) return false;
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                ", category='" + category + '\'' +
                ", date=" + date +
                ", notes='" + notes + '\'' +
                '}';
    }
}
