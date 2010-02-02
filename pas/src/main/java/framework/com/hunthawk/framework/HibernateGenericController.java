/**
 * 
 */
package com.hunthawk.framework;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.hql.ast.QueryTranslatorImpl;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.framework.util.Page;

/**
 * <p>
 * 工具类
 * </p>
 * 
 * @author sunquanzhi
 * 
 */
@SuppressWarnings("unchecked")
public class HibernateGenericController extends HibernateDaoSupport {

	private boolean cacheQueries = false;

	public void setCacheQueries(boolean cacheQueries) {
		this.cacheQueries = cacheQueries;
	}

	public boolean isCacheQueries() {
		return this.cacheQueries;
	}

	protected Criteria createCriteria(Class clazz) {
		Criteria criteria = getSession().createCriteria(clazz);
		if (isCacheQueries()) {
			criteria.setCacheable(true);
		}
		return criteria;
	}

	protected Query createQuery(String hql) throws HibernateException {
		Query query = getSession().createQuery(hql);
		if (isCacheQueries()) {
			query.setCacheable(true);
		}
		return query;
	}

	/**
	 * <p>
	 * 根据ID获取对象. 实际调用Hibernate的session.load()方法返回实体或其proxy对象. 如果对象不存在，抛出异常.
	 * </p>
	 * 
	 * @param <T>
	 * @param clasz
	 * @param id
	 * @return
	 */
	public <T> T load(Class<T> clasz, Serializable id)
			throws HibernateException {
		return (T) getHibernateTemplate().load(clasz, id);
	}

	public <T> T load(Class<T> clasz, Serializable id, LockMode lockMode)
			throws HibernateException {
		return (T) getHibernateTemplate().load(clasz, id, lockMode);
	}

	/**
	 * <p>
	 * 根据ID获取对象. 实际调用Hibernate的session.get()方法返回实体或其proxy对象. 如果对象不存在，返回null.
	 * </p>
	 * 
	 * @param <T>
	 * @param clasz
	 * @param id
	 * @return
	 */
	public <T> T get(Class<T> clasz, Serializable id) {
		return (T) getHibernateTemplate().get(clasz, id);
	}

	public Object get(String entityName, Serializable id) {
		return getHibernateTemplate().get(entityName, id);
	}

	public String getEntityName(Object obj) {
		return getSessionFactory().getClassMetadata(obj.getClass())
				.getEntityName();
		// return getSession().getEntityName(obj);
	}

	public <T> T get(Class<T> clasz, Serializable id, LockMode lockMode) {
		return (T) getHibernateTemplate().get(clasz, id, lockMode);
	}

	/**
	 * <p>
	 * 获取全部对象.
	 * </p>
	 * 
	 * @param <T>
	 * @param clasz
	 * @return
	 */
	public <T> List<T> getAll(Class<T> clasz) {
		return getHibernateTemplate().loadAll(clasz);
	}

	/**
	 * <p>
	 * 获取全部对象,带排序字段与升降序参数.
	 * </p>
	 * 
	 * @param <T>
	 * @param clasz
	 * @param orderBy
	 * @param isAsc
	 * @return
	 */
	public <T> List<T> getAll(Class<T> clasz, String orderBy, boolean isAsc) {
		Assert.hasText(orderBy);
		if (isAsc)
			return getHibernateTemplate().findByCriteria(
					DetachedCriteria.forClass(clasz).addOrder(
							Order.asc(orderBy)));
		else
			return getHibernateTemplate().findByCriteria(
					DetachedCriteria.forClass(clasz).addOrder(
							Order.desc(orderBy)));
	}

	/**
	 * <p>
	 * 删除对象
	 * </p>
	 * 
	 * @param entity
	 */
	public void delete(Object entity) {
		getHibernateTemplate().delete(entity);
	}

	/**
	 * <p>
	 * merge对象
	 * </p>
	 * 
	 * @param <T>
	 * @param entity
	 * @return
	 */
	public <T> T merge(T entity) {
		return (T) getHibernateTemplate().merge(entity);
	}

	/**
	 * <p>
	 * 更新对象
	 * </p>
	 * 
	 * @param entity
	 */
	public void update(Object entity) {
		getHibernateTemplate().update(entity);
	}

