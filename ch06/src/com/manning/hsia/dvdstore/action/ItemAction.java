package com.manning.hsia.dvdstore.action;

import java.util.List;

import com.manning.hsia.dvdstore.model.Item;

public interface ItemAction {

	public abstract Item loadItem(Integer id);

	public abstract List<?> findByTitle(String words);

	public abstract List<Item> findItemByTitle(String words);

}