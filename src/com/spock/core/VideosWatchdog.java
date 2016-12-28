package com.spock.core;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Timer;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import entities.AkamaiFailedLink;
import entities.Attribute;
import entities.Configs;
import entities.Parameter;
import entities.Path;
import entities.PathQos;
import entities.Program;
import entities.Run;
import entities.SystemQos;
import entities.Video;
import entities.VideoMeasurementXref;
import entities.VideoTranslation;
import com.spock.utils.CSVParser;
import com.spock.utils.FeedDataHelper;
import com.spock.utils.JsonHelper;
import com.spock.utils.SignatureGenerator;
import com.spock.utils.XMLHelper;

public class VideosWatchdog {
	public static SyndicationChecker syndicationChecker = new SyndicationChecker();
	public static HashMap<String, HashMap<String, String>> content = new HashMap<String, HashMap<String, String>>();
	public static HashMap<String, Integer> pathAssetsCount = new HashMap<String, Integer>();
	static HashMap<String, String> programs = new HashMap<String, String>();
	static HashMap<String, String> programToTagId = new HashMap<String, String>();
	public static String mcpVideosList = System.getProperty("user.dir") + "/xml/mcpVideosList.xml";
	public static String mcpProgramsPath = System.getProperty("user.dir") + "/xml/mcpPrograms.xml";
	static long timestamp = unixtime();
	public static Date runStartedAt;
	static String startDate = "";
	static String endDate = "";
	static ArrayList<Path> paths = new ArrayList<Path>();
	static ArrayList<String> videoIds = new ArrayList<String>();

	public static Configs configs = new Configs();
	public static Run run = new Run();
	static HashMap<String, Float> SystemSyndicaytionOfSuccess = new HashMap<String, Float>();
	static HashMap<String, Float> SystemAttributeAccuracy = new HashMap<String, Float>();
	static HashMap<String, Float> SystemAdServer = new HashMap<String, Float>();
	static HashMap<String, Float> SystemTransmissionDelay = new HashMap<String, Float>();

	public static ArrayList<String> apps = new ArrayList<String>();
	public static entities.System mcpSystem = new entities.System();
	public static entities.System brightSpotSystem = new entities.System();
	public static TimeZone estTimeZone = TimeZone.getTimeZone("EST");

	public static String getCurrentApp(String parentTopic) {

		for (String app : apps) {
			String[] appNames = app.split("\\|");
			for (String appName : appNames) {
				if (appName.toLowerCase().equals(parentTopic.toLowerCase())) {
					return parentTopic.toLowerCase();
				}
			}

		}
		return null;

	}

	public static void main(String[] args) {

		configs = FeedDataHelper.getConfigs();
		Timer time = new Timer(); // Instantiate Timer Object
		ScheduledTask st = new ScheduledTask(); // Instantiate SheduledTask
												// class
		time.schedule(st, 0, 1000 * 60 * configs.ScheduledDuration); // Create
																		// Repetitively
																		// task
																		// for
		// every 1 secs

	}

	public static void resetData() {
		syndicationChecker = new SyndicationChecker();
		content = new HashMap<String, HashMap<String, String>>();
		pathAssetsCount = new HashMap<String, Integer>();
		programs = new HashMap<String, String>();
		programToTagId = new HashMap<String, String>();
		mcpProgramsPath = System.getProperty("user.dir") + "/xml/mcpPrograms.xml";
		mcpVideosList = System.getProperty("user.dir") + "/xml/mcpVideosList.xml";
		timestamp = unixtime();
		startDate = "";
		endDate = "";
		configs = FeedDataHelper.getConfigs();
		run = new Run();
		paths = new ArrayList<Path>();
		videoIds = new ArrayList<String>();
		SystemSyndicaytionOfSuccess = new HashMap<String, Float>();
		SystemAttributeAccuracy = new HashMap<String, Float>();
		SystemAdServer = new HashMap<String, Float>();
		SystemTransmissionDelay = new HashMap<String, Float>();
		apps = new ArrayList<String>();
		mcpSystem = new entities.System();
		brightSpotSystem = new entities.System();
	}

