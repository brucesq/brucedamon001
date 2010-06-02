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
	 * ��ȡ��Դ����,�����ͼ�鷵��ebook����,��������comics,��־����magazine,��ֽ����newspaper����,��������ڷ���null
	 * .
	 * 
	 * @param rid
	 * @return
	 */
	public ResourceAll getResource(String rid);

	/**
	 * ��ȡ���۰�����
	 * 
	 * @param packId
	 * @return
	 */
	public ResourcePack getResourcePack(int packId);

	public ResourcePackReleation getResourcePackReleation(int relId);

	/**
	 * ��ѯ����ʵ��
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
	 * ��ҳ��ʾ���� ����һ���ӵ�N����ʼ����M�������ļ�¼���� ��ÿҳ��ʾM����¼
	 * 
	 * @param pageHql
	 *            ��̬HQL���
	 * @param firstResult
	 *            ���Կ������α���ʼλ�� ��1���Ļ�ֵΪ0
	 * @param MaxResults
	 *            ���Կ�����ÿҳ��ʾ��������¼
	 * @return LIST ����һ���ӵ�N����ʼ����M�������ļ�¼����
	 * @author add by liuxh
	 */
	// public List<ResourcePackReleation> getRollResourceBagRelations(Class cls,
	// int firstResult, int MaxResults,Object[] objs);
	/** �õ����۰�����Դ���� */
	public Long getResourcePackReleationsCount(int packId);

	/**
	 * �����½�ID��ȡ��Դ�½ڶ���
	 * 
	 * @param chapterId
	 * @return
	 */
	public Object getResourceChapter(String chapterId);

	/**
	 * ��ȡ��ԴID�������½ڻ�����Ϣ
	 * 
	 * @param rid
	 * @return
	 */
	public List<EbookChapterDesc> getEbookChapterDescsByResourceID(String rid);

	/**
	 * ��ȡ�����½���Ϣ
	 * 
	 * @param chapterId
	 * @return
	 */
	public EbookChapterDesc getEbookChapterDesc(String chapterId);

	// /** ������ID���������Դ��Ϣ add by liuxh 09-7-28 */
	// public List<ResourceAll> getResourcesByAuthor(String authorid,int pageNo,
	// int pageSize);
	// //public List<ResourceAll> getResourcesAllByAuthor(String authorid);
	// public int getResourcesAllByAuthor(String authorid) ;

	/** ������ԴID��ѯ�½��б� */
	public List<EbookChapterDesc> getEbookChaptersByResourceID(String rid,
			int pageNo, int pageSize);

	public Long getEbookChaptersByResourceIDCount(String rid);

	/**
	 * ������ԴID��ȡ��־�������½���������
	 * 
	 * @param rid
	 * @return
	 */
	public List<MagazineChapterDesc> getMagazineChaptersByResourceID(String rid);

	/**
	 * ������ԴID��ȡ��־�½ڷ�ҳ�б�
	 * 
	 * @param rid
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<MagazineChapterDesc> getMagazineChaptersByResourceID(
			String rid, int pageNo, int pageSize);

	/**
	 * ��ȡ��־�½�����
	 * 
	 * @param rid
	 * @return
	 */
	public int getMagazineChaptersByResourceIDCount(String rid);

	/**
	 * ��ȡ��־�½ڶ���
	 * 
	 * @param chapterId
	 * @return
	 */
	public MagazineChapterDesc getMagazineChapterDescById(String chapterId);

	/**
	 * ������ԴID��ȡ�����½���Ϣ
	 * 
	 * @param rid
	 * @return
	 */
	public List<ComicsChapter> getComicsChaptersByResourceId(String rid);

	/**
	 * ������ԴID��ȡ������ҳ�б�
	 * 
	 * @param rid
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<ComicsChapter> getComicsChaptersByResourceId(String rid,
			int pageNo, int pageSize);

	/**
	 * ��ȡ�����½�����
	 * 
	 * @param rid
	 * @return
	 */
	public int getComicsChaptersByResourceIDCount(String rid);

	/**
	 * ��ȡ�����½ڶ���
	 * 
	 * @param chapterId
	 * @return
	 */
	public ComicsChapter getComicsChapterById(String chapterId);

	/**
	 * ���ݾ�ID��ȡ�����½��б�
	 * 
	 * @param tomeId
	 * @return
	 */
	public List<ComicsChapter> getComicsChapterByTomeId(String tomeId);

	public int getComicsChapterCountByTomeId(String tomeId);

	public List<ComicsChapter> getComicsChapterByTomeId(String tomeId,
			int pageNo, int pageSize);

	/**
	 * ���ݾ�ID��ȡ��ֽ�½��б�
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
	 * ���ݾ�ID��ȡ��־�½��б�
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
	 * ���ݾ�ID��ȡͼ���½��б�
	 * 
	 * @param tomeId
	 * @return
	 */
	public List<EbookChapterDesc> getEbookChapterDescByTomeId(String tomeId);

	public int getEbookChapterDescCountByTomeId(String tomeId);

	public List<EbookChapterDesc> getEbookChapterDescByTomeId(String tomeId,
			int pageNo, int pageSize);

	/**
	 * ������ԴID��ȡ��ֽ�������½���������
	 * 
	 * @param rid
	 * @return
	 */
	public List<NewsPapersChapterDesc> getNewsPapersChaptersByResourceID(
			String rid);

	/**
	 * ������ԴID��ȡ��ֽ�½ڷ�ҳ�б�
	 * 
	 * @param rid
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<NewsPapersChapterDesc> getNewsPapersChaptersByResourceID(
			String rid, int pageNo, int pageSize);

	/**
	 * ��ȡ��ֽ�½�����
	 * 
	 * @param rid
	 * @return
	 */
	public int getNewsPapersChaptersByResourceIDCount(String rid);

	/**
	 * ��ȡ��ֽ�½ڶ���
	 * 
	 * @param chapterId
	 * @return
	 */
	public NewsPapersChapterDesc getNewsPapersChapterDescById(String chapterId);

	/**
	 * ��ȡ��Դ�ľ��б� ���ӷ�ҳ��ز���
	 * 
	 * @param rid
	 * @return 
	 * modify by liuxh 09-11-05
	 */
	public List<EbookTome> getEbookTomeByResourceId(String rid,int pageNo,int pageSize);

	/**
	 * ��ȡ�����
	 * 
	 * @param tomeId
	 * @return
	 */
	public EbookTome getEbookTomeById(String tomeId);

	/**
	 * ��ȡ��Դ������
	 * 
	 * @param rid
	 * @return
	 */
	public int getEbookTomeCount(String rid);

	/** ������ԴID������Դ����ID ���ص���һ������ */
	public List<Integer> getResourceTypeID(String rid);

	/**
	 * ������Դ���ID��ȡ��ԴID����
	 * 
	 * @param typeId
	 *            ���ID
	 * @param pageNo
	 *            ��ǰҳ
	 * @param pageSize
	 *            ÿҳ��ʾ����
	 * @return ���ص���һ����ԴID����
	 */
	// public List<ResourceAll> getResourceByTypeId(int typeId,int pageNo,int
	// pageSize);
	/**
	 * ��ȡ���������Դ����Ŀ
	 * 
	 * @param typeId
	 * @return
	 */
	// public int getResourceByTypeIdCount(int typeId);
	/** ������Դ����ID��ѯ��Դ������Ϣ */
	public ResourceType getResourceType(int id);

	/** ��������ID��ѯ������Ϣ */
	public ResourceAuthor getResourceAuthor(int id);

	/**
	 * ��ȡ��һ����һ�µ��½�ID .isNotNext���Ϊfalse������һ�£����Ϊtrue����һ��
	 * 
	 * @param chapterId
	 *            ��ǰ�½�ID
	 * @param isNotNext
	 * @return
	 */
	public String browseResourceChapter(String chapterId, boolean isNotNext);

	/**
	 * ��ȡ���۰���������һ��,��һ��.isNotNext���Ϊfalse������һ�������Ϊtrue����һ��
	 * 
	 * @param relId
	 *            ���۰�����ID
	 * @param isNext
	 * @return ��������ڷ���null
	 */
	public ResourcePackReleation browseResourcePackReleation(int relId,
			boolean isNotNext);

	/**
	 * ͨ����ԴID��ȡ����������۰�������Ϣ
	 * 
	 * @param rid
	 * @return
	 */
	public List<ResourcePackReleation> getResourcePackReleationByResourceId(
			String rid, boolean isIphone);

	/**
	 * ͨ���ؼ���������Դ
	 * 
	 * @param resourceType
	 *            �μ�ResourceType�ж���
	 * @param keyword
	 *            �����ؼ��� ����ģ��ƥ���ѯ
	 * @param pageNum
	 * @param pageSize
	 * @param search
	 *            ������������ (�磺�������������ߡ������˹���)
	 * @return
	 */
	// public List<ResourceAll> searchResult(Integer resourceType,String
	// search,String keyword,int pageNum,int pageSize);
	/**
	 * �ؼ�������
	 * ����������������������
	 * @param resourceType
	 *            ��Դ���Ͳμ�ResourceType�ж���
	 * @param searchType
	 *            ��������1�������͹ؼ��� 2������ID
	 * @param keyword
	 *            �����ؼ���
	 * @param pageNum
	 * @param pageSize
	 * @param order �������
	 * @return
	 */
	public List<ResourcePackReleation> searchResource(Integer resourceType,
			int searchType, String keyword, ResourcePack pack, int pageNum,
			int pageSize,int order);

	/**
	 * ͨ���ؼ���������Դ����
	 * 
	 * @param resourceType
	 * @param searchType
	 * @param keyword
	 * @return
	 */
	public int searchResourceCount(Integer resourceType, int searchType,
			String keyword, ResourcePack pack);

	/**
	 * ͨ���ؼ���������Դ����
	 * 
	 * @param resourceType
	 * @param keyword
	 * @return
	 */
	// public int searchResultCount(Integer resourceType,String keyword);
	// /**
	// * ���������������б�
	// * @return ���������б�
	// */
	// public List<ResourceAuthor> searchResult(Collection expressions,int
	// pageNum,int pageSize);
	// /**
	// * �����б�����
	// * @return
	// */
	// public int searchResultCount(Collection expressions);
	public List<String> getResourceAuthorListByPackId(int pageNo, int pageSize,
			ResourcePack pack, Integer resourceType, String inital);

	public int getResourceAuthorListByPackIdCount(ResourcePack pack,
			Integer resourceType,String inital);

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
	 * �����ͨ��ͼƬ,�����߷���ͼ
	 * 
	 * @param img
	 * @return
	 */
	public String getNormalImg(String img);

	/**
	 * ��ȡ�ز�ͼƬ
	 * 
	 * @param id
	 *            �ز�ͼƬID
	 * @return
	 */
	public String getMaterialImg(Integer id);

	/**
	 * ��ȡUEB�ļ���ַ
	 * 
	 * @param relId
	 * @return ����0�� 128��ʽ�� 1��176��ʽ 2��240��ʽ
	 */
	public String[] getUebAddress(int relId);

	/**
	 * ��������ID��ѯ���������Դ
	 * 
	 * @param resourceType
	 *            ��Դ����
	 * @param pack
	 *            ���۰�
	 * @param authorId
	 *            ����ID
	 * @return
	 */
	public List<ResourcePackReleation> getResourceByAuthorId(
			Integer resourceType, ResourcePack pack, String authorId,
			int pageNum, int pageSize);

	public int getResourceCountByAuthorId(Integer resourceType,
			ResourcePack pack, String authorId);

	/**
	 * �����Դ�ĵ����
	 * 
	 * @param resourceId
	 * @return
	 */
	public long getResourceVisits(String resourceId);

	/**
	 * ������Դ�ĵ����
	 * 
	 * @param resourceId
	 * @return
	 */
	public long incrResourceVisits(String resourceId);

	/**
	 * ��ȡ�����û����������Դ
	 * 
	 * @return
	 */
	public Set<String> getAllResourceVisitKey();

	/**
	 * ������� ȡĳһ���۰���ǰ100�� ���з�ҳ��ʾ
	 * 
	 * @return �������۰����ù�ϵ��Ϣ����
	 */
	public List<ResourcePackReleation> getHotTopResourcesListOrderByDownnum(
			int packId, int pageNo, int pageSize, int type);

	public String getPreviewCoverImg(String resourceId, String coverImg,
			int size);

	/**
	 * ���������������ȡ���۰��µ���Դ
	 * 
	 * @param packId
	 * @param pageNo
	 * @param pageSize
	 * @param listCount
	 *            �ܼ�¼��
	 * @return
	 * ����param���� ���������ɸѡ modify by liuxh 091116
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
	 * ����param���� ���������ɸѡ modify by liuxh 091116
	 */
	public int getResourcePackReleationsByOrderCount(int packId, int order,
			int listCount,int param);

	/**
	 * ��������
	 * 
	 * @param packId
	 *            ���۰�
	 * @param type
	 *            ���в��� ��1.������ 2.������ 3.������ 0.�����У�
	 * @param pageNo
	 * @param pageSize
	 * @param listCount
	 *            �������
	 * @return
	 */
	public List<ResourcePackReleation> getSearchTopResourcesList(int packId,
			int type, int pageNo, int pageSize, int listCount);

	public int getSearchTopResourcesListCount(int packId, int listCount);

	/***
	 * �Ƽ�������ݲ�ѯ
	 * 
	 * 
	 * @param ColumnsID
	 *            ��ĿID
	 * @param resource
	 *            ��Դ��
	 * @param size
	 *            ��ʾ����
	 * @return
	 * @throws Exception
	 * @author penglei
	 */
	public List<ResourcePackReleation> findPertinence(String colomnID,
			ResourceAll resource, String size) throws Exception;

	/***
	 * ����url�����Ĳ����õ�����id
	 * 
	 * @param order
	 *            url ������һ������
	 * @param orderSub
	 *            url�����Ķ�������
	 * @return ��������id
	 * 
	 * @author penglei 2009-10-29 12:36:22
	 * 
	 * 
	 */
	public Integer getUrlOrderType(Integer order, Integer orderSub);

	/**
	 * ��ȡͬ����Դ�б� ���򣺸÷���������ǰ20���������ʾX������һ��ͼ�����ڶ�����ʱ��ѡ���ʽΪ��N�����20�������ʾX��
	 * 
	 * @param resourceId
	 * @param pageGroupId
	 * @return �����������ù�ϵ�б���
	 * @author liuxh 09-10-30
	 */
	public List<Map> findCategoryWideResource(String resourceId,
			int pageGroupId, int count);
/**
 * ������ԴID�õ���ص��鲿��Ϣ
 * @param colomnID ��ĿID
 * @param resource  ��ǰ��Դ
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
	 * ������ԴID�õ���ص��鲿��Ϣ���ڿ���Ϣ ����ҳ
	 * @param columnID
	 * @param resource
	 * @param pageNo		ҳ��
	 * @param pageSize		ҳ/��
	 * @param listCount		�б����Χ
	 * @param order			0.���� 1.����
	 * @return
	 * @throws Exception
	 * @author liuxh 09-11-11
	 */
	public List<ResourcePackReleation> findDivisions(String columnID,
			ResourceAll resource,int pageNo,int pageSize,int listCount,int order) throws Exception;

	
	
	/**
	 * ��ȡ��һ����һ��ľ� .isNotNext���Ϊfalse������һ�����Ϊtrue����һ��
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
