package com.amuzlab.nms.web.domain;

import java.io.Serializable;

public class ServerGroupVo implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String group_name;
	private String create_date;
	private int binding;
	
	
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
	 * @return the create_date
	 */
	public String getCreate_date() {
		return create_date;
	}
	/**
	 * @param create_date the create_date to set
	 */
	public void setCreate_date(String create_date) {
		this.create_date = create_date;
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
	@Override
	public String toString() {
		return "ServerGroupVo [id=" + id + ", group_name=" + group_name + ", create_date=" + create_date + ", binding="
				+ binding + "]";
	}
	
}
