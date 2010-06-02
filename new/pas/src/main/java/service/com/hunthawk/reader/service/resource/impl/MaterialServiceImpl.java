/**
 * 
 */
package com.hunthawk.reader.service.resource.impl;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.domain.resource.MaterialCatalog;
import com.hunthawk.reader.service.resource.MaterialService;

/**
 * @author BruceSun
 * 
 */
@SuppressWarnings("unchecked")
public class MaterialServiceImpl implements MaterialService {

	private HibernateGenericController controller;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void addMaterial(Material material) throws Exception {
		controller.save(material);
	}

	public void addMaterialCatalog(MaterialCatalog catalog) throws Exception {
		if (controller.isUnique(MaterialCatalog.class, catalog, "name")) {
			controller.save(catalog);
		} else {
			throw new Exception("素材目录名称已经存在!");
		}
	}

	public void deleteMaterial(Material material) throws Exception {
		controller.delete(material);
	}

	public void deleteMaterialCatalog(MaterialCatalog catalog) throws Exception {
		List<Material> materials = controller.findBy(Material.class,
				"catalogId", catalog.getId());
		if (materials.size() > 0) {
			throw new Exception("该目录下存在素材!");
		}
		controller.delete(catalog);
	}

	public List<Material> findMaterials(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {

		return controller.findBy(Material.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

	public MaterialCatalog getMaterialCatalog(Integer id) {
		return controller.get(MaterialCatalog.class, id);
	}

	public List<MaterialCatalog> getMaterialCatalogs() {
		return controller.getAll(MaterialCatalog.class);
	}

	public Long getMaterialResultCount(
			Collection<HibernateExpression> expressions) {
		return controller.getResultCount(Material.class, expressions);
	}

	public void updateMaterial(Material material) throws Exception {
		controller.update(material);

	}
	public Material getMaterial(Integer id){
		return controller.get(Material.class, id);
	}
	public void updateMaterialCatalog(MaterialCatalog catalog) throws Exception {
		if (controller.isUnique(MaterialCatalog.class, catalog, "name")) {
			controller.save(catalog);
		} else {
			throw new Exception("素材目录名称已经存在!");
		}
	}

}
