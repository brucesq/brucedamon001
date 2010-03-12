package com.hunthawk.reader.pps.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hunthawk.reader.domain.custom.FeeBackMessage;
import com.hunthawk.reader.domain.custom.OrderRecord;
import com.hunthawk.reader.domain.custom.SearchBalanceBackMessage;
import com.hunthawk.reader.domain.custom.SearchOrderBackMessage;
import com.hunthawk.reader.domain.custom.UserOrderBackMessage;
import com.hunthawk.reader.domain.custom.UserOrderRecordMessage;
import com.hunthawk.reader.pps.service.FeeMsgService;
import com.sinovatech.iphone.media.service.IphoneService;
import com.sinovatech.iphone.media.service.IphoneServiceLocator;

public class FeeMsgServiceImpl implements FeeMsgService{


	public FeeBackMessage sendFeeMessage(String service_code,String svcnum,
			String svcid,String clid,String recordsn, String resource, Integer pay_type,
			Integer pay_money, String cp_name, String pro_channel,
			String pro_no, String pro_name, String view_url, String cancel_url,
			Date date) {

		try{
/*
		<?xml version="1.0" encoding="UTF-8"?>
				<interface-result>
				<head>
					<service_code>ACIP02001</service_code>
					<svcnum>13149476324</svcnum>
					<clid>0002</clid>
					<recordsn>901662018890</recordsn>
				</head>
				<content>
					<resource>央视电影包月</resource>
					<pay_type>1</pay_type>
					<pay_money>300</pay_money>
					<cp_name>视频公司</cp_name>
					<pro_channel>央视电影</pro_channel>
					<pro_no>000001</pro_no>
					<pro_name>冰河世纪3</pro_name>
					<view_url>http://ivod.wo.com.cn/subNewsList.aspx?menu=&vid=51</view_url>
					<cancel_url></cancel_url>
					<invalidate_date>2009-10-01 23:59:59</invalidate_date>
				</content>
			</interface-result>*/
			String message="";
			message +=	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			message +=	"<interface-result>";
			message += "<head>";
			message += "<service_code>"+service_code+"</service_code>";
			message += "<svcnum>"+svcnum+"</svcnum>";
			message += "<clid>"+clid+"</clid>";
			message += "<recordsn>"+recordsn+"</recordsn>";
			message += "</head>";
			message += "<content>";
			message += "<resource>"+resource+"</resource>";
			message += "<pay_type>"+pay_type+"</pay_type>";
			message += "<pay_money>"+pay_money+"</pay_money>";
			message += "<cp_name>"+cp_name+"</cp_name>";
			message += "<pro_channel>"+pro_channel+"</pro_channel>";
			message += "<pro_no>"+pro_no+"</pro_no>";
			message += "<pro_name>"+pro_name+"</pro_name>";
			message += "<view_url>"+view_url.replaceAll("&", "&amp;")+"</view_url>";
			message += "<cancel_url>"+cancel_url.replaceAll("&", "&amp;")+"</cancel_url>";
			SimpleDateFormat dateFormate = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss");
			message += "<invalidate_date>"+dateFormate.format(date)+"</invalidate_date>";
			message += "</content>";
			message += "</interface-result>";

			System.out.println(message);
			IphoneService service = new IphoneServiceLocator();
			String backMessage = service.getIphoneServiceHttpPort().doService(message);
			System.out.println(backMessage);
			/*String backMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
					"<interface-result><head><flag>00007</flag><detail>无效手机号码" +
					"</detail><recordsn>901662018890</recordsn><service_code>ACIP02001</service_code>" +
					"<svcnum>13341338586</svcnum></head><content/></interface-result>";*/
	
			FeeBackMessage feeBack = new FeeBackMessage();
			
			feeBack.setService_code(subMiddleStr(backMessage,"<service_code>","</service_code>"));
			feeBack.setSvcnum(subMiddleStr(backMessage,"<svcnum>","</svcnum>"));
			feeBack.setRecordsn(subMiddleStr(backMessage,"<recordsn>","</recordsn>"));
			feeBack.setFlag(subMiddleStr(backMessage,"<flag>","</flag>"));
			feeBack.setDetail(subMiddleStr(backMessage,"<detail>","</detail>"));
			feeBack.setSequence_id(subMiddleStr(backMessage,"<sequence_id>","</sequence_id>"));
			return feeBack;
		}catch(Exception e){
			e.printStackTrace();
			return new FeeBackMessage();
		}
	}
	
