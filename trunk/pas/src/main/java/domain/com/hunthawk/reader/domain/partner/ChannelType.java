/**
 * 
 */
package com.hunthawk.reader.domain.partner;

import java.util.ArrayList;
import java.util.List;

import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * @author BruceSun
 *
 */
public class ChannelType {
	
	private static List<ChannelType> CHANNEL_TYPES = new ArrayList<ChannelType>();
	
	static{
		ChannelType type = new ChannelType(1,"��������",1,999);
		CHANNEL_TYPES.add(type);
		type = new ChannelType(2,"�ն˳���",1000,1499);
		CHANNEL_TYPES.add(type);
		type = new ChannelType(3,"�ֻ�����",1500,1999);
		CHANNEL_TYPES.add(type);
		type = new ChannelType(4,"FreeWap",2000,3999);
		CHANNEL_TYPES.add(type);
		type = new ChannelType(5,"WWW",4000,4999);
		CHANNEL_TYPES.add(type);
		type = new ChannelType(6,"CPĬ������",5000,5999);
		CHANNEL_TYPES.add(type);
		type = new ChannelType(7,"ʵ�����",6000,6999);
		CHANNEL_TYPES.add(type);
		type = new ChannelType(8,"ƽ��ý��",7000,7999);
		CHANNEL_TYPES.add(type);
		type = new ChannelType(9,"��Ӫ���������������ţ�",8000,8999);
		CHANNEL_TYPES.add(type);
		type = new ChannelType(10,"��Ӫ�������������ط���",9000,9999);
		CHANNEL_TYPES.add(type);
	}
	
	public static List<ChannelType> getAllChannelTypes(){
		List<ChannelType> types = new ArrayList<ChannelType>();
		types.addAll(CHANNEL_TYPES);
		types.remove(5);
		return types;
	}
	
	public static ChannelType getChannelType(int channelTypeId){
		for(ChannelType channelType:CHANNEL_TYPES){
			if(channelType.getId() == channelTypeId)
				return channelType;
		}
		return null;
	}
	
	private int id;
	private String name;
	private int min;
	private int max;
	
	
	
	public ChannelType(int id,String name, int min, int max) {
		super();
		this.id = id;
		this.name = name;
		this.min = min;
		this.max = max;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	
	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
	}
}
