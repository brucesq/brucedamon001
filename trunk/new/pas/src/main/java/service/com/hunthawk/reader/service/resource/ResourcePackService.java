package com.hunthawk.reader.service.resource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.enhance.annotation.Memcached;

public interface ResourcePackService {
	/**
	 * 处理批价包（资源包）
	 */
	@Memcached(targetClass = ResourcePack.class, properties = { "id" })
	@Logable(name = "ResourcePack", action = "delete", property = { "id=ID,name=名称,type=类型,code=价格,choice=选择几个,spid=CPID,creator=创建人,createTime=创建时间" })
	@Restrict(roles = { "packdelete" }, mode = Restrict.Mode.ROLE)
	public void deleteResourcePack(ResourcePack pack) throws Exception;

	@Memcached(targetClass = ResourcePack.class, properties = { "id" })
	@Logable(name = "ResourcePack", action = "add", property = { "id=ID,name=名称,type=类型,code=价格,choice=选择几个,spid=CPID,creator=创建人,createTime=创建时间" })
	public void addPack(ResourcePack pack) throws Exception;

	@Memcached(targetClass = ResourcePack.class, properties = { "id" })
	@Logable(name = "ResourcePack", action = "update", property = { "id=ID,name=名称,type=类型,code=价格,choice=选择几个,spid=CPID,creator=创建人,createTime=创建时间" })
	public void updatePack(ResourcePack pack);

	@SuppressWarnings("unchecked")
	public List findEpack(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);

	@SuppressWarnings("unchecked")
	public Long getEpackCount(Collection<HibernateExpression> expressions);

	public List getAllEpacks();

	public Object getEpack(Integer id);

	/**
	 * 批价包与资源包 的关系
	 */

