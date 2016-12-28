package com.spock.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;

public class EnvirommentManager {
	private Properties defaultProps = new Properties();
	private Properties appProps = null;

	private Hashtable<String, ArrayList<PropertyChangeListener>> listeners = null;

	private static Object lock = new Object();
	private static EnvirommentManager instance = null;

	private EnvirommentManager() {
	}

	public static EnvirommentManager getInstance() {
		try {
			if (instance == null) {
				synchronized (lock) {
					if (instance == null) {
						instance = new EnvirommentManager();
						instance.loadProperties();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (instance);

	}

	private void loadProperties() throws IOException {

		List<String> files = new ArrayList<String>();
		String path = System.getProperty("user.dir") + File.separator
				+ "resources";
		File directory = new File(path);

		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()
					&& FilenameUtils.getExtension(file.getPath()).equals(
							"properties")) {
				files.add(file.getPath());

				// create and load default properties
				FileInputStream in = new FileInputStream(file.getAbsolutePath());
				defaultProps.load(in);
				in.close();

				// create application properties with default
				appProps = new Properties(defaultProps);

				try {
					// user/application properties
					in = new FileInputStream(file.getAbsolutePath());
					appProps.load(in);
					in.close();
				} catch (Throwable th) {
				}
			}
		}
	}

	public String getProperty(String key) {
		String val = null;
		if (key != null) {
			if (appProps != null)
				val = (String) appProps.getProperty(key);
			if (val == null) {
				val = defaultProps.getProperty(key);
			}
		}
		return (val);

	}

	/**
	 * Sets Application/User String properties; default property values cannot
	 * be set.
	 */
	public void setProperty(String key, String val) {

		ArrayList<?> list = null;
		Object oldValue = null;

		oldValue = getProperty(key);

		appProps.setProperty(key, val);
		if (listeners.containsKey(key)) {
			list = (ArrayList<?>) listeners.get(key);
			int len = list.size();
			if (len > 0) {
				PropertyChangeEvent evt = new PropertyChangeEvent(this, key,
						oldValue, val);
				for (int i = 0; i < len; i++) {
					if (list.get(i) instanceof PropertyChangeListener)
						((PropertyChangeListener) list.get(i))
								.propertyChange(evt);
				}
			}
		}

	}

	public boolean addListener(String key, PropertyChangeListener listener) {
		boolean added = false;
		ArrayList<PropertyChangeListener> list = null;
		if (listeners == null)
			listeners = new Hashtable<String, ArrayList<PropertyChangeListener>>();

		if (!listeners.contains(key)) {
			list = new ArrayList<PropertyChangeListener>();
			added = list.add(listener);
			listeners.put(key, list);
		} else {
			list = (ArrayList<PropertyChangeListener>) listeners.get(key);
			added = list.add(listener);
		}
		return (added);
	}

	public void removeListener(PropertyChangeListener listener) {
		if (listeners != null && listeners.size() > 0)
			listeners.remove(listener);
	}
}
