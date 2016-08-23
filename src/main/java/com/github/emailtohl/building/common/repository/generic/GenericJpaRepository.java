package com.github.emailtohl.building.common.repository.generic;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
/**
 * 建立在通用数据仓库上的JPA仓库
 * @author HeLei
 *
 * @param <I> ID
 * @param <E> 实体类
 */
public abstract class GenericJpaRepository<I extends Serializable, E extends Serializable>
		extends GenericBaseRepository<I, E> {
	@PersistenceContext
	protected EntityManager entityManager;
	@PersistenceUnit
	protected EntityManagerFactory emf;

	protected GenericJpaRepository() {
		super();
	}

	protected GenericJpaRepository(Class<I> idClass, Class<E> entityClass) {
		super(idClass, entityClass);
	}

	@Override
	public Iterable<E> getAll() {
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<E> query = builder.createQuery(this.entityClass);
		return this.entityManager.createQuery(query.select(query.from(this.entityClass))).getResultList();
	}

	@Override
	public E get(I id) {
		return this.entityManager.find(this.entityClass, id);
	}

	@Override
	public void add(E entity) {
		this.entityManager.persist(entity);
		this.entityManager.flush();
	}

	@Override
	public void update(E entity) {
		this.entityManager.merge(entity);
	}

	@Override
	public void remove(E entity) {
		this.entityManager.remove(entity);
	}

	/**
	 * 注意：当所有实体都有名为id的属性时，deleteById方法才能工作
	 * 如果代理键特性名不同，就必须按照ID获取实体，然后调用remove方法
	 */
	@Override
	public void removeById(I id) {
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		CriteriaDelete<E> query = builder.createCriteriaDelete(this.entityClass);
		this.entityManager.createQuery(query.where(builder.equal(query.from(this.entityClass).get("id"), id)))
				.executeUpdate();
	}
	
}