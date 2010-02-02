package com.hunthawk.reader.domain.custom;

public class SearchBalanceBackMessage extends FeeHeadMessage{

	private String wo_money;
	private String pl_money;
	public String getWo_money() {
		return wo_money;
	}
	public void setWo_money(String wo_money) {
		this.wo_money = wo_money;
	}
	public String getPl_money() {
		return pl_money;
	}
	public void setPl_money(String pl_money) {
		this.pl_money = pl_money;
	}
	
	
}
