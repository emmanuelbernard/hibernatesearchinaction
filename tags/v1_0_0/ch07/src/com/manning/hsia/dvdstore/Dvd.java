package com.manning.hsia.dvdstore;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.hibernate.search.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Indexed
@Analyzer(impl = StandardAnalyzer.class)
public class Dvd {
	private Integer id;
	private String title;
	private String description;

	@Id
	@DocumentId
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Field(index = Index.TOKENIZED, store = Store.YES)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Field(index = Index.TOKENIZED, store = Store.YES)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}