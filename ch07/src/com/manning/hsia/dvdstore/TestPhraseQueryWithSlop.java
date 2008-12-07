package com.manning.hsia.dvdstore;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.PhraseQuery;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import com.manning.hsia.test.SearchTestCase;
import org.testng.annotations.Test;

import java.util.List;
import java.util.StringTokenizer;

public class TestPhraseQueryWithSlop extends SearchTestCase {
	String[] descs = new String[]{"he hits the road as a traveling salesman", "Star Trek The Next Generation",
		"the fifth season of star trek", "to Star Trek fans everywhere the stellar second season",
		"a once-successful salesman"};

	@Test(groups="ch07")
	public void testSloppyPhraseQuery() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();

		try {
			buildIndex( session, tx );

			String userInput = "star trek season";
			StringTokenizer st = new StringTokenizer( userInput, " " );

			tx = session.beginTransaction();
			PhraseQuery query = new PhraseQuery();
			while (st.hasMoreTokens()) {
				query.add( new Term( "description", st.nextToken() ) );
			}
			query.setSlop( 4 );
			System.out.println( query.toString() );

			org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery( query, Dvd.class );
			List<Dvd> results = hibQuery.list();

			assert results.size() == 2 : "incorrect hit count";
			assert results.get( 0 ).getDescription().equals( "the fifth season of star trek" );

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
