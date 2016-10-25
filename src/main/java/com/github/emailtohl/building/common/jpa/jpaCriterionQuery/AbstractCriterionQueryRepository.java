package com.github.emailtohl.building.common.jpa.jpaCriterionQuery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;

import com.github.emailtohl.building.common.jpa.AbstractDynamicQueryRepository;

/**
 * 提供标准查询的基类
 * 本实现依赖Spring data，所以让其继承AbstractDynamicQueryRepository
 * @param <E> 实体类
 */
public abstract class AbstractCriterionQueryRepository<E extends Serializable> extends AbstractDynamicQueryRepository<E>
		implements CriterionQueryRepository<E> {
	/**
	 * 标准查询接口，根据传入的条件List得到一个Page对象 注意，Pageable的查询是从第0页开始
	 * 
	 * @param criteriaList 一个条件List
	 * @param pageable 分页对象
	 * @return
	 */
	@Override
	public Page<E> search(List<Criterion> criteriaList, Pageable pageable) {
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<E> countRoot = countCriteria.from(this.entityClass);
		long total = this.entityManager.createQuery(
				countCriteria.select(builder.count(countRoot)).where(toPredicates(criteriaList, countRoot, builder)))
				.getSingleResult();

		CriteriaQuery<E> pageCriteria = builder.createQuery(this.entityClass);
		Root<E> pageRoot = pageCriteria.from(this.entityClass);
		List<E> list = this.entityManager
				.createQuery(pageCriteria.select(pageRoot).where(toPredicates(criteriaList, pageRoot, builder))
						.orderBy(QueryUtils.toOrders(pageable.getSort(), pageRoot, builder)))
				.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

		return new PageImpl<>(new ArrayList<>(list), pageable, total);
	}

	private static Predicate[] toPredicates(List<Criterion> criteriaList, Root<?> root, CriteriaBuilder builder) {
		Predicate[] predicates = new Predicate[criteriaList.size()];
		int i = 0;
		for (Criterion c : criteriaList)
			predicates[i++] = c.getOperator().toPredicate(c, root, builder);
		return predicates;
	}
}
