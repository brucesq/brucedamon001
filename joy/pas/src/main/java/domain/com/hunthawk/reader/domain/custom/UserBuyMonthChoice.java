/**
 * 
 */
package com.hunthawk.reader.domain.custom;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * 用户包月行为表
 * @author BruceSun
 *
 */
@Entity
@Table(name="reader_order_user_month_choice")
@IdClass(UserBookPk.class)
public class UserBuyMonthChoice extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6802598596267545480L;

	//手机号码
	private String mobile;
	
	//内容ID
	private String contentId;
	
	private String feeId;
	
	private Date createTime;

	@Id
	@Column(name = "mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Id
	@Column(name = "contentid")
	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	@Column(name = "feeid")
	public String getFeeId() {
		return feeId;
	}

	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}
	@Column(name = "In_date")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
}
