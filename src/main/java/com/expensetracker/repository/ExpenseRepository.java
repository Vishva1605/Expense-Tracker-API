package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store, per the assignment ("Data can be stored in memory or a
 * local JSON file; no database is required"). ConcurrentHashMap is used
 * instead of a plain HashMap so the API is safe under concurrent requests
 * without needing extra synchronization in the service layer.
 *
 * Kept as a thin abstraction over the map (rather than calling the map
 * directly from the service) so swapping this for a JSON-file-backed or
 * JPA-backed implementation later only touches this one class.
 */
@Repository
public class ExpenseRepository {

    private final Map<String, Expense> store = new ConcurrentHashMap<>();

    public Expense save(Expense expense) {
        store.put(expense.getId(), expense);
        return expense;
    }

    public Collection<Expense> findAll() {
        return store.values();
    }

    public Optional<Expense> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public boolean existsById(String id) {
        return store.containsKey(id);
    }

    public void deleteById(String id) {
        store.remove(id);
    }
}
