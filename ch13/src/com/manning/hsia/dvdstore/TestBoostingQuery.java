package com.manning.hsia.dvdstore;

import com.manning.hsia.test.Product;
import com.manning.hsia.test.ch13.SearchTestCase;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.reader.ReaderProvider;
import org.hibernate.search.store.DirectoryProvider;
import org.hibernate.search.store.FSDirectoryProvider;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.util.List;

public class TestBoostingQuery extends SearchTestCase {
	public Searcher searcher;

	@Test
	public void testBoostingQuery() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();

		Query positiveQuery =
			new TermQuery( new Term( "description", "salesman" ) );
		Query negativeQuery =
			new TermQuery( new Term( "description", "reeves" ) );
		Query query = new BoostingQuery( positiveQuery, negativeQuery, 0.5F );
		System.out.println( query.toString() );

		org.hibernate.search.FullTextQuery hibQuery =
			session.createFullTextQuery( query, Product.class );
		hibQuery.setProjection( FullTextQuery.DOCUMENT, FullTextQuery.SCORE, FullTextQuery.DOCUMENT_ID );

		try {
			List<Object[]> results = hibQuery.list();

			assert results.size() > 0 : "no reults returned";
			IndexSearcher indexSearcher = getSearcher( session );
			for (Object[] result : results) {
				System.out.println( "score => " + result[1] );

				System.out.println( indexSearcher.explain( query, (Integer) result[2] ).toString() );
			}
			tx.commit();
		}
		finally {
			session.close();
		}
	}

	private IndexSearcher getSearcher( FullTextSession session ) {
		SearchFactory searchFactory = session.getSearchFactory();
		DirectoryProvider provider = searchFactory.getDirectoryProviders( Product.class )[0];
		ReaderProvider readerProvider = searchFactory.getReaderProvider();
		IndexReader reader = readerProvider.openReader( provider );

		return new IndexSearcher( reader );
	}

	@BeforeClass
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
		cfg.setProperty( "hibernate.search.default.directory_provider", FSDirectoryProvider.class.getName() );
		File sub = locateBaseDir();
		cfg.setProperty( "hibernate.search.default.indexBase", sub.getAbsolutePath() );
	}
}
