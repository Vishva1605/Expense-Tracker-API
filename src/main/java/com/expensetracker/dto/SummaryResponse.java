package com.expensetracker.dto;

/**
 * Response DTO for the financial summary endpoint.
 *
 * Shows a quick snapshot:
 *   - Total income received
 *   - Total expenses spent
 *   - Net balance (income - expenses)
 *
 * Example JSON:
 * {
 *   "totalIncome": 15000.00,
 *   "totalExpense": 8500.00,
 *   "netBalance": 6500.00
 * }
 */
public class SummaryResponse {

    private double totalIncome;   // All INCOME transactions summed up
    private double totalExpense;  // All EXPENSE transactions summed up
    private double netBalance;    // totalIncome - totalExpense

    public SummaryResponse() {
    }

    public SummaryResponse(double totalIncome, double totalExpense) {
        this.totalIncome  = totalIncome;
        this.totalExpense = totalExpense;
        this.netBalance   = totalIncome - totalExpense;
    }

    public double getTotalIncome() { return totalIncome; }
    public void setTotalIncome(double totalIncome) { this.totalIncome = totalIncome; }

    public double getTotalExpense() { return totalExpense; }
    public void setTotalExpense(double totalExpense) { this.totalExpense = totalExpense; }

    public double getNetBalance() { return netBalance; }
    public void setNetBalance(double netBalance) { this.netBalance = netBalance; }
}
