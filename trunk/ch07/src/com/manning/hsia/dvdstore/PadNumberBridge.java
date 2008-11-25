package com.manning.hsia.dvdstore;

import org.hibernate.search.bridge.StringBridge;

public class PadNumberBridge implements StringBridge {
	private final int PAD = 5;

	public String objectToString(Object value) {
		if (value == null) return null;
		int num = 0;
		if (value instanceof Integer) {
			num = (Integer) value;
		} else {
			throw new IllegalArgumentException("PadNumberBridge.class " +
				"received a non-int type " + value.getClass());
		}
		return pad(num);
	}

	private String pad(int num) {
		String rawInt = Integer.toString(num);
		if (rawInt.length() > PAD)
			throw new IllegalArgumentException(" integer too large to pad");
		StringBuilder paddedInt = new StringBuilder();
		for (int padIndex = rawInt.length(); padIndex < PAD; padIndex++)
			paddedInt.append("0");
		return paddedInt.append(rawInt).toString();
	}
}
