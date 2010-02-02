package com.hunthawk.reader.domain.adapter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * 适配器分类
 * 
 * @author penglei
 * 
 */
@Entity
@Table(name = "reader_adapter_type")
public class AdapterType extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3508313840932834664L;
	/**
	 * 区域适配
	 */
	public final static int GOTO_ADAPTER_TYPE_AREA = 1;
	/**
	 * 时间适配
	 */
	public final static int GOTO_ADAPTER_TYPE_TIME = 2;
	/**
	 * UA适配
	 */
	public final static int GOTO_ADAPTER_TYPE_UA = 3;
	
	/**
	 * 按周适配
	 */
	public final static int GOTO_ADAPTER_TYPE_WEEK_TIME =4;
	
	/**
	 * 按天适配
	 */
	public final static int GOTO_ADAPTER_TYPE_HOUR_TIME =5;

	/**
	 * 主键
	 */
	private int id;

	/**
	 * 名称
	 */
	private String name;

	public AdapterType() {
	}

	public AdapterType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	@Id
	@GeneratedValue(generator = "system-assigned")
	@GenericGenerator(name = "system-assigned", strategy = "assigned")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
