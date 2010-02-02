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
     * ��ǩ������
     */
    public static final String[] TAGGUIDETYPE = { "Dialog", "Static" };
	
	private Integer id;
	
	/**
	 * ��ǩ���������ƣ���������ǩ
	 */
	private String title;

	/**
	 * ��ǩ��������ʱ���õ����ƣ�����columnList
	 */
	private String name;

	/**
	 * ��ǩ����
	 */
	private String type;

	/**
	 * ��ǩ�������
	 */
	private String pertaintype;

	/**
	 * ��ǩ��ҳ���url��ַ
	 */
	private String wizardPage;

	/**
	 * ��ҳ������Ҫʹ�õĲ�������
	 */
	private String wizardParas ;

	/**
	 * ����ʹ�������ǩ��ģ�弯��
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
