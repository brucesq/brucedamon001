/**
 * 
 */
package com.hunthawk.reader.service.job;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceResType;
import com.hunthawk.reader.domain.resource.Video;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author sunquanzhi
 * 
 */
public class BatchAddReleationJob {

	private HibernateGenericController controller;

	private static final Logger logger = LoggerFactory
			.getLogger(CopyrightExpiredJob.class);

	private SystemService systemService;
	private ResourcePackService resourcePackService;

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void setResourcePackService(ResourcePackService resourcePackService) {
		this.resourcePackService = resourcePackService;
	}

	private String getLastResourceIdByType(int typeId) {
		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		expressions.add(new CompareExpression("resTypeId", typeId,
				CompareType.Equal));
		List<ResourceResType> types = controller.findBy(ResourceResType.class,
				1, 1, "rid", false, expressions);
		if (types.size() > 0) {
			return types.get(0).getRid();
		}
		return "60000000";
	}

	private String getLastPackResourceIdByType(int typeId, int packId) {

		String sql = "select rel from ResourcePackReleation rel , ResourceAll v, ResourceResType rt where rel.pack = ? ";
		ResourcePack pack = new ResourcePack();
		pack.setId(packId);

		sql += " and rt.resTypeId = " + typeId + " and v.id = rt.rid";
		sql += " and rel.resourceId = v.id order by rel.resourceId desc";

		List<ResourcePackReleation> rels = (List<ResourcePackReleation>) controller
				.findBy(sql, 1, 1, pack);
		if (rels.size() > 0) {
			return rels.get(0).getResourceId();
		}
		return "60000000";
	}

	private String getLastResourceId(boolean isNormal) {
		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		if (isNormal) {
			expressions.add(new CompareExpression("name", "%(%",
					CompareType.NotLike));
		}
		expressions
				.add(new CompareExpression("status", 2, CompareType.NotEqual));
		List<Video> videos = controller.findBy(Video.class, 1, 1, "id", false,
				expressions);
		if (videos.size() > 0) {
			return videos.get(0).getId();
		}
		return "60000000";
	}

	private String getLasrReleationId(int packId) {
		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		ResourcePack pack = new ResourcePack();
		pack.setId(packId);
		expressions.add(new CompareExpression("pack", pack, CompareType.Equal));
		expressions.add(new CompareExpression("resourceId", "6%",
				CompareType.Like));
		List<ResourcePackReleation> rels = controller.findBy(
				ResourcePackReleation.class, 1, 1, "resourceId", false,
				expressions);
		if (rels.size() > 0) {
			return rels.get(0).getResourceId();
		}
		return "60000000";
	}

