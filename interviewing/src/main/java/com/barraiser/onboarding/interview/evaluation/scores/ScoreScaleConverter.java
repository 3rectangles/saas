/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.config.ConfigComposer;
import com.barraiser.onboarding.dal.EvaluationScoreDAO;
import com.barraiser.onboarding.dal.PartnerCompanyDAO;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Component
@AllArgsConstructor
public class ScoreScaleConverter {
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final ConfigComposer configComposer;

	public double convertScoreFrom800(double score, String partnerId) {
		return (getScaleBGS(partnerId) * score) / 800;
	}

	public double convertScoreTo800(double score, String partnerId) {
		return (score * 800) / getScaleBGS(partnerId); // (getScaleBGS(partnerId) * score) / 800;
	}

	public Integer getScaleBGS(String partnerId) {
		PartnerCompanyDAO partnerCompanyDAO = partnerCompanyRepository.findById(partnerId).get();
		Integer scaleBGS = partnerCompanyDAO.getScaleBGS();
		if (scaleBGS == null) {
			scaleBGS = partnerCompanyDAO.getScaleScoring();
		}
		if (scaleBGS == null) {
			List<String> tags = new ArrayList<>();
			tags.add("partnership_model." + partnerCompanyDAO.getPartnershipModelId());
			try {
				JsonNode config = configComposer.compose("scoring_rating", tags);
				scaleBGS = config != null ? config.get("bgs_scale").asInt() : null;
				if (scaleBGS == null) {
					JsonNode configFeedback = config != null ? config.get("feedback_config") : null;
					scaleBGS = (configFeedback != null) ? configFeedback.get("question.inputScale").asInt() : null;
				}
			} catch (Exception e) {
				scaleBGS = 5;
			}
		}
		if (scaleBGS == null)
			scaleBGS = 5;
		return scaleBGS;
	}

	public Integer getScaleScoring(String partnerId) {
		/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
		PartnerCompanyDAO partnerCompanyDAO = partnerCompanyRepository.findById(partnerId).get();
		Integer scaleScoring = partnerCompanyDAO.getScaleScoring();
		if (scaleScoring == null) {
			List<String> tags = new ArrayList<>();
			tags.add("partnership_model." + partnerCompanyDAO.getPartnershipModelId());
			try {
				JsonNode config = configComposer.compose("scoring_rating", tags);
				JsonNode configFeedback = config != null ? config.get("feedback_config") : null;
				scaleScoring = (configFeedback != null) ? configFeedback.get("question.inputScale").asInt() : null;
			} catch (Exception e) {
				scaleScoring = 5;
			}
		}
		return scaleScoring;
	}

}
