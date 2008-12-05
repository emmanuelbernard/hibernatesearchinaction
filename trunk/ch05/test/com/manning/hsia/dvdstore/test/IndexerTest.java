package com.manning.hsia.dvdstore.test;

import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.action.Indexer;
import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.TestCase;

public class IndexerTest extends TestCase {
	
	@Test(groups="ch05")
	public void testManualIndex() {
		Indexer indexer = new Indexer(factory);
		FullTextSession s = Search.getFullTextSession( factory.openSession() );
		Transaction tx = s.beginTransaction();
		Query luceneQuery = new TermQuery( new Term("title", "fair") );
		int resultSize = s.createFullTextQuery(luceneQuery, Item.class).getResultSize();
		assert 0 == resultSize;
		tx.commit();
		s.close();
		
		indexer.indexItems();
		
		s = Search.getFullTextSession( factory.openSession() );
		tx = s.beginTransaction();
		@SuppressWarnings("unchecked")
		List<Item> items = s.createFullTextQuery(luceneQuery, Item.class).list();
		resultSize = items.size();
		assert 2 == resultSize : "Items should have been indexed:" + resultSize;
		tx.commit();
		s.close();
		
		indexer.purgeItems(items.get(0).getId(), items.get(1).getId());
		
		s = Search.getFullTextSession( factory.openSession() );
		tx = s.beginTransaction();		
		assert 0 == s.createFullTextQuery(luceneQuery, Item.class).getResultSize(): "items should no longer be there";
		
		Item item1 = (Item) s.get(Item.class, items.get(0).getId());
		Item item2 = (Item) s.get(Item.class, items.get(1).getId());
		tx.commit();
		s.close();
		
		indexer.indexItems(item1, item2);
		
		s = Search.getFullTextSession( factory.openSession() );
		tx = s.beginTransaction();	
		resultSize = s.createFullTextQuery(luceneQuery, Item.class).getResultSize(); 
		assert 2 == resultSize: "items should be back" + resultSize;
		tx.commit();
		s.close();
		
		indexer.purgeItems();
		s = Search.getFullTextSession( factory.openSession() );
		tx = s.beginTransaction();		
		assert 0 == s.createFullTextQuery(luceneQuery, Item.class).getResultSize(): "Everyone should be gone";
		tx.commit();
		s.close();
		
	}
	
}
