package com.hunthawk.reader.pps.resource;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;
/**
 * ��ʾ�������±���  ֻ�б�ֽ�Ż���ʾ�˱�ǩ
 * ��ǩ���ƣ�mnshowtitle
 * @author liuxh
 *
 */
public class MNShowTitleTag extends BaseTag {

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		//�õ���ԴID �ж��Ƿ��Ǳ�ֽ
		int showType=Integer.parseInt(URLUtil.getResourceId(request).substring(0,1));
		if(showType==ResourceType.TYPE_NEWSPAPERS){//��ֽ
			if(request.getParameter(ParameterConstants.TOME_ID)!=null){
				TagLogger.debug(tagName, "��idΪ��", request.getQueryString(), null);
				return new HashMap();
			}
			String flag=request.getParameter(ParameterConstants.SHOW_FLAG);
			String showValue=flag==null?"0":(flag.equals("0")?"1":"0");
			String title=flag==null?"�������±���":(flag.equals("0")?"��ʾ���±���":"�������±���");
			StringBuilder builder=new StringBuilder();
			builder.append(request.getContextPath());
			builder.append(ParameterConstants.PORTAL_PATH);
			builder.append("?");
			builder.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.SHOW_FLAG,ParameterConstants.PAGE_NUMBER));
			builder.append("&");
			builder.append(ParameterConstants.SHOW_FLAG);
			builder.append("=");
			builder.append(showValue);
			Map velocityMap = new HashMap();
			
			velocityMap.put("title", title);
			velocityMap.put("url", URLUtil.trimUrl(builder).toString());
			Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
					.parseVM(velocityMap, this));
			return resultMap;
		}else{
			return new HashMap();
		}
		
	}

}
