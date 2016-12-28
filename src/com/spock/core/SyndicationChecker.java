package com.spock.core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import entities.Attribute;
import entities.VideoAttribute;

import com.spock.utils.DateUtils;
import com.spock.utils.FeedDataHelper;
import com.spock.utils.XMLHelper;

public class SyndicationChecker {

	public boolean attributeHasValueInXml(String response) {
		if (!response.isEmpty()) {
			Document doc = XMLHelper.XMLfromString(response);
			try {
				if (doc.getElementsByTagName("videos").item(0).getTextContent().equals("\n")) {

					return false;
				}
			} catch (Exception ex) {

				return false;
			}

			return true;
		} else {
			return false;
		}
	}

	public JSONObject jsonResponseExistBrighspot(String response) {
		try {
			JSONObject obj = new JSONObject(response);
			if (obj.getJSONObject("data").get("totalItems").toString().equals("0")) {
				System.out.println("I am not in bright spot");
				return null;
			}

			return obj;
		}

		catch (Exception e) {
			return null;
		}
	}

	public JSONObject jsonResponseExistBacklot(String response) {
		try {
			JSONObject obj = new JSONObject(response);
			if (obj.has("message") && obj.get("message").toString().contains("No asset exists")) {
				System.out.println("I am not in backlot");
				return null;
			}

			else {
				return obj;
			}
		}

		catch (Exception e) {
			return null;
		}
	}

	public JSONObject jsonResponseExistWCM(String response) {
		try {
			JSONObject obj = new JSONObject(response);
			try {
				obj = new JSONObject(response);

			}

			catch (Exception ex) {
				return null;
			}
			return obj;
		} catch (Exception e) {
			return null;
		}
	}

	public JSONObject jsonResponseHasValues(String response) {
		try {
			JSONObject obj = new JSONObject(response);
			try {
				obj = new JSONObject(response);
				if (obj.has("message") && obj.get("message").toString().contains("No asset exists")) {
					return null;
				}
				if (obj.has("data") && obj.getJSONObject("data").has("totalItems")
						&& obj.getJSONObject("data").get("totalItems").toString().equals("0")) {
					return null;
				}
			} catch (Exception ex) {
				return null;
			}
			return obj;
		} catch (Exception e) {
			return null;
		}
	}

	public HashMap<String, String> jsonAttributes(JSONObject jsonObject, HashMap<String, String> map) {
		HashMap<String, String> valuesMap = new HashMap<String, String>();
		for (String key : map.keySet()) {
			String value = "";
			JSONObject obj = jsonObject;
			if (!map.get(key).isEmpty()) {
				String[] pattern = map.get(key).split("\\|");

				for (int s = 0; s < pattern.length; s++) {
					try {
						obj = obj.getJSONObject(pattern[s]);
					} catch (Exception e) {
						obj = obj.getJSONArray(pattern[s]).getJSONObject(0);
					}
				}
			}
			value = obj.get(key).toString();
			valuesMap.put(key, value);
		}
		return valuesMap;
	}

	public HashMap<String, String> xmlAttributes(Document document, HashMap<String, String> map) {
		HashMap<String, String> valuesMap = new HashMap<String, String>();
		for (String key : map.keySet()) {
			String value = "";
			String[] pattern = map.get(key).split("\\|");
			Element element = null;

			for (int s = 0; s < pattern.length; s++) {
				if (element == null) {
					element = (Element) document.getElementsByTagName(pattern[s]).item(0);
				} else {
					element = (Element) element.getElementsByTagName(pattern[s]).item(0);
				}
			}

			value = element.getElementsByTagName(key).item(0).getTextContent();
			valuesMap.put(key, value);
		}
		return valuesMap;
	}

