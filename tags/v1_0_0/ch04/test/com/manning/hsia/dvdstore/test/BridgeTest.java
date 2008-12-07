package com.manning.hsia.dvdstore.test;

import org.hibernate.search.bridge.StringBridge;
import org.hibernate.search.bridge.TwoWayStringBridge;
import org.testng.annotations.Test;

import com.manning.hsia.dvdstore.bridge.PaddedPriceBridge;
import com.manning.hsia.dvdstore.bridge.PaddedRoundedPriceBridge;

public class BridgeTest {
	@Test(groups="ch04")
	public void testPaddingAndRounding() {
		StringBridge bridge = new PaddedRoundedPriceBridge();
		assert "000".equals( bridge.objectToString(0d) ) : "found " + bridge.objectToString(0d);
		assert "005".equals( bridge.objectToString(0.5d) );
		assert "005".equals( bridge.objectToString(4.9d) );
		assert "005".equals( bridge.objectToString(5d) );
		assert "010".equals( bridge.objectToString(5.1d) );
	}
	
	@Test(groups="ch04")
	public void testPadding() {
		StringBridge bridge = new PaddedPriceBridge();
		assert "000.0".equals( bridge.objectToString(0d) ) : bridge.objectToString(0d);
		assert "000.5".equals( bridge.objectToString(0.5d) );
		assert "004.9".equals( bridge.objectToString(4.9d) );
		assert "005.0".equals( bridge.objectToString(5d) );
		assert "005.1".equals( bridge.objectToString(5.1d) );
		assert "555.55".equals( bridge.objectToString(555.55d) );
		
		assert bridge.objectToString(5d).compareTo( bridge.objectToString(5.1d) ) < 0;
		assert bridge.objectToString(5.01d).compareTo( bridge.objectToString(5.1d) ) < 0;
	}
	
	@Test(groups="ch04", expectedExceptions={IllegalArgumentException.class})
	public void testOutOfBoundaries() {
		StringBridge bridge = new PaddedPriceBridge();
		bridge.objectToString(555555.55d);
	}
	
	@Test(groups="ch04")
	public void testTwoWayStringBridge() {
		TwoWayStringBridge bridge = new PaddedPriceBridge();
		assert 12.4d == (Double) bridge.stringToObject( bridge.objectToString(12.4d) );
	}
}
