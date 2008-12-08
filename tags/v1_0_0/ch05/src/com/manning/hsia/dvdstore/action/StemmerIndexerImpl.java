package com.manning.hsia.dvdstore.action;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.SearchException;

import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.SessionHolder;

/**
 * Example 5.13
 */
public class StemmerIndexerImpl implements StemmerIndexer {

	public String checkStemmingIndex() {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		try {
			//build the Lucene query
			final Analyzer entityScopedAnalyzer = ftSession.getSearchFactory().getAnalyzer(Item.class);
			QueryParser parser = new QueryParser("id", entityScopedAnalyzer );  //use Item analyzer
			//search on the exact field
			Query query = parser.parse("title:saving");  //build Lucene query
			
			//the query is not altered
			if ( ! "title:saving".equals( query.toString() ) ) {
				return "searching the exact field should not alter the query";
			}
			
			//return matching results
			org.hibernate.search.FullTextQuery hibQuery = 
				ftSession.createFullTextQuery(query, Item.class);  //return matching results 
			@SuppressWarnings("unchecked") 
			List<Item> results = hibQuery.list();
			
			//we find a single matching result
			int exactResultSize = results.size();
			if ( exactResultSize != 1 ) {
				return "exact match should only return 1 result";
			}
			
			//search on the stemmed field
			query = parser.parse("title_stemmer:saving");   //search same word on the stemmed field
			
			//the query search the stem version of each word
			if ( ! "title_stemmer:save".equals( query.toString() ) ) {  //search the stem version of each word
				return "searching the stemmer field should search the stem";
			}
			
			//return matching results
			hibQuery = ftSession.createFullTextQuery(query);
			results = (List<Item>) hibQuery.list();
			
			//we should find more matches than the exact query
			if ( results.size() <= exactResultSize ) {   //more matching results are found 
				return "stemming should return more matches";
			}
			return null; //no error
		}
		catch (ParseException e) {
			throw new SearchException(e);
		}
	}

}
