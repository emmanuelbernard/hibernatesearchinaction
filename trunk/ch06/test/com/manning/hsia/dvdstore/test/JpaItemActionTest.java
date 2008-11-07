package com.manning.hsia.dvdstore.test;

import java.lang.reflect.Proxy;
import java.util.List;

import javax.persistence.EntityTransaction;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.action.ItemAction;
import com.manning.hsia.dvdstore.action.jpa.ItemActionImpl;
import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.EntityManagerInvocationHandler;

public class JpaItemActionTest extends com.manning.hsia.dvdstore.util.JpaTestCase {
	
	@Test(groups="ch06")
	public void testIdentity() {
		ItemAction identity = getItemAction();
		identity.loadItem(1);
	}

	private ItemAction getItemAction() {
		ItemAction action = new ItemActionImpl();
		return (ItemAction) Proxy.newProxyInstance(
					this.getClass().getClassLoader(), 
					new Class[] { ItemAction.class },
					new EntityManagerInvocationHandler(action, factory) );
	}
	
	@Test(groups="ch06")
	public void testSimpleQuery() {
		ItemAction identity = getItemAction();
		assert 2 == identity.findByTitle("batman").size() : 
			"Wrong numbr of matching results. Found " + identity.findByTitle("batman").size();
	}
	
	@Override
	public void postSetUp() throws Exception {
		FullTextEntityManager entityManager = 
			Search.getFullTextEntityManager( factory.createEntityManager() );
		EntityTransaction tx = null;
		try {
			tx = entityManager.getTransaction();
			tx.begin();
			//manual indexing solution OK for small amounts of data
			List results = entityManager.createQuery( "select i from " + Item.class.getName() + " i" ).getResultList();
			for (Object entity : results) {
			    entityManager.index( entity ); //index each element
			}
			//commit the index changes
			tx.commit();
		}
		finally {
			entityManager.close();
		}
	}

}
