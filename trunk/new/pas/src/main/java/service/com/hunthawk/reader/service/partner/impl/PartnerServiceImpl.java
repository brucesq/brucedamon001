/**
 * 
 */
package com.hunthawk.reader.service.partner.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.partner.Channel;
import com.hunthawk.reader.domain.partner.ChannelChild;
import com.hunthawk.reader.domain.partner.ChannelType;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.service.partner.PartnerService;

/**
 * @author BruceSun
 *
 */
public class PartnerServiceImpl implements PartnerService {
	
	private static SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");

	private HibernateGenericController controller ;
	/* (non-Javadoc)
	 * @see com.hunthawk.reader.service.partner.PartnerService#addChannel(com.hunthawk.reader.domain.partner.Channel)
	 */
	public synchronized void addChannel(Channel channel)throws Exception {
		if(channel.getId() == null || !channel.getId().startsWith("5")){
			channel.setId(getNewChannelId(channel.getChannelTypeId()));
		}
		
		channel.setFlag(1);
		Calendar today = Calendar.getInstance();
		today.setTime(new Date());
		today.add(Calendar.MONTH, 1);
		channel.setProportionTime(MONTH_FORMAT.format(today.getTime()));
		
		controller.save(channel);
		ChannelChild child = copyChannelProp(channel);
		child.setParent(channel);
		child.setId("1"+channel.getId()+"000");
		child.setBussinessTypeId(1);
		child.setIntro(channel.getIntro()+"WAP默认渠道");
		addChannelChild(child);
		child = copyChannelProp(channel);
		child.setParent(channel);
		child.setId("2"+channel.getId()+"000");
		child.setIntro(channel.getIntro()+"PAD默认渠道");
		child.setBussinessTypeId(2);
		addChannelChild(child);
		child = copyChannelProp(channel);
		child.setParent(channel);
		child.setId("3"+channel.getId()+"000");
		child.setIntro(channel.getIntro()+"APP默认渠道");
		child.setBussinessTypeId(3);
		addChannelChild(child);
	}

	/* (non-Javadoc)
	 * @see com.hunthawk.reader.service.partner.PartnerService#deleteChannel(com.hunthawk.reader.domain.partner.Channel)
	 */
	public void deleteChannel(Channel channel) {
		
//		List<ChannelChild> childs = controller.findBy(ChannelChild.class, "parent", channel);
//		for(ChannelChild child:childs){
//			deleteChannelChild(child);
//		}
		channel.setFlag(0);
		controller.update(channel);
		

	}

	/* (non-Javadoc)
	 * @see com.hunthawk.reader.service.partner.PartnerService#findChannel(int, int, java.lang.String, boolean, java.lang.String, java.lang.String)
	 */
	public List<Channel> findChannel(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions) {
		expressions.add(new CompareExpression("flag",1,CompareType.Equal));
		List<Channel> channels = controller.findBy(Channel.class,pageNo,pageSize,"id",true,expressions);
		
		return channels;
	}
	
	public List<Channel> getAllFirstLevelChannel(){
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		expressions.add(new CompareExpression("flag",1,CompareType.Equal));
		List<Channel> channels = controller.findBy(Channel.class,1,Integer.MAX_VALUE,"id",true,expressions);
		return channels;
	}

	public List<Provider> getAllProvider(){
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		expressions.add(new CompareExpression("providerType",1,CompareType.Equal));
		List<Provider> providers = controller.findBy(Provider.class,1,Integer.MAX_VALUE,"id",true,expressions);
		return providers;
	}

	/* (non-Javadoc)
	 * @see com.hunthawk.reader.service.partner.PartnerService#findChannelResultCount(java.lang.String, java.lang.String)
	 */
	public long findChannelResultCount(Collection<HibernateExpression> expressions) {
		
		expressions.add(new CompareExpression("flag",1,CompareType.Equal));
		return controller.getResultCount(Channel.class, expressions);
		
	}

	/* (non-Javadoc)
	 * @see com.hunthawk.reader.service.partner.PartnerService#getChannel(java.lang.String)
	 */
	public Channel getChannel(String channelId) {
		return controller.get(Channel.class, channelId);
	}

