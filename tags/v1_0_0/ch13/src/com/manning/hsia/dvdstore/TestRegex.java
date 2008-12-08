package com.manning.hsia.dvdstore;

import com.manning.hsia.test.ch13.SearchTestCase;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.regex.JakartaRegexpCapabilities;
import org.apache.lucene.search.regex.RegexQuery;
import org.apache.lucene.search.regex.SpanRegexQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.testng.annotations.Test;

import java.util.List;

public class TestRegex extends SearchTestCase {
	private FullTextSession s;
	private Transaction tx;
	String texts[] = {
		"Keanu Reeves is completely wooden in this romantic misfired flick",
		"Reeves plays a traveling salesman and agrees to help a woman",
		"Jamie Lee Curtis finds out that he's not really a salesman"
	};

	@Test(groups="ch13")
	public void testRegex1() throws Exception {
		try {
			buildIndex();
			assert regexHitCount( "sa.[aeiou]s.*" ) == 2;
			cleanup();
		}
		finally {
			s.close();
		}
	}

	@Test(groups="ch13")
	public void testRegex2() throws Exception {
		try {
			buildIndex();
			assert regexHitCount( "sa[aeiou]s.*" ) == 0;
			cleanup();
		}
		finally {
			s.close();
		}
	}

	@Test(groups="ch13")
	public void testSpanRegex1() throws Exception {
		try {
			buildIndex();
			assert spanRegexHitCount( "sa.[aeiou]s", "woman", 5, true ) == 1;
			cleanup();
		}
		finally {
			s.close();
		}
	}

	@Test(groups="ch13")
	public void testSpanRegex2() throws Exception {
		try {
			buildIndex();
			assert spanRegexHitCount( "sa.[aeiou]s", "woman", 1, true ) == 0;
			cleanup();
		}
		finally {
			s.close();
		}
	}

	private int regexHitCount( String regex ) throws Exception {
		RegexQuery query = new RegexQuery( newTerm( regex ) );
		query.setRegexImplementation( new JakartaRegexpCapabilities() );

		org.hibernate.search.FullTextQuery hibQuery = s.createFullTextQuery( query, Dvd.class );
		List results = hibQuery.list();
		return results.size();
	}

	private int spanRegexHitCount( String regex1, String regex2, int slop, boolean ordered )
		throws Exception {
		SpanRegexQuery q1 = new SpanRegexQuery( newTerm( regex1 ) );
		SpanRegexQuery q2 = new SpanRegexQuery( newTerm( regex2 ) );
		SpanNearQuery query = new SpanNearQuery( new SpanQuery[]{q1, q2}, slop, ordered );

		org.hibernate.search.FullTextQuery hibQuery = s.createFullTextQuery( query, Dvd.class );
		List results = hibQuery.list();
		return results.size();
	}

	private Term newTerm( String value ) {
		return new Term( "description", value );
	}

	private void buildIndex() {
		s = Search.getFullTextSession( openSession() );
		tx = s.beginTransaction();

		for (int x = 0; x < texts.length; x++) {
			Dvd dvd = new Dvd();
			dvd.setId( x );
			dvd.setDescription( texts[x] );
			s.save( dvd );
		}
		tx.commit();
		s.clear();
	}

	private void cleanup() {
		tx = s.beginTransaction();
		for (Object element : s.createQuery( "from " + Dvd.class.getName() ).list()) {
			s.delete( element );
		}
		tx.commit();
	}

	protected Class[] getMappings() {
		return new Class[]{
			Dvd.class
		};
	}
}
