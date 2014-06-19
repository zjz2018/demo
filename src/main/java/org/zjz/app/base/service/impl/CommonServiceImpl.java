package org.zjz.app.base.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zjz.app.base.dao.CommonDao;
import org.zjz.app.base.dao.util.Constants.MatchType;
import org.zjz.app.base.service.CommonService;

@Service
@Transactional
public class CommonServiceImpl implements CommonService {
	@Autowired
	private CommonDao commonDao;

	public <T, PK extends Serializable> void delete(Class<T> entityClass, PK id) {
		commonDao.delete(entityClass, id);
	}

	public <T> void delete(T entity) {
		commonDao.delete(entity);
	}

	public <T> List<T> findAll(Class<T> entityClass) {
		return commonDao.findAll(entityClass);
	}

	public <T> List<T> findByProperty(Class<T> entityClass, String propertyName, Object value) {
		return commonDao.findByPorperty(entityClass, propertyName, value);
	}

	public <T> List<T> findByProperty(Class<T> entityClass, String propertyName, Object value, MatchType matchType) {
		return commonDao.findByProperty(entityClass, propertyName, value, matchType);
	}

	public <T, PK extends Serializable> T findOne(Class<T> entityClass, PK id) {
		return commonDao.findOne(entityClass, id);
	}

	public <T> T findUniqueByProperty(Class<T> entityClass, String propertyName, Object value) {
		return commonDao.findUniqueByProperty(entityClass, propertyName, value);
	}

	public <T> boolean isPropertyUnique(Class<T> entity, String propertyName, Object newValue, Object oldValue) {
		return false;
	}

	public <T> void save(T entity) {
		commonDao.save(entity);

	}

	public <T> void update(T entity) {
		commonDao.save(entity);

	}

	public <T, PK extends Serializable> List<T> findAll(Class<T> entityClass, Collection<PK> ids) {
		return commonDao.findAll(entityClass, ids);
	}

}
