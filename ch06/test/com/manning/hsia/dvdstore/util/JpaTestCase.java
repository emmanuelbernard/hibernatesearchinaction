package com.manning.hsia.dvdstore.util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

public class JpaTestCase {

	protected EntityManagerFactory factory;
	
	@BeforeTest(groups={"ch06"}, alwaysRun=true)
	protected void setUp() throws Exception {
		factory = Persistence.createEntityManagerFactory("dvdstore-catalog");
		postSetUp();
	}

	@AfterTest(groups={"ch06"}, alwaysRun=true)
	protected void tearDown() throws Exception {
		factory.close();
	}
	
	public void postSetUp() throws Exception {
	}
}
