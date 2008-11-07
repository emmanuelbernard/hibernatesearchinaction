package com.manning.hsia.dvdstore.bridge;

import java.util.Map;

import org.hibernate.search.bridge.ParameterizedBridge;
import org.hibernate.search.bridge.StringBridge;

/**
 * Round a price by range of <code>round</code>, going to the upper boundaries
 * pad the result with up to <code>pad</code> non-significant 0
 * Accept double and Double
 * 
 * Example 4.6
 */
public class ParameterizedPaddedRoundedPriceBridge implements StringBridge, ParameterizedBridge {
	private int pad = 6; //price of 1,000,000 online seems like a decent default target
	private double round = 1d; //by default round to the next non decimal amount
	
	public void setParameterValues(Map parameters) {   //parameters are injected in setParameterValues 
		if ( parameters.containsKey("pad") ) {
			pad = Integer.parseInt( (String) parameters.get("pad") );
		}
		
		if ( parameters.containsKey("round") ) {
			round = Double.parseDouble( (String) parameters.get("round") );
		}
	}

	public String objectToString(Object value) {
		if ( value == null ) return null;
		if (value instanceof Double) {
			long price = round( (Double) value );
			return pad(price);
		}
		else {
			throw new IllegalArgumentException(PaddedRoundedPriceBridge.class 
					+ " used one a non double type: " + value.getClass() );
		}
		
		
	}

	private long round(double price) {
		double rounded = Math.floor( price / round ) * round;
		if ( rounded != price ) rounded+= round; //we round up
		return (long) rounded;
	}

	private String pad(long price) {
		String rawLong = Long.toString(price);
		if (rawLong.length() > pad) 
			throw new IllegalArgumentException( "Try to pad on a number too big" );
		StringBuilder paddedLong = new StringBuilder( );
		for ( int padIndex = rawLong.length() ; padIndex < pad ; padIndex++ ) {
			paddedLong.append('0');
		}
		return paddedLong.append( rawLong ).toString();
	}
}
