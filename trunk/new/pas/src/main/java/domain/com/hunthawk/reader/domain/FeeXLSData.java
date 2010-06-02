package com.hunthawk.reader.domain;


import com.hunthawk.framework.domain.PersistentObject;

public class FeeXLSData extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7136297283419076340L;

	private String id ;
	private Integer feeCode;
	private String spid;
	private String proName;
	private String proUrl;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getFeeCode() {
		return feeCode;
	}
	public void setFeeCode(Integer feeCode) {
		this.feeCode = feeCode;
	}
	public String getSpid() {
		return spid;
	}
	public void setSpid(String spid) {
		this.spid = spid;
	}
	public String getProName() {
		return proName;
	}
	public void setProName(String proName) {
		this.proName = proName;
	}
	public String getProUrl() {
		return proUrl;
	}
	public void setProUrl(String proUrl) {
		this.proUrl = proUrl;
	}
	
}
