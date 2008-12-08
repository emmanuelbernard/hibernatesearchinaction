package com.manning.hsia.dvdstore;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.search.Query;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import com.manning.hsia.test.SearchTestCase;
import org.testng.annotations.Test;

import java.util.List;

public class TestFactoryMethod extends SearchTestCase {
	String[] titles = new String[]{"The Ice Storm", "The Nun's Story", "Toy Story", "The Philadelphia Story",
		"Toy Story 2", "Ever After - A Cinderella Story", "Dodgeball - A True Underdog Story",
		"The Miracle Maker - The Story of Jesus"};

	@Test(groups="ch07")
	public void testFactoryMethod() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();

		try {
			buildIndex( session, tx );
			String userInput = "sturm";
			Analyzer analyzer = new StopAnalyzer();
			MyQueryParser parser = new MyQueryParser( "title", analyzer );
			tx = session.beginTransaction();

			Query query = parser.getFuzzyQuery( "title", userInput, 0.5F );
			System.out.println( query.toString() );
			org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery( query, Dvd.class );
			List<Dvd> results = hibQuery.list();

			assert results.size() == 8 : "incorrect hit count";
			assert results.get( 0 ).getTitle().equals( "The Ice Storm" );

			for (Dvd dvd : results) {
				System.out.println( dvd.getTitle() );
			}

			for (Object element : session.createQuery( "from " + Dvd.class.getName() ).list()) session.delete( element );
			tx.commit();
		}
		finally {
			session.close();
		}
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
