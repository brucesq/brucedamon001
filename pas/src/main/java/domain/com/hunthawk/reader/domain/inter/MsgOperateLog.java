/**
 * 
 */
package com.hunthawk.reader.domain.inter;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * @author BruceSun
 *
 */
@Entity
//@javax.persistence.SequenceGenerator(
//		name = "msglog",
//		sequenceName="reader_inter_msglog_seq"
//)
@Table(name = "reader_inter_msglog")
public class MsgOperateLog extends PersistentObject{
	
	/** 执行操作的定义 */

	// 发布
	public static final int ADUIT_OPERATE_PUB = 1;

	// 撤销
	public static final int ADUIT_OPERATE_PUB_CANCEL = 2;

	// 屏蔽
	public static final int ADUIT_OPERATE_HIDE = 3;

	// 恢复
	public static final int ADUIT_OPERATE_HIDE_CANCEL = 4;

	// 删除
	public static final int ADUIT_OPERATE_DELETE = 5;

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9059609516763982656L;

	private int id;

	private int userId;

	private String userName;

	private int itemId;

	private int projectId;

	private int operateOldStatus;;
	
	private int operateNewStatus;
	
	private int operateAction;

	private Date createTime;

	@Id
//	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="msglog")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	@Column(name = "user_id")
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	@Column(name = "user_name")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	@Column(name = "item_id")
	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	@Column(name = "board_id")
	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	@Column(name = "operate_old_status")
	public int getOperateOldStatus() {
		return operateOldStatus;
	}

	public void setOperateOldStatus(int operateOldStatus) {
		this.operateOldStatus = operateOldStatus;
	}
	@Column(name = "operate_new_status")
	public int getOperateNewStatus() {
		return operateNewStatus;
	}

	public void setOperateNewStatus(int operateNewStatus) {
		this.operateNewStatus = operateNewStatus;
	}
	@Column(name = "operate_action")
	public int getOperateAction() {
		return operateAction;
	}

	public void setOperateAction(int operateAction) {
		this.operateAction = operateAction;
	}
	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	

}
