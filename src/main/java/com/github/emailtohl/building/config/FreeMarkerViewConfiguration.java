package com.github.emailtohl.building.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.github.emailtohl.building.config.FreeMarkerViewConfiguration.FreeMarkerController;

/**
 * 让Spring支持FreeMarker作为视图
 * @author HeLei
 * @date 2017.02.16
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.github.emailtohl.building.site.controller",
includeFilters = @ComponentScan.Filter(FreeMarkerController.class))
public class FreeMarkerViewConfiguration extends WebMvcConfigurerAdapter {
	
	@Bean
	public ViewResolver viewResolver() {
		// InternalResourceViewResolver将视图解析为Web应用的内部资源，一般是JSP
		FreeMarkerViewResolver  resolver = new FreeMarkerViewResolver();
		resolver.setViewClass(FreeMarkerView.class);
		resolver.setPrefix("/template");
		resolver.setSuffix(".ftl");
		return resolver;
	}
	
	/**
	 * 让DispatcherServlet将静态资源转发到Servlet容器中默认的Servlet上
	 */
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	/**
	 * 用于FreeMarker控制器的注解
	 * @author HeLei
	 * @date 2017.02.16
	 */
	@Target(value = { ElementType.TYPE })
	@Retention(value = RetentionPolicy.RUNTIME)
	@Documented
	@Controller
	public static @interface FreeMarkerController {
		String value() default "";
	}
}
