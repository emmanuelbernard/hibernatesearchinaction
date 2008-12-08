package com.manning.hsia.dvdstore;

import com.manning.hsia.test.SearchTestCase;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.reader.ReaderProvider;
import org.hibernate.search.store.DirectoryProvider;
import org.hibernate.search.store.FSDirectoryProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

public class TestShards extends SearchTestCase {
	Transaction tx;

	@Test(groups="ch11", alwaysRun=true)
	public void testShards() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		buildIndex( session );

		ReaderProvider readerProvider = null;

		IndexReader reader0 = null;
		IndexReader reader1 = null;
		List results;

		try {
			tx = session.beginTransaction();
			FullTextSession fullTextSession = Search.getFullTextSession( session );
			QueryParser parser = new QueryParser( "id", new StandardAnalyzer() );

			results = fullTextSession.createFullTextQuery( parser.parse( "name:bear OR name:elephant" ) ).list();
			assert results.size() == 2 : "Either insert or query failed";

			SearchFactory searchFactory = fullTextSession.getSearchFactory();
			DirectoryProvider[] providers =
				searchFactory.getDirectoryProviders( MergedAnimal.class );
			assert providers.length == 2 : "Wrong provider count";

			readerProvider = searchFactory.getReaderProvider();

			reader0 = readerProvider.openReader( providers[0] );
			reader1 = readerProvider.openReader( providers[1] );
			assert reader0.document( 0 ).get( "name" ).equals( "Bear" ) : "Incorrect document name";
			assert reader1.document( 0 ).get( "name" ).equals( "Elephant" ) : "Incorrect document name";

			for (Object o : results) session.delete( o );
			tx.commit();
		}
		finally {
			if ( reader0 != null )
				readerProvider.closeReader( reader0 );
			if ( reader1 != null )
				readerProvider.closeReader( reader1 );
			session.close();
		}
	}

	private void buildIndex( FullTextSession session ) {
		tx = session.beginTransaction();

		MergedAnimal a = new MergedAnimal();
		a.setId( 1 );
		a.setName( "Elephant" );
		session.save( a );

		a = new MergedAnimal();
		a.setId( 2 );
		a.setName( "Bear" );
		session.save( a );
		tx.commit();
		session.clear();
	}

	@BeforeClass(groups="ch11", alwaysRun=true)
	protected void setUp() throws Exception {
		File sub = locateBaseDir();
		File[] files = sub.listFiles();
		if ( files != null ) {
			for (File file : files) {
				if ( file.isDirectory() ) {
					delete( file );
				}
			}
		}
		buildSessionFactory( getMappings(), getAnnotatedPackages(), getXmlFiles() );
	}

	@Override
	protected void configure( Configuration cfg ) {
		super.configure( cfg );
		cfg.setProperty( "hibernate.search.default.directory_provider", FSDirectoryProvider.class.getName() );
		File sub = locateBaseDir();
		cfg.setProperty( "hibernate.search.default.indexBase", sub.getAbsolutePath() );
		cfg.setProperty( "hibernate.search.Animal.sharding_strategy.nbr_of_shards", "2" );
		cfg.setProperty( "hibernate.search.Animal.0.indexName", "Animal00" );
	}

	protected Class[] getMappings() {
		return new Class[]{
			MergedAnimal.class,
		};
	}
}