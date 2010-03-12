/**
 * 
 */
package com.hunthawk.reader.domain.custom;

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
@Table(name="user_info")
public class UserInfo extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 177905184259135985L;

	private String mobile;
	
	private String loginname;
	
	private String nickname ;
	
	private String password;
	//0男1女2未知
	private Integer sex = 2;
	
	private Integer registered;
	
	private String birthday;//生日
	
	private String address; //居住地
	
	private String sign;//签名
	
	private String email;//  email
	
	private String qq;//   QQ    

	private String headPic;//头像


	private String interest;   //兴趣

	private String job; //职业
	
	private String desc;

	@Id
	@GeneratedValue(generator = "system-assigned")
	@GenericGenerator(name = "system-assigned", strategy = "assigned")
	@Column(name = "mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	

	@Column(name = "login_name")
	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}
	@Column(name = "nick_name")
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	@Column(name = "password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	@Column(name = "sex")
	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}
	@Column(name = "is_registered")
	public Integer getRegistered() {
		return registered;
	}

	public void setRegistered(Integer registered) {
		this.registered = registered;
	}
	@Column(name = "birthday")
	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	@Column(name = "address")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	@Column(name = "sign_name")
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	@Column(name = "email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	@Column(name = "qq")
	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}
	@Column(name = "head_pic")
	public String getHeadPic() {
		return headPic;
	}

	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}
	@Column(name = "interset")
	public String getInterest() {
		return interest;
	}

	public void setInterest(String interest) {
		this.interest = interest;
	}
	@Column(name = "job")
	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}
	@Column(name = "user_desc")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"mobile");
	}
}
