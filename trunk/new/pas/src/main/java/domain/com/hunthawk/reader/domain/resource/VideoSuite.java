/**
 * 
 */
package com.hunthawk.reader.domain.resource;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * @author BruceSun
 * 
 */
@Entity
@Table(name = "reader_resource_video_suite")
public class VideoSuite extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5290434926977772975L;
	/*
	 * 规则为YXXXXXXXNNN,共11位， Y:资源类别1图书，2报纸，3杂志，4漫画，5铃声，6视频 
	 * XXXXXXX: 资源序列号，由0补足位数
	 * NNN:章节序列号，由0补足位数
	 */
	private String id;

	/**
	 * 每个资源具体文件名
	 */
	private String filename;
	
	private Integer chapterIndex = 0;

	/**
	 * 文件格式（文件扩展名）
	 */
	private String type = "";
	/**
	 * 文件具体描述，如高潮版，IPHONE版等信息
	 */
	private String filedesc = "";
	/**
	 * 真实的文件名字
	 */
	private String relfiles = "";

	/**
	 * 文件大小
	 */
	private Integer size = 0;
	/**
	 * 资源ID
	 */
	private String resourceId;
	@Id
	@GeneratedValue(generator = "system-assigned")
	@GenericGenerator(name = "system-assigned", strategy = "assigned")
	@Column(name = "chapter_id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Column(name = "filename")
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	@Column(name = "chapter_seq")
	public Integer getChapterIndex() {
		return chapterIndex;
	}
	
	public void setChapterIndex(Integer chapterIndex) {
		this.chapterIndex = chapterIndex;
	}
	@Column(name = "file_type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Column(name = "file_size")
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	@Column(name = "resource_id")
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	@Column(name = "file_desc")
	public String getFiledesc() {
		return filedesc;
	}
	public void setFiledesc(String filedesc) {
		this.filedesc = filedesc;
	}
	
	@Column(name = "rel_files")
	public String getRelfiles() {
		return relfiles;
	}
	public void setRelfiles(String relfiles) {
		this.relfiles = relfiles;
	}
	@Transient
	public List<String> getRelfilelist(){
		String[] files = relfiles.split(";");
		List<String> fs = new ArrayList<String>();
		for(String file : files){
			if(StringUtils.isNotEmpty(file))
							fs.add(file);
		}
		return fs;
	}
}
