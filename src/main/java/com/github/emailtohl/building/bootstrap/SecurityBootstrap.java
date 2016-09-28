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

	/**
	 * 要启用并发控制,还必须配置一个特殊的Spring Security HttpListener，它可以发布HttpSession-related事件
	 * 这允许Spring Security注册表建立一个会话，它可以用来检测并发会话
	 */
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