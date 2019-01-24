package com.amuzlab.nms.snmp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;


/**
 * Simplest client possible
 * 
 * @author shmmon
 *
 */
public class SimpleSnmpClient {

	private String address;

	private Snmp snmp;
	
	private TreeUtils treeUtils;

	public SimpleSnmpClient(String address) {
		super();
		this.address = address;
		try {
			start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// Since snmp4j relies on asynch req/resp we need a listener
	// for responses which should be closed
	public void stop() throws IOException {
		snmp.close();
	}

	private void start() throws IOException {
		TransportMapping<?> transport = new DefaultUdpTransportMapping();
		snmp = new Snmp(transport);
		treeUtils = new TreeUtils(snmp,new DefaultPDUFactory());
		// Do not forget this line!
		transport.listen();
	}
	
	
	public List<String> getSubTreeData(String type, OID oid){
		List<TreeEvent> events = treeUtils.getSubtree(getTarget(), oid);
		if(events == null || events.size() == 0){
		  System.out.println("No result returned.");
		  System.exit(1);
		}

		// Get snmpwalk result.
//		Map<String,String> subTreeNames = new HashMap<String,String>();
		List<String> subTreeList = new ArrayList<String>();
		for (TreeEvent event : events) {
		  if(event != null){
		    if (event.isError()) {
		        System.err.println("oid [" + oid + "] " + event.getErrorMessage());
		      }

		    VariableBinding[] varBindings = event.getVariableBindings();
		    if(varBindings == null || varBindings.length == 0){
		      System.out.println("No result returned.");
		    }
		    for (VariableBinding varBinding : varBindings) {
		    	if(type.equals("STR")) {
		    		subTreeList.add("\""+varBinding.getVariable()+"\"");		    		
		    	} else {
		    		subTreeList.add(varBinding.getVariable().toString());
		    	}
		    	
//		      System.out.println(
//		          varBinding.getOid() + 
//		          " : " + 
//		          varBinding.getVariable().getSyntaxString() +
//		          " : " +
//		          varBinding.getVariable());
		    }
		  }
		}
		return subTreeList;
	}

	public String getAsString(OID oid){
		
		ResponseEvent event = null;
		String resultString = "";

		try {
			event = get(new OID[]{oid});
			if(event != null && event.getResponse() != null) resultString  = event.getResponse().get(0).getVariable().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultString;
	}
	
	
	public void getAsString(OID oids,ResponseListener listener) {
		try {
			snmp.send(getPDU(new OID[]{oids}), getTarget(),null, listener);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private PDU getPDU(OID oids[]) {
		PDU pdu = new PDU();
		for (OID oid : oids) {
			pdu.add(new VariableBinding(oid));
		}
	 	   
		pdu.setType(PDU.GET);
		return pdu;
	}
	
	public ResponseEvent get(OID oids[]) throws IOException {
		
	   ResponseEvent event = snmp.send(getPDU(oids), getTarget(), null);
	   if(event != null) {
		   return event;
	   }
	   throw new RuntimeException("GET timed out");	  
	}
	
	private Target getTarget() {
		Address targetAddress = GenericAddress.parse(address);
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString("nms"));
		target.setAddress(targetAddress);
		target.setRetries(2);
		target.setTimeout(1500);
		target.setVersion(SnmpConstants.version2c);
		return target;
	}

	/**
	 * Normally this would return domain objects or something else than this...
	 */
	public List<List<String>> getTableAsStrings(OID[] oids) {
		TableUtils tUtils = new TableUtils(snmp, new DefaultPDUFactory());
		
		@SuppressWarnings("unchecked") 
			List<TableEvent> events = tUtils.getTable(getTarget(), oids, null, null);
		
		List<List<String>> list = new ArrayList<List<String>>();
		for (TableEvent event : events) {
			if(event.isError()) {
				throw new RuntimeException(event.getErrorMessage());
			}
			List<String> strList = new ArrayList<String>();
			list.add(strList);
			for(VariableBinding vb: event.getColumns()) {
				strList.add(vb.getVariable().toString());
			}
		}
		return list;
	}
	
	public static String extractSingleString(ResponseEvent event) {
		return event.getResponse().get(0).getVariable().toString();
	}
}
