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

import com.hunthawk.framework.Persistent;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;
import com.hunthawk.framework.security.User;

/**
 * @author sunquanzhi
 *
 */
@Entity
@Table(name = "pams_role")
@SuppressWarnings("serial")
public class Role implements Persistent , Serializable{

	private Integer roleId;
	private String name;
	private String cnName;
	
	private Set<Privilege> privileges = new HashSet<Privilege>();
	private Set<User> users = new HashSet<User>();
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "roleID")
	public Integer getRoleId() {
		return this.roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
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
	@Column(name = "cnname",  nullable = false)
	public String getCnName()
	{
		return cnName;
	}
	public void setCnName(String name)
	{
		cnName = name;
	}
	@ManyToMany(cascade={CascadeType.PERSIST},fetch=FetchType.EAGER)
	@JoinTable(name="roles_privileges",
		joinColumns={@JoinColumn(name="roleId")},
		inverseJoinColumns={@JoinColumn(name="privilegeId")})
	public Set<Privilege> getPrivileges()
	{
		return this.privileges;
	}
	
	public void setPrivileges(Set<Privilege> privileges)
	{
		this.privileges = privileges;
	}
	public boolean hasPrivilege(String privilege)
	{
		for(Privilege pri : privileges)
		{
			if(pri.getName().equals(privilege))
				return true;
		}
		return false;
	}
	public void addPrivilege(Privilege pri)
	{
		privileges.add(pri);
	}
	@ManyToMany(mappedBy="roles",targetEntity=SimpleUser.class,fetch=FetchType.EAGER)
	public Set<User> getUsers()
	{
		return this.users;
	}
	public void setUsers(Set<User> users)
	{
		this.users = users;
	}
	public void removeUser(User user)
	{
		users.remove(user);
	}
	public boolean equals(Object o) {
		
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"roleId");
	}

	
}
