package com.manning.hsia.dvdstore.master;

import javax.ejb.MessageDriven;
import javax.ejb.ActivationConfigProperty;
import javax.jms.MessageListener;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.search.backend.impl.jms.AbstractJMSHibernateSearchController;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/hibernatesearch"),
		@ActivationConfigProperty(propertyName="DLQMaxResent", propertyValue="1")
		} )
public class MDBSearchController extends AbstractJMSHibernateSearchController
		implements MessageListener {
	
	@PersistenceContext private EntityManager em;

	@Override
	protected void cleanSessionIfNeeded(Session session) {
		//nothing to do container managed
	}

	@Override
	protected Session getSession() {
		return (Session) em.getDelegate();
	}

}
