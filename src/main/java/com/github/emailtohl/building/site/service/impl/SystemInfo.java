package com.github.emailtohl.building.site.service.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 获取系统信息的定时任务
 * @author HeLei
 */
@Component
public class SystemInfo {
	private static final Logger logger = LogManager.getLogger();
	
	@Scheduled(fixedDelay = 5000)
	public void showSystemInfo() {
		Runtime();
	}
	
	public void Runtime() {
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
			method.setAccessible(true);
			if (method.getName().startsWith("get") && Modifier.isPublic(method.getModifiers())) {
				Object value;
				try {
					value = method.invoke(operatingSystemMXBean);
				} catch (Exception e) {
					value = e;
				} // try
				logger.log(Level.TRACE, method.getName() + " = " + value);
			} // if
		} // for
	}
}
