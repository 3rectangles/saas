/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.common.dal.Money;
import com.barraiser.common.graphql.types.InterviewSlots;
import com.barraiser.onboarding.availability.DTO.BookedSlotDTO;
import com.barraiser.onboarding.dal.InterviewDAO;

import lombok.*;

import java.util.List;
import java.util.Map;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
public class MatchInterviewersData {
	private Map<String, List<BookedSlotDTO>> bookedInterviewingSlotsPerInterviewer;
	private Map<String, List<BookedSlotDTO>> bookedSlotsPerInterviewer;
	private String domainId;
	private String interviewRound;
	private Long availabilityStartDate;
	private Long availabilityEndDate;
	private String hiringCompanyId;
	private Integer workExperienceOfIntervieweeInMonths;
	private List<InterviewersPerDayData> interviewersPerDayDataList;
	private List<InterviewerData> interviewers;
	private List<String> interviewersId;
	private Map<Long, String> slotInterviewerMapping;
	private List<InterviewSlots> interviewSlots;
	private String evaluationId;
	private String interviewStructureId;
	private String category;
	private Integer expertJoiningTime;
	private List<InterviewerData> duplicateExperts;
	private String interviewId;
	private Long durationOfInterview;
	private String candidateId;
	private String partnerCompanyId;
	private InterviewDAO interview;
	private Map<Long, List<InterviewerData>> slotToAllInterviewers;
	private Double overbookingThreshold;
	private List<String> eligibleCountriesForExperts;
	private String timezone;
	private String jobRoleId;
	private Money interviewCost;
	private Boolean isFallbackEnabled;
	private Double barRaiserUsedMarginPercentage;
	private Double barRaiserConfiguredMarginPercentage;
	private Integer rescheduleCount;
	private Map<String, String[]> fixedQuestionMap;
	private List<String> fixedQuestionExperts;
}
