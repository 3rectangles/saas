/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing;

import com.barraiser.common.dal.Money;
import com.barraiser.common.model.InterviewPriceResponseDTO;
import com.barraiser.common.model.ScheduledInterviewCostDetailDTO;
import com.barraiser.pricing.dal.InterviewCostDetailsDAO;
import com.barraiser.pricing.dal.InterviewCostDetailsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class InterviewCostDetailsService {
	public static final Long minutesInAnHour = 60L;

	private final CurrencyUtil currencyUtil;

	private final InterviewCostDetailsRepository interviewCostDetailsRepository;

	public void saveInterviewCostDetails(final String interviewId, final Integer rescheduleCount,
			final InterviewPriceResponseDTO interviewPriceResponseDTO,
			final String expertId,
			final Money expertCostPerHour, final Double expertMinPricePerHour, final Double actualMargin) {
		this.interviewCostDetailsRepository.save(InterviewCostDetailsDAO.builder()
				.id(UUID.randomUUID().toString())
				.interviewId(interviewId)
				.rescheduleCount(rescheduleCount)
				.interviewCost(interviewPriceResponseDTO.getMaximumInterviewPrice())
				.usedMargin(interviewPriceResponseDTO.getBarRaiserMarginPercentage())
				.expertId(expertId)
				.expertCostPerHour(expertCostPerHour)
				.expertMinPricePerHour(expertMinPricePerHour)
				.configuredMargin(actualMargin)
				.build());
	}

	public ScheduledInterviewCostDetailDTO getScheduledInterviewCostDetails(final String interviewId,
			final Integer rescheduleCount) {
		final InterviewCostDetailsDAO interviewCostDetailsDAO;
		if (rescheduleCount != null) {
			interviewCostDetailsDAO = this.interviewCostDetailsRepository
					.findTopByInterviewIdAndRescheduleCountOrderByRescheduleCountDescCreatedOnDesc(interviewId,
							rescheduleCount)
					.orElse(InterviewCostDetailsDAO.builder().build());
		} else {
			interviewCostDetailsDAO = this.interviewCostDetailsRepository
					.findTopByInterviewIdOrderByRescheduleCountDescCreatedOnDesc(interviewId)
					.orElse(InterviewCostDetailsDAO.builder().build());
		}

		return ScheduledInterviewCostDetailDTO.builder()
				.interviewCost(interviewCostDetailsDAO.getInterviewCost())
				.usedMargin(interviewCostDetailsDAO.getUsedMargin())
				.expertId(interviewCostDetailsDAO.getExpertId())
				.expertCostPerHour(interviewCostDetailsDAO.getExpertCostPerHour())
				.expertMinPricePerHour(interviewCostDetailsDAO.getExpertMinPricePerHour())
				.configuredMargin(interviewCostDetailsDAO.getConfiguredMargin())
				.build();
	}

	public Money getPriceToBePaidToExpert(final String interviewId, final Integer rescheduleCount,
			final String expertId,
			final Long durationOfInterviewInMinutes) {
		final Optional<InterviewCostDetailsDAO> interviewCostDetailsDAO = this.interviewCostDetailsRepository
				.findByInterviewIdAndRescheduleCountAndExpertId(interviewId, rescheduleCount, expertId);
		if (interviewCostDetailsDAO.isEmpty()) {
			return null;
		}

		Double maxPriceThatCanBePaidToExpertForAnHourInINR = this.getMaxPriceThatCanBePaidToExpertForAnHourInINR(
				interviewCostDetailsDAO.get(), durationOfInterviewInMinutes,
				interviewCostDetailsDAO.get().getUsedMargin());
		final Double maxPriceOfExpertForAnHourInINR = this.currencyUtil.convertToINR(
				interviewCostDetailsDAO.get().getExpertCostPerHour().getValue(),
				interviewCostDetailsDAO.get().getExpertCostPerHour().getCurrency());
		final Double minPriceOfExpertForAnHourInINR = this.currencyUtil.convertToINR(
				interviewCostDetailsDAO.get().getExpertMinPricePerHour(),
				interviewCostDetailsDAO.get().getExpertCostPerHour().getCurrency());

		Double configuredMargin = interviewCostDetailsDAO.get().getConfiguredMargin();

		// TO BE DELETED ONLY HERE FOR BACKWARD COMPATIBILITY
		if (configuredMargin == null) {
			configuredMargin = interviewCostDetailsDAO.get().getUsedMargin();
		}

		return this.getPriceToBePaidToExpert(maxPriceOfExpertForAnHourInINR,
				maxPriceThatCanBePaidToExpertForAnHourInINR,
				interviewCostDetailsDAO.get().getExpertCostPerHour().getCurrency(), minPriceOfExpertForAnHourInINR,
				interviewCostDetailsDAO.get().getUsedMargin(), configuredMargin);

	}

	private Double getMaxPriceThatCanBePaidToExpertForAnHourInINR(final InterviewCostDetailsDAO interviewCostDetailsDAO,
			final Long durationOfInterviewInMinutes, final Double margin) {
		return this.currencyUtil.convertToINR(
				interviewCostDetailsDAO.getInterviewCost().getValue() * minutesInAnHour
						/ durationOfInterviewInMinutes,
				interviewCostDetailsDAO.getInterviewCost().getCurrency())
				* (1 - margin / 100);
	}

	private Money getPriceToBePaidToExpert(final Double maxPriceOfExpertForAnHourInINR,
			final Double maxPriceThatCanBePaidToExpertForAnHourInINR, final String currencyOfExpert,
			final Double minPriceOfExpertForAnHourInINR, final Double usedMargin, final Double configuredMargin) {
		if (this.isFallbackCase(usedMargin, configuredMargin)) {
			return Money
					.builder().value(this.currencyUtil
							.convertINRToCurrency(minPriceOfExpertForAnHourInINR, currencyOfExpert))
					.currency(currencyOfExpert).build();
		}

		if (maxPriceOfExpertForAnHourInINR < maxPriceThatCanBePaidToExpertForAnHourInINR) {
			return Money.builder().value(
					this.currencyUtil.convertINRToCurrency(maxPriceOfExpertForAnHourInINR, currencyOfExpert))
					.currency(currencyOfExpert).build();
		}
		return Money
				.builder().value(this.currencyUtil
						.convertINRToCurrency(maxPriceThatCanBePaidToExpertForAnHourInINR, currencyOfExpert))
				.currency(currencyOfExpert).build();
	}

	private Boolean isFallbackCase(final Double usedMargin, final Double configuredMargin) {
		return usedMargin < configuredMargin;
	}
}
