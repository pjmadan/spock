package com.spock.core;

import java.util.ArrayList;
import java.util.Hashtable;

import entities.*;

import com.spock.core.interfaces.IQosCalculatorEngine;

public class QosCalculatorEngine implements IQosCalculatorEngine {

	// These variables should be brought from DB or XML
	int numberOfConsumersForPath = 0;
	int numberOfAssetsForPath = 0;
	int totalNumberOfConsumers = 0;
	int totalNumberOfAssets = 0;

	QosPathManager pathManager = null;
	QosPathSystemManager systemManager = null;

	public QosCalculatorEngine(QosPathManager pathManager,
			QosPathSystemManager systemManager) {
		this.pathManager = pathManager;
		this.systemManager = systemManager;
	}

	public TotalWeights getTotalPathsWeight(ArrayList<Path> paths) {
		TotalWeights totalWeight = new TotalWeights();

		for (String key : pathManager.assetSyndicationSuccess.keySet()) {
			for (Path path : paths) {
				if (path.Name.toLowerCase().equals(key.toLowerCase())) {
					totalWeight.SydnicationOfSuccess += path.SyndicationSuccessWeight;
					totalWeight.AdServer += path.AdServerWeight;
					totalWeight.AttributeAccuracy += path.AttributesAccuracyWeight;
					totalWeight.TransmissionDelay += path.TransmissionDelayWeight;
				}
			}
		}
		return totalWeight;
	}

	public float getTotalAPathsWeight(ArrayList<Path> paths) {
		float totalWeight = 0;

		for (String key : pathManager.assetSyndicationSuccess.keySet()) {
			for (Path path : paths) {
				if (path.Name.toLowerCase().equals(key.toLowerCase())) {
					totalWeight += path.AdServerWeight;
				}
			}
		}
		return 0f;
	}

	public void calculatePathQos(Path path, TotalWeights totalPathsWieght) {
		float SynResult = 0;
		float AccResult = 0;
		float AdResult = 0;
		float TransResult = 0;
		float PathQosValue = 0;
		if (pathManager.assetSyndicationSuccess.containsKey(path.Name
				.toLowerCase())
				|| pathManager.assetAdsSuccess.containsKey(path.Name
						.toLowerCase())
				|| pathManager.assetAttributeAccuracy.containsKey(path.Name
						.toLowerCase())
				|| pathManager.assetTransmisionDelay.containsKey(path.Name
						.toLowerCase())) {

			SynResult = CalculateCount(
					pathManager.assetSyndicationSuccess,
					path.Name.toLowerCase(),
					1 );
			AdResult = CalculateCount(pathManager.assetAdsSuccess,
					path.Name.toLowerCase(),
					1);
			AccResult = CalculateAverage(
					pathManager.assetAttributeAccuracy,
					path.Name.toLowerCase(),
				1 );
			TransResult = CalculateAverage(
					pathManager.assetTransmisionDelay,
					path.Name.toLowerCase(),
					1);
			PathQosValue = (SynResult * path.SyndicationSuccessWeight) + (AccResult * path.AttributesAccuracyWeight) + (AdResult * path.AdServerWeight )+ (TransResult * path.TransmissionDelayWeight);
			PathQos pathQos = new PathQos();
			pathQos.QOSValue = PathQosValue;
			pathQos.SyndicationSuccessQOS = SynResult;
			pathQos.PathID = path.Id;
			pathQos.AdServerQOS = AdResult;
			pathQos.AttributesAccuracyQOS = AccResult;
			pathQos.TransmissionDelayQOS = TransResult;
			pathQos.PathName = path.Name;
			pathManager.pathQos.put(path.Name.toLowerCase(), pathQos);
		}

	}

	public void calculatePathsQos(ArrayList<Path> paths) {
		TotalWeights totalPathsWieght = getTotalPathsWeight(paths);
		for (Path path : paths) {
			calculatePathQos(path, totalPathsWieght);
		}
	}

	public static float CalculateCount(
			Hashtable<String, Hashtable<String, Integer>> Hashtable,
			String pathName, float weight) {
		float Failed = 0;
		float Count = 0;
		float Result = 0;
		String failedKey = "Failed";
		String countKey = "Count";
		Hashtable<String, Integer> PathValue = Hashtable.get(pathName);
		if (PathValue == null || PathValue.size() ==0) {
			return 1 * weight;
		}
		try {
			Failed = PathValue.get(failedKey);
		} catch (Exception e) {
			Failed = 0;
		}

		try {
			Count = PathValue.get(countKey);
		} catch (Exception e) {
			Count = 0;
		}
		Result = ((Count - Failed) / Count) * weight;
		return Result;
	}

