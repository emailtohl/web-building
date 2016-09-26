package com.github.emailtohl.building.bootstrap;
import javax.servlet.FilterRegistration;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_PRODUCTION;
import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_QA;
import com.github.emailtohl.building.config.MvcConfiguration;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.filter.PreSecurityLoggingFilter;
import com.github.emailtohl.building.listener.SessionListener;
/**
 * 初始化容器时最先启动的类，它将完成如下工作：
 * （1）激活容器默认的servlet来响应静态资源；
 * （2）启动spring的容器
 * （3）注册springmvc
 * （4）其他监听器和过滤器
 * 
 * @author HeLei
 */
@Order(1)
public class ContainerBootstrap implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext container) throws ServletException {
		/* 默认的Servlet可以处理静态资源 */
		container.getServletRegistration("default").addMapping("/app/*", "/common/*", "/download/*", "/lib/*",
				"/upload/*", "*.css", "*.js", "*.png", "*.gif", "*.jpg");

		/* 配置Spring根应用上下文 */
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		/* 载入配置 */
		rootContext.getEnvironment().setActiveProfiles(PROFILE_PRODUCTION, PROFILE_QA);// 激活spring配置中的profile
		rootContext.register(RootContextConfiguration.class);
		container.addListener(new ContextLoaderListener(rootContext));

		/*
		 * 配置SpringMvc org.springframework.web.context.ContextLoaderListener.
		 * ContextLoaderListener 可以自动将根应用上下文设置为DispatcherServlet的父上下文
		 */
		AnnotationConfigWebApplicationContext springServletContext = new AnnotationConfigWebApplicationContext();
		/* 载入配置 */
		springServletContext.register(MvcConfiguration.class);
		ServletRegistration.Dynamic dispatcher = container.addServlet("springDispatcher",
				new DispatcherServlet(springServletContext));
		dispatcher.setLoadOnStartup(1);
		/* 可以上传文件 */
		dispatcher.setMultipartConfig(new MultipartConfigElement(null, 20_971_520L, 41_943_040L, 512_000));
		dispatcher.addMapping("/");
		// 另一种激活spring配置中的profile的方式
//		dispatcher.setInitParameter("spring.profiles.active", PROFILE_PRODUCTION);
//		container.setInitParameter("spring.profiles.active", PROFILE_PRODUCTION);

		/* 在Servlet容器中注册监听器 */
		container.addListener(SessionListener.class);
		
		/* 在Servlet容器中注册过滤器 */
		FilterRegistration.Dynamic registration = container.addFilter("characterEncodingFilter", new CharacterEncodingFilter());
		/* 第一个参数为null：响应默认的DispatcherType.REQUEST
		         第二个参数为false，表明该filter将在web.xml中配置的任何filter之前
		         第三个参数表明将响应所有地址 */
		registration.addMappingForUrlPatterns(null, false, "/*");
		registration = container.addFilter("loggingFilter", new PreSecurityLoggingFilter());
		registration.addMappingForUrlPatterns(null, false, "/*");
	}

}
