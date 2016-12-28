package com.spock.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.w3c.dom.Document;

import entities.Parameter;
import entities.SystemQos;

import com.spock.core.interfaces.IQosPathManager;
import com.spock.utils.SignatureGenerator;
import com.spock.utils.XMLHelper;

public class QosPathSystemManager implements IQosPathManager {
	/*
	 * these variables will carry the path name and a list/hash of success
	 * rate/count for each asset
	 */
	Hashtable<String, Hashtable<String, Integer>> assetSyndicationSuccess = new Hashtable<String, Hashtable<String, Integer>>();
	Hashtable<String, Hashtable<String, Integer>> assetAdsSuccess = new Hashtable<String, Hashtable<String, Integer>>();
	Hashtable<String, ArrayList<Float>> assetAttributeAccuracy = new Hashtable<String, ArrayList<Float>>();
	Hashtable<String, ArrayList<Float>> assetTransmisionDelay = new Hashtable<String, ArrayList<Float>>();
	
	ArrayList<String> allSystems = new ArrayList<String> ();
	ArrayList<SystemQos> systemQos = new ArrayList<SystemQos> ();
	/*
	 * counts the assets for path and count the failed due not exisiting
	 */
	public void addAssetSyndicationSuccess(String systemName, boolean success) {
		addSystem(systemName);
		if (!assetSyndicationSuccess.containsKey(systemName)) {
			assetSyndicationSuccess.put(systemName,
					new Hashtable<String, Integer>());
		}
		

		Hashtable<String, Integer> item = assetSyndicationSuccess.get(systemName);
		if (!success) {
			assetSyndicationSuccess.get(systemName).put("Failed",
					(item.get("Failed") == null ? 0 : item.get("Failed")) + 1);
		}
		assetSyndicationSuccess.get(systemName).put("Count",
				(item.get("Count") == null ? 0 : item.get("Count")) + 1);

	}

	/*
	 * counts the number of all assets and the failed due ads
	 */
	public void addAdsSuccess(String systemName, boolean success) {
		addSystem(systemName);
		if (!assetAdsSuccess.containsKey(systemName)) {
			assetAdsSuccess.put(systemName, new Hashtable<String, Integer>());
		}
		Hashtable<String, Integer> item = assetAdsSuccess.get(systemName);
		if (!success) {
			assetAdsSuccess.get(systemName).put("Failed",
					(item.get("Failed") == null ? 0 : item.get("Failed")) + 1);
		}
		assetAdsSuccess.get(systemName).put("Count",
				(item.get("Count") == null ? 0 : item.get("Count")) + 1);

	}

	/*
	 * save the attribute accuracy value for each asset in a specific path
	 */
	public void addAttributeAccuracy(String systemName, float accuracy) {
		addSystem(systemName);
		if (!assetAttributeAccuracy.containsKey(systemName)) {
			assetAttributeAccuracy.put(systemName, new ArrayList<Float>());
		}
		assetAttributeAccuracy.get(systemName).add(accuracy);
	}

	/*
	 * save the transmission delay rate for each asset in a specific path
	 */
	public void addTransmisionDelay(String systemName, float rate) {
		addSystem(systemName);
		if (!assetTransmisionDelay.containsKey(systemName)) {
			assetTransmisionDelay.put(systemName, new ArrayList<Float>());
		}
		assetTransmisionDelay.get(systemName).add(rate);
	}
	
