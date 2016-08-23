package com.github.emailtohl.building.common.repository.generic;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * 通用数据仓库
 * @author HeLei
 *
 * @param <I> ID
 * @param <E> 实体类
 */
public abstract class GenericBaseRepository<I extends Serializable, E extends Serializable>
		implements GenericRepository<I, E> {
	private static final Logger logger = LogManager.getLogger();
	protected final Class<I> idClass;
	protected final Class<E> entityClass;

	/**
	 * 通过传入的泛型获取ID和实体的类
	 */
	@SuppressWarnings("unchecked")
	protected GenericBaseRepository() {
		super();
		Class<? extends Serializable>[] classes = new Class[2];
		Class<? extends Serializable> tempClass = null;
		Class<?> clz = this.getClass();
		while (clz != GenericBaseRepository.class) {
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
	protected GenericBaseRepository(Class<I> idClass, Class<E> entityClass) {
		super();
		this.idClass = idClass;
		this.entityClass = entityClass;
		if (idClass == null) {
			logger.debug("初始化： " + this.getClass() + " 时，idClass == null");
			throw new IllegalStateException("初始化： " + this.getClass() + " 时，idClass == null");
		}
		if (entityClass == null) {
			logger.debug("初始化： " + this.getClass() + " 时，entityClass == null");
			throw new IllegalStateException("初始化： " + this.getClass() + " 时，entityClass == null");
		}
	}

	@Override
	public List<E> entities() {
		Iterable<E> iterable = getAll();
		List<E> l = new ArrayList<E>();
		if (iterable == null) {
			return l;
		}
		Iterator<E> i = iterable.iterator();
		while (i.hasNext()) {
			l.add(i.next());
		}
		return l;
	}

}