/**
 * 
 */
package com.hunthawk.reader.domain.inter;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * @author BruceSun
 * 
 */
public class MsgKeyWord extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1437464779804173866L;

	/**
	 * 对象状态
	 */
	public static final String[] KEY_TYPE = { "默认", "飘红", "自动替换", "自动回复" };

	// Fields

	private int id;
	private int projectId;
	private int keyType;
	private String keyName;
	private String keyValue;
	private String userName;
	private Date createTime;

	// Constructors

	
	public MsgKeyWord() {
	}

	

	

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProjectId() {
		return this.projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getKeyType() {
		return this.keyType;
	}

	public void setKeyType(int keyType) {
		this.keyType = keyType;
	}

	public String getKeyName() {
		return this.keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getKeyValue() {
		return this.keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

}
