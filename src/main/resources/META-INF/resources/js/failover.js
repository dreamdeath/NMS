/**
 * 서버 절체 처리 함수
 *
 * @author sanghyun moon <shmoon@amuzlab.com>
 * @version 1.0
 * @date 2016-08-07
 */
'use strict';
/*
 * 절체 조건
 * 바인딩 ON
 * sysInfo의 오류 채널수 
 * Active로 변경할 서버의 상태 
 * */

var doFailOver = function(svrId,manual){
	
	var svrInfo = getServerDataById(svrId);	
	var failOverSvrInfo;
	
	var grpId = svrInfo.grp_id;

	/*바인딩 여부 체크 */
	var isBinding = svrGrpMgr.getBindStatus(grpId);
	failOverSvrInfo = getFailOverServer(svrInfo,manual);
	console.log("절체 시작==================================");

	var currentServerType = (svrInfo.server_type== "MAIN")?"MAIN":"BACKUP";	
	var currentServerMode = (svrInfo.mode == 0)?"STANDBY":"ACTIVE";	
	var confirmMessage = function(msg){
		if(manual) {
			if(confirmWindow(msg)){
				return true;
			} else {
				return false;
			}
		} else{
			return true;
		}
	}
	
	var resultMessage = function(msg){
		if(manual) {
			alert(msg)
		}
	}
	if(isBinding) {//바인딩 TRUE		
		if(svrInfo.mode == 0 && (svrInfo.status == 1 || svrInfo.snmp_status == 1)) {
			if(manual) {
				if(!confirmWindow("선택한 서버가 오류가 있습니다. 절체를 진행하시겠습니까?")){
					return false;
				}
			};
			//return false;
		}
		if(failOverSvrInfo) {
			if(failOverSvrInfo.result == "SUCCESS") {
				var targetServerType = (failOverSvrInfo.server_type== "MAIN")?"MAIN":"BACKUP";
				var targetServerMode = (parseInt(failOverSvrInfo.mode) == 0)?"STANDBY":"ACTIVE";
				if(confirmMessage("현재 서버그룹이 바인딩되어 있습니다.\n"+ currentServerType+"서버 "+svrInfo.server_name+"을 "+targetServerMode+"상태로 변경하실 경우 "+targetServerType+"서버가 자동으로 "+currentServerMode+" 상태로 변경됩니다.\n계속 진행하시겠습니까?")){
					var result = sendFailOverInfo(svrInfo, targetServerMode);
					if(result) {
						result = sendFailOverInfo(failOverSvrInfo,currentServerMode);
						if(result) {
							resultMessage("정상적으로 상태 변경 요청이 완료 되었습니다.");
							//console.log("=====================")
							//console.log(svrInfo)
							//console.log(failOverSvrInfo)
							//console.log("=====================")
							//changeServerMode(svrInfo.id, failOverSvrInfo.mode);
							//changeServerMode(failOverSvrInfo.id, svrInfo.mode);
							initLayout();
						} else {
							sendFailOverInfo(svrInfo, svrInfo.mode);
						}
					} else {
						resultMessage("상태 변경 중 오류가 발생했습니다.");
					} 
				}
			} else if(failOverSvrInfo.result == "MODEDUP") {				
				var targetServerMode = (parseInt(failOverSvrInfo.mode) == 0)?"ACTIVE":"STANDBY";
				if(confirmMessage("현재 서버그룹이 바인딩되어 있습니다.\n"+ currentServerType+"서버 "+svrInfo.server_name+"을 "+targetServerMode+"상태로 변경하시겠습니까?")){

						result = sendFailOverInfo(svrInfo,targetServerMode);
				}
				
			} else if(failOverSvrInfo.result == "STATUSERR") {
				resultMessage("절체 될 서버에 이상이 있어 작업을 진행 할 수 없습니다.");
			}
			
		} else {
			resultMessage("한대의 서버만 존재하여 절체를 진행할수 없습니다.");
		}
		
	} else {//바인딩 FALSE
		if(manual) {
			/*
			if(svrInfo.mode == 0 && svrInfo.status == 1) {
				alert("선택한 서버가 오류가 있어 ACTIVE상태로 변경 할 수 없습니다.");
				return false;
			}
			*/
			var targetServerMode = (parseInt(svrInfo.mode) == 0)?"ACTIVE":"STANDBY";
			if(confirmWindow(currentServerType+"서버 "+svrInfo.server_name+"을 "+targetServerMode+"상태로 변경하시겠습니까?")){
				var changedModeValue = targetServerMode;
				var result = sendFailOverInfo(svrInfo, targetServerMode);
				if(result) {
					//alert("changedModeValue ==========>"+svrInfo.mode)
					
					//alert("changedModeValue============>"+changedModeValue)
					//changeServerMode(svrInfo.server_id, changedModeValue);
					resultMessage("정상적으로 상태 변경 요청이 완료 되었습니다.");
					initLayout();
				}
			}
		}
		
	}
	
	/*standby인 서버의 상태*/	
	
	
};


