package org.zjz.app.base.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.zjz.app.base.dao.util.Constants.MatchType;

public interface CommonService {

	/**
	 * 根据主键删除相应实体
	 * 
	 * @paraT id 主键
	 */
	public <T, PK extends Serializable> void delete(Class<T> entityClass, PK id);

	/**
	 * 删除实体
	 * 
	 */
	public <T> void delete(T entity);

	/**
	 * 查询所有实体
	 * 
	 */
	public <T> List<T> findAll(Class<T> entityClass);

	/**
	 * 按属性查找对象列表, 匹配方式为相等.
	 * 
	 */
	public <T> List<T> findByProperty(Class<T> entityClass, final String propertyName, final Object value);

	/**
	 * 按属性查找对象列表,支持多种匹配方式.
	 * 
	 * @param matchType 匹配方式,目前支持的取值见Constants的MatcheType enum.
	 */
	public <T> List<T> findByProperty(Class<T> entityClass, final String propertyName, final Object value,
			final MatchType matchType);

	/**
	 * 按照主键查询
	 * 
	 * @paraT id 主键
	 * @return 返回id对应的实体
	 */
	public <T, PK extends Serializable> T findOne(Class<T> entityClass, PK id);

	/**
	 * 按属性查找唯一对象, 匹配方式为相等.
	 */
	public <T> T findUniqueByProperty(Class<T> entityClass, final String propertyName, final Object value);

	/**
	 * 判断对象的属性值在数据库内是否唯一.
	 * 
	 * 在修改对象的情景下,如果属性新修改的值(value)等于属性原来的值(orgValue)则不作比较.
	 * 
	 */
	public <T> boolean isPropertyUnique(Class<T> entity, final String propertyName, final Object newValue,
			final Object oldValue);

	/**
	 * 保存单个实体
	 * 
	 */
	public <T> void save(T entity);

	/**
	 * 更新单个实体
	 * 
	 */
	public <T> void update(T entity);

	/**
	 * 按id列表获取对象列表.
	 */
	public <T, PK extends Serializable> List<T> findAll(Class<T> entityClass, final Collection<PK> ids);

}
