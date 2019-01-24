package com.amuzlab.nms.web.tcpsocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.amuzlab.nms.utils.HttpUtils;
import com.amuzlab.nms.web.domain.LogVo;
import com.amuzlab.nms.web.domain.ServerVo;
import com.amuzlab.nms.web.log.LogService;
import com.amuzlab.nms.web.server.ServerFailOverService;
import com.amuzlab.nms.web.server.ServerService;
import com.corundumstudio.socketio.SocketIOServer;


public class ConnectionHandler implements Runnable {
	public Socket socket;
	private SocketIOServer socketIO;
	private JSONObject jObject = null;
	private String intputLine;
	private BufferedReader fromClient= null;
	private String serverId = "";
	private final int MIN_FAILOVER_CH_COUNT = 0;
//	public volatile static List<ServerVo> serverList;
	
	private ServerService serverService;
	
	private LogService logService;
	private ServerFailOverService failOverController;
	private InputStream inSocket;
	
	
	

	public ConnectionHandler(Socket socket, SocketIOServer socketIO,List<ServerVo> svrList,ServerService serverService, LogService logService) {
//		System.out.println("EMS에서 소켓 받음");
		this.socket = socket;
		this.socketIO = socketIO;
		this.inSocket = null;
//		ConnectionHandler.serverList = svrList;
		this.serverService = serverService;
		this.logService = logService;
		this.failOverController = null;

		Thread t = new Thread(this);
		t.start();
	}

	public void run() {

		try
		{
			//        	PrintWriter toClient = 
			//    				new PrintWriter(socket.getOutputStream(),true);
			inSocket = socket.getInputStream();
			fromClient =
					new BufferedReader(
							new InputStreamReader(inSocket));
			
			boolean isConnected = true;
			String prevSysInfo = "";
			boolean failOverResult = false;
			while (isConnected) {
				//    			while((intputLine=fromClient.readLine()) != null){
				try {
					intputLine = fromClient.readLine();
//					System.out.println("입력 스트링 :"+socket+","+intputLine);
					if(intputLine == null) {
//						System.out.println("ATE로 부터 입력 스트림 없음 ==="+socket.getPort());
						String clientPararm = "{\"server_id\":"+serverId+",\"errType\" : \"SERVICEDOWN\"}";
						sendSystemFault(clientPararm);
						isConnected = false;
						closeClentSocket();
					}  else {   	
//						System.out.println("serverList ConnectionHandler : "+serverList.size());
						
						jObject  = new JSONObject(intputLine);
						String eventType = jObject.get("type").toString();
						serverId = jObject.get("server_id").toString();
						
						//		        			System.out.println("serverid:"+jObject.get("server_id").toString());
//								        			System.out.println(intputLine);
						//		        			System.out.println(socket.getInetAddress().toString());
							
//						for(int j = 0;j < ConnectionHandler.serverList.size();j++) {
//							System.out.println("ServerId:"+ConnectionHandler.serverList.get(j).getId());
//							System.out.println("Mode:"+ConnectionHandler.serverList.get(j).getMode());
//							System.out.println("Status:"+ConnectionHandler.serverList.get(j).getStatus());
//							System.out.println("=====================");
//						}
							if(eventType.equals("sysInfo")){						
//								System.out.println("intputLine: "+intputLine);								
								
								try{
									boolean checkResult = checkFailOverCondition(jObject);		
									boolean failOverFlag = (boolean) jObject.get("failOver");
									if(!prevSysInfo.equals(intputLine)){
										
										ServerVo sInfo =  new ServerVo();
										boolean serverMode = (boolean) jObject.get("mode");
										int errAteCount = 	(int) jObject.get("ateError");
										int errFtpCount = 	(int) jObject.get("ftpError");
										
										sInfo.setId(Integer.parseInt(serverId));								
										sInfo.setMode((serverMode)?"1":"0");
										sInfo.setStatus(((errAteCount+errFtpCount) > 0)?"1":"0");
//										System.out.println("sInfo.getSnmp_id"+sInfo.getSnmp_status());
										boolean updateResult = updateServerStatus(sInfo);
	//									if(updateResult)reloadServerList();
									}
								
								
									prevSysInfo = intputLine;
									
									if(failOverFlag) {
	//									serverService.serverFailOver(Integer.valueOf(serverId), jObject);
										failOverController = new ServerFailOverService();
										failOverResult= failOverController.doFailOver(serverService, logService, Integer.valueOf(serverId),"SYS");
										
									}
								} catch(Exception e) {
									//System.out.println("sysInfo 정보 파싱 중 오류 발생");
								}
								//httpUtils.sendPost("http://221.153.66.112:4001/mode", "{\"mode\":false}");
								//toClient.println("Thank you for connecting to " + socket.getLocalSocketAddress() + "\nGoodbye!"); 
							}
							
						//} else if(!eventType.equals("sysInfo")){
							socketIO.getBroadcastOperations().sendEvent(eventType, intputLine);
							if(eventType.equals("ateLog")){
								intputLine = null;
								jObject = null;
								isConnected = false;								
								closeClentSocket();
								Thread.currentThread().interrupt();
								
							}
//							if(eventType.equals("ateLog")){
//								for(int i = 0; i < 400; i++){
//									socketIO.getBroadcastOperations().sendEvent(eventType, intputLine);
//								}
//							}
							
						//}
							
					}
					
				}catch(SocketTimeoutException e)
				{
					System.out.println("Timed out trying to read from socket");
					isConnected = false;
					closeClentSocket();
				} 

			}
			//String line = fromClient.readLine();

		} catch (IOException e) {
//			System.out.println("연결 끊힘");
			String clientPararm = "{\"server_id\":"+serverId+",\"errType\" : \"NETWORKDOWN\"}";
			sendSystemFault(clientPararm);
			closeClentSocket();
			e.printStackTrace();
		} 
	}
	
