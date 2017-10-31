package com.github.emailtohl.building.common.jpa.jpaCriterionQuery;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;

import com.github.emailtohl.building.common.jpa.AbstractDynamicQueryRepository;
import com.github.emailtohl.building.common.utils.BeanUtil;

/**
 * 提供标准查询的基类
 * 
 * 注意：调用者需根据业务情况明确事务边界，添加上@javax.transaction.Transactional
 * 
 * @param <E> 实体类
 * @author HeLei
 * @date 2017.02.04
 */
public abstract class AbstractCriterionQueryRepository<E extends Serializable> extends AbstractDynamicQueryRepository<E>
		implements CriterionQueryRepository<E> {
	/**
	 * 标准查询接口，根据传入的条件集合得到一个Page对象 注意:Pageable的查询是从第0页开始，条件集合之间是AND关系
	 * 
	 * @param criteria 一个条件集合
	 * @param pageable 分页对象
	 * @return
	 */
	@Override
	public Page<E> search(Collection<Criterion> criteria, Pageable pageable) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<E> countRoot = countQuery.from(entityClass);
		long total = entityManager
				.createQuery(
						countQuery.select(builder.count(countRoot)).where(toPredicates(criteria, countRoot, builder)))
				.getSingleResult();

		CriteriaQuery<E> query = builder.createQuery(entityClass);
		Root<E> queryRoot = query.from(entityClass);
		List<E> list = entityManager
				.createQuery(query.select(queryRoot).where(toPredicates(criteria, queryRoot, builder))
						.orderBy(QueryUtils.toOrders(pageable.getSort(), queryRoot, builder)))
				.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

		return new PageImpl<E>(new ArrayList<E>(list), pageable, total);
	}

	protected Predicate[] toPredicates(Collection<Criterion> criteria, Root<?> root, CriteriaBuilder builder) {
		Predicate[] predicates = new Predicate[criteria.size()];
		int i = 0;
		for (Criterion c : criteria)
			predicates[i++] = c.getOperator().toPredicate(c, root, builder);
		return predicates;
	}
	
	/**
	 * 将对象存储的值转成谓词集合
	 * 
	 * 注意：不包括对象中集合属性
	 * @param e 实体参数
	 * @param type 分析对象的方式
	 * @return
	 */
	protected Set<Predicate> toPredicate(final E entity, final AccessType type, final Root<?> r,
			final CriteriaBuilder b) {
		final Set<Object> set = new HashSet<Object>();
		Set<Predicate> predicates = new HashSet<Predicate>();
		/**
		 * 创建内部类，递归地使用创建它作用域中的数据
		 */
		class PredicateByProperty {
			@SuppressWarnings({ "unchecked" })
			void predicate(Object o, Path<?> prefix) {
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
				try {
					for (PropertyDescriptor descriptor : Introspector.getBeanInfo(clz, Object.class)
							.getPropertyDescriptors()) {
						if (BeanUtil.getAnnotation(descriptor, Transient.class) != null) {
							continue;
						}
						Method m = descriptor.getReadMethod();
						if (m == null) {
							continue;
						}
						Object value = m.invoke(o);
						if (value == null) {
							continue;
						}
						if (availableObj(value)) {
							String name = descriptor.getName();
							Path<?> path;
							if (prefix == null) {
								path = r.get(name);
							} else {
								path = prefix.get(name);
							}
							if (value instanceof String && isFuzzy) {
								predicates.add(b.like(b.lower((Path<String>) path), ((String) value).trim().toLowerCase()));
							} else {
								predicates.add(b.equal(path, value));
							}
						} else {
							ManyToOne manyToOne = BeanUtil.getAnnotation(descriptor, ManyToOne.class);
							OneToOne oneToOne = BeanUtil.getAnnotation(descriptor, OneToOne.class);
							Embedded embedded = BeanUtil.getAnnotation(descriptor, Embedded.class);
							if (manyToOne != null || oneToOne != null || embedded != null) {
								if (set.contains(o)) {// 若遇到相互关联的情况，则终止递归
									return;
								}
								set.add(o);
								Path<?> path;
								if (prefix == null) {
									path = r.get(descriptor.getName());
								} else {
									path = prefix.get(descriptor.getName());
								}
								predicate(value, path);
							}
						}

					}
				} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					LOG.catching(e);
					throw new IllegalArgumentException(e);
				}
			}
		}// END Inner class

		class PredicateByField {
			@SuppressWarnings("unchecked")
			void predicate(Object o, Path<?> prefix) {
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
						if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)
								|| field.getAnnotation(Transient.class) != null) {
							continue;
						}
						field.setAccessible(true);
						Object value = null;
						try {
							value = field.get(o);
						} catch (IllegalAccessException e) {
							LOG.catching(e);
							throw new IllegalArgumentException(e);
						}
						if (value == null) {
							continue;
						}
						if (availableObj(value)) {
							String name = field.getName();
							Path<?> path;
							if (prefix == null) {
								path = r.get(name);
							} else {
								path = prefix.get(name);
							}
							if (value instanceof String && isFuzzy) {
								predicates.add(b.like(b.lower((Path<String>) path), ((String) value).trim().toLowerCase()));
							} else {
								predicates.add(b.equal(path, value));
							}
						} else {
							ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
							OneToOne oneToOne = field.getAnnotation(OneToOne.class);
							Embedded embedded = field.getAnnotation(Embedded.class);
							if (manyToOne != null || oneToOne != null || embedded != null) {
								if (set.contains(o)) {// 若遇到相互关联的情况，则终止递归
									return;
								}
								set.add(o);
								Path<?> path;
								if (prefix == null) {
									path = r.get(field.getName());
								} else {
									path = prefix.get(field.getName());
								}
								predicate(value, path);
							}
						}
					}
					clz = clz.getSuperclass();
				}
			}
		}// END Inner class
		if (entity == null) {
			return predicates;
		}
		if (type == null || type == AccessType.PROPERTY) {
			new PredicateByProperty().predicate(entity, null);
		} else {
			new PredicateByField().predicate(entity, null);
		}
		return predicates;
	}
	

	public AbstractCriterionQueryRepository() {
		super();
	}

	public AbstractCriterionQueryRepository(Class<E> entityClass) {
		super(entityClass);
	}
	
}
