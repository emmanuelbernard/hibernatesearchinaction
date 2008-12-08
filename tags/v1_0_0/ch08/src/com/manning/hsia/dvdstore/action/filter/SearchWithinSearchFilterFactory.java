package com.manning.hsia.dvdstore.action.filter;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.hibernate.search.annotations.Factory;

/**
 * Example 8.11
 */
public class SearchWithinSearchFilterFactory {
	private Query previousQuery;

	public void setPreviousQuery(Query previousQuery) {
		this.previousQuery = previousQuery;
	}
	
	@Factory
	public Filter getSearchWithinSearch() {
		return new QueryWrapperFilter(previousQuery); //wrap previous query in filter
	}
}
