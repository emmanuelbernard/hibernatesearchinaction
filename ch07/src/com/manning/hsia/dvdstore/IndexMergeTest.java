package com.manning.hsia.dvdstore;

import com.manning.hsia.test.SearchTestCase;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.engine.DocumentBuilder;
import org.hibernate.search.store.DirectoryProvider;
import org.hibernate.search.store.FSDirectoryProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

public class IndexMergeTest extends SearchTestCase {
	Transaction tx;

	@Test
	public void testTwoEntitiesNoShards() throws Exception {
		FullTextSession session = Search.getFullTextSession(openSession());
		buildIndex(session);

		tx = session.beginTransaction();
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		QueryParser parser = new QueryParser("id", new StandardAnalyzer());

		List results = fullTextSession.createFullTextQuery(parser.parse("id:1")).list();
		assert results.size() == 2:"Either insert or query failed";

		SearchFactory searchFactory = fullTextSession.getSearchFactory();
		DirectoryProvider[] provider = searchFactory.getDirectoryProviders(MergedAnimal.class);
		assert provider.length == 1: "Wrong provider count";
		org.apache.lucene.store.Directory directory = provider[0].getDirectory();

		BooleanQuery classFilter = new BooleanQuery();
		classFilter.setBoost(0);

		Term t = new Term(DocumentBuilder.CLASS_FIELDNAME, Furniture.class.getName());
		TermQuery termQuery = new TermQuery(t);
		classFilter.add(termQuery, BooleanClause.Occur.SHOULD);

		Term luceneTerm = new Term("id", "1");
		Query luceneQuery = new TermQuery(luceneTerm);

		BooleanQuery filteredQuery = new BooleanQuery();
		filteredQuery.add(luceneQuery, BooleanClause.Occur.MUST);
		filteredQuery.add(classFilter, BooleanClause.Occur.MUST);

		IndexSearcher searcher = null;
		try {
			searcher = new IndexSearcher(directory);
			Hits hits = searcher.search(filteredQuery);
			assert hits.length() == 1: "Wrong hit count";

			Document doc = hits.doc(0);
			assert doc.get("color").equals("dark blue");

			for (Object o : results) session.delete(o);
			tx.commit();
		}
		finally {
			if (searcher != null)
				searcher.close();
			session.close();
		}
	}

	private void buildIndex(FullTextSession session) {
		Transaction tx = session.beginTransaction();

		MergedAnimal a = new MergedAnimal();
		a.setId(1);
		a.setName("Elephant");
		session.save(a);

		Furniture fur = new Furniture();
		fur.setColor("dark blue");
		session.save(fur);

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
		cfg.setProperty("hibernate.search.com.manning.hsia.dvdstore.Furniture.indexName", "Animal");
	}

	protected Class[] getMappings() {
		return new Class[]{
			MergedAnimal.class,
			Furniture.class
		};
	}
}