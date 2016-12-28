package com.spock.utils;

import org.json.JSONObject;

public class JsonHelper {
	public static String getJsonValueByPath(JSONObject jsonObject, String path) {
		try {
			String[] pattern = path.split("\\|");
			JSONObject obj = jsonObject;
			int index = 0;
			for (; index < pattern.length - 1; index++) {
				try {
					obj = obj.getJSONObject(pattern[index]);
				} catch (Exception e) {
					obj = obj.getJSONArray(pattern[index]).getJSONObject(0);
				}
			}
			return obj.getString(pattern[index]);
		} catch (Exception ex) {
			return null;
		}
	}
}
