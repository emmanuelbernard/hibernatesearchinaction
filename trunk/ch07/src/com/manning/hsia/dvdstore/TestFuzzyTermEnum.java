package com.manning.hsia.dvdstore;

import com.manning.hsia.test.SearchTestCase;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyTermEnum;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.reader.ReaderProvider;
import org.hibernate.search.store.DirectoryProvider;
import org.testng.annotations.Test;

public class TestFuzzyTermEnum extends SearchTestCase {
	String[] titles = new String[]{"Titan A.E.", "Little Women", "Little Shop of Horrors",
		"The Green Mile", "Somewhere in Time"};

	@Test
	public void testFuzzyQueryEnum() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();
		buildIndex( session, tx );
		String userInput = "title";

		tx = session.beginTransaction();
		FuzzyTermEnum termEnum = new FuzzyTermEnum( getReader( session ), new Term( "title", userInput ), 0.4F );

		try {
			System.out.println( termEnum.term().text() );
			while (termEnum.next()) {
				System.out.println( termEnum.term().text() );
			}
			for (Object element : session.createQuery( "from " + Dvd.class.getName() ).list()) session.delete( element );
			tx.commit();
		}
		finally {
			if ( termEnum != null ) {
				termEnum.close();
			}
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

	private IndexReader getReader( FullTextSession session ) {
		SearchFactory searchFactory = session.getSearchFactory();
		DirectoryProvider provider = searchFactory.getDirectoryProviders( Dvd.class )[0];
		ReaderProvider readerProvider = searchFactory.getReaderProvider();
		return readerProvider.openReader( provider );
	}

	protected Class[] getMappings() {
		return new Class[]{
			Dvd.class
		};
	}
}
