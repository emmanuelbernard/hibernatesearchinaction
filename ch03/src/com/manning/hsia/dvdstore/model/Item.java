package com.manning.hsia.dvdstore.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;

/**
 * Example 3.4, 3.5, 3.15, 3.17
 */
@Entity  //Superclasses do not have to be marked @Indexed 
public abstract class Item {
	
	@Id @GeneratedValue
	@DocumentId      //mark the id property with @DocumentId 
	private Integer id;
	
	@Fields({
		@Field(index=Index.TOKENIZED),    //same property indexed multiple times 
		@Field(name="title_sort", index=Index.UN_TOKENIZED)  //use a different field name
	})
	@Boost(2)  //boost title field 
	private String title;
	
	@Field  //Superclasses can contain indexed properties
	private String description;
	
	private String imageURL;
	
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

	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
}
