package org.zjz.app.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zjz.app.base.dao.util.Page;
import org.zjz.app.example.dao.UserDao;
import org.zjz.app.example.entity.User;
import org.zjz.app.example.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDao userDao;

	public Page<User> findPage(Page<User> page, User user) {
		return userDao.findPage(page, user);
	}

}
