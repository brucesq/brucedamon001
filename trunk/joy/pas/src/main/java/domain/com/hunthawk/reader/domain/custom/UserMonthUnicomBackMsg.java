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
 * 此表主要用于记录联通返回sequence_id 用于包月退订
 * @author liuxh
 *
 */
@Entity
@javax.persistence.SequenceGenerator(
		name = "unicomlist",
		sequenceName="READER_ORDER_MONTH_REQ_SEQ"
)
@Table(name="READER_ORDER_MONTH_REQ")
public class UserMonthUnicomBackMsg extends PersistentObject {
	private static final long serialVersionUID = 2278120048275951670L;
	private Integer id;
	private String feeId;
	private String mobile;
	private String recordsn;//自己平台生成的流水ID
	private String sequenceId;//联通平台返回的计费平台受理流水号
	private Integer orderType;//1.产品 2.栏目 3.VIP
	private String pid;
	private Integer packId;
	private Integer columnId;
	private Integer status;
	private Date createTime;
	private String viewUrl;//产品首页URL或栏目首页URL
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="unicomlist")
	@Column(name = "ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "FEEID",nullable = false)
	public String getFeeId() {
		return feeId;
	}

	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}

	@Column(name = "MOBILE",nullable=false)
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name = "RECORDSN",nullable=false)
	public String getRecordsn() {
		return recordsn;
	}

	public void setRecordsn(String recordsn) {
		this.recordsn = recordsn;
	}

	@Column(name = "SEQUENCE_ID",nullable=false)
	public String getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}

	@Column(name = "ORDER_TYPE",nullable=false)
	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	@Column(name = "PID")
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	@Column(name = "PACK_ID")
	public Integer getPackId() {
		return packId;
	}

	public void setPackId(Integer packId) {
		this.packId = packId;
	}

	@Column(name = "COLUMN_ID")
	public Integer getColumnId() {
		return columnId;
	}

	public void setColumnId(Integer columnId) {
		this.columnId = columnId;
	}

	@Column(name = "STATUS",nullable=false)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "IN_DATE")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "VIEW_URL")
	public String getViewUrl() {
		return viewUrl;
	}

	public void setViewUrl(String viewUrl) {
		this.viewUrl = viewUrl;
	}
	
}
