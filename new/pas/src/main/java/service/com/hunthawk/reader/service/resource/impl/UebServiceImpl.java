/**
 * 
 */
package com.hunthawk.reader.service.resource.impl;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.MagazineChapter;
import com.hunthawk.reader.domain.resource.NewsPapersChapter;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceResType;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.enhance.util.GZIPUtil;
import com.hunthawk.reader.enhance.util.ImageUtil;
import com.hunthawk.reader.enhance.util.ResourceUtil;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.enhance.util.VelocityUtil;
import com.hunthawk.reader.service.partner.FeeService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.resource.UebService;
import com.hunthawk.reader.service.resource.UploadService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author BruceSun
 * 
 */
@SuppressWarnings("unchecked")
public class UebServiceImpl implements UebService {

	private static final Logger logger = LoggerFactory
			.getLogger(UebServiceImpl.class);

	private ResourceService resourceService;

	private UploadService uploadService;

	private SystemService systemService;

	private FeeService feeService;

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	public void setFeeService(FeeService feeService) {
		this.feeService = feeService;
	}

	public void setUploadService(UploadService uploadService) {
		this.uploadService = uploadService;
	}

	public void createUeb(ResourcePackReleation rel) {
//		String resourceId = rel.getResourceId();
//		Integer resourceType = Integer.parseInt(resourceId.substring(0, 1));
//		ResourceAll resource = resourceService.getResource(resourceId,
//				resourceType);
//		// chapters
//		List chapters = resourceService.getResourceChapter(getNewChapter(
//				resourceType).getClass(), resourceId);
//		// 分类
//		String[] sorts = getSorts(resourceId);
//		createUeb(rel, 128, resourceType, resource, chapters, sorts);
//		createUeb(rel, 176, resourceType, resource, chapters, sorts);
//		createUeb(rel, 240, resourceType, resource, chapters, sorts);

	}
	
	public void deleteUeb(ResourcePackReleation rel){
//		Variables dirVar = systemService.getVariables("media_dir");
//		String dir = dirVar.getValue() + "ueb" + File.separator
//				+ (rel.getId() / 1000);
//		
//		String[] files = new String[3];
//		files[0] = dir + File.separator + rel.getId() + "_128.ueb";
//		files[1] = dir + File.separator + rel.getId() + "_176.ueb";
//		files[2] = dir + File.separator + rel.getId() + "_240.ueb";
//		
//		resourceService.rsyncUploadFile(ResourceUtil.RSYNC_TYPE_DEL, files);
//		for(String fileName : files){
//			try{
//				File file = new File(fileName);
//				FileUtils.forceDelete(file);
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//		}
		
		
	}

	/**
	 * 获取作者名称
	 * 
	 * @param authorIds
	 * @return
	 */
	private String getAuthorName(Integer[] authorIds) {
		String author = ";";
		int i = 0;
		for (Integer authorId : authorIds) {
			ResourceAuthor rAuthor = resourceService
					.getResourceAuthorById(authorId);
			if (rAuthor != null) {
				author += rAuthor.getName();
				if (++i < authorIds.length) {
					author += ";";
				}
			}
		}
		return author.substring(1);
	}

	/**
	 * 获取分类名称
	 * 
	 * @param id
	 * @return 数组 0.一级分类 1.二级分类
	 */
	private String[] getSorts(String resourceId) {
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("rid", resourceId,
				CompareType.Equal);
		expressions.add(ex);
		List<ResourceResType> resTypes = resourceService.findResourceResTypeBy(
				1, 1000, "resTypeId", false, expressions);
		StringBuilder firstSort = new StringBuilder();
		StringBuilder secondSort = new StringBuilder();
		firstSort.append(";");
		secondSort.append(";");
		int i = 0;
		for (ResourceResType resType : resTypes) {
			ResourceType type = resourceService.getResourceType(resType
					.getResTypeId());
			++i;
			if (type.getParent() == null) {
				firstSort.append(type.getName());
				if (i < resTypes.size()) {
					firstSort.append(";");
				}
			} else {
				secondSort.append(type.getName());
				if (i < resTypes.size()) {
					secondSort.append(";");
				}
			}
		}

		return new String[] { firstSort.substring(1), secondSort.substring(1) };
	}

	/**
	 * 生成文件名,不足35位用空格补上
	 * 
	 * @param filename
	 * @return
	 */
	private String createFilename(String filename) {
		return StringUtils.rightPad(filename, 35, ' ');
	}

