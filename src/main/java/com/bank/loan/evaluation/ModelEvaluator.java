package com.bank.loan.evaluation;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 * Phase 8: Model Evaluator for computing classification performance metrics:
 * Accuracy, Precision, Recall, F1-Score, ROC AUC, and Confusion Matrix.
 */
public class ModelEvaluator {

    public static class PerformanceMetrics {
        private final String modelName;
        private final double accuracy;
        private final double precision;
        private final double recall;
        private final double f1Score;
        private final double rocAuc;
        private final double[][] confusionMatrix;

        public PerformanceMetrics(String modelName, double accuracy, double precision,
                                  double recall, double f1Score, double rocAuc, double[][] confusionMatrix) {
            this.modelName = modelName;
            this.accuracy = accuracy;
            this.precision = precision;
            this.recall = recall;
            this.f1Score = f1Score;
            this.rocAuc = rocAuc;
            this.confusionMatrix = confusionMatrix;
        }

        public String getModelName() { return modelName; }
        public double getAccuracy() { return accuracy; }
        public double getPrecision() { return precision; }
        public double getRecall() { return recall; }
        public double getF1Score() { return f1Score; }
        public double getRocAuc() { return rocAuc; }
        public double[][] getConfusionMatrix() { return confusionMatrix; }
    }

    /**
     * Evaluates a trained Weka classifier on the test dataset.
     *
     * @param model Trained Weka Classifier
     * @param modelName Name of model
     * @param testData Test Instances
     * @return PerformanceMetrics object containing evaluation results
     * @throws Exception If evaluation fails
     */
    public static PerformanceMetrics evaluateModel(Classifier model, String modelName, Instances testData) throws Exception {
        Evaluation eval = new Evaluation(testData);
        eval.evaluateModel(model, testData);

        double accuracy = eval.pctCorrect();
        double precision = eval.weightedPrecision();
        double recall = eval.weightedRecall();
        double f1Score = eval.weightedFMeasure();
        double rocAuc = eval.weightedAreaUnderROC();
        double[][] confusionMatrix = eval.confusionMatrix();

        return new PerformanceMetrics(modelName, accuracy, precision, recall, f1Score, rocAuc, confusionMatrix);
    }
}
