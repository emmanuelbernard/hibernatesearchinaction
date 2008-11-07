package com.manning.hsia.dvdstore.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchException;

public class SessionInvocationHandler implements InvocationHandler {
	
	private SessionFactory factory;
	private Object delegate;

	public SessionInvocationHandler(Object delegate, SessionFactory factory) {
		this.factory = factory;
		this.delegate = delegate;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		FullTextSession session = Search.getFullTextSession( factory.openSession() );
		SessionHolder.setFullTextSession(session);
		Transaction tx = null;
		Object result;
		try {
			tx = session.beginTransaction();
			result = method.invoke(delegate, args);
			tx.commit();
		}
		catch (HibernateException e) {
			rollbackIfNeeded(tx);
			throw e;
		}
		catch (SearchException e) {
			rollbackIfNeeded(tx);
			throw e;
		}
		finally {
			session.close();
			SessionHolder.setFullTextSession(null);
		}
		return result;
	}
	
	private void rollbackIfNeeded(org.hibernate.Transaction tx) {
		if ( tx != null && tx.isActive() ) {
			tx.rollback();
		}
	}
}
