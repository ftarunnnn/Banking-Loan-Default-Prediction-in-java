package com.bank.loan.data;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.File;
import java.io.IOException;

/**
 * Phase 2: Data Loader module for loading CSV datasets into Weka Instances.
 * Handles header parsing, attribute typing, non-predictive column removal,
 * and class attribute assignment.
 */
public class DataLoader {

    /**
     * Loads dataset from CSV file into Weka Instances.
     * Removes identifier columns like 'customer_id' and sets the target class index.
     *
     * @param csvFilePath Path to CSV file
     * @param targetAttribute Name of target class attribute (e.g. "loan_default")
     * @return Weka Instances object ready for preprocessing
     * @throws Exception If file reading or parsing fails
     */
    public static Instances loadDataset(String csvFilePath, String targetAttribute) throws Exception {
        File inputFile = new File(csvFilePath);
        if (!inputFile.exists()) {
            throw new IOException("CSV Dataset file not found at: " + csvFilePath);
        }

        CSVLoader loader = new CSVLoader();
        loader.setSource(inputFile);
        loader.setMissingValue("?"); // Weka missing value identifier
        Instances dataset = loader.getDataSet();

        System.out.println("Dataset loaded from CSV: " + dataset.numInstances() + " instances, " + dataset.numAttributes() + " attributes.");

        // Remove non-predictive identifier column 'customer_id' if present
        int customerIdIdx = dataset.attribute("customer_id") != null ? dataset.attribute("customer_id").index() : -1;
        if (customerIdIdx != -1) {
            Remove removeFilter = new Remove();
            removeFilter.setAttributeIndices(String.valueOf(customerIdIdx + 1)); // 1-based index in Weka filters
            removeFilter.setInputFormat(dataset);
            dataset = Filter.useFilter(dataset, removeFilter);
            System.out.println("Removed identifier column 'customer_id'. Remaining attributes: " + dataset.numAttributes());
        }

        // Set class attribute index
        if (targetAttribute != null) {
            if (dataset.attribute(targetAttribute) != null) {
                dataset.setClass(dataset.attribute(targetAttribute));
                System.out.println("Set class attribute to: '" + targetAttribute + "' (Index: " + dataset.classIndex() + ")");
            } else {
                // Default to last attribute
                dataset.setClassIndex(dataset.numAttributes() - 1);
                System.out.println("Target attribute '" + targetAttribute + "' not found. Set default class index: " + dataset.classIndex());
            }
        } else {
            dataset.setClassIndex(dataset.numAttributes() - 1);
        }

        return dataset;
    }
}
