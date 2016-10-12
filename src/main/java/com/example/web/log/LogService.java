package com.example.web.log;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.example.web.domain.LogVo;
import com.example.web.domain.ServerGroupVo;
import com.example.web.domain.ServerVo;
import com.example.web.sqlmappers.log.LogMapper;
import com.example.web.sqlmappers.server.ServerMapper;

@Service
public class LogService {
	@Resource
	private LogMapper logMapper;
	
	
	public Boolean insertLogInfo(LogVo vo) {
		boolean result = false;
		try {
			logMapper.insertLogInfo(vo);
			result = true;	
		} catch(Exception e){
			
		}
		return result;
	}
	
	public ArrayList<LogVo> getLogList(LogVo vo) {
		ArrayList<LogVo> dataList = null;
		dataList = logMapper.getLogList(vo);
		return dataList;
	}
	
		
	public Boolean deleteLogInfo(LogVo vo) {
		boolean result = false;
		try {
			logMapper.deleteLogInfo(vo);
			result = true;	
		} catch(Exception e){
			
		}
		return result;
	}
}
