package com.hunthawk.reader.domain.resource;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * 复审实体类
 * 
 * @author penglei
 * 
 */
@Entity
@Table(name = "reader_resource_recheck")
public class ReCheck extends PersistentObject {

	private static final long serialVersionUID = 3023194135800354604L;

	/**
	 * 主键ID
	 */
	private Integer id;

	/**
	 * 复审意见
	 */
	private String comment;

	/**
	 * 资源ID
	 */
	private String resourceId;

	/**
	 * 创建者ID
	 */
	private Integer creatorId;

	/**
	 * 创建时间
	 */
	private Date createTime;

	public ReCheck() {
	}

	public ReCheck(Integer id, String comment, String resourceId,
			Integer creatorId, Date createTime) {
		this.id = id;
		this.comment = comment;
		this.resourceId = resourceId;
		this.creatorId = creatorId;
		this.createTime = createTime;
	}

	@Id
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "RE_comment")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name = "resource_id")
	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	@Column(name = "Creator_Id")
	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}

	@Column(name = "Create_Time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
