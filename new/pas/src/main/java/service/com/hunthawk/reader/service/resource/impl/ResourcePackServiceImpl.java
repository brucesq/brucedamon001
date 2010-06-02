package com.hunthawk.reader.service.resource.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.resource.UebService;

public class ResourcePackServiceImpl implements ResourcePackService {

	private HibernateGenericController controller;

	private UebService uebService;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void setUebService(UebService uebService) {
		this.uebService = uebService;
	}

	public void addPack(ResourcePack pack) throws Exception {

		controller.save(pack);
	}

	private MemCachedClientWrapper memcached;

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}

	public void deleteResourcePack(ResourcePack pack) throws Exception {

		/***********************************************************************
		 * ---2009-10-29---�޸� yuzs ����Ŀ������ʱ�� ���۰�����ɾ��
		 */
		Collection<HibernateExpression> expressions1 = new ArrayList<HibernateExpression>();
		HibernateExpression ex1 = new CompareExpression("pricepackId", pack
				.getId(), CompareType.Equal);
		expressions1.add(ex1);

		HibernateExpression ex2 = new CompareExpression("status", // ������ɾ��״̬��
				3, CompareType.NotEqual);
		expressions1.add(ex2);

		List<Columns> list = controller.findBy(Columns.class, 1,
				Integer.MAX_VALUE, "id", true, expressions1);
		if (list != null && list.size() > 0) {
			throw new Exception("�����۰��Ѿ�����Ŀ�����ˣ���ɾ��������ϵ����ɾ����");
		}
		// ------------------�޸Ľ���----------------------------------
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("pack", pack,
				CompareType.Equal);
		expressions.add(ex);
		List<ResourcePackReleation> releationList = controller.findBy(
				ResourcePackReleation.class, 1, Integer.MAX_VALUE, "id", true,
				expressions);
		if (releationList != null && releationList.size() > 0) {
			for (ResourcePackReleation releation : releationList) {
				controller.delete(releation);
				uebService.deleteUeb(releation);
			}

		}
		controller.delete(pack);
	}

	public void updatePack(ResourcePack pack) {

		controller.update(pack);
	}

	@SuppressWarnings("unchecked")
	public List findEpack(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions) {

		return controller.findBy(ResourcePack.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

	public Object getEpack(Integer id) {

		return controller.get(ResourcePack.class, id);
	}

	public Long getEpackCount(Collection<HibernateExpression> expressions) {

		return controller.getResultCount(ResourcePack.class, expressions);
	}

	public List getAllEpacks() {
		return controller.getAll(ResourcePack.class);
	}

	/***************************************************************************
	 * ��Դ- ���۰���ϵ
	 */

	public void addResourcePackReleation(
			ResourcePackReleation resourcePackRelation) throws Exception {
		if (controller.isUnique(ResourcePackReleation.class,
				resourcePackRelation, "pack,resourceId")) {
			controller.save(resourcePackRelation);
			uebService.createUeb(resourcePackRelation);
		} else {
			throw new Exception("��Դ" + resourcePackRelation.getResourceId()
					+ "�Ѿ����뵽�����۰���");
		}
	}

	public void delResourcePackReleation(
			ResourcePackReleation resourcePackRelation) {
		controller.delete(resourcePackRelation);
		uebService.deleteUeb(resourcePackRelation);
	}

	public List<ResourcePackReleation> getResourceFromEpack(int pageNo,
			int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		// ��������״̬������
		HibernateExpression statusE = new CompareExpression("status", 2,
				CompareType.NotEqual);
		expressions.add(statusE);
		return controller.findBy(ResourcePackReleation.class, pageNo, pageSize,
				orderBy, isAsc, expressions);
	}

	public Object getResourcePackReleation(Integer id) {
		return controller.get(ResourcePackReleation.class, id);
	}

	public Long getResourcePackReleationCount(
			Collection<HibernateExpression> expressions) {
		HibernateExpression statusE = new CompareExpression("status", 2,
				CompareType.NotEqual);
		expressions.add(statusE);
		return controller.getResultCount(ResourcePackReleation.class,
				expressions);
	}

	public List getResourcePackReleations() {
		return controller.getAll(ResourcePackReleation.class);
	}

	public void updateResourcePackReleation(
			ResourcePackReleation resourcePackRelation) {
		controller.update(resourcePackRelation);
		uebService.createUeb(resourcePackRelation);
	}

	public void updateResourcePackReleationNotCreateUEB(
			ResourcePackReleation resourcePackRelation) {
		controller.update(resourcePackRelation);
	}

	public void updatePackReleationNotCreateUEBAndLog(
			ResourcePackReleation resourcePackRelation) {
		controller.update(resourcePackRelation);
	}

	public String addResourcePackReleationMessgae(
			ResourcePackReleation resourcePackRelation, ResourceAll resourceAll) {
		String message = "";
		if (controller.isUnique(ResourcePackReleation.class,
				resourcePackRelation, "pack,resourceId")) {
			controller.save(resourcePackRelation);
			uebService.createUeb(resourcePackRelation);
		} else {
			message += resourceAll.getName() + "/";
		}
		return message;
	}

	public List<ResourceAll> findResourceByHQL(Integer packid,
			Integer authorId, Integer sortid) {
		List param = new ArrayList();
		return null;
	}

	public Long getResourcePackReleationResultCountByHQL(Integer resourceType,
			boolean tof, String name, ResourcePack pack, Integer authorId,
			Integer childTypeId, Integer resourceStatus, Integer cpID,
			String feeID) {
		List param = new ArrayList();
		String hql = "select count(distinct rel.id) from ResourcePackReleation rel , ResourceResType restype ,ResourceAll resource  where  rel.resourceId=resource.id  and  resource.status<>2";
		/*
		 * ��ѯ�б��� yuzs 2009-11-10
		 */
		if (tof) { // ����

			if (childTypeId != null && childTypeId > 0) {
				hql += " and  restype.resTypeId <>? ";
				param.add(childTypeId);
			}
			if (name != null) {
				hql += " and resource.name not like ? ";
				param.add("%" + name + "%");
			}
			if (authorId != null && authorId > 0) {
				hql += " and resource.authorId not like ? ";
				param.add("%|" + authorId + "|%");
			}

			if (resourceStatus != null && resourceStatus > -1) {
				hql += " and resource.status <> ? ";
				param.add(resourceStatus);
			}

			if (cpID != null && cpID.intValue() > 0) {
				hql += " and resource.cpId <> ? ";
				param.add(cpID);
			}

			if (StringUtils.isNotEmpty(feeID)) {
				hql += " and rel.feeId <> ? ";
				param.add(feeID);
			}

			// hql += " and resource.id not in(select restype.rid from restype
			// where restype.resTypeId = "+childTypeId+")";//���޸�
			hql += "   and  not exists(select 'x' from restype where  resource.id = restype.rid and restype.resTypeId = "
					+ childTypeId + ")";
			// �ĳ� no exit
		} else { // ������

			if (childTypeId != null && childTypeId > 0) {
				hql += " and  restype.resTypeId =? ";
				param.add(childTypeId);
			}
			if (authorId != null && authorId > 0) {
				hql += " and resource.authorId like ? ";
				param.add("%|" + authorId + "|%");
			}
			if (resourceStatus != null && resourceStatus > -1) {
				hql += " and resource.status = ? ";
				param.add(resourceStatus);
			}

			if (cpID != null && cpID.intValue() > 0) {
				hql += " and resource.cpId = ? ";
				param.add(cpID);
			}

			if (StringUtils.isNotEmpty(feeID)) {
				hql += " and rel.feeId = ? ";
				param.add(feeID);
			}
			if (name != null) {
				hql += " and resource.name like ? ";
				param.add("%" + name + "%");
			}
			hql += "   and  resource.id = restype.rid  ";
		}
		if (resourceType != null) {
			hql += " and resource.id like ?  ";
			param.add(resourceType + "%");
		}
		if (pack != null) {
			hql += " and rel.pack = ? ";
			param.add(pack);
		}
		final int size = param.size();
		Object[] arr = (Object[]) param.toArray(new Object[size]);
		Long currentTime = System.currentTimeMillis();
		System.out.println("---sql--����-" + hql);
		List<Long> counts = controller.findBy(hql, arr);
		Long newTime = System.currentTimeMillis();
		System.out.println("ִ��ʱ��--������Դ-- ����ִ��ʱ��---" + (newTime - currentTime));
		return counts.get(0);
	}

	public List<ResourcePackReleation> findResourcePackReleationByHQL(
			Integer resourceType, boolean tof, String name, ResourcePack pack,
			Integer authorId, Integer childTypeId, Integer resourceStatus,
			Integer cpID, String feeID, int pageNum, int pageSize, Integer order) {
		List param = new ArrayList();
		// String hql = "select distinct rel from ResourcePackReleation rel ,
		// ResourceAll resource, ResourceResType restype where
		// rel.resourceId=resource.id and resource.status<2";
		String hql = "select distinct rel,"
				+ "resource.rankingNum,"
				+ "resource.rankingNumMonth,"
				+ "resource.orderNum,"
				+ "resource.orderNumMonth "
				+ " from  ResourcePackReleation rel ,ResourceResType restype, ResourceAll resource  where  rel.resourceId=resource.id  and  resource.status<>2 ";

		/*
		 * ��ѯ�б��� yuzs 2009-11-10
		 */
		if (tof) { // ����
			if (childTypeId != null && childTypeId > 0) {
				hql += " and  restype.resTypeId <>? ";
				param.add(childTypeId);
			}
			if (name != null) {
				hql += " and resource.name not like ? ";
				param.add("%" + name + "%");
			}
			if (authorId != null && authorId > 0) {
				hql += " and resource.authorId not like ? ";
				param.add("%|" + authorId + "|%");
			}

			if (resourceStatus != null && resourceStatus > -1) {
				hql += " and resource.status <> ? ";
				param.add(resourceStatus);
			}

			if (cpID != null && cpID.intValue() > 0) {
				hql += " and resource.cpId <> ? ";
				param.add(cpID);
			}

			if (StringUtils.isNotEmpty(feeID)) {
				hql += " and rel.feeId <> ? ";
				param.add(feeID);
			}
			// hql += " and resource.id not in(select restype.rid from restype
			// where restype.resTypeId = "+childTypeId+") "; //��䲻�У����ݿ��ѯ��ʱ߯��
			hql += "   and  not exists(select 'x' from restype where  resource.id = restype.rid and restype.resTypeId = "
					+ childTypeId + ")";
		} else { // �����飨����Ҫ�Ż���

			if (childTypeId != null && childTypeId > 0) {
				hql += " and  restype.resTypeId =? ";
				param.add(childTypeId);
			}
			if (name != null) {
				hql += " and resource.name like ? ";
				param.add("%" + name + "%");
			}
			if (authorId != null && authorId > 0) {
				hql += " and resource.authorId like ? ";
				param.add("%|" + authorId + "|%");
			}
			if (resourceStatus != null && resourceStatus > -1) {
				hql += " and resource.status = ? ";
				param.add(resourceStatus);
			}

			if (cpID != null && cpID.intValue() > 0) {
				hql += " and resource.cpId = ? ";
				param.add(cpID);
			}

			if (StringUtils.isNotEmpty(feeID)) {
				hql += " and rel.feeId = ? ";
				param.add(feeID);
			}
			// hql += " and resource.id = restype.rid and resource.id =
			// rel.resourceId";
			hql += "    and  resource.id = restype.rid ";
		}
		if (resourceType != null) {
			hql += " and resource.id like ?  ";
			param.add(resourceType + "%");
		}
		if (pack != null) {
			hql += " and rel.pack = ? ";
			param.add(pack);
		}
		if (order != null) {
			switch (order) {
			case 1:// order ����
				hql += "order by rel.order";
				break;
			case 2:// id ����
				hql += "order by rel.id desc ";
				break;
			case 10:
				hql += "order by resource.rankingNum desc";
				break;// pv������
			case 11:
				hql += "order by resource.rankingNumMonth desc";
				hql += "";
				break;// pv������
			case 20:
				hql += "order by resource.orderNum desc";
				break;// ����������
			case 21:
				hql += "order by resource.orderNumMonth desc";
				break;// ����������
			default:
				hql += "order by rel.order";
			}
		} else {
			hql += "order by rel.order";
		}
		System.out.println("===dddd!=!!=" + hql);
		final int size = param.size();
		Object[] arr = (Object[]) param.toArray(new Object[size]);

		// List<ResourcePackReleation> rels = controller.findBy(hql, pageNum,
		// pageSize, arr);
		Long currentTime = System.currentTimeMillis();
		List<Object[]> list = controller.findBy(hql, pageNum, pageSize, arr);
		Long newTime = System.currentTimeMillis();
		System.out.println("ִ��ʱ��--- ������Դ�б�ִ��ʱ��---" + (newTime - currentTime));
		List<ResourcePackReleation> rels = new ArrayList<ResourcePackReleation>();
		for (Object[] obj : list) {

			rels.add((ResourcePackReleation) obj[0]);
			// System.out.println("����ĵ�һ���ǣ�������"+obj[0]);
		}
		return rels;
	}

	/*
	 * public Map<String,ResourceAll> getResourceByPack(ResourcePack pack,int
	 * pageNum, int pageSize,Integer order){
	 * 
	 * String key =
	 * Utility.getMemcachedKey(ResourceAuthor.class,String.valueOf(pack.getId()),String.valueOf(pageNum),String.valueOf(pageSize));
	 * Map<String,ResourceAll> maps=null; try { maps = (Map<String,
	 * ResourceAll>) memcached.getAndSaveLocalMedium(key); if(maps !=null &&
	 * maps.size()>0) return maps; } catch (Exception e) {
	 * System.out.println("��Memcached�л�ȡ�����۰��µ���Դ����!"); }
	 * System.out.println("----����ȡ����Դ�б�---"); List param = new ArrayList();
	 * //String hql = "select distinct rel from ResourcePackReleation rel ,
	 * ResourceAll resource, ResourceResType restype where
	 * rel.resourceId=resource.id and resource.status<2"; String hql = "select
	 * resource,rel.order,rel.id " + " from ResourcePackReleation rel ,
	 * ResourceAll resource where rel.resourceId=resource.id and resource.status<>2 ";
	 * if (pack != null) { hql += " and rel.pack = ? "; param.add(pack); }
	 * if(order !=null){ switch(order){ case 1 ://order ���� hql +="order by
	 * rel.order"; break; case 2://id ���� hql +="order by rel.id desc "; break;
	 * case 10: hql += "order by resource.rankingNum desc"; break;// pv������ case
	 * 11: hql += "order by resource.rankingNumMonth desc"; hql += ""; break;//
	 * pv������ case 20: hql += "order by resource.orderNum desc"; break;// ����������
	 * case 21: hql += "order by resource.orderNumMonth desc"; break;// ����������
	 * default: hql +="order by rel.order"; } }else{ hql +="order by rel.order"; }
	 * final int size = param.size(); Object[] arr = (Object[])
	 * param.toArray(new Object[size]);
	 * 
	 * //List<ResourcePackReleation> rels = controller.findBy(hql, pageNum,
	 * pageSize, arr); Long currentTime = System.currentTimeMillis(); List<Object[]>
	 * list = controller.findBy(hql,pageNum,pageSize,arr); Long newTime =
	 * System.currentTimeMillis(); System.out.println("ִ��ʱ��--- ������Դ�б�ִ��ʱ��-
	 * ��Դ--"+(newTime - currentTime)); maps = new HashMap<String,ResourceAll>();
	 * for(Object[] obj : list){ ResourceAll resource = (ResourceAll) obj[0];
	 * maps.put(resource.getId(), resource); } for(ResourceAll resource : list){
	 * maps.put(resource.getId(), resource); }
	 * memcached.setAndSaveLocalMedium(key, maps, 10 *
	 * MemCachedClientWrapper.MINUTE); //���ڴ�1Сʱ
	 * System.out.println("maps�ĳ���----"+maps.size()); return maps; }
	 */
	public List<ResourcePackReleation> findResourcePackReleationByPack(
			ResourcePack pack) {
		return controller.findBy(ResourcePackReleation.class, "pack", pack,
				"id", false);
	}

	public Set<ResourcePack> findResourcePackByResource(String resourceId) {
		Set<ResourcePack> set = new HashSet<ResourcePack>();
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("resourceId",
				resourceId, CompareType.Equal);
		expressions.add(ex);
		List<ResourcePackReleation> releationList = controller.findBy(
				ResourcePackReleation.class, 1, Integer.MAX_VALUE, "id", true,
				expressions);
		for (ResourcePackReleation res : releationList)
			set.add(res.getPack());
		return set;
	}

	/**
	 * ��ѯ�����С��ţ��Լ���ǰ��Ӧ��ϵ ����2����Դ 2009-11-12�� yuzs
	 */
	public Integer findMaxOrderInPack(ResourcePack pack) {
		Integer returnValue = null;

		String hql = "select max(rpr.order),max(rpr.id) from ResourcePackReleation rpr where rpr.pack = ?";
		Object[] arr = new Object[] { pack };
		List<Object[]> list = controller.findBy(hql, 1, Integer.MAX_VALUE, arr);
		for (Object[] obj : list) {
			returnValue = (Integer) obj[0];
			System.out.println("������--����ţ���" + obj[0]);
		}
		return returnValue;
	}

	public Integer findMinOrderInPack(ResourcePack pack) {
		Integer returnValue = null;
		String hql = "select min(rpr.order),min(rpr.id) from ResourcePackReleation rpr where rpr.pack = ? ";
		Object[] arr = new Object[] { pack };
		List<Object[]> list = controller.findBy(hql, 1, Integer.MAX_VALUE, arr);
		for (Object[] obj : list) {
			// rels.add((ResourcePackReleation)obj[0]);
			// if(obj[0]!= null)
			returnValue = (Integer) obj[0];
			System.out.println("�����С��--����ţ�����" + obj[0]);
		}
		return returnValue;
	}

	// ȡ�õ�ǰ��Դ ����� ���ڵ�2�� ��Դ ��/��

	public ResourcePackReleation findBesideDownReleation(ResourcePack pack,
			ResourcePackReleation releation) {
		String hql = "select rel from ResourcePackReleation rel where rel.order = (";
		hql += "select min(rpr.order) from ResourcePackReleation rpr where rpr.pack = ? and rpr.order > ? ) ";
		hql += "and rel.pack=?";
		Object[] arr = new Object[] { pack, releation.getOrder(), pack };
		List<ResourcePackReleation> list = controller.findBy(hql, 1,
				Integer.MAX_VALUE, arr);
		System.out.println("����Ľ��ڵ�����ţ� �£�����" + list.get(0));
		return list.get(0);
	}

	public ResourcePackReleation findBesideUpReleation(ResourcePack pack,
			ResourcePackReleation releation) {
		// String hql = "select max(rpr.order),max(rpr.id) from
		// ResourcePackReleation rpr where rpr.pack = ? and rpr.order < ?";
		String hql = "select rel from ResourcePackReleation rel where rel.order = (";
		hql += "select max(rpr.order) from ResourcePackReleation rpr where rpr.pack = ? and rpr.order < ? ) ";
		hql += "and rel.pack=?";
		Object[] arr = new Object[] { pack, releation.getOrder(), pack };
		List<ResourcePackReleation> list = controller.findBy(hql, 1,
				Integer.MAX_VALUE, arr);
		System.out.println("����Ľ��ڵ�����ţ� �ϣ�����" + list.get(0));
		return list.get(0);
	}

	/**
	 * ��ѯ��ǰ��Դ��ϵ��order �������С ���б� �� order �����������б�
	 */
	public List<ResourcePackReleation> getMaxOrderList(ResourcePack pack,
			ResourcePackReleation releation) { // �ȵ�ǰorder ��� �б�
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("pack", pack,
				CompareType.Equal);
		expressions.add(ex);
		HibernateExpression ex1 = new CompareExpression("order", releation
				.getOrder(), CompareType.Gt);
		expressions.add(ex1);
		List<ResourcePackReleation> releationList = controller.findBy(
				ResourcePackReleation.class, 1, Integer.MAX_VALUE, "order",
				true, expressions);
		return releationList;
	}

	public List<ResourcePackReleation> getMinOrderList(ResourcePack pack,
			ResourcePackReleation releation) {// �ȵ�ǰ�б�С���б�
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("pack", pack,
				CompareType.Equal);
		expressions.add(ex);
		HibernateExpression ex1 = new CompareExpression("order", releation
				.getOrder(), CompareType.Lt);
		expressions.add(ex1);
		List<ResourcePackReleation> releationList = controller.findBy(
				ResourcePackReleation.class, 1, Integer.MAX_VALUE, "order",
				true, expressions);
		return releationList;
	}

	/**
	 * ȡ�� ǰ��λ����order����ͬ�� ��Դ����
	 * 
	 * @param pack
	 * @param releation
	 *            //ǰ��λ
	 * @return
	 */
	public Long getSumSubOrder(ResourcePack pack, Integer firstOrder) {

		Integer returnValue = null;
		String hql = "select count(rpr.order) from ResourcePackReleation rpr where rpr.pack = ? and rpr.order like '"
				+ firstOrder + "%'";
		Object[] arr = new Object[] { pack };
		List<Long> list = controller.findBy(hql, 1, Integer.MAX_VALUE, arr);
		/*
		 * for(Object[] obj : list){ //rels.add((ResourcePackReleation)obj[0]);
		 * //if(obj[0]!= null) returnValue = (Integer)obj[0];
		 * System.out.println("��ͬ�ĸ�order ������----����"+obj[0]); }
		 */
		System.out.println("��ͬ�ĸ�order ������-!!!---����" + list.get(0));
		return list.get(0);
	}

	/**
	 * ȡ�� ���� order ֮��� ����Դ�б�
	 * 
	 * @param pack
	 * @param releation
	 *            //ǰ��λ
	 * @return
	 */
	public Map<Integer, ResourcePackReleation> getSubOrderList(
			ResourcePack pack, Integer firstOrder, Integer lastOrder) {

		String hql = "select rpr from ResourcePackReleation rpr where rpr.pack = ? and  rpr.order >? and rpr.order<=? order by rpr.order";
		Object[] arr = new Object[] { pack, firstOrder, lastOrder };
		List<ResourcePackReleation> list = controller.findBy(hql, 1,
				Integer.MAX_VALUE, arr);
		Map<Integer, ResourcePackReleation> maps = new OrderedMap<Integer, ResourcePackReleation>();
		for (ResourcePackReleation rel : list)
			maps.put(rel.getId(), rel);
		return maps;
	}

	/**
	 * ȡ�� ǰ��λ����order����ͬ�� �����Ǹ���Դ
	 * 
	 * @param pack
	 * @param releation
	 *            //ǰ��λ
	 * @return
	 */
	public ResourcePackReleation getMaxSubOrder(ResourcePack pack,
			Integer firstOrder) {
		System.out.println("---4---111--");
		String hql = "select rel from ResourcePackReleation rel where rel.order = (";
		hql += "select max(rpr.order) from ResourcePackReleation rpr where rpr.pack = ? and rpr.order like '"
				+ firstOrder + "%') ";
		hql += "and rel.pack=?";
		Object[] arr = new Object[] { pack, pack };
		List<ResourcePackReleation> list = controller.findBy(hql, 1,
				Integer.MAX_VALUE, arr);
		System.out.println("��ͬ�ĸ�order �����Ǹ�������" + list.get(0));
		return list.get(0);
	}

	/**
	 * ����ָ��λ�õ���Դ
	 */
	public ResourcePackReleation getIndexReleation(ResourcePack pack,
			Integer index) {
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("pack", pack,
				CompareType.Equal);
		expressions.add(ex);
		List<ResourcePackReleation> lists = controller.findBy(
				ResourcePackReleation.class, 1, index, "order", true,
				expressions);
		ResourcePackReleation rel = null;
		if (lists != null && lists.size() > 0) {
			rel = lists.get(lists.size() - 1);
		}
		return rel;
	}

}
