package com.github.emailtohl.building.listener;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 注释掉@WebListener后，通过编程方式注册本监听，如此可保证Spring容器初始化在前
 * 配置一个监听HttpSession变化的Listener，并且纳入Spring管理下
 * 如此可通过Spring容器获取到本监听器，并获取session管理下的对象
 * @author HeLei
 */
//@WebListener
public class SessionListener implements HttpSessionListener, HttpSessionIdListener, ServletContextListener {
	private final static Logger log = LogManager.getLogger();
	@Inject SessionRegistry sessionRegistry;

	/**
	 * 初始化本监听器时，先获取Spring容器，然后用编程方式将本实例作为Bean添加到Spring中
	 * Spring容器会注入其所有依赖例如本类中的SessionRegistry
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// 从servlet context中获取spring context
		WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext());
		// 获取自动装配工厂
		AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
		// 将本SessionListener纳入Spring的管理下
		// 事实上此时spring已经初始化完，若再添加Bean进入，且该Bean有依赖，Spring仍然可以将其依赖注入进去
		factory.autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		factory.initializeBean(this, "sessionListener");
		log.info("Session listener initialized in Spring application context.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
	
	/**
     * @see HttpSessionIdListener#sessionIdChanged(HttpSessionEvent, String)
     * 监听用户的sessionID变化，可以验证用户登录后，sessionID是否有改变
     */
    public void sessionIdChanged(HttpSessionEvent e, String oldSessionId)  { 
    	sessionRegistry.updateSessionId(e.getSession(), oldSessionId);
    	log.debug("session id由： " + oldSessionId + " 改变为： " + e.getSession().getId());
    }

	/**
     * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent e)  { 
    	sessionRegistry.addSession(e.getSession());
    	log.debug("新增session，id ： " + e.getSession().getId());
    }

	/**
     * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent e)  { 
    	sessionRegistry.removeSession(e.getSession());
    	log.debug("session id： " + e.getSession().getId() + " 失效");
    }
	
}
