package com.manning.hsia.dvdstore.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;

import com.manning.hsia.dvdstore.bridge.PersonPkBridge;

/**
 * Example 4.8
 */
@Entity
@Indexed
public class Person {
	@EmbeddedId @DocumentId                 //Embedded id
	@FieldBridge(impl=PersonPkBridge.class)  //use the custom field bridge
	private PersonPK id;
	
	@Field(index=Index.UN_TOKENIZED) private long age;
	
	public PersonPK getId() {
		return id;
	}
	public void setId(PersonPK id) {
		this.id = id;
	}
	public long getAge() {
		return age;
	}
	public void setAge(long age) {
		this.age = age;
	}
}
