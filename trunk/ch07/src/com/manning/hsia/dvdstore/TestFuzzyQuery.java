package com.manning.hsia.dvdstore;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import com.manning.hsia.test.SearchTestCase;
import org.testng.annotations.Test;

import java.util.List;

public class TestFuzzyQuery extends SearchTestCase {
	String[] titles = new String[]{"Titan A.E.", "Little Women", "Little Shop of Horrors",
		"The Green Mile", "Somewhere in Time"};

	@Test
	public void testFuzzyQuery() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();

		buildIndex( session, tx );
		String userInput = "title";

		tx = session.beginTransaction();
		FuzzyQuery query =
			new FuzzyQuery( new Term( "title", userInput ), 0.4F );
		System.out.println( query.toString() );

		org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery( query, Dvd.class );
		List<Dvd> results = hibQuery.list();

		assert results.size() == 5 : "incorrect hit count";
		assert results.get( 0 ).getTitle().equals( "Titan A.E." );

		for (Dvd dvd : results) {
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
