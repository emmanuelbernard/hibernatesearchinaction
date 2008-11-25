package com.manning.hsia.test;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.search.store.RAMDirectoryProvider;
import org.testng.annotations.BeforeClass;

import java.io.InputStream;

public abstract class HSiATestCase {
	private static SessionFactory sessions;
	private static AnnotationConfiguration cfg;
	private static Class lastTestClass;
	private Session session;

	public HSiATestCase() {

	}

	protected void buildSessionFactory( Class[] classes, String[] packages, String[] xmlFiles ) throws Exception {

		if ( getSessions() != null ) getSessions().close();
		try {
			setCfg( new AnnotationConfiguration() );
			configure( cfg );
			if ( recreateSchema() ) {
				cfg.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
			}
			for (int i = 0; i < packages.length; i++) {
				getCfg().addPackage( packages[i] );
			}
			for (int i = 0; i < classes.length; i++) {
				getCfg().addAnnotatedClass( classes[i] );
			}
			for (int i = 0; i < xmlFiles.length; i++) {
				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( xmlFiles[i] );
				getCfg().addInputStream( is );
			}
			setSessions( getCfg().buildSessionFactory( /*new TestInterceptor()*/ ) );
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@BeforeClass
	protected void setUp() throws Exception {
		if ( getSessions() == null || getSessions().isClosed() || lastTestClass != getClass() ) {
			buildSessionFactory( getMappings(), getAnnotatedPackages(), getXmlFiles() );
			lastTestClass = getClass();
		}
	}

	protected void configure( org.hibernate.cfg.Configuration cfg ) {
		// will be overridden as necessary in the individual tests
		cfg.setProperty( "hibernate.search.default.directory_provider", RAMDirectoryProvider.class.getName() );
		cfg.setProperty( org.hibernate.search.Environment.ANALYZER_CLASS, StandardAnalyzer.class.getName() );
	}

	protected void runTest() throws Throwable {
		try {
			if ( session != null && session.isOpen() ) {
				session.close();
				session = null;
				throw new Exception( "unclosed session" );
			}
			else {
				session = null;
			}
		}
		catch (Throwable e) {
			try {
				if ( session != null && session.isOpen() ) {
					session.close();
				}
			}
			catch (Exception ignore) {
			}
			try {
				if ( sessions != null ) {
					sessions.close();
					sessions = null;
				}
			}
			catch (Exception ignore) {
			}
			throw e;
		}
	}

	public Session openSession() throws HibernateException {
		session = getSessions().openSession();
		return session;
	}

	public Session openSession( Interceptor interceptor ) throws HibernateException {
		session = getSessions().openSession( interceptor );
		return session;
	}

	protected abstract Class[] getMappings();

	protected String[] getAnnotatedPackages() {
		return new String[]{};
	}

	protected String[] getXmlFiles() {
		return new String[]{};
	}

	private void setSessions( SessionFactory sessions ) {
		HSiATestCase.sessions = sessions;
	}

	protected SessionFactory getSessions() {
		return sessions;
	}

	protected static void setCfg( AnnotationConfiguration cfg ) {
		HSiATestCase.cfg = cfg.configure();
	}

	protected static AnnotationConfiguration getCfg() {
		return cfg;
	}

	protected boolean recreateSchema() {
		return true;
	}
}
