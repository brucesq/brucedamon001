package com.hunthawk.reader.domain.custom;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
/**
 * 
 * @author liuxh 09-11-13
 *
 */
@Embeddable
public class UserFavoritesPk implements Serializable {

		private static final long serialVersionUID = 4761635717602797949L;

		//�ֻ�����
		private String mobile;
		
		//����ID
		private String contentId;
		//��ƷID
		private String productid;

		@Column(name = "PRODUCTID")
		public String getProductid() {
			return productid;
		}

		public void setProductid(String productid) {
			this.productid = productid;
		}
		
		@Column(name = "mobile")
		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		@Column(name = "contentid")
		public String getContentId() {
			return contentId;
		}

		public void setContentId(String contentId) {
			this.contentId = contentId;
		}
}
