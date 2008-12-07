package com.manning.hsia.dvdstore.action;

import java.util.Set;

public interface StockAction {
	long geLastUpdateTime();

	Set<String> getEanOfItemsOutOfStock();
}
