/**
 * 
 */
package com.hunthawk.reader.domain.device;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * @author BruceSun
 *
 */
@Entity
@Table(name="reader_useragent_ua_g_u")
@IdClass(UAGroupRelationPk.class)
public class UAGroupRelation implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5352456668303996917L;

	private Integer groupId;
	
	private String ua;

	@Id
	@Column(name = "group_id")
	public Integer getGroupId() {
		return groupId;
	}

	
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	@Id
	@Column(name = "ua")
	public String getUa() {
		return ua;
	}

	public void setUa(String ua) {
		this.ua = ua;
	}
	
	

}
