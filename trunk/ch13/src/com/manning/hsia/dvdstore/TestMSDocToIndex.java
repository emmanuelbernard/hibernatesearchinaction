package com.manning.hsia.dvdstore;

import com.manning.hsia.test.ch13.SearchTestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class TestMSDocToIndex extends SearchTestCase {
	private Analyzer analyzer = new StandardAnalyzer();

	@Test
	public void testExtractFromWordDoc() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();

		try {
			File f = new File( "ch13/src/com/manning/hsia/dvdstore/file1.doc" );
			buildIndex( f.getAbsolutePath(), session, tx );

			tx = session.beginTransaction();
			QueryParser parser = new QueryParser( "description", analyzer );

			Query query = parser.parse( "description" + ":reeves" );
			org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery( query, Dvd.class );
			List<Dvd> results = hibQuery.list();

			assert results.size() == 2 : "wrong number of results";
			for (Dvd dvd : results) {
				assert dvd.getDescription().indexOf( "Reeves" ) >= 0;
			}

			for (Object element : session.createQuery( "from " + Dvd.class.getName() ).list()) session.delete( element );
			tx.commit();
		}
		finally {
			session.close();
		}
	}

	private void buildIndex( String filename, FullTextSession session, Transaction tx ) throws Exception {
		InputStream istream = new FileInputStream( new File( filename ) );

		WordExtractor extractor = new WordExtractor( istream );
		String[] paragraphs = extractor.getParagraphText();

		for (int x = 0; x < paragraphs.length; x++) {
			Dvd dvd = new Dvd();
			dvd.setDescription( paragraphs[x] );
			dvd.setId( x + 1 );
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
