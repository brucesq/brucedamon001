/**
 * 
 */
package com.hunthawk.framework;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;

import com.hunthawk.framework.util.GenericsUtils;

/**
 * @author sunquanzhi
 * 
 */
@SuppressWarnings("unchecked")
public class HibernateEntityController<T> extends HibernateGenericController
		implements EntityController<T> {
	protected Class<T> clasz;// Entity类型.

	/**
	 * <p>
	 * 在构造函数中将泛型T.class赋给clasz.
	 * </p>
	 */
	public HibernateEntityController() {
		clasz = GenericsUtils.getSuperClassGenricType(getClass());
	}

	/**
	 * <p>
	 * 取得clasz.
	 * </p>
	 */
	protected Class<T> getEntityClass() {
		return clasz;
	}

	/**
	 * <p>
	 * 根据ID获取对象.
	 * </p>
	 * 
	 * @see HibernateGenericDao#getId(Class,Object)
	 */
	public T get(Serializable id) {
		return get(getEntityClass(), id);
	}

	/**
	 * <p>
	 * 获取全部对象
	 * </p>
	 * 
	 * @see HibernateGenericDao#getAll(Class)
	 */
	public List<T> getAll() {
		return getAll(getEntityClass());
	}

	/**
	 * <p>
	 * 获取全部对象,带排序参数.
	 * </p>
	 * 
	 * @see HibernateGenericDao#getAll(Class,String,boolean)
	 */
	public List<T> getAll(String orderBy, boolean isAsc) {
		return getAll(getEntityClass(), orderBy, isAsc);
	}

	/**
	 * <p>
	 * 根据ID移除对象.
	 * </p>
	 * 
	 * @see HibernateGenericDao#removeById(Class,Serializable)
	 */
	public void deleteById(Serializable id) {
		deleteById(getEntityClass(), id);
	}

	/**
	 * <p>
	 * 取得Entity的Criteria.
	 * </p>
	 * 
	 * @see HibernateGenericDao#createCriteria(Class,Criterion[])
	 */
	public Criteria createCriteria(Criterion... criterions) {
		return createCriteria(getEntityClass(), criterions);
	}

	/**
	 * <p>
	 * 取得Entity的Criteria,带排序参数.
	 * </p>
	 * 
	 * @see HibernateGenericDao#createCriteria(Class,String,boolean,Criterion[])
	 */
	public Criteria createCriteria(String orderBy, boolean isAsc,
			Criterion... criterions) {
		return createCriteria(getEntityClass(), orderBy, isAsc, criterions);
	}

	/**
	 * <p>
	 * 根据属性名和属性值查询对象.
	 * </p>
	 * 
	 * @return 符合条件的对象列表
	 * @see HibernateGenericDao#findBy(Class,String,Object)
	 */
	public List<T> findBy(String propertyName, Object value) {
		return findBy(getEntityClass(), propertyName, value);
	}

	/**
	 * <p>
	 * 根据属性名和属性值查询对象,带排序参数.
	 * </p>
	 * 
	 * @return 符合条件的对象列表
	 * @see HibernateGenericDao#findBy(Class,String,Object,String,boolean)
	 */
	public List<T> findBy(String propertyName, Object value, String orderBy,
			boolean isAsc) {
		return findBy(getEntityClass(), propertyName, value, orderBy, isAsc);
	}

	/**
	 * <p>
	 * 根据属性名和属性值查询单个对象.
	 * </p>
	 * 
	 * @return 符合条件的唯一对象 or null
	 * @see HibernateGenericDao#findUniqueBy(Class,String,Object)
	 */
	public T findUniqueBy(String propertyName, Object value) {
		return findUniqueBy(getEntityClass(), propertyName, value);
	}

	/**
	 * <p>
	 * 判断对象某些属性的值在数据库中唯一.
	 * </p>
	 * 
	 * @param uniquePropertyNames
	 *            在POJO里不能重复的属性列表,以逗号分割 如"name,loginid,password"
	 * @see HibernateGenericDao#isUnique(Class,Object,String)
	 */
	public boolean isUnique(Object entity, String uniquePropertyNames) {

		return isUnique(getEntityClass(), entity, uniquePropertyNames);
	}

	/**
	 * <p>
	 * 获得记录总数
	 * </p>
	 * 
	 * @param criterions
	 * @return
	 */
	public Long getResultCount(Criterion... criterions) {
		Criteria criteria = createCriteria(getEntityClass(), criterions);
		return getResultCount(criteria);
	}

}
