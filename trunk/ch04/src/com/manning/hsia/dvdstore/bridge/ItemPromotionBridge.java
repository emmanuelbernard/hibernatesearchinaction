package com.manning.hsia.dvdstore.bridge;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

public class ItemPromotionBridge implements FieldBridge {

	public void set(String name, Object value, Document document,
			LuceneOptions luceneOptions) {
		// TODO Auto-generated method stub
		
	}

}
