package com.manning.hsia.dvdstore.bridge;

import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

/**
 * Only Map<String, String> are accepted as value
 * For each key in the map, create a field name.<key> (lowercase) and index its value
 * For example the map [english:good, french:moyen, spanish:excellente] 
 * will result in the following fields in the index
 * <pre>
 * name.english => good
 * name.french => moyen
 * name.spanish => excellente
 * </pre>
 * 
 * Example 4.7
 */
public class MapKeyPerFieldBridge implements FieldBridge {  //implements FieldBridge

	public void set(String name,  //proposed field name 
					Object value, //value to index 
					Document document,  //Lucene Document instance
					LuceneOptions luceneOptions) {  //various indexing strateg
		//we expect a Map here
		if (! (value instanceof Map) ) {
			throw new IllegalArgumentException("support limited to Map<String, String>");
		}
		
		@SuppressWarnings("unchecked") 
		Map<String, String> map = (Map<String, String>) value;
		for (Map.Entry<String, String> entry :  map.entrySet() ) {
			Field field = new Field(                  //create the new field
					name + '.' + entry.getKey().toLowerCase(), 
					entry.getValue().toLowerCase(), 
					luceneOptions.getStore(), 
					luceneOptions.getIndex(), 
					luceneOptions.getTermVector() 
			);
			field.setBoost( luceneOptions.getBoost() );  //inject boost
			document.add( field );    //add new field to document
		}
	}
}
