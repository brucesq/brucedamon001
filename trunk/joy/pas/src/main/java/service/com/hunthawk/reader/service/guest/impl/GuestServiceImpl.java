/**
 * 
 */
package com.hunthawk.reader.service.guest.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.device.MobileInfo;
import com.hunthawk.reader.domain.device.PersonGroup;
import com.hunthawk.reader.domain.device.PersonInfo;
import com.hunthawk.reader.domain.device.PersonInfoPK;
import com.hunthawk.reader.domain.device.UAGroup;
import com.hunthawk.reader.domain.device.UAGroupRelation;
import com.hunthawk.reader.domain.device.UAGroupRelationPk;
import com.hunthawk.reader.domain.device.UAInfo;
import com.hunthawk.reader.service.guest.GuestService;

/**
 * @author BruceSun
 * 
 */
public class GuestServiceImpl implements GuestService {

	private HibernateGenericController controller;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hunthawk.reader.service.guest.GuestService#addMobileInfo(com.hunthawk
	 * .reader.domain.device.MobileInfo)
	 */
	public void addMobileInfo(MobileInfo info) {
		controller.save(info);

	}

	public void addOrUpdateMobileInfo(MobileInfo info) {
		if (controller.get(MobileInfo.class, info.getPrefix()) != null) {
			updateMobileInfo(info);
		} else {
			addMobileInfo(info);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hunthawk.reader.service.guest.GuestService#deleteMobileInfo(com.hunthawk
	 * .reader.domain.device.MobileInfo)
	 */
	public void deleteMobileInfo(MobileInfo info) {
		controller.delete(info);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.guest.GuestService#findMobileInfos(int,
	 * int, java.lang.String, boolean, java.util.Collection)
	 */
	public List<MobileInfo> findMobileInfos(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(MobileInfo.class, pageNo, pageSize, orderBy,
				isAsc, expressions);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hunthawk.reader.service.guest.GuestService#getMobileInfos(java.util
	 * .Collection)
	 */
	public Long getMobileInfoCount(Collection<HibernateExpression> expressions) {

		return controller.getResultCount(MobileInfo.class, expressions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hunthawk.reader.service.guest.GuestService#updateMobileInfo(com.hunthawk
	 * .reader.domain.device.MobileInfo)
	 */
	public void updateMobileInfo(MobileInfo info) {
		controller.update(info);

	}

	public void addOrUpdateUaInfo(UAInfo info) {
		if (controller.get(UAInfo.class, info.getUa()) != null) {
			controller.update(info);
		} else {
			controller.save(info);
		}
	}

	public List<UAInfo> findUAInfos(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions) {
		return controller.findBy(UAInfo.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

	public Long getUAInfoCount(Collection<HibernateExpression> expressions) {
		return controller.getResultCount(UAInfo.class, expressions);
	}

	public void deleteUAInfo(UAInfo info) {
		controller.delete(info);
	}

	public void addPersonGroup(PersonGroup group) {
		controller.save(group);
	}

	public void updatePersonGroup(PersonGroup group) {
		controller.update(group);
	}

	public void deletePersonGroup(PersonGroup group) {
		controller.delete(group);
	}

	public List<PersonGroup> getPersonGroups() {
		return controller.getAll(PersonGroup.class, "id", false);
	}

	public void addOrUpdatePersonInfo(PersonInfo person) {
		PersonInfoPK pk = new PersonInfoPK();
		pk.setGroupId(person.getGroupId());
		pk.setMobile(person.getMobile());
		if (controller.get(PersonInfo.class, pk) != null) {
			controller.update(person);
		} else {
			controller.save(person);
		}
	}

	public void deletePersonInfo(PersonInfo person) {
		controller.delete(person);
	}

	public List<PersonInfo> getPersonInfos(Integer groupId) {
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		expressions.add(new CompareExpression("groupId", groupId,
				CompareType.Equal));
		return controller.findBy(PersonInfo.class, 1, Integer.MAX_VALUE,
				expressions);
	}

	public Long getUAGroupCount(Collection<HibernateExpression> expressions) {
		return controller.getResultCount(UAGroup.class, expressions);
	}

	public List<UAGroup> findUAGroupList(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(UAGroup.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

	public void addOrUpdateUaGroup(UAGroup entity) {
		if (entity != null && entity.getId() != null
				&& controller.get(UAGroup.class, entity.getId()) != null) {
			controller.update(entity);
		} else {
			controller.save(entity);
		}

	}

	public void deleteUAGroup(UAGroup entity) {
		List<UAGroupRelation> uaGroupRelationList = controller.findBy(
				UAGroupRelation.class, "groupId", entity.getId());
		for (UAGroupRelation groupRelation : uaGroupRelationList) {
			controller.delete(groupRelation);
		}
		controller.delete(entity);

	}

	public Long getUAGroupRelationCount(
			Collection<HibernateExpression> expressions) {

		return controller.getResultCount(UAGroupRelation.class, expressions);
	}

	public List<UAGroupRelation> findUAGroupRelationList(int pageNo,
			int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(UAGroupRelation.class, pageNo, pageSize,
				orderBy, isAsc, expressions);
	}

	public void addOrUpdateUaGroupRelation(UAGroupRelation entity) {
		UAGroupRelationPk pk = new UAGroupRelationPk();
		pk.setGroupId(entity.getGroupId());
		pk.setUa(entity.getUa());
		if (controller.get(UAGroupRelation.class, pk) != null) {
			controller.update(entity);
		} else {
			controller.save(entity);
		}

	}

	public void deleteUAGroupRelation(UAGroupRelation uaGroupRelation) {
		controller.delete(uaGroupRelation);

	}

	public Object get(Class clazz, Serializable id) {
		return controller.get(clazz, id);
	}

	public Long getUAInfoNotInGroupCount(Integer groupId, String name) {
		String hql = "select count(*) from UAInfo u where not exists( select 'a' from UAGroupRelation gr where gr.ua=u.ua and gr.groupId=?) ";
		if (StringUtils.isNotEmpty(name)) {
			hql += " and u.ua like %'" + name + "'%";
		}

		return controller.getResultCount(hql, groupId);
	}

	public List<UAInfo> findUAInfosNotInGroup(int pageNo, int pageSize,
			Integer id, String name) {
		String hql = "select u from UAInfo u where not exists( select 'a' from UAGroupRelation gr where gr.ua=u.ua and gr.groupId=?) ";
		if (StringUtils.isNotEmpty(name)) {
			hql += " and u.ua like %'" + name + "'%";
		}

		return controller.findBy(hql, pageNo, pageSize, id);
	}

}
