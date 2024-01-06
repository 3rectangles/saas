/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.common.graphql.input.PartnerAccessInput;
import com.barraiser.common.utilities.PhoneParser;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.dal.PartnerRepsDAO;
import com.barraiser.onboarding.dal.PartnerRepsRepository;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.errorhandling.exception.IllegalOperationException;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;

import com.barraiser.onboarding.partner.auth.PartnerRepAccessUpdationAuthorizationInputConstructor;
import com.barraiser.onboarding.user.UserAccessManagementEventGenerator;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class AddPartnerRepresentative extends AuthorizedGraphQLMutation<Boolean> {

	private final GraphQLUtil graphQLUtil;
	private final UserInformationManagementHelper userInformationManagementHelper;
	private final InterviewingEventProducer eventProducer;
	private final PhoneParser phoneParser;
	private final PartnerRepresentativeAdditionService partnerRepresentativeAdditionService;
	private final PartnerRepresentativeRemovalService partnerRepresentativeRemovalService;
	private final PartnerRepUpdationService partnerRepUpdationService;
	private final PartnerRepsRepository partnerRepsRepository;
	private final UserAccessManagementEventGenerator userAccessManagementEventGenerator;

	public AddPartnerRepresentative(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			PartnerRepAccessUpdationAuthorizationInputConstructor partnerRepAccessUpdationAuthorizationInputConstructor,
			UserInformationManagementHelper userInformationManagementHelper,
			InterviewingEventProducer eventProducer,
			GraphQLUtil graphQLUtil,
			PhoneParser phoneParser,
			PartnerRepresentativeAdditionService partnerRepresentativeAdditionService,
			PartnerRepresentativeRemovalService partnerRepresentativeRemovalService,
			PartnerRepUpdationService partnerRepUpdationService,
			PartnerRepsRepository partnerRepsRepository,
			UserAccessManagementEventGenerator userAccessManagementEventGenerator) {

		super(authorizationServiceFeignClient, partnerRepAccessUpdationAuthorizationInputConstructor);
		this.userInformationManagementHelper = userInformationManagementHelper;
		this.eventProducer = eventProducer;
		this.graphQLUtil = graphQLUtil;
		this.phoneParser = phoneParser;
		this.partnerRepresentativeAdditionService = partnerRepresentativeAdditionService;
		this.partnerRepresentativeRemovalService = partnerRepresentativeRemovalService;
		this.partnerRepUpdationService = partnerRepUpdationService;
		this.partnerRepsRepository = partnerRepsRepository;
		this.userAccessManagementEventGenerator = userAccessManagementEventGenerator;
	}

	@Override
	protected Boolean fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult)
			throws Exception {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final PartnerAccessInput input = this.graphQLUtil.getInput(environment, PartnerAccessInput.class);

		if (input.getUserId() != null) {
			this.handlePartnerRepUpdation(authenticatedUser, input);
		} else {
			this.handlePartnerRepAddition(authenticatedUser, input);
		}
		return Boolean.TRUE;
	}

	public void handlePartnerRepAddition(final AuthenticatedUser actor, final PartnerAccessInput partnerAccessInput)
			throws IOException, IllegalOperationException {

		final UserDetailsDAO user = this.getOrCreateUser(partnerAccessInput.getEmail());

		if (this.isPartnerRepAdditionAllowed(user, partnerAccessInput)) {
			this.updateUserDetails(user, partnerAccessInput);
			this.partnerRepresentativeAdditionService.addPartnerRep(user.getId(), partnerAccessInput,
					actor.getUserName(), partnerAccessInput.getCreationSource(),
					partnerAccessInput.getCreationSourceMeta());
			this.generatePartnerRepAdditionEvent(user.getId(), partnerAccessInput.getPartnerId(), actor.getUserName());
		}
	}

	public void handlePartnerRepUpdation(final AuthenticatedUser actor, final PartnerAccessInput partnerAccessInput)
			throws IOException, IllegalOperationException {

		if (this.isPartnerRepUpdationAllowed(partnerAccessInput)) {

			final UserDetailsDAO userWithEmailInInput = this.getOrCreateUser(partnerAccessInput.getEmail());
			this.updateUserDetails(userWithEmailInInput, partnerAccessInput);

			final Optional<PartnerRepsDAO> partnerRepForUserWithEmailInInput = this
					.getPartnerRep(userWithEmailInInput.getId(), partnerAccessInput.getPartnerId());

			if (!partnerRepForUserWithEmailInInput.isEmpty()) {

				if (!userWithEmailInInput.getId().equals(partnerAccessInput.getUserId())) {
					this.partnerRepresentativeRemovalService.removeAsPartnerRep(actor, partnerAccessInput.getUserId(),
							partnerAccessInput.getPartnerId());
				}

				this.partnerRepUpdationService.updatePartnerRep(userWithEmailInInput.getId(), partnerAccessInput,
						actor.getUserName(), partnerAccessInput.getCreationSource(),
						partnerAccessInput.getCreationSourceMeta());
			} else {
				this.partnerRepresentativeRemovalService.removeAsPartnerRep(actor, partnerAccessInput.getUserId(),
						partnerAccessInput.getPartnerId());
				this.partnerRepresentativeAdditionService.addPartnerRep(userWithEmailInInput.getId(),
						partnerAccessInput, actor.getUserName(), partnerAccessInput.getCreationSource(),
						partnerAccessInput.getCreationSourceMeta());
			}
		}
	}

	private Optional<PartnerRepsDAO> getPartnerRep(final String userId, final String partnerId) {
		return this.partnerRepsRepository
				.findByPartnerRepIdAndPartnerId(userId, partnerId);
	}

	private Boolean isPartnerRepAdditionAllowed(final UserDetailsDAO user,
			final PartnerAccessInput partnerAccessInput) throws IllegalOperationException {

		if (partnerAccessInput.getUserId() != null) {
			throw new IllegalArgumentException("Cannot pass user id while adding a partner rep.");
		}

		if (!this.getPartnerRep(user.getId(), partnerAccessInput.getPartnerId()).isEmpty()) {
			throw new IllegalOperationException("The user already has access on this portal",
					"The user already has access on this portal", 1003);
		}

		return Boolean.TRUE;
	}

	private Boolean isPartnerRepUpdationAllowed(final PartnerAccessInput partnerAccessInput)
			throws IllegalOperationException {

		if (partnerAccessInput.getUserId() == null) {
			throw new IllegalArgumentException("Cannot pass user id while adding a partner rep.");
		}

		if (this.getPartnerRep(partnerAccessInput.getUserId(), partnerAccessInput.getPartnerId()).isEmpty()) {
			throw new IllegalOperationException("The user you are trying to update does have access to this portal.",
					"The user you are trying to update does have access to this portal.", 1004);
		}

		return Boolean.TRUE;
	}

	private UserDetailsDAO getOrCreateUser(final String email) {
		final UserDetailsDAO user = this.userInformationManagementHelper.getOrCreateUserByEmail(email);
		return user;
	}

	private void generatePartnerRepAdditionEvent(final String partnerRepId, final String partnerId,
			final String accessGrantorId) {
		this.userAccessManagementEventGenerator.sendUserAccessGrantedEvent(partnerRepId, partnerId, accessGrantorId);
	}

	private void updateUserDetails(final UserDetailsDAO user, final PartnerAccessInput input) {
		final String formattedPhone = this.phoneParser.getFormattedPhone(
				input.getPhone() == null ? user.getPhone() : input.getPhone());
		final String firstName = input.getFirstName() == null ? user.getFirstName() : input.getFirstName();
		final String lastName = input.getLastName() == null ? user.getLastName() : input.getLastName();
		this.userInformationManagementHelper.updateUserDetailsFromDAO(user.toBuilder()
				.phone(formattedPhone)
				.firstName(firstName)
				.lastName(lastName)
				.build());

		final String updatedPartnerIds = this.userInformationManagementHelper.getUpdatedUserPartnerId(user.getId(),
				input.getPartnerId());
		this.userInformationManagementHelper.updateUserAttributes(user.getId(),
				Map.of("custom:partnerId", updatedPartnerIds));
	}

	@Override
	public String name() {
		return "addPartnerRepresentative";
	}
}
