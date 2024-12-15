package com.example.util;

import java.util.List;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.ArrayList;

public class StatisticalAnalysis {
    private final List<Double> data;
    private final DoubleSummaryStatistics stats;
    private final double median;
    private final double standardDeviation;

    public StatisticalAnalysis(List<Double> data) {
        this.data = data;
        this.stats = data.stream().mapToDouble(Double::doubleValue).summaryStatistics();
        this.median = calculateMedian();
        this.standardDeviation = calculateStandardDeviation();
    }

    public double getMean() {
        return stats.getAverage();
    }

    public double getMedian() {
        return median;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public double getMin() {
        return stats.getMin();
    }

    public double getMax() {
        return stats.getMax();
    }

    private double calculateMedian() {
        List<Double> sortedData = new ArrayList<>(data);
        Collections.sort(sortedData);
        int size = sortedData.size();
        if (size == 0) return 0.0;
        
        if (size % 2 == 0) {
            return (sortedData.get(size/2 - 1) + sortedData.get(size/2)) / 2.0;
        } else {
            return sortedData.get(size/2);
        }
    }

    private double calculateStandardDeviation() {
        double mean = getMean();
        double sumSquaredDiff = data.stream()
            .mapToDouble(value -> Math.pow(value - mean, 2))
            .sum();
        return Math.sqrt(sumSquaredDiff / (data.size() - 1));
    }

    public double getPercentile(double percentile) {
        List<Double> sortedData = new ArrayList<>(data);
        Collections.sort(sortedData);
        int index = (int) Math.ceil(percentile * sortedData.size() / 100.0) - 1;
        return sortedData.get(Math.max(0, Math.min(sortedData.size() - 1, index)));
    }
}