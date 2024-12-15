package com.example.service;

import com.example.dto.SubscriptionPlanRecommendationDto;
import com.example.entity.SubscriptionPlan;
import com.example.service.StatisticsService;
import com.example.service.SubscriptionService;
import com.example.service.PaymentService;
import com.example.util.LogisticRegression;
import com.example.util.StatisticalAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;

import static com.example.util.TimeSeriesAnalysis.*;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final StatisticsService statisticsService;
    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;

    public SubscriptionPlanRecommendationDto getRecommendation(Long productId) {
        try {
            List<SubscriptionPlan> allPlans = subscriptionService.findByProductId(productId);
            if (allPlans.isEmpty()) {
                return getDefaultRecommendationWithMessage("Недостаточно данных для рекомендации: нет планов подписки");
            }

            // Собираем данные для анализа
            List<Double> prices = allPlans.stream()
                .map(plan -> plan.getPrice().doubleValue())
                .collect(Collectors.toList());
            List<Integer> periods = allPlans.stream()
                .map(SubscriptionPlan::getPeriodDays)
                .collect(Collectors.toList());
            List<Double> revenues = allPlans.stream()
                .map(plan -> paymentService.getMonthlyRevenueByPlanId(plan.getId()).doubleValue())
                .collect(Collectors.toList());
            List<Double> conversionRates = allPlans.stream()
                .map(plan -> subscriptionService.getConversionRateByPlanId(plan.getId()))
                .collect(Collectors.toList());

            // Проверяем, есть ли реальные данные о доходах
            if (revenues.stream().allMatch(rev -> rev == 0.0)) {
                return getDefaultRecommendationWithMessage("Недостаточно данных для рекомендации: нет информации о доходах");
            }

            // Статистический анализ
            StatisticalAnalysis priceAnalysis = new StatisticalAnalysis(prices);
            StatisticalAnalysis periodAnalysis = new StatisticalAnalysis(
                periods.stream().map(Double::valueOf).collect(Collectors.toList())
            );
            StatisticalAnalysis revenueAnalysis = new StatisticalAnalysis(revenues);
            StatisticalAnalysis conversionAnalysis = new StatisticalAnalysis(conversionRates);

            // Рассчитываем рекомендуемые значения
            double confidenceLevel = 0.95; // 95% доверительный интервал
            
            // Цена: используем взвешенное среднее успешных планов
            List<SubscriptionPlan> successfulPlans = filterSuccessfulPlans(allPlans);
            double recommendedPrice = calculateRecommendedPrice(successfulPlans, confidenceLevel);
            
            // Период: используем медианное значение с корректировкой на основе корреляции с доходом
            int recommendedPeriod = calculateRecommendedPeriod(periods, revenues);

            // Прогноз дохода с учетом сезонности и тренда
            double expectedMonthlyRevenue = calculateExpectedRevenue(productId, recommendedPrice, recommendedPeriod);
            
            // Расчет конверсии на основе успешных планов
            double conversionRate = calculateExpectedConversionRate(successfulPlans);

            // Проверяем результаты на NaN и адекватность
            if (Double.isNaN(recommendedPrice) || Double.isNaN(expectedMonthlyRevenue) || 
                Double.isNaN(conversionRate) || recommendedPrice <= 0) {
                return getDefaultRecommendationWithMessage(
                    "Недостаточно статистических данных для формирования надежной рекомендации"
                );
            }

            String explanation = String.format(
                "На основе анализа %d существующих планов:\n" +
                "- Средняя цена успешных планов: %.2f₽\n" +
                "- Средний период: %d дней\n" +
                "- Текущий коэффициент конверсии: %.1f%%\n" +
                "- Прогнозируемый месячный доход: %.2f₽",
                allPlans.size(),
                recommendedPrice,
                recommendedPeriod,
                conversionRate * 100,
                expectedMonthlyRevenue
            );

            // Округляем recommendedPrice до целого числа перед преобразованием в BigDecimal
            long roundedPrice = Math.round(recommendedPrice);

            return SubscriptionPlanRecommendationDto.builder()
                .recommendedPrice(BigDecimal.valueOf(roundedPrice))
                .recommendedPeriodDays(recommendedPeriod)
                .explanation(explanation)
                .expectedMonthlyRevenue(expectedMonthlyRevenue)
                .conversionRate(conversionRate)
                .build();

        } catch (Exception e) {
            return getDefaultRecommendationWithMessage(
                "Не удалось рассчитать рекомендацию: недостаточно данных"
            );
        }
    }

    private double calculateExpectedConversionRate(List<SubscriptionPlan> plans) {
        if (plans.isEmpty()) {
            return Double.NaN;
        }
        
        double rate = plans.stream()
            .mapToDouble(plan -> {
                long total = subscriptionService.countByPlanId(plan.getId());
                if (total == 0) return 0.0;
                long active = subscriptionService.countActiveByPlanId(plan.getId());
                return (double) active / total;
            })
            .average()
            .orElse(Double.NaN);

        return Double.isNaN(rate) ? Double.NaN : rate;
    }

    private double calculateExpectedRevenue(Long productId, double price, int period) {
        if (Double.isNaN(price) || period <= 0) {
            return Double.NaN;
        }

        double currentMonthlyRevenue = paymentService.getMonthlyRevenueByProductId(productId).doubleValue();
        double currentAvgPrice = paymentService.getAverageRevenueByProductId(productId);
        
        if (currentAvgPrice <= 0) {
            return Double.NaN;
        }
        
        double result = currentMonthlyRevenue * (price / currentAvgPrice) * (30.0 / period);
        return Double.isFinite(result) ? result : Double.NaN;
    }

    private List<SubscriptionPlan> filterSuccessfulPlans(List<SubscriptionPlan> plans) {
        if (plans.isEmpty()) return plans;
        
        // Считаем план успешным, если его доход выше среднего
        double avgRevenue = plans.stream()
            .mapToDouble(plan -> paymentService.getMonthlyRevenueByPlanId(plan.getId()).doubleValue())
            .average()
            .orElse(0.0);
            
        return plans.stream()
            .filter(plan -> paymentService.getMonthlyRevenueByPlanId(plan.getId()).doubleValue() >= avgRevenue)
            .collect(Collectors.toList());
    }

    private SubscriptionPlanRecommendationDto getDefaultRecommendationWithMessage(String message) {
        return SubscriptionPlanRecommendationDto.builder()
            .recommendedPrice(null)
            .recommendedPeriodDays(null)
            .explanation(message)
            .expectedMonthlyRevenue(null)
            .conversionRate(null)
            .build();
    }

    private double calculateRecommendedPrice(List<SubscriptionPlan> plans, double confidenceLevel) {
        if (plans.isEmpty()) return 999.0; // Значение по умолчанию

        // Собираем цены с весами на основе дохода
        List<Double> weightedPrices = new ArrayList<>();
        for (SubscriptionPlan plan : plans) {
            double revenue = paymentService.getMonthlyRevenueByPlanId(plan.getId()).doubleValue();
            double weight = revenue + 1; // +1 чтобы избежать нулевых весов
            
            // Добавляем цену столько раз, сколько составляет вес
            for (int i = 0; i < weight; i++) {
                weightedPrices.add(plan.getPrice().doubleValue());
            }
        }

        // Статистический анализ взвешенных цен
        StatisticalAnalysis priceAnalysis = new StatisticalAnalysis(weightedPrices);
        
        // Рассчитываем доверительный интервал
        double standardError = priceAnalysis.getStandardDeviation() / Math.sqrt(weightedPrices.size());
        double tValue = getTValue(weightedPrices.size() - 1, confidenceLevel);
        double marginOfError = tValue * standardError;

        // Возвращаем верхнюю границу доверительного интервала
        return priceAnalysis.getMean() + marginOfError;
    }

    private int calculateRecommendedPeriod(List<Integer> periods, List<Double> revenues) {
        if (periods.isEmpty()) return 30; // Значение по умолчанию

        // Рассчитываем корреляцию между периодом и доходом
        double correlation = calculateCorrelation(
            periods.stream().mapToDouble(Integer::doubleValue).toArray(),
            revenues.stream().mapToDouble(Double::doubleValue).toArray()
        );

        // Находим медиану периодов
        List<Integer> sortedPeriods = new ArrayList<>(periods);
        Collections.sort(sortedPeriods);
        int medianPeriod = sortedPeriods.get(sortedPeriods.size() / 2);

        // Корректируем период на основе корреляции
        if (correlation > 0.5) {
            // Если есть сильная положительная корреляция, увеличиваем период
            return (int) Math.round(medianPeriod * 1.2);
        } else if (correlation < -0.5) {
            // Если есть сильная отрицательная корреляция, уменьшаем период
            return (int) Math.round(medianPeriod * 0.8);
        }

        return medianPeriod;
    }

    private double getTValue(int degreesOfFreedom, double confidenceLevel) {
        // Упрощенная версия t-распределения Стьюдента
        if (degreesOfFreedom <= 0) return 1.96; // Стандартное нормальное распределение
        
        // Приближенные значения для разных уровней доверия
        if (confidenceLevel >= 0.99) return 2.576;
        if (confidenceLevel >= 0.98) return 2.326;
        if (confidenceLevel >= 0.95) return 1.96;
        if (confidenceLevel >= 0.90) return 1.645;
        return 1.28; // 80% уровень доверия
    }

    private double calculateCorrelation(double[] x, double[] y) {
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