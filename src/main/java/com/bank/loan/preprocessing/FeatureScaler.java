package com.bank.loan.preprocessing;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

/**
 * Phase 5: Feature Scaler for Min-Max Normalization of numeric attributes.
 * Scales numeric ranges (e.g. income, loan_amount, credit_score) to [0, 1]
 * to improve model convergence and numerical stability.
 */
public class FeatureScaler {

    /**
     * Normalizes numeric attributes to [0, 1] using Weka Normalize filter.
     *
     * @param dataset Input Weka Instances
     * @return Normalized Weka Instances
     * @throws Exception If normalization fails
     */
    public static Instances normalizeFeatures(Instances dataset) throws Exception {
        System.out.println("Starting Phase 5 Feature Scaling & Min-Max Normalization...");

        Normalize normalizeFilter = new Normalize();
        normalizeFilter.setIgnoreClass(true); // Ensure class target is not modified
        normalizeFilter.setInputFormat(dataset);
        Instances normalizedDataset = Filter.useFilter(dataset, normalizeFilter);

        System.out.println("Feature normalization complete for all numeric attributes.");
        return normalizedDataset;
    }
}
