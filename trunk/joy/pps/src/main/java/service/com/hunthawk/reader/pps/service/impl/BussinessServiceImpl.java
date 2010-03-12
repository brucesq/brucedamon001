/**
 * 
 */
package com.hunthawk.reader.pps.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.memcached.NullObject;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.DefaultTemplateSet;
import com.hunthawk.reader.domain.bussiness.DefaultTemplateSetPK;
import com.hunthawk.reader.domain.bussiness.PackGroupProvinceRelation;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.bussiness.Template;
import com.hunthawk.reader.domain.bussiness.UserDefTag;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.pps.service.BussinessService;

/**
 * @author BruceSun
 * 
 */
public class BussinessServiceImpl implements BussinessService {

	private static Logger logger = Logger.getLogger(BussinessServiceImpl.class);

	private HibernateGenericController controller;

	private MemCachedClientWrapper memcached;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}

	public Product getProduct(String id) {
		// CMS��̨���²�Ʒ��Ϣʱ����ոû���
		String key = Utility.getMemcachedKey(Product.class, id);
		Object product = null;
		try {
			product = memcached.getAndSaveLocalMedium(key);
		} catch (Exception e) {
			logger.error("��Memcached�л�ȡ��Ʒ��Ϣ����!", e);
		}
		if (product == null) {
			product = controller.get(Product.class, id);
			if (product == null) {
				product = new NullObject();
			}
			memcached.setAndSaveLocalMedium(key, product,
					72 * MemCachedClientWrapper.HOUR);
		}
		if (product instanceof NullObject) {
			return null;
		}
		return (Product) product;

	}

	public Template getTemplate(int templateId) {
		// CMS��̨����ģ����Ϣʱ����ոû���
		String key = Utility.getMemcachedKey(Template.class, String
				.valueOf(templateId));
		Object tmpl = null;
		try {
			tmpl = memcached.getAndSaveLocalMedium(key);
		} catch (Exception e) {
			logger.error("��Memcached�л�ȡģ����Ϣ����!", e);
		}
		if (tmpl == null) {
			tmpl = controller.get(Template.class, templateId);
			if (tmpl == null) {
				tmpl = new NullObject();
			}
			memcached.setAndSaveLocalMedium(key, tmpl,
					72 * MemCachedClientWrapper.HOUR);
		}
		if (tmpl instanceof NullObject) {
			return null;
		}
		return (Template) tmpl;

	}
	
	public Integer getDefaultTemplate(Integer pageType,Integer wapType){
		String key = Utility.getMemcachedKey(DefaultTemplateSet.class, String
				.valueOf(pageType), String
				.valueOf(wapType));
		try{
			Integer templateId = (Integer)memcached.getAndSaveLocalMedium(key);
			if(templateId != null)
				return templateId;
		}catch(Exception e){
			logger.error("��Memcached�л�ȡĬ��ģ����Ϣ����!", e);
		}
		DefaultTemplateSetPK pk = new DefaultTemplateSetPK();
		pk.setPageType(pageType);
		pk.setWapType(wapType);
		DefaultTemplateSet defaultset = controller.get(DefaultTemplateSet.class,pk);
		if(defaultset != null){
			memcached.setAndSaveLocalMedium(key, defaultset.getTemplateId(), 72 * MemCachedClientWrapper.HOUR);
			return defaultset.getTemplateId();
		}
		return -1;
	}

	public PageGroup getPageGroup(int pagegroupId) {
		// CMS����ҳ������Ϣʱ�����¸û���
		String key = Utility.getMemcachedKey(PageGroup.class, String
				.valueOf(pagegroupId));
		Object pg = null;
		try {
			pg = memcached.getAndSaveLocalMedium(key);
		} catch (Exception e) {
			logger.error("��Memcached�л�ȡҳ������Ϣ����!", e);
		}
		if (pg == null) {
			pg = controller.get(PageGroup.class, pagegroupId);
			if (pg == null) {
				pg = new NullObject();
			}
			memcached.setAndSaveLocalMedium(key, pg,
					72 * MemCachedClientWrapper.HOUR);
		}
		if (pg instanceof NullObject) {
			return null;
		}
		return (PageGroup) pg;
	}

	// public List<Columns> getColumnsByPackId(int packId){
	// // CMS������Ŀ��Ϣʱ�����¸û���
	// String key = Columns.class.getName() + Constants.MEMCACHED_SLASH
	// + packId;
	// List<Columns> cols = null;
	// try {
	// cols = (List<Columns>) memcached
	// .getAndSaveLocalMedium(key);
	// if (cols != null)
	// return cols;
	// } catch (Exception e) {
	// logger.error("��Memcached�л�ȡ��Ŀ�б���Ϣʱ����!", e);
	// }
	// Collection<HibernateExpression> expressions = new
	// ArrayList<HibernateExpression>();
	// HibernateExpression ex = new CompareExpression("pricepackId", packId,
	// CompareType.Equal);
	// expressions.add(ex);
	// cols = controller.findBy(Columns.class, 1, 1000,
	// expressions);
	// memcached.setAndSaveLocalMedium(key, cols,
	// 72 * MemCachedClientWrapper.HOUR);
	// return cols;
	// }
	public Columns getColumns(int columnId) {
		// CMS������Ŀ��Ϣʱ�����¸û���
		String key = Utility.getMemcachedKey(Columns.class, String
				.valueOf(columnId));
		Object col = null;
		try {
			col = memcached.getAndSaveLocalMedium(key);
		} catch (Exception e) {
			logger.error("��Memcached�л�ȡ��Ŀ��Ϣ����!", e);
		}
		if (col == null) {
			col = controller.get(Columns.class, columnId);
			if (col == null) {
				col = new NullObject();
			}
			memcached.setAndSaveLocalMedium(key, col,
					72 * MemCachedClientWrapper.HOUR);
		}
		if (col instanceof NullObject) {
			return null;
		}
		return (Columns) col;
	}

	@SuppressWarnings("unchecked")
	public List<PackGroupProvinceRelation> getPackGroupProvinceRelation(
			String productId) {
		// CMS����ҳ���������Ϣʱ����ոû���
		String key = Utility.getMemcachedKey(PackGroupProvinceRelation.class,
				productId);
		List<PackGroupProvinceRelation> rels = null;
		try {
			rels = (List<PackGroupProvinceRelation>) memcached
					.getAndSaveLocalMedium(key);
			if (rels != null)
				return rels;
		} catch (Exception e) {
			logger.error("��Memcached�л�ȡ��Ʒҳ���������Ϣʱ����!", e);
		}
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("pid", productId,
				CompareType.Equal);
		expressions.add(ex);
		rels = controller.findBy(PackGroupProvinceRelation.class, 1, 1000,
				expressions);
		memcached.setAndSaveLocalMedium(key, rels,
				72 * MemCachedClientWrapper.HOUR);
		return rels;
	}

	/**
	 * ����ҳ����ID������Ϣ
	 * 
	 * @param pageGroupId
	 * @return
	 */
	// @SuppressWarnings("unchecked")
	// public List<PackGroupProvinceRelation>
	// getPackGroupProvinceRelationByPageGroupId(
	// int pageGroupId) {
	// Collection<HibernateExpression> expressions = new
	// ArrayList<HibernateExpression>();
	// HibernateExpression ex = new CompareExpression("pgid", pageGroupId,
	// CompareType.Equal);
	// expressions.add(ex);
	// return controller.findBy(PackGroupProvinceRelation.class, 1, 1000,
	// expressions);
	// }
	@SuppressWarnings("unchecked")
	public List<Columns> getColumnChilds(int parentId,int pageNum,int pageSize,int order) {
		// CMS������Ŀʱ����ոû���
		String key = Utility.getMemcachedKey(Columns.class,
				String.valueOf(parentId), String.valueOf(pageNum), String
						.valueOf(pageSize));
		List<Columns> cols = null;
		try {
			cols = (List<Columns>) memcached.getAndSaveLocalMedium(key);
			if (cols != null)
				return  cols;
		} catch (Exception e) {
			logger.error("��Memcached�л�ȡ��Ʒ����Ŀ��Ϣʱ����!", e);
		}
		String hql="from Columns where parent=? and status=1 ";
		switch(order){
		case 0:hql+=" order by order desc";break;
		case 1:hql+=" order by id desc";break;
		case 2:hql+=" order by order asc";break;//��Ŀ�����ڵ����  ָ�����5
		case 5:hql+=" order by order asc";break;
		case 6:hql+=" order by id asc";break;
		default:hql+=" order by order asc,id desc"; //Ĭ�� order ���� id����
		}
		//String hql="from Columns where parent=? and status=1 order by order asc,id desc";
		Columns parent = new Columns();
		parent.setId(parentId);
		cols=controller.findBy(hql,pageNum, pageSize, parent);
		memcached.setAndSaveLocalMedium(key, cols,
				5 * MemCachedClientWrapper.MINUTE);
		return cols;
		
	}

	public Variables getVariables(String name) {

		// CMS����ϵͳ����ʱ����ոû���
		String key = Utility.getMemcachedKey(Variables.class, name);
		Object var = null;
		try {
			var = memcached.getAndSaveLocalLong(key);
		} catch (Exception e) {
			logger.error("��Memcached�л�ȡϵͳ������Ϣʱ����!", e);
		}
		if (var == null) {
			Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
			HibernateExpression ex = new CompareExpression("name", name,
					CompareType.Equal);
			hibernateExpressions.add(ex);
			List<Variables> variables = controller.findBy(Variables.class, 1,
					1, hibernateExpressions);
			if (variables.size() > 0) {
				var = variables.get(0);
			} else {
				var = new NullObject();
			}
			memcached
					.setAndSaveLong(key, var, 72 * MemCachedClientWrapper.HOUR);
		}
		if (var instanceof NullObject)
			return null;
		return (Variables) var;
	}
	public List<Columns> getColumnsByPageGroupId(Integer pageGroupId,int pageNum,int pageSize){
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		PageGroup pagegroup = new PageGroup();
		pagegroup.setId(pageGroupId);
		HibernateExpression ex = new CompareExpression("pagegroup", pagegroup,
				CompareType.Equal);
		hibernateExpressions.add(ex);
		List<Columns> columns = controller.findBy(Columns.class, pageNum,
				pageSize, hibernateExpressions);
		return columns;
	}
	public Columns getColumnsByResourceType(Integer pageGroupId,Integer resourceTypeId){
		String key = Utility.getMemcachedKey(Columns.class, pageGroupId.toString(),resourceTypeId.toString() );
		Columns col = null;
		try{
			col = (Columns)memcached.getAndSaveLocalMedium(key);
			if(col != null){
				return col;
			}
		}catch(Exception e){
			logger.error("��Memcached�л�ȡ��Դ������Ŀ��Ϣʱ����!", e);
		}
		
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		PageGroup pagegroup = new PageGroup();
		pagegroup.setId(pageGroupId);
		HibernateExpression ex = new CompareExpression("pagegroup", pagegroup,
				CompareType.Equal);
		hibernateExpressions.add(ex);
		
		HibernateExpression columnTypeE = new CompareExpression("columnType", 1,
				CompareType.Equal);
		hibernateExpressions.add(columnTypeE);
		
		HibernateExpression statusE = new CompareExpression("status", 1,
				CompareType.Equal);
		hibernateExpressions.add(statusE);
		
		HibernateExpression TypeE = new CompareExpression("resourceTypeId", resourceTypeId,
				CompareType.Equal);
		hibernateExpressions.add(TypeE);
		List<Columns> columns = controller.findBy(Columns.class, 1,
				1, hibernateExpressions);
		if(columns.size() > 0){
			col = columns.get(0);
			memcached.setAndSaveLocalMedium(key, col, 10*MemCachedClientWrapper.MINUTE);
			return col;
		}else{
			return null;
		}
	}
	public String getDefaultChannelId(String productId){
		Product product = getProduct(productId);
		return product.getShowType()+product.getChannel().getId()+"000";
	}
	
	public UserDefTag getUserDefTagById(int userTagId){
		String key = Utility.getMemcachedKey(UserDefTag.class, String.valueOf(userTagId));
		Object var = null;
		try {
			var = memcached.getAndSaveLocalLong(key);
			if(var!=null)
				return (UserDefTag)var;
		} catch (Exception e) {
			logger.error("��Memcached�л�ȡ�û��Զ����ǩ��Ϣʱ����!", e);
		}
		
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression statusE= new CompareExpression("status", 1,
				CompareType.Equal);
		hibernateExpressions.add(statusE);
		HibernateExpression idE= new CompareExpression("id", userTagId,
				CompareType.Equal);
		hibernateExpressions.add(idE);
		List<UserDefTag> tags=controller.findBy(UserDefTag.class, 1, 1, hibernateExpressions);
		
		if(tags!=null && tags.size()>0){
			memcached.setAndSaveLong(key, tags.get(0), 72 * MemCachedClientWrapper.HOUR);
			return tags.get(0);
		}
		return null;
	}
	
	public TagTemplate getTagTemplate(int id){
		String key = Utility.getMemcachedKey(TagTemplate.class, String.valueOf(id));
		Object var = null;
		try {
			var = memcached.getAndSaveLocalLong(key);
			if(var!=null)
				return (TagTemplate)var;
		} catch (Exception e) {
			logger.error("��Memcached�л�ȡ��ǩģ����Ϣʱ����!", e);
		}
		
		
		TagTemplate tm =controller.get(TagTemplate.class, id);
		
		if(tm != null){
			memcached.setAndSaveLong(key, tm, 1 * MemCachedClientWrapper.HOUR);
			return tm;
		}
		return null;
	}
	/**add by liuxh 09-12-24*/
	public Material getMaterial(Integer id){
		return controller.get(Material.class, id);
	}
	/**end*/
	/**
	 * ��ҳ
	 * 
	 * @param list
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	private List page(List list, int pageNo, int pageSize) {
		if(list == null || list.size()<2){
			return list;
		}
		int start = pageSize * (pageNo - 1);
		int end = pageSize * pageNo;
		start = start > list.size() - 1 ? list.size() - 1 : start;
		end = end > list.size() ? list.size() : end;
		return list.subList(start, end);
	}
}
