package com.hunthawk.reader.pps.xhtml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.EbookChapterDesc;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.MagazineChapterDesc;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.NewsPapersChapterDesc;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.ChapterService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.FeeLogicService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;
import com.hunthawk.tag.vm.VmInstance;
/**
 * ֻ��Կͻ���
 * ��ǩ���ƣ�meta
 * ����˵�����޲���
 * 		���Ӳ��� wordset Ĭ���������� 
 * 		modify by liuxh 09-11-05
 * @author liuxh
 *
 */
public class MetaTag extends BaseTag {

	private ResourceService resourceService;
	private CustomService customService;
	private ChapterService chapterService;
	private FeeLogicService feeLogicService;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		//�õ��½�ID ��ѯ�½���Ϣ
		String chId=request.getParameter(ParameterConstants.CHAPTER_ID);
		if(chId==null || StringUtils.isEmpty(chId)){
			TagLogger.debug(tagName,"�½�IDΪ��", request.getQueryString(), null);
			return new HashMap();
		}
		int totalCapter=0;//�½�����
		int currentCapterID=0;//�½����
		int totalPage=0;//��ǰ�µ���ҳ��
		//
//		int word=request.getParameter(ParameterConstants.WORDAGE)==null?ParameterConstants.CHAPTER_CONTENT_WORD_SET:Integer.parseInt(request.getParameter(ParameterConstants.WORDAGE));
		// �õ����� ������
		int word =request.getParameter(ParameterConstants.WORDAGE)==null?(getIntParameter("wordset",-1)<0?ParameterConstants.CHAPTER_CONTENT_WORD_SET:getIntParameter("wordset",ParameterConstants.CHAPTER_CONTENT_WORD_SET))
				:Integer.parseInt(request.getParameter(ParameterConstants.WORDAGE)); 
		Object[] obj = getChapterService(request).getEbookChapterContent(
				chId, 1, word / 500);
		if (obj != null) {
			if(!chId.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){
				totalPage = Integer.parseInt(obj[0].toString());
			}
		} 
		String currTomeId="";
		EbookTome tome=null;
		MagazineChapterDesc mc=null;
		ResourceAll resource=getResourceService(request).getResource(URLUtil.getResourceId(request));
		if(chId.startsWith(String.valueOf(ResourceType.TYPE_BOOK))){//ͼ��
			Ebook e=(Ebook)resource;
			totalCapter=Integer.parseInt(getResourceService(request).getEbookChaptersByResourceIDCount(e.getId()).toString());//e.getListCount();
			//�����½�ID��ѯ�½���Ϣ
			EbookChapterDesc ec =getResourceService(request).getEbookChapterDesc(chId);	
			currentCapterID=ec.getChapterIndex();
			
		}else if(chId.startsWith(String.valueOf(ResourceType.TYPE_COMICS))){//����
			Comics c=(Comics)resource;
			totalCapter=getResourceService(request).getComicsChaptersByResourceIDCount(c.getId());//c.getListCount();
			ComicsChapter cc=getResourceService(request).getComicsChapterById(chId);
			currentCapterID=cc.getChapterIndex();
		}else if(chId.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){//��־
			/**
			 * modify by liuxh 09-11-13
			 */
			totalCapter=getResourceService(request).getEbookTomeCount(resource.getId());//�ܾ���
			/**
			 * end
			 */
			Magazine m=(Magazine)resource;
//			totalCapter=getResourceService(request).getMagazineChaptersByResourceIDCount(m.getId());//m.getListCount();
			mc=getResourceService(request).getMagazineChapterDescById(chId);
//			currentCapterID=mc.getChapterIndex();
			currTomeId=mc.getTomeId();
			tome=getResourceService(request).getEbookTomeById(currTomeId);
			currentCapterID=tome.getTomeIndex();
			if(StringUtils.isNotEmpty(currTomeId))
				totalPage=getResourceService(request).getMagazineChapterDescCountByTomeId(currTomeId);//��־Ϊ���½���
			
		}else if(chId.startsWith(String.valueOf(ResourceType.TYPE_NEWSPAPERS))){//��ֽ
			NewsPapers n=(NewsPapers)resource;
			totalCapter=getResourceService(request).getNewsPapersChaptersByResourceIDCount(n.getId());//n.getListCount();
			NewsPapersChapterDesc nc=getResourceService(request).getNewsPapersChapterDescById(chId);
			currentCapterID=nc.getChapterIndex();
		}
		
