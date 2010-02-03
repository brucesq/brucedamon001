/**
 * 
 */
package com.hunthawk.reader.service.resource.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.framework.util.ImageTool;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.MagazineChapter;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.NewsPapersChapter;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourceReferen;
import com.hunthawk.reader.domain.resource.ResourceResType;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.resource.Video;
import com.hunthawk.reader.domain.resource.VideoSuite;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.enhance.util.ResourceUtil;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.enhance.util.UnzipFile;
import com.hunthawk.reader.service.partner.PartnerService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.resource.UploadService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * 重新修改上传实现
 * 
 * @author BruceSun
 * 
 */
public class UploadServiceImpl implements UploadService {

	private static Logger logger = Logger.getLogger(UploadServiceImpl.class);

	private ResourceService resourceService;

	private ResourceService resourceServiceTarget;

	private SystemService systemService;

	private PartnerService partnerService;

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	public void setPartnerService(PartnerService partnerService) {
		this.partnerService = partnerService;
	}

	public ResourceService getResourceServiceTarget() {
		return resourceServiceTarget;
	}

	public void setResourceServiceTarget(ResourceService resourceServiceTarget) {
		this.resourceServiceTarget = resourceServiceTarget;
	}

	public void uploadAuthor(String authorFile, String authorDir, UserImpl user)
			throws Exception {
		InputStreamReader fr = null;
		BufferedReader br = null;
		try {
			fr = new InputStreamReader(new FileInputStream(authorFile));
			br = new BufferedReader(fr);
			String rec = null;
			String[] argsArr = null;

			// 去掉第一行数据
			br.readLine();
			while ((rec = br.readLine()) != null) {

				if (StringUtils.isEmpty(rec)) {
					continue;
				}
				argsArr = rec.split(",");
				String name = trim(argsArr[0], false, "作者名称");
				String penName = trim(argsArr[1], true, "作者笔名");

				ResourceAuthor author = resourceService
						.getResourceAuthorByName(penName);

				boolean isNew = false;
				if (author == null) {
					author = new ResourceAuthor();
					isNew = true;
				}
				author.setName(name);

				author.setPenName(penName);

				author.setInitialLetter(trim(argsArr[2], true, "作者首字母"));
				try {
					author.setSex(Integer
							.parseInt(trim(argsArr[3], true, "性别")));
				} catch (Exception e) {
					throw new Exception("性别必须是数字.");
				}
				author.setArea(trim(argsArr[4], true, "地区"));

				String imgdir = "";
				boolean hasDir = false;
				if (argsArr.length == 6) {
					imgdir = argsArr[5].trim();
					hasDir = true;
				}
				File descDir = new File(authorDir + File.separator + imgdir);
				if (hasDir && descDir.exists()) {
					File descFile = new File(descDir.getAbsolutePath(),
							"description.txt");
					if (descFile.exists()) {
						author.setIntro(FileUtils.readFileToString(descFile));
					}

				}
				author.setCreateTime(new Date());
				author.setCreatorId(user.getId());
				author.setStatus(0);

				if (isNew) {
					resourceService.addResourceAuthor(author);
				} else {
					resourceService.updateResourceAuthor(author);
				}

				if (hasDir) {
					String authorImg = getAuthorImg(descDir.getAbsolutePath());
					if (StringUtils.isNotEmpty(authorImg)) {
						File authorPic = getUploadResourceDir("author", ""
								+ author.getId());

						String fileType = authorImg.substring(
								authorImg.lastIndexOf(".") + 1).toLowerCase();
						FileUtils.copyFile(new File(descDir, authorImg),
								new File(authorPic, author.getId() + "."
										+ fileType));

						// TODO:
						File synFile = new File(authorPic, author.getId() + "."
								+ fileType);
						resourceService.rsyncUploadFile(
								ResourceUtil.RSYNC_TYPE_ADD,
								new String[] { synFile.getAbsolutePath() });

						author.setAuthorPic("author/"
								+ String.valueOf(author.getId() / 1000) + "/"
								+ author.getId() + "." + fileType);
						resourceService.updateResourceAuthor(author);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (fr != null)
					fr.close();
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	public Map<String, ResourceReferen> uploadCopyright(String dirName,
			String dir, UserImpl user) throws Exception {

		InputStreamReader fr = null;
		BufferedReader br = null;
		Map<String, ResourceReferen> map = new HashMap<String, ResourceReferen>();
		try {
			fr = new InputStreamReader(new FileInputStream(dirName));
			br = new BufferedReader(fr);
			String rec = null;
			String[] argsArr = null;
			String[] argsArrCopyR = null;
			String copyRightFile = null;

			// 去掉第一行数据
			br.readLine();
			int lineNo = 1;
			while ((rec = br.readLine()) != null) {

				lineNo++;
				String resInfo = "版权文件第" + lineNo + "行";

				if (StringUtils.isEmpty(rec)) {
					continue;
				}

				argsArr = rec.split(",");
				String identifier = argsArr[0].trim();
				try {
					Integer.parseInt(identifier);
				} catch (Exception e) {
					throw new Exception("版权标识必须是数字.");
				}
				ResourceReferen referen = new ResourceReferen();
				int j = 0;
				referen
						.setIdentifier(trim(argsArr[j++], true, resInfo
								+ "版权标识"));
				referen.setName(trim(argsArr[j++], true, resInfo + "版权方名称"));
				referen.setContactName(trim(argsArr[j++], true, resInfo
						+ "版权方联系人"));
				referen.setContactPhone(trim(argsArr[j++], true, resInfo
						+ "版权方联系人电话"));

				referen
						.setEmail(trim(argsArr[j++], true, resInfo + "版权方联系人邮箱"));
				referen
						.setAddress(trim(argsArr[j++], true, resInfo
								+ "版权方联系地址"));
				referen.setFax(trim(argsArr[j++], true, resInfo + "版权方联系传真"));

				referen.setBeginTime(strToDate(argsArr[j++].trim(), true,
						resInfo + "开始时间"));
				referen.setEndTime(strToDate(argsArr[j++].trim(), true, resInfo
						+ "结束时间"));
				String copyurl = argsArr[j];
				referen.setShowUrl(dir + File.separator + copyurl);
//				referen.setStatus(1);

				referen.setModifyTime(new Date());
				referen.setModifierId(user.getId());

				copyRightFile = getFiles(dir + File.separator + argsArr[j]);
				argsArrCopyR = copyRightFile.split("/");

				for (int i = 0; i < argsArrCopyR.length; i++) {
					if (argsArrCopyR[i].startsWith("userpor")) {
						referen.setCopyrightUse(argsArrCopyR[i]);
					}
					if (argsArrCopyR[i].startsWith("attorn")) {
						referen.setCopyrightAttorn(argsArrCopyR[i]);
					}
					if (argsArrCopyR[i].startsWith("cooperate")) {
						referen.setCooperatePro(argsArrCopyR[i]);
					}
					if (argsArrCopyR[i].startsWith("provider")) {
						referen.setProviderInfo(argsArrCopyR[i]);
					}
					if (argsArrCopyR[i].startsWith("copyright")) {
						referen.setCopyrightCheck(argsArrCopyR[i]);
					}
					if (argsArrCopyR[i].startsWith("productinfo")) {
						referen.setProductInfo(argsArrCopyR[i]);
					}
					if (argsArrCopyR[i].startsWith("mcpinfo")) {
						referen.setMcpinfo(argsArrCopyR[i]);
					}
					if (argsArrCopyR[i].startsWith("promises")) {
						referen.setPromises(argsArrCopyR[i]);
					}
					if (argsArrCopyR[i].startsWith("authorinfo")) {
						referen.setAuthorName(argsArrCopyR[i]);
					}
					if (argsArrCopyR[i].startsWith("other")) {
						referen.setCopyrightOther(argsArrCopyR[i]);
					}

				}
				map.put(copyurl, referen);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (fr != null)
					fr.close();
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return map;

	}

	public void rsyncDirectry(File directry) {
		String[] files = new String[directry.listFiles().length];
		int i = 0;
		for (File file : directry.listFiles()) {
			files[i++] = file.getAbsolutePath();
		}
		resourceService.rsyncUploadFile(ResourceUtil.RSYNC_TYPE_ADD, files);

	}

	private void updateCopyRight(ResourceReferen referen) throws Exception {
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		expressions.add(new CompareExpression("cpId", referen.getCpId(),
				CompareType.Equal));
		expressions.add(new CompareExpression("identifier", referen
				.getIdentifier(), CompareType.Equal));
		List<ResourceReferen> referens = resourceService.findResourceReferenBy(
				1, 1, "id", true, expressions);
		System.out.println("123123123"+referen.getCpId()+":"+referen.getIdentifier()+":"+referens.size());
		boolean isNew = true;
		if (referens.size() > 0) {
			
			isNew = false;
			ResourceReferen referenDb = referens.get(0);
			referen.setId(referenDb.getId());
			referen.setCreatorId(referenDb.getModifierId());
			referen.setCreateTime(referenDb.getCreateTime());
			referen.setStatus(referenDb.getStatus());
			System.out.println("update:"+referen.getId());
		}
		if (isNew) {
			referen.setCreatorId(referen.getModifierId());
			referen.setCreateTime(referen.getCreateTime());
			referen.setStatus(1);
			System.out.println("add:reference");
			resourceService.addResourceReferen(referen);
		}
		File destDir = getUploadResourceDir("referen", "" + referen.getId());
		File srcDir = new File(referen.getShowUrl());

		if (srcDir.exists() && srcDir.isDirectory()) {
			// 拷贝目录文件
			FileUtils.copyDirectory(srcDir, destDir);
			rsyncDirectry(destDir);
			referen.setShowUrl("referen/" + referen.getId() / 1000 + "/"
					+ referen.getId() + "/");
			resourceService.updateResourceReferen(referen);
		}

	}

//	public Map<String, Integer> readCopyCsvFile(String dirName, String dir,
//			UserImpl user) throws Exception {
//		InputStreamReader fr = null;
//		BufferedReader br = null;
//		Map<String, Integer> map = new HashMap<String, Integer>();
//
//		if (user.getProvider() == null) {
//			throw new Exception("用户关联的合作方为空,请联系管理员,更新用户关联的合作方信息");
//		}
//		try {
//			fr = new InputStreamReader(new FileInputStream(dirName));
//			br = new BufferedReader(fr);
//			String rec = null;
//			String[] argsArr = null;
//			String[] argsArrCopyR = null;
//			String copyRightFile = null;
//
//			// 去掉第一行数据
//			br.readLine();
//			int lineNo = 1;
//			while ((rec = br.readLine()) != null) {
//
//				lineNo++;
//				String resInfo = "版权文件第" + lineNo + "行";
//
//				if (StringUtils.isEmpty(rec)) {
//					continue;
//				}
//
//				argsArr = rec.split(",");
//
//				boolean isNew = true;
//
//				String identifier = argsArr[0].trim();
//				try {
//					Integer.parseInt(identifier);
//				} catch (Exception e) {
//					throw new Exception("版权标识必须是数字.");
//				}
//
//				Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
//				expressions.add(new CompareExpression("cpId", user
//						.getProvider().getId(), CompareType.Equal));
//				expressions.add(new CompareExpression("identifier", identifier,
//						CompareType.Equal));
//				List<ResourceReferen> referens = resourceService
//						.findResourceReferenBy(1, 1, "id", true, expressions);
//
//				ResourceReferen referen = null;
//				if (referens.size() > 0) {
//					referen = referens.get(0);
//					isNew = false;
//				} else {
//					referen = new ResourceReferen();
//				}
//
//				int j = 0;
//				referen
//						.setIdentifier(trim(argsArr[j++], true, resInfo
//								+ "版权标识"));
//				referen.setName(trim(argsArr[j++], true, resInfo + "版权方名称"));
//				referen.setContactName(trim(argsArr[j++], true, resInfo
//						+ "版权方联系人"));
//				referen.setContactPhone(trim(argsArr[j++], true, resInfo
//						+ "版权方联系人电话"));
//
//				referen
//						.setEmail(trim(argsArr[j++], true, resInfo + "版权方联系人邮箱"));
//				referen
//						.setAddress(trim(argsArr[j++], true, resInfo
//								+ "版权方联系地址"));
//				referen.setFax(trim(argsArr[j++], true, resInfo + "版权方联系传真"));
//
//				referen.setBeginTime(strToDate(argsArr[j++].trim(), true,
//						resInfo + "开始时间"));
//				referen.setEndTime(strToDate(argsArr[j++].trim(), true, resInfo
//						+ "结束时间"));
//				referen.setShowUrl(argsArr[j]);
//				
//				if (isNew) {
//					referen.setStatus(1);
//					referen.setCreatorId(user.getId());
//					referen.setCreateTime(new Date());
//				}
//				referen.setCpId(user.getProvider().getId());
//
//				referen.setModifyTime(new Date());
//				referen.setModifierId(user.getId());
//
//				copyRightFile = getFiles(dir + File.separator + argsArr[j]);
//				argsArrCopyR = copyRightFile.split("/");
//
//				for (int i = 0; i < argsArrCopyR.length; i++) {
//					if (argsArrCopyR[i].startsWith("userpor")) {
//						referen.setCopyrightUse(argsArrCopyR[i]);
//					}
//					if (argsArrCopyR[i].startsWith("attorn")) {
//						referen.setCopyrightAttorn(argsArrCopyR[i]);
//					}
//					if (argsArrCopyR[i].startsWith("cooperate")) {
//						referen.setCooperatePro(argsArrCopyR[i]);
//					}
//					if (argsArrCopyR[i].startsWith("provider")) {
//						referen.setProviderInfo(argsArrCopyR[i]);
//					}
//					if (argsArrCopyR[i].startsWith("copyright")) {
//						referen.setCopyrightCheck(argsArrCopyR[i]);
//					}
//					if (argsArrCopyR[i].startsWith("productinfo")) {
//						referen.setProductInfo(argsArrCopyR[i]);
//					}
//					if (argsArrCopyR[i].startsWith("mcpinfo")) {
//						referen.setMcpinfo(argsArrCopyR[i]);
//					}
//					if (argsArrCopyR[i].startsWith("promises")) {
//						referen.setPromises(argsArrCopyR[i]);
//					}
//					if (argsArrCopyR[i].startsWith("authorinfo")) {
//						referen.setAuthorName(argsArrCopyR[i]);
//					}
//					if (argsArrCopyR[i].startsWith("other")) {
//						referen.setCopyrightOther(argsArrCopyR[i]);
//					}
//
//				}
//
//				if (isNew) {
//					resourceService.addResourceReferen(referen);
//				} else {
//					resourceService.updateResourceReferen(referen);
//				}
//
//				map.put(referen.getShowUrl(), referen.getId());
//
//				File destDir = getUploadResourceDir("referen", ""
//						+ referen.getId());
//				File srcDir = new File(dir + File.separator
//						+ referen.getShowUrl());
//				// 拷贝目录文件
//				FileUtils.copyDirectory(srcDir, destDir);
//				rsyncDirectry(destDir);
//				referen.setShowUrl("referen/" + referen.getId() / 1000 + "/"
//						+ referen.getId() + "/");
//				resourceService.updateResourceReferen(referen);
//
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		} finally {
//			try {
//				if (fr != null)
//					fr.close();
//				if (br != null)
//					br.close();
//			} catch (IOException ex) {
//				ex.printStackTrace();
//			}
//		}
//		return map;
//
//	}

	/**
	 * 清空资源的附属属性,如章节、卷、关联的类型
	 * 
	 * @param resourceId
	 * @param type
	 */
	private void clearResourceAdditionInfo(String resourceId,
			Integer resourceType) {
		// 删除类型关联信息
		if (!resourceId.startsWith(ResourceType.TYPE_BOOK.toString())) {
			// 图书补传暂不修改类型
			resourceService.deleteResourceResType(resourceId);
		}
		// 删除卷信息
		List<EbookTome> tomes = resourceService
				.getEbookTomeByResourceId(resourceId);
		for (EbookTome tome : tomes) {
			resourceService.deleteEbookTome(tome);
		}
		List chapters = resourceService.getResourceChapter(getNewChapter(
				resourceType).getClass(), resourceId);
		for (int i = 0; i < chapters.size(); i++) {
			Object chapter = chapters.get(i);
			resourceService.deleteResourceChapter(chapter, resourceType);
		}
	}

	private void removeCoverImg(File dir) {
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				if (file.getName().startsWith("cover")) {
					try {
						FileUtils.forceDelete(file);
					} catch (IOException e) {
					}
					return;
				}
			}
		}
	}

	/**
	 * 上传资源处理类
	 * 
	 * @param dirName
	 *            资源目录
	 * @param csvFileName
	 *            csv文件
	 * @param copyMap
	 *            版权映射
	 * @param resourceType
	 *            资源类型
	 * @param errInfo
	 *            错误信息
	 * @throws Exception
	 */
	public void uploadResource(String dirName, String csvFileName,
			Map<String, ResourceReferen> copyMap, Integer resourceType,
			ArrayList<String> errInfo, UserImpl user) throws Exception {

		String format = getResourceCsvFormat(resourceType);
		String[] column = format.split(",");
		InputStreamReader fr = null;
		BufferedReader br = null;
		try {
			fr = new InputStreamReader(new FileInputStream(dirName
					+ File.separator + csvFileName));
			br = new BufferedReader(fr);
			String rec = null;
			br.readLine();// 去掉第一行数据

			int lineNo = 1;
			while ((rec = br.readLine()) != null) {
				if (StringUtils.isEmpty(rec)) {
					continue;
				}
				lineNo++;
				String resInfo = "文件" + csvFileName + "第" + lineNo + "行";
				String[] cells = rec.split(",");

				boolean isNew = true;
				// 如果是书续传，不更新书基本信息
				boolean isUpdate = true;
				ResourceAll resource = getNewResource(resourceType);

				// 资源分类内容
				String resourceTypeContent = "";
				String resDir = "";
				try {
					for (int i = 0; i < column.length && i < cells.length; i++) {
						if (column[i].startsWith("$")) {// ID
							if (!cells[i].equals("0")) {
								resource = resourceService.getResource(cells[i]
										.trim(), resourceType);
								if (resource == null) {
									// 修改时资源ID必须有效
									throw new Exception(resInfo
											+ "资源表中不存在ID值为：" + cells[i]
											+ "的记录.");
								}
								isNew = false;
								if (resourceType == ResourceType.TYPE_BOOK) {
									isUpdate = false;
								}
							}
						}

						if (column[i].startsWith("*") && isUpdate) {// 必填
							
							
							char fieldType = column[i].charAt(1);
							String[] columnFileds = column[i].split(":");
							String field = columnFileds[0].substring(2);
							String desc = columnFileds[1];
							Object value = getRealObject(fieldType, cells[i],
									resInfo + desc, true);

							if (value != null) {

								BeanUtils.forceSetProperty(resource, field,
										value);
							}

						}

						if (column[i].startsWith("+") && isUpdate) {// 可选
							char fieldType = column[i].charAt(1);
							String[] columnFileds = column[i].split(":");
							String field = columnFileds[0].substring(2);
							String desc = columnFileds[1];
							Object value = getRealObject(fieldType, cells[i],
									resInfo + desc, false);

							if (value != null) {
								BeanUtils.forceSetProperty(resource, field,
										value);
							}
						}
						if (column[i].startsWith("!") && isUpdate) {// 作者

							String authorIdStr = doAuthor(trim(cells[i], true,
									"作者"), user.getId());
							resource.setAuthorId(authorIdStr);
						}

						if (column[i].startsWith("@") && isUpdate) {// 资源分类
							resourceTypeContent = cells[i].trim();
						}

						if (column[i].startsWith("#") && isUpdate) {// CPID
							String[] columnFileds = column[i].split(":");
							String desc = columnFileds[1];
							Integer cpid = null;
							try {
								cpid = Integer.parseInt(cells[i]);
							} catch (Exception e) {
								throw new Exception(resInfo + "[" + cells[i]
										+ "]" + "CPID值不正确.");
							}
							Provider provider = partnerService
									.getProvider(cpid);
							if (provider == null)
								throw new Exception(resInfo + "[" + cells[i]
										+ "]" + desc + "不存在");
							if (provider.getProviderType() == 0) {
								throw new Exception(resInfo + "[" + cells[i]
										+ "]" + "CPID对应的合作方是SP，请使用CP合作方");
							}
							if (provider.getStatus() == 5) {
								throw new Exception(resInfo + "[" + cells[i]
										+ "]" + "CPID状态为下线状态,不能进行内容管理");
							}
							if (user.isRoleProvider()
									&& !user.getProvider().getId().equals(
											provider.getId())) {
								throw new Exception(resInfo + "[" + cells[i]
										+ "]" + "您没有权限上传该CPID下的内容，请联系系统管理员.");
							}
							resource.setCpId(provider.getId());
						}
						if (column[i].startsWith("^") && isUpdate) {// 版权
							String copyRightName = trim(cells[i], false, "");
							if (StringUtils.isNotEmpty(copyRightName)) {
								ResourceReferen copyRight = copyMap
										.get(copyRightName);
								if (copyRight == null) {
									throw new Exception(resInfo + copyRightName
											+ "版权信息不存在");
								} else {
									copyRight.setCpId(resource.getCpId());
									try {
										updateCopyRight(copyRight);
									} catch (Exception e) {
										throw new Exception(resInfo
												+ copyRightName + "版权信息不正确"
												+ e.getMessage());
									}
									Integer copyRightId = copyRight.getId();
									if (!isNew) {
										resource.setLastCopyrightId(resource
												.getCopyrightId());
									}
									resource.setCopyrightId(copyRightId);
								}
							}
						}
						if (column[i].startsWith("%")) {// 内容
							resDir = dirName + File.separator
									+ trim(cells[i], true, resInfo + "内容目录");
							if (isUpdate) {
								checkContentDir(resDir, resource, resourceType,
										resInfo);
							} else {
								checkOnlyChapter(resDir, resource,
										resourceType, resInfo);
							}
						}
					}

					resource.setModifierId(user.getId());
					resource.setModifyTime(new Date());
					resource.setStatus(1);
					if (isNew) {
						if (resourceService.isResourceExists(resourceType,
								resource)) {
							errInfo.add(resInfo + "资源重复");
							continue;
						}
						resource.setCreateTime(new Date());
						resource.setCreatorId(user.getId());
						resource.setDownnum(1);
						resourceService.addResource(resource, resourceType);
					} else {

						resourceService.updateResource(resource, resourceType);
						clearResourceAdditionInfo(resource.getId(),
								resourceType);
					}
					// 处理内容章节信息
					// 图片处理

					//视频处理文件
					if(ResourceType.TYPE_VIDEO.equals(resourceType)){
						parseVideo(resDir,resource.getId(),resourceType);
					}else{
						parseDirectory(resDir, "directory.txt", resource.getId(),
							resourceType, resInfo);
					}
					File destDir = getUploadResourceDir(resourceType, resource
							.getId());
					File srcDir = new File(resDir + File.separator + "image");
					if (resourceType == ResourceType.TYPE_BOOK && !isNew) {
						removeCoverImg(srcDir);
						FileUtils.copyDirectory(srcDir, destDir);
					} else {
						FileUtils.copyDirectory(srcDir, destDir);
						resizeCoverFile(destDir.getAbsolutePath(),
								getCoverImg(resource));
					}
					if(resourceType == ResourceType.TYPE_VIDEO){
						File videoDir = new File(resDir + File.separator + "video");
						FileUtils.copyDirectory(videoDir, destDir);
					}
					
					rsyncDirectry(destDir);

					// 处理资源与资源类别关系
					try {
						if (StringUtils.isNotEmpty(resourceTypeContent)
								&& !(resourceType == ResourceType.TYPE_BOOK && !isNew)) {
							String[] typeArr = resourceTypeContent.split("\\^");
							for (String str : typeArr) {
								ResourceResType resTypeRel = new ResourceResType();
								resTypeRel.setRid(resource.getId());

								ResourceType resType = resourceService
										.getResourceType(Integer.parseInt(str));
								if (resType == null) {
									errInfo.add(resInfo + "不存在ID=" + str
											+ "的资源类别");
									continue;
								}
								if (!resType.getShowType().equals(resourceType)) {
									errInfo.add(resInfo + "ID=" + str
											+ "的资源类别类型不正确["
											+ resType.getSType() + "]");
									continue;
								}
								Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
								expressions.add(new CompareExpression("parent",
										resType, CompareType.Equal));

								if (resourceService
										.getResourceTypeResultCount(expressions) > 0) {
									// 处理子分类情况
									String name = resType.getName() + "默认创建";
									expressions.add(new CompareExpression(
											"name", name, CompareType.Equal));

									List<ResourceType> types = resourceService
											.findResourceTypeBy(1, 1, "name",
													true, expressions);
									if (types.size() > 0) {
										resType = types.get(0);
									} else {
										ResourceType newType = new ResourceType();
										newType.setName(name);
										newType.setCreateTime(new Date());
										newType.setCreatorId(user.getId());
										newType.setParent(resType);
										newType.setShowType(resType
												.getShowType());
										resourceServiceTarget
												.addResourceType(newType);
										resType = newType;
									}
								}
								resTypeRel.setResTypeId(resType.getId());
								resourceService.addResourceResType(resTypeRel);
							}
						}
					} catch (Exception e) {
						logger.error(resInfo + "处理资源类别时有错误."
								+ resourceTypeContent, e);
						errInfo.add(resInfo + "处理资源类别时有错误."
								+ resourceTypeContent + e.getMessage());
					}

				} catch (Exception e) {
					logger.error(resInfo, e);
					errInfo.add(e.getMessage());
				}
			}
		} catch (IOException e) {
			logger.error("上传文件时出错", e);
			errInfo.add(e.getMessage());
		} finally {
			try {
				if (fr != null)
					fr.close();
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	private String getCoverImg(ResourceAll resource) throws Exception {

		if (resource instanceof Ebook) {
			return (String) BeanUtils.forceGetProperty(resource, "bookPic");
		} else {
			return (String) BeanUtils.forceGetProperty(resource, "image");
		}
	}

	private void checkOnlyChapter(String dir, ResourceAll resource,
			Integer resourceType, String prefixInfo) throws Exception {
		File contentDir = new File(dir);
		System.out.println("file:"+contentDir.getName());
		if (!contentDir.exists()) {
			throw new Exception(prefixInfo + "内容目录不存在");
		}
		String imgDirStr = dir + File.separator + "image";
		checkChapter(dir, resourceType, prefixInfo);
	}

	/**
	 * 检查内容目录是否正确
	 * 
	 * @param dir
	 * @param resource
	 * @param resourceType
	 * @param prefixInfo
	 * @throws Exception
	 */
	private void checkContentDir(String dir, ResourceAll resource,
			Integer resourceType, String prefixInfo) throws Exception {
		File contentDir = new File(dir);
		System.out.println("file:"+contentDir.getName());
		if (!contentDir.exists()) {
			throw new Exception(prefixInfo + "内容目录不存在");
		}
		// 检查图片目录
		String imgDirStr = dir + File.separator + "image";
		File imgDir = new File(imgDirStr);
		if (!imgDir.exists()) {
			throw new Exception(prefixInfo + "图片目录不存在");
		}
		// 检查封面图片
		String cover = getCoverImg(imgDirStr);
		if (StringUtils.isEmpty(cover)) {
			throw new Exception(prefixInfo + "缺少封面图片.");
		}
		if (ResourceAll.RESOURCE_TYPE_BOOK.equals(resourceType)) {
			BeanUtils.forceSetProperty(resource, "bookPic", cover);
		} else {
			try {
				BeanUtils.forceSetProperty(resource, "image", cover);
			} catch (Exception e) {
			}
		}

		// 解析描述文件
		String[] intros = parseDescription(dir + File.separator
				+ "description.txt", prefixInfo);
		if (StringUtils.isNotEmpty(intros[0])) {// 简介
			resource.setCComment(intros[0]);
		}
		if (StringUtils.isNotEmpty(intros[1])) {// 长简介
			resource.setIntroLon(intros[1]);
		}
		if (StringUtils.isNotEmpty(intros[2])) {// 前言
			try {
				BeanUtils.forceSetProperty(resource, "rFirst", intros[2]);
			} catch (Exception e) {
			}
		}
		if (StringUtils.isNotEmpty(intros[3])) {// 后续
			try {
				BeanUtils.forceSetProperty(resource, "rAfter", intros[3]);
			} catch (Exception e) {
			}
		}

		if (!ResourceType.TYPE_VIDEO.equals(resourceType)) {
			int words = checkChapter(dir, resourceType, prefixInfo);
			resource.setWords(words);
		}
	}

	/**
	 * 检查章节
	 * 
	 * @param dir
	 * @param resourceType
	 * @param resInfo
	 * @throws Exception
	 */
	private int checkChapter(String dir, Integer resourceType, String resInfo)
			throws Exception {
		String content = readDateStr(dir + File.separator + "directory.txt");
		String[] argsArr = content.split("§§");
		String[] titles = argsArr[1].split("/;/");
		boolean needImage = false;
		boolean needTxt = false;
		int words = 0;
		if (ResourceAll.RESOURCE_TYPE_BOOK.equals(resourceType)
				|| ResourceAll.RESOURCE_TYPE_NEWSPAPER.equals(resourceType)) {
			needTxt = true;
		}
		if (ResourceAll.RESOURCE_TYPE_COMICS.equals(resourceType)) {
			needImage = true;
		}
		Map<Integer, List<String>> imgMap = readImage(dir + File.separator
				+ "image");

		for (int i = 1; i < titles.length; i++) {

			File txtFile = new File(dir + File.separator + "txt"
					+ File.separator + i + ".txt");
			boolean fileExist = true;
			boolean imgExist = true;
			if (!txtFile.exists()) {
				if (needTxt)
					throw new Exception(resInfo + "第" + i + "章" + titles[i]
							+ "没有相应的文本内容文件");
				fileExist = false;
			} else {
				words += FileUtils.readFileToString(txtFile).length();
			}
			if (!imgMap.containsKey(i)) {
				if (needImage)
					throw new Exception(resInfo + "第" + i + "章" + titles[i]
							+ "没有相应的图片文件");
				imgExist = false;
			}
			if (!fileExist && !imgExist) {
				throw new Exception(resInfo + "第" + i + "章" + titles[i]
						+ "没有相应的图片和文本文件");
			}
		}
		return words;
	}

	/**
	 * 获取资源对象
	 * 
	 * @param type
	 * @param value
	 * @param prefixInfo
	 * @param isNeed
	 * @return
	 * @throws Exception
	 */
	private Object getRealObject(char type, String value, String prefixInfo,
			boolean isNeed) throws Exception {
		switch (type) {
		case 'S':
			return trim(value, isNeed, prefixInfo);
		case 'D':
			return strToDate(value, isNeed, prefixInfo);
		case 'N':
			return getIntValue(value, isNeed, prefixInfo);
		}
		return null;
	}

	private Integer getIntValue(String value, boolean isNeed, String errInfo)
			throws Exception {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			if (isNeed)
				throw new Exception(errInfo + "[" + value + "]格式错误");
		}
		return null;
	}

	/**
	 * 获取资源解析格式
	 * 
	 * @param resourceType
	 * @return
	 */
	private String getResourceCsvFormat(Integer resourceType) {
		String key = "";
		if (ResourceAll.RESOURCE_TYPE_BOOK.equals(resourceType)) {
			key = "ebook_format";
		} else if (ResourceAll.RESOURCE_TYPE_COMICS.equals(resourceType)) {
			key = "comics_format";
		} else if (ResourceAll.RESOURCE_TYPE_MAGAZINE.equals(resourceType)) {
			key = "magazine_format";
		} else if (ResourceAll.RESOURCE_TYPE_NEWSPAPER.equals(resourceType)) {
			key = "newspaper_format";
		}else if (ResourceAll.RESOURCE_TYPE_VIDEO.equals(resourceType)) {
			key = "video_format";
		}
		Variables var = systemService.getVariables(key);
		return var.getValue();
	}

	protected File getUploadResourceDir(Integer resourceType, String id) {
		String key = "notfound";
		if (ResourceAll.RESOURCE_TYPE_BOOK.equals(resourceType)) {
			key = "ebook";
		} else if (ResourceAll.RESOURCE_TYPE_COMICS.equals(resourceType)) {
			key = "comics";
		} else if (ResourceAll.RESOURCE_TYPE_MAGAZINE.equals(resourceType)) {
			key = "magazine";
		} else if (ResourceAll.RESOURCE_TYPE_NEWSPAPER.equals(resourceType)) {
			key = "newspaper";
		}else if (ResourceAll.RESOURCE_TYPE_VIDEO.equals(resourceType)) {
			key = "video";
		}
		return getUploadResourceDir(key, id);
	}

	public String getResourceFileDir(String resourceId, Integer resourceType) {
		return getUploadResourceDir(resourceType, resourceId).getAbsolutePath();
	}

	private Date strToDate(String strD, boolean isNeed, String errInfo)
			throws Exception {

		Date date = ToolDateUtil.stringToDate(strD.trim(), "yyyy-MM-dd");
		if (date == null) {
			date = ToolDateUtil.stringToDate(strD.trim(), "yyyy/MM/dd");
		}
		if (date == null) {
			date = ToolDateUtil.stringToDate(strD.trim(), "yyyy/MM");
		}
		if (date == null) {
			date = ToolDateUtil.stringToDate(strD.trim(), "yyyy-MM");
		}
		if (date == null) {
			date = ToolDateUtil.stringToDate(strD.trim(), "yyyy");
		}
		if (date == null && isNeed)
			throw new Exception(errInfo + "[" + strD + "]格式错误");

		return date;
	}

	/*
	 * 读取目录下所有文件 //return String
	 * 
	 */
	private String getFiles(String dirname) throws Exception {
		File dir = new File(dirname);
		if (!dir.exists()) {
			throw new Exception(dirname + "目录不存在");
		}
		File[] files = dir.listFiles();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < files.length; i++) {
			sb.append(files[i].getName()).append("/");
		}
		return sb.toString();

	}

	private String doAuthor(String str, Integer ursrId) throws Exception {
		StringBuffer sb = new StringBuffer();
		String[] authArr = str.split("/");
		for (int j = 0; j < authArr.length; j++) {
			ResourceAuthor author = null;
			author = resourceService.getResourceAuthorByName(authArr[j].trim());
			if (author != null) {
				sb.append(author.getId()).append("|");
			} else {
				author = new ResourceAuthor();
				author.setName(authArr[j]);
				author.setPenName(authArr[j]);
				author.setCreateTime(new Date());
				author.setCreatorId(ursrId);
				author.setStatus(0);
				resourceService.addResourceAuthor(author);
				sb.append(author.getId()).append("|");
			}
		}
		return "|".concat(sb.toString());
	}

	protected File getUploadResourceDir(String typeName, String id) {
		// 从数据库配置信息中得到服务器存放上传文件的路径
		Variables var = systemService.getVariables("media_dir");
		int idInt = 0;
		// 类别文件夹是否存在
		String tName = var.getValue() + File.separator + typeName;

		File tNameDir = new File(tName);
		if (!tNameDir.exists())
			tNameDir.mkdirs();

		if (typeName.equals("referen") || typeName.equals("author")) {// 是版权，版权ID不是8位的，是自增
			idInt = Integer.parseInt(id);
		} else {
			// 资源ID是8位数据
			idInt = Integer.parseInt(id.substring(1));
		}
		// 资源ID取整后除以1000，整数存建目录,即每个目录下存 1000个文件
		int idIntDir = idInt / 1000;
		String chDir = tNameDir + File.separator + idIntDir;
		File chNameDir = new File(chDir);
		if (!chNameDir.exists())
			chNameDir.mkdirs();

		if (typeName.equals("author")) {
			return chNameDir;
		}

		String uploadDir = chNameDir + File.separator + id;

		File unzipDir = new File(uploadDir);
		if (!unzipDir.exists())
			unzipDir.mkdirs();

		return unzipDir;
	}
	
	/**
	 * 处理在线观看
	 * @param fileDir
	 */
	private void processOnlineVideo(String fileDir){
		//处理UC播放器
		String videoDir = fileDir+File.separator+"video";
		File file = new File(videoDir);
		List<String> onlineFiles = new ArrayList<String>();
	
		if(file.exists() && file.isDirectory()){
			File[] videos = file.listFiles();
			for(File vf : videos){
				if(vf.getName().endsWith(".ucs")){
					String filename = vf.getName();
					int index = filename.lastIndexOf("_");
					String targetFileName = filename;
					if(index > 0){
						targetFileName = filename.substring(0,index)+".mp4";
						logger.info("targetFileName:"+targetFileName);
					}else{
						targetFileName = vf.getName().replaceAll("ucs", "mp4");
					}
					
					onlineFiles.add(getFilePathDir(vf.getAbsolutePath())+File.separator+targetFileName);
				}
			}
		}
		String onlineDir = getOnlineVideoDir()+File.separator;
		for(String online : onlineFiles){
			File srcFile = new File(online);
			logger.info(online);
			if(srcFile.exists()){
				File destFile = new File(onlineDir+srcFile.getName());
				try{
					FileUtils.forceDeleteOnExit(destFile);
					FileUtils.moveFile(srcFile, destFile);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	private String getOnlineVideoDir(){
		// 从数据库配置信息中得到服务器存放上传文件的路径
		Variables var = systemService.getVariables("media_dir");
		int idInt = 0;
		// 类别文件夹是否存在
		String tName = var.getValue() + File.separator + "onlinevideo";

		File tNameDir = new File(tName);
		if (!tNameDir.exists())
			tNameDir.mkdirs();
		return tName;
	}
	
	
	private void parseVideo(String fileDir,String resourceId,Integer type){
		String videoDir = fileDir+File.separator+"video";
		processOnlineVideo(fileDir);
		File file = new File(videoDir);
		if(file.exists() && file.isDirectory()){
			File[] videos = file.listFiles();
			int i = 0;
			for(File vf : videos){
				i++;
				VideoSuite vs = new VideoSuite();
				int index = vf.getName().lastIndexOf("_");
				if(index > 0){
					String filedesc = vf.getName().substring(index+1);
					index = filedesc.indexOf(".");
					filedesc = filedesc.substring(0,index);
					vs.setFiledesc(filedesc);
				}else{
					vs.setFiledesc("");
				}
				vs.setFilename(vf.getName());
				
				vs.setResourceId(resourceId);
				vs.setSize(((Long)vf.length()).intValue());
				vs.setType(getFileExtName(vf.getName()));
				vs.setChapterIndex(i);
				try{
					String destfile = vf.getAbsolutePath().replaceAll(vf.getName(), vs.getFilename());
					if(!destfile.equals(vf.getAbsolutePath())){
						FileUtils.moveFile(vf, new File(destfile));
					}
					resourceService.addResourceChapter(vs, type);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 解析描述文件 返回0简介，1长简介，2序，3后续
	 * 
	 * @param filename
	 * @param preInfo
	 * @throws Exception
	 */
	public static String[] parseDescription(String filename, String preInfo)
			throws Exception {
		File file = new File(filename);
		if (!file.exists()) {
			logger.info(preInfo + " 描述文件不存在.");
		}
		String bLon = FileUtils.readFileToString(file);
		// String[] bLonArr;
		String[] result = new String[4];
		// if (bLon.startsWith("§")) {
		// bLonArr = bLon.split("§§");
		// int aRLeng = bLonArr.length;
		// if (aRLeng == 4) {// 全都有数据
		// result[0] = bLonArr[0].substring(1);
		// result[1] = bLonArr[1];
		// result[2] = bLonArr[2].substring(2);
		// result[3] = bLonArr[3].substring(2);
		// } else if (aRLeng == 3) {// 序或后续其中一个有
		// // 去掉§
		// result[0] = bLonArr[0].substring(1);
		// result[1] = bLonArr[1];
		// if (bLonArr[2].startsWith("§1")) {
		// // 去掉§1
		//
		// result[2] = bLonArr[2].substring(2);
		// }
		// if (bLonArr[2].startsWith("§2")) {
		// // 去掉§2
		//
		// result[3] = bLonArr[2].substring(2);
		// }
		//
		// } else {// ==2，序或后续都没有
		// // 去掉§
		//
		// result[0] = bLonArr[0].substring(1);
		// result[1] = bLonArr[1];
		// }

		// }
		int index = bLon.indexOf("§");
		if(index < 0){
			result[0] = bLon;
		}
		while (index >= 0) {

			try {
				bLon = bLon.substring(index + 1);
			} catch (Exception e) {
				break;
			}

			int nextIndex = bLon.indexOf("§");
			boolean hasNext = true;
			if (nextIndex < 0) {
				nextIndex = bLon.length();
				hasNext = false;
			}

			if (bLon.length() <= 3)
				break;

			if (bLon.charAt(0) == '§' && bLon.charAt(1) != '§') {// 长简介
				bLon = bLon.substring(1);
				nextIndex = bLon.indexOf("§");
				hasNext = true;
				if (nextIndex < 0) {
					nextIndex = bLon.length();
					hasNext = false;
				}
				result[1] = bLon.substring(0, nextIndex);
			} else if (bLon.charAt(0) == '§' && bLon.charAt(1) == '§'
					&& bLon.charAt(2) == '1') {
				if (bLon.length() >= 3) {
					bLon = bLon.substring(3);
					nextIndex = bLon.indexOf("§");
					hasNext = true;
					if (nextIndex < 0) {
						nextIndex = bLon.length();
						hasNext = false;
					}
					result[2] = bLon.substring(0, nextIndex);
				}
			} else if (bLon.charAt(0) == '§' && bLon.charAt(1) == '§'
					&& bLon.charAt(2) == '2') {
				if (bLon.length() >= 3) {
					bLon = bLon.substring(3);
					nextIndex = bLon.indexOf("§");
					hasNext = true;
					if (nextIndex < 0) {
						nextIndex = bLon.length();
						hasNext = false;
					}
					result[3] = bLon.substring(0, nextIndex);
				}
			} else if (bLon.charAt(0) != '§') {// 短简介
				if (bLon.length() > 0) {
					result[0] = bLon.substring(0, nextIndex);
				}
			}
			if (hasNext) {
				index = nextIndex;
			} else {
				index = -1;
			}
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
//		String name = "1_176.jpg";
//		String[] strs = name.split("_");
//		System.out.println(strs[strs.length - 1]);
//		if (strs[strs.length - 1].startsWith("176")
//				|| strs[strs.length - 1].startsWith("240")) {
//			System.out.println("OK");
//		}
//		String file = "2_smooth.mp4";
//		System.out.println(file.lastIndexOf("_"));
		String file1 ="D:\\vae\\aa\\ss/asdas.xx";
		System.out.println(getFilePathDir(file1));

		// String str = "1^2";
		// System.out.println(str.split("\\^").length);
		//		
		// String[] result = new String[4];
		// String bLon = "§§asdsada§2errtyuuu";
		// int index = bLon.indexOf("§");
		// while (index >= 0) {
		// bLon = bLon.substring(index + 1);
		// int nextIndex = bLon.indexOf("§");
		// boolean hasNext = true;
		// if (nextIndex < 0) {
		// nextIndex = bLon.length() - 1;
		// hasNext = false;
		// }
		// System.out.println(nextIndex);
		// if (bLon.charAt(0) == '§') {// 长简介
		// bLon = bLon.substring(1);
		// nextIndex = bLon.indexOf("§");
		// hasNext = true;
		// if (nextIndex < 0) {
		// nextIndex = bLon.length() - 1;
		// hasNext = false;
		// }
		// result[1] = bLon.substring(0, nextIndex);
		// } else if (bLon.charAt(0) == '1') {
		// result[2] = bLon.substring(1, nextIndex);
		// } else if (bLon.charAt(0) == '2') {
		// result[3] = bLon.substring(1, nextIndex);
		// } else {// 短简介
		// result[0] = bLon.substring(0, nextIndex);
		// }
		// if (hasNext) {
		// index = nextIndex;
		// } else {
		// index = -1;
		// }
		// }
		// for (int i = 0; i < result.length; i++) {
		// System.out.println(result[i]);
		// }
		// String addr =
		// "C:\\Users\\sunquanzhi.ASPIRE\\Desktop\\xyj\\xyj\\stream\\xyj\\directory.txt";
		// UploadServiceImpl impl = new UploadServiceImpl();
		// String content = impl.readDateStr(addr);
		// System.out.println(content);
		// String[] argsArr = content.split("§§");
		// String[] titles = argsArr[1].split("/");
		// for (String s : titles) {
		// System.out.println(s + ":");
		// }
		// String[] strs =
		// parseDescription("C:\\Users\\sunquanzhi.ASPIRE\\Desktop\\fsyy\\fsyy\\stream\\jhy\\description.txt","");
		// for(String str : strs){
		// System.out.println(str);
		// }
	}

	/**
	 * 解析目录结构
	 * 
	 * @param dir
	 * @param filename
	 * @param resourceId
	 * @param type
	 * @throws Exception
	 */
	private int parseDirectory(String dir, String filename, String resourceId,
			Integer type, String resInfo) throws Exception {

		
		String content = readDateStr(dir + File.separator + filename);
		String[] argsArr = content.split("§§");
		Map<String, String> tomeMap = new TreeMap<String, String>();
		if (argsArr.length == 2) {
			tomeMap = parseTome(argsArr[0], resourceId);
		}
		return parseChapter(dir, argsArr[1], resourceId, tomeMap, type, resInfo);

	}

	private String[] trimArray(String[] array) {
		List<String> sList = new ArrayList<String>();
		for (String s : array) {
			if (StringUtils.isNotEmpty(s)) {
				sList.add(s);
			}
		}
		return sList.toArray(new String[sList.size()]);
	}

	private int parseChapter(String dir, String chapterTitle,
			String resourceId, Map<String, String> tomeMap, Integer type,
			String resInfo) throws Exception {
		String[] titles = trimArray(chapterTitle.split("/;/"));

		String imgdir = dir + File.separator + "image";
		Map<Integer, List<String>> imgMap = readImage(imgdir);

		int words = 0;
		for (int i = 0; i < titles.length; i++) {

			Object chapter = getNewChapter(type);
			BeanUtils.forceSetProperty(chapter, "chapterIndex", i + 1);
			BeanUtils.forceSetProperty(chapter, "resourceId", resourceId);

			String title = titles[i];
			int index = title.indexOf(" ");
			if (index > 0) {
				String tomeId = title.substring(0, index);
				if (StringUtils.isNumeric(tomeId)
						&& tomeMap.get(tomeId) != null) {
					title = title.substring(index + 1);
					BeanUtils.forceSetProperty(chapter, "tomeId", tomeMap
							.get(tomeId));
				}
			}
			BeanUtils.forceSetProperty(chapter, "name", title);
			try {
				// 处理章节文字内容
				File txtFile = new File(dir + File.separator + "txt"
						+ File.separator + (i + 1) + ".txt");
				if (txtFile.exists()) {
					String content = FileUtils.readFileToString(txtFile);
					if (ResourceAll.RESOURCE_TYPE_BOOK.equals(type)) {
						BeanUtils
								.forceSetProperty(chapter, "bContent", content);
					} else {
						BeanUtils.forceSetProperty(chapter, "content", content);
					}
					BeanUtils.forceSetProperty(chapter, "chapterSize", content
							.getBytes().length);
					words += content.length();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			// 存放此章节下图片数量
			if (imgMap.containsKey((i + 1))) {// 此章节序号下有图片
				String imgs = ",";
				List<String> imgList = imgMap.get((i + 1));
				Collections.sort(imgList);
				for (String img : imgList) {
					imgs += img + ",";
				}
				BeanUtils.forceSetProperty(chapter, "imageName", imgs
						.substring(1));
			}

			resourceService.addResourceChapter(chapter, type);

			String chapterId = (String) BeanUtils.forceGetProperty(chapter,
					"id");
			String chapterImgs = (String) BeanUtils.forceGetProperty(chapter,
					"imageName");
			if (StringUtils.isNotEmpty(chapterImgs)) {
				String[] imgs = chapterImgs.split(",");
				String imageName = "";
				for (String img : imgs) {

					String targetName = resizeChapterImage(imgdir, img,
							chapterId);
					imageName += targetName + ",";
				}
				BeanUtils.forceSetProperty(chapter, "imageName", imageName);
				resourceService.updateResourceChapter(chapter);
			}
		}
		return words;
	}

	private Map<String, String> parseTome(String tome, String resourceId)
			throws Exception {
		int index = tome.indexOf("§");
		Map<String, String> map = new TreeMap<String, String>();
		if (index < 0) {
			return map;
		}

		tome = tome.substring(index);
		String[] tomes = tome.split("/");

		for (int i = 1; i < tomes.length; i++) {
			String t = tomes[i];
			index = t.indexOf(" ");
			if (index > 0) {
				String id = t.substring(0, index);
				String name = t.substring(index + 1);
				EbookTome etome = new EbookTome();
				etome.setName(name);
				etome.setResourceId(resourceId);
				etome.setTomeIndex(i);
				resourceService.addEbookTome(etome);
				map.put(id, etome.getId());
			}
		}
		return map;
	}

	/**
	 * 裁剪章节图片，目前裁剪176，240两种尺寸，原图为320
	 * 
	 * @param destDir
	 * @param img
	 * 
	 */
	public String resizeChapterImage(String destDir, String img,
			String chapterId) {
		// System.out.println("IMG:"+img);
		String[] target = img.split("_");
		String targetName = "";
		if (target.length == 2) {
			targetName = chapterId + target[1];
		} else {
			targetName = chapterId + "_1" + img.substring(img.lastIndexOf("."));
			;
		}

		String srcFile = destDir + File.separator + img;
		String destFile1 = destDir + File.separator
				+ img.replaceAll("\\.", "_176.");
		String destFile2 = destDir + File.separator
				+ img.replaceAll("\\.", "_240.");
		File file1 = new File(destFile1);
		if (!file1.exists()) {
			try {
				ImageTool.resizeImage(srcFile.replaceAll("\\\\", "/"),
						destFile1.replaceAll("\\\\", "/"), 176);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		File file2 = new File(destFile2);
		if (!file2.exists()) {
			try {
				ImageTool.resizeImage(srcFile.replaceAll("\\\\", "/"),
						destFile2.replaceAll("\\\\", "/"), 240);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String target1 = destDir + File.separator + targetName;
		String target2 = destDir + File.separator
				+ targetName.replaceAll("\\.", "_176.");
		String target3 = destDir + File.separator
				+ targetName.replaceAll("\\.", "_240.");
		try {
			FileUtils.moveFile(new File(srcFile), new File(target1));
			FileUtils.moveFile(new File(destFile1), new File(target2));
			FileUtils.moveFile(new File(destFile2), new File(target3));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return targetName;

	}
	
	public static String getFilePathDir(String filename){
		return filename.substring(0,filename.lastIndexOf(File.separator) );
	}

	public static String getFileExtName(String filename){
		return filename.substring(filename.lastIndexOf(".") + 1);
	}
	
	public String resizeCoverFile(String destDir, String coverImg)
			throws Exception {
		String extName = "jpg";
		extName = coverImg.substring(coverImg.lastIndexOf(".") + 1);
		String srcFile = destDir + File.separator + coverImg;
		String destFile1 = destDir + File.separator + "cover75." + extName;
		String destFile2 = destDir + File.separator + "cover180." + extName;
		String destFile3 = destDir + File.separator + "cover90." + extName;
		String destFile4 = destDir + File.separator + "cover60." + extName;
		String destFile5 = destDir + File.separator + "cover81." + extName;
		String destFile6 = destDir + File.separator + "cover51." + extName;

		String destFile7 = destDir + File.separator + "cover240." + extName;
		String destFile8 = destDir + File.separator + "cover176." + extName;
		String destFile9 = destDir + File.separator + "cover128." + extName;
		// 240 176 128

		try {
			// PicUtil picUtil = new PicUtil();
			ImageTool.resizeImage(srcFile.replaceAll("\\\\", "/"), destFile1
					.replaceAll("\\\\", "/"), 75);
//			ImageTool.resizeImage(srcFile.replaceAll("\\\\", "/"), destFile2
//					.replaceAll("\\\\", "/"), 180);
//			ImageTool.resizeImage(srcFile.replaceAll("\\\\", "/"), destFile3
//					.replaceAll("\\\\", "/"), 90);
//			ImageTool.resizeImage(srcFile.replaceAll("\\\\", "/"), destFile4
//					.replaceAll("\\\\", "/"), 60);
//			ImageTool.resizeImage(srcFile.replaceAll("\\\\", "/"), destFile5
//					.replaceAll("\\\\", "/"), 81);
			ImageTool.resizeImage(srcFile.replaceAll("\\\\", "/"), destFile6
					.replaceAll("\\\\", "/"), 51);

			ImageTool.resizeImage(srcFile.replaceAll("\\\\", "/"), destFile7
					.replaceAll("\\\\", "/"), 240);
			ImageTool.resizeImage(srcFile.replaceAll("\\\\", "/"), destFile8
					.replaceAll("\\\\", "/"), 176);
//			ImageTool.resizeImage(srcFile.replaceAll("\\\\", "/"), destFile9
//					.replaceAll("\\\\", "/"), 120);

			// 清除此目录下的temp文件夹
			File tempD = new File(destDir.toString() + File.separator + "temp");
			if(tempD.exists()){
				UnzipFile.dircleanup(tempD);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return coverImg;
	}

	private String trim(String value, boolean isNeed, String name)
			throws Exception {
		if (StringUtils.isEmpty(value)) {
			if (isNeed) {
				throw new Exception(name + "字段不能为空.");
			}
			return value;
		} else {
			return value.trim();
		}

	}

	private String getAuthorImg(String dir) {
		File dirFile = new File(dir);
		if (dirFile.exists()) {
			for (File file : dirFile.listFiles()) {
				if (file.getName().startsWith("author")) {
					return file.getName();
				}
			}
		}
		return null;
	}

	private boolean isRightFormat(String fileName) {
		String patt = "\\.(jpg|gif|png|bmp)$";
		Pattern p = Pattern.compile(patt);
		Matcher m = p.matcher(fileName);

		if (m.find()) {
			return true;
		} else {
			return false;
		}

	}

	private String getCoverImg(String dir) {
		File dirFile = new File(dir);
		if (dirFile.exists()) {
			for (File file : dirFile.listFiles()) {
				if (file.getName().startsWith("cover")) {
					if (isRightFormat(file.getName())) {
						return file.getName();
					}
				}
			}
		}
		return null;
	}

	private String readDateStr(String filename) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(filename))));
			String str = null;
			StringBuffer sb = new StringBuffer();
			while ((str = br.readLine()) != null) {

				sb.append(str).append("/;/");
			}
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
		return "";
	}

	/**
	 * 读取目录下的图片文件
	 * 
	 * @param dirName
	 * @return
	 */
	protected Map<Integer, List<String>> readImage(String dirName) {
		Map<Integer, List<String>> content = new HashMap<Integer, List<String>>();
		File dir = new File(dirName);
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			String[] strs = files[i].getName().split("_");
			// if(strs.length == 3){
			// continue;
			// }
			if (strs[strs.length - 1].startsWith("176")
					|| strs[strs.length - 1].startsWith("240")) {
				continue;
			}
			if (strs.length == 1) {
				strs = files[i].getName().split("\\.");
			}
			try {
				List<String> list = content.get(Integer.parseInt(strs[0]));
				if (list == null) {
					list = new ArrayList<String>();
				}
				// System.out.println("read:"+strs[0]+":"+files[i].getName());
				list.add(files[i].getName());
				content.put(Integer.parseInt(strs[0]), list);
			} catch (Exception e) {

			}
		}
		return content;
	}

	private ResourceAll getNewResource(Integer resourceType) {
		if (ResourceAll.RESOURCE_TYPE_BOOK.equals(resourceType)) {
			return new Ebook();
		} else if (ResourceAll.RESOURCE_TYPE_COMICS.equals(resourceType)) {
			return new Comics();
		} else if (ResourceAll.RESOURCE_TYPE_MAGAZINE.equals(resourceType)) {
			return new Magazine();
		} else if (ResourceAll.RESOURCE_TYPE_NEWSPAPER.equals(resourceType)) {
			return new NewsPapers();
		}else if(ResourceAll.RESOURCE_TYPE_VIDEO.equals(resourceType)) {
			return new Video();
		}
		return null;
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

}
