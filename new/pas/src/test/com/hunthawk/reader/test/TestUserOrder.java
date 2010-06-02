/**
 * 
 */
package com.hunthawk.reader.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.custom.BookBag;
import com.hunthawk.reader.domain.custom.OrderRelationUpdateNotifyReq;
import com.hunthawk.reader.domain.custom.UserBuy;
import com.hunthawk.reader.domain.custom.UserBuyMonth;
import com.hunthawk.reader.domain.custom.UserBuyMonthChoice;
import com.hunthawk.reader.domain.custom.UserOrderList;
import com.hunthawk.reader.domain.partner.Fee;

/**
 * @author BruceSun
 *
 */
public class TestUserOrder {
	private static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMddhhmmss");
	private static SimpleDateFormat FORMAT1 = new SimpleDateFormat("MMddHHmmss");
	
	public static void main(String[] args){
		XmlBeanFactory ctx = new XmlBeanFactory(new ClassPathResource("com/hunthawk/reader/test/test.xml"));
		HibernateGenericController c = (HibernateGenericController)ctx.getBean("hibernateGenericController");
		
		
		
		addOnce(c,0,40,0,100,"311001000001","100010000","10001000");
//		
//		addOnce(c,51,100,50,150,"321002099001","100010001","11500001");
		
//		addMonth(c,140,160,"211001000001","100010004","10001000");
//		
//		addMonthChoice(c,101,103,0,3,"211001000001","100010004","10001000");
//		
//		addVip(c,50,150,"211001000002","100010004","10001000");
		
	}
	
