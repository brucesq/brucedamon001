/**
 * 
 */
package com.hunthawk.reader.pps.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.EbookChapterDesc;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.MagazineChapterDesc;
import com.hunthawk.reader.domain.resource.NewsPapersChapterDesc;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.resource.VideoSuite;

/**
 * @author BruceSun
 * 
 */
public interface ResourceService {

	/**
	 * 获取资源对象,如果是图书返回ebook对象,漫画返回comics,杂志返回magazine,报纸返回newspaper对象,如果不存在返回null
	 * .
	 * 
	 * @param rid
	 * @return
	 */
	public ResourceAll getResource(String rid);

	/**
	 * 获取批价包对象
	 * 
	 * @param packId
	 * @return
	 */
	public ResourcePack getResourcePack(int packId);

	public ResourcePackReleation getResourcePackReleation(int relId);

	/**
	 * 查询单个实体
	 * @param packId
	 * @param resourceId
	 * @return
	 * @author liuxh 09-11-04
	 */
	public ResourcePackReleation getResourcePackReleation(int packId,String resourceId);
	public List<ResourcePackReleation> getResourcePackReleations(int packId,
			int pageNo, int pagesize);
	// public List<ResourceType> getResourceTypeAll();
	/**
	 * 分页显示方法 返回一个从第N条开始到第M条结束的记录集合 即每页显示M条记录
	 * 
	 * @param pageHql
	 *            动态HQL语句
	 * @param firstResult
	 *            可以看成是游标起始位置 第1条的话值为0
	 * @param MaxResults
	 *            可以看成是每页显示多少条记录
	 * @return LIST 返回一个从第N条开始到第M条结束的记录集合
	 * @author add by liuxh
	 */
	// public List<ResourcePackReleation> getRollResourceBagRelations(Class cls,
	// int firstResult, int MaxResults,Object[] objs);
	/** 得到批价包下资源总数 */
	public Long getResourcePackReleationsCount(int packId);

	/**
	 * 根据章节ID获取资源章节对象
	 * 
	 * @param chapterId
	 * @return
	 */
	public Object getResourceChapter(String chapterId);

	/**
	 * 获取资源ID下所有章节基础信息
	 * 
	 * @param rid
	 * @return
	 */
	public List<EbookChapterDesc> getEbookChapterDescsByResourceID(String rid);

	/**
	 * 获取具体章节信息
	 * 
	 * @param chapterId
	 * @return
	 */
	public EbookChapterDesc getEbookChapterDesc(String chapterId);

	// /** 按作者ID查找相关资源信息 add by liuxh 09-7-28 */
	// public List<ResourceAll> getResourcesByAuthor(String authorid,int pageNo,
	// int pageSize);
	// //public List<ResourceAll> getResourcesAllByAuthor(String authorid);
	// public int getResourcesAllByAuthor(String authorid) ;

	/** 根据资源ID查询章节列表 */
	public List<EbookChapterDesc> getEbookChaptersByResourceID(String rid,
			int pageNo, int pageSize);

	public Long getEbookChaptersByResourceIDCount(String rid);

	/**
	 * 根据资源ID获取杂志的所有章节描述对象
	 * 
	 * @param rid
	 * @return
	 */
	public List<MagazineChapterDesc> getMagazineChaptersByResourceID(String rid);

	/**
	 * 根据资源ID获取杂志章节分页列表
	 * 
	 * @param rid
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<MagazineChapterDesc> getMagazineChaptersByResourceID(
			String rid, int pageNo, int pageSize);

	/**
	 * 获取杂志章节总数
	 * 
	 * @param rid
	 * @return
	 */
	public int getMagazineChaptersByResourceIDCount(String rid);

	/**
	 * 获取杂志章节对象
	 * 
	 * @param chapterId
	 * @return
	 */
	public MagazineChapterDesc getMagazineChapterDescById(String chapterId);

	/**
	 * 根据资源ID获取所有章节信息
	 * 
	 * @param rid
	 * @return
	 */
	public List<ComicsChapter> getComicsChaptersByResourceId(String rid);

	/**
	 * 根据资源ID获取漫画分页列表
	 * 
	 * @param rid
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<ComicsChapter> getComicsChaptersByResourceId(String rid,
			int pageNo, int pageSize);

	/**
	 * 获取漫画章节总数
	 * 
	 * @param rid
	 * @return
	 */
	public int getComicsChaptersByResourceIDCount(String rid);

