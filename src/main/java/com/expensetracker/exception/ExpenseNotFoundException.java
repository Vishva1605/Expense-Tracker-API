package com.expensetracker.exception;

/**
 * Custom Exception — Thrown when an expense/transaction is not found.
 *
 * Why a custom exception?
 *   Instead of returning a confusing NullPointerException or generic error,
 *   we throw this specific exception which the GlobalExceptionHandler catches
 *   and converts into a proper 404 HTTP response.
 *
 * Extends RuntimeException:
 *   RuntimeExceptions don't need to be declared in method signatures (no "throws" keyword).
 *   They propagate up automatically until caught.
 *
 * Example:
 *   throw new ExpenseNotFoundException("abc-123");
 *   → HTTP 404: "Transaction not found with id: abc-123"
 */
public class ExpenseNotFoundException extends RuntimeException {

    private final String expenseId;

    public ExpenseNotFoundException(String id) {
        super("Transaction not found with id: " + id);
        this.expenseId = id;
    }

    /** Returns the ID that was not found (useful for logging) */
    public String getExpenseId() {
        return expenseId;
    }
}
