package com.hunthawk.reader.domain.custom;

import java.util.List;

public class UserOrderBackMessage extends FeeHeadMessage{

	private List<UserOrderRecordMessage> recordList;

	public List<UserOrderRecordMessage> getRecordList() {
		return recordList;
	}

	public void setRecordList(List<UserOrderRecordMessage> recordList) {
		this.recordList = recordList;
	}
	
}
