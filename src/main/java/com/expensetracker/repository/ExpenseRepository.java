package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * ExpenseRepository — The DATABASE LAYER
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * This class is responsible for all database operations (CRUD).
 * It uses Spring's JdbcTemplate which is a helper class that:
 *   1. Opens a connection to MySQL
 *   2. Runs your SQL query
 *   3. Closes the connection safely (even if there's an error)
 *   4. Converts the result into Java objects
 *
 * Think of JdbcTemplate as a friendly middleman between your Java code
 * and the MySQL database.
 *
 * Annotation:
 *   @Repository — tells Spring "this class handles database work".
 *   Spring will automatically create one instance and share it everywhere.
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Repository
public class ExpenseRepository {

    // JdbcTemplate is injected by Spring automatically (Dependency Injection)
    private final JdbcTemplate jdbcTemplate;

    public ExpenseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ROW MAPPER — converts a MySQL row into an Expense Java object
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * A RowMapper tells JDBC how to read one row from the database result
     * and turn it into one Expense object.
     *
     * Think of it like a translation dictionary:
     *   MySQL column "title"     →  expense.setTitle(...)
     *   MySQL column "amount"    →  expense.setAmount(...)
     *   ... and so on for every column.
     */
    private final RowMapper<Expense> expenseRowMapper = (rs, rowNum) -> {
        Expense e = new Expense();
        e.setId(rs.getString("id"));
        e.setTitle(rs.getString("title"));
        e.setAmount(rs.getDouble("amount"));
        e.setType(rs.getString("type"));
        e.setCategory(rs.getString("category"));
        e.setDate(rs.getDate("txn_date").toLocalDate());
        e.setNotes(rs.getString("notes"));

        // created_at can be null for very old rows; guard against it
        java.sql.Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            e.setCreatedAt(ts.toLocalDateTime());
        }
        return e;
    };

    // ─────────────────────────────────────────────────────────────────────────
    // CREATE — Insert a new transaction into the database
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Saves a new transaction record to the MySQL 'transactions' table.
     *
     * SQL: INSERT INTO transactions (id, title, amount, type, category, txn_date, notes)
     *      VALUES (?, ?, ?, ?, ?, ?, ?)
     *
     * The '?' are placeholders — JDBC fills them in safely from the Java values.
     * This prevents SQL Injection attacks (a common hacking technique).
     *
     * @param expense the Expense object to save
     * @return the same expense object (now persisted in DB)
     */
    public Expense save(Expense expense) {
        String sql = """
                INSERT INTO transactions (id, title, amount, type, category, txn_date, notes)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getType(),
                expense.getCategory(),
                expense.getDate(),
                expense.getNotes()
        );
        return expense;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ ALL — Fetch every transaction from the database
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Retrieves all rows from the transactions table.
     *
     * SQL: SELECT * FROM transactions ORDER BY txn_date DESC
     *
     * @return a List of all Expense objects (newest first)
     */
    public List<Expense> findAll() {
        String sql = "SELECT * FROM transactions ORDER BY txn_date DESC";
        return jdbcTemplate.query(sql, expenseRowMapper);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ BY ID — Fetch one specific transaction
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Finds one transaction by its unique ID.
     *
     * SQL: SELECT * FROM transactions WHERE id = ?
     *
     * Returns Optional.empty() if no record exists with that ID,
     * instead of crashing with an exception.
     *
     * @param id the UUID string of the transaction
     * @return an Optional containing the Expense, or empty if not found
     */
    public Optional<Expense> findById(String id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        List<Expense> results = jdbcTemplate.query(sql, expenseRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ BY CATEGORY — Fetch transactions filtered by category
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Returns all transactions for a specific category (case-insensitive).
     *
     * SQL: SELECT * FROM transactions WHERE LOWER(category) = LOWER(?)
     *
     * @param category the category name to filter by
     * @return list of matching Expense objects
     */
    public List<Expense> findByCategory(String category) {
        String sql = "SELECT * FROM transactions WHERE LOWER(category) = LOWER(?) ORDER BY txn_date DESC";
        return jdbcTemplate.query(sql, expenseRowMapper, category);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ BY TYPE — Fetch INCOME or EXPENSE transactions
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Returns all transactions of a given type (INCOME or EXPENSE).
     *
     * SQL: SELECT * FROM transactions WHERE type = ?
     *
     * @param type "INCOME" or "EXPENSE"
     * @return list of matching Expense objects
     */
    public List<Expense> findByType(String type) {
        String sql = "SELECT * FROM transactions WHERE type = ? ORDER BY txn_date DESC";
        return jdbcTemplate.query(sql, expenseRowMapper, type.toUpperCase());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ BY DATE RANGE — Search between two dates
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Returns all transactions that fall between a start and end date.
     *
     * SQL: SELECT * FROM transactions WHERE txn_date BETWEEN ? AND ?
     *
     * @param startDate beginning of the date range
     * @param endDate   end of the date range
     * @return list of matching Expense objects
     */
    public List<Expense> findByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM transactions WHERE txn_date BETWEEN ? AND ? ORDER BY txn_date DESC";
        return jdbcTemplate.query(sql, expenseRowMapper, startDate, endDate);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEARCH BY TITLE — Keyword search
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Searches for transactions whose title contains the given keyword.
     *
     * SQL: SELECT * FROM transactions WHERE LOWER(title) LIKE ?
     * The '%' symbols mean "match any characters before/after the keyword".
     *
     * @param keyword the search term
     * @return list of matching Expense objects
     */
    public List<Expense> searchByTitle(String keyword) {
        String sql = "SELECT * FROM transactions WHERE LOWER(title) LIKE ? ORDER BY txn_date DESC";
        String pattern = "%" + keyword.toLowerCase() + "%";
        return jdbcTemplate.query(sql, expenseRowMapper, pattern);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UPDATE — Modify an existing transaction
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Updates all fields of an existing transaction in the database.
     *
     * SQL: UPDATE transactions SET title=?, amount=?, ... WHERE id=?
     *
     * @param expense the Expense object with updated values (id must match existing row)
     * @return the updated expense
     */
    public Expense update(Expense expense) {
        String sql = """
                UPDATE transactions
                SET    title    = ?,
                       amount   = ?,
                       type     = ?,
                       category = ?,
                       txn_date = ?,
                       notes    = ?
                WHERE  id       = ?
                """;
        jdbcTemplate.update(sql,
                expense.getTitle(),
                expense.getAmount(),
                expense.getType(),
                expense.getCategory(),
                expense.getDate(),
                expense.getNotes(),
                expense.getId()
        );
        return expense;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE — Remove a transaction from the database
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Deletes a transaction by its ID.
     *
     * SQL: DELETE FROM transactions WHERE id = ?
     *
     * @param id the UUID of the transaction to delete
     */
    public void deleteById(String id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EXISTS CHECK — Avoid deleting/updating something that doesn't exist
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Checks whether a transaction with the given ID exists in the database.
     *
     * SQL: SELECT COUNT(*) FROM transactions WHERE id = ?
     * Returns true if count > 0, false otherwise.
     *
     * @param id the UUID to check
     * @return true if found, false if not
     */
    public boolean existsById(String id) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SUM — Calculate totals for summary dashboard
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Returns the total sum of amounts for a given transaction type.
     *
     * SQL: SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = ?
     * COALESCE returns 0 if there are no rows (avoids returning null).
     *
     * @param type "INCOME" or "EXPENSE"
     * @return total amount as a double
     */
    public double sumByType(String type) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = ?";
        Double result = jdbcTemplate.queryForObject(sql, Double.class, type.toUpperCase());
        return result != null ? result : 0.0;
    }
}
