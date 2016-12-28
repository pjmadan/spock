package com.spock.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import com.spock.core.PullingManager;
import com.spock.core.RequestInfo;

import entities.*;
import entities.System;

public class FeedDataHelper {
	public static String qosSiteHost = EnvirommentManager.getInstance().getProperty("QosSiteHost");
	public static ArrayList<Run> getRuns() {
		ArrayList<Run> runs = new ArrayList<Run>();
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/getRuns");
		requestInfo.setHttpMethod("GET");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		String data = pullingManager.getFeedData(requestInfo, null);
		JSONArray jsonRuns = new JSONArray(data);
		for (int index = 0; index < jsonRuns.length(); index++) {
			JSONObject currentJsonPath = jsonRuns.getJSONObject(index);
			Run run = new Run();
		
	
			run.Id = currentJsonPath.getInt("ID");
			run.lastUpdDate = DateTime.parse(currentJsonPath
					.getString("LastUpdatedDate"));
			run.startDate = currentJsonPath.getString("StartDate");
			run.endDate = currentJsonPath.optString("EndDate");
			run.QosValue = (float) currentJsonPath.getDouble("QOSValue");
			run.movingWeight = (float) currentJsonPath.getDouble("MovingWeight");
			run.totalAssets = currentJsonPath.getInt("TotalAssets");
			runs.add(run);
		}
		return runs;
	}
	public static ArrayList<String> getLastRunFailedVideos() {
		ArrayList<String> videoIds = new ArrayList<String>();
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/getLastRunFailedVideos");
		requestInfo.setHttpMethod("GET");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		String data = pullingManager.getFeedData(requestInfo, null);
		JSONArray jsonRuns = new JSONArray(data);
		for (int index = 0; index < jsonRuns.length(); index++) {
			JSONObject currentJsonPath = jsonRuns.getJSONObject(index);
			String videoId =currentJsonPath.getString("SystemVideoId");
			videoIds.add(videoId);
		}
		return videoIds;
	}
	public static ArrayList<Path> getPaths() {
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("d:\\myfile.txt", true)))) {
		    out.println("hay");
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
		
		ArrayList<Path> paths = new ArrayList<Path>();
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/getPaths");
		requestInfo.setHttpMethod("GET");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("d:\\myfile.txt", true)))) {
		    out.println("hay1");
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
		
		String data = pullingManager.getFeedData(requestInfo, null);
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("d:\\myfile.txt", true)))) {
		    out.println("hay2");
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
		
		JSONArray jsonPaths = new JSONArray(data);
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("d:\\myfile.txt", true)))) {
		    out.println("hay3");
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
		
		for (int index = 0; index < jsonPaths.length(); index++) {
			JSONObject currentJsonPath = jsonPaths.getJSONObject(index);
			Path path = new Path();
			path.Id = currentJsonPath.getInt("ID");
			path.Name = currentJsonPath.getString("Name");
			path.AppName = currentJsonPath.getString("AppName");
			path.SyndicationSuccessWeight = (float) currentJsonPath
					.getDouble("SyndicationSuccessWeight");
			path.AdServerWeight = (float) currentJsonPath
					.getDouble("AdServerWeight");
			path.TransmissionDelayWeight = (float) currentJsonPath
					.getDouble("TransmissionDelayWeight");
			path.AttributesAccuracyWeight = (float) currentJsonPath
					.getDouble("AttributesAccuracyWeight");
			path.NumberOfConsumers = (float) currentJsonPath
					.getDouble("TotalNumberOfConsumers");
			path.Systems = getPathSystems(path.Id);
			paths.add(path);
		}
		return paths;
	}

	public static ArrayList<System> getPathSystems(int pathId) {
		ArrayList<System> systems = new ArrayList<System>();
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/getSystemsByPathId");
		requestInfo.setHttpMethod("POST");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		requestInfo.setData("{\"pathId\":\"" + pathId + "\"}");
		String data = pullingManager.postFeedData(requestInfo, null);
		JSONArray jsonSystems = new JSONArray(data);
		for (int index = 0; index < jsonSystems.length(); index++) {
			JSONObject currentJsonPath = jsonSystems.getJSONObject(index);
			System system = new System();

			system.Id = currentJsonPath.getInt("ID");
			system.Name = currentJsonPath.getString("Name");
			system.ContentIdParamName = currentJsonPath
					.getString("ContentIdParameterName");
			system.ApiKey = currentJsonPath.getString("ApiKey");
			system.HttpMethod = currentJsonPath.getString("HttpMethod");
			system.SecretKey = currentJsonPath.getString("SecretKey");
			system.SignitureType = currentJsonPath.getString("SignatureType");
			system.Data = currentJsonPath.getString("Data");
			system.RequestPath = currentJsonPath.getString("RequestPath");
			system.responseType = currentJsonPath.getString("ResponseType");
			system.Host = currentJsonPath.getString("Host");
			try
			{

				system.VideoIdPath = currentJsonPath.getString("VideoIdFieldPath");
			}catch(Exception ex)
			{

				system.VideoIdPath = "";
			}
			system.hasAdsServer=(((int) currentJsonPath.getJSONObject("HasAS")
					.getJSONArray("data").get(0)) == 0 ? false : true);
			system.hasAttributeAcuraccy=(((int) currentJsonPath.getJSONObject("HasAA")
					.getJSONArray("data").get(0)) == 0 ? false : true);
			system.hasSyndicationOfSuccess=(((int) currentJsonPath.getJSONObject("HasSS")
					.getJSONArray("data").get(0)) == 0 ? false : true);
			system.hasTransmissionDelay=(((int) currentJsonPath.getJSONObject("HasTD")
					.getJSONArray("data").get(0)) == 0 ? false : true);
			system.IsHttps = (((int) currentJsonPath.getJSONObject("IsHttps")
					.getJSONArray("data").get(0)) == 0 ? false : true);
			system.SignRequest = (((int) currentJsonPath
					.getJSONObject("SignRequest").getJSONArray("data").get(0)) == 0 ? false
					: true);
			system.Parameters = getSystemParameters(system.Id);
			system.Attributes = getSystemAttributes(system.Id);
			try {
				system.DateFieldPath = currentJsonPath
						.getString("DateFieldPath");
			} catch (Exception ex) {
			}
			try {
				system.DateFormat = currentJsonPath.getString("DateFormat");
			} catch (Exception ex) {
			}
			systems.add(system);

		}
		return systems;
	}

	public static ArrayList<Parameter> getSystemParameters(int systemId) {
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/getParametersBySystemId");
		requestInfo.setHttpMethod("POST");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		requestInfo.setData("{\"systemId\":\"" + systemId + "\"}");
		String data = pullingManager.postFeedData(requestInfo, null);
		JSONArray jsonParamaters = new JSONArray(data);
		for (int index = 0; index < jsonParamaters.length(); index++) {
			JSONObject currentJsonPath = jsonParamaters.getJSONObject(index);
			Parameter parameter = new Parameter();
			parameter.Id = currentJsonPath.getInt("ID");
			parameter.Name = currentJsonPath.getString("ParameterName");
			parameter.Value = currentJsonPath.getString("ParameterValue");
			parameter.Type = currentJsonPath.getString("Type");
			parameters.add(parameter);
		}
		return parameters;
	}
	public static int getAdServerFailedCount(String mcpID) {
		try{
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/getAdserverFailedCountForLastTwoRuns");
		requestInfo.setHttpMethod("POST");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		requestInfo.setData("{\"mcpId\":\"" + mcpID + "\"}");
		String data = pullingManager.postFeedData(requestInfo, null);
		JSONArray josnArr = new JSONArray(data);
		return josnArr.getJSONObject(0).getInt("count");
		}catch(Exception ex){
			return 0;
		}

	}
	public static ArrayList<Attribute> getSystemAttributes(int systemId) {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/getAttributesBySystemId");
		requestInfo.setHttpMethod("POST");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		requestInfo.setData("{\"systemId\":\"" + systemId + "\"}");
		String data = pullingManager.postFeedData(requestInfo, null);
		JSONArray jsonAttributes = new JSONArray(data);
		for (int index = 0; index < jsonAttributes.length(); index++) {
			JSONObject currentJsonPath = jsonAttributes.getJSONObject(index);
			Attribute attribute = new Attribute();
			attribute.Id = currentJsonPath.getInt("ID");
			attribute.Name = currentJsonPath.getString("AttributeName");
			attribute.path = currentJsonPath.getString("AttributePath");
			attribute.BaseId = currentJsonPath.getInt("BaseID");
		
			if(	!currentJsonPath.isNull("IsDateValue")){
			attribute.IsDateValue = (((int) currentJsonPath.getJSONObject("IsDateValue")
					.getJSONArray("data").get(0)) == 1 ? true : false);}
			else
			{
				attribute.IsDateValue = false;
			}
			attributes.add(attribute);
		}
		return attributes;
	}

	public static int isVideoSyndicatedToBrightspot(int mcpId) {
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/getVideoByMcpId");
		requestInfo.setHttpMethod("POST");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		requestInfo.setData("{\"mcpId\":\"" + mcpId + "\"}");
		String data = pullingManager.postFeedData(requestInfo, null);
		JSONArray jsonVideos = new JSONArray(data);
		for(int index =0; index < jsonVideos.length(); index++){
			if(jsonVideos.getJSONObject(index).getInt("IsBrightspot") == 1){
				return 1;
			}
			
		}
		return 0;
	}
	public static int insertRun(Run run) {
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/createRun");
		requestInfo.setHttpMethod("POST");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		ObjectMapper mapper = new ObjectMapper();
		String runAsJson = "";
		try {
			runAsJson = mapper.writeValueAsString(run);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestInfo.setData(runAsJson);
		String data =		pullingManager.postFeedData(requestInfo, null);
		JSONObject josnObject = new JSONObject(data);
		return josnObject.getInt("maxId");
	}

	public static void deleteFullRunInfo(Run run) {
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/deleteFullRunInfo");
		requestInfo.setHttpMethod("POST");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		ObjectMapper mapper = new ObjectMapper();
		String runAsJson = "";
		try {
			runAsJson = mapper.writeValueAsString(run);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestInfo.setData(runAsJson);
		pullingManager.postFeedData(requestInfo, null);
	}
	
	public static void insertFailedAkmaiLinkRun(AkamaiFailedLink failedLink) {
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/createFailedAkmaiLink");
		requestInfo.setHttpMethod("POST");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		ObjectMapper mapper = new ObjectMapper();
		String runAsJson = "";
		try {
			runAsJson = mapper.writeValueAsString(failedLink);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestInfo.setData(runAsJson);
		pullingManager.postFeedData(requestInfo, null);
	}


	public static void updateRun(Run run) {
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/updateRun");
		requestInfo.setHttpMethod("POST");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		ObjectMapper mapper = new ObjectMapper();
		String runAsJson = "";
		try {
			runAsJson = mapper.writeValueAsString(run);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestInfo.setData(runAsJson);
		String data =		pullingManager.postFeedData(requestInfo, null);
		
	}
	public static void insertPathQos(PathQos pathQos) {
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/createpathqos");
		requestInfo.setHttpMethod("POST");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		ObjectMapper mapper = new ObjectMapper();
		String pathQosAsJson = "";
		try {
			pathQosAsJson = mapper.writeValueAsString(pathQos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestInfo.setData(pathQosAsJson);
		String data =pullingManager.postFeedData(requestInfo, null);
	}
	public static int insertVideo(Video video) {
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/createvideo");
		requestInfo.setHttpMethod("POST");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		ObjectMapper mapper = new ObjectMapper();
		String videoAsJson = "";
		try {
			videoAsJson = mapper.writeValueAsString(video);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestInfo.setData(videoAsJson);
		String data =pullingManager.postFeedData(requestInfo, null);
		JSONObject josnObject = new JSONObject(data);
		return josnObject.getInt("maxId");
	}
	public static void updateVideo(Video video) {
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/updatevideo");
		requestInfo.setHttpMethod("POST");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		ObjectMapper mapper = new ObjectMapper();
		String videoAsJson = "";
		try {
			videoAsJson = mapper.writeValueAsString(video);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestInfo.setData(videoAsJson);
		String data =pullingManager.postFeedData(requestInfo, null);
		
	}
	public static void insertVideoTranslation(VideoTranslation videoTranslation) {
		PullingManager pullingManager = new PullingManager();
		videoTranslation.SystemRetrievedData = 	CharsUtil.replaceLatinChars(	videoTranslation.SystemRetrievedData);
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/createvideotranslation");
		requestInfo.setHttpMethod("POST");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		ObjectMapper mapper = new ObjectMapper();
		String videoTranslationAsJson = "";
		try {
			videoTranslationAsJson = mapper.writeValueAsString(videoTranslation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestInfo.setData(videoTranslationAsJson);
		String data =pullingManager.postFeedData(requestInfo, null);
	//	JSONObject josnObject = new JSONObject(data);
	//	return josnObject.getInt("maxId");
	}
	public static int insertProgram(Program program)
	{
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/createProgram");
		requestInfo.setHttpMethod("POST");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		ObjectMapper mapper = new ObjectMapper();
		String programAsJson = "";
		try {
			programAsJson = mapper.writeValueAsString(program);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestInfo.setData(programAsJson);
		String data =pullingManager.postFeedData(requestInfo, null);
		JSONObject josnObject = new JSONObject(data);
		return josnObject.getInt("ID");
	}
	public static void insertVideoMeasurementXref(VideoMeasurementXref videoMeasurementXref)
	{
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/createvideomeasurement");
		requestInfo.setHttpMethod("POST");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		ObjectMapper mapper = new ObjectMapper();
		String videoMeasurementXrefAsJson = "";
		try {
			videoMeasurementXrefAsJson = mapper.writeValueAsString(videoMeasurementXref);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestInfo.setData(videoMeasurementXrefAsJson);
		String data =pullingManager.postFeedData(requestInfo, null);
		
	}
	
	public static void insertSystemQos(SystemQos systemQos)
	{
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/createSystemQos");
		requestInfo.setHttpMethod("POST");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		ObjectMapper mapper = new ObjectMapper();
		String systemQosAsJson = "";
		try {
			systemQosAsJson = mapper.writeValueAsString(systemQos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestInfo.setData(systemQosAsJson);
		String data =pullingManager.postFeedData(requestInfo, null);
		
	}
	
	public static void insertVideoAttribute(VideoAttribute videoAttribute)
	{
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/createVideoAttribute");
		requestInfo.setHttpMethod("POST");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		ObjectMapper mapper = new ObjectMapper();
		String videoAttributesAsJson = "";
		try {
			videoAttributesAsJson = mapper.writeValueAsString(videoAttribute);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestInfo.setData(videoAttributesAsJson);
		String data =pullingManager.postFeedData(requestInfo, null);
		
	}

	public static Configs getConfigs() {
		Configs configs = new Configs();
		PullingManager pullingManager = new PullingManager();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHost(qosSiteHost);
		requestInfo.setRequestPath("/getConfig");
		requestInfo.setHttpMethod("GET");
		requestInfo.setIsHttps(false);
		requestInfo.setSignatureType("");
		String data = pullingManager.getFeedData(requestInfo, null);
		JSONObject configsObejct = new JSONObject(data);
		configs.MCPSystemName= configsObejct.getString("MCPSystemName");
		configs.FirstRunPreviousPeriod = configsObejct.getInt("FirstRunPreviousPeriod");
		configs.NumberOfAssetsToRun =  configsObejct.getInt("NumberOfAssetsToStartRun");
		configs.ScheduledDuration =  configsObejct.getInt("ScheduleDuration");
		configs.TransmissionHighDelay = configsObejct.getInt("TransmissionHighestDelay");
		configs.TransmissionMeduimDelay =  configsObejct.getInt("TransmissionMediumDelay");
		configs.TransmissionLowDelay =  configsObejct.getInt("TransmissionLowDelay");
		configs.TransmissionOptimumDelay = configsObejct.getInt("TransmissionOptimumDelay");
		return configs;
	}

}