	public SearchOrderBackMessage searchOrderMessage(String service_code, String svcnum,
			String svcid, String clid, String recordsn, String pro_no,
			String begin_date, String end_date) {
		try{
		String message="";
		message +=	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		message +=	"<interface-result>";
		message += "<head>";
		message += "<service_code>"+service_code+"</service_code>";
		message += "<svcnum>"+svcnum+"</svcnum>";
		message += "<clid>"+clid+"</clid>";
		message += "<recordsn>"+recordsn+"</recordsn>";
		message += "</head>";
		message += "<content>";
		message += "<pro_no>"+pro_no+"</pro_no>";
		message += "<begin_date>"+begin_date+"</begin_date>";
		message += "<end_date>"+end_date+"</end_date>";
		message += "</content>";
		message += "</interface-result>";
		IphoneService service = new IphoneServiceLocator();
		String backMessage = service.getIphoneServiceHttpPort().doService(message);
		SearchOrderBackMessage orderBack = new SearchOrderBackMessage();
		System.out.println(backMessage);
		orderBack.setService_code(subMiddleStr(backMessage,"<service_code>","</service_code>"));
		orderBack.setSvcnum(subMiddleStr(backMessage,"<svcnum>","</svcnum>"));
		orderBack.setRecordsn(subMiddleStr(backMessage,"<recordsn>","</recordsn>"));
		orderBack.setFlag(subMiddleStr(backMessage,"<flag>","</flag>"));
		orderBack.setDetail(subMiddleStr(backMessage,"<detail>","</detail>"));
		
		List<OrderRecord> recordList = new ArrayList<OrderRecord>();
		
		String[] contentMessage = backMessage.split("<content>");
		if(contentMessage!=null && contentMessage.length>1){ //有content内容
			String[] recordMessage = contentMessage[1].split("<record>");
			if(recordMessage!=null && recordMessage.length>0){//有record内容
				for(int i=0;i<recordMessage.length;i++){
					OrderRecord record = new OrderRecord();
					if(recordMessage[i].length()<1)
						continue;
					record.setPro_no(subMiddleStr(recordMessage[i],"<pro_no>","</pro_no>"));
					record.setSequence_id(subMiddleStr(recordMessage[i],"<sequence_id>","</sequence_id>"));
					record.setCreate_time(subMiddleStr(recordMessage[i],"<create_time>","</create_time>"));
					record.setInvalidate_time(subMiddleStr(recordMessage[i],"<invalidate_time>","</invalidate_time>"));
					record.setOrder_money(subMiddleStr(recordMessage[i],"<order_money>","</order_money>"));
					record.setState(subMiddleStr(recordMessage[i],"<state>","</state>"));
					recordList.add(record);
				}
			}
		}
		orderBack.setRecordList(recordList);
		return orderBack;		
		}catch(Exception e){
			e.printStackTrace();
			return new SearchOrderBackMessage();
		}
	}

	public SearchBalanceBackMessage searchBalanceMessage(String service_code,String svcnum,String svcid,String clid,String recordsn){
		

		try{
		String message="";
		message +=	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		message +=	"<interface-result>";
		message += "<head>";
		message += "<service_code>"+service_code+"</service_code>";
		message += "<svcnum>"+svcnum+"</svcnum>";
		message += "<clid>"+clid+"</clid>";
		message += "<recordsn>"+recordsn+"</recordsn>";
		message += "</head>";
		message += "<content/>";
		message += "</interface-result>";
		IphoneService service = new IphoneServiceLocator();
		String backMessage = service.getIphoneServiceHttpPort().doService(message);
		
		//System.out.println(backMessage);
		SearchBalanceBackMessage balance = new SearchBalanceBackMessage();
		
		balance.setService_code(subMiddleStr(backMessage,"<service_code>","</service_code>"));
		balance.setSvcnum(subMiddleStr(backMessage,"<svcnum>","</svcnum>"));
		balance.setRecordsn(subMiddleStr(backMessage,"<recordsn>","</recordsn>"));
		balance.setFlag(subMiddleStr(backMessage,"<flag>","</flag>"));
		balance.setDetail(subMiddleStr(backMessage,"<detail>","</detail>"));
		
		balance.setWo_money(subMiddleStr(backMessage,"<wo_money>","</wo_money>"));
		balance.setPl_money(subMiddleStr(backMessage,"<pl_money>","</pl_money>"));
		
		return balance;		
		}catch(Exception e){
			e.printStackTrace();
			return new SearchBalanceBackMessage();
		}
	
	}
	
