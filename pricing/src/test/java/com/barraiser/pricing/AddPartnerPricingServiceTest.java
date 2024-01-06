/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing;

import com.barraiser.common.DTO.pricing.AddPricingConfigResult;
import com.barraiser.common.enums.PricingType;
import com.barraiser.common.graphql.types.FieldValidationResult;
import com.barraiser.common.graphql.types.ValidationResult;
import com.barraiser.pricing.pojo.PartnerPricingInputData;
import com.barraiser.pricing.validators.AddPartnerPricingInputValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddPartnerPricingServiceTest {
	@InjectMocks
	private AddPartnerPricingService addPartnerPricingService;
	@Mock
	private AddPartnerPricingInputValidator addPartnerPricingInputValidator;
	@Mock
	private ContractualPricingService contractualPricingService;
	@Mock
	private WorkExperienceBasedPricingService workExperienceBasedPricingService;

	@Test
	public void shouldReturnErrorsIfValidationReturnedErrors() {
		final ValidationResult validationResult = new ValidationResult();
		final List<PartnerPricingInputData> partnerPricingInputDataList = Arrays.asList(
				PartnerPricingInputData.builder().pricingType(PricingType.JOB_ROLE_BASED).build());
		validationResult.setFieldErrors(Arrays
				.asList(FieldValidationResult.builder().fieldTag("f1").message("field validation result 1").build()));
		when(this.addPartnerPricingInputValidator.validate(partnerPricingInputDataList)).thenReturn(validationResult);
		final AddPricingConfigResult addPricingConfigResult = this.addPartnerPricingService.add("p1",
				partnerPricingInputDataList, "barRaiser");
		assertEquals(addPricingConfigResult.getValidationResult(), validationResult);
		assertEquals(addPricingConfigResult.getValidationResult().getFieldErrors().size(),
				validationResult.getFieldErrors().size());
		assertEquals(addPricingConfigResult.getValidationResult().getFieldErrors().get(0).getFieldTag(),
				validationResult.getFieldErrors().get(0).getFieldTag());
		assertEquals(addPricingConfigResult.getValidationResult().getFieldErrors().get(0).getMessage(),
				validationResult.getFieldErrors().get(0).getMessage());
	}

	@Test
	public void shouldNotReturnErrorsIfValidationReturnedDidNotErrors() {
		final ValidationResult validationResult = new ValidationResult();
		final List<PartnerPricingInputData> partnerPricingInputDataList = Arrays
				.asList(PartnerPricingInputData.builder().pricingType(PricingType.JOB_ROLE_BASED).build());
		validationResult.setFieldErrors(Arrays.asList());
		when(this.addPartnerPricingInputValidator.validate(partnerPricingInputDataList)).thenReturn(validationResult);
		final AddPricingConfigResult addPricingConfigResult = this.addPartnerPricingService.add("p1",
				partnerPricingInputDataList, "barRaiser");
		assertEquals(addPricingConfigResult.getValidationResult(), null);
		verify(this.contractualPricingService).addContractualPricingConfig("p1", partnerPricingInputDataList,
				"barRaiser");
		verify(this.workExperienceBasedPricingService).addWorkExperienceBasedPricing("p1", partnerPricingInputDataList,
				"barRaiser");
	}
}
