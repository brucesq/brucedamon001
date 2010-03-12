/**
 * 
 */
package com.hunthawk.reader.domain.resource;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * @author BruceSun
 *
 */
@Entity
@Table(name = "reader_resource_info")
@PrimaryKeyJoinColumn(name="resource_id")
public class Infomation extends ResourceAll {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6941018281750053008L;
	//∑‚√ÊÕº
	private String image;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	
	
}
