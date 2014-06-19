package org.zjz.app.example.dao;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.zjz.app.base.dao.BaseDao;
import org.zjz.app.base.dao.util.Page;
import org.zjz.app.example.entity.User;

import com.google.common.collect.Maps;

@Repository
public class UserDao extends BaseDao<User, Long> {

	public Page<User> findPage(Page<User> page, User user) {
		Map<String, Object> values = Maps.newHashMap();
		StringBuilder hql = new StringBuilder();
		hql.append("from User where 1=1 ");
		if (StringUtils.isNotEmpty(user.getLoginName())) {
			hql.append("and loginName like :loginName");
			values.put("loginName", '%'+user.getLoginName()+'%');
		}
		return findPageByHql(page, hql.toString(), values);
	}

}