	private void closeClentSocket() {
		try {
		
			if(fromClient != null) fromClient.close();
			if(socket != null) socket.close();
			if(inSocket != null) {
				inSocket.close();
			}
			
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void sendSystemFault(String clientPararm) {
		socketIO.getBroadcastOperations().sendEvent("sysError", clientPararm);
	}

	private boolean checkFailOverCondition(JSONObject sysInfo){
		boolean result = false;
		
		int errAteCount = 	(int) jObject.get("ateError");
		int errFtpCount = 	(int) jObject.get("ftpError");
		if(errAteCount > MIN_FAILOVER_CH_COUNT || errFtpCount > 0 ) {    
//			System.out.println("errAteCount :"+errAteCount);
//			System.out.println("errFtpCount :"+errFtpCount);
			result = true;
		}
		return result;

	}
	
	private boolean updateServerStatus(ServerVo vo) {
//		System.out.println("serverService : "+serverService);
		return serverService.updateServerState(vo);
	}
	
	

	
	private void reloadServerList() {		
//		serverList = serverService.getServerList();
//		for(int i= threadList.size() - 1 ;i >= 0 ;i--) {
//			try {
//				ConnectionHandler th =  threadList.get(i);
//				if(th.socket.isClosed()) {
//					System.out.println("소켓 클로즈 됨 :"+i);
//					threadList.remove(i);
//				} else {
//
//					ConnectionHandler.serverList = serverList;
//					//				for(int j = 0;j < ConnectionHandler.serverList.size();j++) {
//					//					System.out.println("ServerId:"+ConnectionHandler.serverList.get(j).getId());
//					//					System.out.println("Mode:"+ConnectionHandler.serverList.get(j).getMode());
//					//					System.out.println("Status:"+ConnectionHandler.serverList.get(j).getStatus());
//					//					System.out.println("=====================");
//					//				}
//
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
	}
}