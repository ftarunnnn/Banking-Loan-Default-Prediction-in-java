package com.bank.loan.preprocessing;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

/**
 * Phase 3: Data Preprocessor for missing value imputation and target class formatting.
 * Implements statistical mean/mode replacement for missing data fields.
 */
public class DataPreprocessor {

    /**
     * Preprocesses raw Weka Instances by imputing missing values and formatting target class.
     *
     * @param dataset Raw Weka Instances
     * @return Cleaned Weka Instances with zero missing values
     * @throws Exception If filtering fails
     */
    public static Instances preprocess(Instances dataset) throws Exception {
        System.out.println("Starting Phase 3 Data Preprocessing...");
        int initialMissingCount = countTotalMissingValues(dataset);
        System.out.println("Initial missing values count across dataset: " + initialMissingCount);

        // 1. Convert target class to Nominal if numeric (0/1 -> Nominal "0"/"1")
        if (dataset.classIndex() != -1 && dataset.classAttribute().isNumeric()) {
            NumericToNominal numToNominal = new NumericToNominal();
            numToNominal.setAttributeIndices(String.valueOf(dataset.classIndex() + 1));
            numToNominal.setInputFormat(dataset);
            dataset = Filter.useFilter(dataset, numToNominal);
            System.out.println("Converted numeric class target to Nominal attribute.");
        }

        // 2. Impute missing values using Weka ReplaceMissingValues (Mean for Numeric, Mode for Nominal)
        ReplaceMissingValues missingFilter = new ReplaceMissingValues();
        missingFilter.setInputFormat(dataset);
        Instances cleanedDataset = Filter.useFilter(dataset, missingFilter);

        int remainingMissingCount = countTotalMissingValues(cleanedDataset);
        System.out.println("Missing value imputation completed. Remaining missing values: " + remainingMissingCount);

        return cleanedDataset;
    }

    /**
     * Helper method to count total missing value cells across all instances and attributes.
     */
    public static int countTotalMissingValues(Instances dataset) {
        int missingCount = 0;
        for (int i = 0; i < dataset.numInstances(); i++) {
            for (int j = 0; j < dataset.numAttributes(); j++) {
                if (dataset.instance(i).isMissing(j)) {
                    missingCount++;
                }
            }
        }
        return missingCount;
    }
}
