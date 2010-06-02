/**
 * 
 */
package com.hunthawk.reader.page.inter;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.hunthawk.reader.domain.inter.MsgRecord;
import com.hunthawk.reader.page.util.SelectKeyValuePO;

/**
 * @author BruceSun
 *
 */
public class SearchMsgRecord implements  Serializable {

	private static final long serialVersionUID = 1L;

	private MsgRecord msgRecord;

	private String userMobile;

	private String customId;

	private String resourcName;

	private String keyStr;

	private Date startTime;

	private Date endTime;

	private SelectKeyValuePO msgStatus;

	private SelectKeyValuePO msgReason;

	public SearchMsgRecord() {
		super();
		setMsgRecord(new MsgRecord());
	}

	public MsgRecord getMsgRecord() {
		return msgRecord;
	}

	public void setMsgRecord(MsgRecord msgRecord) {
		this.msgRecord = msgRecord;
	}

	public String getKeyStr() {
		return keyStr;
	}

	public void setKeyStr(String keyStr) {
		this.keyStr = keyStr;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public SelectKeyValuePO getMsgStatus() {
		return msgStatus;
	}

	public void setMsgStatus(SelectKeyValuePO msgStatus) {
		this.msgStatus = msgStatus;
	}

	public SelectKeyValuePO getMsgReason() {
		return msgReason;
	}

	public void setMsgReason(SelectKeyValuePO msgReason) {
		this.msgReason = msgReason;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public String getResourcName() {
		return resourcName;
	}

	public void setResourcName(String resourcName) {
		this.resourcName = resourcName;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
