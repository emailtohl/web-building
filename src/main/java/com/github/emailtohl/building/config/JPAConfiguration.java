package com.github.emailtohl.building.config;
import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_DEVELPMENT;
import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_PRODUCTION;
import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_QA;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.hibernate.dialect.PostgreSQL9Dialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * JPA的配置
 * @author HeLei
 *
 */
@Configuration
// 这是SpringData的注解，启动后，它将扫描指定包中继承了Repository（实际业务代码中的接口是间接继承它）的接口，并为其提供代理
// repositoryImplementationPostfix = "Impl" 扫描实现类的名字，若该类的名字为接口名+"Impl"，则认为该实现类将提供SpringData以外的功能
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
		adapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQL94Dialect");
//		adapter.setShowSql(true);
		adapter.setGenerateDdl(false);
		return adapter;
	}

	/**
	 * 单元测试时，使用META-INFO/persistence.xml中的配置
	 * @return
	 */
	@Profile(PROFILE_DEVELPMENT)
	@Bean(name = "entityManagerFactory")
	public LocalEntityManagerFactoryBean LocalEntityManagerFactory() {
		LocalEntityManagerFactoryBean emfb = new LocalEntityManagerFactoryBean();
		emfb.setPersistenceUnitName("building-unit");
		emfb.setJpaPropertyMap(getJpaPropertyMap());
		return emfb;
	}
	
	/**
	 * 生产环境中，使用容器提供的实体管理工厂，这样可以不用使用META-INFO/persistence.xml配置
	 * @return
	 */
	@Profile({ PROFILE_PRODUCTION, PROFILE_QA })
	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean containerEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
		emfb.setDataSource(dataSource);
		emfb.setJpaVendorAdapter(jpaVendorAdapter());
		// 实际上hibernate可以扫描类路径下有JPA注解的实体类，但是JPA规范并没有此功能，所以最好还是告诉它实际所在位置
		emfb.setPackagesToScan("com.github.emailtohl.building.site.entities");
		emfb.setJpaPropertyMap(getJpaPropertyMap());
		return emfb;
	}
	
	private Map<String, Object> getJpaPropertyMap() {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("javax.persistence.schema-generation.database.action", "none");
		properties.put("hibernate.search.default.directory_provider", "filesystem");
		properties.put("hibernate.search.default.indexBase", "../searchIndexes");
		return properties;
	}
	
	/**
	 * @EnableTransactionManagement 启动了事务管理功能
	 * 应该提供一个PlatformTransactionManager默认实现
	 * 由LocalContainerEntityManagerFactoryBean构造出jpa的事务管理器
	 * 
	 * 编译期，由于无法判断是开发环境还是生产环境，所以需要在@Conditional作进一步判断
	 * 
	 * @return
	 */
	@Profile(PROFILE_DEVELPMENT)
	@Bean(name = "jpaTransactionManager")
	public PlatformTransactionManager development_jpaTransactionManager() {
		return new JpaTransactionManager(LocalEntityManagerFactory().getObject());
	}
	
	@Profile({ PROFILE_PRODUCTION, PROFILE_QA })
	@Bean(name = "jpaTransactionManager")
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
	 * 获取LocalSessionFactoryBuilder，由此可获取Hibernate的SessionFactory
	 * 这里没有直接向spring注册Hibernate的SessionFactory，是因为会影响entityManagerFactory单例
	 * @return
	 */
	@Bean
	public LocalSessionFactoryBuilder sessionFactory() {
		LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(dataSource);
		builder.scanPackages("com.github.emailtohl.building.site.entities");
		builder.setProperty("hibernate.dialect", PostgreSQL9Dialect.class.getCanonicalName());
		builder.setProperty("hibernate.hbm2ddl.auto", "update");
		builder.setProperty("hibernate.search.default.directory_provider", "filesystem");
		builder.setProperty("hibernate.search.default.indexBase", "../searchIndexes");
		return builder;
	}
	
}
