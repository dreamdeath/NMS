package com.amuzlab.nms.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
	
		public void sendGet() throws Exception {

			String url = "http://www.google.com/search?q=mkyong";

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();
//			System.out.println("\nSending 'GET' request to URL : " + url);
//			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			System.out.println(response.toString());

		}

		// HTTP POST request
		public String sendPost(String url, String urlParameters) {
			String result = "{\"result\":\"fail\"}";
			try {
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
				//add reuqest header
				con.setRequestMethod("POST");
				con.setDoOutput(true);
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestProperty("Accept", "application/json");
				con.setRequestMethod("POST");
				con.connect();
	
				// Send post request
				byte[] outputBytes = urlParameters.getBytes("UTF-8");
				OutputStream os = con.getOutputStream();
				os.write(outputBytes);			
				os.flush();
				os.close();
	
				int responseCode = con.getResponseCode();
//				System.out.println("\nSending 'POST' request to URL : " + url);
//				System.out.println("Post parameters : " + urlParameters);
//				System.out.println("Response Code : " + responseCode);
	
				BufferedReader in = new BufferedReader(
				        new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
	
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
	
				//print result
				result = response.toString();
//				System.out.println(response.toString());
			} catch(Exception e){
				e.printStackTrace();
			}
			return result;	
		}

}
