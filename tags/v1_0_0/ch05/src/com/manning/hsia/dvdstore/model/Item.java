package com.manning.hsia.dvdstore.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.StandardFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.apache.solr.analysis.StopFilterFactory;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.AnalyzerDefs;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.validator.Digits;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import com.manning.hsia.dvdstore.util.SnowballPorterFilterFactory;

/**
 * Example 5.6, 5.7, 5.12
 */
@Entity
@Indexed
@Table(name="PRODUCTS")
@AnalyzerDefs( {
	@AnalyzerDef(name="applicationanalyzer",   //analyzer definition name 
		    tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class ),  //tokenizer factory
		    filters = { @TokenFilterDef(factory=LowerCaseFilterFactory.class),  //filter factory 
		                @TokenFilterDef(factory = StopFilterFactory.class,
		                        params = {  @Parameter(name="words",   //parameters passed to the filter factory 
		                        		               value="com/manning/hsia/dvdstore/stopwords.txt"),
		                                    @Parameter(name="ignoreCase", value="true") } ) 
		} ),
	@AnalyzerDef(name="englishSnowball",
		    tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class ),
		    filters = { @TokenFilterDef(factory=StandardFilterFactory.class),
						@TokenFilterDef(factory=LowerCaseFilterFactory.class),
		                @TokenFilterDef(factory = StopFilterFactory.class,    //stop word factory
		                        params = {  @Parameter(name="words",  
		                        		               value="com/manning/hsia/dvdstore/stopwords.txt"),  //file containing stop words
		                                    @Parameter(name="ignoreCase", value="false") } ), 
	                    @TokenFilterDef(factory = SnowballPorterFilterFactory.class,  //use Snowball filter
		                        params = @Parameter(name="language", value="English") )  //define the language
		} )
})
@Analyzer(definition="applicationanalyzer")  //Use a pre defined analyzer
public class Item {
	
	@Id @GeneratedValue
	@DocumentId
	@Column(name="PROD_ID")
	private Integer id;
	
	@Fields( { 
		@Field(name="title", index=Index.TOKENIZED, store=Store.YES),
		@Field(name="title_stemmer", 
				analyzer=@Analyzer(definition="englishSnowball"))  //title_stemmer uses Snowball fitler
	})
	@Boost(2)
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
