package com.manning.hsia.dvdstore;

import com.manning.hsia.test.ch13.SearchTestCase;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.store.FSDirectoryProvider;
import org.testng.annotations.Test;

import java.io.*;
import java.util.List;

public class TestReadTextFile extends SearchTestCase {
	@Test
	public void testTestFile() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();

		File f = new File( "ch13/src/com/manning/hsia/dvdstore/file1.txt" );
		buildIndex( f.getAbsolutePath(), session, tx );
		tx = session.beginTransaction();

		try {
			Query query = new TermQuery( new Term( "description", "salesman" ) );
			org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery( query, Dvd.class );
			List<Dvd> results = hibQuery.list();

			assert results.size() == 1 : "wrong number of hits";
			assert results.get( 0 ).getDescription().indexOf( "salesman" ) >= 0;

			tx.commit();
		}
		finally {
			session.close();
		}
	}

	private void buildIndex( String filename, FullTextSession session, Transaction tx ) {

		File in = new File( filename );
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try {
			String lineIn;
			reader = new BufferedReader( new FileReader( in ) );
			while (( lineIn = reader.readLine() ) != null) {
				sb.append( lineIn );
			}
			Dvd dvd = new Dvd();
			dvd.setDescription( sb.toString() );
			dvd.setId( 1 );
			session.save( dvd );
			tx.commit();
		}
		catch (FileNotFoundException f) {
			System.out.println( "unable to locate input file" );
		}
		catch (IOException io) {
			io.printStackTrace();
		}
		finally {
			if ( reader != null ) {
				try {
					reader.close();
				}
				catch (IOException io) {
					System.out.println( "unable to close file reader" );
				}
			}
			session.clear();
		}
	}

	protected Class[] getMappings() {
		return new Class[]{
			Dvd.class
		};
	}

	protected void configure( org.hibernate.cfg.Configuration cfg ) {
		super.configure( cfg );
		cfg.setProperty( "hibernate.search.default.directory_provider", FSDirectoryProvider.class.getName() );
		cfg.setProperty( "hibernate.search.default.indexBase", locateBaseDir().getAbsolutePath() );
	}
}
