package com.example.web.sqlmappers.log;

import java.util.ArrayList;

import org.springframework.stereotype.Repository;
import com.example.web.domain.LogVo;


@Repository
public interface LogMapper {	
	public void insertLogInfo(LogVo vo);
	public ArrayList<LogVo> getLogList(LogVo vo);
	public void deleteLogInfo(LogVo vo);
}
