package com.expensetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * ExpenseTrackerApplication — The Starting Point
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * This is the ENTRY POINT of the entire Spring Boot application.
 * When you run this class, Java starts here (at the main method),
 * and Spring Boot wakes up and does the following automatically:
 *
 *   1. Reads application.yml → finds MySQL connection details
 *   2. Connects to the MySQL database
 *   3. Runs schema.sql → creates the 'transactions' table if missing
 *   4. Scans all packages under com.expensetracker for:
 *        - @RestController  → sets up REST API endpoints
 *        - @Service         → registers business logic classes
 *        - @Repository      → registers database classes
 *   5. Starts an embedded Tomcat web server on port 8080
 *   6. Your API is now live and ready to accept requests!
 *
 * @SpringBootApplication is a shortcut for three annotations combined:
 *   @SpringBootConfiguration  → marks this as a configuration class
 *   @EnableAutoConfiguration  → lets Spring auto-configure everything
 *   @ComponentScan            → scans this package and sub-packages
 *
 * How to run:
 *   mvn spring-boot:run   (from command line)
 *   OR press Run in your IDE
 *
 * After starting, visit:
 *   http://localhost:8080/api/expenses        → REST API
 *   http://localhost:8080/swagger-ui.html     → Interactive API docs
 *   http://localhost:8080/actuator/health     → Health check
 * ═══════════════════════════════════════════════════════════════════════════
 */
@SpringBootApplication
public class ExpenseTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerApplication.class, args);
    }
}