	public boolean unsubscribeMessage(String service_code,String svcnum,String svcid,String clid,String recordsn,String sequence_id){
		try{
		String message="";
		message +=	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		message +=	"<interface-result>";
		message += "<head>";
		message += "<service_code>"+service_code+"</service_code>";
		message += "<svcnum>"+svcnum+"</svcnum>";
		message += "<clid>"+clid+"</clid>";
		message += "<recordsn>"+recordsn+"</recordsn>";
		message += "</head>";
		message += "<content>";
		message += "<sequence_id>"+sequence_id+"</sequence_id>";
		message += "</content>";
		message += "</interface-result>";
		
		IphoneService service = new IphoneServiceLocator();
		String backMessage = service.getIphoneServiceHttpPort().doService(message);
		String flag = subMiddleStr(backMessage,"<flag>","</flag>");
		if("00000".equals(flag))
			return true;
		else 
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public UserOrderBackMessage userOrderMessage(String service_code, String svcnum,
			String clid, String recordsn, String pro_no, String state,
			String begin_date, String end_date) {
		try{
			String message="";
			message +=	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			message +=	"<interface-result>";
			message += "<head>";
			message += createTag("service_code",service_code);
			message += createTag("svcnum",svcnum);
			message += createTag("clid",clid);
			message += createTag("recordsn",recordsn);
			message += "</head>";
			message += "<content>";
			message += createTag("pro_no",pro_no);
			message += createTag("state",state);
			message += createTag("begin_date",begin_date);
			message += createTag("end_date",end_date);
			message += "</content>";
			message += "</interface-result>";
			
			//System.out.println("message---"+message);
			IphoneService service = new IphoneServiceLocator();
			String backMessage = service.getIphoneServiceHttpPort().doService(message);
			
			//String backMessage = FileUtils.readFileToString(new File("D:\\test.xml"),"utf-8");
			//System.out.println(backMessage);
			
			UserOrderBackMessage userOrderBack 
				= new UserOrderBackMessage();
			
			userOrderBack.setService_code(subMiddleStr(backMessage,"<service_code>","</service_code>"));
			userOrderBack.setSvcnum(subMiddleStr(backMessage,"<svcnum>","</svcnum>"));
			userOrderBack.setRecordsn(subMiddleStr(backMessage,"<recordsn>","</recordsn>"));
			userOrderBack.setFlag(subMiddleStr(backMessage,"<flag>","</flag>"));
			userOrderBack.setDetail(subMiddleStr(backMessage,"<detail>","</detail>"));
			
			List<UserOrderRecordMessage> list 
				= new ArrayList<UserOrderRecordMessage>();
			
			String[] contentMessage = backMessage.split("<content>");
			if(contentMessage!=null && contentMessage.length>1){ //有content内容
				String[] recordMessage = contentMessage[1].split("<record>");
				if(recordMessage!=null && recordMessage.length>1){//有record内容
					for(int i=1;i<recordMessage.length;i++){
						UserOrderRecordMessage record = new UserOrderRecordMessage();
						if(recordMessage[i].length()<1)
							continue;
						if(!recordMessage[i].contains("pro_no"))
							continue;
						record.setPro_no(subMiddleStr(recordMessage[i],"<pro_no>","</pro_no>"));
						if(!recordMessage[i].contains("sequence_id"))
							continue;
						record.setSequence_id(subMiddleStr(recordMessage[i],"<sequence_id>","</sequence_id>"));
						record.setCreate_time(subMiddleStr(recordMessage[i],"<create_time>","</create_time>"));
						record.setInvalidate_time(subMiddleStr(recordMessage[i],"<invalidate_time>","</invalidate_time>"));
						record.setOrder_money(subMiddleStr(recordMessage[i],"<order_money>","</order_money>"));
						record.setState(subMiddleStr(recordMessage[i],"<state>","</state>"));
						list.add(record);
					}
				}
			}
			userOrderBack.setRecordList(list);
			//System.out.println("----"+backMessage);
			return userOrderBack;
			
		}catch(Exception e){
			e.printStackTrace();
			return new UserOrderBackMessage();
		}
	}
	
	public String subMiddleStr(String str,String benginStr,String endStr){
		String lastStr="";
		Integer begin = str.indexOf(benginStr);
		if(begin!=-1)
			begin += benginStr.length();
		Integer end = str.indexOf(endStr);
		if(begin!=-1 && end!=-1)
			lastStr = str.substring(begin,end);
		
		return lastStr;
	}
	
	
	public String createTag(String tagName,String tagValue){
		if(tagValue==null || "".equals(tagValue))
			return "<"+tagName+"/>";
		else
			return "<"+tagName+">"+tagValue+ "</"+tagName+">";
	}
	public static void main(String[] args)throws Exception {
	/*String message = FileUtils.readFileToString(new File("D:\\test.xml"),"utf-8");
		System.out.println(message);
		IphoneService service = new IphoneServiceLocator();
		String backMessage = service.getIphoneServiceHttpPort().doService(message);
		System.out.println(backMessage);*/
		FeeMsgServiceImpl service = new FeeMsgServiceImpl();
		/*FeeBackMessage feeBack = service.sendFeeMessage("ACIP02001","18601120940","","0002","901662018890","央视电影包月", 1, 000, 
				"视频公", "央视电影", "000001", "冰河世纪3", 
				"http://ivod.wo.com.cn/subNewsList.aspx?menu=&vid=51", 
			"", new Date());*/
		
	/*	FeeBackMessage feeBack = service.sendFeeMessage("ACIP02001","18601120940","","0015","iphonefeerecordsniphonefeerecordsn","频道包月", 2, 000, 
				"十分科技", "手机书城", "250000002", "IPHONE临时", 
				"/pps_cms/s.do?pg=p&pd=250000002&gd=4069&ad=001&fc=25000000&", 
				"http://book.moluck.com/pps/unfee?feeType=1&pid=250000002&mobile=MTg2MDExMjA5NDA=", new Date());*/
		/*System.out.println(feeBack.getDetail());
		System.out.println(feeBack.getFlag());
		System.out.println(feeBack.getRecordsn());
		System.out.println(feeBack.getSequence_id());
		System.out.println(feeBack.getService_code());
		System.out.println(feeBack.getSvcnum());*/
	
//		SearchOrderBackMessage orderBack = 
//			service.searchOrderMessage("ACIP01002","13258020567","","0002","901662018890","000001","2009-10-09 11:55:03", "2009-10-09 11:55:03");
//		List<OrderRecord> recordList = orderBack.getRecordList();
//		System.out.println(recordList.size());

		/*SearchBalanceBackMessage balance = 
			service.searchBalanceMessage("ACIP01001","13258020567","","0002","901662018890");
		System.out.println(balance.getPl_money());
		System.out.println(balance.getWo_money());*/
		
		//System.out.println(service.unsubscribeMessage("ACIP02002", "18601120940", "", "0002", "901662018890", "901662018890"));
		//UserOrderBackMessage feeBack = service.userOrderMessage("ACIP01002", "18601120940", "0002", "", "000001", "1", "2009-08-23 18:50:48", "2009-10-23 18:50:48");
		UserOrderBackMessage feeBack = service.userOrderMessage("ACIP01002", "18601120940", "0015", "000000000017", "001500011000640110018611", "1", "", "");
		
		System.out.println(feeBack.getDetail());
		System.out.println(feeBack.getFlag());
		System.out.println(feeBack.getRecordsn());
		System.out.println(feeBack.getService_code());
		System.out.println(feeBack.getSvcnum());
		System.out.println(feeBack.getRecordList().size());
		List<UserOrderRecordMessage> list  = feeBack.getRecordList();
		for(UserOrderRecordMessage order : list){
			System.out.println("state--->>"+order.getState());
		}
	}

}
