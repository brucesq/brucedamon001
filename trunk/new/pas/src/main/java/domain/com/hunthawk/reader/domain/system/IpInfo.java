/**
 * 
 */
package com.hunthawk.reader.domain.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * @author sunquanzhi
 *
 */
@Entity
@Table(name = "reader_system_ipinfo")
public class IpInfo  extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9186258990751832641L;
	//��ʼIP
	private String startip;
	//��������
	private Integer numbers;
	//ʡ����
	private String provinceId;
	//�б���
	private String cityId;
	//��Ӫ�̣�0������1�ƶ���2��ͨ��3����
	private String operators;
	
	@Id
	@GeneratedValue(generator = "system-assigned")
	@GenericGenerator(name = "system-assigned", strategy = "assigned")
	@Column(name = "start_ip")
	public String getStartip() {
		return startip;
	}
	public void setStartip(String startip) {
		this.startip = startip;
	}
	@Column(name = "ip_numbers")
	public Integer getNumbers() {
		return numbers;
	}
	public void setNumbers(Integer numbers) {
		this.numbers = numbers;
	}
	@Column(name = "province_id")
	public String getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}
	@Column(name = "city_id")
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	@Column(name = "operator_id")
	public String getOperators() {
		return operators;
	}
	public void setOperators(String operators) {
		this.operators = operators;
	}
	
	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"startip");
	}
	
}
