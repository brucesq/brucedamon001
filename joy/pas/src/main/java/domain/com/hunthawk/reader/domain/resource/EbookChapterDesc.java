/**
 * 
 */
package com.hunthawk.reader.domain.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * @author BruceSun
 *
 */
@Entity
@Table(name = "reader_resource_ebook_chapter")
public class EbookChapterDesc extends PersistentObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2315074778609404162L;

	/**
	* 图书章节ID
	* 规则为YXXXXXXXNNN,共11位，
	* Y:资源类别1图书，2报纸，3杂志，4漫画，5铃声，6视频
	* XXXXXXX: 资源序列号，由0补足位数
	* NNN:章节序列号，由0补足位数
	*/

	private String id;
	
	/**
	* 资源ID
	*/
	private String resourceId;
	
	/**
	* 章节名称
	*/
	private String name;
	
	/**
	* 章节序号
	*/	
	private Integer chapterIndex;
	
	
	
	/**
	* 章节文件大小
	*/	
	private Integer chapterSize;
	
	/**
	* 章节归属卷的ID
	*/	
	private String tomeId;
	
	
	
	/**
	* 章节图片名称
	*/	
	private String imageName;
	
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
	@Column(name = "resource_id",nullable = false)
	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	@Column(name = "c_name",nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "chapter_index",nullable = false)
	public Integer getChapterIndex() {
		return chapterIndex;
	}

	public void setChapterIndex(Integer chapterIndex) {
		this.chapterIndex = chapterIndex;
	}
	
	@Column(name = "chapter_size",nullable = false)
	public Integer getChapterSize() {
		return chapterSize;
	}

	public void setChapterSize(Integer chapterSize) {
		this.chapterSize = chapterSize;
	}
	@Column(name = "tome_id")
	public String getTomeId() {
		return tomeId;
	}
	public void setTomeId(String tomeId) {
		this.tomeId = tomeId;
	}
	
	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
	}

	@Column(name = "image_name")
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	@Transient
	public String[] getImages(){
		if(StringUtils.isEmpty(imageName)){
			return new String[0];
		}else{
			return imageName.split(",");
		}
	}
}
