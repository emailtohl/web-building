package com.github.emailtohl.building.common.jpa.fullTextSearch;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.AbstractCriterionQueryRepository;
import com.github.emailtohl.building.common.utils.BeanUtil;

/**
 * 全文搜索的实现
 * 继承本类，只需提供实体是什么，即可获得全文搜索能力，注意实体中要注释@org.hibernate.search.annotations.Field
 * 
 * @author HeLei
 *
 * @param <E extends Serializable> 存储搜索结果的实体类
 */
public abstract class AbstractSearchableRepository<E extends Serializable> extends AbstractCriterionQueryRepository<E> implements SearchableRepository<E> {
	@PersistenceContext
	protected EntityManager entityManager;
	protected Class<E> entityClass;
	protected String[] onFields;
	
	/**
	 * 根据this.entityClass、索引字段以及query参数获取FullTextQuery
	 * @param query
	 * @return
	 */
	protected FullTextQuery getFullTextQuery(String query) {
		FullTextEntityManager manager = Search.getFullTextEntityManager(this.entityManager);
		QueryBuilder builder = manager.getSearchFactory().buildQueryBuilder().forEntity(this.entityClass).get();
		Query lucene = builder.keyword().onFields(onFields).matching(query).createQuery();
		return manager.createFullTextQuery(lucene, this.entityClass);
	}
	
	/**
	 * Lucene的默认排序是按照Document的得分进行排序的
	 * 所以调用本接口，不会使用Pageable中的sort属性
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Page<SearchResult<E>> search(String query, Pageable pageable) {
		FullTextQuery q = getFullTextQuery(query);
		q.setProjection(FullTextQuery.THIS, FullTextQuery.SCORE, FullTextQuery.DOCUMENT);
		int total = q.getResultSize();
		q.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize());
		List<Object[]> results = q.getResultList();
		List<SearchResult<E>> list = new ArrayList<SearchResult<E>>();
		for (Object[] o : results) {
			list.add(new SearchResult<E>((E) o[0], (Float) o[1], (Document) o[2]));
		}
		return new PageImpl<SearchResult<E>>(list, pageable, total);
	}
	
	@Override
	public Page<E> find(String query, Pageable pageable) {
		FullTextQuery q = getFullTextQuery(query);
		q.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize());
		@SuppressWarnings("unchecked")
		List<E> list = q.getResultList();
		int total = q.getResultSize();
		return new PageImpl<E>(list, pageable, total);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<E> findAll(String query) {
		FullTextQuery q = getFullTextQuery(query);
		q.limitExecutionTimeTo(5000, TimeUnit.MILLISECONDS);
		return q.getResultList();
	}
	
	@Override
	public Page<E> findAllAndPaging(String query, Pageable pageable) {
		List<E> ls = findAll(query);
		List<E> result = new ArrayList<E>();
		int offset = pageable.getOffset(), size = pageable.getPageSize(), max = ls.size();
		for (int i = offset; i < size && i < max; i++) {
			result.add(ls.get(i));
		}
		return new PageImpl<E>(result, pageable, ls.size());
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
		this.entityClass = (Class<E>) arguments[0];
		
		// 初始化索引域
		List<String> fields = new ArrayList<>();
		findProper("", this.entityClass, fields);
		findField("", this.entityClass, fields);
		onFields = fields.toArray(new String[fields.size()]);
	}

	/**
	 * 分析传入的类型，分析其JavaBean属性，解析其带有@org.hibernate.search.annotations.Field注解的属性
	 * 将该属性的名字存储在fields列表中
	 * @param name 递归计算时的前缀
	 * @param clz
	 * @param fields
	 */
	private void findProper(String name, Class<?> clz, List<String> fields) {
		try {
			for (PropertyDescriptor p : Introspector.getBeanInfo(clz, Object.class).getPropertyDescriptors()) {
				IndexedEmbedded e = BeanUtil.getAnnotation(p, IndexedEmbedded.class);
				Field f = BeanUtil.getAnnotation(p, Field.class);
				if (e != null) {
					findProper(name + p.getName(), p.getPropertyType(), fields);
				} else if (f != null) {
					String field = (name.isEmpty() ? "" : name + '.') + (f.name().isEmpty() ? p.getName() : f.name());
					if (!fields.contains(field)) {
						fields.add(field);
					}
				}
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 分析传入的类型，分析其Field字段，解析其带有@org.hibernate.search.annotations.Field注解的属性
	 * 将该属性的名字存储在fields列表中
	 * @param name
	 * @param clz
	 * @param fields
	 */
	private void findField(String name, Class<?> clz, List<String> fields) {
		Class<?> clzz = clz;
		while (clzz != Object.class) {
			java.lang.reflect.Field[] fs = clz.getDeclaredFields();
			for (int i = 0; i < fs.length; i++) {
				if (fs[i].isSynthetic())// 若是内部类连接外围类的引用，则忽略
					continue;
				Field f = fs[i].getAnnotation(Field.class);
				IndexedEmbedded e = fs[i].getAnnotation(IndexedEmbedded.class);
				if (e != null) {
					findField(name + fs[i].getName(), fs[i].getType(), fields);
				} else if (f != null) {
					String field = (name.isEmpty() ? "" : name + '.') + (f.name().isEmpty() ? fs[i].getName() : f.name());
					if (!fields.contains(field)) {
						fields.add(field);
					}
				}
			}
			clzz = clzz.getSuperclass();
		}
	}
	
}
