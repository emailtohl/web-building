package com.github.emailtohl.building.site.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
/**
 * 测试Spring AOP功能
 * @author HeLei
 * @date 2017.02.04
 */
@Aspect
@Component
public class PerformanceMeasure {
	private static final Logger logger = LogManager.getLogger();
	
	@Around("execution(* com.github.emailtohl.building.site.service.*.*(..))")
	public Object usedTime(ProceedingJoinPoint jp) throws Throwable {
		long start = System.currentTimeMillis();
		Object res = jp.proceed();
		long end = System.currentTimeMillis();
		long interval = end - start;
		logger.trace(jp.getSignature() + " : " + interval + " 毫秒");
		return res;
	}
}
