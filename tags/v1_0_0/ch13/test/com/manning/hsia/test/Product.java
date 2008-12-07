package com.manning.hsia.test;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.hibernate.search.annotations.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Analyzer( impl = StandardAnalyzer.class )
@Indexed
public class Product
	implements Serializable {
	private static final long serialVersionUID = -5378546367347755065L;

	long productId;
	String title;
	String description;

	@Id
	@GeneratedValue
	@DocumentId
	public long getProductId() {
		return productId;
	}

	public void setProductId( long id ) {
		this.productId = id;
	}

	@Field( index = Index.TOKENIZED, store = Store.YES )
	public String getTitle() {
		return title;
	}

	public void setTitle( String title ) {
		this.title = title;
	}

	@Field( index = Index.TOKENIZED, store = Store.YES )
	public String getDescription() {
		return description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}
}