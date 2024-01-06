package com.barraiser.onboarding.payment.expert;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExpertPaymentUtilTest {

    @Test
    public void testGetPayableDurationForJoiningLessThan30Minutes() {
        assertEquals(0, ExpertPaymentUtil.getPayableDurationInMinutes(0), 0.0);
        assertEquals(30, ExpertPaymentUtil.getPayableDurationInMinutes(25), 0.0);
    }

    @Test
    public void testGetPayableDurationForJoiningBetween30And60Minutes() {
        assertEquals(60, ExpertPaymentUtil.getPayableDurationInMinutes(45), 0.0);
    }

    @Test
    public void testGetPayableDurationJoiningMoreThan60Minutes() {
        assertEquals(90, ExpertPaymentUtil.getPayableDurationInMinutes(90), 0.0);
        assertEquals(119, ExpertPaymentUtil.getPayableDurationInMinutes(119), 0.0);
    }

    @Test
    public void testCalculateAmountPayable() {
        assertEquals(130, ExpertPaymentUtil.calculateAmountPayable(100.0, 1.3, 45), 0);
        //To test 1.5 hour round (first introduced for recro)
        assertEquals(1725.075, ExpertPaymentUtil.calculateAmountPayable(766.7, 1.5, 90), 0.0001);
    }
}
