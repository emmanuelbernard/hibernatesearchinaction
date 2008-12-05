package com.manning.hsia.dvdstore.test;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.action.Searcher;
import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.SessionHolder;
import com.manning.hsia.dvdstore.util.TestCase;

public class SearchTest extends TestCase {
	
	@Test(groups="ch02")
	public void testSearcher() throws Exception {
		SessionHolder.setSession(factory.openSession());
		
		Searcher searcher  = new Searcher();
		List<Item> results = searcher.search();
		assert 2 == results.size() : "found #" + results.size();
		
		results = searcher.searchMultipleFields();
		assert 2 == results.size() : "found #" + results.size();
		
		SessionHolder.getSession().close();
		SessionHolder.setSession(null);
	}

	@Override
	public void postSetUp() throws Exception {
		Session s = factory.openSession();
		Transaction tx = s.beginTransaction();
	
		Item item = new Item();
		item.setTitle("Batman Begins");
		item.setEan("1234567890123");
		item.setDescription("Batman Begins explores the genese of the super hero...");
		s.persist(item);
		
		item = new Item();
		item.setTitle("Blade");
		item.setEan("1234567890124");
		item.setDescription("One of those super heros. Like Batman he lives at night");
		s.persist(item);
		
		tx.commit();
		s.close();
	}
	
}
