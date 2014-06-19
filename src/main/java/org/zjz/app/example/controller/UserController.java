package org.zjz.app.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.zjz.app.base.dao.util.Page;
import org.zjz.app.base.service.CommonService;
import org.zjz.app.example.entity.User;
import org.zjz.app.example.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private CommonService commonService;

	Page<User> page = new Page<User>();

	@RequestMapping(method = RequestMethod.GET)
	private String list(@RequestParam(value = "pageNO", defaultValue = "1") int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "10") int pageSize, Model model,
			@ModelAttribute("user") User user) {
		page.setPageNo(pageNumber);
		page.setPageSize(pageSize);
		userService.findPage(page, user);
		model.addAttribute("page", page);
		return "user/user";
	}

	@RequestMapping(value = "create", method = RequestMethod.GET)
	public String createForm(Model model) {
		model.addAttribute("user", new User());
		return "user/user-show";
	}

	@RequestMapping(value = "update/{id}", method = RequestMethod.GET)
	public String updateForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute("user", commonService.findOne(User.class, id));
		return "user/user-show";
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	public String update(@ModelAttribute("user") User user) {
		commonService.save(user);
		return "redirect:/user";
	}

	@RequestMapping(value = "delete/{id}")
	public String delete(@PathVariable("id") Long id) {
		commonService.delete(User.class, id);
		return "redirect:/user";
	}

}
