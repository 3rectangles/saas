/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.endpoint;

import com.barraiser.common.DTO.pricing.*;
import com.barraiser.common.DTO.pricing.JobRoleBasedPricingRequestDTO;
import com.barraiser.common.DTO.pricing.JobRoleBasedPricingResponseDTO;
import com.barraiser.common.DTO.pricing.PartnerPricingRequestDTO;
import com.barraiser.common.DTO.pricing.PartnerPricingResponseDTO;
import com.barraiser.common.dal.Money;
import com.barraiser.common.enums.RoundType;
import com.barraiser.common.graphql.types.PartnerPricingStageUpdationResult;
import com.barraiser.common.model.ScheduledInterviewCostDetailDTO;
import com.barraiser.pricing.PricingService;
import com.barraiser.common.model.InterviewPriceResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@AllArgsConstructor
public class PricingController {
	final static String SERVICE_CONTEXT_PATH = "/pricing-service";

	private final PricingService pricingService;

	@GetMapping(value = SERVICE_CONTEXT_PATH + "/partner/{partnerId}/interview/price")
	ResponseEntity<InterviewPriceResponseDTO> getInterviewPrice(@PathVariable("partnerId") final String partnerId,
			@RequestParam("job-role") final String jobRoleId,
			@RequestParam("interview-structure") final String interviewStructureId,
			@RequestParam("work-ex") final Integer workEx, @RequestParam("round-type") final RoundType roundType,
			@RequestParam("interview-duration") final Long durationOfInterviewInMinutes)
			throws JsonProcessingException {
		return ResponseEntity
				.ok()
				.body(this.pricingService.getInterviewPrice(partnerId, jobRoleId, interviewStructureId, workEx,
						roundType, durationOfInterviewInMinutes));
	}

	@PostMapping(value = SERVICE_CONTEXT_PATH + "/partner/{partnerId}/pricing")
	AddPricingConfigResult addPartnerPricing(@PathVariable("partnerId") final String partnerId,
			@RequestBody final AddPartnerPricingConfigRequestDTO addPartnerPricingConfigRequestDTO) {
		return this.pricingService.addPartnerPricing(partnerId, addPartnerPricingConfigRequestDTO);
	}

	@PutMapping(value = SERVICE_CONTEXT_PATH + "/partner/job-role-pricing")
	JobRoleBasedPricingUpdationResult updateJobRoleBasedPricing(
			@RequestBody final JobRoleBasedPricingUpdationRequestDTO jobRoleBasedPricingUpdationRequestDTO) {
		return this.pricingService.updateJobRoleBasedPricing(jobRoleBasedPricingUpdationRequestDTO);
	}

	@PutMapping(value = SERVICE_CONTEXT_PATH + "/partner/{partnerId}/pricing")
	PartnerPricingStageUpdationResult updatePartnerStageDetails(@PathVariable("partnerId") final String partnerId,
			@RequestBody final PartnerPricingStageDetailsRequestDTO partnerPricingStageDetailsRequestDTO) {
		return this.pricingService.updatePricingStageDetails(partnerId, partnerPricingStageDetailsRequestDTO);
	}

	@PostMapping(value = "/job-role-pricing")
	ResponseEntity<List<JobRoleBasedPricingResponseDTO>> getJobRoleBasedPricing(
			@RequestBody List<JobRoleBasedPricingRequestDTO> jobRoleBasedPricingRequestDTOList) {

		return ResponseEntity
				.ok()
				.body(this.pricingService.getJobRoleBasedPricing(jobRoleBasedPricingRequestDTOList));
	}

	@PostMapping(value = SERVICE_CONTEXT_PATH + "/partner/active-pricing")
	ResponseEntity<List<PartnerPricingResponseDTO>> getActivePricingForPartners(
			@RequestBody PartnerPricingRequestDTO partnerPricingRequestDTO) {
		return ResponseEntity
				.ok()
				.body(this.pricingService.getActivePricingForPartners(partnerPricingRequestDTO));
	}

	@PostMapping(value = SERVICE_CONTEXT_PATH + "/partner/{partnerId}/interview/price")
	void storeInterviewCostDetails(@PathVariable("partnerId") final String partnerId,
			@RequestBody UpdateInterviewCostDetailsRequestDTO updateInterviewCostDetailsRequestDTO)
			throws JsonProcessingException {
		this.pricingService.calculateAndStoreInterviewCostDetails(partnerId, updateInterviewCostDetailsRequestDTO);
	}

	@GetMapping(value = SERVICE_CONTEXT_PATH + "/interview/{interviewId}/interview/price")
	ResponseEntity<Money> getPriceToBePaidToExpert(@PathVariable("interviewId") final String interviewId,
			@RequestParam("reschedule-count") final Integer rescheduleCount,
			@RequestParam("interviewer-id") final String interviewerId,
			@RequestParam("interview-duration") final Long durationOfInterviewInMinutes)
			throws JsonProcessingException {
		return ResponseEntity
				.ok()
				.body(this.pricingService.getPriceToBePaidToExpert(interviewId, rescheduleCount, interviewerId,
						durationOfInterviewInMinutes));
	}

	@GetMapping(value = SERVICE_CONTEXT_PATH + "/interview/{interviewId}/scheduled-interview/price")
	ResponseEntity<ScheduledInterviewCostDetailDTO> getScheduledInterviewCostDetails(
			@PathVariable("interviewId") final String interviewId,
			@RequestParam("reschedule-count") final Integer rescheduleCount) {
		return ResponseEntity
				.ok()
				.body(this.pricingService.getScheduledInterviewCostDetails(interviewId, rescheduleCount));

	}
}
