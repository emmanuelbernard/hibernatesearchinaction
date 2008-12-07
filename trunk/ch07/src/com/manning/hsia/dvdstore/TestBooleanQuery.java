package com.manning.hsia.dvdstore;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import com.manning.hsia.test.SearchTestCase;
import org.testng.annotations.Test;

import java.util.List;

public class TestBooleanQuery extends SearchTestCase {
	String[] titles = new String[]{"The Nun's Story", "Toy Story", "The Philadelphia Story", "Toy Story 2",
		"Ever After - A Cinderella Story", "Dodgeball - A True Underdog Story", "The Miracle Maker - The Story of Jesus",
		"The Office - Season One", "Gargoyles - Season Two, Vol. 1"};

	@Test(groups="ch07")
	public void testBooleanQuery1() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();

		try {
			buildIndex( session, tx );
			String required = "season";
			String optional = "story";
			String omitted = "complete";

			Term requiredTerm = new Term( "title", required );
			Term optionalTerm = new Term( "title", optional );
			Term omittedTerm = new Term( "title", omitted );

			tx = session.beginTransaction();

			BooleanClause requiredClause =
				new BooleanClause( new TermQuery( requiredTerm ), BooleanClause.Occur.MUST );

			BooleanQuery query = new BooleanQuery();
			query.add( requiredClause );
			query.add( new TermQuery( optionalTerm ), BooleanClause.Occur.SHOULD );
			query.add( new TermQuery( omittedTerm ), BooleanClause.Occur.MUST_NOT );
			System.out.println( query.toString() );

			org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery( query, Dvd.class );
			List<Dvd> results = hibQuery.list();

			assert results.size() == 2 : "incorrect hit count";
			assert results.get( 0 ).getTitle().equals( "The Office - Season One" );

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