	/**
	 * 获取漫画章节对象
	 * 
	 * @param chapterId
	 * @return
	 */
	public ComicsChapter getComicsChapterById(String chapterId);

	/**
	 * 根据卷ID获取漫画章节列表
	 * 
	 * @param tomeId
	 * @return
	 */
	public List<ComicsChapter> getComicsChapterByTomeId(String tomeId);

	public int getComicsChapterCountByTomeId(String tomeId);

	public List<ComicsChapter> getComicsChapterByTomeId(String tomeId,
			int pageNo, int pageSize);

	/**
	 * 根据卷ID获取报纸章节列表
	 * 
	 * @param tomeId
	 * @return
	 */
	public List<NewsPapersChapterDesc> getNewsPapersChapterDescByTomeId(
			String tomeId);

	public int getNewsPapersChapterDescCountByTomeId(String tomeId);

	public List<NewsPapersChapterDesc> getNewsPapersChapterDescByTomeId(
			String tomeId, int pageNo, int pageSize);

	/**
	 * 根据卷ID获取杂志章节列表
	 * 
	 * @param tomeId
	 * @return
	 */
	public List<MagazineChapterDesc> getMagazineChapterDescByTomeId(
			String tomeId);

	public int getMagazineChapterDescCountByTomeId(String tomeId);

	public List<MagazineChapterDesc> getMagazineChapterDescByTomeId(
			String tomeId, int pageNo, int pageSize);

	/**
	 * 根据卷ID获取图书章节列表
	 * 
	 * @param tomeId
	 * @return
	 */
	public List<EbookChapterDesc> getEbookChapterDescByTomeId(String tomeId);

	public int getEbookChapterDescCountByTomeId(String tomeId);

	public List<EbookChapterDesc> getEbookChapterDescByTomeId(String tomeId,
			int pageNo, int pageSize);

	/**
	 * 根据资源ID获取报纸的所有章节描述对象
	 * 
	 * @param rid
	 * @return
	 */
	public List<NewsPapersChapterDesc> getNewsPapersChaptersByResourceID(
			String rid);

	/**
	 * 根据资源ID获取报纸章节分页列表
	 * 
	 * @param rid
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<NewsPapersChapterDesc> getNewsPapersChaptersByResourceID(
			String rid, int pageNo, int pageSize);

	/**
	 * 获取报纸章节总数
	 * 
	 * @param rid
	 * @return
	 */
	public int getNewsPapersChaptersByResourceIDCount(String rid);

	/**
	 * 获取报纸章节对象
	 * 
	 * @param chapterId
	 * @return
	 */
	public NewsPapersChapterDesc getNewsPapersChapterDescById(String chapterId);

	/**
	 * 获取资源的卷列表 增加分页相关参数
	 * 
	 * @param rid
	 * @return 
	 * modify by liuxh 09-11-05
	 */
	public List<EbookTome> getEbookTomeByResourceId(String rid,int pageNo,int pageSize);

	/**
	 * 获取卷对象
	 * 
	 * @param tomeId
	 * @return
	 */
	public EbookTome getEbookTomeById(String tomeId);

	/**
	 * 获取资源卷总数
	 * 
	 * @param rid
	 * @return
	 */
	public int getEbookTomeCount(String rid);

	/** 根据资源ID查找资源分类ID 返回的是一个集合 */
	public List<Integer> getResourceTypeID(String rid);

	/**
	 * 根据资源类别ID获取资源ID集合
	 * 
	 * @param typeId
	 *            类别ID
	 * @param pageNo
	 *            当前页
	 * @param pageSize
	 *            每页显示条数
	 * @return 返回的是一个资源ID集合
	 */
	// public List<ResourceAll> getResourceByTypeId(int typeId,int pageNo,int
	// pageSize);
	/**
	 * 获取该类别下资源的数目
	 * 
	 * @param typeId
	 * @return
	 */
	// public int getResourceByTypeIdCount(int typeId);
	/** 根据资源分类ID查询资源类型信息 */
	public ResourceType getResourceType(int id);

	/** 根据作者ID查询作者信息 */
	public ResourceAuthor getResourceAuthor(int id);

	/**
	 * 获取上一章下一章的章节ID .isNotNext如果为false则是下一章，如果为true是上一章
	 * 
	 * @param chapterId
	 *            当前章节ID
	 * @param isNotNext
	 * @return
	 */
	public String browseResourceChapter(String chapterId, boolean isNotNext);

