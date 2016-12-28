package com.spock.core;

import java.util.TimerTask;

import com.spock.utils.FeedDataHelper;

public class ScheduledTask extends TimerTask {
	// Add your task here
	public void run() {
		try {
		VideosWatchdog.configs = FeedDataHelper.getConfigs();
			VideosWatchdog.run();
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				FeedDataHelper.deleteFullRunInfo(VideosWatchdog.run);
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
		}
	}
}