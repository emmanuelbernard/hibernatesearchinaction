package com.manning.hsia.dvdstore;

import com.manning.hsia.test.ch13.SearchTestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.testng.annotations.Test;

import java.util.List;

public class TestXmlBridges extends SearchTestCase {
	private Query query;
	private Analyzer analyzer = new StandardAnalyzer();

	@Test
	public void testSaxXmlBridge() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();
		try {
			buildCDIndex( session, tx );

			tx = session.beginTransaction();
			QueryParser parser = new QueryParser( "title", analyzer );
			query = parser.parse( "burlesque" );
			org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery( query, CD.class );
			hibQuery.setProjection( FullTextQuery.DOCUMENT );

			List<Object[]> results = hibQuery.list();

			assert results.size() == 1 : "incorrect hit count";
			Object[] obj = results.get( 0 );
			Document doc = (Document) obj[0];
			assert doc.get( "title" ).equals( "Empire Burlesque" ) : "incorrect title";
			assert doc.get( "price" ).equals( "10.90" ) : "incorrect price";

			for (Object element : session.createQuery( "from " + CD.class.getName() ).list()) session.delete( element );
			tx.commit();
		}
		finally {
			session.close();
		}
	}

	@Test
	public void testDOMXmlBridge() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();

		try {
			buildCDDOMIndex( session, tx );
			tx = session.beginTransaction();

			QueryParser parser = new QueryParser( "title", analyzer );
			query = parser.parse( "burlesque" );
			org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery( query, CDDOM.class );
			hibQuery.setProjection( FullTextQuery.DOCUMENT );

			List<Object[]> results = hibQuery.list();

			assert results.size() == 1 : "incorrect hit count";
			Object[] obj = results.get( 0 );
			Document doc = (Document) obj[0];
			assert doc.get( "title" ).equals( "Empire Burlesque" ) : "incorrect title";
			assert doc.get( "price" ).equals( "10.90" ) : "incorrect price";

			for (Object element : session.createQuery( "from " + CDDOM.class.getName() ).list()) session.delete( element );
			tx.commit();
		}
		finally {
			session.close();
		}
	}

	private void buildCDIndex( FullTextSession session, Transaction tx ) {
		tx = session.beginTransaction();
		CD cd = new CD();
		cd.setId( 1 );
		cd.setTitle( "Empire Burlesque" );
		cd.setArtist( "Bob Dylan" );
		cd.setPriceData( "<CD YEAR=\"1985\"><COMPANY>Columbia</COMPANY><PRICE>10.90</PRICE></CD>" );
		session.save( cd );

		cd = new CD();
		cd.setId( 2 );
		cd.setTitle( "Hide your heart" );
		cd.setArtist( "Bonnie Tylor" );
		cd.setPriceData( "<CD YEAR=\"1988\"><COMPANY>CBS Records</COMPANY><PRICE>9.90</PRICE></CD>" );
		session.save( cd );

		tx.commit();
	}

	private void buildCDDOMIndex( FullTextSession session, Transaction tx ) {
		tx = session.beginTransaction();
		CDDOM cd = new CDDOM();
		cd.setId( 1 );
		cd.setTitle( "Empire Burlesque" );
		cd.setArtist( "Bob Dylan" );
		cd.setPriceData( "<CD YEAR=\"1985\"><COMPANY>Columbia</COMPANY><PRICE>10.90</PRICE></CD>" );
		session.save( cd );

		cd = new CDDOM();
		cd.setId( 2 );
		cd.setTitle( "Hide your heart" );
		cd.setArtist( "Bonnie Tylor" );
		cd.setPriceData( "<CD YEAR=\"1988\"><COMPANY>CBS Records</COMPANY><PRICE>9.90</PRICE></CD>" );
		session.save( cd );

		tx.commit();
	}

	protected Class[] getMappings() {
		return new Class[]{
			CD.class,
			CDDOM.class
		};
	}
}
