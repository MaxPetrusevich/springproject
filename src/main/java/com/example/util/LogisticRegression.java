package com.example.util;

public class LogisticRegression {
    private double[] weights;
    private double bias;
    private static final double LEARNING_RATE = 0.01;
    private static final int MAX_ITERATIONS = 1000;

    public void fit(double[][] features, double[] labels) {
        int n_features = features[0].length;
        weights = new double[n_features];
        bias = 0.0;

        // Градиентный спуск
        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            double[] gradients = new double[n_features];
            double biasGradient = 0.0;

            for (int i = 0; i < features.length; i++) {
                double prediction = predict(features[i]);
                double error = prediction - labels[i];

                for (int j = 0; j < n_features; j++) {
                    gradients[j] += error * features[i][j];
                }
                biasGradient += error;
            }

            // Обновление весов и смещения
            for (int j = 0; j < n_features; j++) {
                weights[j] -= LEARNING_RATE * gradients[j] / features.length;
            }
            bias -= LEARNING_RATE * biasGradient / features.length;
        }
    }

    public double predict(double[] features) {
        double z = bias;
        for (int i = 0; i < features.length; i++) {
            z += weights[i] * features[i];
        }
        return sigmoid(z);
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
} 