package com.manning.hsia.dvdstore.action;

public class ItemView {
	private final String ean;
	private final String title;
	private float score;
	
	public ItemView(String ean, String title) {
		super();
		this.ean = ean;
		this.title = title;
	}
	
	public ItemView(String ean, String title, float score) {
		this(ean, title);
		this.score = score;
	}
	
	public String getEan() {
		return ean;
	}
	public String getTitle() {
		return title;
	}

	public float getScore() {
		return score;
	}
}
