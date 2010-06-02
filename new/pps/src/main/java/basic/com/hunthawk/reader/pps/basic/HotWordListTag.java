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
 * ��ǩ����:hotWord_list
 * ����˵����
 * 		showType ��ʾ���ͣ�11 ������12 ���ߣ�13 �ؼ��֣�Ĭ�� ������
 * 		timeType ʱ�����ͣ�0 �����У�3 �����У�2 �����У�1 �����У�
 * 		restype:  �μ�ResourceType�ж���(property=2)  ����ȷ����ѯĿ��(�������������ݿ��)
 * 					1ͼ�飬2��ֽ��3��־��4������5������6��Ƶ
 * 		isRoll  �Ƿ���ѯ����-1 ��1 �ǣ�			
 *  		totalCount  ��ѯ������������ǰ����λ��
 *  	rollCount ��ѯ��ʾ������ϵͳĬ��5����
 * 		columnId:	��ĿID �����м���������Դ�б�(property=2)

 * 			
 * @author yuzs
 *
 */
public class HotWordListTag extends BaseTag {
	private BussinessService bussinessService;
	private InteractiveService interactiveService;
	private static final int DEFAULT_SIZE=10;
	/** �����ѭ��Χ */
	private static final int MAXNUMBER_RANGE = 200;

	/** Ĭ�Ϲ�����ʾ���� */
	private static final int DEFAULT_ROLL_COUNT = 5;
	/** ��ʾ����ѭ���� */
	private int rollCount;
	
	/** ������ѭ������ */
	private int totalCount;
	
	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
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
		int isRoll  = getIntParameter("isRoll",-1);
		int totalCount = getIntParameter("totalCount",50);//Ĭ����ѯ��������
		
		int currentPage=request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request.getParameter(ParameterConstants.PAGE_NUMBER));
//		int pageSize=getIntParameter("pageSize",DEFAULT_SIZE);
		
		rollCount = getIntParameter("rollCount", -1);
		if (rollCount < 1) {
			rollCount = DEFAULT_ROLL_COUNT;
		}
		
		List<StatData> statDatas = null;
		List list = null;
		int paraTotalCount = -1;
		if(isRoll==1){  //��ѯ ���� �����ѯ����
			// ��ѭ ���û�ѡ��������ѭ������Ϊ��ҳ��ѯ��sizeֵ
			// ȡ�����������ļ�¼����
			int recordCount =  getInteractiveService(request).getStatDataListCount(showType, timeType);
			//int recordCount = 100;
			totalCount = totalCount > MAXNUMBER_RANGE ? MAXNUMBER_RANGE: totalCount;// ���б߽�У��
			// �����󷶳볬����¼���������ΧΪ��¼����
			totalCount = totalCount > recordCount ? recordCount : totalCount;	
			
			currentPage=1;
	
			statDatas = getInteractiveService(request).getStatDataList(showType, timeType,totalCount);	

			paraTotalCount = totalCount;
			String ruleId = "statDatas_"+paraTotalCount + "_"
					+ rollCount + "_1x_" + 2;
			RollRuleManager rrm = RollRuleManager.getInstance();
			IRollRule rollRule  = new RandomRollRule(ruleId);
			rrm.addRollRule(rollRule);
			rollRule = rrm.getRollRule(ruleId);
			RollRuleContext rrc = new RollRuleContext(Long.valueOf(totalCount),
					currentPage, Integer.valueOf(rollCount));
			
			long[] indexes = rollRule.obtainIndexes(rrc);
		/*	if (resourcePackReleation.size() < indexes.length) {
				resourcePackReleation = getResourceService(request).getResourcePackReleationsByOrder(resourcePackId,currentPage - 1 < 0 ? 1 : currentPage - 1,totalCount,order,totalCount);
			}*/
			list = getIndexResourceBagRelations(indexes, statDatas);
			System.out.println("�ȴ���ѭ ---->;��¼����="+recordCount+";�û�ѡȡ��ѭ��Χ="+totalCount+";��ѭ����="+2+";ҳ/��="+rollCount);			
		}else{
			statDatas = getInteractiveService(request).getStatDataList(showType, timeType,rollCount);//ֻ��ѯǰ����	
			list = statDatas;	
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
			StatData sd = (StatData)it.next();
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
				
				sb.append(java.net.URLEncoder.encode(sd.getContent().trim(),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/** ���浥����¼ */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", sb.toString());
			//obj.put("title",author.getName());
			try {
				String title = StrUtil.getLimitStr(sd.getContent(), ParameterConstants.AUTHOR_NAME_BYTES, ParameterConstants.REPLACE_SYMBOL);
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

	public int getRollCount() {
		return rollCount;
	}

	public void setRollCount(int rollCount) {
		this.rollCount = rollCount;
	}

	private List getIndexResourceBagRelations(long[] indexes, List list) {
		List result = new ArrayList();
		for (int i = 0; i < indexes.length; i++) {
			result.add(list.get(Integer.parseInt(String.valueOf(indexes[i]))));
		}
		return result;
	}
	
}
