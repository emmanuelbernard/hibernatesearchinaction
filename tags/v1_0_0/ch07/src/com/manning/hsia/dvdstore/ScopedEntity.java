package com.manning.hsia.dvdstore;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.hibernate.search.annotations.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Indexed
@Analyzer(impl = StandardAnalyzer.class)
public class ScopedEntity {
	@Id
	@GeneratedValue
	@DocumentId
	private Integer id;

	@Field(index = Index.TOKENIZED, store = Store.YES)
	private String field1;

	@Field(index = Index.TOKENIZED, store = Store.YES)
	@Analyzer(impl = WhitespaceAnalyzer.class)
	private String field2;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}
}
