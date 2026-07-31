package com.expensetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full Spring context integration tests hitting the real HTTP layer via
 * MockMvc, so validation, JSON (de)serialization, and exception handling
 * are exercised end to end - not just the service in isolation.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void addExpense_thenAppearsInGetAll() throws Exception {
        Map<String, Object> payload = Map.of(
                "title", "Groceries",
                "amount", 45.50,
                "category", "Food",
                "date", "2026-07-01"
        );

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Groceries")));

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void addExpense_missingTitle_returns400() throws Exception {
        Map<String, Object> payload = Map.of(
                "amount", 45.50,
                "category", "Food",
                "date", "2026-07-01"
        );

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")));
    }

    @Test
    void addExpense_negativeAmount_returns400() throws Exception {
        Map<String, Object> payload = Map.of(
                "title", "Refund",
                "amount", -10,
                "category", "Food",
                "date", "2026-07-01"
        );

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void filterByCategory_onlyReturnsMatchingExpenses() throws Exception {
        addExpense("Groceries", 45.50, "Food", "2026-07-01");
        addExpense("Bus pass", 20.00, "Transport", "2026-07-02");

        mockMvc.perform(get("/api/expenses").param("category", "Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].category", everyItem(is("Food"))));
    }

    @Test
    void getTotal_overall_returnsSum() throws Exception {
        addExpense("Coffee", 5.00, "Food", "2026-07-01");

        mockMvc.perform(get("/api/expenses/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", greaterThanOrEqualTo(5.0)));
    }

    @Test
    void deleteExpense_thenGetById_returns404() throws Exception {
        String id = addExpense("One-off", 12.00, "Misc", "2026-07-05");

        mockMvc.perform(delete("/api/expenses/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/expenses/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    void deleteExpense_unknownId_returns404() throws Exception {
        mockMvc.perform(delete("/api/expenses/{id}", "not-a-real-id"))
                .andExpect(status().isNotFound());
    }

    /** Helper: creates an expense via the real endpoint and returns its generated id. */
    private String addExpense(String title, double amount, String category, String date) throws Exception {
        Map<String, Object> payload = Map.of(
                "title", title, "amount", amount, "category", category, "date", date
        );
        String response = mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }
}
