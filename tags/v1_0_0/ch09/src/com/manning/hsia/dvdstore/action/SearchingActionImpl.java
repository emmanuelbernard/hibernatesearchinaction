package com.manning.hsia.dvdstore.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;

import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.SessionHolder;

public class SearchingActionImpl implements SearchingAction {

	private static final int WINDOW = 20;

	/**
	 * Exaplme 9.5, 9.6
	 */
	public List<Item> getMatchingItems(String words, int page) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words);
		
		
		org.hibernate.Query query = ftSession.createFullTextQuery(luceneQuery, Item.class);  //restrict query
		
		@SuppressWarnings("unchecked") 
		List<Item> results = query
			.setFirstResult( (page - 1) * WINDOW )  //first result
			.setMaxResults( WINDOW )     //number of results
			.list();
		return results;
	}
	
	/**
	 * Example 9.7
	 */
	public List<Item> getMatchingItemsWithDistributor(String words, int page) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words);
		
		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		Criteria criteria = ftSession.createCriteria(Item.class)   //Define fetching strategy
				.setFetchMode("distributor", FetchMode.JOIN);
		
		@SuppressWarnings("unchecked")
		List<Item> results = query
			.setFirstResult( (page - 1) * WINDOW )
			.setMaxResults( WINDOW )
			.setCriteriaQuery(criteria)
			.list();
		return results;
	}
	
	/**
	 * Example 9.8
	 */
	public List<String> getTitleFromMatchingItems(String words) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words);
		
		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		@SuppressWarnings("unchecked")
		List<Object[]> results = query
			.setProjection("title")    //list projected properties
			.list();
		
		List<String> titles = new ArrayList<String>( results.size() );
		for(Object[] objects : results) {  //retrieve arrays of objects
			titles.add( (String) objects[0] );
		}
		return titles;
	}

	private org.apache.lucene.search.Query buildLuceneQuery(String words) {
		Analyzer analyzer = SessionHolder.getFullTextSession().getSearchFactory().getAnalyzer(Item.class);
		QueryParser parser = new QueryParser( "title", analyzer );
		org.apache.lucene.search.Query luceneQuery = null;
		try {
			luceneQuery = parser.parse(words);
		}
		catch (org.apache.lucene.queryParser.ParseException e) {
			throw new IllegalArgumentException("Unable to parse search entry  into a Lucene query", e);
		}
		return luceneQuery;
	}

}
