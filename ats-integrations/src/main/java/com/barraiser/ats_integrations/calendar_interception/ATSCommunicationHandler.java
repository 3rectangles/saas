/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception;

import com.barraiser.ats_integrations.common.ATSCommunicationSender;
import com.barraiser.ats_integrations.common.dto.ATSEvaluationDetailsDTO;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;
import com.barraiser.commons.dto.ats.enums.ATSProvider;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class ATSCommunicationHandler {

	List<ATSCommunicationSender> atsCommunicationSenderList;

	@SneakyThrows
	public void postNoteOnApplication(final String message, final ATSAggregator atsAggregator,
			final ATSProvider atsProvider, final ATSEvaluationDetailsDTO atsEvaluationDetailsDTO) {
		for (final ATSCommunicationSender sender : this.atsCommunicationSenderList) {
			if (sender.atsAggregator().equals(atsAggregator) &&
					sender.atsProvider().equals(atsProvider)) {

				sender.postNoteOnApplication(message, atsEvaluationDetailsDTO);
			}
		}
	}
}
