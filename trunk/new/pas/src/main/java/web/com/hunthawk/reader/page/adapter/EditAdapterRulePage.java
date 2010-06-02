package com.hunthawk.reader.page.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.adapter.AdapterRule;
import com.hunthawk.reader.domain.bussiness.SysTag;
import com.hunthawk.reader.domain.bussiness.SysTagType;
import com.hunthawk.reader.domain.bussiness.UserDefTag;
import com.hunthawk.reader.domain.device.UAGroup;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.page.guide.TagGuide;
import com.hunthawk.reader.page.util.PageUtil;
import com.hunthawk.reader.service.adapter.AdapterService;
import com.hunthawk.reader.service.bussiness.TemplateService;
import com.hunthawk.reader.service.guest.GuestService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * �༭�������������
 * 
 * @author penglei
 * 
 */
public abstract class EditAdapterRulePage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();

	@InjectObject("spring:adapterService")
	public abstract AdapterService getAdapterService();

	@InjectObject("spring:guestService")
	public abstract GuestService getGuestService();

	@InjectPage("adapter/ShowAdapterRulePage")
	public abstract ShowAdapterRulePage getShowAdapterRulePage();

	// set get���� start
	public abstract SysTagType getSysTagType();

	public abstract void setSysTagType(SysTagType sysTagType);

	public abstract TagGuide getSysTag();

	public abstract void setSysTag(TagGuide tag);

	public abstract Integer getAdapterId();

	public abstract void setAdapterId(Integer adapterId);

	public abstract void setAdapterTypeId(Integer adapterTypeId);

	public abstract Integer getAdapterTypeId();

	// set get���� end

	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return AdapterRule.class;
	}

	@Override
	protected boolean persist(Object object) {
		try {
			AdapterRule adapterRule = (AdapterRule) object;
			if (adapterRule.getBeginHour() != null
					&& adapterRule.getEndHour() != null) {
				if (adapterRule.getBeginHour().intValue() > adapterRule
						.getEndHour().intValue()) {
					throw new Exception("Сʱ�Ŀ�ʼʱ�䲻�ܴ��ڽ���ʱ��(���磺4�㣬��������С��4��)!");
				}
			}
			if (adapterRule.getId() == null) {
				adapterRule.setAdapterId(getAdapterId());
				adapterRule.setAdapterTypeId(getAdapterTypeId());
				getAdapterService().saveAdapterRule(adapterRule);
			} else {
				getAdapterService().updateAdapterRule(adapterRule);
			}

		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		getShowAdapterRulePage().setAdapterId(getAdapterId());
		getShowAdapterRulePage().setAdapterTypeId(getAdapterTypeId());
		return true;
	}

	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			setModel(new AdapterRule());

		}

	}

	public List<SysTagType> getSysTagTypes() {
		List<SysTagType> sysTagTypes = new ArrayList<SysTagType>();
		Variables variables = getSystemService().getVariables(
				Variables.SYSTAG_PERTAINT_TYPE);

		Map map = PageUtil.getMapFormString(variables.getValue());

		Set keySet = map.keySet();

		Iterator keyIt = keySet.iterator();

		while (keyIt.hasNext()) {
			String key = (String) keyIt.next();
			String value = (String) map.get(key);
			if (value.equals("userDefTag")) { // �Զ����ǩ
				Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
				HibernateExpression ex = new CompareExpression("status", 1,
						CompareType.Equal);
				expressions.add(ex); // ����ʹ��״̬�ı�ǩ

				List<UserDefTag> list = getTemplateService().getUserDefTagList(
						1, Integer.MAX_VALUE, "id", true, expressions);
				List<TagGuide> tags = new ArrayList<TagGuide>();
				for (int i = 0; i < list.size(); i++) {
					tags.add(new TagGuide(list.get(i)));
				}
				SysTagType sysTagType = new SysTagType();
				sysTagType.setType(key);
				sysTagType.setTagGuide(tags);

				sysTagTypes.add(sysTagType);

			} else {
				Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
				HibernateExpression ex = new CompareExpression("pertaintype",
						value, CompareType.Equal);
				expressions.add(ex);
				List<SysTag> list = getTemplateService().findSysTag(1,
						Integer.MAX_VALUE, "id", true, expressions);

				List<TagGuide> tags = new ArrayList<TagGuide>();
				for (int i = 0; i < list.size(); i++) {
					tags.add(new TagGuide(list.get(i), getExternalService()));
				}

				SysTagType sysTagType = new SysTagType();
				sysTagType.setType(key);
				sysTagType.setTagGuide(tags);
				sysTagTypes.add(sysTagType);
			}
		}

		return sysTagTypes;
	}

	public IPropertySelectionModel getGotoTypeList() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("ֱ����ת", 0);
		map.put("������ת", 1);
		map.put("�������", 2);
		return new MapPropertySelectModel(map);
	}

	public IPropertySelectionModel getWeekList() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("����һ", 1);
		map.put("���ڶ�", 2);
		map.put("������", 3);
		map.put("������", 4);
		map.put("������", 5);
		map.put("������", 6);
		map.put("������", 0);
		return new MapPropertySelectModel(map);
	}

	public IPropertySelectionModel getHourList() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < 24; i++) {
			map.put(i + "��", i);
		}

		return new MapPropertySelectModel(map);
	}

	public IPropertySelectionModel getUaGroupList() {
		List<UAGroup> uaGroupList = getGuestService().findUAGroupList(1,
				Integer.MAX_VALUE, "desc", false,
				new ArrayList<HibernateExpression>());

		Map<String, Integer> map = new HashMap<String, Integer>();
		for (UAGroup group : uaGroupList) {
			map.put(group.getDesc(), group.getId());
		}
		return new MapPropertySelectModel(map);
	}

	public IPropertySelectionModel getPrivileges() {

		return new MapPropertySelectModel(Constants.getAreaCode());
	}

	public void setSelectedAreas(List areasList) {
		AdapterRule adapterRule = (AdapterRule) getModel();
		String areas = "";
		if (areas != null && areasList.size() > 0) {
			for (int i = 0; i < areasList.size(); i++) {
				areas += areasList.get(i);
				if (areasList.size() > 1 && i != areasList.size() - 1) {
					areas += ",";
				}
			}
		}
		adapterRule.setAreas(areas);
	}

	public List getSelectedAreas() {
		if (isModelNew()) {// ������
			return new ArrayList();
		} else {// �޸ĵ�
			List list = new ArrayList();
			AdapterRule adapterRule = (AdapterRule) getModel();
			String areas = adapterRule.getAreas();
			String[] area = areas != null ? areas.split(",") : null;
			if (area != null && area.length > 0) {
				for (String value : area) {
					list.add(value);
				}
			}
			return list;

		}

	}

}
