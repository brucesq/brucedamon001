/**
 * 
 */
package com.hunthawk.reader.domain.partner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * @author BruceSun
 *
 */
@Entity
@Table(name = "reader_fee_fee")
public class Fee extends PersistentObject{

	private static Map<String,Integer> FEETYPES = new HashMap<String,Integer>();
	private static Map<String,Integer> DISCOUNTTYPES = new HashMap<String,Integer>();
	private static Map<String,Integer> YESNO=new HashMap<String,Integer>();
	static{
		FEETYPES.put("免费", 1);
		FEETYPES.put("包月", 2);
		FEETYPES.put("计次", 3);
		
		DISCOUNTTYPES.put("全价", 0);
		DISCOUNTTYPES.put("折扣", 1);
		DISCOUNTTYPES.put("免费", 2);
		
		YESNO.put("否",0);
		YESNO.put("是",1);
	}
	
	public static Map<String,Integer>  getFeeTypes(){
		return FEETYPES;
	}
	
	//是否动态匹配
	public static Map<String,Integer> getYesNo(){
		return YESNO;
	}
	public static  Map<String,Integer> getDiscountTypes(){
		return DISCOUNTTYPES;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1906617865136095620L;

	private String id;
	//名称
	private String name;
	private String serviceId;
	private String productId;
	//价格
	private String code;
	//描述
	private String comment;
	//计费类型(1.免费，2.包月，3.计次)
	private Integer type;
	//计费代码状态(1 商用，0暂停，默认为暂停状态 )
	private Integer status;
	//是否弹出资费页，并必须选择资费模版
	private Integer isout;
	//是否弹出资费页，若是则必须选择资费模版
	private Integer templateId;
	//资费wap2.0模板
	private Integer templateId_wap;
	//资费3g模板
	private Integer templateId_3g;
	
	//计费地址入口地址
	private String portalUrl;
	//计费确认url
	private String actionUrl;
	//计费url
	private String url;
	//0删除 1正常
	private Integer flag;
	//是否为折扣？0-全价；1-折扣   2-免费
	private Integer discount;
	
	private Provider provider;
	
	private Date createTime;
	private Integer createorId;
	
	private Date modifyTime;
	private Integer motifierId;

	@Id
	@GeneratedValue(generator = "system-assigned")
	@GenericGenerator(name = "system-assigned", strategy = "assigned")
	@Column(name = "FEEID")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	@Column(name = "FEE_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "SERVICEID")
	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	@Column(name = "PRODUCTID")
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
	@Column(name = "FEE_CODE")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	@Column(name = "FEE_COMMENT")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	@Column(name = "FEE_TYPE")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	@Column(name = "FEE_STATUS")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name = "IS_OUT")
	public Integer getIsout() {
		return isout;
	}

	public void setIsout(Integer isout) {
		this.isout = isout;
	}
	@Column(name = "TEMPLATE_ID")
	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}
	@Column(name = "FEE_PORTAL_URL")
	public String getPortalUrl() {
		return portalUrl;
	}

	public void setPortalUrl(String portalUrl) {
		this.portalUrl = portalUrl;
	}
	@Column(name = "FEE_ACTION_URL")
	public String getActionUrl() {
		return actionUrl;
	}

	public void setActionUrl(String actionUrl) {
		this.actionUrl = actionUrl;
	}
	@Column(name = "FEE_URL")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	@ManyToOne
	@JoinColumn(name="provider_id",nullable=false)
	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	@Column(name = "FEE_FLAG")
	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	
	@Column(name = "fee_creator")
	public Integer getCreateorId() {
		return createorId;
	}
	public void setCreateorId(Integer createorId) {
		this.createorId = createorId;
	}
	@Column(name = "fee_createtime")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name = "fee_modifytime")
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	@Column(name = "fee_modifier")
	public Integer getMotifierId() {
		return motifierId;
	}
	public void setMotifierId(Integer motifierId) {
		this.motifierId = motifierId;
	}
	@Transient
	public String getFeeTypeName(){
		
		for(Map.Entry<String, Integer> entry: FEETYPES.entrySet()){
			if(entry.getValue().equals(type)){
				return entry.getKey();
			}
		}
		return "";
	}
	@Transient
	public Integer getProviderId(){
		return getProvider().getId();
	}
	@Transient
	public String getStatusName(){
		switch(status){
		case 1:
			return "商用";
		case 0:
			return "暂停";
		}
		return "";
	}
	@Column(name = "IS_DISCOUNT")
	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}
	
	@Transient
	public String getDiscountName(){
		
		for(Map.Entry<String, Integer> entry: DISCOUNTTYPES.entrySet()){
			if(entry.getValue().equals(discount)){
				return entry.getKey();
			}
		}
		return "";
	}
	
	@Override
	public boolean equals(Object o) {
		return HibernateEqualsBuilder.reflectionEquals(this, o, "id");
	}

	@Column(name="TEMPLATE_WAP")
	public Integer getTemplateId_wap() {
		return templateId_wap;
	}

	public void setTemplateId_wap(Integer templateId_wap) {
		this.templateId_wap = templateId_wap;
	}
	@Column(name="TEMPLATE_3G")
	public Integer getTemplateId_3g() {
		return templateId_3g;
	}

	public void setTemplateId_3g(Integer templateId_3g) {
		this.templateId_3g = templateId_3g;
	}

}