	private static void addOnce(HibernateGenericController c ,int rs,int re,int ms,int me,String feeId,String pid,String channelId){
		int seq = 1;
		for(int i=rs;i<re;i++){
			String cid = "1000"+StringUtils.leftPad(String.valueOf(i), 4,"0");
			Fee fee = c.get(Fee.class, feeId);
			for(int m=ms;m<me;m++){
				String mobile = "1311111"+StringUtils.leftPad(String.valueOf(m), 4,"0");
				
				OrderRelationUpdateNotifyReq req = new OrderRelationUpdateNotifyReq();
				req.setRecordSequenceID(StringUtils.leftPad(String.valueOf(seq++), 18,"0"));
				req.setUserIdType(1);
				req.setUserId(mobile);
				req.setServiceType("01");
				req.setSpId(fee.getProvider().getProviderId());
				req.setProductId(fee.getProductId());
				req.setUpdateType(3);
				req.setUpdateTime(FORMAT.format(new Date()));
				req.setContent("content");
				req.setEffectiveDate(FORMAT.format(new Date()));
				req.setExpireDate(FORMAT.format(new Date()));
				System.out.println(FORMAT1.format(new Date()));
				req.setTimeStamp(FORMAT1.format(new Date()));
				req.setEncodeStr("EncodeStr");
				c.save(req);
				
				
				UserOrderList list = new UserOrderList();
				
				list.setChannelId(channelId);
				list.setContentId(cid);
				list.setCreateTime(new Date());
				list.setFeeId(feeId);
				list.setMobile(mobile);
				list.setOrderType(3);
				list.setPid(pid);
				list.setProductID(fee.getProductId());
				list.setServiceId(fee.getServiceId());
				list.setPackId(50);
				list.setFeeType(1);
				list.setSpid(fee.getProvider().getProviderId());
				
				c.save(list);
				
				UserBuy buy = new UserBuy();
				
				buy.setChannelId(list.getChannelId());
				buy.setContentId(list.getContentId());
				buy.setCreateTime(list.getCreateTime());
				buy.setFeeId(list.getFeeId());
				buy.setMobile(list.getMobile());
				buy.setPid(list.getPid());
				c.save(buy);
				
				BookBag bag = new BookBag();
				bag.setChannelId(buy.getChannelId());
				bag.setContentId(buy.getContentId());
				bag.setCreateTime(buy.getCreateTime());
				bag.setFeeId(buy.getFeeId());
				bag.setMobile(buy.getMobile());
				bag.setPid(buy.getPid());
				
				c.save(bag);
			}
		}
	}
	
	
	private static void addVip(HibernateGenericController c ,int ms,int me,String feeId,String pid,String channelId){
		int seq = 1;
		Fee fee = c.get(Fee.class, feeId);
		for(int m=ms;m<me;m++){
			String mobile = "1311111"+StringUtils.leftPad(String.valueOf(m), 4,"0");
			
			OrderRelationUpdateNotifyReq req = new OrderRelationUpdateNotifyReq();
			req.setRecordSequenceID(StringUtils.leftPad(String.valueOf(seq++), 18,"0"));
			req.setUserIdType(1);
			req.setUserId(mobile);
			req.setServiceType("01");
			req.setSpId(fee.getProvider().getProviderId());
			req.setProductId(fee.getProductId());
			req.setUpdateType(1);
			req.setUpdateTime(FORMAT.format(new Date()));
			req.setContent("content");
			req.setEffectiveDate(FORMAT.format(new Date()));
			req.setExpireDate(FORMAT.format(new Date()));
			System.out.println(FORMAT1.format(new Date()));
			req.setTimeStamp(FORMAT1.format(new Date()));
			req.setEncodeStr("EncodeStr");
			c.save(req);
			
			
			UserOrderList list = new UserOrderList();
			
			list.setChannelId(channelId);
			
			list.setCreateTime(new Date());
			list.setFeeId(feeId);
			list.setMobile(mobile);
			list.setOrderType(Constants.ORDER_TYPE_MONTH);
			list.setPid(pid);
			list.setProductID(fee.getProductId());
			list.setServiceId(fee.getServiceId());
			list.setPackId(1050);
			list.setFeeType(Constants.FEE_TYPE_VIP);
			list.setSpid(fee.getProvider().getProviderId());
			
			c.save(list);
			
			UserBuyMonth month = new UserBuyMonth();
			month.setChannelId(list.getChannelId());
			month.setCreateTime(list.getCreateTime());
			month.setFeeId(list.getFeeId());
			month.setFeeType(Constants.FEE_TYPE_VIP);
			month.setMobile(list.getMobile());
			month.setPid(list.getPid());
			month.setPackId(1050);
    		c.save(month);
			
			Fee fee1 = c.get(Fee.class, "311001000001");
			for(int i=1;i<3;i++){
				String cid = "1000"+StringUtils.leftPad(String.valueOf(i), 4,"0");
				
				
				OrderRelationUpdateNotifyReq req1 = new OrderRelationUpdateNotifyReq();
				req1.setRecordSequenceID(StringUtils.leftPad(String.valueOf(seq++), 18,"0"));
				req1.setUserIdType(1);
				req1.setUserId(mobile);
				req1.setServiceType("01");
				req1.setSpId(fee1.getProvider().getProviderId());
				req1.setProductId(fee1.getProductId());
				req1.setUpdateType(3);
				req1.setUpdateTime(FORMAT.format(new Date()));
				req1.setContent("content");
				req1.setEffectiveDate(FORMAT.format(new Date()));
				req1.setExpireDate(FORMAT.format(new Date()));
//				System.out.println(FORMAT1.format(new Date()));
				req1.setTimeStamp(FORMAT1.format(new Date()));
				req1.setEncodeStr("EncodeStr");
				c.save(req1);
				
				UserOrderList order = new UserOrderList();
				
				order.setChannelId(channelId);
				order.setContentId(cid);
				order.setCreateTime(new Date());
				order.setFeeId("311001000001");
				order.setMobile(mobile);
				order.setOrderType(Constants.ORDER_TYPE_VIEW);
				order.setPid(pid);
				order.setProductID(fee1.getProductId());
				order.setServiceId(fee1.getServiceId());
				order.setPackId(1050);
				order.setFeeType(Constants.FEE_TYPE_VIP);
				order.setSpid(fee1.getProvider().getProviderId());
				
				c.save(order);
				
				UserBuy buy = new UserBuy();
				
				buy.setChannelId(order.getChannelId());
				buy.setContentId(order.getContentId());
				buy.setCreateTime(order.getCreateTime());
				buy.setFeeId(order.getFeeId());
				buy.setMobile(order.getMobile());
				buy.setPid(order.getPid());
				c.save(buy);
				
				BookBag bag = new BookBag();
				bag.setChannelId(buy.getChannelId());
				bag.setContentId(buy.getContentId());
				bag.setCreateTime(buy.getCreateTime());
				bag.setFeeId(buy.getFeeId());
				bag.setMobile(buy.getMobile());
				bag.setPid(buy.getPid());
				
				c.save(bag);
			}
			
		}
			
		
	}
	
