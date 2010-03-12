package com.hunthawk.reader.service.resource;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.request.IUploadFile;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.MagazineChapter;
import com.hunthawk.reader.domain.resource.NewsPapersChapter;
import com.hunthawk.reader.domain.resource.ReCheck;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourceReferen;
import com.hunthawk.reader.domain.resource.ResourceResType;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.enhance.annotation.Memcached;

/**
 * @author xianlaichen
 * 
 */

public interface ResourceService {

	/**
	 * ��ȡ��Դ
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param orderBy
	 * @param isAsc
	 * @param expressions
	 * @return
	 */
	public List<ResourceAll> findResourceBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	/**
	 * ��ȡ��Դ����
	 * 
	 * @param expressions
	 * @return
	 */
	public Long getResourceResultCount(
			Collection<HibernateExpression> expressions);

	/**
	 * ����������Ϣ
	 */
	@Memcached(targetClass = ResourceAuthor.class, properties = { "id" })
	@Logable(name = "ResourceAuthor", action = "add", property = { "id=ID,name=����,initialLetter=����ĸ,penName=���߱���,sex=�Ա�,area=����,intro=���߼��,status=����״̬,creatorId=������,createTime=����ʱ��" })
	public void addResourceAuthor(ResourceAuthor resourceauthor)
			throws Exception;

	@Memcached(targetClass = ResourceAuthor.class, properties = { "id" })
	@Logable(name = "ResourceAuthor", action = "update", property = { "id=ID,name=����,initialLetter=����ĸ,penName=���߱���,sex=�Ա�,area=����,intro=���߼��,status=����״̬,creatorId=������,createTime=����ʱ��" })
	public void updateResourceAuthor(ResourceAuthor resourceauthor);

	@Memcached(targetClass = ResourceAuthor.class, properties = { "id" })
	@Logable(name = "ResourceAuthor", action = "delete", property = { "id=ID,name=����,initialLetter=����ĸ,penName=���߱���,sex=�Ա�,area=����,intro=���߼��,status=����״̬,creatorId=������,createTime=����ʱ��" })
	public void deleteResourceAuthor(ResourceAuthor resourceauthor);

	/**
	 * �����������ƻ������ID
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public ResourceAuthor getResourceAuthorByName(String name) throws Exception;

	/**
	 * ��������ID��ȡ���߶���
	 * 
	 * @param id
	 * @return
	 */
	public ResourceAuthor getResourceAuthorById(Integer id);

	/**
	 * ��ȡ����������Ϣ
	 * 
	 * @return
	 */
	public List<ResourceAuthor> getAllResourceAuthor();

