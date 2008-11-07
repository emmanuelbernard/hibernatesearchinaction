package com.manning.hsia.dvdstore.bridge;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;

import com.manning.hsia.dvdstore.model.PersonPK;

/**
 * two-way bridge storing an embedded id
 * 
 * Example 4.8
 */
public class PersonPkBridge implements TwoWayFieldBridge {

	public Object get(String name, Document document) {  //build composite identifier from document
		PersonPK id = new PersonPK();

		Field field = document.getField( name + ".firstName" );
		id.setFirstName( field.stringValue() );
		
		field = document.getField( name + ".lastName" );
		id.setLastName( field.stringValue() );
		return id;
	}

	public String objectToString(Object object) {  //create unique string from identifier
		PersonPK id = (PersonPK) object;
		
		StringBuilder sb = new StringBuilder();
		sb.append( id.getFirstName() )
			.append( " " )
			.append( id.getLastName() );

		return sb.toString();
	}

	public void set(String name, 
					Object value, 
					Document document, 
					LuceneOptions luceneOptions) {
		PersonPK id = (PersonPK) value;
		Store store = luceneOptions.getStore();
		Index index = luceneOptions.getIndex();
		TermVector termVector = luceneOptions.getTermVector();
		Float boost = luceneOptions.getBoost();

		Field field = new Field( name + ".firstName", id.getFirstName(),   //store each sub property in a field
				store, index, termVector );
		field.setBoost( boost );
		document.add( field );
		
		field = new Field( name + ".lastName", id.getLastName(), 
				store, index, termVector );
		field.setBoost( boost );
		document.add( field );

		field = new Field( name, objectToString( id ),   //store unique representation in named field
				store, index, termVector );
		field.setBoost( boost );
		document.add( field );
	}

}
