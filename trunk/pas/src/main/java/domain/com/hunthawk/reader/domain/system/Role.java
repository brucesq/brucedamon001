/**
 * 
 */
package com.hunthawk.reader.domain.system;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * @author sunquanzhi
 *
 */
@Entity
@Table(name = "reader_system_role")
public class Role extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9027777288797488246L;
	private Integer id;
	private String name;
	private String cnName;
	
	private Set<Privilege> privileges = new HashSet<Privilege>();

	
	@Id
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "id")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
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
	@Column(name = "cnname",  nullable = false)
	public String getCnName()
	{
		return cnName;
	}
	public void setCnName(String name)
	{
		cnName = name;
	}
	@ManyToMany(cascade={CascadeType.PERSIST})
	@JoinTable(name="reader_system_roles_privileges",
		joinColumns={@JoinColumn(name="roleId")},
		inverseJoinColumns={@JoinColumn(name="privilegeId")})
	@LazyCollection(value = LazyCollectionOption.FALSE)
	@Fetch(value=FetchMode.SELECT)
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

	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
	}

	
}
