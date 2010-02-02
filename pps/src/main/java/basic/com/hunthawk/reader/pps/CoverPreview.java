package com.hunthawk.reader.pps;

import javax.servlet.http.HttpServletRequest;

import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.pps.service.ResourceService;

public class CoverPreview {

	/***
	 * 取得图书预览图
	 * @param service
	 * @param request
	 * @param resource
	 * @param size
	 * @return
	 */
	public static String getPreview(ResourceService service,ResourceAll resource,int size){
		String imgUrl="";
		if (resource.getId().startsWith("1")) {// 图书
			Ebook ebook = (Ebook) resource;
			if (ebook.getBookPic().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				imgUrl = service.getPreviewCoverImg(
						ebook.getId(), ebook.getBookPic(), size);
			}
		}else if (resource.getId().startsWith("2")) {// 报纸
			NewsPapers n = (NewsPapers) resource;
			if (n.getImage().toLowerCase()
					.matches("[^.]+\\.(png|jpg|gif|jpeg)")) {
				imgUrl = service.getPreviewCoverImg(
						n.getId(), n.getImage(), size);
				// System.out.println("url--->"+url);
			}
		} else if (resource.getId().startsWith("3")) {// 杂志
			Magazine magazine = (Magazine) resource;
			if (magazine.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				imgUrl =service.getPreviewCoverImg(
						magazine.getId(), magazine.getImage(), size);
				
			}
		} else if (resource.getId().startsWith("4")) {// 漫画
			Comics comics = (Comics) resource;
			if (comics.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				imgUrl = service.getPreviewCoverImg(
						comics.getId(), comics.getImage(), size);
			}
		}
		
		return imgUrl;
	}
	
	public  String getPreviewByPageSize(ResourceService service,ResourceAll resource,int size){
		String imgUrl="";
		if (resource.getId().startsWith("1")) {// 图书
			Ebook ebook = (Ebook) resource;
			if (ebook.getBookPic().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				imgUrl = service.getPreviewCoverImg(
						ebook.getId(), ebook.getBookPic(), size);
			}
		}else if (resource.getId().startsWith("2")) {// 报纸
			NewsPapers n = (NewsPapers) resource;
			if (n.getImage().toLowerCase()
					.matches("[^.]+\\.(png|jpg|gif|jpeg)")) {
				imgUrl = service.getPreviewCoverImg(
						n.getId(), n.getImage(), size);
				// System.out.println("url--->"+url);
			}
		} else if (resource.getId().startsWith("3")) {// 杂志
			Magazine magazine = (Magazine) resource;
			if (magazine.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				imgUrl =service.getPreviewCoverImg(
						magazine.getId(), magazine.getImage(), size);
				
			}
		} else if (resource.getId().startsWith("4")) {// 漫画
			Comics comics = (Comics) resource;
			if (comics.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				imgUrl = service.getPreviewCoverImg(
						comics.getId(), comics.getImage(), size);
			}
		}
		
		return imgUrl;
	}
	
}
