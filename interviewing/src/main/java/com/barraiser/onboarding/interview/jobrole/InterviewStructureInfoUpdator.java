/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jobrole;

import com.barraiser.ats_integrations.dto.ATSInterviewStructureMappingsDTO;
import com.barraiser.common.enums.RoundType;
import com.barraiser.commons.dto.jobRoleManagement.InterviewStructureInput;
import com.barraiser.onboarding.ats_integrations.ATSServiceClient;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.feeback.firestore.v1.InterviewFlow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.barraiser.common.constants.Constants.CREATION_SOURCE_ATS_INTEGRATION;

/**
 * Used to update the interview structure info and
 * job role to interview structure mappings
 */
@AllArgsConstructor
@Component
public class InterviewStructureInfoUpdator {

	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final InterviewStructureRepository interviewStructureRepository;
	private final InterviewStructureSkillsRepository interviewStructureSkillsRepository;
	private final DefaultQuestionsRepository defaultQuestionsRepository;
	private final ATSServiceClient atsServiceClient;
	private final ObjectMapper objectMapper;

	/**
	 * @param previousJRVersionBRJobRoleId
	 *            new UUID for a job role getting created.
	 * @param previousJRVersionBRJobRoleVersion
	 *            -1 for a job role that is getting created for first time
	 * @param partnerId
	 * @param interviewStructureInputs
	 * @param creationSource
	 */
	public void saveInterviewStructureInfo(final String previousJRVersionBRJobRoleId,
			final Integer previousJRVersionBRJobRoleVersion,
			final String partnerId,
			final List<InterviewStructureInput> interviewStructureInputs,
			final String creationSource,
			final String flowVersion) throws JsonProcessingException {

		/**
		 * Get ATS Interview structure mappings for partner
		 */
		final Map<String, String> atsToBRInterviewStructureMapping = Objects
				.requireNonNull(this.atsServiceClient.getATSInterviewStructureMappings(partnerId).getBody())
				.getInterviewStructureMappings()
				.stream()
				.collect(Collectors.toMap(
						ATSInterviewStructureMappingsDTO.InterviewStructureMapping::getAtsInterviewStructureId,
						(ATSInterviewStructureMappingsDTO.InterviewStructureMapping::getBrInterviewStructureId)));

		/**
		 * We always create new interview structures.
		 */
		this.createInterviewStructures(interviewStructureInputs, atsToBRInterviewStructureMapping, flowVersion);

		this.createJobRoleToInterviewStructureMappings(previousJRVersionBRJobRoleId, previousJRVersionBRJobRoleVersion,
				interviewStructureInputs,
				atsToBRInterviewStructureMapping,
				creationSource);

		this.copyDefaultQuestions(interviewStructureInputs, atsToBRInterviewStructureMapping);

	}

	/**
	 * If for an ats id the interview structure mapping already EXISTS means that
	 * was previously synced. In that case we copy details from the old interview
	 * sturcture.
	 * If for an ats id the interview structure mapping DOESNT EXIST , we create new
	 * interview structure without copying any old info
	 * <p>
	 * * NOTE : We have to do all this as our system is built on a fundamental that
	 * no interview strcuture is shared across job roles.
	 * Always new interview structure id is generated
	 *
	 * @param interviewStructureInputs
	 * @param atsToBrInterviewStructureMapping
	 */
	private void createInterviewStructures(final List<InterviewStructureInput> interviewStructureInputs,
			final Map<String, String> atsToBrInterviewStructureMapping,
			final String flowVersion) throws JsonProcessingException {

		for (int i = 0; i < interviewStructureInputs.size(); i++) {

			/**
			 * Case where there was a version of the interview structure that was synced
			 * into the system previously.
			 *
			 * So in this case we copy all info from the associated interview structure in
			 * our system and apply an update on top of
			 * that from the input coming from the ATS sync.
			 *
			 */
			if (atsToBrInterviewStructureMapping.containsKey(interviewStructureInputs.get(i).getAtsId())) {
				// create interview structure based on the old interview structure that was
				// mapped against the ATS id , so no changes are lost.
				this.createInterviewStructure(
						atsToBrInterviewStructureMapping.get(interviewStructureInputs.get(i).getAtsId()),
						interviewStructureInputs.get(i));

				this.copyInterviewStructureToSkillsMapping(
						atsToBrInterviewStructureMapping.get(interviewStructureInputs.get(i).getAtsId()),
						interviewStructureInputs.get(i).getId());

			} else {
				/**
				 * We create fresh interview structure.
				 */
				this.createInterviewStructure(interviewStructureInputs.get(i), flowVersion);
			}
		}
	}

