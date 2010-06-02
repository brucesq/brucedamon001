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

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * 白名单用户组
 * @author BruceSun
 *
 */
@Entity
//@javax.persistence.SequenceGenerator(
//		name = "persongroup",
//		sequenceName="reader_user_wihle_group_seq"
//)
@Table(name = "reader_user_wihle_group")
public class PersonGroup extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2783918210421968200L;

	private Integer id;
	
	private String name;
	
	private String desc;

	@Id
//	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="persongroup")
	@GeneratedValue(generator = "system-assigned")
	@GenericGenerator(name = "system-assigned", strategy = "assigned")
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

	@Column(name = "group_comment")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}
