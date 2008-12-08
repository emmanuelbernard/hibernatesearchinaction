package com.manning.hsia.dvdstore;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.RangeQuery;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.store.FSDirectoryProvider;
import com.manning.hsia.test.SearchTestCase;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestBadRangeQuery extends SearchTestCase {
	int[] numbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

	@Test(groups="ch07")
	public void testNumericRangeQuery() throws Exception {
		FullTextSession session = Search.getFullTextSession(openSession());
		Transaction tx = session.beginTransaction();

		buildIndex(1000, session, tx);

		try {
			Term lower = new Term("number", "1");
			Term upper = new Term("number", "3");

			tx = session.beginTransaction();
			RangeQuery query = new RangeQuery(lower, upper, true);
			System.out.println(query.toString());

			org.hibernate.search.FullTextQuery hibQuery = session.createFullTextQuery(query, Num.class);
			List<Num> results = hibQuery.list();

			List<String> numbers = new ArrayList<String>();
			for (Num num : results) {
				numbers.add(num.getNumber() + "");
				System.out.println(num.getNumber());
			}
			assert results.size() == 4: "incorrect return count";
			assert numbers.contains("10");

			for (Object element : session.createQuery("from " + Num.class.getName()).list()) session.delete(element);
			tx.commit();
		}
		finally {
			session.close();
		}
	}

	private void buildIndex(int indexStart, FullTextSession session, Transaction tx) {
		for (int x = indexStart; x < numbers.length + indexStart; x++) {
			Num num = new Num();
			num.setId(x);
			num.setNumber(numbers[x - indexStart]);
			session.save(num);
		}
		tx.commit();
		session.clear();
	}

	protected Class[] getMappings() {
		return new Class[]{
			Num.class
		};
	}

	protected void configure(org.hibernate.cfg.Configuration cfg) {
		cfg.setProperty("hibernate.search.default.directory_provider", FSDirectoryProvider.class.getName());
		File sub = locateBaseDir();
		cfg.setProperty("hibernate.search.default.indexBase", sub.getAbsolutePath());
	}
}
