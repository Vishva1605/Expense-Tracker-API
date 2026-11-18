package com.expensetracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for creating or updating a transaction.
 *
 * Think of a DTO like a form you fill in before submitting data.
 * The client (Postman, frontend app) sends JSON matching this structure.
 * The server validates it before touching the database.
 *
 * NOTE: We do NOT include 'id' here because the server auto-generates it.
 */
public class ExpenseRequest {

    @NotBlank(message = "title must not be blank")
    private String title;          // e.g. "Monthly Salary"

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be greater than 0")
    private Double amount;         // e.g. 5000.00

    @NotBlank(message = "type is required (INCOME or EXPENSE)")
    @Pattern(regexp = "INCOME|EXPENSE", message = "type must be either INCOME or EXPENSE")
    private String type;           // "INCOME" or "EXPENSE"

    @NotBlank(message = "category must not be blank")
    private String category;       // e.g. "Salary", "Food", "Travel"

    @NotNull(message = "date is required (format: yyyy-MM-dd)")
    private LocalDate date;        // e.g. 2024-06-15

    private String notes;          // Optional: extra details (nullable)

    // ── No-arg constructor ────────────────────────────────────────────────────

    public ExpenseRequest() {
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
