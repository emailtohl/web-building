package com.github.emailtohl.building.common;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.stereotype.Component;

/**
 * 封装Hibernate的SessionFactory
 * @author Helei
 */
@Component
public class HibernateSessionFactory {
	public SessionFactory sessionFactory;
	private final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();

	@Inject
	public HibernateSessionFactory(LocalSessionFactoryBuilder builder) {
		sessionFactory = builder.buildSessionFactory();
	}

	public Session getSession() throws HibernateException {
		Session session = threadLocal.get();
		if (session == null || !session.isOpen()) {
			session = sessionFactory.openSession();
			threadLocal.set(session);
		}
		return session;
	}

	public void closeSession() throws HibernateException {
		Session session = threadLocal.get();
		threadLocal.set(null);
		if (session != null) {
			session.close();
		}
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}
