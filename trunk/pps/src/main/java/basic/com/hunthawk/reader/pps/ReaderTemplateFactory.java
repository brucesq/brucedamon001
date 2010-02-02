/**
 * 
 */
package com.hunthawk.reader.pps;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.PackGroupProvinceRelation;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.domain.bussiness.Template;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.TemplateFactory;
import com.hunthawk.tag.process.Redirect;
import com.hunthawk.tag.util.ParamUtil;

/**
 * @author BruceSun
 * 
 */
public class ReaderTemplateFactory extends TemplateFactory {

	private static final String PAGE_NOT_FOUND = "page_not_found";

	private BussinessService bussinessService;
	
	private ResourceService resourceService;

	public String getTemplate(HttpServletRequest request) {

		int tmplID = ParamUtil.getIntParameter(request,
				ParameterConstants.TEMPLATE_ID, -1);
		String content = "";
		if (tmplID != -1) {// ģ��ID����
			content = getTemplateContent(request, tmplID);
			return content;
		}

		if (!isProductShowable(request)) {
			// ������״̬���û����������ʲ�Ʒ�����ҵ��Ϊ�շ�ҵ������������Ʒѡ�
			// ������״̬���û��޷������ò�Ʒ�����������û������������ʣ������������Ʒѡ�
			// ��ͣ״̬�� �û��޷������ò�Ʒ�����������û������������ʣ������������Ʒѡ�
			// ����״̬�� �û��������������û����޷��������ʲ�Ʒ���������߲�Ʒ�޷��ٱ�Ϊ����״̬

			return getNotFoundTemplate(request);
		}

		int pagegroupId = ParamUtil.getIntParameter(request,
				ParameterConstants.PAGEGROUP_ID, -1);

		String page = ParamUtil.getParameter(request, ParameterConstants.PAGE);

		if (ParameterConstants.PAGE_DETAIL.equals(page)) {// ����ҳģ��
			if(!isResourceShowable(request)){
				return getNotFoundTemplate(request);
			}
			int columnId = ParamUtil.getIntParameter(request,
					ParameterConstants.COLUMN_ID, -1);
			if (columnId != -1) {
				Columns column = getBussinessService(request).getColumns(
						columnId);
				if (isColumnShowable(column)) {
					// Integer templateId = column.getResOneTempId();
					// Template template =
					// getBussinessService(request).getTemplate(templateId);
					// if(isTemplateExist(template.getDownPageId())){//ȡģ�����������ģ��
					// return
					// getTemplateContent(request,template.getDownPageId());
					// }else{//ȡ��Ʒ����������ģ��

					int wapType = RequestUtil.getNeedWapType();
					if (pagegroupId != -1) {
						PageGroup pg = getBussinessService(request)
								.getPageGroup(pagegroupId);
						Integer templateId = 0;
						if (isPageGroupShowable(pg)) {
							templateId = column.getDelOneTempId();
							if (wapType == 2
									&& isTemplateExist(column.getDelSecondTempId())) {
								templateId = column.getDelSecondTempId();
							}
							if (wapType == 3
									&& isTemplateExist(column.getDelThirdTempId())) {
								templateId = column.getDelThirdTempId();
							}
							if (!isTemplateExist(templateId)) {
								if (pg.getShowType() != 1) {
									wapType = 3;
								}

								templateId = getBussinessService(request)
										.getDefaultTemplate(
												TemplateType.DETAIL_PAGE,
												wapType);
							}
							System.out.println("TID::::"+templateId);
							return getTemplateContent(request, templateId);
						}
					}
					// }

				}
			}

		} else if (ParameterConstants.PAGE_RESOURCE.equals(page)) {// ��Դҳģ��
			if(!isResourceShowable(request)){
				return getNotFoundTemplate(request);
			}
			int columnId = ParamUtil.getIntParameter(request,
					ParameterConstants.COLUMN_ID, -1);
			int wapType = RequestUtil.getNeedWapType();
			if (columnId != -1) {
				Columns column = getBussinessService(request).getColumns(
						columnId);
				if (isColumnShowable(column)) {
					if (pagegroupId != -1) {
						PageGroup pg = getBussinessService(request)
								.getPageGroup(pagegroupId);
						if (isPageGroupShowable(pg)) {
							int templateId = column.getResOneTempId();
							if (wapType == 2
									&& isTemplateExist(column
											.getResSecondTempId())) {
								templateId = column.getResSecondTempId();
							}
							if (wapType == 3
									&& isTemplateExist(column
											.getResThirdTempId())) {
								templateId = column.getResThirdTempId();
							}
							if (!isTemplateExist(templateId)) {
								if (pg.getShowType() != 1) {
									wapType = 3;
								}
								templateId = getBussinessService(request)
										.getDefaultTemplate(
												TemplateType.RESOURCE_PAGE,
												wapType);
							}
							return getTemplateContent(request, templateId);
						}
					}
				}
			}

		} else if (ParameterConstants.PAGE_COLUMN.equals(page)) {// ��Ŀҳģ��
			int columnId = ParamUtil.getIntParameter(request,
					ParameterConstants.COLUMN_ID, -1);
			int wapType = RequestUtil.getNeedWapType();
			if (columnId != -1) {
				Columns column = getBussinessService(request).getColumns(
						columnId);
				if (isColumnShowable(column)) {
					if (pagegroupId != -1) {
						PageGroup pg = getBussinessService(request)
								.getPageGroup(pagegroupId);
						if (isPageGroupShowable(pg)) {
							int templateId = column.getColOneTempId();
							if (wapType == 2
									&& isTemplateExist(column
											.getColSecondTempId())) {
								templateId = column.getColSecondTempId();
							}
							if (wapType == 3
									&& isTemplateExist(column
											.getColThirdTempId())) {
								templateId = column.getColThirdTempId();
							}
							if (!isTemplateExist(templateId)) {
								if (pg.getShowType() != 1) {
									wapType = 3;
								}
								templateId = getBussinessService(request)
										.getDefaultTemplate(
												TemplateType.COLUMN_PAGE,
												wapType);
							}
							return getTemplateContent(request, templateId);
						}
					}
				}
			}

		} else if (ParameterConstants.PAGE_PRODUCT.equals(page)) {// ��ҳģ��

			int wapType = RequestUtil.getNeedWapType();
			if (pagegroupId != -1) {
				PageGroup pg = getBussinessService(request).getPageGroup(
						pagegroupId);
				if (isPageGroupShowable(pg)) {
					int templateId = pg.getPkOneTempId();
					if (wapType == 2 && isTemplateExist(pg.getPkSecondTempId())) {
						templateId = pg.getPkSecondTempId();
					}
					if (wapType == 3 && isTemplateExist(pg.getPkThirdTempId())) {
						templateId = pg.getPkThirdTempId();
					}
					return getTemplateContent(request, templateId);
				}
			}

		} else {// ��ڵ�ַ
			String productId = ParamUtil.getParameter(request,
					ParameterConstants.PRODUCT_ID);
			if (StringUtils.isNotEmpty(productId)) {
				List<PackGroupProvinceRelation> pgs = getBussinessService(
						request).getPackGroupProvinceRelation(productId);
				PackGroupProvinceRelation pg = null;
				if (pgs == null || pgs.size() == 0) {
					return "";
				} else if (pgs.size() == 1) {
					pg = pgs.get(0);
				} else {
					// �������䣬���û�к��ʵĵ���,ѡ��ȫ����ҳ����
					String areaId = RequestUtil.getAreaId();
					for (PackGroupProvinceRelation rel : pgs) {
						if (rel.getAid().equals(areaId)) {
							pg = rel;
							break;
						}
						if (rel.getAid().equals("001")) {
							pg = rel;
						}
					}
				}
				String url = request.getRequestURL().toString();
				StringBuilder sb = new StringBuilder();
				sb.append(url);
				sb.append("?");
				sb.append(ParameterConstants.PAGE);
				sb.append("=");
				sb.append(ParameterConstants.PAGE_PRODUCT);
				sb.append("&");
				sb.append(ParameterConstants.PRODUCT_ID);
				sb.append("=");
				sb.append(productId);
				sb.append("&");
				sb.append(ParameterConstants.PAGEGROUP_ID);
				sb.append("=");
				sb.append(pg.getPgid());
				sb.append("&");
				sb.append(ParameterConstants.AREA_ID);
				sb.append("=");
				sb.append(pg.getAid());
				sb.append("&");

				String channelId = ParamUtil.getParameter(request,
						ParameterConstants.CHANNEL_ID);
				if (StringUtils.isEmpty(channelId) || "null".equals(channelId)) {
					channelId = getBussinessService(request)
							.getDefaultChannelId(productId);
				}
				sb.append(ParameterConstants.CHANNEL_ID);
				sb.append("=");
				sb.append(channelId);
				sb.append("&");

				URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
				URLUtil.trimURL(sb);
				Redirect.sendRedirect(sb.toString());
				return "";

			}
		}
		return getNotFoundTemplate(request);
	}

