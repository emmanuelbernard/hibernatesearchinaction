package com.manning.hsia.dvdstore.action;


public interface BatchChangeAction {
	void applyBatchChange(String words);
	
	String getUrl(String words);
}
