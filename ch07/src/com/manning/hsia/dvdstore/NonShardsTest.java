package com.manning.hsia.dvdstore;

import com.manning.hsia.test.SearchTestCase;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.store.DirectoryProvider;
import org.hibernate.search.store.FSDirectoryProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;


public class NonShardsTest extends SearchTestCase {
	Transaction tx;
	private Logger mLogger = Logger.getLogger( com.manning.hsia.dvdstore.NonShardsTest.class.getName());

	@Test
	public void testNoShards() throws Exception {
		FullTextSession session = Search.getFullTextSession(openSession());
		buildIndex(session);

		tx = session.beginTransaction();
		QueryParser parser = new QueryParser("id", new StopAnalyzer());

		List results = session.createFullTextQuery(parser.parse("name:bear OR name:elephant")).list();

		mLogger.info("results returned = " + results.size());

		assert results.size() == 2:"Either insert or query failed";

		SearchFactory searchFactory = session.getSearchFactory();
		DirectoryProvider[] providers = searchFactory.getDirectoryProviders(Animal.class);

		assert providers.length == 1: "Wrong provider count";

		org.apache.lucene.store.Directory directory = providers[0].getDirectory();

		IndexReader reader = null;
		try {
			reader = IndexReader.open(directory);
			assert reader.document(0).get("name").equals("Elephant"): "Incorrect document name";
			assert reader.document(1).get("name").equals("Bear"): "Incorrect document name";
			for (Object o : results)
				session.delete(o);
			tx.commit();
		}
		finally {
			if (reader != null)
				reader.close();
			session.close();
		}
	}

	private void buildIndex(FullTextSession session) {
		tx = session.beginTransaction();
		Animal a = new Animal();
		a.setId(1);
		a.setName("Elephant");
		session.persist(a);
		a = new Animal();

		a.setId(2);
		a.setName("Bear");
		session.persist(a);
		tx.commit();
		session.clear();
	}

	@BeforeClass
	protected void setUp() throws Exception {
		File sub = locateBaseDir();
		File[] files = sub.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					delete(file);
				}
			}
		}
		buildSessionFactory(getMappings(), getAnnotatedPackages(), getXmlFiles());
	}

	@Override
	protected void configure(Configuration cfg) {
		super.configure(cfg);
		cfg.setProperty("hibernate.search.default.directory_provider", FSDirectoryProvider.class.getName());
		File sub = locateBaseDir();
		cfg.setProperty("hibernate.search.default.indexBase", sub.getAbsolutePath());
	}

	protected Class[] getMappings() {
		return new Class[]{
			Animal.class,
		};
	}
}