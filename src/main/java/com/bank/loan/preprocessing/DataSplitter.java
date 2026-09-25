package com.bank.loan.preprocessing;

import weka.core.Instances;

import java.util.Random;

/**
 * Phase 6: Stratified Train/Test Data Splitter.
 * Divides dataset into training (80%) and testing (20%) subsets
 * while preserving target class distributions.
 */
public class DataSplitter {

    public static class SplitResult {
        private final Instances trainData;
        private final Instances testData;

        public SplitResult(Instances trainData, Instances testData) {
            this.trainData = trainData;
            this.testData = testData;
        }

        public Instances getTrainData() { return trainData; }
        public Instances getTestData() { return testData; }
    }

    /**
     * Splits instances into stratified train and test sets.
     *
     * @param dataset Input dataset
     * @param trainRatio Ratio for training data (e.g. 0.80 for 80%)
     * @param seed Random seed for reproducibility
     * @return SplitResult containing train and test Instances
     */
    public static SplitResult split(Instances dataset, double trainRatio, long seed) {
        System.out.println("Starting Phase 6 Stratified Train/Test Split (Train: " + (int)(trainRatio * 100) + "%)...");

        // Randomize instances with fixed seed
        Instances randData = new Instances(dataset);
        randData.randomize(new Random(seed));

        // Stratify class distribution if class attribute is nominal
        if (randData.classAttribute().isNominal()) {
            randData.stratify(10); // 10-fold stratification basis
        }

        int trainSize = (int) Math.round(randData.numInstances() * trainRatio);
        int testSize = randData.numInstances() - trainSize;

        Instances trainData = new Instances(randData, 0, trainSize);
        Instances testData = new Instances(randData, trainSize, testSize);

        System.out.println("Dataset Split Complete -> Train instances: " + trainData.numInstances()
                + ", Test instances: " + testData.numInstances());

        return new SplitResult(trainData, testData);
    }
}
