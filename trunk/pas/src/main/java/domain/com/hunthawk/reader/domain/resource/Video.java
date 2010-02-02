/**
 * 
 */
package com.hunthawk.reader.domain.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

/**
 * @author BruceSun
 *
 */
@Entity
@Table(name = "reader_resource_video")
@PrimaryKeyJoinColumn(name="resource_id")
public class Video extends ResourceAll{

	/**
	 * 
	 */
	private static final long serialVersionUID = 576144655807167685L;
	//封面图
	private String image;
	//时长，单位秒
	private Integer playTime = 0;
	
	@Column(name = "pm_pic")
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	@Column(name = "pm_time")
	public Integer getPlayTime() {
		return playTime;
	}
	public void setPlayTime(Integer playTime) {
		this.playTime = playTime;
	}
	
	@Transient
	public String getFormatPlayTime(){
		int hour = playTime/3600;
		int minute = (playTime%3600)/60;
		int second = ((playTime%3600)%60)/60;
		String s = "";
		if(hour>0){
			s=StringUtils.leftPad(""+hour,2,'0')+":";
		}
		return s+StringUtils.leftPad(""+minute,2,'0')+":"+second;
		
	}
	
	public static void main(String[] args){
		int playTime = 7992;
		int hour = playTime/3600;
		int minute = (playTime%3600)/60;
		int second = (playTime%3600)%60;
		String s = "";
		if(hour>0){
			s=StringUtils.leftPad(""+hour,2,'0')+":";
		}
		System.out.println(s+StringUtils.leftPad(""+minute,2,'0')+":"+second);
	}
	
}
