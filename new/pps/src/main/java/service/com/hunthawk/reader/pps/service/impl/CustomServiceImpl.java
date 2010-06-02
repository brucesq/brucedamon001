/**
 * 
 */
package com.hunthawk.reader.pps.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.hibernate.InExpression;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.custom.BookBag;
import com.hunthawk.reader.domain.custom.Bookmark;
import com.hunthawk.reader.domain.custom.Favorites;
import com.hunthawk.reader.domain.custom.UserBookPk;
import com.hunthawk.reader.domain.custom.UserBuy;
import com.hunthawk.reader.domain.custom.UserBuyMonth;
import com.hunthawk.reader.domain.custom.UserBuyMonthChoice;
import com.hunthawk.reader.domain.custom.UserFavoritesPk;
import com.hunthawk.reader.domain.custom.UserFootprint;
import com.hunthawk.reader.domain.custom.UserMonthUnicomBackMsg;
import com.hunthawk.reader.domain.custom.UserOrderList;
import com.hunthawk.reader.domain.custom.UserUnsubscribeList;
import com.hunthawk.reader.domain.inter.Reservation;
import com.hunthawk.reader.domain.inter.ReservationPK;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.ArrayUtil;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.IphoneFeeParamService;
import com.hunthawk.reader.pps.service.IphoneService;
import com.hunthawk.reader.pps.service.ResourceService;

/**
 * @author BruceSun
 * 
 */
public class CustomServiceImpl implements CustomService {

	private static Logger logger = Logger.getLogger(CustomServiceImpl.class);

	private HibernateGenericController controller;

	private MemCachedClientWrapper memcached;

	private BussinessService bussinessService;
	private IphoneFeeParamService iphoneFeeParamService;

	public void setIphoneFeeParamService(
			IphoneFeeParamService iphoneFeeParamService) {
		this.iphoneFeeParamService = iphoneFeeParamService;
	}

	private ResourceService resourceService;

	private CustomService customService;

