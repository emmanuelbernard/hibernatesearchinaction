package com.manning.hsia.dvdstore.action;

import java.util.List;

import com.manning.hsia.dvdstore.model.Item;

public interface SearchingAction {
	List<Item> getMatchingItems(String query, int page);
	List<Item> getMatchingItemsWithDistributor(String query, int page);
	List<String> getTitleFromMatchingItems(String words);
}
