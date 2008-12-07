package com.manning.hsia.dvdstore;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.testng.annotations.Test;

import java.io.Reader;
import java.io.StringReader;

public class TestManualAnalyzer {
	@Test(groups="ch07")
	public void testManualAnalyzer() throws Exception {
		String search = "The Little Pony";
		Reader reader = new StringReader( search );
		Analyzer analyzer = new StandardAnalyzer();

		TokenStream stream = analyzer.tokenStream( "title", reader );

		Token token = new Token();
		token = stream.next( token );

		BooleanQuery query = new BooleanQuery();
		while (token != null) {
			if ( token.termLength() != 0 ) {
				// create the string out of the token. String(char[])
				// copy the char so we are safe to reuse Token
				String term =
					new String( token.termBuffer(), 0, token.termLength() );
				//add it to the query by creating a TermQuery
				query.add( new TermQuery( new Term( "title", term ) ), BooleanClause.Occur.SHOULD );
			}
			token = stream.next( token );
		}
		assert query.toString().equals( "title:little title:pony" ) : "incorrect query generated";
	}
}
