package com.github.emailtohl.building.bootstrap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * 向容器注册Spring Security’s filters
 * @author HeLei
 */
@Order(2)
public class SecurityBootstrap extends AbstractSecurityWebApplicationInitializer {
	private static final Logger logger = LogManager.getLogger();

	@Override
	protected boolean enableHttpSessionEventPublisher() {
		logger.info("Executing security bootstrap.");
		return true;
	}
	
/*
	@Override
	protected Set<SessionTrackingMode> getSessionTrackingModes() {
		return EnumSet.of(SessionTrackingMode.SSL);
	}
	*/
}