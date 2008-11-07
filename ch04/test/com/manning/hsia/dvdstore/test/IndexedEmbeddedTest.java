package com.manning.hsia.dvdstore.test;

import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.model.Actor;
import com.manning.hsia.dvdstore.model.Country;
import com.manning.hsia.dvdstore.model.Director;
import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.model.Rating;
import com.manning.hsia.dvdstore.util.TestCase;

public class IndexedEmbeddedTest extends TestCase {
	@Test(groups="ch04")
	public void testEmbedded() throws Exception {
		Session session = factory.openSession();
		Item item = new Item();
		item.setDescription("Great DVD");
		item.setEan("123456789012");
		item.setTitle("Great DVD");
		item.setRating( new Rating() );
		item.getRating().setOverall(5);
		item.getRating().setPicture(4);
		item.getRating().setScenario(5);
		item.getRating().setSoundtrack(3);
		Transaction tx = session.beginTransaction();
		session.save(item);
		tx.commit();
		
		session.clear();
		
		tx = session.beginTransaction();
		FullTextSession fts = Search.getFullTextSession(session);
		List results = fts.createFullTextQuery( new TermQuery( new Term("rating.overall", "5" ) ), Item.class ).list();
		assert results.size() == 1;
		fts.delete( results.get(0) );
		tx.commit();
		fts.close();
	}
	
	@Test(groups="ch04")
	public void testCollectionOfEmbedded() throws Exception {
		Session session = factory.openSession();
		Item item = new Item();
		item.setDescription("Great DVD");
		item.setEan("123456789012");
		item.setTitle("Great DVD");
		item.setRating( new Rating() );
		item.getRating().setOverall(5);
		item.getRating().setPicture(4);
		item.getRating().setScenario(5);
		item.getRating().setSoundtrack(3);
		
		Country country = new Country();
		country.setName("Germany");
		item.getDistributedIn().add(country);
		country = new Country();
		country.setName("Italy");
		item.getDistributedIn().add(country);
		
		Transaction tx = session.beginTransaction();
		session.save(item);
		tx.commit();
		
		session.clear();
		
		tx = session.beginTransaction();
		FullTextSession fts = Search.getFullTextSession(session);
		List results = fts.createFullTextQuery( new TermQuery( new Term("distributedIn.name", "italy" ) ), Item.class ).list();
		assert results.size() == 1;
		fts.delete( results.get(0) );
		tx.commit();
		fts.close();
	}
	
	@Test(groups="ch04")
	public void testEntityAssociations() throws Exception {
		Session session = factory.openSession();
		Transaction tx = session.beginTransaction();
		
		Item item = new Item();
		item.setDescription("Great DVD");
		item.setEan("123456789012");
		item.setTitle("Great DVD");
		Director director = new Director();
		director.setName("Emmanuel");
		director.getItems().add(item);
		item.setDirector(director);
		Actor actor = new Actor();
		actor.setName("John");
		session.save(actor);
		item.getActors().add(actor);
		actor.getItems().add(item);
		session.save(item);
		session.save(director);
		tx.commit();
		
		session.clear();
		
		
		tx = session.beginTransaction();
		actor = (Actor) session.get( Actor.class, actor.getId() );
		actor.setName("John Griffin");
		director = (Director) session.get( Director.class, director.getId() );
		director.setName("emmanuel Bernard");
		tx.commit();
		
		session.clear();
		
		tx = session.beginTransaction();
		FullTextSession fts = Search.getFullTextSession(session);
		BooleanQuery query = new BooleanQuery();
		query.add( new TermQuery( new Term("actors.name", "griffin" ) ), BooleanClause.Occur.MUST );
		query.add( new TermQuery( new Term("director.name", "bernard" ) ), BooleanClause.Occur.MUST );
		List results = fts.createFullTextQuery( query, Item.class ).list();
		assert results.size() == 1;
		fts.delete( results.get(0) );
		tx.commit();
		fts.close();
	}
}
