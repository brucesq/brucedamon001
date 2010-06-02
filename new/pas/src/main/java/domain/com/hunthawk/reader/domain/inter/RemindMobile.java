/**
 * 
 */
package com.hunthawk.reader.domain.inter;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * @author BruceSun
 *
 */
@Entity
@Table(name = "reader_inter_remind_mobile")
@IdClass(RemindMobilePK.class)
public class RemindMobile extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8421228100461014084L;
	private Integer remindId;
	private String mobile;
	
	private Date sendTime;
	//状态 0未发送 1正在发送 2发送完成
	private Integer status;
	
	@Id
	@Column(name = "remind_id")
	public Integer getRemindId() {
		return remindId;
	}
	public void setRemindId(Integer remindId) {
		this.remindId = remindId;
	}
	
	@Id
	@Column(name = "mobile")
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	
	@Column(name = "send_time")
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
