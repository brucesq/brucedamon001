/**
 * 
 */
package com.hunthawk.reader.domain.device;

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
//		name = "uagroup",
//		sequenceName="reader_useragent_group_seq"
//)
@Table(name = "reader_useragent_ua_group")
public class UAGroup  extends PersistentObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5332331414286135858L;

	private Integer id;
	
	private String desc;
	

	@Id
//	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="uagroup")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "group_comment")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	

	
}
