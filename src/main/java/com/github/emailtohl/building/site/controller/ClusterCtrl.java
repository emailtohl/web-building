package com.github.emailtohl.building.site.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.emailtohl.building.message.cluster.ClusterManager;
import com.github.emailtohl.building.message.event.ClusterEvent;
import com.github.emailtohl.building.message.event.LoginEvent;
import com.github.emailtohl.building.message.event.LogoutEvent;

@Controller
@RequestMapping("cluster")
public class ClusterCtrl {
	@Inject
	ApplicationEventPublisher publisher;

//	@RequestMapping("")
	public String login(HttpServletRequest request) {
		this.publisher.publishEvent(new LoginEvent(request.getContextPath()));
		return "login";
	}

//	@RequestMapping("/logout")
	public String logout(HttpServletRequest request) {
		this.publisher.publishEvent(new LogoutEvent(request.getContextPath()));
		return "logout";
	}

//	为了代码集中管理，已经在com.github.emailtohl.building.message.publisher中用servlet代替
//	@RequestMapping("/ping")
	public ResponseEntity<String> ping() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "text/plain;charset=UTF-8");
		return new ResponseEntity<>(ClusterManager.RESPONSE_OK, headers, HttpStatus.OK);
	}
	
	@RequestMapping("/frontMessage")
	@ResponseBody
	public void broadcastMessage(String message) {
		this.publisher.publishEvent(new ClusterEvent(message));
	}
}
