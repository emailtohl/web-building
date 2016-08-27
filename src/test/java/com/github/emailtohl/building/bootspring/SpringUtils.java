package com.github.emailtohl.building.bootspring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.emailtohl.building.config.RootContextConfiguration;

/**
 * 加载main环境下的spring配置
 *
 */
public class SpringUtils {
	public static final AnnotationConfigApplicationContext context;
	
	static {
		context = new AnnotationConfigApplicationContext();
		context.getEnvironment().setActiveProfiles(RootContextConfiguration.PROFILE_QA);
		context.register(RootContextConfiguration.class);
		context.refresh();
	}
	
	private SpringUtils() {
	}
	
	// 测试
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		AnnotationConfigApplicationContext ctx = SpringUtils.context;
		System.exit(0);
	}
}
