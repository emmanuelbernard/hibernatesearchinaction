package com.manning.hsia.dvdstore.model;

import javax.persistence.Embeddable;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;

/**
 * Example 4.9
 */
@Embeddable
public class Rating {
	@Field(index=Index.UN_TOKENIZED) private Integer overall;
	@Field(index=Index.UN_TOKENIZED) private Integer scenario;   //mark properties for indexing
	@Field(index=Index.UN_TOKENIZED) private Integer soundtrack;
	@Field(index=Index.UN_TOKENIZED) private Integer picture;
	
	public Integer getOverall() {
		return overall;
	}
	public void setOverall(Integer overall) {
		this.overall = overall;
	}
	public Integer getScenario() {
		return scenario;
	}
	public void setScenario(Integer scenario) {
		this.scenario = scenario;
	}
	public Integer getSoundtrack() {
		return soundtrack;
	}
	public void setSoundtrack(Integer soundtrack) {
		this.soundtrack = soundtrack;
	}
	public Integer getPicture() {
		return picture;
	}
	public void setPicture(Integer picture) {
		this.picture = picture;
	}
	
	
}
