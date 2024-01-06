package com.barraiser.onboarding.common.math;

import java.util.List;

public class Statistics {

    public static Double getStandardDeviation(final List<Double> values) {
        final Double average = values.stream().mapToDouble(Double::doubleValue).sum() / (double)values.size();
        Double sumOfSquares = 0.0;
        for (Double value : values) {
            sumOfSquares += Math.pow(value - average, 2);
        }
        return Math.sqrt(sumOfSquares /  (double)values.size());
    }
}
