/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.UserRole;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@Log4j2
@AllArgsConstructor
public class ImpersonateUserController {
	private final AuthenticationManager authenticationManager;

	@GetMapping(path = "/impersonate", params = { "email" })
	public ResponseEntity<Void> impersonateUser(
			@RequestParam(value = "email") final String email,
			@RequestAttribute(name = "loggedInUser") AuthenticatedUser user,
			final HttpServletResponse response) {
		log.info(String.format("trying to impersonate : %s", email));
		if (user == null) {
			log.error("unauthenticated user trying to impersonate");
			throw new AuthenticationException("");
		}
		if (!user.getRoles().contains(UserRole.SUPER_ADMIN)) {
			log.error(user.getUserName() + " trying to impersonate with insufficient permissions");
			throw new AuthorizationException("");
		}
		final List<ResponseCookie> cookies = this.authenticationManager
				.authenticateWithOtpWithoutClearingSSODetails(email, "");
		cookies.forEach(c -> response.addHeader(HttpHeaders.SET_COOKIE, c.toString()));
		return ResponseEntity.ok().build();
	}
}
