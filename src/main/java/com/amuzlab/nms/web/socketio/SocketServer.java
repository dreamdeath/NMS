package com.amuzlab.nms.web.socketio;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

public class SocketServer {
	public static final int PORT = 10000;
	static Configuration config = new Configuration();
	public static SocketIOServer socketIOserver = null;
	
	
	public static void initSocketIO() {
		if(socketIOserver == null) {
			try{
			System.out.println("소켓 서버 실행");
			String ipAddress = getIpAddress();
			System.out.println("address : " + ipAddress + ", port : " + PORT);
			config.setHostname(ipAddress);			
			config.setPort(PORT);
			SocketConfig sockConfig = new SocketConfig();
			sockConfig.setReuseAddress(true);
			config.setSocketConfig(sockConfig);
			socketIOserver  = new SocketIOServer(config);
			
			socketIOserver.start();		
			}catch(Exception e){
				System.out.println("ERROR : "+e);
			}
		}
	}
	
	public static SocketIOServer getSocketIOServer() {
		return socketIOserver;
	}
	private static String getIpAddress(){
		InetAddress inetAddress = null;
		String address = null;
		try{
			inetAddress = InetAddress.getLocalHost();
			address = inetAddress.getHostAddress();
		}catch(UnknownHostException unknownHostException){
			unknownHostException.printStackTrace();
		}
		return address;
	}
}
