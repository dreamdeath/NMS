/**
 * http://usejsdoc.org/
 */
function NMS(){
	function template(libOption, successFunc, errorFunc, paramData){
		var ajaxParam = {
			url : libOption.url,
			type : libOption.type,
			async: libOption.async,
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
			dataType : 'json',
			type: 'GET',
			async : async
		}
	} 
	
	function getSocketServer(libOption, successFunc, errorFunc){
		libOption.url = '/socketServer';
		template(libOption, successFunc, errorFunc);
	}
	
	function getClientSocketServer(libOption, successFunc, errorFunc){
		libOption.url = '/clientSocketServer';
		template(libOption, successFunc, errorFunc);
	}
	
	function updateServerState(libOption, successFunc, errorFunc, paramData){
		libOption.url = '/admin/server/state.do';
		libOption.type = 'PUT';
		template(libOption, successFunc, errorFunc, paramData);
	}
	function getServerDataList(libOption, successFunc, errorFunc){
		libOption.url = '/server/list.do';
		libOption.type = 'GET';
		template(libOption, successFunc, errorFunc);
	}
	function getServerGroupDataList(libOption, successFunc, errorFunc){
		libOption.url = '/group/list.do';
		libOption.type = 'GET';
		template(libOption, successFunc, errorFunc);
	}
	function getNmsActiveLogList(libOption, successFunc, errorFunc){
		libOption.url = '/nmslog/activeList.do';
		libOption.type = 'GET';
		template(libOption, successFunc, errorFunc);
	}
	
	
	function ASYNC(){
		this.async = true;
	}
	ASYNC.prototype.getSocketServer = function(successFunc, errorFunc){
		getSocketServer(getLibOption(this.async), successFunc, errorFunc);
	};
	ASYNC.prototype.getClientSocketServer = function(successFunc, errorFunc){
		getClientSocketServer(getLibOption(this.async), successFunc, errorFunc);
	};
	ASYNC.prototype.updateServerState = function(successFunc, errorFunc, paramData){
		updateServerState(getLibOption(this.async), successFunc, errorFunc, paramData);
	};
	
	function SYNC(){
		this.async = false;
	}
	SYNC.prototype.getSocketServer = function(successFunc, errorFunc){
		getSocketServer(getLibOption(this.async), successFunc, errorFunc);
	};
	SYNC.prototype.getClientSocketServer = function(successFunc, errorFunc){
		getClientSocketServer(getLibOption(this.async), successFunc, errorFunc);
	};
	SYNC.prototype.updateServerState = function(successFunc, errorFunc, paramData){
		updateServerState(getLibOption(this.async), successFunc, errorFunc, paramData);
	};
	SYNC.prototype.getServerDataList = function(successFunc, errorFunc, paramData){
		getServerDataList(getLibOption(this.async), successFunc, errorFunc);
	};
	
	SYNC.prototype.getServerGroupDataList = function(successFunc, errorFunc, paramData){
		getServerGroupDataList(getLibOption(this.async), successFunc, errorFunc, paramData);
	};
	
	SYNC.prototype.getNmsActiveLogList = function(successFunc, errorFunc, paramData){
		getNmsActiveLogList(getLibOption(this.async), successFunc, errorFunc, paramData);
	};
	
	
	
	this.async = new ASYNC();
	this.sync = new SYNC();
}


var nms = new NMS();