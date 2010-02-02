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
@Table(name = "READER_STATISTICS_ACCESS_LOG")
public class LogData extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1086139778689709992L;
	
	private Integer id;
	
	private String requestTime;
	
	private String requestUrl;
	
	private String msisdn;
	
	private String userAgent;
	
	private String ip;
	
	private String refer;
	
	private int bytes;
	
	private String provinceId;
	
	private String cityId;
	
	private String uaStart;
	// 产品ID
	private String paraPd;
	// 页面级别 首页 列表页 资源页 详情页
	private String paraPg;
	// 页面组ID
	private String paraGd;
	// 栏目ID
	private String paraCd;
	// 模板ID
	private String paraTd;
	// 区域ID
	private String paraAd;
	
	// 计费ID
	private String paraEd;
	
	// 批价包ID
	private String paraFd;
	
	// 批价包关联ID
	private String paraNd;

	// 资源ID
	private String paraRd;
	
	// 推广渠道ID
	private String paraFc;
	
	// 章节ID
	private String paraZd;
	
	//作者ID
	private String paraAu;

	// 功能性页面 添加书签\添加收藏，删除收藏等
	private String paraFn;
	
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

	@Column(name = "request_time")
	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	@Column(name = "request_url")
	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	@Column(name = "request_msisdn")
	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	@Column(name = "user_agent")
	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	@Column(name = "request_ip")
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Column(name = "request_refer")
	public String getRefer() {
		return refer;
	}

	public void setRefer(String refer) {
		this.refer = refer;
	}
	@Column(name = "res_bytes")
	public int getBytes() {
		return bytes;
	}

	public void setBytes(int bytes) {
		this.bytes = bytes;
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
	@Column(name = "ua_start")
	public String getUaStart() {
		return uaStart;
	}

	public void setUaStart(String uaStart) {
		this.uaStart = uaStart;
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
	@Column(name = "para_td")
	public String getParaTd() {
		return paraTd;
	}

	public void setParaTd(String paraTd) {
		this.paraTd = paraTd;
	}
	@Column(name = "para_ad")
	public String getParaAd() {
		return paraAd;
	}

	public void setParaAd(String paraAd) {
		this.paraAd = paraAd;
	}
	@Column(name = "para_ed")
	public String getParaEd() {
		return paraEd;
	}

	public void setParaEd(String paraEd) {
		this.paraEd = paraEd;
	}
	@Column(name = "para_fd")
	public String getParaFd() {
		return paraFd;
	}

	public void setParaFd(String paraFd) {
		this.paraFd = paraFd;
	}
	@Column(name = "para_nd")
	public String getParaNd() {
		return paraNd;
	}

	public void setParaNd(String paraNd) {
		this.paraNd = paraNd;
	}
	@Column(name = "para_rd")
	public String getParaRd() {
		return paraRd;
	}

	public void setParaRd(String paraRd) {
		this.paraRd = paraRd;
	}
	@Column(name = "para_fc")
	public String getParaFc() {
		return paraFc;
	}

	public void setParaFc(String paraFc) {
		this.paraFc = paraFc;
	}
	@Column(name = "para_zd")
	public String getParaZd() {
		return paraZd;
	}

	public void setParaZd(String paraZd) {
		this.paraZd = paraZd;
	}
	@Column(name = "para_au")
	public String getParaAu() {
		return paraAu;
	}

	public void setParaAu(String paraAu) {
		this.paraAu = paraAu;
	}
	@Column(name = "para_fn")
	public String getParaFn() {
		return paraFn;
	}

	public void setParaFn(String paraFn) {
		this.paraFn = paraFn;
	}

	@Column(name = "wap_type")
	public Integer getWapType() {
		return wapType;
	}

	public void setWapType(Integer wapType) {
		this.wapType = wapType;
	}
	

}
