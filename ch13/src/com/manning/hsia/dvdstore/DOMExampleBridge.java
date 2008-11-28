package com.manning.hsia.dvdstore;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.xerces.dom.TextImpl;
import org.apache.xerces.parsers.DOMParser;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class DOMExampleBridge implements FieldBridge {

	public void set( String name,
	                 Object value,
	                 Document document,
	                 LuceneOptions options ) {

		CDDOM cd = (CDDOM) value;
		String xml = cd.getPriceData();
		if ( xml == null ) {
			return;
		}

		InputSource source = new InputSource( new StringReader( xml ) );

		DOMParser parser = new DOMParser();
		try {
			parser.parse( source );
			org.w3c.dom.Document xmlDocument =
				parser.getDocument();
			new DOMHandler( xmlDocument, document, options );
		}
		catch (SAXException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class DOMHandler {
		List<Field> mFieldList = new ArrayList<Field>();
		Field mField;
		Field.Store mStore;
		Field.Index mIndex;
		Field.TermVector mVector;
		Float mBoost;

		public DOMHandler( org.w3c.dom.Document xmlDocument, Document document, LuceneOptions options ) {
			mStore = options.getStore();
			mIndex = options.getIndex();
			mVector = options.getTermVector();
			mBoost = options.getBoost();

			traverse( xmlDocument.getDocumentElement() );

			for (Field field : mFieldList) {
				document.add( field );
			}
		}

		private void traverse( Node node ) {
			if ( node == null ) {
				return;
			}

			int type = node.getNodeType();
			switch (type) {
				case Node.ELEMENT_NODE: {
					NamedNodeMap attrs = node.getAttributes();

					for (int x = 0; x < attrs.getLength(); x++) {
						Node attrNode = attrs.item( x );
						Field field =
							new Field( attrNode.getLocalName().toLowerCase(),
								attrNode.getNodeValue(),
								mStore,
								mIndex,
								mVector );
						if ( mBoost != null ) field.setBoost( mBoost );
						mFieldList.add( field );
					}

					NodeList children = node.getChildNodes();
					if ( children != null ) {
						int len = children.getLength();
						for (int i = 0; i < len; i++) {
							traverse( children.item( i ) );
						}
					}
					break;
				}
				case Node.TEXT_NODE: {
					if ( node instanceof TextImpl ) {
						if ( !( ( (TextImpl) node )
							.isIgnorableWhitespace() ) ) {
							Field field =
								new Field( node.getParentNode()
									.getLocalName().toLowerCase(),
									node.getNodeValue(),
									Field.Store.YES,
									Field.Index.ANALYZED );
							mFieldList.add( field );
						}
					}
					break;
				}
			}
		}
	}
}