	public static float CalculateAverage(
			Hashtable<String, ArrayList<Float>> Hashtable, String pathName,
			float weight) {
		float Result = 0;

		ArrayList<Float> PathValue = Hashtable.get(pathName);
		if (PathValue == null || PathValue.size() ==0) {
			return 1 * weight;
		}
		for (int j = 0; j < PathValue.size(); j++) {
			Result += PathValue.get(j);
		}
		if (Result != 0 && PathValue.size() != 0) {

			Result = (Result / ((float) PathValue.size())) * weight;
		} else {
			return 1 * weight;
		}
		return Result;
	}

	public float CalculateQOS(ArrayList<Path> paths) {
		calculatePathsQos(paths);
		float QOS = 0;
		float n = 0;
		float N = getTotalConsumers(paths);
		float Q = 0;
		float k = 0;
		float K = getTotalAssetsCount();
		for (String key : pathManager.pathQos.keySet()) {
			PathQos pathQos = pathManager.pathQos.get(key);
			Path path = getPathById(paths, pathQos.PathID);
			n = path.NumberOfConsumers;
			Q = pathQos.QOSValue;
			k = VideosWatchdog.pathAssetsCount.get(key);
			QOS += ((n / (2 * N)) + (k / (2 * K))) * Q;
		}
		return QOS;
	}
	public int getTotalConsumers(ArrayList<Path> paths)
	{
		int total = 0;
		for (Path path : paths){
			if ( pathManager.pathQos.containsKey(path.Name.toLowerCase())){
			total += path.NumberOfConsumers;
			}
		}
		return total;
	}
	public int getTotalAssetsCount()
	{
		int totalAssets = 0;
		for (String key : VideosWatchdog.pathAssetsCount.keySet()){
			totalAssets +=  VideosWatchdog.pathAssetsCount.get(key);
		}
		return totalAssets;
	}
	public void calculatePathSystemQos(int runId) {
		float SynResult = 0;
		float AccResult = 0;
		float AdResult = 0;
		float TransResult = 0;
		//float wieght = 1f / (float) systemManager.allSystems.size();
		for (String system : systemManager.allSystems) {

			SynResult = CalculateCount(systemManager.assetSyndicationSuccess,
					system, 1f);
			AdResult = CalculateCount(systemManager.assetAdsSuccess, system,
					1f);
			AccResult = CalculateAverage(systemManager.assetAttributeAccuracy,
					system, 1f);
			TransResult = CalculateAverage(systemManager.assetTransmisionDelay,
					system, 1f);
			float systemQosValue = SynResult + AccResult + AdResult
					+ TransResult;
			SystemQos systemQos = new SystemQos();
			systemQos.QOSValue = systemQosValue;
			systemQos.SystemId = Integer.parseInt(system);
			systemQos.RunId = runId;
			systemQos.SyndicationSuccess = SynResult;
			systemQos.AttributesAccuracy = AccResult;
			systemQos.TransmissionDelay = TransResult;
			systemQos.AdServer = AdResult;
			systemManager.systemQos.add(systemQos);
		}

	}

	public float calculateMovingWeight(Run currentRun, Run previousRun) {
		int previousRunTotalAssets = (previousRun == null ? 0
				: previousRun.totalAssets);
		int totalAssets = currentRun.totalAssets + previousRunTotalAssets;
		float lastMovingWeight = -1;
		if (previousRun != null && previousRun.movingWeight > 0) {
			lastMovingWeight = previousRun.movingWeight;
		} else if (previousRun != null && previousRun.QosValue > 0) {
			lastMovingWeight = previousRun.QosValue;
		}
		float movingWeight = 0;
		if (lastMovingWeight != -1) {
			movingWeight = (((float) currentRun.totalAssets / (float) totalAssets) * (float) currentRun.QosValue)
					+ (((float) previousRunTotalAssets / (float) totalAssets) * (float) lastMovingWeight);
		}
		if(movingWeight == 0){
			return	currentRun.QosValue;
		}
		return movingWeight;
	}

	public static Path getPathById(ArrayList<Path> paths, int pathId) {
		for (Path path : paths) {
			if (path.Id == pathId) {
				return path;
			}
		}
		return new Path();

	}

	/*
	 * calculates the weight for a specific path
	 */
	public float calculateWeight(String path) {
		return ((numberOfConsumersForPath / totalNumberOfConsumers) + (numberOfAssetsForPath / totalNumberOfAssets));
	}

	/*
	 * calculate the average of asset/s attribute accuracy and transmision delay
	 * average
	 */
	public static float averageToListOfIntegers(ArrayList<Float> list) {
		if (list.size() == 0) {
			return -1;
		}
		float avg = 0;
		for (float item : list) {
			avg += item;
		}
		avg = avg / list.size();
		return avg;
	}

}
