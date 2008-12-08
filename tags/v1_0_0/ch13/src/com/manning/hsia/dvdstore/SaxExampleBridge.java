package com.manning.hsia.dvdstore;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.SearchException;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class SaxExampleBridge extends DefaultHandler implements FieldBridge {

	public void set( String name,
	                 Object value,
	                 Document document,
	                 LuceneOptions options ) {
		CD cd = (CD) value;
		String xml = cd.getPriceData();
		if ( xml == null ) {
			return;
		}
		InputSource source = new InputSource( new StringReader( xml ) );

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			SaxHandler handler = new SaxHandler( options, document );
			parser.parse( source, handler );
		}
		catch (SAXException s) {
			throw ( new SearchException( "unable to read cd price data", s ) );
		}
		catch (ParserConfigurationException p) {
			throw ( new SearchException( "unable to read cd price data", p ) );
		}
		catch (IOException i) {
			throw ( new SearchException( "unable to read cd price data", i ) );
		}
	}

	/**
	 * FieldBridge implementations must be thread safe. So it is necessary to
	 * parse the xml in its own instance of a handler to guarantee this.
	 */
	private class SaxHandler extends DefaultHandler {

		Map<String, String> attrs;
		List<Field> mFieldList = new ArrayList<Field>();
		Field.Store mStore;
		Field.Index mIndex;
		Field.TermVector mVector;
		Float mBoost;
		Document mDocument;
		StringBuilder text = new StringBuilder();

		public SaxHandler( LuceneOptions options, Document document ) {
			mStore = options.getStore();
			mIndex = options.getIndex();
			mVector = options.getTermVector();
			mBoost = options.getBoost();
			mDocument = document;
		}

		public void startElement( String uri, String localName, String qName, Attributes attributes ) {
			text.delete( 0, text.length() );
			if ( attributes.getLength() > 0 ) {
				attrs = new HashMap<String, String>();
				for (int x = 0; x < attributes.getLength(); x++) {
					attrs.put( attributes.getQName( x ), attributes.getValue( x ) );
				}
			}
		}

		public void endElement( String uri, String localName, String qName ) {
			if ( !qName.equals( "CD" ) ) {
				Field field = new Field( qName.toLowerCase(), text.toString(), mStore, mIndex, mVector );
				if ( mBoost != null ) field.setBoost( mBoost );
				mFieldList.add( field );

				if ( attrs.size() > 0 ) {
					Set<String> keys = attrs.keySet();
					for (String key : keys) {
						String attrValue = attrs.get( key );
						field = new Field( key.toLowerCase(), attrValue, mStore, mIndex, mVector );
						mFieldList.add( field );
					}
					attrs.clear();
				}
			}
		}

		public void characters( char[] ch, int start, int length ) {
			text.append( ch, start, length );
		}

		public void startDocument() {
			mFieldList.clear();
		}

		public void endDocument() {
			for (Field f : mFieldList) {
				mDocument.add( f );
			}
		}
	}
}
