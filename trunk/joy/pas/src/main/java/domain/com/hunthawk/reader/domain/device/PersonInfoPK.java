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
public class PersonInfoPK implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7678964561785792528L;

	private String mobile;
	
	private Integer groupId;

	@Column(name = "mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name = "group_id")
	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	
	
}
