package com.github.emailtohl.building.common.jpa.envers;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.github.emailtohl.building.common.jpa.fullTextSearch.AbstractSearchableRepository;
/**
 * 查询Hibernate envers对实体的审计记录
 * 
 * 注意：调用者需根据业务情况明确事务边界，添加上@javax.transaction.Transactional
 * 
 * @param <E> 实体类型
 * @author HeLei
 * @date 2017.02.04
 */
public abstract class AbstractAuditedRepository<E extends Serializable> implements AuditedRepository<E> {
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger();
	
	@PersistenceUnit
	protected EntityManagerFactory entityManagerFactory;
	@PersistenceContext
	protected EntityManager entityManager;
	protected Class<E> entityClass;
	
	@SuppressWarnings("unchecked")
	@Override
	public Page<Tuple<E>> getEntityRevision(Map<String, String> propertyNameValueMap, Pageable pageable) {
		AuditReader auditReader = AuditReaderFactory.get(entityManager);
		AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(entityClass, false, false);
		if (propertyNameValueMap != null) {
			for (Entry<String, String> e : propertyNameValueMap.entrySet()) {
				String v = e.getValue();
				if (v != null && !v.isEmpty()) {
					query.add(AuditEntity.property(e.getKey()).like(v.trim(), MatchMode.START));
				}
			}
		}
		Sort sort = pageable.getSort();
		if (sort != null) {
			Iterator<Order> i = sort.iterator();
			while (i.hasNext()) {
				Order o = i.next();
				if (o.isAscending()) {
					query.addOrder(AuditEntity.property(o.getProperty()).asc());
				} else {
					query.addOrder(AuditEntity.property(o.getProperty()).desc());
				}
			}
		}
		query.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize());
		List<Object[]> result = query.getResultList();
		List<Tuple<E>> ls = new ArrayList<Tuple<E>>();
		for (Object[] o : result) {
			Tuple<E> tuple = new Tuple<E>();
			tuple.setEntity((E) o[0]);
			tuple.setDefaultRevisionEntity((DefaultRevisionEntity) o[1]);
			tuple.setRevisionType((RevisionType) o[2]);
			ls.add(tuple);
		}
		/*
		 * 由于AuditQuery没有提供获取总条数的接口，所以此处对total进行猜测：
		 * 如果列表数目等于页码尺寸，那就认为还有下一页，否则到本页为止
		 */
		int total = pageable.getOffset();
		if (result.size() == pageable.getPageSize()) {
			total += 2 * pageable.getPageSize();
		} else {
			total += result.size();
		}
		return new PageImpl<Tuple<E>>(ls, pageable, total);
	}

	@Override
	public Page<E> getEntitiesAtRevision(Number revision, Map<String, String> propertyNameValueMap, Pageable pageable) {
		AuditReader auditReader = AuditReaderFactory.get(entityManager);
		AuditQuery query = auditReader.createQuery().forEntitiesAtRevision(entityClass, revision);
		for (Entry<String, String> e : propertyNameValueMap.entrySet()) {
			String v = e.getValue();
			if (v != null && !v.isEmpty()) {
				query.add(AuditEntity.property(e.getKey()).like(v.trim(), MatchMode.START));
			}
		}
		Sort sort = pageable.getSort();
		if (sort != null) {
			Iterator<Order> i = sort.iterator();
			while (i.hasNext()) {
				Order o = i.next();
				if (o.isAscending()) {
					query.addOrder(AuditEntity.property(o.getProperty()).asc());
				} else {
					query.addOrder(AuditEntity.property(o.getProperty()).desc());
				}
			}
		}
		query.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize());
		@SuppressWarnings("unchecked")
		List<E> result = query.getResultList();
		/*
		 * 由于AuditQuery没有提供获取总条数的接口，所以此处对total进行猜测：
		 * 如果列表数目等于页码尺寸，那就认为还有下一页，否则到本页为止
		 */
		int total = pageable.getOffset();
		if (result.size() == pageable.getPageSize()) {
			total += 2 * pageable.getPageSize();
		} else {
			total += result.size();
		}
		return new PageImpl<E>(result, pageable, total);
	}

	@Override
	public E getEntityAtRevision(Long id, Number revision) {
		AuditReader auditReader = AuditReaderFactory.get(entityManager);
		return auditReader.find(entityClass, id, revision);
	}
	
	@Override
	public void rollback(Long id, Number revision) {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			em.getTransaction().begin();
			AuditReader auditReader = AuditReaderFactory.get(entityManager);
			E bygone = auditReader.find(entityClass, id, revision);
			em.unwrap(Session.class).replicate(bygone, ReplicationMode.OVERWRITE);
			em.getTransaction().commit();
		} finally {
			if (em.isOpen())
				em.close();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected AbstractAuditedRepository() {
		Type genericSuperclass = getClass().getGenericSuperclass();
		while (!(genericSuperclass instanceof ParameterizedType)) {
			if (!(genericSuperclass instanceof Class))
				throw new IllegalStateException("Unable to determine type "
						+ "arguments because generic superclass neither " + "parameterized type nor class.");
			if (genericSuperclass == AbstractSearchableRepository.class)
				throw new IllegalStateException("Unable to determine type "
						+ "arguments because no parameterized generic superclass " + "found.");
			genericSuperclass = ((Class) genericSuperclass).getGenericSuperclass();
		}
		ParameterizedType type = (ParameterizedType) genericSuperclass;
		Type[] arguments = type.getActualTypeArguments();
		entityClass = (Class<E>) arguments[0];
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

}
