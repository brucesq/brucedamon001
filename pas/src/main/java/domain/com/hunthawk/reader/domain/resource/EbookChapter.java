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
 * @author xianlaichen
 *
 */
@Entity
/*
@javax.persistence.SequenceGenerator(
		name = "chapterid",
		sequenceName="reader_resource_b_chapter_seq"
)
*/
@Table(name = "reader_resource_ebook_chapter")

public class EbookChapter extends PersistentObject{


	/**
	 * 
	 */
	private static final long serialVersionUID = 7595736986123271500L;
	
	/**
	* ͼ���½�ID
	* ����ΪYXXXXXXXNNN,��11λ��
	* Y:��Դ���1ͼ�飬2��ֽ��3��־��4������5������6��Ƶ
	* XXXXXXX: ��Դ���кţ���0����λ��
	* NNN:�½����кţ���0����λ��
	*/

	private String id;
	
	/**
	* ��ԴID
	*/
	private String resourceId;
	
	/**
	* �½�����
	*/
	private String name;
	
	/**
	* �½����
	*/	
	private Integer chapterIndex;
	
	/**
	* �½�����
	*/	
	private String bContent;
	
	/**
	* �½��ļ���С
	*/	
	private Integer chapterSize;
	
	/**
	* �½ڹ������ID
	*/	
	private String tomeId;
	
	/**
	* �½�ͼƬ����
	*/	
	private Integer imageNum;
	
	/**
	* �½�ͼƬ����
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
	@Column(name = "resource_id")
	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	@Column(name = "c_name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "chapter_index")
	public Integer getChapterIndex() {
		return chapterIndex;
	}

	public void setChapterIndex(Integer chapterIndex) {
		this.chapterIndex = chapterIndex;
	}
	@Column(name = "b_content")
	public String getBContent() {
		return bContent;
	}

	public void setBContent(String bContent) {
		this.bContent = bContent;
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
	@Column(name = "image_num")
	public Integer getImageNum() {
		return imageNum;
	}
	public void setImageNum(Integer imageNum) {
		this.imageNum = imageNum;
	}
	@Column(name = "image_name")
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	
	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
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