	/**
	 * 创建文件描述
	 * 
	 * @param index
	 * @param fileId
	 * @param locations
	 * @param filename
	 * @param out
	 * @return 返回字符串数组： 0.索引值 1.文件序号
	 * @throws IOException
	 */
	private int[] createFileDesc(int index, int fileId,
			Map<String, UebLocation> locations, String filename,
			DataOutputStream out) throws IOException {
		out.writeInt(fileId++);
		index += 4;
		filename = createFilename(filename);
		out.writeBytes(filename);
		index += filename.length();
		UebLocation loc = new UebLocation();
		loc.index = index;
		loc.name = filename;
		loc.type = UebLocation.Type.TwoLong;
		locations.put(loc.name, loc);
		// 文件起始位置
		out.writeLong(0L);
		index += 8;
		// 文件长度
		out.writeLong(0L);
		index += 8;
		int[] ids = new int[2];
		ids[0] = index;
		ids[1] = fileId;
		return ids;
	}

	/**
	 * 通过velocity模板创建文件内容
	 * 
	 * @param index
	 * @param locations
	 * @param filename
	 * @param out
	 * @param velocityFilename
	 * @param velocityMap
	 * @return
	 * @throws IOException
	 */
	private int createFileContent(Integer index,
			Map<String, UebLocation> locations, String filename,
			DataOutputStream out, String velocityFilename,
			Map<String, Object> velocityMap) throws IOException {
		String fileContent = VelocityUtil.getInstance().parse(velocityFilename,
				velocityMap);
		filename = createFilename(filename);

		out.writeBytes(filename);
		index += filename.length();

		index += 16;

		byte[] filebytes = fileContent.getBytes("utf-8");
		byte[] bytes = GZIPUtil.gzip(filebytes);

		out.writeLong(index.longValue());
		Long filesize = ((Integer) bytes.length).longValue();
		out.writeLong(filesize);

		UebLocation loc = locations.get(filename);
		Long[] twoLong = new Long[2];
		twoLong[0] = index.longValue();
		twoLong[1] = filesize;
		loc.value = twoLong;

		out.write(bytes);
		index += filesize.intValue();

		out.writeBytes("..");
		index += 2;
		return index;
	}

	/**
	 * 读取目录文件创建文件数据内容
	 * 
	 * @param filename
	 * @param dir
	 * @param index
	 * @param locations
	 * @param out
	 * @return
	 * @throws IOException
	 */
	private int createFile(String filename, String existFile, String dir,
			Integer index, Map<String, UebLocation> locations,
			DataOutputStream out) throws IOException {

		File file = new File(dir, existFile);

		filename = createFilename(filename);
		out.writeBytes(filename);
		index += filename.length();
		index += 16;

		byte[] filebytes = FileUtils.readFileToByteArray(file);
		byte[] bytes = GZIPUtil.gzip(filebytes);

		out.writeLong(index.longValue());
		Long filesize = ((Integer) bytes.length).longValue();
		out.writeLong(filesize);
		UebLocation loc = locations.get(filename);
		Long[] twoLong = new Long[2];
		twoLong[0] = index.longValue();
		twoLong[1] = filesize;
		loc.value = twoLong;

		out.write(bytes);
		index += filesize.intValue();

		out.writeBytes("..");
		index += 2;
		return index;
	}

	private static void debug(String info) {
		// if(logger.isDebugEnabled()){
		// logger.debug(info);
		// }
		System.out.println(info);
	}

	private static void error(String error, Exception e) {
		logger.error("[Create ueb file error,info = ]" + error, e);
	}

