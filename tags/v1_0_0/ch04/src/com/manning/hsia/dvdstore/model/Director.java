package com.manning.hsia.dvdstore.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

/**
 * Example 4.13
 */

@Entity
@Indexed
public class Director {
	@Id @GeneratedValue @DocumentId private Integer id;
	@Field private String name;
	
	@OneToMany(mappedBy="director") 
	@ContainedIn         //director is contained in item index
	private Set<Item> items = new HashSet<Item>();
	
	public Set<Item> getItems() {
		return items;
	}
	public void setItems(Set<Item> items) {
		this.items = items;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
