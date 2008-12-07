package com.manning.hsia.dvdstore;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;

public class MyQueryParser extends QueryParser {

	public MyQueryParser(String field, Analyzer analyzer) {
		super(field, analyzer);
	}

	@Override
	protected Query getFuzzyQuery(String field, String term, float sim) {
		return new FuzzyQuery(new Term(field, term), sim);
	}
}
