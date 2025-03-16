package cz.cuni.mff.vopalenf.aimock.service;

import cz.cuni.mff.vopalenf.aimock.api.model.LogData;
import lombok.Getter;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataPreprocessor {

    private static final int FEATURES_COUNT = 10;

    @Getter
    private static List<String> uniqueLabels;

    public DataPreprocessor() {
        // empty constructor
    }

    public static DataSet preprocessTrainData(List<LogData> logDataList) {
        uniqueLabels = logDataList.stream()
                .map(LogData::getLabel)
                .distinct()
                .toList();


        Map<String, double[]> labelMap = new HashMap<>();
        for (int i = 0; i < uniqueLabels.size(); i++) {
            double[] oneHot = new double[uniqueLabels.size()];
            oneHot[i] = 1.0;
            labelMap.put(uniqueLabels.get(i), oneHot);
        }

        int numSamples = logDataList.size();
        int numLabels = uniqueLabels.size();

        double[][] featureArray = new double[numSamples][FEATURES_COUNT];
        double[][] labelArray = new double[numSamples][numLabels];

        for (int i = 0; i < numSamples; i++) {
            LogData data = logDataList.get(i);

            featureArray[i] = new double[]{
                    data.getCicS(), data.getCicW(), data.getCicN(),
                    data.getCicE(), data.getCicC(),
                    data.getSdS(), data.getSdW(), data.getSdN(),
                    data.getSdE(), data.getSdC()
            };

            labelArray[i] = labelMap.get(data.getLabel());
        }

        INDArray features = Nd4j.create(featureArray);
        INDArray labels = Nd4j.create(labelArray);

        return new DataSet(features, labels);
    }

    public static INDArray preprocessTestData(List<LogData> testDataList) {
        int numSamples = testDataList.size();

        double[][] featureArray = new double[numSamples][FEATURES_COUNT];

        for (int i = 0; i < numSamples; i++) {
            LogData data = testDataList.get(i);

            featureArray[i] = new double[]{
                    data.getCicS(), data.getCicW(), data.getCicN(),
                    data.getCicE(), data.getCicC(),
                    data.getSdS(), data.getSdW(), data.getSdN(),
                    data.getSdE(), data.getSdC()
            };
        }

        return Nd4j.create(featureArray);
    }
}
