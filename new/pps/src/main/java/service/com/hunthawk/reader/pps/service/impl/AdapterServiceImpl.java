package com.hunthawk.reader.pps.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.adapter.Adapter;
import com.hunthawk.reader.domain.adapter.AdapterRule;
import com.hunthawk.reader.domain.adapter.AdapterType;
import com.hunthawk.reader.domain.device.MobileInfo;
import com.hunthawk.reader.pps.service.AdapterService;
import com.hunthawk.reader.pps.service.GuestService;

/**
 * 适配器service
 * 
 * @author penglei
 * 
 */

public class AdapterServiceImpl implements AdapterService {

	private static Logger logger = Logger.getLogger(GuestServiceImpl.class);

	private HibernateGenericController controller;

	private MemCachedClientWrapper memcached;

	private GuestService guestService;

	public void setGuestService(GuestService guestService) {
		this.guestService = guestService;
	}

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}

	/**
	 * 获得地区适配规则
	 * 
	 * @param mobile
	 *            手机号
	 * @return
	 * @author penglei
	 */
	public AdapterRule getAdapterRuleWithAreas(String mobile,
			Integer adapterRuleId, Integer adapterId) {
		AdapterRule adapterRule = null;
		MobileInfo mobileInfo = guestService.getMobileInfo(mobile);
		if (mobileInfo != null) {

			String area = mobileInfo.getArea();
			List<AdapterRule> list = null;
			String hql = "";
			String adapterRuleKey = Utility.getMemcachedKey(AdapterRule.class,
					adapterId.toString(), "list");
			try {
				list = (List<AdapterRule>) memcached
						.getAndSaveLocalLong(adapterRuleKey);
			} catch (Exception e) {
				logger.error("从缓存中获取适配列表失败", e);
			}
			if (list == null) {
				hql = "select ar from AdapterRule ar where ar.status = 1 and ar.adapterId = "
						+ adapterId + " and ar.areas is not null ";
				list = controller.findBy(hql);
				memcached.setAndSaveLong(adapterRuleKey, list,
						48 * MemCachedClientWrapper.HOUR);
			}

			if (list != null && list.size() > 0) {
				for (AdapterRule item : list) {
					if (item.getAreas().indexOf(area) >= 0) {
						adapterRule = item;
					}
				}

			}
		}
		// 做默认适配
		if (adapterRule == null) {
			adapterRule = doDefaultAdapter(adapterRule, adapterRuleId);
		}

		return adapterRule;
	}

	private AdapterRule doDefaultAdapter(AdapterRule adapterRule,
			Integer adapterRuleId) {

		if (adapterRuleId != null) {

			String adapterKey = Utility.getMemcachedKey(AdapterRule.class,
					adapterRuleId.toString());
			try {
				adapterRule = (AdapterRule) memcached
						.getAndSaveLocalLong(adapterKey);
				Integer adapterId = null;
				if (adapterRule != null) {
					adapterId = adapterRule.getAdapterId();
				}

				if (adapterId != null) {
					String adapterIdKey = Utility.getMemcachedKey(
							AdapterRule.class, adapterId.toString(), "adapter");

					Adapter adapter = (Adapter) memcached
							.getAndSaveLocalLong(adapterIdKey);

					if (adapter == null) {
						adapter = controller.get(Adapter.class, adapterId);
						memcached.setAndSaveLong(adapterIdKey, adapter,
								48 * MemCachedClientWrapper.HOUR);
					}
					if (adapter == null) {
						return null;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (adapterRuleId != null && adapterRuleId != 0) {
				adapterRule = controller.get(AdapterRule.class, adapterRuleId);
				memcached.setAndSaveLong(adapterKey, adapterRule,
						48 * MemCachedClientWrapper.HOUR);
				return adapterRule;
			}
		}
		return adapterRule;

	}

	/**
	 * 固定时间适配
	 */
	public AdapterRule getAdapterRuleWithTime(Integer adapterRuleId,
			Integer adapterId) {
		AdapterRule adapterRule = null;
		Date date = new Date();

		List<AdapterRule> list = null;
		String hql = "";
		String adapterRuleKey = Utility.getMemcachedKey(AdapterRule.class,
				adapterId.toString(), "list");
		// 从缓存中取列表
		try {
			list = (List<AdapterRule>) memcached
					.getAndSaveLocalLong(adapterRuleKey);
		} catch (Exception e) {
			logger.error("从缓存中获取适配列表失败", e);
		}

		if (list == null) {
			hql = "select ar from AdapterRule ar where ar.status = 1 and ar.adapterId = "
					+ adapterId + " and ar.beginTime is not null ";
			list = controller.findBy(hql);
			memcached.setAndSaveLong(adapterRuleKey, list,
					48 * MemCachedClientWrapper.HOUR);
		}
		if (list != null && list.size() > 0) {
			for (AdapterRule item : list) {
				if (date.getTime() >= item.getBeginTime().getTime()
						&& date.getTime() <= item.getEndTime().getTime()) {
					adapterRule = item;
				}
			}

		}
		// 做默认适配
		if (adapterRule == null) {
			adapterRule = doDefaultAdapter(adapterRule, adapterRuleId);
		}

		return adapterRule;

	}

	/**
	 * UA适配
	 */
	public AdapterRule getAdapterRuleWithUA(String ua, Integer adapterRuleId,
			Integer adapterId) {
		AdapterRule adapterRule = null;
		List<Integer> uaGroupIds = guestService.getUaGroupIdsByUa(ua);
		if (uaGroupIds != null && uaGroupIds.size() != 0) {

			List<AdapterRule> list = null;
			String hql = "";
			String adapterRuleKey = Utility.getMemcachedKey(AdapterRule.class,
					adapterId.toString(), "list");
			// 从缓存中取列表s
			try {
				list = (List<AdapterRule>) memcached
						.getAndSaveLocalLong(adapterRuleKey);
			} catch (Exception e) {
				logger.error("从缓存中获取适配列表失败", e);
			}

			if (list == null) {
				hql = "select ar from AdapterRule ar where ar.status=1 and ar.adapterId = "
						+ adapterId + " and ar.uaGroupId is not null ";
				list = controller.findBy(hql);
				memcached.setAndSaveLong(adapterRuleKey, list,
						48 * MemCachedClientWrapper.HOUR);
			}
			if (list != null && list.size() > 0) {
				for (AdapterRule item : list) {
					for (Integer id : uaGroupIds) {
						if (item.getUaGroupId().intValue() == id.intValue()) {
							adapterRule = item;
							return adapterRule;
						}
					}
				}

			}
		}

		// 做默认适配
		if (adapterRule == null) {
			adapterRule = doDefaultAdapter(adapterRule, adapterRuleId);
		}
		return adapterRule;
	}

	/**
	 * 周适配
	 * 
	 * @author penglei
	 */
	public AdapterRule getAdapterRuleWithWeek(Integer adapterRuleId,
			Integer adapterId) {
		AdapterRule adapterRule = null;
		List<AdapterRule> list = null;
		String hql = "";
		String adapterRuleKey = Utility.getMemcachedKey(AdapterRule.class,
				adapterId.toString(), "list");
		// 从缓存中取列表
		try {
			list = (List<AdapterRule>) memcached
					.getAndSaveLocalLong(adapterRuleKey);
		} catch (Exception e) {
			logger.error("从缓存中获取适配列表失败", e);
		}
		Calendar cal = Calendar.getInstance();
		Integer dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		Integer hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
		if (list == null) {
			hql = "select ar from AdapterRule ar where ar.status = 1 and ar.adapterId="
					+ adapterId
					+ " and ar.beginWeek is not null and ar.beginHour is not null";

			list = controller.findBy(hql);
			memcached.setAndSaveLong(adapterRuleKey, list,
					48 * MemCachedClientWrapper.HOUR);
		}

		if (list != null && list.size() > 0) {
			for (AdapterRule item : list) {
				if (item.getBeginWeek() < item.getEndWeek()) {
					if ((dayOfWeek >= item.getBeginWeek() && dayOfWeek <= item
							.getEndWeek())
							&& (hourOfDay >= item.getBeginHour() && hourOfDay <= item
									.getEndHour())) {
						adapterRule = item;
					}
				} else if (item.getBeginWeek() > item.getEndWeek()) {
					if ((dayOfWeek <= item.getBeginWeek() || dayOfWeek >= item
							.getEndWeek())
							&& (hourOfDay >= item.getBeginHour() && hourOfDay <= item
									.getEndHour())) {
						adapterRule = item;
					}
				} else {
					if ((dayOfWeek == item.getBeginWeek() && dayOfWeek == item
							.getEndWeek())
							&& (hourOfDay >= item.getBeginHour() && hourOfDay <= item
									.getEndHour())) {
						adapterRule = item;
					}
				}

			}

		}
		// 做默认适配
		if (adapterRule == null) {
			adapterRule = doDefaultAdapter(adapterRule, adapterRuleId);
		}

		return adapterRule;
	}

	/**
	 * 日适配
	 * 
	 * @author penglei
	 */
	public AdapterRule getAdapterRuleWithDay(Integer adapterRuleId,
			Integer adapterId) {
		AdapterRule adapterRule = null;
		List<AdapterRule> list = null;
		String hql = "";
		String adapterRuleKey = Utility.getMemcachedKey(AdapterRule.class,
				adapterId.toString(), "list");
		// 从缓存中取列表
		try {
			list = (List<AdapterRule>) memcached
					.getAndSaveLocalLong(adapterRuleKey);
		} catch (Exception e) {
			logger.error("从缓存中获取适配列表失败", e);
		}
		Calendar cal = Calendar.getInstance();
		Integer hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
		if (list == null) {
			hql = "select ar from AdapterRule ar where ar.status = 1 and ar.adapterId="
					+ adapterId + " and ar.beginHour is not null";

			list = controller.findBy(hql);
			memcached.setAndSaveLong(adapterRuleKey, list,
					48 * MemCachedClientWrapper.HOUR);
		}

		if (list != null && list.size() > 0) {
			for (AdapterRule item : list) {
				if (hourOfDay >= item.getBeginHour()
						&& hourOfDay <= item.getEndHour()) {
					adapterRule = item;
					break;
				}
			}

		}
		// 做默认适配
		if (adapterRule == null) {
			adapterRule = doDefaultAdapter(adapterRule, adapterRuleId);
		}

		return adapterRule;
	}

	public Adapter getAdapterById(Integer id) {
		String adapterKey = Utility.getMemcachedKey(AdapterRule.class, id
				.toString());
		Adapter adapter = null;
		try {
			adapter = (Adapter) memcached.getAndSaveLocalLong(adapterKey);
			if (adapter != null && adapter.getStatus() == 0) {
				return null;
			}
		} catch (Exception e) {
			logger.error("从缓存中获取适配列表失败", e);
		}
		if (adapter == null) {
			adapter = controller.get(Adapter.class, id);
			memcached.setAndSaveLong(adapterKey, adapter,
					48 * MemCachedClientWrapper.HOUR);
		}
		if (adapter != null && adapter.getStatus() == 0) {
			return null;
		}
		return adapter;
	}
}
