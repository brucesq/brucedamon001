/**
 * 资料信息修改结果标签
 * 标签名称：uc_info_eidt_result (修改成功自动跳转到资料显示页，失败则提示失败文字)
 * 
 * 
 */
package com.hunthawk.reader.pps.usercenter.user;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.UserCenter.domain.InformationPO;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.UserCenterService;
import com.hunthawk.reader.pps.usercener.Des;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.process.Redirect;
import com.hunthawk.tag.util.ParamUtil;

/**
 * @author liuxh
 *
 */
public class UCInfoEditResultTag extends BaseTag {

	private BussinessService bussinessService;
	private UserCenterService userCenterService;
	/* (non-Javadoc)
	 * @see com.hunthawk.tag.BaseTag#parseTag(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		/**得到修改的index值*/
		Integer index=Integer.parseInt(request.getParameter("item_index").toString());
		/**得到手机号进行解密*/
		if(request.getParameter("mobile")==null){
			TagLogger.debug(tagName, "获取手机号失败", request.getQueryString(), null);
			return new HashMap();
		}
		String mobile=new Des().decrypt(request.getParameter("mobile"));
		
		
		InformationPO info=new InformationPO();
		info.mobile=mobile;
		if(index==1){
			info.nickname=request.getParameter("nickname");
		}else if(index==2){
			info.truename=request.getParameter("truename");
		}else if(index==3){
			info.question=request.getParameter("question");
		}else if(index==4){
			info.answer=request.getParameter("answer");
		}else if(index==5){
			info.level=ParamUtil.getIntParameter(request, "level", 0);
		}else if(index==6){
			info.gender=ParamUtil.getIntParameter(request, "gender", 0);
		}else if(index==7){
			info.birthday=request.getParameter("year")+"年"+request.getParameter("month")+"月"+request.getParameter("day");
		}else if(index==8){
			info.constellation=ParamUtil.getIntParameter(request, "constellation", 0);
		}else if(index==9){
			info.address=request.getParameter("address");
		}else if(index==10){
			info.sign=request.getParameter("sign");
		}else if(index==11){
			info.headPic=request.getParameter("headPic");
		}else if(index==12){
			info.home=request.getParameter("home");
		}else if(index==13){
			info.height=ParamUtil.getIntParameter(request, "height", 0);
		}else if(index==14){
			info.weight=ParamUtil.getIntParameter(request, "weight", 0);
		}else if(index==15){
			info.bodytype=ParamUtil.getIntParameter(request, "bodytype", 0);
		}else if(index==16){
			info.bloodtype=ParamUtil.getIntParameter(request, "bloodtype", 0);
		}else if(index==17){
			info.enjoyBooktype=ParamUtil.getIntParameter(request, "enjoyBooktype", 0);
		}else if(index==18){
			info.personallty=request.getParameter("personallty");
		}else if(index==19){
			info.nativePlace=request.getParameter("nativePlace");
		}else if(index==20){
			info.education=request.getParameter("education");
		}else if(index==21){
			info.interest=request.getParameter("interest");
		}else if(index==22){
			info.income=ParamUtil.getIntParameter(request, "income", 0);
		}else if(index==23){
			info.job=request.getParameter("job");
		}else if(index==24){
			info.feeling=ParamUtil.getIntParameter(request, "feeling", 0);
		}else if(index==25){
			info.maritalStatus=ParamUtil.getIntParameter(request, "maritalStatus", 0);
		}else if(index==26){
			info.smokingStatus=ParamUtil.getIntParameter(request, "isSmoking", 0);
		}else if(index==27){
			info.drinkStatus=ParamUtil.getIntParameter(request, "isDrinking", 0);
		}else if(index==28){
			info.introduction=request.getParameter("introduction");
		}else if(index==29){
			info.enjoyBody=ParamUtil.getIntParameter(request, "enjoyBody", 0);
		}else if(index==30){
			info.enjoyBook=request.getParameter("enjoyBook");
		}else if(index==31){
			info.email=request.getParameter("email");
		}else if(index==32){
			info.QQ=request.getParameter("qq");
		}else if(index==33){
			info.MSN=request.getParameter("msn");
		}else{
			//@TODO
			//更多属性修改，开发中.....
		}
		boolean  flag=getUserCenterService(request).updUserInfo(info);
		System.out.println("update result--->"+flag);
		
		if(flag){
			StringBuilder goUrl=new StringBuilder();
			goUrl.append(getBussinessService(request).getVariables("UC_HOME_URL").getValue());
			goUrl.append("&mobile="+new Des().encrypt(mobile));
			Redirect.sendRedirect(goUrl.toString()) ;//无刷新跳转到登陆页面
			return new HashMap();
		}else{
			StringBuilder backUrl=new StringBuilder();
			backUrl.append(request.getContextPath());
			backUrl.append(ParameterConstants.PORTAL_PATH);
			backUrl.append("?");
			backUrl.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.TEMPLATE_ID,"item_index"));
		
			Map velocityMap = new HashMap();
			//添加操作标识  0.失败 1.成功
			velocityMap.put("url", backUrl.toString());
			Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance()
					.parseVM(velocityMap, this, tagTem));
			return resultMap;
		}
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
	private UserCenterService getUserCenterService(HttpServletRequest request) {
		if (userCenterService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			userCenterService = (UserCenterService) wac
					.getBean("userCenterService");
		}
		return userCenterService;
	}
}
