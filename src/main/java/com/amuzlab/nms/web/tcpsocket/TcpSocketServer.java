package com.amuzlab.nms.web.tcpsocket;

import org.springframework.stereotype.Component;

import com.amuzlab.nms.web.domain.ServerVo;
import com.amuzlab.nms.web.log.LogService;
import com.amuzlab.nms.web.server.ServerService;
import com.amuzlab.nms.web.socketio.SocketServer;
import com.corundumstudio.socketio.SocketIOServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@Component
public class TcpSocketServer implements ApplicationListener<ApplicationReadyEvent> {
	private ServerSocket server;
	private SocketIOServer socketIO;
	public final static int PORT = 9090;
	private List<ConnectionHandler> threadList;

	@Resource
	private ServerService serverService;

	@Resource
	private LogService logService;

	public static ArrayList<ServerVo> serverList;
	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		System.out.println("onApplicationEvent");
		// startTcpServer();
		handleConnection();
	}

	public void handleConnection() {
		try {
			server = new ServerSocket(PORT);
//			System.out.println("Waiting for client message...");
			SocketServer.initSocketIO();
			socketIO = SocketServer.getSocketIOServer();
			serverList = serverService.getServerList();
//			System.out.println("serverList handleConnection : "+serverList.size());

			threadList = new ArrayList<ConnectionHandler>();
			while (true) {
				try {

					Socket socket = server.accept();
					if(socket != null) {
//						System.out.println(socket.getRemoteSocketAddress());	
						ConnectionHandler socketThread = new ConnectionHandler(socket, socketIO,serverList,serverService,logService);
//						threadList.add(socketThread);
//						System.out.println("thread count : "+threadList.size());

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public boolean reloadServerList() {		
		boolean result = false;
		serverList = serverService.getServerList();
		for(int i= threadList.size() - 1 ;i >= 0 ;i--) {
			try {
				ConnectionHandler th =  threadList.get(i);
				if(th.socket.isClosed()) {
					System.out.println("소켓 클로즈 됨 :"+i);
					threadList.remove(i);
				} else {

//					ConnectionHandler.serverList = serverList;
					//				for(int j = 0;j < ConnectionHandler.serverList.size();j++) {
					//					System.out.println("ServerId:"+ConnectionHandler.serverList.get(j).getId());
					//					System.out.println("Mode:"+ConnectionHandler.serverList.get(j).getMode());
					//					System.out.println("Status:"+ConnectionHandler.serverList.get(j).getStatus());
					//					System.out.println("=====================");
					//				}

				}
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

}