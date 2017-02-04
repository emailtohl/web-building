package com.github.emailtohl.building.common.jpa;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.springframework.web.context.annotation.ApplicationScope;

/**
 * Java EE环境中持久化上下文生产者
 * 
 * 在Java EE环境中，只需用@Inject注解将持久化上下文（EntityManager）注入即可
 * 
 * @author HeLei
 * @date 2017.02.04
 */
@ApplicationScope// 在应用上下文中，仅需要一个生产者
public class EntityMangerProducer {
	
	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;
	
	/**
	 * 在每个请求作用域中产生一个持久化上下文
	 * @return
	 */
	@Produces
	@RequestScoped
	public EntityManager create() {
		return entityManagerFactory.createEntityManager();
	}
	
	/**
	 * 关闭持久化上下文
	 * @param entityManager
	 */
	public void dispose(@Disposes EntityManager entityManager) {
		if (entityManager.isOpen())
			entityManager.close();
	}
}
