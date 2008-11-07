package com.manning.hsia.dvdstore.model;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

//@javax.persistence.Entity
@Indexed
public class Pizza extends Item {
	@Field
	@Enumerated(EnumType.STRING)
	private PizzaSize size;

	public PizzaSize getSize() {
		return size;
	}

	public void setSize(PizzaSize size) {
		this.size = size;
	}
	
	
}
