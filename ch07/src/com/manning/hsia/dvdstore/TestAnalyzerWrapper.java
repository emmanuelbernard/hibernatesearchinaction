package com.manning.hsia.dvdstore;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import com.manning.hsia.test.SearchTestCase;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public class TestAnalyzerWrapper extends SearchTestCase {

	@Test
	public void testScopedAnalyzer() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();

		buildIndex( session, tx );

		try {
			tx = session.beginTransaction();
			SearchFactory searchFactory = session.getSearchFactory();

			FullTextQuery hibQuery =
				buildQuery( searchFactory, "field2", session );
			List<ScopedEntity> results = hibQuery.list();

			assert results.size() == 0 : "incorrect result count";
			assert hibQuery.toString().equals( "FullTextQueryImpl(+field2:TEST)" ) : "incorrect query";

			hibQuery = buildQuery( searchFactory, "field1", session );
			results = hibQuery.list();

			assert results.size() == 2 : "incorrect result count";
			assert hibQuery.toString().equals( "FullTextQueryImpl(+field1:test)" ) : "incorrect query";

			for (Object element :
				session.createQuery( "from "
					+ ScopedEntity.class.getName() ).list())
				session.delete( element );
			tx.commit();
		}
		finally {
			session.close();
		}
	}

	private FullTextQuery buildQuery( SearchFactory factory, String field, FullTextSession session )
		throws IOException {

		Reader reader = new StringReader( "TEST" );

		TokenStream stream =
			factory.getAnalyzer( ScopedEntity.class )
				.tokenStream( field, reader );
		BooleanQuery bq = new BooleanQuery();

		Token token = new Token();
		token = stream.next( token ); //this method reuse Token and is faster than next()

		while (token != null) {
			if ( token.termLength() != 0 ) {
				//create the string out of the token.
				// String(char[]) copy the char so we are safe to reuse Token
				String term =
					new String( token.termBuffer(),
						0,
						token.termLength() );
				//add it to the query somehow
				bq.add( new TermQuery( new Term( field, term ) ),
					BooleanClause.Occur.MUST );
			}
			token = stream.next( token );
		}
		return session.createFullTextQuery( bq, ScopedEntity.class );
	}

	private void buildIndex( FullTextSession session, Transaction tx ) {
		tx = session.beginTransaction();

		ScopedEntity entity = new ScopedEntity();
		entity.setField1( "test field1" );
		entity.setField2( "test field2" );
		session.save( entity );

		entity = new ScopedEntity();
		entity.setField1( "test field3" );
		entity.setField2( "test field4" );
		session.save( entity );

		tx.commit();
	}

	protected Class[] getMappings() {
		return new Class[]{ScopedEntity.class};
	}
}
