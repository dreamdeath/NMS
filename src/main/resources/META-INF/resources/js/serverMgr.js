/**
 * 서버 관리 함수 
 *
 * @author sanghyun moon <shmoon@amuzlab.com>
 * @version 1.0
 * @date 2016-08-07
 */
'use strict';
var svrGrpInfo = {};
var svrGrpMgr = {		
		setBindStatus : function(id, status){
			
			var paramData = {
					id : id,
					binding : (status)? 1 : 0
			}
			
			$.ajax({
				url: "/group/update.do",
				dataType: "json",
				data : paramData,
				async :false,
				type: 'PUT',
				success: function(response) {	       	
					if(response.result) {
						svrGrpInfo[id] = {'bind' : status};
						if(status == 1) checkAllStandbyMode(id);	
						/*
						$.post("/reloadServerList",function(data){
							console.log("바인딩 결과:"+data.result)
	            		});
	            		*/
					}
				}
			});
		},
		getBindStatus : function(id, status){
			return svrGrpInfo[id].bind;
		}
}
var svrMgr = {
		data : {}
};

function checkAllStandbyMode(grpId){
	var standByCount = 0;
	var activeCount = 0;
	var targetStandbyServer = null;
	var targetActiveServer = null;
	var serverList = getServerListByGroup(grpId);
	for(var i=0;i<serverList.length;i++){				
		var data = serverList[i];		
		
		if(data.mode == 0 && data.status == 0) {
			targetActiveServer = data;
			standByCount++;
		}
		
		if(data.mode == 1 && data.status == 0){
			if(targetStandbyServer == null)targetStandbyServer = data;
			
			activeCount++;			
		}

	}
	if(standByCount > 1) {
		if(targetActiveServer != null) {
			sendFailOverInfo(targetActiveServer,"ACTIVE");
			changeServerStatus(targetActiveServer.id);
		}
	} else if(activeCount > 1){
		if(targetStandbyServer != null) {
			sendFailOverInfo(targetStandbyServer,"BACKUP");
			changeServerStatus(targetStandbyServer.id);
		}
	}
}