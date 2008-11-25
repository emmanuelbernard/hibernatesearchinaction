package com.jboss.dvd.seam;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table( name = "CATEGORIES" )
@Cache( usage = CacheConcurrencyStrategy.READ_ONLY )
@Indexed
public class Category
	implements Serializable {
	private static final long serialVersionUID = 5544598397835376242L;
	int id;
	String name;

	@Id
	@GeneratedValue
	@Column( name = "CATEGORY" )
	@DocumentId
	public int getCategoryId() {
		return id;
	}

	public void setCategoryId( int id ) {
		this.id = id;
	}

	@Column( name = "NAME", nullable = false, unique = true, length = 50 )
	@Field( index = Index.TOKENIZED )
	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	@Override
	public boolean equals( Object other ) {
		if ( other == null || !( other instanceof Category ) ) {
			return false;
		}

		Category otherCategory = (Category) other;
		return ( getCategoryId() == otherCategory.getCategoryId() );
	}

	@Override
	public int hashCode() {
		return 37 * getCategoryId() + 97;
	}

}