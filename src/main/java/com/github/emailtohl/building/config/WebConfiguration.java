package com.github.emailtohl.building.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.stereotype.Controller;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
/**
 * spring mvc的配置
 * @author HeLei
 * @date 2017.02.04
 */
@Configuration
@EnableWebMvc
// 启动对spring data的支持，使用其分页排序的功能
// It also registers the PageableHandlerMethodArgumentResolver and SortHandlerMethodArgumentResolver beans, 
// enabling conversion of Pageables and Sorts from request parameters
@EnableSpringDataWebSupport
@ComponentScan(basePackages = "com.github.emailtohl.building.site.controller", useDefaultFilters = false, includeFilters = @ComponentScan.Filter(Controller.class))
@Import({ WebsocketConfiguration.class })
public class WebConfiguration extends WebMvcConfigurerAdapter {
	/**
	 * RootContextConfiguration中定义的LocalValidatorFactoryBean继承了SpringValidatorAdapter
	 */
	@Inject
	SpringValidatorAdapter validator;
	
	/**
	 * ViewResolver根据模板名返回一个View接口，该接口的render(model, request, response)
	 * 方法就是接受数据模型及Servlet的request和response对象，并结结合生成视图输出。
	 * 
	 * @return
	 */
	@Bean
	public ViewResolver viewResolver() {
		// InternalResourceViewResolver将视图解析为Web应用的内部资源，一般是JSP
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setViewClass(JstlView.class);
		resolver.setPrefix("/WEB-INF/jsp/");
		resolver.setSuffix(".jsp");
//		resolver.setExposeContextBeansAsAttributes(true);
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
	 * 接收文件上传功能
	 * @return
	 */
	@Bean
	public StandardServletMultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}
	
	@Bean
	public RequestToViewNameTranslator viewNameTranslator() {
		return new DefaultRequestToViewNameTranslator();
	}
	
	/**
	 * WebMvcConfigurerAdapter中的getValidator()返回的值是null所以spring mvc不会对表单进行校验
	 * 现在覆盖该方法，告诉spring mvc校验器，这样就会对提交的表单进行校验了
	 * @return 这里用全名是为了区分在RootContextConfiguration中使用的javax.validation.Validator
	 * 它支持为期望的@Valid参数方法提供Errors参数
	 */
	@Override
	public org.springframework.validation.Validator getValidator() {
		return this.validator;
	}
	
}