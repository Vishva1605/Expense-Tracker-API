package com.expensetracker.service;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.SummaryResponse;
import com.expensetracker.exception.ExpenseNotFoundException;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * ExpenseService — The BUSINESS LOGIC LAYER
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * This is the "brain" of the application. It sits between:
 *   Controller (receives requests) ←→ Service (processes logic) ←→ Repository (talks to DB)
 *
 * Responsibilities:
 *   - Create new transaction objects with generated IDs
 *   - Validate business rules (e.g., expense must exist before deleting)
 *   - Coordinate between what the controller asks and what the DB provides
 *
 * Annotation:
 *   @Service — tells Spring "this class contains business logic".
 *   Spring auto-creates one instance and injects it wherever needed.
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    // Spring automatically passes in the ExpenseRepository (Dependency Injection)
    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ADD TRANSACTION
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Creates a new transaction from the client's request data and saves it.
     *
     * Steps:
     *   1. Generate a unique ID using UUID (like a unique serial number)
     *   2. Copy values from the request DTO into a proper Expense object
     *   3. Save it to the MySQL database via the repository
     *   4. Return the saved object (now with its ID) back to the controller
     *
     * @param request validated incoming data from the client
     * @return the newly created Expense object
     */
    public Expense addExpense(ExpenseRequest request) {
        Expense expense = new Expense();
        expense.setId(UUID.randomUUID().toString());   // auto-generate unique ID
        expense.setTitle(request.getTitle().trim());
        expense.setAmount(request.getAmount());
        expense.setType(request.getType().toUpperCase());
        expense.setCategory(request.getCategory().trim());
        expense.setDate(request.getDate());
        expense.setNotes(request.getNotes());
        return repository.save(expense);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET ALL TRANSACTIONS (optionally filtered)
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Returns a list of transactions, with optional filtering.
     *
     *  - If only 'category' is given → filter by category
     *  - If only 'type' is given     → filter by type (INCOME/EXPENSE)
     *  - If both are given           → filter by both
     *  - If neither is given         → return all transactions
     *
     * @param category optional category filter (e.g. "Food")
     * @param type     optional type filter ("INCOME" or "EXPENSE")
     * @return filtered (or full) list of transactions
     */
    public List<Expense> getExpenses(Optional<String> category, Optional<String> type) {
        // Both filters provided
        if (category.isPresent() && type.isPresent()) {
            return repository.findAll().stream()
                    .filter(e -> e.getCategory().equalsIgnoreCase(category.get()))
                    .filter(e -> e.getType().equalsIgnoreCase(type.get()))
                    .toList();
        }
        // Only category
        if (category.isPresent()) {
            return repository.findByCategory(category.get());
        }
        // Only type
        if (type.isPresent()) {
            return repository.findByType(type.get());
        }
        // No filters — return everything
        return repository.findAll();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET ONE TRANSACTION BY ID
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Finds and returns one transaction by its ID.
     * Throws ExpenseNotFoundException if the ID doesn't exist,
     * which the GlobalExceptionHandler converts into a 404 HTTP response.
     *
     * @param id the UUID string
     * @return the found Expense object
     */
    public Expense getExpenseById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UPDATE AN EXISTING TRANSACTION
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Updates an existing transaction with new values.
     *
     * Steps:
     *   1. Check if the transaction exists (throw 404 if not)
     *   2. Apply the new values from the request
     *   3. Save the changes to the database
     *   4. Return the updated transaction
     *
     * @param id      the UUID of the transaction to update
     * @param request the new data to apply
     * @return the updated Expense object
     */
    public Expense updateExpense(String id, ExpenseRequest request) {
        // Step 1: Make sure the record exists before trying to update
        Expense existing = repository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        // Step 2: Apply the new values
        existing.setTitle(request.getTitle().trim());
        existing.setAmount(request.getAmount());
        existing.setType(request.getType().toUpperCase());
        existing.setCategory(request.getCategory().trim());
        existing.setDate(request.getDate());
        existing.setNotes(request.getNotes());

        // Step 3 & 4: Save and return
        return repository.update(existing);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE A TRANSACTION
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Deletes a transaction by ID.
     * Throws ExpenseNotFoundException if the ID doesn't exist.
     *
     * @param id the UUID of the transaction to delete
     */
    public void deleteExpense(String id) {
        if (!repository.existsById(id)) {
            throw new ExpenseNotFoundException(id);
        }
        repository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEARCH BY TITLE KEYWORD
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Searches for transactions whose title contains the given keyword.
     * Case-insensitive (e.g. "lunch" will find "Monthly Lunch Allowance").
     *
     * @param keyword the search term
     * @return list of matching transactions
     */
    public List<Expense> searchByTitle(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("Search keyword must not be blank");
        }
        return repository.searchByTitle(keyword.trim());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEARCH BY DATE RANGE
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Returns all transactions between a start date and end date (inclusive).
     *
     * @param startDate beginning of the range (must be before or same as endDate)
     * @param endDate   end of the range
     * @return list of transactions in the date range
     */
    public List<Expense> getByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "startDate (" + startDate + ") must not be after endDate (" + endDate + ")");
        }
        return repository.findByDateRange(startDate, endDate);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CALCULATE TOTAL EXPENSES (optionally by category)
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Calculates the total amount spent, optionally filtered by category.
     *
     * @param category optional category name to filter
     * @return sum of matching transaction amounts
     */
    public double getTotal(Optional<String> category) {
        return repository.findAll().stream()
                .filter(e -> category.isEmpty() || e.getCategory().equalsIgnoreCase(category.get()))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FINANCIAL SUMMARY — income vs expense dashboard
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Returns a financial summary showing total income, total expenses,
     * and the net balance (income minus expenses).
     *
     * Uses direct SQL SUM queries via the repository for efficiency
     * instead of loading all rows into memory.
     *
     * @return SummaryResponse with totalIncome, totalExpense, netBalance
     */
    public SummaryResponse getSummary() {
        double totalIncome  = repository.sumByType("INCOME");
        double totalExpense = repository.sumByType("EXPENSE");
        return new SummaryResponse(totalIncome, totalExpense);
    }
}
