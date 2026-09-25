package com.bank.loan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application Entry Point for Bank Loan Default Prediction System.
 */
@SpringBootApplication
public class BankLoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankLoanApplication.class, args);
        System.out.println("\n========================================================");
        System.out.println("  🚀 Bank Loan Default Prediction REST API is Live!");
        System.out.println("  API Endpoint: http://localhost:8080/api/v1/loan/predict");
        System.out.println("  Health Check: http://localhost:8080/api/v1/loan/health");
        System.out.println("========================================================\n");
    }
}
