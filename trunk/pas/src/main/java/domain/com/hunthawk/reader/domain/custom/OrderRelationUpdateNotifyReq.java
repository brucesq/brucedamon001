/**
 * 
 */
package com.hunthawk.reader.domain.custom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * 订购关系同步接口
 * @author BruceSun
 *
 */
@Entity
//@javax.persistence.SequenceGenerator(
//		name = "orderlist",
//		sequenceName="reader_order_user_req_seq"
//)
@Table(name="reader_order_user_req")
public class OrderRelationUpdateNotifyReq extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1750323604440054140L;

	private Integer id;
	
	private String recordSequenceID;
	
	private Integer userIdType;
	
	private String userId;
	
	private String serviceType;
	
	private String spId;
	
	private String productId;
	
	private Integer updateType;
	
	private String updateTime;
	
	private String updateDesc;
	
	private String linkID;
	
	private String content;
	
	private String effectiveDate;
	
	private String expireDate;
	
	private String timeStamp;
	
	private String encodeStr;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "RecordSequenceID")
	public String getRecordSequenceID() {
		return recordSequenceID;
	}

	public void setRecordSequenceID(String recordSequenceID) {
		this.recordSequenceID = recordSequenceID;
	}
	@Column(name = "UserIdType")
	public Integer getUserIdType() {
		return userIdType;
	}

	public void setUserIdType(Integer userIdType) {
		this.userIdType = userIdType;
	}
	@Column(name = "UserId")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Column(name = "ServiceType")
	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	@Column(name = "SpId")
	public String getSpId() {
		return spId;
	}

	public void setSpId(String spId) {
		this.spId = spId;
	}
	@Column(name = "ProductId")
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
	@Column(name = "UpdateType")
	public Integer getUpdateType() {
		return updateType;
	}

	public void setUpdateType(Integer updateType) {
		this.updateType = updateType;
	}
	@Column(name = "UpdateTime")
	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	@Column(name = "UpdateDesc")
	public String getUpdateDesc() {
		return updateDesc;
	}

	public void setUpdateDesc(String updateDesc) {
		this.updateDesc = updateDesc;
	}
	@Column(name = "LinkID")
	public String getLinkID() {
		return linkID;
	}

	public void setLinkID(String linkID) {
		this.linkID = linkID;
	}
	@Column(name = "Content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	@Column(name = "EffectiveDate")
	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	@Column(name = "ExpireDate")
	public String getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}
	@Column(name = "Time_Stamp")
	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	@Column(name = "EncodeStr")
	public String getEncodeStr() {
		return encodeStr;
	}

	public void setEncodeStr(String encodeStr) {
		this.encodeStr = encodeStr;
	}
	
	
	
}
