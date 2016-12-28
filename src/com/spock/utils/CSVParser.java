package com.spock.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class CSVParser {

	public static int websitecount = 0;

	private static BufferedReader parsecsvfile(String filepath) {

		/*
		 * This subroutine will parse an csv file and return an array that
		 * contains objects which have website URL's, elements that need to be
		 * verified and the assertions configured for each
		 */

		String csvFile = filepath;
		BufferedReader br = null;
		try {

			br = new BufferedReader(new FileReader(csvFile));

		}

		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return br;
	}

	public static String[] parsecsvfileToSet(String filepath) {
		String[] lines = {};
		String csvFile = filepath;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {
			br = new BufferedReader(new FileReader(
					System.getProperty("user.dir") + File.separator + csvFile));

			while ((line = br.readLine()) != null) {
				lines = line.split(cvsSplitBy);
			}
		}

		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		catch (IOException e) {
			e.printStackTrace();
		}

		finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return lines;
	}

	public static HashMap<String, String> parseCsvToProgramAndTagHash(
			String fileName) {
		BufferedReader br = null;
		String line = "";
		HashMap<String, String> data = new HashMap<String, String>();

		try {
			br = parsecsvfile(System.getProperty("user.dir") + File.separator
					+ "resources" + File.separator + fileName);
			br.readLine();
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] lines = line.split(",");
				if (!line.trim().isEmpty()) {
					data.put(lines[0], lines[1]);
				}
			}

		}

		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		catch (IOException e) {
			e.printStackTrace();
		}

		finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return data;
	}
}
