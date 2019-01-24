package com.amuzlab.nms.web.server;

import java.util.ArrayList;
import java.util.HashMap;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amuzlab.nms.web.domain.ServerGroupVo;
import com.amuzlab.nms.web.domain.ServerVo;

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
	public @ResponseBody HashMap<String,Object>  getGroupList(ModelMap model,@ModelAttribute ServerGroupVo vo) {
			HashMap<String,Object> result = new HashMap<String,Object>();
			result.put("result", serverService.getGroupList());
			return result;
	}
	
	@RequestMapping(value="/server/list.do",method = RequestMethod.GET)
	public @ResponseBody HashMap<String,Object> getServerList(ModelMap model,@ModelAttribute ServerGroupVo serverGrpVo) {
		HashMap<String,Object> result = new HashMap<String,Object>();
		result.put("result", serverService.getServerList(serverGrpVo));
//		result.put("grp_id", serverGrpVo.getId());
//		result.put("grp_nm", serverGrpVo.getGroup_name());
		return result;
	}
	
	@RequestMapping(value="/admin/group/insert.do",method = RequestMethod.PUT)
	public @ResponseBody HashMap<String,Object> insertGroupInfo(ModelMap model,@ModelAttribute("form") ServerGroupVo serverGrpVo) {
		boolean result = serverService.insertGroupInfo(serverGrpVo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		return hashMap;
	}
	
	@RequestMapping(value="/admin/server/insert.do",method = RequestMethod.PUT)
	public @ResponseBody HashMap<String,Object> insertServerInfo(ModelMap model,@ModelAttribute("form") ServerVo serverVo) {
		boolean result = serverService.insertServerInfo(serverVo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		hashMap.put("serverId", serverVo.getId());
		return hashMap;		
	}
	
	@RequestMapping(value={"/group/update.do", "/admin/group/update.do"},method = RequestMethod.PUT)
	public @ResponseBody HashMap<String,Object> updateGroupInfo(ModelMap model,@ModelAttribute("form") ServerGroupVo serverGrpVo) {
		boolean result = serverService.updateGroupInfo(serverGrpVo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		return hashMap;
	}
	
	@RequestMapping(value="/admin/server/update.do",method = RequestMethod.PUT)
	public @ResponseBody HashMap<String,Object> updateServerInfo(ModelMap model,@ModelAttribute("form") ServerVo serverVo) {
		boolean result = serverService.updateServerInfo(serverVo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		return hashMap;		
	}
	
	@RequestMapping(value="/admin/group/del.do",method = RequestMethod.POST)
	public @ResponseBody HashMap<String,Object> delGroupInfo(ModelMap model,@ModelAttribute ServerGroupVo serverGrpVo) {		
		boolean result = serverService.deleteGroupInfo(serverGrpVo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		return hashMap;		
	}
	
	@RequestMapping(value="/admin/server/del.do",method = RequestMethod.POST)
	public @ResponseBody HashMap<String,Object> delServerInfo(ModelMap model,@ModelAttribute ServerVo serverVo) {
		boolean result = serverService.deleteServerInfo(serverVo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		return hashMap;
	}
	
	@RequestMapping(value="/admin/server/state.do",method = RequestMethod.PUT)
	public @ResponseBody HashMap<String,Object> updateServerState(ModelMap model,@ModelAttribute("form") ServerVo serverVo) {
		boolean result = serverService.updateServerState(serverVo);
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		hashMap.put("result", result);
		return hashMap;		
	}
	
	@RequestMapping(value="/socketServer", method=RequestMethod.GET)
	public @ResponseBody HashMap<String, Object> getSocketServerInfo(){
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		serverService.getSocketServerInfo(hashMap);
		return hashMap;
	}
	
	@RequestMapping(value="/clientSocketServer", method=RequestMethod.GET)
	public @ResponseBody HashMap<String, Object> getClientSocketServerInfo(){
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		serverService.getClientSocketServerInfo(hashMap);
		return hashMap;
	}
	@CrossOrigin(maxAge = 3600)
	@RequestMapping(value="/ems/check.do", method=RequestMethod.POST)
	public @ResponseBody HashMap<String, Object> checkValidEmsServer(ModelMap model,@ModelAttribute ServerVo vo){
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		ServerVo serverVo = serverService.getServerInfo(vo);
		if(serverVo == null) {
			hashMap.put("result", false);
		} else {
			hashMap.put("result", true);
		}
		return hashMap;
	}
	
}
