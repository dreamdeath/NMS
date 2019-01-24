package com.amuzlab.nms.web.server;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.amuzlab.nms.web.domain.ServerGroupVo;
import com.amuzlab.nms.web.domain.ServerVo;
import com.amuzlab.nms.web.socketio.SocketServer;
import com.amuzlab.nms.web.sqlmappers.server.ServerMapper;
import com.amuzlab.nms.web.tcpsocket.TcpSocketServer;



@Service
public class ServerService {
	@Resource
	private ServerMapper serverMapper;
	
//	@Resource
//	private TcpSocketServer tcpSocketServer;
	private static final Pattern patternPrivateNotLocal = Pattern.compile("(^10\\..*)|(^172\\.1[6-9]\\.)|(^172\\.2[0-9]\\.)|(^172\\.3[0-1]\\.)|(^1‌​92\\.168\\.)");
	
	public ServerGroupVo getGroupInfo(ServerGroupVo vo) {
		ServerGroupVo data = null;
		data = serverMapper.getGroupInfo(vo);
		return data;
	}
	
	public ServerVo getServerInfo(ServerVo vo) {
		ServerVo data = null;
		data = serverMapper.getServerInfo(vo);
		return data;
	}
	
	public ArrayList<ServerGroupVo> getGroupList() {
		ArrayList<ServerGroupVo> dataList = null;
		dataList = serverMapper.getGroupList();
		return dataList;
	}
	
	public ArrayList<ServerVo> getServerList(ServerGroupVo vo) {
		ArrayList<ServerVo> dataList = null;
		dataList = serverMapper.getServerList(vo);
		return dataList;
	}
	
	public ArrayList<ServerVo> getServerList() {
		ArrayList<ServerVo> dataList = null;
		ServerGroupVo vo = new ServerGroupVo();
		dataList = serverMapper.getServerList(vo);
		return dataList;
	}
	
	public Boolean insertGroupInfo(ServerGroupVo vo) {
		boolean result = false;
		try {
			 serverMapper.insertGroupInfo(vo);
			result = true;
	
		} catch(Exception e){
			
		}
		return result;
	}
	
	public Boolean insertServerInfo(ServerVo vo) {
		boolean result = false;
		try {
			serverMapper.insertServerInfo(vo);
			result = true;	
//			tcpSocketServer.reloadServerList();
		} catch(Exception e){
			
		}
		return result;
	}
	
	public Boolean updateGroupInfo(ServerGroupVo vo) {
		boolean result = false;
		try {
			 serverMapper.updateGroupInfo(vo);
			result = true;
	
		} catch(Exception e){
			
		}
		return result;
	}
	
	public Boolean updateServerInfo(ServerVo vo) {
		boolean result = false;
		try {
			serverMapper.updateServerInfo(vo);
			result = true;	
//			tcpSocketServer.reloadServerList();
		} catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public Boolean deleteGroupInfo(ServerGroupVo vo) {
		boolean result = false;
		try {
			 serverMapper.deleteGroupInfo(vo);
			result = true;
	
		} catch(Exception e){
			
		}
		return result;
	}
	
	public Boolean deleteServerInfo(ServerVo vo) {
		boolean result = false;
		try {
			serverMapper.deleteServerInfo(vo);
			result = true;	
//			tcpSocketServer.reloadServerList();
		} catch(Exception e){
			
		}
		return result;
	}
	
	public Boolean updateServerState(ServerVo vo) {
		boolean result = false;
		try {
//			System.out.println("==================>"+serverMapper);
//			System.out.println("snmp_status : "+vo.getSnmp_status());
			serverMapper.updateServerState(vo);
			result = true;	
//			tcpSocketServer.reloadServerList();
		} catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	private String getIpAddress() throws UnknownHostException ,SocketException{
		
		
	        String hostIP = InetAddress.getLocalHost().getHostAddress();
	        boolean findIpAddree = false;
	        if (hostIP.equals("127.0.0.1")) {           

		        /*
		         * Above method often returns "127.0.0.1", In this case we need to
		         * check all the available network interfaces
		         */
		        Enumeration<NetworkInterface> nInterfaces = NetworkInterface
		                .getNetworkInterfaces();
		        while (nInterfaces.hasMoreElements()) {
		        	NetworkInterface nwInterface = nInterfaces.nextElement();
		        	if (!nwInterface.isUp() || nwInterface.isLoopback()) {
		               continue;
		        		
		            }

		            Enumeration<InetAddress> inetAddresses = nwInterface.getInetAddresses();		  
	        		
		            while (inetAddresses.hasMoreElements()) {
		            	InetAddress inetAddr = inetAddresses.nextElement();		            	
		                if (!inetAddr.isLoopbackAddress() && 
		                		!inetAddr.isLinkLocalAddress() && 
		                			!inetAddr.isSiteLocalAddress()
		                			) {
		                    	hostIP = inetAddr.getHostAddress();
		                    	findIpAddree = true;
		                        break;
		                }
		               
		            }
		            if(findIpAddree) {
		            	break;
		            }
		        }		
	        }
	        
	        return hostIP;
	}
	
	public void getSocketServerInfo(HashMap<String, Object> hashMap){
		boolean result = true;
		String address;
		try {
			address = getIpAddress();
			hashMap.put("socketServerIp", address);
			hashMap.put("socketServerPort", TcpSocketServer.PORT);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		} finally{
			hashMap.put("result", result);
		}
	}
	
	public void getClientSocketServerInfo(HashMap<String, Object> hashMap){
		boolean result = true;
		String address;
		try {
			address = getIpAddress();
			hashMap.put("socketServerIp", address);
			hashMap.put("socketServerPort", SocketServer.PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			result = false;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		} finally{
			hashMap.put("result", result);
		}
	}
	
	public static boolean isPrivateAndNotLocalIP(String ip)
	{
	    return  patternPrivateNotLocal.matcher(ip).find();
	}
}
