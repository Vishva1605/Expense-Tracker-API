package com.expensetracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * Payload for creating an expense. The id is deliberately NOT part of this
 * DTO - the server generates it - so a client can never collide two
 * expenses on the same id.
 */
public class ExpenseRequest {

    @NotBlank(message = "title must not be blank")
    private String title;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "category must not be blank")
    private String category;

    @NotNull(message = "date is required (format: yyyy-MM-dd)")
    private LocalDate date;

    public ExpenseRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