	/**
	 * 获取批价包关联的上一条,下一条.isNotNext如果为false则是下一条，如果为true是上一条
	 * 
	 * @param relId
	 *            批价包关联ID
	 * @param isNext
	 * @return 如果不存在返回null
	 */
	public ResourcePackReleation browseResourcePackReleation(int relId,
			boolean isNotNext);

	/**
	 * 通过资源ID获取其关联的批价包关联信息
	 * 
	 * @param rid
	 * @return
	 */
	public List<ResourcePackReleation> getResourcePackReleationByResourceId(
			String rid, boolean isIphone);

	/**
	 * 通过关键字搜索资源
	 * 
	 * @param resourceType
	 *            参见ResourceType中定义
	 * @param keyword
	 *            书名关键字 用于模糊匹配查询
	 * @param pageNum
	 * @param pageSize
	 * @param search
	 *            搜索控制条件 (如：按书名、按作者、按主人公等)
	 * @return
	 */
	// public List<ResourceAll> searchResult(Integer resourceType,String
	// search,String keyword,int pageNum,int pageSize);
	/**
	 * 关键字搜索
	 * 搜索结果按照排序规则排序
	 * @param resourceType
	 *            资源类型参见ResourceType中定义
	 * @param searchType
	 *            搜索类型1按书名和关键字 2按作者ID
	 * @param keyword
	 *            搜索关键字
	 * @param pageNum
	 * @param pageSize
	 * @param order 排序规则
	 * @return
	 */
	public List<ResourcePackReleation> searchResource(Integer resourceType,
			int searchType, String keyword, ResourcePack pack, int pageNum,
			int pageSize,int order);

	/**
	 * 通过关键字搜索资源数量
	 * 
	 * @param resourceType
	 * @param searchType
	 * @param keyword
	 * @return
	 */
	public int searchResourceCount(Integer resourceType, int searchType,
			String keyword, ResourcePack pack);

	/**
	 * 通过关键字搜索资源数量
	 * 
	 * @param resourceType
	 * @param keyword
	 * @return
	 */
	// public int searchResultCount(Integer resourceType,String keyword);
	// /**
	// * 按条件搜索作者列表
	// * @return 返回作者列表
	// */
	// public List<ResourceAuthor> searchResult(Collection expressions,int
	// pageNum,int pageSize);
	// /**
	// * 作者列表数量
	// * @return
	// */
	// public int searchResultCount(Collection expressions);
	public List<String> getResourceAuthorListByPackId(int pageNo, int pageSize,
			ResourcePack pack, Integer resourceType, String inital);

	public int getResourceAuthorListByPackIdCount(ResourcePack pack,
			Integer resourceType,String inital);

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
	 * 获得普通的图片,如作者封面图
	 * 
	 * @param img
	 * @return
	 */
	public String getNormalImg(String img);

	/**
	 * 获取素材图片
	 * 
	 * @param id
	 *            素材图片ID
	 * @return
	 */
	public String getMaterialImg(Integer id);

	/**
	 * 获取UEB文件地址
	 * 
	 * @param relId
	 * @return 数组0是 128格式的 1是176格式 2是240格式
	 */
	public String[] getUebAddress(int relId);

	/**
	 * 根据作者ID查询作者相关资源
	 * 
	 * @param resourceType
	 *            资源类型
	 * @param pack
	 *            批价包
	 * @param authorId
	 *            作者ID
	 * @return
	 */
	public List<ResourcePackReleation> getResourceByAuthorId(
			Integer resourceType, ResourcePack pack, String authorId,
			int pageNum, int pageSize);

	public int getResourceCountByAuthorId(Integer resourceType,
			ResourcePack pack, String authorId);

	/**
	 * 获得资源的点击量
	 * 
	 * @param resourceId
	 * @return
	 */
	public long getResourceVisits(String resourceId);

	/**
	 * 增加资源的点击量
	 * 
	 * @param resourceId
	 * @return
	 */
	public long incrResourceVisits(String resourceId);

	/**
	 * 获取所有用户点击量的资源
	 * 
	 * @return
	 */
	public Set<String> getAllResourceVisitKey();

	/**
	 * 点击排行 取某一批价包中前100条 进行分页显示
	 * 
	 * @return 返回批价包引用关系信息集合
	 */
	public List<ResourcePackReleation> getHotTopResourcesListOrderByDownnum(
			int packId, int pageNo, int pageSize, int type);