	/**
	 * <p>
	 * 保存对象
	 * </p>
	 * 
	 * @param entity
	 */
	public void persist(Object entity) {
		getHibernateTemplate().persist(entity);
	}

	/**
	 * <p>
	 * 保存对象
	 * </p>
	 * 
	 * @param entity
	 */
	public void save(Object entity) {
		getHibernateTemplate().save(entity);
	}

	/**
	 * <p>
	 * 根据ID删除对象.
	 * </p>
	 */
	public <T> void deleteById(Class<T> clasz, Serializable id) {
		delete(get(clasz, id));
	}

	protected void flush() {
		getHibernateTemplate().flush();
	}

	protected void clear() {
		getHibernateTemplate().clear();
	}

	protected void refresh(Object entity, LockMode lockMode) {
		getHibernateTemplate().refresh(entity, lockMode);
	}

	protected void refresh(Object entity) {
		getHibernateTemplate().refresh(entity);
	}

	/**
	 * <p>
	 * 创建Query对象.
	 * 对于需要first,max,fetchsize,cache,cacheRegion等诸多设置的函数,可以在返回Query后自行设置.
	 * 留意可以连续设置,如下：
	 * </p>
	 * 
	 * <pre>
	 * dao.getQuery(hql).setMaxResult(100).setCacheable(true).list();
	 * </pre>
	 * 
	 * 调用方式如下：
	 * 
	 * <pre>
	 *        dao.createQuery(hql)
	 *        dao.createQuery(hql,arg0);
	 *        dao.createQuery(hql,arg0,arg1);
	 *        dao.createQuery(hql,new Object[arg0,arg1,arg2])
	 * </pre>
	 * 
	 * @param hql
	 * @param values
	 *            可变参数.
	 */
	protected Query createQuery(String hql, Object... values) {
		Assert.hasText(hql);
		Query query = createQuery(hql);
		for (int i = 0; i < values.length; i++) {
			query.setParameter(i, values[i]);
		}
		return query;
	}

	/**
	 * 创建Query对象
	 * 
	 * 
	 * @param hql
	 * @param params
	 * @return
	 * 
	 * @author penglei
	 */
	protected Query createQuery(String hql, Map<String, Object> params) {
		Assert.hasText(hql);
		Query query = createQuery(hql);
		for (String key : params.keySet()) {
			Object param = params.get(key);
			if (param instanceof Object[]) {
				Object[] obj = (Object[]) param;
				query.setParameterList(key, obj);

			} else if (param instanceof Collection) {
				Collection collection = (Collection) param;
				query.setParameterList(key, collection);
			} else {
				query.setParameter(key, param);
			}
		}
		return query;
	}

	/**
	 * <p>
	 * 创建Criteria对象.
	 * </p>
	 * 
	 * @param clasz
	 * @param criterions
	 *            可变的Restrictions条件列表,见{@link #createQuery(String,Object...)}
	 */
	protected <T> Criteria createCriteria(Class<T> clasz,
			Criterion... criterions) {
		Criteria criteria = createCriteria(clasz);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		return criteria;
	}

	/**
	 * <p>
	 * 创建Criteria对象，带排序字段与升降序字段.
	 * </p>
	 * 
	 * @param clasz
	 * @see #createCriteria(Class,Criterion[])
	 */
	protected <T> Criteria createCriteria(Class<T> clasz, String orderBy,
			boolean isAsc, Criterion... criterions) {
		Assert.hasText(orderBy);

		Criteria criteria = createCriteria(clasz, criterions);

		if (isAsc)
			criteria.addOrder(Order.asc(orderBy));
		else
			criteria.addOrder(Order.desc(orderBy));

		return criteria;
	}

	protected <T> Criteria createCriteria(Class<T> clasz,
			Collection<HibernateExpression> expressions) {
		List<Criterion> criterions = new ArrayList<Criterion>();
		for (HibernateExpression expression : expressions) {
			Criterion criterion = expression.createCriteria();
			if (criterion != null) {
				criterions.add(criterion);
			}
		}

		return createCriteria(clasz, criterions.toArray(new Criterion[0]));
	}

