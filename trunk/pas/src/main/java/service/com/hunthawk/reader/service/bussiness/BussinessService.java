/**
 * 
 */
package com.hunthawk.reader.service.bussiness;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.PackGroupProvinceId;
import com.hunthawk.reader.domain.bussiness.PackGroupProvinceRelation;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.enhance.annotation.Filter;
import com.hunthawk.reader.enhance.annotation.Memcached;
import com.hunthawk.reader.enhance.annotation.Usersable;

/**
 * @author BruceSun
 * 
 */
public interface BussinessService {

	@Filter(targetClass = PageGroup.class, value = Filter.Position.ARG_5)
	public List<PageGroup> findPageGroups(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	public Long getPageGroupsCount(Collection<HibernateExpression> expressions);

	@Memcached(targetClass = PackGroupProvinceRelation.class, properties = { "pid" })
	@Logable(name = "PackGroupProvinceRelation", action = "add", keyproperty="pid",property = { "pid=产品ID,aid=省份id,pgid=页面组id" })
	public void addPackGroupProvinceRelation(PackGroupProvinceRelation rel)
			throws Exception;

	@Memcached(targetClass = PackGroupProvinceRelation.class, properties = { "pid" })
	@Logable(name = "PackGroupProvinceRelation", action = "delete", keyproperty="pid",property = { "pid=产品ID,aid=省份id,pgid=页面组id" })
	public void deletePackGroupProvinceRelation(PackGroupProvinceRelation rel)
			throws Exception;

	@Memcached(targetClass = PackGroupProvinceRelation.class, properties = { "pid" })
	@Logable(name = "PackGroupProvinceRelation", action = "delete",keyproperty="pid", property = { "pid=产品ID,aid=省份id" })
	public void deletePackGroupProvinceRelationbyId(PackGroupProvinceId id)
			throws Exception;
	/**
	 * 通过产品ID查询产品页面组关联信息
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	public List<PackGroupProvinceRelation> getPackGroupProvinceRelationbyProductId(String productId,Collection  expressions)throws Exception;
	@Memcached(targetClass = PackGroupProvinceRelation.class, properties = { "pid" })
	@Logable(name = "PackGroupProvinceRelation", action = "update",keyproperty="pid", property = { "pid=产品ID,aid=省份id,pgid=页面组id" })
	public void updatePackGroupProvinceRelation(PackGroupProvinceRelation rel)
			throws Exception;

	/**
	 * 通过产品ID删除产品页面组关联信息
	 * 
	 * @param pid
	 * @throws Exception
	 */
	@Logable(name = "PackGroupProvinceRelation", action = "deleteByProductId",keyproperty="native", property = { "native=产品ID" })
	public void deletePackGroupProvinceRelationbyProductId(String productId)
			throws Exception;

	public List<PackGroupProvinceRelation> findPackGroupProvinceRelations(
			int pageNo, int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	public Long getPackGroupProvinceRelationsCount(
			Collection<HibernateExpression> expressions);

	public PageGroup getPageGroup(int pgid);
	
	public Product getProduct(String productId);

	@Usersable
	@Logable(name = "Product", action = "add", property = { "id=产品ID,name=名称,channel.id=渠道ID,status=状态,showType=产品类型,isadapter=是否动态适配,credit=宽/窄版适配,createTime=创建时间,creator=创建者,modifyTime=修改时间,modifier=修改人,users=权限" })
	public void addProduct(Product product) throws Exception;

	@Restrict(name = "product", action = "edit", args = { Restrict.Position.ARG_1 }, roles = { "productchange" })
	@Memcached(targetClass = Product.class, properties = { "id" }, type = Memcached.Type.SET)
	@Logable(name = "Product", action = "update", property = { "id=产品ID,name=名称,channel.id=渠道ID,status=状态,showType=产品类型,isadapter=是否动态适配,credit=宽/窄版适配,createTime=创建时间,creator=创建者,modifyTime=修改时间,modifier=修改人,users=权限" })
	public void updateProduct(Product product) throws Exception;

	@Filter(targetClass = Product.class, value = Filter.Position.ARG_5)
	public List<Product> findProducts(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);

	@Filter(targetClass = Product.class, value = Filter.Position.ARG_1)
	public Long getProductCount(Collection<HibernateExpression> expressions);

	@Restrict(name = "product", action = "audit", args = {
			Restrict.Position.ARG_1, Restrict.Position.ARG_2 }, roles = { "productaudit" })
	@Memcached(targetClass = Product.class, properties = { "id" }, type = Memcached.Type.SET)
	@Logable(name = "Product", action = "audit", property = { "id=产品ID,name=名称,channel.id=渠道ID,status=状态,showType=产品类型,isadapter=是否动态适配,credit=宽/窄版适配,createTime=创建时间,creator=创建者,modifyTime=修改时间,modifier=修改人,users=权限" })
	public void auditProduct(Product product, Integer status) throws Exception;

	@Restrict(name = "pagegroup", action = "audit", args = {
			Restrict.Position.ARG_1, Restrict.Position.ARG_2 }, roles = { "pagegroupaudit" })
	@Memcached(targetClass = PageGroup.class, properties = { "id" }, type = Memcached.Type.SET)
	@Logable(name = "PageGroup", action = "audit", property = { "id=ID,pkName=名称,pkComment=描述,pkStatus=状态,showType=产品类型,pkOneTempId=首页模板,pkSecondTempId=首页模板2,pkThirdTempId=首页模板3,resOneTempId=资源模板,resSecondTempId=资源模板2,resThirdTempId=资源模板3,createTime=创建时间,creator=创建者,modifyTime=修改时间,modifier=修改人,users=权限" })
	public void auditPageGroup(PageGroup product, Integer status)
			throws Exception;

	@Usersable
	@Logable(name = "PageGroup", action = "add", property = { "id=ID,pkName=名称,pkComment=描述,pkStatus=状态,showType=产品类型,pkOneTempId=首页模板,pkSecondTempId=首页模板2,pkThirdTempId=首页模板3,resOneTempId=资源模板,resSecondTempId=资源模板2,resThirdTempId=资源模板3,createTime=创建时间,creator=创建者,modifyTime=修改时间,modifier=修改人,users=权限" })
	public void addPageGroup(PageGroup pagegroup) throws Exception;

	@Restrict(name = "pagegroup", action = "edit", args = { Restrict.Position.ARG_1 }, roles = { "pagegroupchange" })
	@Memcached(targetClass = PageGroup.class, properties = { "id" }, type = Memcached.Type.SET)
	@Logable(name = "PageGroup", action = "update", property = { "id=ID,pkName=名称,pkComment=描述,pkStatus=状态,showType=产品类型,pkOneTempId=首页模板,pkSecondTempId=首页模板2,pkThirdTempId=首页模板3,resOneTempId=资源模板,resSecondTempId=资源模板2,resThirdTempId=资源模板3,createTime=创建时间,creator=创建者,modifyTime=修改时间,modifier=修改人,users=权限" })
	public void updatePageGroup(PageGroup pagegroup) throws Exception;

	@Restrict(name = "common", action = "delete", args = { Restrict.Position.ARG_1 }, roles = { "pagegroupdelete" })
	@Memcached(targetClass = PageGroup.class, properties = { "id" })
	@Logable(name = "PageGroup", action = "delete", property = { "id=ID,pkName=名称,pkComment=描述,pkStatus=状态,showType=产品类型,pkOneTempId=首页模板,pkSecondTempId=首页模板2,pkThirdTempId=首页模板3,resOneTempId=资源模板,resSecondTempId=资源模板2,resThirdTempId=资源模板3,createTime=创建时间,creator=创建者,modifyTime=修改时间,modifier=修改人,users=权限" })
	public void deletePageGroup(PageGroup pagegroup) throws Exception;

	@Filter(targetClass = PageGroup.class, value = Filter.Position.ARG_1)
	public Long getPageGroupCount(Collection<HibernateExpression> expressions);

	public Columns getColumn(int id);
	
	@Usersable
	@Memcached(targetClass = Columns.class, properties = { "id", "!parent#parent.id" })
	@Logable(name = "Columns", action = "add", property = { "id=ID,name=名称,comment=描述,status=状态,pricepackId=批价包ID,colOneTempId=栏目模板,colSecondTempId=栏目模板2,colThirdTempId=栏目模板3,resOneTempId=资源模板,resSecondTempId=资源模板2,resThirdTempId=资源模板3,parent.id=父栏目ID,pagegroup.id=页面组ID,createTime=创建时间,creator=创建者,modifyTime=修改时间,modifier=修改人,users=权限" })
	public void addColumn(Columns column) throws Exception;

	@Restrict(name = "common", action = "edit", args = { Restrict.Position.ARG_1 }, roles = { "columnchange" })
	@Memcached(targetClass = Columns.class, properties = { "id", "!parent#parent.id" })
	@Logable(name = "Columns", action = "update", property = { "id=ID,name=名称,comment=描述,status=状态,pricepackId=批价包ID,colOneTempId=栏目模板,colSecondTempId=栏目模板2,colThirdTempId=栏目模板3,resOneTempId=资源模板,resSecondTempId=资源模板2,resThirdTempId=资源模板3,parent.id=父栏目ID,pagegroup.id=页面组ID,createTime=创建时间,creator=创建者,modifyTime=修改时间,modifier=修改人,users=权限" })
	public void updateColumn(Columns column) throws Exception;

	@Restrict(name = "common", action = "edit", args = { Restrict.Position.ARG_1 }, roles = { "columnaudit" })
	@Memcached(targetClass = Columns.class, properties = { "id", "!parent#parent.id" })
	@Logable(name = "Columns", action = "audit", property = { "id=ID,name=名称,comment=描述,status=状态,pricepackId=批价包ID,colOneTempId=栏目模板,colSecondTempId=栏目模板2,colThirdTempId=栏目模板3,resOneTempId=资源模板,resSecondTempId=资源模板2,resThirdTempId=资源模板3,parent.id=父栏目ID,pagegroup.id=页面组ID,createTime=创建时间,creator=创建者,modifyTime=修改时间,modifier=修改人,users=权限" })
	public void auditColumn(Columns c, Integer status) throws Exception;

	@Filter(targetClass = Columns.class, value = Filter.Position.ARG_5)
	public List<Columns> findColumns(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);

	@Filter(targetClass = Columns.class, value = Filter.Position.ARG_1)
	public Long getColumnCount(Collection<HibernateExpression> expressions);

	/**
	 * 取得一个页面组下栏目排序中最大最小的那两个值
	 */
	public Integer getMaxMinOrder(Columns parent,PageGroup pagegroup,String type);
	/***
	 * 取得当前栏目的前后的两个栏目
	 * @param 父栏目
	 * @param 页面组
	 * @param 当前栏目
	 * @param 类型:up,down
	 * @return
	 */
	public Columns getUpDownOrder(Columns parent,PageGroup pagegroup,Columns column,String type);
	
	/**
	 * 获取一个实体对象
	 * @param clasz  实体类的class
	 * @param id   id
	 * @return
	 */
	public Object getObjectById(Class clasz,Integer id);
}