	/* (non-Javadoc)
	 * @see com.hunthawk.reader.service.partner.PartnerService#updateChannel(com.hunthawk.reader.domain.partner.Channel)
	 */
	public void updateChannel(Channel channel) {
		controller.update(channel);

	}
	
	
	@SuppressWarnings("unchecked")
	private String getNewChannelId(int channelTypeId)throws Exception{
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("channelTypeId",channelTypeId,CompareType.Equal);
		hibernateExpressions.add(ex);
		List<Channel> channels = controller.findBy(Channel.class,1,1,"id",false,hibernateExpressions);
		ChannelType channelType = ChannelType.getChannelType(channelTypeId);
		if(channels.size() > 0){
			Channel channel = channels.get(0);
			int channelId = Integer.parseInt(channel.getId());
			if(channelId < channelType.getMax()){
				return StringUtils.leftPad(String.valueOf(channelId+1), 4,"0");
			}else{
				throw new Exception("已经超过该渠道类型的最大值,请联系系统管理员!");
			}
			
		}else{
			return StringUtils.leftPad(String.valueOf(channelType.getMin()), 4,"0");
		}
	}
	
	
	public void setHibernateGenericController(HibernateGenericController controller){
		this.controller = controller;
	}
	
	
	public static void main(String[] args){
		String a = "0001";
		Integer b = 1;
		String c = StringUtils.leftPad(b.toString(), 4,"0");
		System.out.println(Integer.parseInt(a)+":"+c);
		String d = "sssd123";
		System.out.println(StringUtils.right(d, 3));
	}
	
	
	public synchronized void addChannelChild(ChannelChild child) throws Exception {
		if(ParameterCheck.isNullOrEmpty(child.getId())){
			child.setId(getNewChannelChildId(child.getBussinessTypeId(),child.getParent()));
		}
		Calendar today = Calendar.getInstance();
		today.setTime(new Date());
		today.add(Calendar.MONTH, 1);
		child.setProportionTime(MONTH_FORMAT.format(today.getTime()));
		
		child.setFlag(1);
		controller.save(child);
	}

	public void deleteChannelChild(ChannelChild child) {
		child.setFlag(0);
		controller.update(child);
		
	}

	public List<ChannelChild> findChannelChild(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		expressions.add(new CompareExpression("flag",1,CompareType.Equal));
		List<ChannelChild> channels = controller.findBy(ChannelChild.class,1,Integer.MAX_VALUE,"id",true,expressions);
		
		return channels;
	}

	public long findChannelChildResultCount(
			Collection<HibernateExpression> expressions) {
		expressions.add(new CompareExpression("flag",1,CompareType.Equal));
		return controller.getResultCount(ChannelChild.class, expressions);
	}

	public ChannelChild getChannelChild(String channelId) {
		return controller.get(ChannelChild.class, channelId);
		
	}

	public void updateChannelChild(ChannelChild child) {
		controller.update(child);
		
	}

	@SuppressWarnings("unchecked")
	private String getNewChannelChildId(int bussinessTypeId,Channel channel)throws Exception{
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("bussinessTypeId",bussinessTypeId,CompareType.Equal);
		hibernateExpressions.add(ex);
		ex = new CompareExpression("parent",channel,CompareType.Equal);
		hibernateExpressions.add(ex);
		List<ChannelChild> channels = controller.findBy(ChannelChild.class,1,1,"id",false,hibernateExpressions);
		String channelChildId = bussinessTypeId+channel.getId();
		if(channels.size() > 0){
			ChannelChild child = channels.get(0);
			int channelId = Integer.parseInt(StringUtils.right(child.getId(), 3));
			if(channelId < 999){
				return channelChildId+StringUtils.leftPad(String.valueOf(channelId+1), 3,"0");
			}else{
				throw new Exception("已经超过该渠道的子渠道的最大值,请联系系统管理员!");
			}
			
		}else{
			return channelChildId+"001";
		}
	}
	
