/**
 * http://usejsdoc.org/
 */
function EMS(){
	function template(libOption, successFunc, errorFunc, paramData){
		var ajaxParam = {
			url : 'http://' + libOption.ip + ':' + libOption.port + libOption.url,
			type : libOption.type,
			async: libOption.async,
			timeout : libOption.timeout,
			success : successFunc,
			error : errorFunc
		};
		
		if(libOption.contentType){
			ajaxParam.contentType = libOption.contentType;
		}
		
		if(paramData){
			ajaxParam.data = paramData;
		}
		
		$.ajax(ajaxParam);
	}

	function getLibOption(async){                                                                                 
		return {                                                                                                  
			contentType : 'application/json; charset=utf-8',
			dataType : 'json',
			type: 'GET',
			async : async
		}
	} 
	
	function sendNMSSocketServerInfo(libOption, emsInfo, successFunc, errorFunc, paramData){
		libOption.url = '/nms';
		libOption.ip = emsInfo.ip;
		libOption.port = emsInfo.port;
		libOption.type = 'POST';
		template(libOption, successFunc, errorFunc, JSON.stringify(paramData));
	}
	
	function chaneMode(libOption, emsInfo, successFunc, errorFunc, paramData){
		libOption.url = '/mode';
		libOption.ip = emsInfo.ip;
		libOption.port = emsInfo.port;
		libOption.type = 'POST';
		template(libOption, successFunc, errorFunc, JSON.stringify(paramData));
	}
	function getActiveLog(libOption, emsInfo, successFunc, errorFunc, paramData){
		libOption.url = '/activeLog';
		libOption.ip = emsInfo.ip;
		libOption.port = emsInfo.port;
		libOption.type = 'GET';
		libOption.timeout = emsInfo.timeout;
		template(libOption, successFunc, errorFunc, JSON.stringify(paramData));
	}
	
	
	function ASYNC(){
		this.async = true;
	}
	ASYNC.prototype.sendNMSSocketServerInfo = function(emsInfo, successFunc, errorFunc, paramData){
		sendNMSSocketServerInfo(getLibOption(this.async), emsInfo, successFunc, errorFunc, paramData);
	};
	ASYNC.prototype.chaneMode = function(){
		chaneMode(getLibOption(this.async), emsInfo, successFunc, errorFunc, paramData);
	};
	ASYNC.prototype.getActiveLog = function(emsInfo, successFunc, errorFunc, paramData){
		getActiveLog(getLibOption(this.async), emsInfo, successFunc, errorFunc, paramData);
	};
	
	function SYNC(){
		this.async = false;
	}
	
	SYNC.prototype.getActiveLog = function(emsInfo, successFunc, errorFunc, paramData){
		getActiveLog(getLibOption(this.async), emsInfo, successFunc, errorFunc, paramData);
	};
	
	SYNC.prototype.sendNMSSocketServerInfo = function(emsInfo, successFunc, errorFunc, paramData){
		sendNMSSocketServerInfo(getLibOption(this.async), emsInfo, successFunc, errorFunc, paramData);
	};
	SYNC.prototype.chaneMode = function(emsInfo, successFunc, errorFunc, paramData){
		chaneMode(getLibOption(this.async), emsInfo, successFunc, errorFunc, paramData);
	};
	
	this.async = new ASYNC();
	this.sync = new SYNC();
}


var ems = new EMS();