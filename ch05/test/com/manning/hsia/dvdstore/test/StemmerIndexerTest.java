package com.manning.hsia.dvdstore.test;

import java.lang.reflect.Proxy;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.action.StemmerIndexer;
import com.manning.hsia.dvdstore.action.StemmerIndexerImpl;
import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.SessionInvocationHandler;
import com.manning.hsia.dvdstore.util.TestCase;

public class StemmerIndexerTest extends TestCase {
	@Test(groups="ch05")
	public void testList() throws Exception {
		StemmerIndexer action = getStemmerIndexer();
		String result = action.checkStemmingIndex();
		assert result == null : result;
	}
	
	@Override
	public void postSetUp() throws Exception {
		FullTextSession session = Search.getFullTextSession( factory.openSession() );
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			//manual indexing solution OK for small amounts of data
			List results = session.createCriteria( Item.class ).list();
			for (Object entity : results) {
			    session.index( entity ); //index each element
			}
			//commit the index changes
			tx.commit();
		}
		finally {
			session.close();
		}
	}
	
	private StemmerIndexer getStemmerIndexer() {
		StemmerIndexer action = new StemmerIndexerImpl();
		return (StemmerIndexer) Proxy.newProxyInstance(
					this.getClass().getClassLoader(), 
					new Class[] { StemmerIndexer.class },
					new SessionInvocationHandler(action, factory) );
	}
}
