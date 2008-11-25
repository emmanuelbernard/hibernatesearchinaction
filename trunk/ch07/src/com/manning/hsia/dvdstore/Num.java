package com.manning.hsia.dvdstore;

import org.hibernate.search.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Indexed
public class Num {
	private int id;
	private int number;

	@Id
	@DocumentId
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}

