package com.github.emailtohl.building.bootspring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.emailtohl.building.config.RootContextConfiguration;

/**
 * 加载main环境下的spring配置
 *
 */
public class SpringUtils {
	public static final AnnotationConfigApplicationContext ctx;
	
	static {
		ctx = new AnnotationConfigApplicationContext();
		ctx.getEnvironment().setActiveProfiles("qa");
		ctx.register(RootContextConfiguration.class);
		ctx.refresh();
	}
	
	private SpringUtils() {
	}
	
	// 测试
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		AnnotationConfigApplicationContext ctx = SpringUtils.ctx;
		System.exit(0);
	}
}
