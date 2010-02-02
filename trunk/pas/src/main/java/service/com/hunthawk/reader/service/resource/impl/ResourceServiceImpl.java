package com.hunthawk.reader.service.resource.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.request.IUploadFile;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.hibernate.NotInExpression;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.Infomation;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.MagazineChapter;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.NewsPapersChapter;
import com.hunthawk.reader.domain.resource.ReCheck;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceReferen;
import com.hunthawk.reader.domain.resource.ResourceResType;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.resource.Video;
import com.hunthawk.reader.domain.resource.VideoSuite;
import com.hunthawk.reader.domain.system.Log;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.enhance.util.RandomGUID;
import com.hunthawk.reader.enhance.util.ResourceUtil;
import com.hunthawk.reader.service.partner.PartnerService;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * 
 * @author BruceSun
 * 
 */
@SuppressWarnings("unchecked")
public class ResourceServiceImpl implements ResourceService {

	private HibernateGenericController controller;

	private SystemService systemService;

	private ResourcePackService resourcePackService;

	private PartnerService partnerService;

	private MemCachedClientWrapper memcached;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.partner.PartnerService#addChannel(com.hunthawk
	 *      .reader.domain.partner.Channel)
	 */

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	public void setResourcePackService(ResourcePackService resourcePackService) {
		this.resourcePackService = resourcePackService;
	}

	public void setPartnerService(PartnerService partnerService) {
		this.partnerService = partnerService;
	}

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}

	public List<ResourceType> findResourceTypeBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(ResourceType.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

	public List<ResourceType> findResourceTypeBymemcached(String keyWord,
			int pageNo, int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		List<ResourceType> typeList = null;
		// String key = Utility.getMemcachedKey(ResourceType.class, keyWord,
		// String.valueOf(pageNo), String.valueOf(pageSize), orderBy,
		// String.valueOf(isAsc));
		// try {
		// typeList = (List<ResourceType>) memcached
		// .getAndSaveLocalMedium(key);
		// if (typeList != null && typeList.size() > 0)
		// return typeList;
		// } catch (Exception e) {
		// System.out.println("从Memcached中获取分类信息出错!");
		// }
		// System.out.println("----重新取得---分类列表--");
		typeList = controller.findBy(ResourceType.class, pageNo, pageSize,
				orderBy, isAsc, expressions);
		// memcached.setAndSaveLocalMedium(key, typeList,
		// 2 * MemCachedClientWrapper.HOUR); // 放缓存2小时
		return typeList;
	}

	public Long getResourceTypeResultCount(
			Collection<HibernateExpression> expressions) {
		return controller.getResultCount(ResourceType.class, expressions);
	}

	/*
	 * yuzs 2009-12-02 从缓存中取得作者列表
	 */
	public List<ResourceAuthor> findResourceAuthorBymemcached(String keyWord,
			int pageNo, int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		String key = Utility.getMemcachedKey(ResourceAuthor.class, keyWord,
				String.valueOf(pageNo), String.valueOf(pageSize), orderBy,
				String.valueOf(isAsc));
		List<ResourceAuthor> authorList = null;
		try {
			authorList = (List<ResourceAuthor>) memcached
					.getAndSaveLocalMedium(key);
			if (authorList != null && authorList.size() > 0)
				return authorList;
		} catch (Exception e) {
			System.out.println("从Memcached中获取作者信息出错!");
		}
		System.out.println("----重新取得---作者列表--");
		authorList = controller.findBy(ResourceAuthor.class, pageNo, pageSize,
				orderBy, isAsc, expressions);
		memcached.setAndSaveLocalMedium(key, authorList,
				2 * MemCachedClientWrapper.HOUR); // 放内存2小时
		return authorList;
	}

	public List<ResourceAuthor> findResourceAuthorBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		List<ResourceAuthor> authorList = controller.findBy(
				ResourceAuthor.class, pageNo, pageSize, orderBy, isAsc,
				expressions);
		return authorList;
	}

	public Long getResourceAuthorResultCount(
			Collection<HibernateExpression> expressions) {
		return controller.getResultCount(ResourceAuthor.class, expressions);
	}

	public List<ResourceAll> findResourceBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		// 过滤下线状态的内容
		HibernateExpression statusE = new CompareExpression("status", 2,
				CompareType.NotEqual);
		expressions.add(statusE);
		if (getOfflinePartnerCollection().size() > 0) {
			expressions.add(new NotInExpression("cpId",
					getOfflinePartnerCollection()));
		}
		return controller.findBy(ResourceAll.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

	public List<ResourceResType> findResourceResTypeBy(int pageNo,
			int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(ResourceResType.class, pageNo, pageSize,
				orderBy, isAsc, expressions);
	}

	public List<ResourceReferen> findResourceReferenBy(int pageNo,
			int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		// 过滤掉 隐藏状态的版权信息
		HibernateExpression status = new CompareExpression("status", 2,
				CompareType.NotEqual);
		expressions.add(status);
		return controller.findBy(ResourceReferen.class, pageNo, pageSize,
				orderBy, isAsc, expressions);
	}

	public Long getResourceReferenResultCount(
			Collection<HibernateExpression> expressions) {
		// 过滤掉 隐藏状态的版权信息
		HibernateExpression status = new CompareExpression("status", 2,
				CompareType.NotEqual);
		expressions.add(status);
		return controller.getResultCount(ResourceReferen.class, expressions);
	}

	public Long getResourceResultCount(
			Collection<HibernateExpression> expressions) {
		// 过滤下线状态的内容
		HibernateExpression statusE = new CompareExpression("status", 2,
				CompareType.NotEqual);
		expressions.add(statusE);
		if (getOfflinePartnerCollection().size() > 0) {
			expressions.add(new NotInExpression("cpId",
					getOfflinePartnerCollection()));
		}
		return controller.getResultCount(ResourceAll.class, expressions);
	}

	public Long getResourceResultCountByHQL(Integer cpid, Integer resourceType,
			String name, String authorId, Integer childTypeId, Integer status,
			Integer creatorId, String keyWord, Integer isFinish,
			String publisher, String initialLetter, Integer expNum,
			Integer isFirstpublish, Integer isOut, Integer healthNum,
			Integer isSearchTop, Integer isDivision) {
		/*
		 * Integer cpid = null; if (user.isRoleProvider()) { cpid =
		 * user.getProvider().getId(); }
		 */
		List param = new ArrayList();
		String hql = "select resource from ResourceResType restype,ResourceAll resource   where   resource.status<>2 ";
		if (!childTypeId.equals(0)) {
			hql += " and restype.resTypeId=? ";
			param.add(childTypeId);
		}
		if (status != null && status != 9) {
			hql += " and resource.status=? ";
			param.add(status);
		}

		if (name != null) {
			hql += " and resource.name like ?";
			param.add(name);
		}
		if (authorId != null && !"0".equals(authorId)) {
			hql += " and resource.authorId like ? ";
			param.add("%|" + authorId + "|%");
		}
		if (cpid != null) {
			hql += " and resource.cpId =? ";
			param.add(cpid);
		}
		if (creatorId != null) {
			hql += " and resource.creatorId=? ";
			param.add(creatorId);
		}
		if (keyWord != null) {
			hql += " and resource.RKeyword like? ";
			param.add("%" + keyWord + "%");
		}
		if (isFinish != null && isFinish != 0) {
			hql += " and resource.isFinished =? ";
			param.add(isFinish);
		}
		if (getOfflinePartnerIds().length() > 0) {
			// hql += " and resource.cpId not in "+getOfflinePartnerIds()+" ";
			hql += " and not exists (select 'x' from Provider p where resource.cpId=p.id and p.status = 5) ";
			// param.add(getOfflinePartnerIds());
		}
		// ------------新添加查询条件------------------------------
		if (publisher != null) {
			hql += " and resource.publisher like ? ";
			param.add("%" + publisher + "%");
		}
		if (initialLetter != null && !"0".equals(initialLetter)) {
			hql += " and resource.initialLetter =? ";
			param.add(initialLetter);
		}
		if (expNum != null && expNum != 0) {
			hql += " and resource.expNum =? ";
			param.add(expNum);
		}
		if (isFirstpublish != null && isFirstpublish != 0) {
			hql += " and resource.isFirstpublish =? ";
			param.add(isFirstpublish);
		}
		if (isOut != null && isOut != 0) {
			hql += " and resource.isOut =? ";
			param.add(isOut);
		}
		/*
		 * 2009-11-06 新添加查询条件
		 */
		if (healthNum != null && healthNum != 0) {
			hql += " and resource.healthNum =? ";
			param.add(healthNum);
		}
		if (isSearchTop != null && isSearchTop > -1) {
			hql += " and resource.searchTop =? ";
			param.add(isSearchTop);
		}
		if (isDivision != null && isDivision > -1) {
			if (isDivision == 0) {
				hql += "and (division is null)";
			} else {
				hql += "and division != null";
			}
		}
		/*
		 * end
		 */
		// ------------------------------------------------------
		hql += " and resource.id like ?   and  resource.id = restype.rid   order by resource.id desc";
		param.add(resourceType + "%");
		final int size = param.size();
		Object[] arr = (Object[]) param.toArray(new Object[size]);
		return controller.getResultCount(hql, arr);
	}

	private List<Integer> getOfflinePartnerCollection() {
		HibernateExpression ex = new CompareExpression("status", 5,
				CompareType.Equal);
		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		expressions.add(ex);
		List<Provider> providers = partnerService.findProvider(1,
				Integer.MAX_VALUE, "id", false, expressions);
		List<Integer> ids = new ArrayList<Integer>();
		for (Provider provider : providers) {
			ids.add(provider.getId());
		}
		return ids;
	}

	private String getOfflinePartnerIds() {
		HibernateExpression ex = new CompareExpression("status", 5,
				CompareType.Equal);
		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		expressions.add(ex);
		List<Provider> providers = partnerService.findProvider(1,
				Integer.MAX_VALUE, "id", false, expressions);
		String ids = "";
		for (Provider provider : providers) {
			ids += provider.getId() + ",";
		}
		if (ids.length() > 0) {
			ids = ids.substring(0, ids.length() - 1);
			ids = "( " + ids + " )";
		}
		return ids;
	}

	public List<ResourceAll> findResourceByHQL(Integer cpid,
			Integer resourceType, String name, String authorId,
			Integer childTypeId, Integer status, Integer creatorId,
			String keyWord, Integer isFinish, String publisher,
			String initialLetter, Integer expNum, Integer isFirstpublish,
			Integer isOut, Integer healthNum, Integer isSearchTop,
			Integer isDivision, int pageNum, int pageSize) {
		/*
		 * Integer cpid = null; if (user.isRoleProvider()) { cpid =
		 * user.getProvider().getId(); }
		 */
		List param = new ArrayList();
		String hql = "select resource from  ResourceResType restype ,ResourceAll resource where   resource.status<>2";
		if (!childTypeId.equals(0)) {
			hql += " and  restype.resTypeId =? ";
			param.add(childTypeId);
		}
		if (name != null) {
			hql += " and resource.name like ? ";
			param.add(name);
		}
		if (status != null && status != 9) {
			hql += " and resource.status=? ";
			param.add(status);
		}
		if (authorId != null && !"0".equals(authorId)) {
			hql += " and resource.authorId like ? ";
			param.add("%|" + authorId + "|%");
		}
		if (cpid != null) {
			hql += " and  resource.cpId =? ";
			param.add(cpid);
		}
		if (creatorId != null) {
			hql += " and resource.creatorId=? ";
			param.add(creatorId);
		}
		if (keyWord != null) {
			hql += " and resource.RKeyword like? ";
			param.add("%" + keyWord + "%");
		}
		if (isFinish != null && isFinish != 0) {
			hql += " and resource.isFinished =? ";
			param.add(isFinish);
		}
		if (getOfflinePartnerIds().length() > 0) {
			// hql += " and resource.cpId not in "+getOfflinePartnerIds()+" ";
			// //待优化
			hql += " and not exists (select 'x' from Provider p where resource.cpId=p.id and p.status = 5) ";
			// param.add(getOfflinePartnerIds());
		}
		// ------------新添加查询条件------------------------------
		if (publisher != null) {
			hql += " and resource.publisher like ? ";
			param.add("%" + publisher + "%");
		}
		if (initialLetter != null && !"0".equals(initialLetter)) {
			hql += " and resource.initialLetter =? ";
			param.add(initialLetter);
		}
		if (expNum != null && expNum != 0) {
			hql += " and resource.expNum =? ";
			param.add(expNum);
		}
		if (isFirstpublish != null && isFirstpublish != 0) {
			hql += " and resource.isFirstpublish =? ";
			param.add(isFirstpublish);
		}
		if (isOut != null && isOut != 0) {
			hql += " and resource.isOut =? ";
			param.add(isOut);
		}
		/*
		 * 2009-11-06 新添加查询条件
		 */
		if (healthNum != null && healthNum != 0) {
			hql += " and resource.healthNum =? ";
			param.add(healthNum);
		}
		if (isSearchTop != null && isSearchTop > -1) {
			hql += " and resource.searchTop =? ";
			param.add(isSearchTop);
		}
		if (isDivision != null && isDivision > -1) { // 待优化（null值 判断 耗性能）
			if (isDivision == 0) {
				hql += "and (division is null)";
			} else {
				hql += "and division != null";
			}
		}
		/*
		 * end
		 */
		// ------------------------------------------------------
		hql += " and resource.id like ?   and  resource.id = restype.rid   order by resource.id desc";
		param.add(resourceType + "%");
		final int size = param.size();
		Object[] arr = (Object[]) param.toArray(new Object[size]);
		Long currentTime = System.currentTimeMillis();
		System.out.println("---sql--长度查询-" + hql);
		List<ResourceAll> list = controller.findBy(hql, pageNum, pageSize, arr);
		Long newTime = System.currentTimeMillis();
		System.out.println("执行时间--资源-  资源查询时间--" + (newTime - currentTime));
		return list;
	}

	/***************************************************************************
	 * 查询不在某一个批价包下的资源的个数
	 */
	public Long getResourceResultNotInPack(Integer cpid, Integer resourceType,
			String name, String authorId, Integer childTypeId, boolean isTof,
			Integer creatorId, ResourcePack pack, Set<String> set) {
		List param = new ArrayList();
		String hql = "select   count(distinct resource.id) from  ResourceResType restype, ResourceAll resource where   resource.status=0 ";
		/*
		 * 分类反选 yuzs 2009-11-10
		 */

		if (isTof) { // 反选
			if (childTypeId != null && childTypeId > 0) {
				// hql +=
				// " and resource.id not in(select restype.rid from restype
				// where restype.resTypeId = "+childTypeId+")";//耗性能
				hql += " and not exists(select 'x' from restype where resource.id = restype.rid and restype.resTypeId = "
						+ childTypeId + ")";
			}
		} else {
			if (childTypeId != null && childTypeId > 0) {
				hql += " and restype.resTypeId=" + childTypeId;
				hql += " and  resource.id = restype.rid";
			}
		}

		if (name != null) {
			hql += " and resource.name like ?";
			param.add(name);
		}
		if (authorId != null && !authorId.equals("0")) {
			hql += " and resource.authorId like ? ";
			param.add("%|" + authorId + "|%");
		}
		if (cpid != null) {
			hql += " and resource.cpId =? ";
			param.add(cpid);
		}
		if (creatorId != null) {
			hql += " and resource.creatorId=? ";
			param.add(creatorId);
		}

		if (getOfflinePartnerIds().length() > 0) { // 很耗性能
			// hql += " and resource.cpId not in "+getOfflinePartnerIds()+" ";
			hql += " and not exists (select 'x' from Provider p where resource.cpId=p.id and p.status = 5) ";
			// param.add(getOfflinePartnerIds());
		}
		if (pack != null) { // 耗性能
			// hql +=
			// " and releation.pack = ? and resource.id <>
			// releation.resourceId";
			// hql +=
			// " and resource.id not in ( select releation.resourceId from
			// ResourcePackReleation releation where releation.pack = ? )";
			hql += " and not exists(select 'x' from ResourcePackReleation releation where resource.id = releation.resourceId  and releation.pack = ? )";
			param.add(pack);

		}
		if (set != null && set.size() > 0) {// 耗性能
			hql += " and resource.id in (";
			for (String str : set) {
				hql += "'" + str + "',";
			}
			hql = hql.substring(0, hql.length() - 1);// 截取掉最后那个",";
			hql += ")";
		}
		hql += " and resource.id like ?   ";
		param.add(resourceType + "%");
		// System.out.println("-----showsql----"+hql);
		final int size = param.size();
		Object[] arr = (Object[]) param.toArray(new Object[size]);
		Long currentTime = System.currentTimeMillis();
		System.out.println("---sql--长度-" + hql);
		List<Long> counts = controller.findBy(hql, arr);
		Long newTime = System.currentTimeMillis();
		System.out.println("执行时间--资源添加---长度---" + (newTime - currentTime));
		return counts.get(0);
	}

	public List<ResourceAll> findResourceNotInPack(Integer cpid,
			Integer resourceType, String name, String authorId,
			Integer childTypeId, boolean isTof, Integer creatorId,
			ResourcePack pack, Set<String> set, int pageNum, int pageSize) {
		List param = new ArrayList();
		String hql = "select distinct resource.id from ResourceResType restype,ResourceAll resource  where   resource.status=0";
		/*
		 * 分类反选 yuzs 2009-11-10
		 */
		if (isTof) {// 反选
			if (childTypeId != null && childTypeId > 0) {
				// hql +=
				// " and resource.id not in(select restype.rid from restype
				// where restype.resTypeId = "+childTypeId+")";
				hql += " and not exists(select 'x' from restype where resource.id = restype.rid and restype.resTypeId = "
						+ childTypeId + ")";
			}
		} else {
			// hql += " and restype.resTypeId<>"+childTypeId;
			if (childTypeId != null && childTypeId > 0) {
				hql += " and restype.resTypeId=" + childTypeId;
				hql += " and  resource.id = restype.rid";
			}
		}
		// -----结束------
		if (authorId != null && !authorId.equals("0")) {
			hql += " and resource.authorId like ? ";
			param.add("%|" + authorId + "|%");
		}
		if (cpid != null) {
			hql += " and  resource.cpId =? ";
			param.add(cpid);
		}
		if (creatorId != null) {
			hql += " and resource.creatorId=? ";
			param.add(creatorId);
		}
		if (getOfflinePartnerIds().length() > 0) { // 很耗性能
			// hql += " and resource.cpId not in "+getOfflinePartnerIds()+" ";
			System.out.println("-进入了cpsp查询---");
			hql += " and not exists (select 'x' from Provider p where resource.cpId=p.id and p.status = 5 ) ";
			// param.add(getOfflinePartnerIds());
		}
		if (pack != null) {
			// hql +=
			// " and releation.pack = ? and resource.id <>
			// releation.resourceId";
			// hql +=
			// " and resource.id not in ( select releation.resourceId from
			// ResourcePackReleation releation where releation.pack = ? )";
			hql += " and not exists(select 'x' from ResourcePackReleation releation where resource.id = releation.resourceId  and releation.pack = ? )";
			param.add(pack);

		}
		if (set != null && set.size() > 0) {
			hql += " and resource.id in (";
			for (String str : set) {
				hql += "'" + str + "',";
			}
			hql = hql.substring(0, hql.length() - 1);// 截取掉最后那个",";
			hql += ")";
		}
		hql += " and resource.id like ?  order by resource.id desc";

		param.add(resourceType + "%");
		final int size = param.size();
		Object[] arr = (Object[]) param.toArray(new Object[size]);
		Long currentTime = System.currentTimeMillis();
		System.out.println("---sql---" + hql);
		List<String> ids = controller.findBy(hql, pageNum, pageSize, arr);
		Long newTime = System.currentTimeMillis();
		System.out.println("执行时间--资源添加----" + (newTime - currentTime));
		List<ResourceAll> resources = new ArrayList<ResourceAll>();
		for (String id : ids) {
			ResourceAll resource = getResource(id);
			resources.add(resource);
		}
		return resources;
	}

	// 处理作者信息
	public void addResourceAuthor(ResourceAuthor resourceauthor)
			throws Exception {
		if (resourceauthor.getInitialLetter() != null) {
			resourceauthor.setInitialLetter(resourceauthor.getInitialLetter()
					.toUpperCase());
		}
		if (controller
				.isUnique(ResourceAuthor.class, resourceauthor, "penName")) {
			controller.save(resourceauthor);
		} else {
			throw new Exception("作者笔名已经存在!");
		}

	}

	public void updateResourceAuthor(ResourceAuthor resourceauthor) {
		if (resourceauthor.getInitialLetter() != null) {
			resourceauthor.setInitialLetter(resourceauthor.getInitialLetter()
					.toUpperCase());
		}
		controller.update(resourceauthor);
	}

	public void deleteResourceAuthor(ResourceAuthor resourceauthor) {
		controller.delete(resourceauthor);

	}

	public ResourceAuthor getResourceAuthorByName(String name) throws Exception {
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("penName", name,
				CompareType.Equal);
		expressions.add(ex);
		List<ResourceAuthor> authors = controller.findBy(ResourceAuthor.class,
				1, 1, "id", false, expressions);
		if (authors.size() > 0) {

			return authors.get(0);
		} else {
			return null;
		}
	}

	public ResourceAuthor getResourceAuthorById(Integer id) {
		return controller.get(ResourceAuthor.class, id);
	}

	public List<ResourceAuthor> getAllResourceAuthor() {
		return controller.getAll(ResourceAuthor.class);
	}

	// 处理版权信息
	public void addResourceReferen(ResourceReferen resourcereferen)
			throws Exception {
		// if(controller.isUnique(ResourceReferen.class, resourcereferen,
		// "name")){
		System.out.println("addResourceReferen");
		controller.save(resourcereferen);
		// }else{
		// throw new Exception("版权方已经存在!");
		// }

	}

	public void updateResourceReferen(ResourceReferen resourcereferen) {
		System.out.println("updateResourceReferen");
		controller.update(resourcereferen);
	}

	public void deleteResourceReferen(ResourceReferen resourcereferen) {

		controller.delete(resourcereferen);

	}

	public ResourceReferen getResourceReferen(int id) {
		return controller.get(ResourceReferen.class, id);
	}

	public List<ResourceReferen> getAllResourceReferen() {
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		// 过滤掉 隐藏状态的版权信息
		HibernateExpression status = new CompareExpression("status", 2,
				CompareType.NotEqual);
		expressions.add(status);
		return controller.findBy(ResourceReferen.class, 1, Integer.MAX_VALUE,
				expressions);
	}

	public List<ResourceReferen> getResourceReferenByCPID(Integer CPID) {
		return controller.findBy(ResourceReferen.class, "cpId", CPID, "id",
				true);
	}

	// 处理资源分类
	public void addResourceType(ResourceType resourcetype) throws Exception {
		if (controller.isUnique(ResourceType.class, resourcetype,
				"name,showType")) {
			controller.save(resourcetype);
		} else {
			throw new Exception("此资源分类已经存在!");
		}

	}

	/**
	 * 根据分类ID查询出此分类下的资源个数
	 */
	public Long getResourceTypeResResultCount(
			Collection<HibernateExpression> expressions) {
		return controller.getResultCount(ResourceResType.class, expressions);
	}

	public void updateResourceType(ResourceType resourcetype) {
		controller.update(resourcetype);
	}

	/**
	 * 删除分类及分类下的资源关联
	 */
	public void deleteResourceType(ResourceType resourcetype) {

		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("resTypeId",
				resourcetype.getId(), CompareType.Equal);
		expressions.add(ex);
		List<ResourceResType> rrts = findResourceResTypeBy(1,
				Integer.MAX_VALUE, "rid", false, expressions);
		for (ResourceResType res : rrts) {
			controller.delete(res);
		}
		controller.delete(resourcetype);

	}

	public ResourceType getResourceType(int id) {
		return controller.get(ResourceType.class, id);
	}

	// 处理资源与资源分类从属关系
	public void addResourceResType(ResourceResType resourcerestype)
			throws Exception {
		// if(controller.isUnique(ResourceResType.class, ResourceResType,
		// "name")){
		controller.save(resourcerestype);
		// }else{
		// throw new Exception("次资源分类已经存在!");
		// }

	}

	/**
	 * 带判断条件的插入资源-分类关系
	 * 
	 * @param resourcerestype
	 */
	public void addResourceResTypeByUnique(ResourceResType resourcerestype) {

		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("rid", resourcerestype
				.getRid(), CompareType.Equal);
		expressions.add(ex);
		HibernateExpression et = new CompareExpression("resTypeId",
				resourcerestype.getResTypeId(), CompareType.Equal);
		expressions.add(et);

		List<ResourceResType> list = controller.findBy(ResourceResType.class,
				1, 1000, expressions);
		if (list == null || list.size() <= 0)
			controller.save(resourcerestype);
	}

	public void updateResourceResType(ResourceResType resourcerestype) {
		controller.update(resourcerestype);
	}

	/**
	 * 根据Id删除
	 * 
	 * @param <T>
	 * @param clasz
	 * @param id
	 */
	public void deleteResourceResType(String resourceId) {
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("rid", resourceId,
				CompareType.Equal);
		expressions.add(ex);
		List<ResourceResType> list = controller.findBy(ResourceResType.class,
				1, 1000, expressions);
		for (ResourceResType pr : list) {
			controller.delete(pr);
		}
	}

	/**
	 * 根据资源ID 和 分类ID 删除
	 */
	public void deleteResourceResTypeByRT(String resourceId,
			Integer resourceTypeId) {
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("rid", resourceId,
				CompareType.Equal);
		expressions.add(ex);
		HibernateExpression et = new CompareExpression("resTypeId",
				resourceTypeId, CompareType.Equal);
		expressions.add(et);

		List<ResourceResType> list = controller.findBy(ResourceResType.class,
				1, 1000, expressions);
		if (list != null && list.size() > 0) {
			ResourceResType delTypeRes = list.get(0);
			if (delTypeRes != null)
				controller.delete(delTypeRes);
		}
	}

	public ResourceResType getResourceResType(int id) {
		return controller.get(ResourceResType.class, id);
	}

	// 处理图书信息
	// public void addEbook(Ebook ebook) throws Exception {
	//
	// ebook
	// .setId(getNewResourceId(ResourceAll.RESOURCE_TYPE_BOOK
	// .toString()));
	// controller.save(ebook);
	//
	// }
	//
	// public void updateEbook(Ebook ebook) {
	// controller.update(ebook);
	// }
	//
	// public void deleteEbook(Ebook ebook) {
	// controller.delete(ebook);
	//
	// }

	public String getNewResourceId(String id) throws Exception {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("id", id + "%",
				CompareType.Like);
		hibernateExpressions.add(ex);
		// 进行模糊 查询,传递的是资源类别ID,资源类别1图书，2报纸，3杂志，4漫画，5铃声，6视频
		List<ResourceAll> resourcealls = controller.findBy(ResourceAll.class,
				1, 1, "id", false, hibernateExpressions);
		if (resourcealls.size() > 0) {

			ResourceAll resourceall = resourcealls.get(0);
			// int start=Integer.parseInt(resourceall.getId().substring(0,5));
			// 截取后7位
			int last = Integer.parseInt(resourceall.getId().substring(
					(resourceall.getId().length() - 7)));
			return id + StringUtils.leftPad(String.valueOf(last + 1), 7, "0");
		} else {

			return id + "0000001";
		}
	}

	// public Ebook getEbook(String id) {
	// return controller.get(Ebook.class, id);
	// }

	// 处理图书卷与章节从属关系
	public void addEbookTome(EbookTome ebooktome) throws Exception {

		ebooktome.setId(getNewEbookTomeId(ebooktome.getResourceId()));

		controller.save(ebooktome);

	}

	public void updateEbookTome(EbookTome ebooktome) {
		controller.update(ebooktome);
	}

	public void deleteEbookTome(EbookTome ebooktome) {

		controller.delete(ebooktome);

	}

	/**
	 * 此id为章节ID的前8位,规则为YXXXXXXXNN,共10位， Y:资源类别1图书，2报纸，3杂志，4漫画，5铃声，6视频 XXXXXXX:
	 * 资源序列号，由0补足位数 NN:章节序列号，由0补足位数 如1000000101
	 */
	private String getNewEbookTomeId(String id) throws Exception {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("id", id + "%",
				CompareType.Like);
		hibernateExpressions.add(ex);
		// 进行模糊 查询,传递的是资源id,8位
		List<EbookTome> resourcealls = controller.findBy(EbookTome.class, 1, 1,
				"id", false, hibernateExpressions);
		if (resourcealls.size() > 0) {

			EbookTome resourceall = resourcealls.get(0);
			// int start=Integer.parseInt(resourceall.getId().substring(0,5));
			// 截取后2位
			int last = Integer.parseInt(resourceall.getId().substring(
					(resourceall.getId().length() - 2)));
			return id + StringUtils.leftPad(String.valueOf(last + 1), 2, "0");
		} else {

			return id + "01";
		}
	}

	public EbookTome getEbookTome(String id) {
		return controller.get(EbookTome.class, id);
	}

	public List<EbookTome> getEbookTomeByResourceId(String resourceId) {
		return controller.findBy(EbookTome.class, "resourceId", resourceId,
				"tomeIndex", true);
	}

	/**
	 * 此id为章节ID的前8位,规则为YXXXXXXXNNN,共11位， Y:资源类别1图书，2报纸，3杂志，4漫画，5铃声，6视频 XXXXXXX:
	 * 资源序列号，由0补足位数 NNN:章节序列号，由0补足位数 如10000001001
	 */
	public String getNewResourceChapterId(Class clasz, String resourceId)
			throws Exception {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("id", resourceId + "%",
				CompareType.Like);
		hibernateExpressions.add(ex);
		// 进行模糊 查询,传递的是资源id
		List chapters = controller.findBy(clasz, 1, 1, "id", false,
				hibernateExpressions);
		if (chapters.size() > 0) {

			Object chapter = chapters.get(0);

			String chapterID = BeanUtils.forceGetProperty(chapter, "id")
					.toString();
			// int start=Integer.parseInt(resourceall.getId().substring(0,5));
			// 截取后3位
			int last = Integer.parseInt(chapterID
					.substring((chapterID.length() - 3)));
			return resourceId
					+ StringUtils.leftPad(String.valueOf(last + 1), 3, "0");
		} else {

			return resourceId + "001";
		}
	}

	public ResourceAll getResource(String resourceId, Integer resourceType) {
		if (ResourceAll.RESOURCE_TYPE_BOOK.equals(resourceType)) {
			return controller.get(Ebook.class, resourceId);
		} else if (ResourceAll.RESOURCE_TYPE_COMICS.equals(resourceType)) {
			return controller.get(Comics.class, resourceId);
		} else if (ResourceAll.RESOURCE_TYPE_MAGAZINE.equals(resourceType)) {
			return controller.get(Magazine.class, resourceId);
		} else if (ResourceAll.RESOURCE_TYPE_NEWSPAPER.equals(resourceType)) {
			return controller.get(NewsPapers.class, resourceId);
		} else if (ResourceAll.RESOURCE_TYPE_VIDEO.equals(resourceType)) {
			return controller.get(Video.class, resourceId);
		} else if (ResourceAll.RESOURCE_TYPE_INFO.equals(resourceType)) {
			return controller.get(Infomation.class, resourceId);
		}
		return null;
	}

	public ResourceAll getResource(String resourceId) {
		return getResource(resourceId, Integer.parseInt(resourceId.substring(0,
				1)));
	}

	/*
	 * public ResourceAll getResourceFromMemcached(String resourceId){ String
	 * key = Utility.getMemcachedKey(ResourceAll.class,resourceId); Integer
	 * resourceType =Integer.parseInt(resourceId.substring(0,1)); Ebook ebook =
	 * null; Comics comics = null; Magazine magazine = null; NewsPapers papers =
	 * null;
	 * 
	 * if (ResourceAll.RESOURCE_TYPE_BOOK.equals(resourceType)) { try{ ebook =
	 * (Ebook) memcached.getAndSaveLocalMedium(key);
	 * System.out.println("---!!!---"+ebook); if(ebook != null) return ebook;
	 * }catch(Exception e){ System.out.println("从Memcached中获取作者信息出错!"); }
	 * System.out.println("---缓存中取出----"+resourceId); ebook =
	 * controller.get(Ebook.class, resourceId);
	 * memcached.setAndSaveLocalMedium(key, ebook, 5
	 * MemCachedClientWrapper.MINUTE); //放内存2小时 return ebook; } else if
	 * (ResourceAll.RESOURCE_TYPE_COMICS.equals(resourceType)) { try{ comics =
	 * (Comics) memcached.getAndSaveLocalMedium(key); if(comics != null) return
	 * comics; }catch(Exception e){ System.out.println("从Memcached中获取作者信息出错!"); }
	 * System.out.println("---缓存中取出----"+resourceId); comics =
	 * controller.get(Comics.class, resourceId);
	 * memcached.setAndSaveLocalMedium(key, comics, 10
	 * MemCachedClientWrapper.MINUTE); //放内存2小时 return comics; } else if
	 * (ResourceAll.RESOURCE_TYPE_MAGAZINE.equals(resourceType)) { try{ magazine =
	 * (Magazine) memcached.getAndSaveLocalMedium(key); if(magazine != null)
	 * return magazine; }catch(Exception e){
	 * System.out.println("从Memcached中获取作者信息出错!"); }
	 * System.out.println("---缓存中取出----"+resourceId); magazine =
	 * controller.get(Magazine.class, resourceId);
	 * memcached.setAndSaveLocalMedium(key, magazine, 10
	 * MemCachedClientWrapper.MINUTE); //放内存2小时 return magazine; } else if
	 * (ResourceAll.RESOURCE_TYPE_NEWSPAPER.equals(resourceType)) { try{ papers =
	 * (NewsPapers) memcached.getAndSaveLocalMedium(key); if(papers != null)
	 * return papers; }catch(Exception e){
	 * System.out.println("从Memcached中获取作者信息出错!"); }
	 * System.out.println("---缓存中取出----"+resourceId); papers =
	 * controller.get(NewsPapers.class, resourceId);
	 * memcached.setAndSaveLocalMedium(key, papers, 10
	 * MemCachedClientWrapper.MINUTE); //放内存2小时 return papers; } return
	 * controller.get(ResourceAll.class,resourceId); }
	 */
	public void addResource(ResourceAll resource, Integer resourceType)
			throws Exception {
		resource.setId(getNewResourceId(resourceType.toString()));
		if (resource.getInitialLetter() != null) {
			resource
					.setInitialLetter(resource.getInitialLetter().toUpperCase());
		}
		/**
		 * 2009-11-04 yuzs 添加处理书部的代码
		 */
		if (resource.getDivision() != null && resource.getDivision() > 0) {
			String resourceName = "";
			if (resource.getName().indexOf("(") != -1) {
				resourceName = resource.getName().substring(0,
						resource.getName().indexOf("("));
			}
			resource.setDivisionContent(resourceName);
		}
		controller.save(resource);
	}

	public void updateResource(ResourceAll resource, Integer resourceType) {
		if (resource.getInitialLetter() != null) {
			resource
					.setInitialLetter(resource.getInitialLetter().toUpperCase());
		}
		resource.setStatus(1);
		resource.setModifyTime(new Date());

		/**
		 * 2009-11-04 yuzs 添加处理书部的代码
		 */
		if (resource.getDivision() != null && resource.getDivision() > 0) {
			String resourceName = "";
			if (resource.getName().indexOf("(") != -1) {
				resourceName = resource.getName().substring(0,
						resource.getName().indexOf("("));
			}
			resource.setDivisionContent(resourceName);
		}
		controller.update(resource);
		changeResourceReleationStatus(resource.getId(), resource.getStatus());
	}

	public void updateResourceNOChangeStatus(ResourceAll resource,
			Integer resourceType) {
		if (resource.getInitialLetter() != null) {
			resource
					.setInitialLetter(resource.getInitialLetter().toUpperCase());
		}
		resource.setModifyTime(new Date());
		controller.update(resource);
	}

	public void deleteResource(ResourceAll resource, UserImpl user)
			throws Exception {
		auditResource(resource, 2, user);
		deleteResourceResType(resource.getId());
	}

	public void auditResourceTop(ResourceAll resource, Integer status)
			throws Exception {
		String name = "";
		if (status == 0) {
			name = "不置顶状态";
		} else {
			name = "置顶状态";
		}
		if (resource.getSearchTop().equals(status)) {
			throw new Exception("【" + resource.getName() + "】已经是" + name);
		}
		resource.setSearchTop(status);
		controller.update(resource);
	}

	public void auditResource(ResourceAll resource, Integer status,
			UserImpl user) throws Exception {
		if (resource.getStatus().equals(status)) {
			throw new Exception("[" + resource.getName() + "]已经是["
					+ resource.getBookStatus() + "]状态.");
		}
		// * 现在的资源状态(商用 0,待审 1,隐藏 2,暂停 3, 复审 4 , 否决 5 )
		Log log = new Log();
		log.setLogTime(new Date());
		log.setDetail("{资源ID:" + resource.getId() + ",资源名称:"
				+ resource.getName() + ",SP/CP:" + resource.getCpId()
				+ ",作者ID:" + resource.getAuthorId() + ",版权ID:"
				+ resource.getCopyrightId() + ",推荐语:" + resource.getBComment()
				+ ",关键字:" + resource.getRKeyword() + ",publishTime:"
				+ resource.getPublishTime() + ",出版地区:" + resource.getCArea()
				+ ",创建者:" + resource.getCreatorId() + ",创建日期:"
				+ resource.getCreateTime() + ",修改人:" + resource.getModifierId()
				+ ",修改日期:" + resource.getModifyTime() + "}");
		log.setKey("K" + resource.getId() + ",");
		log.setName("ResourceAll");
		if (user != null && user.getId() > 0)
			log.setUserId(user.getId());
		if (status == 0) { // 上线
			// System.out.println("--进入上线状态--");
			if (resource.getStatus() == 5)// 否决状态不能变为 上线
				throw new Exception("【" + resource.getName() + "】状态是【"
						+ resource.getBookStatus() + "】状态.不能变更为【商用】状态");
			if (resource.getStatus() == 1) {// 待审变商用
				log.setAction("waitToPublish");
			} else if (resource.getStatus() == 3) {// 暂停变商用(需求没有,预留 )
				log.setAction("pauseToPublish");
			} else if (resource.getStatus() == 4) {// 复审到商用
				log.setAction("aginToPublish");
			}
			if (resource.getCopyrightId() != null) {
				ResourceReferen copyright = this.getResourceReferen(resource
						.getCopyrightId());
				if (copyright == null) {
					throw new Exception("版权信息不存在");
				} else if (copyright.getStatus() != 0) {
					throw new Exception("上线资源所关联的版权信息状态必须是商用状态");
				}
			}
		}
		if (status == 3) { // 暂停
			// System.out.println("--进入暂停状态--");
			if (resource.getStatus() != 0)// 只有上线的 能变为 暂停
				throw new Exception("【" + resource.getName() + "】状态是【"
						+ resource.getBookStatus() + "】状态.不能变更为【暂停】状态");
			log.setAction("PublishTopause");// 商用--暂停
		}
		if (status == 4) { // 复审
			// System.out.println("--进入复审状态--");
			if (resource.getStatus() != 1)// 只有待审的 能变为 复审
				throw new Exception("【" + resource.getName() + "】状态是【"
						+ resource.getBookStatus() + "】状态.不能变更为【复审】状态");
			log.setAction("waitToAgin"); // 待审---复审
		}
		if (status == 5) { // 否决
			// System.out.println("--进入否决状态--");
			if (resource.getStatus() == 0 || resource.getStatus() == 3
					|| resource.getStatus() == 5 || resource.getStatus() == 2)// 只有待审、复审的
				// 能变为
				// 否决
				throw new Exception("【" + resource.getName() + "】状态是【"
						+ resource.getBookStatus() + "】状态.不能变更为【否决】状态");
			if (resource.getStatus() == 1) { // 待审--否决
				log.setAction("waitToRejected"); // 待审---复审
			} else if (resource.getStatus() == 4) { // 复审--否决
				log.setAction("aginToRejected"); // 待审---复审
			}
		}

		if (resource.getInitialLetter() != null) {
			resource
					.setInitialLetter(resource.getInitialLetter().toUpperCase());
		}
		systemService.addLog(log);
		resource.setStatus(status);
		controller.update(resource);
		changeResourceReleationStatus(resource.getId(), resource.getStatus());
	}

	public void addResourceChapter(Object chapter, Integer resourceType)
			throws Exception {
		String id = getNewResourceChapterId(chapter.getClass(), BeanUtils
				.forceGetProperty(chapter, "resourceId").toString());
		if (controller.isUnique(chapter.getClass(), chapter,
				"resourceId,chapterIndex")) {
			BeanUtils.forceSetProperty(chapter, "id", id);
			controller.save(chapter);
		} else
			throw new Exception("章节序号已经存在！");
	}

	public boolean isVideoFileNameExists(Object chapter) {
		if (controller.isUnique(chapter.getClass(), chapter,
				"resourceId,filename")) {
			return false;
		}
		return true;
	}

	public List getResourceChapter(Class clasz, String resourceId) {
		return controller.findBy(clasz, "resourceId", resourceId,
				"chapterIndex", true);
	}

	public Long getResourceChapterCount(Class clasz,
			Collection<HibernateExpression> expressions) {
		return controller.getResultCount(clasz, expressions);
	}

	public List getResourceChapterList(Class clasz, int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(clasz, pageNo, pageSize, orderBy, isAsc,
				expressions);
	}

	public void updateResourceChapter(Object clasz) throws Exception {
		controller.update(clasz);
	}

	public void deleteResourceChapter(Object chapter, Integer resourceType) {
		List<String> files = new ArrayList<String>();
		if (resourceType.equals(ResourceType.TYPE_VIDEO)) {
			VideoSuite suite = (VideoSuite) chapter;
			files.add(getChapterAddress(suite.getResourceId())
					+ suite.getFilename());
		}
		controller.delete(chapter);
		for (String file : files) {
			File f = new File(file);
			System.out.println("DElete :" + file);
			try {
				FileUtils.forceDeleteOnExit(f);
			} catch (Exception e) {
				e.printStackTrace();
			}
			rsyncUploadFile(ResourceUtil.RSYNC_TYPE_DEL, new String[] { file });
		}
	}

	public ComicsChapter getComicsChapterById(String chapterId) {
		return controller.get(ComicsChapter.class, chapterId);
	}

	public EbookChapter getEbookChapterById(String chapterId) {
		return controller.get(EbookChapter.class, chapterId);
	}

	public MagazineChapter getMagazineChapterById(String chapterId) {
		return controller.get(MagazineChapter.class, chapterId);
	}

	public NewsPapersChapter getNewsPapersChapterById(String chapterId) {
		return controller.get(NewsPapersChapter.class, chapterId);
	}

	/**
	 * 资源状态变化时更新批价包关联信息
	 * 
	 * @param resourceId
	 * @param status
	 */
	private void changeResourceReleationStatus(String resourceId, Integer status) {
		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		CompareExpression resourceE = new CompareExpression("resourceId",
				resourceId, CompareType.Equal);
		expressions.add(resourceE);
		List<ResourcePackReleation> rels = controller.findBy(
				ResourcePackReleation.class, 1, Integer.MAX_VALUE, expressions);
		for (ResourcePackReleation rel : rels) {
			if (rel.getStatus() == null || !rel.getStatus().equals(status)) {
				if (status == 2) {
					try {
						resourcePackService.delResourcePackReleation(rel);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					rel.setStatus(status);
					try {
						resourcePackService.updateResourcePackReleation(rel);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

	public void auditResourceReferen(ResourceReferen resourceReferen,
			Integer status) throws Exception {
		if (resourceReferen.getStatus().equals(status)) {
			throw new Exception("[" + resourceReferen.getName() + "]已经是["
					+ resourceReferen.getReferenStatus() + "]状态.");
		}
		resourceReferen.setStatus(status);
		controller.update(resourceReferen);

	}

	public String getPreviewCoverImg(String resourceId, String coverImg) {
		String url = getResourceDirectory(resourceId);
		if (coverImg == null)
			return "";
		return url + coverImg.replaceAll("\\.", "75.");

	}

	public String getChapterImg(String resourceId, String imgName) {
		if (resourceId.length() > 8) {
			resourceId = resourceId.substring(0, 8);
		}
		String url = getResourceDirectory(resourceId);
		return url + imgName;
	}

	public String getChapterAddress(String resourceId) {
		if (resourceId.length() > 8) {
			resourceId = resourceId.substring(0, 8);
		}
		String address = getResourceChapterAddress(resourceId);
		return address;
	}

	public String getResourceChapterAddress(String resourceId) {
		StringBuilder url = new StringBuilder();
		url.append(systemService.getVariables("media_dir").getValue());
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
		} else if (ResourceAll.RESOURCE_TYPE_VIDEO.equals(resourceType)) {
			key = "video";
		} else if (ResourceAll.RESOURCE_TYPE_INFO.equals(resourceType)) {
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

	public boolean isResourceExists(Integer resourceType, ResourceAll resource) {
		if (resourceType == ResourceType.TYPE_BOOK) {
			List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
			expressions.add(new CompareExpression("name", resource.getName(),
					CompareType.Equal));
			expressions.add(new CompareExpression("authorId", resource
					.getAuthorId(), CompareType.Equal));
			expressions.add(new CompareExpression("division", resource
					.getDivision(), CompareType.Equal));
			expressions.add(new CompareExpression("status", 2,
					CompareType.NotEqual));
			long count = controller.getResultCount(Ebook.class, expressions);
			if (count > 0)
				return true;
		} else if (resourceType == ResourceType.TYPE_MAGAZINE) {
			List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
			expressions.add(new CompareExpression("name", resource.getName(),
					CompareType.Equal));
			expressions.add(new CompareExpression("status", 2,
					CompareType.NotEqual));
			long count = controller.getResultCount(Magazine.class, expressions);
			if (count > 0)
				return true;
		}
		return false;
	}

	public String[] getUebAddress(int relId) {
		StringBuilder url = new StringBuilder();
		url.append(systemService.getVariables("media_url").getValue());
		url.append("ueb/");
		url.append(relId / 1000);
		url.append("/");
		url.append(relId);
		url.append("_");
		String[] urls = new String[3];
		urls[0] = url.toString() + "128.ueb";
		urls[0] = url.toString() + "176.ueb";
		urls[0] = url.toString() + "240.ueb";
		return urls;

	}

	private String getResourceDirectory(String resourceId) {
		StringBuilder url = new StringBuilder();
		url.append(systemService.getVariables("media_url").getValue());
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
		} else if (ResourceAll.RESOURCE_TYPE_VIDEO.equals(resourceType)) {
			key = "video";
		} else if (ResourceAll.RESOURCE_TYPE_INFO.equals(resourceType)) {
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

	public String upload(IUploadFile file, String name, File dir) {
		String fileName = file.getFileName().substring(
				file.getFileName().lastIndexOf("\\") + 1);
		InputStream fis = file.getStream();
		FileOutputStream fos = null;
		String errorMessage = "";
		try {
			String[] fileNames = fileName.split("\\.");
			if (!"".equals(name) && !"zip".equals(name)) {
				if (!fileNames[0].startsWith(name)) {
					errorMessage = "文件名称错误！应该是以" + name + "开始的文件！";
					return errorMessage;
				}
			}
			if ("zip".equals(name)) {
				if (!fileNames[1].equals(name)) {
					errorMessage = "请上传zip包！";
					return errorMessage;
				}

			}
			uploadFile(fileName, dir, fis);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ioe) {
					ioe.getMessage();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {
					ioe.getMessage();
				}
			}
		}
		return errorMessage;
	}

	public void uploadFile(String fileName, File dir, InputStream is) {
		BufferedOutputStream dest = null;
		byte data[] = new byte[4916];
		try {
			File entryFile = new File(dir, fileName);
			int count;
			FileOutputStream fos = new FileOutputStream(entryFile);
			dest = new BufferedOutputStream(fos, 4916);
			while ((count = is.read(data, 0, 4916)) != -1) {
				dest.write(data, 0, count);
			}
			// this.rsyncUploadFile(ResourceUtil.RSYNC_TYPE_ADD, new
			// String[]{entryFile.getAbsolutePath()});

			dest.flush();
			dest.close();
			fos.close();
			is.close();
		} catch (Exception e) {
			System.out.println("***:" + e.toString());
		} finally {
		}
	}

	public boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			System.out.println("删除文件失败：" + fileName + "文件不存在");
			return false;
		} else {
			if (file.isFile()) {

				return deleteFileName(fileName);
			} else {
				return deleteDirectory(fileName);
			}
		}

	}

	/**
	 * 删除单个文件
	 * 
	 * @param fileName
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true,否则返回false
	 */
	protected static boolean deleteFileName(String fileName) {
		File file = new File(fileName);
		if (file.isFile() && file.exists()) {
			file.delete();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param dir
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true,否则返回false
	 */
	protected static boolean deleteDirectory(String dir) {
		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}
		File dirFile = new File(dir);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				flag = deleteFileName(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			} else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
		}

		if (!flag) {
			return false;
		}

		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	public void rsyncUploadFile(String rsyncType, String[] fileNames) {

		// 得到上传文件储存目录的根路径
		Variables baseDirVar = systemService.getVariables("media_dir");
		File baseDir = new File(baseDirVar.getValue());
		if (!baseDir.exists())
			baseDir.mkdirs();

		// 得到rsync脚本文件存放的临时目录
		Variables uploadVar = systemService.getVariables("upload_dir");
		File upload = new File(uploadVar.getValue());
		File rsyncDir = new File(upload, "rsync_file");
		if (!rsyncDir.exists())
			rsyncDir.mkdirs();

		// 用GUID生成唯一文件名
		RandomGUID randomGuid = new RandomGUID();
		String guidStr = randomGuid.toString();
		File outputFile = new File(rsyncDir, guidStr + ".rsync");

		try {
			// 得到各变量的值
			String command = systemService.getVariables("rsync_shell")
					.getValue();
			String serverName = systemService.getVariables("rsync_server")
					.getValue();
			String rsyncIp = systemService.getVariables("rsync_ip").getValue();
			String rsyncModule = systemService.getVariables("rsync_module")
					.getValue();

			String localRoot = baseDir.getCanonicalPath();
			String[] filterFilenames = new String[fileNames.length];
			int i = 0;
			for (String file : fileNames) {
				if (file.startsWith(localRoot)) {
					filterFilenames[i++] = file
							.substring(localRoot.length() + 1);
				} else {
					filterFilenames[i++] = file;
				}
			}
			if (fileNames != null && fileNames.length > 0) {
				ResourceUtil.rsyncFile(command, outputFile, serverName,
						localRoot, rsyncIp, rsyncModule, rsyncType,
						filterFilenames);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {
		// File dir = new File("D:\\hunthawk\\reader\\pas");
		// String[] dirs = dir.list();
		// for (String s : dirs) {
		// System.out.println(s);
		// }
		// System.out.println(dir.getCanonicalPath());
		// File file = new File(
		// "D:\\hunthawk\\reader\\pas\\doc\\阅读平台部署20090915.xls");
		// System.out.println(file.getAbsolutePath().substring(
		// dir.getCanonicalPath().length() + 1));
		String ids = "123,222,33,";
		ids = "(" + ids.substring(0, ids.length() - 1);
		System.out.println(ids);
	}

	public void saveReCheck(ReCheck reCheck) {
		controller.save(reCheck);

	}

	public void updateResourceAll(ResourceAll resourceAll) {
		controller.update(resourceAll);

	}

	public List<ReCheck> findReCheckAll(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {

		return controller.findBy(ReCheck.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

	public Long findReCheckCount(Collection<HibernateExpression> expressions) {
		return controller.getResultCount(ReCheck.class, expressions);
	}

	public String splitDocContent(String text) {

		String result = text;
		String splitStr = Constants.SPLIT_DOC_TAG + "\n\n　　";// 两个中文空格
		int splitLength = 500; // 一个汉字为一个

		// try {
		// String contentSize = getVariables(Variables.DOC_CONTENT_SIZE)
		// .getValue();
		// splitLength = Integer.parseInt(contentSize);
		// } catch (Exception e) {
		// }

		if (result == null) {
			result = "";
			return result;
		}

		result = result.replaceAll(Constants.SPLIT_DOC_TAG, "");

		// 过滤掉 空格 制表符等空白字符
		// result = result.replaceAll("\r|\\v|\\t|\\f|\\x20", "");
		// 过滤掉制表符等空白字符，空格不能过滤，因为会有英文
		result = result.replaceAll("\r|\\v|\\t|\\f", "");
		result = result.replaceAll("<br/>", "");

		// ////
		// 中文空格
		result = result.replaceAll("　", "");
		// 英文空格
		result = result.replaceAll(" +", " ");

		// ///
		// 换行跟着英文空格
		result = result.replaceAll("(\n ?)+", "<br/>");

		// 替换掉法文中的特殊字符
		result = result.replaceAll("è|é|\u00EB|ê", "e");
		result = result.replaceAll("à|á|\u00E2|\u00E3|\u00E4|\u00E5", "a");

		// 替换一个utf-8格式的点号
		result = result.replaceAll("\u2022", ".");

		// 全角字符
		String quanjiao = "１２３４５６７８９０ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ．·％";
		// 半角字符
		String banjiao = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ..%";

		// 初始化map
		HashMap map = new HashMap();
		for (int i = 0; i < quanjiao.length(); i++) {
			map.put(quanjiao.substring(i, i + 1), banjiao.substring(i, i + 1));
		}

		// 全角替换成半角
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < result.length(); i++) {
			if (map.containsKey(result.substring(i, i + 1)))
				stringBuilder.append(map.get(result.substring(i, i + 1)));
			else
				stringBuilder.append(result.substring(i, i + 1));
		}
		result = stringBuilder.toString();

		String[] phrase = result.split("<br/>");
		String tempStr = "";
		result = "";
		for (int i = 0; i < phrase.length; i++) {
			if (tempStr.length() > splitLength) {
				// 文章最后一段不加分页符
				if (i == (phrase.length - 1))
					result = result + tempStr + "<br/>\n\n　　";// 两个中文空格
				else
					result = result + tempStr + splitStr;

				tempStr = phrase[i];
			} else {
				if (tempStr.length() > 0)
					tempStr = tempStr + "<br/>\n\n　　" + phrase[i];
				else
					tempStr = tempStr + phrase[i];
			}
		}

		if (tempStr.length() > 0)
			result = result + tempStr;

		return result;
	}

}