package com.barraiser.onboarding.interview.jira;

import com.barraiser.onboarding.dal.InterviewStructureDAO;
import com.barraiser.onboarding.dal.InterviewStructureSkillsDAO;
import com.barraiser.onboarding.dal.JiraUUIDDAO;
import com.barraiser.onboarding.interview.InterviewStructureManager;
import com.barraiser.onboarding.interview.InterviewStructureSkillManager;
import com.barraiser.onboarding.interview.jira.client.JiraClient;
import com.barraiser.onboarding.interview.jira.dto.IdValueField;
import com.barraiser.onboarding.interview.jira.dto.InterviewStructureIssue;
import com.barraiser.onboarding.interview.jira.dto.JiraEvent;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewStructureDataHandler implements JiraEventHandler {
    public static final String JIRA_ISSUE_TYPE_ID_INTERVIEW_STRUCTURE = "10069";

    private final JiraUtil jiraUtil;
    private final JiraClient jiraClient;
    private final InterviewStructureManager interviewStructureManager;
    private final InterviewStructureSkillManager interviewStructureSkillManager;

    @Override
    public String projectId() {
        return JIRA_ISSUE_TYPE_ID_INTERVIEW_STRUCTURE;
    }

    @Override
    @Transactional
    public void handleEvent(final JiraEvent event) {
        final InterviewStructureIssue issue = this.jiraClient.getInterviewStructure(event.getIssue());
        log.info(issue.getKey());

        final JiraUUIDDAO id = this.jiraUtil.getOrCreateIdAgainstJira(issue.getKey());

        final Optional<InterviewStructureDAO> interviewStructureInDb = this.interviewStructureManager
            .getInterviewStructure(id.getUuid());

        //We want to ensure that only one interview structure gets created from one ticket.
        if (interviewStructureInDb.isPresent()) {
            log.info("Only one interview structure can be created from this ticket. Already one is created.Can update existing one");
        }

        final String interviewStructureName = interviewStructureInDb.isPresent() ? interviewStructureInDb.get().getId() : issue.getFields().getStructureName();
        final String domainId = issue.getFields().getDomainId() == null ? null : this.jiraUtil.getIdFromString(issue.getFields().getDomainId().getValue());
        final Integer minExperience = issue.getFields().getMinExperience();
        final Integer maxExperience = issue.getFields().getMaxExperience();
        final List<IdValueField> skills = issue.getFields().getSkills();
        final List<IdValueField> skillsToBeFocussedOnIdValues = issue.getFields().getSkillsToBeFocussedOn();

        this.interviewStructureManager.add(InterviewStructureDAO.builder()
            .id(interviewStructureName)
            .name(interviewStructureName)
            .domainId(domainId)
            .minExperience(minExperience)
            .maxExperience(maxExperience)
            .jiraIssueId(id.getUuid())
            .build());

        //delete all skill mappings for this interview structure first
        this.interviewStructureSkillManager.delete(interviewStructureName);

        List<String> skillsToBeFocussedOn = null;
        if (skillsToBeFocussedOnIdValues != null) {
            skillsToBeFocussedOn = skillsToBeFocussedOnIdValues.stream().map(IdValueField::getValue).map(this.jiraUtil::getIdFromString).collect(Collectors.toList());
        }

        if (skills != null) {
            //Only inserting new skill IS relation

            final List<String> skillsToBeFoccussedTemp = skillsToBeFocussedOn;
            skills.stream().map(IdValueField::getValue).map(this.jiraUtil::getIdFromString).forEachOrdered(x -> {
                final InterviewStructureSkillsDAO interviewStructureSkillsDAO = InterviewStructureSkillsDAO.builder()
                    .id(UUID.randomUUID().toString())
                    .toBeFocussed(skillsToBeFoccussedTemp != null && skillsToBeFoccussedTemp.contains(x))
                    .interviewStructureId(interviewStructureName).skillId(x).build();
                this.interviewStructureSkillManager.add(interviewStructureSkillsDAO);
            });
        }
    }


}
