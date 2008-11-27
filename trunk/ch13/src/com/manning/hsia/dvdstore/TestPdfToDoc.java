package com.manning.hsia.dvdstore;

import com.manning.hsia.test.ch13.SearchTestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.store.FSDirectoryProvider;
import org.pdfbox.searchengine.lucene.LucenePDFDocument;
import org.testng.annotations.Test;

import java.io.*;
import java.util.List;

// this is testLuceneToPdfDoc
public class TestPdfToDoc extends SearchTestCase {
	private Analyzer analyzer = new StandardAnalyzer();

	@Test
	public void testPdfToDoc() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();

		File f = new File( "ch13/src/com/manning/hsia/dvdstore/file1.pdf" );

		buildIndex( f.getAbsolutePath(), session, tx );
		tx = session.beginTransaction();

		try {
			QueryParser parser = new QueryParser( "description", analyzer );
			Query query = parser.parse( "description" + ":salesman" );

			org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery( query, Pdf.class );
			List<Pdf> results = hibQuery.list();

			assert results.size() == 1 : "incorrect result size";
			Pdf result = results.get( 0 );
			assert result.getAuthor().startsWith( "John Griffin" ) : "incorrect author";
			assert result.getDescription().startsWith( "Keanu Reeves" ) : "incorrect description";

			for (Object element : session.createQuery( "from " + Pdf.class.getName() )
				.list()) {
				session.delete( element );
			}
			tx.commit();
		}
		finally {
			session.close();
		}
	}

	private void buildIndex( String filename, FullTextSession session, Transaction tx ) {
//		session = Search.getFullTextSession( openSession() );
		Document doc = getDocument( filename );
		Pdf pdf = getPdf( doc );
		pdf.setId( 1 );
		session.save( pdf );
		tx.commit();
		session.clear();
	}

	private Document getDocument( String filename ) {
		Document doc;
		InputStream istream;
		File file = new File( filename );
		LucenePDFDocument pdf = new LucenePDFDocument();
		try {
			istream = new FileInputStream( file );
			doc = pdf.convertDocument( istream );
		}
		catch (Exception e) {
			throw new PDFExtractorException( "unable to create document", e );
		}
		return doc;
	}

	private Pdf getPdf( Document doc ) {
		Pdf pdf = new Pdf();
		pdf.setAuthor( doc.get( "Author" ) );
		pdf.setKeywords( doc.get( "Keywords" ) );
		pdf.setSubject( doc.get( "Subject" ) );
		pdf.setTitle( doc.get( "Title" ) );
		pdf.setSummary( doc.get( "summary" ) );
		pdf.setContents( getContents( doc.getField( "contents" ) ) );
		pdf.setDescription( pdf.getContents() );
		return pdf;
	}

	private String getContents( Field field ) {
		StringReader reader = (StringReader) field.readerValue();
		BufferedReader br = new BufferedReader( reader );
		String in;
		StringBuilder sb = new StringBuilder();
		try {
			while (( in = br.readLine() ) != null) {
				sb.append( in );
			}
		}
		catch (IOException e) {
			System.out.println( "unable to retrieve contents field" );
		}
		finally {
			try {
				br.close();
			}
			catch (IOException e) {
				// Live with it.
			}
		}
		return sb.toString();
	}

	protected Class[] getMappings() {
		return new Class[]{
			Pdf.class
		};
	}

	public class PDFExtractorException extends RuntimeException {
		public PDFExtractorException( String msg, Throwable e ) {
			super( msg, e );
		}
	}

	protected void configure( org.hibernate.cfg.Configuration cfg ) {
		cfg.setProperty( "hibernate.search.default.directory_provider", FSDirectoryProvider.class.getName() );
		File sub = locateBaseDir();
		cfg.setProperty( "hibernate.search.default.indexBase", sub.getAbsolutePath() );
	}
}
