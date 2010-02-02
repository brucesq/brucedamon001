package com.hunthawk.reader.timer.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/***
 * Ô¶³ÌÊý¾Ý¿â
 * 
 * @author penglei
 * 
 */

@Entity
@Table(name = "DM_OP_RESOURCE_DT")
public class ResourceDT implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1652614609013962654L;

	private String rowid;

	private String resourceId;

	private String createTime;

	private Integer dpv;
	
	private Integer rpv;

	private String provinceId;
	
	private String feeType;
	
	private Integer totalIncome;
	
	private Integer totalFeeTimes;
	
	private Integer totalFeeUser;
	
	private Integer totalpv;
	
	private Integer totalUser;
	
	private Integer ppv;
	
	private Integer puser;
	
	private Integer cpv;
	
	private Integer cuser;
	
	private Integer ruser;
	
	private Integer duser;
	
	private Integer favUser;
	

	

	@Id
	@Column(name = "rowid")
	public String getRowid() {
		return rowid;
	}

	public void setRowid(String rowid) {
		this.rowid = rowid;
	}

	@Column(name = "RESOURCE_ID")
	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	@Column(name = "DEAL_DATE")
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Column(name = "D_PV")
	public Integer getDpv() {
		return dpv;
	}

	public void setDpv(Integer dpv) {
		this.dpv = dpv;
	}
	
	@Column(name = "R_PV")
	public Integer getRpv() {
		return rpv;
	}

	public void setRpv(Integer rpv) {
		this.rpv = rpv;
	}
	
	@Column(name = "PROVINCE_ID")
	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	@Column(name = "FEE_TYPE")
	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	@Column(name = "TOTAL_INCOME")
	public Integer getTotalIncome() {
		return totalIncome;
	}

	public void setTotalIncome(Integer totalIncome) {
		this.totalIncome = totalIncome;
	}

	@Column(name = "TOTAL_FEE_TIMES")
	public Integer getTotalFeeTimes() {
		return totalFeeTimes;
	}

	public void setTotalFeeTimes(Integer totalFeeTimes) {
		this.totalFeeTimes = totalFeeTimes;
	}

	@Column(name = "TOTAL_FEE_USER")
	public Integer getTotalFeeUser() {
		return totalFeeUser;
	}

	public void setTotalFeeUser(Integer totalFeeUser) {
		this.totalFeeUser = totalFeeUser;
	}

	@Column(name = "TOTAL_PV")
	public Integer getTotalpv() {
		return totalpv;
	}

	public void setTotalpv(Integer totalpv) {
		this.totalpv = totalpv;
	}

	@Column(name = "TOTAL_USER")
	public Integer getTotalUser() {
		return totalUser;
	}

	public void setTotalUser(Integer totalUser) {
		this.totalUser = totalUser;
	}

	@Column(name = "P_PV")
	public Integer getPpv() {
		return ppv;
	}

	public void setPpv(Integer ppv) {
		this.ppv = ppv;
	}

	@Column(name = "P_USER")
	public Integer getPuser() {
		return puser;
	}

	public void setPuser(Integer puser) {
		this.puser = puser;
	}

	@Column(name = "C_PV")
	public Integer getCpv() {
		return cpv;
	}

	public void setCpv(Integer cpv) {
		this.cpv = cpv;
	}

	@Column(name = "C_USER")
	public Integer getCuser() {
		return cuser;
	}

	public void setCuser(Integer cuser) {
		this.cuser = cuser;
	}

	@Column(name = "R_USER")
	public Integer getRuser() {
		return ruser;
	}

	public void setRuser(Integer ruser) {
		this.ruser = ruser;
	}

	@Column(name = "D_USER")
	public Integer getDuser() {
		return duser;
	}

	public void setDuser(Integer duser) {
		this.duser = duser;
	}

	@Column(name = "FAV_USER")
	public Integer getFavUser() {
		return favUser;
	}

	public void setFavUser(Integer favUser) {
		this.favUser = favUser;
	}

}
