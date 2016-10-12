package com.example.web.domain;

import java.io.Serializable;

public class ServerVo implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String server_name; 
	private String server_ip; 
	private String server_mac; 
	private String status; 
	private String last_upate; 
	private String grp_id;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the server_name
	 */
	public String getServer_name() {
		return server_name;
	}
	/**
	 * @param server_name the server_name to set
	 */
	public void setServer_name(String server_name) {
		this.server_name = server_name;
	}
	/**
	 * @return the server_ip
	 */
	public String getServer_ip() {
		return server_ip;
	}
	/**
	 * @param server_ip the server_ip to set
	 */
	public void setServer_ip(String server_ip) {
		this.server_ip = server_ip;
	}
	/**
	 * @return the server_mac
	 */
	public String getServer_mac() {
		return server_mac;
	}
	/**
	 * @param server_mac the server_mac to set
	 */
	public void setServer_mac(String server_mac) {
		this.server_mac = server_mac;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the last_upate
	 */
	public String getLast_upate() {
		return last_upate;
	}
	/**
	 * @param last_upate the last_upate to set
	 */
	public void setLast_upate(String last_upate) {
		this.last_upate = last_upate;
	}
	/**
	 * @return the grp_id
	 */
	public String getGrp_id() {
		return grp_id;
	}
	/**
	 * @param grp_id the grp_id to set
	 */
	public void setGrp_id(String grp_id) {
		this.grp_id = grp_id;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
	
}
