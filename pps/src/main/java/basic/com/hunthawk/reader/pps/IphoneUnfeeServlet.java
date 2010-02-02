package com.hunthawk.reader.pps;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.domain.custom.UserMonthUnicomBackMsg;
import com.hunthawk.reader.domain.custom.UserUnsubscribeList;
import com.hunthawk.reader.domain.partner.Fee;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.enhance.util.Des;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.FeeMsgService;
import com.hunthawk.reader.pps.service.ResourceService;

/**
 * IPHONE�˶�servlet
 * @author liuxh
 *
 */
public class IphoneUnfeeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(ActionServlet.class);
	
    private CustomService customService;   
    private BussinessService bussinessService;
    private FeeMsgService feeMsgService; 
    private MemCachedClientWrapper memcached;
    public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public IphoneUnfeeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		process(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		process(request, response);
	}

	/**
	 * <p>
	 * Process the user request
	 * </p>
	 * 
	 * @since 1.0
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void process(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html; charset=GB2312"); 
		response.setCharacterEncoding("GB2312"); 
		
		String content="";
		System.out.println("IPHONE�˶�");
		//��ȡ��ز��� 
		/**�˶���ʶ 1.��Ʒ  2.��Ŀ 3.VIP*/
		String feeType=request.getParameter("feeType");
		/**�ֻ��� ��Ҫ����*/
		String mobileSec=request.getParameter("mobile");
		
		String mobile="";//���ܺ���ֻ���
		System.out.println("����ǰ�ֻ���="+mobileSec);
		if(StringUtils.isNotEmpty(mobileSec)){//�ֻ���Ϊ��
			mobile = new String(Base64.decodeBase64(new String(mobileSec.getBytes("GB2312"),"UTF-8").getBytes("UTF-8")));
			System.out.println("���ܺ��ֻ���="+mobile);
		}
		
		/** ��ƷID*/
		String pid=request.getParameter("pid");
		/**��ĿID*/
		String columnId=request.getParameter("colid");
		/**�Ʒ�ID*/
		String feeId=request.getParameter("feeId");
		/**���۰�ID*/
		String packId=request.getParameter("packID");
			
		Fee fee=getCustomService(request).getFee(feeId);  
		if(feeType==null || "".equals(feeType) || mobileSec==null || "".equals(mobileSec) || feeId==null || "".equals(feeId) || packId==null || "".equals(packId)){//������ȫ
			logger.error("��ת������ȫ");
			content="error:��ת������ȫ,����ʧ��!";
			//
		}else{
				boolean error=false;
				String temp="";
				if(feeType.equals("1")){//��Ʒ�˶�
					String channelId = getBussinessService(request).getDefaultChannelId(pid);
					UserUnsubscribeList list=new UserUnsubscribeList();
					list.setCreateTime(new Date());
					list.setChannelId(channelId);
					list.setFeeId(fee.getId());
					list.setUnsubType(Integer.parseInt(feeType));
					list.setMobile(mobile);
					list.setPackId(Integer.parseInt(packId));
					list.setPid(pid);
					list.setProductID(fee.getProductId());
					list.setServiceId(fee.getServiceId());
					list.setSpid(fee.getProvider().getProviderId());
					list.setColumnId(-1);
					list.setStatus(1);
					try{
						getCustomService(request).addIphoneUnsubList(list);
						temp=getBussinessService(request).getProduct(pid).getName();
					}catch(Exception ex){
						error=true;
						logger.error("��Ʒ�˶���ˮ���¼���ʧ��:"+ex.getMessage());
					}
				}else if(feeType.equals("2")){//��Ŀ�˶�
					String channelId = getBussinessService(request).getDefaultChannelId(pid);
					UserUnsubscribeList list=new UserUnsubscribeList();
					list.setCreateTime(new Date());
					list.setChannelId(channelId);
					list.setFeeId(fee.getId());
					list.setUnsubType(Integer.parseInt(feeType));
					list.setMobile(mobile);
					list.setPackId(Integer.parseInt(packId));
					list.setPid(pid);
					list.setProductID(fee.getProductId());
					list.setServiceId(fee.getServiceId());
					list.setSpid(fee.getProvider().getProviderId());
					list.setColumnId(Integer.parseInt(columnId));
					list.setStatus(1);
					try{
						getCustomService(request).addIphoneUnsubList(list);
						temp=getBussinessService(request).getColumns(Integer.parseInt(columnId)).getName();
					}catch(Exception ex){
						error=true;
						logger.error("��Ŀ�˶���ˮ���¼���ʧ��:"+ex.getMessage());
					}
				}else if(feeType.equals("3")){//VIP�˶�
					String channelId = getBussinessService(request).getDefaultChannelId(pid);
					UserUnsubscribeList list=new UserUnsubscribeList();
					list.setCreateTime(new Date());
					list.setChannelId(channelId);
					list.setFeeId(fee.getId());
					list.setUnsubType(Integer.parseInt(feeType));
					list.setMobile(mobile);
					list.setPackId(Integer.parseInt(packId));
					list.setPid(pid);
					list.setProductID(fee.getProductId());
					list.setServiceId(fee.getServiceId());
					list.setSpid(fee.getProvider().getProviderId());
					list.setColumnId(-1);
					list.setStatus(1);
					try{
						getCustomService(request).addIphoneUnsubList(list);
						temp="VIP";
					}catch(Exception ex){
						error=true;
						logger.error("VIP�˶���ˮ���¼���ʧ��:"+ex.getMessage());
					}
				}
				if(!error){
					/**�Ʒ�ƽ̨������ˮ�� (��ͨƽ̨���ص�)*/
					//��ѯ���¼ �õ�����˶�����
					UserMonthUnicomBackMsg backmsg=getCustomService(request).getUserMonthUnicomBackMsg(mobile, Integer.parseInt(packId), Integer.parseInt(feeType), pid, Integer.parseInt(columnId),feeId);
					boolean result=getFeeMsgService(request).unsubscribeMessage("ACIP02002", mobile, "", getBussinessService(request).getVariables("iphone_spid").getValue(),backmsg.getRecordsn(),backmsg.getSequenceId());
					if(result){
						content=temp+" �ѳɹ��˶�!";
						//�޸�״̬ Ϊ0   ��ʶ��Դ�Ѿ��˶�  
						try{
							getCustomService(request).updateUserMonthUnicomBackMsgStatus(backmsg.getId());
						}catch(Exception ex){
							logger.debug("UserMonthUnicomBackMsg��״̬�޸�ʧ��:"+ex.getMessage());
						}
						
					}else{//�����˶��ӿ�ʧ��
						content=temp+" �˶�ʧ��";
					}
				}else{
					content="ϵͳ��æ,���Ժ�����!";//�˶���ˮ���¼����쳣
				}
		
		}
		
		PrintWriter out = response.getWriter();
		//��ȡϵͳ����  
		String path=getBussinessService(request).getVariables("unsubscribe_url").getValue();
		//��ȡģ���ļ� ���
		try {
			out.println(IOUtil.htmlParser(path, content));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		private CustomService getCustomService(HttpServletRequest request) {
			if (customService == null) {
				ServletContext servletContext = request.getSession()
						.getServletContext();
				WebApplicationContext wac = WebApplicationContextUtils
						.getRequiredWebApplicationContext(servletContext);
				customService = (CustomService) wac
						.getBean("customService");
			}
			return customService;
		}
		private BussinessService getBussinessService(HttpServletRequest request) {
			if (bussinessService == null) {
				ServletContext servletContext = request.getSession()
						.getServletContext();
				WebApplicationContext wac = WebApplicationContextUtils
						.getRequiredWebApplicationContext(servletContext);
				bussinessService = (BussinessService) wac
						.getBean("bussinessService");
			}
			return bussinessService;
		}
		private FeeMsgService getFeeMsgService(HttpServletRequest request) {
			if (feeMsgService == null) {
				ServletContext servletContext = request.getSession()
						.getServletContext();
				WebApplicationContext wac = WebApplicationContextUtils
						.getRequiredWebApplicationContext(servletContext);
				feeMsgService = (FeeMsgService) wac
						.getBean("feeMsgService");
			}
			return feeMsgService;
		}
}
