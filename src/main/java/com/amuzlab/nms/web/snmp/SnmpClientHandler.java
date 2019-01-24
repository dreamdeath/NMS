package com.amuzlab.nms.web.snmp;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.snmp4j.smi.OID;

import com.amuzlab.nms.snmp.SimpleSnmpClient;
import com.amuzlab.nms.web.domain.LogVo;
import com.amuzlab.nms.web.domain.ServerVo;
import com.amuzlab.nms.web.log.LogService;
import com.amuzlab.nms.web.server.ServerFailOverService;
import com.amuzlab.nms.web.server.ServerService;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.Gson;

public class SnmpClientHandler implements Runnable {

	public final static String CPU_IDLE 	=	".1.3.6.1.4.1.2021.11.11.0";
	public final static String TOTAL_MEM 	=  	".1.3.6.1.4.1.2021.4.5.0";
	public final static String AVAIL_MEM 	=	".1.3.6.1.4.1.2021.4.6.0";
	public final static String BUFFER_MEM 	=   ".1.3.6.1.4.1.2021.4.14.0";
	public final static String CACHED_MEM 	=   ".1.3.6.1.4.1.2021.4.15.0";
	public final static String USED_DISK 	=  	".1.3.6.1.4.1.2021.9.1.9.1";

	public final static String IF_DESC 			= 	".1.3.6.1.2.1.2.2.1.2";	
	public final static String IF_OPER_STATUS	= 	".1.3.6.1.2.1.2.2.1.8";
	public final static String IF_IN_OCTECTS	=   ".1.3.6.1.2.1.2.2.1.10.15";
	public final static String IF_OUT_OCTECTS 	=  	".1.3.6.1.2.1.2.2.1.16.14";	
	public final static String IF_MNG_OCTECTS 	=  	".1.3.6.1.2.1.2.2.1.10.16";

//		public final static String IF_IN_OCTECTS	=   ".1.3.6.1.2.1.2.2.1.10.2";
//		public final static String IF_OUT_OCTECTS 	=  	".1.3.6.1.2.1.2.2.1.10.1";	
//		public final static String IF_MNG_OCTECTS 	=  	".1.3.6.1.2.1.2.2.1.10.3";

	private SocketIOServer socketIOServer;
	private boolean mThreadTimer = false;
	private final int INTERVAL_THREAD = 3;
	private Map<String,Object> snmpClientMap;
	private SimpleSnmpClient snmpClient;
	private ServerService serverService;
	private LogService logService;
	private String serverId ;
	private Double prevInOctets = - 1.0;	
	private Double prevOutOctets = - 1.0;
	private Double prevMngOctets = - 1.0;

	private final int MIN_FAILOVER_INPUT_COUNT = 3;
	private final int MIN_FAILOVER_OUTPUT_COUNT = 2;
	private final int MIN_FAILOVER_MANAGE_COUNT = 2;


	private JSONObject nicListByBonding;
	private Double OFFSET_MAX_PACKET = 4294967295.0;	

	private ServerFailOverService failOverController;

	public SnmpClientHandler(SocketIOServer socketServer,Map<String,Object> snmpClientMaps) {
		this.socketIOServer = socketServer;   
		this.snmpClientMap = snmpClientMaps;
		this.serverId = (String) snmpClientMap.get("serverId");
		this.snmpClient = (SimpleSnmpClient) snmpClientMap.get("snmpClient");	
		this.serverService = (ServerService) snmpClientMap.get("serverService");	
		this.logService = (LogService) snmpClientMap.get("logService");	
		nicListByBonding = new JSONObject();
		int[] nicInputArray = new int[] {5,6,7,8};
		int[] nicOutputArray = new int[] {9,10};
		int[] nicManageArray = new int[] {1,2};

//				int[] nicInputArray = new int[] {0,1,0,1};
//				int[] nicOutputArray = new int[] {0,1};
//				int[] nicManageArray = new int[] {0,1};

		nicListByBonding.put("nicInput",nicInputArray );
		nicListByBonding.put("nicOutput",nicOutputArray );
		nicListByBonding.put("nicManage",nicManageArray );

		failOverController = new ServerFailOverService();

		Thread t = new Thread(this);
		t.start();
	}

