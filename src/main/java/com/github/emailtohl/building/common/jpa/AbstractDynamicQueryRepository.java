package com.github.emailtohl.building.common.jpa;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.Timestamp;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JPA的实体管理器entityManager已经提供了简便的增、删、改功能，所以很容易封装，这里主要提供自定义的动态查询解决方案
 * 应用程序既可以直接继承以Spring data的提供的JpaRepository作为BaseRepository，也可以自定义BaseRepository
 * 本方案基于JPQL，提供分页查询功能
 * @param <E> 实体类，ID统一为Long型
 * @author HeLei
 * @date 2016.09.08
 */
public abstract class AbstractDynamicQueryRepository<E extends Serializable> extends AbstractJpaRepository<Long, E> implements DynamicQueryRepository<E> {
	private static final Logger logger = LogManager.getLogger();
	/**
	 * 匹配JPQL的正则式
	 */
	protected final Pattern jpqlPattern = Pattern.compile(
			"(SELECT\\s+(DISTINCT\\s+)?((\\w+(\\.\\w+)*)|(\\s*NEW\\s+.+(?=\\sFROM)))\\s+)?((FROM\\s+)(\\w+)((\\s+AS\\s+)|(\\s+))(\\w+)(\\s*,\\s*\\w+\\s+\\w+)*((\\s+JOIN\\s+\\w+\\.\\w+\\s+\\w+)|(\\s*,\\s*IN\\s*\\(\\s*\\w+\\.\\w+\\s*\\)\\s*\\w+))*((\\s+WHERE\\s+.+)|(\\s+ORDER\\s+.+)|(\\s+GROUP\\s+.+))?)",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	protected final short fromIndex = 7;
	protected final short selectIndex = 4;
	protected final short distinctIndex = 2;
	protected final short aliasIndex = 13;
	protected final short predicateIndex = 19;
	protected final short entityNameIndex = 9;
	
	/**
	 * 查询字符串时，是否用LIKE模糊查询
	 */
	private boolean isFuzzy = true;

	protected AbstractDynamicQueryRepository() {
		super();
	}

	protected AbstractDynamicQueryRepository(Class<E> entityClass) {
		super(Long.class, entityClass);
	}

	public void setFuzzy(boolean isFuzzy) {
		this.isFuzzy = isFuzzy;
	}

	/**
	 * 得到一个Pager对象，包含最大页码，数据List等信息
	 * 参数是数组
	 */
	@Override
	public Pager<E> getPager(String jpql, Object[] args, Integer pageNumber, Integer pageSize) {
		Matcher m;
		if (jpql == null || !(m = jpqlPattern.matcher(jpql)).find())
			throw new IllegalArgumentException("JPQL可能是null，或者格式可能不对，也可能是正则表达式编写不对");
		// 从第0页开始
		if (pageNumber == null || pageNumber < 0L)
			pageNumber = 0;
		if (pageSize == null || pageSize < 1)
			pageSize = 20;// 默认每页20条记录
		String selectAlias, alias, from, distinct;
		selectAlias = m.group(selectIndex);
		distinct = m.group(distinctIndex) == null ? "" : m.group(distinctIndex);
		if (selectAlias != null) {
			alias = selectAlias.trim().split("\\.")[0];// 考虑到select是查询的某实体的属性，所以取第一个点号前的字符
		} else {
			alias = m.group(aliasIndex).trim();
		}
		from = m.group(fromIndex);
		logger.debug(
				"count: \n" + "SELECT COUNT(" + distinct + " " + alias + ") "  + from + "\n" + "Arguments: \n" + args);
		TypedQuery<Long> countQuery = entityManager.createQuery("SELECT COUNT(" + distinct + " " + alias + ") " + from,
				idClass);
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				countQuery.setParameter(i + 1, args[i]);
			}
		}
		Long totalElements = countQuery.getSingleResult();
		TypedQuery<E> pagedQuery = entityManager.createQuery(jpql, entityClass);
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				pagedQuery.setParameter(i + 1, args[i]);
			}
		}
