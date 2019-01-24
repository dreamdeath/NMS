package com.amuzlab.nms.web.sqlmappers.user;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.amuzlab.nms.web.domain.RoleVo;
import com.amuzlab.nms.web.domain.UserVo;

@Repository
public interface UserMapper {
	public UserVo getUserInfo(UserVo vo);
	public List<UserVo> getUserInfoList();
	public void insertUserInfo(UserVo vo);
	public void insertUserRoleInfo(UserVo vo);
	public void updateUserInfo(UserVo vo);
	public void updateUserRoleInfo(UserVo vo);
	public void deleteUserInfo(UserVo vo);
	public void deleteUserRoleInfo(UserVo vo);
	public List<RoleVo> getRoleInfoList();	
}
