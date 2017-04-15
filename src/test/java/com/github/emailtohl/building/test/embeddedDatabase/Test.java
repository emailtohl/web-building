package com.github.emailtohl.building.test.embeddedDatabase;

import static com.github.emailtohl.building.initdb.PersistenceData.*;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.H2Dialect;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

import com.github.emailtohl.building.common.jpa.entity.BaseEntity;
import com.github.emailtohl.building.site.entities.user.User;

public class Test {

	public LocalSessionFactoryBuilder sessionFactory() {
		LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(embeddedDataSource());
		builder.scanPackages("com.github.emailtohl.building.site.entities");
		builder.setProperty("hibernate.dialect", H2Dialect.class.getCanonicalName());
		builder.setProperty("hibernate.show_sql", "true");
		builder.setProperty("hibernate.hbm2ddl.auto", "update");
		builder.setProperty("hibernate.format_sql", "true");
		builder.setProperty("hibernate.use_sql_comments", "true");
		
		File projectContextRoot = new File(getClass().getResource("/").getFile());
		File dataPath = new File(projectContextRoot.getParentFile(), "web-building-data");
		File indexBase = new File(dataPath, "indexBase");
		if (!indexBase.exists())
			indexBase.mkdirs();
		
		builder.setProperty("hibernate.search.default.directory_provider", "filesystem");
		builder.setProperty("hibernate.search.default.indexBase", indexBase.getAbsolutePath());
		
		return builder;
	}
	
	public DataSource embeddedDataSource() {
		return new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.H2)
				.addScripts("classpath:test-data.sql")
				.build();
	}
	
	private static BaseEntity appendDate(BaseEntity e) {
		Date d = new Date();
		e.setCreateDate(d);
		e.setModifyDate(d);
		try {
			for (PropertyDescriptor p : Introspector.getBeanInfo(e.getClass(), Object.class).getPropertyDescriptors()) {
				if (Collection.class.isAssignableFrom(p.getPropertyType())) {
					Collection<?> c = (Collection<?>) p.getReadMethod().invoke(e, new Object[] {});
					c.forEach(o -> {
						BaseEntity be = (BaseEntity) o;
						be.setCreateDate(d);
						be.setModifyDate(d);
					});
				}
			}
		} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
			e1.printStackTrace();
		}
		return e;
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Test t = new Test();
		SessionFactory sf = t.sessionFactory().buildSessionFactory();
		Session s = sf.openSession();
		s.getTransaction().begin();
		
		s.persist(appendDate(user_role_authority_allocation));
		s.persist(appendDate(user_create_ordinary));
		s.persist(appendDate(user_create_special));
		s.persist(appendDate(user_enable));
		s.persist(appendDate(user_disable));
		s.persist(appendDate(user_grant_roles));
		s.persist(appendDate(user_read_all));
		s.persist(appendDate(user_read_self));
		s.persist(appendDate(user_update_all));
		s.persist(appendDate(user_update_self));
		s.persist(appendDate(user_delete));
		s.persist(appendDate(user_customer));
		s.persist(appendDate(application_form_transit));
		s.persist(appendDate(application_form_read_history));
		s.persist(appendDate(application_form_delete));
		s.persist(appendDate(forum_delete));
		s.persist(appendDate(audit_user));
		s.persist(appendDate(audit_role));
		s.persist(appendDate(resource_manager));
		s.persist(appendDate(content_manager));

		s.persist(appendDate(admin));
		s.persist(appendDate(manager));
		s.persist(appendDate(employee));
		s.persist(appendDate(user));
		
		s.persist(appendDate(product));
		s.persist(appendDate(qa));
		s.persist(appendDate(company));
		s.persist(appendDate(emailtohl));
		s.persist(appendDate(foo));
		s.persist(appendDate(bar));
		s.persist(appendDate(baz));
		s.persist(appendDate(qux));
	
		s.persist(appendDate(parent));
		s.persist(appendDate(subType));
		s.persist(appendDate(article));
		s.persist(appendDate(comment));
		
		s.getTransaction().commit();
		s.close();
		
		// ------------------------
		s = sf.openSession();
		s.getTransaction().begin();
		
		s.createCriteria(User.class)
		.add(Restrictions.isNotNull("username"))
		.list()
		.stream().forEach(o -> System.out.println(o));
		
		s.getTransaction().commit();
		s.close();
	
	}
}
