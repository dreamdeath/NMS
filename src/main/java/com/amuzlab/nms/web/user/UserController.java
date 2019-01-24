package com.amuzlab.nms.web.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.amuzlab.nms.web.domain.RoleVo;
import com.amuzlab.nms.web.domain.ServerGroupVo;
import com.amuzlab.nms.web.domain.UserVo;

@Controller
public class UserController {
	private Logger logger = Logger.getLogger(UserController.class);
	
	@Resource
	private UserService userService;
	
	@RequestMapping(value="/user.do",method = RequestMethod.GET)
	public String  viewUser(ModelMap model,@ModelAttribute("form") UserVo vo) {
		return "/user/user";		
	}
	
	@RequestMapping(value="/user/list.do",method = RequestMethod.GET)
	public @ResponseBody List<UserVo> userList(@ModelAttribute UserVo vo, ModelMap model) {
		return userService.getUserInfoList();
	}
	
	@RequestMapping(value="/role/list.do",method = RequestMethod.GET)
	public @ResponseBody List<RoleVo> roleList(@ModelAttribute RoleVo vo, ModelMap model) {
		return userService.getRoleInfoList();
	}
	
	@RequestMapping(value="/user/info.do",method = RequestMethod.GET)
	public @ResponseBody UserVo userLogin(@ModelAttribute UserVo vo, ModelMap model) {
		return  userService.getUserInfo(vo);
	}
	
	@RequestMapping(value="/user/insert.do",method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> insert(@ModelAttribute UserVo vo, ModelMap model) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		boolean result = false;
		String resultMessage = "";
		try {
			userService.insertUserInfo(vo);
			resultMessage = "성공적으로 사용자가 생성되었습니다.";
			result = true;
		} catch(DataIntegrityViolationException  e){			
			resultMessage ="이미 사용중인 아이디 입니다.\n다른 아이디를 선택하세요.";
			logger.error(e);
		} catch(Exception e) {
			e.printStackTrace();
			resultMessage = "사용자 생성 중 오류가 발생했습니다.";
			logger.error(e);
		}
		resultMap.put("result", result);
		resultMap.put("message", resultMessage);
		return resultMap;
	}
	
	@RequestMapping(value="/user/update.do",method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> update(@ModelAttribute UserVo vo, ModelMap model) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		boolean result = false;
		String resultMessage = "";
		try {
			userService.updateUserInfo(vo);
			resultMessage = "성공적으로 사용자 정보가 수정되었습니다.";
			result = true;
		} catch(Exception e) {
			e.printStackTrace();
			resultMessage = "사용자 정보 수정 중 오류가 발생했습니다.";
			logger.error(e);
		}
		resultMap.put("result", result);
		resultMap.put("message", resultMessage);
		return resultMap;
	}
	
	@RequestMapping(value="/user/delete.do",method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> delete(@ModelAttribute UserVo vo,ModelMap model) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		boolean result = false;
		String resultMessage = "";
		try {
			userService.deleteUserInfo(vo);
			resultMessage = "성공적으로 사용자 정보가 삭제되었습니다.";
			result = true;
		} catch(Exception e) {
			e.printStackTrace();
			resultMessage = "사용자 삭제 중 오류가 발생했습니다.";
			logger.error(e);
		}
		resultMap.put("result", result);
		resultMap.put("message", resultMessage);
		return resultMap;
	}
}
