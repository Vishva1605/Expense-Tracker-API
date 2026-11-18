package com.expensetracker.dto;

/**
 * Response DTO for total-expense queries.
 *
 * When you ask "how much have I spent in total?" or
 * "how much did I spend on Food?", the server wraps the
 * answer in this object so the client gets structured JSON.
 *
 * Example JSON response:
 * {
 *   "category": "Food",
 *   "total": 1250.50
 * }
 * When category is null, it means "grand total across everything".
 */
public class TotalResponse {

    private String category;   // null = overall total; non-null = category total
    private double total;      // the calculated sum

    public TotalResponse() {
    }

    public TotalResponse(String category, double total) {
        this.category = category;
        this.total    = total;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
