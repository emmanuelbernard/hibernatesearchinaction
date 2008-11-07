package com.manning.hsia.dvdstore.test;

import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.Search;
import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.model.Person;
import com.manning.hsia.dvdstore.model.PersonPK;
import com.manning.hsia.dvdstore.util.TestCase;

public class BridgeUseTest extends TestCase {
	@Test(groups="ch04")
	public void testCompositeIdBridge() throws Exception {
		PersonPK emmanuelPK = new PersonPK();
		emmanuelPK.setFirstName( "Emmanuel" );
		emmanuelPK.setLastName( "Bernard" );
		Person emmanuek = new Person();
		emmanuek.setAge( 29 );
		emmanuek.setId( emmanuelPK );
		Session s = factory.openSession();
		Transaction tx = s.beginTransaction();
		s.save(emmanuek);
		tx.commit();
		
		s.clear();
		
		tx = s.beginTransaction();
		List results = Search.getFullTextSession( s ).createFullTextQuery(
				new TermQuery( new Term("id.lastName", "Bernard" ) ) ).list();
		assert 1 == results.size();
		emmanuek = (Person) results.get(0);
		emmanuek.setAge(30);
		tx.commit();
		s.clear();
		
		tx = s.beginTransaction();
		results = Search.getFullTextSession( s ).createFullTextQuery(
				new TermQuery( new Term("id.lastName", "Bernard" ) ) ).list();
		assert 1 == results.size();
		emmanuek = (Person) results.get(0);
		assert 30 == emmanuek.getAge();
		
		s.delete( results.get( 0 ) );
		tx.commit();
		s.close();
	}
}
