package com.bank.loan.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Phase 10: DTO Request object for Loan Default Prediction API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationRequest {

    private String customerId;
    private int age;
    private double income;
    private int employmentLength;
    private double loanAmount;
    private int loanTerm;
    private double interestRate;
    private int creditScore;
    private double dtiRatio;
    private int existingLoans;
    private int previousDefaults;
    private String employmentType; // SALARIED, SELF_EMPLOYED, RETIRED, UNEMPLOYED
    private String education;      // HIGH_SCHOOL, BACHELORS, MASTERS, PHD
    private String maritalStatus;  // SINGLE, MARRIED, DIVORCED
    private String loanPurpose;    // PERSONAL, AUTO, HOME, EDUCATION, BUSINESS
}
