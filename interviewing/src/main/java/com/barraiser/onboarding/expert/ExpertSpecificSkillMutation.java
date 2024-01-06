package com.barraiser.onboarding.expert;

import com.barraiser.onboarding.dal.ExpertSkillsDAO;
import com.barraiser.onboarding.dal.ExpertSkillsHistoryDAO;
import com.barraiser.onboarding.dal.ExpertSkillsHistoryRepository;
import com.barraiser.onboarding.dal.ExpertSkillsRepository;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.graphql.input.UpdateExpertSpecificSkillInput;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ExpertSpecificSkillMutation implements NamedDataFetcher<Object> {
    private final GraphQLUtil graphQLUtil;
    private final ExpertSkillsRepository expertSkillsRepository;
    private final ExpertSkillsHistoryRepository expertSkillsHistoryRepository;

    @Override
    public String name() {
        return "updateExpertSpecificSkill";
    }

    @Override
    public String type() {
        return MUTATION_TYPE;
    }

    @Override
    public Object get(final DataFetchingEnvironment environment) throws Exception {
        final UpdateExpertSpecificSkillInput input = this.graphQLUtil.getArgument(environment, Constants.CONTEXT_KEY_INPUT, UpdateExpertSpecificSkillInput.class);
        this.updateExpertSkills(input.getExpertId(), input.getSkillId(), input.getProficiency());
        return true;
    }

    private void updateExpertSkills(final String expertId, final String skillId, final Double proficiency) {
        final Optional<ExpertSkillsDAO> existingExpertSkillsDAO = this.expertSkillsRepository.findByExpertIdAndSkillId(expertId, skillId);
        ExpertSkillsDAO expertSkillsDAO = existingExpertSkillsDAO.isEmpty() ?
            ExpertSkillsDAO.builder().id(UUID.randomUUID().toString()).build() : existingExpertSkillsDAO.get();
        if (!proficiency.equals(expertSkillsDAO.getProficiency())) {
            expertSkillsDAO = expertSkillsDAO.toBuilder().expertId(expertId).skillId(skillId).proficiency(proficiency).build();
            this.expertSkillsRepository.save(expertSkillsDAO);
            final ExpertSkillsHistoryDAO expertSkillsHistoryDAO = ExpertSkillsHistoryDAO.builder().id(UUID.randomUUID().toString())
                .expertId(expertId)
                .skillId(skillId)
                .proficiency(proficiency).build();
            this.expertSkillsHistoryRepository.save(expertSkillsHistoryDAO);
        }
    }
}
