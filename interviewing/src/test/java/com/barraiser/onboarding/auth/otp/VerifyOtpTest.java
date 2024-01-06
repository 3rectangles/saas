package com.barraiser.onboarding.auth.otp;

import com.barraiser.common.utilities.PhoneParser;
import com.barraiser.onboarding.auth.AuthenticationManager;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VerifyOtpTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private OtpManager otpManager;
    @Mock
    private DynamicAppConfigProperties appConfigProperties;
    @Mock
    private GraphQLUtil graphQLUtil;
    @Mock
    private PhoneParser phoneParser;
    @Mock
    private DataFetchingEnvironment environment;

    @InjectMocks
    private VerifyOtp verifyOtp;

    @Test
    public void shouldVerifyAValidOtp() {

    }

}
