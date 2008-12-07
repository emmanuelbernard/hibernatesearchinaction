package com.manning.hsia.dvdstore.action.filter;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.hibernate.search.annotations.Factory;

/**
 * Example 8.3
 */
public class WarnerDistributorFilterFactory {
	//has a no-arg constructor
	
	@Factory  //factory method
	public Filter buildDistributorFilter() {
		Term term = new Term("distributor.name", "Warner");
		Query query = new TermQuery( term );
		
		Filter filter = new QueryWrapperFilter( query );  //build the query wrapper filter
		
		return filter;
	}
}
