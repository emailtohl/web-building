package com.github.emailtohl.building.config;
import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_DEVELPMENT;
import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_PRODUCTION;
import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_QA;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA的配置
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
@Import({ DataSourceConfiguration.class })
public class JPAConfiguration {

	@Inject
	@Named("dataSource")
	DataSource dataSource;
	
	/**
	 * 以Hibernate作为JPA的实现类
	 * @return
	 */
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.POSTGRESQL);
		adapter.setShowSql(true);
		adapter.setGenerateDdl(false);
		adapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQL94Dialect");
		return adapter;
	}

	/**
	 * 脱离容器环境下使用
	 * @return
	 */
	@Profile({ PROFILE_DEVELPMENT, PROFILE_QA })
	@Bean(name = "entityManagerFactory")
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
	@Profile({ PROFILE_DEVELPMENT, PROFILE_QA })
	@Bean(name = "jpaTransactionManager")
	@Conditional(value = Develpment_QA_ProfileCondition.class)
	public PlatformTransactionManager development_jpaTransactionManager() {
		return new JpaTransactionManager(entityManagerFactory().getObject());
	}
	
	/**
	 * 使用容器提供的实体管理工厂，这样可以不用使用META-INFO/persistence.xml配置
	 * @return
	 */
	@Profile(PROFILE_PRODUCTION)
	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean containerEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
		emfb.setDataSource(dataSource);
		emfb.setJpaVendorAdapter(jpaVendorAdapter());
		// 实际上hibernate可以扫描类路径下有JPA注解的实体类，但是JPA规范并没有此功能，所以最好还是告诉它实际所在位置
		emfb.setPackagesToScan("com.github.emailtohl.building.site.entities");
		return emfb;
	}

	@Profile(PROFILE_PRODUCTION)
	@Bean(name = "jpaTransactionManager")
	@Conditional(value = ProductionProfileCondition.class)
	public PlatformTransactionManager product_jpaTransactionManager() {
		return new JpaTransactionManager(containerEntityManagerFactory().getObject());
	}

	/**
	 * 使用hibernate的异常实现
	 * @return
	 */
	@Bean
	public PersistenceExceptionTranslator persistenceExceptionTranslator() {
		return new HibernateExceptionTranslator();
	}
	
	/**
	 * From the ConditionContext, you can do the following:
	 * Check for bean definitions via the BeanDefinitionRegistry returned from getRegistry(). 
	 * Check for the presence of beans, and even dig into bean properties via the ConfigurableListableBeanFactory returned from getBeanFactory(). 
	 * Check for the presence and values of environment variables via the Environment retrieved from getEnvironment(). 
	 * Read and inspect the contents of resources loaded via the ResourceLoader returned from getResourceLoader().
	 * Load and check for the presence of classes via the ClassLoader returned from getClassLoader().
	 * 
	 * As for the AnnotatedTypeMetadata, it offers you a chance to inspect annotations that may also be placed on the @Bean method.
	 * Like ConditionContext, Annotated- TypeMetadata is an interface.
	 * 
	 */
	public static class Develpment_QA_ProfileCondition implements Condition {
		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			String[] profiles = context.getEnvironment().getActiveProfiles();
			for (int i = 0; i < profiles.length; i++) {
				if (PROFILE_QA.equalsIgnoreCase(profiles[i]) || PROFILE_DEVELPMENT.equalsIgnoreCase(profiles[i])) {
					return true;
				}
			}
			return false;
		}
	}
	
	public static class ProductionProfileCondition implements Condition {
		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			String[] profiles = context.getEnvironment().getActiveProfiles();
			for (int i = 0; i < profiles.length; i++) {
				if (PROFILE_PRODUCTION.equalsIgnoreCase(profiles[i])) {
					return true;
				}
			}
			return false;
		}
	}
	
}
