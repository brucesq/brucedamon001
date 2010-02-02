/**
 * 
 */
package com.hunthawk.reader.service.resource;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.domain.resource.MaterialCatalog;

/**
 * @author BruceSun
 *
 */
public interface MaterialService {

	@Logable(name = "MaterialCatalog", action = "add", property = { "id=ID,name=素材目录名称,type=素材类型" })
	public void addMaterialCatalog(MaterialCatalog catalog)throws Exception;
	
	@Logable(name = "MaterialCatalog", action = "update", property = { "id=ID,name=素材目录名称,type=素材类型" })
	public void updateMaterialCatalog(MaterialCatalog catalog)throws Exception;
	
	@Logable(name = "MaterialCatalog", action = "delete", property = { "id=ID,name=素材目录名称,type=素材类型" })
	public void deleteMaterialCatalog(MaterialCatalog catalog)throws Exception;
	
	public MaterialCatalog getMaterialCatalog(Integer id);
	
	public List<MaterialCatalog> getMaterialCatalogs();
	
	@Logable(name = "Material", action = "add", property = { "id=ID,name=素材名称,type=素材类型,filename=文件名称,size=素材大小,description=描述,extName=扩展名,catalogId=目录ID" })
	public void addMaterial(Material material)throws Exception;
	
	@Logable(name = "Material", action = "update", property = { "id=ID,name=素材名称,type=素材类型,filename=文件名称,size=素材大小,description=描述,extName=扩展名,catalogId=目录ID" })
	public void updateMaterial(Material material)throws Exception;
	
	@Logable(name = "Material", action = "delete", property = { "id=ID,name=素材名称,type=素材类型,filename=文件名称,size=素材大小,description=描述,extName=扩展名,catalogId=目录ID" })
	public void deleteMaterial(Material material)throws Exception;
	
	public Material getMaterial(Integer id);
	
	public List<Material> findMaterials(int pageNo,int pageSize,String orderBy,
			boolean isAsc,Collection<HibernateExpression> expressions);
	
	public Long getMaterialResultCount(Collection<HibernateExpression> expressions);
}