//		 这是从第1页起的计算方式
//		Integer startPosition = (pageNumber - 1) * pageSize;
//		 这是从第0页起的计算方式
		Integer startPosition = pageNumber * pageSize;
		pagedQuery.setFirstResult(startPosition.intValue());
		pagedQuery.setMaxResults(pageSize);
		logger.debug("SELECT Query: \n" + jpql + "\n" + "Arguments: \n" + Arrays.toString(args) + "\n"
				+ "firstResult: \n" + startPosition + "\n" + "maxResults: \n" + pageSize);
		List<E> singlePage = pagedQuery.getResultList();
		Pager<E> p = new Pager<E>(singlePage, totalElements, pageNumber, pageSize);
		p.setPageNumber(pageNumber);
		return p;
	}

	/**
	 * 得到一个Pager对象，包含最大页码，数据List等信息
	 * 参数是Map
	 */
	@Override
	public Pager<E> getPager(String jpql, Map<String, Object> args, Integer pageNumber, Integer pageSize) {
		Matcher m;
		if (jpql == null || !(m = jpqlPattern.matcher(jpql)).find())
			throw new IllegalArgumentException("JPQL可能是null，或者格式可能不对，也可能是正则表达式编写不对");
		// 从第0页开始
		if (pageNumber == null || pageNumber < 0L)
			pageNumber = 1;
		if (pageSize == null || pageSize < 1)
			pageSize = 20;// 默认每页20条记录
		String selectAlias, alias, from, distinct;
		selectAlias = m.group(selectIndex);
		distinct = m.group(distinctIndex) == null ? "" : m.group(distinctIndex);
		if (selectAlias != null) {
			alias = selectAlias.trim().split("\\.")[0];// 考虑到select是查询的某实体的属性，所以取第一个点号前的字符
		} else {
			alias = m.group(aliasIndex).trim();
		}
		from = m.group(fromIndex);
		logger.debug(
				"count: \n" + "SELECT COUNT(" + distinct + " " + alias + ") "  + from + "\n" + "Arguments: \n" + args);
		TypedQuery<Long> countQuery = entityManager.createQuery("SELECT COUNT(" + distinct + " " + alias + ") " + from,
				idClass);
		if (args != null) {
			for (Map.Entry<String, Object> entry : args.entrySet()) {
				countQuery.setParameter(entry.getKey(), entry.getValue());
			}
		}
		Long totalElements = countQuery.getSingleResult();
		TypedQuery<E> pagedQuery = entityManager.createQuery(jpql, entityClass);
		if (args != null) {
			for (Map.Entry<String, Object> entry : args.entrySet()) {
				pagedQuery.setParameter(entry.getKey(), entry.getValue());
			}
		}
//		 这是从第1页起的计算方式
//		Integer startPosition = (pageNumber - 1) * pageSize;
//		 这是从第0页起的计算方式
		Integer startPosition = pageNumber * pageSize;
		pagedQuery.setMaxResults(pageSize);
		logger.debug("SELECT Query: \n" + jpql + "\n" + "Arguments: \n" + args + "\n" + "firstResult: \n"
				+ startPosition + "\n" + "maxResults: \n" + pageSize);
		List<E> singlePage = pagedQuery.getResultList();
		Pager<E> p = new Pager<E>(singlePage, totalElements, pageNumber, pageSize);
		p.setPageNumber(pageNumber);
		return p;
	}
	
	/**
	 * 得到一个Pager对象，包含最大页码，数据List等信息
	 * 参数是实体对象，程序会分析该实体对象哪些属性有值，然后生成一条查询的JPQL，如此实现动态查询
	 */
	@Override
	public Pager<E> getPager(E entity, Integer pageNum, Integer pageSize, AccessType type) {
		JpqlAndArgs jaa;
		if (AccessType.FIELD == type) {
			jaa = jpqlAndArgsByField(entity);
		} else {
			jaa = jpqlAndArgsByPropety(entity);
		}
		return getPager(jaa.jpql, jaa.args, pageNum, pageSize);
	}
	
	/**
	 * 对实体对象的JavaBean属性进行分析，获取到JPQL 注意，实体不能继承非实体的类，否则将会把非实体类中的属性分析出来
	 * 
	 * @param entity
	 * @return
	 */
	protected JpqlAndArgs jpqlAndArgsByPropety(final E entity) {
		final StringBuilder jpql = new StringBuilder();
		final List<Object> args = new ArrayList<Object>();
		final Set<Object> set = new HashSet<Object>();
		String entityName = getEntityName(entity);
		String alias = "_" + entityName.charAt(0);
		jpql.append("SELECT ").append(alias).append(" FROM ").append(entityName).append(" AS ").append(alias);
		/**
		 * 创建内部类，递归地使用创建它作用域中的数据
		 */
		class Predicate {
			boolean first = true;
			int position = 1;
			
			/**
			 * 从JavaBean属性描述器中获取注解
			 * @param descriptor
			 * @param annotationClass
			 * @return
			 */
			<A extends Annotation> A getAnnotation(PropertyDescriptor descriptor, Class<A> annotationClass) {
				Method read = descriptor.getReadMethod(), write = descriptor.getWriteMethod();
				A a = null;
				if (read != null) {
					a = read.getAnnotation(annotationClass);
				}
				if (a == null && write != null) {
					a = write.getAnnotation(annotationClass);
				}
				return a;
			}
			
			void predicate(Object o, String prefix) {
				Class<?> clz;
				// 如果是本实体继承树上的类，则只分析基类的属性
				if (entityClass.isAssignableFrom(o.getClass())) {
					clz = entityClass;
				} else {// 否则找到嵌入类或者其他实体类为止
					clz = o.getClass();
					while (clz != null && clz != Object.class) {
						Embeddable eb = clz.getAnnotation(Embeddable.class);
						Entity et = clz.getAnnotation(Entity.class);
						if (eb != null || et != null) {
							break;
						}
						clz = clz.getSuperclass();
					}
				}
				BeanInfo info;
				try {
					info = Introspector.getBeanInfo(clz, Object.class);
				} catch (IntrospectionException e) {
					e.printStackTrace();
					throw new IllegalArgumentException(e);
				}
				PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
				for (PropertyDescriptor descriptor : descriptors) {
					if (getAnnotation(descriptor, Transient.class) != null) {
						continue;
					}
					Object value = null;
					try {
						Method m = descriptor.getReadMethod();
						if (m == null) {
							continue;
						}
						value = m.invoke(o);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
					if (value == null) {
						continue;
					}
					if (availableObj(value)) {
						String name = descriptor.getName();
						if (first) {
							jpql.append(" WHERE ");
							first = false;
							if (value instanceof String && isFuzzy) {
								jpql.append(prefix).append('.').append(name).append(" LIKE ?").append(position);
							} else {
								jpql.append(prefix).append('.').append(name).append(" = ?").append(position);
							}
						} else {
							jpql.append(" AND ");
							if (value instanceof String && isFuzzy) {
								jpql.append(prefix).append('.').append(name).append(" LIKE ?").append(position);
							} else {
								jpql.append(prefix).append('.').append(name).append(" = ?").append(position);
							}
						}
						args.add(value);
						position++;
					} else {
						ManyToOne manyToOne = getAnnotation(descriptor, ManyToOne.class);
						OneToOne oneToOne =getAnnotation(descriptor, OneToOne.class);
						Embedded embedded = getAnnotation(descriptor, Embedded.class);
						if (manyToOne != null || oneToOne != null || embedded != null) {
							if (set.contains(o)) {// 若遇到相互关联的情况，则终止递归
								return;
							}
							set.add(o);
							String name = descriptor.getName();
							predicate(value, prefix + "." + name);
						}
					}

				}
			}
		}// END Inner class
		new Predicate().predicate(entity, alias);
		logger.debug("JPQL: \n" + jpql.toString() + "\n" + "Arguments: \n" + Arrays.toString(args.toArray()));
		return new JpqlAndArgs(jpql.toString(), args.toArray());
	}

	/**
	 * 对实体对象的Field进行分析，获取到JPQL
	 * 
	 * @param entity
	 * @return
	 */
	protected JpqlAndArgs jpqlAndArgsByField(final E entity) {
		final StringBuilder jpql = new StringBuilder();
		final List<Object> args = new ArrayList<Object>();
		final Set<Object> set = new HashSet<Object>();
		String entityName = getEntityName(entity);
		String alias = "_" + entityName.charAt(0);
		jpql.append("SELECT ").append(alias).append(" FROM ").append(entityName).append(" AS ").append(alias);
		/**
		 * 创建内部类，递归地使用创建它作用域中的数据
		 */
		class Predicate {
			boolean first = true;
			int position = 1;

			void predicate(Object o, String prefix) {
				Class<?> clz;
				// 如果是本实体继承树上的类，则只分析基类的属性
				if (entityClass.isAssignableFrom(o.getClass())) {
					clz = entityClass;
				} else {// 否则找到嵌入类或者其他实体类为止
					clz = o.getClass();
					while (clz != null && clz != Object.class) {
						Embeddable eb = clz.getAnnotation(Embeddable.class);
						Entity et = clz.getAnnotation(Entity.class);
						if (eb != null || et != null) {
							break;
						}
						clz = clz.getSuperclass();
					}
				}
				while (clz != null && clz != Object.class) {
					Field[] fields = clz.getDeclaredFields();
					for (int i = 0; i < fields.length; i++) {
						Field field = fields[i];
						int modifiers = field.getModifiers();
						if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || field.getAnnotation(Transient.class) != null) {
							continue;
						}
						field.setAccessible(true);
						Object value = null;
						try {
							value = field.get(o);
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
						if (value == null) {
							continue;
						}
						if (availableObj(value)) {
							String name = field.getName();
							if (first) {
								jpql.append(" WHERE ");
								first = false;
								if (value instanceof String && isFuzzy) {
									jpql.append(prefix).append('.').append(name).append(" LIKE ?").append(position);
								} else {
									jpql.append(prefix).append('.').append(name).append(" = ?").append(position);
								}
							} else {
								jpql.append(" AND ");
								if (value instanceof String && isFuzzy) {
									jpql.append(prefix).append('.').append(name).append(" LIKE ?").append(position);
								} else {
									jpql.append(prefix).append('.').append(name).append(" = ?").append(position);
								}
							}
							args.add(value);
							position++;
						} else {
							ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
							OneToOne oneToOne = field.getAnnotation(OneToOne.class);
							Embedded embedded = field.getAnnotation(Embedded.class);
							if (manyToOne != null || oneToOne != null || embedded != null) {
								if (set.contains(o)) {// 若遇到相互关联的情况，则终止递归
									return;
								}
								set.add(o);
								String name = field.getName();
								predicate(value, prefix + "." + name);
							}
						}
					}
					clz = clz.getSuperclass();
				}
			}
		}// END Inner class
		new Predicate().predicate(entity, alias);
		logger.debug("JPQL: \n" + jpql.toString() + "\n" + "Arguments: \n" + Arrays.toString(args.toArray()));
		return new JpqlAndArgs(jpql.toString(), args.toArray());
	}

	private String getEntityName(E entity) {
		String entityName;
		Entity e = entityClass.getAnnotation(Entity.class);
		if (e == null || e.name().length() == 0) {
			entityName = entityClass.getSimpleName();
		} else {
			entityName = e.name();
		}
		return entityName;
	}

	/**
	 * 在对象中筛选出可以直接用字符串表示的对象
	 * 
	 * @param o
	 * @return
	 */
	private boolean availableObj(Object o) {
		return o instanceof Serializable && o instanceof String || o instanceof Number || o instanceof Enum
				|| o instanceof Character || o instanceof Boolean || o instanceof Date || o instanceof Calendar
				|| o instanceof Timestamp || o instanceof TimeZone || o instanceof TemporalAmount || o instanceof Temporal;
	}
	
	/**
	 * 筛选出可以直接用字符串表示的对象的Class
	 * 
	 * @param c
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean availableClass(Class<?> c) {
		return String.class.isAssignableFrom(c) || Number.class.isAssignableFrom(c) || Enum.class.isAssignableFrom(c)
				|| Character.class.isAssignableFrom(c) || Boolean.class.isAssignableFrom(c)
				|| Date.class.isAssignableFrom(c) || Calendar.class.isAssignableFrom(c)
				|| Timestamp.class.isAssignableFrom(c);
	}

	/**
	 * 将实体对象分析成JPQL和对应的参数数组 支持普通嵌入类型、一对一、多对一，但不支持多对多，嵌入集合等复杂数据
	 * 
	 * @author HeLei
	 */
	protected static class JpqlAndArgs {
		public final String jpql;
		public final Object[] args;

		public JpqlAndArgs(String jpql, Object[] args) {
			super();
			this.jpql = jpql;
			this.args = args;
		}

		@Override
		public String toString() {
			return "JpqlAndArgs [jpql=" + jpql + ", args=" + Arrays.toString(args) + "]";
		}
	}

	/**
	 * 对于多对多，嵌入集合等复杂场景，需要手写JPQL，本类仅提供动态查询语句
	 * 
	 * @author HeLei
	 *
	 */
	protected static class PredicateAndArgs {
		public final String predicate;
		public final Object[] args;
		public final String entityName;
		public final String alias;

		public PredicateAndArgs(String predicate, Object[] args, String entityName, String alias) {
			super();
			this.predicate = predicate;
			this.args = args;
			this.entityName = entityName;
			this.alias = alias;
		}

		@Override
		public String toString() {
			return "PredicateAndArgs [predicate=" + predicate + ", args=" + Arrays.toString(args) + ", entityName="
					+ entityName + ", alias=" + alias + "]";
		}
	}

	/**
	 * 根据JPQL返回一个WHERE子句供调用端参考，主要是应对多对多、嵌入集合等需要手写JPQL的情况
	 * 
	 * @param entity
	 * @return
	 */
	protected PredicateAndArgs predicateAndArgs(E entity, AccessType type) {
		JpqlAndArgs jaa;
		if (type == AccessType.FIELD) {
			jaa = jpqlAndArgsByField(entity);
		} else {
			jaa = jpqlAndArgsByPropety(entity);
		}
		Matcher m = jpqlPattern.matcher(jaa.jpql);
		if (!m.find()) {
			throw new IllegalStateException("内部错误：可能是生成的JPQL有错，或者是jpqlPattern正则式有错");
		}
		String predicate = m.group(predicateIndex);
		predicate = predicate == null ? "" : predicate.trim();
		Object[] args = jaa.args;
		String entityName = m.group(entityNameIndex).trim();
		String alias = m.group(aliasIndex).trim();
		return new PredicateAndArgs(predicate, args, entityName, alias);
	}
}
