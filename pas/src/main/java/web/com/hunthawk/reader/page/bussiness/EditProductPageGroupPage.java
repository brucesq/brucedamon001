/**
 * 
 */
package com.hunthawk.reader.page.bussiness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.PackGroupProvinceId;
import com.hunthawk.reader.domain.bussiness.PackGroupProvinceRelation;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.service.bussiness.BussinessService;

/**
 * @author BruceSun
 *
 */
public abstract class EditProductPageGroupPage extends EditPage implements PageBeginRenderListener{
	@InjectObject("spring:bussinessService")
	public abstract BussinessService getBussinessService();

	@SuppressWarnings("unchecked")
	@Override
	public  Class getModelClass()
	{
		return PackGroupProvinceRelation.class;
	}
	
	public abstract void setProductId(String id);
	
	public abstract String getProductId();
	
	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try{
			PackGroupProvinceRelation rel = (PackGroupProvinceRelation)object;
			
			PackGroupProvinceId id = new PackGroupProvinceId();
			
			id.setAid(rel.getAid());
			id.setPid(getProductId());
			
			try{
				getBussinessService().deletePackGroupProvinceRelationbyId(id);
			}catch(Exception e){}
			
			rel.setPgid(pageGroup.getId());
			rel.setPid(getProductId());
			getBussinessService().addPackGroupProvinceRelation(rel);
			
			
		}catch(Exception e)
		{
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}


	public void pageBeginRender(PageEvent event)
	{
		if(getModel() == null)
		{
			PackGroupProvinceRelation rel = new PackGroupProvinceRelation();
			rel.setPid(getProductId());
			setModel(rel);
		}
		
	}
	private PageGroup pageGroup;
	public void setPageGroup(PageGroup pageGroup){
		this.pageGroup = pageGroup;
	}
	public PageGroup getPageGroup(){
		if(getModel() == null ){
			return null;
		}else{
			PackGroupProvinceRelation rel = (PackGroupProvinceRelation)getModel();
			if(rel.getPgid() != null && rel.getPgid() > 0){
				return getBussinessService().getPageGroup(rel.getPgid());
			}
		}
		return null;
	}
	
	public IPropertySelectionModel getAreaCodeList(){
		return new MapPropertySelectModel(Constants.getAreaCode());
	}
	
	public IPropertySelectionModel getPageGroups(){
	
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		String productId = getProductId();
		Product product = getBussinessService().getProduct(productId);
		
		HibernateExpression ex = new CompareExpression("showType",product.getShowType() 
				, CompareType.Equal);
		expressions.add(ex);
		
		HibernateExpression statusE = new CompareExpression("pkStatus", 0,
				CompareType.Equal);
		expressions.add(statusE);
		
		List<PageGroup> pgs = getBussinessService().findPageGroups( 1, Integer.MAX_VALUE, "id", false,expressions);
		ObjectPropertySelectionModel model =  new  ObjectPropertySelectionModel(pgs, PageGroup.class, "getPkName", "getId", false, null);
		return model;
	}

}
