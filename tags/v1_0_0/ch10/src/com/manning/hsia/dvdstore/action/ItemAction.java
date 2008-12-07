package com.manning.hsia.dvdstore.action;

import com.manning.hsia.dvdstore.model.Distributor;
import com.manning.hsia.dvdstore.model.Item;

public interface ItemAction {
	void addNewItem(Item item);
	Distributor getDistributor(Integer id);
}
