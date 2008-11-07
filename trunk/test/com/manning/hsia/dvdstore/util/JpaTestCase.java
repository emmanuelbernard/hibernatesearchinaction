package com.manning.hsia.dvdstore.util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

public class JpaTestCase {

	protected EntityManagerFactory factory;
	
	@BeforeTest(groups={"ch02"})
	protected void setUp() throws Exception {
		factory = Persistence.createEntityManagerFactory("dvdstore-catalog");
		postSetUp();
	}

	@AfterTest(groups={"ch02"})
	protected void tearDown() throws Exception {
		factory.close();
	}
	
	/**
	 * can be overridden to initialize the dataset
	 */
	public void postSetUp() throws Exception {
	}
}
