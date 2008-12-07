package com.manning.hsia.dvdstore.action;

public interface IndexingAction {
	void indexAllItems();
	void optimize();
	void optimize(Class<?> clazz);
	void reindex();
}
