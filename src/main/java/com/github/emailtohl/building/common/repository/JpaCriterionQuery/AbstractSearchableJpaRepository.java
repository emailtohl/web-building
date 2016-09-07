package com.github.emailtohl.building.common.repository.JpaCriterionQuery;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * 提供标准查询的基类
 * 
 * @param <E> 实体类
 */
public abstract class AbstractSearchableJpaRepository<E> implements SearchableRepository<E> {
	@PersistenceContext
	protected EntityManager entityManager;

	protected final Class<E> entityClass;

	@SuppressWarnings("unchecked")
	protected AbstractSearchableJpaRepository() {
		Type genericSuperclass = this.getClass().getGenericSuperclass();
		while (!(genericSuperclass instanceof ParameterizedType)) {
			if (!(genericSuperclass instanceof Class))
				throw new IllegalStateException("Unable to determine type "
						+ "arguments because generic superclass neither " + "parameterized type nor class.");
			if (genericSuperclass == AbstractSearchableJpaRepository.class)
				throw new IllegalStateException("Unable to determine type "
						+ "arguments because no parameterized generic superclass " + "found.");

			genericSuperclass = ((Class<?>) genericSuperclass).getGenericSuperclass();
		}

		ParameterizedType type = (ParameterizedType) genericSuperclass;
		Type[] arguments = type.getActualTypeArguments();
		this.entityClass = (Class<E>) arguments[0];
	}
	/**
	 * 标准查询接口，根据传入的条件List得到一个Page对象
	 * 注意，Pageable的查询是从第0页开始
	 * @param criteria 一个条件List
	 * @param pageable 分页对象
	 * @return
	 */
	@Override
	public Page<E> search(SearchCriteria criteria, Pageable pageable) {
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<E> countRoot = countCriteria.from(this.entityClass);
		long total = this.entityManager.createQuery(
				countCriteria.select(builder.count(countRoot)).where(toPredicates(criteria, countRoot, builder)))
				.getSingleResult();

		CriteriaQuery<E> pageCriteria = builder.createQuery(this.entityClass);
		Root<E> pageRoot = pageCriteria.from(this.entityClass);
		List<E> list = this.entityManager
				.createQuery(pageCriteria.select(pageRoot).where(toPredicates(criteria, pageRoot, builder))
						.orderBy(toOrders(pageable.getSort(), pageRoot, builder)))
				.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

		return new PageImpl<>(new ArrayList<>(list), pageable, total);
	}

	private static Predicate[] toPredicates(SearchCriteria criteria, Root<?> root, CriteriaBuilder builder) {
		Predicate[] predicates = new Predicate[criteria.size()];
		int i = 0;
		for (Criterion c : criteria)
			predicates[i++] = c.getOperator().toPredicate(c, root, builder);
		return predicates;
	}
}
