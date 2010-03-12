/**
 * 
 */
package com.hunthawk.reader.domain.bussiness;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * @author BruceSun
 *
 */
@Entity
@Table(name = "READER_BUSSINESS_TEM_TYPE")
public class TemplateType  extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7595736986123271500L;
	
	public static final int FIRST_PAGE = 1;
	public static final int COLUMN_PAGE = 2;
	public static final int RESOURCE_PAGE = 3;
	public static final int DETAIL_PAGE = 4;
	public static final int FEE_PAGE = 5;
	public static final int COMMON_PAGE = 6;
	

	private Integer id;
	
	private String name;
	
	private Date createTime;
	
	private Integer creatorId;

	@Id
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "T_ATTACH_ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "TYPE_NAME",nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "IN_DATE",nullable = false)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "CREATOR_ID",nullable = false)
	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}
	
	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
	}
	
}
