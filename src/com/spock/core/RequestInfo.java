package com.spock.core;

import java.util.HashMap;

import com.spock.utils.SignatureGenerator;

public class RequestInfo {
	private String apiKey;

	public void setApiKey(String value) {
		apiKey = value;
	}

	public String getApiKey() {
		return apiKey;
	}

	private String httpMethod;

	public void setHttpMethod(String value) {
		httpMethod = value;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	private String secretKey;

	public void setSecretKey(String value) {
		secretKey = value;
	}

	public String getSecretKey() {
		return secretKey;
	}

	private String signature;

	public void setSignature(String value) {
		signature = value;
	}

	public String getSignature() {
		SignatureGenerator.getSignature(this.signatureType, this);
		return signature;
	}

	private String signatureType;

	public void setSignatureType(String value) {
		signatureType = value;
	}

	public String getSignatureType() {
		return signatureType;
	}

	private String data;

	public void setData(String value) {
		data = value;
	}

	public String getData() {
		return data;
	}

	private boolean isHttps = false;

	public void setIsHttps(boolean value) {
		isHttps = value;
	}

	public boolean getIsHttps() {
		return isHttps;
	}

	private String requestPath;

	public void setRequestPath(String value) {
		requestPath = value;
	}

	public String getRequestPath() {
		return requestPath;
	}

	private String host;

	public void setHost(String value) {
		host = value;
	}

	public String getHost() {
		return host;
	}

	private HashMap<String, String> parameters = new HashMap<String, String>();

	public void setParameters(HashMap<String, String> value) {
		parameters = value;
	}

	public HashMap<String, String> getParameters() {
		return parameters;
	}

	private HashMap<String, String> headers = new HashMap<String, String>();

	public void setHeaders(HashMap<String, String> value) {
		headers = value;
	}

	public HashMap<String, String> getHeaders() {
		return headers;
	}

	private String textToSign;

	public void setTextToSign(String value) {
		textToSign = value;
	}

	public String getTextToSign() {
		return textToSign;
	}
}
