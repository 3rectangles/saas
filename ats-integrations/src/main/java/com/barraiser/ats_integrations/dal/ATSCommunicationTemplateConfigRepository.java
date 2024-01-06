/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import com.barraiser.common.ats_integrations.ATSProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ATSCommunicationTemplateConfigRepository
		extends JpaRepository<ATSCommunicationTemplateConfigDAO, String> {

	Optional<ATSCommunicationTemplateConfigDAO> findByPartnerIdAndAtsProviderAndEventType(final String partnerId,
			final ATSProvider atsProvider, final String eventType);

	List<ATSCommunicationTemplateConfigDAO> findByEventType(final String eventType);

}
