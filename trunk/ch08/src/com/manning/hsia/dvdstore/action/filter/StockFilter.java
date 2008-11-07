package com.manning.hsia.dvdstore.action.filter;

import java.io.IOException;
import java.util.BitSet;
import java.util.Map;

import org.apache.commons.collections.map.ReferenceMap;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.Filter;
import org.hibernate.search.engine.DocumentBuilder;

import com.manning.hsia.dvdstore.action.StockAction;
import com.manning.hsia.dvdstore.action.StockActionImpl;
import com.manning.hsia.dvdstore.model.Item;

/**
 * Example 8.12
 */
public class StockFilter extends Filter {
	private volatile long lastUpdateTime;  //update timestamp

	@SuppressWarnings("unchecked") 
	private final Map<IndexReader, BitSet> cache = 
		new ReferenceMap(ReferenceMap.SOFT, ReferenceMap.HARD);  //keep cache in a SoftHashMap

	@Override
	public BitSet bits(IndexReader reader) throws IOException {
		StockAction action = getStockAction();    //retrieve the service
		long lastUpdateTime = action.geLastUpdateTime();
		if ( lastUpdateTime != this.lastUpdateTime ) {
			synchronized (cache) {  //clear outdated cache  
				cache.clear();
			}
		}
		synchronized (cache) {
			BitSet cached = cache.get(reader);  //check if in cache already
			if (cached != null) return cached;
		}
		//not in cache, build info
		final BitSet bitSet = getAllPositiveBitSet( reader.maxDoc() ); //by default, all documents pass
		
		Term clazzTerm = new Term(DocumentBuilder.CLASS_FIELDNAME, Item.class.getName() );
		if ( reader.docFreq( clazzTerm ) == 0) {  //no need to filter
			//index does not contain Item objects
			//no-op
		}
		else {
			//for each item out of stock, find the corresponding document id by item id
			//and switch off the corresponding bit
			for ( String ean : action.getEanOfItemsOutOfStock() ) {  //invoke external service
				Term term = new Term( "ean", ean );
				TermDocs termDocs = reader.termDocs(term);  //find document by ean
				while ( termDocs.next() ) {
					bitSet.clear(termDocs.doc());
				}
			}
		}
		
		synchronized (cache) {
			cache.put(reader, bitSet);  //put results in the cache
		}
		this.lastUpdateTime = lastUpdateTime;  //update timestamp
		return bitSet;
	}

	private BitSet getAllPositiveBitSet(int maxDoc) {
		final BitSet bitSet = new BitSet(maxDoc);
		bitSet.set(0, maxDoc-1);        //new BitSet with all bits on
		return bitSet;
	}

	private StockAction getStockAction() {
		return new StockActionImpl();
	}

}
