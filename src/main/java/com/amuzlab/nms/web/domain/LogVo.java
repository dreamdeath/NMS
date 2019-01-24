package com.amuzlab.nms.web.domain;

import java.io.Serializable;

public class LogVo implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String ref_id;
	private String server_id;
	private String server_name;
	private String info; 
	private String message; 
	private String create_date;
	private String clear_date;	
	private String log_type;
	private String search_start;
	private String search_end;
	private int pagenum;
	private int pagesize;
	private int recordstartindex;
	private int recordendindex;
	private String sortdatafield;
	private String sortorder;
	private String code;
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
	 * @return the ref_id
	 */
	public String getRef_id() {
		return ref_id;
	}
	/**
	 * @param ref_id the ref_id to set
	 */
	public void setRef_id(String ref_id) {
		this.ref_id = ref_id;
	}
	/**
	 * @return the server_id
	 */
	public String getServer_id() {
		return server_id;
	}
	/**
	 * @param server_id the server_id to set
	 */
	public void setServer_id(String server_id) {
		this.server_id = server_id;
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
	 * @return the info
	 */
	public String getInfo() {
		return info;
	}
	/**
	 * @param info the info to set
	 */
	public void setInfo(String info) {
		this.info = info;
	}	
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
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
	 * @return the clear_date
	 */
	public String getClear_date() {
		return clear_date;
	}
	/**
	 * @param clear_date the clear_date to set
	 */
	public void setClear_date(String clear_date) {
		this.clear_date = clear_date;
	}
	/**
	 * @return the log_type
	 */
	public String getLog_type() {
		return log_type;
	}
	/**
	 * @param log_type the log_type to set
	 */
	public void setLog_type(String log_type) {
		this.log_type = log_type;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/**
	 * @return the search_start
	 */
	public String getSearch_start() {
		return search_start;
	}
	/**
	 * @param search_start the search_start to set
	 */
	public void setSearch_start(String search_start) {
		this.search_start = search_start;
	}
	/**
	 * @return the search_end
	 */
	public String getSearch_end() {
		return search_end;
	}
	/**
	 * @param search_end the search_end to set
	 */
	public void setSearch_end(String search_end) {
		this.search_end = search_end;
	}
	/**
	 * @return the pagenum
	 */
	public int getPagenum() {
		return pagenum;
	}
	/**
	 * @param pagenum the pagenum to set
	 */
	public void setPagenum(int pagenum) {
		this.pagenum = pagenum;
	}
	/**
	 * @return the pagesize
	 */
	public int getPagesize() {
		return pagesize;
	}
	/**
	 * @param pagesize the pagesize to set
	 */
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	/**
	 * @return the recordstartindex
	 */
	public int getRecordstartindex() {
		return recordstartindex;
	}
	/**
	 * @param recordstartindex the recordstartindex to set
	 */
	public void setRecordstartindex(int recordstartindex) {
		this.recordstartindex = recordstartindex;
	}
	/**
	 * @return the recordendindex
	 */
	public int getRecordendindex() {
		return recordendindex;
	}
	/**
	 * @param recordendindex the recordendindex to set
	 */
	public void setRecordendindex(int recordendindex) {
		this.recordendindex = recordendindex;
	}
	/**
	 * @return the sortdatafield
	 */
	public String getSortdatafield() {
		return sortdatafield;
	}
	/**
	 * @param sortdatafield the sortdatafield to set
	 */
	public void setSortdatafield(String sortdatafield) {
		this.sortdatafield = sortdatafield;
	}
	/**
	 * @return the sortorder
	 */
	public String getSortorder() {
		return sortorder;
	}
	/**
	 * @param sortorder the sortorder to set
	 */
	public void setSortorder(String sortorder) {
		this.sortorder = sortorder;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}	
}
