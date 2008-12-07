package com.manning.hsia.dvdstore;

import com.manning.hsia.test.ch13.SearchTestCase;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.store.DirectoryProvider;
import org.hibernate.search.store.FSDirectoryProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

public class TestSpellChecker extends SearchTestCase {
	public List<String> results;
	private String baseDir;
	private Directory spellDir;

	String texts[] = {
		"Keanu Reeves is completely wooden in this romantic misfired flick",
		"Reeves plays a traveling salesman and agrees to help a woman",
		"Jamie Lee Curtis finds out that he's not really a salesman"
	};

	@Test(groups="ch13")
	public void testSpellCheck() throws Exception {
		FullTextSession session = Search.getFullTextSession( openSession() );
		Transaction tx = session.beginTransaction();

		try {
			buildIndex( session, tx );

			tx = session.beginTransaction();
			SpellChecker spellChecker = buildSpellCheckIndex( "description", session );


			String misspelledUserInput = "kenu";

			assert !spellChecker.exist( misspelledUserInput ) : "misspelled word found";

			String[] suggestions = spellChecker.suggestSimilar( misspelledUserInput, 5 );
			assert suggestions.length == 1 : "incorrect suggestion count";
			for (String suggestion : suggestions) {
				System.out.println( suggestion );
				assert suggestion.equals( "keanu" );
			}

			tx.commit();
		}
		finally {
			session.close();
		}
	}

	private SpellChecker buildSpellCheckIndex( String fieldName, FullTextSession session ) throws Exception {
		SearchFactory searchFactory = session.getSearchFactory();
		DirectoryProvider[] providers = searchFactory.getDirectoryProviders( Dvd.class );

		org.apache.lucene.store.Directory DvdDirectory = providers[0].getDirectory();

		IndexReader spellReader = null;
		SpellChecker spellchecker = null;
		try {
			// read from the DVD directory
			spellReader = IndexReader.open( DvdDirectory );
			LuceneDictionary dict = new LuceneDictionary( IndexReader.open( DvdDirectory ), fieldName );
			// build the spellcheck index in the base directory
			spellDir = FSDirectory.getDirectory( baseDir );
			spellchecker = new SpellChecker( spellDir );
			// build the directory
			spellchecker.indexDictionary( dict );
		}
		finally {
			if ( spellReader != null )
				spellReader.close();
		}

		return spellchecker;
	}

	private void buildIndex( FullTextSession session, Transaction tx ) {
		for (int x = 0; x < texts.length; x++) {
			Dvd dvd = new Dvd();
			dvd.setId( x + 1 );
			dvd.setDescription( texts[x] );
			session.save( dvd );
		}
		tx.commit();
		session.clear();
	}

	@Override
	protected void configure( Configuration cfg ) {
		super.configure( cfg );
		cfg.setProperty( "hibernate.search.default.directory_provider", FSDirectoryProvider.class.getName() );
		File sub = locateBaseDir();
		baseDir = sub.getAbsolutePath();
		cfg.setProperty( "hibernate.search.default.indexBase", baseDir );
	}

	protected Class[] getMappings() {
		return new Class[]{
			Dvd.class
		};
	}
}