	private ChannelChild copyChannelProp(Channel channel){
		ChannelChild child = new ChannelChild();
		child.setAddress(channel.getAddress());
		child.setBankAccount(channel.getBankAccount());
		child.setBankAccountName(channel.getBankAccountName());
		child.setBankName(channel.getBankName());
		child.setChName(channel.getChName());
		child.setCity(channel.getCity());
		child.setContactEmail(channel.getContactEmail());
		child.setContactJob(channel.getContactJob());
		child.setContactMobile(channel.getContactMobile());
		child.setContactName(channel.getContactName());
		child.setContactPhone(channel.getContactPhone());
		child.setCorporate(channel.getCorporate());
		child.setCredit(channel.getCredit());
		child.setFax(channel.getFax());
		child.setIntro(channel.getIntro());
		child.setPhone(channel.getPhone());
		child.setPostcode(channel.getPostcode());
		child.setRegisteredCapita(channel.getRegisteredCapita());
		child.setUrl(channel.getUrl());
		child.setModifyTime(channel.getModifyTime());
		child.setMotifierId(channel.getMotifierId());
		child.setCreateorId(channel.getCreateorId());
		child.setCreateTime(channel.getCreateTime());
		child.setProportion("10%");
		child.setProportionTime(channel.getProportionTime());
		return child;
	}

	public void addProvider(Provider provider) throws Exception {
		if(ParameterCheck.isNullOrEmpty(provider.getProviderId()) || controller.isUnique(Provider.class, provider, "providerId"))
		{
			Calendar today = Calendar.getInstance();
			today.setTime(new Date());
			today.add(Calendar.MONTH, 1);
			provider.setProportionTime(MONTH_FORMAT.format(today.getTime()));
			provider.setId(getNewProviderId());
			controller.save(provider);
			addCpProvider(provider);
		}else{
			throw new Exception("该SP/CP已经存在!");
		}
		
		
	}
	
	private void addCpProvider(Provider provider) throws Exception {
		Channel channel = new Channel();
		channel.setId(String.valueOf(provider.getId()));
		channel.setAddress(provider.getAddress());
		channel.setBankAccount(provider.getBankAccount());
		channel.setBankName(provider.getBankName());
		channel.setBankAccountName(provider.getBankAccountName());
		channel.setChannelTypeId(6);
		channel.setChName(provider.getChName());
		channel.setCity(provider.getCity());
		channel.setContactEmail(provider.getContactEmail());
		channel.setContactJob(provider.getContactJob());
		channel.setContactMobile(provider.getContactMobile());
		channel.setContactName(provider.getContactName());
		channel.setContactPhone(provider.getContactPhone());
		channel.setCorporate(provider.getCorporate());
		channel.setCredit(provider.getCredit());
		channel.setCreateorId(provider.getCreateorId());
		channel.setCreateTime(provider.getCreateTime());
		channel.setFax(provider.getFax());
		channel.setIntro(provider.getIntro());
		channel.setModifyTime(provider.getModifyTime());
		channel.setMotifierId(provider.getMotifierId());
		channel.setPhone(provider.getPhone());
		channel.setPostcode(provider.getPostcode());
		channel.setProportion(provider.getProportion());
		channel.setProportionTime(provider.getProportionTime());
		channel.setRegisteredCapita(provider.getRegisteredCapita());
		channel.setUrl(provider.getUrl());
		addChannel(channel);
		
	}
	
	private int getNewProviderId()throws Exception{
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		List<Provider> providers = controller.findBy(Provider.class,1,1,"id",false,hibernateExpressions);
		int newId = 5000;
		if(providers.size() > 0){
			Provider provider = providers.get(0);
			if(provider.getId() >= 5000){
				newId = provider.getId() +1;
			}
		}
		return newId;
	
	}

	public void deleteProvider(Provider provider) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public List<Provider> findProvider(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		List<Provider> providers = controller.findBy(Provider.class,pageNo,pageSize,"id",true,expressions);
		return providers;
	}

	public long findProviderResultCount(
			Collection<HibernateExpression> expressions) {
		return controller.getResultCount(Provider.class, expressions);
	}

	public Provider getProvider(Integer providerId) {
		return controller.get(Provider.class, providerId);
	}

	public void updateProvider(Provider provider) throws Exception {
		if(ParameterCheck.isNullOrEmpty(provider.getProviderId()) || controller.isUnique(Provider.class, provider, "providerId"))
		{
			controller.update(provider);
		}else{
			throw new Exception("该SP/CP已经存在!");
		}
	}

	public void auditProvider(Provider provider, Integer status) throws Exception{
		if(!provider.getStatus().equals(status)){
			if(status == 3){
				if(provider.getProviderId().length() !=8){
					throw new Exception("SPID/CPID代码必须是8位");
				}
			}
			provider.setStatus(status);
			controller.update(provider);
		}
		
	}

}
