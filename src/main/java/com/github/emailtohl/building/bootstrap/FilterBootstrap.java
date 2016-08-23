package com.github.emailtohl.building.bootstrap;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.WebApplicationInitializer;

import com.github.emailtohl.building.filter.CompressionFilter;
import com.github.emailtohl.building.filter.PostSecurityLoggingFilter;
/**
 * 根据先后顺序注册过滤器
 * @author HeLei
 */
@Order(3)
public class FilterBootstrap implements WebApplicationInitializer {
	private static final Logger log = LogManager.getLogger();

	@Override
	public void onStartup(ServletContext container) throws ServletException {
		log.info("Executing Filters bootstrap.");
		/*
		 * 记录用户身份的过滤器
		 */
		FilterRegistration.Dynamic registration = container.addFilter("postSecurityLoggingFilter", new PostSecurityLoggingFilter());
		registration.addMappingForUrlPatterns(null, false, "/*");
		/*
		 * 压缩文件的过滤器
		 */
		registration = container.addFilter("compressionFilter", new CompressionFilter());
		registration.addMappingForUrlPatterns(null, false, "/*");
		/*
		 * 访问JPA实体懒加载的属性
		 */
		registration = container.addFilter("openEntityManagerInViewFilter", new OpenEntityManagerInViewFilter());
		registration.addMappingForUrlPatterns(null, false, "/*");
		
	}
}
