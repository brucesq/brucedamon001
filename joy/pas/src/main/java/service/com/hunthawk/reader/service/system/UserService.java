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

	@Logable(name = "UserImpl", action = "delete", keyproperty = "id", property = { "id=ID,name=名字,chName=中文名字,phoneNum=用户电话,email=用户email,provider=合作方,type=用户类型（1运营人员 2 内容合作方 3 渠道合作方）,channel=渠道" })
	public void deleteUser(UserImpl user);

	public List getAll(Class clasz);

	public Object getObject(Class t, Integer id);

	/**
	 * <p>
	 * 更新用户
	 * </p>
	 * 
	 * @param user
	 * @throws Exception
	 *             用户名已经存在
	 */
	@Logable(name = "UserImpl", action = "update", keyproperty = "id", property = { "id=ID,name=名字,chName=中文名字,phoneNum=用户电话,email=用户email,provider=合作方,type=用户类型（1运营人员 2 内容合作方 3 渠道合作方）,channel=渠道" })
	public void updateUser(UserImpl user) throws Exception;

	/**
	 * <p>
	 * 更新角色
	 * </p>
	 * 
	 * @param role
	 * @throws Exception
	 *             角色名已经存在
	 */
	@Logable(name = "Role", action = "update", keyproperty = "id", property = { "id=ID,name=角色名称,cnName=角色中文名称" })
	public void updateRole(Role role) throws Exception;

	/**
	 * <p>
	 * 更新组
	 * </p>
	 * 
	 * @param group
	 * @throws Exception
	 *             组名已经存在
	 */
	@Logable(name = "Group", action = "update", keyproperty = "id", property = { "id=ID,name=用户组名称" })
	public void updateGroup(Group group) throws Exception;

	/**
	 * <p>
	 * 更新权限
	 * </p>
	 * 
	 * @param privilege
	 * @throws Exception
	 *             权限名已经存在
	 */
	@Logable(name = "Privilege", action = "update", keyproperty = "id", property = { "id=ID,name=权限名称,title=权限标题" })
	public void updatePrivilege(Privilege privilege) throws Exception;

	@Logable(name = "UserImpl", action = "add", keyproperty = "id", property = { "id=ID,name=名字,chName=中文名字,phoneNum=用户电话,email=用户email,provider=合作方,type=用户类型（1运营人员 2 内容合作方 3 渠道合作方）,channel=渠道" })
	public void addUser(UserImpl user) throws Exception;

	@Logable(name = "Group", action = "add", keyproperty = "id", property = { "id=ID,name=用户组名称" })
	public void addGroup(Group group) throws Exception;

	@Logable(name = "Role", action = "add", keyproperty = "id", property = { "id=ID,name=角色名称,cnName=角色中文名称" })
	public void addRole(Role role) throws Exception;

	@Logable(name = "Privilege", action = "update", keyproperty = "id", property = { "id=ID,name=权限名称,title=权限标题" })
	public void addPrivilege(Privilege privilege) throws Exception;

	/**
	 * <p>
	 * 删除角色
	 * </p>
	 * 
	 * @param role
	 * @throws Exception
	 *             角色已经被用户使用
	 */
	@Logable(name = "Role", action = "delete", keyproperty = "id", property = { "id=ID,name=角色名称,cnName=角色中文名称" })
	public void deleteRole(Role role) throws Exception;

	/**
	 * <p>
	 * 删除组
	 * </p>
	 * 
	 * @param group
	 * @throws Exception
	 *             组已经被用户使用
	 */
	@Logable(name = "Group", action = "delete", keyproperty = "id", property = { "id=ID,name=用户组名称" })
	public void deleteGroup(Group group) throws Exception;

	@Logable(name = "Privilege", action = "delete", keyproperty = "id", property = { "id=ID,name=权限名称,title=权限标题" })
	public void deletePrivilege(Privilege privilege) throws Exception;

	/**
	 * 统计考核列表的用户ID
	 * @param beginTime
	 * @param endTime
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public List<UserImpl> findUserImplExamineList(Date beginTime, Date endTime,
			int pageNum, int pageSize);

	/**
	 * 统计考核列表的用户ID
	 * @param beginTime
	 * @param endTime
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public Long findUserImplExamineCount(Date beginTime, Date endTime);
}
