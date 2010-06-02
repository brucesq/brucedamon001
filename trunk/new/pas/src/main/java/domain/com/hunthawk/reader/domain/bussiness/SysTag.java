/**
 * 
 */
package com.hunthawk.reader.domain.bussiness;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * @author BruceSun
 *
 */
@Entity
@Table(name = "reader_tag_sys")
public class SysTag extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -308515297119408699L;
	
	/**
     * 标签向导类型
     */
    public static final String[] TAGGUIDETYPE = { "Dialog", "Static" };
	
	private Integer id;
	
	/**
	 * 标签的中文名称，方便记忆标签
	 */
	private String title;

	/**
	 * 标签用来解析时候用的名称，诸如columnList
	 */
	private String name;

	/**
	 * 标签类型
	 */
	private String type;

	/**
	 * 标签所属类别
	 */
	private String pertaintype;

	/**
	 * 标签向导页面的url地址
	 */
	private String wizardPage;

	/**
	 * 向导页面中需要使用的参数集合
	 */
	private String wizardParas ;

	/**
	 * 可以使用这个标签的模板集合
	 */
	private String usedTmpls;

	
	@Id
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "id")
	public Integer getId() {
		return this.id;
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

	@Column(name = "name", unique = true, nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "pertaintype")
	public String getPertaintype() {
		return pertaintype;
	}

	public void setPertaintype(String pertaintype) {
		this.pertaintype = pertaintype;
	}
	@Column(name = "wizardpage")
	public String getWizardPage() {
		return wizardPage;
	}

	public void setWizardPage(String wizardPage) {
		this.wizardPage = wizardPage;
	}
	@Column(name = "wizardparas")
	public String getWizardParas() {
		return wizardParas;
	}

	public void setWizardParas(String wizardParas) {
		this.wizardParas = wizardParas;
	}
	@Column(name = "usedtmpls")
	public String getUsedTmpls() {
		return usedTmpls;
	}

	public void setUsedTmpls(String usedTmpls) {
		this.usedTmpls = usedTmpls;
	}
	
	
	
}
