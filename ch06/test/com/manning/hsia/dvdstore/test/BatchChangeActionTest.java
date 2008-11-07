package com.manning.hsia.dvdstore.test;

import java.lang.reflect.Proxy;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.action.BatchChangeAction;
import com.manning.hsia.dvdstore.action.BatchChangeActionImpl;
import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.SessionInvocationHandler;
import com.manning.hsia.dvdstore.util.TestCase;

public class BatchChangeActionTest extends TestCase {
	
	@Test(groups="ch06")
	public void testBatchChange() throws Exception {
		BatchChangeAction action = getBatchChangeAction();
		action.applyBatchChange("director");
		String url = action.getUrl("director");
		assert "http://blog.emmanuelbernard.com".equals( url ) : "found " + url;
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
	
	private BatchChangeAction getBatchChangeAction() {
		BatchChangeAction action = new BatchChangeActionImpl();
		return (BatchChangeAction) Proxy.newProxyInstance(
					this.getClass().getClassLoader(), 
					new Class[] { BatchChangeAction.class },
					new SessionInvocationHandler(action, factory) );
	}
}
