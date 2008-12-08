package com.manning.hsia.dvdstore.action.jpa;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.hibernate.search.engine.DocumentBuilder;
import org.hibernate.search.jpa.FullTextEntityManager;

import com.manning.hsia.dvdstore.action.ItemAction;
import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.model.Pizza;
import com.manning.hsia.dvdstore.util.EntityManagerHolder;
import com.manning.hsia.dvdstore.util.SessionHolder;

public class ItemActionImpl implements ItemAction {
	
	public Item loadItem(Integer id) {
		FullTextEntityManager entityManager = EntityManagerHolder.getFullTextEntityManager();
		//straight lookup
		Item itemFromGet = entityManager.find(Item.class, id);
		
		
		//full text query
		TermQuery termQuery = new TermQuery(new Term("id", id.toString() ) );
		Item itemFromFullText = (Item) entityManager.createFullTextQuery(termQuery, Item.class)
												.getSingleResult();
		
		assert itemFromGet != null;
		assert itemFromGet == itemFromFullText : "Hibernate Core and Search unicity contract";
		
		return itemFromFullText;
	}
	
	public List<?> findByTitle(String words) {
		FullTextEntityManager ftEntityManager = EntityManagerHolder.getFullTextEntityManager();  //get FullTextEntityManager
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, null);
		
		javax.persistence.Query query = ftEntityManager.createFullTextQuery(luceneQuery); //create full-text query
		
		return query.getResultList();
	}
	
	/**
	 * Example 6.6
	 */
	public List<Item> findItemByTitle(String words) {
		FullTextEntityManager ftEntityManager = EntityManagerHolder.getFullTextEntityManager(); //get FullTextEntityManager
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		javax.persistence.Query query = ftEntityManager.createFullTextQuery(
				luceneQuery, 
				Item.class); //list entities to restrict by
		
		@SuppressWarnings("unchecked")
		final List<Item> results = query.getResultList();  //execute it
		return results;
	}

	/**
	 * Example 6.5
	 */
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
	
	/**
	 * Example 6.7
	 */
	public org.apache.lucene.search.Query buildLuceneQueryExcludePizza(String words) {
		QueryParser parser = new QueryParser( "title", new StandardAnalyzer() );
		org.apache.lucene.search.Query userQuery = null;
		try {
			userQuery = parser.parse(words);
		}
		catch (org.apache.lucene.queryParser.ParseException e) {
			throw new IllegalArgumentException("Unable to parse search entry  into a Lucene query", e);
		}
		org.apache.lucene.search.BooleanQuery luceneQuery = addExclusionClause(userQuery);
		
		return luceneQuery;
	}

	/**
	 * Example 6.7
	 */
	private org.apache.lucene.search.BooleanQuery addExclusionClause(
			org.apache.lucene.search.Query userQuery) {
		org.apache.lucene.search.Query filterQuery = new TermQuery( 
				new Term( DocumentBuilder.CLASS_FIELDNAME, Pizza.class.getName() )   //crate filtering term
			);
		org.apache.lucene.search.BooleanQuery luceneQuery = new BooleanQuery();
		luceneQuery.add(userQuery, Occur.MUST);    //join the user query
		luceneQuery.add(filterQuery, Occur.MUST_NOT);  //and exclude the filtering term
		return luceneQuery;
	}
}
