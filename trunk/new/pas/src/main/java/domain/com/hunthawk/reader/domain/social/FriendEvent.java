/**
 * 
 */
package com.hunthawk.reader.domain.social;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.reader.enhance.util.ToolDateUtil;

/**
 * @author sunquanzhi
 *
 */
@Entity
@Table(name="user_friend_event")
public class FriendEvent {
	// 用户ID
	private String userId;
	//更新时间
	private Date lastTime;
	//事件内容列表
	private String content = "";
	
	@Id
	@GeneratedValue(generator = "system-assigned")
	@GenericGenerator(name = "system-assigned", strategy = "assigned")
	@Column(name = "user_id")
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Column(name = "last_time")
	public Date getLastTime() {
		return lastTime;
	}
	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}
	@Column(name = "event_list")
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Transient
	public List<UserEvent> getUserEvents(){
		List<UserEvent> events = new ArrayList<UserEvent>();
		if(StringUtils.isNotEmpty(content)){
			String[] strs = content.split("###");
			for(String str:strs){
				String[] fileds = str.split("@@@");
				UserEvent event = new UserEvent();
				event.setId(Integer.parseInt(fileds[0]));
				event.setUserId(fileds[1]);
				event.setEventTime(ToolDateUtil.stringToDate(fileds[2]));
				event.setEventMessage(fileds[3]);
				event.setEventType(Integer.parseInt(fileds[4]));
				events.add(event);
			}
		}
		return events;
	}
	
	public void addUserEvent(UserEvent event){
		List<UserEvent> events = getUserEvents();
		while(events.size()>50){
			events.remove(events.size()-1);
		}
		events.add(0, event);
		for(UserEvent ue : events){
			content = ue.toString()+"###";
		}
		if(content.endsWith("###")){
			content = content.substring(0, content.length() - 3);
		}
	}
	
	
	public static void main(String[] args){
		String content = "asdsda###";
		if(content.endsWith("###")){
			content = content.substring(0, content.length() - 3);
		}
		System.out.println(content);
	}
}
