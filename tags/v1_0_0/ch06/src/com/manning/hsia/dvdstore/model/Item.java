package com.manning.hsia.dvdstore.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.Digits;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Indexed
@Table(name="PRODUCTS")
@Inheritance(strategy=InheritanceType.JOINED)
@BatchSize(size=20)  //reduce database round trips
public class Item {
	@Id @GeneratedValue
	@DocumentId
	@Column(name="PROD_ID")
	private Integer id;
	
	@Fields( 
			{@Field(index=Index.TOKENIZED, store=Store.YES),  //property is stored in index
			 @Field(name="title_sort", index=Index.UN_TOKENIZED)  //properties untokenized can be sorted
			})
	@Column(name="TITLE")
	@NotNull @Length(max=100) 
	private String title;
	
	@Field 
	@Column(name="DESCRIPTION")
	@Length(max=5000)
	private String description;
	
	@Field(index=Index.UN_TOKENIZED, store=Store.YES)  //property is stored in index, properties untokenized can be sorted
	@Column(name="ASIN")
	@Length(max=16)
	private String ean;
	
	@Column(name="IMAGE_URL")
	@Length(max=256)
	private String imageURL;
	
	@Column(name="PRICE")
	@NotNull
	@Digits(integerDigits=10, fractionalDigits=2)
	private BigDecimal price;
	
	@ManyToOne
	@JoinTable(name="PRODUCTS_DISTRIBUTORS")
	private Distributor distributor;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}
	
}
