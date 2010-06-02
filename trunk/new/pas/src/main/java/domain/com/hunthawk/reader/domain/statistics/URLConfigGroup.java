/**
 * 
 */
package com.hunthawk.reader.domain.statistics;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * @author BruceSun
 * 
 */
@Entity
@Table(name = "STATISTICS_URL_CONFIG_GROUP")
public class URLConfigGroup extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6031741141528723274L;

	private Integer id;

	private String title;

	private Set<URLConfig> configs = new HashSet<URLConfig>();

	private Integer creator;

	private Date createTime;

	private Integer modifier;

	private Date modifyTime;

	private String users;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@ManyToMany(cascade={CascadeType.PERSIST})
	@JoinTable(name="STATISTICS_URL_CONFIG_GROUP_REL",
		joinColumns={@JoinColumn(name="GROUPID")},
		inverseJoinColumns={@JoinColumn(name="CONFIGID")})
	@LazyCollection(value = LazyCollectionOption.FALSE)
	@Fetch(value=FetchMode.SELECT)
	public Set<URLConfig> getConfigs() {
		return configs;
	}

	public void setConfigs(Set<URLConfig> configs) {
		this.configs = configs;
	}
	@Column(name = "create_id")
	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}
	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "modify_id")
	public Integer getModifier() {
		return modifier;
	}

	public void setModifier(Integer modifier) {
		this.modifier = modifier;
	}
	@Column(name = "modify_time")
	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	@Column(name = "users")
	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}
	
	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
	}

}
