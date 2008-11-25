package com.manning.hsia.dvdstore;

import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.Query;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import com.manning.hsia.test.SearchTestCase;
import org.testng.annotations.Test;

import java.util.List;

public class TestMultiField extends SearchTestCase {
	String[] titles = new String[]{"The Nun's Story", "Toy Story", "The Philadelphia Story", "Toy Story 2", "Ever After - A Cinderella Story", "Dodgeball - A True Underdog Story", "The Miracle Maker - The Story of Jesus", "Films of Faith Collection", "Dragonfly"};
	String[] descs = new String[]{"", "", "", "", "", "", "", "Fred Zinneman's epic The Nun's Story", "Belief gets us there explains nun Linda Hunt"};

	@Test
	public void testMultiFieldQueryParser() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();

		try {
			buildIndex( session, tx );

			String query0 = "nun";
			String query1 = "story";
			String field0 = "description";
			String field1 = "title";

			String[] fields = new String[]{field0, field1};
			String[] queries = new String[]{query0, query1};
			tx = session.beginTransaction();

			Query query = MultiFieldQueryParser.parse( queries, fields, new StopAnalyzer() );
			System.out.println( query.toString() );

			org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery( query, Dvd.class );
			List<Dvd> results = hibQuery.list();

			assert results.size() == 9 : "incorrect hit count";
			assert results.get( 0 ).getTitle().equals( "Films of Faith Collection" );

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