	public String getPreviewCoverImg(String resourceId, String coverImg,
			int size);

	/**
	 * 根据排序规则来获取批价包下的资源
	 * 
	 * @param packId
	 * @param pageNo
	 * @param pageSize
	 * @param listCount
	 *            总记录数
	 * @return
	 * 增加param参数 用于做结果筛选 modify by liuxh 091116
	 */
	public List<ResourcePackReleation> getResourcePackReleationsByOrder(
			int packId, int pageNo, int pageSize, int order, int listCount,int param);

	/***
	 * 
	 * @param packId
	 * @param order
	 * @param listCount
	 * @param param
	 * @return
	 * 增加param参数 用于做结果筛选 modify by liuxh 091116
	 */
	public int getResourcePackReleationsByOrderCount(int packId, int order,
			int listCount,int param);

	/**
	 * 搜索排行
	 * 
	 * @param packId
	 *            批价包
	 * @param type
	 *            排行参数 （1.月排行 2.周排行 3.日排行 0.总排行）
	 * @param pageNo
	 * @param pageSize
	 * @param listCount
	 *            最大条数
	 * @return
	 */
	public List<ResourcePackReleation> getSearchTopResourcesList(int packId,
			int type, int pageNo, int pageSize, int listCount);

	public int getSearchTopResourcesListCount(int packId, int listCount);

	/***
	 * 推荐相关内容查询
	 * 
	 * 
	 * @param ColumnsID
	 *            栏目ID
	 * @param resource
	 *            资源类
	 * @param size
	 *            显示数量
	 * @return
	 * @throws Exception
	 * @author penglei
	 */
	public List<ResourcePackReleation> findPertinence(String colomnID,
			ResourceAll resource, String size) throws Exception;

	/***
	 * 根据url过来的参数得到排序id
	 * 
	 * @param order
	 *            url 传来的一级参数
	 * @param orderSub
	 *            url传来的二级参数
	 * @return 最终排序id
	 * 
	 * @author penglei 2009-10-29 12:36:22
	 * 
	 * 
	 */
	public Integer getUrlOrderType(Integer order, Integer orderSub);

	/**
	 * 获取同类资源列表 规则：该分类点击排行前20本里随机显示X本，当一本图书属于多个类别时，选择格式为：N分类×20后随机显示X本
	 * 
	 * @param resourceId
	 * @param pageGroupId
	 * @return 返回批价引用关系列表集合
	 * @author liuxh 09-10-30
	 */
	public List<Map> findCategoryWideResource(String resourceId,
			int pageGroupId, int count);
/**
 * 根据资源ID得到相关的书部信息
 * @param colomnID 栏目ID
 * @param resource  当前资源
 * @return
 * @throws Exception
 * @author yuzs 
 * 2009-11-09
 */
	public List<ResourcePackReleation> findDivisions(String columnID,
			ResourceAll resource,boolean isNeed) throws Exception;
	/**
	 * 
	 * @param columnID
	 * @param resource
	 * @return
	 * @throws Exception
	 * @author liuxh 09-11-11
	 */
	public int getDivisionsCount(String columnID,ResourceAll resource,boolean isNeed) throws Exception;
	/**
	 * 根据资源ID得到相关的书部信息或期刊信息 带分页
	 * @param columnID
	 * @param resource
	 * @param pageNo		页码
	 * @param pageSize		页/条
	 * @param listCount		列表最大范围
	 * @param order			0.倒序 1.正序
	 * @return
	 * @throws Exception
	 * @author liuxh 09-11-11
	 */
	public List<ResourcePackReleation> findDivisions(String columnID,
			ResourceAll resource,int pageNo,int pageSize,int listCount,int order) throws Exception;

	
	
	/**
	 * 获取上一卷下一卷的卷 .isNotNext如果为false则是下一卷，如果为true是上一卷
	 * @param tomeId
	 * @param isNotNext
	 * @return
	 * @author liuxh 09-11-13
	 */
	public String browseResourceTome(String tomeId, boolean isNotNext);
	
	
	public List<VideoSuite> getVideoSuiteList(String resourceId);
	
	public int getVideoSuiteListCount(String resourceId);
	
	public String getResourceDirectory(String resourceId);
	
	public String getVideoResourceDirectory(String resourceId);
}
