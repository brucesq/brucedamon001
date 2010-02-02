package com.hunthawk.reader.domain.custom;

/**
 * 
 * @author yuzs
 *	计费head信息
 */
public class FeeHeadMessage {
		/*<head>
		<service_code>ACIP02001</service_code>
		<svcnum>13149476324</svcnum>
		<recordsn>901662018890</recordsn>
		<flag>00000</flag>
		<detail>操作成功</detail>
	</head>
	<content>
		<record>
			<sequence_id>DD090914132305578043</sequence_id>
		</record>
	</content>*/
	/*
	* flag:
	00000 	操作成功
	00001	帐户余额不足
	00002	网络超时
	00003	系统故障
	00004	错误的服务方法编号
	00005	请求方法参数不正确
	00006	错误的请求字符串
	00007	手机号码无效*/
	
	private String service_code;
	private String svcnum;
	private String recordsn;
	private String flag;
	private String detail;
	public String getService_code() {
		return service_code;
	}
	public void setService_code(String service_code) {
		this.service_code = service_code;
	}
	public String getSvcnum() {
		return svcnum;
	}
	public void setSvcnum(String svcnum) {
		this.svcnum = svcnum;
	}
	public String getRecordsn() {
		return recordsn;
	}
	public void setRecordsn(String recordsn) {
		this.recordsn = recordsn;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	
}
