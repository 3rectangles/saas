/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dto;

import com.barraiser.commons.dto.ats.enums.ATSProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdatePartnerRepMappingsDTO {
	private List<PartnerRepMapping> partnerRepMappings;

	private String partnerId;

	private ATSProvider atsProvider;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder(toBuilder = true)
	public static class PartnerRepMapping {
		private String atsPartnerRepId;

		private String brPartnerRepId;
	}

}
