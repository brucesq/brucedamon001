/**
 * 
 */
package com.hunthawk.reader.pps.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.domain.custom.BookBag;
import com.hunthawk.reader.domain.custom.FeeBackMessage;
import com.hunthawk.reader.domain.custom.UserBuyMonth;
import com.hunthawk.reader.domain.custom.UserMonthUnicomBackMsg;
import com.hunthawk.reader.domain.custom.UserOrderBackMessage;
import com.hunthawk.reader.domain.custom.UserOrderList;
import com.hunthawk.reader.domain.custom.UserOrderRecordMessage;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.ArrayUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.FeeMsgService;
import com.hunthawk.reader.pps.service.IphoneFeeParamService;
import com.hunthawk.reader.pps.service.IphoneService;
import com.hunthawk.reader.pps.service.ResourceService;

/**
 * @author BruceSun
 *
 */
public class IphoneServiceImpl implements IphoneService{

	private HibernateGenericController controller;
	
	private BussinessService bussinessService;
	private IphoneFeeParamService iphoneFeeParamService;
	
	public void setIphoneFeeParamService(IphoneFeeParamService iphoneFeeParamService) {
		this.iphoneFeeParamService = iphoneFeeParamService;
	}
	private MemCachedClientWrapper memcached;
	
	private CustomService customService;
	
	private ResourceService resourceService;
	
	private FeeMsgService feeMsgService;
	
	private static Logger logger = Logger.getLogger(IphoneServiceImpl.class);

