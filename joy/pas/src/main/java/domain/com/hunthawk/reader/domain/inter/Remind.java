/**
 * 
 */
package com.hunthawk.reader.domain.inter;

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
 * ����������
 * 
 * @author BruceSun
 * 
 */
@Entity
@Table(name = "reader_inter_remind")
public class Remind extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8320850217971341123L;

	private Integer id;
	// ״̬ 0δ���� 1���ڷ��� 2������� 3ɾ��
	private Integer status;
	// ����ʱ��
	private Date sendTime;
	// ����
	private String name;

	private Integer creator;

	private Date createTime;

	private Integer modifier;

	private Date modifyTime;
	/**
	 * �·�PUSH��
	 */
	private String pushWord;
	/**
	 * �·�URL
	 */
	private String pushUrl;
	/**
	 * ��ע����ԴID
	 */
	private String contentId;
	/**
	 * �Ƿ������ж����û���0��1��
	 */
	private Integer allFeeuser;
	/**
	 * �Ƿ�������Ԥ���û�,0��1��
	 */
	private Integer allReservation;
	/**
	 * ���ͺ�������
	 */
	private Integer sendNum;

	@Id
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "send_time")
	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "create_id")
	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "modify_id")
	public Integer getModifier() {
		return modifier;
	}

	public void setModifier(Integer modifier) {
		this.modifier = modifier;
	}

	@Column(name = "modify_time")
	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	
	@Column(name = "push_word")
	public String getPushWord() {
		return pushWord;
	}

	public void setPushWord(String pushWord) {
		this.pushWord = pushWord;
	}
	@Column(name = "push_url")
	public String getPushUrl() {
		return pushUrl;
	}

	public void setPushUrl(String pushUrl) {
		this.pushUrl = pushUrl;
	}
	
	
	@Column(name = "content_id")
	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	@Column(name = "fee_user")
	public Integer getAllFeeuser() {
		return allFeeuser;
	}

	public void setAllFeeuser(Integer allFeeuser) {
		this.allFeeuser = allFeeuser;
	}
	@Column(name = "all_reserva")
	public Integer getAllReservation() {
		return allReservation;
	}

	public void setAllReservation(Integer allReservation) {
		this.allReservation = allReservation;
	}

	
	@Column(name = "send_num")
	public Integer getSendNum() {
		return sendNum;
	}

	public void setSendNum(Integer sendNum) {
		this.sendNum = sendNum;
	}

	@Transient
	public String getStatusName() {
		switch (status) {
		case 0:
			return "δ����";
		case 1:
			return "���ڷ���";
		case 2:
			return "�������";
		case 3:
			return "ɾ��";
		}
		return "";
	}
	
	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
	}
}
