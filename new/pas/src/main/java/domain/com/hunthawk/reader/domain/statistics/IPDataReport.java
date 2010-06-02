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
import javax.persistence.Transient;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * @author sunquanzhi
 *
 */
@Entity
@Table(name = "STATISTICS_IP_REPORT")
public class IPDataReport  extends PersistentObject  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8836684282485810526L;

	private Integer id;

	private String dataTime;
	
	private String opertor;
	
	
	/**
	 * 1为按天数据，2为月数据
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

	
	
	@Column(name = "stat_operator")
	public String getOpertor() {
		return opertor;
	}

	public void setOpertor(String opertor) {
		this.opertor = opertor;
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
	
	@Transient
	public String getOperatorName(){
		
		if("1".equals(opertor)){
			return "移动";
		}else if("2".equals(opertor)){
			return "联通";
		}else if("3".equals(opertor)){
			return "电信";
		}else{
			return "其它";
		}
		
	}
}
