package com.expensetracker.service;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.exception.ExpenseNotFoundException;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseServiceTest {

    private ExpenseService service;

    @BeforeEach
    void setUp() {
        // Fresh repository per test so tests don't leak state into each other.
        service = new ExpenseService(new ExpenseRepository());
    }

    private ExpenseRequest request(String title, double amount, String category, LocalDate date) {
        ExpenseRequest r = new ExpenseRequest();
        r.setTitle(title);
        r.setAmount(amount);
        r.setCategory(category);
        r.setDate(date);
        return r;
    }

    @Test
    void addExpense_generatesIdAndPersists() {
        Expense created = service.addExpense(
                request("Groceries", 45.50, "Food", LocalDate.of(2026, 7, 1)));

        assertNotNull(created.getId());
        assertEquals("Groceries", created.getTitle());
        assertEquals(1, service.getExpenses(Optional.empty()).size());
    }

    @Test
    void getExpenses_withNoFilter_returnsAll() {
        service.addExpense(request("Groceries", 45.50, "Food", LocalDate.of(2026, 7, 1)));
        service.addExpense(request("Bus pass", 20.00, "Transport", LocalDate.of(2026, 7, 2)));

        List<Expense> all = service.getExpenses(Optional.empty());

        assertEquals(2, all.size());
    }

    @Test
    void getExpenses_filtersByCategory_caseInsensitive() {
        service.addExpense(request("Groceries", 45.50, "Food", LocalDate.of(2026, 7, 1)));
        service.addExpense(request("Bus pass", 20.00, "Transport", LocalDate.of(2026, 7, 2)));

        List<Expense> food = service.getExpenses(Optional.of("food"));

        assertEquals(1, food.size());
        assertEquals("Groceries", food.get(0).getTitle());
    }

    @Test
    void getExpenses_sortedByDateDescending() {
        service.addExpense(request("Old expense", 10, "Misc", LocalDate.of(2026, 1, 1)));
        service.addExpense(request("New expense", 10, "Misc", LocalDate.of(2026, 7, 1)));

        List<Expense> all = service.getExpenses(Optional.empty());

        assertEquals("New expense", all.get(0).getTitle());
        assertEquals("Old expense", all.get(1).getTitle());
    }

    @Test
    void getTotal_overall_sumsAllExpenses() {
        service.addExpense(request("Groceries", 45.50, "Food", LocalDate.of(2026, 7, 1)));
        service.addExpense(request("Bus pass", 20.00, "Transport", LocalDate.of(2026, 7, 2)));

        double total = service.getTotal(Optional.empty());

        assertEquals(65.50, total, 0.001);
    }

    @Test
    void getTotal_byCategory_sumsOnlyThatCategory() {
        service.addExpense(request("Groceries", 45.50, "Food", LocalDate.of(2026, 7, 1)));
        service.addExpense(request("Dinner out", 30.00, "Food", LocalDate.of(2026, 7, 3)));
        service.addExpense(request("Bus pass", 20.00, "Transport", LocalDate.of(2026, 7, 2)));

        double foodTotal = service.getTotal(Optional.of("Food"));

        assertEquals(75.50, foodTotal, 0.001);
    }

    @Test
    void getTotal_categoryWithNoExpenses_returnsZero() {
        double total = service.getTotal(Optional.of("Nonexistent"));

        assertEquals(0.0, total, 0.001);
    }

    @Test
    void deleteExpense_removesIt() {
        Expense created = service.addExpense(
                request("Groceries", 45.50, "Food", LocalDate.of(2026, 7, 1)));

        service.deleteExpense(created.getId());

        assertTrue(service.getExpenses(Optional.empty()).isEmpty());
    }

    @Test
    void deleteExpense_unknownId_throwsNotFound() {
        assertThrows(ExpenseNotFoundException.class,
                () -> service.deleteExpense("does-not-exist"));
    }

    @Test
    void getExpenseById_unknownId_throwsNotFound() {
        assertThrows(ExpenseNotFoundException.class,
                () -> service.getExpenseById("does-not-exist"));
    }
}
