package com.hunthawk.reader.domain.custom;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author yuzs
 *	������ѯ������Ϣ
 */
public class SearchOrderBackMessage extends FeeHeadMessage{
	
	private List<OrderRecord> recordList;
	
	public List<OrderRecord> getRecordList() {
		return recordList;
	}
	public void setRecordList(List<OrderRecord> recordList) {
		this.recordList = recordList;
	}
}