	public void doJob() {
		try {
			// System.out.println("DO:::::::::::");
			Integer packId = Integer.parseInt(systemService.getVariables(
					"all_resource_pack_id").getValue());
			String lastResourceId = getLastResourceId(false);
			String lastRelId = getLasrReleationId(packId);
			System.out.println("DO:::::::::::" + lastResourceId + "REL:"
					+ lastRelId);
			if (lastRelId.compareTo(lastResourceId) < 0) {
				addResourceRel(lastRelId, packId, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		rsyncNormal();
		rsyncResourceType();
	}

	public void rsyncResourceType() {
		try {
			String value = systemService.getVariables(
					"all_resource_pack_id_type").getValue();
			String strs[] = value.split(";");
			for (String str : strs) {
				String[] kv = str.split("=");
				int packId = Integer.parseInt(kv[0]);
				int typeId = Integer.parseInt(kv[1]);
				String lastRid = getLastResourceIdByType(typeId);
				String lastPackId = getLastPackResourceIdByType(typeId, packId);
				if (lastPackId.compareTo(lastRid) < 0) {
					addResourceTypeRel(lastPackId, packId, typeId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 同步资源名称不带(的资源
	 */
	public void rsyncNormal() {
		try {
			// System.out.println("DO:::::::::::");
			Integer packId = Integer.parseInt(systemService.getVariables(
					"all_resource_pack_id_normal").getValue());
			String lastResourceId = getLastResourceId(true);
			String lastRelId = getLasrReleationId(packId);
			System.out.println("DO NORMAL:::::::::::" + lastResourceId + "REL:"
					+ lastRelId);
			if (lastRelId.compareTo(lastResourceId) < 0) {
				addResourceRel(lastRelId, packId, true);
			}
			removeNormal(packId);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void removeNormal(int packId) {
		List param = new ArrayList();
		String sql = "select rel from ResourcePackReleation rel , Video v where rel.pack = ? ";
		ResourcePack pack = new ResourcePack();
		pack.setId(packId);
		param.add(pack);
		sql += " and rel.resourceId = v.id and v.name  like '%(%' ";
		Object[] arr = (Object[]) param.toArray(new Object[param.size()]);
		List<ResourcePackReleation> rels = controller.findBy(sql, arr);
		System.out.println("aaaa:" + rels.size());
		for (ResourcePackReleation rel : rels) {
			controller.delete(rel);
		}
	}

	private void addResourceTypeRel(String lastRelId, int packId, Integer typeId) {
		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		expressions.add(new CompareExpression("resTypeId", typeId,
				CompareType.Equal));
		int page = 1;
		boolean isTrue = true;
		ResourcePack pack = new ResourcePack();
		pack.setId(packId);
		Integer minOrder = resourcePackService.findMinOrderInPack(pack);
		String strOrder = "";
		String firstOrder = "";
		boolean isFirst = false;
		if (minOrder != null && minOrder > 0) {
			strOrder = minOrder + "";
			firstOrder = strOrder.substring(0, 6);
		} else {// 新插入的资源，
			firstOrder = "500000";
			isFirst = true;
		}
		Integer firstNum = 500000;
		int i = 1;
		while (isTrue) {
			List<ResourceResType> videos = controller.findBy(
					ResourceResType.class, page++, 1000, "rid", true,
					expressions);
			if (videos.size() < 1000)
				isTrue = false;

			for (ResourceResType video : videos) {
				// System.out.println("DO:::::::::::"+video.getId());

				ResourcePackReleation rel = new ResourcePackReleation();
				rel.setChoice(0);
				// if (isFirst) {// 首次
				// firstNum = Integer.parseInt(firstOrder) + i;
				// rel.setOrder(firstNum * 1000);
				// } else {
				firstNum = Integer.parseInt(firstOrder) - i;
				rel.setOrder(firstNum * 1000);
				// }
				i++;
				rel.setPack(pack);
				rel.setResourceId(video.getRid());
				rel.setStatus(0);
				if (controller.isUnique(ResourcePackReleation.class, rel,
						"pack,resourceId")) {
					controller.save(rel);
				}
			}
		}
	}

	private void addResourceRel(String lastRelId, int packId, boolean isNormal) {
		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		expressions.add(new CompareExpression("id", lastRelId, CompareType.Gt));
		expressions
				.add(new CompareExpression("status", 2, CompareType.NotEqual));
		if (isNormal) {
			expressions.add(new CompareExpression("name", "%(%",
					CompareType.NotLike));
		}

		int page = 1;
		boolean isTrue = true;
		ResourcePack pack = new ResourcePack();
		pack.setId(packId);
		Integer minOrder = resourcePackService.findMinOrderInPack(pack);
		String strOrder = "";
		String firstOrder = "";
		boolean isFirst = false;
		if (minOrder != null && minOrder > 0) {
			strOrder = minOrder + "";
			firstOrder = strOrder.substring(0, 6);
		} else {// 新插入的资源，
			firstOrder = "500000";
			isFirst = true;
		}
		Integer firstNum = 500000;
		int i = 1;
		while (isTrue) {
			List<Video> videos = controller.findBy(Video.class, page++, 1000,
					"id", true, expressions);
			if (videos.size() < 1000)
				isTrue = false;

			for (Video video : videos) {
				// System.out.println("DO:::::::::::"+video.getId());
				ResourcePackReleation rel = new ResourcePackReleation();
				rel.setChoice(0);
				// if (isFirst) {// 首次
				// firstNum = Integer.parseInt(firstOrder) + i;
				// rel.setOrder(firstNum * 1000);
				// } else {
				firstNum = Integer.parseInt(firstOrder) - i;
				rel.setOrder(firstNum * 1000);
				// }
				i++;
				rel.setPack(pack);
				rel.setResourceId(video.getId());
				rel.setStatus(video.getStatus());
				controller.save(rel);
			}
		}
	}
}
