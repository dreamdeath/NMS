package com.amuzlab.nms.web.sqlmappers.log;

import java.util.ArrayList;
import org.springframework.stereotype.Repository;
import com.amuzlab.nms.web.domain.LogVo;


@Repository
public interface LogMapper {	
	public void insertLogInfo(LogVo vo);
	public LogVo selectLogInfo(LogVo vo);	
	public void updateLogInfo(LogVo vo);	
	public ArrayList<LogVo> getLogList(LogVo vo);
	public ArrayList<LogVo> getActiveLogList(LogVo vo);
	public void deleteLogInfo(LogVo vo);
	public int getTotalLogCount(LogVo vo);
	
}
