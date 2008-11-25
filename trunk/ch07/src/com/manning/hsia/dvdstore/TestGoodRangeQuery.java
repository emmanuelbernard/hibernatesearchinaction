package com.manning.hsia.dvdstore;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.RangeQuery;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.store.FSDirectoryProvider;
import com.manning.hsia.test.SearchTestCase;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestGoodRangeQuery extends SearchTestCase {
	private static final int INDEX_START = 1000;
	int[] numbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

	@Test
	public void testNumericRangeQuery() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();

		try {
			buildIndex( session, tx );
			Term lower = new Term( "number", "00001" );
			Term upper = new Term( "number", "00003" );

			tx = session.beginTransaction();
			RangeQuery query = new RangeQuery( lower, upper, true );
			System.out.println( query.toString() );

			org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery( query, PaddedNum.class );
			List<PaddedNum> results = hibQuery.list();

			List<Integer> numbers = new ArrayList<Integer>();
			for (PaddedNum num : results) {
				numbers.add( num.getNumber() );
				System.out.println( num.getNumber() );
			}
			assert results.size() == 3 : "incorrect return count";
			assert !numbers.contains( 10 );
			for (Object element : session.createQuery( "from " + PaddedNum.class.getName() ).list())
				session.delete( element );
			tx.commit();
		}
		finally {
			session.close();
		}
	}

	private void buildIndex( FullTextSession session, Transaction tx ) {
		for (int x = INDEX_START; x < numbers.length + INDEX_START; x++) {
			PaddedNum num = new PaddedNum();
			num.setId( x );
			num.setNumber( numbers[x - INDEX_START] );
			session.save( num );
		}
		tx.commit();
		session.clear();
	}

	protected Class[] getMappings() {
		return new Class[]{
			PaddedNum.class
		};
	}

	protected void configure( org.hibernate.cfg.Configuration cfg ) {
		cfg.setProperty( "hibernate.search.default.directory_provider", FSDirectoryProvider.class.getName() );
		File sub = locateBaseDir();
		cfg.setProperty( "hibernate.search.default.indexBase", sub.getAbsolutePath() );
	}
}
