/**
 * 
 */
package com.hunthawk.reader.pps.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.memcached.NullObject;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.custom.UserInfo;
import com.hunthawk.reader.domain.device.MobileInfo;
import com.hunthawk.reader.domain.device.PersonGroup;
import com.hunthawk.reader.domain.device.PersonInfo;
import com.hunthawk.reader.domain.device.UAGroupRelation;
import com.hunthawk.reader.domain.device.UAInfo;
import com.hunthawk.reader.pps.service.GuestService;

/**
 * @author BruceSun
 * 
 */
public class GuestServiceImpl implements GuestService {

	private static Logger logger = Logger.getLogger(GuestServiceImpl.class);

	private HibernateGenericController controller;

	private MemCachedClientWrapper memcached;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}

	public MobileInfo getMobileInfo(String mobile) {
		if (mobile.length() > 7) {
			mobile = mobile.substring(0, 7);
		}
		// ��CMS��̨�����Ŷ���Ϣ����
		Object info = null;
		String key = Utility.getMemcachedKey(MobileInfo.class, mobile);
		try {
			info = memcached.getAndSaveLocalLong(key);
		} catch (Exception e) {
			logger.error("�ӻ����л�ȡ�Ŷ���Ϣ����", e);
		}
		if (info == null) {
			info = controller.get(MobileInfo.class, mobile);
			if (info == null) {
				info = new NullObject();
			}
			memcached.setAndSaveLong(key, info,
					48 * MemCachedClientWrapper.HOUR);
		}
		if (info instanceof NullObject) {
			return null;
		}
		return (MobileInfo) info;
	}

	public UAInfo getUAInfo(String ua) {
		String key = Utility.getMemcachedKey(UAInfo.class, ua);
		Object uainfo = null;
		try {
			uainfo = memcached.getAndSaveLocalLong(key);
		} catch (Exception e) {
			logger.error("�ӻ����л�ȡUA��Ϣ����", e);
		}
		if (uainfo == null) {
			uainfo = controller.get(UAInfo.class, ua);
			if (uainfo == null) {
				uainfo = new NullObject();
			}
			memcached.setAndSaveLong(key, uainfo,
					48 * MemCachedClientWrapper.HOUR);
		}
		if (uainfo instanceof NullObject) {
			return null;
		}
		return (UAInfo) uainfo;

	}

	public List<PersonInfo> getPersonInfo(String mobile) {
		// ��CMS��̨��������������
		String groupKey = Utility.getMemcachedKey(PersonGroup.class, "all");
		List<PersonGroup> groups = null;
		try {
			groups = (List<PersonGroup>) memcached
					.getAndSaveLocalLong(groupKey);
		} catch (Exception e) {
			logger.error("�ӻ����л�ȡ�����������", e);
		}
		if (groups == null) {
			groups = controller.getAll(PersonGroup.class);
			memcached.setAndSaveLong(groupKey, groups,
					48 * MemCachedClientWrapper.HOUR);
		}
		List<PersonInfo> persons = new ArrayList<PersonInfo>();
		for (PersonGroup group : groups) {
			// ��CMS��̨��������������
			String key = Utility.getMemcachedKey(PersonInfo.class, String
					.valueOf(group.getId()));
			HashMap<String, PersonInfo> map = null;
			try {
				map = (HashMap<String, PersonInfo>) memcached
						.getAndSaveLocalLong(key);
			} catch (Exception e) {
				logger.error("�ӻ����л�ȡ����������", e);
			}
			if (map == null) {
				map = new HashMap<String, PersonInfo>();
				List<PersonInfo> infos = controller.findBy(PersonInfo.class,
						"groupId", group.getId());
				for (PersonInfo info : infos) {
					map.put(info.getMobile(), info);
				}
				memcached.setAndSaveLong(key, map,
						48 * MemCachedClientWrapper.HOUR);
			}
			PersonInfo person = map.get(mobile);
			if (person != null) {
				persons.add(person);
			}
		}
		return persons;
	}

	public <T> T get(Class<T> clazz, Serializable id) {
		return controller.get(clazz, id);
	}

	/**
	 * ����UA����UA���ID�б�
	 */
	public List<Integer> getUaGroupIdsByUa(String ua) {
		String uaGroupIdsKey = Utility.getMemcachedKey(UAGroupRelation.class,
				"UaGroupIds");
		Set<Integer> set = new HashSet<Integer>();
		List<UAGroupRelation> temp = null;
		try {
			temp = (List<UAGroupRelation>) memcached
					.getAndSaveLocalLong(uaGroupIdsKey);
		} catch (Exception e) {
			logger.error("��ȡʧ��UA��id�б�ʧ��", e);
		}

		if (temp == null) {
			String hql = "select u from UAGroupRelation u";
			temp = (List<UAGroupRelation>) controller.findBy(hql);

			memcached.setAndSaveLong(uaGroupIdsKey, temp,
					48 * MemCachedClientWrapper.HOUR);
		}
		for (UAGroupRelation groupRelation : temp) {
			if (ua.equalsIgnoreCase(groupRelation.getUa())) {
				set.add(groupRelation.getGroupId());
			}

		}

		return new ArrayList<Integer>(set);
	}
	
	private Long getLastUserId(){
		List<UserInfo> infos = controller.findBy(UserInfo.class, 1, 1, "mobile", false);
		if(infos.size()>0){
			return Long.parseLong(infos.get(0).getMobile());
		}
		return 20000000000L;
	}
	
	public String registerNewMobile(String name,String passwd){
		String mobile = "10000000000";
		String key = Utility.getMemcachedKey(UserInfo.class, "newmobile");
		Long count = 0L;
		try {
			count = memcached.incr(key);
		} catch (Exception e) {
			logger.error("��Memcached�л�ȡ���û�ID����!", e);
		}
		if (count <= 0) {
			count = getLastUserId();
			memcached.storeCounter(key, ++count);
		}
		if(count > 0){
			mobile = count.toString();
			UserInfo info = new UserInfo();
			info.setMobile(mobile);
			info.setLoginname(name);
			info.setPassword(passwd);
			info.setNickname("�û�"+mobile.substring(mobile.length()-4));
			info.setSex(2);
			info.setRegistered(0);
			controller.save(info);
		}
		return mobile;
	}
	public void registerNewMobile(String mobile,String name,String passwd){
		UserInfo info = controller.get(UserInfo.class, mobile);
		if(info == null){
			info = new UserInfo();
			info.setMobile(mobile);
			info.setLoginname(name);
			info.setPassword(passwd);
			info.setNickname("�û�"+mobile.substring(mobile.length()-4));
			info.setSex(2);
			info.setRegistered(0);
			controller.save(info);
		}else{
			info.setLoginname(name);
			info.setPassword(passwd);
			controller.update(info);
		}
		
	}
	
	public UserInfo getUserInfo(String mobile){
		String key = Utility.getMemcachedKey(UserInfo.class, mobile);
		Object info = null;
		try {
			info = memcached.getAndSaveLocal(key);
		} catch (Exception e) {
			logger.error("�ӻ����л�ȡ�û���Ϣ����", e);
		}
		if (info == null) {
			info = controller.get(UserInfo.class, mobile);
			if (info == null) {
				info = new NullObject();
			}
			memcached.setAndSaveLocal(key, info,
					48 * MemCachedClientWrapper.HOUR);
		}
		if (info instanceof NullObject) {
			return null;
		}
		return (UserInfo) info;
	}
	
	public UserInfo getUserInfoByName(String name){
		String key = Utility.getMemcachedKey(UserInfo.class,"name", name);
		Object info = null;
		try {
			info = memcached.getAndSaveLocal(key);
		} catch (Exception e) {
			logger.error("�ӻ����л�ȡ�û���Ϣ����", e);
		}
		if (info == null) {
			
			List<UserInfo> infos = controller.findBy(UserInfo.class,"loginname", name);
			if(infos.size()>0){
				info = infos.get(0);
			}
			if (info == null) {
				info = new NullObject();
			}
			memcached.setAndSaveLocal(key, info,
					48 * MemCachedClientWrapper.HOUR);
		}
		if (info instanceof NullObject) {
			return null;
		}
		return (UserInfo) info;
	}
	
	private void crearUserCache(UserInfo info){
		String namekey = Utility.getMemcachedKey(UserInfo.class,"name", info.getLoginname());
		String mobilekey = Utility.getMemcachedKey(UserInfo.class, info.getMobile());
		memcached.deleteForLocal(namekey);
		memcached.deleteForLocal(mobilekey);
	}
	
	public void updateUserInfo(UserInfo info){
		controller.update(info);
		crearUserCache(info);
	}
	
	public boolean isNameExists(String name){
		UserInfo info = getUserInfoByName(name);
		if(info != null){
			return true;
		}else{
			return false;
		}
	}
	


	
	public static void main(String[] args){
		String mobile = "13322321432";
		System.out.println(mobile.substring(7, 11));
	}
}
