import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.zjz.app.base.utils.SpringTxTestCase;
import org.zjz.app.example.dao.RoleDao;
import org.zjz.app.example.dao.UserDao;
import org.zjz.app.example.entity.Role;
import org.zjz.app.example.entity.User;
import org.zjz.app.example.service.UserService;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class UserDaoTest extends SpringTxTestCase {

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private UserService userService;

	@Test
	@Rollback(false)
	//@Ignore
	public void insertUser() {
		for (int i = 0; i < 30; i++) {
			User user = new User();
			user.setName("xx" + i);
			user.setLoginName("logname" + i);
			user.setBirthday(new Date());
			user.setEmail("xx" + i + "@exp.com");
			userDao.save(user);
		}

	}

	@Test
	@Ignore
	public void listUser() {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from t_user where id<:id");
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", 3);
		List<Map<String, Object>> lists = userDao.findBySqlToMap(sql.toString(), map);
		for (Map<String, Object> map2 : lists) {
			System.out.println(map2.get("login_name"));
		}
		System.out.println(lists.size());

	}
	
	@Test
	@Ignore
	public void listuserByHql() {
		StringBuilder hql = new StringBuilder();
		hql.append("from User where id<:id");
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", 3L);
		List<User> lists=userDao.findByHql(hql.toString(), map);
		for (User user : lists) {
			System.out.println(user.getLoginName());
		}
		System.out.println(lists.size());

	}
	
	@Test
	public void testString(){
		List<String> lists=Lists.newArrayList("a","b","c");
		String str=StringUtils.join(lists.iterator(), "==");
		System.out.println(str);
		
	}

}
