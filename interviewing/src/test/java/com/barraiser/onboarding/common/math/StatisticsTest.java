package com.barraiser.onboarding.common.math;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.DecimalFormat;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsTest {
    @Test
    public void shouldReturnStandardDeviation() {
        final List<Double> values = List.of(1D, 2D, 3D, 4D, 5D);
        final Double actualStandardDeviation = Statistics.getStandardDeviation(values);
        final Double expectedStandardDeviation = 1.4142135623731;
        DecimalFormat df2 = new DecimalFormat("#.#########");
        assertEquals(df2.format(expectedStandardDeviation), df2.format(actualStandardDeviation));
    }
}
