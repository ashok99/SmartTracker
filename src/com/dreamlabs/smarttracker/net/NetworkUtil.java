package com.dreamlabs.smarttracker.net;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * 
 * @author ashok
 *
 */
public class NetworkUtil {
	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String invokeServiceCall(String url, boolean isResponseRequired) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		HttpGet httpGet = new HttpGet(url);
		String text = "";
		
		try {
			HttpResponse response = httpClient.execute(httpGet, localContext);
			HttpEntity entity = response.getEntity();
			if(isResponseRequired) {
				text = getASCIIContentFromEntity(entity);
			}

		} catch (Exception e) {
			StackTraceElement[] stackTrace = e.getStackTrace();
			StringBuffer buffer = new StringBuffer();
			for (StackTraceElement stackTraceElement : stackTrace) {
				buffer.append(stackTraceElement.toString());
			}
			buffer.append("ERROR:");
			if(buffer!=null && buffer.length() >0)
				text = buffer.toString();
		}
		return text;
	}

	protected static String getASCIIContentFromEntity(HttpEntity entity)
			throws IllegalStateException, IOException {
		InputStream in = entity.getContent();
		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n > 0) {
			byte[] b = new byte[4096];
			n = in.read(b);
			if (n > 0)
				out.append(new String(b, 0, n));
		}
		return out.toString();
	}
}
