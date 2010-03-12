/**
 * 
 */
package com.hunthawk.reader.domain.device;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author BruceSun
 *
 */
@Embeddable
public class UAGroupRelationPk implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8777041819368253406L;

	private Integer groupId;
	
	private String ua;

	@Column(name = "group_id")
	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	@Column(name = "ua")
	public String getUa() {
		return ua;
	}

	public void setUa(String ua) {
		this.ua = ua;
	}
	
	
}
