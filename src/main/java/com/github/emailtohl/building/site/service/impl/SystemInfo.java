package com.github.emailtohl.building.site.service.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
/**
 * 获取系统信息的定时任务
 * @author HeLei
 * @date 2017.02.04
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
		Map<String, Object> info = new HashMap<>();
		for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
			method.setAccessible(true);
			if (method.getName().startsWith("get") && Modifier.isPublic(method.getModifiers())) {
				Object value;
				try {
					value = method.invoke(operatingSystemMXBean);
					logger.log(Level.TRACE, method.getName() + " = " + value);
					info.put(method.getName(), value);
				} catch (Exception e) {
					logger.info(e);
				} // try
			} // if
		} // for
		observes.forEach(o -> o.notify(info));
	}
	
	public static interface Observe {
		void notify(Map<String, Object> info);
	}
	
	private List<Observe> observes = new ArrayList<>();
	
	public void register(Observe o) {
		observes.add(o);
	}
	
	public void remove(Observe o) {
		observes.remove(o);
	}
	
}
