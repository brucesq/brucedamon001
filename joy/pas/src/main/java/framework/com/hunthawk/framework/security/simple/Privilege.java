/**
 * 
 */
package com.hunthawk.framework.security.simple;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.hunthawk.framework.Persistent;

/**
 * @author sunquanzhi
 *
 */
@Entity
@Table(name = "pams_privilege")
@SuppressWarnings("serial")
public class Privilege implements Persistent , Serializable{

	private String name;
	
	private String title;
	
	private Integer privilegeId;
	
	private Set<Role> roles = new HashSet<Role>();
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "privilegeId")
	public Integer getPrivilegeId()
	{
		return privilegeId;
	}
	public void setprivilegeId(Integer privilegeId)
	{
		this.privilegeId = privilegeId;
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
	@ManyToMany(mappedBy="privileges",fetch=FetchType.EAGER)
	public Set<Role> getRoles()
	{
		return this.roles;
	}
	public void setRoles(Set<Role> roles)
	{
		this.roles = roles;
	}
}
