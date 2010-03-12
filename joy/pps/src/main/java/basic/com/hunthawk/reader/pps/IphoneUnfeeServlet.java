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
 * IPHONE退订servlet
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
		System.out.println("IPHONE退订");
		//获取相关参数 
		/**退订标识 1.产品  2.栏目 3.VIP*/
		String feeType=request.getParameter("feeType");
		/**手机号 需要解密*/
		String mobileSec=request.getParameter("mobile");
		
		String mobile="";//解密后的手机号
		System.out.println("解密前手机号="+mobileSec);
		if(StringUtils.isNotEmpty(mobileSec)){//手机号为空
			mobile = new String(Base64.decodeBase64(new String(mobileSec.getBytes("GB2312"),"UTF-8").getBytes("UTF-8")));
			System.out.println("解密后手机号="+mobile);
		}
		
		/** 产品ID*/
		String pid=request.getParameter("pid");
		/**栏目ID*/
		String columnId=request.getParameter("colid");
		/**计费ID*/
		String feeId=request.getParameter("feeId");
		/**批价包ID*/
		String packId=request.getParameter("packID");
			
		Fee fee=getCustomService(request).getFee(feeId);  
		if(feeType==null || "".equals(feeType) || mobileSec==null || "".equals(mobileSec) || feeId==null || "".equals(feeId) || packId==null || "".equals(packId)){//参数不全
			logger.error("跳转参数不全");
			content="error:跳转参数不全,请求失败!";
			//
		}else{
				boolean error=false;
				String temp="";
				if(feeType.equals("1")){//产品退订
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
						logger.error("产品退订流水表记录添加失败:"+ex.getMessage());
					}
				}else if(feeType.equals("2")){//栏目退订
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
						logger.error("栏目退订流水表记录添加失败:"+ex.getMessage());
					}
				}else if(feeType.equals("3")){//VIP退订
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
						logger.error("VIP退订流水表记录添加失败:"+ex.getMessage());
					}
				}
				if(!error){
					/**计费平台受理流水号 (联通平台返回的)*/
					//查询表记录 得到相关退订参数
					UserMonthUnicomBackMsg backmsg=getCustomService(request).getUserMonthUnicomBackMsg(mobile, Integer.parseInt(packId), Integer.parseInt(feeType), pid, Integer.parseInt(columnId),feeId);
					boolean result=getFeeMsgService(request).unsubscribeMessage("ACIP02002", mobile, "", getBussinessService(request).getVariables("iphone_spid").getValue(),backmsg.getRecordsn(),backmsg.getSequenceId());
					if(result){
						content=temp+" 已成功退订!";
						//修改状态 为0   标识资源已经退订  
						try{
							getCustomService(request).updateUserMonthUnicomBackMsgStatus(backmsg.getId());
						}catch(Exception ex){
							logger.debug("UserMonthUnicomBackMsg表状态修改失败:"+ex.getMessage());
						}
						
					}else{//调用退订接口失败
						content=temp+" 退订失败";
					}
				}else{
					content="系统繁忙,请稍后再试!";//退订流水表记录添加异常
				}
		
		}
		
		PrintWriter out = response.getWriter();
		//读取系统变量  
		String path=getBussinessService(request).getVariables("unsubscribe_url").getValue();
		//读取模版文件 输出
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
