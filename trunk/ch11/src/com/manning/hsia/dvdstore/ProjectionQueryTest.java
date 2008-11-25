package com.manning.hsia.dvdstore;

import com.manning.hsia.test.SearchTestCase;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.Serializable;

public class ProjectionQueryTest extends SearchTestCase {
	FullTextSession session;
	Transaction tx;

	@Test
	public void testLuceneObjectsProjectionWithScroll() throws Exception {
		session = Search.getFullTextSession( openSession() );
		buildIndex();

		tx = session.beginTransaction();
		QueryParser parser =
			new QueryParser( "dept", new StandardAnalyzer() );

		Query query = parser.parse( "dept:ITech" );
		FullTextQuery hibQuery =
			session.createFullTextQuery( query, Employee.class );
		hibQuery.setProjection( "id", "lastname", "dept",
			FullTextQuery.THIS, FullTextQuery.SCORE,
			FullTextQuery.DOCUMENT, FullTextQuery.ID );

		try {
			ScrollableResults projections = hibQuery.scroll();
			projections.beforeFirst();
			projections.next();
			Object[] projection = projections.get();

			assert (Integer) projection[0] == 1000 : "id incorrect";
			assert ( (String) projection[1] ).equals( "Griffin" ) : "lastname incorrect";
			assert ( (String) projection[2] ).equals( "ITech" ) : "dept incorrect";
			assert session.get( Employee.class, (Serializable) projection[0] )
				.equals( projection[3] ) : "THIS incorrect";

			assert (Float) projection[4] == 1.0F : "SCORE incorrect";
			assert projection[5] instanceof Document : "DOCUMENT incorrect";
			assert ( (Document) projection[5] ).getFields().size() == 4 : "DOCUMENT size incorrect";
			assert (Integer) projection[6] == 1000 : "legacy ID incorrect";
			assert projections.isFirst();

			assert ( (Employee) projection[3] ).getId() == 1000 : "Incorrect entity returned";

			for (Object element : session.createQuery( "from "
				+ Employee.class.getName() ).list())
				session.delete( element );
			tx.commit();
		}
		finally {
			session.close();
		}
	}

	private void buildIndex() {
		Transaction tx = session.beginTransaction();
		Employee e1 =
			new Employee( 1000, "Griffin", "ITech" );
		session.save( e1 );

		Employee e2 =
			new Employee( 1001, "Jackson", "Accounting" );
		session.save( e2 );
		tx.commit();

		session.clear();
	}

	protected Class[] getMappings() {
		return new Class[]{
			Employee.class,
		};
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
		buildSessionFactory( getMappings(), getAnnotatedPackages(), getXmlFiles() );
	}
}