	public static void addMonth(HibernateGenericController c,int ms,int me,String feeId,String pid,String channelId){
		Fee fee = c.get(Fee.class, feeId);
		for(int m=ms;m<me;m++){
			String mobile = "1311111"+StringUtils.leftPad(String.valueOf(m), 4,"0");
			UserOrderList list = new UserOrderList();
			list.setChannelId(channelId);
			list.setCreateTime(new Date());
			list.setFeeId(feeId);
			list.setMobile(mobile);
			list.setOrderType(2);
			list.setPid(pid);
		
			list.setProductID(fee.getProductId());
			list.setServiceId(fee.getServiceId());
			list.setPackId(1100);
			list.setFeeType(Constants.FEE_TYPE_NORMAL);
			list.setSpid(fee.getProvider().getProviderId());
			
			c.save(list);
			
			UserBuyMonth month = new UserBuyMonth();
			month.setChannelId(list.getChannelId());
			month.setCreateTime(list.getCreateTime());
			month.setFeeId(list.getFeeId());
			month.setFeeType(Constants.FEE_TYPE_NORMAL);
			month.setMobile(list.getMobile());
			month.setPid(list.getPid());
			month.setPackId(1100);
			c.save(month);
		}
	}
	
	public static void addMonthChoice(HibernateGenericController c,int rs,int re,int ms,int me,String feeId,String pid,String channelId){
		Fee fee = c.get(Fee.class, feeId);
		int seq = 1;
		for(int m=ms;m<me;m++){
			String mobile = "1311111"+StringUtils.leftPad(String.valueOf(m), 4,"0");
			
			OrderRelationUpdateNotifyReq req = new OrderRelationUpdateNotifyReq();
			req.setRecordSequenceID(StringUtils.leftPad(String.valueOf(seq++), 18,"0"));
			req.setUserIdType(1);
			req.setUserId(mobile);
			req.setServiceType("01");
			req.setSpId(fee.getProvider().getProviderId());
			req.setProductId(fee.getProductId());
			req.setUpdateType(1);
			req.setUpdateTime(FORMAT.format(new Date()));
			req.setContent("content");
			req.setEffectiveDate(FORMAT.format(new Date()));
			req.setExpireDate(FORMAT.format(new Date()));
			
			req.setTimeStamp(FORMAT1.format(new Date()));
			req.setEncodeStr("EncodeStr");
			c.save(req);
			
			
			UserOrderList list = new UserOrderList();
			list.setChannelId(channelId);
			list.setCreateTime(new Date());
			list.setFeeId(feeId);
			list.setMobile(mobile);
			list.setOrderType(Constants.ORDER_TYPE_MONTH);
			list.setPid(pid);
			list.setProductID(fee.getProductId());
			list.setServiceId(fee.getServiceId());
			list.setPackId(1051);
			list.setFeeType(Constants.FEE_TYPE_CHOICE);
			list.setSpid(fee.getProvider().getProviderId());
			c.save(list);
			
			UserBuyMonth month = new UserBuyMonth();
			month.setChannelId(list.getChannelId());
			month.setCreateTime(list.getCreateTime());
			month.setFeeId(list.getFeeId());
			month.setFeeType(Constants.FEE_TYPE_CHOICE);
			month.setMobile(list.getMobile());
			month.setPid(list.getPid());
			month.setPackId(1051);
			c.save(month);
			for(int i=rs;i<re;i++){
				String cid = "1000"+StringUtils.leftPad(String.valueOf(i), 4,"0");
				UserBuyMonthChoice ch = new UserBuyMonthChoice();
				ch.setContentId(cid);
				ch.setCreateTime(list.getCreateTime());
				ch.setFeeId(list.getFeeId());
				ch.setMobile(list.getMobile());
				c.save(ch);
				
				UserOrderList order = new UserOrderList();
				order.setChannelId(channelId);
				order.setContentId(cid);
				order.setCreateTime(new Date());
				order.setFeeId(feeId);
				order.setMobile(mobile);
				order.setOrderType(Constants.ORDER_TYPE_FREE);
				order.setPid(pid);
				order.setProductID(fee.getProductId());
				order.setServiceId(fee.getServiceId());
				order.setPackId(1051);
				order.setFeeType(Constants.FEE_TYPE_CHOICE);
				order.setSpid(fee.getProvider().getProviderId());
				c.save(order);
			}
			
		}
	}
}