	/**
	 * <p>
	 * 创建Criteria对象,带有HibernateExpression
	 * </p>
	 * 
	 * @param <T>
	 * @param clasz
	 * @param orderBy
	 * @param isAsc
	 * @param expressions
	 * @return
	 */
	protected <T> Criteria createCriteria(Class<T> clasz, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions) {
		List<Criterion> criterions = new ArrayList<Criterion>();
		for (HibernateExpression expression : expressions) {
			Criterion criterion = expression.createCriteria();
			if (criterion != null) {
				criterions.add(criterion);
			}
		}

		return createCriteria(clasz, orderBy, isAsc, criterions
				.toArray(new Criterion[0]));
	}

	/**
	 * <p>
	 * 根据hql查询,直接使用HibernateTemplate的find函数.
	 * </p>
	 * 
	 * @param hql
	 * @param values
	 *            可变参数,见{@link #createQuery(String,Object...)}
	 */
	public List findBy(String hql, Object... values) {
		Assert.hasText(hql);
		return getHibernateTemplate().find(hql, values);
	}

	/**
	 * <p>
	 * 根据属性名和属性值查询对象.
	 * </p>
	 * 
	 * @param <T>
	 * @param clasz
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public <T> List<T> findBy(Class<T> clasz, String propertyName, Object value) {
		Assert.hasText(propertyName);
		return createCriteria(clasz, Restrictions.eq(propertyName, value))
				.list();
	}

	/**
	 * <p>
	 * 根据属性名和属性值查询对象,带排序参数.
	 * </p>
	 * 
	 * @param <T>
	 * @param clasz
	 * @param propertyName
	 * @param value
	 * @param orderBy
	 * @param isAsc
	 * @return
	 */
	public <T> List<T> findBy(Class<T> clasz, String propertyName,
			Object value, String orderBy, boolean isAsc) {
		Assert.hasText(propertyName);
		Assert.hasText(orderBy);
		return createCriteria(clasz, orderBy, isAsc,
				Restrictions.eq(propertyName, value)).list();
	}

	/**
	 * <p>
	 * 根据属性名和属性值查询唯一对象.
	 * </p>
	 * 
	 * @param <T>
	 * @param clasz
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public <T> T findUniqueBy(Class<T> clasz, String propertyName, Object value) {
		Assert.hasText(propertyName);
		return (T) createCriteria(clasz, Restrictions.eq(propertyName, value))
				.uniqueResult();
	}

	/**
	 * <p>
	 * 获得记录总数
	 * </p>
	 * 
	 * @param hql
	 * @param values
	 * @return
	 */
	public Long getResultCount(String hql, Object... values) {
		Assert.hasText(hql);
		String countQueryString = " select count (*) "
				+ removeSelect(removeOrders(hql));
		List countlist = getHibernateTemplate().find(countQueryString, values);
		return toLong(countlist.get(0));
	}