	public void run() {
		mThreadTimer = true;

		int usedCpu = 0;
		int diskUsage = 0;
		float totalMem = 0;
		float availableMem = 0;
		float bufferMem = 0;
		float cachedMem = 0;
		int memUsage = 0;

		double inputOcts = 0;
		double outputOcts = 0;
		double mngOcts = 0;

		float calsInOctets = 0;
		float calsOutOctets = 0;
		float calsMngOctets = 0;

		int[] prevSnmpInputStatus = new int[] {1,1,1,1};
		int[] prevSnmpOutputStatus = new int[] {1,1};
		int[] prevSnmpManageStatus = new int[] {1,1};


		String curSnmpStatus = "";
		String prevSnmpStatus = "";
		List<String> ifDescription = null;
		List<String> ifOperStatus = null;

		List<String> prevIfOperStatus = null;

		Gson gson = new Gson(); 

		//소수점 두번째 자리까기 표현하기 위한 설정
		DecimalFormat format = new DecimalFormat(".#");

		String nicLogStr= "";
		LogVo logVo = new LogVo();
		logVo.setServer_id(serverId);
		Map<String,Object> resultSysInfoMap = new HashMap<String,Object>();
		Map<String,Object> delNicLogMap = new HashMap<String,Object>();
		List<Map<String,Object>> delNicLogDatas = new ArrayList<Map<String,Object>>();

		SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ServerVo vo = new ServerVo();
		vo.setId(Integer.valueOf(serverId));
		ServerVo serverInfo = serverService.getServerInfo(vo);
		String connectionStatus = "0";
		String preConnectionStatus = serverInfo.getSnmp_status();
		int tmpCount = 0;
		boolean prevSnmpFailoverStatus = true;
		while(mThreadTimer){       	
			//for( String key : snmpClientList.keySet() ){



			String jsonResult = "";
			try {

//				System.out.println("snmpClient 데이타 풀링 :"+snmpClient);
				String usageCpu = snmpClient.getAsString(getOID(CPU_IDLE));
//				System.out.println("usedCpu :"+usageCpu);
				if(usageCpu.equals("") || usageCpu == null) {
					//System.out.println("usedCpu :"+usageCpu);
					connectionStatus = "1";
				} else {
					connectionStatus = "0";
				}				

//				
//				tmpCount++;
				resultSysInfoMap.clear();
				
				if(connectionStatus.equals("0")) {
					try {
					usedCpu = 100 - Integer.valueOf(usageCpu);

					diskUsage = Integer.valueOf(snmpClient.getAsString(getOID(USED_DISK)));


					totalMem = Integer.valueOf(snmpClient.getAsString(getOID(TOTAL_MEM)));
					availableMem = Integer.valueOf(snmpClient.getAsString(getOID(AVAIL_MEM)));
					bufferMem = Integer.valueOf(snmpClient.getAsString(getOID(BUFFER_MEM)));
					cachedMem = Integer.valueOf(snmpClient.getAsString(getOID(CACHED_MEM)));


					inputOcts = Long.valueOf(snmpClient.getAsString(getOID(IF_IN_OCTECTS)));
					outputOcts = Long.valueOf(snmpClient.getAsString(getOID(IF_OUT_OCTECTS)));
					mngOcts = Long.valueOf(snmpClient.getAsString(getOID(IF_MNG_OCTECTS)));


					float tmpMemUsage = (totalMem - (availableMem+bufferMem+cachedMem)) / totalMem;

					memUsage = (int) Math.round(tmpMemUsage * 100);
					//            	System.out.println("totalMem - (availableMem+bufferMem+cachedMem) : "+(totalMem - (availableMem+bufferMem+cachedMem)));
					//            	System.out.println("totalMem - (availableMem+bufferMem+cachedMem) /totalMem : "+((totalMem - (availableMem+bufferMem+cachedMem))/ totalMem));
					//            	System.out.println("totalMem - (availableMem+bufferMem+cachedMem) /totalMem * 100: "+(((totalMem - (availableMem+bufferMem+cachedMem)) / totalMem) * 100));
					ifDescription = snmpClient.getSubTreeData("STR", getOID(IF_DESC));
					ifOperStatus = snmpClient.getSubTreeData("INT",getOID(IF_OPER_STATUS));


					double tmpInputOcts = new Double(inputOcts);
					if(prevInOctets >= 0){
						if(tmpInputOcts - prevInOctets < 0) {
							System.out.println("이전 입력값보다 작은 경우");
							tmpInputOcts += OFFSET_MAX_PACKET;
						}

						//						prevInOctets = prevOctects.get("inputOcts"+serverId);
						//						prevOutOctets = prevOctects.get("outputOcts"+serverId);
						//						prevMngOctets = prevOctects.get("mngOcts"+serverId);

						calsInOctets = (float) ((((tmpInputOcts - prevInOctets) * 8) / INTERVAL_THREAD) / 1024 / 1024);
						calsOutOctets = (float) ((((outputOcts - prevOutOctets) * 8) / INTERVAL_THREAD) / 1024 / 1024);
						calsMngOctets = (float) ((((mngOcts - prevMngOctets) * 8) / INTERVAL_THREAD) / 1024 / 1024);
						//						System.out.println("outputOcts : "+outputOcts);
						//						System.out.println("prevOutOctets : "+prevOutOctets);
						//						System.out.println("((outputOcts - prevOutOctets) * 8) : "+((outputOcts - prevOutOctets) * 8));

						//						System.out.println("outputOcts : "+format.format(outputOcts)+":"+format.format(prevOutOctets)+":"+format.format(calsOutOctets));
					} else {
						calsInOctets = 0;
						calsOutOctets = 0;
						calsMngOctets = 0;
					}



					//            	System.out.println("usedCpu : "+usedCpu);
					//            	System.out.println("totalMem : "+totalMem);
					//            	System.out.println("availableMem : "+availableMem);
					//            	System.out.println("bufferMem : "+bufferMem);
					//            	System.out.println("cachedMem : "+cachedMem);
					//            	System.out.println("memUsage : "+memUsage);
					//            	System.out.println("diskUsage : "+diskUsage);
					//					System.out.println("SERVER_ID : "+serverId+"================================");
					//					System.out.println("inputOcts : "+format.format(inputOcts)+":"+format.format(tmpInputOcts)+":"+format.format(prevInOctets)+":"+format.format(calsInOctets));
					//					System.out.println("outputOcts : "+format.format(outputOcts)+":"+format.format(prevOutOctets)+":"+format.format(calsOutOctets));
					//					System.out.println("calsMngOctets : "+format.format(mngOcts)+":"+format.format(prevMngOctets)+":"+format.format(calsMngOctets));
					//            	System.out.println("ifDescription : "+ifDescription.size());
					//            	System.out.println("ifOperStatus : "+ifOperStatus.size());
					//            	System.out.println("============================================");

					prevInOctets = inputOcts;
					prevOutOctets = outputOcts;
					prevMngOctets = mngOcts;



					//입력 인터페이스 4개중 3개 이상 다운이면 절체 시작
					int downInputNicCount = 0;
					int downOutputNicCount = 0;
					int downManageNicCount = 0;

					int[] nicInputDatas = (int[]) nicListByBonding.get("nicInput");
					int[] nicOutputDatas = (int[]) nicListByBonding.get("nicOutput");
					int[] nicManageDatas = (int[]) nicListByBonding.get("nicManage");
					int statusValue = 0;
					int prevStatusValue = 0;

					//		    		System.out.println("nicDatas.length :"+nicInputDatas.length);
					//		    		System.out.println("ifOperStatus :"+ifOperStatus.toString());		    		
					boolean snmpFailoverStatus = true;					
					for(int i = 0;i < nicInputDatas.length;i++) {	
						try {
							statusValue = Integer.parseInt(ifOperStatus.get(nicInputDatas[i]));
							prevStatusValue = prevSnmpInputStatus[i];		
//							if(tmpCount > 5 && serverId.equals("64")) statusValue = 2;
//							if(tmpCount > 10 && serverId.equals("64")) statusValue = 1;
							if(statusValue != prevStatusValue) {								
								if(statusValue == 2) {				    			
//									downInputNicCount++;

									//					    			nicLogStr = "{id :\"E600_"+serverId+"_input_"+i+"\", type : \"sysInfo\", server_id: "+serverId+",action :\"ADD\",message : \"INPUT 인터페이스 "+(i+1)+"번째 ["+ifDescription.get(nicInputDatas[i]).toString()+"]에 문제가 발생했습니다.\""+"}";

									resultSysInfoMap.clear();
									resultSysInfoMap.put("id", "E600_"+serverId+"_input_"+i);
									resultSysInfoMap.put("type", "ateLog");
									resultSysInfoMap.put("code", "E600");
									resultSysInfoMap.put("log_type", "E");
									resultSysInfoMap.put("server_name", "");
									Date today = Calendar.getInstance().getTime();					    			
									String create_date = logDateFormat.format(today);
									resultSysInfoMap.put("create_date", create_date);
									resultSysInfoMap.put("server_id", serverId);
									resultSysInfoMap.put("action", "ADD");
									resultSysInfoMap.put("info", serverInfo.getServer_ip()+":"+serverInfo.getServer_port());
									resultSysInfoMap.put("message", "INPUT 인터페이스 "+(i+1)+"번째 ["+ifDescription.get(nicInputDatas[i]).toString()+"]에 문제가 발생했습니다.");

									jsonResult = gson.toJson(resultSysInfoMap);

									//					    			
									socketIOServer.getBroadcastOperations().sendEvent("ateLog", jsonResult);
									logVo = new LogVo();
									logVo.setLog_type("E");
									logVo.setCode("E600");
									logVo.setServer_id(serverId);
									logVo.setRef_id("E600_"+serverId+"_input_"+i);									
									logVo.setMessage("INPUT 인터페이스 "+(i+1)+"번째 ["+ifDescription.get(nicInputDatas[i]).replace("\"","")+"]에 문제가 발생했습니다.");
									logService.insertLogInfo(logVo);


								} else {
									//nicLogStr = "{\"action\" :\"DEL\", \"data\": [{\"id\" :\"E600_"+serverId+"_input_"+i+"\", \"code\" : \"\"}]}";
									//							    	nicLogStr = "{id :\"if_"+serverId+"_input_"+i+"\", type : \"sysInfo\", server_id: "+serverId+",action :\"DEL\",message : \"INPUT 인터페이스 "+(i+1)+"번째 ["+ifDescription.get(nicInputDatas[i]).toString()+"]가 정상적으로 복구 되었습니다.\""+"}";
									delNicLogMap.clear();
									delNicLogMap.put("id", "E600_"+serverId+"_input_"+i);
									delNicLogDatas.clear();
									delNicLogDatas.add(delNicLogMap);
									resultSysInfoMap.clear();
									resultSysInfoMap.put("action", "DEL");
									resultSysInfoMap.put("server_id", serverId);
									resultSysInfoMap.put("data", delNicLogDatas);
									jsonResult = gson.toJson(resultSysInfoMap);
									socketIOServer.getBroadcastOperations().sendEvent("ateLog", jsonResult);
									
									logVo = new LogVo();
									logVo.setRef_id("E600_"+serverId+"_input_"+i);	
									logService.updateLogInfo(logVo);
								}
							}
							prevSnmpInputStatus[i] = statusValue;
						}catch(Exception e) {
							System.out.println("네트웍 카드 상태 조회중 오류 발생");
						}
						if(statusValue == 2) downInputNicCount++;						
					}					
					if(downInputNicCount >= MIN_FAILOVER_INPUT_COUNT)  snmpFailoverStatus = false;
//					tmpCount++;
					for(int i = 0;i < nicOutputDatas.length;i++) {	
						try {
							//		    				System.out.println("ifOperStatus.get(nicOutputDatas[i]) :"+ifOperStatus.get(i));
							statusValue = Integer.valueOf(ifOperStatus.get(nicOutputDatas[i]));
							prevStatusValue = prevSnmpOutputStatus[i];							
							
							if(statusValue != prevStatusValue) {
								if(statusValue == 2) {
//									downOutputNicCount++;
									resultSysInfoMap.clear();
									resultSysInfoMap.put("id", "E600_"+serverId+"_output_"+i);
									resultSysInfoMap.put("type", "ateLog");
									resultSysInfoMap.put("code", "E600");
									resultSysInfoMap.put("log_type", "E");
									resultSysInfoMap.put("server_name", "");
									Date today = Calendar.getInstance().getTime();					    			
									String create_date = logDateFormat.format(today);
									resultSysInfoMap.put("create_date", create_date);
									resultSysInfoMap.put("server_id", serverId);
									resultSysInfoMap.put("action", "ADD");
									resultSysInfoMap.put("info", serverInfo.getServer_ip()+":"+serverInfo.getServer_port());
									resultSysInfoMap.put("message", "OUTPUT 인터페이스 "+(i+1)+"번째 ["+ifDescription.get(nicOutputDatas[i]).toString()+"]에 문제가 발생했습니다.");

									jsonResult = gson.toJson(resultSysInfoMap);
									//				    			nicLogStr = "{\"id\" :\"E600_"+serverId+"_output_"+i+"\", type : \"sysInfo\", server_id: "+serverId+", action :\"ADD\",message : \"OUTPUT 인터페이스 "+(i+1)+"번째 ["+ifDescription.get(nicInputDatas[i]).replace("\"","")+"]에 문제가 발생했습니다.\"}";
									socketIOServer.getBroadcastOperations().sendEvent("ateLog", jsonResult);
									logVo = new LogVo();
									logVo.setLog_type("E");
									logVo.setCode("E600");
									logVo.setServer_id(serverId);
									logVo.setRef_id("E600_"+serverId+"_output_"+i);
									logVo.setMessage("OUTPUT 인터페이스 "+(i+1)+"번째 ["+ifDescription.get(nicOutputDatas[i]).replace("\"","")+"]에 문제가 발생했습니다.");
									logService.insertLogInfo(logVo);

								} else {
									//						    	System.out.println("ifDescription.get(nicInputDatas[i] :"+ifDescription.get(nicInputDatas[i]));
									//						    	nicLogStr = "{'id':jsonData.id,'server_id':jsonData.server_id, 'code' : jsonData.code, 'create_date' : jsonData.create_date, 'log_type':jsonData.log_type,'message':jsonData.message,'info':message,'sid':jsonData.sid,'channelName':jsonData.channelName,'serverName': serverName}
									//						    	nicLogStr = "{\"action\" :\"DEL\", \"data\": [{\"id\" :\"E600_"+serverId+"_output_"+i+"\", \"code\" : \"\"}]}";
									delNicLogMap.clear();
									delNicLogMap.put("id", "E600_"+serverId+"_output_"+i);
									delNicLogDatas.clear();
									delNicLogDatas.add(delNicLogMap);
									resultSysInfoMap.clear();
									resultSysInfoMap.put("action", "DEL");
									resultSysInfoMap.put("server_id", serverId);
									resultSysInfoMap.put("data", delNicLogDatas);
									jsonResult = gson.toJson(resultSysInfoMap);			
									
									socketIOServer.getBroadcastOperations().sendEvent("ateLog", jsonResult);
									logVo = new LogVo();
									logVo.setRef_id("E600_"+serverId+"_output_"+i);
									logService.updateLogInfo(logVo);
								}
							}

							prevSnmpOutputStatus[i] = statusValue;
						}catch(Exception e) {
							e.printStackTrace();
							System.out.println("네트웍 카드 상태 조회중 오류 발생");
						}
						
						if(statusValue == 2) downOutputNicCount++;
						
					}
					tmpCount++;
					if(downOutputNicCount >= MIN_FAILOVER_OUTPUT_COUNT)  snmpFailoverStatus = false;
					
					for(int i = 0;i < nicManageDatas.length;i++) {	
						try {
							statusValue = Integer.parseInt(ifOperStatus.get(nicManageDatas[i]));
							prevStatusValue = prevSnmpManageStatus[i];
							
							if(statusValue != prevStatusValue) {
								if(statusValue == 2) {
//									downManageNicCount++;
									//				    			nicLogStr = "{\"id\" :\"E600_"+serverId+"_manage_"+i+"\", type : \"sysInfo\", server_id: "+serverId+", action :\"ADD\",message : \"MANAGE 인터페이스 "+(i+1)+"번째 ["+ifDescription.get(nicInputDatas[i]).replace("\"","")+"]에 문제가 발생했습니다.\"}";
									resultSysInfoMap.clear();
									resultSysInfoMap.put("id", "E600_"+serverId+"_manage_"+i);
									resultSysInfoMap.put("type", "ateLog");
									resultSysInfoMap.put("code", "E600");
									resultSysInfoMap.put("log_type", "E");
									resultSysInfoMap.put("server_name", "");
									Date today = Calendar.getInstance().getTime();					    			
									String create_date = logDateFormat.format(today);
									resultSysInfoMap.put("create_date", create_date);
									resultSysInfoMap.put("server_id", serverId);
									resultSysInfoMap.put("action", "ADD");
									resultSysInfoMap.put("info", serverInfo.getServer_ip()+":"+serverInfo.getServer_port());
									resultSysInfoMap.put("message", "MANAGE 인터페이스 "+(i+1)+"번째 ["+ifDescription.get(nicManageDatas[i]).toString()+"]에 문제가 발생했습니다.");
									
									jsonResult = gson.toJson(resultSysInfoMap);
									socketIOServer.getBroadcastOperations().sendEvent("ateLog", jsonResult);
									logVo = new LogVo();
									logVo.setLog_type("E");
									logVo.setCode("E600");
									logVo.setServer_id(serverId);
									logVo.setRef_id("E600_"+serverId+"_manage_"+i);
									logVo.setMessage("MANAGE 인터페이스 "+(i+1)+"번째 ["+ifDescription.get(nicManageDatas[i]).replace("\"","")+"]에 문제가 발생했습니다.");
									logService.insertLogInfo(logVo);

								} else {
									//						    	System.out.println("ifDescription.get(nicInputDatas[i] :"+ifDescription.get(nicInputDatas[i]));
									//						    	nicLogStr = "{'id':jsonData.id,'server_id':jsonData.server_id, 'code' : jsonData.code, 'create_date' : jsonData.create_date, 'log_type':jsonData.log_type,'message':jsonData.message,'info':message,'sid':jsonData.sid,'channelName':jsonData.channelName,'serverName': serverName}
									//						    	nicLogStr = "{\"action\" :\"DEL\", \"data\": [{\"id\" :\"E600_"+serverId+"_manage_"+i+"\", \"code\" : \"\"}]}";
									delNicLogMap.clear();
									delNicLogMap.put("id", "E600_"+serverId+"_manage_"+i);
									delNicLogDatas.clear();
									delNicLogDatas.add(delNicLogMap);
									resultSysInfoMap.clear();
									resultSysInfoMap.put("action", "DEL");
									resultSysInfoMap.put("server_id", serverId);
									resultSysInfoMap.put("data", delNicLogDatas);
									jsonResult = gson.toJson(resultSysInfoMap);
									
									socketIOServer.getBroadcastOperations().sendEvent("ateLog", jsonResult);
									logVo = new LogVo();
									logVo.setRef_id("E600_"+serverId+"_manage_"+i);
									logService.updateLogInfo(logVo);
								}
							}
							
							prevSnmpManageStatus[i] = statusValue;
						}catch(Exception e) {
							System.out.println("네트웍 카드 상태 조회중 오류 발생");
						}
						
						if(statusValue == 2) downManageNicCount++;
					}
					
					if(downOutputNicCount >= MIN_FAILOVER_OUTPUT_COUNT)  snmpFailoverStatus = false;
					
					int totalSnmpError= downInputNicCount+downOutputNicCount+downManageNicCount;

					if(totalSnmpError > 0) {
						curSnmpStatus = "1";			    		
					} else {
						curSnmpStatus = "0";
					}

					resultSysInfoMap.clear();
					
					resultSysInfoMap.put("memUsage", String.valueOf(memUsage));
					resultSysInfoMap.put("cpuUsage", String.valueOf(usedCpu));
					resultSysInfoMap.put("diskUsage", String.valueOf(diskUsage));
					resultSysInfoMap.put("memUsage", String.valueOf(memUsage));
					resultSysInfoMap.put("inputOcts", Double.parseDouble(String.format("%.1f",calsInOctets)));
					resultSysInfoMap.put("outputOcts", Double.parseDouble(String.format("%.1f",calsOutOctets)));
					resultSysInfoMap.put("mngOctets", Double.parseDouble(String.format("%.1f",calsMngOctets)));
					resultSysInfoMap.put("nicList", ifDescription.toString());
					resultSysInfoMap.put("nicStatus", String.valueOf(ifOperStatus));
					

//					jsonResult = gson.toJson(resultSysInfoMap);
//
//					socketIOServer.getBroadcastOperations().sendEvent("snmpInfo", jsonResult);
					
					System.out.println("server id=>"+ serverId);
					System.out.println("downInputNicCount=>"+ downInputNicCount);
					System.out.println("downOutputNicCount=>"+ downOutputNicCount);
					System.out.println("downManageNicCount=>"+ downManageNicCount);
					System.out.println("curSnmpStatus=>"+ curSnmpStatus);
					System.out.println("prevSnmpStatus=>"+ prevSnmpStatus);
					System.out.println("======================================");
					
					
					if(snmpFailoverStatus != prevSnmpFailoverStatus) {	
						vo = new ServerVo();
						vo.setId(Integer.valueOf(serverId));
						vo.setFailover((snmpFailoverStatus)?"0":"1");
						serverService.updateServerState(vo);
						
						
					}
					
					if(downInputNicCount >= MIN_FAILOVER_INPUT_COUNT  || downOutputNicCount >= MIN_FAILOVER_OUTPUT_COUNT || downManageNicCount >= MIN_FAILOVER_MANAGE_COUNT) {
						failOverController.doFailOver(serverService, logService, Integer.valueOf(serverId),"SNMP");
						//			    		failOverController.doFailOver(serverId,false);
					}
					
					prevSnmpFailoverStatus = snmpFailoverStatus;
					if(!curSnmpStatus.equals(prevSnmpStatus)) {
						System.out.println( "SNMP_STATUS 저장 : "+ serverId+"/"+curSnmpStatus);
						vo = new ServerVo();
						vo.setId(Integer.valueOf(serverId));
						vo.setSnmp_status(curSnmpStatus);		
						serverService.updateServerState(vo);
					}
					
					prevSnmpStatus = curSnmpStatus;
					prevIfOperStatus = ifOperStatus;
					
					resultSysInfoMap.put("status", curSnmpStatus);					
				} catch (NumberFormatException e){
					
					resultSysInfoMap.put("status", (curSnmpStatus.equals("1"))?curSnmpStatus:"0");
					System.out.println("[오류 ]SNMP 정보 비정상 : SNMP OID 확인 필요");
				} 
				} else {
					
					resultSysInfoMap.put("status", connectionStatus);
				}
				
				resultSysInfoMap.put("server_id", serverId);
				jsonResult = gson.toJson(resultSysInfoMap);
				socketIOServer.getBroadcastOperations().sendEvent("snmpInfo", jsonResult);
					//네트웍 단절 시 
					if(preConnectionStatus != connectionStatus) {
						if(connectionStatus.equals("1")) {
							resultSysInfoMap.clear();
							resultSysInfoMap.put("action", "ADD");
							resultSysInfoMap.put("id", "E700_"+serverId+"_connect");
							resultSysInfoMap.put("server_id", serverId);
							resultSysInfoMap.put("type", "conStatus");					
							resultSysInfoMap.put("status", connectionStatus);
							resultSysInfoMap.put("code", "E700");
							resultSysInfoMap.put("log_type", "E");
							resultSysInfoMap.put("server_name", "");
							resultSysInfoMap.put("message", "네트웍 연결에 문제가 있어 서버와의 연결에 실패하였습니다.");
							resultSysInfoMap.put("info", serverInfo.getServer_ip()+":"+serverInfo.getServer_port());
							Date today = Calendar.getInstance().getTime();					    			
							String create_date = logDateFormat.format(today);
							resultSysInfoMap.put("create_date", create_date);
							logVo = new LogVo();
							logVo.setLog_type("E");
							logVo.setCode("E700");
							logVo.setServer_id(serverId);
							logVo.setRef_id("E700_"+serverId+"_connect");
							logVo.setMessage("네트워 연결에 문제가 있어 서버와의 연결에 실패하였습니다.");
							logService.insertLogInfo(logVo);
							
						} else {
							resultSysInfoMap.clear();
							resultSysInfoMap.put("action", "DEL");
							resultSysInfoMap.put("id", "E700_"+serverId+"_connect");
							resultSysInfoMap.put("type", "conStatus");
							resultSysInfoMap.put("status", (curSnmpStatus.equals("0"))?connectionStatus:curSnmpStatus);
							resultSysInfoMap.put("server_id", serverId);
							resultSysInfoMap.put("code", "E700");
							
							logVo = new LogVo();
							logVo.setRef_id( "E700_"+serverId+"_connect");
							logService.updateLogInfo(logVo);
						}
						
						jsonResult = gson.toJson(resultSysInfoMap);						
						socketIOServer.getBroadcastOperations().sendEvent("conStatus", jsonResult);

//							System.out.println(" SNMP 연결 실패 상태 값 저장 :"+connectionStatus +":"+ preConnectionStatus);
							vo = new ServerVo();
							vo.setId(Integer.valueOf(serverId));
							vo.setSnmp_status(connectionStatus);
							vo.setFailover("0");
							serverService.updateServerState(vo);
	
						
					}
	
				preConnectionStatus = connectionStatus;
//				System.out.println("preConnectionStatus :"+preConnectionStatus);
//				System.out.println("connectionStatus :"+connectionStatus);
				
				Thread.sleep(INTERVAL_THREAD * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//System.out.println("Error:"+e.getStackTrace());
				e.printStackTrace();
			} // sleeps thread
		}
	}
	public void shutdown() {
		mThreadTimer = false;
	}
	private OID getOID(String oidStr){
		return new OID(oidStr);
	}
}