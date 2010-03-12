/**
 * 
 */
package com.hunthawk.reader.service.partner;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.partner.Channel;
import com.hunthawk.reader.domain.partner.ChannelChild;
import com.hunthawk.reader.domain.partner.Provider;

/**
 * @author BruceSun
 *
 */
public interface PartnerService {

	@Logable(name = "Channel", action = "add", property = { "id=ID,channelTypeId=��������ID,chName=����,city=����,intro=���,registeredCapita=ע���ʱ�,corporate=��˾����,address=��˾��ַ,postcode=��������,phone=��˾�绰,fax=����,url=��˾��վURL,credit=���ö�(1��2��3��),bankName=��������,bankAccountName=�ʻ�����,bankAccount=�����ʺ�,contactName=�ӿ�������,contactPhone=�绰,contactMobile=�ֻ�,contactEmail=email,contactJob=ְλ,proportion=�ֳɱ���,proportionTime=��Чʱ��,createTime=����ʱ��,createorId=������ID,modifyTime=�޸�ʱ��,motifierId=�޸���ID" })
	public void addChannel(Channel channel)throws Exception;
	
	@Logable(name = "Channel", action = "update", property = { "id=ID,channelTypeId=��������ID,chName=����,city=����,intro=���,registeredCapita=ע���ʱ�,corporate=��˾����,address=��˾��ַ,postcode=��������,phone=��˾�绰,fax=����,url=��˾��վURL,credit=���ö�(1��2��3��),bankName=��������,bankAccountName=�ʻ�����,bankAccount=�����ʺ�,contactName=�ӿ�������,contactPhone=�绰,contactMobile=�ֻ�,contactEmail=email,contactJob=ְλ,proportion=�ֳɱ���,proportionTime=��Чʱ��,createTime=����ʱ��,createorId=������ID,modifyTime=�޸�ʱ��,motifierId=�޸���ID" })
	public void updateChannel(Channel channel);
	
	@Logable(name = "Channel", action = "delete", property = { "id=ID,channelTypeId=��������ID,chName=����,city=����,intro=���,registeredCapita=ע���ʱ�,corporate=��˾����,address=��˾��ַ,postcode=��������,phone=��˾�绰,fax=����,url=��˾��վURL,credit=���ö�(1��2��3��),bankName=��������,bankAccountName=�ʻ�����,bankAccount=�����ʺ�,contactName=�ӿ�������,contactPhone=�绰,contactMobile=�ֻ�,contactEmail=email,contactJob=ְλ,proportion=�ֳɱ���,proportionTime=��Чʱ��,createTime=����ʱ��,createorId=������ID,modifyTime=�޸�ʱ��,motifierId=�޸���ID" })
	public void deleteChannel(Channel channel)throws Exception;
	
	public Channel getChannel(String channelId);
	
	public List<Channel> findChannel(int pageNo,int pageSize,String orderBy,
			boolean isAsc,Collection<HibernateExpression> expressions);
	
	public long findChannelResultCount(Collection<HibernateExpression> expressions);
	
	
	@Logable(name = "ChannelChild", action = "add", property = { "id=ID,bussinessTypeId=ҵ������ID(1WAP2�ͻ���3�ֳ��豸),chName=����,city=����,intro=���,registeredCapita=ע���ʱ�,corporate=��˾����,address=��˾��ַ,postcode=��������,phone=��˾�绰,fax=����,url=��˾��վURL,credit=���ö�(1��2��3��),bankName=��������,bankAccountName=�ʻ�����,bankAccount=�����ʺ�,contactName=�ӿ�������,contactPhone=�绰,contactMobile=�ֻ�,contactEmail=email,contactJob=ְλ,proportion=�ֳɱ���,proportionTime=��Чʱ��,createTime=����ʱ��,createorId=������ID,modifyTime=�޸�ʱ��,motifierId=�޸���ID" })
	public void addChannelChild(ChannelChild child)throws Exception;
	
	@Logable(name = "ChannelChild", action = "update", property = { "id=ID,bussinessTypeId=ҵ������ID(1WAP2�ͻ���3�ֳ��豸),chName=����,city=����,intro=���,registeredCapita=ע���ʱ�,corporate=��˾����,address=��˾��ַ,postcode=��������,phone=��˾�绰,fax=����,url=��˾��վURL,credit=���ö�(1��2��3��),bankName=��������,bankAccountName=�ʻ�����,bankAccount=�����ʺ�,contactName=�ӿ�������,contactPhone=�绰,contactMobile=�ֻ�,contactEmail=email,contactJob=ְλ,proportion=�ֳɱ���,proportionTime=��Чʱ��,createTime=����ʱ��,createorId=������ID,modifyTime=�޸�ʱ��,motifierId=�޸���ID" })
	public void updateChannelChild(ChannelChild child);
	
