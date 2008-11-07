package com.manning.hsia.dvdstore.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;

import com.manning.hsia.dvdstore.model.Distributor;
import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.SessionHolder;

public class ItemRetrievalActionImpl implements ItemRetrievalAction {
	private static Map<String, Float> boostFactors = new HashMap<String, Float>(2);
	private static String[] fields = new String[] {"title", "description"};
	
	static {
		boostFactors.put("title", 4f);
		boostFactors.put("description", 1f);
	}
	
	/**
	 * Example 8.4, 8.6
	 */
	public List<Item> searchItemWithinDistributor(String search,
			Distributor distributor) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(search);
		
		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		query.enableFullTextFilter("security");  //a filter is activated by name
		query.enableFullTextFilter("distributor") //more than one filter can be activated
				.setParameter("distributor", distributor.getName() );  //pass parameters
		
		@SuppressWarnings("unchecked")
		final List<Item> results = query.list();
		return results;
	}

	private Query buildLuceneQuery(String search) {
		MultiFieldQueryParser parser = new MultiFieldQueryParser(
				fields, 
				new StandardAnalyzer(),
				boostFactors);
		org.apache.lucene.search.Query luceneQuery;
		try {
		    luceneQuery = parser.parse(search);
		}
		catch (ParseException e) {
		    //do something here
		    throw new RuntimeException("Unable to parse query: " + search, e);
		}
		return luceneQuery;
	}

	/**
	 * Example 8.8
	 */
	public List<Item> searchItems(String search, boolean isChild) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(search);

		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		if (!isChild) {
			query.enableFullTextFilter("notachild");  //activate filter
		}
		
		@SuppressWarnings("unchecked")
		final List<Item> results = query.list();
		return results;
	}
	
	/**
	 * Example 8.10
	 */
	public List<Item> searchItemsLowPrice(String search) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(search);
		
		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		query.enableFullTextFilter("maximumprice").setParameter("maxPrice", 15);  //enable filters
		
		@SuppressWarnings("unchecked")
		final List<Item> results = query.list();
		return results;
	}

	/**
	 * Example 8.11
	 */
	private org.apache.lucene.search.Query previousLuceneQuery; //keep previous query around

	/**
	 * Example 8.11
	 */
	public List<Item> searchItems(String search) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(search);
		
		previousLuceneQuery = luceneQuery;
		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		@SuppressWarnings("unchecked")
		final List<Item> results = query.list();
		return results;
	}
	
	/**
	 * Example 8.11
	 */
	public List<Item> searchWithinSearch(String search) {
		if (previousLuceneQuery == null) return searchItems(search);
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(search);

		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		query.enableFullTextFilter("searchWithinSearch")  //pass previous query to filter
				.setParameter("previousQuery", previousLuceneQuery);

		@SuppressWarnings("unchecked")
		final List<Item> results = query.list();
		return results;
	}

	public List<Item> searchWithinStock(String search) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(search);
		
		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);

		query.enableFullTextFilter("stock");

		@SuppressWarnings("unchecked")
		final List<Item> results = query.list();
		return results;
	}

}
