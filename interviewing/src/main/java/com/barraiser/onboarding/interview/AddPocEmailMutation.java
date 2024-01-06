/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.AddPocEmailInput;
import com.barraiser.onboarding.dal.PartnerRepsDAO;
import com.barraiser.onboarding.dal.PartnerRepsRepository;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.graphql.GraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class AddPocEmailMutation implements GraphQLMutation {
	private final GraphQLUtil graphQLUtil;
	private final UserInformationManagementHelper userManagement;
	private final PartnerRepsRepository partnerRepsRepository;
	private final UserDetailsRepository userDetailsRepository;

	@Override
	public String name() {
		return "addPocEmail";
	}

	@Override
	public Boolean get(final DataFetchingEnvironment environment) throws Exception {
		final AddPocEmailInput input = this.graphQLUtil.getInput(environment, AddPocEmailInput.class);

		final List<String> emailList = this.removeExistingPocEmailsFromList(input.getEmails());

		this.addPocEmails(emailList, input.getPartnerId());

		return true;
	}

	private void addPocEmails(final List<String> emailList, final String partnerId) {
		final List<UserDetailsDAO> usersToAdd = new ArrayList<>();
		final List<PartnerRepsDAO> listOfPartnerReps = new ArrayList<>();

		emailList.stream().forEach(
				(email) -> {
					String userId = null;
					try {
						userId = this.userManagement.createUserInCognito(
								email, email, null);
					} catch (final Exception e) {
						log.info(e);
					}
					usersToAdd.add(UserDetailsDAO.builder()
							.email(email)
							.id(userId)
							.build());
					listOfPartnerReps.add(PartnerRepsDAO.builder()
							.id(UUID.randomUUID().toString())
							.partnerId(partnerId)
							.partnerRepId(userId)
							.build());
				});
		this.userDetailsRepository.saveAll(usersToAdd);
		this.partnerRepsRepository.saveAll(listOfPartnerReps);
	}

	private List<String> removeExistingPocEmailsFromList(final List<String> emailList) {
		this.userDetailsRepository.findByEmailIn(emailList)
				.stream().forEach((user) -> {
					emailList.remove(user.getEmail());
				});

		return emailList;
	}

}
