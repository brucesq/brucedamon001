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
@Table(name = "STATISTICS_UA_REPORT")
public class UADataReport implements Serializable {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -7618312484048703683L;

	private Integer id;

	private String requestTime;
	
	private String ua;
	
	private String uaLong;
	
	private Integer pageViews;

	private Integer userViews;
	
	private Integer ipViews;

	
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

	@Column(name = "ua")
	public String getUa() {
		return ua;
	}

	public void setUa(String ua) {
		this.ua = ua;
	}

	@Column(name = "ua_long")
	public String getUaLong() {
		return uaLong;
	}

	public void setUaLong(String uaLong) {
		this.uaLong = uaLong;
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
	
	
	
	
}
