package com.hunthawk.reader.pps.iphone.fee;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.custom.BookBag;
import com.hunthawk.reader.domain.custom.UserBuyMonth;
import com.hunthawk.reader.domain.custom.UserOrderBackMessage;
import com.hunthawk.reader.domain.custom.UserOrderRecordMessage;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.IphoneFeeParamService;
import com.hunthawk.reader.pps.service.IphoneService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.process.Redirect;
import com.hunthawk.tag.util.ParamUtil;
/**
 * iphone������Ϣ¼���ǩ
 * ��ǩ���ƣ�iinfo_enter
 * @author liuxh
 *
 */
public class InfoEnteringTag extends BaseTag {

	private CustomService customService;
	private IphoneService iphoneService;
	private ResourceService resourceService;
	private BussinessService bussinessService;
	private IphoneFeeParamService iphoneFeeParamService;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		System.out.println("�Ѿ�����Ԥ����ת���ҵ�ͨ��ģ������...  iinfo_enter��ǩ������ʼ");
		boolean isIphone=getIphoneService(request).isIphoneProduct(request.getParameter(ParameterConstants.PRODUCT_ID));
		if(isIphone){
			/**����*/
			String resourceId=URLUtil.getResourceId(request);
			String mobile=RequestUtil.getMobile();
			String packId= request.getParameter(ParameterConstants.FEE_BAG_ID);
			String relId=request.getParameter(ParameterConstants.FEE_BAG_RELATION_ID);
			//��ѯ����
			String prono=getIphoneService(request).getIphoneProductNo(resourceId,packId,relId);//����
			UserOrderBackMessage msg_resource=getIphoneService(request).searchOrder(mobile, prono, "1", "", "");
			functionOrderInfoInsert(request,3,msg_resource,prono,mobile);
			
			//��ѯƵ������
			String prono_channel=prono.substring(0,8);
			UserOrderBackMessage msg_channel=getIphoneService(request).searchOrder(mobile, prono_channel, "1", "", "");
			functionOrderInfoInsert(request,1,msg_channel,prono,mobile);
			
			//��ѯ��Ŀ����
			String prono_column=prono.substring(0,16);
			UserOrderBackMessage msg_column=getIphoneService(request).searchOrder(mobile, prono_column, "1", "", "");
			functionOrderInfoInsert(request,2,msg_column,prono,mobile);
			
			Redirect.sendRedirect(request.getRequestURL()+"?"+URLUtil.removeParameter(request.getQueryString(), ParameterConstants.COMMON_PAGE,ParameterConstants.TEMPLATE_ID)) ;//��ת������ҳ��
			return new HashMap();
		}else{
			TagLogger.debug(tagName, "��Iphone��Ʒ", request.getQueryString(), null);
			return new HashMap();
		}
	}
	/**
	 * 
	 * @param flag  1.Ƶ�� 2.��Ŀ 3.����
	 * @param pro_no
	 */
	private void functionOrderInfoInsert(HttpServletRequest request,int flag,UserOrderBackMessage message,String pro_no,String mobile){
		if(message.getFlag().equals("00000")){
			String pid=request.getParameter(ParameterConstants.PRODUCT_ID);
			String resourceId=URLUtil.getResourceId(request);
			
			List<UserOrderRecordMessage> userOrderRecordlist=message.getRecordList();
			for(Iterator it=userOrderRecordlist.iterator();it.hasNext();){
				UserOrderRecordMessage uorm=(UserOrderRecordMessage)it.next();
				String state=uorm.getState();
				if(state.equals("1")){
					int relsId=getResourcePackReleationId(pro_no);
					ResourcePackReleation rpr=getResourceService(request).getResourcePackReleation(relsId);
					if(flag==1 || flag==2){
						String feeId="";
						int packId=-1;
						if(flag==1){//Ƶ��
							/**
							 * ������Դ����
							 * modify by liuxh 09-11-16
							 */
							feeId=getIphoneFeeParamService(request).getIphoneFeeIds(resourceId, 1)[0];
							packId=Integer.parseInt(getIphoneFeeParamService(request).getIphoneChannelPackId(resourceId));
							/**
							 * end
							 */
//							feeId=getBussinessService(request).getVariables("iphone_fee_channel").getValue();
//							packId=Integer.parseInt(getBussinessService(request).getVariables("iphone_channel_packs").getValue());
						}else if(flag==2){//��Ŀ
							feeId=getIphoneFeeParamService(request).getIphoneFeeIds(resourceId, 2)[0];
//							feeId=getBussinessService(request).getVariables("iphone_fee_column").getValue();
							packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
						}
						//����Ϣ����±���бȶ�
						List<UserBuyMonth>  userBuyMonths=getCustomService(request).getUserBuyMonths(mobile);
						if(userBuyMonths.size()==0){
							String msg=getIphoneService(request).orderMonth(feeId,packId,mobile,pid);
							 if(!"".equals(msg)){
								 System.out.println(flag==1?"Ƶ��":"��Ŀ"+"���¶�������¼��ʧ��: "+msg);
								 TagLogger.debug("InfoEnteringTag",flag==1?"Ƶ��":"��Ŀ"+"���¶�������¼��ʧ�ܣ�"+ msg, request.getQueryString(), null);
							 }
						}
						//����Ϣ������±�  ��������ˮ��
						for(int i=0;i<userBuyMonths.size();i++){
							UserBuyMonth userBuyMonth=(UserBuyMonth)userBuyMonths.get(i);
							if(userBuyMonth.getPackId().equals(rpr.getPack().getId())){//��¼����
								continue;
							}else{//��¼������
								if(i==userBuyMonths.size()-1){
									String msg=getIphoneService(request).orderMonth(feeId,packId,mobile,pid);
									 if(!"".equals(msg)){
										 System.out.println(flag==1?"Ƶ��":"��Ŀ"+"���¶�������¼��ʧ��: "+msg);
										 TagLogger.debug("InfoEnteringTag",flag==1?"Ƶ��":"��Ŀ"+"���¶�������¼��ʧ�ܣ�"+ msg, request.getQueryString(), null);
									 }
								}
							}
						}
					}else if(flag==3){
						//����Ϣ���������бȶ�  (�Ƚϵ�������ID)  �����۹���ID ת������ID
						List<BookBag> bookBags=getCustomService(request).getUserBookbag(mobile);
						if(bookBags.size()==0){
							String msg=getIphoneService(request).orderOne(rpr, mobile, resourceId, pid);
							 if(!"".equals(msg)){
								 System.out.println("���ζ�������¼��ʧ�ܣ�"+msg);
								 TagLogger.debug("InfoEnteringTag","���ζ�������¼��ʧ�ܣ�"+ msg, request.getQueryString(), null);
							 }
						}
						 for(int i=0;i<bookBags.size();i++){
							 BookBag bookBag=(BookBag)bookBags.get(i);
							 if(bookBag.getContentId().equals(rpr.getResourceId())){//��¼����
								continue;
							 }else{//��¼������
								 if(i==bookBags.size()-1){
									 String msg=getIphoneService(request).orderOne(rpr, mobile, resourceId, pid);
									 if(!"".equals(msg)){
										 System.out.println("���ζ�������¼��ʧ�ܣ�"+msg);
										 TagLogger.debug("InfoEnteringTag","���ζ�������¼��ʧ�ܣ�"+ msg, request.getQueryString(), null);
									 }
								 }
							 }
						 }
					}
				}else{
					System.out.println("��Ʒ��Ϊ:"+uorm.getPro_no()+"�Ķ�����ʧЧ��");
					TagLogger.debug("InfoEnteringTag", "��Ʒ��Ϊ:"+uorm.getPro_no()+"�Ķ�����ʧЧ��", request.getQueryString(), null);
				}
			}//for end
		}
	}

	/**�õ����۰����ù�ϵID*/
	private int getResourcePackReleationId(String pro_no){
		if(StringUtils.isEmpty(pro_no)){
			return 0;
		}
		String str=pro_no.substring(pro_no.length()-7);
		char [] arr=str.toCharArray();
//		StringBuilder strs=new StringBuilder();
		String packId="";
		for(int i=0;i<arr.length;i++){
			if(Integer.parseInt(String.valueOf(arr[i]))>0){
				packId=str.substring(i);
				break;
			}
		}
		return Integer.parseInt(packId);
	}
	/**�õ����۰�ID*/
	private int getResourcePackId(String pro_no){
		if(StringUtils.isEmpty(pro_no)){
			return 0;
		}
		String str=pro_no.substring(9,16);
		char [] arr=str.toCharArray();
		String packId="";
		for(int i=0;i<arr.length;i++){
			if(Integer.parseInt(String.valueOf(arr[i]))>0){
				packId=str.substring(i);
				break;
			}
		}
		return Integer.parseInt(packId);
	}
	private IphoneService getIphoneService(HttpServletRequest request) {
		if (iphoneService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			iphoneService = (IphoneService) wac.getBean("iphoneService");
		}
		return iphoneService;
	}
	private CustomService getCustomService(HttpServletRequest request) {
		if (customService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			customService = (CustomService) wac.getBean("customService");
		}
		return customService;
	}
	private ResourceService getResourceService(HttpServletRequest request) {
		if (resourceService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			resourceService = (ResourceService) wac.getBean("resourceService");
		}
		return resourceService;
	}
	private BussinessService getBussinessService(HttpServletRequest request) {
		if (bussinessService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			bussinessService = (BussinessService) wac.getBean("bussinessService");
		}
		return bussinessService;
	}
	private IphoneFeeParamService getIphoneFeeParamService(HttpServletRequest request) {
		if (iphoneFeeParamService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			iphoneFeeParamService = (IphoneFeeParamService) wac.getBean("iphoneFeeParamService");
		}
		return iphoneFeeParamService;
	}
	public static void main(String[] args){
		String str="D5E36A0BA08B315A7B6773A83008BD19BFE964E53F2EF2AAB68382EB72D31AC773E0BF8878A859A8";
//		String pro_no="001500011000610000016864";
//		long currentTime = System.currentTimeMillis();
//		long tagBegin = currentTime;
//		System.out.println(new InfoEnteringTag().getResourcePackReleationId(pro_no));
//		System.out.println(new InfoEnteringTag().getResourcePackId(pro_no));
//		System.out.println(System.currentTimeMillis() - tagBegin);
		
	}
}
