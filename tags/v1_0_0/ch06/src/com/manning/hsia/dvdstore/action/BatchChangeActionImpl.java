package com.manning.hsia.dvdstore.action;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.hibernate.ScrollableResults;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;

import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.SessionHolder;

public class BatchChangeActionImpl implements BatchChangeAction {
	
	private static final int WINDOW_SIZE = 50;

	/**
	 * Example 6.12
	 */
	public void applyBatchChange(String words) {
		FullTextSession ftSession = SessionHolder.getFullTextSession(); 
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);

		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		query.setFetchSize(WINDOW_SIZE);  //define fetch size as window size
		
		ScrollableResults items = query.scroll();
		
		log( "Results changed: " + query.getResultSize() );
		try {
			items.beforeFirst();
			int index = 0;
			while( items.next() ) {
				Item item = (Item) items.get()[0];
				index++;
				if ( item != null ) {
					applyChange(item);  //update item
				}
				
				//TODO proof read algo wrt sync between fetch size and window_size
				if ( index % WINDOW_SIZE == 0 ) {  //clear memory every window size
					ftSession.flush();             //flush changes to the database
					ftSession.flushToIndexes();    //flush changes to the index
					ftSession.clear();             //clear memory
				}
			}
		}
		finally {
			items.close();
		}
	}
	
	public String getUrl(String words) {
		FullTextSession ftSession = SessionHolder.getFullTextSession();
		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(words, Item.class);
		
		FullTextQuery query = ftSession.createFullTextQuery(luceneQuery, Item.class);
		
		Item item = (Item) query.setFirstResult(0).setMaxResults(1).uniqueResult();
		return item.getImageURL();
	}

	private org.apache.lucene.search.Query buildLuceneQuery(String words, Class<?> searchedEntity) {
		Analyzer analyzer;
		if (searchedEntity == null) {       //get most appropriate analyzer
			analyzer = new StandardAnalyzer();
		}
		else {
			analyzer = SessionHolder.getFullTextSession().getSearchFactory().getAnalyzer(searchedEntity);
		}
		
		QueryParser parser = new QueryParser( "description", analyzer );
		org.apache.lucene.search.Query luceneQuery = null;
		try {
			luceneQuery = parser.parse(words);
		}
		catch (org.apache.lucene.queryParser.ParseException e) {
			throw new IllegalArgumentException("Unable to parse search entry  into a Lucene query", e);
		}
		return luceneQuery;
	}

	private void log(String string) {
		System.out.println(string);
	}
	
	private void applyChange(Item item) {
		item.setImageURL("http://blog.emmanuelbernard.com");
	}
}
