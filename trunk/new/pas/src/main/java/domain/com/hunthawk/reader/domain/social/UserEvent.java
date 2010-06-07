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
import com.hunthawk.reader.enhance.util.ToolDateUtil;

/**
 * 用户事件
 * @author sunquanzhi
 *
 */
@Entity
@Table(name="user_event")
public class UserEvent  extends PersistentObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 454996358044600279L;
	// 流水号
	private Integer id;
	// 用户ID
	private String userId;
	//更新时间
	private Date eventTime;
	//事件消息
	private String eventMessage;
	//事件类型
	private Integer eventType;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "user_id")
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Column(name = "event_time")
	public Date getEventTime() {
		return eventTime;
	}
	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}
	@Column(name = "event_message")
	public String getEventMessage() {
		return eventMessage;
	}
	public void setEventMessage(String eventMessage) {
		this.eventMessage = eventMessage;
	}
	@Column(name = "event_type")
	public Integer getEventType() {
		return eventType;
	}
	public void setEventType(Integer eventType) {
		this.eventType = eventType;
	}
	
	public String toString(){
		return id+"@@@"+userId+"@@@"+ToolDateUtil.dateToString(eventTime)+"@@@"+eventMessage+"@@@"+eventType;
	}
	
}
