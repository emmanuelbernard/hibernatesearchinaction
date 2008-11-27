package com.manning.hsia.ch13;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.hibernate.search.annotations.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Indexed
@Analyzer(impl = StandardAnalyzer.class)
public class Synonym {
	private String syn;
	private String word;
	private int id;

	@Field(store = Store.YES, index = Index.UN_TOKENIZED)
	public String getSyn() {
		return syn;
	}

	public void setSyn(String syn) {
		this.syn = syn;
	}

	@Field(store = Store.YES, index = Index.UN_TOKENIZED)
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	@Id
	@DocumentId
	@GeneratedValue
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}