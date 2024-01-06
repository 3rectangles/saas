package com.barraiser.onboarding.communication.channels.push;


import com.amazonaws.services.pinpoint.AmazonPinpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeviceRegistrationTest {
    @Mock
    private AmazonPinpoint amazonPinpoint;
    @InjectMocks
    private DeviceRegistrant deviceRegistration;

    @Test
    public void shouldRegisterADevice() {
        // GIVEN
        final String deviceToken = "a device token";
        // WHEN
//        this.deviceRegistration.registerDevice(deviceToken);
        // THEN
    }

}
