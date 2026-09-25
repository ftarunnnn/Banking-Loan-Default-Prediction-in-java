package com.bank.loan.model;

import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Phase 7: Model Trainer for Bank Loan Default Prediction.
 * Instantiates, configures, and trains Random Forest, Logistic Regression,
 * and Decision Tree (J48) classifiers using pure Java ML library (Weka).
 */
public class ModelTrainer {

    /**
     * Trains all three ML models on the provided training dataset.
     *
     * @param trainData Preprocessed and scaled training Instances
     * @return Map of model names to trained Weka Classifiers
     * @throws Exception If model training fails
     */
    public static Map<String, Classifier> trainAllModels(Instances trainData) throws Exception {
        System.out.println("\n========================================================");
        System.out.println("            PHASE 7: MODEL TRAINING PIPELINE");
        System.out.println("========================================================");

        Map<String, Classifier> trainedModels = new LinkedHashMap<>();

        // 1. Train Random Forest Classifier
        System.out.println("--> Training Model 1: Random Forest (100 Trees)...");
        RandomForest randomForest = new RandomForest();
        randomForest.setNumExecutionSlots(Runtime.getRuntime().availableProcessors());
        randomForest.setNumIterations(100);
        randomForest.setSeed(42);
        randomForest.buildClassifier(trainData);
        trainedModels.put("Random Forest", randomForest);
        System.out.println("    Random Forest training complete.");

        // 2. Train Logistic Regression Classifier
        System.out.println("--> Training Model 2: Logistic Regression...");
        Logistic logistic = new Logistic();
        logistic.setMaxIts(200);
        logistic.buildClassifier(trainData);
        trainedModels.put("Logistic Regression", logistic);
        System.out.println("    Logistic Regression training complete.");

        // 3. Train Decision Tree (J48 / C4.5) Classifier
        System.out.println("--> Training Model 3: Decision Tree (J48)...");
        J48 decisionTree = new J48();
        decisionTree.setConfidenceFactor(0.25f);
        decisionTree.setMinNumObj(2);
        decisionTree.buildClassifier(trainData);
        trainedModels.put("Decision Tree (J48)", decisionTree);
        System.out.println("    Decision Tree training complete.");

        System.out.println("========================================================\n");
        return trainedModels;
    }
}
