package com.manning.hsia.dvdstore.action;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.jpa.FullTextEntityManager;

import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.EntityManagerHolder;
import com.manning.hsia.dvdstore.util.SessionHolder;

/**
 * Example 2.8, 2.9
 */
public class Indexer {
	public void indexWithJPA() {
		EntityManager em = EntityManagerHolder.getEntityManager();
		
		//wrap a EntityManager object
		FullTextEntityManager ftem = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
		
		ftem.getTransaction().begin();
		
		@SuppressWarnings("unchecked")
		List<Item> items = em.createQuery("select i from Item i").getResultList();
		
		for (Item item : items) {
		    ftem.index(item);  //manually index an item instance
		}
		
		ftem.getTransaction().commit(); //index are written at commit time
	}
	
	public void indexWithHibernate() {
		Session session = SessionHolder.getSession();
		
		//wrap a Session object
		FullTextSession ftSession = org.hibernate.search.Search.getFullTextSession(session);
		ftSession.getTransaction().begin();
		
		@SuppressWarnings("unchecked")
		List<Item> items = session.createCriteria(Item.class).list();
		
		for (Item item : items) {
		    ftSession.index(item);  //manually index an item instance
		}
		
		ftSession.getTransaction().commit(); //index are written at commit time
	}
}
