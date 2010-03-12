package com.hunthawk.reader.domain.adapter;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * 适配器规则
 * 
 * @author penglei
 * 
 */
@Entity
@Table(name = "reader_adapter_adapterRule")
public class AdapterRule extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4200734796019145920L;

	/**
	 * ID，唯一主键，自增长
	 */
	private Integer id;

	/**
	 * 适配规则名称
	 */
	private String name;

	/**
	 * 适配器ID
	 */
	private Integer adapterId;

	/**
	 * 适配类型ID
	 */
	private Integer adapterTypeId;

	/**
	 * 跳转类型 0 直接跳转 1 链接跳转 2 显示区块代码内容
	 * 
	 */
	private int gotoType;

	/**
	 * 链接文字
	 * */
	private String linkTitle;

	/**
	 * 链接URL
	 * */
	private String linkUrl;

	/**
	 * 区块代码
	 * */
	private String blockCode;

	/**
	 * 状态
	 * */
	private int status;

	/**
	 * 地区条件集合
	 * */
	private String areas;

	/**
	 * 起始时间
	 * */
	private Date beginTime;

	/**
	 * 结束时间
	 * */
	private Date endTime;

	/**
	 * UA终端组ID
	 * */
	private Integer uaGroupId;
	
	/**
	 * 从星期X开始
	 */
	private Integer beginWeek;
	
	/**
	 * 到星期X结束
	 */
	private Integer endWeek;
	
	/**
	 * 从X点开始
	 */
	private Integer beginHour;
	
	/**
	 * 到X点结束
	 */
	private Integer endHour;

	public AdapterRule() {
		super();
	}

	@Id
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "adapter_id")
	public Integer getAdapterId() {
		return adapterId;
	}

	public void setAdapterId(Integer adapterId) {
		this.adapterId = adapterId;
	}

	@Column(name = "adapter_Type_id")
	public Integer getAdapterTypeId() {
		return adapterTypeId;
	}

	public void setAdapterTypeId(Integer adapterTypeId) {
		this.adapterTypeId = adapterTypeId;
	}

	@Column(name = "goto_type")
	public int getGotoType() {
		return gotoType;
	}

	public void setGotoType(int gotoType) {
		this.gotoType = gotoType;
	}

	@Column(name = "link_title")
	public String getLinkTitle() {
		return linkTitle;
	}

	public void setLinkTitle(String linkTitle) {
		this.linkTitle = linkTitle;
	}

	@Column(name = "link_url")
	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	@Column(name = "block_code")
	public String getBlockCode() {
		return blockCode;
	}

	public void setBlockCode(String blockCode) {
		this.blockCode = blockCode;
	}

	@Column(name = "status")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "AREAS")
	public String getAreas() {
		return areas;
	}

	public void setAreas(String areas) {
		this.areas = areas;
	}

	@Column(name = "BEGIN_TIME")
	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	@Column(name = "END_TIME")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Column(name = "UAGroup_ID")
	public Integer getUaGroupId() {
		return uaGroupId;
	}

	public void setUaGroupId(Integer uaGroupId) {
		this.uaGroupId = uaGroupId;
	}

	@Column(name = "BEGIN_WEEK")
	public Integer getBeginWeek() {
		return beginWeek;
	}

	public void setBeginWeek(Integer beginWeek) {
		this.beginWeek = beginWeek;
	}

	@Column(name = "END_WEEK")
	public Integer getEndWeek() {
		return endWeek;
	}

	public void setEndWeek(Integer endWeek) {
		this.endWeek = endWeek;
	}

	@Column(name = "BEGIN_HOUR")
	public Integer getBeginHour() {
		return beginHour;
	}

	public void setBeginHour(Integer beginHour) {
		this.beginHour = beginHour;
	}

	@Column(name = "END_HOUR")
	public Integer getEndHour() {
		return endHour;
	}

	public void setEndHour(Integer endHour) {
		this.endHour = endHour;
	}

}
