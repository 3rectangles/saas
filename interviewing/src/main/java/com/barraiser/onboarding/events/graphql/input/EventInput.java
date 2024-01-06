/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.events.graphql.input;

import com.barraiser.common.graphql.UserDetailsInput;
import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.sendinterviewerfeedback.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EventInput {
	private String partnerId;
	private String userId;
	private String eventType;
	private String event;
}
