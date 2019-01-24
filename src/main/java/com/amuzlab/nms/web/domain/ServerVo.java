package com.amuzlab.nms.web.domain;

import java.io.Serializable;

public class ServerVo implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String server_name; 
	private String server_ip; 
	private String server_mac; 
	private String status; 
	private String update_date; 
	private int grp_id;
	private String server_type;
	private String mode;
	private String server_port;
	private int binding;
	private String group_name;
	private String snmp_status;
	private String failover;
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
	 * @return the upate_date
	 */
	public String getUpdate_date() {
		return update_date;
	}
	/**
	 * @param upate_date the upate_date to set
	 */
	public void setUpdate_date(String update_date) {
		this.update_date = update_date;
	}
	/**
	 * @return the grp_id
	 */
	public int getGrp_id() {
		return grp_id;
	}
	/**
	 * @param grp_id the grp_id to set
	 */
	public void setGrp_id(int grp_id) {
		this.grp_id = grp_id;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/**
	 * @return the server_type
	 */
	public String getServer_type() {
		return server_type;
	}
	/**
	 * @param server_type the server_type to set
	 */
	public void setServer_type(String server_type) {
		this.server_type = server_type;
	}
	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}
	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}
	/**
	 * @return the server_port
	 */
	public String getServer_port() {
		return server_port;
	}
	/**
	 * @param server_port the server_port to set
	 */
	public void setServer_port(String server_port) {
		this.server_port = server_port;
	}
	
	/**
	 * @return the binding
	 */
	public int getBinding() {
		return binding;
	}
	/**
	 * @param binding the binding to set
	 */
	public void setBinding(int binding) {
		this.binding = binding;
	}
	
	
	/**
	 * @return the group_name
	 */
	public String getGroup_name() {
		return group_name;
	}
	/**
	 * @param group_name the group_name to set
	 */
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	
	
	/**
	 * @return the snmp_status
	 */
	public String getSnmp_status() {
		return snmp_status;
	}
	/**
	 * @param snmp_status the snmp_status to set
	 */
	public void setSnmp_status(String snmp_status) {
		this.snmp_status = snmp_status;
	}
	
	/**
	 * @return the failover
	 */
	public String getFailover() {
		return failover;
	}
	/**
	 * @param failover the failover to set
	 */
	public void setFailover(String failover) {
		this.failover = failover;
	}
	@Override
	public String toString() {
		return "ServerVo [id=" + id + ", server_name=" + server_name + ", server_ip=" + server_ip + ", server_mac="
				+ server_mac + ", status=" + status + ", update_date=" + update_date + ", grp_id=" + grp_id
				+ ", server_type=" + server_type + ", mode=" + mode + ", server_port=" + server_port+ ", snmp_status=" + snmp_status +", binding=" + binding + ", server_name=" + server_name+ "]";
	}	
}
