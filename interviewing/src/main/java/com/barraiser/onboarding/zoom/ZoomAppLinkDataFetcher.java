/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.zoom;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.*;
import com.barraiser.onboarding.zoom_app.ZoomAppServiceClient;
import com.barraiser.onboarding.zoom_app.ZoomDeepLinkRequest;
import feign.FeignException;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class ZoomAppLinkDataFetcher extends AuthorizedGraphQLQuery_deprecated<String> {
	private final ZoomAppServiceClient zoomAppServiceClient;
	private final GraphQLUtil graphQLUtil;

	@Value("${zoom.app.installation-link}")
	private String installationLink;

	public ZoomAppLinkDataFetcher(
			AllowAllAuthorizer authorizer,
			ObjectFieldsFilter<String> objectFieldsFilter,
			ZoomAppServiceClient zoomAppServiceClient,
			GraphQLUtil graphQLUtil) {
		super(authorizer, objectFieldsFilter);
		this.zoomAppServiceClient = zoomAppServiceClient;
		this.graphQLUtil = graphQLUtil;
	}

	@Override
	protected String fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		final Interview interview = environment.getSource();
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		try {
			final ResponseEntity<String> response = this.zoomAppServiceClient.getDeepLink(ZoomDeepLinkRequest.builder()
					.email(user.getEmail())
					.payload(ZoomDeepLinkRequest.Payload.builder()
							.joinLink(interview.getMeetingLink())
							.build())
					.build());
			return response.getBody();
		} catch (final FeignException e) {
			if (e.status() == 404) {
				return this.installationLink;
			}
			throw e;
		}
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("Interview", "zoomAppLink"));
	}
}
