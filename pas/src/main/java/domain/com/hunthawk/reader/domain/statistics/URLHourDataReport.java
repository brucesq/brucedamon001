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
@Table(name = "STATISTICS_URL_HOUR_DATA_REPORT")
public class URLHourDataReport extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6081159648815297L;

	private Integer id;

	private String dataTime;
	
	private Integer configId;
	/**
	 * 1为config，2为config group数据
	 */
	private Integer statType;

	private Integer pageViews;

	private Integer userViews;

	private Integer ipViews;

	private Integer allUserCount;

	private Integer newUserCount;

	

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
	
	
	@Column(name = "config_id")
	public Integer getConfigId() {
		return configId;
	}

	public void setConfigId(Integer configId) {
		this.configId = configId;
	}

	@Column(name = "data_time")
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

	@Column(name = "page_views")
	public Integer getPageViews() {
		return pageViews;
	}

	public void setPageViews(Integer pageViews) {
		this.pageViews = pageViews;
	}
	@Column(name = "user_views")
	public Integer getUserViews() {
		return userViews;
	}

	public void setUserViews(Integer userViews) {
		this.userViews = userViews;
	}
	@Column(name = "ip_views")
	public Integer getIpViews() {
		return ipViews;
	}

	public void setIpViews(Integer ipViews) {
		this.ipViews = ipViews;
	}

	@Column(name = "all_user_count")
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
	@Column(name = "bytes")
	public Integer getBytes() {
		return bytes;
	}

	public void setBytes(Integer bytes) {
		this.bytes = bytes;
	}

	@Column(name = "wap_type")
	public Integer getWapType() {
		return wapType;
	}

	public void setWapType(Integer wapType) {
		this.wapType = wapType;
	}
	
}
