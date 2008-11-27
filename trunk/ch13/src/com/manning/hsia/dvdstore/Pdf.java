package com.manning.hsia.dvdstore;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.hibernate.search.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Indexed
@Analyzer( impl = StandardAnalyzer.class )
public class Pdf {
	private Integer id;
	private String description;
	private String author;
	private String title;
	private String keywords;
	private String subject;
	private String summary;
	private String contents;

	public Pdf() {

	}

	@Id
//	@GeneratedValue
	@DocumentId
	public Integer getId() {
		return id;
	}

	public void setId( Integer id ) {
		this.id = id;
	}

	@Field( index = Index.TOKENIZED, store = Store.YES )
	public String getDescription() {
		return description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	@Field( index = Index.TOKENIZED, store = Store.YES )
	public String getAuthor() {
		return author;
	}

	public void setAuthor( String author ) {
		this.author = author;
	}

	@Field( index = Index.TOKENIZED, store = Store.YES )
	public String getTitle() {
		return title;
	}

	public void setTitle( String title ) {
		this.title = title;
	}

	@Field( index = Index.TOKENIZED, store = Store.YES )
	public String getKeywords() {
		return keywords;
	}

	public void setKeywords( String keywords ) {
		this.keywords = keywords;
	}

	@Field( index = Index.TOKENIZED, store = Store.YES )
	public String getSubject() {
		return subject;
	}

	public void setSubject( String subject ) {
		this.subject = subject;
	}

	@Field( index = Index.TOKENIZED, store = Store.YES )
	public String getSummary() {
		return summary;
	}

	public void setSummary( String summary ) {
		this.summary = summary;
	}

	@Field( index = Index.TOKENIZED, store = Store.YES )
	public String getContents() {
		return contents;
	}

	public void setContents( String contents ) {
		this.contents = contents;
	}
}
