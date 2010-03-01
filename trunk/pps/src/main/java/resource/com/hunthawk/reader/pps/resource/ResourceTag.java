package com.hunthawk.reader.pps.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.Application;
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.EbookChapterDesc;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.Infomation;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.MagazineChapterDesc;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.NewsPapersChapterDesc;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.resource.Video;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.FeeLogicService;
import com.hunthawk.reader.pps.service.IphoneService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;

/**
 * ��Դ�������Ա�ǩ
 * 
 * @author liuxh ����˵���� property:��Դ���� number:���ֵ�ҳ��(�ڵڼ�ҳ��ʾ) title:�������� add by
 *         liuxh(2009-08-19) split �����зָ��� add by liuxh 09-09-02 size:Ԥ��ͼ���
 *         ֻ��Ԥ��ͼ������Ч
 * 
 */
public class ResourceTag extends BaseTag {

	private ResourceService resourceService;
	private CustomService customService;
	private IphoneService iphoneService;
	private BussinessService bussinessService;
	private FeeLogicService feeLogicService;
	private int number;
	private int currentPage;
	private String split;

	private TagTemplate tagTem = null;

	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String n = getParameter("number", "0");

		int tagTemplateId = this.getIntParameter("tmd", 0);

		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);

		} else {
			tagTem = null;
		}

		split = getParameter("split", "");
		if (!StringUtils.isEmpty(n)) {
			try {
				this.number = Integer.parseInt(n);
			} catch (Exception e) {
				TagLogger.debug(tagName, "��ǩnumber����ֵ��Ч", request
						.getQueryString(), e);
			}
		}
		// ȡ��Ĭ����ʾֵ ���Ϊ�� ��������
		// else {
		// this.number = 1;// Ĭ�ϵ�һҳ��ʾ
		// }
		// �õ���ԴID ��ѯ��Դʵ���װ��Դ�������Ա�ǩ
		String property = getParameter("property", "");
		if (request.getParameter(ParameterConstants.PAGE_NUMBER) != null
				&& !"".equals(request
						.getParameter(ParameterConstants.PAGE_NUMBER))) {
			this.currentPage = Integer.parseInt(request
					.getParameter(ParameterConstants.PAGE_NUMBER));
		} else {
			this.currentPage = 1;
		}
		// �õ���ԴID
		String resID = URLUtil.getResourceId(request);// request.getParameter(ParameterConstants.RESOURCE_ID);

		if (resID.startsWith("" + ResourceType.TYPE_VIDEO)
				|| resID.startsWith("" + ResourceType.TYPE_APPLICATION)) {
			Object isSet = request.getAttribute("SET_HISTORY");
			if (isSet == null) {
				// ��ǰ��URL
				StringBuilder currentUrl = new StringBuilder();
				currentUrl.append(request.getContextPath());
				currentUrl.append(ParameterConstants.PORTAL_PATH);
				currentUrl.append("?");
				currentUrl.append(request.getQueryString());
				/** �����û����ʼ�¼��ַ */
				try {
					String productId = request
							.getParameter(ParameterConstants.PAGEGROUP_ID);
					getCustomService(request).updateUserFootprint(
							RequestUtil.getMobile(), resID,
							currentUrl.toString(),
							(productId == null ? "0" : productId));
				} catch (Exception ex) {
					ex.printStackTrace();
					TagLogger.debug(tagName, "������ʷ��¼ʧ��:" + ex.getMessage(),
							request.getQueryString(), null);
				}
				request.setAttribute("SET_HISTORY", Boolean.TRUE);

			}
		}

		if (StringUtils.isEmpty(property)) {
			TagLogger.debug(tagName, "property����Ϊ��", request.getQueryString(),
					null);
		} else {
			if (StringUtils.isNotEmpty(resID)) {
				ResourceAll resource = getResourceService(request).getResource(
						resID);
				if (resource != null) {
					if (property.equalsIgnoreCase("name")) {
						return resourceName(request, tagName, resource);
					} else if (property.equalsIgnoreCase("ISBN")) {
						return resourceISBN(request, tagName, resource);
					} else if (property.equalsIgnoreCase("shortdesc")) {
						return resourceShortDescription(request, tagName,
								resource);
					} else if (property.equalsIgnoreCase("downnum")) {
						return resoruceDownnum(request, tagName, resource);
					} else if (property.equalsIgnoreCase("extnum")) {
						return resourceExpNum(request, tagName, resource);
					} else if (property.equalsIgnoreCase("priview")) {
						return resourcePreview(request, tagName, resource);
					} else if (property.equalsIgnoreCase("publisher")) {
						return resourcePublisher(request, tagName, resource);
					} else if (property.equalsIgnoreCase("nextresource")) {// ��һ��
						return resourceNextResource(request, tagName, resource);
					} else if (property.equalsIgnoreCase("preresource")) {// ��һ��
						return resourcePreResource(request, tagName, resource);
					} else if (property.equalsIgnoreCase("longdesc")) {
						return resourceLongDescription(request, tagName,
								resource);
					} else if (property.equalsIgnoreCase("recommend")) {
						return resourceRecommend(request, tagName, resource);
					} else if (property.equalsIgnoreCase("area")) {
						return resourcePublishArea(request, tagName, resource);
					} else if (property.equalsIgnoreCase("chaptercount")) {// add
						// by
						// liuxh
						// 09-08-31
						return resourceChapterCount(request, tagName, resource);
					} else if (property.equalsIgnoreCase("prechapter")) {// ��һ��
						return resourcePreChapter(request, tagName, resource);
					} else if (property.equalsIgnoreCase("nextchapter")) {// ��һ��
						return resourceNextChapter(request, tagName, resource);
					} else if (property.equalsIgnoreCase("pretome")) {// ��һ��
						// ��ֽ��־Ϊ��һ��Ŀ
						return resourcePreTome(request, tagName, resource);
					} else if (property.equalsIgnoreCase("nexttome")) {// ��һ��
						// ��ֽ��־Ϊ��һ��Ŀ
						return resourceNextTome(request, tagName, resource);
					} else if (property.equalsIgnoreCase("words")) {// ��Դ������
						return resourceWords(request, tagName, resource);
					} else if (property.equalsIgnoreCase("favoritesCount")) {// �ղ���
						return resourceFavoritesCount(request, tagName,
								resource);
					}
					/** add by liuxh 09-11-17 ������Դ���������� */
					else if (property.equalsIgnoreCase("PV")) {// ��Դ������
						return resourcePV(request, tagName, resource);
					}
					/** end */
					else if (property.equalsIgnoreCase("playtime")) {// ʱ��
						return resourcePlayTime(request, tagName, resource);
					}
				} else {
					TagLogger.debug("ResourceTag", "IDΪ" + resID + "����Դ������!",
							request.getQueryString(), null);
				}
			}
		}
		return new HashMap();
	}

	/***************************************************************************
	 * 
	 * @param request
	 * @param tagName
	 * @param resource
	 * @return
	 */
	private Map resourcePV(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			Map resultMap = new HashMap();
			String title = getParameter("title", "") + resource.getRankingNum();
			resultMap.put(TagUtil.makeTag(tagName), title + split);
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/** �ղ��� */
	private Map resourceFavoritesCount(HttpServletRequest request,
			String tagName, ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			Map resultMap = new HashMap();
			String title = getParameter("title", "")
					+ getCustomService(request).getResourceFavoritesCount(
							resource.getId());
			resultMap.put(TagUtil.makeTag(tagName), title + split);
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/** ��Դ������ */
	private Map resourceWords(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			Map resultMap = new HashMap();
			String title = getParameter("title", "")
					+ (resource.getWords() == null ? "" : resource.getWords());
			resultMap.put(TagUtil.makeTag(tagName), title + split);
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/** ��Դ�½��� */
	private Map resourceChapterCount(HttpServletRequest request,
			String tagName, ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			Map resultMap = new HashMap();
			String title = getParameter("title", "");
			int count = 0;
			if (resource.getId().startsWith(
					String.valueOf(ResourceType.TYPE_BOOK))) {
				Ebook ebook = (Ebook) resource;
				count = Integer.parseInt(getResourceService(request)
						.getEbookChaptersByResourceIDCount(ebook.getId())
						.toString());
			} else if (resource.getId().startsWith(
					String.valueOf(ResourceType.TYPE_COMICS))) {
				Comics comics = (Comics) resource;
				count = getResourceService(request)
						.getComicsChaptersByResourceIDCount(comics.getId());
			} else if (resource.getId().startsWith(
					String.valueOf(ResourceType.TYPE_MAGAZINE))) {
				Magazine magazine = (Magazine) resource;
				count = getResourceService(request)
						.getMagazineChaptersByResourceIDCount(magazine.getId());
			} else if (resource.getId().startsWith(
					String.valueOf(ResourceType.TYPE_NEWSPAPERS))) {
				NewsPapers newpapers = (NewsPapers) resource;
				count = getResourceService(request)
						.getNewsPapersChaptersByResourceIDCount(
								newpapers.getId());
			}
			title = title + count;
			resultMap.put(TagUtil.makeTag(tagName), title + split);
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/** �Ƽ��� */
	private Map resourceRecommend(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			Map resultMap = new HashMap();
			String title = getParameter("title", "")
					+ (resource.getBComment() == null ? "" : resource
							.getBComment());
			resultMap.put(TagUtil.makeTag(tagName), title + split);
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/** ͼ�鳤��� */
	private Map resourceLongDescription(HttpServletRequest request,
			String tagName, ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			Map resultMap = new HashMap();
			String title = getParameter("title", "")
					+ (resource.getIntroLon() == null ? "����" : resource
							.getIntroLon());
			resultMap.put(TagUtil.makeTag(tagName), title + split);
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/** ������ */
	private Map resourcePublisher(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			Map resultMap = new HashMap();
			String title = getParameter("title", "")
					+ (resource.getPublisher() == null ? "" : resource
							.getPublisher());
			resultMap.put(TagUtil.makeTag(tagName), title + split);
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/** ��Դ���� */
	private Map resourceName(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (request.getAttribute("downnum") == null) {
			long num = getResourceService(request).incrResourceVisits(
					resource.getId());// ��Դ�����+1
			request.setAttribute("downnum", num);
		}
		if (this.currentPage <= this.number || this.number == 0) {
			Map resultMap = new HashMap();
			String title = getParameter("title", "")
					+ (resource.getName() == null ? "" : resource.getName());

			// resultMap.put(TagUtil.makeTag(tagName), title + split);
			/**
			 * ��ǩģ������� modify by liuxh 09-11-03
			 */
			if (tagTem != null) {
				Map velocityMap = new HashMap();
				velocityMap.put("strUtil", new StrUtil());
				velocityMap.put("title", title + split);
				String result = DBVmInstance.getInstance().parseVM(velocityMap,
						this, tagTem);
				resultMap.put(TagUtil.makeTag(tagName), result);
			} else {
				resultMap.put(TagUtil.makeTag(tagName), title + split);
			}
			/**
			 * end
			 */
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/** ISBN */
	private Map resourceISBN(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			Map resultMap = new HashMap();
			String title = getParameter("title", "")
					+ (resource.getIsbn() == null ? "" : resource.getIsbn());
			resultMap.put(TagUtil.makeTag(tagName), title + split);
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/** �̼�� */
	private Map resourceShortDescription(HttpServletRequest request,
			String tagName, ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			String title = getParameter("title", "")
					+ (resource.getCComment() == null ? "" : resource
							.getCComment());
			Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName), title + split);
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/** ����� */
	private Map resoruceDownnum(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			// String title = getParameter("title", "")
			// + (resource.getDownnum() == null ? "" : resource
			// .getDownnum());
			String title = getParameter("title", "")
					+ (getResourceService(request).getResourceVisits(resource
							.getId()));
			Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName), title + split);
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/** �Ƽ�ָ�� */
	private Map resourceExpNum(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			String lable = getParameter("title", "");
			Map resultMap = new HashMap();
			String title = "";
			int expNum = resource.getExpNum() == null ? 3 : resource
					.getExpNum();
			switch (expNum) {
			case 1:
				title = "��";
				break;
			case 2:
				title = "���";
				break;
			case 3:
				title = "����";
				break;
			case 4:
				title = "�����";
				break;
			case 5:
				title = "������";
				break;
			default:
				title = "����";
			}
			title = lable + title;
			resultMap.put(TagUtil.makeTag(tagName), title + split);
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/** Ԥ��ͼ */
	private Map resourcePreview(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			int size = getIntParameter("size", 75);
			// ���Ԥ��ͼ��������ҳ
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
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
			sb.append(URLUtil.getResourceId(request) + "001");
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append(1);
			sb.append("&");
			sb.append(ParameterConstants.WORDAGE);
			sb.append("=");
			sb.append(ParameterConstants.CHAPTER_CONTENT_WORD_SET);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.FEE_ID, request);
			Map velocityMap = new HashMap();
			String title = imagePreview(request, tagName, resource, size);
			velocityMap.put("title", title);
			velocityMap.put("url", URLUtil.trimUrl(sb).toString());
			Map resultMap = new HashMap();
			// System.out.println(VmInstance.getInstance()
			// .parseVM(velocityMap, this));
			// resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
			// .parseVM(velocityMap, this));
			resultMap.put(TagUtil.makeTag(tagName), title);
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/**
	 * Ԥ��ͼ
	 * 
	 * @param tag
	 * @param resource
	 * @return
	 * @throws ProcessingException
	 */
	private String imagePreview(HttpServletRequest request, String tagName,
			ResourceAll resource, int size) {
		StringBuilder sb = new StringBuilder();
		// �ж���Դ����(1ͼ�飬2��ֽ��3��־��4������5������6��Ƶ)
		String imgurl = "";
		String name = "";
		if (resource.getId().startsWith("1")) {// ͼ��
			Ebook ebook = (Ebook) resource;
			if (ebook.getBookPic().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						ebook.getId(), ebook.getBookPic(), size);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" alt=\"" + ebook.getName() + "\" />");

				imgurl = url;
				name = ebook.getName();
			}
		} else if (resource.getId().startsWith("2")) {// ��ֽ
			NewsPapers n = (NewsPapers) resource;
			if (n.getImage().toLowerCase()
					.matches("[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						n.getId(), n.getImage(), size);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" alt=\"" + n.getName() + "\"  />");

				imgurl = url;
				name = n.getName();
			}
		} else if (resource.getId().startsWith("3")) {// ��־
			Magazine magazine = (Magazine) resource;
			if (magazine.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						magazine.getId(), magazine.getImage(), size);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" alt=\"" + magazine.getName() + "\"   />");

				imgurl = url;
				name = magazine.getName();
			}
		} else if (resource.getId().startsWith("4")) {// ����
			Comics comics = (Comics) resource;
			if (comics.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						comics.getId(), comics.getImage(), size);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" alt=\"" + comics.getName() + "\"  />");

				imgurl = url;
				name = comics.getName();
			}
		} else if (resource.getId().startsWith("6")) {// ��Ƶ
			Video video = (Video) resource;
			if (video.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						video.getId(), video.getImage(), size);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" alt=\"" + video.getName() + "\"  />");

				imgurl = url;
				name = video.getName();
			}
		} else if (resource.getId().startsWith("" + ResourceType.TYPE_INFO)) {// INFO
			Infomation info = (Infomation) resource;
			if (StringUtils.isNotEmpty(info.getImage())
					&& info.getImage().toLowerCase().matches(
							"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						info.getId(), info.getImage(), size);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" alt=\"" + info.getName() + "\"  />");

				imgurl = url;
				name = info.getName();
			}
		} else if (resource.getId().startsWith(
				"" + ResourceType.TYPE_APPLICATION)) {// APPLICATION

			Application app = (Application) resource;
			if (StringUtils.isNotEmpty(app.getImage())
					&& app.getImage().toLowerCase().matches(
							"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						app.getId(), app.getImage(), size);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" alt=\"" + app.getName() + "\"  />");

				imgurl = url;
				name = app.getName();
			}
		}

		if (tagTem != null) {
			if (imgurl.length() > 0) {
				HashMap velocityMap = new HashMap();
				velocityMap.put("url", imgurl);
				velocityMap.put("name", name);
				// Map resultMap = new HashMap();
				String result = DBVmInstance.getInstance().parseVM(velocityMap,
						this, tagTem);
				return result;
			}
		} else {
			if (sb.length() > 0) {
				return sb.toString();
			}
		}

		return "";
	}

	/** ������� */
	private Map resourcePublishArea(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			String title = getParameter("title", "")
					+ (resource.getCArea() == null ? "" : resource.getCArea());
			Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName), title + split);
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	private Map resourcePlayTime(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (resource instanceof Video) {
			Video video = (Video) resource;
			if (this.currentPage <= this.number || this.number == 0) {
				String title = getParameter("title", "")
						+ video.getFormatPlayTime();
				Map resultMap = new HashMap();
				resultMap.put(TagUtil.makeTag(tagName), title + split);
				return resultMap;
			} else {
				return new HashMap();
			}
		}else{
			return new HashMap();
		}
	}

	/** ��һ�� */
	private Map resourceNextChapter(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			// �ж���Դ
			if (resource.getId().startsWith(
					String.valueOf(ResourceType.TYPE_BOOK))) {// ͼ��
				return ebookNextChapter(request, tagName, resource);
			} else if (resource.getId().startsWith(
					String.valueOf(ResourceType.TYPE_COMICS))) {// ����
				return comicsNextChapter(request, tagName, resource);
			} else if (resource.getId().startsWith(
					String.valueOf(ResourceType.TYPE_MAGAZINE))
					|| resource.getId().startsWith(
							String.valueOf(ResourceType.TYPE_NEWSPAPERS))) {// ��־����ֽ
				return nextChapter(request, tagName);
			}
			return new HashMap();
		} else {
			return new HashMap();
		}
	}

	/**
	 * ������һ�½�
	 * 
	 * @param request
	 * @param tagName
	 * @param resource
	 * @return
	 * @author liuxh 09-11-03
	 */
	private Map comicsNextChapter(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		String mobile = RequestUtil.getMobile();
		String productId = request.getParameter(ParameterConstants.PRODUCT_ID);
		String resourceId = resource.getId();
		int month_fee_bag_id = ParamUtil.getIntParameter(request,
				ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId = ParamUtil.getIntParameter(request,
				ParameterConstants.FEE_BAG_ID, -1);

		String title = getParameter("title", "");
		String currChapter = request
				.getParameter(ParameterConstants.CHAPTER_ID);
		String nextChapterId = getResourceService(request)
				.browseResourceChapter(currChapter, false);
		boolean exists = StringUtils.isNotEmpty(nextChapterId);
		if (title.indexOf("!chaptername!") < 0) {
			if (StringUtils.isEmpty(nextChapterId)) {// �Ѿ������һ�� û����һ����Ϣ
				return new HashMap();
			}
		} else {
			if (StringUtils.isEmpty(nextChapterId)) {
				String rid = URLUtil.getResourceId(request);
				// �ж���Դ���� ��ȡ�½�����
				int chapterTotalCount = 0;
				if (rid.startsWith(String.valueOf(ResourceType.TYPE_BOOK))) {
					chapterTotalCount = Integer.parseInt(getResourceService(
							request).getEbookChaptersByResourceIDCount(rid)
							.toString());
				} else if (rid.startsWith(String
						.valueOf(ResourceType.TYPE_COMICS))) {
					chapterTotalCount = getResourceService(request)
							.getComicsChaptersByResourceIDCount(rid);
				} else if (rid.startsWith(String
						.valueOf(ResourceType.TYPE_MAGAZINE))) {
					chapterTotalCount = getResourceService(request)
							.getMagazineChaptersByResourceIDCount(rid);
				} else if (rid.startsWith(String
						.valueOf(ResourceType.TYPE_NEWSPAPERS))) {
					chapterTotalCount = getResourceService(request)
							.getNewsPapersChaptersByResourceIDCount(rid);
				}
				String endCount = "";
				if (chapterTotalCount < 100) {
					if (chapterTotalCount < 10) {
						endCount = "00" + chapterTotalCount;
					} else {
						endCount = "0" + chapterTotalCount;
					}
				} else {
					endCount = String.valueOf(chapterTotalCount);
				}
				nextChapterId = rid + endCount;
			}
		}
		ResourcePackReleation rel = null;
		if (ParamUtil.getIntParameter(request,
				ParameterConstants.FEE_BAG_RELATION_ID, -1) != -1) {
			rel = getResourceService(request).getResourcePackReleation(
					ParamUtil.getIntParameter(request,
							ParameterConstants.FEE_BAG_RELATION_ID, -1));
		}

		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		Map feeMap = getFeeLogicService(request).isFee(productId, resourceId,
				mobile, rel, packId, month_fee_bag_id);
		/** �½ڿ��Ƶ� */
		int choicePoint = rel == null ? 0 : rel.getChoice() == null ? 0 : rel
				.getChoice();
		ComicsChapter comics = getResourceService(request)
				.getComicsChapterById(nextChapterId);
		if (feeMap == null || comics.getChapterIndex() < choicePoint) {
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
		} else {
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
		// sb.append("&");
		// URLUtil.append(sb, ParameterConstants.WORDAGE, request);
		if (feeMap != null && comics.getChapterIndex() >= choicePoint) {
			sb.append("&");
			sb.append(ParameterConstants.FEE_ID);
			sb.append("=");
			sb.append(feeMap.get("feeId"));
		}

		if (comics != null) {
			Map velocityMap = new HashMap();

			if (title.indexOf("!chaptername!") > 0) {
				// title = title.replaceAll("!chaptername!",
				// String.valueOf(ec.getName()));
				title = title.replaceAll("!chaptername!", "");
			} else {
				title = ("".equals(getParameter("title", "")) ? ParameterConstants.NEXT_CHAPTER_LINK
						: getParameter("title", ""))
						+ " " + comics.getName();
			}
			// String title = ("".equals(getParameter("title", "")) ?
			// ParameterConstants.NEXT_CHAPTER_LINK
			// : getParameter("title", ""))
			// + " " + ec.getName();
			velocityMap.put("exists", exists);
			velocityMap.put("title", title);
			velocityMap.put("url", sb.toString());
			Map resultMap = new HashMap();
			/**
			 * ��ǩģ������� modify by liuxh 09-11-03
			 */
			String result = DBVmInstance.getInstance().parseVM(velocityMap,
					this, tagTem);
			resultMap.put(TagUtil.makeTag(tagName), result);
			// resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
			// .parseVM(velocityMap, this));
			return resultMap;
		} else {
			TagLogger.debug(tagName, "IDΪ" + nextChapterId
					+ "���½���Ϣ������,�޷���ʾ��һ�½�����", request.getQueryString(), null);
			return new HashMap();
		}
	}

	/**
	 * ��ֽ ��־ ��һƪ��ǩ
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map nextChapter(HttpServletRequest request, String tagName) {
		String mobile = RequestUtil.getMobile();
		String productId = request.getParameter(ParameterConstants.PRODUCT_ID);
		String resourceId = URLUtil.getResourceId(request);
		int month_fee_bag_id = ParamUtil.getIntParameter(request,
				ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId = ParamUtil.getIntParameter(request,
				ParameterConstants.FEE_BAG_ID, -1);

		String title = "".equals(getParameter("title", "")) ? ParameterConstants.NEXT_CHAPTER_LINK
				: getParameter("title", "");
		ResourceAll resource = getResourceService(request).getResource(
				resourceId);
		int flag = Integer.parseInt(resource.getId().substring(0, 1));

		/** ��ǰ�½�ID */
		String chapterID = request.getParameter(ParameterConstants.CHAPTER_ID);
		/** ��һ��ID */
		String nextChapter = getResourceService(request).browseResourceChapter(
				chapterID, false);
		boolean exists = StringUtils.isNotEmpty(nextChapter);
		if (title.indexOf("!chaptername!") < 0) {
			if (StringUtils.isEmpty(nextChapter)) {// �Ѿ������һ�� û����һ����Ϣ
				return new HashMap();
			}
		} else {
			if (StringUtils.isEmpty(nextChapter)) {
				String rid = URLUtil.getResourceId(request);
				// �ж���Դ���� ��ȡ�½�����
				int chapterTotalCount = 0;
				if (rid.startsWith(String.valueOf(ResourceType.TYPE_BOOK))) {
					chapterTotalCount = Integer.parseInt(getResourceService(
							request).getEbookChaptersByResourceIDCount(rid)
							.toString());
				} else if (rid.startsWith(String
						.valueOf(ResourceType.TYPE_COMICS))) {
					chapterTotalCount = getResourceService(request)
							.getComicsChaptersByResourceIDCount(rid);
				} else if (rid.startsWith(String
						.valueOf(ResourceType.TYPE_MAGAZINE))) {
					chapterTotalCount = getResourceService(request)
							.getMagazineChaptersByResourceIDCount(rid);
				} else if (rid.startsWith(String
						.valueOf(ResourceType.TYPE_NEWSPAPERS))) {
					chapterTotalCount = getResourceService(request)
							.getNewsPapersChaptersByResourceIDCount(rid);
				}
				String endCount = "";
				if (chapterTotalCount < 100) {
					if (chapterTotalCount < 10) {
						endCount = "00" + chapterTotalCount;
					} else {
						endCount = "0" + chapterTotalCount;
					}
				} else {
					endCount = String.valueOf(chapterTotalCount);
				}
				nextChapter = rid + endCount;
			}
		}

		ResourcePackReleation rel = null;
		if (ParamUtil.getIntParameter(request,
				ParameterConstants.FEE_BAG_RELATION_ID, -1) != -1) {
			rel = getResourceService(request).getResourcePackReleation(
					ParamUtil.getIntParameter(request,
							ParameterConstants.FEE_BAG_RELATION_ID, -1));
		}

		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		Map feeMap = getFeeLogicService(request).isFee(productId, resourceId,
				mobile, rel, packId, month_fee_bag_id);
		/** �½ڿ��Ƶ� */
		int choicePoint = rel == null ? 0 : rel.getChoice() == null ? 0 : rel
				.getChoice();
		NewsPapersChapterDesc npcd = null;
		MagazineChapterDesc mcd = null;
		int chapterIndex = -1;
		if (flag == ResourceType.TYPE_MAGAZINE) {// ��־
			mcd = getResourceService(request).getMagazineChapterDescById(
					nextChapter);
			if (title.indexOf("!chaptername!") > 0) {
				title = title.replaceAll("!chaptername!", "");
			} else {
				title += " " + mcd.getName();
			}
			/**
			 * ��־�ļƷѿ��Ƶ��ھ� ��ѯ�������index modify by liuxh
			 */
			EbookTome tome = getResourceService(request).getEbookTomeById(
					mcd.getTomeId());
			chapterIndex = tome.getTomeIndex();
		} else if (flag == ResourceType.TYPE_NEWSPAPERS) {// ��ֽ
			npcd = getResourceService(request).getNewsPapersChapterDescById(
					nextChapter);
			if (title.indexOf("!chaptername!") > 0) {
				title = title.replaceAll("!chaptername!", "");
			} else {
				title += " " + npcd.getName();
			}
			chapterIndex = npcd.getChapterIndex();
		}
		if (feeMap == null || chapterIndex < choicePoint) {
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
		} else {
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
		sb.append(nextChapter);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append("1");
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.WORDAGE))) {
			sb.append("&");
			sb.append(ParameterConstants.WORDAGE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.WORDAGE));
		}
		if (feeMap != null && chapterIndex >= choicePoint) {
			sb.append("&");
			sb.append(ParameterConstants.FEE_ID);
			sb.append("=");
			sb.append(feeMap.get("feeId"));
		}

		if (npcd != null || mcd != null) {
			Map velocityMap = new HashMap();
			velocityMap.put("exists", exists);
			velocityMap.put("title", title);
			velocityMap.put("url", sb.toString());
			Map resultMap = new HashMap();
			/**
			 * ��ǩģ������� modify by liuxh 09-11-03
			 */
			String result = DBVmInstance.getInstance().parseVM(velocityMap,
					this, tagTem);
			resultMap.put(TagUtil.makeTag(tagName), result);
			// resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
			// .parseVM(velocityMap, this));
			return resultMap;
		} else {
			TagLogger.debug(tagName, "IDΪ" + chapterID + "���½���Ϣ������,�޷���ʾ��һ�½�����",
					request.getQueryString(), null);
			return new HashMap();
		}
	}

	/**
	 * ͼ����һ�±�ǩ
	 * 
	 * @param request
	 * @param tagName
	 * @param resource
	 * @return
	 */
	private Map ebookNextChapter(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		String mobile = RequestUtil.getMobile();
		String productId = request.getParameter(ParameterConstants.PRODUCT_ID);
		String resourceId = resource.getId();
		int month_fee_bag_id = ParamUtil.getIntParameter(request,
				ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId = ParamUtil.getIntParameter(request,
				ParameterConstants.FEE_BAG_ID, -1);

		String title = getParameter("title", "");
		// Ebook ebook = (Ebook) resource;
		String currChapter = request
				.getParameter(ParameterConstants.CHAPTER_ID);
		String nextChapterId = getResourceService(request)
				.browseResourceChapter(currChapter, false);
		boolean exists = StringUtils.isNotEmpty(nextChapterId);
		if (title.indexOf("!chaptername!") < 0) {
			if (StringUtils.isEmpty(nextChapterId)) {// �Ѿ������һ�� û����һ����Ϣ
				return new HashMap();
			}
		} else {
			if (StringUtils.isEmpty(nextChapterId)) {
				String rid = URLUtil.getResourceId(request);
				// �ж���Դ���� ��ȡ�½�����
				int chapterTotalCount = 0;
				if (rid.startsWith(String.valueOf(ResourceType.TYPE_BOOK))) {
					chapterTotalCount = Integer.parseInt(getResourceService(
							request).getEbookChaptersByResourceIDCount(rid)
							.toString());
				} else if (rid.startsWith(String
						.valueOf(ResourceType.TYPE_COMICS))) {
					chapterTotalCount = getResourceService(request)
							.getComicsChaptersByResourceIDCount(rid);
				} else if (rid.startsWith(String
						.valueOf(ResourceType.TYPE_MAGAZINE))) {
					chapterTotalCount = getResourceService(request)
							.getMagazineChaptersByResourceIDCount(rid);
				} else if (rid.startsWith(String
						.valueOf(ResourceType.TYPE_NEWSPAPERS))) {
					chapterTotalCount = getResourceService(request)
							.getNewsPapersChaptersByResourceIDCount(rid);
				}
				String endCount = "";
				if (chapterTotalCount < 100) {
					if (chapterTotalCount < 10) {
						endCount = "00" + chapterTotalCount;
					} else {
						endCount = "0" + chapterTotalCount;
					}
				} else {
					endCount = String.valueOf(chapterTotalCount);
				}
				nextChapterId = rid + endCount;
			}
		}
		ResourcePackReleation rel = null;
		if (ParamUtil.getIntParameter(request,
				ParameterConstants.FEE_BAG_RELATION_ID, -1) != -1) {
			rel = getResourceService(request).getResourcePackReleation(
					ParamUtil.getIntParameter(request,
							ParameterConstants.FEE_BAG_RELATION_ID, -1));
		}

		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		Map feeMap = getFeeLogicService(request).isFee(productId, resourceId,
				mobile, rel, packId, month_fee_bag_id);
		/** �½ڿ��Ƶ� */
		int choicePoint = rel == null ? 0 : rel.getChoice() == null ? 0 : rel
				.getChoice();
		EbookChapterDesc ec = getResourceService(request).getEbookChapterDesc(
				nextChapterId);
		if (feeMap == null || ec.getChapterIndex() < choicePoint) {
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
		} else {
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
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.WORDAGE))) {
			sb.append("&");
			sb.append(ParameterConstants.WORDAGE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.WORDAGE));
		}
		if (feeMap != null && ec.getChapterIndex() >= choicePoint) {
			sb.append("&");
			sb.append(ParameterConstants.FEE_ID);
			sb.append("=");
			sb.append(feeMap.get("feeId"));
		}

		if (ec != null) {
			Map velocityMap = new HashMap();

			if (title.indexOf("!chaptername!") > 0) {
				// title = title.replaceAll("!chaptername!",
				// String.valueOf(ec.getName()));
				title = title.replaceAll("!chaptername!", "");
			} else {
				title = ("".equals(getParameter("title", "")) ? ParameterConstants.NEXT_CHAPTER_LINK
						: getParameter("title", ""))
						+ " " + ec.getName();
			}
			// String title = ("".equals(getParameter("title", "")) ?
			// ParameterConstants.NEXT_CHAPTER_LINK
			// : getParameter("title", ""))
			// + " " + ec.getName();
			velocityMap.put("exists", exists);
			velocityMap.put("title", title);
			velocityMap.put("url", sb.toString());
			Map resultMap = new HashMap();
			/**
			 * ��ǩģ������� modify by liuxh 09-11-03
			 */
			String result = DBVmInstance.getInstance().parseVM(velocityMap,
					this, tagTem);
			resultMap.put(TagUtil.makeTag(tagName), result);
			// resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
			// .parseVM(velocityMap, this));
			return resultMap;
		} else {
			TagLogger.debug(tagName, "IDΪ" + nextChapterId
					+ "���½���Ϣ������,�޷���ʾ��һ�½�����", request.getQueryString(), null);
			return new HashMap();
		}

	}

	/** ��һ�� */
	private Map resourcePreChapter(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			// �ж���Դ
			if (resource.getId().startsWith(
					String.valueOf(ResourceType.TYPE_BOOK))) {// ͼ��
				return ebookPreChapter(request, tagName, resource);
			} else if (resource.getId().startsWith(
					String.valueOf(ResourceType.TYPE_COMICS))) {// ����
				return comicsPreChapter(request, tagName, resource);
			} else if (resource.getId().startsWith(
					String.valueOf(ResourceType.TYPE_MAGAZINE))
					|| resource.getId().startsWith(
							String.valueOf(ResourceType.TYPE_NEWSPAPERS))) {// ��־\��ֽ
				return prevChapter(request, tagName);
			}
			return new HashMap();
		} else {
			return new HashMap();
		}
	}

	/**
	 * ������һ�±�ǩ
	 * 
	 * @author liuxh 09-11-03
	 * @return
	 */
	private Map comicsPreChapter(HttpServletRequest request, String tagName,
			ResourceAll resource) {

		String title = getParameter("title", "");
		/** ��ǰ�½�ID */
		String chapterID = request.getParameter(ParameterConstants.CHAPTER_ID);
		/** ��һ��ID */
		String preChapterId = getResourceService(request)
				.browseResourceChapter(chapterID, true);
		boolean exists = StringUtils.isNotEmpty(preChapterId);
		if (title.indexOf("!chaptername!") < 0) {
			if (StringUtils.isEmpty(preChapterId)) {//
				TagLogger.debug(tagName, "�Ѿ��ǵ�һ��  ����һ����Ϣ", request
						.getQueryString(), null);
				return new HashMap();
			}
		} else {
			if (StringUtils.isEmpty(preChapterId)) {
				preChapterId = URLUtil.getResourceId(request) + "001";
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		ComicsChapter comics = getResourceService(request)
				.getComicsChapterById(preChapterId);
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
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

		if (comics != null) {
			Map velocityMap = new HashMap();

			if (title.indexOf("!chaptername!") > 0) {
				title = title.replaceAll("!chaptername!", "");
			} else {
				title = ("".equals(getParameter("title", "")) ? ParameterConstants.PRE_CHAPTER_LINK
						: getParameter("title", ""))
						+ " " + comics.getName();
			}
			velocityMap.put("exists", exists);
			velocityMap.put("title", title);
			velocityMap.put("url", sb.toString());
			Map resultMap = new HashMap();
			/**
			 * ��ǩģ������� modify by liuxh 09-11-03
			 */
			String result = DBVmInstance.getInstance().parseVM(velocityMap,
					this, tagTem);
			resultMap.put(TagUtil.makeTag(tagName), result);
			// resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
			// .parseVM(velocityMap, this));
			return resultMap;
		} else {
			TagLogger.debug(tagName, "IDΪ" + preChapterId
					+ "���½���Ϣ������,�޷���ʾ��һ������", request.getQueryString(), null);
			return new HashMap();
		}
	}

	/**
	 * ��ֽ ��־����һƪ��ǩ
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map prevChapter(HttpServletRequest request, String tagName) {
		String resourceId = URLUtil.getResourceId(request);
		String title = "".equals(getParameter("title", "")) ? ParameterConstants.PRE_CHAPTER_LINK
				: getParameter("title", "");
		ResourceAll resource = getResourceService(request).getResource(
				resourceId);
		int flag = Integer.parseInt(resource.getId().substring(0, 1));
		/** ��ǰ�½�ID */
		String chapterID = request.getParameter(ParameterConstants.CHAPTER_ID);
		/** ��һ��ID */
		String preChapter = getResourceService(request).browseResourceChapter(
				chapterID, true);
		boolean exists = StringUtils.isNotEmpty(preChapter);
		if (title.indexOf("!chaptername!") < 0) {
			if (StringUtils.isEmpty(preChapter)) {//
				TagLogger.debug(tagName, "�Ѿ��ǵ�һ��  ����һ����Ϣ", request
						.getQueryString(), null);
				return new HashMap();
			}
		} else {
			if (StringUtils.isEmpty(preChapter)) {
				preChapter = URLUtil.getResourceId(request) + "001";
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		NewsPapersChapterDesc npcd = null;
		MagazineChapterDesc mcd = null;
		if (flag == ResourceType.TYPE_MAGAZINE) {// ��־
			mcd = getResourceService(request).getMagazineChapterDescById(
					preChapter);
			if (title.indexOf("!chaptername!") > 0) {
				title = title.replaceAll("!chaptername!", "");
			} else {
				title += " " + mcd.getName();
			}
		} else if (flag == ResourceType.TYPE_NEWSPAPERS) {// ��ֽ
			npcd = getResourceService(request).getNewsPapersChapterDescById(
					preChapter);
			if (title.indexOf("!chaptername!") > 0) {
				title = title.replaceAll("!chaptername!", "");
			} else {
				title += " " + npcd.getName();
			}
		}

		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
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
		sb.append(preChapter);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append("1");
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.WORDAGE))) {
			sb.append("&");
			sb.append(ParameterConstants.WORDAGE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.WORDAGE));
		}
		if (npcd != null || mcd != null) {
			Map velocityMap = new HashMap();
			velocityMap.put("exists", exists);
			velocityMap.put("title", title);
			velocityMap.put("url", sb.toString());
			Map resultMap = new HashMap();
			/**
			 * ��ǩģ������� modify by liuxh 09-11-03
			 */
			String result = DBVmInstance.getInstance().parseVM(velocityMap,
					this, tagTem);
			resultMap.put(TagUtil.makeTag(tagName), result);
			// resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
			// .parseVM(velocityMap, this));
			return resultMap;
		} else {
			System.out.println("IDΪ" + chapterID + "���½���Ϣ������,�޷���ʾ��һ�½�����");
			TagLogger.debug(tagName, "IDΪ" + chapterID + "���½���Ϣ������,�޷���ʾ��һ������",
					request.getQueryString(), null);
			return new HashMap();
		}
	}

	/**
	 * ͼ�����һ�±�ǩ
	 * 
	 * @param request
	 * @param tagName
	 * @param resource
	 * @return
	 */
	private Map ebookPreChapter(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		// String mobile=RequestUtil.getMobile();
		// String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		// String resourceId=resource.getId();
		// int month_fee_bag_id =ParamUtil.getIntParameter(request,
		// ParameterConstants.MONTH_FEE_BAG_ID, -1);
		// int packId=ParamUtil.getIntParameter(request,
		// ParameterConstants.FEE_BAG_ID, -1);

		String title = getParameter("title", "");
		// Ebook ebook = (Ebook) resource;
		/** ��ǰ�½�ID */
		String chapterID = request.getParameter(ParameterConstants.CHAPTER_ID);
		/** ��һ��ID */
		String preChapterId = getResourceService(request)
				.browseResourceChapter(chapterID, true);
		boolean exists = StringUtils.isNotEmpty(preChapterId);
		if (title.indexOf("!chaptername!") < 0) {
			if (StringUtils.isEmpty(preChapterId)) {//
				TagLogger.debug(tagName, "�Ѿ��ǵ�һ��  ����һ����Ϣ", request
						.getQueryString(), null);
				return new HashMap();
			}
		} else {
			if (StringUtils.isEmpty(preChapterId)) {
				preChapterId = URLUtil.getResourceId(request) + "001";
			}
		}
		// ResourcePackReleation rel = null;
		// if (ParamUtil.getIntParameter(request,
		// ParameterConstants.FEE_BAG_RELATION_ID, -1)!= -1) {
		// rel =
		// getResourceService(request).getResourcePackReleation(ParamUtil.getIntParameter(request,
		// ParameterConstants.FEE_BAG_RELATION_ID, -1));
		// }

		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		// Map feeMap=getFeeLogicService(request).isFee(productId, resourceId,
		// mobile, rel, packId, month_fee_bag_id);
		/** �½ڿ��Ƶ� */
		// int choicePoint = rel.getChoice()==null?0:rel.getChoice();
		// �����½�ID�����½�����
		EbookChapterDesc ec = getResourceService(request).getEbookChapterDesc(
				preChapterId);
		// if(feeMap==null || ec.getChapterIndex()<choicePoint){
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		// }else{
		// sb.append(feeMap.get("builder"));
		// }
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
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.WORDAGE))) {
			sb.append("&");
			sb.append(ParameterConstants.WORDAGE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.WORDAGE));
		}
		// if(feeMap!=null && ec.getChapterIndex()>=choicePoint){
		// sb.append("&");
		// sb.append(ParameterConstants.FEE_ID);
		// sb.append("=");
		// sb.append(feeMap.get("feeId"));
		// }

		if (ec != null) {
			Map velocityMap = new HashMap();

			if (title.indexOf("!chaptername!") > 0) {
				title = title.replaceAll("!chaptername!", "");
			} else {
				title = ("".equals(getParameter("title", "")) ? ParameterConstants.PRE_CHAPTER_LINK
						: getParameter("title", ""))
						+ " " + ec.getName();
			}
			velocityMap.put("exists", exists);
			velocityMap.put("title", title);
			velocityMap.put("url", sb.toString());
			Map resultMap = new HashMap();
			/**
			 * ��ǩģ������� modify by liuxh 09-11-03
			 */
			String result = DBVmInstance.getInstance().parseVM(velocityMap,
					this, tagTem);
			resultMap.put(TagUtil.makeTag(tagName), result);
			// resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
			// .parseVM(velocityMap, this));
			return resultMap;
		} else {
			TagLogger.debug(tagName, "IDΪ" + preChapterId
					+ "���½���Ϣ������,�޷���ʾ��һ������", request.getQueryString(), null);
			return new HashMap();
		}

	}

	/** ��һ�� */
	private Map resourcePreResource(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			ResourcePackReleation rpr = null;
			if (request.getParameter(ParameterConstants.FEE_BAG_RELATION_ID) == null) {//
				return new HashMap();
			} else {
				rpr = getResourceService(request)
						.browseResourcePackReleation(
								Integer
										.parseInt(request
												.getParameter(ParameterConstants.FEE_BAG_RELATION_ID)),
								true);
				if (rpr == null) {
					return new HashMap();
				}
			}

			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append(ParameterConstants.PAGE_RESOURCE);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
			sb.append(ParameterConstants.FEE_BAG_ID);
			sb.append("=");
			sb.append(rpr.getPack().getId());
			sb.append("&");
			sb.append(ParameterConstants.FEE_BAG_RELATION_ID);
			sb.append("=");
			sb.append(rpr.getId());
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_ID);
			sb.append("=");
			sb.append(rpr.getResourceId());
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");

			ResourceAll r = getResourceService(request).getResource(
					rpr.getResourceId());
			if (r == null) {
				TagLogger.debug("ResourceTag", "IDΪ"
						+ rpr.getResourceId().toString() + "����Դδ�ҵ�,û����һ����Ϣ",
						request.getQueryString(), null);
				return new HashMap();
			} else {
				Map velocityMap = new HashMap();
				String title = ("".equals(getParameter("title", "")) ? ParameterConstants.PRE_RESOURCE_LINK
						: getParameter("title", ""))
						+ " " + r.getName();
				velocityMap.put("title", title);
				velocityMap.put("url", sb.toString());
				Map resultMap = new HashMap();
				/**
				 * ��ǩģ������� modify by liuxh 09-11-03
				 */
				String result = DBVmInstance.getInstance().parseVM(velocityMap,
						this, tagTem);
				// resultMap.put(TagUtil.makeTag(tagName), VmInstance
				// .getInstance().parseVM(velocityMap, this));
				resultMap.put(TagUtil.makeTag(tagName), result);
				return resultMap;
			}
		} else {
			return new HashMap();
		}
	}

	/** ��һ��/��һ��Ŀ */
	private Map resourceNextTome(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			// �ж���Դ
			if (resource.getId().startsWith(
					String.valueOf(ResourceType.TYPE_BOOK))) {// ͼ��
				return null;
			} else if (resource.getId().startsWith(
					String.valueOf(ResourceType.TYPE_COMICS))) {// ����
				return null;
			} else if (resource.getId().startsWith(
					String.valueOf(ResourceType.TYPE_MAGAZINE))
					|| resource.getId().startsWith(
							String.valueOf(ResourceType.TYPE_NEWSPAPERS))) {// ��־
				// ����ֽ
				return nextColumn(request, tagName);
			}
			return new HashMap();
		} else {
			return new HashMap();
		}
	}

	/**
	 * ��ֽ ��־ ��һ��Ŀ��ǩ
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map nextColumn(HttpServletRequest request, String tagName) {
		// �õ���ID
		String tomeId = request.getParameter(ParameterConstants.TOME_ID);
		if (tomeId == null || "".equals(tomeId)) {
			TagLogger.debug(tagName, "�޷���ȡ��ID,�޷�������ʾ��һ��Ŀ", request
					.getQueryString(), null);
			return new HashMap();
		}
		String rid = URLUtil.getResourceId(request);
		int flag = Integer.parseInt(rid.substring(0, 1));
		List<EbookTome> tomes = null;
		String[] arrtomes = null;
		if (flag == ResourceType.TYPE_NEWSPAPERS) {// ��ֽ
			// ������ԴID��ѯ���б�
			tomes = getResourceService(request).getEbookTomeByResourceId(rid,
					1, 100);
			arrtomes = new String[tomes.size()];
			for (int i = 0; i < tomes.size(); i++) {
				EbookTome t = tomes.get(i);
				arrtomes[i] = t.getId();
			}
		} else {// �Ǳ�ֽ��Դ ����ʾ��һ��Ŀ����
			return new HashMap();
		}
		int curTomeNum = Integer.parseInt(tomeId
				.substring((tomeId.length() - 2)));
		// int start = Integer.parseInt(arrtomes[0].substring((arrtomes[0]
		// .length() - 2)));
		int end = Integer.parseInt(arrtomes[arrtomes.length - 1]
				.substring((arrtomes[arrtomes.length - 1].length() - 2)));
		if (curTomeNum >= end) {
			curTomeNum = end;
		} else {
			curTomeNum = curTomeNum + 1;
		}
		if (curTomeNum < 10) {
			tomeId = rid + "0" + curTomeNum;
		} else {
			tomeId = rid + curTomeNum;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		sb.append(ParameterConstants.PAGE);
		sb.append("=");
		sb.append(ParameterConstants.PAGE_RESOURCE);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
		URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
		URLUtil.append(sb, ParameterConstants.RESOURCE_ID, request);
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append("1");
		sb.append("&");
		sb.append(ParameterConstants.TOME_ID);
		sb.append("=");
		sb.append(tomeId);

		// ����tomeId��ѯtomeʵ����Ϣ
		EbookTome et = getResourceService(request).getEbookTomeById(tomeId);
		if (et == null) {
			TagLogger.debug(tagName, "IDΪ" + tomeId + "�ľ���Ϣ������", request
					.getQueryString(), null);
			return new HashMap();
		}
		String title = "".equals(getParameter("title", "")) ? "��һ��"
				: getParameter("title", "");
		Map velocityMap = new HashMap();
		velocityMap.put("title", title + " " + et.getName());
		velocityMap.put("url", sb.toString());
		Map resultMap = new HashMap();
		/**
		 * ��ǩģ������� modify by liuxh 09-11-03
		 */
		String result = DBVmInstance.getInstance().parseVM(velocityMap, this,
				tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		// resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
		// .parseVM(velocityMap, this));
		return resultMap;

	}

	/** ��һ��/��һ��Ŀ */
	private Map resourcePreTome(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			// �ж���Դ
			if (resource.getId().startsWith(
					String.valueOf(ResourceType.TYPE_BOOK))) {// ͼ��
				return null;
			} else if (resource.getId().startsWith(
					String.valueOf(ResourceType.TYPE_COMICS))) {// ����
				return null;
			} else if (resource.getId().startsWith(
					String.valueOf(ResourceType.TYPE_MAGAZINE))
					|| resource.getId().startsWith(
							String.valueOf(ResourceType.TYPE_NEWSPAPERS))) {// ��־
				// ����ֽ
				return preColumn(request, tagName);
			}
			return new HashMap();
		} else {
			return new HashMap();
		}

	}

	/**
	 * ��һ��Ŀ (��־ ����ֽ)
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map preColumn(HttpServletRequest request, String tagName) {
		// �õ���ID
		String tomeId = request.getParameter(ParameterConstants.TOME_ID);
		if (tomeId == null || "".equals(tomeId)) {
			TagLogger.debug(tagName, "�޷���ȡ��ID,�޷�������ʾ��һ��Ŀ", request
					.getQueryString(), null);
			return new HashMap();
		}
		String rid = URLUtil.getResourceId(request);
		int flag = Integer.parseInt(rid.substring(0, 1));
		List<EbookTome> tomes = null;
		int[] arrtomes = null;
		if (flag == ResourceType.TYPE_NEWSPAPERS) {// ��ֽ
			// ������ԴID��ѯ���б�
			tomes = getResourceService(request).getEbookTomeByResourceId(rid,
					1, 1000);
			arrtomes = new int[tomes.size()];
			for (int i = 0; i < tomes.size(); i++) {
				EbookTome t = tomes.get(i);
				arrtomes[i] = Integer.parseInt(t.getId().substring(
						(t.getId().length() - 2)));
			}
		} else {// �Ǳ�ֽ��Դ ����ʾ��һ��Ŀ����
			return new HashMap();
		}
		int curTomeNum = Integer.parseInt(tomeId
				.substring((tomeId.length() - 2)));
		int start = arrtomes[0];
		// int end = arrtomes[arrtomes.length - 1];
		if (curTomeNum <= start) {
			curTomeNum = start;
		} else {
			curTomeNum = curTomeNum - 1;
		}
		if (curTomeNum < 10) {
			tomeId = rid + "0" + curTomeNum;
		} else {
			tomeId = rid + curTomeNum;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		sb.append(ParameterConstants.PAGE);
		sb.append("=");
		sb.append(ParameterConstants.PAGE_RESOURCE);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
		URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
		sb.append(ParameterConstants.RESOURCE_ID);
		sb.append("=");
		sb.append(rid);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append("1");
		sb.append("&");
		sb.append(ParameterConstants.TOME_ID);
		sb.append("=");
		sb.append(tomeId);

		// ����tomeId��ѯtomeʵ����Ϣ
		EbookTome et = getResourceService(request).getEbookTomeById(tomeId);
		if (et == null) {
			TagLogger.debug(tagName, "IDΪ" + tomeId + "�ľ���Ϣ������", request
					.getQueryString(), null);
			return new HashMap();
		}
		String title = "".equals(getParameter("title", "")) ? "��һ��"
				: getParameter("title", "");
		Map velocityMap = new HashMap();
		velocityMap.put("title", title + " " + et.getName());
		velocityMap.put("url", sb.toString());
		Map resultMap = new HashMap();
		/**
		 * ��ǩģ������� modify by liuxh 09-11-03
		 */
		String result = DBVmInstance.getInstance().parseVM(velocityMap, this,
				tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		// resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
		// .parseVM(velocityMap, this));
		return resultMap;
	}

	/** ��һ�� */
	private Map resourceNextResource(HttpServletRequest request,
			String tagName, ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			ResourcePackReleation rpr = null;
			if (request.getParameter(ParameterConstants.FEE_BAG_RELATION_ID) == null) {
				return new HashMap();
			} else {
				rpr = getResourceService(request)
						.browseResourcePackReleation(
								Integer
										.parseInt(request
												.getParameter(ParameterConstants.FEE_BAG_RELATION_ID)),
								false);
				if (rpr == null) {
					return new HashMap();
				}
			}
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append(ParameterConstants.PAGE_RESOURCE);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
			sb.append(ParameterConstants.FEE_BAG_ID);
			sb.append("=");
			sb.append(rpr.getPack().getId());
			sb.append("&");
			sb.append(ParameterConstants.FEE_BAG_RELATION_ID);
			sb.append("=");
			sb.append(rpr.getId());
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_ID);
			sb.append("=");
			sb.append(rpr.getResourceId());
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");

			ResourceAll r = getResourceService(request).getResource(
					rpr.getResourceId());
			if (r == null) {
				TagLogger.debug(tagName, "IDΪ" + rpr.getResourceId().toString()
						+ "����Դû���ҵ�,û����һ����Ϣ", request.getQueryString(), null);
				return new HashMap();
			} else {
				Map velocityMap = new HashMap();
				String title = ("".equals(getParameter("title", "")) ? ParameterConstants.NEXT_RESOURCE_LINK
						: getParameter("title", ""))
						+ " " + r.getName();
				velocityMap.put("title", title);
				velocityMap.put("url", sb.toString());
				Map resultMap = new HashMap();
				/**
				 * ��ǩģ������� modify by liuxh 09-11-03
				 */
				String result = DBVmInstance.getInstance().parseVM(velocityMap,
						this, tagTem);
				// resultMap.put(TagUtil.makeTag(tagName), VmInstance
				// .getInstance().parseVM(velocityMap, this));
				resultMap.put(TagUtil.makeTag(tagName), result);
				return resultMap;
			}
		} else {
			return new HashMap();
		}

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

	/**
	 * ����ĳһ��Ŀ�µ��½�ID����
	 * 
	 * @param curChapterId
	 * @return
	 */
	private String[] getChapterIdByTome(HttpServletRequest request,
			String curChapterId) {
		String rid = URLUtil.getResourceId(request);
		ResourceAll resource = getResourceService(request).getResource(rid);
		int flag = Integer.parseInt(resource.getId().substring(0, 1));
		// ���ݵ�ǰ�½�ID�����½�ʵ�� �ٸ��ݾ�ID �����½���Ϣ �����IDΪ�յĻ� �� û����Ŀ
		String chid = request.getParameter(ParameterConstants.CHAPTER_ID);

		// �����½�ID�����½�����
		NewsPapersChapterDesc npcd = null;
		List<NewsPapersChapterDesc> newspapers = null;
		MagazineChapterDesc mcd = null;
		List<MagazineChapterDesc> magazines = null;
		String tomeId = "";
		String[] arrMagazine = null;
		String[] arrNewspaper = null;
		if (flag == ResourceType.TYPE_MAGAZINE) {// ��־
			mcd = getResourceService(request).getMagazineChapterDescById(chid);
			tomeId = mcd.getTomeId();
			if (tomeId != null) {
				magazines = getResourceService(request)
						.getMagazineChapterDescByTomeId(tomeId);
			} else {// û�о�����
				magazines = getResourceService(request)
						.getMagazineChaptersByResourceID(rid);
			}
			arrMagazine = new String[magazines.size()];
			for (int i = 0; i < magazines.size(); i++) {
				MagazineChapterDesc m = magazines.get(i);
				arrMagazine[i] = m.getId();
			}
			return arrMagazine;
		} else if (flag == ResourceType.TYPE_NEWSPAPERS) {// ��ֽ
			npcd = getResourceService(request).getNewsPapersChapterDescById(
					chid);
			tomeId = npcd.getTomeId();
			if (tomeId != null) {
				newspapers = getResourceService(request)
						.getNewsPapersChapterDescByTomeId(tomeId);
			} else {// û�о�����
				newspapers = getResourceService(request)
						.getNewsPapersChaptersByResourceID(rid);
			}
			arrNewspaper = new String[newspapers.size()];
			for (int i = 0; i < newspapers.size(); i++) {
				NewsPapersChapterDesc n = newspapers.get(i);
				arrNewspaper[i] = n.getId();
			}
			return arrNewspaper;
		}
		return new String[] {};
	}

	private IphoneService getIphoneService(HttpServletRequest request) {
		if (iphoneService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			iphoneService = (IphoneService) wac.getBean("iphoneService");
		}
		return iphoneService;
	}

	private BussinessService getBussinessService(HttpServletRequest request) {
		if (bussinessService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			bussinessService = (BussinessService) wac
					.getBean("bussinessService");
		}
		return bussinessService;
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
