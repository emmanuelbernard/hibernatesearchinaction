package com.manning.hsia.dvdstore.test;

import java.lang.reflect.Proxy;

import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.action.IndexingAction;
import com.manning.hsia.dvdstore.action.IndexingActionImpl;
import com.manning.hsia.dvdstore.util.SessionInvocationHandler;
import com.manning.hsia.dvdstore.util.TestCase;

public class IndexingActionTest extends TestCase {
	
	@Test(groups="ch09")
	public void testIndexAll() {
		IndexingAction action = getIndexingAction();
		factory.getStatistics().setStatisticsEnabled(true);
		factory.getStatistics().clear();
		action.indexAllItems();
		factory.getStatistics().logSummary();
		assert 0 == factory.getStatistics().getEntityFetchCount();
		factory.getStatistics().setStatisticsEnabled(false);
	}
	
	@Test(groups="ch09")
	public void indexAndOptimize() {
		IndexingAction action = getIndexingAction();
		action.reindex();
	}
	
	private IndexingAction getIndexingAction() {
		IndexingAction action = new IndexingActionImpl();
		return (IndexingAction) Proxy.newProxyInstance(
					this.getClass().getClassLoader(), 
					new Class[] { IndexingAction.class },
					new SessionInvocationHandler(action, factory) );
	}
}
