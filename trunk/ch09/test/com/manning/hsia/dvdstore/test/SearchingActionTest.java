package com.manning.hsia.dvdstore.test;

import java.lang.reflect.Proxy;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.action.SearchingAction;
import com.manning.hsia.dvdstore.action.SearchingActionImpl;
import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.SessionInvocationHandler;
import com.manning.hsia.dvdstore.util.TestCase;

public class SearchingActionTest extends TestCase {
	
	@Test(groups="ch09")
	public void testTargetedQuery() throws Exception {
		SearchingAction action = getSearchingAction();
		List<Item> results = action.getMatchingItems("season", 1);
		assert results.size() == 20 : "Found " + results.size();
	}
	
	@Test(groups="ch09")
	public void testFetchingStrategy() throws Exception {
		SearchingAction action = getSearchingAction();
		List<Item> results = action.getMatchingItemsWithDistributor("season", 1);
		assert results.size() == 20 : "Found " + results.size();
		assert Hibernate.isInitialized( results.get(0).getDistributor() );
	}
	
	@Test(groups="ch09")
	public void testProjection() throws Exception {
		SearchingAction action = getSearchingAction();
		List<String> results = action.getTitleFromMatchingItems("season");
		assert results.size() == 21 : "Found " + results.size();
	}
	
	@Override
	public void postSetUp() throws Exception {
		FullTextSession session = Search.getFullTextSession( factory.openSession() );
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			//manual indexing solution OK for small amounts of data
			List<?> results = session.createCriteria( Item.class ).list();
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
	
	private SearchingAction getSearchingAction() {
		SearchingAction action = new SearchingActionImpl();
		return (SearchingAction) Proxy.newProxyInstance(
					this.getClass().getClassLoader(), 
					new Class[] { SearchingAction.class },
					new SessionInvocationHandler(action, factory) );
	}
}
