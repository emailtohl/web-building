package com.github.emailtohl.building.bootspring;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
/**
 * JPA的测试配置
 * @author HeLei
 *
 */
@Configuration
// 启用注解式事务管理
@EnableTransactionManagement(
		mode = AdviceMode.PROXY, proxyTargetClass = false, 
		order = Ordered.LOWEST_PRECEDENCE)
// 这时SpringData的注解，扫描DAO层的接口类，SpringData可为DAO中的接口生成代理
// repositoryImplementationPostfix = "Impl" 的含义是匹配接口名后面含"Impl"的实现类，对于已实现的方法，springdata不再生成代理，而是委托给实现类
@EnableJpaRepositories(basePackages = "com.github.emailtohl.building.site.dao", 
		repositoryImplementationPostfix = "Impl", 
		transactionManagerRef = "jpaTransactionManager", 
		entityManagerFactoryRef = "entityManagerFactory")
public class JPAConfigurationForTest {

	@Bean
	public LocalEntityManagerFactoryBean entityManagerFactory() {
		LocalEntityManagerFactoryBean emfb = new LocalEntityManagerFactoryBean();
		emfb.setPersistenceUnitName("building-unit");
		return emfb;
	}
	
	/**
	 * @EnableTransactionManagement 启动了事务管理功能
	 * 应该提供一个PlatformTransactionManager默认实现
	 * 由LocalContainerEntityManagerFactoryBean构造出jpa的事务管理器
	 * 
	 * @return
	 */
	@Bean
	public PlatformTransactionManager jpaTransactionManager() {
		return new JpaTransactionManager(entityManagerFactory().getObject());
	}

	/**
	 * 使用hibernate的异常实现
	 * @return
	 */
	@Bean
	public PersistenceExceptionTranslator persistenceExceptionTranslator() {
		return new HibernateExceptionTranslator();
	}
}