	@Logable(name = "ChannelChild", action = "delete", property = { "id=ID,bussinessTypeId=ҵ������ID(1WAP2�ͻ���3�ֳ��豸),chName=����,city=����,intro=���,registeredCapita=ע���ʱ�,corporate=��˾����,address=��˾��ַ,postcode=��������,phone=��˾�绰,fax=����,url=��˾��վURL,credit=���ö�(1��2��3��),bankName=��������,bankAccountName=�ʻ�����,bankAccount=�����ʺ�,contactName=�ӿ�������,contactPhone=�绰,contactMobile=�ֻ�,contactEmail=email,contactJob=ְλ,proportion=�ֳɱ���,proportionTime=��Чʱ��,createTime=����ʱ��,createorId=������ID,modifyTime=�޸�ʱ��,motifierId=�޸���ID" })
	public void deleteChannelChild(ChannelChild child);
	
	public ChannelChild getChannelChild(String channelId);
	
	public List<ChannelChild> findChannelChild(int pageNo,int pageSize,String orderBy,
			boolean isAsc,Collection<HibernateExpression> expressions);
	
	public long findChannelChildResultCount(Collection<HibernateExpression> expressions);
	
	
	public List<Channel> getAllFirstLevelChannel();
	
	public List<Provider> getAllProvider();

	@Logable(name = "Provider", action = "add", property = { "id=ID,providerId=SPID/CPID,providerTypeName=SP/CP,chName=����,city=����,intro=���,registeredCapita=ע���ʱ�,statusName=״̬,corporate=��˾����,address=��˾��ַ,postcode=��������,phone=��˾�绰,fax=����,url=��˾��վURL,credit=���ö�(1��2��3��),bankName=��������,bankAccountName=�ʻ�����,bankAccount=�����ʺ�,contactName=�ӿ�������,contactPhone=�绰,contactMobile=�ֻ�,contactEmail=email,contactJob=ְλ,proportion=�ֳɱ���,proportionTime=��Чʱ��,createTime=����ʱ��,createorId=������ID,modifyTime=�޸�ʱ��,motifierId=�޸���ID" })
	public void addProvider(Provider provider)throws Exception;
	
	@Logable(name = "Provider", action = "update", property = { "id=ID,providerId=SPID/CPID,providerTypeName=SP/CP,chName=����,city=����,intro=���,registeredCapita=ע���ʱ�,statusName=״̬,corporate=��˾����,address=��˾��ַ,postcode=��������,phone=��˾�绰,fax=����,url=��˾��վURL,credit=���ö�(1��2��3��),bankName=��������,bankAccountName=�ʻ�����,bankAccount=�����ʺ�,contactName=�ӿ�������,contactPhone=�绰,contactMobile=�ֻ�,contactEmail=email,contactJob=ְλ,proportion=�ֳɱ���,proportionTime=��Чʱ��,createTime=����ʱ��,createorId=������ID,modifyTime=�޸�ʱ��,motifierId=�޸���ID" })
	public void updateProvider(Provider provider)throws Exception;
	
	@Logable(name = "Provider", action = "delete", property = { "id=ID,providerId=SPID/CPID,providerTypeName=SP/CP,chName=����,city=����,intro=���,registeredCapita=ע���ʱ�,statusName=״̬,corporate=��˾����,address=��˾��ַ,postcode=��������,phone=��˾�绰,fax=����,url=��˾��վURL,credit=���ö�(1��2��3��),bankName=��������,bankAccountName=�ʻ�����,bankAccount=�����ʺ�,contactName=�ӿ�������,contactPhone=�绰,contactMobile=�ֻ�,contactEmail=email,contactJob=ְλ,proportion=�ֳɱ���,proportionTime=��Чʱ��,createTime=����ʱ��,createorId=������ID,modifyTime=�޸�ʱ��,motifierId=�޸���ID" })
	public void deleteProvider(Provider provider)throws Exception;
	
	public Provider getProvider(Integer providerId);
	
	@Logable(name = "Provider", action = "audit", property = { "id=ID,providerId=SPID/CPID,providerTypeName=SP/CP,chName=����,city=����,intro=���,registeredCapita=ע���ʱ�,statusName=״̬,corporate=��˾����,address=��˾��ַ,postcode=��������,phone=��˾�绰,fax=����,url=��˾��վURL,credit=���ö�(1��2��3��),bankName=��������,bankAccountName=�ʻ�����,bankAccount=�����ʺ�,contactName=�ӿ�������,contactPhone=�绰,contactMobile=�ֻ�,contactEmail=email,contactJob=ְλ,proportion=�ֳɱ���,proportionTime=��Чʱ��,createTime=����ʱ��,createorId=������ID,modifyTime=�޸�ʱ��,motifierId=�޸���ID" })
	@Restrict( roles = { "provideraudit" }, mode = Restrict.Mode.ROLE)
	public void auditProvider(Provider provider,Integer status)throws Exception;
	
	public List<Provider> findProvider(int pageNo,int pageSize,String orderBy,
			boolean isAsc,Collection<HibernateExpression> expressions);
	
	public long findProviderResultCount(Collection<HibernateExpression> expressions);
}
