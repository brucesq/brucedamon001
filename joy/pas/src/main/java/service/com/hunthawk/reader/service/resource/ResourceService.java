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
	 * 获取资源
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
	 * 获取资源条数
	 * 
	 * @param expressions
	 * @return
	 */
	public Long getResourceResultCount(
			Collection<HibernateExpression> expressions);

	/**
	 * 处理作者信息
	 */
	@Memcached(targetClass = ResourceAuthor.class, properties = { "id" })
	@Logable(name = "ResourceAuthor", action = "add", property = { "id=ID,name=名称,initialLetter=首字母,penName=作者笔名,sex=性别,area=地区,intro=作者简介,status=作者状态,creatorId=创建人,createTime=创建时间" })
	public void addResourceAuthor(ResourceAuthor resourceauthor)
			throws Exception;

	@Memcached(targetClass = ResourceAuthor.class, properties = { "id" })
	@Logable(name = "ResourceAuthor", action = "update", property = { "id=ID,name=名称,initialLetter=首字母,penName=作者笔名,sex=性别,area=地区,intro=作者简介,status=作者状态,creatorId=创建人,createTime=创建时间" })
	public void updateResourceAuthor(ResourceAuthor resourceauthor);

	@Memcached(targetClass = ResourceAuthor.class, properties = { "id" })
	@Logable(name = "ResourceAuthor", action = "delete", property = { "id=ID,name=名称,initialLetter=首字母,penName=作者笔名,sex=性别,area=地区,intro=作者简介,status=作者状态,creatorId=创建人,createTime=创建时间" })
	public void deleteResourceAuthor(ResourceAuthor resourceauthor);

	/**
	 * 根据作者名称获得作者ID
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public ResourceAuthor getResourceAuthorByName(String name) throws Exception;

	/**
	 * 根据作者ID获取作者对象
	 * 
	 * @param id
	 * @return
	 */
	public ResourceAuthor getResourceAuthorById(Integer id);

	/**
	 * 获取所有作者信息
	 * 
	 * @return
	 */
	public List<ResourceAuthor> getAllResourceAuthor();

	public List<ResourceAuthor> findResourceAuthorBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	/**
	 * 从缓存中取的作者列表
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
	 * 处理版权信息
	 */
	@Logable(name = "ResourceReferen", action = "add", property = { "id=ID,name=版权方名称,contactName=版权联系人名称,contactPhone=版权联系人电话,email=版权联系人邮箱,address=版权联系人地址,fax=版权联系人传真,beginTime=生效日期,endTime=失效日期,status=版权状态,cpId=CP表中的ID,identifier=版权标识,"
			+ "providerInfo=授权书,cooperatePro=合作协议,authorName=作者信息表,copyrightCheck=版权登记证书,productInfo=作品版权状况说明书,mcpinfo=MCP版权自查情况说明书,promises=MCP版权无问题承诺书,copyrightUse=著作权许可使用协议,copyrightAttorn=著作权转让协议,copyrightOther=其他,modifierId=修改人,modifyTime=修改时间" })
	public void addResourceReferen(ResourceReferen resourcereferen)
			throws Exception;

	@Logable(name = "ResourceReferen", action = "update", property = { "id=ID,name=版权方名称,contactName=版权联系人名称,contactPhone=版权联系人电话,email=版权联系人邮箱,address=版权联系人地址,fax=版权联系人传真,beginTime=生效日期,endTime=失效日期,status=版权状态,cpId=CP表中的ID,identifier=版权标识,"
			+ "providerInfo=授权书,cooperatePro=合作协议,authorName=作者信息表,copyrightCheck=版权登记证书,productInfo=作品版权状况说明书,mcpinfo=MCP版权自查情况说明书,promises=MCP版权无问题承诺书,copyrightUse=著作权许可使用协议,copyrightAttorn=著作权转让协议,copyrightOther=其他,modifierId=修改人,modifyTime=修改时间" })
	public void updateResourceReferen(ResourceReferen resourcereferen);

	@Logable(name = "ResourceReferen", action = "delete", property = { "id=ID,name=版权方名称,contactName=版权联系人名称,contactPhone=版权联系人电话,email=版权联系人邮箱,address=版权联系人地址,fax=版权联系人传真,beginTime=生效日期,endTime=失效日期,status=版权状态,cpId=CP表中的ID,identifier=版权标识,"
			+ "providerInfo=授权书,cooperatePro=合作协议,authorName=作者信息表,copyrightCheck=版权登记证书,productInfo=作品版权状况说明书,mcpinfo=MCP版权自查情况说明书,promises=MCP版权无问题承诺书,copyrightUse=著作权许可使用协议,copyrightAttorn=著作权转让协议,copyrightOther=其他,modifierId=修改人,modifyTime=修改时间" })
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
	 * 版权审核
	 * 
	 * @param resourceReferen
	 * @param status
	 * @throws Exception
	 */
	@Logable(name = "ResourceReferen", action = "audit", property = { "id=ID,name=版权方名称,contactName=版权联系人名称,contactPhone=版权联系人电话,email=版权联系人邮箱,address=版权联系人地址,fax=版权联系人传真,beginTime=生效日期,endTime=失效日期,status=版权状态,cpId=CP表中的ID,identifier=版权标识,"
			+ "providerInfo=授权书,cooperatePro=合作协议,authorName=作者信息表,copyrightCheck=版权登记证书,productInfo=作品版权状况说明书,mcpinfo=MCP版权自查情况说明书,promises=MCP版权无问题承诺书,copyrightUse=著作权许可使用协议,copyrightAttorn=著作权转让协议,copyrightOther=其他,modifierId=修改人,modifyTime=修改时间" })
	@Restrict(roles = { "copyrightaudit" }, mode = Restrict.Mode.ROLE)
	public void auditResourceReferen(ResourceReferen resourceReferen,
			Integer status) throws Exception;

	/**
	 * 处理资源分类
	 */
	@Memcached(targetClass = ResourceType.class, properties = { "id" })
	@Logable(name = "ResourceType", action = "add", property = { "id=ID,name=分类名称,showType=归属类型,parent.id=上级分类ID,creatorId=创建人,createTime=创建时间" })
	public void addResourceType(ResourceType resourcetype) throws Exception;

	@Memcached(targetClass = ResourceType.class, properties = { "id" })
	@Logable(name = "ResourceType", action = "update", property = { "id=ID,name=分类名称,showType=归属类型,parent.id=上级分类ID,creatorId=创建人,createTime=创建时间" })
	public void updateResourceType(ResourceType resourcetype);

	@Memcached(targetClass = ResourceType.class, properties = { "id" })
	@Logable(name = "ResourceType", action = "delete", property = { "id=ID,name=分类名称,showType=归属类型,parent.id=上级分类ID,creatorId=创建人,createTime=创建时间" })
	@Restrict(roles = { "resourcetypedelete" }, mode = Restrict.Mode.ROLE)
	public void deleteResourceType(ResourceType resourcetype);

	public ResourceType getResourceType(int id);

	public List<ResourceType> findResourceTypeBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	/**
	 * 从缓存中取得分类列表 yuzs 2009-12-02
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
	 * 处理资源与资源分类从属关系
	 */
	@Memcached(targetClass = ResourceResType.class, properties = { "rid",
			"resTypeId#!count" })
	@Logable(name = "ResourceResType", action = "add", property = { "rid=资源ID,resTypeId=资源分类ID" })
	public void addResourceResType(ResourceResType resourcerestype)
			throws Exception;

	@Memcached(targetClass = ResourceResType.class, properties = { "rid",
			"resTypeId#!count" })
	@Logable(name = "ResourceResType", action = "update", property = { "rid=资源ID,resTypeId=资源分类ID" })
	public void updateResourceResType(ResourceResType resourcerestype);

	public Long getResourceTypeResResultCount(
			Collection<HibernateExpression> expressions);

	/**
	 * 删除资源ID关联的类型
	 * 
	 * @param resourceId
	 */
	@Memcached(targetClass = ResourceResType.class, properties = { "native" })
	@Logable(name = "ResourceResType", action = "delete", property = { "native=资源ID" })
	public void deleteResourceResType(String resourceId);

	/**
	 * 根据资源和分类类型删除
	 * 
	 * @param resourceId
	 */
	@Memcached(targetClass = ResourceResType.class, properties = { "native" })
	@Logable(name = "ResourceResType", action = "delete", property = { "native=资源ID" })
	public void deleteResourceResTypeByRT(String resourceId,
			Integer resourceTypeId);

	public ResourceResType getResourceResType(int id);

	public List<ResourceResType> findResourceResTypeBy(int pageNo,
			int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	public void addResourceResTypeByUnique(ResourceResType resourcerestype);

	/**
	 * 处理图书卷与章节从属关系
	 */
	@Memcached(targetClass = ResourceAll.class, properties = { "resourceId#!tomes" })
	@Logable(name = "EbookTome", action = "add", property = { "id=卷ID,resourceId=资源ID,name=卷名称,tomeIndex=卷序号" })
	public void addEbookTome(EbookTome ebooktome) throws Exception;

	@Memcached(targetClass = ResourceAll.class, properties = { "resourceId#!tomes" })
	@Logable(name = "EbookTome", action = "update", property = { "id=卷ID,resourceId=资源ID,name=卷名称,tomeIndex=卷序号" })
	public void updateEbookTome(EbookTome ebooktome);

	@Memcached(targetClass = ResourceAll.class, properties = { "resourceId#!tomes" })
	@Logable(name = "EbookTome", action = "delete", property = { "id=卷ID,resourceId=资源ID,name=卷名称,tomeIndex=卷序号" })
	public void deleteEbookTome(EbookTome ebooktome);

	public EbookTome getEbookTome(String id);

	public List<EbookTome> getEbookTomeByResourceId(String resourceId);

	/**
	 * 资源审核
	 * 
	 * @param resource
	 * @param status
	 * @throws Exception
	 */
	@Memcached(targetClass = ResourceAll.class, properties = { "id",
			"id#@updatetime" })
	@Logable(name = "ResourceAll", action = "audit", property = { "id=资源ID,name=资源名称,authorId=作者ID,copyrightId=版权ID,lastCopyrightId=上传使用版权ID,isbn=ISBN,publishTime=出版日期,cpId=CP表中ID,status=资源状态,cArea=出版地区,"
			+ "expNum=推荐指数,bComment=推荐语,rKeyword=关键字,division=书部,initialLetter=首字母,publisher=出版社,bLanguage=语言,isFirstpublish=是否首发,isUnique=是否专有,isOut=是否出版,isFinished=是否全本,"
			+ "creatorId=创建者,modifierId=修改者,createTime=创建时间,modifyTime=修改时间,cComment=短简介,introLon=长简介" })
	@Restrict(roles = { "resourceaudit" }, mode = Restrict.Mode.ROLE)
	public void auditResource(ResourceAll resource, Integer status,
			UserImpl user) throws Exception;

	@Memcached(targetClass = ResourceAll.class, properties = { "id",
			"id#@updatetime" })
	@Logable(name = "ResourceAll", action = "audit", property = { "id=资源ID,name=资源名称,authorId=作者ID,copyrightId=版权ID,lastCopyrightId=上传使用版权ID,isbn=ISBN,publishTime=出版日期,cpId=CP表中ID,status=资源状态,cArea=出版地区,"
			+ "expNum=推荐指数,bComment=推荐语,rKeyword=关键字,division=书部,initialLetter=首字母,publisher=出版社,bLanguage=语言,isFirstpublish=是否首发,isUnique=是否专有,isOut=是否出版,isFinished=是否全本,"
			+ "creatorId=创建者,modifierId=修改者,createTime=创建时间,modifyTime=修改时间,cComment=短简介,introLon=长简介" })
	@Restrict(roles = { "resourceaudit" }, mode = Restrict.Mode.ROLE)
	public void auditResourceTop(ResourceAll resource, Integer status)
			throws Exception;

	@Memcached(targetClass = ResourceAll.class, properties = { "id",
			"id#@updatetime" })
	@Logable(name = "ResourceAll", action = "delete", property = { "id=资源ID,name=资源名称,authorId=作者ID,copyrightId=版权ID,lastCopyrightId=上传使用版权ID,isbn=ISBN,publishTime=出版日期,cpId=CP表中ID,status=资源状态,cArea=出版地区,"
			+ "expNum=推荐指数,bComment=推荐语,rKeyword=关键字,division=书部,initialLetter=首字母,publisher=出版社,bLanguage=语言,isFirstpublish=是否首发,isUnique=是否专有,isOut=是否出版,isFinished=是否全本,"
			+ "creatorId=创建者,modifierId=修改者,createTime=创建时间,modifyTime=修改时间,cComment=短简介,introLon=长简介" })
	@Restrict(roles = { "resourceaudit" }, mode = Restrict.Mode.ROLE)
	public void deleteResource(ResourceAll resource,UserImpl user) throws Exception;

	/**
	 * 获取资源对象
	 * 
	 * @param resourceId
	 * @param resourceType
	 * @return
	 */
	public ResourceAll getResource(String resourceId, Integer resourceType);

	/**
	 * 获取资源
	 * 
	 * @param resourceId
	 * @return
	 */
	public ResourceAll getResource(String resourceId);

	/**
	 * 添加资源
	 * 
	 * @param resource
	 * @param resourceType
	 * @throws Exception
	 */
	@Memcached(targetClass = ResourceAll.class, properties = { "id",
			"id#@updatetime" })
	@Logable(name = "ResourceAll", action = "add", property = { "id=资源ID,name=资源名称,authorId=作者ID,copyrightId=版权ID,lastCopyrightId=上传使用版权ID,isbn=ISBN,publishTime=出版日期,cpId=CP表中ID,status=资源状态,cArea=出版地区,"
			+ "expNum=推荐指数,bComment=推荐语,rKeyword=关键字,division=书部,initialLetter=首字母,publisher=出版社,bLanguage=语言,isFirstpublish=是否首发,isUnique=是否专有,isOut=是否出版,isFinished=是否全本,"
			+ "creatorId=创建者,modifierId=修改者,createTime=创建时间,modifyTime=修改时间,cComment=短简介,introLon=长简介" })
	public void addResource(ResourceAll resource, Integer resourceType)
			throws Exception;

	/**
	 * 更新资源
	 * 
	 * @param resource
	 * @param resourceType
	 */
	@Memcached(targetClass = ResourceAll.class, properties = { "id",
			"id#@updatetime" })
	@Logable(name = "ResourceAll", action = "update", property = { "id=资源ID,name=资源名称,authorId=作者ID,copyrightId=版权ID,lastCopyrightId=上传使用版权ID,isbn=ISBN,publishTime=出版日期,cpId=CP表中ID,status=资源状态,cArea=出版地区,"
			+ "expNum=推荐指数,bComment=推荐语,rKeyword=关键字,division=书部,initialLetter=首字母,publisher=出版社,bLanguage=语言,isFirstpublish=是否首发,isUnique=是否专有,isOut=是否出版,isFinished=是否全本,"
			+ "creatorId=创建者,modifierId=修改者,createTime=创建时间,modifyTime=修改时间,cComment=短简介,introLon=长简介" })
	public void updateResource(ResourceAll resource, Integer resourceType);

	/**
	 * 更新资源时不修改状态
	 * 
	 * @param resource
	 * @param resourceType
	 */
	@Memcached(targetClass = ResourceAll.class, properties = { "id" })
	public void updateResourceNOChangeStatus(ResourceAll resource,
			Integer resourceType);

	/**
	 * 添加章节对象
	 * 
	 * @param obj
	 * @param resourceType
	 * @throws Exception
	 */
	@Memcached(targetClass = ResourceAll.class, properties = {
			"resourceId#@updatetime", "resourceId#!chapters" })
	@Logable(name = "ResourceChapter", action = "add", property = { "id=章节ID,name=章节名称,resourceId=资源ID,chapterIndex=章节序号,tomeId=章节归属卷ID" })
	public void addResourceChapter(Object obj, Integer resourceType)
			throws Exception;

	public boolean isVideoFileNameExists(Object obj);
	/**
	 * 根据资源id获取章节列表
	 * 
	 * @param clasz
	 * @param resourceId
	 * @return
	 */
	public List getResourceChapter(Class clasz, String resourceId);

	/**
	 * 根据资源id以及查询条件获取章节列表
	 * 
	 * @param clasz
	 * @param resourceId
	 * @return
	 */
	public List getResourceChapterList(Class clasz, int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	/**
	 * 获取章节列表个数
	 * 
	 * @param 章节对象
	 * @param 条件
	 */
	public Long getResourceChapterCount(Class clasz,
			Collection<HibernateExpression> expressions);

	/**
	 * 更新章节信息
	 * 
	 * @param 章节对象
	 */
	@Memcached(targetClass = ResourceAll.class, properties = {
			"resourceId#@updatetime", "resourceId#!chapters" })
	@Logable(name = "ResourceChapter", action = "update", property = { "id=章节ID,name=章节名称,resourceId=资源ID,chapterIndex=章节序号,tomeId=章节归属卷ID" })
	public void updateResourceChapter(Object obj) throws Exception;

	/**
	 * 删除章节对象
	 * 
	 * @param chapter
	 * @param resourceType
	 */
	@Memcached(targetClass = ResourceAll.class, properties = {
			"resourceId#@updatetime", "resourceId#!chapters" })
	@Logable(name = "ResourceChapter", action = "delete", property = { "id=章节ID,name=章节名称,resourceId=资源ID,chapterIndex=章节序号,tomeId=章节归属卷ID" })
	public void deleteResourceChapter(Object chapter, Integer resourceType);

	/**
	 * 获取图书章节对象
	 * 
	 * @param chapterId
	 * @return
	 */
	public EbookChapter getEbookChapterById(String chapterId);

	/**
	 * 获取杂志章节对象
	 * 
	 * @param chapterId
	 * @return
	 */
	public MagazineChapter getMagazineChapterById(String chapterId);

	/**
	 * 获取报纸章节对象
	 * 
	 * @param chapterId
	 * @return
	 */
	public NewsPapersChapter getNewsPapersChapterById(String chapterId);

	/**
	 * 获取漫画章节对象
	 * 
	 * @param chapterId
	 * @return
	 */
	public ComicsChapter getComicsChapterById(String chapterId);

	/**
	 * 获取资源预览图
	 * 
	 * @param resourceId
	 *            资源ID
	 * @param coverImg
	 *            封面图
	 * @return
	 */
	public String getPreviewCoverImg(String resourceId, String coverImg);

	/**
	 * 获取章节图片地址
	 * 
	 * @param resourceId
	 * @param imgName
	 * @return
	 */
	public String getChapterImg(String resourceId, String imgName);
	

	/**
	 * 获取UEB文件地址
	 * 
	 * @param relId
	 * @return 数组0是 128格式的 1是176格式 2是240格式
	 */
	public String[] getUebAddress(int relId);

	/**
	 * 获取章节存放的物理地址
	 */

	public String getChapterAddress(String resourceId);

	/**
	 * 单独上传用到一个方法，上传指定名称的文件
	 * 
	 * @param IUploadFile
	 * @param 指定的文件名称
	 * @param 上传到的地方
	 */
	public String upload(IUploadFile file, String name, File dir);

	/***************************************************************************
	 * 删除文件
	 * 
	 * @param 文件路径
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
	 * 获取批价包中未包含的资源
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
	 * 上传资源时，判断资源是否已经存在
	 * 
	 * @param resourceType
	 * @param resource
	 * @return
	 */
	public boolean isResourceExists(Integer resourceType, ResourceAll resource);

	/**
	 * 同步文件
	 * 
	 * @param rsyncType
	 * @param fileNames
	 */
	public void rsyncUploadFile(String rsyncType, String[] fileNames);

	@Logable(name = "ResourceAll", action = "reCheck", keyproperty = "resourceId", property = { "id=ID,resourceId=资源id,comment=复审意见,creatorId=创建者,createTime=创建时间" })
	public void saveReCheck(ReCheck reCheck);

	public void updateResourceAll(ResourceAll resourceAll);

	/**
	 * 获取复审意见列表
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
	 * 获取复审意见列表总条数
	 * 
	 * @param expressions
	 * @return
	 */
	public Long findReCheckCount(Collection<HibernateExpression> expressions);

	// public ResourceAll getResourceFromMemcached(String resourceId);
	
	 /**
     * 对资讯内容进行分页，插入分页标志符 将换行符换成<br/> 并且按一定字数插入分页标志，用于wap前端分页显示
     */
    public String splitDocContent(String text);
}