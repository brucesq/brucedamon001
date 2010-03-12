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
	 * �������۰�����Դ����
	 */
	@Memcached(targetClass = ResourcePack.class, properties = { "id" })
	@Logable(name = "ResourcePack", action = "delete", property = { "id=ID,name=����,type=����,code=�۸�,choice=ѡ�񼸸�,spid=CPID,creator=������,createTime=����ʱ��" })
	@Restrict(roles = { "packdelete" }, mode = Restrict.Mode.ROLE)
	public void deleteResourcePack(ResourcePack pack) throws Exception;

	@Memcached(targetClass = ResourcePack.class, properties = { "id" })
	@Logable(name = "ResourcePack", action = "add", property = { "id=ID,name=����,type=����,code=�۸�,choice=ѡ�񼸸�,spid=CPID,creator=������,createTime=����ʱ��" })
	public void addPack(ResourcePack pack) throws Exception;

	@Memcached(targetClass = ResourcePack.class, properties = { "id" })
	@Logable(name = "ResourcePack", action = "update", property = { "id=ID,name=����,type=����,code=�۸�,choice=ѡ�񼸸�,spid=CPID,creator=������,createTime=����ʱ��" })
	public void updatePack(ResourcePack pack);

	@SuppressWarnings("unchecked")
	public List findEpack(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);

	@SuppressWarnings("unchecked")
	public Long getEpackCount(Collection<HibernateExpression> expressions);

	public List getAllEpacks();

	public Object getEpack(Integer id);

	/**
	 * ���۰�����Դ�� �Ĺ�ϵ
	 */

	/**
	 * �õ�ĳһ���۰��µ���Դ
	 */
	@SuppressWarnings("unchecked")
	public List<ResourcePackReleation> getResourceFromEpack(int pageNo,
			int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	@Memcached(targetClass = ResourcePackReleation.class, properties = { "id",
			"pack.id#!count" })
	@Logable(name = "ResourcePackReleation", action = "add", property = { "id=ID,pack.id=���۰�ID,resourceId=��ԴID,cpid=CPID,feeId=�Ʒ�ID,choice=�Ʒѵ�,order=����,status=״̬" })
	public void addResourcePackReleation(
			ResourcePackReleation resourcePackRelation) throws Exception;

	public List<ResourceAll> findResourceByHQL(Integer packid,
			Integer authorId, Integer sortid);

	@Memcached(targetClass = ResourcePackReleation.class, properties = { "id",
			"pack.id#!count" })
	@Logable(name = "ResourcePackReleation", action = "delete", property = { "id=ID,pack.id=���۰�ID,resourceId=��ԴID,cpid=CPID,feeId=�Ʒ�ID,choice=�Ʒѵ�,order=����,status=״̬" })
	@Restrict(roles = { "packreleationdelete" }, mode = Restrict.Mode.ROLE)
	public void delResourcePackReleation(
			ResourcePackReleation resourcePackRelation) throws Exception;

	@Memcached(targetClass = ResourcePackReleation.class, properties = { "id",
			"pack.id#!count" })
	@Logable(name = "ResourcePackReleation", action = "update", property = { "id=ID,pack.id=���۰�ID,resourceId=��ԴID,cpid=CPID,feeId=�Ʒ�ID,choice=�Ʒѵ�,order=����,status=״̬" })
	public void updateResourcePackReleation(
			ResourcePackReleation resourcePackRelation) throws Exception;

	/**
	 * �������۰�������������ueb�ļ�
	 * @param resourcePackRelation
	 * @throws Exception
	 */
	@Memcached(targetClass = ResourcePackReleation.class, properties = { "id",
	"pack.id#!count" })
	@Logable(name = "ResourcePackReleation", action = "update", property = { "id=ID,pack.id=���۰�ID,resourceId=��ԴID,cpid=CPID,feeId=�Ʒ�ID,choice=�Ʒѵ�,order=����,status=״̬" })
	public void updateResourcePackReleationNotCreateUEB(
	ResourcePackReleation resourcePackRelation) throws Exception;
	
	/**
	 * ������ ����������ʹ�õķ���
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
	 * ����������Դ��
	 */

	@Memcached(targetClass = ResourcePackReleation.class, properties = { "id",
			"pack.id#!count" })
	@Logable(name = "ResourcePackReleation", action = "add", property = { "id=ID,pack.id=���۰�ID,resourceId=��ԴID,cpid=CPID,feeId=�Ʒ�ID,choice=�Ʒѵ�,order=����,status=״̬" })
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
	 * �������۰�ID�õ� ���µ���Դ��ϵ��Ӧ��
	 * 
	 * @param packId
	 * @return
	 */
	public List<ResourcePackReleation> findResourcePackReleationByPack(
			ResourcePack pack);

	/**
	 * ������Դ�õ������� ���۰��б�
	 * 
	 * @param resourceId
	 * @return
	 */
	public Set<ResourcePack> findResourcePackByResource(String resourceId);
	
	/**
	 * ��ѯĳһ�����۰������������
	 * @param pack  ���۰�
	 * @return
	 */
	public Integer findMaxOrderInPack(ResourcePack pack);
	/**
	 * ��ѯĳһ�����۰�����С�������
	 * @param pack  ���۰�
	 * @return
	 */
	public Integer findMinOrderInPack(ResourcePack pack);
	
	/**
	 * ��ѯĳһ��Դ���ڵ�������Դ�� ��
	 * @param pack
	 * @param releation
	 */
	public ResourcePackReleation findBesideUpReleation(ResourcePack pack,ResourcePackReleation releation);
	
	/**
	 * ��ѯĳһ��Դ���ڵ�������Դ�� ��
	 * @param pack
	 * @param releation
	 */
	public ResourcePackReleation findBesideDownReleation(ResourcePack pack,ResourcePackReleation releation);
	/**
	 * ��ѯĳһ��Դ�� order������ �б� 
	 * @param pack
	 * @param releation
	 */
	public List<ResourcePackReleation> getMaxOrderList(ResourcePack pack,ResourcePackReleation releation);
	/**
	 *  ��ѯĳһ��Դ�� order����С�� �б� 
	 * @param pack
	 * @param releation
	 */
	public List<ResourcePackReleation> getMinOrderList(ResourcePack pack,ResourcePackReleation releation);
	
	public ResourcePackReleation getMaxSubOrder(ResourcePack pack,Integer firstOrder);
	
	public Map<Integer,ResourcePackReleation> getSubOrderList(ResourcePack pack,Integer firstOrder,Integer lastOrder);
	
	public Long getSumSubOrder(ResourcePack pack,Integer firstOrder);
	
	public ResourcePackReleation getIndexReleation(ResourcePack pack,Integer index);
	/**
	 * ������۰��µ���Դ�б�
	 * @param pack
	 * @return
	 */
	//public Map<String,ResourceAll> getResourceByPack(ResourcePack pack,int pageNum, int pageSize,Integer order);
}
