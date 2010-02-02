/**
 * 
 */
package com.hunthawk.reader.service.bussiness.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.hibernate.NullExpression;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.PackGroupProvinceId;
import com.hunthawk.reader.domain.bussiness.PackGroupProvinceRelation;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.service.bussiness.BussinessService;

/**
 * @author BruceSun
 * 
 */
public class BussinessServiceImpl implements BussinessService {

	private static final Logger logger = LoggerFactory
			.getLogger(BussinessServiceImpl.class);

	private HibernateGenericController controller;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.bussiness.BussinessService#addPackGroupProvinceRelation(com.hunthawk.reader.domain.bussiness.PackGroupProvinceRelation)
	 */
	public void addPackGroupProvinceRelation(PackGroupProvinceRelation rel)
			throws Exception {
		try {
			controller.save(rel);
		} catch (Exception e) {
			logger.error("添加产品和页面组关联出错!", e);
			throw new Exception("不能添加重复的关联!");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.bussiness.BussinessService#deletePackGroupProvinceRelation(com.hunthawk.reader.domain.bussiness.PackGroupProvinceRelation)
	 */
	public void deletePackGroupProvinceRelation(PackGroupProvinceRelation rel)
			throws Exception {
		controller.delete(rel);

	}

	public void deletePackGroupProvinceRelationbyId(PackGroupProvinceId id)
			throws Exception {
		controller.deleteById(PackGroupProvinceRelation.class, id);
	}

	public void deletePackGroupProvinceRelationbyProductId(String productId)
			throws Exception {
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("pid", productId,
				CompareType.Equal);
		expressions.add(ex);
		List<PackGroupProvinceRelation> list = controller.findBy(
				PackGroupProvinceRelation.class, 1, 1000, expressions);
		for (PackGroupProvinceRelation pr : list) {
			controller.delete(pr);
		}
	}

	public List<PackGroupProvinceRelation> getPackGroupProvinceRelationbyProductId(String productId,Collection  expressions){
//		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
//		HibernateExpression ex = new CompareExpression("pid", productId,
//				CompareType.Equal);
//		expressions.add(ex);
		List<PackGroupProvinceRelation> list = controller.findBy(
				PackGroupProvinceRelation.class, 1, 1000, expressions);
		return list;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.bussiness.BussinessService#findPackGroupProvinceRelations(int,
	 *      int, java.lang.String, boolean, java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	public List<PackGroupProvinceRelation> findPackGroupProvinceRelations(
			int pageNo, int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(PackGroupProvinceRelation.class, pageNo,
				pageSize, orderBy, isAsc, expressions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.bussiness.BussinessService#findPageGroups(int,
	 *      int, java.lang.String, boolean, java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	public List<PageGroup> findPageGroups(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		expressions.add(new CompareExpression("deleteStatus", 1,
				CompareType.Equal));
		return controller.findBy(PageGroup.class, pageNo, pageSize, orderBy,
				isAsc, expressions);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.bussiness.BussinessService#getPackGroupProvinceRelationsCount(java.util.Collection)
	 */
	public Long getPackGroupProvinceRelationsCount(
			Collection<HibernateExpression> expressions) {
		return controller.getResultCount(PackGroupProvinceRelation.class,
				expressions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.bussiness.BussinessService#getPageGroupsCount(java.util.Collection)
	 */
	public Long getPageGroupsCount(Collection<HibernateExpression> expressions) {
		return controller.getResultCount(PageGroup.class, expressions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.bussiness.BussinessService#updatePackGroupProvinceRelation(com.hunthawk.reader.domain.bussiness.PackGroupProvinceRelation)
	 */
	public void updatePackGroupProvinceRelation(PackGroupProvinceRelation rel)
			throws Exception {
		controller.update(rel);
	}

	public PageGroup getPageGroup(int pgid) {
		return controller.get(PageGroup.class, pgid);
	}

	public synchronized void addProduct(Product product) throws Exception {
		product.setId(getNewProductId(product.getId()));
		product.setStatus(Constants.PRODUCTSTATUS_CHECK);//默认待上线状态
		controller.save(product);
	}

	public void updateProduct(Product product) throws Exception {
		controller.update(product);
	}

	public List<Product> findProducts(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions) {
		return controller.findBy(Product.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

	@SuppressWarnings("unchecked")
	public Long getProductCount(Collection<HibernateExpression> expressions) {
		return controller.getResultCount(Product.class, expressions);
	}

	public String getNewProductId(String id) throws Exception {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		 HibernateExpression ex = new
		 CompareExpression("id",id+"%",CompareType.Like);
		 hibernateExpressions.add(ex);
		// 进行模糊 查询
		List<Product> products = controller.findBy(Product.class,1, 1, "id",false, hibernateExpressions);
		if (products.size() > 0) {

			Product product = products.get(0);
			int start = Integer.parseInt(product.getId().substring(0, 5));
			// 截取后四位
			int last = Integer.parseInt(product.getId().substring(
					(product.getId().length() - 4)));
			int productId = Integer.parseInt(product.getId());
			return start
					+ StringUtils.leftPad(String.valueOf(last + 1), 4, "0");
		} else {

			return id + "0000";
		}
	}

	public void auditProduct(Product product, Integer status) throws Exception {
		if (product.getStatus().equals(status)) {
			throw new Exception("[" + product.getName() + "]已经是["
					+ Constants.getProductStatusName(status) + "].");
		}
		if (product.getStatus().equals(Constants.PRODUCTSTATUS_OFFLINE)) {
			throw new Exception("[" + product.getName() + "]下线状态无法变更成其他状态");
		}
		product.setStatus(status);
		controller.update(product);
	}

	public void auditPageGroup(PageGroup product, Integer status)
			throws Exception {
		if (product.getPkStatus().equals(status)) {
			throw new Exception("[" + product.getPkName() + "]已经是["
					+ Constants.getProductStatusName(status) + "].");
		}
		if (product.getPkStatus().equals(Constants.PRODUCTSTATUS_OFFLINE)) {
			throw new Exception("[" + product.getPkName() + "]下线状态无法变更成其他状态");
		}
		product.setPkStatus(status);
		controller.update(product);
	}

	public void addPageGroup(PageGroup pagegroup) throws Exception {
		controller.save(pagegroup);
	}

	public void updatePageGroup(PageGroup pagegroup) throws Exception {
		controller.update(pagegroup);
	}

	public void deletePageGroup(PageGroup pagegroup) throws Exception {
		pagegroup.setDeleteStatus(2);
		controller.update(pagegroup);
	}

	public Long getPageGroupCount(Collection<HibernateExpression> expressions) {
		expressions.add(new CompareExpression("deleteStatus", 1,
				CompareType.Equal));
		return controller.getResultCount(PageGroup.class, expressions);
	}

	public void addColumn(Columns column) throws Exception {
		if(column.getOrderType() == null){
			column.setOrderType(0);
		}
		controller.save(column);
	}

	public void updateColumn(Columns column) throws Exception {
		if(column.getOrderType() == null){
			column.setOrderType(0);
		}
		controller.update(column);
	}
	
	public Columns getColumn(int id){
		return controller.get(Columns.class, id);
	}

	public void auditColumn(Columns c, Integer status) throws Exception {
		if(c.getOrderType() == null){
			c.setOrderType(0);
		}
		if (c.getStatus().equals(status)) {
			throw new Exception("[" + c.getName() + "]已经是[" + c.getStatusName()
					+ "].");
		}

		c.setStatus(status);
		controller.update(c);
	}

	public List<Columns> findColumns(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions) {
		expressions
				.add(new CompareExpression("status", 3, CompareType.NotEqual));
		return controller.findBy(Columns.class, pageNo, pageSize, orderBy,
				isAsc, expressions);

	}

	public Long getColumnCount(Collection<HibernateExpression> expressions) {
		expressions
				.add(new CompareExpression("status", 3, CompareType.NotEqual));
		return controller.getResultCount(Columns.class, expressions);
	}

	public Product getProduct(String productId) {
		return controller.get(Product.class, productId);
	}
	/****
	 * 得到最大最小的排序
	 */
	public Integer getMaxMinOrder(Columns parent, PageGroup pagegroup,
			String type) {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if(parent==null){
			NullExpression ex = new NullExpression("parent");
			 hibernateExpressions.add(ex);
		}else{
			 HibernateExpression ex = new
		 		CompareExpression("parent",parent,CompareType.Equal);
		 hibernateExpressions.add(ex);
		}	 
		 HibernateExpression ey = new
	 		CompareExpression("pagegroup",pagegroup,CompareType.Equal);
		 hibernateExpressions.add(ey);
	 
		 List<Columns> columns = controller.findBy(Columns.class,1, 1000, "order",false, hibernateExpressions);
		 Integer returnValue=50;
		 if(columns.size()>0){
			 if("max".equals(type))
				 return columns.get(0).getOrder();
			 else if("min".equals(type))		
				 return columns.get(columns.size()-1).getOrder();
		 }
		 return returnValue;
	}
	
	/**
	 * 取得当前栏目最近的那个对象（上下）
	 */
	
	public Columns getUpDownOrder(Columns parent,PageGroup pagegroup,Columns column,String type){
		Integer order = column.getOrder();
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if(parent==null){
			NullExpression ex = new NullExpression("parent");
			 hibernateExpressions.add(ex);
		}else{
			HibernateExpression ex1 = new
	 		CompareExpression("parent",parent,CompareType.Equal);
			 hibernateExpressions.add(ex1);
		} 
			 HibernateExpression ey1 = new
					CompareExpression("pagegroup",pagegroup,CompareType.Equal);
			 hibernateExpressions.add(ey1);
	 
		if("up".equals(type)){
			 HibernateExpression ex = new
			 		CompareExpression("order",order,CompareType.Lt);//取小于的
			 hibernateExpressions.add(ex);
		}else if("down".equals(type)){
			 HibernateExpression ex = new
		 		CompareExpression("order",order,CompareType.Gt);//取大于的
		 hibernateExpressions.add(ex);
		}
		List<Columns> columns = controller.findBy(Columns.class,1, 1000, "order",false, hibernateExpressions);
		Columns returnValue= new Columns();
		if(columns.size()>0){
			if("up".equals(type))
				returnValue = columns.get(0);//取得所有小的中最大的那个
			if("down".equals(type))
				returnValue = columns.get(columns.size()-1);//取得所有大的中最小的那个
		}
		return returnValue;
	}

	public Object getObjectById(Class clasz,Integer id) {
		return controller.get(clasz, id);
	}
}
