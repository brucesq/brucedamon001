package com.hunthawk.reader.pps.basic;

import java.io.UnsupportedEncodingException;
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

import com.aspire.gentox.rollrule.DefaultRollRule;
import com.aspire.gentox.rollrule.IRollRule;
import com.aspire.gentox.rollrule.IntervalRandomRollRule;
import com.aspire.gentox.rollrule.IntevalPageRollRule;
import com.aspire.gentox.rollrule.PageRollRule;
import com.aspire.gentox.rollrule.PushRollRule;
import com.aspire.gentox.rollrule.RandomRollRule;
import com.aspire.gentox.rollrule.RollRuleContext;
import com.aspire.gentox.rollrule.RollRuleManager;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.statistics.StatData;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.InteractiveService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
/**
 * �ȴ��б��ǩ
 * ��ǩ����:hotWord_Roll
 * ����˵����
 * 		showType ��ʾ���ͣ�11 ������12 ���ߣ�13 �ؼ��֣�Ĭ�� ������
 * 		//timeType ʱ�����ͣ�0 �����У�3 �����У�2 �����У�1 �����У�
 * 		restype:  �μ�ResourceType�ж���(property=2)  ����ȷ����ѯĿ��(�������������ݿ��)
 * 					1ͼ�飬2��ֽ��3��־��4������5������6��Ƶ
 * 		isRoll  �Ƿ���ѯ����-1 ��1 �ǣ�			
 *  	rollCount ��ѯ��ʾ������ϵͳĬ��5����
 * 		columnId:	��ĿID �����м���������Դ�б�(property=2)
 * 		statDatas:�ȴ��б� �ȴ�1-�ȴ�2-...�������������
 * @author yuzs
 * 2009-11-10
 *
 */
public class HotWordRollTag extends BaseTag {
	private BussinessService bussinessService;
	private InteractiveService interactiveService;
	/** ��ʾ����ѭ���� */
	private int rollCount;

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		
		int columnId=getIntParameter("columnId",-1);		
		Columns column = getBussinessService(request).getColumns(columnId); // ��Ŀ
		// ����ò�����Ŀ ��ֱ�ӷ���
		if (column == null) {
			TagLogger.debug(tagName, "��ȡ��Ŀ��Ϣʧ��", request.getQueryString(), null);
			return new HashMap();
		}
		int showType = getIntParameter("showType",0);
		int timeType = getIntParameter("timeType",0);
		int restype=getIntParameter("restype",1);//Ĭ����ͼ��  
		int searchby=-1;
		List list = null;
		int isRoll  = getIntParameter("isRoll",-1);
		String statDatas = getParameter("statDatas","");
		rollCount = getIntParameter("rollCount", -1);
		if (rollCount < 1) {
			rollCount = 5;
		}
		
		String[] statDataMums= new String[]{};
		if(statDatas==null || StringUtils.isEmpty(statDatas)){
			TagLogger.debug(tagName, "δ��Ӳ�����ѭ���ȴ�",request.getQueryString(), null);
			return new HashMap();
		}else{
			statDataMums=statDatas.split("-");
		}
		List<String> statDataList = new ArrayList<String>();
		if(statDataMums.length>0){
			for(int i=0;i<statDataMums.length;i++){
				statDataList.add(i,statDataMums[i]);
			}
		}
		if(1==isRoll){
			//��ѯ
			String ruleId = "statDatas_"+ rollCount+ "_"
			+ statDataMums.length + "_1x_" + 2;
			RollRuleManager rrm = RollRuleManager.getInstance();
			IRollRule rollRule  = new RandomRollRule(ruleId);
			rrm.addRollRule(rollRule);
			rollRule = rrm.getRollRule(ruleId);
			RollRuleContext rrc = new RollRuleContext(Long.valueOf(statDataMums.length),
					1, Integer.valueOf(rollCount));
			
			long[] indexes = rollRule.obtainIndexes(rrc);
			list = getIndexResourceBagRelations(indexes, statDataList);
			System.out.println("-----������ѯ----��"+"��ѯ������"+statDataMums.length+"����ѯ������"+"rollCount"+"�б��С:"+list.size());
		}else{//����ѯ
			list = statDataList;
		}
	
		Map resultMap = new HashMap();
		String result = "";
		/** ����б���Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		Iterator it = list.iterator();
		while(it.hasNext()){
			loop++;
			//StatData sd = (StatData)it.next();
			String statDataContent = (String)it.next();
			StringBuilder sb=new StringBuilder();
			sb.append(request.getContextPath());
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append(ParameterConstants.PAGE_COLUMN);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			sb.append(ParameterConstants.COLUMN_ID);
			sb.append("=");
			sb.append(columnId);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_TYPE);
			sb.append("=");
			sb.append(restype);
			if(showType==11)
				searchby = 1;
			if(showType==12)
				searchby = 2;
			if(showType==13)
				searchby = 4;
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_TYPE);
			sb.append("=");
			sb.append(searchby);
			
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_PARAM_VALUE);
			sb.append("=");
			try {
				
				sb.append(java.net.URLEncoder.encode(statDataContent.trim(),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/** ���浥����¼ */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", sb.toString());
			//obj.put("title",author.getName());
			try {
				String title = StrUtil.getLimitStr(statDataContent, ParameterConstants.AUTHOR_NAME_BYTES, ParameterConstants.REPLACE_SYMBOL);
				title = StrUtil.toUnicode(title);
				obj.put("title",title );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lsRess.add(obj);
		}
		map.put("objs", lsRess);
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		
		//result = VmInstance.getInstance().parseVM(map, this);
		//resultMap.put(TagUtil.makeTag(tagName), result);
		return resultMap;
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
	
	private InteractiveService getInteractiveService(HttpServletRequest request) {
		if (interactiveService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			interactiveService = (InteractiveService) wac
					.getBean("interactiveService");
		}
		return interactiveService;
	}
	
	private List getIndexResourceBagRelations(long[] indexes, List list) {
		List result = new ArrayList();
		for (int i = 0; i < indexes.length; i++) {
			result.add(list.get(Integer.parseInt(String.valueOf(indexes[i]))));
		}
		return result;
	}
	public int getRollCount() {
		return rollCount;
	}

	public void setRollCount(int rollCount) {
		this.rollCount = rollCount;
	}
	
}
