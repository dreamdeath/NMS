package com.example.web.socketio;

import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

public class SocketServer {
	Configuration config = new Configuration();	
	public SocketIOServer server = null;
	
	
    public SocketIOServer initSocketIO() {
    	System.out.println("소켓 서버 실행");
    	config.setHostname("localhost");
    	config.setPort(10000); 
    	SocketIOServer server = new SocketIOServer(config);
	    server.addEventListener("chatevent", LogDataObject.class, new DataListener<LogDataObject>() {
	        public void onData(SocketIOClient client, LogDataObject data, AckRequest ackRequest) {
	            // broadcast messages to all clients
	        	System.out.println("data :"+data.getId());
	            server.getBroadcastOperations().sendEvent("chatevent", data);
	        }
	    });
	
	    server.start();
	
//	    try {
//			Thread.sleep(Integer.MAX_VALUE);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	
//	    server.stop();
	   
      return server;
    }    
}
