package com.manning.hsia.dvdstore.action;

import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchException;

import com.manning.hsia.dvdstore.model.Item;

public class Indexer {
	private SessionFactory sessionFactory;
	private int BATCH_SIZE = 1000;
	
	public Indexer(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * Example 5.19
	 */
	public void indexItemsNaive() {
		FullTextSession session = Search.getFullTextSession( sessionFactory.openSession() );
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			//read the data from the database
			@SuppressWarnings("unchecked")
			List<Object> items = session.createCriteria(Item.class).list();  //retreive entities to be indexed
			//index the data
			for (Object item : items) {
				session.index(item);    //mark them for indexing 
			}
			//commit the index changes
			tx.commit();  //indexing work performed at commit time
		}
		catch (HibernateException e) {
			rollbackIfNeeded(tx);
			throw e;
		}
		catch (SearchException e) {
			rollbackIfNeeded(tx);
			throw e;
		}
		finally {
			session.close();
		}
	}
	
	/**
	 * Example 5.22
	 */
	public void indexItems() {
		FullTextSession session = Search.getFullTextSession( sessionFactory.openSession() );
		Transaction tx = null;
		try {
			session.setFlushMode(FlushMode.MANUAL);  //disable flush operations
			session.setCacheMode(CacheMode.IGNORE);  //disable 2nd level cache operations
			tx = session.beginTransaction();
			//read the data from the database
			//Scrollable results will avoid loading too many objects in memory
			ScrollableResults results = session.createCriteria( Item.class )
					.scroll( ScrollMode.FORWARD_ONLY );  //ensure forward only result set
			int index = 0;
			while( results.next() ) {
			    index++;
			    session.index( results.get(0) ); //index each element
			    if (index % BATCH_SIZE == 0) {
			    	session.flushToIndexes();    //apply changes to the index
			    	session.clear(); //clear the session releasing memory 
			    }
			}
			
			//commit the remaining index changes
			tx.commit();
		}
		catch (HibernateException e) {
			rollbackIfNeeded(tx);
			throw e;
		}
		catch (SearchException e) {
			rollbackIfNeeded(tx);
			throw e;
		}
		finally {
			session.close();
		}
	}

	private void rollbackIfNeeded(org.hibernate.Transaction tx) {
		if ( tx != null && tx.isActive() ) {
			tx.rollback();
		}
	}
	
	/**
	 * Example 5.21
	 */
	public void purgeItems() {
		FullTextSession session = Search.getFullTextSession( sessionFactory.openSession() );
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			//purge the all the items
			session.purgeAll(Item.class);  //remove all index data for Item 
			//commit the index changes
			tx.commit();   //actual removal happens at commit time
		}
		catch (HibernateException e) {
			rollbackIfNeeded(tx);
			throw e;
		}
		catch (SearchException e) {
			rollbackIfNeeded(tx);
			throw e;
		}
		finally {
			session.close();
		}
	}
	
	/**
	 * Example 5.20
	 */
	public void purgeItems(Integer... ids) {
		FullTextSession session = Search.getFullTextSession( sessionFactory.openSession() );
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			//purge the all the items by id
			for (Integer id : ids) {
				session.purge(Item.class, id);  //mark an entity for purge
			}
			//commit the index changes
			tx.commit();  //actual purge happens at commit time 
		}
		catch (HibernateException e) {
			rollbackIfNeeded(tx);
			throw e;
		}
		catch (SearchException e) {
			rollbackIfNeeded(tx);
			throw e;
		}
		finally {
			session.close();
		}
	}
	
	public void indexItems(Item... items) {
		FullTextSession session = Search.getFullTextSession( sessionFactory.openSession() );
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			//index all requested items
			for (Item item : items) {
				item = (Item) session.merge(item);
				session.index(item);
			}
			//commit the index changes
			tx.commit();
		}
		catch (HibernateException e) {
			rollbackIfNeeded(tx);
			throw e;
		}
		catch (SearchException e) {
			rollbackIfNeeded(tx);
			throw e;
		}
		finally {
			session.close();
		}
	}
}
