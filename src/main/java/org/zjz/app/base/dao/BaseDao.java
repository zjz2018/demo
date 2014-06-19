package org.zjz.app.base.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.transform.ResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.zjz.app.base.dao.util.Constants.MatchType;
import org.zjz.app.base.dao.util.Page;
import org.zjz.app.base.utils.ReflectionUtils;

/**
 * DAO泛型基类.
 * 扩展功能包括分页查询,按属性过滤条件列表查询.
 * 
 * @param <T> DAO操作的对象类型
 * @param <PK> 主键类型
 */
public abstract class BaseDao<T, PK extends Serializable> {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	protected SessionFactory sessionFactory;

	protected Class<T> entityClass;

	/**
	 * 用于Dao层子类使用的构造函数.
	 * 通过子类的泛型定义取得对象类型Class.
	 * eg.
	 * public class UserDao extends BaseDao<User, Long>
	 */
	public BaseDao() {
		this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/**
	 * 执行HQL进行批量修改/删除操作.
	 * 
	 * @param values 命名参数,按名称绑定.
	 * @return 更新记录数.
	 */
	public int batchHqlExecute(final String hql, final Map<String, ?> values) {
		return createHqlQuery(hql, values).executeUpdate();
	}

	/**
	 * 执行SQL进行批量修改/删除操作.
	 * 
	 * @param values 命名参数,按名称绑定.
	 * @return 更新记录数.
	 */
	public int batchSqlExecute(final String sql, final Map<String, ?> values) {
		return createSqlQuery(sql, values).executeUpdate();
	}

	/**
	 * 按属性条件参数创建Criterion,辅助函数.
	 */
	protected Criterion buildCriterion(final String propertyName, final Object propertyValue, final MatchType matchType) {
		Assert.hasText(propertyName, "propertyName不能为空");
		Criterion criterion = null;
		// 根据MatchType构造criterion
		switch (matchType) {
		case EQ:
			criterion = Restrictions.eq(propertyName, propertyValue);
			break;
		case LIKE:
			criterion = Restrictions.like(propertyName, (String) propertyValue, MatchMode.ANYWHERE);
			break;
		case LE:
			criterion = Restrictions.le(propertyName, propertyValue);
			break;
		case LT:
			criterion = Restrictions.lt(propertyName, propertyValue);
			break;
		case GE:
			criterion = Restrictions.ge(propertyName, propertyValue);
			break;
		case GT:
			criterion = Restrictions.gt(propertyName, propertyValue);
		}
		return criterion;
	}

	/**
	 * 执行count查询获得本次Criteria查询所能获得的对象总数.
	 */
	@SuppressWarnings("unchecked")
	private long countCriteriaResult(final Criteria c) {
		CriteriaImpl impl = (CriteriaImpl) c;

		// 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
		Projection projection = impl.getProjection();
		ResultTransformer transformer = impl.getResultTransformer();

		List<CriteriaImpl.OrderEntry> orderEntries = null;
		try {
			orderEntries = (List) ReflectionUtils.getFieldValue(impl, "orderEntries");
			ReflectionUtils.setFieldValue(impl, "orderEntries", new ArrayList());
		} catch (Exception e) {
			logger.error("不可能抛出的异常:{}", e.getMessage());
		}

		// 执行Count查询
		Long totalCountObject = (Long) c.setProjection(Projections.rowCount()).uniqueResult();
		long totalCount = (totalCountObject != null) ? totalCountObject : 0;

		// 将之前的Projection,ResultTransformer和OrderBy条件重新设回去
		c.setProjection(projection);

		if (projection == null) {
			c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		if (transformer != null) {
			c.setResultTransformer(transformer);
		}
		try {
			ReflectionUtils.setFieldValue(impl, "orderEntries", orderEntries);
		} catch (Exception e) {
			logger.error("不可能抛出的异常:{}", e.getMessage());
		}

		return totalCount;
	}

	/**
	 * 执行count查询获得本次Hql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
	 */
	protected long countHqlResult(final String hql, final Map<String, ?> values) {
		String countHql = prepareCountHql(hql);
		try {
			Long count = findUniqueByHql(countHql, values);
			return count;
		} catch (Exception e) {
			throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
		}
	}

	/**
	 * 执行count查询获得本次Sql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的sql语句,复杂的sql查询请另行编写count语句查询.
	 */
	protected long countSqlResult(final String sql, final Map<String, ?> values) {
		String countHql = prepareCountHql(sql);
		try {
			Long count = (Long) createSqlQuery(countHql, values).uniqueResult();
			return count;
		} catch (Exception e) {
			throw new RuntimeException("sql can't be auto count, sql is:" + countHql, e);
		}
	}

	/**
	 * 根据Criterion条件创建Criteria.
	 * 与find()函数可进行更加灵活的操作.
	 * 
	 * @param criterions 数量可变的Criterion.
	 */
	private Criteria createCriteria(final Criterion... criterions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		return criteria;
	}

	/**
	 * 根据查询HQL与参数列表创建Query对象.
	 * 
	 * @param values 命名参数,按名称绑定.
	 */
	private Query createHqlQuery(final String hql, final Map<String, ?> values) {
		Assert.hasText(hql, "queryString不能为空");
		Query query = getSession().createQuery(hql);
		if (values != null) {
			query.setProperties(values);
		}
		return query;
	}

	/**
	 * 根据查询SQL与参数列表创建Query对象.
	 * 
	 * @param values 命名参数,按名称绑定.
	 */
	private Query createSqlQuery(final String sql, final Map<String, ?> values) {
		Assert.hasText(sql, "queryString不能为空");
		Query query = getSession().createQuery(sql);
		if (values != null) {
			query.setProperties(values);
		}
		return query;
	}

	/**
	 * 按id删除对象.
	 */
	public void delete(final PK id) {
		Assert.notNull(id, "id不能为空");
		delete(findOne(id));
		logger.debug("delete entity {},id is {}", entityClass.getSimpleName(), id);
	}

	/**
	 * 删除对象.
	 * 
	 * @param entity 对象必须是session中的对象或含id属性的transient对象.
	 */
	public void delete(final T entity) {
		Assert.notNull(entity, "entity不能为空");
		getSession().delete(entity);
		logger.debug("delete entity: {}", entity);
	}

	/**
	 * 按Criteria查询对象列表.
	 * 
	 * @param criterions 数量可变的Criterion.
	 */
	public List<T> find(final Criterion... criterions) {
		return createCriteria(criterions).list();
	}

	/**
	 * 按HQL查询对象列表.
	 * 
	 * @param values 命名参数,按名称绑定.
	 */
	public <X> List<X> findByHql(final String hql, final Map<String, ?> values) {
		return createHqlQuery(hql, values).list();
	}

	/**
	 * 按属性查找对象列表, 匹配方式为相等.
	 */
	public List<T> findByPorperty(final String propertyName, final Object value) {
		Assert.hasText(propertyName, "propertyName不能为空");
		Criterion criterion = Restrictions.eq(propertyName, value);
		return find(criterion);
	}

	/**
	 * 按属性查找对象列表,支持多种匹配方式.
	 * 
	 * @param matchType 匹配方式,目前支持的取值见Constants的MatcheType enum.
	 */
	public List<T> findByProperty(final String propertyName, final Object value, final MatchType matchType) {
		Criterion criterion = buildCriterion(propertyName, value, matchType);
		return find(criterion);
	}

	/**
	 * 按Criteria分页查询.
	 * 
	 * @param page 分页参数.
	 * @param criterions 数量可变的Criterion.
	 * 
	 * @return 分页查询结果.附带结果列表及所有查询输入参数.
	 */
	@SuppressWarnings("unchecked")
	public Page<T> findPage(final Page<T> page, final Criterion... criterions) {
		Assert.notNull(page, "page不能为空");
		Criteria c = createCriteria(criterions);
		if (page.isAutoCount()) {
			long totalCount = countCriteriaResult(c);
			page.setTotalCount(totalCount);
		}
		setPageParameterToCriteria(c, page);
		List result = c.list();
		page.setResult(result);
		return page;
	}

	/**
	 * 按HQL分页查询.
	 * 
	 * @param page 分页参数.
	 * @param hql hql语句.
	 * @param values 命名参数,按名称绑定.
	 * 
	 * @return 分页查询结果, 附带结果列表及所有查询输入参数.
	 */
	@SuppressWarnings("unchecked")
	public Page<T> findPageByHql(final Page<T> page, String hql, final Map<String, ?> values) {
		Assert.notNull(page, "page不能为空");
		// 排序
		String order = "";
		if (page.isOrderBySetted()) {
			order = " order by " + StringUtils.join(page.getOrders().iterator(), ",");
		}
		Query q = createHqlQuery(hql + order, values);
		if (page.isAutoCount()) {
			long totalCount = countHqlResult(hql + order, values);
			page.setTotalCount(totalCount);
		}
		setPageParameterToQuery(q, page);
		List result = q.list();
		page.setResult(result);
		return page;
	}

	/**
	 * 按SQL分页查询.
	 * 
	 * @param page 分页参数.
	 * @param sql sql语句.
	 * @param values 命名参数,按名称绑定.
	 * 
	 * @return 分页查询结果, 附带结果列表及所有查询输入参数.
	 */
	@SuppressWarnings("unchecked")
	public Page<T> findPageBySql(final Page<T> page, String sql, final Map<String, ?> values) {
		Assert.notNull(page, "page不能为空");
		// 排序
		String order = "";
		if (page.isOrderBySetted()) {
			order = " order by " + StringUtils.join(page.getOrders().iterator(), ",");
		}
		Query q = createSqlQuery(sql + order, values);
		// 是否自动计算总数,union时应该关闭,手动写SQL
		if (page.isAutoCount()) {
			long totalCount = countSqlResult(sql + order, values);
			page.setTotalCount(totalCount);
		}
		setPageParameterToQuery(q, page);
		List result = q.list();
		page.setResult(result);
		return page;
	}

	/**
	 * 按HQL查询唯一对象.
	 * 
	 * @param values 命名参数,按名称绑定.
	 */
	public <X> X findUniqueByHql(final String hql, final Map<String, ?> values) {
		return (X) createHqlQuery(hql, values).uniqueResult();
	}

	/**
	 * 按属性查找唯一对象, 匹配方式为相等.
	 */
	public T findUniqueByProperty(final String propertyName, final Object value) {
		Assert.hasText(propertyName, "propertyName不能为空");
		Criterion criterion = Restrictions.eq(propertyName, value);
		return (T) createCriteria(criterion).uniqueResult();
	}

	/**
	 * Flush当前Session.
	 */
	public void flush() {
		getSession().flush();
	}

	/**
	 * 按id列表获取对象列表.
	 */
	public List<T> findAll(final Collection<PK> ids) {
		return find(Restrictions.in(getIdName(), ids));
	}

	// -- 分页查询函数 --//

	/**
	 * 按id获取对象.
	 */
	public T findOne(final PK id) {
		Assert.notNull(id, "id不能为空");
		return (T) getSession().load(entityClass, id);
	}

	/**
	 * 获取全部对象.
	 */
	public List<T> findAll() {
		return find();
	}

	/**
	 * 分页获取全部对象.
	 */
	public Page<T> findAll(final Page<T> page) {
		return findPage(page);
	}

	/**
	 * 取得对象的主键名.
	 */
	private String getIdName() {
		ClassMetadata meta = sessionFactory.getClassMetadata(entityClass);
		return meta.getIdentifierPropertyName();
	}

	/**
	 * 取得当前Session.
	 */
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * 判断对象的属性值在数据库内是否唯一.
	 * 
	 * 在修改对象的情景下,如果属性新修改的值(value)等于属性原来的值(orgValue)则不作比较.
	 */
	public boolean isPropertyUnique(final String propertyName, final Object newValue, final Object oldValue) {
		if ((newValue == null) || newValue.equals(oldValue)) {
			return true;
		}
		Object object = findUniqueByProperty(propertyName, newValue);
		return (object == null);
	}

	/**
	 * 简单处理HQL的数目查询
	 * 
	 * @param orgHql
	 * @return
	 */
	private String prepareCountHql(String orgHql) {
		String fromHql = orgHql;
		// select子句与order by子句会影响count查询,进行简单的排除.
		fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "order by");
		String countHql = "select count(*) " + fromHql;
		return countHql;
	}

	/**
	 * 保存新增或修改的对象.
	 */
	public void save(final T entity) {
		Assert.notNull(entity, "entity不能为空");
		getSession().saveOrUpdate(entity);
		logger.debug("save entity: {}", entity);
	}

	// -- 属性过滤条件(PropertyFilter)查询函数 --//

	/**
	 * 设置分页参数到Criteria对象,辅助函数.
	 */
	private Criteria setPageParameterToCriteria(final Criteria c, final Page<T> page) {
		Assert.isTrue(page.getPageSize() > 0, "Page Size must larger than zero");
		// hibernate的firstResult的序号从0开始
		c.setFirstResult(page.getFirst() - 1);
		c.setMaxResults(page.getPageSize());
		if (page.isOrderBySetted()) {
			for (Order order : page.getOrders()) {
				c.addOrder(order);
			}
		}
		return c;
	}

	/**
	 * 设置分页参数到Query对象,辅助函数.
	 */
	private Query setPageParameterToQuery(final Query q, final Page<T> page) {
		Assert.isTrue(page.getPageSize() > 0, "Page Size must larger than zero");
		// hibernate的firstResult的序号从0开始
		q.setFirstResult(page.getFirst() - 1);
		q.setMaxResults(page.getPageSize());
		return q;
	}

	/**
	 * 根据查询SQL每行数据返回MAP
	 * 
	 * @param values 命名参数,按名称绑定.
	 */
	public List<Map<String, Object>> findBySqlToMap(final String sql, final Map<String, ?> values) {
		Query query = createSqlQuery(sql, values).setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}

}
