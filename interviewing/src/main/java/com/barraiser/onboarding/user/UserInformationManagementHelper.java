/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.common.utilities.PhoneParser;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.interview.UserDetailsMapper;
import com.barraiser.onboarding.user.dto.UserDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class UserInformationManagementHelper {
	private static final String USER_ATTR_GIVEN_NAME = "given_name";
	private static final String USER_ATTR_EMAIL = "email";

	private final AWSCognitoIdentityProvider cognitoClient;
	private final PhoneParser phoneParser;
	private final StaticAppConfigValues staticAppConfigValues;
	private final UserDetailsRepository userDetailsRepository;
	private final UserDetailsMapper userDetailsMapper;
	private final CognitoClientSecretRetriever cognitoClientSecret;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;

	private static final Map<String, String> legacyRolesToNewRoleIdMapping = Map.of(
			"partner", "PARTNER_SUPER_ADMIN",
			"candidate", "CANDIDATE",
			"admin", "ADMIN",
			"ops", "OPS",
			"qc", "QC");

	public void updateCognito(final String userId, final Map<String, Object> userFields) {
		final String firstName = (String) userFields.get("firstName");
		final String lastName = (String) userFields.get("lastName");
		final String givenName = String.format("%s %s", firstName, lastName);
		final String email = (String) userFields.get("email");
		final String phone = (String) userFields.get("phone");
		final String formattedPhone = this.phoneParser.getFormattedPhone(phone);

		final String emailVerified = email != null ? "true" : "false";

		this.cognitoClient.adminUpdateUserAttributes(
				new AdminUpdateUserAttributesRequest()
						.withUserPoolId(this.staticAppConfigValues.getUserPoolId())
						.withUsername(userId)
						.withUserAttributes(List.of(
								new AttributeType().withName("given_name").withValue(givenName),
								new AttributeType().withName("email").withValue(email),
								new AttributeType().withName("custom:phone_number").withValue(formattedPhone),
								new AttributeType().withName("email_verified").withValue(emailVerified))));
	}

	public void updateUserAccessDataInDb(final String userId, final Map<String, Object> userFields) {

		final Optional<UserDetailsDAO> userDAO = this.userDetailsRepository.findById(userId);
		final String email = (String) userFields.get("email");
		final String phone = (String) userFields.get("phone");
		final String formattedPhone = this.phoneParser.getFormattedPhone(phone);

		this.userDetailsRepository.save(userDAO.get().toBuilder()
				.phone(formattedPhone)
				.email(email)
				.build());
	}

	/**
	 * This function gets user roles from cognito as well as
	 * from the user to role mapping authorization table.
	 *
	 * @param partnerId
	 * @param userId
	 * @return
	 */
	public List<String> getRoles(final String partnerId, final String userId) {
		final List<String> roles = new ArrayList<>();

		// Adding all roles that exist in the authenticated user for backward
		// compatability
		roles.addAll(this.getRolesOfUser(userId));

		// Adding partner level roles
		if (partnerId != null) {
			roles.addAll(this.authorizationServiceFeignClient
					.getActiveUserRoles(partnerId, userId)
					.stream().map(r -> r.getName()).collect(Collectors.toList()));
		}

		return roles;
	}

	public String createUserInCognito(final String email, final String givenName,
			final String preferredUserName) {
		// check if exists by email
		final String userName = Optional.ofNullable(preferredUserName)
				.orElse(UUID.randomUUID().toString());

		final List<AttributeType> attributes = new ArrayList<>();

		if (email != null) {
			attributes.add(new AttributeType().withName(USER_ATTR_EMAIL).withValue(email));
		}

		attributes.add(new AttributeType().withName(USER_ATTR_GIVEN_NAME).withValue(givenName));

		this.cognitoClient.signUp(new SignUpRequest()
				.withSecretHash(this.calculateSecretHash(this.staticAppConfigValues.getCognitoBackendClientId(),
						this.cognitoClientSecret.retrieve(), userName))
				.withClientId(this.staticAppConfigValues.getCognitoBackendClientId())
				.withUsername(userName)
				.withPassword(this.generateCommonLangPassword())
				.withUserAttributes(attributes));

		this.cognitoClient.adminConfirmSignUp(new AdminConfirmSignUpRequest()
				.withUsername(userName)
				.withUserPoolId(this.staticAppConfigValues.getUserPoolId()));

		final AdminUpdateUserAttributesRequest adminUpdateUserAttributesRequest = new AdminUpdateUserAttributesRequest()
				.withUserAttributes(new AttributeType().withName("email_verified").withValue("true"))
				.withUserPoolId(this.staticAppConfigValues.getUserPoolId())
				.withUsername(userName);

		this.cognitoClient.adminUpdateUserAttributes(adminUpdateUserAttributesRequest);

		return userName;
	}

	public String generateCommonLangPassword() {
		final String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
		final String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
		final String numbers = RandomStringUtils.randomNumeric(2);
		final String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
		final String totalChars = RandomStringUtils.randomAlphanumeric(2);
		final String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
				.concat(numbers)
				.concat(specialChar)
				.concat(totalChars);
		final List<Character> pwdChars = combinedChars.chars()
				.mapToObj(c -> (char) c)
				.collect(Collectors.toList());
		Collections.shuffle(pwdChars);
		final String password = pwdChars.stream()
				.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
				.toString();
		return password;
	}

	private String calculateSecretHash(final String userPoolClientId,
			final String userPoolClientSecret,
			final String userName) {
		final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

		final SecretKeySpec signingKey = new SecretKeySpec(
				userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
				HMAC_SHA256_ALGORITHM);
		try {
			final Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
			mac.init(signingKey);
			mac.update(userName.getBytes(StandardCharsets.UTF_8));
			final byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(rawHmac);
		} catch (final Exception e) {
			throw new RuntimeException("Error while calculating ");
		}
	}

	public boolean doesUserExistsByEmail(final String email) {
		return this.findIfUserExists("email", email);
	}

	private boolean findIfUserExists(final String attribute, final String value) {
		final ListUsersResult result = this.cognitoClient.listUsers(new ListUsersRequest()
				.withFilter(String.format("%s=\"%s\"", attribute, value))
				.withUserPoolId(this.staticAppConfigValues.getUserPoolId())
				.withLimit(1));
		return result.getUsers().size() > 0;
	}

	public Optional<String> findUserByEmail(final String email) {
		final ListUsersResult result = this.cognitoClient.listUsers(new ListUsersRequest()
				.withFilter(String.format("email=\"%s\"", email))
				.withUserPoolId(this.staticAppConfigValues.getUserPoolId()));
		final Optional<UserType> userByEmail = result.getUsers().stream().filter(x -> {
			final Optional<AttributeType> emailVerifiedAttribute = x.getAttributes().stream()
					.filter(y -> y.getName().equals("email_verified"))
					.findAny();
			return emailVerifiedAttribute.map(attributeType -> attributeType.getValue().equals("true")).orElse(false);
		}).findAny();

		final String userIdByEmail = userByEmail.orElse(new UserType()).getUsername();

		if (userIdByEmail == null) {
			return Optional.empty();
		}

		return Optional.of(userIdByEmail);
	}

	public void addUserRole(final String userId, final UserRole role) {
		this.cognitoClient.adminAddUserToGroup(new AdminAddUserToGroupRequest()
				.withUserPoolId(this.staticAppConfigValues.getUserPoolId())
				.withUsername(userId)
				.withGroupName(role.getRole()));
	}

	public void removeUserRole(final String userId, final UserRole role) {
		this.cognitoClient.adminRemoveUserFromGroup(new AdminRemoveUserFromGroupRequest()
				.withUserPoolId(this.staticAppConfigValues.getUserPoolId())
				.withUsername(userId)
				.withGroupName(role.getRole()));
	}

	public void removeAllUserRoles(final String userId) {
		for (final UserRole role : UserRole.values()) {
			this.removeUserRole(userId, role);
		}
	}

	public void updateUserRoles(final String userId, final List<UserRole> roles) {
		this.removeAllUserRoles(userId);
		roles.forEach(role -> this.addUserRole(userId, role));
	}

	public void updateUserAttributes(final String userId, final Map<String, String> attributes) {
		final List<AttributeType> attributeTypes = new ArrayList<>();
		attributes.forEach((x, y) -> attributeTypes.add(new AttributeType().withName(x).withValue(y)));
		this.cognitoClient.adminUpdateUserAttributes(new AdminUpdateUserAttributesRequest()
				.withUserAttributes(attributeTypes)
				.withUserPoolId(this.staticAppConfigValues.getUserPoolId())
				.withUsername(userId));
	}

	public UserDetailsDAO getOrCreateUserByEmail(final String email) {
		String userId = this.findUserByEmail(email).orElse(null);
		if (userId == null) {
			userId = this.createUserInCognito(email, "", null);
		}
		UserDetailsDAO user = this.userDetailsRepository.findById(userId).orElse(null);
		if (user == null) {
			user = UserDetailsDAO.builder()
					.id(userId)
					.email(email)
					.build();
			this.userDetailsRepository.save(user);
		}
		return user;
	}

	public UserDetailsDAO findUserById(final String id) {
		return this.userDetailsRepository.findById(id).get();
	}

	public void updateUserDetailsFromDAO(final UserDetailsDAO userDetails) {
		this.userDetailsRepository.save(userDetails);
		this.updateUserAttributes(userDetails.getId(), Map.of(
				"given_name",
				String.format("%s %s", userDetails.getFirstName(),
						userDetails.getLastName() == null ? "" : userDetails.getLastName()),
				"email", userDetails.getEmail(),
				"custom:phone_number", userDetails.getPhone() != null ? userDetails.getPhone() : "",
				"email_verified", "true"));
	}

	public List<String> getRolesOfUser(final String userId) {
		return this.cognitoClient.adminListGroupsForUser(new AdminListGroupsForUserRequest().withUsername(userId)
				.withUserPoolId(this.staticAppConfigValues.getUserPoolId())).getGroups().stream()
				.map(GroupType::getGroupName)
				.collect(Collectors.toList());
	}

	public Map<String, String> getUserAttributes(final String userId) {
		final AdminGetUserResult result = this.cognitoClient.adminGetUser(new AdminGetUserRequest()
				.withUserPoolId(this.staticAppConfigValues.getUserPoolId())
				.withUsername(userId));
		return result.getUserAttributes().stream()
				.collect(Collectors.toMap(AttributeType::getName, AttributeType::getValue));
	}

	public String getUpdatedUserPartnerId(final String userId, final String partnerId) {
		final String userPartnerIds = this.getUserAttributes(userId).get("custom:partnerId");
		return userPartnerIds == null || userPartnerIds.isEmpty() ? partnerId
				: Arrays.stream(userPartnerIds.split(","))
						.noneMatch(x -> x.equals(partnerId)) ? userPartnerIds + "," + partnerId : userPartnerIds;
	}

	public UserDetails getUserDetailsById(String id) {
		final Optional<UserDetailsDAO> userDetailsDAO = this.userDetailsRepository.findById(id);
		return userDetailsDAO.map(this.userDetailsMapper::getUserDetails).orElse(null);
	}

	/**
	 * Creates or updates the user details in database and on cognito.
	 * based on email id of the user that is passed in the input
	 */
	public String addOrUpdateUser(final UserDetailsDTO userDetails) {
		final UserDetailsDAO user = this.getOrCreateUserByEmail(userDetails.getEmail());
		this.updateUserDetailsFromDAO(user, userDetails);
		return user.getId();
	}

	/**
	 * First finds the user based on email , and then updates the basic info in db
	 * and on cognito.
	 * <p>
	 * NOTE: Email change is not supported.
	 */
	public void updateUserDetailsFromDTO(final String userId, final UserDetailsDTO userDetailsDTO) {
		final UserDetailsDAO user = this.userDetailsRepository.findById(userId).get();
		this.updateUserDetailsFromDAO(user, userDetailsDTO);
	}

	/**
	 * Updates user details in db and on cognito.
	 * <p>
	 * Does not support unsetting of values right now.
	 *
	 * @param user
	 * @param userDetailsDTO
	 */
	private void updateUserDetailsFromDAO(final UserDetailsDAO user, final UserDetailsDTO userDetailsDTO) {
		final String formattedPhone = this.phoneParser.getFormattedPhone(
				userDetailsDTO.getPhone() == null ? user.getPhone() : userDetailsDTO.getPhone());
		final String firstName = userDetailsDTO.getFirstName() == null ? user.getFirstName()
				: userDetailsDTO.getFirstName();
		final String lastName = userDetailsDTO.getLastName() == null ? user.getLastName()
				: userDetailsDTO.getLastName();
		this.updateUserDetailsFromDAO(user.toBuilder()
				.phone(formattedPhone)
				.firstName(firstName)
				.lastName(lastName)
				.build());
		final String updatedPartnerIds = this.getUpdatedUserPartnerId(user.getId(),
				userDetailsDTO.getPartnerId());
		this.updateUserAttributes(user.getId(), Map.of("custom:partnerId", updatedPartnerIds));
	}
}
