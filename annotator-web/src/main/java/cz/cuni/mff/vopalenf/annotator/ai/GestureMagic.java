package cz.cuni.mff.vopalenf.annotator.ai;

import cz.cuni.mff.vopalenf.annotator.api.model.LogData;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GestureMagic {

    NormalizerMinMaxScaler scaler;
    private MultiLayerNetwork model;
    private Long projectId;


    private void initModel(INDArray features, INDArray labels) {
        int inputSize = features.columns();
        int outputSize = labels.columns();

        // Neural network configuration
        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .seed(123)
                .updater(new org.nd4j.linalg.learning.config.Adam(0.01))
                .list()
                .layer(new DenseLayer.Builder().nIn(inputSize).nOut(64)
                        .activation(Activation.RELU).build())
                .layer(new DenseLayer.Builder().nOut(32).activation(Activation.RELU).build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nOut(outputSize).build())
                .build();

        model = new MultiLayerNetwork(config);
        model.init();
    }

    public void train(List<LogData> logDataList, Long projectId) {
        this.projectId = projectId;

        DataSet dataSet = DataPreprocessor.preprocessTrainData(logDataList);

        scaler = new NormalizerMinMaxScaler();
        scaler.fit(dataSet);
        scaler.transform(dataSet);

        INDArray features = dataSet.getFeatures();
        INDArray labels = dataSet.getLabels();

        initModel(features, labels);

        for (int epoch = 0; epoch < 50; epoch++) {
            model.fit(dataSet);
        }
    }

    public List<PredictionTriple> test(List<LogData> testDataList) {
        INDArray testFeatures = DataPreprocessor.preprocessTestData(testDataList);
        scaler.transform(testFeatures);

        INDArray predictions = model.output(testFeatures);

        return returnPredictedLabels(predictions, DataPreprocessor.uniqueLabels);
    }

    private List<PredictionTriple> returnPredictedLabels(INDArray predictions, List<String> labels) {
        List<PredictionTriple> resultingPredictions = new ArrayList<>();
        for (int i = 0; i < predictions.rows(); i++) {
            INDArray row = predictions.getRow(i);

            double maxValue = Double.NEGATIVE_INFINITY;
            int predictedIndex = -1;

            for (int j = 0; j < row.columns(); j++) {
                double value = row.getDouble(j);
                if (value > maxValue) {
                    maxValue = value;
                    predictedIndex = j;
                }
            }

            String predictedLabel = labels.get(predictedIndex);

            PredictionTriple prediction = new PredictionTriple(projectId, (long) i, predictedLabel);
            resultingPredictions.add(prediction);

            // TODO send result data at BE endpoint

            //System.out.println("Sample " + i + ": Predicted label = " + predictedLabel);
        }
        return resultingPredictions;
    }
}
