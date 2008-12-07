package com.manning.hsia.dvdstore.action;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.engine.DocumentBuilder;

import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.model.Pizza;
import com.manning.hsia.dvdstore.util.SessionHolder;

public class ItemActionImpl implements ItemAction {
	
	public Item loadItem(Integer id) {
		FullTextSession session = SessionHolder.getFullTextSession();
		//straight lookup
		Item itemFromGet = (Item) session.get(Item.class, id);
		
		//criteria query
		Criteria criteriaQuery = session.createCriteria(Item.class)
												.add(Restrictions.idEq(id));
		Item itemFromCriteria = (Item) criteriaQuery.uniqueResult();
		
		//full text query
		TermQuery termQuery = new TermQuery(new Term("id", id.toString() ) );
		Item itemFromFullText = (Item) session.createFullTextQuery(termQuery, Item.class)
												.uniqueResult();
		
		assert itemFromGet != null;
		assert itemFromGet == itemFromCriteria : "Hibernate Core unicity contract";
		assert itemFromCriteria == itemFromFullText : "Hibernate Core and Search unicity contract";
		assert itemFromGet == itemFromFullText : "Hibernate Core and Search unicity contract";
		
		return itemFromFullText;
	}
	
	/**
	 * Example 6.5
	 */
	public List<?> findByTitle(String words) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();  //get the FullTextSession<
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, null);
		
		org.hibernate.Query query = ftSession.createFullTextQuery(luceneQuery);  //create the full-text query
		
		return query.list();
	}
	
	/**
	 * Example 6.6
	 */
	public List<Item> findItemByTitle(String words) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();  //get the FullTextSession<
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		org.hibernate.Query query = ftSession.createFullTextQuery(luceneQuery, Item.class); //list entities to restrict by
		
		@SuppressWarnings("unchecked")
		final List<Item> results = query.list();  //execute it
		return results;
	}

	/**
	 * Example 6.5
	 */
	private org.apache.lucene.search.Query buildLuceneQuery(String words, Class<?> searchedEntity) {
		Analyzer analyzer;
		if (searchedEntity == null) {       //get most appropriate analyzer
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

	private org.apache.lucene.search.BooleanQuery addExclusionClause(
			org.apache.lucene.search.Query userQuery) {
		org.apache.lucene.search.Query filterQuery = new TermQuery( 
				new Term( DocumentBuilder.CLASS_FIELDNAME, Pizza.class.getName() ) 
			);
		org.apache.lucene.search.BooleanQuery luceneQuery = new BooleanQuery();
		luceneQuery.add(userQuery, Occur.MUST);
		luceneQuery.add(filterQuery, Occur.MUST_NOT);
		return luceneQuery;
	}

}
