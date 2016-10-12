package com.example.web.tcpsocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.web.socketio.SocketServer;

public class ConnectionHandler implements Runnable {
	Socket socket;
	SocketIOServer socketIO;
	JSONObject jObject = null;
	String intputLine;
    public ConnectionHandler(Socket socket, SocketIOServer socketIO) {
        this.socket = socket;
        this.socketIO = socketIO;
        Thread t = new Thread(this);
        t.start();
    }
 
    public void run() {
    	
        try
        {
        	PrintWriter toClient = 
    				new PrintWriter(socket.getOutputStream(),true);
    			BufferedReader fromClient =
    				new BufferedReader(
    						new InputStreamReader(socket.getInputStream()));
    			while((intputLine=fromClient.readLine()) != null){
                    System.out.println(intputLine);
                    System.out.println("Server received: " + intputLine); 
        			jObject  = new JSONObject(intputLine);
        			String eventType = jObject.get("type").toString();
        			//toClient.println("Thank you for connecting to " + socket.getLocalSocketAddress() + "\nGoodbye!"); 
        			socketIO.getBroadcastOperations().sendEvent(eventType, intputLine);
                }
    			//String line = fromClient.readLine();
    			
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
}