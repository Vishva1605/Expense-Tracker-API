package com.expensetracker.exception;

import com.expensetracker.dto.ApiError;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * GlobalExceptionHandler — Centralized Error Handler
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Without this class, if something goes wrong, Spring would return an ugly
 * HTML error page or a confusing stack trace.
 *
 * This class catches specific exceptions and converts them into clean,
 * structured JSON error responses that clients can understand.
 *
 * @RestControllerAdvice:
 *   This annotation tells Spring: "Watch ALL controllers for exceptions.
 *   If any controller throws an exception, give it to me to handle."
 *
 * @ExceptionHandler(SomeException.class):
 *   "When this specific exception type is thrown, run this method."
 *
 * Example: If ExpenseNotFoundException is thrown anywhere,
 *          Spring calls handleNotFound() which returns a 404 JSON response.
 * ═══════════════════════════════════════════════════════════════════════════
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─────────────────────────────────────────────────────────────────────────
    // Handle 404 — Transaction Not Found
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Triggered when: the client asks for a transaction ID that doesn't exist.
     * Response: HTTP 404 Not Found
     *
     * Example JSON:
     * {
     *   "status": 404,
     *   "error": "Not Found",
     *   "messages": ["Transaction not found with id: abc-123"]
     * }
     */
    @ExceptionHandler(ExpenseNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ExpenseNotFoundException ex) {
        ApiError body = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                List.of(ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Handle 400 — Validation Failures (e.g. missing required fields)
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Triggered when: @Valid validation fails (e.g. blank title, negative amount).
     * Response: HTTP 400 Bad Request with a list of ALL validation errors.
     *
     * Example JSON:
     * {
     *   "status": 400,
     *   "error": "Validation Failed",
     *   "messages": ["title: must not be blank", "amount: must be greater than 0"]
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.toList());

        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                messages
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Handle 400 — Illegal business logic arguments
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Triggered when: service layer throws IllegalArgumentException
     * (e.g. startDate is after endDate, or search keyword is blank).
     * Response: HTTP 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                List.of(ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Handle 400 — Wrong parameter type in URL
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Triggered when: a URL parameter has the wrong type
     * (e.g. passing "abc" where a date is expected).
     * Response: HTTP 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Parameter '" + ex.getName() + "' has invalid value: '" + ex.getValue() + "'";
        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Parameter",
                List.of(message)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Handle 400 — Missing required request parameters
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Triggered when: a required @RequestParam is missing from the URL.
     * Response: HTTP 400 Bad Request
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(MissingServletRequestParameterException ex) {
        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Missing Parameter",
                List.of("Required parameter '" + ex.getParameterName() + "' is missing")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Handle 500 — Database errors
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Triggered when: a SQL/database error occurs (e.g. connection refused,
     * constraint violation, table not found).
     * Response: HTTP 500 Internal Server Error
     *
     * We do NOT expose the raw SQL error to the client (security best practice).
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiError> handleDatabaseError(DataAccessException ex) {
        ApiError body = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Database Error",
                List.of("A database error occurred. Please try again later.")
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Handle 500 — Catch-all for unexpected errors
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Safety net: catches any exception not handled by the methods above.
     * Response: HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        ApiError body = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                List.of("An unexpected error occurred. Please try again later.")
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
