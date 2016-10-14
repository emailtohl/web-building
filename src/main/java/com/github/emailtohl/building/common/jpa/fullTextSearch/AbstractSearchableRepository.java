package com.github.emailtohl.building.common.jpa.fullTextSearch;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.lucene.search.Query;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.FatalBeanException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.EntityManagerProxy;

import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.AbstractCriterionQueryRepository;
import com.github.emailtohl.building.site.entities.ForumPost;

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
	/**
	 * Spring 注入的EntityManager实际上就是EntityManagerProxy
	 * 但是本程序需要访问entityManagerProxy.getTargetEntityManager()获取
	 * “actual Hibernate ORM EntityManager implementation”
	 * 所以在注入EntityManager后，将其向下转型为EntityManagerProxy
	 */
	protected EntityManagerProxy entityManagerProxy;
	protected Class<E> entityClass;
	protected String[] onFields;
	
	/**
	 * Lucene的默认排序是按照Document的得分进行排序的
	 * 所以调用本接口，不会使用Pageable中的sort属性
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Page<SearchResult<E>> search(String query, Pageable pageable) {
		// Search.getFullTextEntityManager接收具体的实现，而不是Spring的代理
		FullTextEntityManager manager = Search.getFullTextEntityManager(this.entityManagerProxy.getTargetEntityManager());

		QueryBuilder builder = manager.getSearchFactory().buildQueryBuilder().forEntity(ForumPost.class).get();

		Query lucene = builder.keyword().onFields(onFields).matching(query).createQuery();

		FullTextQuery q = manager.createFullTextQuery(lucene, ForumPost.class);
		q.setProjection(FullTextQuery.THIS, FullTextQuery.SCORE);

		long total = q.getResultSize();

		q.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize());

		List<Object[]> results = q.getResultList();
		List<SearchResult<E>> list = new ArrayList<>();
		results.forEach(o -> list.add(new SearchResult<>((E) o[0], (Float) o[1])));
		
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
	 * @param name
	 * @param clz
	 * @param fields
	 */
	private void findProper(String name, Class<?> clz, List<String> fields) {
		try {
			for (PropertyDescriptor p : Introspector.getBeanInfo(clz, Object.class).getPropertyDescriptors()) {
				Method rm = p.getReadMethod(), wm = p.getWriteMethod();
				Field f = rm.getAnnotation(Field.class);
				if (f == null && wm != null) {
					f = wm.getAnnotation(Field.class);
				}
				IndexedEmbedded e = rm.getAnnotation(IndexedEmbedded.class);
				if (e == null && wm != null) {
					e = wm.getAnnotation(IndexedEmbedded.class);
				}
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
