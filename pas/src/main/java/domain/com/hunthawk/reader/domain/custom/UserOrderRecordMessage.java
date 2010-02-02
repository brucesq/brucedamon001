package com.hunthawk.reader.domain.custom;

public class UserOrderRecordMessage {
	
	private String invalidate_time;
	private String create_time;
	private String pro_no;
	private String state;
	private String order_money;
	private String sequence_id;
	
	public String getInvalidate_time() {
		return invalidate_time;
	}
	public void setInvalidate_time(String invalidate_time) {
		this.invalidate_time = invalidate_time;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getPro_no() {
		return pro_no;
	}
	public void setPro_no(String pro_no) {
		this.pro_no = pro_no;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getOrder_money() {
		return order_money;
	}
	public void setOrder_money(String order_money) {
		this.order_money = order_money;
	}
	public String getSequence_id() {
		return sequence_id;
	}
	public void setSequence_id(String sequence_id) {
		this.sequence_id = sequence_id;
	}
	
	
}
