package com.github.emailtohl.building.bootspring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.emailtohl.building.config.RootContextConfiguration;

/**
 * 加载main环境下的spring配置
 * 
 * 单元测试中可能会在Spring已经启动好后设置Spring环境，例如添加Bean进容器：
 * AutowireCapableBeanFactory factory = Spring.context.getAutowireCapableBeanFactory();
 * factory.autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
 * factory.initializeBean(this, "testBean");
 * 
 * 所以简单地在junit上使用
 * @RunWith(SpringJUnit4ClassRunner.class)
 * @ContextConfiguration(classes = RootContextConfiguration.class)
 * 注解可能不满足需要
 * 
 * 所以还需要手动创建Spring上下文，本类将Spring上下文设置到静态域，它是只加载一次的单例，可在虚拟机运行时一直存在
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
