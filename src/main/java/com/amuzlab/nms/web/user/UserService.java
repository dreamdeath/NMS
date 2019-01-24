package com.amuzlab.nms.web.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.amuzlab.nms.web.domain.RoleVo;
import com.amuzlab.nms.web.domain.UserVo;
import com.amuzlab.nms.web.sqlmappers.login.LoginMapper;
import com.amuzlab.nms.web.sqlmappers.user.UserMapper;


@Service
public class UserService {

	@Resource
	private UserMapper userMapper;
	
	public UserVo getUserInfo(UserVo vo) {
		UserVo userVo = null;
		userVo = userMapper.getUserInfo(vo);
		return userVo;
	}
	
	public List<UserVo> getUserInfoList() {
		List<UserVo> resultList = null;
		resultList = userMapper.getUserInfoList();
		return resultList;
	}
	public List<RoleVo> getRoleInfoList() {
		List<RoleVo> resultList = null;
		resultList = userMapper.getRoleInfoList();
		return resultList;
	}
	
	public void insertUserInfo(UserVo vo) {
		boolean result = false;
		
		userMapper.insertUserInfo(vo);
		userMapper.insertUserRoleInfo(vo);		
	}
	
	public void updateUserInfo(UserVo vo) {
		userMapper.updateUserInfo(vo);
		userMapper.updateUserRoleInfo(vo);		
	}
	
	public void deleteUserInfo(UserVo vo) {
		boolean result = false;
		userMapper.deleteUserRoleInfo(vo);
		userMapper.deleteUserInfo(vo);		
	}
}
