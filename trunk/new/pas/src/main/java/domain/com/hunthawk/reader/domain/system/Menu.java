/**
 * 
 */
package com.hunthawk.reader.domain.system;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * @author BruceSun
 *
 */
@Entity
@Table(name = "reader_system_menu")
public class Menu  extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6393724773621331236L;

	/**
	 * �˵�ID
	 */
	private Integer id;

	/**
	 * �˵�����
	 */
	private String title;

	/**
	 * ���˵�
	 */
	private Menu parent;

	/**
	 * ��ַ
	 */
	private String url;

	/**
	 * ����
	 */
	private String parameter;

	/**
	 * �Ӳ˵�
	 */
	private List<Menu> childs;

	/**
	 * ��Ҫ��Ȩ�޼���
	 */
	private String powers;
	@Id
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
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

	@ManyToOne
	@JoinColumn(name="parent_id")
	public Menu getParent() {
		return parent;
	}

	public void setParent(Menu parent) {
		this.parent = parent;
	}
	@Column(name = "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	@Column(name = "parameter")
	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	@OneToMany(mappedBy="parent" ,cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	@OrderBy("id")
	public List<Menu> getChilds() {
		return childs;
	}

	public void setChilds(List<Menu> childs) {
		this.childs = childs;
	}
	@Column(name = "powers")
	public String getPowers() {
		return powers;
	}

	public void setPowers(String powers) {
		this.powers = powers;
	}

	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
	}
}
