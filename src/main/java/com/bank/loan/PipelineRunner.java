package com.bank.loan;

import com.bank.loan.data.DataLoader;
import com.bank.loan.data.LoanDatasetGenerator;
import com.bank.loan.evaluation.ExploratoryDataAnalysis;
import com.bank.loan.evaluation.ModelComparer;
import com.bank.loan.evaluation.ModelEvaluator;
import com.bank.loan.model.ModelPersistence;
import com.bank.loan.model.ModelTrainer;
import com.bank.loan.prediction.LoanPredictionEngine;
import com.bank.loan.preprocessing.CategoricalEncoder;
import com.bank.loan.preprocessing.DataPreprocessor;
import com.bank.loan.preprocessing.DataSplitter;
import com.bank.loan.preprocessing.FeatureScaler;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * End-to-End Orchestrator for the 10-Phase Java Machine Learning Pipeline.
 * Executes Dataset Generation -> Ingestion -> Preprocessing -> Encoding ->
 * Scaling -> Splitting -> EDA -> Training -> Evaluation -> Model Selection ->
 * Serialization -> Sample Prediction.
 */
public class PipelineRunner {

    public static void main(String[] args) {
        try {
            System.out.println("\n========================================================");
            System.out.println("  🏦 BANK LOAN DEFAULT PREDICTION SYSTEM (PURE JAVA ML)");
            System.out.println("========================================================\n");

            // Phase 2: Generate 10,000+ Record Dataset & Load
            String csvPath = "data/loan_default_dataset.csv";
            System.out.println("--- PHASE 2: DATASET PREPARATION & INGESTION ---");
            LoanDatasetGenerator.generateDataset(csvPath, 10000);
            Instances rawDataset = DataLoader.loadDataset(csvPath, "loan_default");

            // Phase 6 (Part A): Exploratory Data Analysis
            ExploratoryDataAnalysis.performEDA(rawDataset);

            // Phase 3: Data Preprocessing & Missing Value Imputation
            System.out.println("--- PHASE 3: DATA PREPROCESSING ---");
            Instances cleanedDataset = DataPreprocessor.preprocess(rawDataset);

            // Phase 4: Categorical Feature Encoding
            System.out.println("--- PHASE 4: CATEGORICAL ENCODING ---");
            Instances encodedDataset = CategoricalEncoder.encodeCategoricalFeatures(cleanedDataset, false);

            // Phase 5: Feature Scaling & Min-Max Normalization
            System.out.println("--- PHASE 5: FEATURE SCALING ---");
            Instances scaledDataset = FeatureScaler.normalizeFeatures(encodedDataset);

            // Phase 6 (Part B): Stratified Train/Test Split (80/20)
            System.out.println("--- PHASE 6: STRATIFIED TRAIN/TEST SPLIT ---");
            DataSplitter.SplitResult split = DataSplitter.split(scaledDataset, 0.80, 42);

            // Phase 7: Model Training Pipeline
            Map<String, Classifier> trainedModels = ModelTrainer.trainAllModels(split.getTrainData());

            // Phase 8: Model Evaluation & Metrics
            System.out.println("--- PHASE 8: MODEL EVALUATION ---");
            List<ModelEvaluator.PerformanceMetrics> metricsList = new ArrayList<>();
            for (Map.Entry<String, Classifier> entry : trainedModels.entrySet()) {
                var metrics = ModelEvaluator.evaluateModel(entry.getValue(), entry.getKey(), split.getTestData());
                metricsList.add(metrics);
            }

            // Phase 8 (Part B): Model Comparison & Selection
            var championMetrics = ModelComparer.compareAndSelectBestModel(metricsList);
            Classifier championModel = trainedModels.get(championMetrics.getModelName());

            // Phase 9: Save Trained Model & Header
            System.out.println("--- PHASE 9: MODEL PERSISTENCE ---");
            String modelPath = "data/trained_loan_model.model";
            ModelPersistence.saveModel(championModel, split.getTrainData(), modelPath);

            // Phase 9 (Part B): Sample Prediction Test
            System.out.println("\n--- TESTING PREDICTION ENGINE INFERENCE ---");
            LoanPredictionEngine engine = new LoanPredictionEngine(championModel, split.getTrainData());

            // Low Risk Customer Test
            var lowRisk = engine.predict(
                    38, 95000.0, 10, 15000.0, 36, 7.5,
                    780, 0.18, 1, 0, "SALARIED", "MASTERS", "MARRIED", "HOME"
            );
            System.out.printf("Sample Customer #1 (High Income, Good Credit) -> Prediction: %s, Probability: %.2f%%, Decision: %s%n",
                    lowRisk.getDefaultPredictionLabel(), lowRisk.getDefaultProbability() * 100, lowRisk.getDecision());

            // High Risk Customer Test
            var highRisk = engine.predict(
                    24, 22000.0, 1, 45000.0, 60, 21.5,
                    520, 0.58, 4, 2, "UNEMPLOYED", "HIGH_SCHOOL", "SINGLE", "PERSONAL"
            );
            System.out.printf("Sample Customer #2 (Low Income, Poor Credit, Past Defaults) -> Prediction: %s, Probability: %.2f%%, Decision: %s%n",
                    highRisk.getDefaultPredictionLabel(), highRisk.getDefaultProbability() * 100, highRisk.getDecision());

            System.out.println("\n========================================================");
            System.out.println("  🎉 COMPLETE JAVA ML PIPELINE EXECUTED SUCCESSFULLY!");
            System.out.println("========================================================\n");

        } catch (Exception e) {
            System.err.println("Pipeline Execution Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