	public List<ResourceAuthor> findResourceAuthorBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	/**
	 * �ӻ�����ȡ�������б�
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param orderBy
	 * @param isAsc
	 * @param expressions
	 * @return
	 */
	public List<ResourceAuthor> findResourceAuthorBymemcached(String keys,
			int pageNo, int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	public Long getResourceAuthorResultCount(
			Collection<HibernateExpression> expressions);

	/**
	 * �����Ȩ��Ϣ
	 */
	@Logable(name = "ResourceReferen", action = "add", property = { "id=ID,name=��Ȩ������,contactName=��Ȩ��ϵ������,contactPhone=��Ȩ��ϵ�˵绰,email=��Ȩ��ϵ������,address=��Ȩ��ϵ�˵�ַ,fax=��Ȩ��ϵ�˴���,beginTime=��Ч����,endTime=ʧЧ����,status=��Ȩ״̬,cpId=CP���е�ID,identifier=��Ȩ��ʶ,"
			+ "providerInfo=��Ȩ��,cooperatePro=����Э��,authorName=������Ϣ��,copyrightCheck=��Ȩ�Ǽ�֤��,productInfo=��Ʒ��Ȩ״��˵����,mcpinfo=MCP��Ȩ�Բ����˵����,promises=MCP��Ȩ�������ŵ��,copyrightUse=����Ȩ���ʹ��Э��,copyrightAttorn=����Ȩת��Э��,copyrightOther=����,modifierId=�޸���,modifyTime=�޸�ʱ��" })
	public void addResourceReferen(ResourceReferen resourcereferen)
			throws Exception;

	@Logable(name = "ResourceReferen", action = "update", property = { "id=ID,name=��Ȩ������,contactName=��Ȩ��ϵ������,contactPhone=��Ȩ��ϵ�˵绰,email=��Ȩ��ϵ������,address=��Ȩ��ϵ�˵�ַ,fax=��Ȩ��ϵ�˴���,beginTime=��Ч����,endTime=ʧЧ����,status=��Ȩ״̬,cpId=CP���е�ID,identifier=��Ȩ��ʶ,"
			+ "providerInfo=��Ȩ��,cooperatePro=����Э��,authorName=������Ϣ��,copyrightCheck=��Ȩ�Ǽ�֤��,productInfo=��Ʒ��Ȩ״��˵����,mcpinfo=MCP��Ȩ�Բ����˵����,promises=MCP��Ȩ�������ŵ��,copyrightUse=����Ȩ���ʹ��Э��,copyrightAttorn=����Ȩת��Э��,copyrightOther=����,modifierId=�޸���,modifyTime=�޸�ʱ��" })
	public void updateResourceReferen(ResourceReferen resourcereferen);

	@Logable(name = "ResourceReferen", action = "delete", property = { "id=ID,name=��Ȩ������,contactName=��Ȩ��ϵ������,contactPhone=��Ȩ��ϵ�˵绰,email=��Ȩ��ϵ������,address=��Ȩ��ϵ�˵�ַ,fax=��Ȩ��ϵ�˴���,beginTime=��Ч����,endTime=ʧЧ����,status=��Ȩ״̬,cpId=CP���е�ID,identifier=��Ȩ��ʶ,"
			+ "providerInfo=��Ȩ��,cooperatePro=����Э��,authorName=������Ϣ��,copyrightCheck=��Ȩ�Ǽ�֤��,productInfo=��Ʒ��Ȩ״��˵����,mcpinfo=MCP��Ȩ�Բ����˵����,promises=MCP��Ȩ�������ŵ��,copyrightUse=����Ȩ���ʹ��Э��,copyrightAttorn=����Ȩת��Э��,copyrightOther=����,modifierId=�޸���,modifyTime=�޸�ʱ��" })
	@Restrict(roles = { "copyrightaudit" }, mode = Restrict.Mode.ROLE)
	public void deleteResourceReferen(ResourceReferen resourcereferen);

	public ResourceReferen getResourceReferen(int id);

	public List<ResourceReferen> getAllResourceReferen();

	public List<ResourceReferen> findResourceReferenBy(int pageNo,
			int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	public Long getResourceReferenResultCount(
			Collection<HibernateExpression> expressions);

	public List<ResourceReferen> getResourceReferenByCPID(Integer CPID);

	/**
	 * ��Ȩ���
	 * 
	 * @param resourceReferen
	 * @param status
	 * @throws Exception
	 */
	@Logable(name = "ResourceReferen", action = "audit", property = { "id=ID,name=��Ȩ������,contactName=��Ȩ��ϵ������,contactPhone=��Ȩ��ϵ�˵绰,email=��Ȩ��ϵ������,address=��Ȩ��ϵ�˵�ַ,fax=��Ȩ��ϵ�˴���,beginTime=��Ч����,endTime=ʧЧ����,status=��Ȩ״̬,cpId=CP���е�ID,identifier=��Ȩ��ʶ,"
			+ "providerInfo=��Ȩ��,cooperatePro=����Э��,authorName=������Ϣ��,copyrightCheck=��Ȩ�Ǽ�֤��,productInfo=��Ʒ��Ȩ״��˵����,mcpinfo=MCP��Ȩ�Բ����˵����,promises=MCP��Ȩ�������ŵ��,copyrightUse=����Ȩ���ʹ��Э��,copyrightAttorn=����Ȩת��Э��,copyrightOther=����,modifierId=�޸���,modifyTime=�޸�ʱ��" })
	@Restrict(roles = { "copyrightaudit" }, mode = Restrict.Mode.ROLE)
	public void auditResourceReferen(ResourceReferen resourceReferen,
			Integer status) throws Exception;

	/**
	 * ������Դ����
	 */
	@Memcached(targetClass = ResourceType.class, properties = { "id" })
	@Logable(name = "ResourceType", action = "add", property = { "id=ID,name=��������,showType=��������,parent.id=�ϼ�����ID,creatorId=������,createTime=����ʱ��" })
	public void addResourceType(ResourceType resourcetype) throws Exception;

	@Memcached(targetClass = ResourceType.class, properties = { "id" })
	@Logable(name = "ResourceType", action = "update", property = { "id=ID,name=��������,showType=��������,parent.id=�ϼ�����ID,creatorId=������,createTime=����ʱ��" })
	public void updateResourceType(ResourceType resourcetype);

	@Memcached(targetClass = ResourceType.class, properties = { "id" })
	@Logable(name = "ResourceType", action = "delete", property = { "id=ID,name=��������,showType=��������,parent.id=�ϼ�����ID,creatorId=������,createTime=����ʱ��" })
	@Restrict(roles = { "resourcetypedelete" }, mode = Restrict.Mode.ROLE)
	public void deleteResourceType(ResourceType resourcetype);

	public ResourceType getResourceType(int id);

	public List<ResourceType> findResourceTypeBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	/**
	 * �ӻ�����ȡ�÷����б� yuzs 2009-12-02
	 * 
	 * @param keyWord
	 * @param pageNo
	 * @param pageSize
	 * @param orderBy
	 * @param isAsc
	 * @param expressions
	 * @return
	 */
	public List<ResourceType> findResourceTypeBymemcached(String keyWord,
			int pageNo, int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	public Long getResourceTypeResultCount(
			Collection<HibernateExpression> expressions);

	/**
	 * ������Դ����Դ���������ϵ
	 */
	@Memcached(targetClass = ResourceResType.class, properties = { "rid",
			"resTypeId#!count" })
	@Logable(name = "ResourceResType", action = "add", property = { "rid=��ԴID,resTypeId=��Դ����ID" })
	public void addResourceResType(ResourceResType resourcerestype)
			throws Exception;

	@Memcached(targetClass = ResourceResType.class, properties = { "rid",
			"resTypeId#!count" })
	@Logable(name = "ResourceResType", action = "update", property = { "rid=��ԴID,resTypeId=��Դ����ID" })
	public void updateResourceResType(ResourceResType resourcerestype);

	public Long getResourceTypeResResultCount(
			Collection<HibernateExpression> expressions);

	/**
	 * ɾ����ԴID����������
	 * 
	 * @param resourceId
	 */
	@Memcached(targetClass = ResourceResType.class, properties = { "native" })
	@Logable(name = "ResourceResType", action = "delete", property = { "native=��ԴID" })
	public void deleteResourceResType(String resourceId);

	/**
	 * ������Դ�ͷ�������ɾ��
	 * 
	 * @param resourceId
	 */
	@Memcached(targetClass = ResourceResType.class, properties = { "native" })
	@Logable(name = "ResourceResType", action = "delete", property = { "native=��ԴID" })
	public void deleteResourceResTypeByRT(String resourceId,
			Integer resourceTypeId);

	public ResourceResType getResourceResType(int id);

	public List<ResourceResType> findResourceResTypeBy(int pageNo,
			int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	public void addResourceResTypeByUnique(ResourceResType resourcerestype);

	/**
	 * ����ͼ������½ڴ�����ϵ
	 */
	@Memcached(targetClass = ResourceAll.class, properties = { "resourceId#!tomes" })
	@Logable(name = "EbookTome", action = "add", property = { "id=��ID,resourceId=��ԴID,name=������,tomeIndex=�����" })
	public void addEbookTome(EbookTome ebooktome) throws Exception;

	@Memcached(targetClass = ResourceAll.class, properties = { "resourceId#!tomes" })
	@Logable(name = "EbookTome", action = "update", property = { "id=��ID,resourceId=��ԴID,name=������,tomeIndex=�����" })
	public void updateEbookTome(EbookTome ebooktome);

	@Memcached(targetClass = ResourceAll.class, properties = { "resourceId#!tomes" })
	@Logable(name = "EbookTome", action = "delete", property = { "id=��ID,resourceId=��ԴID,name=������,tomeIndex=�����" })
	public void deleteEbookTome(EbookTome ebooktome);

	public EbookTome getEbookTome(String id);

	public List<EbookTome> getEbookTomeByResourceId(String resourceId);

	/**
	 * ��Դ���
	 * 
	 * @param resource
	 * @param status
	 * @throws Exception
	 */
	@Memcached(targetClass = ResourceAll.class, properties = { "id",
			"id#@updatetime" })
	@Logable(name = "ResourceAll", action = "audit", property = { "id=��ԴID,name=��Դ����,authorId=����ID,copyrightId=��ȨID,lastCopyrightId=�ϴ�ʹ�ð�ȨID,isbn=ISBN,publishTime=��������,cpId=CP����ID,status=��Դ״̬,cArea=�������,"
			+ "expNum=�Ƽ�ָ��,bComment=�Ƽ���,rKeyword=�ؼ���,division=�鲿,initialLetter=����ĸ,publisher=������,bLanguage=����,isFirstpublish=�Ƿ��׷�,isUnique=�Ƿ�ר��,isOut=�Ƿ����,isFinished=�Ƿ�ȫ��,"
			+ "creatorId=������,modifierId=�޸���,createTime=����ʱ��,modifyTime=�޸�ʱ��,cComment=�̼��,introLon=�����" })
	@Restrict(roles = { "resourceaudit" }, mode = Restrict.Mode.ROLE)
	public void auditResource(ResourceAll resource, Integer status,
			UserImpl user) throws Exception;

	@Memcached(targetClass = ResourceAll.class, properties = { "id",
			"id#@updatetime" })
	@Logable(name = "ResourceAll", action = "audit", property = { "id=��ԴID,name=��Դ����,authorId=����ID,copyrightId=��ȨID,lastCopyrightId=�ϴ�ʹ�ð�ȨID,isbn=ISBN,publishTime=��������,cpId=CP����ID,status=��Դ״̬,cArea=�������,"
			+ "expNum=�Ƽ�ָ��,bComment=�Ƽ���,rKeyword=�ؼ���,division=�鲿,initialLetter=����ĸ,publisher=������,bLanguage=����,isFirstpublish=�Ƿ��׷�,isUnique=�Ƿ�ר��,isOut=�Ƿ����,isFinished=�Ƿ�ȫ��,"
			+ "creatorId=������,modifierId=�޸���,createTime=����ʱ��,modifyTime=�޸�ʱ��,cComment=�̼��,introLon=�����" })
	@Restrict(roles = { "resourceaudit" }, mode = Restrict.Mode.ROLE)
	public void auditResourceTop(ResourceAll resource, Integer status)
			throws Exception;

	@Memcached(targetClass = ResourceAll.class, properties = { "id",
			"id#@updatetime" })
	@Logable(name = "ResourceAll", action = "delete", property = { "id=��ԴID,name=��Դ����,authorId=����ID,copyrightId=��ȨID,lastCopyrightId=�ϴ�ʹ�ð�ȨID,isbn=ISBN,publishTime=��������,cpId=CP����ID,status=��Դ״̬,cArea=�������,"
			+ "expNum=�Ƽ�ָ��,bComment=�Ƽ���,rKeyword=�ؼ���,division=�鲿,initialLetter=����ĸ,publisher=������,bLanguage=����,isFirstpublish=�Ƿ��׷�,isUnique=�Ƿ�ר��,isOut=�Ƿ����,isFinished=�Ƿ�ȫ��,"
			+ "creatorId=������,modifierId=�޸���,createTime=����ʱ��,modifyTime=�޸�ʱ��,cComment=�̼��,introLon=�����" })
	@Restrict(roles = { "resourceaudit" }, mode = Restrict.Mode.ROLE)
	public void deleteResource(ResourceAll resource,UserImpl user) throws Exception;

	/**
	 * ��ȡ��Դ����
	 * 
	 * @param resourceId
	 * @param resourceType
	 * @return
	 */
	public ResourceAll getResource(String resourceId, Integer resourceType);

	/**
	 * ��ȡ��Դ
	 * 
	 * @param resourceId
	 * @return
	 */
	public ResourceAll getResource(String resourceId);

	/**
	 * �����Դ
	 * 
	 * @param resource
	 * @param resourceType
	 * @throws Exception
	 */
	@Memcached(targetClass = ResourceAll.class, properties = { "id",
			"id#@updatetime" })
	@Logable(name = "ResourceAll", action = "add", property = { "id=��ԴID,name=��Դ����,authorId=����ID,copyrightId=��ȨID,lastCopyrightId=�ϴ�ʹ�ð�ȨID,isbn=ISBN,publishTime=��������,cpId=CP����ID,status=��Դ״̬,cArea=�������,"
			+ "expNum=�Ƽ�ָ��,bComment=�Ƽ���,rKeyword=�ؼ���,division=�鲿,initialLetter=����ĸ,publisher=������,bLanguage=����,isFirstpublish=�Ƿ��׷�,isUnique=�Ƿ�ר��,isOut=�Ƿ����,isFinished=�Ƿ�ȫ��,"
			+ "creatorId=������,modifierId=�޸���,createTime=����ʱ��,modifyTime=�޸�ʱ��,cComment=�̼��,introLon=�����" })
	public void addResource(ResourceAll resource, Integer resourceType)
			throws Exception;

	/**
	 * ������Դ
	 * 
	 * @param resource
	 * @param resourceType
	 */
	@Memcached(targetClass = ResourceAll.class, properties = { "id",
			"id#@updatetime" })
	@Logable(name = "ResourceAll", action = "update", property = { "id=��ԴID,name=��Դ����,authorId=����ID,copyrightId=��ȨID,lastCopyrightId=�ϴ�ʹ�ð�ȨID,isbn=ISBN,publishTime=��������,cpId=CP����ID,status=��Դ״̬,cArea=�������,"
			+ "expNum=�Ƽ�ָ��,bComment=�Ƽ���,rKeyword=�ؼ���,division=�鲿,initialLetter=����ĸ,publisher=������,bLanguage=����,isFirstpublish=�Ƿ��׷�,isUnique=�Ƿ�ר��,isOut=�Ƿ����,isFinished=�Ƿ�ȫ��,"
			+ "creatorId=������,modifierId=�޸���,createTime=����ʱ��,modifyTime=�޸�ʱ��,cComment=�̼��,introLon=�����" })
	public void updateResource(ResourceAll resource, Integer resourceType);

	/**
	 * ������Դʱ���޸�״̬
	 * 
	 * @param resource
	 * @param resourceType
	 */
	@Memcached(targetClass = ResourceAll.class, properties = { "id" })
	public void updateResourceNOChangeStatus(ResourceAll resource,
			Integer resourceType);

	/**
	 * ����½ڶ���
	 * 
	 * @param obj
	 * @param resourceType
	 * @throws Exception
	 */
	@Memcached(targetClass = ResourceAll.class, properties = {
			"resourceId#@updatetime", "resourceId#!chapters" })
	@Logable(name = "ResourceChapter", action = "add", property = { "id=�½�ID,name=�½�����,resourceId=��ԴID,chapterIndex=�½����,tomeId=�½ڹ�����ID" })
	public void addResourceChapter(Object obj, Integer resourceType)
			throws Exception;

	public boolean isVideoFileNameExists(Object obj);
	/**
	 * ������Դid��ȡ�½��б�
	 * 
	 * @param clasz
	 * @param resourceId
	 * @return
	 */
	public List getResourceChapter(Class clasz, String resourceId);

	/**
	 * ������Դid�Լ���ѯ������ȡ�½��б�
	 * 
	 * @param clasz
	 * @param resourceId
	 * @return
	 */
	public List getResourceChapterList(Class clasz, int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	/**
	 * ��ȡ�½��б����
	 * 
	 * @param �½ڶ���
	 * @param ����
	 */
	public Long getResourceChapterCount(Class clasz,
			Collection<HibernateExpression> expressions);

	/**
	 * �����½���Ϣ
	 * 
	 * @param �½ڶ���
	 */
	@Memcached(targetClass = ResourceAll.class, properties = {
			"resourceId#@updatetime", "resourceId#!chapters" })
	@Logable(name = "ResourceChapter", action = "update", property = { "id=�½�ID,name=�½�����,resourceId=��ԴID,chapterIndex=�½����,tomeId=�½ڹ�����ID" })
	public void updateResourceChapter(Object obj) throws Exception;

	/**
	 * ɾ���½ڶ���
	 * 
	 * @param chapter
	 * @param resourceType
	 */
	@Memcached(targetClass = ResourceAll.class, properties = {
			"resourceId#@updatetime", "resourceId#!chapters" })
	@Logable(name = "ResourceChapter", action = "delete", property = { "id=�½�ID,name=�½�����,resourceId=��ԴID,chapterIndex=�½����,tomeId=�½ڹ�����ID" })
	public void deleteResourceChapter(Object chapter, Integer resourceType);

	/**
	 * ��ȡͼ���½ڶ���
	 * 
	 * @param chapterId
	 * @return
	 */
	public EbookChapter getEbookChapterById(String chapterId);

	/**
	 * ��ȡ��־�½ڶ���
	 * 
	 * @param chapterId
	 * @return
	 */
	public MagazineChapter getMagazineChapterById(String chapterId);

	/**
	 * ��ȡ��ֽ�½ڶ���
	 * 
	 * @param chapterId
	 * @return
	 */
	public NewsPapersChapter getNewsPapersChapterById(String chapterId);

	/**
	 * ��ȡ�����½ڶ���
	 * 
	 * @param chapterId
	 * @return
	 */
	public ComicsChapter getComicsChapterById(String chapterId);

	/**
	 * ��ȡ��ԴԤ��ͼ
	 * 
	 * @param resourceId
	 *            ��ԴID
	 * @param coverImg
	 *            ����ͼ
	 * @return
	 */
	public String getPreviewCoverImg(String resourceId, String coverImg);

	/**
	 * ��ȡ�½�ͼƬ��ַ
	 * 
	 * @param resourceId
	 * @param imgName
	 * @return
	 */
	public String getChapterImg(String resourceId, String imgName);
	

	/**
	 * ��ȡUEB�ļ���ַ
	 * 
	 * @param relId
	 * @return ����0�� 128��ʽ�� 1��176��ʽ 2��240��ʽ
	 */
	public String[] getUebAddress(int relId);

	/**
	 * ��ȡ�½ڴ�ŵ������ַ
	 */

	public String getChapterAddress(String resourceId);

	/**
	 * �����ϴ��õ�һ���������ϴ�ָ�����Ƶ��ļ�
	 * 
	 * @param IUploadFile
	 * @param ָ�����ļ�����
	 * @param �ϴ����ĵط�
	 */
	public String upload(IUploadFile file, String name, File dir);

	/***************************************************************************
	 * ɾ���ļ�
	 * 
	 * @param �ļ�·��
	 */
	public boolean deleteFile(String dir);

	public Long getResourceResultCountByHQL(Integer cpid, Integer resourceType,
			String name, String authorId, Integer childTypeId, Integer status,
			Integer creatorId, String keyWord, Integer isFinish,
			String publisher, String initialLetter, Integer expNum,
			Integer isFirstpublish, Integer isOut, Integer healthNum,
			Integer isSearchTop, Integer isDivision);

	public List<ResourceAll> findResourceByHQL(Integer cpid,
			Integer resourceType, String name, String authorId,
			Integer childTypeId, Integer status, Integer creatorId,
			String keyWord, Integer isFinish, String publisher,
			String initialLetter, Integer expNum, Integer isFirstpublish,
			Integer isOut, Integer healthNum, Integer isSearchTop,
			Integer isDivision, int pageNum, int pageSize);

	/**
	 * ��ȡ���۰���δ��������Դ
	 * 
	 * @param cpid
	 * @param resourceType
	 * @param name
	 * @param authorId
	 * @param childTypeId
	 * @param status
	 * @param creatorId
	 * @param packId
	 * @return
	 */
	public Long getResourceResultNotInPack(Integer cpid, Integer resourceType,
			String name, String authorId, Integer childTypeId, boolean isTof,
			 Integer creatorId, ResourcePack pack,
			Set<String> set);

	public List<ResourceAll> findResourceNotInPack(Integer cpid,
			Integer resourceType, String name, String authorId,
			Integer childTypeId, boolean isTof,
			Integer creatorId, ResourcePack pack, Set<String> set, int pageNum,
			int pageSize);

	/**
	 * �ϴ���Դʱ���ж���Դ�Ƿ��Ѿ�����
	 * 
	 * @param resourceType
	 * @param resource
	 * @return
	 */
	public boolean isResourceExists(Integer resourceType, ResourceAll resource);

	/**
	 * ͬ���ļ�
	 * 
	 * @param rsyncType
	 * @param fileNames
	 */
	public void rsyncUploadFile(String rsyncType, String[] fileNames);

	@Logable(name = "ResourceAll", action = "reCheck", keyproperty = "resourceId", property = { "id=ID,resourceId=��Դid,comment=�������,creatorId=������,createTime=����ʱ��" })
	public void saveReCheck(ReCheck reCheck);

	public void updateResourceAll(ResourceAll resourceAll);

	/**
	 * ��ȡ��������б�
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param orderBy
	 * @param isAsc
	 * @param expressions
	 * @return
	 */
	public List<ReCheck> findReCheckAll(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	/**
	 * ��ȡ��������б�������
	 * 
	 * @param expressions
	 * @return
	 */
	public Long findReCheckCount(Collection<HibernateExpression> expressions);

	// public ResourceAll getResourceFromMemcached(String resourceId);
	
	 /**
     * ����Ѷ���ݽ��з�ҳ�������ҳ��־�� �����з�����<br/> ���Ұ�һ�����������ҳ��־������wapǰ�˷�ҳ��ʾ
     */
    public String splitDocContent(String text);
}