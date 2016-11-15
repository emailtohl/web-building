package com.github.emailtohl.building.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 在Spring中可以通过WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);获取ServletContext
 * 在这里将ServletContext容器注入到Spring容器中，在应用中可以直接访问ServletContext
 * Application Lifecycle Listener implementation class ContextRegistration
 *
 */
//@WebListener
public class ContextRegistration implements ServletContextListener {
	private final static Logger log = LogManager.getLogger();
	ServletContext servletContext;
	AutowireCapableBeanFactory factory;
    /**
     * Default constructor. 
     */
    public ContextRegistration() {
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  {
    	factory.destroyBean(servletContext);
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
    	servletContext = sce.getServletContext();
    	WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
    	factory = context.getAutowireCapableBeanFactory();
		// 将本SessionListener纳入Spring的管理下
		// 事实上此时spring已经初始化完，若再添加Bean进入，且该Bean有依赖，Spring仍然可以将其依赖注入进去
		factory.autowireBean(servletContext);
		factory.initializeBean(servletContext, "servletContext");
		log.info("ServletContext initialized in Spring application context.");
    }
	
}
