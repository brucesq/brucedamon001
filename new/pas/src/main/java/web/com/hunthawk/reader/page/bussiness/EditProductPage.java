package com.hunthawk.reader.page.bussiness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.PackGroupProvinceRelation;
import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.domain.partner.Channel;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.bussiness.BussinessService;
import com.hunthawk.reader.service.partner.PartnerService;

@Restrict(roles = { "productchange" }, mode = Restrict.Mode.ROLE)
public abstract class EditProductPage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:bussinessService")
	public abstract BussinessService getBussinessService();

	// @SuppressWarnings("unchecked")
	// @InjectObject("spring:packGroupProvinceRelationService")
	// public abstract PackGroupProvinceRelationService
	// getPackGroupProvinceRelationService();
	//	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#getModelClass()
	 */
	@Override
	public Class getModelClass() {

		return Product.class;
	}

	@Override
	protected boolean persist(Object object) {

		try {
			Product product = (Product) object;
			product.setModifier(getUser().getId());
			product.setModifyTime(new Date());
			if (isModelNew()) {
				if(StringUtils.isEmpty(getMap())){
					throw new Exception("您必须配置关联页面组.");
				}
				product.setId(product.getShowType()
						+ product.getChannel().getId());
				product.setCreateTime(new Date());
				product.setCreator(getUser().getId());
				product.setStatus(Constants.PRODUCTSTATUS_CHECK);
				getBussinessService().addProduct(product);
				String pid = product.getId();
				String map = getMap();
				String arrs[] = map.split(",");
				for (int i = 0; i < arrs.length; i++) {
					String s[] = arrs[i].split("=");
					String areaid = s[0];
					int pgid = Integer.parseInt(s[1]);
					PackGroupProvinceRelation pgpr = new PackGroupProvinceRelation();
					pgpr.setAid(areaid);
					pgpr.setPgid(pgid);
					pgpr.setPid(pid);

					getBussinessService().addPackGroupProvinceRelation(pgpr);
				}
			} else {
				getBussinessService()
						.deletePackGroupProvinceRelationbyProductId(
								product.getId());
				String map = getMap();
				// 重新添加引用关系
				String arrs[] = map.split(",");
				for (int i = 0; i < arrs.length; i++) {
					String aid = arrs[i].split("=")[0];
					String pgid = arrs[i].split("=")[1];
					PackGroupProvinceRelation pgpr = new PackGroupProvinceRelation();
					pgpr.setPid(product.getId());
					pgpr.setAid(aid);
					pgpr.setPgid(Integer.parseInt(pgid));
					getBussinessService().addPackGroupProvinceRelation(pgpr);
				}
				getBussinessService().updateProduct(product);
			}
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();

	public IPropertySelectionModel getChannelList() {
		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				getPartnerService().getAllFirstLevelChannel(), Channel.class,
				"getIntro", "getId", false, "");
		return parentProper;
	}

	public IPropertySelectionModel getCreditList() {
		return new MapPropertySelectModel(Product.getYesNo());
	}

	public IPropertySelectionModel getProductStatusList() {
		return new MapPropertySelectModel(Constants.getProductStatus());
	}

	public IPropertySelectionModel getBussinessTypeList() {
		return new MapPropertySelectModel(Constants.getBussinessTypes());
	}

	public void pageBeginRender(PageEvent arg0) {

		if (getModel() == null) {
			setModel(new Product());
		} else if(!isModelNew()) {
			Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
			HibernateExpression ex = new CompareExpression("pid",
					((Product) getModel()).getId(), CompareType.Equal);
			hibernateExpressions.add(ex);
			List<PackGroupProvinceRelation> rels = getBussinessService()
					.findPackGroupProvinceRelations(1, Integer.MAX_VALUE,
							"aid", true, hibernateExpressions);
			String map = "";
			for (PackGroupProvinceRelation rel : rels) {
				map += "," + rel.getAid() + "=" + rel.getPgid();
			}
			if (map.length() > 0) {
				map = map.substring(1);
			}
			setMap(map);
		}
	}

	public abstract void setMap(String map);

	public abstract String getMap();

	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();

	public String getPackGroupSetURL() {
		IEngineService service = getExternalService();

		Object[] params = new Object[] { "map", "001=1050", 1 };
		String packGroupSetURL = PageHelper.getExternalFunction(service,
				"bussiness/PackGroupProvinceRelationPage", params);
		return packGroupSetURL;
	}

	
}
