package com.manning.hsia.dvdstore.action;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class StockActionImpl implements StockAction {

	public long geLastUpdateTime() {
		return new Date().getTime();
	}

	public Set<String> getEanOfItemsOutOfStock() {
		Set<String> results = new HashSet<String>();
		results.add("630522577X");
		results.add("B00003CXCD");
		results.add("B00000IQW5");
		results.add("B00011D1OA");
		results.add("B00005ALMH");
		results.add("B00003CXCG");
		return results;
	}

}
