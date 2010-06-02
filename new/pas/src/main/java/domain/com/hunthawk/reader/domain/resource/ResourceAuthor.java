package com.hunthawk.reader.domain.resource;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.enhance.util.ToolDateUtil;

/**
 * @author xianlaichen
 *
 */
@Entity
//@javax.persistence.SequenceGenerator(
//		name = "authorid",
//		sequenceName="reader_resource_author_seq"
//)
@Table(name = "reader_resource_author")

public class ResourceAuthor extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7595736986123271500L;

	private Integer id;

	/**
	* ��������
	*/
	private String name;
	
	private String showUrl;
	
	private String authorPic;
	
	private String initialLetter;

	/**
	* ���߱���
	*/
	private String penName;
	/**
	 * �Ա� 1�� 2Ů
	 */
	private Integer sex;
	/**
	 * ���ߵ���
	 */
	private String area;

	/**
	* ���߼��
	*/
	private String intro;

	/**
	* ����״̬,״̬,0���ߣ�1Ϊ���ߣ�Ĭ��Ϊ0
	*/
	private Integer status;
	
	private Date createTime;
	
	private Integer creatorId;
	
	@Id
//	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="authorid")
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "author_id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "author_name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "show_url")
	public String getShowUrl() {
		return showUrl;
	}

	public void setShowUrl(String showUrl) {
		this.showUrl = showUrl;
	}
	@Column(name = "author_pic")
	public String getAuthorPic() {
		return authorPic;
	}

	public void setAuthorPic(String authorPic) {
		this.authorPic = authorPic;
	}
	@Column(name = "initial_letter")
	public String getInitialLetter() {
		return initialLetter;
	}

	public void setInitialLetter(String initialLetter) {
		this.initialLetter = initialLetter;
	}
	@Column(name = "pen_name")
	public String getPenName() {
		return penName;
	}

	public void setPenName(String penName) {
		this.penName = penName;
	}
	@Column(name = "intro")
	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}
	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name = "in_date",nullable = false)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "creator_id",nullable = false)
	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}
	
	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
	}
	
	@SuppressWarnings("unused")
	private String authorStatus;
	//����״̬
	@Transient
	public String getAuthorStatus(){
		String typeName = "δ֪״̬";
		if(status != null){
			for(Map.Entry<String,Integer> entry :Constants.getAuthorStatus().entrySet()){
				if(entry.getValue().equals(status))
					return entry.getKey();
			}
		}
		return typeName;
	}
	@Column(name = "sex")
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	
	@Column(name = "area")
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	
	@Transient
	public String getShowModifyTimeName() {
		if(createTime != null){
			return ToolDateUtil.dateToString(createTime);
		}else{
			return "";
		}
		
	}

}
