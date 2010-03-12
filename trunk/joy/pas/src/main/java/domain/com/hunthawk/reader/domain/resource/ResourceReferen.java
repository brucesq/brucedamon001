package com.hunthawk.reader.domain.resource;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import javax.persistence.Transient;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.enhance.util.ToolDateUtil;

/**
 * @author xianlaichen
 *
 */
@Entity
//@javax.persistence.SequenceGenerator(
//		name = "copyrightid",
//		sequenceName="reader_resource_referen_seq"
//)
@Table(name = "reader_resource_referen")

public class ResourceReferen extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7595736986123271500L;

	private Integer id;

	/**
	* 版权方名称
	*/
	private String name;

	/**
	* 版权联系人姓名
	*/	
	private String contactName;
	
	private String contactPhone;
	
	/**
	* 版权生效时间
	*/		
	private Date beginTime;
	
	/**
	* 版权失效时间
	*/			
	private Date endTime;
	
	private String scope;
	
	private String showUrl;
	
	private String pZone;
	/**
	 * 版权方联系人邮箱
	 */
	private String email;
	/**
	 * 版权方联系地址
	 */
	private String address;
	/**
	 * 版权方联系传真
	 */
	private String fax;
	
	/**
	* 版权状态
	*/			
	//private Integer copyrightStatus;
	
	/**
	* 状态
	*/			
	private Integer status;
	/**
	 * 被授权的图书作者名称
	*/
	private String authorName;
	/**
	 * 版权登记证书
	*/
	private String copyrightCheck;
	/**
	 * 作品版权状况说明书
	*/
	private String productInfo;
	/**
	 * MCP版权自查情况说明书
	*/
	private String mcpinfo;
	/**
	 * MCP版权无问题承诺书
	*/
	private String promises;
	/**
	 * 其它
	*/
	private String copyrightOther;
	/**
	 * 著作权许可使用协议
	*/
	private String copyrightUse;
	/**
	 * 著作权转让协议
	*/
	private String copyrightAttorn;
	/**
	 * 授权书（含目录）
	*/
	private String providerInfo;
	/**
	 * 合作协议
	*/
	private String cooperatePro;
	
	private Date createTime;
	
	private Integer creatorId;
	/**
	 * CP表中的ID
	 */
	private Integer cpId;
	
	/**
	 * 版权标识，同一个CPID版权标识唯一
	 */
	private String identifier;
	
	/**
	 * 修改时间
	 */
	private Date modifyTime;
	/**
	 * 修改人
	 */
	private Integer modifierId;
	
	@Id
//	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="copyrightid")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "copyright_id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "owner",nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "contact_name")
	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	@Column(name = "contact_phone")
	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	@Column(name = "begin_time")
	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	@Column(name = "end_time")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	@Column(name = "scope")
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
	@Column(name = "show_url")
	public String getShowUrl() {
		return showUrl;
	}

	public void setShowUrl(String showUrl) {
		this.showUrl = showUrl;
	}
	@Column(name = "p_zone")
	public String getPZone() {
		return pZone;
	}

	public void setPZone(String pZone) {
		this.pZone = pZone;
	}
	/*
	@Column(name = "copyright_status")
	public Integer getCopyrightStatus() {
		return copyrightStatus;
	}

	public void setCopyrightStatus(Integer copyrightStatus) {
		this.copyrightStatus = copyrightStatus;
	}*/
	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name = "author_name")
	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	@Column(name = "copyright_check")
	public String getCopyrightCheck() {
		return copyrightCheck;
	}

	public void setCopyrightCheck(String copyrightCheck) {
		this.copyrightCheck = copyrightCheck;
	}
	@Column(name = "copyright_use")
	public String getCopyrightUse() {
		return copyrightUse;
	}

	public void setCopyrightUse(String copyrightUse) {
		this.copyrightUse = copyrightUse;
	}
	@Column(name = "copyright_attorn")
	public String getCopyrightAttorn() {
		return copyrightAttorn;
	}

	public void setCopyrightAttorn(String copyrightAttorn) {
		this.copyrightAttorn = copyrightAttorn;
	}
	@Column(name = "provider_info")
	public String getProviderInfo() {
		return providerInfo;
	}

	public void setProviderInfo(String providerInfo) {
		this.providerInfo = providerInfo;
	}
	@Column(name = "cooperate_pro")
	public String getCooperatePro() {
		return cooperatePro;
	}

	public void setCooperatePro(String cooperatePro) {
		this.cooperatePro = cooperatePro;
	}
	@Column(name = "in_date")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "creator_id")
	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}
	@Column(name = "productinfo")
	public String getProductInfo() {
		return productInfo;
	}
	public void setProductInfo(String productInfo) {
		this.productInfo = productInfo;
	}
	@Column(name = "mcpinfo")
	public String getMcpinfo() {
		return mcpinfo;
	}
	public void setMcpinfo(String mcpinfo) {
		this.mcpinfo = mcpinfo;
	}
	@Column(name = "promises")
	public String getPromises() {
		return promises;
	}
	public void setPromises(String promises) {
		this.promises = promises;
	}
	@Column(name = "copyright_other")
	public String getCopyrightOther() {
		return copyrightOther;
	}
	public void setCopyrightOther(String copyrightOther) {
		this.copyrightOther = copyrightOther;
	}
	
	
	@Column(name = "cp_id")
	public Integer getCpId() {
		return cpId;
	}
	public void setCpId(Integer cpId) {
		this.cpId = cpId;
	}
	
	
	
	@Column(name = "IDENTIFIER")
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	@Column(name = "MODIFY_TIME")
	public Date getModifyTime() {
		return modifyTime;
	}
	
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	@Column(name = "MODIFY_ID")
	public Integer getModifierId() {
		return modifierId;
	}
	public void setModifierId(Integer modifierId) {
		this.modifierId = modifierId;
	}
	public void setReferenStatus(String referenStatus) {
		this.referenStatus = referenStatus;
	}
	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
	}
	
	@SuppressWarnings("unused")
	private String referenStatus;
	//版权状态
	@Transient
	public String getReferenStatus(){
		String typeName = "未知状态";
		if(status != null){
			for(Map.Entry<String,Integer> entry :Constants.getReferenStatus().entrySet()){
				if(entry.getValue().equals(status))
					return entry.getKey();
			}
		}
		return typeName;
	}
	
	@Column(name = "email")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Column(name = "ADDRESS")
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	@Column(name = "FAX")
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	@Transient
	public String getShowModifyTimeName() {
		if(modifyTime != null){
			return ToolDateUtil.dateToString(modifyTime);
		}else{
			return "";
		}
		
	}
	
}
