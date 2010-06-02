/**
 * 
 */
package com.hunthawk.reader.service.guest;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.device.MobileInfo;
import com.hunthawk.reader.domain.device.PersonGroup;
import com.hunthawk.reader.domain.device.PersonInfo;
import com.hunthawk.reader.domain.device.UAGroup;
import com.hunthawk.reader.domain.device.UAGroupRelation;
import com.hunthawk.reader.domain.device.UAInfo;
import com.hunthawk.reader.enhance.annotation.Memcached;

/**
 * ������Ϣ����,�ŶΡ�UA����
 * 
 * @author BruceSun
 * 
 */
public interface GuestService {

	/**
	 * ���ºŶ�
	 * 
	 * @param info
	 */
	public void updateMobileInfo(MobileInfo info);

	public void addMobileInfo(MobileInfo info);

	@Memcached(targetClass = MobileInfo.class, properties = { "prefix" }, type = Memcached.Type.SET)
	public void addOrUpdateMobileInfo(MobileInfo info);

	public List<MobileInfo> findMobileInfos(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	public Long getMobileInfoCount(Collection<HibernateExpression> expressions);

	@Memcached(targetClass = MobileInfo.class, properties = { "prefix" })
	@Restrict(mode = Restrict.Mode.ROLE, roles = { "mobileinfochange" })
	public void deleteMobileInfo(MobileInfo info);

	@Memcached(targetClass = UAInfo.class, properties = { "ua" }, type = Memcached.Type.SET)
	@Logable(name = "UAInfo", action = "add", keyproperty = "ua", property = { "ua=UA,width=��,height=��,videoTypes=��Ƶ֧������,ringTypes=����֧������,wapType=wap�汾,screenType=��խ��,createTime=����ʱ��" })
	public void addOrUpdateUaInfo(UAInfo info);

	@Memcached(targetClass = UAGroupRelation.class, properties = { "!UaGroupIds" })
	public void addOrUpdateUaGroup(UAGroup entity);

	@Memcached(targetClass = UAGroupRelation.class, properties = { "!UaGroupIds" })
	public void addOrUpdateUaGroupRelation(UAGroupRelation entity);

	/**
	 * ����UA��Ϣ
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param orderBy
	 * @param isAsc
	 * @param expressions
	 * @return
	 */
	public List<UAInfo> findUAInfos(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);

	public List<UAInfo> findUAInfosNotInGroup(int pageNo, int pageSize,
			Integer id, String name);

	public List<UAGroup> findUAGroupList(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	public List<UAGroupRelation> findUAGroupRelationList(int pageNo,
			int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	public Long getUAInfoCount(Collection<HibernateExpression> expressions);

	public Long getUAInfoNotInGroupCount(Integer groupId, String name);

	public Long getUAGroupCount(Collection<HibernateExpression> expressions);

	public Long getUAGroupRelationCount(
			Collection<HibernateExpression> expressions);

	@Memcached(targetClass = UAInfo.class, properties = { "ua" })
	@Restrict(mode = Restrict.Mode.ROLE, roles = { "uainfochange" })
	@Logable(name = "UAInfo", action = "delete", keyproperty = "ua", property = { "ua=UA,width=��,height=��,videoTypes=��Ƶ֧������,ringTypes=����֧������,wapType=wap�汾,screenType=��խ��,createTime=����ʱ��" })
	public void deleteUAInfo(UAInfo info);

	@Memcached(targetClass = PersonGroup.class, properties = { "!all" })
	@Logable(name = "PersonGroup", action = "add", property = { "id=ID,name=����,desc=����" })
	public void addPersonGroup(PersonGroup group);

	@Memcached(targetClass = PersonGroup.class, properties = { "!all" })
	@Logable(name = "PersonGroup", action = "update", property = { "id=ID,name=����,desc=����" })
	public void updatePersonGroup(PersonGroup group);

	@Memcached(targetClass = PersonGroup.class, properties = { "!all" })
	@Restrict(mode = Restrict.Mode.ROLE, roles = { "personchange" })
	@Logable(name = "PersonGroup", action = "delete", property = { "id=ID,name=����,desc=����" })
	public void deletePersonGroup(PersonGroup group);

	public List<PersonGroup> getPersonGroups();

	@Memcached(targetClass = PersonInfo.class, properties = { "groupId" })
	@Logable(name = "PersonInfo", action = "add", keyproperty = "mobile", property = { "mobile=�ֻ�����,comment=����,groupId=��������ID,creator=������ID,createTime=����ʱ��" })
	public void addOrUpdatePersonInfo(PersonInfo person);

	@Memcached(targetClass = PersonInfo.class, properties = { "groupId" })
	@Restrict(mode = Restrict.Mode.ROLE, roles = { "personchange" })
	@Logable(name = "PersonInfo", action = "delete", keyproperty = "mobile", property = { "mobile=�ֻ�����,comment=����,groupId=��������ID,creator=������ID,createTime=����ʱ��" })
	public void deletePersonInfo(PersonInfo person);

	public List<PersonInfo> getPersonInfos(Integer groupId);

	@Memcached(targetClass = UAGroupRelation.class, properties = { "!UaGroupIds" })
	public void deleteUAGroup(UAGroup entity);

	@Memcached(targetClass = UAGroupRelation.class, properties = { "!UaGroupIds" })
	public void deleteUAGroupRelation(UAGroupRelation uaGroupRelation);

	public Object get(Class clazz, Serializable id);
}
