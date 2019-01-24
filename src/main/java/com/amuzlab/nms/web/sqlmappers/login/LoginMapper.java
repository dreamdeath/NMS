package com.amuzlab.nms.web.sqlmappers.login;

import org.springframework.stereotype.Repository;

import com.amuzlab.nms.web.domain.UserVo;

@Repository
public interface LoginMapper {
	public UserVo getUser(String userId);
}
