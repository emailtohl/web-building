package com.github.emailtohl.building.common.jpa;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 管理JPA资源的基础仓库，包括：
 * 实体管理器工厂（EntityManagerFactory）、实体管理器（EntityManager） 、
 * id的class以及实体class的管理和初始化。
 * @author HeLei
 * @date 2016.09.08
 */
public abstract class AbstractJpaRepository<I extends Serializable, E extends Serializable> {
	private static final Logger logger = LogManager.getLogger();
	protected final Class<I> idClass;
	protected final Class<E> entityClass;
	@PersistenceUnit
	protected EntityManagerFactory entityManagerFactory;
	@PersistenceContext
	protected EntityManager entityManager;
	
	@SuppressWarnings("unchecked")
	protected AbstractJpaRepository() {
		super();
		Class<? extends Serializable>[] classes = new Class[2];
		Class<? extends Serializable> tempClass = null;
		Class<?> clz = this.getClass();
		while (clz != AbstractJpaRepository.class) {
			Type genericSuperclass = clz.getGenericSuperclass();
			if (genericSuperclass instanceof ParameterizedType) {
				ParameterizedType type = (ParameterizedType) genericSuperclass;
				Type[] arguments = type.getActualTypeArguments();
				if (arguments == null) {
					continue;
				}
				if (arguments.length == 1 && arguments[0] instanceof Class) {// 若继承层次上分开声明参数类型时arguments.length就为1
					tempClass = (Class<? extends Serializable>) arguments[0];
				} else if (arguments.length == 2) {// 只有当参数类型有两个时，才能确定idClass和entityClass分别是哪个
					if (arguments[0] instanceof Class) {
						classes[0] = (Class<? extends Serializable>) arguments[0];
					}
					if (arguments[1] instanceof Class) {
						classes[1] = (Class<? extends Serializable>) arguments[1];
					}
				}
				if (classes[0] != null && classes[1] == null) {
					classes[1] = tempClass;
				} else if (classes[0] == null && classes[1] != null) {
					classes[0] = tempClass;
				}
				if (classes[0] != null && classes[1] != null) {
					break;
				}
			}
			clz = clz.getSuperclass();
		}
		idClass = (Class<I>) classes[0];
		entityClass = (Class<E>) classes[1];
		if (idClass == null) {
			logger.debug("初始化： " + this.getClass() + " 时，idClass == null");
			throw new IllegalStateException("初始化： " + this.getClass() + " 时，idClass == null");
		}
		if (entityClass == null) {
			logger.debug("初始化： " + this.getClass() + " 时，entityClass == null");
			throw new IllegalStateException("初始化： " + this.getClass() + " 时，entityClass == null");
		}
		logger.debug(idClass);
		logger.debug(entityClass);
	}

	/**
	 * 若通过泛型分析ID和实体的class失败，可以使用构造方法传入
	 * @param idClass
	 * @param entityClass
	 */
	public AbstractJpaRepository(Class<I> idClass, Class<E> entityClass) {
		super();
		this.idClass = idClass;
		this.entityClass = entityClass;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	/**
	 * 若实体管理器或实体管理工厂不是由Spring注入，则提供接口让应用程序写入EntityManagerFactory
	 * @param entityManagerFactory
	 */
	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	/**
	 * 让外部使用EntityManager接口，进行增删改查操作
	 * @return
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

}
