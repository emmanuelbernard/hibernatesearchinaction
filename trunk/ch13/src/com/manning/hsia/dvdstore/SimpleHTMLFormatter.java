package com.manning.hsia.dvdstore;

import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;

public class SimpleHTMLFormatter implements Formatter {
	String preTag;
	String postTag;

	public SimpleHTMLFormatter(String preTag, String postTag) {
		this.preTag = preTag;
		this.postTag = postTag;
	}

	public SimpleHTMLFormatter() {
		this.preTag = "<B>";
		this.postTag = "</B>";
	}

	public String highlightTerm(String originalText, TokenGroup tokenGroup) {
		StringBuffer returnBuffer;
		if (tokenGroup.getTotalScore() > 0) {
			returnBuffer = new StringBuffer();
			returnBuffer.append(preTag);
			returnBuffer.append(originalText);
			returnBuffer.append(postTag);
			return returnBuffer.toString();
		}
		return originalText;
	}
}
