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
 * 用户包月表
 * @author BruceSun
 *
 */
@Entity
@Table(name="reader_order_user_month")
@IdClass(UserBuyMonthPK.class)
public class UserBuyMonth  extends PersistentObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8099682858507727554L;

	private String mobile;
	
	private String feeId;
	
	private Integer feeType;
	
	private String channelId;
	
	private String pid;
	
	private Date createTime;
	
	private Integer packId;

	@Id
	@Column(name = "mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Id
	@Column(name = "feeid")
	public String getFeeId() {
		return feeId;
	}

	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}
	@Column(name = "fee_type")
	public Integer getFeeType() {
		return feeType;
	}

	public void setFeeType(Integer feeType) {
		this.feeType = feeType;
	}

	@Column(name = "channel_id")
	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	@Column(name = "pid")
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	@Column(name = "in_date")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "pack_id")
	public Integer getPackId() {
		return packId;
	}

	public void setPackId(Integer packId) {
		this.packId = packId;
	}
	
	

}
