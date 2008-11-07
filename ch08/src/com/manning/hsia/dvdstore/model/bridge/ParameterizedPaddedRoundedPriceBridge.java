package com.manning.hsia.dvdstore.model.bridge;

import java.math.BigDecimal;
import java.util.Map;

import org.hibernate.search.bridge.ParameterizedBridge;
import org.hibernate.search.bridge.StringBridge;

/**
 * Round a price by range of <code>round</code>, going to the upper boundaries
 * pad the result with up to <code>pad</code> non-significant 0
 * Accept double and Double 
 */
public class ParameterizedPaddedRoundedPriceBridge implements StringBridge, ParameterizedBridge {
	private int pad = 6; //price of 1,000,000 online seems like a decent default target
	private double round = 1d; //by default round to the next non decimal amount
	
	public void setParameterValues(Map parameters) {
		if ( parameters.containsKey("pad") ) {
			pad = Integer.parseInt( (String) parameters.get("pad") );
		}
		
		if ( parameters.containsKey("round") ) {
			round = Double.parseDouble( (String) parameters.get("round") );
		}
	}

	public String objectToString(Object value) {
		if ( value == null ) return null;
		if (value instanceof BigDecimal) {
			long price = round( (BigDecimal) value );
			return pad(price);
		}
		else {
			throw new IllegalArgumentException(ParameterizedPaddedRoundedPriceBridge.class 
					+ " used one a non BigDecimal type: " + value.getClass() );
		}
		
		
	}

	private long round(BigDecimal price) {
		double rounded = Math.floor( price.doubleValue() / round ) * round;
		if ( rounded != price.doubleValue() ) rounded+= round; //we round up
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
