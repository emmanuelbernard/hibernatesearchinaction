package com.manning.hsia.dvdstore.action;

import java.util.List;

import org.apache.lucene.search.Explanation;

public interface DisplayAction {

	public List<String> displayAllByMatchingTitle(String words);

	public List<String> displayAllByMatchingTitle(String words,
			int pageNumber, int window);

	public String displayIMFeelingLuckyByMatchingTitle(String words);

	public List<String> displayAllByMatchingTitleUsingCache(
			String words);

	public List<String> displayMediumResultsByMatchingTitle(
			String words, int n);

	public int displayResultSizeByMatchingTitle(String words);

	public ResultHolder displayResultsAndTotalByMatchingTitle(
			String words, int pageNumber, int window);

	public List<ItemView> displayProjectionByMatchingTitle(String words);
	
	public List<ItemView> displayProjectionAndMetadataByMatchingTitle(String words);
	
	public List<ItemView> displayProjectionUsingResultTransformerByMatchingTitle(String words);
	
	public List<String> displayAllByMatchingTitleOrderedBy(String words, OrderBy orderBy);
	
	public List<String> displayItemAndDistributorByMatchingTitle(String words);
	
	public Explanation explainFirstMatchingItem(String words);

}