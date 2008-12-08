package com.manning.hsia.dvdstore.util;

import org.hibernate.search.jpa.FullTextEntityManager;

public class EntityManagerHolder {
	private static ThreadLocal<FullTextEntityManager> entityManagerHolder = new ThreadLocal<FullTextEntityManager>();
	
	public static FullTextEntityManager getFullTextEntityManager() {
		return entityManagerHolder.get();
	}
	
	public static void setFullTextEntityManager(FullTextEntityManager session) {
		entityManagerHolder.set(session);
	}
}
