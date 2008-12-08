package com.manning.hsia.dvdstore.model;

import javax.persistence.Embeddable;

import org.hibernate.search.annotations.Field;

@Embeddable
public class Country {
	@Field private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
