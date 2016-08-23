package com.github.emailtohl.building.config;

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
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
/**
 * 数据源配置
 * @author HeLei
 */
@Configuration
@PropertySource({ "classpath:database.properties", "classpath:config.properties" })
public class DataSourceConfiguration {
	private static final Logger logger = LogManager.getLogger();
	/**
	 * 将@PropertySource中引入的属性封装到Environment
	 */
	@Inject
	Environment env;

	/**
	 * 若要使用@Value直接将值注入Bean中，除了在@Value中使用SpEl表达式外，
	 * 还需要为spring配置PropertySourcesPlaceholderConfigurer对应xml中的配置是
	 * <context:property-placeholder />
	 */
	@Value("${local.driverClassName}")
	String driverClassName;
	@Value("${local.url}")
	String url;
	@Value("${local.username}")
	String username;
	@Value("${local.password}")
	String password;

	/**
	 * 静态配置方法，该方法将在最早执行，这样才能读取properties配置
	 * @return
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	/**
	 * spring 提供的用于测试的简单数据源
	 * @return
	 */
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
	
	/**
	 * 获取容器提供的数据源
	 * @return
	 */
	@Bean
	public DataSource jndiDataSource() {
		JndiDataSourceLookup lookup = new JndiDataSourceLookup();
		return lookup.getDataSource("jdbc/building");
	}

}