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
	
	

	//����ID
	private String id;
	//��������ID
	private Integer channelTypeId;
	
	
	//���ڳ���
	private String city;
	//��ҵ��������  *
	private String chName;
	//��ҵ���  *
	private String intro;
	//ע���ʱ�
	private String registeredCapita;
	//	��˾����  
	private String corporate;
	//��˾��ַ
	private String address;
	//��������
	private String postcode;
	//	��˾�绰  
	private String phone;
	//����
	private String fax;
	//	��˾��վURL 
	private String url;
	//���ö� ��
	private Integer credit;
	//��������  *
	private String bankName;
	//�ʻ�����  *
	private String bankAccountName;
	//�����ʺ�  *
	private String bankAccount;
	//	�ӿ�������  
	private String contactName;
	//�绰 
	private String contactPhone;
	//�ֻ�
	private String contactMobile;
	//email   
	private String contactEmail;
	//ְλ
	private String contactJob;
	
	private Date createTime;
	private Integer createorId;
	private Date modifyTime;
	private Integer motifierId;
	//ɾ����ʶλ 0ɾ�� 1����
	private Integer flag;
	
	//�ֳɱ���
	private String proportion;
	//�ֳɱ�����Чʱ�� ��¼���·�
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
