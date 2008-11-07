package com.manning.hsia.dvdstore.test;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.action.Indexer;
import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.EntityManagerHolder;
import com.manning.hsia.dvdstore.util.JpaTestCase;
import com.manning.hsia.dvdstore.util.SessionHolder;
import com.manning.hsia.dvdstore.util.TestCase;

public class JpafIndexerTest extends JpaTestCase {
	
	@Test(groups="ch02")
	public void testIndexer() throws Exception {
		EntityManagerHolder.setEntityManager(factory.createEntityManager());
		
		Indexer indexing  = new Indexer();
		indexing.indexWithJPA();
		
		EntityManagerHolder.getEntityManager().close();
		EntityManagerHolder.setEntityManager(null);
	}

	@Override
	public void postSetUp() throws Exception {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
	
		Item item = new Item();
		item.setTitle("Batman Begins");
		item.setEan("1234567890123");
		item.setDescription("Batman Begins explores the genese of the super hero...");
		
		em.persist(item);
		
		em.getTransaction().commit();
		em.close();
	}
	
	

}
