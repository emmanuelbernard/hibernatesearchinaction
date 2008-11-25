package com.manning.hsia.dvdstore.ex12_17;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.hibernate.search.annotations.*;

/**
 * @author John Griffin
 */
@Entity
@Indexed
@Analyzer(impl = StandardAnalyzer.class)
public class ElectricalProperties {
	private int id;
	private String content;

	public ElectricalProperties() {

	}

	public ElectricalProperties(int id, String content) {
		this.id = id;
		this.content = content;
	}

	@Field( index = Index.TOKENIZED, store = Store.YES, termVector = TermVector.WITH_POSITION_OFFSETS )
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
