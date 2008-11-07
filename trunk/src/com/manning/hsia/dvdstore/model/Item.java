package com.manning.hsia.dvdstore.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

/**
 * Example 2.6
 */
@Entity
@Indexed  //Mark for indexing
public class Item {
	
	@Id @GeneratedValue
	@DocumentId  //Mark id property shared by Core and Search
	private Integer id;
	
	@Field   //Mark for indexing using tokenization
	private String title;
	
	@Field
	private String description;
	
	@Field(index=Index.UN_TOKENIZED, store=Store.YES)  //Mark for indexing without tokenization
	private String ean;
	
	private String imageURL;  //This property is not indexed (default)
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEan() {
		return ean;
	}
	public void setEan(String ean) {
		this.ean = ean;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
}
