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
@Table(name="user_friend")
public class UserFriend extends PersistentObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6954645201568682862L;
	// 流水号
	private Integer id;
	// 用户ID
	private String userId;
	// 好友ID
	private String friendId;
	// 最后一次更新时间
	private Date lastDate;
	// 标识位0未建立、1成为好友
	private Integer flag;

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
	@Column(name = "friend_id")
	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}
	@Column(name = "last_date")
	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}
	@Column(name = "friend_flag")
	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

}
