package com.example.web.server;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import com.example.web.domain.ServerGroupVo;
import com.example.web.domain.ServerVo;
import com.example.web.sqlmappers.server.ServerMapper;

@Service
public class ServerService {
	@Resource
	private ServerMapper serverMapper;
	
	public ServerGroupVo getGroupInfo(ServerGroupVo vo) {
		ServerGroupVo data = null;
		data = serverMapper.getGroupInfo(vo);
		return data;
	}
	
	public ServerVo getServerInfo(ServerVo vo) {
		ServerVo data = null;
		data = serverMapper.getServerInfo(vo);
		return data;
	}
	
	public ArrayList<ServerGroupVo> getGroupList() {
		ArrayList<ServerGroupVo> dataList = null;
		dataList = serverMapper.getGroupList();
		return dataList;
	}
	
	public ArrayList<ServerVo> getServerList(ServerGroupVo vo) {
		ArrayList<ServerVo> dataList = null;
		dataList = serverMapper.getServerList(vo);
		return dataList;
	}
	
	public Boolean insertGroupInfo(ServerGroupVo vo) {
		boolean result = false;
		try {
			 serverMapper.insertGroupInfo(vo);
			result = true;
	
		} catch(Exception e){
			
		}
		return result;
	}
	
	public Boolean insertServerInfo(ServerVo vo) {
		boolean result = false;
		try {
			serverMapper.insertServerInfo(vo);
			result = true;	
		} catch(Exception e){
			
		}
		return result;
	}
	
	public Boolean updateGroupInfo(ServerGroupVo vo) {
		boolean result = false;
		try {
			 serverMapper.updateGroupInfo(vo);
			result = true;
	
		} catch(Exception e){
			
		}
		return result;
	}
	
	public Boolean updateServerInfo(ServerVo vo) {
		boolean result = false;
		try {
			serverMapper.updateServerInfo(vo);
			result = true;	
		} catch(Exception e){
			
		}
		return result;
	}
	
	public Boolean deleteGroupInfo(ServerGroupVo vo) {
		boolean result = false;
		try {
			 serverMapper.deleteGroupInfo(vo);
			result = true;
	
		} catch(Exception e){
			
		}
		return result;
	}
	
	public Boolean deleteServerInfo(ServerVo vo) {
		boolean result = false;
		try {
			serverMapper.deleteServerInfo(vo);
			result = true;	
		} catch(Exception e){
			
		}
		return result;
	}
	
	public Boolean updateServerState(ServerVo vo) {
		boolean result = false;
		try {
			serverMapper.updateServerState(vo);
			result = true;	
		} catch(Exception e){
			
		}
		return result;
	}
}