	public void setFeeMsgService(FeeMsgService feeMsgService) {
		this.feeMsgService = feeMsgService;
	}

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}
	
	public void setBussinessService(BussinessService bussinessService) {
		this.bussinessService = bussinessService;
	}
	
	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}
	
	public void setCustomService(CustomService customService) {
		this.customService = customService;
	}

	public boolean isUserBuyBook(String mobile,String productId,Integer packId,String resourceId){
		if(mobile.equals("10000000000")){
			return false;
		}
		/**
		 * 增加资源类型过滤
		 * modify by liuxh 09-11-16
		 */
		/**频道包月*/
		String [] channelFeeIds=iphoneFeeParamService.getIphoneFeeIds(resourceId, 1);
		for(String feeId:channelFeeIds){
			if(customService.isOrderMonth(mobile, feeId))
				return true;
		}
//		String channelFeeId =  bussinessService.getVariables("iphone_fee_channel").getValue();
//		String channelDisFeeId =  bussinessService.getVariables("iphone_fee_channel_dis").getValue();
//		if(customService.isOrderMonth(mobile, channelFeeId))
//			return true;
//		if(customService.isOrderMonth(mobile, channelDisFeeId))
//			return true;
		/**栏目包月*/
		String [] columnFeeids=iphoneFeeParamService.getIphoneFeeIds(resourceId, 0);
		for(String feeId:columnFeeids){
			if(customService.isOrderMonth(mobile, feeId,packId))
				return true;
		}
//		String columnFeeId =  bussinessService.getVariables("iphone_fee_column").getValue();
//		String columnDisFeeId =  bussinessService.getVariables("iphone_fee_column_dis").getValue();
//		if(customService.isOrderMonth(mobile, columnFeeId,packId))
//			return true;
//		if(customService.isOrderMonth(mobile, columnDisFeeId,packId))
//			return true;
		
		/**按次*/
		if(customService.isUserBuyBook(mobile, resourceId))
			return true;	
		return false;
	}
	
	/**
	 * 增加对资源类型的判断		不同类型的资源获取不同的计费ID
	 * modify by liuxh 09-11-16
	 */
	public boolean isUserBuy(String mobile,String productId,Integer packId,String resourceId,String relId){
		System.out.println("mobile="+mobile+" productId="+productId+" packId="+packId+" resourceId="+resourceId+" relId="+relId);
		if(mobile.equals("10000000000")){
			return false;
		}
		ResourcePackReleation rpr=resourceService.getResourcePackReleation(Integer.parseInt(relId));
		if(rpr==null)
			return true;
		String pro_no=getIphoneProductNo(resourceId,packId.toString(),relId);//
		
			/**频道包月*/
		String[] channelFeeIds=iphoneFeeParamService.getIphoneFeeIds(resourceId,1);
		for(String channelFeeId:channelFeeIds){
			if(customService.isOrderMonth(mobile, channelFeeId)){
				return true;
			}else{
				String prono_channel=pro_no.substring(0,8);
				UserOrderBackMessage msg_channel= searchOrder(mobile,prono_channel,"1","","");
				if(msg_channel.getRecordList().size()>0){
					for(Iterator it=msg_channel.getRecordList().iterator();it.hasNext();){
						UserOrderRecordMessage uorm=(UserOrderRecordMessage)it.next();
						if(uorm.getPro_no().equals(prono_channel) && uorm.getState().equals("1")){
							//记录频道包月信息
							packId=Integer.parseInt(iphoneFeeParamService.getIphoneChannelPackId(resourceId));
//							packId=Integer.parseInt(bussinessService.getVariables("iphone_channel_packs").getValue());
							String msg=orderMonth(channelFeeId,packId,mobile,productId);
							if(!"".equals(msg))
								logger.info("记录频道包月信息失败："+msg);
							return true;
						}
					}
				}
			}
		}
		
		String[] packids=iphoneFeeParamService.getIphoneMonthPackIds(resourceId);
		if(packids.length>0){
			/**判断是否在栏目包月批价包中*/
			if(ArrayUtil.isHave(String.valueOf(packId),packids)){//在iphone包月批价包 中
				/**栏目包月*/
				String[] columnFeeIds=iphoneFeeParamService.getIphoneFeeIds(resourceId,2);
				for(String columnFeeId:columnFeeIds){
					if(customService.isOrderMonth(mobile, columnFeeId,packId)){
						return true;
					}else{
						String prono_column=pro_no.substring(0,16);
						UserOrderBackMessage msg_column= searchOrder(mobile,prono_column,"1","","");
						if(msg_column.getRecordList().size()>0){
							for(Iterator it=msg_column.getRecordList().iterator();it.hasNext();){
								UserOrderRecordMessage uorm=(UserOrderRecordMessage)it.next();
								if(uorm.getPro_no().equals(prono_column) && uorm.getState().equals("1")){
									//记录栏目包月信息
									String msg=orderMonth(columnFeeId,packId,mobile,productId);
									if(!"".equals(msg))
										logger.info("记录栏目包月信息失败："+msg);
									return true;
								}
							}
						}
					}
				}
			}
		}
		
//			String channelFeeId =  bussinessService.getVariables("iphone_fee_channel").getValue();
//			String channelDisFeeId =  bussinessService.getVariables("iphone_fee_channel_dis").getValue();
//			if(customService.isOrderMonth(mobile, channelFeeId)){
//				return true;
//			}else{
//				String prono_channel=pro_no.substring(0,8);
//				UserOrderBackMessage msg_channel= searchOrder(mobile,prono_channel,"1","","");
//				if(msg_channel.getRecordList().size()>0){
//					for(Iterator it=msg_channel.getRecordList().iterator();it.hasNext();){
//						UserOrderRecordMessage uorm=(UserOrderRecordMessage)it.next();
//						if(uorm.getPro_no().equals(prono_channel) && uorm.getState().equals("1")){
//							//记录频道包月信息
//							packId=Integer.parseInt(bussinessService.getVariables("iphone_channel_packs").getValue());
//							String msg=orderMonth(channelFeeId,packId,mobile,productId);
//							if(!"".equals(msg))
//								logger.info("记录频道包月信息失败："+msg);
//							return true;
//						}
//					}
//				}
//			}
			
//			/**判断是否在栏目包月批价包中*/
//			String[] packids = bussinessService.getVariables("iphone_month_packs").getValue().split(";");
//			if(ArrayUtil.isHave(String.valueOf(packId),packids)){//在iphone包月批价包 中
//				/**栏目包月*/
//				String columnFeeId =  bussinessService.getVariables("iphone_fee_column").getValue();
//				String columnDisFeeId =  bussinessService.getVariables("iphone_fee_column_dis").getValue();
//				if(customService.isOrderMonth(mobile, columnFeeId,packId)){
//					return true;
//				}else{
//					String prono_column=pro_no.substring(0,16);
//					UserOrderBackMessage msg_column= searchOrder(mobile,prono_column,"1","","");
//					if(msg_column.getRecordList().size()>0){
//						for(Iterator it=msg_column.getRecordList().iterator();it.hasNext();){
//							UserOrderRecordMessage uorm=(UserOrderRecordMessage)it.next();
//							if(uorm.getPro_no().equals(prono_column) && uorm.getState().equals("1")){
//								//记录栏目包月信息
//								String msg=orderMonth(columnFeeId,packId,mobile,productId);
//								if(!"".equals(msg))
//									logger.info("记录栏目包月信息失败："+msg);
//								return true;
//							}
//						}
//					}
//				}
//		}
		
		/**按次计费*/
		if(customService.isUserBuyBook(mobile, resourceId)){
			return true;
		}else{
			UserOrderBackMessage msg_resource= searchOrder(mobile,pro_no,"1","","");
			if(msg_resource.getRecordList().size()>0){
				for(Iterator it=msg_resource.getRecordList().iterator();it.hasNext();){
					UserOrderRecordMessage uorm=(UserOrderRecordMessage)it.next();
					if(uorm.getPro_no().equals(pro_no)){
						//记录按次信息
						String msg=orderOne(rpr,mobile,resourceId,productId);
						if(!"".equals(msg))
							logger.info("记录按次信息失败："+msg);
						return true;
					}
				}
			}
		}
		return false;
	}
	public boolean isIphoneProduct(String productId){
//		String[] products = bussinessService.getVariables("iphone_products").getValue().split(";");
//		for(String pd : products){
//			if(pd.equals(productId)){
//				return true;
//			}
//		}
		return false;
	}
	
	public boolean isPackFeeMonth(Integer packId){
//		String[] packs = bussinessService.getVariables("iphone_month_packs").getValue().split(";");
//		for(String id : packs){
//			if(id.equals(packId.toString())){
//				return true;
//			}
//		}
		return false;
	}
	
	public boolean isVipUser(String mobile){
//		String vipFeeId =  bussinessService.getVariables("iphone_fee_vip").getValue();
//		if(customService.isOrderMonth(mobile, vipFeeId))
//			return true;
		return false;
	}
	
	public String orderChannel(String mobile,String pid,Integer packId,Fee fee,String viewUrl){
		//产品退订地址:?feeType=1&pid=25000000&mobile=&packID=
		// 栏目退订地址:?feeType=2&feeId=&packID=&mobile=&colid=&pid=
		// VIP退订地址:?feeType=3&feeId=&mobile=&pid=&pacdID=
		String recordsn=getRecordsn();
		Double feeMoney = Double.parseDouble(fee.getCode());
		feeMoney = feeMoney*100;
		Product product = bussinessService.getProduct(pid);
		String  cancelUrl = getCancelUrl()+"?feeType=1&pid="+pid+"&packID="+packId+"&mobile="+new String(Base64.encodeBase64(mobile.getBytes()));
//		Date date = new Date();
//		date = DateUtils.addMonths(date, 1);
		FeeBackMessage message = feeMsgService.sendFeeMessage("ACIP02001", mobile, "", getSpid(),recordsn, "频道包月", 2, feeMoney.intValue(), "十分科技", "手机书城", pid, product.getName(), viewUrl, cancelUrl, getExpirationDate());
		if(message.getFlag().equals("00000")){
			String channelId = bussinessService.getDefaultChannelId(pid);
			UserOrderList list = new UserOrderList();
			list.setCreateTime(new Date());
			list.setChannelId(channelId);
			list.setFeeId(fee.getId());
			list.setOrderType(fee.getType());
			list.setMobile(mobile);
			list.setPackId(packId);
			list.setPid(pid);
			list.setProductID(fee.getProductId());
			list.setServiceId(fee.getServiceId());
			list.setSpid(fee.getProvider().getProviderId());
			list.setFeeType(Constants.FEE_TYPE_NORMAL);
			customService.addUserOrderList(list);
			UserBuyMonth month = new UserBuyMonth();
			month.setChannelId(list.getChannelId());
			month.setCreateTime(list.getCreateTime());
			month.setFeeId(list.getFeeId());
			month.setPackId(packId);
			month.setFeeType(list.getFeeType());
			month.setMobile(list.getMobile());
			month.setPid(list.getPid());
			customService.addUserBuyMonth(month);
			//添加记录 联通返回sequence_id
			UserMonthUnicomBackMsg umub=new UserMonthUnicomBackMsg();
			umub.setMobile(mobile);
			umub.setColumnId(-1);
			umub.setFeeId(fee.getId());
			umub.setOrderType(1);
			umub.setPackId(packId);
			umub.setPid(pid);
			umub.setSequenceId(message.getSequence_id());
			umub.setRecordsn(recordsn);
			umub.setCreateTime(new Date());
			umub.setStatus(1);
			umub.setViewUrl(viewUrl);
			customService.addIphoneUnicomBackMsg(umub);
			return "";
		}else{
			return message.getDetail();
		}
		
	}
	public String orderVip(String mobile,String pid,Integer packId,Fee fee,String viewUrl){
		//产品退订地址:?feeType=1&pid=25000000&mobile=&packID=
		// 栏目退订地址:?feeType=2&feeId=&packID=&mobile=&colid=&pid=
		// VIP退订地址:?feeType=3&feeId=&mobile=&pid=&packID=
		String recordsn= getRecordsn();
		Double feeMoney = Double.parseDouble(fee.getCode());
		feeMoney = feeMoney*100;
//		Product product = bussinessService.getProduct(pid);
		String  cancelUrl = getCancelUrl()+"?feeType=3&feeId="+fee.getId()+"&pid="+pid+"&packID="+packId+"&mobile="+new String(Base64.encodeBase64(mobile.getBytes()));
//		Date date = new Date();
//		date = DateUtils.addMonths(date, 1);
		FeeBackMessage message = feeMsgService.sendFeeMessage("ACIP02001", mobile, "", getSpid(),recordsn, "VIP包月", 2, feeMoney.intValue(), "十分科技", "手机书城", "1"+pid, "手机书城VIP", viewUrl, cancelUrl, getExpirationDate());
		if(message.getFlag().equals("00000")){
			String channelId = bussinessService.getDefaultChannelId(pid);
			UserOrderList list = new UserOrderList();
			list.setCreateTime(new Date());
			list.setChannelId(channelId);
			list.setFeeId(fee.getId());
			list.setOrderType(fee.getType());
			list.setMobile(mobile);
			list.setPackId(packId);
			list.setPid(pid);
			list.setProductID(fee.getProductId());
			list.setServiceId(fee.getServiceId());
			list.setSpid(fee.getProvider().getProviderId());
			list.setFeeType(Constants.FEE_TYPE_NORMAL);
			customService.addUserOrderList(list);
			UserBuyMonth month = new UserBuyMonth();
			month.setChannelId(list.getChannelId());
			month.setCreateTime(list.getCreateTime());
			month.setFeeId(list.getFeeId());
			month.setPackId(packId);
			month.setFeeType(list.getFeeType());
			month.setMobile(list.getMobile());
			month.setPid(list.getPid());
			customService.addUserBuyMonth(month);
			//添加记录 联通返回sequence_id
			UserMonthUnicomBackMsg umub=new UserMonthUnicomBackMsg();
			umub.setMobile(mobile);
			umub.setColumnId(-1);
			umub.setFeeId(fee.getId());
			umub.setOrderType(3);
			umub.setPackId(packId);
			umub.setPid(pid);
			umub.setSequenceId(message.getSequence_id());
			umub.setRecordsn(recordsn);
			umub.setCreateTime(new Date());
			umub.setStatus(1);
			umub.setViewUrl(viewUrl);
			customService.addIphoneUnicomBackMsg(umub);
			return "";
		}else{
			return message.getDetail();
		}
	}
	
	public String orderColumn(String mobile,String pid,Integer packId,Fee fee,Integer columnId,String viewUrl){
		//产品退订地址:?feeType=1&pid=25000000&mobile=&packID=
		// 栏目退订地址:?feeType=2&feeId=&packID=&mobile=&colid=&pid=
		// VIP退订地址:?feeType=3&feeId=&mobile=&pid=&pacdID=
		String recordsn=getRecordsn();
		Double feeMoney = Double.parseDouble(fee.getCode());
		feeMoney = feeMoney*100;
		Columns column = bussinessService.getColumns(columnId);
		String  cancelUrl = getCancelUrl()+"?feeType=2&feeId="+fee.getId()+"&packID="+packId+"&colid="+columnId+"&pid="+pid+"&mobile="+new String(Base64.encodeBase64(mobile.getBytes()));
//		Date date = new Date();
//		date = DateUtils.addMonths(date, 1);
//		System.out.println(date);
		FeeBackMessage message = feeMsgService.sendFeeMessage("ACIP02001", mobile, "", getSpid(), recordsn, "栏目包月", 2, feeMoney.intValue(), "十分科技", "手机书城", columnId.toString(), column.getName(), viewUrl, cancelUrl, getExpirationDate());
		if(message.getFlag().equals("00000")){
			String channelId = bussinessService.getDefaultChannelId(pid);
			UserOrderList list = new UserOrderList();
			list.setCreateTime(new Date());
			list.setChannelId(channelId);
			list.setFeeId(fee.getId());
			list.setOrderType(fee.getType());
			list.setMobile(mobile);
			list.setPackId(packId);
			list.setPid(pid);
			list.setProductID(fee.getProductId());
			list.setServiceId(fee.getServiceId());
			list.setSpid(fee.getProvider().getProviderId());
			list.setFeeType(Constants.FEE_TYPE_NORMAL);
			customService.addUserOrderList(list);
			UserBuyMonth month = new UserBuyMonth();
			month.setChannelId(list.getChannelId());
			month.setCreateTime(list.getCreateTime());
			month.setFeeId(list.getFeeId());
			month.setFeeType(list.getFeeType());
			month.setPackId(packId);
			month.setMobile(list.getMobile());
			month.setPid(list.getPid());
			customService.addUserBuyMonth(month);
			//添加记录 联通返回sequence_id
			UserMonthUnicomBackMsg umub=new UserMonthUnicomBackMsg();
			umub.setMobile(mobile);
			umub.setColumnId(columnId);
			umub.setFeeId(fee.getId());
			umub.setOrderType(2);
			umub.setPackId(packId);
			umub.setPid(pid);
			umub.setSequenceId(message.getSequence_id());
			umub.setRecordsn(recordsn);
			umub.setCreateTime(new Date());
			umub.setStatus(1);
			umub.setViewUrl(viewUrl);
			customService.addIphoneUnicomBackMsg(umub);
			return "";
		}else{
			return message.getDetail();
		}
	}
	
	
	public String orderResource(String mobile,String pid,Integer packId,Fee fee,String resourceId,Integer relId,String viewUrl){
		Double feeMoney = Double.parseDouble(fee.getCode());
		feeMoney = feeMoney*100;
		ResourceAll resource = resourceService.getResource(resourceId);
		Date date = new Date();
		date = DateUtils.addYears(date, 1);
		String startstr="";
		if(resourceId.startsWith(ResourceAll.RESOURCE_TYPE_BOOK.toString())){
			startstr="图书";
		}else if(resourceId.startsWith(ResourceAll.RESOURCE_TYPE_COMICS.toString())){
			startstr="漫画";
		}else if(resourceId.startsWith(ResourceAll.RESOURCE_TYPE_MAGAZINE.toString())){
			startstr="杂志";
		}else if(resourceId.startsWith(ResourceAll.RESOURCE_TYPE_NEWSPAPER.toString())){
			startstr="报纸";
		}
		FeeBackMessage message = feeMsgService.sendFeeMessage("ACIP02001", mobile, "", getSpid(), getRecordsn(), "资源按次", 1, feeMoney.intValue(), "十分科技", "手机书城", resourceId, startstr+"-"+resource.getName(), viewUrl, "", date);
		if(message.getFlag().equals("00000")){
			String channelId = bussinessService.getDefaultChannelId(pid);
			UserOrderList list = new UserOrderList();
			list.setCreateTime(new Date());
			list.setChannelId(channelId);
			list.setFeeId(fee.getId());
			list.setOrderType(fee.getType());
			list.setMobile(mobile);
			list.setPackId(packId);
			list.setPid(pid);
			list.setProductID(fee.getProductId());
			list.setServiceId(fee.getServiceId());
			list.setSpid(fee.getProvider().getProviderId());
			list.setFeeType(Constants.FEE_TYPE_VIEW);
			list.setContentId(resourceId);
			customService.addUserOrderList(list);
			//加入书包
			BookBag bag = new BookBag();
			bag.setChannelId(list.getChannelId());
			bag.setContentId(list.getContentId());
			bag.setCreateTime(list.getCreateTime());
			bag.setFeeId(list.getFeeId());
			bag.setMobile(list.getMobile());
			bag.setPid(list.getPid());
			try {
				customService.addBookbag(bag);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "";
		}else{
			return message.getDetail();
		}
	}
	public UserOrderBackMessage searchOrder(String mobile,String pro_no,String state,String begin_date,String end_date){
		return  feeMsgService.userOrderMessage("ACIP01002", mobile, getSpid(),getRecordsn(), pro_no, state, begin_date, end_date);
	}
	/**iphone用户包月订购信息*/
	private  List<UserBuyMonth> getUserBuyMonths() {
		// TODO Auto-generated method stub
		/**
		 * 包月表里 feeid 为以下 标识ID的时候 为IPHONE 包月信息 iphone_fee_column_dis
		 * IPHONE栏目包月折扣计费ID iphone_fee_channel_dis IPHONE频道包月折扣计费ID
		 * iphone_fee_vip IPHONE包月VIP计费ID iphone_fee_column IPHONE栏目包月计费ID
		 * iphone_fee_channel IPHONE频道包月计费ID
		 */
		StringBuilder ids = new StringBuilder();
		// 获取产品包月 feeid系统变量
		String iphone_fee_channel = bussinessService.getVariables(
				"iphone_fee_channel").getValue();
		String iphone_fee_channel_dis = bussinessService.getVariables(
				"iphone_fee_channel_dis").getValue();
		// 获取栏目包月计费ID
		String iphone_fee_column = bussinessService.getVariables(
				"iphone_fee_column").getValue();
		String iphone_fee_column_dis = bussinessService.getVariables(
				"iphone_fee_column_dis").getValue();
		String iphone_fee_vip = bussinessService.getVariables("iphone_fee_vip")
				.getValue();
		ids.append(iphone_fee_channel).append(",").append(iphone_fee_channel_dis).append(",").append(iphone_fee_column).append(",").append(iphone_fee_column_dis).append(",").append(iphone_fee_vip);
		String hql = "select ubm from UserBuyMonth ubm where ubm.feeId in ("
				+ ids.toString() + ")";
		List<UserBuyMonth> ubms = controller.findBy(hql);
		return ubms;
	}
	public String orderMonth(String feeId,Integer packId,String mobile,String pid) {
		try{
			Fee fee=customService.getFee(feeId);
			String channelId = bussinessService.getDefaultChannelId(pid);
			UserOrderList list = new UserOrderList();
			list.setCreateTime(new Date());
			list.setChannelId(channelId);
			list.setFeeId(fee.getId());
			list.setOrderType(fee.getType());
			list.setMobile(mobile);
			list.setPackId(packId);
			list.setPid(pid);
			list.setProductID(fee.getProductId());
			list.setServiceId(fee.getServiceId());
			list.setSpid(fee.getProvider().getProviderId());
			list.setFeeType(Constants.FEE_TYPE_NORMAL);
			customService.addUserOrderList(list);
			UserBuyMonth month = new UserBuyMonth();
			month.setChannelId(list.getChannelId());
			month.setCreateTime(list.getCreateTime());
			month.setFeeId(list.getFeeId());
			month.setPackId(packId);
			month.setFeeType(list.getFeeType());
			month.setMobile(list.getMobile());
			month.setPid(list.getPid());
			customService.addUserBuyMonth(month);
		}catch(Exception ex){
			ex.printStackTrace();
			return ex.getMessage();
		}
		return "";
	}

	public String orderOne(ResourcePackReleation rpr,String mobile,String resourceId,String pid){
		Fee fee=customService.getFee(rpr.getFeeId());
		String channelId = bussinessService.getDefaultChannelId(pid);
		UserOrderList list = new UserOrderList();
		list.setCreateTime(new Date());
		list.setChannelId(channelId);
		list.setFeeId(fee.getId());
		list.setOrderType(fee.getType());
		list.setMobile(mobile);
		list.setPackId(rpr.getPack().getId());
		list.setPid(pid);
		list.setProductID(fee.getProductId());
		list.setServiceId(fee.getServiceId());
		list.setSpid(fee.getProvider().getProviderId());
		list.setFeeType(Constants.FEE_TYPE_VIEW);
		list.setContentId(resourceId);
		customService.addUserOrderList(list);
		//加入书包
		BookBag bag = new BookBag();
		bag.setChannelId(list.getChannelId());
		bag.setContentId(list.getContentId());
		bag.setCreateTime(list.getCreateTime());
		bag.setFeeId(list.getFeeId());
		bag.setMobile(list.getMobile());
		bag.setPid(list.getPid());
		try {
			customService.addBookbag(bag);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
		return "";
	}
	public Fee getResourceFee(String mobile,Fee fee){
		if(isVipUser(mobile)){
			String code = fee.getCode();
			String feeId = bussinessService.getVariables("iphone_fee_dis_"+code).getValue();
			return customService.getFee(feeId);
		}
		return fee;
	}
	public Fee getChannel(String mobile){
		if(isVipUser(mobile)){
			String feeId = bussinessService.getVariables("iphone_fee_channel_dis").getValue();
			return customService.getFee(feeId);
		}else{
			String feeId = bussinessService.getVariables("iphone_fee_channel").getValue();
			return customService.getFee(feeId);
		}
	}
	public Fee getColumnFee(String mobile,Integer packId){
		if(isPackFeeMonth(packId)){
			if(isVipUser(mobile)){
				String feeId = bussinessService.getVariables("iphone_fee_column_dis").getValue();
				return customService.getFee(feeId);
			}else{
				String feeId = bussinessService.getVariables("iphone_fee_column").getValue();
				return customService.getFee(feeId);
			}
		}
		return null;
	}
	
//	private String getServiceCode(String type){
//		String code = "ACIP"+type;
//		long num = 0;
//		try{
//			num = memcached.incr(code);
//		}catch(Exception e){
//			logger.error("从Memcached中增加IPHONE Service Code序号信息出错!", e);
//		}
//		if(num <=0 || num > 999){
//			num = 1;
//			memcached.storeCounter(code, 1);
//		}
//		
//		return code+StringUtils.leftPad(String.valueOf(num), 3, "0");
//	}
	
	private String getSpid(){
		return bussinessService.getVariables("iphone_spid").getValue();
	}
	private String getRecordsn(){
		String code = "iphonefeerecordsn";
		long num = 0;
		try{
			num = memcached.incr(code);
		}catch(Exception e){
			logger.error("从Memcached中增加IPHONE 流水号信息出错!", e);
		}
		if(num <=0 || num > 999999999999L){
			num = 1;
			memcached.storeCounter(code, 1);
		}
		return StringUtils.leftPad(String.valueOf(num), 12, "0");
	}
	public String getIphoneProductNo(String rid,String packId,String relId){
		StringBuilder pno=new StringBuilder();
		pno.append(getSpid());
		if(rid.startsWith(ResourceType.TYPE_BOOK.toString())){
			pno.append("0001");
		}else if(rid.startsWith(ResourceType.TYPE_MAGAZINE.toString())){
			pno.append("0003");
		}else if(rid.startsWith(ResourceType.TYPE_NEWSPAPERS.toString())){
			pno.append("0002");
		}else if(rid.startsWith(ResourceType.TYPE_COMICS.toString())){
			pno.append("0004");
		}
		pno.append("1");
		pno.append(StringUtils.leftPad(packId,7,"0"));
		if(rid.startsWith(ResourceType.TYPE_BOOK.toString())){
			pno.append("1");
		}else if(rid.startsWith(ResourceType.TYPE_MAGAZINE.toString())){
			pno.append("3");
		}else if(rid.startsWith(ResourceType.TYPE_NEWSPAPERS.toString())){
			pno.append("2");
		}else if(rid.startsWith(ResourceType.TYPE_COMICS.toString())){
			pno.append("4");
		}
		pno.append(StringUtils.leftPad(relId,7,"0"));
		return pno.toString();
	}
	private String getCancelUrl(){
		return bussinessService.getVariables("iphone_cancel_url").getValue();
	}
	private Date getExpirationDate(){
		java.util.Calendar calendar = java.util.Calendar.getInstance();   
    	calendar.setTime(new java.util.Date());   
    	String date= calendar.get(calendar.YEAR)+"-"+(calendar.get(calendar.MONTH) + 2) + "-" +"1"+" 01:01";
//    	   + calendar.get(calendar.HOUR_OF_DAY) + ":"  
//    	   + (calendar.get(calendar.MINUTE)<10?"0"+calendar.get(calendar.MINUTE):calendar.get(calendar.MINUTE));
//    	
    	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
         try {
             return sdf.parse(date);
         } catch (ParseException e) {
             return null;
         }
	}
	public static void main(String[] args){
//		String mo=new String(Base64.encodeBase64("13111002132".getBytes()));
//		System.out.println(mo);
//		System.out.println(new String(Base64.decodeBase64(mo.getBytes())));
//		long num = 1L;
//		System.out.println(StringUtils.leftPad(""+num, 12, "0"));
	}
}
