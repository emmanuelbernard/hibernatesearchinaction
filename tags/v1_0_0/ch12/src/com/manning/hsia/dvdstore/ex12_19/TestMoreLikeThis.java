package com.manning.hsia.dvdstore.ex12_19;

import com.manning.hsia.test.ch12.SearchTestCase;
import com.manning.hsia.test.Product;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.reader.ReaderProvider;
import org.hibernate.search.store.DirectoryProvider;
import org.hibernate.search.store.FSDirectoryProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.StringReader;
import java.util.List;

public class TestMoreLikeThis extends SearchTestCase {
	private ReaderProvider readerProvider;

	String likeText =
		"Keanu Reeves is completely wooden in this romantic"
			+ " misfire. Reeves plays a traveling salesman and "
			+ "agrees to help a woman";
	String[] moreLikeFields = new String[]{"description"};

	@Test(groups="ch12")
	public void testMoreLikeThis() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();

		StringReader sr = new StringReader( likeText );
		IndexReader reader = getReader( session );

		try {
			MoreLikeThis mlt = new MoreLikeThis( reader );
			mlt.setBoost( true );
			mlt.setFieldNames( moreLikeFields );
			mlt.setMaxQueryTerms( 2 );
			mlt.setMinDocFreq( 1 );
			mlt.setAnalyzer( new StandardAnalyzer() );
			mlt.setMaxWordLen( 8 );
			mlt.setMinWordLen( 7 );
			mlt.setMinTermFreq( 1 );
			Query query = mlt.like( sr );
			System.out.println( query.toString() );

			org.hibernate.search.FullTextQuery hibQuery =
				session.createFullTextQuery( query, Product.class );
			hibQuery.setProjection( FullTextQuery.DOCUMENT,
				FullTextQuery.SCORE,
				FullTextQuery.DOCUMENT_ID );

			List<Object[]> results = hibQuery.list();

			assert results.size() == 6 : "incorrect result count";
			for (Object[] result : results) {
				Document doc = (Document) result[0];
				assert ( doc.get( "description" )
					.indexOf( "salesman" ) > 0
					|| doc.get( "description" )
					.indexOf( "misfire" ) > 0 );
			}
			if ( readerProvider != null ) {
				readerProvider.closeReader( reader );
			}
			tx.commit();
		}
		finally {
			session.close();
		}
	}

	private IndexReader getReader( FullTextSession session ) {
		SearchFactory searchFactory =
			session.getSearchFactory();
		DirectoryProvider provider =
			searchFactory.getDirectoryProviders( Product.class )[0];
		readerProvider =
			searchFactory.getReaderProvider();
		return readerProvider.openReader( provider );
	}

	@BeforeClass(groups="ch12", alwaysRun=true)
	protected void setUp() throws Exception {
		File sub = locateBaseDir();
		File[] files = sub.listFiles();
		if ( files != null ) {
			for (File file : files) {
				if ( file.isDirectory() ) {
					delete( file );
				}
			}
		}
		copyIndexes();
		buildSessionFactory( getMappings(), getAnnotatedPackages(), getXmlFiles() );
	}

	protected Class[] getMappings() {
		return new Class[]{
			Product.class,
		};
	}

	protected void configure( org.hibernate.cfg.Configuration cfg ) {
		super.configure( cfg );
		cfg.setProperty( "hibernate.search.default.directory_provider", FSDirectoryProvider.class.getName() );
		cfg.setProperty( "hibernate.search.default.indexBase", locateBaseDir().getAbsolutePath() );
	}
}

