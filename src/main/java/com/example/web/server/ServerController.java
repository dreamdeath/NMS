package com.example.web.server;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.web.domain.ServerGroupVo;
import com.example.web.domain.ServerVo;

@Controller
public class ServerController {
	@Resource
	private ServerService serverService;
	
	@RequestMapping(value="/group/info.do",method = RequestMethod.GET)
	public @ResponseBody ServerGroupVo getGroupInfo(ModelMap model,@ModelAttribute ServerGroupVo vo) {

			return serverService.getGroupInfo(vo);
		
		
	}
	
	@RequestMapping(value="/server/info.do",method = RequestMethod.GET)
	public @ResponseBody ServerVo getServerInfo(ModelMap model,@ModelAttribute ServerVo vo) {
		return serverService.getServerInfo(vo);
		
		
	}
	
	@RequestMapping(value="/group/list.do",method = RequestMethod.GET)
	public @ResponseBody ArrayList<ServerGroupVo> getGroupList(ModelMap model,@ModelAttribute ServerGroupVo vo) {

			return serverService.getGroupList();
		
		
	}
	
	@RequestMapping(value="/server/list.do",method = RequestMethod.GET)
	public @ResponseBody HashMap<String,Object> getServerList(ModelMap model,@ModelAttribute ServerGroupVo serverGrpVo) {
		HashMap<String,Object> result = new HashMap<String,Object>();
		result.put("list", serverService.getServerList(serverGrpVo));
		result.put("grp_id", serverGrpVo.getId());
		result.put("grp_nm", serverGrpVo.getGroup_name());
		return result;
		
		
	}
	
	@RequestMapping(value="/group/insert.do",method = RequestMethod.PUT)
	public @ResponseBody HashMap<String,Object> insertGroupInfo(ModelMap model,@ModelAttribute("form") ServerGroupVo serverGrpVo) {
		
		boolean result = serverService.insertGroupInfo(serverGrpVo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		
		return hashMap;
		
		
	}
	
	@RequestMapping(value="/server/insert.do",method = RequestMethod.PUT)
	public @ResponseBody HashMap<String,Object> insertServerInfo(ModelMap model,@ModelAttribute("form") ServerVo serverVo) {
		
		boolean result = serverService.insertServerInfo(serverVo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		
		return hashMap;		
		
	}
	
	@RequestMapping(value="/group/update.do",method = RequestMethod.PUT)
	public @ResponseBody HashMap<String,Object> updateGroupInfo(ModelMap model,@ModelAttribute("form") ServerGroupVo serverGrpVo) {
		
		boolean result = serverService.updateGroupInfo(serverGrpVo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		
		return hashMap;
		
		
	}
	
	@RequestMapping(value="/server/update.do",method = RequestMethod.PUT)
	public @ResponseBody HashMap<String,Object> updateServerInfo(ModelMap model,@ModelAttribute("form") ServerVo serverVo) {
		
		boolean result = serverService.updateServerInfo(serverVo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		
		return hashMap;		
		
	}
	
	@RequestMapping(value="/group/del.do",method = RequestMethod.POST)
	public @ResponseBody HashMap<String,Object> delGroupInfo(ModelMap model,@ModelAttribute ServerGroupVo serverGrpVo) {
		
		boolean result = serverService.deleteGroupInfo(serverGrpVo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		
		return hashMap;		
		
	}
	
	@RequestMapping(value="/server/del.do",method = RequestMethod.POST)
	public @ResponseBody HashMap<String,Object> delServerInfo(ModelMap model,@ModelAttribute ServerVo serverVo) {
		
		System.out.println(serverVo.getId());
		boolean result = serverService.deleteServerInfo(serverVo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		
		return hashMap;		
		
	}
	
	@RequestMapping(value="/server/state.do",method = RequestMethod.PUT)
	public @ResponseBody HashMap<String,Object> updateServerState(ModelMap model,@ModelAttribute("form") ServerVo serverVo) {
		
		boolean result = serverService.updateServerState(serverVo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		
		return hashMap;		
		
	}
	
}
