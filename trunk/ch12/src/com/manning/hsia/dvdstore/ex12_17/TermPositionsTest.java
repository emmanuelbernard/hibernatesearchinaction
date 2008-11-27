package com.manning.hsia.dvdstore.ex12_17;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.index.TermVectorOffsetInfo;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.reader.ReaderProvider;
import org.hibernate.search.store.DirectoryProvider;
import org.hibernate.search.store.FSDirectoryProvider;
import com.manning.hsia.test.ch12.SearchTestCase;
import org.testng.annotations.Test;

import java.util.List;

public class TermPositionsTest extends SearchTestCase {
	IndexReader reader;
	ReaderProvider provider;

	@Test
	public void vectorTest() throws Exception {
		FullTextSession session = Search.getFullTextSession(openSession());
		Transaction tx = session.beginTransaction();
		buildIndex(session, tx);

		try {
			tx = session.beginTransaction();

			Query query =
				new TermQuery(new Term("content",
					"properties"));
			System.out.println(query.toString());

			FullTextQuery hibQuery =
				session.createFullTextQuery(query,
					ElectricalProperties.class);
			hibQuery.setProjection(FullTextQuery.DOCUMENT,
				FullTextQuery.DOCUMENT_ID,
				FullTextQuery.SCORE);

			reader = getReader(session);

			List<Object[]> results = hibQuery.list();

			assert results.size() > 0 : "no results returned";
			for (int x = 0; x < results.size(); x++) {

				Integer docId = (Integer) results.get(x)[1];
				TermPositionVector vector =
					(TermPositionVector) reader
						.getTermFreqVector(docId, "content");
				String[] terms = vector.getTerms();
				int[] f = vector.getTermFrequencies();

				System.out.println(results.get(x)[2]);
				for (int y = 0; y < vector.size(); y++) {
					System.out.print("docID# =>" + docId);
					System.out.print(" term => " + terms[y]);
					System.out.print(" freq => " + f[y]);

					int[] positions = vector.getTermPositions(y);
					TermVectorOffsetInfo[] offsets =
						vector.getOffsets(y);
					for (int z = 0; z < positions.length; z++) {
						System.out.print(" position => "
							+ positions[z]);
						System.out.print(" starting offset => "
							+ offsets[z].getStartOffset());
						System.out.println(" ending offset => "
							+ offsets[z].getEndOffset());
					}
					System.out.println("---------------");
				}
			}
			for (Object element :
				session.createQuery("from " + ElectricalProperties.class.getName()).list())
				session.delete(element);

			tx.commit();
		}
		finally {
			session.close();
			if (provider != null) {
				provider.closeReader(reader);
			}
		}
	}

	private void buildIndex(FullTextSession session, Transaction tx) throws Exception {
		tx = session.beginTransaction();

		ElectricalProperties ep = new ElectricalProperties();
		ep.setContent("Electrical Engineers measure Electrical Properties");
		session.save(ep);

		ep = new ElectricalProperties();
		ep.setContent("Electrical Properties are interesting");
		session.save(ep);

		ep = new ElectricalProperties();
		ep.setContent("Electrical Properties are measurable properties");
		session.save(ep);

		tx.commit();
		session.clear();
	}

	private IndexReader getReader(FullTextSession session) {
		SearchFactory searchFactory = session.getSearchFactory();
		DirectoryProvider dirProvider =
			searchFactory.getDirectoryProviders(
				ElectricalProperties.class)[0];
		provider = searchFactory.getReaderProvider();
		return provider.openReader(dirProvider);
	}

	protected Class[] getMappings() {
		return new Class[]{
			ElectricalProperties.class
		};
	}

	protected void configure(org.hibernate.cfg.Configuration cfg) {
		super.configure(cfg);
		cfg.setProperty("hibernate.search.default.directory_provider", FSDirectoryProvider.class.getName());
		cfg.setProperty("hibernate.search.default.indexBase", locateBaseDir().getAbsolutePath());
	}
}