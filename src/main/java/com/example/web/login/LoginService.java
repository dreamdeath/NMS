package com.example.web.login;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.example.web.domain.UserVo;
import com.example.web.sqlmappers.login.LoginMapper;

@Service
public class LoginService {

	@Resource
	private LoginMapper loginMapper;
	
	public UserVo getUser(String userId) {
		UserVo userVo = null;
		if (userId != null && !"".equals(userId)) userVo = loginMapper.getUser(userId);
		return userVo;
	}
}
