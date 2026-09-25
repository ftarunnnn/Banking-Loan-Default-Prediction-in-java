package com.bank.loan.evaluation;

import weka.core.Attribute;
import weka.core.Instances;

/**
 * Phase 6: Exploratory Data Analysis (EDA) module.
 * Inspects feature distributions, missing values, class balance,
 * and summary statistics of dataset attributes.
 */
public class ExploratoryDataAnalysis {

    public static void performEDA(Instances dataset) {
        System.out.println("\n========================================================");
        System.out.println("            EXPLORATORY DATA ANALYSIS (EDA)");
        System.out.println("========================================================");
        System.out.println("Total Instances (Records): " + dataset.numInstances());
        System.out.println("Total Attributes (Features): " + dataset.numAttributes());

        // Class Target Summary
        Attribute classAttr = dataset.classAttribute();
        System.out.println("\n--- Target Class Distribution ('" + classAttr.name() + "') ---");
        int[] classCounts = dataset.attributeStats(dataset.classIndex()).nominalCounts;
        for (int i = 0; i < classAttr.numValues(); i++) {
            double pct = (classCounts[i] * 100.0) / dataset.numInstances();
            System.out.printf("  Value '%s': %d records (%.2f%%)%n", classAttr.value(i), classCounts[i], pct);
        }

        // Feature Summary Table
        System.out.println("\n--- Feature Summary Statistics ---");
        System.out.printf("%-20s %-10s %-12s %-12s %-12s %-10s%n",
                "Attribute", "Type", "Min / Values", "Max / Values", "Mean / Top", "Missing %");
        System.out.println("----------------------------------------------------------------------------------");

        for (int i = 0; i < dataset.numAttributes(); i++) {
            Attribute attr = dataset.attribute(i);
            var stats = dataset.attributeStats(i);
            double missingPct = (stats.missingCount * 100.0) / dataset.numInstances();

            if (attr.isNumeric()) {
                System.out.printf("%-20s %-10s %-12.2f %-12.2f %-12.2f %-10.2f%%%n",
                        attr.name(), "Numeric", stats.numericStats.min, stats.numericStats.max, stats.numericStats.mean, missingPct);
            } else if (attr.isNominal()) {
                System.out.printf("%-20s %-10s %-12d %-12s %-12s %-10.2f%%%n",
                        attr.name(), "Nominal", attr.numValues(), "N/A", attr.value(0), missingPct);
            }
        }
        System.out.println("========================================================\n");
    }
}
