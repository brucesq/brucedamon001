package com.hunthawk.reader.domain.custom;

/**
 * 
 * @author yuzs
 *	�Ʒ�head��Ϣ
 */
public class FeeHeadMessage {
		/*<head>
		<service_code>ACIP02001</service_code>
		<svcnum>13149476324</svcnum>
		<recordsn>901662018890</recordsn>
		<flag>00000</flag>
		<detail>�����ɹ�</detail>
	</head>
	<content>
		<record>
			<sequence_id>DD090914132305578043</sequence_id>
		</record>
	</content>*/
	/*
	* flag:
	00000 	�����ɹ�
	00001	�ʻ�����
	00002	���糬ʱ
	00003	ϵͳ����
	00004	����ķ��񷽷����
	00005	���󷽷���������ȷ
	00006	����������ַ���
	00007	�ֻ�������Ч*/
	
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
