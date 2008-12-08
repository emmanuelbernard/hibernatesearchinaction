package com.manning.hsia.dvdstore;

import org.hibernate.search.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Indexed
public class Employee {
	private Integer id;
	private String lastname;
	private String dept;

	@Field(store=Store.YES, index = Index.UN_TOKENIZED)
	@DateBridge(resolution = Resolution.DAY)
	public Date getHireDate() {
		return hireDate;
	}

	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}

	private Date hireDate;

	public Employee() {
	}

	public Employee(Integer id, String lastname, String dept) {
		this.id = id;
		this.lastname = lastname;
		this.dept = dept;
	}

	@Id
	@DocumentId
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Field( index = Index.NO, store = Store.YES )
	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@Field( index = Index.TOKENIZED, store = Store.YES )
	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}
}