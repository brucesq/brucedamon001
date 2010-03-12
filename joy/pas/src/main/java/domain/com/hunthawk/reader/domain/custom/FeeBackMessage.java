package com.hunthawk.reader.domain.custom;

/**
 * @author yuzs
 * 客户订购返回信息
 */
public class FeeBackMessage extends FeeHeadMessage{
	
	private String sequence_id;
	
	public String getSequence_id() {
		return sequence_id;
	}
	public void setSequence_id(String sequence_id) {
		this.sequence_id = sequence_id;
	}
	
	
}
