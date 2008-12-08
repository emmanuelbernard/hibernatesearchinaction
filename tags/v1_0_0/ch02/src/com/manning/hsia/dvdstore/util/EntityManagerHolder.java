package com.manning.hsia.dvdstore.util;

import javax.persistence.EntityManager;

public class EntityManagerHolder {
	private static ThreadLocal<EntityManager> entityManagerHolder = new ThreadLocal<EntityManager>();
	
	public static EntityManager getEntityManager() {
		return entityManagerHolder.get();
	}
	
	public static void setEntityManager(EntityManager session) {
		entityManagerHolder.set(session);
	}
}
