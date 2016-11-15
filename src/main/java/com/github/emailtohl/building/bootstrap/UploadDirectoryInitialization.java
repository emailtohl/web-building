package com.github.emailtohl.building.bootstrap;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

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
		String uploadBase = env.getProperty("uploadBase");
		File uploadBaseDir;
		if (uploadBase == null || uploadBase.isEmpty()) {
			File p = new File(servletContext.getRealPath("")).getParentFile();
			uploadBaseDir = new File(p, "web-building-upload");
		} else {
			uploadBaseDir = new File(uploadBase);
		}
		if (!uploadBaseDir.exists()) {
			uploadBaseDir.mkdir();
		}
	}

}
