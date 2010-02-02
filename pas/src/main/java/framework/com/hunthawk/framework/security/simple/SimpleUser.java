/**
 * 
 */
package com.hunthawk.framework.security.simple;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.hunthawk.framework.Persistent;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;
import com.hunthawk.framework.security.User;


/**
 * @author sunquanzhi
 *
 */
@Entity
@javax.persistence.SequenceGenerator(
		name = "user",
		sequenceName="pams_user_sequence"
)
@Table(name = "pams_user")
@SuppressWarnings("serial")
public class SimpleUser implements User , Persistent , Serializable{

	private Integer userId;
	private String pwd;
	private String name ;
	
	private Set<Role> roles = new HashSet<Role>();
	private Set<Group> groups = new HashSet<Group>();
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="user")
	@Column(name = "userID")
	public Integer getId() {
		return this.userId;
	}

	public void setId(Integer userId) {
		this.userId = userId;
	}
	@Column(name = "name", unique = true, nullable = false)
	public String getName() {
		
		return name;
	}

	@Column(name = "password", nullable = false)
	public String getPassword() {
		
		return pwd;
	}

	
	public boolean hasRole(String name) {
		for(Role role : roles)
		{
			if(role.hasPrivilege(name))
				return true;
		}
		return false;
	}

	public boolean isAdmin()
	{
		for(Role role : roles)
		{
			if(role.getName().equals(ADMIN))
				return true;
		}
		return false;
	}
	
	public void setName(String name) {

		this.name = name;

	}

	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.security.User#setPassword(java.lang.String)
	 */
	public void setPassword(String pwd) {
		this.pwd = pwd;

	}
	
	@ManyToMany(cascade={CascadeType.PERSIST})
	@JoinTable(name="users_roles",
		joinColumns={@JoinColumn(name="userId")},
		inverseJoinColumns={@JoinColumn(name="roleId")})
	@LazyCollection(value = LazyCollectionOption.FALSE)
	public Set<Role> getRoles()
	{
		return this.roles;
	}
	
	public void setRoles(Set<Role> roles)
	{
		this.roles = roles;
	}

	public void addRole(Role role)
	{
		for(Role ro : roles)
		{
			if(ro.equals(role))
				return;
		}
		
		roles.add(role);
		
	}
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="users_groups",
		joinColumns={@JoinColumn(name="userId")},
		inverseJoinColumns={@JoinColumn(name="groupId")})
	public Set<Group> getGroups()
	{
		return this.groups;
	}
	
	public void setGroups(Set<Group> groups)
	{
		this.groups = groups;
	}
	public boolean equals(Object o) {
        return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
	}

	
}
