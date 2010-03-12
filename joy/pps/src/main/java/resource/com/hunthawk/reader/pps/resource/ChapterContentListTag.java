package com.hunthawk.reader.pps.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.bussiness.Template;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.EbookChapterDesc;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.MagazineChapterDesc;
import com.hunthawk.reader.domain.resource.NewsPapersChapterDesc;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.ChapterService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.FeeLogicService;
import com.hunthawk.reader.pps.service.KeyWordService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.reader.tag.util.Navigator;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.process.Redirect;
import com.hunthawk.tag.util.ParamUtil;

/**
 * �½������б��ǩ 
 * ��ǩ���ƣ�chapter_content_list
 * @author liuxh
 * modify by liuxh  :�����˸����û����ʼ�¼��ַ �˹��ܷ����� �����ϴο�����ǩ
 * ���ӱ�ǩ���� wordset ->�����½�����Ĭ������  add by liuxh 09-11-03
 * 
 */
public class ChapterContentListTag extends BaseTag {

	/** ����ʾ��ҳ��ص����� */
	// private boolean noPageLink;
	/** ��ǰҳ */
	private int currentPage;
	private ChapterService chapterService;
	private ResourceService resourceService;
	private CustomService customService;
	private FeeLogicService feeLogicService;
	private BussinessService bussinessService;
	private KeyWordService keyWordService;
	
	
	private boolean isComics;

	public boolean isComics() {
		return isComics;
	}

	public void setComics(boolean isComics) {
		this.isComics = isComics;
	}

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
			
		}else{
			tagTem = null;
		}
		
		String mobile=RequestUtil.getMobile();
		String rid=URLUtil.getResourceId(request);
		/**ȡ�������ù�ϵID */
		int relId= ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_RELATION_ID, -1);
		if(relId<0){
			/**�ж��Ƿ����˴���Դ*/
			boolean isBuy=getCustomService(request).isUserBuyBook(mobile, rid);
			if(!isBuy){
				int templateId=-1;
				String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
				Product pro=getBussinessService(request).getProduct(productId);
				String params[]=getBussinessService(request).getVariables("page_error").getValue().split(";");
				for(int i=0;i<params.length;i++){
					Template tmpl = getBussinessService(request).getTemplate(Integer.parseInt(params[i]));
					if(pro.getShowType().equals(tmpl.getShowType())){
						if(pro.getShowType()==1){//wap  
							 int wapType = RequestUtil.getNeedWapType();
							 if(wapType==tmpl.getSignType()){
								 templateId=tmpl.getId();
								 break;
							 }
						}
						templateId=tmpl.getId();
						break;
					}
				}
				if(templateId<0)
					return new HashMap();
				StringBuilder builder=new StringBuilder();
				builder.append(request.getContextPath());
				builder.append(ParameterConstants.PORTAL_PATH);
				builder.append("?");
				builder.append(ParameterConstants.TEMPLATE_ID);
				builder.append("=");
				builder.append(templateId);
				Redirect.sendRedirect(builder.toString()) ;
			}
		}
