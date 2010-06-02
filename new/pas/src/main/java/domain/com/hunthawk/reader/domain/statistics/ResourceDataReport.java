/**
 * 
 */
package com.hunthawk.reader.domain.statistics;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author sunquanzhi
 * 
 */
@Entity
@Table(name = "STATISTICS_RESOURCE_REPORT")
public class ResourceDataReport implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4726056778855779516L;

	private Integer id;

	private String requestTime;
	
	private String resourceId;
	
	private String resourceName;
	
	private String resourceType;
	
	private Integer pageViews;

	private Integer userViews;

	private Integer ipViews;

	private Integer allUserCount;

	private Integer newUserCount;
	
	private Integer downViews;
	
	private Integer playViews;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "request_time")
	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	@Column(name = "resource_id")
	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	@Column(name = "resource_name")
	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	@Column(name = "resource_type")
	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
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

	@Column(name = "down_views")
	public Integer getDownViews() {
		return downViews;
	}

	public void setDownViews(Integer downViews) {
		this.downViews = downViews;
	}

	@Column(name = "play_views")
	public Integer getPlayViews() {
		return playViews;
	}

	public void setPlayViews(Integer playViews) {
		this.playViews = playViews;
	}
	
	
	
	
}
