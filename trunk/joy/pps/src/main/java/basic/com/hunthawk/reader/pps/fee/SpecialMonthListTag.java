package com.hunthawk.reader.pps.fee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.custom.UserBuyMonthChoice;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.pps.ArrayUtil;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.components.wml.Anchor;
import com.hunthawk.tag.components.wml.Go;
import com.hunthawk.tag.components.wml.IPostfieldModel;
import com.hunthawk.tag.components.wml.SimplePostfieldModel;
import com.hunthawk.tag.vm.VmInstance;

/**
 * 20ѡ3��Դ�б�(��ѡ/δѡ) 
 * ��ǩ���ƣ�special_month_list 
 * ����˵���� 
 * packId:���۰�ID
 * buttonTitle:���ܰ�ť��������
 * templateId:ѡ������ʾҳ
 * orderTitle:������������ (δ���������û�)
 * 
 * @author liuxh
 * 
 */
public class SpecialMonthListTag extends BaseTag {

	private ResourceService resourceService;
	private CustomService customService;
	private BussinessService bussinessService;
	private boolean showButton;
	public boolean isShowButton() {
		return showButton;
	}

	public void setShowButton(boolean showButton) {
		this.showButton = showButton;
	}

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		TagTemplate tagTem=null;
		int tagTemplateId = this.getIntParameter("tmd", 0);
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
			
		}else{
			tagTem = null;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		Map resultMap = new HashMap();
		String result = "";
		// �õ����۰���ID
		String packId = request
				.getParameter(ParameterConstants.MONTH_FEE_BAG_ID) == null ? ""
				: request.getParameter(ParameterConstants.MONTH_FEE_BAG_ID);
		String mobile =RequestUtil.getMobile();
		if (StringUtils.isEmpty(packId)) {
			packId = getParameter("packId", "");
			if (StringUtils.isEmpty(packId)) {
				TagLogger.debug(tagName, "���۰�IDΪ��", request.getQueryString(),
						null);
				return new HashMap();
			}
		}
		// �������۰���ID���������Ϣ
		ResourcePack pack = getResourceService(request).getResourcePack(
				Integer.parseInt(packId));
		if (pack == null) {
			TagLogger.error(tagName, "NѡX��������������۰�������", request
					.getQueryString(), null);
			return new HashMap();
		}
		int type=pack.getType();//�ж�����  �Ƿ���20ѡ3
		if(type==Constants.FEE_TYPE_CHOICE){//���ݿ���
			boolean isVip = getCustomService(request).isOrderMonth(mobile,
					pack.getFeeId());
			map.put("isVip", isVip);
			if (isVip) {
				// �������۰���Ϣ�õ�feeId
				String feeId = pack.getFeeId();
				// ��ȡ�û�����ͨ��NѡX�ķ�ʽ ѡ����ͼ��
				List<UserBuyMonthChoice> selectedList = getCustomService(request)
						.getUserChoiceBooks(mobile, feeId);
				int allowChoise = pack.getChoice();// ����ѡ��ĸ���
				int userChoise = selectedList.size();// �û��Ѿ�ѡ��ĸ���
				String[] selectIds = new String[userChoise];// �û���ѡ��ԴID����
				List<Object> selectedRess = new ArrayList<Object>();
				int loop = 0;
				for (Iterator it = selectedList.iterator(); it.hasNext();) {
					loop++;
					UserBuyMonthChoice user = (UserBuyMonthChoice) it.next();
					// ������ԴID��ѯ��Դ�����Ϣ
					ResourceAll resource = getResourceService(request).getResource(
							user.getContentId());
					selectIds[loop-1] = resource.getId();
					StringBuilder sb = new StringBuilder();
					// sb.append("��ѡ���ͼ��:");
					// sb.append("<br/>");
					sb.append(loop);
					sb.append(".");
					sb.append(resource.getName());
					//sb.append("<br/>");
					/** ���浥����¼ */
					Map<String, Object> obj = new HashMap<String, Object>();
					obj.put("selectTitle", sb.toString());
					StringBuilder url=new StringBuilder();
					url.append(request.getContextPath());
					url.append(ParameterConstants.PORTAL_PATH);
					url.append("?");
					url.append(ParameterConstants.PAGE);
					url.append("=");
					url.append(ParameterConstants.PAGE_RESOURCE);
					url.append("&");
					URLUtil.append(url, ParameterConstants.PRODUCT_ID, request);
					URLUtil.append(url, ParameterConstants.PAGEGROUP_ID, request);
					URLUtil.append(url, ParameterConstants.AREA_ID, request);
					URLUtil.append(url, ParameterConstants.COLUMN_ID, request);
					url.append(ParameterConstants.FEE_BAG_ID);
					url.append("=");
					url.append(packId);
					url.append("&");
//					url.append(ParameterConstants.FEE_BAG_RELATION_ID);
//					url.append("=");
//					ResourcePackReleation rpr=getResourceService(request).getResourcePackReleationByResourceId(resource.getId()).get(0);
//					url.append(rpr.getId());
//					url.append("&");
					url.append(ParameterConstants.RESOURCE_ID);
					url.append("=");
					url.append(resource.getId());
					url.append("&");
					URLUtil.append(url, ParameterConstants.CHANNEL_ID, request);
					URLUtil.append(url, ParameterConstants.UNICOM_PT, request);
					url.append(ParameterConstants.PAGE_NUMBER);
					url.append("=");
					url.append(1);
					url.append("&");
					url.append(ParameterConstants.FEE_ID);
					url.append("=");
					url.append(feeId);
					obj.put("url", url.toString());
					/**������Դ����*/
					obj.put("resource", resource);
					selectedRess.add(obj);
				}
				map.put("selectedobjs", selectedRess);
				// �û�δѡ����ͼ�� �б�
				// ��һ�����ж��û�ѡ��ĸ��� ����Ѿ�ѡ�� ����Ҫ����ѡ���б�
				if (userChoise < allowChoise) {
					// �������۰�ID��ѯ�ð��µ�������ԴID����
					String[] ids = null;
					List<ResourcePackReleation> rprs = getResourceService(request)
							.getResourcePackReleations(Integer.parseInt(packId), 1,
									1000);
					if (rprs != null && rprs.size() > 0) {
						ids = new String[rprs.size()];
						for (int i = 0; i < rprs.size(); i++) {
							ResourcePackReleation rpr = rprs.get(i);
							ids[i] = rpr.getResourceId();
						}
					}
					// �Ӽ����г�ȥ�û��Ѿ�ѡ�����Դ
					String[] noSelectIds = ArrayUtil.isContentDiffKey(selectIds,
							ids);
					List<Object> noSelectedRess = new ArrayList<Object>();
					int loop2 = 0;
					for (String rid : noSelectIds) {
						loop2++;
						ResourceAll resource = getResourceService(request)
								.getResource(rid);
						StringBuilder sb = new StringBuilder();
						//sb.append("<input type=\"checkbox\"  name=\"ids"+(loop2-1)+"\"  "+(loop2<=allowChoise-userChoise?"checked=\"checked\"":"")+" value=\""+resource.getId()+"\"/>");//Ĭ��ѡ��ǰ����
						sb.append(loop2);
						sb.append(".");
						sb.append(resource.getName());
						//�ύURL
						StringBuilder back = new StringBuilder();
						back.append(request.getContextPath());
						back.append(ParameterConstants.PORTAL_PATH);
						back.append("?");
						back.append(URLUtil.removeParameter(request.getQueryString(),
								ParameterConstants.PAGE_NUMBER,
								ParameterConstants.TEMPLATE_ID,
								ParameterConstants.COMMENT_PARAM_VALUE,
								ParameterConstants.CUSTOM_KEY_VALUE,
								ParameterConstants.COMMENT_PLATE,
								ParameterConstants.COMMENT_TARGET,
								ParameterConstants.COMMENT_TARGET_ID,
								ParameterConstants.FEE_ID,
								ParameterConstants.FEE_BAG_ID));
						back.append("&");
						back.append(ParameterConstants.FEE_BAG_ID);
						back.append("=");
						back.append(packId);
						back.append("&");
						back.append(ParameterConstants.FEE_ID);
						back.append("=");
						back.append(feeId);
						back.append("&");
						back.append(ParameterConstants.PAGE_NUMBER);
						back.append("=");
						back.append(1);
						back.append("&");
						back.append(ParameterConstants.TEMPLATE_ID);
						back.append("=");
						back.append(getIntParameter("templateId",-1));
						
						/**
						 * �޸����� ֧��2.0
						 * modify by liuxh 
						 * 09-11-11 
						 */
						StringBuilder builder=new StringBuilder();
						builder.append(URLUtil.removeParameter(back.toString(), ParameterConstants.SEARCH_PARAM_VALUE,"old","allow"));
						builder.append("&");
						builder.append(ParameterConstants.SEARCH_PARAM_VALUE);
						builder.append("=");
						builder.append(rid);
						builder.append("&");
						builder.append("old");
						builder.append("=");
						builder.append(String.valueOf(userChoise));
						builder.append("&");
						builder.append("allow");
						builder.append("=");
						builder.append(String.valueOf(allowChoise));
						/**
						 * end
						 */
//						Go go = new Go();
//						go.setCharset("UTF-8");
//						go.setMethod("POST");
//						go.setUrl(back.toString());
//						Map<String, String> postMap = new HashMap<String, String>();
//						postMap.put(ParameterConstants.SEARCH_PARAM_VALUE, rid);
//						postMap.put("old", String.valueOf(userChoise));
//						postMap.put("allow", String.valueOf(allowChoise));
//						IPostfieldModel model = new SimplePostfieldModel(postMap);
//						go.setPostfieldModel(model);
//						Anchor anchor = new Anchor();
//						anchor.setGo(go);
//						anchor.setTitle(getParameter("buttonTitle", "ѡ��"));
//						anchor.setText(getParameter("buttonTitle", "ѡ��"));
//						builder.append(anchor.renderComponent());
						
						//sb.append("<br/>");
						/** ���浥����¼ */
						Map<String, Object> obj = new HashMap<String, Object>();
						obj.put("noSelectTitle", sb.toString());
						obj.put("selectUrl", builder.toString());
						/**������Դ���� */
						obj.put("resource", resource);
						noSelectedRess.add(obj);
					}
					map.put("noselectedobjs", noSelectedRess);
				}
				this.showButton=true;
				map.put("selects", allowChoise-userChoise);
				map.put("allowChoise", allowChoise);
				map.put("userChoise", userChoise);
//				map.put(ParameterConstants.PRE_TAG_SUFFIX + tagName
//						+ ParameterConstants.END_TAG_SUFFIX, builder.toString());
				result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
//				result = VmInstance.getInstance().parseVM(map, this);
//				resultMap.put(TagUtil.makeTag(tagName), result);
//				return resultMap;
			}else{
				//��������ȡ���б�����
				Fee fee = getCustomService(request).getFee(pack.getFeeId());
				String url=request.getQueryString();
				url = URLUtil.removeParameter(url,ParameterConstants.FEE_ID,ParameterConstants.MONTH_FEE_BAG_ID);
				url += "&" + ParameterConstants.FEE_ID + "=" + fee.getId()+"&"+ParameterConstants.MONTH_FEE_BAG_ID+"="+pack.getId();
				StringBuilder builder = new StringBuilder();
				if(fee.getIsout() == 0){
					builder.append(request.getContextPath());
					builder.append( "/");
					builder.append(fee.getUrl());
					builder.append(ParameterConstants.PORTAL_PATH);
					builder.append( "?");
					builder.append( url);
				}else{
					builder.append( request.getContextPath());
					builder.append( ParameterConstants.PORTAL_PATH);
					builder.append( "?");
					builder.append( ParameterConstants.COMMON_PAGE);
					builder.append("=");
					builder.append(ParameterConstants.COMMON_PAGE_FEE);
					builder.append("&");
					builder.append(ParameterConstants.TEMPLATE_ID);
					builder.append("=");
					builder.append(fee.getTemplateId());
					builder.append("&");
					builder.append(url);
				}
				map.put("msg", "����û�ж����˰���ҵ��");
				map.put("orderUrl", builder.toString());
				map.put("title", "���϶���");
//				result = VmInstance.getInstance().parseVM(map, this);
//				resultMap.put(TagUtil.makeTag(tagName), result);
//				return resultMap;
			}
		}else{
			System.out.println("�������Ͳ���20ѡ3");
			TagLogger.debug(tagName, "�������Ͳ���20ѡ3", request.getQueryString(), null);
			return new HashMap();
		}
		
		/**
		 * ��ǩģ�������
		 * modify by liuxh 09-11-11
		 */
		map.put("strUtil", new StrUtil());
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		/**
		 * end
		 */
		return resultMap;
//		return new HashMap();
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
}
