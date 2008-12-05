package com.manning.hsia.dvdstore.model;

import javax.persistence.Entity;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

/**
 * Example 3.1
 */
@Entity
@Indexed  //mark the entity as @Indexed 
public class Dvd extends Item {
	
	@Field(index=Index.UN_TOKENIZED, store=Store.YES)
	private String ean;
	
	public String getEan() {
		return ean;
	}
	public void setEan(String ean) {
		this.ean = ean;
	}
	
}