	public float CompareAttributes(HashMap<String, String> attributes, HashMap<String, String> attributesToCompare,
                                   HashMap<String, String> mappingTable, entities.System mcpSystem, entities.System system, int videoId) {
		String attibuteValue = "";
		String attibuteToCompareValue = "";
		String mapingValue = "";
		float passedCount = (float) mappingTable.size();
		for (String mapingKey : mappingTable.keySet()) {
			try {
				attibuteValue = attributes.get(mapingKey);
			}

			catch (Exception e) {
				System.out.println("The key \"" + mapingKey + "\" is not found in the mcp response hashmap.");
				passedCount--;
				continue;
			}

			mapingValue = mappingTable.get(mapingKey);
			try {
				attibuteToCompareValue = attributesToCompare.get(mapingValue);
			} catch (Exception e) {
				System.out.println("The key \"" + mapingValue + "\" is not found in the second response hashmap.");
				passedCount--;
				continue;
			}
			int attributeId = getAttributeId(system.Attributes, mapingValue);
			VideoAttribute videoAttribute = new VideoAttribute();
			videoAttribute.AttributeId = attributeId;
			videoAttribute.SystemId = system.Id;
			videoAttribute.ActualValue = attibuteToCompareValue;
			videoAttribute.ExpectedValue = attibuteValue;
			videoAttribute.VideoId = videoId;
			boolean success = true;
			if (isAttributeHasDateValue(mcpSystem.Attributes, mapingKey)) {

				DateFormat systemDateFormat = new SimpleDateFormat(system.DateFormat);
				DateFormat mcpSystemDateFormat = new SimpleDateFormat(mcpSystem.DateFormat);
				try {
					Date systemDate = new Date();
					try {
						systemDate = (Date) systemDateFormat.parse(attibuteToCompareValue);
					} catch (Exception e) {
						systemDate = DateUtils.parseDate(attibuteToCompareValue);
					}
					Date mcpSystemDate = (Date) mcpSystemDateFormat.parse(attibuteValue);
					if (!areDatesEqual((new DateTime(systemDate)), (new DateTime(mcpSystemDate)))) {
						success = false;
						areDatesEqual((new DateTime(systemDate)), (new DateTime(mcpSystemDate)));
						passedCount--;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					success = false;
					passedCount--;
				}
			} else {
				if (mapingKey.toLowerCase().equals("duration") && attibuteValue.contains(":")) {
					String[] time = attibuteValue.split(":");
					if (time.length == 3) {
						attibuteValue = String.valueOf((Integer.parseInt(time[0]) * 60 * 60)
								+ (Integer.parseInt(time[1]) * 60) + Integer.parseInt(time[2]));
					} else if (time.length == 2) {
						attibuteValue = String.valueOf((Integer.parseInt(time[0]) * 60) + (Integer.parseInt(time[1])));
					}
				}
				if (!attibuteValue.trim()
						.equals((attibuteToCompareValue.trim().length() >= attibuteValue.length()
								? attibuteToCompareValue.trim().substring(0, attibuteValue.length())
								: attibuteToCompareValue.trim()))) {
					passedCount--;
					success = false;
					System.out.println("Unmatch in the \"" + mapingKey + "\" the expected is: \"" + attibuteValue
							+ "\" and the actual is: \"" + attibuteToCompareValue);
				}
			}
			videoAttribute.Success = success;
			FeedDataHelper.insertVideoAttribute(videoAttribute);
		}
		return passedCount / ((float) mappingTable.size());
	}

	public boolean areDatesEqual(DateTime firstDate, DateTime secondDate) {
		if (firstDate.getDayOfMonth() != secondDate.getDayOfMonth()) {
			return false;
		}
		if (firstDate.getMonthOfYear() != secondDate.getMonthOfYear()) {
			return false;
		}
		if (firstDate.getYear() != secondDate.getYear()) {
			return false;
		}
		return true;
	}

	public int getAttributeId(ArrayList<Attribute> attributes, String attributeName) {
		int attributeId = -1;
		for (Attribute attribute : attributes) {
			if (attribute.Name.toLowerCase().equals(attributeName.toLowerCase())) {
				attributeId = attribute.Id;
			}
		}
		return attributeId;
	}

	public boolean isAttributeHasDateValue(ArrayList<Attribute> attributes, String attributeName) {
		for (Attribute attr : attributes) {
			if (attr.IsDateValue == true && attr.Name.equals(attributeName)) {
				return true;
			}
		}
		return false;
	}

	public float assetTansmissionDelayAccuracy(Date mcpDate, Date toCompareDate) {
		int optimumDelay = VideosWatchdog.configs.TransmissionOptimumDelay;
		int LowDelay = VideosWatchdog.configs.TransmissionLowDelay;
		int MediumDelay = VideosWatchdog.configs.TransmissionMeduimDelay;
		int HighestDelay = VideosWatchdog.configs.TransmissionHighDelay;
		long timeBetweenInSeconds = (mcpDate.getTime() - toCompareDate.getTime()) / 1000;
		long[] delayList = { optimumDelay, LowDelay, MediumDelay, HighestDelay };
		float accuracy = 1;
		for (long delay : delayList) {
			if (timeBetweenInSeconds < delay) {
				break;
			}
			accuracy = accuracy - 0.25f;
		}
		return accuracy;
	}
}
