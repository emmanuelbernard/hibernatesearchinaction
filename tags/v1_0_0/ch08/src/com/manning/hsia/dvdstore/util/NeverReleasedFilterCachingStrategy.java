package com.manning.hsia.dvdstore.util;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.search.Filter;
import org.hibernate.search.filter.FilterCachingStrategy;
import org.hibernate.search.filter.FilterKey;

public class NeverReleasedFilterCachingStrategy implements
		FilterCachingStrategy {
	private java.util.Map<FilterKey, Filter> cache = new ConcurrentHashMap<FilterKey, Filter>();

	public void initialize(Properties properties) {
	}
	
	public void addCachedFilter(FilterKey key, Filter filter) {
		cache.put(key, filter);
	}

	public Filter getCachedFilter(FilterKey key) {
		return cache.get(key);
	}
}
