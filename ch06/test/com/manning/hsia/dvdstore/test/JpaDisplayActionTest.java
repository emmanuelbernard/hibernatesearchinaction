package com.manning.hsia.dvdstore.test;

import java.lang.reflect.Proxy;
import java.util.List;

import javax.persistence.EntityTransaction;

import org.apache.lucene.search.Explanation;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.action.DisplayAction;
import com.manning.hsia.dvdstore.action.ItemView;
import com.manning.hsia.dvdstore.action.OrderBy;
import com.manning.hsia.dvdstore.action.ResultHolder;
import com.manning.hsia.dvdstore.action.jpa.DisplayActionImpl;
import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.EntityManagerInvocationHandler;
import com.manning.hsia.dvdstore.util.JpaTestCase;

public class JpaDisplayActionTest extends JpaTestCase {
	
	@Test(groups="ch06")
	public void testList() throws Exception {
		DisplayAction action = getDisplayAction();
		List<String> results = action.displayAllByMatchingTitle("batman");
		assert 2 == results.size();
		for (String result : results) {
			System.out.println(result);
		}
	}

	private DisplayAction getDisplayAction() {
		DisplayAction action =  new DisplayActionImpl();
		return (DisplayAction) Proxy.newProxyInstance(
				this.getClass().getClassLoader(), 
				new Class[] { DisplayAction.class },
				new EntityManagerInvocationHandler(action, factory) );
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
	public void testUniqueResult() throws Exception {
		DisplayAction action = getDisplayAction();
		String item = action.displayIMFeelingLuckyByMatchingTitle("batman");
		assert item != null;
	}
	
	@Test(groups="ch06")
	public void testPagination() throws Exception {
		DisplayAction action = getDisplayAction();
		List<String> results = action.displayAllByMatchingTitle("season", 2, 5);
		for (String result : results) {
			System.out.println(result);
		}
		assert 5 == results.size(): "found " + results.size();
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
