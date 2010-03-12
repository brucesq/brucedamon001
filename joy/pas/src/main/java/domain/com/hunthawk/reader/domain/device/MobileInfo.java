/**
 * 
 */
package com.hunthawk.reader.domain.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * 号段表
 * @author BruceSun
 *
 */
@Entity
@Table(name = "reader_area_user")
public class MobileInfo extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4792979588728490737L;
	//手机号前几位
	private String prefix;
	//归属地域
	private String area;
	//归属城市
	private String city;
	//手机品牌
	private String brand;
	
	@Id
	@GeneratedValue(generator = "system-assigned")
	@GenericGenerator(name = "system-assigned", strategy = "assigned")
	@Column(name = "mobile")
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	@Column(name = "area_id")
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	@Column(name = "city_id")
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	@Column(name = "brand")
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	
	
}
