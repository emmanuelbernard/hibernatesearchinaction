package com.manning.hsia.dvdstore.test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.action.SearchingAction;
import com.manning.hsia.dvdstore.action.SearchingActionImpl;
import com.manning.hsia.dvdstore.model.Distributor;
import com.manning.hsia.dvdstore.model.Item;
import com.manning.hsia.dvdstore.util.SessionInvocationHandler;

public class HibernateSearchIntegrationTest {

	private SessionFactory factory;
	
	@BeforeTest(groups={"hibernatesearch"})   //executed before every test
	protected void setUp() throws Exception {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		factory = configuration
			.configure( "hibernate-test.cfg.xml" )
			.buildSessionFactory();    //build the session factory 
		postSetUp();          //run post initialization
	}

	@AfterTest(groups={"hibernatesearch"})
	protected void tearDown() throws Exception {
		factory.close();    //clear the factory after every test
	}
	
	public void postSetUp() throws Exception {   //populate the database before each test
		//create a test case
		Distributor distributor = new Distributor();
		distributor.setName("Manning Video");
		distributor.setStockName("MAN");
		
		Item item = new Item();
		item.setTitle("Hibernate Search in Action");
		item.setEan("1234567890123");
		item.setDescription("Video version of HSiA, go through tutorials.");
		item.setPrice(new BigDecimal(20) );
		item.setDistributor(distributor);
		
		Session session = factory.openSession();
		session.getTransaction().begin();
		session.persist(distributor);
		session.persist(item);
		session.getTransaction().commit(); //database and indexes are populated
		session.close();
	}
	
	@Test(groups="hibernatesearch")   //actual test
	public void testSearch() throws Exception {
		SearchingAction action = getSearchingAction();
		
		List<String> titles = action.getTitleFromMatchingItems("title:search");  //run test on prepopulated dataset
		
		assert 1 == titles.size() : "should have one match, not " + titles.size();  //assert results based on the dataset
		assert "Hibernate Search in Action".equals( titles.get(0) ) : "right book matches";
	}
	
	private SearchingAction getSearchingAction() {
		SearchingAction action = new SearchingActionImpl();
		return (SearchingAction) Proxy.newProxyInstance(
					this.getClass().getClassLoader(), 
					new Class[] { SearchingAction.class },
					new SessionInvocationHandler(action, factory) );
	}
}
