package com.manning.hsia.dvdstore.action.filter;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;

/**
 * Example 8.4,l 8.6
 */
public class DistributorFilterFactory {
	
	private String distributor;
	
	public void setDistributor(String distributor) {  //parameters are injected in setters
		this.distributor = distributor;
	}
	
	@Factory
	public Filter buildDistributorFilter() {
		Term term = new Term("distributor.name", distributor);  //make use of injected parameters
		Query query = new TermQuery( term );
		Filter filter = new QueryWrapperFilter( query );
		return filter;
	}
	
	@Key      //method generating the FilterKey
	public FilterKey getKey() {
		StandardFilterKey key = new StandardFilterKey();  //use the default implementation
		key.addParameter(distributor);  //parameters are available
		return key;
	}
}
