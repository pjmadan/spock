package com.spock.core;

import java.util.HashMap;

import org.w3c.dom.Document;

import entities.Parameter;

import com.spock.utils.SignatureGenerator;
import com.spock.utils.XMLHelper;

public class Syndicator {
	public static void syndicateVideo(entities.System system, String uploadId, String syndicatorId){
	
		long timestamp = unixtime();
		String mcpSyndicatePath = System.getProperty("user.dir")
				+ "\\xml\\mcpSyndicate.xml";
			long largeTimeStamp = unixtime() + 1209600;
			HashMap<String, String> parameters = new HashMap<String, String>();
			int signitureIndex = -1;
			for (int index = 0; index < system.Parameters.size(); index++) {
				if (system.Parameters.get(index).Type.toLowerCase().equals(
						"param")) {

					if (system.Parameters.get(index).Value
							.equals("TimeStampValue")) {
						parameters.put(system.Parameters.get(index).Name,
								Long.toString(timestamp));
					} else if (system.Parameters.get(index).Value
							.equals("LargeTimeStamp")) {
						parameters.put(system.Parameters.get(index).Name,
								Long.toString(largeTimeStamp));
					} else if (system.Parameters.get(index).Value
							.equals("SignitureValue")) {
						signitureIndex = index;
					} else if (system.Parameters.get(index).Value
							.equals("CounterValue")) {
					
					} else if (system.Parameters.get(index).Name
							.equals("id")) {
						parameters.put(system.Parameters.get(index).Name,
							"EA3D6521592F4885B4B22284CF8A32C3");
					}
					else {
						parameters.put(system.Parameters.get(index).Name,
								system.Parameters.get(index).Value);
					}
				}
			}
			String strXMLFilename = mcpSyndicatePath;
			String xml = XMLHelper.xmlparser(strXMLFilename);
			xml = xml.replace("$$uploadId$$", uploadId);
			xml = xml.replace("$$syndicatorId$$", syndicatorId);
			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Content-type", "text/xml; charset=ISO-8859-1");
			RequestInfo requestInfo = new RequestInfo();
			requestInfo.setHttpMethod("POST");
			requestInfo.setApiKey("EA3D6521592F4885B4B22284CF8A32C3");
			requestInfo.setSecretKey("B9618F69B1DD4899A2E0DF3C0D2F9247");
			requestInfo.setData(xml);
			requestInfo.setIsHttps(system.IsHttps);
			requestInfo.setRequestPath(system.RequestPath);
			requestInfo.setHost("mcm-stage.uim.univision.com");//(system.Host);
			requestInfo.setParameters(parameters);
			requestInfo.setHeaders(headers);
			requestInfo.setSignatureType("");
			Parameter signitureParameter = new Parameter();
			if (signitureIndex > -1) {
				signitureParameter.Name = system.Parameters
						.get(signitureIndex).Name;
				signitureParameter.Value = SignatureGenerator.generatebase64(
						xml, "B9618F69B1DD4899A2E0DF3C0D2F9247", timestamp,
						"","", false);
			} else {
				signitureParameter = null;
			}
			long expiration_time = unixtime() + 1209600;
			PullingManager pm = new PullingManager();
			Document doc = null;
	try{
		pm.postFeedData(
						requestInfo, signitureParameter);

			} catch (Exception ex) {
			
			}
			
		
		
		
		
	}
	public static long unixtime() {
		long unixTime = System.currentTimeMillis() / 1000L;

		return unixTime;
	}
}
