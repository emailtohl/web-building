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
	protected static final Logger LOG = LogManager.getLogger();
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
			LOG.debug("初始化： " + this.getClass() + " 时，idClass == null");
			throw new IllegalStateException("初始化： " + this.getClass() + " 时，idClass == null");
		}
		if (entityClass == null) {
			LOG.debug("初始化： " + this.getClass() + " 时，entityClass == null");
			throw new IllegalStateException("初始化： " + this.getClass() + " 时，entityClass == null");
		}
		LOG.debug(idClass);
		LOG.debug(entityClass);
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
	
	/**
	 * 若在启动事务之前打开了持久化上下文（EntityManager），则需要将持久化上下文加入到事务中
	 */
	public void joinTransaction() {
		if (!entityManager.isJoinedToTransaction())
			entityManager.joinTransaction();
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Class<E> getEntityClass() {
		return entityClass;
	}

}