	public void createInterviewStructure(final InterviewStructureInput interviewStructureInput,
			final String flowVersion) throws JsonProcessingException {
		this.interviewStructureRepository.save(
				InterviewStructureDAO.builder()
						.id(interviewStructureInput.getId())
						.name(interviewStructureInput.getName())
						.domainId(interviewStructureInput.getDomainId())
						.interviewFlow(
								interviewStructureInput.getInterviewFlow() != null
										? interviewStructureInput.getInterviewFlow()
										: this.objectMapper.writeValueAsString(InterviewFlow.builder()
												.version(flowVersion)
												.build()))
						.duration(interviewStructureInput.getDuration())
						.isBrRound(interviewStructureInput.getIsBrRound())
						.build());

		if (interviewStructureInput.getCategoryIds() != null) {
			this.interviewStructureSkillsRepository.saveAll(
					interviewStructureInput.getCategoryIds().stream()
							.map(x -> {
								return InterviewStructureSkillsDAO.builder()
										.id(UUID.randomUUID().toString())
										.interviewStructureId(interviewStructureInput.getId())
										.skillId(x)
										.build();
							})
							.collect(Collectors.toList()));
		}
	}

	/**
	 * It uses the previous interview strcuture of BR system that was like the
	 * previous version of the
	 * interview structure to be added.
	 * <p>
	 * Then updates the values in it based on that and creates new interview
	 * structure based on it. So that no changes are lost.
	 *
	 * @param interviewStructureIdWhoseInfoIsToBeCopied
	 * @param interviewStructureInput
	 */
	public void createInterviewStructure(final String interviewStructureIdWhoseInfoIsToBeCopied,
			final InterviewStructureInput interviewStructureInput) {

		final InterviewStructureDAO interviewStructureDAO = this.interviewStructureRepository
				.findById(interviewStructureIdWhoseInfoIsToBeCopied).get();

		this.interviewStructureRepository.save(
				interviewStructureDAO.toBuilder()
						.id(interviewStructureInput.getId())
						.name(interviewStructureInput.getName())
						.build());
	}

	public void createJobRoleToInterviewStructureMappings(final String previousJRVersionBRJobRoleId,
			final Integer previousJRVersionBRVersionId,
			final List<InterviewStructureInput> interviewStructureInputs,
			final Map<String, String> atsToBrInterviewStructureMappings,
			final String creationSource) {

		final List<JobRoleToInterviewStructureDAO> oldJobRoleToInterviewStructureDAOs = this.jobRoleToInterviewStructureRepository
				.findAllByJobRoleIdAndJobRoleVersionOrderByOrderIndexAsc(previousJRVersionBRJobRoleId,
						previousJRVersionBRVersionId);

		/**
		 * CASE 1 : This will be empty incase the job role is getting created for the
		 * first time.
		 * In such a case all interview structures will be created from scratch.
		 */
		if (oldJobRoleToInterviewStructureDAOs.size() == 0) {

			for (int i = 0; i < interviewStructureInputs.size(); i++) {

				this.createJobRoleToInterviewStructureMappings(previousJRVersionBRJobRoleId,
						previousJRVersionBRVersionId, i,
						interviewStructureInputs.get(i), creationSource);
			}

		} else {

			for (int i = 0; i < interviewStructureInputs.size(); i++) {

				/**
				 * CASE B : More rounds are being added than there were previously in the job
				 * role, so for all those rounds this condition will be true.
				 */
				if (i > oldJobRoleToInterviewStructureDAOs.size()) {
					// create job role mapping
					this.createJobRoleToInterviewStructureMappings(previousJRVersionBRJobRoleId,
							previousJRVersionBRVersionId, i,
							interviewStructureInputs.get(i), creationSource);
				} else {

					if (atsToBrInterviewStructureMappings.containsKey(interviewStructureInputs.get(i).getAtsId())
							&& oldJobRoleToInterviewStructureDAOs.get(i)
									.getInterviewStructureId() == atsToBrInterviewStructureMappings
											.get(interviewStructureInputs.get(i).getAtsId())) {

						/**
						 * CASE C : For the same round index, the corresponding interview structure id
						 * in the previous version of the job role , should be same as the
						 * br interview structure id corrsponding to the ats interview structure id in
						 * the input. This means that no information should be lost from
						 * the previous version of the job role as its the same interview structure as
						 * it is was on ats .
						 *
						 * This means this is updation case.
						 */

						this.updateJobRoleToInterviewStructureMappings(previousJRVersionBRJobRoleId,
								previousJRVersionBRVersionId,
								oldJobRoleToInterviewStructureDAOs.get(i), i, interviewStructureInputs.get(i),
								creationSource);
					} else {

						/**
						 * CASE D : There is no mapping for the ats interview str id in the input in our
						 * system means its a fresh IS.
						 * This means this is creation case.
						 */

						this.createJobRoleToInterviewStructureMappings(previousJRVersionBRJobRoleId,
								previousJRVersionBRVersionId, i,
								interviewStructureInputs.get(i), creationSource);
					}
				}
			}
		}

	}

