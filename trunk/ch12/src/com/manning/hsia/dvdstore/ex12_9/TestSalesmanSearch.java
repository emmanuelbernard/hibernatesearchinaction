package com.manning.hsia.dvdstore.ex12_9;

import com.manning.hsia.test.ch12.SearchTestCase;
import com.manning.hsia.test.Product;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.store.FSDirectoryProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

public class TestSalesmanSearch extends SearchTestCase {
	@Test
	public void searchProduct() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();

		try {
			BooleanQuery query = new BooleanQuery();
			query.add( new BooleanClause(
				new TermQuery( new Term( "description", "spielberg" ) ),
				BooleanClause.Occur.MUST ) );
			query.add( new BooleanClause(
				new TermQuery( new Term( "description", "war" ) ),
				BooleanClause.Occur.SHOULD ) );
			System.out.println( query.toString() );

			org.hibernate.search.FullTextQuery hibQuery =
				session.createFullTextQuery( query, Product.class );
			hibQuery.setProjection( FullTextQuery.DOCUMENT,
				FullTextQuery.SCORE,
				FullTextQuery.DOCUMENT_ID,
				FullTextQuery.EXPLANATION );

			List<Object[]> results = hibQuery.list();

			assert results.size() > 0 : "no results returned";
			for (Object[] result : results) {
				System.out.println( "score => " + result[1] );

				System.out.println( hibQuery
					.explain( (Integer) result[2] ) );
			}
			tx.commit();
		}
		finally {
			session.close();
		}
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
			Product.class
		};
	}

	protected void configure( org.hibernate.cfg.Configuration cfg ) {
		super.configure( cfg );
		cfg.setProperty( "hibernate.search.default.directory_provider", FSDirectoryProvider.class.getName() );
		cfg.setProperty( "hibernate.search.default.indexBase", locateBaseDir().getAbsolutePath() );
		cfg.setProperty( "hibernate.search.similarity", "com.manning.hsia.dvdstore.ex12_9.ScoringTestSimilarity" );
	}
}

