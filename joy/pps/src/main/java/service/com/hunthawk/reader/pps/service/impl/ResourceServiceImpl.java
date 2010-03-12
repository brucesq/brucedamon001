/**
 * 
 */
package com.hunthawk.reader.pps.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.hibernate.NullExpression;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.memcached.NullObject;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.EbookChapterDesc;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.Infomation;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.MagazineChapter;
import com.hunthawk.reader.domain.resource.MagazineChapterDesc;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.NewsPapersChapter;
import com.hunthawk.reader.domain.resource.NewsPapersChapterDesc;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceResType;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.resource.Video;
import com.hunthawk.reader.domain.resource.VideoSuite;
import com.hunthawk.reader.pps.ArrayUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.reader.service.partner.PartnerService;

/**
 * @author BruceSun
 * 
 */
@SuppressWarnings("unchecked")
public class ResourceServiceImpl implements ResourceService {

	private HibernateGenericController controller;
	private BussinessService bussinessService;
	private PartnerService partnerService;

	public PartnerService getPartnerService() {
		return partnerService;
	}

	public void setPartnerService(PartnerService partnerService) {
		this.partnerService = partnerService;
	}

	// private HibernateOverWriteController hibernateOverWriteController;
	private MemCachedClientWrapper memcached;

	private static Logger logger = Logger.getLogger(ResourceServiceImpl.class);

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	// public HibernateOverWriteController getHibernateOverWriteController() {
	// return hibernateOverWriteController;
	// }
	//
	// public void setHibernateOverWriteController(
	// HibernateOverWriteController hibernateOverWriteController) {
	// this.hibernateOverWriteController = hibernateOverWriteController;
	// }

