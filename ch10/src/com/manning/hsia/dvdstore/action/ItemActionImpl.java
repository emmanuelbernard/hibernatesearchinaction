package com.manning.hsia.dvdstore.action;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.manning.hsia.dvdstore.model.Distributor;
import com.manning.hsia.dvdstore.model.Item;

@Stateless
public class ItemActionImpl implements ItemAction {
	@PersistenceContext EntityManager em;

	public void addNewItem(Item item) {
		em.persist(item);
	}
	
	public Distributor getDistributor(Integer id) {
		return em.find(Distributor.class, id);
	}
}
