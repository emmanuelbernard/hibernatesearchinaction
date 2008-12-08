package com.manning.hsia.dvdstore.test;

import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.Search;
import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.model.Drink;
import com.manning.hsia.dvdstore.model.Dvd;

public class InheritanceTest extends com.manning.hsia.dvdstore.util.TestCase {
	@Test(groups="ch03")
	public void inheritance() throws Exception {
		Session s = factory.openSession();
		Transaction tx = s.beginTransaction();
		Dvd dvd = new Dvd();
		dvd.setDescription("A movie about Hobbits");
		dvd.setEan("1234567890123");
		dvd.setTitle("The Lord of the Ring");
		s.save(dvd);
		Drink wine = new Drink();
		wine.setAlcoholicBeverage(true);
		wine.setTitle("Mouton Cadet");
		wine.setDescription("Good and consistent French wine");
		s.save(wine);
		tx.commit();
		
		tx = s.beginTransaction();
		Query luceneQuery = new TermQuery( new Term("alcoholicBeverage", "true") );
		List results = Search.getFullTextSession(s).createFullTextQuery(luceneQuery, Drink.class).list();
		assert results.size() == 1 : "Alcoholic filter failed";
		
		luceneQuery = new TermQuery( new Term("title", "ring") );
		results = Search.getFullTextSession(s).createFullTextQuery(luceneQuery, Dvd.class, Drink.class).list();
		assert results.size() == 1 : "Polymorphic query";
		tx.commit();
		s.close();
	}

}
