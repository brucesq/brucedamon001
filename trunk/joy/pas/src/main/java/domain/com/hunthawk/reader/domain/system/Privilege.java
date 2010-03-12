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

/**
 * @author sunquanzhi
 *
 */
@Entity
@Table(name = "reader_system_privilege")
public class Privilege extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2446204184125123322L;

	private String name;
	
	private String title;
	
	private Integer id;
	
	@Id
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "id")
	public Integer getId()
	{
		return id;
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
	@Column(name = "title" , nullable = false, length = 30)
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}

	
	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
	}
}
