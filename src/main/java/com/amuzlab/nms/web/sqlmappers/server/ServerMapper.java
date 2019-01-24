package com.amuzlab.nms.web.sqlmappers.server;

import java.util.ArrayList;

import org.springframework.stereotype.Repository;

import com.amuzlab.nms.web.domain.ServerGroupVo;
import com.amuzlab.nms.web.domain.ServerVo;

@Repository
public interface ServerMapper {
	public  ServerGroupVo getGroupInfo(ServerGroupVo vo);
	
	public ServerVo getServerInfo(ServerVo vo);
	
	public ArrayList< ServerGroupVo> getGroupList();
	
	public ArrayList<ServerVo> getServerList(ServerGroupVo vo);
	
	public int insertGroupInfo(ServerGroupVo vo);
	
	public int insertServerInfo(ServerVo vo);
	
	public int updateGroupInfo(ServerGroupVo vo);
	
	public int updateServerInfo(ServerVo vo);
	
	public int deleteGroupInfo(ServerGroupVo vo);
	
	public int deleteServerInfo(ServerVo vo);
	
	public int updateServerState(ServerVo vo);
	
}