	private void createJobRoleToInterviewStructureMappings(final String oldJobRoleId, final Integer oldJobRoleVersion,
			final Integer orderIndex, final InterviewStructureInput interviewStructureInput,
			final String creationSource) {
		final String interviewRound = CREATION_SOURCE_ATS_INTEGRATION.equals(creationSource)
				? RoundType.INTERNAL.getValue()
				: null;

		this.jobRoleToInterviewStructureRepository.save(
				JobRoleToInterviewStructureDAO.builder()
						.id(UUID.randomUUID().toString())
						.jobRoleId(oldJobRoleId)
						.jobRoleVersion(oldJobRoleVersion + 1)
						.interviewRound(interviewRound)
						.orderIndex(orderIndex)
						.interviewStructureId(interviewStructureInput.getId())
						.build());
	}

	/**
	 * Please note that update means new object is created , but with the values
	 * from the previous job role version + delta changes in input.
	 * Job role to interview structure mappings are immutable as per the db
	 */
	private JobRoleToInterviewStructureDAO updateJobRoleToInterviewStructureMappings(final String oldJobRoleId,
			final Integer oldJobRoleVersion,
			final JobRoleToInterviewStructureDAO oldJobRoleToInterviewStructureDAO,
			final Integer orderIndex,
			final InterviewStructureInput interviewStructureInput,
			final String creationSource) {

		final String interviewRound = CREATION_SOURCE_ATS_INTEGRATION.equals(creationSource)
				? RoundType.INTERNAL.getValue()
				: null;

		return this.jobRoleToInterviewStructureRepository.save(
				oldJobRoleToInterviewStructureDAO.toBuilder()
						.id(UUID.randomUUID().toString())
						.jobRoleId(oldJobRoleId)
						.jobRoleVersion(oldJobRoleVersion + 1)
						.interviewRound(interviewRound)
						.orderIndex(orderIndex)
						.interviewStructureId(interviewStructureInput.getId())
						.build());
	}

	private void copyDefaultQuestions(final List<InterviewStructureInput> interviewStructureInputs,
			final Map<String, String> atsToBRInterviewStructureMapping) {

		for (int i = 0; i < interviewStructureInputs.size(); i++) {

			/**
			 * This means this interview structure was already created in our system
			 * previously so we will copy
			 * the default questions against it and save it against the new interview id.
			 */

			if (atsToBRInterviewStructureMapping.containsKey(interviewStructureInputs.get(i).getAtsId())) {
				final List<DefaultQuestionsDAO> defaultQuestionsAgainstThePreviousVersionOfATSInterviewStructure = this.defaultQuestionsRepository
						.findAllByInterviewStructureId(
								atsToBRInterviewStructureMapping.get(interviewStructureInputs.get(i).getAtsId()));

				final int index = i;
				this.defaultQuestionsRepository.saveAll(
						defaultQuestionsAgainstThePreviousVersionOfATSInterviewStructure.stream()
								.map(dq -> dq.toBuilder()
										.id(UUID.randomUUID().toString())
										.interviewStructureId(interviewStructureInputs.get(index).getId())
										.build())
								.collect(Collectors.toList()));
			}

		}

	}

	/**
	 * @param interviewStructureIdWhoseMappingsAreToBeCopied
	 * @param interviewStructureId
	 */
	private void copyInterviewStructureToSkillsMapping(final String interviewStructureIdWhoseMappingsAreToBeCopied,
			final String interviewStructureId) {

		this.interviewStructureSkillsRepository.saveAll(
				this.interviewStructureSkillsRepository
						.findAllByInterviewStructureId(interviewStructureIdWhoseMappingsAreToBeCopied)
						.stream()
						.map(iss -> iss.toBuilder()
								.interviewStructureId(interviewStructureId)
								.build())
						.collect(Collectors.toList()));

	}

	/**
	 * If there is no change in the structuring whatsoever is only when
	 * skillWeightages should be copied from previous version of job role.
	 * <p>
	 * TODO: Ideally we should collect distinct skills to be added a
	 */
	public void shouldCopySkillWeightage() {

	}

}