	private void createUeb(ResourcePackReleation rel, int width,
			Integer resourceType, ResourceAll resource, List chapters,
			String[] sorts) {
		String resourceId = rel.getResourceId();
		Variables dirVar = systemService.getVariables("media_dir");
		String dir = dirVar.getValue() + "ueb" + File.separator
				+ (rel.getId() / 1000);
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		dir += File.separator + rel.getId() + "_" + width + ".ueb";

		debug(dir);
		Variables versionVar = systemService.getVariables("ueb_version");

		// 流式
		int formatType = 1;

		Map<String, Object> velocityMap = new HashMap<String, Object>();
		velocityMap.put("cpid", rel.getCpid());
		// 1.流式书籍 2.版式书籍
		velocityMap.put("type", formatType);

		velocityMap.put("generator", "hunthawk");

		// 是否付费
		String feeId = rel.getFeeId();
		if (feeId != null) {
			// TODO:如果是N选X这种方式，需要在考虑一下
			Fee fee = feeService.getFee(feeId);
			velocityMap.put("price", fee.getCode());
			velocityMap.put("chargetype", 1);
		} else {
			// 免费
			velocityMap.put("chargetype", 0);
			velocityMap.put("price", "0");
		}

		velocityMap.put("book", resource);

		velocityMap.put("imageUtil", new ImageUtil());

		velocityMap.put("authorName", getAuthorName(resource.getAuthorIds()));

		// 一级分类
		velocityMap.put("sort", sorts[0]);
		velocityMap.put("secondSort", sorts[1]);

		String resDir = uploadService.getResourceFileDir(resourceId,
				resourceType);

		Map<String, UebLocation> locations = new HashMap<String, UebLocation>();
		try {
			FileOutputStream fout = new FileOutputStream(dir);
			DataOutputStream out = new DataOutputStream(fout);
			int index = 0;
			out.writeBytes("aplication/x-ueb");
			index += 16;
			// 版本01
			out.writeInt(Integer.parseInt(versionVar.getValue()));
			index += 4;
			// 内容ID
			out.writeLong(Long.parseLong(resourceId));
			index += 8;
			// 流式
			out.writeInt(formatType);
			index += 4;
			// 计费类型
			// if (rel.getChoice() == null) {
			out.writeInt(1);
			// }
			// else {
			// out.writeInt(rel.getChoice());
			// }
			index += 4;
			// 渠道ID
			out.writeLong(1L);
			index += 8;
			// CPID
			try {
				out.writeLong(Long.parseLong(rel.getCpid()));
			} catch (Exception e) {
				out.writeLong(0L);
			}
			index += 8;
			// 批价ID
			out.writeLong(rel.getId().longValue());
			index += 8;

			String date = ToolDateUtil.dateToString(new Date(), "yyyyMMdd");

			// 文件创建日期
			out.writeBytes(date);
			index += date.length();

			// 文件修改日期
			out.writeBytes(date);
			index += date.length();
			// 文件长度
			UebLocation loc = new UebLocation();
			loc.index = index;
			loc.name = "fileLength";
			loc.type = UebLocation.Type.Long;
			locations.put(loc.name, loc);
			out.writeLong(0L);
			index += 8;

			// 保留字段
			out.writeBytes("1234567890");
			index += 10;

			debug("Finish file format desc regional");

			int fileId = 1;
			// META-INF文件索引区段 BEGIN=======================
			// container.xml
			int[] ids = createFileDesc(index, fileId, locations,
					"container.xml", out);
			index = ids[0];
			fileId = ids[1];

			debug("Finish META-INF file  desc regional");

			// META-INF文件索引区段 =======================END

			// 内容文件索引区段 BEGIN=======================
			// content.opf
			ids = createFileDesc(index, fileId, locations, "content.opf", out);
			index = ids[0];
			fileId = ids[1];

			// content.ncx
			ids = createFileDesc(index, fileId, locations, "content.ncx", out);
			index = ids[0];
			fileId = ids[1];

			// stylesheet.css
			ids = createFileDesc(index, fileId, locations, "stylesheet.css",
					out);
			index = ids[0];
			fileId = ids[1];

			// cover.html
			ids = createFileDesc(index, fileId, locations, "cover.html", out);
			index = ids[0];
			fileId = ids[1];

			String coverImg = getCoverImage(resource, resourceType);
			String extName = coverImg.substring(coverImg.lastIndexOf(".") + 1);
			velocityMap.put("extName", extName);
			// 获取相应尺寸的图片
			coverImg = coverImg.replaceAll("\\.", width + ".");
			ids = createFileDesc(index, fileId, locations, "images/cover."
					+ extName, out);
			index = ids[0];
			fileId = ids[1];

			velocityMap.put("chapters", chapters);

			for (Object chapter : chapters) {
				String[] imgs = (String[]) BeanUtils.forceGetProperty(chapter,
						"images");
				for (String img : imgs) {
					String imgFile = "images/" + img;
					ids = createFileDesc(index, fileId, locations, imgFile, out);
					index = ids[0];
					fileId = ids[1];
				}
				String filename = "chapters/"
						+ BeanUtils.forceGetProperty(chapter, "chapterIndex")
						+ ".html";
				ids = createFileDesc(index, fileId, locations, filename, out);
				index = ids[0];
				fileId = ids[1];
			}
			debug("Finish content file  desc regional");
			// 内容文件索引区段 =======================END

			// 文件内容生成区段 BEGIN======================
			// container.xml
			index = createFileContent(index, locations, "container.xml", out,
					"container.xml", velocityMap);

			// content.opf
			index = createFileContent(index, locations, "content.opf", out,
					"OEBPS/content.opf", velocityMap);

			// content.ncx
			index = createFileContent(index, locations, "content.ncx", out,
					"OEBPS/content.ncx", velocityMap);

			// stylesheet.css
			index = createFileContent(index, locations, "stylesheet.css", out,
					"OEBPS/stylesheet.css", velocityMap);

			// cover.html
			index = createFileContent(index, locations, "cover.html", out,
					"OEBPS/cover.html", velocityMap);

			try {
				index = createFile("images/cover." + extName, coverImg, resDir,
						index, locations, out);
			} catch (Exception e) {
				error("Releation id=" + rel.getId() + " create file error.", e);
			}
			// chapters
			for (Object chapter : chapters) {

				String[] imgs = (String[]) BeanUtils.forceGetProperty(chapter,
						"images");
				for (String img : imgs) {
					String imgFile = "images/" + img;
					index = createFile(imgFile, img, resDir, index, locations,
							out);
				}
				velocityMap.put("chapterImages", imgs);
				velocityMap.put("chapterTitle", BeanUtils.forceGetProperty(
						chapter, "name"));
				String content = "";
				if (ResourceAll.RESOURCE_TYPE_BOOK.equals(resourceType)) {
					content = (String) BeanUtils.forceGetProperty(chapter,
							"bContent");

				} else {
					content = (String) BeanUtils.forceGetProperty(chapter,
							"content");

				}
				if (content == null) {
					content = "";
				}
				content = content.replaceAll("\r\n", "\n");
				content = content.replaceAll("\n", "</p><p>");
				velocityMap.put("chapterContent", content);
				String filename = "chapters/"
						+ BeanUtils.forceGetProperty(chapter, "chapterIndex")
						+ ".html";
				index = createFileContent(index, locations, filename, out,
						"OEBPS/chapter.html", velocityMap);

			}

			debug("Finish  file  content regional");
			debug("Ueb File size = " + index);
			// 文件内容生成区段 ======================END

			out.flush();
			out.close();
			fout.close();

			RandomAccessFile raf = new RandomAccessFile(new File(dir), "rw");

			UebLocation locFileLength = locations.get("fileLength");
			locFileLength.value = ((Integer) index).longValue();

			for (Map.Entry<String, UebLocation> entry : locations.entrySet()) {

				UebLocation location = entry.getValue();

				String infos = location.name + ":";
				infos += location.index + ":";

				if (location.value == null)
					continue;

				if (location.type.equals(UebLocation.Type.Long)) {
					raf.seek(location.index);
					raf.writeLong((Long) location.value);
					infos += (Long) location.value;
				} else if (location.type.equals(UebLocation.Type.TwoLong)) {
					raf.seek(location.index);
					Long[] vs = (Long[]) location.value;
					raf.writeLong(vs[0]);
					raf.writeLong(vs[1]);
					infos += vs[0] + ":";
					infos += vs[1];
				}
				debug(infos);
			}
			raf.close();
			
			resourceService.rsyncUploadFile(ResourceUtil.RSYNC_TYPE_ADD, new String[]{dir});

		} catch (Exception e) {

			error("Releation id=" + rel.getId(), e);
		}

	}

