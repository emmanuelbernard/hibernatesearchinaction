package com.manning.hsia.dvdstore.test;

import java.lang.reflect.Proxy;
import java.util.List;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchException;
import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.action.DisplayAction;
import com.manning.hsia.dvdstore.action.DisplayActionImpl;
import com.manning.hsia.dvdstore.action.ItemAction;
import com.manning.hsia.dvdstore.action.ItemActionImpl;
import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.SessionInvocationHandler;
import com.manning.hsia.dvdstore.util.TestCase;

public class ItemActionTest extends TestCase {
	
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
					new SessionInvocationHandler(action, factory) );
	}
	
	@Test(groups="ch06")
	public void testSimpleQuery() {
		ItemAction identity = getItemAction();
		assert 2 == identity.findByTitle("batman").size() : 
			"Wrong numbr of matching results. Found " + identity.findByTitle("batman").size();
	}
	
	@Override
	public void postSetUp() throws Exception {
		FullTextSession session = Search.getFullTextSession( factory.openSession() );
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			//manual indexing solution OK for small amounts of data
			List results = session.createCriteria( Item.class ).list();
			for (Object entity : results) {
			    session.index( entity ); //index each element
			}
			//commit the index changes
			tx.commit();
		}
		finally {
			session.close();
		}
	}

}
