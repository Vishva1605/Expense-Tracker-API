package com.expensetracker.dto;

import java.time.Instant;
import java.util.List;

/**
 * Standard Error Response DTO.
 *
 * Whenever something goes wrong (4xx or 5xx), the API returns this
 * structured JSON object instead of a raw Java exception dump.
 *
 * Example JSON for a 404 error:
 * {
 *   "timestamp": "2024-06-15T10:30:00Z",
 *   "status": 404,
 *   "error": "Not Found",
 *   "messages": ["Transaction not found with id: abc-123"]
 * }
 *
 * Having a consistent error format makes it easy for frontend apps,
 * Postman tests, and other clients to handle errors reliably.
 */
public class ApiError {

    private Instant timestamp;      // When the error occurred (UTC time)
    private int status;             // HTTP status code (e.g. 400, 404, 500)
    private String error;           // Short label (e.g. "Not Found")
    private List<String> messages;  // Detailed error message(s)

    public ApiError() {
    }

    public ApiError(int status, String error, List<String> messages) {
        this.timestamp = Instant.now();   // automatically captures the current time
        this.status    = status;
        this.error     = error;
        this.messages  = messages;
    }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public List<String> getMessages() { return messages; }
    public void setMessages(List<String> messages) { this.messages = messages; }
}
