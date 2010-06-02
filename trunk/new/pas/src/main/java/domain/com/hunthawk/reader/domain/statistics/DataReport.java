/**
 * 
 */
package com.hunthawk.reader.domain.statistics;

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
@Table(name = "READER_STATISTICS_DATA_REPORT")
public class DataReport extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4201510555009082611L;
	
	private Integer id;
	
	private String dataTime;
	
	//
	// 类型，1为总的，2为分页面级别
	//11产品，12页面组，13为栏目
	//
	private Integer statType; 
	
	// 产品ID
	private String paraPd;
	
	// 页面级别 首页 列表页 资源页 详情页
	private String paraPg;
	
	// 页面组ID
	private String paraGd;
	
	// 栏目ID
	private String paraCd;
	
	private String cityId;
	
	private String provinceId;
	
	private Integer pageViews;
	
	private Integer userViews;
	
	private Integer ipViews;
	
	private Integer allUserCount;
	
	private Integer newUserCount;
	
	private Integer downPageViews;
	
	private Integer downUserViews;
	
	private Integer userType;
	
	private Integer bytes;
	
	private Integer wapType;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "date_time")
	public String getDataTime() {
		return dataTime;
	}

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}
	@Column(name = "stat_type")
	public Integer getStatType() {
		return statType;
	}

	public void setStatType(Integer statType) {
		this.statType = statType;
	}
	@Column(name = "para_pd")
	public String getParaPd() {
		return paraPd;
	}

	public void setParaPd(String paraPd) {
		this.paraPd = paraPd;
	}
	@Column(name = "para_pg")
	public String getParaPg() {
		return paraPg;
	}

	public void setParaPg(String paraPg) {
		this.paraPg = paraPg;
	}
	@Column(name = "para_gd")
	public String getParaGd() {
		return paraGd;
	}

	public void setParaGd(String paraGd) {
		this.paraGd = paraGd;
	}
	@Column(name = "para_cd")
	public String getParaCd() {
		return paraCd;
	}

	public void setParaCd(String paraCd) {
		this.paraCd = paraCd;
	}
	@Column(name = "city_id")
	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	@Column(name = "province_id")
	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}
	@Column(name = "page_view")
	public Integer getPageViews() {
		return pageViews;
	}

	public void setPageViews(Integer pageViews) {
		this.pageViews = pageViews;
	}
	@Column(name = "user_view")
	public Integer getUserViews() {
		return userViews;
	}

	public void setUserViews(Integer userViews) {
		this.userViews = userViews;
	}
	@Column(name = "totle_user_count")
	public Integer getAllUserCount() {
		return allUserCount;
	}

	public void setAllUserCount(Integer allUserCount) {
		this.allUserCount = allUserCount;
	}
	@Column(name = "new_user_count")
	public Integer getNewUserCount() {
		return newUserCount;
	}

	public void setNewUserCount(Integer newUserCount) {
		this.newUserCount = newUserCount;
	}
	@Column(name = "user_type")
	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	@Column(name = "request_bytes")
	public Integer getBytes() {
		return bytes;
	}

	public void setBytes(Integer bytes) {
		this.bytes = bytes;
	}

	@Column(name = "down_page_views")
	public Integer getDownPageViews() {
		return downPageViews;
	}

	public void setDownPageViews(Integer downPageViews) {
		this.downPageViews = downPageViews;
	}
	@Column(name = "down_user_views")
	public Integer getDownUserViews() {
		return downUserViews;
	}

	public void setDownUserViews(Integer downUserViews) {
		this.downUserViews = downUserViews;
	}
	@Column(name = "wap_type")
	public Integer getWapType() {
		return wapType;
	}

	public void setWapType(Integer wapType) {
		this.wapType = wapType;
	}

	@Column(name = "ip_views")
	public Integer getIpViews() {
		return ipViews;
	}

	public void setIpViews(Integer ipViews) {
		this.ipViews = ipViews;
	}
	
	

}
