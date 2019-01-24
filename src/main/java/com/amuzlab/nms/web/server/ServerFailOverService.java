package com.amuzlab.nms.web.server;

import java.util.List;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import com.amuzlab.nms.utils.HttpUtils;
import com.amuzlab.nms.web.domain.LogVo;
import com.amuzlab.nms.web.domain.ServerVo;
import com.amuzlab.nms.web.log.LogService;
import com.amuzlab.nms.web.tcpsocket.ConnectionHandler;

public class ServerFailOverService {
	
	
	private HttpUtils httpUtils;
	
	private LogVo logVo;	
	
//	private int serverId=0;
//	private JSONObject socketInfo;
	
	private List<ServerVo> serverList;
	
	public boolean doFailOver(ServerService serverService,LogService logService,int serverId,String reqFrom) {
		boolean result = false;    	
//		ServerService serverService = new ServerService();
//		System.out.println("serverService :"+serverService);
		List<ServerVo> serverList = serverService.getServerList();
		logVo = new LogVo();
		httpUtils = new HttpUtils();
		this.serverList = serverList;
		ServerVo svrInfo = getServerInfo(serverId);  	
		ServerVo failOverSvrInfo;


		int grpId = svrInfo.getGrp_id();
		/*바인딩 여부 체크 */
		int isBinding = svrInfo.getBinding();

		failOverSvrInfo = getFailOverServer(svrInfo);
//		System.out.println("절체  조건 판단 시작");
		String originSvrMode = svrInfo.getMode();
		String currentServerType = (svrInfo.getServer_type().equals("MAIN"))?"MAIN":"BACKUP";	    	
		boolean currentServerMode = (originSvrMode.equals("0"))?false:true;	

//		System.out.println("서버 아이디 :"+serverId +"/바인딩 모드:"+isBinding+"/스탠바이 모드:"+originSvrMode);
		if(isBinding == 1) {//바인딩 TRUE		
			if(originSvrMode.equals("0")) {
				System.out.println("STANBY 상태임으로 절체 안함");
				return false;
			}
			if(failOverSvrInfo != null) {
//				String targetServerType = (failOverSvrInfo.getServer_type().equals("MAIN"))?"MAIN":"BACKUP";
				boolean targetServerMode = (failOverSvrInfo.getMode().equals("0"))?false:true;

				//if(confirmMessage("현재 서버그룹이 바인딩되어 있습니다.\n"+ currentServerType+"서버 "+svrInfo.server_name+"을 "+targetServerMode+"상태로 변경하실 경우 "+targetServerType+"서버가 자동으로 "+currentServerMode+" 상태로 변경됩니다.\n계속 진행하시겠습니까?")){
				System.out.println("절체될 서버 시작 "+svrInfo.getId());
				result = sendFailOverInfo(svrInfo, targetServerMode);
				if(result) {
					System.out.println("절체될 서버 시작 "+failOverSvrInfo.getId());
					result = sendFailOverInfo(failOverSvrInfo,currentServerMode);
					if(result) {
						System.out.println("정상적으로 상태 변경 요청이 완료 되었습니다.");
						//console.log("=====================")
						//console.log(svrInfo)
						//console.log(failOverSvrInfo)
						//console.log("=====================");
						if(reqFrom.equals("SYS")) {
							svrInfo.setStatus("1");
						} else {
							svrInfo.setSnmp_status("1");
						}
						svrInfo.setMode(failOverSvrInfo.getMode());						
						failOverSvrInfo.setMode(svrInfo.getMode());
						
						System.out.println("svrInfo "+svrInfo);
						serverService.updateServerInfo(svrInfo);
						serverService.updateServerInfo(failOverSvrInfo);
						//reloadServerList();
						
						logVo.setServer_id(String.valueOf(svrInfo.getId()));
						logVo.setMessage("ATE["+svrInfo.getServer_name()+"]를 STANDBY 모드로 변경");
						logVo.setLog_type("I");						
						logService.insertLogInfo(logVo);
						logVo.setServer_id(String.valueOf(failOverSvrInfo.getId()));
						logVo.setMessage("ATE["+failOverSvrInfo.getServer_name()+"]를 ACTIVE 모드로 변경");
						logService.insertLogInfo(logVo);

					} else {
						sendFailOverInfo(svrInfo, currentServerMode);
					}
				} else {					
					System.out.println("상태 변경 중 오류가 발생했습니다.");
				} 
				
				if(!result) {
					logVo.setServer_id(String.valueOf(svrInfo.getId()));
					logVo.setMessage("ATE["+svrInfo.getServer_name()+"]를 절체중 오류 발생");
					logService.insertLogInfo(logVo);
				}
				//}
			} 

		} else {
			
			System.out.println("바인딩 모드 아님");
		}
		return result;
	}

	private ServerVo getServerInfo(int id) {
		ServerVo result = null;
//		System.out.println("serverService :"+serverService);
		
		for(int i = 0; i<serverList.size();i++) {
			
			ServerVo info = serverList.get(i);
//			System.out.println("getServerInfo ===================>");
//			System.out.println("getServerInfo ====>"+info.getId());
//			System.out.println("getMode ====>"+info.getMode());
//			System.out.println("getStatus ====>"+info.getStatus());
//			System.out.println("getFailover ====>"+info.getFailover());
			if(id == info.getId()) {
				result= info;
				//break;
			}

		}
		return result;
	}
	
	private ServerVo getFailOverServer(ServerVo vo){
		ServerVo failOverServer = null;
		String resultCode = "";
		
		for(int i = 0; i< serverList.size();i++) {
			ServerVo info = serverList.get(i);
			if(vo.getGrp_id() == info.getGrp_id() && vo.getId() != info.getId()) {
				//console.log()
//				if(info.getStatus().equals("1") || info.getSnmp_status().equals("1")) {
//				System.out.println("==================");
//				System.out.println(vo.getId()+"====>"+info.getId());
//				System.out.println("절체 대상 STATUS : "+info.getStatus());
//				System.out.println("절체 대상 FAILOVER : "+info.getFailover());
//				System.out.println("==================");
				if(info.getStatus().equals("1") || info.getFailover().equals("1")) {    
					resultCode = "STATUSERR";
					break;
				} else {
					if(info.getMode().equals("0")) { 
						resultCode = "SUCCESS";
						failOverServer = info;
						break;
					} else {
						resultCode = "MODEDUP";
//						System.out.println("절체 할 대상이 이미 ACTIVE 모드임[MODEDUP]");
						break;
					}
				}

			} 	
		}
		if(resultCode.equals("")) {
			System.out.println("한대의 서버만 존재하여 절체를 진행할수 없습니다.");
		}
		return failOverServer;

	}
	
	private boolean sendFailOverInfo(ServerVo vo, boolean mode){
		
		String result = httpUtils.sendPost("http://"+vo.getServer_ip()+":"+vo.getServer_port()+"/mode", "{\"mode\":"+mode+"}");
		JSONObject resultObject  = new JSONObject(result);
		if(resultObject.get("result").equals("success")) {
			return true;
		}

		return false;
	}
}