	public void setBussinessService(BussinessService bussinessService) {
		this.bussinessService = bussinessService;
	}

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}

	public ResourceAll getResource(String resourceId) {
		// 资源更新时需要，清空缓存
		String key = Utility.getMemcachedKey(ResourceAll.class, resourceId);
		Object resource = null;
		try {
			resource = memcached.getAndSaveLocalMedium(key);
		} catch (Exception e) {
			logger.error("从Memcached中获取资源信息出错!", e);
		}
		if (resource == null) {
			resource = getResource(resourceId, Integer.parseInt(resourceId
					.substring(0, 1)));
			if (resource == null) {
				resource = new NullObject();
			}
			memcached.setAndSaveLocalMedium(key, resource,
					72 * MemCachedClientWrapper.HOUR);
		}
		if (resource instanceof NullObject) {
			return null;
		}
		ResourceAll res = (ResourceAll) resource;
		if (res.getStatus() == 0)
			return res;
		return null;
	}

	public ResourcePack getResourcePack(int packId) {
		// 批价包更新时，需要清空缓存
		String key = Utility.getMemcachedKey(ResourcePack.class, String
				.valueOf(packId));
		Object pack = null;
		try {
			pack = memcached.getAndSaveLocalMedium(key);
		} catch (Exception e) {
			logger.error("从Memcached中获取批价包信息出错!", e);
		}
		if (pack == null) {
			pack = controller.get(ResourcePack.class, packId);
			if (pack == null) {
				pack = new NullObject();
			}
			memcached.setAndSaveLocalMedium(key, pack,
					72 * MemCachedClientWrapper.HOUR);
		}
		if (pack instanceof NullObject) {
			return null;
		}
		return (ResourcePack) pack;
	}

	public ResourcePackReleation getResourcePackReleation(int relId) {
		// 批价包关联信息更新时，需要清空缓存
		String key = Utility.getMemcachedKey(ResourcePackReleation.class,
				String.valueOf(relId));
		Object rel = null;
		try {
			rel = memcached.getAndSaveLocalMedium(key);
		} catch (Exception e) {
			logger.error("从Memcached中获取批价包关联信息出错!", e);
		}
		if (rel == null) {
			rel = controller.get(ResourcePackReleation.class, relId);
			if (rel == null) {
				rel = new NullObject();
			}
			memcached.setAndSaveLocalMedium(key, rel,
					72 * MemCachedClientWrapper.HOUR);
		}
		if (rel instanceof NullObject) {
			return null;
		}
		ResourcePackReleation r = (ResourcePackReleation) rel;
		if (r.getStatus() == 0)
			return r;
		return null;

	}

	@SuppressWarnings("unchecked")
	public List<ResourcePackReleation> getResourcePackReleations(int packId,
			int pageNo, int pageSize) {
		// TODO:需要根据批价包更新时间,来判断是否需要更新关联集合内容
		String key = Utility.getMemcachedKey(ResourcePackReleation.class,
				String.valueOf(packId), String.valueOf(pageNo), String
						.valueOf(pageSize));
		List<ResourcePackReleation> rels = null;
		try {
			rels = (List<ResourcePackReleation>) memcached
					.getAndSaveLocalMedium(key);
			if (rels != null)
				return rels;
		} catch (Exception e) {
			logger.error("从Memcached中获取批价包关联列表信息出错!", e);
		}
		String hql = "select rel from  ResourcePackReleation rel,ResourceAll resource where rel.pack = ?  and resource.status = 0 and rel.status=0  and resource.id=rel.resourceId ";
		if (getNotBussinessPartnerIds().length() > 0) {
			hql += " and resource.cpId not in " + getNotBussinessPartnerIds()
					+ " ";
		}
		ResourcePack pack = new ResourcePack();
		pack.setId(packId);
		rels = controller.findBy(hql, pageNo, pageSize, pack);
		// Collection<HibernateExpression> expressions = new
		// ArrayList<HibernateExpression>();

		// HibernateExpression ex = new CompareExpression("pack", pack,
		// CompareType.Equal);
		// HibernateExpression statusEx = new CompareExpression("status", 0,
		// CompareType.Equal);
		// expressions.add(ex);
		// expressions.add(statusEx);
		// rels = controller.findBy(ResourcePackReleation.class, pageNo,
		// pageSize,
		// "order", false, expressions);
		memcached.setAndSaveLocalMedium(key, rels,
				5 * MemCachedClientWrapper.MINUTE);
		return rels;
	}

	public Long getResourcePackReleationsCount(int packId) {
		// 批价包信息变更时，清空缓存
		String key = Utility.getMemcachedKey(ResourcePackReleation.class,
				String.valueOf(packId), "count");
		Long count = null;
		try {
			count = (Long) memcached.getAndSaveLocalMedium(key);
			if (count != null)
				return count;
		} catch (Exception e) {
			logger.error("从Memcached中获取批价包关联列表总数信息出错!", e);
		}
		String hql = "select rel from  ResourcePackReleation rel,ResourceAll resource where rel.pack = ?  and resource.status = 0 and rel.status=0  and resource.id=rel.resourceId ";
		if (getNotBussinessPartnerIds().length() > 0) {
			hql += " and resource.cpId not in " + getNotBussinessPartnerIds()
					+ " ";
		}
		ResourcePack pack = new ResourcePack();
		pack.setId(packId);
		count = controller.getResultCount(hql, pack);
		// Collection<HibernateExpression> expressions = new
		// ArrayList<HibernateExpression>();
		// ResourcePack pack = new ResourcePack();
		// pack.setId(packId);
		// HibernateExpression ex = new CompareExpression("pack", pack,
		// CompareType.Equal);
		// HibernateExpression statusEx = new CompareExpression("status", 0,
		// CompareType.Equal);
		// expressions.add(ex);
		// expressions.add(statusEx);
		// count = controller.getResultCount(ResourcePackReleation.class,
		// expressions);
		memcached.setAndSaveLocalMedium(key, count,
				5 * MemCachedClientWrapper.MINUTE);
		return count;
	}

	@SuppressWarnings("unchecked")
	public List<ResourcePackReleation> getResourcePackReleationsByOrder(
			int packId, int pageNo, int pageSize, int order, int listCount,
			int param) {
		// TODO:需要根据批价包更新时间,来判断是否需要更新关联集合内容
		// String key = Utility.getMemcachedKey(ResourcePackReleation.class,
		// String.valueOf(packId), String.valueOf(pageNo), String
		// .valueOf(pageSize));
		String key = Utility.getMemcachedKey(ResourcePackReleation.class,
				String.valueOf(packId), String.valueOf(pageNo), String
						.valueOf(pageSize), String.valueOf(order), String
						.valueOf(listCount), String.valueOf(param));
		List<ResourcePackReleation> rels = null;
		try {
			rels = (List<ResourcePackReleation>) memcached
					.getAndSaveLocalMedium(key);
			if (rels != null)
				return rels;
		} catch (Exception e) {
			logger.error("从Memcached中获取批价包关联列表信息出错!", e);
		}
		String hql = "select rel from  ResourcePackReleation rel,ResourceAll resource where rel.pack = ?  and resource.status = 0 and rel.status=0  and resource.id=rel.resourceId ";
		if (getNotBussinessPartnerIds().length() > 0) {
			hql += " and resource.cpId not in " + getNotBussinessPartnerIds()
					+ " ";
		}
		/***********************************************************************
		 * add by liuxh 091116 增加筛选条件
		 */
		// 1.全本(1) 2.连载(2) 3.已出版 (1)4.未出版(2)
		switch (param) {
		case 1:
			hql += " and resource.isFinished=1";
			break;
		case 2:
			hql += " and resource.isFinished=2";
			break;
		case 3:
			hql += " and resource.isOut=1";
			break;
		case 4:
			hql += " and resource.isOut=2";
			break;
		}
		/**
		 * end
		 */

		switch (order) {// 0按照排序ID降序、1按照ID降序、2按照点击数降序、5按照排序ID升序、6按照ID升序、7按照访问量+搜索（置顶）
		case 0:
			hql += " order by rel.order desc";
			break;
		case 1:
			hql += " order by rel.id desc";
			break;
		case 2:
			hql += " order by resource.downnum desc";
			break;// 点击总排行
		case 5:
			hql += " order by rel.order asc";
			break;
		case 6:
			hql += " order by rel.id asc";
			break;
		
		case 10:
			hql += " order by resource.downnum desc";
			break;// 人气总排行
		
		case 11:
			hql += " order by resource.downNumMonth desc";
			break;// 点击月排行
		case 12:
			hql += " order by resource.downNumWeek desc";
			break;// 点击周排行
		case 13:
			hql += " order by resource.downNumDate desc";
			break;// 点击日排行
		case 20:
			hql += " order by resource.searchNum desc";
			break;// 搜索总排行
		case 21:
			hql += " order by resource.searchNumMonth desc";
			break;// 搜索月排行
		case 22:
			hql += " order by resource.searchNumWeek desc";
			break;// 搜索周排行
		case 23:
			hql += " order by resource.searchNumDate desc";
			break;// 搜索日排行
		case 30:
			hql += " order by resource.favNum desc";
			break;// 收藏总排行
		case 31:
			hql += " order by resource.favNumMonth desc";
			break;// 收藏月排行
		case 32:
			hql += " order by resource.favNumWeek desc";
			break;// 收藏周排行
		case 33:
			hql += " order by resource.favNumDate desc";
			break;// 收藏日排行
		case 40:
			hql += " order by resource.orderNum desc";
			break;// 订购总排行
		case 41:
			hql += " order by resource.orderNumMonth desc";
			break;// 订购月排行
		case 42:
			hql += " order by resource.orderNumWeek desc";
			break;// 订购周排行
		case 43:
			hql += " order by resource.orderNumDate desc";
			break;// 订购日排行
		case 50:
			hql += " order by resource.msgNum desc";
			break;// 留言总排行
		case 51:
			hql += " order by resource.msgNumMonth desc";
			break;// 留言月排行
		case 52:
			hql += " order by resource.msgNumWeek desc";
			break;// 留言周排行
		case 53:
			hql += " order by resource.msgNumDate desc";
			break;// 留言日排行
		/**
		 * yuzs 添加新的排序规则 2009-11-05
		 */
		case 60:
			hql += " order by resource.rankingNum desc";
			break;// 访问量总排行
		case 61:
			hql += " order by resource.rankingNumMonth desc";
			break;// 访问量月排行
		case 62:
			hql += " order by resource.rankingNumWeek desc";
			break;// 访问量周排行
		case 63:
			hql += " order by resource.rankingNumDate desc";
			break;// 访问量日排行
		/**
		 * 增加投票排行
		 * 
		 * @author penglei 2009.11.20
		 */
		case 70:
			hql += " order by resource.voteNum desc";
			break;// 投票量总排行
		case 71:
			hql += " order by resource.voteNumMonth desc";
			break;// 投票量月排行
		case 72:
			hql += " order by resource.voteNumWeek desc";
			break;// 投票量周排行
		case 73:
			hql += " order by resource.voteNumDate desc";
			break;// 投票量日排行
		case 7:
			hql += " order by resource.searchTop desc,resource.rankingNum desc";
			break;// 访问量+搜索量排行
		/**
		 * 结束
		 */
		default:
			hql += " order by rel.order desc"; // 默认认规则5
		}

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>hql=" + hql);
		ResourcePack pack = new ResourcePack();
		pack.setId(packId);
		// if(listCount<0){
		// rels=controller.findBy(hql, pack);
		// }else{
		// rels=controller.findBy(hql, 1, listCount,
		// pack);
		// }
		rels = controller.findBy(hql, pageNo, pageSize, pack);
		if (listCount > 0) {
			int newCount = listCount - (pageNo - 1) * pageSize;
			if (rels.size() > newCount) {
				List<ResourcePackReleation> newList = new ArrayList<ResourcePackReleation>();
				for (int i = 0; i < newCount; i++) {
					newList.add(rels.get(i));
				}
				rels = newList;
			}
		}
		memcached.setAndSaveLocalMedium(key, rels,
				5 * MemCachedClientWrapper.MINUTE);
		return rels;
	}

	@SuppressWarnings("unchecked")
	public int getResourcePackReleationsByOrderCount(int packId, int order,
			int listCount, int param) {
		// TODO:需要根据批价包更新时间,来判断是否需要更新关联集合内容
		String key = Utility.getMemcachedKey(ResourcePackReleation.class,
				String.valueOf(packId), String.valueOf(listCount), String
						.valueOf(param), "count");
		Integer count = null;
		try {
			count = (Integer) memcached.getAndSaveLocalMedium(key);
			if (count != null) {
				return count;
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取批价包关联列表信息出错!", e);
		}
		String hql = "select rel from  ResourcePackReleation rel,ResourceAll resource where rel.pack = ?  and resource.status = 0 and rel.status=0  and resource.id=rel.resourceId ";
		if (getNotBussinessPartnerIds().length() > 0) {
			hql += " and resource.cpId not in " + getNotBussinessPartnerIds()
					+ " ";
		}
		// 1.全本(1) 2.连载(2) 3.已出版 (1)4.未出版(2)
		switch (param) {
		case 1:
			hql += " and resource.isFinished=1";
			break;
		case 2:
			hql += " and resource.isFinished=2";
			break;
		case 3:
			hql += " and resource.isOut=1";
			break;
		case 4:
			hql += " and resource.isOut=2";
			break;
		}
		// switch (order) {// 0按照排序ID降序、1按照ID降序、2按照点击数降序、5按照排序ID升序、6按照ID升序
		// case 0:
		// hql += " order by rel.order desc";
		// break;
		// case 1:
		// hql += " order by rel.id desc";
		// break;
		// case 2:
		// hql += " order by resource.downnum desc";
		// break;// 点击总排行
		// case 3:
		// hql += " order by rel.order desc, resource.downnum desc";
		// break;
		// case 5:
		// hql += " order by rel.order asc";
		// break;
		// case 6:
		// hql += " order by rel.id asc";
		// break;
		// case 11:
		// hql += " order by resource.downNumMonth desc";
		// break;// 点击月排行
		// case 12:
		// hql += " order by resource.downNumWeek desc";
		// break;// 点击周排行
		// case 13:
		// hql += " order by resource.downNumDate desc";
		// break;// 点击日排行
		// case 20:
		// hql += " order by resource.searchNum desc";
		// break;// 搜索总排行
		// case 21:
		// hql += " order by resource.searchNumMonth desc";
		// break;// 搜索月排行
		// case 22:
		// hql += " order by resource.searchNumWeek desc";
		// break;// 搜索周排行
		// case 23:
		// hql += " order by resource.searchNumDate desc";
		// break;// 搜索日排行
		// default:
		// hql += " order by resource.order asc"; // 默认认规则5
		// }

		ResourcePack pack = new ResourcePack();
		pack.setId(packId);
		count = controller.getResultCount(hql, pack).intValue();
		if (listCount > 0) {
			count = count <= listCount ? count : listCount;
		}
		// if(listCount<0){
		// count = controller.getResultCount(hql,pack).intValue();
		// }else{
		// count=controller.findBy(hql, 1, listCount, pack).size();
		// }
		memcached.setAndSaveLocalMedium(key, count,
				5 * MemCachedClientWrapper.MINUTE);
		return count;
	}

	@SuppressWarnings("unchecked")
	// public List<ResourcePackReleation> getRollResourceBagRelations(Class cla,
	// int firstResult, int MaxResults, Object[] objs) {
	// String pageHql = "from " + cla.getSimpleName()
	// + " as rpr where rpr.pack.id=?";
	// System.out.println("hql=" + pageHql + " :firstResult=" + firstResult
	// + " :MaxResults=" + MaxResults);
	// return hibernateOverWriteController.findBy(pageHql, firstResult,
	// MaxResults, objs);
	// }
	public Object getResourceChapter(String chapterId) {
		Class clasz = getNewChapter(Integer.parseInt(chapterId.substring(0, 1)));
		return controller.get(clasz, chapterId);
	}

	public List<EbookChapterDesc> getEbookChaptersByResourceID(String rid,
			int pageNo, int pageSize) {
		List<EbookChapterDesc> descs = getEbookChapterDescsByResourceID(rid);
		if (descs.size() == 0) {
			return descs;
		}
		return page(descs, pageNo, pageSize);
	}

	public Long getEbookChaptersByResourceIDCount(String rid) {
		return Long.valueOf(getEbookChapterDescsByResourceID(rid).size());
	}

	// /**
	// * 应该为被加入到批价包里的资源集合
	// */
	// public List<ResourceAll> getResourcesByAuthor(String authorid, int
	// pageNo,
	// int pageSize) {
	// // 得到被加入到批价包的资源的ID集合
	// // StringBuilder builder = new StringBuilder();
	// // builder.append(" from ResourceAll res ,ResourcePackReleation rel ");
	// // builder
	// // .append(" where res.authorId = ? and res.id =rel.resourceId");
	// // List<ResourceAll> reses = controller.findBy(
	// // builder.toString(),authorid);
	// //
	// // return reses;
	// Collection<HibernateExpression> expressions = new
	// ArrayList<HibernateExpression>();
	// HibernateExpression nameE = new CompareExpression("authorId", "%|"
	// + authorid + "|%", CompareType.Like);
	// expressions.add(nameE);
	// HibernateExpression statusE = new CompareExpression("status", 0,
	// CompareType.Equal);
	// expressions.add(statusE);
	// return controller.findBy(ResourceAll.class, pageNo, pageSize, "id",
	// false, expressions);
	//
	// }

	// public List<ResourceAll> getResourcesAllByAuthor(String authorid) {
	// Collection<HibernateExpression> expressions = new
	// ArrayList<HibernateExpression>();
	// HibernateExpression nameE = new CompareExpression("authorId", authorid,
	// CompareType.Like);
	// expressions.add(nameE);
	// return controller.findBy(ResourceAll.class, 1, 10000, "id", false,
	// expressions);
	// }

	// public int getResourcesAllByAuthor(String authorid) {
	// Collection<HibernateExpression> expressions = new
	// ArrayList<HibernateExpression>();
	// HibernateExpression nameE = new CompareExpression("authorId", "%|"
	// + authorid + "|%", CompareType.Like);
	// expressions.add(nameE);
	// HibernateExpression statusE = new CompareExpression("status", 0,
	// CompareType.Equal);
	// expressions.add(statusE);
	// return controller.getResultCount(ResourceAll.class, expressions)
	// .intValue();
	// }

	public List<Integer> getResourceTypeID(String rid) {
		// 更新资源和类别关系时清空该缓存
		String key = Utility.getMemcachedKey(ResourceResType.class, rid);
		List<Integer> typeIds = null;
		try {
			typeIds = (List<Integer>) memcached.getAndSaveLocalMedium(key);
			if (typeIds != null)
				return typeIds;
		} catch (Exception e) {
			logger.error("从Memcached中获取资源关联类型信息出错!", e);
		}
		List<ResourceResType> list = controller.findBy(ResourceResType.class,
				"rid", rid);
		typeIds = new ArrayList<Integer>();
		for (ResourceResType pr : list) {
			typeIds.add(pr.getResTypeId());
		}
		memcached.setAndSaveLocalMedium(key, typeIds,
				72 * MemCachedClientWrapper.HOUR);
		return typeIds;
	}

	// public int getResourceByTypeIdCount(int typeId) {
	// // Collection<HibernateExpression> expressions = new
	// // ArrayList<HibernateExpression>();
	// // HibernateExpression nameE = new CompareExpression("resTypeId",
	// // typeId,
	// // CompareType.Equal);
	// // expressions.add(nameE);
	// // return controller.getResultCount(ResourceResType.class, expressions)
	// // .intValue();
	// String hql =
	// "select resource from ResourceAll resource , ResourceResType rtype where
	// rtype.resTypeId = ? and rtype.rid=resource.id and resource.status=0 ";
	// String key = Utility.getMemcachedKey(ResourceResType.class, String
	// .valueOf(typeId), "count");
	// Integer count = null;
	// try {
	// count = (Integer) memcached.getAndSaveLocalMedium(key);
	// if (count != null) {
	// return count;
	// }
	// } catch (Exception e) {
	// logger.error("从Memcached中获取资源类型关联资源信息总数出错!", e);
	// }
	// count = controller.getResultCount(hql, typeId).intValue();
	// memcached.setAndSaveLocalMedium(key, count,
	// 1 * MemCachedClientWrapper.HOUR);
	// return count;
	//
	// }

	// public List<ResourceAll> getResourceByTypeId(int typeId, int pageNo,
	// int pageSize) {
	// // Collection<HibernateExpression> expressions = new
	// // ArrayList<HibernateExpression>();
	// // HibernateExpression nameE = new CompareExpression("resTypeId",
	// // typeId,
	// // CompareType.Equal);
	// // expressions.add(nameE);
	// // String rid = "";
	// // List<ResourceResType> list = controller.findBy(ResourceResType.class,
	// // pageNo, pageSize, "rid", false, expressions);
	// // List resIds = new ArrayList();
	// // for (ResourceResType pr : list) {
	// // rid = pr.getRid();
	// // resIds.add(rid);
	// // }
	// // return resIds;
	// String hql =
	// "select resource from ResourceAll resource , ResourceResType rtype where
	// rtype.resTypeId = ? and rtype.rid=resource.id and resource.status=0 ";
	// String key = Utility.getMemcachedKey(ResourceResType.class, String
	// .valueOf(typeId), String.valueOf(pageNo), String
	// .valueOf(pageSize));
	// List<ResourceAll> reses = null;
	// try {
	// reses = (List<ResourceAll>) memcached.getAndSaveLocalMedium(key);
	// if (reses != null)
	// return reses;
	// } catch (Exception e) {
	// logger.error("从Memcached中获取资源类型关联资源信息出错!", e);
	// }
	//
	// reses = controller.findBy(hql, pageNo, pageSize, typeId);
	// memcached.setAndSaveLocalMedium(key, reses,
	// 1 * MemCachedClientWrapper.MINUTE);
	// return reses;
	// }

	public ResourceType getResourceType(int id) {
		// 资源类型变更时，更新该缓存
		String key = Utility.getMemcachedKey(ResourceType.class, String
				.valueOf(id));
		ResourceType type = null;
		try {
			type = (ResourceType) memcached.getAndSaveLocalMedium(key);
			if (type != null)
				return type;
		} catch (Exception e) {
			logger.error("从Memcached中获取资源类型信息出错!", e);
		}
		type = controller.get(ResourceType.class, id);
		memcached.setAndSaveLocalMedium(key, type,
				72 * MemCachedClientWrapper.HOUR);
		return type;
	}

	public ResourceAuthor getResourceAuthor(int id) {
		// 作者信息变更时，更新该缓存
		String key = Utility.getMemcachedKey(ResourceAuthor.class, String
				.valueOf(id));
		ResourceAuthor author;
		try {
			author = (ResourceAuthor) memcached.getAndSaveLocalMedium(key);
			if (author != null)
				return author;
		} catch (Exception e) {
			logger.error("从Memcached中获取作者信息出错!", e);
		}
		author = controller.get(ResourceAuthor.class, id);
		memcached.setAndSaveLocalMedium(key, author,
				72 * MemCachedClientWrapper.HOUR);
		return author;
	}

	public List<EbookChapterDesc> getEbookChapterDescsByResourceID(String rid) {
		String key = Utility
				.getMemcachedKey(ResourceAll.class, rid, "chapters");
		List<EbookChapterDesc> chapters = null;
		try {
			chapters = (List<EbookChapterDesc>) memcached
					.getAndSaveLocalLong(key);
			if (chapters != null) {
				return chapters;
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取资源章节信息出错!", e);
		}
		chapters = controller.findBy(EbookChapterDesc.class, "resourceId", rid,
				"chapterIndex", true);
		memcached.setAndSaveLong(key, chapters,
				72 * MemCachedClientWrapper.HOUR);
		return chapters;
	}

	public EbookChapterDesc getEbookChapterDesc(String chapterId) {
		String resourceId = getResourceIdfromChapterId(chapterId);
		List<EbookChapterDesc> chapters = getEbookChapterDescsByResourceID(resourceId);
		for (EbookChapterDesc desc : chapters) {
			if (desc.getId().equals(chapterId)) {
				return desc;
			}
		}
		return null;
	}

	public ComicsChapter getComicsChapterById(String chapterId) {
		String resourceId = getResourceIdfromChapterId(chapterId);
		List<ComicsChapter> chapters = getComicsChaptersByResourceId(resourceId);
		for (ComicsChapter desc : chapters) {
			if (desc.getId().equals(chapterId)) {
				return desc;
			}
		}
		return null;
	}

	public int getComicsChaptersByResourceIDCount(String rid) {

		return getComicsChaptersByResourceId(rid).size();
	}

	public List<ComicsChapter> getComicsChaptersByResourceId(String rid) {
		String key = Utility
				.getMemcachedKey(ResourceAll.class, rid, "chapters");
		List<ComicsChapter> chapters = null;
		try {
			chapters = (List<ComicsChapter>) memcached.getAndSaveLocalLong(key);
			if (chapters != null) {
				return chapters;
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取资源章节信息出错!", e);
		}
		chapters = controller.findBy(ComicsChapter.class, "resourceId", rid,
				"chapterIndex", true);
		memcached.setAndSaveLong(key, chapters,
				72 * MemCachedClientWrapper.HOUR);
		return chapters;
	}

	public List<ComicsChapter> getComicsChaptersByResourceId(String rid,
			int pageNo, int pageSize) {
		List<ComicsChapter> descs = getComicsChaptersByResourceId(rid);
		if (descs.size() == 0) {
			return descs;
		}
		return page(descs, pageNo, pageSize);
	}

	public EbookTome getEbookTomeById(String tomeId) {
		String resourceId = getResourceIdfromTomeId(tomeId);
		List<EbookTome> tomes = getEbookTomeByResourceId(resourceId, 1, 100);
		for (EbookTome tome : tomes) {
			if (tome.getId().equals(tomeId)) {
				return tome;
			}
		}
		return null;
	}

	public List<EbookTome> getEbookTomeByResourceId(String rid, int pageNo,
			int pageSize) {
		String key = Utility.getMemcachedKey(EbookTome.class, rid, "tomes",
				String.valueOf(pageNo), String.valueOf(pageSize));
		List<EbookTome> tomes = null;
		try {
			tomes = (List<EbookTome>) memcached.getAndSaveLocalLong(key);
			if (tomes != null) {
				return tomes;
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取资源卷信息出错!", e);
		}

		// tomes = controller.findBy(EbookTome.class, "resourceId", rid,
		// "tomeIndex", true);
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression nameE = new CompareExpression("resourceId", rid,
				CompareType.Equal);
		expressions.add(nameE);
		tomes = controller.findBy(EbookTome.class, pageNo, pageSize,
				"tomeIndex", true, expressions);
		memcached.setAndSaveLong(key, tomes, 72 * MemCachedClientWrapper.HOUR);
		return tomes;

	}

	public int getEbookTomeCount(String rid) {
		String key = Utility.getMemcachedKey(EbookTome.class, rid, "count");
		Long count = 0L;
		try {
			count = (Long) memcached.getAndSaveLocalLong(key);
			if (count != null) {
				return Integer.parseInt(count.toString());
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取资源卷总数信息出错!", e);
		}

		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression nameE = new CompareExpression("resourceId", rid,
				CompareType.Equal);
		expressions.add(nameE);
		count = controller.getResultCount(EbookTome.class, expressions);
		memcached.setAndSaveLong(key, count, 72 * MemCachedClientWrapper.HOUR);
		return Integer.parseInt(count.toString());
	}

	public MagazineChapterDesc getMagazineChapterDescById(String chapterId) {
		String resourceId = getResourceIdfromChapterId(chapterId);
		List<MagazineChapterDesc> chapters = getMagazineChaptersByResourceID(resourceId);
		for (MagazineChapterDesc desc : chapters) {
			if (desc.getId().equals(chapterId)) {
				return desc;
			}
		}
		return null;
	}

	public List<MagazineChapterDesc> getMagazineChaptersByResourceID(String rid) {
		String key = Utility
				.getMemcachedKey(ResourceAll.class, rid, "chapters");
		List<MagazineChapterDesc> chapters = null;
		try {
			chapters = (List<MagazineChapterDesc>) memcached
					.getAndSaveLocalLong(key);
			if (chapters != null) {
				return chapters;
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取资源章节信息出错!", e);
		}
		chapters = controller.findBy(MagazineChapterDesc.class, "resourceId",
				rid, "chapterIndex", true);
		memcached.setAndSaveLong(key, chapters,
				72 * MemCachedClientWrapper.HOUR);
		return chapters;
	}

	public List<MagazineChapterDesc> getMagazineChaptersByResourceID(
			String rid, int pageNo, int pageSize) {
		List<MagazineChapterDesc> descs = getMagazineChaptersByResourceID(rid);
		if (descs.size() == 0) {
			return descs;
		}
		return page(descs, pageNo, pageSize);
	}

	public int getMagazineChaptersByResourceIDCount(String rid) {

		return getMagazineChaptersByResourceID(rid).size();
	}

	public NewsPapersChapterDesc getNewsPapersChapterDescById(String chapterId) {
		String resourceId = getResourceIdfromChapterId(chapterId);
		List<NewsPapersChapterDesc> chapters = getNewsPapersChaptersByResourceID(resourceId);
		for (NewsPapersChapterDesc desc : chapters) {
			if (desc.getId().equals(chapterId)) {
				return desc;
			}
		}
		return null;
	}

	public String browseResourceChapter(String chapterId, boolean isNotNext) {
		// 根据章节ID判断资源类型
		String resourceId = getResourceIdfromChapterId(chapterId);
		if (chapterId.substring(0, 1).startsWith(
				String.valueOf(ResourceType.TYPE_BOOK))) {// 图书
			List<EbookChapterDesc> chapters = getEbookChapterDescsByResourceID(resourceId);
			for (int i = 0; i < chapters.size(); i++) {
				EbookChapterDesc desc = chapters.get(i);
				if (desc.getId().equals(chapterId)) {
					if (isNotNext) {// 上一章
						if ((i - 1) < 0)
							return "";// 无上一章
						return chapters.get(i - 1).getId();
					} else {// 下一章
						if ((i + 1) >= chapters.size())
							return "";// 无下一章
						return chapters.get(i + 1).getId();
					}
				}
			}
		} else if (chapterId.substring(0, 1).startsWith(
				String.valueOf(ResourceType.TYPE_COMICS))) {// 漫画
			List<ComicsChapter> chapters = getComicsChaptersByResourceId(resourceId);
			for (int i = 0; i < chapters.size(); i++) {
				ComicsChapter desc = chapters.get(i);
				if (desc.getId().equals(chapterId)) {
					if (isNotNext) {// 上一章
						if ((i - 1) < 0)
							return "";// 无上一章
						return chapters.get(i - 1).getId();
					} else {// 下一章
						if ((i + 1) >= chapters.size())
							return "";// 无下一章
						return chapters.get(i + 1).getId();
					}
				}
			}
		} else if (chapterId.substring(0, 1).startsWith(
				String.valueOf(ResourceType.TYPE_NEWSPAPERS))) {// 报纸
			List<NewsPapersChapterDesc> chapters = getNewsPapersChaptersByResourceID(resourceId);
			for (int i = 0; i < chapters.size(); i++) {
				NewsPapersChapterDesc desc = chapters.get(i);
				if (desc.getId().equals(chapterId)) {
					if (isNotNext) {// 上一章
						if ((i - 1) < 0)
							return "";// 无上一章
						return chapters.get(i - 1).getId();
					} else {// 下一章
						if ((i + 1) >= chapters.size())
							return "";// 无下一章
						return chapters.get(i + 1).getId();
					}
				}
			}
		} else if (chapterId.substring(0, 1).startsWith(
				String.valueOf(ResourceType.TYPE_MAGAZINE))) {// 杂志
			List<MagazineChapterDesc> chapters = getMagazineChaptersByResourceID(resourceId);
			for (int i = 0; i < chapters.size(); i++) {
				MagazineChapterDesc desc = chapters.get(i);
				if (desc.getId().equals(chapterId)) {
					if (isNotNext) {// 上一章
						if ((i - 1) < 0)
							return "";// 无上一章
						return chapters.get(i - 1).getId();
					} else {// 下一章
						if ((i + 1) >= chapters.size())
							return "";// 无下一章
						return chapters.get(i + 1).getId();
					}
				}
			}
		}
		return "";
	}

	public List<NewsPapersChapterDesc> getNewsPapersChaptersByResourceID(
			String rid) {
		String key = Utility
				.getMemcachedKey(ResourceAll.class, rid, "chapters");
		List<NewsPapersChapterDesc> chapters = null;
		try {
			chapters = (List<NewsPapersChapterDesc>) memcached
					.getAndSaveLocalLong(key);
			if (chapters != null) {
				return chapters;
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取资源章节信息出错!", e);
		}
		chapters = controller.findBy(NewsPapersChapterDesc.class, "resourceId",
				rid, "chapterIndex", true);
		memcached.setAndSaveLong(key, chapters,
				72 * MemCachedClientWrapper.HOUR);
		return chapters;
	}

	public List<NewsPapersChapterDesc> getNewsPapersChaptersByResourceID(
			String rid, int pageNo, int pageSize) {
		List<NewsPapersChapterDesc> descs = getNewsPapersChaptersByResourceID(rid);
		if (descs.size() == 0) {
			return descs;
		}
		return page(descs, pageNo, pageSize);
	}

	public int getNewsPapersChaptersByResourceIDCount(String rid) {
		return getNewsPapersChaptersByResourceID(rid).size();
	}

	public ResourcePackReleation browseResourcePackReleation(int relId,
			boolean isNotNext) {
		String key = Utility.getMemcachedKey(ResourcePackReleation.class,
				String.valueOf(relId), String.valueOf(isNotNext));
		Object browseRel = null;
		try {
			browseRel = memcached.getAndSaveLocalMedium(key);
		} catch (Exception e) {
			logger.error("从Memcached中获取资源上一条、下一条信息出错!", e);
		}
		if (browseRel == null) {
			ResourcePackReleation rel = getResourcePackReleation(relId);
			if (rel == null)
				return null;
			StringBuilder builder = new StringBuilder();
			builder.append(" from ResourcePackReleation rel ");
			if (isNotNext) {
				builder
						.append(" where rel.id < ? and rel.pack = ? and status = 0 order by rel.order desc,rel.id desc");

			} else {
				builder
						.append(" where rel.id > ? and rel.pack = ? and status = 0 order by rel.order asc,rel.id asc");

			}
			List<ResourcePackReleation> rels = controller.findBy(builder
					.toString(), 1, 1, relId, rel.getPack());
			if (rels.size() > 0) {
				browseRel = rels.get(0);
			}
			if (browseRel == null) {
				browseRel = new NullObject();
			}
			memcached.setAndSaveLocalMedium(key, browseRel,
					5 * MemCachedClientWrapper.MINUTE);
		}
		if (browseRel instanceof NullObject)
			return null;
		return (ResourcePackReleation) browseRel;
	}

	/**
	 * 根据卷ID获取漫画章节列表
	 * 
	 * @param tomeId
	 * @return
	 */
	public List<ComicsChapter> getComicsChapterByTomeId(String tomeId) {
		String resourceId = getResourceIdfromTomeId(tomeId);
		List<ComicsChapter> chapters = getComicsChaptersByResourceId(resourceId);
		List<ComicsChapter> tomeChapters = new ArrayList<ComicsChapter>();
		for (ComicsChapter chapter : chapters) {
			if (chapter.getTomeId().equals(tomeId)) {
				tomeChapters.add(chapter);
			}
		}
		return tomeChapters;
	}

	public int getComicsChapterCountByTomeId(String tomeId) {
		return getComicsChapterByTomeId(tomeId).size();
	}

	public List<ComicsChapter> getComicsChapterByTomeId(String tomeId,
			int pageNo, int pageSize) {
		List<ComicsChapter> descs = getComicsChapterByTomeId(tomeId);
		if (descs.size() == 0) {
			return descs;
		}
		return page(descs, pageNo, pageSize);
	}

	/**
	 * 根据卷ID获取报纸章节列表
	 * 
	 * @param tomeId
	 * @return
	 */
	public List<NewsPapersChapterDesc> getNewsPapersChapterDescByTomeId(
			String tomeId) {
		String resourceId = getResourceIdfromTomeId(tomeId);
		List<NewsPapersChapterDesc> chapters = getNewsPapersChaptersByResourceID(resourceId);
		List<NewsPapersChapterDesc> tomeChapters = new ArrayList<NewsPapersChapterDesc>();
		for (NewsPapersChapterDesc chapter : chapters) {
			if (chapter.getTomeId().equals(tomeId)) {
				tomeChapters.add(chapter);
			}
		}
		return tomeChapters;
	}

	public int getNewsPapersChapterDescCountByTomeId(String tomeId) {
		return getNewsPapersChapterDescByTomeId(tomeId).size();
	}

	public List<NewsPapersChapterDesc> getNewsPapersChapterDescByTomeId(
			String tomeId, int pageNo, int pageSize) {
		List<NewsPapersChapterDesc> descs = getNewsPapersChapterDescByTomeId(tomeId);
		if (descs.size() == 0) {
			return descs;
		}
		return page(descs, pageNo, pageSize);
	}

	/**
	 * 根据卷ID获取杂志章节列表
	 * 
	 * @param tomeId
	 * @return
	 */
	public List<MagazineChapterDesc> getMagazineChapterDescByTomeId(
			String tomeId) {
		String resourceId = getResourceIdfromTomeId(tomeId);
		List<MagazineChapterDesc> chapters = getMagazineChaptersByResourceID(resourceId);
		List<MagazineChapterDesc> tomeChapters = new ArrayList<MagazineChapterDesc>();
		for (MagazineChapterDesc chapter : chapters) {
			if (chapter.getTomeId().equals(tomeId)) {
				tomeChapters.add(chapter);
			}
		}
		return tomeChapters;
	}

	public int getMagazineChapterDescCountByTomeId(String tomeId) {
		return getMagazineChapterDescByTomeId(tomeId).size();
	}

	public List<MagazineChapterDesc> getMagazineChapterDescByTomeId(
			String tomeId, int pageNo, int pageSize) {
		List<MagazineChapterDesc> descs = getMagazineChapterDescByTomeId(tomeId);
		if (descs.size() == 0) {
			return descs;
		}
		return page(descs, pageNo, pageSize);
	}

	/**
	 * 根据卷ID获取图书章节列表
	 * 
	 * @param tomeId
	 * @return
	 */
	public List<EbookChapterDesc> getEbookChapterDescByTomeId(String tomeId) {
		String resourceId = getResourceIdfromTomeId(tomeId);
		List<EbookChapterDesc> chapters = getEbookChapterDescsByResourceID(resourceId);
		List<EbookChapterDesc> tomeChapters = new ArrayList<EbookChapterDesc>();
		for (EbookChapterDesc chapter : chapters) {
			if (chapter.getTomeId().equals(tomeId)) {
				tomeChapters.add(chapter);
			}
		}
		return tomeChapters;
	}

	public int getEbookChapterDescCountByTomeId(String tomeId) {
		return getEbookChapterDescByTomeId(tomeId).size();
	}

	public List<EbookChapterDesc> getEbookChapterDescByTomeId(String tomeId,
			int pageNo, int pageSize) {
		List<EbookChapterDesc> descs = getEbookChapterDescByTomeId(tomeId);
		if (descs.size() == 0) {
			return descs;
		}
		return page(descs, pageNo, pageSize);
	}

	public List<ResourcePackReleation> getResourcePackReleationByResourceId(
			String rid, boolean isIphone) {
		String key = Utility.getMemcachedKey(ResourcePackReleation.class,
				"releation", rid);
		List<ResourcePackReleation> rels = null;
		try {
			rels = (List<ResourcePackReleation>) memcached
					.getAndSaveLocalMedium(key);
			if (rels != null) {
				return rels;
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取资源关联批价包信息出错!", e);
		}
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!isIphone) {
			NullExpression ex = new NullExpression("feeId");
			hibernateExpressions.add(ex);
		}
		HibernateExpression ey = new CompareExpression("resourceId", rid,
				CompareType.Equal);
		hibernateExpressions.add(ey);
		rels = controller.findBy(ResourcePackReleation.class, 1, 100,
				hibernateExpressions);
		// rels = controller.findBy(ResourcePackReleation.class, "resourceId",
		// rid);
		memcached.setAndSaveLocalMedium(key, rels,
				1 * MemCachedClientWrapper.MINUTE);
		return rels;
	}

	// public List<ResourceAll> searchResult(Integer resourceType, String
	// search,
	// String keyword, int pageNo, int pageSize) {
	// if (StringUtils.isEmpty(keyword)) {
	// return new ArrayList<ResourceAll>();
	// }
	// if (resourceType.equals(ResourceType.TYPE_BOOK)) {
	// Collection<HibernateExpression> expressions = new
	// ArrayList<HibernateExpression>();
	// HibernateExpression nameE = new CompareExpression("name", "%"
	// + keyword + "%", CompareType.Like);
	// HibernateExpression statusEx = new CompareExpression("status", 0,
	// CompareType.Equal);
	// expressions.add(nameE);
	// expressions.add(statusEx);
	// return controller.findBy(Ebook.class, pageNo, pageSize, "id",
	// false, expressions);
	// }
	// return new ArrayList<ResourceAll>();
	// }

	/**
	 * 新修改方法 yuzs 2009-11-05
	 */
	public List<ResourcePackReleation> searchResource(Integer resourceType,
			int searchType, String keyword, ResourcePack pack, int pageNum,
			int pageSize, int order) {

		if (StringUtils.isEmpty(keyword)) {
			return new ArrayList<ResourcePackReleation>();
		}
		String hql = "";
		if (searchType == 1) {// 按名称
			hql = "select rel from  ResourcePackReleation rel,ResourceAll resource where rel.pack = ?  and  resource.name like ?  and resource.status = 0 and rel.status=0 and  resource.id like ?  and resource.id=rel.resourceId";
			if (getNotBussinessPartnerIds().length() > 0) {
				hql += " and resource.cpId not in "
						+ getNotBussinessPartnerIds() + " ";
			}
		} else if (searchType == 2) {// 按作者
			hql = "select rel from  ResourcePackReleation rel,ResourceAll resource , ResourceAuthor author where rel.pack = ? and rel.resourceId=resource.id and resource.status=0 and rel.status=0  and author.name like ?  and resource.id like ? and resource.authorId like ('%'||author.id||'%') ";
			if (getNotBussinessPartnerIds().length() > 0) {
				hql += " and resource.cpId not in "
						+ getNotBussinessPartnerIds() + " ";
			}
			// String hql = "select resource.id from ResourceAll resource where
			// resource.authorId like ? and resource.status = 0 and resource.id
			// like ? ";
		} else if (searchType == 3 || searchType == 4) {// 按关键字
			hql = "select rel from  ResourcePackReleation rel,ResourceAll resource where  rel.pack = ? and  resource.RKeyword like ?  and resource.status = 0 and rel.status=0 and  resource.id like ?  and resource.id=rel.resourceId";
			if (getNotBussinessPartnerIds().length() > 0) {
				hql += " and resource.cpId not in "
						+ getNotBussinessPartnerIds() + " ";
			}
		} else if (searchType == 5) {// 按出版社
			hql = "select rel from  ResourcePackReleation rel,ResourceAll resource where rel.pack=? and   resource.publisher like ?  and resource.status = 0 and rel.status=0 and resource.id like ? and resource.id=rel.resourceId";
			if (getNotBussinessPartnerIds().length() > 0) {
				hql += " and resource.cpId not in "
						+ getNotBussinessPartnerIds() + " ";
			}
		}
		if (hql != null && !"".equals(hql)) {
			switch (order) {// 0按照排序ID降序、1按照ID降序、2按照点击数降序、5按照排序ID升序、6按照ID升序、7按照访问量+搜索（置顶）
			case 0:
				hql += " order by rel.order desc";
				break;
			case 1:
				hql += " order by rel.id desc";
				break;
			case 2:
				hql += " order by resource.downnum desc";
				break;// 点击总排行
			case 5:
				hql += " order by rel.order asc";
				break;
			case 6:
				hql += " order by rel.id asc";
				break;
			case 11:
				hql += " order by resource.downNumMonth desc";
				break;// 点击月排行
			case 12:
				hql += " order by resource.downNumWeek desc";
				break;// 点击周排行
			case 13:
				hql += " order by resource.downNumDate desc";
				break;// 点击日排行
			case 20:
				hql += " order by resource.searchNum desc";
				break;// 搜索总排行
			case 21:
				hql += " order by resource.searchNumMonth desc";
				break;// 搜索月排行
			case 22:
				hql += " order by resource.searchNumWeek desc";
				break;// 搜索周排行
			case 23:
				hql += " order by resource.searchNumDate desc";
				break;// 搜索日排行
			case 30:
				hql += " order by resource.favNum desc";
				break;// 收藏总排行
			case 31:
				hql += " order by resource.favNumMonth desc";
				break;// 收藏月排行
			case 32:
				hql += " order by resource.favNumWeek desc";
				break;// 收藏周排行
			case 33:
				hql += " order by resource.favNumDate desc";
				break;// 收藏日排行
			case 40:
				hql += " order by resource.orderNum desc";
				break;// 订购总排行
			case 41:
				hql += " order by resource.orderNumMonth desc";
				break;// 订购月排行
			case 42:
				hql += " order by resource.orderNumWeek desc";
				break;// 订购周排行
			case 43:
				hql += " order by resource.orderNumDate desc";
				break;// 订购日排行
			case 50:
				hql += " order by resource.msgNum desc";
				break;// 留言总排行
			case 51:
				hql += " order by resource.msgNumMonth desc";
				break;// 留言月排行
			case 52:
				hql += " order by resource.msgNumWeek desc";
				break;// 留言周排行
			case 53:
				hql += " order by resource.msgNumDate desc";
				break;// 留言日排行
			/**
			 * yuzs 添加新的排序规则 2009-11-05
			 */
			case 60:
				hql += " order by resource.rankingNum desc";
				break;// 访问量总排行
			case 61:
				hql += " order by resource.rankingNumMonth desc";
				break;// 访问量月排行
			case 62:
				hql += " order by resource.rankingNumWeek desc";
				break;// 访问量周排行
			case 63:
				hql += " order by resource.rankingNumDate desc";
				break;// 访问量日排行
			case 7:
				hql += " order by resource.searchTop desc,resource.rankingNum desc";
				break;// 访问量+搜索量(置顶)排行
			/**
			 * 结束
			 */
			default:
				hql += " order by rel.order desc"; // 默认认规则5
			}
			/**
			 * // * 修改Bug 按出版社条件搜索资源增加分页条件 // * modify by liuxh 09-11-16 //
			 */
			// if (searchType == 5) {
			// return controller.findBy(hql, pageNum, pageSize,pack, "%" +
			// keyword + "%",
			// resourceType.toString() + "%");
			// } else {
			/**
			 * // * end //
			 */
			return controller.findBy(hql, pageNum, pageSize, pack, "%"
					+ keyword + "%", resourceType.toString() + "%");
			// }
		} else {
			return new ArrayList<ResourcePackReleation>();
		}
	}

	public List<ResourcePackReleation> searchResource(Integer resourceType,
			int searchType, String keyword, ResourcePack pack, int pageNum,
			int pageSize) {
		if (StringUtils.isEmpty(keyword)) {
			return new ArrayList<ResourcePackReleation>();
		}
		// List<ResourcePackReleation> rels = new
		// ArrayList<ResourcePackReleation>();
		if (searchType == 1) {// 按名称
			String hql = "select rel from  ResourcePackReleation rel,ResourceAll resource where rel.pack = ?  and  resource.name like ?  and resource.status = 0 and rel.status=0 and  resource.id like ?  and resource.id=rel.resourceId";
			if (getNotBussinessPartnerIds().length() > 0) {
				hql += " and resource.cpId not in "
						+ getNotBussinessPartnerIds() + " ";
			}
			return controller.findBy(hql, pageNum, pageSize, pack, "%"
					+ keyword + "%", resourceType.toString() + "%");

		} else if (searchType == 2) {// 按作者
			String hql = "select rel from  ResourcePackReleation rel,ResourceAll resource , ResourceAuthor author where rel.pack = ? and rel.resourceId=resource.id and resource.status=0 and rel.status=0  and author.name like ?  and resource.id like ? and resource.authorId like ('%'||author.id||'%') ";
			if (getNotBussinessPartnerIds().length() > 0) {
				hql += " and resource.cpId not in "
						+ getNotBussinessPartnerIds() + " ";
			}
			// String hql = "select resource.id from ResourceAll resource where
			// resource.authorId like ? and resource.status = 0 and resource.id
			// like ? ";
			return controller.findBy(hql, pageNum, pageSize, pack, "%"
					+ keyword + "%", resourceType.toString() + "%");
		} else if (searchType == 3 || searchType == 4) {// 按关键字
			String hql = "select rel from  ResourcePackReleation rel,ResourceAll resource where  rel.pack = ? and  resource.RKeyword like ?  and resource.status = 0 and rel.status=0 and  resource.id like ?  and resource.id=rel.resourceId";
			if (getNotBussinessPartnerIds().length() > 0) {
				hql += " and resource.cpId not in "
						+ getNotBussinessPartnerIds() + " ";
			}
			return controller.findBy(hql, pageNum, pageSize, pack, "%"
					+ keyword + "%", resourceType.toString() + "%");
		}
		/**
		 * 增加搜索条件 按出版社搜索 add by liuxh 09-11-02
		 */
		else if (searchType == 5) {// 按出版社
			String hql = "select rel from  ResourcePackReleation rel,ResourceAll resource where rel.pack=? and   resource.publisher like ?  and resource.status = 0 and rel.status=0 and resource.id like ? and resource.id=rel.resourceId";
			if (getNotBussinessPartnerIds().length() > 0) {
				hql += " and resource.cpId not in "
						+ getNotBussinessPartnerIds() + " ";
			}
			return controller.findBy(hql, pageNum, pageSize, pack, "%"
					+ keyword + "%", resourceType.toString() + "%");
		} else {
			return new ArrayList<ResourcePackReleation>();
		}

	}

	public int searchResourceCount(Integer resourceType, int searchType,
			String keyword, ResourcePack pack) {
		if (StringUtils.isEmpty(keyword)) {
			return 0;
		}
		if (searchType == 1) {// 按书名
			String hql = "select rel from  ResourcePackReleation rel,ResourceAll resource where rel.pack=? and resource.name like ? and resource.status = 0  and rel.status=0  and resource.id like ? and resource.id=rel.resourceId";
			if (getNotBussinessPartnerIds().length() > 0) {
				hql += " and resource.cpId not in "
						+ getNotBussinessPartnerIds() + " ";
			}
			return controller.getResultCount(hql, pack, "%" + keyword + "%",
					resourceType.toString() + "%").intValue();
		} else if (searchType == 2) {// 按作者
			String hql = "select rel from  ResourcePackReleation rel,ResourceAll resource , ResourceAuthor author where rel.pack = ? and rel.resourceId=resource.id and resource.status=0 and rel.status=0 and author.name like ? and resource.id like ? and resource.authorId like ('%'||author.id||'%') ";
			if (getNotBussinessPartnerIds().length() > 0) {
				hql += " and resource.cpId not in "
						+ getNotBussinessPartnerIds() + " ";
			}
			// String hql = "select resource.id from ResourceAll resource where
			// resource.authorId like ? and resource.status = 0 and resource.id
			// like ? ";
			return controller.getResultCount(hql, pack, "%" + keyword + "%",
					resourceType.toString() + "%").intValue();

		} else if (searchType == 3 || searchType == 4) {// 按关键字
			String hql = "select rel from  ResourcePackReleation rel,ResourceAll resource where rel.pack=? and   resource.RKeyword like ?  and resource.status = 0 and rel.status=0 and resource.id like ? and resource.id=rel.resourceId";
			if (getNotBussinessPartnerIds().length() > 0) {
				hql += " and resource.cpId not in "
						+ getNotBussinessPartnerIds() + " ";
			}
			return controller.getResultCount(hql, pack, "%" + keyword + "%",
					resourceType.toString() + "%").intValue();
		}
		/**
		 * 增加搜索条件 按出版社搜索 add by liuxh 09-11-02
		 */
		else if (searchType == 5) {// 按出版社
			String hql = "select rel from  ResourcePackReleation rel,ResourceAll resource where rel.pack=? and   resource.publisher like ?  and resource.status = 0 and rel.status=0 and resource.id like ? and resource.id=rel.resourceId";
			if (getNotBussinessPartnerIds().length() > 0) {
				hql += " and resource.cpId not in "
						+ getNotBussinessPartnerIds() + " ";
			}
			return controller.getResultCount(hql, pack, "%" + keyword + "%",
					resourceType.toString() + "%").intValue();
		} else {
			return 0;
		}
	}

	public List<ResourcePackReleation> getResourceByAuthorId(
			Integer resourceType, ResourcePack pack, String authorId,
			int pageNum, int pageSize) {
		String key = Utility.getMemcachedKey(ResourcePackReleation.class,
				String.valueOf(pack.getId()), resourceType.toString(),
				authorId, String.valueOf(pageNum), String.valueOf(pageSize));
		List<ResourcePackReleation> rers = null;
		try {
			rers = (List<ResourcePackReleation>) memcached
					.getAndSaveLocalMedium(key);
			if (rers != null)
				return rers;
		} catch (Exception e) {
			logger.error("从Memcached中获取作者相关作品列表信息出错!", e);
		}
		String hql = "select rel from  ResourcePackReleation rel,ResourceAll resource  where rel.pack = ? and rel.resourceId=resource.id and resource.status=0 and rel.status=0  and resource.id like ? and resource.authorId like ?  ";
		if (getNotBussinessPartnerIds().length() > 0) {
			hql += " and resource.cpId not in " + getNotBussinessPartnerIds()
					+ " ";
		}
		hql += " order by rel.id";
		rers = controller.findBy(hql, pageNum, pageSize, pack, resourceType
				.toString()
				+ "%", "%" + authorId + "%");
		memcached.setAndSaveLocalMedium(key, rers,
				10 * MemCachedClientWrapper.MINUTE);
		return rers;
	}

	public int getResourceCountByAuthorId(Integer resourceType,
			ResourcePack pack, String authorId) {
		String key = Utility
				.getMemcachedKey(ResourcePackReleation.class, pack.getId()
						.toString(), resourceType.toString(), authorId, "count");
		Integer count = null;
		try {
			count = (Integer) memcached.getAndSaveLocalMedium(key);
			if (count != null) {
				return count;
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取作者相关资源列表个数信息出错!", e);
		}
		String hql = "select rel from  ResourcePackReleation rel,ResourceAll resource  where rel.pack = ? and rel.resourceId=resource.id and resource.status=0 and rel.status=0  and resource.id like ? and resource.authorId like ? ";
		if (getNotBussinessPartnerIds().length() > 0) {
			hql += " and resource.cpId not in " + getNotBussinessPartnerIds()
					+ " ";
		}
		hql += " order by rel.id";
		count = controller.getResultCount(hql, pack,
				resourceType.toString() + "%", "%" + authorId + "%").intValue();
		memcached.setAndSaveLocalMedium(key, count,
				10 * MemCachedClientWrapper.MINUTE);
		return count;
	}

	// public int searchResultCount(Integer resourceType, String keyword) {
	// if (StringUtils.isEmpty(keyword)) {
	// return 0;
	// }
	// if (resourceType.equals(ResourceType.TYPE_BOOK)) {
	// Collection<HibernateExpression> expressions = new
	// ArrayList<HibernateExpression>();
	// HibernateExpression nameE = new CompareExpression("name", "%"
	// + keyword + "%", CompareType.Like);
	// HibernateExpression statusEx = new CompareExpression("status", 0,
	// CompareType.Equal);
	// expressions.add(nameE);
	// expressions.add(statusEx);
	// return controller.getResultCount(Ebook.class, expressions)
	// .intValue();
	// }
	// return 0;
	// }
	public List<String> getResourceAuthorListByPackId(int pageNo, int pageSize,
			ResourcePack pack, Integer resourceType, String inital) {
		List<String> id = null;
		String key = null;
		key = Utility.getMemcachedKey(ResourcePackReleation.class, String
				.valueOf(pack.getId()), resourceType.toString(), String
				.valueOf(pageNo), String.valueOf(pageSize), StringUtils
				.isNotEmpty(inital) ? inital : "");

		try {
			id = (List<String>) memcached.getAndSaveLocalMedium(key);
			if (id != null)
				return id;
		} catch (Exception e) {
			logger.error("从Memcached中获取作者ID集合信息出错!", e);
		}

		String hql = "select  distinct author.id,author.initialLetter   from  ResourcePackReleation rel,ResourceAll resource , ResourceAuthor author where rel.pack = ? and rel.resourceId=resource.id and resource.status=0 and rel.status=0 and resource.id like ?  ";
		if (StringUtils.isNotEmpty(inital)) {
			hql += " and author.initialLetter=?";
		}
		if (getNotBussinessPartnerIds().length() > 0) {
			hql += " and resource.cpId not in " + getNotBussinessPartnerIds()
					+ " ";
		}
		hql += " and resource.authorId like ('%'||author.id||'%')";
		hql += " order by author.initialLetter asc";
		Object[] objs = StringUtils.isNotEmpty(inital) ? new Object[] { pack,
				resourceType.toString() + "%", inital } : new Object[] { pack,
				resourceType.toString() + "%" };
		List<Object[]> ids = controller.findBy(hql, pageNo, pageSize, objs);
		id = new ArrayList();
		for (int i = 0; i < ids.size(); i++) {
			id.add(ids.get(i)[0].toString());
		}
		memcached.setAndSaveLocalMedium(key, id,
				2 * MemCachedClientWrapper.HOUR);
		return id;
	}

	public int getResourceAuthorListByPackIdCount(ResourcePack pack,
			Integer resourceType, String inital) {
		Integer count = null;
		String key = null;
		key = Utility.getMemcachedKey(ResourcePackReleation.class, "author",
				pack.getId().toString(), resourceType.toString(), "count",
				StringUtils.isNotEmpty(inital) ? inital : "");

		try {
			count = (Integer) memcached.getAndSaveLocalMedium(key);
			if (count != null) {
				return count;
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取作者ID集合个数信息出错!", e);
		}

		String hql = "select count (distinct author.id) from  ResourcePackReleation rel,ResourceAll resource , ResourceAuthor author where rel.pack = ? and rel.resourceId=resource.id and resource.status=0 and rel.status=0  and resource.id like ? ";

		if (StringUtils.isNotEmpty(inital)) {
			hql += " and author.initialLetter=?";
		}
		if (getNotBussinessPartnerIds().length() > 0) {
			hql += " and resource.cpId not in " + getNotBussinessPartnerIds()
					+ " ";
		}
		hql += " and resource.authorId like ('%'||author.id||'%') ";
		hql += " order by author.initialLetter asc  ";
		Object[] objs = StringUtils.isNotEmpty(inital) ? new Object[] { pack,
				resourceType.toString() + "%", inital } : new Object[] { pack,
				resourceType.toString() + "%" };
		List<Long> result = controller.findBy(hql, objs);
		count = result.get(0).intValue();
		memcached.setAndSaveLocalMedium(key, count,
				2 * MemCachedClientWrapper.HOUR);
		return count;
	}

	// public List<ResourceAuthor> getResourceAuthorAll() {
	// return controller.getAll(ResourceAuthor.class);
	// }

	public int getResourceAuthorAllCount() {
		return controller.getResultCount(ResourceAuthor.class).intValue();
	}

	// public List<ResourceType> getResourceTypeAll() {
	// return controller.getAll(ResourceType.class);
	// }

	public String getPreviewCoverImg(String resourceId, String coverImg) {
		String url = getResourceDirectory(resourceId);
		return url + coverImg.replaceAll("\\.", "75.");

	}

	public String getPreviewCoverImg(String resourceId, String coverImg,
			int size) {
		String url = getResourceDirectory(resourceId);
		return url + coverImg.replaceAll("\\.", size + ".");

	}

	public String getNormalImg(String img) {
		StringBuilder url = new StringBuilder();
		url.append(bussinessService.getVariables("media_url").getValue());
		url.append(img);
		return url.toString();
	}

	public String getChapterImg(String resourceId, String imgName) {
		if (resourceId.length() > 8) {
			resourceId = resourceId.substring(0, 8);
		}
		String url = getResourceDirectory(resourceId);
		return url + imgName;
	}

	public String[] getUebAddress(int relId) {
		StringBuilder url = new StringBuilder();
		url.append(bussinessService.getVariables("media_url").getValue());
		url.append("ueb/");
		url.append(relId / 1000);
		url.append("/");
		url.append(relId);
		url.append("_");
		String[] urls = new String[3];
		urls[0] = url.toString() + "128.ueb";
		urls[1] = url.toString() + "176.ueb";
		urls[2] = url.toString() + "240.ueb";
		return urls;

	}

	public long getResourceVisits(String resourceId) {
		ResourceAll resource = getResource(resourceId);

		return resource.getDownnum() == null ? 1 : resource.getDownnum()+1;
	}

	/**
	 * 增加资源的点击量
	 * 
	 * @param resourceId
	 * @return
	 */
	public long incrResourceVisits(String resourceId) {
		return getResourceVisits(resourceId);//resource.getDownnum() == null ? 1 : resource.getDownnum()+1;
//		StatisticsLog.logStat(1, resourceId);
//		ResourceAll resource = getResource(resourceId);
//		return (resource.getDownnum() == null ? 1 : resource.getDownnum()) + 1;
	}

	public Set<String> getAllResourceVisitKey() {
		Set<String> allKeys = new HashSet<String>();
		// allKeys.addAll(RESOURCE_VISITS);
		// RESOURCE_VISITS.clear();
		return allKeys;
	}
	public String getVideoResourceDirectory(String resourceId){
		StringBuilder url = new StringBuilder();
		url.append(bussinessService.getVariables("video_url").getValue());
		Integer resourceType = Integer.parseInt(resourceId.substring(0, 1));
		String key = "notfound";
		if (ResourceAll.RESOURCE_TYPE_BOOK.equals(resourceType)) {
			key = "ebook";
		} else if (ResourceAll.RESOURCE_TYPE_COMICS.equals(resourceType)) {
			key = "comics";
		} else if (ResourceAll.RESOURCE_TYPE_MAGAZINE.equals(resourceType)) {
			key = "magazine";
		} else if (ResourceAll.RESOURCE_TYPE_NEWSPAPER.equals(resourceType)) {
			key = "newspaper";
		}else if (ResourceAll.RESOURCE_TYPE_VIDEO.equals(resourceType)) {
			key = "video";
		}else if (ResourceAll.RESOURCE_TYPE_INFO.equals(resourceType)) {
			key = "infomation";
		}
		url.append(key);
		url.append("/");
		int id = Integer.parseInt(resourceId.substring(1));
		url.append(id / 1000);
		url.append("/");
		url.append(resourceId);
		url.append("/");
		return url.toString();
	}

	public String getResourceDirectory(String resourceId) {
		StringBuilder url = new StringBuilder();
		url.append(bussinessService.getVariables("media_url").getValue());
		Integer resourceType = Integer.parseInt(resourceId.substring(0, 1));
		String key = "notfound";
		if (ResourceAll.RESOURCE_TYPE_BOOK.equals(resourceType)) {
			key = "ebook";
		} else if (ResourceAll.RESOURCE_TYPE_COMICS.equals(resourceType)) {
			key = "comics";
		} else if (ResourceAll.RESOURCE_TYPE_MAGAZINE.equals(resourceType)) {
			key = "magazine";
		} else if (ResourceAll.RESOURCE_TYPE_NEWSPAPER.equals(resourceType)) {
			key = "newspaper";
		}else if (ResourceAll.RESOURCE_TYPE_VIDEO.equals(resourceType)) {
			key = "video";
		}else if (ResourceAll.RESOURCE_TYPE_INFO.equals(resourceType)) {
			key = "infomation";
		}
		url.append(key);
		url.append("/");
		int id = Integer.parseInt(resourceId.substring(1));
		url.append(id / 1000);
		url.append("/");
		url.append(resourceId);
		url.append("/");
		return url.toString();
	}

	private Class getNewChapter(Integer resourceType) {
		if (ResourceAll.RESOURCE_TYPE_BOOK.equals(resourceType)) {
			return EbookChapter.class;
		} else if (ResourceAll.RESOURCE_TYPE_COMICS.equals(resourceType)) {
			return ComicsChapter.class;
		} else if (ResourceAll.RESOURCE_TYPE_MAGAZINE.equals(resourceType)) {
			return MagazineChapter.class;
		} else if (ResourceAll.RESOURCE_TYPE_NEWSPAPER.equals(resourceType)) {
			return NewsPapersChapter.class;
		}
		return null;
	}

	private ResourceAll getResource(String resourceId, Integer resourceType) {
		if (ResourceAll.RESOURCE_TYPE_BOOK.equals(resourceType)) {
			return controller.get(Ebook.class, resourceId);
		} else if (ResourceAll.RESOURCE_TYPE_COMICS.equals(resourceType)) {
			return controller.get(Comics.class, resourceId);
		} else if (ResourceAll.RESOURCE_TYPE_MAGAZINE.equals(resourceType)) {
			return controller.get(Magazine.class, resourceId);
		} else if (ResourceAll.RESOURCE_TYPE_NEWSPAPER.equals(resourceType)) {
			return controller.get(NewsPapers.class, resourceId);
		}else if (ResourceAll.RESOURCE_TYPE_VIDEO.equals(resourceType)) {
			return controller.get(Video.class, resourceId);
		}else if (ResourceAll.RESOURCE_TYPE_INFO.equals(resourceType)) {
			return controller.get(Infomation.class, resourceId);
		}
		return controller.get(ResourceAll.class, resourceId);
	}

	/**
	 * 获取非商用的合作方ID
	 * 
	 * @return
	 */
	private String getNotBussinessPartnerIds() {
		String key = Utility.getMemcachedKey(Provider.class, "notbus");
		String ids = "";
		List<Provider> providers = null;
		try {
			providers = (List<Provider>) memcached.getAndSaveLocalMedium(key);
		} catch (Exception e) {
			logger.error("从Memcached中获取非商用合作方信息出错!", e);
		}
		if (providers == null) {
			HibernateExpression ex = new CompareExpression("status",
					Provider.STATUS_BUSSINESS, CompareType.NotEqual);
			List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
			expressions.add(ex);
			providers = controller.findBy(Provider.class, 1, Integer.MAX_VALUE,
					"id", false, expressions);
			memcached.setAndSaveLocalMedium(key, providers,
					10 * MemCachedClientWrapper.MINUTE);
		}

		for (Provider provider : providers) {
			ids += provider.getId() + ",";
		}
		if (ids.length() > 0) {
			ids = ids.substring(0, ids.length() - 1);
			ids = "( " + ids + " )";
		}
		return ids;
	}

	private String getResourceIdfromTomeId(String tomeId) {
		return tomeId.substring(0, tomeId.length() - 2);
	}

	private String getResourceIdfromChapterId(String chapterId) {
		return chapterId.substring(0, chapterId.length() - 3);
	}

	public List<ResourcePackReleation> getHotTopResourcesListOrderByDownnum(
			int packId, int pageNo, int pageSize, int type) {
		String key = Utility.getMemcachedKey(ResourcePackReleation.class,
				"hot", String.valueOf(packId));
		List<ResourcePackReleation> rels = null;
		try {
			rels = (List<ResourcePackReleation>) memcached
					.getAndSaveLocalMedium(key);
			if (rels != null) {
				return page(rels, pageNo, pageSize);
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取资源关联批价包信息出错!", e);
		}
		ResourcePack rp = new ResourcePack();
		rp.setId(packId);
		String hql = "select rel from ResourcePackReleation rel , ResourceAll res   where  rel.resourceId=res.id  and  rel.pack=? and  res.status=0 and rel.status=0  ";
		if (getNotBussinessPartnerIds().length() > 0) {
			hql += " and res.cpId not in " + getNotBussinessPartnerIds() + " ";
		}

		switch (type) {
		case 0:
			hql += " order by res.downnum desc ,rel.order asc";
			break;
		case 1:
			hql += " order by res.downNumMonth desc,rel.order asc";
			break;
		case 2:
			hql += " order by res.downNumWeek desc, rel.order asc";
			break;
		case 3:
			hql += " order by res.downNumDate desc,rel.order asc ";
			break;
		default:
			hql += " order by res.downnum desc ,rel.order asc";
		}
		// String sql =
		// "select t.id from (select rownum as rn,rel.id as id from
		// reader_resource_p_res_info rel , reader_resource_all res where
		// rel.Resource_id=res.Resource_id and REL.FEE_PACK_ID=? order by "
		// + "res.downnum desc, rel.Res_order desc) t where rn<=100 ";
		// Map map=new HashMap();
		// map.put("FEE_PACK_ID", Hibernate.INTEGER);
		// List list=SQLUtil.getFromApplicationContext().querySQL(sql,
		// null,packId);
		// return page(list, pageNo, pageSize);
		rels = controller.findBy(hql, 1, 100, rp);
		memcached.setAndSaveLocalMedium(key, rels,
				1 * MemCachedClientWrapper.HOUR);

		return page(rels, pageNo, pageSize);
	}

	/**
	 * 分页
	 * 
	 * @param list
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
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

	// private static Set<String> RESOURCE_VISITS = new HashSet<String>();

	public String getMaterialImg(Integer id) {
		StringBuilder url = new StringBuilder();
		url.append(bussinessService.getVariables("media_url").getValue());
		Material mater = controller.get(Material.class, id);
		url.append(mater.getFilename() + "." + mater.getExtName());
		return url.toString();
	}

	/**
	 * 搜索排行
	 * 
	 * @param packId
	 *            批价包
	 * @param type
	 *            排行参数 （1.月排行 2.周排行 3.日排行 0.总排行）
	 * @param pageNo
	 * @param pageSize
	 * @param listCount
	 *            最大条数
	 * @return
	 */
	public List<ResourcePackReleation> getSearchTopResourcesList(int packId,
			int type, int pageNo, int pageSize, int listCount) {
		String key = Utility.getMemcachedKey(ResourcePackReleation.class,
				"search", String.valueOf(packId), String.valueOf(type), String
						.valueOf(listCount), String.valueOf(pageNo), String
						.valueOf(pageSize));
		List<ResourcePackReleation> rels = null;
		try {
			rels = (List<ResourcePackReleation>) memcached
					.getAndSaveLocalMedium(key);
			if (rels != null) {
				return rels;
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取资源关联批价包信息出错!", e);
		}

		String hql = "select rel from ResourcePackReleation rel , ResourceAll res   where  rel.resourceId=res.id  and  rel.pack=? and  res.status=0 and rel.status=0 ";
		if (getNotBussinessPartnerIds().length() > 0) {
			hql += " and res.cpId not in " + getNotBussinessPartnerIds() + " ";
		}
		switch (type) {
		case 0:
			hql += " order by res.searchNum desc ";
			break;
		case 1:
			hql += " order by res.searchNumMonth desc ";
			break;
		case 2:
			hql += " order by res.searchNumWeek desc ";
			break;
		case 3:
			hql += " order by res.searchNumDate desc ";
			break;
		default:
			hql += " order by res.searchNum desc ";
		}
		ResourcePack rp = new ResourcePack();
		rp.setId(packId);
		rels = controller.findBy(hql, pageNo, pageSize, rp);
		if (listCount > 0) {
			int newCount = listCount - (pageNo - 1) * pageSize;
			if (rels.size() > newCount) {
				List<ResourcePackReleation> newList = new ArrayList<ResourcePackReleation>();
				for (int i = 0; i < newCount; i++) {
					newList.add(rels.get(i));
				}
				rels = newList;
			}
		}
		memcached.setAndSaveLocalMedium(key, rels,
				5 * MemCachedClientWrapper.MINUTE);

		return rels;
	}

	public int getSearchTopResourcesListCount(int packId, int listCount) {
		String key = Utility.getMemcachedKey(ResourcePackReleation.class,
				"searchtop", String.valueOf(packId), String.valueOf(listCount),
				"count");
		Integer count = null;
		try {
			count = (Integer) memcached.getAndSaveLocalMedium(key);
			if (count != null) {
				return count;
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取批价包关联列表信息出错!", e);
		}
		String hql = "select rel from ResourcePackReleation rel , ResourceAll res   where  rel.resourceId=res.id  and  rel.pack=? and  res.status=0 and rel.status=0 ";
		if (getNotBussinessPartnerIds().length() > 0) {
			hql += " and res.cpId not in " + getNotBussinessPartnerIds() + " ";
		}
		ResourcePack pack = new ResourcePack();
		pack.setId(packId);
		count = controller.getResultCount(hql, pack).intValue();
		if (listCount > 0) {
			count = count <= listCount ? count : listCount;
		}
		memcached.setAndSaveLocalMedium(key, count,
				5 * MemCachedClientWrapper.MINUTE);
		return count;
	}

	/***************************************************************************
	 * 推荐相关内容查询
	 * 
	 * 
	 * @param columnID
	 *            栏目ID
	 * @param resource
	 *            资源类
	 * @param size
	 *            显示数量
	 * @return
	 * @throws Exception
	 * @author penglei
	 */
	public List<ResourcePackReleation> findPertinence(String columnID,
			ResourceAll resource, String size) throws Exception {

		String hql = "select rpr from ResourcePackReleation rpr , ResourceAll ra where ra.id=rpr.resourceId and ra.name != ? and rpr.pack.id = ? and rpr.status = 0 and ra.status=0";
		String bookName = resource.getName();
		String keyWord = resource.getRKeyword();
		String title = "";
		String[] keyWords = keyWord.split("/");
		List list = new ArrayList();
		if (StringUtils.isEmpty(columnID)) {
			logger.error("栏目ID不存在");
			throw new Exception("栏目ID不存在");

		}
		if (StringUtils.isEmpty(bookName)) {
			logger.error("书名不存在");
			throw new Exception("书名不存在");
		}

		Columns coloum = this.bussinessService
				.getColumns(new Integer(columnID));
		Integer packID = coloum.getPricepackId();
		if (bookName.length() / 2 > 3) {
			title = bookName.subSequence(0, 3).toString();
		} else {
			title = bookName.subSequence(0, bookName.length() / 2).toString();
		}
		list.add(bookName);
		list.add(packID);
		if (resource.getDivision() != null) { // 按批价包查询
			logger.info("资源内容推荐：书部查询");

			hql += " and ra.name like ?";
			list.add("%" + title + "%");

		} else { // 按关键字查询
			logger.info("资源内容推荐：按关键字查询");
			if (keyWords.length > 3) { // 关键字大于三个
				logger.info("按照关键字查询:" + "[" + keyWords[0] + "]," + "["
						+ keyWords[1] + "]," + "[" + keyWords[0] + "],");
				hql += " and (ra.RKeyword like ? or ra.RKeyword like ? or ra.RKeyword like ?)";
				list.add("%" + keyWords[0] + "%");
				list.add("%" + keyWords[1] + "%");
				list.add("%" + keyWords[2] + "%");
			} else { // 关键字小于三个

				hql += " and (ra.RKeyword like ? ";
				list.add("%" + keyWords[0] + "%");

				for (int i = 1; i < keyWords.length; i++) {
					hql += " or ra.RKeyword like ? ";
					list.add("%" + keyWords[i] + "%");
				}
				hql += ")";

			}

		}
		if (getNotBussinessPartnerIds().length() > 0) {
			hql += " and ra.cpId not in " + getNotBussinessPartnerIds() + " ";
		}
		hql += " order by "
				+ (resource.getDivision() != null ? "ra.division asc"
						: "ra.downnum desc");
		Object[] values = list.toArray();

		return controller.findBy(hql, 1, new Integer(size).intValue(), values);
	}

	/***************************************************************************
	 * 根据url过来的参数得到排序id
	 * 
	 * @param order
	 *            url 传来的一级参数
	 * @param orderSub
	 *            url传来的二级参数
	 * @return 最终排序id
	 * 
	 * @author penglei 2009-10-29 12:36:22
	 * 
	 * 
	 */
	public Integer getUrlOrderType(Integer order, Integer orderSub) {
		Integer result = null;
		Map<Integer, Map<Integer, Integer>> map = Constants.getUrlOrderMap();
		Map<Integer, Integer> temp = map.get(order);
		if (temp != null) {
			result = temp.get(orderSub);
		}
		return result;
	}

	private List<ResourcePackReleation> filterResource(int packId, int pageNo,
			int pageSize, String resourceId) {
		String key = Utility.getMemcachedKey(ResourcePackReleation.class,
				String.valueOf(packId), String.valueOf(pageNo), String
						.valueOf(pageSize), resourceId, "filter");
		List<ResourcePackReleation> rels = null;
		try {
			rels = (List<ResourcePackReleation>) memcached
					.getAndSaveLocalMedium(key);
			if (rels != null)
				return rels;
		} catch (Exception e) {
			logger.error("从Memcached中获取批价包关联列表信息出错!", e);
		}
		String hql = "select rel from  ResourcePackReleation rel,ResourceAll resource where rel.pack = ?  and resource.status = 0 and rel.status=0  and resource.id=rel.resourceId ";
		if (getNotBussinessPartnerIds().length() > 0) {
			hql += " and resource.cpId not in " + getNotBussinessPartnerIds()
					+ " ";
		}
		hql += " and resource.id not in ('" + resourceId
				+ "')  order by resource.downnum desc";
		ResourcePack pack = new ResourcePack();
		pack.setId(packId);
		rels = controller.findBy(hql, pageNo, pageSize, pack);
		memcached.setAndSaveLocalMedium(key, rels,
				5 * MemCachedClientWrapper.MINUTE);
		return rels;
	}

	public List<Map> findCategoryWideResource(String resourceId,
			int pageGroupId, int count) {
		List<Map> all = new ArrayList<Map>();
		int totalCount = 0;
		List typeIds = getResourceTypeID(resourceId);
		if (typeIds != null && typeIds.size() > 0) {
			for (Iterator it = typeIds.iterator(); it.hasNext();) {
				int typeId = (Integer) it.next();
				if (typeId > 0) {
					Columns column = bussinessService.getColumnsByResourceType(
							pageGroupId, typeId);
					if (column != null && column.getPricepackId() != null) {
						ResourcePack pack = getResourcePack(column
								.getPricepackId());
						if (pack != null) {
							/** 取出该分类下点击排行前20本书 */
							List<ResourcePackReleation> rprs = filterResource(
									pack.getId(), 1, 20, resourceId);// getResourcePackReleationsByOrder(pack.getId(),
							// 1,
							// 20,2,20);
							totalCount += rprs.size();
							for (Iterator it2 = rprs.iterator(); it2.hasNext();) {
								ResourcePackReleation rpr = (ResourcePackReleation) it2
										.next();
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("columnId", column.getId());
								map.put("releation", rpr);
								all.add(map);
							}
						} else {
							continue;
						}
					}
				}
			}
		} else {
			return new ArrayList();
		}
		if (all.size() < 1)
			return new ArrayList();
		if (all.size() - 1 >= count) {
			/** 随机生成X个不重复的数 */
			Integer arr[] = ArrayUtil.getRandom(all.size() - 1, count > all
					.size() ? all.size() : count);
			List<Map> backList = new ArrayList<Map>();
			for (int i = 0; i < arr.length; i++) {
				backList.add(all.get(arr[i]));
			}
			return backList;
		} else {
			return all;
		}

	}

	public ResourcePackReleation getResourcePackReleation(int packId,
			String resourceId) {
		// 批价包关联信息更新时，需要清空缓存
		String key = Utility.getMemcachedKey(ResourcePackReleation.class,
				String.valueOf(packId), resourceId);
		Object rel = null;
		try {
			rel = memcached.getAndSaveLocalMedium(key);
		} catch (Exception e) {
			logger.error("从Memcached中获取批价包关联信息出错!", e);
		}

		if (rel == null) {
			Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
			ResourcePack pack = new ResourcePack();
			pack.setId(packId);
			HibernateExpression packE = new CompareExpression("pack", pack,
					CompareType.Equal);
			expressions.add(packE);
			HibernateExpression statusE = new CompareExpression("status", 0,
					CompareType.Equal);
			expressions.add(statusE);
			HibernateExpression resourceE = new CompareExpression("resourceId",
					resourceId, CompareType.Equal);
			expressions.add(resourceE);
			rel = controller.findBy(ResourcePackReleation.class, 1, 1,
					expressions).get(0);
			if (rel == null) {
				rel = new NullObject();
			}
			memcached.setAndSaveLocalMedium(key, rel,
					72 * MemCachedClientWrapper.HOUR);
		}
		if (rel instanceof NullObject) {
			return null;
		}
		ResourcePackReleation r = (ResourcePackReleation) rel;
		if (r.getStatus() == 0)
			return r;
		return null;
	}

	/**
	 * 资源关联书部信息
	 * 
	 * @author yuzs 2009-11-09
	 */
	public List<ResourcePackReleation> findDivisions(String columnID,
			ResourceAll resource) throws Exception {

		String key = Utility.getMemcachedKey(ResourcePackReleation.class,
				columnID, resource.getId(), String.valueOf(resource
						.getDivision()), resource.getDivisionContent());
		List<ResourcePackReleation> rels = null;
		try {
			rels = (List<ResourcePackReleation>) memcached
					.getAndSaveLocalMedium(key);
			if (rels != null)
				return rels;
		} catch (Exception e) {
			logger.error("从Memcached中获取相关书部信息失败!", e);
		}

		String hql = "select rpr from ResourcePackReleation rpr , ResourceAll ra where ra.id=rpr.resourceId and rpr.pack.id = ? and rpr.status = 0 and ra.status=0  and ra.id like '"
				+ resource.getId().substring(0, 1) + "%'";

		List list = new ArrayList();
		if (StringUtils.isEmpty(columnID)) {
			logger.error("栏目ID不存在");
			throw new Exception("栏目ID不存在");

		}
		Columns coloum = this.bussinessService
				.getColumns(new Integer(columnID));
		Integer packID = coloum.getPricepackId();

		list.add(packID);
		if (resource.getDivision() != null) { // 按批价包查询
			logger.info("资源内容推荐：书部查询");

			hql += " and ra.divisionContent like ?";
			list.add(resource.getDivisionContent());
		}
		// 排除当前资源对象
		hql += "and ra.id not like ?";
		list.add(resource.getId());

		if (getNotBussinessPartnerIds().length() > 0) {
			hql += " and ra.cpId not in " + getNotBussinessPartnerIds() + " ";
		}

		hql += " order by "
				+ (resource.getDivision() != null ? "ra.division asc"
						: "ra.downnum desc");
		Object[] values = list.toArray();

		rels = controller.findBy(hql, 1, Integer.MAX_VALUE, values);

		memcached.setAndSaveLocalMedium(key, rels, MemCachedClientWrapper.HOUR);

		return rels;

	}

	public int getDivisionsCount(String columnID, ResourceAll resource)
			throws Exception {

		if (resource.getDivision() == null
				|| resource.getDivisionContent() == null
				|| StringUtils.isEmpty(resource.getDivisionContent())) {
			return 0;
		}
		String key = Utility
				.getMemcachedKey(ResourcePackReleation.class, columnID,
						resource.getId(), String
								.valueOf(resource.getDivision()), resource
								.getDivisionContent(), "count");
		Integer count = null;
		try {
			count = (Integer) memcached.getAndSaveLocalMedium(key);
			if (count != null)
				return count;
		} catch (Exception e) {
			logger.error("从Memcached中获取相关书部总数量信息失败!", e);
		}

		String hql = "select rpr from ResourcePackReleation rpr , ResourceAll ra where ra.id=rpr.resourceId and rpr.pack.id = ? and rpr.status = 0 and ra.status=0  and ra.id like '"
				+ resource.getId().substring(0, 1) + "%'";

		List list = new ArrayList();
		if (StringUtils.isEmpty(columnID)) {
			logger.error("栏目ID不存在");
			throw new Exception("栏目ID不存在");

		}
		Columns coloum = this.bussinessService
				.getColumns(new Integer(columnID));
		Integer packID = coloum.getPricepackId();

		list.add(packID);
		// if (resource.getDivision()!=null &&
		// resource.getDivisionContent()!=null &&
		// StringUtils.isNotEmpty(resource.getDivisionContent())) { // 按批价包查询
		hql += " and ra.divisionContent=?";
		list.add(resource.getDivisionContent());
		// }
		// 排除当前资源对象
		hql += "and ra.id not in ('" + resource.getId() + "')";
		// list.add(resource.getId());

		if (getNotBussinessPartnerIds().length() > 0) {
			hql += " and ra.cpId not in " + getNotBussinessPartnerIds() + " ";
		}
		Object[] values = list.toArray();
		count = controller.getResultCount(hql, values).intValue();
		memcached
				.setAndSaveLocalMedium(key, count, MemCachedClientWrapper.HOUR);
		return count;
	}

	public List<ResourcePackReleation> findDivisions(String columnID,
			ResourceAll resource, int pageNo, int pageSize, int listCount,
			int order) throws Exception {
		if (resource.getDivision() == null
				|| resource.getDivisionContent() == null
				|| StringUtils.isEmpty(resource.getDivisionContent())) {
			return new ArrayList();
		}

		boolean isMagazine = resource.getId().startsWith(
				String.valueOf(ResourceType.TYPE_MAGAZINE));

		String key = Utility.getMemcachedKey(ResourcePackReleation.class,
				columnID, resource.getId(), String.valueOf(resource
						.getDivision()), resource.getDivisionContent(), String
						.valueOf(listCount), String.valueOf(order), String
						.valueOf(isMagazine));
		List<ResourcePackReleation> rels = null;
		try {
			rels = (List<ResourcePackReleation>) memcached
					.getAndSaveLocalMedium(key);
			if (rels != null) {
//				System.out.println("findDivisions rels size from mem:"+rels.size());
				if (isMagazine)
					return page(rels, pageNo, pageSize);
				else
					return getDivisionsBesidesMagazine(pageSize, rels, resource);
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取相关书部/期刊信息失败!", e);
		}

		String hql = "select rpr from ResourcePackReleation rpr , ResourceAll ra where ra.id=rpr.resourceId and rpr.pack.id = ? and rpr.status = 0 and ra.status=0  and ra.id like '"
				+ resource.getId().substring(0, 1) + "%'";

		List list = new ArrayList();
		if (StringUtils.isEmpty(columnID)) {
			logger.error("栏目ID不存在");
			throw new Exception("栏目ID不存在");

		}
		Columns coloum = this.bussinessService
				.getColumns(new Integer(columnID));
		Integer packID = coloum.getPricepackId();

		list.add(packID);
		hql += " and ra.divisionContent =?";
		list.add(resource.getDivisionContent());

		/** 杂志资源排除当前对象 */
		if (isMagazine) {
			hql += "and ra.id not in ('" + resource.getId() + "')";
		}

		if (getNotBussinessPartnerIds().length() > 0) {
			hql += " and ra.cpId not in " + getNotBussinessPartnerIds() + " ";
		}
		if (isMagazine) {
			if (order == 0) {// 按书部/期刊数倒序
				hql += " order by  ra.division desc";
			} else if (order == 1) {// 按书部/期刊数正序
				hql += " order by ra.division asc";
			}
		} else {// 非杂志资源升序
			hql += " order by ra.division asc";
		}

		Object[] values = list.toArray();

		if (isMagazine) {
			rels = controller.findBy(hql, 1, listCount, values);
		} else {
			/** 非杂志资源取所有书部信息 */
			rels = controller.findBy(hql, 1, Integer.MAX_VALUE, values);
		}
//		System.out.println("findDivisions rels size from db:"+rels.size());
		memcached.setAndSaveLocalMedium(key, rels, MemCachedClientWrapper.HOUR);

		if (isMagazine) {
			return page(rels, pageNo, pageSize);
		} else {
			/** 不带分页 */
			// return
			// page(getDivisionsBesidesMagazine(listCount,rels,resource),pageNo,pageSize);
			return getDivisionsBesidesMagazine(pageSize, rels, resource);
			// return getDivisionsBesidesMagazine(listCount,rels,resource);
		}

	}

	/***************************************************************************
	 * 
	 * @param listCount
	 *            列表最大范围
	 * @param rels
	 * @param resource
	 * @return add by liuxh 09-11-20
	 */
//	private List<ResourcePackReleation> getDivisionsBesidesMagazine(
//			int listCount, List<ResourcePackReleation> rels,
//			ResourceAll resource) {
//		if (listCount >= rels.size()) {
//			for (ResourcePackReleation rpr : rels) {
//				if (rpr.getResourceId().equals(resource.getId())) {
//					rels.remove(rpr);
//					return rels;
//				}
//			}
//		}
//		/** 向前取的个数 */
//		int before_count = 0;
//		/** 向后取的个数 */
//		int end_count = 0;
//		if (listCount % 2 == 0) {// 偶数
//			before_count = end_count = listCount / 2;
//		} else {
//			before_count = (listCount + 1) / 2 - 1;
//			end_count = (listCount + 1) / 2;
//		}
//		System.out.println("前取" + before_count + "个 : 后取" + end_count + "个");
//		List<ResourcePackReleation> result = new ArrayList<ResourcePackReleation>();
//		/** 升序 */
//		for (int j = 0; j < rels.size(); j++) {
//			ResourcePackReleation releation = rels.get(j);
//			if (resource.getId().equals(releation.getResourceId())) {
//				int B_INDEX = j == 0 ? rels.size() : j;// 开始索引 如果当前索引为0
//														// B_INDEX重置为最后一个
//				int E_INDEX = j == rels.size() - 1 ? 0 : j;// 结束索引
//
//				for (int b = before_count; b > 0; b--) {
//					if (j - b < 0) {
//						if (j != 0)
//							result.add(rels.get(rels.size() - (b - j)));
//						else
//							// 说明是第一个
//							result.add(rels.get(B_INDEX - b));
//					} else {
//						result.add(rels.get(j - b));
//					}
//				}
//				for (int e = 1; e <= end_count; e++) {
//					if (j + e > rels.size() - 1) {
//						if (j != rels.size() - 1) {
//							result.add(rels.get((e - 1)
//									- ((rels.size() - 1) - E_INDEX)));
//						} else
//							// 说明是最后一个
//							result.add(rels.get(E_INDEX + (e - 1)));
//					} else {
//						result.add(rels.get(j + e));
//					}
//				}
//				break;
//			}
//		}
//
//		// System.out.println("结果====================");
//		// for(Iterator it=result.iterator();it.hasNext();){
//		// ResourceAll
//		// r=this.getResource(((ResourcePackReleation)it.next()).getResourceId());
//		// System.out.println(r.getId()+"."+r.getName());
//		// }
//		return result;
//	}

	
	private List<ResourcePackReleation> getDivisionsBesidesMagazine(int listCount,List<ResourcePackReleation> rels,ResourceAll resource){
		if(listCount>=rels.size()){
			for (Iterator it = rels.iterator();it.hasNext();){    
				ResourcePackReleation rpr = (ResourcePackReleation)it.next();   
		        	if (rpr.getResourceId().equals(resource.getId())){ 
		        	 	it.remove();
		        	 	rels.remove(rpr); 
		         	}   
		     	}   
			return rels;
		}
		/**向前取的个数*/
		int before_count=0;
		/**向后取的个数*/
		int end_count=0;
		if(listCount%2==0){//偶数
			before_count=end_count=listCount/2;
		}else{
			before_count=(listCount+1)/2-1;
			end_count=(listCount+1)/2;
		}
		System.out.println("前取"+before_count+"个 : 后取"+end_count+"个");
		List<ResourcePackReleation> result=new ArrayList<ResourcePackReleation>();
		/**升序*/
		for(int j=0;j<rels.size();j++){
			ResourcePackReleation releation=rels.get(j);
			if(resource.getId().equals(releation.getResourceId())){
				int B_INDEX=j==0?rels.size():j;//开始索引    如果当前索引为0  B_INDEX重置为最后一个
				int E_INDEX=j==rels.size()-1?0:j;//结束索引
			
				for(int b=before_count;b>0;b--){
					if(j-b<0){
						if(j!=0)
							result.add(rels.get(rels.size()-(b-j)));
						else//说明是第一个
							result.add(rels.get(B_INDEX-b));
					}else{
						result.add(rels.get(j-b));
					}
				}
				for(int e=1;e<=end_count;e++){
					if(j+e>rels.size()-1){
						if(j!=rels.size()-1){
							result.add(rels.get((e-1)-((rels.size()-1)-E_INDEX)));
						}else//说明是最后一个
							result.add(rels.get(E_INDEX+(e-1)));
					}else{
						result.add(rels.get(j+e));
					}
				}
				break;
			}
		}
		
//		System.out.println("结果====================");
//		for(Iterator it=result.iterator();it.hasNext();){
//			ResourceAll r=this.getResource(((ResourcePackReleation)it.next()).getResourceId());
//			System.out.println(r.getId()+"."+r.getName());
//		}
		return result;
	}
	
	public String browseResourceTome(String tomeId, boolean isNotNext) {
		String resourceId = getResourceIdfromTomeId(tomeId);
		List<EbookTome> tomes = getEbookTomeByResourceId(resourceId, 1,
				Integer.MAX_VALUE);
		for (int i = 0; i < tomes.size(); i++) {
			EbookTome tome = tomes.get(i);
			if (tome.getId().equals(tomeId)) {
				if (isNotNext) {// 上一章
					if ((i - 1) < 0)
						return "";// 无上一卷
					return tomes.get(i - 1).getId();
				} else {// 下一章
					if ((i + 1) >= tomes.size())
						return "";// 无下一卷
					return tomes.get(i + 1).getId();
				}
			}
		}
		return "";
	}

	public List<VideoSuite> getVideoSuiteList(String resourceId) {
		String key = Utility
				.getMemcachedKey(ResourceAll.class, resourceId, "chapters");
		List<VideoSuite> chapters = null;
		try {
			chapters = (List<VideoSuite>) memcached
					.getAndSaveLocalLong(key);
			if (chapters != null) {
				return chapters;
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取资源章节信息出错!", e);
		}
		chapters = controller.findBy(VideoSuite.class, "resourceId", resourceId,
				"chapterIndex", true);
		memcached.setAndSaveLong(key, chapters,
				72 * MemCachedClientWrapper.HOUR);
		return chapters;
	}

	public int getVideoSuiteListCount(String resourceId) {
		return getVideoSuiteList(resourceId).size();
	}
}
