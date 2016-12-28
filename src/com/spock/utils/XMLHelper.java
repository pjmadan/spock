package com.spock.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLHelper {

	public static Document XMLfromString(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);

		} catch (ParserConfigurationException e) {
			System.out.println("XML parse error: " + e.getMessage());
			return null;
		} catch (SAXException e) {
			System.out.println("Wrong XML file structure: " + e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println("I/O exeption: " + e.getMessage());
			return null;
		}
		return doc;
	}

	public static String xmlparser(String filepath) {
		try

		{
			BufferedReader br = new BufferedReader(new FileReader(new File(
					filepath)));
			String line;
			StringBuilder sb = new StringBuilder();

			while ((line = br.readLine()) != null) {
				sb.append(line.trim());
			}
			br.close();
			return sb.toString();
		}

		catch (Exception e)

		{
			return "";
		}

	}

	public static String getXmlValueByPath(String xmlData, String path) {
		Document document = null;
		try {
			document = XMLfromString(xmlData);
		} catch (Exception ex) {

		}
		if (document == null) {
			return "";
		}
		String value = "";
		String[] pattern = path.split("\\|");
		Element element = null;

		for (int s = 0; s < pattern.length; s++) {
			if (element == null) {
				element = (Element) document.getElementsByTagName(pattern[s])
						.item(0);
			} else {
				element = (Element) element.getElementsByTagName(pattern[s])
						.item(0);
			}
		}
		value = element.getTextContent();
		return value;

	}
}
