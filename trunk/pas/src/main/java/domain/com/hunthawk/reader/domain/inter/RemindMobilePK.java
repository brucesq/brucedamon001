/**
 * 
 */
package com.hunthawk.reader.domain.inter;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * @author BruceSun
 * 
 */
@Embeddable
public class RemindMobilePK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8127290764476701473L;

	private Integer remindId;
	private String mobile;

	@Column(name = "remind_id")
	public Integer getRemindId() {
		return remindId;
	}

	public void setRemindId(Integer remindId) {
		this.remindId = remindId;
	}

	@Column(name = "mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int hashCode() {
		return mobile.hashCode() ^ remindId.hashCode();
	}

	public boolean equals(Object obj) {

		if (!(obj instanceof RemindMobilePK))
			return false;
		final RemindMobilePK other = (RemindMobilePK) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj)).append(
				this.getMobile(), other.getMobile()).append(this.getRemindId(),
						other.getRemindId()).isEquals();
	}
}
