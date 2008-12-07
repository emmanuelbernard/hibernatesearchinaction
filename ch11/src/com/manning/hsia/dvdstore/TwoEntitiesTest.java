package com.manning.hsia.dvdstore;

import com.manning.hsia.test.SearchTestCase;
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

public class TwoEntitiesTest extends SearchTestCase {
	FullTextSession session;
	Transaction tx;

	@Test(groups="ch11")
	public void testTwoEntitiesNoShards() throws Exception {
		session = Search.getFullTextSession(openSession());
		buildIndex();

		tx = session.beginTransaction();
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		QueryParser parser = new QueryParser("id", new StopAnalyzer());

		try {
			List results = fullTextSession.createFullTextQuery(parser.parse("name:elephant OR color:blue")).list();
			assert results.size() == 2: "Either insert or query failed";

			SearchFactory searchFactory = fullTextSession.getSearchFactory();
			DirectoryProvider[] provider0 = searchFactory.getDirectoryProviders(Animal.class);
			assert provider0.length == 1: "Wrong provider count";
			org.apache.lucene.store.Directory directory0 = provider0[0].getDirectory();

			DirectoryProvider[] provider1 = searchFactory.getDirectoryProviders(Furniture.class);
			assert provider1.length == 1: "Wrong provider count";
			org.apache.lucene.store.Directory directory1 = provider1[0].getDirectory();

			IndexReader reader0 = IndexReader.open(directory0);
			assert reader0.document(0).get("name").equals("Elephant"):"Incorrect document name";
			IndexReader reader1 = IndexReader.open(directory1);
			assert reader1.document(0).get("color").equals("dark blue"): "Incorrect color";

			for (Object o : results) session.delete(o);
			tx.commit();
		}
		finally {
			session.close();
		}
	}

	private void buildIndex() {
		tx = session.beginTransaction();

		Animal a = new Animal();
		a.setId(1);
		a.setName("Elephant");
		session.save(a);

		Furniture fur = new Furniture();
		fur.setColor("dark blue");
		session.save(fur);
		tx.commit();

		session.clear();
	}

	@BeforeClass(groups="ch11", alwaysRun=true)
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
			Furniture.class
		};
	}
}