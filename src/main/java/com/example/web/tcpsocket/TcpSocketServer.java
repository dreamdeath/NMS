package com.example.web.tcpsocket;

import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.example.web.socketio.SocketServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;


@Component
public class TcpSocketServer implements ApplicationListener<ApplicationReadyEvent> {
	private ServerSocket server;
	@Override 
    public void onApplicationEvent(final ApplicationReadyEvent event) {
    	 
    	System.out.println("onApplicationEvent");
//    	startTcpServer();
    	try {
			handleConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void handleConnection() throws IOException {
    	server = new ServerSocket(9090);
        System.out.println("Waiting for client message...");
        SocketServer socketIOServer = new SocketServer();
        SocketIOServer socketIO = socketIOServer.initSocketIO();
        while (true) {
            try {
            	System.out.println("server :"+server);
                Socket socket = server.accept();
                new ConnectionHandler(socket,socketIO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
 } 