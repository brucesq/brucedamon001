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
 * ���
 * @author BruceSun
 * 
 */
@Entity
@Table(name="reader_order_user_satchel")
@IdClass(UserBookPk.class)
public class BookBag extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2454269360662738361L;

	//�ֻ�����
	private String mobile;
	
	//����ID
	private String contentId;
	
	
	
	//�Ʒ�ID
	private String feeId;
	
	//��ƷID
	private String pid; 
	
	//����ID
	private String channelId;
	
	
	
	//����ʱ��
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
	@Column(name = "pid")
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}
	@Column(name = "channel_id")
	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	
	@Column(name = "in_date")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
}
