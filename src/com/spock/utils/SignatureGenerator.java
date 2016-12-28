package com.spock.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.spock.core.RequestInfo;

public class SignatureGenerator {

	public static void getSignature(String type, RequestInfo request) {

		if (type.equals("SHA-1")) {
			request.setSignature(generateSignatureSHA1(request));
		} else if (type.equals("SHA-256")) {

			request.setSignature(generateSignatureSHA256(request));
		} else {
			request.setSignature("");
		}
	}

	private static String generateSignatureSHA256(RequestInfo request)

	{
		try {
			String stringToSign = request.getTextToSign();
			stringToSign += concatenateParams(request.getParameters(), "");
			stringToSign += request.getData() != null ? request.getData() : "";
			MessageDigest digestProvider;
			digestProvider = MessageDigest.getInstance("SHA-256");
			digestProvider.reset();
			byte[] digest = digestProvider.digest(stringToSign.getBytes());
			String encodedBytes = new Base64().encodeToString(digest);
			return encodedBytes.substring(0, 43);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String sha1(String input) throws NoSuchAlgorithmException

	{
		MessageDigest mDigest = MessageDigest.getInstance("SHA1");
		byte[] result = mDigest.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16)
					.substring(1));
		}

		return sb.toString();
	}

	private static String generateSignatureSHA1(RequestInfo request)

	{
		
		// method + clientid + url + secretkey
		String signature = request.getTextToSign();
		try {
			signature = sha1(signature);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return signature;
	}

	public static String concatenateParams(HashMap<String, String> parameters,
			String separator)

	{
		Vector<String> keys = new Vector<String>(parameters.keySet());
		Collections.sort(keys);
		String string = "";
		for (Enumeration<String> e = keys.elements(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String value = (String) parameters.get(key);
			if (!string.isEmpty())
				string += separator;
			string += key + "=" + value;
		}
		return string;
	}

	public static String signrequest(String payload, long timeStamp) {
		String s = Objects.toString(timeStamp, null);
		payload = payload + s;
		return payload;
	}

	public static String generatebase64(String xml, String privateKey,
			long timeStamp, String startDate, String endDate, boolean isPath) {
		try {
			String message = "";
			if (isPath) {
				message = XMLHelper.xmlparser(xml);
			} else {
				message = xml;
			}
			message = message.replace("$$startDate$$", startDate);
			message = message.replace("$$endDate$$", endDate);
			message = signrequest(message, timeStamp);
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(privateKey.getBytes(),
					"HmacSHA256");
			sha256_HMAC.init(secret_key);

			String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(message
					.getBytes()));
			return hash;
		}

		catch (Exception e) {
			System.out.println("Error");
			return "Error generating signature";
		}

	}
}
