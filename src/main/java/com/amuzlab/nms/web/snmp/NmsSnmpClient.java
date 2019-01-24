package com.amuzlab.nms.web.snmp;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.amuzlab.nms.snmp.SimpleSnmpClient;
import com.amuzlab.nms.web.domain.ServerVo;
import com.amuzlab.nms.web.log.LogService;
import com.amuzlab.nms.web.server.ServerService;
import com.amuzlab.nms.web.socketio.SocketServer;
import com.corundumstudio.socketio.SocketIOServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@Component
public class NmsSnmpClient implements ApplicationListener<ApplicationReadyEvent> {
//	public final static int PORT = 9090;

	public static final  Map<String, String> OID_MAP = new LinkedHashMap<String, String>(); //Name to OID
	public static final Map<String, String> OID_NAME_MAP = new LinkedHashMap<String, String>(); //OID to Name
	
	static public List<SnmpClientHandler> snmpThreads;
	
	@Resource
	private ServerService serverService;
	
	@Resource
	private LogService logService;
	
	private ArrayList<ServerVo> serverList;
	
	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		System.out.println("NmsSnmpClient onApplicationEvent");
		snmpThreads = new ArrayList<SnmpClientHandler>();			
		handleConnection();
	}

	public void handleConnection() {
		SimpleSnmpClient snmpClient = null;
		snmpThreads.clear();
		serverList = serverService.getServerList();	
		Map<String,Object> snmpClientMap = new HashMap<String,Object>();		
		SocketServer.initSocketIO();
		SocketIOServer socket = SocketServer.getSocketIOServer();
		String serverIp = "";
		for(int i = 0;i<serverList.size();i++) {
			
			ServerVo serverVo = serverList.get(i);
			serverIp = StringUtils.replace(serverVo.getServer_ip().trim(), " ", "");
			if(!serverIp.equals("")) {
				snmpClient = new SimpleSnmpClient("udp:"+serverVo.getServer_ip()+"/161");			
				snmpClientMap.put("serverId" , String.valueOf(serverVo.getId()));
				snmpClientMap.put("snmpClient" , snmpClient);
				snmpClientMap.put("serverService" , serverService);
				snmpClientMap.put("logService" , logService);
				System.out.println("SNMP START == >"+serverVo.getId());
				SnmpClientHandler snmpThread = new SnmpClientHandler(socket,snmpClientMap);
				snmpThreads.add(snmpThread);
				System.out.println("snmpThreads size :"+snmpThreads.size());
				snmpClientMap.clear();
			}
			
			//snmpClientList.put(String.valueOf(serverVo.getId()),snmpClient);
		}
		//System.out.println("snmp : "+result);				
		
	}
	
	public boolean restartSnmpThread() {
		boolean result = false;
		try {
			for(int i = 0;i < snmpThreads.size();i++) {
				snmpThreads.get(i).shutdown();
				System.out.println("Stop SNMP Thread : ");
			}
			handleConnection();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
		
		
	}

}