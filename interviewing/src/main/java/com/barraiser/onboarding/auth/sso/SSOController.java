/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.sso;

import com.barraiser.onboarding.auth.AuthenticationException;
import com.barraiser.onboarding.auth.sso.dto.SignInWithSSODTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
public class SSOController {
	private final SSOService ssoService;

	@PostMapping(value = "sso")
	public ResponseEntity<Void> signIn(@RequestBody SignInWithSSODTO body, final HttpServletResponse response)
			throws IOException {
		log.info("attempting login via sso for provider : " + body.getSource());
		try {
			final List<ResponseCookie> cookies = this.ssoService.signIn(body);
			cookies.forEach(c -> response.addHeader(HttpHeaders.SET_COOKIE, c.toString()));
			return ResponseEntity.ok().build();
		} catch (final AuthenticationException e) {
			if (e.getType().equals(AuthenticationException.USER_NOT_FOUND)) {
				log.warn("user not found for sso login");
				return ResponseEntity.notFound().build();
			}
			log.warn("sso login failed");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
}
