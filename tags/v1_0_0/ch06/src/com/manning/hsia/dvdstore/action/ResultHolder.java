package com.manning.hsia.dvdstore.action;

import java.util.Collections;
import java.util.List;

/**
 * Contains the result list and number of results
 */
public class ResultHolder {
	private final List<String> results;
	private final int resultSize;
	
	public ResultHolder(List<String> results, int resultSize) {
		super();
		this.results = Collections.unmodifiableList(results);
		this.resultSize = resultSize;
	}

	public List<String> getResults() {
		return results;
	}

	public int getResultSize() {
		return resultSize;
	}
	
	
}
