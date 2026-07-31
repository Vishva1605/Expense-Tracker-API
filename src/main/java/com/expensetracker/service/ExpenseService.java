package com.expensetracker.service;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.exception.ExpenseNotFoundException;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public Expense addExpense(ExpenseRequest request) {
        Expense expense = new Expense(
                UUID.randomUUID().toString(),
                request.getTitle().trim(),
                request.getAmount(),
                request.getCategory().trim(),
                request.getDate()
        );
        return repository.save(expense);
    }

    /**
     * Returns all expenses, optionally filtered by category (case-insensitive).
     * Sorted by date descending so the most recent expense shows up first -
     * that's the order most expense-tracker UIs expect.
     */
    public List<Expense> getExpenses(Optional<String> category) {
        return repository.findAll().stream()
                .filter(e -> category.isEmpty()
                        || e.getCategory().equalsIgnoreCase(category.get()))
                .sorted(Comparator.comparing(Expense::getDate).reversed())
                .collect(Collectors.toList());
    }

    public Expense getExpenseById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    /**
     * Overall total when category is empty, otherwise the total for that
     * one category. Returns 0.0 for a category with no matching expenses
     * rather than throwing - an empty category total is a valid answer,
     * not an error.
     */
    public double getTotal(Optional<String> category) {
        return repository.findAll().stream()
                .filter(e -> category.isEmpty()
                        || e.getCategory().equalsIgnoreCase(category.get()))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public void deleteExpense(String id) {
        if (!repository.existsById(id)) {
            throw new ExpenseNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
