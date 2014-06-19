package org.zjz.app.example.service;

import org.zjz.app.base.dao.util.Page;
import org.zjz.app.example.entity.User;

public interface UserService {

	public Page<User> findPage(Page<User> page, User user);

}
