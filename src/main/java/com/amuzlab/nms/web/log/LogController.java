package com.amuzlab.nms.web.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amuzlab.nms.web.domain.LogVo;

@Controller
public class LogController {
	@Resource
	private LogService logService;
	
	@RequestMapping(value="/nmslog/insert.do",method = RequestMethod.PUT)
	public @ResponseBody HashMap<String,Object> insertLogInfo(ModelMap model,@ModelAttribute("form") LogVo vo) {
		
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		
		boolean result = true;
		LogVo logInfo = logService.selectLogInfo(vo);
		if(logInfo == null) {
			result = logService.insertLogInfo(vo);
		}

		hashMap.put("result", result);		
		return hashMap;
		
		
	}
	
	@RequestMapping(value="/nmslog/update.do",method = RequestMethod.PUT)
	public @ResponseBody HashMap<String,Object> updateLogInfo(ModelMap model,@ModelAttribute("form") LogVo vo) {		
		
		boolean result = logService.updateLogInfo(vo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		
		return hashMap;
		
		
	}
	
	@RequestMapping(value="/nmslog/list.do",method = RequestMethod.GET)
	public @ResponseBody HashMap<String,Object> getLogList(ModelMap model,@ModelAttribute LogVo vo) {
			HashMap<String,Object> hashMap = new HashMap<String,Object>();
			ArrayList<LogVo> logList = logService.getLogList(vo);
			int totalRecords = logService.getTotalLogCount(vo);
			hashMap.put("totalRecords", totalRecords);
			hashMap.put("datas", logList);
			return hashMap;		
	}

	@RequestMapping(value="/nmslog/activeList.do",method = RequestMethod.GET)
	public @ResponseBody HashMap<String,Object> activeLogList(ModelMap model,@ModelAttribute LogVo vo) {
			boolean result = false;
			HashMap<String,Object> hashMap = new HashMap<String,Object>();
			List<LogVo> logList = null;
			try {
				logList = logService.getActiveLogList(vo);
				result = true;
			} catch(Exception e){
				e.printStackTrace();
			}
			hashMap.put("result",result );
			hashMap.put("data", logList);
			return hashMap;
	}
	
	@RequestMapping(value="/nmslog/del.do",method = RequestMethod.POST)
	public @ResponseBody HashMap<String,Object> delLogInfo(ModelMap model,@ModelAttribute LogVo vo) {
		
		boolean result = logService.deleteLogInfo(vo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		
		return hashMap;		
		
	}
}