//		else{
			String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
			//�ж���Դ����   ��ȡ�½�����
			int chapterTotalCount=0;
			if(rid.startsWith(String.valueOf(ResourceType.TYPE_BOOK))){
				chapterTotalCount=Integer.parseInt(getResourceService(request).getEbookChaptersByResourceIDCount(rid).toString());
			}else if(rid.startsWith(String.valueOf(ResourceType.TYPE_COMICS))){
				chapterTotalCount=getResourceService(request).getComicsChaptersByResourceIDCount(rid);
			}else if(rid.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){
				chapterTotalCount=getResourceService(request).getMagazineChaptersByResourceIDCount(rid);
			}else if(rid.startsWith(String.valueOf(ResourceType.TYPE_NEWSPAPERS))){
				chapterTotalCount=getResourceService(request).getNewsPapersChaptersByResourceIDCount(rid);
			}
			ResourcePackReleation rel = null;
			if (ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_RELATION_ID, -1)!= -1) {
				rel = getResourceService(request).getResourcePackReleation(ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_RELATION_ID, -1));
			}
			int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
			int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
			
			//��ǰ��URL
			StringBuilder currentUrl=new StringBuilder();
			currentUrl.append(request.getContextPath());
			currentUrl.append(ParameterConstants.PORTAL_PATH);
			currentUrl.append("?");
			currentUrl.append(request.getQueryString());
			/**�����û����ʼ�¼��ַ*/
			try{
				getCustomService(request).updateUserFootprint(RequestUtil.getMobile(), rid,currentUrl.toString(),(productId==null?"0":productId));
			}catch(Exception ex){
				ex.printStackTrace();
				TagLogger.debug(tagName, "������ʷ��¼ʧ��:"+ex.getMessage(), request.getQueryString(), null);
			}
			
			
			
			/**�ж��Ƿ�Ʒ�*/
			Map feeMap=getFeeLogicService(request).isFee(productId, rid, mobile, rel, packId, month_fee_bag_id);
			if(feeMap!=null){//�����Ʒ�ҳ
				/**��ǰ�½�ID*/
				String chapterId=request.getParameter(ParameterConstants.CHAPTER_ID);
				int choicePoint = (rel.getChoice()==null || "".equals(rel.getChoice()))?0:rel.getChoice();
				int index=-1;
				if(rid.startsWith(String.valueOf(ResourceType.TYPE_BOOK))){//ͼ��
					EbookChapterDesc ebookChapter = getResourceService(request).getEbookChapterDesc(chapterId);
					index=ebookChapter.getChapterIndex();
				}else if(rid.startsWith(String.valueOf(ResourceType.TYPE_BOOK))){//����
					ComicsChapter comics=getResourceService(request).getComicsChapterById(chapterId);
					index=comics.getChapterIndex();
				}else if(rid.startsWith(String.valueOf(ResourceType.TYPE_BOOK))){//��ֽ
					NewsPapersChapterDesc newspaper=getResourceService(request).getNewsPapersChapterDescById(chapterId);
					index=newspaper.getChapterIndex();
				}else if(rid.startsWith(String.valueOf(ResourceType.TYPE_BOOK))){//��־
					MagazineChapterDesc magazine=getResourceService(request).getMagazineChapterDescById(chapterId);
					EbookTome tome=getResourceService(request).getEbookTomeById(magazine.getTomeId());
					index=tome.getTomeIndex();
				}
				
				/**�жϵ�ǰ�½��Ƿ��ڼƷѵ���*/
				if(index>=choicePoint){
					StringBuilder feeUrl=new StringBuilder();
					feeUrl.append(request.getRequestURL().substring(0,request.getRequestURL().indexOf(request.getRequestURI())));
					feeUrl.append(request.getContextPath());
					feeUrl.append(feeMap.get("builder"));
					feeUrl.append(request.getQueryString());
					Redirect.sendRedirect(feeUrl.toString()) ;
				}
			}
			if(rid.startsWith("4")){//����
				this.isComics=true;
				//�ж��Ƿ��Զ�����
				boolean isAuto=Integer.parseInt(request.getParameter(ParameterConstants.AUTO_PLAY)==null?"-1":request.getParameter(ParameterConstants.AUTO_PLAY))>0;//Ĭ���ֶ�
				ComicsChapter comicsChapter=getResourceService(request).getComicsChapterById(request.getParameter(ParameterConstants.CHAPTER_ID));
				String [] imgs=comicsChapter.getImages();
				int pageNumber = 1;// ��ǰҳ
				try {
					pageNumber = Integer.parseInt(request
							.getParameter(ParameterConstants.PAGE_NUMBER));
				} catch (Exception ex) {
					TagLogger.debug(tagName,
							"����ԭ��=currentPageת����ʱ�쳣",
							request.getQueryString(), ex);
				}
				int totalPage=imgs.length;
				if(isAuto){
					int nextPageNum=Integer.parseInt(request.getParameter(ParameterConstants.PAGE_NUMBER))+1;
					int timer=request.getParameter(ParameterConstants.TIMER)==null?2:Integer.parseInt(request.getParameter(ParameterConstants.TIMER));//getIntParameter("timer",2);//���ż��ʱ��   Ĭ��2����
					System.out.println(timer+"<------->timer");
					if(timer<1){
						timer=2;
					}
					StringBuilder builder=new StringBuilder();
					builder.append(request.getContextPath());
					builder.append(ParameterConstants.PORTAL_PATH);
					builder.append("?");
					builder.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.PAGE_NUMBER));
					builder.append("&");
					builder.append(ParameterConstants.PAGE_NUMBER);
					builder.append("=");
					builder.append(nextPageNum==totalPage?totalPage:nextPageNum);
					String targetUrl=builder.toString();
					if(nextPageNum<totalPage+1){
						com.hunthawk.tag.process.Refresh.pageRefresh(timer,targetUrl) ;
					}else{//ȥ����һ��
						//�õ���ǰ�½�ID
						String chapId=request.getParameter(ParameterConstants.CHAPTER_ID);
						int nextChap=Integer.parseInt(chapId.substring((chapId.length()-3),chapId.length()))+1;//��ȡ�����λ
						if(nextChap<chapterTotalCount+1){
							StringBuilder builder1=new StringBuilder();
							builder1.append(request.getContextPath());
							builder1.append(ParameterConstants.PORTAL_PATH);
							builder1.append("?");
							if(nextPageNum<totalPage+1){
								builder1.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.PAGE_NUMBER));
								builder1.append("&");
								builder1.append(ParameterConstants.PAGE_NUMBER);
								builder1.append("=");
								builder1.append(nextPageNum==totalPage?totalPage:nextPageNum);
								
							}else{
								builder1.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.PAGE_NUMBER,ParameterConstants.CHAPTER_ID));
								builder1.append("&");
								builder1.append(ParameterConstants.CHAPTER_ID);
								builder1.append("=");
								String newChapId="";
								if(nextChap<100){
									if(nextChap<10){
										newChapId="00"+nextChap;
									}else{
										newChapId="0"+nextChap;
									}
								}else{
									newChapId=String.valueOf(nextChap);
								}
								builder1.append(rid+newChapId);
								builder1.append("&");
								builder1.append(ParameterConstants.PAGE_NUMBER);
								builder1.append("=");
								builder1.append(1);
							}
							targetUrl=builder1.toString();
	//						if(nextChap==chapterTotalCount){
	//							targetUrl=URLUtil.removeParameter(targetUrl, ParameterConstants.AUTO_PLAY);
	//						}
							com.hunthawk.tag.process.Refresh.pageRefresh(timer,targetUrl) ;
						}else{
							//ת�ص���һҳ
							StringBuilder first=new StringBuilder();
							first.append(request.getContextPath());
							first.append(ParameterConstants.PORTAL_PATH);
							first.append("?");
							String s=request.getQueryString();
							if(s.indexOf(String.valueOf(ParameterConstants.TIMER))>=0){
								s=request.getQueryString().substring(s.indexOf(String.valueOf(ParameterConstants.PAGE)));
							}
							first.append(URLUtil.removeParameter(s, ParameterConstants.PAGE_NUMBER,ParameterConstants.CHAPTER_ID));
							first.append("&");
							first.append(ParameterConstants.CHAPTER_ID);
							first.append("=");
							first.append(rid+"001");
							first.append("&");
							first.append(ParameterConstants.PAGE_NUMBER);
							first.append("=");
							first.append(1);
							targetUrl=URLUtil.removeParameter(first.toString(), ParameterConstants.AUTO_PLAY,ParameterConstants.TIMER);
							
	//						System.out.println("firstpage---->"+targetUrl);
							com.hunthawk.tag.process.Refresh.pageRefresh(timer,targetUrl) ;
						}
					}
				}
				Navigator navi = new Navigator(totalPage, pageNumber, 1, 5);
				request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
				String content=imgs[pageNumber-1];
				StringBuilder img=new StringBuilder();
				String url=getResourceService(request).getChapterImg(rid, content);
