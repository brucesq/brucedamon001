/**
 * 
 */
package com.hunthawk.reader.pps.column;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * @author liuxh
 * 标签名称：column
 * 功能说明：栏目属性标签
 * 参数说明：
 * 		property:栏目基本属性
 *		split:分隔符号
 *		columnId：栏目ID 
 */
public class ColumnPropertyTag extends BaseTag {

	private BussinessService bussinessService;
	private String split;
	
	public String getSplit() {
		return split;
	}
	public void setSplit(String split) {
		this.split = split;
	}
	/* (non-Javadoc)
	 * @see com.hunthawk.tag.BaseTag#parseTag(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		split = getParameter("split", "");
		String column_id=getParameter("columnId","");
		if(column_id==null || StringUtils.isEmpty(column_id)){
			/**得到当前栏目ID*/
			column_id=URLUtil.getURLvalue(request.getQueryString(), ParameterConstants.COLUMN_ID);
			try{
				Integer.parseInt(column_id);
			}catch(Exception ex){
				TagLogger.debug(tagName, "获取栏目ID失败", request.getQueryString(), null);
				return new HashMap();
			}
		}
		Columns column=getBussinessService(request).getColumns(Integer.parseInt(column_id));
		if(column==null){
			TagLogger.debug(tagName, "id为"+column_id+"的栏目不存在", request.getQueryString(), null);
			return new HashMap();
		}
		String property=getParameter("property","");
		if (StringUtils.isEmpty(property)) {
			TagLogger.debug(tagName, "property属性为空", request.getQueryString(),null);
		} else {
			if(property.equalsIgnoreCase("title")){
				return columnTitle(request,tagName,column);
			}else if(property.equalsIgnoreCase("icon")){
				return columnIcon(request,tagName,column);
			}else if(property.equalsIgnoreCase("comment")){
				return columnComment(request,tagName,column);
			}
		}
		return new HashMap();
	}
	private Map columnTitle(HttpServletRequest request,
			String tagName,Columns column){
		Map resultMap = new HashMap();
		String title = column.getTitle();
		resultMap.put(TagUtil.makeTag(tagName), title + split);
		return resultMap;
	} 
	private Map columnIcon(HttpServletRequest request,
			String tagName,Columns column){
		Map resultMap = new HashMap();
		String icon_id=column.getIcon();
		if(icon_id==null || StringUtils.isEmpty(icon_id))
			return new HashMap();
		/**得到icon的引用路径*/
		String url = getBussinessService(request).getVariables("media_url").getValue();
		Material mater =  getBussinessService(request).getMaterial(Integer.parseInt(icon_id));
		String imgName=mater.getFilename()+"." + mater.getExtName();
		String imgUrl="";
		if (imgName.toLowerCase().matches("[^.]+\\.(png|jpg|gif|jpeg)")) {
			imgUrl=url+imgName;
		}else{
			TagLogger.debug(tagName,"图片格式错误", request.getQueryString(), null);
			return new HashMap();
		}
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		
		Map velocityMap = new HashMap();
		velocityMap.put("title", column.getName());
		velocityMap.put("imgUrl", imgUrl);
		velocityMap.put("columnUrl", getColumnUrl(request,column.getId()));
		String result = DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		return resultMap;
	} 
	private Map columnComment(HttpServletRequest request,
			String tagName,Columns column){
		Map resultMap = new HashMap();
		String comment = column.getComment();
		resultMap.put(TagUtil.makeTag(tagName), comment + split);
		return resultMap;
	} 
	private String getColumnUrl(HttpServletRequest request,int column_id){
		StringBuilder columnUrl=new StringBuilder();
		columnUrl.append(request.getContextPath());
		columnUrl.append(ParameterConstants.PORTAL_PATH);
		columnUrl.append("?");
		columnUrl.append(ParameterConstants.PAGE);
		columnUrl.append("=");
		columnUrl.append(ParameterConstants.PAGE_COLUMN);
		columnUrl.append("&");
		URLUtil.append(columnUrl, ParameterConstants.PRODUCT_ID, request);
		URLUtil.append(columnUrl, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(columnUrl, ParameterConstants.AREA_ID, request);
		columnUrl.append(ParameterConstants.COLUMN_ID);
		columnUrl.append("=");
		columnUrl.append(column_id);
		columnUrl.append("&");
		URLUtil.append(columnUrl, ParameterConstants.CHANNEL_ID, request);
		URLUtil.trimURL(columnUrl);
		return columnUrl.toString();
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
}
