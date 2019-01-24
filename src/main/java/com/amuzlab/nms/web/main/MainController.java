package com.amuzlab.nms.web.main;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.amuzlab.nms.web.domain.UserVo;
import com.amuzlab.nms.web.snmp.NmsSnmpClient;
import com.amuzlab.nms.web.tcpsocket.TcpSocketServer;

@Controller
public class MainController {	
	
	@Autowired
	  NmsSnmpClient nmsSnmpClient;
	
	@Autowired
	  TcpSocketServer tcpSocketServer;
	
	@RequestMapping(value="/index.do",method = RequestMethod.GET)
	public ModelAndView  viewIndex(ModelMap model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userId = auth.getName();
//        System.out.println("userId=============>"+userId);        
        ModelAndView mv = new ModelAndView("/index");
        mv.addObject("userId", userId);
		return mv;
	}
	
	@RequestMapping(value="/",method = RequestMethod.GET)
	public ModelAndView  getRootPath(ModelMap model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userId = auth.getName();
//        System.out.println("userId=============>"+userId);        
        ModelAndView mv = new ModelAndView("/index");
        mv.addObject("userId", userId);
		return mv;
	}
	
	@RequestMapping(value="/admin/restartSnmp",method = RequestMethod.POST)
	public @ResponseBody Map<String,Boolean> restartSnmpThread(ModelMap model) {
		boolean result = nmsSnmpClient.restartSnmpThread();
		Map<String,Boolean> resultMap = new HashMap<String,Boolean>();
		resultMap.put("result",result);
		return resultMap;
	}
	
	@RequestMapping(value="/admin/reloadServerList",method = RequestMethod.POST)
	public @ResponseBody Map<String,Boolean> reloadServerList(ModelMap model) {
		boolean result = tcpSocketServer.reloadServerList();
		Map<String,Boolean> resultMap = new HashMap<String,Boolean>();
		resultMap.put("result",result);
		return resultMap;
	}
}
