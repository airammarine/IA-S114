import java.io.*;
import java.net.URL;
import java.util.*;

public class KNN {
    static class DataPoint {
        double[] features;  
        int outcome;       
        double distance;   

        DataPoint(double[] features, int outcome) {
            this.features = features;
            this.outcome = outcome;
        }
    }

    public static void main(String[] args) {
        try {
            List<DataPoint> dataset = loadDataset();
            
            normalizeDataset(dataset);
    
            Collections.shuffle(dataset); 
            int splitPoint = (int) (dataset.size() * 0.8);
            List<DataPoint> trainSet = new ArrayList<>(dataset.subList(0, splitPoint));
            List<DataPoint> testSet = new ArrayList<>(dataset.subList(splitPoint, dataset.size()));
            
            int k = 5; 
            int correctPredictions = 0;

            for (DataPoint testPoint : testSet) {
                for (DataPoint trainPoint : trainSet) {
                    trainPoint.distance = euclideanDistance(testPoint.features, trainPoint.features);
                }

                trainSet.sort(Comparator.comparingDouble(dp -> dp.distance));
                
                int prediccion = prediccion(trainSet, k);

                if (prediccion == testPoint.outcome) {
                    correctPredictions++;
                }
            }

            double accuracy = (double) correctPredictions / testSet.size();
            System.out.printf("Precisión del modelo: %.2f%%\n", accuracy * 100);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<DataPoint> loadDataset() throws Exception {
        List<DataPoint> dataset = new ArrayList<>();
        URL url = new URL("https://raw.githubusercontent.com/jbrownlee/Datasets/master/pima-indians-diabetes.data.csv");
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                double[] features = new double[8];
                for (int i = 0; i < 8; i++) {
                    features[i] = Double.parseDouble(values[i]);
                }
                int outcome = Integer.parseInt(values[8]); 
                dataset.add(new DataPoint(features, outcome));
            }
        }
        return dataset;
    }

    private static void normalizeDataset(List<DataPoint> dataset) {
        if (dataset.isEmpty()) return;
        
        int featureCount = dataset.get(0).features.length;
        double[] min = new double[featureCount]; 
        double[] max = new double[featureCount]; 
        
        for (int i = 0; i < featureCount; i++) {
            min[i] = dataset.get(0).features[i];
            max[i] = dataset.get(0).features[i];
        }
        
        for (DataPoint dp : dataset) {
            for (int i = 0; i < featureCount; i++) {
                if (dp.features[i] < min[i]) min[i] = dp.features[i];
                if (dp.features[i] > max[i]) max[i] = dp.features[i];
            }
        }
        for (DataPoint dp : dataset) {
            for (int i = 0; i < featureCount; i++) {
                if (max[i] - min[i] != 0) {
                    dp.features[i] = (dp.features[i] - min[i]) / (max[i] - min[i]);
                }
            }
        }
    }

    private static double euclideanDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }

    private static int prediccion(List<DataPoint> vecinos, int k) {
        int cont0 = 0; 
        int cont1 = 0; 

        for (int i = 0; i < k && i < vecinos.size(); i++) {
            if (vecinos.get(i).outcome == 0) {
                cont0++;
            } else {
                cont1++;
            }
        }
        return cont0 > cont1 ? 0 : 1;
    }
}