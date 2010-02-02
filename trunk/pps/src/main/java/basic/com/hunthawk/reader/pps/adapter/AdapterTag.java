package com.hunthawk.reader.pps.adapter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.adapter.Adapter;
import com.hunthawk.reader.domain.adapter.AdapterRule;
import com.hunthawk.reader.domain.adapter.AdapterType;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.service.AdapterService;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.GuestService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.process.Redirect;

/**
 * ��������ǩ
 * 
 * @author penglei
 * 
 */
public class AdapterTag extends BaseTag {

	private Integer adapterId = 0;
	private String mobile = "";
	private Integer adapterRuleId = 0;

	private BussinessService bussinessService;

	private AdapterService adapterService;

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

	private AdapterService getAdapterService(HttpServletRequest request) {
		if (adapterService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			adapterService = (AdapterService) wac.getBean("adapterService");
		}
		return adapterService;
	}

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// ��ȡ������ID
		adapterId = Integer.parseInt(this.getParameter("adapterId", "0"));

		// ��ȡ����������ID
		adapterRuleId = Integer.parseInt(this
				.getParameter("adapterRuleId", "0")); // û��Ĭ��Ϊ0
		// ����ֻ���
		this.mobile = RequestUtil.getMobile();

		Map map = new HashMap();
		if (adapterId == null || adapterId < 1) {
			return map;
		}
		// ��ȡ������
		Adapter adapter = getAdapterService(request).getAdapterById(adapterId);
		if (adapter == null) {
			return map;
		}

		if (adapter.getAdapterTypeId() == AdapterType.GOTO_ADAPTER_TYPE_AREA) {
			// ��������
			map = doAdapterAreas(request, tagName);

		} else if (adapter.getAdapterTypeId() == AdapterType.GOTO_ADAPTER_TYPE_TIME) {
			// �̶�ʱ������
			map = doAdapterTime(request, tagName);
		} else if (adapter.getAdapterTypeId() == AdapterType.GOTO_ADAPTER_TYPE_UA) {
			// UA����
			map = doAdapterUA(request, tagName);

		} else if (adapter.getAdapterTypeId() == AdapterType.GOTO_ADAPTER_TYPE_WEEK_TIME) {
			// ������
			map = doAdapterWeek(request, tagName);
		} else if (adapter.getAdapterTypeId() == AdapterType.GOTO_ADAPTER_TYPE_HOUR_TIME) {
			// ������
			map = doAdapterDay(request, tagName);
		}
		return map;
	}

	private Map doAdapterAreas(HttpServletRequest request, String tagName) {
		Map resultMap = new HashMap();
		Map map = new HashMap();
		// ��ȡ�����������
		AdapterRule adapterRule = getAdapterService(request)
				.getAdapterRuleWithAreas(mobile, adapterRuleId, adapterId);
		// ������
		map = doAdapter(map, adapterRule);

		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		String result = DBVmInstance.getInstance().parseVM(map, this, tagTem);
		// result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName),
				StringUtils.isNotEmpty(result) ? result : "");

		return resultMap;
	}

	// ����ʱ��������
	private Map doAdapterTime(HttpServletRequest request, String tagName) {
		Map resultMap = new HashMap();
		Map map = new HashMap();

		AdapterRule adapterRule = getAdapterService(request)
				.getAdapterRuleWithTime(adapterRuleId, adapterId);
		// ������
		map = doAdapter(map, adapterRule);

		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		String result = DBVmInstance.getInstance().parseVM(map, this, tagTem);
		// result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName),
				StringUtils.isNotEmpty(result) ? result : "");

		return resultMap;
	}

	// ��UA����
	private Map doAdapterUA(HttpServletRequest request, String tagName) {
		Map resultMap = new HashMap();
		Map map = new HashMap();

		String ua = RequestUtil.getUa();
		// ������
		AdapterRule adapterRule = getAdapterService(request)
				.getAdapterRuleWithUA(ua, adapterRuleId, adapterId);
		map = doAdapter(map, adapterRule);

		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		String result = DBVmInstance.getInstance().parseVM(map, this, tagTem);
		// result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName),
				StringUtils.isNotEmpty(result) ? result : "");

		return resultMap;

	}

	// ������
	private Map doAdapterWeek(HttpServletRequest request, String tagName) {
		Map resultMap = new HashMap();
		Map map = new HashMap();

		// ������
		AdapterRule adapterRule = getAdapterService(request)
				.getAdapterRuleWithWeek(adapterRuleId, adapterId);

		map = doAdapter(map, adapterRule);

		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		String result = DBVmInstance.getInstance().parseVM(map, this, tagTem);
		// result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName),
				StringUtils.isNotEmpty(result) ? result : "");

		return resultMap;

	}

	// ������
	private Map doAdapterDay(HttpServletRequest request, String tagName) {
		Map resultMap = new HashMap();
		Map map = new HashMap();

		// ������
		AdapterRule adapterRule = getAdapterService(request)
				.getAdapterRuleWithDay(adapterRuleId, adapterId);

		map = doAdapter(map, adapterRule);

		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		String result = DBVmInstance.getInstance().parseVM(map, this, tagTem);
		// result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName),
				StringUtils.isNotEmpty(result) ? result : "");

		return resultMap;

	}

	// ������ת����������
	private Map doAdapter(Map map, AdapterRule adapterRule) {
		if (adapterRule == null) {
			map.put("type", -1);
			return map;
		}
		if (adapterRule.getGotoType() == 0) {
			// ֱ����ת
			Redirect.sendRedirect(adapterRule.getLinkUrl());
			map.put("type", adapterRule.getGotoType());
		} else if (adapterRule.getGotoType() == 1) {
			// ������ת
			map.put("linkName", adapterRule.getLinkTitle());
			map.put("linkUrl", adapterRule.getLinkUrl());
			map.put("type", adapterRule.getGotoType());
			return map;
		} else if (adapterRule.getGotoType() == 2) {
			// �������
			map.put("blockCode", adapterRule.getBlockCode());
			map.put("type", adapterRule.getGotoType());
			return map;
		}
		map.put("type", -1);
		return map;
	}

}
