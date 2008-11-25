package com.manning.hsia.dvdstore;

import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.testng.annotations.Test;

import java.io.StringReader;
import java.util.ArrayList;


public class TestStopAnalyzer {
	@Test
	public void testStopAnalyzer() throws Exception {
		String phrase = "The Britannic, Olympic and Titanic were the White Star liners.";
		StringReader reader = new StringReader( phrase );

		StopAnalyzer analyzer = new StopAnalyzer();
		TokenStream stream = null;
		try {
			stream = analyzer.tokenStream( phrase, reader );
			ArrayList<String> terms = new ArrayList<String>();

			while (true) {
				Token token = stream.next();
				if ( token == null ) {
					break;
				}
				String term = extractToken( token );
				terms.add( term );
			}
			assert terms.size() == 7 : "incorrect term count";
			assert terms.get( 0 ).equals( "britannic" );
			assert terms.get( 6 ).equals( "liners" );

			for (String term : terms) {
				System.out.println( term );
			}
		}
		finally {
			if ( stream != null ) {
				stream.close();
			}
		}
	}

	private String extractToken( Token token ) {
		char[] chars = token.termBuffer();

		return new String( chars, 0, token.termLength() );
	}
}