	public boolean isResourceShowable(HttpServletRequest request) {
		String resourceId = URLUtil.getResourceId(request);
		if(StringUtils.isEmpty(resourceId))
			return false;
		ResourceAll resource = getResourceService(request).getResource(resourceId);
		if(resource == null)
			return false;
		return true;
	}

	/**
	 * �жϲ�Ʒ�Ƿ�ɼ�
	 * 
	 * @param request
	 * @return
	 */
	private boolean isProductShowable(HttpServletRequest request) {
		Product product = RequestUtil.getProduct();
		if (product == null) {
			String productId = ParamUtil.getParameter(request,
					ParameterConstants.PRODUCT_ID);
			if (StringUtils.isEmpty(productId)) {// Ԥ��ʹ��
				return true;
			} else {
				return false;
			}
		}
		if (product.getStatus().equals(Constants.PRODUCTSTATUS_PUBLISH)) {
			return true;
		} else if (product.getStatus().equals(Constants.PRODUCTSTATUS_OFFLINE)) {
			return false;
		} else if (RequestUtil.getPersonInfo().size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �ж�ҳ�����Ƿ�ɼ�
	 * 
	 * @param pg
	 * @return
	 */
	private boolean isPageGroupShowable(PageGroup pg) {
		if (pg == null
				|| pg.getPkStatus().equals(Constants.PRODUCTSTATUS_OFFLINE)) {
			return false;
		} else if (pg.getPkStatus().equals(Constants.PRODUCTSTATUS_PUBLISH)
				|| RequestUtil.getPersonInfo().size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �ж���Ŀ�Ƿ�ɼ�
	 * 
	 * @param column
	 * @return
	 */
	private boolean isColumnShowable(Columns column) {
		if (column == null
				|| !column.getStatus().equals(Constants.COLUMNSTATUS_PUBLISH)) {
			return false;
		} else {
			return isPageGroupShowable(column.getPagegroup());
		}
	}

	/**
	 * ��ȡҳ�治����ģ��
	 * 
	 * @param request
	 * @return
	 */
	private String getNotFoundTemplate(HttpServletRequest request) {
		Variables var = getBussinessService(request).getVariables(
				PAGE_NOT_FOUND);
		Template tmpl = getBussinessService(request).getTemplate(
				Integer.parseInt(var.getValue()));
		return tmpl.getContent();
	}

	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append("ss&ss");
		sb.append("12&");
		// System.out.println(sb.charAt(sb.length()-1));
		if (sb.charAt(sb.length() - 1) == '&') {
			sb.deleteCharAt(sb.length() - 1);
		}

		System.out.println(sb.toString());
	}

	private boolean isTemplateExist(Integer tid) {
		if (tid != null && tid > 0)
			return true;
		return false;
	}

	/**
	 * ��ȡģ������,���ģ��δ���߲��ҷǰ������û�������ҳ�治����ģ��
	 * 
	 * @param request
	 * @param templateId
	 * @return
	 */
	private String getTemplateContent(HttpServletRequest request,
			Integer templateId) {
		Template tmpl = getBussinessService(request).getTemplate(templateId);

		int preview = ParamUtil.getIntParameter(request, "preview", -1);
		if (preview == 1) {
			return tmpl.getPreContent();
		}
		if (tmpl.getStatus().equals(Constants.STATUS_PUBLISH)
				|| RequestUtil.getPersonInfo().size() > 0
				|| tmpl.getPreStatus().equals(Constants.STATUS_PUBLISH)) {
			return tmpl.getContent();
		} else {
			return getNotFoundTemplate(request);
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
	private ResourceService getResourceService(HttpServletRequest request) {
		if (resourceService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			resourceService = (ResourceService) wac
					.getBean("resourceService");
		}
		return resourceService;
	}
}
