/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.payment.expert;

import com.barraiser.common.dal.Money;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.expert.CostUtil;
import com.barraiser.onboarding.interview.InterviewCostRepository;
import com.barraiser.onboarding.interview.InterviewHistoryManager;
import com.barraiser.onboarding.interview.InterviewUtil;

import com.barraiser.onboarding.partner.partnerPricing.PricingServiceClient;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class ExpertPaymentManager {

	private final ExpertRepository expertRepository;
	private final List<InterviewCostCalculator> expertPaymentHandler;
	private final InterviewCostRepository interviewCostRepository;
	private final CostUtil costUtil;
	private final InterviewUtil interviewUtil;
	private final DynamicAppConfigProperties dynamicAppConfigProperties;
	private final PricingServiceClient pricingServiceClient;
	private final InterviewHistoryManager interviewHistoryManager;

	private static final String PAYMENT_TYPE_INTERVIEW_DONE = "Interview";
	private static final String PAYMENT_TYPE_CANCELLED = "Cancelled";

	private static final String CANCELLATION_PAYMENT_CALCULATION_ALGO_VERSION = "cancellation_payment_calculation_algo_version";

	public void computeAndSave(InterviewPaymentCalculationData data) {
		// TODO : TO BE DELETED
		final InterviewHistoryDAO interviewHistoryDAO = this.interviewHistoryManager
				.getLatestInterviewChangeHistoryByRescheduleCountAndInterviewerId(
						data.getInterviewId(), data.getRescheduleCount(), data.getInterviewerId());

		final InterviewDAO interview = this.interviewHistoryManager
				.mapInterviewHistoriesToInterviews(List.of(interviewHistoryDAO))
				.get(0);

		final String expertId = data.getInterviewerId();
		ExpertDAO expert = this.expertRepository
				.findById(expertId)
				.orElseThrow(
						() -> new IllegalArgumentException(
								"Expert with id : "
										+ interview.getInterviewerId()
										+ " does not exist in db!"));

		final InterviewStructureDAO interviewStructure = this.interviewUtil
				.getInterviewStructureForInterview(interview);

		final Money expertPayment = this.pricingServiceClient.getPriceToBePaidToExpert(data.getInterviewId(),
				data.getRescheduleCount(), expertId, interviewStructure.getDuration().longValue())
				.getBody();

		expert = expert.toBuilder()
				.baseCost(expertPayment != null ? expertPayment.getValue() : expert.getBaseCost())
				.multiplier(expertPayment != null ? 1D : expert.getMultiplier())
				.build();

		data = data.toBuilder().interview(interview).expert(expert).interviewStructure(interviewStructure).build();

		final String paymentType = InterviewStatus.DONE.getValue().equalsIgnoreCase(data.getInterviewStatus())
				? PAYMENT_TYPE_INTERVIEW_DONE
				: InterviewStatus.CANCELLATION_DONE
						.getValue()
						.equalsIgnoreCase(data.getInterviewStatus())
								? PAYMENT_TYPE_CANCELLED
								: "";

		log.info("calculating payment for : {} of type : {}", interview.getId(), paymentType);

		final boolean isInterviewPaymentAlreadyCalculated = this.interviewCostRepository
				.findByInterviewIdAndRescheduleCountAndInterviewerId(
						data.getInterviewId(), data.getRescheduleCount(), expert.getId()) != null;

		if (!isInterviewPaymentAlreadyCalculated) {
			if (InterviewStatus.DONE.getValue().equalsIgnoreCase(data.getInterviewStatus())) {
				final Double netPayableAmount = this.calculateDoneInterviewPayment(data);
				this.syncInInterviewCost(interview, expert, netPayableAmount, paymentType);

			} else if (InterviewStatus.CANCELLATION_DONE
					.getValue()
					.equalsIgnoreCase(data.getInterviewStatus())) {
				/**
				 * This condition basically ensures that payment calculation happens only for
				 * scheduled interview. A scheduled interview will always have an interviewer.
				 */
				if (expert != null) {
					final Double netPayableAmount = this.calculateCancelledInterviewPayment(data);
					this.syncInInterviewCost(interview, expert, netPayableAmount, paymentType);
				}
			}
		}
	}

	private Double calculateDoneInterviewPayment(final InterviewPaymentCalculationData data) {
		final Double netPayableAmount = this.expertPaymentHandler.stream()
				.filter(
						x -> (x.version().equals(data.getExpert().getCostLogic())
								&& InterviewStatus.DONE
										.getValue()
										.equals(x.status())))
				.findFirst()
				.orElseThrow(
						() -> new IllegalArgumentException("Cost logic does not exist"))
				.calculate(data);

		return netPayableAmount;
	}

	private Double calculateCancelledInterviewPayment(final InterviewPaymentCalculationData data) {
		final String currentCancellationLogicAlgoVersion = this.dynamicAppConfigProperties
				.getString(CANCELLATION_PAYMENT_CALCULATION_ALGO_VERSION);
		final Double netPayableAmount = this.expertPaymentHandler.stream()
				.filter(
						x -> (x.version().equals(currentCancellationLogicAlgoVersion)
								&& InterviewStatus.CANCELLATION_DONE
										.getValue()
										.equals(x.status())))
				.findFirst()
				.orElseThrow(
						() -> new IllegalArgumentException(
								"Cancellation logic does not exist"))
				.calculate(data);

		return netPayableAmount;
	}

	private void syncInInterviewCost(
			final InterviewDAO interview,
			final ExpertDAO expert,
			final Double netPayableAmount,
			final String paymentType) {

		final double actualPayment = Math.round(netPayableAmount);
		final InterviewCostDAO interviewCost = InterviewCostDAO.builder()
				.id(UUID.randomUUID().toString())
				.interviewId(interview.getId())
				.interviewerId(expert.getId())
				.interviewerBaseCost(expert.getBaseCost())
				.multiplier(expert.getMultiplier())
				.totalAmount(actualPayment)
				.paymentType(paymentType)
				.costLogic(expert.getCostLogic())
				.cancellationLogic(this.dynamicAppConfigProperties
						.getString(CANCELLATION_PAYMENT_CALCULATION_ALGO_VERSION))
				.interviewSnapshot(interview)
				.currencyId(this.costUtil.getCurrencyForCurrencyCode(expert.getCurrency()).getId())
				.rescheduleCount(interview.getRescheduleCount())
				.build();

		log.info("cost for interview : {} is {}", interviewCost.getInterviewId(), actualPayment);

		this.interviewCostRepository.save(interviewCost);
	}

}
