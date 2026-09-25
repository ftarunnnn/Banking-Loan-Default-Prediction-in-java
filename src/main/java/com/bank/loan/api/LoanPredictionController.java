package com.bank.loan.api;

import com.bank.loan.prediction.LoanPredictionEngine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Phase 10: REST Controller exposing endpoints for real-time loan prediction,
 * health status checks, and pipeline triggers.
 */
@RestController
@RequestMapping("/api/v1/loan")
@CrossOrigin(origins = "*")
public class LoanPredictionController {

    private LoanPredictionEngine predictionEngine;

    public LoanPredictionController() {
        try {
            this.predictionEngine = new LoanPredictionEngine(
                    "data/trained_loan_model.model",
                    "data/dataset_header.meta"
            );
            System.out.println("LoanPredictionController: Successfully initialized Prediction Engine with serialized model.");
        } catch (Exception e) {
            System.err.println("LoanPredictionController: Warning - Trained model file not found yet. Run pipeline training first! " + e.getMessage());
        }
    }

    /**
     * Endpoint for predicting loan default status based on customer financial and demographic data.
     * POST /api/v1/loan/predict
     */
    @PostMapping("/predict")
    public ResponseEntity<LoanPredictionResponse> predictLoanDefault(@RequestBody LoanApplicationRequest request) {
        if (predictionEngine == null) {
            try {
                this.predictionEngine = new LoanPredictionEngine("data/trained_loan_model.model", "data/dataset_header.meta");
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(LoanPredictionResponse.builder()
                        .predictionLabel("Model Not Trained. Please run training pipeline first.")
                        .build());
            }
        }

        try {
            var result = predictionEngine.predict(
                    request.getAge(),
                    request.getIncome(),
                    request.getEmploymentLength(),
                    request.getLoanAmount(),
                    request.getLoanTerm(),
                    request.getInterestRate(),
                    request.getCreditScore(),
                    request.getDtiRatio(),
                    request.getExistingLoans(),
                    request.getPreviousDefaults(),
                    request.getEmploymentType(),
                    request.getEducation(),
                    request.getMaritalStatus(),
                    request.getLoanPurpose()
            );

            LoanPredictionResponse response = LoanPredictionResponse.builder()
                    .customerId(request.getCustomerId() != null ? request.getCustomerId() : "CUST-REQ-001")
                    .predictionCode(result.getDefaultPredictionCode())
                    .predictionLabel(result.getDefaultPredictionLabel())
                    .defaultProbability(Math.round(result.getDefaultProbability() * 10000.0) / 10000.0)
                    .riskScorePercent(Math.round(result.getRiskScore() * 100.0) / 100.0)
                    .recommendedDecision(result.getDecision())
                    .selectedModel("Random Forest (Weka 3.8.6)")
                    .evaluationTime(LocalDateTime.now())
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(LoanPredictionResponse.builder()
                    .predictionLabel("Error during model prediction: " + e.getMessage())
                    .build());
        }
    }

    /**
     * System health and model state endpoint.
     * GET /api/v1/loan/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Bank Loan Default Prediction System");
        health.put("language", "Java 17");
        health.put("mlFramework", "Weka 3.8.6");
        health.put("modelLoaded", predictionEngine != null);
        health.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(health);
    }
}
