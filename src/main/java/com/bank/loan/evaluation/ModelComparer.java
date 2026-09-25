package com.bank.loan.evaluation;

import java.util.Comparator;
import java.util.List;

/**
 * Phase 8: Model Comparer module.
 * Displays comparative evaluation table and selects the best performing model based on F1-Score.
 */
public class ModelComparer {

    public static ModelEvaluator.PerformanceMetrics compareAndSelectBestModel(List<ModelEvaluator.PerformanceMetrics> metricsList) {
        System.out.println("\n==========================================================================================");
        System.out.println("                        PHASE 8: MODEL COMPARISON MATRIX");
        System.out.println("==========================================================================================");
        System.out.printf("%-22s | %-12s | %-12s | %-12s | %-12s | %-10s%n",
                "Model Name", "Accuracy (%)", "Precision", "Recall", "F1-Score", "ROC AUC");
        System.out.println("------------------------------------------------------------------------------------------");

        for (ModelEvaluator.PerformanceMetrics m : metricsList) {
            System.out.printf("%-22s | %-12.2f | %-12.4f | %-12.4f | %-12.4f | %-10.4f%n",
                    m.getModelName(), m.getAccuracy(), m.getPrecision(), m.getRecall(), m.getF1Score(), m.getRocAuc());

            // Print Confusion Matrix
            double[][] cm = m.getConfusionMatrix();
            System.out.println("   Confusion Matrix:");
            System.out.printf("      [TN: %-5.0f  FP: %-5.0f]%n", cm[0][0], cm[0][1]);
            System.out.printf("      [FN: %-5.0f  TP: %-5.0f]%n", cm[1][0], cm[1][1]);
            System.out.println("------------------------------------------------------------------------------------------");
        }

        // Select champion model with highest F1-Score
        ModelEvaluator.PerformanceMetrics bestModel = metricsList.stream()
                .max(Comparator.comparingDouble(ModelEvaluator.PerformanceMetrics::getF1Score))
                .orElse(metricsList.get(0));

        System.out.printf("%n🏆 CHAMPION MODEL SELECTED: %s (F1-Score: %.4f, Accuracy: %.2f%%)%n",
                bestModel.getModelName(), bestModel.getF1Score(), bestModel.getAccuracy());
        System.out.println("==========================================================================================\n");

        return bestModel;
    }
}
