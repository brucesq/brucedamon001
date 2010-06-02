/**
 * 
 */
package com.hunthawk.reader.service.system;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.system.Group;
import com.hunthawk.reader.domain.system.Privilege;
import com.hunthawk.reader.domain.system.Role;
import com.hunthawk.reader.domain.system.UserImpl;

/**
 * @author BruceSun
 * 
 */
public interface UserService {

	@SuppressWarnings("unchecked")
	public List findBy(Class clasz, int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);

	@SuppressWarnings("unchecked")
	public Long getResultCount(Class clasz,
			Collection<HibernateExpression> expressions);

	@Logable(name = "UserImpl", action = "delete", keyproperty = "id", property = { "id=ID,name=����,chName=��������,phoneNum=�û��绰,email=�û�email,provider=������,type=�û����ͣ�1��Ӫ��Ա 2 ���ݺ����� 3 ������������,channel=����" })
	public void deleteUser(UserImpl user);

	public List getAll(Class clasz);

	public Object getObject(Class t, Integer id);

	/**
	 * <p>
	 * �����û�
	 * </p>
	 * 
	 * @param user
	 * @throws Exception
	 *             �û����Ѿ�����
	 */
	@Logable(name = "UserImpl", action = "update", keyproperty = "id", property = { "id=ID,name=����,chName=��������,phoneNum=�û��绰,email=�û�email,provider=������,type=�û����ͣ�1��Ӫ��Ա 2 ���ݺ����� 3 ������������,channel=����" })
	public void updateUser(UserImpl user) throws Exception;

	/**
	 * <p>
	 * ���½�ɫ
	 * </p>
	 * 
	 * @param role
	 * @throws Exception
	 *             ��ɫ���Ѿ�����
	 */
	@Logable(name = "Role", action = "update", keyproperty = "id", property = { "id=ID,name=��ɫ����,cnName=��ɫ��������" })
	public void updateRole(Role role) throws Exception;

	/**
	 * <p>
	 * ������
	 * </p>
	 * 
	 * @param group
	 * @throws Exception
	 *             �����Ѿ�����
	 */
	@Logable(name = "Group", action = "update", keyproperty = "id", property = { "id=ID,name=�û�������" })
	public void updateGroup(Group group) throws Exception;

	/**
	 * <p>
	 * ����Ȩ��
	 * </p>
	 * 
	 * @param privilege
	 * @throws Exception
	 *             Ȩ�����Ѿ�����
	 */
	@Logable(name = "Privilege", action = "update", keyproperty = "id", property = { "id=ID,name=Ȩ������,title=Ȩ�ޱ���" })
	public void updatePrivilege(Privilege privilege) throws Exception;

	@Logable(name = "UserImpl", action = "add", keyproperty = "id", property = { "id=ID,name=����,chName=��������,phoneNum=�û��绰,email=�û�email,provider=������,type=�û����ͣ�1��Ӫ��Ա 2 ���ݺ����� 3 ������������,channel=����" })
	public void addUser(UserImpl user) throws Exception;

	@Logable(name = "Group", action = "add", keyproperty = "id", property = { "id=ID,name=�û�������" })
	public void addGroup(Group group) throws Exception;

	@Logable(name = "Role", action = "add", keyproperty = "id", property = { "id=ID,name=��ɫ����,cnName=��ɫ��������" })
	public void addRole(Role role) throws Exception;

	@Logable(name = "Privilege", action = "update", keyproperty = "id", property = { "id=ID,name=Ȩ������,title=Ȩ�ޱ���" })
	public void addPrivilege(Privilege privilege) throws Exception;

	/**
	 * <p>
	 * ɾ����ɫ
	 * </p>
	 * 
	 * @param role
	 * @throws Exception
	 *             ��ɫ�Ѿ����û�ʹ��
	 */
	@Logable(name = "Role", action = "delete", keyproperty = "id", property = { "id=ID,name=��ɫ����,cnName=��ɫ��������" })
	public void deleteRole(Role role) throws Exception;

	/**
	 * <p>
	 * ɾ����
	 * </p>
	 * 
	 * @param group
	 * @throws Exception
	 *             ���Ѿ����û�ʹ��
	 */
	@Logable(name = "Group", action = "delete", keyproperty = "id", property = { "id=ID,name=�û�������" })
	public void deleteGroup(Group group) throws Exception;

	@Logable(name = "Privilege", action = "delete", keyproperty = "id", property = { "id=ID,name=Ȩ������,title=Ȩ�ޱ���" })
	public void deletePrivilege(Privilege privilege) throws Exception;

	/**
	 * ͳ�ƿ����б���û�ID
	 * @param beginTime
	 * @param endTime
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public List<UserImpl> findUserImplExamineList(Date beginTime, Date endTime,
			int pageNum, int pageSize);

	/**
	 * ͳ�ƿ����б���û�ID
	 * @param beginTime
	 * @param endTime
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public Long findUserImplExamineCount(Date beginTime, Date endTime);
}
