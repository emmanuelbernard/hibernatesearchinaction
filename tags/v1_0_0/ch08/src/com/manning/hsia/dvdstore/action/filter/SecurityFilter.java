package com.manning.hsia.dvdstore.action.filter;

import java.io.IOException;
import java.util.BitSet;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Filter;

public class SecurityFilter extends Filter {

	@Override
	public BitSet bits(IndexReader reader) throws IOException {
		BitSet bitSet = new BitSet( reader.maxDoc() );
		//reverse it
		bitSet.flip(0, reader.maxDoc() - 1 );
		return bitSet;
	}

}
