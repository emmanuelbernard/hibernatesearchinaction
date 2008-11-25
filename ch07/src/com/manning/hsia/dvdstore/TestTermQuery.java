package com.manning.hsia.dvdstore;

import com.manning.hsia.test.SearchTestCase;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.testng.annotations.Test;

import java.util.List;

public class TestTermQuery extends SearchTestCase {
	String[] descs = new String[]{"he hits the road as a traveling salesman", "he's not a computer salesman",
		"a traveling salesman touting the wave of the future", "transforms into an aggressive, high-risk salesman",
		"a once-successful salesman"};

	@Test
	public void testTermQuery() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();

		try {
			buildIndex( session, tx );

			String userInput = "salesman";

			tx = session.beginTransaction();
			Term term = new Term( "description", userInput );
			TermQuery query = new TermQuery( term );

			System.out.println( query.toString() );

			org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery( query, Dvd.class );
			List<Dvd> results = hibQuery.list();

			assert results.size() == 5 : "incorrect hit count";
			assert results.get( 0 ).getDescription().equals( "he's not a computer salesman" );

			for (Dvd dvd : results) {
				System.out.println( dvd.getDescription() );
			}

			for (Object element : session.createQuery( "from " + Dvd.class.getName() ).list()) session.delete( element );
			tx.commit();
		}
		finally {
			session.close();
		}
	}

	private void buildIndex( FullTextSession session, Transaction tx ) {
		for (int x = 0; x < descs.length; x++) {
			Dvd dvd = new Dvd();
			dvd.setDescription( descs[x] );
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
