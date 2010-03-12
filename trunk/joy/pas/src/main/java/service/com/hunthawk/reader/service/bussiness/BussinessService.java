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
	@Logable(name = "PackGroupProvinceRelation", action = "add", keyproperty="pid",property = { "pid=��ƷID,aid=ʡ��id,pgid=ҳ����id" })
	public void addPackGroupProvinceRelation(PackGroupProvinceRelation rel)
			throws Exception;

	@Memcached(targetClass = PackGroupProvinceRelation.class, properties = { "pid" })
	@Logable(name = "PackGroupProvinceRelation", action = "delete", keyproperty="pid",property = { "pid=��ƷID,aid=ʡ��id,pgid=ҳ����id" })
	public void deletePackGroupProvinceRelation(PackGroupProvinceRelation rel)
			throws Exception;

	@Memcached(targetClass = PackGroupProvinceRelation.class, properties = { "pid" })
	@Logable(name = "PackGroupProvinceRelation", action = "delete",keyproperty="pid", property = { "pid=��ƷID,aid=ʡ��id" })
	public void deletePackGroupProvinceRelationbyId(PackGroupProvinceId id)
			throws Exception;
	/**
	 * ͨ����ƷID��ѯ��Ʒҳ���������Ϣ
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	public List<PackGroupProvinceRelation> getPackGroupProvinceRelationbyProductId(String productId,Collection  expressions)throws Exception;
	@Memcached(targetClass = PackGroupProvinceRelation.class, properties = { "pid" })
	@Logable(name = "PackGroupProvinceRelation", action = "update",keyproperty="pid", property = { "pid=��ƷID,aid=ʡ��id,pgid=ҳ����id" })
	public void updatePackGroupProvinceRelation(PackGroupProvinceRelation rel)
			throws Exception;

	/**
	 * ͨ����ƷIDɾ����Ʒҳ���������Ϣ
	 * 
	 * @param pid
	 * @throws Exception
	 */
	@Logable(name = "PackGroupProvinceRelation", action = "deleteByProductId",keyproperty="native", property = { "native=��ƷID" })
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
	@Logable(name = "Product", action = "add", property = { "id=��ƷID,name=����,channel.id=����ID,status=״̬,showType=��Ʒ����,isadapter=�Ƿ�̬����,credit=��/խ������,createTime=����ʱ��,creator=������,modifyTime=�޸�ʱ��,modifier=�޸���,users=Ȩ��" })
	public void addProduct(Product product) throws Exception;

	@Restrict(name = "product", action = "edit", args = { Restrict.Position.ARG_1 }, roles = { "productchange" })
	@Memcached(targetClass = Product.class, properties = { "id" }, type = Memcached.Type.SET)
	@Logable(name = "Product", action = "update", property = { "id=��ƷID,name=����,channel.id=����ID,status=״̬,showType=��Ʒ����,isadapter=�Ƿ�̬����,credit=��/խ������,createTime=����ʱ��,creator=������,modifyTime=�޸�ʱ��,modifier=�޸���,users=Ȩ��" })
	public void updateProduct(Product product) throws Exception;

	@Filter(targetClass = Product.class, value = Filter.Position.ARG_5)
	public List<Product> findProducts(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);

	@Filter(targetClass = Product.class, value = Filter.Position.ARG_1)
	public Long getProductCount(Collection<HibernateExpression> expressions);

	@Restrict(name = "product", action = "audit", args = {
			Restrict.Position.ARG_1, Restrict.Position.ARG_2 }, roles = { "productaudit" })
	@Memcached(targetClass = Product.class, properties = { "id" }, type = Memcached.Type.SET)
	@Logable(name = "Product", action = "audit", property = { "id=��ƷID,name=����,channel.id=����ID,status=״̬,showType=��Ʒ����,isadapter=�Ƿ�̬����,credit=��/խ������,createTime=����ʱ��,creator=������,modifyTime=�޸�ʱ��,modifier=�޸���,users=Ȩ��" })
	public void auditProduct(Product product, Integer status) throws Exception;

	@Restrict(name = "pagegroup", action = "audit", args = {
			Restrict.Position.ARG_1, Restrict.Position.ARG_2 }, roles = { "pagegroupaudit" })
	@Memcached(targetClass = PageGroup.class, properties = { "id" }, type = Memcached.Type.SET)
	@Logable(name = "PageGroup", action = "audit", property = { "id=ID,pkName=����,pkComment=����,pkStatus=״̬,showType=��Ʒ����,pkOneTempId=��ҳģ��,pkSecondTempId=��ҳģ��2,pkThirdTempId=��ҳģ��3,resOneTempId=��Դģ��,resSecondTempId=��Դģ��2,resThirdTempId=��Դģ��3,createTime=����ʱ��,creator=������,modifyTime=�޸�ʱ��,modifier=�޸���,users=Ȩ��" })
	public void auditPageGroup(PageGroup product, Integer status)
			throws Exception;

	@Usersable
	@Logable(name = "PageGroup", action = "add", property = { "id=ID,pkName=����,pkComment=����,pkStatus=״̬,showType=��Ʒ����,pkOneTempId=��ҳģ��,pkSecondTempId=��ҳģ��2,pkThirdTempId=��ҳģ��3,resOneTempId=��Դģ��,resSecondTempId=��Դģ��2,resThirdTempId=��Դģ��3,createTime=����ʱ��,creator=������,modifyTime=�޸�ʱ��,modifier=�޸���,users=Ȩ��" })
	public void addPageGroup(PageGroup pagegroup) throws Exception;

	@Restrict(name = "pagegroup", action = "edit", args = { Restrict.Position.ARG_1 }, roles = { "pagegroupchange" })
	@Memcached(targetClass = PageGroup.class, properties = { "id" }, type = Memcached.Type.SET)
	@Logable(name = "PageGroup", action = "update", property = { "id=ID,pkName=����,pkComment=����,pkStatus=״̬,showType=��Ʒ����,pkOneTempId=��ҳģ��,pkSecondTempId=��ҳģ��2,pkThirdTempId=��ҳģ��3,resOneTempId=��Դģ��,resSecondTempId=��Դģ��2,resThirdTempId=��Դģ��3,createTime=����ʱ��,creator=������,modifyTime=�޸�ʱ��,modifier=�޸���,users=Ȩ��" })
	public void updatePageGroup(PageGroup pagegroup) throws Exception;

	@Restrict(name = "common", action = "delete", args = { Restrict.Position.ARG_1 }, roles = { "pagegroupdelete" })
	@Memcached(targetClass = PageGroup.class, properties = { "id" })
	@Logable(name = "PageGroup", action = "delete", property = { "id=ID,pkName=����,pkComment=����,pkStatus=״̬,showType=��Ʒ����,pkOneTempId=��ҳģ��,pkSecondTempId=��ҳģ��2,pkThirdTempId=��ҳģ��3,resOneTempId=��Դģ��,resSecondTempId=��Դģ��2,resThirdTempId=��Դģ��3,createTime=����ʱ��,creator=������,modifyTime=�޸�ʱ��,modifier=�޸���,users=Ȩ��" })
	public void deletePageGroup(PageGroup pagegroup) throws Exception;

	@Filter(targetClass = PageGroup.class, value = Filter.Position.ARG_1)
	public Long getPageGroupCount(Collection<HibernateExpression> expressions);

	public Columns getColumn(int id);
	
	@Usersable
	@Memcached(targetClass = Columns.class, properties = { "id", "!parent#parent.id" })
	@Logable(name = "Columns", action = "add", property = { "id=ID,name=����,comment=����,status=״̬,pricepackId=���۰�ID,colOneTempId=��Ŀģ��,colSecondTempId=��Ŀģ��2,colThirdTempId=��Ŀģ��3,resOneTempId=��Դģ��,resSecondTempId=��Դģ��2,resThirdTempId=��Դģ��3,parent.id=����ĿID,pagegroup.id=ҳ����ID,createTime=����ʱ��,creator=������,modifyTime=�޸�ʱ��,modifier=�޸���,users=Ȩ��" })
	public void addColumn(Columns column) throws Exception;

	@Restrict(name = "common", action = "edit", args = { Restrict.Position.ARG_1 }, roles = { "columnchange" })
	@Memcached(targetClass = Columns.class, properties = { "id", "!parent#parent.id" })
	@Logable(name = "Columns", action = "update", property = { "id=ID,name=����,comment=����,status=״̬,pricepackId=���۰�ID,colOneTempId=��Ŀģ��,colSecondTempId=��Ŀģ��2,colThirdTempId=��Ŀģ��3,resOneTempId=��Դģ��,resSecondTempId=��Դģ��2,resThirdTempId=��Դģ��3,parent.id=����ĿID,pagegroup.id=ҳ����ID,createTime=����ʱ��,creator=������,modifyTime=�޸�ʱ��,modifier=�޸���,users=Ȩ��" })
	public void updateColumn(Columns column) throws Exception;

	@Restrict(name = "common", action = "edit", args = { Restrict.Position.ARG_1 }, roles = { "columnaudit" })
	@Memcached(targetClass = Columns.class, properties = { "id", "!parent#parent.id" })
	@Logable(name = "Columns", action = "audit", property = { "id=ID,name=����,comment=����,status=״̬,pricepackId=���۰�ID,colOneTempId=��Ŀģ��,colSecondTempId=��Ŀģ��2,colThirdTempId=��Ŀģ��3,resOneTempId=��Դģ��,resSecondTempId=��Դģ��2,resThirdTempId=��Դģ��3,parent.id=����ĿID,pagegroup.id=ҳ����ID,createTime=����ʱ��,creator=������,modifyTime=�޸�ʱ��,modifier=�޸���,users=Ȩ��" })
	public void auditColumn(Columns c, Integer status) throws Exception;

	@Filter(targetClass = Columns.class, value = Filter.Position.ARG_5)
	public List<Columns> findColumns(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);

	@Filter(targetClass = Columns.class, value = Filter.Position.ARG_1)
	public Long getColumnCount(Collection<HibernateExpression> expressions);

	/**
	 * ȡ��һ��ҳ��������Ŀ�����������С��������ֵ
	 */
	public Integer getMaxMinOrder(Columns parent,PageGroup pagegroup,String type);
	/***
	 * ȡ�õ�ǰ��Ŀ��ǰ���������Ŀ
	 * @param ����Ŀ
	 * @param ҳ����
	 * @param ��ǰ��Ŀ
	 * @param ����:up,down
	 * @return
	 */
	public Columns getUpDownOrder(Columns parent,PageGroup pagegroup,Columns column,String type);
	
	/**
	 * ��ȡһ��ʵ�����
	 * @param clasz  ʵ�����class
	 * @param id   id
	 * @return
	 */
	public Object getObjectById(Class clasz,Integer id);
}
