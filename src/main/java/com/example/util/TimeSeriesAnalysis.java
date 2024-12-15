package com.example.util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class TimeSeriesAnalysis {
    public static double calculateSeasonalFactor() {
        // Упрощенная реализация сезонности
        int month = LocalDateTime.now().getMonthValue();
        
        // Коэффициенты сезонности по месяцам (можно настроить на основе исторических данных)
        double[] seasonalFactors = {
            1.0,  // Январь
            0.9,  // Февраль
            1.1,  // Март
            1.0,  // Апрель
            1.2,  // Май
            0.8,  // Июнь
            0.7,  // Июль
            0.9,  // Август
            1.3,  // Сентябрь
            1.1,  // Октябрь
            1.2,  // Ноябрь
            1.4   // Декабрь
        };
        
        return seasonalFactors[month - 1];
    }

    public static double calculateTrendFactor(List<Double> historicalData) {
        if (historicalData == null || historicalData.isEmpty()) {
            return 1.0;
        }

        // Простой линейный тренд
        double sum = 0;
        double count = historicalData.size();
        
        for (int i = 0; i < count - 1; i++) {
            double change = (historicalData.get(i + 1) - historicalData.get(i)) / historicalData.get(i);
            sum += change;
        }
        
        double avgChange = sum / (count - 1);
        return 1.0 + avgChange;
    }

    public static double calculateCorrelation(double[] x, double[] y) {
        if (x.length != y.length || x.length == 0) {
            return 0.0;
        }

        double meanX = 0.0, meanY = 0.0;
        for (int i = 0; i < x.length; i++) {
            meanX += x[i];
            meanY += y[i];
        }
        meanX /= x.length;
        meanY /= y.length;

        double covariance = 0.0;
        double varX = 0.0;
        double varY = 0.0;

        for (int i = 0; i < x.length; i++) {
            double diffX = x[i] - meanX;
            double diffY = y[i] - meanY;
            covariance += diffX * diffY;
            varX += diffX * diffX;
            varY += diffY * diffY;
        }

        if (varX == 0.0 || varY == 0.0) {
            return 0.0;
        }

        return covariance / Math.sqrt(varX * varY);
    }
} 