package com.github.emailtohl.building.message.listener;

import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_PRODUCTION;
import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_QA;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.message.event.LoginEvent;

@Profile({ PROFILE_PRODUCTION, PROFILE_QA })
@Service
public class LoginInterestedParty implements ApplicationListener<LoginEvent> {
	private static final Logger log = LogManager.getLogger();

	@Inject
	ServletContext servletContext;

	@Override
	public void onApplicationEvent(LoginEvent event) {
		log.info("Login event for context {} received in context {}.", event.getSource(),
				this.servletContext.getContextPath());
	}
}
