package com.github.emailtohl.building.message.observer;

import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_PRODUCTION;
import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_QA;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.message.event.ClusterEvent;

@Profile({ PROFILE_PRODUCTION, PROFILE_QA })
@Service
public class ClusterInterestedParty implements ApplicationListener<ClusterEvent> {
	private static final Logger log = LogManager.getLogger();

	@Inject ServletContext servletContext;
	@Inject SimpleApplicationEventMulticaster clusterEventMulticaster;

	@Override
	public void onApplicationEvent(ClusterEvent event) {
		log.debug("Cluster event for context {} received in context {}.", event.getSource(),
				this.servletContext.getContextPath());
	}
}
