/**
 * 
 */
package com.hunthawk.reader.domain.bussiness;

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
@Table(name = "reader_tag_userdef")
public class UserDefTag extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3890722731831849966L;
	private Integer id;
	/**
	 * 标签显示名
	 */
	private String title;

	/**
	 * 标签名，诸如columnList。不能和系统标签重命
	 */
	private String name;

	/**
	 * 自定义标签的内容
	 */
	private String content;

	/**
	 * 可以使用自定义标签的模板集合
	 */
	private String usedTmpls ;

	private String users;

	/**
	 * 标签的状态
	 */
	private Integer status;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "title", unique = true, nullable = false)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	@Column(name = "name", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	@Column(name = "usedtmpls")
	public String getUsedTmpls() {
		return usedTmpls;
	}

	public void setUsedTmpls(String usedTmpls) {
		this.usedTmpls = usedTmpls;
	}

	@Column(name = "users")
	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}
	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	

}
