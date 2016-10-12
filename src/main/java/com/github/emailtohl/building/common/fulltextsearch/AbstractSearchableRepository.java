package com.github.emailtohl.building.common.fulltextsearch;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.FatalBeanException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.EntityManagerProxy;

import com.github.emailtohl.building.site.entities.ForumPost;

/**
 * 全文搜索的实现
 * 
 * @author HeLei
 *
 * @param <T> 存储搜索结果的实体类
 */
public class AbstractSearchableRepository<T> implements SearchableRepository<T> {
	@PersistenceContext
	EntityManager entityManager;

	EntityManagerProxy entityManagerProxy;
	
	Class<T> entityClass;
	String[] onFields;
	
	@SuppressWarnings("unchecked")
	@Override
	public Page<SearchResult<T>> search(String query, Pageable pageable) {
		FullTextEntityManager manager = Search.getFullTextEntityManager(this.entityManagerProxy.getTargetEntityManager());

		QueryBuilder builder = manager.getSearchFactory().buildQueryBuilder().forEntity(ForumPost.class).get();

		Query lucene = builder.keyword()
				.onFields("title", "body", "keywords", "user.name", "user.username", "user.email", "user.description")
				.matching(query).createQuery();

		FullTextQuery q = manager.createFullTextQuery(lucene, ForumPost.class);
		q.setProjection(FullTextQuery.THIS, FullTextQuery.SCORE);

		long total = q.getResultSize();

		q.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize());

		List<Object[]> results = q.getResultList();
		List<SearchResult<T>> list = new ArrayList<>();
		results.forEach(o -> list.add(new SearchResult<>((T) o[0], (Float) o[1])));
		
		return new PageImpl<>(list, pageable, total);
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected AbstractSearchableRepository() {
		Type genericSuperclass = this.getClass().getGenericSuperclass();
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
		this.entityClass = (Class<T>) arguments[0];
		List<String> fields = new ArrayList<>();
		setOnFields(fields);
	}

	private void setOnFields(List<String> fields) {
		
	}
	
	/**
	 * Spring完成该类实例化后执行的方法
	 */
	@PostConstruct
	public void initialize() {
		if (!(this.entityManager instanceof EntityManagerProxy))
			throw new FatalBeanException("Entity manager " + this.entityManager + " was not a proxy");

		this.entityManagerProxy = (EntityManagerProxy) this.entityManager;
	}
	
}
