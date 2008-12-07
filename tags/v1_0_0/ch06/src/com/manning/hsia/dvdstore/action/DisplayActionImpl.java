package com.manning.hsia.dvdstore.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.ScrollableResults;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.transform.AliasToBeanResultTransformer;

import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.SessionHolder;

public class DisplayActionImpl implements DisplayAction {
	
	/**
	 * Example 6.9
	 */
	public List<String> displayAllByMatchingTitle(String words) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		org.hibernate.Query query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		@SuppressWarnings("unchecked")
		List<Item> items = query.list();  //return a list of items
		
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
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		org.hibernate.Query query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		@SuppressWarnings("unchecked")
		List<Item> items = query
				.setFirstResult( (pageNumber - 1) * window )  //set first result from the page
				.setMaxResults( window )          //set number of results
				.list();
	
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
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		org.hibernate.Query query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		Item item =  (Item) query
				.setFirstResult(0).setMaxResults(1)  //use pagination to return one result
				.uniqueResult();  //return one element
		
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
	 * Example 6.10
	 */
	public List<String> displayAllByMatchingTitleUsingCache(String words) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		org.hibernate.Query query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		@SuppressWarnings("unchecked")
		Iterator<Item> items = query.iterate();  //retrieve an iterator on items
		
		List<String> results = new ArrayList<String>();
		while ( items.hasNext() ) {
			Item item = items.next();          //load object from the persistence context
			StringBuilder itemInString = new StringBuilder("Item ")
				.append("(").append(item.getEan()).append(")")
				.append(" ").append(item.getTitle());
			results.add(itemInString.toString());
		}
		return results;
	}
	
	/**
	 * Display results starting from the middle of the list up to n elements
	 * 
	 * Example 6.11
	 */
	public List<String> displayMediumResultsByMatchingTitle(String words, int n) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
	
		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		query.setFetchSize(n);   //define fetch size
		ScrollableResults items = query.scroll();  //retrieve a ScrollableResults
		
		List<String> results = new ArrayList<String>();
		try {
			items.beforeFirst();    //go to the first position
			//get the jump to the position before the medium element
			int mediumIndexJump = query.getResultSize() / 2;
		
			items.scroll(mediumIndexJump);  //jump to a specific position
			
			int index = 0;
			while(index < n) {
				if ( items.next() ) {                //load the next element
					Item item = (Item) items.get()[0];  //read the object
					if ( item != null ) {               
						StringBuilder itemInString = new StringBuilder("Item ")
							.append("(").append(item.getEan()).append(")")
							.append(" ").append(item.getTitle());
						results.add(itemInString.toString());
						index++;
					}
					else {
						//mismatch between the index and the database: ignore null entries
					}
				}
				else {
					break;
				}
			}
		}
		finally {
			items.close();   //close resources
		}
		return results;
	}

	/**
	 * Example 6.15
	 */
	public int displayResultSizeByMatchingTitle(String words) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		return query.getResultSize(); //nbumber of matching results (cheap)
	}

	/**
	 * Example 6.15
	 */
	public ResultHolder displayResultsAndTotalByMatchingTitle(String words, int pageNumber, int window) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
	
		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		@SuppressWarnings("unchecked")
		List<String> results = query
				.setFirstResult( (pageNumber - 1) * window )
				.setMaxResults(window)
				.list();               //return matching results
		
		int resultSize = query.getResultSize();  //return total number of results
		
		ResultHolder holder = new ResultHolder(results, resultSize);
		return holder;
	}
	
	/**
	 * Example 6.16
	 */
	public List<ItemView> displayProjectionByMatchingTitle(String words) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
	
		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		@SuppressWarnings("unchecked")
		List<Object[]> results = query
				.setProjection("ean", "title") //set the projected properties
				.list();
		
		List<ItemView> endResults = new ArrayList<ItemView>(results.size());
		for (Object[] line : results) {
			endResults.add( new ItemView( 
					(String) line[0],    //build object from projection array
					(String) line[1]) );
		}
		return endResults;
	}
	
	/**
	 * Example 6.17
	 */
	public List<ItemView> displayProjectionAndMetadataByMatchingTitle(String words) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
	
		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		@SuppressWarnings("unchecked")
		List<Object[]> results = query
				.setProjection(
						"ean", 
						"title", 
						FullTextQuery.SCORE)  //project the document score
				.list();
		
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
	 * Example 6.19
	 */
	public List<ItemView> displayProjectionUsingResultTransformerByMatchingTitle(String words) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		@SuppressWarnings("unchecked")
		List<ItemView> results = query
				.setProjection("ean", "title")
				.setResultTransformer( 
						new AliasToBeanResultTransformer(ItemView.class)  //attach the result transformer 
				) 
				.list();
		return results;
	}

	/**
	 * Example 6.20 
	 */
	public List<String> displayAllByMatchingTitleOrderedBy(String words, OrderBy orderBy) {
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		FullTextSession ftSession = SessionHolder.getFullTextSession();
	
		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		Sort sort = null;
		
		switch (orderBy) {
		case EAN:
		{
			//sort by ean
			SortField sortField = new SortField("ean", SortField.STRING);  //build a SortField
			sort = new Sort(sortField);           //wrap it in a sort
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
		
		query.setSort( sort );    //assign Sort to the query
		
		@SuppressWarnings("unchecked")
		List<Item> items = query.list();
		
		List<String> results = new ArrayList<String>();
		for (Item item : items) {
			StringBuilder itemInString = new StringBuilder("Item ")
				.append(item.getTitle())
				.append(" (").append(item.getEan() ).append(")");
			results.add( itemInString.toString() );
		}
		return results;
	}
	
	public List<String> displayItemAndDistributorByMatchingTitle(String words) {
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		
		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		Criteria fetchingStrategy = ftSession.createCriteria(Item.class)  //create criteria on targeted entit
											.setFetchMode("distributor", FetchMode.JOIN); //set fetching profil
		query.setCriteriaQuery(fetchingStrategy);
		
		@SuppressWarnings("unchecked")
		List<Item> items = query.list();
		
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
	 * Example 6.23
	 */
	public Explanation explainFirstMatchingItem(String words) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);

		@SuppressWarnings("unchecked")
		Object[] result = (Object[]) query
								.setProjection(
									FullTextQuery.DOCUMENT_ID, //retrieve the docuemnt id
									FullTextQuery.THIS)
								.setMaxResults(1)
								.uniqueResult();
		
		return query.explain( (Integer) result[0] ); //explain a given document
	}

	private org.apache.lucene.search.Query buildLuceneQuery(String words, Class<?> searchedEntity) {
		Analyzer analyzer;
		if (searchedEntity == null) {    //get the most appropriate analyzer
			analyzer = new StandardAnalyzer();
		}
		else {
			analyzer = SessionHolder.getFullTextSession().getSearchFactory().getAnalyzer(searchedEntity);
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

}