//				img.append("<img src=\"").append(url).append(
//							"\" alt=\"loading...\"/>");
				Map resultMap = new HashMap();
				/**
				 * modify by liuxh 09-11-10
				 */
				Map velocityMap = new HashMap();
				velocityMap.put("totalPage", totalPage);
				velocityMap.put("isComics", this.isComics);//�Ƿ���������Դ
				velocityMap.put("currentPage", pageNumber);//��ǰҳ��
				velocityMap.put("url", url);//ͼƬ��ַ
				String result = DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem);
				resultMap.put(TagUtil.makeTag(tagName), result);
				/**
				 * end
				 */
//				resultMap.put(TagUtil.makeTag(tagName), img.toString());
				return resultMap;
			}else{
				this.isComics=false;
				String chapterID = request.getParameter(ParameterConstants.CHAPTER_ID);
				// �����½�ID�����½���Ϣ
				int pageNumber =request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request.getParameter(ParameterConstants.PAGE_NUMBER));// ��ǰҳ
	
				// �õ����� ������
				int word =request.getParameter(ParameterConstants.WORDAGE)==null?(getIntParameter("wordset",-1)<0?ParameterConstants.CHAPTER_CONTENT_WORD_SET:getIntParameter("wordset",ParameterConstants.CHAPTER_CONTENT_WORD_SET))
						:Integer.parseInt(request.getParameter(ParameterConstants.WORDAGE)); 
				int pageSize = word / 500;
				Object[] object = getChapterService(request).getEbookChapterContent(
						chapterID, pageNumber, pageSize);
				String content = "";
				int totalPage = 0;// ��ҳ��
				if (object != null) {
					totalPage = Integer.parseInt(object[0].toString());
					/**
					 * �������д��滻
					 */
					content = getKeyWordService(request).replace(object[1].toString(),1);
				} else {
					TagLogger.debug(tagName, "�½�����Ϊ��", request
							.getQueryString(), null);
					return new HashMap();
				}
				Navigator navi = new Navigator(totalPage, pageNumber, 1, 5);
				request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
				
