package com.github.emailtohl.building.site.dao.impl;

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

import com.github.emailtohl.building.common.fulltextsearch.SearchResult;
import com.github.emailtohl.building.common.fulltextsearch.SearchableRepository;
import com.github.emailtohl.building.site.entities.ForumPost;

public class ForumPostRepositoryImpl implements SearchableRepository<ForumPost> {
	@PersistenceContext
	EntityManager entityManager;

	EntityManagerProxy entityManagerProxy;

	@Override
	public Page<SearchResult<ForumPost>> search(String query, Pageable pageable) {
		FullTextEntityManager manager = this.getFullTextEntityManager();

		QueryBuilder builder = manager.getSearchFactory().buildQueryBuilder().forEntity(ForumPost.class).get();

		Query lucene = builder.keyword()
				.onFields("title", "body", "keywords", "user.name", "user.username", "user.email", "user.description")
				.matching(query).createQuery();

		FullTextQuery q = manager.createFullTextQuery(lucene, ForumPost.class);
		q.setProjection(FullTextQuery.THIS, FullTextQuery.SCORE);

		long total = q.getResultSize();

		q.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize());

		@SuppressWarnings("unchecked")
		List<Object[]> results = q.getResultList();
		List<SearchResult<ForumPost>> list = new ArrayList<>();
		results.forEach(o -> list.add(new SearchResult<>((ForumPost) o[0], (Float) o[1])));

		return new PageImpl<>(list, pageable, total);
	}

	private FullTextEntityManager getFullTextEntityManager() {
		return Search.getFullTextEntityManager(this.entityManagerProxy.getTargetEntityManager());
	}

	@PostConstruct
	public void initialize() {
		if (!(this.entityManager instanceof EntityManagerProxy))
			throw new FatalBeanException("Entity manager " + this.entityManager + " was not a proxy");

		this.entityManagerProxy = (EntityManagerProxy) this.entityManager;
	}
}
