package com.github.emailtohl.building.bootspring;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
/**
 * 数据源测试配置，没有包含JNDI
 * @author Helei
 */
@Configuration
@PropertySource({ "classpath:database.properties", "classpath:config.properties" })
public class DataSourceConfigurationForTest {
	private static final Logger logger = LogManager.getLogger();
	/**
	 * 将@PropertySource中引入的属性封装到Environment
	 */
	@Inject
	Environment env;

	/**
	 * 若要使用@Value直接将值注入Bean中，除了在@Value中使用SpEl表达式外，
	 * 还需要为spring配置PropertySourcesPlaceholderConfigurer 要么在xml中添加
	 * <context:property-placeholder />
	 * 要么在java编程式的配置PropertySourcesPlaceholderConfigurer
	 */
	@Value("${local.driverClassName}")
	String driverClassName;
	@Value("${local.url}")
	String url;
	@Value("${local.username}")
	String username;
	@Value("${local.password}")
	String password;

	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public DataSource springTestDataSource() {
		logger.info(env.getProperty("local.driverClassName"));
		logger.info(env.getProperty("local.url"));
		logger.info(env.getProperty("local.username"));
		logger.info(env.getProperty("local.password"));
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}
	
}