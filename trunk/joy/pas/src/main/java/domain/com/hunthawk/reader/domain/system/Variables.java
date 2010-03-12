/**
 * 
 */
package com.hunthawk.reader.domain.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * @author BruceSun
 *
 */
@Entity
@Table(name = "reader_system_variables")
public class Variables extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8020191893711345215L;
	
	
	public final static String SYSTAG_PERTAINT_TYPE = "tag_of_type";

	/**
	 * Id:主键，数据库自增字段
	 */
	private Integer id;

	/**
	 * 全局属性名称
	 */
	private String name;

	/**
	 * 全局属性值
	 */
	private String value;

	/**
	 * 全局属性描述\备注
	 */
	private String description;

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
	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean equals(Object o) {
		boolean bEquals = HibernateEqualsBuilder.reflectionEquals(this, o,"id");
        return bEquals;
	}
	
}
