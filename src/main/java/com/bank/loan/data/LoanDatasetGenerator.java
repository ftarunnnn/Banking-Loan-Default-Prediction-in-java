package com.bank.loan.data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Phase 2: Synthetic Dataset Generator for Bank Loan Default Prediction.
 * Generates 10,000+ realistic customer records with financial, demographic,
 * and credit risk attributes, incorporating domain logic for loan default probability.
 */
public class LoanDatasetGenerator {

    private static final String DEFAULT_FILE_PATH = "data/loan_default_dataset.csv";
    private static final int DEFAULT_RECORD_COUNT = 10000;

    private static final String[] EMPLOYMENT_TYPES = {"SALARIED", "SELF_EMPLOYED", "RETIRED", "UNEMPLOYED"};
    private static final String[] EDUCATION_LEVELS = {"HIGH_SCHOOL", "BACHELORS", "MASTERS", "PHD"};
    private static final String[] MARITAL_STATUSES = {"SINGLE", "MARRIED", "DIVORCED"};
    private static final String[] LOAN_PURPOSES = {"PERSONAL", "AUTO", "HOME", "EDUCATION", "BUSINESS"};

    public static void main(String[] args) {
        try {
            generateDataset(DEFAULT_FILE_PATH, DEFAULT_RECORD_COUNT);
        } catch (IOException e) {
            System.err.println("Error generating dataset: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String generateDataset(String filePath, int totalRecords) throws IOException {
        File dataDir = new File(filePath).getParentFile();
        if (dataDir != null && !dataDir.exists()) {
            dataDir.mkdirs();
        }

        Random rand = new Random(42); // Fixed seed for reproducibility

        String[] headers = {
            "customer_id", "age", "income", "employment_length", "loan_amount",
            "loan_term", "interest_rate", "credit_score", "dti_ratio", "existing_loans",
            "previous_defaults", "employment_type", "education", "marital_status",
            "loan_purpose", "loan_default"
        };

        try (FileWriter out = new FileWriter(filePath);
             CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.builder().setHeader(headers).build())) {

            for (int i = 1; i <= totalRecords; i++) {
                String customerId = "CUST-" + String.format("%06d", i);
                int age = 21 + rand.nextInt(50); // 21-70
                double income = 18000 + (rand.nextDouble() * 182000); // 18k - 200k
                int empLength = Math.max(0, Math.min(age - 18, rand.nextInt(35)));
                double loanAmount = 3000 + (rand.nextDouble() * 87000); // 3k - 90k
                int loanTerm = (rand.nextInt(5) + 1) * 12; // 12, 24, 36, 48, 60
                double interestRate = 5.0 + (rand.nextDouble() * 20.0); // 5% - 25%
                int creditScore = 300 + rand.nextInt(550); // 300 - 850
                double dtiRatio = 0.05 + (rand.nextDouble() * 0.65); // 0.05 - 0.70
                int existingLoans = rand.nextInt(7); // 0 - 6
                int previousDefaults = rand.nextDouble() < 0.15 ? (1 + rand.nextInt(3)) : 0; // 0 - 3

                String empType = EMPLOYMENT_TYPES[rand.nextInt(EMPLOYMENT_TYPES.length)];
                String education = EDUCATION_LEVELS[rand.nextInt(EDUCATION_LEVELS.length)];
                String maritalStatus = MARITAL_STATUSES[rand.nextInt(MARITAL_STATUSES.length)];
                String loanPurpose = LOAN_PURPOSES[rand.nextInt(LOAN_PURPOSES.length)];

                // Domain Logic Risk Score Calculation
                double riskScore = 0.0;

                // Credit Score impact
                if (creditScore < 580) riskScore += 0.35;
                else if (creditScore < 670) riskScore += 0.20;
                else if (creditScore < 740) riskScore += 0.08;

                // DTI ratio impact
                if (dtiRatio > 0.45) riskScore += 0.25;
                else if (dtiRatio > 0.35) riskScore += 0.15;

                // Income vs Loan ratio impact
                double loanToIncome = loanAmount / income;
                if (loanToIncome > 0.6) riskScore += 0.20;

                // Interest rate impact
                if (interestRate > 15.0) riskScore += 0.15;

                // Previous defaults impact
                if (previousDefaults > 0) riskScore += 0.30 * previousDefaults;

                // Employment type impact
                if ("UNEMPLOYED".equals(empType)) riskScore += 0.25;
                if ("SELF_EMPLOYED".equals(empType)) riskScore += 0.08;

                // Determine default status based on risk score + random noise
                double defaultProb = 1.0 / (1.0 + Math.exp(- (riskScore - 0.65) * 3.5));
                int loanDefault = (rand.nextDouble() < defaultProb) ? 1 : 0;

                // Simulate ~1.5% missing values for robustness testing
                String incomeStr = (rand.nextDouble() < 0.015) ? "?" : String.format("%.2f", income);
                String empLengthStr = (rand.nextDouble() < 0.015) ? "?" : String.valueOf(empLength);
                String creditScoreStr = (rand.nextDouble() < 0.015) ? "?" : String.valueOf(creditScore);

                printer.printRecord(
                    customerId,
                    age,
                    incomeStr,
                    empLengthStr,
                    String.format("%.2f", loanAmount),
                    loanTerm,
                    String.format("%.2f", interestRate),
                    creditScoreStr,
                    String.format("%.4f", dtiRatio),
                    existingLoans,
                    previousDefaults,
                    empType,
                    education,
                    maritalStatus,
                    loanPurpose,
                    loanDefault
                );
            }
        }

        System.out.println("Dataset generated successfully at: " + filePath + " (" + totalRecords + " records)");
        return filePath;
    }
}
