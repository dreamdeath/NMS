package com.amuzlab.nms.web.log;

import java.util.ArrayList;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.amuzlab.nms.web.domain.LogVo;
import com.amuzlab.nms.web.sqlmappers.log.LogMapper;

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
	public LogVo selectLogInfo(LogVo vo) {
		LogVo logInfo = null;
		try {
			logInfo = logMapper.selectLogInfo(vo);
		} catch(Exception e){
			
		}
		return logInfo;
	}
	
	
	public Boolean updateLogInfo(LogVo vo) {
		boolean result = false;
		try {
			logMapper.updateLogInfo(vo);
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
	
	public ArrayList<LogVo> getActiveLogList(LogVo vo) {
		ArrayList<LogVo> dataList = null;
		dataList = logMapper.getActiveLogList(vo);
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
	
	public int getTotalLogCount(LogVo vo) {
		int result = 0;
		try {
			result = logMapper.getTotalLogCount(vo);
		} catch(Exception e){
			
		}
		return result;
	}
}