//				StringBuilder imgurl=new StringBuilder();
//				//��һҳ   �������Ϊͼ+����  
//				if(pageNumber==1){
					EbookChapterDesc echapter=null;
					MagazineChapterDesc mchapter=null;
					NewsPapersChapterDesc nchapter=null;
					String [] imgs=null;
					if(chapterID.startsWith(String.valueOf(ResourceType.TYPE_BOOK))){
						echapter=getResourceService(request).getEbookChapterDesc(chapterID);
						if(echapter!=null){
							imgs=echapter.getImages();
						}
					}else if(chapterID.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){
						mchapter=getResourceService(request).getMagazineChapterDescById(chapterID);
						if(mchapter!=null){
							imgs=mchapter.getImages();
							/**modify by liuxh 09-12-28 ������־��ԴͼƬ���������*/
							Integer width=RequestUtil.getUAInfo().getWidth();
							if(width!=null && width>0){
								List adapImg=new ArrayList();
								if(width<240){//����176
									for(String img:imgs)
										adapImg.add(getNewImg(img,176));
								}else if(width>=240 && width<320){
									for(String img:imgs)
										adapImg.add(getNewImg(img,240));
								}
								if(adapImg!=null && adapImg.size()>0)
									imgs=(String [])adapImg.toArray(new String[adapImg.size()]);
							}else{
								TagLogger.debug(tagName, "��ȡ�ֻ���Ļ�ߴ�ʧ��", request.getQueryString(), null);
							}
							/**end*/
						}
					}else if(chapterID.startsWith(String.valueOf(ResourceType.TYPE_NEWSPAPERS))){
						nchapter=getResourceService(request).getNewsPapersChapterDescById(chapterID);
						if(nchapter!=null){
							imgs=nchapter.getImages();
						}
					}
					List<Object> lsRess = new ArrayList<Object>();
					if(imgs!=null){
						for(int i=0;i<imgs.length;i++){
							Map<String, Object> obj = new HashMap<String, Object>();
							String url=getResourceService(request).getChapterImg(URLUtil.getResourceId(request), imgs[i]);
							obj.put("url", url);
							lsRess.add(obj);
//							imgurl.append("<img src=\"").append(url).append(
//							"\" alt=\"loading...\"/>").append("<br/>");
						}
					}
//				}
				Map resultMap = new HashMap();
				/**
				 * ��ͼ�����ַֿ���Ϊ��������
				 * modify by liuxh 09-11-10
				 */
				
				Map velocityMap = new HashMap();
				velocityMap.put("totalPage", totalPage);
				velocityMap.put("isComics", this.isComics);//�Ƿ���������Դ
				velocityMap.put("currentPage", pageNumber);//��ǰҳ��
				velocityMap.put("imgs", lsRess);
				velocityMap.put("content", content);
				String result = DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem);
				resultMap.put(TagUtil.makeTag(tagName), result);
				/**
				 * end
				 */
				//resultMap.put(TagUtil.makeTag(tagName), content);
//				resultMap.put(TagUtil.makeTag(tagName), pageNumber==1?(imgurl.toString()+content):content);
				return resultMap;
			}
//		}
	}
	private String getNewImg(String img,int size){
		if(img.toLowerCase().matches("[^.]+\\.(png|jpg|gif|jpeg)")){
			String name=img.substring(0,img.lastIndexOf("."));
			String ext=img.substring(img.lastIndexOf("."));
			return name+"_"+size+ext;
		}
		return img;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	// public void setNoPageLink(boolean noPageLink) {
	// this.noPageLink = noPageLink;
	// }
	//
	// public boolean isNoPageLink() {
	// return noPageLink;
	// }

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
	private BussinessService getBussinessService(HttpServletRequest request) {
		if (bussinessService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			bussinessService = (BussinessService) wac.getBean("bussinessService");
		}
		return bussinessService;
	}
	
	private KeyWordService getKeyWordService(HttpServletRequest request) {
		if (keyWordService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			keyWordService = (KeyWordService) wac.getBean("keyWordService");
		}
		return keyWordService;
	}
}