	public static void run() {
		/*
		 * Code that will ping all MCP ID's that have been syndicated every time
		 * interval which is configurable
		 */

		resetData();
		DateFormat runFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// runFormat.setTimeZone(estTimeZone);
		DateFormat runFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// runFormat1.setTimeZone(estTimeZone);
		runStartedAt = new Date();
		Run previousRun = getLastRun();
		paths = new ArrayList<Path>();
		DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss 'EDT'");
		DateFormat dateFormat2 = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss 'EDT'");
		// dateFormat.setTimeZone(estTimeZone);
		// get current date time with Date()
		DateTime dateTime = new DateTime();
		try {
			startDate = dateFormat.format(new DateTime(runFormat1.parse(previousRun.endDate)).toDate());
		} catch (Exception e) {
			System.out.println("Warining: could not parse previuos run end date");
			dateFormat.setTimeZone(estTimeZone);
			startDate = dateFormat.format(dateTime.minusHours(configs.FirstRunPreviousPeriod).toDate());
		}
		dateFormat.setTimeZone(estTimeZone);
		// startDate =
		// dateFormat.format(dateTime.minusHours(FirstRunPreviousPeriod).toDate());
		endDate = dateFormat.format(dateTime.toDate());
		timestamp = unixtime();
		System.out.println("start date:" + startDate + ", end date:" + endDate);
	//	startDate = "Oct 07, 2016 17:07:20 EDT";
	//	endDate = "Oct 07, 2016 18:07:20 EDT";
		try {
			run.startDate = runFormat.format(dateFormat2.parse(startDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//startDate = "Oct 07, 2016 17:07:20 EDT";
		//endDate = "Oct 07, 2016 18:07:20 EDT";
		run.QosValue = 0;
		run.endDate = "";
		run.totalAssets = 0;
		run.movingWeight = 0;
		paths = FeedDataHelper.getPaths();
		defineCurrentApps(paths);
		programToTagId = new HashMap<String, String>();
		programToTagId = CSVParser.parseCsvToProgramAndTagHash("mcp-mapping.csv");
		QosPathManager pathMng = new QosPathManager();
		QosPathSystemManager systemManeger = new QosPathSystemManager();
		QosCalculatorEngine qosCalculater = new QosCalculatorEngine(pathMng, systemManeger);
		brightSpotSystem = getSystemByName(paths, "brightspot");
		mcpSystem = getSystemByName(paths, configs.MCPSystemName);
		// Syndicator.syndicateVideo("3066281","10812");
		if (mcpSystem != null) {
			programs = getmcpPrograms();
			System.out.println("Programmes count:" + programs.size());
			timestamp = unixtime();
			// videoIds = new ArrayList<>();
			// videoIds.add("3178734");
			// checkLastRunFailedVideos();
			getmcpsyndicatedids();
			if (content.size() >= configs.NumberOfAssetsToRun) {
				run.Id = FeedDataHelper.insertRun(run);
				System.out.println(content.size());
			} else {
				System.out.println("run not started, asstes count is:" + content.size());
				return;
			}


			for (String key : content.keySet()) {
//				if(!key.equals("3243966")){
//					continue;
//					
//				}
				String programName = content.get(key).get("program_name");
				//Date publishDate = getDateFromString(content.get(key).get("published"), "MMM d,yyyy HH:mm:ss");
				if(!content.get(key).get("published").toString().toLowerCase().equals("yes") && !content.get(key).get("published").toString().toLowerCase().equals("true")){
					continue;
				}
				Date expirationDate = getDateFromString(content.get(key).get("ts_expire"), "MMM d,yyyy HH:mm:ss");
				if (expirationDate.before(new Date())) {
					System.out.println(expirationDate.toString() + " before " + (new Date().toString()));
					Video video = new Video();
					video.Title = content.get(key).get("def_title");
					video.Duration = content.get(key).get("duration");
					video.RunId = run.Id;
					if(content.get(key).get("syndicator_name").toLowerCase().trim().equals("brightspot 2"))
					{
						video.isBrightspot=1;
					}else{
						video.isBrightspot=0;
					}
					video.isExpired = true;
					try {
						video.McpID = Integer.parseInt(key);

					} catch (Exception e) {
						continue;
					}
					video.Id = FeedDataHelper.insertVideo(video);
					continue;
				}
				Date mcpDate = getDateFromString(content.get(key).get("syndication_date"), "MMM d,yyyy HH:mm:ss");

				String programId = programs.get(programName);
				String primaryTagId = programToTagId.get(programId);
				String parentTopicName = getParentTopicValue(primaryTagId);
				String app = getCurrentApp(parentTopicName);
				if (app == null) {
					continue;
				}
				Video video = new Video();
				video.Title = content.get(key).get("def_title");
				video.Duration = content.get(key).get("duration");
				video.RunId = run.Id;
				video.isExpired = false;
				if(content.get(key).get("syndicator_name").toLowerCase().trim().equals("brightspot 2"))
				{
					video.isBrightspot=1;
				}else{
					video.isBrightspot=0;
				}
				try {
					video.McpID = Integer.parseInt(key);
				} catch (Exception e) {
					continue;
				}
				video.Id = FeedDataHelper.insertVideo(video);
				System.out.println(app);
				Program program = new Program();
				program.Title = programName;
				program.Id = FeedDataHelper.insertProgram(program);

				int pathIndex = getPathIndexByAppName(paths, app);
				if (pathIndex > -1) {
					Path currentPath = paths.get(pathIndex);
					boolean assetSyndicationSuccess = true;
					ArrayList<Float> attributeAccuracy = new ArrayList<Float>();
					ArrayList<Float> transmisionDelayAccuracy = new ArrayList<Float>();
					if (pathAssetsCount.containsKey(currentPath.Name.toLowerCase())) {
						pathAssetsCount.put(currentPath.Name.toLowerCase(),
								pathAssetsCount.get(currentPath.Name.toLowerCase()) + 1);
					} else {
						pathAssetsCount.put(currentPath.Name.toLowerCase(), 1);
					}
					boolean adsSuccess = true;
					for (entities.System system : currentPath.Systems) {
						VideoTranslation videoTranslation = new VideoTranslation();
						videoTranslation.VideoId = video.Id;
						videoTranslation.SystemId = system.Id;
						if (!system.Name.toLowerCase().equals(configs.MCPSystemName)) {
							VideoMeasurementXref videoMeasurementXref = new VideoMeasurementXref();
							videoMeasurementXref.ProgramID = program.Id;
							videoMeasurementXref.VideoID = video.Id;
							videoMeasurementXref.PathId = currentPath.Id;
							videoMeasurementXref.RunID = run.Id;
							videoMeasurementXref.SystemId = system.Id;
							videoMeasurementXref.AttributeAccuracy = -1;
							videoMeasurementXref.TransmissionDelay = -1;
							videoMeasurementXref.AdServer = -1;
							videoMeasurementXref.SysndicationOfSuccess = -1;
							String syndicationDate = "";
							syndicationDate = runFormat.format(mcpDate);
							videoMeasurementXref.SyndicationDate = syndicationDate;
							String result = getSystemResult(system, key);
							videoTranslation.SystemRetrievedData = StringEscapeUtils.escapeJava(result);
							boolean isXML = false;
							if (system.responseType.toLowerCase().equals("xml")) {
								isXML = true;
							}
							if (system.Name.toLowerCase().equals("akamai")) {
								assetSyndicationSuccess = checkSuccessOfMcpAkamaiLinks(result, video.Id);
								if (assetSyndicationSuccess) {
									videoMeasurementXref.SysndicationOfSuccess = 1;
								} else {
									videoMeasurementXref.SysndicationOfSuccess = 0;
								}
								systemManeger.addAssetSyndicationSuccess(String.valueOf(system.Id),
										assetSyndicationSuccess);
							} else if (system.hasAdsServer && (content.get(key).get("syndicator_name").toLowerCase().trim().equals("brightspot 2") )) {
								adsSuccess = syndicationChecker.attributeHasValueInXml(result);
								if (adsSuccess) {
									videoMeasurementXref.AdServer = 1;
								} else {
									videoMeasurementXref.AdServer = 0;
								}
								if (!system.VideoIdPath.isEmpty()) {
									try {
										videoTranslation.SystemVideoId = XMLHelper.getXmlValueByPath(result,
												system.VideoIdPath);
									} catch (Exception ex) {
										System.out.println("could not parse video id path: " + system.VideoIdPath
												+ " for system " + system.Name);
									}
								}
								systemManeger.addAdsSuccess(String.valueOf(system.Id), adsSuccess);
							} else if (system.hasSyndicationOfSuccess) {
								JSONObject resultAsJson = syndicationChecker.jsonResponseHasValues(result);
								if (resultAsJson == null) {
									System.out.println("######## key:" + key + ",, result:" + result);
									assetSyndicationSuccess = false;
									videoMeasurementXref.SysndicationOfSuccess = 0;
									systemManeger.addAssetSyndicationSuccess(String.valueOf(system.Id), false);
								} else if (system.Name.toLowerCase().equals("brightspot")
										&& (content.get(key).get("syndicator_name").toLowerCase().trim().equals("brightspot 2") )
										&& !checkBrightspotSyndication(video.Id, resultAsJson, brightSpotSystem,
												videoMeasurementXref)) {
									System.out.println("######## key:" + key + ",, result:" + result);
									assetSyndicationSuccess = false;
									videoMeasurementXref.SysndicationOfSuccess = 0;
									systemManeger.addAssetSyndicationSuccess(String.valueOf(system.Id), false);
								} else {
									if (system.Name.toLowerCase().equals("brightspot")) {
										if(!content.get(key).get("syndicator_name").toLowerCase().trim().equals("brightspot 2") ){
											continue;	
										}
										String frontEndUrl = JsonHelper.getJsonValueByPath(resultAsJson,
												"data|items|url");
										video.FrontEndLink = frontEndUrl;
										FeedDataHelper.updateVideo(video);
									}
									systemManeger.addAssetSyndicationSuccess(String.valueOf(system.Id), true);
									videoMeasurementXref.SysndicationOfSuccess = 1;
									if (!system.VideoIdPath.isEmpty()) {
										try {
											videoTranslation.SystemVideoId = JsonHelper.getJsonValueByPath(resultAsJson,
													system.VideoIdPath);
										} catch (Exception ex) {
											System.out.println("could not parse video id path: " + system.VideoIdPath
													+ " for system " + system.Name);
										}
									}
									if (system.hasAttributeAcuraccy) {
										HashMap<String, String> attributePaths = getAttributesAsHashmap(
												system.Attributes);
										HashMap<String, String> mcpAttributeMap = getMcpToOtherSystemAttributeMap(
												mcpSystem.Attributes, system.Attributes, false);
										HashMap<String, String> attributesToCompare = syndicationChecker
												.jsonAttributes(resultAsJson, attributePaths);
										float assetAttributeAccuracy = syndicationChecker.CompareAttributes(
												content.get(key), attributesToCompare, mcpAttributeMap, mcpSystem,
												system, video.Id);
										attributeAccuracy.add(assetAttributeAccuracy);
										videoMeasurementXref.AttributeAccuracy = assetAttributeAccuracy;
										systemManeger.addAttributeAccuracy(String.valueOf(system.Id),
												assetAttributeAccuracy);
									}
									if (system.hasTransmissionDelay) {
										String strPubDate = JsonHelper.getJsonValueByPath(resultAsJson,
												system.DateFieldPath);
										Date pubDate = getDateFromString(strPubDate, system.DateFormat);
										float transmissionDelay = syndicationChecker
												.assetTansmissionDelayAccuracy(mcpDate, pubDate);
										transmisionDelayAccuracy.add(transmissionDelay);
										videoMeasurementXref.TransmissionDelay = transmissionDelay;
										systemManeger.addTransmisionDelay(String.valueOf(system.Id), transmissionDelay);
									}
								}
							}
							if((system.Name.equalsIgnoreCase("akamai") || (content.get(key).get("syndicator_name").toLowerCase().trim().equals("brightspot 2") )) ){
								FeedDataHelper.insertVideoTranslation(videoTranslation);
						
								FeedDataHelper.insertVideoMeasurementXref(videoMeasurementXref);
							}
						}
					}
					VideoTranslation mcpVideoTranslation = new VideoTranslation();
					mcpVideoTranslation.VideoId = video.Id;
					mcpVideoTranslation.SystemId = mcpSystem.Id;
					mcpVideoTranslation.SystemVideoId = key;
					try {
						mcpVideoTranslation.SystemRetrievedData = QosPathSystemManager.getMcpVideoById(mcpSystem, key);
					} catch (Exception ex) {
						mcpVideoTranslation.SystemRetrievedData = QosPathSystemManager.getMcpVideoById(mcpSystem, key);
					}
					FeedDataHelper.insertVideoTranslation(mcpVideoTranslation);
					pathMng.addAssetSyndicationSuccess(currentPath.Name.toLowerCase(), assetSyndicationSuccess);
					pathMng.addAdsSuccess(currentPath.Name.toLowerCase(), adsSuccess);
					float pathAssetAttributeAccuracy = qosCalculater.averageToListOfIntegers(attributeAccuracy);
					if (pathAssetAttributeAccuracy > -1) {
						pathMng.addAttributeAccuracy(currentPath.Name.toLowerCase(), pathAssetAttributeAccuracy);
					}
					float pathAssetTransmisionDelayAccuracy = qosCalculater
							.averageToListOfIntegers(transmisionDelayAccuracy);
					if (pathAssetTransmisionDelayAccuracy > -1) {
						pathMng.addTransmisionDelay(currentPath.Name.toLowerCase(), pathAssetTransmisionDelayAccuracy);
					}
				}
			}

			float qosValue = qosCalculater.CalculateQOS(paths);
			System.out.println("qos value =" + qosValue);
			try {
				run.endDate = runFormat.format(dateFormat2.parse(endDate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			run.QosValue = qosValue;
			run.totalAssets = qosCalculater.getTotalAssetsCount();
			run.movingWeight = qosCalculater.calculateMovingWeight(run, previousRun);
			System.out.println("run.movingWeight =" + run.movingWeight);
			videoIds = FeedDataHelper.getLastRunFailedVideos();
			if (run.totalAssets > 0) {
				FeedDataHelper.updateRun(run);
			} else {

			}
			qosCalculater.calculatePathSystemQos(run.Id);
			for (SystemQos systemQos : systemManeger.systemQos) {
				FeedDataHelper.insertSystemQos(systemQos);
			}
			for (String key : pathMng.pathQos.keySet()) {
				PathQos pathQos = pathMng.pathQos.get(key);
				pathQos.PathID = getPathId(paths, pathQos.PathName);
				pathQos.RunID = run.Id;
				pathQos.TotalPathAssets = pathAssetsCount.get(key);
				FeedDataHelper.insertPathQos(pathQos);
			}
		} else {
			System.out.println("Could not find mcp system in the current paths...");
		}
		System.out.println("check last run videos");		
		checkLastRunFailedVideos();
		System.out.println("finished last run videos check");
	}

	public static int getPathId(ArrayList<Path> paths, String pathName) {
		for (Path path : paths) {
			if (path.Name.toLowerCase().equals(pathName.toLowerCase())) {
				return path.Id;
			}
		}
		return 0;
	}

	public static void defineCurrentApps(ArrayList<Path> paths) {
		for (Path path : paths) {
			apps.add(path.AppName);
		}
	}

	public static entities.System getSystemByName(ArrayList<Path> paths, String systemName) {
		for (Path path : paths) {
			for (entities.System system : path.Systems) {
				if (system.Name.toLowerCase().equals(systemName.toLowerCase())) {
					return system;
				}
			}
		}
		return null;
	}

	public static HashMap<String, String> getMcpToOtherSystemAttributeMap(ArrayList<Attribute> mcpAttributes,
			ArrayList<Attribute> otherSystemAttributes, boolean isMcpList) {
		HashMap<String, String> mcpMap = new HashMap<String, String>();
		for (Attribute attr : otherSystemAttributes) {
			Attribute currMcpAttribute = new Attribute();
			for (Attribute mcpAttr : mcpAttributes) {
				if (attr.BaseId == mcpAttr.BaseId) {
					currMcpAttribute = mcpAttr;
				}
			}
			String mcpName = currMcpAttribute.Name;
			if (isMcpList) {
				mcpName = currMcpAttribute.Name.replace("def_", "");
			}
			mcpMap.put(mcpName, attr.Name);
		}
		return mcpMap;
	}

	public static HashMap<String, String> getAttributesAsHashmap(ArrayList<Attribute> attributes) {
		HashMap<String, String> attrbiutesHash = new HashMap<String, String>();
		for (Attribute attr : attributes) {
			attrbiutesHash.put(attr.Name, attr.path);
		}
		return attrbiutesHash;
	}

	public static String getSystemResult(entities.System system, String mcpId) {
		long largeTimeStamp = unixtime() + 1209600;
		HashMap<String, String> parameters = new HashMap<String, String>();
		int signitureIndex = -1;
		String clientID = "";
		for (int index = 0; index < system.Parameters.size(); index++) {
			if (system.Parameters.get(index).Type.toLowerCase().equals("param")) {
				if (system.Parameters.get(index).Name.toLowerCase().equals("client_id")) {
					clientID = system.Parameters.get(index).Value;
				}
				if (system.Parameters.get(index).Value.equals("TimeStampValue")) {
					parameters.put(system.Parameters.get(index).Name, Long.toString(timestamp));
				} else if (system.Parameters.get(index).Value.equals("LargeTimeStamp")) {
					parameters.put(system.Parameters.get(index).Name, Long.toString(largeTimeStamp));
				} else if (system.Parameters.get(index).Value.equals("SignitureValue")) {
					signitureIndex = index;
				} else {
					parameters.put(system.Parameters.get(index).Name, system.Parameters.get(index).Value);
				}
			}
		}

		if (!system.ContentIdParamName.trim().equals("") && !system.ContentIdParamName.trim().equals("undefined")) {
			parameters.put(system.ContentIdParamName, mcpId);
		}
		HashMap<String, String> headers = new HashMap<String, String>();
		for (int index = 0; index < system.Parameters.size(); index++) {
			if (system.Parameters.get(index).Type.toLowerCase().equals("header")) {

				headers.put(system.Parameters.get(index).Name, system.Parameters.get(index).Value);
			}
		}
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHttpMethod(system.HttpMethod.toUpperCase());
		requestInfo.setIsHttps(system.IsHttps);
		if (system.ContentIdParamName.trim().equals("") || system.ContentIdParamName.trim().equals("undefined")) {
			requestInfo.setRequestPath(system.RequestPath + mcpId);
		} else {
			requestInfo.setRequestPath(system.RequestPath);
		}
		requestInfo.setHost(system.Host);
		requestInfo.setParameters(parameters);
		requestInfo.setHeaders(headers);
		requestInfo.setData(system.Data.replace("$$startDate$$", startDate).replace("$$endDate$$", endDate));
		requestInfo.setApiKey(system.ApiKey);
		requestInfo.setSecretKey(system.SecretKey);
		requestInfo.setSignatureType(system.SignitureType.trim());
		String signiture = "";
		if (system.SignitureType.trim().equals("SHA-1")) {
			requestInfo.setTextToSign(system.HttpMethod.toUpperCase() + clientID + requestInfo.getRequestPath() + "?"
					+ SignatureGenerator.concatenateParams(parameters, "&") + system.SecretKey);
			signiture = requestInfo.getSignature();
		} else if (system.SignitureType.trim().equals("SHA-256")) {
			requestInfo.setTextToSign(
					requestInfo.getSecretKey() + requestInfo.getHttpMethod() + requestInfo.getRequestPath());
			signiture = requestInfo.getSignature();
		} else if (system.SignitureType.trim().equals("HmacSHA256")) {
			signiture = SignatureGenerator.generatebase64(system.Data, system.SecretKey, timestamp, startDate, endDate,
					false);
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
			result = pullingManager.getFeedData(requestInfo, signitureParameter);
		} else {
			result = pullingManager.postFeedData(requestInfo, signitureParameter);
		}

		return result;
	}

	public static int getPathIndexByAppName(ArrayList<Path> paths, String appName) {

		for (int index = 0; index < paths.size(); index++) {
			String[] apps = paths.get(index).AppName.split("\\|");
			for (String app : apps) {
				if (app.toLowerCase().equals(appName.toLowerCase())) {
					return index;
				}
			}

		}
		return -1;
	}

	public class listwatcher extends Thread {
		public void run() {
			/*
			 * Code that checks for the ArrayList/DB and looks for rows that
			 * have one or more failures that need to be reverified
			 */
		}

	}

	public static String encodeURIComponent(String s) {
		String result = null;

		try {
			result = URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20").replaceAll("\\%21", "!")
					.replaceAll("\\%27", "'").replaceAll("\\%28", "(").replaceAll("\\%29", ")")
					.replaceAll("\\%7E", "~");
		}

		// This exception should never occur.
		catch (UnsupportedEncodingException e) {
			result = s;
		}

		return result;
	}

	public static void getmcpsyndicatedids() {

		int numberOfIterations = 1;
		for (int i = 0; i < numberOfIterations; i++) {
			// System.out.println("Getting mcp id's number of iterations:" +
			// numberOfIterations + ", current iteration:" +i);
			long largeTimeStamp = unixtime() + 1209600;
			HashMap<String, String> parameters = new HashMap<String, String>();
			int signitureIndex = -1;
			for (int index = 0; index < mcpSystem.Parameters.size(); index++) {
				if (mcpSystem.Parameters.get(index).Type.toLowerCase().equals("param")) {
					if (mcpSystem.Parameters.get(index).Value.equals("TimeStampValue")) {
						parameters.put(mcpSystem.Parameters.get(index).Name, Long.toString(timestamp));
					} else if (mcpSystem.Parameters.get(index).Value.equals("LargeTimeStamp")) {
						parameters.put(mcpSystem.Parameters.get(index).Name, Long.toString(largeTimeStamp));
					} else if (mcpSystem.Parameters.get(index).Value.equals("SignitureValue")) {
						signitureIndex = index;
					} else if (mcpSystem.Parameters.get(index).Value.equals("CounterValue")) {
						parameters.put(mcpSystem.Parameters.get(index).Name, String.valueOf((i + 1)));
					} else {
						parameters.put(mcpSystem.Parameters.get(index).Name, mcpSystem.Parameters.get(index).Value);
					}
				}
			}

			SignatureGenerator.signrequest(mcpSystem.Data, timestamp);
			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Content-type", "text/xml; charset=ISO-8859-1");
			RequestInfo requestInfo = new RequestInfo();
			requestInfo.setHttpMethod("POST");
			requestInfo.setApiKey(mcpSystem.ApiKey);
			requestInfo.setSecretKey(mcpSystem.SecretKey);
			requestInfo.setData(mcpSystem.Data.replace("$$startDate$$", startDate).replace("$$endDate$$", endDate));
			requestInfo.setIsHttps(mcpSystem.IsHttps);
			requestInfo.setRequestPath(mcpSystem.RequestPath);
			requestInfo.setHost(mcpSystem.Host);
			requestInfo.setParameters(parameters);
			requestInfo.setHeaders(headers);
			requestInfo.setSignatureType("");
			Parameter signitureParameter = new Parameter();
			if (signitureIndex > -1) {
				signitureParameter.Name = mcpSystem.Parameters.get(signitureIndex).Name;
				signitureParameter.Value = SignatureGenerator.generatebase64(mcpSystem.Data, mcpSystem.SecretKey,
						timestamp, startDate, endDate, false);
			} else {
				signitureParameter = null;
			}
			PullingManager pm = new PullingManager();
			Document doc = null;
			try {

				doc = XMLHelper.XMLfromString(pm.postFeedData(requestInfo, signitureParameter));

			} catch (Exception ex) {
				System.out.println("i has changed from:" + i);
				i = i - 1;
				System.out.println("i has changed to:" + i);
				continue;
			}
			if (numberOfIterations == 1) {
				if (doc.getElementsByTagName("num_pages") == null
						|| doc.getElementsByTagName("num_pages").item(0) == null) {
					return;
				}
				numberOfIterations = Integer.parseInt(doc.getElementsByTagName("num_pages").item(0).getTextContent());
			}
			NodeList nlist = doc.getElementsByTagName("item");

			for (int x = 0; x < nlist.getLength(); x++) {
				Element e = (Element) nlist.item(x);
				String completedDate = e.getElementsByTagName("completed_date").item(0).getTextContent();
				boolean isDateInRange = false;
				if (!completedDate.equals("")) {
					// Jul 13, 2015 02:27:55 EDT
					DateFormat mcpFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss 'EST'");
					DateFormat mcpFormatter2 = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss 'EDT'");
					try {
						Date datetime;
						try {
							datetime = (Date) mcpFormatter.parse(completedDate);
						} catch (Exception ex) {
							datetime = (Date) mcpFormatter2.parse(completedDate);
						}
						Date startDateValue;
						try {
							startDateValue = ((Date) mcpFormatter2.parse(startDate));
						} catch (Exception ex2) {
							startDateValue = ((Date) mcpFormatter.parse(startDate));
						}
						Date endDateValue;
						try {
							endDateValue = ((Date) mcpFormatter2.parse(endDate));
						} catch (Exception ex3) {
							endDateValue = ((Date) mcpFormatter.parse(endDate));
						}
						if (datetime.after(startDateValue) && datetime.before(endDateValue)) {
							isDateInRange = true;
						}
					} catch (ParseException e1) {
						isDateInRange = false;
					}
				}
				if (isDateInRange) {
					String mcpId = e.getElementsByTagName("upload_id").item(0).getTextContent();
					if (!content.containsKey(mcpId)) {
						NodeList childNodes = nlist.item(x).getChildNodes();
						HashMap<String, String> childContents = new HashMap<String, String>();

						for (int childNodesIndex = 0; childNodesIndex < childNodes.getLength(); childNodesIndex++) {
							Node childNode = childNodes.item(childNodesIndex);

							childContents.put(childNode.getNodeName(), childNode.getTextContent());
						}
					
							content.put(mcpId, childContents);
					
					}
				}
			}

		}
	}

	public static HashMap<String, String> getmcpPrograms() {
		HashMap<String, String> programs = new HashMap<String, String>();
		boolean notPassed = true;
		String data = "";
		while (notPassed) {
			try {
				int numberOfIterations = 1;
				for (int i = 0; i < numberOfIterations; i++) {

					long largeTimeStamp = unixtime() + 1209600;
					HashMap<String, String> parameters = new HashMap<String, String>();
					int signitureIndex = -1;
					for (int index = 0; index < mcpSystem.Parameters.size(); index++) {
						if (mcpSystem.Parameters.get(index).Type.toLowerCase().equals("param")) {

							if (mcpSystem.Parameters.get(index).Value.equals("TimeStampValue")) {
								parameters.put(mcpSystem.Parameters.get(index).Name, Long.toString(timestamp));
							} else if (mcpSystem.Parameters.get(index).Value.equals("LargeTimeStamp")) {
								parameters.put(mcpSystem.Parameters.get(index).Name, Long.toString(largeTimeStamp));
							} else if (mcpSystem.Parameters.get(index).Value.equals("SignitureValue")) {
								signitureIndex = index;
							} else if (mcpSystem.Parameters.get(index).Value.equals("CounterValue")) {
								parameters.put(mcpSystem.Parameters.get(index).Name, String.valueOf((i + 1)));
							} else {
								parameters.put(mcpSystem.Parameters.get(index).Name,
										mcpSystem.Parameters.get(index).Value);
							}
						}
					}
					String strXMLFilename = mcpProgramsPath;
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
						signitureParameter.Name = mcpSystem.Parameters.get(signitureIndex).Name;
						signitureParameter.Value = SignatureGenerator.generatebase64(mcpProgramsPath,
								mcpSystem.SecretKey, timestamp, startDate, endDate, true);
					} else {
						signitureParameter = null;
					}
					long expiration_time = unixtime() + 1209600;
					PullingManager pm = new PullingManager();
					Document doc = null;
					try {
						data = pm.postFeedData(requestInfo, signitureParameter);
						doc = XMLHelper.XMLfromString(data);

					} catch (Exception ex) {
						System.out.println("i has changed from:" + i);
						i = i - 1;
						System.out.println("i has changed to:" + i);
						continue;
					}
					if (numberOfIterations == 1) {
						numberOfIterations = Integer
								.parseInt(doc.getElementsByTagName("num_pages").item(0).getTextContent());
					}
					NodeList nlist = doc.getElementsByTagName("program");

					for (int x = 0; x < nlist.getLength(); x++) {
						Element e = (Element) nlist.item(x);
						String programId = e.getElementsByTagName("program_id").item(0).getTextContent();
						programs.put(e.getElementsByTagName("program_title").item(0).getTextContent(), programId);
					}

				}
				notPassed = false;
			} catch (Exception ex) {
				System.out.println(data);
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ex.printStackTrace();
			}
		}
		return programs;
	}

	public static boolean checkSuccessOfMcpAkamaiLinks(String response, int videoID) {
		Document doc = null;
		try {

			doc = doc = XMLHelper.XMLfromString(response);
		} catch (Exception ex) {
		}
		if (doc != null) {
			NodeList nlist = doc.getElementsByTagName("video_published_url");
			for (int x = 0; x < nlist.getLength(); x++) {
				Element element = (Element) nlist.item(x);
				String videoFormat = element.getElementsByTagName("format").item(0).getTextContent();
				String videoUrl = element.getElementsByTagName("suburl").item(0).getTextContent();
				videoFormat = videoFormat.replace("-mp", "").replace("-", "").replace("bp", "").replace("hpv3", "");
				try {
					HttpURLConnection.setFollowRedirects(false);
					HttpURLConnection con = (HttpURLConnection) new URL(
							"http://h.univision.com/media" + videoUrl + "." + videoFormat).openConnection();
					con.setRequestMethod("HEAD");
					if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
						AkamaiFailedLink failedLink = new AkamaiFailedLink();
						failedLink.videoId = videoID;
						failedLink.AkamaiLink = "http://h.univision.com/media" + videoUrl + "." + videoFormat;
						failedLink.typeId = 1;
						FeedDataHelper.insertFailedAkmaiLinkRun(failedLink);
						return false;
					}
				} catch (Exception e) {
					AkamaiFailedLink failedLink = new AkamaiFailedLink();
					failedLink.videoId = videoID;
					failedLink.AkamaiLink = "http://h.univision.com/media" + videoUrl + "." + videoFormat;
					failedLink.typeId = 1;
					FeedDataHelper.insertFailedAkmaiLinkRun(failedLink);
					return false;
				}
			}
		} else {
			AkamaiFailedLink failedLink = new AkamaiFailedLink();
			failedLink.videoId = videoID;
			failedLink.AkamaiLink = "No Data Retrieved";
			failedLink.typeId = 1;
			FeedDataHelper.insertFailedAkmaiLinkRun(failedLink);
			return false;
		}
		return true;
	}

	public static ArrayList<Integer> parsemcpresponse(String response) {
		ArrayList<Integer> idreturnlist = new ArrayList();
		return idreturnlist;
	}

	public static String getParentTopicValue(String primaryTagId) {
		try {
			String restMethod = "GET";
			String client_id = brightSpotSystem.ApiKey;
			String signature = "";
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet httpget = null;
			String secret_key = brightSpotSystem.SecretKey;
			HashMap<String, String> parameters = new HashMap<String, String>();
			parameters.put("client_id", client_id);
			HashMap<String, String> headers = new HashMap<String, String>();
			for (int index = 0; index < brightSpotSystem.Parameters.size(); index++) {
				if (brightSpotSystem.Parameters.get(index).Type.toLowerCase().equals("header")) {
					headers.put(brightSpotSystem.Parameters.get(index).Name,
							brightSpotSystem.Parameters.get(index).Value);
				}
			}
			String urlPath = "/feed/taxonomy/" + primaryTagId + "?client_id=" + client_id;

			RequestInfo requestInfo = new RequestInfo();
			requestInfo.setHttpMethod(restMethod);
			requestInfo.setIsHttps(false);
			requestInfo.setRequestPath("/feed/taxonomy/" + primaryTagId);
			requestInfo.setHost(brightSpotSystem.Host);
			requestInfo.setParameters(parameters);
			requestInfo.setHeaders(headers);

			requestInfo.setTextToSign(restMethod + client_id + urlPath + secret_key);
			requestInfo.setSignatureType("SHA-1");

			Parameter signitureParameter = new Parameter();
			signitureParameter.Name = "signature";
			signitureParameter.Value = requestInfo.getSignature();
			PullingManager pm = new PullingManager();
			String response = pm.getFeedData(requestInfo, signitureParameter);

			JSONObject jsonObject = new JSONObject(response);

			return jsonObject.getJSONObject("data").getString("primaryTopicName");
		} catch (Exception ex) {
			return "";
		}

	}

	public static Date getDateFromString(String strDate, String format) {
		DateFormat formatter = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = (Date) formatter.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static long unixtime() {
		long unixTime = System.currentTimeMillis() / 1000L;

		return unixTime;
	}

	public static Run getLastRun() {
		Run LastRun = null;
		int maxId = -1;
		for (Run run : FeedDataHelper.getRuns()) {
			if (maxId == -1) {
				maxId = run.Id;
				LastRun = run;
			} else if (run.Id > maxId) {
				maxId = run.Id;
				LastRun = run;
			}

		}
		return LastRun;
	}

	public static void checkLastRunFailedVideos() {
		System.out.println("Last run videos:" + videoIds.size());
		HashMap<String, HashMap<String, String>> videos = getmcpVideosByIds(videoIds);
		for (String key : videos.keySet()) {
			String programId = videos.get(key).get("program_id");
//			try{
//			Date publishDate = getDateFromString(videos.get(key).get("ts_upload_publish"), "MMM d,yyyy HH:mm:ss");
//			if(publishDate.after(new Date())){
//				continue;
//			}
//			}
//			catch(Exception ex){
//				
//			}
//			
			Date expirationDate = getDateFromString(videos.get(key).get("ts_expire"), "MMM d,yyyy HH:mm:ss");
			if (expirationDate.before(new Date())) {
				System.out.println(expirationDate.toString() + " before " + (new Date().toString()));
				Video video = new Video();
				video.Title = videos.get(key).get("title");
				video.Duration = videos.get(key).get("duration");
				video.RunId = run.Id;
				video.isExpired = true;
				video.isBrightspot = FeedDataHelper.isVideoSyndicatedToBrightspot(Integer.parseInt(key));
				try {
					video.McpID = Integer.parseInt(key);

				} catch (Exception e) {
					continue;
				}
				video.Id = FeedDataHelper.insertVideo(video);
				continue;
			}
			Video video = new Video();
			video.Title = videos.get(key).get("title");
			video.Duration = videos.get(key).get("duration");
			video.RunId = run.Id;
			video.isRerun = true;
			video.isExpired = false;
			try {
				video.McpID = Integer.parseInt(key);

			} catch (Exception e) {
				continue;
			}
			video.Id = FeedDataHelper.insertVideo(video);

			Date mcpDate = getDateFromString(videos.get(key).get("ts_upload_publish"), "MMM d,yyyy HH:mm:ss");

			String programName = getKeyByValueForHash(programs, programId);// .get(programName);
			String primaryTagId = programToTagId.get(programId);
			String parentTopicName = getParentTopicValue(primaryTagId);
			String app = getCurrentApp(parentTopicName);
			System.out.println(app);
			Program program = new Program();
			program.Title = programName;
			program.Id = FeedDataHelper.insertProgram(program);

			int pathIndex = getPathIndexByAppName(paths, app);
			if (pathIndex > -1) {
				Path currentPath = paths.get(pathIndex);
				boolean assetSyndicationSuccess = true;
				ArrayList<Float> attributeAccuracy = new ArrayList<Float>();
				ArrayList<Float> transmisionDelayAccuracy = new ArrayList<Float>();
				if (pathAssetsCount.containsKey(currentPath.Name.toLowerCase())) {
					pathAssetsCount.put(currentPath.Name.toLowerCase(),
							pathAssetsCount.get(currentPath.Name.toLowerCase()) + 1);
				} else {
					pathAssetsCount.put(currentPath.Name.toLowerCase(), 1);
				}
				boolean adsSuccess = true;

				for (entities.System system : currentPath.Systems) {
					VideoTranslation videoTranslation = new VideoTranslation();
					videoTranslation.VideoId = video.Id;
					videoTranslation.SystemId = system.Id;
					if (!system.Name.toLowerCase().equals(configs.MCPSystemName)) {
						VideoMeasurementXref videoMeasurementXref = new VideoMeasurementXref();
						videoMeasurementXref.ProgramID = program.Id;
						videoMeasurementXref.VideoID = video.Id;
						videoMeasurementXref.PathId = currentPath.Id;
						videoMeasurementXref.RunID = run.Id;
						videoMeasurementXref.SystemId = system.Id;
						videoMeasurementXref.AttributeAccuracy = -1;
						videoMeasurementXref.TransmissionDelay = -1;
						videoMeasurementXref.AdServer = -1;
						videoMeasurementXref.SysndicationOfSuccess = -1;
						String result = getSystemResult(system, key);
						videoTranslation.SystemRetrievedData = StringEscapeUtils.escapeJava(result);
						boolean isXML = false;
						if (system.responseType.toLowerCase().equals("xml")) {
							isXML = true;

						}
						if (system.Name.toLowerCase().equals("akamai")) {
							assetSyndicationSuccess = checkSuccessOfMcpAkamaiLinks(result, video.Id);
							if (assetSyndicationSuccess) {
								videoMeasurementXref.SysndicationOfSuccess = 1;

							} else {
								videoMeasurementXref.SysndicationOfSuccess = 0;
							}
							// systemManeger.addAssetSyndicationSuccess(String.valueOf(system.Id)
							// , assetSyndicationSuccess);
						} else if (system.hasAdsServer && video.isBrightspot==1) {
							adsSuccess = syndicationChecker.attributeHasValueInXml(result);
							if (adsSuccess) {
								videoMeasurementXref.AdServer = 1;
							} else {
								videoMeasurementXref.AdServer = 0;
								if (FeedDataHelper.getAdServerFailedCount(key) > 1) {
									String bsResult = "";
									JSONObject bsResultAsJson = null;
									try {
										bsResult = getSystemResult(brightSpotSystem, key);
										bsResultAsJson = syndicationChecker.jsonResponseHasValues(bsResult);
									} catch (Exception ex) {

									}
									String bsFrontendUrl = "";
									try {
										bsFrontendUrl = bsResultAsJson.getJSONObject("data").getJSONArray("items")
												.getJSONObject(0).getString("url");
									} catch (Exception ex) {
									}
									String bsId = "";
									try {
										bsId = bsResultAsJson.getJSONObject("data").getJSONArray("items")
												.getJSONObject(0).getString("uid");
									} catch (Exception ex) {
									}
									sendVideoFailureEmail(key, bsFrontendUrl, bsId);
								}
							}
							// systemManeger.addAdsSuccess(String.valueOf(system.Id)
							// , adsSuccess);
						} else if (system.hasSyndicationOfSuccess) {

							JSONObject resultAsJson = syndicationChecker.jsonResponseHasValues(result);
							if (resultAsJson == null) {
								System.out.println("######## key:" + key + ",, result:" + result);
								assetSyndicationSuccess = false;
								videoMeasurementXref.SysndicationOfSuccess = 0;
								// systemManeger.addAssetSyndicationSuccess(String.valueOf(system.Id)
								// , false);
							} else if (system.Name.toLowerCase().equals("brightspot") && video.isBrightspot==1
									&& !checkBrightspotSyndication(video.Id, resultAsJson, brightSpotSystem,
											videoMeasurementXref)) {
								System.out.println("######## key:" + key + ",, result:" + result);
								assetSyndicationSuccess = false;
								videoMeasurementXref.SysndicationOfSuccess = 0;
								// systemManeger.addAssetSyndicationSuccess(String.valueOf(system.Id),
								// false);
							} else {
								if (system.Name.toLowerCase().equals("brightspot")) {
									if(!(video.isBrightspot ==1) ){
										continue;	
									}
									String frontEndUrl = JsonHelper.getJsonValueByPath(resultAsJson, "data|items|url");
									video.FrontEndLink = frontEndUrl;
									FeedDataHelper.updateVideo(video);
								}

								// systemManeger.addAssetSyndicationSuccess(String.valueOf(system.Id)
								// , true);
								videoMeasurementXref.SysndicationOfSuccess = 1;
								if (!system.VideoIdPath.isEmpty()) {
									try {

										videoTranslation.SystemVideoId = JsonHelper.getJsonValueByPath(resultAsJson,
												system.VideoIdPath);
									} catch (Exception ex) {
										System.out.println("could not parse video id path: " + system.VideoIdPath
												+ " for system " + system.Name);
									}
								}
								if (system.hasAttributeAcuraccy) {
									HashMap<String, String> attributePaths = getAttributesAsHashmap(system.Attributes);

									HashMap<String, String> mcpAttributeMap = getMcpToOtherSystemAttributeMap(
											mcpSystem.Attributes, system.Attributes, true);
									HashMap<String, String> attributesToCompare = syndicationChecker
											.jsonAttributes(resultAsJson, attributePaths);
									float assetAttributeAccuracy = syndicationChecker.CompareAttributes(videos.get(key),
											attributesToCompare, mcpAttributeMap, mcpSystem, system, video.Id);
									attributeAccuracy.add(assetAttributeAccuracy);
									videoMeasurementXref.AttributeAccuracy = assetAttributeAccuracy;
									// systemManeger.addAttributeAccuracy(String.valueOf(system.Id)
									// , assetAttributeAccuracy);
								}
								if (system.hasTransmissionDelay) {
									String strPubDate = JsonHelper.getJsonValueByPath(resultAsJson,
											system.DateFieldPath);
									Date pubDate = getDateFromString(strPubDate, system.DateFormat);
									float transmissionDelay = syndicationChecker.assetTansmissionDelayAccuracy(mcpDate,
											pubDate);
									transmisionDelayAccuracy.add(transmissionDelay);
									videoMeasurementXref.TransmissionDelay = transmissionDelay;
									// systemManeger.addTransmisionDelay(String.valueOf(system.Id),
									// transmissionDelay);
								}
							}
						}
					if(video.isBrightspot==1 || system.Name.equalsIgnoreCase("akamai")){
							FeedDataHelper.insertVideoTranslation(videoTranslation);
							FeedDataHelper.insertVideoMeasurementXref(videoMeasurementXref);
					}
					}
				}
				VideoTranslation mcpVideoTranslation = new VideoTranslation();
				mcpVideoTranslation.VideoId = video.Id;
				mcpVideoTranslation.SystemId = mcpSystem.Id;
				mcpVideoTranslation.SystemVideoId = key;
				FeedDataHelper.insertVideoTranslation(mcpVideoTranslation);
			}
		}

	}

	public static String getKeyByValueForHash(HashMap<String, String> table, String value) {
		String key_you_look_for = "";
		Iterator<Map.Entry<String, String>> iter = table.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = iter.next();
			if (entry.getValue().equals(value)) {
				key_you_look_for = entry.getKey();
			}
		}
		return key_you_look_for;
	}

	public static HashMap<String, HashMap<String, String>> getmcpVideosByIds(ArrayList<String> videoIds) {
		HashMap<String, HashMap<String, String>> videos = new HashMap<String, HashMap<String, String>>();
		for (int i = 0; i < videoIds.size(); i++) {
			Document doc = null;
			try {
				doc = XMLHelper.XMLfromString(QosPathSystemManager.getMcpVideoById(mcpSystem, videoIds.get(i)));
			} catch (Exception ex) {
				System.out.println("i has changed from:" + i);
				i = i - 1;
				System.out.println("i has changed to:" + i);
				continue;
			}
			try {
				Node videoNode = doc.getElementsByTagName("video").item(0);
				NodeList childNodes = videoNode.getChildNodes();
				Element e = (Element) videoNode;
				String mcpId = e.getElementsByTagName("upload_id").item(0).getTextContent();
				String ts_published = e.getElementsByTagName("ts_published").item(0).getTextContent();
				String ts_expired = e.getElementsByTagName("ts_expire").item(0).getTextContent();
				//String publishDate = e.getElementsByTagName("published").item(0).getTextContent();
				HashMap<String, String> childContents = new HashMap<String, String>();
				childContents.put("ts_upload_publish", ts_published);
				childContents.put("ts_expire", ts_expired);
			//	childContents.put("published", publishDate);
				for (int childNodesIndex = 0; childNodesIndex < childNodes.getLength(); childNodesIndex++) {
					Node childNode = childNodes.item(childNodesIndex);
					childContents.put(childNode.getNodeName(), childNode.getTextContent());
				}
				videos.put(mcpId, childContents);
			} catch (Exception ex) {
				System.out.println(videoIds.get(i) + ":" + ex.getMessage());
			}
		}
		return videos;
	}

	public static boolean checkBrightspotSyndication(int key, JSONObject brightspotInfo,
													 entities.System brightspotSystem, VideoMeasurementXref videoMeasurementXref) {

		String restMethod = "GET";
		String client_id = brightSpotSystem.ApiKey;
		String secret_key = brightSpotSystem.SecretKey;
		String videoId = JsonHelper.getJsonValueByPath(brightspotInfo, "data|items|uid");
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("client_id", client_id);
		HashMap<String, String> headers = new HashMap<String, String>();
		for (int index = 0; index < brightSpotSystem.Parameters.size(); index++) {
			if (brightSpotSystem.Parameters.get(index).Type.toLowerCase().equals("header")) {
				headers.put(brightSpotSystem.Parameters.get(index).Name, brightSpotSystem.Parameters.get(index).Value);
			}
		}
		String urlPath = "/feed/content/video/" + videoId + "?client_id=" + client_id;

		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setHttpMethod(restMethod);
		requestInfo.setIsHttps(false);
		requestInfo.setRequestPath("/feed/content/video/" + videoId);
		requestInfo.setHost(brightSpotSystem.Host);
		requestInfo.setParameters(parameters);
		requestInfo.setHeaders(headers);

		requestInfo.setTextToSign(restMethod + client_id + urlPath + secret_key);
		requestInfo.setSignatureType("SHA-1");

		Parameter signitureParameter = new Parameter();
		signitureParameter.Name = "signature";
		signitureParameter.Value = requestInfo.getSignature();
		PullingManager pm = new PullingManager();
		String response = pm.getFeedData(requestInfo, signitureParameter);
	//	System.out.println(response);
		JSONObject jsonObject = new JSONObject(response);
		JSONArray arr = null;
		try {
			arr = jsonObject.getJSONObject("data").getJSONArray("videoLocations");
		} catch (Exception ex) {

		}
		videoMeasurementXref.Note = "";
		boolean hasMp4 = false;
		boolean hasM3u8 = false;
		boolean hasFailedLink = false;
		String strFailedLink = "";
		if (arr != null) {
			for (int i = 0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				String url = obj.getString("url");
				if (url.trim().toLowerCase().endsWith(".mp4")) {
					hasMp4 = true;
				}
				if (url.trim().toLowerCase().endsWith(".m3u8")) {
					hasM3u8 = true;
				}
				HttpURLConnection.setFollowRedirects(false);
				HttpURLConnection con;
				try {
					con = (HttpURLConnection) new URL(url.trim()).openConnection();
					con.setRequestMethod("HEAD");
					if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
						hasFailedLink = true;
						AkamaiFailedLink failedLink = new AkamaiFailedLink();
						failedLink.videoId = key;
						failedLink.AkamaiLink = url;
						strFailedLink = url;
						failedLink.typeId = 2;
						FeedDataHelper.insertFailedAkmaiLinkRun(failedLink);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			videoMeasurementXref.Note += "No video locations specified ";
		}

		if (arr != null && !hasMp4) {
			videoMeasurementXref.Note += "Does not have mp4 link,";
		}
		if (arr != null && !hasM3u8) {
			videoMeasurementXref.Note += "Does not have m3u8 link,";
		}
		if (arr != null && hasFailedLink) {
			videoMeasurementXref.Note += "This link does not return 200 status:" + strFailedLink;
		}
		if (arr != null && hasMp4 && hasM3u8 && !hasFailedLink) {
			return true;
		} else {
			return false;
		}
	}

	static void sendVideoFailureEmail(String mcpId, String frontendUrl, String bsId) {

		String username = "univisionvideofailure@gmail.com";
		String password = "univision123";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("univisionvideofailure-email@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(
					"kabdelqader@aspire-infotech.net,DigitalOperations@univision.net,pmadan@US.UNIVISION.COM,ashriber@US.UNIVISION.COM,VSankar@us.univision.com,ssrinivasan@us.univision.com,JNassar@aspire-infotech.net"));
			message.setSubject("Video not in Add server");
			message.setText("Video with mcp id:" + mcpId + ", does not exist in ad server." + "\r\n Frontend Url:"
					+ frontendUrl + ".\r\n"
					+ "Brightspot link:https://cms.uvn.io/manage/content/edit.jsp?typeId=00000147-f39e-d38e-a5f7-f79f74ce0026&id="
					+ bsId);
			Transport.send(message);

			System.out.println("Done");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
