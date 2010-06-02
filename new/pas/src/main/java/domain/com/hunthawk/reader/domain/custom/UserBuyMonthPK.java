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
public class UserBuyMonthPK implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7080222523497811307L;
	
	private String mobile;
	
	private String feeId;

	@Column(name = "mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	@Column(name = "feeid")
	public String getFeeId() {
		return feeId;
	}

	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}
	
	

}
