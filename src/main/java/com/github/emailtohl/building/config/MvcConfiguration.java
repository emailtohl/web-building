package com.github.emailtohl.building.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.stereotype.Controller;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
/**
 * spring mvc的配置
 * @author HeLei
 */
@Configuration
@EnableWebMvc
// 启动对spring data的支持，使用其分页排序的功能
// It also registers the PageableHandlerMethodArgumentResolver and SortHandlerMethodArgumentResolver beans, 
// enabling conversion of Pageables and Sorts from request parameters
@EnableSpringDataWebSupport
@ComponentScan(basePackages = "com.github.emailtohl.building.site.controller", useDefaultFilters = false, includeFilters = @ComponentScan.Filter(Controller.class))
public class MvcConfiguration extends WebMvcConfigurerAdapter {

	/**
	 * RootContextConfiguration中定义的LocalValidatorFactoryBean继承了SpringValidatorAdapter
	 */
	@Inject SpringValidatorAdapter validator;
	
	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setViewClass(JstlView.class);
		resolver.setPrefix("/WEB-INF/jsp/");
		resolver.setSuffix(".jsp");
		return resolver;
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