package com.bank.loan.prediction;

import com.bank.loan.model.ModelPersistence;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Phase 9: Real-time Loan Prediction Engine.
 * Accepts raw customer input attributes, builds Weka Instance matching trained dataset schema,
 * performs model inference, calculates default probability, and evaluates credit risk decision.
 */
public class LoanPredictionEngine {

    private final Classifier model;
    private final Instances datasetHeader;

    public LoanPredictionEngine(String modelFilePath, String headerFilePath) throws Exception {
        this.model = ModelPersistence.loadModel(modelFilePath);
        this.datasetHeader = ModelPersistence.loadHeader(headerFilePath);
    }

    public LoanPredictionEngine(Classifier model, Instances datasetHeader) {
        this.model = model;
        this.datasetHeader = new Instances(datasetHeader, 0);
    }

    public static class PredictionResult {
        private final int defaultPredictionCode; // 0 = No Default, 1 = Default
        private final String defaultPredictionLabel; // "No Default" or "Default"
        private final double defaultProbability; // 0.0 to 1.0
        private final double riskScore; // 0 to 100 risk score
        private final String decision; // "APPROVE", "REJECT", "MANUAL_REVIEW"

        public PredictionResult(int defaultPredictionCode, String defaultPredictionLabel,
                                double defaultProbability, double riskScore, String decision) {
            this.defaultPredictionCode = defaultPredictionCode;
            this.defaultPredictionLabel = defaultPredictionLabel;
            this.defaultProbability = defaultProbability;
            this.riskScore = riskScore;
            this.decision = decision;
        }

        public int getDefaultPredictionCode() { return defaultPredictionCode; }
        public String getDefaultPredictionLabel() { return defaultPredictionLabel; }
        public double getDefaultProbability() { return defaultProbability; }
        public double getRiskScore() { return riskScore; }
        public String getDecision() { return decision; }
    }

    /**
     * Performs model prediction and risk decision for a single customer application.
     */
    public PredictionResult predict(
            int age, double income, int employmentLength, double loanAmount,
            int loanTerm, double interestRate, int creditScore, double dtiRatio,
            int existingLoans, int previousDefaults, String employmentType,
            String education, String maritalStatus, String loanPurpose) throws Exception {

        Instance instance = new DenseInstance(datasetHeader.numAttributes());
        instance.setDataset(datasetHeader);

        // Set attribute values matching dataset schema
        setValueIfPresent(instance, "age", age);
        setValueIfPresent(instance, "income", income);
        setValueIfPresent(instance, "employment_length", employmentLength);
        setValueIfPresent(instance, "loan_amount", loanAmount);
        setValueIfPresent(instance, "loan_term", loanTerm);
        setValueIfPresent(instance, "interest_rate", interestRate);
        setValueIfPresent(instance, "credit_score", creditScore);
        setValueIfPresent(instance, "dti_ratio", dtiRatio);
        setValueIfPresent(instance, "existing_loans", existingLoans);
        setValueIfPresent(instance, "previous_defaults", previousDefaults);

        setNominalIfPresent(instance, "employment_type", employmentType);
        setNominalIfPresent(instance, "education", education);
        setNominalIfPresent(instance, "marital_status", maritalStatus);
        setNominalIfPresent(instance, "loan_purpose", loanPurpose);

        // Class attribute is set to missing before evaluation
        instance.setClassMissing();

        // Perform model inference
        double predictionIndex = model.classifyInstance(instance);
        double[] distribution = model.distributionForInstance(instance);

        int defaultCode = (int) predictionIndex;
        String defaultLabel = (datasetHeader.classAttribute().isNominal())
                ? datasetHeader.classAttribute().value(defaultCode)
                : (defaultCode == 1 ? "Default" : "No Default");

        // Probability of Class 1 (Default)
        double defaultProb = distribution.length > 1 ? distribution[1] : distribution[0];
        double riskScore = defaultProb * 100.0;

        // Recommendation decision logic
        String decision;
        if (defaultProb >= 0.50) {
            decision = "REJECT";
        } else if (defaultProb >= 0.30) {
            decision = "MANUAL_REVIEW";
        } else {
            decision = "APPROVE";
        }

        return new PredictionResult(defaultCode, defaultLabel, defaultProb, riskScore, decision);
    }

    private void setValueIfPresent(Instance inst, String attrName, double val) {
        var attr = datasetHeader.attribute(attrName);
        if (attr != null) inst.setValue(attr, val);
    }

    private void setNominalIfPresent(Instance inst, String attrName, String val) {
        var attr = datasetHeader.attribute(attrName);
        if (attr != null && attr.isNominal()) {
            int idx = attr.indexOfValue(val);
            if (idx != -1) {
                inst.setValue(attr, val);
            } else {
                inst.setValue(attr, attr.value(0)); // fallback to default nominal value
            }
        }
    }
}
