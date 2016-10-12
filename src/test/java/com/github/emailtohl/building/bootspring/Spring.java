package com.github.emailtohl.building.bootspring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.emailtohl.building.config.RootContextConfiguration;

/**
 * 加载main环境下的spring配置
 * 
 * 单元测试中可能设置Spring环境，例如添加Bean进去：
 * AutowireCapableBeanFactory factory = Spring.context.getAutowireCapableBeanFactory();
 * factory.autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
 * factory.initializeBean(this, "testBean");
 * 
 * 所以简单地在junit上使用
 * @RunWith(SpringJUnit4ClassRunner.class)
 * @ContextConfiguration(classes = RootContextConfiguration.class)
 * 注解可能不满足需要
 * 
 * 这时候还是使用本类中的静态域：context，它只加载一次，并在虚拟机运行时均可使用Spring容器
 * 
 * @author HeLei
 */
public class Spring {
	public static final AnnotationConfigApplicationContext context;
	
	static {
		context = new AnnotationConfigApplicationContext();
		context.getEnvironment().setActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT);
		context.register(RootContextConfiguration.class);
		context.refresh();
	}
	
	private Spring() {
	}
	
	// 测试
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		AnnotationConfigApplicationContext ctx = Spring.context;
		System.exit(0);
	}
}
