package com.hunthawk.reader.pps;

import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.Infomation;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.Video;
import com.hunthawk.reader.pps.service.ResourceService;

public class CoverPreview {

	/***************************************************************************
	 * È¡µÃÍ¼ÊéÔ¤ÀÀÍ¼
	 * 
	 * @param service
	 * @param request
	 * @param resource
	 * @param size
	 * @return
	 */
	public static String getPreview(ResourceService service,
			ResourceAll resource, int size) {
		String imgUrl = "";
		try {
			if (resource.getId().startsWith("1")) {// Í¼Êé
				Ebook ebook = (Ebook) resource;
				if (ebook.getBookPic().toLowerCase().matches(
						"[^.]+\\.(png|jpg|gif|jpeg)")) {
					imgUrl = service.getPreviewCoverImg(ebook.getId(), ebook
							.getBookPic(), size);
				}
			} else if (resource.getId().startsWith("2")) {// ±¨Ö½
				NewsPapers n = (NewsPapers) resource;
				if (n.getImage().toLowerCase().matches(
						"[^.]+\\.(png|jpg|gif|jpeg)")) {
					imgUrl = service.getPreviewCoverImg(n.getId(),
							n.getImage(), size);
					// System.out.println("url--->"+url);
				}
			} else if (resource.getId().startsWith("3")) {// ÔÓÖ¾
				Magazine magazine = (Magazine) resource;
				if (magazine.getImage().toLowerCase().matches(
						"[^.]+\\.(png|jpg|gif|jpeg)")) {
					imgUrl = service.getPreviewCoverImg(magazine.getId(),
							magazine.getImage(), size);

				}
			} else if (resource.getId().startsWith("4")) {// Âþ»­
				Comics comics = (Comics) resource;
				if (comics.getImage().toLowerCase().matches(
						"[^.]+\\.(png|jpg|gif|jpeg)")) {
					imgUrl = service.getPreviewCoverImg(comics.getId(), comics
							.getImage(), size);
				}
			} else if (resource.getId().startsWith("6")) {// Âþ»­
				Video video = (Video) resource;
				if (video.getImage().toLowerCase().matches(
						"[^.]+\\.(png|jpg|gif|jpeg)")) {
					imgUrl = service.getPreviewCoverImg(video.getId(), video
							.getImage(), size);
				}
			} else if (resource.getId().startsWith("7")) {// Âþ»­
				Infomation comics = (Infomation) resource;
				if (comics.getImage().toLowerCase().matches(
						"[^.]+\\.(png|jpg|gif|jpeg)")) {
					imgUrl = service.getPreviewCoverImg(comics.getId(), comics
							.getImage(), size);
				}
			}
		} catch (Exception e) {

		}
		return imgUrl;
	}

	public String getPreviewByPageSize(ResourceService service,
			ResourceAll resource, int size) {
		String imgUrl = "";
		if (resource.getId().startsWith("1")) {// Í¼Êé
			Ebook ebook = (Ebook) resource;
			if (ebook.getBookPic().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				imgUrl = service.getPreviewCoverImg(ebook.getId(), ebook
						.getBookPic(), size);
			}
		} else if (resource.getId().startsWith("2")) {// ±¨Ö½
			NewsPapers n = (NewsPapers) resource;
			if (n.getImage().toLowerCase()
					.matches("[^.]+\\.(png|jpg|gif|jpeg)")) {
				imgUrl = service.getPreviewCoverImg(n.getId(), n.getImage(),
						size);
				// System.out.println("url--->"+url);
			}
		} else if (resource.getId().startsWith("3")) {// ÔÓÖ¾
			Magazine magazine = (Magazine) resource;
			if (magazine.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				imgUrl = service.getPreviewCoverImg(magazine.getId(), magazine
						.getImage(), size);

			}
		} else if (resource.getId().startsWith("4")) {// Âþ»­
			Comics comics = (Comics) resource;
			if (comics.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				imgUrl = service.getPreviewCoverImg(comics.getId(), comics
						.getImage(), size);
			}
		}

		return imgUrl;
	}

}
