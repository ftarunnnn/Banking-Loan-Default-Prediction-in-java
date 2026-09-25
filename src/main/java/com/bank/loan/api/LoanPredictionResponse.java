package com.bank.loan.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Phase 10: DTO Response object for Loan Default Prediction API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanPredictionResponse {

    private String customerId;
    private int predictionCode;            // 0 = No Default, 1 = Default
    private String predictionLabel;         // "No Default" or "Default"
    private double defaultProbability;     // e.g. 0.1254 (12.54%)
    private double riskScorePercent;        // e.g. 12.54
    private String recommendedDecision;    // "APPROVE", "REJECT", "MANUAL_REVIEW"
    private String selectedModel;          // e.g. "Random Forest"
    private LocalDateTime evaluationTime;
}
