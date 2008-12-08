package com.manning.hsia.dvdstore.model;

import javax.persistence.Entity;

import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;

/**
 * Example 3.2
 */
@Entity
@Indexed(index="Drinkable")  //index name can be customized
@Boost(.75f)    //Reduce the score of all Drinks 
public class Drink extends Item {
	@Field(index=Index.UN_TOKENIZED)
	private boolean alcoholicBeverage;

	public boolean isAlcoholicBeverage() {
		return alcoholicBeverage;
	}

	public void setAlcoholicBeverage(boolean alcoholicBeverage) {
		this.alcoholicBeverage = alcoholicBeverage;
	}
	
}
