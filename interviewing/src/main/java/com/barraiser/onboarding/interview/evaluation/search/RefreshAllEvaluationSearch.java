/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search;

import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.audit.AuditListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/refresh/evaluationsearch")
public class RefreshAllEvaluationSearch {

	@Autowired
	private EvaluationRepository evaluationRepository;

	@Autowired
	private AuditListener auditListener;

	@GetMapping
	public ResponseEntity<String> triggerEntityChangeEvent(@RequestParam(required = false) List<String> evaluationIds,
			@RequestParam(required = false) List<String> partnerIds) {
		if (evaluationIds != null && !evaluationIds.isEmpty()) {
			List<EvaluationDAO> evaluationsById = evaluationRepository.findAllById(evaluationIds);
			evaluationsById.forEach(this::auditEntity);
		} else if (partnerIds != null && !partnerIds.isEmpty()) {
			partnerIds.forEach(partnerId -> {
				List<EvaluationDAO> evaluationsByPartner = evaluationRepository.findAllByPartnerId(partnerId);
				evaluationsByPartner.forEach(this::auditEntity);
			});
		}

		return new ResponseEntity<>("EntityChangeEvent successfully triggered for specified evaluations",
				HttpStatus.OK);
	}

	private void auditEntity(EvaluationDAO evaluation) {
		try {
			auditListener.onPostUpdate(evaluation);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
