/**
 * 
 */
package com.hunthawk.reader.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * 记录日志
 * 
 * @author yuzs
 * 
 */
@Entity
@Table(name = "reader_offLine_log")
public class OffLineLog extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7866058378673795234L;
	
	private Integer id;
	/**
	 * 日志信息
	 */
	private String value;
	/**
	 * 日志状态；
	 * 0：开始，1：进程，2：错误信息，3：结束
	 */
	private Integer status;
	/**
	 * 日志组信息，同一个进程进入日志数据库的标识
	 */
	private Integer mark;
	/**
	 * 记录日志时 对同一个文件操作，依次文件名称来标识
	 */
	public String packName;
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
	@Column(name = "log_value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	@Column(name = "log_status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name = "log_mark")
	public Integer getMark() {
		return mark;
	}

	public void setMark(Integer mark) {
		this.mark = mark;
	}

	@Column(name = "LOG_PACKNAME")
	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public boolean equals(Object o) {
		boolean bEquals = HibernateEqualsBuilder
				.reflectionEquals(this, o, "id");
		return bEquals;
	}
}
