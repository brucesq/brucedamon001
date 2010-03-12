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
@Table(name = "reader_resource_comics_chapter")
public class ComicsChapter extends PersistentObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5234230422374428272L;

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
	 * 章节内容
	 */
	private String content;

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

	private String imgFormat;

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

	@Column(name = "resource_id")
	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	@Column(name = "title")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "chapter_seq")
	public Integer getChapterIndex() {
		return chapterIndex;
	}

	public void setChapterIndex(Integer chapterIndex) {
		this.chapterIndex = chapterIndex;
	}

	@Column(name = "chapter_content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "chapter_size")
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

	@Column(name = "images")
	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	@Column(name = "image_format")
	public String getImgFormat() {
		return imgFormat;
	}

	public void setImgFormat(String imgFormat) {
		this.imgFormat = imgFormat;
	}

	public boolean equals(Object o) {
		return HibernateEqualsBuilder.reflectionEquals(this, o, "id");
	}

	@Transient
	public String[] getImages() {
		if (StringUtils.isEmpty(imageName)) {
			return new String[0];
		} else {
			return imageName.split(",");
		}
	}
}
