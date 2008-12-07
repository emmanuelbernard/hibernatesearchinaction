package com.manning.hsia.dvdstore.action.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.transform.AliasToBeanResultTransformer;

import com.manning.hsia.dvdstore.action.DisplayAction;
import com.manning.hsia.dvdstore.action.ItemView;
import com.manning.hsia.dvdstore.action.OrderBy;
import com.manning.hsia.dvdstore.action.ResultHolder;
import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.EntityManagerHolder;

public class DisplayActionImpl implements DisplayAction {

	/**
	 * Example 6.9
	 */
	public List<String> displayAllByMatchingTitle(String words) {
		FullTextEntityManager ftEntityManager = EntityManagerHolder.getFullTextEntityManager();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		javax.persistence.Query query = ftEntityManager.createFullTextQuery(luceneQuery, Item.class);
		
		@SuppressWarnings("unchecked")
		List<Item> items = query.getResultList();
		
		List<String> results = new ArrayList<String>();
		for (Item item : items) {
			StringBuilder itemInString = new StringBuilder("Item ")
				.append("(").append(item.getEan()).append(")")
				.append(" ").append(item.getTitle());
			results.add( itemInString.toString() );
		}
		return results;
	}

	/**
	 * Returns the matching results. Each result is represented by a string representation
	 * 
	 * @param words matching words
	 * @param pageNumber number of the displayed page starting from 1
	 * @param window number of elements per page
	 * 
	 * @return list of matching results represented as String
	 * 
	 * Example 6.14
	 */
	public List<String> displayAllByMatchingTitle(String words, int pageNumber, int window) {
		FullTextEntityManager ftEntityManager = EntityManagerHolder.getFullTextEntityManager();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		javax.persistence.Query query = ftEntityManager.createFullTextQuery(luceneQuery, Item.class);
		
		@SuppressWarnings("unchecked")
		List<Item> items = query
				.setFirstResult( (pageNumber - 1) * window )   //set pagination according to window
				.setMaxResults( window )
				.getResultList();
		
		List<String> results = new ArrayList<String>();
		for (Item item : items) {
			StringBuilder itemInString = new StringBuilder("Item ")
				.append("(").append(item.getEan()).append(")")
				.append(" ").append(item.getTitle());
			results.add( itemInString.toString() );
		}
		return results;
	}
	
	/**
	 * Example 6.13
	 */
	public String displayIMFeelingLuckyByMatchingTitle(String words) {
		FullTextEntityManager ftEntityManager = EntityManagerHolder.getFullTextEntityManager();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		javax.persistence.Query query = ftEntityManager.createFullTextQuery(luceneQuery, Item.class);
		
		Query hSearchQuery = query
				.setFirstResult(0).setMaxResults(1);
		
		Item item;
		try {
			item =  (Item) hSearchQuery.getSingleResult();  //return one element
		}
		catch (NoResultException e) {       //guard against no element found
			item = null;
		}
		StringBuilder itemInString = new StringBuilder("Item ");
		if (item == null) {
			itemInString.append("not found");
		}
		else {
			itemInString.append("(").append(item.getEan()).append(")")
			.append(" ").append(item.getTitle());
		}
		return itemInString.toString();
	}
	
	/**
	 * Example 6.15
	 */
	public int displayResultSizeByMatchingTitle(String words) {
		FullTextEntityManager ftEntityManager = EntityManagerHolder.getFullTextEntityManager();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		FullTextQuery query = ftEntityManager.createFullTextQuery(luceneQuery, Item.class);
		
		return query.getResultSize();  //number of matching results (cheap)
	}
	
	/**
	 * Example 6.23
	 */
	public Explanation explainFirstMatchingItem(String words) {
		FullTextEntityManager ftEntityManager = EntityManagerHolder.getFullTextEntityManager();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		FullTextQuery query = ftEntityManager.createFullTextQuery(luceneQuery, Item.class);

		@SuppressWarnings("unchecked")
		Object[] result = (Object[]) query
								.setProjection(
									FullTextQuery.DOCUMENT_ID,  //retrieve the doucment id
									FullTextQuery.THIS)
								.setMaxResults(1)
								.getSingleResult();
		
		return query.explain( (Integer) result[0] ); //explain a given document
	}
	
