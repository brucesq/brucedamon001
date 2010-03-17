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
	 * ����ΪYXXXXXXXNNN,��11λ�� Y:��Դ���1ͼ�飬2��ֽ��3��־��4������5������6��Ƶ 
	 * XXXXXXX: ��Դ���кţ���0����λ��
	 * NNN:�½����кţ���0����λ��
	 */
	private String id;

	/**
	 * ÿ����Դ�����ļ���
	 */
	private String filename;
	
	private Integer chapterIndex = 0;

	/**
	 * �ļ���ʽ���ļ���չ����
	 */
	private String type = "";
	/**
	 * �ļ�������������߳��棬IPHONE�����Ϣ
	 */
	private String filedesc = "";
	/**
	 * ��ʵ���ļ�����
	 */
	private String relfiles = "";

	/**
	 * �ļ���С
	 */
	private Integer size = 0;
	/**
	 * ��ԴID
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
