package com.spock.core;

import java.util.ArrayList;
import java.util.Hashtable;

import entities.PathQos;

import com.spock.core.interfaces.IQosPathManager;

public class QosPathManager implements IQosPathManager {
	/*
	 * these variables will carry the path name and a list/hash of success
	 * rate/count for each asset
	 */
	Hashtable<String, Hashtable<String, Integer>> assetSyndicationSuccess = new Hashtable<String, Hashtable<String, Integer>>();
	Hashtable<String, Hashtable<String, Integer>> assetAdsSuccess = new Hashtable<String, Hashtable<String, Integer>>();
	Hashtable<String, ArrayList<Float>> assetAttributeAccuracy = new Hashtable<String, ArrayList<Float>>();
	Hashtable<String, ArrayList<Float>> assetTransmisionDelay = new Hashtable<String, ArrayList<Float>>();
	/*
	 * these variables will carry Qos value for each path
	 */
	Hashtable<String, PathQos> pathQos = new Hashtable<String, PathQos>();

	/*
	 * counts the assets for path and count the failed due not exisiting
	 */
	public void addAssetSyndicationSuccess(String pathName, boolean success) {
		if (!assetSyndicationSuccess.containsKey(pathName)) {
			assetSyndicationSuccess.put(pathName,
					new Hashtable<String, Integer>());
		}
		

		Hashtable<String, Integer> item = assetSyndicationSuccess.get(pathName);
		if (!success) {
			assetSyndicationSuccess.get(pathName).put("Failed",
					(item.get("Failed") == null ? 0 : item.get("Failed")) + 1);
		}
		assetSyndicationSuccess.get(pathName).put("Count",
				(item.get("Count") == null ? 0 : item.get("Count")) + 1);

	}

	/*
	 * counts the number of all assets and the failed due ads
	 */
	public void addAdsSuccess(String pathName, boolean success) {
		if (!assetAdsSuccess.containsKey(pathName)) {
			assetAdsSuccess.put(pathName, new Hashtable<String, Integer>());
		}
		Hashtable<String, Integer> item = assetAdsSuccess.get(pathName);
		if (!success) {
			assetAdsSuccess.get(pathName).put("Failed",
					(item.get("Failed") == null ? 0 : item.get("Failed")) + 1);
		}
		assetAdsSuccess.get(pathName).put("Count",
				(item.get("Count") == null ? 0 : item.get("Count")) + 1);

	}

	/*
	 * save the attribute accuracy value for each asset in a specific path
	 */
	public void addAttributeAccuracy(String pathName, float accuracy) {
		if (!assetAttributeAccuracy.containsKey(pathName)) {
			assetAttributeAccuracy.put(pathName, new ArrayList<Float>());
		}
		assetAttributeAccuracy.get(pathName).add(accuracy);
	}

	/*
	 * save the transmission delay rate for each asset in a specific path
	 */
	public void addTransmisionDelay(String pathName, float rate) {
		if (!assetTransmisionDelay.containsKey(pathName)) {
			assetTransmisionDelay.put(pathName, new ArrayList<Float>());
		}
		assetTransmisionDelay.get(pathName).add(rate);
	}
}
