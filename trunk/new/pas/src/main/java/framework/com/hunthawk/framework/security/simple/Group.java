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
import com.hunthawk.framework.security.User;

/**
 * @author sunquanzhi
 *
 */
@Entity
@Table(name = "pams_group")
@SuppressWarnings("serial")
public class Group implements Persistent , Serializable{
	private Integer groupId;
	private String name;
	private Set<User> users = new HashSet<User>();
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "groupID")
	public Integer getGroupId()
	{
		return this.groupId;
	}
	public void setGroupId(Integer groupId)
	{
		this.groupId = groupId;
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
	
	@ManyToMany(mappedBy="groups",targetEntity=SimpleUser.class,fetch=FetchType.EAGER)
	public Set<User> getUsers()
	{
		return this.users;
	}
	public void setUsers(Set<User> users)
	{
		this.users = users;
	}
}