	/**
	 * <p>
	 * 执行数据库语句
	 * </p>
	 * 
	 * @param hql
	 * @param objects
	 * @return
	 */
	public Integer executeUpdate(final String hql, final Object... objects) {
		return (Integer) getHibernateTemplate().executeWithNativeSession(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createQuery(hql);
						if (objects != null) {
							for (int i = 0; i < objects.length; i++) {
								query.setParameter(i, objects[i]);
							}
						}
						return query.executeUpdate();
					}
				});
	}

	/**
	 * <p>
	 * 获得记录总数
	 * </p>
	 * 
	 * @param criteria
	 * @return
	 */
	public Long getResultCount(Criteria criteria) {
		Assert.notNull(criteria);
		CriteriaImpl impl = (CriteriaImpl) criteria;
		Long totalCount = 0L;
		try {
			// 先把Projection和OrderBy条件取出来,清空两者来执行Count操作
			Projection projection = impl.getProjection();
			List<CriteriaImpl.OrderEntry> orderEntries;
			try {
				orderEntries = (List) BeanUtils.forceGetProperty(impl,
						"orderEntries");
				BeanUtils.forceSetProperty(impl, "orderEntries",
						new ArrayList());
			} catch (Exception e) {
				throw new InternalError(
						" Runtime Exception impossibility throw ");
			}

			// 执行查询
			totalCount = toLong(criteria.setProjection(Projections.rowCount())
					.uniqueResult());

			// 将之前的Projection和OrderBy条件重新设回去
			criteria.setProjection(projection);
			if (projection == null) {
				criteria
						.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
			}

			try {
				BeanUtils.forceSetProperty(impl, "orderEntries", orderEntries);
			} catch (Exception e) {
				throw new InternalError(
						" Runtime Exception impossibility throw ");
			}
		} finally {
			SessionFactoryUtils.closeSession((Session) impl.getSession());
		}
		return totalCount;
	}

	/**
	 * <p>
	 * 获得记录总数
	 * </p>
	 * 
	 * @param clasz
	 * @param criterions
	 * @return
	 */
	public Long getResultCount(Class clasz, Criterion... criterions) {
		Criteria criteria = createCriteria(clasz, criterions);
		return getResultCount(criteria);
	}

	/**
	 * <p>
	 * 获得记录总数
	 * </p>
	 * 
	 * @param clasz
	 * @param expressions
	 * @return
	 */
	public Long getResultCount(Class clasz,
			Collection<HibernateExpression> expressions) {
		Criteria criteria = createCriteria(clasz);
		for (HibernateExpression expression : expressions) {
			Criterion criterion = expression.createCriteria();
			if (criterion != null) {
				criteria.add(criterion);
			}

		}
		return getResultCount(criteria);
	}

	/**
	 * <p>
	 * 分页查询
	 * </p>
	 * 
	 * @param hql
	 * @param pageNo
	 * @param pageSize
	 * @param values
	 * @return
	 */
	public List findBy(String hql, int pageNo, int pageSize, Object... values) {
		Assert.hasText(hql);
		Assert.isTrue(pageNo >= 1, "pageNo should start from 1");
		int startIndex = getStartOfPage(pageNo, pageSize);
		Query query = createQuery(hql, values);
		return query.setFirstResult(startIndex).setMaxResults(pageSize).list();

	}

	/***************************************************************************
	 * <p>
	 * 分页查询
	 * </p>
	 * 
	 * @param hql
	 * @param pageNo
	 * @param pageSize
	 * @param params
	 * @return
	 * 
	 * @author penglei
	 */
	public List findBy(String hql, int pageNo, int pageSize,
			Map<String, Object> params) {
		Assert.hasText(hql);
		Assert.isTrue(pageNo >= 1, "pageNo should start from 1");
		int startIndex = getStartOfPage(pageNo, pageSize);
		Query query = createQuery(hql, params);
		return query.setFirstResult(startIndex).setMaxResults(pageSize).list();

	}

	/**
	 * 
	 * @param hql
	 * @param pageNo
	 * @param pageSize
	 * @param values
	 * @return
	 */
	public List findByArray(String hql, int pageNo, int pageSize,
			Object[] values) {
		Assert.hasText(hql);
		Assert.isTrue(pageNo >= 1, "pageNo should start from 1");
		int startIndex = getStartOfPage(pageNo, pageSize);
		Query query = createQuery(hql, values);
		return query.setFirstResult(startIndex).setMaxResults(pageSize).list();

	}

	/**
	 * <p>
	 * 分页查询
	 * </p>
	 * 
	 * @param criteria
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List findBy(Criteria criteria, int pageNo, int pageSize) {
		Assert.notNull(criteria);
		Assert.isTrue(pageNo >= 1, "pageNo should start from 1");
		int startIndex = getStartOfPage(pageNo, pageSize);
		return criteria.setFirstResult(startIndex).setMaxResults(pageSize)
				.list();
	}

	/**
	 * <p>
	 * 分页查询
	 * </p>
	 * 
	 * @param clasz
	 * @param pageNo
	 * @param pageSize
	 * @param criterions
	 * @return
	 */
	public List findBy(Class clasz, int pageNo, int pageSize,
			Criterion... criterions) {
		Criteria criteria = createCriteria(clasz, criterions);
		return findBy(criteria, pageNo, pageSize);
	}

	/**
	 * <p>
	 * 分页查询,带有HibernateExpression
	 * </p>
	 * 
	 * @param clasz
	 * @param pageNo
	 * @param pageSize
	 * @param expressions
	 * @return
	 */
	public List findBy(Class clasz, int pageNo, int pageSize,
			Collection<HibernateExpression> expressions) {
		Criteria criteria = createCriteria(clasz, expressions);
		return findBy(criteria, pageNo, pageSize);
	}

	/**
	 * <p>
	 * 带排序功能,分页查询,带有HibernateExpression
	 * </p>
	 * 
	 * @param clasz
	 * @param pageNo
	 * @param pageSize
	 * @param orderBy
	 * @param isAsc
	 * @param expressions
	 * @return
	 */
	public List findBy(Class clasz, int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions) {
		Criteria criteria = createCriteria(clasz, orderBy, isAsc, expressions);
		return findBy(criteria, pageNo, pageSize);
	}

	/**
	 * <p>
	 * 分页查询
	 * </p>
	 * 
	 * @param clasz
	 * @param pageNo
	 * @param pageSize
	 * @param orderBy
	 * @param isAsc
	 * @param criterions
	 * @return
	 */
	public List findBy(Class clasz, int pageNo, int pageSize, String orderBy,
			boolean isAsc, Criterion... criterions) {
		Criteria criteria = createCriteria(clasz, orderBy, isAsc, criterions);
		return findBy(criteria, pageNo, pageSize);
	}

	/**
	 * 判断对象某些属性的值在数据库中是否唯一.
	 * 
	 * @param uniquePropertyNames
	 *            在POJO里不能重复的属性列表,以逗号分割 如"name,loginid,password"
	 */
	public <T> boolean isUnique(Class<T> clasz, Object entity,
			String uniquePropertyNames) {
		Assert.hasText(uniquePropertyNames);
		Criteria criteria = createCriteria(clasz).setProjection(
				Projections.rowCount());
		String[] nameList = uniquePropertyNames.split(",");
		try {
			// 循环加入唯一列
			for (String name : nameList) {
				criteria.add(Restrictions.eq(name, PropertyUtils.getProperty(
						entity, name)));
			}

			// 以下代码为了如果是update的情况,排除entity自身.

			// 取得entity的主键值
			Serializable id = getId(clasz, entity);

			// 如果id!=null,说明对象已存在,该操作为update,加入排除自身的判断
			if (id != null) {
				String idName = getIdName(clasz);
				criteria.add(Restrictions.not(Restrictions.eq(idName, id)));
			}

		} catch (Exception e) {
			ReflectionUtils.handleReflectionException(e);
		}
		Integer resultCount = (Integer) criteria.uniqueResult();
		return resultCount.intValue() == 0;
	}

	/**
	 * <p>
	 * 取得对象的主键值,辅助函数.
	 * </p>
	 * 
	 * @param clasz
	 * @param entity
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Serializable getId(Class clasz, Object entity)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		Assert.notNull(entity);
		Assert.notNull(clasz);

		return (Serializable) PropertyUtils.getProperty(entity,
				getIdName(clasz));
	}

	/**
	 * <p>
	 * 取得对象的主键值,辅助函数.
	 * </p>
	 * 
	 * @param entity
	 * @return
	 */
	public Serializable getId(Object entity) {
		return getSession().getIdentifier(entity);
	}

	/**
	 * <p>
	 * 取得对象的主键名,辅助函数.
	 * </p>
	 * 
	 * @param clazz
	 * @return
	 */
	public String getIdName(Class clazz) {
		Assert.notNull(clazz);
		ClassMetadata meta = getSessionFactory().getClassMetadata(clazz);
		Assert.notNull(meta, "Class " + clazz
				+ " not define in hibernate session factory.");
		String idName = meta.getIdentifierPropertyName();
		Assert.hasText(idName, clazz.getSimpleName()
				+ " has no identifier property define.");
		return idName;
	}

	/**
	 * <p>
	 * 去除hql的select 子句，未考虑union的情况,用于页查询.
	 * </p>
	 * 
	 * @param hql
	 * @return
	 */
	private static String removeSelect(String hql) {
		Assert.hasText(hql);
		int beginPos = hql.toLowerCase().indexOf("from");
		Assert.isTrue(beginPos != -1, " hql : " + hql
				+ " must has a keyword 'from'");
		return hql.substring(beginPos);
	}

	/**
	 * <p>
	 * 去除hql的orderby 子句，用于页查询.
	 * </p>
	 * 
	 * @param hql
	 * @return
	 */
	private static String removeOrders(String hql) {
		Assert.hasText(hql);
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*",
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(hql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * <p>
	 * 获取任一页第一条数据在数据集的位置.
	 * </p>
	 * 
	 * @param pageNo
	 *            从1开始的页号
	 * @param pageSize
	 *            每页记录条数
	 * @return 该页第一条数据
	 */
	private static int getStartOfPage(int pageNo, int pageSize) {
		return (pageNo - 1) * pageSize;
	}

	/**
	 * <p>
	 * 为Hibernate3.1之前版本做兼容
	 * </p>
	 * 
	 * @param obj
	 * @return
	 */
	private static Long toLong(Object obj) {
		if (obj instanceof Long) {
			return (Long) obj;
		} else if (obj instanceof Integer) {
			Integer i = (Integer) obj;
			return i.longValue();
		} else {
			return 0L;
		}
	}

	/**
	 * 封装分页查询
	 * 
	 * @param hql
	 * @param startPageNo
	 * @param maxResultsInPage
	 * @return
	 * @author penglei
	 */
	public Page findByPage(String hql, Object params, final int startPageNo,
			final int maxResultsInPage) {
		Query query = createQuery(hql);
		SQLQuery countQuery = getSession()
				.createSQLQuery(generateCountHql(hql));
		if (params instanceof Map) {
			prepareHql((Map<String, Object>) params, new Object[] { countQuery,
					query });
		} else {
			prepareHql((List) params, new Object[] { countQuery, query });
		}

		Long totalCount = ((BigDecimal) countQuery.uniqueResult()).longValue();
		query.setMaxResults(maxResultsInPage);
		query.setFirstResult((startPageNo - 1) * maxResultsInPage);
		List datas = query.list();
		return new Page(startPageNo, totalCount, maxResultsInPage, datas);
	}

	/**
	 * 根据原始的hql生成统计总数的hql
	 * 
	 * @author penglei
	 */
	private String generateCountHql(String originalHql) {
		QueryTranslatorImpl queryTranslatorImpl = new QueryTranslatorImpl(
				originalHql, originalHql, Collections.EMPTY_MAP,
				(SessionFactoryImplementor) getSessionFactory());
		queryTranslatorImpl.compile(Collections.EMPTY_MAP, false);
		return "SELECT count(*) FROM (" + queryTranslatorImpl.getSQLString()
				+ " )";
	}

	/**
	 * 准备Query语句 目前主要是做将参数设置到Query中去
	 * 
	 * @param params
	 * @param hqls
	 * @author penglei
	 */
	private void prepareHql(Map<String, Object> params, Object[] querys) {
		if (null != params) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				if (entry.getValue() instanceof Collection) {
					for (int i = 0; i < querys.length; i++) {
						if (querys[i] instanceof SQLQuery) {

							SQLQuery query = (SQLQuery) querys[i];
							query.setParameterList(entry.getKey(),
									(Collection) entry.getValue());
						} else {
							Query query = (Query) querys[i];
							query.setParameterList(entry.getKey(),
									(Collection) entry.getValue());
						}

					}
				} else {
					for (int i = 0; i < querys.length; i++) {
						if (querys[i] instanceof SQLQuery) {
							SQLQuery query = (SQLQuery) querys[i];
							query
									.setParameter(entry.getKey(), entry
											.getValue());

						} else {
							Query query = (Query) querys[i];
							query
									.setParameter(entry.getKey(), entry
											.getValue());
						}

					}
				}

			}
		}
	}

	private void prepareHql(List params, Object[] querys) {
		if (null != params) {
			for (int i = 0; i < params.size(); i++) {
				Object entry = params.get(i);
				for (int j = 0; j < querys.length; j++) {
					if (querys[j] instanceof SQLQuery) {
						SQLQuery query = (SQLQuery) querys[j];
						query.setParameter(i, entry);

					} else {
						Query query = (Query) querys[j];
						query.setParameter(i, entry);
					}

				}

			}
		}
	}
}
