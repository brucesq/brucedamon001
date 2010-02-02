/**
 * 
 */
package com.hunthawk.reader.domain.partner;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@Table(name = "reader_partner_channel")
public class Channel extends PersistentObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7699798883166815771L;
	
	

	//渠道ID
	private String id;
	//渠道类型ID
	private Integer channelTypeId;
	
	
	//所在城市
	private String city;
	//企业中文名称  *
	private String chName;
	//企业简称  *
	private String intro;
	//注册资本
	private String registeredCapita;
	//	公司法人  
	private String corporate;
	//公司地址
	private String address;
	//邮政编码
	private String postcode;
	//	公司电话  
	private String phone;
	//传真
	private String fax;
	//	公司网站URL 
	private String url;
	//信用度 中
	private Integer credit;
	//开户银行  *
	private String bankName;
	//帐户名称  *
	private String bankAccountName;
	//银行帐号  *
	private String bankAccount;
	//	接口人姓名  
	private String contactName;
	//电话 
	private String contactPhone;
	//手机
	private String contactMobile;
	//email   
	private String contactEmail;
	//职位
	private String contactJob;
	
	private Date createTime;
	private Integer createorId;
	private Date modifyTime;
	private Integer motifierId;
	//删除标识位 0删除 1正常
	private Integer flag;
	
	//分成比例
	private String proportion;
	//分成比例生效时间 记录到月份
	private String proportionTime;
	
	@Id
//	@GeneratedValue(strategy=GenerationType.AUTO)
	@GeneratedValue(generator = "system-assigned")
	@GenericGenerator(name = "system-assigned", strategy = "assigned")
	@Column(name = "channel_id")
	public String getId()
	{
		return this.id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	
	
	@Column(name = "channel_chname",nullable=false)
	public String getChName() {
		return chName;
	}
	public void setChName(String name) {
		this.chName = name;
	}
	@Column(name = "channel_type_id",nullable=false)
	public Integer getChannelTypeId() {
		return channelTypeId;
	}
	
	public void setChannelTypeId(Integer channelTypeId) {
		this.channelTypeId = channelTypeId;
	}
	
	@Transient
	public ChannelType getChannelType() {
		if(channelTypeId == null){
			return ChannelType.getChannelType(1);
		}
		return ChannelType.getChannelType(channelTypeId);
	}
	
	public void setChannelType(ChannelType channelType) {
		this.channelTypeId = channelType.getId();
	}
	
	@Column(name = "channel_city")
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	@Column(name = "channel_intro",nullable=false)
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	@Column(name = "channel_registered_capita")
	public String getRegisteredCapita() {
		return registeredCapita;
	}
	public void setRegisteredCapita(String registeredCapita) {
		this.registeredCapita = registeredCapita;
	}
	@Column(name = "channel_corporate")
	public String getCorporate() {
		return corporate;
	}
	
	public void setCorporate(String corporate) {
		this.corporate = corporate;
	}
	@Column(name = "channel_address")
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	@Column(name = "channel_postcode")
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	@Column(name = "channel_phone")
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Column(name = "channel_fax")
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	@Column(name = "channel_url")
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Column(name = "channel_credit")
	public Integer getCredit() {
		return credit;
	}
	public void setCredit(Integer credit) {
		this.credit = credit;
	}
	@Column(name = "channel_bank_name",nullable=false)
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	@Column(name = "channel_bank_account_name",nullable=false)
	public String getBankAccountName() {
		return bankAccountName;
	}
	public void setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
	}
	@Column(name = "channel_bank_account",nullable=false)
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	@Column(name = "channel_contact_name")
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	@Column(name = "channel_contack_phone")
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	@Column(name = "channel_contack_mobile")
	public String getContactMobile() {
		return contactMobile;
	}
	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}
	@Column(name = "channel_contack_email")
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	@Column(name = "channel_contack_job")
	public String getContactJob() {
		return contactJob;
	}
	public void setContactJob(String contactJob) {
		this.contactJob = contactJob;
	}
	
	@Column(name = "channel_createtime")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "channel_creator")
	public Integer getCreateorId() {
		return createorId;
	}
	public void setCreateorId(Integer createorId) {
		this.createorId = createorId;
	}
	@Column(name = "channel_modifytime")
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	@Column(name = "channel_modifier")
	public Integer getMotifierId() {
		return motifierId;
	}
	public void setMotifierId(Integer motifierId) {
		this.motifierId = motifierId;
	}
	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
	}
	@Column(name = "channel_flag")
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	@Column(name = "proportion")
	public String getProportion() {
		return proportion;
	}

	public void setProportion(String proportion) {
		this.proportion = proportion;
	}
	@Column(name = "proportion_time")
	public String getProportionTime() {
		return proportionTime;
	}

	public void setProportionTime(String proportionTime) {
		this.proportionTime = proportionTime;
	}
	
}
