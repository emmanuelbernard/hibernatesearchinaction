package com.manning.hsia.dvdstore.action.filter;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.hibernate.search.annotations.Factory;

/**
 * exclude all items reserved for children
 * 
 * Example 8.8
 */
public class NotAChildFilterFactory {
	@Factory
	public Filter getChildrenFilter() {
		Term term = new Term("childrenOnly", "no");  // use flag info
		Query query = new TermQuery( term );
		
		Filter filter = new QueryWrapperFilter( query );
		
		return filter;
	}
	
	//@Factory
	//alternative implementation
	/**
	 * Example 8.9
	 */
	public Filter getChildrenFilterThroughNegativeQuery() {
		
		Term term = new Term("childrenOnly", "yes");
		Query query = new TermQuery( term );
		
		BooleanQuery totalQuery = new BooleanQuery();
		totalQuery.add(new MatchAllDocsQuery(), Occur.SHOULD );  //add a term matching all documents
		totalQuery.add(query, Occur.MUST_NOT);                   //exclude elements matching a specific term
		
		Filter filter = new QueryWrapperFilter( totalQuery );
		
		return filter;
	}
}
