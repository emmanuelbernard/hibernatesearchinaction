package com.manning.hsia.dvdstore.model;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.Digits;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Indexed
@Table(name="PRODUCTS")
public class Item {
	@Id @GeneratedValue
	@DocumentId
	@Column(name="PROD_ID")
	private Integer id;
	
	@Fields( 
			{@Field(index=Index.TOKENIZED, store=Store.YES), //stored to enable projection
			@Field(name="title_sort", index=Index.UN_TOKENIZED)	
			})
	@Column(name="TITLE")
	@NotNull @Length(max=100) 
	private String title;
	
	@Field 
	@Column(name="DESCRIPTION")
	@Length(max=5000)
	private String description;
	
	@Field(index=Index.UN_TOKENIZED, store=Store.YES)
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
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinTable(name="PRODUCTS_DISTRIBUTORS")
	@IndexedEmbedded
	private Distributor distributor;
	
	@ManyToMany
	@JoinTable(name="PRODUCT_CATEGORY", 
			joinColumns=@JoinColumn(name="PROD_ID"), 
			inverseJoinColumns=@JoinColumn(name="CATEGORY"))
	private Set<Category> categories;
	
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

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}
	
}
