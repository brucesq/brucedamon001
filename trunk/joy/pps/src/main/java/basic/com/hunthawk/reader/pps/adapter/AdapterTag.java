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
 * 适配器标签
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
		// 获取适配器ID
		adapterId = Integer.parseInt(this.getParameter("adapterId", "0"));

		// 获取适配器规则ID
		adapterRuleId = Integer.parseInt(this
				.getParameter("adapterRuleId", "0")); // 没有默认为0
		// 获得手机号
		this.mobile = RequestUtil.getMobile();

		Map map = new HashMap();
		if (adapterId == null || adapterId < 1) {
			return map;
		}
		// 获取适配器
		Adapter adapter = getAdapterService(request).getAdapterById(adapterId);
		if (adapter == null) {
			return map;
		}

		if (adapter.getAdapterTypeId() == AdapterType.GOTO_ADAPTER_TYPE_AREA) {
			// 地区适配
			map = doAdapterAreas(request, tagName);

		} else if (adapter.getAdapterTypeId() == AdapterType.GOTO_ADAPTER_TYPE_TIME) {
			// 固定时间适配
			map = doAdapterTime(request, tagName);
		} else if (adapter.getAdapterTypeId() == AdapterType.GOTO_ADAPTER_TYPE_UA) {
			// UA适配
			map = doAdapterUA(request, tagName);

		} else if (adapter.getAdapterTypeId() == AdapterType.GOTO_ADAPTER_TYPE_WEEK_TIME) {
			// 周适配
			map = doAdapterWeek(request, tagName);
		} else if (adapter.getAdapterTypeId() == AdapterType.GOTO_ADAPTER_TYPE_HOUR_TIME) {
			// 天适配
			map = doAdapterDay(request, tagName);
		}
		return map;
	}

	private Map doAdapterAreas(HttpServletRequest request, String tagName) {
		Map resultMap = new HashMap();
		Map map = new HashMap();
		// 获取地区适配规则
		AdapterRule adapterRule = getAdapterService(request)
				.getAdapterRuleWithAreas(mobile, adapterRuleId, adapterId);
		// 做适配
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

	// 根据时间做适配
	private Map doAdapterTime(HttpServletRequest request, String tagName) {
		Map resultMap = new HashMap();
		Map map = new HashMap();

		AdapterRule adapterRule = getAdapterService(request)
				.getAdapterRuleWithTime(adapterRuleId, adapterId);
		// 做适配
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

	// 做UA适配
	private Map doAdapterUA(HttpServletRequest request, String tagName) {
		Map resultMap = new HashMap();
		Map map = new HashMap();

		String ua = RequestUtil.getUa();
		// 做适配
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

	// 周适配
	private Map doAdapterWeek(HttpServletRequest request, String tagName) {
		Map resultMap = new HashMap();
		Map map = new HashMap();

		// 周适配
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

	// 天适配
	private Map doAdapterDay(HttpServletRequest request, String tagName) {
		Map resultMap = new HashMap();
		Map map = new HashMap();

		// 周适配
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

	// 根据跳转规则做适配
	private Map doAdapter(Map map, AdapterRule adapterRule) {
		if (adapterRule == null) {
			map.put("type", -1);
			return map;
		}
		if (adapterRule.getGotoType() == 0) {
			// 直接跳转
			Redirect.sendRedirect(adapterRule.getLinkUrl());
			map.put("type", adapterRule.getGotoType());
		} else if (adapterRule.getGotoType() == 1) {
			// 连接跳转
			map.put("linkName", adapterRule.getLinkTitle());
			map.put("linkUrl", adapterRule.getLinkUrl());
			map.put("type", adapterRule.getGotoType());
			return map;
		} else if (adapterRule.getGotoType() == 2) {
			// 区块代码
			map.put("blockCode", adapterRule.getBlockCode());
			map.put("type", adapterRule.getGotoType());
			return map;
		}
		map.put("type", -1);
		return map;
	}

}
