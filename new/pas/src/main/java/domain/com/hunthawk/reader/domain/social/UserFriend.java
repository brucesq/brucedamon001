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
	// ��ˮ��
	private Integer id;
	// �û�ID
	private String userId;
	// ����ID
	private String friendId;
	// ���һ�θ���ʱ��
	private Date lastDate;
	// ��ʶλ0δ������1��Ϊ����
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
