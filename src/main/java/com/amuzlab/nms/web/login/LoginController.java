package com.amuzlab.nms.web.login;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.amuzlab.nms.web.domain.UserVo;

@Controller
public class LoginController {
	@Resource
	private LoginService loginService;
	
	@RequestMapping(value="/authLogin.do",method = RequestMethod.POST)
	public String  authLogin(ModelMap model,@ModelAttribute("form") UserVo form) {
		System.out.println("form.getUserId() :"+form.getUserid());
		if (form.getUserid() != null && !"".equals(form.getUserid())) {
			UserVo userVo = loginService.getUser(form.getUserid());
			System.out.println(userVo);
			if(userVo != null && !userVo.getUserid().equals("")){
				return "redirect:/index.do";
			} 
			
		}
		return "redirect:/login.do?error";		
	}
	
	@RequestMapping(value="/login.do",method = {RequestMethod.GET,RequestMethod.PUT})
	public String userLogin(ModelMap model) {
		return "/login/login";
	}
	
	@RequestMapping(value="/logout.do",method = RequestMethod.POST)
	public String logout(ModelMap model) {
		return "redirect:/login.do?logout";
	}
}
