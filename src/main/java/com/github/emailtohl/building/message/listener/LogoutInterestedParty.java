package com.github.emailtohl.building.message.listener;

import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_PRODUCTION;
import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_QA;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.message.event.LogoutEvent;

@Profile({ PROFILE_PRODUCTION, PROFILE_QA })
@Service
public class LogoutInterestedParty implements ApplicationListener<LogoutEvent> {
	private static final Logger log = LogManager.getLogger();

	@Inject
	ServletContext servletContext;

	@Override
	@Async
	public void onApplicationEvent(LogoutEvent event) {
		log.info("Logout event for user {} received in context {}.", event.getSource(),
				this.servletContext.getContextPath());

		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
			log.error(e);
		}
	}
}