	private org.apache.lucene.search.Query buildLuceneQuery(String words, Class<?> searchedEntity) {
		Analyzer analyzer;
		if (searchedEntity == null) {    //get the most appropriate analyzer
			analyzer = new StandardAnalyzer();
		}
		else {
			analyzer = EntityManagerHolder.getFullTextEntityManager().getSearchFactory().getAnalyzer(searchedEntity);
		}
		
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
	
	/**
	 * Example 6.20 
	 */
	public List<String> displayAllByMatchingTitleOrderedBy(String words,
			OrderBy orderBy) {
		FullTextEntityManager ftEntityManager = EntityManagerHolder.getFullTextEntityManager();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		FullTextQuery query = ftEntityManager.createFullTextQuery(luceneQuery, Item.class);
		
		Sort sort = null;
		switch (orderBy) {
		case EAN:
		{
			//sort by ean
			SortField sortField = new SortField("ean", SortField.STRING);  //build a SortField
			sort = new Sort(sortField);           //wrap it in a Sort
			break;
		}
		case TITLE_THEN_EAN:
		{
			//sort by title and for equals titles by ean
			SortField[] sortFields = new SortField[2];    //multiple sort fields are possible
			sortFields[0] = new SortField("title_sort", SortField.STRING);
			sortFields[1] = new SortField("ean", SortField.STRING);
			sort = new Sort(sortFields);
			break;
		}
		case TITLE_THEN_SCORE:
		{
			//sort by title and for equals titles by ean
			SortField[] sortFields = new SortField[2];
			sortFields[0] = new SortField("title_sort", SortField.STRING);
			
			//use the special SortField
			sortFields[1] = SortField.FIELD_SCORE;  //sort by score after title
			sort = new Sort(sortFields);
			break;
		}
		default:
			assert sort == null: "Unknown OrderBy." + orderBy;
		}
		
		query.setSort( sort );  //assign Sort to the query
		
		@SuppressWarnings("unchecked")
		List<Item> items = query.getResultList();
		
		List<String> results = new ArrayList<String>();
		for (Item item : items) {
			StringBuilder itemInString = new StringBuilder("Item ")
				.append(item.getTitle())
				.append(" (").append(item.getEan() ).append(")");
			results.add( itemInString.toString() );
		}
		return results;
	}
	
	

	public List<String> displayAllByMatchingTitleUsingCache(String words) {
		//cannot be implemented with JPA
		return null;
	}

	public List<String> displayMediumResultsByMatchingTitle(String words, int n) {
		//cannot be implemented with JPA
		return null;
	}
	
	/**
	 * Example 6/22
	 */
	public List<String> displayItemAndDistributorByMatchingTitle(String words) {
		FullTextEntityManager ftEm = EntityManagerHolder.getFullTextEntityManager();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		FullTextQuery query = ftEm.createFullTextQuery(luceneQuery, Item.class);
		
		final Session session = (Session) ftEm.getDelegate();
		Criteria fetchingStrategy = session.createCriteria(Item.class)  //create criteria on targeted entit
											.setFetchMode("distributor", FetchMode.JOIN); //set fetching profil
		query.setCriteriaQuery(fetchingStrategy);
		
		@SuppressWarnings("unchecked")
		List<Item> items = query.getResultList();
		
		List<String> results = new ArrayList<String>();
		for (Item item : items) {
			StringBuilder itemInString = new StringBuilder("Item ")
				.append("(").append(item.getEan()).append(")")
				.append(" ").append(item.getTitle())
				.append(" - ").append(item.getDistributor().getName());  //use pre loaded association
			results.add( itemInString.toString() );
		}
		return results;
	}

	/**
	 * Example 6.17
	 */
	public List<ItemView> displayProjectionAndMetadataByMatchingTitle(String words) {
		FullTextEntityManager ftEm = EntityManagerHolder.getFullTextEntityManager();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
	
		FullTextQuery query = ftEm.createFullTextQuery(luceneQuery, Item.class);
		
		@SuppressWarnings("unchecked")
		List<Object[]> results = query
				.setProjection(
						"ean", 
						"title", 
						FullTextQuery.SCORE)  //project the document score
				.getResultList();
		
		List<ItemView> endResults = new ArrayList<ItemView>(results.size());
		for (Object[] line : results) {
			ItemView itemView = new ItemView( 
					(String) line[0],    
					(String) line[1], 
					(Float) line[2] );  //retrieve the document score
			endResults.add( itemView );
		}
		return endResults;
	}

	/**
	 * Example 6.16
	 */
	public List<ItemView> displayProjectionByMatchingTitle(String words) {
		FullTextEntityManager ftEm = EntityManagerHolder.getFullTextEntityManager();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
	
		FullTextQuery query = ftEm.createFullTextQuery(luceneQuery, Item.class);
		
		@SuppressWarnings("unchecked")
		List<Object[]> results = query
				.setProjection("ean", "title") //set the projected properties
				.getResultList();
		
		List<ItemView> endResults = new ArrayList<ItemView>(results.size());
		for (Object[] line : results) {
			endResults.add( new ItemView( 
					(String) line[0],    //build object from projection array
					(String) line[1]) );
		}
		return endResults;
	}

	/**
	 * Example 6.19
	 */
	public List<ItemView> displayProjectionUsingResultTransformerByMatchingTitle(String words) {
		FullTextEntityManager ftEm = EntityManagerHolder.getFullTextEntityManager();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		FullTextQuery query = ftEm.createFullTextQuery(luceneQuery, Item.class);
		
		@SuppressWarnings("unchecked")
		List<ItemView> results = query
				.setProjection("ean", "title")
				.setResultTransformer( 
						new AliasToBeanResultTransformer(ItemView.class)  //attach the result transformer 
				) 
				.getResultList();
		return results;
	}

	/**
	 * Example 6.15
	 */
	public ResultHolder displayResultsAndTotalByMatchingTitle(String words, int pageNumber, int window) {
		FullTextEntityManager ftEm = EntityManagerHolder.getFullTextEntityManager();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
	
		FullTextQuery query = ftEm.createFullTextQuery(luceneQuery, Item.class);
		
		@SuppressWarnings("unchecked")
		List<String> results = query
				.setFirstResult( (pageNumber - 1) * window )
				.setMaxResults(window)
				.getResultList();               //return matching results
		
		int resultSize = query.getResultSize();  //return total number of results
		
		ResultHolder holder = new ResultHolder(results, resultSize);
		return holder;
	}
}
