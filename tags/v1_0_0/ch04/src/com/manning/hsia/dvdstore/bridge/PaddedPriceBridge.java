package com.manning.hsia.dvdstore.bridge;

import org.hibernate.search.bridge.TwoWayStringBridge;

/**
 * pad a price with up to 3 non-significant 0
 * Accept double and Double 
 * 
 * Example 4.5
 */
public class PaddedPriceBridge implements TwoWayStringBridge {   //implements TwoWayStringBridge
	public static int PAD = 3;

	public String objectToString(Object value) {
		if ( value == null ) return null;
		if (value instanceof Double) {
			return pad( (Double) value );
		}
		else {
			throw new IllegalArgumentException(PaddedRoundedPriceBridge.class 
					+ " used one a non double type: " + value.getClass() );
		}
		
		
	}
	
	public Object stringToObject(String price) {  //reverse objectToString work
		return Double.parseDouble(price);
	}

	private String pad(double price) {
		String rawDouble = Double.toString(price);
		int dotIndex = rawDouble.indexOf('.');
		if (dotIndex == -1) dotIndex = rawDouble.length();
		if (dotIndex > PAD) 
			throw new IllegalArgumentException( "Try to pad on a number too big" );
		StringBuilder paddedLong = new StringBuilder( );
		for ( int padIndex = dotIndex ; padIndex < PAD ; padIndex++ ) {
			paddedLong.append('0');
		}
		return paddedLong.append( rawDouble ).toString();
	}
}
