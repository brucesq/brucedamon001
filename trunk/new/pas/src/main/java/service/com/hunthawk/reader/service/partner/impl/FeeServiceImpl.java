/**
 * 
 */
package com.hunthawk.reader.service.partner.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.framework.util.StringGenerator;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.service.partner.FeeService;

/**
 * @author BruceSun
 * 
 */
public class FeeServiceImpl implements FeeService {

	private HibernateGenericController controller;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.partner.FeeService#addFee(com.hunthawk.reader.domain.partner.Fee)
	 */
	public synchronized void addFee(Fee fee) throws Exception {
		if (fee.getIsout() == 1) {
			if (fee.getTemplateId() == null || fee.getTemplateId() == 0) {
				throw new Exception("计费弹出模板不能为空!");
			}
		}
		String feeId = getNewId(fee);
		fee.setId(feeId);
		fee.setFlag(1);
		fee.setStatus(0);
		fee.setUrl(getFeeUrl());
		fee.setActionUrl("a/" + fee.getUrl());
		controller.save(fee);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.partner.FeeService#deleteFee(com.hunthawk.reader.domain.partner.Fee)
	 */
	public void deleteFee(Fee fee) throws Exception {
		List<ResourcePack> packs = controller.findBy(ResourcePack.class,
				"feeId", fee.getId());

		if (packs.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("计费被批价包引用");
			for (ResourcePack pack : packs) {
				sb.append("[批价包Id=");
				sb.append(pack.getId());
				sb.append(",名称=");
				sb.append(pack.getName());
				sb.append("]");
			}
			throw new Exception(sb.toString());
		}
		List<ResourcePackReleation> rels = controller.findBy(
				ResourcePackReleation.class, "feeId", fee.getId());
		if (rels.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("计费被批价包资源引用");
			for (ResourcePackReleation rel : rels) {
				sb.append("[批价包Id=");
				sb.append(rel.getPack().getId());
				sb.append(",名称=");
				sb.append(rel.getPack().getName());
				sb.append(",资源ID=");
				sb.append(rel.getResourceId());
				sb.append("]");
			}
			throw new Exception(sb.toString());
		}
		fee.setFlag(0);
		controller.update(fee);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.partner.FeeService#findFee(int, int,
	 *      java.lang.String, boolean, java.util.Collection)
	 */
	public List<Fee> findFee(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions) {
		expressions.add(new CompareExpression("flag", 1, CompareType.Equal));
		List<Fee> fees = controller.findBy(Fee.class, pageNo, pageSize, "id",
				true, expressions);

		return fees;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.partner.FeeService#findFeeResultCount(java.util.Collection)
	 */
	public long findFeeResultCount(Collection<HibernateExpression> expressions) {
		expressions.add(new CompareExpression("flag", 1, CompareType.Equal));
		return controller.getResultCount(Fee.class, expressions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.partner.FeeService#getFee(java.lang.String)
	 */
	public Fee getFee(String id) {

		return controller.get(Fee.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.partner.FeeService#updateFee(com.hunthawk.reader.domain.partner.Fee)
	 */
	public void updateFee(Fee fee) throws Exception {
		if (fee.getIsout() == 1) {
			if (fee.getTemplateId() == null || fee.getTemplateId() == 0) {
				throw new Exception("计费弹出模板不能为空!");
			}
		}
		/*
		 * 2009-10-30
		 * 更改计费价格时，同步更新下引用它的批价包中的计费价格
		 * by yuzs
		 */
		
		List<ResourcePack> list = controller.findBy(ResourcePack.class, "feeId", fee.getId());
		if(list!=null && list.size()>0){
			for(ResourcePack rp:list){
				rp.setCode(fee.getCode());
				controller.update(rp);
			}
		}
		
		/*
		 * 2009-10-30修改结束
		 */
		controller.update(fee);

	}

	private String getFeeUrl() throws Exception {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		List<Fee> fees = controller.findBy(Fee.class, 1, 1, "url", false,
				hibernateExpressions);
		if (fees.size() > 0) {
			Fee lastFee = fees.get(0);
			return StringGenerator.generatorNext(lastFee.getUrl());
		} else {
			return "aaaa";
		}
	}

	private String getNewId(Fee fee) throws Exception {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("type", fee.getType(),
				CompareType.Equal);
		hibernateExpressions.add(ex);
		ex = new CompareExpression("provider", fee.getProvider(),
				CompareType.Equal);
		hibernateExpressions.add(ex);
		List<Fee> fees = controller.findBy(Fee.class, 1, 1, "id", false,
				hibernateExpressions);
		String feeId = fee.getType() + fee.getProvider().getProviderId();
		if (fees.size() > 0) {
			Fee lastFee = fees.get(0);
			int lastFeeId = Integer.parseInt(StringUtils.right(lastFee.getId(),
					3));
			if (lastFeeId < 999) {
				return feeId
						+ StringUtils.leftPad(String.valueOf(lastFeeId + 1), 3,
								"0");
			} else {
				throw new Exception("已经超过该类型计费的最大值,请联系系统管理员!");
			}

		} else {
			return feeId + "001";
		}
	}

	public void auditFee(Fee fee, int status) throws Exception {
		if (fee.getStatus() == status) {
			throw new Exception("[" + fee.getId() + "]该计费状态不需要变更");
		}
		if (status == 1) {
			if (ParameterCheck.isNullOrEmpty(fee.getServiceId())
					|| ParameterCheck.isNullOrEmpty(fee.getProductId())) {
				throw new Exception("[" + fee.getId()
						+ "]需要填写完整的serviceid和productid信息");
			}
		}

		fee.setStatus(status);
		controller.update(fee);
	}

}
