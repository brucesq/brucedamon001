/**
 * 
 */
package com.hunthawk.reader.domain.custom;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * @author BruceSun
 *
 */
@Entity
//@javax.persistence.SequenceGenerator(
//		name = "bookmark",
//		sequenceName="reader_order_mark_seq"
//)
@Table(name="reader_order_user_bookmark")
public class Bookmark extends PersistentObject{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 7168754575328994829L;

	private Integer id;
	
	private String mobile;
	
	private String title;
	//0普通书签，1自动书签
	private Integer type;
	
	private String url;
	
	private Date createTime;
	
	private String productId;

	@Id
//	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="bookmark")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	@Column(name = "title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	@Column(name = "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	@Column(name = "in_date")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "PRODUCTID")
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	

}