	/**
	 * 得到某一批价包下的资源
	 */
	@SuppressWarnings("unchecked")
	public List<ResourcePackReleation> getResourceFromEpack(int pageNo,
			int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	@Memcached(targetClass = ResourcePackReleation.class, properties = { "id",
			"pack.id#!count" })
	@Logable(name = "ResourcePackReleation", action = "add", property = { "id=ID,pack.id=批价包ID,resourceId=资源ID,cpid=CPID,feeId=计费ID,choice=计费点,order=排序,status=状态" })
	public void addResourcePackReleation(
			ResourcePackReleation resourcePackRelation) throws Exception;

	public List<ResourceAll> findResourceByHQL(Integer packid,
			Integer authorId, Integer sortid);

	@Memcached(targetClass = ResourcePackReleation.class, properties = { "id",
			"pack.id#!count" })
	@Logable(name = "ResourcePackReleation", action = "delete", property = { "id=ID,pack.id=批价包ID,resourceId=资源ID,cpid=CPID,feeId=计费ID,choice=计费点,order=排序,status=状态" })
	@Restrict(roles = { "packreleationdelete" }, mode = Restrict.Mode.ROLE)
	public void delResourcePackReleation(
			ResourcePackReleation resourcePackRelation) throws Exception;

	@Memcached(targetClass = ResourcePackReleation.class, properties = { "id",
			"pack.id#!count" })
	@Logable(name = "ResourcePackReleation", action = "update", property = { "id=ID,pack.id=批价包ID,resourceId=资源ID,cpid=CPID,feeId=计费ID,choice=计费点,order=排序,status=状态" })
	public void updateResourcePackReleation(
			ResourcePackReleation resourcePackRelation) throws Exception;

	/**
	 * 更新批价包，不重新生产ueb文件
	 * @param resourcePackRelation
	 * @throws Exception
	 */
	@Memcached(targetClass = ResourcePackReleation.class, properties = { "id",
	"pack.id#!count" })
	@Logable(name = "ResourcePackReleation", action = "update", property = { "id=ID,pack.id=批价包ID,resourceId=资源ID,cpid=CPID,feeId=计费ID,choice=计费点,order=排序,status=状态" })
	public void updateResourcePackReleationNotCreateUEB(
	ResourcePackReleation resourcePackRelation) throws Exception;
	
	/**
	 * 仅仅是 供批量工具使用的方法
	 * @param resourcePackRelation
	 * @throws Exception
	 */
	@Memcached(targetClass = ResourcePackReleation.class, properties = { "id",
	"pack.id#!count" })
	public void updatePackReleationNotCreateUEBAndLog(
	ResourcePackReleation resourcePackRelation) throws Exception;
	
	@SuppressWarnings("unchecked")
	public Long getResourcePackReleationCount(
			Collection<HibernateExpression> expressions);

	public List getResourcePackReleations();

	public Object getResourcePackReleation(Integer id);

	/***************************************************************************
	 * 批量加入资源包
	 */

	@Memcached(targetClass = ResourcePackReleation.class, properties = { "id",
			"pack.id#!count" })
	@Logable(name = "ResourcePackReleation", action = "add", property = { "id=ID,pack.id=批价包ID,resourceId=资源ID,cpid=CPID,feeId=计费ID,choice=计费点,order=排序,status=状态" })
	public String addResourcePackReleationMessgae(
			ResourcePackReleation resourcePackRelation, ResourceAll resourceAll);

	public Long getResourcePackReleationResultCountByHQL(Integer resourceType,boolean tof,String name,
			ResourcePack pack, Integer authorId, Integer childTypeId,
			Integer resourceStatus, Integer cpID, String feeID);

	public List<ResourcePackReleation> findResourcePackReleationByHQL(Integer resourceType,boolean tof,
			String name, ResourcePack pack, Integer authorId,
			Integer childTypeId, Integer resourceStatus, Integer cpID,
			String feeID, int pageNum, int pageSize,Integer order);

	/***
	 * 根据批价包ID得到 其下的资源关系对应表
	 * 
	 * @param packId
	 * @return
	 */
	public List<ResourcePackReleation> findResourcePackReleationByPack(
			ResourcePack pack);

	/**
	 * 根据资源得到归属的 批价包列表
	 * 
	 * @param resourceId
	 * @return
	 */
	public Set<ResourcePack> findResourcePackByResource(String resourceId);
	
	/**
	 * 查询某一个批价包下最大的排序号
	 * @param pack  批价包
	 * @return
	 */
	public Integer findMaxOrderInPack(ResourcePack pack);
	/**
	 * 查询某一个批价包下最小的排序号
	 * @param pack  批价包
	 * @return
	 */
	public Integer findMinOrderInPack(ResourcePack pack);
	
	/**
	 * 查询某一资源相邻的两个资源的 上
	 * @param pack
	 * @param releation
	 */
	public ResourcePackReleation findBesideUpReleation(ResourcePack pack,ResourcePackReleation releation);
	
	/**
	 * 查询某一资源相邻的两个资源的 下
	 * @param pack
	 * @param releation
	 */
	public ResourcePackReleation findBesideDownReleation(ResourcePack pack,ResourcePackReleation releation);
	/**
	 * 查询某一资源下 order排序大的 列表 
	 * @param pack
	 * @param releation
	 */
	public List<ResourcePackReleation> getMaxOrderList(ResourcePack pack,ResourcePackReleation releation);
	/**
	 *  查询某一资源下 order排序小的 列表 
	 * @param pack
	 * @param releation
	 */
	public List<ResourcePackReleation> getMinOrderList(ResourcePack pack,ResourcePackReleation releation);
	
	public ResourcePackReleation getMaxSubOrder(ResourcePack pack,Integer firstOrder);
	
	public Map<Integer,ResourcePackReleation> getSubOrderList(ResourcePack pack,Integer firstOrder,Integer lastOrder);
	
	public Long getSumSubOrder(ResourcePack pack,Integer firstOrder);
	
	public ResourcePackReleation getIndexReleation(ResourcePack pack,Integer index);
	/**
	 * 获得批价包下的资源列表
	 * @param pack
	 * @return
	 */
	//public Map<String,ResourceAll> getResourceByPack(ResourcePack pack,int pageNum, int pageSize,Integer order);
}
