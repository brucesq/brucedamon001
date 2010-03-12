/**
 * 
 */
package com.hunthawk.reader.domain.inter;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author BruceSun
 *
 */
@Embeddable
public class ReservationPK implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6631710314335568481L;

	private String mobile;
	
	private String contentId;

	@Column(name = "mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name = "content_id")
	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	
	
}
