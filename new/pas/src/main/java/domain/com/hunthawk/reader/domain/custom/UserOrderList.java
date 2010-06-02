/**
 * 
 */
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
 * @author BruceSun
 *
 */
@Entity
//@javax.persistence.SequenceGenerator(
//		name = "orderlist",
//		sequenceName="reader_order_user_list_seq"
//)
@Table(name="reader_order_user_list")
public class UserOrderList extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2278120048275951670L;
	
	private Integer id;
	
	private String mobile;
	
	private String contentId;
	//�������1.��ѣ�2.���£�3.�ƴ�
	private Integer orderType;
	
	private String feeId;
	//����ƽ̨�ϵĲ�Ʒid
	private String pid;
	//�ƹ�����id
	private String channelId;
	//���۰����еļƷ�����:1�ƴΣ�2��Ŀ���£�VIP����3��Ŀ���£����ݿ��ƣ���4��Ŀ���£����棩��5���
	private Integer feeType;
	//���۰�ID
	private Integer packId;
	//SPID/CPID
	private String spid;
	
	private String serviceId;
	
	private String productID;
	
	private Date createTime;

	@Id
//	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="orderlist")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	@Column(name = "contentid")
	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	@Column(name = "order_type")
	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
	@Column(name = "feeid")
	public String getFeeId() {
		return feeId;
	}

	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}
	@Column(name = "pid")
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}
	@Column(name = "channel_id")
	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	@Column(name = "in_date")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "Fee_type")
	public Integer getFeeType() {
		return feeType;
	}

	public void setFeeType(Integer feeType) {
		this.feeType = feeType;
	}
	@Column(name = "Pack_id")
	public Integer getPackId() {
		return packId;
	}

	public void setPackId(Integer packId) {
		this.packId = packId;
	}
	@Column(name = "spid")
	public String getSpid() {
		return spid;
	}

	public void setSpid(String spid) {
		this.spid = spid;
	}
	@Column(name = "ServiceId")
	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	@Column(name = "productID")
	public String getProductID() {
		return productID;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}
	
	
	

}
