package com.manning.hsia.dvdstore;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.testng.annotations.Test;

public class TestQueryParserQueryGeneration {
	@Test
	public void testQueryParser() throws Exception {

		String queryString = "The Story of the Day";
		QueryParser parser = new QueryParser( "title", new StandardAnalyzer() );
		Query query = parser.parse( queryString );
		assert query.toString().equals( "title:story title:day" );

		queryString = "The Story of the Day";
		parser = new QueryParser( "title", new SimpleAnalyzer() );
		query = parser.parse( queryString );
		assert query.toString().equals( "title:the title:story title:of title:the title:day" );

		queryString = "Story*";
		parser = new QueryParser( "title", new StandardAnalyzer() );
		query = parser.parse( queryString );
		assert query.toString().equals( "title:story*" );

		queryString = "Story~0.8 Judgement";
		parser = new QueryParser( "title", new StandardAnalyzer() );
		parser.setDefaultOperator( QueryParser.Operator.AND );
		query = parser.parse( queryString );
		assert query.toString().equals( "+title:story~0.8 +title:judgement" );
	}
}
