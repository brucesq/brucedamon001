/**
 * 
 */
package com.hunthawk.reader.pps.service;

import java.util.List;
import java.util.Map;

import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.PackGroupProvinceRelation;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.bussiness.Template;
import com.hunthawk.reader.domain.bussiness.UserDefTag;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.domain.system.Variables;

/**
 * @author BruceSun
 *
 */
public interface BussinessService {

	/**
	 * 根据产品ID获取产品对象
	 * @param productId
	 * @return
	 */
	public Product getProduct(String productId);
	/**
	 * 根据模板ID获取模板对象
	 * @param templateId
	 * @return
	 */
	public Template getTemplate(int templateId);
	/**
	 * 获取默认模板ID
	 * @param pageType
	 * @param wapType
	 * @return
	 */
	public Integer getDefaultTemplate(Integer pageType,Integer wapType);
	/**
	 * 根据页面组ID获取页面组对象
	 * @param pagegroupId
	 * @return
	 */
	public PageGroup getPageGroup(int pagegroupId);
	/**
	 * 根据栏目ID获取栏目对象
	 * @param columnId
	 * @return
	 */
	public Columns getColumns(int columnId);
	/**
	 * 根据批价包ID获取栏目
	 * @param packId
	 * @return
	 */
//	public List<Columns> getColumnsByPackId(int packId);
	/**
	 * 根据页面组ID获得栏目
	 */
	public List<Columns> getColumnsByPageGroupId(Integer pageGroupId,int pageNum,int pageSize);
	/**
	 * 根据产品ID获取关联页面组关联信息
	 * @param productId
	 * @return
	 */
	public List<PackGroupProvinceRelation> getPackGroupProvinceRelation(String productId); 
	/**
	 * 获取子栏目
	 * @param parentId
	 * @return
	 */
	public List<Columns> getColumnChilds(int parentId,int pageNum,int pageSize,int order);
	/**
	 * 获取系统变量信息
	 * @param name
	 * @return
	 */
	public Variables getVariables(String name);
	/**
	 * 获取分类对应页面组中的栏目
	 * @param pageGroupId
	 * @param resourceTypeId
	 * @return
	 */
	public Columns getColumnsByResourceType(Integer pageGroupId,Integer resourceTypeId);
	
	/**
	 * 获取产品的默认渠道
	 * @param productId
	 * @return
	 */
	public String getDefaultChannelId(String productId);
	/**
	 * 获取用户自定义的标签
	 * @param userTagId
	 * @return
	 */
	public UserDefTag getUserDefTagById(int userTagId);
	
	/**
	 * 获得标签模板
	 * @param id
	 * @return
	 */
	public TagTemplate getTagTemplate(int id);
	/**add by liuxh 09-12-24*/
	public Material getMaterial(Integer id);
	
	public Map<String,String> getBrandNames();
}
