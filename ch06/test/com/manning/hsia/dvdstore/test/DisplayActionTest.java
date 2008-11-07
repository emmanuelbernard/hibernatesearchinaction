package com.manning.hsia.dvdstore.test;

import java.lang.reflect.Proxy;
import java.util.List;

import org.apache.lucene.search.Explanation;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.action.DisplayAction;
import com.manning.hsia.dvdstore.action.DisplayActionImpl;
import com.manning.hsia.dvdstore.action.ItemView;
import com.manning.hsia.dvdstore.action.OrderBy;
import com.manning.hsia.dvdstore.action.ResultHolder;
import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.SessionInvocationHandler;
import com.manning.hsia.dvdstore.util.TestCase;

public class DisplayActionTest extends TestCase {
	
	@Test(groups="ch06")
	public void testList() throws Exception {
		DisplayAction action = getDisplayAction();
		List<String> results = action.displayAllByMatchingTitle("batman");
		assert 2 == results.size() : "found " + results.size();
		for (String result : results) {
			System.out.println(result);
		}
	}
	
	@Test(groups="ch06")
	public void testResultSize() throws Exception {
		DisplayAction action = getDisplayAction();
		int results = action.displayResultSizeByMatchingTitle("season");
		assert 21 == results: "found " + results;
		ResultHolder resultHolder = action.displayResultsAndTotalByMatchingTitle("season", 1, 5);
		assert 21 == resultHolder.getResultSize(): "found " + results;
		assert 5 == resultHolder.getResults().size(): "found " + resultHolder.getResults().size();
	}
	
	@Test(groups="ch06")
	public void testPagination() throws Exception {
		DisplayAction action = getDisplayAction();
		List<String> results = action.displayAllByMatchingTitle("season", 2, 5);
		for (String result : results) {
			System.out.println(result);
		}
		assert 5 == results.size();
		for (String result : results) {
			System.out.println(result);
		}
	}
	
	@Test(groups="ch06")
	public void testUniqueResult() throws Exception {
		DisplayAction action = getDisplayAction();
		String item = action.displayIMFeelingLuckyByMatchingTitle("batman");
		assert item != null;
	}
	
	@Test(groups="ch06")
	public void testIterate() throws Exception {
		DisplayAction action = getDisplayAction();
		List<String> results = action.displayAllByMatchingTitleUsingCache("batman");
		assert 2 == results.size() : "found " + results.size();
		for (String result : results) {
			System.out.println(result);
		}
	}
	
	@Test(groups="ch06")
	public void testSimpleProjection() throws Exception {
		DisplayAction action = getDisplayAction();
		List<ItemView> results = action.displayProjectionByMatchingTitle("batman");
		assert 2 == results.size() : "found " + results.size();
		for (ItemView result : results) {
			System.out.println( result.getTitle() );
			assert result.getTitle() != null;
			assert result.getEan() != null;
		}
	}
	
	@Test(groups="ch06")
	public void testProjectionWithMetadata() throws Exception {
		DisplayAction action = getDisplayAction();
		List<ItemView> results = action.displayProjectionAndMetadataByMatchingTitle("batman");
		assert 2 == results.size() : "found " + results.size();
		for (ItemView result : results) {
			System.out.println( result.getTitle() );
			assert result.getTitle() != null;
			assert result.getEan() != null;
			assert result.getScore() > 0;
		}
	}
	
	@Test(groups="ch06")
	public void testResultTransformer() throws Exception {
		DisplayAction action = getDisplayAction();
		List<ItemView> results = action.displayProjectionByMatchingTitle("batman");
		assert 2 == results.size() : "found " + results.size();
		for (ItemView result : results) {
			System.out.println( result.getTitle() );
			assert result.getTitle() != null;
			assert result.getEan() != null;
		}
	}
	
	@Test(groups="ch06")
	public void testScroll() throws Exception {
		DisplayAction action = getDisplayAction();
		List<String> results = action.displayMediumResultsByMatchingTitle("notfoundinatitle", 3);
		assert 0 == results.size();
		for (String result : results) {
			System.out.println(result);
		}
		
		results = action.displayMediumResultsByMatchingTitle("batman", 3);
		assert 1 == results.size();
		for (String result : results) {
			System.out.println(result);
		}
		
		results = action.displayMediumResultsByMatchingTitle("harry", 3);
		assert 3 == results.size() : "found " + results.size();
		for (String result : results) {
			System.out.println(result);
		}
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
	
	@Test(groups="ch06")
	public void testSort() throws Exception {
		DisplayAction action = getDisplayAction();
		List<String> results = action.displayAllByMatchingTitleOrderedBy("season", OrderBy.TITLE_THEN_EAN);
		assert 21 == results.size() : "found " + results.size();
		assert results.get(0).compareTo( results.get(1) ) < 0;
		
		results = action.displayAllByMatchingTitleOrderedBy("season", OrderBy.TITLE_THEN_SCORE);
		assert 21 == results.size() : "found " + results.size();
		assert results.get(0).compareTo( results.get(1) ) < 0;
	}
	
	@Test(groups="ch06")
	public void testCriteria() throws Exception {
		DisplayAction action = getDisplayAction();
		List<String> results = action.displayItemAndDistributorByMatchingTitle("season");
		assert 21 == results.size() : "found " + results.size();
		for (String result : results) {
			System.out.println(result);
		}
		
	}
	
	@Test(groups="ch06")
	public void testExplanation() throws Exception {
		DisplayAction action = getDisplayAction();
		Explanation results = action.explainFirstMatchingItem("batman");
		assert results != null;
		System.out.println(results);
	}
	
	private DisplayAction getDisplayAction() {
		DisplayAction action = new DisplayActionImpl();
		return (DisplayAction) Proxy.newProxyInstance(
					this.getClass().getClassLoader(), 
					new Class[] { DisplayAction.class },
					new SessionInvocationHandler(action, factory) );
	}
}
