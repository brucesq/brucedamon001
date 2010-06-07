/**
 * 
 */
package com.hunthawk.reader.domain.social;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * @author sunquanzhi
 *
 */
@Entity
@Table(name="user_message")
public class UserMessage extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8200400513196241857L;

	private Integer id;
	
	private String fromId;
	
	private String toId;
	
	private String message;
	//0未读，1已读
	private Integer flag;
	//过期时间
	private Date expiredTime;
	//消息类型 0普通，1加为好友，2系统消息
	private Integer messageType;
	//是否公开
	private Integer isPublish;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "from_id")
	public String getFromId() {
		return fromId;
	}
	
	public void setFromId(String fromId) {
		this.fromId = fromId;
	}
	@Column(name = "to_id")
	public String getToId() {
		return toId;
	}
	public void setToId(String toId) {
		this.toId = toId;
	}
	@Column(name = "message")
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Column(name = "flag")
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	@Column(name = "expired_time")
	public Date getExpiredTime() {
		return expiredTime;
	}
	public void setExpiredTime(Date expiredTime) {
		this.expiredTime = expiredTime;
	}
	@Column(name = "message_type")
	public Integer getMessageType() {
		return messageType;
	}
	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}
	@Column(name = "is_publish")
	public Integer getIsPublish() {
		return isPublish;
	}
	public void setIsPublish(Integer isPublish) {
		this.isPublish = isPublish;
	}
	
	
}
