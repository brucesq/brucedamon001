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

	@Logable(name = "Channel", action = "add", property = { "id=ID,channelTypeId=渠道类型ID,chName=名称,city=城市,intro=简称,registeredCapita=注册资本,corporate=公司法人,address=公司地址,postcode=邮政编码,phone=公司电话,fax=传真,url=公司网站URL,credit=信用度(1差2中3高),bankName=开户银行,bankAccountName=帐户名称,bankAccount=银行帐号,contactName=接口人姓名,contactPhone=电话,contactMobile=手机,contactEmail=email,contactJob=职位,proportion=分成比例,proportionTime=生效时间,createTime=创建时间,createorId=创建者ID,modifyTime=修改时间,motifierId=修改人ID" })
	public void addChannel(Channel channel)throws Exception;
	
	@Logable(name = "Channel", action = "update", property = { "id=ID,channelTypeId=渠道类型ID,chName=名称,city=城市,intro=简称,registeredCapita=注册资本,corporate=公司法人,address=公司地址,postcode=邮政编码,phone=公司电话,fax=传真,url=公司网站URL,credit=信用度(1差2中3高),bankName=开户银行,bankAccountName=帐户名称,bankAccount=银行帐号,contactName=接口人姓名,contactPhone=电话,contactMobile=手机,contactEmail=email,contactJob=职位,proportion=分成比例,proportionTime=生效时间,createTime=创建时间,createorId=创建者ID,modifyTime=修改时间,motifierId=修改人ID" })
	public void updateChannel(Channel channel);
	
	@Logable(name = "Channel", action = "delete", property = { "id=ID,channelTypeId=渠道类型ID,chName=名称,city=城市,intro=简称,registeredCapita=注册资本,corporate=公司法人,address=公司地址,postcode=邮政编码,phone=公司电话,fax=传真,url=公司网站URL,credit=信用度(1差2中3高),bankName=开户银行,bankAccountName=帐户名称,bankAccount=银行帐号,contactName=接口人姓名,contactPhone=电话,contactMobile=手机,contactEmail=email,contactJob=职位,proportion=分成比例,proportionTime=生效时间,createTime=创建时间,createorId=创建者ID,modifyTime=修改时间,motifierId=修改人ID" })
	public void deleteChannel(Channel channel)throws Exception;
	
	public Channel getChannel(String channelId);
	
	public List<Channel> findChannel(int pageNo,int pageSize,String orderBy,
			boolean isAsc,Collection<HibernateExpression> expressions);
	
	public long findChannelResultCount(Collection<HibernateExpression> expressions);
	
	
	@Logable(name = "ChannelChild", action = "add", property = { "id=ID,bussinessTypeId=业务类型ID(1WAP2客户端3手持设备),chName=名称,city=城市,intro=简称,registeredCapita=注册资本,corporate=公司法人,address=公司地址,postcode=邮政编码,phone=公司电话,fax=传真,url=公司网站URL,credit=信用度(1差2中3高),bankName=开户银行,bankAccountName=帐户名称,bankAccount=银行帐号,contactName=接口人姓名,contactPhone=电话,contactMobile=手机,contactEmail=email,contactJob=职位,proportion=分成比例,proportionTime=生效时间,createTime=创建时间,createorId=创建者ID,modifyTime=修改时间,motifierId=修改人ID" })
	public void addChannelChild(ChannelChild child)throws Exception;
	
	@Logable(name = "ChannelChild", action = "update", property = { "id=ID,bussinessTypeId=业务类型ID(1WAP2客户端3手持设备),chName=名称,city=城市,intro=简称,registeredCapita=注册资本,corporate=公司法人,address=公司地址,postcode=邮政编码,phone=公司电话,fax=传真,url=公司网站URL,credit=信用度(1差2中3高),bankName=开户银行,bankAccountName=帐户名称,bankAccount=银行帐号,contactName=接口人姓名,contactPhone=电话,contactMobile=手机,contactEmail=email,contactJob=职位,proportion=分成比例,proportionTime=生效时间,createTime=创建时间,createorId=创建者ID,modifyTime=修改时间,motifierId=修改人ID" })
	public void updateChannelChild(ChannelChild child);
	
	@Logable(name = "ChannelChild", action = "delete", property = { "id=ID,bussinessTypeId=业务类型ID(1WAP2客户端3手持设备),chName=名称,city=城市,intro=简称,registeredCapita=注册资本,corporate=公司法人,address=公司地址,postcode=邮政编码,phone=公司电话,fax=传真,url=公司网站URL,credit=信用度(1差2中3高),bankName=开户银行,bankAccountName=帐户名称,bankAccount=银行帐号,contactName=接口人姓名,contactPhone=电话,contactMobile=手机,contactEmail=email,contactJob=职位,proportion=分成比例,proportionTime=生效时间,createTime=创建时间,createorId=创建者ID,modifyTime=修改时间,motifierId=修改人ID" })
	public void deleteChannelChild(ChannelChild child);
	
	public ChannelChild getChannelChild(String channelId);
	
	public List<ChannelChild> findChannelChild(int pageNo,int pageSize,String orderBy,
			boolean isAsc,Collection<HibernateExpression> expressions);
	
	public long findChannelChildResultCount(Collection<HibernateExpression> expressions);
	
	
	public List<Channel> getAllFirstLevelChannel();
	
	public List<Provider> getAllProvider();

	@Logable(name = "Provider", action = "add", property = { "id=ID,providerId=SPID/CPID,providerTypeName=SP/CP,chName=名称,city=城市,intro=简称,registeredCapita=注册资本,statusName=状态,corporate=公司法人,address=公司地址,postcode=邮政编码,phone=公司电话,fax=传真,url=公司网站URL,credit=信用度(1差2中3高),bankName=开户银行,bankAccountName=帐户名称,bankAccount=银行帐号,contactName=接口人姓名,contactPhone=电话,contactMobile=手机,contactEmail=email,contactJob=职位,proportion=分成比例,proportionTime=生效时间,createTime=创建时间,createorId=创建者ID,modifyTime=修改时间,motifierId=修改人ID" })
	public void addProvider(Provider provider)throws Exception;
	
	@Logable(name = "Provider", action = "update", property = { "id=ID,providerId=SPID/CPID,providerTypeName=SP/CP,chName=名称,city=城市,intro=简称,registeredCapita=注册资本,statusName=状态,corporate=公司法人,address=公司地址,postcode=邮政编码,phone=公司电话,fax=传真,url=公司网站URL,credit=信用度(1差2中3高),bankName=开户银行,bankAccountName=帐户名称,bankAccount=银行帐号,contactName=接口人姓名,contactPhone=电话,contactMobile=手机,contactEmail=email,contactJob=职位,proportion=分成比例,proportionTime=生效时间,createTime=创建时间,createorId=创建者ID,modifyTime=修改时间,motifierId=修改人ID" })
	public void updateProvider(Provider provider)throws Exception;
	
	@Logable(name = "Provider", action = "delete", property = { "id=ID,providerId=SPID/CPID,providerTypeName=SP/CP,chName=名称,city=城市,intro=简称,registeredCapita=注册资本,statusName=状态,corporate=公司法人,address=公司地址,postcode=邮政编码,phone=公司电话,fax=传真,url=公司网站URL,credit=信用度(1差2中3高),bankName=开户银行,bankAccountName=帐户名称,bankAccount=银行帐号,contactName=接口人姓名,contactPhone=电话,contactMobile=手机,contactEmail=email,contactJob=职位,proportion=分成比例,proportionTime=生效时间,createTime=创建时间,createorId=创建者ID,modifyTime=修改时间,motifierId=修改人ID" })
	public void deleteProvider(Provider provider)throws Exception;
	
	public Provider getProvider(Integer providerId);
	
	@Logable(name = "Provider", action = "audit", property = { "id=ID,providerId=SPID/CPID,providerTypeName=SP/CP,chName=名称,city=城市,intro=简称,registeredCapita=注册资本,statusName=状态,corporate=公司法人,address=公司地址,postcode=邮政编码,phone=公司电话,fax=传真,url=公司网站URL,credit=信用度(1差2中3高),bankName=开户银行,bankAccountName=帐户名称,bankAccount=银行帐号,contactName=接口人姓名,contactPhone=电话,contactMobile=手机,contactEmail=email,contactJob=职位,proportion=分成比例,proportionTime=生效时间,createTime=创建时间,createorId=创建者ID,modifyTime=修改时间,motifierId=修改人ID" })
	@Restrict( roles = { "provideraudit" }, mode = Restrict.Mode.ROLE)
	public void auditProvider(Provider provider,Integer status)throws Exception;
	
	public List<Provider> findProvider(int pageNo,int pageSize,String orderBy,
			boolean isAsc,Collection<HibernateExpression> expressions);
	
	public long findProviderResultCount(Collection<HibernateExpression> expressions);
}
