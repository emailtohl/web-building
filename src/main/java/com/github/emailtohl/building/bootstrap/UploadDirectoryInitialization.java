package com.github.emailtohl.building.bootstrap;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.github.emailtohl.building.common.utils.Uploader;

/**
 * 用户上传的内容要保存在项目目录之外
 * @author HeLei
 */
@Order(4)
public class UploadDirectoryInitialization implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
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
		// 获取自动装配工厂
		AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
		// 将本SessionListener纳入Spring的管理下
		// 事实上此时spring已经初始化完，若再添加Bean进入，且该Bean有依赖，Spring仍然可以将其依赖注入进去
		factory.autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		factory.initializeBean(new Uploader(uploadBase), "uploader");
	}
	
}
