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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;
import com.hunthawk.reader.enhance.util.ToolDateUtil;

/**
 * @author BruceSun
 *
 */
@Entity
@Table(name = "READER_BUSSINESS_TEM_CATALOG")
public class TemplateCatalog extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7246467494940864199L;

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
	@Transient
	public String getShowModifyTimeName() {
		if(createTime != null){
			return ToolDateUtil.dateToString(createTime);
		}else{
			return "";
		}
		
	}
}
