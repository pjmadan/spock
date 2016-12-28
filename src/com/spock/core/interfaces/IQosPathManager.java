package com.spock.core.interfaces;

import java.util.ArrayList;
import java.util.Hashtable;

public interface IQosPathManager {
	void addAssetSyndicationSuccess(String pathName, boolean success);
	void addAdsSuccess(String pathName, boolean success);
	void addAttributeAccuracy(String pathName, 
			float accuracy);
	void addTransmisionDelay(String pathName, 
			float rate);
	/*
	 * these variables will carry the path name and a list/hash of success rate/count for
	 * each asset
	 */
	Hashtable<String, Hashtable<String, Integer>> assetSyndicationSuccess = new Hashtable<String, Hashtable<String,Integer>>();
	Hashtable<String, Hashtable<String, Integer>> assetAdsSuccess = new Hashtable<String, Hashtable<String,Integer>>();
	Hashtable<String, ArrayList<Float>> assetAttributeAccuracy= new Hashtable<String, ArrayList<Float>>();
	Hashtable<String, ArrayList<Float>> assetTransmisionDelay = new Hashtable<String, ArrayList<Float>>();
	/*
	 * these variables will carry Qos value for each path
	 */
}
