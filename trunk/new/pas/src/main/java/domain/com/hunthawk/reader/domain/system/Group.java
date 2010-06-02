/**
 * 
 */
package com.hunthawk.reader.domain.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

@Entity
@Table(name = "reader_system_group")
public class Group extends PersistentObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3733772811128608332L;
	private Integer id;
	private String name;
	
	@Id
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "id")
	public Integer getId()
	{
		return this.id;
	}
	public void setId(Integer id)
	{
		this.id = id;
	}
	@Column(name = "name", unique = true, nullable = false)
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
	}
}
