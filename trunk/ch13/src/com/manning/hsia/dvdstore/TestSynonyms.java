package com.manning.hsia.dvdstore;

import com.manning.hsia.ch13.Synonym;
import com.manning.hsia.test.ch13.SearchTestCase;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.store.FSDirectoryProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Set;

public class TestSynonyms  extends SearchTestCase {
	String desc[] = {
		"Keanu Reeves is completely wooden in this romantic misfired flick",
		"Reeves plays a traveling salesman and agrees to help a woman",
		"Jamie Lee Curtis finds out that he's not really a used car salesman"
	};

	@Test(groups="ch13")
	public void testQuery() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.getTransaction();
		SynonymHelper helper = new SynonymHelper();

		// to build the synonym index from scratch
		// uncomment the following line
//		 buildSynonymIndex(session, helper);

		// test synonyms
		String query = "movie flick";
		Set<String> q = helper.getSynonyms( query, session, new StandardAnalyzer() );

		assert q.contains( "film" ) : "not found";
		assert q.contains( "picture" ) : "not found";

		try {
			// test a query
			buildDvdIndex( session, tx );
			tx = session.beginTransaction();
			query = "automobile";
			Query expandedQuery =
				helper.expandQuery( query, session, new StandardAnalyzer(), "description", 1.0F );

			org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery( expandedQuery, Dvd.class );
			List<Dvd> results = hibQuery.list();

			assert results.size() == 1 : "didn't find the synonym";
			assert results.get( 0 ).getDescription().startsWith( "Jamie Lee Curtis" );
			assert results.get( 0 ).getDescription().indexOf( "car" ) >= 0;

			for (Object element : session.createQuery( "from " + Dvd.class.getName() ).list()) {
				session.delete( element );
			}

			// uncommenting the following lines will remove all
			// entries in the synonym index and require it to be
			// rebuilt
			//	for (Object element : session.createQuery("from " + Synonym.class.getName()).list()) {
			//		session.delete(element);
			//	}
			tx.commit();
		}
		finally {
			session.close();
		}
	}

	private void buildSynonymIndex( FullTextSession session, SynonymHelper helper ) throws Exception {
		helper.buildSynonymIndex( session, "wn_s.pl" );
	}

	private void buildDvdIndex( FullTextSession session, Transaction tx ) {
		tx = session.beginTransaction();
		for (int x = 0; x < desc.length; x++) {
			Dvd dvd = new Dvd();
			dvd.setDescription( desc[x] );
			dvd.setId( x + 1 );
			session.save( dvd );
		}
		tx.commit();
		session.clear();
	}

	protected Class[] getMappings() {
		return new Class[]{
			Dvd.class,
			Synonym.class
		};
	}

	protected void configure( org.hibernate.cfg.Configuration cfg ) {
		cfg.setProperty( "hibernate.search.default.directory_provider",
			FSDirectoryProvider.class.getName() );
		File f = new File( "synonym_index" );
		cfg.setProperty( "hibernate.search.default.indexBase",
			f.getAbsolutePath() );
	}
}
