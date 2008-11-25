package com.manning.hsia.dvdstore;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.hibernate.search.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Indexed
@Analyzer(impl = StandardAnalyzer.class)
public class Animal {
	@Id
	@DocumentId
	private Integer id;
	@Field(index = Index.TOKENIZED, store= Store.YES)
	private String name;


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