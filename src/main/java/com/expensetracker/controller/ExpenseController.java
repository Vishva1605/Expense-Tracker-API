package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.TotalResponse;
import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expenses", description = "Manage personal expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Operation(summary = "Add a new expense")
    @PostMapping
    public ResponseEntity<Expense> addExpense(@Valid @RequestBody ExpenseRequest request) {
        Expense created = expenseService.addExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "View all expenses, optionally filtered by category")
    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses(
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(expenseService.getExpenses(Optional.ofNullable(category)));
    }

    @Operation(summary = "Get a single expense by id")
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable String id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    @Operation(summary = "Get total expenses - overall, or for one category")
    @GetMapping("/total")
    public ResponseEntity<TotalResponse> getTotal(
            @RequestParam(required = false) String category) {
        double total = expenseService.getTotal(Optional.ofNullable(category));
        return ResponseEntity.ok(new TotalResponse(category, total));
    }

    @Operation(summary = "Delete an expense by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable String id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
