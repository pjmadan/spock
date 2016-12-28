package com.spock.core;

import entities.Parameter;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class PullingManager {
	 public static <T extends Appendable> T escapeNonLatin(CharSequence sequence,
		      T out) throws java.io.IOException {
		    for (int i = 0; i < sequence.length(); i++) {
		      char ch = sequence.charAt(i);
		      if (Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.BASIC_LATIN) {
		        out.append(ch);
		      } else {
		        int codepoint = Character.codePointAt(sequence, i);
		        // handle supplementary range chars
		        i += Character.charCount(codepoint) - 1;
		        // emit entity
		        out.append("&#x");
		        out.append(Integer.toHexString(codepoint));
		        out.append(";");
		      }
		    }
		    return out;
		  }
	public String getFeedData(RequestInfo requestInfo,
			Parameter siginitureParameter) {
		String result = "";
		for (int i = 0; i < 3; i++) {
			try {
			
				java.net.URI uri;
				HttpClient client = HttpClientBuilder.create().build();
				HttpGet httpget = null;
				URIBuilder builder = new URIBuilder();
				builder.setScheme((requestInfo.getIsHttps() == true ? "https"
						: "http"));
				builder.setHost(requestInfo.getHost()).setPath(
						requestInfo.getRequestPath());
				Vector<String> keys = new Vector<String>(requestInfo
						.getParameters().keySet());
				Collections.sort(keys);
				for (Enumeration<String> e = keys.elements(); e
						.hasMoreElements();) {
					// for (String key : requestInfo.getParameters().keySet()) {
					String key = (String) e.nextElement();
					builder.setParameter(key,
							requestInfo.getParameters().get(key));
				}

				if (siginitureParameter != null) {
					builder.setParameter(siginitureParameter.Name,
							siginitureParameter.Value);
				}

				try {
					uri = builder.build();
					httpget = new HttpGet(uri);
					for (String key : requestInfo.getHeaders().keySet()) {
						httpget.setHeader(key, requestInfo.getHeaders()
								.get(key));
					}
				}

				catch (URISyntaxException e1) {
					e1.printStackTrace();
				}

				HttpResponse response;

				try {
					response = client.execute(httpget);
					//HttpEntity entity = response.getEntity();
					//result = IOUtils.toString(	entity.getContent(),"UTF-8"); 
					//StringBuilder sb = escapeNonLatin(result, new StringBuilder()); 
					//System.out.println(sb.toString());
					result = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				//	result = CharsUtil.replaceLatinChars(result);

				}

				catch (ClientProtocolException e) {
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
					continue;
				}

				catch (IOException e) {
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
					continue;
				}
				break;
			} catch (Exception e) {
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
				continue;
			}

		}
		return result;
	}
	

	public String postFeedData(RequestInfo requestInfo,
			Parameter siginitureParameter) {
		String result = "";
		for (int i = 0; i < 3; i++) {
			try {
				java.net.URI uri;
				RequestConfig requestConfig = RequestConfig.custom()
						.setConnectTimeout(30000).setSocketTimeout(30000)
						.build();
				HttpClient client = HttpClientBuilder.create()
						.setDefaultRequestConfig(requestConfig).build();

				URIBuilder builder = new URIBuilder();
				builder.setScheme((requestInfo.getIsHttps() == true ? "https"
						: "http"));
				builder.setHost(requestInfo.getHost()).setPath(
						requestInfo.getRequestPath());
				if (siginitureParameter != null) {
					builder.setParameter(siginitureParameter.Name,
							siginitureParameter.Value);
				}
				for (String key : requestInfo.getParameters().keySet()) {

					builder.setParameter(key,
							requestInfo.getParameters().get(key));
				}
				if (requestInfo.getSignature() != null
						&& !requestInfo.getSignature().isEmpty()
						&& siginitureParameter == null) {
					builder.setParameter("signature",
							requestInfo.getSignature());
				}

				HttpPost post = null;
				uri = builder.build();
				post = new HttpPost(uri);
				post.setHeader("Content-type", "application/json");
				post.setHeader("Accept", "application/json");
				HttpEntity entity;

				entity = new ByteArrayEntity(requestInfo.getData().getBytes(
						"UTF-8"));
				post.setEntity(entity);

				HttpResponse response;

				response = client.execute(post);
				result = EntityUtils.toString(response.getEntity());
				//result = CharsUtil.replaceLatinChars(result);
				break;
			//	return result;
			}
				catch (SocketTimeoutException e) {
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
					continue;

			}
			 catch (Exception ex) {
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				ex.printStackTrace();
				continue;
			}
		}
		return result;
	}

}
