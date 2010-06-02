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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;
import com.hunthawk.framework.security.User;
import com.hunthawk.reader.domain.partner.Channel;
import com.hunthawk.reader.domain.partner.Provider;

/**
 * @author sunquanzhi
 * 
 */
@Entity
@Table(name = "reader_system_user")
public class UserImpl extends PersistentObject implements User {

	/**
	 * 
	 */
	private static final long serialVersionUID = -60087475022935966L;
	public static final Integer VALID = 1;
	public static final Integer DISABLE = 0;

	private Integer id;
	private String pwd;
	private String name;
	private Integer flag;

	/**
	 * 用户的真实姓名
	 */
	private String chName;

	/**
	 * 用户的电话
	 */
	private String phoneNum;

	/**
	 * 系统用户的email
	 */
	private String email;

	/**
	 * 合作方
	 */
	private Provider provider;

	/**
	 * 用户类型，1运营人员 2 内容合作方 3 渠道合作方
	 */
	private Integer type;

	private Channel channel;

	private Set<Role> roles = new HashSet<Role>();
	private Set<Group> groups = new HashSet<Group>();

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
	public String getName() {

		return name;
	}

	@Column(name = "password", nullable = false)
	public String getPassword() {

		return pwd;
	}

	/**
	 * <p>
	 * 是否有权限
	 * </p>
	 */
	public boolean hasRole(String name) {
		for (Role role : roles) {
			if (role.hasPrivilege(name))
				return true;
		}
		return false;
	}

	/**
	 * <p>
	 * 是否是管理员
	 * </p>
	 */
	@Transient
	public boolean isAdmin() {
		for (Role role : roles) {
			if (role.getName().equals(ADMIN))
				return true;
		}
		return false;
	}

	public void setName(String name) {

		this.name = name;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.security.User#setPassword(java.lang.String)
	 */
	public void setPassword(String pwd) {
		this.pwd = pwd;

	}

	@ManyToMany(cascade = { CascadeType.PERSIST })
	@JoinTable(name = "reader_system_users_roles", joinColumns = { @JoinColumn(name = "userId") }, inverseJoinColumns = { @JoinColumn(name = "roleId") })
	@LazyCollection(value = LazyCollectionOption.FALSE)
	@Fetch(value = FetchMode.SELECT)
	public Set<Role> getRoles() {
		return this.roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void addRole(Role role) {
		for (Role ro : roles) {
			if (ro.equals(role))
				return;
		}

		roles.add(role);

	}

	@ManyToMany(cascade = { CascadeType.PERSIST })
	@JoinTable(name = "reader_system_users_groups", joinColumns = { @JoinColumn(name = "userId") }, inverseJoinColumns = { @JoinColumn(name = "groupId") })
	@LazyCollection(value = LazyCollectionOption.FALSE)
	@Fetch(value = FetchMode.SUBSELECT)
	public Set<Group> getGroups() {
		return this.groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	public void removeGroup(Group group) {
		groups.remove(group);
	}

	public boolean equals(Object o) {
		boolean bEquals = HibernateEqualsBuilder
				.reflectionEquals(this, o, "id");
		return bEquals;
	}

	@Column(name = "chname")
	public String getChName() {
		return chName;
	}

	public void setChName(String chName) {
		this.chName = chName;
	}

	@Column(name = "phonenum")
	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	@Column(name = "email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	@Column(name = "flag", nullable = false)
	public Integer getFlag() {
		return flag;
	}

	@ManyToOne
	@JoinColumn(name = "provider_id")
	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	/**
	 * <p>
	 * 是否有效
	 * </p>
	 * 
	 * @return
	 */
	@Transient
	public boolean isValid() {
		if (flag.equals(VALID))
			return true;
		return false;
	}
	@Transient
	public boolean isRoleProvider() {
		if(type != null && type == 2)
			return true;
		return false;
	}
	@Transient
	public boolean isRoleChannel(){
		if(type != null && type == 3)
			return true;
		return false;
	}

	@Column(name = "USER_TYPE")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@ManyToOne
	@JoinColumn(name = "CHANNEL_ID")
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

}
