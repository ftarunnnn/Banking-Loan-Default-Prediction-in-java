package com.bank.loan.model;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.File;

/**
 * Phase 9: Model Persistence Service for serializing and deserializing
 * trained Weka models and dataset header specifications to disk.
 */
public class ModelPersistence {

    public static final String DEFAULT_MODEL_PATH = "data/trained_loan_model.model";
    public static final String DEFAULT_HEADER_PATH = "data/dataset_header.meta";

    /**
     * Serializes trained Classifier model and dataset header format to disk.
     *
     * @param model Trained Weka Classifier
     * @param datasetHeader Dataset header structure (for attribute mapping)
     * @param modelFilePath Destination path for model file
     * @throws Exception If serialization fails
     */
    public static void saveModel(Classifier model, Instances datasetHeader, String modelFilePath) throws Exception {
        File dataDir = new File(modelFilePath).getParentFile();
        if (dataDir != null && !dataDir.exists()) {
            dataDir.mkdirs();
        }

        // Save Classifier object
        SerializationHelper.write(modelFilePath, model);

        // Save Dataset Header (empty dataset with attributes structure)
        Instances headerCopy = new Instances(datasetHeader, 0);
        SerializationHelper.write(DEFAULT_HEADER_PATH, headerCopy);

        System.out.println("Model successfully saved to disk: " + modelFilePath);
        System.out.println("Dataset header saved to disk: " + DEFAULT_HEADER_PATH);
    }

    /**
     * Deserializes and loads trained Classifier model from disk.
     *
     * @param modelFilePath Path to serialized .model file
     * @return Loaded Weka Classifier
     * @throws Exception If file reading or deserialization fails
     */
    public static Classifier loadModel(String modelFilePath) throws Exception {
        File modelFile = new File(modelFilePath);
        if (!modelFile.exists()) {
            throw new IllegalArgumentException("Serialized model file not found at: " + modelFilePath);
        }
        Classifier model = (Classifier) SerializationHelper.read(modelFilePath);
        System.out.println("Trained model successfully loaded from: " + modelFilePath);
        return model;
    }

    /**
     * Loads saved dataset header structure from disk.
     */
    public static Instances loadHeader(String headerFilePath) throws Exception {
        File headerFile = new File(headerFilePath);
        if (!headerFile.exists()) {
            throw new IllegalArgumentException("Dataset header meta file not found at: " + headerFilePath);
        }
        return (Instances) SerializationHelper.read(headerFilePath);
    }
}
