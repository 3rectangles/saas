/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WhatsAppConsentRepository extends JpaRepository<WhatsAppConsentDAO, String> {
	Optional<WhatsAppConsentDAO> findByPhone(String phone);
}
