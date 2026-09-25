package com.bank.loan.preprocessing;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.StringToNominal;

/**
 * Phase 4: Categorical Encoder for encoding categorical features into nominal
 * representation and optional One-Hot Binary vectors using Weka 3.8.6 API.
 */
public class CategoricalEncoder {

    /**
     * Converts String attributes to Nominal and optionally applies One-Hot Encoding.
     *
     * @param dataset Input Weka Instances
     * @param applyOneHot If true, applies NominalToBinary encoding
     * @return Encoded Instances
     * @throws Exception If encoding filter fails
     */
    public static Instances encodeCategoricalFeatures(Instances dataset, boolean applyOneHot) throws Exception {
        System.out.println("Starting Phase 4 Categorical Feature Encoding...");

        // 1. Convert all String attributes to Nominal
        StringToNominal stringToNominal = new StringToNominal();
        stringToNominal.setAttributeRange("first-last");
        stringToNominal.setInputFormat(dataset);
        Instances nominalDataset = Filter.useFilter(dataset, stringToNominal);
        System.out.println("Converted string attributes to Nominal indices.");

        if (!applyOneHot) {
            return nominalDataset;
        }

        // 2. Apply One-Hot Encoding (NominalToBinary)
        NominalToBinary nominalToBinary = new NominalToBinary();
        nominalToBinary.setInputFormat(nominalDataset);
        Instances binaryDataset = Filter.useFilter(nominalDataset, nominalToBinary);

        System.out.println("Applied One-Hot Encoding (NominalToBinary). New total attributes count: " + binaryDataset.numAttributes());
        return binaryDataset;
    }
}
