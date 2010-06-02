/**
 * 
 */
package com.hunthawk.reader.domain.inter;

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
 * @author BruceSun
 * 
 */
@Entity
@Table(name = "reader_inter_vote_item")
public class VoteItem extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -33758124620351563L;
	
	private Integer id;
	/**
	 * 条目名称
	 */
	private String name;
	/**
	 * 图标ID
	 */
	private Integer imgId;
	/**
	 * 投票ID
	 */
	private Integer voteId;
	/**
	 * 投票序号
	 */
	private Integer voteNumber;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 创建人
	 */
	private Integer creator;

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
	@Column(name = "img_id")
	public Integer getImgId() {
		return imgId;
	}

	public void setImgId(Integer imgId) {
		this.imgId = imgId;
	}
	@Column(name = "vote_id")
	public Integer getVoteId() {
		return voteId;
	}

	public void setVoteId(Integer voteId) {
		this.voteId = voteId;
	}
	@Column(name = "vote_number")
	public Integer getVoteNumber() {
		return voteNumber;
	}

	public void setVoteNumber(Integer voteNumber) {
		this.voteNumber = voteNumber;
	}
	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "creator_id")
	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}
	public boolean equals(Object o) {
		boolean bEquals = HibernateEqualsBuilder
				.reflectionEquals(this, o, "id");
		return bEquals;
	}
}
