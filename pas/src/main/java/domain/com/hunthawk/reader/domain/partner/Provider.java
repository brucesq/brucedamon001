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
//@javax.persistence.SequenceGenerator(
//		name = "provider",
//		sequenceName="READER_PARTNER_SPCP_SEQ"
//)
@Table(name = "READER_PARTNER_SPCP")
public class Provider extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7151560545365505438L;
	
	private static Map<String,Integer> PROVIDERTYPES = new HashMap<String,Integer>();
	static{
		PROVIDERTYPES.put("SP", 0);
		PROVIDERTYPES.put("CP", 1);
	}
	public static Map<String,Integer>  getProviderTypes(){
		return PROVIDERTYPES;
	}
	
	public static final int STATUS_AUDIT_ELIGIBLE = 1;
	public static final int STATUS_AUDIT_CONTENT = 2;
	public static final int STATUS_BUSSINESS = 3;
	public static final int STATUS_SUSPEND = 4;
	public static final int STATUS_OFF = 5;
	
	private Integer id;
	//SP/CP ID��ͨ����
	private String providerId;
	//���� 0 SP 1 CP
	private Integer providerType;
	
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
	//�ֳɱ���
	private String proportion;
	//�ֳɱ�����Чʱ�� ��¼���·�
	private String proportionTime;
	
	private Date createTime;
	private Integer createorId;
	private Date modifyTime;
	private Integer motifierId;
	
	private Integer status;

	@Id
//	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="provider")
	@GeneratedValue(generator = "system-assigned")
	@GenericGenerator(name = "system-assigned", strategy = "assigned")
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "CPID")
	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	@Column(name = "IS_SPCP")
	public Integer getProviderType() {
		return providerType;
	}

	public void setProviderType(Integer providerType) {
		this.providerType = providerType;
	}
	@Column(name = "CORP_CITY")
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	@Column(name = "CORP_NAME_CH")
	public String getChName() {
		return chName;
	}

	public void setChName(String chName) {
		this.chName = chName;
	}
	@Column(name = "CORP_NAME")
	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}
	@Column(name = "CORP_CAPITAL")
	public String getRegisteredCapita() {
		return registeredCapita;
	}

	public void setRegisteredCapita(String registeredCapita) {
		this.registeredCapita = registeredCapita;
	}
	@Column(name = "CORP_M")
	public String getCorporate() {
		return corporate;
	}

	public void setCorporate(String corporate) {
		this.corporate = corporate;
	}
	@Column(name = "CORP_ADDRESS")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	@Column(name = "CORP_ZIP")
	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	@Column(name = "CORP_TEL")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Column(name = "CORP_FAX")
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}
	@Column(name = "CORP_URL")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	@Column(name = "CORP_REP")
	public Integer getCredit() {
		return credit;
	}

	public void setCredit(Integer credit) {
		this.credit = credit;
	}
	@Column(name = "CORP_BANK_NAME")
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	@Column(name = "CORP_ACCOUNTS_NAME")
	public String getBankAccountName() {
		return bankAccountName;
	}

	public void setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
	}
	@Column(name = "CORP_BANK_ACCOUNTS")
	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	@Column(name = "PORTAL_POEPLE")
	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	@Column(name = "PORTAL_PHONE")
	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	@Column(name = "PORTAL_MOBILE")
	public String getContactMobile() {
		return contactMobile;
	}

	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}
	@Column(name = "PORTAL_MAIL")
	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	@Column(name = "PORTAL_JOB")
	public String getContactJob() {
		return contactJob;
	}

	public void setContactJob(String contactJob) {
		this.contactJob = contactJob;
	}
	@Column(name = "IN_DATE")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "CREATOR_ID")
	public Integer getCreateorId() {
		return createorId;
	}

	public void setCreateorId(Integer createorId) {
		this.createorId = createorId;
	}
	@Column(name = "MODIFY_TIME")
	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	@Column(name = "MODIFY_ID")
	public Integer getMotifierId() {
		return motifierId;
	}

	public void setMotifierId(Integer motifierId) {
		this.motifierId = motifierId;
	}
	@Column(name = "STATUS")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Transient
	public String getProviderTypeName(){
		for(Map.Entry<String, Integer> entry : PROVIDERTYPES.entrySet() ){
			if(entry.getValue().equals(providerType)){
				return entry.getKey();
			}
		}
		return "";
	}
	@Transient
	public String getStatusName(){
		switch(status){
			case 1:
				return "�������";
			case 2:
				return "�������";
			case 3:
				return "����";
			case 4:
				return "��ͣ";
			case 5:
				return "����";
			default:
				return "";
		}
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
	
	
	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
	}
}
