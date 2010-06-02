/**
 * 
 */
package com.hunthawk.reader.pps.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.pps.service.ChapterService;
import com.hunthawk.reader.pps.service.ResourceService;

/**
 * @author BruceSun
 * 
 */
public class ChapterServiceImpl implements ChapterService {

	private static Logger logger = Logger.getLogger(ChapterServiceImpl.class);

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	private static final String SIZE_FILE_NAME = "size.txt";

	// 系统存储路径,需要有结尾符,如/var/cache/
	private String filePath;

	private MemCachedClientWrapper memcached;
	private ResourceService resourceService;

	private CacheManager cacheMgr;
	private Cache ehcache;
	private String ehcacheName = "resourcechapter";

	/**
	 * 读取写入文件的编码方式
	 */
	private String encoding = "GBK";
	/**
	 * 标记该章节正在进行生成本地分页文件处理。
	 * 
	 */
	private Map<String, String> doingSplitMap = new HashMap<String, String>();

	public void setEhcacheName(String ehcacheName) {
		this.ehcacheName = ehcacheName;
	}

	public void init() {
		cacheMgr = CacheManager.create();
		ehcache = cacheMgr.getCache(ehcacheName);
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public Object[] getEbookChapterContent(String chapterId, int pageNum,
			int size) {
		Object[] objs = new Object[2];
		if (!isNeedUpdate(chapterId)) {
			Element element = ehcache.get(chapterId);
			if (element != null) {
				String value = (String) element.getValue();
				String[] vals = value.split("#");
				int count = Integer.parseInt(vals[0]);
				objs[0] = ((Double)Math.ceil((float) count / size)).intValue();

				int startPage = (pageNum - 1) * size + 1;
				int endPage = pageNum * size;
				endPage = endPage > count ? count : endPage;
				StringBuilder builder = new StringBuilder();
				String path = getChapterPath(chapterId);
				for (int i = startPage; i <= endPage; i++) {
					String filename = path + i + ".txt";
					builder.append(readFile(filename, encoding));
				}
				objs[1] = builder.toString();
				return objs;
			}
		} 
		ehcache.remove(chapterId);
		Object chapter = resourceService.getResourceChapter(chapterId);
		Integer type = Integer.parseInt(chapterId.substring(0, 1));
		String content = "";
		try {
			if (ResourceAll.RESOURCE_TYPE_BOOK.equals(type)) {
				content = (String) BeanUtils.forceGetProperty(chapter,
						"bContent");
			} else {
				content = (String) BeanUtils.forceGetProperty(chapter,
						"content");
			}
		} catch (Exception e) {
			logger.error("获取章节内容时报错", e);
		}

		if (StringUtils.isEmpty(content)) {
			objs[0] = 0;
			objs[1] = "";
			return objs;
		}

		int len = content.length();
		int count = ((Double)Math.ceil((float) len / 500)).intValue();
		objs[0] = ((Double)Math.ceil((float) count / size)).intValue();

		int startIndex = (pageNum - 1) * size * 500;
		int endIndex = (pageNum) * size * 500 ;
//		len = len - 1;
		startIndex = startIndex > len ? len : startIndex;
		endIndex = endIndex > len ? len : endIndex;
		if(pageNum == (Integer)objs[0]){
			endIndex = len;
		}
		objs[1] = typeSetting(content.substring(startIndex, endIndex));

		splitChapter(content, chapterId);

		return objs;
	}

	/**
	 * 获取章节信息文件存储目录
	 * 
	 * @param chapterId
	 * @return
	 */
	private String getChapterPath(String chapterId) {
		StringBuilder builder = new StringBuilder();
		builder.append(filePath);
		builder.append(chapterId.charAt(0));
		builder.append(File.separatorChar);
		builder.append(chapterId.substring(1, 4));
		int num = Integer.parseInt(chapterId.substring(4, 8));
		builder.append(File.separatorChar);
		builder.append(num / 1000);
		builder.append(File.separatorChar);
		builder.append(chapterId.substring(1, 8));
		builder.append(File.separatorChar);
		builder.append(chapterId.substring(chapterId.length() - 3));
		builder.append(File.separatorChar);
		return builder.toString();
	}

	/**
	 * 判断书是否更新过，从缓存中获取图书的最后更新时间，和服务器上缓存文件时间做比较，如果更新时间晚于文件时间，需要从新更新
	 * 
	 * @param chapterId
	 * @return
	 */
	private boolean isNeedUpdate(String chapterId) {
		Element element = ehcache.get(chapterId);
		String value = "";
		if (element == null) {// 读文件系统内容
			String path = getChapterPath(chapterId);
			File file = new File(path + SIZE_FILE_NAME);
			if (file.exists()) {
				value = readFile(file, encoding);
				element = new Element(chapterId, value);
				ehcache.put(element);
			} else {
				return true;
			}
		} else {
			value = (String) element.getValue();
		}

		String[] vals = value.split("#");
		try {
			Date date = DATE_FORMAT.parse(vals[1]);
			String rid = chapterId.substring(0, chapterId.length() - 3);
			// 资源更新时需要清空该缓存
			Date updateTime = getUpdateTime(rid);
			if (updateTime == null) {
				
				setUpdateDate(rid, date);
				return false;
			} else {
				
				if (updateTime.after(date)) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			logger.error("判断书是否更新过信息出错!", e);
		}
		return true;
	}

	private void setUpdateDate(String rid, Date date) {
		String key = Utility.getMemcachedKey(ResourceAll.class, rid, "updatetime");
		memcached.setAndSaveLocalMedium(key, date, 0);
	}

	private Date getUpdateTime(String rid) {
		String key = Utility.getMemcachedKey(ResourceAll.class, rid , "updatetime");
		try {
			Date updateTime = (Date) memcached.getAndSaveLocalMedium(key);
			return updateTime;
		} catch (Exception e) {
			return null;
		}
	}

	private void splitChapter(String content, String chapterId) {
		synchronized (doingSplitMap) {
			if (!doingSplitMap.containsKey(chapterId)) {
				doingSplitMap.put(chapterId, "1");
				SplitThread split = new SplitThread();
				split.setChapterid(chapterId);
				split.setContent(content);
				Thread thread = new Thread(split);
				thread.start();
			}
		}
	}

	private String readFile(String fileName, String characterEncoding) {
		return readFile(new File(fileName), characterEncoding);
	}

	private String readFile(File file, String characterEncoding) {
		String result = "";
		try {
			FileInputStream inFile = new FileInputStream(file);
			FileChannel inChannel = inFile.getChannel();
			int fileLength = (int) inChannel.size();
			ByteBuffer buf = ByteBuffer.allocate(fileLength);
			inChannel.read(buf);
			buf.flip();
			Charset charset = Charset.forName(characterEncoding);
			CharsetDecoder decoder = charset.newDecoder();
			CharBuffer charBuffer = decoder.decode(buf);
			result = charBuffer.toString();
			inChannel.close();
			inChannel = null;
			inFile.close();
			inFile = null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	private void writeFile(String fileName, String characterEncoding,
			String content) {
		OutputStreamWriter writer = null;
		OutputStream out = null;

		try {
			out = new FileOutputStream(fileName);
			writer = new OutputStreamWriter(out, characterEncoding);
			writer.write(content, 0, content.length());
			writer.flush();
		} catch (IOException e) {
			logger.error("写入文件" + fileName + "异常!!", e);
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			try {
				out.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	/**
	 * 排版
	 * 
	 * @param str
	 * @return
	 */
	private String typeSetting(String str) {
		String result = str.replaceAll("\r\n", "\n");
		return result.replaceAll("\n", "<br/>");

	}

	public static void main(String[] args) {
		ChapterServiceImpl impl = new ChapterServiceImpl();
		impl.setFilePath("/var/cache/");
		System.out.println(impl.getChapterPath("12001009123"));

		System.out.println("12001009123".substring(0,
				"12001009123".length() - 3));

		

	}

	class SplitThread implements Runnable {

		private String chapterid; // 图书章节id

		private String content; // 图书当前章节的具体内容

		public void setChapterid(String chapterid) {
			this.chapterid = chapterid;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public void run() {

			String path = getChapterPath(chapterid);
			File dir = new File(path);
			if (!dir.exists())
				dir.mkdirs();

			int size = content.length();
			int count = ((Double)Math.ceil((float) size / 500)).intValue();

			String rid = chapterid.substring(0, chapterid.length() - 3);

			Date updateTime = getUpdateTime(rid);
			if (updateTime == null) {
				updateTime = new Date();
			}
			
			String sizeValue = count + "#" + DATE_FORMAT.format(DateUtils.addMinutes(updateTime, 1));
			// 写入文件大小
			writeFile(path + SIZE_FILE_NAME, encoding, sizeValue);
			int offset = 0;
			int countChar = 500;
			for (int j = 0; j < count; j++) {

				String fileName = (j + 1) + ".txt";

				fileName = path + File.separator + fileName;
				offset = j * 500;

				if (offset + 500 > size || j==count-1)
					countChar = size - offset;
				if (logger.isDebugEnabled()) {
					logger.debug("写入文件" + fileName);
				}

				writeFile(fileName, encoding, typeSetting(content.substring(
						offset, offset + countChar)));

			}

			synchronized (doingSplitMap) {
				doingSplitMap.remove(chapterid);
			}
		}

	};

}