var getFailOverServer = function(sInfo,manualType) {
//	return ATE_SVR_LIST.reduce(function(result, server){
//		if(server.grp_id == grpId && server.status == 0) {
//			if(server.mode == mode) {
//				result = {error : 'dupmode'}
//			} else {
//				server.error = "success";
//				result = server;
//			}
//		} 
//		return result;
//	}, null);
	return ATE_SVR_LIST.find(function(server){
		
		if(server.grp_id == sInfo.grp_id && server.id != sInfo.id) {
			if(!manualType) {
				if(server.status == "0" && server.failover == "0") {
					if(server.mode == sInfo.mode ) {
						server.result = "MODEDUP";
						return true;
					} else {
						server.result = "SUCCESS";
						return true;
					}
				} else {
					server.result = "STATUSERR";
					return true;
				}	
			} else {
				server.result = "SUCCESS";
				return true
			}
		} 		
	});
}

var sendFailOverInfo = function(serverInfo,mode) {

		var serverId = serverInfo.id;
		var modeType = (mode == "ACTIVE")?'1':'0';
		var paramData = {
				mode : mode.toLowerCase()
		};
		
		var sendResult = false;

		$.ajax({
 		   url: "http://"+serverInfo.server_ip+":"+serverInfo.server_port+"/mode",
 		   dataType: "json",
 		   data : JSON.stringify(paramData),
 		   contentType: "application/json; charset=utf-8",
 		   type: 'POST',
 		   async: false,
 		   success: function(response) {
 		     if(response.result == "success") {   		   
 		    	
 		    	changeServerMode(serverId, modeType);
 		    	var logData = {
     		    	server_id : serverId,
     		    	message: 'ATE['+serverInfo.server_name+']를 '+mode+'로 변경',
     		    	info: serverInfo.server_ip+":"+serverInfo.server_port,
     		    	log_type: 'I'
     		     }
 		    	serverInfo.mode =	(serverInfo.mode == 0)?1:0;
 		    	sendResult = true;
 		     } else {
 		    	
 		    	//changeServerStatus(element, modeType);
 		    	var logData = {
 	     		    	server_id : serverId,
 	     		    	message: 'ATE['+serverInfo.server_name+'] '+mode+'로 변경 중 오류 발생',
 	     		    	info: serverInfo.server_ip+":"+serverInfo.server_port,
 	     		    	log_type: 'E',
 	     		    	clear_date : (new Date).getFromFormat('yyyy-mm-dd hh:ii:ss')
 	     		     }
 		     }
 		     
 		     saveNmsLog(logData);
 		   },
 		   error : function(XMLHttpRequest, textStatus, errorThrown) {
 		        if (XMLHttpRequest.readyState == 4) {
 		            // HTTP error (can be checked by XMLHttpRequest.status and XMLHttpRequest.statusText)    	

 		        }
 		        else if (XMLHttpRequest.readyState == 0) {
 		            // Network error (i.e. connection refused, access denied due to CORS, etc.)
 		        	alert("ATE 서버("+serverInfo.server_ip+":"+serverInfo.server_port+") 연결에 실패했습니다.\nIP/PORT나 연결상태를  확인 후 다시 시도하세요.");	
 		        }
 		        else {
 		            // something weird is happening 		        	
 		        }
 			       			 
 			  //changeServerStatus(element,modeType);
 			 var logData = {
 	     		    	server_id : serverId,
 	     		    	message: 'ATE['+serverInfo.server_name+']로 '+mode+'로 변경 중 오류 발생',
 	     		    	info: serverInfo.server_ip+":"+serverInfo.server_port,
 	     		    	log_type: 'E',
 	     		    	clear_date : (new Date).getFromFormat('yyyy-mm-dd hh:ii:ss')
 	     	};
     		saveNmsLog(logData);
 		   }
 		});	
		
		return sendResult;
}