		String prevCapterUrl=getPrevCapterUrl( request, chId);//��һ��
		String nextCapterUrl=getNextCapterUrl(request, chId);//��һ��
		String lastCapterUrl=getLastCapterUrl(request);		//	���һ��
		String beginCapterUrl=getBeginCapterUrl(request);	//��һ��
		String nextContentUrl=""; //��һҳ
		String prevContentUrl=""; //��һҳ
		if(!chId.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){
			int currPn=request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request.getParameter(ParameterConstants.PAGE_NUMBER));
			currPn=currPn>totalPage?currPn=totalPage:currPn;
			if(currPn<totalPage && currPn>0){//
				nextContentUrl=getContentUrl(request,currPn+1,word,chId);//������һҳ
			}
			if(currPn>1){
				prevContentUrl=getContentUrl(request,currPn-1,word,chId);//������һҳ
			}
		}else{//��־��һҳ��Ϊ��һ��
			if(StringUtils.isNotEmpty(currTomeId)){
				List<MagazineChapterDesc> listChapters=getResourceService(request).getMagazineChapterDescByTomeId(currTomeId);
				if(mc.getChapterIndex()<listChapters.size() && mc.getChapterIndex()>0){
					String nextChapterMagazine=getResourceService(request).browseResourceChapter(chId, false);
					nextContentUrl=getContentUrl(request,1,word,nextChapterMagazine);//��һ��
				}
				if(mc.getChapterIndex()>1){
					String preChapterMagazine=getResourceService(request).browseResourceChapter(chId, true);
					prevContentUrl=getContentUrl(request,1,word,preChapterMagazine);//��һ�½�
				}
			}
		}
		
		StringBuilder builder=new StringBuilder();
		builder.append(" <meta name=\"currentCapterID\" content=\""+currentCapterID+"\"/>");
		builder.append("<meta name=\"totalCapter\" content=\""+totalCapter+"\" />");
		builder.append("<meta name=\"prevCapterUrl\" content=\""+prevCapterUrl+"\" />");
		builder.append("<meta name=\"nextCapterUrl\" content=\""+nextCapterUrl+"\" />");
		builder.append("<meta name=\"lastCapterUrl\" content=\""+lastCapterUrl+"\" />");
		builder.append("<meta name=\"beginCapterUrl\" content=\""+beginCapterUrl+"\" />");
		builder.append("<meta name=\"totalPage\" content=\""+totalPage+"\" />");
		builder.append("<meta name=\"prevContentUrl\" content=\""+prevContentUrl+"\" />");
		builder.append("<meta name=\"nextContentUrl\" content=\""+nextContentUrl+"\" />");
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), builder.toString());
		return resultMap;
	}
	/**������/��һҳ*/
	private String getContentUrl(HttpServletRequest request,int nextPn,int word,String chId){
		StringBuilder builder = new StringBuilder();
		builder.append(request.getContextPath());
		builder.append(ParameterConstants.PORTAL_PATH);
		builder.append("?");
		builder.append(ParameterConstants.PAGE);
		builder.append("=");
		builder.append(ParameterConstants.PAGE_DETAIL);
		builder.append("&");
		URLUtil.append(builder, ParameterConstants.PRODUCT_ID, request);
		URLUtil.append(builder, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(builder, ParameterConstants.AREA_ID, request);
		URLUtil.append(builder, ParameterConstants.COLUMN_ID, request);
		URLUtil.append(builder, ParameterConstants.FEE_BAG_ID, request);
		URLUtil.append(builder, ParameterConstants.FEE_BAG_RELATION_ID, request);
		if(!chId.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){
			URLUtil.append(builder, ParameterConstants.CHAPTER_ID, request);
		}else{//��־��Դ 
			builder.append(ParameterConstants.CHAPTER_ID);
			builder.append("=");
			builder.append(chId);
			builder.append("&");
		}
		URLUtil.append(builder, ParameterConstants.CHANNEL_ID, request);
		builder.append(ParameterConstants.PAGE_NUMBER);
		builder.append("=");
		builder.append(nextPn);
		if(word>0){
			builder.append("&");
			builder.append(ParameterConstants.WORDAGE);
			builder.append("=");
			builder.append(word);
		}
		builder.append("&");
		URLUtil.append(builder, ParameterConstants.FEE_ID, request);
		return builder.toString();
	}
	
	private String getMagazineChapterId(HttpServletRequest request,String ChapterId,boolean isNotNext){
		MagazineChapterDesc mc=getResourceService(request).getMagazineChapterDescById(ChapterId);
		String tomeId=mc.getTomeId();
		if(tomeId==null || StringUtils.isEmpty(tomeId))
			return "";
		String newTomeId=getResourceService(request).browseResourceTome(tomeId, isNotNext);
		if(StringUtils.isEmpty(newTomeId)){
			return "";
		}
		List listChapters=getResourceService(request).getMagazineChapterDescByTomeId(newTomeId);
		if(listChapters==null ||listChapters.size()==0)
			return "";
		return ((MagazineChapterDesc)listChapters.get(0)).getId();
	}
	/**
	 * ��һ������
	 * @param request
	 * @param chapterID ��ǰ�½�ID
	 * @param listCount �½�����
	 * @param isBuy  �û��Ƿ��ѹ���
	 * @return
	 */
	
	private String getPrevCapterUrl(HttpServletRequest request,String chapterID){
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		String resourceId=URLUtil.getResourceId(request);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		String preChapterId ="";
		if(chapterID.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){//��־��Դ
			preChapterId=getMagazineChapterId(request,chapterID,true);//��һ��ĵ�һ�½ڵ�ID
		}else{
			preChapterId = getResourceService(request).browseResourceChapter(chapterID, true);
		}
		if (StringUtils.isEmpty(preChapterId)) {//
			TagLogger.debug("MetaTag", "�Ѿ��ǵ�һ��  ����һ����Ϣ",
					request.getQueryString(), null);
			return "";
		}

		// �õ����۰����ù�ϵ��ID��ѯ�շ��½ڿ�����Ϣ
		String relId = request.getParameter(ParameterConstants.FEE_BAG_RELATION_ID);
		ResourcePackReleation rpl=null;
		if (relId != null && !"".equals(relId)) {
			 rpl = getResourceService(request).getResourcePackReleation(Integer.parseInt(relId));
		} else {
			TagLogger.debug("MetaTag", "���۰����ù�ϵIDΪ��", request.getQueryString(),
					null);
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		Map feeMap=getFeeLogicService(request).isFee(productId, resourceId, mobile, rpl, packId, month_fee_bag_id);
		/**�½ڿ��Ƶ�*/
		int choicePoint =rpl==null?0: rpl.getChoice()==null?0:rpl.getChoice();
		int chapterIndex=getChapterIndex(request,preChapterId,resourceId);
		if(feeMap==null || chapterIndex<choicePoint){
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
		}else{
			sb.append(feeMap.get("builder"));
		}
		
		sb.append(ParameterConstants.PAGE);
		sb.append("=");
		sb.append(ParameterConstants.PAGE_DETAIL);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
		URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
		sb.append(ParameterConstants.CHAPTER_ID);
		sb.append("=");
		sb.append(preChapterId);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append("1");
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.WORDAGE, request);
		if (StringUtils.isNotEmpty(request.getParameter(ParameterConstants.WORDAGE))) {
			sb.append("&");
			sb.append(ParameterConstants.WORDAGE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.WORDAGE));
		} 
		if(feeMap!=null){
			sb.append("&");
			sb.append(ParameterConstants.FEE_ID);
			sb.append("=");
			sb.append(feeMap.get("feeId"));
		}
		return sb.toString();
	}
	/**
	 * ��һ������
	 * @param request
	 * @param chapterID ��ǰ�½�ID
	 * @param listCount �½�����
	 * @param isBuy  �û��Ƿ��ѹ���
	 * @return
	 */
	private String getNextCapterUrl(HttpServletRequest request,String chapterID){
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		String resourceId=URLUtil.getResourceId(request);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		String currChapter = request.getParameter(ParameterConstants.CHAPTER_ID);
		String nextChapterId ="";
		if(chapterID.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){
			nextChapterId=getMagazineChapterId(request,chapterID,false);
		}else{
			nextChapterId= getResourceService(request).browseResourceChapter(currChapter, false);
		}
		if (StringUtils.isEmpty(nextChapterId)) {// �Ѿ������һ�� û����һ����Ϣ
			return "";
		}
		ResourcePackReleation rpl=null;
		// �õ����۰����ù�ϵ��ID��ѯ�շ��½ڿ�����Ϣ
		String relId = request.getParameter(ParameterConstants.FEE_BAG_RELATION_ID);
		if (relId != null && !"".equals(relId)) {
			 rpl = getResourceService(request).getResourcePackReleation(Integer.parseInt(relId));
		} else {
			TagLogger.debug("Meta", "���۰����ù�ϵIDΪ��", request.getQueryString(),null);
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		Map feeMap=getFeeLogicService(request).isFee(productId, resourceId, mobile, rpl, packId, month_fee_bag_id);
		/**�½ڿ��Ƶ�*/
		int choicePoint=rpl==null?0:rpl.getChoice()==null?0:rpl.getChoice();
		int chapterIndex=getChapterIndex(request,nextChapterId,resourceId);
		if(feeMap==null || chapterIndex<choicePoint){
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
		}else{
			sb.append(feeMap.get("builder"));
		}
		sb.append(ParameterConstants.PAGE);
		sb.append("=");
		sb.append(ParameterConstants.PAGE_DETAIL);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
		URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
		sb.append(ParameterConstants.CHAPTER_ID);
		sb.append("=");
		sb.append(nextChapterId);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append("1");
		if (StringUtils.isNotEmpty(request.getParameter(ParameterConstants.WORDAGE))) {
			sb.append("&");
			sb.append(ParameterConstants.WORDAGE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.WORDAGE));
		} 
		if(feeMap!=null){
			sb.append("&");
			sb.append(ParameterConstants.FEE_ID);
			sb.append("=");
			sb.append(feeMap.get("feeId"));
		}
		return sb.toString();
	}
	/**
	 * ��һ������
	 * @param request
	 * @return
	 */
	private String getBeginCapterUrl(HttpServletRequest request){
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		String resourceId=URLUtil.getResourceId(request);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
//		String firstId=URLUtil.getResourceId(request)+"001";
		String firstId="";/**��һ�µ��½�ID*/
		Map map=getChapterList(request,resourceId,true);
		if(map!=null){
			firstId=map.get("first").toString();
		}else{
			TagLogger.debug("MetaTag", "��ȡ��һ���½�IDʧ��", request.getQueryString(), null);
			return "";
		}
		String relId = request.getParameter(ParameterConstants.FEE_BAG_RELATION_ID);
		ResourcePackReleation rpl=null;
		if (relId != null && !"".equals(relId)) {
			 rpl = getResourceService(request)
					.getResourcePackReleation(Integer.parseInt(relId));
		} else {
			TagLogger.debug("MetaTag", "���۰����ù�ϵIDΪ��",request.getQueryString(), null);
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		Map feeMap=getFeeLogicService(request).isFee(productId, resourceId, mobile, rpl, packId, month_fee_bag_id);
		/**�½ڿ��Ƶ�*/
		int choicePoint =rpl==null?0: rpl.getChoice()==null?0:rpl.getChoice();
		int chapterIndex=getChapterIndex(request,firstId,resourceId);
		if(feeMap==null || chapterIndex<choicePoint){
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
		}else{
			sb.append(feeMap.get("builder"));
		}
		sb.append(ParameterConstants.PAGE);
		sb.append("=");
		sb.append(ParameterConstants.PAGE_DETAIL);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
		URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
		sb.append(ParameterConstants.CHAPTER_ID);
		sb.append("=");
		sb.append(firstId);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append("1");
		if (StringUtils.isNotEmpty(request.getParameter(ParameterConstants.WORDAGE))) {
			sb.append("&");
			sb.append(ParameterConstants.WORDAGE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.WORDAGE));
		} 
		if(feeMap!=null){
			sb.append("&");
			sb.append(ParameterConstants.FEE_ID);
			sb.append("=");
			sb.append(feeMap.get("feeId"));
		}
		return sb.toString();
	}
	/**
	 * ��ȡ��һ�½ں����һ�µ�ID
	 * @param resourceId
	 * @return
	 */
	private Map<String,String> getChapterList(HttpServletRequest request,String resourceId,boolean isFirst){
		Map<String,String> map=new HashMap<String,String>();
		if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_BOOK))){
			List<EbookChapterDesc> ebookchapters = getResourceService(request).getEbookChaptersByResourceID(resourceId,1,1000);
			if(ebookchapters.size()==0){
				return null;
			}else{
				map.put("first", ebookchapters.get(0).getId());
				map.put("last", ebookchapters.get(ebookchapters.size()-1).getId());
			}
		}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_COMICS))){
			List<ComicsChapter> comics = getResourceService(request).getComicsChaptersByResourceId(resourceId);
			if(comics.size()==0){
				return null;
			}else{
				map.put("first", comics.get(0).getId());
				map.put("last", comics.get(comics.size()-1).getId());
			}
		}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){
			/**
			 * ��һ������һ��ĵ�һ�½ڵ�ID
			 * modify by liuxh 09-11-13
			 */
			List<EbookTome> tomes=getResourceService(request).getEbookTomeByResourceId(resourceId, 1, Integer.MAX_VALUE);
			if(tomes==null || tomes.size()==0){
				return null;
			}else{
				if(isFirst){
					List<MagazineChapterDesc> magazinesFirst=getResourceService(request).getMagazineChapterDescByTomeId(tomes.get(0).getId());
					if(magazinesFirst==null || magazinesFirst.size()==0){
						return null;
					}
					map.put("first", magazinesFirst.get(0).getId());
				}else{
					List<MagazineChapterDesc> magazinesLast=getResourceService(request).getMagazineChapterDescByTomeId(tomes.get(tomes.size()-1).getId());
					if(magazinesLast==null || magazinesLast.size()==0){
						return null;
					}
					map.put("last", magazinesLast.get(0).getId());
				}
			}
			/**
			 * end
			 */
			List<MagazineChapterDesc> mcd = getResourceService(request).getMagazineChaptersByResourceID(resourceId);
			if(mcd.size()==0){
				return null;
			}else{
				map.put("first", mcd.get(0).getId());
				map.put("last", mcd.get(mcd.size()-1).getId());
			}
		}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_NEWSPAPERS))){
			List<NewsPapersChapterDesc> npcd=getResourceService(request).getNewsPapersChaptersByResourceID(resourceId);
			if(npcd.size()==0){
				return null;
			}else{
				map.put("first", npcd.get(0).getId());
				map.put("last", npcd.get(npcd.size()-1).getId());
			}
		}
		
		return map;
	}
	/**
	 * ��ȡ�ض��½ڵ��½����
	 * @param request
	 * @param chapterId
	 * @param resourceId
	 * @return
	 */
	private int getChapterIndex(HttpServletRequest request,String chapterId,String resourceId){
		int chapterIndex=-1;
		if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_BOOK))){
			EbookChapterDesc ec = getResourceService(request).getEbookChapterDesc(chapterId);
			chapterIndex=ec.getChapterIndex();
		}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_COMICS))){
			ComicsChapter comics = getResourceService(request).getComicsChapterById(chapterId);
			chapterIndex=comics.getChapterIndex();
		}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){
			MagazineChapterDesc mcd = getResourceService(request).getMagazineChapterDescById(chapterId);
			chapterIndex=mcd.getChapterIndex();
		}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_NEWSPAPERS))){
			NewsPapersChapterDesc npcd=getResourceService(request).getNewsPapersChapterDescById(chapterId);
			chapterIndex=npcd.getChapterIndex();
		}
		return chapterIndex;
	}
	/**���һ������*/
	private String getLastCapterUrl(HttpServletRequest request){
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		String resourceId=URLUtil.getResourceId(request);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		String lastId="";/**���һ�µ��½�ID*/
		Map map=getChapterList(request,resourceId,false);
		if(map!=null){
			lastId=map.get("last").toString();
		}else{
			TagLogger.debug("MetaTag", "��ȡ���һ���½�IDʧ��", request.getQueryString(), null);
			return "";
		}
		String relId = request.getParameter(ParameterConstants.FEE_BAG_RELATION_ID);
		ResourcePackReleation rpl=null;
		if (relId != null && !"".equals(relId)) {
			 rpl = getResourceService(request).getResourcePackReleation(Integer.parseInt(relId));
		} else {
			TagLogger.debug("MetaTag", "���۰����ù�ϵIDΪ��",
					request.getQueryString(), null);
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		Map feeMap=getFeeLogicService(request).isFee(productId, resourceId, mobile, rpl, packId, month_fee_bag_id);
		/**�½ڿ��Ƶ�*/
		int choicePoint =rpl==null?0: rpl.getChoice()==null?0:rpl.getChoice();
		int chapterIndex=getChapterIndex(request,lastId,resourceId);
		if(feeMap==null || chapterIndex<choicePoint){
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
		}else{
			sb.append(feeMap.get("builder"));
		}
		sb.append(ParameterConstants.PAGE);
		sb.append("=");
		sb.append(ParameterConstants.PAGE_DETAIL);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
		URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
		sb.append(ParameterConstants.CHAPTER_ID);
		sb.append("=");
		sb.append(lastId);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append("1");
		sb.append("&");
		if (StringUtils.isNotEmpty(request.getParameter(ParameterConstants.WORDAGE))) {
			sb.append("&");
			sb.append(ParameterConstants.WORDAGE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.WORDAGE));
		} 
		if(feeMap!=null){
			sb.append("&");
			sb.append(ParameterConstants.FEE_ID);
			sb.append("=");
			sb.append(feeMap.get("feeId"));
		}
		return sb.toString();
	}
	private ResourceService getResourceService(HttpServletRequest request) {
		if (resourceService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			resourceService = (ResourceService) wac.getBean("resourceService");
		}
		return resourceService;
	}
	private CustomService getCustomService(HttpServletRequest request) {
		if (customService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			customService = (CustomService) wac.getBean("customService");
		}
		return customService;
	}
	private ChapterService getChapterService(HttpServletRequest request) {
		if (chapterService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			chapterService = (ChapterService) wac.getBean("chapterService");
		}
		return chapterService;
	}
	private FeeLogicService getFeeLogicService(HttpServletRequest request) {
		if (feeLogicService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			feeLogicService = (FeeLogicService) wac.getBean("feeLogicService");
		}
		return feeLogicService;
	}
}
