package com.manning.hsia.dvdstore.action;

import java.util.List;

import com.manning.hsia.dvdstore.model.Distributor;
import com.manning.hsia.dvdstore.model.Item;

public interface ItemRetrievalAction {
	List<Item> searchItemWithinDistributor(String search, Distributor distributor);
	List<Item> searchItems(String search, boolean isChild);
	List<Item> searchItemsLowPrice(String search);
	List<Item> searchItems(String search);
	List<Item> searchWithinSearch(String search);
	List<Item> searchWithinStock(String search);
}
