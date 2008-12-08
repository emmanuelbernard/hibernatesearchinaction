package com.manning.hsia.dvdstore;

import com.manning.hsia.test.ch13.SearchTestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TestPDFTextExtractor extends SearchTestCase {
	InputStream istream = null;
	private Analyzer analyzer = new StandardAnalyzer();

	@Test(groups="ch13")
	public void testPDFExtractor() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();

		PDDocument doc;
		try {
			File f = new File( "ch13/src/com/manning/hsia/dvdstore/file1.pdf" );
			istream = new FileInputStream( f.getAbsolutePath() );

			PDFParser p = new PDFParser( istream );
			p.parse();
			doc = p.getPDDocument();

			Pdf pdf = getDocument( doc );
			closeInputStream( istream );
			closeDocument( doc );
			pdf.setId(1);
			buildIndex( pdf, session, tx );

			tx = session.beginTransaction();
			QueryParser parser = new QueryParser( "description", analyzer );

			Query query = parser.parse( "description:salesman" );
			org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery( query, Pdf.class );
			List results = hibQuery.list();
			assert results.size() == 1 : "incorrect result size";
			Pdf result = (Pdf) results.get( 0 );
			assert result.getAuthor().startsWith( "John Griffin" ) : "incorrect author";
			assert result.getDescription().startsWith( "Keanu Reeves" ) : "incorrect description";

			for (Object element : session.createQuery( "from " + Pdf.class.getName() ).list()) {
				session.delete( element );
			}
			tx.commit();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {

			session.close();
		}
	}

	private Pdf getDocument( PDDocument pd ) {
		String description;
		try {
			PDFTextStripper stripper = new PDFTextStripper();
			description = stripper.getText( pd );
		}
		catch (IOException e) {
			closeDocument( pd );
			throw new PDFExtractorException( "unable to extract text", e );
		}
		PDDocumentInformation info = pd.getDocumentInformation();
		String author = info.getAuthor();
		String title = info.getTitle();
		String keywords = info.getKeywords();
		String subject = info.getSubject();

		Pdf doc = new Pdf();
		doc.setDescription( description );
		doc.setAuthor( author );
		doc.setTitle( title );
		doc.setKeywords( keywords );
		doc.setSubject( subject );

		return doc;
	}

	private void buildIndex( Pdf doc, FullTextSession session, Transaction tx ) {
		session.save( doc );
		tx.commit();
		session.clear();
	}

	private void closeDocument( PDDocument pd ) {
		try {
			if ( pd != null ) {
				pd.close();
			}
		}
		catch (IOException e) {
			// live with it
		}
	}

	private static void closeInputStream( InputStream istream ) {
		if ( istream != null ) {
			try {
				istream.close();
			}
			catch (IOException e) {
				System.out.printf( "unable to close file input stream" );
			}
		}
	}

	public class PDFExtractorException extends RuntimeException {
		public PDFExtractorException( String msg, Throwable e ) {
			super( msg, e );
		}
	}

	protected Class[] getMappings() {
		return new Class[]{
			Pdf.class
		};
	}
}
