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
	 * ���ݲ�ƷID��ȡ��Ʒ����
	 * @param productId
	 * @return
	 */
	public Product getProduct(String productId);
	/**
	 * ����ģ��ID��ȡģ�����
	 * @param templateId
	 * @return
	 */
	public Template getTemplate(int templateId);
	/**
	 * ��ȡĬ��ģ��ID
	 * @param pageType
	 * @param wapType
	 * @return
	 */
	public Integer getDefaultTemplate(Integer pageType,Integer wapType);
	/**
	 * ����ҳ����ID��ȡҳ�������
	 * @param pagegroupId
	 * @return
	 */
	public PageGroup getPageGroup(int pagegroupId);
	/**
	 * ������ĿID��ȡ��Ŀ����
	 * @param columnId
	 * @return
	 */
	public Columns getColumns(int columnId);
	/**
	 * �������۰�ID��ȡ��Ŀ
	 * @param packId
	 * @return
	 */
//	public List<Columns> getColumnsByPackId(int packId);
	/**
	 * ����ҳ����ID�����Ŀ
	 */
	public List<Columns> getColumnsByPageGroupId(Integer pageGroupId,int pageNum,int pageSize);
	/**
	 * ���ݲ�ƷID��ȡ����ҳ���������Ϣ
	 * @param productId
	 * @return
	 */
	public List<PackGroupProvinceRelation> getPackGroupProvinceRelation(String productId); 
	/**
	 * ��ȡ����Ŀ
	 * @param parentId
	 * @return
	 */
	public List<Columns> getColumnChilds(int parentId,int pageNum,int pageSize,int order);
	/**
	 * ��ȡϵͳ������Ϣ
	 * @param name
	 * @return
	 */
	public Variables getVariables(String name);
	/**
	 * ��ȡ�����Ӧҳ�����е���Ŀ
	 * @param pageGroupId
	 * @param resourceTypeId
	 * @return
	 */
	public Columns getColumnsByResourceType(Integer pageGroupId,Integer resourceTypeId);
	
	/**
	 * ��ȡ��Ʒ��Ĭ������
	 * @param productId
	 * @return
	 */
	public String getDefaultChannelId(String productId);
	/**
	 * ��ȡ�û��Զ���ı�ǩ
	 * @param userTagId
	 * @return
	 */
	public UserDefTag getUserDefTagById(int userTagId);
	
	/**
	 * ��ñ�ǩģ��
	 * @param id
	 * @return
	 */
	public TagTemplate getTagTemplate(int id);
	/**add by liuxh 09-12-24*/
	public Material getMaterial(Integer id);
	
	public Map<String,String> getBrandNames();
}
