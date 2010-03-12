package com.hunthawk.reader.page.bussiness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.tapestry.form.ExStringPropertySelectionModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.PackGroupProvinceRelation;
import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.domain.partner.Channel;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.page.resource.EditResourceTypePage;
import com.hunthawk.reader.page.resource.ShowEpackReleationPage;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.bussiness.BussinessService;
import com.hunthawk.reader.service.partner.FeeService;
import com.hunthawk.reader.service.partner.PartnerService;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.resource.ResourceService;

public abstract class CopyProductPage extends SearchPage{//EditPage implements PageBeginRenderListener{
	
	@InjectObject("spring:bussinessService")
	public abstract BussinessService getBussinessService();
	/**
	 * 产品的set集合
	 */
	
	public abstract Set getProducts();
	public abstract void setProducts(Set productSet);
	
	public abstract String getName();
	public abstract void setName(String name);
	
//	@SuppressWarnings("unchecked")
//	@Override
//	public  Class getModelClass(){
//		return Product.class;
//	}
	
	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedProducts();

	public abstract void setSelectedProducts(Set set);

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		Product pro = (Product) getCurrentProduct();
		Set selectedPros = getSelectedProducts();
		// 选择了用户
		if (bSelected) {
			selectedPros.add(pro);
		} else {
			selectedPros.remove(pro);
		}
		// persist value
		setSelectedProducts(selectedPros);

	}

	public boolean getCheckboxSelected() {
		return getSelectedProducts().contains(getCurrentProduct());
	}
	
	public abstract Object getCurrentProduct();
	
	protected  void delete(Object object){
		
	}

	@InjectPage("bussiness/ShowProductPage")
	public abstract ShowProductPage getShowProductPage();
	public IPage saveAndReturn() {
		try{
			if(getSelectedProducts().size() == 0){
				setSelectedProducts(new HashSet());
				return this;
			}
			List<Product> pros =new ArrayList(getSelectedProducts());
			//System.out.println(pros.size());
			for(Iterator it=pros.iterator();it.hasNext();){
				Product pro=(Product)it.next();
				String oldProId=pro.getId();
				if(pro.getChannel()==null){
					getDelegate().setFormComponent(null);
					getDelegate().record("产品复制失败:您还没有选择归属渠道", null);
					setSelectedProducts(new HashSet());
					return this;
				}
				//System.out.println("渠道名称--->"+pro.getChannel().getChName());
				pro.setId(pro.getShowType()
						+ pro.getChannel().getId());
				pro.setCreator(getUser().getId());
				Date date=new Date();
				pro.setCreateTime(date);
				pro.setModifyTime(date);
				pro.setModifier(getUser().getId());
				//复制产品
				getBussinessService().addProduct(pro);
				//复制引用关系
				Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
				HibernateExpression ex = new CompareExpression("pid", oldProId,
						CompareType.Equal);
				expressions.add(ex);
				List<PackGroupProvinceRelation> list=getBussinessService().getPackGroupProvinceRelationbyProductId(oldProId,expressions);
				//System.out.println("新产品ID--->"+pro.getId());
				for(Iterator l=list.iterator();l.hasNext();){
					PackGroupProvinceRelation ppr=(PackGroupProvinceRelation)l.next();
					String areaid=ppr.getAid();
					int pgid = ppr.getPgid();
					PackGroupProvinceRelation pgpr = new PackGroupProvinceRelation();
					pgpr.setAid(areaid);
					pgpr.setPgid(pgid);
					pgpr.setPid(pro.getId());
					getBussinessService().addPackGroupProvinceRelation(pgpr);//循环添加关系
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage()+",产品复制失败", null);
			setSelectedProducts(new HashSet());
			return this;
		}
		ShowProductPage page=getShowProductPage();
		return page;
	}

	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();
	
	// 渠道公司
	public abstract Channel getChannel();

	public abstract void setChannel(Channel channel);

	/**
	 * 获得渠道列表
	 * 
	 * @return
	 */
	public IPropertySelectionModel getChannelList() {
		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				getPartnerService().getAllFirstLevelChannel(), Channel.class,
				"getIntro", "getId", true, "");
		return parentProper;
	}
	
	
		public   IBasicTableModel getTableModel()
		{
			return new IBasicTableModel()
			{
				public int getRowCount()
				{
					//return getProducts().size();
					return getNewProductList().size();
				}
				public Iterator getCurrentPageRows(int nFirst, int nPageSize,
						ITableColumn objSortColumn, boolean bSortOrder)
				{
					//生成新的product列表
					//return  getProducts().iterator();
					return getNewProductList().iterator();
				}
			};
		}

	public List<Product> getNewProductList(){
		
		List<Product> pros=null;
		if(getProducts()!=null){
			pros=new ArrayList(getProducts());
		}else{
			pros=new ArrayList();
		}
		List<Product> newPros=new ArrayList();
		//查询产品列表 得到最大ID
		for(Iterator it=pros.iterator();it.hasNext();){
			Product old=(Product)it.next();
			Product newPro=new Product();
			newPro.setId(old.getId());
			//newPro.setChannel(old.getChannel());
			newPro.setCreator(getUser().getId());
			newPro.setName("复件   "+old.getName());
			newPro.setIsadapter(old.getIsadapter());
			newPro.setCreateTime(new Date());
			newPro.setCredit(old.getCredit());
			newPro.setShowType(old.getShowType());
			newPro.setStatus(Constants.PRODUCTSTATUS_CHECK);
			newPros.add(newPro);
		}
		return newPros;
	}
	
}
