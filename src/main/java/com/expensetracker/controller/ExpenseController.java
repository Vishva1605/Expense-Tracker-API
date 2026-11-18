package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.SummaryResponse;
import com.expensetracker.dto.TotalResponse;
import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * ExpenseController — The REST API LAYER (Entry Point)
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * This is the "front door" of the application.
 * When someone makes an HTTP request (GET, POST, PUT, DELETE), this class
 * receives it, calls the right service method, and sends back a response.
 *
 * Base URL: http://localhost:8080/api/expenses
 *
 * Annotations explained:
 *   @RestController   — Marks this as a REST API controller.
 *                       All methods automatically convert return values to JSON.
 *   @RequestMapping   — Sets the base URL path for all methods in this class.
 *   @Tag              — Swagger documentation label for this group of APIs.
 * ═══════════════════════════════════════════════════════════════════════════
 */
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    // Spring automatically injects ExpenseService here (Dependency Injection)
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/expenses — Create a new transaction
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * HTTP Method: POST
     * URL:         /api/expenses
     * Body:        JSON with title, amount, type, category, date, notes
     * Response:    201 CREATED + the saved transaction (with auto-generated ID)
     *
     * @Valid triggers automatic validation of all @NotBlank, @Positive, etc.
     * If validation fails, GlobalExceptionHandler returns a 400 error.
     */
    @PostMapping
    public ResponseEntity<Expense> addExpense(@Valid @RequestBody ExpenseRequest request) {
        Expense created = expenseService.addExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/expenses — Get all transactions (with optional filters)
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * HTTP Method: GET
     * URL:         /api/expenses
     *              /api/expenses?category=Food
     *              /api/expenses?type=EXPENSE
     *              /api/expenses?category=Food&type=EXPENSE
     * Response:    200 OK + list of transactions
     *
     * @RequestParam(required = false) makes query params optional.
     */
    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type) {

        List<Expense> expenses = expenseService.getExpenses(
                Optional.ofNullable(category),
                Optional.ofNullable(type)
        );
        return ResponseEntity.ok(expenses);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/expenses/{id} — Get one transaction by ID
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * HTTP Method: GET
     * URL:         /api/expenses/{id}   (e.g. /api/expenses/abc-123)
     * Response:    200 OK + the transaction, or 404 if not found
     *
     * {id} is a path variable — it comes from the URL itself (not a query param).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(
            @PathVariable String id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/expenses/{id} — Update an existing transaction
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * HTTP Method: PUT
     * URL:         /api/expenses/{id}
     * Body:        JSON with updated fields (all required again)
     * Response:    200 OK + the updated transaction, or 404 if not found
     *
     * PUT replaces the entire record (as opposed to PATCH which does partial update).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(
            @PathVariable String id,
            @Valid @RequestBody ExpenseRequest request) {
        Expense updated = expenseService.updateExpense(id, request);
        return ResponseEntity.ok(updated);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/expenses/{id} — Remove a transaction
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * HTTP Method: DELETE
     * URL:         /api/expenses/{id}
     * Response:    204 NO CONTENT (success, nothing to return), or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable String id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/expenses/search — Keyword search by title
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * HTTP Method: GET
     * URL:         /api/expenses/search?keyword=lunch
     * Response:    200 OK + list of transactions whose title contains the keyword
     */
    @GetMapping("/search")
    public ResponseEntity<List<Expense>> searchByTitle(
            @RequestParam String keyword) {
        return ResponseEntity.ok(expenseService.searchByTitle(keyword));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/expenses/range — Filter by date range
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * HTTP Method: GET
     * URL:         /api/expenses/range?startDate=2024-01-01&endDate=2024-06-30
     * Response:    200 OK + list of transactions in that date range
     *
     * @DateTimeFormat tells Spring how to parse the date string from the URL.
     */
    @GetMapping("/range")
    public ResponseEntity<List<Expense>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(expenseService.getByDateRange(startDate, endDate));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/expenses/total — Total amount spent
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * HTTP Method: GET
     * URL:         /api/expenses/total
     *              /api/expenses/total?category=Food
     * Response:    200 OK + { "category": "Food", "total": 1250.50 }
     */
    @GetMapping("/total")
    public ResponseEntity<TotalResponse> getTotal(
            @RequestParam(required = false) String category) {
        double total = expenseService.getTotal(Optional.ofNullable(category));
        return ResponseEntity.ok(new TotalResponse(category, total));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/expenses/summary — Financial dashboard summary
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * HTTP Method: GET
     * URL:         /api/expenses/summary
     * Response:    200 OK + { "totalIncome": X, "totalExpense": Y, "netBalance": Z }
     *
     * This is the "dashboard" endpoint — a quick snapshot of finances.
     */
    @GetMapping("/summary")
    public ResponseEntity<SummaryResponse> getSummary() {
        return ResponseEntity.ok(expenseService.getSummary());
    }
}
