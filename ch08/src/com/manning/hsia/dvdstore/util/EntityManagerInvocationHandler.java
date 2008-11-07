package com.manning.hsia.dvdstore.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.hibernate.search.SearchException;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;

public class EntityManagerInvocationHandler implements InvocationHandler {
	
	private EntityManagerFactory factory;
	private Object delegate;

	public EntityManagerInvocationHandler(Object delegate, EntityManagerFactory factory) {
		this.factory = factory;
		this.delegate = delegate;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		FullTextEntityManager entityManager = Search.getFullTextEntityManager( factory.createEntityManager() );
		EntityManagerHolder.setFullTextEntityManager(entityManager);
		EntityTransaction tx = null;
		Object result;
		try {
			tx = entityManager.getTransaction();
			tx.begin();
			result = method.invoke(delegate, args);
			tx.commit();
		}
		catch (PersistenceException e) {
			rollbackIfNeeded(tx);
			throw e;
		}
		catch (SearchException e) {
			rollbackIfNeeded(tx);
			throw e;
		}
		finally {
			entityManager.close();
			EntityManagerHolder.setFullTextEntityManager(null);
		}
		return result;
	}
	
	private void rollbackIfNeeded(EntityTransaction tx) {
		if ( tx != null && tx.isActive() ) {
			tx.rollback();
		}
	}
}