	private String getCoverImage(ResourceAll resource, Integer resourceType) {
		String cover = "";
		try {
			if (ResourceAll.RESOURCE_TYPE_BOOK.equals(resourceType)) {
				cover = BeanUtils.forceGetProperty(resource, "bookPic")
						.toString();
			} else {
				try {
					cover = BeanUtils.forceGetProperty(resource, "image")
							.toString();
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cover;
	}

	private Object getNewChapter(Integer resourceType) {
		if (ResourceAll.RESOURCE_TYPE_BOOK.equals(resourceType)) {
			return new EbookChapter();
		} else if (ResourceAll.RESOURCE_TYPE_COMICS.equals(resourceType)) {
			return new ComicsChapter();
		} else if (ResourceAll.RESOURCE_TYPE_MAGAZINE.equals(resourceType)) {
			return new MagazineChapter();
		} else if (ResourceAll.RESOURCE_TYPE_NEWSPAPER.equals(resourceType)) {
			return new NewsPapersChapter();
		}
		return null;
	}

	public static void main(String[] args) {
		String resourceId = "10000089";
		Integer resourceType = Integer.parseInt(resourceId.substring(0, 1));
		System.out.println(resourceType);
	}

}

class UebLocation {
	enum Type {
		Long, TwoLong
	}

	public int index;
	public String name;
	public Object value;
	public Type type;
}
