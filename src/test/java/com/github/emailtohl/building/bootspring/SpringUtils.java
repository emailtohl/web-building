package com.github.emailtohl.building.bootspring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 加载main环境下的spring配置
 *
 */
public class SpringUtils {
	public static final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
			RootContextConfigurationForTest.class);

	private SpringUtils() {
	}
	
	// 测试
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		AnnotationConfigApplicationContext ctx = SpringUtils.ctx;
		System.exit(0);
	}
}
