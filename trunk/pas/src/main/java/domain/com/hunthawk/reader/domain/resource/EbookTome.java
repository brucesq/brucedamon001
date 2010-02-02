package com.hunthawk.reader.domain.resource;

import com.hunthawk.framework.domain.PersistentObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * @author xianlaichen
 *
 */
@Entity
@Table(name = "reader_resource_ebook_tome")
public class EbookTome  extends PersistentObject{


	/**
	 * 
	 */
	private static final long serialVersionUID = 7595736986123271500L;
	
	/**
	* ͼ���ID
	* ����ΪYXXXXXXXNN,��10λ��
	* Y:��Դ���1ͼ�飬2��ֽ��3��־��4������5������6��Ƶ
	* XXXXXXX: ��Դ���кţ���0����λ��
	* NN:�����кţ���0����λ��
	*/

	private String id;
	
	/**
	* ��ԴID
	*/
	private String resourceId;
	
	/**
	* ������
	*/
	private String name;
	
	/**
	* �����
	*/	
	private Integer tomeIndex;
	
	
	@Id
	@GeneratedValue(generator = "system-assigned")
	@GenericGenerator(name = "system-assigned", strategy = "assigned")
	@Column(name = "tome_id")
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

	
	@Column(name = "tome_name",nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	@Column(name = "tome_index",nullable = false)
	public Integer getTomeIndex() {
		return tomeIndex;
	}

	public void setTomeIndex(Integer tomeIndex) {
		this.tomeIndex = tomeIndex;
	}

}
