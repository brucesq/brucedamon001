/**
 * 
 */
package com.hunthawk.reader.domain.device;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.reader.domain.Constants;

/**
 * @author BruceSun
 *
 */
@Entity
@Table(name = "reader_useragent_ua")
public class UAInfo extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4454941681169282361L;
	
	private static Map<String,Integer> SCREEN_TYPES = new HashMap<String,Integer>();
	static{
		SCREEN_TYPES.put("窄屏", 1);
		SCREEN_TYPES.put("宽屏", 2);
	}
	public static Map<String,Integer>  getScreenTypes(){
		return SCREEN_TYPES;
	}
	
	//手机UA串
	private String ua;
	//手机屏款
	private Integer width;
	//手机屏高
	private Integer height;
	//视频支持类型
	private String videoTypes;
	//铃声支持类型
	private String ringTypes;
	//wap类型1,wap1.x;2,wap2.0;3,3g
	private Integer wapType;
	//屏幕类型1窄屏,2宽屏
	private Integer screenType;
	//创建时间
	private Date createTime;
	
	@Id
	@GeneratedValue(generator = "system-assigned")
	@GenericGenerator(name = "system-assigned", strategy = "assigned")
	@Column(name = "ua")
	public String getUa() {
		return ua;
	}
	public void setUa(String ua) {
		this.ua = ua;
	}
	@Column(name = "width")
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	@Column(name = "height")
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	@Column(name = "video_types")
	public String getVideoTypes() {
		return videoTypes;
	}
	public void setVideoTypes(String videoTypes) {
		this.videoTypes = videoTypes;
	}
	@Column(name = "ring_types")
	public String getRingTypes() {
		return ringTypes;
	}
	public void setRingTypes(String ringTypes) {
		this.ringTypes = ringTypes;
	}
	@Column(name = "wap_type")
	public Integer getWapType() {
		return wapType;
	}
	public void setWapType(Integer wapType) {
		this.wapType = wapType;
	}
	@Column(name = "in_date")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "screen_type")
	public Integer getScreenType() {
		return screenType;
	}
	public void setScreenType(Integer screenType) {
		this.screenType = screenType;
	}
	
	@Transient
	public String getVersionTypeName(){
		for(Map.Entry<String, Integer> entry:Constants.getVersionTypes().entrySet()){
			if(entry.getValue().equals(wapType)){
				return entry.getKey();
			}
		}
		return "";
	}
	
	@Transient
	public String getScreenTypeName(){
		for(Map.Entry<String, Integer> entry:SCREEN_TYPES.entrySet()){
			if(entry.getValue().equals(screenType)){
				return entry.getKey();
			}
		}
		return "";
	}
	@Transient
	public boolean isNarrowScreen(){
		if(screenType == null || screenType.equals(1)){
			return true;
		}
		return false;
	}
}
