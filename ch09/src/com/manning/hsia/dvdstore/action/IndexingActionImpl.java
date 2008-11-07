package com.manning.hsia.dvdstore.action;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.search.FullTextSession;

import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.SessionHolder;

public class IndexingActionImpl implements IndexingAction {
	

	private static int BATCH_SIZE = 100;

	/**
	 * Example 9.2, 9.3, 9.4
	 */
	public void indexAllItems() {
		
		FullTextSession session = SessionHolder.getFullTextSession();
		
		session.purgeAll(Item.class);  //remove obsolete content
		session.getSearchFactory().optimize(Item.class);  //physically clear space
		
		Criteria query = session.createCriteria(Item.class)
				.setFetchMode("distributor", FetchMode.JOIN)  //load necessary associations
				.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY) //distinct them (due to collection load)
				.setCacheMode(CacheMode.IGNORE)     //minimize cache interaction
				.setFetchSize(BATCH_SIZE);     //align batch size and JDBC fetch size
		
		ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);  //scroll in forward only
		
		int batch = 0;
		scroll.beforeFirst();
		while (scroll.next()) {
			batch++;
			session.index(scroll.get(0));
			if (batch % BATCH_SIZE == 0) {
				// no need to session.flush() since we don't change anything
				session.flushToIndexes();  //flush index works and clear the session
				session.clear();
			}
		}
		// the remaining non flushed work index are processed at commit time
	}

	public void optimize() {
		FullTextSession session = SessionHolder.getFullTextSession();
		session.getSearchFactory().optimize();  //Optimize all indexes
}

	public void optimize(Class<?> clazz) {
		FullTextSession session = SessionHolder.getFullTextSession();
		session.getSearchFactory().optimize(clazz);  //Optimize a given class 
	}

	/**
	 * Example 9.12
	 */
	public void reindex() {
		FullTextSession session = SessionHolder.getFullTextSession();
		
		session.purgeAll(Item.class);
		session.getSearchFactory().optimize(Item.class); //run after purge to save space
		
		Criteria query = session.createCriteria(Item.class)
				.setFetchMode("distributor", FetchMode.JOIN)
				.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
				.setCacheMode(CacheMode.IGNORE)
				.setFetchSize(BATCH_SIZE);
		
		ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);
		
		scroll.beforeFirst();
		int batch = 0;
		while (scroll.next()) {
			batch++;
			session.index(scroll.get(0));
			if (batch % BATCH_SIZE == 0) {
				// no need to session.flush() since we don't change anything
				session.flushToIndexes();
				session.clear();
			}
		}
		
		session.flushToIndexes();  //flush last changes before optimizing
		session.getSearchFactory().optimize(Item.class);  //Run optimization
	}

}
