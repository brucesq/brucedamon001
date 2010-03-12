/**
 * 
 */
package com.hunthawk.reader.domain.custom;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author BruceSun
 *
 */
@Embeddable
public class UserBookPk implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4761635717602797949L;

	//ÊÖ»úºÅÂë
	private String mobile;
	
	//ÄÚÈÝID
	private String contentId;

	@Column(name = "mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	@Column(name = "contentid")
	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	
	
}
