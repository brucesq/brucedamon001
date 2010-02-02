/**
 * 
 */
package com.hunthawk.reader.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * ��¼��־
 * 
 * @author yuzs
 * 
 */
@Entity
@Table(name = "reader_offLine_log")
public class OffLineLog extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7866058378673795234L;
	
	private Integer id;
	/**
	 * ��־��Ϣ
	 */
	private String value;
	/**
	 * ��־״̬��
	 * 0����ʼ��1�����̣�2��������Ϣ��3������
	 */
	private Integer status;
	/**
	 * ��־����Ϣ��ͬһ�����̽�����־���ݿ�ı�ʶ
	 */
	private Integer mark;
	/**
	 * ��¼��־ʱ ��ͬһ���ļ������������ļ���������ʶ
	 */
	public String packName;
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
	@Column(name = "log_value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	@Column(name = "log_status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name = "log_mark")
	public Integer getMark() {
		return mark;
	}

	public void setMark(Integer mark) {
		this.mark = mark;
	}

	@Column(name = "LOG_PACKNAME")
	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public boolean equals(Object o) {
		boolean bEquals = HibernateEqualsBuilder
				.reflectionEquals(this, o, "id");
		return bEquals;
	}
}
