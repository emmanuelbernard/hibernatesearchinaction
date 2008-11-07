package com.manning.hsia.dvdstore.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

public class TestCase {

	protected SessionFactory factory;
	
	@BeforeTest(groups={"ch09"}, alwaysRun=true)
	protected void setUp() throws Exception {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		factory = configuration.configure().buildSessionFactory();
		postSetUp();
	}

	@AfterTest(groups={"ch09"}, alwaysRun=true)
	protected void tearDown() throws Exception {
		factory.close();
	}
	
	public void postSetUp() throws Exception {
	}
}
