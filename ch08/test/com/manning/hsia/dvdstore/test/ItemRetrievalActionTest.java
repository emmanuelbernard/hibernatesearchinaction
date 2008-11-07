package com.manning.hsia.dvdstore.test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.action.DistributorAction;
import com.manning.hsia.dvdstore.action.DistributorActionImpl;
import com.manning.hsia.dvdstore.action.ItemRetrievalAction;
import com.manning.hsia.dvdstore.action.ItemRetrievalActionImpl;
import com.manning.hsia.dvdstore.model.Distributor;
import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.SessionInvocationHandler;
import com.manning.hsia.dvdstore.util.TestCase;

public class ItemRetrievalActionTest extends TestCase {
	
	@Test(groups="ch08")
	public void testIdentity() {
		ItemRetrievalAction action = getItemRetrievalAction();
		List<Item> results = action.searchItemWithinDistributor("season", getDistributor() );
		assert 9 == results.size() : "Found " + results.size();
	}
	
	@Test(groups="ch08")
	public void testSecurity() {
		ItemRetrievalAction action = getItemRetrievalAction();
		searchFor(action, "toy");
		searchFor(action, "season");
	}

	private void searchFor(ItemRetrievalAction action, String searchString) {
		List<Item> resultsWhenChild = action.searchItems(searchString, true );
		List<Item> resultsWhenAdult = action.searchItems(searchString, false );
		assert resultsWhenChild.size() > resultsWhenAdult.size() : 
				"Found child " + resultsWhenChild.size() + " : adult " + resultsWhenAdult.size();
		System.out.println( searchString + " child " + resultsWhenChild.size() + " adult " + resultsWhenAdult.size() );
		assert resultsWhenChild.size() > 0;
		assert resultsWhenAdult.size() > 0;
	}
	

	@Test(groups="ch08")
	public void testRangeFilter() {
		ItemRetrievalAction action = getItemRetrievalAction();
		List<Item> results = action.searchItemsLowPrice("funny");
		assert 17 == results.size() : "Found " + results.size();
		final BigDecimal bigDecimal = new BigDecimal(15);
		for (Item item : results) {
			assert item.getPrice().compareTo( bigDecimal ) <= 0;
		}
	}
	
	@Test(groups="ch08")
	public void testSearchWithinSearch() {
		ItemRetrievalAction action = getItemRetrievalAction();
		List<Item> results = action.searchItems("season");
		assert 28 == results.size() : "Found " + results.size();
		results = action.searchWithinSearch("friends");
		int subsearch = results.size();
		assert 9 == subsearch : "Found " + results.size();
		assert subsearch < action.searchItems("friends").size();
	}
	
	@Test(groups="ch08")
	public void testSearchWithinStock() {
		ItemRetrievalAction action = getItemRetrievalAction();
		List<Item> results = action.searchWithinStock("lady");
		assert 8 == results.size() : "Found " + results.size();
		for (Item item : results) {
			assert !"630522577X".equals( item.getEan() );
		}
	}
	
	public Distributor getDistributor() {
		DistributorAction distAction = getDistributorAction();
		return distAction.getDistributors().get(0);
	}

	private DistributorAction getDistributorAction() {
		DistributorAction action = new DistributorActionImpl();
		return (DistributorAction) Proxy.newProxyInstance(
					this.getClass().getClassLoader(), 
					new Class[] { DistributorAction.class },
					new SessionInvocationHandler(action, factory) );
	}
	
	private ItemRetrievalAction getItemRetrievalAction() {
		ItemRetrievalAction action = new ItemRetrievalActionImpl();
		return (ItemRetrievalAction) Proxy.newProxyInstance(
					this.getClass().getClassLoader(), 
					new Class[] { ItemRetrievalAction.class },
					new SessionInvocationHandler(action, factory) );
	}
	
	@Override
	public void postSetUp() throws Exception {
		FullTextSession session = Search.getFullTextSession( factory.openSession() );
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			//manual indexing solution OK for small amounts of data
			List results = session.createCriteria( Item.class )
				.setFetchMode("categories", FetchMode.JOIN)
				.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
				.list();
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
