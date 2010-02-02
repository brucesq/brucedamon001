/**
 * 
 */
package com.hunthawk.reader.domain.bussiness;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * @author BruceSun
 *
 */
@Embeddable
public class ColumnResourceRelationId implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 499818175519994720L;
	
	
	//批价包关联ID
	private Integer relId;
	//栏目ID
	private Integer columnId;
	
	@Column(name = "ID")
	public Integer getRelId() {
		return relId;
	}
	public void setRelId(Integer relId) {
		this.relId = relId;
	}
	
	@Column(name = "COLUMN_ID")
	public Integer getColumnId() {
		return columnId;
	}
	public void setColumnId(Integer columnId) {
		this.columnId = columnId;
	}
	
	public int hashCode() {
		return columnId.hashCode() ^ relId.hashCode();
	}

	public boolean equals(Object obj) {

		if (!(obj instanceof ColumnResourceRelationId))
			return false;
		final ColumnResourceRelationId other = (ColumnResourceRelationId) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj)).append(
				this.getColumnId(), other.getColumnId()).append(this.getRelId(),
						other.getRelId()).isEquals();
	}

}