	private IphoneService iphoneService;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}

	public void setBussinessService(BussinessService bussinessService) {
		this.bussinessService = bussinessService;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public void setCustomService(CustomService customService) {
		this.customService = customService;
	}

	public void setIphoneService(IphoneService iphoneService) {
		this.iphoneService = iphoneService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.pps.service.CustomService#addBookbag(com.hunthawk
	 *      .reader.domain.custom.BookBag)
	 */
	public void addBookbag(BookBag bag) throws Exception {

		controller.save(bag);
		// 清空书包缓存
		String key = Utility.getMemcachedKey(BookBag.class, bag.getMobile());
		memcached.deleteForLocal(key);

		UserBuy buy = new UserBuy();
		buy.setChannelId(bag.getChannelId());
		buy.setContentId(bag.getContentId());
		buy.setCreateTime(bag.getCreateTime());
		buy.setFeeId(bag.getFeeId());
		buy.setMobile(bag.getMobile());
		buy.setPid(bag.getPid());
		controller.save(buy);
		// 清空用户购买缓存
		key = Utility.getMemcachedKey(UserBuy.class, buy.getMobile());
		memcached.deleteForLocal(key);
	}

	public void addChoiceBook(String pid, String resourceId, String feeId,
			String mobile, int packId, String channelId) throws Exception {

		if (StringUtils.isEmpty(channelId) || "null".equals(channelId)) {
			channelId = bussinessService.getDefaultChannelId(pid);
		}

		UserBuyMonthChoice choice = new UserBuyMonthChoice();
		choice.setContentId(resourceId);
		choice.setCreateTime(new Date());
		choice.setFeeId(feeId);
		choice.setMobile(mobile);
		controller.save(choice);
		// 清楚该用户N选X记录
		String key = Utility.getMemcachedKey(UserBuyMonthChoice.class, mobile);
		memcached.deleteForLocal(key);

		UserOrderList order = new UserOrderList();
		order.setChannelId(channelId);
		order.setContentId(resourceId);
		order.setCreateTime(new Date());
		order.setFeeId(feeId);
		order.setMobile(mobile);
		order.setFeeType(Constants.FEE_TYPE_CHOICE);
		order.setOrderType(Constants.ORDER_TYPE_FREE);
		order.setPackId(packId);
		order.setPid(pid);
		Fee fee = getFee(feeId);
		order.setProductID(fee.getProductId());
		order.setServiceId(fee.getServiceId());
		order.setSpid(fee.getProvider().getProviderId());
		controller.save(order);

		BookBag bag = new BookBag();
		bag.setChannelId(order.getChannelId());
		bag.setContentId(order.getContentId());
		bag.setCreateTime(order.getCreateTime());
		bag.setFeeId(order.getFeeId());
		bag.setMobile(order.getMobile());
		bag.setPid(order.getPid());

		addBookbag(bag);
	}

	public List<UserBuyMonthChoice> getUserChoiceBooks(String mobile,
			String feeId) {
		// 用户增加选则结果时，更新缓存
		// TODO:每月定时清空用户选择时，清空该缓存
		String key = Utility.getMemcachedKey(UserBuyMonthChoice.class, mobile);
		List<UserBuyMonthChoice> choices = null;
		try {
			choices = (List<UserBuyMonthChoice>) memcached.getAndSaveLocal(key);
		} catch (Exception e) {
			logger.error("通过缓存获取用户N选X信息出错!", e);
		}
		if (choices == null) {
			choices = controller.findBy(UserBuyMonthChoice.class, "mobile",
					mobile);
			memcached.setAndSaveLocal(key, choices,
					24 * MemCachedClientWrapper.HOUR);
		}

		List<UserBuyMonthChoice> results = new ArrayList<UserBuyMonthChoice>();
		for (UserBuyMonthChoice choice : choices) {
			if (choice.getFeeId().equals(feeId)) {
				results.add(choice);
			}
		}
		return results;
	}

	public void addBookmark(Bookmark mark) throws Exception {
		if (mark.getMobile() != null && !mark.getMobile().equals("10000000000")) {
			try {
				controller.save(mark);
			} catch (Exception ex) {
				throw new Exception("您已经添加过此书签,不能重复添加");
			}
			// 清空书签缓存
			String key = Utility.getMemcachedKey(Bookmark.class, mark
					.getMobile());
			memcached.deleteForLocal(key);
		} else {
			/** 获取系统变量 */
			String error_info = bussinessService.getVariables("error_info")
					.getValue();
			throw new Exception(error_info);
		}

	}

	public void addUserOrderList(UserOrderList order) {
		controller.save(order);
	}

	public void addIphoneUnsubList(UserUnsubscribeList order) {
		controller.save(order);
	}

	public void addIphoneUnicomBackMsg(UserMonthUnicomBackMsg umub) {
		controller.save(umub);
	}

	public UserMonthUnicomBackMsg getUserMonthUnicomBackMsg(String mobile,
			int packId, int orderType, String pid, int columnId, String feeid) {
		String hql = "select umub from UserMonthUnicomBackMsg umub where umub.mobile=? and umub.packId=? and umub.feeId=?";
		if (orderType == 1) {
			hql += " and umub.orderType=1 and  umub.pid=? ";
			return (UserMonthUnicomBackMsg) controller.findBy(hql, mobile,
					packId, feeid, pid).get(0);
		} else if (orderType == 2) {
			hql += " and umub.orderType=2 and umub.columnId=? ";
			// System.out.println(hql);
			// List list=controller.findBy(hql,mobile,packId,columnId);
			// if(list.size()==0){
			// System.out.println("is null");
			// }
			return (UserMonthUnicomBackMsg) controller.findBy(hql, mobile,
					packId, feeid, columnId).get(0);
		} else if (orderType == 3) {
			hql += " and umub.orderType=3 ";
			return (UserMonthUnicomBackMsg) controller.findBy(hql, mobile,
					packId, feeid).get(0);
		}
		return null;
	}

	public void updateUserMonthUnicomBackMsgStatus(int id) {
		UserMonthUnicomBackMsg umub = controller.get(
				UserMonthUnicomBackMsg.class, id);
		umub.setStatus(0);
		controller.update(umub);
	}

	public void addUserBuyMonth(UserBuyMonth month) {
		if (StringUtils.isEmpty(month.getChannelId())
				|| "null".equals(month.getChannelId())) {
			month.setChannelId(bussinessService.getDefaultChannelId(month
					.getPid()));
		}
		controller.save(month);
		// 清空用户包月信息缓存
		String key = Utility.getMemcachedKey(UserBuyMonth.class, month
				.getMobile());
		memcached.deleteForLocal(key);
	}

	// public void delUserBuyMonth(String mobile,String feeid,boolean
	// isIphone,String ids){
	// String
	// hql="select ubm from UserBuyMonth ubm where ubm.mobile=? and
	// ubm.feeId=?";
	// if(isIphone){
	// hql+=" and ubm.feeId in ("+ids+")";
	// }
	// UserBuyMonth ubm=null;
	// if(isIphone){
	// ubm=(UserBuyMonth)controller.findBy(hql, mobile,feeid,ids).get(0);
	// }else{
	// ubm=(UserBuyMonth)controller.findBy(hql, mobile,feeid).get(0);
	// }
	// controller.delete(ubm);
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.pps.service.CustomService#addFavorites(com.hunthawk
	 *      .reader.domain.custom.Favorites)
	 */
	public void addFavorites(Favorites fav) throws Exception {
		if (fav.getMobile() != null && !fav.getMobile().equals("10000000000")) {
			try {
				controller.save(fav);
			} catch (Exception ex) {
				throw new Exception("您已经收藏过该视频");
			}
			// 清空收藏缓存
			String key = Utility.getMemcachedKey(Favorites.class, fav
					.getMobile());
			memcached.deleteForLocal(key);
			key = Utility.getMemcachedKey(Favorites.class, fav.getContentId());
			memcached.deleteForLocal(key);
		} else {
			String error_info = bussinessService.getVariables("error_info")
					.getValue();
			throw new Exception(error_info);
		}
	}

	public void deleteBookmark(Integer id) {

		Bookmark mark = controller.get(Bookmark.class, id);
		if (mark != null) {
			controller.delete(mark);
			// 清空书签缓存
			String key = Utility.getMemcachedKey(Bookmark.class, mark
					.getMobile());
			memcached.deleteForLocal(key);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.pps.service.CustomService#deleteFavorites(java.lang
	 *      .Integer)
	 */
	public void deleteFavorites(String mobile, String cid, String productid) {
		// UserBookPk pk = new UserBookPk();
		UserFavoritesPk pk = new UserFavoritesPk();
		pk.setContentId(cid);
		pk.setProductid(productid);
		pk.setMobile(mobile);
		controller.deleteById(Favorites.class, pk);
		// 清空书签缓存
		String key = Utility.getMemcachedKey(Favorites.class, mobile);
		memcached.deleteForLocal(key);
		key = Utility.getMemcachedKey(Favorites.class, cid);
		memcached.deleteForLocal(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.pps.service.CustomService#deleteUserBookbag(java.
	 *      lang.Integer)
	 */
	public void deleteUserBookbag(String mobile, String cid) {
		UserBookPk pk = new UserBookPk();
		pk.setContentId(cid);
		pk.setMobile(mobile);
		controller.deleteById(BookBag.class, pk);
		// 清空书包缓存
		String key = Utility.getMemcachedKey(BookBag.class, mobile);
		memcached.deleteForLocal(key);
	}

	public List<BookBag> getUserBookbag(String mobile) {
		// 书包变更时，需要清空该缓存
		String key = Utility.getMemcachedKey(BookBag.class, mobile);
		List<BookBag> bags = null;
		try {
			bags = (List<BookBag>) memcached.getAndSaveLocal(key);
			if (bags != null)
				return bags;
		} catch (Exception e) {
			logger.error("获取用户书包信息出错!", e);
		}

		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("mobile", mobile,
				CompareType.Equal);
		expressions.add(ex);
		// bags = controller.findBy(BookBag.class, 1, 10000, expressions);
		bags = controller.findBy(BookBag.class, 1, 10000, "createTime", false,
				expressions);
		memcached.setAndSaveLocal(key, bags, 24 * MemCachedClientWrapper.HOUR);
		return bags;
	}

	/** 书签列表,历史记录,收藏列表 产品过滤 */
	private List productFilter(List filters, String productId, Object o) {
		if (productId == null || productId.equals("0"))
			return new ArrayList();

		List list = new ArrayList();
		for (Iterator it = filters.iterator(); it.hasNext();) {
			if (o instanceof Bookmark) {
				Bookmark mark = (Bookmark) it.next();
				if (productId.equals(mark.getProductId())) {
					list.add(mark);
				}
			} else if (o instanceof UserFootprint) {
				UserFootprint uf = (UserFootprint) it.next();
				if (productId.equals(uf.getProductId())) {
					list.add(uf);
				}
			} else if (o instanceof Favorites) {
				Favorites fav = (Favorites) it.next();
				if (productId.equals(fav.getProductid())) {
					list.add(fav);
				}
			}
		}
		return list;
	}

	public List<Bookmark> getUserBookmark(String mobile, String productId) {
		// 书签变更时，清空书签混存
		String key = Utility.getMemcachedKey(Bookmark.class, mobile);
		List<Bookmark> marks = null;
		try {
			marks = (List<Bookmark>) memcached.getAndSaveLocal(key);
			if (marks != null)
				return productFilter(marks, productId, new Bookmark());
		} catch (Exception e) {
			logger.error("获取用户书签信息出错!", e);
		}
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("mobile", mobile,
				CompareType.Equal);
		expressions.add(ex);
		marks = controller.findBy(Bookmark.class, 1, 200, "createTime", false,
				expressions);
		memcached.setAndSaveLocal(key, marks, 24 * MemCachedClientWrapper.HOUR);

		return productFilter(marks, productId, new Bookmark());
	}

	public int getResourceFavoritesCount(String resourceId) {
		// 用户收藏变更时，清空该缓存
		String key = Utility.getMemcachedKey(Favorites.class, resourceId);
		Integer count = null;
		try {
			count = (Integer) memcached.getAndSaveLocal(key);
			if (count != null)
				return count;
		} catch (Exception e) {
			logger.error("获取内容被收藏总数时出错!", e);
		}
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("contentId", resourceId,
				CompareType.Equal);
		expressions.add(ex);
		count = controller.getResultCount(Favorites.class, expressions)
				.intValue();
		memcached.setAndSaveLocal(key, count, 24 * MemCachedClientWrapper.HOUR);
		return count;
	}

	public List<Favorites> getUserFavorites(String mobile, String productId) {
		// 用户收藏变更时，清空该缓存
		String key = Utility.getMemcachedKey(Favorites.class, mobile);
		List<Favorites> favs = null;
		try {
			favs = (List<Favorites>) memcached.getAndSaveLocal(key);
			if (favs != null)
				return productFilter(favs, productId, new Favorites());
		} catch (Exception e) {
			logger.error("获取用户收藏信息时出错!", e);
		}
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("mobile", mobile,
				CompareType.Equal);
		expressions.add(ex);
		// favs = controller.findBy(Favorites.class, 1, 200, expressions);
		favs = controller.findBy(Favorites.class, 1, 200, "createTime", false,
				expressions);
		memcached.setAndSaveLocal(key, favs, 24 * MemCachedClientWrapper.HOUR);
		// return favs;
		/**
		 * 过滤产品 modify by liuxh 09-11-12
		 */
		return productFilter(favs, productId, new Favorites());
		/**
		 * end
		 */
	}

	public List<Favorites> getUserFavoritesByPage(String mobile,
			String productId, int pageSize, int pageNo) {
		// Collection<HibernateExpression> expressions = new
		// ArrayList<HibernateExpression>();
		// HibernateExpression ex = new
		// CompareExpression("mobile",mobile,CompareType.Equal);
		// expressions.add(ex);
		// return controller.findBy(Favorites.class, pageNo, pageSize,
		// "contentId", false, expressions);
		List<Favorites> favs = getUserFavorites(mobile, productId);
		if (favs.size() == 0) {
			return favs;
		}
		return page(favs, pageNo, pageSize);
		// int start = pageSize * (pageNo - 1);
		// int end = pageSize * pageNo;
		// start = start > favs.size() - 1 ? favs.size() - 1 : start;
		// end = end > favs.size() - 1 ? favs.size() - 1 : end;
		// return favs.subList(start, end);
	}

	public List<BookBag> getUserBookbagsByPage(String mobile, String productId,
			int pageSize, int pageNo) {
		// Collection<HibernateExpression> expressions = new
		// ArrayList<HibernateExpression>();
		// HibernateExpression ex = new
		// CompareExpression("mobile",mobile,CompareType.Equal);
		// expressions.add(ex);
		// return controller.findBy(BookBag.class, pageNo, pageSize,
		// "contentId", false, expressions);
		List<BookBag> bags = getUserBookbag(mobile);
		if (bags.size() == 0) {
			return bags;
		}
		return page(bags, pageNo, pageSize);
		// int start = pageSize * (pageNo - 1);
		// int end = pageSize * pageNo;
		// start = start > bags.size() - 1 ? bags.size() - 1 : start;
		// end = end > bags.size() - 1 ? bags.size() - 1 : end;
		// return bags.subList(start, end);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.pps.service.CustomService#getUserBookmarksByPage(
	 *      java.lang.String)
	 */
	public List<Bookmark> getUserBookmarksByPage(String mobile,
			String productId, int pageSize, int pageNo) {
		// Collection<HibernateExpression> expressions = new
		// ArrayList<HibernateExpression>();
		// HibernateExpression ex = new
		// CompareExpression("mobile",mobile,CompareType.Equal);
		// expressions.add(ex);
		// return controller.findBy(Bookmark.class, pageNo, pageSize, "id",
		// false, expressions);
		List<Bookmark> marks = getUserBookmark(mobile, productId);
		if (marks.size() == 0) {
			return marks;
		}
		return page(marks, pageNo, pageSize);
		// int start = pageSize * (pageNo - 1);
		// int end = pageSize * pageNo;
		// start = start > marks.size() - 1 ? marks.size() - 1 : start;
		// end = end > marks.size() - 1 ? marks.size() - 1 : end;
		// return marks.subList(start, end);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.pps.service.CustomService#getUserFavoritesResultCount
	 *      (java.lang.String)
	 */
	public Long getUserFavoritesResultCount(String mobile, String productId) {
		// Collection<HibernateExpression> expressions = new
		// ArrayList<HibernateExpression>();
		// HibernateExpression ex = new
		// CompareExpression("mobile",mobile,CompareType.Equal);
		// expressions.add(ex);
		// return controller.getResultCount(Favorites.class, expressions);
		return Long.valueOf(getUserFavorites(mobile, productId).size());
	}

	public Long getUserBookbagResultCount(String mobile) {

		return Long.valueOf(getUserBookbag(mobile).size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.pps.service.CustomService#getUserBookmarkResultCount
	 *      (java.lang.String)
	 */
	public int getUserBookmarkResultCount(String mobile, String productId) {
		// Collection<HibernateExpression> expressions = new
		// ArrayList<HibernateExpression>();
		// HibernateExpression ex = new
		// CompareExpression("mobile",mobile,CompareType.Equal);
		// expressions.add(ex);
		// return controller.getResultCount(Bookmark.class, expressions);
		return getUserBookmark(mobile, productId).size();
	}

	public Fee getFee(String feeId) {
		if (StringUtils.isEmpty(feeId))
			return null;
		String key = Utility.getMemcachedKey(Fee.class, feeId);
		Fee fee = null;
		try {
			fee = (Fee) memcached.getAndSaveLocal(key);
			if (fee != null) {
				return fee;
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取计费信息出错", e);
		}
		fee = controller.get(Fee.class, feeId);
		memcached.setAndSaveLocal(key, fee, 0);
		return fee;

	}

	public Fee getFeeByUrl(String url) {
		String key = Utility.getMemcachedKey(Fee.class, url);
		Fee fee = null;
		try {
			fee = (Fee) memcached.getAndSaveLocal(key);
			if (fee != null) {
				return fee;
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取计费信息出错", e);
		}
		List<Fee> fees = controller.findBy(Fee.class, "url", url);
		if (fees.size() > 0) {
			fee = fees.get(0);
		}
		memcached.setAndSaveLocal(key, fee, 0);
		return fee;
	}

	@SuppressWarnings("unchecked")
	public List<UserBuyMonth> getUserBuyMonths(String mobile) {
		// 添加用户包月信息时，清空缓存
		String key = Utility.getMemcachedKey(UserBuyMonth.class, mobile);
		List<UserBuyMonth> objs = null;
		try {
			objs = (List<UserBuyMonth>) memcached.getAndSaveLocal(key);
			if (objs != null)
				return objs;

		} catch (Exception e) {
			logger.error("从Memcached中获取用户包月信息出错", e);
		}
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("mobile", mobile,
				CompareType.Equal);
		expressions.add(ex);
		objs = controller.findBy(UserBuyMonth.class, 1, Integer.MAX_VALUE,
				expressions);
		memcached.setAndSaveLocal(key, objs, 24 * MemCachedClientWrapper.HOUR);
		return objs;
	}

	public List<UserBuy> getUserBuyList(String mobile) {
		// 用户购买信息变更时变更时，需要清空该缓存
		String key = Utility.getMemcachedKey(UserBuy.class, mobile);
		List<UserBuy> buys = null;
		try {
			buys = (List<UserBuy>) memcached.getAndSaveLocal(key);
			if (buys != null)
				return buys;
		} catch (Exception e) {
			logger.error("获取用户购买信息出错!", e);
		}

		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("mobile", mobile,
				CompareType.Equal);
		expressions.add(ex);
		buys = controller.findBy(UserBuy.class, 1, Integer.MAX_VALUE,
				expressions);
		memcached.setAndSaveLocal(key, buys, 24 * MemCachedClientWrapper.HOUR);
		return buys;
	}

	public boolean isUserBuyBook(String mobile, String cid) {
		List<UserBuy> buys = getUserBuyList(mobile);
		for (UserBuy buy : buys) {
			if (buy.getContentId().equals(cid))
				return true;
		}
		return false;
	}

	public boolean isOrderMonth(String mobile, String feeid) {
		List<UserBuyMonth> orders = getUserBuyMonths(mobile);
		// for(Iterator it=orders.iterator();it.hasNext();){
		// UserBuyMonth month =(UserBuyMonth)it.next();
		// System.out.println("test --packId="+month.getPackId()+"
		// feeid="+month.getFeeId());
		// }
		// System.out.println("mobile->"+mobile+" ; feeid->"+feeid);
		for (UserBuyMonth month : orders) {
			if (month.getFeeId().equals(feeid))
				return true;
		}
		return false;
	}

	public boolean isOrderMonth(String mobile, String feeid, Integer packId) {
		List<UserBuyMonth> orders = getUserBuyMonths(mobile);
		// for(Iterator it=orders.iterator();it.hasNext();){
		// UserBuyMonth month =(UserBuyMonth)it.next();
		// System.out.println("test --packId="+month.getPackId()+"
		// feeid="+month.getFeeId());
		// }
		// System.out.println("mobile->"+mobile+" ; feeid->"+feeid+" ;
		// packId->"+packId);
		for (UserBuyMonth month : orders) {
			if (month.getFeeId().equals(feeid)
					&& month.getPackId().equals(packId))
				return true;
		}
		return false;
	}

	public void addReservation(String mobile, String contentId,
			Integer packRelationId) {
		Reservation res = new Reservation();
		res.setContentId(contentId);
		res.setMobile(mobile);
		res.setCreateTime(new Date());
		res.setPackRelationId(packRelationId);
		controller.save(res);
		String key = Utility.getMemcachedKey(Reservation.class, mobile);
		String key2 = Utility.getMemcachedKey(Reservation.class, mobile,
				contentId);
		// 清空连载预订缓存
		memcached.deleteForLocal(key);
		memcached.deleteForLocal(key2);
	}

	public List<Reservation> getReservation(String mobile) {
		String key = Utility.getMemcachedKey(Reservation.class, mobile);
		List<Reservation> reservations = null;
		try {
			reservations = (List<Reservation>) memcached.getAndSaveLocal(key);
			if (reservations != null) {
				return reservations;
			}
		} catch (Exception e) {
			logger.error("从缓存中获取用户连载预订出错", e);
		}
		reservations = controller.findBy(Reservation.class, "mobile", mobile,
				"createTime", false);
		memcached.setAndSaveLocal(key, reservations,
				4 * MemCachedClientWrapper.HOUR);
		return reservations;
	}

	public Reservation getReservationByContetnId(String mobile, String contentid) {
		String key = Utility.getMemcachedKey(Reservation.class, mobile,
				contentid);
		Reservation reservation = null;
		try {
			reservation = (Reservation) memcached.getAndSaveLocal(key);
			if (reservation != null) {
				return reservation;
			}
		} catch (Exception e) {
			logger.error("从缓存中获取用户连载预订出错", e);
		}

		String hql = "select t from Reservation t where t.mobile=? and t.contentId=?";
		List<Reservation> reservations = controller.findBy(hql, mobile,
				contentid);
		if (reservations == null || reservations.size() == 0) {
			reservation = null;
		} else {
			reservation = reservations.get(0);
		}
		memcached.setAndSaveLocal(key, reservation,
				4 * MemCachedClientWrapper.MINUTE);
		return reservation;
	}

	public void deleteReservation(String mobile, String contentId) {
		ReservationPK resPk = new ReservationPK();
		resPk.setContentId(contentId);
		resPk.setMobile(mobile);
		controller.deleteById(Reservation.class, resPk);
		String key = Utility.getMemcachedKey(Reservation.class, mobile);
		String key2 = Utility.getMemcachedKey(Reservation.class, mobile,
				contentId);
		// 清空连载预订缓存
		memcached.deleteForLocal(key);
		memcached.deleteForLocal(key2);
	}

	public void updateUserFootprint(String mobile, String contentId,
			String url, String productId) {
		if (mobile != null && !mobile.equals("10000000000")) {

			// 去掉AJAX方式中的所带的模板ID参数
			// 2009-11-4
			// brucesun
			int index = url.indexOf("?");
			if (index >= 0) {
				String prefix = url.substring(0, index + 1);
				String queryString = url.substring(index + 1);
				url = prefix
						+ URLUtil.removeParameter(queryString,
								ParameterConstants.TEMPLATE_ID);
			}

			// END
			String printKey = mobile + Constants.MEMCACHED_SLASH + contentId;
			USER_FOOTPRINT.add(printKey);
			String key = Utility.getMemcachedKey(UserFootprint.class, printKey);
			UserFootprint footprint = new UserFootprint();
			footprint.setContentId(contentId);
			footprint.setCreateTime(new Date());
			footprint.setMobile(mobile);
			footprint.setUrl(url);
			footprint.setProductId(productId);
			// controller.save(footprint);
			memcached.set(key, footprint, 24 * MemCachedClientWrapper.HOUR);
		}

		// updateUserFootprintList(mobile,contentId,url);
	}

	// private void updateUserFootprintList(String mobile,String
	// contentId,String url){
	// String key = Utility.getMemcachedKey(UserFootprint.class, mobile);
	// try {
	// List<UserFootprint> footprints = (List<UserFootprint>) memcached
	// .get(key);
	// boolean hit = false;
	// if(footprints != null){
	// for(UserFootprint foot : footprints){
	// if(foot.getContentId().equals(contentId)){
	// foot.setCreateTime(new Date());
	// foot.setUrl(url);
	// hit = true;
	// break;
	// }
	// }
	// if(!hit){
	// if(footprints.size() == 20){
	// footprints.remove(19);
	// }
	// UserFootprint foot = new UserFootprint();
	// foot.setContentId(contentId);
	// foot.setCreateTime(new Date());
	// foot.setMobile(mobile);
	// foot.setUrl(url);
	// footprints.add(0, foot);
	// }
	// memcached.set(key, footprints, 24 * MemCachedClientWrapper.HOUR);
	// }
	// }catch (Exception e) {
	// logger.error("从缓存中获取最近访问内容的地址出错", e);
	// }
	// }

	/**
	 * 获取用户最近访问内容的地址
	 * 
	 * @param mobile
	 * @param contentId
	 * @return
	 */
	public String getLastUserFootprint(String mobile, String contentId) {
		String printKey = mobile + Constants.MEMCACHED_SLASH + contentId;
		String key = Utility.getMemcachedKey(UserFootprint.class, printKey);
		String url = null;
		try {
			UserFootprint footprint = (UserFootprint) memcached.get(key);
			if (footprint != null) {
				url = footprint.getUrl();
			}
		} catch (Exception e) {
			logger.error("从缓存中获取最近访问内容的地址出错", e);
		}
		if (url == null) {
			UserBookPk pk = new UserBookPk();
			pk.setContentId(contentId);
			pk.setMobile(mobile);
			UserFootprint footprint = controller.get(UserFootprint.class, pk);
			if (footprint != null) {
				memcached.set(key, footprint, 24 * MemCachedClientWrapper.HOUR);
				url = footprint.getUrl();
			}
		}
		return url;
	}

	public List<UserFootprint> getUsetFootprints(String mobile, int pageNo,
			int pageSize, String productId) {

		// String key = Utility.getMemcachedKey(UserFootprint.class, mobile);
		// try {
		// List<UserFootprint> footprints = (List<UserFootprint>) memcached
		// .get(key);
		// if (footprints != null) {
		// return footprints;
		// }
		// } catch (Exception e) {
		// logger.error("从缓存中获取最近访问历史记录出错", e);
		// }
		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		expressions.add(new CompareExpression("mobile", mobile,
				CompareType.Equal));
		// List<UserFootprint> footprints =
		// controller.findBy(UserFootprint.class,
		// pageNo, pageSize, "createTime", false, expressions);
		// memcached.set(key, footprints, 24 * MemCachedClientWrapper.HOUR);
		// return footprints;
		List<UserFootprint> footprints = controller.findBy(UserFootprint.class,
				1, 20, "createTime", false, expressions);// 取20条 进行分页
		// return page(footprints, pageNo, pageSize);
		return page(productFilter(footprints, productId, new UserFootprint()),
				pageNo, pageSize);

	}

	/**
	 * 获取所有用户访问的地址主键
	 * 
	 * @return
	 */
	public Set<String> getAllUserFootprintKey() {
		Set<String> allKeys = new HashSet<String>();
		allKeys.addAll(USER_FOOTPRINT);
		USER_FOOTPRINT.clear();
		return allKeys;
	}

	public List<Integer> getResourceUserRelation(String resourceId, int packId,
			int pageSize) {
		String key = Utility.getMemcachedKey(UserFootprint.class, "relation",
				resourceId, packId + "");
		List<Integer> reses = null;
		try {
			reses = (List<Integer>) memcached.getAndSaveLocalMedium(key);
		} catch (Exception e) {
			logger.error("从缓存中获取最近资源关联阅读记录出错", e);
		}
		if (reses == null) {
			reses = new ArrayList<Integer>();
			List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
			expressions.add(new CompareExpression("contentId", resourceId,
					CompareType.Equal));
			List<UserFootprint> userReads = controller.findBy(
					UserFootprint.class, 1, 10, "createTime", false,
					expressions);
			// List<String> mobiles = new ArrayList<String>();
			// for (UserFootprint print : userReads) {
			// mobiles.add(print.getMobile());
			// }

			/**
			 * 资源过滤 列表资源必须是packId下的资源 modify by liuxh 09-11-09
			 */
			StringBuilder mobiles = new StringBuilder();
			for (UserFootprint print : userReads) {
				mobiles.append("'");
				mobiles.append(print.getMobile());
				mobiles.append("'");
				mobiles.append(",");
			}
			String str = "";
			if (mobiles.length() > 0) {
				str = mobiles.toString().substring(0,
						mobiles.toString().lastIndexOf(","));
			} else {
				return reses;
			}
			String hql = "select distinct rel.id,foot.createTime from UserFootprint foot,ResourcePackReleation rel where  rel.pack=? "
					+ " and foot.mobile in ("
					+ str
					+ ") and foot.contentId not in ('"
					+ resourceId
					+ "') and foot.contentId=rel.resourceId and  rel.status=0  and rel.resourceId like '"
					+ resourceId.substring(0, 1)
					+ "%' order by foot.createTime desc";
			ResourcePack pack = new ResourcePack();
			pack.setId(packId);

			List<Object[]> ids = controller.findBy(hql, 1, 30, pack);
			for (int i = 0; i < ids.size(); i++) {
				reses.add(Integer.parseInt(ids.get(i)[0].toString()));
			}
			/**
			 * 数组去重 mydify by liuxh 09-11-13
			 */
			if (reses != null && reses.size() > 0)
				reses = ArrayUtil.filterRedundanceData(reses);
			/**
			 * end
			 */
			// reses=controller.findBy(hql,1,30,pack);
			/**
			 * end
			 */
			// expressions = new ArrayList<HibernateExpression>();
			// if (mobiles.size() == 0) {
			// return reses;
			// }
			// expressions.add(new InExpression("mobile", mobiles));
			// expressions.add(new CompareExpression("contentId", resourceId,
			// CompareType.NotEqual));
			// expressions.add(new CompareExpression("contentId",
			// resourceId.substring(0,1)+"%",
			// CompareType.Like));
			// expressions.add(new CompareExpression("url", "%nd=%",
			// CompareType.Like));
			//			
			// List<UserFootprint> relReads = controller.findBy(
			// UserFootprint.class, 1, 30, "createTime", false,
			// expressions);
			//
			// List<String> exists = new ArrayList<String>();
			// for (UserFootprint print : relReads) {
			// if (exists.contains(print.getContentId())) {
			// continue;
			// }
			// exists.add(print.getContentId());
			// try {
			// int index = print.getUrl().indexOf("nd=");
			// String url = print.getUrl().substring(index + 3);
			// index = url.indexOf("&");
			// url = url.substring(0, index);
			// reses.add(Integer.parseInt(url));
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
		}

		if (reses.size() > 0) {
			memcached.setAndSaveLocalMedium(key, reses,
					8 * MemCachedClientWrapper.HOUR);
		} else {
			memcached.setAndSaveLocalMedium(key, reses,
					1 * MemCachedClientWrapper.HOUR);
		}

		/**
		 * 从记录中取前20本内容随机显示X本 modify by liuxh 09-11-03
		 */
		if (reses.size() < 1)
			return new ArrayList();
		if (reses.size() - 1 >= pageSize) {
			/** 随机生成X个不重复的数 */
			Integer arr[] = ArrayUtil.getRandom(reses.size() - 1,
					pageSize > reses.size() ? reses.size() : pageSize);
			List<Integer> backList = new ArrayList<Integer>();
			for (int i = 0; i < arr.length; i++) {
				backList.add(reses.get(arr[i]));
			}
			return backList;
		} else {
			return reses;
		}
		/**
		 * end
		 */

		// pageSize = pageSize > reses.size() ? reses.size() : pageSize;
		// return reses.subList(0, pageSize);
	}

	private static Set<String> USER_FOOTPRINT = new HashSet<String>();

	/**
	 * 分页
	 * 
	 * @param list
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List page(List list, int pageNo, int pageSize) {
		if (list == null || list.size() < 2) {
			return list;
		}
		int start = pageSize * (pageNo - 1);
		int end = pageSize * pageNo;
		start = start > list.size() - 1 ? list.size() - 1 : start;
		end = end > list.size() ? list.size() : end;
		return list.subList(start, end);
	}

	public boolean isOtherOrderMonth(String resourceId, String mobile,
			String productId) {

		boolean isIphone = iphoneService.isIphoneProduct(productId);
		/**
		 * 增加对资源类型的过滤 modify by liuxh 09-11-16
		 */
		String[] packids = iphoneFeeParamService
				.getIphoneMonthPackIds(resourceId);
		/**
		 * end
		 */
		// String[] packids =
		// bussinessService.getVariables("iphone_month_packs")
		// .getValue().split(";");
		/** 资源所在的批价包集合 */
		List<ResourcePackReleation> rprs = resourceService
				.getResourcePackReleationByResourceId(resourceId, isIphone);
		for (Iterator it = rprs.iterator(); it.hasNext();) {
			ResourcePackReleation rpr = (ResourcePackReleation) it.next();
			int packId = rpr.getPack().getId();
			// System.out.println("packId="+rpr.getPack().getId());
			if (isIphone) {
				if (ArrayUtil.isHave(String.valueOf(packId), packids)) {// iphone栏目包月批价包
					/**
					 * modify by liuxh 09-11-16
					 */
					String[] columnFeeIds = iphoneFeeParamService
							.getIphoneFeeIds(resourceId, 2);
					for (String feeId : columnFeeIds) {
						if (customService.isOrderMonth(mobile, feeId, packId))
							return true;
					}
					/**
					 * end
					 */
					// String columnFeeId = bussinessService.getVariables(
					// "iphone_fee_column").getValue();
					// String columnDisFeeId = bussinessService.getVariables(
					// "iphone_fee_column_dis").getValue();
					// if (customService.isOrderMonth(mobile, columnFeeId,
					// packId))
					// return true;
					// if (customService.isOrderMonth(mobile, columnDisFeeId,
					// packId))
					// return true;
				}
				/** 频道包月判断 */
				String[] channelFeeIds = iphoneFeeParamService.getIphoneFeeIds(
						resourceId, 1);
				for (String feeId : channelFeeIds) {
					if (customService.isOrderMonth(mobile, feeId))
						return true;
				}
				// String channelFeeId = bussinessService.getVariables(
				// "iphone_fee_channel").getValue();
				// String channelDisFeeId = bussinessService.getVariables(
				// "iphone_fee_channel_dis").getValue();
				// if (customService.isOrderMonth(mobile, channelFeeId))
				// return true;
				// if (customService.isOrderMonth(mobile, channelDisFeeId))
				// return true;

			} else {
				if (customService.isOrderMonth(mobile, String.valueOf(packId)))
					return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		// String url = "/pps/s.do?td=123&amp;ss=34&ad=76";
		// int index = url.indexOf("?");
		// System.out.println(index);
		// if(index >= 0){
		// String prefix = url.substring(0,index+1);
		// String queryString = url.substring(index+1);
		// url = prefix + URLUtil.removeParameter(queryString,
		// ParameterConstants.TEMPLATE_ID);
		// }
		// System.out.println(url);
		// List favs = new ArrayList() ;
		// favs.add(0);
		// favs.add(1);
		// int pageSize = 10;
		// int pageNo = 1;
		// int start = pageSize * (pageNo - 1);
		// int end = pageSize * pageNo;
		// start = start > favs.size() - 1 ? favs.size() - 1 : start;
		// end = end > favs.size() - 1 ? favs.size() - 1 : end;
		// List list = page(favs,1,10);//favs.subList(start, end);
		// System.out.println(list.size());
	}
}
