package com.github.emailtohl.building.message.listener;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;

import com.github.emailtohl.building.message.event.ClusterEvent;

//@Service
public class ClusterInterestedParty implements ApplicationListener<ClusterEvent> {
	private static final Logger log = LogManager.getLogger();

	@Inject
	ServletContext servletContext;
	@Inject
	SimpleApplicationEventMulticaster clusterEventMulticaster;

	@Override
	public void onApplicationEvent(ClusterEvent event) {
		log.info("Cluster event for context {} received in context {}.", event.getSource(),
				this.servletContext.getContextPath());
	}
}
