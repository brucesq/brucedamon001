package com.hunthawk.reader.domain.statistics;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * 统计没有做适配的ua
 * 
 * @author penglei
 * 
 */
@Entity
//@javax.persistence.SequenceGenerator(name = "statdata", sequenceName = "READER_STATISTICS_DATA_SEQ")
@Table(name = "READER_StatisticsUA_DATA")
public class StatisticsUA {

	private Integer id;
	private String shortUA;
	private String longUA;
	private Date createTime;

	@Id
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statdata")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "SHORT_UA")
	public String getShortUA() {
		return shortUA;
	}

	public void setShortUA(String shortUA) {
		this.shortUA = shortUA;
	}

	@Column(name = "LONG_UA")
	public String getLongUA() {
		return longUA;
	}

	public void setLongUA(String longUA) {
		this.longUA = longUA;
	}

	@Column(name = "CREATE_TIME")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public boolean equals(Object o) {
		boolean bEquals = HibernateEqualsBuilder
				.reflectionEquals(this, o, "id");
		return bEquals;
	}
}
