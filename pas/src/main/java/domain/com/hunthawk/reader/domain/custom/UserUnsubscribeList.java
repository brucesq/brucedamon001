package com.hunthawk.reader.domain.custom;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hunthawk.framework.domain.PersistentObject;
/**
 * iphone 退订流水表
 * @author liuxh
 *
 */
@Entity
//@javax.persistence.SequenceGenerator(
//		name = "unsubscribelist",
//		sequenceName="READER_IPHONE_UNSUB_LIST_SEQ"
//)
@Table(name="READER_IPHONE_UNSUB_LIST")
public class UserUnsubscribeList extends PersistentObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2278120048275951670L;
	
	private Integer id;
	
	private String mobile;
	
	
	private String feeId;
	//我们平台上的产品id
	private String pid;
	//推广渠道id
	private String channelId;

	//批价包ID
	private Integer packId;
	//SPID/CPID
	private String spid;
	
	private String serviceId;
	
	private String productID;
	
	private Date createTime;
	//栏目ID
	private Integer columnId;

	//退订类型 （1.产品 2.栏目 3.VIP）
	private Integer unsubType;

	private Integer status;//状态  (0.已退订  1.未退订)
	
	@Id
//	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="unsubscribelist")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "MOBILE")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@Column(name = "FEEID")
	public String getFeeId() {
		return feeId;
	}

	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}
	@Column(name = "PID")
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}
	@Column(name = "CHANNEL_ID")
	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	@Column(name = "IN_DATE")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name = "PACK_ID")
	public Integer getPackId() {
		return packId;
	}

	public void setPackId(Integer packId) {
		this.packId = packId;
	}
	@Column(name = "SPID")
	public String getSpid() {
		return spid;
	}

	public void setSpid(String spid) {
		this.spid = spid;
	}
	@Column(name = "SERVICE_ID")
	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	@Column(name = "PRODUCT_ID")
	public String getProductID() {
		return productID;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}
	@Column(name = "COLUMN_ID")
	public Integer getColumnId() {
		return columnId;
	}

	public void setColumnId(Integer columnId) {
		this.columnId = columnId;
	}
	
	@Column(name = "UNSUB_TYPE")
	public Integer getUnsubType() {
		return unsubType;
	}

	public void setUnsubType(Integer unsubType) {
		this.unsubType = unsubType;
	}

	@Column(name = "STATUS")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	
}
