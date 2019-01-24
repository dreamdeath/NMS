package com.amuzlab.nms.web.domain;

import java.io.Serializable;

public class RoleVo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String roleid;
	private String rolename;
	/**
	 * @return the roleid
	 */
	public String getRoleid() {
		return roleid;
	}
	/**
	 * @param roleid the roleid to set
	 */
	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}
	/**
	 * @return the rolename
	 */
	public String getRolename() {
		return rolename;
	}
	/**
	 * @param rolename the rolename to set
	 */
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

}
