package com.example.web.log;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.web.domain.LogVo;
import com.example.web.domain.ServerGroupVo;
import com.example.web.domain.ServerVo;

@Controller
public class LogController {
	@Resource
	private LogService logService;
	
	@RequestMapping(value="/nmslog/insert.do",method = RequestMethod.PUT)
	public @ResponseBody HashMap<String,Object> insertGroupInfo(ModelMap model,@ModelAttribute("form") LogVo vo) {
		
		boolean result = logService.insertLogInfo(vo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		
		return hashMap;
		
		
	}
	
	@RequestMapping(value="/nmslog/list.do",method = RequestMethod.GET)
	public @ResponseBody ArrayList<LogVo> getLogList(ModelMap model,@ModelAttribute LogVo vo) {
			return logService.getLogList(vo);		
	}

	
	@RequestMapping(value="/nmslog/del.do",method = RequestMethod.POST)
	public @ResponseBody HashMap<String,Object> delLogInfo(ModelMap model,@ModelAttribute LogVo vo) {
		
		boolean result = logService.deleteLogInfo(vo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		
		return hashMap;		
		
	}
}
