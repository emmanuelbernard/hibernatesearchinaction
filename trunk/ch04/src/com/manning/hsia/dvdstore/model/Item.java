package com.manning.hsia.dvdstore.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKey;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.ClassBridge;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.Store;

import com.manning.hsia.dvdstore.bridge.ItemPromotionBridge;
import com.manning.hsia.dvdstore.bridge.MapKeyPerFieldBridge;
import com.manning.hsia.dvdstore.bridge.PaddedRoundedPriceBridge;

/**
 * Example 4.1, 4.7, 4.9, 4.12, 4.13
 */
@Entity
@Indexed
@ClassBridge(    //Mark the use of a class bridge 
		name="promotion",            //recommended namespace
		index=Index.UN_TOKENIZED,    //Class Bridges have properties similar to @Field 
		impl=ItemPromotionBridge.class )  //class bridge implementation used 
public class Item {
	@Id @GeneratedValue
	@DocumentId
	private Integer id;
	
	@Fields({
		@Field(index=Index.TOKENIZED),
		@Field(name="title_sort", index=Index.UN_TOKENIZED)
	})
	@Boost(2)
	private String title;
	
	@Field
	private String description;
	
	@Field(index=Index.UN_TOKENIZED, store=Store.YES)
	private String ean;
	
	@Field
	@FieldBridge(                                         //property marked to use a bridge
			impl=PaddedRoundedPriceBridge.class,          //declare the bridge implementation
			params= { @Parameter(name="pad", value="3"),  //optionally provide parameters 
					  @Parameter(name="round", value="5") }
			)
	private double price;
	
	private String imageURL;
	
	@IndexedEmbedded    //mark the association for indexing 
	private Rating rating;
	
	@CollectionOfElements 
	@IndexedEmbedded        //collection elements embedded in the document
	private Collection<Country> distributedIn = new ArrayList<Country>();
	
	@Field(store=Store.YES)
	@FieldBridge(impl=MapKeyPerFieldBridge.class)   //bridge implementation
	@CollectionOfElements
	@MapKey
	private Map<String, String> ratePerDubbing = new HashMap<String, String>();
	
	@ManyToMany 
	@IndexedEmbedded  //embed actors when indexing
	private Set<Actor> actors = new HashSet<Actor>();
	
	@ManyToOne 
	@IndexedEmbedded   //embed director when indexing
	private Director director;
	
	public Set<Actor> getActors() {
		return actors;
	}

	public void setActors(Set<Actor> actors) {
		this.actors = actors;
	}

	public Director getDirector() {
		return director;
	}

	public void setDirector(Director director) {
		this.director = director;
	}

	public Integer getId() {
		return id;
	}
	
	public String getEan() {
		return ean;
	}
	public void setEan(String ean) {
		this.ean = ean;
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

	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Map<String, String> getRatePerDubbing() {
		return ratePerDubbing;
	}

	public void setRatePerDoubling(Map<String, String> ratePerDubbing) {
		this.ratePerDubbing = ratePerDubbing;
	}

	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public Collection<Country> getDistributedIn() {
		return distributedIn;
	}

	public void setDistributedIn(Collection<Country> distributedIn) {
		this.distributedIn = distributedIn;
	}
	
}
