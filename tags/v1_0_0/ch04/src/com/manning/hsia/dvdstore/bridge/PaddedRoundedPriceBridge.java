package com.manning.hsia.dvdstore.bridge;
import org.hibernate.search.bridge.StringBridge;

/**
 * Round a price by range of 5, going to the upper boundaries
 * pad the result with up to 3 non-significant 0
 * Accept double and Double 
 * 
 * Example 4.4
 */
public class PaddedRoundedPriceBridge implements StringBridge {
	public static int PAD = 3;
	public static double ROUND = 5d;
	
	public String objectToString(Object value) {  //convert property value into s String
		if ( value == null ) return null;        //null strings are not indexed
		
		if (value instanceof Double) {
			long price = round( (Double) value );
			return pad(price);
		}
		else {
			throw new IllegalArgumentException(PaddedRoundedPriceBridge.class //raise runtime exceptions on error
					+ " used one a non double type: " + value.getClass() );
		}
		
		
	}

	private long round(double price) {
		double rounded = Math.floor( price / ROUND ) * ROUND;
		if ( rounded != price ) rounded+= ROUND; //we round up
		return (long) rounded;
	}
 
	private String pad(long price) {                     //padding implementations
		String rawLong = Long.toString(price);
		if (rawLong.length() > PAD) 
			throw new IllegalArgumentException( "Try to pad on a number too big" );
		
		StringBuilder paddedLong = new StringBuilder();
		for ( int padIndex = rawLong.length() ; padIndex < PAD ; padIndex++ ) {
			paddedLong.append('0');
		}
		return paddedLong.append( rawLong ).toString();
	}
}
