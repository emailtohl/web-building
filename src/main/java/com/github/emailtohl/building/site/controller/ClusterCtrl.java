package com.github.emailtohl.building.site.controller;

import javax.inject.Inject;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ClusterCtrl {
	@Inject
	ApplicationEventPublisher publisher;
/*
	@RequestMapping("")
	public String login(HttpServletRequest request) {
		this.publisher.publishEvent(new LoginEvent(request.getContextPath()));
		return "login";
	}

	@RequestMapping("/logout")
	public String logout(HttpServletRequest request) {
		this.publisher.publishEvent(new LogoutEvent(request.getContextPath()));
		return "logout";
	}
*/
	@RequestMapping("/ping")
	public ResponseEntity<String> ping() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "text/plain;charset=UTF-8");
		return new ResponseEntity<>("ok", headers, HttpStatus.OK);
	}
}
