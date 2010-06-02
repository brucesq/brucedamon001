package com.hunthawk.reader.pps.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.pps.CoverPreview;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.PpsUtil;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

public class PertinenceTag extends BaseTag {

	private String count;
	private String split;
	private ResourceService resourceService;
	private BussinessService bussinessService;

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
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
			bussinessService = (BussinessService) wac
					.getBean("bussinessService");
		}
		return bussinessService;
	}

	public Map parseTag(HttpServletRequest request, String tagName) {
		//String mix = getParameter("mix", "1");// // title组合 默认只显示资源名称不做组合
		//count = this.getParameter("count", "5");
		String columnID = request.getParameter(ParameterConstants.COLUMN_ID);
		String resID = URLUtil.getResourceId(request);
		ResourceAll resource = null;
		Map resultMap = new HashMap();
		Map map = new HashMap();
		String result = "";
		List<ResourcePackReleation> rprList = null;
		List resultList = new ArrayList();
		
		if (StringUtils.isNotEmpty(resID)) {
			resource = getResourceService(request).getResource(resID);
			
			try {
			/*	rprList = getResourceService(request).findPertinence(columnID,
						resource, count);*/
				rprList = getResourceService(request).findDivisions(columnID, resource,true);
				if(rprList == null ||  rprList.size() <= 0)
					return new HashMap();
			} catch (Exception e) {
				TagLogger.debug(tagName, "没有关联书部",request.getQueryString(), null);
				return new HashMap();
				//e.printStackTrace();
			}
			if (rprList != null && rprList.size() != 0) {
				for (ResourcePackReleation rpr : rprList) {
					ResourceAll resourceAll = getResourceService(request)
							.getResource(rpr.getResourceId());
					// 拼写url start
					//System.out.println("---关联资源-----"+resourceAll+"-------对应资源书部-------"+resourceAll.getDivision()+"::::"+resourceAll.getDivisionContent());
					StringBuilder sb = new StringBuilder();
					sb.append(request.getContextPath());
					sb.append(ParameterConstants.PORTAL_PATH);
					sb.append("?");
					sb.append(ParameterConstants.PAGE);
					sb.append("=");
					sb.append("r");
					sb.append("&");
					URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
					URLUtil
							.append(sb, ParameterConstants.PAGEGROUP_ID,
									request);
					URLUtil.append(sb, ParameterConstants.AREA_ID, request);
					URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
					sb.append(ParameterConstants.FEE_BAG_ID);
					sb.append("=");
					sb.append(rpr.getPack().getId());
					sb.append("&");
					sb.append(ParameterConstants.FEE_BAG_RELATION_ID);
					sb.append("=");
					sb.append(rpr.getId());
					sb.append("&");
					sb.append(ParameterConstants.RESOURCE_ID);
					sb.append("=");
					sb.append(rpr.getResourceId());
					sb.append("&");
					URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
					URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
					if (rpr.getFeeId() != null) {
						sb.append(ParameterConstants.FEE_ID);
						sb.append("=");
						sb.append(rpr.getFeeId());
						sb.append("&");
					}
					sb.append(ParameterConstants.PAGE_NUMBER);
					sb.append("=");
					sb.append("1");

					// 拼写URL end
					StringBuilder linkname = new StringBuilder();
					/*if (!mix.equals("1")) {
						List<String> mixparams = PpsUtil.getParameters(mix);
						if (mixparams.size() < 1) {
							linkname.append(resourceAll.getName());
						}
						for (String str : mixparams) {

							if (str.equalsIgnoreCase("name")) {
								linkname.append(resourceAll.getName());
							} else if (str.equalsIgnoreCase("bComment")) {
								linkname
										.append(resourceAll.getBComment() == null ? ""
												: resourceAll.getBComment());
								linkname.append(":");
							} else if (str.equalsIgnoreCase("authorId")) {
								// 根据作者ID查询作者信息
								Integer[] authorIds = resourceAll
										.getAuthorIds();
								if (authorIds.length > 0) {
									for (int i = 0; i < authorIds.length; i++) {
										ResourceAuthor author = getResourceService(
												request).getResourceAuthor(
												resourceAll.getAuthorIds()[i]);
										if (author != null) {
											linkname.append("(");
											linkname
													.append((author
															.getPenName() == null
															|| StringUtils
																	.isEmpty(author
																			.getPenName()) ? author
															.getName()
															: author
																	.getPenName()));
											linkname.append(")");
											break;
										} else {
											continue;
										}
									}

								}
							} else if (str.equalsIgnoreCase("downnum")) {
								linkname.append("(");
								linkname
										.append(getResourceService(request)
												.getResourceVisits(
														resourceAll.getId()));
								linkname.append("次)");
							}
						}
					} else {
						linkname.append(resourceAll.getName());
					}*/
					linkname.append(resourceAll.getName());
					Map<String, Object> obj = new HashMap<String, Object>();
					obj.put("url", sb.toString());
					obj.put("title", linkname.toString());
					obj.put("resource", resourceAll); // 新添加上了 资源对象
					String imgUrl = CoverPreview.getPreview(
							getResourceService(request), resourceAll, 51);// 把预览图放进去
					obj.put("preview", imgUrl);
					resultList.add(obj);
				}

			}
			map.put("objs", resultList);
			map.put("strUtil", new StrUtil());
			map.put("previewUtil", new CoverPreview());
			map.put("service", getResourceService(request));
		}
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this, tagTem);

		// result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);

		return resultMap;
		// return null;
	}

}
