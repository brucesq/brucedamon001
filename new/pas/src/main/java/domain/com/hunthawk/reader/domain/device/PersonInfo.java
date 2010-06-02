/**
 * 
 */
package com.hunthawk.reader.domain.device;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * 白名单用户信息
 * @author BruceSun
 *
 */
@Entity
@Table(name="reader_user_while")
@IdClass(PersonInfoPK.class)
public class PersonInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 733639899387576547L;

	private String mobile;
	
	private String comment;
	
	private Integer groupId;
	
	private Integer creator;
	
	private Date createTime;

	@Id
	@Column(name = "mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	@Column(name = "user_comment")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	@Id
	@Column(name = "group_id")
	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	@Column(name = "creator_id")
	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}
	@Column(name = "in_date")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	
	
}
