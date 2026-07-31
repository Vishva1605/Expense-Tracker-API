package com.expensetracker.dto;

/**
 * Response shape for total-expense queries. "category" is null when the
 * total represents the overall sum across every expense.
 */
public class TotalResponse {

    private String category;
    private double total;

    public TotalResponse() {
    }

    public TotalResponse(String category, double total) {
        this.category = category;
        this.total = total;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
