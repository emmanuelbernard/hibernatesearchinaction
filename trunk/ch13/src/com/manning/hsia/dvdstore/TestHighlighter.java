package com.manning.hsia.dvdstore;

import com.manning.hsia.test.ch13.SearchTestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.reader.ReaderProvider;
import org.hibernate.search.store.DirectoryProvider;
import org.testng.annotations.Test;

import java.io.StringReader;
import java.util.List;

public class TestHighlighter extends SearchTestCase {
	private IndexReader reader;
	private Analyzer analyzer = new StandardAnalyzer();
	private ReaderProvider readerProvider;

	String desc[] = {
		"Keanu Reeves is completely wooden in this romantic misfire. Reeves plays a traveling salesman and agrees to help a woman",
		"Jamie Lee Curtis) finds out that he's not really a salesman and Bill Paxton is a used-car salesman."
	};

	@Test
	public void testSimpleHighLighter() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );

		try {
			 buildIndex( session );

			Transaction tx = session.beginTransaction();
			QueryParser parser = new QueryParser( "description", analyzer );

			Query query = parser.parse( "salesman" );
			query = query.rewrite( reader );
			org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery( query, Dvd.class );
			List<Dvd> results = hibQuery.list();


			Highlighter highlighter = new Highlighter( new QueryScorer( query ) );
			highlighter.setTextFragmenter( new SimpleFragmenter( 20 ) );

			int maxNumFragmentsRequired = 3;

			for (Dvd p : results) {
				String text = p.getDescription();
				TokenStream tokenStream =
					analyzer.tokenStream( "description", new StringReader( text ) );

				String result = highlighter.getBestFragments( tokenStream, text, maxNumFragmentsRequired, " ..." );
				assert result != null : "null result";
				assert result.length() > 0 : "0 length result";
				System.out.println( result );
			}

			readerProvider.closeReader( reader );
			for (Object element : session.createQuery( "from " + Dvd.class.getName() ).list()) session.delete( element );
			tx.commit();
		}
		finally {
			session.close();
		}
	}

	private void buildIndex( FullTextSession session ) throws Exception {
		getReader( session );
		Transaction tx = session.beginTransaction();

		for (int x = 0; x < desc.length; x++) {
			Dvd dvd = new Dvd();
			dvd.setId( x );
			dvd.setDescription( desc[x] );
			session.save( dvd );
		}
		tx.commit();
	}

	private void getReader( FullTextSession session ) {
		SearchFactory searchFactory = session.getSearchFactory();
		DirectoryProvider provider = searchFactory.getDirectoryProviders( Dvd.class )[0];
		readerProvider = searchFactory.getReaderProvider();
		reader = readerProvider.openReader( provider );
	}

	protected Class[] getMappings() {
		return new Class[]{
			Dvd.class
		};
	}
}
