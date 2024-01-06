/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.common;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Constants {

	public static final String PARTNER_SUPER_ADMIN_ROLE_ID = "PARTNER_SUPER_ADMIN";

	public static final String SOURCING_PARTNER_ROLE_ID = "PARTNER_SOURCING_PARTNER";

	public static final String GLOBAL_ROLE_ADMIN_ID = "ADMIN";

	public static final String GLOBAL_ROLE_OPS_ID = "OPS";

	public static final String GLOBAL_ROLE_CANDIDATE_ID = "CANDIDATE";

	public static List<String> GLOBAL_SUPERUSER_ROLE_IDS = List.of(GLOBAL_ROLE_ADMIN_ID, GLOBAL_ROLE_OPS_ID);

	public static final String ALL_ACTION_ON_ALL_RESOURCES_PERMISSION_ID = "ALL";

}