	public void addSystem(String system)
	{
		if (!allSystems.contains(system))
		{
			allSystems.add(system);
		}
	}
	public static long unixtime() {
		long unixTime = System.currentTimeMillis() / 1000L;

		return unixTime;
	}
	public static String getMcpVideoById(entities.System mcpSystem, String videoId)
	{
		String mcpVideosList = System.getProperty("user.dir")
				+ "/xml/mcpVideosList.xml";
		long timestamp = unixtime();
		long largeTimeStamp = unixtime() + 1209600;
		HashMap<String, String> parameters = new HashMap<String, String>();
		int signitureIndex = -1;
		for (int index = 0; index < mcpSystem.Parameters.size(); index++) {
			if (mcpSystem.Parameters.get(index).Type.toLowerCase().equals(
					"param")) {

				if (mcpSystem.Parameters.get(index).Value
						.equals("TimeStampValue")) {
					parameters.put(mcpSystem.Parameters.get(index).Name,
							Long.toString(timestamp));
				} else if (mcpSystem.Parameters.get(index).Value
						.equals("LargeTimeStamp")) {
					parameters.put(mcpSystem.Parameters.get(index).Name,
							Long.toString(largeTimeStamp));
				} else if (mcpSystem.Parameters.get(index).Value
						.equals("SignitureValue")) {
					signitureIndex = index;
				} else if (mcpSystem.Parameters.get(index).Value
						.equals("CounterValue")) {
					// parameters.put(mcpSystem.Parameters.get(index).Name,
					// String.valueOf((i + 1)));
				} else {
					parameters.put(mcpSystem.Parameters.get(index).Name,
							mcpSystem.Parameters.get(index).Value);
				}
			}
		}
		parameters.put("filter_cond", "==");
		parameters.put("filter_by", "video_id");
		parameters.put("filter_value", videoId);
		String strXMLFilename = mcpVideosList;
		String xml = XMLHelper.xmlparser(strXMLFilename);
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-type", "text/xml; charset=ISO-8859-1");
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHttpMethod("POST");
		requestInfo.setApiKey(mcpSystem.ApiKey);
		requestInfo.setSecretKey(mcpSystem.SecretKey);
		requestInfo.setData(xml);
		requestInfo.setIsHttps(mcpSystem.IsHttps);
		requestInfo.setRequestPath(mcpSystem.RequestPath);
		requestInfo.setHost(mcpSystem.Host);
		requestInfo.setParameters(parameters);
		requestInfo.setHeaders(headers);
		requestInfo.setSignatureType("");
		Parameter signitureParameter = new Parameter();
		if (signitureIndex > -1) {
			signitureParameter.Name = mcpSystem.Parameters
					.get(signitureIndex).Name;
			signitureParameter.Value = SignatureGenerator.generatebase64(
					mcpVideosList, mcpSystem.SecretKey, timestamp,
					"", "", true);
		} else {
			signitureParameter = null;
		}
		long expiration_time = unixtime() + 1209600;
		PullingManager pm = new PullingManager();
		Document doc = null;

String returnedData =pm.postFeedData(requestInfo,
		signitureParameter);
			return returnedData;

		
	}
	public static String getSystemResult(entities.System system, String mcpId, long timestamp, long unixTime, String startDate, String endDate) {
		long largeTimeStamp = unixTime + 1209600;
		HashMap<String, String> parameters = new HashMap<String, String>();
		int signitureIndex = -1;
		String clientID = "";
		for (int index = 0; index < system.Parameters.size(); index++) {
			if (system.Parameters.get(index).Type.toLowerCase().equals("param")) {
				if (system.Parameters.get(index).Name.toLowerCase().equals(
						"client_id")) {
					clientID = system.Parameters.get(index).Value;
				}
				if (system.Parameters.get(index).Value.equals("TimeStampValue")) {
					parameters.put(system.Parameters.get(index).Name,
							Long.toString(timestamp));
				} else if (system.Parameters.get(index).Value
						.equals("LargeTimeStamp")) {
					parameters.put(system.Parameters.get(index).Name,
							Long.toString(largeTimeStamp));
				} else if (system.Parameters.get(index).Value
						.equals("SignitureValue")) {
					signitureIndex = index;
				} else {
					parameters.put(system.Parameters.get(index).Name,
							system.Parameters.get(index).Value);
				}
			}
		}

		if (!system.ContentIdParamName.trim().equals("")
				&& !system.ContentIdParamName.trim().equals("undefined")) {
			parameters.put(system.ContentIdParamName, mcpId);
		}
		HashMap<String, String> headers = new HashMap<String, String>();
		for (int index = 0; index < system.Parameters.size(); index++) {
			if (system.Parameters.get(index).Type.toLowerCase()
					.equals("header")) {

				headers.put(system.Parameters.get(index).Name,
						system.Parameters.get(index).Value);
			}
		}
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHttpMethod(system.HttpMethod.toUpperCase());
		requestInfo.setIsHttps(system.IsHttps);
		if (system.ContentIdParamName.trim().equals("")
				|| system.ContentIdParamName.trim().equals("undefined")) {
			requestInfo.setRequestPath(system.RequestPath + mcpId);
		} else {
			requestInfo.setRequestPath(system.RequestPath);
		}
		requestInfo.setHost(system.Host);
		requestInfo.setParameters(parameters);
		requestInfo.setHeaders(headers);
		requestInfo.setData(system.Data.replace("$$startDate$$", startDate)
				.replace("$$endDate$$", endDate));
		requestInfo.setApiKey(system.ApiKey);
		requestInfo.setSecretKey(system.SecretKey);
		requestInfo.setSignatureType(system.SignitureType.trim());
		String signiture = "";
		if (system.SignitureType.trim().equals("SHA-1")) {
			requestInfo.setTextToSign(system.HttpMethod.toUpperCase()
					+ clientID + requestInfo.getRequestPath() + "?"
					+ SignatureGenerator.concatenateParams(parameters, "&")
					+ system.SecretKey);
			signiture = requestInfo.getSignature();
		} else if (system.SignitureType.trim().equals("SHA-256")) {
			requestInfo.setTextToSign(requestInfo.getSecretKey()
					+ requestInfo.getHttpMethod()
					+ requestInfo.getRequestPath());
			signiture = requestInfo.getSignature();
		} else if (system.SignitureType.trim().equals("HmacSHA256")) {
			signiture = SignatureGenerator.generatebase64(system.Data,
					system.SecretKey, timestamp, startDate, endDate, false);
		}
		Parameter signitureParameter = new Parameter();
		if (signitureIndex > -1) {
			signitureParameter.Name = system.Parameters.get(signitureIndex).Name;
			signitureParameter.Value = signiture;
		} else {
			signitureParameter = null;
		}

		PullingManager pullingManager = new PullingManager();
		String result = "";
		if (requestInfo.getHttpMethod().equals("GET")) {
			result = pullingManager
					.getFeedData(requestInfo, signitureParameter);
		} else {
			result = pullingManager.postFeedData(requestInfo,
					signitureParameter);
		}

		return result;
	}
}
