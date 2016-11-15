package com.github.emailtohl.building.listener;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.github.emailtohl.building.common.utils.Uploader;

/**
 * 创建上传的目录
 * 
 * Application Lifecycle Listener implementation class UploadDirectoryInitialization
 *
 */
//@WebListener
public class UploadDirectoryInitialization implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public UploadDirectoryInitialization() {
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  {
    	ServletContext servletContext = sce.getServletContext();
    	// 从servlet context中获取spring context
		WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		// 获取自动装配工厂
		Environment env = context.getEnvironment();
		String uploadBaseStr = env.getProperty("uploadBase");
		File uploadBase;
		if (uploadBaseStr == null || uploadBaseStr.isEmpty()) {
			File p = new File(servletContext.getRealPath("")).getParentFile();
			uploadBase = new File(p, "web-building-upload");
		} else {
			uploadBase = new File(uploadBaseStr);
		}
		if (!uploadBase.exists()) {
			uploadBase.mkdir();
		}
		AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
		Uploader u = new Uploader(uploadBase);
		factory.autowireBean(u);
		factory.initializeBean(u, "uploader");
	}
	
}
