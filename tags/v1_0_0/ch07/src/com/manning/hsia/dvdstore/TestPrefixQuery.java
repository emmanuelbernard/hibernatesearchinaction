package com.manning.hsia.dvdstore;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.PrefixQuery;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import com.manning.hsia.test.SearchTestCase;
import org.testng.annotations.Test;

import java.util.List;

public class TestPrefixQuery extends SearchTestCase {
	String[] titles = new String[]{"Sleepless in Seattle", "Moonlighting - Seasons 1 & 2", "Song of the Sea",
		"he's not a computer salesman", "Friends - The Complete Tenth Season"};

	@Test(groups="ch07")
	public void testPrefixQuery() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();
		buildIndex( session, tx );

		String userInput = "sea";

		tx = session.beginTransaction();
		PrefixQuery query = new PrefixQuery( new Term( "title", userInput ) );
		System.out.println( query.toString() );

		org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery( query, Dvd.class );
		List<Dvd> results = hibQuery.list();

		assert results.size() == 4 : "incorrect hit count";
		for (Dvd dvd : results) {
			assert dvd.getTitle().indexOf( "Sea" ) >= 0;
			System.out.println( dvd.getTitle() );
		}

		for (Object element : session.createQuery( "from " + Dvd.class.getName() ).list()) session.delete( element );
		tx.commit();
		session.close();
	}

	private void buildIndex( FullTextSession session, Transaction tx ) {
		for (int x = 0; x < titles.length; x++) {
			Dvd dvd = new Dvd();
			dvd.setTitle( titles[x] );
			dvd.setId( x );
			session.save( dvd );
		}
		tx.commit();
		session.clear();
	}

	protected Class[] getMappings() {
		return new Class[]{
			Dvd.class
		};
	}
}
