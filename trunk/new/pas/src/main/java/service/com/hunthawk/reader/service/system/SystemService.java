/**
 * 
 */
package com.hunthawk.reader.service.system;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.system.Log;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.enhance.annotation.Memcached;

/**
 * @author BruceSun
 * 
 */
public interface SystemService {

	@SuppressWarnings("unchecked")
	public List findVariablesBy(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);

	@SuppressWarnings("unchecked")
	public Long getVariablesResultCount(
			Collection<HibernateExpression> expressions);

	@Memcached(targetClass = Variables.class, properties = { "name" }, type = Memcached.Type.SET)
	@Logable(name = "Variables", action = "update", property = { "id=ID,name=全局属性名称,value=属性值,description=备注、描述" })
	public void updateVariables(Variables object);

	@Memcached(targetClass = Variables.class, properties = { "name" }, type = Memcached.Type.SET)
	@Logable(name = "Variables", action = "add", property = { "id=ID,name=全局属性名称,value=属性值,description=备注、描述" })
	public void addVariables(Variables object);

	@Memcached(targetClass = Variables.class, properties = { "name" }, type = Memcached.Type.DELETE)
	@Logable(name = "Variables", action = "delete", property = { "id=ID,name=全局属性名称,value=属性值,description=备注、描述" })
	public void deleteVariables(Variables object);

	public Variables getVariables(String name);

	public List<Log> findLogBy(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);

	public Long getLogResultCount(Collection<HibernateExpression> expressions);

	public void addLog(Log log);

	public Long findExamineActionByCount(Integer userId, String action);

	public Long findExamineResourceCount(Date beginTime, Date endTime);
	
	
	public List<Log> findExamineResourceList(Date beginTime, Date endTime,
			int pageNum, int pageSize);
}
